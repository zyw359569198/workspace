package com.reign.gcld.courtesy.service;

import org.springframework.stereotype.*;
import org.springframework.beans.factory.annotation.*;
import java.util.concurrent.*;
import com.reign.gcld.courtesy.common.*;
import com.reign.gcld.player.dto.*;
import com.reign.gcld.player.domain.*;
import com.reign.gcld.courtesy.domain.*;
import com.reign.framework.json.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.util.*;
import com.reign.gcld.battle.common.*;
import org.springframework.transaction.annotation.*;
import com.reign.gcld.common.*;
import com.reign.gcld.chat.service.*;
import com.reign.gcld.chat.common.*;
import java.util.*;
import java.io.*;

@Service("courtesyService")
public class CourtesyService implements ICourtesyService
{
    @Autowired
    private IDataGetter dataGetter;
    public static final int PUSH_LIYIDU_REACH_MAX = 1;
    public static final int PUSH_LIYIDU_CLOSE = 2;
    
    @Override
    public void addXiaoQianEvent(final int playerId, final int step) {
        try {
            final PlayerDto playerDto = Players.getPlayer(playerId);
            if (playerDto == null) {
                return;
            }
            final EtiqueteEvent etiqueteEvent = this.dataGetter.getEtiqueteEventCache().xiaoQianEtiqueteEvent;
            final int forceId = playerDto.forceId;
            final ConcurrentHashMap<Integer, PlayerCourtesyObj> countrymap = CourtesyManager.getInstance().PCOContainer.get(forceId);
            final PlayerCourtesyObj playerCourtesyObj = countrymap.get(playerDto.playerId);
            synchronized (countrymap) {
                final CourtesyEvent courtesyEvent = new CourtesyEvent();
                courtesyEvent.id = CourtesyEvent.atomicInteger.incrementAndGet();
                if (step == 1) {
                    courtesyEvent.type = 2;
                }
                else if (step == 2) {
                    courtesyEvent.type = 1;
                }
                else {
                    ErrorSceneLog.getInstance().appendErrorMsg("step is invalid, set as type 1").appendPlayerId(playerDto.playerId).appendPlayerName(playerDto.playerName).append("step", step).appendClassName("CourtesyService").appendMethodName("addXiaoQianEvent").flush();
                    courtesyEvent.type = 1;
                }
                courtesyEvent.playerId = 0;
                courtesyEvent.playerName = LocalMessages.XIAO_QIAN;
                courtesyEvent.playerPic = 10;
                courtesyEvent.playerLv = 1;
                courtesyEvent.eventId = etiqueteEvent.getId();
                courtesyEvent.state = 1;
                playerCourtesyObj.needHandleList.add(0, courtesyEvent);
                playerCourtesyObj.cutNeedHandleList();
            }
            // monitorexit(countrymap)
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("CourtesyService.addPlayerLvUpEvent catch Exception", e);
        }
    }
    
    @Override
    public void addPlayerEvent(final int playerId, final int eventID, final int taskId) {
        try {
            final PlayerDto playerDto = CourtesyManager.needHandleCourtesyEvent(playerId);
            if (playerDto == null) {
                return;
            }
            final EtiqueteEvent etiqueteEvent = this.dataGetter.getEtiqueteEventCache().getEtiqueteEvent(eventID, taskId);
            if (etiqueteEvent == null) {
                return;
            }
            CourtesyManager.getInstance().addNewCourtesyEvent(playerDto, etiqueteEvent.getId());
            if (etiqueteEvent == this.dataGetter.getEtiqueteEventCache().lvUpEtiqueteEvent) {
                this.addPlayerReachLimitEvent(playerId);
            }
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("CourtesyService.addPlayerLvUpEvent catch Exception", e);
        }
    }
    
