package com.reign.gcld.battle.scene;

import com.reign.gcld.battle.service.*;
import com.reign.gcld.juben.service.*;
import com.reign.gcld.mine.domain.*;
import com.reign.gcld.juben.common.*;
import com.reign.gcld.general.domain.*;
import com.reign.gcld.player.domain.*;
import com.reign.gcld.battle.domain.*;
import com.reign.framework.json.*;
import com.reign.gcld.world.common.*;
import com.reign.gcld.player.dto.*;
import com.reign.gcld.sdata.common.*;
import com.reign.gcld.common.*;
import com.reign.util.*;
import com.reign.gcld.chat.common.*;
import java.util.*;
import com.reign.gcld.battle.common.*;
import com.reign.gcld.sdata.domain.*;

public class GroupMineBuider extends PersonalMineBuider
{
    public GroupMineBuider(final int battleType) {
        super(battleType);
    }
    
    @Override
    public Tuple<Boolean, String> attPermitCreate(final IDataGetter dataGetter, final int playerId, final int defId) {
        final Tuple<Boolean, String> tuple = new Tuple();
        tuple.left = false;
        final Mine mine = (Mine)dataGetter.getMineCache().get((Object)defId);
        if (mine == null || mine.getType() == 2 || mine.getType() == 4) {
            tuple.right = LocalMessages.T_COMM_10011;
            return tuple;
        }
        final Player player = dataGetter.getPlayerDao().read(playerId);
        final PlayerMine pm = dataGetter.getPlayerMineDao().getByOwner(player.getForceId(), mine.getType());
        if (pm != null) {
            tuple.right = LocalMessages.MINE_FORCE_HAVE_MINE_NO_BATTLE;
            return tuple;
        }
        final Battle battle = NewBattleManager.getInstance().getBattleByParm(this.battleType, 0, defId);
        if (battle != null && battle.getAttBaseInfo().getForceId() != player.getForceId()) {
            tuple.right = LocalMessages.MINE_HAVE_BATTLE_NO_CREATE_BATTLE;
            return tuple;
        }
        final JuBenDto juBenDto = JuBenManager.getInstance().getByPid(playerId);
        if (juBenDto != null) {
            tuple.right = LocalMessages.IN_JUBEN_CANNT_BATTLE;
            return tuple;
        }
        if (battle == null && dataGetter.getRankService().getRank(1, player.getPlayerId(), player.getForceId()) > 10) {
            tuple.right = LocalMessages.MINE_NO_ENOUGH_RANK_BATTLE;
            return tuple;
        }
        tuple.left = true;
        tuple.right = "";
        return tuple;
    }
    
