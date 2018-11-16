package com.reign.gcld.battle.service;

import org.springframework.stereotype.*;
import java.util.concurrent.atomic.*;
import java.util.concurrent.locks.*;
import com.reign.gcld.job.service.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.battle.dao.*;
import com.reign.gcld.player.dao.*;
import com.reign.gcld.rank.dao.*;
import com.reign.gcld.sdata.cache.*;
import com.reign.gcld.common.log.*;
import java.util.concurrent.*;
import com.reign.framework.json.*;
import com.reign.gcld.general.domain.*;
import com.reign.gcld.player.dto.*;
import org.springframework.transaction.annotation.*;
import com.reign.gcld.general.dto.*;
import com.reign.gcld.battle.scene.*;
import com.reign.gcld.log.*;
import org.apache.commons.lang.*;
import com.reign.gcld.world.domain.*;
import com.reign.gcld.scenario.message.*;
import com.reign.util.*;
import com.reign.gcld.chat.common.*;
import com.reign.gcld.kfwd.common.*;
import com.reign.gcld.store.domain.*;
import com.reign.gcld.juben.service.*;
import com.reign.gcld.juben.common.*;
import com.reign.gcld.phantom.common.*;
import com.reign.gcld.common.util.*;
import com.reign.gcld.tech.domain.*;
import com.reign.gcld.rank.service.*;
import com.reign.gcld.team.service.*;
import com.reign.gcld.battle.reward.*;
import java.util.*;
import com.reign.gcld.common.*;
import com.reign.gcld.world.service.*;
import com.reign.gcld.rank.domain.*;
import org.springframework.beans.*;
import com.reign.kf.comm.param.match.*;
import com.reign.gcld.battle.domain.*;
import com.reign.gcld.sdata.common.*;
import com.reign.gcld.player.domain.*;
import java.math.*;
import com.reign.gcld.battle.common.*;
import com.reign.gcld.player.common.*;
import com.reign.gcld.chat.service.*;
import com.reign.framework.netty.util.*;
import com.reign.framework.netty.servlet.*;
import com.reign.gcld.huizhan.domain.*;
import com.reign.gcld.world.common.*;
import com.reign.gcld.rank.*;
import com.reign.gcld.huizhan.service.*;
import com.reign.gcld.sdata.domain.*;
import java.io.*;

@Component("battleService")
public class BattleService implements IBattleService
{
    public static final AtomicInteger barVid;
    private static final Logger errorLog;
    private static final Logger timerLog;
    public static Map<Integer, Terrain> rankTerrainmapDisToVal;
    public static Map<Integer, Terrain> rankTerrainmapValToDis;
    public static Map<String, List<Integer>> tokenRewardMap;
    private static Map<String, OfficerTokenUseInfo> officerUseMap;
    private static Map<String, GoldOrder> goldOrderUseMap;
    private final ReentrantReadWriteLock lock;
    private final Lock writeLock;
    private final int FEAT = 15;
    private static Map<Integer, List<CampArmy>> playerPhantomMap;
    @Autowired
    private IJobService jobService;
    @Autowired
    private IDataGetter dataGetter;
    @Autowired
    private IOfficerTokenDao officerTokenDao;
    @Autowired
    private OfficialCache officialCache;
    @Autowired
    private IChatService chatService;
    @Autowired
    private IPlayerAttributeDao playerAttributeDao;
    @Autowired
    private IPlayerDao playerDao;
    @Autowired
    private ChargeitemCache chargeitemCache;
    @Autowired
    private CCache cCache;
    @Autowired
    private IPlayerResourceDao playerResourceDao;
    @Autowired
    private IGoldOrderDao goldOrderDao;
    @Autowired
    private IPlayerOfficeRelativeDao playerOfficeRelativeDao;
    @Autowired
    private KtHjNpcCache ktHjNpcCache;
    @Autowired
    private IForceInfoDao forceInfoDao;
    @Autowired
    private ArmyCache armyCache;
    @Autowired
    private BtGatherRankingCache btGatherRankingCache;
    @Autowired
    private CityEffectCache cityEffectCache;
    @Autowired
    private IWorldFarmService worldFarmService;
    
    static {
        barVid = new AtomicInteger(0);
        errorLog = CommonLog.getLog(BattleService.class);
        timerLog = new TimerLogger();
        BattleService.rankTerrainmapDisToVal = new HashMap<Integer, Terrain>();
        BattleService.rankTerrainmapValToDis = new HashMap<Integer, Terrain>();
        BattleService.tokenRewardMap = new ConcurrentHashMap<String, List<Integer>>();
        BattleService.officerUseMap = new ConcurrentHashMap<String, OfficerTokenUseInfo>();
        BattleService.goldOrderUseMap = new ConcurrentHashMap<String, GoldOrder>();
        BattleService.playerPhantomMap = new ConcurrentHashMap<Integer, List<CampArmy>>();
        BattleService.rankTerrainmapDisToVal.put(1, new Terrain(1, 1, 1));
        BattleService.rankTerrainmapDisToVal.put(2, new Terrain(2, 1, 2));
        BattleService.rankTerrainmapDisToVal.put(3, new Terrain(3, 2, 3));
        BattleService.rankTerrainmapDisToVal.put(4, new Terrain(4, 3, 4));
        BattleService.rankTerrainmapDisToVal.put(5, new Terrain(5, 4, 5));
        BattleService.rankTerrainmapDisToVal.put(6, new Terrain(6, 4, 6));
        BattleService.rankTerrainmapValToDis.put(1, new Terrain(1, 1, 1));
        BattleService.rankTerrainmapValToDis.put(2, new Terrain(3, 2, 3));
        BattleService.rankTerrainmapValToDis.put(3, new Terrain(4, 3, 4));
        BattleService.rankTerrainmapValToDis.put(4, new Terrain(5, 4, 5));
    }
    
    public BattleService() {
        this.lock = new ReentrantReadWriteLock();
        this.writeLock = this.lock.writeLock();
    }
    
    private Tuple<Boolean, byte[]> attPermitCreate(final int playerId, int battleType, final int defId) {
        final Tuple<Boolean, byte[]> result = new Tuple();
        result.left = false;
        if (this.dataGetter.getAutoBattleService().InAutoBattleMode(playerId)) {
            result.right = JsonBuilder.getJson(State.FAIL, LocalMessages.AUTO_BATTLE_CANNOT_OPERATE);
            return result;
        }
        battleType = getBattleType(battleType, defId, this.dataGetter, playerId);
        final int generalNum = this.dataGetter.getPlayerGeneralMilitaryDao().getMilitaryNum(playerId);
        if (generalNum <= 0) {
            result.right = JsonBuilder.getJson(State.FAIL, LocalMessages.GENEGAL_CANNOT_BATTLE);
            return result;
        }
        final Builder builder = BuilderFactory.getInstance().getBuilder(battleType);
        if (builder == null) {
            result.right = JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
            return result;
        }
        final Tuple<Boolean, String> tuple = builder.attPermitCreate(this.dataGetter, playerId, defId);
        if (!(boolean)tuple.left) {
            result.left = false;
            result.right = JsonBuilder.getJson(State.FAIL, tuple.right);
        }
        else {
            final JsonDocument doc = new JsonDocument();
            doc.startObject();
            doc.createElement("battle", true);
            doc.createElement("side", 1);
            doc.endObject();
            result.left = true;
            result.right = JsonBuilder.getJson(State.SUCCESS, doc.toByte());
        }
        return result;
    }
    
    private Tuple<Boolean, byte[]> attPermitBack(final int playerId, final int battleType, final int defId, final int generalId) {
        final Tuple<Boolean, byte[]> tuple = new Tuple();
        tuple.left = false;
        Builder builder = BuilderFactory.getInstance().getBuilder(battleType);
        if (builder == null) {
            tuple.right = JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
            return tuple;
        }
        Battle battle = null;
        if (generalId <= 0) {
            return builder.attPermitBack(this.dataGetter, playerId, defId, generalId);
        }
        battle = NewBattleManager.getInstance().getBattleByGId(playerId, generalId);
        if (battle != null) {
            builder = BuilderFactory.getInstance().getBuilder(battle.getBattleType());
            if (System.currentTimeMillis() > battle.getNextMaxExeTime() + 180000L) {
                final StringBuilder sBuilder = new StringBuilder();
                sBuilder.append("battle stop error!").append("\n").append(battle.getBattleId()).append("\n").append("att list:").append("\n");
                for (final CampArmy campArmy : battle.getAttCamp()) {
                    sBuilder.append(campArmy.getPlayerName()).append("|").append(campArmy.getPlayerId()).append("|").append(campArmy.getGeneralName()).append("|").append(campArmy.getArmyHp()).append("|").append(campArmy.getArmyHpKill()).append("|").append(campArmy.getArmyHpLoss()).append("|").append(campArmy.getArmyHpOrg()).append("\n");
                }
                sBuilder.append("def list:").append("\n");
                for (final CampArmy campArmy : battle.getDefCamp()) {
                    sBuilder.append(campArmy.getPlayerName()).append("|").append(campArmy.getPlayerId()).append("|").append(campArmy.getGeneralName()).append("|").append(campArmy.getArmyHp()).append("|").append(campArmy.getArmyHpKill()).append("|").append(campArmy.getArmyHpLoss()).append("|").append(campArmy.getArmyHpOrg()).append("\n");
                }
                BattleService.errorLog.error(sBuilder.toString());
                final Map<Integer, PlayerInfo> pisMap = battle.getInBattlePlayers();
                for (final PlayerInfo pis : pisMap.values()) {
                    NewBattleManager.getInstance().quitBattle(pis.getPlayerId(), battle.getBattleId());
                    builder.inBattleInfo(pis.getPlayerId(), false);
                }
                NewBattleManager.getInstance().quitBattle(playerId, battle.getBattleId());
                NewBattleManager.getInstance().deleteBattle(battle.getBattleId());
            }
            final JsonDocument doc = new JsonDocument();
            doc.startObject();
            doc.createElement("battle", true);
            doc.createElement("battleId", battle.getBattleId());
            final int battleSide = builder.getBattleSide(this.dataGetter, this.dataGetter.getPlayerDao().read(playerId), battle.getDefBaseInfo().getId());
            doc.createElement("side", battleSide);
            doc.endObject();
            tuple.left = true;
            tuple.right = JsonBuilder.getJson(State.SUCCESS, doc.toByte());
            return tuple;
        }
        final PlayerGeneralMilitary pgm = this.dataGetter.getPlayerGeneralMilitaryDao().getMilitary(playerId, generalId);
        if (pgm == null) {
            tuple.right = JsonBuilder.getJson(State.FAIL, LocalMessages.T_NO_SUCH_GENERAL);
            return tuple;
        }
        if (pgm.getState() > 1) {
            BattleService.errorLog.error("PlayerGeneralMilitary state error! playerId:" + playerId + " generalId:" + generalId + " state:" + pgm.getState());
            this.dataGetter.getPlayerGeneralMilitaryDao().restartRecruit(playerId, generalId, 1, new Date());
            this.dataGetter.getGeneralService().sendGmUpdate(playerId, generalId, false);
            tuple.right = JsonBuilder.getJson(State.FAIL, LocalMessages.BATTLE_GENERAL_STATE_ERROR1);
            return tuple;
        }
        tuple.right = JsonBuilder.getJson(State.FAIL, LocalMessages.BATTLE_GENERAL_STATE_ERROR);
        return tuple;
    }
    
    @Transactional
    @Override
    public byte[] attPermit(final PlayerDto playerDto, final int battleType, final int defId, final int create, final int generalId) {
        if (create == 1) {
            return this.attPermitCreate(playerDto.playerId, battleType, defId).right;
        }
        return this.attPermitBack(playerDto.playerId, battleType, defId, generalId).right;
    }
    
