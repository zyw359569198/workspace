package com.reign.gcld.battle.service;

import org.springframework.beans.factory.*;
import org.springframework.stereotype.*;
import com.reign.gcld.common.log.*;
import org.springframework.beans.factory.annotation.*;
import java.util.concurrent.atomic.*;
import com.reign.gcld.log.*;
import com.reign.gcld.common.util.*;
import com.reign.gcld.world.domain.*;
import com.reign.gcld.battle.scene.*;
import com.reign.gcld.battle.common.*;
import com.reign.gcld.world.service.*;
import com.reign.gcld.player.dto.*;
import com.reign.gcld.chat.common.*;
import com.reign.util.*;
import com.reign.gcld.world.common.*;
import com.reign.gcld.chat.service.*;
import com.reign.framework.json.*;
import com.reign.framework.netty.util.*;
import com.reign.framework.netty.servlet.*;
import java.util.*;
import com.reign.gcld.battle.domain.*;
import com.reign.gcld.rank.domain.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.gcld.activity.common.*;
import com.reign.gcld.activity.domain.*;
import com.reign.gcld.common.*;
import java.io.*;

@Component("timerBattleService")
public class TimerBattleService implements ITimerBattleService, InitializingBean
{
    private static final Logger timerLog;
    @Autowired
    private IBattleService battleService;
    @Autowired
    private IDataGetter dataGetter;
    private int nearlyChuJiManzuCapitalId;
    private int faDongForceId;
    private long lastCountryEAsAddTime;
    private Map<Integer, NationTaskConquerEAScheduler> conquerEASchedulerMap;
    public BitSet barbarainFoodArmyBitSet;
    AtomicInteger NationTaskExpeditionArmyVId;
    AtomicInteger activityNpcMaxVid;
    Map<String, Integer> composition2of3Map;
    
    static {
        timerLog = new TimerLogger();
    }
    
    public TimerBattleService() {
        this.nearlyChuJiManzuCapitalId = 0;
        this.faDongForceId = 0;
        this.lastCountryEAsAddTime = 0L;
        this.conquerEASchedulerMap = new HashMap<Integer, NationTaskConquerEAScheduler>();
        this.barbarainFoodArmyBitSet = new BitSet();
        this.NationTaskExpeditionArmyVId = null;
        this.activityNpcMaxVid = null;
        this.composition2of3Map = new HashMap<String, Integer>();
    }
    
    private void resetComposition2of3Map() {
        final String key1 = "1_2";
        final String key2 = "1_3";
        final String key3 = "2_3";
        this.composition2of3Map.put(key1, 0);
        this.composition2of3Map.put(key2, 0);
        this.composition2of3Map.put(key3, 0);
    }
    
    private Integer get2of3CompositionFlag(final Integer playerForce1, final Integer playerForce2) {
        String key = null;
        if (playerForce1 < playerForce2) {
            key = playerForce1 + "_" + playerForce2;
        }
        else {
            key = playerForce2 + "_" + playerForce1;
        }
        return this.composition2of3Map.get(key);
    }
    
    private void set2of3CompositionFlag(final Integer playerForce1, final Integer playerForce2) {
        String key = null;
        if (playerForce1 < playerForce2) {
            key = playerForce1 + "_" + playerForce2;
        }
        else {
            key = playerForce2 + "_" + playerForce1;
        }
        this.composition2of3Map.put(key, 1);
    }
    
    @Override
    public long getLastCountryEAsAddTime() {
        return this.lastCountryEAsAddTime;
    }
    
    @Override
    public void addCountryNpc() {
        final long start = System.currentTimeMillis();
        final Map<Integer, List<WorldCity>> dis3Cities = this.dataGetter.getWorldCityCache().getDistanceLess3Cities();
        for (final Integer forceId : dis3Cities.keySet()) {
            final List<WorldCity> wcList = dis3Cities.get(forceId);
            for (final WorldCity wc : wcList) {
                try {
                    if (WorldCityCommon.specialNationIdMap.containsKey(wc.getId())) {
                        continue;
                    }
                    this.battleService.npcStartOrJoinBattle(wc, forceId);
                }
                catch (Exception e) {
                    BattleSceneLog.getInstance().error("TimerBattleService addCountryNpc CityName:" + wc.getName() + " CityId" + wc.getId());
                    e.printStackTrace();
                }
            }
        }
        TimerBattleService.timerLog.info(LogUtil.formatThreadLog("TimerBattleService", "addCountryNpc", 2, System.currentTimeMillis() - start, ""));
    }
    
    @Override
    public void resetVip3PhantomCountEachDay() {
    }
    
    @Override
    public void BarbarainInvade() {
        try {
            this.barbarainFoodArmyBitSet.clear();
            final BarbarainInvader barbarainInvader = new BarbarainInvader(this.dataGetter);
            barbarainInvader.start();
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("BarbarainInvade catch exception", e);
        }
    }
    
    @Override
    public void BarbarainInvadeRecover() {
        try {
            this.barbarainFoodArmyBitSet.clear();
            final long endTime = this.dataGetter.getRankService().getBarbarianNationTaskEnd();
            if (endTime <= 0L || endTime < System.currentTimeMillis()) {
                return;
            }
            final BarbarainInvader barbarainInvader = new BarbarainInvader(this.dataGetter, endTime);
            barbarainInvader.start();
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("BarbarainInvadeRecover catch exception", e);
        }
    }
    
    @Override
    public void worldExpeditionAddEachHour() {
        final long startTime = System.currentTimeMillis();
        try {
            final String serverTime = Configuration.getProperty("gcld.server.time");
            final long start = Long.parseLong(serverTime);
            final long now = System.currentTimeMillis();
            final Calendar calendar1 = Calendar.getInstance();
            calendar1.setTime(new Date(start));
            calendar1.set(6, calendar1.get(6));
            calendar1.set(11, 18);
            calendar1.set(12, 0);
            calendar1.set(13, 0);
            final long beginAddTime = calendar1.getTime().getTime();
            if (now >= beginAddTime) {
                this.lastCountryEAsAddTime = now;
                final String sysNpc = Configuration.getProperty("gcld.battle.sysNpc");
                if (sysNpc.equals("1")) {
                    this.addRoundEAs();
                }
            }
            TimerBattleService.timerLog.info(LogUtil.formatThreadLog("TimerBattleService", "worldExpeditionAddEachHour", 2, System.currentTimeMillis() - startTime, "param:"));
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("worldExpeditionAddEachHour catch exception", e);
        }
    }
    
    public void addRoundEAs() {
        try {
            final EfLv eflv = (EfLv)this.dataGetter.getEfLvCache().get((Object)WorldCityCommon.MAX_COUNTRY_LV);
            if (eflv == null) {
                ErrorSceneLog.getInstance().appendErrorMsg("eflv is null from EfLvCache").append("maxCountryLv", WorldCityCommon.MAX_COUNTRY_LV).flush();
            }
            int expeditionForceId = 0;
            Map<Integer, List<EfL>> eflMap = null;
            final List<Integer> EAvIdList = null;
            Integer[] weiArmyIds = null;
            Integer[] shuArmyIds = null;
            Integer[] wuArmyIds = null;
            double pro = 0.0;
            if (this.dataGetter.getRankService().hasNationTasks(1) == 4) {
                pro = eflv.getTicketProbArray()[1];
            }
            else {
                pro = eflv.getTicketProbArray()[0];
            }
            if (WebUtil.nextDouble() <= pro) {
                weiArmyIds = eflv.getTicketArmyIds();
                shuArmyIds = eflv.getTicketArmyIds();
                wuArmyIds = eflv.getTicketArmyIds();
            }
            else {
                weiArmyIds = eflv.getWeiArmyIds();
                shuArmyIds = eflv.getShuArmyIds();
                wuArmyIds = eflv.getWuArmyIds();
            }
            expeditionForceId = 1;
            eflMap = this.dataGetter.getEfLCache().getForceIdLinesMap().get(expeditionForceId);
            this.addEAsForOneCountry(expeditionForceId, weiArmyIds, eflMap, EAvIdList, eflv);
            expeditionForceId = 2;
            eflMap = this.dataGetter.getEfLCache().getForceIdLinesMap().get(expeditionForceId);
            this.addEAsForOneCountry(expeditionForceId, shuArmyIds, eflMap, EAvIdList, eflv);
            expeditionForceId = 3;
            eflMap = this.dataGetter.getEfLCache().getForceIdLinesMap().get(expeditionForceId);
            this.addEAsForOneCountry(expeditionForceId, wuArmyIds, eflMap, EAvIdList, eflv);
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("WorldExpeditionManager.addRoundEAs catch exception", e);
        }
    }
    
    private void addEAsForOneCountry(final int expeditionForceId, final Integer[] armyIds, final Map<Integer, List<EfL>> eflMap, final List<Integer> EAvIdList, final EfLv eflv) {
        final List<ExpeditionArmy> list = new ArrayList<ExpeditionArmy>();
        final Date date = new Date();
        for (final Integer direction : eflMap.keySet()) {
            final List<EfL> eflList = eflMap.get(direction);
            final int index = WebUtil.nextInt(eflList.size());
            final EfL efl = eflList.get(index);
            final Integer[] cityIds = efl.getCityIds();
            final int initLocationId = cityIds[0];
            final Battle capitalBattle = NewBattleManager.getInstance().getBattleByDefId(3, initLocationId);
            if (capitalBattle != null) {
                ErrorSceneLog.getInstance().appendErrorMsg("capital is in battle").appendBattleId(capitalBattle.getBattleId()).append("initLocationId", initLocationId).append("efl", efl.getId()).flush();
                if (list.size() > 0) {
                    this.dataGetter.getExpeditionArmyDao().batchCreate(list);
                }
                return;
            }
            final City nextCity = CityDataCache.cityArray[initLocationId];
            if (nextCity == null || nextCity.getForceId() != expeditionForceId) {
                ErrorSceneLog.getInstance().appendErrorMsg("capital forceId error").append("initLocationId", initLocationId).append("expeditionForceId", expeditionForceId).append("initLocationId ForceId", nextCity.getForceId()).flush();
                if (list.size() > 0) {
                    this.dataGetter.getExpeditionArmyDao().batchCreate(list);
                }
                return;
            }
            final int armyId = armyIds[WebUtil.nextInt(armyIds.length)];
            final Army army = (Army)this.dataGetter.getArmyCache().get((Object)armyId);
            for (int i = 0; i < efl.getN(); ++i) {
                final ExpeditionArmy expeditionArmy = new ExpeditionArmy();
                expeditionArmy.setCreateTime(date);
                expeditionArmy.setLocationId(initLocationId);
                expeditionArmy.setForceId(expeditionForceId);
                expeditionArmy.setEfLvId(eflv.getEfLv());
                expeditionArmy.setEfLId(efl.getId());
                expeditionArmy.setTacticval(1);
                expeditionArmy.setState(0);
                expeditionArmy.setArmyId(armyId);
                expeditionArmy.setHp(army.getArmyHp());
                expeditionArmy.setVId(0);
                list.add(expeditionArmy);
            }
        }
        if (list.size() > 0) {
            this.dataGetter.getExpeditionArmyDao().batchCreate(list);
        }
    }
    
    @Override
    public void worldDoExpeditionEachMinute() {
        final long start = System.currentTimeMillis();
        try {
            this.doExpedition();
            this.addNationTaskExpeditionArmyTry();
            TimerBattleService.timerLog.info(LogUtil.formatThreadLog("TimerBattleService", "worldDoExpeditionEachMinute", 2, System.currentTimeMillis() - start, "param:"));
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("worldDoExpeditionEachMinute catch exception", e);
        }
    }
    
    public void doExpedition() {
        try {
            final List<ExpeditionArmy> list = this.dataGetter.getExpeditionArmyDao().getModels();
            for (final ExpeditionArmy tempEA : list) {
                final int nowCityId = tempEA.getLocationId();
                final Battle cityBattle = NewBattleManager.getInstance().getBattleByDefId(3, nowCityId);
                if (cityBattle == null) {
                    this.moveAheadOneStepWhenNowCityIsFreeEA(nowCityId, tempEA);
                }
            }
            final List<NationTaskExpeditionArmy> nationTaslEAList = this.dataGetter.getNationTaskExpeditionArmyDao().getModels();
            for (final NationTaskExpeditionArmy tempEA2 : nationTaslEAList) {
                if (tempEA2.getMoveLine() == null) {
                    continue;
                }
                final int nowCityId2 = tempEA2.getLocationId();
                final Battle cityBattle2 = NewBattleManager.getInstance().getBattleByDefId(3, nowCityId2);
                if (cityBattle2 != null) {
                    continue;
                }
                this.moveAheadOneStepWhenNowCityIsFreeNTEA(nowCityId2, tempEA2);
            }
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("WorldExpeditionManager.doExpedition catch exception", e);
        }
    }
    
    private void addNationTaskExpeditionArmyTry() {
        try {
            final long now = System.currentTimeMillis();
            final Set<Integer> citySet = new HashSet<Integer>();
            citySet.addAll(this.conquerEASchedulerMap.keySet());
            for (final int targetCityId : citySet) {
                if (!this.dataGetter.getRankService().duringTaskByTarget(targetCityId)) {
                    this.conquerEASchedulerMap.remove(targetCityId);
                }
                else {
                    final NationTaskConquerEAScheduler nationTaskConquerEAScheduler = this.conquerEASchedulerMap.get(targetCityId);
                    final long interval = this.dataGetter.getKtCoNpcCache().getKtCoNpcOfNow().getAttInt();
                    int bitId = (int)((now - nationTaskConquerEAScheduler.startTime) / (interval * 60000L));
                    bitId *= 2;
                    if (nationTaskConquerEAScheduler.bitSet.get(bitId)) {
                        continue;
                    }
                    int attForceId1 = 0;
                    int attForceId2 = 0;
                    final City city = this.dataGetter.getCityDao().read(targetCityId);
                    switch (city.getForceId()) {
                        case 1: {
                            attForceId1 = 2;
                            attForceId2 = 3;
                            break;
                        }
                        case 2: {
                            attForceId1 = 1;
                            attForceId2 = 3;
                            break;
                        }
                        case 3: {
                            attForceId1 = 1;
                            attForceId2 = 2;
                            break;
                        }
                    }
                    this.addNationTaskExpeditionArmyForConquerCountry(attForceId1, targetCityId);
                    this.addNationTaskExpeditionArmyForConquerCountry(attForceId2, targetCityId);
                    nationTaskConquerEAScheduler.bitSet.set(bitId);
                }
            }
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("WorldExpeditionManager.addNationTaskExpeditionArmyTry catch exception", e);
        }
    }
    
