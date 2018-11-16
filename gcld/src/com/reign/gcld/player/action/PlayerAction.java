package com.reign.gcld.player.action;

import com.reign.gcld.common.web.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.job.service.*;
import com.reign.gcld.antiaddiction.*;
import com.reign.gcld.building.service.*;
import com.reign.gcld.player.dao.*;
import com.reign.gcld.player.service.*;
import com.reign.plugin.yx.common.*;
import com.reign.gcld.common.log.*;
import org.apache.commons.logging.*;
import java.util.concurrent.*;
import com.reign.framework.netty.mvc.result.*;
import com.reign.gcld.player.dto.*;
import org.apache.commons.lang.*;
import com.reign.gcld.common.util.*;
import com.reign.gcld.user.dto.*;
import com.reign.framework.netty.mvc.annotation.*;
import com.reign.framework.json.*;
import java.util.*;
import com.reign.gcld.player.common.*;
import com.reign.gcld.log.*;
import com.reign.gcld.phantom.common.*;
import com.reign.gcld.common.event.*;
import com.reign.plugin.yx.util.kingnet.udplog.*;
import com.reign.plugin.yx.util.kingnet.demo.*;
import com.reign.util.*;
import com.reign.gcld.player.domain.*;
import com.reign.gcld.common.*;
import com.reign.framework.netty.servlet.*;
import com.reign.gcld.chat.service.*;

public class PlayerAction extends BaseAction
{
    private static final Logger log;
    private static final Logger timerLog;
    private static final long serialVersionUID = -1191779855920095921L;
    private static String DUOWAN_GAME_ID;
    @Autowired
    private IPlayerService playerService;
    @Autowired
    private IJobService jobService;
    @Autowired
    private IAntiAddictionService antiAddictionService;
    @Autowired
    private RandomNamer randomNamer;
    @Autowired
    private IBuildingService buildingService;
    @Autowired
    private IPlayerDao playerDao;
    @Autowired
    private IPlayerAttributeDao playerAttributeDao;
    @Autowired
    private IResourceService resourceService;
    @Autowired
    private IYxOperation yxOperation;
    private static final Log opReport;
    public static ConcurrentMap<String, ConcurrentMap<Integer, Integer>> pKeyMaps;
    public static ConcurrentMap<Integer, String> pIdKeyMap;
    
    static {
        log = CommonLog.getLog(PlayerAction.class);
        timerLog = new TimerLogger();
        PlayerAction.DUOWAN_GAME_ID = "GCLD";
        opReport = LogFactory.getLog("com.reign.gcld.opreport");
        PlayerAction.pKeyMaps = new ConcurrentHashMap<String, ConcurrentMap<Integer, Integer>>();
        PlayerAction.pIdKeyMap = new ConcurrentHashMap<Integer, String>();
    }
    
    @Command("player@wantToLeave")
    public ByteResult wantToLeave(final Request request) {
        return this.getResult(JsonBuilder.getJson(State.SUCCESS, "true"), request);
    }
    
    @Command("player@leave")
    public ByteResult stay(final Request request) {
        return this.getResult(JsonBuilder.getJson(State.SUCCESS, "true"), request);
    }
    
    public static void clearPKeyMaps() {
        PlayerAction.pKeyMaps.clear();
        PlayerAction.pIdKeyMap.clear();
    }
    
    public static void clearPKeyInfo(final int playerId) {
        if (PlayerAction.pIdKeyMap.containsKey(playerId)) {
            final ConcurrentMap<Integer, Integer> map = PlayerAction.pKeyMaps.get(PlayerAction.pIdKeyMap.get(playerId));
            map.remove(playerId);
            PlayerAction.pIdKeyMap.remove(playerId);
        }
    }
    
    @Command("player@music")
    public ByteResult music(@RequestParam("sType1") final int sound1, @RequestParam("sValue1") final int sound2, @RequestParam("sType2") final int sound3, @RequestParam("sValue2") final int sound4, final Request request) {
        return this.getResult(JsonBuilder.getJson(State.SUCCESS, "true"), request);
    }
    
