package com.reign.gcld.gm.service;

import org.springframework.stereotype.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.building.dao.*;
import com.reign.gcld.battle.dao.*;
import com.reign.gcld.task.dao.*;
import com.reign.gcld.player.dao.*;
import com.reign.gcld.sdata.cache.*;
import com.reign.gcld.task.service.*;
import com.reign.gcld.rank.service.*;
import com.reign.gcld.building.service.*;
import com.reign.gcld.general.dao.*;
import com.reign.gcld.tavern.service.*;
import com.reign.gcld.world.dao.*;
import com.reign.gcld.player.service.*;
import com.reign.gcld.player.dto.*;
import com.reign.gcld.world.common.*;
import com.reign.gcld.juben.service.*;
import com.reign.gcld.common.*;
import com.reign.gcld.common.event.*;
import com.reign.framework.json.*;
import com.reign.gcld.task.common.*;
import com.reign.gcld.task.domain.*;
import com.reign.gcld.general.domain.*;
import com.reign.gcld.juben.common.*;
import com.reign.gcld.world.domain.*;
import org.springframework.transaction.annotation.*;
import com.reign.gcld.battle.common.*;
import com.reign.gcld.battle.scene.*;
import java.util.*;
import com.reign.gcld.tavern.domain.*;
import com.reign.gcld.battle.domain.*;
import com.reign.gcld.tech.domain.*;
import com.reign.gcld.weapon.domain.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.gcld.building.domain.*;
import com.reign.gcld.player.domain.*;

@Component("autoPlayerTask")
public class AutoPlayerTask implements IAutoPlayerTask
{
    @Autowired
    private ArmiesCache armiesCache;
    @Autowired
    private PowerCache powerCache;
    @Autowired
    private BuildingCache buildingCache;
    @Autowired
    private TaskCache taskCache;
    @Autowired
    private IPlayerDao playerDao;
    @Autowired
    private IPlayerArmyDao playerArmyDao;
    @Autowired
    private IPlayerBuildingDao playerBuildingDao;
    @Autowired
    private IPlayerPowerDao playerPowerDao;
    @Autowired
    private IPlayerTaskDao playerTaskDao;
    @Autowired
    private IDataGetter dataGetter;
    @Autowired
    private IPlayerResourceDao playerResourceDao;
    @Autowired
    private SerialCache serialCache;
    @Autowired
    private IBuildingOutputCache buildingOutputCache;
    @Autowired
    private IPlayerTaskService playerTaskService;
    @Autowired
    private IRankService rankService;
    @Autowired
    private IBuildingService buildingService;
    @Autowired
    private IPlayerGeneralMilitaryDao playerGeneralMilitaryDao;
    @Autowired
    private IPlayerWorldDao playerWorldDao;
    @Autowired
    private IPlayerGeneralDao playerGeneralDao;
    @Autowired
    private ITavernService tavernService;
    @Autowired
    private ICityDao cityDao;
    @Autowired
    private IPlayerService playerService;
    
