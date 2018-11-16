package com.reign.gcld.world.common;

import org.springframework.stereotype.*;
import com.reign.gcld.common.log.*;
import com.reign.gcld.battle.domain.*;
import com.reign.gcld.task.reward.*;
import com.reign.gcld.event.util.*;
import com.reign.gcld.player.dto.*;
import com.reign.gcld.world.service.*;
import com.reign.gcld.world.domain.*;
import com.reign.gcld.log.*;
import com.reign.gcld.battle.service.*;
import com.reign.gcld.common.util.*;
import com.reign.gcld.diamondshop.domain.*;
import com.reign.gcld.player.domain.*;
import com.reign.gcld.event.domain.*;
import com.reign.gcld.tech.domain.*;
import com.reign.util.*;
import com.reign.gcld.chat.common.*;
import com.reign.gcld.battle.scene.*;
import com.reign.gcld.chat.service.*;
import com.reign.framework.json.*;
import com.reign.framework.netty.util.*;
import com.reign.framework.netty.servlet.*;
import com.reign.gcld.general.domain.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.gcld.battle.common.*;
import com.reign.gcld.common.*;
import com.reign.gcld.slave.domain.*;
import java.io.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;
import java.util.concurrent.*;
import java.lang.reflect.*;
import sun.misc.*;
import java.util.*;
import java.util.concurrent.locks.*;

@Component("cityEventManager")
public class CityEventManager
{
    private static final Logger timeLog;
    private static final Logger errorLog;
    public Map<Integer, Long> groupTimeStamp;
    public Map<Integer, CityAttribute> cityAttributeMap;
    public ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, PlayerEventObj>> playerEventMap;
    private static final CityEventManager instance;
    IDataGetter dataGetter;
    public static final int PLAYER_EVENT_TYPE_GEM = 1;
    public static final int PLAYER_EVENT_TYPE_FREE_PHANTOM = 2;
    public static final int PLAYER_EVENT_TYPE_STONE_SRC = 3;
    public static final int PLAYER_EVENT_TYPE_NATIONAL_TREASURE = 4;
    public static final int PLAYER_EVENT_TYPE_WORLD_SCENARIO = 5;
    public static final int PLAYER_EVENT_TYPE_TRAINNING_TOKEN = 6;
    public static final int PLAYER_EVENT_TYPE_SDLR = 7;
    public static final int PLAYER_EVENT_TYPE_SLAVE = 8;
    public static final int SLAVE_NUM_EACH_DAY = 40;
    
    static {
        timeLog = new TimerLogger();
        errorLog = CommonLog.getLog(CityEventManager.class);
        instance = new CityEventManager();
    }
    
    private CityEventManager() {
        this.groupTimeStamp = new HashMap<Integer, Long>();
        this.cityAttributeMap = new ConcurrentHashMap<Integer, CityAttribute>();
        this.playerEventMap = new ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, PlayerEventObj>>();
        this.dataGetter = null;
    }
    
    public static CityEventManager getInstance() {
        return CityEventManager.instance;
    }
    
    public boolean isInPlayerEventTimeWindow(int playerEventHourStart, int playerEventHourEnd) {
        if (playerEventHourStart < 0) {
            playerEventHourStart = 0;
        }
        if (playerEventHourEnd > 24) {
            playerEventHourEnd = 24;
        }
        final Calendar calendar1 = Calendar.getInstance();
        final Calendar calendar2 = Calendar.getInstance();
        calendar1.setTime(new Date());
        calendar2.setTime(new Date());
        calendar1.set(11, playerEventHourStart);
        calendar2.set(11, playerEventHourEnd);
        final long now = System.currentTimeMillis();
        return now >= calendar1.getTimeInMillis() && now <= calendar2.getTimeInMillis();
    }
    
    public void initCityEventManager(final IDataGetter dataGetter) {
        try {
            this.dataGetter = dataGetter;
            final Map<Integer, Map<Integer, ArrayList<WdSjIn>>> groupCountryMap = dataGetter.getWdSjInCache().getGroupCountryMap();
            for (final Integer groupId : groupCountryMap.keySet()) {
                this.groupTimeStamp.put(groupId, System.currentTimeMillis());
            }
            for (final Integer eventType : dataGetter.getWdSjpCache().getCacheMap().keySet()) {
                final Tuple<Integer, Integer> tuple = dataGetter.getWdSjpCache().timeWindowtMap.get(eventType);
                if (this.isInPlayerEventTimeWindow(tuple.left, tuple.right)) {
                    this.addFirstRoundPlayerEvent(eventType);
                }
            }
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("initCityEventManager catch Exception", e);
        }
    }
    
