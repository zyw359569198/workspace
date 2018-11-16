package com.reign.gcld.battle.scene;

import com.reign.gcld.juben.service.*;
import com.reign.gcld.common.*;
import com.reign.gcld.juben.common.*;
import com.reign.gcld.player.domain.*;
import com.reign.gcld.player.dto.*;
import com.reign.framework.json.*;
import com.reign.gcld.general.domain.*;
import com.reign.gcld.duel.util.*;
import com.reign.gcld.duel.model.*;
import com.reign.gcld.world.common.*;
import com.reign.util.*;
import com.reign.gcld.chat.common.*;
import com.reign.gcld.battle.service.*;
import java.util.*;
import com.reign.gcld.battle.reward.*;
import com.reign.gcld.battle.common.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.gcld.sdata.common.*;

public class DuelBuilder extends Builder
{
    public DuelBuilder(final int battleType) {
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
    public Tuple<Boolean, String> attPermitCreate(final IDataGetter dataGetter, final int playerId, final int defId) {
        final Tuple<Boolean, String> tuple = this.conditionCheck(dataGetter, playerId, defId);
        if (!(boolean)tuple.left) {
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
    public Tuple<Boolean, String> canCreateBattle(final Player player, final int defId, final IDataGetter dataGetter) {
        final Tuple<Boolean, String> tuple = new Tuple();
        tuple.left = false;
        final JuBenDto juBenDto = JuBenManager.getInstance().getByPid(player.getPlayerId());
        if (juBenDto != null) {
            tuple.right = LocalMessages.IN_JUBEN_CANNT_BATTLE;
            return tuple;
        }
        return this.conditionCheck(dataGetter, player.getPlayerId(), defId);
    }
    
    @Override
    public Terrain getTerrain(final int playerId, final int defId, final IDataGetter dataGetter) {
        final List<Duel> duelList = dataGetter.getDuelsCache().getDuelList(playerId);
        for (final Duel duel : duelList) {
            if (duel.getPlayerId() == defId) {
                return duel.getTerrain();
            }
        }
        return null;
    }
    
    @Override
    public byte[] getPrepareInfo(final IDataGetter dataGetter, final PlayerDto playerDto, final int defId, final Battle battle, final int terrain) {
        final int playerId = playerDto.playerId;
        final JsonDocument doc = new JsonDocument();
        doc.createElement("bat", this.battleType * 100000 + 1);
        final List<PlayerGeneralMilitary> pgmList = dataGetter.getPlayerGeneralMilitaryDao().getMilitaryList(playerId);
        doc.appendJson(this.getAttTopLeft(dataGetter, playerId, battle));
        doc.appendJson(this.getDefTopRight(dataGetter, playerId, battle, defId));
        doc.appendJson(this.getAttGenerals(dataGetter, playerId, pgmList, playerDto.forceId, terrain));
        doc.appendJson(this.getDefGenerals(dataGetter, defId, terrain));
        return doc.toByte();
    }
    
    @Override
    public byte[] getDefTopRight(final IDataGetter dataGetter, final int playerId, final Battle battle, final int defId) {
        final JsonDocument doc = new JsonDocument();
        final Player player = dataGetter.getPlayerDao().read(defId);
        doc.createElement("npcId", player.getPlayerId());
        doc.createElement("npcName", player.getPlayerName());
        doc.createElement("npcPic", player.getPic());
        doc.createElement("npcLv", player.getPlayerLv());
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
    public boolean initDefCamp(final IDataGetter dataGetter, final BattleAttacker battleAttacker, final int defId, final Battle bat) {
        bat.defBaseInfo.setForceId(dataGetter.getPlayerDao().getForceId(defId));
        bat.defBaseInfo.setDefChiefId(defId);
        bat.defBaseInfo.setId2(defId);
        int defNum = 0;
        final List<PlayerGeneralMilitary> pgmList = dataGetter.getPlayerGeneralMilitaryDao().getMilitaryList(defId);
        final Player playerTemp = dataGetter.getPlayerDao().read(defId);
        for (int i = 0; i < pgmList.size(); ++i) {
            final PlayerGeneralMilitary pgm = pgmList.get(i);
            final CampArmy campArmy = this.copyArmyFromCach(playerTemp, pgm, dataGetter, bat);
            if (campArmy != null) {
                defNum += campArmy.getArmyHpOrg();
                bat.defCamp.add(campArmy);
                BattleSceneLog.getInstance().info("#batId:" + bat.getBattleId() + "_" + bat.getStartTime() + "#add:init#side:def" + "#playerId:" + campArmy.getPlayerId() + ":" + campArmy.isPhantom + "#general:" + campArmy.getGeneralId() + "#defSize:" + bat.defCamp.size());
            }
        }
        bat.defBaseInfo.setAllNum(defNum);
        bat.defBaseInfo.setNum(defNum);
        return false;
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
    public void countNpcReward(final boolean attWin, final IDataGetter dataGetter, final Battle bat, final BattleResult battleResult) {
        if (5 != dataGetter.getRankService().hasNationTasks(bat.attBaseInfo.forceId)) {
            return;
        }
        final int playerId = bat.attBaseInfo.id;
        final int forceId = bat.attBaseInfo.forceId;
        final int defId = bat.defBaseInfo.id;
        final int defForceId = bat.defBaseInfo.forceId;
        final long now = System.currentTimeMillis();
        final boolean not_in_scene = !bat.inSceneSet.contains(playerId);
        if (attWin) {
            int score = 0;
            final List<Duel> duelList = dataGetter.getDuelsCache().getDuelList(playerId);
            for (final Duel duel : duelList) {
                if (duel.getPlayerId() == defId) {
                    score = DuelUtil.getRewardScoreByIndex(duel.getIndex());
                    break;
                }
            }
            dataGetter.getRankService().updateKillNum(5, score, playerId, now);
            dataGetter.getIndividualTaskService().sendTaskMessage(new PlayerDto(playerId, forceId), score, "score");
            dataGetter.getRankService().updateKillNum(5, -1, defId, now);
            dataGetter.getIndividualTaskService().sendTaskMessage(new PlayerDto(defId, defForceId), -1, "score");
            dataGetter.getIndividualTaskService().sendTaskMessage(new PlayerDto(playerId, forceId), 1, "win");
            dataGetter.getPlayerBattleAttributeDao().addWinTimes(playerId);
            dataGetter.getDuelRecordsCache().put(playerId, new Record(defId, true, true, score));
            dataGetter.getDuelRecordsCache().put(defId, new Record(playerId, false, false, 1));
            if (not_in_scene) {
                final String msg = MessageFormatter.format(LocalMessages.DUEL_ATT_WIN, new Object[] { String.valueOf(WorldCityCommon.nationIdNameMapDot.get(defForceId)) + dataGetter.getPlayerDao().getPlayerName(defId), score });
                dataGetter.getChatService().sendSystemChat("SYS2ONE", playerId, 0, msg, null);
            }
            final String msg = MessageFormatter.format(LocalMessages.DUEL_DEF_FAIL, new Object[] { String.valueOf(WorldCityCommon.nationIdNameMapDot.get(forceId)) + dataGetter.getPlayerDao().getPlayerName(playerId), 1 });
            dataGetter.getChatService().sendSystemChat("SYS2ONE", defId, 0, msg, null);
        }
        else {
            dataGetter.getRankService().updateKillNum(5, bat.getAttBaseInfo().initNum, playerId, now);
            dataGetter.getIndividualTaskService().sendTaskMessage(new PlayerDto(defId, defForceId), bat.getAttBaseInfo().initNum, "score");
            dataGetter.getPlayerBattleAttributeDao().addFailTimes(playerId);
            dataGetter.getDuelRecordsCache().put(playerId, new Record(defId, true, false, bat.getAttBaseInfo().initNum));
            dataGetter.getDuelRecordsCache().put(defId, new Record(playerId, false, true, 0));
            if (not_in_scene) {
                final String msg2 = MessageFormatter.format(LocalMessages.DUEL_ATT_FAIL, new Object[] { String.valueOf(WorldCityCommon.nationIdNameMapDot.get(defForceId)) + dataGetter.getPlayerDao().getPlayerName(defId) });
                dataGetter.getChatService().sendSystemChat("SYS2ONE", playerId, 0, msg2, null);
            }
            final String msg2 = MessageFormatter.format(LocalMessages.DUEL_DEF_WIN, new Object[] { String.valueOf(WorldCityCommon.nationIdNameMapDot.get(forceId)) + dataGetter.getPlayerDao().getPlayerName(playerId) });
            dataGetter.getChatService().sendSystemChat("SYS2ONE", defId, 0, msg2, null);
        }
        dataGetter.getDuelsCache().remove(playerId);
    }
    
    @Override
    public void dealNextNpc(final boolean attWin, final IDataGetter dataGetter, final Battle bat, final BattleResult battleResult) {
        for (final CampArmy campArmy : bat.attCamp) {
            if (campArmy.isUpdateDB() && campArmy.armyHpKill > 0) {
                dataGetter.getKillRankService().dealKillrank(campArmy.armyHpKill, campArmy.getForceId(), dataGetter, campArmy.getPlayerId());
            }
        }
    }
    
    @Override
    public String getAttTargetName(final IDataGetter dataGetter, final Battle bat) {
        return dataGetter.getPlayerDao().getPlayerName(bat.getDefBaseInfo().getId());
    }
    
    @Override
    public int getGeneralState() {
        return 17;
    }
    
    @Override
    public byte[] getBattleTopInfo(final IDataGetter dataGetter, final PlayerDto playerDto, final Battle battle) {
        final JsonDocument doc = new JsonDocument();
        doc.createElement("bat", this.battleType * 100000 + 1);
        doc.appendJson(this.getAttTopLeft(dataGetter, playerDto.playerId, battle));
        doc.appendJson(this.getDefTopRight(dataGetter, playerDto.playerId, battle, battle.getDefBaseInfo().getId()));
        return doc.toByte();
    }
    
    @Override
    public void getExReward(final boolean attWin, final IDataGetter dataGetter, final PlayerInfo pi, final Battle bat) {
        final int playerId = pi.getPlayerId();
        final int defId = bat.defBaseInfo.id;
        final BattleDrop bd = new BattleDrop();
        bd.type = 1006;
        if (attWin) {
            int score = 0;
            final List<Duel> duelList = dataGetter.getDuelsCache().getDuelList(playerId);
            if (duelList != null) {
                for (final Duel duel : duelList) {
                    if (duel.getPlayerId() == defId) {
                        score = DuelUtil.getRewardScoreByIndex(duel.getIndex());
                        break;
                    }
                }
            }
            bd.num = score;
        }
        else {
            bd.num = bat.getAttBaseInfo().initNum;
        }
        pi.addDrop(bd);
    }
    
    @Override
    public void dealUniqueStaff(final IDataGetter dataGetter, final Battle bat, final int playerId, final int defId) {
        bat.getAttBaseInfo().initNum = bat.attCamp.size();
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
    public void inBattleInfo(final int playerId, final boolean inBattle) {
    }
    
    @Override
    public boolean canJoinBattle(final double armyHp, final double maxHp) {
        return armyHp >= maxHp;
    }
    
    @Override
    public void dealKillrank(final boolean isBarbarainInvade, final int gKillTotal, final Battle bat, final IDataGetter dataGetter, final int playerId) {
        final Builder barbarainBuilder = BuilderFactory.getInstance().getBuilder(14);
        barbarainBuilder.dealKillrank(false, gKillTotal, bat, dataGetter, playerId);
    }
    
    @Override
    public void roundReduceTroopSingle(final IDataGetter dataGetter, final Battle bat, final CampArmy campArmyA, final CampArmy campArmyB, final boolean isBeKill, final int lostA, final int lostB) {
        if (campArmyA.updateDB) {
            if (isBeKill) {
                dataGetter.getPlayerGeneralMilitaryDao().consumeForcesSetState1(campArmyA.getPlayerId(), campArmyA.generalId, lostA, new Date());
                dataGetter.getGeneralService().sendGmForcesReduce(campArmyA.getPlayerId(), campArmyA.generalId, lostA, lostB, bat.inSceneSet.contains(campArmyA.getPlayerId()), campArmyB.playerName, campArmyB.generalName, campArmyB.quality);
                dataGetter.getGeneralService().sendGmUpdate(campArmyA.getPlayerId(), campArmyA.generalId, bat.inSceneSet.contains(campArmyA.getPlayerId()));
                dataGetter.getKillRankService().dealKillrank(campArmyA.armyHpKill, campArmyA.getForceId(), dataGetter, campArmyA.getPlayerId());
            }
            else {
                if (lostA > 0) {
                    dataGetter.getPlayerGeneralMilitaryDao().consumeForces(campArmyA.getPlayerId(), campArmyA.generalId, lostA, new Date());
                }
                dataGetter.getGeneralService().sendGmForcesReduce(campArmyA.getPlayerId(), campArmyA.generalId, lostA, lostB, bat.inSceneSet.contains(campArmyA.getPlayerId()), campArmyB.playerName, campArmyB.generalName, campArmyB.quality);
            }
            final PlayerGeneralMilitary pgm = dataGetter.getPlayerGeneralMilitaryDao().getMilitary(campArmyA.getPlayerId(), campArmyA.generalId);
            BattleSceneLog.getInstance().appendLogMsg("general reduce force").appendPlayerName(campArmyA.playerName).appendGeneralName(campArmyA.generalName).append("is killed", isBeKill).append("reduce", lostA).append("now force", pgm.getForces()).appendClassName("Builder").appendMethodName("roundReduceTroopSingle").flush();
        }
    }
    
    @Override
    public void tacticUpdateDB(final IDataGetter dataGetter, final Battle bat, final LinkedList<CampArmy> campList, final CampArmy exeTacticCa, final CampArmy firstDefCa, final TacticInfo tacticInfoA) {
        if (tacticInfoA.reduceMap != null) {
            for (final CampArmy campArmy : tacticInfoA.reduceMap.keySet()) {
                if (campArmy != firstDefCa && campArmy.updateDB) {
                    final int reduce = tacticInfoA.reduceMap.get(campArmy);
                    if (campArmy.getArmyHpLoss() >= campArmy.getArmyHpOrg()) {
                        dataGetter.getPlayerGeneralMilitaryDao().consumeForcesSetState1(campArmy.getPlayerId(), campArmy.generalId, reduce, new Date());
                        dataGetter.getKillRankService().dealKillrank(campArmy.armyHpKill, campArmy.getForceId(), dataGetter, campArmy.getPlayerId());
                    }
                    else {
                        dataGetter.getPlayerGeneralMilitaryDao().consumeForces(campArmy.getPlayerId(), campArmy.generalId, reduce, new Date());
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
    
    private CampArmy copyArmyFromCach(final Player player, final PlayerGeneralMilitary pgm, final IDataGetter dataGetter, final Battle bat) {
        final CampArmy campArmy = new CampArmy();
        final General general = (General)dataGetter.getGeneralCache().get((Object)pgm.getGeneralId());
        campArmy.setSpecialGeneral(new SpecialGeneral(general.getGeneralSpecialInfo().generalType, general.getGeneralSpecialInfo().param, general.getGeneralSpecialInfo().param2));
        final Troop troop = dataGetter.getTroopCache().getTroop(general.getTroop(), player.getPlayerId());
        campArmy.setPlayerLv(player.getPlayerLv());
        campArmy.setPlayerId(player.getPlayerId());
        campArmy.setPlayerName(player.getPlayerName());
        campArmy.setForceId(player.getForceId());
        Builder.getTerrainValue(bat.terrainVal, troop, 0, campArmy);
        campArmy.setGeneralId(pgm.getGeneralId());
        campArmy.setTroopId(troop.getId());
        campArmy.setTroopSerial(troop.getSerial());
        campArmy.setTroopType(troop.getType());
        campArmy.setGeneralLv(pgm.getLv());
        campArmy.setPgmVId(0);
        campArmy.setArmyName(general.getName());
        campArmy.setGeneralName(general.getName());
        campArmy.setGeneralPic(general.getPic());
        campArmy.setQuality(general.getQuality());
        campArmy.setTroopName(troop.getName());
        campArmy.setStrength(pgm.getStrength(general.getStrength()));
        campArmy.setLeader(pgm.getLeader(general.getLeader()));
        campArmy.setTacicId(general.getTacticId());
        campArmy.setInRecruit(false);
        campArmy.setId(bat.campNum.getAndIncrement());
        int forces = dataGetter.getBattleDataCache().getMaxHp(pgm);
        final int remainder = forces % 3;
        forces -= remainder;
        campArmy.setArmyHp(forces);
        campArmy.setArmyHpOrg(forces);
        Builder.getAttDefHp(dataGetter, campArmy);
        if (general.getTacticId() > 0) {
            campArmy.setTacticVal(1);
            if (campArmy.getSpecialGeneral().generalType == 7) {
                campArmy.setTacticVal((int)campArmy.getSpecialGeneral().param);
            }
        }
        campArmy.setStrategies(troop.getStrategyDefMap().get(bat.terrainVal));
        return campArmy;
    }
    
    private boolean isDule(final IDataGetter dataGetter, final int playerId, final int defId) {
        final List<Duel> duelList = dataGetter.getDuelsCache().getDuelList(playerId);
        if (duelList == null || duelList.isEmpty()) {
            return false;
        }
        for (final Duel duel : duelList) {
            if (duel.getPlayerId() == defId) {
                return true;
            }
        }
        return false;
    }
    
    private Tuple<Boolean, String> conditionCheck(final IDataGetter dataGetter, final int playerId, final int defId) {
        final Player player = dataGetter.getPlayerDao().read(playerId);
        final PlayerDto playerDto = new PlayerDto(playerId);
        playerDto.forceId = player.getForceId();
        playerDto.playerLv = player.getPlayerLv();
        final Tuple<Boolean, String> tuple = DuelUtil.canDuel(playerDto, dataGetter.getRankService());
        if (!(boolean)tuple.left) {
            return tuple;
        }
        tuple.left = false;
        final Battle battle = NewBattleManager.getInstance().getBattleByBatType(player.getPlayerId(), 16);
        if (battle != null) {
            tuple.right = LocalMessages.BATTLE_INT_BATTLE;
            return tuple;
        }
        if (!this.isDule(dataGetter, playerId, defId)) {
            tuple.right = LocalMessages.BATTLE_NOT_IN_DULE_LIST;
            return tuple;
        }
        tuple.left = true;
        return tuple;
    }
    
    private byte[] getDefGenerals(final IDataGetter dataGetter, final int defId, final int terrain) {
        final JsonDocument doc = new JsonDocument();
        doc.startArray("defGenerals");
        final List<PlayerGeneralMilitary> pgmList = dataGetter.getPlayerGeneralMilitaryDao().getMilitaryList(defId);
        for (int i = 0; i < pgmList.size(); ++i) {
            final PlayerGeneralMilitary pgm = pgmList.get(i);
            final General general = (General)dataGetter.getGeneralCache().get((Object)pgm.getGeneralId());
            doc.startObject();
            doc.createElement("generalId", pgm.getGeneralId());
            doc.createElement("generalName", general.getName());
            doc.createElement("generalLv", pgm.getLv());
            final Player playerTemp = dataGetter.getPlayerDao().read(pgm.getPlayerId());
            final Troop troop = dataGetter.getTroopCache().getTroop(general.getTroop(), playerTemp.getPlayerId());
            doc.createElement("troopId", troop.getType());
            doc.createElement("troopType", general.getTroop());
            doc.createElement("armyHp", dataGetter.getBattleDataCache().getMaxHp(pgm));
            doc.createElement("quality", general.getQuality());
            doc.createElement("generalPic", general.getPic());
            if (general.getTacticId() != 0) {
                final Tactic tactic = (Tactic)dataGetter.getTacticCache().get((Object)general.getTacticId());
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
}
