package com.reign.gcld.battle.scene;

import com.reign.gcld.general.domain.*;
import com.reign.util.*;
import com.reign.gcld.juben.service.*;
import com.reign.gcld.juben.common.*;
import com.reign.gcld.player.dto.*;
import com.reign.gcld.player.domain.*;
import com.reign.gcld.sdata.common.*;
import java.util.concurrent.atomic.*;
import com.reign.gcld.common.util.*;
import com.reign.gcld.sdata.domain.*;
import java.util.*;
import com.reign.gcld.battle.common.*;
import com.reign.gcld.battle.domain.*;
import com.reign.gcld.player.common.*;
import com.reign.gcld.battle.service.*;
import com.reign.gcld.world.common.*;
import com.reign.framework.json.*;
import com.reign.gcld.common.*;

public class ManyVsNpcBuilder extends Builder
{
    public ManyVsNpcBuilder(final int battleType) {
        this.battleType = battleType;
    }
    
    @Override
    public void quitNewBattle(final CampArmy ca, final int battleSide, final Battle bat) {
    }
    
    @Override
    public void endCampsDeal(final IDataGetter dataGetter, final Battle bat, final BattleResult battleResult, final boolean attWin) {
        this.endQuitNewBattle(1, bat);
        int i = 1;
        for (final CampArmy ca : bat.attCamp) {
            BattleSceneLog.getInstance().info("#batId:" + bat.getBattleId() + "_" + bat.getStartTime() + "#quit:" + (attWin ? "win" : "loss") + "#side:att" + "#playerId:" + ca.getPlayerId() + ":" + ca.isPhantom + "#general:" + ca.getGeneralId() + "#attSize:" + (bat.attCamp.size() - i));
            ++i;
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
    
    private void endQuitNewBattle(final int battleSide, final Battle bat) {
        for (final Integer playerId : bat.inBattlePlayers.keySet()) {
            if (playerId <= 0) {
                continue;
            }
            NewBattleManager.getInstance().quitBattle(playerId, bat.getBattleId());
        }
    }
    
    @Override
    public Tuple<Boolean, String> canJoinBattle(final Player player, final List<PlayerGeneralMilitary> pgmList, final IDataGetter dataGetter, final Battle bat) {
        final Tuple<Boolean, String> tuple = new Tuple();
        tuple.left = false;
        final int defId = bat.getDefBaseInfo().getId();
        final Armies armies = (Armies)dataGetter.getArmiesCache().get((Object)defId);
        if (armies == null) {
            tuple.left = false;
            tuple.right = LocalMessages.T_COMM_10011;
        }
        final PlayerArmy playerArmy = dataGetter.getPlayerArmyDao().getPlayerArmy(player.getPlayerId(), defId);
        if (playerArmy == null || playerArmy.getAttackable() != 1) {
            tuple.left = false;
            tuple.right = LocalMessages.AMIES_CANNOT_ATTACKABLE;
            return tuple;
        }
        if (player.getPlayerLv() < armies.getLevel()) {
            tuple.left = false;
            tuple.right = MessageFormatter.format(LocalMessages.BATTLE_NO_ENOUGH_LEVEL, new Object[] { armies.getLevel() });
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
    public Battle existBattle(final IDataGetter dataGetter, final Player player, final int defId) {
        final Battle battle = NewBattleManager.getInstance().getBattleByParm(this.battleType, player.getForceId(), defId);
        return battle;
    }
    
    @Override
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
        doc.appendJson(this.getDefGenerals(dataGetter, battle, armies, terrain));
        return doc.toByte();
    }
    
    public byte[] getDefGenerals(final IDataGetter dataGetter, final Battle battle, final Armies armies, final int terrain) {
        if (battle != null) {
            final JsonDocument doc = new JsonDocument();
            doc.startArray("defGenerals");
            for (final CampArmy ca : battle.defCamp) {
                doc.startObject();
                doc.createElement("generalId", ca.generalId);
                doc.createElement("generalName", ca.generalName);
                doc.createElement("att", ca.attEffect);
                doc.createElement("generalLv", ca.generalLv);
                doc.createElement("troopId", ca.troopType);
                final Troop troop = (Troop)dataGetter.getTroopCache().get((Object)ca.troopId);
                doc.createElement("troopType", troop.getSerial());
                doc.createElement("generalPic", ca.generalPic);
                doc.createElement("armyHp", ca.armyHpOrg - ca.armyHpLoss);
                if (ca.tacicId != 0) {
                    final Tactic tactic = (Tactic)dataGetter.getTacticCache().get((Object)ca.tacicId);
                    if (tactic != null) {
                        doc.createElement("tacticName", tactic.getName());
                    }
                }
                final TroopTerrain gTerrain = troop.getTerrains().get(terrain);
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
            doc.endArray();
            return doc.toByte();
        }
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
            final Tactic tactic3 = (Tactic)dataGetter.getTacticCache().get((Object)general.getTacticId());
            if (tactic3 != null) {
                doc.createElement("tacticName", tactic3.getName());
            }
        }
        doc.createElement("armyHp", army.getArmyHp());
        gTerrain = troop.getTerrains().get(terrain);
        if (gTerrain != null && gTerrain.getDefEffect() > 0) {
            doc.createElement("terrainAdd", gTerrain.getDefEffect());
            doc.createElement("terrainQ", gTerrain.getDefQuality());
        }
        final List<TerrainStrategySpecDto> tssList3 = troop.getTsstList();
        if (tssList3 != null && tssList3.size() > 0) {
            final List<Integer> tssIds3 = new LinkedList<Integer>();
            for (final TerrainStrategySpecDto tss3 : tssList3) {
                if (tss3.terrainId == terrain && tss3.show == 1) {
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
        doc.endArray();
        return doc.toByte();
    }
    
    @Override
    public Tuple<Boolean, String> attPermitCreate(final IDataGetter dataGetter, final int playerId, final int defId) {
        final Player player = dataGetter.getPlayerDao().read(playerId);
        final Tuple<Boolean, String> tuple = new Tuple();
        tuple.left = false;
        if (defId != 0) {
            final PlayerArmy playerArmy = dataGetter.getPlayerArmyDao().getPlayerArmy(playerId, defId);
            if (playerArmy == null || playerArmy.getAttackable() != 1) {
                tuple.right = LocalMessages.AMIES_CANNOT_ATTACKABLE;
                return tuple;
            }
            final Armies armies = (Armies)dataGetter.getArmiesCache().get((Object)defId);
            if (armies.getType() == 3 && !BattleConstant.isInTime(BattleConstant.CAN_BATTLESTART_HOUR, BattleConstant.CAN_BATTLE_END_HOUR)) {
                tuple.right = LocalMessages.BATTLE_CANNOT_ATTACKABLE_NOT_TIME;
                return tuple;
            }
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
    
    @Override
    public Tuple<Boolean, String> canCreateBattle(final Player player, final int defId, final IDataGetter dataGetter) {
        final Tuple<Boolean, String> tuple = new Tuple();
        tuple.left = true;
        final Armies armies = (Armies)dataGetter.getArmiesCache().get((Object)defId);
        if (armies == null) {
            tuple.left = false;
            tuple.right = LocalMessages.T_COMM_10011;
        }
        final JuBenDto juBenDto = JuBenManager.getInstance().getByPid(player.getPlayerId());
        if (juBenDto != null) {
            tuple.right = LocalMessages.IN_JUBEN_CANNT_BATTLE;
            return tuple;
        }
        final PlayerArmy playerArmy = dataGetter.getPlayerArmyDao().getPlayerArmy(player.getPlayerId(), defId);
        if (playerArmy == null || playerArmy.getAttackable() != 1) {
            tuple.left = false;
            tuple.right = LocalMessages.AMIES_CANNOT_ATTACKABLE;
            return tuple;
        }
        if (player.getPlayerLv() < armies.getLevel()) {
            tuple.left = false;
            tuple.right = MessageFormatter.format(LocalMessages.BATTLE_NO_ENOUGH_LEVEL, new Object[] { armies.getLevel() });
        }
        if (armies.getType() == 3 && !BattleConstant.isInTime(BattleConstant.CAN_BATTLESTART_HOUR, BattleConstant.CAN_BATTLE_END_HOUR)) {
            tuple.left = false;
            tuple.right = LocalMessages.BATTLE_CANNOT_ATTACKABLE_NOT_TIME;
        }
        return tuple;
    }
    
    @Override
    public String getBattleId(final IDataGetter dataGetter, final Player player, final int defId) {
        return NewBattleManager.getBattleId(this.battleType, player.getForceId(), defId);
    }
    
    @Override
    public LinkedList<CampArmy> addAttNpc(final IDataGetter dataGetter, final int num, final BaseInfo attbaseInfo, final LinkedList<CampArmy> camps, final AtomicInteger campNum, final Battle bat) {
        if (camps.size() <= 0) {
            return null;
        }
        boolean add = false;
        final Player player = dataGetter.getPlayerDao().read(attbaseInfo.id);
        if (num > 0 && attbaseInfo.getNpcNum() < 2) {
            if (player.getPlayerLv() <= 25) {
                if (num % 3 == 0) {
                    add = true;
                }
            }
            else if (num % 8 == 0) {
                add = true;
            }
        }
        if (add) {
            attbaseInfo.setNpcNum(attbaseInfo.getNpcNum() + 1);
            final int chooseNum = dataGetter.getTavernService().getMaxGeneralNum(player.getPlayerId(), player.getPlayerLv(), 2);
            final String npcName = dataGetter.getRandomNamer().getRandomName();
            int id = 0;
            final LinkedList<CampArmy> campChange = new LinkedList<CampArmy>();
            int addNum = 0;
            final Set<Integer> gSet = new HashSet<Integer>();
            for (int i = 0; i < chooseNum; ++i) {
                final int index = WebUtil.nextInt(camps.size());
                final CampArmy campArmy = camps.get(index);
                id = campNum.getAndIncrement();
                final CampArmy copyCa = this.copyCampArmyInfo(dataGetter, campArmy, id, npcName, attbaseInfo.getNpcNum(), gSet);
                camps.add(copyCa);
                BattleSceneLog.getInstance().info("#batId:" + bat.getBattleId() + "_" + bat.getStartTime() + "#add:manyNpc#side:att" + "#playerId:" + copyCa.getPlayerId() + ":" + copyCa.isPhantom + "#general:" + copyCa.getGeneralId() + "#attSize:" + camps.size());
                campChange.add(copyCa);
                addNum += copyCa.getArmyHp();
            }
            attbaseInfo.setAllNum(attbaseInfo.getAllNum() + addNum);
            attbaseInfo.setNum(attbaseInfo.getNum() + addNum);
            return campChange;
        }
        return null;
    }
    
    private CampArmy copyCampArmyInfo(final IDataGetter dataGetter, final CampArmy campArmy, final int id, final String playerName, final int npcNum, final Set<Integer> gSet) {
        final CampArmy copyCa = new CampArmy();
        copyCa.setId(id);
        copyCa.setPlayerId(-1000 - npcNum);
        copyCa.setPlayerName(playerName);
        copyCa.setPlayerLv(campArmy.getPlayerLv());
        copyCa.setAttEffect(campArmy.getAttEffect());
        copyCa.setDefEffect(campArmy.getDefEffect());
        copyCa.setBdEffect(campArmy.getBdEffect());
        copyCa.setForceId(campArmy.getForceId());
        General general = null;
        for (int i = 10; i >= -10; --i) {
            general = (General)dataGetter.getGeneralCache().get((Object)(campArmy.getGeneralId() + i));
            if (general != null && general.getType() == 2 && !gSet.contains(general.getId())) {
                break;
            }
        }
        campArmy.setSpecialGeneral(new SpecialGeneral(general.getGeneralSpecialInfo().generalType, general.getGeneralSpecialInfo().param, general.getGeneralSpecialInfo().param2));
        gSet.add(general.getId());
        copyCa.setPgmVId(0);
        copyCa.setArmyName(general.getName());
        copyCa.setGeneralId(general.getId());
        copyCa.setSpecialGeneral(new SpecialGeneral(general.getGeneralSpecialInfo().generalType, general.getGeneralSpecialInfo().param, general.getGeneralSpecialInfo().param2));
        copyCa.setGeneralName(general.getName());
        copyCa.setGeneralLv(campArmy.getGeneralLv());
        copyCa.setGeneralPic(general.getPic());
        copyCa.setQuality(general.getQuality());
        copyCa.setTacicId(general.getTacticId());
        if (general.getTacticId() > 0) {
            copyCa.setTacticVal(1);
            if (campArmy.getSpecialGeneral().generalType == 7) {
                campArmy.setTacticVal((int)campArmy.getSpecialGeneral().param);
            }
        }
        final int troopId = (campArmy.getTroopId() + WebUtil.nextInt(15)) % 15;
        Troop troop = (Troop)dataGetter.getTroopCache().get((Object)troopId);
        for (int i = 2; i >= -2 && troop != null; --i) {
            troop = (Troop)dataGetter.getTroopCache().get((Object)(troopId + i));
            if (troop != null) {
                break;
            }
        }
        if (troop != null) {
            copyCa.setTroopId(troop.getId());
            campArmy.setTroopSerial(troop.getSerial());
            copyCa.setTroopType(troop.getType());
            copyCa.setTroopName(troop.getName());
        }
        else {
            copyCa.setTroopId(campArmy.getTroopId());
            copyCa.setTroopSerial(campArmy.getTroopSerial());
            copyCa.setTroopType(campArmy.getTroopType());
            copyCa.setTroopName(campArmy.getTroopName());
        }
        copyCa.setStrength(campArmy.getStrength());
        copyCa.setLeader(campArmy.getLeader());
        int armyHpOrg = campArmy.getArmyHpOrg();
        int remainder = armyHpOrg % 3;
        armyHpOrg -= remainder;
        copyCa.setArmyHp(armyHpOrg);
        copyCa.setArmyHpOrg(armyHpOrg);
        final int column = dataGetter.getBattleDataCache().getColumNum(campArmy.getPlayerId());
        int troopHp = campArmy.getMaxForces() / column;
        remainder = troopHp % 3;
        troopHp += 3 - remainder;
        copyCa.setTroopHp(troopHp);
        copyCa.setMaxForces(campArmy.getMaxForces());
        copyCa.setInRecruit(false);
        copyCa.setColumn(column);
        copyCa.strategies = campArmy.getStrategies();
        return copyCa;
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
    public boolean conSumeFood(final int playerId, final int defId, final IDataGetter dataGetter) {
        final Armies armies = (Armies)dataGetter.getArmiesCache().get((Object)defId);
        return armies.getFoodConsume() == 0 || dataGetter.getPlayerResourceDao().consumeFood(playerId, armies.getFoodConsume(), "\u526f\u672c\u6218\u6597\u6d88\u8017\u7cae\u98df");
    }
    
    @Override
    public void updateGeneralDB(final IDataGetter dataGetter, final Battle bat, final CampArmy ca, final boolean attWin) {
        final int state = 1;
        dataGetter.getPlayerGeneralMilitaryDao().restartRecruit(ca.getPlayerId(), ca.getGeneralId(), state, new Date());
        dataGetter.getGeneralService().sendGmUpdate(ca.getPlayerId(), ca.getGeneralId(), bat.inSceneSet.contains(ca.getPlayerId()));
    }
    
    @Override
    public int getDefPlayerLevel(final IDataGetter dataGetter, final Battle bat, final RoundInfo roundInfo) {
        final int defId = bat.defBaseInfo.getId();
        return ((Armies)dataGetter.getArmiesCache().get((Object)defId)).getLevel();
    }
    
    @Override
    public void getExReward(final boolean attWin, final IDataGetter dataGetter, final PlayerInfo pi, final Battle bat) {
        if (!attWin) {
            return;
        }
        final Armies armies = (Armies)dataGetter.getArmiesCache().get((Object)bat.defBaseInfo.getId());
        final FightRewardCoe frc = (FightRewardCoe)dataGetter.getFightRewardCoeCache().get((Object)this.getFightRewardCoeId(dataGetter, bat));
        final int selfLevel = dataGetter.getPlayerDao().read(pi.getPlayerId()).getPlayerLv();
        final int rivalLevel = armies.getLevel();
        final double L = Builder.getLevelDifferCoe(frc, selfLevel, rivalLevel);
        double rewardDouble = 1.0;
        final PlayerBattleAttribute pba = dataGetter.getPlayerBattleAttributeDao().read(pi.getPlayerId());
        if (pba != null && pba.getSupportTime() != null && pba.getSupportTime().getTime() > System.currentTimeMillis()) {
            if (pba.getType() == 1) {
                rewardDouble = 1.5;
            }
            else if (pba.getType() == 2) {
                rewardDouble = 2.0;
            }
        }
        final int addmExp = (int)(armies.getExpReward() * L * rewardDouble);
        final String debug = Configuration.getProperty("gcld.battle.caculate.debug");
        if (debug.equals("1")) {
            final StringBuilder sb = new StringBuilder();
            sb.append("ExReward.").append(" | ").append("battleId:" + bat.getBattleId()).append(" | ").append("playerId:" + pi.playerId).append(" | ").append("selfLevel:" + selfLevel).append(" | ").append("rivalLevel:" + rivalLevel).append(" | ").append("armies:" + armies.getId()).append(" | ").append("addmExp:" + addmExp).append(" | ");
            BattleSceneLog.getInstance().debug(sb);
        }
        try {
            final AddExpInfo addExpInfo = dataGetter.getPlayerService().updateExpAndPlayerLevel(pi.playerId, addmExp, "\u6218\u6597\u589e\u52a0\u989d\u5916\u7ecf\u9a8c");
            final boolean upLv = addExpInfo.upLv;
            final BattleDrop battleDrop = new BattleDrop();
            battleDrop.type = 5;
            battleDrop.num = addExpInfo.addExp;
            pi.addDrop(battleDrop);
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().appendErrorMsg("ManyVsNpcBuilder getExReward \u589e\u52a0\u73a9\u5bb6\u7b49\u7ea7\u7ecf\u9a8c Exception").appendClassName("ManyVsNpcBuilder").appendMethodName("getExReward").append("PlayerId", pi.playerId).append("mExp", addmExp).flush();
            ErrorSceneLog.getInstance().error("ManyVsNpcBuilder getExReward \u589e\u52a0\u73a9\u5bb6\u7b49\u7ea7\u7ecf\u9a8c Exception", e);
        }
    }
    
    @Override
    public String getDefName(final IDataGetter dataGetter, final int defId) {
        return ((Armies)dataGetter.getArmiesCache().get((Object)defId)).getName();
    }
    
    @Override
    public void sendBattleInfo(final IDataGetter dataGetter, final Battle bat, final BattleAttacker battleAttacker) {
        final Player player = battleAttacker.attPlayer;
        String[] strs = PowerService.getBatInfo(bat.getDefBaseInfo().getId());
        if (strs == null) {
            strs = new String[5];
            PowerService.batInfoMap.put(bat.getDefBaseInfo().getId(), strs);
        }
        final int index = (PowerService.indexs[bat.getDefBaseInfo().getId()] + 1) % 5;
        PowerService.indexs[bat.getDefBaseInfo().getId()] = index;
        final StringBuilder sb = new StringBuilder();
        sb.append(bat.getAttBaseInfo().getForceId()).append("#");
        sb.append(WorldCityCommon.nationIdNameMapDot.get(bat.getAttBaseInfo().getForceId())).append(player.getPlayerName());
        strs[index] = sb.toString();
    }
    
    @Override
    public void afterBat(final boolean attWin, final IDataGetter dataGetter, final Battle bat) {
        try {
            final String[] strs = PowerService.getBatInfo(bat.getDefBaseInfo().getId());
            if (strs != null) {
                for (int i = 0; i < strs.length; ++i) {
                    if (strs[i] != null) {
                        final Player player = dataGetter.getPlayerDao().read(bat.attBaseInfo.getId());
                        if (player != null) {
                            if (strs[i].endsWith("\u25aa" + player.getPlayerName())) {
                                strs[i] = null;
                            }
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("ManyVsNpcBuilder afterBat " + e);
        }
        Players.push(bat.getAttBaseInfo().id, PushCommand.PUSH_POWER, JsonBuilder.getSimpleJson("refresh", true));
    }
}