    public Tuple<List<Map<Integer, Integer>>, List<Map<Integer, Integer>>> getPlayerEventCost(final int playerId, final int eventType, final PlayerEventObj playerEventObj) {
        final PlayerBattleAttribute pba = this.dataGetter.getPlayerBattleAttributeDao().read(playerId);
        final Tuple<List<Map<Integer, Integer>>, List<Map<Integer, Integer>>> tuple = new Tuple();
        final List<Map<Integer, Integer>> result = new LinkedList<Map<Integer, Integer>>();
        int consumeType = 0;
        int consumeNum = 0;
        int gainType = 0;
        int gainNum = 0;
        final Map<Integer, Integer> consumeMap = new HashMap<Integer, Integer>();
        final Map<Integer, Integer> consumeMap2 = new HashMap<Integer, Integer>();
        final Map<Integer, Integer> gainMap = new HashMap<Integer, Integer>();
        final Map<Integer, Integer> gainMap2 = new HashMap<Integer, Integer>();
        if (eventType == 1) {
            consumeType = 4;
            final int consumeEach = this.dataGetter.getWdSjpGemCache().getIronCost(pba.getEventGemCount());
            consumeNum = consumeEach * playerEventObj.gemBaoJiCount;
            consumeMap.put(consumeType, consumeNum);
            result.add(consumeMap);
            gainType = 7;
            gainNum = playerEventObj.gemBaoJiCount;
            gainMap.put(gainType, gainNum);
            result.add(gainMap);
            tuple.left = result;
            return tuple;
        }
        if (eventType == 2) {
            final WdSjpHy wdSjpHy = (WdSjpHy)this.dataGetter.getWdSjpHyCache().get((Object)playerEventObj.eventId);
            consumeType = 1;
            consumeNum = wdSjpHy.getConsumeCopper();
            if (consumeNum > 0) {
                consumeMap.put(consumeType, consumeNum);
            }
            consumeType = 2;
            consumeNum = wdSjpHy.getConsumeLumber();
            if (consumeNum > 0) {
                consumeMap.put(consumeType, consumeNum);
            }
            consumeType = 3;
            consumeNum = wdSjpHy.getConsumeFood();
            if (consumeNum > 0) {
                consumeMap.put(consumeType, consumeNum);
            }
            result.add(consumeMap);
            gainType = 24;
            gainNum = wdSjpHy.getHyNum();
            gainMap.put(gainType, gainNum);
            result.add(gainMap);
            tuple.left = result;
            return tuple;
        }
        if (eventType == 3) {
            final WdSjpXtys wdSjpXtys = (WdSjpXtys)this.dataGetter.getWdSjpXtysCache().get((Object)playerEventObj.stoneSrcEventId);
            consumeType = 1;
            consumeNum = wdSjpXtys.getConsumeCopper();
            if (consumeNum > 0) {
                consumeMap.put(consumeType, consumeNum);
            }
            consumeType = 2;
            consumeNum = wdSjpXtys.getConsumeLumber();
            if (consumeNum > 0) {
                consumeMap.put(consumeType, consumeNum);
            }
            consumeType = 3;
            consumeNum = wdSjpXtys.getConsumeFood();
            if (consumeNum > 0) {
                consumeMap.put(consumeType, consumeNum);
            }
            result.add(consumeMap);
            gainType = 29;
            gainNum = wdSjpXtys.getXtysNum();
            gainMap.put(gainType, gainNum);
            result.add(gainMap);
            tuple.left = result;
            return tuple;
        }
        if (eventType == 4) {
            final FstNdEvent fstNdEvent = (FstNdEvent)this.dataGetter.getFstNdEventCache().get((Object)playerEventObj.nationTreasureEventId);
            final Tuple<Integer, Integer> tupleTemp = this.getCostTypeAndNum(fstNdEvent.getCost1());
            consumeMap.put(tupleTemp.left, tupleTemp.right);
            result.add(consumeMap);
            final ITaskReward itrv = TaskRewardFactory.getInstance().getTaskReward(fstNdEvent.getReward1());
            final Player player = this.dataGetter.getPlayerDao().read(playerId);
            final double rate = this.dataGetter.getFstDbLveCache().getRate(player.getPlayerLv());
            final CityEventRate cer = new CityEventRate();
            cer.rate = rate;
            final Map<Integer, Reward> rMap = itrv.getReward(PlayerDtoUtil.getPlayerDto(player, this.dataGetter.getPlayerAttributeDao().read(playerId)), this.dataGetter, cer);
            for (final Map.Entry<Integer, Reward> entry : rMap.entrySet()) {
                gainMap.put(entry.getKey(), entry.getValue().getNum());
            }
            result.add(gainMap);
            tuple.left = result;
            final List<Map<Integer, Integer>> result2 = new LinkedList<Map<Integer, Integer>>();
            final Tuple<Integer, Integer> tupleTemp2 = this.getCostTypeAndNum(fstNdEvent.getCost2());
            consumeMap2.put(tupleTemp2.left, tupleTemp2.right);
            result2.add(consumeMap2);
            final ITaskReward itrv2 = TaskRewardFactory.getInstance().getTaskReward(fstNdEvent.getReward2());
            final Map<Integer, Reward> rMap2 = itrv2.getReward(PlayerDtoUtil.getPlayerDto(player, this.dataGetter.getPlayerAttributeDao().read(playerId)), this.dataGetter, cer);
            for (final Map.Entry<Integer, Reward> entry2 : rMap2.entrySet()) {
                gainMap2.put(entry2.getKey(), entry2.getValue().getNum());
            }
            result2.add(gainMap2);
            tuple.right = result2;
            return tuple;
        }
        if (eventType == 5) {
            return tuple;
        }
        if (eventType == 6) {
            final WdSjpLbl wdSjpLbl = (WdSjpLbl)this.dataGetter.getWdSjpLblCache().get((Object)playerEventObj.stoneSrcEventId);
            result.add(consumeMap);
            gainType = 43;
            gainNum = wdSjpLbl.getNum();
            gainMap.put(gainType, gainNum);
            result.add(gainMap);
            tuple.left = result;
            return tuple;
        }
        if (eventType == 7) {
            final WdSjpSdlr wdsjpSdlr = (WdSjpSdlr)this.dataGetter.getWdSjpSdlrCache().get((Object)playerEventObj.eventId);
            final int type = wdsjpSdlr.getType();
            if (1 == type) {
                gainType = 35;
            }
            else if (2 == type) {
                gainType = 36;
            }
            else if (3 == type) {
                gainType = 37;
            }
            gainNum = wdsjpSdlr.getNum();
            gainMap.put(gainType, gainNum);
            result.add(consumeMap);
            result.add(gainMap);
            tuple.left = result;
            return tuple;
        }
        if (eventType == 8) {
            result.add(consumeMap);
            gainType = 44;
            gainNum = 1;
            gainMap.put(gainType, gainNum);
            result.add(gainMap);
            tuple.left = result;
            return tuple;
        }
        return null;
    }
    
    public void addFirstRoundPlayerEvent(final int eventType) {
        List<Integer> pIdList = null;
        switch (eventType) {
            case 1: {
                pIdList = this.dataGetter.getPlayerTechDao().getPlayerIdListByKey(46);
                break;
            }
            case 2: {
                pIdList = this.dataGetter.getPlayerTechDao().getPlayerIdListByKey(47);
                break;
            }
            case 3: {
                pIdList = this.dataGetter.getPlayerBlacksmithDao().getPlayerIdListBySmithId(4);
                break;
            }
            case 4: {
                if (!EventUtil.isEventTime(11)) {
                    return;
                }
                pIdList = this.dataGetter.getPlayerEventDao().getPlayerIdListByEventId(11);
                break;
            }
            case 5: {
                final List<Integer> techIdList = this.dataGetter.getWdSjpDramaCache().getDramaOpenList();
                pIdList = this.dataGetter.getPlayerTechDao().getPlayerIdListByFirstWorldDramaKey(techIdList);
                break;
            }
            case 6: {
                final Collection<PlayerDto> list = Players.getAllPlayer();
                pIdList = new ArrayList<Integer>();
                if (list != null) {
                    for (final PlayerDto playerDto : list) {
                        if (playerDto.playerLv >= 30) {
                            pIdList.add(playerDto.playerId);
                        }
                    }
                    break;
                }
                break;
            }
            case 7: {
                if (!EventUtil.isEventTime(16)) {
                    return;
                }
                pIdList = this.dataGetter.getPlayerEventDao().getPlayerIdListByEventId(16);
                break;
            }
            default: {
                ErrorSceneLog.getInstance().appendErrorMsg("eventType is invalid").append("eventType", eventType).appendMethodName("addFirstRoundPlayerEvent").appendClassName("CityEventManager").flush();
                return;
            }
        }
        for (final Integer playerId : pIdList) {
            this.addPlayerEvent(playerId, eventType);
        }
    }
    
    public void removePlayerEventAfterConquerCityCheck(final int cityId) {
        try {
            final City city = CityDataCache.cityArray[cityId];
            final int nowForceId = city.getForceId();
            for (final Map.Entry<Integer, ConcurrentHashMap<Integer, PlayerEventObj>> entry1 : this.playerEventMap.entrySet()) {
                final int playerId = entry1.getKey();
                final Player player = this.dataGetter.getPlayerDao().read(playerId);
                if (nowForceId == player.getForceId()) {
                    continue;
                }
                int eventType = 0;
                for (final Map.Entry<Integer, PlayerEventObj> entry2 : entry1.getValue().entrySet()) {
                    if (entry2.getValue().cityId == cityId) {
                        eventType = entry2.getKey();
                        break;
                    }
                }
                if (eventType <= 0) {
                    continue;
                }
                this.playerEventMap.get(playerId).remove(eventType);
                final boolean add = this.addPlayerEvent(playerId, eventType);
                if (!add) {
                    continue;
                }
                this.bobaoOnePlayerEvent(playerId, eventType);
            }
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("CityEventManager.removePlayerEventAfterConquerCityCheck Exception. cityId:" + cityId, e);
        }
    }
    
