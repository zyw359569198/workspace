package com.reign.gcld.battle.scene;

import com.reign.util.*;
import com.reign.gcld.battle.service.*;
import com.reign.gcld.juben.service.*;
import com.reign.gcld.common.*;
import com.reign.gcld.player.domain.*;
import com.reign.gcld.juben.common.*;
import com.reign.framework.json.*;
import com.reign.gcld.player.dto.*;
import com.reign.gcld.general.domain.*;
import com.reign.gcld.sdata.common.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.gcld.battle.common.*;
import com.reign.gcld.chat.common.*;
import com.reign.gcld.task.message.*;
import java.util.*;

public class NationRankBuider extends Builder
{
    public NationRankBuider(final int battleType) {
        this.battleType = battleType;
    }
    
    @Override
    public Tuple<Boolean, String> attPermitCreate(final IDataGetter dataGetter, final int playerId, final int defId) {
        final Player player = dataGetter.getPlayerDao().read(playerId);
        final Tuple<Boolean, String> tuple = new Tuple();
        tuple.left = false;
        final Battle battle = NewBattleManager.getInstance().getBattleByBatType(player.getPlayerId(), 8);
        if (battle != null && battle.getDefBaseInfo().getId() != defId) {
            tuple.right = LocalMessages.BATTLE_INT_BATTLE;
            return tuple;
        }
        final PlayerAttribute pa = dataGetter.getPlayerAttributeDao().read(player.getPlayerId());
        final char[] cs = pa.getFunctionId().toCharArray();
        if (cs[46] == '0') {
            tuple.right = LocalMessages.T_COMM_10020;
            return tuple;
        }
        if (playerId == defId) {
            tuple.right = LocalMessages.BATTLE_NATION_RANK_SELF;
            return tuple;
        }
        final JuBenDto juBenDto = JuBenManager.getInstance().getByPid(playerId);
        if (juBenDto != null) {
            tuple.right = LocalMessages.IN_JUBEN_CANNT_BATTLE;
            return tuple;
        }
        final int[] res = dataGetter.getRankBatService().getSixRankInfo(player.getForceId(), playerId);
        int rank = res[0];
        if (rank == 0) {
            rank = 201;
        }
        if (defId == rank) {
            tuple.right = LocalMessages.BATTLE_NATION_RANK_SELF;
            return tuple;
        }
        if (defId > rank) {
            final JsonDocument doc = new JsonDocument();
            doc.startObject();
            doc.createElement("rbRefresh", true);
            doc.endObject();
            Players.push(playerId, PushCommand.PUSH_UPDATE, doc.toByte());
            tuple.right = LocalMessages.BATTLE_NATION_RANK_LESS;
            return tuple;
        }
        if (defId < rank - 5) {
            final JsonDocument doc = new JsonDocument();
            doc.startObject();
            doc.createElement("rbRefresh", true);
            doc.endObject();
            Players.push(playerId, PushCommand.PUSH_UPDATE, doc.toByte());
            tuple.right = LocalMessages.BATTLE_NATION_RANK_OUT;
            return tuple;
        }
        final long leftTimes = dataGetter.getRankBatService().getLeftRankTimes(playerId);
        if (leftTimes <= 0L) {
            tuple.right = LocalMessages.BATTLE_NATION_RANK_NO_TIMES;
            return tuple;
        }
        tuple.left = true;
        tuple.right = "";
        return tuple;
    }
    
