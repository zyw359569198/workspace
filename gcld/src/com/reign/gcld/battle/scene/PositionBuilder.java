package com.reign.gcld.battle.scene;

import com.reign.gcld.general.domain.*;
import com.reign.gcld.battle.service.*;
import com.reign.gcld.juben.service.*;
import com.reign.gcld.player.domain.*;
import com.reign.gcld.battle.domain.*;
import com.reign.gcld.juben.common.*;
import com.reign.framework.json.*;
import com.reign.gcld.player.dto.*;
import com.reign.gcld.sdata.common.*;
import java.util.concurrent.atomic.*;
import com.reign.gcld.store.domain.*;
import com.reign.gcld.task.message.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.gcld.common.*;
import com.reign.util.*;
import com.reign.gcld.chat.common.*;
import com.reign.gcld.battle.common.*;
import java.util.*;

public class PositionBuilder extends Builder
{
    public PositionBuilder(final int battleType) {
        this.battleType = battleType;
    }
    
    @Override
    public void updateChooseTime(final Battle bat, final RoundInfo roundInfo, final boolean attAutoStChoosed, final boolean defAutoStChoosed) {
        if (roundInfo.win != 1 && bat.attList.size() > 0 && bat.attList.get(0).getCampArmy().getPlayerId() > 0 && !attAutoStChoosed && !bat.attList.get(0).getCampArmy().isPhantom && Players.getSession(Integer.valueOf(bat.attList.get(0).getCampArmy().getPlayerId())) != null) {
            roundInfo.nextMaxExeTime += 6000;
            roundInfo.timePredicationBuffer.append("st choose:").append(6000).append("|");
        }
    }
    