    public void removePlayerEventByEventType(final int eventType) {
        try {
            for (final Map.Entry<Integer, ConcurrentHashMap<Integer, PlayerEventObj>> entry1 : this.playerEventMap.entrySet()) {
                final int playerId = entry1.getKey();
                boolean flag = false;
                for (final Map.Entry<Integer, PlayerEventObj> entry2 : entry1.getValue().entrySet()) {
                    if (entry2.getKey() == eventType) {
                        flag = true;
                        break;
                    }
                }
                if (flag) {
                    this.playerEventMap.get(playerId).remove(eventType);
                    this.bobaoOnePlayerEvent(playerId, eventType);
                }
            }
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("class:CityEventManager#method:removePlayerEventByEventType#eventType:" + eventType, e);
        }
    }
    
    public void removePlayerEventAfterWorkShopBuild(final int playerId) {
        try {
            final ConcurrentHashMap<Integer, PlayerEventObj> map = this.playerEventMap.get(playerId);
            if (map != null) {
                final PlayerEventObj playerEventObj = map.remove(2);
                if (playerEventObj != null) {
                    this.pushPlayerEventInfo(playerId, 2);
                }
            }
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("CityEventManager.removePlayerEventAfterWorkShopBuild Exception. playerId:" + playerId, e);
        }
    }
    
    public void removePlayerEventAfterDiamondShopBuild(final int playerId) {
        try {
            final ConcurrentHashMap<Integer, PlayerEventObj> map = this.playerEventMap.get(playerId);
            if (map != null) {
                final PlayerEventObj playerEventObj = map.remove(1);
                if (playerEventObj != null) {
                    this.pushPlayerEventInfo(playerId, 1);
                }
            }
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("CityEventManager.removePlayerEventAfterDiamondShopBuild Exception. playerId:" + playerId, e);
        }
    }
    
    public boolean addPlayerEvent(final int playerId, final int eventType) {
        try {
            final Player player = this.dataGetter.getPlayerDao().read(playerId);
            final int forceId = player.getForceId();
            final WdSjp wdSjp = (WdSjp)this.dataGetter.getWdSjpCache().get((Object)eventType);
            if (wdSjp == null) {
                ErrorSceneLog.getInstance().appendErrorMsg("wdSjp is null").append("eventType", eventType).append("playerId", playerId).appendMethodName("addPlayerEvent").appendClassName("CityEventManager").flush();
                return false;
            }
            final PlayerBattleAttribute pba = this.dataGetter.getPlayerBattleAttributeDao().read(playerId);
            int countToday = 0;
            int NumLimit = 0;
            switch (eventType) {
                case 1: {
                    final PlayerDiamondShop pds = this.dataGetter.getPlayerDiamondShopDao().getMaxShop(playerId);
                    if (pds != null) {
                        return false;
                    }
                    countToday = pba.getEventGemCountToday();
                    NumLimit = wdSjp.getNumMax();
                    break;
                }
                case 2: {
                    if (pba.getPhantomWorkShopLv() > 0) {
                        return false;
                    }
                    countToday = pba.getEventJiebingCountToday();
                    NumLimit = this.dataGetter.getTechEffectCache().getTechEffect(playerId, 47);
                    break;
                }
                case 3: {
                    countToday = pba.getEventXtysCountToday();
                    NumLimit = wdSjp.getNumMax();
                    break;
                }
                case 4: {
                    countToday = pba.getEventNationalTreasureCountToday();
                    NumLimit = wdSjp.getNumMax();
                    break;
                }
                case 5: {
                    countToday = pba.getEventWorldDramaCountToday();
                    NumLimit = wdSjp.getNumMax();
                    break;
                }
                case 6: {
                    if (player.getPlayerLv() < 30) {
                        CityEventManager.timeLog.info(LogUtil.formatThreadLog("CityEventManager", "addPlayerEvent", 1, 0L, "playerId:" + playerId + ",forceId:" + forceId + ",eventType:" + eventType + ". world farm function not open, ignore"));
                        return false;
                    }
                    countToday = pba.getEventTrainningTokenCountToday();
                    NumLimit = wdSjp.getNumMax();
                    break;
                }
                case 7: {
                    countToday = pba.getEventSdlrCountToday();
                    NumLimit = wdSjp.getNumMax();
                    break;
                }
                case 8: {
                    countToday = pba.getEventSlaveCountToday();
                    NumLimit = wdSjp.getNumMax();
                    break;
                }
                default: {
                    return false;
                }
            }
            final ErrorSceneLog errorSceneLog = ErrorSceneLog.getInstance();
            if (countToday >= NumLimit) {
                if (eventType == 5) {
                    errorSceneLog.error("addPlayerEvent skipped. today num full.playerId:" + playerId + "eventType:" + eventType);
                }
                return false;
            }
            ConcurrentHashMap<Integer, PlayerEventObj> map = this.playerEventMap.get(playerId);
            if (map == null) {
                map = new ConcurrentHashMap<Integer, PlayerEventObj>();
                this.playerEventMap.put(playerId, map);
            }
            final Integer capitalId = WorldCityCommon.nationMainCityIdMap.get(forceId);
            if (capitalId == null) {
                ErrorSceneLog.getInstance().appendErrorMsg("capitalId is null").append("forceId", forceId).append("playerId", playerId).appendMethodName("addPlayerEvent").appendClassName("CityEventManager").flush();
                return false;
            }
            final int breadth = wdSjp.getDis();
            final List<Integer> cityIdSet = this.dataGetter.getCityDataCache().getBFSCityOrderListByBreadth(capitalId, breadth);
            Collections.shuffle(cityIdSet);
            Integer targetCityId = null;
            for (final Integer cityId : cityIdSet) {
                final City city = CityDataCache.cityArray[cityId];
                if (city == null) {
                    CityEventManager.errorLog.error("addPlayerEvent  CityDataCache.cityArray[cityId]" + cityId);
                }
                else {
                    if (city.getForceId() != forceId) {
                        continue;
                    }
                    if (NewBattleManager.getInstance().getBattleByDefId(3, cityId) != null) {
                        continue;
                    }
                    if (WorldFarmCache.forceCityIdMap.get(forceId) == cityId) {
                        continue;
                    }
                    final CityAttribute cityAttribute = getInstance().cityAttributeMap.get(cityId);
                    if (cityAttribute != null) {
                        final PlayerAttribute pa = this.dataGetter.getPlayerAttributeDao().read(playerId);
                        final char[] cs = pa.getFunctionId().toCharArray();
                        if (cs[61] == '1') {
                            if (cityAttribute.countDown == -1L && cityAttribute.leftCount >= 0) {
                                continue;
                            }
                            if (System.currentTimeMillis() < cityAttribute.countDown) {
                                continue;
                            }
                            final Integer myCount = cityAttribute.playerIdCountMap.get(playerId);
                            if (myCount == null) {
                                continue;
                            }
                            if (myCount < cityAttribute.eachLimit) {
                                continue;
                            }
                            if (cityAttribute.viewForceId == 0) {
                                continue;
                            }
                            if (cityAttribute.viewForceId == player.getForceId()) {
                                continue;
                            }
                        }
                    }
                    boolean alreadyHas = false;
                    for (final Map.Entry<Integer, PlayerEventObj> entry : this.playerEventMap.get(playerId).entrySet()) {
                        if (entry.getValue().cityId == cityId) {
                            alreadyHas = true;
                            break;
                        }
                    }
                    if (alreadyHas) {
                        continue;
                    }
                    targetCityId = cityId;
                    break;
                }
            }
            if (targetCityId == null) {
                return false;
            }
            final PlayerEventObj playerEventObj = new PlayerEventObj();
            playerEventObj.cityId = targetCityId;
            if (eventType == 2) {
                playerEventObj.eventId = this.dataGetter.getWdSjpHyCache().getRandWdSjpHy().getId();
            }
            else if (eventType == 1) {
                final Integer baoJiCount = this.dataGetter.getWdSjpGemCache().getBaoJiCount(pba.getEventGemCount());
                if (baoJiCount != null) {
                    playerEventObj.gemBaoJiCount = baoJiCount;
                }
                else {
                    playerEventObj.gemBaoJiCount = 1;
                    ErrorSceneLog.getInstance().appendErrorMsg("baoJiCount is null. set as WdSjpGemCache.BAO_JI_COUNT_1").appendPlayerId(player.getPlayerId()).appendPlayerName(player.getPlayerName()).append("buyCount", pba.getEventGemCount()).appendMethodName("addPlayerEvent").appendClassName("CityEventManager").flush();
                }
            }
            else if (eventType == 3) {
                playerEventObj.stoneSrcEventId = this.dataGetter.getWdSjpXtysCache().getWdSjpXtys().getId();
            }
            else if (eventType == 4) {
                final PlayerEvent pe = this.dataGetter.getPlayerEventDao().getPlayerEvent(playerId, 11);
                if (pe == null) {
                    ErrorSceneLog.getInstance().appendErrorMsg("playerEvent record is null").appendPlayerId(player.getPlayerId()).appendPlayerName(player.getPlayerName()).appendMethodName("addPlayerEvent").appendClassName("CityEventManager").flush();
                    return false;
                }
                playerEventObj.nationTreasureEventId = pe.getParam8() + 1;
            }
            else if (eventType == 5) {
                final List<Integer> canSelectList = this.getCanSelectWorldDramaList(player);
                if (canSelectList == null || canSelectList.isEmpty()) {
                    errorSceneLog.error("addPlayerEvent.can select is null.." + playerId + "eventType:" + eventType);
                    return false;
                }
                final int randId = WebUtil.nextInt(canSelectList.size());
                final int dramaId = canSelectList.get(randId);
                playerEventObj.worldDramaId = dramaId;
            }
            else if (eventType == 6) {
                playerEventObj.stoneSrcEventId = this.dataGetter.getWdSjpLblCache().getWdSjpXtys().getId();
            }
            else if (eventType == 7) {
                playerEventObj.eventId = this.dataGetter.getWdSjpSdlrCache().getWdSjpSdlr().getId();
            }
            map.put(eventType, playerEventObj);
            this.pushPlayerEventInfo(playerId, eventType);
            return true;
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("CityEventManager.addPlayerEvent Exception", e);
            return false;
        }
    }
    
