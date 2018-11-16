package com.reign.gcld.juben.service;

import org.springframework.stereotype.*;
import java.util.concurrent.atomic.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.general.service.*;
import com.reign.gcld.job.service.*;
import com.reign.gcld.scenario.service.*;
import com.reign.gcld.player.service.*;
import com.reign.gcld.general.dao.*;
import com.reign.gcld.player.dao.*;
import com.reign.gcld.battle.dao.*;
import com.reign.gcld.juben.dao.*;
import com.reign.gcld.store.dao.*;
import com.reign.gcld.sdata.cache.*;
import com.reign.gcld.common.log.*;
import com.reign.framework.netty.servlet.*;
import com.reign.framework.json.*;
import org.springframework.transaction.annotation.*;
import org.apache.commons.lang.*;
import com.reign.gcld.juben.domain.*;
import com.reign.gcld.common.web.*;
import com.reign.gcld.system.domain.*;
import com.reign.gcld.battle.service.*;
import java.util.concurrent.*;
import com.reign.gcld.store.service.*;
import com.reign.gcld.player.dto.*;
import com.reign.gcld.store.domain.*;
import com.reign.gcld.log.*;
import com.reign.gcld.chat.common.*;
import com.reign.gcld.battle.domain.*;
import com.reign.gcld.scenario.common.*;
import com.reign.gcld.player.common.*;
import com.reign.gcld.world.service.*;
import com.reign.gcld.team.common.*;
import com.reign.gcld.player.domain.*;
import com.reign.gcld.general.dto.*;
import java.util.*;
import com.reign.gcld.general.domain.*;
import com.reign.gcld.civiltrick.trick.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.gcld.team.service.*;
import com.reign.util.*;
import com.reign.gcld.scenario.message.*;
import com.reign.gcld.scenario.common.choice.*;
import com.reign.gcld.common.*;
import com.reign.gcld.common.util.*;
import com.reign.gcld.battle.scene.*;
import com.reign.gcld.battle.common.*;
import com.reign.gcld.juben.common.*;
import com.reign.gcld.world.common.*;
import com.reign.gcld.tech.domain.*;
import java.io.*;

@Component("juBenService")
public class JuBenService implements IJuBenService
{
    private static final Logger errorLog;
    private static final Logger timeLog;
    private static final Logger timerLog;
    public static final Map<String, MiniTimeWinInfo> miniWinTime;
    public static AtomicInteger scenarioNpcVId;
    @Autowired
    private IDataGetter dataGetter;
    @Autowired
    private IPlayerScenarioDao playerScenarioDao;
    @Autowired
    private IPlayerScenarioCityDao playerScenarioCityDao;
    @Autowired
    private SoloCityCache soloCityCache;
    @Autowired
    private SoloDramaCache soloDramaCache;
    @Autowired
    private SoloEventCache soloEventCache;
    @Autowired
    private SoloRewardCache soloRewardCache;
    @Autowired
    private IPlayerDao playerDao;
    @Autowired
    private SoloRoadCache soloRoadCache;
    @Autowired
    private IPlayerGeneralMilitaryDao playerGeneralMilitaryDao;
    @Autowired
    private IJuBenDataCache juBenDataCache;
    @Autowired
    private IGeneralService generalService;
    @Autowired
    private GeneralCache generalCache;
    @Autowired
    private TroopCache troopCache;
    @Autowired
    private IJobService jobService;
    @Autowired
    private IBattleService battleService;
    @Autowired
    private ScenarioEventManager scenarioEventManager;
    @Autowired
    private StratagemCache stratagemCache;
    @Autowired
    private IPlayerResourceDao playerResourceDao;
    @Autowired
    private IPlayerService playerService;
    @Autowired
    private IPlayerGeneralCivilDao playerGeneralCivilDao;
    @Autowired
    private IScenarioNpcDao scenarioNpcDao;
    @Autowired
    private IPlayerAttributeDao playerAttributeDao;
    @Autowired
    private ArmiesCache armiesCache;
    @Autowired
    private IPlayerArmyDao playerArmyDao;
    @Autowired
    private IScenarioInfoDao scenarioInfoDao;
    @Autowired
    private ItemsCache itemsCache;
    @Autowired
    private IStoreHouseService storeHouseService;
    @Autowired
    private IStoreHouseDao storeHouseDao;
    @Autowired
    private EquipCache equipCache;
    @Autowired
    private IStoreHouseSellDao storeHouseSellDao;
    @Autowired
    private WdSjpDramaCache wdSjpDramaCache;
    @Autowired
    private BattleDataCache battleDataCache;
    public static Object lock;
    
    static {
        errorLog = CommonLog.getLog(JuBenService.class);
        timeLog = new TimerLogger();
        timerLog = new TimerLogger();
        miniWinTime = new ConcurrentHashMap<String, MiniTimeWinInfo>();
        JuBenService.scenarioNpcVId = new AtomicInteger();
        JuBenService.lock = new Object();
    }
    
    @Override
    public void initMiniWinTime() {
        final List<ScenarioInfo> list = this.scenarioInfoDao.getModels();
        if (list != null) {
            for (final ScenarioInfo si : list) {
                final MiniTimeWinInfo mtwi = new MiniTimeWinInfo();
                mtwi.forceId = si.getForceId();
                mtwi.name = si.getPlayerName();
                mtwi.miniTime = (long)si.getMinTime();
                final String str = this.getMiniWinStr(si.getSoloId(), si.getGrade());
                JuBenService.miniWinTime.put(str, mtwi);
            }
        }
    }
    
    @Transactional
    @Override
    public byte[] getJuBenList(final PlayerDto playerDto, final Request request) {
        final int playerId = playerDto.playerId;
        final PlayerAttribute pa = this.playerAttributeDao.read(playerId);
        final char[] cs = pa.getFunctionId().toCharArray();
        if (cs[62] != '1') {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.FUNCTION_NOT_OPEN);
        }
        List<PlayerScenario> psList = this.playerScenarioDao.getScenarioByPid(playerId);
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.startArray("jubens");
        SoloDrama soloDrama = null;
        final List<Armies> list = this.armiesCache.getJubenOpenList();
        Armies armies = null;
        boolean isOpen = false;
        for (boolean create = true; list.size() > psList.size() && create; psList = this.playerScenarioDao.getScenarioByPid(playerId), isOpen = true, create = true) {
            create = false;
            for (int i = psList.size(); i >= 0; --i) {
                armies = list.get(i);
                boolean needOpen = true;
                for (final PlayerScenario ps : psList) {
                    if (ps.getScenarioId() == armies.getDropMap().get(105).id) {
                        needOpen = false;
                        break;
                    }
                }
                if (needOpen) {
                    final PlayerArmy playerArmy = this.playerArmyDao.getPlayerArmy(playerId, armies.getId());
                    if (playerArmy != null && playerArmy.getWinNum() > 0) {
                        final boolean res = this.openNextJuBen(playerId, armies.getDropMap().get(105).id);
                        if (res) {
                            break;
                        }
                    }
                }
            }
        }
        if (!isOpen && list.size() > psList.size()) {
            armies = list.get(psList.size());
        }
        else {
            armies = null;
        }
        final PlayerScenario[] pss = new PlayerScenario[psList.size() + 2];
        for (final PlayerScenario ps2 : psList) {
            if (this.wdSjpDramaCache.getWorldDramaByDramaId(ps2.getScenarioId()) != null) {
                continue;
            }
            pss[this.soloDramaCache.getSoloOrder(ps2.getScenarioId()) - 1] = ps2;
        }
        int grade = 5;
        PlayerScenario[] array;
        for (int length = (array = pss).length, k = 0; k < length; ++k) {
            final PlayerScenario ps = array[k];
            if (ps != null) {
                soloDrama = (SoloDrama)this.soloDramaCache.get((Object)ps.getScenarioId());
                if (this.wdSjpDramaCache.getWorldDramaByDramaId(ps.getScenarioId()) == null) {
                    grade = soloDrama.getGrade();
                    doc.startObject();
                    doc.createElement("sId", ps.getScenarioId());
                    doc.createElement("title", soloDrama.getName());
                    doc.createElement("openLv", soloDrama.getLv());
                    doc.createElement("target", soloDrama.getPlot());
                    doc.startArray("degrees");
                    final String[] starLvs = ps.getStarlv().split(",");
                    int curStar = 0;
                    Map<Integer, SoloReward> starMap = null;
                    int j = 1;
                    int nextStar = 0;
                    SoloReward reward = null;
                    for (j = 1; j <= grade; ++j) {
                        doc.startObject();
                        doc.createElement("grade", j);
                        starMap = this.soloRewardCache.getBySoloId(ps.getScenarioId(), j);
                        if (j <= starLvs.length) {
                            curStar = Integer.valueOf(starLvs[j - 1]);
                            doc.createElement("curStar", curStar);
                            doc.createElement("maxStar", 5);
                            final String str = this.getMiniWinStr(ps.getScenarioId(), j);
                            final MiniTimeWinInfo mtwi = JuBenService.miniWinTime.get(str);
                            if (mtwi != null) {
                                doc.createElement("winForceId", mtwi.forceId);
                                doc.createElement("winName", mtwi.name);
                                doc.createElement("winTime", mtwi.miniTime * 1000L);
                            }
                            nextStar = ((curStar + 1 > 5) ? 5 : (curStar + 1));
                            doc.createElement("nextStar", nextStar);
                            reward = starMap.get(nextStar);
                            doc.createElement("food", reward.getReqFood());
                            doc.appendJson(this.getReward(reward));
                            doc.createElement("open", 1);
                            final Map<Integer, SoloCity> map = this.soloCityCache.getBySoloId(ps.getScenarioId());
                            doc.appendJson(this.getEventInfo(soloDrama.getEvent(j), map));
                        }
                        else if (j == starLvs.length + 1) {
                            doc.createElement("grade", j);
                            doc.createElement("curStar", 0);
                            doc.createElement("maxStar", 5);
                            doc.createElement("nextStar", 1);
                            reward = starMap.get(nextStar);
                            doc.createElement("food", reward.getReqFood());
                            doc.appendJson(this.getReward(reward));
                            doc.createElement("open", 0);
                            final Map<Integer, SoloCity> map2 = this.soloCityCache.getBySoloId(ps.getScenarioId());
                            doc.appendJson(this.getEventInfo(soloDrama.getEvent(j), map2));
                        }
                        if (starMap != null) {
                            final byte[] bytes = this.getGradeTotalReward(starMap);
                            if (bytes != null) {
                                doc.appendJson(bytes);
                            }
                        }
                        doc.endObject();
                    }
                    doc.endArray();
                    doc.endObject();
                }
            }
        }
        int nextSoloId = 0;
        if (armies != null) {
            nextSoloId = armies.getDropMap().get(105).id;
            soloDrama = (SoloDrama)this.soloDramaCache.get((Object)nextSoloId);
            if (soloDrama != null) {
                doc.startObject();
                doc.createElement("sId", soloDrama.getId());
                doc.createElement("title", soloDrama.getName());
                doc.createElement("openLv", soloDrama.getLv());
                doc.createElement("target", soloDrama.getPlot());
                doc.createElement("openPower", soloDrama.getOpenPower());
                doc.startArray("degrees");
                doc.endArray();
                doc.endObject();
            }
        }
        doc.endArray();
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    private byte[] getGradeTotalReward(final Map<Integer, SoloReward> starMap) {
        final JsonDocument doc = new JsonDocument();
        try {
            Map<Integer, Integer> totalRewards = null;
            for (final Integer key : starMap.keySet()) {
                final SoloReward reward = starMap.get(key);
                final Map<Integer, Integer> cellMap = reward.getRewardMap();
                if (cellMap == null) {
                    continue;
                }
                for (final Integer rewardKey : cellMap.keySet()) {
                    if (totalRewards == null) {
                        totalRewards = new HashMap<Integer, Integer>();
                    }
                    final Integer value = totalRewards.get(rewardKey);
                    if (value == null) {
                        totalRewards.put(rewardKey, cellMap.get(rewardKey));
                    }
                    else {
                        totalRewards.put(rewardKey, cellMap.get(rewardKey) + value);
                    }
                }
            }
            if (totalRewards == null) {
                return null;
            }
            doc.startArray("totalRewards");
            for (final Integer key : totalRewards.keySet()) {
                doc.startObject();
                if (key == 43) {
                    final int itemId = totalRewards.get(key);
                    final Items items = (Items)this.itemsCache.get((Object)itemId);
                    doc.createElement("type", items.getPic());
                    doc.createElement("val", 1);
                }
                else {
                    doc.createElement("type", key);
                    doc.createElement("val", totalRewards.get(key));
                }
                doc.endObject();
            }
            doc.endArray();
        }
        catch (Exception e) {
            JuBenService.errorLog.error(this, e);
        }
        return doc.toByte();
    }
    
    private byte[] getReward(final SoloReward sr) {
        final JsonDocument doc = new JsonDocument();
        doc.createElement("bNum", sr.getTeqHyNumber());
        doc.startArray("rewards");
        for (final Integer key : sr.getRewardMap().keySet()) {
            if (key == 43) {
                final int itemId = sr.getRewardMap().get(key);
                final Items items = (Items)this.itemsCache.get((Object)itemId);
                doc.startObject();
                doc.createElement("type", items.getPic());
                doc.createElement("val", 1);
                doc.endObject();
            }
            else {
                doc.startObject();
                doc.createElement("type", key);
                doc.createElement("val", sr.getRewardMap().get(key));
                doc.endObject();
            }
        }
        doc.endArray();
        return doc.toByte();
    }
    
    private byte[] getEventInfo(final String eventStrs, final Map<Integer, SoloCity> map) {
        final String[] strs = eventStrs.split(";");
        final JsonDocument doc = new JsonDocument();
        doc.startArray("events");
        int event = 0;
        String[] array;
        for (int length = (array = strs).length, i = 0; i < length; ++i) {
            final String str = array[i];
            if (!StringUtils.isBlank(str)) {
                event = Integer.valueOf(str);
                final SoloEvent se = (SoloEvent)this.soloEventCache.get((Object)event);
                if (se != null && StringUtils.isBlank(se.getTrigger()) && !StringUtils.isBlank(se.getFlag1())) {
                    final String[] flags = se.getFlag1().split(",");
                    final SoloCity sc = map.get(Integer.valueOf(flags[1]));
                    doc.startObject();
                    doc.createElement("cId", flags[1]);
                    if (sc != null) {
                        doc.createElement("cCapital", sc.getCapital());
                    }
                    if (flags.length >= 3) {
                        doc.createElement("cPic", flags[2]);
                    }
                    doc.createElement("cIntro", se.getIntro());
                    doc.endObject();
                }
            }
        }
        doc.endArray();
        return doc.toByte();
    }
    
    @Transactional
    @Override
    public void openJuBenNextStar(final int playerId, final int soloId, final int grade) {
    }
    
    @Transactional
    @Override
    public boolean openNextJuBen(final int playerId, final int soloId) {
        try {
            final Player player = this.playerDao.read(playerId);
            PlayerScenario playerScenario = this.playerScenarioDao.getScenarioByPidSid(playerId, soloId);
            final Date date = new Date();
            if (playerScenario != null) {
                return false;
            }
            playerScenario = new PlayerScenario();
            playerScenario.setPlayerId(playerId);
            playerScenario.setScenarioId(soloId);
            playerScenario.setAttackable(1);
            playerScenario.setStarttime(date);
            playerScenario.setEndtime(date);
            playerScenario.setGrade(1);
            playerScenario.setRewarded(0);
            playerScenario.setState(0);
            playerScenario.setEventInfo("");
            playerScenario.setStarlv("0,");
            playerScenario.setCurstar(5);
            playerScenario.setJieBingCount(0);
            playerScenario.setOvertime(0L);
            this.playerScenarioDao.create(playerScenario);
            final Map<Integer, SoloCity> map = this.soloCityCache.getBySoloId(soloId);
            if (map == null) {
                return false;
            }
            int belong = 0;
            for (final SoloCity sc : map.values()) {
                final PlayerScenarioCity playerScenarioCity = new PlayerScenarioCity();
                playerScenarioCity.setCityId(sc.getId());
                belong = 0;
                if (sc.getBelong() == 0) {
                    belong = player.getForceId();
                }
                playerScenarioCity.setForceId(belong);
                playerScenarioCity.setPlayerId(playerId);
                playerScenarioCity.setScenarioId(sc.getSoloId());
                playerScenarioCity.setState(0);
                playerScenarioCity.setTrickinfo("");
                playerScenarioCity.setUpdatetime(date.getTime());
                playerScenarioCity.setTitle(0);
                playerScenarioCity.setBorder(0);
                playerScenarioCity.setEventInfo("");
                this.playerScenarioCityDao.create(playerScenarioCity);
            }
            return true;
        }
        catch (Exception e) {
            JuBenService.errorLog.error(this, e);
            return false;
        }
    }
    
    public void initJuBenNpcs(final PlayerDto playerDto, final int scenarioId, final int grade) {
        try {
            final List<ScenarioNpc> snList = new LinkedList<ScenarioNpc>();
            final int npcType = 2;
            final int state = 0;
            final Date date = new Date();
            final Map<Integer, SoloCity> scMap = this.dataGetter.getSoloCityCache().getBySoloId(scenarioId);
            for (final SoloCity sc : scMap.values()) {
                final List<Integer> npcList = sc.getNpcListMap().get(grade);
                if (npcList != null) {
                    if (npcList.size() == 0) {
                        continue;
                    }
                    int forceId = 0;
                    if (sc.getBelong() == 0) {
                        forceId = playerDto.forceId;
                    }
                    if (sc.getBelong() > 100) {
                        forceId = sc.getBelong();
                    }
                    final int locationId = sc.getId();
                    for (final Integer armyId : npcList) {
                        final ScenarioNpc scenarioNpc = new ScenarioNpc();
                        scenarioNpc.setVId(JuBenService.scenarioNpcVId.incrementAndGet());
                        scenarioNpc.setPlayerId(playerDto.playerId);
                        scenarioNpc.setScenarioId(scenarioId);
                        scenarioNpc.setLocationId(locationId);
                        scenarioNpc.setForceId(forceId);
                        scenarioNpc.setNpcType(npcType);
                        scenarioNpc.setArmyId(armyId);
                        final Army army = (Army)this.dataGetter.getArmyCache().get((Object)armyId);
                        scenarioNpc.setHp(army.getArmyHp());
                        scenarioNpc.setTacticVal(1);
                        scenarioNpc.setState(state);
                        scenarioNpc.setAddTime(date);
                        snList.add(scenarioNpc);
                    }
                }
            }
            this.dataGetter.getScenarioNpcDao().batchCreate(snList);
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("JuBenService.InitJuBenNpcs catch Exception", e);
        }
    }
    
    private void updateJuBen(final int playerId, final int soloId, final int grade) {
        final Date starttime = new Date();
        final Map<Integer, SoloReward> srMap = this.soloRewardCache.getBySoloId(soloId, grade);
        long endTime = srMap.get(5).getReqTime();
        endTime = starttime.getTime() + endTime * 1000L;
        this.playerScenarioDao.updateInit(playerId, soloId, grade, starttime, new Date(endTime), 5, 0);
        final Player player = this.playerDao.read(playerId);
        final Map<Integer, SoloCity> map = this.soloCityCache.getBySoloId(soloId);
        int belong = 0;
        for (final SoloCity sc : map.values()) {
            final PlayerScenarioCity playerScenarioCity = new PlayerScenarioCity();
            playerScenarioCity.setCityId(sc.getId());
            belong = 0;
            if (sc.getBelong() == 0) {
                belong = player.getForceId();
            }
            else if (sc.getBelong() > 100) {
                belong = sc.getBelong();
            }
            this.playerScenarioCityDao.updateInit(playerId, sc.getSoloId(), sc.getId(), 0, belong, "", starttime.getTime(), 0, 0, "");
        }
    }
    
    @Override
    public void initAllJuBenIngToCache() {
        final List<PlayerScenario> list = this.playerScenarioDao.getListByState();
        if (list != null) {
            final Date date = new Date();
            for (final PlayerScenario ps : list) {
                try {
                    long endTime = 0L;
                    final ServerTime st = GcldInitManager.lastServerTime;
                    if (st != null) {
                        endTime = date.getTime() - st.getEndTime().getTime() + ps.getEndtime().getTime() + 10000L;
                    }
                    if (endTime < ps.getEndtime().getTime()) {
                        final Map<Integer, SoloReward> srMap = this.soloRewardCache.getBySoloId(ps.getScenarioId(), ps.getGrade());
                        endTime = srMap.get(ps.getCurstar()).getReqTime();
                        endTime = date.getTime() + endTime * 1000L;
                    }
                    final Date eTime = new Date(endTime);
                    ps.setEndtime(eTime);
                    this.playerScenarioDao.updateEndTime(ps.getPlayerId(), ps.getScenarioId(), eTime);
                    this.cacheJuBen(ps.getPlayerId(), ps);
                    this.recoverJuBenBattles(ps);
                }
                catch (Exception e) {
                    JuBenService.errorLog.error("JuBenService initAllJuBenIngToCache playerId\uff1a" + ps.getPlayerId() + " soId\uff1a" + ps.getScenarioId() + e.getMessage());
                }
            }
        }
    }
    
