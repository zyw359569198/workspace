package com.reign.gcld.battle.scene;

import com.reign.util.*;
import com.reign.gcld.battle.service.*;
import com.reign.gcld.juben.service.*;
import com.reign.gcld.mine.domain.*;
import com.reign.gcld.juben.common.*;
import com.reign.framework.json.*;
import com.reign.gcld.task.message.*;
import com.reign.gcld.general.domain.*;
import com.reign.gcld.player.domain.*;
import com.reign.gcld.player.dto.*;
import com.reign.gcld.sdata.common.*;
import com.reign.gcld.battle.domain.*;
import com.reign.gcld.sdata.domain.*;
import java.util.*;
import com.reign.gcld.battle.common.*;
import com.reign.gcld.common.*;

public class PersonalMineBuider extends Builder
{
    public PersonalMineBuider(final int battleType) {
        this.battleType = battleType;
    }
    
    @Override
    public Tuple<Boolean, String> attPermitCreate(final IDataGetter dataGetter, final int playerId, final int defId) {
        final Tuple<Boolean, String> tuple = new Tuple();
        tuple.left = false;
        final Mine mine = (Mine)dataGetter.getMineCache().get((Object)defId);
        if (mine == null || mine.getType() == 1 || mine.getType() == 3) {
            tuple.right = LocalMessages.T_COMM_10011;
            return tuple;
        }
        final PlayerMine pm = dataGetter.getPlayerMineDao().getByOwner(playerId, mine.getType());
        if (pm != null) {
            tuple.right = LocalMessages.MINE_HAVE_MINE_NO_BATTLE;
            return tuple;
        }
        final Battle battle = NewBattleManager.getInstance().getBattleByParm(this.battleType, 0, defId);
        if (battle != null && battle.getAttBaseInfo().getId() != playerId) {
            tuple.right = LocalMessages.MINE_HAVE_BATTLE_NO_CREATE_BATTLE;
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
    public Tuple<Boolean, byte[]> attPermitBack(final IDataGetter dataGetter, final int playerId, final int defId, final int generalId) {
        final Tuple<Boolean, byte[]> tuple = new Tuple();
        Battle battle = null;
        final Map<Integer, Battle> battles = NewBattleManager.getInstance().getBattleByPid(playerId);
        for (final Map.Entry<Integer, Battle> entry : battles.entrySet()) {
            if (entry.getKey() == 6) {
                battle = entry.getValue();
                break;
            }
            if (entry.getKey() == 7) {
                battle = entry.getValue();
                break;
            }
        }
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
    public void sendTaskMessage(final int playerId, final int defId, final IDataGetter dataGetter) {
        final Mine mine = (Mine)dataGetter.getMineCache().get((Object)defId);
        if (mine.getType() == 1 || mine.getType() == 2) {
            TaskMessageHelper.sendWorldMineIronOwnTaskMessage(playerId);
        }
        else {
            TaskMessageHelper.sendWorldMineJadeOwnTaskMessage(playerId);
        }
    }
    
    @Override
    public Terrain getTerrain(final int playerId, final int defId, final IDataGetter dataGetter) {
        return new Terrain(3, 2, 9);
    }
    
    @Override
    public String getBattleId(final IDataGetter dataGetter, final Player player, final int defId) {
        return NewBattleManager.getBattleId(this.battleType, 0, defId);
    }
    
    @Override
    public Battle existBattle(final IDataGetter dataGetter, final Player player, final int defId) {
        final Battle battle = NewBattleManager.getInstance().getBattleByParm(this.battleType, 0, defId);
        return battle;
    }
    
    @Override
    public Tuple<Boolean, String> canJoinBattle(final Player player, final List<PlayerGeneralMilitary> pgmList, final IDataGetter dataGetter, final Battle bat) {
        final Tuple<Boolean, String> tuple = new Tuple();
        tuple.left = false;
        final int defId = bat.getDefBaseInfo().getId();
        final Battle battle = NewBattleManager.getInstance().getBattleByDefId(this.battleType, defId);
        if (battle != null && battle.getAttBaseInfo().getId() != player.getPlayerId()) {
            tuple.right = LocalMessages.MINE_BATTLING_NO_ATTACK;
            return tuple;
        }
        final Mine mine = (Mine)dataGetter.getMineCache().get((Object)bat.getDefBaseInfo().getId());
        final PlayerAttribute pa = dataGetter.getPlayerAttributeDao().read(player.getPlayerId());
        final char[] cs = pa.getFunctionId().toCharArray();
        if (mine.getType() == 2 && cs[34] == '0') {
            tuple.right = LocalMessages.T_COMM_10020;
            return tuple;
        }
        if (mine.getType() == 4 && cs[40] == '0') {
            tuple.right = LocalMessages.T_COMM_10020;
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
    public Tuple<Boolean, String> canCreateBattle(final Player player, final int defId, final IDataGetter dataGetter) {
        final Tuple<Boolean, String> tuple = new Tuple();
        tuple.left = false;
        final Mine mine = (Mine)dataGetter.getMineCache().get((Object)defId);
        if (mine == null || mine.getType() == 1 || mine.getType() == 3) {
            tuple.right = LocalMessages.T_COMM_10011;
            return tuple;
        }
        final PlayerMine pm = dataGetter.getPlayerMineDao().getByOwner(player.getPlayerId(), mine.getType());
        if (pm != null) {
            tuple.right = LocalMessages.MINE_HAVE_MINE_NO_BATTLE;
            return tuple;
        }
        final Battle battle = NewBattleManager.getInstance().getBattleByParm(this.battleType, 0, defId);
        if (battle != null) {
            tuple.right = LocalMessages.MINE_HAVE_BATTLE_NO_CREATE_BATTLE;
            return tuple;
        }
        final JuBenDto juBenDto = JuBenManager.getInstance().getByPid(player.getPlayerId());
        if (juBenDto != null) {
            tuple.right = LocalMessages.IN_JUBEN_CANNT_BATTLE;
            return tuple;
        }
        final PlayerAttribute pa = dataGetter.getPlayerAttributeDao().read(player.getPlayerId());
        final char[] cs = pa.getFunctionId().toCharArray();
        if (mine.getType() == 2 && cs[34] == '0') {
            tuple.right = LocalMessages.T_COMM_10020;
            return tuple;
        }
        if (mine.getType() == 4 && cs[40] == '0') {
            tuple.right = LocalMessages.T_COMM_10020;
            return tuple;
        }
        tuple.left = true;
        return tuple;
    }
    
    @Override
    public int getBatInfo(final IDataGetter dataGetter, final int defId) {
        final int type = ((Mine)dataGetter.getMineCache().get((Object)defId)).getType();
        if (type == 1 || type == 2) {
            return 600001;
        }
        return 600002;
    }
    
    @Override
    public byte[] getPrepareInfo(final IDataGetter dataGetter, final PlayerDto playerDto, final int defId, final Battle battle, final int terrain) {
        final int playerId = playerDto.playerId;
        final JsonDocument doc = new JsonDocument();
        doc.createElement("bat", this.getBatInfo(dataGetter, defId));
        final List<PlayerGeneralMilitary> pgmList = dataGetter.getPlayerGeneralMilitaryDao().getMilitaryList(playerId);
        doc.appendJson(this.getAttTopLeft(dataGetter, playerDto.playerId, battle));
        doc.appendJson(this.getDefTopRight(dataGetter, playerDto, battle, defId));
        doc.appendJson(this.getAttGenerals(dataGetter, playerId, pgmList, playerDto.forceId, terrain));
        doc.appendJson(this.getDefGenerals(dataGetter, playerDto, defId, terrain));
        return doc.toByte();
    }
    
    public byte[] getDefGenerals(final IDataGetter dataGetter, final PlayerDto playerDto, final int defId, final int terrain) {
        final JsonDocument doc = new JsonDocument();
        final PlayerMineBatInfo pmbi = dataGetter.getPlayerMineBatInfoDao().read(defId);
        doc.startArray("defGenerals");
        if (pmbi == null) {
            doc.endArray();
            return doc.toByte();
        }
        final PlayerMine pm = dataGetter.getPlayerMineDao().getByMineId(defId);
        if (pm == null) {
            doc.endArray();
            return doc.toByte();
        }
        final Player defPlayer = dataGetter.getPlayerDao().read(pm.getOwnerId());
        if (defPlayer.getForceId() != playerDto.forceId && pm.getMode() == 2) {
            doc.endArray();
            return doc.toByte();
        }
        int playerId = 0;
        final String[] playerStrs = pmbi.getBattleInfo().split(";");
        for (int i = 0; i < playerStrs.length; ++i) {
            final String[] generals = playerStrs[i].split("#");
            if (generals.length >= 2) {
                playerId = Integer.valueOf(generals[0]);
                final Map<Integer, PlayerGeneralMilitary> gMap = dataGetter.getPlayerGeneralMilitaryDao().getMilitaryMap(playerId);
                for (final PlayerGeneralMilitary pgm : gMap.values()) {
                    if (pgm == null) {
                        continue;
                    }
                    final General general = (General)dataGetter.getGeneralCache().get((Object)pgm.getGeneralId());
                    final Player palyerTemp = dataGetter.getPlayerDao().read(pgm.getPlayerId());
                    final Troop troop = dataGetter.getTroopCache().getTroop(general.getTroop(), palyerTemp.getPlayerId());
                    doc.startObject();
                    doc.createElement("generalId", pgm.getGeneralId());
                    doc.createElement("generalName", general.getName());
                    doc.createElement("att", troop.getAtt());
                    doc.createElement("generalLv", pgm.getLv());
                    doc.createElement("troopId", troop.getType());
                    doc.createElement("troopType", troop.getSerial());
                    doc.createElement("generalPic", general.getPic());
                    doc.createElement("armyHp", pgm.getForces());
                    doc.createElement("quality", general.getQuality());
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
                break;
            }
        }
        doc.endArray();
        return doc.toByte();
    }
    
    @Override
    public byte[] getBattleTopInfo(final IDataGetter dataGetter, final PlayerDto playerDto, final Battle battle) {
        final JsonDocument doc = new JsonDocument();
        doc.createElement("bat", this.getBatInfo(dataGetter, battle.getDefBaseInfo().getId()));
        doc.appendJson(this.getAttTopLeft(dataGetter, playerDto.playerId, battle));
        doc.appendJson(this.getDefTopRight(dataGetter, playerDto, battle, 0));
        return doc.toByte();
    }
    
    public byte[] getDefTopRight(final IDataGetter dataGetter, final PlayerDto playerDto, final Battle battle, final int defId) {
        final JsonDocument doc = new JsonDocument();
        Player defPlayer = null;
        if (battle == null) {
            final PlayerMine pm = dataGetter.getPlayerMineDao().getByMineId(defId);
            if (pm == null) {
                doc.createElement("npcId", "");
                doc.createElement("npcName", "");
                doc.createElement("npcPic", "");
                doc.createElement("npcLv", "");
                return doc.toByte();
            }
            defPlayer = dataGetter.getPlayerDao().read(pm.getOwnerId());
            if (defPlayer.getForceId() != playerDto.forceId && pm.getMode() == 2) {
                doc.createElement("rush", true);
            }
        }
        else {
            defPlayer = dataGetter.getPlayerDao().read(battle.getDefBaseInfo().getId());
        }
        if (defPlayer != null) {
            doc.createElement("npcId", defPlayer.getPlayerId());
            doc.createElement("npcName", defPlayer.getPlayerName());
            doc.createElement("npcPic", defPlayer.getPic());
            doc.createElement("npcLv", defPlayer.getPlayerLv());
        }
        else {
            doc.createElement("npcId", "");
            doc.createElement("npcName", "");
            doc.createElement("npcPic", "");
            doc.createElement("npcLv", "");
        }
        if (battle != null) {
            doc.createElement("npcForces", battle.getDefBaseInfo().getNum());
            doc.createElement("npcMaxForces", battle.getDefBaseInfo().getAllNum());
        }
        return doc.toByte();
    }
    
    @Override
    public boolean initDefCamp(final IDataGetter dataGetter, final BattleAttacker battleAttacker, final int defId, final Battle bat) {
        final PlayerMineBatInfo pmbi = dataGetter.getPlayerMineBatInfoDao().read(defId);
        if (pmbi == null) {
            return false;
        }
        final String[] playerStrs = pmbi.getBattleInfo().split(";");
        int id = 0;
        int defNum = 0;
        int playerId = 0;
        for (int i = 0; i < playerStrs.length; ++i) {
            final String[] generals = playerStrs[i].split("#");
            playerId = Integer.valueOf(generals[0]);
            final Player defPlayer = dataGetter.getPlayerDao().read(playerId);
            final Map<Integer, PlayerGeneralMilitary> gMap = dataGetter.getPlayerGeneralMilitaryDao().getMilitaryMap(playerId);
            for (final PlayerGeneralMilitary pgm : gMap.values()) {
                id = bat.campNum.getAndIncrement();
                final CampArmy campArmy = this.copyArmyFromCachIrror(defPlayer, pgm, dataGetter, id, this.getGeneralState(), bat.terrainVal, -1);
                defNum += campArmy.getArmyHpOrg();
                bat.defCamp.add(campArmy);
                BattleSceneLog.getInstance().info("#batId:" + bat.getBattleId() + "_" + bat.getStartTime() + "#add:init#side:def" + "#playerId:" + campArmy.getPlayerId() + ":" + campArmy.isPhantom + "#general:" + campArmy.getGeneralId() + "#defSize:" + bat.defCamp.size());
            }
        }
        bat.defBaseInfo.setAllNum(defNum);
        bat.defBaseInfo.setNum(defNum);
        bat.defBaseInfo.setForceId(playerId);
        return false;
    }
    
    @Override
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
        final List<PlayerGeneralMilitary> pgmList = battleAttacker.pgmList;
        final Player player = battleAttacker.attPlayer;
        int attNum = 0;
        for (int i = 0; i < pgmList.size(); ++i) {
            final PlayerGeneralMilitary pgm = pgmList.get(i);
            final CampArmy campArmy = this.copyArmyFromPlayerTable(player, pgm, dataGetter, this.getGeneralState(), bat, 1);
            if (campArmy != null) {
                attNum += campArmy.getArmyHpOrg();
                bat.attCamp.add(campArmy);
                BattleSceneLog.getInstance().info("#batId:" + bat.getBattleId() + "_" + bat.getStartTime() + "#add:init#side:att" + "#playerId:" + campArmy.getPlayerId() + ":" + campArmy.isPhantom + "#general:" + campArmy.getGeneralId() + "#attSize:" + bat.attCamp.size());
            }
        }
        bat.attBaseInfo.setNum(bat.attBaseInfo.getNum() + attNum);
        bat.attBaseInfo.setAllNum(bat.attBaseInfo.getAllNum() + attNum);
        bat.attBaseInfo.setForceId(player.getPlayerId());
    }
    
    @Override
    public int getGeneralState() {
        return 7;
    }
    
    public CampArmy copyArmyFromCachIrror(final Player player, final PlayerGeneralMilitary pgm, final IDataGetter dataGetter, final int id, final int battleType, final int terrainType, final int defId) {
        final CampArmy campArmy = new CampArmy();
        final General general = (General)dataGetter.getGeneralCache().get((Object)pgm.getGeneralId());
        campArmy.setSpecialGeneral(new SpecialGeneral(general.getGeneralSpecialInfo().generalType, general.getGeneralSpecialInfo().param, general.getGeneralSpecialInfo().param2));
        final Troop troop = dataGetter.getTroopCache().getTroop(general.getTroop(), player.getPlayerId());
        campArmy.setId(id);
        campArmy.setPlayerLv(player.getPlayerLv());
        campArmy.setPlayerId(player.getPlayerId());
        campArmy.setPlayerName(player.getPlayerName());
        campArmy.setForceId(player.getForceId());
        Builder.getTerrainValue(terrainType, troop, 0, campArmy);
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
        Builder.getAttDefHp(dataGetter, campArmy);
        campArmy.setArmyHp(campArmy.getMaxForces());
        campArmy.setArmyHpOrg(campArmy.getMaxForces());
        if (general.getTacticId() > 0 && pgm.getForces() >= campArmy.getMaxForces()) {
            campArmy.setTacticVal(1);
            if (campArmy.getSpecialGeneral().generalType == 7) {
                campArmy.setTacticVal((int)campArmy.getSpecialGeneral().param);
            }
        }
        campArmy.setStrategies(troop.getStrategyDefMap().get(terrainType));
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
    public void inBattleInfo(final int playerId, final boolean inBattle) {
    }
    
    @Override
    public String getAttTargetName(final IDataGetter dataGetter, final Battle bat) {
        return ((Mine)dataGetter.getMineCache().get((Object)bat.getDefBaseInfo().getId())).getName();
    }
    
    @Override
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
        bat = NewBattleManager.getInstance().getBattleByBatType(playerId, 7);
        if (bat != null) {
            return bat;
        }
        return bat;
    }
    
    @Override
    public void countNpcReward(final boolean attWin, final IDataGetter dataGetter, final Battle bat, final BattleResult battleResult) {
    }
    
    @Override
    public void dealNextNpc(final boolean attWin, final IDataGetter dataGetter, final Battle bat, final BattleResult battleResult) {
        if (attWin) {
            final StringBuilder sb = new StringBuilder();
            for (final int playerId : bat.inBattlePlayers.keySet()) {
                final PlayerInfo pi = bat.inBattlePlayers.get(playerId);
                if (!pi.isAttSide) {
                    continue;
                }
                sb.append(playerId).append(";");
            }
            battleResult.mineInfo = dataGetter.getMineService().handleAfterBattle(bat.getDefBaseInfo().getId(), bat.getAttBaseInfo().getForceId());
            if (dataGetter.getPlayerMineBatInfoDao().read(bat.getDefBaseInfo().getId()) == null) {
                final PlayerMineBatInfo pmbi = new PlayerMineBatInfo();
                pmbi.setBattleInfo(sb.toString());
                pmbi.setMineId(bat.getDefBaseInfo().getId());
                dataGetter.getPlayerMineBatInfoDao().create(pmbi);
            }
            else {
                dataGetter.getPlayerMineBatInfoDao().updateInfo(bat.getDefBaseInfo().getId(), sb.toString());
            }
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
    public void updateChooseTime(final Battle bat, final RoundInfo roundInfo, final boolean attAutoStChoosed, final boolean defAutoStChoosed) {
        if (roundInfo.win != 1 && bat.attList.size() > 0 && bat.attList.get(0).getCampArmy().getPlayerId() > 0 && !attAutoStChoosed && !bat.attList.get(0).getCampArmy().isPhantom && Players.getSession(Integer.valueOf(bat.attList.get(0).getCampArmy().getPlayerId())) != null) {
            roundInfo.nextMaxExeTime += 6000;
            roundInfo.timePredicationBuffer.append("st choose:").append(6000).append("|");
        }
    }
}