    private List<Integer> getCanSelectWorldDramaList(final Player player) {
        try {
            final List<WdSjpDrama> dramas = this.dataGetter.getWdSjpDramaCache().getDramaListByLv(player.getPlayerLv());
            if (dramas == null) {
                return null;
            }
            final WorldDramaTimesCache cache = WorldDramaTimesCache.getInstatnce();
            List<Integer> result = null;
            for (final WdSjpDrama drama : dramas) {
                final int times = cache.getTimesByPIDAndSIdAndGrade(player.getPlayerId(), drama.getDramaId(), drama.getDifficulty());
                if (times >= drama.getNumMax()) {
                    continue;
                }
                final int techId = drama.getOpenTech();
                final PlayerTech tech = this.dataGetter.getPlayerTechDao().getPlayerTech(player.getPlayerId(), techId);
                if (tech == null) {
                    continue;
                }
                if (tech.getStatus() != 5) {
                    continue;
                }
                if (result == null) {
                    result = new ArrayList<Integer>();
                }
                result.add(drama.getId());
            }
            return result;
        }
        catch (Exception e) {
            final ErrorSceneLog errorSceneLog = ErrorSceneLog.getInstance();
            errorSceneLog.error(this, e);
            return null;
        }
    }
    
    public void pushPlayerEventInfo(final int playerId, final int eventType) {
        try {
            final JsonDocument doc = new JsonDocument();
            doc.startObject();
            doc.startArray("playerEvents");
            for (final Map.Entry<Integer, PlayerEventObj> pEObj : this.playerEventMap.get(playerId).entrySet()) {
                if (pEObj.getKey() == eventType) {
                    final City city = this.dataGetter.getCityDao().read(pEObj.getValue().cityId);
                    this.dataGetter.getCityService().getPlayerEventInfo(doc, city, playerId);
                }
            }
            doc.endArray();
            doc.endObject();
            Players.push(playerId, PushCommand.PUSH_PLAYER_EVENT, doc.toByte());
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("CityEventManager.pushPlayerEventInfo Exception", e);
        }
    }
    
    public void bobaoAllPlayerEvent() {
        try {
            for (final Map.Entry<Integer, ConcurrentHashMap<Integer, PlayerEventObj>> entry1 : this.playerEventMap.entrySet()) {
                final int playerId = entry1.getKey();
                for (final Integer eventType : entry1.getValue().keySet()) {
                    this.bobaoOnePlayerEvent(playerId, eventType);
                }
            }
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("CityEventManager.bobaoAllPlayerEvent Exception", e);
        }
    }
    