    @Override
    public byte[] battlePrepare(final PlayerDto playerDto, int battleType, final int defId, final int generalId, final int join, int terrainType) {
        final int playerId = playerDto.playerId;
        final Player player = this.dataGetter.getPlayerDao().read(playerId);
        Builder builder = BuilderFactory.getInstance().getBuilder(battleType);
        if (builder == null) {
            BattleService.errorLog.error("BattleService battlePrepare battleType:" + battleType);
            if (generalId > 0) {
                final Battle bat = NewBattleManager.getInstance().getBattleByGId(playerId, generalId);
                if (bat != null) {
                    battleType = bat.getBattleType();
                    builder = BuilderFactory.getInstance().getBuilder(battleType);
                }
            }
            if (builder == null) {
                return JsonBuilder.getJson(State.FAIL, "\u6218\u6597\u7c7b\u578b\u9519\u8bef. battleType=" + battleType);
            }
        }
        Battle battle = builder.getPlayerBattleInfo(playerId, battleType, generalId);
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        int battleSide = 1;
        final PlayerBattleAttribute pba = this.dataGetter.getPlayerBattleAttributeDao().read(playerId);
        if (battle != null) {
            if (!battle.isBattleDoing()) {
                builder.inBattleInfo(playerId, false);
                return JsonBuilder.getJson(State.FAIL, LocalMessages.BATTLE_END_INFO);
            }
            this.getBattleView(playerDto, battle, doc);
            doc.endObject();
            return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
        }
        else {
            if (defId == 0) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.BATTLE_END_INFO);
            }
            if (defId < 0 && battleType != 8 && defId < 0 && battleType != 11) {
                ErrorSceneLog.getInstance().appendErrorMsg("try to create battle, battlePrepare defId is negative.").appendClassName("BattleService").appendMethodName("battlePrepare").append("defId", defId).append("PlayerId", playerDto.playerId).append("PlayerName", playerDto.playerName).append("generalId", generalId).append("battleType", battleType).flush();
                return JsonBuilder.getJson(State.FAIL, "defId==" + defId);
            }
            battleSide = builder.getBattleSide(this.dataGetter, player, defId);
            doc.createElement("side", battleSide);
            battleType = getBattleType(battleType, defId, this.dataGetter, playerId);
            doc.createElement("battleType", battleType);
            builder = BuilderFactory.getInstance().getBuilder(battleType);
            Terrain terrain = null;
            if (battleType == 8) {
                if (terrainType == 0) {
                    terrainType = 1;
                }
                terrain = BattleService.rankTerrainmapValToDis.get(terrainType);
            }
            else {
                terrain = builder.getTerrain(playerId, defId, this.dataGetter);
            }
            doc.createElement("terrain", terrain.getDisplay());
            doc.createElement("terrainPic", terrain.getTerrainPic());
            doc.createElement("targetId", defId);
            battle = builder.existBattle(this.dataGetter, player, defId);
            doc.appendJson(builder.getOtherBatInfo(this.dataGetter, defId, playerId, battleSide, pba));
            doc.appendJson(builder.getPrepareInfo(this.dataGetter, playerDto, defId, battle, terrain.getValue()));
            doc.appendJson(builder.getRewardModeInfo(this.dataGetter, playerDto));
            int autoSt = -1;
            final int zdzsTech = this.dataGetter.getTechEffectCache().getTechEffect(playerId, 43);
            if (zdzsTech > 0) {
                autoSt = pba.getAutoStrategy();
            }
            else {
                autoSt = -1;
            }
            doc.createElement("autoSt", autoSt);
            if (battle != null) {
                battle.addInSceneSet(playerId);
                doc.createElement("battleReport", battle.getCurrentBattleInfo(playerId, battleSide, this.dataGetter, 2));
            }
            else if (join == 1) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.BATTLE_END_INFO);
            }
            doc.endObject();
            return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
        }
    }
    
    @Transactional
    @Override
    public void reSetAllDamageE(final int cityId) {
        final Battle bat = NewBattleManager.getInstance().getBattleByDefId(3, cityId);
        if (bat != null) {
            bat.reSetAllDamageE(this.dataGetter, bat.getAttBaseInfo().getForceId(), bat.getDefBaseInfo().getForceId());
        }
    }
    
    @Override
    public void dobattleExceptionBatId(final String battleId, final Exception e) {
        try {
            final Battle bat = NewBattleManager.getInstance().getBattleByBatId(battleId);
            if (bat == null) {
                BattleService.errorLog.error("doBattle Exception battle is NULL battleId" + battleId);
                return;
            }
            BattleService.errorLog.error("//////////////////////////////////////////////////doBattle Exception.", e);
            this.dataGetter.getBattleInfoService().deleteBattle(bat);
            ErrorSceneLog.getInstance().appendErrorMsg("doBattle Exception.").appendClassName("BattleService").appendMethodName("doBattle").append("battleId", bat.getBattleId()).append("battleType", bat.getBattleType()).append("defId", bat.getDefBaseInfo().getId()).append("attId", bat.getAttBaseInfo().getId()).flush();
            try {
                final StringBuilder sb = new StringBuilder();
                sb.append(" roundNum:").append(bat.getRoundNum());
                sb.append(" getNextMinExeTime:").append(new Date(bat.getNextMinExeTime()));
                sb.append(" NextMinExeTime:").append(bat.getNextMinExeTime());
                sb.append(" getNextMaxExeTime:").append(new Date(bat.getNextMaxExeTime()));
                sb.append(" NextMaxExeTime:").append(bat.getNextMaxExeTime());
                sb.append(" getStartTime:").append(new Date(bat.getStartTime()));
                sb.append(" StartTime:").append(bat.getStartTime());
                sb.append(" isBattleDoing:").append(bat.isBattleDoing());
                if (bat.getAttList() != null) {
                    sb.append("\n bat.getAttList().size():").append(bat.getAttList().size());
                    if (bat.getAttList().size() > 0) {
                        final BattleArmy ba = bat.getAttList().get(0);
                        sb.append(" att pos:").append(ba.getPosition());
                        final CampArmy ca = ba.getCampArmy();
                        sb.append(" att pId:").append(ca.getPlayerId());
                        sb.append(" att armyHp:").append(ca.getArmyHp());
                        sb.append(" att getArmyHpOrg:").append(ca.getArmyHpOrg());
                        sb.append(" att getArmyHpLoss:").append(ca.getArmyHpLoss());
                        sb.append(" att generalId:").append(ca.getGeneralId());
                    }
                }
                if (bat.getDefList() != null) {
                    sb.append("\n bat.getDefList().size():").append(bat.getDefList().size());
                    if (bat.getDefList().size() > 0) {
                        final BattleArmy ba = bat.getDefList().get(0);
                        sb.append(" def pos:").append(ba.getPosition());
                        final CampArmy ca = ba.getCampArmy();
                        sb.append(" def pId:").append(ca.getPlayerId());
                        sb.append(" def armyHp:").append(ca.getArmyHp());
                        sb.append(" def getArmyHpOrg:").append(ca.getArmyHpOrg());
                        sb.append(" def getArmyHpLoss:").append(ca.getArmyHpLoss());
                        sb.append(" def generalId:").append(ca.getGeneralId());
                    }
                }
                ErrorSceneLog.getInstance().error(sb.toString());
            }
            catch (Exception ee) {
                BattleService.errorLog.error("BattleService dobattleException2 ", ee);
            }
            for (final CampArmy ca2 : bat.getAttCamp()) {
                try {
                    if (ca2.getPlayerId() <= 0 || !ca2.isUpdateDB() || ca2.isPhantom()) {
                        continue;
                    }
                    this.dataGetter.getPlayerGeneralMilitaryDao().restartRecruit(ca2.getPlayerId(), ca2.getGeneralId(), ca2.isInRecruit() ? 1 : 0, new Date());
                    this.dataGetter.getGeneralService().sendGmUpdate(ca2.getPlayerId(), ca2.getGeneralId(), false);
                }
                catch (Exception ee2) {
                    BattleService.errorLog.error("BattleService dobattleException ", ee2);
                    ErrorSceneLog.getInstance().appendErrorMsg("dobattleException exception again").appendPlayerName(ca2.getPlayerName()).appendPlayerId(ca2.getPlayerId()).appendGeneralName(ca2.getGeneralName()).appendGeneralId(ca2.getGeneralId()).append("hp", ca2.getArmyHp()).flush();
                }
            }
            if (bat.getBattleType() == 3 || bat.getBattleType() == 13) {
                BattleService.errorLog.error("//////////////////////////////////////////////////this is city battle, see world log for detail.");
                final int cityId = bat.getDefBaseInfo().getId();
                int state = 0;
                if (bat.getBattleType() == 3) {
                    state = 3;
                }
                else {
                    state = 13;
                }
                this.dataGetter.getBarbarainPhantomDao().resetStateByLocationAndState(cityId, state);
                bat.worldSceneLog.newLine().newLine().newLine().flush();
                this.modifyWorldCityAndGeneralState(this.dataGetter, bat);
                final City city = this.dataGetter.getCityDao().read(cityId);
                if (city.getForceId() == 1 || city.getForceId() == 2 || city.getForceId() == 3) {
                    for (final CampArmy ca3 : bat.getDefCamp()) {
                        try {
                            if (ca3.getPlayerId() <= 0 || !ca3.isUpdateDB() || ca3.isPhantom()) {
                                continue;
                            }
                            this.dataGetter.getPlayerGeneralMilitaryDao().restartRecruit(ca3.getPlayerId(), ca3.getGeneralId(), ca3.isInRecruit() ? 1 : 0, new Date());
                            this.dataGetter.getGeneralService().sendGmUpdate(ca3.getPlayerId(), ca3.getGeneralId(), false);
                        }
                        catch (Exception ee3) {
                            BattleService.errorLog.error("BattleService dobattleException 2", ee3);
                            ErrorSceneLog.getInstance().appendErrorMsg("dobattleException exception again").appendPlayerName(ca3.getPlayerName()).appendPlayerId(ca3.getPlayerId()).appendGeneralName(ca3.getGeneralName()).appendGeneralId(ca3.getGeneralId()).append("hp", ca3.getArmyHp()).flush();
                        }
                    }
                }
            }
            final Map<Integer, PlayerInfo> pisMap = bat.getInBattlePlayers();
            if (pisMap != null) {
                final Builder builder = BuilderFactory.getInstance().getBuilder(bat.getBattleType());
                for (final PlayerInfo pis : pisMap.values()) {
                    if (pis == null) {
                        BattleService.errorLog.error("pis:" + pis);
                    }
                    else {
                        NewBattleManager.getInstance().quitBattle(pis.getPlayerId(), bat.getBattleId());
                        if (builder == null) {
                            BattleService.errorLog.error("result.builder:" + builder);
                        }
                        else {
                            builder.inBattleInfo(pis.getPlayerId(), false);
                        }
                    }
                }
            }
            NewBattleManager.getInstance().deleteBattle(bat.getBattleId());
            BattleService.errorLog.error("//////////////////////////////////////////////////handle doBattle Exception succ.");
        }
        catch (Exception e2) {
            BattleService.errorLog.error("//////////////////////////////////////////////////dobattleException Exception again.", e2);
        }
    }
    
    @Override
    public void modifyWorldCityAndGeneralState(final IDataGetter dataGetter, final Battle bat) {
        bat.worldSceneLog.appendLogMsg("modifyWorldCityAndGeneralState").newLine();
        synchronized (bat.getBattleId()) {
            final int cityId = bat.getDefBaseInfo().getId();
            dataGetter.getCityService().changeState(cityId, 0, false);
            final Set<Integer> neribors = dataGetter.getWorldRoadCache().getNeighbors(cityId);
            final Set<City> neiCitySet = new HashSet<City>();
            for (final int neiId : neribors) {
                final City city = dataGetter.getCityDao().read(neiId);
                neiCitySet.add(city);
            }
            for (final CampArmy ca : bat.getAttCamp()) {
                if (ca.getPlayerId() >= 0 && !ca.isPhantom()) {
                    if (ca.isBarPhantom()) {
                        continue;
                    }
                    boolean neiborDone = false;
                    int toLocationId = 0;
                    final PlayerGeneralMilitary pgm = dataGetter.getPlayerGeneralMilitaryDao().getMilitary(ca.getPlayerId(), ca.getGeneralId());
                    final int playerForceId = pgm.getForceId();
                    for (final City neiCity : neiCitySet) {
                        if (neiCity.getForceId() == playerForceId && neiCity.getState() == 0) {
                            toLocationId = neiCity.getId();
                            neiborDone = true;
                        }
                    }
                    if (!neiborDone) {
                        final int capitalId = toLocationId = WorldCityCommon.nationMainCityIdMap.get(playerForceId);
                    }
                    dataGetter.getPlayerGeneralMilitaryDao().updateLocationId(ca.getPlayerId(), ca.getGeneralId(), toLocationId);
                    bat.worldSceneLog.Indent().append("pgmVId", pgm.getVId()).appendPlayerId(ca.getPlayerId()).appendPlayerName(ca.getPlayerName()).appendGeneralId(ca.getGeneralId()).appendGeneralName(ca.getGeneralName()).append("from cityId", pgm.getLocationId()).append("from city", dataGetter.getWorldCityCache().get((Object)pgm.getLocationId())).append("to cityId", toLocationId).append("to city", dataGetter.getWorldCityCache().get((Object)toLocationId)).newLine();
                    try {
                        dataGetter.getCityService().updateGNumAndSend(pgm.getLocationId(), toLocationId);
                    }
                    catch (Exception e) {
                        ErrorSceneLog.getInstance().error("BattleService modifyWorldCityAndGeneralState 1 ", e);
                    }
                    try {
                        final String cgm = dataGetter.getCityService().getColoredGeneralName(ca.getGeneralId());
                        dataGetter.getCityDataCache().fireCityMoveMessage(ca.getPlayerId(), cityId, toLocationId, cgm);
                    }
                    catch (Exception e) {
                        ErrorSceneLog.getInstance().error("BattleService modifyWorldCityAndGeneralState 2 ", e);
                    }
                    try {
                        dataGetter.getCityService().sendAttMoveInfo(ca.getPlayerId(), ca.getGeneralId(), cityId, toLocationId, ca.getForceId(), "", ca.getArmyHp(), true);
                    }
                    catch (Exception e) {
                        ErrorSceneLog.getInstance().appendErrorMsg("sendAttMoveInfo exception").append("playerId", ca.getPlayerId()).append("generalId", ca.getGeneralId()).appendClassName("BattleService").appendMethodName("modifyWorldCityAndGeneralState").flush();
                        ErrorSceneLog.getInstance().error("BattleService modifyWorldCityAndGeneralState 3 ", e);
                    }
                }
            }
        }
        // monitorexit(bat.getBattleId())
    }
    
    @Override
    public byte[] battleStart(final int playerId, final int battleType, final int defId, final String gIds, final int terrainType) {
        try {
            if (gIds == null || gIds.isEmpty()) {
                return JsonBuilder.getJson(State.FAIL, "gIds=" + gIds);
            }
            final List<Integer> gIdList = new ArrayList<Integer>();
            try {
                String[] split;
                for (int length = (split = gIds.split("#")).length, i = 0; i < length; ++i) {
                    final String s = split[i];
                    if (!s.trim().isEmpty()) {
                        final int gId = Integer.parseInt(s);
                        if (gIdList.contains(gId)) {
                            return JsonBuilder.getJson(State.FAIL, String.valueOf(LocalMessages.PLUG_IS_SHAMEFUL) + "gIds=" + gIds);
                        }
                        gIdList.add(gId);
                    }
                }
            }
            catch (NumberFormatException e2) {
                ErrorSceneLog.getInstance().appendErrorMsg("battleStart plug attack").appendPlayerId(playerId).append("battleType", battleType).append("defId", defId).append("gIds", gIds).flush();
                return JsonBuilder.getJson(State.FAIL, String.valueOf(LocalMessages.PLUG_IS_SHAMEFUL) + "gIds=" + gIds);
            }
            final List<PlayerGeneralMilitary> pgmList = this.dataGetter.getPlayerGeneralMilitaryDao().getMilitaryList(playerId);
            final Set<Integer> pgmSet = new HashSet<Integer>();
            for (final PlayerGeneralMilitary pgm : pgmList) {
                pgmSet.add(pgm.getGeneralId());
                if (this.worldFarmService.isInFarmForbiddenOperation(pgm, false) && gIdList.contains(pgm.getGeneralId())) {
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.GENERAL_BUSY);
                }
            }
            for (final Integer gId2 : gIdList) {
                if (!pgmSet.contains(gId2)) {
                    return JsonBuilder.getJson(State.FAIL, "\u4f60\u6ca1\u6709\u8fd9\u4e2a\u6b66\u5c06\uff1a" + ((General)this.dataGetter.getGeneralCache().get((Object)gId2)).getName());
                }
                final PlayerGeneralMilitary pgm2 = this.dataGetter.getPlayerGeneralMilitaryDao().getMilitary(playerId, gId2);
                final HuizhanHistory hh = this.dataGetter.getHuiZhanService().getTodayHuizhanInProcess();
                final int curLocationId = pgm2.getLocationId();
                if (hh != null && hh.getCityId() == curLocationId) {
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.HUIZHAN_ALEADY_IN_HZ_CITY);
                }
            }
            if ((battleType == 1 || battleType == 2 || battleType == 11) && gIds != null) {
                this.dataGetter.getPlayerBattleAttributeDao().updateArmiesBattleOrder(playerId, gIds);
            }
            final BattleStartResult battleStartResult = this.battleStart2(playerId, battleType, defId, terrainType, gIdList);
            if (battleStartResult.succ) {
                final JsonDocument doc = new JsonDocument();
                doc.startObject();
                doc.createElement("battleId", battleStartResult.battleId);
                doc.endObject();
                return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
            }
            return JsonBuilder.getJson(State.FAIL, battleStartResult.failReason);
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("BattleService.battleStart2 catch Exception.", e);
            return JsonBuilder.getJson(State.FAIL, LocalMessages.BATTLE_START_BATTLE_EXCEPTIONED);
        }
    }
    
    private BattleStartResult battleStart2(final int playerId, int battleType, final int defId, final int terrainType, final List<Integer> gIdList) {
        final BattleStartResult battleStartResult = new BattleStartResult();
        battleStartResult.succ = false;
        try {
            if (gIdList == null || gIdList.size() <= 0) {
                battleStartResult.succ = false;
                battleStartResult.failReason = LocalMessages.GENEGAL_CHOOSE_TO_BATTLE;
                return battleStartResult;
            }
            if (defId < 0 && battleType != 8 && battleType != 11) {
                ErrorSceneLog.getInstance().appendErrorMsg("try to create battle, battleStart defId is negative.").appendClassName("BattleService").appendMethodName("battleStart").append("defId", defId).append("PlayerId", playerId).append("battleType", battleType).flush();
                battleStartResult.succ = false;
                battleStartResult.failReason = "battleType==" + battleType + " defId==" + defId;
                return battleStartResult;
            }
            final Player player = this.dataGetter.getPlayerDao().read(playerId);
            battleType = getBattleType(battleType, defId, this.dataGetter, playerId);
            final Builder builder = BuilderFactory.getInstance().getBuilder(battleType);
            if (builder == null) {
                battleStartResult.succ = false;
                battleStartResult.failReason = String.valueOf(LocalMessages.PLUG_IS_SHAMEFUL) + "battleType==" + battleType;
                return battleStartResult;
            }
            final Tuple<List<PlayerGeneralMilitary>, String> gTuple = builder.chooseGeneral(this.dataGetter, player, defId, gIdList);
            if (gTuple.left == null) {
                battleStartResult.succ = false;
                battleStartResult.failReason = gTuple.right;
                return battleStartResult;
            }
            int force = 0;
            for (final PlayerGeneralMilitary pgm : (List)gTuple.left) {
                force += pgm.getForces();
            }
            this.updateHuizhanPlayerForce(defId, playerId, force);
            this.updateHuizhanNationForce(defId, player.getForceId(), force);
            if (battleType != 3) {
                final List<PlayerGeneralMilitary> chooseList = gTuple.left;
                for (final PlayerGeneralMilitary pgm2 : chooseList) {
                    final GeneralMoveDto gmd = CityService.getGeneralMoveDto(pgm2.getPlayerId(), pgm2.getGeneralId());
                    if (gmd != null && CDUtil.isInCD(gmd.nextMoveTime, new Date())) {
                        gmd.moveLine = "";
                        gmd.nextMoveTime = 0L;
                        gmd.cityState = 0;
                    }
                }
            }
            String battleId = null;
            Battle battle = builder.existBattle(this.dataGetter, player, defId);
            if (battle == null) {
                battleId = builder.getBattleId(this.dataGetter, player, defId);
                battle = NewBattleManager.getInstance().getBattleByBatId(battleId);
            }
            if (battle != null) {
                return this.joinBattle(player, battle, builder, gTuple.left);
            }
            return this.fireBattle(player, battleId, builder, gTuple.left, battleType, defId, terrainType);
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("BattleService.battleStart2 catch Exception.", e);
            battleStartResult.succ = false;
            battleStartResult.failReason = LocalMessages.BATTLE_START_BATTLE_EXCEPTIONED;
            return battleStartResult;
        }
    }
    
    private BattleStartResult fireBattle(final Player player, final String battleId, final Builder builder, final List<PlayerGeneralMilitary> pgmList, final int battleType, final int defId, final int terrainType) {
        final BattleStartResult battleStartResult = new BattleStartResult();
        Battle battle = null;
        try {
            final Tuple<Boolean, String> tuple = builder.canCreateBattle(player, defId, this.dataGetter);
            if (!(boolean)tuple.left) {
                battleStartResult.succ = false;
                battleStartResult.failReason = tuple.right;
                return battleStartResult;
            }
            tuple.left = false;
            battle = NewBattleManager.getInstance().createBattle(battleId);
            if (battle == null) {
                battleStartResult.succ = false;
                battleStartResult.failReason = LocalMessages.BATTLE_CANNOT_CREATE_BAT;
                return battleStartResult;
            }
            if (!builder.conSumeFood(player.getPlayerId(), defId, this.dataGetter)) {
                NewBattleManager.getInstance().deleteBattle(battleId);
                battleStartResult.succ = false;
                battleStartResult.failReason = LocalMessages.T_COMM_10027;
                return battleStartResult;
            }
            final String saveReport = Configuration.getProperty("gcld.battle.report.save");
            if (saveReport.equals("1")) {
                BattleSceneLog.getInstance().appendLogMsg("[\u653b\u51fb\u6309\u94ae-\u521b\u5efa]").appendClassName("BattleService").appendMethodName("battleStart2").append("playerId", player.getPlayerId()).append("playerName", player.getPlayerName()).append("battleType", battleType).append("defId", defId).flush();
            }
            Terrain terrain = null;
            if (battleType == 8) {
                terrain = BattleService.rankTerrainmapDisToVal.get(terrainType);
            }
            else {
                terrain = builder.getTerrain(player.getPlayerId(), defId, this.dataGetter);
            }
            if (terrain == null) {
                ErrorSceneLog.getInstance().appendErrorMsg("terrain is null").appendClassName("BattleService").appendMethodName("battleStart2").append("battleId", battleId).append("battleType", battleType).append("terrainType", terrainType).flush();
                NewBattleManager.getInstance().deleteBattle(battleId);
                battleStartResult.succ = false;
                battleStartResult.failReason = "terrain is null";
                return battleStartResult;
            }
            final BattleAttacker battleAttacker = new BattleAttacker();
            battleAttacker.attType = 1;
            battleAttacker.attForceId = player.getForceId();
            battleAttacker.attPlayerId = player.getPlayerId();
            battleAttacker.attPlayer = player;
            battleAttacker.pgmList = pgmList;
            final boolean autoBat = false;
            battle.init(battleAttacker, battleType, defId, this.dataGetter, autoBat, terrainType);
            if (battleType == 16 && player != null) {
                this.dataGetter.getIndividualTaskService().sendTaskMessage(new PlayerDto(player.getPlayerId(), player.getForceId()), 1, "challenge");
            }
            builder.dealBuilderBattleTask(this.dataGetter, player.getPlayerId(), defId);
            builder.dealUniqueStaff(this.dataGetter, battle, player.getPlayerId(), defId);
            battleStartResult.succ = true;
            battleStartResult.battleId = battleId;
            return battleStartResult;
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("BattleService.fireBattle catch Exception.", e);
            ErrorSceneLog.getInstance().appendErrorMsg("fireBattle catch Exception. battle will be cleared").appendBattleId(battleId).appendPlayerId(player.getPlayerId()).appendPlayerName(player.getPlayerName()).appendClassName("BattleService").appendMethodName("joinBattle").flush();
            Builder.clearBattleAfterError(this.dataGetter, player.getPlayerId(), battle);
            battleStartResult.succ = false;
            battleStartResult.failReason = LocalMessages.BATTLE_FIRE_BATTLE_EXCEPTIONED;
            return battleStartResult;
        }
    }
    
    private BattleStartResult joinBattle(final Player player, final Battle battle, final Builder builder, final List<PlayerGeneralMilitary> pgmList) {
        final BattleStartResult battleStartResult = new BattleStartResult();
        try {
            final Tuple<Boolean, String> tuple = builder.canJoinBattle(player, pgmList, this.dataGetter, battle);
            if (!(boolean)tuple.left) {
                battleStartResult.succ = false;
                battleStartResult.failReason = tuple.right;
                return battleStartResult;
            }
            if (battle.getBattleType() == 4) {
                for (final CampArmy ca : battle.getDefCamp()) {
                    if (ca.getPlayerId() == player.getPlayerId()) {
                        battleStartResult.succ = false;
                        battleStartResult.failReason = LocalMessages.CAN_NO_ATT_YOUR_OWN_BUILDING;
                        return battleStartResult;
                    }
                }
            }
            final int defId = battle.getDefBaseInfo().getId();
            if (!builder.conSumeFood(player.getPlayerId(), defId, this.dataGetter)) {
                battleStartResult.succ = false;
                battleStartResult.failReason = LocalMessages.T_COMM_10027;
                return battleStartResult;
            }
            if (!battle.join(player, pgmList, this.dataGetter)) {
                battleStartResult.succ = false;
                battleStartResult.failReason = LocalMessages.BATTLE_END_INFO;
                return battleStartResult;
            }
            battleStartResult.succ = true;
            battleStartResult.battleId = battle.getBattleId();
            return battleStartResult;
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("BattleService.joinBattle catch Exception.", e);
            ErrorSceneLog.getInstance().appendErrorMsg("joinBattle catch Exception").appendBattleId(battle.getBattleId()).appendPlayerId(player.getPlayerId()).appendPlayerName(player.getPlayerName()).appendClassName("BattleService").appendMethodName("joinBattle").flush();
            battleStartResult.succ = false;
            battleStartResult.failReason = LocalMessages.BATTLE_JOIN_BATTLE_EXCEPTIONED;
            return battleStartResult;
        }
    }
    
    @Override
    public void npcStartOrJoinBattle(final WorldCity wc, final int forceId) {
        if (WorldCityCommon.mainCityNationIdMap.containsKey(wc.getId())) {
            return;
        }
        final Builder builder = BuilderFactory.getInstance().getBuilder(3);
        City city = CityDataCache.cityArray[wc.getId()];
        if (city == null || city.getForceId() == 0) {
            return;
        }
        Battle battle = NewBattleManager.getInstance().getBattleByDefId(3, wc.getId());
        if (battle != null) {
            if (!battle.joinCityNpc(wc, 1, this.dataGetter, forceId)) {
                return;
            }
        }
        else {
            if (city.getForceId() == forceId) {
                return;
            }
            String battleId = null;
            battleId = CityBuilder.getBattleId(this.dataGetter, forceId, wc.getId());
            final Terrain terrain = builder.getTerrain(-1, wc.getId(), this.dataGetter);
            battle = NewBattleManager.getInstance().createBattle(battleId);
            if (battle == null) {
                return;
            }
            city = CityDataCache.cityArray[wc.getId()];
            if (city == null || city.getForceId() == forceId) {
                NewBattleManager.getInstance().deleteBattle(battleId);
                return;
            }
            final BattleAttacker battleAttacker = new BattleAttacker();
            battleAttacker.attType = 2;
            battleAttacker.attForceId = forceId;
            battleAttacker.attPlayerId = -2;
            battle.init(battleAttacker, 3, wc.getId(), this.dataGetter, false, terrain.getValue());
            builder.dealUniqueStaff(this.dataGetter, battle, -1, wc.getId());
        }
    }
    
    @Override
    public void battleReStart(final String paramStr) {
        final long start = System.currentTimeMillis();
        BattleService.timerLog.info(LogUtil.formatThreadLog("BattleService", "battleReStart", 0, 0L, "paramStr:" + paramStr));
        final String[] params = paramStr.split("#");
        final int playerId = Integer.valueOf(params[0]);
        final int cityId = Integer.valueOf(params[2]);
        final int attForceId = Integer.valueOf(params[4]);
        String battleId = null;
        final City city = this.dataGetter.getCityDao().read(cityId);
        battleId = CityBuilder.getBattleId(this.dataGetter, city.getForceId(), city.getId());
        final Builder builder = BuilderFactory.getInstance().getBuilder(3);
        final Terrain terrain = builder.getTerrain(playerId, city.getId(), this.dataGetter);
        final Battle battle = NewBattleManager.getInstance().createBattle(battleId);
        if (battle == null) {
            return;
        }
        final BattleAttacker battleAttacker = new BattleAttacker();
        battleAttacker.attForceId = attForceId;
        battle.init(battleAttacker, 3, cityId, this.dataGetter, false, terrain.getValue());
        builder.dealUniqueStaff(this.dataGetter, battle, -1, city.getId());
        BattleService.timerLog.info(LogUtil.formatThreadLog("BattleService", "battleReStart", 2, System.currentTimeMillis() - start, "paramStr:" + paramStr));
    }
    
    @Override
    public void leaveBattle(final int playerId, final int battleType, final String battleId) {
        Battle battle = NewBattleManager.getInstance().getBattleByBatId(battleId);
        if (battle == null) {
            battle = NewBattleManager.getInstance().getBattleByBatType(playerId, battleType);
        }
        if (battle != null) {
            battle.leave(playerId);
            NewBattleManager.getInstance().clearPlayerWatchBattle(playerId);
        }
    }
    
    @Override
    public byte[] getQuitGeneral(final PlayerDto playerDto, final String battleId, final String gIds) {
        if (this.dataGetter.getAutoBattleService().InAutoBattleMode(playerDto.playerId)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.AUTO_BATTLE_CANNOT_OPERATE);
        }
        final int playerId = playerDto.playerId;
        final Battle battle = NewBattleManager.getInstance().getBattleByBatId(battleId);
        if (battle == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.BATTLE_ENDEND_CANNOT_WITHDRAW);
        }
        final HuizhanHistory hh = this.dataGetter.getHuiZhanService().getTodayHuizhanInProcess();
        if (hh != null && hh.getCityId() == battle.getDefBaseInfo().getId()) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.HUIZHAN_IN_PREPARATION_CAN_NOT_QUIT);
        }
        final PlayerInfo pi = battle.getInBattlePlayers().get(playerId);
        if (pi == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.NOT_IN_BATTLE_CANNOT_WITHDRAW);
        }
        final int cityId = battle.getDefBaseInfo().getId();
        if (battle.getBattleType() == 3 || battle.getBattleType() == 14) {
            final City city = this.dataGetter.getCityDao().read(cityId);
            if (pi.isAttSide()) {
                boolean attCanWithDraw = false;
                final Set<Integer> neibors = this.dataGetter.getWorldRoadCache().getNeighbors(cityId);
                for (final Integer neiId : neibors) {
                    final City neiCity = this.dataGetter.getCityDao().read(neiId);
                    if (neiCity.getForceId() == playerDto.forceId && neiCity.getState() == 0 && neiCity.getTitle() != 1 && neiCity.getTitle() != 2) {
                        attCanWithDraw = true;
                        break;
                    }
                }
            }
            else {
                final int cityTitle = city.getTitle();
                if (cityTitle != 1) {}
            }
        }
        else if (battle.getBattleType() == 18) {
            final JuBenCityDto juBenCityDto = JuBenManager.getInstance().getJuBenCityDto(playerId, cityId);
            if (pi.isAttSide()) {
                boolean attCanWithDraw = false;
                final Set<Integer> neibors = this.dataGetter.getSoloRoadCache().getNeighbors(cityId);
                for (final Integer neiId : neibors) {
                    final JuBenCityDto neiJuBenCityDto = JuBenManager.getInstance().getJuBenCityDto(playerId, neiId);
                    if (neiJuBenCityDto.forceId == playerDto.forceId && neiJuBenCityDto.state == 0 && neiJuBenCityDto.title != 1 && neiJuBenCityDto.title != 2) {
                        attCanWithDraw = true;
                        break;
                    }
                }
            }
            else {
                final int cityTitle = juBenCityDto.title;
                if (cityTitle != 1) {}
            }
        }
        LinkedList<CampArmy> camps = battle.getAttCamp();
        List<BattleArmy> lists = battle.getAttList();
        if (!pi.isAttSide()) {
            camps = battle.getDefCamp();
            lists = battle.getDefList();
        }
        final Set<CampArmy> cset = new HashSet<CampArmy>();
        final Set<Integer> paraGIds = new HashSet<Integer>();
        if (!StringUtils.isBlank(gIds)) {
            final String[] gids = gIds.split("#");
            if (gIds.length() >= 1) {
                String[] array;
                for (int length = (array = gids).length, i = 0; i < length; ++i) {
                    final String gid = array[i];
                    try {
                        paraGIds.add(Integer.parseInt(gid));
                    }
                    catch (Exception e) {
                        return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
                    }
                }
            }
        }
        for (final CampArmy ca : camps) {
            if (paraGIds.size() > 0 && !paraGIds.contains(ca.getGeneralId())) {
                continue;
            }
            if (ca.getPlayerId() != playerId || ca.getArmyHp() < 0 || !ca.isInBattle() || ca.isPhantom()) {
                continue;
            }
            cset.add(ca);
        }
        if (cset.size() == 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.BATTLE_ON_QUEUE_CANNOT_WITHDRAW);
        }
        final Set<Integer> bset = new HashSet<Integer>();
        for (final BattleArmy bas : lists) {
            bset.add(bas.getCampArmy().getId());
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.startArray("gIds");
        double reduce = 0.0;
        for (final CampArmy ca2 : cset) {
            if (bset.contains(ca2.getId())) {
                continue;
            }
            doc.startObject();
            doc.createElement("gId", ca2.getGeneralId());
            doc.createElement("gName", ca2.getGeneralName());
            doc.createElement("gPic", ca2.getGeneralPic());
            doc.createElement("gQuality", ca2.getQuality());
            reduce = ca2.getArmyHp() * ((C)this.dataGetter.getcCache().get((Object)"World.MoveDuringFighting.DeserterProportion")).getValue();
            doc.createElement("reduce", (int)reduce);
            doc.endObject();
        }
        doc.endArray();
        if (battle.getBattleType() == 3 || battle.getBattleType() == 14) {
            final Set<Integer> mistOpendedSet = new HashSet<Integer>();
            final PlayerWorld pw = this.dataGetter.getPlayerWorldDao().read(playerId);
            if (pw != null) {
                final String attStr = pw.getAttedId();
                if (!StringUtils.isBlank(attStr)) {
                    final String[] ids = attStr.split(",");
                    String[] array2;
                    for (int length2 = (array2 = ids).length, j = 0; j < length2; ++j) {
                        final String str = array2[j];
                        mistOpendedSet.add(Integer.valueOf(str));
                    }
                }
            }
            final Set<Integer> neibors2 = this.dataGetter.getWorldRoadCache().getNeighbors(cityId);
            if (neibors2 != null && neibors2.size() > 0) {
                doc.startArray("cityOptions");
                boolean canWithDraw = false;
                for (final Integer neiId2 : neibors2) {
                    final City neiCity2 = this.dataGetter.getCityDao().read(neiId2);
                    int state = 0;
                    if (playerDto.forceId == neiCity2.getForceId() && neiCity2.getState() == 0 && mistOpendedSet.contains(neiId2)) {
                        state = 1;
                        canWithDraw = true;
                    }
                    doc.startObject();
                    final WorldCity worldCity = (WorldCity)this.dataGetter.getWorldCityCache().get((Object)neiId2);
                    doc.createElement("cityId", worldCity.getId());
                    doc.createElement("cityName", worldCity.getName());
                    doc.createElement("forceId", neiCity2.getForceId());
                    doc.createElement("state", state);
                    doc.endObject();
                }
                doc.endArray();
                if (!canWithDraw) {
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.BATTLE_NO_MOVABLE_CITY_CANNOT_WITHDRAW);
                }
            }
            else {
                ErrorSceneLog.getInstance().appendErrorMsg("neibors of city is empty").appendClassName("BattleService").appendMethodName("getQuitGeneral").append("cityId", cityId).flush();
            }
        }
        else if (battle.getBattleType() == 18) {
            final Set<Integer> neibors3 = this.dataGetter.getSoloRoadCache().getNeighbors(cityId);
            if (neibors3 != null && neibors3.size() > 0) {
                doc.startArray("cityOptions");
                boolean canWithDraw2 = false;
                for (final Integer neiId3 : neibors3) {
                    final JuBenCityDto neiJuBenCityDto2 = JuBenManager.getInstance().getJuBenCityDto(playerId, neiId3);
                    int state2 = 0;
                    if (playerDto.forceId == neiJuBenCityDto2.forceId && neiJuBenCityDto2.state == 0) {
                        state2 = 1;
                        canWithDraw2 = true;
                    }
                    doc.startObject();
                    final SoloCity soloCity = (SoloCity)this.dataGetter.getSoloCityCache().get((Object)neiId3);
                    doc.createElement("cityId", soloCity.getId());
                    doc.createElement("cityName", soloCity.getName());
                    doc.createElement("forceId", neiJuBenCityDto2.forceId);
                    doc.createElement("state", state2);
                    doc.endObject();
                }
                doc.endArray();
                if (!canWithDraw2) {
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.BATTLE_NO_MOVABLE_CITY_CANNOT_WITHDRAW);
                }
            }
            else {
                ErrorSceneLog.getInstance().appendErrorMsg("neibors of city is empty").appendClassName("BattleService").appendMethodName("getQuitGeneral").append("cityId", cityId).flush();
            }
        }
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public byte[] quitBattle(final PlayerDto playerDto, final String gIds, final String battleId, final int destinationCityId) {
        if (this.dataGetter.getAutoBattleService().InAutoBattleMode(playerDto.playerId)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.AUTO_BATTLE_CANNOT_OPERATE);
        }
        final int playerId = playerDto.playerId;
        final Battle battle = NewBattleManager.getInstance().getBattleByBatId(battleId);
        if (battle == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.BATTLE_ENDEND_CANNOT_WITHDRAW);
        }
        final String[] generalIds = gIds.split("#");
        final Set<Integer> gIdSet = new HashSet<Integer>();
        int gId = 0;
        String[] array;
        for (int length = (array = generalIds).length, i = 0; i < length; ++i) {
            final String str = array[i];
            try {
                gId = Integer.valueOf(str);
            }
            catch (Exception e2) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10012);
            }
            if (gId > 0) {
                gIdSet.add(Integer.valueOf(str));
            }
        }
        final int nowCityId = battle.getDefBaseInfo().getId();
        double foodNeed = 0.0;
        if (battle.getBattleType() == 3 || battle.getBattleType() == 14) {
            for (final int g : gIdSet) {
                final PlayerGeneralMilitary pgm = this.dataGetter.getPlayerGeneralMilitaryDao().getMilitary(playerId, g);
                if (pgm.getLocationId() != nowCityId) {
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10012);
                }
            }
            final Set<Integer> neibors = this.dataGetter.getWorldRoadCache().getNeighbors(nowCityId);
            if (destinationCityId <= 0 || destinationCityId > 280) {
                ErrorSceneLog.getInstance().appendErrorMsg("\u5916\u6302\u653b\u51fb, \u975e\u6cd5\u7684 destinationCityId").appendClassName("BattleService").appendMethodName("quitBattle").append("destinationCityId", destinationCityId).flush();
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
            }
            if (!neibors.contains(destinationCityId)) {
                ErrorSceneLog.getInstance().appendErrorMsg("\u5916\u6302\u653b\u51fb, destinationCityId \u4e0d\u662f\u5f53\u524d\u7684\u4e34\u8fd1\u57ce\u5e02.").appendClassName("BattleService").appendMethodName("quitBattle").append("nowCityId", nowCityId).append("destinationCityId", destinationCityId).flush();
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
            }
            final PlayerInfo pi = battle.getInBattlePlayers().get(playerId);
            if (pi.isAttSide()) {
                boolean attCanWithDraw = false;
                for (final int neiId : neibors) {
                    if (neiId == destinationCityId) {
                        final City neiCity = this.dataGetter.getCityDao().read(neiId);
                        if (neiCity.getForceId() == playerDto.forceId && neiCity.getState() == 0 && neiCity.getTitle() != 1 && neiCity.getTitle() != 2) {
                            attCanWithDraw = true;
                            break;
                        }
                        continue;
                    }
                }
            }
            else {
                final City nowcity = this.dataGetter.getCityDao().read(nowCityId);
                final int nowcityTitle = nowcity.getTitle();
                if (nowcityTitle != 1) {}
            }
            final City city = this.dataGetter.getCityDao().read(destinationCityId);
            if (city.getForceId() != playerDto.forceId || city.getState() == 1) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.BATTLE_NO_MOVABLE_CITY_CANNOT_WITHDRAW);
            }
            final List<PlayerGeneralMilitary> listAll = this.dataGetter.getPlayerGeneralMilitaryDao().getMilitaryList(playerId);
            final List<PlayerGeneralMilitary> list = new ArrayList<PlayerGeneralMilitary>();
            for (final PlayerGeneralMilitary pgm2 : listAll) {
                if (gIdSet.contains(pgm2.getGeneralId())) {
                    list.add(pgm2);
                }
            }
            foodNeed = this.dataGetter.getCityService().getMilitariesCost(list, nowCityId, destinationCityId, playerId, playerDto.forceId).left;
            final PlayerResource playerResource = this.dataGetter.getPlayerResourceDao().read(playerId);
            if (playerResource.getFood() < foodNeed) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10027);
            }
        }
        else if (battle.getBattleType() == 18) {
            for (final int g : gIdSet) {
                final PlayerGeneralMilitary pgm = this.dataGetter.getPlayerGeneralMilitaryDao().getMilitary(playerId, g);
                if (pgm.getJubenLoId() != nowCityId) {
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10012);
                }
            }
            final Set<Integer> neibors = this.dataGetter.getSoloRoadCache().getNeighbors(nowCityId);
            if (!neibors.contains(destinationCityId)) {
                ErrorSceneLog.getInstance().appendErrorMsg("\u5916\u6302\u653b\u51fb, destinationCityId \u4e0d\u662f\u5f53\u524d\u7684\u4e34\u8fd1\u57ce\u5e02.").appendClassName("BattleService").appendMethodName("quitBattle").append("nowCityId", nowCityId).append("destinationCityId", destinationCityId).flush();
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
            }
            final JuBenCityDto juBenCityDto = JuBenManager.getInstance().getJuBenCityDto(playerId, destinationCityId);
            if (juBenCityDto.forceId != playerDto.forceId || juBenCityDto.state == 1) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.BATTLE_NO_MOVABLE_CITY_CANNOT_WITHDRAW);
            }
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        try {
            if (battle.getBattleType() == 3) {
                final PlayerResource pr = this.dataGetter.getPlayerResourceDao().read(playerId);
                if (pr.getFood() < foodNeed) {
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10027);
                }
            }
            final Set<CampArmy> quitGids = battle.quit(playerId, gIdSet, this.dataGetter, false);
            if (quitGids == null) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.BATTLE_ON_QUEUE_CANNOT_WITHDRAW);
            }
            if (battle.getBattleType() == 3 || battle.getBattleType() == 14) {
                for (final CampArmy ca : quitGids) {
                    final PlayerGeneralMilitary pgm3 = this.dataGetter.getPlayerGeneralMilitaryDao().getMilitary(ca.getPlayerId(), ca.getGeneralId());
                    final double reduce = pgm3.getForces() * ((C)this.dataGetter.getcCache().get((Object)"World.MoveDuringFighting.DeserterProportion")).getValue();
                    this.dataGetter.getPlayerGeneralMilitaryDao().updateLocationForceSetState1(ca.getPlayerId(), ca.getGeneralId(), destinationCityId, reduce, new Date());
                    try {
                        final String cgm = this.dataGetter.getCityService().getColoredGeneralName(ca.getGeneralId());
                        this.dataGetter.getGeneralService().sendGmUpdate(ca.getPlayerId(), ca.getGeneralId(), false);
                        this.dataGetter.getCityDataCache().fireCityMoveMessage(ca.getPlayerId(), nowCityId, destinationCityId, cgm);
                        final Battle destBattle = NewBattleManager.getInstance().getBattleByDefId(3, destinationCityId);
                        final HuizhanHistory hh = this.dataGetter.getHuiZhanService().getTodayHuizhanInProcess();
                        if (hh == null || hh.getCityId() != destinationCityId || destBattle != null || hh.getDefForceId() != playerDto.forceId) {
                            continue;
                        }
                        this.dataGetter.getBattleService().updateHuizhanPlayerForce(destinationCityId, playerId, pgm3.getForces());
                        this.dataGetter.getBattleService().updateHuizhanNationForce(destinationCityId, playerDto.forceId, pgm3.getForces());
                    }
                    catch (Exception e) {
                        ErrorSceneLog.getInstance().error("BattleService quitBattle ", e);
                    }
                }
            }
            else if (battle.getBattleType() == 18) {
                for (final CampArmy ca : quitGids) {
                    final PlayerGeneralMilitary pgm3 = this.dataGetter.getPlayerGeneralMilitaryDao().getMilitary(ca.getPlayerId(), ca.getGeneralId());
                    final double reduce = pgm3.getForces() * ((C)this.dataGetter.getcCache().get((Object)"World.MoveDuringFighting.DeserterProportion")).getValue();
                    this.dataGetter.getPlayerGeneralMilitaryDao().consumeForces(pgm3.getPlayerId(), pgm3.getGeneralId(), reduce, new Date());
                    this.dataGetter.getPlayerGeneralMilitaryDao().moveJuben(ca.getPlayerId(), ca.getGeneralId(), 1, destinationCityId);
                    this.dataGetter.getGeneralService().sendGmUpdate(ca.getPlayerId(), ca.getGeneralId(), false);
                    ScenarioEventMessageHelper.sendMoveToCitykMessage(ca.getPlayerId(), ca.getGeneralId(), destinationCityId);
                }
            }
            doc.startArray("gIds");
            for (final CampArmy ca : quitGids) {
                doc.startObject();
                doc.createElement("gId", ca.getGeneralId());
                doc.createElement("gName", ca.getGeneralName());
                doc.createElement("gQuality", ca.getQuality());
                doc.endObject();
                if (battle != null && nowCityId >= 0 && destinationCityId > 0 && (battle.getBattleType() == 18 || battle.getBattleType() == 3 || battle.getBattleType() == 14)) {
                    if (battle.getBattleType() == 18) {
                        this.dataGetter.getJuBenService().sendAttMoveInfo(playerId, ca.getGeneralId(), nowCityId, destinationCityId, playerDto.forceId, "", 0L, false);
                    }
                    else {
                        this.dataGetter.getCityService().sendAttMoveInfo(playerId, ca.getGeneralId(), nowCityId, destinationCityId, playerDto.forceId, "", 0L, false);
                    }
                }
            }
            doc.endArray();
            if (battle.getBattleType() == 3 && foodNeed > 0.0) {
                this.dataGetter.getPlayerResourceDao().consumeFood(playerId, (int)foodNeed, "\u64a4\u9000\u6d88\u8017\u7cae\u98df");
            }
        }
        catch (Exception e2) {
            ErrorSceneLog.getInstance().error("battleservice quitBattle exception", e2);
        }
        doc.createElement("battleId", battleId);
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    private static int getBattleType(final int battleType, final int defId, final IDataGetter dataGetter, final int playerId) {
        if (battleType == 1 || battleType == 2) {
            final Armies armies = (Armies)dataGetter.getArmiesCache().get((Object)defId);
            if (armies == null) {
                final Battle battle = NewBattleManager.getInstance().getBattleByBatType(playerId, 1);
                if (battle != null) {
                    return battle.getBattleType();
                }
                return 2;
            }
            else {
                if (armies.getType() != 1) {
                    return 2;
                }
                return 1;
            }
        }
        else {
            if (battleType != 6 && battleType != 7) {
                if (battleType == 3) {
                    final PlayerWorld pw = dataGetter.getPlayerWorldDao().read(playerId);
                    if (pw != null && Builder.containsCityId(pw.getCanAttId(), defId)) {
                        return 10;
                    }
                }
                return battleType;
            }
            final Mine mine = (Mine)dataGetter.getMineCache().get((Object)defId);
            if (mine.getType() == 2 || mine.getType() == 4) {
                return 6;
            }
            return 7;
        }
    }
    
    @Transactional
    @Override
    public byte[] getBattleResult(final PlayerDto playerDto, final int vId) {
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        final int playerId = playerDto.playerId;
        final PlayerBattleReward pbr = this.dataGetter.getPlayerBattleRewardDao().read(vId);
        if (pbr == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10015);
        }
        if (pbr.getPlayerId() != playerId) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10012);
        }
        final Player player = this.dataGetter.getPlayerDao().read(playerId);
        int side = 1;
        boolean isNpc = true;
        if (pbr.getType() == 3 && player.getForceId() > 0) {
            isNpc = false;
            if (player.getForceId() != pbr.getDefForceId()) {
                side = 0;
            }
        }
        doc.createElement("terrain", pbr.getTerrain());
        doc.createElement("battleType", pbr.getType());
        if (pbr.getType() == 1 || pbr.getType() == 2) {
            doc.createElement("attName", player.getPlayerName());
            doc.createElement("attLv", player.getPlayerLv());
            doc.createElement("attPic", player.getPic());
            final Armies armies = (Armies)this.dataGetter.getArmiesCache().get((Object)pbr.getDefId());
            doc.createElement("defName", armies.getName());
            doc.createElement("defLv", armies.getLevel());
            doc.createElement("defPic", ((General)this.dataGetter.getGeneralCache().get((Object)armies.getChief())).getPic());
        }
        else if (pbr.getType() == 3) {
            if (isNpc) {
                doc.createElement("attName", (Object)(String.valueOf(WorldCityCommon.nationIdNameMap.get(player.getForceId())) + LocalMessages.T_FORCE_NATION));
                doc.createElement("attLv", player.getPlayerLv());
                doc.createElement("attPic", player.getForceId());
                doc.createElement("npcFlag", "NPC");
                final Army army = (Army)this.dataGetter.getArmyCache().get((Object)((WorldCity)this.dataGetter.getWorldCityCache().get((Object)pbr.getDefId())).getChief());
                doc.createElement("defName", army.getName());
                doc.createElement("defPic", ((General)this.dataGetter.getGeneralCache().get((Object)army.getGeneralId())).getPic());
                doc.createElement("defLv", army.getGeneralLv());
            }
            else if (side == 1) {
                doc.createElement("attName", (Object)(String.valueOf(WorldCityCommon.nationIdNameMap.get(player.getForceId())) + LocalMessages.T_FORCE_NATION));
                doc.createElement("attLv", player.getPlayerLv());
                doc.createElement("attPic", player.getForceId());
                doc.createElement("defName", (Object)(String.valueOf(WorldCityCommon.nationIdNameMap.get(pbr.getDefForceId())) + LocalMessages.T_FORCE_NATION));
                doc.createElement("defLv", pbr.getDefForceId());
                doc.createElement("defPic", pbr.getDefForceId());
            }
            else {
                doc.createElement("attName", (Object)(String.valueOf(WorldCityCommon.nationIdNameMap.get(pbr.getAttForceId())) + LocalMessages.T_FORCE_NATION));
                doc.createElement("attLv", pbr.getAttForceId());
                doc.createElement("attPic", pbr.getAttForceId());
                doc.createElement("defName", (Object)(String.valueOf(WorldCityCommon.nationIdNameMap.get(player.getForceId())) + LocalMessages.T_FORCE_NATION));
                doc.createElement("defPic", player.getPlayerLv());
                doc.createElement("defLv", player.getForceId());
            }
        }
        doc.createElement("result", pbr.getWinSide());
        final StringBuilder sb = new StringBuilder();
        if (pbr.getBonusDrop() != null) {
            sb.append(pbr.getBonusDrop());
        }
        else {
            sb.append(1).append("*").append(pbr.getCopper()).append("|").append(5).append("*").append(pbr.getMExp());
        }
        doc.createElement("drop", sb.toString());
        doc.createElement("maxKillG", pbr.getMaxKillGnum());
        if (pbr.getRbReward() != null) {
            final String[] strs = pbr.getRbReward().split("#");
            doc.createElement("rbType", strs[0]);
            doc.createElement("rbNum", strs[1]);
            doc.createElement("rbTop", strs[2]);
        }
        doc.createElement("attLoss", pbr.getAttLost());
        doc.createElement("defLoss", pbr.getDefLost());
        this.dataGetter.getPlayerBattleRewardDao().deleteById(vId);
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Transactional
    @Override
    public byte[] deleteResult(final PlayerDto playerDto, final int vId) {
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        final int playerId = playerDto.playerId;
        final PlayerBattleReward pbr = this.dataGetter.getPlayerBattleRewardDao().read(vId);
        if (pbr == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10015);
        }
        if (pbr.getPlayerId() != playerId) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10012);
        }
        this.dataGetter.getPlayerBattleRewardDao().deleteById(vId);
        doc.createElement("del", true);
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public byte[] helpInfo(final PlayerDto playerDto, final String battleId) {
        final Battle bat = NewBattleManager.getInstance().getBattleByBatId(battleId);
        if (bat == null || bat.getAttBaseInfo().getId() != playerDto.playerId) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.BATTLE_END_INFO_3);
        }
        if (System.currentTimeMillis() - bat.getSendMsgTime() < 60000L) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.BATTLE_SEND_CHAT_INFO);
        }
        bat.setSendMsgTime(System.currentTimeMillis());
        if (bat.getBattleType() == 2) {
            final String msg = MessageFormatter.format(LocalMessages.MINE_START_BATTTLE_CHAT_INFO, new Object[] { ColorUtil.getSpecialColorMsg(playerDto.playerName), ColorUtil.getSpecialColorMsg(((Armies)this.dataGetter.getArmiesCache().get((Object)bat.getDefBaseInfo().getId())).getName()) });
            final String param = String.valueOf(bat.getBattleType()) + "#" + bat.getDefBaseInfo().getId();
            this.dataGetter.getChatService().sendSystemChat("GLOBAL", playerDto.playerId, playerDto.forceId, msg, new ChatLink(1, param));
        }
        else if (bat.getBattleType() == 7) {
            final Mine mine = (Mine)this.dataGetter.getMineCache().get((Object)bat.getDefBaseInfo().getId());
            final String msg2 = MessageFormatter.format(LocalMessages.MINE_START_BATTTLE_CHAT_INFO, new Object[] { ColorUtil.getSpecialColorMsg(playerDto.playerName), ColorUtil.getSpecialColorMsg(mine.getName()) });
            final String param2 = String.valueOf(bat.getBattleType()) + "#" + bat.getDefBaseInfo().getId();
            this.dataGetter.getChatService().sendSystemChat("GLOBAL", playerDto.playerId, playerDto.forceId, msg2, new ChatLink(1, param2));
        }
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Override
    public CampArmyParam[] getKfCampDatas(final int playerId, final String gIds) {
        return null;
    }
    
    public static String getKfCurReport(final String reports, final boolean isAttSide, final MatchFightMember mfmA, final MatchFightMember mfmB) {
        final List<String> att1 = new ArrayList<String>();
        final List<String> def1 = new ArrayList<String>();
        final List<String> att2 = new ArrayList<String>();
        final List<String> def2 = new ArrayList<String>();
        String att3 = null;
        String def3 = null;
        String report3 = null;
        List<String> temp = null;
        final String[] strsE = reports.split(":");
        for (int i = 0; i < strsE.length; ++i) {
            final String[] strsB = strsE[i].split("#");
            for (int j = 1; j < strsB.length; ++j) {
                final int reportId = Integer.valueOf(strsB[j].substring(0, strsB[j].indexOf("|")));
                if (reportId == 1) {
                    final String[] strsC = strsB[j].split(";");
                    temp = def1;
                    if (strsC[0].split("\\|")[1].equals("att")) {
                        temp = att1;
                    }
                    for (int k = 1; k < strsC.length; ++k) {
                        temp.add(strsC[k]);
                    }
                }
                else if (reportId == 2) {
                    final String[] strsC = strsB[j].split(";");
                    temp = def2;
                    if (strsC[0].split("\\|")[1].equals("att")) {
                        temp = att2;
                    }
                    for (int k = 1; k < strsC.length; ++k) {
                        temp.add(strsC[k]);
                    }
                }
                else if (reportId == 3) {
                    report3 = strsB[j];
                }
                else if (reportId != 8 && reportId == 11) {
                    final String[] strsC = strsB[j].split(";");
                    if (strsC[0].split("\\|")[1].equals("att")) {
                        att3 = strsB[j];
                    }
                    else {
                        def3 = strsB[j];
                    }
                }
            }
        }
        int attIndex = 0;
        int defIndex = 0;
        if (report3 != null) {
            final String[] attVsdef = report3.substring(0, report3.indexOf(";")).split("\\|");
            attIndex = (Integer.valueOf(attVsdef[2]) / 10 - 1) * 3;
            defIndex = (Integer.valueOf(attVsdef[3]) / 10 - 1) * 3;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append(0).append("#").append(17).append("|").append(isAttSide ? 1 : 0).append("#");
        sb.append(23).append("|").append("att").append(";").append(mfmA.getPlayerName()).append("|").append(mfmA.getPlayerLv()).append("|").append(mfmA.getPlayerPic()).append("#");
        sb.append(23).append("|").append("def").append(";").append(mfmB.getPlayerName()).append("|").append(mfmB.getPlayerLv()).append("|").append(mfmB.getPlayerPic()).append("#");
        sb.append(1).append("|").append("att").append(";");
        for (int l = 0; l < att1.size(); ++l) {
            sb.append(att1.get(l)).append(";");
        }
        sb.append("#");
        sb.append(1).append("|").append("def").append(";");
        for (int l = 0; l < def1.size(); ++l) {
            sb.append(def1.get(l)).append(";");
        }
        sb.append("#");
        sb.append(10).append("|").append("att").append(";");
        for (int l = attIndex + 3; l < att2.size(); ++l) {
            sb.append(att2.get(l)).append(";");
        }
        sb.append("#");
        sb.append(10).append("|").append("def").append(";");
        for (int l = defIndex + 3; l < def2.size(); ++l) {
            sb.append(def2.get(l)).append(";");
        }
        sb.append("#");
        if (report3 != null) {
            changeToCurReprot(att2, def2, report3, sb, attIndex, defIndex);
        }
        if (att3 != null) {
            sb.append(att3).append("#");
        }
        if (def3 != null) {
            sb.append(def3).append("#");
        }
        return sb.toString();
    }
    
    private static void changeToCurReprot(final List<String> att2, final List<String> def2, final String report3, final StringBuilder sb, final int attIndex, final int defIndex) {
        sb.append(12).append("|").append("*");
        for (int i = 0; i < 3; ++i) {
            sb.append(att2.get(attIndex + i)).append(";");
        }
        sb.replace(sb.length() - 1, sb.length(), "*");
        for (int i = 0; i < 3; ++i) {
            sb.append(def2.get(defIndex + i)).append(";");
        }
        sb.replace(sb.length() - 1, sb.length(), "*");
        final String[] strsD = report3.split("\\*");
        for (int j = 0; j < strsD.length; ++j) {
            sb.append(strsD[j].substring(strsD[j].lastIndexOf(";") + 1)).append(",");
        }
        sb.replace(sb.length() - 1, sb.length(), "#");
    }
    
    @Override
    public byte[] useStrategy(final PlayerDto playerDto, final String battleId, final int strategyId, final int position) {
        if (battleId == null || battleId.equalsIgnoreCase("null") || battleId.trim().isEmpty()) {
            return JsonBuilder.getJson(State.FAIL, "illegal battleId:" + battleId);
        }
        final int playerId = playerDto.playerId;
        final Battle battle = NewBattleManager.getInstance().getBattleByBatId(battleId);
        if (battle == null || !battle.isBattleDoing()) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.BATTLE_END_STRATEGY_CANNOT_USE);
        }
        return battle.useStrategy(playerId, strategyId, this.dataGetter, position);
    }
    
    boolean isSurrounded(final PlayerDto playerDto, final Battle battle) {
        final int defId = battle.getDefBaseInfo().getId();
        switch (battle.getBattleType()) {
            case 3: {
                final City city = this.dataGetter.getCityDao().read(defId);
                return playerDto.forceId == city.getForceId() && city.getTitle() == 1;
            }
            case 14: {
                return false;
            }
            case 18: {
                final JuBenCityDto juBenCityDto = JuBenManager.getInstance().getJuBenCityDto(playerDto.playerId, defId);
                return playerDto.forceId == juBenCityDto.forceId && (juBenCityDto.title == 1 || juBenCityDto.title == 2);
            }
            default: {
                return false;
            }
        }
    }
    
    Tuple<Boolean, String> canThisBattleDoAssemble(final PlayerDto playerDto, final Battle battle) {
        final Tuple<Boolean, String> result = new Tuple();
        result.left = false;
        if (battle.getBattleType() != 3 && battle.getBattleType() != 14 && battle.getBattleType() != 18) {
            result.right = LocalMessages.BATTLE_TYPE_ERROR_CANNOT_ASSEMBLE;
            ErrorSceneLog.getInstance().appendErrorMsg("plug attack. battle is not city or barbarain battle").appendClassName("BattleService").appendMethodName("AssembleBattle").append("battleId", battle.getBattleId()).appendPlayerId(playerDto.playerId).appendPlayerName(playerDto.playerName).flush();
            return result;
        }
        if (battle.getBattleType() == 3) {
            final int cityId = battle.getDefBaseInfo().getId();
            final City city = this.dataGetter.getCityDao().read(cityId);
            if (city.getForceId() != playerDto.forceId) {
                final Stratagem stratagem = this.dataGetter.getCilvilTrickService().afterStateTrick(cityId, 5, -1);
                if (stratagem != null) {
                    result.right = LocalMessages.CITY_UNDER_PROTECT;
                    return result;
                }
            }
        }
        result.left = true;
        return result;
    }
    
    boolean isPgmAvilable(final PlayerGeneralMilitary pgm) {
        if (pgm.getState() > 1) {
            return false;
        }
        final boolean joinTeam = TeamManager.getInstance().isJoinTeam(pgm.getPlayerId(), pgm.getGeneralId());
        if (joinTeam) {
            return false;
        }
        final GeneralMoveDto gmd = CityService.getGeneralMoveDto(pgm.getPlayerId(), pgm.getGeneralId());
        if (gmd != null) {
            CDUtil.isInCD(gmd.nextMoveTime, new Date());
        }
        return gmd == null || (gmd.cityState != 22 && gmd.cityState != 23);
    }
    
    boolean isPgmAvilableMove(final PlayerGeneralMilitary pgm, final int cityId) {
        if (pgm.getState() > 1 && pgm.getState() != 24) {
            return false;
        }
        final boolean joinTeam = TeamManager.getInstance().isJoinTeam(pgm.getPlayerId(), pgm.getGeneralId());
        if (joinTeam) {
            return false;
        }
        final GeneralMoveDto gmd = CityService.getGeneralMoveDto(pgm.getPlayerId(), pgm.getGeneralId());
        if (gmd != null && gmd.moveLine.length() > 0) {
            final String[] strs = gmd.moveLine.split(",");
            final int endCityId = Integer.valueOf(strs[strs.length - 1]);
            if (cityId == endCityId) {
                return false;
            }
        }
        return gmd.cityState != 22 && gmd.cityState != 23;
    }
    
    boolean isAvilable(final Battle battle, final PlayerGeneralMilitary pgm) {
        if (pgm.getState() > 1) {
            return false;
        }
        final int maxHp = this.dataGetter.getBattleDataCache().getMaxHp(pgm);
        if (pgm.getForces() * 1.0 / maxHp < 0.05) {
            return false;
        }
        switch (battle.getBattleType()) {
            case 3:
            case 14: {
                final boolean joinTeam = TeamManager.getInstance().isJoinTeam(pgm.getPlayerId(), pgm.getGeneralId());
                if (joinTeam) {
                    return false;
                }
                final GeneralMoveDto gmd = CityService.getGeneralMoveDto(pgm.getPlayerId(), pgm.getGeneralId());
                if (gmd != null) {
                    CDUtil.isInCD(gmd.nextMoveTime, new Date());
                }
                if (gmd != null && (gmd.cityState == 22 || gmd.cityState == 23)) {
                    return false;
                }
                break;
            }
        }
        return true;
    }
    
    private byte[] getAssembleGeneralFree(final PlayerDto playerDto, final int cityId) {
        final List<PlayerGeneralMilitary> pgmList = this.dataGetter.getPlayerGeneralMilitaryDao().getMilitaryList(playerDto.playerId);
        if (pgmList.size() == 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.THIS_PLAYER_HAS_NO_PGM);
        }
        final List<PlayerGeneralMilitary> pgmListAvailable = new LinkedList<PlayerGeneralMilitary>();
        boolean comeHere = false;
        for (final PlayerGeneralMilitary pgm : pgmList) {
            if (this.isPgmAvilable(pgm)) {
                final GeneralMoveDto gmd = CityService.getGeneralMoveDto(pgm.getPlayerId(), pgm.getGeneralId());
                if (gmd != null && gmd.moveLine.length() > 0) {
                    final String[] strs = gmd.moveLine.split(",");
                    final int endCityId = Integer.valueOf(strs[strs.length - 1]);
                    if (cityId == endCityId) {
                        comeHere = true;
                        continue;
                    }
                }
                pgmListAvailable.add(pgm);
            }
        }
        if (pgmListAvailable.size() != 0) {
            final JsonDocument doc = new JsonDocument();
            doc.startObject();
            doc.startArray("gIds");
            for (final PlayerGeneralMilitary pgm2 : pgmListAvailable) {
                final General general = (General)this.dataGetter.getGeneralCache().get((Object)pgm2.getGeneralId());
                doc.startObject();
                doc.createElement("gId", pgm2.getGeneralId());
                doc.createElement("gName", general.getName());
                doc.createElement("gPic", general.getPic());
                doc.createElement("gQuality", general.getQuality());
                doc.endObject();
            }
            doc.endArray();
            doc.endObject();
            return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
        }
        if (!comeHere) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.ALL_PGM_IS_BUSY_OR_LOW_HP_CANNOT_ASSEMBLE);
        }
        return JsonBuilder.getJson(State.FAIL, LocalMessages.PGM_COME_HERE_ASSEMBLE);
    }
    
    private byte[] getAssembleGeneralBattle(final PlayerDto playerDto, final Battle battle) {
        final Tuple<Boolean, String> tuple = this.canThisBattleDoAssemble(playerDto, battle);
        if (!(boolean)tuple.left) {
            return JsonBuilder.getJson(State.FAIL, tuple.right);
        }
        final int playerId = playerDto.playerId;
        final boolean isSurrounded = this.isSurrounded(playerDto, battle);
        if (isSurrounded) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.CURRENT_PGM_IS_SURROUND_CANNOT_ASSEMBLE);
        }
        final List<PlayerGeneralMilitary> pgmList = this.dataGetter.getPlayerGeneralMilitaryDao().getMilitaryList(playerId);
        if (pgmList.size() == 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.THIS_PLAYER_HAS_NO_PGM);
        }
        final List<PlayerGeneralMilitary> pgmListAvailable = new LinkedList<PlayerGeneralMilitary>();
        boolean comeHere = false;
        for (final PlayerGeneralMilitary pgm : pgmList) {
            if (this.isAvilable(battle, pgm)) {
                final GeneralMoveDto gmd = CityService.getGeneralMoveDto(pgm.getPlayerId(), pgm.getGeneralId());
                if (gmd != null && gmd.moveLine.length() > 0) {
                    final String[] strs = gmd.moveLine.split(",");
                    final int endCityId = Integer.valueOf(strs[strs.length - 1]);
                    if (battle.getDefBaseInfo().getId() == endCityId) {
                        comeHere = true;
                        continue;
                    }
                }
                pgmListAvailable.add(pgm);
            }
        }
        if (pgmListAvailable.size() != 0) {
            final JsonDocument doc = new JsonDocument();
            doc.startObject();
            doc.startArray("gIds");
            for (final PlayerGeneralMilitary pgm2 : pgmListAvailable) {
                final General general = (General)this.dataGetter.getGeneralCache().get((Object)pgm2.getGeneralId());
                doc.startObject();
                doc.createElement("gId", pgm2.getGeneralId());
                doc.createElement("gName", general.getName());
                doc.createElement("gPic", general.getPic());
                doc.createElement("gQuality", general.getQuality());
                doc.endObject();
            }
            doc.endArray();
            doc.endObject();
            return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
        }
        if (!comeHere) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.ALL_PGM_IS_BUSY_OR_LOW_HP_CANNOT_ASSEMBLE);
        }
        return JsonBuilder.getJson(State.FAIL, LocalMessages.PGM_COME_HERE_ASSEMBLE);
    }
    
    @Override
    public byte[] getAssembleGeneral(final PlayerDto playerDto, final int cityId) {
        final Battle battle = NewBattleManager.getInstance().getBattleByDefId(3, cityId);
        if (battle == null) {
            return this.getAssembleGeneralFree(playerDto, cityId);
        }
        return this.getAssembleGeneralBattle(playerDto, battle);
    }
    
    @Override
    public Tuple<Boolean, Object> AssembleBattleAllFree(final PlayerDto playerDto, final int cityId, final Set<Integer> gIdSet) {
        final Tuple<Boolean, Object> resuTuple = new Tuple();
        resuTuple.left = false;
        final int playerId = playerDto.playerId;
        final List<PlayerGeneralMilitary> pgmList = this.dataGetter.getPlayerGeneralMilitaryDao().getMilitaryList(playerId);
        if (pgmList.size() == 0) {
            resuTuple.right = LocalMessages.THIS_PLAYER_HAS_NO_PGM;
            return resuTuple;
        }
        final List<PlayerGeneralMilitary> pgmListAvailable = new LinkedList<PlayerGeneralMilitary>();
        for (final PlayerGeneralMilitary pgm : pgmList) {
            if (gIdSet.contains(pgm.getGeneralId()) && this.isPgmAvilableMove(pgm, cityId)) {
                pgmListAvailable.add(pgm);
            }
        }
        if (pgmListAvailable.size() == 0) {
            resuTuple.right = LocalMessages.ALL_PGM_IS_BUSY_OR_LOW_HP_CANNOT_ASSEMBLE;
            return resuTuple;
        }
        final List<PlayerGeneralMilitary> succList = new LinkedList<PlayerGeneralMilitary>();
        String reason = null;
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.startArray("failGIds");
        for (final PlayerGeneralMilitary pgm2 : pgmListAvailable) {
            final Tuple<Boolean, String> tuple = this.dataGetter.getCityService().assembleMove(playerId, pgm2.getGeneralId(), cityId, 1);
            if (!(boolean)tuple.left) {
                reason = tuple.right;
                doc.startObject();
                doc.createElement("gId", pgm2.getGeneralId());
                doc.createElement("gName", ((General)this.dataGetter.getGeneralCache().get((Object)pgm2.getGeneralId())).getName());
                doc.createElement("reason", tuple.right);
                doc.endObject();
            }
            else {
                succList.add(pgm2);
            }
        }
        doc.endArray();
        if (succList.size() == 0) {
            resuTuple.right = reason;
            return resuTuple;
        }
        doc.startArray("succGIds");
        for (final PlayerGeneralMilitary pgm2 : succList) {
            doc.startObject();
            doc.createElement("gId", pgm2.getGeneralId());
            doc.createElement("gName", ((General)this.dataGetter.getGeneralCache().get((Object)pgm2.getGeneralId())).getName());
            doc.endObject();
        }
        doc.endArray();
        doc.endObject();
        resuTuple.left = true;
        resuTuple.right = doc;
        return resuTuple;
    }
    
    @Override
    public Tuple<Boolean, Object> AssembleBattleAllBattle(final PlayerDto playerDto, final Battle battle, final Set<Integer> gIdSet) {
        final Tuple<Boolean, Object> resuTuple = new Tuple();
        resuTuple.left = false;
        Tuple<Boolean, String> tuple = this.canThisBattleDoAssemble(playerDto, battle);
        if (!(boolean)tuple.left) {
            resuTuple.right = tuple.right;
            return resuTuple;
        }
        final int playerId = playerDto.playerId;
        final boolean isSurrounded = this.isSurrounded(playerDto, battle);
        if (isSurrounded) {
            resuTuple.right = LocalMessages.CURRENT_PGM_IS_SURROUND_CANNOT_ASSEMBLE;
            return resuTuple;
        }
        final List<PlayerGeneralMilitary> pgmList = this.dataGetter.getPlayerGeneralMilitaryDao().getMilitaryList(playerId);
        if (pgmList.size() == 0) {
            resuTuple.right = LocalMessages.THIS_PLAYER_HAS_NO_PGM;
            return resuTuple;
        }
        final List<PlayerGeneralMilitary> pgmListAvailable = new LinkedList<PlayerGeneralMilitary>();
        for (final PlayerGeneralMilitary pgm : pgmList) {
            if (gIdSet.contains(pgm.getGeneralId()) && this.isAvilable(battle, pgm)) {
                pgmListAvailable.add(pgm);
            }
        }
        if (pgmListAvailable.size() == 0) {
            resuTuple.right = LocalMessages.ALL_PGM_IS_BUSY_OR_LOW_HP_CANNOT_ASSEMBLE;
            return resuTuple;
        }
        final List<PlayerGeneralMilitary> succList = new LinkedList<PlayerGeneralMilitary>();
        String reason = null;
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.startArray("failGIds");
        for (final PlayerGeneralMilitary pgm2 : pgmListAvailable) {
            if (battle.getBattleType() == 3 || battle.getBattleType() == 14) {
                tuple = this.dataGetter.getCityService().assembleMove(playerId, pgm2.getGeneralId(), battle.getDefBaseInfo().getId(), 1);
            }
            else {
                tuple = this.dataGetter.getJuBenService().assembleMove(playerId, pgm2.getGeneralId(), battle.getDefBaseInfo().getId(), 1);
            }
            if (!(boolean)tuple.left) {
                reason = tuple.right;
                doc.startObject();
                doc.createElement("gId", pgm2.getGeneralId());
                doc.createElement("gName", ((General)this.dataGetter.getGeneralCache().get((Object)pgm2.getGeneralId())).getName());
                doc.createElement("reason", tuple.right);
                doc.endObject();
            }
            else {
                succList.add(pgm2);
            }
        }
        doc.endArray();
        if (succList.size() == 0) {
            resuTuple.right = reason;
            return resuTuple;
        }
        doc.startArray("succGIds");
        for (final PlayerGeneralMilitary pgm2 : succList) {
            doc.startObject();
            doc.createElement("gId", pgm2.getGeneralId());
            doc.createElement("gName", ((General)this.dataGetter.getGeneralCache().get((Object)pgm2.getGeneralId())).getName());
            doc.endObject();
        }
        doc.endArray();
        doc.endObject();
        resuTuple.left = true;
        resuTuple.right = doc;
        return resuTuple;
    }
    
    @Override
    public byte[] AssembleBattleAll(final PlayerDto playerDto, final String gIds, final int cityId) {
        if (this.dataGetter.getAutoBattleService().InAutoBattleMode(playerDto.playerId)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.AUTO_BATTLE_CANNOT_OPERATE);
        }
        if (gIds == null || gIds.trim().isEmpty()) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.ASSEMBLE_NO_PGM_CHOOSED);
        }
        if (WorldCityCommon.barbarainCitySet.contains(cityId)) {
            final Builder barbarBuilder = BuilderFactory.getInstance().getBuilder(14);
            final Player player = this.dataGetter.getPlayerDao().read(playerDto.playerId);
            final Tuple<Boolean, String> tuple = barbarBuilder.canCreateBattle(player, cityId, this.dataGetter);
            if (!(boolean)tuple.left) {
                return JsonBuilder.getJson(State.FAIL, tuple.right);
            }
        }
        final String[] generalIds = gIds.split("#");
        final Set<Integer> gIdSet = new HashSet<Integer>();
        int gId = 0;
        String[] array;
        for (int length = (array = generalIds).length, i = 0; i < length; ++i) {
            final String str = array[i];
            try {
                gId = Integer.valueOf(str);
            }
            catch (Exception e) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.PLUG_IS_SHAMEFUL);
            }
            if (gId > 0) {
                gIdSet.add(Integer.valueOf(str));
            }
        }
        final Integer farmCityId = WorldFarmCache.forceCityIdMap.get(playerDto.forceId);
        if (farmCityId != null && farmCityId == cityId) {
            final List<StoreHouse> storeHouse = this.dataGetter.getStoreHouseDao().getByItemId(playerDto.playerId, 1701, 20);
            if (storeHouse == null || storeHouse.size() <= 0 || storeHouse.get(0).getNum() < gIdSet.size()) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.FARM_TOKEN_IS_NOT_ENOUGH);
            }
        }
        Tuple<Boolean, Object> resuTuple = null;
        final Battle battle = NewBattleManager.getInstance().getBattleByDefId(3, cityId);
        if (battle == null) {
            resuTuple = this.AssembleBattleAllFree(playerDto, cityId, gIdSet);
        }
        else {
            resuTuple = this.AssembleBattleAllBattle(playerDto, battle, gIdSet);
        }
        if (resuTuple.left) {
            final JsonDocument doc = (JsonDocument)resuTuple.right;
            return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
        }
        final String failReason = (String)resuTuple.right;
        return JsonBuilder.getJson(State.FAIL, failReason);
    }
    
    @Override
    public byte[] AssembleBattle(final PlayerDto playerDto, final int gId, final String battleId) {
        if (this.dataGetter.getAutoBattleService().InAutoBattleMode(playerDto.playerId)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.AUTO_BATTLE_CANNOT_OPERATE);
        }
        final Battle battle = NewBattleManager.getInstance().getBattleByBatId(battleId);
        if (battle == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.BATTLE_ENDED_CANNOT_ASSEMBLE);
        }
        Tuple<Boolean, String> tuple = this.canThisBattleDoAssemble(playerDto, battle);
        if (!(boolean)tuple.left) {
            return JsonBuilder.getJson(State.FAIL, tuple.right);
        }
        final int playerId = playerDto.playerId;
        final boolean isSurrounded = this.isSurrounded(playerDto, battle);
        if (isSurrounded) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.CURRENT_PGM_IS_SURROUND_CANNOT_ASSEMBLE);
        }
        final List<PlayerGeneralMilitary> pgmList = this.dataGetter.getPlayerGeneralMilitaryDao().getMilitaryList(playerId);
        if (pgmList.size() == 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.THIS_PLAYER_HAS_NO_PGM);
        }
        PlayerGeneralMilitary resultPgm = null;
        for (final PlayerGeneralMilitary pgm : pgmList) {
            if (pgm.getGeneralId() == gId && this.isAvilable(battle, pgm)) {
                resultPgm = pgm;
            }
        }
        if (resultPgm == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.ALL_PGM_IS_BUSY_OR_LOW_HP_CANNOT_ASSEMBLE);
        }
        if (battle.getBattleType() == 3 || battle.getBattleType() == 14) {
            tuple = this.dataGetter.getCityService().assembleMove(playerId, resultPgm.getGeneralId(), battle.getDefBaseInfo().getId(), 1);
        }
        else {
            tuple = this.dataGetter.getJuBenService().assembleMove(playerId, resultPgm.getGeneralId(), battle.getDefBaseInfo().getId(), 1);
        }
        if (!(boolean)tuple.left) {
            return JsonBuilder.getJson(State.FAIL, tuple.right);
        }
        return JsonBuilder.getJson(State.SUCCESS, tuple.right);
    }
    
    @Override
    public byte[] getCopyArmyCost(final PlayerDto playerDto) {
        final Chargeitem ci = (Chargeitem)this.dataGetter.getChargeitemCache().get((Object)53);
        if (playerDto.consumeLv < ci.getLv()) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10002);
        }
        int gold = 0;
        final PlayerBattleAttribute pba = this.dataGetter.getPlayerBattleAttributeDao().read(playerDto.playerId);
        if (pba.getVip3PhantomCount() < 1) {
            gold = ci.getCost();
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("gold", gold);
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    CampArmy savePlayerPhantom(final PlayerDto playerDto, final Battle battle, final CampArmy highCa, final int battleSide) {
        final Builder builder = BuilderFactory.getInstance().getBuilder(battle.getBattleType());
        CampArmy CaPhantom = null;
        final int cityId = battle.getDefBaseInfo().getId();
        if (battle.getBattleType() == 3 || battle.getBattleType() == 14) {
            final PlayerGeneralMilitaryPhantom phantom = new PlayerGeneralMilitaryPhantom();
            phantom.setBuyTime(new Date());
            phantom.setPlayerId(highCa.getPlayerId());
            phantom.setForceId(highCa.getForceId());
            phantom.setPlayerLv(highCa.getPlayerLv());
            phantom.setGeneralId(highCa.getGeneralId());
            phantom.setGeneralLv(highCa.getGeneralLv());
            phantom.setTroopId(highCa.getTroopId());
            phantom.setStrength(highCa.getStrength());
            phantom.setLeader(highCa.getLeader());
            phantom.setAtt(highCa.getAttEffect());
            phantom.setDef(highCa.getDefEffect());
            phantom.setColumnNum(highCa.getColumn());
            int maxHp = highCa.getMaxForces();
            final int remainder = maxHp % (3 * phantom.getColumnNum());
            maxHp -= remainder;
            phantom.setHp(maxHp);
            phantom.setHpMax(maxHp);
            phantom.setLocationId(cityId);
            if (highCa.getAttDef_B() != null) {
                phantom.setAttB(highCa.getAttDef_B().ATT_B);
                phantom.setDefB(highCa.getAttDef_B().DEF_B);
            }
            else {
                phantom.setAttB(0);
                phantom.setDefB(0);
            }
            phantom.setTacticAtt(highCa.getTACTIC_ATT());
            phantom.setTacticDef(highCa.getTACTIC_DEF());
            this.dataGetter.getPlayerGeneralMilitaryPhantomDao().create(phantom);
            CaPhantom = builder.copyArmyFromPhantom(this.dataGetter, battle, phantom, battleSide);
        }
        else if (battle.getBattleType() == 18) {
            final int pgmVId = highCa.getPgmVId();
            final PlayerGeneralMilitary pgm = this.dataGetter.getPlayerGeneralMilitaryDao().read(pgmVId);
            final Player player = this.dataGetter.getPlayerDao().read(playerDto.playerId);
            final JuBenDto juBenDto = JuBenManager.getInstance().getByPid(playerDto.playerId);
            final JuBenCityDto juBenCityDto = JuBenManager.getInstance().getJuBenCityDto(playerDto.playerId, battle.getDefBaseInfo().getId());
            final ScenarioNpc scenarioNpc = new ScenarioNpc();
            scenarioNpc.setPlayerId(playerDto.playerId);
            scenarioNpc.setScenarioId(juBenDto.juBen_id);
            scenarioNpc.setLocationId(juBenCityDto.cityId);
            scenarioNpc.setForceId(highCa.getForceId());
            scenarioNpc.setNpcType(1);
            scenarioNpc.setArmyId(pgm.getVId());
            int maxHp2 = highCa.getMaxForces();
            final int remainder2 = maxHp2 % (3 * highCa.getColumn());
            maxHp2 -= remainder2;
            scenarioNpc.setHp(maxHp2);
            scenarioNpc.setTacticVal(1);
            scenarioNpc.setState(19);
            scenarioNpc.setAddTime(new Date());
            scenarioNpc.setVId(JuBenService.scenarioNpcVId.incrementAndGet());
            this.dataGetter.getScenarioNpcDao().create(scenarioNpc);
            CaPhantom = builder.copyArmyFromScenarioNpc(this.dataGetter, player, battle, scenarioNpc, battleSide);
            final JuBenDto juBenDto2 = juBenDto;
            ++juBenDto2.jieBingCount;
            this.dataGetter.getPlayerScenarioDao().updateJieBingCount(playerDto.playerId, juBenDto.juBen_id, juBenDto.jieBingCount);
        }
        return CaPhantom;
    }
    
    @Transactional
    @Override
    public byte[] doCopyArmy(final PlayerDto playerDto, final String battleId) {
        final Battle battle = NewBattleManager.getInstance().getBattleByBatId(battleId);
        if (battle == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.BATTLE_ENDEDN_CANNOT_BUY_PHANTOM);
        }
        final int cityId = battle.getDefBaseInfo().getId();
        if (battle.getBattleType() != 3 && battle.getBattleType() != 14 && battle.getBattleType() != 18) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.THIS_BATTLE_TYPE_CANNOT_BUY_PHANTOM);
        }
        if (battle.getBattleType() == 18) {
            final JuBenDto juBenDto = JuBenManager.getInstance().getByPid(playerDto.playerId);
            if (juBenDto == null) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.PLUG_IS_SHAMEFUL);
            }
            if (juBenDto.jieBingCount >= juBenDto.maxJieBingCount) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.JUBEN_REACH_MAX_JIEBING_COUNT);
            }
        }
        synchronized (battle.getBattleId()) {
            final PlayerInfo pi = battle.getInBattlePlayers().get(playerDto.playerId);
            if (pi == null) {
                // monitorexit(battle.getBattleId())
                return JsonBuilder.getJson(State.FAIL, LocalMessages.YOU_ARE_NOT_IN_THIS_BATTLE_CANNOT_BUY_PHANTOM);
            }
            int battleSide = -1;
            CampArmy highCa = null;
            boolean specialGeneral = false;
            if (pi.isAttSide()) {
                battleSide = 1;
                for (final CampArmy temp : battle.getAttCamp()) {
                    if (temp.getPlayerId() == playerDto.playerId && !temp.isPhantom()) {
                        if (temp.getSpecialGeneral().generalType == 4) {
                            specialGeneral = true;
                        }
                        else {
                            if (highCa != null && temp.getGeneralLv() <= highCa.getGeneralLv()) {
                                continue;
                            }
                            highCa = temp;
                        }
                    }
                }
            }
            else {
                battleSide = 0;
                for (final CampArmy temp : battle.getDefCamp()) {
                    if (temp.getPlayerId() == playerDto.playerId && !temp.isPhantom()) {
                        if (temp.getSpecialGeneral().generalType == 4) {
                            specialGeneral = true;
                        }
                        else {
                            if (highCa != null && temp.getGeneralLv() <= highCa.getGeneralLv()) {
                                continue;
                            }
                            highCa = temp;
                        }
                    }
                }
            }
            if (highCa == null) {
                if (specialGeneral) {
                    // monitorexit(battle.getBattleId())
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.SEPCIAL_GENERAL_NOBUY_PHANTOM);
                }
                // monitorexit(battle.getBattleId())
                return JsonBuilder.getJson(State.FAIL, LocalMessages.HAS_NO_PGM_IN_THIS_BATTLE);
            }
            else {
                final Player player = this.dataGetter.getPlayerDao().read(playerDto.playerId);
                final Chargeitem ci = (Chargeitem)this.dataGetter.getChargeitemCache().get((Object)53);
                if (playerDto.consumeLv < ci.getLv()) {
                    // monitorexit(battle.getBattleId())
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10002);
                }
                final PlayerBattleAttribute pba = this.dataGetter.getPlayerBattleAttributeDao().read(playerDto.playerId);
                boolean isFree = false;
                int gold = 0;
                if (battle.getBattleType() == 18 && !this.dataGetter.getJuBenService().isInWorldDrama(playerDto.playerId)) {
                    isFree = true;
                }
                else if (pba.getVip3PhantomCount() > 0) {
                    isFree = true;
                    this.dataGetter.getPlayerBattleAttributeDao().decreaseVip3PhantomCount(playerDto.playerId, "\u501f\u5175\u4f7f\u7528\u514d\u8d39\u501f\u5175\u6b21\u6570");
                }
                else {
                    gold = ci.getCost();
                    if (!this.dataGetter.getPlayerDao().consumeGold(player, gold, "\u8d2d\u4e70\u5e7b\u5f71\u6d88\u8017\u91d1\u5e01")) {
                        // monitorexit(battle.getBattleId())
                        return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10003);
                    }
                }
                final CampArmy CaPhantom = this.savePlayerPhantom(playerDto, battle, highCa, battleSide);
                battle.joinCampArmy(this.dataGetter, battleSide, CaPhantom);
                final HuizhanHistory hh = this.dataGetter.getHuiZhanService().getTodayHuizhanInProcess();
                if (hh != null && cityId == hh.getCityId() && hh.getGatherFlag() <= 0) {
                    this.dataGetter.getPlayerHuizhanDao().addPhantomNumByhzIdAndPlayerId(hh.getVId(), playerDto.playerId);
                }
                this.updateHuizhanPlayerForce(cityId, playerDto.playerId, CaPhantom.getArmyHp());
                int addExp = 0;
                if (gold > 0) {
                    addExp = this.dataGetter.getTechEffectCache().getTechEffect(playerDto.playerId, 49);
                    if (addExp > 0) {
                        this.dataGetter.getPlayerService().updateExpAndPlayerLevel(playerDto.playerId, addExp, "\u91d1\u5e01\u501f\u5175\u76f4\u63a5\u83b7\u5f97\u7ecf\u9a8c");
                    }
                }
                if ((battle.getBattleType() == 3 || battle.getBattleType() == 14) && System.currentTimeMillis() - playerDto.copyArmyReportTime > 5000L) {
                    playerDto.copyArmyReportTime = System.currentTimeMillis();
                    final String msg = MessageFormatter.format(LocalMessages.BATTLE_COPY_CAMPARMY_SUCC, new Object[] { ColorUtil.getForceMsg(playerDto.forceId, String.valueOf(WorldCityCommon.nationIdNameMapDot.get(playerDto.forceId)) + playerDto.playerName), ColorUtil.getForceMsg(playerDto.forceId, ((WorldCity)this.dataGetter.getWorldCityCache().get((Object)cityId)).getName()) });
                    this.dataGetter.getChatService().sendBigNotice("GLOBAL", playerDto, msg, null);
                }
                if (battle.getBattleType() == 18) {
                    ScenarioEventMessageHelper.sendMirageScenarioMessage(playerDto.playerId);
                }
                final JsonDocument doc = new JsonDocument();
                doc.startObject();
                doc.createElement("copy", true);
                int maxFreePc = 30;
                final PlayerPhantomObj playerPhantomObj = PhantomManager.getInstance().playerPhantomObjMap.get(playerDto.playerId);
                if (playerPhantomObj != null) {
                    maxFreePc = playerPhantomObj.maxPhantomNum;
                }
                doc.createElement("maxFreePc", maxFreePc);
                doc.createElement("freePhantomCount", (pba.getVip3PhantomCount() > 0) ? (pba.getVip3PhantomCount() - 1) : 0);
                if (battle.getBattleType() == 18) {
                    final JuBenDto juBenDto2 = JuBenManager.getInstance().getByPid(playerDto.playerId);
                    if (juBenDto2 != null) {
                        doc.createElement("juBenLeftJieBing", juBenDto2.maxJieBingCount - juBenDto2.jieBingCount);
                        if (!this.dataGetter.getJuBenService().isInWorldDrama(playerDto.playerId)) {
                            doc.createElement("maxFreePc", juBenDto2.maxJieBingCount);
                            doc.createElement("freePhantomCount", juBenDto2.maxJieBingCount - juBenDto2.jieBingCount);
                        }
                    }
                    else {
                        ErrorSceneLog.getInstance().appendErrorMsg("juBenDto is null").appendBattleId(battle.getBattleId()).appendClassName("BattleService").appendMethodName("doCopyArmy").flush();
                    }
                }
                doc.createElement("isFree", isFree);
                doc.createElement("gold", gold);
                doc.createElement("addExp", addExp);
                doc.createElement("gName", ((General)this.dataGetter.getGeneralCache().get((Object)highCa.getGeneralId())).getName());
                doc.createElement("gQuality", highCa.getQuality());
                doc.endObject();
                this.dataGetter.getIndividualTaskService().sendTaskMessage(playerDto, 1, "hy");
                // monitorexit(battle.getBattleId())
                return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
            }
        }
    }
    
    @Override
    public byte[] youdi(final String battleId, final PlayerDto playerDto) {
        if (this.dataGetter.getAutoBattleService().InAutoBattleMode(playerDto.playerId)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.AUTO_BATTLE_CANNOT_OPERATE);
        }
        return this.exeYoudiChuji(playerDto.playerId, battleId, 1);
    }
    
    @Override
    public byte[] chuji(final String battleId, final PlayerDto playerDto) {
        if (this.dataGetter.getAutoBattleService().InAutoBattleMode(playerDto.playerId)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.AUTO_BATTLE_CANNOT_OPERATE);
        }
        return this.exeYoudiChuji(playerDto.playerId, battleId, 2);
    }
    
    @Override
    public byte[] exeYoudiChuji(final int playerId, final String battleId, final int type) {
        final Player player = this.dataGetter.getPlayerDao().read(playerId);
        final Battle oldBattle = NewBattleManager.getInstance().getBattleByBatId(battleId);
        if (oldBattle == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.BATTLE_ENDED);
        }
        if (oldBattle.getBattleType() != 3 && oldBattle.getBattleType() != 14 && oldBattle.getBattleType() != 18) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.BATTLE_TYPE_INVALID);
        }
        final boolean hzFlag = this.dataGetter.getHuiZhanService().isHuiZhanInStatePreparation(oldBattle.getDefBaseInfo().getId());
        if (hzFlag) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.HUIZHAN_IN_PREPARATION_CAN_NOT_CHUJI_YOUDI);
        }
        final int generalId = oldBattle.chooseCampArmyGeneralId(this.dataGetter, playerId, type);
        int copper = 0;
        if (generalId > 0) {
            final PlayerGeneralMilitary pgm = this.dataGetter.getPlayerGeneralMilitaryDao().getMilitary(playerId, generalId);
            if (pgm == null) {
                if (1 == type) {
                    return JsonBuilder.getJson(State.FAIL, String.valueOf(LocalMessages.BATTLE_YOUDI_FAIL) + LocalMessages.YOUDI_CHUJI_INFO1);
                }
                return JsonBuilder.getJson(State.FAIL, String.valueOf(LocalMessages.BATTLE_CHUJI_FAIL) + LocalMessages.YOUDI_CHUJI_INFO1);
            }
            else {
                final General general = (General)this.dataGetter.getGeneralCache().get((Object)generalId);
                final Troop troop = this.dataGetter.getTroopCache().getTroop(general.getTroop(), playerId);
                final double troopFoodConsumeCoeA = ((TroopConscribe)this.dataGetter.getTroopConscribeCache().get((Object)troop.getId())).getFood();
                final double troopFoodA = troopFoodConsumeCoeA * pgm.getForces();
                float due = 0.0f;
                float dueLv = 0.0f;
                if (1 == type) {
                    due = ((C)this.dataGetter.getcCache().get((Object)"World.Due.CopperE")).getValue();
                    dueLv = ((C)this.dataGetter.getcCache().get((Object)"World.Due.LvCopperE")).getValue();
                }
                else {
                    due = ((C)this.dataGetter.getcCache().get((Object)"World.InitDue.CopperE")).getValue();
                    dueLv = ((C)this.dataGetter.getcCache().get((Object)"World.InitDue.LvCopperE")).getValue();
                }
                copper = ((int)((troopFoodA * due + dueLv * player.getPlayerLv()) / 100.0) + 1) * 100;
                final PlayerResource pr = this.dataGetter.getPlayerResourceDao().read(playerId);
                if (pr.getCopper() < copper) {
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10001);
                }
            }
        }
        final CampArmy[] cas = new CampArmy[2];
        int res = 0;
        final boolean isNTYellowTurbans = this.dataGetter.getBattleService().isNTYellowTurbansXiangYangDoing(oldBattle.getDefBaseInfo().getId());
        if (isNTYellowTurbans) {
            res = oldBattle.chooseCampArmyForNTYellowTurbans(this.dataGetter, playerId, cas);
        }
        else {
            res = oldBattle.chooseCampArmy(this.dataGetter, playerId, cas, type);
        }
        if (res == 2) {
            if (1 == type) {
                return JsonBuilder.getJson(State.FAIL, String.valueOf(LocalMessages.BATTLE_YOUDI_FAIL) + LocalMessages.YOUDI_CHUJI_INFO1);
            }
            return JsonBuilder.getJson(State.FAIL, String.valueOf(LocalMessages.BATTLE_CHUJI_FAIL) + LocalMessages.YOUDI_CHUJI_INFO1);
        }
        else if (res == 4) {
            if (1 == type) {
                return JsonBuilder.getJson(State.FAIL, String.valueOf(LocalMessages.BATTLE_YOUDI_FAIL) + LocalMessages.YOUDI_CHUJI_INFO2);
            }
            return JsonBuilder.getJson(State.FAIL, String.valueOf(LocalMessages.BATTLE_CHUJI_FAIL) + LocalMessages.YOUDI_CHUJI_INFO2);
        }
        else {
            if (res == 3) {
                if (copper > 0) {
                    this.dataGetter.getPlayerResourceDao().consumeCopperUnconditional(playerId, copper, "\u8bf1\u654c\u51fa\u51fb\u6d88\u8017\u94f6\u5e01");
                }
                final JsonDocument doc = new JsonDocument();
                doc.startObject();
                doc.createElement("msg", (Object)LocalMessages.CHUJI_YOUDI_FAIL_PRO);
                doc.createElement("challenge", false);
                doc.createElement("cd", 0);
                doc.createElement("copper", copper);
                doc.endObject();
                return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
            }
            if (copper > 0) {
                this.dataGetter.getPlayerResourceDao().consumeCopperUnconditional(playerId, copper, "\u8bf1\u654c\u51fa\u51fb\u6d88\u8017\u94f6\u5e01");
            }
            return this.createOneToOneBattle(playerId, cas, oldBattle, type, copper);
        }
    }
    
    @Override
    public byte[] createOneToOneBattle(final int playerId, final CampArmy[] cas, final Battle oldBattle, final int type, final int consumeCopper) {
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        if (consumeCopper > 0) {
            doc.createElement("copper", consumeCopper);
            doc.createElement("cd", 0);
        }
        this.sendPKMsg(oldBattle, cas, playerId, type);
        CampArmy createCampArmy = cas[0];
        if (cas[1].getPlayerId() == playerId) {
            createCampArmy = cas[1];
        }
        int OneToOneBattleType = 0;
        int pgmState = 0;
        int param1 = createCampArmy.getId();
        final int param2 = oldBattle.getDefBaseInfo().getId();
        switch (oldBattle.getBattleType()) {
            case 3: {
                OneToOneBattleType = 13;
                pgmState = 13;
                break;
            }
            case 14: {
                OneToOneBattleType = 15;
                pgmState = 15;
                break;
            }
            case 18: {
                OneToOneBattleType = 19;
                pgmState = 20;
                param1 += oldBattle.getAttBaseInfo().getId() * 1000;
                break;
            }
        }
        final String newBattleId = NewBattleManager.getBattleId(OneToOneBattleType, param1, param2);
        Battle oneToOnebattle = NewBattleManager.getInstance().getBattleByBatId(newBattleId);
        if (oneToOnebattle != null) {
            ErrorSceneLog.getInstance().appendErrorMsg("oneToOnebattle already exists").append("OneToOneBattleType", OneToOneBattleType).append("param1", param1).append("param2", param2).append("create PlayerName", createCampArmy.getPlayerName()).append("create PlayerId", createCampArmy.getPlayerId()).append("createCampArmy", createCampArmy.getGeneralName()).append("createCampArmy", createCampArmy.getGeneralId()).append("oldBattle", oldBattle.getBattleId()).append("oldCampCurNum", oldBattle.getCampNum().get()).append("oneToOnebattleStartTime", new Date(oneToOnebattle.getStartTime())).append("oneToOnebattleAttId", oneToOnebattle.getAttBaseInfo().getId()).append("oneToOnebattleNextMaxExeTime", new Date(oneToOnebattle.getNextMaxExeTime())).appendBattleId(newBattleId).appendPlayerId(playerId).append("type", type).flush();
            final StringBuilder detail = new StringBuilder();
            detail.append("\nattSide:").append("\n");
            for (final CampArmy ca : oneToOnebattle.getAttCamp()) {
                detail.append(ca.getId()).append("|").append(ca.getPlayerName()).append("|").append(ca.getPlayerId()).append("|").append(ca.getGeneralName()).append("|").append(ca.getGeneralId()).append("\n");
            }
            detail.append("defSide:").append("\n");
            for (final CampArmy ca : oneToOnebattle.getDefCamp()) {
                detail.append(ca.getId()).append("|").append(ca.getPlayerName()).append("|").append(ca.getPlayerId()).append("|").append(ca.getGeneralName()).append("|").append(ca.getGeneralId()).append("\n");
            }
            ErrorSceneLog.getInstance().error(detail.toString());
            BattleSceneLog.getInstance().error("BATTLE_ERROR batNotNull playerId:" + createCampArmy.getPlayerId() + "#generalId" + createCampArmy.getGeneralId() + "#pgmVid:" + createCampArmy.getPgmVId() + "#oldBattleId:" + oldBattle.getBattleId() + "#newBatId" + newBattleId);
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10012);
        }
        oneToOnebattle = NewBattleManager.getInstance().createBattle(newBattleId);
        if (oneToOnebattle == null) {
            ErrorSceneLog.getInstance().appendErrorMsg("oneToOnebattle cannot create.").appendBattleId(newBattleId).appendPlayerId(playerId).append("type", type).flush();
            BattleSceneLog.getInstance().error("BATTLE_ERROR CreateFail playerId:" + createCampArmy.getPlayerId() + "#generalId" + createCampArmy.getGeneralId() + "#pgmVid:" + createCampArmy.getPgmVId() + "#oldBattleId:" + oldBattle.getBattleId() + "#newBatId" + newBattleId);
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10012);
        }
        oneToOnebattle.beginOneToOneBattle(this.dataGetter, OneToOneBattleType, playerId, cas, oldBattle);
        if (cas[0].getPlayerId() > 0 && !cas[0].isPhantom()) {
            this.dataGetter.getPlayerGeneralMilitaryDao().updateState(cas[0].getPgmVId(), pgmState);
            this.dataGetter.getGeneralService().sendGeneralMilitaryRecruitInfo(cas[0].getPlayerId(), cas[0].getGeneralId());
        }
        else if (cas[0].isBarPhantom()) {
            this.dataGetter.getBarbarainPhantomDao().updateState(cas[0].getPgmVId(), pgmState);
        }
        else if (cas[0].isYellowTrubans) {
            this.dataGetter.getYellowTurbansDao().updateState(cas[0].getPgmVId(), pgmState);
        }
        if (cas[1].getPlayerId() > 0 && !cas[1].isPhantom()) {
            this.dataGetter.getPlayerGeneralMilitaryDao().updateState(cas[1].getPgmVId(), pgmState);
            this.dataGetter.getGeneralService().sendGeneralMilitaryRecruitInfo(cas[1].getPlayerId(), cas[1].getGeneralId());
        }
        else if (cas[1].isBarPhantom()) {
            this.dataGetter.getBarbarainPhantomDao().updateState(cas[1].getPgmVId(), pgmState);
        }
        else if (cas[1].isYellowTrubans) {
            this.dataGetter.getYellowTurbansDao().updateState(cas[1].getPgmVId(), pgmState);
        }
        if (cas[0].getPlayerId() > 0) {
            final PlayerInfo playerInfo = oldBattle.getInBattlePlayers().get(cas[0].getPlayerId());
            if (playerInfo != null) {
                playerInfo.battleMode = 2;
            }
            else {
                ErrorSceneLog.getInstance().appendErrorMsg("playerInfo is null").appendBattleId(oldBattle.getBattleId()).appendPlayerId(cas[0].getPlayerId()).appendPlayerName(cas[0].getPlayerName()).appendClassName("BattleService").appendMethodName("createOneToOneBattle").flush();
            }
        }
        if (cas[1].getPlayerId() > 0) {
            final PlayerInfo playerInfo = oldBattle.getInBattlePlayers().get(cas[1].getPlayerId());
            if (playerInfo != null) {
                playerInfo.battleMode = 2;
            }
            else {
                ErrorSceneLog.getInstance().appendErrorMsg("playerInfo is null").appendBattleId(oldBattle.getBattleId()).appendPlayerId(cas[1].getPlayerId()).appendPlayerName(cas[1].getPlayerName()).appendClassName("BattleService").appendMethodName("createOneToOneBattle").flush();
            }
        }
        this.dataGetter.getHuiZhanService().dealHuiZhanPk(cas[0], cas[1], oldBattle);
        doc.createElement("challenge", true);
        doc.createElement("generalId", (cas[0].getPlayerId() == playerId) ? cas[0].getGeneralId() : cas[1].getGeneralId());
        doc.createElement("battleId", newBattleId);
        doc.endObject();
        try {
            if (cas[0] != null && cas[0].getPlayerId() > 0) {
                this.dataGetter.getIndividualTaskService().sendTaskMessage(new PlayerDto(cas[0].getPlayerId(), cas[0].getForceId()), 1, "dantiao");
            }
            if (cas[1] != null && cas[1].getPlayerId() > 0) {
                this.dataGetter.getIndividualTaskService().sendTaskMessage(new PlayerDto(cas[1].getPlayerId(), cas[1].getForceId()), 1, "dantiao");
            }
        }
        catch (Exception e) {
            BattleService.errorLog.error(this, e);
        }
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    private void sendPKMsg(final Battle oldBattle, final CampArmy[] cas, final int playerId, final int type) {
    }
    
    @Override
    public byte[] setChangeBat(final PlayerDto playerDto) {
        this.dataGetter.getPlayerBattleAttributeDao().updateChangeBat(playerDto.playerId);
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("changeBat", true);
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public byte[] getCoverChujuCd(final PlayerDto playerDto) {
        final PlayerBattleAttribute pba = this.dataGetter.getPlayerBattleAttributeDao().read(playerDto.playerId);
        if (pba.getChujiTime() <= System.currentTimeMillis()) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.NOT_IN_CD_INFO);
        }
        final Chargeitem ci = (Chargeitem)this.dataGetter.getChargeitemCache().get((Object)54);
        final int cost = (int)Math.ceil((pba.getChujiTime() - System.currentTimeMillis()) / (ci.getParam() * 60000.0));
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("gold", cost);
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public byte[] getCoverYoudiCd(final PlayerDto playerDto) {
        final PlayerBattleAttribute pba = this.dataGetter.getPlayerBattleAttributeDao().read(playerDto.playerId);
        if (pba.getYoudiTime() <= System.currentTimeMillis()) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.NOT_IN_CD_INFO);
        }
        final Chargeitem ci = (Chargeitem)this.dataGetter.getChargeitemCache().get((Object)52);
        final int cost = (int)Math.ceil((pba.getYoudiTime() - System.currentTimeMillis()) / (ci.getParam() * 60000.0));
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("gold", cost);
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public byte[] doCoverChujuCd(final PlayerDto playerDto) {
        final Chargeitem ci = (Chargeitem)this.dataGetter.getChargeitemCache().get((Object)54);
        final int playerId = playerDto.playerId;
        final Player player = this.dataGetter.getPlayerDao().read(playerId);
        if (ci.getLv() > player.getConsumeLv()) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10002);
        }
        final PlayerBattleAttribute pba = this.dataGetter.getPlayerBattleAttributeDao().read(playerId);
        if (pba.getChujiTime() <= System.currentTimeMillis()) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.NOT_IN_CD_INFO);
        }
        final int cost = (int)Math.ceil((pba.getChujiTime() - System.currentTimeMillis()) / (ci.getParam() * 60000.0));
        if (!this.dataGetter.getPlayerDao().consumeGold(player, cost, ci.getName())) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10003);
        }
        this.dataGetter.getPlayerBattleAttributeDao().updateChujiTime(playerId, System.currentTimeMillis());
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("cover", true);
        doc.createElement("gold", ci.getCost());
        doc.createElement("cd", 0);
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public byte[] doCoverYoudiCd(final PlayerDto playerDto) {
        final Chargeitem ci = (Chargeitem)this.dataGetter.getChargeitemCache().get((Object)52);
        final int playerId = playerDto.playerId;
        final Player player = this.dataGetter.getPlayerDao().read(playerId);
        if (ci.getLv() > player.getConsumeLv()) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10002);
        }
        final PlayerBattleAttribute pba = this.dataGetter.getPlayerBattleAttributeDao().read(playerId);
        if (pba.getYoudiTime() <= System.currentTimeMillis()) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.NOT_IN_CD_INFO);
        }
        final int cost = (int)Math.ceil((pba.getYoudiTime() - System.currentTimeMillis()) / (ci.getParam() * 60000.0));
        if (!this.dataGetter.getPlayerDao().consumeGold(player, cost, ci.getName())) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10003);
        }
        this.dataGetter.getPlayerBattleAttributeDao().updateYoudiTime(playerId, System.currentTimeMillis());
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("cover", true);
        doc.createElement("gold", ci.getCost());
        doc.createElement("cd", 0);
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public Tuple<Boolean, String> useOfficerToken(final int playerId, final int cityId, final int officerId, final String battleId) {
        boolean suc = false;
        String reason = "";
        final Tuple<Boolean, String> result = new Tuple();
        final Player player = this.dataGetter.getPlayerDao().read(playerId);
        final boolean hzFlag = this.dataGetter.getHuiZhanService().isHuiZhanInStatePreparation(cityId);
        if (hzFlag) {
            result.left = false;
            result.right = LocalMessages.OFFICER_TOKEN_CAN_NOT_USE_IN_HUIZHAN;
            return result;
        }
        final OfficerToken token = this.officerTokenDao.getTokenByForceIdAndOfficerId(officerId, player.getForceId());
        final int tokenNum = (token == null || token.getNum() == null) ? 0 : token.getNum();
        if (token == null || tokenNum <= 0) {
            reason = LocalMessages.NO_TOKEN;
            result.left = suc;
            result.right = reason;
            return result;
        }
        if (BattleService.officerUseMap.containsKey(this.orgnizeKey(player.getForceId(), officerId))) {
            reason = LocalMessages.CD;
            result.left = suc;
            result.right = reason;
            return result;
        }
        final long time = System.currentTimeMillis();
        List<OfficerTokenUseInfo> otherTokenList = this.getCurrentInUseToken(player.getForceId());
        for (final OfficerTokenUseInfo otherToken : otherTokenList) {
            if (otherToken == null) {
                continue;
            }
            if (otherToken.getBattleId().equalsIgnoreCase(battleId)) {
                reason = LocalMessages.ONE_BATTLE_ONE_TOKEN;
                result.left = suc;
                result.right = reason;
                return result;
            }
        }
        otherTokenList = null;
        final long updateTime = time + 300000L;
        final OfficerTokenUseInfo tokenUseInfo = new OfficerTokenUseInfo(player.getForceId(), officerId, battleId, updateTime, cityId, playerId);
        final String key = this.orgnizeKey(player.getForceId(), officerId);
        BattleService.officerUseMap.put(key, tokenUseInfo);
        final String params = this.orgnizeKey(player.getForceId(), officerId);
        this.jobService.addJob("battleService", "deleteTheTokenReward", params, System.currentTimeMillis() + 300000L);
        suc = true;
        final Halls halls = (Halls)this.dataGetter.getHallsCache().get((Object)officerId);
        String nameList = "";
        if (halls != null) {
            String tokenName = "";
            if (halls.getOfficialId() == 1) {
                tokenName = LocalMessages.KING;
                nameList = String.valueOf(WebUtil.getForceName(player.getForceId())) + LocalMessages.RANK_CONSTANTS_KING;
            }
            else {
                final Official official = (Official)this.officialCache.get((Object)halls.getOfficialId());
                tokenName = ((official == null) ? "" : official.getNameShort());
                nameList = halls.getNameList();
            }
            final String broadContent = MessageFormatter.format(LocalMessages.BROAD_CONTENT, new Object[] { String.valueOf(nameList) + player.getPlayerName(), tokenName });
            this.dataGetter.getChatService().sendSystemChat("GLOBAL", 0, player.getForceId(), broadContent, null);
        }
        AddJob.getInstance().addJob(JobClassMethondEnum.BATTLESERVICE_PUSHTOKENMESSAGE, player.getForceId().toString(), 0L);
        result.left = suc;
        result.right = reason;
        return result;
    }
    
    @Override
    public void pushMessageToCitizen(final String forceId) {
        try {
            final int force = Integer.parseInt(forceId);
            final List<OfficerTokenUseInfo> ingTokens = this.getCurrentInUseToken(force);
            final Collection<PlayerDto> onlinePlayerList = Players.getAllPlayer();
            JsonDocument doc = null;
            for (final PlayerDto dto : onlinePlayerList) {
                if (dto == null) {
                    continue;
                }
                if (dto.forceId != force) {
                    continue;
                }
                doc = new JsonDocument();
                doc.startObject();
                this.getCurrentOfficerTokenPushInfo(ingTokens, doc, dto.playerId);
                doc.endObject();
                Players.push(dto.playerId, PushCommand.PUSH_OFFICER_TOKEN, doc.toByte());
            }
        }
        catch (Exception e) {
            BattleService.errorLog.error(this, e);
        }
    }
    
    @Override
    public void getCurrentOfficerTokenPushInfo(final List<OfficerTokenUseInfo> ingTokens, final JsonDocument doc, final int playerId) {
        if (ingTokens == null || ingTokens.isEmpty() || ingTokens.size() == 0) {
            doc.startArray("officialNew");
            doc.endArray();
            return;
        }
        Collections.sort(ingTokens);
        final long time = System.currentTimeMillis();
        int count = 0;
        OfficerTokenUseInfo firstToken = null;
        int tokenGotNum = 0;
        List<Integer> gmList = null;
        List<OfficerTokenUseInfo> userFuList = new ArrayList<OfficerTokenUseInfo>();
        final List<PlayerGeneralMilitary> pgmList = this.dataGetter.getPlayerGeneralMilitaryDao().getMilitaryList(playerId);
        final int maxWhileTimes = this.dataGetter.getHallsCache().getTokenList().size() * 3;
        int whileCount = 0;
        while (count < ingTokens.size()) {
            if (++whileCount > maxWhileTimes) {
                BattleService.errorLog.error("While loop times:" + whileCount + "playerId:" + playerId);
                break;
            }
            firstToken = ingTokens.get(count);
            tokenGotNum = 0;
            if (firstToken == null || firstToken.getExpireTime() <= time) {
                ++count;
            }
            else {
                gmList = BattleService.tokenRewardMap.get(this.orgnizeKey(firstToken.getForceId(), firstToken.getOfficerId()));
                if (gmList == null) {
                    ++count;
                    userFuList.add(firstToken);
                }
                else {
                    if (pgmList == null) {
                        count = ingTokens.size();
                        break;
                    }
                    for (final PlayerGeneralMilitary pgm : pgmList) {
                        final int vId = (pgm == null) ? 0 : pgm.getVId();
                        if (gmList.contains(vId)) {
                            ++tokenGotNum;
                        }
                    }
                    if (tokenGotNum > 0) {
                        firstToken = ((++count >= ingTokens.size()) ? null : ingTokens.get(count));
                    }
                    else {
                        userFuList.add(firstToken);
                        ++count;
                    }
                }
            }
        }
        firstToken = ((userFuList == null || userFuList.size() <= 0) ? null : userFuList.get(0));
        if (firstToken == null) {
            doc.startArray("officialNew");
            doc.endArray();
            return;
        }
        final Player player = this.dataGetter.getPlayerDao().read(firstToken.getPlayerId());
        if (player == null) {
            doc.startArray("officialNew");
            doc.endArray();
            return;
        }
        final Halls halls = (Halls)this.dataGetter.getHallsCache().get((Object)firstToken.getOfficerId());
        if (halls == null) {
            doc.startArray("officialNew");
            doc.endArray();
            return;
        }
        doc.startArray("officialNew");
        doc.startObject();
        doc.createElement("forceName", WorldCityCommon.nationIdNameMap.get(firstToken.getForceId()));
        doc.createElement("officerName", halls.getNameList());
        doc.createElement("officerNum", userFuList.size());
        doc.createElement("tokenType", halls.getOfficialId() - 1);
        doc.endObject();
        doc.endArray();
        userFuList = null;
    }
    
    @Override
    public byte[] getCurrentTokenInfo(final PlayerDto playerDto, final int index) {
        final int forceId = playerDto.forceId;
        final long time = System.currentTimeMillis();
        final List<OfficerTokenUseInfo> list = this.getCurrentInUseToken(forceId);
        if (list == null || list.isEmpty() || list.size() == 0 || list.size() < index) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.NO_TOKEN_IN_USR);
        }
        Collections.sort(list);
        int count = 0;
        OfficerTokenUseInfo firstToken = null;
        int tokenGotNum = 0;
        final Map<Integer, Boolean> hasGotMap = new HashMap<Integer, Boolean>();
        List<Integer> gmList = null;
        final List<OfficerTokenUseInfo> userFuList = new ArrayList<OfficerTokenUseInfo>();
        int playerId = playerDto.playerId;
        final List<PlayerGeneralMilitary> pgmList = this.dataGetter.getPlayerGeneralMilitaryDao().getMilitaryList(playerId);
        final int maxWhileTimes = this.dataGetter.getHallsCache().getTokenList().size() * 3;
        int whileCount = 0;
        while (count < list.size()) {
            if (++whileCount > maxWhileTimes) {
                BattleService.errorLog.error("While loop times:" + whileCount + "playerId:" + playerId);
                break;
            }
            firstToken = list.get(count);
            tokenGotNum = 0;
            if (firstToken == null || firstToken.getExpireTime() <= time) {
                ++count;
            }
            else {
                gmList = BattleService.tokenRewardMap.get(this.orgnizeKey(firstToken.getForceId(), firstToken.getOfficerId()));
                if (gmList == null) {
                    ++count;
                    userFuList.add(firstToken);
                }
                else {
                    if (pgmList == null) {
                        count = list.size();
                        break;
                    }
                    for (final PlayerGeneralMilitary pgm : pgmList) {
                        final int vId = (pgm == null) ? 0 : pgm.getVId();
                        if (gmList.contains(vId)) {
                            ++tokenGotNum;
                        }
                    }
                    if (tokenGotNum > 0) {
                        firstToken = ((++count >= list.size()) ? null : list.get(count));
                    }
                    else {
                        userFuList.add(firstToken);
                        ++count;
                    }
                }
            }
        }
        if (userFuList == null || userFuList.isEmpty() || userFuList.size() < index) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.NO_TOKEN_IN_USR);
        }
        firstToken = userFuList.get(index - 1);
        playerId = firstToken.getPlayerId();
        final WorldCity city = (WorldCity)this.dataGetter.getWorldCityCache().get((Object)firstToken.getCityId());
        final Player player = this.dataGetter.getPlayerDao().read(playerId);
        final Halls halls = (Halls)this.dataGetter.getHallsCache().get((Object)firstToken.getOfficerId());
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("battleId", firstToken.getBattleId());
        doc.createElement("playerName", player.getPlayerName());
        doc.createElement("cityName", city.getName());
        doc.createElement("cityId", city.getId());
        doc.createElement("officerName", halls.getNameList());
        doc.createElement("forceId", player.getForceId());
        final long latestTime = firstToken.getExpireTime() - time;
        doc.createElement("latestTime", latestTime);
        this.dataGetter.getGeneralService().getGeneralPortait(playerDto.playerId, doc, hasGotMap);
        this.rewardDoc(halls.getOrderReward(), doc);
        int feat = 0;
        final PlayerTech pt = this.dataGetter.getPlayerTechDao().getPlayerTech(playerDto.playerId, 502);
        if (pt != null && pt.getStatus() == 5 && this.playerAttributeDao.getFunctionId(playerId).toCharArray()[32] == '1') {
            feat = 15;
        }
        doc.createElement("feat", feat);
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public void addOfficerToken() {
        final long start = System.currentTimeMillis();
        this.officerTokenDao.addTokenTimer();
        BattleService.timerLog.info(LogUtil.formatThreadLog("BattleService", "addOfficerToken", 2, System.currentTimeMillis() - start, ""));
    }
    
    @Override
    public byte[] getTuJinGenerals(final PlayerDto playerDto, final String battleId) {
        if (this.dataGetter.getAutoBattleService().InAutoBattleMode(playerDto.playerId)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.AUTO_BATTLE_CANNOT_OPERATE);
        }
        final Battle battle = NewBattleManager.getInstance().getBattleByBatId(battleId);
        if (battle == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.BATTLE_ENDED_CANNOT_ASSEMBLE);
        }
        final HuizhanHistory hh = this.dataGetter.getHuiZhanService().getTodayHuizhanInProcess();
        if (hh != null && hh.getCityId() == battle.getDefBaseInfo().getId()) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.HUIZHAN_IN_PREPARATION_CAN_NOT_TUJIN);
        }
        if (battle.getBattleType() != 3 && battle.getBattleType() != 14 && battle.getBattleType() != 18) {
            ErrorSceneLog.getInstance().appendErrorMsg("plug attack. battle is not city battle").appendClassName("BattleService").appendMethodName("getTuJinGenerals").append("battleId", battleId).appendPlayerId(playerDto.playerId).appendPlayerName(playerDto.playerName).flush();
            return JsonBuilder.getJson(State.FAIL, LocalMessages.BATTLE_TYPE_ERROR_CANNOT_TUJIN);
        }
        final int playerId = playerDto.playerId;
        final PlayerInfo pi = battle.getInBattlePlayers().get(playerId);
        if (pi == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.NOT_IN_BATTLE_CANNOT_TUJIN);
        }
        int myCampNum = 0;
        int counterCampNum = 0;
        final Set<CampArmy> onQueueSet = new HashSet<CampArmy>();
        final List<CampArmy> caAvilableList = new LinkedList<CampArmy>();
        if (pi.isAttSide()) {
            myCampNum = battle.getAttCamp().size();
            counterCampNum = battle.getDefCamp().size();
            for (final CampArmy temp : battle.getAttCamp()) {
                if (temp.getPlayerId() == playerId && !temp.isPhantom()) {
                    caAvilableList.add(temp);
                }
            }
            for (final BattleArmy ba : battle.getAttList()) {
                onQueueSet.add(ba.getCampArmy());
            }
        }
        else {
            myCampNum = battle.getDefCamp().size();
            counterCampNum = battle.getAttCamp().size();
            for (final CampArmy temp : battle.getDefCamp()) {
                if (temp.getPlayerId() == playerId && !temp.isPhantom()) {
                    caAvilableList.add(temp);
                }
            }
            for (final BattleArmy ba : battle.getDefList()) {
                onQueueSet.add(ba.getCampArmy());
            }
        }
        int times = 3;
        final int timesReduce = this.dataGetter.getTechEffectCache().getTechEffect(playerDto.playerId, 39);
        if (timesReduce > 0) {
            times -= timesReduce;
        }
        if (myCampNum <= times * counterCampNum) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.LESS_THAN_3_TIMES_CANNOT_TUJIN);
        }
        if (caAvilableList.size() == 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.NOT_IN_BATTLE_CANNOT_TUJIN);
        }
        for (final CampArmy ca : onQueueSet) {
            caAvilableList.remove(ca);
        }
        if (caAvilableList.size() == 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.GENERAL_ON_QUEUE_CANNOT_TUJIN);
        }
        final Set<CampArmy> hpLowSet = new HashSet<CampArmy>();
        for (final CampArmy ca2 : caAvilableList) {
            final int maxHp = ca2.getMaxForces();
            if (ca2.getArmyHp() * 1.0 / maxHp < 0.05) {
                hpLowSet.add(ca2);
            }
        }
        for (final CampArmy ca2 : hpLowSet) {
            caAvilableList.remove(ca2);
        }
        if (caAvilableList.size() == 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.GENERAL_HP_LOW_CANNOT_TUJIN);
        }
        final List<Tuple<Integer, String>> avilableCities = new LinkedList<Tuple<Integer, String>>();
        this.caculateTuJinAvilableCities(playerDto, battle, avilableCities);
        boolean hasTujinCity = false;
        for (final Tuple<Integer, String> tuple : avilableCities) {
            if (tuple.right.split("_")[0].equals("1")) {
                hasTujinCity = true;
                break;
            }
        }
        if (!hasTujinCity) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.NO_AVILABLE_CITY_CANNOT_TUJIN);
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.startArray("gIds");
        for (final CampArmy ca3 : caAvilableList) {
            doc.startObject();
            doc.createElement("gId", ca3.getGeneralId());
            doc.createElement("gName", ca3.getGeneralName());
            doc.createElement("gPic", ca3.getGeneralPic());
            doc.createElement("gQuality", ca3.getQuality());
            doc.endObject();
        }
        doc.endArray();
        doc.startArray("cityOptions");
        for (final Tuple<Integer, String> tuple2 : avilableCities) {
            final int cityId = tuple2.left;
            final String[] right = tuple2.right.split("_");
            final int state = Integer.parseInt(right[0]);
            final int type = Integer.parseInt(right[1]);
            doc.startObject();
            doc.createElement("cityId", cityId);
            if (type == 1) {
                final JuBenCityDto neiJuBenCityDto = JuBenManager.getInstance().getJuBenCityDto(playerId, cityId);
                final SoloCity soloCity = (SoloCity)this.dataGetter.getSoloCityCache().get((Object)cityId);
                doc.createElement("cityName", soloCity.getName());
                doc.createElement("forceId", neiJuBenCityDto.forceId);
            }
            else {
                final City city = this.dataGetter.getCityDao().read(cityId);
                final WorldCity wc = (WorldCity)this.dataGetter.getWorldCityCache().get((Object)cityId);
                doc.createElement("cityName", wc.getName());
                doc.createElement("forceId", city.getForceId());
            }
            doc.createElement("state", state);
            doc.endObject();
        }
        doc.endArray();
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    void caculateTuJinAvilableCities(final PlayerDto playerDto, final Battle battle, final List<Tuple<Integer, String>> avilableCities) {
        if (battle.getBattleType() == 3 || battle.getBattleType() == 14) {
            final Set<Integer> mistOpendedSet = new HashSet<Integer>();
            final PlayerWorld pw = this.dataGetter.getPlayerWorldDao().read(playerDto.playerId);
            if (pw != null) {
                final String attStr = pw.getAttedId();
                if (!StringUtils.isBlank(attStr)) {
                    final String[] ids = attStr.split(",");
                    String[] array;
                    for (int length = (array = ids).length, i = 0; i < length; ++i) {
                        final String str = array[i];
                        mistOpendedSet.add(Integer.valueOf(str));
                    }
                }
            }
            final Set<Integer> neibors = this.dataGetter.getWorldRoadCache().getNeighbors(battle.getDefBaseInfo().getId());
            for (final Integer nei : neibors) {
                int state = 1;
                if (WorldCityCommon.mainCityNationIdMap.get(nei) != null) {
                    state = 0;
                }
                if (!mistOpendedSet.contains(nei)) {
                    state = 0;
                }
                if (!RankService.canEnterHJCenterCity(nei)) {
                    state = 0;
                }
                final Battle tempBattle = NewBattleManager.getInstance().getBattleByDefId(3, nei);
                if (tempBattle == null) {
                    final City neiCity = this.dataGetter.getCityDao().read(nei);
                    if (playerDto.forceId == neiCity.getForceId()) {
                        state = 0;
                    }
                }
                if (WorldCityCommon.barbarainCitySet.contains(nei)) {
                    final int degree = this.dataGetter.getRankService().hasBarTasks(playerDto.forceId);
                    if (degree == 0 || !WorldCityCommon.forcIdManzuCityIdMap.get(playerDto.forceId).equals(nei)) {
                        state = 0;
                    }
                }
                final WorldCity wc = (WorldCity)this.dataGetter.getWorldCityCache().get((Object)nei);
                final Tuple<Integer, String> tuple = new Tuple();
                tuple.left = wc.getId();
                tuple.right = String.valueOf(state) + "_" + 0;
                avilableCities.add(tuple);
            }
        }
        else if (battle.getBattleType() == 18) {
            final Set<Integer> neibors2 = this.dataGetter.getSoloRoadCache().getNeighbors(battle.getDefBaseInfo().getId());
            final JuBenDto juBenDto = JuBenManager.getInstance().getByPid(playerDto.playerId);
            SoloRoad road = null;
            for (final Integer nei : neibors2) {
                int state = 1;
                try {
                    if (juBenDto != null) {
                        road = this.dataGetter.getSoloRoadCache().getRoad(juBenDto.juBen_id, nei, battle.getDefBaseInfo().getId());
                        if (road != null && juBenDto.roadLinked != null && juBenDto.roadLinked.contains(road.getId())) {
                            state = 0;
                        }
                    }
                }
                catch (Exception e) {
                    BattleService.errorLog.error(this, e);
                }
                final Builder scenarioBuilder = BuilderFactory.getInstance().getBuilder(18);
                final Player player = this.dataGetter.getPlayerDao().read(playerDto.playerId);
                final String battleId = scenarioBuilder.getBattleId(this.dataGetter, player, nei);
                final Battle tempBattle2 = NewBattleManager.getInstance().getBattleByBatId(battleId);
                final JuBenCityDto juBenCityDto = JuBenManager.getInstance().getJuBenCityDto(playerDto.playerId, nei);
                if (tempBattle2 == null && playerDto.forceId == juBenCityDto.forceId) {
                    state = 0;
                }
                if (juBenCityDto.forceId != juBenDto.player_force_id) {
                    final OperationResult result = this.dataGetter.getCilvilTrickService().hasTrick(juBenCityDto);
                    if (result == null || !result.getResult()) {
                        state = 0;
                    }
                }
                final SoloCity soloCity = (SoloCity)this.dataGetter.getSoloCityCache().get((Object)nei);
                final Tuple<Integer, String> tuple2 = new Tuple();
                tuple2.left = soloCity.getId();
                tuple2.right = String.valueOf(state) + "_" + 1;
                avilableCities.add(tuple2);
            }
        }
    }
    
    @Override
    public byte[] TuJin(final PlayerDto playerDto, final String battleId, final String gIds, final int cityId) {
        if (this.dataGetter.getAutoBattleService().InAutoBattleMode(playerDto.playerId)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.AUTO_BATTLE_CANNOT_OPERATE);
        }
        final int playerId = playerDto.playerId;
        if (gIds == null || gIds.isEmpty()) {
            return JsonBuilder.getJson(State.FAIL, "gIds=" + gIds);
        }
        final Set<Integer> gIdSet = new HashSet<Integer>();
        try {
            String[] split;
            for (int length = (split = gIds.split("#")).length, i = 0; i < length; ++i) {
                final String s = split[i];
                if (!s.equals("")) {
                    gIdSet.add(Integer.parseInt(s));
                }
            }
        }
        catch (NumberFormatException e2) {
            ErrorSceneLog.getInstance().appendErrorMsg("TuJin plug attack").appendPlayerId(playerId).appendPlayerName(playerDto.playerName).append("battleId", battleId).append("gIds", gIds).append("cityId", cityId).flush();
            return JsonBuilder.getJson(State.FAIL, "\u5916\u6302\u53ef\u803b\uff01gIds=" + gIds);
        }
        final Battle battle = NewBattleManager.getInstance().getBattleByBatId(battleId);
        if (battle == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.BATTLE_ENDEND_CANNOT_WITHDRAW);
        }
        if (battle.getBattleType() != 3 && battle.getBattleType() != 14 && battle.getBattleType() != 18) {
            ErrorSceneLog.getInstance().appendErrorMsg("plug attack. battle is not city battle").appendClassName("BattleService").appendMethodName("getTuJinGenerals").append("battleId", battleId).appendPlayerId(playerDto.playerId).appendPlayerName(playerDto.playerName).flush();
            return JsonBuilder.getJson(State.FAIL, LocalMessages.BATTLE_TYPE_ERROR_CANNOT_TUJIN);
        }
        try {
            final JuBenDto juBenDto = JuBenManager.getInstance().getByPid(playerId);
            if (juBenDto != null) {
                final int curCityId = battle.getDefBaseInfo().getId();
                final SoloRoad road = this.dataGetter.getSoloRoadCache().getRoad(juBenDto.juBen_id, cityId, curCityId);
                if (road != null && juBenDto.roadLinked != null && juBenDto.roadLinked.contains(road.getId())) {
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.MOVE_AUTO_NO_ROAD);
                }
                if (road == null) {
                    ErrorSceneLog.getInstance().appendErrorMsg("soloroad is null...").appendClassName("BattleService").appendMethodName("TuJin").append("battleId", battleId).appendPlayerId(playerDto.playerId).appendPlayerName(playerDto.playerName).flush();
                }
            }
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error(this, e);
        }
        Set<Integer> neibors = null;
        if (battle.getBattleType() == 3 || battle.getBattleType() == 14) {
            neibors = this.dataGetter.getWorldRoadCache().getNeighbors(battle.getDefBaseInfo().getId());
        }
        else {
            neibors = this.dataGetter.getSoloRoadCache().getNeighbors(battle.getDefBaseInfo().getId());
        }
        if (!neibors.contains(cityId)) {
            ErrorSceneLog.getInstance().appendErrorMsg("plug attack. cityId is not in neibors").appendClassName("BattleService").appendMethodName("TuJin").append("battleId", battleId).appendPlayerId(playerDto.playerId).appendPlayerName(playerDto.playerName).flush();
            return JsonBuilder.getJson(State.FAIL, LocalMessages.PLUG_IS_SHAMEFUL);
        }
        if (WorldCityCommon.barbarainCitySet.contains(cityId)) {
            final int degree = this.dataGetter.getRankService().hasBarTasks(playerDto.forceId);
            if (degree == 0 || !WorldCityCommon.forcIdManzuCityIdMap.get(playerDto.forceId).equals(cityId)) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.CANNOT_TUJIN_TO_OTHER_BARBARAIN_CITY);
            }
        }
        final PlayerInfo pi = battle.getInBattlePlayers().get(playerId);
        if (pi == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.NOT_IN_BATTLE_CANNOT_TUJIN);
        }
        int myCampNum = 0;
        int counterCampNum = 0;
        final Set<CampArmy> onQueueSet = new HashSet<CampArmy>();
        final List<CampArmy> caAvilableList = new LinkedList<CampArmy>();
        if (pi.isAttSide()) {
            myCampNum = battle.getAttCamp().size();
            counterCampNum = battle.getDefCamp().size();
            for (final CampArmy temp : battle.getAttCamp()) {
                if (temp.getPlayerId() == playerId && !temp.isPhantom()) {
                    caAvilableList.add(temp);
                }
            }
            for (final BattleArmy ba : battle.getAttList()) {
                onQueueSet.add(ba.getCampArmy());
            }
        }
        else {
            myCampNum = battle.getDefCamp().size();
            counterCampNum = battle.getAttCamp().size();
            for (final CampArmy temp : battle.getDefCamp()) {
                if (temp.getPlayerId() == playerId && !temp.isPhantom()) {
                    caAvilableList.add(temp);
                }
            }
            for (final BattleArmy ba : battle.getDefList()) {
                onQueueSet.add(ba.getCampArmy());
            }
        }
        int times = 3;
        final int timesReduce = this.dataGetter.getTechEffectCache().getTechEffect(playerDto.playerId, 39);
        if (timesReduce > 0) {
            times -= timesReduce;
        }
        if (myCampNum <= times * counterCampNum) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.LESS_THAN_3_TIMES_CANNOT_TUJIN);
        }
        if (caAvilableList.size() == 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.NOT_IN_BATTLE_CANNOT_TUJIN);
        }
        for (final CampArmy ca : onQueueSet) {
            caAvilableList.remove(ca);
        }
        if (caAvilableList.size() == 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.GENERAL_ON_QUEUE_CANNOT_TUJIN);
        }
        final Set<Integer> myCampGIdSet = new HashSet<Integer>();
        for (final CampArmy ca2 : caAvilableList) {
            myCampGIdSet.add(ca2.getGeneralId());
        }
        final Set<CampArmy> hpLowSet = new HashSet<CampArmy>();
        for (final CampArmy ca3 : caAvilableList) {
            final int maxHp = ca3.getMaxForces();
            if (ca3.getArmyHp() * 1.0 / maxHp < 0.05) {
                hpLowSet.add(ca3);
            }
        }
        for (final CampArmy ca3 : hpLowSet) {
            caAvilableList.remove(ca3);
        }
        if (caAvilableList.size() == 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.GENERAL_HP_LOW_CANNOT_TUJIN);
        }
        if (battle.getBattleType() == 3 || battle.getBattleType() == 14) {
            return this.doWorldTuJin(playerDto, battleId, battle, cityId, gIdSet);
        }
        return this.doJuBenTuJin(playerDto, battleId, battle, cityId, gIdSet);
    }
    
    private byte[] doWorldTuJin(final PlayerDto playerDto, final String battleId, Battle battle, final int cityId, final Set<Integer> gIdSet) {
        if (WorldCityCommon.mainCityNationIdMap.get(cityId) != null) {
            return JsonBuilder.getJson(State.FAIL, String.valueOf(LocalMessages.PLUG_IS_SHAMEFUL) + ((WorldCity)this.dataGetter.getWorldCityCache().get((Object)cityId)).getName());
        }
        if (!RankService.canEnterHJCenterCity(cityId)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.NATION_TASK_XY_CANNOT_ENTER);
        }
        if (WorldCityCommon.barbarainCitySet.contains(cityId)) {
            final int degree = this.dataGetter.getRankService().hasBarTasks(playerDto.forceId);
            if (degree == 0) {
                return JsonBuilder.getJson(State.FAIL, String.valueOf(LocalMessages.PLUG_IS_SHAMEFUL) + ((WorldCity)this.dataGetter.getWorldCityCache().get((Object)cityId)).getName());
            }
        }
        final String[] cityBatLocks = CityService.getCityLock(cityId, battle.getDefBaseInfo().getId());
        synchronized (cityBatLocks[0]) {
            // monitorenter(s = cityBatLocks[1])
            try {
                final Battle tempBattle = NewBattleManager.getInstance().getBattleByDefId(3, cityId);
                if (tempBattle == null) {
                    final City neiCity = this.dataGetter.getCityDao().read(cityId);
                    if (playerDto.forceId == neiCity.getForceId()) {
                        // monitorexit(s)
                        // monitorexit(cityBatLocks[0])
                        return JsonBuilder.getJson(State.FAIL, String.valueOf(LocalMessages.CANNOT_TUJIN_TO_THIS_CITY) + ((WorldCity)this.dataGetter.getWorldCityCache().get((Object)cityId)).getName());
                    }
                }
                final int playerId = playerDto.playerId;
                final Set<Integer> mistOpendedSet = new HashSet<Integer>();
                final PlayerWorld pw = this.dataGetter.getPlayerWorldDao().read(playerId);
                if (pw != null) {
                    final String attStr = pw.getAttedId();
                    if (!StringUtils.isBlank(attStr)) {
                        final String[] ids = attStr.split(",");
                        String[] array;
                        for (int length = (array = ids).length, i = 0; i < length; ++i) {
                            final String str = array[i];
                            mistOpendedSet.add(Integer.valueOf(str));
                        }
                    }
                }
                int battleType = 3;
                if (WorldCityCommon.barbarainCitySet.contains(cityId)) {
                    battleType = 14;
                }
                try {
                    final Set<CampArmy> quitGids = battle.quit(playerId, gIdSet, this.dataGetter, true);
                    final Set<CampArmy> tuJinFailSet = new HashSet<CampArmy>();
                    for (final CampArmy tuJinCa : quitGids) {
                        final List<Integer> gIdList = new ArrayList<Integer>();
                        gIdList.add(tuJinCa.getGeneralId());
                        final BattleStartResult battleStartResult = this.battleStart2(playerId, battleType, cityId, 0, gIdList);
                        if (!battleStartResult.succ) {
                            tuJinFailSet.add(tuJinCa);
                            ErrorSceneLog.getInstance().appendErrorMsg("TuJin battleStart failed").append("reason", battleStartResult.failReason).appendClassName("BattleService").appendMethodName("TuJin").append("playerId", playerId).append("battleType", battleType).append("city", ((WorldCity)this.dataGetter.getWorldCityCache().get((Object)cityId)).getName()).append("cityId", cityId).append("gId", tuJinCa.getGeneralId()).flush();
                        }
                    }
                    Set<CampArmy> bakFailSet = new HashSet<CampArmy>();
                    if (tuJinFailSet.size() > 0) {
                        battle = NewBattleManager.getInstance().getBattleByBatId(battleId);
                        if (battle != null) {
                            for (final CampArmy tuJinFailCa : tuJinFailSet) {
                                final List<Integer> gIdList2 = new ArrayList<Integer>();
                                gIdList2.add(tuJinFailCa.getGeneralId());
                                final BattleStartResult battleStartResult2 = this.battleStart2(playerId, battle.getBattleType(), battle.getDefBaseInfo().getId(), 0, gIdList2);
                                if (!battleStartResult2.succ) {
                                    bakFailSet.add(tuJinFailCa);
                                    ErrorSceneLog.getInstance().appendErrorMsg("TuJin. back to pre battle failed").append("reason", battleStartResult2.failReason).appendClassName("BattleService").appendMethodName("TuJin").append("playerId", playerId).appendBattleId(battle.getBattleId()).append("battleType", battle.getBattleType()).append("city", ((WorldCity)this.dataGetter.getWorldCityCache().get((Object)cityId)).getName()).append("cityId", cityId).append("gId", tuJinFailCa.getGeneralId()).flush();
                                }
                            }
                        }
                        else {
                            bakFailSet = tuJinFailSet;
                            ErrorSceneLog.getInstance().appendErrorMsg("TuJin. pre battle now is null").appendClassName("BattleService").appendMethodName("TuJin").append("playerId", playerId).append("battleType", battleType).appendBattleId(battleId).append("city", ((WorldCity)this.dataGetter.getWorldCityCache().get((Object)cityId)).getName()).append("cityId", cityId).flush();
                        }
                    }
                    if (bakFailSet.size() > 0) {
                        for (final CampArmy bakFailCa : bakFailSet) {
                            final int capitalCityId = WorldCityCommon.nationMainCityIdMap.get(bakFailCa.getForceId());
                            this.dataGetter.getPlayerGeneralMilitaryDao().updateLocationId(bakFailCa.getPlayerId(), bakFailCa.getGeneralId(), capitalCityId);
                            ErrorSceneLog.getInstance().appendErrorMsg("TuJin. pgm back to capital").appendClassName("BattleService").appendMethodName("TuJin").append("playerId", bakFailCa.getPlayerId()).append("battleType", battleType).append("city", ((WorldCity)this.dataGetter.getWorldCityCache().get((Object)capitalCityId)).getName()).append("cityId", capitalCityId).append("gId", bakFailCa.getGeneralId()).flush();
                        }
                    }
                    if (tuJinFailSet.size() < quitGids.size()) {
                        final Battle tuJinBattle = NewBattleManager.getInstance().getBattleByDefId(battleType, cityId);
                        if (tuJinBattle != null) {
                            final JsonDocument doc = new JsonDocument();
                            doc.startObject();
                            doc.createElement("battleId", tuJinBattle.getBattleId());
                            doc.createElement("battleType", battleType);
                            doc.createElement("defId", tuJinBattle.getDefBaseInfo().getId());
                            doc.endObject();
                            this.dataGetter.getIndividualTaskService().sendTaskMessage(playerDto, 1, "tujin");
                            // monitorexit(s)
                            // monitorexit(cityBatLocks[0])
                            return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
                        }
                        ErrorSceneLog.getInstance().appendErrorMsg("tuJinBattle is null").appendClassName("BattleService").appendMethodName("TuJin").append("playerId", playerId).append("battleType", battleType).append("city", ((WorldCity)this.dataGetter.getWorldCityCache().get((Object)cityId)).getName()).append("cityId", cityId).append("gIds", gIdSet.toString()).flush();
                        this.dataGetter.getIndividualTaskService().sendTaskMessage(playerDto, 1, "tujin");
                        // monitorexit(s)
                        // monitorexit(cityBatLocks[0])
                        return JsonBuilder.getJson(State.SUCCESS, "battle ended!");
                    }
                }
                catch (Exception e) {
                    BattleService.errorLog.error("//////////////////////////////////////////////////TuJin battleStart Exception.", e);
                    ErrorSceneLog.getInstance().appendErrorMsg("TuJin battleStart Exception").appendClassName("BattleService").appendMethodName("TuJin").append("playerId", playerId).append("battleType", battleType).append("city", ((WorldCity)this.dataGetter.getWorldCityCache().get((Object)cityId)).getName()).append("cityId", cityId).append("gIds", gIdSet.toString()).flush();
                    BattleService.errorLog.error("//////////////////////////////////////////////////handle TuJin battleStart Exception succ.");
                }
                // monitorexit(s)
                // monitorexit(cityBatLocks[0])
                return JsonBuilder.getJson(State.FAIL, LocalMessages.TUJIN_FAILED);
            }
            finally {}
        }
    }
    
    private byte[] doJuBenTuJin(final PlayerDto playerDto, final String battleId, Battle battle, final int cityId, final Set<Integer> gIdSet) {
        final int playerId = playerDto.playerId;
        final int battleType = 18;
        final String tuJinBattleId = NewBattleManager.getBattleId(18, playerId, cityId);
        final Battle tuJinBattle = NewBattleManager.getInstance().getBattleByBatId(tuJinBattleId);
        final JuBenDto juBenDto = JuBenManager.getInstance().getByPid(playerDto.playerId);
        final JuBenCityDto juBenCityDto = JuBenManager.getInstance().getJuBenCityDto(playerDto.playerId, cityId);
        if (tuJinBattle == null && playerDto.forceId == juBenCityDto.forceId) {
            return JsonBuilder.getJson(State.FAIL, String.valueOf(LocalMessages.CANNOT_TUJIN_TO_THIS_CITY) + ((SoloCity)this.dataGetter.getSoloCityCache().get((Object)cityId)).getName());
        }
        final OperationResult result = this.dataGetter.getCilvilTrickService().hasTrick(juBenCityDto);
        if (result != null && !result.getResult()) {
            return JsonBuilder.getJson(State.FAIL, result.getResultContent());
        }
        try {
            final Set<CampArmy> quitGids = battle.quit(playerId, gIdSet, this.dataGetter, true);
            if (quitGids == null) {
                return JsonBuilder.getJson(State.FAIL, String.valueOf(LocalMessages.LESS_THAN_3_TIMES_CANNOT_TUJIN) + "!");
            }
            final Set<CampArmy> tuJinFailSet = new HashSet<CampArmy>();
            for (final CampArmy tuJinCa : quitGids) {
                final List<Integer> gIdList = new ArrayList<Integer>();
                gIdList.add(tuJinCa.getGeneralId());
                final BattleStartResult battleStartResult = this.battleStart2(playerId, battleType, cityId, 0, gIdList);
                if (!battleStartResult.succ) {
                    tuJinFailSet.add(tuJinCa);
                    ErrorSceneLog.getInstance().appendErrorMsg("TuJin battleStart failed").append("reason", battleStartResult.failReason).appendClassName("BattleService").appendMethodName("TuJin").append("playerId", playerId).append("battleType", battleType).append("city", ((WorldCity)this.dataGetter.getWorldCityCache().get((Object)cityId)).getName()).append("cityId", cityId).append("gId", tuJinCa.getGeneralId()).flush();
                }
            }
            Set<CampArmy> bakFailSet = new HashSet<CampArmy>();
            if (tuJinFailSet.size() > 0) {
                battle = NewBattleManager.getInstance().getBattleByBatId(battleId);
                if (battle != null) {
                    for (final CampArmy tuJinFailCa : tuJinFailSet) {
                        final List<Integer> gIdList2 = new ArrayList<Integer>();
                        gIdList2.add(tuJinFailCa.getGeneralId());
                        final BattleStartResult battleStartResult2 = this.battleStart2(playerId, battle.getBattleType(), battle.getDefBaseInfo().getId(), 0, gIdList2);
                        if (!battleStartResult2.succ) {
                            bakFailSet.add(tuJinFailCa);
                            ErrorSceneLog.getInstance().appendErrorMsg("TuJin. back to pre battle failed").append("reason", battleStartResult2.failReason).appendClassName("BattleService").appendMethodName("TuJin").append("playerId", playerId).appendBattleId(battle.getBattleId()).append("battleType", battle.getBattleType()).append("city", ((WorldCity)this.dataGetter.getWorldCityCache().get((Object)cityId)).getName()).append("cityId", cityId).append("gId", tuJinFailCa.getGeneralId()).flush();
                        }
                    }
                }
                else {
                    bakFailSet = tuJinFailSet;
                    ErrorSceneLog.getInstance().appendErrorMsg("TuJin. pre battle now is null").appendClassName("BattleService").appendMethodName("TuJin").append("playerId", playerId).append("battleType", battleType).appendBattleId(battleId).append("city", ((WorldCity)this.dataGetter.getWorldCityCache().get((Object)cityId)).getName()).append("cityId", cityId).flush();
                }
            }
            if (bakFailSet.size() > 0) {
                for (final CampArmy bakFailCa : bakFailSet) {
                    final int capitalCityId = juBenDto.capital;
                    this.dataGetter.getPlayerGeneralMilitaryDao().moveJuben(bakFailCa.getPlayerId(), bakFailCa.getGeneralId(), 1, capitalCityId);
                    ErrorSceneLog.getInstance().appendErrorMsg("TuJin. pgm back to capital").appendClassName("BattleService").appendMethodName("TuJin").append("playerId", bakFailCa.getPlayerId()).append("battleType", battleType).append("city", ((SoloCity)this.dataGetter.getSoloCityCache().get((Object)capitalCityId)).getName()).append("cityId", capitalCityId).append("gId", bakFailCa.getGeneralId()).flush();
                }
            }
            if (tuJinFailSet.size() < quitGids.size()) {
                ScenarioEventMessageHelper.sendTujinScenarioMessage(playerId);
                final Battle tuJinBattleDoubleCheck = NewBattleManager.getInstance().getBattleByBatId(tuJinBattleId);
                if (tuJinBattleDoubleCheck != null) {
                    final JsonDocument doc = new JsonDocument();
                    doc.startObject();
                    doc.createElement("battleId", tuJinBattleDoubleCheck.getBattleId());
                    doc.createElement("battleType", battleType);
                    doc.createElement("defId", tuJinBattleDoubleCheck.getDefBaseInfo().getId());
                    doc.endObject();
                    return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
                }
                ErrorSceneLog.getInstance().appendErrorMsg("tuJinBattle is null").appendClassName("BattleService").appendMethodName("TuJin").append("playerId", playerId).append("battleType", battleType).append("city", ((WorldCity)this.dataGetter.getWorldCityCache().get((Object)cityId)).getName()).append("cityId", cityId).append("gIds", gIdSet.toString()).flush();
                return JsonBuilder.getJson(State.SUCCESS, "battle ended!");
            }
        }
        catch (Exception e) {
            BattleService.errorLog.error("//////////////////////////////////////////////////TuJin battleStart Exception.", e);
            ErrorSceneLog.getInstance().appendErrorMsg("TuJin battleStart Exception").appendClassName("BattleService").appendMethodName("TuJin").append("playerId", playerId).append("battleType", battleType).append("city", ((WorldCity)this.dataGetter.getWorldCityCache().get((Object)cityId)).getName()).append("cityId", cityId).append("gIds", gIdSet.toString()).flush();
            BattleService.errorLog.error("//////////////////////////////////////////////////handle TuJin battleStart Exception succ.");
        }
        return JsonBuilder.getJson(State.FAIL, LocalMessages.TUJIN_FAILED);
    }
    
    @Override
    public byte[] useOfficerTokenInBattle(final PlayerDto playerDto, final int cityId, final String battleId) {
        final Battle battle = NewBattleManager.getInstance().getBattleByBatId(battleId);
        if (battle == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.BATTLE_ENDED_CANNOT_USE_OFFICER_TOKEN);
        }
        if (WorldFarmCache.forceCityIdMap.values().contains(cityId)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final WorldCity worldCity = (WorldCity)this.dataGetter.getWorldCityCache().get((Object)cityId);
        int battleCityId = 0;
        try {
            battleCityId = battle.getDefBaseInfo().getId();
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("BattleService useOfficerTokenInBattle ", e);
        }
        if (battle == null || worldCity == null || battleCityId != cityId) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.BATTLE_ENDED_CANNOT_USE_OFFICER_TOKEN);
        }
        final PlayerOfficeRelative por = this.dataGetter.getPlayerOfficeRelativeDao().read(playerDto.playerId);
        final int officerId = (por == null || por.getOfficerId() == null) ? 0 : por.getOfficerId();
        if (battle.getBattleType() != 3) {
            ErrorSceneLog.getInstance().appendErrorMsg("plug attack. battle is not city battle").appendClassName("BattleService").appendMethodName("AssembleBattle").append("battleId", battleId).appendPlayerId(playerDto.playerId).appendPlayerName(playerDto.playerName).flush();
            return JsonBuilder.getJson(State.FAIL, LocalMessages.BATTLE_TYPE_ERROR_CANNOT_USE);
        }
        final int playerId = playerDto.playerId;
        Integer num = battle.attSideDetail.get(playerDto.forceId);
        if (num == null) {
            num = battle.defSideDetail.get(playerDto.forceId);
        }
        if (num == null || num <= 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.MYSIDE_NOT_IN_BATTLE_CANNOT_USE);
        }
        final Tuple<Boolean, String> result = this.useOfficerToken(playerId, cityId, officerId, battleId);
        if (!(boolean)result.left) {
            return JsonBuilder.getJson(State.FAIL, result.right);
        }
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Override
    public byte[] replyOfficerToken(final PlayerDto playerDto, final Battle battle, final int cityId, final String gIds) {
        if (this.dataGetter.getAutoBattleService().InAutoBattleMode(playerDto.playerId)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.AUTO_BATTLE_CANNOT_OPERATE);
        }
        Tuple<Boolean, byte[]> tuple = null;
        try {
            this.dataGetter.getIndividualTaskService().sendTaskMessage(playerDto, 1, "jinling");
            return this.battleStart(playerDto.playerId, battle.getBattleType(), cityId, gIds, 0);
        }
        catch (Exception e) {
            BattleService.errorLog.error("className:BattleService#methodName:replyOfficerToken");
            BattleService.errorLog.error(e.getMessage());
            BattleService.errorLog.error(this, e);
            tuple = new Tuple();
            tuple.left = false;
            tuple.right = JsonBuilder.getJson(State.FAIL, "");
            return tuple.right;
        }
    }
    
    @Override
    public Tuple<Boolean, String> getReplyReward(final int cityId, final int forceId, final String battleId, final int playerId, final String gIds) {
        final Tuple<Boolean, String> result = new Tuple();
        boolean suc = false;
        String reason = "";
        final Battle battle = NewBattleManager.getInstance().getBattleByBatId(battleId);
        int[] generals = null;
        try {
            final String[] generalId = gIds.split(",");
            if (generalId.length <= 0) {
                reason = LocalMessages.BATTLE_INT_BATTLE_BLOOD_NO_ENOUGH;
                result.left = suc;
                result.right = reason;
                return result;
            }
            generals = new int[generalId.length];
            for (int i = 0; i < generalId.length; ++i) {
                if (!StringUtils.isBlank(generalId[i])) {
                    generals[i] = Integer.parseInt(generalId[i]);
                }
            }
        }
        catch (Exception e) {
            reason = LocalMessages.T_COMM_10011;
            result.left = suc;
            result.right = reason;
            return result;
        }
        final JuBenDto juBenDto = JuBenManager.getInstance().getByPid(playerId);
        if (juBenDto != null) {
            result.left = suc;
            result.right = LocalMessages.IN_JUBEN_NOT_JOIN_TEAM;
            return result;
        }
        final PlayerWorld pw = this.dataGetter.getPlayerWorldDao().read(playerId);
        final String attedCity = (pw == null) ? "" : pw.getAttedId();
        String[] cities = null;
        boolean isInMist = true;
        try {
            cities = attedCity.split(",");
            String[] array;
            for (int length = (array = cities).length, j = 0; j < length; ++j) {
                final String city = array[j];
                if (cityId == Integer.parseInt(city)) {
                    isInMist = false;
                    break;
                }
            }
        }
        catch (Exception e2) {
            isInMist = true;
        }
        if (isInMist) {
            reason = LocalMessages.CITY_IN_MIST;
            result.left = suc;
            result.right = reason;
            return result;
        }
        final Map<Integer, PlayerGeneralMilitary> pgmMap = this.dataGetter.getPlayerGeneralMilitaryDao().getMilitaryMap(playerId);
        List<OfficerTokenUseInfo> token = this.getCurrentInUseToken(forceId);
        if (token == null || token.isEmpty() || token.size() == 0 || battle == null) {
            reason = LocalMessages.BATTLE_ENDED_CANNOT_USE_OFFICER_TOKEN;
        }
        else {
            StringBuffer sBuffer = null;
            List<Integer> pgmList = null;
            PlayerGeneralMilitary pgm = null;
            final long time = System.currentTimeMillis() / 1000L;
            for (final OfficerTokenUseInfo t : token) {
                if (t != null) {
                    if (generals == null) {
                        continue;
                    }
                    if (!t.getBattleId().equalsIgnoreCase(battleId)) {
                        continue;
                    }
                    final long expire = t.getExpireTime();
                    if (expire <= time) {
                        continue;
                    }
                    sBuffer = new StringBuffer();
                    int count = 0;
                    pgmList = BattleService.tokenRewardMap.get(this.orgnizeKey(forceId, t.getOfficerId()));
                    int[] array2;
                    for (int length2 = (array2 = generals).length, k = 0; k < length2; ++k) {
                        final int s = array2[k];
                        pgm = pgmMap.get(s);
                        final boolean isInTeam = TeamManager.getInstance().isJoinTeam2(pgm.getPlayerId(), pgm.getGeneralId());
                        if (!isInTeam) {
                            if (pgm != null) {
                                if (pgm.getState() < 2) {
                                    final GeneralMoveDto gmd = CityService.getGeneralMoveDto(pgm.getPlayerId(), pgm.getGeneralId());
                                    if (gmd != null) {
                                        if (gmd.cityState == 22) {
                                            continue;
                                        }
                                        if (gmd.cityState == 23) {
                                            continue;
                                        }
                                    }
                                    if (pgmList == null) {
                                        ++count;
                                        pgmList = new ArrayList<Integer>();
                                        pgmList.add(pgm.getVId());
                                        final String key = this.orgnizeKey(t.getForceId(), t.getOfficerId());
                                        BattleService.tokenRewardMap.put(key, pgmList);
                                        sBuffer.append(pgm.getGeneralId()).append("#");
                                    }
                                    else {
                                        if (pgmList.contains(pgm.getVId())) {
                                            final General general = (General)this.dataGetter.getGeneralCache().get((Object)pgm.getGeneralId());
                                            reason = MessageFormatter.format(LocalMessages.REWARD_EALIER, new Object[] { general.getName() });
                                            result.left = suc;
                                            result.right = reason;
                                            return result;
                                        }
                                        ++count;
                                        pgmList.add(pgm.getVId());
                                        sBuffer.append(pgm.getGeneralId()).append("#");
                                    }
                                }
                            }
                        }
                    }
                    if (sBuffer.length() > 0) {
                        sBuffer.deleteCharAt(sBuffer.length() - 1);
                    }
                    if (count <= 0) {
                        continue;
                    }
                    suc = true;
                    final Halls halls = (Halls)this.dataGetter.getHallsCache().get((Object)t.getOfficerId());
                    final String reward = halls.getOrderReward();
                    this.getReward(reward, playerId, count, 2);
                    reason = sBuffer.toString();
                    final PlayerTech pt = this.dataGetter.getPlayerTechDao().getPlayerTech(playerId, 502);
                    if (pt != null && pt.getStatus() == 5 && this.playerAttributeDao.getFunctionId(playerId).toCharArray()[32] == '1') {
                        this.dataGetter.getRankService().addFeat(playerId, 15 * count);
                        break;
                    }
                    break;
                }
            }
        }
        if (suc) {
            final JsonDocument doc = new JsonDocument();
            doc.startObject();
            this.getCurrentOfficerTokenPushInfo(token, doc, playerId);
            doc.endObject();
            Players.push(playerId, PushCommand.PUSH_OFFICER_TOKEN, doc.toByte());
        }
        token = null;
        result.left = suc;
        result.right = reason;
        return result;
    }
    
    private String orgnizeKey(final int forceId, final int officerId) {
        final StringBuffer sb = new StringBuffer();
        sb.append(forceId).append("-").append(officerId);
        return sb.toString();
    }
    
    @Override
    public void deleteTheTokenReward(final String params) {
        final long time = System.currentTimeMillis();
        int officerId = 0;
        int forceId = 0;
        final String[] single = params.split("-");
        try {
            forceId = Integer.parseInt(single[0]);
            officerId = Integer.parseInt(single[1]);
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("BattleService deleteTheTokenReward", e);
            ErrorSceneLog.getInstance().appendClassName("BattleService").appendMethodName("deleteTheTokenReward").append("params", params).flush();
        }
        final OfficerTokenUseInfo info = BattleService.officerUseMap.get(this.orgnizeKey(forceId, officerId));
        if (info == null || info.getExpireTime() > time) {
            return;
        }
        this.dealTokenBattle(info.getBattleId(), forceId);
    }
    
    @Override
    public void dealTokenBattle(final String battleId, final int forceId) {
        try {
            List<OfficerTokenUseInfo> list = this.removeBattleToken(battleId, forceId);
            final Set<Integer> forceSet = new HashSet<Integer>();
            for (final OfficerTokenUseInfo info : list) {
                final int force = info.getForceId();
                final int officerId = info.getOfficerId();
                this.officerTokenDao.resetBattle(force, officerId);
                BattleService.tokenRewardMap.remove(this.orgnizeKey(info.getForceId(), officerId));
                forceSet.add(force);
            }
            for (final Integer single : forceSet) {
                AddJob.getInstance().addJob(JobClassMethondEnum.BATTLESERVICE_PUSHTOKENMESSAGE, String.valueOf(single), 0L);
            }
            list = null;
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("BattleService dealTokenBattle", e);
            ErrorSceneLog.getInstance().appendClassName("BattleService").appendMethodName("dealTokenBattle").append("battleId", battleId).flush();
        }
    }
    
    private List<OfficerTokenUseInfo> removeBattleToken(final String battleId, final int forceId) {
        List<String> list = new ArrayList<String>();
        final List<OfficerTokenUseInfo> result = new ArrayList<OfficerTokenUseInfo>();
        for (final String key : BattleService.officerUseMap.keySet()) {
            final OfficerTokenUseInfo tokenUseInfo = BattleService.officerUseMap.get(key);
            if (tokenUseInfo == null) {
                continue;
            }
            int tokenForce = 0;
            try {
                tokenForce = Integer.parseInt(key.split("-")[0]);
            }
            catch (Exception e) {
                BattleService.errorLog.error("key " + key + "is wrong", e);
                continue;
            }
            if (tokenUseInfo.getBattleId().equalsIgnoreCase(battleId)) {
                if (forceId == 0) {
                    list.add(key);
                    result.add(tokenUseInfo);
                }
                else if (forceId == tokenForce) {
                    list.add(key);
                    result.add(tokenUseInfo);
                }
            }
            if (tokenUseInfo.getExpireTime() > System.currentTimeMillis() || list.contains(key)) {
                continue;
            }
            list.add(key);
            result.add(tokenUseInfo);
        }
        for (final String key : list) {
            BattleService.officerUseMap.remove(key);
        }
        list = null;
        return result;
    }
    
    @Override
    public List<OfficerTokenUseInfo> getCurrentInUseToken(final int forceId) {
        final List<OfficerTokenUseInfo> list = new ArrayList<OfficerTokenUseInfo>();
        String[] keys = null;
        for (final String key : BattleService.officerUseMap.keySet()) {
            keys = key.split("-");
            final int force = Integer.parseInt(keys[0]);
            if (force == forceId) {
                list.add(BattleService.officerUseMap.get(key));
            }
        }
        return list;
    }
    
    public void getReward(final String reward, final int playerId, final int count, final int rewardReason) {
        final String[] rewards = reward.split(";");
        String[] array;
        for (int length = (array = rewards).length, i = 0; i < length; ++i) {
            final String r = array[i];
            final String[] single = r.split(",");
            final int type = RewardType.getTypeInt(single[0]);
            final int value = Integer.parseInt(single[1]) * count;
            RewardType.reward(this.dataGetter, type, value, playerId, rewardReason);
        }
    }
    
    public void rewardDoc(final Map<Integer, Integer> map, final JsonDocument doc) {
        doc.startArray("rewards");
        for (final Integer i : map.keySet()) {
            doc.startObject();
            doc.createElement("type", i);
            doc.createElement("value", map.get(i));
            doc.endObject();
        }
        doc.endArray();
    }
    
    public void rewardDoc(final String reward, final JsonDocument doc) {
        String[] rewards = reward.split(";");
        doc.startArray("rewards");
        String[] array;
        for (int length = (array = rewards).length, i = 0; i < length; ++i) {
            final String s = array[i];
            final String[] single = s.split(",");
            doc.startObject();
            doc.createElement("type", RewardType.getTypeInt(single[0]));
            doc.createElement("value", single[1]);
            doc.endObject();
        }
        rewards = null;
        doc.endArray();
    }
    
    public static boolean rewardContains(final Integer vId) {
        List<Integer> list = null;
        for (final String key : BattleService.tokenRewardMap.keySet()) {
            if (StringUtils.isBlank(key)) {
                continue;
            }
            list = BattleService.tokenRewardMap.get(key);
            if (list.contains(vId)) {
                return true;
            }
        }
        list = null;
        return false;
    }
    
    @Override
    public byte[] useAutoStrategy(final PlayerDto playerDto) {
        final int playerId = playerDto.playerId;
        final int autoStrategy = this.dataGetter.getTechEffectCache().getTechEffect(playerId, 43);
        if (autoStrategy <= 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.ZDZS_NOT_GAIN_YET);
        }
        this.dataGetter.getPlayerBattleAttributeDao().updateAutoStrategy(playerId, 1);
        final Map<Integer, Battle> MyBattles = NewBattleManager.getInstance().getBattleByPid(playerId);
        for (final Battle bat : MyBattles.values()) {
            final PlayerInfo pi = bat.getInBattlePlayers().get(playerId);
            if (pi == null) {
                ErrorSceneLog.getInstance().appendErrorMsg("useAutoStrategy playerInfo is null").appendBattleId(bat.getBattleId()).appendPlayerName(playerDto.playerName).appendPlayerId(playerId).flush();
            }
            else {
                pi.setAutoStrategy(1);
                CampArmy firstAttCA = null;
                boolean attIsNPC = true;
                if (bat.getAttList().size() > 0) {
                    firstAttCA = bat.getAttList().get(0).getCampArmy();
                    attIsNPC = (firstAttCA.getPlayerId() <= 0 || firstAttCA.isPhantom());
                }
                CampArmy firstDefCA = null;
                boolean defIsNPC = true;
                if (bat.getDefList().size() > 0) {
                    firstDefCA = bat.getDefList().get(0).getCampArmy();
                    defIsNPC = (firstDefCA.getPlayerId() <= 0 || firstDefCA.isPhantom());
                }
                final boolean inFirstRow = (firstAttCA != null && firstAttCA.getPlayerId() == playerId && !firstAttCA.isPhantom()) || (firstDefCA != null && firstDefCA.getPlayerId() == playerId && !firstDefCA.isPhantom());
                if (!inFirstRow || bat.getNextMaxExeTime() <= bat.getNextMinExeTime()) {
                    continue;
                }
                synchronized (bat.getBattleId()) {
                    boolean attAutoStChoosed = false;
                    if (firstAttCA != null && firstAttCA.getPlayerId() > 0 && !firstAttCA.isPhantom()) {
                        final PlayerInfo piAtt = bat.getInBattlePlayers().get(firstAttCA.getPlayerId());
                        attAutoStChoosed = (piAtt != null && piAtt.getAutoStrategy() == 1);
                    }
                    boolean defAutoStChoosed = false;
                    if (firstDefCA != null && firstDefCA.getPlayerId() > 0 && !firstDefCA.isPhantom()) {
                        final PlayerInfo piDef = bat.getInBattlePlayers().get(firstDefCA.getPlayerId());
                        defAutoStChoosed = (piDef != null && piDef.getAutoStrategy() == 1);
                    }
                    if ((attAutoStChoosed && defAutoStChoosed) || (attAutoStChoosed && defIsNPC) || (attIsNPC && defAutoStChoosed)) {
                        bat.setNextMaxExeTime(bat.getNextMinExeTime());
                        final long exeTime = bat.getNextMaxExeTime();
                        BattleService.timerLog.info(LogUtil.formatThreadLog("BattleService", "useAutoStrategy", 0, 0L, "battleId:" + bat.getBattleId() + "|roundNum:" + bat.getRoundNum() + "|exeTime:" + exeTime));
                        bat.changeExeTime(exeTime);
                    }
                }
                // monitorexit(bat.getBattleId())
            }
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("done", 1);
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public byte[] cancelAutoStrategy(final PlayerDto playerDto) {
        final int playerId = playerDto.playerId;
        final int autoStrategy = this.dataGetter.getTechEffectCache().getTechEffect(playerId, 43);
        if (autoStrategy <= 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.ZDZS_NOT_GAIN_YET);
        }
        this.dataGetter.getPlayerBattleAttributeDao().updateAutoStrategy(playerId, 0);
        final Map<Integer, Battle> MyBattles = NewBattleManager.getInstance().getBattleByPid(playerId);
        for (final Battle bat : MyBattles.values()) {
            final PlayerInfo pi = bat.getInBattlePlayers().get(playerId);
            if (pi == null) {
                ErrorSceneLog.getInstance().appendErrorMsg("cancelAutoStrategy playerInfo is null").appendBattleId(bat.getBattleId()).appendPlayerName(playerDto.playerName).appendPlayerId(playerId).flush();
            }
            else if (pi.getAutoStrategy() == 0) {
                ErrorSceneLog.getInstance().appendErrorMsg("cancelAutoStrategy ignore due to plug or error").appendBattleId(bat.getBattleId()).appendPlayerName(playerDto.playerName).appendPlayerId(playerDto.playerId).append("roundNum", bat.getRoundNum()).append("pi.autoStrategy", pi.getAutoStrategy()).append("NextMaxExeTime", bat.getNextMaxExeTime()).append("NextMinExeTime", bat.getNextMinExeTime()).flush();
            }
            else {
                pi.setAutoStrategy(0);
                CampArmy firstAttCA = null;
                if (bat.getAttList().size() > 0) {
                    firstAttCA = bat.getAttList().get(0).getCampArmy();
                }
                CampArmy firstDefCA = null;
                if (bat.getDefList().size() > 0) {
                    firstDefCA = bat.getDefList().get(0).getCampArmy();
                }
                final boolean inFirstRow = (firstAttCA != null && firstAttCA.getPlayerId() == playerId && !firstAttCA.isPhantom()) || (firstDefCA != null && firstDefCA.getPlayerId() == playerId && !firstDefCA.isPhantom());
                if (!inFirstRow || bat.getNextMaxExeTime() != bat.getNextMinExeTime()) {
                    continue;
                }
                final Builder builder = BuilderFactory.getInstance().getBuilder(bat.getBattleType());
                synchronized (bat.getBattleId()) {
                    if (builder.isBattleEnd(this.dataGetter, bat) == 1 && bat.getNextMaxExeTime() == bat.getNextMinExeTime()) {
                        bat.setNextMaxExeTime(bat.getNextMinExeTime() + 6000L);
                        final long exeTime = bat.getNextMaxExeTime();
                        BattleService.timerLog.info(LogUtil.formatThreadLog("BattleService", "cancelAutoStrategy", 0, 0L, "battleId:" + bat.getBattleId() + "|roundNum:" + bat.getRoundNum() + "|exeTime:" + exeTime));
                        bat.changeExeTime(exeTime);
                    }
                }
                // monitorexit(bat.getBattleId())
            }
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("done", 1);
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public void addBarbarainNpc(final Barbarain barbarain, final int forceId, final int cityId, final int npcNum) {
        final Random rand = new Random();
        Integer[] armyIds = null;
        switch (forceId) {
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
            default: {
                ErrorSceneLog.getInstance().appendErrorMsg("forceId error").append("forceId", forceId).appendMethodName("addBarbarainNpc").flush();
                break;
            }
        }
        final Builder builder = BuilderFactory.getInstance().getBuilder(3);
        Battle cityBattle = NewBattleManager.getInstance().getBattleByDefId(3, cityId);
        if (cityBattle != null) {
            final int defForceId = cityBattle.getDefBaseInfo().getForceId();
            int battleSide = -1;
            if (defForceId == forceId) {
                battleSide = 0;
            }
            else {
                battleSide = 1;
            }
            final List<BarbarainPhantom> list = new ArrayList<BarbarainPhantom>();
            for (int i = 0; i < npcNum; ++i) {
                final BarbarainPhantom barPhantom = new BarbarainPhantom();
                barPhantom.setNpcType(1);
                barPhantom.setCreateTime(new Date());
                barPhantom.setLocationId(cityId);
                barPhantom.setForceId(forceId);
                barPhantom.setBarbarainId(barbarain.getId());
                barPhantom.setTacticval(1);
                barPhantom.setState(3);
                final int randInt = rand.nextInt(armyIds.length);
                final int armyId = armyIds[randInt];
                final Army army = (Army)this.dataGetter.getArmyCache().get((Object)armyId);
                barPhantom.setArmyId(armyId);
                barPhantom.setHp(army.getArmyHp());
                barPhantom.setVId(BattleService.barVid.incrementAndGet());
                list.add(barPhantom);
                final CampArmy barCa = builder.copyArmyformBarPhantom(this.dataGetter, barbarain, cityBattle, barPhantom, battleSide);
                cityBattle.joinCampArmy(this.dataGetter, battleSide, barCa);
            }
            this.dataGetter.getBarbarainPhantomDao().batchCreate(list);
        }
        else {
            final City city = this.dataGetter.getCityDao().read(cityId);
            if (city.getForceId() == forceId) {
                final List<BarbarainPhantom> list2 = new ArrayList<BarbarainPhantom>();
                for (int j = 0; j < npcNum; ++j) {
                    final BarbarainPhantom barPhantom2 = new BarbarainPhantom();
                    barPhantom2.setNpcType(1);
                    barPhantom2.setCreateTime(new Date());
                    barPhantom2.setLocationId(cityId);
                    barPhantom2.setForceId(forceId);
                    barPhantom2.setBarbarainId(barbarain.getId());
                    barPhantom2.setTacticval(1);
                    barPhantom2.setState(0);
                    final int randInt2 = rand.nextInt(armyIds.length);
                    final int armyId2 = armyIds[randInt2];
                    final Army army2 = (Army)this.dataGetter.getArmyCache().get((Object)armyId2);
                    barPhantom2.setArmyId(armyId2);
                    barPhantom2.setHp(army2.getArmyHp());
                    barPhantom2.setVId(BattleService.barVid.incrementAndGet());
                    list2.add(barPhantom2);
                }
                this.dataGetter.getBarbarainPhantomDao().batchCreate(list2);
            }
            else {
                final List<BarbarainPhantom> list2 = new ArrayList<BarbarainPhantom>();
                for (int j = 0; j < npcNum; ++j) {
                    final BarbarainPhantom barPhantom2 = new BarbarainPhantom();
                    barPhantom2.setNpcType(1);
                    barPhantom2.setCreateTime(new Date());
                    barPhantom2.setLocationId(cityId);
                    barPhantom2.setForceId(forceId);
                    barPhantom2.setBarbarainId(barbarain.getId());
                    barPhantom2.setTacticval(1);
                    barPhantom2.setState(0);
                    final int randInt2 = rand.nextInt(armyIds.length);
                    final int armyId2 = armyIds[randInt2];
                    final Army army2 = (Army)this.dataGetter.getArmyCache().get((Object)armyId2);
                    barPhantom2.setArmyId(armyId2);
                    barPhantom2.setHp(army2.getArmyHp());
                    barPhantom2.setVId(BattleService.barVid.incrementAndGet());
                    list2.add(barPhantom2);
                }
                this.dataGetter.getBarbarainPhantomDao().batchCreate(list2);
                if (WorldCityCommon.mainCityNationIdMap.containsKey(cityId)) {
                    return;
                }
                final WorldCity wc = (WorldCity)this.dataGetter.getWorldCityCache().get((Object)cityId);
                String battleId = null;
                battleId = CityBuilder.getBattleId(this.dataGetter, forceId, wc.getId());
                final Terrain terrain = builder.getTerrain(-1, wc.getId(), this.dataGetter);
                cityBattle = NewBattleManager.getInstance().createBattle(battleId);
                if (cityBattle == null) {
                    ErrorSceneLog.getInstance().appendErrorMsg("create battle fail").appendBattleId(battleId).append("barId", barbarain.getId()).append("forceId", forceId).append("cityId", cityId).appendMethodName("addBarbarainNpc").flush();
                    return;
                }
                final BattleAttacker battleAttacker = new BattleAttacker();
                battleAttacker.attType = 4;
                battleAttacker.attForceId = forceId;
                battleAttacker.attPlayerId = -4;
                battleAttacker.attBarbarain = barbarain;
                cityBattle.init(battleAttacker, 3, cityId, this.dataGetter, false, terrain.getValue());
                builder.dealUniqueStaff(this.dataGetter, cityBattle, -1, cityId);
            }
        }
    }
    
    @Override
    public void addBarbarainTryNpc(final CdExams cdExams, final int stage, final int forceId) {
        final Random rand = new Random();
        final List<Integer> armyIds = new ArrayList<Integer>();
        final int playerForceId = WorldCityCommon.manZuPlayerForceMap.get(forceId);
        final CdExamsObj ceo = this.getCdExamsObjByStageAndForceId(cdExams, stage, playerForceId);
        final String[] cityIds = ceo.getCityIds().split(";");
        final String[] armyIdsStr = ceo.getArmyIds().split(";");
        String[] array;
        for (int length = (array = armyIdsStr).length, k = 0; k < length; ++k) {
            final String temp = array[k];
            if (StringUtils.isNotBlank(temp)) {
                armyIds.add(Integer.parseInt(temp));
            }
        }
        final int npcNum = ceo.getGeneralNum();
        String[] array2;
        for (int length2 = (array2 = cityIds).length, l = 0; l < length2; ++l) {
            final String cityIdStr = array2[l];
            if (StringUtils.isBlank(cityIdStr)) {
                break;
            }
            final int cityId = Integer.parseInt(cityIdStr);
            int battleType = 3;
            if (WorldCityCommon.barbarainCitySet.contains(cityId)) {
                battleType = 14;
            }
            final Builder builder = BuilderFactory.getInstance().getBuilder(3);
            Battle cityBattle = NewBattleManager.getInstance().getBattleByDefId(battleType, cityId);
            if (cityBattle != null) {
                final int defForceId = cityBattle.getDefBaseInfo().getForceId();
                int battleSide = -1;
                if (defForceId == forceId) {
                    battleSide = 0;
                }
                else {
                    battleSide = 1;
                }
                final List<BarbarainPhantom> list = new ArrayList<BarbarainPhantom>();
                for (int i = 0; i < npcNum; ++i) {
                    final BarbarainPhantom barPhantom = new BarbarainPhantom();
                    barPhantom.setNpcType(4);
                    barPhantom.setCreateTime(new Date());
                    barPhantom.setLocationId(cityId);
                    barPhantom.setForceId(forceId);
                    barPhantom.setBarbarainId(cdExams.getId());
                    barPhantom.setTacticval(1);
                    barPhantom.setState(3);
                    final int randInt = rand.nextInt(armyIds.size());
                    final int armyId = armyIds.get(randInt);
                    final Army army = (Army)this.dataGetter.getArmyCache().get((Object)armyId);
                    barPhantom.setArmyId(armyId);
                    barPhantom.setHp(army.getArmyHp());
                    barPhantom.setVId(BattleService.barVid.incrementAndGet());
                    barPhantom.setName(ceo.getName());
                    list.add(barPhantom);
                    final CampArmy barCa = builder.copyArmyfromBarPhantom4(this.dataGetter, cityBattle, barPhantom, battleSide);
                    cityBattle.joinCampArmy(this.dataGetter, battleSide, barCa);
                }
                this.dataGetter.getBarbarainPhantomDao().batchCreate(list);
            }
            else {
                final City city = this.dataGetter.getCityDao().read(cityId);
                if (city.getForceId() == forceId) {
                    final List<BarbarainPhantom> list2 = new ArrayList<BarbarainPhantom>();
                    for (int j = 0; j < npcNum; ++j) {
                        final BarbarainPhantom barPhantom2 = new BarbarainPhantom();
                        barPhantom2.setNpcType(4);
                        barPhantom2.setCreateTime(new Date());
                        barPhantom2.setLocationId(cityId);
                        barPhantom2.setForceId(forceId);
                        barPhantom2.setBarbarainId(cdExams.getId());
                        barPhantom2.setTacticval(1);
                        barPhantom2.setState(0);
                        final int randInt2 = rand.nextInt(armyIds.size());
                        final int armyId2 = armyIds.get(randInt2);
                        final Army army2 = (Army)this.dataGetter.getArmyCache().get((Object)armyId2);
                        barPhantom2.setArmyId(armyId2);
                        barPhantom2.setHp(army2.getArmyHp());
                        barPhantom2.setVId(BattleService.barVid.incrementAndGet());
                        barPhantom2.setName(ceo.getName());
                        list2.add(barPhantom2);
                    }
                    this.dataGetter.getBarbarainPhantomDao().batchCreate(list2);
                }
                else {
                    final List<BarbarainPhantom> list2 = new ArrayList<BarbarainPhantom>();
                    for (int j = 0; j < npcNum; ++j) {
                        final BarbarainPhantom barPhantom2 = new BarbarainPhantom();
                        barPhantom2.setNpcType(4);
                        barPhantom2.setCreateTime(new Date());
                        barPhantom2.setLocationId(cityId);
                        barPhantom2.setForceId(forceId);
                        barPhantom2.setBarbarainId(cdExams.getId());
                        barPhantom2.setTacticval(1);
                        barPhantom2.setState(0);
                        final int randInt2 = rand.nextInt(armyIds.size());
                        final int armyId2 = armyIds.get(randInt2);
                        final Army army2 = (Army)this.dataGetter.getArmyCache().get((Object)armyId2);
                        barPhantom2.setArmyId(armyId2);
                        barPhantom2.setHp(army2.getArmyHp());
                        barPhantom2.setVId(BattleService.barVid.incrementAndGet());
                        barPhantom2.setName(ceo.getName());
                        list2.add(barPhantom2);
                    }
                    this.dataGetter.getBarbarainPhantomDao().batchCreate(list2);
                    if (WorldCityCommon.mainCityNationIdMap.containsKey(cityId)) {
                        return;
                    }
                    final WorldCity wc = (WorldCity)this.dataGetter.getWorldCityCache().get((Object)cityId);
                    String battleId = null;
                    battleId = CityBuilder.getBattleId(this.dataGetter, forceId, wc.getId());
                    final Terrain terrain = builder.getTerrain(-1, wc.getId(), this.dataGetter);
                    cityBattle = NewBattleManager.getInstance().createBattle(battleId);
                    if (cityBattle == null) {
                        ErrorSceneLog.getInstance().appendErrorMsg("create battle fail").appendBattleId(battleId).append("cdExamsId", cdExams.getId()).append("forceId", forceId).append("cityId", cityId).appendClassName("BattleService").appendMethodName("addBarbarainTryNpc").flush();
                        return;
                    }
                    final BattleAttacker battleAttacker = new BattleAttacker();
                    battleAttacker.attType = 4;
                    battleAttacker.attForceId = forceId;
                    battleAttacker.attPlayerId = -4;
                    cityBattle.init(battleAttacker, battleType, cityId, this.dataGetter, false, terrain.getValue());
                    builder.dealUniqueStaff(this.dataGetter, cityBattle, -1, cityId);
                }
            }
        }
    }
    
    @Override
    public CdExamsObj getCdExamsObjByStageAndForceId(final CdExams cdExams, final int stage, final int forceId) {
        final CdExamsObj obj = new CdExamsObj();
        if (1 == forceId) {
            if (stage == 0) {
                obj.setName(cdExams.getWeiName0());
                obj.setArmyIds(cdExams.getWeiArmies0());
                obj.setCityIds(WorldCityCommon.forcIdManzuCityIdMap.get(forceId).toString());
                obj.setGeneralLv(cdExams.getGLv0());
                obj.setGeneralNum(cdExams.getGNum0());
                obj.setOpenNextNum(cdExams.getOpenKg1());
            }
            else if (1 == stage) {
                obj.setName(cdExams.getWeiName1());
                obj.setArmyIds(cdExams.getWeiArmies1());
                obj.setCityIds(cdExams.getWeiCities1());
                obj.setGeneralLv(cdExams.getGLv1());
                obj.setGeneralNum(cdExams.getGNum1());
                obj.setOpenNextNum(cdExams.getOpenKg2());
            }
            else if (2 == stage) {
                obj.setName(cdExams.getWeiName2());
                obj.setArmyIds(cdExams.getWeiArmies2());
                obj.setCityIds(cdExams.getWeiCities2());
                obj.setGeneralLv(cdExams.getGLv2());
                obj.setGeneralNum(cdExams.getGNum2());
                obj.setOpenNextNum(cdExams.getOpenKg3());
            }
            else if (3 == stage) {
                obj.setName(cdExams.getWeiName3());
                obj.setArmyIds(cdExams.getWeiArmies3());
                obj.setCityIds(cdExams.getWeiCities3());
                obj.setGeneralLv(cdExams.getGLv3());
                obj.setGeneralNum(cdExams.getGNum3());
                obj.setOpenNextNum(0);
            }
        }
        else if (2 == forceId) {
            if (stage == 0) {
                obj.setName(cdExams.getShuName0());
                obj.setArmyIds(cdExams.getShuArmies0());
                obj.setCityIds(WorldCityCommon.forcIdManzuCityIdMap.get(forceId).toString());
                obj.setGeneralLv(cdExams.getGLv0());
                obj.setGeneralNum(cdExams.getGNum0());
                obj.setOpenNextNum(cdExams.getOpenKg1());
            }
            else if (1 == stage) {
                obj.setName(cdExams.getShuName1());
                obj.setArmyIds(cdExams.getShuArmies1());
                obj.setCityIds(cdExams.getShuCities1());
                obj.setGeneralLv(cdExams.getGLv1());
                obj.setGeneralNum(cdExams.getGNum1());
                obj.setOpenNextNum(cdExams.getOpenKg2());
            }
            else if (2 == stage) {
                obj.setName(cdExams.getShuName2());
                obj.setArmyIds(cdExams.getShuArmies2());
                obj.setCityIds(cdExams.getShuCities2());
                obj.setGeneralLv(cdExams.getGLv2());
                obj.setGeneralNum(cdExams.getGNum2());
                obj.setOpenNextNum(cdExams.getOpenKg3());
            }
            else if (3 == stage) {
                obj.setName(cdExams.getShuName3());
                obj.setArmyIds(cdExams.getShuArmies3());
                obj.setCityIds(cdExams.getShuCities3());
                obj.setGeneralLv(cdExams.getGLv3());
                obj.setGeneralNum(cdExams.getGNum3());
                obj.setOpenNextNum(0);
            }
        }
        else if (3 == forceId) {
            if (stage == 0) {
                obj.setName(cdExams.getWuName0());
                obj.setArmyIds(cdExams.getWuArmies0());
                obj.setCityIds(WorldCityCommon.forcIdManzuCityIdMap.get(forceId).toString());
                obj.setGeneralLv(cdExams.getGLv0());
                obj.setGeneralNum(cdExams.getGNum0());
                obj.setOpenNextNum(cdExams.getOpenKg1());
            }
            else if (1 == stage) {
                obj.setName(cdExams.getWuName1());
                obj.setArmyIds(cdExams.getWuArmies1());
                obj.setCityIds(cdExams.getWuCities1());
                obj.setGeneralLv(cdExams.getGLv1());
                obj.setGeneralNum(cdExams.getGNum1());
                obj.setOpenNextNum(cdExams.getOpenKg2());
            }
            else if (2 == stage) {
                obj.setName(cdExams.getWuName2());
                obj.setArmyIds(cdExams.getWuArmies2());
                obj.setCityIds(cdExams.getWuCities2());
                obj.setGeneralLv(cdExams.getGLv2());
                obj.setGeneralNum(cdExams.getGNum2());
                obj.setOpenNextNum(cdExams.getOpenKg3());
            }
            else if (3 == stage) {
                obj.setName(cdExams.getWuName3());
                obj.setArmyIds(cdExams.getWuArmies3());
                obj.setCityIds(cdExams.getWuCities3());
                obj.setGeneralLv(cdExams.getGLv3());
                obj.setGeneralNum(cdExams.getGNum3());
                obj.setOpenNextNum(0);
            }
        }
        return obj;
    }
    
    @Override
    public void dealNextNpcBuidler(final Builder builder, final boolean attWin, final IDataGetter dataGetter, final Battle bat, final BattleResult battleResult) {
        builder.dealNextNpc(attWin, dataGetter, bat, battleResult);
    }
    
    @Transactional
    @Override
    public void dealNextNpc(final Builder builder, final boolean attWin, final IDataGetter dataGetter, final Battle bat, final BattleResult battleResult) {
        builder.dealNextNpc(attWin, dataGetter, bat, battleResult);
    }
    
    private void getBattleView(final PlayerDto playerDto, final Battle battle, final JsonDocument doc) {
        final int playerId = playerDto.playerId;
        final Player player = this.dataGetter.getPlayerDao().read(playerId);
        final int battleType = battle.getBattleType();
        final Builder builder = BuilderFactory.getInstance().getBuilder(battleType);
        final int defId = battle.getDefBaseInfo().getId();
        final int battleSide = builder.getBattleSide(this.dataGetter, player, defId);
        doc.createElement("battleId", battle.getBattleId());
        doc.createElement("battleType", battle.getBattleType());
        doc.createElement("side", battleSide);
        doc.createElement("targetId", defId);
        final PlayerOfficeRelative por = this.dataGetter.getPlayerOfficeRelativeDao().read(playerId);
        final int officerId = (por == null || por.getOfficerId() == null) ? 37 : por.getOfficerId();
        if (this.dataGetter.getHallsCache().getTokenList().contains(officerId)) {
            final Halls halls = (Halls)this.dataGetter.getHallsCache().get((Object)officerId);
            final OfficerToken token = this.dataGetter.getOfficerTokenDao().getTokenByForceIdAndOfficerId(officerId, player.getForceId());
            int num = (token == null) ? 0 : token.getNum();
            final OfficerTokenUseInfo info = BattleService.officerUseMap.get(this.orgnizeKey(player.getForceId(), officerId));
            if (info != null) {
                --num;
            }
            doc.createElement("hasOfficerToken", true);
            doc.createElement("tokenType", halls.getOfficialId() - 1);
            doc.createElement("num", num);
        }
        boolean canUseGoldOrder1 = true;
        boolean canUseGoldOrder2 = true;
        if (!battle.attSideDetail.containsKey(playerDto.forceId) && !battle.defSideDetail.containsKey(playerDto.forceId)) {
            canUseGoldOrder1 = false;
        }
        final char[] cs = this.playerAttributeDao.read(playerId).getFunctionId().toCharArray();
        if (cs[59] != '1') {
            canUseGoldOrder2 = false;
        }
        final Chargeitem ci = (Chargeitem)this.chargeitemCache.get((Object)76);
        doc.createElement("canUseGoldOrder", canUseGoldOrder1 && canUseGoldOrder2);
        doc.createElement("goldOrderCost", ci.getCost());
        doc.createElement("goldOrderExp", ci.getParam());
        doc.createElement("isGoldOrderFree", false);
        builder.addViewType(player, battle, doc);
        doc.createElement("terrain", battle.terrain);
        doc.createElement("terrainPic", battle.terrainPic);
        if (battle.getBattleType() >= 18 && battle.getBattleType() <= 20) {
            final JuBenDto juBenDto = JuBenManager.getInstance().getByPid(playerId);
            if (juBenDto != null) {
                doc.createElement("juBenLeftJieBing", juBenDto.maxJieBingCount - juBenDto.jieBingCount);
            }
            else {
                ErrorSceneLog.getInstance().appendErrorMsg("juBenDto is null").appendBattleId(battle.getBattleId()).appendClassName("BattleService").appendMethodName("watchBattle").flush();
            }
        }
        doc.createElement("inBattle", true);
        if (battle.getBattleType() == 3) {
            doc.createElement("cityId", battle.getDefBaseInfo().getId());
        }
        NewBattleManager.getInstance().setPlayerWatchBattle(playerId, battle);
        final PlayerBattleAttribute pba = this.dataGetter.getPlayerBattleAttributeDao().read(playerDto.playerId);
        doc.appendJson(builder.getOtherBatInfo(this.dataGetter, defId, playerId, battleSide, pba));
        doc.appendJson(builder.getBattleTopInfo(this.dataGetter, playerDto, battle));
        doc.createElement("help", battle.canCallHelp(playerId));
        final JuBenDto juBenDto2 = JuBenManager.getInstance().getByPid(playerId);
        final boolean isInThisBattle = battleType >= 18 && battleType <= 20;
        if (juBenDto2 != null && !this.dataGetter.getJuBenService().isInWorldDrama(playerId) && isInThisBattle) {
            doc.createElement("freePhantomCount", juBenDto2.maxJieBingCount - juBenDto2.jieBingCount);
        }
        else {
            doc.createElement("freePhantomCount", pba.getVip3PhantomCount());
        }
        doc.createElement("battleReport", battle.getCurrentBattleInfo(playerId, battleSide, this.dataGetter, 1));
        battle.addInSceneSet(playerId);
        doc.createElement("join", false);
        int autoSt = -1;
        final PlayerInfo pi = battle.getInBattlePlayers().get(playerId);
        if (pi != null) {
            autoSt = pi.getAutoStrategy();
        }
        else {
            final int zdzsTech = this.dataGetter.getTechEffectCache().getTechEffect(playerId, 43);
            if (zdzsTech > 0) {
                autoSt = pba.getAutoStrategy();
            }
            else {
                autoSt = -1;
            }
        }
        doc.createElement("autoSt", autoSt);
        this.dataGetter.getHuiZhanService().getHzInfoInBattle(doc, defId);
    }
    
    @Transactional
    @Override
    public byte[] watchBattle(final PlayerDto playerDto, final String battleId) {
        final Battle battle = NewBattleManager.getInstance().getBattleByBatId(battleId);
        if (battle == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.BATTLE_ENDED);
        }
        battle.isBattleDoing();
        boolean canWatchBat = false;
        for (final Integer side : battle.attSideDetail.keySet()) {
            if (side == playerDto.forceId && battle.attSideDetail.get(side) > 0) {
                canWatchBat = true;
            }
        }
        for (final Integer side : battle.defSideDetail.keySet()) {
            if (side == playerDto.forceId && battle.defSideDetail.get(side) > 0) {
                canWatchBat = true;
            }
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        this.getBattleView(playerDto, battle, doc);
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public void initBarbarainPhantomMaxId() {
        final int maxVid = this.dataGetter.getBarbarainPhantomDao().getMaxVid();
        BattleService.barVid.set(maxVid);
    }
    
    @Override
    public void resetAtZeroClock() {
        final long start = System.currentTimeMillis();
        Label_0082: {
            try {
                this.dataGetter.getAutoBattleService().resetAtZeroClock();
                this.resetAtZeroClockOldPart();
                this.resetOfficeKillToken();
                this.dataGetter.getPhantomService().resetPahntomWorkShopTodayNum();
                WorldService.playerCityOccupyMap.clear();
            }
            catch (Exception e) {
                ErrorSceneLog.getInstance().error("BattleService.resetAtZeroClock catch Exception", e);
                break Label_0082;
            }
            finally {
                this.pushRefreshInfoAtZeroOClock();
            }
            this.pushRefreshInfoAtZeroOClock();
            try {
                this.dataGetter.getPlayerIncenseDao().resetIncenseTimes();
            }
            catch (Exception e) {
                ErrorSceneLog.getInstance().error("BattleService.resetAtZeroClock resetIncenseTimes catch Exception", e);
            }
        }
        try {
            this.dataGetter.getPlayerBlacksmithDao().resetSmithNum();
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("BattleService.resetAtZeroClock resetSmithNum catch Exception", e);
        }
        final long end = System.currentTimeMillis();
        BattleService.timerLog.info(LogUtil.formatTimerLog("battleService", "resetAtZeroClock", end - start));
    }
    
    private void resetAtZeroClockOldPart() {
        try {
            this.dataGetter.getForceInfoDao().resetShouMaiCount(WorldCityCommon.MANZU_SHOUMAI_COUNT_PER_DAY);
            this.dataGetter.getSlaveholderDao().clearSlaveDayNum();
            this.dataGetter.getCityService().clearPlayerEventPerDay();
            this.dataGetter.getPlayerScoreRankDao().clearAll();
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("BattleService.resetAtZeroClockOldPart catch Exception", e);
        }
    }
    
    private void pushRefreshInfoAtZeroOClock() {
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("refresh", true);
        doc.endObject();
        final byte[] send = doc.toByte();
        for (final PlayerDto playerDto : Players.getAllPlayer()) {
            Players.push(playerDto.playerId, PushCommand.PUSH_ZERO_OCLOCK_RESET_REFRESH, send);
        }
    }
    
    @Override
    public void resetOfficeKillToken() {
        try {
            final List<ForceInfo> list = this.dataGetter.getForceInfoDao().getModels();
            for (final ForceInfo forceInfo : list) {
                if (forceInfo.getForceLv() >= 2) {
                    this.dataGetter.getOfficerTokenDao().addKillTokenLimited(forceInfo.getForceId(), 1);
                }
            }
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("BattleService.resetOfficeKillToken catch Exception", e);
        }
    }
    
    @Override
    public CampArmyParam[] getKfwdCampDatas(final int playerId, final String gIds) {
        final Set<Integer> gIdSet = new HashSet<Integer>();
        final List<PlayerGeneralMilitary> pgmList = new ArrayList<PlayerGeneralMilitary>();
        String[] split;
        for (int length = (split = gIds.split("#")).length, j = 0; j < length; ++j) {
            final String s = split[j];
            if (!s.equals("")) {
                gIdSet.add(Integer.parseInt(s));
                final PlayerGeneralMilitary pg = this.dataGetter.getPlayerGeneralMilitaryDao().getMilitary(playerId, Integer.parseInt(s));
                if (pg != null) {
                    pgmList.add(pg);
                }
            }
        }
        final Player player = this.dataGetter.getPlayerDao().read(playerId);
        final Builder builder = BuilderFactory.getInstance().getBuilder(1);
        final Battle bat = new Battle("1");
        final int techYingYong = this.dataGetter.getTechEffectCache().getTechEffect(playerId, 10);
        final int techJianRen = this.dataGetter.getTechEffectCache().getTechEffect(playerId, 13);
        builder.initAttCampForKf(this.dataGetter, player, pgmList, 1, bat);
        final GemAttribute ga = this.getGemAttributeObj(playerId);
        final CampArmyParam[] armyParam = new CampArmyParam[pgmList.size()];
        for (int i = 0; i < pgmList.size(); ++i) {
            armyParam[i] = new CampArmyParam();
            final CampArmy pa = bat.getAttCamp().get(i);
            BeanUtils.copyProperties(pa, armyParam[i]);
            armyParam[i].setTechYinYong(techYingYong);
            armyParam[i].setTechJianRen(techJianRen);
            final KfSpecialGeneral kfsp = new KfSpecialGeneral();
            kfsp.setGeneralType(pa.getSpecialGeneral().generalType);
            kfsp.setParam(pa.getSpecialGeneral().param);
            armyParam[i].setKfspecialGeneral(kfsp);
            armyParam[i].setATT_B(pa.getAttDef_B().ATT_B);
            armyParam[i].setDEF_B(pa.getAttDef_B().DEF_B);
            if (ga != null) {
                armyParam[i].setGemAttribute(ga);
            }
        }
        return armyParam;
    }
    
    private GemAttribute getGemAttributeObj(final int playerId) {
        final Map<Integer, Double> playerGemAttribute = this.dataGetter.getBattleDataCache().getGemAttribute(playerId);
        if (playerGemAttribute == null || playerGemAttribute.isEmpty()) {
            return null;
        }
        final GemAttribute ga = new GemAttribute();
        ga.skillMs = playerGemAttribute.get(1);
        ga.skillBj = playerGemAttribute.get(2);
        ga.skillZfbj = playerGemAttribute.get(3);
        ga.skillZfjb = playerGemAttribute.get(4);
        ga.skillDt = playerGemAttribute.get(5);
        ga.skillDef = playerGemAttribute.get(6);
        ga.skillAtt = playerGemAttribute.get(7);
        return ga;
    }
    
    @Override
    public int trickReduceHpBarbarain(final int cityId, final int count, final int hpReduce) {
        try {
            final List<BarbarainPhantom> list1 = this.dataGetter.getBarbarainPhantomDao().getBarPhantomByLocationId(cityId);
            final List<BarbarainExpeditionArmy> list2 = this.dataGetter.getBarbarainExpeditionArmyDao().getBarEAsByLocationId(cityId);
            int reduceSum = 0;
            final int size1 = list1.size();
            final int size2 = list2.size();
            for (int i = 0; i < count; ++i) {
                if (i > size1 - 1) {
                    if (i > size1 + size2 - 1) {
                        break;
                    }
                    final BarbarainExpeditionArmy bea = list2.get(i - size1);
                    final int reduce = (bea.getHp() >= hpReduce) ? hpReduce : bea.getHp();
                    this.dataGetter.getBarbarainExpeditionArmyDao().updateHpAndTacticVal(bea.getVId(), reduce, bea.getTacticval());
                    reduceSum += reduce;
                }
                else {
                    final BarbarainPhantom bp = list1.get(i);
                    final int reduce = (bp.getHp() >= hpReduce) ? hpReduce : bp.getHp();
                    this.dataGetter.getBarbarainPhantomDao().updateHpTacticVal(bp.getVId(), reduce, bp.getTacticval());
                    reduceSum += reduce;
                }
            }
            return reduceSum;
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("BattleService.trickReduceHpBarbarain catch Exception", e);
            return 0;
        }
    }
    
    @Override
    public int trickReduceHpYellowTurbans(final int cityId, int count, final int hpReduce) {
        try {
            final List<YellowTurbans> list = this.dataGetter.getYellowTurbansDao().getYellowTurbansByCityId(cityId);
            int reduceSum = 0;
            final int size = list.size();
            if (count > size) {
                count = size;
            }
            for (int i = 0; i < count; ++i) {
                final YellowTurbans yellowTurbans = list.get(i);
                final int curHp = yellowTurbans.getHp();
                final int reduce = (curHp >= hpReduce) ? hpReduce : curHp;
                if (curHp - reduce > 0) {
                    this.dataGetter.getYellowTurbansDao().updateHpAndTacticVal(yellowTurbans.getVId(), curHp - reduce, yellowTurbans.getTacticval());
                }
                else {
                    this.dataGetter.getYellowTurbansDao().deleteById(yellowTurbans.getVId());
                }
                reduceSum += reduce;
            }
            return reduceSum;
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("BattleService.trickReduceHpBarbarain catch Exception", e);
            return 0;
        }
    }
    
    @Override
    public byte[] joinBattle(final PlayerDto playerDto, final String battleId) {
        try {
            if (this.dataGetter.getAutoBattleService().InAutoBattleMode(playerDto.playerId)) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.AUTO_BATTLE_CANNOT_OPERATE);
            }
            final Integer battleType = Integer.parseInt(battleId.split("_")[0]);
            if (battleType != 4) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.PLUG_IS_SHAMEFUL);
            }
            final Builder builder = BuilderFactory.getInstance().getBuilder(battleType);
            if (builder == null) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.PLUG_IS_SHAMEFUL);
            }
            final Battle battle = NewBattleManager.getInstance().getBattleByBatId(battleId);
            if (battle == null) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.BATTLE_END_INFO);
            }
            final int defId = battle.getDefBaseInfo().getId();
            int lv = 0;
            boolean isNpc = true;
            if (!BattleConstant.officerBatMap.contains(defId)) {
                final OfficerBuildingInfo obi = this.dataGetter.getOfficerBuildingInfoDao().getByBuildingId(playerDto.forceId, defId);
                if (obi != null) {
                    final Player p = this.dataGetter.getPlayerDao().read(obi.getPlayerId());
                    lv = p.getPlayerLv() - playerDto.playerLv;
                    isNpc = false;
                }
            }
            if (isNpc) {
                final ChiefNpc chiefNpc = this.dataGetter.getHallsCache().getChiefNpc(defId, 1);
                final Army army = (Army)this.dataGetter.getArmyCache().get((Object)chiefNpc.getCheif());
                lv = army.getGeneralLv() - playerDto.playerLv;
            }
            final int limitLv = (int)(Object)((C)this.dataGetter.getcCache().get((Object)"Official.Attack.LvLimit")).getValue();
            if (lv > limitLv) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.BUILDING_POSITION_ATT_LV_LIMIT);
            }
            final int playerId = playerDto.playerId;
            final Player player = this.dataGetter.getPlayerDao().read(playerId);
            final Tuple<List<PlayerGeneralMilitary>, String> gTuple = builder.chooseAllGeneral(this.dataGetter, player, battle.getDefBaseInfo().getId(), 1);
            if (gTuple.left == null) {
                return JsonBuilder.getJson(State.FAIL, gTuple.right);
            }
            final Tuple<Boolean, String> tuple1 = builder.canJoinBattle(player, gTuple.left, this.dataGetter, battle);
            if (!(boolean)tuple1.left) {
                return JsonBuilder.getJson(State.FAIL, tuple1.right);
            }
            final boolean succ = battle.join(player, gTuple.left, this.dataGetter);
            if (succ) {
                return JsonBuilder.getJson(State.SUCCESS, "");
            }
            return JsonBuilder.getJson(State.FAIL, LocalMessages.BATTLE_JOIN_FAIL);
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().trace("BattleService.joinBattle catch Exception", e);
            return JsonBuilder.getJson(State.FAIL, LocalMessages.INTERFACE_BACKEND_CATCH_EXCEPTION);
        }
    }
    
    @Override
    public boolean fireManWangLing(final int fromForceId, final int toForceId, final int cityId, final Long expireTime) {
        if (this.dataGetter.getWorldCityCache().get((Object)cityId) == null) {
            ErrorSceneLog.getInstance().appendErrorMsg("cityId error").append("fromForceId", fromForceId).append("toForceId", toForceId).append("cityId", cityId).append("expireTime", (expireTime == null) ? "null" : expireTime).appendClassName("BattleService").appendMethodName("fireManWangLing").flush();
            return false;
        }
        if (expireTime == null || expireTime <= System.currentTimeMillis()) {
            ErrorSceneLog.getInstance().appendErrorMsg("expireTime error").append("fromForceId", fromForceId).append("toForceId", toForceId).append("expireTime", (expireTime == null) ? "null" : expireTime).appendClassName("BattleService").appendMethodName("fireManWangLing").flush();
            return false;
        }
        return this.fireManWangLing2(fromForceId, toForceId, cityId, expireTime);
    }
    
    @Override
    public void fireManWangLing(final int fromForceId, final int toForceId) {
        this.fireManWangLing2(fromForceId, toForceId, 0, 0L);
    }
    
    private boolean fireManWangLing2(final int fromForceId, final int toForceId, final int cityId, final long expireTime) {
        final long start = System.currentTimeMillis();
        try {
            BattleService.timerLog.info(LogUtil.formatThreadLog("BattleService", "fireManWangLing2", 0, 0L, "fromForceId:" + fromForceId + ",toForceId:" + toForceId + ",cityId:" + cityId + ",expireTime:" + expireTime));
            if (fromForceId == toForceId) {
                ErrorSceneLog.getInstance().appendErrorMsg("BattleService.fireManWangLing2 fromForceId == toForceId").append("fromForceId", fromForceId).append("toForceId", toForceId).flush();
                return false;
            }
            if (WorldCityCommon.playerManZuForceMap.get(fromForceId) == null) {
                ErrorSceneLog.getInstance().appendErrorMsg("BattleService.fireManWangLing2 fromForceId is invalid").append("fromForceId", fromForceId).append("toForceId", toForceId).flush();
                return false;
            }
            if (WorldCityCommon.playerManZuForceMap.get(toForceId) == null) {
                ErrorSceneLog.getInstance().appendErrorMsg("BattleService.fireManWangLing2 toForceId is invalid").append("fromForceId", fromForceId).append("toForceId", toForceId).flush();
                return false;
            }
            final int manZuForceId = WorldCityCommon.playerManZuForceMap.get(fromForceId);
            int targetCityId = WorldCityCommon.manZuForceIdManWangLingTargetMap.get(manZuForceId);
            if (cityId != 0) {
                targetCityId = cityId;
            }
            final City targetCity = this.dataGetter.getCityDao().read(targetCityId);
            if (targetCity.getForceId() != fromForceId) {
                ErrorSceneLog.getInstance().appendErrorMsg("BattleService.fireManWangLing2 targetCity is not own to fromForceId, ignore.").append("fromForceId", fromForceId).append("targetCityId", targetCityId).append("targetCity.getForceId()", targetCity.getForceId()).flush();
                return false;
            }
            final ManWangLingObj manWangLingObj = new ManWangLingObj();
            manWangLingObj.fromForceId = fromForceId;
            manWangLingObj.manZuForceId = manZuForceId;
            manWangLingObj.toForceId = toForceId;
            manWangLingObj.targetCityId = targetCityId;
            if (expireTime != 0L && expireTime > System.currentTimeMillis()) {
                manWangLingObj.expireTime = expireTime;
                manWangLingObj.type = 2;
            }
            else {
                manWangLingObj.expireTime = System.currentTimeMillis() + 1800000L;
                manWangLingObj.type = 1;
            }
            ManWangLingManager.getInstance().addManWangLingObj(manWangLingObj.toForceId, manWangLingObj.type, manWangLingObj);
            final StringBuilder param = new StringBuilder();
            param.append(manWangLingObj.fromForceId).append("#").append(manWangLingObj.toForceId).append("#").append(manWangLingObj.type);
            this.dataGetter.getJobService().addJob("battleService", "removeManWangLing", param.toString(), manWangLingObj.expireTime, false);
            this.dataGetter.getTimerBattleService().addManZuBeforeManWangLing(manWangLingObj.fromForceId, manWangLingObj.targetCityId);
            ManWangLingManager.getInstance().pushManWangLingMsg(manWangLingObj.toForceId);
            BattleService.timerLog.info(LogUtil.formatThreadLog("BattleService", "fireManWangLing2", 2, System.currentTimeMillis() - start, "fromForceId:" + fromForceId + ",toForceId:" + toForceId + ",cityId:" + cityId + ",expireTime:" + expireTime));
            return true;
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("BattleService.fireManWangLing2 catch Exception", e);
            return false;
        }
    }
    
    @Override
    public byte[] getReplyMWLInfo(final PlayerDto playerDto) {
        final ManWangLingObj manWangLingObj = ManWangLingManager.getInstance().getCanReplyMWLByForceId(playerDto.playerId, playerDto.forceId);
        if (manWangLingObj == null) {
            ErrorSceneLog.getInstance().appendErrorMsg("BattleService.getReplyMWLInfo manWangLingObj is null").append("playerDto.forceId", playerDto.forceId).appendPlayerName(playerDto.playerName).appendPlayerId(playerDto.playerId).flush();
            return JsonBuilder.getJson(State.FAIL, String.valueOf(LocalMessages.PLUG_IS_SHAMEFUL) + LocalMessages.MAN_WANG_LING_N0_OBJ);
        }
        final List<PlayerGeneralMilitary> pgmList = this.dataGetter.getPlayerGeneralMilitaryDao().getMilitaryList(playerDto.playerId);
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("countDown", manWangLingObj.expireTime - System.currentTimeMillis());
        doc.createElement("type", manWangLingObj.type);
        doc.startArray("gArray");
        for (final PlayerGeneralMilitary pgm : pgmList) {
            doc.startObject();
            final General general = (General)this.dataGetter.getGeneralCache().get((Object)pgm.getGeneralId());
            doc.createElement("gId", pgm.getGeneralId());
            doc.createElement("gName", general.getName());
            int state = pgm.getState();
            final boolean joinTeam = TeamManager.getInstance().isJoinTeam(pgm.getPlayerId(), pgm.getGeneralId());
            if (joinTeam) {
                state = 100;
            }
            else {
                final GeneralMoveDto gmd = CityService.getGeneralMoveDto(pgm.getPlayerId(), pgm.getGeneralId());
                if (gmd != null) {
                    if (CDUtil.isInCD(gmd.nextMoveTime, new Date())) {
                        state = 6;
                    }
                    else if (gmd.cityState == 22 || gmd.cityState == 23) {
                        state = 22;
                    }
                }
                else {
                    final int armyHp = pgm.getForces();
                    final int maxHp = this.dataGetter.getBattleDataCache().getMaxHp(pgm);
                    if (armyHp * 1.0 / maxHp <= 0.05) {
                        state = -1;
                    }
                }
            }
            doc.createElement("state", state);
            doc.createElement("pic", general.getPic());
            doc.createElement("quality", general.getQuality());
            doc.endObject();
        }
        doc.endArray();
        final Halls halls = (Halls)this.dataGetter.getHallsCache().get((Object)1);
        final BattleDrop battleDrop = BattleDropFactory.getInstance().getBattleDrop(halls.getOrderReward());
        if (battleDrop != null) {
            doc.createElement("rewardType", battleDrop.type);
            doc.createElement("rewardNum", battleDrop.num);
        }
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public byte[] replyManWangLing(final PlayerDto playerDto, final String gIds) {
        if (this.dataGetter.getAutoBattleService().InAutoBattleMode(playerDto.playerId)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.AUTO_BATTLE_CANNOT_OPERATE);
        }
        if (gIds == null || gIds.trim().isEmpty()) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.ASSEMBLE_NO_PGM_CHOOSED);
        }
        if (this.dataGetter.getAutoBattleService().InAutoBattleMode(playerDto.playerId)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.AUTO_BATTLE_CANNOT_OPERATE);
        }
        final String[] generalIds = gIds.split("#");
        final Set<Integer> gIdSet = new HashSet<Integer>();
        Integer gId = 0;
        String[] array;
        for (int length = (array = generalIds).length, i = 0; i < length; ++i) {
            final String str = array[i];
            try {
                gId = Integer.valueOf(str);
            }
            catch (Exception e) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.PLUG_IS_SHAMEFUL);
            }
            if (gId > 0) {
                gIdSet.add(Integer.valueOf(str));
            }
        }
        final PlayerAttribute pa = this.dataGetter.getPlayerAttributeDao().read(playerDto.playerId);
        final char[] cs = pa.getFunctionId().toCharArray();
        if (cs[10] != '1') {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.FUNCTION_NOT_OPEN);
        }
        final ManWangLingObj manWangLingObj = ManWangLingManager.getInstance().getCanReplyMWLByForceId(playerDto.playerId, playerDto.forceId);
        if (manWangLingObj == null) {
            ErrorSceneLog.getInstance().appendErrorMsg("BattleService.replyManWangLing manWangLingObj is null").append("playerDto.forceId", playerDto.forceId).appendPlayerName(playerDto.playerName).appendPlayerId(playerDto.playerId).flush();
            return JsonBuilder.getJson(State.FAIL, String.valueOf(LocalMessages.PLUG_IS_SHAMEFUL) + LocalMessages.MAN_WANG_LING_N0_OBJ);
        }
        if (manWangLingObj.playerSet.contains(playerDto.playerId)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.MAN_WANG_LING_ALREADY_ECHOED);
        }
        final City city = this.dataGetter.getCityDao().read(manWangLingObj.targetCityId);
        if (city.getForceId() != manWangLingObj.fromForceId) {
            final StringBuilder param = new StringBuilder();
            param.append(manWangLingObj.fromForceId).append("#").append(manWangLingObj.toForceId).append("#").append(manWangLingObj.type);
            this.removeManWangLing(param.toString());
            return JsonBuilder.getJson(State.FAIL, LocalMessages.MAN_WANG_LING_CITY_WINNED);
        }
        final PlayerWorld pw = this.dataGetter.getPlayerWorldDao().read(playerDto.playerId);
        final Set<Integer> attedSet = new HashSet<Integer>();
        if (pw.getAttedId() != null) {
            final String[] ids = pw.getAttedId().split(",");
            int key = 0;
            String[] array2;
            for (int length2 = (array2 = ids).length, j = 0; j < length2; ++j) {
                final String str2 = array2[j];
                key = Integer.valueOf(str2);
                attedSet.add(key);
            }
        }
        if (!attedSet.contains(manWangLingObj.targetCityId)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.CITY_IN_MIST);
        }
        final List<PlayerGeneralMilitary> pgmList = this.dataGetter.getPlayerGeneralMilitaryDao().getMilitaryList(playerDto.playerId);
        if (pgmList.size() == 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.THIS_PLAYER_HAS_NO_PGM);
        }
        final List<PlayerGeneralMilitary> pgmListAvalable = new LinkedList<PlayerGeneralMilitary>();
        for (final PlayerGeneralMilitary pgm : pgmList) {
            if (this.worldFarmService.isInFarmForbiddenOperation(pgm, false)) {
                continue;
            }
            if (!gIdSet.contains(pgm.getGeneralId()) || !this.isPgmAvilable(pgm)) {
                continue;
            }
            pgmListAvalable.add(pgm);
        }
        if (pgmListAvalable.size() == 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.ALL_PGM_IS_BUSY_OR_LOW_HP_CANNOT_ASSEMBLE);
        }
        final Halls halls = (Halls)this.dataGetter.getHallsCache().get((Object)1);
        final BattleDrop battleDrop = BattleDropFactory.getInstance().getBattleDrop(halls.getOrderReward());
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        int succNum = 0;
        for (final Integer generalId : gIdSet) {
            final List<Integer> gIdList = new ArrayList<Integer>();
            gIdList.add(generalId);
            final BattleStartResult battleStartResult = this.battleStart2(playerDto.playerId, 3, manWangLingObj.targetCityId, 0, gIdList);
            if (battleStartResult.succ) {
                ++succNum;
            }
        }
        if (succNum > 0) {
            ManWangLingManager.getInstance().resProtectManWang(playerDto.playerId, manWangLingObj);
            if (manWangLingObj.playerSet.size() < 1) {
                final String msg = MessageFormatter.format(LocalMessages.TRY_SEND_PROTECT, new Object[] { manWangLingObj.targetCityId, ((WorldCity)this.dataGetter.getWorldCityCache().get((Object)manWangLingObj.targetCityId)).getName() });
                this.chatService.sendSystemChat("COUNTRY", 0, manWangLingObj.fromForceId, msg, null);
            }
            manWangLingObj.playerSet.add(playerDto.playerId);
            if (battleDrop != null) {
                battleDrop.num *= succNum;
                this.dataGetter.getBattleDropService().saveBattleDrop(playerDto.playerId, battleDrop, "\u6218\u6597\u83b7\u5f97");
                doc.createElement("rewardType", battleDrop.type);
                doc.createElement("totalNum", battleDrop.num);
            }
            ManWangLingManager.getInstance().caculateOnePlayer(playerDto.playerId, playerDto.forceId, doc);
            doc.endObject();
            return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
        }
        return JsonBuilder.getJson(State.FAIL, LocalMessages.JOIN_BATTLE_FAIL);
    }
    
    @Override
    public void removeManWangLing(final String param) {
        final long start = System.currentTimeMillis();
        try {
            BattleService.timerLog.info(LogUtil.formatThreadLog("BattleService", "removeManWangLing", 0, 0L, "param:" + param));
            if (param == null || param.trim().isEmpty()) {
                ErrorSceneLog.getInstance().appendErrorMsg("BattleService.removeManWangLing param is invalid").append("param", param).flush();
                return;
            }
            final String[] params = param.split("#");
            if (params.length != 3) {
                ErrorSceneLog.getInstance().appendErrorMsg("BattleService.removeManWangLing param is invalid").append("param", param).flush();
                return;
            }
            final int fromForceId = Integer.parseInt(params[0]);
            final int toForceId = Integer.parseInt(params[1]);
            final int type = Integer.parseInt(params[2]);
            final ManWangLingObj manWangLingObj = ManWangLingManager.getInstance().removeManWangLingObj(toForceId, type);
            if (manWangLingObj == null) {
                ErrorSceneLog.getInstance().appendErrorMsg("BattleService.removeManWangLing manWangLingObj is null").append("fromForceId", fromForceId).append("toForceId", toForceId).flush();
                return;
            }
            BattleService.timerLog.info(LogUtil.formatThreadLog("BattleService", "removeManWangLing", 2, System.currentTimeMillis() - start, "fromForceId:" + fromForceId + ",toForceId:" + toForceId));
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("BattleService.removeManWangLing catch Exception", e);
        }
    }
    
    @Override
    public void removeManWangLingTryAfterCityConquered(final String param) {
        final long start = System.currentTimeMillis();
        try {
            BattleService.timerLog.info(LogUtil.formatThreadLog("BattleService", "removeManWangLingTryAfterCityConquered", 0, 0L, "param:" + param));
            if (param == null || param.trim().isEmpty()) {
                ErrorSceneLog.getInstance().appendErrorMsg("BattleService.removeManWangLingTryAfterCityConquered param is invalid").append("param", param).flush();
                return;
            }
            final String[] params = param.split("#");
            if (params.length != 2) {
                ErrorSceneLog.getInstance().appendErrorMsg("BattleService.removeManWangLingTryAfterCityConquered param is invalid").append("param", param).flush();
                return;
            }
            final int cityId = Integer.parseInt(params[0]);
            final int oldForceId = Integer.parseInt(params[1]);
            ManWangLingManager.getInstance().removeManWangLingTryAfterCityConquered(cityId, oldForceId);
            BattleService.timerLog.info(LogUtil.formatThreadLog("BattleService", "removeManWangLingTryAfterCityConquered", 2, System.currentTimeMillis() - start, "param:" + param));
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("BattleService.removeManWangLingTryAfterCityConquered catch Exception", e);
        }
    }
    
    @Transactional
    @Override
    public byte[] useKillToken(final PlayerDto playerDto, final int cityId) {
        if (WorldCityCommon.mainCityNationIdMap.get(cityId) != null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.CAPITAL_CANNOT_USE);
        }
        if (WorldFarmCache.forceCityIdMap.values().contains(cityId)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.FARM_CANNOT_USE);
        }
        if (WorldCityCommon.barbarainCitySet.contains(cityId)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.MANZU_CAPITAL_CANNOT_USE);
        }
        final WorldCity worldCity = (WorldCity)this.dataGetter.getWorldCityCache().get((Object)cityId);
        if (worldCity == null) {
            return JsonBuilder.getJson(State.FAIL, String.valueOf(LocalMessages.PLUG_IS_SHAMEFUL) + "cityId:" + cityId);
        }
        final PlayerOfficeRelative por = this.dataGetter.getPlayerOfficeRelativeDao().read(playerDto.playerId);
        final int officerId = (por == null || por.getOfficerId() == null) ? 0 : por.getOfficerId();
        final OfficerToken token = this.officerTokenDao.getTokenByForceIdAndOfficerId(officerId, playerDto.forceId);
        if (token == null) {
            return JsonBuilder.getJson(State.FAIL, String.valueOf(LocalMessages.PLUG_IS_SHAMEFUL) + "officerId:" + officerId);
        }
        if (token.getKillTokenNum() <= 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.PLUG_IS_SHAMEFUL);
        }
        this.dataGetter.getOfficerTokenDao().decreaseKillTokenLimited(playerDto.forceId, officerId);
        final City city = this.dataGetter.getCityDao().read(cityId);
        if (city == null) {
            ErrorSceneLog.getInstance().appendErrorMsg("city is null").appendPlayerId(playerDto.playerId).appendPlayerName(playerDto.playerName).append("cityId", cityId).appendClassName("BattleService").appendMethodName("useKillToken").flush();
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10035);
        }
        final Long expireTime = this.dataGetter.getCityService().updateShaDiLingInfoInThisCity(cityId, playerDto.forceId);
        if (expireTime != null && expireTime > System.currentTimeMillis()) {
            this.dataGetter.getJobService().addJob("cityService", "removeExpiredShaDiLingInfoInThisCity", new StringBuilder().append(cityId).toString(), expireTime, true);
        }
        final Battle battle = NewBattleManager.getInstance().getBattleByDefId(3, cityId);
        if (battle != null) {
            battle.useShaDiLing(playerDto.forceId, 1800000L);
        }
        final Halls halls = (Halls)this.dataGetter.getHallsCache().get((Object)officerId);
        String guanZhiMing = null;
        if (halls != null) {
            if (halls.getOfficialId() == 1) {
                guanZhiMing = LocalMessages.KING;
            }
            else {
                guanZhiMing = halls.getNameList();
            }
            final String msg = MessageFormatter.format(LocalMessages.SHA_DI_LING_USE_BO_BAO_MSG_FORMAT, new Object[] { guanZhiMing, playerDto.playerName, ColorUtil.getSpecialColorMsg(worldCity.getName()) });
            this.dataGetter.getChatService().sendSystemChat("GLOBAL", 0, playerDto.forceId, msg, null);
        }
        return JsonBuilder.getJson(State.SUCCESS, LocalMessages.SHA_DI_LING_USE_SUCC);
    }
    
    @Transactional
    @Override
    public byte[] useGoldOrder(final PlayerDto playerDto, final String battleId) {
        try {
            final char[] cs = this.playerAttributeDao.read(playerDto.playerId).getFunctionId().toCharArray();
            if (cs[24] != '1') {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.GOLDORDER_WORLD_NOT_OPEN);
            }
            if (cs[59] != '1') {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.GOLDORDER_IS_NOT_OPEN);
            }
            final Battle battle = NewBattleManager.getInstance().getBattleByBatId(battleId);
            if (battle == null) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.BATTLE_NOT_EXIST);
            }
            if (!battle.isBattleDoing()) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.GOLDORDER_BATTLE_IS_OVER);
            }
            if (3 != battle.getBattleType() && 14 != battle.getBattleType()) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.GOLDORDER_WRONG_BATTLE_TYPE);
            }
            final JuBenDto juBenDto = JuBenManager.getInstance().getByPid(playerDto.playerId);
            if (juBenDto != null) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.GOLDORDER_CAN_NOT_REPLY_WHILE_IN_JUBEN);
            }
            final String batId = battle.getBattleId();
            final int forceId = playerDto.forceId;
            final String cityId = batId.split("_")[2];
            final String key = String.valueOf(forceId) + "_" + cityId;
            final Chargeitem ci = (Chargeitem)this.chargeitemCache.get((Object)76);
            final Player player = this.playerDao.read(playerDto.playerId);
            final boolean hzFlag = this.dataGetter.getHuiZhanService().isHuiZhanInStatePreparation(Integer.parseInt(cityId));
            if (hzFlag) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.GOLDORDER_CAN_NOT_USE_IN_HUIZHAN);
            }
            if (!this.canAttackInMist(playerDto.playerId, Integer.parseInt(cityId))) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.GOLDORDER_CITY_IN_MIST);
            }
            if (!battle.attSideDetail.containsKey(forceId) && !battle.defSideDetail.containsKey(forceId)) {
                return JsonBuilder.getJson(State.FAIL, MessageFormatter.format(LocalMessages.GOLDORDER_HAS_NO_YOUR_ARMY, new Object[] { WorldCityCommon.nationIdNameMap.get(forceId) }));
            }
            final Tuple<Boolean, byte[]> res = this.addGoldOrder(playerDto, batId, key);
            if (!(boolean)res.left) {
                return res.right;
            }
            this.jobService.addJob("battleService", "deleteGoldOrder", key, System.currentTimeMillis() + 300000L, false);
            BattleService.errorLog.error("Timer: deleteGoldOrder has been set up successfully ....#key:" + key);
            final double addExp = ci.getParam();
            this.dataGetter.getPlayerService().updateExpAndPlayerLevel(playerDto.playerId, (int)addExp, "\u53d1\u52a8\u5f81\u53ec\u4ee4\u4e3b\u5c06\u5956\u52b1\u7ecf\u9a8c");
            this.pushGoldOrderMsg(forceId);
            final WorldCity worldCity = (WorldCity)this.dataGetter.getWorldCityCache().get((Object)Integer.parseInt(cityId));
            final String cityName = worldCity.getName();
            final int officerId = this.playerOfficeRelativeDao.getOfficerId(playerDto.playerId);
            final Halls halls = (Halls)this.dataGetter.getHallsCache().get((Object)officerId);
            final String playerName = this.dataGetter.getPlayerDao().getPlayerName(playerDto.playerId);
            final String msg = MessageFormatter.format(LocalMessages.GOLDORDER_BROADCAST_MSG2, new Object[] { halls.getNameList(), playerName, ColorUtil.getSpecialColorMsg(cityName) });
            this.chatService.sendSystemChat("COUNTRY", 0, playerDto.forceId, msg, null);
            final JsonDocument doc = new JsonDocument();
            doc.startObject();
            doc.createElement("goldOrderExp", (int)addExp);
            doc.endObject();
            return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
        }
        catch (Exception e) {
            BattleService.errorLog.error("#className:battleService#methodName:useGoldOrder", e);
            return JsonBuilder.getJson(State.FAIL, LocalMessages.GOLDORDER_USE_EXCEPTION);
        }
    }
    
    private synchronized Tuple<Boolean, byte[]> addGoldOrder(final PlayerDto playerDto, final String batId, final String key) {
        final Tuple<Boolean, byte[]> res = new Tuple(true, "".getBytes());
        try {
            final Player player = this.playerDao.read(playerDto.playerId);
            if (BattleService.goldOrderUseMap.containsKey(key)) {
                res.left = false;
                final GoldOrder goldOrder = BattleService.goldOrderUseMap.get(key);
                if (goldOrder.getPlayerId() == playerDto.playerId) {
                    res.right = JsonBuilder.getJson(State.FAIL, LocalMessages.GOLDORDER_IS_IN_CD);
                    return res;
                }
                res.right = JsonBuilder.getJson(State.FAIL, LocalMessages.GOLDORDER_IS_BEING_USED_BY_OTHERS);
                return res;
            }
            else {
                final Chargeitem ci = (Chargeitem)this.chargeitemCache.get((Object)76);
                final int cityId = Integer.parseInt(key.split("_")[1]);
                if (!this.playerDao.consumeGold(player, ci)) {
                    res.left = false;
                    res.right = JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10003);
                    return res;
                }
                final GoldOrder goldOrder2 = new GoldOrder();
                goldOrder2.setForceId(playerDto.forceId);
                goldOrder2.setCityId(cityId);
                goldOrder2.setNum(0);
                goldOrder2.setBattleId(batId);
                goldOrder2.setStartTime(new Date());
                goldOrder2.setPlayerId(playerDto.playerId);
                BattleService.goldOrderUseMap.put(key, goldOrder2);
                this.goldOrderDao.create(goldOrder2);
                return res;
            }
        }
        catch (Exception e) {
            BattleService.errorLog.error("#className:BattleService#methodName:addGoldOrder", e);
            res.left = false;
            return res;
        }
    }
    
    public boolean hasFreeGoldOrder(final int playerId) {
        final PlayerBattleAttribute pba = this.dataGetter.getPlayerBattleAttributeDao().read(playerId);
        boolean isFree = false;
        if (pba != null && pba.getTeamTimes() > 0) {
            isFree = true;
        }
        return isFree;
    }
    
    @Override
    public void pushGoldOrderMsg(final int forceId) {
        final Collection<PlayerDto> onlinePlayerList = Players.getAllPlayer();
        for (final PlayerDto dto : onlinePlayerList) {
            if (dto == null) {
                continue;
            }
            if (dto.forceId != forceId) {
                continue;
            }
            if (dto.cs[24] != '1') {
                continue;
            }
            final Tuple<Boolean, Integer> result = this.getGordOrderNum(dto.playerId);
            final boolean hasGoldOrder = result.left;
            final int num = result.right;
            final JsonDocument doc = new JsonDocument();
            doc.startObject();
            doc.createElement("hasGoldOrder", hasGoldOrder);
            doc.createElement("goldOrderNum", num);
            doc.endObject();
            Players.push(dto.playerId, PushCommand.PUSH_UPDATE, doc.toByte());
        }
    }
    
    public void pushGoldOrderMsgForSinglePlayer(final int playerId) {
        final Tuple<Boolean, Integer> result = this.getGordOrderNum(playerId);
        final boolean hasGoldOrder = result.left;
        final int num = result.right;
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("hasGoldOrder", hasGoldOrder);
        doc.createElement("goldOrderNum", num);
        doc.endObject();
        Players.push(playerId, PushCommand.PUSH_UPDATE, doc.toByte());
    }
    
    @Override
    public void deleteGoldOrder(final String key) {
        BattleService.errorLog.error("Timer: deleteGoldOrder starts...#key:" + key);
        try {
            if (StringUtils.isBlank(key)) {
                return;
            }
            final int forceId = Integer.parseInt(key.split("_")[0]);
            final int cityId = Integer.parseInt(key.split("_")[1]);
            GoldOrder goldOrder = null;
            if (!BattleService.goldOrderUseMap.containsKey(key)) {
                BattleService.errorLog.error("Timer: deleteGoldOrder doing...#Reason:the map does not cantain key:" + key);
                return;
            }
            goldOrder = BattleService.goldOrderUseMap.get(key);
            BattleService.goldOrderUseMap.remove(key);
            this.goldOrderDao.deleteByForceIdAndCityId(forceId, cityId);
            if (goldOrder == null) {
                BattleService.errorLog.error("Timer: deleteGoldOrder doing...#Reason:goldOrder is null!#key" + key);
                return;
            }
            final int playerId = goldOrder.getPlayerId();
            final String msg = MessageFormatter.format(LocalMessages.GOLDORDER_BROADCAST_MSG, new Object[] { goldOrder.getNum() });
            this.chatService.sendSystemChat("SYS2ONE", playerId, 0, msg, null);
            this.pushGoldOrderMsg(forceId);
            BattleService.errorLog.error("Timer: deleteGoldOrder ends...#key:" + key);
        }
        catch (Exception e) {
            BattleService.errorLog.error("className:BattleService#methodName:deleteGoldOrder", e);
        }
    }
    
    @Override
    public void deleteAllGoldOrderInBattle(final int cityId) {
        BattleService.errorLog.error("deleteAllGoldOrderInBattle starts...#cityId:" + cityId);
        try {
            for (final String tempKey : BattleService.goldOrderUseMap.keySet()) {
                final int cityIdInKey = Integer.parseInt(tempKey.split("_")[1]);
                if (cityId == cityIdInKey) {
                    BattleService.goldOrderUseMap.remove(tempKey);
                }
            }
            this.goldOrderDao.deleteByCityId(cityId);
            this.pushGoldOrderMsg(1);
            this.pushGoldOrderMsg(2);
            this.pushGoldOrderMsg(3);
        }
        catch (Exception e) {
            BattleService.errorLog.error("className:BattleService#methodName:deleteAllGoldOrderInBattle", e);
        }
    }
    
    @Override
    public byte[] getGoldOrderInfo(final PlayerDto playerDto) {
        final List<GoldOrder> goldOrderList = new ArrayList<GoldOrder>();
        for (final String tempKey : BattleService.goldOrderUseMap.keySet()) {
            final int forceIdInKey = Integer.parseInt(tempKey.split("_")[0]);
            if (playerDto.forceId == forceIdInKey) {
                goldOrderList.add(BattleService.goldOrderUseMap.get(tempKey));
            }
        }
        if (goldOrderList.isEmpty()) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.GOLDORDER_IS_OVER);
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        this.dataGetter.getGeneralService().getGeneralInfoForGoldOrder(playerDto.playerId, doc);
        doc.startArray("goldOrderList");
        for (final GoldOrder goldOrder : goldOrderList) {
            if (goldOrder.getPlayerIdList().contains(playerDto.playerId)) {
                continue;
            }
            final int forceId = goldOrder.getForceId();
            final int playerId = goldOrder.getPlayerId();
            final int cityId = goldOrder.getCityId();
            final WorldCity worldCity = (WorldCity)this.dataGetter.getWorldCityCache().get((Object)cityId);
            final String cityName = worldCity.getName();
            final int officerId = this.playerOfficeRelativeDao.getOfficerId(playerId);
            final Halls halls = (Halls)this.dataGetter.getHallsCache().get((Object)officerId);
            final String playerName = this.dataGetter.getPlayerDao().getPlayerName(playerId);
            doc.startObject();
            doc.createElement("playerName", playerName);
            doc.createElement("cityName", cityName);
            doc.createElement("forceId", forceId);
            doc.createElement("countryName", WebUtil.getForceName(forceId));
            doc.createElement("officerName", halls.getNameList());
            final long time = goldOrder.getStartTime().getTime() + 300000L - System.currentTimeMillis();
            doc.createElement("time", time);
            doc.createElement("battleId", goldOrder.getBattleId());
            doc.createElement("cityId", goldOrder.getCityId());
            doc.endObject();
        }
        doc.endArray();
        final int foodReward = (int)(Object)((C)this.cCache.get((Object)"World.GoldOrd.Food")).getValue();
        doc.createElement("foodReward", foodReward);
        final double fightCoe = ((C)this.cCache.get((Object)"World.GoldOrd.LegionE")).getValue();
        doc.createElement("fightReward", fightCoe * 100.0);
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public byte[] replyGoldOrder(final PlayerDto playerDto, final Battle battle, final int cityId, final String gIds) {
        try {
            final int playerId = playerDto.playerId;
            final int forceId = playerDto.forceId;
            final String key = String.valueOf(forceId) + "_" + cityId;
            final char[] cs = this.playerAttributeDao.read(playerDto.playerId).getFunctionId().toCharArray();
            if (cs[24] != '1') {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.GOLDORDER_WORLD_NOT_OPEN);
            }
            if (!this.canAttackInMist(playerId, cityId)) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.GOLDORDER_CITY_IN_MIST);
            }
            if (!BattleService.goldOrderUseMap.containsKey(key)) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.GOLDORDER_IS_OVER);
            }
            if (this.dataGetter.getAutoBattleService().InAutoBattleMode(playerDto.playerId)) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.AUTO_BATTLE_CANNOT_OPERATE);
            }
            final GoldOrder goldOrder = BattleService.goldOrderUseMap.get(key);
            if (goldOrder == null) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.GOLDORDER_IS_OVER);
            }
            final JuBenDto juBenDto = JuBenManager.getInstance().getByPid(playerId);
            if (juBenDto != null) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.GOLDORDER_CAN_NOT_REPLY_WHILE_IN_JUBEN);
            }
            if (3 != battle.getBattleType() && 14 != battle.getBattleType()) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.GOLDORDER_WRONG_BATTLE_TYPE);
            }
            if (gIds == null || gIds.length() <= 0) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.GENEGAL_CHOOSE_TO_BATTLE);
            }
            final String[] generalIds = gIds.split("#");
            final int generalNum = generalIds.length;
            if (!battle.getBattleId().trim().equalsIgnoreCase(goldOrder.getBattleId().trim()) || forceId != goldOrder.getForceId()) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.GOLDORDER_WRONG_BATTLE);
            }
            if (goldOrder.getPlayerIdList().contains(playerId)) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.GOLDORDER_ONLY_CAN_REPLY_ONCE);
            }
            if (!battle.isBattleDoing()) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.GOLDORDER_BATTLE_IS_OVER);
            }
            final List<Integer> gIdList = new ArrayList<Integer>();
            int gId = 0;
            String[] array;
            for (int length = (array = generalIds).length, i = 0; i < length; ++i) {
                final String str = array[i];
                gId = Integer.valueOf(str);
                if (gId > 0) {
                    gIdList.add(Integer.valueOf(str));
                }
            }
            final Tuple<Boolean, byte[]> result = this.checkGeneralForces(playerDto, gIdList);
            if (!(boolean)result.left) {
                return result.right;
            }
            List<PlayerGeneralMilitary> pgmList = this.getAvailableGenerals(playerId, gIdList);
            if (pgmList.isEmpty()) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.GOLDORDER_HAS_NO_AVAILABLE_GENERALS);
            }
            for (final PlayerGeneralMilitary pgm : pgmList) {
                final int maxForces = this.dataGetter.getBattleDataCache().getMaxHp(pgm);
                final long currentForces = pgm.getForces();
                long needForces = maxForces - currentForces;
                final General general = (General)this.dataGetter.getGeneralCache().get((Object)pgm.getGeneralId());
                final Troop troop = this.dataGetter.getTroopCache().getTroop(general.getTroop(), playerId);
                final WorldCity worldCity = (WorldCity)this.dataGetter.getWorldCityCache().get((Object)pgm.getLocationId());
                final PlayerResource pr = this.dataGetter.getPlayerResourceDao().read(playerId);
                final long food = pr.getFood();
                final double maxRemainingForces = food / this.dataGetter.getGeneralService().getRecuitConsume(troop.getId(), forceId, worldCity);
                if (maxRemainingForces - needForces <= 0.0) {
                    needForces = (long)maxRemainingForces;
                }
                this.dataGetter.getPlayerGeneralMilitaryDao().addGeneralForces(playerId, pgm.getGeneralId(), new Date(), 0, needForces);
                final double consume = needForces * this.dataGetter.getGeneralService().getRecuitConsume(troop.getId(), forceId, worldCity);
                if (consume > 0.0 && !this.dataGetter.getPlayerResourceDao().consumeFood(playerId, (int)consume, "\u52df\u5175\u6d88\u8017\u8d44\u6e90")) {
                    this.dataGetter.getPlayerGeneralMilitaryDao().resetForces(playerId, pgm.getGeneralId(), new Date(), currentForces);
                }
                this.dataGetter.getGeneralService().sendGeneralMilitaryRecruitInfo(playerId, pgm.getGeneralId());
            }
            final int attEffect = (int)(Object)((C)this.cCache.get((Object)"World.GoldOrd.Att")).getValue();
            final int defEffect = (int)(Object)((C)this.cCache.get((Object)"World.GoldOrd.Def")).getValue();
            final double tempEffect = ((C)this.cCache.get((Object)"World.GoldOrd.LegionE")).getValue();
            final BigDecimal b = new BigDecimal(tempEffect);
            final double teamEffect = b.setScale(2, 4).doubleValue();
            final Player player = this.playerDao.read(goldOrder.getPlayerId());
            pgmList = this.getAvailableGenerals(playerId, gIdList);
            final boolean battleResult = battle.joinZhengZhaoLing(this.dataGetter, playerId, pgmList, teamEffect, attEffect, defEffect, player.getPlayerName());
            if (!battleResult) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.GOLDORDER_REPONSE_FAIL_TO_JOIN_BATTLE);
            }
            final int foodCoe = (int)(Object)((C)this.cCache.get((Object)"World.GoldOrd.Food")).getValue();
            this.playerResourceDao.addFoodIgnoreMax(playerId, foodCoe * generalNum, "\u54cd\u5e94\u5f81\u53ec\u4ee4\u4e3b\u5c06\u7cae\u98df\u5956\u52b1");
            try {
                this.writeLock.lock();
                goldOrder.getPlayerIdList().add(playerId);
                final int num = goldOrder.getNum();
                goldOrder.setNum(num + generalNum);
                if (generalNum > 0) {
                    this.goldOrderDao.updateNumByForceIdAndCityId(forceId, cityId, generalNum);
                }
            }
            finally {
                this.writeLock.unlock();
            }
            this.writeLock.unlock();
            this.pushGoldOrderMsgForSinglePlayer(playerDto.playerId);
            this.dataGetter.getIndividualTaskService().sendTaskMessage(playerDto, 1, "jinling");
            return JsonBuilder.getJson(State.SUCCESS, LocalMessages.GOLDORDER_REPONSE_SUCCESS);
        }
        catch (Exception e) {
            BattleService.errorLog.error("className:BattleService#methodName:replyGoldOrder", e);
            return JsonBuilder.getJson(State.FAIL, LocalMessages.GOLDORDER_REPONSE_EXCEPTION);
        }
    }
    
    @Override
    public List<PlayerGeneralMilitary> getAvailableGenerals(final int playerId, final List<Integer> gIdList) {
        final Map<Integer, PlayerGeneralMilitary> pgmMap = this.dataGetter.getPlayerGeneralMilitaryDao().getMilitaryMap(playerId);
        final List<PlayerGeneralMilitary> chooseList = new ArrayList<PlayerGeneralMilitary>();
        for (int i = 0; i < gIdList.size(); ++i) {
            final PlayerGeneralMilitary pgm = pgmMap.get(gIdList.get(i));
            if (pgm != null) {
                if (pgm.getState() <= 1) {
                    if (!TeamManager.getInstance().isJoinTeam(pgm.getPlayerId(), pgm.getGeneralId())) {
                        if (!this.worldFarmService.isInFarmForbiddenOperation(pgm, false)) {
                            final GeneralMoveDto gmd = CityService.getGeneralMoveDto(pgm.getPlayerId(), pgm.getGeneralId());
                            if (gmd != null) {
                                if (gmd.cityState == 22) {
                                    continue;
                                }
                                if (gmd.cityState == 23) {
                                    continue;
                                }
                            }
                            chooseList.add(pgm);
                        }
                    }
                }
            }
        }
        return chooseList;
    }
    
    @Override
    public Tuple<Boolean, byte[]> checkGeneralForces(final PlayerDto playerDto, final List<Integer> gIdList) {
        final Tuple<Boolean, byte[]> result = new Tuple(false, "".getBytes());
        final Map<Integer, PlayerGeneralMilitary> pgmMap = this.dataGetter.getPlayerGeneralMilitaryDao().getMilitaryMap(playerDto.playerId);
        final PlayerResource pr = this.dataGetter.getPlayerResourceDao().read(playerDto.playerId);
        long food = pr.getFood();
        final int forceId = playerDto.forceId;
        for (final int generalId : gIdList) {
            final PlayerGeneralMilitary pgm = pgmMap.get(generalId);
            if (pgm == null) {
                result.right = JsonBuilder.getJson(State.FAIL, LocalMessages.GOLDORDER_WRONG_GENERAL_INFO);
                return result;
            }
            final int maxForces = this.dataGetter.getBattleDataCache().getMaxHp(pgm);
            final long currentForces = pgm.getForces();
            long needForces = maxForces - currentForces;
            final General general = (General)this.dataGetter.getGeneralCache().get((Object)pgm.getGeneralId());
            final Troop troop = this.dataGetter.getTroopCache().getTroop(general.getTroop(), playerDto.playerId);
            final WorldCity worldCity = (WorldCity)this.dataGetter.getWorldCityCache().get((Object)pgm.getLocationId());
            double finalForce = 0.0;
            final double consume = needForces * this.dataGetter.getGeneralService().getRecuitConsume(troop.getId(), forceId, worldCity);
            final double maxRemainingForces = food / this.dataGetter.getGeneralService().getRecuitConsume(troop.getId(), forceId, worldCity);
            if (maxRemainingForces - needForces <= 0.0) {
                needForces = (long)maxRemainingForces;
            }
            finalForce = currentForces + needForces;
            food -= (long)consume;
            if (finalForce * 1.0 / maxForces < 0.05) {
                result.right = JsonBuilder.getJson(State.FAIL, LocalMessages.BATTLE_INT_BATTLE_BLOOD_NO_ENOUGH);
                return result;
            }
        }
        result.left = true;
        result.right = "".getBytes();
        return result;
    }
    
    @Override
    public void getGoldOrderInfoForLogin(final int playerId, final int forceId, final JsonDocument doc) {
        boolean hasGoldOrder = false;
        final PlayerAttribute pa = this.dataGetter.getPlayerAttributeDao().read(playerId);
        final char[] cs = pa.getFunctionId().toCharArray();
        if (cs[24] != '1') {
            doc.createElement("hasGoldOrder", hasGoldOrder);
            return;
        }
        final Tuple<Boolean, Integer> result = this.getGordOrderNum(playerId);
        hasGoldOrder = result.left;
        final int num = result.right;
        doc.createElement("hasGoldOrder", hasGoldOrder);
        doc.createElement("goldOrderNum", num);
    }
    
    @Override
    public void loadGoldOrderFromDB(final int mimute) {
        try {
            final List<GoldOrder> goldOrderList = this.goldOrderDao.getModels();
            for (final GoldOrder goldOrder : goldOrderList) {
                if (goldOrder == null) {
                    continue;
                }
                final String battleId = goldOrder.getBattleId();
                final int cityId = goldOrder.getCityId();
                final int forceId = goldOrder.getForceId();
                final Battle battle = NewBattleManager.getInstance().getBattleByBatId(battleId);
                if (battle == null) {
                    this.goldOrderDao.deleteByForceIdAndCityId(forceId, cityId);
                }
                else if (!battle.isBattleDoing()) {
                    this.goldOrderDao.deleteByForceIdAndCityId(forceId, cityId);
                }
                else {
                    final String key = String.valueOf(forceId) + "_" + cityId;
                    goldOrder.setStartTime(new Date());
                    BattleService.goldOrderUseMap.put(key, goldOrder);
                    this.jobService.addJob("battleService", "deleteGoldOrder", key, System.currentTimeMillis() + mimute * 60000L, false);
                }
            }
        }
        catch (Exception e) {
            BattleService.errorLog.error("className:BattleService#methodName:loadGoldOrderFromDB", e);
        }
    }
    
    @Override
    public boolean hasGoldOrderInCertainCity(final int cityId) {
        boolean flag = false;
        for (final String tempKey : BattleService.goldOrderUseMap.keySet()) {
            final int cityIdInKey = Integer.parseInt(tempKey.split("_")[1]);
            if (cityId == cityIdInKey) {
                flag = true;
            }
        }
        return flag;
    }
    
    @Override
    public boolean canAttackInMist(final int playerId, final int cityId) {
        final PlayerWorld pw = this.dataGetter.getPlayerWorldDao().read(playerId);
        if (pw == null) {
            return false;
        }
        final String attedCityIds = pw.getAttedId();
        if (!StringUtils.isBlank(attedCityIds)) {
            final String[] cityIds = attedCityIds.split(",");
            String[] array;
            for (int length = (array = cityIds).length, i = 0; i < length; ++i) {
                final String str = array[i];
                if (Integer.parseInt(str) == cityId) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public Tuple<Boolean, Integer> getGordOrderNum(final int playerId) {
        final Player player = this.playerDao.read(playerId);
        final int forceId = player.getForceId();
        int num = 0;
        final Tuple<Boolean, Integer> tuple = new Tuple(false, 0);
        final PlayerAttribute pa = this.dataGetter.getPlayerAttributeDao().read(playerId);
        final char[] cs = pa.getFunctionId().toCharArray();
        if (cs[24] != '1') {
            return tuple;
        }
        boolean hasGoldOrder = false;
        for (final String tempKey : BattleService.goldOrderUseMap.keySet()) {
            final GoldOrder goldOrder = BattleService.goldOrderUseMap.get(tempKey);
            final int forceIdInKey = Integer.parseInt(tempKey.split("_")[0]);
            if (forceId == forceIdInKey && !goldOrder.getPlayerIdList().contains(playerId)) {
                ++num;
                hasGoldOrder = true;
            }
        }
        tuple.left = hasGoldOrder;
        tuple.right = num;
        return tuple;
    }
    
    @Override
    public void clearBattleForNTYellowTurbans(final int phase) {
        int[] cityIds = new int[0];
        BattleService.playerPhantomMap.clear();
        if (phase == 1) {
            cityIds = BattleConstant.NT_YELLOW_TURBANS_CLEAR_BATTLE_CITY_PHASE_1;
        }
        else {
            if (phase != 2) {
                return;
            }
            cityIds = BattleConstant.NT_YELLOW_TURBANS_CLEAR_BATTLE_CITY_PHASE_2;
        }
        int[] array;
        for (int length = (array = cityIds).length, i = 0; i < length; ++i) {
            final int cityId = array[i];
            CityService.setCityFlag(cityId, 1);
        }
        this.clearBattleInDetail(cityIds, 104, false);
        this.addYellowTurbansNPC(phase);
        this.caculatePlayerPhantomCopperExp(phase);
        int[] array2;
        for (int length2 = (array2 = cityIds).length, j = 0; j < length2; ++j) {
            final int cityId = array2[j];
            CityService.setCityFlag(cityId, 0);
        }
        this.refreshWorld();
    }
    
    private void clearBattleInDetail(final int[] cityIds, final int targetForcreId, final boolean isHuizhan) {
        try {
            for (final int cityId : cityIds) {
                final Battle battle = NewBattleManager.getInstance().getBattleByDefId(3, cityId);
                Label_0710: {
                    if (battle != null) {
                        synchronized (battle.getBattleId()) {
                            for (final CampArmy attCampArmy : battle.getAttCamp()) {
                                if (attCampArmy.getPlayerId() > 0) {
                                    if (!attCampArmy.isPhantom()) {
                                        NewBattleManager.getInstance().quitBattle(battle, attCampArmy.getPlayerId(), attCampArmy.getGeneralId());
                                    }
                                    else {
                                        List<CampArmy> campArmyList = BattleService.playerPhantomMap.get(attCampArmy.getPlayerId());
                                        if (campArmyList == null) {
                                            campArmyList = new ArrayList<CampArmy>();
                                        }
                                        final CampArmy tempCamp = new CampArmy();
                                        tempCamp.setArmyHp(attCampArmy.getArmyHp());
                                        tempCamp.setGeneralId(attCampArmy.getGeneralId());
                                        tempCamp.setPlayerId(attCampArmy.getPlayerId());
                                        campArmyList.add(tempCamp);
                                        BattleService.playerPhantomMap.put(attCampArmy.getPlayerId(), campArmyList);
                                    }
                                }
                                attCampArmy.setArmyHpLoss(attCampArmy.getArmyHp());
                                attCampArmy.setArmyHp(0);
                            }
                            battle.getAttBaseInfo().setNum(0);
                            battle.getAttList().clear();
                            for (final CampArmy defCampArmy : battle.getDefCamp()) {
                                if (defCampArmy.getPlayerId() > 0) {
                                    if (!defCampArmy.isPhantom()) {
                                        NewBattleManager.getInstance().quitBattle(battle, defCampArmy.getPlayerId(), defCampArmy.getGeneralId());
                                    }
                                    else {
                                        List<CampArmy> campArmyList = BattleService.playerPhantomMap.get(defCampArmy.getPlayerId());
                                        if (campArmyList == null) {
                                            campArmyList = new ArrayList<CampArmy>();
                                        }
                                        final CampArmy tempCamp = new CampArmy();
                                        tempCamp.setArmyHp(defCampArmy.getArmyHp());
                                        tempCamp.setGeneralId(defCampArmy.getGeneralId());
                                        tempCamp.setPlayerId(defCampArmy.getPlayerId());
                                        campArmyList.add(tempCamp);
                                        BattleService.playerPhantomMap.put(defCampArmy.getPlayerId(), campArmyList);
                                    }
                                }
                                defCampArmy.setArmyHpLoss(defCampArmy.getArmyHp());
                                defCampArmy.setArmyHp(0);
                            }
                            battle.getDefBaseInfo().setNum(0);
                            battle.getDefList().clear();
                            final boolean delSucc = BattleScheduler.getInstance().removeBattle(battle);
                            if (delSucc) {
                                battle.doBattle(this.dataGetter, battle.getStartTime());
                            }
                            else {
                                ErrorSceneLog.getInstance().appendErrorMsg("removeBattle CityBattle fail").appendBattleId(battle.getBattleId()).appendClassName("BattleService").appendMethodName("clearBattleInDetail").flush();
                            }
                            // monitorexit(battle.getBattleId())
                            break Label_0710;
                        }
                    }
                    final List<PlayerGeneralMilitaryPhantom> pgmPhantomList = this.dataGetter.getPlayerGeneralMilitaryPhantomDao().getPhantomByLocationIdOrderByPlayerIdLvDesc(cityId);
                    for (final PlayerGeneralMilitaryPhantom pgm : pgmPhantomList) {
                        List<CampArmy> campArmyList = BattleService.playerPhantomMap.get(pgm.getPlayerId());
                        if (campArmyList == null) {
                            campArmyList = new ArrayList<CampArmy>();
                        }
                        final CampArmy tempCamp = new CampArmy();
                        tempCamp.setArmyHp(pgm.getHp());
                        tempCamp.setGeneralId(pgm.getGeneralId());
                        tempCamp.setPlayerId(pgm.getPlayerId());
                        campArmyList.add(tempCamp);
                        BattleService.playerPhantomMap.put(pgm.getPlayerId(), campArmyList);
                    }
                }
                final List<PlayerGeneralMilitary> pgmList = this.dataGetter.getPlayerGeneralMilitaryDao().getMilitaryByLocationIdOrderByPlayerIdLvDesc(cityId);
                for (final PlayerGeneralMilitary pgm2 : pgmList) {
                    this.dataGetter.getGeneralService().sendGeneralMilitaryRecruitInfo(pgm2.getPlayerId(), pgm2.getGeneralId());
                    final int capitalId = WorldCityCommon.nationMainCityIdMap.get(pgm2.getForceId());
                    this.dataGetter.getCityService().sendAttMoveInfo(pgm2.getPlayerId(), pgm2.getGeneralId(), pgm2.getLocationId(), capitalId, pgm2.getForceId(), "", pgm2.getForces(), true);
                }
                this.dataGetter.getPlayerGeneralMilitaryDao().updateLocationByforceIdAndLocationId(cityId);
                this.dataGetter.getExpeditionArmyDao().deleteByLocationId(cityId);
                this.dataGetter.getBarbarainPhantomDao().removeAllInThisCity(cityId);
                this.dataGetter.getBarbarainExpeditionArmyDao().removeAllInThisCity(cityId);
                this.dataGetter.getPlayerGeneralMilitaryPhantomDao().deleteByLocationId(cityId);
                this.dataGetter.getYellowTurbansDao().deleteByCityId(cityId);
                final City city = this.dataGetter.getCityDao().read(cityId);
                if (!isHuizhan) {
                    if (cityId == 144 || cityId == 133 || cityId == 102) {
                        this.dataGetter.getCityService().changeForceIdAndState(cityId, targetForcreId, 0, 0, null);
                    }
                    else {
                        this.dataGetter.getCityDao().updateForceIdStateTitleBorder(cityId, targetForcreId, 0, 0, 0);
                        this.dataGetter.getCityDao().updateTrickInfo(cityId, "");
                        this.cityEffectCache.refreshCityEffect(targetForcreId, city.getForceId(), cityId);
                    }
                }
                else {
                    this.dataGetter.getCityDao().updateForceIdStateTitleBorder(cityId, targetForcreId, 0, 0, 0);
                    if (city.getForceId() != targetForcreId) {
                        this.dataGetter.getCityService().changeForceIdAndState(cityId, targetForcreId, 0, 0, null);
                        this.cityEffectCache.refreshCityEffect(targetForcreId, city.getForceId(), cityId);
                    }
                }
            }
        }
        catch (Exception e) {
            BattleService.errorLog.error("className:BattleService#methodName:clearBattleInDetail", e);
        }
    }
    
    public void addYellowTurbansNPC(final int phase) {
        try {
            final int maxLv = this.forceInfoDao.getMaxLv();
            final int lv = (maxLv <= 0) ? 1 : maxLv;
            final List<KtHjNpc> list = this.ktHjNpcCache.getBylvAndType(lv, phase);
            if (list == null) {
                return;
            }
            for (final KtHjNpc hjNpc : list) {
                if (hjNpc == null) {
                    continue;
                }
                final int armyId = hjNpc.getArmyId();
                final Army armyCache = (Army)this.dataGetter.getArmyCache().get((Object)armyId);
                final String[] cityIds = hjNpc.getCityId().split(";");
                final int npcNum = hjNpc.getNum();
                String[] array;
                for (int length = (array = cityIds).length, j = 0; j < length; ++j) {
                    final String str = array[j];
                    final int cityId = Integer.parseInt(str);
                    final List<YellowTurbans> yellowTurbansList = new ArrayList<YellowTurbans>();
                    for (int i = 0; i < npcNum; ++i) {
                        final YellowTurbans yellowTurbans = new YellowTurbans();
                        yellowTurbans.setCreateTime(new Date());
                        yellowTurbans.setArmyId(armyId);
                        yellowTurbans.setForceId(104);
                        yellowTurbans.setHp(armyCache.getArmyHp());
                        yellowTurbans.setLocationId(cityId);
                        yellowTurbans.setState(0);
                        yellowTurbans.setTacticval(1);
                        yellowTurbansList.add(yellowTurbans);
                    }
                    if (yellowTurbansList.size() > 0) {
                        this.dataGetter.getYellowTurbansDao().batchCreate(yellowTurbansList);
                    }
                    else {
                        BattleService.errorLog.error("className:BattleService#methodName:addYellowTurbansNPC#Reason:KtHjNpcList is Empty!");
                    }
                }
            }
        }
        catch (Exception e) {
            BattleService.errorLog.error("className:BattleService#methodName:addYellowTurbansNPC", e);
        }
    }
    
    @Override
    public void addXiangYangPhantomForTimer(final String param) {
        try {
            final int maxLv = this.forceInfoDao.getMaxLv();
            final int lv = (maxLv <= 0) ? 1 : maxLv;
            int armyId = 0;
            int cityId = 0;
            final List<KtHjNpc> list = this.ktHjNpcCache.getBylvAndType(lv, 3);
            if (list == null) {
                BattleService.errorLog.error("className:BattleService#methodName:addXiangYangPhantomForTimer#Could Not Load Zhangjiao Phantom From SData!");
                return;
            }
            if (list.size() != 1) {
                BattleService.errorLog.error("className:BattleService#methodName:addXiangYangPhantomForTimer#Zhangjiao Phantom number can not be more than 1!");
                return;
            }
            for (final KtHjNpc hjNpc : list) {
                armyId = hjNpc.getArmyId();
                final String[] cityIds = hjNpc.getCityId().split(";");
                cityId = Integer.parseInt(cityIds[0]);
            }
            if (armyId == 0 || cityId == 0) {
                BattleService.errorLog.error("className:BattleService#methodName:addXiangYangPhantomForTimer#armyId OR cityId is 0!");
                return;
            }
            if (RankService.taskInfo == null) {
                BattleService.errorLog.error("className:BattleService#methodName:addXiangYangPhantomForTimer:RankService.taskInfo is null!");
                return;
            }
            if (RankService.taskInfo.getState() != 1) {
                BattleService.errorLog.error("className:BattleService#methodName:addXiangYangPhantomForTimer:Nation Task is not ONGOING!#state=" + RankService.taskInfo.getState());
                return;
            }
            final int jitanNum = RankService.taskInfo.getTempleNum();
            long time = 0L;
            if (jitanNum == 3) {
                time = 30000L;
            }
            else if (jitanNum == 2) {
                time = 60000L;
            }
            else {
                if (jitanNum != 1) {
                    BattleService.errorLog.info("className:BattleService#methodName:addXiangYangPhantomForTimer#\u796d\u575b\u6570\u91cf\u4e3a0\uff0c\u5f20\u89d2\u5e7b\u5f71\u6295\u653e\u7ed3\u675f\uff01");
                    return;
                }
                time = 90000L;
            }
            final Army army = (Army)this.armyCache.get((Object)armyId);
            final Battle battle = NewBattleManager.getInstance().getBattleByDefId(3, cityId);
            if (battle != null) {
                if (battle.getDefBaseInfo().getForceId() != 104) {
                    BattleService.errorLog.error("className:BattleService#methodName:addXiangYangPhantomForTimer#\u5b88\u65b9\u52bf\u529b\u4e0d\u662f\u9ec4\u5dfe\u519b\uff0c\u505c\u6b62\u6295\u653e\u5f20\u89d2\u5e7b\u5f71\uff01");
                    return;
                }
                final Builder builder = BuilderFactory.getInstance().getBuilder(3);
                final int battleSide = 0;
                final int maxVid = this.dataGetter.getYellowTurbansDao().getMaxVid();
                final YellowTurbans yellowTurbans = new YellowTurbans();
                yellowTurbans.setCreateTime(new Date());
                yellowTurbans.setArmyId(armyId);
                yellowTurbans.setForceId(104);
                yellowTurbans.setHp(army.getArmyHp());
                yellowTurbans.setLocationId(cityId);
                yellowTurbans.setState(3);
                yellowTurbans.setTacticval(1);
                yellowTurbans.setVId(maxVid + 1);
                final CampArmy xiangYangPhantomCamp = builder.copyArmyfromNationTaskYellowTurbans(this.dataGetter, battle, yellowTurbans, battleSide);
                battle.joinCampArmy(this.dataGetter, battleSide, xiangYangPhantomCamp);
                this.dataGetter.getYellowTurbansDao().create(yellowTurbans);
            }
            else {
                final int maxVid2 = this.dataGetter.getYellowTurbansDao().getMaxVid();
                final YellowTurbans yellowTurbans2 = new YellowTurbans();
                yellowTurbans2.setCreateTime(new Date());
                yellowTurbans2.setArmyId(armyId);
                yellowTurbans2.setForceId(104);
                yellowTurbans2.setHp(army.getArmyHp());
                yellowTurbans2.setLocationId(cityId);
                yellowTurbans2.setState(0);
                yellowTurbans2.setTacticval(1);
                yellowTurbans2.setVId(maxVid2 + 1);
                this.dataGetter.getYellowTurbansDao().create(yellowTurbans2);
            }
            this.jobService.addJob("battleService", "addXiangYangPhantomForTimer", "", System.currentTimeMillis() + time, false);
        }
        catch (Exception e) {
            BattleService.errorLog.error("className:BattleService#methodName:addXiangYangPhantomForTimer", e);
        }
    }
    
    private void caculatePlayerPhantomCopperExp(final int phase) {
        AddExpInfo expResult = null;
        try {
            List<CampArmy> campArmyList = new ArrayList<CampArmy>();
            if (BattleService.playerPhantomMap == null || BattleService.playerPhantomMap.size() <= 0) {
                BattleService.errorLog.error("className:BattleService#methodName:caculatePlayerPhantomCopperExp#playerPhantomMap is null!");
                return;
            }
            for (final int playerId : BattleService.playerPhantomMap.keySet()) {
                campArmyList = BattleService.playerPhantomMap.get(playerId);
                if (campArmyList == null) {
                    BattleService.errorLog.error("className:BattleService#methodName:caculatePlayerPhantomCopperExp#campArmyList is null!");
                }
                else {
                    double chiefExpSum = 0.0;
                    double copperSum = 0.0;
                    final Map<Integer, Double> generalExpMap = new HashMap<Integer, Double>();
                    for (final CampArmy campArmy : campArmyList) {
                        final int generalId = campArmy.getGeneralId();
                        final General general = (General)this.dataGetter.getGeneralCache().get((Object)generalId);
                        final Troop troop = this.dataGetter.getTroopCache().getTroop(general.getTroop(), playerId);
                        final int troopId = troop.getId();
                        final double troopFoodConsumeCoe = ((TroopConscribe)this.dataGetter.getTroopConscribeCache().get((Object)troopId)).getFood();
                        final int hp = campArmy.getArmyHp();
                        final double troopFood = troopFoodConsumeCoe * hp;
                        final double delta = troopFood * 0.45;
                        copperSum += delta;
                        final double attTechAddGZJY = this.dataGetter.getTechEffectCache().getTechEffect(playerId, 40) / 100.0;
                        chiefExpSum += (1.0 + attTechAddGZJY) * delta;
                        final double gExpAdd = this.dataGetter.getTechEffectCache().getTechEffect(playerId, 16) / 100.0;
                        final double gExp = (1.0 + gExpAdd) * delta;
                        if (gExpAdd == 0.0 || gExp == 0.0) {
                            final String msg = "playerId = " + playerId + "generalId = " + generalId + " hp= " + hp + " troopFood= " + troopFood + " delta = " + delta + " gExpAdd = " + gExpAdd + ";gExp =" + gExp;
                            BattleService.errorLog.error("className:BattleService#methodName:caculatePlayerPhantomCopperExp#" + msg);
                        }
                        else {
                            final Double gExpSum = generalExpMap.get(generalId);
                            if (gExpSum == null) {
                                generalExpMap.put(generalId, gExp);
                            }
                            else {
                                generalExpMap.put(generalId, gExpSum + gExp);
                            }
                        }
                    }
                    if (copperSum == 0.0 || chiefExpSum == 0.0 || generalExpMap.size() <= 0) {
                        final String msg2 = "copperSum=" + copperSum + ";chiefExpSum=" + chiefExpSum + ";generalExpMap.size()=" + generalExpMap.size();
                        BattleService.errorLog.error("className:BattleService#methodName:caculatePlayerPhantomCopperExp#" + msg2);
                    }
                    else {
                        if (copperSum > 0.0) {
                            this.dataGetter.getPlayerResourceDao().addCopperIgnoreMax(playerId, copperSum, "\u56fd\u5bb6\u4efb\u52a1\u6e05\u7406\u6218\u6597\u6298\u7b97\u5e7b\u5f71\u83b7\u5f97\u94f6\u5e01", true);
                        }
                        if (chiefExpSum > 0.0) {
                            expResult = this.dataGetter.getPlayerService().updateExpAndPlayerLevel(playerId, (int)chiefExpSum, "\u56fd\u5bb6\u4efb\u52a1\u6e05\u7406\u6218\u6597\u6298\u7b97\u5e7b\u5f71\u83b7\u5f97\u7ecf\u9a8c");
                        }
                        final StringBuilder content = new StringBuilder();
                        String contentPart1 = "";
                        if (phase == 1) {
                            contentPart1 = LocalMessages.NT_YELLOW_TURBANS_CALCULATE_PLAYER_PHANTOM_MAIL_CONTENT_PART1_1;
                        }
                        else if (phase == 2) {
                            contentPart1 = LocalMessages.NT_YELLOW_TURBANS_CALCULATE_PLAYER_PHANTOM_MAIL_CONTENT_PART1_2;
                        }
                        else if (phase == 4) {
                            contentPart1 = LocalMessages.HUIZHAN_CALCULATE_PLAYER_PHANTOM_MAIL_CONTENT_PART1_4;
                        }
                        content.append(MessageFormatter.format(contentPart1, new Object[] { (int)copperSum, expResult.addExp }));
                        for (final Map.Entry<Integer, Double> entry : generalExpMap.entrySet()) {
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
                                content.append(MessageFormatter.format(LocalMessages.NT_YELLOW_TURBANS_CALCULATE_PLAYER_PHANTOM_MAIL_CONTENT_PART2, new Object[] { gName, addGExp }));
                            }
                        }
                        this.dataGetter.getMailService().writeSystemMail(LocalMessages.T_MAIL_ROLE_SYSTEM, LocalMessages.NT_YELLOW_TURBANS_CALCULATE_PLAYER_PHANTOM_MAIL_TITLE, content.toString(), 1, playerId, new Date());
                    }
                }
            }
        }
        catch (Exception e) {
            BattleService.errorLog.error("className:BattleService#methodName:caculatePlayerPhantomCopperExp", e);
        }
    }
    
    @Override
    public boolean isNTYellowTurbansXiangYangDoing(final int defId) {
        return RankService.taskInfo != null && RankService.taskInfo.isCanAtt() && RankService.taskInfo.getState() == 1 && defId == 105;
    }
    
    @Override
    public void refreshWorld() {
        try {
            final Group group = GroupManager.getInstance().getGroup(ChatType.WORLD.toString());
            if (group != null) {
                final byte[] bytes = JsonBuilder.getJson(JsonBuilder.getJson(State.PUSH, PushCommand.PUSH_CITIES.getModule(), JsonBuilder.getSimpleJson("refreshWorld", true)));
                group.notify((String)null, WrapperUtil.wrapper(PushCommand.PUSH_CITIES.getCommand(), 0, bytes));
            }
        }
        catch (Exception e) {
            BattleService.errorLog.error("className:BattleService#methodName:refreshWorld", e);
        }
    }
    
    @Override
    public void clearBattleForHuiZhan(final int cityId, final int defForceId) {
        try {
            final int[] cityIds = { 0 };
            BattleService.playerPhantomMap.clear();
            CityService.setCityFlag(cityIds[0] = cityId, 1);
            this.clearBattleInDetail(cityIds, defForceId, true);
            this.caculatePlayerPhantomCopperExp(4);
            CityService.setCityFlag(cityId, 0);
            this.refreshWorld();
        }
        catch (Exception e) {
            BattleService.errorLog.error("className:BattleService#methodName:clearBattleForHuiZhan", e);
        }
    }
    
    @Override
    public void updateHuizhanPlayerForce(final int defId, final int playerId, final int force) {
        try {
            final Player player = this.dataGetter.getPlayerDao().read(playerId);
            final HuizhanHistory hh = this.dataGetter.getHuiZhanService().getTodayHuizhanInProcess();
            if (hh != null && defId == hh.getCityId() && force > 0) {
                final PlayerHuizhan ph = this.dataGetter.getPlayerHuizhanDao().getByhuiZhanIdAndplayerId(hh.getVId(), playerId);
                int oldForce = 0;
                if (ph == null) {
                    final PlayerHuizhan newPh = new PlayerHuizhan();
                    newPh.setCityId(defId);
                    newPh.setForces(force);
                    newPh.setForceId(player.getForceId());
                    newPh.setHzId(hh.getVId());
                    newPh.setKillNum(0);
                    newPh.setPhantomNum(0);
                    newPh.setPkTimes(0);
                    newPh.setPlayerId(playerId);
                    newPh.setAwardFlag(0);
                    newPh.setJoinTime(new Date());
                    this.dataGetter.getPlayerHuizhanDao().create(newPh);
                }
                else {
                    this.dataGetter.getPlayerHuizhanDao().updateForceByhzIdAndPlayerId(force, hh.getVId(), playerId);
                    oldForce = ph.getForces();
                }
                if (hh.getGatherFlag() <= 0) {
                    final BaseRanker ranker = RankService.huiZhanForceRanker;
                    if (ranker != null) {
                        ranker.fireRankEvent(1, new RankData(playerId, oldForce + force));
                    }
                }
            }
        }
        catch (Exception e) {
            BattleService.errorLog.error("className:BattleService#methodName:updateHuizhanPlayerForce", e);
        }
    }
    
    @Override
    public synchronized void updateHuizhanNationForce(final int defId, final int forceId, final int force) {
        try {
            final HuizhanHistory hh = this.dataGetter.getHuiZhanService().getTodayHuizhanInProcess();
            if (hh != null && defId == hh.getCityId()) {
                final long oldAttForce = hh.getAttForce1() + hh.getAttForce2();
                final long oldDefForce = hh.getDefForce();
                final long oldMinForce = (oldAttForce > oldDefForce) ? oldDefForce : oldAttForce;
                int oldBlLv = 0;
                final BtLevel oldBl = this.btGatherRankingCache.getBtLevelByForce((int)oldMinForce / 10000);
                if (oldBl != null) {
                    oldBlLv = oldBl.getLv();
                }
                long attForce = oldAttForce;
                long defForce = oldDefForce;
                if (forceId == hh.getAttForceId1()) {
                    this.dataGetter.getHuiZhanService().updateHzAttForce1ByVid(force, hh.getVId());
                    attForce += force;
                }
                else if (forceId == hh.getAttForceId2()) {
                    this.dataGetter.getHuiZhanService().updateHzAttForce2ByVid(force, hh.getVId());
                    attForce += force;
                }
                else {
                    if (forceId != hh.getDefForceId()) {
                        return;
                    }
                    this.dataGetter.getHuiZhanService().updateHzDefForceByVid(force, hh.getVId());
                    defForce += force;
                }
                final int upForce = this.btGatherRankingCache.getForceByBtLv(1);
                final long minForce = (attForce > defForce) ? defForce : attForce;
                if (minForce >= upForce * 10000 && hh.getGatherFlag() < 1) {
                    this.dataGetter.getHuiZhanService().updateGatherFlagByVid(1, hh.getVId());
                    this.dataGetter.getHuiZhanService().pushHuiZhanTaskInfo(5, 0);
                }
                final BtLevel newBl = this.btGatherRankingCache.getBtLevelByForce((int)minForce / 10000);
                if (newBl != null && newBl.getLv() > oldBlLv) {
                    final int attForceId1 = hh.getAttForceId1();
                    final int attForceId2 = hh.getAttForceId2();
                    final int defForceId = hh.getDefForceId();
                    final WorldCity worldCity = (WorldCity)this.dataGetter.getWorldCityCache().get((Object)hh.getCityId());
                    final String cityName = worldCity.getName();
                    final String msg = MessageFormatter.format(newBl.getUpgradeWords(), new Object[] { ColorUtil.getForceMsg(attForceId1, WorldCityCommon.nationIdNameMap.get(attForceId1)), ColorUtil.getForceMsg(attForceId2, WorldCityCommon.nationIdNameMap.get(attForceId2)), ColorUtil.getForceMsg(defForceId, WorldCityCommon.nationIdNameMap.get(defForceId)), cityName });
                    this.chatService.sendSystemChat("GLOBAL", 0, 0, msg, null);
                }
                final JsonDocument doc = new JsonDocument();
                doc.startObject();
                doc.createElement("lastestAttForfce", attForce / 10000L);
                doc.createElement("lastestDefForfce", defForce / 10000L);
                doc.endObject();
                final Group worldGroup = GroupManager.getInstance().getGroup(ChatType.WORLD.toString());
                final Group battleGroup = GroupManager.getInstance().getGroup(ChatType.BATTLE.toString());
                if ((worldGroup != null || battleGroup != null) && System.currentTimeMillis() - HuiZhanService.pushTime > 3000L) {
                    final byte[] bytes = JsonBuilder.getJson(JsonBuilder.getJson(State.PUSH, PushCommand.PUSH_HUIZHAN_FORCE_CHANGE.getModule(), doc.toByte()));
                    if (worldGroup != null) {
                        worldGroup.notify((String)null, WrapperUtil.wrapper(PushCommand.PUSH_HUIZHAN_FORCE_CHANGE.getCommand(), 0, bytes));
                    }
                    if (battleGroup != null) {
                        battleGroup.notify((String)null, WrapperUtil.wrapper(PushCommand.PUSH_HUIZHAN_FORCE_CHANGE.getCommand(), 0, bytes));
                    }
                    HuiZhanService.pushTime = System.currentTimeMillis();
                }
            }
        }
        catch (Exception e) {
            BattleService.errorLog.error("className:BattleService#methodName:updateHuizhanNationForce", e);
        }
    }
    
    @Override
    public byte[] getCampList(final String batId, final int page, final int side) {
        final Battle battle = NewBattleManager.getInstance().getBattleByBatId(batId);
        if (page <= 0 || (side != 0 && side != 1) || battle == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final List<CampArmy> attCaList = new ArrayList<CampArmy>();
        final List<CampArmy> defCaList = new ArrayList<CampArmy>();
        final Set<CampArmy> attOnQueueSet = new HashSet<CampArmy>();
        final Set<CampArmy> defOnQueueSet = new HashSet<CampArmy>();
        for (final BattleArmy ba : battle.getAttList()) {
            final CampArmy ca = ba.getCampArmy();
            if (!attOnQueueSet.contains(ca)) {
                attOnQueueSet.add(ca);
                attCaList.add(ba.getCampArmy());
            }
        }
        for (final BattleArmy ba : battle.getDefList()) {
            final CampArmy ca = ba.getCampArmy();
            if (!defOnQueueSet.contains(ca)) {
                defOnQueueSet.add(ca);
                defCaList.add(ca);
            }
        }
        for (final CampArmy ca2 : battle.getAttCamp()) {
            if (ca2.getArmyHp() > 0 && !attOnQueueSet.contains(ca2)) {
                attCaList.add(ca2);
            }
        }
        for (final CampArmy ca2 : battle.getDefCamp()) {
            if (ca2.getArmyHp() > 0 && !defOnQueueSet.contains(ca2)) {
                defCaList.add(ca2);
            }
        }
        final int startIndex = (page - 1) * 8 + 1;
        final int endIndex = page * 8;
        final List<CampArmy> attPageList = new ArrayList<CampArmy>();
        final List<CampArmy> defPageList = new ArrayList<CampArmy>();
        if (side == 0 && attCaList.size() > 0) {
            for (int i = 0; i < attCaList.size(); ++i) {
                if (i >= startIndex - 1 && i < endIndex) {
                    attPageList.add(attCaList.get(i));
                }
            }
        }
        if (side == 1 && defCaList.size() > 0) {
            for (int i = 0; i < defCaList.size(); ++i) {
                if (i >= startIndex - 1 && i < endIndex) {
                    defPageList.add(defCaList.get(i));
                }
            }
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        int totalPage = 0;
        if (side == 0 && attPageList.size() > 0) {
            doc.startArray("pageList");
            for (final CampArmy ca3 : attPageList) {
                int playerId = ca3.getPlayerId();
                if (ca3.isPhantom()) {
                    playerId -= playerId;
                }
                boolean isOnQueue = false;
                if (attOnQueueSet.contains(ca3)) {
                    isOnQueue = true;
                }
                doc.startObject();
                doc.createElement("playerId", playerId);
                doc.createElement("playerName", ca3.getPlayerName());
                doc.createElement("generalName", ca3.getGeneralName());
                doc.createElement("quality", ca3.getQuality());
                doc.createElement("isOnQueue", isOnQueue);
                doc.createElement("forceId", ca3.getForceId());
                doc.endObject();
            }
            doc.endArray();
            if (attCaList.size() % 8 == 0) {
                totalPage = attCaList.size() / 8;
            }
            else {
                totalPage = attCaList.size() / 8 + 1;
            }
        }
        if (side == 1 && defPageList.size() > 0) {
            doc.startArray("pageList");
            for (final CampArmy ca3 : defPageList) {
                int playerId = ca3.getPlayerId();
                if (ca3.isPhantom()) {
                    playerId -= playerId;
                }
                boolean isOnQueue = false;
                if (defOnQueueSet.contains(ca3)) {
                    isOnQueue = true;
                }
                doc.startObject();
                doc.createElement("playerId", playerId);
                doc.createElement("playerName", ca3.getPlayerName());
                doc.createElement("generalName", ca3.getGeneralName());
                doc.createElement("quality", ca3.getQuality());
                doc.createElement("isOnQueue", isOnQueue);
                doc.createElement("forceId", ca3.getForceId());
                doc.endObject();
            }
            doc.endArray();
            if (defCaList.size() % 8 == 0) {
                totalPage = defCaList.size() / 8;
            }
            else {
                totalPage = defCaList.size() / 8 + 1;
            }
        }
        doc.createElement("totalPage", totalPage);
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    static class BattleStartResult
    {
        public boolean succ;
        public String battleId;
        public String failReason;
        
        BattleStartResult() {
            this.succ = false;
        }
    }
}