    private void moveAheadOneStepWhenNowCityIsFreeNTEA(final int nowCityId, final NationTaskExpeditionArmy tempEA) {
        final Builder builder = BuilderFactory.getInstance().getBuilder(3);
        final String line = tempEA.getMoveLine();
        final String[] lineArray = line.split(";");
        final Integer[] cityIds = new Integer[lineArray.length];
        for (int i = 0; i < lineArray.length; ++i) {
            final String s = lineArray[i];
            if (!s.trim().isEmpty()) {
                try {
                    cityIds[i] = Integer.parseInt(s);
                }
                catch (NumberFormatException e) {
                    ErrorSceneLog.getInstance().appendErrorMsg("NumberFormatException").append("tempBarEA vId", tempEA.getVId()).append("move line", line).append("s", s).flush();
                    return;
                }
            }
        }
        if (cityIds[cityIds.length - 1] == nowCityId) {
            return;
        }
        Integer nextCityId = null;
        for (int j = 0; j < cityIds.length - 1; ++j) {
            if (cityIds[j] == nowCityId) {
                nextCityId = cityIds[j + 1];
                break;
            }
        }
        if (nextCityId == null) {
            ErrorSceneLog.getInstance().appendErrorMsg("nextCityId is null").append("move line", line).append("now cityId", nowCityId).append("now cityName", ((WorldCity)this.dataGetter.getWorldCityCache().get((Object)nowCityId)).getName()).append("tempEA vId", tempEA.getVId()).appendClassName("TimerBattleService").appendMethodName("moveAheadOneStepWhenNowCityIsFree").flush();
            return;
        }
        final EfLv eflv = (EfLv)this.dataGetter.getEfLvCache().get((Object)tempEA.getTableId());
        Battle nextCityBattle = NewBattleManager.getInstance().getBattleByDefId(3, nextCityId);
        final int tempEAForceId = tempEA.getForceId();
        if (nextCityBattle != null) {
            int battleSide = -1;
            if (nextCityBattle.getDefBaseInfo().getForceId() == tempEAForceId) {
                battleSide = 0;
            }
            else {
                battleSide = 1;
            }
            final CampArmy tempEACa = builder.copyArmyFromNationTaskExpeditionArmy(this.dataGetter, eflv, nextCityBattle, tempEA, battleSide);
            nextCityBattle.joinCampArmy(this.dataGetter, battleSide, tempEACa);
            this.dataGetter.getNationTaskExpeditionArmyDao().updateLocationAndState(tempEA.getVId(), nextCityId, 3);
        }
        else {
            final City nextCity = this.dataGetter.getCityDao().read(nextCityId);
            if (nextCity.getForceId() == tempEAForceId) {
                this.dataGetter.getNationTaskExpeditionArmyDao().updateLocationAndState(tempEA.getVId(), nextCityId, 0);
            }
            else {
                this.dataGetter.getNationTaskExpeditionArmyDao().updateLocationAndState(tempEA.getVId(), nextCityId, 0);
                final WorldCity wc = (WorldCity)this.dataGetter.getWorldCityCache().get((Object)nextCityId);
                final String battleId = CityBuilder.getBattleId(this.dataGetter, tempEAForceId, wc.getId());
                final Terrain terrain = builder.getTerrain(-1, wc.getId(), this.dataGetter);
                nextCityBattle = NewBattleManager.getInstance().createBattle(battleId);
                if (nextCityBattle == null) {
                    ErrorSceneLog.getInstance().appendErrorMsg("create battle fail").appendBattleId(battleId).append("tempEA vId", tempEA.getVId()).append("cityId", wc.getId()).append("cityName", wc.getName()).appendClassName("TimerBattleService").appendMethodName("moveAheadOneStepWhenNowCityIsFree").flush();
                    return;
                }
                final BattleAttacker battleAttacker = new BattleAttacker();
                battleAttacker.attType = 6;
                battleAttacker.attForceId = tempEAForceId;
                battleAttacker.attPlayerId = -7;
                nextCityBattle.init(battleAttacker, 3, nextCityId, this.dataGetter, false, terrain.getValue());
                builder.dealUniqueStaff(this.dataGetter, nextCityBattle, -1, nextCityId);
            }
        }
    }
    
    private void moveAheadOneStepWhenNowCityIsFreeEA(final int nowCityId, final ExpeditionArmy tempEA) {
        final Builder builder = BuilderFactory.getInstance().getBuilder(3);
        final EfL lineEA = (EfL)this.dataGetter.getEfLCache().get((Object)tempEA.getEfLId());
        if (lineEA == null) {
            ErrorSceneLog.getInstance().appendErrorMsg("EfL is null from EfLCache").append("cityId", nowCityId).append("cityName", ((WorldCity)this.dataGetter.getWorldCityCache().get((Object)nowCityId)).getName()).append("tempEA vId", tempEA.getVId()).append("efl Id", tempEA.getEfLId()).appendClassName("WorldExpeditionManager").appendMethodName("moveAheadOneStepWhenNowCityIsFreeEA").flush();
        }
        final Integer[] cityIds = lineEA.getCityIds();
        if (cityIds[cityIds.length - 1] == nowCityId) {
            return;
        }
        Integer nextCityId = null;
        for (int i = 0; i < cityIds.length - 1; ++i) {
            if (cityIds[i] == nowCityId) {
                nextCityId = cityIds[i + 1];
                break;
            }
        }
        if (nextCityId == null) {
            ErrorSceneLog.getInstance().appendErrorMsg("nextCityId is null").append("cityIds", lineEA.getL()).append("now cityId", nowCityId).append("now cityName", ((WorldCity)this.dataGetter.getWorldCityCache().get((Object)nowCityId)).getName()).append("tempEA vId", tempEA.getVId()).append("efl Id", tempEA.getEfLId()).appendClassName("WorldExpeditionManager").appendMethodName("moveAheadOneStepWhenNowCityIsFreeEA").flush();
            return;
        }
        final int nextCityFlag = CityService.getCityFlag(nextCityId);
        if (nextCityFlag != 0) {
            return;
        }
        final EfLv eflv = (EfLv)this.dataGetter.getEfLvCache().get((Object)tempEA.getEfLvId());
        Battle nextCityBattle = NewBattleManager.getInstance().getBattleByDefId(3, nextCityId);
        final int EAForceId = tempEA.getForceId();
        if (nextCityBattle != null) {
            int battleSide = -1;
            if (nextCityBattle.getDefBaseInfo().getForceId() == EAForceId) {
                battleSide = 0;
            }
            else {
                battleSide = 1;
            }
            final CampArmy EACa = builder.copyArmyformExpeditionArmy(this.dataGetter, eflv, nextCityBattle, tempEA, battleSide);
            nextCityBattle.joinCampArmy(this.dataGetter, battleSide, EACa);
            this.dataGetter.getExpeditionArmyDao().updateLocationAndState(tempEA.getVId(), nextCityId, 3);
        }
        else {
            final City nextCity = this.dataGetter.getCityDao().read(nextCityId);
            if (nextCity.getForceId() == EAForceId) {
                this.dataGetter.getExpeditionArmyDao().updateLocationAndState(tempEA.getVId(), nextCityId, 0);
            }
            else {
                this.dataGetter.getExpeditionArmyDao().updateLocationAndState(tempEA.getVId(), nextCityId, 0);
                final WorldCity wc = (WorldCity)this.dataGetter.getWorldCityCache().get((Object)nextCityId);
                final String battleId = CityBuilder.getBattleId(this.dataGetter, EAForceId, wc.getId());
                final Terrain terrain = builder.getTerrain(-1, wc.getId(), this.dataGetter);
                nextCityBattle = NewBattleManager.getInstance().createBattle(battleId);
                if (nextCityBattle == null) {
                    ErrorSceneLog.getInstance().appendErrorMsg("create battle fail").appendBattleId(battleId).append("EA vId", tempEA.getVId()).append("cityId", wc.getId()).append("cityName", wc.getName()).appendClassName("WorldExpeditionManager").appendMethodName("moveAheadOneStepWhenNowCityIsFreeEA").flush();
                    return;
                }
                final BattleAttacker battleAttacker = new BattleAttacker();
                battleAttacker.attType = 5;
                battleAttacker.attForceId = EAForceId;
                battleAttacker.attPlayerId = -5;
                battleAttacker.EA = tempEA;
                battleAttacker.eflv = eflv;
                nextCityBattle.init(battleAttacker, 3, nextCityId, this.dataGetter, false, terrain.getValue());
                builder.dealUniqueStaff(this.dataGetter, nextCityBattle, -1, nextCityId);
                String attName = "null";
                switch (EAForceId) {
                    case 1: {
                        attName = LocalMessages.YUAN_ZHEN_JUN_WEI;
                        break;
                    }
                    case 2: {
                        attName = LocalMessages.YUAN_ZHEN_JUN_SHU;
                        break;
                    }
                    case 3: {
                        attName = LocalMessages.YUAN_ZHEN_JUN_WU;
                        break;
                    }
                }
                boolean isElite = false;
                String armyPrefix = "";
                try {
                    final int generalId = tempEA.getArmyId();
                    final General general = (General)this.dataGetter.getGeneralCache().get((Object)generalId);
                    final Troop troop = (Troop)this.dataGetter.getTroopCache().get((Object)general.getTroop());
                    final String name = general.getName();
                    if (name.equalsIgnoreCase(LocalMessages.ELITE_EA_ARMY_NAME)) {
                        isElite = true;
                    }
                    if (troop.getTroopDrop() != null && troop.getTroopDrop().getDropAndMap().get(1023) != null) {
                        armyPrefix = LocalMessages.FAN_BEI_QUAN_PREFIX;
                    }
                }
                catch (Exception e) {
                    ErrorSceneLog.getInstance().appendErrorMsg("caculate isElite catch Exception").append("tempEA vId", tempEA.getVId()).appendClassName("TimerBattleService").appendMethodName("moveAheadOneStepWhenNowCityIsFreeEA").flush();
                    ErrorSceneLog.getInstance().error(e);
                }
                final int boBaoToForceId = nextCity.getForceId();
                if (isElite && (boBaoToForceId == 1 || boBaoToForceId == 2 || boBaoToForceId == 3)) {
                    final String EAInvadeBoBaoMsg = MessageFormatter.format(LocalMessages.YUAN_ZHEN_JUN_INVADE_BOBAO_FORMAT, new Object[] { ColorUtil.getForceMsg(EAForceId, armyPrefix), ColorUtil.getForceMsg(EAForceId, attName), String.valueOf(LocalMessages.CITY) + ColorUtil.getForceMsg(boBaoToForceId, wc.getName()) });
                    try {
                        this.dataGetter.getChatService().sendBigNotice("COUNTRY", new PlayerDto(0, boBaoToForceId), EAInvadeBoBaoMsg, null);
                    }
                    catch (Exception e2) {
                        ErrorSceneLog.getInstance().appendErrorMsg("EAInvadeBoBaoMsg catch Exception").append("boBaoToForceId", boBaoToForceId).append("EAInvadeBoBaoMsg", EAInvadeBoBaoMsg).appendClassName("TimerBattleService").appendMethodName("moveAheadOneStepWhenNowCityIsFreeEA").flush();
                    }
                }
            }
        }
    }
    
    public void Init() {
        try {
            this.lastCountryEAsAddTime = System.currentTimeMillis();
            final int maxVid = this.dataGetter.getNationTaskExpeditionArmyDao().getMaxVid();
            this.NationTaskExpeditionArmyVId = new AtomicInteger(maxVid);
            final int activityNpcMaxVid = this.dataGetter.getActivityNpcDao().getMaxVid();
            this.activityNpcMaxVid = new AtomicInteger(activityNpcMaxVid);
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("WorldExpeditionManager.doExpedition catch exception", e);
        }
    }
    
    private void sendMsgAfterFaDong(final ForceInfo forceInfo, final int manZuCapitalId) {
        try {
            String shouMaiForceName = null;
            switch (forceInfo.getForceId()) {
                case 1: {
                    shouMaiForceName = LocalMessages.T_FORCE_WEI;
                    break;
                }
                case 2: {
                    shouMaiForceName = LocalMessages.T_FORCE_SHU;
                    break;
                }
                case 3: {
                    shouMaiForceName = LocalMessages.T_FORCE_WU;
                    break;
                }
            }
            String counterForceName = null;
            int counterForceId = 0;
            switch (manZuCapitalId) {
                case 251: {
                    counterForceName = LocalMessages.T_FORCE_WEI;
                    counterForceId = 1;
                    break;
                }
                case 250: {
                    counterForceName = LocalMessages.T_FORCE_SHU;
                    counterForceId = 2;
                    break;
                }
                case 252: {
                    counterForceName = LocalMessages.T_FORCE_WU;
                    counterForceId = 3;
                    break;
                }
            }
            final String boBaoMsgWorld1 = MessageFormatter.format(LocalMessages.MANZU_SHOUMAI_RUQIN_WORLD_BOBAO_MSG_FORMAT, new Object[] { ColorUtil.getSpecialColorMsg(String.valueOf(shouMaiForceName) + LocalMessages.T_FORCE_NATION), ColorUtil.getSpecialColorMsg(String.valueOf(counterForceName) + LocalMessages.T_FORCE_NATION) });
            final String boBaoMsgWorld2 = MessageFormatter.format(LocalMessages.MANZU_SHOUMAI_RUQIN_WORLD_BOBAO_MSG_FORMAT, new Object[] { ColorUtil.getForceMsg(forceInfo.getForceId(), String.valueOf(shouMaiForceName) + LocalMessages.T_FORCE_NATION), ColorUtil.getForceMsg(forceInfo.getForceId(), String.valueOf(counterForceName) + LocalMessages.T_FORCE_NATION) });
            this.dataGetter.getChatService().sendSystemChat("GLOBAL", 0, 0, boBaoMsgWorld1, null);
            this.dataGetter.getChatService().sendBigNotice("GLOBAL", null, boBaoMsgWorld2, null);
            this.dataGetter.getChatService().sendSystemChat("GLOBAL", 0, counterForceId, LocalMessages.MANZU_SHOUMAI_RUQIN_COUNTRY_BOBAO_MSG_FORMAT, null);
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("sendMsgAfterFaDong catch exception", e);
        }
    }
    