    @Transactional
    @Override
    public byte[] autoTask(final PlayerDto playerDto, final int taskId) {
        final int playerId = playerDto.playerId;
        Player player = this.playerDao.read(playerId);
        if (player.getPlayerLv() < 2) {
            this.rankService.updatePlayerLv(playerId, 2);
        }
        final Task task = (Task)this.taskCache.get((Object)taskId);
        if (task == null) {
            return JsonBuilder.getJson(State.FAIL, "\u76ee\u6807\u4efb\u52a1\u4e0d\u5b58\u5728");
        }
        final GameTask exeTask = TaskFactory.getInstance().getTask(task.getId());
        final PlayerTask playerTask = this.playerTaskDao.getCurMainTask(playerId);
        GameTask gameTask = TaskFactory.getInstance().getTask(playerTask.getTaskId());
        if (gameTask == null) {
            return JsonBuilder.getJson(State.FAIL, "\u65e0\u4efb\u52a1\u53ef\u6267\u884c\u4e86");
        }
        if (gameTask.getSeq() > exeTask.getSeq()) {
            return JsonBuilder.getJson(State.FAIL, "\u8be5\u4efb\u52a1\u5df2\u7ecf\u6267\u884c\u8fc7\u4e86");
        }
        int exeTimes = exeTask.getSeq() - gameTask.getSeq();
        final Map<String, Object> targetMap = new HashMap<String, Object>();
        final Map<String, Object> rewardMap = new HashMap<String, Object>();
        while (gameTask != null && exeTimes-- > 0) {
            final String[] targets = gameTask.getTarget().split(";");
            String[] array;
            for (int length = (array = targets).length, j = 0; j < length; ++j) {
                final String str = array[j];
                this.dealTaskTarget(str, targetMap);
            }
            final String[] rewards = gameTask.getReward().split(";");
            String[] array2;
            for (int length2 = (array2 = rewards).length, k = 0; k < length2; ++k) {
                final String str2 = array2[k];
                this.dealTaskReward(str2, rewardMap);
            }
            gameTask = gameTask.getNextTask();
        }
        this.reward(playerId, rewardMap);
        this.target(playerId, targetMap);
        if (gameTask == null) {
            playerTask.setState(2);
            this.playerTaskDao.update(playerTask);
        }
        else {
            playerTask.setTaskId(gameTask.getId());
            playerTask.setState(1);
            playerTask.setProcess(0);
            this.playerTaskDao.update(playerTask);
        }
        player = this.playerDao.read(playerId);
        final List<Player> listPlayer = this.playerDao.getSamePlayerLevel(2, player.getPlayerLv(), player.getForceId());
        if (listPlayer != null) {
            Player samePlayer = null;
            for (final Player pp : listPlayer) {
                if (pp.getPlayerId() != playerId) {
                    samePlayer = pp;
                    break;
                }
            }
            if (samePlayer != null) {
                final Map<Integer, PlayerGeneralMilitary> pgmMap = this.playerGeneralMilitaryDao.getMilitaryMap(playerId);
                final List<PlayerGeneralMilitary> pgmList = this.playerGeneralMilitaryDao.getMilitaryList(samePlayer.getPlayerId());
                final Date date = new Date();
                final int maxNum = this.tavernService.getMaxGeneralNum(player.getPlayerId(), player.getPlayerLv(), 2);
                int curNum = 0;
                if (pgmMap != null) {
                    curNum = pgmMap.size();
                }
                for (final PlayerGeneralMilitary pgm : pgmList) {
                    if (curNum >= maxNum) {
                        break;
                    }
                    if (pgmMap != null && pgmMap.get(pgm.getGeneralId()) != null) {
                        continue;
                    }
                    final PlayerGeneral playerGeneral = this.playerGeneralDao.getPlayerGeneral(playerId, pgm.getGeneralId());
                    if (playerGeneral != null) {
                        this.playerGeneralDao.deleteById(playerGeneral.getVId());
                    }
                    ++curNum;
                    final PlayerGeneralMilitary playerGeneralMilitary = new PlayerGeneralMilitary();
                    playerGeneralMilitary.setPlayerId(playerId);
                    playerGeneralMilitary.setGeneralId(pgm.getGeneralId());
                    playerGeneralMilitary.setLeader(pgm.getLeader());
                    playerGeneralMilitary.setStrength(pgm.getStrength());
                    playerGeneralMilitary.setForces(pgm.getForces());
                    playerGeneralMilitary.setLv(pgm.getLv());
                    playerGeneralMilitary.setExp(pgm.getExp());
                    playerGeneralMilitary.setLocationId(WorldCityCommon.nationMainCityIdMap.get(player.getForceId()));
                    playerGeneralMilitary.setUpdateForcesTime(date);
                    playerGeneralMilitary.setState(1);
                    playerGeneralMilitary.setMorale(100);
                    playerGeneralMilitary.setAuto(pgm.getAuto());
                    playerGeneralMilitary.setTacticEffect(pgm.getTacticEffect());
                    playerGeneralMilitary.setForceId(playerDto.forceId);
                    final JuBenDto juBenDto = JuBenManager.getInstance().getByPid(playerId);
                    if (juBenDto != null) {
                        playerGeneralMilitary.setJubenLoId(juBenDto.capital);
                    }
                    else {
                        playerGeneralMilitary.setJubenLoId(0);
                    }
                    this.playerGeneralMilitaryDao.create(playerGeneralMilitary);
                }
            }
        }
        final PlayerWorld myPw = this.playerWorldDao.read(playerId);
        if (myPw != null) {
            final List<City> list = this.cityDao.getModels();
            final StringBuilder sb = new StringBuilder();
            for (final City city : list) {
                sb.append(city.getId());
                sb.append(",");
            }
            this.playerWorldDao.updateAttInfo(playerId, sb.toString(), null);
        }
        Players.push(playerDto.playerId, PushCommand.PUSH_TASK, this.playerTaskService.getAllTaskInfo(playerDto));
        EventListener.fireEvent(new CommonEvent(1, playerId));
        this.playerResourceDao.addWoodIgnoreMax(playerId, 10000.0, "GM\u6307\u4ee4", true);
        this.playerResourceDao.addFoodIgnoreMax(playerId, 10000.0, "GM\u6307\u4ee4");
        this.playerResourceDao.addIronIgnoreMax(playerId, 10000, "GM\u6307\u4ee4", true);
        this.playerResourceDao.addCopperIgnoreMax(playerId, 20000.0, "GM\u6307\u4ee4", true);
        EventListener.fireEvent(new CommonEvent(3, playerId));
        for (int i = 1; i <= 5; ++i) {
            this.buildingOutputCache.clearBase(playerId, i);
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("player", player);
        doc.endObject();
        Players.push(playerId, PushCommand.PUSH_PLAYERINFO, doc.toByte());
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    private void target(final int playerId, final Map<String, Object> targetMap) {
        Integer playerLv = targetMap.get("chief_lv");
        if (playerLv == null) {
            playerLv = 1;
        }
        final Set<Integer> armySet = targetMap.get("battle_win");
        if (armySet != null) {
            for (final Integer armyId : armySet) {
                final Armies curArmies = (Armies)this.armiesCache.get((Object)armyId);
                playerLv = ((curArmies.getLevel() > playerLv) ? curArmies.getLevel() : playerLv);
                PlayerPower curPower = this.playerPowerDao.getPlayerPower(playerId, curArmies.getPowerId());
                if (curPower == null) {
                    curPower = new PlayerPower();
                    curPower.setPlayerId(playerId);
                    curPower.setPowerId(curArmies.getPowerId());
                    curPower.setComplete(0);
                    curPower.setAttackable(1);
                    curPower.setReward(1);
                    curPower.setExpireTime(new Date(System.currentTimeMillis() + 86400000L));
                    curPower.setState(-1);
                    curPower.setBuyCount(0);
                    this.playerPowerDao.create(curPower);
                }
                else if (curPower.getAttackable() != 1) {
                    this.playerPowerDao.updateAttackable(playerId, curArmies.getPowerId(), 1);
                }
                final PlayerArmy curArmy = this.playerArmyDao.getPlayerArmy(playerId, armyId);
                if (curArmy == null) {
                    final LinkedList<Armies> list = this.armiesCache.getArmiesByPowerId(curArmies.getPowerId());
                    for (int i = 0; i < list.size(); ++i) {
                        final Armies armies = list.get(i);
                        final PlayerArmy playerArmy = new PlayerArmy();
                        playerArmy.setArmyId(armies.getId());
                        playerArmy.setPlayerId(playerId);
                        playerArmy.setPowerId(curArmies.getPowerId());
                        playerArmy.setAttNum(0);
                        playerArmy.setWinNum(0);
                        playerArmy.setFirstWin(0);
                        playerArmy.setAttackable(0);
                        if (i == 0) {
                            playerArmy.setAttackable(1);
                        }
                        playerArmy.setFirstOpen(1);
                        playerArmy.setDropCount(0);
                        this.playerArmyDao.create(playerArmy);
                    }
                }
                if (curArmies.getDropMap() != null) {
                    BattleDrop battleDrop = curArmies.getDropMap().get(101);
                    if (battleDrop != null) {
                        final General general = (General)this.dataGetter.getGeneralCache().get((Object)battleDrop.id);
                        final String gId = general.getId() + ",";
                        final PlayerTavern playerTavern = this.dataGetter.getPlayerTavernDao().read(playerId);
                        if (playerTavern != null) {
                            if (general.getType() == 1) {
                                if (playerTavern.getCivilInfo() != null && playerTavern.getCivilInfo().contains(gId)) {
                                    continue;
                                }
                                this.dataGetter.getPlayerTavernDao().updateCivilInfo(playerId, gId);
                            }
                            else {
                                if (playerTavern.getMilitaryInfo() != null && playerTavern.getMilitaryInfo().contains(gId)) {
                                    continue;
                                }
                                this.dataGetter.getPlayerTavernDao().updateMilitaryInfo(playerId, gId);
                            }
                        }
                    }
                    battleDrop = curArmies.getDropMap().get(104);
                    int extraPowerId = 0;
                    if (battleDrop != null) {
                        extraPowerId = battleDrop.id;
                    }
                    if (extraPowerId != 0) {
                        Builder.dealExtraPower(this.dataGetter, playerId, extraPowerId);
                    }
                    battleDrop = curArmies.getDropMap().get(103);
                    int rewardNpcId = 0;
                    if (battleDrop != null) {
                        rewardNpcId = battleDrop.id;
                    }
                    if (rewardNpcId != 0) {
                        PlayerArmyReward playerArmyReward = this.dataGetter.getPlayerArmyRewardDao().getPlayerArmyRewardByArmyId(playerId, rewardNpcId);
                        if (playerArmyReward == null) {
                            playerArmyReward = new PlayerArmyReward();
                            playerArmyReward.setPlayerId(playerId);
                            playerArmyReward.setPowerId(curArmies.getPowerId());
                            playerArmyReward.setArmyId(rewardNpcId);
                            playerArmyReward.setFirst(1);
                            final int miniuteNum = ((ArmiesReward)this.dataGetter.getArmiesRewardCache().get((Object)rewardNpcId)).getTime();
                            playerArmyReward.setExpireTime(new Date(System.currentTimeMillis() + miniuteNum * 60000L));
                            playerArmyReward.setNpcLost(null);
                            final int num = OneVsRewardNpcBuilder.getMaxHp(this.dataGetter, rewardNpcId);
                            playerArmyReward.setHp(num);
                            playerArmyReward.setHpMax(num);
                            playerArmyReward.setState(0);
                            playerArmyReward.setBuyCount(0);
                            playerArmyReward.setFirstWin(0);
                            playerArmyReward.setWinCount(0);
                            this.dataGetter.getPlayerArmyRewardDao().create(playerArmyReward);
                        }
                    }
                    battleDrop = curArmies.getDropMap().get(102);
                    int techId = 0;
                    if (battleDrop != null) {
                        techId = battleDrop.id;
                    }
                    if (techId != 0) {
                        this.dataGetter.getTechService().dropTech(playerId, techId);
                    }
                }
                if (curArmies.getId() == 31) {
                    this.dataGetter.getPlayerBattleAttributeDao().updateArmiesAutoCount(playerId, 5);
                }
                else if (curArmies.getId() == 41) {
                    this.dataGetter.getPlayerBattleAttributeDao().updateArmiesAutoCount(playerId, 10);
                }
                else if (curArmies.getId() == 50) {
                    this.dataGetter.getPlayerBattleAttributeDao().updateArmiesAutoCount(playerId, 99);
                }
                this.playerArmyDao.updateAttackWinNum(playerId, curArmies.getId(), 1, 1);
                final Armies nexArmies = this.armiesCache.getNextArmies(armyId);
                if (nexArmies != null) {
                    playerLv = ((nexArmies.getLevel() > playerLv) ? nexArmies.getLevel() : playerLv);
                    this.playerArmyDao.updateAttackable(playerId, nexArmies.getId(), 1);
                }
                if (this.dataGetter.getArmiesCache().isLastArmies(armyId)) {
                    final Power power = (Power)this.powerCache.get((Object)curArmies.getPowerId());
                    final PlayerPower pp = this.playerPowerDao.getPlayerPower(playerId, power.getNextPower());
                    if (pp == null) {
                        final PlayerPower nextPower = new PlayerPower();
                        nextPower.setPlayerId(playerId);
                        nextPower.setPowerId(power.getNextPower());
                        nextPower.setComplete(0);
                        nextPower.setAttackable(1);
                        nextPower.setReward(0);
                        nextPower.setBuyCount(0);
                        nextPower.setState(0);
                        this.playerPowerDao.create(nextPower);
                        final LinkedList<Armies> listNext = this.armiesCache.getArmiesByPowerId(power.getNextPower());
                        if (listNext == null) {
                            continue;
                        }
                        for (int j = 0; j < listNext.size(); ++j) {
                            final Armies armies2 = listNext.get(j);
                            final PlayerArmy playerArmy2 = new PlayerArmy();
                            playerArmy2.setArmyId(armies2.getId());
                            playerArmy2.setPlayerId(playerId);
                            playerArmy2.setPowerId(power.getNextPower());
                            playerArmy2.setAttNum(0);
                            playerArmy2.setWinNum(0);
                            playerArmy2.setFirstWin(0);
                            playerArmy2.setAttackable(0);
                            if (j == 0) {
                                playerArmy2.setAttackable(1);
                            }
                            playerArmy2.setFirstOpen(1);
                            playerArmy2.setDropCount(0);
                            this.playerArmyDao.create(playerArmy2);
                        }
                    }
                    else {
                        if (pp.getAttackable() == 1) {
                            continue;
                        }
                        this.playerPowerDao.updateAttackable(playerId, power.getNextPower(), 1);
                    }
                }
            }
        }
        if (playerLv == null || playerLv < 2) {
            playerLv = 2;
        }
        this.rankService.updatePlayerLv(playerId, playerLv);
        this.playerDao.updateMaxLv(playerId, playerLv + 10);
        this.playerBuildingDao.upgradeBuildingLv(playerId, playerLv - 1);
    }
    
    private boolean dealTaskTarget(final String targetStr, final Map<String, Object> targetMap) {
        final String[] s = targetStr.split(",");
        final String type = s[0].trim();
        if (!"building".equalsIgnoreCase(type) && !"officer".equalsIgnoreCase(type) && !"general".equalsIgnoreCase(type)) {
            if ("get_power".equalsIgnoreCase(type)) {
                this.collectData("get_power", targetMap, Integer.valueOf(s[1]));
            }
            else if ("battle_win".equalsIgnoreCase(type)) {
                this.collectData("battle_win", targetMap, Integer.valueOf(s[1]));
            }
            else if (!"store_buy".equalsIgnoreCase(type) && !"wear_equip".equalsIgnoreCase(type) && !"update_equip".equalsIgnoreCase(type) && !"halls_position".equalsIgnoreCase(type)) {
                if ("chief_lv".equalsIgnoreCase(type)) {
                    targetMap.put("chief_lv", Integer.valueOf(s[1]));
                }
                else if (!"tech_upgrade".equalsIgnoreCase(type) && !"equip".equalsIgnoreCase(type) && !"store_buy_s".equalsIgnoreCase(type) && !"treasure".equalsIgnoreCase(type) && !"tech".equalsIgnoreCase(type)) {
                    "arms_weapon_on".equalsIgnoreCase(type);
                }
            }
        }
        return true;
    }
    
    private void reward(final int playerId, final Map<String, Object> rewardMap) {
        final Player player = this.dataGetter.getPlayerDao().read(playerId);
        final PlayerAttribute pa = this.dataGetter.getPlayerAttributeDao().read(playerId);
        for (final String type : rewardMap.keySet()) {
            final Object object = rewardMap.get(type);
            if ("new_building".equalsIgnoreCase(type)) {
                final Set<Integer> set = (Set<Integer>)object;
                for (final Integer key : set) {
                    PlayerBuilding pb = this.buildingService.getPlayerBuilding(playerId, key);
                    final Building building = (Building)this.buildingCache.get((Object)key);
                    if (building != null && pb == null) {
                        final Date nowDate = new Date();
                        pb = new PlayerBuilding();
                        pb.setBuildingId(building.getId());
                        pb.setState(0);
                        pb.setOutputType(building.getType());
                        pb.setAreaId((building.getType() >= 5 && building.getType() <= 8) ? 5 : ((int)building.getType()));
                        pb.setLv(1);
                        pb.setPlayerId(playerId);
                        pb.setUpdateTime(nowDate);
                        pb.setIsNew(0);
                        pb.setEventId(0);
                        pb.setSpeedUpNum(this.serialCache.get(building.getTimeT(), 1));
                        this.playerBuildingDao.create(pb);
                    }
                }
            }
            else if ("functionId".equalsIgnoreCase(type)) {
                final Set<Integer> set = (Set<Integer>)object;
                final char[] cs = pa.getFunctionId().toCharArray();
                for (final Integer funtionId : set) {
                    cs[funtionId] = '1';
                    if (funtionId == 27) {
                        this.dataGetter.getMarketService().openMarketFunction(new PlayerDto(playerId));
                    }
                    else if (funtionId == 33) {
                        this.dataGetter.getDinnerService().openDinnerFunction(playerId);
                    }
                    else if (funtionId == 39) {
                        this.dataGetter.getGiftService().openOnlineGiftFunctin(playerId);
                    }
                    else if (funtionId == 10) {
                        this.dataGetter.getWorldService().createRecord(playerId);
                        this.dataGetter.getWorldService().createWholeKill(playerId);
                        this.dataGetter.getActivityService().openPlayerScoreRank(playerId);
                        this.dataGetter.getActivityService().openDragonRecord(playerId);
                        this.dataGetter.getNationService().createPlayerTryRank(playerId);
                        this.dataGetter.getProtectService().openPRank(playerId);
                        this.dealCity(player);
                    }
                    else if (funtionId == 14) {
                        final Map<Integer, Integer> oMap = new HashMap<Integer, Integer>();
                        oMap.put(playerId, 1);
                        final JsonDocument doc = new JsonDocument();
                        doc.startObject();
                        doc.createElement("refreshMainCity", true);
                        doc.endObject();
                        Players.push(playerId, PushCommand.PUSH_UPDATE, doc.toByte());
                    }
                    else if (funtionId == 16) {
                        this.dataGetter.getIncenseService().openIncense(playerId);
                    }
                    else if (funtionId == 46) {
                        final PlayerBatRank playerBatRank = new PlayerBatRank();
                        playerBatRank.setPlayerId(playerId);
                        playerBatRank.setRank(0);
                        playerBatRank.setReward(null);
                        playerBatRank.setLastRankTime(new Date());
                        playerBatRank.setRankScore(0);
                        playerBatRank.setBuyTimesToday(0);
                        playerBatRank.setRankBatNum(10);
                        final int done = this.dataGetter.getPlayerBatRankDao().create(playerBatRank);
                    }
                    else if (funtionId == 17) {
                        this.dataGetter.getStoreService().refreshItem(playerId, 2, this.dataGetter.getPlayerStoreDao().read(playerId), (Chargeitem)this.dataGetter.getChargeitemCache().get((Object)9));
                    }
                    else {
                        if (funtionId == 52) {
                            continue;
                        }
                        if (funtionId == 19) {
                            this.dataGetter.getTechService().openTechFunction(playerId);
                        }
                        else if (funtionId == 38) {
                            Players.push(playerId, PushCommand.PUSH_UPDATE, JsonBuilder.getSimpleJson("haveDayGift", true));
                        }
                        else if (funtionId == 56) {
                            this.dataGetter.getBuildingService().openFreeConstruction(playerId);
                        }
                        else if (funtionId == 5) {
                            this.dataGetter.getBuildingService().openLumberArea(playerId);
                        }
                        else if (funtionId == 29) {
                            this.dataGetter.getWeaponService().openWeaponFunction(playerId);
                        }
                        else {
                            if (funtionId != 51) {
                                continue;
                            }
                            this.playerService.afterOpenFunction(51, playerId);
                        }
                    }
                }
                this.dataGetter.getPlayerAttributeDao().updateFunction(playerId, new String(cs));
            }
            else if ("new_tech".equalsIgnoreCase(type)) {
                final Set<Integer> set = (Set<Integer>)object;
                for (final Integer key : set) {
                    if (this.dataGetter.getPlayerTechDao().getPlayerTech(playerId, key) == null) {
                        final PlayerTech playerTech = new PlayerTech();
                        playerTech.setPlayerId(playerId);
                        playerTech.setTechId(key);
                        this.dataGetter.getPlayerTechDao().create(playerTech);
                        this.dataGetter.getBattleDataCache().removeTroopEffect(playerId, playerTech.getTechId());
                    }
                }
            }
            else {
                if ("Leader".equalsIgnoreCase(type) || "Politics".equalsIgnoreCase(type) || "Intel".equalsIgnoreCase(type) || "Strength".equalsIgnoreCase(type)) {
                    continue;
                }
                if ("arms_weapon".equalsIgnoreCase(type)) {
                    final Set<Integer> set = (Set<Integer>)object;
                    for (final Integer key : set) {
                        if (this.dataGetter.getPlayerWeaponDao().getPlayerWeapon(playerId, key) == null) {
                            final PlayerWeapon playerWeapon = new PlayerWeapon();
                            playerWeapon.setPlayerId(playerId);
                            playerWeapon.setWeaponId(key);
                            playerWeapon.setType(((ArmsWeapon)this.dataGetter.getArmsWeaponCache().get((Object)key)).getType());
                            playerWeapon.setLv(0);
                            playerWeapon.setGemId("");
                            playerWeapon.setTimes(0);
                            this.dataGetter.getPlayerWeaponDao().create(playerWeapon);
                        }
                    }
                    this.dataGetter.getBattleDataCache().refreshWeaponEffect(playerId);
                }
                else if ("new_incense".equalsIgnoreCase(type)) {
                    final Set<Integer> set = (Set<Integer>)object;
                    for (final Integer godId : set) {
                        this.dataGetter.getIncenseService().addIncenseGod(playerId, godId);
                    }
                }
                else if ("refresh_store".equalsIgnoreCase(type)) {
                    Players.push(playerId, PushCommand.PUSH_STORE, JsonBuilder.getSimpleJson("store", 1));
                }
                else if ("refresh_store_equip".equalsIgnoreCase(type)) {
                    this.dataGetter.getStoreService().refreshItem(playerId, 1, false);
                }
                else {
                    if (!"new_construction".equalsIgnoreCase(type)) {
                        continue;
                    }
                    final Set<Integer> set = (Set<Integer>)object;
                    final PlayerConstants pc = this.dataGetter.getPlayerConstantsDao().read(playerId);
                    int workId = pc.getExtraNum() + 1;
                    for (int m = 0; m < set.size(); ++m) {
                        final PlayerBuildingWork pbw = new PlayerBuildingWork();
                        pbw.setPlayerId(playerId);
                        pbw.setStartTime(new Date());
                        pbw.setEndTime(new Date());
                        pbw.setTargetBuildId(0);
                        pbw.setWorkId(workId);
                        pbw.setWorkState(0);
                        this.dataGetter.getPlayerBuildingWorkDao().create(pbw);
                        ++workId;
                    }
                }
            }
        }
    }
    
    private boolean dealTaskReward(final String rewardStr, final Map<String, Object> rewardMap) {
        final String[] s = rewardStr.split(",");
        final String type = s[0].trim();
        if ("new_building".equalsIgnoreCase(type)) {
            this.collectData("new_building", rewardMap, Integer.valueOf(s[1]));
        }
        else if ("functionId".equalsIgnoreCase(type)) {
            this.collectData("functionId", rewardMap, Integer.valueOf(s[1]));
        }
        else if ("new_tech".equalsIgnoreCase(type)) {
            this.collectData("new_tech", rewardMap, Integer.valueOf(s[1]));
        }
        else if ("new_incense".equalsIgnoreCase(type)) {
            this.collectData("new_incense", rewardMap, Integer.valueOf(s[1]));
        }
        else if (!"Leader".equalsIgnoreCase(type) && !"Politics".equalsIgnoreCase(type) && !"Intel".equalsIgnoreCase(type) && !"Strength".equalsIgnoreCase(type)) {
            if ("arms_weapon".equalsIgnoreCase(type)) {
                this.collectData("arms_weapon", rewardMap, Integer.valueOf(s[1]));
            }
            else if ("refresh_store".equalsIgnoreCase(type)) {
                this.collectData("refresh_store", rewardMap, 1);
            }
            else if ("refresh_store_equip".equalsIgnoreCase(type)) {
                this.collectData("refresh_store_equip", rewardMap, 1);
            }
            else if ("new_construction".equalsIgnoreCase(type)) {
                final Object obj = rewardMap.get("new_construction");
                int num = 1;
                if (obj != null) {
                    final Set<Integer> set = (Set<Integer>)obj;
                    num = set.size() + 1;
                }
                this.collectData("new_construction", rewardMap, num);
            }
        }
        return true;
    }
    
    private void collectData(final String rewardType, final Map<String, Object> rewardMap, final int value) {
        Set<Integer> set = null;
        if (!rewardMap.containsKey(rewardType)) {
            set = new HashSet<Integer>();
            rewardMap.put(rewardType, set);
        }
        else {
            set = rewardMap.get(rewardType);
        }
        set.add(value);
    }
    
    private void dealCity(final Player player) {
        final int mainCityId = WorldCityCommon.nationMainCityIdMap.get(player.getForceId());
        final Set<Integer> temp = this.dataGetter.getWorldRoadCache().getNeighbors(mainCityId);
        final Set<Integer> mainNeiSet = new HashSet<Integer>();
        int sn = 1;
        for (final Integer key : temp) {
            if (sn++ % 3 == 0) {
                continue;
            }
            mainNeiSet.add(key);
        }
        final List<City> list = this.dataGetter.getCityDao().getForceCities(player.getForceId());
        final Set<Integer> attedSet = new HashSet<Integer>();
        for (final City city : list) {
            attedSet.add(city.getId());
        }
        for (final Integer key2 : mainNeiSet) {
            attedSet.add(key2);
        }
        final Set<Integer> canAttSet = new HashSet<Integer>();
        for (final City city2 : list) {
            final Set<Integer> neighbors = this.dataGetter.getWorldRoadCache().getNeighbors(city2.getId());
            for (final Integer key3 : neighbors) {
                if (attedSet.contains(key3)) {
                    continue;
                }
                canAttSet.add(key3);
            }
        }
        for (final Integer cityId : mainNeiSet) {
            final Set<Integer> neighbors = this.dataGetter.getWorldRoadCache().getNeighbors(cityId);
            for (final Integer key3 : neighbors) {
                if (attedSet.contains(key3)) {
                    continue;
                }
                canAttSet.add(key3);
            }
        }
        final StringBuilder attedSb = new StringBuilder();
        for (final Integer key4 : attedSet) {
            attedSb.append(key4).append(",");
        }
        final StringBuilder canAttSb = new StringBuilder();
        for (final Integer key5 : canAttSet) {
            canAttSb.append(key5).append(",");
        }
    }
}
