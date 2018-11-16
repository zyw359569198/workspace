package com.reign.gcld.courtesy.common;

import org.springframework.stereotype.*;
import com.reign.gcld.common.log.*;
import java.util.concurrent.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.player.dto.*;
import com.reign.gcld.battle.common.*;
import com.reign.gcld.player.domain.*;
import com.reign.gcld.log.*;
import com.reign.util.*;
import com.reign.framework.json.*;
import com.reign.gcld.common.*;
import java.util.*;
import com.reign.gcld.common.util.*;

@Component("courtesyManager")
public class CourtesyManager
{
    private static final Logger timeLog;
    public ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, PlayerCourtesyObj>> PCOContainer;
    public ConcurrentHashMap<Integer, Set<Integer>> chattedMapSet;
    private static final CourtesyManager instance;
    @Autowired
    private IDataGetter dataGetter;
    public static final int MIN_CHAT_LV = 16;
    public static final int MAX_LV = 36;
    public static final int FIRST_FRIENDS_SIZE = 5;
    public static final int CHOOSE_LV_BIAS = 5;
    
    static {
        timeLog = new TimerLogger();
        instance = new CourtesyManager();
    }
    
    private CourtesyManager() {
        this.PCOContainer = new ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, PlayerCourtesyObj>>();
        this.chattedMapSet = new ConcurrentHashMap<Integer, Set<Integer>>();
        for (final Integer forceId : Constants.PLAYER_FORCE_SET) {
            this.PCOContainer.put(forceId, new ConcurrentHashMap<Integer, PlayerCourtesyObj>());
        }
    }
    
    public static CourtesyManager getInstance() {
        return CourtesyManager.instance;
    }
    
    public static PlayerDto needHandleCourtesyEvent(final int playerId) {
        final PlayerDto playerDto = Players.getPlayer(playerId);
        if (playerDto == null) {
            return null;
        }
        final ConcurrentHashMap<Integer, PlayerCourtesyObj> countrymap = CourtesyManager.instance.PCOContainer.get(playerDto.forceId);
        if (countrymap == null) {
            ErrorSceneLog.getInstance().appendErrorMsg("forceId error").appendPlayerId(playerDto.playerId).appendPlayerName(playerDto.playerName).append("forceId", playerDto.forceId).appendClassName("CourtesyManager").appendMethodName("needHandleCourtesyEvent").flush();
            return null;
        }
        if (playerDto.cs[67] == '0') {
            return null;
        }
        final PlayerCourtesyObj playerCourtesyObj = countrymap.get(playerDto.playerId);
        if (playerCourtesyObj == null) {
            ErrorSceneLog.getInstance().appendErrorMsg("function closed, but playerCourtesyObj is null").appendPlayerId(playerDto.playerId).appendPlayerName(playerDto.playerName).appendClassName("CourtesyManager").appendMethodName("needHandleCourtesyEvent").flush();
            return null;
        }
        if (playerCourtesyObj.inXinShouYInDao) {
            return null;
        }
        if (playerCourtesyObj.liYiDuReachMax) {
            return null;
        }
        if (playerDto.playerLv > 36) {
            return null;
        }
        return playerDto;
    }
    
    public void initCourtesyManager(final IDataGetter dataGetter) {
        this.dataGetter = dataGetter;
    }
    
    private boolean validForceId(final PlayerDto playerDto) {
        final int forceId = playerDto.forceId;
        final ConcurrentHashMap<Integer, PlayerCourtesyObj> countrymap = this.PCOContainer.get(forceId);
        if (countrymap == null) {
            ErrorSceneLog.getInstance().appendErrorMsg("forceId error").appendPlayerId(playerDto.playerId).appendPlayerName(playerDto.playerName).append("forceId", forceId).appendClassName("CourtesyManager").appendMethodName("validForceId").flush();
            return false;
        }
        return true;
    }
    