    @Override
    public void manZuExpeditionAddAfterFaDong(final ForceInfo forceInfo, final WorldPaidB worldPaidB, final int manZuCapitalId) {
        final long start = System.currentTimeMillis();
        try {
            TimerBattleService.timerLog.info(LogUtil.formatThreadLog("TimerBattleService", "manZuExpeditionAddAfterFaDong", 0, 0L, "manZuCapitalId:" + manZuCapitalId));
            if (!WorldCityCommon.barbarainCitySet.contains(manZuCapitalId)) {
                ErrorSceneLog.getInstance().appendErrorMsg("cityId is not manzu capital").append("cityId", manZuCapitalId).appendMethodName("manZuExpeditionAddAfterFaDong").appendClassName("TimerBattleService").flush();
                return;
            }
            int manZuEAForceId = 0;
            Integer[] armyIds = null;
            String line1 = null;
            String line2 = null;
            String line3 = null;
            switch (manZuCapitalId) {
                case 251: {
                    manZuEAForceId = 101;
                    armyIds = worldPaidB.getWeiArmyIds();
                    line1 = worldPaidB.getWeiR1();
                    line2 = worldPaidB.getWeiR2();
                    line3 = worldPaidB.getWeiR3();
                    break;
                }
                case 250: {
                    manZuEAForceId = 102;
                    armyIds = worldPaidB.getShuArmyIds();
                    line1 = worldPaidB.getShuR1();
                    line2 = worldPaidB.getShuR2();
                    line3 = worldPaidB.getShuR3();
                    break;
                }
                case 252: {
                    manZuEAForceId = 103;
                    armyIds = worldPaidB.getWuArmyIds();
                    line1 = worldPaidB.getWuR1();
                    line2 = worldPaidB.getWuR2();
                    line3 = worldPaidB.getWuR3();
                    break;
                }
                default: {
                    ErrorSceneLog.getInstance().appendErrorMsg("manZuCapitalId is invalid").append("manZuCapitalId", manZuCapitalId).appendMethodName("manZuExpeditionAddAfterFaDong").appendClassName("TimerBattleService").flush();
                    break;
                }
            }
            final Date date = new Date();
            final int state = 0;
            final int n1 = worldPaidB.getN1();
            final int n2 = worldPaidB.getN2();
            final int n3 = worldPaidB.getN3();
            final int armyLength = armyIds.length;
            final List<BarbarainExpeditionArmy> batchList = new LinkedList<BarbarainExpeditionArmy>();
            if (line1 != null) {
                for (int i = 0; i < n1; ++i) {
                    final BarbarainExpeditionArmy barbarainExpeditionArmy = new BarbarainExpeditionArmy();
                    barbarainExpeditionArmy.setCreateTime(date);
                    barbarainExpeditionArmy.setState(state);
                    barbarainExpeditionArmy.setLocationId(manZuCapitalId);
                    barbarainExpeditionArmy.setForceId(manZuEAForceId);
                    barbarainExpeditionArmy.setWorldPaidBId(worldPaidB.getId());
                    barbarainExpeditionArmy.setTacticval(1);
                    barbarainExpeditionArmy.setMoveLine(line1);
                    final int armyId = armyIds[WebUtil.nextInt(armyLength)];
                    final Army army = (Army)this.dataGetter.getArmyCache().get((Object)armyId);
                    barbarainExpeditionArmy.setArmyId(armyId);
                    barbarainExpeditionArmy.setHp(army.getArmyHp());
                    barbarainExpeditionArmy.setVId(0);
                    batchList.add(barbarainExpeditionArmy);
                }
            }
            if (line2 != null) {
                for (int i = 0; i < n2; ++i) {
                    final BarbarainExpeditionArmy barbarainExpeditionArmy = new BarbarainExpeditionArmy();
                    barbarainExpeditionArmy.setCreateTime(date);
                    barbarainExpeditionArmy.setState(state);
                    barbarainExpeditionArmy.setLocationId(manZuCapitalId);
                    barbarainExpeditionArmy.setForceId(manZuEAForceId);
                    barbarainExpeditionArmy.setWorldPaidBId(worldPaidB.getId());
                    barbarainExpeditionArmy.setTacticval(1);
                    barbarainExpeditionArmy.setMoveLine(line2);
                    final int armyId = armyIds[WebUtil.nextInt(armyLength)];
                    final Army army = (Army)this.dataGetter.getArmyCache().get((Object)armyId);
                    barbarainExpeditionArmy.setArmyId(armyId);
                    barbarainExpeditionArmy.setHp(army.getArmyHp());
                    barbarainExpeditionArmy.setVId(0);
                    batchList.add(barbarainExpeditionArmy);
                }
            }
            if (line3 != null) {
                for (int i = 0; i < n3; ++i) {
                    final BarbarainExpeditionArmy barbarainExpeditionArmy = new BarbarainExpeditionArmy();
                    barbarainExpeditionArmy.setCreateTime(date);
                    barbarainExpeditionArmy.setState(state);
                    barbarainExpeditionArmy.setLocationId(manZuCapitalId);
                    barbarainExpeditionArmy.setForceId(manZuEAForceId);
                    barbarainExpeditionArmy.setWorldPaidBId(worldPaidB.getId());
                    barbarainExpeditionArmy.setTacticval(1);
                    barbarainExpeditionArmy.setMoveLine(line3);
                    final int armyId = armyIds[WebUtil.nextInt(armyLength)];
                    final Army army = (Army)this.dataGetter.getArmyCache().get((Object)armyId);
                    barbarainExpeditionArmy.setArmyId(armyId);
                    barbarainExpeditionArmy.setHp(army.getArmyHp());
                    barbarainExpeditionArmy.setVId(0);
                    batchList.add(barbarainExpeditionArmy);
                }
            }
            this.dataGetter.getBarbarainExpeditionArmyDao().batchCreate(batchList);
            this.sendMsgAfterFaDong(forceInfo, manZuCapitalId);
            this.nearlyChuJiManzuCapitalId = manZuCapitalId;
            this.faDongForceId = forceInfo.getForceId();
            TimerBattleService.timerLog.info(LogUtil.formatThreadLog("TimerBattleService", "manZuExpeditionAddAfterFaDong", 2, System.currentTimeMillis() - start, "manZuCapitalId:" + manZuCapitalId));
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("manZuExpeditionAddAfterFaDong catch exception", e);
        }
    }
    
    private void sendFaDongBoBaoMsg() {
        String manZuForceName = null;
        String forceName = null;
        int defenderForceId = 0;
        try {
            if (this.nearlyChuJiManzuCapitalId > 0) {
                switch (this.nearlyChuJiManzuCapitalId) {
                    case 251: {
                        manZuForceName = LocalMessages.T_FORCE_BEIDI;
                        defenderForceId = 1;
                        forceName = LocalMessages.T_FORCE_WEI;
                        break;
                    }
                    case 250: {
                        manZuForceName = LocalMessages.T_FORCE_XIRONG;
                        defenderForceId = 2;
                        forceName = LocalMessages.T_FORCE_SHU;
                        break;
                    }
                    case 252: {
                        manZuForceName = LocalMessages.T_FORCE_DONGYI;
                        defenderForceId = 3;
                        forceName = LocalMessages.T_FORCE_WU;
                        break;
                    }
                }
                final String attackerMsg = MessageFormatter.format(LocalMessages.MANZU_SHOUMAI_CHUJI_ATTACKER_BOBAO_MSG_FORMAT, new Object[] { ColorUtil.getSpecialColorMsg(String.valueOf(forceName) + LocalMessages.T_FORCE_NATION) });
                this.dataGetter.getChatService().sendSystemChat("GLOBAL", 0, this.faDongForceId, attackerMsg, null);
                this.dataGetter.getChatService().sendSystemChat("GLOBAL", 0, defenderForceId, LocalMessages.MANZU_SHOUMAI_CHUJI_DEFENDER_BOBAO_MSG_FORMAT, null);
                this.nearlyChuJiManzuCapitalId = 0;
            }
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("sendFaDongBoBaoMsg catch exception", e);
            ErrorSceneLog.getInstance().appendErrorMsg("sendFaDongBoBaoMsg catch exception").append("this.nearlyChuJiManzuCapitalId", this.nearlyChuJiManzuCapitalId).append("this.faDongForceId", this.faDongForceId).append("manZuForceName", manZuForceName).append("defenderForceId", defenderForceId).flush();
        }
    }
    
    @Override
    public void worldDoManZuExpeditionEachMinute() {
        try {
            final long start = System.currentTimeMillis();
            TimerBattleService.timerLog.info(LogUtil.formatThreadLog("TimerBattleService", "worldDoManZuExpeditionEachMinute", 0, 0L, "param:"));
            this.sendFaDongBoBaoMsg();
            final List<BarbarainExpeditionArmy> list = this.dataGetter.getBarbarainExpeditionArmyDao().getModels();
            for (final BarbarainExpeditionArmy tempBarEA : list) {
                final int nowCityId = tempBarEA.getLocationId();
                final Battle cityBattle = NewBattleManager.getInstance().getBattleByDefId(3, nowCityId);
                if (cityBattle == null) {
                    this.moveAheadOneStepWhenNowCityIsFreeBarEA(nowCityId, tempBarEA);
                }
            }
            TimerBattleService.timerLog.info(LogUtil.formatThreadLog("TimerBattleService", "worldDoManZuExpeditionEachMinute", 2, System.currentTimeMillis() - start, "param:"));
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("worldDoManZuExpeditionEachMinute catch exception", e);
        }
    }
    
    private void moveAheadOneStepWhenNowCityIsFreeBarEA(final int nowCityId, final BarbarainExpeditionArmy tempBarEA) {
        final Builder builder = BuilderFactory.getInstance().getBuilder(3);
        final String line = tempBarEA.getMoveLine();
        final String[] lineArray = line.split(";");
        final Integer[] cityIds = new Integer[lineArray.length];
        for (int i = 0; i < lineArray.length; ++i) {
            final String s = lineArray[i];
            try {
                cityIds[i] = Integer.parseInt(s);
            }
            catch (NumberFormatException e) {
                ErrorSceneLog.getInstance().appendErrorMsg("NumberFormatException").append("tempBarEA vId", tempBarEA.getVId()).append("move line", tempBarEA.getMoveLine()).append("s", s).appendMethodName("moveAheadOneStepWhenNowCityIsFreeBarEA").flush();
                return;
            }
        }
        if (cityIds[cityIds.length - 1] == nowCityId) {
            return;
        }
        Integer nextCityId = null;
        for (int j = 0; j < cityIds.length - 1; ++j) {
            if (cityIds[j] == nowCityId) {
                nextCityId = cityIds[j + 1];
                break;
            }
        }
        if (nextCityId == null) {
            ErrorSceneLog.getInstance().appendErrorMsg("nextCityId is null").append("move line", line).append("now cityId", nowCityId).append("now cityName", ((WorldCity)this.dataGetter.getWorldCityCache().get((Object)nowCityId)).getName()).append("tempBarEA vId", tempBarEA.getVId()).appendClassName("TimerBattleService").appendMethodName("moveAheadOneStepWhenNowCityIsFreeBarEA").flush();
            return;
        }
        final int nextCityFlag = CityService.getCityFlag(nextCityId);
        if (nextCityFlag != 0) {
            return;
        }
        final WorldPaidB worldPaidB = (WorldPaidB)this.dataGetter.getWorldPaidBCache().get((Object)tempBarEA.getWorldPaidBId());
        Battle nextCityBattle = NewBattleManager.getInstance().getBattleByDefId(3, nextCityId);
        final int tempBarEAForceId = tempBarEA.getForceId();
        if (nextCityBattle != null) {
            int battleSide = -1;
            if (nextCityBattle.getDefBaseInfo().getForceId() == tempBarEAForceId) {
                battleSide = 0;
            }
            else {
                battleSide = 1;
            }
            final CampArmy tempBarEACa = builder.copyArmyformBarExpeditionArmy(this.dataGetter, worldPaidB, nextCityBattle, tempBarEA, battleSide);
            nextCityBattle.joinCampArmy(this.dataGetter, battleSide, tempBarEACa);
            this.dataGetter.getBarbarainExpeditionArmyDao().updateLocationAndState(tempBarEA.getVId(), nextCityId, 3);
        }
        else {
            final City nextCity = this.dataGetter.getCityDao().read(nextCityId);
            if (nextCity.getForceId() == tempBarEAForceId) {
                this.dataGetter.getBarbarainExpeditionArmyDao().updateLocationAndState(tempBarEA.getVId(), nextCityId, 0);
            }
            else {
                this.dataGetter.getBarbarainExpeditionArmyDao().updateLocationAndState(tempBarEA.getVId(), nextCityId, 0);
                final WorldCity wc = (WorldCity)this.dataGetter.getWorldCityCache().get((Object)nextCityId);
                final String battleId = CityBuilder.getBattleId(this.dataGetter, tempBarEAForceId, wc.getId());
                final Terrain terrain = builder.getTerrain(-1, wc.getId(), this.dataGetter);
                nextCityBattle = NewBattleManager.getInstance().createBattle(battleId);
                if (nextCityBattle == null) {
                    ErrorSceneLog.getInstance().appendErrorMsg("create battle fail").appendBattleId(battleId).append("tempBarEA vId", tempBarEA.getVId()).append("cityId", wc.getId()).append("cityName", wc.getName()).appendClassName("TimerBattleService").appendMethodName("moveAheadOneStepWhenNowCityIsFreeBarEA").flush();
                    return;
                }
                final BattleAttacker battleAttacker = new BattleAttacker();
                battleAttacker.attType = 6;
                battleAttacker.attForceId = tempBarEAForceId;
                battleAttacker.attPlayerId = -6;
                battleAttacker.barEA = tempBarEA;
                battleAttacker.worldPaidB = worldPaidB;
                nextCityBattle.init(battleAttacker, 3, nextCityId, this.dataGetter, false, terrain.getValue());
                builder.dealUniqueStaff(this.dataGetter, nextCityBattle, -1, nowCityId);
            }
        }
    }
    
    @Override
    public void worldManZuExpeditionBobaoEach30Minutes() {
        try {
            final long start = System.currentTimeMillis();
            TimerBattleService.timerLog.info(LogUtil.formatThreadLog("TimerBattleService", "worldManZuExpeditionBobaoEach30Minutes", 0, 0L, "param:"));
            CityEventManager.getInstance().bobaoAllPlayerEvent();
            final List<ForceInfo> list = this.dataGetter.getForceInfoDao().getModels();
            for (final ForceInfo forceInfo : list) {
                final CountryPrivilege countryPrivilege = WorldCityCommon.countryPrivilegeMap.get(forceInfo.getForceId());
                if (!countryPrivilege.canShouMaiManzu) {
                    continue;
                }
                int counterManzuCityId1 = 0;
                String boBaoMsg1 = null;
                int counterManzuCityId2 = 0;
                String boBaoMsg2 = null;
                switch (forceInfo.getForceId()) {
                    case 1: {
                        counterManzuCityId1 = 250;
                        boBaoMsg1 = MessageFormatter.format(LocalMessages.MANZU_SHOUMAI_WEI_BOBAO_MSG_FORMAT, new Object[] { ColorUtil.getForceMsg(2, LocalMessages.T_FORCE_SHU) });
                        counterManzuCityId2 = 252;
                        boBaoMsg2 = MessageFormatter.format(LocalMessages.MANZU_SHOUMAI_WEI_BOBAO_MSG_FORMAT, new Object[] { ColorUtil.getForceMsg(3, LocalMessages.T_FORCE_WU) });
                        break;
                    }
                    case 2: {
                        counterManzuCityId1 = 251;
                        boBaoMsg1 = MessageFormatter.format(LocalMessages.MANZU_SHOUMAI_WEI_BOBAO_MSG_FORMAT, new Object[] { ColorUtil.getForceMsg(1, LocalMessages.T_FORCE_WEI) });
                        counterManzuCityId2 = 252;
                        boBaoMsg2 = MessageFormatter.format(LocalMessages.MANZU_SHOUMAI_WEI_BOBAO_MSG_FORMAT, new Object[] { ColorUtil.getForceMsg(3, LocalMessages.T_FORCE_WU) });
                        break;
                    }
                    case 3: {
                        counterManzuCityId1 = 251;
                        boBaoMsg1 = MessageFormatter.format(LocalMessages.MANZU_SHOUMAI_WEI_BOBAO_MSG_FORMAT, new Object[] { ColorUtil.getForceMsg(1, LocalMessages.T_FORCE_WEI) });
                        counterManzuCityId2 = 250;
                        boBaoMsg2 = MessageFormatter.format(LocalMessages.MANZU_SHOUMAI_WEI_BOBAO_MSG_FORMAT, new Object[] { ColorUtil.getForceMsg(2, LocalMessages.T_FORCE_SHU) });
                        break;
                    }
                    default: {
                        return;
                    }
                }
                final ManZuShouMaiDetail manZuShouMaiDetail1 = this.dataGetter.getCityService().getManZuShouMaiDetail(forceInfo, counterManzuCityId1);
                final int leftCount1 = manZuShouMaiDetail1.leftCount;
                final int qinMinDu1 = manZuShouMaiDetail1.qinMiDu;
                final int shouMaiSum1 = manZuShouMaiDetail1.shouMaiSum;
                final WorldPaidB worldPaidB1 = this.dataGetter.getWorldPaidBCache().getWorldPaidBByCountryLvAndQmd(qinMinDu1);
                if (shouMaiSum1 < worldPaidB1.getCm() && leftCount1 == WorldCityCommon.MANZU_SHOUMAI_COUNT_PER_DAY) {
                    this.dataGetter.getChatService().sendBigNotice("COUNTRY", new PlayerDto(0, forceInfo.getForceId()), boBaoMsg1, null);
                }
                final ManZuShouMaiDetail manZuShouMaiDetail2 = this.dataGetter.getCityService().getManZuShouMaiDetail(forceInfo, counterManzuCityId2);
                final int leftCount2 = manZuShouMaiDetail2.leftCount;
                final int qinMinDu2 = manZuShouMaiDetail2.qinMiDu;
                final int shouMaiSum2 = manZuShouMaiDetail2.shouMaiSum;
                final WorldPaidB worldPaidB2 = this.dataGetter.getWorldPaidBCache().getWorldPaidBByCountryLvAndQmd(qinMinDu2);
                if (shouMaiSum2 < worldPaidB2.getCm() && leftCount2 == WorldCityCommon.MANZU_SHOUMAI_COUNT_PER_DAY) {
                    this.dataGetter.getChatService().sendBigNotice("COUNTRY", new PlayerDto(0, forceInfo.getForceId()), boBaoMsg2, null);
                }
                TimerBattleService.timerLog.info(LogUtil.formatThreadLog("TimerBattleService", "worldManZuExpeditionBobaoEach30Minutes", 2, System.currentTimeMillis() - start, "param:"));
            }
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("worldManZuExpeditionBobaoEach30Minutes catch Exception.", e);
        }
    }
    