    public void bobaoOnePlayerEvent(final int playerId, final int eventType) {
        try {
            if (this.playerEventMap.get(playerId) == null) {
                final Player player = this.dataGetter.getPlayerDao().read(playerId);
                ErrorSceneLog.getInstance().appendErrorMsg("bobaoOnePlayerEvent first step is null").append("playerId", playerId).append("playerName", player.getPlayerName()).flush();
                return;
            }
            if (this.playerEventMap.get(playerId).get(eventType) == null) {
                return;
            }
            final int cityId = this.playerEventMap.get(playerId).get(eventType).cityId;
            final String cityName = ((WorldCity)this.dataGetter.getWorldCityCache().get((Object)cityId)).getName();
            String bobaoFormat = null;
            switch (eventType) {
                case 1: {
                    bobaoFormat = LocalMessages.PLAYER_EVENT_GEM_MERCHANT_BOBAO_FORMAT;
                    break;
                }
                case 2: {
                    bobaoFormat = LocalMessages.PLAYER_EVENT_FREE_JIEBING_BOBAO_FORMAT;
                    break;
                }
                case 3: {
                    bobaoFormat = LocalMessages.PLAYER_EVENT_XTYS_BOBAO_FORMAT;
                    break;
                }
                case 4: {
                    bobaoFormat = LocalMessages.PLAYER_EVENT_NATIONAL_TREASURE_BOBAO_FORMAT;
                    break;
                }
                case 5: {
                    bobaoFormat = LocalMessages.PLAYER_EVENT_WORLD_DRAMA_BOBAO_FORMAT;
                    break;
                }
                case 6: {
                    bobaoFormat = LocalMessages.PLAYER_EVENT_TRAINNING_TOKEN_BOBAO_FORMAT;
                    break;
                }
                case 7: {
                    bobaoFormat = LocalMessages.PLAYER_EVENT_SDLR_BOBAO_FORMAT;
                    break;
                }
                default: {
                    return;
                }
            }
            if (eventType == 6) {
                final PlayerAttribute pa = this.dataGetter.getPlayerAttributeDao().read(playerId);
                if (pa != null) {
                    final char[] cs = pa.getFunctionId().toCharArray();
                    if (cs[10] != '1') {
                        return;
                    }
                }
            }
            final String bobaoMsg = MessageFormatter.format(bobaoFormat, new Object[] { cityName });
            this.dataGetter.getChatService().sendSystemChat("SYS2ONE", playerId, 0, bobaoMsg, new ChatLink(2, new StringBuilder(String.valueOf(cityId)).toString()));
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("CityEventManager.bobaoPlayerEvent Exception", e);
        }
    }
    
    public void addCityEventEachMinute() {
        try {
            this.doClearCityEventByGroup();
            for (final Map.Entry<Integer, Long> entry : this.groupTimeStamp.entrySet()) {
                final Integer group = entry.getKey();
                final Long time = entry.getValue();
                final long now = System.currentTimeMillis();
                if (now >= time) {
                    this.doClearCityEventByGroup(group);
                    this.doAddCityEvent(group);
                    final Calendar todayStart = Calendar.getInstance();
                    todayStart.setTime(new Date(now));
                    todayStart.set(6, todayStart.get(6));
                    todayStart.set(11, 0);
                    todayStart.set(12, 0);
                    todayStart.set(13, 0);
                    final long todayStartTime = todayStart.getTime().getTime();
                    int bitId = (int)((now - todayStartTime) / 3600000L) + 1;
                    if (bitId < 1) {
                        bitId = 1;
                    }
                    if (bitId > 24) {
                        bitId = 24;
                    }
                    final WdSjFe wdSjFe = (WdSjFe)this.dataGetter.getWdSjFeCache().get((Object)bitId);
                    if (wdSjFe == null) {
                        ErrorSceneLog.getInstance().appendErrorMsg("wdSjFe is null").append("bitId", bitId).append("now", now).append("todayStart", todayStart.toString()).append("todayStartTime", todayStartTime).appendMethodName("getFreeCityWithDistanceSet").appendClassName("CityEventManager").flush();
                    }
                    else {
                        final Tuple<Integer, Integer> timeWindow = this.dataGetter.getWdSjInCache().getTimeWindowByGroup(group);
                        int minutes = timeWindow.left + WebUtil.nextInt(timeWindow.right - timeWindow.left);
                        minutes *= (int)(Object)wdSjFe.getE();
                        final long nextTime = now + minutes * 60000L;
                        this.groupTimeStamp.put(group, nextTime);
                    }
                }
            }
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("addCityEventEachMinute catch Exception.", e);
        }
    }
    
    private void doClearCityEventByGroup() {
        final Set<Integer> keySet = new HashSet<Integer>();
        for (final Integer cityId : this.cityAttributeMap.keySet()) {
            keySet.add(cityId);
        }
        for (final Integer cityId : keySet) {
            final Battle cityEventBattle = NewBattleManager.getInstance().getBattleByDefId(17, cityId);
            if (cityEventBattle != null) {
                continue;
            }
            final CityAttribute cityAttribute = this.cityAttributeMap.get(cityId);
            if (cityAttribute.countDown != -1L) {
                if (System.currentTimeMillis() <= cityAttribute.countDown) {
                    continue;
                }
                this.cityAttributeMap.remove(cityId);
                this.pushCityEventChangeInfo(cityId);
            }
            else {
                if (cityAttribute.leftCount > 0) {
                    continue;
                }
                this.cityAttributeMap.remove(cityId);
                this.pushCityEventChangeInfo(cityId);
            }
        }
    }
    
    private void doClearCityEventByGroup(final int group) {
        final Set<Integer> keySet = new HashSet<Integer>();
        for (final Integer cityId : this.cityAttributeMap.keySet()) {
            keySet.add(cityId);
        }
        for (final Integer cityId : keySet) {
            final Battle cityEventBattle = NewBattleManager.getInstance().getBattleByDefId(17, cityId);
            if (cityEventBattle != null) {
                continue;
            }
            final CityAttribute cityAttribute = this.cityAttributeMap.get(cityId);
            if (cityAttribute.group != group || cityAttribute.countDown != -1L) {
                continue;
            }
            this.cityAttributeMap.remove(cityId);
            this.pushCityEventChangeInfo(cityId);
        }
    }
    
    private WorldCity getFreeCityWithDistanceSet(final Integer country, final List<WorldCity> distanceSet, final Set<Integer> inBattleCitySet) {
        WorldCity result = null;
        final List<WorldCity> avaliableCityList = new ArrayList<WorldCity>();
        for (final WorldCity worldCity : distanceSet) {
            if (WorldCityCommon.mainCityNationIdMap.get(worldCity.getId()) != null) {
                continue;
            }
            final int cityId = worldCity.getId();
            if (cityId == 253 || cityId == 254) {
                continue;
            }
            if (cityId == 206) {
                continue;
            }
            if (WorldCityCommon.barbarainCitySet.contains(worldCity.getId())) {
                continue;
            }
            if (inBattleCitySet.contains(worldCity.getId())) {
                continue;
            }
            final City city = this.dataGetter.getCityDao().read(worldCity.getId());
            if (country != 0 && city.getForceId() != country) {
                continue;
            }
            if (city.getTitle() == 1) {
                continue;
            }
            if (city.getTitle() == 2) {
                continue;
            }
            if (this.cityAttributeMap.get(worldCity.getId()) != null) {
                continue;
            }
            avaliableCityList.add(worldCity);
        }
        final int size = avaliableCityList.size();
        if (size > 0) {
            final int index = WebUtil.nextInt(size);
            result = avaliableCityList.get(index);
        }
        else {
            final StringBuilder distanceSetSB = new StringBuilder();
            for (final WorldCity wc : distanceSet) {
                distanceSetSB.append(wc.getId() + ":" + wc.getName()).append("|");
            }
            final StringBuilder inBattleCitySetSB = new StringBuilder();
            for (final Integer tempId : inBattleCitySet) {
                inBattleCitySetSB.append(tempId).append("|");
            }
        }
        return result;
    }
    
