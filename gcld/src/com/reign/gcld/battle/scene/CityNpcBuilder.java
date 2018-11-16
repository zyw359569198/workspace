package com.reign.gcld.battle.scene;

import com.reign.gcld.battle.service.*;
import com.reign.gcld.general.domain.*;
import com.reign.util.*;
import com.reign.gcld.juben.service.*;
import com.reign.gcld.player.domain.*;
import com.reign.gcld.juben.common.*;
import com.reign.framework.json.*;
import com.reign.gcld.world.common.*;
import com.reign.gcld.world.domain.*;
import com.reign.gcld.battle.domain.*;
import com.reign.gcld.player.dto.*;
import com.reign.gcld.sdata.common.*;
import com.reign.gcld.world.service.*;
import com.reign.gcld.team.service.*;
import com.reign.gcld.general.dto.*;
import com.reign.gcld.battle.reward.*;
import com.reign.gcld.common.*;
import com.reign.gcld.task.message.*;
import com.reign.gcld.sdata.domain.*;
import java.util.*;
import com.reign.gcld.battle.common.*;
import java.io.*;

public class CityNpcBuilder extends Builder
{
    public CityNpcBuilder(final int battleType) {
        this.battleType = battleType;
    }
    
    @Override
    public String getBattleId(final IDataGetter dataGetter, final Player player, final int defId) {
        return NewBattleManager.getBattleId(this.battleType, player.getPlayerId(), defId);
    }
    
    @Override
    public Battle existBattle(final IDataGetter dataGetter, final Player player, final int defId) {
        return null;
    }
    
    public int getArea(final int forceId, final WorldCity worldCity) {
        if (forceId == 1) {
            return worldCity.getWeiArea();
        }
        if (forceId == 2) {
            return worldCity.getShuArea();
        }
        return worldCity.getWuArea();
    }
    