    private void recoverJuBenBattles(final PlayerScenario playerScenario) {
        try {
            final int playerId = playerScenario.getPlayerId();
            final int scenarioId = playerScenario.getScenarioId();
            final Player player = this.dataGetter.getPlayerDao().read(playerId);
            final int playerForceId = player.getForceId();
            final List<PlayerGeneralMilitary> pgmList = this.dataGetter.getPlayerGeneralMilitaryDao().getMilitaryList(playerId);
            final Map<Integer, List<PlayerGeneralMilitary>> pgmCityIdMap = new HashMap<Integer, List<PlayerGeneralMilitary>>();
            for (final PlayerGeneralMilitary pgm : pgmList) {
                List<PlayerGeneralMilitary> list = pgmCityIdMap.get(pgm.getJubenLoId());
                if (list == null) {
                    list = new LinkedList<PlayerGeneralMilitary>();
                    pgmCityIdMap.put(pgm.getJubenLoId(), list);
                }
                list.add(pgm);
            }
            final List<PlayerScenarioCity> psList = this.playerScenarioCityDao.getSCityByPidSid(playerId, scenarioId);
            for (final PlayerScenarioCity playerScenarioCity : psList) {
                final int cityId = playerScenarioCity.getCityId();
                final int cityForceId = playerScenarioCity.getForceId();
                final List<ScenarioNpc> scList = this.dataGetter.getScenarioNpcDao().getByPlayerIdLocationId(playerId, cityId);
                final List<ScenarioNpc> scAttList = new LinkedList<ScenarioNpc>();
                final List<ScenarioNpc> scDefList = new LinkedList<ScenarioNpc>();
                for (final ScenarioNpc scenarioNpc : scList) {
                    if (scenarioNpc.getForceId() == cityForceId) {
                        scDefList.add(scenarioNpc);
                    }
                    else {
                        scAttList.add(scenarioNpc);
                    }
                }
                boolean hasAttArmy = false;
                boolean hasDefArmy = false;
                final List<PlayerGeneralMilitary> pgmListInThisCity = pgmCityIdMap.get(cityId);
                final boolean hasPgmInThisCity = pgmListInThisCity != null && pgmListInThisCity.size() > 0;
                if (playerForceId == cityForceId) {
                    hasAttArmy = (scAttList.size() > 0);
                    hasDefArmy = (hasPgmInThisCity || scDefList.size() > 0);
                }
                else {
                    hasAttArmy = (scAttList.size() > 0 || hasPgmInThisCity);
                    hasDefArmy = (scDefList.size() > 0);
                }
                if (hasAttArmy && hasDefArmy) {
                    this.fireRecoverJubenBattle(player, cityId);
                }
            }
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("JuBenService recoverJuBenBattles catch Exception. playerId\uff1a" + playerScenario.getPlayerId() + " soId\uff1a" + playerScenario.getScenarioId(), e);
        }
    }
    
    private void fireRecoverJubenBattle(final Player player, final int cityId) {
        try {
            final int playerId = player.getPlayerId();
            final Builder builder = BuilderFactory.getInstance().getBuilder(18);
            final String battleId = NewBattleManager.getBattleId(18, playerId, cityId);
            final Battle juBenCityBattle = NewBattleManager.getInstance().createBattle(battleId);
            if (juBenCityBattle == null) {
                ErrorSceneLog.getInstance().appendErrorMsg("create battle fail").appendBattleId(battleId).append("playerId", playerId).append("cityId", cityId).appendClassName("JuBenService").appendMethodName("fireARecoverJubenBattle").flush();
                return;
            }
            final Terrain terrain = builder.getTerrain(-1, cityId, this.dataGetter);
            final BattleAttacker battleAttacker = new BattleAttacker();
            battleAttacker.attType = 7;
            battleAttacker.attForceId = 0;
            battleAttacker.attPlayerId = playerId;
            battleAttacker.attPlayer = player;
            juBenCityBattle.init(battleAttacker, 18, cityId, this.dataGetter, false, terrain.getValue());
            builder.dealUniqueStaff(this.dataGetter, juBenCityBattle, -1, cityId);
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("JuBenService fireRecoverJubenBattle catch Exception.", e);
        }
    }
    
    private JuBenDto cacheJuBen(final int playerId, final int sId) {
        final PlayerScenario ps = this.playerScenarioDao.getScenarioByPidSid(playerId, sId);
        return this.cacheJuBen(playerId, ps);
    }
    
    private JuBenDto cacheJuBen(final int playerId, final PlayerScenario ps) {
        final JuBenDto juBenDto = new JuBenDto();
        juBenDto.player_id = ps.getPlayerId();
        juBenDto.juBen_id = ps.getScenarioId();
        juBenDto.startTime = ps.getStarttime().getTime();
        juBenDto.endTime = ps.getEndtime().getTime();
        juBenDto.overTime = ps.getOvertime();
        juBenDto.grade = ps.getGrade();
        final SoloDrama soloDrama = (SoloDrama)this.soloDramaCache.get((Object)ps.getScenarioId());
        juBenDto.title = soloDrama.getName();
        juBenDto.star = ps.getCurstar();
        juBenDto.state = ps.getState();
        juBenDto.target = soloDrama.getPlot();
        juBenDto.jieBingCount = ps.getJieBingCount();
        final Player player = this.playerDao.read(ps.getPlayerId());
        juBenDto.player_force_id = player.getForceId();
        juBenDto.maxJieBingCount = this.dataGetter.getSoloRewardCache().getMaxJieBingCount(juBenDto.juBen_id, juBenDto.grade, juBenDto.star);
        juBenDto.roadLinked = new CopyOnWriteArraySet<Integer>();
        juBenDto.royalJadeBelong = -1;
        juBenDto.royalEndTime = 0L;
        juBenDto.cityChangeMap = new ConcurrentHashMap<Integer, Integer>();
        if (soloDrama.getId() == 11) {
            juBenDto.mengdeLocation = 142;
        }
        juBenDto.isMengdeSafe = false;
        if (juBenDto.juBen_id == 9) {
            juBenDto.roadLinked.add(123);
            if (juBenDto.grade >= 1 && juBenDto.grade <= 3) {
                juBenDto.roadLinked.add(121);
            }
        }
        final Map<Integer, SoloCity> map = this.soloCityCache.getBySoloId(ps.getScenarioId());
        for (final SoloCity sc : map.values()) {
            if (sc.getCapital() == 1) {
                if (sc.getBelong() == 0) {
                    juBenDto.capital = sc.getId();
                }
                else {
                    juBenDto.npcCapital = sc.getId();
                }
            }
        }
        final int grade = juBenDto.grade;
        List<ScenarioEvent> list = null;
        try {
            final String eventIds = (grade == 1) ? soloDrama.getEvent1() : ((grade == 2) ? soloDrama.getEvent2() : ((grade == 3) ? soloDrama.getEvent3() : ((grade == 4) ? soloDrama.getEvent4() : soloDrama.getEvent5())));
            final String eventStoreInfo = ps.getEventInfo();
            list = this.scenarioEventManager.addScenarioToPlayer(playerId, eventIds, eventStoreInfo);
            juBenDto.eventList = list;
        }
        catch (Exception e) {
            JuBenService.errorLog.error("addScenarioToPlayer fail...playerId:" + playerId + "\tid:" + juBenDto.juBen_id);
            JuBenService.errorLog.error(e.getMessage());
            JuBenService.errorLog.error(this, e);
        }
        final List<PlayerScenarioCity> psList = this.playerScenarioCityDao.getSCityByPidSid(playerId, ps.getScenarioId());
        juBenDto.juBenCityDtoMap = new HashMap<Integer, JuBenCityDto>();
        SoloCity soloCity = null;
        for (final PlayerScenarioCity psc : psList) {
            final JuBenCityDto jcd = new JuBenCityDto();
            jcd.cityId = psc.getCityId();
            soloCity = (SoloCity)this.soloCityCache.get((Object)psc.getCityId());
            jcd.cityName = soloCity.getName();
            jcd.forceId = psc.getForceId();
            jcd.vId = psc.getVId();
            jcd.state = 0;
            jcd.title = 0;
            if (jcd.trickDto == null) {
                jcd.trickDto = new HashMap<Integer, HashMap<String, TrickDto>>();
            }
            this.restoreTrickInfo(jcd.trickDto, psc.getTrickinfo());
            jcd.terrain = soloCity.getTerrain();
            jcd.terrianType = soloCity.getTerrainEffectType();
            jcd.update = false;
            juBenDto.juBenCityDtoMap.put(psc.getCityId(), jcd);
            final StringBuilder sb = new StringBuilder();
            sb.append(18).append("_").append(playerId).append("_").append(psc.getCityId());
            jcd.battleId = sb.toString();
            if (jcd.cityId == 121 && jcd.forceId == juBenDto.player_force_id) {
                juBenDto.royalJadeBelong = 0;
            }
        }
        for (final JuBenCityDto ci : juBenDto.juBenCityDtoMap.values()) {
            if (juBenDto.capital != ci.cityId) {
                if (juBenDto.npcCapital == ci.cityId) {
                    continue;
                }
                ci.title = this.getSurroundState(juBenDto, ci);
            }
        }
        JuBenManager.getInstance().putJuBen(juBenDto);
        this.jobService.addJob("juBenService", "juBenDeal", new StringBuilder(String.valueOf(playerId)).toString(), juBenDto.endTime, false);
        return juBenDto;
    }
    
    private void restoreTrickInfo(HashMap<Integer, HashMap<String, TrickDto>> trickDto, final String trickinfo) {
        if (trickDto == null) {
            trickDto = new HashMap<Integer, HashMap<String, TrickDto>>();
        }
        if (StringUtils.isBlank(trickinfo)) {
            return;
        }
        HashMap<String, TrickDto> map = null;
        final String[] single = trickinfo.split("#");
        String[] array;
        for (int length = (array = single).length, i = 0; i < length; ++i) {
            final String value = array[i];
            if (!StringUtils.isBlank(value)) {
                final String[] cell = value.split("-");
                if (cell.length >= 5) {
                    final int id = Integer.parseInt(cell[0]);
                    final Stratagem stratagem = (Stratagem)this.dataGetter.getStratagemCache().get((Object)id);
                    if (stratagem != null) {
                        final TrickDto dto = new TrickDto(cell, this.dataGetter);
                        map = trickDto.get(dto.getForceId());
                        if (map == null) {
                            map = new HashMap<String, TrickDto>();
                            map.put(dto.getType(), dto);
                            trickDto.put(dto.getForceId(), map);
                        }
                        else {
                            map.put(dto.getType(), dto);
                        }
                    }
                }
            }
        }
    }
    