    private void doAddCityEvent(final Integer group) {
        try {
            final long start = System.currentTimeMillis();
            CityEventManager.timeLog.info("CityEventManager.doAddCityEvent start. group:" + group);
            final List<WorldCity> worldCityList = this.dataGetter.getWorldCityCache().getWorldCityList();
            final Set<Integer> inBattleCitySet = NewBattleManager.getInstance().getInBattleCitySet();
            final Map<Integer, ArrayList<WdSjIn>> countryMap = this.dataGetter.getWdSjInCache().getWdSjInMapByGroup(group);
            for (final Map.Entry<Integer, ArrayList<WdSjIn>> entry : countryMap.entrySet()) {
                final ArrayList<WdSjIn> list = entry.getValue();
                for (final WdSjIn wdSjIn : list) {
                    this.doAddCityEventForOneWdSjIn(wdSjIn, worldCityList, inBattleCitySet, group);
                }
            }
            final long end = System.currentTimeMillis();
            CityEventManager.timeLog.info("CityEventManager.doAddCityEvent end. group:" + group + "time cost:" + (end - start));
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("doAddCityEvent catch Exception.", e);
        }
    }
    
    private void doAddCityEventForOneWdSjIn(final WdSjIn wdSjIn, final List<WorldCity> worldCityList, final Set<Integer> inBattleCitySet, final Integer group) {
        try {
            WorldCity targetWorldCity = null;
            List<WorldCity> citySet = null;
            final int c = wdSjIn.getC();
            final int d = wdSjIn.getD();
            if (c > 0) {
                if (d > 0) {
                    citySet = this.dataGetter.getWorldCityCache().getCitySetByCountryAndLessThanDistance(c, d);
                }
                else {
                    citySet = worldCityList;
                }
            }
            else if (d > 0) {
                citySet = this.dataGetter.getWorldCityCache().getCitySetByLessThanDistance(d);
            }
            else {
                citySet = worldCityList;
            }
            if (citySet == null) {
                ErrorSceneLog.getInstance().appendErrorMsg("citySet is null. ignore this round").append("wdSjIn Id", wdSjIn.getId()).appendMethodName("doAddCityEventForOneWdSjIn").appendClassName("CityEventManager").flush();
                return;
            }
            targetWorldCity = this.getFreeCityWithDistanceSet(c, citySet, inBattleCitySet);
            if (targetWorldCity == null) {
                ErrorSceneLog.getInstance().appendErrorMsg("targetWorldCity is null. ignore this round").appendMethodName("doAddCityEventForOneWdSjIn").appendClassName("CityEventManager").flush();
                return;
            }
            final CityAttribute cityAttribute = new CityAttribute();
            cityAttribute.wdSjIn = wdSjIn;
            cityAttribute.targetWorldCity = targetWorldCity;
            int type = this.dataGetter.getWdSjSeCache().getRandomType();
            if (type == 0) {
                ErrorSceneLog.getInstance().appendErrorMsg("type is 0, change to 2").appendMethodName("doAddCityEventForOneWdSjIn").appendClassName("CityEventManager").flush();
                type = 2;
            }
            cityAttribute.eventType = type;
            cityAttribute.group = group;
            if (wdSjIn.getDu() == 0) {
                cityAttribute.countDown = -1L;
            }
            else if (wdSjIn.getDu() > 0) {
                cityAttribute.countDown = System.currentTimeMillis() + wdSjIn.getDu() * 60000L;
            }
            else {
                ErrorSceneLog.getInstance().appendErrorMsg("wdSjIn.getDu() is negative").append("wdSjIn", wdSjIn.getId()).append("wdSjIn.getDu()", wdSjIn.getDu()).appendMethodName("doAddCityEventForOneWdSjIn").appendClassName("CityEventManager").flush();
                cityAttribute.countDown = -1L;
            }
            cityAttribute.leftCount = wdSjIn.getTn();
            if (cityAttribute.leftCount == 0) {
                cityAttribute.leftCount = Integer.MAX_VALUE;
            }
            cityAttribute.eachLimit = wdSjIn.getPn();
            cityAttribute.viewForceId = wdSjIn.getCv();
            cityAttribute.visiable = wdSjIn.getV();
            cityAttribute.rewadLv = wdSjIn.getRewardLv();
            final int terrainTypeValue = targetWorldCity.getTerrainEffectType();
            int eventTargetId = 0;
            if (cityAttribute.eventType == 1) {
                eventTargetId = this.dataGetter.getWdSjBoCache().getRandWdSjBoByRewardlvViewTerrain(cityAttribute.rewadLv, cityAttribute.visiable, terrainTypeValue).getId();
            }
            else if (cityAttribute.eventType == 2) {
                eventTargetId = this.dataGetter.getWdSjEvCache().getRandWdSjEvByRewardlvViewTerrain(cityAttribute.rewadLv, cityAttribute.visiable, terrainTypeValue).getId();
            }
            cityAttribute.eventTargetId = eventTargetId;
            final int cityId = targetWorldCity.getId();
            this.cityAttributeMap.put(cityId, cityAttribute);
            this.pushCityEventChangeInfo(cityId);
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("doAddCityEventForOneWdSjIn catch Exception.", e);
        }
    }
    
    public void removeCityEventAfterBesieged(final int cityId) {
        try {
            if (this.cityAttributeMap.get(cityId) != null) {
                this.cityAttributeMap.remove(cityId);
                this.pushCityEventChangeInfo(cityId);
            }
            this.dataGetter.getTimerBattleService().addBarbarainFoodArmyAfterBesiegedTry(cityId);
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("removeCityEventAfterBesieged ", e);
        }
    }
    
    public void pushCityEventChangeInfoDueToBattle(final int cityId) {
        try {
            final CityAttribute cityAttribute = this.cityAttributeMap.get(cityId);
            if (cityAttribute == null) {
                return;
            }
            final JsonDocument doc = new JsonDocument();
            doc.startObject();
            doc.createElement("cityId", cityId);
            doc.startObject("cityEvent");
            doc.createElement("dropType", 1);
            doc.createElement("eventType", 1);
            doc.createElement("eventCount", 0);
            doc.endObject();
            doc.endObject();
            final String groupId = ChatType.WORLD.toString();
            final Group worldG = GroupManager.getInstance().getGroup(groupId);
            if (worldG != null) {
                final byte[] bytes = JsonBuilder.getJson(JsonBuilder.getJson(State.PUSH, PushCommand.PUSH_CITY_EVENT.getModule(), doc.toByte()));
                worldG.notify((String)null, WrapperUtil.wrapper(PushCommand.PUSH_CITY_EVENT.getCommand(), 0, bytes));
            }
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("pushCityEventChangeInfoDueToBattle get Exception. cityId:" + cityId, e);
        }
    }
    