    public void addPlayerToContainer(final int playerId, final boolean isNewlyOpen) {
        try {
            final Player player = this.dataGetter.getPlayerDao().read(playerId);
            final ConcurrentHashMap<Integer, PlayerCourtesyObj> countrymap = this.PCOContainer.get(player.getForceId());
            if (countrymap == null) {
                ErrorSceneLog.getInstance().appendErrorMsg("forceId error").appendPlayerId(playerId).appendPlayerName(player.getPlayerName()).append("forceId", player.getForceId()).appendClassName("CourtesyManager").appendMethodName("addPlayerToContainer").flush();
                return;
            }
            final PlayerCourtesyObj playerCourtesyObj = new PlayerCourtesyObj();
            playerCourtesyObj.playerId = playerId;
            playerCourtesyObj.playerLv = player.getPlayerLv();
            playerCourtesyObj.recommendCount = 0;
            if (isNewlyOpen) {
                playerCourtesyObj.inXinShouYInDao = true;
                playerCourtesyObj.openPanelCount = 0;
            }
            else {
                playerCourtesyObj.inXinShouYInDao = false;
                playerCourtesyObj.openPanelCount = 10;
            }
            countrymap.put(playerCourtesyObj.playerId, playerCourtesyObj);
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("CourtesyManager.addPlayerToContainer catch Exception", e);
        }
    }
    
    public void addNewCourtesyEvent(final PlayerDto playerDto, final int eventId) {
        try {
            if (!this.validForceId(playerDto)) {
                return;
            }
            final int forceId = playerDto.forceId;
            final ConcurrentHashMap<Integer, PlayerCourtesyObj> countrymap = this.PCOContainer.get(forceId);
            final PlayerCourtesyObj playerCourtesyObj = countrymap.get(playerDto.playerId);
            if (playerCourtesyObj == null) {
                ErrorSceneLog.getInstance().appendErrorMsg("playerCourtesyObj is null").appendPlayerId(playerDto.playerId).appendPlayerName(playerDto.playerName).append("eventId", eventId).appendClassName("CourtesyManager").appendMethodName("addCourtesyEvent").flush();
            }
            synchronized (countrymap) {
                playerCourtesyObj.newlyEventId = eventId;
                this.getRealCourtesyEvent(countrymap, playerCourtesyObj);
            }
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("CourtesyManager.addCourtesyEvent catch Exception", e);
        }
    }
    
    private void getRealCourtesyEvent(final ConcurrentHashMap<Integer, PlayerCourtesyObj> countrymap, final PlayerCourtesyObj playerCourtesyObj) {
        try {
            final long start = System.currentTimeMillis();
            CourtesyManager.timeLog.debug(LogUtil.formatThreadLog("CourtesyManager", "getCourtesyEvent", 0, 0L, "playerId#" + playerCourtesyObj.playerId));
            final List<Tuple<Integer, PlayerCourtesyObj>> optionList = new LinkedList<Tuple<Integer, PlayerCourtesyObj>>();
            final List<Tuple<Integer, PlayerCourtesyObj>> optionListFirst5 = new LinkedList<Tuple<Integer, PlayerCourtesyObj>>();
            long recommendSum = 0L;
            for (final PlayerCourtesyObj temp : countrymap.values()) {
                if (temp != playerCourtesyObj && temp.newlyEventId > 0 && temp.playerLv >= playerCourtesyObj.playerLv - 5 && temp.playerLv <= playerCourtesyObj.playerLv + 5) {
                    final Tuple<Integer, PlayerCourtesyObj> tuple = new Tuple();
                    tuple.left = temp.recommendCount;
                    tuple.right = temp;
                    if (playerCourtesyObj.first5s.contains(temp.playerId)) {
                        optionListFirst5.add(tuple);
                    }
                    optionList.add(tuple);
                    recommendSum += tuple.left;
                }
            }
            PlayerCourtesyObj targetCourtesyObj = null;
            if (playerCourtesyObj.first5s.size() > 5 && optionListFirst5.size() > 0) {
                targetCourtesyObj = this.getNegativelyByRecommendCount(optionListFirst5, recommendSum, playerCourtesyObj);
            }
            else {
                targetCourtesyObj = this.getNegativelyByRecommendCount(optionList, recommendSum, playerCourtesyObj);
            }
            if (targetCourtesyObj == null) {
                return;
            }
            final CourtesyEvent courtesyEvent = new CourtesyEvent();
            courtesyEvent.id = CourtesyEvent.atomicInteger.incrementAndGet();
            courtesyEvent.type = 1;
            courtesyEvent.playerId = targetCourtesyObj.playerId;
            final Player player = this.dataGetter.getPlayerDao().read(courtesyEvent.playerId);
            courtesyEvent.playerName = player.getPlayerName();
            courtesyEvent.playerPic = player.getPic();
            courtesyEvent.playerLv = player.getPlayerLv();
            courtesyEvent.eventId = targetCourtesyObj.newlyEventId;
            courtesyEvent.state = 1;
            playerCourtesyObj.needHandleList.add(0, courtesyEvent);
            playerCourtesyObj.cutNeedHandleList();
            final JsonDocument doc2 = new JsonDocument();
            doc2.startObject();
            doc2.createElement("liShangWangLai", true);
            doc2.endObject();
            Players.push(playerCourtesyObj.playerId, PushCommand.PUSH_UPDATE, doc2.toByte());
            if (playerCourtesyObj.first5s.size() < 5) {
                playerCourtesyObj.first5s.add(targetCourtesyObj.playerId);
            }
            final PlayerCourtesyObj playerCourtesyObj2 = targetCourtesyObj;
            ++playerCourtesyObj2.recommendCount;
            targetCourtesyObj.newlyEventId = 0;
            CourtesyManager.timeLog.debug(LogUtil.formatThreadLog("CourtesyManager", "getCourtesyEvent", 2, System.currentTimeMillis() - start, "playerId#" + playerCourtesyObj.playerId));
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("CourtesyManager.getCourtesyEvent catch Exception", e);
        }
    }
    