    @Override
    public void addCityEventEachMinute() {
        try {
            final long start = System.currentTimeMillis();
            TimerBattleService.timerLog.info(LogUtil.formatThreadLog("TimerBattleService", "addCityEventEachMinute", 0, 0L, ""));
            CityEventManager.getInstance().addCityEventEachMinute();
            this.deleteInvadeBarbarain();
            this.deleteNationTaskDefenceEA();
            TimerBattleService.timerLog.info(LogUtil.formatThreadLog("TimerBattleService", "addCityEventEachMinute", 2, System.currentTimeMillis() - start, ""));
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("addCityEventEachMinute catch Exception.", e);
        }
    }
    
    private void deleteNationTaskDefenceEA() {
        try {
            if (this.dataGetter.getRankService().hasNationTasks(1) > 0) {
                return;
            }
            final List<Integer> lastRoundAttDefTaskCityIdList = this.dataGetter.getRankService().getLastAttDefChooseCities();
            if (lastRoundAttDefTaskCityIdList == null || lastRoundAttDefTaskCityIdList.size() == 0) {
                return;
            }
            for (final Integer cityId : lastRoundAttDefTaskCityIdList) {
                final List<NationTaskExpeditionArmy> defArmyList = this.dataGetter.getNationTaskExpeditionArmyDao().getNationTaskDefenceEAsByLocationId(cityId);
                if (defArmyList.size() <= 10) {
                    continue;
                }
                final Battle battle = NewBattleManager.getInstance().getBattleByDefId(3, cityId);
                if (battle == null) {
                    final int vId = defArmyList.get(0).getVId();
                    final int done = this.dataGetter.getNationTaskExpeditionArmyDao().deleteById(vId);
                    if (done == 1) {
                        continue;
                    }
                    ErrorSceneLog.getInstance().appendErrorMsg("NationTaskExpeditionArmyDao.deleteById failed").append("cityId", cityId).append("vId", vId).appendClassName("TimerBattleService").appendMethodName("deleteNationTaskDefenceEA").flush();
                }
                else {
                    if (battle.getDefCamp().size() <= 10) {
                        continue;
                    }
                    synchronized (battle.getBattleId()) {
                        CampArmy removeCa = null;
                        for (final CampArmy temp : battle.getDefCamp()) {
                            if (!temp.isOnQueues() && temp.getNationTaskEAType() == 2) {
                                removeCa = temp;
                                break;
                            }
                        }
                        if (removeCa != null) {
                            final int vId2 = removeCa.getPgmVId();
                            final int done2 = this.dataGetter.getNationTaskExpeditionArmyDao().deleteById(vId2);
                            if (done2 == 1) {
                                battle.getDefCamp().remove(removeCa);
                            }
                            else {
                                ErrorSceneLog.getInstance().appendErrorMsg("NationTaskExpeditionArmyDao.deleteById failed").appendBattleId(battle.getBattleId()).append("vId", vId2).append("ca.playerName", removeCa.getPlayerName()).append("ca.playerId", removeCa.getPlayerId()).append("ca.GeneralName", removeCa.getGeneralName()).append("ca.GeneralId", removeCa.getGeneralId()).appendClassName("TimerBattleService").appendMethodName("deleteNationTaskDefenceEA").flush();
                            }
                        }
                        // monitorexit(battle.getBattleId())
                    }
                }
            }
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("deleteNationTaskDefenceEA catch Exception.", e);
        }
    }
    
    private void deleteInvadeBarbarain() {
        try {
            if (this.dataGetter.getRankService().hasNationTasks(1) == 3 || this.dataGetter.getRankService().hasNationTasks(1) == 7) {
                return;
            }
            final Set<Integer> cityIdSet = new HashSet<Integer>();
            cityIdSet.addAll(this.dataGetter.getKtMzSCache().getTouFangCitySet());
            cityIdSet.addAll(this.dataGetter.getKtSdmzSCache().getTouFangCitySet());
            for (final Integer cityId : cityIdSet) {
                final List<BarbarainPhantom> list = this.dataGetter.getBarbarainPhantomDao().getBarPhantomByLocationId(cityId);
                if (list.size() <= 10) {
                    continue;
                }
                final Battle battle = NewBattleManager.getInstance().getBattleByDefId(3, cityId);
                final City city = this.dataGetter.getCityDao().read(cityId);
                if (battle == null) {
                    if (city.getForceId() != 101 && city.getForceId() != 102 && city.getForceId() != 103) {
                        continue;
                    }
                    this.deleteInvadeBarbarainFree(city, list);
                }
                else if (battle.getDefBaseInfo().getForceId() == 101 || battle.getDefBaseInfo().getForceId() == 102 || battle.getDefBaseInfo().getForceId() == 103) {
                    this.deleteInvadeBarbarainBattleDef(battle, list);
                }
                else {
                    this.deleteInvadeBarbarainBattleAttTry(battle, list);
                }
            }
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("deleteInvadeBarbarain catch Exception.", e);
        }
    }
    
    private void deleteInvadeBarbarainBattleAttTry(final Battle battle, final List<BarbarainPhantom> list) {
        try {
            int barbarainCount = 0;
            CampArmy ca = null;
            for (final CampArmy temp : battle.getAttCamp()) {
                if ((temp.getForceId() == 101 || temp.getForceId() == 102 || temp.getForceId() == 103) && temp.isBarPhantom() && ++barbarainCount > 10) {
                    if (temp.isOnQueues()) {
                        continue;
                    }
                    ca = temp;
                    break;
                }
            }
            if (ca == null) {
                return;
            }
            synchronized (battle.getBattleId()) {
                final int vId = ca.getPgmVId();
                final int done = this.dataGetter.getBarbarainPhantomDao().deleteById(vId);
                if (done == 1) {
                    battle.getAttCamp().remove(ca);
                }
                else {
                    ErrorSceneLog.getInstance().appendErrorMsg("BarbarainPhantomDao.deleteById failed").appendBattleId(battle.getBattleId()).append("vId", vId).append("ca.playerName", ca.getPlayerName()).append("ca.playerId", ca.getPlayerId()).append("ca.GeneralName", ca.getGeneralName()).append("ca.GeneralId", ca.getGeneralId()).appendClassName("TimerBattleService").appendMethodName("deleteInvadeBarbarainFree").flush();
                }
            }
            // monitorexit(battle.getBattleId())
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("deleteInvadeBarbarainBattleAttTry catch Exception.", e);
        }
    }
    
    private void deleteInvadeBarbarainBattleDef(final Battle battle, final List<BarbarainPhantom> list) {
        try {
            if (battle.getDefCamp().size() <= 10) {
                return;
            }
            synchronized (battle.getBattleId()) {
                int index = 0;
                CampArmy ca = null;
                for (int i = battle.getDefCamp().size() - 1; i >= 0; --i) {
                    final CampArmy temp = battle.getDefCamp().get(i);
                    if (!temp.isOnQueues() && temp.isBarPhantom()) {
                        ca = temp;
                        index = i;
                        break;
                    }
                }
                if (ca == null) {
                    // monitorexit(battle.getBattleId())
                    return;
                }
                final int vId = ca.getPgmVId();
                final int done = this.dataGetter.getBarbarainPhantomDao().deleteById(vId);
                if (done == 1) {
                    battle.getDefCamp().remove(index);
                }
                else {
                    ErrorSceneLog.getInstance().appendErrorMsg("BarbarainPhantomDao.deleteById failed").appendBattleId(battle.getBattleId()).append("vId", vId).append("ca.playerName", ca.getPlayerName()).append("ca.playerId", ca.getPlayerId()).append("ca.GeneralName", ca.getGeneralName()).append("ca.GeneralId", ca.getGeneralId()).appendClassName("TimerBattleService").appendMethodName("deleteInvadeBarbarainFree").flush();
                }
            }
            // monitorexit(battle.getBattleId())
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("deleteInvadeBarbarainBattleDef catch Exception.", e);
        }
    }
    
    private void deleteInvadeBarbarainFree(final City city, final List<BarbarainPhantom> list) {
        try {
            final int vId = list.get(0).getVId();
            final int done = this.dataGetter.getBarbarainPhantomDao().deleteById(vId);
            if (done != 1) {
                ErrorSceneLog.getInstance().appendErrorMsg("BarbarainPhantomDao.deleteById failed").append("cityId", city.getId()).append("vId", vId).appendClassName("TimerBattleService").appendMethodName("deleteInvadeBarbarainFree").flush();
            }
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("deleteInvadeBarbarainFree catch Exception.", e);
        }
    }
    
    @Override
    public boolean addNationTaskExpeditionArmy(final int targrtCityId) {
        final long start = System.currentTimeMillis();
        try {
            TimerBattleService.timerLog.info(LogUtil.formatThreadLog("TimerBattleService", "addNationTaskExpeditionArmy", 0, 0L, "targrtCityId:" + targrtCityId));
            final City targrtCity = this.dataGetter.getCityDao().read(targrtCityId);
            if (targrtCity == null) {
                ErrorSceneLog.getInstance().appendErrorMsg("targrtCity is null").append("targrtCityId", targrtCityId).appendMethodName("addNationTaskExpeditionArmy").appendClassName("TimerBattleService").flush();
                return false;
            }
            final EfLv eflv = (EfLv)this.dataGetter.getEfLvCache().get((Object)WorldCityCommon.MAX_COUNTRY_LV);
            if (eflv == null) {
                ErrorSceneLog.getInstance().appendErrorMsg("eflv is null").append("WorldCityCommon.MAX_COUNTRY_LV", WorldCityCommon.MAX_COUNTRY_LV).appendMethodName("addNationTaskExpeditionArmy").appendClassName("TimerBattleService").flush();
                return false;
            }
            final NationTaskConquerEAScheduler nationTaskConquerEAScheduler = new NationTaskConquerEAScheduler();
            nationTaskConquerEAScheduler.startTime = System.currentTimeMillis();
            nationTaskConquerEAScheduler.eflv = eflv;
            nationTaskConquerEAScheduler.bitSet.clear();
            this.conquerEASchedulerMap.put(targrtCityId, nationTaskConquerEAScheduler);
            final String battleId = CityService.cityBatIdSet.get(targrtCityId);
            if (battleId == null) {
                final ErrorSceneLog errorSceneLog = ErrorSceneLog.getInstance();
                errorSceneLog.error("addNationTaskExpeditionArmy battleId is null...targrtCityId:" + targrtCityId);
            }
            else {
                synchronized (battleId) {
                    final Battle battle = NewBattleManager.getInstance().getBattleByDefId(3, targrtCityId);
                    if (battle != null) {
                        this.addNationTaskExpeditionArmyForDefenceCountryBattle(battle);
                    }
                    else {
                        this.addNationTaskExpeditionArmyForDefenceCountryFree(targrtCity);
                    }
                }
            }
            TimerBattleService.timerLog.info(LogUtil.formatThreadLog("TimerBattleService", "addNationTaskExpeditionArmy", 2, System.currentTimeMillis() - start, "targrtCityId:" + targrtCityId));
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("addNationTaskExpeditionArmy catch exception", e);
        }
        return true;
    }
    
    @Override
    public void recoverNationTaskExpeditionArmy() {
        final long start = System.currentTimeMillis();
        try {
            TimerBattleService.timerLog.info(LogUtil.formatThreadLog("TimerBattleService", "recoverNationTaskExpeditionArmy", 0, 0L, "param:"));
            final List<Tuple<Integer, Long>> nationTaskList = this.dataGetter.getRankService().getAttDefNationTaskInfos();
            if (nationTaskList == null || nationTaskList.size() == 0) {
                TimerBattleService.timerLog.info("recoverNationTaskExpeditionArmy ignore.");
                return;
            }
            final EfLv eflv = (EfLv)this.dataGetter.getEfLvCache().get((Object)WorldCityCommon.MAX_COUNTRY_LV);
            for (final Tuple<Integer, Long> tuple : nationTaskList) {
                final Integer cityId = tuple.left;
                final Long startTime = tuple.right;
                if (startTime <= 0L) {
                    ErrorSceneLog.getInstance().appendErrorMsg("startTime <= 0").append("cityId", cityId).append("startTime", startTime).appendClassName("TimerBattleService").appendMethodName("recoverNationTaskExpeditionArmy").flush();
                }
                else if (startTime > System.currentTimeMillis()) {
                    ErrorSceneLog.getInstance().appendErrorMsg("startTime > now").append("cityId", cityId).append("startTime", startTime).append("now", System.currentTimeMillis()).appendClassName("TimerBattleService").appendMethodName("recoverNationTaskExpeditionArmy").flush();
                }
                else {
                    final NationTaskConquerEAScheduler nationTaskConquerEAScheduler = new NationTaskConquerEAScheduler();
                    nationTaskConquerEAScheduler.startTime = startTime;
                    nationTaskConquerEAScheduler.eflv = eflv;
                    nationTaskConquerEAScheduler.bitSet.clear();
                    this.conquerEASchedulerMap.put(cityId, nationTaskConquerEAScheduler);
                }
            }
            TimerBattleService.timerLog.info(LogUtil.formatThreadLog("TimerBattleService", "recoverNationTaskExpeditionArmy", 2, System.currentTimeMillis() - start, "param:"));
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("recoverNationTaskExpeditionArmy catch exception", e);
        }
    }
    
