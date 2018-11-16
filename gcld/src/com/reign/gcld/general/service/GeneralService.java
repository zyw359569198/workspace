package com.reign.gcld.general.service;

import org.springframework.stereotype.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.general.dao.*;
import com.reign.gcld.building.service.*;
import com.reign.gcld.player.dao.*;
import com.reign.gcld.task.dao.*;
import com.reign.gcld.sdata.cache.*;
import com.reign.gcld.store.dao.*;
import com.reign.gcld.slave.dao.*;
import com.reign.gcld.world.dao.*;
import java.util.concurrent.locks.*;
import com.reign.gcld.common.log.*;
import com.reign.gcld.world.service.*;
import com.reign.gcld.general.dto.*;
import com.reign.gcld.player.dto.*;
import com.reign.framework.json.*;
import org.springframework.transaction.annotation.*;
import com.reign.gcld.battle.service.*;
import com.reign.gcld.sdata.common.*;
import com.reign.gcld.team.service.*;
import com.reign.gcld.battle.scene.*;
import com.reign.gcld.slave.domain.*;
import com.reign.gcld.world.domain.*;
import com.reign.gcld.task.domain.*;
import com.reign.gcld.task.request.*;
import com.reign.gcld.task.common.*;
import java.util.*;
import com.reign.gcld.world.common.*;
import com.reign.gcld.store.domain.*;
import com.reign.gcld.store.service.*;
import com.reign.gcld.store.common.*;
import com.reign.gcld.common.*;
import com.reign.gcld.task.message.*;
import com.reign.gcld.general.domain.*;
import com.reign.gcld.civiltrick.trick.*;
import com.reign.gcld.battle.common.*;
import com.reign.gcld.juben.service.*;
import com.reign.gcld.common.event.*;
import com.reign.gcld.juben.common.*;
import com.reign.gcld.player.domain.*;
import com.reign.util.*;
import com.reign.gcld.sdata.domain.*;

@Component("generalService")
public class GeneralService implements IGeneralService
{
    private static final Logger log;
    @Autowired
    private IPlayerGeneralMilitaryDao playerGeneralMilitaryDao;
    @Autowired
    private IPlayerGeneralCivilDao playerGeneralCivilDao;
    @Autowired
    private IPlayerGeneralDao playerGeneralDao;
    @Autowired
    private GeneralCache generalCache;
    @Autowired
    private TroopConscribeCache troopConscribeCache;
    @Autowired
    private IBuildingOutputCache buildingOutputCache;
    @Autowired
    private GeneralPositionCache generalPositionCache;
    @Autowired
    private IStoreHouseDao storeHouseDao;
    @Autowired
    private EquipCache equipCache;
    @Autowired
    private StratagemCache stratagemCache;
    @Autowired
    private IPlayerAttributeDao playerAttributeDao;
    @Autowired
    private SerialCache serialCache;
    @Autowired
    private CCache cCache;
    @Autowired
    private WorldCityCache worldCityCache;
    @Autowired
    private WorldCityAreaCache worldCityAreaCache;
    @Autowired
    private IPlayerResourceDao playerResourceDao;
    @Autowired
    private IBattleDataCache battleDataCache;
    @Autowired
    private OfficerSpecialtyCache officerSpecialtyCache;
    @Autowired
    private TacticCache tacticCache;
    @Autowired
    private IPlayerDao playerDao;
    @Autowired
    private ChargeitemCache chargeitemCache;
    @Autowired
    private IPlayerTaskDao playerTaskDao;
    @Autowired
    private TroopCache troopCache;
    @Autowired
    private GeneralTreasureCache generalTreasureCache;
    @Autowired
    private EquipSuitCache equipSuitCache;
    @Autowired
    private EquipSkillCache equipSkillCache;
    @Autowired
    private IDataGetter dataGetter;
    @Autowired
    private StringCCache stringCCache;
    @Autowired
    private FightStrategiesCache fightStrategiesCache;
    @Autowired
    private EquipSkillEffectCache equipSkillEffectCache;
    @Autowired
    private IStoreHouseBakDao storeHouseBakDao;
    @Autowired
    private IPlayerSlaveDao playerSlaveDao;
    @Autowired
    private IPlayerFarmDao playerFarmDao;
    private static ReentrantLock[] locks;
    private static final int LOCKS_LEN;
    public static final int MUBING_TYPE_0_NORMAL = 0;
    public static final int MUBING_TYPE_1_MUBINGLING_ONLY = 1;
    
    static {
        log = CommonLog.getLog(GeneralService.class);
        GeneralService.locks = new ReentrantLock[10240];
        LOCKS_LEN = GeneralService.locks.length;
        for (int i = 0; i < GeneralService.LOCKS_LEN; ++i) {
            GeneralService.locks[i] = new ReentrantLock(false);
        }
    }
    
    @Override
    public int getRate(final int forceId, final WorldCity worldCity) {
        final WorldCityArea worldCityArea = (WorldCityArea)this.worldCityAreaCache.get((Object)worldCity.getArea(forceId));
        return worldCityArea.getTroopConscribeSpeed();
    }
    
    @Override
    public Double getOutput(final int playerId, final int forceId, final int cityId, final Troop troop) {
        final int output = this.buildingOutputCache.getBuildingsOutput(playerId, 5);
        final WorldCity worldCity = (WorldCity)this.worldCityCache.get((Object)cityId);
        return output * 1.0 / 3600.0 * this.getRate(forceId, worldCity) / 100.0;
    }
    
    @Override
    public void sendGenerlMoveInfo(final int playerId, final int generalId) {
        final GeneralMoveDto gmd = CityService.getGeneralMoveDto(playerId, generalId);
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("generalId", generalId);
        boolean autoMove = false;
        if (gmd != null && gmd.moveLine.length() > 0) {
            final String[] strs = gmd.moveLine.split(",");
            if (strs.length > 1) {
                autoMove = true;
            }
        }
        doc.createElement("autoMove", autoMove);
        doc.endObject();
        Players.push(playerId, PushCommand.PUSH_GENERALMOVE, doc.toByte());
    }
    
    @Override
    public void sendGenerlJuBenMoveInfo(final int playerId, final int generalId) {
        final GeneralMoveDto gmd = CityService.getGeneralMoveDto(playerId, generalId);
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("generalId", generalId);
        boolean autoMove = false;
        if (gmd != null && gmd.moveLine.length() > 0) {
            final String[] strs = gmd.moveLine.split(",");
            if (strs.length > 1) {
                autoMove = true;
            }
        }
        doc.createElement("autoMove", autoMove);
        doc.endObject();
        Players.push(playerId, PushCommand.PUSH_GENERAL_JUBENMOVE, doc.toByte());
    }
    
    @Override
    public void sendGeneralMilitaryRecruitInfo(final int playerId, final int generalId) {
        final PlayerGeneralMilitary pgm = this.playerGeneralMilitaryDao.getMilitary(playerId, generalId);
        this.sendGeneralMilitaryRecruitInfo(playerId, pgm);
    }
    
    @Override
    public void sendGmForcesSet(final int playerId, final Map<Integer, Long> forces) {
        if (forces.size() > 0) {
            final JsonDocument doc = new JsonDocument();
            doc.startObject();
            doc.createElement("update", "set");
            doc.startArray("military");
            final Date date = new Date();
            boolean send = false;
            for (final Integer key : forces.keySet()) {
                final GeneralMoveDto gmd = CityService.getGeneralMoveDto(playerId, key);
                if (gmd != null) {
                    if (CDUtil.isInCD(gmd.nextMoveTime, date) || gmd.cityState == 22) {
                        continue;
                    }
                    if (gmd.cityState == 23) {
                        continue;
                    }
                }
                doc.startObject();
                doc.createElement("generalId", key);
                doc.createElement("forces", forces.get(key));
                doc.endObject();
                send = true;
            }
            doc.endArray();
            doc.endObject();
            if (send) {
                Players.push(playerId, PushCommand.PUSH_GENERAL, doc.toByte());
            }
        }
    }
    
    @Override
    public void sendGmStateSet(final int playerId, final int generalId, final int state) {
        final PlayerGeneralMilitary pgm = this.playerGeneralMilitaryDao.getMilitary(playerId, generalId);
        this.sendGeneralMilitaryRecruitInfo(playerId, pgm);
    }
    
    @Override
    public void sendGmStateLocationIdSet(final int playerId, final int generalId, final int state, final int cityId) {
        final PlayerGeneralMilitary pgm = this.playerGeneralMilitaryDao.getMilitary(playerId, generalId);
        this.sendGeneralMilitaryRecruitInfo(playerId, pgm);
    }
    
    @Override
    public void sendGmCityStateSet(final int playerId, final int generalId, final long cTime, final int cState, final int type, final int cityId, final int state) {
        if (cTime > 0L) {
            final PlayerGeneralMilitary pgm = this.playerGeneralMilitaryDao.getMilitary(playerId, generalId);
            this.sendGeneralMilitaryRecruitInfo(playerId, pgm);
        }
    }
    
    @Override
    public void sendGmUpdate1(final int playerId, final int generalId, final boolean isDirect) {
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("update", "set");
        doc.startArray("military");
        final PlayerGeneralMilitary pgm = this.playerGeneralMilitaryDao.getMilitary(playerId, generalId);
        doc.startObject();
        doc.createElement("generalId", pgm.getGeneralId());
        doc.createElement("generalLv", pgm.getLv());
        doc.createElement("generalExp", pgm.getExp());
        final int generalExpMax = this.serialCache.get(((General)this.generalCache.get((Object)pgm.getGeneralId())).getUpExpS(), pgm.getLv());
        doc.createElement("generalExpMax", generalExpMax);
        doc.endObject();
        doc.endArray();
        doc.endObject();
        if (isDirect) {
            Players.push(playerId, PushCommand.PUSH_GENERAL_BATTLE, doc.toByte());
        }
        else {
            Players.push(playerId, PushCommand.PUSH_GENERAL, doc.toByte());
        }
    }
    
    @Override
    public void sendGmUpdate(final int playerId, final int generalId, final boolean isDirect) {
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("update", "set");
        doc.startArray("military");
        final PlayerGeneralMilitary pgm = this.playerGeneralMilitaryDao.getMilitary(playerId, generalId);
        final Player player = this.playerDao.read(playerId);
        doc.appendJson(this.oneGeneralMilitaryInfo(pgm, player.getForceId(), player.getPlayerId()));
        doc.endArray();
        doc.endObject();
        if (isDirect) {
            Players.push(playerId, PushCommand.PUSH_GENERAL_BATTLE, doc.toByte());
        }
        else {
            Players.push(playerId, PushCommand.PUSH_GENERAL, doc.toByte());
        }
    }
    