    @Override
    public Tuple<Boolean, String> canJoinBattle(final Player player, final List<PlayerGeneralMilitary> pgmList, final IDataGetter dataGetter, final Battle bat) {
        final Tuple<Boolean, String> tuple = new Tuple();
        tuple.left = false;
        final int defId = bat.getDefBaseInfo().getId();
        final PlayerAttribute pa = dataGetter.getPlayerAttributeDao().read(player.getPlayerId());
        final char[] cs = pa.getFunctionId().toCharArray();
        if (cs[24] == '0') {
            tuple.right = LocalMessages.T_COMM_10020;
            return tuple;
        }
        final int areaId = this.getArea(player.getForceId(), (WorldCity)dataGetter.getWorldCityCache().get((Object)bat.getDefBaseInfo().getId()));
        final WorldCityArea wca = (WorldCityArea)dataGetter.getWorldCityAreaCache().get((Object)areaId);
        final int rank = dataGetter.getRankService().getRank(1, player.getPlayerId(), player.getForceId());
        if (rank > wca.getEnterRank()) {
            tuple.right = LocalMessages.BATTLE_MUST_RANK;
            return tuple;
        }
        final PlayerWorld pw = dataGetter.getPlayerWorldDao().read(player.getPlayerId());
        final Set<Integer> canAttSet = new HashSet<Integer>();
        int aId = 0;
        if (pw.getCanAttId() != null) {
            final String[] ids = pw.getCanAttId().split(",");
            String[] array;
            for (int length = (array = ids).length, i = 0; i < length; ++i) {
                final String str = array[i];
                aId = Integer.valueOf(str);
                canAttSet.add(aId);
            }
        }
        if (!canAttSet.contains(defId)) {
            tuple.right = LocalMessages.LOCATION_NOT_OPT;
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
    
    @Override
    public Tuple<Boolean, String> attPermitCreate(final IDataGetter dataGetter, final int playerId, final int defId) {
        final Tuple<Boolean, String> tuple = new Tuple();
        tuple.left = false;
        final Player player = dataGetter.getPlayerDao().read(playerId);
        final PlayerAttribute pa = dataGetter.getPlayerAttributeDao().read(player.getPlayerId());
        final char[] cs = pa.getFunctionId().toCharArray();
        if (cs[24] == '0') {
            tuple.right = LocalMessages.T_COMM_10020;
            return tuple;
        }
        final JuBenDto juBenDto = JuBenManager.getInstance().getByPid(playerId);
        if (juBenDto != null) {
            tuple.right = LocalMessages.IN_JUBEN_CANNT_BATTLE;
            return tuple;
        }
        final int areaId = dataGetter.getWorldCityCache().getArea(player.getForceId(), defId);
        final WorldCityArea wca = (WorldCityArea)dataGetter.getWorldCityAreaCache().get((Object)areaId);
        final int rank = dataGetter.getRankService().getRank(1, player.getPlayerId(), player.getForceId());
        if (rank > wca.getEnterRank()) {
            tuple.right = LocalMessages.BATTLE_MUST_RANK;
            return tuple;
        }
        final PlayerWorld pw = dataGetter.getPlayerWorldDao().read(player.getPlayerId());
        final Set<Integer> canAttSet = new HashSet<Integer>();
        int aId = 0;
        if (pw.getCanAttId() != null) {
            final String[] ids = pw.getCanAttId().split(",");
            String[] array;
            for (int length = (array = ids).length, i = 0; i < length; ++i) {
                final String str = array[i];
                aId = Integer.valueOf(str);
                canAttSet.add(aId);
            }
        }
        if (!canAttSet.contains(defId)) {
            tuple.right = LocalMessages.LOCATION_NOT_OPT;
            return tuple;
        }
        tuple.left = true;
        tuple.right = "";
        return tuple;
    }
    
    @Override
    public Tuple<Boolean, byte[]> attPermitBack(final IDataGetter dataGetter, final int playerId, final int defId, final int generalId) {
        final Tuple<Boolean, byte[]> tuple = new Tuple();
        tuple.left = false;
        final Battle battle = NewBattleManager.getInstance().getBattleByDefId(10, defId);
        if (battle == null) {
            tuple.right = JsonBuilder.getJson(State.FAIL, LocalMessages.BATTLE_NOT_EXIST);
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
    
    @Override
    public Tuple<Boolean, String> canCreateBattle(final Player player, final int defId, final IDataGetter dataGetter) {
        final Tuple<Boolean, String> tuple = new Tuple();
        tuple.left = false;
        final PlayerAttribute pa = dataGetter.getPlayerAttributeDao().read(player.getPlayerId());
        final char[] cs = pa.getFunctionId().toCharArray();
        if (cs[24] == '0') {
            tuple.right = LocalMessages.T_COMM_10020;
            return tuple;
        }
        if (WorldCityCommon.nationMainCityIdMap.get(player.getForceId()) == defId) {
            tuple.right = LocalMessages.BATTLE_INFO_WORLD_CITY_SELF;
            return tuple;
        }
        final JuBenDto juBenDto = JuBenManager.getInstance().getByPid(player.getPlayerId());
        if (juBenDto != null) {
            tuple.right = LocalMessages.IN_JUBEN_CANNT_BATTLE;
            return tuple;
        }
        final PlayerWorld pw = dataGetter.getPlayerWorldDao().read(player.getPlayerId());
        final Set<Integer> canAttSet = new HashSet<Integer>();
        int aId = 0;
        if (pw.getCanAttId() != null) {
            final String[] ids = pw.getCanAttId().split(",");
            String[] array;
            for (int length = (array = ids).length, i = 0; i < length; ++i) {
                final String str = array[i];
                aId = Integer.valueOf(str);
                canAttSet.add(aId);
            }
        }
        if (!canAttSet.contains(defId)) {
            tuple.right = LocalMessages.LOCATION_NOT_OPT;
            return tuple;
        }
        final int areaId = this.getArea(player.getForceId(), (WorldCity)dataGetter.getWorldCityCache().get((Object)defId));
        final WorldCityArea wca = (WorldCityArea)dataGetter.getWorldCityAreaCache().get((Object)areaId);
        final int rank = dataGetter.getRankService().getRank(1, player.getPlayerId(), player.getForceId());
        if (rank > wca.getEnterRank()) {
            tuple.right = LocalMessages.BATTLE_MUST_RANK;
            return tuple;
        }
        tuple.left = true;
        return tuple;
    }
    
    @Override
    public Terrain getTerrain(final int playerId, final int defId, final IDataGetter dataGetter) {
        final WorldCity worldCity = (WorldCity)dataGetter.getWorldCityCache().get((Object)defId);
        final int display = (worldCity == null) ? 1 : worldCity.getTerrain();
        return new Terrain(display, (worldCity == null) ? 1 : worldCity.getTerrainEffectType(), display);
    }
    
    @Override
    public int getBatInfo(final IDataGetter dataGetter, final int defId) {
        return 1000001;
    }
    
    @Override
    public boolean initDefCamp(final IDataGetter dataGetter, final BattleAttacker battleAttacker, final int defId, final Battle bat) {
        final int attForceId = battleAttacker.attForceId;
        final int playerId = battleAttacker.attPlayerId;
        final City city = dataGetter.getCityDao().read(defId);
        final boolean isNpc = true;
        int defNum = 0;
        int forceId = 0;
        final int areaId = dataGetter.getWorldCityCache().getArea(attForceId, city.getId());
        final WorldCityArea wca = (WorldCityArea)dataGetter.getWorldCityAreaCache().get((Object)areaId);
        forceId = wca.getMaskChief();
        CampArmy campArmy = null;
        final Army armyCach = (Army)dataGetter.getArmyCache().get((Object)forceId);
        final Integer[] defNpcs = wca.getMaskArmiesId();
        final PlayerMistLost playerMistLost = dataGetter.getPlayerMistLostDao().getMist(playerId, areaId);
        String[] npcLosts = null;
        if (playerMistLost == null || playerMistLost.getNpcLost() == null || playerMistLost.getNpcLost().equals("")) {
            npcLosts = new String[defNpcs.length];
            for (int i = 0; i < npcLosts.length; ++i) {
                npcLosts[i] = "0";
            }
        }
        else {
            npcLosts = playerMistLost.getNpcLost().split(";");
            if (npcLosts.length == 0 || defNpcs.length != npcLosts.length) {
                ErrorSceneLog.getInstance().appendErrorMsg("table player_mist_lost npclost size error").appendClassName("OneVsRewardNpcBuilder").appendMethodName("initDefCamp").append("playerId", playerMistLost.getPlayerId()).append("areaId", playerMistLost.getAreaId()).append("lost", playerMistLost.getNpcLost()).flush();
            }
        }
        int id = 0;
        int npcId = 0;
        int npcLost = 0;
        if (defNpcs != null) {
            for (int j = 0; j < defNpcs.length; ++j) {
                npcId = defNpcs[j];
                if (npcId > 0) {
                    id = bat.campNum.getAndIncrement();
                    if (j >= npcLosts.length) {
                        npcLost = 0;
                    }
                    else {
                        npcLost = Integer.parseInt(npcLosts[j]);
                    }
                    campArmy = copyArmyFromCach(npcId, npcLost, dataGetter, id, bat.terrainVal, armyCach.getGeneralLv());
                    defNum += campArmy.getArmyHpOrg();
                    bat.defCamp.add(campArmy);
                    BattleSceneLog.getInstance().info("#batId:" + bat.getBattleId() + "_" + bat.getStartTime() + "#add:init#side:def" + "#playerId:" + campArmy.getPlayerId() + ":" + campArmy.isPhantom + "#general:" + campArmy.getGeneralId() + "#defSize:" + bat.defCamp.size());
                }
            }
        }
        bat.defBaseInfo.setAllNum(defNum);
        bat.defBaseInfo.setNum(defNum);
        bat.defBaseInfo.setForceId(forceId);
        return isNpc;
    }
    
    public static CampArmy copyArmyFromCach(final int npcId, final int npc_lost, final IDataGetter dataGetter, final int id, final int terrainType, final int npcLv) {
        final CampArmy campArmy = new CampArmy();
        final Army armyCach = (Army)dataGetter.getArmyCache().get((Object)npcId);
        campArmy.setPlayerId(-1);
        campArmy.setPlayerName("NPC");
        campArmy.setForceId(0);
        campArmy.setPlayerLv(npcLv);
        campArmy.setId(id);
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
        campArmy.setMaxForces(armyCach.getTroopHp());
        int armyHp = armyCach.getArmyHp() - npc_lost;
        final int remainder = armyHp % 3;
        armyHp -= remainder;
        campArmy.setArmyHp(armyHp);
        campArmy.setArmyHpOrg(armyHp);
        if (general.getTacticId() > 0) {
            campArmy.setTacticVal(1);
            if (campArmy.getSpecialGeneral().generalType == 7) {
                campArmy.setTacticVal((int)campArmy.getSpecialGeneral().param);
            }
        }
        campArmy.setColumn(armyCach.getArmyHp() / armyCach.getTroopHp());
        campArmy.setStrategies(troop.getStrategyDefMap().get(terrainType));
        return campArmy;
    }
    
    @Override
    public String getAttTargetName(final IDataGetter dataGetter, final Battle bat) {
        return ((WorldCity)dataGetter.getWorldCityCache().get((Object)bat.getDefBaseInfo().getId())).getName();
    }
    
    @Override
    public void sendTaskMessage(final int playerId, final int defId, final IDataGetter dataGetter) {
    }
    
    @Override
    public byte[] getBattleTopInfo(final IDataGetter dataGetter, final PlayerDto playerDto, final Battle battle) {
        final JsonDocument doc = new JsonDocument();
        doc.createElement("bat", 1000001);
        final int areaId = dataGetter.getWorldCityCache().getArea(playerDto.forceId, battle.getDefBaseInfo().getId());
        final WorldCityArea wca = (WorldCityArea)dataGetter.getWorldCityAreaCache().get((Object)areaId);
        doc.appendJson(this.getAttTopLeft(dataGetter, playerDto.playerId, battle));
        doc.appendJson(this.getDefTopRight(dataGetter, playerDto.playerId, battle, wca.getMaskChief()));
        return doc.toByte();
    }
    
    @Override
    public byte[] getPrepareInfo(final IDataGetter dataGetter, final PlayerDto playerDto, final int defId, final Battle battle, final int terrain) {
        final int playerId = playerDto.playerId;
        final JsonDocument doc = new JsonDocument();
        final List<PlayerGeneralMilitary> pgmList = dataGetter.getPlayerGeneralMilitaryDao().getMilitaryList(playerId);
        final WorldCity worldCity = (WorldCity)dataGetter.getWorldCityCache().get((Object)defId);
        doc.createElement("name", worldCity.getName());
        doc.createElement("intro", worldCity.getIntro());
        doc.createElement("bat", this.battleType * 100000 + 1);
        final WorldCityArea wca = (WorldCityArea)dataGetter.getWorldCityAreaCache().get((Object)dataGetter.getWorldCityCache().getArea(playerDto.forceId, worldCity));
        doc.appendJson(this.getAttTopLeft(dataGetter, playerId, battle));
        doc.appendJson(this.getDefTopRight(dataGetter, playerId, battle, wca.getMaskChief()));
        doc.appendJson(this.getAttGenerals(dataGetter, pgmList, playerDto, defId, terrain));
        doc.appendJson(this.getDefGenerals(dataGetter, playerDto.playerId, wca, terrain));
        return doc.toByte();
    }
    
    public byte[] getAttGenerals(final IDataGetter dataGetter, final List<PlayerGeneralMilitary> pgmList, final PlayerDto playerDto, final int defId, final int terrain) {
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
            final Troop troop = dataGetter.getTroopCache().getTroop(general.getTroop(), playerDto.playerId);
            final TroopTerrain gTerrain = troop.getTerrains().get(terrain);
            if (gTerrain != null && gTerrain.getAttEffect() > 0) {
                doc.createElement("terrainAdd", gTerrain.getAttEffect());
                doc.createElement("terrainQ", gTerrain.getAttQuality());
            }
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
            final GeneralMoveDto gmd = CityService.getGeneralMoveDto(pgm.getPlayerId(), pgm.getGeneralId());
            doc.createElement("troopId", troop.getType());
            doc.createElement("troopType", troop.getSerial());
            doc.createElement("troopName", troop.getName());
            doc.createElement("generalPic", general.getPic());
            doc.createElement("quality", general.getQuality());
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
            else if (armyHp * 1.0 / maxHp < 0.05) {
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
            else if (TeamManager.getInstance().isJoinTeam(pgm.getPlayerId(), pgm.getGeneralId())) {
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
            doc.createElement("location", ((WorldCity)dataGetter.getWorldCityCache().get((Object)pgm.getLocationId())).getName());
            doc.createElement("armyHp", armyHp);
            doc.createElement("armyHpMax", dataGetter.getBattleDataCache().getMaxHp(pgm));
            adhMap = dataGetter.getBattleDataCache().getAttDefHp(playerDto.playerId, pgm.getGeneralId(), troop, pgm.getLv());
            final int forcesMax = adhMap.get(3);
            final long needForces = forcesMax - pgm.getForces();
            long needTime = 0L;
            final double secondForces = dataGetter.getGeneralService().getOutput(pgm.getPlayerId(), playerDto.forceId, pgm.getLocationId(), troop);
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
                doc.createElement("tacticName", tactic.getName());
            }
            if (general.getTacticId() == 24) {
                doc.createElement("rbType", 3);
                final RewardInfo ri = tactic.getReward().canReward(dataGetter, playerDto.playerId, pgm);
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
    
    private byte[] getDefGenerals(final IDataGetter dataGetter, final int playerId, final WorldCityArea wca, final int terrain) {
        final JsonDocument doc = new JsonDocument();
        final Integer[] defNpcs = wca.getMaskArmiesId();
        doc.startArray("defGenerals");
        General general = null;
        Troop troop = null;
        TroopTerrain gTerrain = null;
        final PlayerMistLost playerMistLost = dataGetter.getPlayerMistLostDao().getMist(playerId, wca.getArea());
        String[] losts = null;
        if (playerMistLost == null || playerMistLost.getNpcLost() == null || playerMistLost.getNpcLost().equals("")) {
            losts = new String[defNpcs.length];
            for (int i = 0; i < losts.length; ++i) {
                losts[i] = "0";
            }
        }
        else {
            losts = playerMistLost.getNpcLost().split(";");
            if (losts.length == 0 || defNpcs.length != losts.length) {
                ErrorSceneLog.getInstance().appendErrorMsg("player_mist_lost\u8868npclost\u5f02\u5e38\uff01").appendClassName("OneVsRewardNpcBuilder").appendMethodName("initDefCamp").append("playerId", playerMistLost.getPlayerId()).append("areaId", playerMistLost.getAreaId()).append("npclost", playerMistLost.getNpcLost()).flush();
            }
        }
        if (defNpcs != null && defNpcs.length > 0) {
            for (int i = 0; i < defNpcs.length; ++i) {
                final int npc = defNpcs[i];
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
                    if (i >= losts.length) {
                        doc.createElement("armyHp", armyCach.getArmyHp());
                    }
                    else {
                        final int lost = Integer.parseInt(losts[i]);
                        doc.createElement("armyHp", armyCach.getArmyHp() - lost);
                    }
                    doc.createElement("armyHpMax", armyCach.getArmyHp());
                    if (general.getTacticId() != 0) {
                        final Tactic tactic = (Tactic)dataGetter.getTacticCache().get((Object)general.getTacticId());
                        if (tactic != null) {
                            doc.createElement("tacticName", tactic.getName());
                        }
                    }
                    gTerrain = troop.getTerrains().get(terrain);
                    if (gTerrain != null && gTerrain.getDefEffect() > 0) {
                        doc.createElement("terrainAdd", gTerrain.getDefEffect());
                        doc.createElement("terrainQ", gTerrain.getDefQuality());
                    }
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
                    doc.endObject();
                }
            }
        }
        doc.endArray();
        return doc.toByte();
    }
    
    @Override
    public int getGeneralState() {
        return 10;
    }
    
    @Override
    public void dealNextNpc(final boolean attWin, final IDataGetter dataGetter, final Battle bat, final BattleResult battleResult) {
        final int defId = bat.getDefBaseInfo().getId();
        int cityId = 0;
        final int forceId = bat.getAttBaseInfo().getForceId();
        int attackingArea = 0;
        final WorldCity wc = (WorldCity)dataGetter.getWorldCityCache().get((Object)defId);
        attackingArea = dataGetter.getWorldCityCache().getArea(forceId, wc);
        final Set<Integer> openAreaSet = new HashSet<Integer>();
        for (final int playerId : bat.inBattlePlayers.keySet()) {
            final PlayerInfo pi = bat.inBattlePlayers.get(playerId);
            final PlayerWorld pw = dataGetter.getPlayerWorldDao().read(playerId);
            if (pi.isAttSide && attWin) {
                battleResult.cityName = ((WorldCity)dataGetter.getWorldCityCache().get((Object)defId)).getName();
                battleResult.cType = 1;
                final Set<Integer> attedSet = new HashSet<Integer>();
                attedSet.add(defId);
                if (pw.getAttedId() != null) {
                    final String[] ids = pw.getAttedId().split(",");
                    String[] array;
                    for (int length = (array = ids).length, i = 0; i < length; ++i) {
                        final String str = array[i];
                        cityId = Integer.valueOf(str);
                        attedSet.add(cityId);
                    }
                }
                final List<WorldCity> list = dataGetter.getWorldCityCache().getAreaCity(forceId, attackingArea);
                for (final WorldCity wcTemp : list) {
                    attedSet.add(wcTemp.getId());
                }
                final Set<Integer> canAttSet = new HashSet<Integer>();
                if (pw.getCanAttId() != null) {
                    final String[] ids2 = pw.getCanAttId().split(",");
                    String[] array2;
                    for (int length2 = (array2 = ids2).length, j = 0; j < length2; ++j) {
                        final String str2 = array2[j];
                        cityId = Integer.valueOf(str2);
                        if (!attedSet.contains(cityId)) {
                            canAttSet.add(cityId);
                        }
                    }
                }
                for (final WorldCity wcTemp2 : list) {
                    final Set<Integer> neighbors = dataGetter.getWorldRoadCache().getNeighbors(wcTemp2.getId());
                    for (final Integer nbId : neighbors) {
                        if (attedSet.contains(nbId)) {
                            continue;
                        }
                        if (canAttSet.contains(nbId)) {
                            continue;
                        }
                        canAttSet.add(nbId);
                        final int openArea = dataGetter.getWorldCityCache().getArea(forceId, nbId);
                        openAreaSet.add(openArea);
                        final List<WorldCity> wcList = dataGetter.getWorldCityCache().getAreaCity(forceId, openArea);
                        for (final WorldCity wcc : wcList) {
                            canAttSet.add(wcc.getId());
                        }
                    }
                }
                final StringBuilder attedSb = new StringBuilder();
                for (final Integer key : attedSet) {
                    attedSb.append(key).append(",");
                }
                final StringBuilder canAttSb = new StringBuilder();
                for (final Integer key2 : canAttSet) {
                    canAttSb.append(key2).append(",");
                }
                dataGetter.getPlayerWorldDao().updateAttInfo(playerId, attedSb.toString(), canAttSb.toString());
                String xyAxis = CityService.worldGeneralCityMap.get(playerId);
                String[] xys = null;
                if (xyAxis != null) {
                    xys = xyAxis.split(":");
                    xyAxis = String.valueOf(xys[0]) + ":" + bat.getDefBaseInfo().getId();
                }
                else {
                    xyAxis = "0:" + bat.getDefBaseInfo().getId();
                }
                CityService.worldGeneralCityMap.put(playerId, xyAxis);
                boolean needappend = true;
                final StringBuilder details = new StringBuilder();
                final String oldNpcLostDetail = pw.getNpcLostDetail();
                if (oldNpcLostDetail != null && !oldNpcLostDetail.isEmpty()) {
                    final String[] oldDetails = oldNpcLostDetail.split(";");
                    String[] array3;
                    for (int length3 = (array3 = oldDetails).length, k = 0; k < length3; ++k) {
                        final String oldDetail = array3[k];
                        final String[] ss = oldDetail.split(",");
                        if (ss.length == 3) {
                            final int id = Integer.parseInt(ss[0]);
                            if (id == attackingArea) {
                                needappend = false;
                                details.append(oldNpcLostDetail.replace(String.valueOf(oldDetail) + ";", ""));
                                break;
                            }
                        }
                    }
                    if (needappend) {
                        details.append(oldNpcLostDetail);
                    }
                }
                for (final Integer openArea2 : openAreaSet) {
                    final int maxHp = getMaxHp(dataGetter, openArea2);
                    final String detailTemp = openArea2 + "," + maxHp + "," + maxHp + ";";
                    details.append(detailTemp);
                    if (dataGetter.getPlayerMistLostDao().getMist(playerId, openArea2) == null) {
                        final PlayerMistLost playerMistLost = new PlayerMistLost();
                        playerMistLost.setPlayerId(playerId);
                        playerMistLost.setAreaId(openArea2);
                        playerMistLost.setNpcLost(null);
                        dataGetter.getPlayerMistLostDao().create(playerMistLost);
                    }
                    else {
                        dataGetter.getPlayerMistLostDao().updateNpcLostDetail(playerId, openArea2, null);
                    }
                }
                dataGetter.getPlayerWorldDao().updateNpcLostDetail(playerId, details.toString());
                final int done = dataGetter.getPlayerMistLostDao().deleteByPlayerIdAreaId(playerId, attackingArea);
            }
            else {
                final StringBuilder details2 = new StringBuilder();
                final String oldNpcLostDetail2 = pw.getNpcLostDetail();
                boolean needAdd = true;
                if (oldNpcLostDetail2 != null) {
                    final String[] oldDetails2 = oldNpcLostDetail2.split(";");
                    String[] array4;
                    for (int length4 = (array4 = oldDetails2).length, l = 0; l < length4; ++l) {
                        final String oldDetail2 = array4[l];
                        final String[] ss2 = oldDetail2.split(",");
                        if (ss2.length != 3) {
                            ErrorSceneLog.getInstance().appendErrorMsg("PlayerWorld NpcLostDetail error.").appendClassName("CityNpcBuilder").appendMethodName("dealNextNpc").append("playerId", pw.getPlayerId()).append("NpcLostDetail", oldNpcLostDetail2).append("part", oldDetail2).flush();
                        }
                        else {
                            final int id2 = Integer.parseInt(ss2[0]);
                            if (id2 == attackingArea) {
                                needAdd = false;
                                final String detailTemp2 = String.valueOf(ss2[0]) + "," + bat.defBaseInfo.num + "," + ss2[2] + ";";
                                details2.append(detailTemp2);
                            }
                            else {
                                details2.append(oldDetail2).append(";");
                            }
                        }
                    }
                    if (needAdd) {
                        final int maxHp2 = getMaxHp(dataGetter, attackingArea);
                        details2.append(attackingArea).append(",").append(bat.defBaseInfo.num).append(",").append(maxHp2).append(";");
                    }
                }
                else {
                    final int maxHp3 = getMaxHp(dataGetter, attackingArea);
                    details2.append(attackingArea).append(",").append(bat.defBaseInfo.num).append(",").append(maxHp3).append(";");
                }
                dataGetter.getPlayerWorldDao().updateNpcLostDetail(playerId, details2.toString());
                final WorldCityArea wca = (WorldCityArea)dataGetter.getWorldCityAreaCache().get((Object)attackingArea);
                final StringBuilder sb = new StringBuilder();
                int serialNo = 0;
                Integer[] maskArmiesId;
                for (int length5 = (maskArmiesId = wca.getMaskArmiesId()).length, n = 0; n < length5; ++n) {
                    final int id3 = maskArmiesId[n];
                    Builder.getCampArmyLost(dataGetter, sb, bat, id3, serialNo);
                    ++serialNo;
                }
                sb.replace(sb.length() - 1, sb.length(), "");
                if (dataGetter.getPlayerMistLostDao().getMist(playerId, attackingArea) != null) {
                    dataGetter.getPlayerMistLostDao().updateNpcLostDetail(playerId, attackingArea, sb.toString());
                }
                else {
                    final PlayerMistLost playerMistLost2 = new PlayerMistLost();
                    playerMistLost2.setPlayerId(playerId);
                    playerMistLost2.setAreaId(attackingArea);
                    playerMistLost2.setNpcLost(sb.toString());
                    dataGetter.getPlayerMistLostDao().create(playerMistLost2);
                }
            }
        }
    }
    
    @Override
    public void afterBat(final boolean attWin, final IDataGetter dataGetter, final Battle bat) {
        final int playerId = bat.getAttBaseInfo().getId();
        if (!bat.inSceneSet.contains(playerId)) {
            Players.push(playerId, PushCommand.PUSH_WORLD, JsonBuilder.getSimpleJson("mask", true));
        }
    }
    
    @Override
    public void dealTaskAfterWin(final boolean attWin, final IDataGetter dataGetter, final Battle bat, final BattleResult battleResult) {
        final int cityId = bat.getDefBaseInfo().getId();
        final int playerId = bat.getAttBaseInfo().getId();
        final Player self = dataGetter.getPlayerDao().read(playerId);
        final WorldCity worldCity = (WorldCity)dataGetter.getWorldCityCache().get((Object)cityId);
        int area = 0;
        switch (self.getForceId()) {
            case 1: {
                area = worldCity.getWeiArea();
                break;
            }
            case 2: {
                area = worldCity.getShuArea();
                break;
            }
            case 3: {
                area = worldCity.getWuArea();
                break;
            }
        }
        TaskMessageHelper.sendWorldMistWinTaskMessage(playerId, area);
    }
    
    @Override
    public void dealBuilderBattleTask(final IDataGetter dataGetter, final int playerId, final int bonudId) {
        dataGetter.getCourtesyService().addPlayerEvent(playerId, 9, 0);
    }
    
    @Override
    public void roundCaculateReward(final IDataGetter dataGetter, final Battle bat, final RoundInfo roundInfo) {
        FightRewardCoe frc = (FightRewardCoe)dataGetter.getFightRewardCoeCache().get((Object)this.getFightRewardCoeId(dataGetter, bat));
        if (frc == null) {
            BattleSceneLog.getInstance().debug("FightRewardCoe is null. battle type:" + bat.getBattleType());
            frc = new FightRewardCoe();
        }
        this.roundCaculateAttReward(dataGetter, frc, bat, roundInfo);
    }
    
    @Override
    public Tuple<List<PlayerGeneralMilitary>, String> chooseGeneral(final IDataGetter dataGetter, final Player player, final int defId, final List<Integer> gIdList) {
        final Tuple<List<PlayerGeneralMilitary>, String> tuple = new Tuple();
        final List<PlayerGeneralMilitary> chooseList = new ArrayList<PlayerGeneralMilitary>();
        final Map<Integer, PlayerGeneralMilitary> pgmMap = dataGetter.getPlayerGeneralMilitaryDao().getMilitaryMap(player.getPlayerId());
        for (int i = 0; i < gIdList.size(); ++i) {
            final PlayerGeneralMilitary pgm = pgmMap.get(gIdList.get(i));
            if (pgm.getState() <= 1) {
                final int maxHp = dataGetter.getBattleDataCache().getMaxHp(pgm);
                if (pgm.getForces() * 1.0 / maxHp < 0.05) {
                    tuple.left = null;
                    tuple.right = LocalMessages.BATTLE_INT_BATTLE_BLOOD_NO_ENOUGH;
                    return tuple;
                }
                final Set<Integer> neighborSet = dataGetter.getWorldRoadCache().getNeighbors(defId);
                neighborSet.contains(pgm.getLocationId());
                if (!TeamManager.getInstance().isJoinTeam(pgm.getPlayerId(), pgm.getGeneralId())) {
                    chooseList.add(pgm);
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
    
    @Override
    public int battleRewardSave(final boolean attWin, final int playerId, final PlayerInfo pi, final Date date, final IDataGetter dataGetter, final BattleResult battleResult, final Battle bat) {
        return 0;
    }
    
    @Override
    public int getDefPlayerLevel(final IDataGetter dataGetter, final Battle bat, final RoundInfo roundInfo) {
        final int defId = bat.defBaseInfo.getId();
        final int chiefId = ((WorldCity)dataGetter.getWorldCityCache().get((Object)defId)).getChief();
        return ((Army)dataGetter.getArmyCache().get((Object)chiefId)).getGeneralLv();
    }
    
    @Override
    public int caculateRoundCopper(final IDataGetter dataGetter, final Battle bat, final int playerId, final double mAttOmega, final FightRewardCoe frc) {
        final double copperAdd = dataGetter.getTechEffectCache().getTechEffect(playerId, 23) / 100.0;
        final int copper = (int)(mAttOmega * frc.getM() * (1.0 + copperAdd));
        return copper;
    }
    
    @Override
    public void countNpcReward(final boolean attWin, final IDataGetter dataGetter, final Battle bat, final BattleResult battleResult) {
    }
    
    public static int getMaxHp(final IDataGetter dataGetter, final int areaId) {
        final WorldCityArea worldCityArea = (WorldCityArea)dataGetter.getWorldCityAreaCache().get((Object)areaId);
        final Integer[] defNpcs = worldCityArea.getMaskArmiesId();
        int allNum = 0;
        for (int i = 0; i < defNpcs.length; ++i) {
            final Army armyCach = (Army)dataGetter.getArmyCache().get((Object)defNpcs[i]);
            allNum += armyCach.getArmyHp();
        }
        return allNum;
    }
    
    public static Tuple<Integer, Integer> getHpMaxHp(final IDataGetter dataGetter, final int playerId, final int areaId) {
        final Tuple<Integer, Integer> tuple = new Tuple();
        final int allNum = getMaxHp(dataGetter, areaId);
        tuple.right = allNum;
        int lostTotal = 0;
        final PlayerMistLost playerMistLost = dataGetter.getPlayerMistLostDao().read(areaId);
        final String[] losts = playerMistLost.getNpcLost().split(",");
        String[] array;
        for (int length = (array = losts).length, i = 0; i < length; ++i) {
            final String lost = array[i];
            lostTotal += Integer.parseInt(lost);
        }
        tuple.left = allNum - lostTotal;
        return tuple;
    }
    
    @Override
    public void dealTroopDrop(final IDataGetter dataGetter, final Battle bat, final RoundInfo roundInfo) {
        for (final BattleArmy ba : roundInfo.defKilledList) {
            final int troopId = ba.getCampArmy().getTroopId();
            final Troop bonusTroop = (Troop)dataGetter.getTroopCache().get((Object)troopId);
            if (bonusTroop != null && bonusTroop.getTroopDrop() != null) {
                dataGetter.getBattleDropService().saveBattleDrop(bat.attBaseInfo.id, bonusTroop.getTroopDrop());
                final Map<Integer, BattleDrop> dropMap = bonusTroop.getTroopDrop().getDropAndMap();
                dropMap.size();
                roundInfo.attRoundReward.addDropMap(dropMap);
            }
        }
    }
    
    @Override
    public void inBattleInfo(final int playerId, final boolean inBattle) {
    }
}