    private void addNationTaskExpeditionArmyForConquerCountry(final int attForceId, final int targetCityId) {
        try {
            final int capitalId = WorldCityCommon.nationMainCityIdMap.get(attForceId);
            final List<Integer> cityIdlist = this.dataGetter.getCityDataCache().getMinPath(capitalId, targetCityId);
            if (cityIdlist == null) {
                ErrorSceneLog.getInstance().appendErrorMsg("cityIdlist is null").append("capitalId", capitalId).append("targetCityId", targetCityId).appendClassName("TimerBattleService").appendMethodName("addNationTaskExpeditionArmyForConquerCountry").flush();
                return;
            }
            if (cityIdlist.size() == 0) {
                ErrorSceneLog.getInstance().appendErrorMsg("cityIdlist is empty").append("capitalId", capitalId).append("targetCityId", targetCityId).append("cityIdlist", cityIdlist.toString()).appendClassName("TimerBattleService").appendMethodName("addNationTaskExpeditionArmyForConquerCountry").flush();
                return;
            }
            final StringBuilder sb = new StringBuilder();
            for (final Integer i : cityIdlist) {
                sb.append(i).append(";");
            }
            sb.replace(sb.length() - 1, sb.length(), "");
            final String moveLine = sb.toString();
            final List<NationTaskExpeditionArmy> batchList = new LinkedList<NationTaskExpeditionArmy>();
            final Date date = new Date();
            final int state = 0;
            final EfLv efLv = this.conquerEASchedulerMap.get(targetCityId).eflv;
            final int efLvId = efLv.getEfLv();
            Integer[] armyIds = null;
            switch (attForceId) {
                case 1: {
                    armyIds = efLv.getWeiAttArmyIds();
                    break;
                }
                case 2: {
                    armyIds = efLv.getShuAttArmyIds();
                    break;
                }
                case 3: {
                    armyIds = efLv.getWuAttArmyIds();
                    break;
                }
            }
            final int armyId = armyIds[WebUtil.nextInt(armyIds.length)];
            final Army army = (Army)this.dataGetter.getArmyCache().get((Object)armyId);
            for (int attNum = this.dataGetter.getKtCoNpcCache().getKtCoNpcOfNow().getAttNum(), j = 0; j < attNum; ++j) {
                final NationTaskExpeditionArmy activityNpc = new NationTaskExpeditionArmy();
                activityNpc.setVId(this.NationTaskExpeditionArmyVId.incrementAndGet());
                activityNpc.setNpcType(1);
                activityNpc.setState(state);
                activityNpc.setCreateTime(date);
                activityNpc.setLocationId(capitalId);
                activityNpc.setForceId(attForceId);
                activityNpc.setTableId(efLvId);
                activityNpc.setArmyId(armyId);
                activityNpc.setHp(army.getArmyHp());
                activityNpc.setTacticval(1);
                activityNpc.setMoveLine(moveLine);
                batchList.add(activityNpc);
            }
            this.dataGetter.getNationTaskExpeditionArmyDao().batchCreate(batchList);
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("addNationTaskExpeditionArmyForConquerCountry catch exception. attForceId:" + attForceId + " targetCityId:" + targetCityId, e);
        }
    }
    
    private void addNationTaskExpeditionArmyForDefenceCountryBattle(final Battle battle) {
        try {
            final List<NationTaskExpeditionArmy> batchList = new LinkedList<NationTaskExpeditionArmy>();
            final Date date = new Date();
            final int state = 3;
            final int targetCityId = battle.getDefBaseInfo().getId();
            final int forceIdId = battle.getDefBaseInfo().getForceId();
            final EfLv efLv = this.conquerEASchedulerMap.get(targetCityId).eflv;
            final int efLvId = efLv.getEfLv();
            Integer[] armyIds = null;
            switch (forceIdId) {
                case 1: {
                    armyIds = efLv.getWeiDefArmyIds();
                    break;
                }
                case 2: {
                    armyIds = efLv.getShuDefArmyIds();
                    break;
                }
                case 3: {
                    armyIds = efLv.getWuDefArmyIds();
                    break;
                }
            }
            final Builder builder = BuilderFactory.getInstance().getBuilder(3);
            final int battleSide = 0;
            for (int defNpcNum = this.dataGetter.getKtCoNpcCache().getKtCoNpcOfNow().getDefNum(), i = 0; i < defNpcNum; ++i) {
                final NationTaskExpeditionArmy activityNpc = new NationTaskExpeditionArmy();
                activityNpc.setVId(this.NationTaskExpeditionArmyVId.incrementAndGet());
                activityNpc.setNpcType(1);
                activityNpc.setState(state);
                activityNpc.setCreateTime(date);
                activityNpc.setLocationId(targetCityId);
                activityNpc.setForceId(forceIdId);
                activityNpc.setTableId(efLvId);
                final int armyId = armyIds[WebUtil.nextInt(armyIds.length)];
                final Army army = (Army)this.dataGetter.getArmyCache().get((Object)armyId);
                activityNpc.setArmyId(armyId);
                activityNpc.setHp(army.getArmyHp());
                activityNpc.setTacticval(1);
                activityNpc.setMoveLine(null);
                final CampArmy barCa = builder.copyArmyFromNationTaskExpeditionArmy(this.dataGetter, efLv, battle, activityNpc, battleSide);
                battle.joinCampArmy(this.dataGetter, battleSide, barCa);
                batchList.add(activityNpc);
            }
            this.dataGetter.getNationTaskExpeditionArmyDao().batchCreate(batchList);
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("addNationTaskExpeditionArmyForDefenceCountryFree catch exception. battleId:" + battle.getBattleId(), e);
        }
    }
    
    private void addNationTaskExpeditionArmyForDefenceCountryFree(final City targrtCity) {
        try {
            final List<NationTaskExpeditionArmy> batchList = new LinkedList<NationTaskExpeditionArmy>();
            final Date date = new Date();
            final int state = 0;
            final int targetCityId = targrtCity.getId();
            final int forceIdId = targrtCity.getForceId();
            final EfLv efLv = this.conquerEASchedulerMap.get(targetCityId).eflv;
            final int efLvId = efLv.getEfLv();
            Integer[] armyIds = null;
            switch (forceIdId) {
                case 1: {
                    armyIds = efLv.getWeiDefArmyIds();
                    break;
                }
                case 2: {
                    armyIds = efLv.getShuDefArmyIds();
                    break;
                }
                case 3: {
                    armyIds = efLv.getWuDefArmyIds();
                    break;
                }
            }
            for (int defNpcNum = this.dataGetter.getKtCoNpcCache().getKtCoNpcOfNow().getDefNum(), i = 0; i < defNpcNum; ++i) {
                final NationTaskExpeditionArmy activityNpc = new NationTaskExpeditionArmy();
                activityNpc.setVId(this.NationTaskExpeditionArmyVId.incrementAndGet());
                activityNpc.setNpcType(1);
                activityNpc.setState(state);
                activityNpc.setCreateTime(date);
                activityNpc.setLocationId(targetCityId);
                activityNpc.setForceId(forceIdId);
                activityNpc.setTableId(efLvId);
                final int armyId = armyIds[WebUtil.nextInt(armyIds.length)];
                final Army army = (Army)this.dataGetter.getArmyCache().get((Object)armyId);
                activityNpc.setArmyId(armyId);
                activityNpc.setHp(army.getArmyHp());
                activityNpc.setTacticval(1);
                activityNpc.setMoveLine(null);
                batchList.add(activityNpc);
            }
            this.dataGetter.getNationTaskExpeditionArmyDao().batchCreate(batchList);
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("addNationTaskExpeditionArmyForDefenceCountryFree catch exception. targrtCity:" + targrtCity.getId(), e);
        }
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final BattleCheckThread thread = new BattleCheckThread();
        thread.start();
    }
    
    @Override
    public void addBarbarainFoodArmyAfterBesiegedTry(final int cityId) {
        final long start = System.currentTimeMillis();
        try {
            TimerBattleService.timerLog.info(LogUtil.formatThreadLog("TimerBattleService", "addBarbarainFoodArmyAfterBesiegedTry", 0, 0L, "cityId:" + cityId));
            if (this.dataGetter.getRankService().hasNationTasks(1) != 3) {
                return;
            }
            final City city = this.dataGetter.getCityDao().read(cityId);
            if (city.getTitle() != 1) {
                return;
            }
            final int foodArmyForceId = city.getForceId();
            int stepFactor = 0;
            switch (foodArmyForceId) {
                case 101: {
                    stepFactor = 1;
                    break;
                }
                case 102: {
                    stepFactor = 2;
                    break;
                }
                case 103: {
                    stepFactor = 3;
                    break;
                }
                default: {
                    return;
                }
            }
            final long endTime = this.dataGetter.getRankService().getBarbarianNationTaskEnd();
            if (endTime <= 0L || endTime < System.currentTimeMillis()) {
                return;
            }
            final long startTime = endTime - 7200000L;
            final long timeDifference = System.currentTimeMillis() - startTime;
            int step = this.dataGetter.getKtMzSCache().getKtMzSByTime(timeDifference).getId();
            step *= stepFactor;
            if (!this.barbarainFoodArmyBitSet.get(step)) {
                final List<Integer> BFS3List = this.dataGetter.getCityDataCache().getBFSCityOrderListByBreadth(cityId, 3);
                if (BFS3List.size() == 0) {
                    TimerBattleService.timerLog.info("TimerBattleService.addBarbarainFoodArmyAfterBesiegedTry: BFS3Set is empty, ignore this besiege.");
                    return;
                }
                boolean findFreeCity = false;
                City targetCity = null;
                for (final Integer targetCityId : BFS3List) {
                    final Battle battle = NewBattleManager.getInstance().getBattleByDefId(3, targetCityId);
                    if (battle != null) {
                        continue;
                    }
                    targetCity = this.dataGetter.getCityDao().read(targetCityId);
                    if (targetCity.getForceId() == 0 || targetCity.getForceId() == 101 || targetCity.getForceId() == 102) {
                        continue;
                    }
                    if (targetCity.getForceId() == 103) {
                        continue;
                    }
                    final CityAttribute cityAttribute = CityEventManager.getInstance().cityAttributeMap.get(targetCityId);
                    if (cityAttribute != null) {
                        continue;
                    }
                    this.createBattleOfRewardArmy(targetCityId, foodArmyForceId);
                    findFreeCity = true;
                    this.barbarainFoodArmyBitSet.set(step);
                    break;
                }
                if (!findFreeCity) {
                    TimerBattleService.timerLog.info("TimerBattleService.addBarbarainFoodArmyAfterBesiegedTry: cannot find Free City, ignore this besiege.cityId:" + cityId + "BFS3Set" + BFS3List.toString());
                }
            }
            TimerBattleService.timerLog.info(LogUtil.formatThreadLog("TimerBattleService", "addBarbarainFoodArmyAfterBesiegedTry", 2, System.currentTimeMillis() - start, "cityId:" + cityId));
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("addBarbarainFoodArmyAfterBesiegedTry catch Exception:", e);
        }
    }
    
    private void createBattleOfRewardArmy(final int targetCityId, final int fooArmyForceId) {
        final List<ForceInfo> forceInfoList = this.dataGetter.getForceInfoDao().getModels();
        int minCountryLv = Integer.MAX_VALUE;
        for (final ForceInfo temp : forceInfoList) {
            if (temp.getForceLv() < minCountryLv) {
                minCountryLv = temp.getForceLv();
            }
        }
        final Barbarain barbarain = (Barbarain)this.dataGetter.getBarbarainCache().get((Object)minCountryLv);
        final Integer[] rewardArmyIds = barbarain.getRewardArmyIds();
        final int armyId = rewardArmyIds[WebUtil.nextInt(rewardArmyIds.length)];
        final Army army = (Army)this.dataGetter.getArmyCache().get((Object)armyId);
        List<BarbarainPhantom> list = new ArrayList<BarbarainPhantom>();
        for (int i = 0; i < 10; ++i) {
            final BarbarainPhantom barPhantom = new BarbarainPhantom();
            barPhantom.setNpcType(1);
            barPhantom.setCreateTime(new Date());
            barPhantom.setLocationId(targetCityId);
            barPhantom.setForceId(fooArmyForceId);
            barPhantom.setBarbarainId(barbarain.getId());
            barPhantom.setTacticval(1);
            barPhantom.setState(0);
            barPhantom.setArmyId(armyId);
            barPhantom.setHp(army.getArmyHp());
            barPhantom.setVId(BattleService.barVid.incrementAndGet());
            barPhantom.setVId(BattleService.barVid.incrementAndGet());
            list.add(barPhantom);
        }
        this.dataGetter.getBarbarainPhantomDao().batchCreate(list);
        final WorldCity wc = (WorldCity)this.dataGetter.getWorldCityCache().get((Object)targetCityId);
        final String battleId = CityBuilder.getBattleId(this.dataGetter, fooArmyForceId, wc.getId());
        final Builder builder = BuilderFactory.getInstance().getBuilder(3);
        final Terrain terrain = builder.getTerrain(-1, wc.getId(), this.dataGetter);
        final Battle targetBattle = NewBattleManager.getInstance().createBattle(battleId);
        if (targetBattle == null) {
            ErrorSceneLog.getInstance().appendErrorMsg("create battle fail").appendBattleId(battleId).append("fooArmyForceId", fooArmyForceId).append("cityId", wc.getId()).append("cityName", wc.getName()).appendClassName("TimerBattleService").appendMethodName("createBattleOfRewardArmy").flush();
            return;
        }
        list = this.dataGetter.getBarbarainPhantomDao().getBarPhantomByLocationId(targetCityId);
        final BattleAttacker battleAttacker = new BattleAttacker();
        battleAttacker.attType = 4;
        battleAttacker.attForceId = fooArmyForceId;
        battleAttacker.attPlayerId = -5;
        battleAttacker.attBarbarain = barbarain;
        battleAttacker.barPhantom = ((list.size() > 0) ? list.get(0) : null);
        targetBattle.init(battleAttacker, 3, targetCityId, this.dataGetter, false, terrain.getValue());
        builder.dealUniqueStaff(this.dataGetter, targetBattle, -1, targetCityId);
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("cityId", targetCityId);
        doc.createElement("disPlay", 1);
        doc.endObject();
        final String groupId = ChatType.WORLD.toString();
        final Group worldG = GroupManager.getInstance().getGroup(groupId);
        if (worldG != null) {
            final byte[] bytes = JsonBuilder.getJson(JsonBuilder.getJson(State.PUSH, PushCommand.PUSH_BARBARAIN_INVADE_FOOD_ARMY.getModule(), doc.toByte()));
            worldG.notify((String)null, WrapperUtil.wrapper(PushCommand.PUSH_BARBARAIN_INVADE_FOOD_ARMY.getCommand(), 0, bytes));
        }
        final String boBaoMsg = MessageFormatter.format(LocalMessages.MANZU_INVADE_FOOD_ARMY_BOBAO_MSG_FORMAT, new Object[] { ColorUtil.getSpecialColorMsg(((WorldCity)this.dataGetter.getWorldCityCache().get((Object)targetCityId)).getName()) });
        this.dataGetter.getChatService().sendSystemChat("GLOBAL", 0, 0, boBaoMsg, null);
    }
    
    @Override
    public void triggerAddTicketArmyTask() {
        try {
            final long now = System.currentTimeMillis();
            for (int i = 0; i < 4; ++i) {
                final long exeTime = now + i * 30 * 60000L;
                this.dataGetter.getJobService().addJob("timerBattleService", "addTicketArmyDuringNationTask", new StringBuilder().append(i).toString(), exeTime, true);
            }
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("TimerBattleService.triggerAddTicketArmyTask catch Exception:", e);
        }
    }
    
    @Override
    public void deleteAllTicketArmyAfterNationTaskEnded() {
        try {
            this.dataGetter.getNationTaskExpeditionArmyDao().deleteAllFreeTicketArmy();
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("TimerBattleService.deleteAllTicketArmyAfterNationTaskEnded catch Exception:", e);
        }
    }
    