    private void addPlayerReachLimitEvent(final int playerId) {
        try {
            final PlayerDto playerDto = CourtesyManager.needHandleCourtesyEvent(playerId);
            if (playerDto == null) {
                return;
            }
            final Player player = this.dataGetter.getPlayerDao().read(playerId);
            if (player.getPlayerLv() == 35) {
                if (this.dataGetter.getEtiqueteEventCache().reachLimitEtiqueteEvent != null) {
                    final EtiqueteEvent etiqueteEvent = this.dataGetter.getEtiqueteEventCache().reachLimitEtiqueteEvent;
                    CourtesyManager.getInstance().addNewCourtesyEvent(playerDto, etiqueteEvent.getId());
                }
            }
            else if (player.getPlayerLv() >= 36) {
                this.dataGetter.getJobService().addJob("courtesyService", "closeLiShangWangLaiModule", String.valueOf(playerId) + "#" + "2", System.currentTimeMillis(), true);
            }
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("CourtesyService.addPlayerReachLimitEvent catch Exception", e);
        }
    }
    
    private Tuple<Boolean, String> checkOpen(final PlayerDto playerDto) {
        final Tuple<Boolean, String> tuple = new Tuple();
        tuple.left = false;
        final char[] cs = this.dataGetter.getPlayerAttributeDao().getFunctionId(playerDto.playerId).toCharArray();
        if (cs[67] != '1') {
            tuple.right = LocalMessages.T_COMM_10020;
            return tuple;
        }
        PlayerLiYi playerLiYi = this.dataGetter.getPlayerLiYiDao().read(playerDto.playerId);
        if (playerLiYi == null) {
            ErrorSceneLog.getInstance().appendErrorMsg("playerLiYi is null").appendPlayerId(playerDto.playerId).appendPlayerName(playerDto.playerName).appendClassName("CourtesyService").appendMethodName("checkOpen").flush();
            playerLiYi = new PlayerLiYi();
            playerLiYi.setPlayerId(playerDto.playerId);
            playerLiYi.setLiYiDu(0);
            playerLiYi.setRewardInfo(null);
            this.dataGetter.getPlayerLiYiDao().create(playerLiYi);
        }
        tuple.left = true;
        return tuple;
    }
    
