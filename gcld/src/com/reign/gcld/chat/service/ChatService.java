package com.reign.gcld.chat.service;

import org.springframework.stereotype.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.chat.dao.*;
import com.reign.gcld.player.dao.*;
import com.reign.gcld.sdata.cache.*;
import com.reign.gcld.chat.repeatedLimit.*;
import java.util.concurrent.*;
import com.reign.gcld.player.dto.*;
import com.reign.framework.json.*;
import com.reign.gcld.player.domain.*;
import com.reign.gcld.common.util.*;
import com.reign.gcld.common.*;
import com.reign.gcld.chat.common.*;
import com.reign.util.*;
import com.reign.gcld.common.util.characterFilter.*;
import com.reign.gcld.chat.domain.*;
import com.reign.framework.netty.util.*;
import com.reign.framework.netty.servlet.*;
import java.util.*;
import org.springframework.transaction.annotation.*;
import com.reign.gcld.kfwd.service.*;
import com.reign.gcld.sdata.domain.*;

@Component("chatService")
public class ChatService implements IChatService
{
    @Autowired
    private IPlayerDao playerDao;
    @Autowired
    private ISilenceDao silenceDao;
    @Autowired
    private IPlayerBlackDao playerBlackDao;
    @Autowired
    private IPlayerOfficeRelativeDao playerOfficeRelativeDao;
    @Autowired
    private HallsCache hallsCache;
    public static final ConcurrentMap<Integer, ChatContentNodeList> chatHistoryMap;
    private final ThreadLocal<Boolean> inTransactional;
    private final ThreadLocal<List<Runnable>> tasks;
    
    static {
        chatHistoryMap = new ConcurrentHashMap<Integer, ChatContentNodeList>();
    }
    
    public ChatService() {
        this.inTransactional = new ThreadLocal<Boolean>();
        this.tasks = new ThreadLocal<List<Runnable>>();
    }
    
    private boolean isInTransactional() {
        return Boolean.TRUE.equals(this.inTransactional.get());
    }
    
    private void addTask(final Runnable runnable) {
        this.tasks.get().add(runnable);
    }
    
    @Override
    public void startTransactional() {
        this.inTransactional.set(Boolean.TRUE);
        this.tasks.set(new ArrayList<Runnable>());
    }
    
    @Override
    public String[] initBlackList(final int playerId) {
        final List<PlayerBlack> list = this.playerBlackDao.getPlayerBlackList(playerId);
        final HashSet<Integer> set = new HashSet<Integer>();
        final String[] name = new String[list.size()];
        int i = 0;
        for (final PlayerBlack pb : list) {
            set.add(pb.getPlayerId());
            name[i++] = pb.getPlayerName();
        }
        return name;
    }
    