    @Override
    public void addTicketArmyDuringNationTask(final String param) {
        final long start = System.currentTimeMillis();
        try {
            TimerBattleService.timerLog.info(LogUtil.formatThreadLog("TimerBattleService", "addTicketArmyDuringNationTask", 0, 0L, "param:" + param));
            this.resetComposition2of3Map();
            final List<BattleInfo> worldBattleInfoList = this.dataGetter.getBattleInfoDao().getModels();
            if (worldBattleInfoList.size() == 0) {
                ErrorSceneLog.getInstance().appendErrorMsg("\u6ca1\u6709\u6218\u6597\u4e2d\u7684\u57ce\u5e02,\u7565\u8fc7\u8f6e\u7ffb\u500d\u5238\u90e8\u961f").flush();
                return;
            }
            Collections.shuffle(worldBattleInfoList);
            final StringBuilder cityNameList = new StringBuilder();
            for (final BattleInfo battleInfo : worldBattleInfoList) {
                final Battle battle = NewBattleManager.getInstance().getBattleByBatId(battleInfo.getBattleId());
                if (battle == null) {
                    continue;
                }
                final int defForceId = battle.getDefBaseInfo().getForceId();
                int attForceId = 0;
                synchronized (battle.getBattleId()) {
                    final int attMaxNum = 0;
                    for (final Map.Entry<Integer, Integer> entry : battle.attSideDetail.entrySet()) {
                        if (entry.getValue() > attMaxNum) {
                            attForceId = entry.getKey();
                        }
                    }
                    final Integer flag = this.get2of3CompositionFlag(attForceId, defForceId);
                    if (flag != null && flag != 1) {
                        this.addTicketArmyInCityForOneSide(battle, attForceId, 1);
                        this.addTicketArmyInCityForOneSide(battle, defForceId, 0);
                        this.set2of3CompositionFlag(attForceId, defForceId);
                        final String cityName = ((WorldCity)this.dataGetter.getWorldCityCache().get((Object)battle.getDefBaseInfo().getId())).getName();
                        cityNameList.append(ColorUtil.getSpecialColorMsg(cityName)).append(" ");
                    }
                    // monitorexit(battle.getBattleId())
                }
            }
            final String msg = MessageFormatter.format(LocalMessages.TOUZIRENWU_FANBEIQUAN_ARMY_ADD_BOBAO_FORMAT, new Object[] { cityNameList.toString() });
            this.dataGetter.getChatService().sendSystemChat("GLOBAL", 0, 0, msg, null);
            TimerBattleService.timerLog.info(LogUtil.formatThreadLog("TimerBattleService", "addTicketArmyDuringNationTask", 2, System.currentTimeMillis() - start, "param:" + param));
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("TimerBattleService.addTicketArmyDuringNationTask catch Exception:", e);
        }
    }
    
    private void addTicketArmyInCityForOneSide(final Battle battle, final int forceId, final int battleSide) {
        try {
            final Builder builder = BuilderFactory.getInstance().getBuilder(3);
            final EfLv efLv = (EfLv)this.dataGetter.getEfLvCache().get((Object)WorldCityCommon.MAX_COUNTRY_LV);
            final Integer[] armyIds = efLv.getTicketArmyIds();
            final int state = 3;
            final Date date = new Date();
            final int locationId = battle.getDefBaseInfo().getId();
            final int efLvId = efLv.getEfLv();
            final List<NationTaskExpeditionArmy> batchList = new LinkedList<NationTaskExpeditionArmy>();
            for (int i = 0; i < 50; ++i) {
                final NationTaskExpeditionArmy activityNpc = new NationTaskExpeditionArmy();
                activityNpc.setVId(this.NationTaskExpeditionArmyVId.incrementAndGet());
                activityNpc.setNpcType(2);
                activityNpc.setState(state);
                activityNpc.setCreateTime(date);
                activityNpc.setLocationId(locationId);
                activityNpc.setForceId(forceId);
                activityNpc.setTableId(efLvId);
                final Integer armyId = armyIds[WebUtil.nextInt(armyIds.length)];
                final Army army = (Army)this.dataGetter.getArmyCache().get((Object)armyId);
                activityNpc.setArmyId(armyId);
                activityNpc.setHp(army.getArmyHp());
                activityNpc.setTacticval(1);
                activityNpc.setMoveLine(null);
                final CampArmy ticketCA = builder.copyArmyFromNationTaskExpeditionArmy(this.dataGetter, efLv, battle, activityNpc, battleSide);
                battle.joinCampArmy(this.dataGetter, battleSide, ticketCA);
                batchList.add(activityNpc);
            }
            this.dataGetter.getNationTaskExpeditionArmyDao().batchCreate(batchList);
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("TimerBattleService.addTicketArmyInCityForOneSide catch Exception:", e);
        }
    }
    
    @Override
    public Integer getLastManZuLeftPercent(final int forceId, final int round) {
        final NationTask nationTask = this.dataGetter.getNationTaskDao().getByForce(forceId);
        if (nationTask == null) {
            ErrorSceneLog.getInstance().appendErrorMsg("nationTask is null").append("forceId", forceId).appendClassName("TimerBattleService").appendMethodName("getLastManZuLeftPercent").flush();
            return null;
        }
        final String info = nationTask.getTaskRelateInfo();
        if (info == null) {
            ErrorSceneLog.getInstance().appendErrorMsg("info is null").append("forceId", forceId).append("nationTask", nationTask.getNationTaskId()).appendClassName("TimerBattleService").appendMethodName("getLastManZuLeftPercent").flush();
            return null;
        }
        final String[] sArray = info.split("_");
        if (sArray.length != 2) {
            ErrorSceneLog.getInstance().appendErrorMsg("info is invalid").append("info", info).append("forceId", forceId).append("nationTask", nationTask.getNationTaskId()).appendClassName("TimerBattleService").appendMethodName("getLastManZuLeftPercent").flush();
            return null;
        }
        long doneNum = 0L;
        long allNum = 0L;
        try {
            doneNum = Long.parseLong(sArray[0]);
            allNum = Long.parseLong(sArray[1]);
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().appendErrorMsg("parseLong catch exception").append("info", info).append("forceId", forceId).append("nationTask", nationTask.getNationTaskId()).appendClassName("TimerBattleService").appendMethodName("getLastManZuLeftPercent").flush();
            return null;
        }
        int percent = (int)(doneNum * 100L / allNum);
        if (percent != 100) {
            final int manZuForceId = WorldCityCommon.playerManZuForceMap.get(forceId);
            final List<BarbarainPhantom> barPhantomList = this.dataGetter.getBarbarainPhantomDao().getBarPhantomByForceId(manZuForceId);
            if (barPhantomList == null || barPhantomList.size() == 0) {
                percent = 100;
                final String info2 = String.valueOf(allNum) + "_" + allNum;
                this.dataGetter.getNationTaskDao().updateManZuSaoDangTaskRelateInfo(forceId, info2);
            }
        }
        return percent;
    }
    
    @Override
    public void addRoundSaoDangManZu(final int forceId, final int round) {
        final Integer manZuForceId = WorldCityCommon.playerManZuForceMap.get(forceId);
        if (manZuForceId == null) {
            ErrorSceneLog.getInstance().appendErrorMsg("manZuForceId is null").append("forceId", forceId).appendClassName("TimerBattleService").appendMethodName("addRoundSaoDangManZu").flush();
            return;
        }
        final KtSdmzS ktSdmzS = this.dataGetter.getKtSdmzSCache().getKtSdmzSByRound(round);
        if (ktSdmzS == null) {
            ErrorSceneLog.getInstance().appendErrorMsg("ktSdmzS is null").append("round", round).appendClassName("TimerBattleService").appendMethodName("addRoundSaoDangManZu").flush();
            return;
        }
        final Integer[] armyIds = ktSdmzS.getArmyIds(forceId);
        if (armyIds == null) {
            ErrorSceneLog.getInstance().appendErrorMsg("armyIds is null").append("forceId", forceId).appendClassName("TimerBattleService").appendMethodName("addRoundSaoDangManZu").flush();
            return;
        }
        final Set<Integer> citySet = ktSdmzS.getCitySet(forceId);
        if (citySet == null) {
            ErrorSceneLog.getInstance().appendErrorMsg("citySet is null").append("forceId", forceId).appendClassName("TimerBattleService").appendMethodName("addRoundSaoDangManZu").flush();
            return;
        }
        long hpSum = 0L;
        for (final Integer cityId : citySet) {
            final Battle battle = NewBattleManager.getInstance().getBattleByDefId(3, cityId);
            if (battle != null) {
                synchronized (battle.getBattleId()) {
                    final Long add = this.addSaoDangManZuBattle(battle, manZuForceId, ktSdmzS, armyIds);
                    if (add != null && add > 0L) {
                        hpSum += add;
                    }
                    // monitorexit(battle.getBattleId())
                    continue;
                }
            }
            final City city = this.dataGetter.getCityDao().read(cityId);
            if (city.getForceId() == manZuForceId) {
                final Long add = this.saveSaoDangManZuFree(cityId, manZuForceId, ktSdmzS, armyIds);
                if (add == null || add <= 0L) {
                    continue;
                }
                hpSum += add;
            }
            else {
                final Long add = this.fireSaoDangManZuBattle(cityId, manZuForceId, ktSdmzS, armyIds);
                if (add == null || add <= 0L) {
                    continue;
                }
                hpSum += add;
            }
        }
        if (hpSum != 0L) {
            final String info = "0_" + hpSum;
            this.dataGetter.getNationTaskDao().updateManZuSaoDangTaskRelateInfo(forceId, info);
        }
    }
    
    private Long fireSaoDangManZuBattle(final int cityId, final int manZuForceId, final KtSdmzS ktSdmzS, final Integer[] armyIds) {
        try {
            long hpSum = 0L;
            final List<BarbarainPhantom> list = new ArrayList<BarbarainPhantom>();
            for (int i = 0; i < ktSdmzS.getN(); ++i) {
                final BarbarainPhantom barPhantom = new BarbarainPhantom();
                barPhantom.setNpcType(2);
                barPhantom.setCreateTime(new Date());
                barPhantom.setLocationId(cityId);
                barPhantom.setForceId(manZuForceId);
                barPhantom.setBarbarainId(ktSdmzS.getId());
                barPhantom.setTacticval(1);
                barPhantom.setState(0);
                final int armyId = armyIds[WebUtil.nextInt(armyIds.length)];
                final Army army = (Army)this.dataGetter.getArmyCache().get((Object)armyId);
                hpSum += army.getArmyHp();
                barPhantom.setArmyId(armyId);
                barPhantom.setHp(army.getArmyHp());
                barPhantom.setVId(BattleService.barVid.incrementAndGet());
                list.add(barPhantom);
            }
            this.dataGetter.getBarbarainPhantomDao().batchCreate(list);
            if (WorldCityCommon.mainCityNationIdMap.containsKey(cityId)) {
                return null;
            }
            final WorldCity wc = (WorldCity)this.dataGetter.getWorldCityCache().get((Object)cityId);
            String battleId = null;
            battleId = CityBuilder.getBattleId(this.dataGetter, manZuForceId, wc.getId());
            final Builder builder = BuilderFactory.getInstance().getBuilder(3);
            final Terrain terrain = builder.getTerrain(-1, wc.getId(), this.dataGetter);
            final Battle cityBattle = NewBattleManager.getInstance().createBattle(battleId);
            if (cityBattle == null) {
                ErrorSceneLog.getInstance().appendErrorMsg("create battle fail").appendBattleId(battleId).append("ktSdmzS", ktSdmzS.getId()).append("manZuForceId", manZuForceId).append("cityId", cityId).appendMethodName("addBarbarainNpc").flush();
                return null;
            }
            final BattleAttacker battleAttacker = new BattleAttacker();
            battleAttacker.attType = 4;
            battleAttacker.attForceId = manZuForceId;
            battleAttacker.attPlayerId = -8;
            cityBattle.init(battleAttacker, 3, cityId, this.dataGetter, false, terrain.getValue());
            builder.dealUniqueStaff(this.dataGetter, cityBattle, -1, cityId);
            return hpSum;
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("TimerBattleService.fireSaoDangManZuBattle catch exception.", e);
            return null;
        }
    }
    
    private Long saveSaoDangManZuFree(final int cityId, final int manZuForceId, final KtSdmzS ktSdmzS, final Integer[] armyIds) {
        try {
            long hpSum = 0L;
            final Date date = new Date();
            final int state = 0;
            final List<BarbarainPhantom> list = new ArrayList<BarbarainPhantom>();
            for (int i = 0; i < ktSdmzS.getN(); ++i) {
                final BarbarainPhantom barPhantom = new BarbarainPhantom();
                barPhantom.setNpcType(2);
                barPhantom.setCreateTime(date);
                barPhantom.setLocationId(cityId);
                barPhantom.setForceId(manZuForceId);
                barPhantom.setBarbarainId(ktSdmzS.getId());
                barPhantom.setTacticval(1);
                barPhantom.setState(state);
                final int armyId = armyIds[WebUtil.nextInt(armyIds.length)];
                final Army army = (Army)this.dataGetter.getArmyCache().get((Object)armyId);
                hpSum += army.getArmyHp();
                barPhantom.setArmyId(armyId);
                barPhantom.setHp(army.getArmyHp());
                barPhantom.setVId(BattleService.barVid.incrementAndGet());
                list.add(barPhantom);
            }
            this.dataGetter.getBarbarainPhantomDao().batchCreate(list);
            return hpSum;
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("TimerBattleService.saveSaoDangManZuFree catch exception.", e);
            return null;
        }
    }
    
    private Long addSaoDangManZuBattle(final Battle battle, final int manZuForceId, final KtSdmzS ktSdmzS, final Integer[] armyIds) {
        try {
            long hpSum = 0L;
            final Date date = new Date();
            final int state = 3;
            final int cityId = battle.getDefBaseInfo().getId();
            final int defForceId = battle.getDefBaseInfo().getForceId();
            int battleSide = -1;
            if (manZuForceId == defForceId) {
                battleSide = 0;
            }
            else {
                battleSide = 1;
            }
            final List<BarbarainPhantom> list = new ArrayList<BarbarainPhantom>();
            final Builder builder = BuilderFactory.getInstance().getBuilder(3);
            for (int i = 0; i < ktSdmzS.getN(); ++i) {
                final BarbarainPhantom barPhantom = new BarbarainPhantom();
                barPhantom.setNpcType(2);
                barPhantom.setCreateTime(date);
                barPhantom.setLocationId(cityId);
                barPhantom.setForceId(manZuForceId);
                barPhantom.setBarbarainId(ktSdmzS.getId());
                barPhantom.setTacticval(1);
                barPhantom.setState(state);
                final int armyId = armyIds[WebUtil.nextInt(armyIds.length)];
                final Army army = (Army)this.dataGetter.getArmyCache().get((Object)armyId);
                hpSum += army.getArmyHp();
                barPhantom.setArmyId(armyId);
                barPhantom.setHp(army.getArmyHp());
                barPhantom.setVId(BattleService.barVid.incrementAndGet());
                list.add(barPhantom);
                final CampArmy barCa = builder.copyArmyformBarPhantom2(this.dataGetter, ktSdmzS, battle, barPhantom, battleSide);
                battle.joinCampArmy(this.dataGetter, battleSide, barCa);
            }
            this.dataGetter.getBarbarainPhantomDao().batchCreate(list);
            return hpSum;
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("TimerBattleService.addSaoDangManZuBattle catch exception. battleId:" + battle.getBattleId(), e);
            return null;
        }
    }
    
