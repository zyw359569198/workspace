package com.reign.gcld.battle.scene;

import com.reign.util.*;
import com.reign.gcld.juben.service.*;
import com.reign.gcld.juben.common.*;
import com.reign.gcld.common.*;
import com.reign.gcld.world.common.*;
import com.reign.gcld.general.domain.*;
import com.reign.gcld.player.dto.*;
import com.reign.framework.json.*;
import com.reign.gcld.sdata.common.*;
import com.reign.gcld.player.domain.*;
import java.util.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.gcld.battle.common.*;

public class CityEventBuilder extends Builder
{
    public CityEventBuilder(final int battleType) {
        this.battleType = battleType;
    }
    
    @Override
    public Tuple<Boolean, String> attPermitCreate(final IDataGetter dataGetter, final int playerId, final int cityId) {
        final Tuple<Boolean, String> tuple = new Tuple();
        tuple.left = false;
        final Tuple<Boolean, String> tupleInter = this.canFightCityEvent(dataGetter, playerId, cityId);
        if (!(boolean)tupleInter.left) {
            tuple.right = tupleInter.right;
            return tuple;
        }
        final JuBenDto juBenDto = JuBenManager.getInstance().getByPid(playerId);
        if (juBenDto != null) {
            tuple.right = LocalMessages.IN_JUBEN_CANNT_BATTLE;
            return tuple;
        }
        tuple.left = true;
        return tuple;
    }
    
    private Tuple<Boolean, String> canFightCityEvent(final IDataGetter dataGetter, final int playerId, final int cityId) {
        final Tuple<Boolean, String> tuple = new Tuple();
        tuple.left = false;
        final PlayerDto playerDto = Players.getPlayer(playerId);
        final CityAttribute cityAttribute = CityEventManager.getInstance().cityAttributeMap.get(cityId);
        if (cityAttribute == null) {
            tuple.right = LocalMessages.CITY_EVENT_EVENT_NOT_EXIST;
            return tuple;
        }
        if (cityAttribute.eventType != 1) {
            tuple.right = LocalMessages.CITY_EVENT_TYPE_ERROR;
            return tuple;
        }
        if (cityAttribute.countDown == -1L) {
            if (cityAttribute.leftCount <= 0) {
                tuple.right = LocalMessages.CITY_EVENT_NO_LEFT_COUNT_OR_TIMEOUT;
                return tuple;
            }
        }
        else if (System.currentTimeMillis() > cityAttribute.countDown) {
            tuple.right = LocalMessages.CITY_EVENT_NO_LEFT_COUNT_OR_TIMEOUT;
            return tuple;
        }
        final Integer myCount = cityAttribute.playerIdCountMap.get(playerId);
        if (myCount != null && myCount >= cityAttribute.eachLimit) {
            tuple.right = LocalMessages.CITY_EVENT_NO_LEFT_COUNT_OR_TIMEOUT;
            return tuple;
        }
        if (cityAttribute.viewForceId != 0 && cityAttribute.viewForceId != playerDto.forceId) {
            tuple.right = LocalMessages.CITY_EVENT_INVISIBLE_TO_THIS_COUNTRY;
            return tuple;
        }
        boolean hasPgmInThis = false;
        final List<PlayerGeneralMilitary> pgmList = dataGetter.getPlayerGeneralMilitaryDao().getMilitaryList(playerId);
        for (final PlayerGeneralMilitary pgm : pgmList) {
            if (pgm.getLocationId() == cityId) {
                hasPgmInThis = true;
                break;
            }
        }
        if (!hasPgmInThis) {
            tuple.right = LocalMessages.CITY_EVENT_NO_PGM_IN_THIS_CITY;
            return tuple;
        }
        tuple.left = true;
        return tuple;
    }
    
    @Override
    public Terrain getTerrain(final int playerId, final int cityId, final IDataGetter dataGetter) {
        final WorldCity worldCity = (WorldCity)dataGetter.getWorldCityCache().get((Object)cityId);
        return new Terrain(worldCity.getTerrain(), worldCity.getTerrainEffectType(), worldCity.getTerrain());
    }
    