    @Override
    public byte[] getPanel(final PlayerDto playerDto) {
        final Tuple<Boolean, String> tuple = this.checkOpen(playerDto);
        if (!(boolean)tuple.left) {
            return JsonBuilder.getJson(State.FAIL, tuple.right);
        }
        final PlayerLiYi playerLiYi = this.dataGetter.getPlayerLiYiDao().read(playerDto.playerId);
        if (playerLiYi == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.WE_ARE_SORRY_ERROR_HAPPENED);
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        this.getLiYiDuOfPanel(doc, playerLiYi);
        final PlayerCourtesyObj playerCourtesyObj = CourtesyManager.getInstance().getPlayerCourtesyObj(playerDto);
        if (playerCourtesyObj != null) {
            final PlayerCourtesyObj playerCourtesyObj2 = playerCourtesyObj;
            ++playerCourtesyObj2.openPanelCount;
            this.getEventsOfPanel(doc, playerCourtesyObj);
            doc.createElement("flag", playerCourtesyObj.openPanelCount);
        }
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    private void getLiYiDuOfPanel(final JsonDocument doc, final PlayerLiYi playerLiYi) {
        final Set<Integer> rewardedSet = new HashSet<Integer>();
        if (playerLiYi.getRewardInfo() != null) {
            final String[] rewardStrings = playerLiYi.getRewardInfo().split(",");
            String[] array;
            for (int length = (array = rewardStrings).length, i = 0; i < length; ++i) {
                final String temp = array[i];
                rewardedSet.add(Integer.parseInt(temp));
            }
        }
        final int max = this.dataGetter.getEtiquetePointCache().getMaxLiYiDu();
        doc.createElement("point", playerLiYi.getLiYiDu());
        doc.createElement("percent", playerLiYi.getLiYiDu() / max * 100);
        doc.startArray("rewards");
        for (final EtiquetePoint etiquetePoint : this.dataGetter.getEtiquetePointCache().getLiYiDuOrderList()) {
            doc.startObject();
            doc.createElement("rewardId", etiquetePoint.getId());
            doc.createElement("point", etiquetePoint.getDemand());
            doc.createElement("type", etiquetePoint.getRewardDrop().type);
            doc.createElement("num", etiquetePoint.getRewardDrop().num);
            if (rewardedSet.contains(etiquetePoint.getId())) {
                doc.createElement("rewarded", 1);
            }
            else {
                doc.createElement("rewarded", 0);
            }
            doc.endObject();
        }
        doc.endArray();
    }
    
    private void getEventsOfPanel(final JsonDocument doc, final PlayerCourtesyObj playerCourtesyObj) {
        doc.startArray("events");
        int count = 0;
        for (final CourtesyEvent courtesyEvent : playerCourtesyObj.needHandleList) {
            final EtiqueteEvent etiqueteEvent = (EtiqueteEvent)this.dataGetter.getEtiqueteEventCache().get((Object)courtesyEvent.eventId);
            if (etiqueteEvent == null) {
                ErrorSceneLog.getInstance().appendErrorMsg("etiqueteEvent is null").appendPlayerId(playerCourtesyObj.playerId).append("courtesyEvent.playerId", courtesyEvent.playerId).append("courtesyEvent.playerName", courtesyEvent.playerName).append("courtesyEvent.eventId", courtesyEvent.eventId).appendClassName("CourtesyService").appendMethodName("getEventsOfPanel.needHandleList").flush();
            }
            else {
                String format = null;
                BattleDrop battleDrop = null;
                int liYiDU = 0;
                if (courtesyEvent.type == 1) {
                    format = etiqueteEvent.getWords();
                    battleDrop = etiqueteEvent.getSendRewardDrop();
                    liYiDU = etiqueteEvent.getSendPoint();
                }
                else {
                    if (courtesyEvent.type != 2) {
                        ErrorSceneLog.getInstance().appendErrorMsg("type error").appendPlayerId(playerCourtesyObj.playerId).appendClassName("CourtesyService").appendMethodName("getEventsOfPanel").flush();
                        continue;
                    }
                    format = etiqueteEvent.getReply();
                    battleDrop = etiqueteEvent.getReplyRewardDrop();
                    liYiDU = etiqueteEvent.getReplyPoint();
                }
                doc.startObject();
                doc.createElement("eventId", courtesyEvent.id);
                doc.createElement("playerPic", courtesyEvent.playerPic);
                doc.createElement("playerLv", courtesyEvent.playerLv);
                doc.createElement("msg", MessageFormatter.format(format, new Object[] { ColorUtil.getGreenMsg(courtesyEvent.playerName) }));
                doc.createElement("button", courtesyEvent.type);
                if (liYiDU > 0) {
                    doc.createElement("liYiDU", liYiDU);
                }
                if (battleDrop != null) {
                    doc.createElement("rewardType", battleDrop.type);
                    doc.createElement("rewardNum", battleDrop.num);
                }
                doc.createElement("state", courtesyEvent.state);
                doc.endObject();
                if (++count >= 4) {
                    break;
                }
                continue;
            }
        }
        doc.endArray();
    }
    
    @Transactional
    @Override
    public byte[] handleEvent(final PlayerDto playerDto, final int eventId) {
        final Tuple<Boolean, String> tuple = this.checkOpen(playerDto);
        if (!(boolean)tuple.left) {
            return JsonBuilder.getJson(State.FAIL, tuple.right);
        }
        final PlayerCourtesyObj playerCourtesyObj = CourtesyManager.getInstance().getPlayerCourtesyObj(playerDto);
        if (playerCourtesyObj == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.WE_ARE_SORRY_ERROR_HAPPENED);
        }
        CourtesyEvent targetCourtesyEvent = null;
        for (final CourtesyEvent courtesyEvent : playerCourtesyObj.needHandleList) {
            if (courtesyEvent.id == eventId) {
                targetCourtesyEvent = courtesyEvent;
                break;
            }
        }
        if (targetCourtesyEvent == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.PLUG_IS_SHAMEFUL);
        }
        if (targetCourtesyEvent.state != 1) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.PLUG_IS_SHAMEFUL);
        }
        final ConcurrentHashMap<Integer, PlayerCourtesyObj> countrymap = CourtesyManager.getInstance().PCOContainer.get(playerDto.forceId);
        final PlayerLiYi playerLiYi = this.dataGetter.getPlayerLiYiDao().read(playerDto.playerId);
        Tuple<Integer, BattleDrop> gain = null;
        int liYiDuGain = 0;
        synchronized (countrymap) {
            if (targetCourtesyEvent.eventId == this.dataGetter.getEtiqueteEventCache().xiaoQianEtiqueteEvent.getId()) {
                gain = this.handleXiaoQianEvent(playerDto, playerCourtesyObj, targetCourtesyEvent);
            }
            else if (targetCourtesyEvent.type == 1) {
                gain = this.handleCourtesyEvent(playerDto, playerCourtesyObj, targetCourtesyEvent);
            }
            else {
                if (targetCourtesyEvent.type != 2) {
                    ErrorSceneLog.getInstance().appendErrorMsg("type error").appendPlayerId(playerDto.playerId).appendPlayerName(playerDto.playerName).append("targetCourtesyEvent.type", targetCourtesyEvent.type).append("targetCourtesyEvent.playerId", targetCourtesyEvent.playerId).append("targetCourtesyEvent.playerName", targetCourtesyEvent.playerName).append("targetCourtesyEvent.eventId", targetCourtesyEvent.eventId).appendClassName("CourtesyManager").appendMethodName("needHandleCourtesyEvent").flush();
                    // monitorexit(countrymap)
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.WE_ARE_SORRY_ERROR_HAPPENED);
                }
                gain = this.replyCourtesyEvent(playerDto, playerCourtesyObj, targetCourtesyEvent);
            }
            liYiDuGain = gain.left;
            if (liYiDuGain > 0 && liYiDuGain + playerLiYi.getLiYiDu() >= this.dataGetter.getEtiquetePointCache().getMaxLiYiDu()) {
                liYiDuGain = this.dataGetter.getEtiquetePointCache().getMaxLiYiDu() - playerLiYi.getLiYiDu();
                playerCourtesyObj.liYiDuReachMax = true;
            }
        }
        // monitorexit(countrymap)
        this.dataGetter.getBattleDropService().saveBattleDrop(playerDto.playerId, gain.right, "\u793c\u5c1a\u5f80\u6765\u83b7\u5f97");
        if (liYiDuGain > 0) {
            this.dataGetter.getPlayerLiYiDao().addliYiDu(playerDto.playerId, liYiDuGain);
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        if (liYiDuGain > 0) {
            doc.createElement("liYiDu", liYiDuGain);
        }
        if (gain.right != null && gain.right.type != 0) {
            doc.createElement("type", gain.right.type);
            doc.createElement("num", gain.right.num);
        }
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    private Tuple<Integer, BattleDrop> handleXiaoQianEvent(final PlayerDto playerDto, final PlayerCourtesyObj playerCourtesyObj, final CourtesyEvent courtesyEvent) {
        final int eventId = courtesyEvent.eventId;
        if (eventId != this.dataGetter.getEtiqueteEventCache().xiaoQianEtiqueteEvent.getId()) {
            ErrorSceneLog.getInstance().appendErrorMsg("xiaoqian eventId invalid").appendPlayerId(playerDto.playerId).appendPlayerName(playerDto.playerName).append("eventId", eventId).append("courtesyEvent.playerId", courtesyEvent.playerId).append("courtesyEvent.playerName", courtesyEvent.playerName).appendClassName("CourtesyService").appendMethodName("handleXiaoQianEvent").flush();
            return null;
        }
        final EtiqueteEvent etiqueteEvent = (EtiqueteEvent)this.dataGetter.getEtiqueteEventCache().get((Object)eventId);
        final Tuple<Integer, BattleDrop> result = new Tuple();
        if (courtesyEvent.type == 2) {
            courtesyEvent.state = 2;
            result.left = etiqueteEvent.getReplyPoint();
            result.right = etiqueteEvent.getReplyRewardDrop();
            this.addXiaoQianEvent(playerDto.playerId, 2);
        }
        else {
            if (courtesyEvent.type != 1) {
                ErrorSceneLog.getInstance().appendErrorMsg("courtesyEvent.type error").append("courtesyEvent.type", courtesyEvent.type).appendPlayerId(playerDto.playerId).appendPlayerName(playerDto.playerName).append("eventId", eventId).append("courtesyEvent.playerId", courtesyEvent.playerId).append("courtesyEvent.playerName", courtesyEvent.playerName).appendClassName("CourtesyService").appendMethodName("handleXiaoQianEvent").flush();
                return null;
            }
            courtesyEvent.state = 2;
            result.left = etiqueteEvent.getSendPoint();
            result.right = etiqueteEvent.getSendRewardDrop();
            playerCourtesyObj.inXinShouYInDao = false;
        }
        return result;
    }
    
    private Tuple<Integer, BattleDrop> handleCourtesyEvent(final PlayerDto playerDto, final PlayerCourtesyObj playerCourtesyObj, final CourtesyEvent courtesyEvent) {
        final int eventId = courtesyEvent.eventId;
        final EtiqueteEvent etiqueteEvent = (EtiqueteEvent)this.dataGetter.getEtiqueteEventCache().get((Object)eventId);
        this.addReplyEventForCounterPlayer(playerDto, courtesyEvent);
        this.sendOneToOneChat(playerDto, courtesyEvent.playerId);
        courtesyEvent.state = 2;
        final Tuple<Integer, BattleDrop> result = new Tuple();
        result.left = etiqueteEvent.getSendPoint();
        result.right = etiqueteEvent.getSendRewardDrop();
        return result;
    }
    
    private void addReplyEventForCounterPlayer(final PlayerDto selfPlayerDto, final CourtesyEvent courtesyEvent) {
        final int counterPlayerId = courtesyEvent.playerId;
        final PlayerDto playerDto = CourtesyManager.needHandleCourtesyEvent(counterPlayerId);
        if (playerDto == null) {
            return;
        }
        final int forceId = selfPlayerDto.forceId;
        final ConcurrentHashMap<Integer, PlayerCourtesyObj> countrymap = CourtesyManager.getInstance().PCOContainer.get(forceId);
        final PlayerCourtesyObj counterPlayerCourtesyObj = countrymap.get(counterPlayerId);
        if (counterPlayerCourtesyObj == null) {
            ErrorSceneLog.getInstance().appendErrorMsg("counterPlayerCourtesyObj is null").appendPlayerId(selfPlayerDto.playerId).appendPlayerName(selfPlayerDto.playerName).append("counterPlayerId", counterPlayerId).appendClassName("CourtesyService").appendMethodName("addReplyEvent").flush();
            return;
        }
        final CourtesyEvent replyEvent = new CourtesyEvent();
        replyEvent.id = CourtesyEvent.atomicInteger.incrementAndGet();
        replyEvent.type = 2;
        replyEvent.playerId = selfPlayerDto.playerId;
        final Player selfPlayer = this.dataGetter.getPlayerDao().read(selfPlayerDto.playerId);
        replyEvent.playerName = selfPlayer.getPlayerName();
        replyEvent.playerPic = selfPlayer.getPic();
        replyEvent.playerLv = selfPlayer.getPlayerLv();
        replyEvent.eventId = courtesyEvent.eventId;
        replyEvent.state = 1;
        counterPlayerCourtesyObj.needHandleList.add(0, replyEvent);
        counterPlayerCourtesyObj.cutNeedHandleList();
        final JsonDocument doc2 = new JsonDocument();
        doc2.startObject();
        doc2.createElement("liShangWangLai", true);
        doc2.endObject();
        Players.push(counterPlayerCourtesyObj.playerId, PushCommand.PUSH_UPDATE, doc2.toByte());
    }
    
    private void sendOneToOneChat(final PlayerDto selfPlayerDto, final int counterPlayerId) {
        try {
            final PlayerDto counterPlayerDto = Players.getPlayer(counterPlayerId);
            if (counterPlayerDto == null) {
                return;
            }
            if (selfPlayerDto.playerLv < 16) {
                return;
            }
            if (counterPlayerDto.playerLv <= selfPlayerDto.playerLv) {
                return;
            }
            Set<Integer> chattedSet = CourtesyManager.getInstance().chattedMapSet.get(selfPlayerDto.playerId);
            if (chattedSet != null && chattedSet.contains(counterPlayerId)) {
                return;
            }
            final String msg = this.dataGetter.getChatWordsCache().getRandomChatWords(counterPlayerDto.playerLv);
            if (msg == null) {
                return;
            }
            this.dataGetter.getChatService().SystemOFakene2one(selfPlayerDto.playerId, selfPlayerDto.playerName, counterPlayerDto.playerId, ChatType.ONE2ONE, msg, null);
            if (chattedSet == null) {
                chattedSet = new HashSet<Integer>();
                CourtesyManager.getInstance().chattedMapSet.put(selfPlayerDto.playerId, chattedSet);
            }
            chattedSet.add(counterPlayerId);
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("CourtesyService.sendOneToOneChat catch Exception", e);
        }
    }
    
    private Tuple<Integer, BattleDrop> replyCourtesyEvent(final PlayerDto playerDto, final PlayerCourtesyObj playerCourtesyObj, final CourtesyEvent courtesyEvent) {
        final int eventId = courtesyEvent.eventId;
        final EtiqueteEvent etiqueteEvent = (EtiqueteEvent)this.dataGetter.getEtiqueteEventCache().get((Object)eventId);
        courtesyEvent.state = 2;
        final Tuple<Integer, BattleDrop> result = new Tuple();
        result.left = etiqueteEvent.getReplyPoint();
        result.right = etiqueteEvent.getReplyRewardDrop();
        return result;
    }
    
    @Transactional
    @Override
    public byte[] getLiYiDuReward(final PlayerDto playerDto, final int rewardId) {
        final Tuple<Boolean, String> tuple = this.checkOpen(playerDto);
        if (!(boolean)tuple.left) {
            return JsonBuilder.getJson(State.FAIL, tuple.right);
        }
        final EtiquetePoint etiquetePoint = (EtiquetePoint)this.dataGetter.getEtiquetePointCache().get((Object)rewardId);
        if (etiquetePoint == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.PLUG_IS_SHAMEFUL);
        }
        final PlayerLiYi playerLiYi = this.dataGetter.getPlayerLiYiDao().read(playerDto.playerId);
        if (playerLiYi.getLiYiDu() < etiquetePoint.getDemand()) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.LISHANGWANGLAI_REWARD_GAINED);
        }
        boolean allRewarded = false;
        String rewardInfo = null;
        if (playerLiYi.getRewardInfo() != null) {
            final String[] rewardStrings = playerLiYi.getRewardInfo().split(",");
            String[] array;
            for (int length = (array = rewardStrings).length, i = 0; i < length; ++i) {
                final String temp = array[i];
                if (Integer.toString(rewardId).equalsIgnoreCase(temp)) {
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.LISHANGWANGLAI_REWARD_GAINED);
                }
            }
            rewardInfo = String.valueOf(playerLiYi.getRewardInfo()) + "," + rewardId;
            allRewarded = (rewardStrings.length == this.dataGetter.getEtiquetePointCache().getCacheMap().size() - 1);
        }
        else {
            rewardInfo = Integer.toString(rewardId);
        }
        this.dataGetter.getBattleDropService().saveBattleDrop(playerDto.playerId, etiquetePoint.getRewardDrop(), "\u793c\u5c1a\u5f80\u6765\u83b7\u5f97");
        if (allRewarded) {
            this.dataGetter.getJobService().addJob("courtesyService", "closeLiShangWangLaiModule", String.valueOf(playerDto.playerId) + "#" + "1", System.currentTimeMillis(), true);
        }
        this.dataGetter.getPlayerLiYiDao().updateRewardInfo(playerDto.playerId, rewardInfo);
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Override
    public void removePlayerAfterLogOut(final PlayerDto playerDto) {
        try {
            final ConcurrentHashMap<Integer, PlayerCourtesyObj> countrymap = CourtesyManager.getInstance().PCOContainer.get(playerDto.forceId);
            countrymap.remove(playerDto.playerId);
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("CourtesyService.removePlayerAfterLogOut catch Exception", e);
        }
    }
    
    @Transactional
    @Override
    public void closeLiShangWangLaiModule(final String params) {
        try {
            final String[] paramArray = params.split("#");
            final int playerId = Integer.parseInt(paramArray[0]);
            int type = 0;
            if (paramArray.length > 1) {
                type = Integer.parseInt(paramArray[1]);
            }
            final char[] cs = this.dataGetter.getPlayerAttributeDao().getFunctionId(playerId).toCharArray();
            cs[67] = '0';
            final PlayerDto playerDto = Players.getPlayer(playerId);
            if (playerDto != null) {
                playerDto.cs = cs;
            }
            this.dataGetter.getPlayerAttributeDao().updateFunction(playerId, new String(cs));
            final Player player = this.dataGetter.getPlayerDao().read(playerId);
            final ConcurrentHashMap<Integer, PlayerCourtesyObj> countrymap = CourtesyManager.getInstance().PCOContainer.get(player.getForceId());
            countrymap.remove(player.getPlayerId());
            if (type == 2) {
                this.autoSaveLiYiDuReward(player);
            }
            final JsonDocument pushDoc = new JsonDocument();
            pushDoc.startObject();
            pushDoc.createElement("type", 2);
            pushDoc.endObject();
            final byte[] send = pushDoc.toByte();
            Players.push(playerId, PushCommand.PUSH_COURTESY_EVENT, send);
            final JsonDocument doc2 = new JsonDocument();
            doc2.startObject();
            doc2.createElement("liShangWangLai", false);
            doc2.endObject();
            Players.push(playerId, PushCommand.PUSH_UPDATE, doc2.toByte());
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("CourtesyService.closeLiShangWangLaiModule catch Exception", e);
        }
    }
    
    private void autoSaveLiYiDuReward(final Player player) {
        try {
            final int playerId = player.getPlayerId();
            final PlayerLiYi playerLiYi = this.dataGetter.getPlayerLiYiDao().read(playerId);
            if (playerLiYi != null) {
                final Set<Integer> rewardedSet = new HashSet<Integer>();
                if (playerLiYi.getRewardInfo() != null) {
                    final String[] rewardStrings = playerLiYi.getRewardInfo().split(",");
                    String[] array;
                    for (int length = (array = rewardStrings).length, i = 0; i < length; ++i) {
                        final String temp = array[i];
                        rewardedSet.add(Integer.parseInt(temp));
                    }
                }
                if (rewardedSet.size() < this.dataGetter.getEtiquetePointCache().getCacheMap().size()) {
                    final StringBuilder mailMsg = new StringBuilder();
                    mailMsg.append(LocalMessages.LISHANGWANGLAI_LIYIDU_LINGQU_FORMAT);
                    boolean notEmpty = false;
                    for (final EtiquetePoint etiquetePoint : this.dataGetter.getEtiquetePointCache().getLiYiDuOrderList()) {
                        if (playerLiYi.getLiYiDu() < etiquetePoint.getDemand()) {
                            continue;
                        }
                        if (rewardedSet.contains(etiquetePoint.getId())) {
                            continue;
                        }
                        notEmpty = true;
                        this.dataGetter.getBattleDropService().saveBattleDrop(playerId, etiquetePoint.getRewardDrop(), "\u793c\u5c1a\u5f80\u6765\u83b7\u5f97");
                        mailMsg.append(BattleDrop.getDropToString(etiquetePoint.getReward())).append(",");
                    }
                    if (notEmpty) {
                        mailMsg.replace(mailMsg.length() - 1, mailMsg.length(), "");
                        this.dataGetter.getMailService().writeSystemMail(LocalMessages.T_MAIL_ROLE_SYSTEM, LocalMessages.LISHANGWANGLAI_LIYIDU_LINGQU_TITLE, mailMsg.toString(), 1, playerId, new Date());
                    }
                }
                this.dataGetter.getPlayerLiYiDao().deleteById(playerId);
            }
            else {
                ErrorSceneLog.getInstance().appendErrorMsg("playerLiYi is null.").appendPlayerId(playerId).appendPlayerName(player.getPlayerName()).append("playerLv", player.getPlayerLv()).appendClassName("CourtesyService").appendMethodName("autoSaveLiYiDuReward").flush();
            }
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("CourtesyService.autoSaveLiYiDuReward catch Exception", e);
        }
    }
}