    @Override
    public Tuple<Boolean, byte[]> attPermitBack(final IDataGetter dataGetter, final int playerId, final int defId, final int generalId) {
        final Tuple<Boolean, byte[]> tuple = new Tuple();
        final Battle battle = NewBattleManager.getInstance().getBattleByBatType(playerId, 8);
        if (battle == null) {
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
    public Tuple<Boolean, String> canCreateBattle(final Player player, final int defId, final IDataGetter dataGetter) {
        final int playerId = player.getPlayerId();
        final Tuple<Boolean, String> tuple = new Tuple();
        tuple.left = false;
        final PlayerAttribute pa = dataGetter.getPlayerAttributeDao().read(player.getPlayerId());
        final char[] cs = pa.getFunctionId().toCharArray();
        if (cs[46] == '0') {
            tuple.right = LocalMessages.T_COMM_10020;
            return tuple;
        }
        final Map<Integer, Battle> batMap = NewBattleManager.getInstance().getBattleByPid(playerId);
        if (batMap.containsKey(this.battleType)) {
            tuple.right = LocalMessages.BATTLE_NATION_RANK_BATTING;
            return tuple;
        }
        final JuBenDto juBenDto = JuBenManager.getInstance().getByPid(player.getPlayerId());
        if (juBenDto != null) {
            tuple.right = LocalMessages.IN_JUBEN_CANNT_BATTLE;
            return tuple;
        }
        if (playerId == defId) {
            tuple.right = LocalMessages.BATTLE_NATION_RANK_SELF;
            return tuple;
        }
        final int[] res = dataGetter.getRankBatService().getSixRankInfo(player.getForceId(), playerId);
        int rank = res[0];
        if (rank == 0) {
            rank = 201;
        }
        if (defId == rank) {
            tuple.right = LocalMessages.BATTLE_NATION_RANK_SELF;
            return tuple;
        }
        if (defId > rank) {
            tuple.right = LocalMessages.BATTLE_NATION_RANK_LESS;
            return tuple;
        }
        if (defId < rank - 5) {
            tuple.right = LocalMessages.BATTLE_NATION_RANK_OUT;
            return tuple;
        }
        final long leftTimes = dataGetter.getRankBatService().getLeftRankTimes(playerId);
        if (leftTimes <= 0L) {
            tuple.right = LocalMessages.BATTLE_NATION_RANK_NO_TIMES;
            return tuple;
        }
        tuple.left = true;
        return tuple;
    }
    
    @Override
    public byte[] getPrepareInfo(final IDataGetter dataGetter, final PlayerDto playerDto, int defId, final Battle battle, final int terrain) {
        final int defPlayerId = dataGetter.getRankBatService().getRankPlayer(playerDto.forceId, defId);
        if (defPlayerId <= 0) {
            defId = -defId;
        }
        else {
            defId = defPlayerId;
        }
        final int playerId = playerDto.playerId;
        final JsonDocument doc = new JsonDocument();
        doc.createElement("bat", this.battleType * 100000 + 1);
        final List<PlayerGeneralMilitary> pgmList = dataGetter.getPlayerGeneralMilitaryDao().getMilitaryList(playerId);
        boolean isNpc = false;
        QualifyingLevel ql = null;
        int defChiefId = 0;
        if (defId < 0) {
            isNpc = true;
            ql = (QualifyingLevel)dataGetter.getQualifyingLevelCache().get((Object)(-defId));
            defChiefId = ql.getChief();
        }
        else {
            defChiefId = defId;
        }
        doc.appendJson(this.getAttTopLeft(dataGetter, playerId, battle));
        doc.appendJson(this.getDefTopRight(dataGetter, playerId, battle, defChiefId, isNpc));
        doc.appendJson(this.getAttGenerals(dataGetter, playerId, pgmList, playerDto.forceId, terrain));
        doc.appendJson(this.getDefGenerals(dataGetter, ql, defChiefId, terrain));
        return doc.toByte();
    }
    
    private byte[] getDefGenerals(final IDataGetter dataGetter, final QualifyingLevel ql, final int defChiefId, final int terrain) {
        final JsonDocument doc = new JsonDocument();
        doc.startArray("defGenerals");
        if (ql != null) {
            final Army army = (Army)dataGetter.getArmyCache().get((Object)defChiefId);
            General general = (General)dataGetter.getGeneralCache().get((Object)army.getGeneralId());
            Troop troop = (Troop)dataGetter.getTroopCache().get((Object)general.getTroop());
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
                final Tactic tactic = (Tactic)dataGetter.getTacticCache().get((Object)general.getTacticId());
                if (tactic != null) {
                    doc.createElement("tacticName", tactic.getName());
                }
            }
            TroopTerrain gTerrain = troop.getTerrains().get(terrain);
            if (gTerrain != null && gTerrain.getDefEffect() > 0) {
                doc.createElement("terrainAdd", gTerrain.getDefEffect());
                doc.createElement("terrainQ", gTerrain.getDefQuality());
            }
            List<TerrainStrategySpecDto> tssList = troop.getTsstList();
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
            final ChiefNpc cn = ql.getChiefNpc();
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
                tssList = troop.getTsstList();
                if (tssList != null && tssList.size() > 0) {
                    final List<Integer> tssIds2 = new LinkedList<Integer>();
                    for (final TerrainStrategySpecDto tss2 : tssList) {
                        if (tss2.terrainId == terrain && (tss2.show == 2 || tss2.show == 3)) {
                            tssIds2.add(tss2.strategyId);
                        }
                    }
                    if (tssIds2.size() > 0) {
                        doc.startArray("tssList");
                        for (final TerrainStrategySpecDto tss2 : tssList) {
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
        else {
            final List<PlayerGeneralMilitary> pgmList = dataGetter.getPlayerGeneralMilitaryDao().getMilitaryList(defChiefId);
            for (int i = 0; i < pgmList.size(); ++i) {
                final PlayerGeneralMilitary pgm = pgmList.get(i);
                final General general2 = (General)dataGetter.getGeneralCache().get((Object)pgm.getGeneralId());
                doc.startObject();
                doc.createElement("generalId", pgm.getGeneralId());
                doc.createElement("generalName", general2.getName());
                doc.createElement("generalLv", pgm.getLv());
                final Player playerTemp = dataGetter.getPlayerDao().read(pgm.getPlayerId());
                final Troop troop2 = dataGetter.getTroopCache().getTroop(general2.getTroop(), playerTemp.getPlayerId());
                doc.createElement("troopId", troop2.getType());
                doc.createElement("troopType", general2.getTroop());
                doc.createElement("armyHp", dataGetter.getBattleDataCache().getMaxHp(pgm));
                doc.createElement("quality", general2.getQuality());
                doc.createElement("generalPic", general2.getPic());
                if (general2.getTacticId() != 0) {
                    final Tactic tactic3 = (Tactic)dataGetter.getTacticCache().get((Object)general2.getTacticId());
                    if (tactic3 != null) {
                        doc.createElement("tacticName", tactic3.getName());
                    }
                }
                final TroopTerrain gTerrain2 = troop2.getTerrains().get(terrain);
                if (gTerrain2 != null && gTerrain2.getDefEffect() > 0) {
                    doc.createElement("terrainAdd", gTerrain2.getDefEffect());
                    doc.createElement("terrainQ", gTerrain2.getDefQuality());
                }
                final List<TerrainStrategySpecDto> tssList2 = troop2.getTsstList();
                if (tssList2 != null && tssList2.size() > 0) {
                    final List<Integer> tssIds3 = new LinkedList<Integer>();
                    for (final TerrainStrategySpecDto tss3 : tssList2) {
                        if (tss3.terrainId == terrain && (tss3.show == 2 || tss3.show == 3)) {
                            tssIds3.add(tss3.strategyId);
                        }
                    }
                    if (tssIds3.size() > 0) {
                        doc.startArray("tssList");
                        for (final TerrainStrategySpecDto tss3 : tssList2) {
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
        doc.endArray();
        return doc.toByte();
    }
    
    @Override
    public String getAttTargetName(final IDataGetter dataGetter, final Battle bat) {
        if (bat.getDefBaseInfo().getId2() < 0) {
            return ((Army)dataGetter.getArmyCache().get((Object)((QualifyingLevel)dataGetter.getQualifyingLevelCache().get((Object)(-bat.getDefBaseInfo().getId2()))).getChief())).getName();
        }
        return dataGetter.getPlayerDao().read(bat.getDefBaseInfo().getId2()).getPlayerName();
    }
    
    @Override
    public byte[] getBattleTopInfo(final IDataGetter dataGetter, final PlayerDto playerDto, final Battle battle) {
        final JsonDocument doc = new JsonDocument();
        doc.createElement("bat", this.battleType * 100000 + 1);
        doc.appendJson(this.getAttTopLeft(dataGetter, playerDto.playerId, battle));
        int defChiefId = battle.getDefBaseInfo().getId2();
        if (defChiefId < 0) {
            final QualifyingLevel ql = (QualifyingLevel)dataGetter.getQualifyingLevelCache().get((Object)(-defChiefId));
            defChiefId = ql.getChief();
        }
        doc.appendJson(this.getDefTopRight(dataGetter, playerDto.playerId, battle, defChiefId, battle.isNpc));
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
    public Battle existBattle(final IDataGetter dataGetter, final Player player, final int defId) {
        return null;
    }
    
    @Override
    public Tuple<Boolean, String> canJoinBattle(final Player player, final List<PlayerGeneralMilitary> pgmList, final IDataGetter dataGetter, final Battle bat) {
        final Tuple<Boolean, String> tuple = new Tuple();
        tuple.left = false;
        tuple.right = LocalMessages.T_COMM_10011;
        final JuBenDto juBenDto = JuBenManager.getInstance().getByPid(player.getPlayerId());
        if (juBenDto != null) {
            tuple.right = LocalMessages.IN_JUBEN_CANNT_BATTLE;
            return tuple;
        }
        return tuple;
    }
    
    @Override
    public String getBattleId(final IDataGetter dataGetter, final Player player, final int defId) {
        return NewBattleManager.getBattleId(this.battleType, player.getPlayerId(), defId);
    }
    
    @Override
    public void inBattleInfo(final int playerId, final boolean inBattle) {
    }
    
    @Override
    public boolean initDefCamp(final IDataGetter dataGetter, final BattleAttacker battleAttacker, int defId, final Battle bat) {
        final int attForceId = battleAttacker.attForceId;
        final int playerId = battleAttacker.attPlayerId;
        QualifyingLevel ql = null;
        int defChiefId = 0;
        final int defPlayerId = dataGetter.getRankBatService().getRankPlayer(playerId, defId);
        boolean isNpc = false;
        if (defPlayerId <= 0) {
            if (defPlayerId < 0) {
                BattleSceneLog.getInstance().debug("defPlayerId==0\u5c31\u8868\u793aNPC\uff0c\u4e0d\u5e94\u8be5\u4e3a\u8d1f\u6570\uff1adefPlayerId:\u3000" + defPlayerId + " defId: " + defId + " playerId: " + playerId);
            }
            defId = -defId;
            isNpc = true;
            ql = (QualifyingLevel)dataGetter.getQualifyingLevelCache().get((Object)(-defId));
            defChiefId = ql.getChief();
            bat.defBaseInfo.setForceId(0);
            bat.defBaseInfo.setDefChiefId(defChiefId);
            bat.defBaseInfo.setId2(defId);
        }
        else {
            defId = (defChiefId = defPlayerId);
            bat.defBaseInfo.setForceId(attForceId);
            bat.defBaseInfo.setDefChiefId(defId);
            bat.defBaseInfo.setId2(defId);
        }
        int defNum = 0;
        if (isNpc) {
            int id = 0;
            final Army armyCach = (Army)dataGetter.getArmyCache().get((Object)defChiefId);
            CampArmy campArmy = null;
            final List<Integer> npcList = ql.getChiefNpc().getNpcList();
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
            final List<PlayerGeneralMilitary> pgmList = dataGetter.getPlayerGeneralMilitaryDao().getMilitaryList(defChiefId);
            final Player playerTemp = dataGetter.getPlayerDao().read(defChiefId);
            for (int i = 0; i < pgmList.size(); ++i) {
                final PlayerGeneralMilitary pgm = pgmList.get(i);
                final CampArmy campArmy2 = this.copyArmyFromCach(playerTemp, pgm, dataGetter, bat, 0);
                if (campArmy2 != null) {
                    defNum += campArmy2.getArmyHpOrg();
                    bat.defCamp.add(campArmy2);
                    BattleSceneLog.getInstance().info("#batId:" + bat.getBattleId() + "_" + bat.getStartTime() + "#add:init#side:def" + "#playerId:" + campArmy2.getPlayerId() + ":" + campArmy2.isPhantom + "#general:" + campArmy2.getGeneralId() + "#defSize:" + bat.defCamp.size());
                }
            }
        }
        bat.defBaseInfo.setAllNum(defNum);
        bat.defBaseInfo.setNum(defNum);
        return isNpc;
    }
    
    private CampArmy copyArmyFromCach(final Player player, final PlayerGeneralMilitary pgm, final IDataGetter dataGetter, final Battle bat, final int batSide) {
        final CampArmy campArmy = new CampArmy();
        final General general = (General)dataGetter.getGeneralCache().get((Object)pgm.getGeneralId());
        campArmy.setSpecialGeneral(new SpecialGeneral(general.getGeneralSpecialInfo().generalType, general.getGeneralSpecialInfo().param, general.getGeneralSpecialInfo().param2));
        final Troop troop = dataGetter.getTroopCache().getTroop(general.getTroop(), player.getPlayerId());
        campArmy.setPlayerLv(player.getPlayerLv());
        campArmy.setPlayerId(player.getPlayerId());
        campArmy.setPlayerName(player.getPlayerName());
        campArmy.setForceId(player.getForceId());
        Builder.getTerrainValue(bat.terrainVal, troop, batSide, campArmy);
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
        int forces = pgm.getForces();
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
        return 8;
    }
    
    @Override
    public int getBatInfo(final IDataGetter dataGetter, final int defId) {
        return this.battleType * 100000 + 1;
    }
    
    @Override
    public void countNpcReward(final boolean attWin, final IDataGetter dataGetter, final Battle bat, final BattleResult battleResult) {
        if (bat.attBaseInfo.forceId != 0 && bat.defBaseInfo.forceId != 0 && bat.attBaseInfo.forceId != bat.defBaseInfo.forceId) {
            ErrorSceneLog.getInstance().appendErrorMsg("\u6392\u4f4d\u8d5b\u653b\u5b88\u65b9\u4e0d\u5c5e\u4e8e\u540c\u4e00\u52bf\u529b").appendClassName("NationRankBuider").appendMethodName("countNpcReward").append("battleId", bat.getBattleId()).append("attWin", attWin).flush();
        }
        dataGetter.getRankBatService().addRankBatRewardAndJifen(bat.attBaseInfo.forceId, bat.inBattlePlayers.get(bat.attBaseInfo.id), attWin);
    }
    
    @Override
    public void dealNextNpc(final boolean attWin, final IDataGetter dataGetter, final Battle bat, final BattleResult battleResult) {
        if (attWin) {
            final int defPlayerId = dataGetter.getRankBatService().getRankPlayer(bat.attBaseInfo.forceId, bat.defBaseInfo.id);
            final int fallRank = dataGetter.getRankBatService().getPlayerRank(bat.attBaseInfo.forceId, bat.attBaseInfo.id);
            if (defPlayerId > 0) {
                try {
                    final String param = String.valueOf(bat.getBattleType()) + "#" + bat.getDefBaseInfo().getId();
                    dataGetter.getChatUtil().sendRankChat(defPlayerId, fallRank, new ChatLink(1, param));
                }
                catch (Exception e) {
                    ErrorSceneLog.getInstance().error("NationRankBuilder dealNextNpc" + e);
                }
            }
            dataGetter.getRankBatService().changeNameList(bat.getAttBaseInfo().forceId, bat.getAttBaseInfo().getId(), bat.getDefBaseInfo().getId2());
            TaskMessageHelper.sendNationalRankBattleWinMessage(bat.attBaseInfo.getId());
        }
    }
    
    @Override
    public int battleRewardSave(final boolean attWin, final int playerId, final PlayerInfo pi, final Date date, final IDataGetter dataGetter, final BattleResult battleResult, final Battle bat) {
        return 0;
    }
    
    @Override
    public void dealBuilderBattleTask(final IDataGetter dataGetter, final int playerId, final int defId) {
        TaskMessageHelper.sendNationalRankBattleMessage(playerId);
    }
    
    @Override
    public int caculateRoundCopper(final IDataGetter dataGetter, final Battle bat, final int playerId, final double mAttOmega, final FightRewardCoe frc) {
        final int copper = (int)(mAttOmega * frc.getM());
        return copper;
    }
    
    @Override
    public void dealUniqueStaff(final IDataGetter dataGetter, final Battle bat, final int playerId, final int defId) {
        final int leftTimes = dataGetter.getPlayerBatRankDao().read(playerId).getRankBatNum();
        if (leftTimes <= 0) {
            ErrorSceneLog.getInstance().appendErrorMsg("\u6392\u4f4d\u8d5b\u6b21\u6570\u9519\u8bef").appendClassName("NationRankBuider").appendMethodName("dealUniqueStaff").append("battleId", bat.getBattleId()).append("playerId", playerId).flush();
        }
        dataGetter.getPlayerBatRankDao().updateRankBatNum(playerId, leftTimes - 1);
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
}