    @Override
    public void removeManZuBeforeNationTask(final int playerForceId, final Set<Integer> citySet) {
        try {
            final long start = System.currentTimeMillis();
            TimerBattleService.timerLog.info(LogUtil.formatThreadLog("TimerBattleService", "removeManZuBeforeNationTask", 0, 0L, "playerForceId:" + playerForceId + ",citySet:" + citySet.toString()));
            final Integer manZuForceId = WorldCityCommon.playerManZuForceMap.get(playerForceId);
            if (manZuForceId == null) {
                ErrorSceneLog.getInstance().appendErrorMsg("playerForceId is invalid").append("playerForceId", playerForceId).appendClassName("TimerBattleService").appendMethodName("removeManZuBeforeNationTask").flush();
                return;
            }
            for (final Integer cityId : citySet) {
                if (this.dataGetter.getWorldCityCache().get((Object)cityId) == null) {
                    ErrorSceneLog.getInstance().appendErrorMsg("citySet is invalid. ignore").append("citySet", citySet.toString()).append("cityId", cityId).appendClassName("TimerBattleService").appendMethodName("removeManZuBeforeNationTask").flush();
                }
                else if (WorldCityCommon.specialNationIdMap.get(cityId) != null) {
                    ErrorSceneLog.getInstance().appendErrorMsg("cityId is capital. ignore").append("citySet", citySet.toString()).append("cityId", cityId).appendClassName("TimerBattleService").appendMethodName("removeManZuBeforeNationTask").flush();
                }
                else {
                    final City city = this.dataGetter.getCityDao().read(cityId);
                    if (city.getForceId() != manZuForceId) {
                        return;
                    }
                    final Battle battle = NewBattleManager.getInstance().getBattleByDefId(3, cityId);
                    Label_0391: {
                        if (battle != null) {
                            if (battle.getDefBaseInfo().getForceId() != manZuForceId) {
                                break Label_0391;
                            }
                            synchronized (battle.getBattleId()) {
                                this.clearManZuBattle(battle);
                                // monitorexit(battle.getBattleId())
                                break Label_0391;
                            }
                        }
                        synchronized (CityService.cityBatIdSet.get(cityId)) {
                            this.dataGetter.getCityService().changeForceIdAndState(cityId, playerForceId, 0, 0, null);
                        }
                        // monitorexit((String)CityService.cityBatIdSet.get((Object)cityId))
                    }
                    this.dataGetter.getBarbarainPhantomDao().removeAllInThisCity(cityId);
                    this.dataGetter.getBarbarainExpeditionArmyDao().removeAllInThisCity(cityId);
                }
            }
            TimerBattleService.timerLog.info(LogUtil.formatThreadLog("TimerBattleService", "removeManZuBeforeNationTask", 2, System.currentTimeMillis() - start, "playerForceId:" + playerForceId + ",citySet:" + citySet.toString()));
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("TimerBattleService.removeManZuBeforeNationTask catch Exception.", e);
        }
    }
    
    private void clearManZuBattle(final Battle battle) {
        for (final CampArmy temp : battle.getDefCamp()) {
            temp.setArmyHpLoss(temp.getArmyHp());
            temp.setArmyHp(0);
        }
        battle.getDefBaseInfo().setNum(0);
        battle.getDefList().clear();
    }
    
    @Override
    public void addRoundManZuForBianJiang(final int manZuForceId, final int eachCityNum, final Set<Integer> citySet) {
        try {
            final long start = System.currentTimeMillis();
            TimerBattleService.timerLog.info(LogUtil.formatThreadLog("TimerBattleService", "addRoundManZuForBianJiang", 0, 0L, "manZuForceId:" + manZuForceId + ",eachCityNum:" + eachCityNum + ",citySet:" + citySet.toString()));
            if (WorldCityCommon.manZuPlayerForceMap.get(manZuForceId) == null) {
                ErrorSceneLog.getInstance().appendErrorMsg("manZuForceId is invalid").append("manZuForceId", manZuForceId).appendClassName("TimerBattleService").appendMethodName("addRoundManZuForBianJiang").flush();
                return;
            }
            if (eachCityNum <= 0) {
                ErrorSceneLog.getInstance().appendErrorMsg("eachCityNum is invalid").append("eachCityNum", eachCityNum).appendClassName("TimerBattleService").appendMethodName("addRoundManZuForBianJiang").flush();
                return;
            }
            for (final Integer cityId : citySet) {
                if (this.dataGetter.getWorldCityCache().get((Object)cityId) == null) {
                    ErrorSceneLog.getInstance().appendErrorMsg("citySet is invalid").append("citySet", citySet.toString()).append("cityId", cityId).appendClassName("TimerBattleService").appendMethodName("addRoundManZuForBianJiang").flush();
                    return;
                }
            }
            final Barbarain barbarain = (Barbarain)this.dataGetter.getBarbarainCache().get((Object)WorldCityCommon.MAX_COUNTRY_LV);
            if (barbarain == null) {
                ErrorSceneLog.getInstance().appendErrorMsg("barbarain is null").append("WorldCityCommon.MAX_COUNTRY_LV", WorldCityCommon.MAX_COUNTRY_LV).appendClassName("TimerBattleService").appendMethodName("addRoundManZuForBianJiang").flush();
                return;
            }
            Integer[] armyIds = null;
            switch (manZuForceId) {
                case 101: {
                    armyIds = barbarain.getWeiIArmyIds();
                    break;
                }
                case 102: {
                    armyIds = barbarain.getShuIArmyIds();
                    break;
                }
                case 103: {
                    armyIds = barbarain.getWuIArmyIds();
                    break;
                }
            }
            if (armyIds == null || armyIds.length == 0) {
                ErrorSceneLog.getInstance().appendErrorMsg("armyIds is invalid").append("barbarain", barbarain.getId()).append("manZuForceId", manZuForceId).appendClassName("TimerBattleService").appendMethodName("addRoundSaoDangManZu").flush();
                return;
            }
            for (final Integer cityId2 : citySet) {
                final Battle battle = NewBattleManager.getInstance().getBattleByDefId(3, cityId2);
                if (battle != null) {
                    synchronized (battle.getBattleId()) {
                        this.addRoundManZuForBianJiangBattle(battle, manZuForceId, barbarain, armyIds, eachCityNum);
                        // monitorexit(battle.getBattleId())
                        continue;
                    }
                }
                final City city = this.dataGetter.getCityDao().read(cityId2);
                if (city.getForceId() == manZuForceId) {
                    this.saveRoundManZuForBianJiangFree(cityId2, manZuForceId, barbarain, armyIds, eachCityNum);
                }
                else {
                    this.fireRoundManZuForBianJiangBattle(cityId2, manZuForceId, barbarain, armyIds, eachCityNum);
                }
            }
            TimerBattleService.timerLog.info(LogUtil.formatThreadLog("TimerBattleService", "addRoundManZuForBianJiang", 2, System.currentTimeMillis() - start, "manZuForceId:" + manZuForceId + ",eachCityNum:" + eachCityNum + ",citySet:" + citySet.toString()));
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("TimerBattleService.addRoundManZuForBianJiang catch Exception.", e);
        }
    }
    
    @Override
    public void addManZuBeforeManWangLing(final int fireForceId, final int cityId) {
        try {
            final Barbarain barbarain = (Barbarain)this.dataGetter.getBarbarainCache().get((Object)WorldCityCommon.MAX_COUNTRY_LV);
            if (barbarain == null) {
                ErrorSceneLog.getInstance().appendErrorMsg("barbarain is null").append("WorldCityCommon.MAX_COUNTRY_LV", WorldCityCommon.MAX_COUNTRY_LV).appendClassName("TimerBattleService").appendMethodName("addManZuBeforeManWangLing").flush();
                return;
            }
            Integer[] armyIds = null;
            int manZuForceId = 0;
            switch (fireForceId) {
                case 1: {
                    armyIds = barbarain.getWeiIArmyIds();
                    manZuForceId = 101;
                    break;
                }
                case 2: {
                    armyIds = barbarain.getShuIArmyIds();
                    manZuForceId = 102;
                    break;
                }
                case 3: {
                    armyIds = barbarain.getWuIArmyIds();
                    manZuForceId = 103;
                    break;
                }
                default: {
                    ErrorSceneLog.getInstance().appendErrorMsg("fireForceId is invalid").append("fireForceId", fireForceId).appendClassName("TimerBattleService").appendMethodName("addManZuBeforeManWangLing").flush();
                    return;
                }
            }
            if (armyIds == null || armyIds.length == 0) {
                ErrorSceneLog.getInstance().appendErrorMsg("armyIds is invalid").append("barbarain", barbarain.getId()).append("fireForceId", fireForceId).appendClassName("TimerBattleService").appendMethodName("addManZuBeforeManWangLing").flush();
                return;
            }
            final City city = this.dataGetter.getCityDao().read(cityId);
            if (city.getForceId() != fireForceId) {
                ErrorSceneLog.getInstance().appendErrorMsg("city.forceId changed. ignore this round").append("cityId", cityId).append("fireForceId", fireForceId).append("nowForceId", city.getForceId()).appendClassName("TimerBattleService").appendMethodName("addManZuBeforeManWangLing").flush();
                return;
            }
            final int eachCityNum = 10;
            final Battle battle = NewBattleManager.getInstance().getBattleByDefId(3, cityId);
            if (battle != null) {
                synchronized (battle.getBattleId()) {
                    this.addRoundManZuForBianJiangBattle(battle, manZuForceId, barbarain, armyIds, eachCityNum);
                    // monitorexit(battle.getBattleId())
                    return;
                }
            }
            this.fireRoundManZuForBianJiangBattle(cityId, manZuForceId, barbarain, armyIds, eachCityNum);
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("TimerBattleService.addManZuBeforeManWangLing catch Exception.", e);
        }
    }
    
    private void fireRoundManZuForBianJiangBattle(final Integer cityId, final int manZuForceId, final Barbarain barbarain, final Integer[] armyIds, final int eachCityNum) {
        try {
            final List<BarbarainPhantom> list = new ArrayList<BarbarainPhantom>();
            for (int i = 0; i < eachCityNum; ++i) {
                final BarbarainPhantom barPhantom = new BarbarainPhantom();
                barPhantom.setNpcType(3);
                barPhantom.setCreateTime(new Date());
                barPhantom.setLocationId(cityId);
                barPhantom.setForceId(manZuForceId);
                barPhantom.setBarbarainId(barbarain.getId());
                barPhantom.setTacticval(1);
                barPhantom.setState(0);
                final int armyId = armyIds[WebUtil.nextInt(armyIds.length)];
                final Army army = (Army)this.dataGetter.getArmyCache().get((Object)armyId);
                barPhantom.setArmyId(armyId);
                barPhantom.setHp(army.getArmyHp());
                barPhantom.setVId(BattleService.barVid.incrementAndGet());
                list.add(barPhantom);
            }
            this.dataGetter.getBarbarainPhantomDao().batchCreate(list);
            if (WorldCityCommon.mainCityNationIdMap.containsKey(cityId)) {
                return;
            }
            final WorldCity wc = (WorldCity)this.dataGetter.getWorldCityCache().get((Object)cityId);
            String battleId = null;
            battleId = CityBuilder.getBattleId(this.dataGetter, manZuForceId, wc.getId());
            final Builder builder = BuilderFactory.getInstance().getBuilder(3);
            final Terrain terrain = builder.getTerrain(-1, wc.getId(), this.dataGetter);
            final Battle cityBattle = NewBattleManager.getInstance().createBattle(battleId);
            if (cityBattle == null) {
                ErrorSceneLog.getInstance().appendErrorMsg("create battle fail").appendBattleId(battleId).append("barbarain", barbarain.getId()).append("manZuForceId", manZuForceId).append("cityId", cityId).appendMethodName("addBarbarainNpc").flush();
                return;
            }
            final BattleAttacker battleAttacker = new BattleAttacker();
            battleAttacker.attType = 4;
            battleAttacker.attForceId = manZuForceId;
            battleAttacker.attPlayerId = -8;
            cityBattle.init(battleAttacker, 3, cityId, this.dataGetter, false, terrain.getValue());
            builder.dealUniqueStaff(this.dataGetter, cityBattle, -1, cityId);
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("TimerBattleService.fireRoundManZuForBianJiangBattle catch exception.", e);
        }
    }
    
    private void saveRoundManZuForBianJiangFree(final Integer cityId, final int manZuForceId, final Barbarain barbarain, final Integer[] armyIds, final int eachCityNum) {
        try {
            final Date date = new Date();
            final int state = 0;
            final List<BarbarainPhantom> list = new ArrayList<BarbarainPhantom>();
            for (int i = 0; i < eachCityNum; ++i) {
                final BarbarainPhantom barPhantom = new BarbarainPhantom();
                barPhantom.setNpcType(3);
                barPhantom.setCreateTime(date);
                barPhantom.setLocationId(cityId);
                barPhantom.setForceId(manZuForceId);
                barPhantom.setBarbarainId(barbarain.getId());
                barPhantom.setTacticval(1);
                barPhantom.setState(state);
                final int armyId = armyIds[WebUtil.nextInt(armyIds.length)];
                final Army army = (Army)this.dataGetter.getArmyCache().get((Object)armyId);
                barPhantom.setArmyId(armyId);
                barPhantom.setHp(army.getArmyHp());
                barPhantom.setVId(BattleService.barVid.incrementAndGet());
                list.add(barPhantom);
            }
            this.dataGetter.getBarbarainPhantomDao().batchCreate(list);
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("TimerBattleService.saveRoundManZuForBianJiangFree catch exception.", e);
        }
    }
    
    private void addRoundManZuForBianJiangBattle(final Battle battle, final int manZuForceId, final Barbarain barbarain, final Integer[] armyIds, final int eachCityNum) {
        try {
            final Date date = new Date();
            final int state = 3;
            final int cityId = battle.getDefBaseInfo().getId();
            final int defForceId = battle.getDefBaseInfo().getForceId();
            int battleSide = -1;
            if (manZuForceId == defForceId) {
                battleSide = 0;
            }
            else {
                battleSide = 1;
            }
            final List<BarbarainPhantom> list = new ArrayList<BarbarainPhantom>();
            final Builder builder = BuilderFactory.getInstance().getBuilder(3);
            for (int i = 0; i < eachCityNum; ++i) {
                final BarbarainPhantom barPhantom = new BarbarainPhantom();
                barPhantom.setNpcType(3);
                barPhantom.setCreateTime(date);
                barPhantom.setLocationId(cityId);
                barPhantom.setForceId(manZuForceId);
                barPhantom.setBarbarainId(barbarain.getId());
                barPhantom.setTacticval(1);
                barPhantom.setState(state);
                final int armyId = armyIds[WebUtil.nextInt(armyIds.length)];
                final Army army = (Army)this.dataGetter.getArmyCache().get((Object)armyId);
                barPhantom.setArmyId(armyId);
                barPhantom.setHp(army.getArmyHp());
                barPhantom.setVId(BattleService.barVid.incrementAndGet());
                list.add(barPhantom);
                final CampArmy barCa = builder.copyArmyformBarPhantom3(this.dataGetter, barbarain, battle, barPhantom, battleSide);
                battle.joinCampArmy(this.dataGetter, battleSide, barCa);
            }
            this.dataGetter.getBarbarainPhantomDao().batchCreate(list);
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("TimerBattleService.addRoundManZuForBianJiang catch exception. battleId:" + battle.getBattleId(), e);
        }
    }
    