    @Override
    public byte[] getPrepareInfo(final IDataGetter dataGetter, final PlayerDto playerDto, final int cityId, final Battle battle, final int terrain) {
        final int playerId = playerDto.playerId;
        final JsonDocument doc = new JsonDocument();
        final List<PlayerGeneralMilitary> pgmList = dataGetter.getPlayerGeneralMilitaryDao().getMilitaryList(playerId);
        final List<PlayerGeneralMilitary> pgmListInThisCity = new ArrayList<PlayerGeneralMilitary>();
        for (final PlayerGeneralMilitary pgm : pgmList) {
            if (pgm.getLocationId() == cityId) {
                pgmListInThisCity.add(pgm);
            }
        }
        final CityAttribute cityAttribute = CityEventManager.getInstance().cityAttributeMap.get(cityId);
        final WdSjBo wdSjBo = (WdSjBo)dataGetter.getWdSjBoCache().get((Object)cityAttribute.eventTargetId);
        doc.createElement("name", wdSjBo.getName());
        doc.createElement("bat", 1700000);
        doc.appendJson(this.getAttTopLeft(dataGetter, playerId, battle));
        doc.appendJson(this.getDefTopRight(dataGetter, playerId, battle, wdSjBo));
        doc.appendJson(this.getAttGenerals(dataGetter, playerId, pgmListInThisCity, playerDto.forceId, terrain));
        doc.appendJson(this.getDefGenerals(dataGetter, playerDto.playerId, wdSjBo, terrain));
        return doc.toByte();
    }
    
