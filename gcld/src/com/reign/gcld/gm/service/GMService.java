package com.reign.gcld.gm.service;

import org.springframework.stereotype.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.building.dao.*;
import com.reign.gcld.building.service.*;
import com.reign.gcld.pay.service.*;
import com.reign.gcld.general.service.*;
import com.reign.gcld.world.dao.*;
import com.reign.gcld.task.dao.*;
import com.reign.gcld.general.dao.*;
import com.reign.gcld.slave.service.*;
import com.reign.gcld.pay.dao.*;
import com.reign.gcld.chat.service.*;
import com.reign.gcld.tech.service.*;
import com.reign.gcld.tech.dao.*;
import com.reign.gcld.battle.service.*;
import com.reign.gcld.store.service.*;
import com.reign.gcld.slave.dao.*;
import com.reign.gcld.activity.dao.*;
import com.reign.gcld.tickets.dao.*;
import com.reign.gcld.player.service.*;
import com.reign.gcld.player.dao.*;
import com.reign.gcld.battle.dao.*;
import com.reign.gcld.event.service.*;
import com.reign.gcld.weapon.dao.*;
import com.reign.framework.common.cache.*;
import com.reign.gcld.store.dao.*;
import com.reign.gcld.sdata.cache.*;
import com.reign.gcld.rank.dao.*;
import com.reign.framework.json.*;
import com.reign.gcld.common.event.*;
import org.springframework.transaction.annotation.*;
import com.reign.gcld.player.dto.*;
import com.reign.gcld.building.domain.*;
import com.reign.gcld.battle.domain.*;
import com.reign.gcld.juben.service.*;
import com.reign.gcld.world.domain.*;
import com.reign.gcld.juben.common.*;
import com.reign.framework.netty.servlet.*;
import com.reign.gcld.gm.common.*;
import com.reign.gcld.general.domain.*;
import com.reign.gcld.task.message.*;
import org.apache.commons.lang.*;
import com.reign.gcld.chat.common.*;
import com.reign.gcld.task.reward.*;
import com.reign.gcld.tech.domain.*;
import com.reign.gcld.weapon.domain.*;
import com.reign.gcld.world.common.*;
import com.reign.gcld.world.service.*;
import com.reign.gcld.slave.domain.*;
import com.reign.gcld.general.dto.*;
import com.reign.gcld.rank.domain.*;
import com.reign.gcld.common.util.*;
import com.reign.gcld.player.domain.*;
import com.reign.gcld.event.util.*;
import com.reign.gcld.team.service.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.gcld.store.domain.*;
import com.reign.gcld.sdata.common.*;
import com.reign.gcld.battle.common.*;
import com.reign.gcld.rank.service.*;
import java.util.*;
import com.reign.gcld.rank.common.*;
import com.reign.gcld.common.*;

@Component("gMService")
public class GMService implements IGMService
{
    @Autowired
    private IPlayerDao playerDao;
    @Autowired
    private IBuildingService buildingService;
    @Autowired
    private BuildingCache buildingCache;
    @Autowired
    private ArmiesCache armiesCache;
    @Autowired
    private IPlayerResourceDao playerResourceDao;
    @Autowired
    private IPlayerBuildingDao playerBuildingDao;
    @Autowired
    private ChargeitemCache chargeItemCache;
    @Autowired
    private IBuildingOutputCache buildingOutputCache;
    @Autowired
    private IPayService payService;
    @Autowired
    private IPlayerWorldDao playerWorldDao;
    @Autowired
    private ICache[] caches;
    @Autowired
    private IPlayerGeneralMilitaryDao playerGeneralMilitaryDao;
    @Autowired
    private IGeneralService generalService;
    @Autowired
    private IPlayerAttributeDao playerAttributeDao;
    @Autowired
    private IPlayerArmyDao playerArmyDao;
    @Autowired
    private IPlayerPowerDao playerPowerDao;
    @Autowired
    private PowerCache powerCache;
    @Autowired
    private GeneralCache generalCache;
    @Autowired
    private ICityDao cityDao;
    @Autowired
    private WorldCityCache worldCityCache;
    @Autowired
    private CCache cCache;
    @Autowired
    private ICityService cityService;
    @Autowired
    private ICopyPlayerService copyPlayerService;
    @Autowired
    private IAutoPlayerTask autoPlayerTask;
    @Autowired
    private IPlayerGeneralCivilDao playerGeneralCivilDao;
    @Autowired
    private SerialCache serialCache;
    @Autowired
    private IPlayerTaskDao playerTaskDao;
    @Autowired
    private IPlayerGeneralDao playerGeneralDao;
    @Autowired
    private IPlayerSlaveDao playerSlaveDao;
    @Autowired
    private ISlaveService slaveService;
    @Autowired
    private ChatUtil chatUtil;
    @Autowired
    private BroadCastUtil broadCastUtil;
    @Autowired
    private IPlayerPayDao playerPayDao;
    @Autowired
    private CityDataCache cityDataCache;
    @Autowired
    private IChatService chatService;
    @Autowired
    private GeneralTreasureCache generalTreasureCache;
    @Autowired
    private IStoreHouseDao storeHouseDao;
    @Autowired
    private IRankService rankService;
    @Autowired
    private TechEffectCache techEffectCache;
    @Autowired
    private TechCache techcache;
    @Autowired
    private IPlayerTechDao playerTechDao;
    @Autowired
    private LoginRewardComboCache loginRewardComboCache;
    @Autowired
    private IDataGetter dataGetter;
    @Autowired
    private EquipCache equipCache;
    @Autowired
    private BattleDataCache battleDataCache;
    @Autowired
    private HourlyRewardCache hourlyRewardCache;
    @Autowired
    private ArmsGemCache armsGemCache;
    @Autowired
    private IStoreHouseService storeHouseService;
    @Autowired
    private IEquipService equipService;
    @Autowired
    private ISlaveholderDao slaveholderDao;
    @Autowired
    private IPlayerDragonDao playerDragonDao;
    @Autowired
    private IForceInfoDao forceInfoDao;
    @Autowired
    private IPlayerTicketsDao playerTicketsDao;
    @Autowired
    private IPlayerService playerService;
    @Autowired
    private IPlayerOfficeRelativeDao playerOfficeRelativeDao;
    @Autowired
    private HallsCache hallsCache;
    @Autowired
    private IOfficerTokenDao officerTokenDao;
    @Autowired
    private IEventService eventService;
    @Autowired
    private IJuBenService juBenService;
    @Autowired
    private IPlayerWeaponDao playerWeaponDao;
    @Autowired
    private SDataLoader dataLoader;
    @Autowired
    private IStoreHouseBakDao storeHouseBakDao;
    @Autowired
    private KingdomLvCache kingdomLvCache;
    @Autowired
    private KtTypeCache ktTypeCache;
    @Autowired
    private IPlayerExpandInfoDao playerExpandInfoDao;
    @Autowired
    private ITaskInitDao taskInitDao;
    