    private PlayerCourtesyObj getNegativelyByRecommendCount(final List<Tuple<Integer, PlayerCourtesyObj>> optionList, final long recommendSum, final PlayerCourtesyObj playerCourtesyObj) {
        try {
            if (optionList.size() == 0) {
                return null;
            }
            if (optionList.size() == 1) {
                return optionList.get(0).right;
            }
            PlayerCourtesyObj targetCourtesyObj = null;
            final double randPro = WebUtil.nextDouble();
            final long randSum = (long)(randPro * recommendSum);
            long cummuSum = 0L;
            final int N_1 = optionList.size() - 1;
            for (final Tuple<Integer, PlayerCourtesyObj> tuple : optionList) {
                final long tempCount = (recommendSum - tuple.left) / N_1;
                cummuSum += tempCount;
                if (cummuSum >= randSum) {
                    targetCourtesyObj = tuple.right;
                    break;
                }
            }
            if (targetCourtesyObj == null) {
                ErrorSceneLog.getInstance().appendErrorMsg("targetCourtesyObj is null. apponit the last one").appendPlayerId(playerCourtesyObj.playerId).append("randPro", randPro).append("recommendSum", recommendSum).append("randSum", randSum).append("N_1", N_1).appendClassName("CourtesyManager").appendMethodName("getCourtesyEvent").flush();
                targetCourtesyObj = optionList.get(optionList.size() - 1).right;
            }
            return targetCourtesyObj;
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("CourtesyManager.getNegativelyByRecommendCount catch Exception", e);
            return null;
        }
    }
    
    public PlayerCourtesyObj getPlayerCourtesyObj(final PlayerDto playerDto) {
        try {
            if (!this.validForceId(playerDto)) {
                return null;
            }
            final int forceId = playerDto.forceId;
            final ConcurrentHashMap<Integer, PlayerCourtesyObj> countrymap = this.PCOContainer.get(forceId);
            final PlayerCourtesyObj playerCourtesyObj = countrymap.get(playerDto.playerId);
            if (playerCourtesyObj == null) {
                ErrorSceneLog.getInstance().appendErrorMsg("playerCourtesyObj is null").appendPlayerId(playerDto.playerId).appendPlayerName(playerDto.playerName).appendClassName("CourtesyManager").appendMethodName("getPlayerCourtesyObj").flush();
                return null;
            }
            return playerCourtesyObj;
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("CourtesyManager.getNegativelyByRecommendCount catch Exception", e);
            return null;
        }
    }
}