    public void pushCityEventChangeInfo(final int cityId) {
        try {
            final CityAttribute cityAttribute = this.cityAttributeMap.get(cityId);
            if (cityAttribute == null) {
                final JsonDocument doc = new JsonDocument();
                doc.startObject();
                doc.createElement("cityId", cityId);
                doc.startObject("cityEvent");
                doc.createElement("dropType", 1);
                doc.createElement("eventType", 1);
                doc.createElement("eventCount", 0);
                doc.endObject();
                doc.endObject();
                final String groupId = ChatType.WORLD.toString();
                final Group worldG = GroupManager.getInstance().getGroup(groupId);
                if (worldG != null) {
                    final byte[] bytes = JsonBuilder.getJson(JsonBuilder.getJson(State.PUSH, PushCommand.PUSH_CITY_EVENT.getModule(), doc.toByte()));
                    worldG.notify((String)null, WrapperUtil.wrapper(PushCommand.PUSH_CITY_EVENT.getCommand(), 0, bytes));
                }
                return;
            }
            final JsonDocument doc = new JsonDocument();
            doc.startObject();
            doc.createElement("cityId", cityId);
            doc.startObject("cityEvent");
            String dropPic = "0";
            switch (cityAttribute.eventType) {
                case 1: {
                    final WdSjBo wdSjBo = (WdSjBo)this.dataGetter.getWdSjBoCache().get((Object)cityAttribute.eventTargetId);
                    dropPic = wdSjBo.getPic();
                    break;
                }
                case 2: {
                    final WdSjEv wdSjEv = (WdSjEv)this.dataGetter.getWdSjEvCache().get((Object)cityAttribute.eventTargetId);
                    dropPic = wdSjEv.getPic();
                    break;
                }
            }
            doc.createElement("dropType", dropPic);
            if (cityAttribute.countDown == -1L) {
                doc.createElement("eventType", 1);
                doc.createElement("eventCount", cityAttribute.leftCount);
            }
            else {
                doc.createElement("eventType", 2);
                if (cityAttribute.leftCount == 0) {
                    doc.createElement("countDown", 0);
                }
                else {
                    doc.createElement("countDown", cityAttribute.countDown - System.currentTimeMillis());
                }
            }
            doc.endObject();
            doc.endObject();
            String groupId2 = null;
            switch (cityAttribute.viewForceId) {
                case 0: {
                    groupId2 = ChatType.WORLD.toString();
                    break;
                }
                case 1: {
                    groupId2 = ChatType.WORLD_1.toString();
                    break;
                }
                case 2: {
                    groupId2 = ChatType.WORLD_2.toString();
                    break;
                }
                case 3: {
                    groupId2 = ChatType.WORLD_3.toString();
                    break;
                }
                default: {
                    return;
                }
            }
            final Group worldG2 = GroupManager.getInstance().getGroup(groupId2);
            if (worldG2 != null) {
                final byte[] bytes2 = JsonBuilder.getJson(JsonBuilder.getJson(State.PUSH, PushCommand.PUSH_CITY_EVENT.getModule(), doc.toByte()));
                final Object content = WrapperUtil.wrapper(PushCommand.PUSH_CITY_EVENT.getCommand(), 0, bytes2);
                final GroupImpl gImpl = (GroupImpl)worldG2;
                for (final Map.Entry<String, Session> entry : gImpl.getUserMap().entrySet()) {
                    final String sessionId = entry.getKey();
                    final Session session = entry.getValue();
                    final PlayerDto playerDto = Players.getSession(sessionId);
                    if (playerDto == null) {
                        continue;
                    }
                    final PlayerAttribute pa = this.dataGetter.getPlayerAttributeDao().read(playerDto.playerId);
                    final char[] cs = pa.getFunctionId().toCharArray();
                    if (cs[61] != '1') {
                        continue;
                    }
                    final Integer gainCount = cityAttribute.playerIdCountMap.get(playerDto.playerId);
                    if (gainCount != null && gainCount >= cityAttribute.eachLimit) {
                        continue;
                    }
                    if (cityAttribute.visiable == 0) {
                        boolean hasPgmInThisCity = false;
                        final List<PlayerGeneralMilitary> pgmList = this.dataGetter.getPlayerGeneralMilitaryDao().getMilitaryList(playerDto.playerId);
                        for (final PlayerGeneralMilitary pgm : pgmList) {
                            if (pgm.getLocationId() == cityId) {
                                hasPgmInThisCity = true;
                                break;
                            }
                        }
                        if (!hasPgmInThisCity) {
                            continue;
                        }
                    }
                    session.write(content);
                }
            }
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("pushCityEventChangeInfo get Exception. cityId:" + cityId, e);
        }
    }
    
    public void pushCityEventChangeInfoDueToMove(final int playerId, final int fromCityId, final int toCityId) {
        final PlayerDto playerDto = Players.getPlayer(playerId);
        if (playerDto == null) {
            return;
        }
        final PlayerAttribute pa = this.dataGetter.getPlayerAttributeDao().read(playerDto.playerId);
        final char[] cs = pa.getFunctionId().toCharArray();
        if (cs[61] != '1') {
            return;
        }
        try {
            final List<PlayerGeneralMilitary> pgmList = this.dataGetter.getPlayerGeneralMilitaryDao().getMilitaryList(playerDto.playerId);
            boolean hasPgmInFromCity = false;
            for (final PlayerGeneralMilitary pgm : pgmList) {
                if (pgm.getLocationId() == fromCityId) {
                    hasPgmInFromCity = true;
                    break;
                }
            }
            if (!hasPgmInFromCity) {
                final CityAttribute cityAttribute1 = this.cityAttributeMap.get(fromCityId);
                if (cityAttribute1 != null && cityAttribute1.visiable == 0 && (cityAttribute1.viewForceId == 0 || cityAttribute1.viewForceId == playerDto.forceId)) {
                    final JsonDocument doc1 = new JsonDocument();
                    doc1.startObject();
                    doc1.createElement("cityId", fromCityId);
                    doc1.startObject("cityEvent");
                    doc1.createElement("dropType", 1);
                    doc1.createElement("eventType", 1);
                    doc1.createElement("eventCount", 0);
                    doc1.endObject();
                    doc1.endObject();
                    Players.push(playerDto.playerId, PushCommand.PUSH_CITY_EVENT, doc1.toByte());
                }
            }
            int pgmInToCount = 0;
            for (final PlayerGeneralMilitary pgm2 : pgmList) {
                if (pgm2.getLocationId() == toCityId) {
                    ++pgmInToCount;
                }
            }
            if (pgmInToCount == 1) {
                final CityAttribute cityAttribute2 = this.cityAttributeMap.get(toCityId);
                if (cityAttribute2 != null && cityAttribute2.visiable == 0 && (cityAttribute2.viewForceId == 0 || cityAttribute2.viewForceId == playerDto.forceId)) {
                    final JsonDocument doc2 = new JsonDocument();
                    doc2.startObject();
                    doc2.createElement("cityId", toCityId);
                    doc2.startObject("cityEvent");
                    String dropPic = "0";
                    switch (cityAttribute2.eventType) {
                        case 2: {
                            final WdSjEv wdSjEv = (WdSjEv)this.dataGetter.getWdSjEvCache().get((Object)cityAttribute2.eventTargetId);
                            dropPic = wdSjEv.getPic();
                            break;
                        }
                        case 1: {
                            final WdSjBo wdSjBo = (WdSjBo)this.dataGetter.getWdSjBoCache().get((Object)cityAttribute2.eventTargetId);
                            dropPic = wdSjBo.getPic();
                            break;
                        }
                    }
                    doc2.createElement("dropType", dropPic);
                    if (cityAttribute2.countDown == -1L) {
                        doc2.createElement("eventType", 1);
                        doc2.createElement("eventCount", cityAttribute2.leftCount);
                    }
                    else {
                        doc2.createElement("eventType", 2);
                        doc2.createElement("countDown", cityAttribute2.countDown - System.currentTimeMillis());
                    }
                    doc2.endObject();
                    doc2.endObject();
                    Players.push(playerDto.playerId, PushCommand.PUSH_CITY_EVENT, doc2.toByte());
                }
            }
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("pushCityEventChangeInfoDueToMove get Exception. fromCityId:" + fromCityId + " toCityId:" + toCityId, e);
        }
    }
    
