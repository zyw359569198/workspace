package com.reign.gcld.battle.scene;

import com.reign.gcld.common.log.*;
import com.reign.gcld.log.*;
import com.reign.util.*;
import com.reign.gcld.juben.service.*;
import com.reign.gcld.juben.common.*;
import com.reign.gcld.battle.service.*;
import com.reign.framework.json.*;
import com.reign.gcld.general.domain.*;
import com.reign.gcld.player.dto.*;
import com.reign.gcld.player.domain.*;
import com.reign.gcld.world.service.*;
import com.reign.gcld.sdata.common.*;
import com.reign.gcld.team.service.*;
import com.reign.gcld.general.dto.*;
import com.reign.gcld.battle.reward.*;
import com.reign.gcld.store.domain.*;
import java.util.concurrent.atomic.*;
import com.reign.gcld.common.util.*;
import com.reign.gcld.task.message.*;
import com.reign.gcld.task.domain.*;
import com.reign.gcld.tavern.domain.*;
import com.reign.gcld.common.*;
import com.reign.framework.netty.util.*;
import com.reign.framework.netty.servlet.*;
import org.apache.commons.lang.*;
import com.reign.gcld.activity.service.*;
import ast.gcldcore.fight.*;
import com.reign.gcld.sdata.cache.*;
import java.util.concurrent.*;
import com.reign.gcld.rank.service.*;
import java.util.*;
import com.reign.gcld.slave.common.*;
import com.reign.gcld.event.common.*;
import com.reign.gcld.battle.common.*;
import com.reign.gcld.asynchronousDB.manager.*;
import com.reign.gcld.asynchronousDB.operation.*;
import com.reign.gcld.asynchronousDB.operation.basic.*;
import com.reign.gcld.activity.domain.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.gcld.battle.domain.*;
import java.io.*;

public abstract class Builder
{
    int battleType;
    protected static final Logger timerLog;
    public static final int WORLD_CITY_ATTACKER_TACTIC_HALF = 1;
    public static final int GUANYU_WUSHENFUTI_TACTIC_HALF = 2;
    public static final int GEM_SKILL_RENGXING_TACTIC_HALF = 3;
    public static final String REPORT_STATIC_NULL = "0|0|0|0|0|0";
    
    static {
        timerLog = new TimerLogger();
    }
    
    public Tuple<Boolean, String> attPermitCreate(final IDataGetter dataGetter, final int playerId, final int defId) {
        final Player player = dataGetter.getPlayerDao().read(playerId);
        final Tuple<Boolean, String> tuple = new Tuple();
        tuple.left = false;
        if (defId > 0) {
            final PlayerArmy playerArmy = dataGetter.getPlayerArmyDao().getPlayerArmy(player.getPlayerId(), defId);
            if (playerArmy == null || playerArmy.getAttackable() != 1) {
                tuple.right = LocalMessages.AMIES_CANNOT_ATTACKABLE;
                return tuple;
            }
            final Armies armies = (Armies)dataGetter.getArmiesCache().get((Object)defId);
            if (player.getPlayerLv() < armies.getLevel()) {
                tuple.right = MessageFormatter.format(LocalMessages.BATTLE_NO_ENOUGH_LEVEL, new Object[] { armies.getLevel() });
                return tuple;
            }
        }
        final JuBenDto juBenDto = JuBenManager.getInstance().getByPid(playerId);
        if (juBenDto != null) {
            tuple.right = LocalMessages.IN_JUBEN_CANNT_BATTLE;
            return tuple;
        }
        tuple.left = true;
        tuple.right = "";
        return tuple;
    }
    