    @Override
    public void fireASinglePK(final String param) {
        try {
            final long start = System.currentTimeMillis();
            TimerBattleService.timerLog.info(LogUtil.formatThreadLog("TimerBattleService", "fireASinglePK", 0, 0L, "param:" + param));
            final Battle battle = NewBattleManager.getInstance().getBattleByBatId(param);
            if (battle == null) {
                ErrorSceneLog.getInstance().appendErrorMsg("battle is null.").appendBattleId(param).appendClassName("TimerBattleService").appendMethodName("fireASinglePK").flush();
                return;
            }
            if (battle.getBattleType() != 3) {
                ErrorSceneLog.getInstance().appendErrorMsg("battle type is invalid.").appendBattleId(param).append("battle.getBattleType()", battle.getBattleType()).appendClassName("TimerBattleService").appendMethodName("fireASinglePK").flush();
                return;
            }
            battle.fireASinglePk(this.dataGetter);
            TimerBattleService.timerLog.info(LogUtil.formatThreadLog("TimerBattleService", "fireASinglePK", 2, System.currentTimeMillis() - start, "param:" + param));
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("TimerBattleService.fireASinglePK catch exception. param:" + param, e);
        }
    }
    
    @Override
    public void fireJiTuanJunForBianJiang(final Battle battle, final int manZuForceId, final int cityId) {
        try {
            if (battle == null) {
                return;
            }
            for (int i = 0; i < 50; ++i) {
                this.fireASinglePK(battle.getBattleId());
            }
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("TimerBattleService.fireJiTuanJunForBianJiang catch exception. manZuForceId:" + manZuForceId + ", cityId:" + cityId, e);
        }
    }
    
    @Override
    public void addMoonCakeArmyForThreeCountry(final String parama) {
        final int activityType = MiddleAutumnCache.isInActivity();
        if (activityType == 0) {
            return;
        }
        MiddleAutumnCache.getInstance().init(this.dataGetter);
        for (final Integer forceId : Constants.PLAYER_FORCE_SET) {
            this.addMoonCakeArmyForOneCountry(forceId, activityType);
        }
    }
    
    @Override
    public void checkActivityPer3Seconds() {
        final long start = System.currentTimeMillis();
        this.checkMoonCakeActivity();
        TimerBattleService.timerLog.info(LogUtil.formatThreadLog("TimerBattleService", "checkActivityPer3Seconds", 2, System.currentTimeMillis() - start, ""));
    }
    
    private void checkMoonCakeActivity() {
        try {
            final boolean hasActivity = MiddleAutumnCache.isInActivity() > 0;
            if (!hasActivity) {
                return;
            }
            for (final Integer forceId : Constants.PLAYER_FORCE_SET) {
                final MiddleAutumnCache.CountryMoonCakeObj countryMoonCakeObj = MiddleAutumnCache.getInstance().getCurrentObj(forceId);
                if (countryMoonCakeObj == null) {
                    ErrorSceneLog.getInstance().appendErrorMsg("countryMoonCakeObj == null").append("forceId", forceId).appendClassName(this.getClass().getSimpleName()).appendMethodName("checkMoonCakeActivity").flush();
                }
                else {
                    final int cityId = countryMoonCakeObj.cityId;
                    final int activityType = countryMoonCakeObj.activityType;
                    if (cityId > 0) {
                        synchronized (CityService.cityBatIdSet.get(cityId)) {
                            final List<ActivityNpc> activityNpcList = this.dataGetter.getActivityNpcDao().getActivityNpcsByLocationIdAndForceIdExclude(cityId, forceId, countryMoonCakeObj.activityType);
                            if (activityNpcList.size() == 0) {
                                MiddleAutumnCache.getInstance().setNextMoonCakeTime(forceId);
                                MiddleAutumnCache.getInstance().resetNextMoonCakeDeleteNum(forceId);
                            }
                            else {
                                final Battle battle = NewBattleManager.getInstance().getBattleByDefId(3, cityId);
                                if (battle == null) {
                                    int deleteNum = MiddleAutumnCache.getInstance().getNextMoonCakedeleteNum(forceId);
                                    if (deleteNum == 0) {
                                        deleteNum = (int)Math.ceil(activityNpcList.size() * 0.00625);
                                        MiddleAutumnCache.getInstance().setNextMoonCakeDeleteNum(forceId, deleteNum);
                                    }
                                    if (activityNpcList.size() < deleteNum) {
                                        deleteNum = activityNpcList.size();
                                    }
                                    for (int i = 0; i < deleteNum; ++i) {
                                        this.dataGetter.getActivityNpcDao().deleteById(activityNpcList.get(i).getVId());
                                    }
                                }
                            }
                            // monitorexit((String)CityService.cityBatIdSet.get((Object)Integer.valueOf(cityId)))
                            continue;
                        }
                    }
                    final long nextTime = MiddleAutumnCache.getInstance().getNextMoonCakeTime(forceId);
                    if (nextTime > 0L) {
                        if (System.currentTimeMillis() < nextTime) {
                            continue;
                        }
                        this.addMoonCakeArmyForOneCountry(forceId, activityType);
                        MiddleAutumnCache.getInstance().resetNextMoonCakeDeleteNum(forceId);
                    }
                    else {
                        ErrorSceneLog.getInstance().appendErrorMsg("error happens").append("forceId", forceId).append("cityId", cityId).append("nextTime", nextTime).appendClassName(this.getClass().getSimpleName()).appendMethodName("checkMoonCakeActivity").flush();
                    }
                }
            }
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error(String.valueOf(this.getClass().getSimpleName()) + " checkMoonCakeActivity catch Exception", e);
        }
    }
    
    private void addMoonCakeArmyForOneCountry(final int forceId, final int activityType) {
        final long start = System.currentTimeMillis();
        try {
            TimerBattleService.timerLog.info(LogUtil.formatThreadLog(this.getClass().getSimpleName(), "addMoonCakeArmyForOneCountry", 0, 0L, Integer.toString(forceId)));
            final List<BattleInfo> worldBattleInfoList = this.dataGetter.getBattleInfoDao().getModels();
            if (worldBattleInfoList.size() > 1) {
                Collections.shuffle(worldBattleInfoList);
            }
            int ourOnLineNum = 0;
            try {
                for (final PlayerDto playerDto : Players.getAllPlayer()) {
                    if (playerDto.forceId == forceId) {
                        ++ourOnLineNum;
                    }
                }
            }
            catch (Exception e) {
                ErrorSceneLog.getInstance().error(String.valueOf(this.getClass().getSimpleName()) + " traverse Players.getAllPlayer() catch Exception", e);
            }
            int num = ourOnLineNum * 3;
            if (num < 5) {
                num = 5;
            }
            boolean added = false;
            for (final BattleInfo battleInfo : worldBattleInfoList) {
                final Battle battle = NewBattleManager.getInstance().getBattleByBatId(battleInfo.getBattleId());
                if (battle == null) {
                    continue;
                }
                if (forceId != battle.getDefBaseInfo().getForceId()) {
                    continue;
                }
                synchronized (battle.getBattleId()) {
                    final List<Integer> moonCakeForceIdList = new ArrayList<Integer>();
                    for (final int tempForceId : battle.attSideDetail.keySet()) {
                        if (Constants.PLAYER_FORCE_SET.contains(tempForceId)) {
                            moonCakeForceIdList.add(tempForceId);
                        }
                    }
                    if (moonCakeForceIdList.size() != 0) {
                        if (moonCakeForceIdList.size() > 1) {
                            Collections.shuffle(worldBattleInfoList);
                        }
                        final int moonCakeForceId = moonCakeForceIdList.get(0);
                        final int armyId = this.addMoonCakeArmyInCityForCounterSide(battle, 1, moonCakeForceId, num, activityType);
                        final int ourNum = 5 - battle.getDefCamp().size();
                        if (ourNum > 0) {
                            this.addMoonCakeArmyInCityForOurSideToThresholdTry(battle, 0, forceId, ourNum, activityType);
                        }
                        final int currentCityId = battle.getDefBaseInfo().getId();
                        if (this.dataGetter.getWorldCityCache().get((Object)currentCityId) != null) {
                            MiddleAutumnCache.getInstance().setCurrentCityId(forceId, currentCityId, armyId);
                            final String cityName = ((WorldCity)this.dataGetter.getWorldCityCache().get((Object)currentCityId)).getName();
                            final String msg = MiddleAutumnCache.getInstance().getActivityBoBaoMsg(activityType, cityName, armyId);
                            if (msg != null) {
                                for (final PlayerDto dto : Players.getAllPlayer()) {
                                    if (forceId == dto.forceId && dto.cs[10] == '1') {
                                        this.dataGetter.getChatService().sendSystemChat("SYS2ONE", dto.playerId, forceId, msg, new ChatLink(2, new StringBuilder(String.valueOf(battle.getDefBaseInfo().getId())).toString()));
                                    }
                                }
                            }
                            else {
                                ErrorSceneLog.getInstance().appendErrorMsg("msg is null").append("activityType", activityType).append("cityName", cityName).append("armyId", armyId).appendClassName(this.getClass().getSimpleName()).appendMethodName("addMoonCakeArmyForOneCountry").flush();
                            }
                            added = true;
                            // monitorexit(battle.getBattleId())
                            break;
                        }
                        ErrorSceneLog.getInstance().appendErrorMsg("dataGetter.getWorldCityCache().get(currentCityId) == null").append("forceId", forceId).append("battleId", battle.getBattleId()).append("currentCityId", currentCityId).appendClassName(this.getClass().getSimpleName()).appendMethodName("checkMoonCakeActivity").flush();
                    }
                    // monitorexit(battle.getBattleId())
                }
            }
            if (!added) {
                MiddleAutumnCache.getInstance().setNextMoonCakeTime(forceId);
                String msg2 = null;
                switch (activityType) {
                    case 10: {
                        msg2 = LocalMessages.MOON_CAKE_ARMY_IGNORE_BOBAO_FORMAT;
                        break;
                    }
                    case 11: {
                        msg2 = LocalMessages.NATIONAL_DAY_ARMY_IGNORE_BOBAO_FORMAT;
                        break;
                    }
                    case 15: {
                        msg2 = LocalMessages.IRON_GIVE_IGNORE_BOBAO_FORMAT;
                        break;
                    }
                    case 16: {
                        msg2 = LocalMessages.CHRISTMAS_DAY_IGNORE_BOBAO_FORMAT;
                        break;
                    }
                    case 18: {
                        msg2 = LocalMessages.BEAST_IGNORE_BOBAO_FORMAT;
                        break;
                    }
                }
                this.dataGetter.getChatService().sendSystemChat("COUNTRY", 0, forceId, msg2, null);
            }
            TimerBattleService.timerLog.info(LogUtil.formatThreadLog(this.getClass().getSimpleName(), "addMoonCakeArmyForOneCountry", 2, System.currentTimeMillis() - start, Integer.toString(forceId)));
        }
        catch (Exception e2) {
            ErrorSceneLog.getInstance().error(String.valueOf(this.getClass().getSimpleName()) + " addMoonCakeArmyForOneCountry catch Exception", e2);
        }
    }
    
    private int addMoonCakeArmyInCityForOneSide(final Battle battle, final int battleSide, final int moonCakeForceId, final int num, final int activityType) {
        try {
            final Builder builder = BuilderFactory.getInstance().getBuilder(3);
            final int state = 3;
            final Date date = new Date();
            final int locationId = battle.getDefBaseInfo().getId();
            final int armyId = MiddleAutumnCache.getInstance().getActivityArmyId(activityType);
            final Army moonCakeArmy = (Army)this.dataGetter.getArmyCache().get((Object)armyId);
            if (moonCakeArmy == null) {
                ErrorSceneLog.getInstance().appendErrorMsg("moonCakeArmy is null").append("armyId", armyId).appendClassName(this.getClass().getSimpleName()).appendMethodName("addMoonCakeArmyInCityForOneSide").flush();
            }
            final List<ActivityNpc> batchList = new LinkedList<ActivityNpc>();
            for (int i = 0; i < num; ++i) {
                final ActivityNpc activityNpc = new ActivityNpc();
                activityNpc.setVId(this.activityNpcMaxVid.incrementAndGet());
                activityNpc.setNpcType(activityType);
                activityNpc.setState(state);
                activityNpc.setCreateTime(date);
                activityNpc.setLocationId(locationId);
                activityNpc.setForceId(moonCakeForceId);
                activityNpc.setTableId(0);
                activityNpc.setArmyId(armyId);
                activityNpc.setHp(moonCakeArmy.getArmyHp());
                activityNpc.setTacticval(1);
                activityNpc.setMoveLine(null);
                final CampArmy moonCakeCA = builder.copyArmyFromActivityNpc(this.dataGetter, null, battle, activityNpc, battleSide);
                battle.joinCampArmy(this.dataGetter, battleSide, moonCakeCA);
                batchList.add(activityNpc);
            }
            this.dataGetter.getActivityNpcDao().batchCreate(batchList);
            return armyId;
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error(String.valueOf(this.getClass().getSimpleName()) + " addMoonCakeArmyInCityForOneSide catch Exception", e);
            return 0;
        }
    }
    
    private void addMoonCakeArmyInCityForOurSideToThresholdTry(final Battle battle, final int battleSide, final int moonCakeForceId, final int num, final int activityType) {
        try {
            this.addMoonCakeArmyInCityForOneSide(battle, battleSide, moonCakeForceId, num, activityType);
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error(String.valueOf(this.getClass().getSimpleName()) + " addMoonCakeArmyInCityForOurSideToThresholdTry catch Exception", e);
        }
    }
    
    private int addMoonCakeArmyInCityForCounterSide(final Battle battle, final int battleSide, final int moonCakeForceId, final int num, final int activityType) {
        try {
            return this.addMoonCakeArmyInCityForOneSide(battle, battleSide, moonCakeForceId, num, activityType);
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error(String.valueOf(this.getClass().getSimpleName()) + " addMoonCakeArmyInCityForCounterSide catch Exception", e);
            return 0;
        }
    }
    
    private class BattleCheckThread extends Thread
    {
        public BattleCheckThread() {
            super("battle-check-thread");
        }
        
        @Override
        public void run() {
            while (true) {
                try {
                    final long curTime = System.currentTimeMillis();
                    for (final Battle battle : NewBattleManager.getInstance().batMap.values()) {
                        try {
                            if (curTime <= battle.getNextMaxExeTime() + 60000L) {
                                continue;
                            }
                            TimerBattleService.this.battleService.dobattleExceptionBatId(battle.getBattleId(), new Exception("battle Check Exception!"));
                        }
                        catch (Exception e) {
                            ErrorSceneLog.getInstance().error("BattleCheckThread Exception battle", e);
                        }
                    }
                }
                catch (Exception e2) {
                    ErrorSceneLog.getInstance().error("BattleCheckThread Exception", e2);
                    try {
                        Thread.sleep(30000L);
                    }
                    catch (InterruptedException e3) {
                        ErrorSceneLog.getInstance().error("BattleCheckThread InterruptedException:", e3);
                    }
                    continue;
                }
                finally {
                    try {
                        Thread.sleep(30000L);
                    }
                    catch (InterruptedException e3) {
                        ErrorSceneLog.getInstance().error("BattleCheckThread InterruptedException:", e3);
                    }
                }
                try {
                    Thread.sleep(30000L);
                }
                catch (InterruptedException e3) {
                    ErrorSceneLog.getInstance().error("BattleCheckThread InterruptedException:", e3);
                }
            }
        }
    }
}