    @Override
    public void sendGmStateAndLvSet(final int playerId, final Map<Integer, MilitaryDto> map, final boolean isDirect) {
        if (map.size() < 1) {
            return;
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("update", "set");
        doc.startArray("military");
        final List<PlayerGeneralMilitary> pgmList = this.playerGeneralMilitaryDao.getMilitaryListOrder(playerId);
        final Player player = this.playerDao.read(playerId);
        for (final PlayerGeneralMilitary pgm : pgmList) {
            if (map.containsKey(pgm.getGeneralId())) {
                doc.appendJson(this.oneGeneralMilitaryInfo(pgm, player.getForceId(), player.getPlayerId()));
            }
        }
        doc.endArray();
        doc.endObject();
        if (isDirect) {
            Players.push(playerId, PushCommand.PUSH_GENERAL_BATTLE, doc.toByte());
        }
        else {
            Players.push(playerId, PushCommand.PUSH_GENERAL, doc.toByte());
        }
    }
    
    @Override
    public void sendGeneralMilitaryList(final int playerId) {
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("update", "set");
        doc.startArray("military");
        final List<PlayerGeneralMilitary> pgmList = this.playerGeneralMilitaryDao.getMilitaryListOrder(playerId);
        if (pgmList.size() < 1) {
            return;
        }
        final Player player = this.playerDao.read(playerId);
        for (final PlayerGeneralMilitary pgm : pgmList) {
            doc.appendJson(this.oneGeneralMilitaryInfo(pgm, player.getForceId(), player.getPlayerId()));
        }
        doc.endArray();
        doc.endObject();
        Players.push(playerId, PushCommand.PUSH_GENERAL, doc.toByte());
    }
    
    @Override
    public void sendGmForcesReduceUpdateState(final int playerId, final Map<Integer, MilitaryDto> map, final boolean isDirect) {
        if (map.size() < 1) {
            return;
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("update", "reduce");
        doc.startArray("military");
        final List<PlayerGeneralMilitary> pgmList = this.playerGeneralMilitaryDao.getMilitaryListOrder(playerId);
        final Player player = this.playerDao.read(playerId);
        for (final PlayerGeneralMilitary pgm : pgmList) {
            if (map.containsKey(pgm.getGeneralId())) {
                doc.appendJson(this.oneGeneralMilitaryInfo(pgm, player.getForceId(), player.getPlayerId()));
            }
        }
        doc.endArray();
        doc.endObject();
        if (isDirect) {
            Players.push(playerId, PushCommand.PUSH_GENERAL_BATTLE, doc.toByte());
        }
        else {
            Players.push(playerId, PushCommand.PUSH_GENERAL, doc.toByte());
        }
    }
    
    @Override
    public void sendGmForcesReduce(final int playerId, final int generalId, final int reduceForces, final int killCount, final boolean isDirect) {
        boolean send = false;
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("update", "reduce");
        doc.startArray("military");
        doc.startObject();
        doc.createElement("generalId", generalId);
        if (killCount > 0) {
            final PlayerGeneralMilitary pgm = this.playerGeneralMilitaryDao.getMilitary(playerId, generalId);
            if (pgm == null) {
                return;
            }
            doc.createElement("forces", reduceForces);
            doc.createElement("realForces", pgm.getForces());
            doc.createElement("kill", killCount);
            send = true;
        }
        doc.endObject();
        doc.endArray();
        doc.endObject();
        if (send) {
            if (isDirect) {
                Players.push(playerId, PushCommand.PUSH_GENERAL_BATTLE, doc.toByte());
            }
            else {
                Players.push(playerId, PushCommand.PUSH_GENERAL, doc.toByte());
            }
        }
    }
    
    @Override
    public void sendGmForcesReduce(final int playerId, final int generalId, final int lostA, final int lostB, final boolean isDirect, final String vsPname, final String vsGname, final int vsGq) {
        boolean send = false;
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("update", "reduce");
        doc.startArray("military");
        final PlayerGeneralMilitary pgm = this.playerGeneralMilitaryDao.getMilitary(playerId, generalId);
        if (pgm == null) {
            return;
        }
        doc.startObject();
        doc.createElement("generalId", generalId);
        doc.createElement("forces", lostA);
        doc.createElement("realForces", pgm.getForces());
        if (vsPname != null) {
            doc.createElement("vsPname", vsPname);
        }
        doc.createElement("vsGname", vsGname);
        doc.createElement("vsGq", vsGq);
        doc.createElement("kill", lostB);
        doc.endObject();
        send = true;
        doc.endArray();
        doc.endObject();
        if (send) {
            if (isDirect) {
                Players.push(playerId, PushCommand.PUSH_GENERAL_BATTLE, doc.toByte());
            }
            else {
                Players.push(playerId, PushCommand.PUSH_GENERAL, doc.toByte());
            }
        }
    }
    
    @Override
    public void sendGeneralMilitaryRecruitInfo(final int playerId, final PlayerGeneralMilitary pgm) {
        if (pgm != null) {
            final JsonDocument doc = new JsonDocument();
            doc.startObject();
            doc.createElement("update", "set");
            doc.startArray("military");
            final Player player = this.playerDao.read(playerId);
            doc.appendJson(this.oneGeneralMilitaryInfo(pgm, player.getForceId(), player.getPlayerId()));
            doc.endArray();
            doc.endObject();
            Players.push(playerId, PushCommand.PUSH_GENERAL, doc.toByte());
        }
    }
    
    @Override
    public void sendGeneralSizeInfo(final int playerId, final int playerLv) {
        final PlayerAttribute pa = this.playerAttributeDao.read(playerId);
        final char[] cs = pa.getFunctionId().toCharArray();
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("update", "set");
        boolean send = false;
        if (cs[2] == '1') {
            final int mSizeMaxOrg = this.dataGetter.getTechEffectCache().getTechEffect(playerId, 27);
            final int mSizeMax = this.dataGetter.getTechEffectCache().getTechEffect(playerId, 27);
            if (mSizeMaxOrg != mSizeMax) {
                send = true;
                doc.appendJson(this.getGeneralMilitaryMaxSizeInfo(playerId, playerLv));
            }
        }
        if (cs[1] == '1') {
            final int cSizeMaxOrg = this.dataGetter.getTechEffectCache().getTechEffect(playerId, 32);
            final int cSizeMax = this.generalPositionCache.getCivilCountByLv(playerLv);
            if (cSizeMaxOrg != cSizeMax) {
                send = true;
                doc.appendJson(this.getGeneralCivilMaxSizeInfo(playerId, playerLv));
            }
        }
        doc.endObject();
        if (send) {
            Players.push(playerId, PushCommand.PUSH_GENERAL, doc.toByte());
        }
    }
    
    @Override
    public void sendGeneralMilitaryRecruitInfo(final PlayerDto playerDto) {
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("update", "set");
        doc.appendJson(this.getGeneralMilitaryListInfo(playerDto));
        doc.endObject();
        Players.push(playerDto.playerId, PushCommand.PUSH_GENERAL, doc.toByte());
    }
    
    @Override
    public void sendGeneralMilitaryRecruitInfo(final int playerId, final boolean recruit) {
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("update", "set");
        final Player player = this.playerDao.read(playerId);
        final List<PlayerGeneralMilitary> pgmList = this.playerGeneralMilitaryDao.getMilitaryListOrder(playerId);
        doc.createElement("mSize", pgmList.size());
        doc.startArray("military");
        for (int i = 0; i < pgmList.size(); ++i) {
            if (!recruit || pgmList.get(i).getState() == 1) {
                final General gm = (General)this.generalCache.get((Object)pgmList.get(i).getGeneralId());
                final Troop troop = this.dataGetter.getTroopCache().getTroop(gm.getTroop(), playerId);
                doc.startObject();
                doc.createElement("generalId", pgmList.get(i).getGeneralId());
                final double secondForces = this.getOutput(playerId, player.getForceId(), pgmList.get(i).getLocationId(), troop);
                doc.createElement("intervalForces", (int)(secondForces * 10.0));
                doc.endObject();
            }
        }
        doc.endArray();
        doc.endObject();
        Players.push(playerId, PushCommand.PUSH_GENERAL, doc.toByte());
    }
    
    @Override
    public void sendGeneralMilitaryRecruitInfo(final Player player, final PlayerGeneralMilitary pgm, final boolean firstSend) {
        if (pgm != null) {
            final JsonDocument doc = new JsonDocument();
            doc.startObject();
            doc.startArray("military");
            doc.appendJson(this.oneGeneralMilitaryInfo(pgm, player.getForceId(), player.getPlayerId(), firstSend));
            doc.endArray();
            doc.appendJson(this.getGeneralMilitaryMaxSizeInfo(player.getPlayerId(), player.getPlayerLv()));
            doc.endObject();
            Players.push(player.getPlayerId(), PushCommand.PUSH_GENERAL, doc.toByte());
        }
    }
    
    @Override
    public void sendGeneralCivilRecruitInfo(final int playerId, final int playerLv, final PlayerGeneralCivil pgc, final boolean firstSend) {
        if (pgc != null) {
            final JsonDocument doc = new JsonDocument();
            doc.startObject();
            doc.startArray("civil");
            doc.appendJson(this.oneGeneralCivilInfo(pgc));
            doc.endArray();
            doc.appendJson(this.getGeneralCivilMaxSizeInfo(playerId, playerLv));
            doc.endObject();
            Players.push(playerId, PushCommand.PUSH_GENERAL, doc.toByte());
        }
    }
    
    @Transactional
    @Override
    public byte[] getGeneralSimpleInfo(final PlayerDto playerDto) {
        final int playerId = playerDto.playerId;
        final int playerLv = playerDto.playerLv;
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.appendJson(this.getGeneralMilitaryListInfo(playerDto));
        doc.appendJson(this.getGeneralMilitaryMaxSizeInfo(playerId, playerLv));
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public void sendGeneralMaxSizeInfo(final int playerId, final int playerLv, final int type) {
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        if (2 == type) {
            doc.appendJson(this.getGeneralMilitaryMaxSizeInfo(playerId, playerLv));
        }
        else {
            doc.appendJson(this.getGeneralCivilMaxSizeInfo(playerId, playerLv));
        }
        doc.endObject();
        Players.push(playerId, PushCommand.PUSH_GENERAL, doc.toByte());
    }
    
    private byte[] oneGeneralMilitaryInfo(final PlayerGeneralMilitary pgm, final int forceId, final int playerId) {
        return this.oneGeneralMilitaryInfo(pgm, forceId, playerId, false);
    }
    
    private byte[] oneGeneralMilitaryInfo(final PlayerGeneralMilitary pgm, final int forceId, final int playerId, final boolean firstSend) {
        final JsonDocument doc = new JsonDocument();
        if (pgm != null) {
            doc.startObject();
            doc.createElement("special", firstSend);
            final General general = (General)this.generalCache.get((Object)pgm.getGeneralId());
            doc.createElement("generalId", pgm.getGeneralId());
            doc.createElement("generalName", general.getName());
            doc.createElement("generalLv", pgm.getLv());
            doc.createElement("forces", pgm.getForces());
            doc.createElement("generalLocationId", pgm.getLocationId());
            if (this.worldCityCache == null) {
                GeneralService.log.error("worldCityCache is Null gId:" + pgm.getGeneralId() + " pid:" + pgm.getPlayerId() + " locId:" + pgm.getLocationId());
            }
            else if (this.worldCityCache.get((Object)pgm.getLocationId()) == null) {
                GeneralService.log.error("worldCityCache get is Null gId" + pgm.getGeneralId() + " pid:" + pgm.getPlayerId() + " locId:" + pgm.getLocationId());
            }
            doc.createElement("generalLocationName", ((WorldCity)this.worldCityCache.get((Object)pgm.getLocationId())).getName());
            doc.createElement("tacticId", general.getTacticId());
            doc.createElement("gIntro", general.getIntro());
            doc.createElement("juBenLoId", pgm.getJubenLoId());
            final Battle battle = NewBattleManager.getInstance().getBattleByGId(playerId, pgm.getGeneralId());
            if (battle != null) {
                doc.createElement("battleId", battle.getBattleId());
            }
            else {
                doc.createElement("battleId", "");
            }
            final Tactic tactic = (Tactic)this.tacticCache.get((Object)general.getTacticId());
            if (tactic != null) {
                doc.createElement("tacName", tactic.getName());
                doc.createElement("tacRange", tactic.getRange());
                doc.createElement("tacIntro", tactic.getIntro());
            }
            else {
                doc.createElement("tacName", "");
                doc.createElement("tacRange", "");
                doc.createElement("tacIntro", "");
            }
            doc.createElement("generalExp", pgm.getExp());
            final int generalExpMax = this.serialCache.get(((General)this.generalCache.get((Object)pgm.getGeneralId())).getUpExpS(), pgm.getLv());
            doc.createElement("generalExpMax", generalExpMax);
            final Troop troop = this.troopCache.getTroop(general.getTroop(), playerId);
            doc.createElement("troopId", troop.getType());
            doc.createElement("troopType", troop.getSerial());
            doc.createElement("troopLv", troop.getLevel());
            doc.createElement("troopName", troop.getName());
            doc.createElement("troopQuality", troop.getQuality());
            doc.createElement("leader", pgm.getLeader(general.getLeader()));
            doc.createElement("strength", pgm.getStrength(general.getStrength()));
            if (troop.getTsstList() != null) {
                doc.startArray("tts");
                FightStrategies fs = null;
                for (final TerrainStrategySpecDto ts : troop.getTsstList()) {
                    doc.startObject();
                    doc.createElement("terrainId", ts.terrainId);
                    fs = (FightStrategies)this.fightStrategiesCache.get((Object)ts.strategyId);
                    doc.createElement("strategyId", ts.strategyId);
                    doc.createElement("sName", fs.getName());
                    doc.createElement("show", ts.show);
                    doc.endObject();
                }
                doc.endArray();
            }
            doc.createElement("pic", general.getPic());
            doc.createElement("quality", general.getQuality());
            GeneralMoveDto gmd = CityService.getGeneralMoveDto(playerId, pgm.getGeneralId());
            if (gmd == null) {
                gmd = CityService.getUpdateGeneralMoveDto(playerId, pgm.getGeneralId());
                final PlayerSlave ps = this.playerSlaveDao.getBySlaveIdAndGeneralId(playerId, pgm.getGeneralId());
                if (ps != null) {
                    if (ps.getCd() != null) {
                        final long runTime = CDUtil.getCD(ps.getCd().getTime(), new Date());
                        if (runTime > 0L) {
                            gmd.runawayTime = ps.getCd().getTime();
                            gmd.cityState = 23;
                        }
                    }
                    else {
                        gmd.cityState = 22;
                    }
                }
                final PlayerFarm playerFarm = this.playerFarmDao.getByPAndGId(playerId, pgm.getGeneralId());
                if (playerFarm != null && playerFarm.getEndTime() != null) {
                    final long cd = CDUtil.getCD(playerFarm.getEndTime().getTime(), new Date());
                    gmd.farmtime = ((cd > 0L) ? playerFarm.getEndTime().getTime() : 0L);
                }
            }
            boolean autoMove = false;
            if (gmd != null) {
                long time = CDUtil.getCD(gmd.nextMoveTime, new Date());
                if (time > 0L) {
                    doc.createElement("cTime", time);
                    doc.createElement("cState", 6);
                    if (gmd.moveLine.length() > 0) {
                        final String[] strs = gmd.moveLine.split(",");
                        final int endCityId = Integer.valueOf(strs[strs.length - 1]);
                        doc.createElement("endCityId", endCityId);
                        if (pgm.getLocationId() != endCityId) {
                            autoMove = true;
                        }
                    }
                }
                else if (gmd.cityState == 22) {
                    doc.createElement("cState", 22);
                }
                else if (gmd.cityState == 23) {
                    time = CDUtil.getCD(gmd.runawayTime, new Date());
                    if (time > 0L) {
                        doc.createElement("cTime", time);
                        doc.createElement("cState", 23);
                    }
                }
            }
            doc.createElement("autoMove", autoMove);
            doc.startArray("terrain");
            for (final Integer key : troop.getTerrains().keySet()) {
                final TroopTerrain tt = troop.getTerrains().get(key);
                if (tt.getShow() != 0) {
                    if (tt.getShow() == 2 && tt.getDefEffect() > 0) {
                        doc.startObject();
                        doc.createElement("tType", key);
                        doc.createElement("tShow", tt.getShow());
                        doc.createElement("terrainQ", tt.getDefQuality());
                        doc.createElement("tValue", tt.getDefEffect());
                        doc.endObject();
                    }
                    else {
                        if (tt.getAttEffect() <= 0) {
                            continue;
                        }
                        doc.startObject();
                        doc.createElement("tType", key);
                        doc.createElement("tShow", tt.getShow());
                        doc.createElement("terrainQ", tt.getAttQuality());
                        doc.createElement("tValue", tt.getAttEffect());
                        doc.endObject();
                    }
                }
            }
            doc.endArray();
            int generalState = pgm.getState();
            final double secondForces = this.getOutput(pgm.getPlayerId(), forceId, pgm.getLocationId(), troop);
            final int forcesMax = this.battleDataCache.getMaxHp(pgm);
            final long needForces = forcesMax - pgm.getForces();
            if (needForces > 0L) {
                long needTime = (long)(needForces / secondForces);
                final long timeed = (System.currentTimeMillis() - pgm.getUpdateForcesTime().getTime()) / 1000L;
                needTime = needTime - timeed + 10L;
                if (needTime < 0L) {
                    needTime = 10L;
                }
                doc.createElement("needTime", needTime * 1000L);
            }
            else {
                doc.createElement("needTime", 0);
                if (generalState == 1) {
                    generalState = 0;
                    this.playerGeneralMilitaryDao.updateStateByPidAndGid(pgm.getPlayerId(), pgm.getGeneralId(), generalState, new Date());
                }
            }
            if (generalState <= 1 && TeamManager.getInstance().isJoinTeam2(pgm.getPlayerId(), pgm.getGeneralId())) {
                generalState = 16;
            }
            doc.createElement("generalState", generalState);
            final Date date = new Date();
            final long farmCd = CDUtil.getCD(gmd.farmtime, date);
            doc.createElement("farmCd", (farmCd <= 0L) ? 0L : farmCd);
            final WorldFarmCache cache = WorldFarmCache.getInstatnce();
            final long value = cache.getBuffCdByPlayerId(playerId, pgm.getGeneralId());
            doc.createElement("buffCd", CDUtil.getCD(value, date.getTime()));
            final Chargeitem item = (Chargeitem)this.chargeitemCache.get((Object)86);
            final double param = (item == null) ? 1.0 : item.getParam();
            final int gold = (item == null) ? 1 : item.getCost();
            doc.createElement("cdRecoverGold", gold / param);
            doc.createElement("forcesMax", forcesMax);
            doc.createElement("intervalForces", (int)(secondForces * 10.0));
            doc.createElement("maxforcesTime", (int)Math.ceil(forcesMax / (this.buildingOutputCache.getBuildingsOutput(playerId, 5) * 1.0)));
            doc.endObject();
        }
        return doc.toByte();
    }
    
    private byte[] oneGeneralCivilInfo(final PlayerGeneralCivil pgc) {
        return this.oneGeneralCivilInfo(pgc, false);
    }
    
    private byte[] oneGeneralCivilInfo(final PlayerGeneralCivil pgc, final boolean firstSend) {
        final Date nowDate = new Date();
        final JsonDocument doc = new JsonDocument();
        if (pgc != null) {
            doc.startObject();
            doc.createElement("firstSend", firstSend);
            final General general = (General)this.generalCache.get((Object)pgc.getGeneralId());
            doc.createElement("generalId", pgc.getGeneralId());
            doc.createElement("generalName", general.getName());
            doc.createElement("generalLv", pgc.getLv());
            doc.createElement("pic", general.getPic());
            doc.createElement("quality", general.getQuality());
            doc.createElement("output", this.civilOutputPerHour(pgc));
            final long time = CDUtil.getCD(pgc.getNextMoveTime(), nowDate);
            if (time > 0L) {
                doc.createElement("cState", 6);
                doc.createElement("cTime", time);
            }
            doc.endObject();
        }
        return doc.toByte();
    }
    
    private int civilOutputPerHour(final PlayerGeneralCivil pgc) {
        final General general = (General)this.generalCache.get((Object)pgc.getGeneralId());
        int sum = 0;
        if (general != null) {
            sum = pgc.getIntel(general.getIntel()) + pgc.getPolitics(general.getPolitics());
        }
        return (int)(((int)(Object)((C)this.cCache.get((Object)"Officer.OutputE")).getValue() * this.serialCache.get((int)(Object)((C)this.cCache.get((Object)"Officer.OutputS")).getValue(), pgc.getLv()) + 0) * (1.0 + 0.005 * sum));
    }
    
    private byte[] getGeneralMilitaryMaxSizeInfo(final int playerId, final int playerLv) {
        final JsonDocument doc = new JsonDocument();
        final int mSizeMax = this.dataGetter.getTavernService().getMaxGeneralNum(playerId, playerLv, 2);
        doc.createElement("mSizeMax", mSizeMax);
        if (mSizeMax < 5) {
            doc.createElement("mOpLv", ((GeneralPosition)this.generalPositionCache.get((Object)(mSizeMax + 1 + 5))).getOpenLv());
        }
        return doc.toByte();
    }
    
    private byte[] getGeneralCivilMaxSizeInfo(final int playerId, final int playerLv) {
        final JsonDocument doc = new JsonDocument();
        final int mSizeMax = this.dataGetter.getTavernService().getMaxGeneralNum(playerId, playerLv, 1);
        doc.createElement("cSizeMax", mSizeMax);
        if (mSizeMax < 5) {
            doc.createElement("cOpLv", ((GeneralPosition)this.generalPositionCache.get((Object)(mSizeMax + 1))).getOpenLv());
        }
        return doc.toByte();
    }
    
    private byte[] getGeneralMilitaryListInfo(final PlayerDto playerDto) {
        final JsonDocument doc = new JsonDocument();
        final PlayerAttribute pa = this.playerAttributeDao.read(playerDto.playerId);
        doc.createElement("token", pa.getRecruitToken());
        final Chargeitem ci = (Chargeitem)this.chargeitemCache.get((Object)13);
        doc.createElement("rCost", ci.getCost());
        doc.createElement("minutes", (int)(double)ci.getParam());
        final List<PlayerGeneralMilitary> pgmList = this.playerGeneralMilitaryDao.getMilitaryListOrder(playerDto.playerId);
        doc.createElement("mSize", pgmList.size());
        doc.startArray("military");
        for (int i = 0; i < pgmList.size(); ++i) {
            doc.appendJson(this.oneGeneralMilitaryInfo(pgmList.get(i), playerDto.forceId, playerDto.playerId));
        }
        doc.endArray();
        return doc.toByte();
    }
    
    @Transactional
    @Override
    public byte[] getCivils(final PlayerDto playerDto, final int gId) {
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        final int playerId = playerDto.playerId;
        final List<PlayerGeneralCivil> pgcList = this.playerGeneralCivilDao.getCivilList(playerId);
        doc.startArray("civils");
        for (final PlayerGeneralCivil pgc : pgcList) {
            if (pgc.getOwner() == gId) {
                doc.startObject();
                General general = (General)this.generalCache.get((Object)pgc.getGeneralId());
                final Stratagem stratagem = (Stratagem)this.stratagemCache.get((Object)general.getStratagemId());
                doc.createElement("cId", pgc.getGeneralId());
                doc.createElement("cLv", pgc.getLv());
                doc.createElement("cQuality", general.getQuality());
                doc.createElement("cName", general.getName());
                doc.createElement("cIntel", pgc.getIntel(general.getIntel()));
                doc.createElement("cIdentify", pgc.getIntel(general.getIntel()) / 2.0);
                doc.createElement("stratagemName", stratagem.getName());
                doc.createElement("stratagemLv", stratagem.getQuality());
                doc.createElement("stratagemIntro", stratagem.getIntro());
                if (pgc.getOwner() > 0) {
                    general = (General)this.generalCache.get((Object)pgc.getOwner());
                    doc.createElement("gName", general.getName());
                }
                doc.endObject();
                break;
            }
        }
        for (final PlayerGeneralCivil pgc : pgcList) {
            if (pgc.getOwner() == gId) {
                continue;
            }
            doc.startObject();
            General general = (General)this.generalCache.get((Object)pgc.getGeneralId());
            doc.createElement("cId", pgc.getGeneralId());
            doc.createElement("cLv", pgc.getLv());
            doc.createElement("cQuality", general.getQuality());
            doc.createElement("cName", general.getName());
            doc.createElement("cIntel", pgc.getIntel(general.getIntel()));
            doc.createElement("cIdentify", pgc.getIntel(general.getIntel()) / 2.0);
            if (pgc.getOwner() > 0) {
                general = (General)this.generalCache.get((Object)pgc.getOwner());
                doc.createElement("gName", general.getName());
            }
            doc.endObject();
        }
        doc.endArray();
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    public Set<Integer> getTaskList(final int playerId) {
        final List<PlayerTask> taskList = this.playerTaskDao.getDisPlayPlayerTask(playerId);
        GameTask gameTask = null;
        final Set<Integer> taskSet = new HashSet<Integer>();
        for (final PlayerTask playerTask : taskList) {
            final int taskType = playerTask.getType();
            if (taskType == 1) {
                gameTask = TaskFactory.getInstance().getTask(playerTask.getTaskId());
            }
            else {
                gameTask = TaskFactory.getInstance().getTask(playerTask.getGroupId(), playerTask.getTaskId(), taskType);
            }
            if (gameTask != null && gameTask.getTaskRequest().isConcernedMessage(new TaskMessageEquipOn(playerId))) {
                if (gameTask.getTaskRequest() instanceof TaskRequestAnd) {
                    final TaskRequestAnd request = (TaskRequestAnd)gameTask.getTaskRequest();
                    final List<ITaskRequest> list = request.getRequestList();
                    for (final ITaskRequest tr : list) {
                        if (!tr.isConcernedMessage(new TaskMessageEquipOn(playerId))) {
                            continue;
                        }
                        final TaskRequestEquipOn treo = (TaskRequestEquipOn)tr;
                        taskSet.add(treo.getType());
                    }
                }
                else if (gameTask.getTaskRequest() instanceof TaskRequestOr) {
                    final TaskRequestOr request2 = (TaskRequestOr)gameTask.getTaskRequest();
                    final List<ITaskRequest> list = request2.getRequestList();
                    for (final ITaskRequest tr : list) {
                        if (!tr.isConcernedMessage(new TaskMessageEquipOn(playerId))) {
                            continue;
                        }
                        final TaskRequestEquipOn treo = (TaskRequestEquipOn)tr;
                        taskSet.add(treo.getType());
                    }
                }
                else {
                    if (!(gameTask.getTaskRequest() instanceof TaskMessageEquipOn)) {
                        continue;
                    }
                    final TaskRequestEquipOn request3 = (TaskRequestEquipOn)gameTask.getTaskRequest();
                    taskSet.add(request3.getType());
                }
            }
        }
        return taskSet;
    }
    
    @Transactional
    @Override
    public byte[] getGeneralInfo(final PlayerDto playerDto) {
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        final int playerId = playerDto.playerId;
        List<StoreHouse> shList = this.storeHouseDao.getByType(playerId, 1);
        final List<StoreHouse> suitList = this.storeHouseDao.getByType(playerId, 10);
        final List<StoreHouse> prosetList = this.storeHouseDao.getByType(playerId, 14);
        final Map<Integer, StoreHouse> suitOwnerMap = new HashMap<Integer, StoreHouse>();
        if (shList == null && suitList != null) {
            shList = new ArrayList<StoreHouse>();
        }
        this.checkSuitStorehouse(shList, suitList, suitOwnerMap);
        this.checkProsetStorehouse(shList, prosetList, suitOwnerMap);
        final Map<Integer, List<StoreHouse>> shMap = new HashMap<Integer, List<StoreHouse>>();
        for (int i = 0; i < shList.size(); ++i) {
            final StoreHouse sh = shList.get(i);
            List<StoreHouse> list = shMap.get(sh.getGoodsType());
            if (list == null) {
                list = new ArrayList<StoreHouse>();
                shMap.put(sh.getGoodsType(), list);
            }
            list.add(sh);
        }
        final List<StoreHouse> gshList = this.storeHouseDao.getOwnerByType(playerId, 3);
        final PlayerAttribute pa = this.playerAttributeDao.read(playerId);
        doc.createElement("token", pa.getRecruitToken());
        final Chargeitem ci = (Chargeitem)this.chargeitemCache.get((Object)13);
        doc.createElement("rCost", ci.getCost());
        doc.createElement("minutes", (int)(double)ci.getParam());
        final Set<Integer> taskSet = this.getTaskList(playerId);
        final List<PlayerGeneralMilitary> pgmList = this.playerGeneralMilitaryDao.getMilitaryListOrder(playerId);
        doc.createElement("mSize", pgmList.size());
        final int mSizeMax = this.dataGetter.getTavernService().getMaxGeneralNum(playerId, playerDto.playerLv, 2);
        doc.createElement("mSizeMax", mSizeMax);
        if (mSizeMax < 5) {
            doc.createElement("mOpLv", ((GeneralPosition)this.generalPositionCache.get((Object)(mSizeMax + 1))).getOpenLv());
        }
        doc.startArray("military");
        final Date nowDate = new Date();
        Map<Integer, Integer> adhMap = new HashMap<Integer, Integer>();
        Tactic tactic = null;
        final int colum = this.dataGetter.getBattleDataCache().getColumNum(playerId);
        for (int j = 0; j < pgmList.size(); ++j) {
            final PlayerGeneralMilitary pgm = pgmList.get(j);
            final General gm = (General)this.generalCache.get((Object)pgm.getGeneralId());
            doc.startObject();
            final StoreHouse shStoreHouse = suitOwnerMap.get(gm.getId());
            if (shStoreHouse != null) {
                doc.createElement("equipWithSuit", true);
                final EquipCoordinates equipCoordinates = this.equipCache.getEquipCoordinateByItemId(shStoreHouse.getItemId());
                if (equipCoordinates != null) {
                    doc.createElement("rewardAtt", equipCoordinates.getAtt());
                    doc.createElement("rewardDef", equipCoordinates.getDef());
                    doc.createElement("rewardBlood", equipCoordinates.getBlood());
                    doc.createElement("suitPic", equipCoordinates.getPic());
                    doc.createElement("equipSuitName", equipCoordinates.getName());
                    doc.createElement("suitIntro", equipCoordinates.getIntro());
                }
                else {
                    final EquipProset equipProset = this.equipCache.getEquipProsetByItemId(shStoreHouse.getItemId());
                    if (equipProset != null) {
                        doc.createElement("rewardAtt", equipProset.getAtt());
                        doc.createElement("rewardDef", equipProset.getDef());
                        doc.createElement("rewardBlood", equipProset.getBlood());
                        doc.createElement("suitPic", equipProset.getPic());
                        doc.createElement("equipSuitName", equipProset.getName());
                        doc.createElement("suitIntro", equipProset.getIntro());
                    }
                }
            }
            else {
                doc.createElement("equipWithSuit", false);
            }
            doc.createElement("generalId", pgm.getGeneralId());
            doc.createElement("generalName", gm.getName());
            doc.createElement("generalLv", pgm.getLv());
            doc.createElement("treasureNum", this.getTreasureNum(pgm.getLv()));
            doc.createElement("quality", gm.getQuality());
            doc.createElement("tacticId", gm.getTacticId());
            doc.createElement("auto", pgm.getAuto());
            doc.createElement("gIntro", gm.getIntro());
            tactic = (Tactic)this.tacticCache.get((Object)gm.getTacticId());
            if (tactic != null) {
                doc.createElement("tacName", tactic.getName());
                doc.createElement("tacRange", tactic.getRange());
                doc.createElement("tacIntro", tactic.getIntro());
            }
            else {
                doc.createElement("tacName", "");
                doc.createElement("tacRange", "");
                doc.createElement("tacIntro", "");
            }
            doc.createElement("generalExp", pgm.getExp());
            final int generalExpMax = this.serialCache.get(((General)this.generalCache.get((Object)pgm.getGeneralId())).getUpExpS(), pgm.getLv());
            doc.createElement("generalExpMax", generalExpMax);
            doc.createElement("generalLocationId", pgm.getLocationId());
            doc.createElement("generalLocationName", ((WorldCity)this.worldCityCache.get((Object)pgm.getLocationId())).getName());
            doc.createElement("colum", colum);
            doc.createElement("juBenLoId", pgm.getJubenLoId());
            doc.createElement("inMain", WorldCityCommon.mainCityNationIdMap.containsKey(pgm.getLocationId()));
            final Troop troop = this.dataGetter.getTroopCache().getTroop(gm.getTroop(), pgm.getPlayerId());
            doc.createElement("troopId", troop.getType());
            doc.createElement("troopType", troop.getSerial());
            doc.createElement("troopLv", troop.getLevel());
            doc.createElement("troopName", troop.getName());
            doc.createElement("troopQuality", troop.getQuality());
            final GeneralMoveDto gmd = CityService.getGeneralMoveDto(playerId, pgm.getGeneralId());
            boolean autoMove = false;
            if (gmd != null) {
                long time = CDUtil.getCD(gmd.nextMoveTime, nowDate);
                if (time > 0L) {
                    doc.createElement("cTime", time);
                    doc.createElement("cState", 6);
                    if (gmd.moveLine.length() > 0) {
                        final String[] strs = gmd.moveLine.split(",");
                        final int endCityId = Integer.valueOf(strs[strs.length - 1]);
                        doc.createElement("endCityId", endCityId);
                        if (pgm.getLocationId() != endCityId) {
                            autoMove = true;
                        }
                    }
                }
                else if (gmd.cityState == 22) {
                    doc.createElement("cState", 22);
                }
                else if (gmd.cityState == 23) {
                    time = CDUtil.getCD(gmd.runawayTime, new Date());
                    if (time > 0L) {
                        doc.createElement("cTime", time);
                        doc.createElement("cState", 23);
                    }
                }
            }
            doc.createElement("autoMove", autoMove);
            doc.startArray("goods");
            int countGoods = 0;
            for (final StoreHouse gsh : gshList) {
                if (gsh.getOwner() == pgm.getGeneralId()) {
                    if (countGoods >= 1) {
                        break;
                    }
                    final String[] strs2 = gsh.getAttribute().split(",");
                    doc.startObject();
                    doc.createElement("vId", gsh.getVId());
                    doc.createElement("location", gsh.getLv());
                    final GeneralTreasure generalTreasure = (GeneralTreasure)this.generalTreasureCache.get((Object)gsh.getItemId());
                    doc.createElement("att1", strs2[0]);
                    doc.createElement("att2", strs2[1]);
                    doc.createElement("name", generalTreasure.getName());
                    doc.createElement("quality", generalTreasure.getQuality());
                    doc.createElement("pic", generalTreasure.getPic());
                    doc.endObject();
                    ++countGoods;
                }
            }
            doc.endArray();
            adhMap = this.battleDataCache.getAttDefHp(playerId, pgm.getGeneralId(), troop, pgm.getLv());
            final int forcesMax = adhMap.get(3);
            doc.createElement("forcesMax", forcesMax);
            doc.createElement("fMax", forcesMax / (colum * 3));
            doc.createElement("att", adhMap.get(1));
            doc.createElement("def", adhMap.get(2));
            if (pgm.getForces() > forcesMax) {
                doc.createElement("forces", forcesMax);
                this.playerGeneralMilitaryDao.resetForces(playerId, pgm.getGeneralId(), new Date(), forcesMax);
            }
            else {
                doc.createElement("forces", pgm.getForces());
            }
            doc.createElement("pic", gm.getPic());
            final General general = (General)this.generalCache.get((Object)pgm.getGeneralId());
            if (general != null) {
                doc.createElement("strength", pgm.getStrength(general.getStrength()));
                doc.createElement("leader", pgm.getLeader(general.getLeader()));
            }
            doc.createElement("speed", troop.getSpeed());
            if (troop.getTsstList() != null) {
                doc.startArray("tts");
                FightStrategies fs = null;
                for (final TerrainStrategySpecDto ts : troop.getTsstList()) {
                    doc.startObject();
                    doc.createElement("terrainId", ts.terrainId);
                    fs = (FightStrategies)this.fightStrategiesCache.get((Object)ts.strategyId);
                    doc.createElement("strategyId", ts.strategyId);
                    doc.createElement("sName", fs.getName());
                    doc.createElement("show", ts.show);
                    doc.endObject();
                }
                doc.endArray();
            }
            doc.startArray("terrain");
            for (final Integer key : troop.getTerrains().keySet()) {
                final TroopTerrain tt = troop.getTerrains().get(key);
                if (tt.getShow() != 0) {
                    if (tt.getShow() == 2 && tt.getDefEffect() > 0) {
                        doc.startObject();
                        doc.createElement("tType", key);
                        doc.createElement("tShow", tt.getShow());
                        doc.createElement("terrainQ", tt.getDefQuality());
                        doc.createElement("tValue", tt.getDefEffect());
                        doc.endObject();
                    }
                    else {
                        if (tt.getAttEffect() <= 0) {
                            continue;
                        }
                        doc.startObject();
                        doc.createElement("tType", key);
                        doc.createElement("tShow", tt.getShow());
                        doc.createElement("terrainQ", tt.getAttQuality());
                        doc.createElement("tValue", tt.getAttEffect());
                        doc.endObject();
                    }
                }
            }
            doc.endArray();
            int state = pgm.getState();
            final long needForces = forcesMax - pgm.getForces();
            final double secondForces = this.getOutput(pgm.getPlayerId(), playerDto.forceId, pgm.getLocationId(), troop);
            if (needForces > 0L) {
                long needTime = (long)(needForces / secondForces);
                final long timeed = (System.currentTimeMillis() - pgm.getUpdateForcesTime().getTime()) / 1000L;
                needTime = needTime - timeed + 10L;
                if (needTime < 0L) {
                    needTime = 10L;
                }
                final WorldCity worldCity = (WorldCity)this.worldCityCache.get((Object)pgm.getLocationId());
                final int consume = (int)(this.getRecuitConsume(troop.getId(), playerDto.forceId, worldCity) * needForces);
                doc.startArray("consume");
                doc.startObject();
                doc.createElement("inputType", 3);
                doc.createElement("inputValue", consume);
                doc.endObject();
                doc.endArray();
                doc.createElement("needTime", needTime * 1000L);
            }
            else {
                doc.createElement("needTime", 0);
                if (pgm.getState() == 1) {
                    state = 0;
                    pgm.setState(0);
                    this.playerGeneralMilitaryDao.updateState(pgm.getVId(), 0);
                }
            }
            if (state <= 1 && TeamManager.getInstance().isJoinTeam(pgm.getPlayerId(), pgm.getGeneralId())) {
                state = 16;
            }
            doc.createElement("intervalForces", (int)(secondForces * 10.0));
            doc.createElement("generalState", state);
            final Battle battle = NewBattleManager.getInstance().getBattleByGId(playerId, pgm.getGeneralId());
            if (battle != null) {
                doc.createElement("battleId", battle.getBattleId());
            }
            else {
                doc.createElement("battleId", "");
            }
            final long now = System.currentTimeMillis();
            doc.createElement("farmCd", (gmd == null) ? 0L : (gmd.farmtime - now));
            doc.appendJson(this.getEquipInfo(shMap, pgm.getGeneralId(), playerDto.playerLv, 1, 6, taskSet));
            final WorldFarmCache cache = WorldFarmCache.getInstatnce();
            final long value = cache.getBuffCdByPlayerId(playerId, pgm.getGeneralId());
            doc.createElement("buffCd", CDUtil.getCD(value, now));
            final Chargeitem item = (Chargeitem)this.chargeitemCache.get((Object)86);
            final double param = (item == null) ? 1.0 : item.getParam();
            final int gold = (item == null) ? 1 : item.getCost();
            doc.createElement("cdRecoverGold", gold / param);
            doc.endObject();
        }
        doc.endArray();
        doc.createElement("vipLimit", ci.getLv());
        final List<GeneralPosition> gpList = this.generalPositionCache.getMilitaryList();
        GeneralPosition gp = null;
        doc.startArray("lvs");
        for (int k = 0; k < gpList.size(); ++k) {
            doc.startObject();
            gp = gpList.get(k);
            doc.createElement("pos", k + 1);
            doc.createElement("intro", gp.getOpenIntro());
            doc.createElement("tips", gp.getOpenTips());
            doc.endObject();
        }
        doc.endArray();
        final StringC sc = (StringC)this.stringCCache.get((Object)9);
        final String[] lvs = sc.getValue().split(",");
        doc.startArray("gtLvs");
        for (int countGtLvs = 0, l = 0; l < lvs.length && countGtLvs < 1; ++countGtLvs, ++l) {
            doc.startObject();
            doc.createElement("pos", l + 1);
            doc.createElement("lv", Integer.parseInt(lvs[l]));
            doc.endObject();
        }
        doc.endArray();
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    private void checkProsetStorehouse(final List<StoreHouse> shList, final List<StoreHouse> prosetList, final Map<Integer, StoreHouse> suitOwnerMap) {
        try {
            if (prosetList == null || prosetList.isEmpty()) {
                return;
            }
            List<StoreHouseBak> storeHouseBaks = null;
            for (final StoreHouse storeHouse : prosetList) {
                if (storeHouse == null) {
                    continue;
                }
                storeHouseBaks = this.storeHouseBakDao.getBySuitIdAndIndex(storeHouse.getVId(), 0);
                for (final StoreHouseBak bak : storeHouseBaks) {
                    final StoreHouse toStore = new StoreHouse();
                    EquipService.copyProperties(toStore, bak);
                    toStore.setOwner(storeHouse.getOwner());
                    shList.add(toStore);
                }
                suitOwnerMap.put(storeHouse.getOwner(), storeHouse);
            }
        }
        catch (Exception e) {
            GeneralService.log.error(e.getMessage());
            GeneralService.log.error(this, e);
        }
    }
    
    private void checkSuitStorehouse(final List<StoreHouse> shList, final List<StoreHouse> suitList, final Map<Integer, StoreHouse> suitOwnerMap) {
        if (suitList == null || suitList.isEmpty()) {
            return;
        }
        List<StoreHouseBak> baks = null;
        for (final StoreHouse sh : suitList) {
            if (sh != null) {
                if (sh.getOwner() <= 0) {
                    continue;
                }
                baks = this.storeHouseBakDao.getListByStoreId(sh.getVId());
                if (baks == null) {
                    continue;
                }
                if (baks.isEmpty()) {
                    continue;
                }
                for (final StoreHouseBak bak : baks) {
                    final StoreHouse toStore = new StoreHouse();
                    EquipService.copyProperties(toStore, bak);
                    toStore.setOwner(sh.getOwner());
                    shList.add(toStore);
                }
                suitOwnerMap.put(sh.getOwner(), sh);
            }
        }
    }
    
    private byte[] getEquipInfo(final Map<Integer, List<StoreHouse>> shMap, final int generalId, final int playerLv, final int start, final int end, final Set<Integer> taskSet) {
        final JsonDocument doc = new JsonDocument();
        doc.startArray("equips");
        for (int i = start; i <= end; ++i) {
            final List<StoreHouse> list = shMap.get(i);
            doc.startObject();
            doc.createElement("type", i);
            int quality = 0;
            int eQ = 0;
            int eLv = 0;
            int lv = 0;
            if (list != null) {
                for (final StoreHouse sh : list) {
                    if (sh.getOwner() <= 0) {
                        if (quality < sh.getQuality()) {
                            quality = sh.getQuality();
                            lv = sh.getLv();
                        }
                        else if (quality == sh.getQuality()) {
                            lv = ((lv < sh.getLv()) ? sh.getLv() : lv);
                        }
                    }
                    if (sh.getOwner() == generalId) {
                        eQ = sh.getQuality();
                        eLv = sh.getLv();
                        final Equip equip = (Equip)this.equipCache.get((Object)sh.getItemId());
                        if (equip.getType() == 5 || equip.getType() == 6) {
                            doc.createElement("attribute", Integer.valueOf(sh.getAttribute()) / 3);
                        }
                        else {
                            doc.createElement("attribute", sh.getAttribute());
                        }
                        EquipCommon.getRefreshAttribute(sh.getRefreshAttribute(), doc, this.equipSkillCache, null, this.equipSkillEffectCache, equip);
                        EquipCommon.getMaxSkillAndLv(doc, equip, this.equipCache, sh.getSpecialSkillId(), sh.getRefreshAttribute());
                        doc.createElement("vId", sh.getVId());
                        doc.createElement("lv", sh.getLv());
                        doc.createElement("quality", sh.getQuality());
                        doc.createElement("suitName", this.equipSuitCache.getSuitName(sh.getItemId()));
                        doc.createElement("pic", equip.getPic());
                        doc.createElement("itemName", equip.getName());
                    }
                }
            }
            if ((quality > eQ || (quality == eQ && lv > eLv)) && taskSet.contains(i)) {
                doc.createElement("change", true);
            }
            else {
                doc.createElement("change", false);
            }
            doc.endObject();
        }
        doc.endArray();
        return doc.toByte();
    }
    
    @Transactional
    @Override
    public byte[] startRecruitForces(final PlayerDto playerDto, final int generalId) {
        final int playerId = playerDto.playerId;
        final PlayerGeneralMilitary pgm = this.playerGeneralMilitaryDao.getMilitary(playerId, generalId);
        if (pgm == null || pgm.getPlayerId() != playerId) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10012);
        }
        if (pgm.getState() >= 24) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.GENERAL_BUSY);
        }
        if (pgm.getState() > 1) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.GNERAL_BATTLE_NO_RECRUIT);
        }
        if (pgm.getState() == 1) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.GNERAL_FORCES_RECRUIT);
        }
        final int forcesMax = this.battleDataCache.getMaxHp(pgm);
        if (pgm.getForces() >= forcesMax) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.GNERAL_FORCES_IS_FULL);
        }
        this.playerGeneralMilitaryDao.updateStateAuto(pgm.getVId(), new Date(), 1, 1, 0);
        pgm.setState(1);
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("start", true);
        doc.endObject();
        TaskMessageHelper.sendRecruitForcesTaskMessage(playerId);
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Transactional
    @Override
    public byte[] stopRecruitForces(final PlayerDto playerDto, final int generalId, int auto) {
        final PlayerGeneralMilitary pgm = this.playerGeneralMilitaryDao.getMilitary(playerDto.playerId, generalId);
        if (pgm == null || pgm.getPlayerId() != playerDto.playerId) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10012);
        }
        if (auto < 0 || auto > 1) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        auto = 1;
        if (pgm.getState() != 1) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10012);
        }
        this.playerGeneralMilitaryDao.updateStateAuto(pgm.getVId(), new Date(), 0, auto, 1);
        pgm.setState(0);
        this.sendGeneralMilitaryRecruitInfo(playerDto.playerId, pgm);
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("stop", "true");
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Transactional
    @Override
    public byte[] fireGeneral(final int playerId, final int generalId, final int type) {
        final char[] cs = this.playerAttributeDao.getFunctionId(playerId).toCharArray();
        int result = 0;
        final PlayerGeneral pg = new PlayerGeneral();
        if (type == 2) {
            final PlayerGeneralMilitary pgm = this.playerGeneralMilitaryDao.getMilitary(playerId, generalId);
            if (pgm == null || pgm.getPlayerId() != playerId) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10012);
            }
            if (pgm.getState() != 0 && pgm.getState() != 1) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.GENERAL_BUSY);
            }
            if (cs[45] != '1') {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.GENERAL_CANNOT_FIRE);
            }
            if (TeamManager.getInstance().isJoinTeam(playerId, generalId)) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_TEAM_CONDITION_10033);
            }
            if (this.playerSlaveDao.isSlave2(playerId, generalId)) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.GENTEAL_IN_CELL);
            }
            StoreHouse generalTreasure = null;
            final int genrealWear = this.storeHouseDao.getGeneralEquipCount(playerId, pgm.getGeneralId());
            if (genrealWear > 0) {
                generalTreasure = this.storeHouseDao.getByGeneralIdAndType(playerId, generalId, 2, 3);
                final int usedSize = this.storeHouseDao.getCountByPlayerId(playerId);
                final int maxSize = this.playerAttributeDao.read(playerId).getMaxStoreNum();
                if (usedSize + genrealWear > maxSize) {
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.T_STORE_NUM_TOP_FIRE);
                }
                this.storeHouseDao.resetOwnerByGeneralId(playerId, pgm.getGeneralId());
                this.battleDataCache.refreshWeaponEffect(playerId);
                this.battleDataCache.removeEquipEffect(playerId, generalId);
            }
            pg.setPlayerId(playerId);
            pg.setGeneralId(pgm.getGeneralId());
            pg.setLv(pgm.getLv());
            pg.setExp(pgm.getExp());
            pg.setPolitics(0);
            pg.setIntel(0);
            final General general = (General)this.generalCache.get((Object)generalId);
            if (general == null) {
                pg.setLeader(0);
                pg.setStrength(0);
            }
            else {
                int leader = 0;
                int strength = 0;
                if (generalTreasure != null) {
                    final String attr = generalTreasure.getAttribute();
                    final String[] attrs = attr.split(",");
                    try {
                        leader = Integer.parseInt(attrs[0]);
                        strength = Integer.parseInt(attrs[1]);
                    }
                    catch (Exception e) {
                        GeneralService.log.error("GeneralService fireGeneral ", e);
                    }
                }
                pg.setLeader(pgm.getLeader() - leader);
                pg.setStrength(pgm.getStrength() - strength);
            }
            pg.setForces(0);
            pg.setType(2);
            result = this.playerGeneralMilitaryDao.deleteById(pgm.getVId());
            CityService.clearGeneralMove(playerId, generalId);
            TaskMessageHelper.sendGeneralLvTaskMessage(playerId, pgm.getLv());
        }
        else {
            final PlayerGeneralCivil pgc = this.playerGeneralCivilDao.getCivil(playerId, generalId);
            if (pgc == null || pgc.getPlayerId() != playerId) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10012);
            }
            if (pgc.getCd() != null && pgc.getCd().getTime() > new Date().getTime()) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.GENERAL_CIVIL_CANNOT_FIRE);
            }
            if (cs[44] != '1') {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.GENERAL_CIVIL_CANNOT_FIRE);
            }
            final int genrealWear2 = this.storeHouseDao.getGeneralEquipCount(playerId, pgc.getGeneralId());
            if (genrealWear2 > 0) {
                final int usedSize2 = this.storeHouseDao.getCountByPlayerId(playerId);
                final int maxSize2 = this.playerAttributeDao.read(playerId).getMaxStoreNum();
                if (usedSize2 + genrealWear2 > maxSize2) {
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.T_STORE_NUM_TOP_FIRE);
                }
                this.storeHouseDao.resetOwnerByGeneralId(playerId, pgc.getGeneralId());
            }
            pg.setPlayerId(playerId);
            pg.setGeneralId(pgc.getGeneralId());
            pg.setLv(pgc.getLv());
            pg.setExp(pgc.getExp());
            pg.setIntel(pgc.getIntel());
            pg.setPolitics(pgc.getPolitics());
            pg.setLeader(0);
            pg.setStrength(0);
            pg.setForces(0);
            pg.setType(1);
            result = this.playerGeneralCivilDao.deleteById(pgc.getVId());
        }
        if (result != 1) {
            return JsonBuilder.getJson(State.FAIL, "fail");
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        try {
            if (type == 1) {
                doc.startArray("stratagem");
                for (final PlayerGeneralCivil pgc2 : this.playerGeneralCivilDao.getCivilList(playerId)) {
                    doc.startObject();
                    final General general = (General)this.generalCache.get((Object)pgc2.getGeneralId());
                    final Stratagem stratagem = (Stratagem)this.stratagemCache.get((Object)general.getStratagemId());
                    if (stratagem != null) {
                        TrickFactory.getTrickInfo(doc, stratagem);
                        doc.createElement("stratagemId", stratagem.getId());
                        doc.createElement("stratagemIntro", stratagem.getIntro());
                    }
                    doc.createElement("pic", general.getPic());
                    doc.createElement("cilvilId", general.getId());
                    if (pgc2.getCd() != null) {
                        final long cd = pgc2.getCd().getTime() - new Date().getTime();
                        doc.createElement("cd", (cd > 0L) ? cd : 0L);
                    }
                    doc.endObject();
                }
                doc.endArray();
            }
        }
        catch (Exception e2) {
            GeneralService.log.error(this, e2);
        }
        doc.endObject();
        TaskMessageHelper.sendFireGeneralMessage(playerId);
        if (this.playerGeneralDao.getPlayerGeneral(playerId, generalId) != null) {
            this.playerGeneralDao.deleteByPlayerIdAndGeneralId(playerId, generalId);
        }
        this.playerGeneralDao.create(pg);
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Transactional
    @Override
    public byte[] getGeneral(final int playerId, final int type) {
        final List<PlayerGeneral> pgList = this.playerGeneralDao.getGeneralListByType(playerId, type);
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.startArray("generals");
        for (int i = 0; i < pgList.size(); ++i) {
            final PlayerGeneral pg = pgList.get(i);
            final General general = (General)this.generalCache.get((Object)pg.getGeneralId());
            doc.startObject();
            doc.createElement("generalId", pg.getGeneralId());
            doc.createElement("generalName", general.getName());
            doc.createElement("generalLv", pg.getLv());
            doc.endObject();
        }
        doc.endArray();
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Transactional
    @Override
    public List<UpdateExp> updateExpAndGeneralLevel(final int playerId, final int generalId, final int addExp) {
        final List<UpdateExp> upList = new ArrayList<UpdateExp>();
        Label_0720: {
            try {
                GeneralService.locks[playerId % GeneralService.LOCKS_LEN].lock();
                final PlayerGeneralMilitary pgm = this.playerGeneralMilitaryDao.getMilitary(playerId, generalId);
                if (pgm == null) {
                    return null;
                }
                final int gFactor = ((General)this.generalCache.get((Object)generalId)).getUpExpS();
                int upNum = this.serialCache.get(gFactor, pgm.getLv());
                long curExp = pgm.getExp();
                UpdateExp uep = null;
                final Player player = this.playerDao.read(playerId);
                int upLv = 0;
                curExp += addExp;
                while (true) {
                    while (curExp >= upNum) {
                        ++upLv;
                        if (pgm.getLv() + upLv > player.getPlayerLv()) {
                            if (--upLv == 0) {
                                uep = new UpdateExp(pgm.getLv(), pgm.getExp(), upNum - pgm.getExp(), upNum);
                                curExp = upNum - pgm.getExp();
                            }
                            else {
                                uep = new UpdateExp(pgm.getLv() + upLv, 0L, upNum, upNum);
                                curExp = upNum;
                            }
                            upList.add(uep);
                            if (upLv > 0) {
                                final General general = (General)this.generalCache.get((Object)pgm.getGeneralId());
                                final Troop troop = this.troopCache.getTroop(general.getTroop(), player.getPlayerId());
                                final Map<Integer, Integer> adhMap = this.battleDataCache.getAttDefHp(playerId, pgm.getGeneralId(), troop, pgm.getLv() + upLv);
                                this.playerGeneralMilitaryDao.updateExpAndGlv(playerId, generalId, (int)curExp, upLv);
                                TaskMessageHelper.sendAttTaskMessage(playerId, adhMap.get(1));
                                TaskMessageHelper.sendDefTaskMessage(playerId, adhMap.get(2));
                                TaskMessageHelper.sendBloodTaskMessage(playerId, adhMap.get(3));
                                TaskMessageHelper.sendGeneralLvTaskMessage(playerId, pgm.getLv() + upLv);
                                break Label_0720;
                            }
                            this.playerGeneralMilitaryDao.addExp(playerId, generalId, (int)curExp);
                            break Label_0720;
                        }
                        else {
                            curExp -= upNum;
                            if (upLv == 1) {
                                uep = new UpdateExp(pgm.getLv() + upLv - 1, pgm.getExp(), upNum - pgm.getExp(), upNum);
                            }
                            else {
                                uep = new UpdateExp(pgm.getLv() + upLv - 1, 0L, upNum, upNum);
                            }
                            upList.add(uep);
                            upNum = this.serialCache.get(gFactor, pgm.getLv() + upLv);
                        }
                    }
                    if (upLv == 0) {
                        uep = new UpdateExp(pgm.getLv(), pgm.getExp(), addExp, upNum);
                        curExp = addExp;
                    }
                    else {
                        uep = new UpdateExp(pgm.getLv() + upLv, 0L, curExp, upNum);
                    }
                    upList.add(uep);
                    continue;
                }
            }
            catch (Exception e) {
                GeneralService.log.debug("updateExpAndPlayerLevel building exception", e);
                return upList;
            }
            finally {
                GeneralService.locks[playerId % GeneralService.LOCKS_LEN].unlock();
            }
        }
        GeneralService.locks[playerId % GeneralService.LOCKS_LEN].unlock();
        return upList;
    }
    
    @Transactional
    @Override
    public byte[] cdRecoverConfirm(final PlayerDto playerDto, final int generalId, final int mubingType) {
        final int playerId = playerDto.playerId;
        final Player player = this.playerDao.read(playerId);
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        final PlayerAttribute pa = this.playerAttributeDao.read(playerId);
        final PlayerGeneralMilitary pgm = this.playerGeneralMilitaryDao.getMilitary(playerId, generalId);
        if (pgm == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10012);
        }
        if (pgm.getState() > 24) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.GENERAL_BUSY);
        }
        if (pgm.getState() > 1) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.BATTLE_CANNOT_USE_RECRUIT_TOKEN);
        }
        final int forcesMax = this.battleDataCache.getMaxHp(pgm);
        if (pgm.getForces() >= forcesMax) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.GNERAL_RECRUIT_NOT_NEED);
        }
        boolean isInSoloDrama = false;
        final JuBenDto juBenDto = JuBenManager.getInstance().getByPid(playerId);
        if (juBenDto != null && !this.dataGetter.getJuBenService().isInWorldDrama(playerId)) {
            isInSoloDrama = true;
        }
        final General general = (General)this.generalCache.get((Object)pgm.getGeneralId());
        final Troop troop = this.troopCache.getTroop(general.getTroop(), player.getPlayerId());
        final long time = (System.currentTimeMillis() - pgm.getUpdateForcesTime().getTime()) / 1000L;
        final double secondForces = this.getOutput(pgm.getPlayerId(), player.getForceId(), pgm.getLocationId(), troop);
        long output = (long)(time * secondForces);
        final Chargeitem ci = (Chargeitem)this.chargeitemCache.get((Object)13);
        final long canAddedForces = (long)(this.getOutput(playerDto.playerId, playerDto.forceId, pgm.getLocationId(), troop) * ci.getParam() * 60.0);
        long needForces = forcesMax - pgm.getForces();
        if (needForces < output) {
            output = needForces;
        }
        int num = (int)((needForces - output) / canAddedForces) + (((needForces - output) % canAddedForces != 0L) ? 1 : 0);
        boolean full = true;
        if (pa.getRecruitToken() > 0 && pa.getRecruitToken() < num && !isInSoloDrama) {
            num = pa.getRecruitToken();
            if (num * canAddedForces < needForces) {
                needForces = num * canAddedForces;
                full = false;
            }
        }
        final WorldCity worldCity = (WorldCity)this.worldCityCache.get((Object)pgm.getLocationId());
        final double consume = needForces * this.getRecuitConsume(troop.getId(), player.getForceId(), worldCity);
        final PlayerResource pr = this.playerResourceDao.read(playerId);
        if (pr.getFood() < consume && !isInSoloDrama) {
            this.playerResourceDao.pushIncenseData(playerId, 3);
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10021);
        }
        if (pa.getRecruitToken() <= 0 && mubingType == 1) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.GNERAL_RECRUIT_NO_TOKEN);
        }
        boolean consumeGold = false;
        if (!isInSoloDrama) {
            if (pa.getRecruitToken() <= 0) {
                consumeGold = true;
                if (player.getConsumeLv() < ci.getLv()) {
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10002);
                }
                if (!this.playerDao.canConsumeMoney(player, ci.getCost() * num)) {
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10003);
                }
                if (!this.playerResourceDao.consumeFood(playerId, (int)consume, "\u52df\u5175\u6d88\u8017\u8d44\u6e90")) {
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10021);
                }
                if (!this.playerDao.consumeGold(player, ci.getCost() * num, ci.getName())) {
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10003);
                }
                doc.createElement("costGold", ci.getCost() * num);
            }
            else {
                this.playerAttributeDao.updateRecruitToken(playerId, num, "\u52df\u5175\u6d88\u8017\u52df\u5175\u4ee4");
                if (!this.playerResourceDao.consumeFood(playerId, (int)consume, "\u52df\u5175\u6d88\u8017\u8d44\u6e90")) {
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10021);
                }
                doc.createElement("consumeToken", num);
            }
        }
        else {
            doc.createElement("consumeToken", 0);
        }
        if (consume > 0.0) {
            EventListener.fireEvent(new CommonEvent(8, playerId));
        }
        if (full) {
            this.playerGeneralMilitaryDao.addGeneralForces(playerId, generalId, new Date(), 0, needForces);
            TaskMessageHelper.sendFullBloodTaskMessage(playerId);
        }
        else {
            this.playerGeneralMilitaryDao.addGeneralForces2(playerId, generalId, needForces);
        }
        if (!consumeGold) {
            doc.createElement("token", pa.getRecruitToken());
        }
        doc.endObject();
        this.dataGetter.getIndividualTaskService().sendTaskMessage(playerDto, 1, "mubing");
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public byte[] getCivilInfo(final PlayerDto playerDto, int generalId) {
        final Date nowDate = new Date();
        final int playerId = playerDto.playerId;
        final List<PlayerGeneralCivil> pgcList = this.playerGeneralCivilDao.getCivilListOrderByLv(playerId);
        PlayerGeneralCivil pgc = null;
        if (generalId == 0 && !pgcList.isEmpty()) {
            pgc = pgcList.get(0);
            generalId = pgc.getGeneralId();
        }
        else {
            pgc = this.playerGeneralCivilDao.getCivil(playerId, generalId);
        }
        final List<StoreHouse> shList = this.storeHouseDao.getByType(playerId, 1);
        final Map<Integer, List<StoreHouse>> shMap = new HashMap<Integer, List<StoreHouse>>();
        for (int i = 0; i < shList.size(); ++i) {
            final StoreHouse sh = shList.get(i);
            List<StoreHouse> list = shMap.get(sh.getGoodsType());
            if (list == null) {
                list = new ArrayList<StoreHouse>();
                shMap.put(sh.getGoodsType(), list);
            }
            list.add(sh);
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("cSize", pgcList.size());
        final int mSizeMax = this.dataGetter.getTavernService().getMaxGeneralNum(playerId, playerDto.playerLv, 1);
        doc.createElement("cSizeMax", mSizeMax);
        if (mSizeMax < 5) {
            doc.createElement("cOpLv", ((GeneralPosition)this.generalPositionCache.get((Object)(mSizeMax + 1 + 5))).getOpenLv());
        }
        if (pgc == null) {
            doc.endObject();
            return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
        }
        final General general = (General)this.generalCache.get((Object)pgc.getGeneralId());
        doc.createElement("generalId", pgc.getGeneralId());
        doc.createElement("generalName", general.getName());
        doc.createElement("generalLv", pgc.getLv());
        doc.createElement("quality", general.getQuality());
        doc.createElement("generalExp", pgc.getExp());
        final int gFactor = ((General)this.generalCache.get((Object)generalId)).getUpExpS();
        final int upNum = this.serialCache.get(gFactor, pgc.getLv());
        doc.createElement("generalExpMax", upNum);
        doc.createElement("politics", pgc.getPolitics(general.getPolitics()));
        doc.createElement("intel", pgc.getIntel(general.getIntel()));
        doc.createElement("pic", general.getPic());
        doc.createElement("output", this.civilOutputPerHour(pgc));
        final OfficerSpecialty specialty = (OfficerSpecialty)this.officerSpecialtyCache.get((Object)general.getTacticId());
        if (specialty != null) {
            doc.createElement("specialtyName", specialty.getName());
            doc.createElement("specialtyIntro", specialty.getIntro());
        }
        else {
            doc.createElement("specialtyName", "");
            doc.createElement("specialtyIntro", "");
        }
        final long time = CDUtil.getCD(pgc.getNextMoveTime(), nowDate);
        if (time > 0L) {
            doc.createElement("cState", 6);
            doc.createElement("cTime", time);
        }
        doc.createElement("affairNum", general.getQuality());
        final Set<Integer> taskSet = this.getTaskList(playerId);
        doc.appendJson(this.getEquipInfo(shMap, pgc.getGeneralId(), playerDto.playerLv, 7, 12, taskSet));
        doc.appendJson(this.getPlayerSimpleInfo(pgcList, mSizeMax));
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    private byte[] getPlayerSimpleInfo(final List<PlayerGeneralCivil> list, int maxCivilNum) {
        final JsonDocument doc = new JsonDocument();
        doc.startArray("generals");
        for (final PlayerGeneralCivil pgc : list) {
            final General general = (General)this.generalCache.get((Object)pgc.getGeneralId());
            final Stratagem stratagem = (Stratagem)this.stratagemCache.get((Object)general.getStratagemId());
            doc.startObject();
            doc.createElement("generalId", general.getId());
            doc.createElement("generalName", general.getName());
            doc.createElement("generalPic", general.getPic());
            doc.createElement("quality", general.getQuality());
            doc.createElement("lv", pgc.getLv());
            doc.createElement("open", maxCivilNum > 0);
            --maxCivilNum;
            doc.createElement("generalExp", pgc.getExp());
            final int gFactor = general.getUpExpS();
            final int upNum = this.serialCache.get(gFactor, pgc.getLv());
            doc.createElement("generalExpMax", upNum);
            doc.createElement("politics", pgc.getPolitics(general.getPolitics()));
            doc.createElement("intel", pgc.getIntel(general.getIntel()));
            doc.createElement("pic", general.getPic());
            if (stratagem != null) {
                doc.createElement("stratagemName", stratagem.getName());
                doc.createElement("stratagemLv", stratagem.getQuality());
                doc.createElement("stratagemIntro", stratagem.getIntro());
            }
            doc.createElement("output", this.civilOutputPerHour(pgc));
            final OfficerSpecialty specialty = (OfficerSpecialty)this.officerSpecialtyCache.get((Object)general.getTacticId());
            if (specialty != null) {
                doc.createElement("specialtyName", specialty.getName());
                doc.createElement("specialtyIntro", specialty.getIntro());
            }
            else {
                doc.createElement("specialtyName", "");
                doc.createElement("specialtyIntro", "");
            }
            doc.endObject();
        }
        doc.endArray();
        return doc.toByte();
    }
    
    @Override
    public byte[] cdRecover(final int playerId, final int generalId) {
        final Chargeitem ci = (Chargeitem)this.chargeitemCache.get((Object)13);
        final Player player = this.playerDao.read(playerId);
        if (ci.getLv() > player.getConsumeLv()) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10002);
        }
        final PlayerGeneralMilitary pgm = this.playerGeneralMilitaryDao.getMilitary(playerId, generalId);
        if (pgm == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10012);
        }
        if (pgm.getState() > 1) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.BATTLE_CANNOT_USE_RECRUIT_TOKEN);
        }
        final int forcesMax = this.battleDataCache.getMaxHp(pgm);
        if (pgm.getForces() >= forcesMax) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.GNERAL_RECRUIT_NOT_NEED);
        }
        final General general = (General)this.generalCache.get((Object)pgm.getGeneralId());
        final Troop troop = this.troopCache.getTroop(general.getTroop(), player.getPlayerId());
        final long time = (System.currentTimeMillis() - pgm.getUpdateForcesTime().getTime()) / 1000L;
        final double secondForces = this.getOutput(pgm.getPlayerId(), player.getForceId(), pgm.getLocationId(), troop);
        long output = (long)(time * secondForces);
        final long canAddedForces = (long)(this.getOutput(playerId, player.getForceId(), pgm.getLocationId(), troop) * ci.getParam() * 60.0);
        final long needForces = forcesMax - pgm.getForces();
        if (needForces < output) {
            output = needForces;
        }
        final int num = (int)((needForces - output) / canAddedForces) + (((needForces - output) % canAddedForces != 0L) ? 1 : 0);
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("gold", ci.getCost() * num);
        doc.createElement("minutes", (int)(double)ci.getParam() * num);
        final PlayerAttribute pa = this.playerAttributeDao.read(playerId);
        doc.createElement("token", pa.getRecruitToken());
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public byte[] autoRecruit(final int playerId, final int generalId, final int auto) {
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        if (auto < 0 || auto > 1) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10015);
        }
        final int result = this.playerGeneralMilitaryDao.updateAuto(playerId, generalId, auto);
        if (result <= 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Override
    public byte[] getGeneralTreasureInfo(final int playerId, final int generalId, final int location, final int type) {
        if (generalId <= 0 || location <= 0 || location > 3 || type < 1 || type > 2) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final Tuple<Boolean, Tuple<Integer, byte[]>> tuple = this.match(playerId, generalId, type);
        if (!(boolean)tuple.left) {
            return (byte[])((Tuple)tuple.right).right;
        }
        final List<StoreHouse> shList = this.storeHouseDao.getGeneralTreasureByType(playerId, type);
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.startArray("generalTreasures");
        String[] strs = null;
        GeneralTreasure gt = null;
        for (final StoreHouse sh : shList) {
            if (sh.getLv() == location && sh.getOwner() == generalId) {
                doc.startObject();
                doc.createElement("has", 1);
                doc.createElement("vId", sh.getVId());
                gt = (GeneralTreasure)this.generalTreasureCache.get((Object)sh.getItemId());
                doc.createElement("name", gt.getName());
                doc.createElement("type", sh.getGoodsType());
                strs = sh.getAttribute().split(",");
                doc.createElement("att1", strs[0]);
                doc.createElement("att2", strs[1]);
                doc.createElement("pic", gt.getPic());
                doc.createElement("quality", gt.getQuality());
                doc.endObject();
                break;
            }
        }
        for (final StoreHouse sh : shList) {
            if (sh.getOwner() > 0) {
                continue;
            }
            doc.startObject();
            doc.createElement("has", 0);
            doc.createElement("vId", sh.getVId());
            gt = (GeneralTreasure)this.generalTreasureCache.get((Object)sh.getItemId());
            doc.createElement("name", gt.getName());
            doc.createElement("type", sh.getGoodsType());
            strs = sh.getAttribute().split(",");
            doc.createElement("att1", strs[0]);
            doc.createElement("att2", strs[1]);
            doc.createElement("pic", gt.getPic());
            doc.createElement("quality", gt.getQuality());
            doc.endObject();
        }
        doc.endArray();
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Transactional
    @Override
    public byte[] changeGeneralTreasure(final int playerId, final int generalId, final int vId, final int location, final int type) {
        if (generalId < 1 || vId < 1 || location < 1 || location > 3 || type < 1 || type > 2) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final Tuple<Boolean, Tuple<Integer, byte[]>> tuple = this.match(playerId, generalId, type);
        if (!(boolean)tuple.left) {
            return (byte[])((Tuple)tuple.right).right;
        }
        if ((int)((Tuple)tuple.right).left < this.getOpenGeneralTreasureLevel(location)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.GENERAL_TREASURE_NOTOPEN);
        }
        final List<StoreHouse> shList = this.storeHouseDao.getGeneralTreasureByType(playerId, type);
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        final String[] temp = { "0", "0" };
        for (final StoreHouse sh : shList) {
            if (generalId == sh.getOwner() && location == sh.getLv()) {
                if (vId == sh.getVId()) {
                    final int usedSize = this.storeHouseDao.getCountByPlayerId(playerId);
                    final int maxSize = this.playerAttributeDao.read(playerId).getMaxStoreNum();
                    if (usedSize + 1 > maxSize) {
                        return JsonBuilder.getJson(State.FAIL, LocalMessages.GENERAL_TREASURE_NO_STORE_NUM_ERROR);
                    }
                    this.storeHouseDao.resetGeneralTreasure(vId);
                    this.consumeGeneralAttributeValue(playerId, generalId, sh.getAttribute(), type);
                    this.getReturnString(sh.getAttribute().split(","), temp, doc);
                }
                else {
                    final StoreHouse sh2 = this.storeHouseDao.read(vId);
                    if (sh2 == null || 3 != sh2.getType() || playerId != sh2.getPlayerId() || sh2.getOwner() > 0 || sh2.getGoodsType() != type || sh2.getState() > 0) {
                        return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
                    }
                    this.storeHouseDao.resetGeneralTreasure(sh.getVId());
                    this.consumeGeneralAttributeValue(playerId, generalId, sh.getAttribute(), type);
                    this.storeHouseDao.changeGeneralTreasure(vId, generalId, location);
                    this.addGeneralAttributeValue(playerId, generalId, sh2.getAttribute(), type);
                    this.getReturnString(sh.getAttribute().split(","), sh2.getAttribute().split(","), doc);
                }
                doc.endObject();
                return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
            }
        }
        StoreHouse sh = this.storeHouseDao.read(vId);
        if (sh == null || 3 != sh.getType() || playerId != sh.getPlayerId() || sh.getOwner() > 0 || sh.getGoodsType() != type || sh.getState() > 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        this.storeHouseDao.changeGeneralTreasure(vId, generalId, location);
        this.addGeneralAttributeValue(playerId, generalId, sh.getAttribute(), type);
        this.getReturnString(temp, sh.getAttribute().split(","), doc);
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Transactional
    @Override
    public byte[] changAllEquip(final PlayerDto playerDto, final int orgGeneralId, final int nowGeneralId) {
        final int playerId = playerDto.playerId;
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        final PlayerGeneralMilitary orgPgm = this.playerGeneralMilitaryDao.getMilitary(playerId, orgGeneralId);
        final PlayerGeneralMilitary nowPgm = this.playerGeneralMilitaryDao.getMilitary(playerId, nowGeneralId);
        if (orgPgm == null || nowPgm == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final long orgHp1 = orgPgm.getForces();
        final long orgHp2 = nowPgm.getForces();
        final StoreHouse orgSuit = this.storeHouseDao.getByGeneralIdAndType(playerId, orgGeneralId, 10, 10);
        final StoreHouse nowSuit = this.storeHouseDao.getByGeneralIdAndType(playerId, nowGeneralId, 10, 10);
        final StoreHouse orgProset = this.storeHouseDao.getByGeneralIdAndType(playerId, orgGeneralId, 14, 14);
        final StoreHouse nowProset = this.storeHouseDao.getByGeneralIdAndType(playerId, nowGeneralId, 14, 14);
        int typeOrg = 1;
        int typeNow = 1;
        if (orgSuit != null) {
            typeOrg = 10;
        }
        if (orgProset != null) {
            typeOrg = 14;
        }
        if (nowSuit != null) {
            typeNow = 10;
        }
        if (nowProset != null) {
            typeNow = 14;
        }
        this.storeHouseDao.updateGeneralEquip(playerId, orgPgm.getGeneralId(), -1, typeOrg);
        this.storeHouseDao.updateGeneralEquip(playerId, nowPgm.getGeneralId(), orgPgm.getGeneralId(), typeNow);
        this.storeHouseDao.updateGeneralEquip(playerId, -1, nowPgm.getGeneralId(), typeOrg);
        this.battleDataCache.removeEquipEffect(playerId, orgPgm.getGeneralId());
        this.battleDataCache.removeEquipEffect(playerId, nowPgm.getGeneralId());
        final long nowHp1 = this.battleDataCache.getMaxHp(orgPgm);
        final long nowHp2 = this.battleDataCache.getMaxHp(nowPgm);
        if (nowHp1 < orgHp1) {
            this.playerGeneralMilitaryDao.resetForces(playerId, orgPgm.getGeneralId(), new Date(), nowHp1);
        }
        else if (nowHp1 > orgHp1) {
            this.playerGeneralMilitaryDao.updateAutoRecruit(orgPgm.getPlayerId(), orgPgm.getGeneralId());
        }
        if (nowHp2 < orgHp2) {
            this.playerGeneralMilitaryDao.resetForces(playerId, nowPgm.getGeneralId(), new Date(), nowHp2);
        }
        else if (nowHp2 > orgHp2) {
            this.playerGeneralMilitaryDao.updateAutoRecruit(nowPgm.getPlayerId(), nowPgm.getGeneralId());
        }
        TaskMessageHelper.sendWearEquipTaskMessage(playerId);
        TaskMessageHelper.sendEquipOnTaskMessage(playerId);
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    private Tuple<Boolean, Tuple<Integer, byte[]>> match(final int playerId, final int generalId, final int type) {
        final Tuple<Boolean, Tuple<Integer, byte[]>> tuple = new Tuple();
        tuple.left = false;
        tuple.right = new Tuple();
        ((Tuple)tuple.right).left = 0;
        if (type == 1) {
            final PlayerGeneralCivil pgc = this.playerGeneralCivilDao.getCivil(playerId, generalId);
            if (pgc == null) {
                ((Tuple)tuple.right).right = JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
            }
            else {
                tuple.left = true;
                ((Tuple)tuple.right).left = pgc.getLv();
            }
        }
        else {
            final PlayerGeneralMilitary pgm = this.playerGeneralMilitaryDao.getMilitary(playerId, generalId);
            if (pgm == null) {
                ((Tuple)tuple.right).right = JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
            }
            else {
                tuple.left = true;
                ((Tuple)tuple.right).left = pgm.getLv();
            }
        }
        return tuple;
    }
    
    private void addGeneralAttributeValue(final int playerId, final int generalId, final String attribute, final int type) {
        final String[] att = attribute.split(",");
        if (type == 1) {
            final PlayerGeneralCivil pgc = this.playerGeneralCivilDao.getCivil(playerId, generalId);
            if (pgc != null && 2 == att.length) {
                this.playerGeneralCivilDao.addIntelAndPolitics(playerId, generalId, Integer.parseInt(att[0]), Integer.parseInt(att[1]));
            }
        }
        else {
            final PlayerGeneralMilitary pgm = this.playerGeneralMilitaryDao.getMilitary(playerId, generalId);
            if (pgm != null && 2 == att.length) {
                this.playerGeneralMilitaryDao.addLeaderAndStrength(playerId, generalId, Integer.parseInt(att[0]), Integer.parseInt(att[1]));
            }
        }
    }
    
    private void consumeGeneralAttributeValue(final int playerId, final int generalId, final String attribute, final int type) {
        final String[] att = attribute.split(",");
        if (type == 1) {
            final PlayerGeneralCivil pgc = this.playerGeneralCivilDao.getCivil(playerId, generalId);
            if (pgc != null && 2 == att.length) {
                this.playerGeneralCivilDao.consumeIntelAndPolitics(playerId, generalId, Integer.parseInt(att[0]), Integer.parseInt(att[1]));
            }
        }
        else {
            final PlayerGeneralMilitary pgm = this.playerGeneralMilitaryDao.getMilitary(playerId, generalId);
            if (pgm != null && 2 == att.length) {
                this.playerGeneralMilitaryDao.consumeLeaderAndStrength(playerId, generalId, Integer.parseInt(att[0]), Integer.parseInt(att[1]));
            }
        }
    }
    
    private int getOpenGeneralTreasureLevel(final int location) {
        final StringC sc = (StringC)this.stringCCache.get((Object)9);
        final String[] lvs = sc.getValue().split(",");
        if (lvs.length < 3) {
            return 9999;
        }
        switch (location) {
            case 1: {
                return Integer.parseInt(lvs[0]);
            }
            case 2: {
                return Integer.parseInt(lvs[1]);
            }
            default: {
                return Integer.parseInt(lvs[2]);
            }
        }
    }
    
    private int getTreasureNum(final int lv) {
        final StringC sc = (StringC)this.stringCCache.get((Object)9);
        final String[] lvs = sc.getValue().split(",");
        if (lvs.length < 3) {
            return 0;
        }
        if (lv < Integer.parseInt(lvs[0])) {
            return 0;
        }
        if (lv < Integer.parseInt(lvs[1])) {
            return 1;
        }
        if (lv < Integer.parseInt(lvs[2])) {
            return 1;
        }
        return 1;
    }
    
    private void getReturnString(final String[] att, final String[] newatt, final JsonDocument doc) {
        final int t1 = Integer.valueOf(newatt[0]) - Integer.valueOf(att[0]);
        final int t2 = Integer.valueOf(newatt[1]) - Integer.valueOf(att[1]);
        doc.createElement("att1", t1);
        doc.createElement("att2", t2);
    }
    
    @Override
    public double getRecuitConsume(final int troopId, final int forceId, final WorldCity worldCity) {
        final TroopConscribe troopConscribe = (TroopConscribe)this.troopConscribeCache.get((Object)troopId);
        double comsume = (troopConscribe == null) ? 1.0 : troopConscribe.getFood();
        comsume = troopConscribe.getFood() * (1.0f + ((C)this.cCache.get((Object)"World.TroopConscribe.Consume.E")).getValue() * worldCity.getDistance(forceId));
        return comsume;
    }
    
    @Override
    public void getGeneralPortait(final int playerId, final JsonDocument doc, final Map<Integer, Boolean> hasGotMap) {
        doc.startArray("generalInfo");
        final List<PlayerGeneralMilitary> list = this.playerGeneralMilitaryDao.getMilitaryList(playerId);
        for (final PlayerGeneralMilitary pgm : list) {
            doc.startObject();
            final General general = (General)this.generalCache.get((Object)pgm.getGeneralId());
            doc.createElement("generalName", general.getName());
            doc.createElement("generalPic", general.getPic());
            doc.createElement("generalLv", pgm.getLv());
            doc.createElement("generalQuality", general.getQuality());
            final Troop troop = this.dataGetter.getTroopCache().getTroop(general.getTroop(), pgm.getPlayerId());
            doc.createElement("troopId", troop.getType());
            doc.createElement("troopType", troop.getSerial());
            doc.createElement("generalId", general.getId());
            final boolean isInTeam = TeamManager.getInstance().isJoinTeam2(pgm.getPlayerId(), pgm.getGeneralId());
            doc.createElement("isInTeam", isInTeam);
            final Map<Integer, Integer> adhMap = this.battleDataCache.getAttDefHp(pgm);
            final int forcesMax = adhMap.get(3);
            doc.createElement("isFullForces", forcesMax <= pgm.getForces());
            final int maxHp = this.dataGetter.getBattleDataCache().getMaxHp(pgm);
            if (pgm.getForces() * 1.0 / maxHp < 0.05) {
                doc.createElement("forceNotEnough", true);
            }
            int state = pgm.getState();
            final GeneralMoveDto dto = CityService.getGeneralMoveDto(playerId, pgm.getGeneralId());
            if (dto != null) {
                state = (CDUtil.isInCD(dto.nextMoveTime, new Date()) ? 6 : state);
                if (dto.cityState == 22 || dto.cityState == 23) {
                    state = 22;
                }
            }
            if (hasGotMap != null) {
                final Boolean flag = hasGotMap.get(pgm.getVId());
                doc.createElement("isGetReward", flag != null && flag);
            }
            else {
                doc.createElement("isGetReward", false);
            }
            doc.createElement("state", state);
            doc.endObject();
        }
        doc.endArray();
    }
    
    @Override
    public void getGeneralInfoForGoldOrder(final int playerId, final JsonDocument doc) {
        doc.startArray("generalInfo");
        final List<PlayerGeneralMilitary> list = this.playerGeneralMilitaryDao.getMilitaryList(playerId);
        for (final PlayerGeneralMilitary pgm : list) {
            doc.startObject();
            final General general = (General)this.generalCache.get((Object)pgm.getGeneralId());
            doc.createElement("generalName", general.getName());
            doc.createElement("generalPic", general.getPic());
            doc.createElement("generalLv", pgm.getLv());
            doc.createElement("generalQuality", general.getQuality());
            final Troop troop = this.dataGetter.getTroopCache().getTroop(general.getTroop(), pgm.getPlayerId());
            doc.createElement("troopId", troop.getType());
            doc.createElement("troopType", troop.getSerial());
            doc.createElement("generalId", general.getId());
            int state = pgm.getState();
            final GeneralMoveDto dto = CityService.getGeneralMoveDto(playerId, pgm.getGeneralId());
            if (dto != null) {
                state = (CDUtil.isInCD(dto.nextMoveTime, new Date()) ? 6 : state);
                if (dto.cityState == 22 || dto.cityState == 23) {
                    state = 22;
                }
            }
            final boolean isInTeam = TeamManager.getInstance().isJoinTeam2(pgm.getPlayerId(), pgm.getGeneralId());
            if (isInTeam) {
                state = 16;
            }
            doc.createElement("state", state);
            doc.endObject();
        }
        doc.endArray();
    }
}