    public Tuple<Boolean, byte[]> attPermitBack(final IDataGetter dataGetter, final int playerId, final int defId, final int generalId) {
        final Tuple<Boolean, byte[]> tuple = new Tuple();
        Battle battle = null;
        final Map<Integer, Battle> battles = NewBattleManager.getInstance().getBattleByPid(playerId);
        for (final Map.Entry<Integer, Battle> entry : battles.entrySet()) {
            if (entry.getKey() == 1) {
                battle = entry.getValue();
                break;
            }
            if (entry.getKey() == 2) {
                battle = entry.getValue();
                break;
            }
            if (entry.getKey() == 11) {
                battle = entry.getValue();
                break;
            }
            if (entry.getKey() == 12) {
                battle = entry.getValue();
                break;
            }
        }
        if (battle == null) {
            NewBattleManager.inPveBattle(playerId, false);
            tuple.right = JsonBuilder.getJson(State.FAIL, LocalMessages.BATTLE_NOT_EXIST);
            return tuple;
        }
        final JuBenDto juBenDto = JuBenManager.getInstance().getByPid(playerId);
        if (juBenDto != null) {
            tuple.right = JsonBuilder.getJson(State.FAIL, LocalMessages.IN_JUBEN_CANNT_BATTLE);
            return tuple;
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("battle", true);
        doc.createElement("battleId", battle.getBattleId());
        doc.createElement("side", 1);
        doc.endObject();
        tuple.left = true;
        tuple.right = JsonBuilder.getJson(State.SUCCESS, doc.toByte());
        return tuple;
    }
    
    public Battle existBattle(final IDataGetter dataGetter, final Player player, final int defId) {
        return null;
    }
    
    public Tuple<Boolean, String> canCreateBattle(final Player player, final int defId, final IDataGetter dataGetter) {
        final Tuple<Boolean, String> tuple = new Tuple();
        tuple.left = false;
        final Armies armies = (Armies)dataGetter.getArmiesCache().get((Object)defId);
        if (armies == null) {
            tuple.right = LocalMessages.T_COMM_10011;
            return tuple;
        }
        final PlayerArmy playerArmy = dataGetter.getPlayerArmyDao().getPlayerArmy(player.getPlayerId(), defId);
        if (playerArmy == null || playerArmy.getAttackable() != 1) {
            tuple.right = LocalMessages.AMIES_CANNOT_ATTACKABLE;
            return tuple;
        }
        if (player.getPlayerLv() < armies.getLevel()) {
            tuple.right = MessageFormatter.format(LocalMessages.BATTLE_NO_ENOUGH_LEVEL, new Object[] { armies.getLevel() });
            return tuple;
        }
        final JuBenDto juBenDto = JuBenManager.getInstance().getByPid(player.getPlayerId());
        if (juBenDto != null) {
            tuple.right = LocalMessages.IN_JUBEN_CANNT_BATTLE;
            return tuple;
        }
        tuple.left = true;
        return tuple;
    }
    
    public Tuple<Boolean, String> canJoinBattle(final Player player, final List<PlayerGeneralMilitary> pgmList, final IDataGetter dataGetter, final Battle bat) {
        final Tuple<Boolean, String> tuple = new Tuple();
        tuple.left = false;
        final int defId = bat.getDefBaseInfo().getId();
        final Armies armies = (Armies)dataGetter.getArmiesCache().get((Object)defId);
        if (armies == null) {
            tuple.right = LocalMessages.T_COMM_10011;
        }
        final PlayerArmy playerArmy = dataGetter.getPlayerArmyDao().getPlayerArmy(player.getPlayerId(), defId);
        if (playerArmy == null || playerArmy.getAttackable() != 1) {
            tuple.right = LocalMessages.AMIES_CANNOT_ATTACKABLE;
            return tuple;
        }
        if (player.getPlayerLv() < armies.getLevel()) {
            tuple.right = MessageFormatter.format(LocalMessages.BATTLE_NO_ENOUGH_LEVEL, new Object[] { armies.getLevel() });
            return tuple;
        }
        if (bat.getAttBaseInfo().getId() != player.getPlayerId()) {
            tuple.right = LocalMessages.T_COMM_10012;
            return tuple;
        }
        final JuBenDto juBenDto = JuBenManager.getInstance().getByPid(player.getPlayerId());
        if (juBenDto != null) {
            tuple.right = LocalMessages.IN_JUBEN_CANNT_BATTLE;
            return tuple;
        }
        tuple.left = true;
        return tuple;
    }
    
    public byte[] getOtherBatInfo(final IDataGetter dataGetter, final int defId, final int playerId, final int battleSide, PlayerBattleAttribute pba) {
        final JsonDocument doc = new JsonDocument();
        doc.createElement("targetName", "");
        if (pba == null) {
            pba = dataGetter.getPlayerBattleAttributeDao().read(playerId);
        }
        doc.createElement("freePhantomCount", pba.getVip3PhantomCount());
        return doc.toByte();
    }
    
    public int getBatInfo(final IDataGetter dataGetter, final int defId) {
        final Armies armies = (Armies)dataGetter.getArmiesCache().get((Object)defId);
        return 100000 + armies.getPowerId();
    }
    
    public byte[] getRewardModeInfo(final IDataGetter dataGetter, final PlayerDto playerDto) {
        final int playerId = playerDto.playerId;
        final JsonDocument doc = new JsonDocument();
        final PlayerBattleAttribute pba = dataGetter.getPlayerBattleAttributeDao().read(playerId);
        doc.createElement("cost1", ((Chargeitem)dataGetter.getChargeitemCache().get((Object)14)).getCost());
        doc.createElement("cost2", ((Chargeitem)dataGetter.getChargeitemCache().get((Object)15)).getCost());
        final long time = pba.getSupportTime().getTime() - System.currentTimeMillis();
        doc.createElement("time", (time > 0L) ? time : 0L);
        if (time > 0L) {
            doc.createElement("mode", pba.getType());
        }
        else {
            doc.createElement("mode", 0);
        }
        return doc.toByte();
    }
    
    public byte[] getPrepareInfo(final IDataGetter dataGetter, final PlayerDto playerDto, final int defId, final Battle battle, final int terrain) {
        final int playerId = playerDto.playerId;
        final JsonDocument doc = new JsonDocument();
        final List<PlayerGeneralMilitary> pgmList = dataGetter.getPlayerGeneralMilitaryDao().getMilitaryList(playerId);
        final Armies armies = (Armies)dataGetter.getArmiesCache().get((Object)defId);
        doc.createElement("name", armies.getName());
        doc.createElement("intro", armies.getIntro());
        doc.createElement("bat", 100000 + armies.getPowerId());
        final PlayerResource pr = dataGetter.getPlayerResourceDao().read(playerId);
        doc.createElement("food", armies.getFoodConsume());
        doc.createElement("color", pr.getFood() >= armies.getFoodConsume());
        doc.appendJson(this.getAttTopLeft(dataGetter, playerId, battle));
        doc.appendJson(this.getDefTopRight(dataGetter, playerId, battle, armies.getChief()));
        doc.appendJson(this.getAttGenerals(dataGetter, playerId, pgmList, playerDto.forceId, terrain));
        doc.appendJson(this.getDefGenerals(dataGetter, armies, terrain));
        return doc.toByte();
    }
    
    public byte[] getAttTopLeft(final IDataGetter dataGetter, final int playerId, final Battle battle) {
        final JsonDocument doc = new JsonDocument();
        final Player attPlayer = dataGetter.getPlayerDao().read(playerId);
        doc.createElement("playerId", attPlayer.getPlayerId());
        doc.createElement("playerName", attPlayer.getPlayerName());
        doc.createElement("playerPic", attPlayer.getPic());
        doc.createElement("playerLv", attPlayer.getPlayerLv());
        if (battle != null) {
            doc.createElement("playerForces", battle.getAttBaseInfo().getNum());
            doc.createElement("playerMaxForces", battle.getAttBaseInfo().getAllNum());
        }
        return doc.toByte();
    }
    
    public byte[] getDefTopRight(final IDataGetter dataGetter, final int playerId, final Battle battle, final int armyId) {
        final JsonDocument doc = new JsonDocument();
        final Army army = (Army)dataGetter.getArmyCache().get((Object)armyId);
        final General general = (General)dataGetter.getGeneralCache().get((Object)army.getGeneralId());
        doc.createElement("npcId", army.getGeneralId());
        doc.createElement("npcName", general.getName());
        doc.createElement("npcPic", general.getPic());
        doc.createElement("npcLv", army.getGeneralLv());
        if (battle != null) {
            doc.createElement("npcForces", battle.getDefBaseInfo().getNum());
            doc.createElement("npcMaxForces", battle.getDefBaseInfo().getAllNum());
        }
        return doc.toByte();
    }
    
    public byte[] getBattleTopInfo(final IDataGetter dataGetter, final PlayerDto playerDto, final Battle battle) {
        final JsonDocument doc = new JsonDocument();
        final Armies armies = (Armies)dataGetter.getArmiesCache().get((Object)battle.getDefBaseInfo().getId());
        doc.createElement("name", armies.getName());
        doc.createElement("intro", armies.getIntro());
        doc.createElement("bat", 100000 + armies.getPowerId());
        doc.appendJson(this.getAttTopLeft(dataGetter, playerDto.playerId, battle));
        doc.appendJson(this.getDefTopRight(dataGetter, playerDto.playerId, battle, armies.getChief()));
        return doc.toByte();
    }
    
    public void reorderPGMList(final IDataGetter dataGetter, final List<PlayerGeneralMilitary> pgmList) {
        if (pgmList == null || pgmList.size() == 0) {
            return;
        }
        final int playerId = pgmList.get(0).getPlayerId();
        final PlayerBattleAttribute pba = dataGetter.getPlayerBattleAttributeDao().read(playerId);
        final String armiesBattleOrder = pba.getArmiesBattleOrder();
        if (armiesBattleOrder == null) {
            return;
        }
        final List<PlayerGeneralMilitary> pgmListNew = new ArrayList<PlayerGeneralMilitary>(pgmList.size());
        final List<Integer> ids = new ArrayList<Integer>(5);
        String[] split;
        for (int length = (split = armiesBattleOrder.split("#")).length, k = 0; k < length; ++k) {
            final String s = split[k];
            if (!s.equals("")) {
                ids.add(Integer.parseInt(s));
            }
        }
        PlayerGeneralMilitary pgm = null;
        for (final Integer id : ids) {
            for (int i = 0; i < pgmList.size(); ++i) {
                pgm = pgmList.get(i);
                final int armyHp = pgm.getForces();
                final int maxHp = dataGetter.getBattleDataCache().getMaxHp(pgm);
                if (pgm.getState() <= 1 && armyHp * 1.0 / maxHp >= 0.05 && pgm.getGeneralId().equals(id)) {
                    pgmListNew.add(pgm);
                }
            }
        }
        for (final PlayerGeneralMilitary temp : pgmListNew) {
            pgmList.remove(temp);
        }
        for (final PlayerGeneralMilitary temp : pgmList) {
            pgmListNew.add(temp);
        }
        pgmList.clear();
        for (int j = 0; j < pgmListNew.size(); ++j) {
            pgmList.add(pgmListNew.get(j));
        }
    }
    
    public byte[] getAttGenerals(final IDataGetter dataGetter, final int playerId, final List<PlayerGeneralMilitary> pgmList, final int forceId, final int terrain) {
        this.reorderPGMList(dataGetter, pgmList);
        final JsonDocument doc = new JsonDocument();
        doc.startArray("attGenerals");
        long armyHp = 0L;
        Map<Integer, Integer> adhMap = new HashMap<Integer, Integer>();
        for (int i = 0; i < pgmList.size(); ++i) {
            doc.startObject();
            final PlayerGeneralMilitary pgm = pgmList.get(i);
            final General general = (General)dataGetter.getGeneralCache().get((Object)pgm.getGeneralId());
            doc.createElement("index", i);
            doc.createElement("generalId", pgm.getGeneralId());
            doc.createElement("generalName", general.getName());
            doc.createElement("generalLv", pgm.getLv());
            doc.createElement("generalPic", general.getPic());
            doc.createElement("quality", general.getQuality());
            final Player playerTemp = dataGetter.getPlayerDao().read(pgm.getPlayerId());
            final Troop troop = dataGetter.getTroopCache().getTroop(general.getTroop(), playerTemp.getPlayerId());
            final TroopTerrain gTerrain = troop.getTerrains().get(terrain);
            if (gTerrain != null && gTerrain.getAttEffect() > 0) {
                doc.createElement("terrainAdd", gTerrain.getAttEffect());
                doc.createElement("terrainQ", gTerrain.getAttQuality());
            }
            final GeneralMoveDto gmd = CityService.getGeneralMoveDto(pgm.getPlayerId(), pgm.getGeneralId());
            final List<TerrainStrategySpecDto> tssList = troop.getTsstList();
            if (tssList != null && tssList.size() > 0) {
                final List<Integer> tssIds = new LinkedList<Integer>();
                for (final TerrainStrategySpecDto tss : tssList) {
                    if (tss.terrainId == terrain && (tss.show == 1 || tss.show == 3)) {
                        tssIds.add(tss.strategyId);
                    }
                }
                if (tssIds.size() > 0) {
                    doc.startArray("tssList");
                    for (final TerrainStrategySpecDto tss : tssList) {
                        doc.startObject();
                        doc.createElement("strategyId", tss.strategyId);
                        doc.endObject();
                    }
                    doc.endArray();
                }
            }
            doc.createElement("troopId", troop.getType());
            doc.createElement("troopType", troop.getSerial());
            doc.createElement("generalPic", general.getPic());
            armyHp = pgm.getForces();
            final int maxHp = dataGetter.getBattleDataCache().getMaxHp(pgm);
            if (pgm.getState() == 2) {
                doc.createElement("state", 2);
                doc.createElement("reason", (Object)LocalMessages.BATTLE_INT_BATTLE_ARMY);
            }
            else if (pgm.getState() == 3) {
                doc.createElement("state", 3);
                doc.createElement("reason", (Object)LocalMessages.BATTLE_INT_BATTLE_WORLD_CITY);
            }
            else if (!this.canJoinBattle(armyHp, maxHp)) {
                doc.createElement("state", 4);
                doc.createElement("reason", (Object)LocalMessages.BATTLE_INT_BATTLE_BLOOD_NO_ENOUGH);
            }
            else if (pgm.getState() == 4) {
                doc.createElement("state", 5);
                doc.createElement("reason", (Object)LocalMessages.BATTLE_INT_BATTLE_OCCUPY);
            }
            else if (pgm.getState() == 10) {
                doc.createElement("state", 11);
                doc.createElement("reason", (Object)LocalMessages.BATTLE_INT_BATTLE_CITY);
            }
            else if (pgm.getState() > 1) {
                doc.createElement("state", 100);
                doc.createElement("reason", (Object)LocalMessages.BATTLE_INT_BATTLE);
            }
            else if (TeamManager.getInstance().isJoinTeam(playerId, pgm.getGeneralId())) {
                doc.createElement("state", 10);
                doc.createElement("reason", (Object)LocalMessages.BATTLE_IN_GROUP_ARMY);
            }
            else if (gmd != null && (gmd.cityState == 22 || gmd.cityState == 23)) {
                doc.createElement("state", 12);
                doc.createElement("reason", (Object)LocalMessages.GENTEAL_IN_CELL);
            }
            else {
                doc.createElement("state", 1);
            }
            doc.createElement("armyHp", armyHp);
            doc.createElement("armyHpMax", dataGetter.getBattleDataCache().getMaxHp(pgm));
            adhMap = dataGetter.getBattleDataCache().getAttDefHp(playerId, pgm.getGeneralId(), troop, pgm.getLv());
            final int forcesMax = adhMap.get(3);
            final long needForces = forcesMax - pgm.getForces();
            long needTime = 0L;
            final double secondForces = dataGetter.getGeneralService().getOutput(pgm.getPlayerId(), forceId, pgm.getLocationId(), troop);
            if (needForces > 0L) {
                needTime = (long)(needForces / secondForces);
                final long timeed = (System.currentTimeMillis() - pgm.getUpdateForcesTime().getTime()) / 1000L;
                needTime = needTime - timeed + 10L;
                if (needTime < 0L) {
                    needTime = 10L;
                }
            }
            doc.createElement("needTime", needTime * 1000L);
            final Tactic tactic = (Tactic)dataGetter.getTacticCache().get((Object)general.getTacticId());
            if (tactic != null) {
                doc.createElement("tacticName", (Object)tactic.getName());
            }
            if (general.getTacticId() == 24) {
                doc.createElement("rbType", 3);
                final RewardInfo ri = tactic.getReward().canReward(dataGetter, playerId, pgm);
                if (ri.isCanReward()) {
                    doc.createElement("canRb", 1);
                }
                else {
                    doc.createElement("canRb", 0);
                }
            }
            doc.endObject();
        }
        doc.endArray();
        return doc.toByte();
    }
    
    public boolean canJoinBattle(final double armyHp, final double maxHp) {
        return armyHp * 1.0 / maxHp >= 0.05;
    }
    
    public byte[] getDefGenerals(final IDataGetter dataGetter, final Armies armies, final int terrain) {
        final JsonDocument doc = new JsonDocument();
        final Integer[] defNpcs = armies.getArmiesId();
        doc.startArray("defGenerals");
        General general = null;
        Troop troop = null;
        TroopTerrain gTerrain = null;
        if (defNpcs != null && defNpcs.length > 0) {
            Integer[] array;
            for (int length = (array = defNpcs).length, i = 0; i < length; ++i) {
                final int npc = array[i];
                if (npc > 0) {
                    doc.startObject();
                    final Army armyCach = (Army)dataGetter.getArmyCache().get((Object)npc);
                    general = (General)dataGetter.getGeneralCache().get((Object)armyCach.getGeneralId());
                    troop = (Troop)dataGetter.getTroopCache().get((Object)general.getTroop());
                    doc.createElement("generalId", armyCach.getGeneralId());
                    doc.createElement("generalName", armyCach.getName());
                    doc.createElement("att", armyCach.getAtt());
                    doc.createElement("generalLv", armyCach.getGeneralLv());
                    doc.createElement("troopId", troop.getType());
                    doc.createElement("troopType", troop.getSerial());
                    doc.createElement("generalPic", ((General)dataGetter.getGeneralCache().get((Object)armyCach.getGeneralId())).getPic());
                    doc.createElement("armyHp", armyCach.getArmyHp());
                    if (general.getTacticId() != 0) {
                        final Tactic tactic = (Tactic)dataGetter.getTacticCache().get((Object)general.getTacticId());
                        if (tactic != null) {
                            doc.createElement("tacticName", (Object)tactic.getName());
                        }
                    }
                    final List<TerrainStrategySpecDto> tssList = troop.getTsstList();
                    if (tssList != null && tssList.size() > 0) {
                        final List<Integer> tssIds = new LinkedList<Integer>();
                        for (final TerrainStrategySpecDto tss : tssList) {
                            if (tss.terrainId == terrain && (tss.show == 2 || tss.show == 3)) {
                                tssIds.add(tss.strategyId);
                            }
                        }
                        if (tssIds.size() > 0) {
                            doc.startArray("tssList");
                            for (final TerrainStrategySpecDto tss : tssList) {
                                doc.startObject();
                                doc.createElement("strategyId", tss.strategyId);
                                doc.endObject();
                            }
                            doc.endArray();
                        }
                    }
                    gTerrain = troop.getTerrains().get(terrain);
                    if (gTerrain != null && gTerrain.getDefEffect() > 0) {
                        doc.createElement("terrainAdd", gTerrain.getDefEffect());
                        doc.createElement("terrainQ", gTerrain.getDefQuality());
                    }
                    doc.endObject();
                }
            }
        }
        doc.startObject();
        final Army army = (Army)dataGetter.getArmyCache().get((Object)armies.getChief());
        general = (General)dataGetter.getGeneralCache().get((Object)army.getGeneralId());
        troop = (Troop)dataGetter.getTroopCache().get((Object)general.getTroop());
        doc.createElement("generalId", army.getGeneralId());
        doc.createElement("generalName", army.getName());
        doc.createElement("att", army.getAtt());
        doc.createElement("generalLv", army.getGeneralLv());
        doc.createElement("troopId", troop.getType());
        doc.createElement("troopType", troop.getSerial());
        doc.createElement("generalPic", general.getPic());
        doc.createElement("quality", general.getQuality());
        if (general.getTacticId() != 0) {
            final Tactic tactic2 = (Tactic)dataGetter.getTacticCache().get((Object)general.getTacticId());
            if (tactic2 != null) {
                doc.createElement("tacticName", (Object)tactic2.getName());
            }
        }
        doc.createElement("armyHp", army.getArmyHp());
        doc.createElement("armyHpMax", army.getArmyHp());
        gTerrain = troop.getTerrains().get(terrain);
        if (gTerrain != null && gTerrain.getDefEffect() > 0) {
            doc.createElement("terrainAdd", gTerrain.getDefEffect());
            doc.createElement("terrainQ", gTerrain.getDefQuality());
        }
        final List<TerrainStrategySpecDto> tssList2 = troop.getTsstList();
        if (tssList2 != null && tssList2.size() > 0) {
            final List<Integer> tssIds2 = new LinkedList<Integer>();
            for (final TerrainStrategySpecDto tss2 : tssList2) {
                if (tss2.terrainId == terrain && (tss2.show == 2 || tss2.show == 3)) {
                    tssIds2.add(tss2.strategyId);
                }
            }
            if (tssIds2.size() > 0) {
                doc.startArray("tssList");
                for (final TerrainStrategySpecDto tss2 : tssList2) {
                    doc.startObject();
                    doc.createElement("strategyId", tss2.strategyId);
                    doc.endObject();
                }
                doc.endArray();
            }
        }
        doc.endObject();
        doc.endArray();
        return doc.toByte();
    }
    
    public Tuple<List<PlayerGeneralMilitary>, String> chooseGeneral(final IDataGetter dataGetter, final Player player, final int defId, final List<Integer> gIdList) {
        final Tuple<List<PlayerGeneralMilitary>, String> tuple = new Tuple();
        final Map<Integer, PlayerGeneralMilitary> pgmMap = dataGetter.getPlayerGeneralMilitaryDao().getMilitaryMap(player.getPlayerId());
        final List<PlayerGeneralMilitary> chooseList = new ArrayList<PlayerGeneralMilitary>();
        for (int i = 0; i < gIdList.size(); ++i) {
            final PlayerGeneralMilitary pgm = pgmMap.get(gIdList.get(i));
            if (pgm != null) {
                if (pgm.getState() <= 1) {
                    final int maxHp = dataGetter.getBattleDataCache().getMaxHp(pgm);
                    if (this.canJoinBattle(pgm.getForces() * 1.0, maxHp)) {
                        if (!dataGetter.getWorldFarmService().isInFarmForbiddenOperation(pgm, false)) {
                            if (!TeamManager.getInstance().isJoinTeam(pgm.getPlayerId(), pgm.getGeneralId())) {
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
        }
        if (chooseList.size() <= 0) {
            tuple.left = null;
            tuple.right = LocalMessages.GENEGAL_CANNOT_BATTLE;
            return tuple;
        }
        tuple.left = chooseList;
        return tuple;
    }
    
    public Tuple<List<PlayerGeneralMilitary>, String> chooseAllGeneral(final IDataGetter dataGetter, final Player player, final int defId, final int battleSide) {
        final Tuple<List<PlayerGeneralMilitary>, String> tuple = new Tuple();
        final Map<Integer, PlayerGeneralMilitary> pgmMap = dataGetter.getPlayerGeneralMilitaryDao().getMilitaryMap(player.getPlayerId());
        final List<PlayerGeneralMilitary> chooseList = new ArrayList<PlayerGeneralMilitary>();
        for (final PlayerGeneralMilitary pgm : pgmMap.values()) {
            if (pgm == null) {
                continue;
            }
            if (pgm.getState() > 1) {
                continue;
            }
            final int maxHp = dataGetter.getBattleDataCache().getMaxHp(pgm);
            if (!this.canJoinBattle(pgm.getForces() * 1.0, maxHp)) {
                continue;
            }
            if (TeamManager.getInstance().isJoinTeam(pgm.getPlayerId(), pgm.getGeneralId())) {
                continue;
            }
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
        if (chooseList.size() <= 0) {
            tuple.left = null;
            tuple.right = LocalMessages.GENEGAL_CANNOT_BATTLE;
            return tuple;
        }
        tuple.left = chooseList;
        return tuple;
    }
    
    public String getBattleId(final IDataGetter dataGetter, final Player player, final int defId) {
        return NewBattleManager.getBattleId(this.battleType, player.getPlayerId(), defId);
    }
    
    public void initAttCamp(final IDataGetter dataGetter, final BattleAttacker battleAttacker, final int defId, final Battle bat) {
        if (battleAttacker.attPlayerId > 0) {
            final int zdzsTech = dataGetter.getTechEffectCache().getTechEffect(battleAttacker.attPlayerId, 43);
            int autoStrategy = 0;
            if (zdzsTech > 0) {
                final PlayerBattleAttribute pba = dataGetter.getPlayerBattleAttributeDao().read(battleAttacker.attPlayerId);
                autoStrategy = pba.getAutoStrategy();
            }
            else {
                autoStrategy = -1;
            }
            bat.inBattlePlayers.put(battleAttacker.attPlayerId, new PlayerInfo(battleAttacker.attPlayerId, true, autoStrategy));
        }
        final Builder builder = BuilderFactory.getInstance().getBuilder(bat.getBattleType());
        final List<PlayerGeneralMilitary> pgmList = battleAttacker.pgmList;
        final Player player = battleAttacker.attPlayer;
        int attNum = 0;
        for (int i = 0; i < pgmList.size(); ++i) {
            final PlayerGeneralMilitary pgm = pgmList.get(i);
            final CampArmy campArmy = this.copyArmyFromPlayerTable(player, pgm, dataGetter, builder.getGeneralState(), bat, 1);
            if (campArmy != null) {
                attNum += campArmy.getArmyHpOrg();
                bat.attCamp.add(campArmy);
                BattleSceneLog.getInstance().info("#batId:" + bat.getBattleId() + "_" + bat.getStartTime() + "#add:init#side:att" + "#playerId:" + campArmy.getPlayerId() + ":" + campArmy.isPhantom + "#general:" + campArmy.getGeneralId() + "#attSize:" + bat.attCamp.size());
            }
        }
        bat.attBaseInfo.setNum(bat.attBaseInfo.getNum() + attNum);
        bat.attBaseInfo.setAllNum(bat.attBaseInfo.getAllNum() + attNum);
        bat.attBaseInfo.setForceId(player.getForceId());
    }
    
    public static double getTerrainValue(final int terrainType, final Troop troop, final int batSide, final CampArmy campArmy) {
        final TroopTerrain terrain = troop.getTerrains().get(terrainType);
        int effect = 0;
        if (terrain != null) {
            if (batSide == 1) {
                effect = terrain.getAttEffect();
                campArmy.setTerrainQ(terrain.getAttQuality());
            }
            else {
                effect = terrain.getDefEffect();
                campArmy.setTerrainQ(terrain.getDefQuality());
            }
        }
        campArmy.setTerrain(effect / 100.0);
        campArmy.terrainAdd = effect;
        return campArmy.getTerrain();
    }
    
    public static void getAttDefHp(final IDataGetter dataGetter, final CampArmy campArmy) {
        final Troop troop = (Troop)dataGetter.getTroopCache().get((Object)campArmy.getTroopId());
        final int att = dataGetter.getBattleDataCache().getAtt(campArmy.getPlayerId(), campArmy.getGeneralId(), troop, campArmy.getGeneralLv());
        final int def = dataGetter.getBattleDataCache().getDef(campArmy.getPlayerId(), campArmy.getGeneralId(), troop, campArmy.getGeneralLv());
        int maxHp = dataGetter.getBattleDataCache().getMaxHp(campArmy.getPlayerId(), campArmy.getGeneralId(), troop, campArmy.getGeneralLv());
        final int column = dataGetter.getBattleDataCache().getColumNum(campArmy.getPlayerId());
        final int remainder = maxHp % (3 * column);
        maxHp -= remainder;
        final int troopHp = maxHp / column;
        campArmy.setTroopHp(troopHp);
        campArmy.setColumn(column);
        campArmy.setAttEffect(att);
        campArmy.setDefEffect(def);
        campArmy.setMaxForces(maxHp);
        final List<StoreHouse> prosetList = dataGetter.getStoreHouseDao().getGeneralEquipList(campArmy.playerId, campArmy.generalId, 14);
        if (prosetList != null && prosetList.size() > 0) {
            if (prosetList.size() > 1) {
                ErrorSceneLog.getInstance().appendErrorMsg("too many prosetList").appendPlayerId(campArmy.playerId).appendPlayerName(campArmy.playerName).appendGeneralId(campArmy.generalId).appendGeneralName(campArmy.generalName).appendClassName("Builder").appendMethodName("getAttDefHp").flush();
            }
            final StoreHouse proset = prosetList.get(0);
            final EquipProset equipProset = dataGetter.getEquipCache().getEquipProsetByItemId(proset.getItemId());
            if (equipProset != null) {
                final EquipCoordinates equipCoordinates = dataGetter.getEquipCache().getMainSuit(equipProset.getId());
                if (equipCoordinates != null) {
                    final int[] skills = { equipCoordinates.getPos1Skill(), equipCoordinates.getPos2Skill(), equipCoordinates.getPos3Skill(), equipCoordinates.getPos4Skill(), equipCoordinates.getPos5Skill(), equipCoordinates.getPos6Skill() };
                    final int lv = 5;
                    final int starNum = 4;
                    int[] array;
                    for (int length = (array = skills).length, i = 0; i < length; ++i) {
                        final int skillId = array[i];
                        final EquipSkillEffect equipSkillEffect = dataGetter.getEquipSkillEffectCache().getEquipSkillEffectByIdLV(skillId, lv);
                        if (equipSkillEffect != null) {
                            if (equipSkillEffect.getAttDef_B() != null) {
                                final AttDef_B attDef_B = campArmy.attDef_B;
                                attDef_B.ATT_B += starNum * equipSkillEffect.getAttDef_B().ATT_B;
                                final AttDef_B attDef_B2 = campArmy.attDef_B;
                                attDef_B2.DEF_B += starNum * equipSkillEffect.getAttDef_B().DEF_B;
                            }
                            campArmy.TACTIC_ATT += starNum * equipSkillEffect.getTACTIC_ATT();
                            campArmy.TACTIC_DEF += starNum * equipSkillEffect.getTACTIC_DEF();
                        }
                        else {
                            final Player player = dataGetter.getPlayerDao().read(campArmy.playerId);
                            ErrorSceneLog.getInstance().appendErrorMsg("equipSkillEffect is null.").append("skillId", skillId).append("lv", lv).appendPlayerId(campArmy.playerId).appendPlayerName(player.getPlayerName()).appendGeneralId(campArmy.generalId).appendGeneralName(((General)dataGetter.getGeneralCache().get((Object)campArmy.generalId)).getName()).flush();
                        }
                    }
                }
                else {
                    ErrorSceneLog.getInstance().appendErrorMsg("equipCoordinates is null.").append("equipProset.getId()", equipProset.getId()).append("proset.getVId()", proset.getVId()).appendPlayerId(campArmy.playerId).appendPlayerName(campArmy.playerName).appendGeneralId(campArmy.generalId).appendGeneralName(campArmy.generalName).appendClassName("Builder").appendMethodName("getAttDefHp").flush();
                }
            }
            else {
                ErrorSceneLog.getInstance().appendErrorMsg("equipProset is null.").append("proset.getItemId()", proset.getItemId()).append("proset.getVId()", proset.getVId()).appendPlayerId(campArmy.playerId).appendPlayerName(campArmy.playerName).appendGeneralId(campArmy.generalId).appendGeneralName(campArmy.generalName).appendClassName("Builder").appendMethodName("getAttDefHp").flush();
            }
        }
        List<StoreHouse> shList = dataGetter.getStoreHouseDao().getGeneralEquipList(campArmy.playerId, campArmy.generalId, 10);
        for (final StoreHouse psh : shList) {
            if (psh.getType() != 10) {
                continue;
            }
            final EquipCoordinates equipCoordinates2 = dataGetter.getEquipCache().getEquipCoordinateByItemId(psh.getItemId());
            if (equipCoordinates2 != null) {
                final int[] skills2 = { equipCoordinates2.getPos1Skill(), equipCoordinates2.getPos2Skill(), equipCoordinates2.getPos3Skill(), equipCoordinates2.getPos4Skill(), equipCoordinates2.getPos5Skill(), equipCoordinates2.getPos6Skill() };
                final int lv2 = 5;
                final int starNum2 = 4;
                int[] array2;
                for (int length2 = (array2 = skills2).length, j = 0; j < length2; ++j) {
                    final int skillId2 = array2[j];
                    final EquipSkillEffect equipSkillEffect2 = dataGetter.getEquipSkillEffectCache().getEquipSkillEffectByIdLV(skillId2, lv2);
                    if (equipSkillEffect2 != null) {
                        if (equipSkillEffect2.getAttDef_B() != null) {
                            final AttDef_B attDef_B3 = campArmy.attDef_B;
                            attDef_B3.ATT_B += starNum2 * equipSkillEffect2.getAttDef_B().ATT_B;
                            final AttDef_B attDef_B4 = campArmy.attDef_B;
                            attDef_B4.DEF_B += starNum2 * equipSkillEffect2.getAttDef_B().DEF_B;
                        }
                        campArmy.TACTIC_ATT += starNum2 * equipSkillEffect2.getTACTIC_ATT();
                        campArmy.TACTIC_DEF += starNum2 * equipSkillEffect2.getTACTIC_DEF();
                    }
                    else {
                        final Player player2 = dataGetter.getPlayerDao().read(campArmy.playerId);
                        ErrorSceneLog.getInstance().appendErrorMsg("equipSkillEffect is null.").append("skillId", skillId2).append("lv", lv2).appendPlayerId(campArmy.playerId).appendPlayerName(player2.getPlayerName()).appendGeneralId(campArmy.generalId).appendGeneralName(((General)dataGetter.getGeneralCache().get((Object)campArmy.generalId)).getName()).flush();
                    }
                }
            }
            else {
                final Player player3 = dataGetter.getPlayerDao().read(campArmy.playerId);
                ErrorSceneLog.getInstance().appendErrorMsg("equipCoordinates is null.").append("StoreHouse", psh.getVId()).append("psh.getItemId()", psh.getItemId()).appendPlayerId(campArmy.playerId).appendPlayerName(player3.getPlayerName()).appendGeneralId(campArmy.generalId).appendGeneralName(((General)dataGetter.getGeneralCache().get((Object)campArmy.generalId)).getName()).flush();
            }
        }
        shList = dataGetter.getStoreHouseDao().getGeneralEquipList(campArmy.playerId, campArmy.generalId, 1);
        for (final StoreHouse psh : shList) {
            if (psh.getRefreshAttribute() != null && !psh.getRefreshAttribute().isEmpty()) {
                final String[] skills3 = psh.getRefreshAttribute().split(";");
                String[] array3;
                for (int length3 = (array3 = skills3).length, k = 0; k < length3; ++k) {
                    final String skill = array3[k];
                    final String[] idLv = skill.split(":");
                    if (idLv.length == 2) {
                        final int id = Integer.parseInt(idLv[0]);
                        final int lv3 = Integer.parseInt(idLv[1]);
                        final EquipSkillEffect equipSkillEffect2 = dataGetter.getEquipSkillEffectCache().getEquipSkillEffectByIdLV(id, lv3);
                        if (equipSkillEffect2 != null) {
                            if (equipSkillEffect2.getAttDef_B() != null) {
                                final AttDef_B attDef_B5 = campArmy.attDef_B;
                                attDef_B5.ATT_B += equipSkillEffect2.getAttDef_B().ATT_B;
                                final AttDef_B attDef_B6 = campArmy.attDef_B;
                                attDef_B6.DEF_B += equipSkillEffect2.getAttDef_B().DEF_B;
                            }
                            campArmy.TACTIC_ATT += equipSkillEffect2.getTACTIC_ATT();
                            campArmy.TACTIC_DEF += equipSkillEffect2.getTACTIC_DEF();
                        }
                        else {
                            final Player player2 = dataGetter.getPlayerDao().read(campArmy.playerId);
                            ErrorSceneLog.getInstance().appendErrorMsg("equipSkillEffect is null.").append("id", id).append("lv", lv3).appendPlayerId(campArmy.playerId).appendPlayerName(player2.getPlayerName()).appendGeneralId(campArmy.generalId).appendGeneralName(((General)dataGetter.getGeneralCache().get((Object)campArmy.generalId)).getName()).flush();
                        }
                    }
                }
            }
        }
    }
    
    public static void getAttDef(final IDataGetter dataGetter, final CampArmy campArmy) {
        final Troop troop = (Troop)dataGetter.getTroopCache().get((Object)campArmy.getTroopId());
        final int att = dataGetter.getBattleDataCache().getAtt(campArmy.getPlayerId(), campArmy.getGeneralId(), troop, campArmy.getGeneralLv());
        final int def = dataGetter.getBattleDataCache().getDef(campArmy.getPlayerId(), campArmy.getGeneralId(), troop, campArmy.getGeneralLv());
        campArmy.setAttEffect(att);
        campArmy.setDefEffect(def);
    }
    
    public CampArmy copyArmyFromPlayerTable(final Player player, final PlayerGeneralMilitary pgm, final IDataGetter dataGetter, final int generalBattleType, final Battle bat, final int batSide) {
        final CampArmy campArmy = new CampArmy();
        final General general = (General)dataGetter.getGeneralCache().get((Object)pgm.getGeneralId());
        campArmy.setSpecialGeneral(new SpecialGeneral(general.getGeneralSpecialInfo().generalType, general.getGeneralSpecialInfo().param, general.getGeneralSpecialInfo().param2));
        final Troop troop = dataGetter.getTroopCache().getTroop(general.getTroop(), player.getPlayerId());
        campArmy.updateDB = true;
        campArmy.setPlayerLv(player.getPlayerLv());
        campArmy.setPlayerId(player.getPlayerId());
        campArmy.setPlayerName(player.getPlayerName());
        campArmy.setForceId(player.getForceId());
        campArmy.setActivityAddExp(dataGetter.getActivityService().getAddBatValue(player.getPlayerId(), player.getPlayerLv()));
        final PlayerBattleAttribute pba = dataGetter.getPlayerBattleAttributeDao().read(player.getPlayerId());
        getTerrainValue(bat.terrainVal, troop, batSide, campArmy);
        campArmy.setTroopId(troop.getId());
        campArmy.setTroopSerial(troop.getSerial());
        campArmy.setTroopType(troop.getType());
        campArmy.setTroopName(troop.getName());
        campArmy.setGeneralId(pgm.getGeneralId());
        campArmy.setGeneralLv(pgm.getLv());
        campArmy.setPgmVId(pgm.getVId());
        campArmy.setStrength(pgm.getStrength(general.getStrength()));
        campArmy.setLeader(pgm.getLeader(general.getLeader()));
        campArmy.setArmyName(general.getName());
        campArmy.setGeneralName(general.getName());
        campArmy.setGeneralPic(general.getPic());
        campArmy.setQuality(general.getQuality());
        campArmy.setTacicId(general.getTacticId());
        if (pgm.getState() == 1 || pgm.getAuto() == 1) {
            campArmy.setInRecruit(true);
        }
        try {
            final int res = dataGetter.getPlayerGeneralMilitaryDao().updateStateCheck(player.getPlayerId(), pgm.getGeneralId(), generalBattleType);
            if (res <= 0) {
                return null;
            }
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().appendErrorMsg("Builder copyArmyFromPlayerTable \u66f4\u6539\u6b66\u5c06\u72b6\u6001Exception").appendClassName("Builder").appendMethodName("copyArmyFromPlayerTable").append("PlayerId", player.getPlayerId()).append("GeneralId", pgm.getGeneralId()).flush();
            return null;
        }
        campArmy.setUpdateDB(true);
        campArmy.setId(bat.campNum.getAndIncrement());
        if (campArmy.getPlayerId() > 0 && !campArmy.isPhantom) {
            NewBattleManager.getInstance().joinBattle(bat, campArmy.getPlayerId(), campArmy.getGeneralId());
        }
        dataGetter.getGeneralService().sendGmStateSet(player.getPlayerId(), pgm.getGeneralId(), this.battleType);
        int forces = pgm.getForces();
        final int remainder = forces % 3;
        forces -= remainder;
        campArmy.setArmyHp(forces);
        campArmy.setArmyHpOrg(forces);
        getAttDefHp(dataGetter, campArmy);
        if (pba != null && pba.getSupportTime() != null && pba.getSupportTime().getTime() > System.currentTimeMillis()) {
            if (pba.getType() == 1) {
                campArmy.setRewardDoubleType(1);
                campArmy.setRewardDouble(1.5);
            }
            else if (pba.getType() == 2) {
                campArmy.setRewardDoubleType(2);
                campArmy.setRewardDouble(2.0);
            }
        }
        if (general.getTacticId() > 0 && pgm.getForces() >= campArmy.getMaxForces()) {
            campArmy.setTacticVal(1);
            if (campArmy.getSpecialGeneral().generalType == 7) {
                campArmy.setTacticVal((int)campArmy.getSpecialGeneral().param);
            }
        }
        if (batSide == 1) {
            campArmy.setStrategies(troop.getStrategyMap().get(bat.terrainVal));
        }
        else {
            campArmy.setStrategies(troop.getStrategyDefMap().get(bat.terrainVal));
        }
        return campArmy;
    }
    
    public CampArmy copyArmyFromPlayerTableForKfwd(final Player player, final PlayerGeneralMilitary pgm, final IDataGetter dataGetter, final int battleType, final Battle bat, final int batSide) {
        final CampArmy campArmy = new CampArmy();
        final General general = (General)dataGetter.getGeneralCache().get((Object)pgm.getGeneralId());
        final Troop troop = dataGetter.getTroopCache().getTroop(general.getTroop(), player.getPlayerId());
        campArmy.updateDB = true;
        campArmy.setPlayerLv(player.getPlayerLv());
        campArmy.setPlayerId(player.getPlayerId());
        campArmy.setPlayerName(player.getPlayerName());
        campArmy.setForceId(player.getForceId());
        campArmy.setActivityAddExp(dataGetter.getActivityService().getAddBatValue(player.getPlayerId(), player.getPlayerLv()));
        final PlayerBattleAttribute pba = dataGetter.getPlayerBattleAttributeDao().read(player.getPlayerId());
        getTerrainValue(bat.terrainVal, troop, batSide, campArmy);
        campArmy.setTroopId(troop.getId());
        campArmy.setTroopSerial(troop.getSerial());
        campArmy.setTroopType(troop.getType());
        campArmy.setTroopName(troop.getName());
        campArmy.setGeneralId(pgm.getGeneralId());
        campArmy.setGeneralLv(pgm.getLv());
        campArmy.setPgmVId(pgm.getVId());
        campArmy.setStrength(pgm.getStrength(general.getStrength()));
        campArmy.setLeader(pgm.getLeader(general.getLeader()));
        campArmy.setArmyName(general.getName());
        campArmy.setGeneralName(general.getName());
        campArmy.setGeneralPic(general.getPic());
        campArmy.setQuality(general.getQuality());
        campArmy.setTacicId(general.getTacticId());
        if (pgm.getState() == 1 || pgm.getAuto() == 1) {
            campArmy.setInRecruit(true);
        }
        campArmy.setUpdateDB(true);
        campArmy.setId(bat.campNum.getAndIncrement());
        dataGetter.getGeneralService().sendGmStateSet(player.getPlayerId(), pgm.getGeneralId(), battleType);
        int forces = dataGetter.getBattleDataCache().getMaxHp(pgm);
        final int remainder = forces % 3;
        forces -= remainder;
        campArmy.setArmyHp(forces);
        campArmy.setArmyHpOrg(forces);
        getAttDefHp(dataGetter, campArmy);
        if (pba != null && pba.getSupportTime() != null && pba.getSupportTime().getTime() > System.currentTimeMillis()) {
            if (pba.getType() == 1) {
                campArmy.setRewardDoubleType(1);
                campArmy.setRewardDouble(1.5);
            }
            else if (pba.getType() == 2) {
                campArmy.setRewardDoubleType(2);
                campArmy.setRewardDouble(2.0);
            }
        }
        if (batSide == 1) {
            campArmy.setStrategies(troop.getStrategyMap().get(bat.terrainVal));
        }
        else {
            campArmy.setStrategies(troop.getStrategyDefMap().get(bat.terrainVal));
        }
        final SpecialGeneral sp = new SpecialGeneral(general.getGeneralSpecialInfo().generalType, general.getGeneralSpecialInfo().param, general.getGeneralSpecialInfo().param2);
        campArmy.setSpecialGeneral(sp);
        if (general.getTacticId() > 0 && pgm.getForces() >= campArmy.getMaxForces()) {
            campArmy.setTacticVal(1);
            if (campArmy.getSpecialGeneral().generalType == 7) {
                campArmy.setTacticVal((int)campArmy.getSpecialGeneral().param);
            }
        }
        return campArmy;
    }
    
    public CampArmy copyArmyFromPhantom(final IDataGetter dataGetter, final Battle bat, final PlayerGeneralMilitaryPhantom phantom, final int batSide) {
        final CampArmy campArmy = new CampArmy();
        final Player player = dataGetter.getPlayerDao().read(phantom.getPlayerId());
        final General general = (General)dataGetter.getGeneralCache().get((Object)phantom.getGeneralId());
        final SpecialGeneral sg = new SpecialGeneral(general.getGeneralSpecialInfo().generalType, general.getGeneralSpecialInfo().param, general.getGeneralSpecialInfo().param2);
        if (general.getGeneralSpecialInfo().generalType == 2) {
            sg.param = ((WorldCity)dataGetter.getWorldCityCache().get((Object)bat.getDefBaseInfo().getId())).getDistance(player.getForceId());
        }
        campArmy.setSpecialGeneral(sg);
        final Troop troop = (Troop)dataGetter.getTroopCache().get((Object)phantom.getTroopId());
        campArmy.isPhantom = true;
        campArmy.updateDB = true;
        campArmy.setId(bat.campNum.getAndIncrement());
        campArmy.setPgmVId(phantom.getVId());
        campArmy.setArmyName(general.getName());
        campArmy.setPlayerLv(phantom.getPlayerLv());
        campArmy.setPlayerId(phantom.getPlayerId());
        campArmy.setForceId(player.getForceId());
        campArmy.setPlayerName(player.getPlayerName());
        campArmy.setActivityAddExp(dataGetter.getActivityService().getAddBatValue(player.getPlayerId(), player.getPlayerLv()));
        getTerrainValue(bat.terrainVal, troop, batSide, campArmy);
        campArmy.setTroopId(troop.getId());
        campArmy.setTroopSerial(troop.getSerial());
        campArmy.setTroopType(troop.getType());
        campArmy.setTroopName(troop.getName());
        campArmy.setGeneralId(phantom.getGeneralId());
        campArmy.setGeneralLv(phantom.getGeneralLv());
        campArmy.setGeneralName(String.valueOf(general.getName()) + LocalMessages.PGM_PHANTOM_SUFFIX);
        campArmy.setGeneralPic(general.getPic());
        campArmy.setQuality(general.getQuality());
        campArmy.setTacicId(general.getTacticId());
        campArmy.setStrength(phantom.getStrength());
        campArmy.setLeader(phantom.getLeader());
        int forces = phantom.getHp();
        final int remainder = forces % 3;
        forces -= remainder;
        campArmy.setArmyHp(forces);
        campArmy.setArmyHpOrg(forces);
        campArmy.setAttEffect(phantom.getAtt());
        campArmy.setDefEffect(phantom.getDef());
        campArmy.setMaxForces(phantom.getHpMax());
        campArmy.setColumn(phantom.getColumnNum());
        campArmy.setTroopHp(phantom.getHpMax() / phantom.getColumnNum());
        campArmy.setAttDef_B(new AttDef_B());
        campArmy.getAttDef_B().ATT_B = phantom.getAttB();
        campArmy.getAttDef_B().DEF_B = phantom.getDefB();
        campArmy.setTACTIC_ATT(phantom.getTacticAtt());
        campArmy.setTACTIC_DEF(phantom.getTacticDef());
        if (general.getTacticId() > 0 && phantom.getHp().equals(phantom.getHpMax())) {
            campArmy.setTacticVal(1);
            if (campArmy.getSpecialGeneral().generalType == 7) {
                campArmy.setTacticVal((int)campArmy.getSpecialGeneral().param);
            }
        }
        if (batSide == 1) {
            campArmy.setStrategies(troop.getStrategyMap().get(bat.terrainVal));
        }
        else {
            campArmy.setStrategies(troop.getStrategyDefMap().get(bat.terrainVal));
        }
        return campArmy;
    }
    
    public static int getBattleStat(final IDataGetter dataGetter, final int battleType, final int defId, final int campNum) {
        if (battleType == 3) {
            return dataGetter.getBattleStatCache().getWorldPvp(campNum);
        }
        if (battleType == 2 && ((Armies)dataGetter.getArmiesCache().get((Object)defId)).getType() == 3) {
            return dataGetter.getBattleStatCache().getTimePve(campNum);
        }
        return 1;
    }
    
    public boolean initDefCamp(final IDataGetter dataGetter, final BattleAttacker battleAttacker, final int defId, final Battle bat) {
        final Armies defLegion = (Armies)dataGetter.getArmiesCache().get((Object)defId);
        if (defLegion == null) {
            return false;
        }
        final int defForceId = defLegion.getChief();
        int defNum = 0;
        int id = 0;
        CampArmy campArmy = null;
        final Integer[] defNpcs = defLegion.getArmiesId();
        if (defNpcs != null) {
            Integer[] array;
            for (int length = (array = defNpcs).length, i = 0; i < length; ++i) {
                final Integer npc = array[i];
                if (npc > 0) {
                    id = bat.campNum.getAndIncrement();
                    campArmy = copyArmyFromCach(null, npc, dataGetter, id, bat.terrainVal, defLegion.getLevel());
                    defNum += campArmy.getArmyHpOrg();
                    bat.defCamp.add(campArmy);
                    BattleSceneLog.getInstance().info("#batId:" + bat.getBattleId() + "_" + bat.getStartTime() + "#add:init#side:def" + "#playerId:" + campArmy.getPlayerId() + ":" + campArmy.isPhantom + "#general:" + campArmy.getGeneralId() + "#defSize:" + bat.defCamp.size());
                }
            }
        }
        id = bat.campNum.getAndIncrement();
        campArmy = copyArmyFromCach(null, defLegion.getChief(), dataGetter, id, bat.terrainVal, defLegion.getLevel());
        defNum += campArmy.getArmyHpOrg();
        bat.defCamp.add(campArmy);
        BattleSceneLog.getInstance().info("#batId:" + bat.getBattleId() + "_" + bat.getStartTime() + "#add:init#side:def" + "#playerId:" + campArmy.getPlayerId() + ":" + campArmy.isPhantom + "#general:" + campArmy.getGeneralId() + "#defSize:" + bat.defCamp.size());
        bat.defBaseInfo.setAllNum(defNum);
        bat.defBaseInfo.setNum(defNum);
        bat.defBaseInfo.setForceId(defForceId);
        return false;
    }
    
    public static CampArmy copyArmyFromCach(final Player player, final int npc, final IDataGetter dataGetter, final int id, final int terrainType, final int npcLv) {
        final CampArmy campArmy = new CampArmy();
        final Army armyCach = (Army)dataGetter.getArmyCache().get((Object)npc);
        if (player == null) {
            campArmy.setPlayerId(-1);
            campArmy.setPlayerName("NPC");
            campArmy.setForceId(0);
            campArmy.setPlayerLv(npcLv);
        }
        else {
            campArmy.setPlayerId(player.getPlayerId());
            campArmy.setPlayerName(player.getPlayerName());
            campArmy.setForceId(player.getForceId());
            campArmy.setPlayerLv(player.getPlayerLv());
        }
        campArmy.setId(id);
        campArmy.setUpdateDB(false);
        campArmy.setPgmVId(0);
        campArmy.setArmyName(armyCach.getName());
        campArmy.setGeneralId(armyCach.getGeneralId());
        campArmy.setGeneralName(armyCach.getName());
        campArmy.setGeneralLv(armyCach.getGeneralLv());
        final General general = (General)dataGetter.getGeneralCache().get((Object)armyCach.getGeneralId());
        campArmy.setSpecialGeneral(new SpecialGeneral(general.getGeneralSpecialInfo().generalType, general.getGeneralSpecialInfo().param, general.getGeneralSpecialInfo().param2));
        campArmy.setGeneralPic(general.getPic());
        campArmy.setQuality(general.getQuality());
        campArmy.setStrength(general.getStrength());
        campArmy.setLeader(general.getLeader());
        campArmy.setTacicId(general.getTacticId());
        if (general.getTacticId() > 0) {
            campArmy.setTacticVal(1);
            if (campArmy.getSpecialGeneral().generalType == 7) {
                campArmy.setTacticVal((int)campArmy.getSpecialGeneral().param);
            }
        }
        final Troop troop = (Troop)dataGetter.getTroopCache().get((Object)general.getTroop());
        campArmy.setTroopId(troop.getId());
        campArmy.setTroopSerial(troop.getSerial());
        campArmy.setTroopType(troop.getType());
        campArmy.setTroopName(troop.getName());
        campArmy.setTroopDropType(BattleDrop.getDropType(troop.getDrop()));
        campArmy.setTroopDrop(troop.getTroopDrop());
        campArmy.setAttEffect(armyCach.getAtt());
        campArmy.setDefEffect(armyCach.getDef());
        campArmy.setBdEffect(armyCach.getBd());
        campArmy.setTroopHp(armyCach.getTroopHp());
        int armyHp = armyCach.getArmyHp();
        final int remainder = armyHp % 3;
        armyHp -= remainder;
        campArmy.setArmyHp(armyHp);
        campArmy.setArmyHpOrg(armyHp);
        campArmy.setMaxForces(armyHp);
        campArmy.setColumn(armyCach.getArmyHp() / armyCach.getTroopHp());
        campArmy.setStrategies(troop.getStrategyDefMap().get(terrainType));
        return campArmy;
    }
    
    public static boolean canAddBattleArmyList(final List<BattleArmy> attList) {
        return attList.size() < 6;
    }
    
    public static void onceAddQueues(final List<BattleArmy> attAddQlist, final List<BattleArmy> batList, final LinkedList<CampArmy> batCamp, final int battleSide, final AtomicInteger batQNum, final BaseInfo baseInfo, final Map<Integer, PlayerInfo> inBattlePlayers, final RoundInfo roundInfo, final Battle bat) {
        CampArmy campArmy = null;
        final Set<CampArmy> caSet = new HashSet<CampArmy>();
        while (canAddBattleArmyList(batList)) {
            if (campArmy == null || campArmy.getArmyHp() < 3) {
                campArmy = null;
                for (final CampArmy ca : batCamp) {
                    if (ca.armyHp >= 3) {
                        campArmy = ca;
                        break;
                    }
                    if (roundInfo == null) {
                        continue;
                    }
                    roundInfo.needPushReport13 = true;
                }
                if (campArmy == null) {
                    break;
                }
            }
            caSet.add(campArmy);
            campArmy.setOnQueues(true);
            if (campArmy.playerId > 0) {
                final PlayerInfo playerInfo = bat.inBattlePlayers.get(campArmy.playerId);
                if (playerInfo != null) {
                    playerInfo.battleMode = 2;
                }
            }
            addQueues(bat, attAddQlist, batList, campArmy, battleSide, batQNum);
        }
        for (final CampArmy ca : caSet) {
            if (ca.getPlayerId() > 0 && !ca.isPhantom && !bat.inSceneSet.contains(ca.getPlayerId()) && ca.updateDB) {
                final JsonDocument doc2 = new JsonDocument();
                doc2.startObject();
                doc2.createElement("onQueuesGId", ca.getGeneralId());
                doc2.endObject();
                Players.push(ca.getPlayerId(), PushCommand.PUSH_GENERAL_INFO2, doc2.toByte());
            }
        }
    }
    
    public static void addQueues(final Battle battle, final List<BattleArmy> attAddQlist, final List<BattleArmy> attList, final CampArmy campArmy, final int battleSide, final AtomicInteger batQNum) {
        int perRow = campArmy.troopHp;
        if (perRow > campArmy.armyHp) {
            perRow = campArmy.armyHp;
        }
        perRow /= 3;
        if (perRow == 0) {
            perRow = 1;
        }
        campArmy.setArmyHp(campArmy.armyHp - perRow * 3);
        final BattleArmy battleArmy = new BattleArmy();
        battleArmy.setCampArmy(campArmy);
        battleArmy.setPosition(batQNum.getAndIncrement());
        for (int i = 0; i < 3; ++i) {
            battleArmy.getTroopHp()[i] = perRow;
        }
        int curStrategy = 0;
        if (campArmy.isPhantom) {
            if (campArmy.curStrategy == 0) {
                curStrategy = campArmy.getStrategies()[WebUtil.nextInt(campArmy.getStrategies().length)];
                campArmy.curStrategy = curStrategy;
            }
            else {
                curStrategy = campArmy.curStrategy;
            }
        }
        else {
            curStrategy = campArmy.getStrategies()[WebUtil.nextInt(campArmy.getStrategies().length)];
        }
        battleArmy.setStrategy(curStrategy);
        attAddQlist.add(battleArmy);
        attList.add(battleArmy);
    }
    
    public boolean canAddQueues(final List<BattleArmy[]> attList, final CampArmy campArmy) {
        if (campArmy.armyHp >= campArmy.maxForces * 0.05) {
            for (final BattleArmy[] bas : attList) {
                if (bas[0].getCampArmy().getId() == campArmy.getId()) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    
    public boolean canAddQueuesForDef(final List<BattleArmy[]> battleList, final CampArmy campArmy) {
        if (campArmy.armyHp >= campArmy.troopHp * 0.2) {
            for (final BattleArmy[] bas : battleList) {
                if (bas[0].getCampArmy().getId() == campArmy.getId()) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    
    public static void allFrocesToString(final StringBuilder battleMsg, final int reportType, final String battleSide, final BaseInfo baseInfo) {
        battleMsg.append(reportType).append("|").append(battleSide).append(";");
        battleMsg.append(baseInfo.num).append("|").append(baseInfo.allNum).append("#");
    }
    
    public static void getCurBattleTroop(final IDataGetter dataGetter, final Battle bat, final RoundInfo roundInfo) {
        roundInfo.troopData = new TroopData[2][3];
        copyAttCurBattleInfo(dataGetter, bat, roundInfo.troopData[0], roundInfo.attCampArmy, roundInfo.defCampArmy, roundInfo.attBattleArmy, bat.all_damage_e_att, roundInfo.defCampArmy.isBarPhantom || roundInfo.defCampArmy.isBarEA);
        copyDefCurBattleInfo(dataGetter, bat, roundInfo.troopData[1], roundInfo.attCampArmy, roundInfo.defCampArmy, roundInfo.defBattleArmy, bat.all_damage_e_def, roundInfo.attCampArmy.isBarPhantom || roundInfo.defCampArmy.isBarEA);
        if (bat.world_frontLine_buff_att_def_e_side == 1) {
            for (int i = 0; i < 3; ++i) {
                roundInfo.troopData[0][i].world_weaken_frontLine_buff = bat.world_frontLine_buff_att_def_e;
            }
        }
        else if (bat.world_frontLine_buff_att_def_e_side == 2) {
            for (int i = 0; i < 3; ++i) {
                roundInfo.troopData[1][i].world_weaken_frontLine_buff = bat.world_frontLine_buff_att_def_e;
            }
        }
    }
    
    public static void copyAttCurBattleInfo(final IDataGetter dataGetter, final Battle bat, final TroopData[] troopData, final CampArmy attCampArmy, final CampArmy defCampArmy, final BattleArmy battleArmy, final double all_damage_e, final boolean vsSideIsBarPhantom) {
        for (int i = 0; i < 3; ++i) {
            final TroopData tempData = new TroopData();
            tempData.troop_id = battleArmy.getCampArmy().getId();
            tempData.hp = battleArmy.getTroopHp()[i];
            tempData.att = attCampArmy.getAttEffect() + bat.att_gongji_base_add;
            tempData.def = attCampArmy.getDefEffect() + bat.att_fangyu_base_add;
            tempData.base_damage = attCampArmy.getBdEffect();
            if (attCampArmy.getAttDef_B() != null) {
                tempData.ATT_B = attCampArmy.getAttDef_B().ATT_B;
                tempData.DEF_B = attCampArmy.getAttDef_B().DEF_B;
            }
            tempData.Str = attCampArmy.getStrength();
            tempData.Lea = attCampArmy.getLeader();
            tempData.max_hp = attCampArmy.getTroopHp() / 3;
            if (defCampArmy.specialGeneral.generalType == 11) {
                tempData.terrain_effect = 0.0;
            }
            else {
                tempData.terrain_effect = attCampArmy.getTerrain();
            }
            tempData.all_damage_e = all_damage_e;
            tempData.world_legion_e = attCampArmy.getTeamEffect();
            if (attCampArmy.specialGeneral.generalType == 2) {
                tempData.isFS = true;
                tempData.world_fs_d = (int)attCampArmy.specialGeneral.param;
                if (tempData.world_fs_d <= 0) {
                    tempData.isFS = false;
                }
            }
            else {
                tempData.isFS = false;
                tempData.world_fs_d = 0;
            }
            if (attCampArmy.specialGeneral.generalType == 3 && vsSideIsBarPhantom) {
                tempData.isMz = true;
                tempData.world_mz_e = attCampArmy.specialGeneral.param;
            }
            else {
                tempData.isMz = false;
                tempData.world_mz_e = 0.0;
            }
            if (attCampArmy.specialGeneral.generalType == 6 && (bat.getBattleType() == 3 || bat.getBattleType() == 13)) {
                tempData.isBS = true;
                tempData.BS_My = bat.attCamp.size();
                tempData.BS_Your = bat.defCamp.size();
            }
            final Map<Integer, Double> playerGemAttribute = dataGetter.getBattleDataCache().getGemAttribute(attCampArmy.getPlayerId());
            if (playerGemAttribute == null || playerGemAttribute.isEmpty()) {
                troopData[i] = tempData;
            }
            else {
                if (bat.getBattleType() == 13 || bat.getBattleType() == 15 || bat.getBattleType() == 19) {
                    tempData.JS_SKILL_dt = playerGemAttribute.get(5);
                }
                tempData.JS_SKILL_ms = playerGemAttribute.get(1);
                tempData.JS_SKILL_bj = playerGemAttribute.get(2);
                tempData.JS_SKILL_att = playerGemAttribute.get(7);
                troopData[i] = tempData;
            }
        }
    }
    
    public static void copyDefCurBattleInfo(final IDataGetter dataGetter, final Battle bat, final TroopData[] troopData, final CampArmy attCampArmy, final CampArmy defCampArmy, final BattleArmy battleArmy, final double all_damage_e, final boolean vsSideIsBarPhantom) {
        for (int i = 0; i < 3; ++i) {
            final TroopData tempData = new TroopData();
            tempData.troop_id = battleArmy.getCampArmy().getId();
            tempData.hp = battleArmy.getTroopHp()[i];
            tempData.att = defCampArmy.getAttEffect() + bat.def_gongji_base_add;
            tempData.def = defCampArmy.getDefEffect() + bat.def_fangyu_base_add;
            tempData.base_damage = defCampArmy.getBdEffect();
            if (defCampArmy.getAttDef_B() != null) {
                tempData.ATT_B = defCampArmy.getAttDef_B().ATT_B;
                tempData.DEF_B = defCampArmy.getAttDef_B().DEF_B;
            }
            tempData.Str = defCampArmy.getStrength();
            tempData.Lea = defCampArmy.getLeader();
            tempData.max_hp = defCampArmy.getTroopHp() / 3;
            if (attCampArmy.specialGeneral.generalType == 11) {
                tempData.terrain_effect = 0.0;
            }
            else {
                tempData.terrain_effect = defCampArmy.getTerrain();
            }
            tempData.all_damage_e = all_damage_e;
            tempData.world_weaken_besiege = bat.world_weaken_besiege_e;
            tempData.world_legion_e = defCampArmy.getTeamEffect();
            if (defCampArmy.specialGeneral.generalType == 2) {
                tempData.isFS = true;
                tempData.world_fs_d = (int)defCampArmy.specialGeneral.param;
                if (tempData.world_fs_d <= 0) {
                    tempData.isFS = false;
                }
            }
            else {
                tempData.isFS = false;
                tempData.world_fs_d = 0;
            }
            if (defCampArmy.specialGeneral.generalType == 3 && vsSideIsBarPhantom) {
                tempData.isMz = true;
                tempData.world_mz_e = defCampArmy.specialGeneral.param;
            }
            else {
                tempData.isMz = false;
                tempData.world_mz_e = 0.0;
            }
            if (defCampArmy.specialGeneral.generalType == 6 && (bat.getBattleType() == 3 || bat.getBattleType() == 13)) {
                tempData.isBS = true;
                tempData.BS_My = bat.defCamp.size();
                tempData.BS_Your = bat.attCamp.size();
            }
            final Map<Integer, Double> playerGemAttribute = dataGetter.getBattleDataCache().getGemAttribute(defCampArmy.getPlayerId());
            if (playerGemAttribute == null || playerGemAttribute.isEmpty()) {
                troopData[i] = tempData;
            }
            else {
                if (bat.getBattleType() == 13 || bat.getBattleType() == 15 || bat.getBattleType() == 19) {
                    tempData.JS_SKILL_dt = playerGemAttribute.get(5);
                }
                tempData.JS_SKILL_ms = playerGemAttribute.get(1);
                tempData.JS_SKILL_bj = playerGemAttribute.get(2);
                tempData.JS_SKILL_def = playerGemAttribute.get(6);
                troopData[i] = tempData;
            }
        }
    }
    
    public int isBattleEnd(final IDataGetter dataGetter, final Battle bat) {
        final int aQLsize = bat.attList.size();
        final int dQLsize = bat.defList.size();
        if (aQLsize < 1 && dQLsize < 1) {
            return 4;
        }
        if (aQLsize < 1) {
            return 2;
        }
        if (dQLsize < 1) {
            return 3;
        }
        return 1;
    }
    
    public void sendBattleCityEndInfo(final IDataGetter dataGetter, final Battle bat) {
    }
    
    public String getAttTargetName(final IDataGetter dataGetter, final Battle bat) {
        final Armies armies = (Armies)dataGetter.getArmiesCache().get((Object)bat.getDefBaseInfo().getId());
        return armies.getName();
    }
    
    public int battleRewardSave(final boolean attWin, final int playerId, final PlayerInfo pi, final Date date, final IDataGetter dataGetter, final BattleResult battleResult, final Battle bat) {
        final PlayerBattleReward pbr = new PlayerBattleReward();
        pbr.setPlayerId(playerId);
        pbr.setGainTime(date);
        pbr.setGExp(0);
        if (bat.getBattleType() == 2 || bat.getBattleType() == 5) {
            pbr.setType(1);
        }
        else {
            pbr.setType(bat.getBattleType());
        }
        pbr.setAttLost(pi.lostTotal);
        pbr.setDefLost(pi.killTotal);
        pbr.setWinSide(2);
        if (pi.isAttSide && attWin) {
            pbr.setWinSide(1);
        }
        if (!pi.isAttSide && !attWin) {
            pbr.setWinSide(1);
        }
        pbr.setDefId(bat.getDefBaseInfo().getId());
        pbr.setAttForceId(bat.getAttBaseInfo().getForceId());
        if (bat.isNpc) {
            pbr.setDefForceId(0);
        }
        else {
            pbr.setDefForceId(bat.getDefBaseInfo().getForceId());
        }
        pbr.setTerrain(bat.terrain);
        pbr.setMaxKillGnum(pi.maxKillG);
        final StringBuilder rbReward = new StringBuilder();
        rbReward.append(pi.rbType).append("#").append(pi.rbTotal).append("#").append(pi.rbTop);
        pbr.setRbReward(rbReward.toString());
        pbr.setGeneralIds("");
        dataGetter.getPlayerBattleRewardDao().create(pbr);
        return pbr.getVId();
    }
    
    public void getExReward(final boolean attWin, final IDataGetter dataGetter, final PlayerInfo pi, final Battle bat) {
    }
    
    public String getDefName(final IDataGetter dataGetter, final int defId) {
        return "null";
    }
    
    public static void getReportType13(final Battle battle, final StringBuilder battleMsg) {
        final List<CampArmy> attCaList = new ArrayList<CampArmy>();
        final List<CampArmy> defCaList = new ArrayList<CampArmy>();
        final Set<CampArmy> attOnQueueSet = new HashSet<CampArmy>();
        final Set<CampArmy> defOnQueueSet = new HashSet<CampArmy>();
        Set<CampArmy> newlyJoinSet = new HashSet<CampArmy>();
        for (final BattleArmy ba : battle.attList) {
            final CampArmy ca = ba.getCampArmy();
            if (!attOnQueueSet.contains(ca)) {
                attOnQueueSet.add(ca);
                attCaList.add(ba.getCampArmy());
            }
        }
        for (final BattleArmy ba : battle.defList) {
            final CampArmy ca = ba.getCampArmy();
            if (!defOnQueueSet.contains(ca)) {
                defOnQueueSet.add(ca);
                defCaList.add(ca);
            }
        }
        boolean attNeedPush = false;
        boolean defNeedPush = false;
        final int attListSize = battle.getAttList().size();
        final int defListSize = battle.getDefList().size();
        int lastAttOnQueuePos = 0;
        int lastDefOnQueuePos = 0;
        if (attListSize > 0) {
            lastAttOnQueuePos = battle.attList.get(attListSize - 1).getPosition();
        }
        if (defListSize > 0) {
            lastDefOnQueuePos = battle.defList.get(defListSize - 1).getPosition();
        }
        if (lastAttOnQueuePos != battle.lastAttOnQueuePosId) {
            battle.lastAttOnQueuePosId = lastAttOnQueuePos;
            attNeedPush = true;
        }
        if (lastDefOnQueuePos != battle.lastDefOnQueuePosId) {
            battle.lastDefOnQueuePosId = lastDefOnQueuePos;
            defNeedPush = true;
        }
        for (final CampArmy ca2 : battle.attCamp) {
            if (ca2.getArmyHp() > 0 && !attOnQueueSet.contains(ca2)) {
                attCaList.add(ca2);
            }
        }
        for (final CampArmy ca2 : battle.defCamp) {
            if (ca2.getArmyHp() > 0 && !defOnQueueSet.contains(ca2)) {
                defCaList.add(ca2);
            }
        }
        newlyJoinSet = battle.newlyJoinSet;
        battle.newlyJoinSet = new HashSet<CampArmy>();
        if (attOnQueueSet.size() > 0 || defOnQueueSet.size() > 0 || newlyJoinSet.size() > 0) {
            battleMsg.append(13).append("|");
            if (attOnQueueSet.size() > 0 && attNeedPush) {
                int i = 0;
                for (final CampArmy ca3 : attOnQueueSet) {
                    int playerId = ca3.getPlayerId();
                    if (ca3.isPhantom) {
                        playerId = -playerId;
                    }
                    battleMsg.append(i + 1).append(",").append(playerId).append(",").append(ca3.getPlayerName()).append(",").append(ca3.getGeneralName()).append(",").append(ca3.getQuality()).append(",").append(1).append(",").append(0).append(",").append(ca3.getForceId()).append(";");
                    ++i;
                }
                battleMsg.replace(battleMsg.length() - 1, battleMsg.length(), "|");
            }
            else {
                battleMsg.append("null").append("|");
            }
            int j = 0;
            if (defOnQueueSet.size() > 0 && defNeedPush) {
                for (final CampArmy ca3 : defOnQueueSet) {
                    int playerId = ca3.getPlayerId();
                    if (ca3.isPhantom) {
                        playerId = -playerId;
                    }
                    battleMsg.append(j + 1).append(",").append(playerId).append(",").append(ca3.getPlayerName()).append(",").append(ca3.getGeneralName()).append(",").append(ca3.getQuality()).append(",").append(1).append(",").append(0).append(",").append(ca3.getForceId()).append(";");
                    ++j;
                }
                battleMsg.replace(battleMsg.length() - 1, battleMsg.length(), "|");
            }
            else {
                battleMsg.append("null").append("|");
            }
            if (newlyJoinSet.size() > 0) {
                int k = 0;
                for (final CampArmy ca4 : newlyJoinSet) {
                    int playerId2 = ca4.getPlayerId();
                    int isPhantom = 0;
                    if (ca4.isPhantom) {
                        playerId2 = -playerId2;
                        isPhantom = 1;
                    }
                    int isDef = 0;
                    if (ca4.getForceId() == battle.getDefBaseInfo().getForceId()) {
                        isDef = 1;
                    }
                    battleMsg.append(k + 1).append(",").append(playerId2).append(",").append(ca4.getPlayerName()).append(",").append(ca4.getGeneralName()).append(",").append(ca4.getQuality()).append(",").append(0).append(",").append(1).append(",").append(isDef).append(",").append(isPhantom).append(",").append(ca4.getForceId()).append(";");
                    ++k;
                }
                battleMsg.replace(battleMsg.length() - 1, battleMsg.length(), "|");
            }
            else {
                battleMsg.append("null").append("|");
            }
            battleMsg.append(attCaList.size()).append("|").append(defCaList.size()).append("#");
        }
    }
    
    public void updateGeneralDB(final IDataGetter dataGetter, final Battle bat, final CampArmy ca, final boolean attWin) {
        final int state = 1;
        dataGetter.getPlayerGeneralMilitaryDao().restartRecruit(ca.getPlayerId(), ca.getGeneralId(), state, new Date());
        dataGetter.getGeneralService().sendGmUpdate(ca.getPlayerId(), ca.getGeneralId(), bat.inSceneSet.contains(ca.getPlayerId()));
    }
    
    public void endCampsDeal(final IDataGetter dataGetter, final Battle bat, final BattleResult battleResult, final boolean attWin) {
        int i = 1;
        for (final CampArmy ca : bat.attCamp) {
            BattleSceneLog.getInstance().info("#batId:" + bat.getBattleId() + "_" + bat.getStartTime() + "#quit:" + (attWin ? "win" : "loss") + "#side:att" + "#playerId:" + ca.getPlayerId() + ":" + ca.isPhantom + "#general:" + ca.getGeneralId() + "#attSize:" + (bat.attCamp.size() - i));
            ++i;
            this.endQuitNewBattle(ca, 1, bat);
            if (!bat.inBattlePlayers.containsKey(ca.getPlayerId())) {
                continue;
            }
            final PlayerInfo pi = bat.inBattlePlayers.get(ca.getPlayerId());
            if (pi != null && ca.killGeneral > pi.maxKillG) {
                pi.maxKillG = ca.killGeneral;
            }
            if (!ca.inBattle || ca.armyHp <= -1) {
                continue;
            }
            this.updateGeneralDB(dataGetter, bat, ca, attWin);
        }
        i = 1;
        for (final CampArmy ca : bat.defCamp) {
            BattleSceneLog.getInstance().info("#batId:" + bat.getBattleId() + "_" + bat.getStartTime() + "#quit:" + (attWin ? "loss" : "win") + "#side:att" + "#playerId:" + ca.getPlayerId() + ":" + ca.isPhantom + "#general:" + ca.getGeneralId() + "#defSize:" + (bat.defCamp.size() - i));
            ++i;
        }
    }
    
    public void endBattle(final int winSide, final IDataGetter dataGetter, final Battle bat) {
        final boolean attWin = winSide == 3;
        if (bat.battleType != 14) {
            if (attWin && bat.getDefBaseInfo().getNum() != 0) {
                ErrorSceneLog.getInstance().appendErrorMsg("att Win\uff0c\u5b88\u65b9\u5175\u529b\u4e0d\u4e3a0").appendClassName("Builder").appendMethodName("endBattle").append("battleId", bat.getBattleId()).append("defLeftForce", bat.getDefBaseInfo().getNum()).flush();
            }
            else if (!attWin && bat.getAttBaseInfo().getNum() != 0) {
                ErrorSceneLog.getInstance().appendErrorMsg("def Win\uff0c\u653b\u65b9\u5175\u529b\u4e0d\u4e3a0 ").appendClassName("Builder").appendMethodName("endBattle").append("battleId", bat.getBattleId()).append("attLeftForce", bat.getAttBaseInfo().getNum()).flush();
            }
        }
        final BattleResult battleResult = new BattleResult();
        this.gainExReward(attWin, battleResult, bat, dataGetter);
        this.countNpcReward(attWin, dataGetter, bat, battleResult);
        try {
            if (bat.getBattleType() != 3) {
                dataGetter.getBattleService().dealNextNpc(this, attWin, dataGetter, bat, battleResult);
            }
            else {
                dataGetter.getBattleService().dealNextNpcBuidler(this, attWin, dataGetter, bat, battleResult);
            }
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().appendErrorMsg("dealNextNpc Exception").appendClassName(this.getClass().getName()).appendMethodName("dealNextNpc").append("attWin", attWin).flush();
            ErrorSceneLog.getInstance().error("dealNextNpc Exception", e);
        }
        this.endCampsDeal(dataGetter, bat, battleResult, attWin);
        try {
            this.dealTaskAfterWin(attWin, dataGetter, bat, battleResult);
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("dealTaskAfterWin Exception", e);
        }
        this.endBattleMsg(attWin, battleResult, bat, dataGetter);
        this.deleteBattleInfo(dataGetter, bat);
        this.afterBat(attWin, dataGetter, bat);
        if (bat.getBattleType() == 3) {
            dataGetter.getJobService().addJob("autoBattleService", "stopAutoBattleAfterBattleEnded", new StringBuilder().append(bat.getDefBaseInfo().getId()).toString(), System.currentTimeMillis(), false);
        }
    }
    
    public void dealTaskAfterWin(final boolean attWin, final IDataGetter dataGetter, final Battle bat, final BattleResult battleResult) {
    }
    
    public void deleteBattleInfo(final IDataGetter dataGetter, final Battle bat) {
        dataGetter.getBattleService().dealTokenBattle(bat.getBattleId(), 0);
        dataGetter.getBattleInfoService().deleteBattle(bat);
    }
    
    public void afterBat(final boolean attWin, final IDataGetter dataGetter, final Battle bat) {
    }
    
    public void dealNextNpc(final boolean attWin, final IDataGetter dataGetter, final Battle bat, final BattleResult battleResult) {
        for (final int playerId : bat.inBattlePlayers.keySet()) {
            final PlayerInfo pi = bat.inBattlePlayers.get(playerId);
            if (pi.isAttSide) {
                final Armies curArmies = (Armies)dataGetter.getArmiesCache().get((Object)bat.defBaseInfo.getId());
                if (attWin) {
                    final PlayerArmy pa = dataGetter.getPlayerArmyDao().getPlayerArmy(playerId, curArmies.getId());
                    if (pa.getWinNum() < 1) {
                        this.dealStuffAfterFirstWin(dataGetter, playerId, curArmies, pa);
                    }
                    else {
                        dataGetter.getPlayerArmyDao().updateAttackWinNum(playerId, curArmies.getId(), 1, 1);
                    }
                    final Armies nextArmies = dataGetter.getArmiesCache().getNextArmies(bat.defBaseInfo.getId());
                    if (nextArmies != null) {
                        dataGetter.getPlayerArmyDao().updateAttackable(playerId, nextArmies.getId(), 1);
                    }
                    if (dataGetter.getArmiesCache().isLastArmies(bat.defBaseInfo.getId())) {
                        final Power power = (Power)dataGetter.getPowerCache().get((Object)curArmies.getPowerId());
                        dataGetter.getPlayerPowerDao().updateRewardState(playerId, curArmies.getPowerId(), 1);
                        final PlayerPower pp = dataGetter.getPlayerPowerDao().getPlayerPower(playerId, power.getNextPower());
                        if (pp != null && pp.getAttackable() != 1) {
                            dataGetter.getPlayerPowerDao().updateAttackable(playerId, power.getNextPower(), 1);
                        }
                    }
                    TaskMessageHelper.sendBattleWinTaskMessage(playerId, bat.defBaseInfo.getId());
                    dataGetter.getPlayerAttributeDao().addBattleWinTimes(playerId);
                    final Map<Integer, BattleDrop> dropMap = curArmies.getDropMap();
                    if (dropMap == null) {
                        continue;
                    }
                    for (final BattleDrop battleDrop : dropMap.values()) {
                        if (battleDrop.type > 200 && battleDrop.type < 800) {
                            final Items dropItem = (Items)dataGetter.getItemsCache().get((Object)battleDrop.id);
                            if (dropItem.getType() == 2) {
                                if (pa.getDropCount() + battleDrop.num > battleDrop.limit) {
                                    continue;
                                }
                                int taskId = 0;
                                final String targrtPrefix = "collect," + battleDrop.id;
                                for (final Task task : dataGetter.getTaskCache().getModels()) {
                                    if (task.getTarget().contains(targrtPrefix)) {
                                        taskId = task.getId();
                                    }
                                }
                                final PlayerTask playerTask = dataGetter.getPlayerTaskDao().getCurMainTask(playerId);
                                if (playerTask == null || playerTask.getTaskId() != taskId || WebUtil.nextDouble() >= battleDrop.pro) {
                                    continue;
                                }
                                dataGetter.getPlayerArmyDao().addDropCount(playerId, curArmies.getId(), battleDrop.num);
                                dataGetter.getStoreHouseService().gainItems(playerId, battleDrop.num, battleDrop.id, LocalMessages.T_LOG_ITEM_1);
                                pi.dropMap.put(battleDrop.type, new BattleDrop(battleDrop));
                                TaskMessageHelper.sendItemCollectTaskMessage(playerId, battleDrop.id);
                            }
                            else {
                                if ((dropItem.getType() != 3 && dropItem.getType() != 4) || pa.getDropCount() + battleDrop.num > battleDrop.limit) {
                                    continue;
                                }
                                try {
                                    if (WebUtil.nextDouble() >= battleDrop.pro) {
                                        continue;
                                    }
                                    dataGetter.getPlayerArmyDao().addDropCount(playerId, curArmies.getId(), battleDrop.num);
                                    dataGetter.getStoreHouseService().gainItems(playerId, battleDrop.num, battleDrop.id, LocalMessages.T_LOG_ITEM_1);
                                    pi.dropMap.put(battleDrop.type, new BattleDrop(battleDrop));
                                }
                                catch (Exception e) {
                                    ErrorSceneLog.getInstance().appendErrorMsg("StoreHouseService gainItems Exception").appendClassName("Builder").appendMethodName("dealNextNpc").append("playerId", playerId).append("curArmies", curArmies.getName()).flush();
                                    ErrorSceneLog.getInstance().error("builder dealNextNpc 1 ", e);
                                }
                            }
                        }
                        else {
                            if (battleDrop.type < 800) {
                                continue;
                            }
                            final BuildingDrawing draw = (BuildingDrawing)dataGetter.getBuildingDrawingCache().get((Object)battleDrop.id);
                            if (draw == null) {
                                continue;
                            }
                            try {
                                if (!dataGetter.getBuildingService().dropBluePrintById(playerId, draw.getId())) {
                                    continue;
                                }
                                pi.dropMap.put(battleDrop.type, new BattleDrop(battleDrop));
                            }
                            catch (Exception e) {
                                ErrorSceneLog.getInstance().appendErrorMsg("BuildingService dropBluePrintById Exception").appendClassName("Builder").appendMethodName("dealNextNpc").append("playerId", playerId).append("curArmies", curArmies.getName()).append("BuildingDrawing", draw.getId()).flush();
                                ErrorSceneLog.getInstance().error("builder dealNextNpc 2 ", e);
                            }
                        }
                    }
                }
                else {
                    dataGetter.getPlayerArmyDao().updateAttNum(playerId, curArmies.getId(), 1);
                }
            }
        }
    }
    
    public void dealStuffAfterFirstWin(final IDataGetter dataGetter, final int playerId, final Armies curArmies, final PlayerArmy pa) {
        if (curArmies.getType() == 2) {
            dataGetter.getCourtesyService().addPlayerEvent(playerId, 4, 0);
        }
        dataGetter.getPlayerArmyDao().updateAttackWin(playerId, curArmies.getId(), 1, 1, 1);
        if (curArmies != null && dataGetter.getArmiesCache().getHasGold().contains(curArmies.getId())) {
            dataGetter.getPlayerArmyDao().updateGoldReward(playerId, curArmies.getId(), 1);
        }
        dataGetter.getPlayerDao().updateMaxLv(playerId, curArmies.getLevel() + 10);
        if (pa.getArmyId() == 31) {
            dataGetter.getPlayerBattleAttributeDao().updateArmiesAutoCount(playerId, 5);
        }
        else if (pa.getArmyId() == 41) {
            dataGetter.getPlayerBattleAttributeDao().updateArmiesAutoCount(playerId, 10);
        }
        else if (pa.getArmyId() == 50) {
            dataGetter.getPlayerBattleAttributeDao().updateArmiesAutoCount(playerId, 99);
        }
        final Map<Integer, BattleDrop> dropMap = curArmies.getDropMap();
        if (dropMap != null) {
            BattleDrop battleDrop = dropMap.get(103);
            int rewardNpcId = 0;
            if (battleDrop != null) {
                rewardNpcId = battleDrop.id;
            }
            if (rewardNpcId != 0) {
                PlayerArmyReward playerArmyReward = dataGetter.getPlayerArmyRewardDao().getPlayerArmyRewardByArmyId(playerId, rewardNpcId);
                if (playerArmyReward == null) {
                    playerArmyReward = new PlayerArmyReward();
                    playerArmyReward.setPlayerId(playerId);
                    playerArmyReward.setPowerId(curArmies.getPowerId());
                    playerArmyReward.setArmyId(rewardNpcId);
                    playerArmyReward.setFirst(1);
                    final int miniuteNum = ((ArmiesReward)dataGetter.getArmiesRewardCache().get((Object)rewardNpcId)).getTime();
                    playerArmyReward.setExpireTime(new Date(System.currentTimeMillis() + miniuteNum * 60000L));
                    playerArmyReward.setNpcLost(null);
                    final int num = OneVsRewardNpcBuilder.getMaxHp(dataGetter, rewardNpcId);
                    playerArmyReward.setHp(num);
                    playerArmyReward.setHpMax(num);
                    playerArmyReward.setState(0);
                    playerArmyReward.setBuyCount(0);
                    playerArmyReward.setFirstWin(0);
                    playerArmyReward.setWinCount(0);
                    dataGetter.getPlayerArmyRewardDao().create(playerArmyReward);
                }
            }
            battleDrop = dropMap.get(104);
            int extraPowerId = 0;
            if (battleDrop != null) {
                extraPowerId = battleDrop.id;
            }
            if (extraPowerId != 0) {
                dealExtraPower(dataGetter, playerId, extraPowerId);
            }
            battleDrop = dropMap.get(102);
            int techId = 0;
            if (battleDrop != null) {
                techId = battleDrop.id;
            }
            if (techId != 0) {
                try {
                    dataGetter.getTechService().dropTech(playerId, techId);
                }
                catch (Exception e) {
                    ErrorSceneLog.getInstance().appendErrorMsg("TechService dropTech Exception").appendClassName("Builder").appendMethodName("dealStuffAfterFirstWin").append("playerId", playerId).append("techId", techId).flush();
                    ErrorSceneLog.getInstance().error("dealStuffAfterFirstWin 1", e);
                }
            }
            battleDrop = dropMap.get(105);
            int juBenId = 0;
            if (battleDrop != null) {
                juBenId = battleDrop.id;
            }
            if (juBenId != 0) {
                try {
                    dataGetter.getJuBenService().openNextJuBen(playerId, juBenId);
                }
                catch (Exception e2) {
                    ErrorSceneLog.getInstance().appendErrorMsg("JuBenService openNextJuBen Exception").appendClassName("Builder").appendMethodName("dealStuffAfterFirstWin").append("playerId", playerId).append("juBenId", juBenId).flush();
                    ErrorSceneLog.getInstance().error("dealStuffAfterFirstWin 2", e2);
                }
            }
            final Armies nextArmies = dataGetter.getArmiesCache().getNextArmies(curArmies.getId());
            if (nextArmies != null && nextArmies.getType() != 1) {
                try {
                    dataGetter.getBroadCastUtil().sendWinNPCBroadCast(playerId, curArmies.getName());
                }
                catch (Exception e3) {
                    ErrorSceneLog.getInstance().appendErrorMsg("BroadCastUtil WinNPCBroadCast Exception").appendClassName("Builder").appendMethodName("dealNextNpc").append("playerId", playerId).append("curArmies", curArmies.getName()).flush();
                    ErrorSceneLog.getInstance().error("dealStuffAfterFirstWin 2", e3);
                }
            }
        }
    }
    
    public static void dealExtraPower(final IDataGetter dataGetter, final int playerId, final int extraPowerId) {
        PlayerPower playerPower = dataGetter.getPlayerPowerDao().getPlayerPower(playerId, extraPowerId);
        if (playerPower == null) {
            playerPower = new PlayerPower();
            playerPower.setPlayerId(playerId);
            playerPower.setPowerId(extraPowerId);
            playerPower.setAttackable(1);
            playerPower.setComplete(0);
            playerPower.setReward(0);
            playerPower.setExpireTime(new Date(System.currentTimeMillis() + 86400000L));
            playerPower.setState(-1);
            playerPower.setBuyCount(0);
            dataGetter.getPlayerPowerDao().create(playerPower);
            final List<PlayerArmyExtra> PlayerArmiesExtraList = dataGetter.getPlayerArmyExtraDao().getArmiesByPowerId(playerId, extraPowerId);
            if (PlayerArmiesExtraList != null && PlayerArmiesExtraList.size() > 0) {
                ErrorSceneLog.getInstance().appendErrorMsg("power_extra\u8d2d\u4e70,PlayerPower\u65e0\u8bb0\u5f55,PlayerArmyExtra\u5df2\u7ecf\u6709\u8bb0\u5f55.").appendClassName("PowerService").appendMethodName("buyPowerExtra").append("playerId", playerId).append("extraPowerId", extraPowerId).flush();
                return;
            }
            final List<ArmiesExtra> armiesExtraList = dataGetter.getArmiesExtraCache().getArmiesExtraByPowerId(extraPowerId);
            for (int i = 0; i < armiesExtraList.size(); ++i) {
                final ArmiesExtra armiesExtra = armiesExtraList.get(i);
                final PlayerArmyExtra playerArmyExtra = new PlayerArmyExtra();
                playerArmyExtra.setPlayerId(playerId);
                playerArmyExtra.setPowerId(armiesExtra.getPowerId());
                playerArmyExtra.setArmyId(armiesExtra.getId());
                playerArmyExtra.setAttackable(0);
                if (i == 0) {
                    playerArmyExtra.setAttackable(1);
                }
                playerArmyExtra.setAttNum(0);
                playerArmyExtra.setFirstOpen(1);
                playerArmyExtra.setFirstWin(0);
                playerArmyExtra.setWinNum(0);
                final int num = OneVsExtraBuilder.getMaxHp(dataGetter, armiesExtra.getId());
                playerArmyExtra.setHp(num);
                playerArmyExtra.setHpMax(num);
                playerArmyExtra.setNpcLost(null);
                dataGetter.getPlayerArmyExtraDao().create(playerArmyExtra);
            }
        }
    }
    
    public void countNpcReward(final boolean attWin, final IDataGetter dataGetter, final Battle bat, final BattleResult battleResult) {
        final Armies armies = (Armies)dataGetter.getArmiesCache().get((Object)bat.defBaseInfo.getId());
        final Map<Integer, BattleDrop> dropMap = armies.getDropMap();
        BattleDrop battleDrop = null;
        if (dropMap != null) {
            battleDrop = dropMap.get(101);
        }
        if (battleDrop != null) {
            final General general = (General)dataGetter.getGeneralCache().get((Object)battleDrop.id);
            if (general != null) {
                for (final int playerId : bat.inBattlePlayers.keySet()) {
                    final PlayerInfo pi = bat.inBattlePlayers.get(playerId);
                    if (((pi.isAttSide && attWin) || (!pi.isAttSide && !attWin)) && WebUtil.nextDouble() < battleDrop.pro) {
                        final String gId = general.getId() + ",";
                        final PlayerTavern playerTavern = dataGetter.getPlayerTavernDao().read(playerId);
                        if (playerTavern == null) {
                            return;
                        }
                        if (general.getType() == 1) {
                            if (playerTavern.getCivilInfo() != null && playerTavern.getCivilInfo().contains(gId)) {
                                continue;
                            }
                            dataGetter.getPlayerTavernDao().updateCivilInfo(playerId, gId);
                        }
                        else {
                            if (playerTavern.getMilitaryInfo() != null && playerTavern.getMilitaryInfo().contains(gId)) {
                                continue;
                            }
                            dataGetter.getPlayerTavernDao().updateMilitaryInfo(playerId, gId);
                        }
                        battleResult.dropGId = general.getId();
                        battleResult.dropGName = general.getName();
                        battleResult.dropGPic = general.getPic();
                        battleResult.dropGQuality = general.getQuality();
                        battleResult.dropGType = general.getType();
                        if (general.getType() == 1) {
                            battleResult.gTroopName = LocalMessages.T_CIVIL_NAME;
                            battleResult.gTroopQuality = 1;
                        }
                        else {
                            final Troop troop = dataGetter.getTroopCache().getTroop(general.getTroop(), playerId);
                            battleResult.gTroopName = troop.getName();
                            battleResult.gTroopQuality = troop.getQuality();
                        }
                        if ((general.getId() != 225 && general.getId() != 266) || !dataGetter.getTavernService().recruitGeneralDirect(playerId, general.getId(), true)) {
                            continue;
                        }
                        battleResult.dropGget = 1;
                    }
                }
            }
        }
    }
    
    public static void sendMsgToAll(final Battle bat, final StringBuilder battleMsg) {
        final String headInfo = " battleId " + bat.getBattleId() + " \u53d1\u9001\u7ed9\u6240\u6709\u4eba  battleMsg:";
        final String saveReport = Configuration.getProperty("gcld.battle.report.save");
        if (saveReport.equals("1")) {
            getLog(battleMsg.toString(), headInfo);
        }
        final String quickMode = Configuration.getProperty("gcld.battle.quick");
        if (!quickMode.equals("1")) {
            for (final Integer playerId : bat.inSceneSet) {
                if (NewBattleManager.getInstance().isWatchBattle(playerId, bat.getBattleId())) {
                    sendMsgToOne(playerId, battleMsg);
                }
            }
        }
    }
    
    public static void sendMsgToAllExcludeOne(final Battle bat, final StringBuilder battleMsg, final int excludePlayerId) {
        final String headInfo = " battleId " + bat.getBattleId() + " \u53d1\u9001\u7ed9\u5176\u4ed6\u4eba  battleMsg:";
        final String saveReport = Configuration.getProperty("gcld.battle.report.save");
        if (saveReport.equals("1")) {
            getLog(battleMsg.toString(), headInfo);
        }
        final String quickMode = Configuration.getProperty("gcld.battle.quick");
        if (!quickMode.equals("1")) {
            for (final Integer playerId : bat.inSceneSet) {
                if (playerId == excludePlayerId) {
                    continue;
                }
                if (!NewBattleManager.getInstance().isWatchBattle(playerId, bat.getBattleId())) {
                    continue;
                }
                sendMsgToOne(playerId, battleMsg);
            }
        }
    }
    
    public static void sendMsgToSingle(final Battle bat, final StringBuilder msgToCurrentPlayer, final int playerId, final String SingleHeadInfo, final String backN) {
        final String saveReport = Configuration.getProperty("gcld.battle.report.save");
        if (saveReport.equals("1")) {
            if (backN == null) {
                getLog(msgToCurrentPlayer.toString(), SingleHeadInfo);
            }
            else {
                getLog(String.valueOf(backN) + msgToCurrentPlayer.toString(), SingleHeadInfo);
            }
        }
        final String quickMode = Configuration.getProperty("gcld.battle.quick");
        if (!quickMode.equals("1")) {
            sendMsgToOne(playerId, msgToCurrentPlayer);
        }
    }
    
    public static void sendMsgToOne(final int playerId, final StringBuilder battleMsg) {
        final Session session = Players.getSession(Integer.valueOf(playerId));
        if (session != null) {
            final byte[] bytes = JsonBuilder.getJson(JsonBuilder.getJson(State.PUSH, PushCommand.PUSH_BATTLE_DOBATTLE.getModule(), (Object)battleMsg));
            session.write(WrapperUtil.wrapper(PushCommand.PUSH_BATTLE_DOBATTLE.getCommand(), 0, bytes));
        }
    }
    
    public static void getReportType10(final StringBuilder battleMsg, final List<BattleArmy> battleList, final String battleSide, final BaseInfo baseInfo) {
        int i = 0;
        if (baseInfo.foreWin) {
            i = 1;
        }
        if (battleList.size() > i) {
            battleMsg.append(10).append("|").append(battleSide).append(";");
            while (i < battleList.size()) {
                final BattleArmy battleArmy = battleList.get(i);
                int specialType = (battleArmy.getCampArmy().getTeamGenreal() == null) ? 0 : ((battleArmy.getCampArmy().getTeamEffect() > 0.0) ? 2 : 1);
                if (battleArmy.getTD_defense_e() > 0.0) {
                    specialType = 3;
                }
                battleMsg.append(battleArmy.getPosition()).append("|").append(battleArmy.getCampArmy().getPlayerId()).append("|").append(battleArmy.getCampArmy().getTroopSerial()).append("|").append(battleArmy.getCampArmy().getTroopType()).append("|").append(battleArmy.getCampArmy().getTroopName()).append("|").append(battleArmy.getCampArmy().getTroopDropType()).append("|").append(battleArmy.getStrategy()).append("|").append(specialType).append("|");
                final int troopHpMax = battleArmy.getCampArmy().getTroopHp() / 3;
                for (int j = 0; j < 3; ++j) {
                    battleMsg.append(battleArmy.getTroopHp()[j]).append("*").append(troopHpMax).append(",");
                }
                battleMsg.replace(battleMsg.length() - 1, battleMsg.length(), ";");
                ++i;
            }
            battleMsg.append("#");
        }
    }
    
    public Terrain getTerrain(final int playerId, final int defId, final IDataGetter dataGetter) {
        final Armies armies = (Armies)dataGetter.getArmiesCache().get((Object)defId);
        return new Terrain(armies.getTerrain(), armies.getTerrainEffectType(), armies.getTerrain());
    }
    
    public void inBattleInfo(final int playerId, final boolean inBattle) {
        NewBattleManager.inPveBattle(playerId, inBattle);
    }
    
    public void addInSceneSet(final Battle bat, final int playerId) {
        if (!bat.auto && playerId > 0) {
            bat.inSceneSet.add(playerId);
            NewBattleManager.getInstance().setPlayerWatchBattle(playerId, bat);
        }
    }
    
    public int getGeneralState() {
        return 2;
    }
    
    public void sendTaskMessage(final int playerId, final int defId, final IDataGetter dataGetter) {
    }
    
    public int getBattleSide(final IDataGetter dataGetter, final Player player, final int defId) {
        return 1;
    }
    
    public int getBattleSide(final IDataGetter dataGetter, final Player player, final Battle battle) {
        return 1;
    }
    
    public Battle getPlayerBattleInfo(final int playerId, final int battleType, final int generalId) {
        Battle bat = null;
        if (generalId > 0) {
            bat = NewBattleManager.getInstance().getBattleByGId(playerId, generalId);
            if (bat != null) {
                return bat;
            }
        }
        bat = NewBattleManager.getInstance().getBattleByBatType(playerId, battleType);
        if (bat != null) {
            return bat;
        }
        if (battleType == 1) {
            final Map<Integer, Battle> battles = NewBattleManager.getInstance().getBattleByPid(playerId);
            for (final Map.Entry<Integer, Battle> entry : battles.entrySet()) {
                if (entry.getKey() == 1) {
                    return entry.getValue();
                }
                if (entry.getKey() == 2) {
                    return entry.getValue();
                }
                if (entry.getKey() == 11) {
                    return entry.getValue();
                }
                if (entry.getKey() == 12) {
                    return entry.getValue();
                }
            }
        }
        return bat;
    }
    
    public void sendBattleInfo(final IDataGetter dataGetter, final Battle bat, final BattleAttacker battleAttacker) {
    }
    
    public LinkedList<CampArmy> addAttNpc(final IDataGetter dataGetter, final int num, final BaseInfo attbaseInfo, final LinkedList<CampArmy> camps, final AtomicInteger campNum, final Battle bat) {
        return null;
    }
    
    public LinkedList<CampArmy> addDefNpc(final IDataGetter dataGetter, final Battle bat, final RoundInfo roundInfo) {
        return null;
    }
    
    public void systemSinglePK(final IDataGetter dataGetter, final Battle bat, final RoundInfo roundInfo) {
    }
    
    public void dealBuilderBattleTask(final IDataGetter dataGetter, final int playerId, final int cityId) {
    }
    
    public void getGeneralForce(final IDataGetter dataGetter, final Battle bat, final StringBuilder battleMsg, final AtomicInteger ticket) {
        final int sn = ticket.incrementAndGet();
        battleMsg.append(sn).append("#");
        CampArmy campArmy = bat.attList.get(0).getCampArmy();
        battleMsg.append(18).append("|").append("att").append("|").append(campArmy.armyHpOrg - campArmy.armyHpLoss).append("|").append(campArmy.armyHpOrg).append("#");
        campArmy = bat.defList.get(0).getCampArmy();
        battleMsg.append(18).append("|").append("def").append("|").append(campArmy.armyHpOrg - campArmy.armyHpLoss).append("|").append(campArmy.armyHpOrg).append("#");
    }
    
    public void initJoinPlayer(final List<PlayerGeneralMilitary> pgmList, final IDataGetter dataGetter, final Player player, final Battle bat, final BaseInfo baseInfo, final LinkedList<CampArmy> campMap, final LinkedList<CampArmy> campChange, final int battleSide, final TeamInfo teamInfo) {
        CampArmy campArmy = null;
        int num = 0;
        for (int i = 0; i < pgmList.size(); ++i) {
            final PlayerGeneralMilitary pgm = pgmList.get(i);
            campArmy = this.copyArmyFromPlayerTable(player, pgm, dataGetter, this.getGeneralState(), bat, battleSide);
            if (campArmy != null) {
                num += campArmy.getArmyHpOrg();
                campMap.add(campArmy);
                BattleSceneLog.getInstance().info("#batId:" + bat.getBattleId() + "_" + bat.getStartTime() + "#add:join#side:" + ((battleSide == 1) ? "att" : "def") + "#playerId:" + campArmy.getPlayerId() + ":" + campArmy.isPhantom + "#general:" + campArmy.getGeneralId() + "#" + ((battleSide == 1) ? "att" : "def") + "Size:" + campMap.size());
                campChange.add(campArmy);
            }
        }
        baseInfo.setNum(baseInfo.getNum() + num);
        baseInfo.setAllNum(baseInfo.getAllNum() + num);
    }
    
    public boolean conSumeFood(final int playerId, final int defId, final IDataGetter dataGetter) {
        return true;
    }
    
    public static boolean containsCityId(final String strs, final int defId) {
        if (StringUtils.isBlank(strs)) {
            return false;
        }
        final String[] ids = strs.split(",");
        int cityId = 0;
        String[] array;
        for (int length = (array = ids).length, i = 0; i < length; ++i) {
            final String str = array[i];
            cityId = Integer.valueOf(str);
            if (cityId == defId) {
                return true;
            }
        }
        return false;
    }
    
    public static void getCurCampInfo(final IDataGetter dataGetter, final Battle bat, final RoundInfo roundInfo) {
        roundInfo.attBattleArmy = bat.attList.get(0);
        roundInfo.defBattleArmy = bat.defList.get(0);
        roundInfo.attCampArmy = roundInfo.attBattleArmy.getCampArmy();
        roundInfo.defCampArmy = roundInfo.defBattleArmy.getCampArmy();
        if (bat.attList.size() > 1) {
            roundInfo.nextAttBattleArmy = bat.attList.get(1);
        }
        if (bat.defList.size() > 1) {
            roundInfo.nextDefBattleArmy = bat.defList.get(1);
        }
        if (roundInfo.attCampArmy.playerId > 0 && ActivityService.setActivityLv.contains(roundInfo.attCampArmy.playerLv)) {
            roundInfo.attCampArmy.activityAddExp = dataGetter.getActivityService().getAddBatValue(roundInfo.attCampArmy.playerId, roundInfo.attCampArmy.playerLv);
        }
        if (roundInfo.defCampArmy.playerId > 0 && ActivityService.setActivityLv.contains(roundInfo.defCampArmy.playerLv)) {
            roundInfo.defCampArmy.activityAddExp = dataGetter.getActivityService().getAddBatValue(roundInfo.defCampArmy.playerId, roundInfo.defCampArmy.playerLv);
        }
        getReportType16(dataGetter, bat, roundInfo.battleMsg, roundInfo.attCampArmy, "att", false, false);
        getReportType16(dataGetter, bat, roundInfo.battleMsg, roundInfo.defCampArmy, "def", false, false);
    }
    
    public static void fight(final RoundInfo roundInfo) {
        saveFightDetailBefore(roundInfo);
        roundInfo.reports = Fight.fight(roundInfo.troopData[0], roundInfo.troopData[1]);
        saveFightDetailAfter(roundInfo);
        int numMax = 0;
        int num = 0;
        String[] reports;
        for (int length = (reports = roundInfo.reports).length, i = 0; i < length; ++i) {
            final String s = reports[i];
            num = s.split(";").length - 1;
            if (num > numMax) {
                numMax = num;
            }
        }
        roundInfo.nextMinExeTime += 600 + numMax * 800;
        roundInfo.timePredicationBuffer.append("fight:").append(600 + numMax * 800).append("|");
    }
    
    public static void saveTacticDetailBefore(final RoundInfo roundInfo, final TroopData[] attacker, final TroopData[][] defenders) {
        final String debug = Configuration.getProperty("gcld.battle.caculate.debug");
        if (debug.equals("1")) {
            roundInfo.caculateDebugBuffer.append("Before Tactic Attacker.").append("\n\t").append("hp:").append(attacker[0].hp).append(" | ").append("max_hp:").append(attacker[0].max_hp).append(" | ").append("att:").append(attacker[0].att).append(" | ").append("def:").append(attacker[0].def).append(" | ").append("TACTIC_ATT:").append(attacker[0].TACTIC_ATT).append(" | ").append("TACTIC_DEF:").append(attacker[0].TACTIC_DEF).append(" | ").append("ATT_B:").append(attacker[0].ATT_B).append(" | ").append("DEF_B:").append(attacker[0].DEF_B).append(" | ").append("Str:").append(attacker[0].Str).append(" | ").append("Lea:").append(attacker[0].Lea).append(" | ").append("tech_yingyong_damage_e:").append(attacker[0].tech_yingyong_damage_e).append(" | ").append("tactic_id:").append(attacker[0].tactic_id).append(" | ").append("tactic_damage_e:").append(attacker[0].tactic_damage_e).append(" | ").append("tactic_range:").append(attacker[0].tactic_range).append(" | ").append("world_weaken_frontLine_buff:").append(attacker[0].world_weaken_frontLine_buff).append(" | ").append("isCityAttacker:").append(attacker[0].isCityAttacker).append(" | ").append("isBS:").append(attacker[0].isBS).append(" | ").append("BS_My:").append(attacker[0].BS_My).append(" | ").append("BS_Your:").append(attacker[0].BS_Your).append(" | ").append("isYX:").append(attacker[0].isYX).append(" | ").append("YX_cur_Blood:").append(attacker[0].YX_cur_Blood).append(" | ").append("YX_max_Blood:").append(attacker[0].YX_max_Blood).append(" | ").append("\n\t");
            roundInfo.caculateDebugBuffer.append("Before Tactic Defenders.").append("\n\t");
            for (int i = 0; i < defenders.length; ++i) {
                roundInfo.caculateDebugBuffer.append("hp:").append(defenders[i][0].hp).append(" | ").append("max_hp:").append(defenders[i][0].max_hp).append(" | ").append("att:").append(defenders[i][0].att).append(" | ").append("def:").append(defenders[i][0].def).append(" | ").append("TACTIC_ATT:").append(defenders[i][0].TACTIC_DEF).append(" | ").append("TACTIC_DEF:").append(defenders[i][0].TACTIC_DEF).append(" | ").append("ATT_B:").append(defenders[i][0].ATT_B).append(" | ").append("DEF_B:").append(defenders[i][0].DEF_B).append(" | ").append("Str:").append(defenders[i][0].Str).append(" | ").append("Lea:").append(defenders[i][0].Lea).append(" | ").append("isTD:").append(defenders[i][0].isTD).append(" | ").append("TD_defense_e:").append(defenders[i][0].TD_defense_e).append(" | ").append("\n\t");
            }
        }
    }
    
    public static void saveTacticDetailAfter(final RoundInfo roundInfo, final TacticInfo tacticInfo, final TroopData[] attacker, final TroopData[][] defenders) {
        final String debug = Configuration.getProperty("gcld.battle.caculate.debug");
        if (debug.equals("1")) {
            final String[] Result = tacticInfo.tacticStr.split(";");
            roundInfo.caculateDebugBuffer.append("Tactic Reports.").append("\n\t");
            String[] array;
            for (int length = (array = Result).length, i = 0; i < length; ++i) {
                final String result = array[i];
                roundInfo.caculateDebugBuffer.append(result).append("\n\t");
            }
        }
    }
    
    public static void saveFightDetailBefore(final RoundInfo roundInfo) {
        final String debug = Configuration.getProperty("gcld.battle.caculate.debug");
        if (debug.equals("1")) {
            roundInfo.caculateDebugBuffer.append("Before Fight Att.").append("\n\t");
            roundInfo.caculateDebugBuffer.append("hp:").append(roundInfo.troopData[0][0].hp).append(" | ").append("max_hp:").append(roundInfo.troopData[0][0].max_hp).append(" | ").append("att:").append(roundInfo.troopData[0][0].att).append(" | ").append("def:").append(roundInfo.troopData[0][0].def).append(" | ").append("ATT_B:").append(roundInfo.troopData[0][0].ATT_B).append(" | ").append("DEF_B:").append(roundInfo.troopData[0][0].DEF_B).append(" | ").append("Str:").append(roundInfo.troopData[0][0].Str).append(" | ").append("Lea:").append(roundInfo.troopData[0][0].Lea).append(" | ").append("terrain_effect:").append(roundInfo.troopData[0][0].terrain_effect).append(" | ").append("all_damage_e:").append(roundInfo.troopData[0][0].all_damage_e).append(" | ").append("world_weaken_besiege:").append(roundInfo.troopData[0][0].world_weaken_besiege).append(" | ").append("world_weaken_frontLine_buff:").append(roundInfo.troopData[0][0].world_weaken_frontLine_buff).append("\n\t");
            roundInfo.caculateDebugBuffer.append("Before Fight Def.").append("\n\t");
            roundInfo.caculateDebugBuffer.append("hp:").append(roundInfo.troopData[1][0].hp).append(" | ").append("max_hp:").append(roundInfo.troopData[1][0].max_hp).append(" | ").append("att:").append(roundInfo.troopData[1][0].att).append(" | ").append("def:").append(roundInfo.troopData[1][0].def).append(" | ").append("ATT_B:").append(roundInfo.troopData[1][0].ATT_B).append(" | ").append("DEF_B:").append(roundInfo.troopData[1][0].DEF_B).append(" | ").append("Str:").append(roundInfo.troopData[1][0].Str).append(" | ").append("Lea:").append(roundInfo.troopData[1][0].Lea).append(" | ").append("terrain_effect:").append(roundInfo.troopData[1][0].terrain_effect).append(" | ").append("all_damage_e:").append(roundInfo.troopData[1][0].all_damage_e).append(" | ").append("world_weaken_besiege:").append(roundInfo.troopData[1][0].world_weaken_besiege).append(" | ").append("world_weaken_frontLine_buff:").append(roundInfo.troopData[1][0].world_weaken_frontLine_buff).append("\n\t");
        }
    }
    
    public static void saveFightDetailAfter(final RoundInfo roundInfo) {
        final String debug = Configuration.getProperty("gcld.battle.caculate.debug");
        if (debug.equals("1")) {
            roundInfo.caculateDebugBuffer.append("Fight Reports.").append("\n\t");
            String[] reports;
            for (int length = (reports = roundInfo.reports).length, i = 0; i < length; ++i) {
                final String report = reports[i];
                roundInfo.caculateDebugBuffer.append(report).append("\n\t");
            }
            roundInfo.caculateDebugBuffer.append("After Fight Att.").append("\n\t");
            roundInfo.caculateDebugBuffer.append("hp:").append(roundInfo.troopData[0][0].hp).append(" | ").append("max_hp:").append(roundInfo.troopData[0][0].max_hp).append("\n\t");
            roundInfo.caculateDebugBuffer.append("After Fight Def.").append("\n\t");
            roundInfo.caculateDebugBuffer.append("hp:").append(roundInfo.troopData[1][0].hp).append(" | ").append("max_hp:").append(roundInfo.troopData[1][0].max_hp).append("\n\t");
        }
    }
    
    protected static boolean exeTactic(final IDataGetter dataGetter, final int batSide, final Tactic tactic, final TacticInfo tacticInfo, final CampArmy campA, final CampArmy campB, final Battle bat, final List<BattleArmy> killedList, final RoundInfo roundInfo, final List<BattleArmy> baListB, final boolean isRebound) {
        roundInfo.nextMinExeTime += tactic.getPlayertime() + 3000;
        roundInfo.timePredicationBuffer.append("tactic:").append(tactic.getPlayertime() + 3000).append("|");
        tacticInfo.tacticId = tactic.getId();
        tacticInfo.tacticDisplayId = tactic.getDisplayId();
        tacticInfo.tacticNameId = tactic.getPic();
        tacticInfo.tacticBasicPic = tactic.getBasicPic();
        tacticInfo.specialType = tactic.getSpecialType();
        boolean firstRowKilled = false;
        List<BattleArmy> listA = bat.attList;
        List<BattleArmy> listB = bat.defList;
        LinkedList<CampArmy> campsB = bat.defCamp;
        if (batSide == 0) {
            listA = bat.defList;
            listB = bat.attList;
            campsB = bat.attCamp;
        }
        final TroopData[] attacker = new TroopData[3];
        if (isRebound) {
            copyTacticBattleInfo(listB.get(0), campB, attacker, tactic, dataGetter, listB.get(0).getCampArmy().isBarPhantom || listB.get(0).getCampArmy().isBarEA);
        }
        else {
            copyTacticBattleInfo(listA.get(0), campA, attacker, tactic, dataGetter, listB.get(0).getCampArmy().isBarPhantom || listB.get(0).getCampArmy().isBarEA);
        }
        double world_frontLine_buff_att_def_e = 0.0;
        int yingYong = 0;
        boolean isCityAttacker = false;
        boolean isBS = false;
        if ((batSide == 1 && bat.world_frontLine_buff_att_def_e_side == 1) || (batSide == 0 && bat.world_frontLine_buff_att_def_e_side == 2)) {
            world_frontLine_buff_att_def_e = bat.world_frontLine_buff_att_def_e;
        }
        if (campA.playerId > 0) {
            yingYong = dataGetter.getTechEffectCache().getTechEffect(campA.playerId, 10);
        }
        if ((bat.battleType == 3 || bat.battleType == 13) && batSide == 1 && bat.getTerrainVal() == 4 && !tactic.getSpecialEffect().equalsIgnoreCase("siege_gun")) {
            isCityAttacker = true;
        }
        int attackerBSNum = 0;
        int defenderBSNum = 0;
        if ((bat.getBattleType() == 3 || bat.getBattleType() == 13) && tactic.getSpecialEffect().equalsIgnoreCase("bs")) {
            isBS = true;
            Battle battleBSTemp = bat;
            if (bat.getBattleType() == 13) {
                battleBSTemp = NewBattleManager.getInstance().getBattleByDefId(3, bat.defBaseInfo.id);
                if (battleBSTemp == null) {
                    battleBSTemp = bat;
                }
            }
            attackerBSNum = battleBSTemp.attCamp.size();
            defenderBSNum = battleBSTemp.defCamp.size();
            if (batSide == 0) {
                attackerBSNum = battleBSTemp.defCamp.size();
                defenderBSNum = battleBSTemp.attCamp.size();
            }
        }
        boolean isYX = false;
        int YX_cur_Blood = 0;
        int YX_max_Blood = 0;
        if (campA.getSpecialGeneral().generalType == 9) {
            isYX = true;
            YX_cur_Blood = campA.getArmyHpOrg() - campA.getArmyHpLoss();
            YX_max_Blood = campA.getArmyHpOrg();
        }
        for (int i = 0; i < attacker.length; ++i) {
            attacker[i].world_weaken_frontLine_buff = world_frontLine_buff_att_def_e;
            attacker[i].tech_yingyong_damage_e = yingYong;
            attacker[i].isCityAttacker = isCityAttacker;
            attacker[i].isBS = isBS;
            attacker[i].BS_My = attackerBSNum;
            attacker[i].BS_Your = defenderBSNum;
            attacker[i].isYX = isYX;
            attacker[i].YX_cur_Blood = YX_cur_Blood;
            attacker[i].YX_max_Blood = YX_max_Blood;
        }
        final TroopData[][] defenders = new TroopData[listB.size()][3];
        copyTacticBattleInfo(campsB, listB, defenders, dataGetter, listA.get(0).getCampArmy().isBarPhantom || listA.get(0).getCampArmy().isBarEA);
        if (campB.playerId > 0) {
            final int jianren = dataGetter.getTechEffectCache().getTechEffect(campB.playerId, 13);
            for (int j = 0; j < listB.size(); ++j) {
                for (int k = 0; k < 3; ++k) {
                    defenders[j][k].tech_jianren_damage_e = jianren;
                }
            }
        }
        saveTacticDetailBefore(roundInfo, attacker, defenders);
        tacticInfo.tacticStr = ast.gcldcore.fight.Tactic.tacticAttack(attacker, defenders);
        if (tacticInfo.tacticStr.equalsIgnoreCase("SP")) {
            tacticInfo.beStop = true;
            roundInfo.timePredicationBuffer.append("tactic is stoped:").append(0).append("|");
            BattleSceneLog.getInstance().appendLogMsg("tactic is stoped.");
            return firstRowKilled;
        }
        General generalA = null;
        if (isRebound) {
            generalA = (General)dataGetter.getGeneralCache().get((Object)campB.getGeneralId());
        }
        else {
            generalA = (General)dataGetter.getGeneralCache().get((Object)campA.getGeneralId());
        }
        if (generalA != null && generalA.getGeneralSpecialInfo() != null && generalA.getGeneralSpecialInfo().generalType == 10) {
            int wuShenFuTiLimit = generalA.getGeneralSpecialInfo().rowNum;
            if (listA.size() < wuShenFuTiLimit) {
                wuShenFuTiLimit = listA.size();
            }
            for (int l = 0; l < wuShenFuTiLimit; ++l) {
                listA.get(l).setIsTd(true);
                listA.get(l).setTD_defense_e(generalA.getGeneralSpecialInfo().param2);
            }
        }
        if (tacticInfo.specialType > 0) {
            if (tacticInfo.specialType == 2) {
                tacticInfo.specialEffect = tactic.getSpecialEffect();
                tacticInfo.reward = tactic.getReward();
                final RewardInfo ri = tacticInfo.reward.getReward(dataGetter, campA.getPlayerId(), campA.getGeneralId());
                if (ri != null) {
                    tacticInfo.tacticDrop = new BattleDrop();
                    tacticInfo.tacticDrop.type = ri.getType();
                    tacticInfo.tacticDrop.num = ri.getAddValue();
                }
                roundInfo.nextMinExeTime += 25000;
                roundInfo.timePredicationBuffer.append("rob food tactic:").append(25000).append("|");
                return firstRowKilled;
            }
            if (tacticInfo.specialType == 1) {
                final int range = tactic.getRange();
                final StringBuilder sb = new StringBuilder();
                for (int m = 0; m < range && baListB.size() > m; ++m) {
                    baListB.get(m).setSpecial(1);
                    sb.append(baListB.get(m).getPosition()).append(",");
                }
                tacticInfo.columnStr = sb.toString();
            }
        }
        saveTacticDetailAfter(roundInfo, tacticInfo, attacker, defenders);
        if (!StringUtils.isEmpty(tacticInfo.tacticStr)) {
            roundInfo.nextMinExeTime += 1500;
            roundInfo.timePredicationBuffer.append("tactic reduce:").append(1500).append("|");
            final String[] tacs = tacticInfo.tacticStr.split(";");
            final Map<CampArmy, Integer> reduceMap = new HashMap<CampArmy, Integer>();
            final int campBId = campB.getId();
            for (int i2 = 0; i2 < tacs.length; ++i2) {
                if (tacs[i2].contains("jsBJ")) {
                    tacticInfo.zfBJ = true;
                }
                if (tacs[i2].contains("jsJB")) {
                    tacticInfo.zfJB = true;
                }
                final String[] tempArr = tacs[i2].split("\\|");
                final String tempStr = tempArr[tempArr.length - 1];
                final String[] tacStr = tempStr.split(",");
                final BattleArmy batArmy = listB.get(i2);
                final CampArmy defendCa = batArmy.getCampArmy();
                int reduce = 0;
                int perReduce = Integer.valueOf(tacStr[0]);
                if (perReduce >= batArmy.getTroopHp()[0]) {
                    perReduce = batArmy.getTroopHp()[0];
                    killedList.add(batArmy);
                    if (i2 == 0) {
                        firstRowKilled = true;
                        if (batSide == 0) {
                            roundInfo.win = 2;
                        }
                        else {
                            roundInfo.win = 1;
                        }
                    }
                }
                for (int j2 = 0; j2 < batArmy.getTroopHp().length; ++j2) {
                    final int[] troopHp = batArmy.getTroopHp();
                    final int n = j2;
                    troopHp[n] -= perReduce;
                    reduce += perReduce;
                }
                if (defendCa.getId() == campBId) {
                    tacticInfo.firstCReduce += reduce;
                }
                tacticInfo.allCReduce += reduce;
                if (defendCa.isBarPhantom) {
                    tacticInfo.allBarbarainReduce += reduce;
                }
                final CampArmy campArmy = defendCa;
                campArmy.armyHpLoss += reduce;
                if (defendCa.armyHpLoss >= defendCa.armyHpOrg) {
                    ++campA.killGeneral;
                    roundInfo.needPushReport13 = true;
                    roundInfo.nextMinExeTime += 0;
                    roundInfo.timePredicationBuffer.append("tactic cheers:").append(0).append("|");
                    defendCa.armyHp = -1;
                    if (listB.get(0).getCampArmy().getId() == defendCa.getId()) {
                        if (batSide == 0) {
                            roundInfo.killAttG = true;
                        }
                        else {
                            roundInfo.killDefG = true;
                        }
                        if (bat.attList.size() > 0 && bat.defList.size() > 0) {
                            bat.setBattleRoundBuff(dataGetter, bat.attList.get(0).getCampArmy(), bat.defList.get(0).getCampArmy());
                        }
                    }
                    final PlayerInfo pi = bat.inBattlePlayers.get(campA.getPlayerId());
                    if (pi != null && campA.killGeneral > pi.maxKillG) {
                        pi.maxKillG = campA.killGeneral;
                    }
                }
                final PlayerInfo pi = bat.inBattlePlayers.get(defendCa.getPlayerId());
                if (pi != null) {
                    final PlayerInfo playerInfo = pi;
                    playerInfo.lostTotal += reduce;
                }
                if (reduceMap.containsKey(defendCa)) {
                    reduceMap.put(defendCa, reduceMap.get(defendCa) + reduce);
                }
                else {
                    reduceMap.put(defendCa, reduce);
                }
            }
            tacticInfo.reduceMap = reduceMap;
            campA.armyHpKill += tacticInfo.allCReduce;
            campA.barbarainHpKill += tacticInfo.allBarbarainReduce;
            final PlayerInfo piA = bat.inBattlePlayers.get(campA.getPlayerId());
            if (piA != null) {
                final PlayerInfo playerInfo2 = piA;
                playerInfo2.killTotal += tacticInfo.allCReduce;
            }
        }
        return firstRowKilled;
    }
    
    protected static void copyTacticBattleInfo(final LinkedList<CampArmy> camps, final List<BattleArmy> queues, final TroopData[][] troopData, final IDataGetter dataGetter, final boolean vsSideIsBarPhantom) {
        for (int i = 0; i < queues.size(); ++i) {
            final BattleArmy battleArmy = queues.get(i);
            final CampArmy campArmy = battleArmy.getCampArmy();
            final TroopData[] troop = new TroopData[3];
            copyTacticBattleInfo(battleArmy, campArmy, troop, null, dataGetter, vsSideIsBarPhantom);
            troopData[i] = troop;
        }
    }
    
    protected static void copyTacticBattleInfo(final BattleArmy battleArmy, final CampArmy campArmy, final TroopData[] troopData, final Tactic tactic, final IDataGetter dataGetter, final boolean vsSideIsBarPhantom) {
        int att = campArmy.getAttEffect();
        int def = campArmy.getDefEffect();
        if (campArmy.getPlayerId() > 0) {
            final Troop troop = (Troop)dataGetter.getTroopCache().get((Object)campArmy.getTroopId());
            att = dataGetter.getBattleDataCache().getAtt(campArmy.getPlayerId(), campArmy.getGeneralId(), troop, campArmy.getGeneralLv());
            def = dataGetter.getBattleDataCache().getDef(campArmy.getPlayerId(), campArmy.getGeneralId(), troop, campArmy.getGeneralLv());
        }
        for (int i = 0; i < battleArmy.getTroopHp().length; ++i) {
            final TroopData tempData = new TroopData();
            tempData.troop_id = battleArmy.getCampArmy().getId();
            tempData.hp = battleArmy.getTroopHp()[i];
            tempData.max_hp = campArmy.getTroopHp() / 3;
            tempData.att = att;
            tempData.def = def;
            tempData.base_damage = campArmy.getBdEffect();
            tempData.Lea = campArmy.getLeader();
            tempData.Str = campArmy.getStrength();
            tempData.general_quality = campArmy.getQuality();
            tempData.TACTIC_ATT = campArmy.getTACTIC_ATT();
            tempData.TACTIC_DEF = campArmy.getTACTIC_DEF();
            if (tactic != null) {
                tempData.tactic_id = tactic.getId();
                tempData.tactic_damage_e = tactic.getDamageE();
                tempData.tactic_range = tactic.getRange();
            }
            if (campArmy.specialGeneral.generalType == 2) {
                tempData.isFS = true;
                tempData.world_fs_d = (int)campArmy.specialGeneral.param;
                if (tempData.world_fs_d <= 0) {
                    tempData.isFS = false;
                }
            }
            else {
                tempData.isFS = false;
                tempData.world_fs_d = 0;
            }
            if (campArmy.specialGeneral.generalType == 3 && vsSideIsBarPhantom) {
                tempData.isMz = true;
                tempData.world_mz_e = campArmy.specialGeneral.param;
            }
            else {
                tempData.isMz = false;
                tempData.world_mz_e = 0.0;
            }
            tempData.isTD = battleArmy.getIsTd();
            tempData.TD_defense_e = battleArmy.getTD_defense_e();
            final Map<Integer, Double> playerGemAttribute = dataGetter.getBattleDataCache().getGemAttribute(campArmy.getPlayerId());
            if (playerGemAttribute == null || playerGemAttribute.isEmpty()) {
                troopData[i] = tempData;
            }
            else {
                tempData.JS_SKILL_zfbj = playerGemAttribute.get(3);
                tempData.JS_SKILL_zfjb = playerGemAttribute.get(4);
                troopData[i] = tempData;
            }
        }
    }
    
    public void dealFight(final IDataGetter dataGetter, final Battle bat, final RoundInfo roundInfo) {
        TroopData attTroop = null;
        TroopData defTroop = null;
        for (int i = 0; i < 3; ++i) {
            attTroop = roundInfo.troopData[0][i];
            defTroop = roundInfo.troopData[1][i];
            if (attTroop != null) {
                roundInfo.attLost += attTroop.lost_hp;
                roundInfo.attRemain += attTroop.hp;
                roundInfo.attBattleArmy.getTroopHp()[i] -= attTroop.lost_hp;
            }
            if (defTroop != null) {
                roundInfo.defLost += defTroop.lost_hp;
                roundInfo.defRemain += defTroop.hp;
                roundInfo.defBattleArmy.getTroopHp()[i] -= defTroop.lost_hp;
            }
        }
        BattleSceneLog.getInstance().appendLogMsg("fight lost_hp").appendBattleId(bat.getBattleId()).append("attTroop.lost_hp", roundInfo.troopData[0][0].lost_hp).append("defTroop.lost_hp", roundInfo.troopData[1][0].lost_hp).appendMethodName("dealFight").flush();
        roundInfo.killDefG = isKillGeneral(roundInfo.defRemain, bat.defList, roundInfo.nextDefBattleArmy);
        if (roundInfo.killDefG) {
            if (bat.attList.size() > 0 && bat.defList.size() > 0) {
                bat.setBattleRoundBuff(dataGetter, bat.attList.get(0).getCampArmy(), bat.defList.get(0).getCampArmy());
            }
            final CampArmy attCampArmy = roundInfo.attCampArmy;
            ++attCampArmy.killGeneral;
            roundInfo.needPushReport13 = true;
            roundInfo.nextMinExeTime += 0;
            roundInfo.timePredicationBuffer.append("fight att cheers:").append(0).append("|");
            roundInfo.defCampArmy.armyHp = -1;
            bat.defCamp.remove(roundInfo.defCampArmy);
            BattleSceneLog.getInstance().info("#batId:" + bat.getBattleId() + "_" + bat.getStartTime() + "#quit:fight#side:def" + "#playerId:" + roundInfo.defCampArmy.getPlayerId() + ":" + roundInfo.defCampArmy.isPhantom + "#general:" + roundInfo.defCampArmy.getGeneralId() + "#defSize:" + bat.defCamp.size());
            this.quitNewBattle(roundInfo.defCampArmy, 0, bat);
            if (roundInfo.attCampArmy.getPlayerId() > 0 && !roundInfo.attCampArmy.isPhantom) {
                final PlayerInfo pi = bat.inBattlePlayers.get(roundInfo.attCampArmy.getPlayerId());
                if (pi != null && roundInfo.attCampArmy.killGeneral > pi.maxKillG) {
                    pi.maxKillG = roundInfo.attCampArmy.killGeneral;
                }
            }
        }
        roundInfo.killAttG = isKillGeneral(roundInfo.attRemain, bat.attList, roundInfo.nextAttBattleArmy);
        if (roundInfo.killAttG) {
            if (bat.attList.size() > 0 && bat.defList.size() > 0) {
                bat.setBattleRoundBuff(dataGetter, bat.attList.get(0).getCampArmy(), bat.defList.get(0).getCampArmy());
            }
            final CampArmy defCampArmy = roundInfo.defCampArmy;
            ++defCampArmy.killGeneral;
            roundInfo.needPushReport13 = true;
            roundInfo.nextMinExeTime += 0;
            roundInfo.timePredicationBuffer.append("fight def cheers:").append(0).append("|");
            roundInfo.attCampArmy.armyHp = -1;
            bat.attCamp.remove(roundInfo.attCampArmy);
            BattleSceneLog.getInstance().info("#batId:" + bat.getBattleId() + "_" + bat.getStartTime() + "#quit:fight#side:att" + "#playerId:" + roundInfo.attCampArmy.getPlayerId() + ":" + roundInfo.attCampArmy.isPhantom + "#general:" + roundInfo.attCampArmy.getGeneralId() + "#attSize:" + bat.attCamp.size());
            this.quitNewBattle(roundInfo.attCampArmy, 1, bat);
            if (roundInfo.defCampArmy.getPlayerId() > 0 && !roundInfo.defCampArmy.isPhantom) {
                final PlayerInfo pi = bat.inBattlePlayers.get(roundInfo.defCampArmy.getPlayerId());
                if (pi != null && roundInfo.defCampArmy.killGeneral > pi.maxKillG) {
                    pi.maxKillG = roundInfo.defCampArmy.killGeneral;
                }
            }
        }
        bat.attBaseInfo.foreWin = true;
        if (roundInfo.attRemain <= 0) {
            bat.attBaseInfo.foreWin = false;
            roundInfo.win = 2;
            roundInfo.attKilledList.add(roundInfo.attBattleArmy);
        }
        bat.defBaseInfo.foreWin = true;
        if (roundInfo.defRemain <= 0) {
            bat.defBaseInfo.foreWin = false;
            roundInfo.win = 1;
            roundInfo.defKilledList.add(roundInfo.defBattleArmy);
        }
        if (roundInfo.attRemain <= 0 && roundInfo.defRemain <= 0) {
            roundInfo.win = 3;
        }
        final CampArmy attCampArmy2 = roundInfo.attCampArmy;
        attCampArmy2.armyHpLoss += roundInfo.attLost;
        final CampArmy attCampArmy3 = roundInfo.attCampArmy;
        attCampArmy3.armyHpKill += roundInfo.defLost;
        if (roundInfo.defCampArmy.isBarPhantom) {
            final CampArmy attCampArmy4 = roundInfo.attCampArmy;
            attCampArmy4.barbarainHpKill += roundInfo.defLost;
        }
        final PlayerInfo piAtt = bat.inBattlePlayers.get(roundInfo.attBattleArmy.getCampArmy().getPlayerId());
        if (piAtt != null) {
            final PlayerInfo playerInfo = piAtt;
            playerInfo.lostTotal += roundInfo.attLost;
            final PlayerInfo playerInfo2 = piAtt;
            playerInfo2.killTotal += roundInfo.defLost;
        }
        final CampArmy defCampArmy2 = roundInfo.defCampArmy;
        defCampArmy2.armyHpLoss += roundInfo.defLost;
        final CampArmy defCampArmy3 = roundInfo.defCampArmy;
        defCampArmy3.armyHpKill += roundInfo.attLost;
        if (roundInfo.attCampArmy.isBarPhantom) {
            final CampArmy defCampArmy4 = roundInfo.defCampArmy;
            defCampArmy4.barbarainHpKill += roundInfo.attLost;
        }
        final PlayerInfo piDef = bat.inBattlePlayers.get(roundInfo.defBattleArmy.getCampArmy().getPlayerId());
        if (piDef != null) {
            final PlayerInfo playerInfo3 = piDef;
            playerInfo3.lostTotal += roundInfo.defLost;
            final PlayerInfo playerInfo4 = piDef;
            playerInfo4.killTotal += roundInfo.attLost;
        }
        bat.getAttBaseInfo().setNum(bat.getAttBaseInfo().getNum() - roundInfo.attLost);
        bat.getDefBaseInfo().setNum(bat.getDefBaseInfo().getNum() - roundInfo.defLost);
        getReportType3(roundInfo.battleMsg, bat, roundInfo);
        getReportType31(roundInfo.battleMsg, roundInfo);
        roundInfo.battleMsg.append(30).append("|");
        if (roundInfo.win != 1) {
            roundInfo.battleMsg.append(roundInfo.attBattleArmy.getPosition()).append("|");
        }
        else {
            roundInfo.battleMsg.append(-1).append("|");
        }
        if (roundInfo.win != 2) {
            roundInfo.battleMsg.append(roundInfo.defBattleArmy.getPosition()).append("#");
        }
        else {
            roundInfo.battleMsg.append(-1).append("#");
        }
        getReportType20(bat, roundInfo.battleMsg, roundInfo);
        if (roundInfo.killAttG && roundInfo.nextAttBattleArmy != null) {
            if (bat.attList.size() > 1 && bat.defList.size() > 1) {
                bat.setBattleRoundBuff(dataGetter, bat.attList.get(1).getCampArmy(), bat.defList.get(1).getCampArmy());
            }
            getReportType16(dataGetter, bat, roundInfo.battleMsg, roundInfo.nextAttBattleArmy.getCampArmy(), "att", true, false);
        }
        else {
            getReportType16(dataGetter, bat, roundInfo.battleMsg, roundInfo.attCampArmy, "att", false, false);
        }
        if (roundInfo.killDefG && roundInfo.nextDefBattleArmy != null) {
            if (bat.attList.size() > 1 && bat.defList.size() > 1) {
                bat.setBattleRoundBuff(dataGetter, bat.attList.get(1).getCampArmy(), bat.defList.get(1).getCampArmy());
            }
            getReportType16(dataGetter, bat, roundInfo.battleMsg, roundInfo.nextDefBattleArmy.getCampArmy(), "def", true, false);
        }
        else {
            getReportType16(dataGetter, bat, roundInfo.battleMsg, roundInfo.defCampArmy, "def", false, false);
        }
    }
    
    protected static boolean isKillGeneral(final int remainNum, final List<BattleArmy> listQ, final BattleArmy nextBattleArmy) {
        if (remainNum <= 0) {
            if (nextBattleArmy == null) {
                return true;
            }
            if (nextBattleArmy.getCampArmy().getId() != listQ.get(0).getCampArmy().getId()) {
                return true;
            }
        }
        return false;
    }
    
    protected static void getReportType26(final StringBuilder battleMsg, final RoundInfo roundInfo) {
        battleMsg.append(26).append("|").append(roundInfo.nextMaxExeTime).append("#");
    }
    
    protected static void getReportType18(final StringBuilder battleMsg, final CampArmy campArmy, final String battleSide) {
        battleMsg.append(18).append("|").append(battleSide).append("|").append(campArmy.armyHpOrg - campArmy.armyHpLoss).append("|").append(campArmy.armyHpOrg).append("#");
    }
    
    protected static void getReportType29(final StringBuilder battleMsg, final RoundInfo roundInfo, final boolean attExeTactic, final boolean defExeTactic) {
        if (attExeTactic && defExeTactic) {
            battleMsg.append(29).append("|").append(1).append(";");
            battleMsg.append(100).append("|").append(roundInfo.attTacticInfo.tacticNameId).append("|").append(100).append("|").append(roundInfo.defTacticInfo.tacticNameId).append("#");
        }
        else if (attExeTactic && !defExeTactic) {
            battleMsg.append(29).append("|").append(1).append(";");
            battleMsg.append(100).append("|").append(roundInfo.attTacticInfo.tacticNameId).append("|").append(roundInfo.defBattleArmy.getStrategy()).append("|").append(FightStrategiesCache.getStrategyPic(roundInfo.defBattleArmy.getStrategy())).append("#");
        }
        else if (!attExeTactic && defExeTactic) {
            battleMsg.append(29).append("|").append(2).append(";");
            battleMsg.append(roundInfo.attBattleArmy.getStrategy()).append("|").append(FightStrategiesCache.getStrategyPic(roundInfo.attBattleArmy.getStrategy())).append("|").append(100).append("|").append(roundInfo.defTacticInfo.tacticNameId).append("#");
        }
        else {
            battleMsg.append(29).append("|").append(roundInfo.tacticStrategyResult).append(";");
            battleMsg.append(roundInfo.attBattleArmy.getStrategy()).append("|").append(FightStrategiesCache.getStrategyPic(roundInfo.attBattleArmy.getStrategy())).append("|").append(roundInfo.defBattleArmy.getStrategy()).append("|").append(FightStrategiesCache.getStrategyPic(roundInfo.defBattleArmy.getStrategy())).append("#");
        }
    }
    
    protected static void getReportType14(final IDataGetter dataGetter, final Battle bat, final RoundInfo roundInfo) {
        roundInfo.battleMsg.append(14).append("|").append(roundInfo.tacticStrategyResult).append(";").append(roundInfo.attBattleArmy.getPosition()).append(",").append(roundInfo.attBattleArmy.getCampArmy().isPhantom ? 0 : roundInfo.attBattleArmy.getCampArmy().getPlayerId()).append("|").append(roundInfo.defBattleArmy.getPosition()).append(",").append(roundInfo.defBattleArmy.getCampArmy().isPhantom ? 0 : roundInfo.defBattleArmy.getCampArmy().getPlayerId()).append(";");
        if (roundInfo.attTacticInfo != null) {
            int attTacticReduceType = 0;
            int zfBj = 0;
            final Tactic tactic = (Tactic)dataGetter.getTacticCache().get((Object)roundInfo.attTacticInfo.tacticId);
            if ((bat.battleType == 3 || bat.battleType == 13) && bat.terrainVal == 4 && !tactic.getSpecialEffect().equalsIgnoreCase("siege_gun")) {
                attTacticReduceType = 1;
            }
            if (roundInfo.attTacticInfo.zfJB) {
                attTacticReduceType = 3;
            }
            if (roundInfo.attTacticInfo.attacked_guanyu) {
                attTacticReduceType = 2;
            }
            if (roundInfo.attTacticInfo.zfBJ) {
                zfBj = 1;
            }
            int stopType = 0;
            if (roundInfo.attRebound && roundInfo.attTacticInfo.beStop) {
                stopType = 3;
            }
            else if (roundInfo.attRebound) {
                stopType = 2;
            }
            else if (roundInfo.attTacticInfo.beStop) {
                stopType = 1;
            }
            roundInfo.battleMsg.append(1).append(",").append(1).append(",").append(roundInfo.attTacticInfo.tacticNameId).append(",").append(stopType).append(",").append((stopType == 1 || stopType == 3) ? roundInfo.defCampArmy.getcDifyType() : roundInfo.attTacticInfo.tacticDisplayId).append(",").append(roundInfo.attTacticInfo.tacticBasicPic).append(",").append(roundInfo.attBattleArmy.getStrategy()).append(",").append("null").append(",").append(attTacticReduceType).append(",").append(roundInfo.attBattleArmy.getCampArmy().getGeneralPic()).append(",").append(roundInfo.defBattleArmy.getCampArmy().getGeneralPic()).append(",").append(zfBj);
        }
        else {
            final FightStrategies attFT = (FightStrategies)dataGetter.getFightStrategiesCache().get((Object)roundInfo.attBattleArmy.getStrategy());
            roundInfo.battleMsg.append(2).append(",").append(0).append(",").append(roundInfo.attBattleArmy.getStrategy()).append(",").append(0).append(",").append(0).append(",").append(0).append(",").append(roundInfo.attBattleArmy.getStrategy()).append(",").append(attFT.getName()).append(",").append(0).append(",").append(0).append(",").append(0);
        }
        roundInfo.battleMsg.append("|");
        if (roundInfo.defTacticInfo != null) {
            int stopType2 = 0;
            if (roundInfo.defRebound && roundInfo.defTacticInfo.beStop) {
                stopType2 = 3;
            }
            else if (roundInfo.defRebound) {
                stopType2 = 2;
            }
            else if (roundInfo.defTacticInfo.beStop) {
                stopType2 = 1;
            }
            int defTacticReduceType = 0;
            int zfBj2 = 0;
            if (roundInfo.defTacticInfo.zfJB) {
                defTacticReduceType = 3;
            }
            if (roundInfo.defTacticInfo.attacked_guanyu) {
                defTacticReduceType = 2;
            }
            if (roundInfo.defTacticInfo.zfBJ) {
                zfBj2 = 1;
            }
            roundInfo.battleMsg.append(1).append(",").append(roundInfo.defTacticInfo.executed ? 1 : 0).append(",").append(roundInfo.defTacticInfo.tacticNameId).append(",").append(stopType2).append(",").append((stopType2 == 1 || stopType2 == 3) ? roundInfo.attCampArmy.getcDifyType() : roundInfo.defTacticInfo.tacticDisplayId).append(",").append(roundInfo.defTacticInfo.tacticBasicPic).append(",").append(roundInfo.defBattleArmy.getStrategy()).append(",").append("null").append(",").append(defTacticReduceType).append(",").append(roundInfo.attBattleArmy.getCampArmy().getGeneralPic()).append(",").append(roundInfo.defBattleArmy.getCampArmy().getGeneralPic()).append(",").append(zfBj2);
        }
        else {
            final FightStrategies defFT = (FightStrategies)dataGetter.getFightStrategiesCache().get((Object)roundInfo.defBattleArmy.getStrategy());
            roundInfo.battleMsg.append(2).append(",").append(0).append(",").append(roundInfo.defBattleArmy.getStrategy()).append(",").append(0).append(",").append(0).append(",").append(0).append(",").append(roundInfo.defBattleArmy.getStrategy()).append(",").append(defFT.getName()).append(",").append(0).append(",").append(0).append(",").append(0);
        }
        roundInfo.battleMsg.append(";");
        if (roundInfo.defTacticInfo != null) {
            if (StringUtils.isEmpty(roundInfo.defTacticInfo.tacticStr) || roundInfo.defTacticInfo.tacticStr.equalsIgnoreCase("SP")) {
                roundInfo.battleMsg.append("null");
            }
            else {
                final String[] strs = roundInfo.defTacticInfo.tacticStr.split(";");
                for (int i = 0; i < strs.length; ++i) {
                    roundInfo.battleMsg.append(bat.getAttList().get(i).getPosition()).append(",");
                    final String[] tempArr = strs[i].split("\\|");
                    final String tempStr = tempArr[tempArr.length - 1];
                    final String[] loss = tempStr.split(",");
                    for (int j = 0; j < loss.length; ++j) {
                        roundInfo.battleMsg.append(loss[j]).append(",").append(bat.getAttList().get(i).getTroopHp()[j]).append(",");
                    }
                    roundInfo.battleMsg.replace(roundInfo.battleMsg.length() - 1, roundInfo.battleMsg.length(), "*");
                }
            }
        }
        else if (roundInfo.attStrategyLost > 0) {
            final int lost = roundInfo.attStrategyLost / 3;
            roundInfo.battleMsg.append(roundInfo.attBattleArmy.getPosition()).append(",");
            for (int k = 0; k < roundInfo.attBattleArmy.getTroopHp().length; ++k) {
                roundInfo.battleMsg.append(lost).append(",").append(roundInfo.attBattleArmy.getTroopHp()[k]).append(",");
            }
        }
        else {
            roundInfo.battleMsg.append("null");
        }
        roundInfo.battleMsg.append("|");
        if (roundInfo.attTacticInfo != null) {
            if (StringUtils.isEmpty(roundInfo.attTacticInfo.tacticStr) || roundInfo.attTacticInfo.tacticStr.equalsIgnoreCase("SP")) {
                roundInfo.battleMsg.append("null").append(";");
            }
            else {
                final String[] strs = roundInfo.attTacticInfo.tacticStr.split(";");
                for (int i = 0; i < strs.length; ++i) {
                    roundInfo.battleMsg.append(bat.getDefList().get(i).getPosition()).append(",");
                    final String[] tempArr = strs[i].split("\\|");
                    final String tempStr = tempArr[tempArr.length - 1];
                    final String[] loss = tempStr.split(",");
                    for (int j = 0; j < loss.length; ++j) {
                        roundInfo.battleMsg.append(loss[j]).append(",").append(bat.getDefList().get(i).getTroopHp()[j]).append(",");
                    }
                    roundInfo.battleMsg.replace(roundInfo.battleMsg.length() - 1, roundInfo.battleMsg.length(), "*");
                }
                roundInfo.battleMsg.replace(roundInfo.battleMsg.length() - 1, roundInfo.battleMsg.length(), ";");
            }
        }
        else if (roundInfo.defStrategyLost > 0) {
            final int lost = roundInfo.defStrategyLost / 3;
            roundInfo.battleMsg.append(roundInfo.defBattleArmy.getPosition()).append(",");
            for (int i = 0; i < roundInfo.defBattleArmy.getTroopHp().length; ++i) {
                roundInfo.battleMsg.append(lost).append(",").append(roundInfo.defBattleArmy.getTroopHp()[0]).append(",");
            }
            roundInfo.battleMsg.replace(roundInfo.battleMsg.length() - 1, roundInfo.battleMsg.length(), ";");
        }
        else {
            roundInfo.battleMsg.append("null").append(";");
        }
        if (roundInfo.attTacticInfo != null) {
            if (roundInfo.attTacticInfo.beStop) {
                roundInfo.battleMsg.append("null");
            }
            else {
                final PlayerInfo pi = bat.inBattlePlayers.get(roundInfo.attCampArmy.playerId);
                if (pi != null && pi.rbTop == 0 && roundInfo.attTacticInfo.tacticDrop != null) {
                    roundInfo.battleMsg.append(roundInfo.attTacticInfo.tacticDrop.type).append("*").append(roundInfo.attTacticInfo.tacticDrop.num);
                }
                else {
                    roundInfo.battleMsg.append("null");
                }
            }
        }
        else {
            roundInfo.battleMsg.append("null");
        }
        roundInfo.battleMsg.append("|");
        if (roundInfo.defTacticInfo != null) {
            if (roundInfo.defTacticInfo.beStop) {
                roundInfo.battleMsg.append("null").append(";");
            }
            else {
                final PlayerInfo pi = bat.inBattlePlayers.get(roundInfo.defCampArmy.playerId);
                if (pi != null && pi.rbTop == 0 && roundInfo.defTacticInfo.tacticDrop != null) {
                    roundInfo.battleMsg.append(roundInfo.defTacticInfo.tacticDrop.type).append("*").append(roundInfo.defTacticInfo.tacticDrop.num).append(";");
                }
                else {
                    roundInfo.battleMsg.append("null").append(";");
                }
            }
        }
        else {
            roundInfo.battleMsg.append("null").append(";");
        }
        if (roundInfo.defTacticInfo != null && !roundInfo.defTacticInfo.beStop && roundInfo.defTacticInfo.columnStr != null && roundInfo.defTacticInfo.columnStr != "") {
            roundInfo.battleMsg.append(roundInfo.defTacticInfo.columnStr);
        }
        else {
            roundInfo.battleMsg.append("null");
        }
        roundInfo.battleMsg.append("|");
        if (roundInfo.attTacticInfo != null && !roundInfo.attTacticInfo.beStop && roundInfo.attTacticInfo.columnStr != null && roundInfo.attTacticInfo.columnStr != "") {
            roundInfo.battleMsg.append(roundInfo.attTacticInfo.columnStr).append("#");
        }
        else {
            roundInfo.battleMsg.append("null").append("#");
        }
    }
    
    public static void getReportType27(final StringBuilder battleMsg, final RoundInfo roundInfo, final Battle bat, final IDataGetter dataGetter, final long countDown) {
        if (roundInfo == null) {
            return;
        }
        CampArmy attCa = null;
        BattleArmy attBa = null;
        if (bat.getAttList().size() > 0) {
            attBa = bat.getAttList().get(0);
            attCa = bat.getAttList().get(0).getCampArmy();
        }
        CampArmy defCa = null;
        BattleArmy defBa = null;
        if (bat.getDefList().size() > 0) {
            defBa = bat.getDefList().get(0);
            defCa = bat.getDefList().get(0).getCampArmy();
        }
        boolean attNextChoose = false;
        if (roundInfo.win != 1 && attCa != null) {
            final int playerId = attCa.playerId;
            final PlayerInfo piDef = bat.inBattlePlayers.get(playerId);
            if (playerId > 0 && !attCa.isPhantom && piDef != null && piDef.autoStrategy != 1) {
                final FightStragtegyCoe fsc = dataGetter.getFightStragtegyCoeCache().getAttWin(roundInfo.defBattleArmy.getStrategy(), attCa.strategies);
                int canChooseTac = 0;
                if (attBa.getSpecial() > 0) {
                    canChooseTac = 1;
                }
                else if (defCa != null && defCa.getSpecialGeneral().generalType == 8) {
                    canChooseTac = 3;
                }
                battleMsg.append(27).append("|").append("att").append("|").append(playerId).append("|").append(attBa.getPosition()).append("|").append(attCa.playerName).append("|").append(countDown).append("|").append(1).append(";");
                battleMsg.append(fsc.getAttStrategy()).append("|").append(attCa.strategies[0]).append("|").append(((FightStrategies)dataGetter.getFightStrategiesCache().get((Object)attCa.strategies[0])).getName()).append("|").append(attCa.strategies[1]).append("|").append(((FightStrategies)dataGetter.getFightStrategiesCache().get((Object)attCa.strategies[1])).getName()).append("|").append(attCa.strategies[2]).append("|").append(((FightStrategies)dataGetter.getFightStrategiesCache().get((Object)attCa.strategies[2])).getName()).append(";");
                battleMsg.append(attBa.getPosition()).append("|").append(attBa.getCampArmy().getTacticVal()).append("|").append(attBa.getCampArmy().getGeneralPic()).append("|").append(canChooseTac).append("|").append(attBa.getSpecial()).append("#");
                attNextChoose = true;
            }
        }
        if (!attNextChoose && bat.getAttList().size() > 0) {
            battleMsg.append(27).append("|").append("att").append("|").append(attCa.playerId).append("|").append(attBa.getPosition()).append("|").append(attCa.playerName).append("|").append(countDown).append("|").append(0).append(";");
            battleMsg.append("null").append(";");
            battleMsg.append("null").append("#");
        }
        boolean defNextChoose = false;
        if (roundInfo.win != 2 && defCa != null) {
            final int playerId2 = defCa.playerId;
            final PlayerInfo piAtt = bat.inBattlePlayers.get(playerId2);
            if (playerId2 > 0 && !defCa.isPhantom() && piAtt != null && piAtt.autoStrategy != 1) {
                final FightStragtegyCoe fsc2 = dataGetter.getFightStragtegyCoeCache().getAttWin(roundInfo.attBattleArmy.getStrategy(), defCa.strategies);
                int canChooseTac2 = 0;
                if (defBa.getSpecial() > 0) {
                    canChooseTac2 = 1;
                }
                else if (attCa != null && attCa.getSpecialGeneral().generalType == 8) {
                    canChooseTac2 = 3;
                }
                else if (bat.surround > 0) {
                    canChooseTac2 = 2;
                }
                battleMsg.append(27).append("|").append("def").append("|").append(playerId2).append("|").append(defBa.getPosition()).append("|").append(defCa.playerName).append("|").append(countDown).append("|").append(1).append(";");
                battleMsg.append(fsc2.getDefStrategy()).append("|").append(defCa.strategies[0]).append("|").append(((FightStrategies)dataGetter.getFightStrategiesCache().get((Object)defCa.strategies[0])).getName()).append("|").append(defCa.strategies[1]).append("|").append(((FightStrategies)dataGetter.getFightStrategiesCache().get((Object)defCa.strategies[1])).getName()).append("|").append(defCa.strategies[2]).append("|").append(((FightStrategies)dataGetter.getFightStrategiesCache().get((Object)defCa.strategies[2])).getName()).append(";");
                battleMsg.append(defBa.getPosition()).append("|").append(defBa.getCampArmy().getTacticVal()).append("|").append(defBa.getCampArmy().getGeneralPic()).append("|").append(canChooseTac2).append("|").append(defBa.getSpecial()).append("#");
                defNextChoose = true;
            }
        }
        if (!defNextChoose && bat.getDefList().size() > 0) {
            battleMsg.append(27).append("|").append("def").append("|").append(defCa.playerId).append("|").append(defBa.getPosition()).append("|").append(defCa.playerName).append("|").append(countDown).append("|").append(0).append(";");
            battleMsg.append("null").append(";");
            battleMsg.append("null").append("#");
        }
    }
    
    protected static void getReportType30(final StringBuilder battleMsg, final RoundInfo roundInfo) {
        battleMsg.append(30).append("|");
        if (roundInfo.attKilledList.size() > 0) {
            for (final BattleArmy ba : roundInfo.attKilledList) {
                battleMsg.append(ba.getPosition()).append(",");
            }
            battleMsg.replace(battleMsg.length() - 1, battleMsg.length(), "|");
        }
        else {
            battleMsg.append(-1).append("|");
        }
        if (roundInfo.defKilledList.size() > 0) {
            for (final BattleArmy ba : roundInfo.defKilledList) {
                battleMsg.append(ba.getPosition()).append(",");
            }
            battleMsg.replace(battleMsg.length() - 1, battleMsg.length(), "#");
        }
        else {
            battleMsg.append(-1).append("#");
        }
    }
    
    protected static void getReportType31(final StringBuilder battleMsg, final RoundInfo roundInfo) {
        battleMsg.append(31).append("|").append(roundInfo.win).append("#");
    }
    
    public static void getReportType100(final IDataGetter dataGetter, final Battle battle, final StringBuilder battleMsg) {
        try {
            if (battle.battleType != 3 && battle.battleType != 13) {
                return;
            }
            final StringBuilder attPart = new StringBuilder();
            final StringBuilder defPart = new StringBuilder();
            CampArmy attCa = null;
            if (battle.getAttList().size() > 0) {
                attCa = battle.getAttList().get(0).getCampArmy();
            }
            CampArmy defCa = null;
            if (battle.getDefList().size() > 0) {
                defCa = battle.getDefList().get(0).getCampArmy();
            }
            if (attCa == null || defCa == null) {
                return;
            }
            final int attForceId = attCa.forceId;
            final int defForceId = defCa.forceId;
            final int defId = battle.defBaseInfo.id;
            long tryExipreTime = dataGetter.getNationService().getTryTaskCd(attForceId);
            if (tryExipreTime > System.currentTimeMillis()) {
                attPart.append(1).append(",").append(attForceId).append(",").append(tryExipreTime - System.currentTimeMillis()).append(",").append(0).append(";");
            }
            tryExipreTime = dataGetter.getNationService().getTryTaskCd(defForceId);
            if (tryExipreTime > System.currentTimeMillis()) {
                defPart.append(1).append(",").append(defForceId).append(",").append(tryExipreTime - System.currentTimeMillis()).append(",").append(0).append(";");
            }
            ConcurrentHashMap<Integer, ManWangLingObj> map = ManWangLingManager.getInstance().manWangLingObjMap.get(attForceId);
            if (map != null) {
                final ManWangLingObj manWangLingObj = map.get(2);
                if (manWangLingObj != null && battle.battleType == 3 && defId == manWangLingObj.targetCityId && manWangLingObj.expireTime > System.currentTimeMillis()) {
                    attPart.append(2).append(",").append(attForceId).append(",").append(manWangLingObj.expireTime - System.currentTimeMillis()).append(",").append(0).append(";");
                }
            }
            map = ManWangLingManager.getInstance().manWangLingObjMap.get(defForceId);
            if (map != null) {
                final ManWangLingObj manWangLingObj = map.get(2);
                if (manWangLingObj != null && battle.battleType == 3 && defId == manWangLingObj.targetCityId && manWangLingObj.expireTime > System.currentTimeMillis()) {
                    defPart.append(2).append(",").append(defForceId).append(",").append(manWangLingObj.expireTime - System.currentTimeMillis()).append(",").append(0).append(";");
                }
            }
            final int BarbarainInvadeRound = NewBattleManager.getInstance().BarbarainInvadeRound;
            final long BarbarainInvadeCountDown = NewBattleManager.getInstance().BarbarainInvadeCountDown;
            if (BarbarainInvadeRound > 0 && BarbarainInvadeCountDown > 0L) {
                attPart.append(3).append(",").append(attForceId).append(",").append(BarbarainInvadeCountDown).append(",").append(BarbarainInvadeRound).append(";");
                defPart.append(3).append(",").append(defForceId).append(",").append(BarbarainInvadeCountDown).append(",").append(BarbarainInvadeRound).append(";");
            }
            Tuple<Integer, Long> tuple = dataGetter.getRankService().getNextInvadeInfo(attForceId);
            if (tuple != null && tuple.right > 0L) {
                attPart.append(4).append(",").append(attForceId).append(",").append(tuple.right).append(",").append(tuple.left).append(";");
            }
            tuple = dataGetter.getRankService().getNextInvadeInfo(defForceId);
            if (tuple != null && tuple.right > 0L) {
                defPart.append(4).append(",").append(defForceId).append(",").append(tuple.right).append(",").append(tuple.left).append(";");
            }
            final boolean isNTYellowTurbans = dataGetter.getBattleService().isNTYellowTurbansXiangYangDoing(battle.getDefBaseInfo().getId());
            if (isNTYellowTurbans) {
                int jitanNum = RankService.taskInfo.getTempleNum();
                if (jitanNum <= 0) {
                    jitanNum = 0;
                }
                attPart.append(6).append(",").append(attForceId).append(",").append(jitanNum).append(",").append(0).append(";");
                defPart.append(6).append(",").append(attForceId).append(",").append(jitanNum).append(",").append(0).append(";");
            }
            Long shaDiLingExpireTime = battle.getShaDiLingExpireTime(attForceId);
            if (shaDiLingExpireTime != null && shaDiLingExpireTime > System.currentTimeMillis()) {
                attPart.append(5).append(",").append(attForceId).append(",").append(shaDiLingExpireTime - System.currentTimeMillis()).append(",").append(0).append(";");
            }
            shaDiLingExpireTime = battle.getShaDiLingExpireTime(defForceId);
            if (shaDiLingExpireTime != null && shaDiLingExpireTime > System.currentTimeMillis()) {
                defPart.append(5).append(",").append(defForceId).append(",").append(shaDiLingExpireTime - System.currentTimeMillis()).append(",").append(0).append(";");
            }
            if (attPart.length() == 0 && defPart.length() == 0) {
                return;
            }
            battleMsg.append(100).append("|").append(attPart).append("|").append(defPart).append("#");
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("Builder.getReportType100 catch Exception", e);
        }
    }
    
    protected static void getReportType20(final Battle bat, final StringBuilder battleMsg, final RoundInfo roundInfo) {
        if (roundInfo.killDefG) {
            battleMsg.append(20).append("|").append("att").append(";").append(roundInfo.attCampArmy.id).append(";").append(roundInfo.attCampArmy.killGeneral).append(";").append(roundInfo.attCampArmy.playerId).append(";");
            boolean addNeeded = true;
            final int currentCA = roundInfo.attCampArmy.id;
            for (final BattleArmy ba : bat.attList) {
                if (ba.getCampArmy().getId() == currentCA) {
                    addNeeded = false;
                    battleMsg.append(ba.getPosition()).append(",");
                }
            }
            if (addNeeded) {
                battleMsg.append("null").append("#");
            }
            battleMsg.replace(battleMsg.length() - 1, battleMsg.length(), "#");
        }
        if (roundInfo.killAttG) {
            battleMsg.append(20).append("|").append("def").append(";").append(roundInfo.defCampArmy.id).append(";").append(roundInfo.defCampArmy.killGeneral).append(";").append(roundInfo.defCampArmy.playerId).append(";");
            boolean addNeeded = true;
            final int currentCA = roundInfo.defCampArmy.id;
            for (final BattleArmy ba : bat.defList) {
                if (ba.getCampArmy().getId() == currentCA) {
                    addNeeded = false;
                    battleMsg.append(ba.getPosition()).append(",");
                }
            }
            if (addNeeded) {
                battleMsg.append("null").append("#");
            }
            battleMsg.replace(battleMsg.length() - 1, battleMsg.length(), "#");
        }
    }
    
    protected static void getReportType19(final StringBuilder battleMsg, final RoundInfo roundInfo) {
        if (roundInfo.attCampArmy.playerId > 0) {
            battleMsg.append(19).append("|").append("att").append(";");
            battleMsg.append(roundInfo.attCampArmy.playerId).append("|").append(roundInfo.attRoundReward.gExp).append("|").append(roundInfo.attRoundReward.mUpLv).append("|").append(roundInfo.attRoundReward.gUpLv).append(";");
            if (roundInfo.attRoundReward.roundDropMap == null || roundInfo.attRoundReward.roundDropMap.size() == 0) {
                battleMsg.append(0).append("*").append(0).append("#");
            }
            else {
                final Map<Integer, Integer> attRoundDrop = new HashMap<Integer, Integer>();
                for (final BattleDrop battleDrop : roundInfo.attRoundReward.roundDropMap.values()) {
                    Integer key = battleDrop.type;
                    if (key > 1000) {
                        key -= 1000;
                    }
                    if (attRoundDrop.containsKey(key)) {
                        final int newNum = attRoundDrop.get(key) + battleDrop.num;
                        attRoundDrop.put(key, newNum);
                    }
                    else {
                        attRoundDrop.put(key, battleDrop.num);
                    }
                }
                for (final Integer key2 : attRoundDrop.keySet()) {
                    battleMsg.append(key2).append("*").append(attRoundDrop.get(key2)).append("|");
                }
                battleMsg.replace(battleMsg.length() - 1, battleMsg.length(), "#");
            }
        }
        if (roundInfo.defCampArmy.playerId > 0) {
            battleMsg.append(19).append("|").append("def").append(";");
            battleMsg.append(roundInfo.defCampArmy.playerId).append("|").append(roundInfo.defRoundReward.gExp).append("|").append(roundInfo.defRoundReward.mUpLv).append("|").append(roundInfo.defRoundReward.gUpLv).append(";");
            if (roundInfo.defRoundReward.roundDropMap == null || roundInfo.defRoundReward.roundDropMap.size() == 0) {
                battleMsg.append(0).append("*").append(0).append("#");
            }
            else {
                final Map<Integer, Integer> defRoundDrop = new HashMap<Integer, Integer>();
                for (final BattleDrop battleDrop : roundInfo.defRoundReward.roundDropMap.values()) {
                    Integer key = battleDrop.type;
                    if (key > 1000) {
                        key -= 1000;
                    }
                    if (defRoundDrop.containsKey(key)) {
                        final int newNum = defRoundDrop.get(key) + battleDrop.num;
                        defRoundDrop.put(key, newNum);
                    }
                    else {
                        defRoundDrop.put(key, battleDrop.num);
                    }
                }
                for (final Integer key2 : defRoundDrop.keySet()) {
                    battleMsg.append(key2).append("*").append(defRoundDrop.get(key2)).append("|");
                }
                battleMsg.replace(battleMsg.length() - 1, battleMsg.length(), "#");
            }
        }
    }
    
    protected static void getReportType8(final StringBuilder battleMsg, final RoundInfo roundInfo) {
        if (roundInfo.killDefG) {
            battleMsg.append(8).append("|").append("def").append(";").append(roundInfo.defCampArmy.getId()).append("#");
        }
        if (roundInfo.killAttG) {
            battleMsg.append(8).append("|").append("att").append(";").append(roundInfo.attCampArmy.getId()).append("#");
        }
    }
    
    protected static void getReportType3(final StringBuilder battleMsg, final Battle bat, final RoundInfo roundInfo) {
        final int len = roundInfo.reports.length;
        if (len > 0) {
            battleMsg.append(3).append("|").append(bat.battleNum.incrementAndGet()).append("|").append(roundInfo.win).append(";");
            battleMsg.append(roundInfo.attBattleArmy.getPosition()).append("|").append(roundInfo.defBattleArmy.getPosition()).append(";");
            for (int i = 0; i < len; ++i) {
                battleMsg.append(roundInfo.reports[i].replace(";", ","));
                if (i < len - 1) {
                    battleMsg.append("*");
                }
            }
            battleMsg.append(";");
            int[] troopHp;
            for (int length = (troopHp = roundInfo.attBattleArmy.getTroopHp()).length, j = 0; j < length; ++j) {
                final int left = troopHp[j];
                battleMsg.append(left).append(",");
            }
            battleMsg.replace(battleMsg.length() - 1, battleMsg.length(), "|");
            int[] troopHp2;
            for (int length2 = (troopHp2 = roundInfo.defBattleArmy.getTroopHp()).length, k = 0; k < length2; ++k) {
                final int left = troopHp2[k];
                battleMsg.append(left).append(",");
            }
            battleMsg.replace(battleMsg.length() - 1, battleMsg.length(), "#");
        }
    }
    
    protected static void getReportType16(final IDataGetter dataGetter, final Battle bat, final StringBuilder battleMsg, final CampArmy campArmy, final String battleSide, final boolean campArmyChanged, final boolean first) {
        battleMsg.append(16).append("|").append(battleSide).append("|").append(campArmy.generalLv).append("|").append(campArmy.armyHpOrg - campArmy.armyHpLoss).append("|").append(campArmy.armyHpOrg).append("|").append(campArmy.killGeneral).append("|").append(";");
        String playerName = campArmy.playerName;
        if (bat.getBattleType() == 2 || bat.getBattleType() == 4 || bat.getBattleType() == 3) {
            playerName = campArmy.playerName;
        }
        battleMsg.append(campArmy.getForceId()).append("|").append(playerName).append("|").append(campArmy.generalName).append("|").append(campArmy.generalPic).append("|").append(campArmy.quality).append("|").append(campArmy.rewardDoubleType).append("|");
        String teamTips = campArmy.getTeamGenreal();
        if (campArmy.getTeamGenreal() != null && campArmy.getTeamEffect() > 0.0) {
            teamTips = String.valueOf(teamTips) + MessageFormatter.format(LocalMessages.T_TEAM_CONDITION_10037, new Object[] { campArmy.getTeamEffect() * 100.0 });
        }
        battleMsg.append(teamTips).append("|").append((campArmy.getTeamEffect() > 0.0) ? 1 : 0).append(";");
        boolean thirdPartAdded = false;
        final List<Tuple<Integer, String>> reportBuffList = new LinkedList<Tuple<Integer, String>>();
        if (battleSide.equals("att")) {
            reportBuffList.addAll(bat.attBuffListInit);
            reportBuffList.addAll(bat.attBuffListRound);
        }
        else {
            reportBuffList.addAll(bat.defBuffListInit);
            reportBuffList.addAll(bat.defBuffListRound);
        }
        for (int i = reportBuffList.size() - 1; i >= 0; --i) {
            final Tuple<Integer, String> buff = reportBuffList.get(i);
            final Tactic tactic = (Tactic)dataGetter.getTacticCache().get((Object)campArmy.tacicId);
            if (tactic != null && tactic.getSpecialEffect() != null && tactic.getSpecialEffect().equalsIgnoreCase("siege_gun") && buff.left.equals(10)) {
                reportBuffList.remove(i);
            }
        }
        if (reportBuffList.size() > 0) {
            for (final Tuple<Integer, String> buff2 : reportBuffList) {
                battleMsg.append(buff2.left).append("*").append(buff2.right).append("|");
            }
            battleMsg.replace(battleMsg.length() - 1, battleMsg.length(), "#");
            thirdPartAdded = true;
        }
        if (!thirdPartAdded) {
            battleMsg.append("null").append("#");
        }
    }
    
    protected static void getReportType11(final StringBuilder battleMsg, final BaseInfo baseInfo, final String battleSide) {
        battleMsg.append(11).append("|").append(battleSide).append(";").append(baseInfo.num).append("|").append(baseInfo.allNum).append("#");
    }
    
    public void gainExReward(final boolean attWin, final BattleResult battleResult, final Battle bat, final IDataGetter dataGetter) {
        PlayerInfo pi = null;
        for (final Integer playerId : bat.inBattlePlayers.keySet()) {
            pi = bat.inBattlePlayers.get(playerId);
            if (pi.isAttSide) {
                this.getExReward(attWin, dataGetter, pi, bat);
            }
        }
    }
    
    public void endBattleMsg(final boolean attWin, final BattleResult battleResult, final Battle bat, final IDataGetter dataGetter) {
        final int sn = bat.ticket.incrementAndGet();
        PlayerInfo pi = null;
        for (final Integer playerId : bat.inBattlePlayers.keySet()) {
            pi = bat.inBattlePlayers.get(playerId);
            int result = 2;
            if (pi.isAttSide && attWin) {
                result = 1;
            }
            if (!pi.isAttSide && !attWin) {
                result = 1;
            }
            final StringBuilder difSb = new StringBuilder();
            difSb.append(sn).append("|").append(bat.getBattleId()).append("#");
            final int maxNum = getMaxHpByType(dataGetter, bat);
            int percent = -1;
            if (maxNum > 0) {
                final int doneNum = maxNum - bat.defBaseInfo.num;
                percent = doneNum * 100 / maxNum;
            }
            difSb.append(7).append("|").append(result).append("|").append(percent).append(";");
            difSb.append(pi.killTotal).append("|").append(pi.lostTotal).append("|").append(pi.maxKillG).append("|").append(this.getDefName(dataGetter, bat.getDefBaseInfo().getId())).append(";");
            if (battleResult.slaveMap != null) {
                final SlaveInfo slaveInfo = battleResult.slaveMap.get(playerId);
                if (slaveInfo != null && slaveInfo.getPlayerIdList() != null && slaveInfo.getPlayerIdList().size() > 0) {
                    if (slaveInfo.getType() == 1) {
                        difSb.append(8);
                    }
                    else if (slaveInfo.getType() == 2) {
                        difSb.append(9);
                    }
                    else if (slaveInfo.getType() == 3) {
                        difSb.append(10);
                    }
                    difSb.append("*");
                    for (final Integer pId : slaveInfo.getPlayerIdList()) {
                        difSb.append(dataGetter.getPlayerDao().read(pId).getPlayerName()).append(",");
                    }
                    difSb.replace(difSb.length() - 1, difSb.length(), "|");
                }
            }
            if (pi.dropMap.size() == 0) {
                difSb.append(0).append("*").append(0).append(";");
            }
            else {
                for (final Map.Entry<Integer, BattleDrop> entry : pi.dropMap.entrySet()) {
                    if (entry.getValue().type > 200 && entry.getValue().type < 1000) {
                        difSb.append(entry.getValue().type).append("*").append(entry.getValue().num).append("*").append(entry.getValue().reserve).append("|");
                    }
                    else {
                        difSb.append(entry.getValue().type).append("*").append(entry.getValue().num).append("|");
                    }
                }
                difSb.replace(difSb.length() - 1, difSb.length(), ";");
            }
            difSb.append(pi.rbType).append("|").append(pi.rbTotal).append("|").append((pi.rbTotal > 0) ? 0 : pi.rbTop).append("|").append(";");
            if (battleResult.mineInfo != null) {
                final OccupyMineInfo omi = battleResult.mineInfo;
                difSb.append(omi.type).append("|").append(omi.state).append("|").append(omi.num).append("|").append(omi.outPut).append("|").append(omi.stoneNum).append("|").append(omi.outPutStone).append(";");
            }
            else {
                difSb.append("null").append(";");
            }
            if (battleResult.dropGId > 0) {
                difSb.append(battleResult.dropGId).append("|").append(battleResult.dropGName).append("|").append(battleResult.dropGPic).append("|").append(battleResult.dropGQuality).append("|").append(battleResult.dropGget).append("|").append(battleResult.dropGType).append("|").append(battleResult.gTroopName).append("|").append(battleResult.gTroopQuality).append(";");
                final JsonDocument doc = new JsonDocument();
                doc.startObject();
                doc.createElement("gainGeneral", battleResult.dropGId);
                doc.createElement("dropGName", battleResult.dropGName);
                doc.createElement("dropGId", battleResult.dropGId);
                doc.createElement("dropGPic", battleResult.dropGPic);
                doc.createElement("dropGQuality", battleResult.dropGQuality);
                doc.createElement("dropGget", battleResult.dropGget);
                doc.createElement("dropGType", battleResult.dropGType);
                final General general = (General)dataGetter.getGeneralCache().get((Object)battleResult.dropGId);
                if (general.getType() == 1) {
                    doc.createElement("dropTroopQuality", general.getQuality());
                    doc.createElement("dropTroopName", general.getName());
                }
                else {
                    final Troop troop = dataGetter.getTroopCache().getTroopOfGeneral(general.getTroop());
                    doc.createElement("dropTroopQuality", troop.getQuality());
                    doc.createElement("dropTroopName", troop.getName());
                }
                doc.endObject();
                Players.push(playerId, PushCommand.PUSH_UPDATE, doc.toByte());
            }
            else {
                difSb.append("null").append(";");
            }
            if (battleResult.oMap.containsKey(playerId)) {
                difSb.append(battleResult.oMap.get(playerId)).append(";");
            }
            else {
                difSb.append("null").append(";");
            }
            if (pi.isAttSide && battleResult.cityName != null) {
                difSb.append(battleResult.cityName).append("|").append(battleResult.cType);
            }
            else {
                difSb.append("null");
            }
            difSb.append(";");
            final String oldBattleId = bat.oldBattleId;
            final Battle oldBattle = NewBattleManager.getInstance().getBattleByBatId(oldBattleId);
            if (result == 1 && oldBattle != null) {
                CampArmy winCa = null;
                if (pi.isAttSide) {
                    if (bat.attCamp.size() == 0) {
                        winCa = null;
                    }
                    else {
                        winCa = bat.attCamp.get(0);
                    }
                }
                else if (bat.defCamp.size() == 0) {
                    winCa = null;
                }
                else {
                    winCa = bat.defCamp.get(0);
                }
                if (winCa != null) {
                    difSb.append(oldBattle.battleType).append("|").append(oldBattle.defBaseInfo.id).append("|").append(winCa.generalId);
                }
                else {
                    difSb.append("null");
                }
            }
            else {
                difSb.append("null");
            }
            difSb.append("#");
            if (bat.inSceneSet.contains(playerId)) {
                sendMsgToOne(playerId, difSb);
            }
            final String saveReport = Configuration.getProperty("gcld.battle.report.save");
            if (saveReport.equals("1")) {
                BattleSceneLog.getInstance().debug(" battleId " + bat.getBattleId() + " \u4e2a\u4eba\u7ed3\u7b97\u4fe1\u606f:" + " playerId:" + playerId + " msg:" + difSb);
            }
            NewBattleManager.getInstance().clearPlayerWatchBattle(playerId, bat.getBattleId());
            this.inBattleInfo(playerId, false);
        }
        final Builder builder = BuilderFactory.getInstance().getBuilder(bat.battleType);
        final String string = "0|0|0|null;null;0|0|0|;null;null;null;null;null#";
        for (final Integer viewPlayerId : bat.inSceneSet) {
            if (bat.inBattlePlayers.get(viewPlayerId) != null) {
                continue;
            }
            final Player player = dataGetter.getPlayerDao().read(viewPlayerId);
            final int result2 = builder.getViewPlayer7ReportResult(attWin, bat, player);
            final StringBuilder battleMsg = new StringBuilder();
            battleMsg.append(sn).append("|").append(bat.getBattleId()).append("#");
            final int maxNum2 = getMaxHpByType(dataGetter, bat);
            int percent2 = -1;
            if (maxNum2 > 0) {
                final int doneNum2 = maxNum2 - bat.defBaseInfo.num;
                percent2 = doneNum2 * 100 / maxNum2;
            }
            battleMsg.append(7).append("|").append(result2).append("|").append(percent2).append(";");
            battleMsg.append(string);
            sendMsgToOne(viewPlayerId, battleMsg);
            final String saveReport2 = Configuration.getProperty("gcld.battle.report.save");
            if (saveReport2.equals("1")) {
                BattleSceneLog.getInstance().debug(" battleId " + bat.getBattleId() + " \u4e2a\u4eba\u7ed3\u7b97\u4fe1\u606f:" + " playerId:" + viewPlayerId + " msg:" + battleMsg);
            }
            NewBattleManager.getInstance().clearPlayerWatchBattle(viewPlayerId, bat.getBattleId());
        }
        NewBattleManager.getInstance().deleteBattle(bat.getBattleId());
    }
    
    public int getViewPlayer7ReportResult(final boolean attWin, final Battle bat, final Player player) {
        return 2;
    }
    
    public static int getMaxHpByType(final IDataGetter dataGetter, final Battle bat) {
        final int battleType = bat.getBattleType();
        final int defId = bat.defBaseInfo.id;
        final int attForceId = bat.attBaseInfo.forceId;
        switch (battleType) {
            case 11: {
                return OneVsRewardNpcBuilder.getMaxHp(dataGetter, defId);
            }
            case 12: {
                return OneVsExtraBuilder.getMaxHp(dataGetter, defId);
            }
            case 3: {
                return CityBuilder.getMaxHp(dataGetter, defId);
            }
            case 10: {
                final int areaId = dataGetter.getWorldCityCache().getArea(attForceId, defId);
                return CityNpcBuilder.getMaxHp(dataGetter, areaId);
            }
            default: {
                return Integer.MIN_VALUE;
            }
        }
    }
    
    protected int getFightRewardCoeId(final IDataGetter dataGetter, final Battle bat) {
        final int battleType = bat.getBattleType();
        if (battleType == 2) {
            final Armies armies = (Armies)dataGetter.getArmiesCache().get((Object)bat.getDefBaseInfo().getId());
            if (armies.getType() == 3) {
                return 333;
            }
        }
        else if (battleType >= 18 && battleType <= 20) {
            final int jubenId = bat.defBaseInfo.id;
            if (jubenId < 10000) {
                return 21;
            }
        }
        return bat.getBattleType();
    }
    
    protected static double getTroopDamageCoe(final FightRewardCoe frc, final double troopFoodA, final double troopFoodB) {
        double yita = troopFoodB / (1.0 + troopFoodA);
        if (yita < 0.5) {
            yita = 0.5;
        }
        else if (yita > 2.0) {
            yita = 2.0;
        }
        final double selta = 0.5 * (yita - 1.0);
        if (selta > 0.5 || selta < -0.25) {
            ErrorSceneLog.getInstance().appendErrorMsg("selta error. selta:" + selta + " aLost,bLost,aLv,bLv:" + troopFoodA + "," + troopFoodB).appendClassName("Builder").appendMethodName("getTroopDamageCoe").append("frc", frc.getId()).flush();
        }
        final double delta = 1.0 + frc.getDelta() * selta;
        if (delta > 1.5 || delta < 0.75) {
            ErrorSceneLog.getInstance().appendErrorMsg("delta error. delta:" + selta + " selta,frcId:" + selta + "," + frc.getId()).appendClassName("Builder").appendMethodName("getTroopDamageCoe").append("frc", frc.getId()).flush();
        }
        return delta;
    }
    
    protected static double getLevelDifferCoe(final FightRewardCoe frc, final int aLv, final int bLv) {
        double l = 0.0;
        if (aLv > bLv + 5) {
            l = -1.0;
        }
        else if (aLv > bLv) {
            l = -0.2 * (aLv - bLv);
        }
        else {
            l = 0.0;
        }
        return 1.0 + frc.getLvCoe() * l;
    }
    
    protected static double getRoundRewardBase(final IDataGetter dataGetter, final FightRewardCoe frc, final int troopIdA, final int TroopIdB, final int aLost, final int bLost, final int aLv, final int bLv) {
        final double troopFoodConsumeCoeA = ((TroopConscribe)dataGetter.getTroopConscribeCache().get((Object)troopIdA)).getFood();
        final double troopFoodConsumeCoeB = ((TroopConscribe)dataGetter.getTroopConscribeCache().get((Object)TroopIdB)).getFood();
        final double troopFoodA = troopFoodConsumeCoeA * aLost;
        final double troopFoodB = troopFoodConsumeCoeB * bLost;
        final double troopDamageCoe = getTroopDamageCoe(frc, troopFoodA, troopFoodB);
        final double levelDifferCoe = getLevelDifferCoe(frc, aLv, bLv);
        return troopDamageCoe * levelDifferCoe * troopFoodA;
    }
    
    public void roundUpdateDB(final IDataGetter dataGetter, final Battle bat, final RoundInfo roundInfo) {
        try {
            roundInfo.attRoundReward = new RoundReward();
            roundInfo.defRoundReward = new RoundReward();
            this.dealTroopDrop(dataGetter, bat, roundInfo);
            this.roundCaculateReward(dataGetter, bat, roundInfo);
            this.roundAddReward(dataGetter, bat, roundInfo, roundInfo.attRoundReward, roundInfo.defRoundReward);
            this.roundTacticUpdateDB(dataGetter, bat, roundInfo);
            this.roundReduceTroop(dataGetter, bat, roundInfo);
            this.dealKillTotalStaff(dataGetter, bat, roundInfo);
            final PlayerInfo piAtt = bat.inBattlePlayers.get(roundInfo.attCampArmy.getPlayerId());
            if (piAtt != null) {
                piAtt.addDropMap(roundInfo.attRoundReward.roundDropMap);
            }
            final PlayerInfo piDef = bat.inBattlePlayers.get(roundInfo.defCampArmy.getPlayerId());
            if (piDef != null) {
                piDef.addDropMap(roundInfo.defRoundReward.roundDropMap);
            }
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("Builder.roundUpdateDB catch Exception. battleId:" + bat.getBattleId(), e);
        }
    }
    
    public void dealKillTotalStaff(final IDataGetter dataGetter, final Battle bat, final RoundInfo roundInfo) {
    }
    
    public void dealKillrank(final boolean isBarbarainInvade, final int gKillTotal, final Battle bat, final IDataGetter dataGetter, final int playerId) {
    }
    
    public int getDefPlayerLevel(final IDataGetter dataGetter, final Battle bat, final RoundInfo roundInfo) {
        return roundInfo.defCampArmy.getPlayerLv();
    }
    
    public void roundCaculateReward(final IDataGetter dataGetter, final Battle bat, final RoundInfo roundInfo) {
        FightRewardCoe frc = (FightRewardCoe)dataGetter.getFightRewardCoeCache().get((Object)this.getFightRewardCoeId(dataGetter, bat));
        if (frc == null) {
            ErrorSceneLog.getInstance().info("FightRewardCoe is null. battle type:" + bat.getBattleType());
            frc = new FightRewardCoe();
        }
        this.roundCaculateAttReward(dataGetter, frc, bat, roundInfo);
        this.roundCaculateDefReward(dataGetter, frc, bat, roundInfo);
    }
    
    public void roundCaculateAttReward(final IDataGetter dataGetter, final FightRewardCoe frc, final Battle bat, final RoundInfo roundInfo) {
        final String debug = Configuration.getProperty("gcld.battle.caculate.debug");
        if (roundInfo.defTacticInfo != null && roundInfo.defTacticInfo.reduceMap != null && roundInfo.defTacticInfo.reduceMap.size() > 1) {
            final int counterLost = 0;
            final int counterLv = roundInfo.defCampArmy.playerLv;
            final int gCounterLv = roundInfo.defCampArmy.generalLv;
            final int counterTroopId = roundInfo.defCampArmy.troopId;
            if (debug.equals("1")) {
                roundInfo.caculateDebugBuffer.append("defTacticInfo!=null.").append(" | ").append("FightRewardCoe:" + frc.getId()).append(" | ").append("counterPlayer:" + roundInfo.defCampArmy.playerName).append(" | ").append("counterGeneral:" + roundInfo.defCampArmy.generalName).append(" | ").append("counterLost:" + counterLost).append(" | ").append("counterLv:" + counterLv).append(" | ").append("gCounterLv:" + gCounterLv).append(" | ").append("counterTroopId:" + counterTroopId).append("\n");
            }
            for (final CampArmy campArmy : roundInfo.defTacticInfo.reduceMap.keySet()) {
                if (campArmy != roundInfo.attCampArmy && campArmy.updateDB && campArmy.playerId > 0) {
                    final PlayerInfo pInfo = bat.inBattlePlayers.get(campArmy.playerId);
                    if (pInfo == null) {
                        ErrorSceneLog.getInstance().appendErrorMsg("PlayerInfo is null").appendPlayerName(campArmy.playerName).appendPlayerId(campArmy.playerId).appendGeneralName(campArmy.generalName).appendGeneralId(campArmy.generalId).append("isupdateDB", campArmy.updateDB).append("isPhantom", campArmy.isPhantom).append("pgmVId", campArmy.pgmVId).appendBattleId(bat.getBattleId()).appendMethodName("roundCaculateDefReward").flush();
                    }
                    else {
                        final RoundReward tempRD = new RoundReward();
                        final int myLost = roundInfo.defTacticInfo.reduceMap.get(campArmy);
                        final int myLv = campArmy.playerLv;
                        final int gMyLv = campArmy.generalLv;
                        final int myTroopId = campArmy.troopId;
                        final double mAttOmega = getRoundRewardBase(dataGetter, frc, myTroopId, counterTroopId, myLost, counterLost, myLv, counterLv);
                        final double gAttOmega = getRoundRewardBase(dataGetter, frc, myTroopId, counterTroopId, myLost, counterLost, gMyLv, gCounterLv);
                        BattleDrop battleDrop = new BattleDrop();
                        final int copper = (int)(mAttOmega * frc.getM());
                        battleDrop.type = 1;
                        battleDrop.num = copper;
                        tempRD.roundDropMap.put(1, battleDrop);
                        pInfo.addDrop(battleDrop);
                        battleDrop = new BattleDrop();
                        int mExp = (int)(mAttOmega * frc.getC() * campArmy.rewardDouble);
                        double att_world_frontLine_buff_exp_e = 1.0;
                        if (bat.world_frontLine_buff_att_def_e_side == 1) {
                            att_world_frontLine_buff_exp_e = bat.world_frontLine_buff_exp_e;
                        }
                        final double attLowLvSCoe = this.getLowLvSCoe(bat, myLv, counterLv);
                        double attTechAddGZJY = 0.0;
                        if (bat.battleType == 3 || bat.battleType == 13) {
                            attTechAddGZJY = dataGetter.getTechEffectCache().getTechEffect(campArmy.playerId, 40) / 100.0;
                        }
                        double specialGeneralAddExp = 0.0;
                        if (roundInfo.attCampArmy.getSpecialGeneral().generalType == 4 && roundInfo.defCampArmy.isPhantom) {
                            specialGeneralAddExp = roundInfo.attCampArmy.getSpecialGeneral().param;
                        }
                        if (roundInfo.attCampArmy.getSpecialGeneral().generalType == 3 && (roundInfo.defCampArmy.isBarPhantom || roundInfo.defCampArmy.isBarEA)) {
                            specialGeneralAddExp = roundInfo.attCampArmy.getSpecialGeneral().param2;
                        }
                        double farmBuff = 0.0;
                        if (!campArmy.isPhantom && campArmy.playerId > 0) {
                            farmBuff = dataGetter.getWorldFarmService().getBuff(campArmy.playerId, campArmy.generalId);
                            farmBuff = farmBuff * 1.0 / 100.0;
                        }
                        double bainianBuff = 0.0;
                        if (!campArmy.isPhantom && campArmy.playerId > 0) {
                            bainianBuff = BaiNianEvent.getBuff(campArmy.playerId, dataGetter);
                            bainianBuff = bainianBuff * 1.0 / 100.0;
                        }
                        mExp *= (int)(att_world_frontLine_buff_exp_e + attLowLvSCoe + attTechAddGZJY + bat.world_round_ally_buff_att_e + specialGeneralAddExp + roundInfo.attCampArmy.activityAddExp + farmBuff + bainianBuff);
                        battleDrop.type = 5;
                        battleDrop.num = mExp;
                        tempRD.roundDropMap.put(5, battleDrop);
                        pInfo.addDrop(battleDrop);
                        double gExpAdd = 0.0;
                        gExpAdd = dataGetter.getTechEffectCache().getTechEffect(campArmy.playerId, 16) / 100.0;
                        tempRD.gExp = (int)(gAttOmega * frc.getE() * roundInfo.attCampArmy.rewardDouble * (1.0 + gExpAdd + specialGeneralAddExp + roundInfo.attCampArmy.activityAddExp + farmBuff + bainianBuff));
                        if (campArmy.playerId == roundInfo.attCampArmy.playerId) {
                            roundInfo.attRoundReward.addDropMap(tempRD.roundDropMap);
                            try {
                                final List<UpdateExp> defUpExpList = dataGetter.getGeneralService().updateExpAndGeneralLevel(campArmy.getPlayerId(), campArmy.getGeneralId(), tempRD.gExp);
                                if (defUpExpList == null) {
                                    continue;
                                }
                                int addGExp = 0;
                                for (final UpdateExp ue : defUpExpList) {
                                    addGExp += (int)ue.getCurExp();
                                }
                                if (tempRD.gExp != 0 && addGExp == 0) {
                                    campArmy.expTop = 1;
                                }
                                tempRD.gUpLv = defUpExpList.size() - 1;
                                if (tempRD.gUpLv <= 0) {
                                    continue;
                                }
                                final PlayerGeneralMilitary pgm = dataGetter.getPlayerGeneralMilitaryDao().getMilitary(campArmy.playerId, campArmy.generalId);
                                if (pgm == null) {
                                    continue;
                                }
                                campArmy.generalLv = pgm.getLv();
                                dataGetter.getGeneralService().sendGmUpdate1(pgm.getPlayerId(), pgm.getGeneralId(), true);
                            }
                            catch (Exception e) {
                                ErrorSceneLog.getInstance().error("updateExpAndGeneralLevel catch Exception.", e);
                                ErrorSceneLog.getInstance().appendErrorMsg("updateExpAndGeneralLevel catch Exception.").appendBattleId(bat.getBattleId()).appendPlayerId(campArmy.playerId).appendPlayerName(campArmy.playerName).appendGeneralId(campArmy.generalId).appendGeneralName(campArmy.generalName).flush();
                            }
                        }
                        else {
                            roundAddRewardSingle(dataGetter, campArmy, tempRD);
                            if (!debug.equals("1")) {
                                continue;
                            }
                            roundInfo.caculateDebugBuffer.append("Att reward by Def tactic ").append(" | ").append("myPlayer:" + campArmy.playerName).append(" | ").append("myGeneral:" + campArmy.generalName).append(" | ").append("myLost:" + myLost).append(" | ").append("myLv:" + myLv).append(" | ").append("gMyLv:" + gMyLv).append(" | ").append("myTroopId:" + myTroopId).append(" | ").append("mAttOmega:" + mAttOmega).append(" | ").append("gAttOmega:" + gAttOmega).append(" | ").append("rewardDouble:" + campArmy.rewardDouble).append(" | ").append("TECH_KEY_16_LILIAN:" + gExpAdd).append(" | ").append("LowLvSCoe:" + attLowLvSCoe).append(" | ").append("world_frontLine_buff_exp_e:" + att_world_frontLine_buff_exp_e).append(" | ").append("TechAddGZJY:" + attTechAddGZJY).append(" | ").append("world_round_ally_buff_att_e:" + bat.world_round_ally_buff_att_e).append(" | ").append("copper:" + copper).append(" | ").append("mExp:" + mExp).append(" | ").append("gExp:" + tempRD.gExp).append(" \n ");
                        }
                    }
                }
            }
        }
        int attTacticLost = 0;
        if (roundInfo.defTacticInfo != null) {
            attTacticLost = roundInfo.defTacticInfo.firstCReduce;
        }
        final int attLost = roundInfo.attLost + roundInfo.attStrategyLost + attTacticLost;
        final int attLv = roundInfo.attCampArmy.getPlayerLv();
        final int gAttLv = roundInfo.attCampArmy.getGeneralLv();
        final int troopIdAtt = roundInfo.attCampArmy.getTroopId();
        int defTacticLost = 0;
        if (roundInfo.attTacticInfo != null) {
            defTacticLost = roundInfo.attTacticInfo.allCReduce;
        }
        final int defLost = roundInfo.defLost + roundInfo.defStrategyLost + defTacticLost;
        final int defLv = this.getDefPlayerLevel(dataGetter, bat, roundInfo);
        final int gDefLv = roundInfo.defCampArmy.getGeneralLv();
        final int TroopIdDef = roundInfo.defCampArmy.getTroopId();
        dataGetter.getJobService().addJob("autoBattleService", "increaseLost", String.valueOf(roundInfo.attCampArmy.getPlayerId()) + "#" + defLost, System.currentTimeMillis(), false);
        final double mAttOmega2 = getRoundRewardBase(dataGetter, frc, troopIdAtt, TroopIdDef, attLost, defLost, attLv, defLv);
        final double gAttOmega2 = getRoundRewardBase(dataGetter, frc, troopIdAtt, TroopIdDef, attLost, defLost, gAttLv, gDefLv);
        BattleDrop battleDrop2 = new BattleDrop();
        final int copper2 = (int)(mAttOmega2 * frc.getM());
        battleDrop2.type = 1;
        battleDrop2.num = copper2;
        roundInfo.attRoundReward.roundDropMap.put(1, battleDrop2);
        battleDrop2 = new BattleDrop();
        int mExp2 = (int)(mAttOmega2 * frc.getC() * roundInfo.attCampArmy.rewardDouble);
        double att_world_frontLine_buff_exp_e2 = 1.0;
        if (bat.world_frontLine_buff_att_def_e_side == 1) {
            att_world_frontLine_buff_exp_e2 = bat.world_frontLine_buff_exp_e;
        }
        final double attLowLvSCoe2 = this.getLowLvSCoe(bat, attLv, defLv);
        double attTechAddGZJY2 = 0.0;
        if (bat.battleType == 3 || bat.battleType == 13) {
            attTechAddGZJY2 = dataGetter.getTechEffectCache().getTechEffect(roundInfo.attCampArmy.playerId, 40) / 100.0;
        }
        double specialGeneralAddExp2 = 0.0;
        if (roundInfo.attCampArmy.getSpecialGeneral().generalType == 4 && roundInfo.defCampArmy.isPhantom) {
            specialGeneralAddExp2 = roundInfo.attCampArmy.getSpecialGeneral().param;
        }
        if (roundInfo.attCampArmy.getSpecialGeneral().generalType == 3 && (roundInfo.defCampArmy.isBarPhantom || roundInfo.defCampArmy.isBarEA)) {
            specialGeneralAddExp2 = roundInfo.attCampArmy.getSpecialGeneral().param2;
        }
        double farmBuff2 = 0.0;
        if (!roundInfo.attCampArmy.isPhantom && roundInfo.attCampArmy.playerId > 0) {
            farmBuff2 = dataGetter.getWorldFarmService().getBuff(roundInfo.attCampArmy.playerId, roundInfo.attCampArmy.generalId);
            farmBuff2 = farmBuff2 * 1.0 / 100.0;
        }
        double bainianBuff2 = 0.0;
        if (!roundInfo.attCampArmy.isPhantom && roundInfo.attCampArmy.playerId > 0) {
            bainianBuff2 = BaiNianEvent.getBuff(roundInfo.attCampArmy.playerId, dataGetter);
            bainianBuff2 = bainianBuff2 * 1.0 / 100.0;
        }
        mExp2 *= (int)(att_world_frontLine_buff_exp_e2 + attLowLvSCoe2 + attTechAddGZJY2 + bat.world_round_ally_buff_att_e + specialGeneralAddExp2 + roundInfo.attCampArmy.activityAddExp + farmBuff2 + bainianBuff2);
        battleDrop2.type = 5;
        battleDrop2.num = mExp2;
        roundInfo.attRoundReward.roundDropMap.put(5, battleDrop2);
        double gExpAdd2 = 0.0;
        gExpAdd2 = dataGetter.getTechEffectCache().getTechEffect(roundInfo.attCampArmy.playerId, 16) / 100.0;
        roundInfo.attRoundReward.gExp = (int)(gAttOmega2 * frc.getE() * roundInfo.attCampArmy.rewardDouble * (1.0 + gExpAdd2 + specialGeneralAddExp2 + roundInfo.attCampArmy.activityAddExp + farmBuff2 + bainianBuff2));
        if (debug.equals("1")) {
            roundInfo.caculateDebugBuffer.append("Round.").append(" | ").append("battleId:" + bat.getBattleId()).append(" | ").append("FightRewardCoe:" + frc.getId()).append(" | ").append("attPlayer:" + roundInfo.attCampArmy.playerName).append(" | ").append("defPlayer:" + roundInfo.defCampArmy.playerName).append(" | ").append("attGeneral:" + roundInfo.attCampArmy.generalName).append(" | ").append("defGeneral:" + roundInfo.defCampArmy.generalName).append(" | ").append("attTroopId:" + troopIdAtt).append(" | ").append("defTroopId:" + TroopIdDef).append(" | ").append("attRoundLost:" + roundInfo.attLost).append(" | ").append("defRoundLost:" + roundInfo.defLost).append(" | ").append("attStrategyLost:" + roundInfo.attStrategyLost).append(" | ").append("defStrategyLost:" + roundInfo.defStrategyLost).append(" | ").append("attTacticLost:" + attTacticLost).append(" | ").append("defTacticLost:" + defTacticLost).append(" | ").append("attLostTotal:" + attLost).append(" | ").append("defLostTotal:" + defLost).append(" | ").append("attLv:" + attLv).append(" | ").append("defLv:" + defLv).append(" | ").append("gAttLv:" + gAttLv).append(" | ").append("gDefLv:" + gDefLv).append(" \n ");
            roundInfo.caculateDebugBuffer.append("Att").append(" | ").append("mAttOmega:" + mAttOmega2).append(" | ").append("gAttOmega:" + gAttOmega2).append(" | ").append("rewardDouble:" + roundInfo.attCampArmy.rewardDouble).append(" | ").append("ATT TECH_KEY_16_LILIAN:" + gExpAdd2).append(" | ").append("attLowLvSCoe:" + attLowLvSCoe2).append(" | ").append("att_world_frontLine_buff_exp_e:" + att_world_frontLine_buff_exp_e2).append(" | ").append("attTechAddGZJY:" + attTechAddGZJY2).append(" | ").append("world_round_ally_buff_att_e:" + bat.world_round_ally_buff_att_e).append(" | ").append("copper:" + copper2).append(" | ").append("mExp:" + mExp2).append(" | ").append("gExp:" + roundInfo.attRoundReward.gExp).append(" \n ");
        }
    }
    
    public void roundCaculateDefReward(final IDataGetter dataGetter, final FightRewardCoe frc, final Battle bat, final RoundInfo roundInfo) {
        final String debug = Configuration.getProperty("gcld.battle.caculate.debug");
        if (roundInfo.attTacticInfo != null && roundInfo.attTacticInfo.reduceMap != null && roundInfo.attTacticInfo.reduceMap.size() > 1) {
            final int counterLost = 0;
            final int counterLv = roundInfo.attCampArmy.playerLv;
            final int gCounterLv = roundInfo.attCampArmy.generalLv;
            final int counterTroopId = roundInfo.attCampArmy.troopId;
            if (debug.equals("1")) {
                roundInfo.caculateDebugBuffer.append("attTacticInfo!=null.").append(" | ").append("FightRewardCoe:" + frc.getId()).append(" | ").append("counterPlayer:" + roundInfo.attCampArmy.playerName).append(" | ").append("counterGeneral:" + roundInfo.attCampArmy.generalName).append(" | ").append("counterLost:" + counterLost).append(" | ").append("counterLv:" + counterLv).append(" | ").append("gCounterLv:" + gCounterLv).append(" | ").append("counterTroopId:" + counterTroopId).append("\n");
            }
            for (final CampArmy campArmy : roundInfo.attTacticInfo.reduceMap.keySet()) {
                if (campArmy != roundInfo.defCampArmy && campArmy.updateDB && campArmy.playerId > 0) {
                    final PlayerInfo pInfo = bat.inBattlePlayers.get(campArmy.playerId);
                    if (pInfo == null) {
                        ErrorSceneLog.getInstance().appendErrorMsg("PlayerInfo is null").appendPlayerName(campArmy.playerName).appendPlayerId(campArmy.playerId).appendGeneralName(campArmy.generalName).appendGeneralId(campArmy.generalId).append("isupdateDB", campArmy.updateDB).append("isPhantom", campArmy.isPhantom).append("pgmVId", campArmy.pgmVId).appendBattleId(bat.getBattleId()).appendMethodName("roundCaculateDefReward").flush();
                    }
                    else {
                        final RoundReward tempRD = new RoundReward();
                        final int myLost = roundInfo.attTacticInfo.reduceMap.get(campArmy);
                        final int myLv = campArmy.playerLv;
                        final int gMyLv = campArmy.generalLv;
                        final int myTroopId = campArmy.troopId;
                        final double mAttOmega = getRoundRewardBase(dataGetter, frc, myTroopId, counterTroopId, myLost, counterLost, myLv, counterLv);
                        final double gAttOmega = getRoundRewardBase(dataGetter, frc, myTroopId, counterTroopId, myLost, counterLost, gMyLv, gCounterLv);
                        BattleDrop battleDrop = new BattleDrop();
                        final int copper = (int)(mAttOmega * frc.getM());
                        battleDrop.type = 1;
                        battleDrop.num = copper;
                        tempRD.roundDropMap.put(1, battleDrop);
                        pInfo.addDrop(battleDrop);
                        battleDrop = new BattleDrop();
                        int mExp = (int)(mAttOmega * frc.getC() * campArmy.rewardDouble);
                        double att_world_frontLine_buff_exp_e = 1.0;
                        if (bat.world_frontLine_buff_att_def_e_side == 1) {
                            att_world_frontLine_buff_exp_e = bat.world_frontLine_buff_exp_e;
                        }
                        final double attLowLvSCoe = this.getLowLvSCoe(bat, myLv, counterLv);
                        double attTechAddGZJY = 0.0;
                        if (bat.battleType == 3 || bat.battleType == 13) {
                            attTechAddGZJY = dataGetter.getTechEffectCache().getTechEffect(campArmy.playerId, 40) / 100.0;
                        }
                        double specialGeneralAddExp = 0.0;
                        if (roundInfo.defCampArmy.getSpecialGeneral().generalType == 4 && roundInfo.attCampArmy.isPhantom) {
                            specialGeneralAddExp = roundInfo.defCampArmy.getSpecialGeneral().param;
                        }
                        if (roundInfo.defCampArmy.getSpecialGeneral().generalType == 3 && (roundInfo.attCampArmy.isBarPhantom || roundInfo.attCampArmy.isBarEA)) {
                            specialGeneralAddExp = roundInfo.defCampArmy.getSpecialGeneral().param2;
                        }
                        double farmBuff = 0.0;
                        if (!campArmy.isPhantom && campArmy.playerId > 0) {
                            farmBuff = dataGetter.getWorldFarmService().getBuff(campArmy.playerId, campArmy.generalId);
                            farmBuff = farmBuff * 1.0 / 100.0;
                        }
                        double bainianBuff = 0.0;
                        if (!campArmy.isPhantom && campArmy.playerId > 0) {
                            bainianBuff = BaiNianEvent.getBuff(campArmy.playerId, dataGetter);
                            bainianBuff = bainianBuff * 1.0 / 100.0;
                        }
                        mExp *= (int)(att_world_frontLine_buff_exp_e + attLowLvSCoe + attTechAddGZJY + bat.world_round_ally_buff_def_e + specialGeneralAddExp + roundInfo.defCampArmy.activityAddExp + farmBuff + bainianBuff);
                        battleDrop.type = 5;
                        battleDrop.num = mExp;
                        tempRD.roundDropMap.put(5, battleDrop);
                        pInfo.addDrop(battleDrop);
                        double gExpAdd = 0.0;
                        gExpAdd = dataGetter.getTechEffectCache().getTechEffect(campArmy.playerId, 16) / 100.0;
                        tempRD.gExp = (int)(gAttOmega * frc.getE() * roundInfo.attCampArmy.rewardDouble * (1.0 + gExpAdd + specialGeneralAddExp + roundInfo.defCampArmy.activityAddExp + farmBuff + bainianBuff));
                        if (campArmy.playerId == roundInfo.defCampArmy.playerId) {
                            roundInfo.defRoundReward.addDropMap(tempRD.roundDropMap);
                            try {
                                final List<UpdateExp> defUpExpList = dataGetter.getGeneralService().updateExpAndGeneralLevel(campArmy.getPlayerId(), campArmy.getGeneralId(), tempRD.gExp);
                                if (defUpExpList == null) {
                                    continue;
                                }
                                int addGExp = 0;
                                for (final UpdateExp ue : defUpExpList) {
                                    addGExp += (int)ue.getCurExp();
                                }
                                if (tempRD.gExp != 0 && addGExp == 0) {
                                    campArmy.expTop = 1;
                                }
                                tempRD.gUpLv = defUpExpList.size() - 1;
                                if (tempRD.gUpLv <= 0) {
                                    continue;
                                }
                                final PlayerGeneralMilitary pgm = dataGetter.getPlayerGeneralMilitaryDao().getMilitary(campArmy.playerId, campArmy.generalId);
                                if (pgm == null) {
                                    continue;
                                }
                                campArmy.generalLv = pgm.getLv();
                                dataGetter.getGeneralService().sendGmUpdate1(pgm.getPlayerId(), pgm.getGeneralId(), true);
                            }
                            catch (Exception e) {
                                ErrorSceneLog.getInstance().error("updateExpAndGeneralLevel catch Exception.", e);
                                ErrorSceneLog.getInstance().appendErrorMsg("updateExpAndGeneralLevel catch Exception.").appendBattleId(bat.getBattleId()).appendPlayerId(campArmy.playerId).appendPlayerName(campArmy.playerName).appendGeneralId(campArmy.generalId).appendGeneralName(campArmy.generalName).flush();
                            }
                        }
                        else {
                            roundAddRewardSingle(dataGetter, campArmy, tempRD);
                            if (!debug.equals("1")) {
                                continue;
                            }
                            roundInfo.caculateDebugBuffer.append("Def reward by Att tactic ").append(" | ").append("myPlayer:" + campArmy.playerName).append(" | ").append("myGeneral:" + campArmy.generalName).append(" | ").append("mAttOmega:" + mAttOmega).append(" | ").append("gAttOmega:" + gAttOmega).append(" | ").append("rewardDouble:" + campArmy.rewardDouble).append(" | ").append("TECH_KEY_16_LILIAN:" + gExpAdd).append(" | ").append("LowLvSCoe:" + attLowLvSCoe).append(" | ").append("world_frontLine_buff_exp_e:" + att_world_frontLine_buff_exp_e).append(" | ").append("TechAddGZJY:" + attTechAddGZJY).append(" | ").append("world_round_ally_buff_def_e:" + bat.world_round_ally_buff_def_e).append(" | ").append("copper:" + copper).append(" | ").append("mExp:" + mExp).append(" | ").append("gExp:" + tempRD.gExp).append(" \n ");
                        }
                    }
                }
            }
        }
        int attTacticLost = 0;
        if (roundInfo.defTacticInfo != null) {
            attTacticLost = roundInfo.defTacticInfo.allCReduce;
        }
        final int attLost = roundInfo.attLost + roundInfo.attStrategyLost + attTacticLost;
        final int attLv = roundInfo.attCampArmy.getPlayerLv();
        final int gAttLv = roundInfo.attCampArmy.getGeneralLv();
        final int troopIdAtt = roundInfo.attCampArmy.getTroopId();
        int defTacticLost = 0;
        if (roundInfo.attTacticInfo != null) {
            defTacticLost = roundInfo.attTacticInfo.firstCReduce;
        }
        final int defLost = roundInfo.defLost + roundInfo.defStrategyLost + defTacticLost;
        final int defLv = this.getDefPlayerLevel(dataGetter, bat, roundInfo);
        final int gDefLv = roundInfo.defCampArmy.getGeneralLv();
        final int TroopIdDef = roundInfo.defCampArmy.getTroopId();
        dataGetter.getJobService().addJob("autoBattleService", "increaseLost", String.valueOf(roundInfo.defCampArmy.getPlayerId()) + "#" + attLost, System.currentTimeMillis(), false);
        final double mDefOmega = getRoundRewardBase(dataGetter, frc, TroopIdDef, troopIdAtt, defLost, attLost, defLv, attLv);
        final double gDefOmega = getRoundRewardBase(dataGetter, frc, TroopIdDef, troopIdAtt, defLost, attLost, gDefLv, gAttLv);
        BattleDrop battleDrop2 = new BattleDrop();
        final int copper2 = (int)(mDefOmega * frc.getM());
        battleDrop2.type = 1;
        battleDrop2.num = copper2;
        roundInfo.defRoundReward.roundDropMap.put(1, battleDrop2);
        battleDrop2 = new BattleDrop();
        int mExp2 = (int)(mDefOmega * frc.getC() * roundInfo.defCampArmy.rewardDouble);
        double def_world_frontLine_buff_exp_e = 1.0;
        if (bat.world_frontLine_buff_att_def_e_side == 2) {
            def_world_frontLine_buff_exp_e = bat.world_frontLine_buff_exp_e;
        }
        final double defLowLvSCoe = this.getLowLvSCoe(bat, defLv, attLv);
        double defTechAddGZJY = 0.0;
        if (bat.battleType == 3 || bat.battleType == 13) {
            defTechAddGZJY = dataGetter.getTechEffectCache().getTechEffect(roundInfo.defCampArmy.playerId, 40) / 100.0;
        }
        double specialGeneralAddExp2 = 0.0;
        if (roundInfo.defCampArmy.getSpecialGeneral().generalType == 4 && roundInfo.attCampArmy.isPhantom) {
            specialGeneralAddExp2 = roundInfo.defCampArmy.getSpecialGeneral().param;
        }
        if (roundInfo.defCampArmy.getSpecialGeneral().generalType == 3 && (roundInfo.attCampArmy.isBarPhantom || roundInfo.attCampArmy.isBarEA)) {
            specialGeneralAddExp2 = roundInfo.defCampArmy.getSpecialGeneral().param2;
        }
        double farmBuff2 = 0.0;
        if (!roundInfo.defCampArmy.isPhantom && roundInfo.defCampArmy.playerId > 0) {
            farmBuff2 = dataGetter.getWorldFarmService().getBuff(roundInfo.defCampArmy.playerId, roundInfo.defCampArmy.generalId);
            farmBuff2 = farmBuff2 * 1.0 / 100.0;
        }
        double bainianBuff2 = 0.0;
        if (!roundInfo.defCampArmy.isPhantom && roundInfo.defCampArmy.playerId > 0) {
            bainianBuff2 = BaiNianEvent.getBuff(roundInfo.defCampArmy.playerId, dataGetter);
            bainianBuff2 = bainianBuff2 * 1.0 / 100.0;
        }
        mExp2 *= (int)(def_world_frontLine_buff_exp_e + defLowLvSCoe + defTechAddGZJY + bat.world_round_ally_buff_def_e + specialGeneralAddExp2 + roundInfo.defCampArmy.activityAddExp + farmBuff2 + bainianBuff2);
        battleDrop2.type = 5;
        battleDrop2.num = mExp2;
        roundInfo.defRoundReward.roundDropMap.put(5, battleDrop2);
        final double gExpAdd2 = dataGetter.getTechEffectCache().getTechEffect(roundInfo.defCampArmy.playerId, 16) / 100.0;
        roundInfo.defRoundReward.gExp = (int)(gDefOmega * frc.getE() * roundInfo.defCampArmy.rewardDouble * (1.0 + gExpAdd2 + specialGeneralAddExp2 + roundInfo.defCampArmy.activityAddExp) + farmBuff2 + bainianBuff2);
        if (debug.equals("1")) {
            roundInfo.caculateDebugBuffer.append("Def").append(" | ").append("mDefOmega:" + mDefOmega).append(" | ").append("gDefOmega:" + gDefOmega).append(" | ").append("rewardDouble:" + roundInfo.defCampArmy.rewardDouble).append(" | ").append("DEF TECH_KEY_16_LILIAN:" + gExpAdd2).append(" | ").append("defLowLvSCoe:" + defLowLvSCoe).append(" | ").append("def_world_frontLine_buff_exp_e:" + def_world_frontLine_buff_exp_e).append(" | ").append("defTechAddGZJY:" + defTechAddGZJY).append(" | ").append("world_round_ally_buff_def_e:" + bat.world_round_ally_buff_def_e).append(" | ").append("copper:" + copper2).append(" | ").append("mExp:" + mExp2).append(" | ").append("gExp:" + roundInfo.defRoundReward.gExp).append(" \n ");
        }
    }
    
    double getLowLvSCoe(final Battle bat, final int myLv, final int counterLv) {
        if (bat.battleType != 3 && bat.battleType != 13) {
            return 0.0;
        }
        if (counterLv - myLv >= 25) {
            return 1.0;
        }
        if (counterLv - myLv >= 15) {
            return 0.1 * (counterLv - myLv - 15);
        }
        return 0.0;
    }
    
    public void roundTacticUpdateDB(final IDataGetter dataGetter, final Battle bat, final RoundInfo roundInfo) {
        if (roundInfo.attTacticInfo != null && roundInfo.attTacticInfo.executed) {
            this.tacticUpdateDB(dataGetter, bat, bat.defCamp, roundInfo.attCampArmy, roundInfo.defCampArmy, roundInfo.attTacticInfo);
            if (roundInfo.attTacticInfo.reduceMap != null) {
                for (final CampArmy campArmy : roundInfo.attTacticInfo.reduceMap.keySet()) {
                    if (campArmy != roundInfo.defCampArmy) {
                        final int reduce = roundInfo.attTacticInfo.reduceMap.get(campArmy);
                        roundInfo.defRoundLostTotal += reduce;
                    }
                }
            }
        }
        if (roundInfo.defTacticInfo != null && roundInfo.defTacticInfo.executed) {
            this.tacticUpdateDB(dataGetter, bat, bat.attCamp, roundInfo.defCampArmy, roundInfo.attCampArmy, roundInfo.defTacticInfo);
            if (roundInfo.defTacticInfo.reduceMap != null) {
                for (final CampArmy campArmy : roundInfo.defTacticInfo.reduceMap.keySet()) {
                    if (campArmy != roundInfo.attCampArmy) {
                        final int reduce = roundInfo.defTacticInfo.reduceMap.get(campArmy);
                        roundInfo.attRoundLostTotal += reduce;
                    }
                }
            }
        }
    }
    
    public void roundAddReward(final IDataGetter dataGetter, final Battle bat, final RoundInfo roundInfo, final RoundReward attRoundReward, final RoundReward defRoundReward) {
        roundAddRewardSingle(dataGetter, roundInfo.attCampArmy, attRoundReward);
        if ((bat.getBattleType() == 3 || bat.getBattleType() == 13) && !bat.isNpc) {
            roundAddRewardSingle(dataGetter, roundInfo.defCampArmy, defRoundReward);
        }
    }
    
    protected static void roundAddRewardSingle(final IDataGetter dataGetter, final CampArmy campArmy, final RoundReward roundReward) {
        if (campArmy.updateDB) {
            final BattleDrop battleDropChiefExp = roundReward.roundDropMap.get(5);
            AsynchronousDBOperationManager.getInstance().addBattleDropRetry(campArmy.getPlayerId(), battleDropChiefExp, "\u6218\u6597\u83b7\u5f97");
            dataGetter.getJobService().addJob("autoBattleService", "increaseExp", String.valueOf(campArmy.getPlayerId()) + "#" + battleDropChiefExp.num, System.currentTimeMillis(), false);
            AsynchronousDBOperationManager.getInstance().addBattleDropRetry(campArmy.getPlayerId(), roundReward.roundDropMap.get(1), "\u6218\u6597\u83b7\u5f97");
            final BattleDrop copper = roundReward.roundDropMap.get(1);
            if (copper != null) {
                final int copperNum = copper.num;
                dataGetter.getIndividualTaskService().sendTaskMessage(new PlayerDto(campArmy.getPlayerId(), campArmy.getForceId()), copperNum, "killyb");
            }
            try {
                if (roundReward.gExp > 0) {
                    final IAsynchronousDBOperation gExpOperation = new AsynchronousDBOperationAddGeneralExp(campArmy.getPlayerId(), campArmy.getGeneralId(), roundReward.gExp, "\u6218\u6597\u83b7\u5f97");
                    AsynchronousDBOperationManager.getInstance().addDBRetryOperation(gExpOperation);
                }
            }
            catch (Exception e) {
                ErrorSceneLog.getInstance().appendErrorMsg("Builder roundAddRewardSingle \u589e\u52a0\u6b66\u5c06\u7b49\u7ea7\u7ecf\u9a8c\u7ecf\u9a8c Exception").appendClassName("Builder").appendMethodName("roundAddRewardSingle").appendPlayerName(campArmy.playerName).append("PlayerId", campArmy.playerId).appendGeneralName(campArmy.generalName).appendGeneralId(campArmy.generalId).append("gExp", roundReward.gExp).flush();
                ErrorSceneLog.getInstance().error("Builder roundAddRewardSingle \u589e\u52a0\u73a9\u5bb6\u7b49\u7ea7\u7ecf\u9a8c Exception", e);
            }
        }
    }
    
    public void tacticUpdateDB(final IDataGetter dataGetter, final Battle bat, final LinkedList<CampArmy> campList, final CampArmy exeTacticCa, final CampArmy firstDefCa, final TacticInfo tacticInfoA) {
        if (tacticInfoA.reduceMap != null) {
            for (final CampArmy campArmy : tacticInfoA.reduceMap.keySet()) {
                if (campArmy != firstDefCa && campArmy.updateDB) {
                    final int reduce = tacticInfoA.reduceMap.get(campArmy);
                    if (campArmy.getArmyHpLoss() >= campArmy.getArmyHpOrg()) {
                        final IAsynchronousDBOperation gStateOperation = new AsynchronousDBOperationResetGeneralState(campArmy.getPlayerId(), campArmy.generalId, reduce);
                        AsynchronousDBOperationManager.getInstance().addDBOperation(gStateOperation);
                    }
                    else {
                        final IAsynchronousDBOperation gHpOperation = new AsynchronousDBOperationReduceGeneralHp(campArmy.getPlayerId(), campArmy.generalId, reduce);
                        AsynchronousDBOperationManager.getInstance().addDBOperation(gHpOperation);
                    }
                    if (!campArmy.isPhantom()) {
                        dataGetter.getGeneralService().sendGmForcesReduce(campArmy.playerId, campArmy.generalId, reduce, 0, bat.isInSceneSet(campArmy.playerId));
                    }
                    BattleSceneLog.getInstance().appendLogMsg("exe tactic reduce general force").appendPlayerName(campArmy.playerName).appendGeneralName(campArmy.generalName).append("reduce", reduce).appendClassName("Builder").appendMethodName("tacticUpdateDB").flush();
                }
            }
        }
        if (exeTacticCa.updateDB) {
            final PlayerInfo pi = bat.inBattlePlayers.get(exeTacticCa.getPlayerId());
            if (pi == null) {
                return;
            }
            if (tacticInfoA.reward != null) {
                final RewardInfo ri = tacticInfoA.reward.rewardPlayer(dataGetter, exeTacticCa.playerId, "\u7279\u6b8a\u6218\u6cd5", exeTacticCa.generalId);
                if (ri.getReward() == 1) {
                    pi.rbType = ri.getType();
                    final PlayerInfo playerInfo = pi;
                    playerInfo.rbTotal += ri.getAddValue();
                    tacticInfoA.tacticDrop = new BattleDrop();
                    tacticInfoA.tacticDrop.type = ri.getType();
                    tacticInfoA.tacticDrop.num = ri.getAddValue();
                }
                else {
                    pi.rbTop = 1;
                    exeTacticCa.rbTop = 1;
                }
            }
            if (!exeTacticCa.isPhantom()) {
                dataGetter.getGeneralService().sendGmForcesReduce(exeTacticCa.playerId, exeTacticCa.generalId, 0, tacticInfoA.allCReduce, bat.isInSceneSet(exeTacticCa.playerId));
            }
        }
    }
    
    public int caculateRoundCopper(final IDataGetter dataGetter, final Battle bat, final int playerId, final double mAttOmega, final FightRewardCoe frc) {
        final double copperAdd = dataGetter.getTechEffectCache().getTechEffect(playerId, 15) / 100.0;
        final int copper = (int)(mAttOmega * frc.getM() * (1.0 + copperAdd));
        return copper;
    }
    
    public void roundReduceTroop(final IDataGetter dataGetter, final Battle bat, final RoundInfo roundInfo) {
        final CampArmy attCa = roundInfo.attCampArmy;
        final CampArmy defCa = roundInfo.defCampArmy;
        int attreduce = 0;
        int defreduce = 0;
        if (roundInfo.defTacticInfo != null && roundInfo.defTacticInfo.executed && roundInfo.defTacticInfo.reduceMap != null && roundInfo.defTacticInfo.reduceMap.get(attCa) != null) {
            final int attCaTacticReduce = roundInfo.defTacticInfo.reduceMap.get(attCa);
            attreduce += attCaTacticReduce;
            BattleSceneLog.getInstance().appendLogMsg("def exe tactic, att reduce general force").appendPlayerName(attCa.playerName).appendGeneralName(attCa.generalName).append("attCaTacticReduce", attCaTacticReduce).flush();
        }
        if (roundInfo.attTacticInfo != null && roundInfo.attTacticInfo.executed && roundInfo.attTacticInfo.reduceMap != null && roundInfo.attTacticInfo.reduceMap.get(defCa) != null) {
            final int defCaTacticReduce = roundInfo.attTacticInfo.reduceMap.get(defCa);
            defreduce += defCaTacticReduce;
            BattleSceneLog.getInstance().appendLogMsg("att exe tactic, def reduce general force").appendPlayerName(defCa.playerName).appendGeneralName(defCa.generalName).append("defCaTacticReduce", defCaTacticReduce).flush();
        }
        attreduce += roundInfo.attStrategyLost;
        defreduce += roundInfo.defStrategyLost;
        attreduce += roundInfo.attLost;
        defreduce += roundInfo.defLost;
        roundInfo.attRoundLostTotal += attreduce;
        roundInfo.defRoundLostTotal += defreduce;
        this.roundReduceTroopSingle(dataGetter, bat, roundInfo.attCampArmy, roundInfo.defCampArmy, roundInfo.killAttG, attreduce, defreduce);
        this.roundReduceTroopSingle(dataGetter, bat, roundInfo.defCampArmy, roundInfo.attCampArmy, roundInfo.killDefG, defreduce, attreduce);
    }
    
    public void roundReduceTroopSingle(final IDataGetter dataGetter, final Battle bat, final CampArmy campArmyA, final CampArmy campArmyB, final boolean isBeKill, final int lostA, final int lostB) {
        if (campArmyA.updateDB) {
            if (isBeKill) {
                final IAsynchronousDBOperation gStateOperation = new AsynchronousDBOperationResetGeneralState(campArmyA.getPlayerId(), campArmyA.generalId, lostA);
                AsynchronousDBOperationManager.getInstance().addDBOperation(gStateOperation);
                dataGetter.getGeneralService().sendGmForcesReduce(campArmyA.getPlayerId(), campArmyA.generalId, lostA, lostB, bat.inSceneSet.contains(campArmyA.getPlayerId()), campArmyB.playerName, campArmyB.generalName, campArmyB.quality);
            }
            else {
                if (lostA > 0) {
                    final IAsynchronousDBOperation gHpOperation = new AsynchronousDBOperationReduceGeneralHp(campArmyA.getPlayerId(), campArmyA.generalId, lostA);
                    AsynchronousDBOperationManager.getInstance().addDBOperation(gHpOperation);
                }
                dataGetter.getGeneralService().sendGmForcesReduce(campArmyA.getPlayerId(), campArmyA.generalId, lostA, lostB, bat.inSceneSet.contains(campArmyA.getPlayerId()), campArmyB.playerName, campArmyB.generalName, campArmyB.quality);
            }
        }
    }
    
    public void dealTroopDrop(final IDataGetter dataGetter, final Battle bat, final RoundInfo roundInfo) {
    }
    
    public static void getReportType2(final StringBuilder battleMsg, final List<BattleArmy> addQlist, final String battleSide) {
        BattleArmy battleArmy = null;
        if (addQlist.size() > 0) {
            battleMsg.append(2).append("|").append(battleSide).append(";");
            for (int i = 0; i < addQlist.size(); ++i) {
                battleArmy = addQlist.get(i);
                int specialType = (battleArmy.getCampArmy().getTeamGenreal() == null) ? 0 : ((battleArmy.getCampArmy().getTeamEffect() > 0.0) ? 2 : 1);
                if (battleArmy.getTD_defense_e() > 0.0) {
                    specialType = 3;
                }
                final int troopHpMax = battleArmy.getCampArmy().getTroopHp() / 3;
                battleMsg.append(battleArmy.getPosition()).append("|").append(battleArmy.getCampArmy().getPlayerId()).append("|").append(battleArmy.getCampArmy().getTroopSerial()).append("|").append(battleArmy.getCampArmy().getTroopType()).append("|").append(battleArmy.getCampArmy().getTroopName()).append("|").append(battleArmy.getCampArmy().getTroopDropType()).append("|").append(battleArmy.getStrategy()).append("|").append(specialType).append("|");
                for (int j = 0; j < 3; ++j) {
                    battleMsg.append(battleArmy.getTroopHp()[j]).append("*").append(troopHpMax).append(",");
                }
                battleMsg.replace(battleMsg.length() - 1, battleMsg.length(), ";");
            }
            battleMsg.append("#");
        }
    }
    
    public int quitUpdateDb(final IDataGetter dataGetter, final CampArmy ca, final Battle bat, final PlayerInfo pi) {
        ca.inBattle = false;
        final int reduceNum = ca.armyHp;
        ca.armyHp = -1;
        if (pi.isAttSide) {
            bat.attCamp.remove(ca);
            BattleSceneLog.getInstance().info("#batId:" + bat.getBattleId() + "_" + bat.getStartTime() + "#quit:quit#side:att" + "#playerId:" + ca.getPlayerId() + ":" + ca.isPhantom + "#general:" + ca.getGeneralId() + "#attSize:" + bat.attCamp.size());
        }
        else {
            bat.defCamp.remove(ca);
            BattleSceneLog.getInstance().info("#batId:" + bat.getBattleId() + "_" + bat.getStartTime() + "#quit:quit#side:def" + "#playerId:" + ca.getPlayerId() + ":" + ca.isPhantom + "#general:" + ca.getGeneralId() + "#defSize:" + bat.defCamp.size());
        }
        if (ca.getPlayerId() > 0 && !ca.isPhantom) {
            NewBattleManager.getInstance().quitBattle(bat, ca.getPlayerId(), ca.getGeneralId());
        }
        dataGetter.getPlayerGeneralMilitaryDao().restartRecruit(ca.playerId, ca.generalId, ca.isInRecruit ? 1 : 0, new Date());
        dataGetter.getGeneralService().sendGmUpdate(ca.playerId, ca.generalId, true);
        return reduceNum;
    }
    
    public void quit(final Battle bat, final IDataGetter dataGetter, final int playerId, final PlayerInfo pi, final Set<CampArmy> quitGid, final boolean isQuitAll, final boolean isTuJin) {
        synchronized (bat.battleId) {
            int reduceNum = 0;
            for (final CampArmy ca : quitGid) {
                reduceNum += this.quitUpdateDb(dataGetter, ca, bat, pi);
            }
            if (pi.isAttSide) {
                bat.attBaseInfo.setNum(bat.attBaseInfo.getNum() - reduceNum);
            }
            else {
                bat.defBaseInfo.setNum(bat.defBaseInfo.getNum() - reduceNum);
            }
            for (final CampArmy ca : quitGid) {
                dataGetter.getPlayerGeneralMilitaryDao().updateStateByPidAndGid(ca.playerId, ca.generalId, 1, new Date());
            }
            final StringBuilder battleMsg = new StringBuilder();
            int sn = bat.ticket.incrementAndGet();
            battleMsg.append(sn).append("|").append(bat.getBattleId()).append("#");
            battleMsg.append(6).append("|").append(pi.isAttSide ? "att" : "def").append(";");
            for (final CampArmy ca2 : quitGid) {
                battleMsg.append(ca2.getId()).append("|");
            }
            battleMsg.append("#");
            if (battleMsg.length() > 0) {
                getReportType13(bat, battleMsg);
                bat.SaveCurReport(dataGetter);
                sendMsgToAll(bat, battleMsg);
            }
            if (isQuitAll && !isTuJin) {
                final StringBuilder myBattleMsg = new StringBuilder();
                ++sn;
                myBattleMsg.append(sn).append("#");
                this.oneQuitBattleMsg(pi, myBattleMsg, playerId, dataGetter, bat);
            }
        }
        // monitorexit(bat.battleId)
    }
    
    protected void oneQuitBattleMsg(final PlayerInfo pbi, final StringBuilder battleMsg, final int playerId, final IDataGetter dataGetter, final Battle bat) {
        final int result = 3;
        battleMsg.append(7).append("|").append(result).append("|").append(-1).append(";");
        sendMsgToOne(playerId, battleMsg);
        BattleSceneLog.getInstance().debug(" battleId  \u4e2a\u4eba\u9000\u51fa\u7ed3\u7b97\u4fe1\u606f: playerId:" + playerId + " msg:" + battleMsg);
    }
    
    public static void getLog(final String battleMsg, final String headInfo) {
        try {
            final StringBuilder formatInfo = new StringBuilder();
            formatInfo.append(headInfo);
            final String[] bats = battleMsg.split("#");
            formatInfo.append("\t").append(bats[0]).append("\n");
            String s = new String();
            int reportId = 0;
            String[] temp = null;
            for (int i = 1; i < bats.length; ++i) {
                s = bats[i];
                if (s.length() == 1) {
                    formatInfo.append("\t").append(s).append("\n");
                }
                else if (s.indexOf("|") == -1) {
                    formatInfo.append("\t").append(s).append("\n");
                }
                else {
                    reportId = Integer.valueOf(s.substring(0, s.indexOf("|")));
                    if (reportId == 1 || reportId == 5) {
                        temp = bats[i].split(";");
                        formatInfo.append("\t").append(temp[0]).append("\n");
                        for (int j = 1; j < temp.length; ++j) {
                            formatInfo.append("\t").append("\t").append(temp[j]).append("\n");
                        }
                    }
                    else if (reportId == 2 || reportId == 10) {
                        temp = bats[i].split(";");
                        formatInfo.append("\t").append(temp[0]).append("\n");
                        for (int j = 1; j < temp.length; ++j) {
                            formatInfo.append("\t").append("\t").append(temp[j]).append(";").append("\n");
                        }
                    }
                    else if (reportId == 12) {
                        temp = bats[i].split(";");
                        formatInfo.append("\t").append(temp[0]).append("\n");
                        for (int j = 1; j < temp.length; ++j) {
                            final String g = temp[j];
                            formatInfo.append("\t").append("\t").append(g).append(";").append("\n");
                        }
                        formatInfo.append("\n");
                    }
                    else if (reportId == 13) {
                        temp = bats[i].split("\\|");
                        formatInfo.append("\t").append(temp[0]).append("\n");
                        for (int j = 1; j < temp.length; ++j) {
                            final String[] gList = temp[j].split(";");
                            String[] array;
                            for (int length = (array = gList).length, k = 0; k < length; ++k) {
                                final String g2 = array[k];
                                formatInfo.append("\t").append("\t").append(g2).append(";").append("\n");
                            }
                            formatInfo.append("\n");
                        }
                    }
                    else {
                        formatInfo.append("\t").append(bats[i]).append("\n");
                    }
                }
            }
            BattleSceneLog.getInstance().debug(formatInfo.toString());
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("getLog exception", e);
        }
    }
    
    public void dealUniqueStaff(final IDataGetter dataGetter, final Battle bat, final int playerId, final int defId) {
    }
    
    public boolean dealTacticStrategy(final IDataGetter dataGetter, final Battle bat, final RoundInfo roundInfo) {
        boolean exeFight = true;
        boolean attExeTactic = false;
        boolean defExeTactic = false;
        final CampArmy attCamp = roundInfo.attCampArmy;
        final CampArmy defCamp = roundInfo.defCampArmy;
        boolean attLoss = false;
        boolean defLoss = false;
        int attTacticVal = roundInfo.attCampArmy.tacticVal;
        if (roundInfo.defCampArmy.getSpecialGeneral().generalType == 8) {
            attTacticVal = 0;
        }
        int defTacticVal = roundInfo.defCampArmy.tacticVal;
        if (roundInfo.attCampArmy.getSpecialGeneral().generalType == 8) {
            defTacticVal = 0;
        }
        roundInfo.tacticStrategyResult = 3;
        if (attTacticVal > 0 && attCamp.getTacicId() > 0 && (bat.isAttChooseTactic() || (bat.attList.size() < 2 && !bat.isAttChoose()) || (bat.attList.size() >= 2 && bat.attList.get(1).getCampArmy().getId() != roundInfo.attCampArmy.getId() && !bat.isAttChoose()) || (roundInfo.attCampArmy.playerId <= 0 && WebUtil.nextInt(roundInfo.attCampArmy.getColumn()) == 0)) && bat.attList.get(0).getSpecial() != 1) {
            roundInfo.attTacticInfo = new TacticInfo();
            attExeTactic = true;
            final Tactic tactic = (Tactic)dataGetter.getTacticCache().get((Object)attCamp.getTacicId());
            if (tactic == null) {
                ErrorSceneLog.getInstance().appendErrorMsg("tactic is null.").appendClassName("Builder").appendMethodName("dealTacticStrategy").append("TacicId:", attCamp.getTacicId()).append("player id", attCamp.getPlayerId()).append("player name", attCamp.getPlayerName()).append("general name", attCamp.getGeneralName()).flush();
            }
            if (roundInfo.defCampArmy.specialGeneral.generalType == 5 && WebUtil.nextDouble() < roundInfo.defCampArmy.specialGeneral.param) {
                roundInfo.defRebound = true;
            }
            if (!roundInfo.defRebound) {
                for (final BattleArmy ba : bat.getDefList()) {
                    if (ba.getTD_defense_e() > 0.0) {
                        roundInfo.attTacticInfo.attacked_guanyu = true;
                        break;
                    }
                }
                roundInfo.defFirstRowKilled = exeTactic(dataGetter, 1, tactic, roundInfo.attTacticInfo, attCamp, defCamp, bat, roundInfo.defKilledList, roundInfo, bat.getDefList(), false);
                if (roundInfo.attTacticInfo.firstCReduce > 0) {
                    defLoss = true;
                }
                bat.getDefBaseInfo().setNum(bat.getDefBaseInfo().getNum() - roundInfo.attTacticInfo.allCReduce);
                roundInfo.tacticStrategyResult = 1;
            }
            else {
                roundInfo.attTacticInfo.tacticId = tactic.getId();
                roundInfo.attTacticInfo.tacticDisplayId = tactic.getDisplayId();
                roundInfo.attTacticInfo.tacticNameId = tactic.getPic();
                roundInfo.attTacticInfo.tacticBasicPic = tactic.getBasicPic();
                roundInfo.attTacticInfo.specialType = tactic.getSpecialType();
            }
            roundInfo.attTacticInfo.executed = true;
            final CampArmy campArmy = attCamp;
            --campArmy.tacticVal;
        }
        if (defTacticVal > 0 && defCamp.getTacicId() > 0 && bat.surround <= 0 && (bat.isDefChooseTactic() || (bat.defList.size() < 2 && !bat.isDefChoose()) || (bat.defList.size() >= 2 && bat.defList.get(1).getCampArmy().getId() != roundInfo.defCampArmy.getId() && !bat.isDefChoose()) || roundInfo.defCampArmy.playerId <= 0) && bat.defList.get(0).getSpecial() != 1) {
            defExeTactic = true;
            if (!roundInfo.defFirstRowKilled) {
                roundInfo.defTacticInfo = new TacticInfo();
                final Tactic tactic = (Tactic)dataGetter.getTacticCache().get((Object)defCamp.getTacicId());
                if (tactic == null) {
                    ErrorSceneLog.getInstance().appendErrorMsg("tactic is null.").appendClassName("Builder").appendMethodName("dealTacticStrategy").append("TacicId:", defCamp.getTacicId()).append("player id", defCamp.getPlayerId()).append("player name", defCamp.getPlayerName()).append("general name", defCamp.getGeneralName()).flush();
                }
                if (roundInfo.attCampArmy.specialGeneral.generalType == 5 && WebUtil.nextDouble() < roundInfo.attCampArmy.specialGeneral.param) {
                    roundInfo.attRebound = true;
                }
                if (!roundInfo.attRebound) {
                    for (final BattleArmy ba : bat.getAttList()) {
                        if (ba.getTD_defense_e() > 0.0) {
                            roundInfo.defTacticInfo.attacked_guanyu = true;
                            break;
                        }
                    }
                    roundInfo.attFirstRowKilled = exeTactic(dataGetter, 0, tactic, roundInfo.defTacticInfo, defCamp, attCamp, bat, roundInfo.attKilledList, roundInfo, bat.getAttList(), false);
                    if (roundInfo.defTacticInfo.firstCReduce > 0) {
                        attLoss = true;
                    }
                    bat.getAttBaseInfo().setNum(bat.getAttBaseInfo().getNum() - roundInfo.defTacticInfo.allCReduce);
                    roundInfo.tacticStrategyResult = 2;
                }
                else {
                    roundInfo.defTacticInfo.tacticId = tactic.getId();
                    roundInfo.defTacticInfo.tacticDisplayId = tactic.getDisplayId();
                    roundInfo.defTacticInfo.tacticNameId = tactic.getPic();
                    roundInfo.defTacticInfo.tacticBasicPic = tactic.getBasicPic();
                    roundInfo.defTacticInfo.specialType = tactic.getSpecialType();
                }
                roundInfo.defTacticInfo.executed = true;
                final CampArmy campArmy2 = defCamp;
                --campArmy2.tacticVal;
            }
            else {
                final Tactic tactic = (Tactic)dataGetter.getTacticCache().get((Object)defCamp.getTacicId());
                roundInfo.defTacticInfo = new TacticInfo();
                roundInfo.defTacticInfo.tacticId = tactic.getId();
                roundInfo.defTacticInfo.tacticDisplayId = tactic.getDisplayId();
                roundInfo.defTacticInfo.tacticNameId = tactic.getPic();
                roundInfo.defTacticInfo.tacticBasicPic = tactic.getBasicPic();
            }
        }
        if (roundInfo.attRebound) {
            roundInfo.attTacticInfo = new TacticInfo();
            final Tactic tactic = (Tactic)dataGetter.getTacticCache().get((Object)defCamp.getTacicId());
            roundInfo.nextMinExeTime += tactic.getPlayertime() + 3000;
            for (final BattleArmy ba : bat.getDefList()) {
                if (ba.getTD_defense_e() > 0.0) {
                    roundInfo.attTacticInfo.attacked_guanyu = true;
                    break;
                }
            }
            roundInfo.defFirstRowKilled = exeTactic(dataGetter, 1, tactic, roundInfo.attTacticInfo, attCamp, defCamp, bat, roundInfo.defKilledList, roundInfo, bat.getDefList(), true);
            if (roundInfo.attTacticInfo.firstCReduce > 0) {
                defLoss = true;
            }
            bat.getDefBaseInfo().setNum(bat.getDefBaseInfo().getNum() - roundInfo.attTacticInfo.allCReduce);
            roundInfo.attTacticInfo.executed = true;
            roundInfo.tacticStrategyResult = 1;
        }
        if (roundInfo.defRebound) {
            roundInfo.defTacticInfo = new TacticInfo();
            final Tactic tactic = (Tactic)dataGetter.getTacticCache().get((Object)attCamp.getTacicId());
            roundInfo.nextMinExeTime += tactic.getPlayertime() + 3000;
            for (final BattleArmy ba : bat.getAttList()) {
                if (ba.getTD_defense_e() > 0.0) {
                    roundInfo.defTacticInfo.attacked_guanyu = true;
                    break;
                }
            }
            roundInfo.attFirstRowKilled = exeTactic(dataGetter, 0, tactic, roundInfo.defTacticInfo, defCamp, attCamp, bat, roundInfo.attKilledList, roundInfo, bat.getAttList(), true);
            if (roundInfo.defTacticInfo.firstCReduce > 0) {
                attLoss = true;
            }
            bat.getAttBaseInfo().setNum(bat.getAttBaseInfo().getNum() - roundInfo.defTacticInfo.allCReduce);
            roundInfo.defTacticInfo.executed = true;
            roundInfo.tacticStrategyResult = 2;
        }
        if (!attExeTactic && !defExeTactic) {
            this.dealStrategy(dataGetter, bat, roundInfo);
            if (roundInfo.attStrategyLost > 0) {
                attLoss = true;
            }
            if (roundInfo.defStrategyLost > 0) {
                defLoss = true;
            }
        }
        bat.defBaseInfo.foreWin = true;
        if (roundInfo.defFirstRowKilled) {
            bat.defBaseInfo.foreWin = false;
        }
        bat.attBaseInfo.foreWin = true;
        if (roundInfo.attFirstRowKilled) {
            bat.attBaseInfo.foreWin = false;
        }
        exeFight = (!roundInfo.attFirstRowKilled && !roundInfo.defFirstRowKilled);
        if (roundInfo.attFirstRowKilled && roundInfo.defFirstRowKilled) {
            roundInfo.win = 3;
        }
        getReportType14(dataGetter, bat, roundInfo);
        getReportType30(roundInfo.battleMsg, roundInfo);
        if (!exeFight) {
            getReportType31(roundInfo.battleMsg, roundInfo);
        }
        if (!roundInfo.attKilledList.isEmpty()) {
            if (bat.attList.size() <= 1) {
                roundInfo.nextAttBattleArmy = null;
            }
            else {
                boolean find = false;
                for (int i = 1; i < bat.attList.size(); ++i) {
                    find = false;
                    for (final BattleArmy ba2 : roundInfo.attKilledList) {
                        if (bat.attList.get(i).getPosition() == ba2.getPosition()) {
                            find = true;
                            break;
                        }
                    }
                    if (!find && i > 0) {
                        roundInfo.nextAttBattleArmy = bat.attList.get(i);
                        break;
                    }
                }
                if (find) {
                    roundInfo.nextAttBattleArmy = null;
                }
            }
        }
        if (!roundInfo.defKilledList.isEmpty()) {
            if (bat.defList.size() <= 1) {
                roundInfo.nextDefBattleArmy = null;
            }
            else {
                boolean find = false;
                for (int i = 1; i < bat.defList.size(); ++i) {
                    find = false;
                    for (final BattleArmy ba2 : roundInfo.defKilledList) {
                        if (bat.defList.get(i).getPosition() == ba2.getPosition()) {
                            find = true;
                            break;
                        }
                    }
                    if (!find) {
                        roundInfo.nextDefBattleArmy = bat.defList.get(i);
                        break;
                    }
                }
                if (find) {
                    roundInfo.nextDefBattleArmy = null;
                }
            }
        }
        if (attLoss) {
            if (roundInfo.killAttG && roundInfo.nextAttBattleArmy != null) {
                if (bat.attList.size() > 1 && bat.defList.size() > 1) {
                    bat.setBattleRoundBuff(dataGetter, bat.attList.get(1).getCampArmy(), bat.defList.get(1).getCampArmy());
                }
                getReportType16(dataGetter, bat, roundInfo.battleMsg, roundInfo.nextAttBattleArmy.getCampArmy(), "att", true, false);
            }
            else {
                getReportType16(dataGetter, bat, roundInfo.battleMsg, roundInfo.attCampArmy, "att", false, false);
            }
        }
        if (defLoss) {
            if (roundInfo.killDefG && roundInfo.nextDefBattleArmy != null) {
                if (bat.attList.size() > 1 && bat.defList.size() > 1) {
                    bat.setBattleRoundBuff(dataGetter, bat.attList.get(1).getCampArmy(), bat.defList.get(1).getCampArmy());
                }
                getReportType16(dataGetter, bat, roundInfo.battleMsg, roundInfo.nextDefBattleArmy.getCampArmy(), "def", true, false);
            }
            else {
                getReportType16(dataGetter, bat, roundInfo.battleMsg, roundInfo.defCampArmy, "def", false, false);
            }
        }
        getReportType20(bat, roundInfo.battleMsg, roundInfo);
        return exeFight;
    }
    
    public void dealStrategy(final IDataGetter dataGetter, final Battle bat, final RoundInfo roundInfo) {
        final int attSt = roundInfo.attBattleArmy.getStrategy();
        final int defSt = roundInfo.defBattleArmy.getStrategy();
        final FightStragtegyCoe fsc = dataGetter.getFightStragtegyCoeCache().getFightStragtegyCoe(defSt, attSt);
        roundInfo.tacticStrategyResult = fsc.getWinerSide();
        boolean lostBlood = false;
        final String debug = Configuration.getProperty("gcld.battle.caculate.debug");
        if (debug.equals("1")) {
            roundInfo.caculateDebugBuffer.append("Strategy.").append(" | ").append("attSt:").append(" | ").append(attSt).append(" | ").append("defSt:").append(" | ").append(defSt).append("\n\t");
            roundInfo.caculateDebugBuffer.append("FightStragtegyCoe").append(" | ").append("Id:").append(" | ").append(fsc.getId()).append(" | ").append("Att:").append(" | ").append(fsc.getAttStrategy()).append(" | ").append("Def:").append(" | ").append(fsc.getDefStrategy()).append(" | ").append("Win:").append(" | ").append(fsc.getWinerSide()).append(" | ").append("AttLost:").append(" | ").append(fsc.getAttLost()).append(" | ").append("DefLost:").append(" | ").append(fsc.getDefLost()).append("\n\t");
        }
        final int attHp = roundInfo.attBattleArmy.getTroopHp()[0];
        final int defHp = roundInfo.defBattleArmy.getTroopHp()[0];
        if (fsc.getDefLost() > 0.0) {
            final FightStrategies fs = (FightStrategies)dataGetter.getFightStrategiesCache().get((Object)attSt);
            int reduceNum = (int)(attHp * fsc.getDefLost());
            if (fsc.getWinerSide() == 1) {
                reduceNum += fs.getBaseDamage();
            }
            if (reduceNum < 10) {
                reduceNum = 10;
            }
            if (reduceNum >= roundInfo.defBattleArmy.getTroopHp()[0]) {
                reduceNum = roundInfo.defBattleArmy.getTroopHp()[0];
                roundInfo.defFirstRowKilled = true;
                roundInfo.defKilledList.add(roundInfo.defBattleArmy);
                roundInfo.win = 1;
            }
            for (int i = 0; i < 3; ++i) {
                roundInfo.defBattleArmy.getTroopHp()[i] -= reduceNum;
            }
            roundInfo.defStrategyLost += reduceNum * 3;
            if (debug.equals("1")) {
                roundInfo.caculateDebugBuffer.append("Att ST Base:").append(" | ").append(fs.getBaseDamage()).append(" | ").append("AttTroopHp:").append(roundInfo.attBattleArmy.getTroopHp()[0]).append(" | ").append("Def Lost:").append(" | ").append(reduceNum).append(" | ").append("Def Lost All:").append(" | ").append(roundInfo.defStrategyLost).append("\n\t");
            }
            bat.getDefBaseInfo().setNum(bat.getDefBaseInfo().getNum() - roundInfo.defStrategyLost);
            lostBlood = true;
            final CampArmy defCampArmy = roundInfo.defCampArmy;
            defCampArmy.armyHpLoss += roundInfo.defStrategyLost;
            final PlayerInfo piDef = bat.inBattlePlayers.get(roundInfo.defBattleArmy.getCampArmy().getPlayerId());
            if (piDef != null) {
                final PlayerInfo playerInfo = piDef;
                playerInfo.lostTotal += roundInfo.defStrategyLost;
            }
            final CampArmy attCampArmy = roundInfo.attCampArmy;
            attCampArmy.armyHpKill += roundInfo.defStrategyLost;
            if (roundInfo.defCampArmy.isBarPhantom) {
                final CampArmy attCampArmy2 = roundInfo.attCampArmy;
                attCampArmy2.barbarainHpKill += roundInfo.defStrategyLost;
            }
            final PlayerInfo piAtt = bat.inBattlePlayers.get(roundInfo.attBattleArmy.getCampArmy().getPlayerId());
            if (piAtt != null) {
                final PlayerInfo playerInfo2 = piAtt;
                playerInfo2.killTotal += roundInfo.defStrategyLost;
            }
        }
        if (fsc.getAttLost() > 0.0) {
            final FightStrategies fs = (FightStrategies)dataGetter.getFightStrategiesCache().get((Object)defSt);
            int reduceNum = (int)(defHp * fsc.getAttLost());
            if (fsc.getWinerSide() == 2) {
                reduceNum += fs.getBaseDamage();
            }
            if (reduceNum < 10) {
                reduceNum = 10;
            }
            if (reduceNum >= roundInfo.attBattleArmy.getTroopHp()[0]) {
                reduceNum = roundInfo.attBattleArmy.getTroopHp()[0];
                roundInfo.attFirstRowKilled = true;
                roundInfo.attKilledList.add(roundInfo.attBattleArmy);
                roundInfo.win = 2;
            }
            for (int i = 0; i < 3; ++i) {
                roundInfo.attBattleArmy.getTroopHp()[i] -= reduceNum;
            }
            roundInfo.attStrategyLost += reduceNum * 3;
            if (debug.equals("1")) {
                roundInfo.caculateDebugBuffer.append("Def ST Base:").append(" | ").append(fs.getBaseDamage()).append(" | ").append("DefTroopHp:").append(roundInfo.defBattleArmy.getTroopHp()[0]).append(" | ").append("Att Lost:").append(" | ").append(reduceNum).append(" | ").append("Att Lost All:").append(" | ").append(roundInfo.attStrategyLost).append("\n\t");
            }
            bat.getAttBaseInfo().setNum(bat.getAttBaseInfo().getNum() - roundInfo.attStrategyLost);
            lostBlood = true;
            final CampArmy attCampArmy3 = roundInfo.attCampArmy;
            attCampArmy3.armyHpLoss += roundInfo.attStrategyLost;
            final PlayerInfo piAtt2 = bat.inBattlePlayers.get(roundInfo.attBattleArmy.getCampArmy().getPlayerId());
            if (piAtt2 != null) {
                final PlayerInfo playerInfo3 = piAtt2;
                playerInfo3.lostTotal += roundInfo.attStrategyLost;
            }
            final CampArmy defCampArmy2 = roundInfo.defCampArmy;
            defCampArmy2.armyHpKill += roundInfo.attStrategyLost;
            if (roundInfo.attCampArmy.isBarPhantom) {
                final CampArmy defCampArmy3 = roundInfo.defCampArmy;
                defCampArmy3.barbarainHpKill += roundInfo.attStrategyLost;
            }
            final PlayerInfo piDef2 = bat.inBattlePlayers.get(roundInfo.defBattleArmy.getCampArmy().getPlayerId());
            if (piDef2 != null) {
                final PlayerInfo playerInfo4 = piDef2;
                playerInfo4.killTotal += roundInfo.attStrategyLost;
            }
        }
        if (lostBlood) {
            roundInfo.nextMinExeTime += 1500;
            roundInfo.timePredicationBuffer.append("strategy reduce:").append(1500).append("|");
        }
        if (roundInfo.attCampArmy.armyHpLoss >= roundInfo.attCampArmy.armyHpOrg) {
            final CampArmy defCampArmy4 = roundInfo.defCampArmy;
            ++defCampArmy4.killGeneral;
            roundInfo.needPushReport13 = true;
            roundInfo.nextMinExeTime += 0;
            roundInfo.timePredicationBuffer.append("strategy att cheers:").append(0).append("|");
            roundInfo.killAttG = true;
            if (bat.attList.size() > 0 && bat.defList.size() > 0) {
                bat.setBattleRoundBuff(dataGetter, bat.attList.get(0).getCampArmy(), bat.defList.get(0).getCampArmy());
            }
            roundInfo.attCampArmy.armyHp = -1;
            bat.attCamp.remove(roundInfo.attCampArmy);
            BattleSceneLog.getInstance().info("#batId:" + bat.getBattleId() + "_" + bat.getStartTime() + "#quit:strategy#side:att" + "#playerId:" + roundInfo.attCampArmy.getPlayerId() + ":" + roundInfo.attCampArmy.isPhantom + "#general:" + roundInfo.attCampArmy.getGeneralId() + "#attSize:" + bat.attCamp.size());
            this.quitNewBattle(roundInfo.attCampArmy, 1, bat);
            if (roundInfo.defCampArmy.getPlayerId() > 0 && !roundInfo.defCampArmy.isPhantom) {
                final PlayerInfo pi = bat.inBattlePlayers.get(roundInfo.defCampArmy.getPlayerId());
                if (pi != null && roundInfo.defCampArmy.killGeneral > pi.maxKillG) {
                    pi.maxKillG = roundInfo.defCampArmy.killGeneral;
                }
            }
        }
        if (roundInfo.defCampArmy.armyHpLoss >= roundInfo.defCampArmy.armyHpOrg) {
            final CampArmy attCampArmy4 = roundInfo.attCampArmy;
            ++attCampArmy4.killGeneral;
            roundInfo.needPushReport13 = true;
            roundInfo.nextMinExeTime += 0;
            roundInfo.timePredicationBuffer.append("strategy def cheers:").append(0).append("|");
            roundInfo.killDefG = true;
            if (bat.attList.size() > 0 && bat.defList.size() > 0) {
                bat.setBattleRoundBuff(dataGetter, bat.attList.get(0).getCampArmy(), bat.defList.get(0).getCampArmy());
            }
            roundInfo.defCampArmy.armyHp = -1;
            bat.defCamp.remove(roundInfo.defCampArmy);
            BattleSceneLog.getInstance().info("#batId:" + bat.getBattleId() + "_" + bat.getStartTime() + "#quit:strategy#side:def" + "#playerId:" + roundInfo.defCampArmy.getPlayerId() + ":" + roundInfo.defCampArmy.isPhantom + "#general:" + roundInfo.defCampArmy.getGeneralId() + "#defSize:" + bat.defCamp.size());
            this.quitNewBattle(roundInfo.defCampArmy, 0, bat);
            if (roundInfo.attCampArmy.getPlayerId() > 0 && !roundInfo.attCampArmy.isPhantom) {
                final PlayerInfo pi = bat.inBattlePlayers.get(roundInfo.attCampArmy.getPlayerId());
                if (pi != null && roundInfo.attCampArmy.killGeneral > pi.maxKillG) {
                    pi.maxKillG = roundInfo.attCampArmy.killGeneral;
                }
            }
        }
    }
    
    public static void getStrategyInfo(final StringBuilder battleMsg, final Battle bat, final IDataGetter dataGetter) {
        if (bat.getAttList().size() > 0 && bat.getDefList().size() > 0) {
            final BattleArmy attBattleArmy = bat.getAttList().get(0);
            final BattleArmy defBattleArmy = bat.getDefList().get(0);
            final CampArmy attCampArmy = attBattleArmy.getCampArmy();
            final CampArmy defCampArmy = defBattleArmy.getCampArmy();
            final PlayerInfo piAtt = bat.inBattlePlayers.get(attCampArmy.playerId);
            final boolean attNeedPush = attCampArmy.playerId > 0 && !attCampArmy.isPhantom && piAtt != null && piAtt.autoStrategy != 1;
            final PlayerInfo piDef = bat.inBattlePlayers.get(defCampArmy.playerId);
            final boolean defNeedPush = defCampArmy.playerId > 0 && !defCampArmy.isPhantom && piDef != null && piDef.autoStrategy != 1;
            if (attNeedPush) {
                int canChooseTac = 0;
                if (attBattleArmy.getSpecial() > 0) {
                    canChooseTac = 1;
                }
                else if (defCampArmy.getSpecialGeneral().generalType == 8) {
                    canChooseTac = 3;
                }
                final Troop attTroop = (Troop)dataGetter.getTroopCache().get((Object)attCampArmy.getTroopId());
                final int[] attSts = attTroop.getStrategyMap().get(bat.getTerrainVal());
                battleMsg.append(27).append("|").append("att").append("|").append(attCampArmy.playerId).append("|").append(attBattleArmy.getPosition()).append("|").append(attCampArmy.playerName).append("|").append(-1).append("|").append(1).append(";");
                final FightStragtegyCoe fsc = dataGetter.getFightStragtegyCoeCache().getAttWin(defBattleArmy.getStrategy(), attSts);
                battleMsg.append((fsc == null) ? attSts[0] : fsc.getAttStrategy()).append("|").append(attSts[0]).append("|").append(((FightStrategies)dataGetter.getFightStrategiesCache().get((Object)attSts[0])).getName()).append("|").append(attSts[1]).append("|").append(((FightStrategies)dataGetter.getFightStrategiesCache().get((Object)attSts[1])).getName()).append("|").append(attSts[2]).append("|").append(((FightStrategies)dataGetter.getFightStrategiesCache().get((Object)attSts[2])).getName()).append(";");
                battleMsg.append(attCampArmy.getId()).append("|").append(attCampArmy.getTacticVal()).append("|").append(attCampArmy.getGeneralPic()).append("|").append(canChooseTac).append("|").append(attBattleArmy.getSpecial()).append("#");
            }
            else {
                battleMsg.append(27).append("|").append("att").append("|").append(attCampArmy.playerId).append("|").append(attBattleArmy.getPosition()).append("|").append(attCampArmy.playerName).append("|").append(-1).append("|").append(0).append(";");
                battleMsg.append("null").append(";");
                battleMsg.append("null").append("#");
            }
            if (defNeedPush) {
                int canChooseTac = 0;
                if (defBattleArmy.getSpecial() > 0) {
                    canChooseTac = 1;
                }
                else if (attCampArmy.getSpecialGeneral().generalType == 8) {
                    canChooseTac = 3;
                }
                else if (bat.surround > 0) {
                    canChooseTac = 2;
                }
                final Troop defTroop = (Troop)dataGetter.getTroopCache().get((Object)defCampArmy.getTroopId());
                final int[] defSts = defTroop.getStrategyDefMap().get(bat.getTerrainVal());
                battleMsg.append(27).append("|").append("def").append("|").append(defCampArmy.playerId).append("|").append(defBattleArmy.getPosition()).append("|").append(defCampArmy.playerName).append("|").append(-1).append("|").append(1).append(";");
                battleMsg.append(defSts[0]).append("|").append(defSts[0]).append("|").append(((FightStrategies)dataGetter.getFightStrategiesCache().get((Object)defSts[0])).getName()).append("|").append(defSts[1]).append("|").append(((FightStrategies)dataGetter.getFightStrategiesCache().get((Object)defSts[1])).getName()).append("|").append(defSts[2]).append("|").append(((FightStrategies)dataGetter.getFightStrategiesCache().get((Object)defSts[2])).getName()).append(";");
                battleMsg.append(defCampArmy.getId()).append("|").append(defCampArmy.getTacticVal()).append("|").append(defCampArmy.getGeneralPic()).append("|").append(canChooseTac).append("|").append(defBattleArmy.getSpecial()).append("#");
            }
            else {
                battleMsg.append(27).append("|").append("def").append("|").append(defCampArmy.playerId).append("|").append(defBattleArmy.getPosition()).append("|").append(defCampArmy.playerName).append("|").append(-1).append("|").append(0).append(";");
                battleMsg.append("null").append(";");
                battleMsg.append("null").append("#");
            }
        }
    }
    
    public static void clearBattleAfterError(final IDataGetter dataGetter, final int playerId, final Battle bat) {
        if (bat == null) {
            return;
        }
        final Builder builder = BuilderFactory.getInstance().getBuilder(bat.getBattleType());
        for (final CampArmy ca : bat.getAttCamp()) {
            if (ca.isUpdateDB() && ca.isInBattle() && ca.getArmyHp() >= 0 && !ca.isPhantom) {
                final int state = ca.isInRecruit() ? 1 : 0;
                dataGetter.getPlayerGeneralMilitaryDao().restartRecruit(ca.getPlayerId(), ca.getGeneralId(), state, new Date());
                BattleSceneLog.getInstance().append("playerId", ca.playerId).append("playerName", ca.playerName).append("gId", ca.generalId).append("gName", ca.generalName).append("state", state).flush();
                if (bat.getBattleType() == 3) {
                    bat.worldSceneLog.Indent().append("playerId", ca.playerId).append("playerName", ca.playerName).append("gId", ca.generalId).append("gName", ca.generalName).append("state", state).newLine();
                }
                dataGetter.getGeneralService().sendGmUpdate(ca.getPlayerId(), ca.getGeneralId(), false);
            }
        }
        if (bat.getBattleType() == 3 && !bat.isNpc) {
            BattleSceneLog.getInstance().appendLogMsg("battleStart exception. reset def pgm state").appendClassName("Builder").appendMethodName("clearBattleAfterError").flush();
            bat.worldSceneLog.appendLogMsg("battleStart exception. reset def pgm state").appendClassName("Builder").appendMethodName("clearBattleAfterError").newLine();
            for (final CampArmy ca : bat.getDefCamp()) {
                if (ca.isUpdateDB() && ca.isInBattle() && ca.getArmyHp() >= 0 && !ca.isPhantom) {
                    final int state = ca.isInRecruit() ? 1 : 0;
                    dataGetter.getPlayerGeneralMilitaryDao().restartRecruit(ca.getPlayerId(), ca.getGeneralId(), state, new Date());
                    BattleSceneLog.getInstance().append("playerId", ca.playerId).append("playerName", ca.playerName).append("gId", ca.generalId).append("gName", ca.generalName).append("state", state).flush();
                    bat.worldSceneLog.Indent().append("playerId", ca.playerId).append("playerName", ca.playerName).append("gId", ca.generalId).append("gName", ca.generalName).append("state", state).newLine();
                    dataGetter.getGeneralService().sendGmUpdate(ca.getPlayerId(), ca.getGeneralId(), false);
                }
            }
        }
        final Map<Integer, PlayerInfo> pisMap = bat.getInBattlePlayers();
        for (final PlayerInfo pis : pisMap.values()) {
            NewBattleManager.getInstance().quitBattle(pis.getPlayerId(), bat.getBattleId());
            builder.inBattleInfo(pis.getPlayerId(), false);
        }
        NewBattleManager.getInstance().deleteBattle(bat.getBattleId());
    }
    
    public static void getReportType28(final StringBuilder battleMsg, final LinkedList<CampArmy> attCamps, final LinkedList<CampArmy> defCamps) {
        final Set<Integer> set = new HashSet<Integer>();
        for (final CampArmy ca : attCamps) {
            int[] strategies;
            for (int length = (strategies = ca.getStrategies()).length, i = 0; i < length; ++i) {
                final Integer key = strategies[i];
                set.add(key);
            }
        }
        for (final CampArmy ca : defCamps) {
            int[] strategies2;
            for (int length2 = (strategies2 = ca.getStrategies()).length, j = 0; j < length2; ++j) {
                final Integer key = strategies2[j];
                set.add(key);
            }
        }
        if (set.size() > 0) {
            battleMsg.append(28).append("|").append(28).append(";");
            for (final Integer key2 : set) {
                battleMsg.append(key2).append("|").append(FightStrategiesCache.getStrategyPic(key2)).append(";");
            }
            battleMsg.replace(battleMsg.length() - 1, battleMsg.length(), "#");
        }
    }
    
    public void saveTimePredicationBuffer(final RoundInfo roundInfo) {
        final String saveReport = Configuration.getProperty("gcld.battle.report.save");
        if (saveReport.equals("1")) {
            BattleSceneLog.getInstance().debug(roundInfo.timePredicationBuffer);
        }
    }
    
    public void saveCaculateDebugBuffer(final RoundInfo roundInfo) {
        final String debug = Configuration.getProperty("gcld.battle.caculate.debug");
        if (debug.equals("1")) {
            BattleSceneLog.getInstance().debug("\n\t" + roundInfo.caculateDebugBuffer);
        }
    }
    
    public void quitNewBattle(final CampArmy ca, final int battleSide, final Battle bat) {
        if (ca.getPlayerId() > 0 && !ca.isPhantom) {
            NewBattleManager.getInstance().quitBattle(bat, ca.getPlayerId(), ca.getGeneralId());
        }
    }
    
    public void endQuitNewBattle(final CampArmy ca, final int battleSide, final Battle bat) {
        if (ca.getPlayerId() > 0 && !ca.isPhantom) {
            NewBattleManager.getInstance().quitBattle(bat, ca.getPlayerId(), ca.getGeneralId());
        }
    }
    
    public void roundRemoveCamp(final Battle battle) {
        final Set<CampArmy> attRemoveSet = new HashSet<CampArmy>();
        for (final CampArmy attCa : battle.attCamp) {
            if (attCa.armyHpLoss >= attCa.armyHpOrg) {
                attRemoveSet.add(attCa);
            }
            else {
                if (attCa.armyHp == attCa.armyHpOrg) {
                    break;
                }
                continue;
            }
        }
        for (final CampArmy attCa : attRemoveSet) {
            battle.attCamp.remove(attCa);
            this.quitNewBattle(attCa, 1, battle);
            BattleSceneLog.getInstance().info("#batId:" + battle.getBattleId() + "_" + battle.getStartTime() + "#quit:tactic#side:att" + "#playerId:" + attCa.getPlayerId() + ":" + attCa.isPhantom + "#general:" + attCa.getGeneralId() + "#attSize:" + battle.attCamp.size());
        }
        final Set<CampArmy> defRemoveSet = new HashSet<CampArmy>();
        for (final CampArmy defCa : battle.defCamp) {
            if (defCa.armyHpLoss >= defCa.armyHpOrg) {
                defRemoveSet.add(defCa);
            }
            else {
                if (defCa.armyHp == defCa.armyHpOrg) {
                    break;
                }
                continue;
            }
        }
        for (final CampArmy defCa : defRemoveSet) {
            battle.defCamp.remove(defCa);
            this.quitNewBattle(defCa, 0, battle);
            BattleSceneLog.getInstance().info("#batId:" + battle.getBattleId() + "_" + battle.getStartTime() + "#quit:tactic#side:def" + "#playerId:" + defCa.getPlayerId() + ":" + defCa.isPhantom + "#general:" + defCa.getGeneralId() + "#defSize:" + battle.defCamp.size());
        }
    }
    
    public static void getCampArmyLost(final IDataGetter dataGetter, final StringBuilder sbLost, final Battle battle, final int armyId, final int serialNo) {
        for (final CampArmy campArmy : battle.defCamp) {
            if (campArmy.getGeneralId() == armyId && campArmy.getId() == serialNo) {
                final int hpMax = ((Army)dataGetter.getArmyCache().get((Object)armyId)).getArmyHp();
                sbLost.append(hpMax - (campArmy.getArmyHpOrg() - campArmy.getArmyHpLoss()));
                sbLost.append(";");
                return;
            }
        }
        final int hpMax2 = ((Army)dataGetter.getArmyCache().get((Object)armyId)).getArmyHp();
        sbLost.append(hpMax2);
        sbLost.append(";");
    }
    
    public void setSurroundState(final IDataGetter dataGetter, final Battle bat) {
    }
    
    public void beginOneToOneBattle(final IDataGetter dataGetter, final Battle one2Onebattle, final Battle mainBattle, final CampArmy attCa, final CampArmy defCa) {
    }
    
    public CampArmy copyArmyformBarPhantom(final IDataGetter dataGetter, final Barbarain barbarain, final Battle bat, final BarbarainPhantom barPhantom, final int batSide) {
        return null;
    }
    
    public CampArmy copyArmyFromNationTaskExpeditionArmy(final IDataGetter dataGetter, final EfLv efLv, final Battle bat, final NationTaskExpeditionArmy nationTaskExpeditionArmy, final int batSide) {
        return null;
    }
    
    public CampArmy copyArmyFromActivityNpc(final IDataGetter dataGetter, final Object table, final Battle bat, final ActivityNpc activityNpc, final int battleSide) {
        return null;
    }
    
    public void initAttCampForKf(final IDataGetter dataGetter, final Player player, final List<PlayerGeneralMilitary> pgmList, final int defId, final Battle bat) {
        int attNum = 0;
        for (int i = 0; i < pgmList.size(); ++i) {
            final PlayerGeneralMilitary pgm = pgmList.get(i);
            final CampArmy campArmy = this.copyArmyFromPlayerTableForKfwd(player, pgm, dataGetter, this.getGeneralState(), bat, 1);
            if (campArmy != null) {
                attNum += campArmy.getArmyHpOrg();
                bat.attCamp.add(campArmy);
            }
        }
        bat.attBaseInfo.setNum(bat.attBaseInfo.getNum() + attNum);
        bat.attBaseInfo.setAllNum(bat.attBaseInfo.getAllNum() + attNum);
        bat.attBaseInfo.setForceId(player.getForceId());
    }
    
    public CampArmy copyArmyformExpeditionArmy(final IDataGetter dataGetter, final EfLv eflv, final Battle bat, final ExpeditionArmy expeditionArmy, final int batSide) {
        return null;
    }
    
    public CampArmy copyArmyformBarExpeditionArmy(final IDataGetter dataGetter, final WorldPaidB WorldPaidB, final Battle bat, final BarbarainExpeditionArmy expeditionArmy, final int batSide) {
        return null;
    }
    
    public void updateChooseTime(final Battle bat, final RoundInfo roundInfo, final boolean attAutoStChoosed, final boolean defAutoStChoosed) {
        if ((roundInfo.win != 1 && bat.attList.size() > 0 && bat.attList.get(0).getCampArmy().getPlayerId() > 0 && !attAutoStChoosed && !bat.attList.get(0).getCampArmy().isPhantom && Players.getSession(Integer.valueOf(bat.attList.get(0).getCampArmy().getPlayerId())) != null) || (roundInfo.win != 2 && bat.defList.size() > 0 && bat.defList.get(0).getCampArmy().getPlayerId() > 0 && !defAutoStChoosed && !bat.defList.get(0).getCampArmy().isPhantom && Players.getSession(Integer.valueOf(bat.defList.get(0).getCampArmy().getPlayerId())) != null)) {
            roundInfo.nextMaxExeTime += 6000;
            roundInfo.timePredicationBuffer.append("st choose:").append(6000).append("|");
        }
    }
    
    public CampArmy copyArmyFromScenarioNpc(final IDataGetter dataGetter, final Player player, final Battle battle, final ScenarioNpc scenarioNpc, final int battleSide) {
        return null;
    }
    
    public CampArmy copyArmyFromPgmIgnoreState(final IDataGetter dataGetter, final Player player, final PlayerGeneralMilitary pgm, final int hp, final int gState, final Battle bat, final int batSide) {
        final CampArmy campArmy = new CampArmy();
        final General general = (General)dataGetter.getGeneralCache().get((Object)pgm.getGeneralId());
        final SpecialGeneral sg = new SpecialGeneral(general.getGeneralSpecialInfo().generalType, general.getGeneralSpecialInfo().param, general.getGeneralSpecialInfo().param2);
        campArmy.setSpecialGeneral(sg);
        final Troop troop = dataGetter.getTroopCache().getTroop(general.getTroop(), player.getPlayerId());
        campArmy.updateDB = true;
        campArmy.setPlayerLv(player.getPlayerLv());
        campArmy.setPlayerId(player.getPlayerId());
        campArmy.setPlayerName(player.getPlayerName());
        campArmy.setForceId(pgm.getForceId());
        campArmy.setActivityAddExp(dataGetter.getActivityService().getAddBatValue(player.getPlayerId(), player.getPlayerLv()));
        getTerrainValue(bat.terrainVal, troop, batSide, campArmy);
        campArmy.setTroopId(troop.getId());
        campArmy.setTroopSerial(troop.getSerial());
        campArmy.setTroopType(troop.getType());
        campArmy.setTroopName(troop.getName());
        campArmy.setGeneralId(pgm.getGeneralId());
        campArmy.setGeneralLv(pgm.getLv());
        campArmy.setPgmVId(pgm.getVId());
        campArmy.setStrength(pgm.getStrength(general.getStrength()));
        campArmy.setLeader(pgm.getLeader(general.getLeader()));
        campArmy.setArmyName(general.getName());
        campArmy.setGeneralName(general.getName());
        campArmy.setGeneralPic(general.getPic());
        campArmy.setQuality(general.getQuality());
        campArmy.setTacicId(general.getTacticId());
        campArmy.setId(bat.campNum.getAndIncrement());
        campArmy.setArmyHp(hp);
        campArmy.setArmyHpOrg(hp);
        getAttDefHp(dataGetter, campArmy);
        final PlayerBattleAttribute pba = dataGetter.getPlayerBattleAttributeDao().read(player.getPlayerId());
        if (pba != null && pba.getSupportTime() != null && pba.getSupportTime().getTime() > System.currentTimeMillis()) {
            if (pba.getType() == 1) {
                campArmy.setRewardDoubleType(1);
                campArmy.setRewardDouble(1.5);
            }
            else if (pba.getType() == 2) {
                campArmy.setRewardDoubleType(2);
                campArmy.setRewardDouble(2.0);
            }
        }
        if (general.getTacticId() > 0) {
            campArmy.setTacticVal(1);
            if (campArmy.getSpecialGeneral().generalType == 7) {
                campArmy.setTacticVal((int)campArmy.getSpecialGeneral().param);
            }
        }
        if (batSide == 1) {
            campArmy.setStrategies(troop.getStrategyMap().get(bat.terrainVal));
        }
        else {
            campArmy.setStrategies(troop.getStrategyDefMap().get(bat.terrainVal));
        }
        return campArmy;
    }
    
    public void addViewType(final Player player, final Battle battle, final JsonDocument doc) {
    }
    
    public CampArmy copyArmyformBarPhantom2(final IDataGetter dataGetter, final KtSdmzS ktSdmzS, final Battle battle, final BarbarainPhantom barPhantom, final int battleSide) {
        return null;
    }
    
    public void setAttDefBaseInfo(final IDataGetter dataGetter, final Battle battle, final BattleAttacker battleAttacker, final int battleType, final int defId) {
        battle.attBaseInfo.setId(battleAttacker.attPlayerId);
        battle.defBaseInfo.setId(defId);
    }
    
    public void checkErrorAndHandle(final IDataGetter dataGetter, final Battle battle) {
    }
    
    public CampArmy copyArmyformBarPhantom3(final IDataGetter dataGetter, final Barbarain barbarain, final Battle battle, final BarbarainPhantom barPhantom, final int battleSide) {
        return null;
    }
    
    public CampArmy copyArmyformBarPhantom4(final IDataGetter dataGetter, final Battle battle, final BarbarainPhantom barPhantom, final int battleSide) {
        return null;
    }
    
    public CampArmy copyArmyfromBarPhantom4(final IDataGetter dataGetter, final Battle battle, final BarbarainPhantom barPhantom, final int battleSide) {
        return null;
    }
    
    public void initZhengZhaoLingJoinPlayer(final IDataGetter dataGetter, final Battle battle, final Player player, final int battleSide, final List<PlayerGeneralMilitary> pgmList, final BaseInfo baseInfo, final LinkedList<CampArmy> campMap, final LinkedList<CampArmy> campChange, final double teamEffect, final int attEffect, final int defEffect, final String playerName) {
    }
    
    public CampArmy copyArmyfromNationTaskYellowTurbans(final IDataGetter dataGetter, final Battle battle, final YellowTurbans yellowTurbans, final int battleSide) {
        return null;
    }
    
    public CampArmy copyArmyfromHuizhanPkRewardNpc(final IDataGetter dataGetter, final Battle battle, final int armyId, final int battleSide, final int forceId) {
        return null;
    }
}
