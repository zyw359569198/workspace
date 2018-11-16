package com.reign.gcld.world.service;

import org.springframework.stereotype.*;
import com.reign.gcld.rank.dao.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.general.dao.*;
import com.reign.gcld.world.dao.*;
import com.reign.gcld.store.dao.*;
import com.reign.gcld.sdata.cache.*;
import com.reign.gcld.player.dao.*;
import com.reign.gcld.log.*;
import com.reign.gcld.player.dto.*;
import com.reign.framework.json.*;
import com.reign.gcld.rank.domain.*;
import org.springframework.transaction.annotation.*;
import com.reign.gcld.general.domain.*;
import com.reign.gcld.store.domain.*;
import com.reign.gcld.world.common.*;
import com.reign.gcld.world.domain.*;
import com.reign.gcld.common.*;
import com.reign.gcld.general.dto.*;
import com.reign.gcld.battle.reward.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.gcld.player.domain.*;
import java.util.*;

@Component("worldFarmService")
public class WorldFarmService implements IWorldFarmService
{
    @Autowired
    private IForceInfoDao forceInfoDao;
    @Autowired
    private FarmCache farmCache;
    @Autowired
    private IPlayerGeneralMilitaryDao playerGeneralMilitaryDao;
    @Autowired
    private IPlayerFarmDao playerFarmDao;
    @Autowired
    private IDataGetter dataGetter;
    @Autowired
    private IPlayerResourceDao playerResourceDao;
    @Autowired
    private IStoreHouseDao storeHouseDao;
    @Autowired
    private FarmCoeCache farmCoeCache;
    @Autowired
    private WorldCitySpecialCache worldCitySpecialCache;
    @Autowired
    private ICityDataCache cityDataCache;
    @Autowired
    private CityEffectCache cityEffectCache;
    @Autowired
    private ChargeitemCache chargeitemCache;
    @Autowired
    private IPlayerDao playerDao;
    private static Object[] objects;
    private static ErrorLogger errorLog;
    private final int BUFF = 50;
    
    static {
        WorldFarmService.objects = new Object[] { new Object(), new Object(), new Object() };
        WorldFarmService.errorLog = new ErrorLogger();
    }
    