    @Override
    public byte[] getJuBenReward(final PlayerDto playerDto) {
        final int playerId = playerDto.playerId;
        final JuBenDto juBenDto = JuBenManager.getInstance().getByPid(playerId);
        if (juBenDto == null || juBenDto.state == 1) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10012);
        }
        final JsonDocument doc2 = new JsonDocument();
        doc2.startObject();
        try {
            if (juBenDto.isWin) {
                doc2.createElement("target", juBenDto.target);
                doc2.createElement("win", juBenDto.isWin);
                doc2.createElement("curStar", juBenDto.star);
                doc2.createElement("maxStar", 5);
                final Map<Integer, SoloReward> map = this.soloRewardCache.getBySoloId(juBenDto.juBen_id, juBenDto.grade);
                final PlayerScenario playerScenario = this.playerScenarioDao.getScenarioByPidSid(playerId, juBenDto.juBen_id);
                int rewarded = playerScenario.getRewarded();
                final List<SoloReward> list = new ArrayList<SoloReward>();
                int pow = 0;
                int i;
                for (int start = i = (juBenDto.grade - 1) * 5; i < start + juBenDto.star; ++i) {
                    pow = (int)Math.pow(2.0, i);
                    if ((rewarded & pow) != pow || this.isWorldDrama(juBenDto.juBen_id)) {
                        list.add(map.get(i + 1 - start));
                        rewarded += pow;
                    }
                }
                final String[] openLv = playerScenario.getStarlv().split(",");
                final StringBuilder sb = new StringBuilder();
                int orgLv = 0;
                for (int j = 0; j < openLv.length; ++j) {
                    orgLv = Integer.valueOf(openLv[j]);
                    if (j + 1 == juBenDto.grade && orgLv < juBenDto.star) {
                        orgLv = juBenDto.star;
                    }
                    sb.append(orgLv).append(",");
                }
                if (juBenDto.star >= 4) {
                    final SoloDrama soloDrama = (SoloDrama)this.soloDramaCache.get((Object)juBenDto.juBen_id);
                    if (juBenDto.grade >= openLv.length && juBenDto.grade < soloDrama.getGrade()) {
                        sb.append(0).append(",");
                    }
                }
                long consumeTime = 0L;
                for (int k = 5; k > juBenDto.star; --k) {
                    final SoloReward sr = map.get(k);
                    consumeTime += sr.getReqTime();
                }
                final SoloReward srd = map.get(juBenDto.star);
                long curStrPassTime = srd.getReqTime() - (juBenDto.endTime - juBenDto.overTime) / 1000L;
                if (curStrPassTime < 0L) {
                    curStrPassTime = 0L;
                }
                consumeTime += curStrPassTime;
                doc2.createElement("consumeTime", consumeTime);
                doc2.startArray("rewards");
                final Map<Integer, Integer> totalRewardMap = new HashMap<Integer, Integer>();
                final List<Items> itemsList = new ArrayList<Items>();
                for (final SoloReward sr2 : list) {
                    doc2.startObject();
                    doc2.createElement("star", sr2.getStar());
                    doc2.startArray("res");
                    for (final Integer key : sr2.getRewardMap().keySet()) {
                        if (key == 43) {
                            final int itemId = sr2.getRewardMap().get(key);
                            final Items items = (Items)this.itemsCache.get((Object)itemId);
                            if (items.getChangeItemId() > 0) {
                                final Items changeItem = (Items)this.itemsCache.get((Object)items.getChangeItemId());
                                if (changeItem != null) {
                                    final int changeItemId = changeItem.getId();
                                    final int changeItemType = changeItem.getType();
                                    final EquipCoordinates ecd = this.equipCache.getEquipCoordinateByItemId(changeItem.getId());
                                    if (ecd != null) {
                                        final List<StoreHouse> shList = this.storeHouseDao.getByItemId(playerDto.playerId, changeItemId, 10);
                                        if (shList != null && shList.size() > 0) {
                                            continue;
                                        }
                                    }
                                    final EquipProset equipProset = this.equipCache.getEquipProsetByItemId(changeItemId);
                                    if (equipProset != null) {
                                        final List<StoreHouse> shList2 = this.storeHouseDao.getByItemId(playerId, changeItemId, 14);
                                        if (shList2 != null && shList2.size() > 0) {
                                            continue;
                                        }
                                    }
                                    final EquipProset canCompoundProset = this.equipCache.getProsetBySubSuitId(changeItemId);
                                    if (canCompoundProset != null) {
                                        final List<StoreHouse> shList3 = this.storeHouseDao.getByItemId(playerId, canCompoundProset.getItemId(), 14);
                                        if (shList3 != null && shList3.size() > 0) {
                                            continue;
                                        }
                                    }
                                    final List<StoreHouse> shList3 = this.storeHouseDao.getByItemId(playerDto.playerId, changeItemId, StoreHouseService.getStoreHouseType(changeItemType));
                                    if (shList3 != null && shList3.size() > 0) {
                                        continue;
                                    }
                                    final StoreHouseSell shs = this.storeHouseSellDao.getByItemId(playerDto.playerId, changeItemId, StoreHouseService.getStoreHouseType(changeItemType));
                                    if (shs != null) {
                                        continue;
                                    }
                                }
                            }
                            itemsList.add(items);
                            doc2.startObject();
                            doc2.createElement("type", items.getPic());
                            doc2.createElement("val", 1);
                            doc2.endObject();
                        }
                        else {
                            doc2.startObject();
                            doc2.createElement("type", key);
                            doc2.createElement("val", sr2.getRewardMap().get(key));
                            doc2.endObject();
                            if (totalRewardMap.containsKey(key)) {
                                totalRewardMap.put(key, totalRewardMap.get(key) + sr2.getRewardMap().get(key));
                            }
                            else {
                                totalRewardMap.put(key, sr2.getRewardMap().get(key));
                            }
                        }
                    }
                    doc2.endArray();
                    doc2.endObject();
                }
                doc2.endArray();
                this.playerScenarioDao.updateRewardStarLv(playerId, playerScenario.getScenarioId(), sb.toString(), rewarded);
                final List<ResourceDto> rewardList = new ArrayList<ResourceDto>();
                for (final Integer key2 : totalRewardMap.keySet()) {
                    final ResourceDto rd = new ResourceDto(key2, totalRewardMap.get(key2));
                    rewardList.add(rd);
                }
                if (totalRewardMap.containsKey(5)) {
                    this.playerService.updateExpAndPlayerLevel(playerId, totalRewardMap.get(5), "\u5267\u672c\u80dc\u5229\u589e\u52a0\u7ecf\u9a8c");
                }
                this.playerResourceDao.addResourceIgnoreMax(playerId, rewardList, "\u5267\u672c\u7ed3\u675f\u5956\u52b1", true);
                for (final Items items2 : itemsList) {
                    this.storeHouseService.gainItems(playerId, 1, items2.getId(), LocalMessages.T_LOG_ITEM_9, true);
                }
            }
            else {
                doc2.createElement("target", juBenDto.target);
                doc2.createElement("win", false);
            }
            doc2.createElement("reward", true);
            final int phantomExp = this.caculatePhantomCopperExp(playerId, juBenDto);
            this.dealWithWorldDrama(juBenDto, playerDto);
            this.clearJubBenOver(juBenDto, playerId);
            doc2.createElement("phantomExp", phantomExp);
            doc2.endObject();
            this.scenarioNpcDao.deleteAll(playerId);
        }
        catch (Exception e) {
            JuBenService.errorLog.error("JuBenService getJuBenReward playerId " + playerId, e);
        }
        JuBenManager.getInstance().clearByVid(playerId);
        final JsonDocument doc3 = new JsonDocument();
        doc3.startObject();
        doc3.createElement("inJuBen", false);
        doc3.endObject();
        Players.push(playerId, PushCommand.PUSH_UPDATE, doc3.toByte());
        return JsonBuilder.getJson(State.SUCCESS, doc2.toByte());
    }
    
    private void dealWithWorldDrama(final JuBenDto juBenDto, final PlayerDto playerDto) {
        try {
            final WdSjpDrama drama = this.dataGetter.getWdSjpDramaCache().getWorldDramaByDramaIdAndGrade(juBenDto.juBen_id, juBenDto.grade);
            if (drama != null) {
                final WdSjp wdSjp = (WdSjp)this.dataGetter.getWdSjpCache().get((Object)5);
                if (wdSjp != null) {
                    final PlayerBattleAttribute pba = this.dataGetter.getPlayerBattleAttributeDao().read(playerDto.playerId);
                    int todayCount = (pba == null) ? 0 : pba.getEventWorldDramaCountToday();
                    if (juBenDto.isWin) {
                        this.dataGetter.getPlayerBattleAttributeDao().addEventWorldDramaCountToday(playerDto.playerId);
                        final WorldDramaTimesCache cache = WorldDramaTimesCache.getInstatnce();
                        final int allTimes = cache.getTimesByPIDAndSIdAndGrade(playerDto.playerId, juBenDto.juBen_id, juBenDto.grade);
                        final int updateResult = cache.updateTimes(playerDto.playerId, drama.getDramaId(), drama.getDifficulty(), allTimes + 1);
                        if (updateResult <= 0) {
                            JuBenService.errorLog.error("cache updateTimes return 0..playerId:" + playerDto.playerId + " dramaId:" + drama.getDramaId() + " grade:" + drama.getDifficulty() + " times:" + allTimes + 1);
                        }
                        todayCount += ((updateResult > 0) ? 1 : 0);
                        final int openTech = drama.getOpenTech();
                        final List<WdSjpDrama> soloIdList = this.dataGetter.getWdSjpDramaCache().getDramaListByOpenTech(openTech);
                        if (soloIdList == null || soloIdList.isEmpty()) {
                            JuBenService.errorLog.error("dealWithWorldDrama ..OpenTechDramasList is empty. openTech:" + openTech);
                            return;
                        }
                        boolean isThisTechDramaOver = true;
                        for (final WdSjpDrama singleDrama : soloIdList) {
                            final int useTimes = cache.getTimesByPIDAndSIdAndGrade(playerDto.playerId, singleDrama.getDramaId(), singleDrama.getDifficulty());
                            final int maxTimes = singleDrama.getNumMax();
                            if (useTimes < maxTimes) {
                                isThisTechDramaOver = false;
                                break;
                            }
                        }
                        final int techIndex = this.dataGetter.getWdSjpDramaCache().getTechIndexByTechId(openTech);
                        if (isThisTechDramaOver) {
                            Players.push(playerDto.playerId, PushCommand.PUSH_WORLD_DRAMA, JsonBuilder.getSimpleJson("worldDramaGrade", techIndex));
                        }
                        JuBenService.errorLog.error(LogUtil.formatWorldDramaLog(playerDto, juBenDto, juBenDto.star));
                    }
                    if (todayCount >= wdSjp.getNumMax()) {
                        final String msg = LocalMessages.JUBEN_TODAY_COUNT_IS_ZERO;
                        this.dataGetter.getChatService().sendSystemChat("SYS2ONE", playerDto.playerId, playerDto.forceId, msg, null);
                    }
                    else {
                        this.dataGetter.getCityService().addPlayerNextEvent(playerDto.playerId, playerDto.forceId, 5, false);
                    }
                }
                else {
                    JuBenService.errorLog.error("dealWithWorldDrama wdsjp is null...");
                }
            }
        }
        catch (Exception e) {
            JuBenService.errorLog.error(this, e);
        }
    }
    
    private String getMiniWinStr(final int soId, final int grade) {
        final StringBuilder sb = new StringBuilder();
        sb.append(soId).append("_").append(grade);
        return sb.toString();
    }
    
    @Transactional
    @Override
    public void juBenOver(final int playerId, final JuBenDto juBenDto, final boolean win) {
        juBenDto.state = 0;
        juBenDto.isWin = win;
        juBenDto.overTime = System.currentTimeMillis();
        this.sendOverDialog(juBenDto, playerId, win);
        if (win && juBenDto.star == 5) {
            final Map<Integer, SoloReward> map = this.soloRewardCache.getBySoloId(juBenDto.juBen_id, juBenDto.grade);
            long consumeTime = 0L;
            for (int i = 5; i > juBenDto.star; --i) {
                final SoloReward sr = map.get(i);
                consumeTime += sr.getReqTime();
            }
            final SoloReward srd = map.get(juBenDto.star);
            long curStrPassTime = srd.getReqTime() - (juBenDto.endTime - juBenDto.overTime) / 1000L;
            if (curStrPassTime < 0L) {
                curStrPassTime = 0L;
            }
            consumeTime += curStrPassTime;
            final String str = this.getMiniWinStr(juBenDto.juBen_id, juBenDto.grade);
            if (JuBenService.miniWinTime.get(str) == null || JuBenService.miniWinTime.get(str).miniTime > consumeTime) {
                final MiniTimeWinInfo mtwi = new MiniTimeWinInfo();
                final Player player = this.playerDao.read(playerId);
                mtwi.forceId = player.getForceId();
                mtwi.name = player.getPlayerName();
                mtwi.miniTime = consumeTime;
                if (JuBenService.miniWinTime.get(str) == null) {
                    JuBenService.miniWinTime.put(str, mtwi);
                    final ScenarioInfo scenarioInfo = new ScenarioInfo();
                    scenarioInfo.setGrade(juBenDto.grade);
                    scenarioInfo.setSoloId(juBenDto.juBen_id);
                    scenarioInfo.setMinTime((int)consumeTime);
                    scenarioInfo.setPlayerId(playerId);
                    scenarioInfo.setForceId(player.getForceId());
                    scenarioInfo.setPlayerName(player.getPlayerName());
                    this.scenarioInfoDao.create(scenarioInfo);
                }
                else {
                    JuBenService.miniWinTime.put(str, mtwi);
                    this.scenarioInfoDao.updateMinTime(juBenDto.juBen_id, juBenDto.grade, (int)consumeTime, playerId, player.getForceId(), player.getPlayerName());
                }
                if (this.dataGetter.getWdSjpDramaCache().getWorldDramaByDramaId(juBenDto.juBen_id) == null) {
                    final String msg = MessageFormatter.format(LocalMessages.JUBEN_WIN_MIN_TIME, new Object[] { ColorUtil.getSpecialColorMsg(player.getPlayerName()), juBenDto.title, this.getGradeStr(juBenDto.grade) });
                    this.dataGetter.getChatService().sendSystemChat("GLOBAL", 0, 0, msg, null);
                }
            }
        }
        this.playerScenarioDao.updateStateOverTime(playerId, juBenDto.juBen_id, 0, System.currentTimeMillis());
        final JsonDocument doc1 = new JsonDocument();
        doc1.startObject();
        doc1.createElement("juBenOver", true);
        doc1.endObject();
        Players.push(playerId, PushCommand.PUSH_UPDATE, doc1.toByte());
    }
    
    private void sendOverDialog(final JuBenDto juBenDto, final int playerId, final boolean win) {
        if (juBenDto.juBen_id == 10001) {
            if (win) {
                ScenarioEventJsonBuilder.sendDialog(playerId, 10001003, null);
            }
        }
        else if (juBenDto.juBen_id == 10002) {
            ScenarioEventJsonBuilder.sendDialog(playerId, 10002002, null);
        }
        else if (juBenDto.juBen_id == 10003) {
            ScenarioEventJsonBuilder.sendDialog(playerId, 10003001, null);
        }
        else if (juBenDto.juBen_id == 10004) {
            ScenarioEventJsonBuilder.sendDialog(playerId, 10004001, null);
        }
        else if (juBenDto.juBen_id == 10009) {
            ScenarioEventJsonBuilder.sendDialog(playerId, 10009001, null);
        }
        else if (juBenDto.juBen_id == 11 && win) {
            ScenarioEventJsonBuilder.sendDialog(playerId, 11003, null);
        }
    }
    
    private String getGradeStr(final int grade) {
        if (grade == 1) {
            return LocalMessages.JUBEN_GRADE_1;
        }
        if (grade == 2) {
            return LocalMessages.JUBEN_GRADE_2;
        }
        if (grade == 3) {
            return LocalMessages.JUBEN_GRADE_3;
        }
        if (grade == 4) {
            return LocalMessages.JUBEN_GRADE_4;
        }
        if (grade == 5) {
            return LocalMessages.JUBEN_GRADE_5;
        }
        return LocalMessages.JUBEN_GRADE_1;
    }
    
    private void clearOneSideOfBattle(final Player player, final JuBenCityDto juBenCityDto, final Battle battle, final boolean playerSide) {
        boolean clearAttSide = false;
        if ((playerSide && battle.getAttBaseInfo().getForceId() == player.getForceId()) || (!playerSide && battle.getAttBaseInfo().getForceId() != player.getForceId())) {
            clearAttSide = true;
        }
        else if ((playerSide && battle.getDefBaseInfo().getForceId() == player.getForceId()) || (!playerSide && battle.getDefBaseInfo().getForceId() != player.getForceId())) {
            clearAttSide = false;
        }
        else {
            ErrorSceneLog.getInstance().appendErrorMsg("battle forceId error. AttSide will be cleared").appendBattleId(battle.getBattleId()).appendPlayerId(player.getPlayerId()).appendPlayerName(player.getPlayerName()).append("player forceId", player.getForceId()).append("playerSide", playerSide).append("Att ForceId", battle.getAttBaseInfo().getForceId()).append("Def ForceId", battle.getDefBaseInfo().getForceId()).appendClassName("JuBenService").appendMethodName("clearOneSideOfBattle").flush();
            clearAttSide = true;
        }
        if (clearAttSide) {
            synchronized (battle.getBattleId()) {
                for (final CampArmy temp : battle.getAttCamp()) {
                    if (temp.getPlayerId() > 0 && !temp.isPhantom()) {
                        NewBattleManager.getInstance().quitBattle(battle, temp.getPlayerId(), temp.getGeneralId());
                    }
                    temp.setArmyHpLoss(temp.getArmyHp());
                    temp.setArmyHp(0);
                }
                battle.getAttBaseInfo().setNum(0);
                battle.getAttList().clear();
            }
            // monitorexit(battle.getBattleId())
            final boolean delSucc = BattleScheduler.getInstance().removeBattle(battle);
            if (delSucc) {
                battle.doBattle(this.dataGetter, battle.getStartTime());
            }
            else {
                ErrorSceneLog.getInstance().appendErrorMsg("removeBattle battle fail").appendBattleId(battle.getBattleId()).appendPlayerId(player.getPlayerId()).appendPlayerName(player.getPlayerName()).appendClassName("JuBenService").appendMethodName("clearOneSideOfBattle").flush();
            }
        }
        else {
            synchronized (battle.getBattleId()) {
                for (final CampArmy temp : battle.getDefCamp()) {
                    if (temp.getPlayerId() > 0 && !temp.isPhantom()) {
                        NewBattleManager.getInstance().quitBattle(battle, temp.getPlayerId(), temp.getGeneralId());
                    }
                    temp.setArmyHpLoss(temp.getArmyHp());
                    temp.setArmyHp(0);
                }
                battle.getDefBaseInfo().setNum(0);
                battle.getDefList().clear();
            }
            // monitorexit(battle.getBattleId())
            final boolean delSucc = BattleScheduler.getInstance().removeBattle(battle);
            if (delSucc) {
                battle.doBattle(this.dataGetter, battle.getStartTime());
            }
            else {
                ErrorSceneLog.getInstance().appendErrorMsg("removeBattle battle fail").appendBattleId(battle.getBattleId()).appendPlayerId(player.getPlayerId()).appendPlayerName(player.getPlayerName()).appendClassName("JuBenService").appendMethodName("clearOneSideOfBattle").flush();
            }
        }
    }
    
    private int caculatePhantomCopperExp(final int playerId, final JuBenDto juBenDto) {
        if (juBenDto != null && !this.isInWorldDrama(playerId)) {
            return 0;
        }
        AddExpInfo expResult = null;
        try {
            double copperSum = 0.0;
            double chiefExpSum = 0.0;
            final Map<Integer, Double> gExpMap = new HashMap<Integer, Double>();
            for (final JuBenCityDto juBenCityDto : juBenDto.juBenCityDtoMap.values()) {
                final int cityId = juBenCityDto.cityId;
                final List<ScenarioNpc> list = this.dataGetter.getScenarioNpcDao().getByPlayerIdLocationId(playerId, cityId);
                for (final ScenarioNpc scenarioNpc : list) {
                    if (scenarioNpc.getNpcType() == 1) {
                        final PlayerGeneralMilitary pgm = this.dataGetter.getPlayerGeneralMilitaryDao().read(scenarioNpc.getArmyId());
                        if (pgm == null) {
                            continue;
                        }
                        final int generalId = pgm.getGeneralId();
                        final General general = (General)this.dataGetter.getGeneralCache().get((Object)generalId);
                        final Troop troop = this.dataGetter.getTroopCache().getTroop(general.getTroop(), playerId);
                        final int troopId = troop.getId();
                        final double troopFoodConsumeCoe = ((TroopConscribe)this.dataGetter.getTroopConscribeCache().get((Object)troopId)).getFood();
                        final int hp = scenarioNpc.getHp();
                        final double troopFood = troopFoodConsumeCoe * hp;
                        final double delta = troopFood * 0.28;
                        copperSum += delta;
                        final double attTechAddGZJY = this.dataGetter.getTechEffectCache().getTechEffect(playerId, 40) / 100.0;
                        chiefExpSum += (1.0 + attTechAddGZJY) * delta;
                        final double gExpAdd = this.dataGetter.getTechEffectCache().getTechEffect(playerId, 16) / 100.0;
                        final double gExp = (1.0 + gExpAdd) * delta;
                        final Double gExpSum = gExpMap.get(generalId);
                        if (gExpSum == null) {
                            gExpMap.put(generalId, gExp);
                        }
                        else {
                            gExpMap.put(generalId, gExpSum + gExp);
                        }
                    }
                }
            }
            if (copperSum == 0.0 && chiefExpSum == 0.0 && gExpMap.size() == 0) {
                return 0;
            }
            if (copperSum > 0.0) {
                this.dataGetter.getPlayerResourceDao().addCopperIgnoreMax(playerId, copperSum, "\u5267\u672c\u7ed3\u675f\uff0c\u6298\u7b97\u5e7b\u5f71\u83b7\u5f97\u94f6\u5e01", true);
            }
            if (chiefExpSum > 0.0) {
                expResult = this.dataGetter.getPlayerService().updateExpAndPlayerLevel(playerId, (int)chiefExpSum, "\u5267\u672c\u7ed3\u675f\uff0c\u6298\u7b97\u5e7b\u5f71\u589e\u52a0\u7ecf\u9a8c");
            }
            final StringBuilder content = new StringBuilder();
            String jubenName = LocalMessages.DANREN_JUEBN_NAME;
            if (this.isWorldDrama(juBenDto.juBen_id)) {
                jubenName = LocalMessages.WORLD_DRAMA_NAME;
            }
            content.append(MessageFormatter.format(LocalMessages.JIEBING_FANHUAN_FORMAT_PART1, new Object[] { jubenName, (int)copperSum, expResult.addExp }));
            for (final Map.Entry<Integer, Double> entry : gExpMap.entrySet()) {
                final int gId = entry.getKey();
                final int gExp2 = (int)(Object)entry.getValue();
                if (gExp2 > 0) {
                    final List<UpdateExp> gExpUpList = this.dataGetter.getGeneralService().updateExpAndGeneralLevel(playerId, gId, gExp2);
                    if (gExpUpList == null) {
                        continue;
                    }
                    int addGExp = 0;
                    for (final UpdateExp ue : gExpUpList) {
                        addGExp += (int)ue.getCurExp();
                    }
                    final String gName = ((General)this.dataGetter.getGeneralCache().get((Object)gId)).getName();
                    content.append(MessageFormatter.format(LocalMessages.JIEBING_FANHUAN_FORMAT_PART2, new Object[] { gName, addGExp }));
                }
            }
            this.dataGetter.getMailService().writeSystemMail(LocalMessages.T_MAIL_ROLE_SYSTEM, LocalMessages.JIEBING_FANHUAN_TITLE, content.toString(), 1, playerId, new Date());
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("JuBenService.caculatePhantomCopperExp Exception", e);
        }
        return (expResult == null) ? 0 : expResult.addExp;
    }
    
    @Override
    public void juBenDeal(final String param) {
        final int playerId = Integer.valueOf(param);
        final JuBenDto juBenDto = JuBenManager.getInstance().getByPid(Integer.valueOf(param));
        if (juBenDto != null) {
            synchronized (juBenDto) {
                if (juBenDto.state == 0) {
                    // monitorexit(juBenDto)
                    return;
                }
                if (juBenDto.star <= 1) {
                    this.juBenOver(playerId, juBenDto, false);
                }
                else if (System.currentTimeMillis() >= juBenDto.endTime) {
                    --juBenDto.star;
                    juBenDto.addTime = 0L;
                    final Map<Integer, SoloReward> srMap = this.soloRewardCache.getBySoloId(juBenDto.juBen_id, juBenDto.grade);
                    final long addTime = srMap.get(juBenDto.star).getReqTime();
                    juBenDto.endTime += addTime * 1000L;
                    this.playerScenarioDao.updateStar(playerId, juBenDto.juBen_id, juBenDto.star, new Date(juBenDto.endTime));
                    this.jobService.addJob("juBenService", "juBenDeal", new StringBuilder(String.valueOf(playerId)).toString(), juBenDto.endTime, false);
                    final JsonDocument doc = new JsonDocument();
                    doc.startObject();
                    doc.createElement("title", juBenDto.title);
                    final long time = juBenDto.endTime - System.currentTimeMillis();
                    doc.createElement("time", (time > 0L) ? time : 0L);
                    doc.createElement("addTime", addTime);
                    doc.createElement("addStar", (-1));
                    doc.createElement("grade", juBenDto.grade);
                    doc.createElement("star", juBenDto.star);
                    doc.createElement("target", juBenDto.target);
                    doc.endObject();
                    Players.push(playerId, PushCommand.PUSH_JUBEN_INFO, doc.toByte());
                }
            }
            // monitorexit(juBenDto)
        }
    }
    
    @Transactional
    @Override
    public void addEndTime(final int playerId, long addTime) {
        final JuBenDto juBenDto = JuBenManager.getInstance().getByPid(playerId);
        if (juBenDto == null) {
            return;
        }
        final Date date = new Date();
        final Map<Integer, SoloReward> srMap = this.soloRewardCache.getBySoloId(juBenDto.juBen_id, juBenDto.grade);
        long curStarMaxTime = srMap.get(juBenDto.star).getReqTime() * 1000L;
        final long curStarLeftTime = juBenDto.endTime - date.getTime();
        long curStarPassTime = curStarMaxTime - curStarLeftTime;
        final long addTimeCopy = addTime;
        long endTime = 0L;
        int addStar = 0;
        while (true) {
            while (juBenDto.star < 5) {
                if (addTime > curStarPassTime) {
                    final JuBenDto juBenDto2 = juBenDto;
                    ++juBenDto2.star;
                    ++addStar;
                    addTime -= curStarPassTime;
                    endTime = date.getTime() + curStarMaxTime;
                    curStarMaxTime = (curStarPassTime = srMap.get(juBenDto.star).getReqTime() * 1000L);
                    juBenDto.endTime = date.getTime();
                    if (addTime > 0L) {
                        continue;
                    }
                }
                else {
                    endTime = juBenDto.endTime + addTime;
                    addTime = 0L;
                }
                juBenDto.endTime = endTime;
                this.playerScenarioDao.updateEndTimeCurStar(playerId, juBenDto.juBen_id, juBenDto.star, new Date(juBenDto.endTime));
                this.jobService.addJob("juBenService", "juBenDeal", new StringBuilder(String.valueOf(playerId)).toString(), juBenDto.endTime, false);
                final JsonDocument doc = new JsonDocument();
                doc.startObject();
                doc.createElement("title", juBenDto.title);
                final long time = juBenDto.endTime - System.currentTimeMillis();
                doc.createElement("time", (time > 0L) ? time : 0L);
                doc.createElement("addTime", addTimeCopy / 1000L);
                doc.createElement("addStar", addStar);
                doc.createElement("grade", juBenDto.grade);
                doc.createElement("star", juBenDto.star);
                doc.createElement("target", juBenDto.target);
                doc.endObject();
                Players.push(playerId, PushCommand.PUSH_JUBEN_INFO, doc.toByte());
                return;
            }
            final long maxTime = date.getTime() + srMap.get(5).getReqTime() * 1000L;
            endTime = juBenDto.endTime + addTime;
            if (endTime > maxTime) {
                endTime = maxTime;
            }
            addTime = 0L;
            continue;
        }
    }
    
    @Transactional
    @Override
    public byte[] enterJuBenQuick(final PlayerDto playerDto, final Request request) {
        final int playerId = playerDto.playerId;
        final JuBenDto juBenDto = JuBenManager.getInstance().getByPid(playerId);
        if (juBenDto != null) {
            return this.enterJuBenScene(playerDto, juBenDto.juBen_id, juBenDto.grade, 0, request);
        }
        return JsonBuilder.getJson(State.FAIL, LocalMessages.JUBEN_CANNT_QUIT);
    }
    
    @Transactional
    @Override
    public byte[] juBenPermit(final PlayerDto playerDto, final int sId, final int grade, final int create, final Request request) {
        final int playerId = playerDto.playerId;
        final JuBenDto juBenDto = JuBenManager.getInstance().getByPid(playerId);
        if (juBenDto != null) {
            return JsonBuilder.getJson(State.SUCCESS, "");
        }
        final List<PlayerGeneralMilitary> pgmList = this.playerGeneralMilitaryDao.getMilitaryList(playerId);
        final PlayerScenario ps = this.playerScenarioDao.getScenarioByPidSid(playerId, sId);
        if (ps == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10020);
        }
        if (ps != null && ps.getState() == 0 && create == 0) {
            final JsonDocument doc1 = new JsonDocument();
            doc1.startObject();
            doc1.createElement("inJuBen", false);
            doc1.endObject();
            Players.push(playerId, PushCommand.PUSH_UPDATE, doc1.toByte());
            return JsonBuilder.getJson(State.FAIL, "");
        }
        final String[] starLvs = ps.getStarlv().split(",");
        if (grade - 1 > 0 && Integer.valueOf(starLvs[grade - 2]) < 4) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.JUBEN_NOT_OPEN_JUBEN_LV);
        }
        final boolean canAtt = true;
        if (!canAtt) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10012);
        }
        final Map<Integer, SoloReward> soloReward = this.soloRewardCache.getBySoloId(sId, grade);
        final SoloReward reward = soloReward.get(1);
        if (reward != null) {
            final int food = reward.getReqFood();
            final PlayerResource playerResource = this.playerResourceDao.read(playerId);
            if (playerResource != null && playerResource.getFood() < food) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10027);
            }
        }
        final boolean isSoloDrama = !this.isInWorldDrama(playerId);
        for (final PlayerGeneralMilitary pgm : pgmList) {
            if (pgm.getState() > 1) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.JUBEN_CANNT_JOIN_GENREAL_BUSY);
            }
            final GeneralMoveDto generalMoveDto = CityService.getGeneralMoveDto(playerId, pgm.getGeneralId());
            if (generalMoveDto != null) {
                final int cityState = generalMoveDto.cityState;
            }
            final GeneralMoveDto gmd = CityService.getGeneralMoveDto(pgm.getPlayerId(), pgm.getGeneralId());
            if (gmd != null && (gmd.cityState == 22 || gmd.cityState == 23)) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.GENERAL_IN_CELL);
            }
            final int forcesMax = this.battleDataCache.getMaxHp(pgm);
            if (isSoloDrama && pgm.getForces() < forcesMax) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.JUBEN_FORCES_NOT_FULL);
            }
        }
        final Set<Team> teamSet = TeamManager.getInstance().getJoinTeam(playerId);
        final Set<Integer> set = new HashSet<Integer>();
        if (teamSet != null) {
            for (final Team team : teamSet) {
                if (team.getMemberMap().get(playerId) != null) {
                    final List<GeneralInfo> giList = team.getMemberMap().get(playerId).getGeneralInfo();
                    if (giList == null || giList.size() <= 0) {
                        continue;
                    }
                    for (final GeneralInfo gi : giList) {
                        set.add(gi.getGeneralId());
                    }
                }
            }
        }
        if (set.size() > 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.JUBEN_CANNT_JOIN_GENREAL_BUSY);
        }
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Transactional
    @Override
    public byte[] getJuBenScene(final PlayerDto playerDto, final int sId, final int grade, final Request request) {
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        final SoloDrama soloDrama = (SoloDrama)this.soloDramaCache.get((Object)sId);
        if (soloDrama == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        doc.createElement("sId", sId);
        doc.createElement("title", soloDrama.getName());
        final Map<Integer, SoloReward> srMap = this.soloRewardCache.getBySoloId(sId, grade);
        final long time = srMap.get(5).getReqTime() * 1000;
        doc.createElement("time", (time > 0L) ? time : 0L);
        doc.createElement("grade", grade);
        doc.createElement("target", soloDrama.getPlot());
        doc.startArray("cities");
        final Map<Integer, SoloCity> map = this.soloCityCache.getBySoloId(sId);
        int npcCapital = 0;
        int myCapital = 0;
        for (final SoloCity bcd : map.values()) {
            if (bcd.getCapital() == 1) {
                if (bcd.getBelong() == 1) {
                    npcCapital = bcd.getId();
                }
                else {
                    myCapital = bcd.getId();
                }
            }
            doc.startObject();
            doc.createElement("cityId", bcd.getId());
            doc.createElement("name", bcd.getName());
            final int belong = bcd.getBelong();
            doc.createElement("forceId", (belong == 0) ? playerDto.forceId : ((belong >= 101) ? belong : 0));
            doc.createElement("terrain", bcd.getTerrain());
            doc.endObject();
        }
        doc.endArray();
        doc.createElement("capital", myCapital);
        doc.createElement("targetCityId", npcCapital);
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Transactional
    @Override
    public byte[] enterJuBenScene(final PlayerDto playerDto, final int sId, final int grade, final int create, final Request request) {
        final int playerId = playerDto.playerId;
        List<PlayerGeneralMilitary> pgmList = this.playerGeneralMilitaryDao.getMilitaryList(playerId);
        if (this.isWorldDrama(sId)) {
            final WdSjp wdSjp = (WdSjp)this.dataGetter.getWdSjpCache().get((Object)5);
            final int perDayMax = wdSjp.getNumMax();
            final PlayerBattleAttribute pba = this.dataGetter.getPlayerBattleAttributeDao().read(playerDto.playerId);
            final int todayCount = pba.getEventWorldDramaCountToday();
            if (todayCount >= perDayMax) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.PLAYER_EVENT_REACH_COUNT_LIMIT);
            }
            final WorldDramaTimesCache cache = WorldDramaTimesCache.getInstatnce();
            final WdSjpDrama worldDrama = this.dataGetter.getWdSjpDramaCache().getWorldDramaByDramaIdAndGrade(sId, grade);
            if (worldDrama == null) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
            }
            final int allTimes = cache.getTimesByPIDAndSIdAndGrade(playerDto.playerId, worldDrama.getDramaId(), worldDrama.getDifficulty());
            if (allTimes >= worldDrama.getNumMax()) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.PLAYER_EVENT_REACH_COUNT_LIMIT);
            }
        }
        JuBenDto juBenDto = JuBenManager.getInstance().getByPid(playerId);
        boolean isFirst = false;
        if (juBenDto == null) {
            isFirst = true;
            PlayerScenario ps = this.playerScenarioDao.getScenarioByPidSid(playerId, sId);
            if (ps == null) {
                final boolean open = false;
                if (!open) {
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10020);
                }
                ps = this.playerScenarioDao.getScenarioByPidSid(playerId, sId);
            }
            if (ps != null && ps.getState() == 0 && create == 0) {
                final JsonDocument doc1 = new JsonDocument();
                doc1.startObject();
                doc1.createElement("inJuBen", false);
                doc1.endObject();
                Players.push(playerId, PushCommand.PUSH_UPDATE, doc1.toByte());
                return JsonBuilder.getJson(State.FAIL, "");
            }
            final boolean canAtt = true;
            if (!canAtt) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10012);
            }
            final WdSjpDrama drama = this.wdSjpDramaCache.getWorldDramaByDramaId(sId);
            final int locationId = WorldCityCommon.nationMainCityIdMap.get(playerDto.forceId);
            int jubenLoId = 0;
            final Map<Integer, SoloCity> map = this.soloCityCache.getBySoloId(sId);
            for (final SoloCity sc : map.values()) {
                if (sc.getCapital() == 1 && sc.getBelong() == 0) {
                    jubenLoId = sc.getId();
                }
            }
            final boolean isSoloDrama = !this.isInWorldDrama(playerId);
            for (final PlayerGeneralMilitary pgm : pgmList) {
                if (pgm.getState() > 1) {
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.JUBEN_CANNT_JOIN_GENREAL_BUSY);
                }
                final int forcesMax = this.battleDataCache.getMaxHp(pgm);
                if (isSoloDrama && pgm.getForces() < forcesMax) {
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.JUBEN_FORCES_NOT_FULL);
                }
            }
            final Map<Integer, SoloReward> soloReward = this.soloRewardCache.getBySoloId(sId, grade);
            if (soloReward != null) {
                final SoloReward reward = soloReward.get(1);
                final int food = reward.getReqFood();
                if (!this.playerResourceDao.consumeFood(playerId, food, "\u8fdb\u5165\u666e\u901a\u5267\u672c\u6d88\u8017\u7cae\u98df")) {
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10027);
                }
            }
            final Set<Team> teamSet = TeamManager.getInstance().getJoinTeam(playerId);
            final Set<Integer> set = new HashSet<Integer>();
            if (teamSet != null) {
                for (final Team team : teamSet) {
                    if (team.getMemberMap().get(playerId) != null) {
                        final List<GeneralInfo> giList = team.getMemberMap().get(playerId).getGeneralInfo();
                        if (giList == null || giList.size() <= 0) {
                            continue;
                        }
                        for (final GeneralInfo gi : giList) {
                            set.add(gi.getGeneralId());
                        }
                    }
                }
            }
            if (set.size() > 0) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.JUBEN_CANNT_JOIN_GENREAL_BUSY);
            }
            if (ps.getState() == 0) {
                final String[] starLvs = ps.getStarlv().split(",");
                if (drama == null && grade - 1 > 0 && Integer.valueOf(starLvs[grade - 2]) < 4) {
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.JUBEN_NOT_OPEN_JUBEN_LV);
                }
                final int res = this.playerGeneralMilitaryDao.updateJuBenLocation(playerId, locationId, jubenLoId);
                if (res != pgmList.size()) {
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.JUBEN_CANNT_JOIN_GENREAL_BUSY);
                }
                this.updateJuBen(playerId, sId, grade);
                this.initJuBenNpcs(playerDto, sId, grade);
            }
            this.generalService.sendGeneralMilitaryList(playerId);
            pgmList = this.playerGeneralMilitaryDao.getMilitaryList(playerId);
            final JsonDocument doc2 = new JsonDocument();
            doc2.startObject();
            doc2.createElement("inJuBen", true);
            doc2.endObject();
            Players.push(playerId, PushCommand.PUSH_UPDATE, doc2.toByte());
            juBenDto = this.cacheJuBen(playerId, sId);
        }
        final JsonDocument doc3 = new JsonDocument();
        doc3.startObject();
        doc3.createElement("title", juBenDto.title);
        final long time = juBenDto.endTime - System.currentTimeMillis();
        doc3.createElement("time", (time > 0L) ? time : 0L);
        doc3.createElement("grade", juBenDto.grade);
        doc3.createElement("capital", juBenDto.capital);
        doc3.createElement("star", juBenDto.star);
        doc3.createElement("target", juBenDto.target);
        doc3.createElement("targetCityId", juBenDto.npcCapital);
        doc3.createElement("state", juBenDto.state);
        if (juBenDto.state == 0) {
            doc3.createElement("win", juBenDto.isWin);
        }
        doc3.startArray("cities");
        int canAtt2 = 0;
        for (final JuBenCityDto bcd : juBenDto.juBenCityDtoMap.values()) {
            doc3.startObject();
            doc3.createElement("cityId", bcd.cityId);
            doc3.createElement("name", bcd.cityName);
            doc3.createElement("forceId", bcd.forceId);
            doc3.createElement("state", bcd.state);
            doc3.createElement("terrain", bcd.terrain);
            if (juBenDto.capital == bcd.cityId) {
                doc3.createElement("nameId", 0);
            }
            else {
                doc3.createElement("nameId", bcd.title);
            }
            this.getTrickInfo(doc3, bcd.trickDto, playerDto.forceId, bcd.cityId);
            canAtt2 = 0;
            if (bcd.forceId != playerDto.forceId) {
                final Set<Integer> roadSet = this.soloRoadCache.getNeighbors(bcd.cityId);
                for (final Integer key : roadSet) {
                    if (juBenDto.juBenCityDtoMap.get(key).forceId == playerDto.forceId) {
                        canAtt2 = 1;
                        break;
                    }
                }
            }
            doc3.createElement("canAtt", canAtt2);
            if (juBenDto.cityChangeMap != null) {
                final Integer cityType = juBenDto.cityChangeMap.get(bcd.cityId);
                if (cityType != null) {
                    doc3.createElement("newAppearanceType", cityType);
                }
            }
            doc3.endObject();
        }
        doc3.endArray();
        final List<PlayerGeneralCivil> pgcList = this.playerGeneralCivilDao.getCivilListOrderByLv(playerId);
        doc3.startArray("stratagem");
        for (final PlayerGeneralCivil pgc : pgcList) {
            final General general = (General)this.generalCache.get((Object)pgc.getGeneralId());
            final Stratagem stratagem = (Stratagem)this.stratagemCache.get((Object)general.getStratagemId());
            if (stratagem == null) {
                continue;
            }
            doc3.startObject();
            TrickFactory.getTrickInfo(doc3, stratagem);
            doc3.createElement("stratagemId", stratagem.getId());
            doc3.createElement("cilvilId", general.getId());
            doc3.createElement("pic", general.getPic());
            if (pgc.getCd() != null) {
                final long cd = pgc.getCd().getTime() - new Date().getTime();
                doc3.createElement("cd", (cd > 0L) ? cd : 0L);
            }
            doc3.createElement("stratagemIntro", stratagem.getIntro());
            doc3.endObject();
        }
        doc3.endArray();
        final long curTime = System.currentTimeMillis();
        doc3.startArray("generalPaths");
        double percentPath = 0.0;
        for (final PlayerGeneralMilitary pgm2 : pgmList) {
            final GeneralMoveDto gmd = CityService.getGeneralMoveDto(playerId, pgm2.getGeneralId());
            if (gmd != null && gmd.nextMoveTime > curTime && gmd.type == 2) {
                doc3.startObject();
                doc3.createElement("generalId", pgm2.getGeneralId());
                doc3.createElement("startCityId", gmd.startCityId);
                percentPath = 0.0;
                if (gmd.nextMoveTime > curTime) {
                    percentPath = (curTime - gmd.startMoveTime) * 1.0 / (gmd.nextMoveTime - gmd.startMoveTime);
                    percentPath = Math.ceil(percentPath * 100.0) / 100.0;
                }
                doc3.createElement("percent", percentPath);
                doc3.appendJson(this.getMovePath(pgm2.getJubenLoId(), gmd.moveLine));
                doc3.endObject();
            }
            else {
                doc3.startObject();
                doc3.createElement("generalId", pgm2.getGeneralId());
                doc3.createElement("startCityId", pgm2.getJubenLoId());
                doc3.endObject();
            }
        }
        doc3.endArray();
        if (isFirst) {
            this.sendFirstEnterJubenDialog(doc3, juBenDto, playerId);
        }
        if (juBenDto.mengdeLocation > 0) {
            doc3.createElement("mengdeLocation", juBenDto.mengdeLocation);
        }
        doc3.createElement("isSafe", juBenDto.isMengdeSafe);
        this.scenarioEventManager.getPlayerScenarioEventInfo(playerId, doc3);
        doc3.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc3.toByte());
    }
    
    private void sendFirstEnterJubenDialog(final JsonDocument doc, final JuBenDto juBenDto, final int playerId) {
        if (juBenDto.juBen_id == 1) {
            doc.createElement("dialogId", 1);
        }
        else if (juBenDto.juBen_id == 9) {
            if (juBenDto.grade == 1) {
                ScenarioEventJsonBuilder.sendDialog(playerId, 9006, null);
            }
            else if (juBenDto.grade == 2 || juBenDto.grade == 3) {
                ScenarioEventJsonBuilder.sendDialog(playerId, 9007, null);
            }
            else if (juBenDto.grade == 4) {
                ScenarioEventJsonBuilder.sendDialog(playerId, 9008, null);
            }
            else if (juBenDto.grade == 5) {
                ScenarioEventJsonBuilder.sendDialog(playerId, 9009, null);
            }
        }
        else if (juBenDto.juBen_id == 11 && juBenDto.grade == 1) {
            ScenarioEventJsonBuilder.sendDialog(playerId, 11002, null);
        }
    }
    
    private void getTrickInfo(final JsonDocument doc, final HashMap<Integer, HashMap<String, TrickDto>> trickDto, final int forceId, final int cityId) {
        if (trickDto == null) {
            return;
        }
        doc.createElement("cityId", cityId);
        doc.startArray("trickState");
        for (final Integer index : trickDto.keySet()) {
            final HashMap<String, TrickDto> map = trickDto.get(index);
            if (map == null) {
                continue;
            }
            for (final String type : map.keySet()) {
                final TrickDto dto = map.get(type);
                if (dto == null) {
                    continue;
                }
                if (!type.equalsIgnoreCase("kongcheng") && !type.equalsIgnoreCase("huogong") && !type.equalsIgnoreCase("shuigong") && !type.equalsIgnoreCase("luoshi") && dto.getForceId() != forceId) {
                    continue;
                }
                final long now = System.currentTimeMillis();
                final long lastTime = dto.getLastTime();
                final long protectTime = dto.getProtectTime();
                if (lastTime <= now && protectTime <= now) {
                    continue;
                }
                doc.startObject();
                doc.createElement("stratagemId", dto.getId());
                doc.createElement("type", dto.getType());
                doc.createElement("lv", dto.getLv());
                if (protectTime > now) {
                    doc.createElement("protectCd", protectTime - now);
                }
                if (lastTime > now) {
                    doc.createElement("lastTime", lastTime - now);
                    if (type.equalsIgnoreCase("guwu") || type.equalsIgnoreCase("dongyao")) {
                        final Stratagem stratagem = (Stratagem)this.stratagemCache.get((Object)dto.getId());
                        doc.createElement("stateValue", stratagem.getPar1());
                    }
                }
                doc.endObject();
            }
        }
        doc.endArray();
    }
    
    private byte[] getMovePath(final int cityId, final String moveLine) {
        final JsonDocument doc = new JsonDocument();
        doc.startArray("path");
        doc.startObject();
        doc.createElement("cityId", cityId);
        doc.endObject();
        if (!StringUtils.isEmpty(moveLine)) {
            final String[] cityIds = moveLine.split(",");
            String[] array;
            for (int length = (array = cityIds).length, i = 0; i < length; ++i) {
                final String id = array[i];
                doc.startObject();
                doc.createElement("cityId", id);
                doc.endObject();
            }
        }
        doc.endArray();
        return doc.toByte();
    }
    
    @Transactional
    @Override
    public byte[] autoMoveJuBenStop(final PlayerDto playerDto, final int generalId) {
        final int playerId = playerDto.playerId;
        final PlayerGeneralMilitary pgm = this.playerGeneralMilitaryDao.getMilitary(playerId, generalId);
        final GeneralMoveDto gmd = CityService.getGeneralMoveDto(playerId, generalId);
        if (gmd == null) {
            return JsonBuilder.getJson(State.FAIL, "");
        }
        if (gmd.cityState != 6) {
            gmd.moveLine = "";
            return JsonBuilder.getJson(State.FAIL, "");
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        gmd.moveLine = "";
        this.generalService.sendGeneralMilitaryRecruitInfo(playerId, pgm);
        this.generalService.sendGenerlJuBenMoveInfo(playerId, generalId);
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public byte[] autoMoveJuBen(final int playerId, final int cityId, final int generalId) {
        final JuBenDto juBenDto = JuBenManager.getInstance().getByPid(playerId);
        synchronized (juBenDto.juBenCityDtoMap.get(cityId).battleId) {
            final Player player = this.playerDao.read(playerId);
            final PlayerGeneralMilitary pgm = this.playerGeneralMilitaryDao.getMilitary(playerId, generalId);
            if (pgm == null) {
                // monitorexit((JuBenCityDto)juBenDto.juBenCityDtoMap.get((Object)Integer.valueOf(cityId)).battleId)
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_NO_SUCH_GENERAL_MOVE);
            }
            final int curId = pgm.getJubenLoId();
            final SoloRoad soloRoad = this.soloRoadCache.getRoad(juBenDto.juBen_id, curId, cityId);
            if (soloRoad != null && juBenDto.roadLinked != null && juBenDto.roadLinked.contains(soloRoad.getId())) {
                // monitorexit((JuBenCityDto)juBenDto.juBenCityDtoMap.get((Object)Integer.valueOf(cityId)).battleId)
                return JsonBuilder.getJson(State.FAIL, LocalMessages.MOVE_AUTO_NO_ROAD);
            }
            final int state = pgm.getState();
            if (state != 0 && state != 1) {
                // monitorexit((JuBenCityDto)juBenDto.juBenCityDtoMap.get((Object)Integer.valueOf(cityId)).battleId)
                return JsonBuilder.getJson(State.FAIL, LocalMessages.GENERAL_BUSY);
            }
            boolean adjoining = false;
            final Set<Integer> nbSet = this.soloRoadCache.getNeighbors(pgm.getJubenLoId());
            for (final Integer key : nbSet) {
                if (key == cityId) {
                    adjoining = true;
                }
            }
            final JuBenCityDto targetCity = juBenDto.juBenCityDtoMap.get(cityId);
            GeneralMoveDto gmd = CityService.getGeneralMoveDto(playerId, generalId);
            if (gmd != null && CDUtil.isInCD(gmd.nextMoveTime, new Date())) {
                if (pgm.getJubenLoId() == cityId) {
                    gmd.moveLine = "";
                    this.generalService.sendGenerlJuBenMoveInfo(playerId, generalId);
                    // monitorexit((JuBenCityDto)juBenDto.juBenCityDtoMap.get((Object)Integer.valueOf(cityId)).battleId)
                    return JsonBuilder.getJson(State.FAIL, "");
                }
            }
            else {
                final String battleId = NewBattleManager.getBattleId(18, playerId, cityId);
                final Battle battle = NewBattleManager.getInstance().getBattleByBatId(battleId);
                if ((targetCity.forceId != player.getForceId() || battle != null) && adjoining) {
                    final OperationResult result = this.dataGetter.getCilvilTrickService().hasTrick(targetCity);
                    if (result != null && !result.getResult()) {
                        // monitorexit((JuBenCityDto)juBenDto.juBenCityDtoMap.get((Object)Integer.valueOf(cityId)).battleId)
                        return JsonBuilder.getJson(State.FAIL, result.getResultContent());
                    }
                    gmd = CityService.getGeneralMoveDto(playerId, generalId);
                    if (gmd != null && CDUtil.isInCD(gmd.nextMoveTime, new Date())) {
                        // monitorexit((JuBenCityDto)juBenDto.juBenCityDtoMap.get((Object)Integer.valueOf(cityId)).battleId)
                        return JsonBuilder.getJson(State.FAIL, LocalMessages.GENERAL_IN_MOVE_CD);
                    }
                    final int maxHp = this.dataGetter.getBattleDataCache().getMaxHp(pgm);
                    if (pgm.getForces() * 1.0 / maxHp < 0.05) {
                        // monitorexit((JuBenCityDto)juBenDto.juBenCityDtoMap.get((Object)Integer.valueOf(cityId)).battleId)
                        return JsonBuilder.getJson(State.FAIL, LocalMessages.BATTLE_INT_BATTLE_BLOOD_NO_ENOUGH);
                    }
                    final SoloCity sc = (SoloCity)this.soloCityCache.get((Object)cityId);
                    this.battleService.battleStart(playerId, 18, cityId, new StringBuilder(String.valueOf(generalId)).toString(), sc.getTerrain());
                    this.sendAttMoveInfo(playerId, generalId, pgm.getJubenLoId(), cityId, player.getForceId(), "", pgm.getForces(), false);
                    gmd = CityService.getGeneralMoveDto(playerId, generalId);
                    if (gmd != null) {
                        gmd.moveLine = "";
                    }
                    final JsonDocument doc = new JsonDocument();
                    doc.startObject();
                    doc.createElement("endCityId", cityId);
                    doc.endObject();
                    // monitorexit((JuBenCityDto)juBenDto.juBenCityDtoMap.get((Object)Integer.valueOf(cityId)).battleId)
                    return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
                }
            }
            final Set<Integer> setTemp = new HashSet<Integer>();
            setTemp.add(cityId);
            setTemp.add(pgm.getJubenLoId());
            for (final JuBenCityDto temp : juBenDto.juBenCityDtoMap.values()) {
                if (temp.forceId == player.getForceId()) {
                    setTemp.add(temp.cityId);
                }
            }
            final int[] arr = new int[setTemp.size()];
            int j = 0;
            for (final Integer key2 : setTemp) {
                arr[j] = key2;
                ++j;
            }
            int[] x = null;
            int[] y = null;
            try {
                if (juBenDto.roadLinked != null && !juBenDto.roadLinked.isEmpty()) {
                    final int size = juBenDto.roadLinked.size();
                    x = new int[size];
                    y = new int[size];
                    int i = 0;
                    for (final Integer roadId : juBenDto.roadLinked) {
                        final SoloRoad road = (SoloRoad)this.soloRoadCache.get((Object)roadId);
                        if (road != null) {
                            x[i] = road.getStart();
                            y[i] = road.getEnd();
                            ++i;
                        }
                    }
                }
            }
            catch (Exception e) {
                JuBenService.errorLog.error(this, e);
            }
            final List<Integer> list = this.juBenDataCache.getMinPathJuBen(juBenDto.juBen_id, pgm.getJubenLoId(), cityId, arr, x, y);
            if (list == null || list.size() <= 0) {
                // monitorexit((JuBenCityDto)juBenDto.juBenCityDtoMap.get((Object)Integer.valueOf(cityId)).battleId)
                return JsonBuilder.getJson(State.FAIL, LocalMessages.MOVE_AUTO_NO_ROAD);
            }
            final StringBuilder sb = new StringBuilder();
            for (int k = 1; k < list.size(); ++k) {
                sb.append(list.get(k)).append(",");
            }
            final JsonDocument doc2 = new JsonDocument();
            doc2.startObject();
            gmd = CityService.getUpdateGeneralMoveDto(playerId, generalId);
            gmd.type = 2;
            gmd.moveLine = sb.toString();
            if (sb.length() <= 0) {
                this.generalService.sendGeneralMilitaryRecruitInfo(playerId, pgm);
                this.generalService.sendGenerlJuBenMoveInfo(playerId, generalId);
            }
            gmd.cityState = 6;
            if (gmd == null || gmd.nextMoveTime <= System.currentTimeMillis()) {
                final Tuple<Integer, String> tupleMove = this.move(playerId, generalId, list.get(1), true);
                if (tupleMove.left != 6 && tupleMove.left != 1) {
                    // monitorexit((JuBenCityDto)juBenDto.juBenCityDtoMap.get((Object)Integer.valueOf(cityId)).battleId)
                    return JsonBuilder.getJson(State.FAIL, tupleMove.right);
                }
                if (tupleMove.left == 6) {
                    doc2.startArray("path");
                    for (final int id : list) {
                        doc2.startObject();
                        doc2.createElement("cityId", id);
                        doc2.endObject();
                    }
                    doc2.endArray();
                    this.generalService.sendGenerlJuBenMoveInfo(playerId, generalId);
                }
            }
            else {
                doc2.startArray("path");
                for (final int id2 : list) {
                    doc2.startObject();
                    doc2.createElement("cityId", id2);
                    doc2.endObject();
                }
                doc2.endArray();
                this.generalService.sendGenerlJuBenMoveInfo(playerId, generalId);
            }
            doc2.createElement("endCityId", cityId);
            doc2.endObject();
            // monitorexit((JuBenCityDto)juBenDto.juBenCityDtoMap.get((Object)Integer.valueOf(cityId)).battleId)
            return JsonBuilder.getJson(State.SUCCESS, doc2.toByte());
        }
    }
    
    @Override
    public Tuple<Integer, String> move(final int playerId, final int generalId, final int cityId, final boolean auto) {
        final JuBenDto juBenDto = JuBenManager.getInstance().getByPid(playerId);
        synchronized (juBenDto.juBenCityDtoMap.get(cityId).battleId) {
            final Tuple<Integer, String> tupleMove = new Tuple();
            final Player player = this.playerDao.read(playerId);
            final PlayerGeneralMilitary pgm = this.playerGeneralMilitaryDao.getMilitary(playerId, generalId);
            final Set<Integer> neighbors = this.soloRoadCache.getNeighbors(pgm.getJubenLoId());
            if (neighbors.isEmpty() || !neighbors.contains(cityId)) {
                final GeneralMoveDto gmd = CityService.getGeneralMoveDto(playerId, generalId);
                if (gmd != null) {
                    gmd.moveLine = "";
                }
                tupleMove.left = 0;
                tupleMove.right = LocalMessages.LOCATION_NOT_NEIGHBOR;
                // monitorexit((JuBenCityDto)juBenDto.juBenCityDtoMap.get((Object)Integer.valueOf(cityId)).battleId)
                return tupleMove;
            }
            final int curId = pgm.getJubenLoId();
            final SoloRoad soloRoad = this.soloRoadCache.getRoad(juBenDto.juBen_id, curId, cityId);
            if (soloRoad != null && juBenDto.roadLinked != null && juBenDto.roadLinked.contains(soloRoad.getId())) {
                final GeneralMoveDto gmd2 = CityService.getGeneralMoveDto(playerId, generalId);
                if (gmd2 != null) {
                    gmd2.moveLine = "";
                }
                tupleMove.left = 0;
                tupleMove.right = LocalMessages.MOVE_AUTO_NO_ROAD;
                // monitorexit((JuBenCityDto)juBenDto.juBenCityDtoMap.get((Object)Integer.valueOf(cityId)).battleId)
                return tupleMove;
            }
            final String battleId = NewBattleManager.getBattleId(18, playerId, cityId);
            final Battle battle = NewBattleManager.getInstance().getBattleByBatId(battleId);
            boolean adjoining = false;
            final Set<Integer> nbSet = this.soloRoadCache.getNeighbors(pgm.getJubenLoId());
            for (final Integer key : nbSet) {
                if (key == cityId) {
                    adjoining = true;
                }
            }
            final JuBenCityDto targetCity = juBenDto.juBenCityDtoMap.get(cityId);
            if ((targetCity.forceId != player.getForceId() || battle != null) && adjoining) {
                final OperationResult result = this.dataGetter.getCilvilTrickService().hasTrick(targetCity);
                if (result != null && !result.getResult()) {
                    tupleMove.left = 0;
                    tupleMove.right = result.getResultContent();
                    // monitorexit((JuBenCityDto)juBenDto.juBenCityDtoMap.get((Object)Integer.valueOf(cityId)).battleId)
                    return tupleMove;
                }
                final int maxHp = this.dataGetter.getBattleDataCache().getMaxHp(pgm);
                if (pgm.getForces() * 1.0 / maxHp < 0.05) {
                    final GeneralMoveDto gmd3 = CityService.getGeneralMoveDto(playerId, generalId);
                    if (gmd3 != null) {
                        gmd3.moveLine = "";
                    }
                    tupleMove.left = 0;
                    tupleMove.right = LocalMessages.BATTLE_INT_BATTLE_BLOOD_NO_ENOUGH;
                    // monitorexit((JuBenCityDto)juBenDto.juBenCityDtoMap.get((Object)Integer.valueOf(cityId)).battleId)
                    return tupleMove;
                }
                final SoloCity sc = (SoloCity)this.soloCityCache.get((Object)cityId);
                this.battleService.battleStart(playerId, 18, cityId, new StringBuilder(String.valueOf(generalId)).toString(), sc.getTerrain());
                this.sendAttMoveInfo(playerId, generalId, pgm.getJubenLoId(), cityId, player.getForceId(), "", pgm.getForces(), false);
                final GeneralMoveDto gmd4 = CityService.getGeneralMoveDto(playerId, generalId);
                if (gmd4 != null) {
                    gmd4.moveLine = "";
                }
                tupleMove.left = 1;
                tupleMove.right = LocalMessages.CITY_IN_BATTLEING;
                // monitorexit((JuBenCityDto)juBenDto.juBenCityDtoMap.get((Object)Integer.valueOf(cityId)).battleId)
                return tupleMove;
            }
            else {
                final Tuple<Integer, String> tuple = this.checkMoveCondition(pgm, player, cityId, auto);
                if (tuple.left != 1) {
                    if (tuple.left != 6) {
                        final GeneralMoveDto gmd5 = CityService.getGeneralMoveDto(playerId, generalId);
                        if (gmd5 != null) {
                            gmd5.moveLine = "";
                        }
                    }
                    this.generalService.sendGeneralMilitaryRecruitInfo(playerId, pgm);
                    tupleMove.left = tuple.left;
                    tupleMove.right = tuple.right;
                    // monitorexit((JuBenCityDto)juBenDto.juBenCityDtoMap.get((Object)Integer.valueOf(cityId)).battleId)
                    return tupleMove;
                }
                this.startMove(playerId, generalId, cityId, auto);
                StringBuilder sb = new StringBuilder();
                final GeneralMoveDto gmd3 = CityService.getGeneralMoveDto(playerId, generalId);
                if (gmd3 != null && gmd3.moveLine != null && gmd3.moveLine.length() > 0) {
                    final String[] strs = gmd3.moveLine.split(",");
                    for (int i = 1; i < strs.length; ++i) {
                        if (i > 1 || cityId != Integer.valueOf(strs[i])) {
                            sb.append(strs[i]).append(",");
                        }
                    }
                    this.sendAttMoveInfo(playerId, generalId, pgm.getJubenLoId(), cityId, player.getForceId(), sb.toString(), pgm.getForces(), false);
                    sb = this.updateMoveLine(gmd3.moveLine, cityId);
                    gmd3.moveLine = sb.toString();
                    if (sb.length() <= 0) {
                        gmd3.moveLine = "";
                    }
                    gmd3.cityState = 6;
                }
                tupleMove.left = 1;
                tupleMove.right = "";
                // monitorexit((JuBenCityDto)juBenDto.juBenCityDtoMap.get((Object)Integer.valueOf(cityId)).battleId)
                return tupleMove;
            }
        }
    }
    
    private StringBuilder updateMoveLine(final String moveLine, final int cityId) {
        final StringBuilder sb = new StringBuilder();
        final String[] strs = moveLine.split(",");
        for (int i = 1; i < strs.length; ++i) {
            if (i > 1 || cityId != Integer.valueOf(strs[i])) {
                sb.append(strs[i]).append(",");
            }
        }
        return sb;
    }
    
    private Tuple<Integer, String> checkMoveCondition(final PlayerGeneralMilitary pgm, final Player player, final int cityId, final boolean auto) {
        final Tuple<Integer, String> tuple = new Tuple();
        if (pgm == null) {
            tuple.left = 2;
            tuple.right = LocalMessages.T_NO_SUCH_GENERAL_MOVE;
            return tuple;
        }
        final int playerId = pgm.getPlayerId();
        final Date nowDate = new Date();
        final int state = pgm.getState();
        if (state != 0 && state != 1) {
            tuple.left = 5;
            tuple.right = LocalMessages.GENERAL_BUSY;
            return tuple;
        }
        final GeneralMoveDto gmd = CityService.getGeneralMoveDto(playerId, pgm.getGeneralId());
        if (gmd != null && CDUtil.isInCD(gmd.nextMoveTime, nowDate)) {
            tuple.left = 6;
            tuple.right = LocalMessages.GENERAL_IN_MOVE_CD;
            return tuple;
        }
        final JuBenDto juBenDto = JuBenManager.getInstance().getByPid(playerId);
        final JuBenCityDto targetCity = juBenDto.juBenCityDtoMap.get(cityId);
        if (targetCity.forceId != player.getForceId()) {
            tuple.left = 7;
            tuple.right = LocalMessages.FORCE_WRONG;
            return tuple;
        }
        final Set<Integer> neighbors = this.soloRoadCache.getNeighbors(pgm.getJubenLoId());
        if (neighbors.isEmpty() || !neighbors.contains(cityId)) {
            tuple.left = 8;
            tuple.right = LocalMessages.LOCATION_NOT_NEIGHBOR;
            return tuple;
        }
        tuple.left = 1;
        return tuple;
    }
    
    @Override
    public boolean startMove(final int playerId, final int generalId, final int cityId, final boolean auto) {
        final PlayerGeneralMilitary pgm = this.playerGeneralMilitaryDao.getMilitary(playerId, generalId);
        final Player player = this.playerDao.read(playerId);
        final int state = pgm.getState();
        final int curCityId = pgm.getJubenLoId();
        final JuBenDto juBenDto = JuBenManager.getInstance().getByPid(playerId);
        if (juBenDto == null) {
            return false;
        }
        final SoloRoad road = this.soloRoadCache.getRoad(juBenDto.juBen_id, curCityId, cityId);
        long cd = 0L;
        final General general = (General)this.generalCache.get((Object)pgm.getGeneralId());
        final Troop troop = this.troopCache.getTroop(general.getTroop(), player.getPlayerId());
        cd = WorldCityCommon.getNextMoveCd(road.getLength(), troop.getSpeed());
        final Date nextMoveDate = new Date(System.currentTimeMillis() + cd);
        final boolean isRecruiting = state == 1;
        final String params = WorldCityCommon.makeTaskParam(playerId, generalId, isRecruiting, auto);
        final StringBuffer sb = new StringBuffer(params);
        sb.append(";").append(cityId);
        final long nextMoveTime = nextMoveDate.getTime();
        final int taskId = this.jobService.addJob("juBenService", "changeState", sb.toString(), nextMoveTime + 1000L);
        final GeneralMoveDto gmd = CityService.getUpdateGeneralMoveDto(playerId, generalId);
        gmd.startMoveTime = System.currentTimeMillis();
        gmd.startCityId = curCityId;
        gmd.cityState = 6;
        gmd.nextMoveTime = nextMoveTime;
        gmd.taskId = taskId;
        gmd.type = 2;
        this.playerGeneralMilitaryDao.moveJuben(playerId, generalId, state, cityId);
        this.generalService.sendGeneralMilitaryRecruitInfo(playerId, generalId);
        this.generalService.sendGenerlJuBenMoveInfo(playerId, generalId);
        ScenarioEventMessageHelper.sendMoveToCitykMessage(playerId, generalId, cityId);
        return true;
    }
    
    @Override
    public void changeState(final String param) {
        final String[] s = param.split(";");
        final int playerId = Integer.valueOf(s[0]);
        final int generalId = Integer.valueOf(s[1]);
        final boolean wasRecruiting = Integer.valueOf(s[2]) == 1;
        final boolean auto = Integer.valueOf(s[3]) == 1;
        final PlayerGeneralMilitary pgm = this.playerGeneralMilitaryDao.getMilitary(playerId, generalId);
        if (pgm == null) {
            return;
        }
        final GeneralMoveDto gmd = CityService.getUpdateGeneralMoveDto(playerId, generalId);
        synchronized (gmd) {
            if (gmd.nextMoveTime == 0L || System.currentTimeMillis() < gmd.nextMoveTime) {
                // monitorexit(gmd)
                return;
            }
            gmd.nextMoveTime = 0L;
            gmd.type = 2;
        }
        // monitorexit(gmd)
        if (auto && this.inAutoMove(playerId, generalId)) {
            return;
        }
        gmd.cityState = 0;
        gmd.nextMoveTime = 0L;
        if (pgm.getState() == 1 || pgm.getState() == 0) {
            final Date nowDate = new Date();
            if (wasRecruiting) {
                this.playerGeneralMilitaryDao.restartRecruit(playerId, generalId, 1, nowDate);
            }
        }
        this.generalService.sendGeneralMilitaryRecruitInfo(playerId, generalId);
        this.generalService.sendGenerlJuBenMoveInfo(playerId, generalId);
    }
    
    public boolean inAutoMove(final int playerId, final int generalId) {
        final PlayerGeneralMilitary pgm = this.playerGeneralMilitaryDao.getMilitary(playerId, generalId);
        final GeneralMoveDto gmd = CityService.getGeneralMoveDto(playerId, generalId);
        if (gmd != null && gmd.moveLine != null && gmd.moveLine.length() > 0) {
            final String[] strs = gmd.moveLine.split(",");
            if (strs.length > 0) {
                int cityId = Integer.valueOf(strs[0]);
                if (cityId == pgm.getJubenLoId()) {
                    if (strs.length <= 1) {
                        gmd.moveLine = "";
                        this.generalService.sendGeneralMilitaryRecruitInfo(playerId, pgm);
                        this.generalService.sendGenerlJuBenMoveInfo(playerId, generalId);
                        return false;
                    }
                    cityId = Integer.valueOf(strs[1]);
                }
                final Tuple<Integer, String> tupleMove = this.move(playerId, generalId, cityId, true);
                if (tupleMove.left != 1) {
                    gmd.moveLine = "";
                    this.generalService.sendGeneralMilitaryRecruitInfo(playerId, pgm.getGeneralId());
                    this.generalService.sendGenerlJuBenMoveInfo(playerId, generalId);
                }
                return true;
            }
            gmd.moveLine = "";
            this.generalService.sendGeneralMilitaryRecruitInfo(playerId, pgm.getGeneralId());
            this.generalService.sendGenerlJuBenMoveInfo(playerId, generalId);
        }
        return false;
    }
    
    @Override
    public void sendAttMoveInfo(final int playerId, final int generalId, final int cityId, final int nextCityId, final int forceId, final String moveLine, final long forceShow, final boolean atOnce) {
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("generalId", generalId);
        doc.createElement("orgCityId", cityId);
        doc.createElement("curCityId", nextCityId);
        doc.createElement("atOnce", atOnce);
        doc.createElement("id", nextCityId);
        if (cityId != nextCityId) {
            doc.appendJson(this.getMovePath(cityId, nextCityId, moveLine));
        }
        final GeneralMoveDto gmd = CityService.getGeneralMoveDto(playerId, generalId);
        if (gmd != null && gmd.nextMoveTime > 0L) {
            doc.createElement("cd", CDUtil.getCD(gmd.nextMoveTime, new Date()));
        }
        if (!StringUtils.isEmpty(moveLine)) {
            doc.createElement("autoMove", true);
        }
        doc.endObject();
        Players.push(playerId, PushCommand.PUSH_ATTMOV_JUBEN, doc.toByte());
    }
    
    private byte[] getMovePath(final int orgCityId, final int cityId, final String moveLine) {
        final JsonDocument doc = new JsonDocument();
        doc.startArray("path");
        doc.startObject();
        doc.createElement("cityId", orgCityId);
        doc.endObject();
        doc.startObject();
        doc.createElement("cityId", cityId);
        doc.endObject();
        if (!StringUtils.isEmpty(moveLine)) {
            final String[] cityIds = moveLine.split(",");
            String[] array;
            for (int length = (array = cityIds).length, i = 0; i < length; ++i) {
                final String id = array[i];
                doc.startObject();
                doc.createElement("cityId", Integer.valueOf(id));
                doc.endObject();
            }
        }
        doc.endArray();
        return doc.toByte();
    }
    
    @Override
    public void changeForceIdAndState(final int cityId, final int forceId, final int state, final int playerId, final String playerName) {
        try {
            final JuBenDto juBenDto = JuBenManager.getInstance().getByPid(playerId);
            if (juBenDto == null) {
                return;
            }
            final JuBenCityDto city = juBenDto.juBenCityDtoMap.get(cityId);
            city.state = 0;
            city.forceId = forceId;
            this.playerScenarioCityDao.updateForceId(playerId, juBenDto.juBen_id, cityId, forceId, juBenDto.player_force_id);
            ScenarioEventMessageHelper.sendCityStateChangeMessage(cityId, state, playerId);
            final Map<Integer, Integer> changeMap = new HashMap<Integer, Integer>();
            int orgTitle = city.title;
            final int surroundTitle = this.getSurroundState(juBenDto, city);
            if (orgTitle != surroundTitle && city.cityId != juBenDto.npcCapital && juBenDto.juBen_id != 10 && !this.isWorldDrama(juBenDto.juBen_id)) {
                city.title = surroundTitle;
                changeMap.put(city.cityId, surroundTitle);
            }
            JuBenCityDto ci = null;
            final Set<Integer> set = this.soloRoadCache.getNeighbors(cityId);
            for (final Integer cId : set) {
                ci = juBenDto.juBenCityDtoMap.get(cId);
                orgTitle = ci.title;
                final int subSurroundTitle = this.getSurroundState(juBenDto, ci);
                if (orgTitle != subSurroundTitle || ci.cityId == juBenDto.capital) {
                    if (ci.cityId != juBenDto.npcCapital && juBenDto.juBen_id != 10 && !this.isWorldDrama(juBenDto.juBen_id)) {
                        ci.title = subSurroundTitle;
                        changeMap.put(ci.cityId, subSurroundTitle);
                    }
                    final Set<Integer> subSet = this.soloRoadCache.getNeighbors(ci.cityId);
                    JuBenCityDto subCi = null;
                    int subOrgTitle = 0;
                    for (final Integer subCId : subSet) {
                        if (subCId == city.cityId) {
                            continue;
                        }
                        subCi = juBenDto.juBenCityDtoMap.get(subCId);
                        if (subCi.forceId != ci.forceId) {
                            continue;
                        }
                        subOrgTitle = subCi.title;
                        final int thirdSurroundState = this.getSurroundState(juBenDto, ci);
                        if (subOrgTitle == thirdSurroundState || subCi.cityId == juBenDto.npcCapital || juBenDto.juBen_id == 10 || this.isWorldDrama(juBenDto.juBen_id)) {
                            continue;
                        }
                        subCi.title = thirdSurroundState;
                        changeMap.put(subCi.cityId, thirdSurroundState);
                    }
                }
            }
            Players.push(playerId, PushCommand.PUSH_JUBEN, JsonBuilder.getSimpleJson("refresh", true));
        }
        catch (Exception e) {
            JuBenService.errorLog.error("CityService, modify city force and state exception.", e);
        }
    }
    
    private boolean isWorldDrama(final int jubenId) {
        return this.wdSjpDramaCache.getWorldDramaByDramaId(jubenId) != null;
    }
    
    private int getSurroundState(final JuBenDto juBenDto, final JuBenCityDto city) {
        final Set<Integer> set = this.soloRoadCache.getNeighbors(city.cityId);
        JuBenCityDto ci = null;
        int sameForceNum = 0;
        JuBenCityDto sameForceCity = null;
        for (final Integer cId : set) {
            ci = juBenDto.juBenCityDtoMap.get(cId);
            if (city.forceId == ci.forceId) {
                ++sameForceNum;
                sameForceCity = ci;
            }
        }
        if (sameForceNum == 0) {
            return 1;
        }
        if (sameForceNum == 1) {
            JuBenCityDto subCi = null;
            final Set<Integer> subSet = this.soloRoadCache.getNeighbors(sameForceCity.cityId);
            for (final Integer subCId : subSet) {
                if (subCId == city.cityId) {
                    continue;
                }
                subCi = juBenDto.juBenCityDtoMap.get(subCId);
                if (city.forceId == subCi.forceId) {
                    return 0;
                }
            }
            return 2;
        }
        return 0;
    }
    
    private void clearJubBenOver(final JuBenDto juBenDto, final int playerId) {
        final Player player = this.dataGetter.getPlayerDao().read(playerId);
        final List<PlayerGeneralMilitary> pgmList = this.playerGeneralMilitaryDao.getMilitaryList(playerId);
        int forcesMax = 0;
        int needForces = 0;
        for (final PlayerGeneralMilitary pgm : pgmList) {
            final GeneralMoveDto gmd = CityService.getGeneralMoveDto(playerId, pgm.getGeneralId());
            if (gmd != null && gmd.type == 2) {
                gmd.moveLine = "";
                this.generalService.sendGeneralMilitaryRecruitInfo(playerId, pgm);
                this.generalService.sendGenerlJuBenMoveInfo(playerId, pgm.getGeneralId());
            }
            if (!this.isInWorldDrama(playerId)) {
                forcesMax = this.battleDataCache.getMaxHp(pgm);
                needForces = forcesMax - pgm.getForces();
                if (needForces <= 0) {
                    continue;
                }
                this.playerGeneralMilitaryDao.addGeneralForces(playerId, pgm.getGeneralId(), new Date(), 0, needForces);
            }
        }
        final int capitalId = juBenDto.capital;
        for (final JuBenCityDto jbcd : juBenDto.juBenCityDtoMap.values()) {
            if (jbcd.cityId == juBenDto.capital) {
                continue;
            }
            this.clearBattlesInThisCity(player, jbcd, true);
            for (final PlayerGeneralMilitary pgm2 : pgmList) {
                if (pgm2.getJubenLoId() == jbcd.cityId) {
                    this.dataGetter.getPlayerGeneralMilitaryDao().moveJuben(pgm2.getPlayerId(), pgm2.getGeneralId(), 1, capitalId);
                    ScenarioEventMessageHelper.sendMoveToCitykMessage(pgm2.getPlayerId(), pgm2.getGeneralId(), capitalId);
                }
            }
            this.dataGetter.getScenarioNpcDao().deleteAllInThisCity(playerId, jbcd.cityId);
        }
        this.generalService.sendGeneralMilitaryList(playerId);
    }
    
    private void clearBattlesInThisCity(final Player player, final JuBenCityDto juBenCityDto, final boolean playerSide) {
        try {
            final Battle battle = NewBattleManager.getInstance().getBattleByBatId(juBenCityDto.battleId);
            if (battle != null) {
                this.clearOneSideOfBattle(player, juBenCityDto, battle, playerSide);
            }
            final List<Battle> oneToOneBattleList = NewBattleManager.getInstance().getAllScenariosOneToOneBattle(player.getPlayerId(), juBenCityDto.cityId);
            if (oneToOneBattleList != null && oneToOneBattleList.size() > 0) {
                for (final Battle oneToOneBattle : oneToOneBattleList) {
                    this.clearOneSideOfBattle(player, juBenCityDto, oneToOneBattle, playerSide);
                }
            }
            final Builder scenarioEventBuilder = BuilderFactory.getInstance().getBuilder(20);
            final String scenarioEventBattleId = scenarioEventBuilder.getBattleId(this.dataGetter, player, juBenCityDto.cityId);
            final Battle scenarioEventBattle = NewBattleManager.getInstance().getBattleByBatId(scenarioEventBattleId);
            if (scenarioEventBattle != null) {
                this.clearOneSideOfBattle(player, juBenCityDto, scenarioEventBattle, playerSide);
            }
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("JuBenService.clearBattlesInThisCity catch Exception", e);
        }
    }
    
    @Override
    public byte[] quitJuBen(final PlayerDto playerDto) {
        final int playerId = playerDto.playerId;
        final JuBenDto juBenDto = JuBenManager.getInstance().getByPid(playerId);
        if (juBenDto == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.JUBEN_CANNT_QUIT);
        }
        juBenDto.state = 0;
        juBenDto.isWin = false;
        try {
            this.caculatePhantomCopperExp(playerId, juBenDto);
            this.clearJubBenOver(juBenDto, playerId);
        }
        catch (Exception e) {
            JuBenService.errorLog.error("JuBenService quitJuBen playerId " + playerId, e);
        }
        JuBenManager.getInstance().clearByVid(playerId);
        this.playerScenarioDao.updateStateOverTime(playerId, juBenDto.juBen_id, 0, System.currentTimeMillis());
        this.scenarioNpcDao.deleteAll(playerId);
        final JsonDocument doc1 = new JsonDocument();
        doc1.startObject();
        doc1.createElement("inJuBen", false);
        doc1.endObject();
        Players.push(playerId, PushCommand.PUSH_UPDATE, doc1.toByte());
        if (this.isWorldDrama(juBenDto.juBen_id)) {
            final WdSjp wdSjp = (WdSjp)this.dataGetter.getWdSjpCache().get((Object)5);
            if (wdSjp != null) {
                this.dataGetter.getCityService().addPlayerNextEvent(playerDto.playerId, playerDto.forceId, 5, false);
            }
        }
        final JsonDocument doc2 = new JsonDocument();
        doc2.startObject();
        doc2.createElement("quit", true);
        doc2.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc2.toByte());
    }
    
    @Override
    public byte[] pleaseGiveMeAReply(final PlayerDto playerDto, final int generalId) {
        final PlayerGeneralMilitary pgm = this.playerGeneralMilitaryDao.getMilitary(playerDto.playerId, generalId);
        if (pgm == null) {
            return JsonBuilder.getJson(State.FAIL, "");
        }
        final GeneralMoveDto gmd = CityService.getGeneralMoveDto(pgm.getPlayerId(), pgm.getGeneralId());
        if (gmd == null || StringUtils.isBlank(gmd.moveLine)) {
            return JsonBuilder.getJson(State.FAIL, "");
        }
        if (gmd.type != 2) {
            return JsonBuilder.getJson(State.FAIL, "");
        }
        final String[] split = gmd.moveLine.split(",");
        if (split.length > 0) {
            final boolean isRecruiting = pgm.getState() == 1;
            final String params = WorldCityCommon.makeTaskParam(playerDto.playerId, generalId, isRecruiting, true);
            final StringBuffer sb = new StringBuffer(params);
            sb.append(";").append(pgm.getLocationId());
            this.jobService.addJob("juBenService", "changeState", sb.toString(), gmd.nextMoveTime);
        }
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Override
    public Tuple<Boolean, String> assembleMove(final int playerId, final int generalId, final int cityId, final int kick) {
        final Tuple<Boolean, String> tuple = new Tuple();
        final JuBenDto juBenDto = JuBenManager.getInstance().getByPid(playerId);
        if (juBenDto == null) {
            tuple.left = false;
            tuple.right = LocalMessages.JUBEN_CANNT_QUIT;
            return tuple;
        }
        synchronized (juBenDto.juBenCityDtoMap.get(cityId).battleId) {
            final Player player = this.playerDao.read(playerId);
            final PlayerGeneralMilitary pgm = this.playerGeneralMilitaryDao.getMilitary(playerId, generalId);
            if (pgm == null) {
                tuple.left = false;
                tuple.right = LocalMessages.T_NO_SUCH_GENERAL_MOVE;
                // monitorexit((JuBenCityDto)juBenDto.juBenCityDtoMap.get((Object)Integer.valueOf(cityId)).battleId)
                return tuple;
            }
            boolean adjoining = false;
            final Set<Integer> nbSet = this.soloRoadCache.getNeighbors(pgm.getJubenLoId());
            for (final Integer key : nbSet) {
                if (key == cityId) {
                    adjoining = true;
                }
            }
            final JuBenCityDto targetCity = juBenDto.juBenCityDtoMap.get(cityId);
            if (targetCity.forceId != player.getForceId() && adjoining) {
                final int maxHp = this.dataGetter.getBattleDataCache().getMaxHp(pgm);
                if (pgm.getForces() * 1.0 / maxHp < 0.05) {
                    tuple.left = false;
                    tuple.right = LocalMessages.BATTLE_INT_BATTLE_BLOOD_NO_ENOUGH;
                    // monitorexit((JuBenCityDto)juBenDto.juBenCityDtoMap.get((Object)Integer.valueOf(cityId)).battleId)
                    return tuple;
                }
                final SoloCity sc = (SoloCity)this.soloCityCache.get((Object)cityId);
                this.battleService.battleStart(playerId, 18, cityId, new StringBuilder(String.valueOf(generalId)).toString(), sc.getTerrain());
                this.sendAttMoveInfo(playerId, generalId, pgm.getJubenLoId(), cityId, player.getForceId(), "", pgm.getForces(), false);
                final GeneralMoveDto gmd = CityService.getGeneralMoveDto(playerId, generalId);
                if (gmd != null) {
                    gmd.moveLine = "";
                }
                tuple.left = true;
                tuple.right = LocalMessages.CITY_IN_BATTLEING;
                // monitorexit((JuBenCityDto)juBenDto.juBenCityDtoMap.get((Object)Integer.valueOf(cityId)).battleId)
                return tuple;
            }
            else {
                final int state = pgm.getState();
                if (state != 0 && state != 1) {
                    tuple.left = false;
                    tuple.right = LocalMessages.GENERAL_BUSY;
                    // monitorexit((JuBenCityDto)juBenDto.juBenCityDtoMap.get((Object)Integer.valueOf(cityId)).battleId)
                    return tuple;
                }
                final JuBenCityDto cityTemp = juBenDto.juBenCityDtoMap.get(pgm.getJubenLoId());
                if (cityTemp.forceId != player.getForceId()) {
                    JuBenService.errorLog.error("CityService autoMove playerId:" + playerId + " playerForceId:" + player.getForceId() + " generalId:" + generalId + " pgm.getLocationId():" + pgm.getJubenLoId() + " targetCityId:" + cityId);
                }
                GeneralMoveDto gmd = CityService.getGeneralMoveDto(playerId, generalId);
                if (pgm.getJubenLoId() == cityId) {
                    tuple.left = false;
                    tuple.right = LocalMessages.GENERAL_IN_CURCITY_NO_MOVE;
                    // monitorexit((JuBenCityDto)juBenDto.juBenCityDtoMap.get((Object)Integer.valueOf(cityId)).battleId)
                    return tuple;
                }
                final Set<Integer> setTemp = new HashSet<Integer>();
                setTemp.add(cityId);
                setTemp.add(pgm.getJubenLoId());
                for (final JuBenCityDto temp : juBenDto.juBenCityDtoMap.values()) {
                    if (temp.forceId == player.getForceId()) {
                        setTemp.add(temp.cityId);
                    }
                }
                final int[] arr = new int[setTemp.size()];
                int j = 0;
                for (final Integer key2 : setTemp) {
                    arr[j] = key2;
                    ++j;
                }
                int[] x = null;
                int[] y = null;
                try {
                    if (juBenDto.roadLinked != null && !juBenDto.roadLinked.isEmpty()) {
                        final int size = juBenDto.roadLinked.size();
                        x = new int[size];
                        y = new int[size];
                        int i = 0;
                        for (final Integer roadId : juBenDto.roadLinked) {
                            final SoloRoad road = (SoloRoad)this.soloRoadCache.get((Object)roadId);
                            if (road != null) {
                                x[i] = road.getStart();
                                y[i] = road.getEnd();
                                ++i;
                            }
                        }
                    }
                }
                catch (Exception e) {
                    JuBenService.errorLog.error(this, e);
                }
                final List<Integer> list = this.juBenDataCache.getMinPathJuBen(juBenDto.juBen_id, pgm.getJubenLoId(), cityId, arr, x, y);
                if (list == null || list.size() <= 0) {
                    tuple.left = false;
                    tuple.right = LocalMessages.MOVE_AUTO_NO_ROAD;
                    // monitorexit((JuBenCityDto)juBenDto.juBenCityDtoMap.get((Object)Integer.valueOf(cityId)).battleId)
                    return tuple;
                }
                final StringBuilder sb = new StringBuilder();
                for (int k = 1; k < list.size(); ++k) {
                    sb.append(list.get(k)).append(",");
                }
                gmd = CityService.getUpdateGeneralMoveDto(playerId, generalId);
                gmd.moveLine = sb.toString();
                gmd.type = 1;
                if (sb.length() <= 0) {
                    this.generalService.sendGeneralMilitaryRecruitInfo(playerId, pgm);
                    this.generalService.sendGenerlJuBenMoveInfo(playerId, generalId);
                }
                gmd.cityState = 6;
                final Tuple<Integer, String> tupleMove = this.move(playerId, generalId, list.get(1), true);
                if (tupleMove.left != 6 && tupleMove.left != 1) {
                    tuple.left = false;
                    tuple.right = tupleMove.right;
                    // monitorexit((JuBenCityDto)juBenDto.juBenCityDtoMap.get((Object)Integer.valueOf(cityId)).battleId)
                    return tuple;
                }
                if (tupleMove.left == 6) {
                    this.generalService.sendGenerlMoveInfo(playerId, generalId);
                }
                tuple.left = true;
                tuple.right = LocalMessages.ASSEMBLE_SUCC;
                // monitorexit((JuBenCityDto)juBenDto.juBenCityDtoMap.get((Object)Integer.valueOf(cityId)).battleId)
                return tuple;
            }
        }
    }
    
    @Override
    public void changeState(final int playerId, final int cityId, final int state, final boolean attWin) {
        final JuBenDto juBenDto = JuBenManager.getInstance().getByPid(playerId);
        if (juBenDto == null) {
            return;
        }
        final JuBenCityDto jubenDto = juBenDto.juBenCityDtoMap.get(cityId);
        if (jubenDto == null) {
            JuBenService.errorLog.error("JubBenService changeState cityId:" + cityId);
        }
        ScenarioEventMessageHelper.sendCityStateChangeMessage(cityId, jubenDto.state = state, playerId);
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.startArray("cities");
        doc.startObject();
        doc.createElement("cityId", cityId);
        doc.createElement("state", state);
        doc.endObject();
        doc.endArray();
        doc.endObject();
        Players.push(playerId, PushCommand.PUSH_JUBEN_STATE, doc.toByte());
    }
    
    @Override
    public void dealTrap(final Player player, final PlayerGeneralMilitary pgm, final int cityId) {
    }
    
    @Override
    public byte[] getJuBenCityInfo(final int cityId, final PlayerDto playerDto) {
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        final JuBenDto juBenDto = JuBenManager.getInstance().getByPid(playerDto.playerId);
        if (juBenDto == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.JUBEN_NOT_IN_JUBEN);
        }
        final JuBenCityDto jbcd = juBenDto.juBenCityDtoMap.get(cityId);
        if (jbcd == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10012);
        }
        final Battle battle = NewBattleManager.getInstance().getBattleByBatId(jbcd.battleId);
        if (battle != null) {
            doc.createElement("inBattle", true);
            final boolean canWatchBat = true;
            doc.startArray("attSide");
            for (final Map.Entry<Integer, Integer> entry : battle.attSideDetail.entrySet()) {
                if (entry.getValue() <= 0) {
                    continue;
                }
                doc.startObject();
                doc.createElement("forceId", entry.getKey());
                doc.createElement("num", entry.getValue());
                doc.endObject();
            }
            doc.endArray();
            doc.startArray("defSide");
            for (final Map.Entry<Integer, Integer> entry : battle.defSideDetail.entrySet()) {
                if (entry.getValue() <= 0) {
                    continue;
                }
                doc.startObject();
                doc.createElement("forceId", entry.getKey());
                doc.createElement("num", entry.getValue());
                doc.endObject();
            }
            doc.endArray();
            if (canWatchBat) {
                doc.createElement("battleId", battle.getBattleId());
                doc.createElement("targetId", battle.getDefBaseInfo().getId());
                doc.createElement("type", battle.getBattleType());
            }
        }
        else {
            doc.createElement("inBattle", false);
        }
        this.getTrickInfo(doc, jbcd.trickDto, playerDto.forceId, jbcd.cityId);
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public byte[] getChoiceInfo(final PlayerDto playerDto, final int eventId) {
        final JuBenDto dto = JuBenManager.getInstance().getByPid(playerDto.playerId);
        if (dto == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.JUBEN_NOT_IN_JUBEN);
        }
        final List<ScenarioEvent> list = dto.eventList;
        if (list == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.JUBEN_DEAL_CONDITION_NOT_FULFILL);
        }
        for (final ScenarioEvent event : list) {
            if (event.getSoloEvent().getId() == eventId) {
                if (event.getState() != 2) {
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.JUBEN_EVENT_IS_COMPLETE);
                }
                final ScenarioChoice choice = event.getChoice();
                final int cityId = choice.getChoiceCity();
                final String catogery = choice.getChoiceCatogery();
                List<SoloCity> neighbours = null;
                if (catogery.equalsIgnoreCase("dychoice")) {
                    neighbours = new ArrayList<SoloCity>();
                    this.checkNeighbours(neighbours, dto);
                }
                final int hasGeneralInCity = this.playerGeneralMilitaryDao.getGeneralNumInCity(playerDto.playerId, cityId);
                if (hasGeneralInCity <= 0) {
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.JUBEN_DEAL_CONDITION_NOT_FULFILL);
                }
                final JuBenCityDto cityDto = dto.juBenCityDtoMap.get(cityId);
                if (cityDto == null || cityDto.state != 0) {
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.JUBEN_IS_FIGHTING);
                }
                final JsonDocument doc = choice.getChoiceInfo(neighbours, dto.mengdeLocation);
                if (doc != null) {
                    return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
                }
                return JsonBuilder.getJson(State.FAIL, LocalMessages.JUBEN_DEAL_CONDITION_NOT_FULFILL);
            }
        }
        return JsonBuilder.getJson(State.FAIL, LocalMessages.JUBEN_DEAL_CONDITION_NOT_FULFILL);
    }
    
    private void checkNeighbours(final List<SoloCity> neighbours, final JuBenDto dto) {
        final int location = dto.mengdeLocation;
        final Set<Integer> result = this.dataGetter.getSoloRoadCache().getNeighbors(location);
        for (final Integer key : result) {
            final SoloCity city = (SoloCity)this.soloCityCache.get((Object)key);
            neighbours.add(city);
        }
    }
    
    @Override
    public void saveEventInfo(final int playerId) {
        final JuBenDto dto = JuBenManager.getInstance().getByPid(playerId);
        if (dto == null) {
            return;
        }
        final List<ScenarioEvent> events = dto.eventList;
        if (events == null || events.isEmpty()) {
            return;
        }
        final StringBuffer sb = new StringBuffer();
        try {
            for (final ScenarioEvent event : events) {
                if (event == null) {
                    continue;
                }
                sb.append(event.selfStore()).append("+");
            }
            SymbolUtil.removeTheLast(sb);
            this.playerScenarioDao.updateScenarioInfo(playerId, dto.juBen_id, sb.toString());
        }
        catch (Exception e) {
            JuBenService.errorLog.error("saveEventInfo fail...playerId:" + playerId);
            JuBenService.errorLog.error(e.getMessage());
            JuBenService.errorLog.error(this, e);
        }
    }
    
    @Override
    public byte[] makeAChoice(final int playerId, final int eventId, final int choice) {
        List<ScenarioEvent> list = null;
        final JuBenDto dto = JuBenManager.getInstance().getByPid(playerId);
        if (dto == null) {
            return JsonBuilder.getJson(State.FAIL, "");
        }
        list = dto.eventList;
        if (list == null) {
            return JsonBuilder.getJson(State.FAIL, "");
        }
        final SoloEvent soloEvent = (SoloEvent)this.soloEventCache.get((Object)eventId);
        final int cityId = this.getChoiceCity(soloEvent);
        final String battleId = NewBattleManager.getBattleId(20, playerId, cityId);
        final Battle battle = NewBattleManager.getInstance().getBattleByBatId(battleId);
        if (battle != null) {
            return JsonBuilder.getJson(State.FAIL, "");
        }
        ScenarioEvent temp = null;
        JsonDocument doc = null;
        for (final ScenarioEvent event : list) {
            if (event.getSoloEvent().getId() == eventId) {
                if (event.getState() >= 3 || event.getState() < 2) {
                    return JsonBuilder.getJson(State.FAIL, "");
                }
                final ScenarioChoice scenarioChoice = event.getChoice();
                if (scenarioChoice != null && event.getMainChoice() != 0 && !scenarioChoice.getHasFight()) {
                    temp = event;
                }
                event.setPlayerChoice(choice);
                final OperationResult result = this.doSpecialChoice(dto, scenarioChoice, choice, event, playerId);
                if (!result.getResult()) {
                    return JsonBuilder.getJson(State.FAIL, result.getResultContent());
                }
                if (result.getExtraInfo() != null) {
                    doc = new JsonDocument();
                    doc.startObject();
                    doc.createElement("choice", result.getExtraInfo());
                    doc.endObject();
                    break;
                }
                break;
            }
        }
        if (temp != null) {
            this.makeAChoiceToTriggerOperation(playerId, eventId);
        }
        if (eventId == Constants.EVENT_ID_ANSHALIUZHANG_68 && choice == 1) {
            ScenarioEventJsonBuilder.sendDialog(playerId, 8001, null);
        }
        else if (eventId == Constants.EVENT_ID_JIXIZONGMIAO_170 && dto.grade >= 3) {
            if (choice == 1) {
                ScenarioEventJsonBuilder.sendDialog(playerId, 10011, null);
            }
            else {
                ScenarioEventJsonBuilder.sendDialog(playerId, 10012, null);
            }
        }
        if (doc == null) {
            return JsonBuilder.getJson(State.SUCCESS, "");
        }
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    private OperationResult doSpecialChoice(final JuBenDto dto, final ScenarioChoice scenarioChoice, final int choice, final ScenarioEvent event, final int playerId) {
        final OperationResult result = new OperationResult(true);
        try {
            if (scenarioChoice != null) {
                final String catogery = scenarioChoice.getChoiceCatogeryByChoice();
                if (catogery.equalsIgnoreCase("multichoice")) {
                    final int multiChoice = scenarioChoice.getMultiChoiceContent(choice);
                    if (multiChoice >= 0) {
                        transferRoyalJade(dto, multiChoice);
                        event.setPlayerChoice(1);
                    }
                }
                else if (catogery.equalsIgnoreCase("randchoice")) {
                    final int number = scenarioChoice.getRandChoiceContent(choice);
                    if (number <= 0) {
                        return result;
                    }
                    final String costs = scenarioChoice.getCosts();
                    final int copper = Integer.parseInt(costs);
                    if (!this.playerResourceDao.consumeCopper(playerId, copper, "\u5267\u672c\u968f\u673a\u4e8b\u4ef6\u83b7\u53d6\u9526\u56ca\u6d88\u8017\u94f6\u5e01")) {
                        result.setResult(false);
                        result.setResultContent(LocalMessages.T_COMM_10001);
                    }
                    final int randInt = WebUtil.nextInt(number);
                    event.setPlayerChoice(randInt + 1);
                    final String choice2 = scenarioChoice.getChoice1();
                    final String[] single = choice2.split(",");
                    result.setExtraInfo(single[randInt + 1]);
                }
                else if (catogery.equalsIgnoreCase("dychoice")) {
                    final int location = dto.mengdeLocation;
                    final Set<Integer> set = this.dataGetter.getSoloRoadCache().getNeighbors(location);
                    if (set == null || !set.contains(choice)) {
                        return new OperationResult(false, LocalMessages.T_COMM_10011);
                    }
                    event.setPlayerChoice(choice);
                }
            }
        }
        catch (Exception e) {
            JuBenService.errorLog.error(this, e);
            result.setResult(false);
            result.setResultContent(LocalMessages.T_COMM_10011);
        }
        return result;
    }
    
    private static void transferRoyalJade(final JuBenDto dto, final int multiChoice) {
        if (dto == null) {
            return;
        }
        if ((dto.royalJadeBelong = multiChoice) == 0) {
            dto.royalEndTime = 0L;
        }
        else {
            dto.royalEndTime = System.currentTimeMillis() + 600000L;
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("forceId", multiChoice);
        doc.createElement("endTime", dto.royalEndTime - System.currentTimeMillis());
        doc.endObject();
        Players.push(dto.player_id, PushCommand.PUSH_JUBEN_ROYALJADE_TRANSFER, doc.toByte());
    }
    
    private int getChoiceCity(final SoloEvent soloEvent) {
        if (soloEvent == null) {
            return 0;
        }
        try {
            final String flag = soloEvent.getFlag2();
            if (StringUtils.isBlank(flag)) {
                return 0;
            }
            final String[] single = flag.split(",");
            return Integer.parseInt(single[1]);
        }
        catch (Exception e) {
            JuBenService.errorLog.error("getChoiceCity fail...eventId" + soloEvent.getId());
            JuBenService.errorLog.error(e.getMessage());
            JuBenService.errorLog.error(this, e);
            return 0;
        }
    }
    
    @Override
    public void addNpcToCity(final int playerId, final int cityId, final List<Tuple<Integer, Integer>> list, final int camp) {
        try {
            if (list == null || list.isEmpty()) {
                return;
            }
            final long start = System.currentTimeMillis();
            JuBenService.timeLog.info(LogUtil.formatThreadLog("JuBenService", "addNpcToCity", 0, 0L, "playerId:" + playerId + "cityId:" + cityId + "map:" + list.size() + "camp:" + camp));
            final JuBenDto juBenDto = JuBenManager.getInstance().getByPid(playerId);
            if (juBenDto == null) {
                ErrorSceneLog.getInstance().appendErrorMsg("JuBenDto is null").append("playerId", playerId).append("cityId", cityId).append("map size", list.size()).appendClassName("JuBenService").appendMethodName("addNpcToCity").flush();
                return;
            }
            final JuBenCityDto juBenCityDto = JuBenManager.getInstance().getJuBenCityDto(playerId, cityId);
            if (juBenCityDto == null) {
                ErrorSceneLog.getInstance().appendErrorMsg("JuBenCityDto is null").append("playerId", playerId).append("cityId", cityId).append("map size", list.size()).appendClassName("JuBenService").appendMethodName("addNpcToCity").flush();
                return;
            }
            if (camp == 0) {
                this.addNpcToCityForPlayer(playerId, cityId, list, juBenDto, juBenCityDto);
            }
            else {
                this.addNpcToCityForNpc(playerId, cityId, list, juBenDto, juBenCityDto, camp);
            }
            JuBenService.timeLog.info(LogUtil.formatThreadLog("JuBenService", "addNpcToCity", 2, System.currentTimeMillis() - start, "playerId:" + playerId + "cityId:" + cityId + "map size:" + list.size() + "camp:" + camp));
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("JuBenService.addNpcToCity catch Exception", e);
        }
    }
    
    public static int changeForceId(final int forceId) {
        return 105;
    }
    
    private void addNpcToCityForNpc(final int playerId, final int cityId, final List<Tuple<Integer, Integer>> list, final JuBenDto juBenDto, final JuBenCityDto juBenCityDto, final int camp) {
        try {
            final Player player = this.dataGetter.getPlayerDao().read(playerId);
            int forceId = 0;
            if (camp == 4) {
                forceId = changeForceId(player.getForceId());
            }
            else if (camp >= 101) {
                forceId = camp;
            }
            final int npcType = 2;
            final Date date = new Date();
            final Builder builder = BuilderFactory.getInstance().getBuilder(18);
            final String battleId = NewBattleManager.getBattleId(18, playerId, cityId);
            Battle juBenCityBattle = NewBattleManager.getInstance().getBattleByBatId(battleId);
            if (juBenCityBattle != null) {
                final int state = 19;
                final List<ScenarioNpc> snList = new LinkedList<ScenarioNpc>();
                for (final Tuple<Integer, Integer> general : list) {
                    final int armyId = general.left;
                    for (int num = general.right, i = 0; i < num; ++i) {
                        final ScenarioNpc scenarioNpc = new ScenarioNpc();
                        scenarioNpc.setVId(JuBenService.scenarioNpcVId.incrementAndGet());
                        scenarioNpc.setPlayerId(playerId);
                        scenarioNpc.setScenarioId(juBenDto.juBen_id);
                        scenarioNpc.setLocationId(cityId);
                        scenarioNpc.setForceId(forceId);
                        scenarioNpc.setNpcType(npcType);
                        scenarioNpc.setArmyId(armyId);
                        final Army army = (Army)this.dataGetter.getArmyCache().get((Object)armyId);
                        scenarioNpc.setHp(army.getArmyHp());
                        scenarioNpc.setTacticVal(1);
                        scenarioNpc.setState(state);
                        scenarioNpc.setAddTime(date);
                        snList.add(scenarioNpc);
                    }
                }
                this.dataGetter.getScenarioNpcDao().batchCreate(snList);
                final int defForceId = juBenCityBattle.getDefBaseInfo().getForceId();
                int battleSide = -1;
                if (defForceId == forceId) {
                    battleSide = 0;
                }
                else {
                    battleSide = 1;
                }
                for (final ScenarioNpc scenarioNpc2 : snList) {
                    final CampArmy tempEACa = builder.copyArmyFromScenarioNpc(this.dataGetter, player, juBenCityBattle, scenarioNpc2, battleSide);
                    juBenCityBattle.joinCampArmy(this.dataGetter, battleSide, tempEACa);
                }
            }
            else if (juBenCityDto.forceId != forceId) {
                final int state = 0;
                final List<ScenarioNpc> snList = new LinkedList<ScenarioNpc>();
                for (final Tuple<Integer, Integer> general : list) {
                    final int armyId = general.left;
                    for (int num = general.right, i = 0; i < num; ++i) {
                        final ScenarioNpc scenarioNpc = new ScenarioNpc();
                        scenarioNpc.setVId(JuBenService.scenarioNpcVId.incrementAndGet());
                        scenarioNpc.setPlayerId(playerId);
                        scenarioNpc.setScenarioId(juBenDto.juBen_id);
                        scenarioNpc.setLocationId(cityId);
                        scenarioNpc.setForceId(forceId);
                        scenarioNpc.setNpcType(npcType);
                        scenarioNpc.setArmyId(armyId);
                        final Army army = (Army)this.dataGetter.getArmyCache().get((Object)armyId);
                        scenarioNpc.setHp(army.getArmyHp());
                        scenarioNpc.setTacticVal(1);
                        scenarioNpc.setState(state);
                        scenarioNpc.setAddTime(date);
                        snList.add(scenarioNpc);
                    }
                }
                this.dataGetter.getScenarioNpcDao().batchCreate(snList);
                final Terrain terrain = builder.getTerrain(-1, cityId, this.dataGetter);
                juBenCityBattle = NewBattleManager.getInstance().createBattle(battleId);
                if (juBenCityBattle == null) {
                    ErrorSceneLog.getInstance().appendErrorMsg("create battle fail").appendBattleId(battleId).append("playerId", playerId).append("cityId", cityId).append("map size", list.size()).appendClassName("JuBenService").appendMethodName("addNpcToCity").flush();
                    return;
                }
                final BattleAttacker battleAttacker = new BattleAttacker();
                battleAttacker.attType = 7;
                battleAttacker.attForceId = forceId;
                battleAttacker.attPlayerId = playerId;
                battleAttacker.attPlayer = player;
                juBenCityBattle.init(battleAttacker, 18, cityId, this.dataGetter, false, terrain.getValue());
                builder.dealUniqueStaff(this.dataGetter, juBenCityBattle, -1, cityId);
            }
            else {
                final int state = 0;
                final List<ScenarioNpc> snList = new LinkedList<ScenarioNpc>();
                for (final Tuple<Integer, Integer> general : list) {
                    final int armyId = general.left;
                    for (int num = general.right, i = 0; i < num; ++i) {
                        final ScenarioNpc scenarioNpc = new ScenarioNpc();
                        scenarioNpc.setVId(JuBenService.scenarioNpcVId.incrementAndGet());
                        scenarioNpc.setPlayerId(playerId);
                        scenarioNpc.setScenarioId(juBenDto.juBen_id);
                        scenarioNpc.setLocationId(cityId);
                        scenarioNpc.setForceId(forceId);
                        scenarioNpc.setNpcType(npcType);
                        scenarioNpc.setArmyId(armyId);
                        final Army army = (Army)this.dataGetter.getArmyCache().get((Object)armyId);
                        scenarioNpc.setHp(army.getArmyHp());
                        scenarioNpc.setTacticVal(1);
                        scenarioNpc.setState(state);
                        scenarioNpc.setAddTime(date);
                        snList.add(scenarioNpc);
                    }
                }
                this.dataGetter.getScenarioNpcDao().batchCreate(snList);
            }
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("JuBenService.addNpcToCityForNpc catch Exception", e);
        }
    }
    
    private void addNpcToCityForPlayer(final int playerId, final int cityId, final List<Tuple<Integer, Integer>> list, final JuBenDto juBenDto, final JuBenCityDto juBenCityDto) {
        try {
            final Player player = this.dataGetter.getPlayerDao().read(playerId);
            final int forceId = player.getForceId();
            final int npcType = 2;
            final Date date = new Date();
            final Builder builder = BuilderFactory.getInstance().getBuilder(18);
            final String battleId = NewBattleManager.getBattleId(18, playerId, cityId);
            Battle juBenCityBattle = NewBattleManager.getInstance().getBattleByBatId(battleId);
            if (juBenCityBattle != null) {
                final int state = 19;
                final List<ScenarioNpc> snList = new LinkedList<ScenarioNpc>();
                for (final Tuple<Integer, Integer> general : list) {
                    final int generalId = general.left;
                    for (int num = general.right, i = 0; i < num; ++i) {
                        final ScenarioNpc scenarioNpc = new ScenarioNpc();
                        scenarioNpc.setVId(JuBenService.scenarioNpcVId.incrementAndGet());
                        scenarioNpc.setPlayerId(playerId);
                        scenarioNpc.setScenarioId(juBenDto.juBen_id);
                        scenarioNpc.setLocationId(cityId);
                        scenarioNpc.setForceId(forceId);
                        scenarioNpc.setNpcType(npcType);
                        scenarioNpc.setArmyId(generalId);
                        final Army army = (Army)this.dataGetter.getArmyCache().get((Object)generalId);
                        scenarioNpc.setHp(army.getArmyHp());
                        scenarioNpc.setTacticVal(1);
                        scenarioNpc.setState(state);
                        scenarioNpc.setAddTime(date);
                        snList.add(scenarioNpc);
                    }
                }
                this.dataGetter.getScenarioNpcDao().batchCreate(snList);
                final int defForceId = juBenCityBattle.getDefBaseInfo().getForceId();
                int battleSide = -1;
                if (defForceId == forceId) {
                    battleSide = 0;
                }
                else {
                    battleSide = 1;
                }
                for (final ScenarioNpc scenarioNpc2 : snList) {
                    final CampArmy tempEACa = builder.copyArmyFromScenarioNpc(this.dataGetter, player, juBenCityBattle, scenarioNpc2, battleSide);
                    juBenCityBattle.joinCampArmy(this.dataGetter, battleSide, tempEACa);
                }
            }
            else if (juBenCityDto.forceId != forceId) {
                final int state = 0;
                final List<ScenarioNpc> snList = new LinkedList<ScenarioNpc>();
                for (final Tuple<Integer, Integer> general : list) {
                    final int armyId = general.left;
                    for (int num = general.right, i = 0; i < num; ++i) {
                        final ScenarioNpc scenarioNpc = new ScenarioNpc();
                        scenarioNpc.setVId(JuBenService.scenarioNpcVId.incrementAndGet());
                        scenarioNpc.setPlayerId(playerId);
                        scenarioNpc.setScenarioId(juBenDto.juBen_id);
                        scenarioNpc.setLocationId(cityId);
                        scenarioNpc.setForceId(forceId);
                        scenarioNpc.setNpcType(npcType);
                        scenarioNpc.setArmyId(armyId);
                        final Army army = (Army)this.dataGetter.getArmyCache().get((Object)armyId);
                        scenarioNpc.setHp(army.getArmyHp());
                        scenarioNpc.setTacticVal(1);
                        scenarioNpc.setState(state);
                        scenarioNpc.setAddTime(date);
                        snList.add(scenarioNpc);
                    }
                }
                this.dataGetter.getScenarioNpcDao().batchCreate(snList);
                final Terrain terrain = builder.getTerrain(-1, cityId, this.dataGetter);
                juBenCityBattle = NewBattleManager.getInstance().createBattle(battleId);
                if (juBenCityBattle == null) {
                    ErrorSceneLog.getInstance().appendErrorMsg("create battle fail").appendBattleId(battleId).append("playerId", playerId).append("cityId", cityId).append("map size", list.size()).appendClassName("JuBenService").appendMethodName("addNpcToCity").flush();
                    return;
                }
                final BattleAttacker battleAttacker = new BattleAttacker();
                battleAttacker.attType = 7;
                battleAttacker.attForceId = forceId;
                battleAttacker.attPlayerId = playerId;
                battleAttacker.attPlayer = player;
                juBenCityBattle.init(battleAttacker, 18, cityId, this.dataGetter, false, terrain.getValue());
                builder.dealUniqueStaff(this.dataGetter, juBenCityBattle, -1, cityId);
            }
            else {
                final int state = 0;
                final List<ScenarioNpc> snList = new LinkedList<ScenarioNpc>();
                for (final Tuple<Integer, Integer> general : list) {
                    final int armyId = general.left;
                    for (int num = general.right, i = 0; i < num; ++i) {
                        final ScenarioNpc scenarioNpc = new ScenarioNpc();
                        scenarioNpc.setVId(JuBenService.scenarioNpcVId.incrementAndGet());
                        scenarioNpc.setPlayerId(playerId);
                        scenarioNpc.setScenarioId(juBenDto.juBen_id);
                        scenarioNpc.setLocationId(cityId);
                        scenarioNpc.setForceId(forceId);
                        scenarioNpc.setNpcType(npcType);
                        scenarioNpc.setArmyId(armyId);
                        final Army army = (Army)this.dataGetter.getArmyCache().get((Object)armyId);
                        scenarioNpc.setHp(army.getArmyHp());
                        scenarioNpc.setTacticVal(1);
                        scenarioNpc.setState(state);
                        scenarioNpc.setAddTime(date);
                        snList.add(scenarioNpc);
                    }
                }
                this.dataGetter.getScenarioNpcDao().batchCreate(snList);
            }
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("JuBenService.addNpcToCityForPlayer catch Exception", e);
        }
    }
    
    @Override
    public void removeNpcInCity(final int playerId, final Integer cityId, final int armyId, final int num, final int camp) {
        final long start = System.currentTimeMillis();
        try {
            JuBenService.timerLog.info(LogUtil.formatThreadLog("JuBenService", "removeNpcInCity", 0, 0L, "playerId:" + playerId + "cityId:" + cityId + "armyId:" + armyId + "num:" + num + "camp:" + camp));
            final JuBenDto juBenDto = JuBenManager.getInstance().getByPid(playerId);
            if (juBenDto == null) {
                ErrorSceneLog.getInstance().appendErrorMsg("JuBenDto is null").append("playerId", playerId).append("cityId", cityId).append("armyId", armyId).append("num", num).appendClassName("JuBenService").appendMethodName("addNpcToCity").flush();
                return;
            }
            final JuBenCityDto juBenCityDto = JuBenManager.getInstance().getJuBenCityDto(playerId, cityId);
            if (juBenCityDto == null) {
                ErrorSceneLog.getInstance().appendErrorMsg("JuBenCityDto is null").append("playerId", playerId).append("cityId", cityId).append("armyId", armyId).append("num", num).appendClassName("JuBenService").appendMethodName("addNpcToCity").flush();
                return;
            }
            int forceId = 0;
            if (camp == 0) {
                final Player player = this.dataGetter.getPlayerDao().read(playerId);
                forceId = player.getForceId();
            }
            else if (camp == 1) {
                forceId = 0;
            }
            else if (camp >= 101) {
                forceId = camp;
            }
            this.doremoveNpcInCity(playerId, cityId, armyId, num, juBenDto, juBenCityDto, forceId);
            JuBenService.timerLog.info(LogUtil.formatThreadLog("JuBenService", "removeNpcInCity", 2, System.currentTimeMillis() - start, "playerId:" + playerId + "cityId:" + cityId + "armyId:" + armyId + "num:" + num + "camp:" + camp));
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("JuBenService.removeNpcInCity catch Exception", e);
        }
    }
    
    private void doremoveNpcInCity(final int playerId, final Integer cityId, final int armyId, final int num, final JuBenDto juBenDto, final JuBenCityDto juBenCityDto, final int forceId) {
        try {
            final String battleId = NewBattleManager.getBattleId(18, playerId, cityId);
            final Battle juBenCityBattle = NewBattleManager.getInstance().getBattleByBatId(battleId);
            if (juBenCityBattle != null) {
                JuBenService.timerLog.info(LogUtil.formatThreadLog("JuBenService", "doremoveNpcInCity", 0, 0L, "playerId:" + playerId + "cityId:" + cityId + "armyId:" + armyId + "num:" + num + "forceId:" + forceId + "battleId:" + battleId));
                List<CampArmy> CAList = null;
                if (juBenCityBattle.getAttBaseInfo().getForceId() == forceId) {
                    CAList = juBenCityBattle.getAttCamp();
                }
                else {
                    if (juBenCityBattle.getDefBaseInfo().getForceId() != forceId) {
                        ErrorSceneLog.getInstance().appendErrorMsg("JuBenService.doremoveNpcInCity battle force error").appendPlayerId(playerId).append("cityId", cityId).append("armyId", armyId).append("juBenCityBattle.getAttBaseInfo().getForceId()", juBenCityBattle.getAttBaseInfo().getForceId()).append("juBenCityBattle.getDefBaseInfo().getForceId()", juBenCityBattle.getDefBaseInfo().getForceId()).flush();
                        return;
                    }
                    CAList = juBenCityBattle.getDefCamp();
                }
                final List<CampArmy> removeCaList = new LinkedList<CampArmy>();
                synchronized (juBenCityBattle.getBattleId()) {
                    for (final CampArmy ca : CAList) {
                        if (ca.isOnQueues()) {
                            continue;
                        }
                        if (ca.getGeneralId() != armyId) {
                            continue;
                        }
                        if (ca.scenarioArmyType != 2) {
                            continue;
                        }
                        removeCaList.add(ca);
                        if (removeCaList.size() >= num) {
                            break;
                        }
                    }
                    for (final CampArmy ca : removeCaList) {
                        CAList.remove(ca);
                        final int done = this.dataGetter.getScenarioNpcDao().deleteById(ca.getPgmVId());
                        if (done != 1) {
                            ErrorSceneLog.getInstance().appendErrorMsg("JuBenService.doremoveNpcInCity fail when battle").appendPlayerId(playerId).append("cityId", cityId).append("armyId", armyId).append("num", num).appendBattleId(battleId).append("vId", ca.getPgmVId()).flush();
                        }
                    }
                    // monitorexit(juBenCityBattle.getBattleId())
                    return;
                }
            }
            final List<ScenarioNpc> scList = this.dataGetter.getScenarioNpcDao().getByPlayerIdLocationId(playerId, cityId);
            final List<ScenarioNpc> removeSCList = new LinkedList<ScenarioNpc>();
            for (final ScenarioNpc sc : scList) {
                if (sc.getForceId() == forceId && sc.getArmyId() == armyId) {
                    removeSCList.add(sc);
                    if (removeSCList.size() >= num) {
                        break;
                    }
                    continue;
                }
            }
            for (final ScenarioNpc removeSC : removeSCList) {
                final int vId = removeSC.getVId();
                final int done = this.dataGetter.getScenarioNpcDao().deleteById(vId);
                if (done != 1) {
                    ErrorSceneLog.getInstance().appendErrorMsg("JuBenService.doremoveNpcInCity fail when free").appendPlayerId(playerId).append("cityId", cityId).append("armyId", armyId).append("num", num).append("vId", vId).flush();
                }
            }
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("JuBenService.removeNpcInCityOfNpc catch Exception", e);
        }
    }
    
    @Override
    public void killAllPlayerPgmsInThisCity(final int playerId, final int cityId) {
        try {
            final JuBenDto juBenDto = JuBenManager.getInstance().getByPid(playerId);
            if (juBenDto == null) {
                ErrorSceneLog.getInstance().appendErrorMsg("JuBenDto is null").append("playerId", playerId).append("cityId", cityId).appendClassName("JuBenService").appendMethodName("killAllPlayerPgmsInThisCity").flush();
                return;
            }
            final JuBenCityDto juBenCityDto = JuBenManager.getInstance().getJuBenCityDto(playerId, cityId);
            if (juBenCityDto == null) {
                ErrorSceneLog.getInstance().appendErrorMsg("JuBenCityDto is null").append("playerId", playerId).append("cityId", cityId).appendClassName("JuBenService").appendMethodName("killAllPlayerPgmsInThisCity").flush();
                return;
            }
            final Player player = this.dataGetter.getPlayerDao().read(playerId);
            final String battleId = NewBattleManager.getBattleId(18, playerId, cityId);
            final Battle juBenCityBattle = NewBattleManager.getInstance().getBattleByBatId(battleId);
            if (juBenCityBattle != null) {
                this.clearBattlesInThisCity(player, juBenCityDto, true);
            }
            else {
                this.dataGetter.getJuBenService().changeForceIdAndState(cityId, 0, 0, playerId, null);
            }
            final int capitalId = juBenDto.capital;
            final List<PlayerGeneralMilitary> pgmList = this.dataGetter.getPlayerGeneralMilitaryDao().getMilitaryList(playerId);
            for (final PlayerGeneralMilitary pgm : pgmList) {
                if (pgm.getJubenLoId() == cityId) {
                    final int done = this.dataGetter.getPlayerGeneralMilitaryDao().upJuBenLocationForceSetState1(pgm.getPlayerId(), pgm.getGeneralId(), capitalId, pgm.getForces(), new Date());
                    if (done == 1) {
                        this.dataGetter.getGeneralService().sendGeneralMilitaryRecruitInfo(pgm.getPlayerId(), pgm.getGeneralId());
                        ScenarioEventMessageHelper.sendMoveToCitykMessage(pgm.getPlayerId(), pgm.getGeneralId(), capitalId);
                    }
                    else {
                        ErrorSceneLog.getInstance().appendErrorMsg("updateLocationForceSetState1 fail").appendPlayerId(pgm.getPlayerId()).appendGeneralId(pgm.getGeneralId()).append("capitalId", capitalId).append("pgm.getForces().intValue()", pgm.getForces()).appendClassName("JuBenService").appendMethodName("killAllPlayerPgmsInThisCity").flush();
                    }
                }
            }
            final List<ScenarioNpc> snList = this.dataGetter.getScenarioNpcDao().getByPlayerIdLocationId(playerId, cityId);
            for (final ScenarioNpc scenarioNpc : snList) {
                if (scenarioNpc.getForceId() == player.getForceId()) {
                    this.dataGetter.getScenarioNpcDao().deleteById(scenarioNpc.getVId());
                }
            }
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("JuBenService.killAllPlayerPgmsInThisCity catch Exception", e);
        }
    }
    
    @Override
    public void killAllNpcsInThisCity(final int playerId, final int cityId) {
        try {
            final JuBenDto juBenDto = JuBenManager.getInstance().getByPid(playerId);
            if (juBenDto == null) {
                ErrorSceneLog.getInstance().appendErrorMsg("JuBenDto is null").append("playerId", playerId).append("cityId", cityId).appendClassName("JuBenService").appendMethodName("killAllPlayerPgmsInThisCity").flush();
                return;
            }
            final JuBenCityDto juBenCityDto = JuBenManager.getInstance().getJuBenCityDto(playerId, cityId);
            if (juBenCityDto == null) {
                ErrorSceneLog.getInstance().appendErrorMsg("JuBenCityDto is null").append("playerId", playerId).append("cityId", cityId).appendClassName("JuBenService").appendMethodName("killAllPlayerPgmsInThisCity").flush();
                return;
            }
            final Player player = this.dataGetter.getPlayerDao().read(playerId);
            final String battleId = NewBattleManager.getBattleId(18, playerId, cityId);
            final Battle juBenCityBattle = NewBattleManager.getInstance().getBattleByBatId(battleId);
            if (juBenCityBattle != null) {
                this.clearBattlesInThisCity(player, juBenCityDto, false);
            }
            else {
                this.dataGetter.getJuBenService().changeForceIdAndState(cityId, player.getForceId(), 0, playerId, null);
            }
            final List<ScenarioNpc> snList = this.dataGetter.getScenarioNpcDao().getByPlayerIdLocationId(playerId, cityId);
            for (final ScenarioNpc scenarioNpc : snList) {
                if (scenarioNpc.getForceId() == 0) {
                    this.dataGetter.getScenarioNpcDao().deleteById(scenarioNpc.getVId());
                }
            }
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("JuBenService.killAllNpcsInThisCity catch Exception", e);
        }
    }
    
    @Override
    public void makeAChoiceToTriggerOperation(final int playerId, final int eventId) {
        List<ScenarioEvent> list = null;
        final JuBenDto dto = JuBenManager.getInstance().getByPid(playerId);
        if (dto == null) {
            return;
        }
        list = dto.eventList;
        if (list == null) {
            return;
        }
        for (final ScenarioEvent event : list) {
            if (event.getSoloEvent().getId() == eventId) {
                if (event.getMainChoice() == 0) {
                    return;
                }
                final ScenarioChoice choice = event.getChoice();
                if (choice == null) {
                    return;
                }
                if (event.getState() != 2) {
                    return;
                }
                if (choice.getChoiceCatogery().equalsIgnoreCase("dychoice")) {
                    event.setCurChoice(1);
                    break;
                }
                event.setCurChoice(event.getPlayerChoice());
                break;
            }
        }
    }
    
    @Override
    public int trickReduceForce(final int playerId, final int cityId, final int reduceEach, final int num) {
        try {
            final Player player = this.dataGetter.getPlayerDao().read(playerId);
            JuBenService.timerLog.info("JuBenService.trickReduceForce start. playerId:" + playerId + "PlayerName:" + player.getPlayerName() + "cityId:" + cityId + "reduceEach:" + reduceEach + "num:" + num);
            final JuBenDto juBenDto = JuBenManager.getInstance().getByPid(playerId);
            if (juBenDto == null) {
                ErrorSceneLog.getInstance().appendErrorMsg("JuBenDto is null").append("playerId", playerId).append("cityId", cityId).appendClassName("JuBenService").appendMethodName("trickReduceForce").flush();
                return 0;
            }
            final JuBenCityDto juBenCityDto = JuBenManager.getInstance().getJuBenCityDto(playerId, cityId);
            if (juBenCityDto == null) {
                ErrorSceneLog.getInstance().appendErrorMsg("JuBenCityDto is null").append("playerId", playerId).append("cityId", cityId).appendClassName("JuBenService").appendMethodName("trickReduceForce").flush();
                return 0;
            }
            final String battleId = NewBattleManager.getBattleId(18, playerId, cityId);
            final Battle juBenCityBattle = NewBattleManager.getInstance().getBattleByBatId(battleId);
            int result = 0;
            boolean allonQueue = false;
            if (juBenCityBattle != null) {
                final Tuple<Integer, Boolean> tuple = this.trickReduceForceBattle(player, juBenCityBattle, juBenDto, cityId, reduceEach, num);
                result = tuple.left;
                allonQueue = tuple.right;
            }
            else {
                result = this.trickReduceForceFree(player, juBenDto, cityId, reduceEach, num);
            }
            JuBenService.timerLog.info("JuBenService.trickReduceForce end. result:" + result + ", allonQueue:" + allonQueue);
            return result;
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("JuBenService.trickReduceForce catch Exception", e);
            return 0;
        }
    }
    
    private int trickReduceForceFree(final Player player, final JuBenDto juBenDto, final int cityId, final int reduceEach, final int num) {
        int reduceSum = 0;
        int reducedCount = 0;
        final List<PlayerGeneralMilitary> pgmList = this.dataGetter.getPlayerGeneralMilitaryDao().getMilitaryList(player.getPlayerId());
        for (final PlayerGeneralMilitary pgm : pgmList) {
            if (pgm.getJubenLoId() != cityId) {
                continue;
            }
            int reduce = 0;
            if (pgm.getForces() > reduceEach) {
                reduce = reduceEach;
                this.dataGetter.getPlayerGeneralMilitaryDao().consumeForces(pgm.getPlayerId(), pgm.getGeneralId(), reduce, new Date());
            }
            else {
                reduce = pgm.getForces();
                this.dataGetter.getPlayerGeneralMilitaryDao().consumeForces(pgm.getPlayerId(), pgm.getGeneralId(), reduce, new Date());
                this.dataGetter.getPlayerGeneralMilitaryDao().moveJuben(pgm.getPlayerId(), pgm.getGeneralId(), 1, juBenDto.capital);
                ScenarioEventMessageHelper.sendMoveToCitykMessage(pgm.getPlayerId(), pgm.getGeneralId(), juBenDto.capital);
            }
            this.dataGetter.getGeneralService().sendGeneralMilitaryRecruitInfo(pgm.getPlayerId(), pgm.getGeneralId());
            reduceSum += reduce;
            if (++reducedCount >= num) {
                return reduceSum;
            }
        }
        final List<ScenarioNpc> snList = this.dataGetter.getScenarioNpcDao().getByPlayerIdLocationId(player.getPlayerId(), cityId);
        for (final ScenarioNpc scenarioNpc : snList) {
            if (scenarioNpc.getForceId() == player.getForceId()) {
                int reduce2 = 0;
                if (scenarioNpc.getHp() > reduceEach) {
                    reduce2 = reduceEach;
                    this.dataGetter.getScenarioNpcDao().updateHpAndTacticVal(scenarioNpc.getVId(), scenarioNpc.getHp() - reduce2, scenarioNpc.getTacticVal());
                }
                else {
                    reduce2 = scenarioNpc.getHp();
                    this.dataGetter.getScenarioNpcDao().deleteById(scenarioNpc.getVId());
                }
                reduceSum += reduce2;
                if (++reducedCount >= num) {
                    return reduceSum;
                }
                continue;
            }
        }
        return reduceSum;
    }
    
    private Tuple<Integer, Boolean> trickReduceForceBattle(final Player player, final Battle juBenCityBattle, final JuBenDto juBenDto, final int cityId, int reduceEach, final int num) {
        boolean allOnqueue = true;
        int reduceSum = 0;
        synchronized (juBenCityBattle.getBattleId()) {
            LinkedList<CampArmy> campList = null;
            final LinkedList<CampArmy> deadCampList = new LinkedList<CampArmy>();
            BaseInfo baseInfo = null;
            if (juBenCityBattle.getAttBaseInfo().getForceId() == player.getForceId()) {
                campList = juBenCityBattle.getAttCamp();
                baseInfo = juBenCityBattle.getAttBaseInfo();
            }
            else {
                if (juBenCityBattle.getDefBaseInfo().getForceId() != player.getForceId()) {
                    ErrorSceneLog.getInstance().appendErrorMsg("no side is npc").appendBattleId(juBenCityBattle.getBattleId()).append("AttBase forceId", juBenCityBattle.getAttBaseInfo().getForceId()).append("DefBase forceId", juBenCityBattle.getDefBaseInfo().getForceId()).append("cityId", cityId).appendPlayerId(player.getPlayerId()).appendPlayerName(player.getPlayerName()).appendClassName("JuBenService").appendMethodName("trickReduceForceBattle").flush();
                    // monitorexit(juBenCityBattle.getBattleId())
                    return null;
                }
                campList = juBenCityBattle.getDefCamp();
                baseInfo = juBenCityBattle.getDefBaseInfo();
            }
            final int remainder = reduceEach % 3;
            reduceEach -= remainder;
            int reducedCount = 0;
            for (final CampArmy temp : campList) {
                if (temp.isOnQueues()) {
                    continue;
                }
                allOnqueue = false;
                int reduce = 0;
                if (temp.getArmyHp() > reduceEach) {
                    reduce = reduceEach;
                    if (temp.scenarioArmyType == 0 && temp.getPlayerId() > 0) {
                        this.dataGetter.getPlayerGeneralMilitaryDao().consumeForces(temp.getPlayerId(), temp.getGeneralId(), reduce, new Date());
                        this.dataGetter.getGeneralService().sendGeneralMilitaryRecruitInfo(temp.getPlayerId(), temp.getGeneralId());
                    }
                    else {
                        this.dataGetter.getScenarioNpcDao().updateHpAndTacticVal(temp.getPgmVId(), temp.getArmyHp() - reduce, temp.getTacticVal());
                    }
                }
                else {
                    reduce = temp.getArmyHp();
                    if (temp.scenarioArmyType == 0 && temp.getPlayerId() > 0) {
                        this.dataGetter.getPlayerGeneralMilitaryDao().consumeForces(temp.getPlayerId(), temp.getGeneralId(), reduce, new Date());
                        this.dataGetter.getPlayerGeneralMilitaryDao().moveJuben(temp.getPlayerId(), temp.getGeneralId(), 1, juBenDto.capital);
                        ScenarioEventMessageHelper.sendMoveToCitykMessage(temp.getPlayerId(), temp.getGeneralId(), juBenDto.capital);
                        this.dataGetter.getGeneralService().sendGeneralMilitaryRecruitInfo(temp.getPlayerId(), temp.getGeneralId());
                    }
                    else {
                        this.dataGetter.getScenarioNpcDao().deleteById(temp.getPgmVId());
                    }
                    deadCampList.add(temp);
                }
                temp.setArmyHp(temp.getArmyHp() - reduce);
                temp.setArmyHpLoss(temp.getArmyHpLoss() + reduce);
                baseInfo.setNum(baseInfo.getNum() - reduce);
                reduceSum += reduce;
                if (++reducedCount >= num) {
                    break;
                }
            }
            for (final CampArmy temp : deadCampList) {
                campList.remove(temp);
            }
        }
        // monitorexit(juBenCityBattle.getBattleId())
        final Tuple<Integer, Boolean> resultTuple = new Tuple();
        resultTuple.left = reduceSum;
        resultTuple.right = allOnqueue;
        return resultTuple;
    }
    
    @Override
    public ScenarioEvent getEventByCityId(final int playerId, final int cityId) {
        final JuBenDto dto = JuBenManager.getInstance().getByPid(playerId);
        if (dto == null || dto.eventList == null) {
            return null;
        }
        for (final ScenarioEvent event : dto.eventList) {
            if (event == null) {
                continue;
            }
            if (event.getMainChoice() == 0) {
                continue;
            }
            if (event.getPlayerChoice() == 0) {
                continue;
            }
            final ScenarioChoice choice = event.getChoice();
            if (choice == null) {
                continue;
            }
            final int choiceCity = choice.getChoiceCity();
            if (choiceCity != cityId) {
                continue;
            }
            if (event.getState() >= 3) {
                continue;
            }
            return event;
        }
        return null;
    }
    
    @Override
    public void setJubenBuff(final int playerId, final double value, long duration, final int type) {
        try {
            if (type != 1 && type != 2) {
                ErrorSceneLog.getInstance().appendErrorMsg("type is invalid").appendPlayerId(playerId).append("type", type).appendClassName("JuBenService").appendMethodName("setJubenPlayerBuff").flush();
                return;
            }
            final JuBenDto juBenDto = JuBenManager.getInstance().getByPid(playerId);
            if (juBenDto == null) {
                ErrorSceneLog.getInstance().appendErrorMsg("juBenDto is null").appendPlayerId(playerId).appendClassName("JuBenService").appendMethodName("setJubenPlayerBuff").flush();
                return;
            }
            if (duration == 0L) {
                duration = 0L;
            }
            else {
                duration += System.currentTimeMillis();
            }
            if (type == 1) {
                juBenDto.juben_buff_player = value;
                juBenDto.juben_buff_player_duration_time = duration;
            }
            else {
                if (type != 2) {
                    return;
                }
                juBenDto.juben_buff_npc = value;
                juBenDto.juben_buff_npc_duration_time = duration;
            }
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("JuBenService.setJubenPlayerBuff catch Exception", e);
        }
    }
    
    @Override
    public void setJubenAttDefBaseBuff(final int playerId, final int attBase, final int defBase, long duration, final int type) {
        try {
            if (type != 1 && type != 2) {
                ErrorSceneLog.getInstance().appendErrorMsg("type is invalid").appendPlayerId(playerId).append("type", type).appendClassName("JuBenService").appendMethodName("setJubenAttDefBaseBuff").flush();
                return;
            }
            if (attBase <= 0 && defBase <= 0) {
                ErrorSceneLog.getInstance().appendErrorMsg("attBase or defBase is invalid").appendPlayerId(playerId).append("attBase", attBase).append("defBase", defBase).appendClassName("JuBenService").appendMethodName("setJubenAttDefBaseBuff").flush();
                return;
            }
            final JuBenDto juBenDto = JuBenManager.getInstance().getByPid(playerId);
            if (juBenDto == null) {
                ErrorSceneLog.getInstance().appendErrorMsg("juBenDto is null").appendPlayerId(playerId).appendClassName("JuBenService").appendMethodName("setJubenAttDefBaseBuff").flush();
                return;
            }
            if (duration == 0L) {
                duration = 0L;
            }
            else {
                duration += System.currentTimeMillis();
            }
            if (type == 1) {
                juBenDto.juben_att_base_buff_player = attBase;
                juBenDto.juben_def_base_buff_player = defBase;
                juBenDto.juben_att_def_base_buff_player_duration_time = duration;
            }
            else {
                if (type != 2) {
                    return;
                }
                juBenDto.juben_att_base_buff_npc = attBase;
                juBenDto.juben_def_base_buff_npc = defBase;
                juBenDto.juben_att_def_base_buff_npc_duration_time = duration;
            }
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("JuBenService.setJubenPlayerBuff catch Exception", e);
        }
    }
    
    @Override
    public void initScenarioNpcVid() {
        JuBenService.scenarioNpcVId.set(this.dataGetter.getScenarioNpcDao().getMaxVid());
    }
    
    @Override
    public void checkRoyalJadeRobbed(final Battle bat) {
        if (bat == null) {
            return;
        }
        try {
            final CampArmy army = bat.getAttList().get(0).getCampArmy();
            if (army.getPlayerId() <= 0) {
                return;
            }
            final JuBenDto dto = JuBenManager.getInstance().getByPid(army.getPlayerId());
            if (dto == null || dto.royalJadeBelong <= 0) {
                return;
            }
            final int defForceId = bat.getDefBaseInfo().getForceId();
            final Integer capitalId = JubenConstans.map.get(defForceId);
            if (capitalId == null) {
                return;
            }
            final int cityId = bat.getDefBaseInfo().getId();
            if (capitalId != cityId) {
                return;
            }
            final SoloCity soloCity = (SoloCity)this.soloCityCache.get((Object)cityId);
            if (soloCity == null) {
                return;
            }
            if (dto.royalJadeBelong != soloCity.getBelong()) {
                return;
            }
            transferRoyalJade(dto, 0);
        }
        catch (Exception e) {
            JuBenService.errorLog.error(this, e);
        }
    }
    
    @Override
    public void checkJadeTimeOver(final JuBenDto dto) {
        try {
            if (dto == null || dto.juBen_id != 10) {
                return;
            }
            final long now = System.currentTimeMillis();
            if (dto.royalJadeBelong != 0 && now >= dto.royalEndTime && dto.royalJadeBelong != -1) {
                ScenarioEventJsonBuilder.sendDialog(dto.player_id, 10006, JubenConstans.forceId2NameMap.get(dto.royalJadeBelong));
                this.juBenOver(dto.player_id, dto, false);
            }
        }
        catch (Exception e) {
            JuBenService.errorLog.error(this, e);
        }
    }
    
    @Override
    public void checkWorldDramaOpen(final int playerId, final int playerLv, final boolean isGm) {
        try {
            if (playerLv == this.dataGetter.getWdSjpDramaCache().getMinWorldDramaLv()) {
                CityEventManager.getInstance().addPlayerEvent(playerId, 5);
                CityEventManager.getInstance().bobaoOnePlayerEvent(playerId, 5);
            }
        }
        catch (Exception e) {
            JuBenService.errorLog.error(this, e);
        }
    }
    
    @Override
    public OperationResult enterWorldDramaScene(final PlayerDto playerDto, final int sId, final Integer grade) {
        try {
            final int playerId = playerDto.playerId;
            JuBenDto juBenDto = JuBenManager.getInstance().getByPid(playerId);
            if (juBenDto != null) {
                return new OperationResult(false, "");
            }
            PlayerScenario ps = this.playerScenarioDao.getScenarioByPidSid(playerId, sId);
            if (ps == null) {
                final WdSjpDrama drama = this.dataGetter.getWdSjpDramaCache().getWorldDramaByDramaIdAndGrade(sId, grade);
                boolean open = false;
                if (drama != null) {
                    final int techId = drama.getOpenTech();
                    final PlayerTech playerTech = this.dataGetter.getPlayerTechDao().getPlayerTech(playerId, techId);
                    if (playerTech.getStatus() >= 5) {
                        open = this.openNextJuBen(playerId, sId);
                    }
                }
                if (!open) {
                    return new OperationResult(false, LocalMessages.T_COMM_10020);
                }
                ps = this.playerScenarioDao.getScenarioByPidSid(playerId, sId);
            }
            if (ps.getState() != 0) {
                return new OperationResult(false, LocalMessages.JUBEN_CANNOT_JOIN);
            }
            final List<PlayerGeneralMilitary> pgmList = this.playerGeneralMilitaryDao.getMilitaryList(playerId);
            int jubenLoId = 0;
            final Map<Integer, SoloCity> map = this.soloCityCache.getBySoloId(sId);
            for (final SoloCity sc : map.values()) {
                if (sc.getCapital() == 1 && sc.getBelong() == 0) {
                    jubenLoId = sc.getId();
                }
            }
            final int locationId = WorldCityCommon.nationMainCityIdMap.get(playerDto.forceId);
            final int res = this.playerGeneralMilitaryDao.updateJuBenLocation(playerDto.playerId, locationId, jubenLoId);
            if (res != pgmList.size()) {
                return new OperationResult(false, LocalMessages.JUBEN_CANNT_JOIN_GENREAL_BUSY);
            }
            this.updateJuBen(playerId, sId, grade);
            this.initJuBenNpcs(playerDto, sId, grade);
            this.generalService.sendGeneralMilitaryList(playerId);
            final JsonDocument doc1 = new JsonDocument();
            doc1.startObject();
            doc1.createElement("inJuBen", true);
            doc1.endObject();
            Players.push(playerId, PushCommand.PUSH_UPDATE, doc1.toByte());
            juBenDto = this.cacheJuBen(playerId, sId);
            return new OperationResult(true);
        }
        catch (Exception e) {
            JuBenService.errorLog.error(this, e);
            return new OperationResult(false, LocalMessages.T_COMM_10011);
        }
    }
    
    @Override
    public boolean isInWorldDrama(final int playerId) {
        final JuBenDto dto = JuBenManager.getInstance().getByPid(playerId);
        return dto != null && this.isWorldDrama(dto.juBen_id);
    }
}
