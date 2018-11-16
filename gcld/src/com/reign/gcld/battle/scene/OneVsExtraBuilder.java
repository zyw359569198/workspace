package com.reign.gcld.battle.scene;

import com.reign.util.*;
import com.reign.gcld.juben.service.*;
import com.reign.gcld.battle.domain.*;
import com.reign.gcld.juben.common.*;
import com.reign.gcld.player.dto.*;
import com.reign.gcld.general.domain.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.gcld.sdata.common.*;
import com.reign.gcld.player.domain.*;
import java.util.*;
import com.reign.gcld.battle.common.*;
import com.reign.framework.json.*;
import com.reign.gcld.common.*;
import java.io.*;

public class OneVsExtraBuilder extends OneVsNpcBuilder
{
    public OneVsExtraBuilder(final int battleType) {
        super(battleType);
    }
    
    @Override
    public Tuple<Boolean, String> attPermitCreate(final IDataGetter dataGetter, final int playerId, final int defId) {
        final Tuple<Boolean, String> tuple = new Tuple();
        tuple.left = false;
        final PlayerArmyExtra playerArmyExtra = dataGetter.getPlayerArmyExtraDao().getArmyByArmyId(playerId, defId);
        if (playerArmyExtra == null || playerArmyExtra.getAttackable() != 1) {
            tuple.right = LocalMessages.AMIES_CANNOT_ATTACKABLE;
            return tuple;
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
    
    @Override
    public Terrain getTerrain(final int playerId, final int defId, final IDataGetter dataGetter) {
        final ArmiesExtra armiesExtra = (ArmiesExtra)dataGetter.getArmiesExtraCache().get((Object)defId);
        return new Terrain(armiesExtra.getTerrain(), armiesExtra.getTerrainEffectType(), armiesExtra.getTerrain());
    }
    
    @Override
    public byte[] getPrepareInfo(final IDataGetter dataGetter, final PlayerDto playerDto, final int defId, final Battle battle, final int terrain) {
        final int playerId = playerDto.playerId;
        final JsonDocument doc = new JsonDocument();
        final List<PlayerGeneralMilitary> pgmList = dataGetter.getPlayerGeneralMilitaryDao().getMilitaryList(playerId);
        final ArmiesExtra armiesExtra = (ArmiesExtra)dataGetter.getArmiesExtraCache().get((Object)defId);
        doc.createElement("name", armiesExtra.getName());
        doc.createElement("bat", 1200000 + armiesExtra.getPowerId());
        doc.appendJson(this.getAttTopLeft(dataGetter, playerId, battle));
        doc.appendJson(this.getDefTopRight(dataGetter, playerId, battle, armiesExtra));
        doc.appendJson(this.getAttGenerals(dataGetter, playerId, pgmList, playerDto.forceId, terrain));
        doc.appendJson(this.getDefGenerals(dataGetter, playerDto.playerId, armiesExtra, terrain));
        return doc.toByte();
    }
    
    public byte[] getDefGenerals(final IDataGetter dataGetter, final int playerId, final ArmiesExtra armiesExtra, final int terrain) {
        final JsonDocument doc = new JsonDocument();
        final Integer[] defNpcs = armiesExtra.getArmiesId();
        doc.startArray("defGenerals");
        General general = null;
        Troop troop = null;
        TroopTerrain gTerrain = null;
        final PlayerArmyExtra playerArmyExtra = dataGetter.getPlayerArmyExtraDao().getArmyByArmyId(playerId, armiesExtra.getId());
        String[] losts = null;
        if (playerArmyExtra.getNpcLost() == null || playerArmyExtra.getNpcLost().equals("")) {
            losts = new String[defNpcs.length];
            for (int i = 0; i < losts.length; ++i) {
                losts[i] = "0";
            }
        }
        else {
            losts = playerArmyExtra.getNpcLost().split(";");
            if (losts.length == 0 || defNpcs.length != losts.length) {
                ErrorSceneLog.getInstance().appendErrorMsg("player_army_reward\u8868npclost\u5f02\u5e38\uff01").appendClassName("OneVsRewardNpcBuilder").appendMethodName("initDefCamp").append("playerId", playerArmyExtra.getPlayerId()).append("armyId", playerArmyExtra.getArmyId()).append("v_id", playerArmyExtra.getVId()).flush();
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
                    if (general.getTacticId() != 0) {
                        final Tactic tactic = (Tactic)dataGetter.getTacticCache().get((Object)general.getTacticId());
                        if (tactic != null) {
                            doc.createElement("tacticName", tactic.getName());
                        }
                    }
                    if (i >= losts.length) {
                        doc.createElement("armyHp", armyCach.getArmyHp());
                    }
                    else {
                        final int lost = Integer.parseInt(losts[i]);
                        doc.createElement("armyHp", armyCach.getArmyHp() - lost);
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
            }
        }
        doc.endArray();
        return doc.toByte();
    }
    
    @Override
    public int getBatInfo(final IDataGetter dataGetter, final int defId) {
        final ArmiesExtra armiesExtra = (ArmiesExtra)dataGetter.getArmiesExtraCache().get((Object)defId);
        return 1200000 + armiesExtra.getPowerId();
    }
    
    @Override
    public Tuple<Boolean, String> canCreateBattle(final Player player, final int defId, final IDataGetter dataGetter) {
        final Tuple<Boolean, String> tuple = new Tuple();
        tuple.left = false;
        final PlayerArmyExtra playerArmyExtra = dataGetter.getPlayerArmyExtraDao().getArmyByArmyId(player.getPlayerId(), defId);
        if (playerArmyExtra == null || playerArmyExtra.getAttackable() != 1) {
            tuple.right = LocalMessages.AMIES_CANNOT_ATTACKABLE;
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
    
    public static int getMaxHp(final IDataGetter dataGetter, final int defId) {
        final ArmiesExtra defLegion = (ArmiesExtra)dataGetter.getArmiesExtraCache().get((Object)defId);
        final Integer[] defNpcs = defLegion.getArmiesId();
        int allNum = 0;
        for (int i = 0; i < defNpcs.length; ++i) {
            final Army armyCach = (Army)dataGetter.getArmyCache().get((Object)defNpcs[i]);
            allNum += armyCach.getArmyHp();
        }
        return allNum;
    }
    
    @Override
    public boolean initDefCamp(final IDataGetter dataGetter, final BattleAttacker battleAttacker, final int defId, final Battle bat) {
        final Player player = battleAttacker.attPlayer;
        final ArmiesExtra defLegion = (ArmiesExtra)dataGetter.getArmiesExtraCache().get((Object)defId);
        if (defLegion == null) {
            return false;
        }
        final int defForceId = defLegion.getChief();
        int allNum = 0;
        int curNum = 0;
        final Integer[] defNpcs = defLegion.getArmiesId();
        if (defNpcs == null || defNpcs.length == 0) {
            return false;
        }
        final PlayerArmyExtra playerArmyExtra = dataGetter.getPlayerArmyExtraDao().getArmyByArmyId(player.getPlayerId(), defId);
        if (playerArmyExtra == null) {
            ErrorSceneLog.getInstance().appendErrorMsg("\u5f02\u5e38\uff01\u73a9\u5bb6extra\u526f\u672c\u672a\u6389\u843d").appendClassName("OneVsExtraBuilder").appendMethodName("initDefCamp").append("playerId", player.getPlayerId()).append("defId", defId).flush();
        }
        String[] npcLosts = null;
        if (playerArmyExtra.getNpcLost() == null || playerArmyExtra.getNpcLost().equals("")) {
            npcLosts = new String[defNpcs.length];
            for (int i = 0; i < npcLosts.length; ++i) {
                npcLosts[i] = "0";
            }
        }
        else {
            npcLosts = playerArmyExtra.getNpcLost().split(";");
            if (npcLosts.length == 0 || defNpcs.length != npcLosts.length) {
                ErrorSceneLog.getInstance().appendErrorMsg("player_army_reward\u8868npclost\u5f02\u5e38\uff01").appendClassName("OneVsExtraBuilder").appendMethodName("initDefCamp").append("playerId", player.getPlayerId()).append("defId", defId).flush();
            }
        }
        int id = 0;
        int npcId = 0;
        int npcLost = 0;
        CampArmy campArmy = null;
        for (int j = 0; j < defNpcs.length; ++j) {
            id = bat.campNum.getAndIncrement();
            npcId = defNpcs[j];
            if (j >= npcLosts.length) {
                npcLost = 0;
            }
            else {
                npcLost = Integer.parseInt(npcLosts[j]);
            }
            campArmy = copyArmyFromCach(npcId, npcLost, dataGetter, id, bat.terrainVal, defLegion.getLevel());
            allNum += campArmy.getArmyHpOrg();
            curNum += campArmy.getArmyHp();
            bat.defCamp.add(campArmy);
            BattleSceneLog.getInstance().info("#batId:" + bat.getBattleId() + "_" + bat.getStartTime() + "#add:init#side:def" + "#playerId:" + campArmy.getPlayerId() + ":" + campArmy.isPhantom + "#general:" + campArmy.getGeneralId() + "#defSize:" + bat.defCamp.size());
        }
        bat.defBaseInfo.setAllNum(allNum);
        bat.defBaseInfo.setNum(curNum);
        bat.defBaseInfo.setForceId(defForceId);
        return false;
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
    public boolean conSumeFood(final int playerId, final int defId, final IDataGetter dataGetter) {
        return true;
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
    public void countNpcReward(final boolean attWin, final IDataGetter dataGetter, final Battle bat, final BattleResult battleResult) {
    }
    
    @Override
    public int getDefPlayerLevel(final IDataGetter dataGetter, final Battle bat, final RoundInfo roundInfo) {
        final int defId = bat.defBaseInfo.getId();
        return ((ArmiesExtra)dataGetter.getArmiesExtraCache().get((Object)defId)).getLevel();
    }
    
    @Override
    public void dealNextNpc(final boolean attWin, final IDataGetter dataGetter, final Battle bat, final BattleResult battleResult) {
        final int playerId = bat.attBaseInfo.id;
        final int armyId = bat.defBaseInfo.id;
        if (attWin) {
            final PlayerArmyExtra pae = dataGetter.getPlayerArmyExtraDao().getArmyByArmyId(playerId, armyId);
            if (pae.getWinNum() < 1) {
                dataGetter.getPlayerArmyExtraDao().updateAttackable(playerId, armyId, 0);
                final ArmiesExtra paeNext = dataGetter.getArmiesExtraCache().getNextArmiesExtra(armyId);
                if (paeNext != null) {
                    dataGetter.getPlayerArmyExtraDao().updateAttackable(playerId, paeNext.getId(), 1);
                }
            }
            dataGetter.getPlayerArmyExtraDao().addWinAttNumReset(playerId, armyId, 1, 1);
            final int extraPowerId = ((ArmiesExtra)dataGetter.getArmiesExtraCache().get((Object)armyId)).getPowerId();
            final List<PlayerArmyExtra> list = dataGetter.getPlayerArmyExtraDao().getArmiesByPowerIdAndAttackable(playerId, extraPowerId, 1);
            if (list == null || list.size() == 0) {
                dataGetter.getPlayerArmyExtraDao().deleteByPlayerIdPowerId(playerId, extraPowerId);
                dataGetter.getPlayerPowerDao().updateState(playerId, extraPowerId, 1);
                final int powerId = dataGetter.getPowerCache().fromExtraToArmy(extraPowerId);
                dataGetter.getPlayerDao().updatePowerId(playerId, powerId);
            }
            try {
                dataGetter.getBroadCastUtil().sendPassBonusBroadCast(playerId, ((ArmiesExtra)dataGetter.getArmiesExtraCache().get((Object)armyId)).getName());
            }
            catch (Exception e) {
                ErrorSceneLog.getInstance().appendErrorMsg("BroadCastUtil Exception").appendClassName("BroadCastUtil").appendMethodName("sendPassBonusBroadCast").append("playerId", playerId).append("armyId", armyId).flush();
            }
        }
        else {
            final ArmiesExtra armiesExtra = (ArmiesExtra)dataGetter.getArmiesExtraCache().get((Object)armyId);
            final StringBuilder sb = new StringBuilder();
            int serialNo = 0;
            Integer[] armiesId;
            for (int length = (armiesId = armiesExtra.getArmiesId()).length, i = 0; i < length; ++i) {
                final int id = armiesId[i];
                Builder.getCampArmyLost(dataGetter, sb, bat, id, serialNo);
                ++serialNo;
            }
            sb.replace(sb.length() - 1, sb.length(), "");
            dataGetter.getPlayerArmyExtraDao().updateNpcLostHp(playerId, armyId, sb.toString(), bat.defBaseInfo.num);
        }
    }
    
    @Override
    public void getExReward(final boolean attWin, final IDataGetter dataGetter, final PlayerInfo pi, final Battle bat) {
    }
    
    @Override
    public void afterBat(final boolean attWin, final IDataGetter dataGetter, final Battle bat) {
        Players.push(bat.getAttBaseInfo().id, PushCommand.PUSH_POWER, JsonBuilder.getSimpleJson("refresh", true));
    }
    
    @Override
    public String getDefName(final IDataGetter dataGetter, final int defId) {
        return ((ArmiesExtra)dataGetter.getArmiesExtraCache().get((Object)defId)).getName();
    }
    
    @Override
    public void sendBattleInfo(final IDataGetter dataGetter, final Battle bat, final BattleAttacker battleAttacker) {
    }
    
    @Override
    public byte[] getBattleTopInfo(final IDataGetter dataGetter, final PlayerDto playerDto, final Battle battle) {
        final JsonDocument doc = new JsonDocument();
        final ArmiesExtra armiesExtra = (ArmiesExtra)dataGetter.getArmiesExtraCache().get((Object)battle.getDefBaseInfo().getId());
        doc.createElement("name", armiesExtra.getName());
        doc.createElement("bat", 1200000 + armiesExtra.getPowerId());
        doc.appendJson(this.getAttTopLeft(dataGetter, playerDto.playerId, battle));
        doc.appendJson(this.getDefTopRight(dataGetter, playerDto.playerId, battle, armiesExtra));
        return doc.toByte();
    }
    
    public byte[] getDefTopRight(final IDataGetter dataGetter, final int playerId, final Battle battle, final ArmiesExtra armiesExtra) {
        final JsonDocument doc = new JsonDocument();
        final Army army = (Army)dataGetter.getArmyCache().get((Object)armiesExtra.getChief());
        final General general = (General)dataGetter.getGeneralCache().get((Object)army.getGeneralId());
        doc.createElement("npcId", army.getGeneralId());
        doc.createElement("npcName", armiesExtra.getName());
        doc.createElement("npcPic", general.getPic());
        if (battle != null) {
            doc.createElement("npcForces", battle.getDefBaseInfo().getNum());
            doc.createElement("npcMaxForces", battle.getDefBaseInfo().getAllNum());
        }
        return doc.toByte();
    }
    
    @Override
    public String getAttTargetName(final IDataGetter dataGetter, final Battle bat) {
        final ArmiesExtra armiesExtra = (ArmiesExtra)dataGetter.getArmiesExtraCache().get((Object)bat.getDefBaseInfo().getId());
        return armiesExtra.getName();
    }
}
