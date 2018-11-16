package com.reign.gcld.battle.scene;

import com.reign.util.*;
import com.reign.gcld.common.*;
import com.reign.gcld.general.domain.*;
import com.reign.gcld.scenario.common.*;
import com.reign.gcld.player.dto.*;
import com.reign.framework.json.*;
import com.reign.gcld.sdata.common.*;
import com.reign.gcld.player.domain.*;
import java.util.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.gcld.battle.common.*;

public class ScenarioEventBuilder extends Builder
{
    public ScenarioEventBuilder(final int battleType) {
        this.battleType = battleType;
    }
    
    @Override
    public Tuple<Boolean, String> attPermitCreate(final IDataGetter dataGetter, final int playerId, final int cityId) {
        final Tuple<Boolean, String> tuple = new Tuple();
        tuple.left = false;
        final ScenarioEvent scenarioEvent = dataGetter.getJuBenService().getEventByCityId(playerId, cityId);
        if (scenarioEvent == null) {
            tuple.right = LocalMessages.JUBEN_EVENT_NO_THIS_EVENT;
            return tuple;
        }
        final List<PlayerGeneralMilitary> pgmList = dataGetter.getPlayerGeneralMilitaryDao().getMilitaryList(playerId);
        boolean hasPgmInThisCity = false;
        for (final PlayerGeneralMilitary pgm : pgmList) {
            if (pgm.getJubenLoId() == cityId) {
                hasPgmInThisCity = true;
                break;
            }
        }
        if (!hasPgmInThisCity) {
            tuple.right = LocalMessages.CITY_EVENT_NO_PGM_IN_THIS_CITY;
            return tuple;
        }
        final Tuple<List<Integer>, Integer> tupleInner = scenarioEvent.getFightNpcList();
        if (tupleInner == null) {
            tuple.right = LocalMessages.JUBEN_EVENT_NOT_FIGHT_OR_CANNOT_FIGHT;
            return tuple;
        }
        tuple.left = true;
        tuple.right = "";
        return tuple;
    }
    
    @Override
    public Terrain getTerrain(final int playerId, final int cityId, final IDataGetter dataGetter) {
        final ScenarioEvent scenarioEvent = dataGetter.getJuBenService().getEventByCityId(playerId, cityId);
        if (scenarioEvent == null) {
            return null;
        }
        final Tuple<List<Integer>, Integer> tupleInner = scenarioEvent.getFightNpcList();
        return new Terrain(tupleInner.right, tupleInner.right, tupleInner.right);
    }
    
    @Override
    public byte[] getPrepareInfo(final IDataGetter dataGetter, final PlayerDto playerDto, final int cityId, final Battle battle, final int terrain) {
        final JsonDocument doc = new JsonDocument();
        doc.createElement("name", " ");
        final SoloCity soloCity = (SoloCity)dataGetter.getSoloCityCache().get((Object)cityId);
        doc.createElement("bat", 2000000 + soloCity.getSoloId());
        final List<PlayerGeneralMilitary> pgmList = dataGetter.getPlayerGeneralMilitaryDao().getMilitaryList(playerDto.playerId);
        final List<PlayerGeneralMilitary> pgmListInThisCity = new ArrayList<PlayerGeneralMilitary>();
        for (final PlayerGeneralMilitary pgm : pgmList) {
            if (pgm.getJubenLoId() == cityId) {
                pgmListInThisCity.add(pgm);
            }
        }
        final ScenarioEvent scenarioEvent = dataGetter.getJuBenService().getEventByCityId(playerDto.playerId, cityId);
        doc.appendJson(this.getAttGenerals(dataGetter, playerDto.playerId, pgmListInThisCity, playerDto.forceId, terrain));
        doc.appendJson(this.getDefGenerals(dataGetter, playerDto.playerId, scenarioEvent, terrain));
        return doc.toByte();
    }
    