    @Override
    public Tuple<Boolean, String> canJoinBattle(final Player player, final List<PlayerGeneralMilitary> pgmList, final IDataGetter dataGetter, final Battle bat) {
        final Tuple<Boolean, String> tuple = new Tuple();
        tuple.left = false;
        final int defId = bat.getDefBaseInfo().getId();
        final Battle battle = NewBattleManager.getInstance().getBattleByDefId(this.battleType, defId);
        if (battle != null && battle.getAttBaseInfo().getForceId() != player.getForceId()) {
            tuple.right = LocalMessages.MINE_BATTLING_NO_ATTACK;
            return tuple;
        }
        final Mine mine = (Mine)dataGetter.getMineCache().get((Object)bat.getDefBaseInfo().getId());
        final PlayerAttribute pa = dataGetter.getPlayerAttributeDao().read(player.getPlayerId());
        final char[] cs = pa.getFunctionId().toCharArray();
        if (mine.getType() == 1 && cs[34] == '0') {
            tuple.right = LocalMessages.T_COMM_10020;
            return tuple;
        }
        if (mine.getType() == 3 && cs[40] == '0') {
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
        if (mine == null || mine.getType() == 4 || mine.getType() == 2) {
            tuple.right = LocalMessages.T_COMM_10011;
            return tuple;
        }
        final PlayerMine pm = dataGetter.getPlayerMineDao().getByOwner(player.getForceId(), mine.getType());
        if (pm != null) {
            tuple.right = LocalMessages.MINE_FORCE_HAVE_MINE_NO_BATTLE;
            return tuple;
        }
        final Battle battle = NewBattleManager.getInstance().getBattleByParm(this.battleType, 0, defId);
        if (battle != null && battle.getAttBaseInfo().getForceId() != player.getForceId()) {
            tuple.right = LocalMessages.MINE_HAVE_BATTLE_NO_CREATE_BATTLE;
            return tuple;
        }
        final JuBenDto juBenDto = JuBenManager.getInstance().getByPid(player.getPlayerId());
        if (juBenDto != null) {
            tuple.right = LocalMessages.IN_JUBEN_CANNT_BATTLE;
            return tuple;
        }
        if (dataGetter.getRankService().getRank(1, player.getPlayerId(), player.getForceId()) > 10) {
            tuple.right = LocalMessages.MINE_NO_ENOUGH_RANK_BATTLE;
            return tuple;
        }
        final PlayerAttribute pa = dataGetter.getPlayerAttributeDao().read(player.getPlayerId());
        final char[] cs = pa.getFunctionId().toCharArray();
        if (mine.getType() == 1 && cs[34] == '0') {
            tuple.right = LocalMessages.T_COMM_10020;
            return tuple;
        }
        if (mine.getType() == 3 && cs[40] == '0') {
            tuple.right = LocalMessages.T_COMM_10020;
            return tuple;
        }
        tuple.left = true;
        return tuple;
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
        int forceId = 0;
        for (int i = 0; i < playerStrs.length; ++i) {
            final String[] generals = playerStrs[i].split("#");
            playerId = Integer.valueOf(generals[0]);
            final Player defPlayer = dataGetter.getPlayerDao().read(playerId);
            forceId = defPlayer.getForceId();
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
        bat.defBaseInfo.setForceId(forceId);
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
        bat.attBaseInfo.setForceId(player.getForceId());
    }
    
    @Override
    public byte[] getAttTopLeft(final IDataGetter dataGetter, final int playerId, final Battle battle) {
        final JsonDocument doc = new JsonDocument();
        final Player attPlayer = dataGetter.getPlayerDao().read(playerId);
        doc.createElement("playerId", attPlayer.getForceId());
        doc.createElement("playerName", (Object)(String.valueOf(WorldCityCommon.nationIdNameMap.get(attPlayer.getForceId())) + LocalMessages.T_FORCE_NATION));
        doc.createElement("playerPic", attPlayer.getForceId());
        doc.createElement("playerLv", 0);
        if (battle != null) {
            doc.createElement("playerForces", battle.getAttBaseInfo().getNum());
            doc.createElement("playerMaxForces", battle.getAttBaseInfo().getAllNum());
        }
        return doc.toByte();
    }
    
    @Override
    public byte[] getDefGenerals(final IDataGetter dataGetter, final PlayerDto playerDto, final int defId, final int terrain) {
        final JsonDocument doc = new JsonDocument();
        final PlayerMineBatInfo pmbi = dataGetter.getPlayerMineBatInfoDao().read(defId);
        doc.startArray("defGenerals");
        if (pmbi == null) {
            doc.endArray();
            return doc.toByte();
        }
        int playerId = 0;
        final String[] playerStrs = pmbi.getBattleInfo().split(";");
        int num = 0;
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
                    final Player playerTemp = dataGetter.getPlayerDao().read(pgm.getPlayerId());
                    final Troop troop = dataGetter.getTroopCache().getTroop(general.getTroop(), playerTemp.getPlayerId());
                    doc.startObject();
                    doc.createElement("generalId", pgm.getGeneralId());
                    doc.createElement("generalName", general.getName());
                    doc.createElement("att", troop.getAtt());
                    doc.createElement("generalLv", pgm.getLv());
                    doc.createElement("troopId", troop.getType());
                    doc.createElement("troopType", general.getTroop());
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
                    if (++num >= 5) {
                        break;
                    }
                }
                if (num >= 5) {
                    break;
                }
            }
        }
        doc.endArray();
        return doc.toByte();
    }
    
    @Override
    public byte[] getDefTopRight(final IDataGetter dataGetter, final PlayerDto playerDto, final Battle battle, final int defId) {
        final JsonDocument doc = new JsonDocument();
        if (battle != null) {
            doc.createElement("npcId", battle.defBaseInfo.getForceId());
            doc.createElement("npcName", (Object)(String.valueOf(WorldCityCommon.nationIdNameMap.get(battle.defBaseInfo.getForceId())) + LocalMessages.T_FORCE_NATION));
            doc.createElement("npcPic", battle.defBaseInfo.getForceId());
            doc.createElement("npcLv", 0);
            doc.createElement("npcForces", battle.getDefBaseInfo().getNum());
            doc.createElement("npcMaxForces", battle.getDefBaseInfo().getAllNum());
            return doc.toByte();
        }
        final PlayerMine pm = dataGetter.getPlayerMineDao().getByMineId(defId);
        if (pm == null) {
            doc.createElement("npcId", "");
            doc.createElement("npcName", "");
            doc.createElement("npcPic", "");
            doc.createElement("npcLv", "");
            return doc.toByte();
        }
        doc.createElement("npcId", pm.getOwnerId());
        doc.createElement("npcName", (Object)(String.valueOf(WorldCityCommon.nationIdNameMap.get(pm.getOwnerId())) + LocalMessages.T_FORCE_NATION));
        doc.createElement("npcPic", pm.getOwnerId());
        doc.createElement("npcLv", 0);
        return doc.toByte();
    }
    
    @Override
    public void sendBattleInfo(final IDataGetter dataGetter, final Battle bat, final BattleAttacker battleAttacker) {
        final Player player = battleAttacker.attPlayer;
        final Mine mine = (Mine)dataGetter.getMineCache().get((Object)bat.defBaseInfo.getId());
        final String msg = MessageFormatter.format(LocalMessages.MINE_START_BATTTLE_CHAT_INFO, new Object[] { ColorUtil.getSpecialColorMsg(player.getPlayerName()), ColorUtil.getSpecialColorMsg(mine.getName()) });
        final String param = String.valueOf(bat.getBattleType()) + "#" + bat.getDefBaseInfo().getId();
        dataGetter.getChatService().sendSystemChat("GLOBAL", player.getPlayerId(), player.getForceId(), msg, new ChatLink(1, param));
    }
    
    @Override
    public int battleRewardSave(final boolean attWin, final int playerId, final PlayerInfo pi, final Date date, final IDataGetter dataGetter, final BattleResult battleResult, final Battle bat) {
        return 0;
    }
    
    @Override
    public int caculateRoundCopper(final IDataGetter dataGetter, final Battle bat, final int playerId, final double mAttOmega, final FightRewardCoe frc) {
        final int copper = (int)(mAttOmega * frc.getM());
        return copper;
    }
    
    @Override
    public void afterBat(final boolean attWin, final IDataGetter dataGetter, final Battle bat) {
        if (attWin) {
            final String msg = MessageFormatter.format(LocalMessages.MINE_CHANGE_OWNER_INFO, new Object[] { ColorUtil.getSpecialColorMsg(String.valueOf(WorldCityCommon.nationIdNameMap.get(bat.getAttBaseInfo().getForceId())) + LocalMessages.T_FORCE_NATION) });
            dataGetter.getChatService().sendSystemChat("GLOBAL", 0, 0, msg, null);
        }
    }
}