    @Override
    public byte[] handleBattleLeaveCommand(final int playerId, final String... params) {
        if (params == null || params.length != 2) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_GM_10003);
        }
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Override
    public byte[] handleBattleEndCommand(final int playerId, final String... params) {
        if (params == null || params.length != 1) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_GM_10003);
        }
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Override
    public byte[] handleBattleJoinCommand(final int playerId, final String... params) {
        if (params == null || params.length != 3) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_GM_10003);
        }
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Override
    public byte[] handleBattleCommand(final int playerId, final String... params) {
        if (params == null || params.length != 2) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_GM_10003);
        }
        final Armies attLegion = (Armies)this.armiesCache.get((Object)Integer.valueOf(params[0]));
        if (attLegion == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_GM_10003);
        }
        final Armies defLegion = (Armies)this.armiesCache.get((Object)Integer.valueOf(params[1]));
        if (defLegion == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_GM_10003);
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("interface", "battle@createBattle");
        doc.createElement("attLegionId", params[0]);
        doc.createElement("defLegionId", params[1]);
        doc.endObject();
        Players.push(playerId, PushCommand.PUSH_GM_BATTLE_DOBATTLE, doc.toByte());
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Transactional
    @Override
    public byte[] handleWoodCommand(final int playerId, final String... params) {
        final int[] result = this.getPlayerAttributeCommandParams(params);
        if (result == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_GM_10003);
        }
        if (1 == result[0]) {
            this.playerResourceDao.addWoodIgnoreMax(playerId, result[1], "GM\u6307\u4ee4", true);
        }
        else if (-1 == result[0]) {
            this.playerResourceDao.consumeWood(playerId, result[1], "GM\u6307\u4ee4");
        }
        else if (result[0] == 0 && result[1] >= 0) {
            this.playerResourceDao.setWood(playerId, result[1], "GM\u6307\u4ee4");
        }
        EventListener.fireEvent(new CommonEvent(7, playerId));
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Transactional
    @Override
    public byte[] handleFoodCommand(final int playerId, final String... params) {
        final int[] result = this.getPlayerAttributeCommandParams(params);
        if (result == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_GM_10003);
        }
        if (1 == result[0]) {
            this.playerResourceDao.addFoodIgnoreMax(playerId, result[1], "GM\u6307\u4ee4");
        }
        else if (-1 == result[0]) {
            this.playerResourceDao.consumeFood(playerId, result[1], "GM\u6307\u4ee4");
        }
        else if (result[0] == 0 && result[1] >= 0) {
            this.playerResourceDao.setFood(playerId, result[1], "GM\u6307\u4ee4");
        }
        EventListener.fireEvent(new CommonEvent(8, playerId));
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Transactional
    @Override
    public byte[] handleIronCommand(final int playerId, final String... params) {
        final int[] result = this.getPlayerAttributeCommandParams(params);
        if (result == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_GM_10003);
        }
        if (1 == result[0]) {
            this.playerResourceDao.addIronIgnoreMax(playerId, result[1], "GM\u6307\u4ee4", true);
        }
        else if (-1 == result[0]) {
            this.playerResourceDao.consumeIron(playerId, result[1], "GM\u6307\u4ee4");
        }
        else if (result[0] == 0 && result[1] >= 0) {
            this.playerResourceDao.setIron(playerId, result[1], "GM\u6307\u4ee4");
        }
        EventListener.fireEvent(new CommonEvent(12, playerId));
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Transactional
    @Override
    public byte[] handleTicketCommand(final int playerId, final String... params) {
        final int[] result = this.getPlayerAttributeCommandParams(params);
        if (result == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_GM_10003);
        }
        if (1 == result[0]) {
            this.playerTicketsDao.addTickets(playerId, result[1], "GM\u6307\u4ee4", false);
        }
        else if (-1 == result[0]) {
            this.playerTicketsDao.consumeTickets(playerId, result[1], "GM\u6307\u4ee4");
        }
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Transactional
    @Override
    public byte[] handleExploitCommand(final int playerId, final String... params) {
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Transactional
    @Override
    public byte[] handleArmyCommand(final PlayerDto playerDto, final String... params) {
        final int[] result = this.getPlayerAttributeCommandParams(params);
        if (result == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_GM_10003);
        }
        if (1 == result[0]) {
            this.playerGeneralMilitaryDao.addPlayerForces(playerDto.playerId, result[1]);
            this.generalService.sendGeneralMilitaryRecruitInfo(playerDto);
        }
        else {
            if (-1 == result[0]) {
                return JsonBuilder.getJson(State.FAIL, "\u6ca1\u6709\u652f\u6301\u51cf\u5175\u529b\uff0c\u56e0\u4e3a\u6bcf\u4e2a\u6b66\u5c06\u7684\u5175\u529b\u4e0d\u4e00\u6837\uff0c\u53ef\u80fd\u51fa\u73b0\u8d1f\u6570\uff0c\u8981\u51cf\u5c11\u5175\u529b\u7528\u7b49\u4e8e\u6216\u8005\u52a0\u8d1f\u6570\u5b9e\u73b0\u5373\u53ef");
            }
            if (result[0] == 0) {
                if (result[1] < 0) {
                    result[1] = 0;
                }
                this.playerGeneralMilitaryDao.setPlayerForces(playerDto.playerId, result[1] * 1000);
                this.generalService.sendGeneralMilitaryRecruitInfo(playerDto);
            }
        }
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Transactional
    @Override
    public byte[] handleCopperCommand(final int playerId, final String... params) {
        final int[] result = this.getPlayerAttributeCommandParams(params);
        if (result == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_GM_10003);
        }
        if (1 == result[0]) {
            this.playerResourceDao.addCopperIgnoreMax(playerId, result[1], "GM\u6307\u4ee4", true);
        }
        else if (-1 == result[0]) {
            this.playerResourceDao.consumeCopper(playerId, result[1], "GM\u6307\u4ee4");
        }
        else if (result[0] == 0 && result[1] >= 0) {
            this.playerResourceDao.setCopper(playerId, result[1], "GM\u6307\u4ee4");
        }
        EventListener.fireEvent(new CommonEvent(2, playerId));
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Transactional
    @Override
    public byte[] handleGoldCommand(final int playerId, final String... params) {
        final int[] result = this.getPlayerAttributeCommandParams(params);
        if (result == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_GM_10003);
        }
        final Player player = this.playerDao.read(playerId);
        if (1 == result[0]) {
            this.playerDao.addSysGold(player, result[1], "GM\u6307\u4ee4");
        }
        else if (-1 == result[0]) {
            this.playerDao.consumeGold(player, result[1], "GM\u6307\u4ee4");
        }
        else if (result[0] == 0) {
            player.setUserGold(0);
            player.setSysGold(result[1]);
            this.playerDao.setSysGold(player, result[1], "GM\u6307\u4ee4");
            this.playerDao.setUserGold(player, 0, "GM\u6307\u4ee4");
        }
        EventListener.fireEvent(new CommonEvent(1, playerId));
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Transactional
    @Override
    public byte[] handleLevelCommand(final int playerId, final String... params) {
        final int[] result = this.getPlayerAttributeCommandParams(params);
        if (result == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_GM_10003);
        }
        final Player player = this.playerDao.read(playerId);
        final int oldLevel = player.getPlayerLv();
        if (1 == result[0]) {
            if (result[1] > 0) {
                player.setPlayerLv(player.getPlayerLv() + result[1]);
            }
        }
        else if (-1 == result[0]) {
            if (result[1] > 0) {
                if (player.getPlayerLv() >= result[1]) {
                    player.setPlayerLv(player.getPlayerLv() - result[1]);
                }
                else {
                    player.setPlayerLv(0);
                }
            }
        }
        else if (result[0] == 0 && result[1] >= 0) {
            player.setPlayerLv(result[1]);
        }
        this.rankService.updatePlayerLv(playerId, player.getPlayerLv());
        for (int i = oldLevel; i < player.getPlayerLv(); ++i) {}
        EventListener.fireEvent(new CommonEvent(3, playerId));
        final int newLv = player.getPlayerLv();
        if (newLv > oldLevel) {
            this.juBenService.checkWorldDramaOpen(playerId, player.getPlayerLv(), true);
        }
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    private int[] getPlayerAttributeCommandParams(final String[] params) {
        final int[] result = new int[2];
        if (params == null || params.length != 2) {
            return null;
        }
        final String sign = params[0].trim();
        if ("+".equals(sign)) {
            result[0] = 1;
        }
        else if ("-".equals(sign)) {
            result[0] = -1;
        }
        else {
            if (!"=".equals(sign)) {
                return null;
            }
            result[0] = 0;
        }
        try {
            result[1] = Integer.valueOf(params[1].trim());
        }
        catch (NumberFormatException e) {
            return null;
        }
        return result;
    }
    
    @Transactional
    @Override
    public byte[] handleSdataCommand(final int playerId, final String... params) {
        Exception e1 = null;
        try {
            ICache[] caches;
            for (int length = (caches = this.caches).length, i = 0; i < length; ++i) {
                final ICache<?, ?> cache = caches[i];
                cache.reload();
            }
        }
        catch (Exception e2) {
            e1 = e2;
            return JsonBuilder.getJson(State.FAIL, "fail");
        }
        finally {
            if (e1 != null) {
                throw new RuntimeException(e1);
            }
        }
        if (e1 != null) {
            throw new RuntimeException(e1);
        }
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Transactional
    @Override
    public byte[] handleCreateBuildingCommand(final int playerId, final String... params) {
        final Date nowDate = new Date();
        for (final Building building : this.buildingCache.getModels()) {
            PlayerBuilding pb = this.buildingService.getPlayerBuilding(playerId, building.getId());
            if (pb != null) {
                continue;
            }
            pb = new PlayerBuilding();
            pb.setBuildingId(building.getId());
            pb.setState(0);
            pb.setOutputType(building.getType());
            pb.setLv(1);
            pb.setPlayerId(playerId);
            pb.setUpdateTime(nowDate);
            pb.setIsNew(0);
            pb.setAreaId((building.getType() >= 5 && building.getType() <= 8) ? 5 : ((int)building.getType()));
            pb.setEventId(0);
            pb.setSpeedUpNum(this.serialCache.get(building.getTimeT(), 1));
            this.playerBuildingDao.create(pb);
        }
        for (int i = 1; i <= 5; ++i) {
            this.buildingOutputCache.clearBase(playerId, i);
        }
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Transactional
    @Override
    public byte[] handleConsumeLvCommand(final int playerId, final String... params) {
        final int[] result = this.getPlayerAttributeCommandParams(params);
        if (result == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_GM_10003);
        }
        final int param = result[1];
        int maxLevel = 1;
        for (final Chargeitem chargeItem : this.chargeItemCache.getModels()) {
            if (chargeItem.getLv() > maxLevel) {
                maxLevel = chargeItem.getLv();
            }
        }
        if (param < 0 || param > maxLevel) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_GM_10004);
        }
        this.playerDao.updatePlayerConsumeLv(playerId, param);
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Transactional
    @Override
    public byte[] handleGetCommand(final int playerId, final String... params) {
        final Player player = this.playerDao.read(playerId);
        this.playerDao.addSysGold(player, 10000, "GM\u6307\u4ee4");
        EventListener.fireEvent(new CommonEvent(1, playerId));
        this.playerResourceDao.addWoodIgnoreMax(playerId, 10000.0, "GM\u6307\u4ee4", true);
        this.playerResourceDao.addFoodIgnoreMax(playerId, 10000.0, "GM\u6307\u4ee4");
        this.playerResourceDao.addIronIgnoreMax(playerId, 10000, "GM\u6307\u4ee4", true);
        this.playerResourceDao.addCopperIgnoreMax(playerId, 20000.0, "GM\u6307\u4ee4", true);
        this.playerDao.updatePlayerConsumeLv(playerId, 6);
        this.rankService.updatePlayerLv(playerId, 25);
        EventListener.fireEvent(new CommonEvent(3, playerId));
        final char[] ns = new char[6];
        for (int i = 0; i < 6; ++i) {
            ns[i] = '1';
        }
        final char[] function = new char[128];
        for (int j = 0; j < 128; ++j) {
            function[j] = '1';
        }
        this.playerAttributeDao.updateFunction(playerId, new String(function));
        this.playerService.afterOpenFunction(51, playerId);
        this.playerAttributeDao.setIsNewArea(playerId, new String(ns));
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Transactional
    @Override
    public byte[] handleKillCommand(final int playerId, final String... params) {
        if (params == null || params.length != 2) {
            return null;
        }
        final int powerId = Integer.valueOf(params[0].trim());
        final int pos = Integer.valueOf(params[1].trim());
        int id = 1;
        final LinkedList<Armies> armiesList = this.armiesCache.getArmiesByPowerId(powerId);
        for (final Armies armies : armiesList) {
            if (armies.getPos() == pos) {
                id = armies.getId();
                break;
            }
        }
        Armies armies = this.armiesCache.getNextArmies(id);
        final Armies curArmies = (Armies)this.armiesCache.get((Object)id);
        this.playerArmyDao.updateAttack(playerId, curArmies.getId(), 1, 1);
        if (armies != null) {
            this.playerArmyDao.updateAttackable(playerId, armies.getId(), 1);
        }
        else {
            final Power power = (Power)this.powerCache.get((Object)curArmies.getPowerId());
            final PlayerPower pp = this.playerPowerDao.getPlayerPower(playerId, power.getNextPower());
            if (pp != null && pp.getAttackable() != 1) {
                this.playerPowerDao.updateAttackable(playerId, power.getNextPower(), 1);
            }
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("powerId", powerId);
        doc.endObject();
        Players.push(playerId, PushCommand.PUSH_POWER, doc.toByte());
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Override
    public byte[] handleFunctionCommand(final int playerId, final String... params) {
        this.playerAttributeDao.updateFunction(playerId, "11101111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111");
        this.playerService.afterOpenFunction(51, playerId);
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Transactional
    @Override
    public byte[] handleGeneralCommand(final int playerId, final String... params) {
        int size = 0;
        if (params != null) {
            size = params.length;
        }
        final int num = this.playerGeneralMilitaryDao.getMilitaryNum(playerId);
        if (num >= 5) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_GENERAL_NUM_TOP);
        }
        int generalId = 0;
        int cityId = 0;
        final Player player = this.playerDao.read(playerId);
        if (size < 1) {
            final List<General> list = this.generalCache.getGeneralByQuality(WebUtil.nextInt(6), 2);
            final General general = list.get(WebUtil.nextInt(list.size()));
            if (general != null) {
                generalId = general.getId();
            }
            cityId = WorldCityCommon.nationMainCityIdMap.get(player.getForceId());
        }
        else if (size == 1) {
            generalId = Integer.valueOf(params[0].trim());
            cityId = WorldCityCommon.nationMainCityIdMap.get(player.getForceId());
        }
        else {
            generalId = Integer.valueOf(params[0].trim());
            cityId = Integer.valueOf(params[1].trim());
        }
        final General general2 = (General)this.generalCache.get((Object)generalId);
        if (general2 == null || general2.getType() != 2) {
            return JsonBuilder.getJson(State.FAIL, String.valueOf(LocalMessages.T_GM_10003) + generalId);
        }
        final WorldCity worldCity = (WorldCity)this.worldCityCache.get((Object)cityId);
        if (worldCity == null) {
            return JsonBuilder.getJson(State.FAIL, String.valueOf(LocalMessages.T_GM_10003) + cityId);
        }
        final City city = this.cityDao.read(cityId);
        if (city == null) {
            return JsonBuilder.getJson(State.FAIL, String.valueOf(LocalMessages.T_GM_10003) + cityId);
        }
        final PlayerGeneralMilitary pgmT = this.playerGeneralMilitaryDao.getMilitary(playerId, generalId);
        if (pgmT != null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.GENERAL_HAD_THIS_GENERAL);
        }
        final PlayerGeneralMilitary pgm = new PlayerGeneralMilitary();
        pgm.setPlayerId(playerId);
        pgm.setGeneralId(generalId);
        pgm.setLeader(0);
        pgm.setStrength(0);
        pgm.setForces((int)(Object)((C)this.cCache.get((Object)"General.Origin.Blood")).getValue());
        pgm.setLv(1);
        pgm.setExp(0L);
        pgm.setLocationId(cityId);
        pgm.setUpdateForcesTime(new Date());
        pgm.setState(0);
        pgm.setMorale(100);
        pgm.setAuto(0);
        pgm.setTacticEffect(0);
        pgm.setForceId(player.getForceId());
        final JuBenDto juBenDto = JuBenManager.getInstance().getByPid(playerId);
        if (juBenDto != null) {
            pgm.setJubenLoId(juBenDto.capital);
        }
        else {
            pgm.setJubenLoId(0);
        }
        this.playerGeneralMilitaryDao.create(pgm);
        this.generalService.sendGeneralMilitaryRecruitInfo(player, pgm, false);
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Transactional
    @Override
    public byte[] handleMoveCommand(final int playerId, final String... params) {
        if (params == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_GM_10003);
        }
        final String cityNameOrId = params[0].trim();
        return this.cityService.moveByCityName(playerId, cityNameOrId);
    }
    
    @Transactional
    @Override
    public byte[] handleCopyCommand(final int playerId, final String... params) {
        if (params == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_GM_10003);
        }
        final int num = Integer.valueOf(params[0].trim());
        if (num <= 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_GM_10003);
        }
        return this.copyPlayerService.copyPlayerTable(playerId, num);
    }
    
    @Transactional
    @Override
    public byte[] handleGeneralLvCommand(final int playerId, final String... params) {
        final int[] result = this.getPlayerAttributeCommandParams(params);
        if (result == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_GM_10003);
        }
        if (1 == result[0]) {
            if (result[1] > 0) {
                this.playerGeneralMilitaryDao.updateGlv(playerId, result[1]);
            }
        }
        else {
            if (-1 == result[0]) {
                return JsonBuilder.getJson(State.FAIL, "\u4e0d\u652f\u6301\u6b66\u5c06\u7b49\u7ea7\u51cf\u5c11\u6307\u4ee4\uff0c\u53ef\u4ee5\u901a\u8fc7\u52a0\u4e0a\u8d1f\u7684\u6b66\u5c06\u7b49\u7ea7\u5b8c\u6210");
            }
            if (result[0] == 0 && result[1] >= 0) {
                this.playerGeneralMilitaryDao.SetGlv(playerId, result[1]);
            }
        }
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Transactional
    @Override
    public byte[] handleTaskCommand(final PlayerDto PlayerDto, final String... params) {
        if (params == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_GM_10003);
        }
        final int num = Integer.valueOf(params[0].trim());
        if (num <= 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_GM_10003);
        }
        return this.autoPlayerTask.autoTask(PlayerDto, num);
    }
    
    @Transactional
    @Override
    public byte[] handlePayCommand(final PlayerDto playerDto, final Request request, final String... params) {
        if (params == null || params.length < 2) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_GM_10003);
        }
        final String orderId = "gcld_test_" + params[0].trim();
        final int num = Integer.valueOf(params[1].trim());
        if (num <= 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_GM_10003);
        }
        if (this.playerPayDao.containsOrderId(orderId, playerDto.yx)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.BACK_STAGE_REPAY_HAVE_RECORD);
        }
        return this.payService.pay(orderId, playerDto.playerId, playerDto.userId, playerDto.yx, num, playerDto.yxSource, request);
    }
    
    @Override
    public byte[] handleHelpCommand(final PlayerDto playerDto, final String... params) {
        final StringBuffer sb = new StringBuffer();
        final Command[] cmds = Command.values();
        sb.append("\n");
        Command[] array;
        for (int length = (array = cmds).length, i = 0; i < length; ++i) {
            final Command cmd = array[i];
            sb.append(cmd.getIntro());
            sb.append("\n");
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("from", playerDto.playerName);
        doc.createElement("to", playerDto.playerName);
        doc.createElement("type", "ONE2ONE");
        doc.createElement("msg", sb.toString());
        doc.endObject();
        Players.push(playerDto.playerId, PushCommand.PUSH_CHAT_SEND, doc.toByte());
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Override
    public byte[] handleCivilLvCommand(final PlayerDto playerDto, final String... params) {
        final int[] result = this.getPlayerAttributeCommandParams(params);
        if (result == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_GM_10003);
        }
        if (1 == result[0]) {
            if (result[1] > 0) {
                return JsonBuilder.getJson(State.FAIL, "\u4e0d\u652f\u6301\u6587\u5b98\u7b49\u7ea7\u589e\u52a0\u6307\u4ee4");
            }
        }
        else {
            if (-1 == result[0]) {
                return JsonBuilder.getJson(State.FAIL, "\u4e0d\u652f\u6301\u6b66\u5c06\u6587\u5b98\u7b49\u7ea7\u51cf\u5c11\u6307\u4ee4");
            }
            if (result[0] == 0 && result[1] >= 0) {
                this.playerGeneralCivilDao.setCivilLv(playerDto.playerId, result[1]);
            }
        }
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Transactional
    @Override
    public byte[] handleOpenMistCommand(final PlayerDto playerDto, final String[] params) {
        final List<City> list = this.cityDao.getModels();
        final StringBuilder sb = new StringBuilder();
        for (final City city : list) {
            sb.append(city.getId());
            sb.append(",");
        }
        this.playerWorldDao.updateAttInfo(playerDto.playerId, sb.toString(), null);
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Transactional
    @Override
    public byte[] handleExpCommand(final PlayerDto playerDto, final String[] params) {
        final int[] result = this.getPlayerAttributeCommandParams(params);
        if (result == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_GM_10003);
        }
        if (1 == result[0]) {
            return JsonBuilder.getJson(State.FAIL, "\u4e0d\u652f\u6301\u89d2\u8272\u7ecf\u9a8c\u589e\u52a0\u6307\u4ee4\uff0c\u8bf7\u4f7f\u7528 = \u9009\u9879\u6307\u5b9a\u89d2\u8272\u7ecf\u9a8c\u503c");
        }
        if (-1 == result[0]) {
            return JsonBuilder.getJson(State.FAIL, "\u4e0d\u652f\u6301\u89d2\u8272\u7ecf\u9a8c\u51cf\u5c11\u6307\u4ee4\uff0c\u8bf7\u4f7f\u7528 = \u9009\u9879\u6307\u5b9a\u89d2\u8272\u7ecf\u9a8c\u503c");
        }
        if (result[0] == 0 && result[1] >= 0) {
            this.playerResourceDao.setExp(playerDto.playerId, result[1], "GM\u6307\u4ee4\u8bbe\u7f6e\u7ecf\u9a8c\u503c", 0L);
        }
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Override
    public byte[] handleStoreCommand(final PlayerDto playerDto, final String[] params) {
        final int[] result = this.getPlayerAttributeCommandParams(params);
        if (result == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_GM_10003);
        }
        if (1 == result[0]) {
            return JsonBuilder.getJson(State.FAIL, "\u4e0d\u652f\u6301\u4ed3\u5e93\u589e\u52a0\u6307\u4ee4\uff0c\u8bf7\u4f7f\u7528 = \u9009\u9879\u6307\u5b9a\u89d2\u8272\u4ed3\u5e93\u6570");
        }
        if (-1 == result[0]) {
            return JsonBuilder.getJson(State.FAIL, "\u4e0d\u652f\u6301\u4ed3\u5e93\u51cf\u5c11\u6307\u4ee4\uff0c\u8bf7\u4f7f\u7528 = \u9009\u9879\u6307\u5b9a\u89d2\u8272\u4ed3\u5e93\u6570");
        }
        if (result[0] == 0 && result[1] >= 0) {
            this.playerAttributeDao.setMaxStoreNum(playerDto.playerId, result[1]);
        }
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Override
    public byte[] handleResourceCommand(final PlayerDto playerDto, final String[] params) {
        final int[] result = this.getPlayerAttributeCommandParams(params);
        if (result == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_GM_10003);
        }
        final int playerId = playerDto.playerId;
        if (1 == result[0]) {
            return JsonBuilder.getJson(State.FAIL, "\u4e0d\u652f\u8d44\u6e90\u589e\u52a0\u6307\u4ee4\uff0c\u8bf7\u4f7f\u7528 = \u9009\u9879\u6307\u5b9a\u89d2\u8272\u8d44\u6e90\u6570");
        }
        if (-1 == result[0]) {
            return JsonBuilder.getJson(State.FAIL, "\u4e0d\u652f\u6301\u8d44\u6e90\u51cf\u5c11\u6307\u4ee4\uff0c\u8bf7\u4f7f\u7528 = \u9009\u9879\u6307\u5b9a\u89d2\u8272\u8d44\u6e90\u6570");
        }
        if (result[0] == 0 && result[1] >= 0) {
            this.playerResourceDao.setCopper(playerId, result[1], "GM\u6307\u4ee4");
            this.playerResourceDao.setWood(playerId, result[1], "GM\u6307\u4ee4");
            this.playerResourceDao.setFood(playerId, result[1], "GM\u6307\u4ee4");
            this.playerResourceDao.setIron(playerId, result[1], "GM\u6307\u4ee4");
        }
        EventListener.fireEvent(new CommonEvent(2, playerId));
        EventListener.fireEvent(new CommonEvent(7, playerId));
        EventListener.fireEvent(new CommonEvent(8, playerId));
        EventListener.fireEvent(new CommonEvent(12, playerId));
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Override
    public byte[] handleSetTaskCommand(final PlayerDto playerDto, final String[] params) {
        final int[] result = this.getPlayerAttributeCommandParams(params);
        if (result == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_GM_10003);
        }
        final int playerId = playerDto.playerId;
        if (1 == result[0]) {
            return JsonBuilder.getJson(State.FAIL, "\u4e0d\u652f\u4efb\u52a1id\u589e\u52a0\u6307\u4ee4\uff0c\u8bf7\u4f7f\u7528 = \u9009\u9879\u6307\u5b9a\u4efb\u52a1id");
        }
        if (-1 == result[0]) {
            return JsonBuilder.getJson(State.FAIL, "\u4e0d\u652f\u6301\u4efb\u52a1id\u51cf\u5c11\u6307\u4ee4\uff0c\u8bf7\u4f7f\u7528 = \u9009\u9879\u6307\u5b9a\u4efb\u52a1id");
        }
        if (result[0] == 0 && result[1] >= 0) {
            this.playerTaskDao.resetMainTask(playerId, result[1]);
        }
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Override
    public byte[] handleCivilCommand(final PlayerDto playerDto, final String[] params) {
        if (params == null || params.length != 1) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_GM_10003);
        }
        final int generalId = Integer.parseInt(params[0]);
        final int playerId = playerDto.playerId;
        if (generalId <= 0) {
            return JsonBuilder.getJson(State.FAIL, "\u6587\u5b98id\u4e0d\u80fd\u4e3a\u975e\u6b63\u6570");
        }
        final General general = (General)this.generalCache.get((Object)generalId);
        if (general == null) {
            return JsonBuilder.getJson(State.FAIL, "\u4e0d\u5b58\u5728\u8be5\u6587\u5b98");
        }
        if (general.getType() != 1) {
            return JsonBuilder.getJson(State.FAIL, "\u4e0d\u662f\u6587\u5b98\uff0c\u662f\u6b66\u5c06");
        }
        final Player player = this.playerDao.read(playerId);
        final PlayerGeneralCivil pg = this.playerGeneralCivilDao.getCivil(playerId, generalId);
        if (pg != null) {
            return JsonBuilder.getJson(State.FAIL, "\u5df2\u7ecf\u62db\u52df\u4e86\u8be5\u6587\u5b98");
        }
        final List<PlayerGeneral> retiredGeneralList = this.playerGeneralDao.getGeneralList(player.getPlayerId());
        PlayerGeneral retiredGeneral = null;
        for (final PlayerGeneral tempGeneral : retiredGeneralList) {
            if (tempGeneral.getGeneralId().equals(general.getId())) {
                retiredGeneral = tempGeneral;
                break;
            }
        }
        final Date date = new Date();
        final PlayerGeneralCivil pgc = new PlayerGeneralCivil();
        pgc.setPlayerId(playerId);
        pgc.setGeneralId(generalId);
        if (retiredGeneral != null) {
            pgc.setIntel(retiredGeneral.getIntel());
            pgc.setPolitics(retiredGeneral.getPolitics());
            pgc.setLv(retiredGeneral.getLv());
            pgc.setExp(retiredGeneral.getExp());
            this.playerGeneralDao.deleteById(retiredGeneral.getVId());
        }
        else {
            pgc.setIntel(0);
            pgc.setPolitics(0);
            pgc.setLv(1);
            pgc.setExp(0L);
        }
        pgc.setOwner(0);
        pgc.setUpdateTime(date);
        this.playerGeneralCivilDao.create(pgc);
        TaskMessageHelper.sendOfficerTaskMessage(playerId, 1);
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Override
    public byte[] handleTokenCommand(final PlayerDto playerDto, final String[] params) {
        final int[] result = this.getPlayerAttributeCommandParams(params);
        if (result == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_GM_10003);
        }
        final int playerId = playerDto.playerId;
        if (1 == result[0]) {
            return JsonBuilder.getJson(State.FAIL, "\u4e0d\u652f\u52df\u5175\u4ee4\u589e\u52a0\u6307\u4ee4\uff0c\u8bf7\u4f7f\u7528 = \u9009\u9879\u6307\u5b9a\u52df\u5175\u4ee4");
        }
        if (-1 == result[0]) {
            return JsonBuilder.getJson(State.FAIL, "\u4e0d\u652f\u6301\u52df\u5175\u4ee4\u51cf\u5c11\u6307\u4ee4\uff0c\u8bf7\u4f7f\u7528 = \u9009\u9879\u6307\u5b9a\u52df\u5175\u4ee4");
        }
        if (result[0] == 0 && result[1] >= 0) {
            this.playerAttributeDao.setRecruitToken(playerId, result[1]);
        }
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Override
    public byte[] handleSlaveCommand(final PlayerDto playerDto, final String[] params) {
        if (params == null || StringUtils.isEmpty(params[0])) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_GM_10003);
        }
        final int playerId = playerDto.playerId;
        char[] cs = this.playerAttributeDao.getFunctionId(playerId).toCharArray();
        if (cs[52] != '1') {
            return JsonBuilder.getJson(State.FAIL, "\u4f60\u5974\u96b6\u529f\u80fd\u6ca1\u6709\u5f00\u542f\u54e6\uff0c\u4e0d\u53ef\u4ee5\u6293\u5974\u96b6");
        }
        final String slaveName = params[0];
        final Player player = this.playerDao.getPlayerByNameAndYx(slaveName, playerDto.yx);
        if (player == null) {
            return JsonBuilder.getJson(State.FAIL, "\u5974\u96b6\u89d2\u8272\u4e0d\u5b58\u5728");
        }
        if (playerDto.forceId == player.getForceId()) {
            return JsonBuilder.getJson(State.FAIL, "\u4e0d\u53ef\u4ee5\u6293\u672c\u56fd\u89d2\u8272\u6210\u4e3a\u5974\u96b6");
        }
        if (this.playerSlaveDao.getEmptyCellSize(playerId) <= 0) {
            return JsonBuilder.getJson(State.FAIL, "\u4f60\u6ca1\u6709\u7a7a\u7262\u623f,\u8bf7\u5148\u4e70\u7262\u623f\u5427");
        }
        cs = this.playerAttributeDao.getFunctionId(player.getPlayerId()).toCharArray();
        if (cs[52] != '1') {
            return JsonBuilder.getJson(State.FAIL, "\u4f60\u8981\u6293\u7684\u89d2\u8272\u5974\u96b6\u529f\u80fd\u8fd8\u6ca1\u6709\u5f00\u653e\u54e6");
        }
        final Map<Integer, Integer> win = new HashMap<Integer, Integer>();
        final Map<Integer, Integer> lose = new HashMap<Integer, Integer>();
        win.put(playerId, this.playerDao.getPlayerLv(playerId));
        lose.put(player.getPlayerId(), player.getPlayerLv());
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Override
    public byte[] handleResetSlaveCommand(final PlayerDto playerDto, final String[] params) {
        this.slaveService.resetSlaveSystem(playerDto.playerId);
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Override
    public byte[] handleStopWorkCommand(final PlayerDto playerDto, final String[] params) {
        this.buildingService.constructionComplete(playerDto.playerId);
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Override
    public byte[] handleStopAutoCommand(final PlayerDto playerDto, final String[] params) {
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.appendJson(this.buildingService.stopAutoUpBuilding(playerDto.playerId));
        doc.endObject();
        Players.push(playerDto.playerId, PushCommand.PUSH_BUILDING_UPGRADE, doc.toByte());
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Override
    public byte[] handleTrickChatCommand(final PlayerDto playerDto, final String[] params) {
        if (params == null || StringUtils.isEmpty(params[0])) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_GM_10003);
        }
        this.chatUtil.sendTrickChat(playerDto.playerId, params[0]);
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Override
    public byte[] handleWHCCityChatCommand(final PlayerDto playerDto, final String[] params) {
        if (params == null || StringUtils.isEmpty(params[0]) || StringUtils.isEmpty(params[1])) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_GM_10003);
        }
        final int forceId2 = Integer.parseInt(params[0]);
        if (forceId2 < 1 || forceId2 > 3) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_GM_10003);
        }
        if (forceId2 == playerDto.forceId) {
            return JsonBuilder.getJson(State.FAIL, "\u4e0d\u80fd\u591f\u662f\u672c\u56fd\u52bf\u529b");
        }
        this.chatUtil.sendWinHostileCountryCityChat(playerDto.forceId, forceId2, params[1], true, playerDto.playerName);
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Override
    public byte[] handleLCCityChatCommand(final PlayerDto playerDto, final String[] params) {
        if (params == null || StringUtils.isEmpty(params[0]) || StringUtils.isEmpty(params[1])) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_GM_10003);
        }
        final int forceId2 = Integer.parseInt(params[0]);
        if (forceId2 < 1 || forceId2 > 3) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_GM_10003);
        }
        if (forceId2 == playerDto.forceId) {
            return JsonBuilder.getJson(State.FAIL, "\u4e0d\u80fd\u591f\u662f\u672c\u56fd\u52bf\u529b");
        }
        this.chatUtil.sendLoseCountryCityChat(playerDto.forceId, forceId2, params[1], true, playerDto.playerName);
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Override
    public byte[] handleLGLPlaceChatCommand(final PlayerDto playerDto, final String[] params) {
        if (params == null || StringUtils.isEmpty(params[0]) || StringUtils.isEmpty(params[1]) || StringUtils.isEmpty(params[2])) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_GM_10003);
        }
        final int forceId2 = Integer.parseInt(params[2]);
        if (forceId2 < 1 || forceId2 > 3) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_GM_10003);
        }
        if (forceId2 == playerDto.forceId) {
            return JsonBuilder.getJson(State.FAIL, "\u4e0d\u80fd\u591f\u662f\u672c\u56fd\u52bf\u529b");
        }
        this.chatUtil.sendGeneralLosePlaceChat(playerDto.playerId, params[0], params[1], forceId2);
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Override
    public byte[] handleLGWPlaceChatCommand(final PlayerDto playerDto, final String[] params) {
        if (params == null || StringUtils.isEmpty(params[0]) || StringUtils.isEmpty(params[1]) || StringUtils.isEmpty(params[2])) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_GM_10003);
        }
        final int forceId2 = Integer.parseInt(params[2]);
        if (forceId2 < 1 || forceId2 > 3) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_GM_10003);
        }
        if (forceId2 == playerDto.forceId) {
            return JsonBuilder.getJson(State.FAIL, "\u4e0d\u80fd\u591f\u662f\u672c\u56fd\u52bf\u529b");
        }
        this.chatUtil.sendGeneralWinPlaceChat(playerDto.playerId, params[0], 1, params[1], forceId2);
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Override
    public byte[] handleRankChatCommand(final PlayerDto playerDto, final String[] params) {
        if (params == null || StringUtils.isEmpty(params[0])) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_GM_10003);
        }
        this.chatUtil.sendRankChat(playerDto.playerId, 0, new ChatLink(1, String.valueOf(8) + params[0]));
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Override
    public byte[] handleWinNPCCommand(final PlayerDto playerDto, final String[] params) {
        if (params == null || StringUtils.isEmpty(params[0])) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_GM_10003);
        }
        this.broadCastUtil.sendWinNPCBroadCast(playerDto.playerId, params[0]);
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Override
    public byte[] handlePassBonusCommand(final PlayerDto playerDto, final String[] params) {
        if (params == null || StringUtils.isEmpty(params[0])) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_GM_10003);
        }
        this.broadCastUtil.sendPassBonusBroadCast(playerDto.playerId, params[0]);
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Override
    public byte[] handleCNeutralPlaceCommand(final PlayerDto playerDto, final String[] params) {
        if (params == null || StringUtils.isEmpty(params[0])) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_GM_10003);
        }
        this.broadCastUtil.sendCaptureNeutralPlaceBroadCast(playerDto.forceId, params[0]);
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Override
    public byte[] handleWinHCCommonPlaceCommand(final PlayerDto playerDto, final String[] params) {
        if (params == null || StringUtils.isEmpty(params[0]) || StringUtils.isEmpty(params[1])) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_GM_10003);
        }
        final int forceId = Integer.parseInt(params[0]);
        if (forceId == playerDto.forceId) {
            return JsonBuilder.getJson(State.FAIL, "\u654c\u56fd\u52bf\u529b\u4e0d\u80fd\u591f\u548c\u672c\u56fd\u76f8\u540c");
        }
        this.broadCastUtil.sendWinHCCommonPlaceBroadCast(playerDto.forceId, forceId, params[1]);
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Override
    public byte[] handleLCommonPlaceCommand(final PlayerDto playerDto, final String[] params) {
        if (params == null || StringUtils.isEmpty(params[0]) || StringUtils.isEmpty(params[1])) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_GM_10003);
        }
        final int forceId = Integer.parseInt(params[0]);
        if (forceId == playerDto.forceId) {
            return JsonBuilder.getJson(State.FAIL, "\u654c\u56fd\u52bf\u529b\u4e0d\u80fd\u591f\u548c\u672c\u56fd\u76f8\u540c");
        }
        this.broadCastUtil.sendLoseCommonPlaceBroadCast(playerDto.forceId, forceId, params[1]);
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Override
    public byte[] handleLPlaceCHCommand(final PlayerDto playerDto, final String[] params) {
        if (params == null || StringUtils.isEmpty(params[0]) || StringUtils.isEmpty(params[1])) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_GM_10003);
        }
        final int forceId = Integer.parseInt(params[0]);
        if (forceId == playerDto.forceId) {
            return JsonBuilder.getJson(State.FAIL, "\u6218\u80dc\u56fd\u52bf\u529b\u4e0d\u80fd\u591f\u548c\u672c\u56fd\u76f8\u540c");
        }
        this.broadCastUtil.sendPlaceChangeHandsBroadCast(playerDto.forceId, forceId, params[1]);
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Override
    public byte[] handleReOpenBonusCommand(final PlayerDto playerDto, final String[] params) {
        if (params == null || StringUtils.isEmpty(params[0]) || StringUtils.isEmpty(params[1])) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_GM_10003);
        }
        final int powerId = Integer.parseInt(params[0]);
        this.broadCastUtil.sendReOpenBonusBroadCast(playerDto.playerId, powerId, params[1]);
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Override
    public byte[] handleCNPCommand(final PlayerDto playerDto, final String[] params) {
        int forceId = playerDto.forceId;
        if (params != null && StringUtils.isNotBlank(params[0])) {
            forceId = Integer.parseInt(params[0]);
            if (forceId < 1 || forceId > 3) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_GM_10003);
            }
        }
        final int P = this.cityDataCache.getCNPNum(forceId);
        this.chatService.sendSystemChat("SYS2ONE", playerDto.playerId, 0, ColorUtil.getForceMsg(forceId, String.valueOf(WebUtil.getForceName(forceId)) + "\u56fd\u529b\u503c\u4e3a\uff1a" + P), null);
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Override
    public byte[] handleOfficialOCommand(final PlayerDto playerDto, final String[] params) {
        this.chatService.sendSystemChat("SYS2ONE", playerDto.playerId, 0, "\u5b98\u804c\u4ea7\u51fa\u4e3a\uff1a0", null);
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Override
    public byte[] handleExeTimesCommand(final int playerId, final String[] params) {
        final int times = Integer.parseInt(params[0]);
        int t5 = 0;
        int t6 = 0;
        int t7 = 0;
        int t8 = 0;
        int t9 = 0;
        int t10 = 0;
        int total = times;
        int t11 = 0;
        while (total > 0) {
            final LoginRewardCombo lrc = this.loginRewardComboCache.getProb(WebUtil.nextDouble());
            final ITaskReward reward = lrc.getBaseReward();
            final Map<Integer, Reward> rewardMap = reward.getReward(Players.getPlayer(playerId), this.dataGetter, null);
            for (final Reward reward2 : rewardMap.values()) {
                if (reward2.getType() == 19) {
                    if (5 == reward2.getNum()) {
                        ++t5;
                    }
                    else if (10 == reward2.getNum()) {
                        ++t6;
                    }
                    else if (20 == reward2.getNum()) {
                        ++t7;
                    }
                    else if (25 == reward2.getNum()) {
                        ++t8;
                    }
                    else if (50 == reward2.getNum()) {
                        ++t9;
                    }
                    else if (125 == reward2.getNum()) {
                        ++t10;
                    }
                    else {
                        System.out.println(reward2.getNum());
                        ++t11;
                    }
                }
            }
            --total;
        }
        final String msg = "\u6267\u884c\u603b\u6b21\u6570:" + times + ":5\u91d1\u5e01\u51fa\u73b0\u7684\u6b21\u6570:" + t5 + ":10\u91d1\u5e01\u51fa\u73b0\u7684\u6b21\u6570:" + t7 + ":25\u91d1\u5e01\u51fa\u73b0\u7684\u6b21\u6570:" + t7 + ":25\u91d1\u5e01\u51fa\u73b0\u7684\u6b21\u6570:" + t8 + ":50\u91d1\u5e01\u51fa\u73b0\u7684\u6b21\u6570:" + t9 + ":125\u91d1\u5e01\u51fa\u73b0\u7684\u6b21\u6570:" + t10 + ":5\u91d1\u5e01\u51fa\u73b0\u7684\u6982\u7387:" + t5 * 1.0 / times + ":10\u91d1\u5e01\u51fa\u73b0\u7684\u6982\u7387:" + t6 * 1.0 / times + ":20\u91d1\u5e01\u51fa\u73b0\u7684\u6982\u7387:" + t7 * 1.0 / times + ":25\u91d1\u5e01\u51fa\u73b0\u7684\u6982\u7387:" + t8 * 1.0 / times + ":50\u91d1\u5e01\u51fa\u73b0\u7684\u6982\u7387:" + t9 * 1.0 / times + ":125\u91d1\u5e01\u51fa\u73b0\u7684\u6982\u7387:" + t10 * 1.0 / times;
        this.chatService.sendSystemChat("SYS2ONE", playerId, 0, msg, null);
        System.out.println("\u6267\u884c\u603b\u6b21\u6570\uff1a" + times + "\n" + ":5:" + t5 * 1.0 / times + "\n" + ":10:" + t6 * 1.0 / times + "\n" + ":20:" + t7 * 1.0 / times + "\n" + ":25:" + t8 * 1.0 / times + "\n" + ":50:" + t9 * 1.0 / times + "\n" + ":125:" + t10 * 1.0 / times + "\n");
        final int test_total = t5 + t6 + t7 + t8 + t9 + t10 + t11;
        System.out.println("Total:" + test_total * 1.0 / times);
        return JsonBuilder.getJson(State.SUCCESS, msg);
    }
    
    @Override
    public byte[] handleGTreasureCommand(final PlayerDto playerDto, final String[] params) {
        final List<GeneralTreasure> list = this.generalTreasureCache.getModels();
        final GeneralTreasure generalTreasure = list.get(WebUtil.nextInt(list.size()));
        final StringBuilder attb = new StringBuilder();
        if (generalTreasure.getType() == 1) {
            attb.append(generalTreasure.getIntMin() + WebUtil.nextInt(generalTreasure.getIntMax() + 1 - generalTreasure.getIntMin())).append(",").append(generalTreasure.getPolMin() + WebUtil.nextInt(generalTreasure.getPolMax() + 1 - generalTreasure.getPolMin()));
        }
        else {
            attb.append(generalTreasure.getLeaMin() + WebUtil.nextInt(generalTreasure.getLeaMax() + 1 - generalTreasure.getLeaMin())).append(",").append(generalTreasure.getStrMin() + WebUtil.nextInt(generalTreasure.getStrMax() + 1 - generalTreasure.getStrMin()));
        }
        final StoreHouse sh = new StoreHouse();
        sh.setType(3);
        sh.setGoodsType(generalTreasure.getType());
        sh.setItemId(generalTreasure.getId());
        sh.setLv(0);
        sh.setPlayerId(playerDto.playerId);
        sh.setOwner(0);
        sh.setQuality(generalTreasure.getQuality());
        sh.setGemId(0);
        sh.setAttribute(attb.toString());
        sh.setNum(1);
        sh.setState(0);
        sh.setRefreshAttribute("");
        sh.setQuenchingTimes(0);
        sh.setBindExpireTime(0L);
        sh.setMarkId(0);
        this.storeHouseDao.create(sh);
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Override
    public byte[] handleBaseCommand(final PlayerDto playerDto, final String[] params) {
        if (params == null || params.length < 1) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_GM_10003);
        }
        final int result = Integer.parseInt(params[0]);
        if (result < 1 || result > 5) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_GM_10003);
        }
        final int output = this.buildingOutputCache.getBuildingsOutputBase(playerDto.playerId, result);
        this.chatService.sendSystemChat("SYS2ONE", playerDto.playerId, 0, "\u57fa\u672c\u4ea7\u51fa(base)\uff1a" + output, null);
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Override
    public byte[] handleCheckFunctionId(final int playerId, final String[] params) {
        if (params == null || params.length != 1) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_GM_10003);
        }
        int index;
        try {
            index = Integer.parseInt(params[0]);
        }
        catch (NumberFormatException e) {
            this.chatService.sendSystemChat("SYS2ONE", playerId, 0, "\u9519\u8bef\u7684\u6307\u4ee4\u53c2\u6570\uff1a\u53c2\u6570\u5fc5\u987b\u4e3a\u6574\u6570", null);
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_GM_10003);
        }
        if (index <= 0 || index >= 128) {
            return JsonBuilder.getJson(State.FAIL, "\u6570\u7ec4\u8d8a\u754c\uff01");
        }
        final char[] functionId = this.playerAttributeDao.read(playerId).getFunctionId().toCharArray();
        String MSG;
        if ('1' == functionId[index]) {
            MSG = "\u7b2c" + index + "\u4f4d\u529f\u80fd\u5df2\u5f00\u542f";
        }
        else if ('0' == functionId[index]) {
            MSG = "\u7b2c" + index + "\u4f4d\u529f\u80fd\u5df2\u5173\u95ed";
        }
        else {
            MSG = "\u9519\u8bef\u7684functionId\u53c2\u6570\uff0c\u53ea\u80fd\u4e3a0\u6216\u80051";
        }
        this.chatService.sendSystemChat("SYS2ONE", playerId, 0, MSG, null);
        return JsonBuilder.getJson(State.SUCCESS, MSG);
    }
    
    @Override
    public byte[] handleSetFunctionId(final int playerId, final String[] params) {
        char[] functionId = this.playerAttributeDao.read(playerId).getFunctionId().toCharArray();
        final int[] result = new int[2];
        if (params == null || params.length != 2) {
            this.chatService.sendSystemChat("SYS2ONE", playerId, 0, "\u9519\u8bef\u7684\u6307\u4ee4\u53c2\u6570\uff1a\u53c2\u6570\u7684\u4e2a\u6570\u53ea\u80fd\u4e3a2", null);
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_GM_10003);
        }
        try {
            result[0] = Integer.valueOf(params[0].trim());
            result[1] = Integer.valueOf(params[1].trim());
        }
        catch (NumberFormatException e) {
            this.chatService.sendSystemChat("SYS2ONE", playerId, 0, "\u9519\u8bef\u7684\u6307\u4ee4\u53c2\u6570\uff1a\u540e\u4e24\u4e2a\u53c2\u6570\u5fc5\u987b\u4e3a\u6574\u6570", null);
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_GM_10003);
        }
        if (result[0] < 1 || result[0] >= 128) {
            this.chatService.sendSystemChat("SYS2ONE", playerId, 0, "\u9519\u8bef\u7684\u6307\u4ee4\u53c2\u6570\uff1a\u7b2c\u4e00\u4e2a\u53c2\u6570\u7684\u503c\u8d8a\u754c", null);
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_GM_10003);
        }
        final int index = result[0];
        this.chatService.sendSystemChat("SYS2ONE", playerId, 0, "\u4fee\u6539\u524d\u7684\u503c\u4e3a:" + functionId[index], null);
        String MSG;
        if (result[1] == 0) {
            functionId[index] = '0';
            MSG = "\u7b2c" + index + "\u4f4dfunctionId\u5df2\u5173\u95ed\u3002";
        }
        else {
            if (result[1] != 1) {
                this.chatService.sendSystemChat("SYS2ONE", playerId, 0, "\u9519\u8bef\u7684\u6307\u4ee4\u53c2\u6570\uff1a\u7b2c\u4e8c\u4e2a\u53c2\u6570\u7684\u503c\u53ea\u80fd\u4e3a0\u6216\u80051", null);
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_GM_10003);
            }
            functionId[index] = '1';
            MSG = "\u7b2c" + index + "\u4f4dfunctionId\u5df2\u5f00\u542f\u3002";
        }
        this.playerAttributeDao.updateFunction(playerId, new String(functionId));
        if (result[1] == 1) {
            this.playerService.afterOpenFunction(functionId[index], playerId);
        }
        functionId = this.playerAttributeDao.getFunctionId(playerId).toCharArray();
        this.chatService.sendSystemChat("SYS2ONE", playerId, 0, String.valueOf(MSG) + "\u4fee\u6539\u540e\u7684\u503c\u4e3a\uff1a" + functionId[index], null);
        return JsonBuilder.getJson(State.SUCCESS, MSG);
    }
    
    @Override
    public byte[] handleOfficersCommand(final PlayerDto playerDto, final String[] params) {
        if (params == null || params.length != 1) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_GM_10003);
        }
        int buildingType;
        try {
            buildingType = Integer.parseInt(params[0]);
        }
        catch (NumberFormatException e) {
            this.chatService.sendSystemChat("SYS2ONE", playerDto.playerId, 0, "\u9519\u8bef\u7684\u6307\u4ee4\u53c2\u6570\uff1a\u53c2\u6570\u5fc5\u987b\u4e3a\u6574\u6570", null);
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_GM_10003);
        }
        if (buildingType < 1 || buildingType > 5) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_GM_10003);
        }
        final int output = this.buildingOutputCache.getOfficersOutput(playerDto.playerId, buildingType);
        this.chatService.sendSystemChat("SYS2ONE", playerDto.playerId, 0, "\u5b98\u804c\u4ea7\u51fa(officers)\uff1a" + output, null);
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Override
    public byte[] handleTechCommand(final PlayerDto playerDto, final String[] params) {
        if (params == null || params.length != 1) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_GM_10003);
        }
        int buildingType;
        try {
            buildingType = Integer.parseInt(params[0]);
        }
        catch (NumberFormatException e) {
            this.chatService.sendSystemChat("SYS2ONE", playerDto.playerId, 0, "\u9519\u8bef\u7684\u6307\u4ee4\u53c2\u6570\uff1a\u53c2\u6570\u5fc5\u987b\u4e3a\u6574\u6570", null);
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_GM_10003);
        }
        if (buildingType < 1 || buildingType > 5) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_GM_10003);
        }
        final int output = this.buildingOutputCache.getTechsOutput(playerDto.playerId, buildingType);
        this.chatService.sendSystemChat("SYS2ONE", playerDto.playerId, 0, "\u79d1\u6280\u4ea7\u51fa(tech)\uff1a" + output, null);
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Override
    public byte[] handleAdditionsCommand(final PlayerDto playerDto, final String[] params) {
        if (params == null || params.length != 1) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_GM_10003);
        }
        int buildingType;
        try {
            buildingType = Integer.parseInt(params[0]);
        }
        catch (NumberFormatException e) {
            this.chatService.sendSystemChat("SYS2ONE", playerDto.playerId, 0, "\u9519\u8bef\u7684\u6307\u4ee4\u53c2\u6570\uff1a\u53c2\u6570\u5fc5\u987b\u4e3a\u6574\u6570", null);
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_GM_10003);
        }
        if (buildingType < 1 || buildingType > 5) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_GM_10003);
        }
        final int output = this.buildingOutputCache.getAdditionsOutput(playerDto.playerId, buildingType);
        this.chatService.sendSystemChat("SYS2ONE", playerDto.playerId, 0, "\u8d44\u6e90\u52a0\u6210\u503c(Addition)\uff1a" + output, null);
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Override
    public byte[] handleTechEffectCommand(final PlayerDto playerDto, final String[] params) {
        if (params == null || params.length < 2) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_GM_10003);
        }
        final int key = Integer.parseInt(params[0]);
        final int type = Integer.parseInt(params[1]);
        if (type < 1 || type > 3) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_GM_10003);
        }
        double value = 0.0;
        if (1 == type) {
            value = this.techEffectCache.getTechEffect(playerDto.playerId, key);
        }
        else if (2 == type) {
            value = this.techEffectCache.getTechEffect2(playerDto.playerId, key);
        }
        else if (3 == type) {
            value = this.techEffectCache.getTechEffect3(playerDto.playerId, key);
        }
        this.chatService.sendSystemChat("SYS2ONE", playerDto.playerId, 0, "\u79d1\u6280\u6548\u679c\u503c\u4e3a\uff1a" + value, null);
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Override
    public byte[] handleAddTechCommand(final int playerId, final String[] params) {
        if (params == null || 1 != params.length) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_GM_10003);
        }
        int techId;
        try {
            techId = Integer.parseInt(params[0]);
        }
        catch (NumberFormatException e) {
            this.chatService.sendSystemChat("SYS2ONE", playerId, 0, "\u9519\u8bef\u7684\u6307\u4ee4\u53c2\u6570\uff1a\u53c2\u6570\u5fc5\u987b\u4e3a\u6574\u6570", null);
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_GM_10003);
        }
        final Tech tech = (Tech)this.techcache.get((Object)techId);
        if (tech == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_NO_SUCH_TECH);
        }
        if (this.playerTechDao.getPlayerTech(playerId, techId) != null) {
            return JsonBuilder.getJson(State.FAIL, "\u8be5\u79d1\u6280\u5df2\u5b58\u5728\uff01");
        }
        final PlayerTech pt = new PlayerTech();
        pt.setCd(new Date());
        pt.setFinishNew(0);
        pt.setIsNew(0);
        pt.setJobId(0);
        pt.setKeyId(tech.getKey());
        pt.setNum(0);
        pt.setPlayerId(playerId);
        pt.setStatus(2);
        pt.setTechId(techId);
        this.playerTechDao.create(pt);
        this.chatService.sendSystemChat("SYS2ONE", playerId, 0, "\u5df2\u6210\u529f\u4e3a\u8be5\u73a9\u5bb6\u6dfb\u52a0\u4e86\u79d1\u6280", null);
        return JsonBuilder.getJson(State.SUCCESS, "\u5df2\u6210\u529f\u4e3a\u8be5\u73a9\u5bb6\u6dfb\u52a0\u4e86\u79d1\u6280");
    }
    
    @Override
    public byte[] handleAddBluePrintCommand(final int playerId, final String[] params) {
        if (params == null || 1 > params.length) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_GM_10003);
        }
        final int index = Integer.parseInt(params[0]);
        this.buildingService.dropBluePrintById(playerId, index);
        this.chatService.sendSystemChat("SYS2ONE", playerId, 0, "\u83b7\u53d6\u56fe\u7eb8\u6210\u529f", null);
        return JsonBuilder.getJson(State.SUCCESS, "\u83b7\u53d6\u56fe\u7eb8\u6210\u529f");
    }
    
    @Override
    public byte[] handleAddKillBanditCommand(final int playerId, final String[] params) {
        if (params == null || 1 > params.length) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_GM_10003);
        }
        final int banditId = Integer.parseInt(params[0]);
        this.buildingService.killBandit(playerId, banditId);
        this.chatService.sendSystemChat("SYS2ONE", playerId, 0, "\u51fb\u6740\u571f\u532a\u6210\u529f", null);
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Override
    public byte[] handleCityIdCommand(final int playerId, final String[] params) {
        if (params == null || 1 > params.length) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_GM_10003);
        }
        final WorldCity wc = this.worldCityCache.getCityIdByName(params[0]);
        if (wc == null) {
            this.chatService.sendSystemChat("SYS2ONE", playerId, 0, "\u6ca1\u6709\u8be5\u57ce\u6c60", null);
        }
        else {
            this.chatService.sendSystemChat("SYS2ONE", playerId, 0, "\u57ce\u6c60id\u4e3a:" + wc.getId(), null);
        }
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Override
    public byte[] handleTechAllCommand(final int playerId, final String[] params) {
        this.playerTechDao.techAll(playerId);
        this.chatService.sendSystemChat("SYS2ONE", playerId, 0, "\u60a8\u73b0\u6709\u79d1\u6280\u90fd\u5df2\u7ecf\u751f\u6548\uff0c\u8bf7\u5237\u65b0\u79d1\u6280\u9762\u677f", null);
        this.techEffectCache.clearTechEffect(playerId);
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Override
    public byte[] handleUndefeatableCommand(final int playerId, final String[] params) {
        if (params == null || params.length != 1) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_GM_10003);
        }
        final String refreshAttribute = "3:5;3:5;3:5";
        final Player player = this.playerDao.read(playerId);
        final int playerLv = player.getPlayerLv();
        final int isLeaderChange = Integer.parseInt(params[0]);
        if (isLeaderChange == 0) {
            this.playerGeneralMilitaryDao.updateLvAndLeader(playerId, playerLv);
        }
        final List<PlayerGeneralMilitary> list = this.playerGeneralMilitaryDao.getMilitaryList(playerId);
        final List<Integer> sequips = this.equipCache.getJinpinEquips();
        final List<Integer> equips = new ArrayList<Integer>();
        for (final Integer integer : sequips) {
            final Equip equip = (Equip)this.equipCache.get((Object)integer);
            if (equip.getQuality() == 6) {
                equips.add(integer);
            }
        }
        final int size = Math.min(equips.size(), 6);
        final List<StoreHouse> storeHouses = this.storeHouseDao.getAllEquip(playerId);
        for (final StoreHouse sh : storeHouses) {
            this.storeHouseDao.resetOwnerByVId(sh.getVId(), 0);
        }
        StoreHouse sh = null;
        for (final PlayerGeneralMilitary pgm : list) {
            if (pgm == null) {
                continue;
            }
            for (int i = 0; i < size; ++i) {
                final Equip equip2 = (Equip)this.equipCache.get((Object)equips.get(i));
                sh = new StoreHouse();
                sh.setRefreshAttribute(refreshAttribute);
                sh.setAttribute(String.valueOf(equip2.getAttribute()));
                sh.setOwner(pgm.getGeneralId());
                sh.setItemId(equip2.getId());
                sh.setPlayerId(playerId);
                sh.setLv(equip2.getDefaultLevel());
                sh.setType(1);
                sh.setGoodsType(equip2.getType());
                sh.setQuality(6);
                sh.setGemId(0);
                sh.setNum(1);
                sh.setState(0);
                sh.setQuenchingTimes(0);
                sh.setBindExpireTime(0L);
                sh.setMarkId(0);
                this.storeHouseDao.create(sh);
            }
            this.battleDataCache.removeEquipEffect(playerId, pgm.getGeneralId());
        }
        final List<PlayerWeapon> pwList = this.dataGetter.getPlayerWeaponDao().getPlayerWeapons(playerId);
        for (final PlayerWeapon pw : pwList) {
            final int lvUp = 100 - pw.getLv();
            this.dataGetter.getPlayerWeaponDao().upgradeWeapon(playerId, pw.getWeaponId(), lvUp, 0);
        }
        this.battleDataCache.refreshWeaponEffect(playerId);
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Override
    public byte[] handleIdCommand(final int playerId, final String[] params) {
        this.chatService.sendSystemChat("SYS2ONE", playerId, 0, "\u89d2\u8272id\u4e3a:" + playerId, null);
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Override
    public byte[] handleZdCommand(final int playerId, final String[] params) {
        final int times = Integer.parseInt(params[0]);
        int t1 = 0;
        int t2 = 0;
        int t3 = 0;
        int t4 = 0;
        int total = times;
        int t5 = 0;
        while (total > 0) {
            final HourlyReward hr = this.hourlyRewardCache.getHourlyReward();
            if (1 == hr.getId()) {
                ++t1;
            }
            else if (2 == hr.getId()) {
                ++t2;
            }
            else if (3 == hr.getId()) {
                ++t3;
            }
            else if (4 == hr.getId()) {
                ++t4;
            }
            else {
                System.out.println(hr.getId());
                ++t5;
            }
            --total;
        }
        final String msg = "\u6267\u884c\u603b\u6b21\u6570:" + times + ":t1\u51fa\u73b0\u7684\u6b21\u6570:" + t1 + ":t2\u51fa\u73b0\u7684\u6b21\u6570:" + t2 + ":t3\u51fa\u73b0\u7684\u6b21\u6570:" + t3 + ":t4\u51fa\u73b0\u7684\u6b21\u6570:" + t4 + ":t1\u51fa\u73b0\u7684\u6982\u7387:" + t1 * 1.0 / times + ":t2\u51fa\u73b0\u7684\u6982\u7387:" + t2 * 1.0 / times + ":t3\u51fa\u73b0\u7684\u6982\u7387:" + t3 * 1.0 / times + ":t4\u51fa\u73b0\u7684\u6982\u7387:" + t4 * 1.0 / times;
        this.chatService.sendSystemChat("SYS2ONE", playerId, 0, msg, null);
        System.out.println("\u6267\u884c\u603b\u6b21\u6570\uff1a" + times + "\n" + ":t1:" + t1 * 1.0 / times + "\n" + ":t2:" + t2 * 1.0 / times + "\n" + ":t3:" + t3 * 1.0 / times + "\n" + ":t4:" + t4 * 1.0 / times + "\n" + "\n");
        final int test_total = t1 + t2 + t3 + t4 + t5;
        System.out.println("Total:" + test_total * 1.0 / times);
        return JsonBuilder.getJson(State.SUCCESS, msg);
    }
    
    @Override
    public byte[] hanleAddRankerNum(final int playerId, final String[] params) {
        int type = 0;
        int times = 0;
        int generalId = 0;
        try {
            type = Integer.parseInt(params[0]);
            times = Integer.parseInt(params[1]);
        }
        catch (Exception e) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_GM_10003);
        }
        final List<PlayerGeneralMilitary> list = this.playerGeneralMilitaryDao.getMilitaryList(playerId);
        if (list == null || list.isEmpty()) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_GM_10003);
        }
        generalId = list.get(0).getGeneralId();
        if (type == 1) {
            this.rankService.updatePlayerChallengeInfo(playerId, generalId, times);
        }
        else {
            if (type != 2) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_GM_10003);
            }
            this.rankService.updatePlayerOccupyCItyInfo(playerId, generalId, times);
        }
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Override
    public byte[] hanleCityEvent(final int playerId, final String[] params) {
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.startArray("cityEvents");
        for (final Map.Entry<Integer, CityAttribute> entry : CityEventManager.getInstance().cityAttributeMap.entrySet()) {
            final int cityId = entry.getKey();
            final CityAttribute cityAttribute = entry.getValue();
            doc.startObject();
            doc.createElement("cityId", cityId);
            doc.createElement("eventType", cityAttribute.eventType);
            doc.createElement("leftCount", cityAttribute.leftCount);
            doc.createElement("eachLimit", cityAttribute.eachLimit);
            if (cityAttribute.countDown == -1L) {
                doc.createElement("countDown", cityAttribute.countDown);
            }
            else {
                doc.createElement("countDown", cityAttribute.countDown - System.currentTimeMillis());
            }
            doc.createElement("viewForceId", cityAttribute.viewForceId);
            doc.createElement("visiable", cityAttribute.visiable);
            doc.createElement("eventTargetId", cityAttribute.eventTargetId);
            doc.endObject();
        }
        doc.endArray();
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public byte[] hanleSetGem(final int playerId, final String[] params) {
        if (params.length < 2) {
            return JsonBuilder.getJson(State.FAIL, "\u53c2\u6570\u9519\u8bef:\u53c2\u6570\u5305\u62ec\u5b9d\u77f3\u7b49\u7ea7\u548c\u6570\u91cf");
        }
        final int gemId = Integer.parseInt(params[0]);
        final int num = Integer.parseInt(params[1]);
        if (num <= 0) {
            return JsonBuilder.getJson(State.FAIL, "\u53c2\u6570\u9519\u8bef:\u5b9d\u77f3\u6570\u91cf\u5fc5\u987b\u4e3a\u6b63\u6570");
        }
        final ArmsGem armsGem = (ArmsGem)this.armsGemCache.get((Object)gemId);
        if (armsGem == null) {
            return JsonBuilder.getJson(State.FAIL, "\u53c2\u6570\u9519\u8bef:\u6ca1\u6709\u8fd9\u6837\u7684\u5b9d\u77f3");
        }
        final List<StoreHouse> shList = this.storeHouseDao.getByItemId(playerId, gemId, 2);
        if (shList != null && shList.size() > 0) {
            this.storeHouseDao.addNum(shList.get(0).getVId(), num - shList.get(0).getNum());
        }
        else {
            final StoreHouse sh = new StoreHouse();
            sh.setType(2);
            sh.setGoodsType(2);
            sh.setItemId(armsGem.getId());
            sh.setLv(armsGem.getGemLv());
            sh.setPlayerId(playerId);
            sh.setOwner(0);
            sh.setQuality(armsGem.getGemLv());
            sh.setGemId(0);
            sh.setAttribute("0");
            sh.setNum(num);
            sh.setState(0);
            sh.setRefreshAttribute("");
            sh.setQuenchingTimes(0);
            sh.setBindExpireTime(0L);
            sh.setMarkId(0);
            this.storeHouseDao.create(sh);
        }
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Override
    public byte[] hanleLimboPic(final int playerId, final String[] params) {
        if (params.length < 2) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final int prisonLv = Integer.parseInt(params[1]);
        if (prisonLv < 1 || prisonLv > 5) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        this.slaveService.addLimboPic(playerId, Integer.parseInt(params[0]), prisonLv);
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Override
    public byte[] hanleSlave2(final int playerId, final String[] params) {
        if (params.length < 3) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final String slaveName = params[0];
        final String generalName = params[1];
        final int type = Integer.parseInt(params[2]);
        if (StringUtils.isBlank(slaveName) || StringUtils.isBlank(generalName) || type < 0 || type > 1) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final Slaveholder sh = this.slaveholderDao.read(playerId);
        if (sh == null) {
            return JsonBuilder.getJson(State.FAIL, "\u5148\u4fee\u6539\u7262\u623f\u5427");
        }
        if (type == 0 && sh.getPrisonLv() < 2) {
            return JsonBuilder.getJson(State.FAIL, "\u4e8c\u7ea7\u4ee5\u4e0a\u7262\u623f\u624d\u53ef\u4ee5\u6293\u5974\u96b6\u5e7b\u5f71\u54e6");
        }
        final Player player = this.playerDao.getPlayerByName(slaveName);
        if (player == null) {
            return JsonBuilder.getJson(State.FAIL, "\u5974\u96b6\u4e3b\u5c06\u4e0d\u5b58\u5728slaveName=" + slaveName);
        }
        if (this.playerDao.getForceId(playerId) == player.getForceId()) {
            return JsonBuilder.getJson(State.FAIL, "\u53ea\u80fd\u6293\u53d6\u5b83\u56fd\u6b66\u5c06\u54e6");
        }
        final General g = this.generalCache.getGeneralByName(generalName);
        if (g == null) {
            return JsonBuilder.getJson(State.FAIL, "\u7cfb\u7edf\u4e2d\u6839\u672c\u6ca1\u6709\u8be5\u6b66\u5c06\u54e6");
        }
        final int slaveId = player.getPlayerId();
        final int generalId = g.getId();
        if (this.playerGeneralMilitaryDao.getMilitary(slaveId, generalId) == null) {
            return JsonBuilder.getJson(State.FAIL, "\u88ab\u6293\u53d6\u5bf9\u8c61\u6ca1\u6709\u8be5\u6b66\u5c06generalName=" + generalName);
        }
        if (1 == type && this.playerSlaveDao.isSlave2(slaveId, generalId)) {
            return JsonBuilder.getJson(State.FAIL, "\u8be5\u6b66\u5c06\u5df2\u7ecf\u662f\u5974\u96b6\u5566\uff0c\u5feb\u53bb\u6293\u5176\u5b83\u6b66\u5c06\u5427");
        }
        final PlayerSlave ps = new PlayerSlave();
        ps.setPlayerId(playerId);
        ps.setSlaveId(slaveId);
        ps.setGeneralId(generalId);
        ps.setGrabTime(new Date());
        ps.setSlashTimes(0);
        ps.setCd(null);
        ps.setType(type);
        this.playerSlaveDao.create(ps);
        final GeneralMoveDto gmd = CityService.getGeneralMoveDto(slaveId, generalId);
        if (gmd != null) {
            gmd.cityState = 22;
        }
        if (1 == type && Players.getPlayer(player.getPlayerId()) != null) {
            this.generalService.sendGeneralMilitaryList(player.getPlayerId());
        }
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Override
    public byte[] handleGetSuitPaper(final int playerId, final String[] params) {
        if (params.length < 1) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final boolean canGet = this.equipService.canGetSuit(playerId, this.techEffectCache);
        if (!canGet) {
            return JsonBuilder.getJson(State.FAIL, "\u6ca1\u79d1\u6280");
        }
        int paperId = 0;
        try {
            paperId = Integer.parseInt(params[0]);
        }
        catch (Exception e) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        int num = 1;
        if (params.length >= 2) {
            num = Integer.parseInt(params[1]);
        }
        final Items items = (Items)this.dataGetter.getItemsCache().get((Object)paperId);
        if (items.getType() == 1) {
            this.storeHouseService.gainSearchItems(paperId, num, new PlayerDto(playerId), "gm\u6307\u4ee4\u83b7\u5f97\u56fe\u7eb8");
        }
        else {
            this.storeHouseService.gainItems(playerId, num, paperId, "gm\u6307\u4ee4\u83b7\u5f97\u56fe\u7eb8");
        }
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Override
    public byte[] handleSetCity(final int playerId, final String[] params) {
        if (params.length < 1) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final int todayNum = Integer.parseInt(params[0]);
        if (todayNum < 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final List<PlayerGeneralMilitary> pgmList = this.playerGeneralMilitaryDao.getMilitaryList(playerId);
        if (pgmList == null || pgmList.size() <= 0) {
            return JsonBuilder.getJson(State.FAIL, "\u4e00\u4e2a\u6b66\u5c06\u90fd\u6ca1\u6709");
        }
        this.dataGetter.getRankService().updatePlayerOccupyCItyInfo(playerId, pgmList.get(0).getGeneralId(), todayNum);
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Override
    public byte[] handleSetDragon(final int playerId, final String[] params) {
        if (params.length < 1) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final int dragonNum = Integer.parseInt(params[0]);
        if (dragonNum < 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        this.playerDragonDao.setDragonNumByPlayerId(playerId, dragonNum);
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Override
    public byte[] handleAddForceExp(final int playerId, final String[] params) {
        if (params.length < 1) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final int addExp = Integer.parseInt(params[0]);
        if (addExp < 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final Player player = this.playerDao.read(playerId);
        final ForceInfo forceInfo = this.forceInfoDao.read(player.getForceId());
        this.forceInfoDao.updateNationExp(player.getForceId(), forceInfo.getForceExp() + addExp);
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Override
    public byte[] handleManWangLing(final PlayerDto playerDto, final String[] params) {
        final int forceId = Integer.parseInt(params[0]);
        final int type = Integer.parseInt(params[1]);
        if (type == 1) {
            this.dataGetter.getBattleService().fireManWangLing(forceId, playerDto.forceId);
        }
        else if (type == 2) {
            int cityId = 1;
            City[] cityArray;
            for (int length = (cityArray = CityDataCache.cityArray).length, i = 0; i < length; ++i) {
                final City city = cityArray[i];
                if (city != null) {
                    if (city.getForceId() == forceId) {
                        cityId = city.getId();
                    }
                }
            }
            this.dataGetter.getBattleService().fireManWangLing(forceId, playerDto.forceId, cityId, System.currentTimeMillis() + 1800000L);
        }
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Override
    public byte[] handleGetNiubiEquip(final PlayerDto playerDto, final String[] params) {
        if (params.length < 3) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        try {
            final int type = Integer.parseInt(params[0]);
            final int skillType = Integer.parseInt(params[1]);
            final int num = Integer.parseInt(params[2]);
            if (type <= 0 || type > 6 || num <= 0 || skillType <= 0 || skillType > 7) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
            }
            final PlayerAttribute pa = this.playerAttributeDao.read(playerDto.playerId);
            if (pa == null) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
            }
            final int maxNum = pa.getMaxStoreNum();
            final int storeNum = this.storeHouseDao.getCountByPlayerId(playerDto.playerId);
            if (storeNum + num > maxNum) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
            }
            final StoreHouse storeHouse = new StoreHouse();
            final Equip equip = this.equipCache.getEquipsByTypeQualityBest(type, 6);
            if (equip == null) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
            }
            final StringBuffer sBuffer = new StringBuffer();
            for (int i = 0; i < 4; ++i) {
                sBuffer.append(skillType).append(":").append(5).append(";");
            }
            SymbolUtil.removeTheLast(sBuffer);
            storeHouse.setAttribute(equip.getAttribute().toString());
            storeHouse.setGemId(0);
            storeHouse.setGoodsType(type);
            storeHouse.setItemId(equip.getId());
            storeHouse.setLv(equip.getLevel());
            storeHouse.setNum(1);
            storeHouse.setOwner(0);
            storeHouse.setPlayerId(playerDto.playerId);
            storeHouse.setQuality(equip.getQuality());
            storeHouse.setQuenchingTimes(100);
            storeHouse.setQuenchingTimesFree(100);
            storeHouse.setRefreshAttribute(sBuffer.toString());
            storeHouse.setSpecialSkillId(skillType);
            storeHouse.setState(0);
            storeHouse.setType(1);
            for (int i = 0; i < num; ++i) {
                storeHouse.setBindExpireTime(0L);
                storeHouse.setMarkId(0);
                this.storeHouseDao.create(storeHouse);
            }
            return JsonBuilder.getJson(State.SUCCESS, "");
        }
        catch (Exception e) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
    }
    
    @Override
    public byte[] hanleSetOfficerToken(final PlayerDto playerDto, final String[] params) {
        if (params.length < 1) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final PlayerOfficeRelative por = this.playerOfficeRelativeDao.read(playerDto.playerId);
        if (por == null || !this.hallsCache.getTokenList().contains(por.getOfficerId())) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        int num = 0;
        try {
            num = Integer.parseInt(params[0]);
        }
        catch (Exception e) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        if (num <= 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        this.officerTokenDao.setTokenNum(playerDto.forceId, por.getOfficerId(), num);
        return null;
    }
    
    @Override
    public byte[] hanleDefaultPay(final PlayerDto playerDto, final String[] params) {
        final int playerId = this.playerDao.getDefaultPlayerId(playerDto.userId, playerDto.yx);
        final Player player = this.playerDao.read(playerId);
        final String msg = "playerId:" + playerId + "#playerName:" + player.getPlayerName();
        this.chatService.sendSystemChat("SYS2ONE", playerDto.playerId, 0, msg, null);
        return JsonBuilder.getJson(State.SUCCESS, "success");
    }
    
    @Override
    public byte[] handleMooncake(final PlayerDto playerDto, final String[] params) {
        if (params.length < 1) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        int num = 0;
        try {
            num = Integer.parseInt(params[0]);
        }
        catch (Exception e) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        EventUtil.handleOperation(playerDto.playerId, 10, num);
        return JsonBuilder.getJson(State.SUCCESS, "success");
    }
    
    @Override
    public byte[] handleBmw(final PlayerDto playerDto, final String[] params) {
        if (params.length < 1) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        int num = 0;
        try {
            num = Integer.parseInt(params[0]);
        }
        catch (Exception e) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        this.eventService.addBmw(playerDto.playerId, num, "GM\u6307\u4ee4\u589e\u52a0\u5b9d\u9a6c");
        return JsonBuilder.getJson(State.SUCCESS, "success");
    }
    
    @Override
    public byte[] handleXo(final PlayerDto playerDto, final String[] params) {
        if (params.length < 1) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        int num = 0;
        try {
            num = Integer.parseInt(params[0]);
        }
        catch (Exception e) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        this.eventService.addXo(playerDto.playerId, num, "GM\u6307\u4ee4\u589e\u52a0\u7f8e\u9152");
        return JsonBuilder.getJson(State.SUCCESS, "success");
    }
    
    @Override
    public byte[] handlePicasso(final PlayerDto playerDto, final String[] params) {
        if (params.length < 1) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        int num = 0;
        try {
            num = Integer.parseInt(params[0]);
        }
        catch (Exception e) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        this.eventService.addPicasso(playerDto.playerId, num, "GM\u6307\u4ee4\u589e\u52a0\u4e66\u753b");
        return JsonBuilder.getJson(State.SUCCESS, "success");
    }
    
    @Override
    public byte[] handleMs(final PlayerDto playerDto, final String[] params) {
        if (params.length < 1) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        int num = 0;
        try {
            num = Integer.parseInt(params[0]);
        }
        catch (Exception e) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        EventUtil.handleOperation(playerDto.playerId, 11, num);
        return JsonBuilder.getJson(State.SUCCESS, "success");
    }
    
    @Override
    public byte[] handleEnterWorldDrama(final PlayerDto playerDto, final String[] params) {
        if (params.length < 2) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        int sId = 0;
        int grade = 0;
        try {
            sId = Integer.parseInt(params[0]);
            grade = Integer.parseInt(params[1]);
        }
        catch (NumberFormatException e) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final OperationResult result = this.juBenService.enterWorldDramaScene(playerDto, sId, grade);
        if (!result.getResult()) {
            return JsonBuilder.getJson(State.FAIL, result.getResultContent());
        }
        return JsonBuilder.getJson(State.SUCCESS, "success");
    }
    
    @Override
    public byte[] handleIronEffect(final PlayerDto playerDto, final String[] params) {
        int param1 = 0;
        for (int i = 1; i <= 5; ++i) {
            param1 += (int)Math.pow(2.0, i - 1);
        }
        this.dataGetter.getPlayerEventDao().clearEvent(13);
        this.dataGetter.getPlayerEventDao().updateInfo3(playerDto.playerId, 13, 400, param1);
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Override
    public byte[] handleWeaponLv(final PlayerDto playerDto, final String[] params) {
        if (params == null || params.length < 2) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_GM_10003);
        }
        final int weaponId = Integer.valueOf(params[0].trim());
        final int lv = Integer.valueOf(params[1].trim());
        this.playerWeaponDao.setWeaponLv(playerDto.playerId, weaponId, lv);
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Transactional
    @Override
    public byte[] handleCallMeHXL(final PlayerDto playerDto, final String[] params) {
        if (params == null || params.length <= 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_GM_10003);
        }
        int ifTaozhuang = 0;
        int ifBingqi = 0;
        int ifBaoshi = 0;
        try {
            if (params.length >= 1) {
                ifTaozhuang = Integer.parseInt(params[0]);
                if (params.length >= 2) {
                    ifBingqi = Integer.parseInt(params[1]);
                    if (params.length >= 3) {
                        ifBaoshi = Integer.parseInt(params[2]);
                    }
                }
            }
        }
        catch (Exception e) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_GM_10003);
        }
        this.playerGeneralMilitaryDao.deleteByPlayerId(playerDto.playerId);
        this.storeHouseDao.resetOwnerByGeneralId(playerDto.playerId, 0);
        this.storeHouseDao.deleteWeaponAndClearOwner(playerDto.playerId);
        this.storeHouseBakDao.deleteByPlayerId(playerDto.playerId);
        final List<Integer> generalVid = new ArrayList<Integer>();
        this.recruitStrongestGeneral(generalVid, playerDto);
        if (ifTaozhuang > 0) {
            this.armedWithEquipProset(generalVid, playerDto.playerId);
        }
        if (ifBingqi > 0) {
            this.playerWeaponDao.deleteByPlayerId(playerDto.playerId);
            PlayerWeapon playerWeapon = null;
            for (int i = 0; i < 6; ++i) {
                playerWeapon = new PlayerWeapon();
                playerWeapon.setLv(200);
                playerWeapon.setWeaponId(i + 1);
                playerWeapon.setPlayerId(playerDto.playerId);
                playerWeapon.setType(i % 3 + 1);
                playerWeapon.setTimes(0);
                if (ifBaoshi > 0) {
                    playerWeapon.setGemId("20,");
                }
                else {
                    playerWeapon.setGemId("");
                }
                this.playerWeaponDao.create(playerWeapon);
            }
        }
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    private void armedWithEquipProset(final List<Integer> generalVid, final int playerId) {
        if (generalVid == null || generalVid.isEmpty()) {
            return;
        }
        final List<EquipProset> prosets = this.dataLoader.getModels((Class)EquipProset.class);
        final int size = generalVid.size();
        int count = 0;
        StoreHouse prosetStorehouseTemp = null;
        while (count < size) {
            final int prosetId = WebUtil.nextInt(prosets.size()) + 1;
            final EquipProset proset = prosets.get(prosetId - 1);
            if (proset == null) {
                continue;
            }
            final int generalId = generalVid.get(count);
            prosetStorehouseTemp = this.createStoreHouse(playerId, generalId, 14, proset.getItemId());
            this.storeHouseDao.create(prosetStorehouseTemp);
            final List<EquipCoordinates> coordinates = this.dataGetter.getEquipCache().getAllSuits(prosetId);
            int suitIndex = 0;
            for (final EquipCoordinates single : coordinates) {
                final Integer[] type = this.equipCache.getSkillArray(single.getId());
                StoreHouseBak toCreateEquipHouseBak = null;
                for (int i = 0; i < type.length; ++i) {
                    final Equip equip = this.equipCache.getSuitSingleEquipByType(i + 1);
                    toCreateEquipHouseBak = this.createStoreHouseBak(playerId, generalId, equip);
                    toCreateEquipHouseBak.setType(1);
                    toCreateEquipHouseBak.setGoodsType(i + 1);
                    toCreateEquipHouseBak.setSpecialSkillId(type[i]);
                    toCreateEquipHouseBak.setRefreshAttribute(this.getFullStarRefreshAttribute(type[i]));
                    toCreateEquipHouseBak.setSuitId(prosetStorehouseTemp.getVId());
                    toCreateEquipHouseBak.setSuitIndex(suitIndex);
                    this.storeHouseBakDao.create(toCreateEquipHouseBak);
                }
                ++suitIndex;
            }
            ++count;
        }
    }
    
    private String getFullStarRefreshAttribute(final int id) {
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < 4; ++i) {
            sb.append(id).append(":").append(5).append(";");
        }
        SymbolUtil.removeTheLast(sb);
        return sb.toString();
    }
    
    private StoreHouse createStoreHouse(final int playerId, final int generalId, final int type, final int itemId) {
        final StoreHouse temp = new StoreHouse();
        temp.setPlayerId(playerId);
        temp.setType(14);
        temp.setGoodsType(14);
        temp.setItemId(itemId);
        temp.setQuenchingTimes(0);
        temp.setQuenchingTimesFree(0);
        temp.setRefreshAttribute("");
        temp.setSpecialSkillId(0);
        temp.setOwner(generalId);
        temp.setState(0);
        temp.setNum(1);
        temp.setQuality(6);
        temp.setLv(0);
        temp.setGemId(0);
        temp.setQuenchingTimes(0);
        temp.setBindExpireTime(0L);
        temp.setMarkId(0);
        return temp;
    }
    
    private StoreHouseBak createStoreHouseBak(final int playerId, final int generalId, final Equip equip) {
        final StoreHouseBak temp = new StoreHouseBak();
        temp.setPlayerId(playerId);
        temp.setType(14);
        temp.setGoodsType(14);
        temp.setItemId(equip.getId());
        temp.setQuenchingTimes(0);
        temp.setQuenchingTimesFree(0);
        temp.setRefreshAttribute("");
        temp.setSpecialSkillId(0);
        temp.setOwner(0);
        temp.setState(0);
        temp.setNum(1);
        temp.setQuality(6);
        temp.setLv(0);
        temp.setGemId(0);
        temp.setQuenchingTimes(0);
        temp.setBindExpireTime(0L);
        temp.setSuitId(0);
        temp.setSuitIndex(0);
        temp.setAttribute(equip.getAttribute().toString());
        return temp;
    }
    
    private void recruitStrongestGeneral(final List<Integer> generalVid, final PlayerDto playerDto) {
        final List<RecruitInfo> list = this.dataGetter.getGeneralRecruitCache().getDropGeneralList(2);
        PlayerGeneralMilitary pgm = null;
        RecruitInfo info = null;
        for (int i = list.size() - 5; i < list.size(); ++i) {
            pgm = new PlayerGeneralMilitary();
            info = list.get(i);
            pgm.setPlayerId(playerDto.playerId);
            pgm.setGeneralId(info.getGeneralId());
            pgm.setLeader(0);
            pgm.setStrength(0);
            pgm.setForces(0);
            pgm.setLv(playerDto.playerLv);
            pgm.setExp(0L);
            pgm.setLocationId(WorldCityCommon.nationMainCityIdMap.get(playerDto.forceId));
            pgm.setUpdateForcesTime(new Date());
            pgm.setState(1);
            pgm.setMorale(100);
            pgm.setAuto(1);
            pgm.setTacticEffect(0);
            pgm.setForceId(playerDto.forceId);
            pgm.setJubenLoId(0);
            this.playerGeneralMilitaryDao.create(pgm);
            generalVid.add(pgm.getGeneralId());
        }
    }
    
    @Override
    public byte[] handleXiLian(final PlayerDto playerDto, final String[] params) {
        if (params == null || params.length < 1) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_GM_10003);
        }
        for (int times = Integer.parseInt(params[0]), i = 0; i < times; ++i) {
            EventUtil.handleOperation(playerDto.playerId, 14, 100);
        }
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Override
    public byte[] handleChangeForceLv(final PlayerDto playerDto, final String[] params) {
        if (params == null || params.length < 3) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_GM_10003);
        }
        int forceId = 0;
        int lv = 0;
        int exp = 0;
        try {
            forceId = Integer.parseInt(params[0]);
            lv = Integer.parseInt(params[1]);
            exp = Integer.parseInt(params[2]);
        }
        catch (Exception e) {
            final ErrorSceneLog errorSceneLog = ErrorSceneLog.getInstance();
            errorSceneLog.error(this, e);
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_GM_10003);
        }
        if (lv <= 0 || lv > this.kingdomLvCache.maxLv) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_GM_10003);
        }
        if (forceId >= 0) {
            this.forceInfoDao.updateForceLvAndAddExp(forceId, lv, exp);
        }
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Override
    public byte[] handleChangeNationTask(final PlayerDto playerDto, final String[] params) {
        if (params == null || params.length < 1) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_GM_10003);
        }
        int type = 0;
        try {
            type = Integer.parseInt(params[0]);
        }
        catch (Exception e) {
            final ErrorSceneLog errorSceneLog = ErrorSceneLog.getInstance();
            errorSceneLog.error(this, e);
        }
        if (type <= 0 || type > 12 || type == 11) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_GM_10003);
        }
        int timeDivision = RankService.getTimeDevision();
        if (timeDivision <= -1) {
            if (TimeForNationTask.isInTime(RankService.TASK_TIME1[1], RankService.TASK_TIME2[0])) {
                timeDivision = 3;
            }
            else if (TimeForNationTask.isInTime(RankService.TASK_TIME2[1], RankService.TASK_TIME3[0])) {
                timeDivision = 5;
            }
            else {
                timeDivision = 1;
            }
            final String startServer = Configuration.getProperty("gcld.server.time");
            final long startLong = Long.parseLong(startServer);
            final Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(startLong);
            final int startYear = calendar.get(1);
            final int startDay = calendar.get(6);
            final long nowLong = System.currentTimeMillis();
            calendar.setTimeInMillis(nowLong);
            final int nowYear = calendar.get(1);
            final int nowDay = calendar.get(6);
            final int day = (nowYear - startYear) * 365 + (nowDay - startDay);
            final int composeId = day * 100 + (timeDivision + 1) / 2;
            final int realType = this.rankService.getFromDatabase(composeId);
            if (realType != type) {
                this.taskInitDao.updateType(composeId, type);
            }
        }
        else {
            this.dataGetter.getNationTaskDao().deleteAllTasks();
            InMemmoryIndivTaskManager.getInstance().clearAfterTaskIsOver();
            this.dataGetter.getTaskKillInfoDao().deleteAllInfos();
            this.playerExpandInfoDao.deleteAll();
            RankService.nationTaskKillRanker.clear();
            if (type == 12) {
                RankService.isYuanXiao = true;
                this.rankService.checkIsYuanXiaoNight(true, type);
            }
            this.rankService.startNationTaskByType(type, timeDivision);
        }
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Override
    public byte[] handleChangeIndivTask(final PlayerDto playerDto, final String[] params) {
        if (params.length != 6) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_GM_10003);
        }
        final int type1 = Integer.parseInt(params[0]);
        final int level1 = Integer.parseInt(params[1]);
        final int type2 = Integer.parseInt(params[2]);
        final int level2 = Integer.parseInt(params[3]);
        final int type3 = Integer.parseInt(params[4]);
        final int level3 = Integer.parseInt(params[5]);
        final int playerId = playerDto.playerId;
        final int forceId = playerDto.forceId;
        final ForceInfo info = this.forceInfoDao.read(forceId);
        final int taskType = this.rankService.hasNationTasks(forceId);
        if (taskType <= 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_GM_10003);
        }
        this.dataGetter.getPlayerIndivTaskDao().deleteById(playerId);
        final InMemmoryIndivTaskManager manager = InMemmoryIndivTaskManager.getInstance();
        final List<MultiResult> list = new ArrayList<MultiResult>();
        MultiResult temp = new MultiResult();
        temp.result1 = type1;
        temp.result2 = level1 % 1000;
        temp.result3 = level1 / 1000;
        list.add(temp);
        temp = new MultiResult();
        temp.result1 = type2;
        temp.result2 = level2 % 1000;
        temp.result3 = level2 / 1000;
        list.add(temp);
        temp = new MultiResult();
        temp.result1 = type3;
        temp.result2 = level3 % 1000;
        temp.result3 = level3 / 1000;
        list.add(temp);
        manager.getDefaultTasks().remove(forceId);
        manager.initTasksByList(list, forceId, taskType);
        manager.getTasks().clear();
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
}