    @Override
    public Tuple<Boolean, String> canJoinBattle(final Player player, final List<PlayerGeneralMilitary> pgmList, final IDataGetter dataGetter, final Battle bat) {
        final int playerId = player.getPlayerId();
        final Tuple<Boolean, String> tuple = new Tuple();
        tuple.left = false;
        final int defId = bat.getDefBaseInfo().getId();
        if (defId < 1 || defId > 40) {
            tuple.right = LocalMessages.T_COMM_10011;
            return tuple;
        }
        final Battle battle = NewBattleManager.getInstance().getBattleByParm(4, player.getForceId(), defId);
        if (battle != null && battle.getAttBaseInfo().forceId != player.getForceId()) {
            tuple.right = LocalMessages.STATE_FINGHTING_INFO;
            return tuple;
        }
        if (battle != null) {
            int attPlayerCount = 0;
            for (final PlayerInfo pi : battle.inBattlePlayers.values()) {
                if (pi.playerId == player.getPlayerId()) {
                    tuple.right = LocalMessages.BATTLE_NO_JOIN_POSITION;
                    return tuple;
                }
                if (!pi.isAttSide) {
                    continue;
                }
                ++attPlayerCount;
            }
            if (attPlayerCount >= 3) {
                tuple.right = LocalMessages.BATTLE_FULL_NO_JOIN;
                return tuple;
            }
        }
        final PlayerOfficeRelative por = dataGetter.getPlayerOfficeRelativeDao().read(playerId);
        if (por == null || defId < por.getOfficerNpc()) {
            tuple.right = LocalMessages.BATTLE_CANNOT_ATTACKABLE_OFFICER;
            return tuple;
        }
        int lv = 0;
        boolean isNpc = true;
        if (!BattleConstant.officerBatMap.contains(defId)) {
            final OfficerBuildingInfo obi = dataGetter.getOfficerBuildingInfoDao().getByBuildingId(player.getForceId(), defId);
            if (obi != null) {
                final Player p = dataGetter.getPlayerDao().read(obi.getPlayerId());
                lv = p.getPlayerLv() - player.getPlayerLv();
                isNpc = false;
            }
        }
        if (isNpc) {
            final ChiefNpc chiefNpc = dataGetter.getHallsCache().getChiefNpc(defId, 1);
            final Army army = (Army)dataGetter.getArmyCache().get((Object)chiefNpc.getCheif());
            lv = army.getGeneralLv() - player.getPlayerLv();
        }
        final int limitLv = (int)(Object)((C)dataGetter.getcCache().get((Object)"Official.Attack.LvLimit")).getValue();
        if (lv > limitLv) {
            tuple.right = LocalMessages.BUILDING_POSITION_ATT_LV_LIMIT;
            return tuple;
        }
        final PlayerOfficerBuilding pob = dataGetter.getPlayerOfficerBuildingDao().read(playerId);
        if (pob != null && pob.getBuildingId() == defId) {
            tuple.right = LocalMessages.ALREADY_HAS_THIS_BUILDING;
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
    public void initJoinPlayer(final List<PlayerGeneralMilitary> pgmList, final IDataGetter dataGetter, final Player player, final Battle bat, final BaseInfo baseInfo, final LinkedList<CampArmy> campMap, final LinkedList<CampArmy> campChange, final int battleSide, final TeamInfo teamInfo) {
        if (battleSide != 1) {
            return;
        }
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
    
    @Override
    public Tuple<Boolean, String> canCreateBattle(final Player player, final int defId, final IDataGetter dataGetter) {
        final int playerId = player.getPlayerId();
        final Tuple<Boolean, String> tuple = new Tuple();
        tuple.left = false;
        final Battle battle = NewBattleManager.getInstance().getBattleByParm(4, player.getForceId(), defId);
        if (battle != null && battle.getAttBaseInfo().forceId != player.getForceId()) {
            tuple.right = LocalMessages.STATE_FINGHTING_INFO;
            return tuple;
        }
        if (battle != null) {
            int attPlayerCount = 0;
            for (final PlayerInfo pi : battle.inBattlePlayers.values()) {
                if (pi.isAttSide) {
                    ++attPlayerCount;
                }
            }
            if (attPlayerCount >= 3) {
                tuple.right = LocalMessages.BATTLE_FULL_NO_JOIN;
                return tuple;
            }
        }
        final JuBenDto juBenDto = JuBenManager.getInstance().getByPid(player.getPlayerId());
        if (juBenDto != null) {
            tuple.right = LocalMessages.IN_JUBEN_CANNT_BATTLE;
            return tuple;
        }
        final PlayerOfficeRelative por = dataGetter.getPlayerOfficeRelativeDao().read(playerId);
        if (defId < por.getOfficerNpc() || por == null) {
            tuple.right = LocalMessages.BATTLE_CANNOT_ATTACKABLE_OFFICER;
            return tuple;
        }
        int lv = 0;
        boolean isNpc = true;
        if (!BattleConstant.officerBatMap.contains(defId)) {
            final OfficerBuildingInfo obi = dataGetter.getOfficerBuildingInfoDao().getByBuildingId(player.getForceId(), defId);
            if (obi != null) {
                final Player p = dataGetter.getPlayerDao().read(obi.getPlayerId());
                lv = p.getPlayerLv() - player.getPlayerLv();
                isNpc = false;
            }
        }
        if (isNpc) {
            final ChiefNpc chiefNpc = dataGetter.getHallsCache().getChiefNpc(defId, 1);
            final Army army = (Army)dataGetter.getArmyCache().get((Object)chiefNpc.getCheif());
            lv = army.getGeneralLv() - player.getPlayerLv();
        }
        final int limitLv = (int)(Object)((C)dataGetter.getcCache().get((Object)"Official.Attack.LvLimit")).getValue();
        if (lv > limitLv) {
            tuple.right = LocalMessages.BUILDING_POSITION_ATT_LV_LIMIT;
            return tuple;
        }
        final PlayerOfficerBuilding pob = dataGetter.getPlayerOfficerBuildingDao().read(playerId);
        if (pob != null && pob.getBuildingId() == defId) {
            tuple.right = LocalMessages.ALREADY_HAS_THIS_BUILDING;
            return tuple;
        }
        tuple.left = true;
        return tuple;
    }
    
    @Override
    public Battle existBattle(final IDataGetter dataGetter, final Player player, final int defId) {
        if (BattleConstant.officerBatMap.contains(defId)) {
            return null;
        }
        final Battle battle = NewBattleManager.getInstance().getBattleByParm(this.battleType, player.getForceId(), defId);
        return battle;
    }
    
    @Override
    public Tuple<Boolean, String> attPermitCreate(final IDataGetter dataGetter, final int playerId, final int defId) {
        final Player player = dataGetter.getPlayerDao().read(playerId);
        final Tuple<Boolean, String> tuple = new Tuple();
        tuple.left = false;
        final Battle battle = NewBattleManager.getInstance().getBattleByParm(4, player.getForceId(), defId);
        if (battle != null && battle.getAttBaseInfo().forceId != player.getForceId()) {
            tuple.right = LocalMessages.STATE_FINGHTING_INFO;
            return tuple;
        }
        final JuBenDto juBenDto = JuBenManager.getInstance().getByPid(playerId);
        if (juBenDto != null) {
            tuple.right = LocalMessages.IN_JUBEN_CANNT_BATTLE;
            return tuple;
        }
        if (battle != null) {
            int attPlayerCount = 0;
            for (final PlayerInfo pi : battle.inBattlePlayers.values()) {
                if (pi.isAttSide) {
                    ++attPlayerCount;
                }
            }
            if (attPlayerCount >= 3) {
                tuple.right = LocalMessages.BATTLE_FULL_NO_JOIN;
                return tuple;
            }
        }
        final PlayerOfficeRelative por = dataGetter.getPlayerOfficeRelativeDao().read(playerId);
        if (defId < por.getOfficerNpc() || por == null) {
            tuple.right = LocalMessages.BATTLE_CANNOT_ATTACKABLE_OFFICER;
            return tuple;
        }
        int lv = 0;
        boolean isNpc = true;
        if (!BattleConstant.officerBatMap.contains(defId)) {
            final OfficerBuildingInfo obi = dataGetter.getOfficerBuildingInfoDao().getByBuildingId(player.getForceId(), defId);
            if (obi != null) {
                final Player p = dataGetter.getPlayerDao().read(obi.getPlayerId());
                lv = p.getPlayerLv() - player.getPlayerLv();
                isNpc = false;
            }
        }
        if (isNpc) {
            final ChiefNpc chiefNpc = dataGetter.getHallsCache().getChiefNpc(defId, 1);
            final Army army = (Army)dataGetter.getArmyCache().get((Object)chiefNpc.getCheif());
            lv = army.getGeneralLv() - player.getPlayerLv();
        }
        final int limitLv = (int)(Object)((C)dataGetter.getcCache().get((Object)"Official.Attack.LvLimit")).getValue();
        if (lv > limitLv) {
            tuple.right = LocalMessages.BUILDING_POSITION_ATT_LV_LIMIT;
            return tuple;
        }
        final PlayerOfficerBuilding pob = dataGetter.getPlayerOfficerBuildingDao().read(playerId);
        if (pob != null && pob.getBuildingId() == defId) {
            tuple.right = LocalMessages.ALREADY_HAS_THIS_BUILDING;
            return tuple;
        }
        tuple.left = true;
        tuple.right = "";
        return tuple;
    }
    
    @Override
    public Tuple<Boolean, byte[]> attPermitBack(final IDataGetter dataGetter, final int playerId, final int defId, final int generalId) {
        final Tuple<Boolean, byte[]> tuple = new Tuple();
        final Battle battle = NewBattleManager.getInstance().getBattleByBatType(playerId, 4);
        if (battle == null) {
            NewBattleManager.inOccupyBattle(playerId, false);
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
    
    @Override
    public byte[] getBattleTopInfo(final IDataGetter dataGetter, final PlayerDto playerDto, final Battle battle) {
        final JsonDocument doc = new JsonDocument();
        doc.createElement("bat", this.battleType * 100000 + 1);
        doc.appendJson(this.getAttTopLeft(dataGetter, playerDto.playerId, battle));
        doc.appendJson(this.getDefTopRight(dataGetter, playerDto.playerId, battle, battle.getDefBaseInfo().getDefChiefId(), battle.isNpc()));
        return doc.toByte();
    }
    
    @Override
    public byte[] getAttTopLeft(final IDataGetter dataGetter, final int playerId, final Battle battle) {
        final JsonDocument doc = new JsonDocument();
        Player player = null;
        if (battle != null) {
            player = dataGetter.getPlayerDao().read(battle.getAttBaseInfo().getId());
            doc.createElement("playerForces", battle.getAttBaseInfo().getNum());
            doc.createElement("playerMaxForces", battle.getAttBaseInfo().getAllNum());
        }
        else {
            player = dataGetter.getPlayerDao().read(playerId);
        }
        doc.createElement("playerId", player.getPlayerId());
        doc.createElement("playerName", player.getPlayerName());
        doc.createElement("playerPic", player.getPic());
        doc.createElement("playerLv", player.getPlayerLv());
        return doc.toByte();
    }
    
    private byte[] getDefTopRight(final IDataGetter dataGetter, final int playerId, final Battle battle, final int defLegionId, final boolean isNpc) {
        final JsonDocument doc = new JsonDocument();
        if (isNpc) {
            final Army army = (Army)dataGetter.getArmyCache().get((Object)defLegionId);
            doc.createElement("npcId", army.getGeneralId());
            doc.createElement("npcName", (Object)(String.valueOf(army.getName()) + LocalMessages.T_COMM_10016));
            doc.createElement("npcPic", ((General)dataGetter.getGeneralCache().get((Object)army.getGeneralId())).getPic());
            doc.createElement("npcFlag", "NPC");
            doc.createElement("npcLv", army.getGeneralLv());
        }
        else {
            final Player player = dataGetter.getPlayerDao().read(defLegionId);
            doc.createElement("npcId", player.getPlayerId());
            doc.createElement("npcName", player.getPlayerName());
            doc.createElement("npcPic", player.getPic());
            doc.createElement("npcLv", player.getPlayerLv());
        }
        if (battle != null) {
            doc.createElement("npcForces", battle.getDefBaseInfo().getNum());
            doc.createElement("npcMaxForces", battle.getDefBaseInfo().getAllNum());
        }
        return doc.toByte();
    }
    
    @Override
    public int getBatInfo(final IDataGetter dataGetter, final int defId) {
        return this.battleType * 100000 + 1;
    }
    
    @Override
    public byte[] getPrepareInfo(final IDataGetter dataGetter, final PlayerDto playerDto, final int defId, final Battle battle, final int terrain) {
        final int playerId = playerDto.playerId;
        final JsonDocument doc = new JsonDocument();
        doc.createElement("bat", this.battleType * 100000 + 1);
        final List<PlayerGeneralMilitary> pgmList = dataGetter.getPlayerGeneralMilitaryDao().getMilitaryList(playerId);
        int defChiefId = 0;
        boolean isNpc = true;
        OfficerBuildingInfo obi = null;
        if (!BattleConstant.officerBatMap.contains(defId)) {
            obi = dataGetter.getOfficerBuildingInfoDao().getByBuildingId(playerDto.forceId, defId);
            if (obi != null) {
                defChiefId = obi.getPlayerId();
                isNpc = false;
            }
        }
        if (isNpc) {
            defChiefId = dataGetter.getHallsCache().getChiefNpc(defId, 1).getCheif();
        }
        doc.appendJson(this.getAttTopLeft(dataGetter, playerId, battle));
        doc.appendJson(this.getDefTopRight(dataGetter, playerId, battle, defChiefId, isNpc));
        doc.appendJson(this.getAttGenerals(dataGetter, playerId, pgmList, playerDto.forceId, terrain));
        doc.appendJson(this.getDefGenerals(dataGetter, playerDto, defChiefId, obi, battle, defId, terrain));
        return doc.toByte();
    }
    
    private byte[] getDefGenerals(final IDataGetter dataGetter, final PlayerDto playerDto, final int defChiefId, final OfficerBuildingInfo obi, final Battle battle, final int buildingId, final int terrain) {
        final JsonDocument doc = new JsonDocument();
        doc.startArray("defGenerals");
        if (obi == null) {
            TroopTerrain gTerrain = null;
            Troop troop = null;
            General general = null;
            final ChiefNpc cn = dataGetter.getHallsCache().getChiefNpc(buildingId, 1);
            for (final int npc : cn.getNpcList()) {
                if (npc <= 0) {
                    continue;
                }
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
                doc.createElement("armyHp", armyCach.getArmyHp());
                doc.createElement("quality", general.getQuality());
                doc.createElement("generalPic", general.getPic());
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
                doc.endObject();
            }
            final Army army = (Army)dataGetter.getArmyCache().get((Object)defChiefId);
            general = (General)dataGetter.getGeneralCache().get((Object)army.getGeneralId());
            troop = (Troop)dataGetter.getTroopCache().get((Object)general.getTroop());
            doc.startObject();
            doc.createElement("generalId", army.getGeneralId());
            doc.createElement("generalName", army.getName());
            doc.createElement("att", army.getAtt());
            doc.createElement("generalLv", army.getGeneralLv());
            doc.createElement("troopId", troop.getType());
            doc.createElement("troopType", troop.getSerial());
            doc.createElement("armyHp", army.getArmyHp());
            doc.createElement("quality", general.getQuality());
            doc.createElement("generalPic", general.getPic());
            if (general.getTacticId() != 0) {
                final Tactic tactic2 = (Tactic)dataGetter.getTacticCache().get((Object)general.getTacticId());
                if (tactic2 != null) {
                    doc.createElement("tacticName", tactic2.getName());
                }
            }
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
        }
        else {
            Troop troop2 = null;
            if (battle != null) {
                int num = 0;
                for (final CampArmy campArmy : battle.getDefCamp()) {
                    if (num >= 5) {
                        break;
                    }
                    doc.startObject();
                    doc.createElement("generalId", campArmy.generalId);
                    doc.createElement("generalName", campArmy.getGeneralName());
                    doc.createElement("att", campArmy.getAttEffect());
                    doc.createElement("generalLv", campArmy.getGeneralLv());
                    doc.createElement("troopId", campArmy.getTroopId());
                    troop2 = (Troop)dataGetter.getTroopCache().get((Object)campArmy.getTroopId());
                    doc.createElement("troopType", troop2.getSerial());
                    doc.createElement("armyHp", campArmy.getMaxForces());
                    final TroopTerrain gTerrain2 = troop2.getTerrains().get(terrain);
                    doc.createElement("quality", campArmy.getQuality());
                    doc.createElement("generalPic", campArmy.getGeneralPic());
                    if (campArmy.getTacicId() != 0) {
                        final Tactic tactic2 = (Tactic)dataGetter.getTacticCache().get((Object)campArmy.getTacicId());
                        if (tactic2 != null) {
                            doc.createElement("tacticName", tactic2.getName());
                        }
                    }
                    if (gTerrain2 != null && gTerrain2.getDefEffect() > 0) {
                        doc.createElement("terrainAdd", gTerrain2.getDefEffect());
                        doc.createElement("terrainQ", gTerrain2.getDefQuality());
                    }
                    final List<TerrainStrategySpecDto> tssList2 = troop2.getTsstList();
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
                    ++num;
                }
            }
            else {
                final List<PlayerOfficerBuilding> pobList = dataGetter.getPlayerOfficerBuildingDao().getBuildingMembers(playerDto.forceId, buildingId);
                final List<OccupyArmy> list = this.getCurPlayerIrror(pobList, dataGetter);
                for (int num2 = (list.size() > 5) ? 5 : list.size(), i = 0; i < num2; ++i) {
                    final OccupyArmy oa = list.get(i);
                    final General general2 = (General)dataGetter.getGeneralCache().get((Object)oa.generalId);
                    doc.startObject();
                    doc.createElement("generalId", oa.generalId);
                    doc.createElement("generalName", general2.getName());
                    doc.createElement("att", oa.attEffect);
                    doc.createElement("generalLv", oa.generalLv);
                    doc.createElement("troopId", oa.troopId);
                    troop2 = (Troop)dataGetter.getTroopCache().get((Object)oa.troopId);
                    doc.createElement("troopType", troop2.getSerial());
                    doc.createElement("armyHp", oa.maxForces);
                    doc.createElement("quality", general2.getQuality());
                    doc.createElement("generalPic", general2.getPic());
                    if (general2.getTacticId() != 0) {
                        final Tactic tactic = (Tactic)dataGetter.getTacticCache().get((Object)general2.getTacticId());
                        if (tactic != null) {
                            doc.createElement("tacticName", tactic.getName());
                        }
                    }
                    final TroopTerrain gTerrain3 = troop2.getTerrains().get(terrain);
                    if (gTerrain3 != null && gTerrain3.getDefEffect() > 0) {
                        doc.createElement("terrainAdd", gTerrain3.getDefEffect());
                        doc.createElement("terrainQ", gTerrain3.getDefQuality());
                    }
                    final List<TerrainStrategySpecDto> tssList3 = troop2.getTsstList();
                    if (tssList3 != null && tssList3.size() > 0) {
                        final List<Integer> tssIds3 = new LinkedList<Integer>();
                        for (final TerrainStrategySpecDto tss3 : tssList3) {
                            if (tss3.terrainId == terrain && (tss3.show == 2 || tss3.show == 3)) {
                                tssIds3.add(tss3.strategyId);
                            }
                        }
                        if (tssIds3.size() > 0) {
                            doc.startArray("tssList");
                            for (final TerrainStrategySpecDto tss3 : tssList3) {
                                doc.startObject();
                                doc.createElement("strategyId", tss3.strategyId);
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
    
    public List<OccupyArmy> getCurPlayerIrror(final List<PlayerOfficerBuilding> pobList, final IDataGetter dataGetter) {
        final List<OccupyArmy> list = new ArrayList<OccupyArmy>();
        int playerId = 0;
        Player player = null;
        for (final PlayerOfficerBuilding pob : pobList) {
            playerId = pob.getPlayerId();
            player = dataGetter.getPlayerDao().read(playerId);
            final List<PlayerGeneralMilitary> pgmList = dataGetter.getPlayerGeneralMilitaryDao().getMilitaryList(playerId);
            for (final PlayerGeneralMilitary pgm : pgmList) {
                final OccupyArmy oa = new OccupyArmy();
                oa.playerLv = player.getPlayerLv();
                oa.playerId = player.getPlayerId();
                oa.playerName = player.getPlayerName();
                oa.generalId = pgm.getGeneralId();
                oa.generalLv = pgm.getLv();
                final General general = (General)dataGetter.getGeneralCache().get((Object)pgm.getGeneralId());
                final Troop troop = dataGetter.getTroopCache().getTroop(general.getTroop(), player.getPlayerId());
                oa.troopId = troop.getId();
                oa.troopType = troop.getType();
                oa.quality = general.getQuality();
                oa.leader = pgm.getLeader(general.getLeader());
                oa.strength = pgm.getStrength(general.getStrength());
                final CampArmy campArmy = new CampArmy();
                campArmy.playerId = oa.playerId;
                campArmy.generalId = oa.generalId;
                campArmy.playerLv = oa.playerLv;
                campArmy.generalLv = oa.generalLv;
                campArmy.troopId = oa.troopId;
                campArmy.troopType = oa.troopType;
                final double terrain = 1.0;
                campArmy.terrain = terrain;
                Builder.getAttDefHp(dataGetter, campArmy);
                oa.maxForces = campArmy.maxForces;
                oa.troopHp = campArmy.troopHp;
                oa.attEffect = campArmy.attEffect;
                oa.defEffect = campArmy.defEffect;
                list.add(oa);
            }
        }
        return list;
    }
    
    @Override
    public String getBattleId(final IDataGetter dataGetter, final Player player, final int defId) {
        if (BattleConstant.officerBatMap.contains(defId)) {
            return NewBattleManager.getBattleId(this.battleType, player.getPlayerId(), defId);
        }
        return NewBattleManager.getBattleId(this.battleType, player.getForceId(), defId);
    }
    
    private Map<Integer, String> assignOfficer(final IDataGetter dataGetter, final Battle bat) {
        final Map<Integer, Integer> oMap = new HashMap<Integer, Integer>();
        for (final Integer playerId : bat.getInBattlePlayers().keySet()) {
            if (playerId == bat.getAttBaseInfo().getId()) {
                oMap.put(playerId, 1);
            }
            else {
                oMap.put(playerId, 2);
            }
        }
        final Map<Integer, String> map = dataGetter.getOccupyService().handleOfficerAfterBattle(bat.getAttBaseInfo().forceId, bat.getDefBaseInfo().getId(), oMap);
        return map;
    }
    
    @Override
    public Terrain getTerrain(final int playerId, final int defId, final IDataGetter dataGetter) {
        return new Terrain(6, 4, 6);
    }
    
    @Override
    public void inBattleInfo(final int playerId, final boolean inBattle) {
        NewBattleManager.inOccupyBattle(playerId, inBattle);
    }
    
    @Override
    public boolean initDefCamp(final IDataGetter dataGetter, final BattleAttacker battleAttacker, final int defId, final Battle bat) {
        final int attForceId = battleAttacker.attForceId;
        boolean isNpc = false;
        OfficerBuildingInfo obi = null;
        if (!BattleConstant.officerBatMap.contains(defId)) {
            obi = dataGetter.getOfficerBuildingInfoDao().getByBuildingId(attForceId, defId);
        }
        int defChiefId = 0;
        if (obi == null) {
            isNpc = true;
            defChiefId = dataGetter.getHallsCache().getChiefNpc(defId, 1).getCheif();
            bat.defBaseInfo.setForceId(0);
            bat.defBaseInfo.setDefChiefId(defChiefId);
        }
        else {
            final Player p = dataGetter.getPlayerDao().read(obi.getPlayerId());
            bat.defBaseInfo.setForceId(p.getForceId());
            bat.defBaseInfo.setDefChiefId(p.getPlayerId());
        }
        int defNum = 0;
        if (isNpc) {
            int id = 0;
            CampArmy campArmy = null;
            final Army armyCach = (Army)dataGetter.getArmyCache().get((Object)defChiefId);
            final List<Integer> npcList = dataGetter.getHallsCache().getChiefNpc(defId, 1).getNpcList();
            for (final Integer npc : npcList) {
                if (npc <= 0) {
                    continue;
                }
                id = bat.campNum.getAndIncrement();
                campArmy = Builder.copyArmyFromCach(null, npc, dataGetter, id, bat.terrainVal, armyCach.getGeneralLv());
                defNum += campArmy.getArmyHpOrg();
                bat.defCamp.add(campArmy);
                BattleSceneLog.getInstance().info("#batId:" + bat.getBattleId() + "_" + bat.getStartTime() + "#add:init#side:def" + "#playerId:" + campArmy.getPlayerId() + ":" + campArmy.isPhantom + "#general:" + campArmy.getGeneralId() + "#defSize:" + bat.defCamp.size());
            }
            id = bat.campNum.getAndIncrement();
            campArmy = Builder.copyArmyFromCach(null, defChiefId, dataGetter, id, bat.terrainVal, armyCach.getGeneralLv());
            defNum += campArmy.getArmyHpOrg();
            bat.defCamp.add(campArmy);
            BattleSceneLog.getInstance().info("#batId:" + bat.getBattleId() + "_" + bat.getStartTime() + "#add:init#side:def" + "#playerId:" + campArmy.getPlayerId() + ":" + campArmy.isPhantom + "#general:" + campArmy.getGeneralId() + "#defSize:" + bat.defCamp.size());
        }
        else {
            defNum = this.copyArmyFromMirror(dataGetter, bat.defCamp, defId, bat.campNum, bat.defBaseInfo.forceId, bat.terrainVal, bat);
        }
        bat.defBaseInfo.setAllNum(defNum);
        bat.defBaseInfo.setNum(defNum);
        return isNpc;
    }
    
    private int copyArmyFromMirror(final IDataGetter dataGetter, final LinkedList<CampArmy> camp, final int buildingId, final AtomicInteger campNum, final int defForceId, final int terrainVal, final Battle bat) {
        final List<PlayerOfficerBuilding> pobList = dataGetter.getPlayerOfficerBuildingDao().getBuildingMembers(defForceId, buildingId);
        final List<OccupyArmy> list = this.getCurPlayerIrror(pobList, dataGetter);
        int id = 0;
        int num = 0;
        int force = 0;
        for (int i = 0; i < list.size(); ++i) {
            final OccupyArmy oa = list.get(i);
            id = campNum.getAndIncrement();
            final CampArmy campArmy = new CampArmy();
            campArmy.setId(id);
            campArmy.setPlayerId(oa.playerId);
            campArmy.setPlayerName(oa.playerName);
            campArmy.setAttEffect(oa.attEffect);
            campArmy.setDefEffect(oa.defEffect);
            campArmy.setPlayerLv(oa.playerLv);
            campArmy.setForceId(defForceId);
            final General general = (General)dataGetter.getGeneralCache().get((Object)oa.generalId);
            campArmy.setSpecialGeneral(new SpecialGeneral(general.getGeneralSpecialInfo().generalType, general.getGeneralSpecialInfo().param, general.getGeneralSpecialInfo().param2));
            campArmy.setPgmVId(0);
            campArmy.setArmyName(general.getName());
            campArmy.setGeneralId(oa.generalId);
            campArmy.setGeneralName(general.getName());
            campArmy.setGeneralLv(oa.generalLv);
            campArmy.setGeneralPic(general.getPic());
            campArmy.setQuality(oa.quality);
            campArmy.setStrength(oa.strength);
            campArmy.setLeader(oa.leader);
            campArmy.setTacicId(general.getTacticId());
            campArmy.setTroopId(oa.troopId);
            final Troop troop = (Troop)dataGetter.getTroopCache().get((Object)oa.troopId);
            campArmy.setTroopSerial(troop.getSerial());
            campArmy.setTroopType(oa.troopType);
            campArmy.setTroopName(troop.getName());
            force = oa.maxForces;
            final int remainder = force % 3;
            force -= remainder;
            campArmy.setArmyHp(force);
            campArmy.setArmyHpOrg(force);
            campArmy.setTroopHp(oa.troopHp);
            campArmy.setMaxForces(force);
            num += force;
            if (general.getTacticId() > 0 && oa.maxForces >= campArmy.getMaxForces()) {
                campArmy.setTacticVal(1);
                if (campArmy.getSpecialGeneral().generalType == 7) {
                    campArmy.setTacticVal((int)campArmy.getSpecialGeneral().param);
                }
            }
            campArmy.setColumn(campArmy.getArmyHp() / oa.troopHp);
            campArmy.setStrategies(troop.getStrategyDefMap().get(terrainVal));
            List<StoreHouse> shList = dataGetter.getStoreHouseDao().getGeneralEquipList(campArmy.playerId, campArmy.generalId, 10);
            for (final StoreHouse psh : shList) {
                if (psh.getType() != 10) {
                    continue;
                }
                final EquipCoordinates equipCoordinates = dataGetter.getEquipCache().getEquipCoordinateByItemId(psh.getItemId());
                if (equipCoordinates != null) {
                    final int[] skills = { equipCoordinates.getPos1Skill(), equipCoordinates.getPos2Skill(), equipCoordinates.getPos3Skill(), equipCoordinates.getPos4Skill(), equipCoordinates.getPos5Skill(), equipCoordinates.getPos6Skill() };
                    final int lv = 5;
                    final int starNum = 4;
                    int[] array;
                    for (int length = (array = skills).length, j = 0; j < length; ++j) {
                        final int skillId = array[j];
                        final EquipSkillEffect equipSkillEffect = dataGetter.getEquipSkillEffectCache().getEquipSkillEffectByIdLV(skillId, lv);
                        if (equipSkillEffect != null) {
                            if (equipSkillEffect.getAttDef_B() != null) {
                                final AttDef_B attDef_B = campArmy.attDef_B;
                                attDef_B.ATT_B += starNum * equipSkillEffect.getAttDef_B().ATT_B;
                                final AttDef_B attDef_B2 = campArmy.attDef_B;
                                attDef_B2.DEF_B += starNum * equipSkillEffect.getAttDef_B().DEF_B;
                            }
                            final CampArmy campArmy2 = campArmy;
                            campArmy2.TACTIC_ATT += starNum * equipSkillEffect.getTACTIC_ATT();
                            final CampArmy campArmy3 = campArmy;
                            campArmy3.TACTIC_DEF += starNum * equipSkillEffect.getTACTIC_DEF();
                        }
                        else {
                            final Player player = dataGetter.getPlayerDao().read(campArmy.playerId);
                            ErrorSceneLog.getInstance().appendErrorMsg("equipSkillEffect is null.").append("skillId", skillId).append("lv", lv).appendPlayerId(campArmy.playerId).appendPlayerName(player.getPlayerName()).appendGeneralId(campArmy.generalId).appendGeneralName(((General)dataGetter.getGeneralCache().get((Object)campArmy.generalId)).getName()).flush();
                        }
                    }
                }
                else {
                    final Player player2 = dataGetter.getPlayerDao().read(campArmy.playerId);
                    ErrorSceneLog.getInstance().appendErrorMsg("equipCoordinates is null.").append("StoreHouse", psh.getVId()).append("psh.getItemId()", psh.getItemId()).appendPlayerId(campArmy.playerId).appendPlayerName(player2.getPlayerName()).appendGeneralId(campArmy.generalId).appendGeneralName(((General)dataGetter.getGeneralCache().get((Object)campArmy.generalId)).getName()).flush();
                }
            }
            shList = dataGetter.getStoreHouseDao().getGeneralEquipList(campArmy.playerId, campArmy.generalId, 1);
            for (final StoreHouse psh : shList) {
                if (psh.getRefreshAttribute() != null && !psh.getRefreshAttribute().isEmpty()) {
                    final String[] skills2 = psh.getRefreshAttribute().split(";");
                    String[] array2;
                    for (int length2 = (array2 = skills2).length, k = 0; k < length2; ++k) {
                        final String skill = array2[k];
                        final String[] idLv = skill.split(":");
                        if (idLv.length == 2) {
                            final int idid = Integer.parseInt(idLv[0]);
                            final int lv2 = Integer.parseInt(idLv[1]);
                            final EquipSkillEffect equipSkillEffect = dataGetter.getEquipSkillEffectCache().getEquipSkillEffectByIdLV(idid, lv2);
                            if (equipSkillEffect != null) {
                                if (equipSkillEffect.getAttDef_B() != null) {
                                    final AttDef_B attDef_B3 = campArmy.attDef_B;
                                    attDef_B3.ATT_B += equipSkillEffect.getAttDef_B().ATT_B;
                                    final AttDef_B attDef_B4 = campArmy.attDef_B;
                                    attDef_B4.DEF_B += equipSkillEffect.getAttDef_B().DEF_B;
                                }
                                final CampArmy campArmy4 = campArmy;
                                campArmy4.TACTIC_ATT += equipSkillEffect.getTACTIC_ATT();
                                final CampArmy campArmy5 = campArmy;
                                campArmy5.TACTIC_DEF += equipSkillEffect.getTACTIC_DEF();
                            }
                            else {
                                final Player player = dataGetter.getPlayerDao().read(campArmy.playerId);
                                ErrorSceneLog.getInstance().appendErrorMsg("equipSkillEffect is null.").append("id", idid).append("lv", lv2).appendPlayerId(campArmy.playerId).appendPlayerName(player.getPlayerName()).appendGeneralId(campArmy.generalId).appendGeneralName(((General)dataGetter.getGeneralCache().get((Object)campArmy.generalId)).getName()).flush();
                            }
                        }
                    }
                }
            }
            camp.add(campArmy);
            BattleSceneLog.getInstance().info("#batId:" + bat.getBattleId() + "_" + bat.getStartTime() + "#add:init#side:def" + "#playerId:" + campArmy.getPlayerId() + ":" + campArmy.isPhantom + "#general:" + campArmy.getGeneralId() + "#defSize:" + camp.size());
        }
        return num;
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
    public int getGeneralState() {
        return 4;
    }
    
    @Override
    public void sendTaskMessage(final int playerId, final int defId, final IDataGetter dataGetter) {
        TaskMessageHelper.sendHallsFightTaskMessage(playerId);
    }
    
    @Override
    public String getAttTargetName(final IDataGetter dataGetter, final Battle bat) {
        final Halls hall = dataGetter.getHallsCache().getHalls(bat.getDefBaseInfo().getId(), 1);
        return hall.getNameList();
    }
    
    @Override
    public void sendBattleInfo(final IDataGetter dataGetter, final Battle bat, final BattleAttacker battleAttacker) {
        final Player player = battleAttacker.attPlayer;
        if (!BattleConstant.officerBatMap.contains(bat.getDefBaseInfo().getId())) {
            final Halls halls = dataGetter.getHallsCache().getHalls(bat.defBaseInfo.getId(), 1);
            if (halls == null) {
                return;
            }
            final String msg = MessageFormatter.format(LocalMessages.MINE_START_BATTTLE_CHAT_INFO, new Object[] { ColorUtil.getSpecialColorMsg(player.getPlayerName()), ColorUtil.getSpecialColorMsg(BattleConstant.getBuildingName(halls, player.getForceId())) });
            dataGetter.getChatService().sendSystemChat("COUNTRY", player.getPlayerId(), player.getForceId(), msg, new ChatLink(3, bat.battleId));
        }
    }
    
    @Override
    public void countNpcReward(final boolean attWin, final IDataGetter dataGetter, final Battle bat, final BattleResult battleResult) {
    }
    
    @Override
    public void dealNextNpc(final boolean attWin, final IDataGetter dataGetter, final Battle bat, final BattleResult battleResult) {
        if (attWin) {
            battleResult.oMap = this.assignOfficer(dataGetter, bat);
        }
    }
    
    @Override
    public int battleRewardSave(final boolean attWin, final int playerId, final PlayerInfo pi, final Date date, final IDataGetter dataGetter, final BattleResult battleResult, final Battle bat) {
        return 0;
    }
    
    @Override
    public int getDefPlayerLevel(final IDataGetter dataGetter, final Battle bat, final RoundInfo roundInfo) {
        return roundInfo.defCampArmy.getPlayerLv();
    }
    
    @Override
    public int caculateRoundCopper(final IDataGetter dataGetter, final Battle bat, final int playerId, final double mAttOmega, final FightRewardCoe frc) {
        final int copper = (int)(mAttOmega * frc.getM());
        return copper;
    }
    
    @Override
    public void afterBat(final boolean attWin, final IDataGetter dataGetter, final Battle bat) {
        for (final Integer playerId : bat.getInBattlePlayers().keySet()) {
            TaskMessageHelper.sendOfficialMessage(playerId);
        }
    }
    
    @Override
    public void quitNewBattle(final CampArmy ca, final int battleSide, final Battle bat) {
        if (battleSide == 0) {
            return;
        }
        if (ca.getPlayerId() > 0 && !ca.isPhantom) {
            NewBattleManager.getInstance().quitBattle(bat, ca.getPlayerId(), ca.getGeneralId());
        }
    }
    
    @Override
    public void endQuitNewBattle(final CampArmy ca, final int battleSide, final Battle bat) {
        if (battleSide == 0) {
            return;
        }
        if (ca.getPlayerId() > 0 && !ca.isPhantom) {
            NewBattleManager.getInstance().quitBattle(bat, ca.getPlayerId(), ca.getGeneralId());
        }
    }
    
    @Override
    public void addViewType(final Player player, final Battle battle, final JsonDocument doc) {
        final int defId = battle.defBaseInfo.id;
        if (BattleConstant.officerBatMap.contains(defId)) {
            return;
        }
        if (player.getForceId() != battle.attBaseInfo.forceId && player.getForceId() != battle.defBaseInfo.forceId) {
            return;
        }
        for (final CampArmy temp : battle.getAttCamp()) {
            if (temp.getPlayerId() == player.getPlayerId()) {
                doc.createElement("viewType", 2);
                return;
            }
        }
        for (final CampArmy temp : battle.getDefCamp()) {
            if (temp.getPlayerId() == player.getPlayerId()) {
                doc.createElement("viewType", 3);
                return;
            }
        }
        doc.createElement("viewType", 1);
    }
    
    @Override
    public int getViewPlayer7ReportResult(final boolean attWin, final Battle battle, final Player player) {
        boolean isDefSide = false;
        for (final CampArmy temp : battle.getDefCamp()) {
            if (temp.getPlayerId() == player.getPlayerId()) {
                isDefSide = true;
                break;
            }
        }
        int result = 0;
        if (!attWin && isDefSide) {
            result = 1;
        }
        else {
            result = 2;
        }
        return result;
    }
}