    private byte[] getDefGenerals(final IDataGetter dataGetter, final int playerId, final WdSjBo wdSjBo, final int terrain) {
        final JsonDocument doc = new JsonDocument();
        final Integer[] defNpcs = wdSjBo.getArmiesId();
        doc.startArray("defGenerals");
        General general = null;
        Troop troop = null;
        TroopTerrain gTerrain = null;
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
        }
        doc.endArray();
        return doc.toByte();
    }
    
    @Override
    public int getBatInfo(final IDataGetter dataGetter, final int cityId) {
        return 1700000;
    }
    
    @Override
    public int getDefPlayerLevel(final IDataGetter dataGetter, final Battle bat, final RoundInfo roundInfo) {
        final int cityId = bat.defBaseInfo.getId();
        final CityAttribute cityAttribute = CityEventManager.getInstance().cityAttributeMap.get(cityId);
        if (cityAttribute != null) {
            final WdSjBo wdSjBo = (WdSjBo)dataGetter.getWdSjBoCache().get((Object)cityAttribute.eventTargetId);
            return wdSjBo.getLevel();
        }
        return 70;
    }
    
    @Override
    public Tuple<Boolean, String> canCreateBattle(final Player player, final int cityId, final IDataGetter dataGetter) {
        final Tuple<Boolean, String> result = new Tuple();
        result.left = false;
        final Tuple<Boolean, String> tupleInter = this.canFightCityEvent(dataGetter, player.getPlayerId(), cityId);
        if (!(boolean)tupleInter.left) {
            result.right = tupleInter.right;
            return tupleInter;
        }
        final JuBenDto juBenDto = JuBenManager.getInstance().getByPid(player.getPlayerId());
        if (juBenDto != null) {
            result.right = LocalMessages.IN_JUBEN_CANNT_BATTLE;
            return result;
        }
        result.left = true;
        result.right = "";
        return result;
    }
    
    @Override
    public boolean initDefCamp(final IDataGetter dataGetter, final BattleAttacker battleAttacker, final int cityId, final Battle bat) {
        final CityAttribute cityAttribute = CityEventManager.getInstance().cityAttributeMap.get(cityId);
        final WdSjBo wdSjBo = (WdSjBo)dataGetter.getWdSjBoCache().get((Object)cityAttribute.eventTargetId);
        if (wdSjBo == null) {
            return false;
        }
        int allNum = 0;
        int curNum = 0;
        final Integer[] defNpcs = wdSjBo.getArmiesId();
        if (defNpcs == null || defNpcs.length == 0) {
            return false;
        }
        int id = 0;
        int npcId = 0;
        final int npcLost = 0;
        CampArmy campArmy = null;
        for (int i = 0; i < defNpcs.length; ++i) {
            id = bat.campNum.getAndIncrement();
            npcId = defNpcs[i];
            campArmy = copyArmyFromCach(npcId, npcLost, dataGetter, id, bat.terrainVal, wdSjBo.getLevel());
            allNum += campArmy.getArmyHpOrg();
            curNum += campArmy.getArmyHp();
            bat.defCamp.add(campArmy);
            BattleSceneLog.getInstance().info("#batId:" + bat.getBattleId() + "_" + bat.getStartTime() + "#add:init#side:def" + "#playerId:" + campArmy.getPlayerId() + ":" + campArmy.isPhantom + "#general:" + campArmy.getGeneralId() + "#defSize:" + bat.defCamp.size());
        }
        bat.defBaseInfo.setAllNum(allNum);
        bat.defBaseInfo.setNum(curNum);
        bat.defBaseInfo.setForceId(0);
        return false;
    }
    
    @Override
    public int getGeneralState() {
        return 18;
    }
    
    private static CampArmy copyArmyFromCach(final int npcId, final int npc_lost, final IDataGetter dataGetter, final int id, final int terrainType, final int npcLv) {
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
        final CityAttribute cityAttribute = CityEventManager.getInstance().cityAttributeMap.get(cityId);
        final WdSjBo wdSjBo = (WdSjBo)dataGetter.getWdSjBoCache().get((Object)cityAttribute.eventTargetId);
        return wdSjBo.getName();
    }
    
    @Override
    public byte[] getBattleTopInfo(final IDataGetter dataGetter, final PlayerDto playerDto, final Battle battle) {
        final JsonDocument doc = new JsonDocument();
        final int cityId = battle.getDefBaseInfo().getId();
        final CityAttribute cityAttribute = CityEventManager.getInstance().cityAttributeMap.get(cityId);
        final WdSjBo wdSjBo = (WdSjBo)dataGetter.getWdSjBoCache().get((Object)cityAttribute.eventTargetId);
        doc.createElement("name", wdSjBo.getName());
        doc.createElement("bat", 1700000);
        doc.appendJson(this.getAttTopLeft(dataGetter, playerDto.playerId, battle));
        doc.appendJson(this.getDefTopRight(dataGetter, playerDto.playerId, battle, wdSjBo));
        return doc.toByte();
    }
    
    public byte[] getDefTopRight(final IDataGetter dataGetter, final int playerId, final Battle battle, final WdSjBo wdSjBo) {
        final JsonDocument doc = new JsonDocument();
        final Army army = (Army)dataGetter.getArmyCache().get((Object)wdSjBo.getChief());
        final General general = (General)dataGetter.getGeneralCache().get((Object)army.getGeneralId());
        doc.createElement("npcId", army.getGeneralId());
        doc.createElement("npcName", wdSjBo.getName());
        doc.createElement("npcPic", general.getPic());
        doc.createElement("npcLv", wdSjBo.getLevel());
        if (battle != null) {
            doc.createElement("npcForces", battle.getDefBaseInfo().getNum());
            doc.createElement("npcMaxForces", battle.getDefBaseInfo().getAllNum());
        }
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
        final int cityId = battle.getDefBaseInfo().getId();
        final CityAttribute cityAttribute = CityEventManager.getInstance().cityAttributeMap.get(cityId);
        final WdSjBo wdSjBo = (WdSjBo)dataGetter.getWdSjBoCache().get((Object)cityAttribute.eventTargetId);
        return wdSjBo.getName();
    }
    
    @Override
    public void dealBuilderBattleTask(final IDataGetter dataGetter, final int playerId, final int cityId) {
        try {
            final CityAttribute cityAttribute = CityEventManager.getInstance().cityAttributeMap.get(cityId);
            cityAttribute.addPlayerCount(playerId, 0);
            CityEventManager.getInstance().pushCityEventChangeInfo(cityId);
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("CityEventBuilder.dealBuilderBattleTask get Exception. cityId:" + cityId, e);
        }
    }
}