    private byte[] getDefGenerals(final IDataGetter dataGetter, final int playerId, final ScenarioEvent scenarioEvent, final int terrain) {
        if (scenarioEvent == null) {
            return null;
        }
        final Tuple<List<Integer>, Integer> tupleInner = scenarioEvent.getFightNpcList();
        final JsonDocument doc = new JsonDocument();
        doc.startArray("defGenerals");
        General general = null;
        TroopTerrain gTerrain = null;
        Troop troop = null;
        if (tupleInner.left != null && ((List)tupleInner.left).size() > 0) {
            for (final Integer npc : (List)tupleInner.left) {
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
                doc.createElement("generalPic", ((General)dataGetter.getGeneralCache().get((Object)armyCach.getGeneralId())).getPic());
                if (general.getTacticId() != 0) {
                    final Tactic tactic = (Tactic)dataGetter.getTacticCache().get((Object)general.getTacticId());
                    if (tactic != null) {
                        doc.createElement("tacticName", tactic.getName());
                    }
                }
                doc.createElement("armyHp", armyCach.getArmyHp());
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
        doc.endArray();
        return doc.toByte();
    }
    
    @Override
    public int getBatInfo(final IDataGetter dataGetter, final int cityId) {
        final SoloCity soloCity = (SoloCity)dataGetter.getSoloCityCache().get((Object)cityId);
        return 2000000 + soloCity.getSoloId();
    }
    
    @Override
    public int getDefPlayerLevel(final IDataGetter dataGetter, final Battle bat, final RoundInfo roundInfo) {
        final int cityId = bat.defBaseInfo.getId();
        final int playerId = bat.attBaseInfo.id;
        final ScenarioEvent scenarioEvent = dataGetter.getJuBenService().getEventByCityId(playerId, cityId);
        if (scenarioEvent != null) {
            final Tuple<List<Integer>, Integer> tupleInner = scenarioEvent.getFightNpcList();
            final int armyId = ((List)tupleInner.left).get(0);
            final Army army = (Army)dataGetter.getArmyCache().get((Object)armyId);
            return army.getGeneralLv();
        }
        ErrorSceneLog.getInstance().appendErrorMsg("scenarioEvent is null").appendBattleId(bat.battleId).appendPlayerId(playerId).append("cityId", cityId).flush();
        return roundInfo.defCampArmy.playerLv;
    }
    
    @Override
    public Tuple<Boolean, String> canCreateBattle(final Player player, final int cityId, final IDataGetter dataGetter) {
        final Tuple<Boolean, String> tuple = new Tuple();
        tuple.left = false;
        final ScenarioEvent scenarioEvent = dataGetter.getJuBenService().getEventByCityId(player.getPlayerId(), cityId);
        if (scenarioEvent == null) {
            tuple.right = LocalMessages.JUBEN_EVENT_NO_THIS_EVENT;
            return tuple;
        }
        final List<PlayerGeneralMilitary> pgmList = dataGetter.getPlayerGeneralMilitaryDao().getMilitaryList(player.getPlayerId());
        boolean hasPgmInThisCity = false;
        for (final PlayerGeneralMilitary pgm : pgmList) {
            if (pgm.getJubenLoId() == cityId) {
                hasPgmInThisCity = true;
                break;
            }
        }
        if (!hasPgmInThisCity) {
            tuple.right = LocalMessages.CITY_EVENT_NO_PGM_IN_THIS_CITY;
            return tuple;
        }
        final Tuple<List<Integer>, Integer> tupleInner = scenarioEvent.getFightNpcList();
        if (tupleInner == null) {
            tuple.right = LocalMessages.JUBEN_EVENT_NOT_FIGHT_OR_CANNOT_FIGHT;
            return tuple;
        }
        tuple.left = true;
        return tuple;
    }
    
    @Override
    public boolean initDefCamp(final IDataGetter dataGetter, final BattleAttacker battleAttacker, final int cityId, final Battle bat) {
        final ScenarioEvent scenarioEvent = dataGetter.getJuBenService().getEventByCityId(bat.attBaseInfo.id, cityId);
        bat.scenarioEvent = scenarioEvent;
        final Tuple<List<Integer>, Integer> tupleInner = scenarioEvent.getFightNpcList();
        if (tupleInner == null || tupleInner.left == null) {
            return false;
        }
        final List<Integer> npcList = tupleInner.left;
        int allNum = 0;
        int curNum = 0;
        int id = 0;
        final int npcLost = 0;
        CampArmy campArmy = null;
        for (final Integer npcId : npcList) {
            id = bat.campNum.getAndIncrement();
            campArmy = copyArmyFromCach(npcId, npcLost, dataGetter, id, bat.terrainVal);
            allNum += campArmy.getArmyHpOrg();
            curNum += campArmy.getArmyHp();
            bat.defCamp.add(campArmy);
        }
        bat.defBaseInfo.setAllNum(allNum);
        bat.defBaseInfo.setNum(curNum);
        bat.defBaseInfo.setForceId(0);
        return false;
    }
    
    @Override
    public int getGeneralState() {
        return 21;
    }
    
    private static CampArmy copyArmyFromCach(final int npcId, final int npc_lost, final IDataGetter dataGetter, final int id, final int terrainType) {
        final CampArmy campArmy = new CampArmy();
        final Army armyCach = (Army)dataGetter.getArmyCache().get((Object)npcId);
        campArmy.setPlayerId(-1);
        campArmy.setPlayerName("NPC");
        campArmy.setForceId(0);
        campArmy.setPlayerLv(armyCach.getGeneralLv());
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
    public boolean conSumeFood(final int playerId, final int cityId, final IDataGetter dataGetter) {
        return true;
    }
    
    @Override
    public void sendBattleInfo(final IDataGetter dataGetter, final Battle bat, final BattleAttacker battleAttacker) {
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
    public void dealNextNpc(final boolean attWin, final IDataGetter dataGetter, final Battle bat, final BattleResult battleResult) {
        if (attWin) {
            dataGetter.getJuBenService().makeAChoiceToTriggerOperation(bat.attBaseInfo.id, bat.scenarioEvent.getSoloEvent().getId());
        }
    }
    
    @Override
    public void getExReward(final boolean attWin, final IDataGetter dataGetter, final PlayerInfo pi, final Battle bat) {
    }
    
    @Override
    public void afterBat(final boolean attWin, final IDataGetter dataGetter, final Battle bat) {
    }
    
    @Override
    public void inBattleInfo(final int playerId, final boolean inBattle) {
    }
    
    @Override
    public String getDefName(final IDataGetter dataGetter, final int cityId) {
        return "";
    }
    
    @Override
    public byte[] getBattleTopInfo(final IDataGetter dataGetter, final PlayerDto playerDto, final Battle battle) {
        final JsonDocument doc = new JsonDocument();
        doc.createElement("name", "");
        final SoloCity soloCity = (SoloCity)dataGetter.getSoloCityCache().get((Object)battle.defBaseInfo.id);
        doc.createElement("bat", 2000000 + soloCity.getSoloId());
        return doc.toByte();
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
    public String getAttTargetName(final IDataGetter dataGetter, final Battle battle) {
        return LocalMessages.JUBEN_EVENT_BATTLE_EVENT;
    }
    
    @Override
    public void dealBuilderBattleTask(final IDataGetter dataGetter, final int playerId, final int cityId) {
    }
}