    @Override
    public byte[] getBlackList(final PlayerDto playerDto) {
        final int playerId = playerDto.playerId;
        final List<PlayerBlack> list = this.playerBlackDao.getPlayerBlackList(playerId);
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.startArray("blacks");
        for (final PlayerBlack pb : list) {
            doc.startObject();
            doc.createElement("vId", pb.getVId());
            doc.createElement("blackId", pb.getBlackId());
            doc.createElement("blackName", pb.getPlayerName());
            doc.endObject();
        }
        doc.endArray();
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public byte[] addBlackName(final PlayerDto playerDto, final String blackName) {
        final int playerId = playerDto.playerId;
        final Player blackPlayer = this.playerDao.getPlayerByName(blackName);
        if (blackPlayer == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_USER_10002);
        }
        final int blackId = blackPlayer.getPlayerId();
        if (blackId == playerId) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        PlayerBlack playerBlack = this.playerBlackDao.getPlayerBlackByBid(playerId, blackId);
        if (playerBlack != null) {
            return JsonBuilder.getJson(State.SUCCESS, "");
        }
        playerBlack = new PlayerBlack();
        playerBlack.setBlackId(blackId);
        playerBlack.setPlayerId(playerId);
        playerBlack.setPlayerName(blackPlayer.getPlayerName());
        this.playerBlackDao.create(playerBlack);
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Override
    public byte[] removeBlackName(final PlayerDto playerDto, final int vId) {
        final PlayerBlack playerBlack = this.playerBlackDao.read(vId);
        if (playerBlack != null) {
            this.playerBlackDao.deleteById(vId);
        }
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Override
    public void commitTransactional() {
        try {
            final List<Runnable> taskList = this.tasks.get();
            if (taskList == null) {
                return;
            }
            for (final Runnable task : taskList) {
                task.run();
            }
        }
        finally {
            try {
                this.tasks.remove();
            }
            finally {
                this.inTransactional.set(Boolean.FALSE);
            }
            this.inTransactional.set(Boolean.FALSE);
        }
        try {
            this.tasks.remove();
        }
        finally {
            this.inTransactional.set(Boolean.FALSE);
        }
        this.inTransactional.set(Boolean.FALSE);
    }
    
    @Override
    public void endTransactional() {
        try {
            this.inTransactional.set(Boolean.FALSE);
        }
        finally {
            this.tasks.remove();
        }
        this.tasks.remove();
    }
    
    @Override
    public byte[] send(final String type, final PlayerDto playerDto, final String to, String msg) {
        if (playerDto == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_PLAYER_10005);
        }
        if (msg.length() > Configuration.getIntProperty("gcld.chat.len")) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_CHAT_CHAT_TOO_LONG);
        }
        final Tuple<Boolean, String> htmlContent = WebUtil.getHTMLContent(msg);
        msg = htmlContent.right;
        final ICharacterFilter characterFilter = CharacterFilterFactory.getInstance().getFilter("default");
        if (characterFilter != null) {
            msg = characterFilter.filter(msg);
        }
        if ("1".equals(Configuration.getProperty("gcld.chat.repeatCheck"))) {
            synchronized (playerDto) {
                final ChatContentNodeList chatHistory = this.getChatHistory(playerDto.playerId);
                final int checkResult = chatHistory.checkChatContent(msg);
                if (checkResult == 3) {
                    // monitorexit(playerDto)
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.T_CHAT_CONTENT_REPEATED);
                }
            }
        }
        final Silence silence = this.silenceDao.getByPlayerIdAndYx(playerDto.playerId, playerDto.yx);
        if (silence != null && silence.getNextSayTime() != null) {
            if (silence.getNextSayTime().getTime() > System.currentTimeMillis()) {
                if (silence.getType() == 1) {
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.T_CHAT_SILENCE);
                }
                final JsonDocument doc = new JsonDocument();
                doc.startObject();
                doc.createElement("from", playerDto.playerName);
                String rcvPlayerName = "";
                final ChatType ct = Enum.valueOf(ChatType.class, type.toUpperCase());
                switch (ct) {
                    case ONE2ONE: {
                        final Player player = this.playerDao.getPlayerByName(to);
                        if (player == null || player.getPlayerId() == 0) {
                            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_CHAT_NO_SUCH_PLAYER);
                        }
                        rcvPlayerName = player.getPlayerName();
                        break;
                    }
                }
                doc.createElement("to", rcvPlayerName);
                doc.createElement("type", type);
                doc.createElement("msg", msg);
                doc.endObject();
                Players.push(playerDto.playerId, PushCommand.PUSH_CHAT_SEND, doc.toByte());
                return JsonBuilder.getJson(State.SUCCESS, "");
            }
            else {
                this.silenceDao.deleteById(silence.getSilenceId());
            }
        }
        final ChatType ct2 = Enum.valueOf(ChatType.class, type.toUpperCase());
        switch (ct2) {
            case ONE2ONE: {
                if (to.trim().equals(LocalMessages.T_COMM_10010)) {
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.T_CHAT_PLAYER_NO_USE);
                }
                final Player player2 = this.playerDao.getPlayerByName(to);
                if (player2 == null || player2.getPlayerId() == 0) {
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.T_CHAT_NO_SUCH_PLAYER);
                }
                if (!player2.getForceId().equals(playerDto.forceId)) {
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.CAN_NOT_COMM_WITH_OTHER_FORCE);
                }
                if (player2.getPlayerId() == playerDto.playerId) {
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.CAN_NOT_COMM_WITH_YOURSELF);
                }
                return this.one2one(playerDto.playerId, playerDto.playerName, player2.getPlayerId(), ct2, msg, null, false);
            }
            case GLOBAL: {
                return JsonBuilder.getJson(State.SUCCESS, "");
            }
            case COUNTRY: {
                return this.one2Country(playerDto.playerName, playerDto.forceId, ct2, msg, null, playerDto.gm, false);
            }
            case AREA: {
                return this.one2Area(playerDto.playerName, ct2, msg, playerDto.gm);
            }
            case LEGION: {
                return JsonBuilder.getJson(State.SUCCESS, "");
            }
            default: {
                throw new RuntimeException("system has not the chat type :" + ct2.toString());
            }
        }
    }
    
    @Override
    public void sendBigNotice(final String type, final PlayerDto playerDto, final String msg, final Object obj) {
        final ChatType ct = Enum.valueOf(ChatType.class, type.toUpperCase());
        if (!this.isInTransactional()) {
            this.sendBigNotice(ct, type, playerDto, msg, obj);
        }
        else {
            this.addTask(new Runnable() {
                @Override
                public void run() {
                    ChatService.this.sendBigNotice(ct, type, playerDto, msg, obj);
                }
            });
        }
    }
    
    private void sendBigNotice(final ChatType ct, final String type, final PlayerDto playerDto, final String msg, final Object obj) {
        switch (ct) {
            case ONE2ONE: {
                final JsonDocument doc = new JsonDocument();
                doc.startObject();
                doc.createElement("type", type);
                doc.createElement("content", msg);
                doc.endObject();
                Players.push(playerDto.playerId, PushCommand.PUSH_NOTICE, doc.toByte());
            }
            case GLOBAL: {
                this.sendBigInfo(type, type, msg);
                break;
            }
            case COUNTRY: {
                if (obj == null) {
                    this.sendBigInfo(String.valueOf(type) + playerDto.forceId, type, msg);
                    break;
                }
                this.sendBigInfo(String.valueOf(type) + playerDto.forceId + obj, type, msg);
                break;
            }
            case AREA:
            case LEGION: {
                this.sendBigInfo(type, type, msg);
                break;
            }
        }
    }
    
    private void sendBigInfo(final String group, final String type, final String msg) {
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("type", type);
        doc.createElement("content", msg);
        doc.endObject();
        final Group g = GroupManager.getInstance().getGroup(group);
        final byte[] bytes = JsonBuilder.getJson(JsonBuilder.getJson(State.PUSH, PushCommand.PUSH_NOTICE.getModule(), doc.toByte()));
        g.notify((String)null, WrapperUtil.wrapper(PushCommand.PUSH_NOTICE.getCommand(), 0, bytes));
    }
    
    @Override
    public void sendManWangChat(final String type, final int playerId, final int forceId, final String msg, final ChatLink batLink) {
        final String systemRole = LocalMessages.MANWANG_CHAT_NAME;
        final ChatType ct = Enum.valueOf(ChatType.class, type.toUpperCase());
        if (!this.isInTransactional()) {
            this.sendSystemChat(playerId, forceId, msg, batLink, systemRole, ct, 0);
        }
        else {
            this.addTask(new Runnable() {
                @Override
                public void run() {
                    ChatService.this.sendSystemChat(playerId, forceId, msg, batLink, systemRole, ct, 0);
                }
            });
        }
    }
    
    @Override
    public void sendSystemChat(final String type, final int playerId, final int forceId, final String msg, final ChatLink batLink) {
        final String systemRole = LocalMessages.T_CHAT_ROLE_SYSTEM;
        final ChatType ct = Enum.valueOf(ChatType.class, type.toUpperCase());
        if (!this.isInTransactional()) {
            this.sendSystemChat(playerId, forceId, msg, batLink, systemRole, ct, 0);
        }
        else {
            this.addTask(new Runnable() {
                @Override
                public void run() {
                    ChatService.this.sendSystemChat(playerId, forceId, msg, batLink, systemRole, ct, 0);
                }
            });
        }
    }
    
    @Override
    public void sendYxChat(final String yx, final String msg) {
        this.SendInfo(LocalMessages.T_CHAT_ROLE_SYSTEM, String.valueOf(ChatType.YX.toString()) + yx, ChatType.GLOBAL.toString(), msg, null, 0, false);
    }
    
    private void sendSystemChat(final int playerId, final int forceId, final String msg, final ChatLink batLink, final String systemRole, final ChatType ct, final int gm) {
        switch (ct) {
            case ONE2ONE: {
                this.one2one(-1, systemRole, playerId, ct, msg, batLink, false);
            }
            case SYS2ONE: {
                this.one2one(-1, systemRole, playerId, ct, msg, batLink, false);
            }
            case GLOBAL: {
                this.one2Global(systemRole, ct, msg, gm, forceId, batLink, false);
            }
            case COUNTRY: {
                this.one2Country(systemRole, forceId, ct, msg, batLink, gm, false);
            }
            case LEGION: {
                this.one2Legion(systemRole, ct, msg, gm, false);
            }
            default: {
                throw new RuntimeException("system has not the chat type :" + ct.toString());
            }
        }
    }
    
    private byte[] one2one(final int sendPlayerId, final String sendPlayerName, final int rcvPlayerId, final ChatType type, final String message, final ChatLink battleLink, final boolean isVoice) {
        String rcvPlayerName = "";
        Player player = null;
        if (rcvPlayerId > 0) {
            player = this.playerDao.read(rcvPlayerId);
        }
        if (player != null) {
            rcvPlayerName = player.getPlayerName();
            final JsonDocument doc = new JsonDocument();
            doc.startObject();
            this.addWdPosInfo(doc, sendPlayerName);
            doc.createElement("from", sendPlayerName);
            doc.createElement("to", rcvPlayerName);
            doc.createElement("type", type.toString());
            doc.createElement("msg", message);
            doc.createElement("voice", isVoice);
            if (battleLink != null) {
                doc.createElement("chatType", battleLink.type);
                doc.createElement("params", battleLink.params);
            }
            doc.endObject();
            final Session session = Players.getSession(player.getPlayerId());
            if (session == null) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_PLAYER_10014);
            }
            Players.push(sendPlayerId, PushCommand.PUSH_CHAT_SEND, doc.toByte());
            Players.push(player.getPlayerId(), PushCommand.PUSH_CHAT_SEND, doc.toByte());
        }
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Override
    public byte[] SystemOFakene2one(final int sendPlayerId, final String sendPlayerName, final int rcvPlayerId, final ChatType type, final String message, final ChatLink battleLink) {
        String rcvPlayerName = "";
        Player player = null;
        if (rcvPlayerId > 0) {
            player = this.playerDao.read(rcvPlayerId);
        }
        if (player != null) {
            rcvPlayerName = player.getPlayerName();
            final JsonDocument doc = new JsonDocument();
            doc.startObject();
            this.addWdPosInfo(doc, sendPlayerName);
            doc.createElement("from", sendPlayerName);
            doc.createElement("to", rcvPlayerName);
            doc.createElement("type", type.toString());
            doc.createElement("msg", message);
            if (battleLink != null) {
                doc.createElement("chatType", battleLink.type);
                doc.createElement("params", battleLink.params);
            }
            doc.endObject();
            final Session session = Players.getSession(player.getPlayerId());
            if (session == null) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_PLAYER_10014);
            }
            Players.push(player.getPlayerId(), PushCommand.PUSH_CHAT_SEND, doc.toByte());
        }
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    private byte[] one2Area(final String sendPlayerName, final ChatType type, final String message, final int gm) {
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    private byte[] one2Legion(final String sendPlayerName, final ChatType type, final String message, final int gm, final boolean isVoice) {
        final String group = type.toString();
        this.SendInfo(sendPlayerName, group, group, message, null, gm, isVoice);
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    private byte[] one2Country(final String playerName, final int forceId, final ChatType type, final String message, final ChatLink batLink, final int gm, final boolean isVoice) {
        final String group = String.valueOf(type.toString()) + forceId;
        this.SendInfo(playerName, group, type.toString(), message, batLink, gm, isVoice);
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    private byte[] one2Global(final String sendPlayerName, final ChatType type, final String message, final int isGm, final int forceId, final ChatLink chatLink, final boolean isVoice) {
        String group = type.toString();
        if (forceId >= 1 && forceId <= 3) {
            group = String.valueOf("COUNTRY".toUpperCase()) + forceId;
        }
        this.SendInfo(sendPlayerName, group, type.toString(), message, chatLink, isGm, isVoice);
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    private byte[] SendInfo(final String sendPlayerName, final String group, final String type, final String msg, final ChatLink batLink, final int gm, final boolean isVoice) {
        final String rcvPlayerName = "";
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        this.addWdPosInfo(doc, sendPlayerName);
        doc.createElement("isGm", gm == 1);
        doc.createElement("from", sendPlayerName);
        doc.createElement("to", rcvPlayerName);
        doc.createElement("type", type);
        doc.createElement("msg", msg);
        doc.createElement("voice", isVoice);
        if (batLink != null) {
            doc.createElement("chatType", batLink.type);
            doc.createElement("params", batLink.params);
        }
        doc.endObject();
        final Group g = GroupManager.getInstance().getGroup(group);
        final byte[] bytes = JsonBuilder.getJson(JsonBuilder.getJson(State.PUSH, PushCommand.PUSH_CHAT_SEND.getModule(), doc.toByte()));
        g.notify((String)null, WrapperUtil.wrapper(PushCommand.PUSH_CHAT_SEND.getCommand(), 0, bytes));
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    private ChatContentNodeList getChatHistory(final int playerId) {
        ChatContentNodeList chatHistory = ChatService.chatHistoryMap.get(playerId);
        if (chatHistory == null) {
            chatHistory = new ChatContentNodeList(Integer.parseInt(Configuration.getProperty("gcld.chat.history.maxCount")), Long.parseLong(Configuration.getProperty("gcld.chat.history.expireTime")) * 60L * 1000L, Integer.parseInt(Configuration.getProperty("gcld.chat.maxRepeatedTimes")), Double.parseDouble(Configuration.getProperty("gcld.chat.minSimPoint")));
            ChatService.chatHistoryMap.put(playerId, chatHistory);
        }
        return chatHistory;
    }
    
    @Transactional
    @Override
    public void keepSilence(final String userId, final String yx, final int playerId, final String reason, final Date nextSayTime) {
        Silence silence = this.silenceDao.getByPlayerIdAndYx(playerId, yx);
        if (silence == null) {
            silence = new Silence();
            silence.setUserId(userId);
            silence.setYx(yx);
            silence.setPlayerId(playerId);
        }
        if (silence.getNextSayTime() != null && silence.getNextSayTime().after(nextSayTime)) {
            return;
        }
        final Date now = new Date();
        silence.setSilenceTime(now);
        silence.setReason(reason);
        silence.setNextSayTime(nextSayTime);
        if (silence.getSilenceId() != null) {
            this.silenceDao.update(silence);
        }
        else {
            this.silenceDao.create(silence);
        }
    }
    
    private void addWdPosInfo(final JsonDocument doc, final String sendPlayerName) {
        final String title = KfwdMatchService.getTitleByPlayerName(sendPlayerName);
        if (title != null) {
            doc.createElement("title", title);
        }
    }
    
    @Override
    public byte[] speak(String idStr, final int time, final String type, final String to, final PlayerDto playerDto) {
        if (playerDto == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_PLAYER_10005);
        }
        if (idStr.length() > Configuration.getIntProperty("gcld.chat.len")) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_CHAT_CHAT_TOO_LONG);
        }
        final Halls hall = (Halls)this.hallsCache.get((Object)this.playerOfficeRelativeDao.getOfficerId(playerDto.playerId));
        hall.getOfficialId();
        final Tuple<Boolean, String> htmlContent = WebUtil.getHTMLContent(idStr);
        idStr = htmlContent.right;
        final ICharacterFilter characterFilter = CharacterFilterFactory.getInstance().getFilter("default");
        if (characterFilter != null) {
            final String temp = characterFilter.filter(idStr);
            if (!temp.equals(idStr)) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10012);
            }
            idStr = temp;
        }
        final Silence silence = this.silenceDao.getByPlayerIdAndYx(playerDto.playerId, playerDto.yx);
        if (silence != null && silence.getNextSayTime() != null) {
            if (silence.getNextSayTime().getTime() > System.currentTimeMillis()) {
                if (silence.getType() == 1) {
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.T_CHAT_SILENCE);
                }
                return JsonBuilder.getJson(State.SUCCESS, "");
            }
            else {
                this.silenceDao.deleteById(silence.getSilenceId());
            }
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("**!").append(idStr).append("|").append(time).append("!**");
        final ChatType ct = Enum.valueOf(ChatType.class, type.toUpperCase());
        switch (ct) {
            case ONE2ONE: {
                if (to.trim().equals(LocalMessages.T_COMM_10010)) {
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.T_CHAT_PLAYER_NO_USE);
                }
                final Player player = this.playerDao.getPlayerByName(to);
                if (player == null || player.getPlayerId() == 0) {
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.T_CHAT_NO_SUCH_PLAYER);
                }
                if (!player.getForceId().equals(playerDto.forceId)) {
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.CAN_NOT_COMM_WITH_OTHER_FORCE);
                }
                if (player.getPlayerId() == playerDto.playerId) {
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.CAN_NOT_COMM_WITH_YOURSELF);
                }
                return this.one2one(playerDto.playerId, playerDto.playerName, player.getPlayerId(), ct, sb.toString(), null, true);
            }
            case COUNTRY: {
                return this.one2Country(playerDto.playerName, playerDto.forceId, ChatType.COUNTRY, sb.toString(), null, playerDto.gm, true);
            }
            default: {
                return JsonBuilder.getJson(State.FAIL, "fail");
            }
        }
    }
}