    public void doHiddenEventBoBao(final int playerId, final CityAttribute cityAttribute, final int option) {
        final PlayerDto playerDto = Players.getPlayer(playerId);
        String countryName = null;
        switch (playerDto.forceId) {
            case 1: {
                countryName = LocalMessages.T_FORCE_WEI_DOT;
                break;
            }
            case 2: {
                countryName = LocalMessages.T_FORCE_SHU_DOT;
                break;
            }
            case 3: {
                countryName = LocalMessages.T_FORCE_WU_DOT;
                break;
            }
        }
        final String part1 = String.valueOf(countryName) + playerDto.playerName;
        String part2 = cityAttribute.targetWorldCity.getName();
        String part2_2 = null;
        String part3 = null;
        switch (cityAttribute.eventType) {
            case 2: {
                final WdSjEv wdSjEv = (WdSjEv)this.dataGetter.getWdSjEvCache().get((Object)cityAttribute.eventTargetId);
                part2_2 = wdSjEv.getNotice();
                if (1 == option) {
                    part3 = String.valueOf(wdSjEv.getRewardDisc1()) + wdSjEv.getReward1().split(",")[1];
                    break;
                }
                if (2 == option) {
                    part3 = String.valueOf(wdSjEv.getRewardDisc2()) + wdSjEv.getReward2().split(",")[1];
                    break;
                }
                break;
            }
            case 1: {
                final WdSjBo wdSjBo = (WdSjBo)this.dataGetter.getWdSjBoCache().get((Object)cityAttribute.eventTargetId);
                part2_2 = wdSjBo.getNotice();
                final int armyId = wdSjBo.getChief();
                final int troopId = ((General)this.dataGetter.getGeneralCache().get((Object)armyId)).getTroop();
                final Troop troop = (Troop)this.dataGetter.getTroopCache().get((Object)troopId);
                part3 = BattleDrop.getDropToString(troop.getDrop());
                break;
            }
            default: {
                ErrorSceneLog.getInstance().appendErrorMsg("eventType error").append("cityAttribute.eventType", cityAttribute.eventType).appendClassName("CityEventManager").appendMethodName("doHiddenEventBoBao").flush();
                return;
            }
        }
        if (part2_2 == null) {
            ErrorSceneLog.getInstance().appendErrorMsg("Notice error").append("cityAttribute.eventType", cityAttribute.eventType).append("cityAttribute.eventTargetId", cityAttribute.eventTargetId).appendClassName("CityEventManager").appendMethodName("doHiddenEventBoBao").flush();
            return;
        }
        part2 = String.valueOf(part2) + part2_2;
        final String boBaoMsgWorld = MessageFormatter.format(LocalMessages.CITY_EVENT_HIDDEN_EVENT_BOBAO_FORMAT, new Object[] { ColorUtil.getForceMsg(playerDto.forceId, part1), part2, part3 });
        this.dataGetter.getChatService().sendBigNotice("GLOBAL", null, boBaoMsgWorld, null);
    }
    
    public Tuple<Integer, Integer> getCostTypeAndNum(final String str) {
        final Tuple<Integer, Integer> tuple = new Tuple();
        final String[] tempArr = str.split(",");
        if ("bmw".equals(tempArr[0])) {
            tuple.left = 31;
        }
        else if ("xo".equals(tempArr[0])) {
            tuple.left = 32;
        }
        else if ("picasso".equals(tempArr[0])) {
            tuple.left = 33;
        }
        else if ("gold".equals(tempArr[0])) {
            tuple.left = 11;
        }
        tuple.right = Integer.parseInt(tempArr[1]);
        return tuple;
    }
    
    public boolean alreadyHasWorldDramaEvent(final int playerId) {
        try {
            final Map<Integer, PlayerEventObj> map = this.playerEventMap.get(playerId);
            if (map == null) {
                return false;
            }
            for (final Map.Entry<Integer, PlayerEventObj> entry : map.entrySet()) {
                if (entry.getKey() == 5) {
                    return true;
                }
            }
        }
        catch (Exception e) {
            final ErrorSceneLog errorSceneLog = ErrorSceneLog.getInstance();
            errorSceneLog.error(this, e);
        }
        return false;
    }
    
    public void addTuFeiSlaveEventWhenLogIn(final int playerId) {
        try {
            final char[] cs = this.dataGetter.getPlayerAttributeDao().getFunctionId(playerId).toCharArray();
            if (cs[52] != '1') {
                return;
            }
            final Slaveholder slaveholder = this.dataGetter.getSlaveholderDao().read(playerId);
            if (slaveholder == null) {
                return;
            }
            if (slaveholder.getGrabNum() > 40) {
                return;
            }
            ConcurrentHashMap<Integer, PlayerEventObj> map = getInstance().playerEventMap.get(playerId);
            if (map == null) {
                map = new ConcurrentHashMap<Integer, PlayerEventObj>();
                getInstance().playerEventMap.put(playerId, map);
            }
            final int eventType = 8;
            if (map.get(eventType) != null) {
                return;
            }
            final WdSjp wdSjp = (WdSjp)this.dataGetter.getWdSjpCache().get((Object)eventType);
            if (wdSjp == null) {
                ErrorSceneLog.getInstance().appendErrorMsg("wdSjp is null").append("eventType", eventType).append("playerId", playerId).appendMethodName("addTuFeiSlaveEventWhenLogIn").appendClassName(this.getClass().getSimpleName()).flush();
                return;
            }
            final PlayerBattleAttribute pba = this.dataGetter.getPlayerBattleAttributeDao().read(playerId);
            if (pba.getEventSlaveCountToday() < wdSjp.getNumMax()) {
                getInstance().addPlayerEvent(playerId, eventType);
            }
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error(String.valueOf(this.getClass().getSimpleName()) + " addTuFeiSlaveEventWhenLogIn catch Exception", e);
        }
    }
    
    public void addTuFeiSlaveEventForOnLinePlayersAtZeroOclockAfterReset() {
        try {
            for (final PlayerDto playerDto : Players.getAllPlayer()) {
                this.addTuFeiSlaveEventWhenLogIn(playerDto.playerId);
            }
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error(String.valueOf(this.getClass().getSimpleName()) + " addTuFeiSlaveEventForOnLinePlayersAtZeroOclockAfterReset catch Exception", e);
        }
    }
}