    @Transactional
    @Override
    public byte[] investFarm(final PlayerDto playerDto) {
        if (playerDto.playerLv < 30) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.FUNCTION_NOT_OPEN);
        }
        if (playerDto.forceId > 3 && playerDto.forceId < 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final int number = 10000;
        final WorldFarmCache cache = WorldFarmCache.getInstatnce();
        final long expireTime = cache.getCdByPlayerId(playerDto.playerId);
        final long now = System.currentTimeMillis();
        if (expireTime > now + 3600000L) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.FARM_INVEST_IN_CD);
        }
        synchronized (WorldFarmService.objects[playerDto.forceId - 1]) {
            final ForceInfo forceInfo = this.forceInfoDao.read(playerDto.forceId);
            final int lv = forceInfo.getLv();
            if (lv >= this.farmCache.getMaxLv()) {
                // monitorexit(WorldFarmService.objects[playerDto.forceId - 1])
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
            }
            final long sum = forceInfo.getFarmInvestSum();
            final Farm farm = (Farm)this.farmCache.get((Object)(lv + 1));
            if (farm == null) {
                // monitorexit(WorldFarmService.objects[playerDto.forceId - 1])
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
            }
            if (farm.getNationLv() > forceInfo.getForceLv()) {
                // monitorexit(WorldFarmService.objects[playerDto.forceId - 1])
                return JsonBuilder.getJson(State.FAIL, LocalMessages.FARM_INVEST_NATION_LV_TOO_LOW);
            }
            if (farm.getUpCopper() <= sum) {
                // monitorexit(WorldFarmService.objects[playerDto.forceId - 1])
                return JsonBuilder.getJson(State.FAIL, LocalMessages.FARM_INVEST_FULL);
            }
            if (!this.playerResourceDao.consumeCopper(playerDto.playerId, number, "\u5c6f\u7530\u6350\u8d60\u6d88\u8017\u94f6\u5e01")) {
                // monitorexit(WorldFarmService.objects[playerDto.forceId - 1])
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10001);
            }
            final long sumAfter = sum + number;
            if (sumAfter >= farm.getUpCopper()) {
                this.forceInfoDao.updateFarmLv(playerDto.forceId, lv + 1);
            }
            else {
                this.forceInfoDao.updateInvestSum(playerDto.forceId, sumAfter);
            }
        }
        // monitorexit(WorldFarmService.objects[playerDto.forceId - 1])
        final long newExpireTime = Math.max(now, expireTime);
        cache.updatePlayerCd(playerDto.playerId, newExpireTime + 600000L);
        final int exp = 1000;
        this.dataGetter.getPlayerService().updateExpAndPlayerLevel(playerDto.playerId, exp, "\u5c6f\u7530\u6350\u8d60\u83b7\u5f97\u7ecf\u9a8c");
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("exp", exp);
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public byte[] start(final PlayerDto playerDto, final int type, final int vId) {
        if (playerDto.playerLv < 30) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.FUNCTION_NOT_OPEN);
        }
        if (type < 0 && type > 3) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final PlayerGeneralMilitary pgm = this.playerGeneralMilitaryDao.read(vId);
        if (pgm == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final int nextState = this.getStateByType(type);
        final boolean isSwitch = pgm.getState() != nextState && pgm.getState() > 24;
        if (pgm.getState() < 24 || (!isSwitch && pgm.getState() >= 25)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.GENERAL_BUSY);
        }
        if (pgm.getLocationId() != WorldFarmCache.forceCityIdMap.get(playerDto.forceId)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.FARM_LOCATION_WRONG);
        }
        final MultiResult result = this.doStart(playerDto, isSwitch, type, pgm);
        return (byte[])result.result2;
    }
    
    @Override
    public MultiResult doStart(final PlayerDto playerDto, final boolean isSwitch, final int type, final PlayerGeneralMilitary pgm) {
        final MultiResult result = new MultiResult();
        final ForceInfo forceInfo = this.forceInfoDao.read(playerDto.forceId);
        final List<StoreHouse> shList = this.storeHouseDao.getByItemId(playerDto.playerId, 1701, 20);
        StoreHouse tokes = null;
        if (!isSwitch) {
            if (shList == null || shList.size() <= 0 || shList.get(0).getNum() <= 0) {
                result.result1 = false;
                result.result2 = JsonBuilder.getJson(State.FAIL, LocalMessages.FARM_TOKEN_IS_NOT_ENOUGH);
                return result;
            }
            tokes = shList.get(0);
        }
        final int lv = forceInfo.getLv();
        if (lv <= 0) {
            result.result1 = false;
            result.result2 = JsonBuilder.getJson(State.FAIL, LocalMessages.FARM_LV_IS_WOO_LV);
            return result;
        }
        final Farm farm = (Farm)this.farmCache.get((Object)lv);
        if (farm == null) {
            result.result1 = false;
            result.result2 = JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
            return result;
        }
        int extraCost = 0;
        int extraExp = 0;
        final int coe = this.farmCoeCache.getByLv(playerDto.playerLv);
        if (type == 2) {
            extraCost = farm.getConsumeFood() * coe;
            extraExp = farm.getExpExtra();
            if (!this.playerResourceDao.consumeFood(playerDto.playerId, extraCost, "\u5c6f\u7530\u82e6\u7ec3\u6d88\u8017\u7cae\u98df")) {
                result.result1 = false;
                result.result2 = JsonBuilder.getJson(State.FAIL, LocalMessages.T_MARKET_NOENOUGH_RESOURCE);
                return result;
            }
        }
        else if (type == 3) {
            extraCost = farm.getConsumeFood2() * coe;
            extraExp = farm.getExpExtra2();
            if (!this.playerResourceDao.consumeFood(playerDto.playerId, extraCost, "\u5c6f\u7530\u82e6\u7ec32\u6d88\u8017\u7cae\u98df")) {
                result.result1 = false;
                result.result2 = JsonBuilder.getJson(State.FAIL, LocalMessages.T_MARKET_NOENOUGH_RESOURCE);
                return result;
            }
        }
        final int time = (type == 0) ? farm.getFoodTime() : farm.getExpTime();
        final long executeTime = time * 60000L;
        int cityEffect = this.getCityEffect(playerDto);
        if (type == 0) {
            cityEffect = 0;
        }
        final FarmingInfo farmingInfo = new FarmingInfo();
        farmingInfo.vId = pgm.getVId();
        farmingInfo.playerId = playerDto.playerId;
        farmingInfo.generalId = pgm.getGeneralId();
        farmingInfo.endTimeDate = new Date(executeTime + System.currentTimeMillis());
        farmingInfo.type = type;
        farmingInfo.rewardNum = ((type == 0) ? farm.getFoodReward() : farm.getExpReward());
        final FarmingInfo farmingInfo2 = farmingInfo;
        farmingInfo2.rewardNum += extraExp;
        farmingInfo.rewardNum *= coe;
        farmingInfo.rewardNum += cityEffect;
        farmingInfo.lv = pgm.getLv();
        final PlayerFarm playerFarm = new PlayerFarm();
        playerFarm.setEndTime(farmingInfo.endTimeDate);
        playerFarm.setGeneralId(pgm.getGeneralId());
        playerFarm.setPlayerId(playerDto.playerId);
        playerFarm.setType(type);
        playerFarm.setReward(farmingInfo.rewardNum);
        playerFarm.setTime(time);
        this.playerFarmDao.deletByPAndGId(playerFarm.getPlayerId(), playerFarm.getGeneralId());
        this.doCreatPlayerFarm(playerFarm);
        final int state = this.getStateByType(type);
        this.changeGeneralState(state, pgm);
        final WorldFarmCache cache = WorldFarmCache.getInstatnce();
        cache.addFarmingInfoByForceId(playerDto.forceId, farmingInfo);
        if (!isSwitch) {
            if (tokes.getNum() <= 1) {
                this.storeHouseDao.deleteById(tokes.getVId());
            }
            else {
                this.storeHouseDao.reduceNum(tokes.getVId(), 1);
            }
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("generalId", pgm.getGeneralId());
        doc.createElement("soro", 0);
        doc.createElement("type", type);
        doc.endObject();
        Players.push(playerDto.playerId, PushCommand.PUSH_WORLD_FARM, doc.toByte());
        final JsonDocument returnDoc = new JsonDocument();
        returnDoc.startObject();
        returnDoc.createElement("food", extraCost);
        returnDoc.endObject();
        result.result1 = true;
        result.result2 = JsonBuilder.getJson(State.SUCCESS, returnDoc.toByte());
        return result;
    }
    
    private int getCityEffect(final PlayerDto playerDto) {
        int cityEffect = 0;
        final Integer cityId = this.worldCitySpecialCache.getCityIdDisplayByKey(3);
        final boolean hasCity = this.cityDataCache.hasCity(playerDto.forceId, (cityId == null) ? 0 : ((int)cityId));
        if (hasCity) {
            cityEffect = (int)this.cityEffectCache.getCityEffect2(playerDto.forceId, 3);
        }
        return cityEffect;
    }
    
    private void doCreatPlayerFarm(final PlayerFarm playerFarm) {
        this.playerFarmDao.create(playerFarm);
        final GeneralMoveDto gmd = CityService.getUpdateGeneralMoveDto(playerFarm.getPlayerId(), playerFarm.getGeneralId());
        gmd.farmtime = playerFarm.getEndTime().getTime();
    }
    
    private int getStateByType(final int type) {
        return (type == 0) ? 25 : ((type == 1) ? 26 : ((type == 2) ? 27 : 28));
    }
    
    @Override
    public void changeGeneralState(final int state, final PlayerGeneralMilitary pgm) {
        try {
            this.playerGeneralMilitaryDao.updateState(pgm.getVId(), state);
            pgm.setState(state);
            this.dataGetter.getGeneralService().sendGeneralMilitaryRecruitInfo(pgm.getPlayerId(), pgm);
        }
        catch (Exception e) {
            WorldFarmService.errorLog.error(this, e);
        }
    }
    
    @Transactional
    @Override
    public byte[] stop(final PlayerDto playerDto, final int vid) {
        if (playerDto.playerLv < 30) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.FUNCTION_NOT_OPEN);
        }
        final PlayerGeneralMilitary pgm = this.playerGeneralMilitaryDao.read(vid);
        if (pgm == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final int generalId = pgm.getGeneralId();
        final PlayerFarm playerFarm = this.playerFarmDao.getByPAndGId(playerDto.playerId, generalId);
        if (playerFarm == null) {
            if (pgm.getState() >= 25) {
                WorldFarmService.errorLog.error("State Wrong.No Farm Record. PlayerId:" + playerDto.playerId + "generalId:" + generalId);
                this.changeGeneralState(24, pgm);
            }
            return JsonBuilder.getJson(State.FAIL, LocalMessages.FARM_NO_FARM_NOW);
        }
        final ForceInfo forceInfo = this.forceInfoDao.read(playerDto.forceId);
        final int lv = forceInfo.getLv();
        final Farm farm = (Farm)this.farmCache.get((Object)lv);
        if (farm == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final int type = playerFarm.getType();
        final int realReward = this.rewardPlayerGeneral(playerFarm, pgm, playerDto, false);
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("type", type);
        doc.createElement("reward", realReward);
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    private void dealRewardAfterStop(final int vId, final int type, final PlayerDto playerDto, final int reward, final int generalId) {
        if (type == 0) {
            final int rewardType = 3;
            RewardType.reward(this.dataGetter, rewardType, reward, playerDto.playerId, 8);
        }
        else {
            final int reasonType = (type == 1) ? 9 : ((type == 2) ? 10 : 11);
            final int rewardType2 = 10;
            RewardType.reward(this.dataGetter, rewardType2, reward, playerDto.playerId, reasonType);
            this.dataGetter.getGeneralService().updateExpAndGeneralLevel(playerDto.playerId, generalId, reward);
        }
        this.playerFarmDao.deleteById(vId);
    }
    
    @Override
    public void dealFarmWork(final String params) {
    }
    
    @Override
    public byte[] getFarmInfo(final PlayerDto playerDto) {
        if (playerDto.playerLv < 30) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.FUNCTION_NOT_OPEN);
        }
        final WorldFarmCache cache = WorldFarmCache.getInstatnce();
        final ForceInfo forceInfo = this.forceInfoDao.read(playerDto.forceId);
        final int lv = forceInfo.getLv();
        final Farm farm = (Farm)this.farmCache.get((Object)lv);
        final Farm nextFarm = (Farm)this.farmCache.get((Object)(lv + 1));
        final int forceLv = forceInfo.getForceLv();
        final long sum = forceInfo.getFarmInvestSum();
        final int cityId = WorldFarmCache.forceCityIdMap.get(playerDto.forceId);
        final WorldCity worldCity = (WorldCity)this.dataGetter.getWorldCityCache().get((Object)cityId);
        final Integer specialCity = this.worldCitySpecialCache.getCityIdDisplayByKey(3);
        final boolean hasCity = this.cityDataCache.hasCity(playerDto.forceId, (specialCity == null) ? 0 : ((int)specialCity));
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("forceId", playerDto.forceId);
        doc.createElement("cityName", worldCity.getName());
        if (specialCity != null) {
            final WorldCity specialWorldCity = (WorldCity)this.dataGetter.getWorldCityCache().get((Object)specialCity);
            if (specialWorldCity != null) {
                doc.createElement("specialCityName", specialWorldCity.getName());
                doc.createElement("hasSpecial", hasCity);
                final WorldCitySpecial cityEffect = (WorldCitySpecial)this.worldCitySpecialCache.get((Object)specialCity);
                if (cityEffect != null) {
                    doc.createElement("specialEffect", cityEffect.getPar2());
                }
            }
        }
        if (farm != null) {
            doc.appendJson("nowFarm", this.getFarmInfoDoc(farm));
            final int coe = this.farmCoeCache.getByLv(playerDto.playerLv);
            final int extraFood = coe * farm.getConsumeFood();
            final int extraFood2 = coe * farm.getConsumeFood2();
            doc.createElement("perFood", extraFood);
            doc.createElement("perGold", extraFood2);
            final int extra1 = farm.getExpExtra();
            final int extra2 = farm.getExpExtra2();
            final int cityEffect2 = this.getCityEffect(playerDto);
            final int rewardNum0 = farm.getFoodReward() * coe + cityEffect2;
            final int rewardNum2 = (farm.getExpReward() + extra1) * coe + cityEffect2;
            final int rewardNum3 = (farm.getExpReward() + extra2) * coe + cityEffect2;
            doc.createElement("rewardFood", rewardNum0);
            doc.createElement("rewardExp1", rewardNum2);
            doc.createElement("rewardExp2", rewardNum3);
            final Chargeitem item = (Chargeitem)this.chargeitemCache.get((Object)86);
            final int gold = (item == null) ? 1 : item.getCost();
            final double param = (item == null) ? 1.0 : item.getParam();
            doc.createElement("cdRecoverGold", gold / param);
        }
        if (nextFarm != null) {
            doc.createElement("canInvest", nextFarm != null && forceLv >= nextFarm.getNationLv());
            doc.createElement("percentage", (int)(sum / nextFarm.getUpCopper() * 100.0));
            doc.appendJson("nextFarm", this.getFarmInfoDoc(nextFarm));
        }
        else {
            doc.createElement("percentage", 100);
        }
        final List<StoreHouse> shList = this.storeHouseDao.getByItemId(playerDto.playerId, 1701, 20);
        doc.createElement("itemNumber", (shList == null || shList.size() <= 0) ? 0 : shList.get(0).getNum());
        doc.createElement("copper", 10000);
        doc.createElement("cd", cache.getCdByPlayerId(playerDto.playerId) - System.currentTimeMillis());
        doc.createElement("maxCd", 3600000);
        final List<PlayerGeneralMilitary> list = this.playerGeneralMilitaryDao.getMilitaryList(playerDto.playerId);
        boolean willDisplay = false;
        final List<PlayerGeneralMilitary> displayeList = new ArrayList<PlayerGeneralMilitary>();
        final List<FarmingInfo> farmingInfos = cache.getRandNumberInfos(playerDto.forceId, playerDto.playerId);
        for (final PlayerGeneralMilitary pgm : list) {
            if (pgm.getLocationId() == WorldFarmCache.forceCityIdMap.get(playerDto.forceId)) {
                willDisplay = true;
                displayeList.add(pgm);
            }
        }
        if (farmingInfos != null && !farmingInfos.isEmpty()) {
            willDisplay = true;
            displayeList.addAll(this.changeFarmingInfos(farmingInfos));
        }
        if (willDisplay) {
            PlayerFarm playerFarm = null;
            doc.startArray("generals");
            for (final PlayerGeneralMilitary pgm2 : displayeList) {
                doc.startObject();
                final General general = (General)this.dataGetter.getGeneralCache().get((Object)pgm2.getGeneralId());
                final Player player = this.dataGetter.getPlayerDao().read(pgm2.getPlayerId());
                this.appendGeneralNormalInfo(doc, general);
                doc.createElement("generalLv", pgm2.getLv());
                doc.createElement("playerName", player.getPlayerName());
                doc.createElement("forceId", player.getForceId());
                doc.createElement("playerId", pgm2.getPlayerId());
                doc.createElement("vId", pgm2.getVId());
                doc.createElement("generalId", pgm2.getGeneralId());
                playerFarm = this.playerFarmDao.getByPAndGId(player.getPlayerId(), pgm2.getGeneralId());
                if (playerFarm != null) {
                    this.appendFarmInfo(doc, playerFarm);
                }
                doc.createElement("generalState", pgm2.getState());
                doc.endObject();
            }
            doc.endArray();
        }
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    private List<PlayerGeneralMilitary> changeFarmingInfos(final List<FarmingInfo> farmingInfos) {
        final List<PlayerGeneralMilitary> list = new ArrayList<PlayerGeneralMilitary>();
        PlayerGeneralMilitary pgm = null;
        for (final FarmingInfo info : farmingInfos) {
            pgm = new PlayerGeneralMilitary();
            pgm.setPlayerId(info.playerId);
            pgm.setGeneralId(info.generalId);
            pgm.setState((info.type == 0) ? 25 : 26);
            pgm.setVId(info.vId);
            pgm.setLv(info.lv);
            list.add(pgm);
        }
        return list;
    }
    
    private byte[] getFarmInfoDoc(final Farm farm) {
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("farmLv", farm.getLv());
        doc.createElement("farmName", farm.getName());
        doc.createElement("rewardFood", farm.getFoodReward());
        doc.createElement("foodTime", farm.getFoodTime());
        doc.createElement("rewardExp", farm.getExpReward());
        doc.createElement("expTime", farm.getExpTime());
        doc.endObject();
        return doc.toByte();
    }
    
    private void appendFarmInfo(final JsonDocument doc, final PlayerFarm playerFarm) {
        doc.createElement("type", playerFarm.getType());
        final Date date = playerFarm.getEndTime();
        doc.createElement("leftTime", date.getTime() - System.currentTimeMillis());
        doc.createElement("reward", playerFarm.getReward());
    }
    
    private void appendGeneralNormalInfo(final JsonDocument doc, final General general) {
        doc.createElement("generalName", general.getName());
        doc.createElement("generalQuality", general.getQuality());
        doc.createElement("generalType", general.getType());
        doc.createElement("generalPic", general.getPic());
        doc.createElement("generalTroop", general.getTroop());
    }
    
    @Override
    public boolean isInFarmForbiddenOperation(final PlayerGeneralMilitary pgm, final boolean isMove) {
        try {
            if (pgm == null) {
                return false;
            }
            if (isMove) {
                if (pgm.getState() > 24) {
                    return true;
                }
            }
            else if (pgm.getState() >= 24) {
                return true;
            }
            final PlayerFarm playerFarm = this.playerFarmDao.getByPAndGId(pgm.getPlayerId(), pgm.getGeneralId());
            if (playerFarm != null) {
                final Date endDate = playerFarm.getEndTime();
                if (endDate.after(new Date())) {
                    return true;
                }
            }
            return false;
        }
        catch (Exception e) {
            WorldFarmService.errorLog.error(this, e);
            return false;
        }
    }
    
    @Override
    public byte[] startAll(final PlayerDto playerDto, final int type) {
        if (playerDto.playerLv < 30) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.FUNCTION_NOT_OPEN);
        }
        if (type < 0 && type > 3) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final ForceInfo forceInfo = this.forceInfoDao.read(playerDto.forceId);
        final int lv = forceInfo.getLv();
        if (lv <= 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.FARM_LV_IS_WOO_LV);
        }
        final List<PlayerGeneralMilitary> list = this.playerGeneralMilitaryDao.getMilitaryList(playerDto.playerId);
        if (list == null || list.isEmpty()) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.FARM_NO_GENERAL);
        }
        final Farm farm = (Farm)this.farmCache.get((Object)lv);
        if (farm == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final int nextState = this.getStateByType(type);
        final int time = (type == 0) ? farm.getFoodTime() : farm.getExpTime();
        final int reward = (type == 0) ? farm.getFoodReward() : farm.getExpReward();
        final int extraReward = (type == 2) ? farm.getExpExtra() : ((type == 3) ? farm.getExpExtra2() : 0);
        final long executionTime = time * 60000L;
        final int coe = this.farmCoeCache.getByLv(playerDto.playerLv);
        List<FarmingInfo> infos = null;
        FarmingInfo info = null;
        final List<PlayerGeneralMilitary> pgmLists = new ArrayList<PlayerGeneralMilitary>();
        int switchNum = 0;
        int cityEffect = this.getCityEffect(playerDto);
        int extraCost = 0;
        if (type == 0) {
            cityEffect = 0;
        }
        for (final PlayerGeneralMilitary pgm : list) {
            if (pgm.getLocationId() != WorldFarmCache.forceCityIdMap.get(playerDto.forceId)) {
                continue;
            }
            if (pgm.getState() < 24) {
                continue;
            }
            if (pgm.getState() == nextState) {
                continue;
            }
            final PlayerFarm playerFarm = this.playerFarmDao.getByPAndGId(pgm.getPlayerId(), pgm.getGeneralId());
            if (playerFarm != null) {
                if (playerFarm.getType() == type) {
                    continue;
                }
                ++switchNum;
            }
            if (infos == null) {
                infos = new ArrayList<FarmingInfo>();
            }
            info = new FarmingInfo();
            info.vId = pgm.getVId();
            info.playerId = pgm.getPlayerId();
            info.generalId = pgm.getGeneralId();
            info.type = type;
            info.endTimeDate = new Date(System.currentTimeMillis() + executionTime);
            info.rewardNum = reward + extraReward;
            info.rewardNum *= coe;
            info.rewardNum += cityEffect;
            info.lv = pgm.getLv();
            infos.add(info);
            pgmLists.add(pgm);
        }
        if (infos == null || infos.isEmpty()) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.FARM_NO_GENERAL);
        }
        final List<StoreHouse> shList = this.storeHouseDao.getByItemId(playerDto.playerId, 1701, 20);
        StoreHouse storeHouse = null;
        if (infos.size() - switchNum > 0) {
            if (shList == null || shList.size() <= 0 || shList.get(0).getNum() < infos.size() - switchNum) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.FARM_TOKEN_IS_NOT_ENOUGH);
            }
            storeHouse = shList.get(0);
        }
        if (type == 2) {
            extraCost = farm.getConsumeFood() * infos.size() * coe;
            if (!this.playerResourceDao.consumeFood(playerDto.playerId, extraCost, "\u5c6f\u7530\u82e6\u7ec3\u6d88\u8017\u7cae\u98df")) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_MARKET_NOENOUGH_RESOURCE);
            }
        }
        else if (type == 3) {
            extraCost = farm.getConsumeFood2() * infos.size() * coe;
            if (!this.playerResourceDao.consumeFood(playerDto.playerId, extraCost, "\u5c6f\u7530\u82e6\u7ec32\u6d88\u8017\u7cae\u98df")) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_MARKET_NOENOUGH_RESOURCE);
            }
        }
        PlayerFarm playerFarm = null;
        for (final FarmingInfo f : infos) {
            playerFarm = new PlayerFarm();
            playerFarm.setPlayerId(f.playerId);
            playerFarm.setGeneralId(f.generalId);
            playerFarm.setType(f.type);
            playerFarm.setEndTime(f.endTimeDate);
            playerFarm.setTime(time);
            playerFarm.setReward(f.rewardNum);
            this.playerFarmDao.deletByPAndGId(f.playerId, f.generalId);
            this.doCreatPlayerFarm(playerFarm);
        }
        final int state = this.getStateByType(type);
        for (final PlayerGeneralMilitary pgm2 : pgmLists) {
            this.changeGeneralState(state, pgm2);
            final JsonDocument doc = new JsonDocument();
            doc.startObject();
            doc.createElement("generalId", pgm2.getGeneralId());
            doc.createElement("soro", 0);
            doc.createElement("type", type);
            doc.endObject();
            Players.push(playerDto.playerId, PushCommand.PUSH_WORLD_FARM, doc.toByte());
        }
        final WorldFarmCache cache = WorldFarmCache.getInstatnce();
        cache.addFarmingInfoByForceId(playerDto.forceId, infos);
        if (infos.size() - switchNum > 0) {
            if (storeHouse.getNum() > infos.size() - switchNum) {
                this.storeHouseDao.reduceNum(storeHouse.getVId(), infos.size() - switchNum);
            }
            else {
                this.storeHouseDao.deleteById(storeHouse.getVId());
            }
        }
        final JsonDocument doc2 = new JsonDocument();
        doc2.startObject();
        doc2.createElement("food", extraCost);
        doc2.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc2.toByte());
    }
    
    @Override
    public byte[] getRecoverCostGold(final PlayerDto playerDto) {
        int gold = 0;
        final WorldFarmCache cache = WorldFarmCache.getInstatnce();
        final long cd = cache.getCdByPlayerId(playerDto.playerId) - System.currentTimeMillis();
        if (cd > 0L) {
            final Chargeitem chargeitem = (Chargeitem)this.dataGetter.getChargeitemCache().get((Object)78);
            final int part1 = (int)Math.ceil(cd / (chargeitem.getParam() * 60000.0));
            gold = part1 * chargeitem.getCost();
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("gold", gold);
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Transactional
    @Override
    public byte[] recoverGold(final PlayerDto playerDto) {
        int gold = 0;
        final WorldFarmCache cache = WorldFarmCache.getInstatnce();
        final long cd = cache.getCdByPlayerId(playerDto.playerId) - System.currentTimeMillis();
        if (cd > 0L) {
            final Chargeitem chargeitem = (Chargeitem)this.dataGetter.getChargeitemCache().get((Object)78);
            final int part1 = (int)Math.ceil(cd / (chargeitem.getParam() * 60000.0));
            gold = part1 * chargeitem.getCost();
        }
        if (gold <= 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final Player player = this.dataGetter.getPlayerDao().read(playerDto.playerId);
        if (!this.dataGetter.getPlayerDao().consumeGold(player, gold, "\u6350\u8d60\u5c6f\u7530\u79d2cd\u6d88\u8017\u91d1\u5e01")) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10003);
        }
        cache.updatePlayerCd(playerDto.playerId, System.currentTimeMillis());
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Override
    public byte[] stopAll(final PlayerDto playerDto) {
        if (playerDto.playerLv < 30) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.FUNCTION_NOT_OPEN);
        }
        final ForceInfo forceInfo = this.forceInfoDao.read(playerDto.forceId);
        final int lv = forceInfo.getLv();
        if (lv <= 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.FARM_LV_IS_WOO_LV);
        }
        final List<PlayerGeneralMilitary> list = this.playerGeneralMilitaryDao.getMilitaryList(playerDto.playerId);
        if (list == null || list.isEmpty()) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.FARM_NO_GENERAL);
        }
        final Farm farm = (Farm)this.farmCache.get((Object)lv);
        if (farm == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        boolean hasStopped = false;
        final Map<Integer, Integer> results = new HashMap<Integer, Integer>();
        final long now = System.currentTimeMillis();
        int goldCount = 0;
        Date date = null;
        final Chargeitem item = (Chargeitem)this.chargeitemCache.get((Object)86);
        final int param = (item == null) ? 1 : ((int)(Object)item.getParam());
        final long divided = param * 60000L;
        for (final PlayerGeneralMilitary pgm : list) {
            if (pgm.getState() <= 24) {
                continue;
            }
            final PlayerFarm playerFarm = this.playerFarmDao.getByPAndGId(pgm.getPlayerId(), pgm.getGeneralId());
            if (playerFarm == null) {
                continue;
            }
            date = playerFarm.getEndTime();
            if (date.getTime() > now) {
                final long cdTime = date.getTime() - now;
                final int times = (int)((cdTime % divided > 0L) ? (cdTime / divided + 1L) : (cdTime / divided));
                goldCount += times;
            }
            final int real = this.rewardPlayerGeneral(playerFarm, pgm, playerDto, true);
            hasStopped = true;
            if (results.containsKey(playerFarm.getType())) {
                int value = results.get(playerFarm.getType());
                value += real;
                results.put(playerFarm.getType(), value);
            }
            else {
                results.put(playerFarm.getType(), real);
            }
        }
        if (!hasStopped) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.FARM_NO_FARM_NOW);
        }
        if (goldCount > 0) {
            final Player player = this.playerDao.read(playerDto.playerId);
            int gold = (item == null) ? 1 : item.getCost();
            gold *= goldCount;
            if (!this.playerDao.consumeGold(player, gold, "\u79d2\u5c6f\u7530cd\u6d88\u8017\u91d1\u5e01")) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10003);
            }
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.startArray("rewards");
        for (final Integer key : results.keySet()) {
            doc.startObject();
            doc.createElement("type", key);
            doc.createElement("reward", results.get(key));
            doc.endObject();
        }
        doc.endArray();
        doc.createElement("buffCd", 1800000L);
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public int rewardPlayerGeneral(final PlayerFarm playerFarm, final PlayerGeneralMilitary pgm, final PlayerDto playerDto, final boolean rightNow) {
        try {
            final int type = playerFarm.getType();
            final int time = playerFarm.getTime();
            final int reward = playerFarm.getReward();
            final Date endDate = playerFarm.getEndTime();
            final long endTime = endDate.getTime();
            final long now = System.currentTimeMillis();
            final long lastTime = (endTime - now <= 0L) ? 0L : (endTime - now);
            final double percentage = Math.min(lastTime / (time * 60000L), 1.0);
            int realReward = 0;
            if (rightNow) {
                realReward = reward;
            }
            else {
                realReward = (int)((1.0 - percentage) * reward);
            }
            this.dealRewardAfterStop(playerFarm.getVId(), type, playerDto, realReward, pgm.getGeneralId());
            this.changeGeneralState(24, pgm);
            this.dealPlayerBuffer(pgm, type);
            final JsonDocument doc = new JsonDocument();
            doc.startObject();
            doc.createElement("generalId", pgm.getGeneralId());
            doc.createElement("type", playerFarm.getType());
            doc.createElement("soro", 1);
            doc.createElement("reward", realReward);
            doc.endObject();
            Players.push(playerDto.playerId, PushCommand.PUSH_WORLD_FARM, doc.toByte());
            return realReward;
        }
        catch (Exception e) {
            WorldFarmService.errorLog.error(this, e);
            return 0;
        }
    }
    
    private void dealPlayerBuffer(final PlayerGeneralMilitary pgm, final int type) {
        try {
            final int playerId = pgm.getPlayerId();
            final int generalId = pgm.getGeneralId();
            final WorldFarmCache cache = WorldFarmCache.getInstatnce();
            final long now = System.currentTimeMillis();
            cache.updatePlayerBuffCd(playerId, generalId, now + 1800000L);
        }
        catch (Exception e) {
            WorldFarmService.errorLog.error(this, e);
        }
    }
    
    @Override
    public int getBuff(final int playerId, final int generalId) {
        final WorldFarmCache cache = WorldFarmCache.getInstatnce();
        final long value = cache.getBuffCdByPlayerId(playerId, generalId);
        if (value > System.currentTimeMillis()) {
            return 50;
        }
        return 0;
    }
    
    @Override
    public void rebootInit() {
    }
    
    @Transactional
    @Override
    public byte[] getReward(final PlayerDto playerDto, final int generalId, final boolean isVid) {
        PlayerGeneralMilitary pgm = null;
        if (isVid) {
            pgm = this.playerGeneralMilitaryDao.read(generalId);
        }
        else {
            pgm = this.playerGeneralMilitaryDao.getMilitary(playerDto.playerId, generalId);
        }
        if (pgm == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final PlayerFarm playerFarm = this.playerFarmDao.getByPAndGId(playerDto.playerId, generalId);
        if (playerFarm == null) {
            if (pgm.getState() >= 25) {
                WorldFarmService.errorLog.error("PlayerFarm is null but state not right..");
                this.changeGeneralState(24, pgm);
            }
            return JsonBuilder.getJson(State.FAIL, LocalMessages.FARM_NO_FARM_NOW);
        }
        final Date date = playerFarm.getEndTime();
        final Date now = new Date();
        if (date.after(now)) {
            final long cdTime = date.getTime() - now.getTime();
            final Chargeitem item = (Chargeitem)this.chargeitemCache.get((Object)86);
            final int param = (item == null) ? 1 : ((int)(Object)item.getParam());
            final long divided = param * 60000L;
            final int times = (int)((cdTime % divided > 0L) ? (cdTime / divided + 1L) : (cdTime / divided));
            int gold = (item == null) ? 1 : item.getCost();
            gold *= times;
            final Player player = this.playerDao.read(playerDto.playerId);
            if (!this.playerDao.consumeGold(player, gold, "\u79d2\u5c6f\u7530cd\u6d88\u8017\u91d1\u5e01")) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10003);
            }
        }
        final ForceInfo forceInfo = this.forceInfoDao.read(playerDto.forceId);
        final int lv = forceInfo.getLv();
        final Farm farm = (Farm)this.farmCache.get((Object)lv);
        if (farm == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final int realReward = this.rewardPlayerGeneral(playerFarm, pgm, playerDto, true);
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("reward", realReward);
        doc.createElement("type", playerFarm.getType());
        doc.createElement("generalId", playerFarm.getGeneralId());
        doc.createElement("buffCd", 1800000L);
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
}