    @Command("player@game")
    public ByteResult game(@RequestParam("pkey") final String pkey, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return this.getResult(JsonBuilder.getJson(State.FAIL, ""), request);
        }
        if (StringUtils.isBlank(pkey) || pkey.equalsIgnoreCase("null")) {
            return this.getResult(JsonBuilder.getJson(State.SUCCESS, "true"), request);
        }
        final String sysTopLv = Configuration.getProperty("gcld.player.machine.login.limit");
        if (StringUtils.isNotBlank(sysTopLv)) {
            final int limitNum = Integer.valueOf(sysTopLv);
            ConcurrentMap<Integer, Integer> pIdMap = PlayerAction.pKeyMaps.get(pkey);
            if (pIdMap == null) {
                pIdMap = new ConcurrentHashMap<Integer, Integer>(limitNum);
                PlayerAction.pKeyMaps.put(pkey, pIdMap);
            }
            if (pIdMap.size() >= limitNum) {
                if (!pIdMap.containsKey(playerDto.playerId)) {
                    final StringBuilder sb = new StringBuilder();
                    String tempPkey = null;
                    for (final Integer pid : pIdMap.keySet()) {
                        tempPkey = PlayerAction.pIdKeyMap.get(pid);
                        if (tempPkey != null && !tempPkey.equals(pkey)) {
                            pIdMap.remove(pid);
                            pIdMap.put(playerDto.playerId, 1);
                            PlayerAction.pIdKeyMap.put(playerDto.playerId, pkey);
                            PlayerAction.log.error("#playerMachineLoginLimit#playerId:" + playerDto.playerId + "#playerName:" + playerDto.playerName + "#forceId:" + playerDto.forceId + "#lv:" + playerDto.playerLv + "#pkey:" + pkey + "#" + tempPkey + "#" + WebUtil.getIpAddr(request) + "#pid:" + pid);
                            return this.getResult(JsonBuilder.getJson(State.SUCCESS, "true"), request);
                        }
                        sb.append(pid).append(",");
                    }
                    PlayerAction.log.error("#playerMachineLoginLimit#playerId:" + playerDto.playerId + "#playerName:" + playerDto.playerName + "#forceId:" + playerDto.forceId + "#lv:" + playerDto.playerLv + "#pkey:" + pkey + "#" + sb.toString() + "#" + WebUtil.getIpAddr(request));
                    this.clearSession(request);
                    return this.getResult(JsonBuilder.getJson(State.SUCCESS, LocalMessages.T_PLAYER_10011), request);
                }
            }
            else if (!pIdMap.containsKey(playerDto.playerId)) {
                pIdMap.put(playerDto.playerId, 1);
                PlayerAction.pIdKeyMap.put(playerDto.playerId, pkey);
            }
        }
        return this.getResult(JsonBuilder.getJson(State.SUCCESS, "true"), request);
    }
    
    @Command("player@setPlayerName")
    public ByteResult setPlayerName(@RequestParam("playerName") final String playerName, final Request request) {
        if (StringUtils.isBlank(playerName)) {
            return this.getResult(JsonBuilder.getJson(State.FAIL, LocalMessages.T_PLAYER_10001), request);
        }
        final int result = WebUtil.validate(playerName, Configuration.getIntProperty("gcld.playername.len"), Configuration.getPatternProperty("gcld.character.pattern"), true);
        if (result != 0) {
            return this.getResult(JsonBuilder.getJson(State.FAIL, WebUtil.getValidateMsg(result, Configuration.getIntProperty("gcld.playername.len"))), request);
        }
        if (WebUtil.containsPunctOrWhitespace(playerName)) {
            return this.getResult(JsonBuilder.getJson(State.FAIL, LocalMessages.T_PLAYER_10003), request);
        }
        if (this.playerService.validatePlayerName(playerName)) {
            return this.getResult(JsonBuilder.getJson(State.FAIL, LocalMessages.T_PLAYER_10002), request);
        }
        final PlayerDto dto = new PlayerDto();
        dto.playerName = playerName;
        this.putToSession("PLAYER", dto, request);
        return this.getResult(JsonBuilder.getJson(State.SUCCESS, ""), request);
    }
    
    @Command("player@setPlayerNames")
    public ByteResult setPlayerNames(@RequestParam("playerNames") final String[] playerNames, final Request request) {
        if (playerNames == null || playerNames.length == 0) {
            return this.getResult(JsonBuilder.getJson(State.FAIL, ""), request);
        }
        for (final String playerName : playerNames) {
            if (!StringUtils.isBlank(playerName)) {
                final int result = WebUtil.validate(playerName, Configuration.getIntProperty("gcld.playername.len"), Configuration.getPatternProperty("gcld.character.pattern"), true);
                if (result == 0) {
                    if (!WebUtil.containsPunctOrWhitespace(playerName)) {
                        if (!this.playerService.validatePlayerName(playerName)) {
                            return this.getResult(JsonBuilder.getJson(State.SUCCESS, "playerName", playerName), request);
                        }
                    }
                }
            }
        }
        return this.getResult(JsonBuilder.getJson(State.FAIL, ""), request);
    }
    
    @Command("player@getPlayerSize")
    public ByteResult getPlayerSize(@SessionParam("user") final UserDto userDto, final Request request) {
        if (userDto == null) {
            return null;
        }
        return this.getResult(this.playerService.getPlayerSize(userDto.userId, userDto.yx), request);
    }
    
    @Command("player@getPlayerList")
    public ByteResult getPlayerList(@SessionParam("user") final UserDto userDto, final Request request) {
        if (userDto == null) {
            return null;
        }
        return this.getResult(this.playerService.getPlayerList(userDto.userId, userDto.yx), request);
    }
    
    @Command("player@getRandomNames")
    public ByteResult getRandomNames(@RequestParam("male") final boolean male, @SessionParam("user") final UserDto userDto, final Request request) {
        final List<String> names = this.randomNamer.generateRandomNames(male, 5);
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("list", names);
        doc.endObject();
        return this.getResult(JsonBuilder.getJson(State.SUCCESS, doc.toByte()), request);
    }
    
    @Command("player@deletePlayer")
    public ByteResult deletePlayer(@RequestParam("playerId") final int playerId, @SessionParam("user") final UserDto userDto, final Request request) {
        if (userDto == null) {
            return null;
        }
        return this.getResult(this.playerService.deletePlayer(playerId, userDto.userId, userDto.yx), request);
    }
    
    @Command("player@retrievePlayer")
    public ByteResult retrievePlayer(@RequestParam("playerId") final int playerId, @SessionParam("user") final UserDto userDto, final Request request) {
        if (userDto == null) {
            return null;
        }
        return this.getResult(this.playerService.retrievePlayer(playerId, userDto.userId, userDto.yx), request);
    }
    
    @Command("player@getPlayerInfo")
    public ByteResult getPlayerInfo(@RequestParam("playerId") final int playerId, @RequestParam("platform") final String platform, @SessionParam("user") final UserDto userDto, final Request request) {
        if (userDto == null) {
            return null;
        }
        final Player player = this.playerDao.read(playerId);
        if (player != null && this.yxOperation.checkTencentPf(player.getYx()) && player.getYx() != userDto.yx) {
            Users.modifyUserMap(userDto.userId, userDto.yx, player.getYx());
        }
        PlatForm playerPlatForm = null;
        final String testPlatForm = "";
        if (platform == null) {
            playerPlatForm = PlatForm.PC;
        }
        else {
            playerPlatForm = PlatForm.valueOf(platform);
        }
        Tuple<Player, byte[]> tuple = null;
        if (playerId <= 0) {
            tuple = this.playerService.getPlayerInfo(playerId, userDto.userId, userDto.yx);
        }
        else {
            tuple = this.playerService.getPlayerInfo(playerId, userDto.userId);
        }
        if (tuple.left == null) {
            return this.getResult(tuple.right, request);
        }
        final PlayerDto dto = new PlayerDto();
        this.copyProperties(dto, tuple.left, userDto);
        final PlayerAttribute pa = this.playerAttributeDao.read(playerId);
        final char[] cs = pa.getFunctionId().toCharArray();
        if (tuple.left.getPlayerLv() >= 36 && cs[67] == '1') {
            cs[67] = '0';
            this.jobService.addJob("courtesyService", "closeLiShangWangLaiModule", String.valueOf(playerId) + "#" + "2", System.currentTimeMillis());
        }
        dto.cs = cs;
        dto.platForm = playerPlatForm;
        this.putToSession("PLAYER", dto, request);
        PlayerAction.timerLog.debug(LogUtil.formatThreadLog("PlayerAction", "getPlayerInfo", 0, 0L, String.valueOf(playerId) + "#" + dto.platForm.getValue() + "#" + testPlatForm));
        if (userDto.isNeedAntiAddiction()) {
            userDto.getAntiAddictionStateMachine().changeState(dto, this.jobService);
            this.antiAddictionService.sendAntiAddicitonNotice(playerId);
        }
        PhantomManager.getInstance().refreshOnePlayer(playerId);
        this.buildingService.initPlayerBuilding(playerId);
        this.joinChatGroup(playerId, request.getSession());
        EventListener.init(dto.playerId);
        final String yxSource = this.playerDao.getYxSource(playerId);
        if (StringUtils.isBlank(yxSource)) {
            this.playerDao.setYxSource(playerId, userDto.getYxSource());
        }
        this.resourceService.dealTroop(dto, 60L);
        if (this.yxOperation.checkTencentPf(userDto.yx)) {
            try {
                final LogUserInfo logInfo = new LogUserInfo();
                logInfo.setGameTime(System.currentTimeMillis() / 1000L);
                logInfo.setOuid(userDto.getOpenId());
                logInfo.setIuid(new StringBuilder(String.valueOf(playerId)).toString());
                logInfo.setUserLevel(player.getPlayerLv());
                logInfo.setVipLevel(new StringBuilder(String.valueOf(userDto.getYellowVipLevel())).toString());
                logInfo.setTimestamp(System.currentTimeMillis() / 1000L);
                final UdpSender udpSender = new UdpSender();
                udpSender.sendLoginLog("via", userDto.getYxSource(), logInfo);
            }
            catch (Exception e) {
                PlayerAction.opReport.error("#yxTencent_Login#getPlayerInfo#e:" + e);
            }
        }
        return this.getResult(tuple.right, request);
    }
    
    @Command("player@getForceInfo")
    public ByteResult getForceInfo(@SessionParam("user") final UserDto userDto, final Request request) {
        if (userDto == null) {
            return null;
        }
        return this.getResult(this.playerService.getForceInfo(userDto), request);
    }
    
    @Command("player@setPlayerForce")
    public ByteResult setPlayerForce(@RequestParam("forceId") final int forceId, @SessionParam("user") final UserDto userDto, final Request request) {
        if (userDto == null) {
            return this.getResult(JsonBuilder.getJson(State.FAIL, LocalMessages.T_USER_10004), request);
        }
        final Tuple<byte[], Boolean> tuple = this.playerService.addNewPlayer(LocalMessages.T_COMM_10010, userDto.userId, userDto.yx, userDto.getYxSource(), forceId, request);
        if (tuple.right) {
            final UserDto dto = (UserDto)this.getFromSession("user", request);
            dto.firstLogin = false;
            dto.success = true;
            this.putToSession("user", dto, request);
            final String callback = Configuration.getProperty(userDto.yx, "gcld.callback");
            if (StringUtils.isNotBlank(callback) && "1".equals(callback.trim())) {
                ThreadUtil.executor.execute(new SendCmWebRunner(request, userDto.userId, userDto.getYxSource(), userDto.yx, Configuration.getProperty(userDto.yx, "gcld.serverid")));
            }
        }
        return this.getResult(tuple.left, request);
    }
    
    @Command("player@setPlayerNameAndPic")
    public ByteResult setPlayerNameAndPic(@RequestParam("playerName") final String playerName, @RequestParam("pic") final int pic, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.playerService.setPlayerNameAndPic(playerDto, playerName, pic, request), request);
    }
    
    @Command("player@getDuowanSDKInfo")
    public ByteResult getDuowanSDKInfo(final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("userId", playerDto.userId);
        doc.createElement("gameId", PlayerAction.DUOWAN_GAME_ID);
        doc.createElement("serverId", StringUtils.lowerCase(Configuration.getProperty(playerDto.yx, "gcld.serverids")));
        doc.endObject();
        return this.getResult(JsonBuilder.getJson(State.SUCCESS, doc.toByte()), request);
    }
    
    @Command("player@setDefaultPay")
    public ByteResult setDefaultPay(@RequestParam("playerId") final int playerId, final Request request) {
        final UserDto userDto = (UserDto)this.getFromSession("user", request);
        if (userDto == null) {
            return null;
        }
        return this.getResult(this.playerService.setDefaultPay(userDto, playerId), request);
    }
    
    @Command("player@payByReceipt")
    public ByteResult payByReceipt(@RequestParam("receipt") final String receipt, @RequestParam("testing") final String testing, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.playerService.payByReceipt(playerDto, receipt, testing, request), request);
    }
    
    @Command("player@getLoginRewardInfo")
    public ByteResult getLoginRewardInfo(final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.playerService.getLoginRewardInfo(playerDto), request);
    }
    
    @Command("player@getLoginReward")
    public ByteResult getLoginReward(final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.playerService.getLoginReward(playerDto), request);
    }
    
    private void copyProperties(final PlayerDto dto, final Player player, final UserDto userDto) {
        dto.playerId = player.getPlayerId();
        dto.playerName = player.getPlayerName();
        dto.playerLv = player.getPlayerLv();
        dto.loginTime = userDto.loginTime;
        dto.userId = userDto.userId;
        dto.yx = userDto.yx;
        dto.forceId = player.getForceId();
        dto.consumeLv = player.getConsumeLv();
        dto.yxSource = userDto.getYxSource();
        dto.gm = player.getGm();
        dto.createTime = player.getCreateTime();
    }
    
    private void joinChatGroup(final int playerId, final Session session) {
        final PlayerDto playerDto = Players.getPlayer(playerId);
        if (playerDto == null) {
            return;
        }
        final int forceId = playerDto.forceId;
        GroupManager.getInstance().getGroup(ChatType.GLOBAL.toString()).join(session);
        GroupManager.getInstance().getGroup(String.valueOf(ChatType.YX.toString()) + playerDto.yx).join(session);
        GroupManager.getInstance().getGroup(String.valueOf(ChatType.COUNTRY.toString()) + forceId).join(session);
        final int playerLv = playerDto.playerLv;
        if (playerLv <= 30) {
            GroupManager.getInstance().getGroup(String.valueOf(ChatType.COUNTRY.toString()) + forceId + "_" + 1).join(session);
        }
        else if (playerLv <= 50) {
            GroupManager.getInstance().getGroup(String.valueOf(ChatType.COUNTRY.toString()) + forceId + "_" + 2).join(session);
        }
        final char[] cs = this.playerAttributeDao.getFunctionId(playerId).toCharArray();
        if (cs[10] == '1') {
            if (1 == forceId) {
                GroupManager.getInstance().getGroup(ChatType.WORLD_OPENED_1.toString()).join(session);
            }
            else if (2 == forceId) {
                GroupManager.getInstance().getGroup(ChatType.WORLD_OPENED_2.toString()).join(session);
            }
            else if (3 == forceId) {
                GroupManager.getInstance().getGroup(ChatType.WORLD_OPENED_3.toString()).join(session);
            }
        }
    }
}
