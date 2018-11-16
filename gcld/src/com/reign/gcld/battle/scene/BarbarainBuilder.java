package com.reign.gcld.battle.scene;

import com.reign.gcld.world.common.*;
import com.reign.gcld.juben.service.*;
import com.reign.gcld.juben.common.*;
import com.reign.framework.json.*;
import com.reign.gcld.world.domain.*;
import com.reign.gcld.team.service.*;
import com.reign.gcld.phantom.common.*;
import com.reign.gcld.common.util.*;
import com.reign.gcld.team.common.*;
import com.reign.gcld.player.domain.*;
import com.reign.gcld.player.dto.*;
import com.reign.gcld.general.domain.*;
import org.apache.commons.lang.*;
import com.reign.gcld.rank.domain.*;
import com.reign.gcld.world.service.*;
import com.reign.gcld.battle.service.*;
import com.reign.gcld.general.dto.*;
import com.reign.gcld.battle.domain.*;
import com.reign.gcld.task.message.*;
import com.reign.gcld.sdata.common.*;
import com.reign.gcld.common.*;
import java.util.*;
import com.reign.util.*;
import com.reign.gcld.player.common.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.gcld.battle.common.*;
import com.reign.gcld.battle.reward.*;
import com.reign.gcld.asynchronousDB.manager.*;

public class BarbarainBuilder extends Builder
{
    Random rand;
    
    public BarbarainBuilder(final int battleType) {
        this.rand = new Random();
        this.battleType = battleType;
    }
    
    @Override
    public Tuple<Boolean, String> attPermitCreate(final IDataGetter dataGetter, final int playerId, final int defId) {
        final Tuple<Boolean, String> tuple = new Tuple();
        tuple.left = false;
        if (!WorldCityCommon.barbarainCitySet.contains(defId)) {
            tuple.right = LocalMessages.THIS_IS_NOT_BARBARAIN_CAPITAL_CITY;
            return tuple;
        }
        final Player player = dataGetter.getPlayerDao().read(playerId);
        final PlayerAttribute pa = dataGetter.getPlayerAttributeDao().read(player.getPlayerId());
        final char[] cs = pa.getFunctionId().toCharArray();
        if (cs[24] == '0') {
            tuple.right = LocalMessages.T_COMM_10020;
            return tuple;
        }
        final int degree = dataGetter.getRankService().hasBarTasks(player.getForceId());
        final int stage = dataGetter.getNationService().getStageByForceId(player.getForceId());
        if (degree == 0 && stage >= 4) {
            ErrorSceneLog.getInstance().appendErrorMsg("barbarain is not open AND tryTask is not open").appendPlayerName(player.getPlayerName()).appendPlayerId(player.getPlayerId()).append("forceId", player.getForceId()).flush();
            tuple.right = LocalMessages.BARBARAIN_NOT_OPEN_YET;
            return tuple;
        }
        if (degree > 0) {
            final Barbarain barbarain = dataGetter.getBarbarainCache().getByDegree(degree);
            if (barbarain == null) {
                ErrorSceneLog.getInstance().appendErrorMsg("attPermitCreate, barbarain is null").append("degree", degree).flush();
            }
            final long target = barbarain.getTarget();
            final int forceId = player.getForceId();
            final long killTotal = NewBattleManager.getInstance().getBarbarainKillByForceId(forceId);
            if (killTotal > target) {
                tuple.right = LocalMessages.BARBARAIN_DONE_ALREADY;
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
    public Tuple<Boolean, byte[]> attPermitBack(final IDataGetter dataGetter, final int playerId, final int defId, final int generalId) {
        final Tuple<Boolean, byte[]> tuple = new Tuple();
        tuple.left = false;
        final Player player = dataGetter.getPlayerDao().read(playerId);
        final Battle battle = NewBattleManager.getInstance().getBattleByDefId(14, defId);
        if (battle == null) {
            tuple.right = JsonBuilder.getJson(State.FAIL, LocalMessages.BATTLE_INFO_WORLD_BATENDED);
            return tuple;
        }
        final JuBenDto juBenDto = JuBenManager.getInstance().getByPid(playerId);
        if (juBenDto != null) {
            tuple.right = JsonBuilder.getJson(State.FAIL, LocalMessages.IN_JUBEN_CANNT_BATTLE);
            return tuple;
        }
        final City city = dataGetter.getCityDao().read(defId);
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("battle", true);
        doc.createElement("battleId", battle.getBattleId());
        if (city.getForceId() == player.getPlayerId()) {
            doc.createElement("side", 0);
        }
        else {
            doc.createElement("side", 1);
        }
        doc.endObject();
        tuple.left = true;
        tuple.right = JsonBuilder.getJson(State.SUCCESS, doc.toByte());
        return tuple;
    }
    
    @Override
    public Terrain getTerrain(final int playerId, final int defId, final IDataGetter dataGetter) {
        final WorldCity worldCity = (WorldCity)dataGetter.getWorldCityCache().get((Object)defId);
        final int display = (worldCity == null) ? 1 : worldCity.getTerrain();
        return new Terrain(display, (worldCity == null) ? 1 : worldCity.getTerrainEffectType(), display);
    }
    
    @Override
    public byte[] getOtherBatInfo(final IDataGetter dataGetter, final int defId, final int playerId, final int battleSide, PlayerBattleAttribute pba) {
        final JsonDocument doc = new JsonDocument();
        doc.createElement("targetName", ((WorldCity)dataGetter.getWorldCityCache().get((Object)defId)).getName());
        if (pba == null) {
            pba = dataGetter.getPlayerBattleAttributeDao().read(playerId);
        }
        final Team team = TeamManager.getInstance().getCreateTeam2(playerId);
        if (team != null) {
            doc.createElement("teamId", team.getTeamId());
            doc.createElement("teamType", team.getWorldLegionId());
            final WorldLegion wl = (WorldLegion)dataGetter.getWorldLegionCache().get((Object)team.getWorldLegionId());
            doc.createElement("teamGold", wl.getGoldDeploy() * team.getCurNum());
            doc.createElement("curNum", team.getCurNum());
            doc.createElement("maxNum", team.getMaxNum());
            doc.createElement("teamExp", team.getOwnerAddExp() * team.getCurNum());
        }
        doc.createElement("changeBat", pba.getChangebat());
        doc.createElement("freePhantomCount", pba.getVip3PhantomCount());
        int maxFreePc = 30;
        final PlayerPhantomObj playerPhantomObj = PhantomManager.getInstance().playerPhantomObjMap.get(playerId);
        if (playerPhantomObj != null) {
            maxFreePc = playerPhantomObj.maxPhantomNum;
        }
        doc.createElement("maxFreePc", maxFreePc);
        final Player player = dataGetter.getPlayerDao().read(playerId);
        final PlayerOfficeRelative por = dataGetter.getPlayerOfficeRelativeDao().read(playerId);
        if (por != null && por.getOfficerId() > 0) {
            final Halls hall = (Halls)dataGetter.getHallsCache().get((Object)por.getOfficerId());
            if (hall.getHyN() > 0) {
                final Official official = (Official)dataGetter.getOfficialCache().get((Object)hall.getOfficialId());
                if (official.getId() == 1) {
                    doc.createElement("officer", (Object)(String.valueOf(WebUtil.getForceName(player.getForceId())) + LocalMessages.RANK_CONSTANTS_KING));
                }
                else {
                    doc.createElement("officer", String.valueOf(official.getNameShort()) + hall.getNameList());
                }
                doc.createElement("freeNum", hall.getHyN());
                doc.createElement("hour", hall.getHyT());
            }
        }
        final long youdiCd = pba.getYoudiTime() - System.currentTimeMillis();
        doc.createElement("cd", (youdiCd < 0L) ? 0L : youdiCd);
        return doc.toByte();
    }
    
    @Override
    public int getBatInfo(final IDataGetter dataGetter, final int defId) {
        return 300001;
    }
    
    @Override
    public byte[] getPrepareInfo(final IDataGetter dataGetter, final PlayerDto playerDto, final int defId, final Battle battle, final int terrain) {
        final JsonDocument doc = new JsonDocument();
        doc.createElement("bat", 1400001);
        return doc.toByte();
    }
    
    @Override
    public byte[] getBattleTopInfo(final IDataGetter dataGetter, final PlayerDto playerDto, final Battle battle) {
        final JsonDocument doc = new JsonDocument();
        doc.createElement("bat", 1400001);
        return doc.toByte();
    }
    
    @Override
    public String getBattleId(final IDataGetter dataGetter, final Player player, final int defId) {
        return NewBattleManager.getBattleId(this.battleType, player.getForceId(), defId);
    }
    
    @Override
    public Tuple<Boolean, String> canCreateBattle(final Player player, final int defId, final IDataGetter dataGetter) {
        final Tuple<Boolean, String> tuple = new Tuple();
        tuple.left = false;
        if (!WorldCityCommon.barbarainCitySet.contains(defId)) {
            tuple.right = LocalMessages.THIS_IS_NOT_BARBARAIN_CAPITAL_CITY;
            return tuple;
        }
        final PlayerAttribute pa = dataGetter.getPlayerAttributeDao().read(player.getPlayerId());
        final char[] cs = pa.getFunctionId().toCharArray();
        if (cs[24] == '0') {
            tuple.right = LocalMessages.T_COMM_10020;
            return tuple;
        }
        final int degree = dataGetter.getRankService().hasBarTasks(player.getForceId());
        if (degree == 0 && dataGetter.getNationService().getStageByForceId(player.getForceId()) >= 4) {
            ErrorSceneLog.getInstance().appendErrorMsg("barbarain is not open AND tryTask is not open").appendPlayerName(player.getPlayerName()).appendPlayerId(player.getPlayerId()).append("forceId", player.getForceId()).flush();
            tuple.right = LocalMessages.BARBARAIN_NOT_OPEN_YET;
            return tuple;
        }
        if (degree > 0) {
            final Barbarain barbarain = dataGetter.getBarbarainCache().getByDegree(degree);
            if (barbarain == null) {
                ErrorSceneLog.getInstance().appendErrorMsg("attPermitCreate, barbarain is null").append("degree", degree).flush();
            }
            final long target = barbarain.getTarget();
            final int forceId = player.getForceId();
            if (WorldCityCommon.forcIdManzuCityIdMap.get(forceId) != defId) {
                tuple.right = LocalMessages.NOT_YOUR_BARBARAIN_CAPITAL_CITY;
                return tuple;
            }
            final long killTotal = NewBattleManager.getInstance().getBarbarainKillByForceId(forceId);
            if (killTotal > target) {
                tuple.right = LocalMessages.BARBARAIN_DONE_ALREADY;
                return tuple;
            }
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
    public Tuple<Boolean, String> canJoinBattle(final Player player, final List<PlayerGeneralMilitary> pgmList, final IDataGetter dataGetter, final Battle bat) {
        final Tuple<Boolean, String> tuple = new Tuple();
        tuple.left = false;
        final int defId = bat.getDefBaseInfo().getId();
        if (!WorldCityCommon.barbarainCitySet.contains(defId)) {
            tuple.right = LocalMessages.THIS_IS_NOT_BARBARAIN_CAPITAL_CITY;
            return tuple;
        }
        final PlayerAttribute pa = dataGetter.getPlayerAttributeDao().read(player.getPlayerId());
        final char[] cs = pa.getFunctionId().toCharArray();
        if (cs[24] == '0') {
            tuple.right = LocalMessages.T_COMM_10020;
            return tuple;
        }
        final int degree = dataGetter.getRankService().hasBarTasks(player.getForceId());
        final int stage = dataGetter.getNationService().getStageByForceId(player.getForceId());
        if (degree == 0 && stage >= 4) {
            ErrorSceneLog.getInstance().appendErrorMsg("barbarain is not open AND tryTask is not open").appendPlayerName(player.getPlayerName()).appendPlayerId(player.getPlayerId()).append("forceId", player.getForceId()).flush();
            tuple.right = LocalMessages.BARBARAIN_NOT_OPEN_YET;
            return tuple;
        }
        if (degree > 0) {
            final Barbarain barbarain = dataGetter.getBarbarainCache().getByDegree(degree);
            if (barbarain == null) {
                ErrorSceneLog.getInstance().appendErrorMsg("attPermitCreate, barbarain is null").append("degree", degree).flush();
            }
            final long target = barbarain.getTarget();
            final int forceId = player.getForceId();
            if (WorldCityCommon.forcIdManzuCityIdMap.get(forceId) != defId) {
                tuple.right = LocalMessages.NOT_YOUR_BARBARAIN_CAPITAL_CITY;
                return tuple;
            }
            final long killTotal = NewBattleManager.getInstance().getBarbarainKillByForceId(forceId);
            if (killTotal > target) {
                tuple.right = LocalMessages.BARBARAIN_DONE_ALREADY;
                return tuple;
            }
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
    public void inBattleInfo(final int playerId, final boolean inBattle) {
    }
    
    @Override
    public void addInSceneSet(final Battle bat, final int playerId) {
    }
    
    @Override
    public void sendBattleInfo(final IDataGetter dataGetter, final Battle bat, final BattleAttacker battleAttacker) {
        try {
            dataGetter.getCityService().changeState(bat.defBaseInfo.getId(), 1, false);
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("BarbarainBuilder sendBattleInfo set city state catch Exception.", e);
        }
    }
    
    @Override
    public boolean initDefCamp(final IDataGetter dataGetter, final BattleAttacker battleAttacker, final int defId, final Battle bat) {
        final int attForceId = battleAttacker.attForceId;
        int defForceId = 0;
        switch (defId) {
            case 251: {
                defForceId = 101;
                break;
            }
            case 250: {
                defForceId = 102;
                break;
            }
            case 252: {
                defForceId = 103;
                break;
            }
            default: {
                ErrorSceneLog.getInstance().appendErrorMsg("defId error").append("defId", defId).appendBattleId(bat.getBattleId()).append("attForceId", attForceId).appendClassName("BarbarainBuilder").flush();
                break;
            }
        }
        int defNum = 0;
        final int stage = dataGetter.getNationService().getStageByForceId(attForceId);
        final int degree = dataGetter.getRankService().hasBarTasks(attForceId);
        final ForceInfo fi = dataGetter.getForceInfoDao().read(attForceId);
        if (degree > 0) {
            final Barbarain barbarain = dataGetter.getBarbarainCache().getByDegree(degree);
            if (barbarain == null) {
                ErrorSceneLog.getInstance().appendErrorMsg("barbarain is null").append("degree", degree).appendBattleId(bat.getBattleId()).append("attForceId", attForceId).flush();
                return true;
            }
            final Player player = new Player();
            player.setPlayerId(-1);
            player.setForceId(defForceId);
            player.setPlayerLv(barbarain.getLv());
            Integer[] ArmyIds = null;
            switch (defForceId) {
                case 101: {
                    ArmyIds = barbarain.getWeiArmyIds();
                    player.setPlayerName(barbarain.getWeiName());
                    break;
                }
                case 102: {
                    ArmyIds = barbarain.getShuArmyIds();
                    player.setPlayerName(barbarain.getShuName());
                    break;
                }
                case 103: {
                    ArmyIds = barbarain.getWuArmyIds();
                    player.setPlayerName(barbarain.getWuName());
                    break;
                }
                default: {
                    ErrorSceneLog.getInstance().appendErrorMsg("ForceId invalid.").append("attForceId", attForceId).flush();
                    break;
                }
            }
            for (int i = bat.defCamp.size(); i < 100; ++i) {
                final int randInt = this.rand.nextInt(ArmyIds.length);
                final int npc = ArmyIds[randInt];
                final Army armyCach = (Army)dataGetter.getArmyCache().get((Object)npc);
                final int id = bat.campNum.getAndIncrement();
                final CampArmy campArmy = Builder.copyArmyFromCach(player, npc, dataGetter, id, bat.terrainVal, armyCach.getGeneralLv());
                defNum += campArmy.getArmyHpOrg();
                bat.defCamp.add(campArmy);
            }
        }
        else {
            final List<BarbarainPhantom> BarPhantomList = dataGetter.getBarbarainPhantomDao().getBarPhantomByLocationId(defId);
            final List<BarbarainPhantom> defBarPhantomList = new LinkedList<BarbarainPhantom>();
            for (final BarbarainPhantom barPhantom : BarPhantomList) {
                if (barPhantom.getState() > 0) {
                    ErrorSceneLog.getInstance().appendErrorMsg("barPhantom is not free").appendBattleId(bat.getBattleId()).append("barPhantom vId", barPhantom.getVId()).append("barPhantom state", barPhantom.getState()).appendClassName("BarbarainBuilder").appendMethodName("initDefCamp").flush();
                }
                else {
                    if (barPhantom.getForceId() != defForceId) {
                        continue;
                    }
                    defBarPhantomList.add(barPhantom);
                }
            }
            for (final BarbarainPhantom barPhantom : defBarPhantomList) {
                final int battleSide = 0;
                CampArmy campArmy2 = null;
                if (barPhantom.getNpcType() == 4) {
                    campArmy2 = this.copyArmyformBarPhantom4(dataGetter, bat, barPhantom, battleSide);
                    if (campArmy2 == null) {
                        ErrorSceneLog.getInstance().appendErrorMsg("city battle init defCamp get null campArmy from BarPhantom").appendBattleId(bat.getBattleId()).append("barPhantom vId", barPhantom.getVId()).append("barPhantom state", barPhantom.getState()).flush();
                        continue;
                    }
                }
                if (campArmy2 != null) {
                    dataGetter.getBarbarainPhantomDao().updateState(barPhantom.getVId(), 3);
                    defNum += campArmy2.getArmyHpOrg();
                    bat.defCamp.add(campArmy2);
                }
            }
            if (defBarPhantomList.size() < 1) {
                ErrorSceneLog.getInstance().error("BarbarainBuilder initDefCamp ERROR, defIsNull so add NPC!!!");
                final Player player2 = new Player();
                player2.setPlayerId(-1);
                player2.setForceId(defForceId);
                final int id2 = fi.getId();
                final CdExams cdExams = (CdExams)dataGetter.getCdExamsCache().get((Object)id2);
                final CdExamsObj ceo = dataGetter.getBattleService().getCdExamsObjByStageAndForceId(cdExams, stage, attForceId);
                player2.setPlayerLv(ceo.getGeneralLv());
                player2.setPlayerName(ceo.getName());
                final List<Integer> armyIds = new ArrayList<Integer>();
                String[] split;
                for (int length = (split = ceo.getArmyIds().split(";")).length, k = 0; k < length; ++k) {
                    final String temp = split[k];
                    if (StringUtils.isNotBlank(temp)) {
                        armyIds.add(Integer.parseInt(temp));
                    }
                }
                for (int j = bat.defCamp.size(); j < 5; ++j) {
                    final int randInt2 = this.rand.nextInt(armyIds.size());
                    final int npc2 = armyIds.get(randInt2);
                    final Army armyCach2 = (Army)dataGetter.getArmyCache().get((Object)npc2);
                    final int id3 = bat.campNum.getAndIncrement();
                    final CampArmy campArmy3 = Builder.copyArmyFromCach(player2, npc2, dataGetter, id3, bat.terrainVal, armyCach2.getGeneralLv());
                    defNum += campArmy3.getArmyHpOrg();
                    bat.defCamp.add(campArmy3);
                }
            }
        }
        bat.defBaseInfo.setAllNum(defNum);
        bat.defBaseInfo.setNum(defNum);
        bat.defBaseInfo.setForceId(defForceId);
        bat.defBaseInfo.id2 = degree;
        return true;
    }
    
    @Override
    public void dealTroopDrop(final IDataGetter dataGetter, final Battle bat, final RoundInfo roundInfo) {
        final int attPlayerId = roundInfo.attCampArmy.playerId;
        if (attPlayerId > 0) {
            for (final BattleArmy ba : roundInfo.defKilledList) {
                if (ba.getCampArmy().isDefenceNpc) {
                    continue;
                }
                final int troopId = ba.getCampArmy().getTroopId();
                final Troop bonusTroop = (Troop)dataGetter.getTroopCache().get((Object)troopId);
                if (bonusTroop == null || bonusTroop.getTroopDrop() == null) {
                    continue;
                }
                dataGetter.getBattleDropService().saveBattleDrop(attPlayerId, bonusTroop.getTroopDrop());
                final Map<Integer, BattleDrop> dropMap = bonusTroop.getTroopDrop().getDropAndMap();
                dropMap.size();
                roundInfo.attRoundReward.addDropMap(dropMap);
            }
        }
        final int defPlayerId = roundInfo.defCampArmy.playerId;
        if (defPlayerId > 0) {
            for (final BattleArmy ba2 : roundInfo.attKilledList) {
                if (ba2.getCampArmy().isDefenceNpc) {
                    continue;
                }
                final int troopId2 = ba2.getCampArmy().getTroopId();
                final Troop bonusTroop2 = (Troop)dataGetter.getTroopCache().get((Object)troopId2);
                if (bonusTroop2 == null || bonusTroop2.getTroopDrop() == null) {
                    continue;
                }
                dataGetter.getBattleDropService().saveBattleDrop(defPlayerId, bonusTroop2.getTroopDrop());
                final Map<Integer, BattleDrop> dropMap2 = bonusTroop2.getTroopDrop().getDropAndMap();
                dropMap2.size();
                roundInfo.defRoundReward.addDropMap(dropMap2);
            }
        }
    }
    
    @Override
    public CampArmy copyArmyformBarPhantom4(final IDataGetter dataGetter, final Battle bat, final BarbarainPhantom barPhantom, final int batSide) {
        if (barPhantom.getNpcType() != 4) {
            ErrorSceneLog.getInstance().appendErrorMsg("barPhantom.getNpcType() error").appendBattleId(bat.battleId).append("barPhantom.getNpcType()", barPhantom.getNpcType()).append("barPhantom", barPhantom.getVId()).appendClassName("BarbarainBuilder").appendMethodName("copyArmyformBarPhantom2").flush();
            return null;
        }
        final CampArmy campArmy = new CampArmy();
        final int armyId = barPhantom.getArmyId();
        final Army army = (Army)dataGetter.getArmyCache().get((Object)armyId);
        final General general = (General)dataGetter.getGeneralCache().get((Object)armyId);
        campArmy.setSpecialGeneral(new SpecialGeneral(general.getGeneralSpecialInfo().generalType, general.getGeneralSpecialInfo().param, general.getGeneralSpecialInfo().param2));
        final Troop troop = (Troop)dataGetter.getTroopCache().get((Object)general.getTroop());
        campArmy.isBarPhantom = true;
        campArmy.updateDB = false;
        campArmy.setId(bat.campNum.getAndIncrement());
        campArmy.setPgmVId(barPhantom.getVId());
        campArmy.setArmyName(general.getName());
        campArmy.setPlayerLv(army.getGeneralLv());
        campArmy.setPlayerId(-4);
        campArmy.setForceId(barPhantom.getForceId());
        String playerName = null;
        switch (barPhantom.getForceId()) {
            case 101: {
                playerName = barPhantom.getName();
                break;
            }
            case 102: {
                playerName = barPhantom.getName();
                break;
            }
            case 103: {
                playerName = barPhantom.getName();
                break;
            }
        }
        campArmy.setPlayerName(playerName);
        getTerrainValue(bat.terrainVal, troop, batSide, campArmy);
        campArmy.setTroopId(troop.getId());
        campArmy.setTroopSerial(troop.getSerial());
        campArmy.setTroopType(troop.getType());
        campArmy.setTroopName(troop.getName());
        campArmy.setTroopDropType(BattleDrop.getDropType(troop.getDrop()));
        campArmy.setTroopDrop(troop.getTroopDrop());
        campArmy.setGeneralId(general.getId());
        campArmy.setGeneralLv(army.getGeneralLv());
        campArmy.setGeneralName(general.getName());
        campArmy.setGeneralPic(general.getPic());
        campArmy.setQuality(general.getQuality());
        campArmy.setTacicId(general.getTacticId());
        campArmy.setStrength(general.getStrength());
        campArmy.setLeader(general.getLeader());
        int forces = barPhantom.getHp();
        final int remainder = forces % 3;
        forces -= remainder;
        campArmy.setArmyHp(forces);
        campArmy.setArmyHpOrg(forces);
        campArmy.setMaxForces(army.getArmyHp());
        campArmy.setAttEffect(army.getAtt());
        campArmy.setDefEffect(army.getDef());
        campArmy.setBdEffect(army.getBd());
        int column = forces / army.getTroopHp();
        if (column <= 0) {
            column = 1;
        }
        campArmy.setColumn(column);
        campArmy.setTroopHp(army.getTroopHp());
        campArmy.setAttDef_B(new AttDef_B());
        campArmy.getAttDef_B().ATT_B = 0;
        campArmy.setTACTIC_ATT(campArmy.getAttDef_B().DEF_B = 0);
        campArmy.setTACTIC_DEF(0);
        campArmy.setTacticVal(barPhantom.getTacticval());
        if (batSide == 1) {
            campArmy.setStrategies(troop.getStrategyMap().get(bat.terrainVal));
        }
        else {
            campArmy.setStrategies(troop.getStrategyDefMap().get(bat.terrainVal));
        }
        return campArmy;
    }
    
    @Override
    public void dealKillrank(final boolean isBarbarain, final int gKillTotal, final Battle bat, final IDataGetter dataGetter, final int playerId) {
        final Builder cityBuilder = BuilderFactory.getInstance().getBuilder(3);
        cityBuilder.dealKillrank(isBarbarain, gKillTotal, bat, dataGetter, playerId);
        if (gKillTotal <= 0) {
            if (gKillTotal < 0) {
                ErrorSceneLog.getInstance().appendErrorMsg("gKillTotal is negative.").append("gKillTotal", gKillTotal).appendPlayerId(playerId).appendBattleId(bat.battleId).appendClassName("BarbarainBuilder").appendMethodName("dealKillrank").flush();
            }
            return;
        }
        dataGetter.getRankService().updateKillNum(0, gKillTotal, playerId, System.currentTimeMillis());
        final int stage = dataGetter.getNationService().getStageByForceId(bat.getAttBaseInfo().forceId);
        if (stage < 4) {
            final StringBuffer sb = new StringBuffer();
            sb.append(playerId);
            sb.append("#");
            sb.append(gKillTotal);
            dataGetter.getJobService().addJob("rankService", "updateTryRank", sb.toString(), System.currentTimeMillis(), false);
        }
    }
    
    @Override
    public void sendBattleCityEndInfo(final IDataGetter dataGetter, final Battle bat) {
        final int cityId = bat.defBaseInfo.id;
        try {
            dataGetter.getCityService().changeState(cityId, 0, false);
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().appendErrorMsg("quit lead to battle ended, reset city state catch exception").append("cityId", cityId).append("cityName", ((WorldCity)dataGetter.getWorldCityCache().get((Object)cityId)).getName()).appendClassName("BarbarainBuilder").appendMethodName("sendBattleCityEndInfo").flush();
        }
    }
    
    @Override
    public Battle existBattle(final IDataGetter dataGetter, final Player player, final int defId) {
        final Battle battle = NewBattleManager.getInstance().getBattleByParm(this.battleType, player.getForceId(), defId);
        return battle;
    }
    
    @Override
    public Tuple<List<PlayerGeneralMilitary>, String> chooseGeneral(final IDataGetter dataGetter, final Player player, final int defId, final List<Integer> gIdList) {
        final Tuple<List<PlayerGeneralMilitary>, String> tuple = new Tuple();
        final List<PlayerGeneralMilitary> chooseList = new ArrayList<PlayerGeneralMilitary>();
        final Map<Integer, PlayerGeneralMilitary> pgmMap = dataGetter.getPlayerGeneralMilitaryDao().getMilitaryMap(player.getPlayerId());
        final Date nowDate = new Date();
        for (int i = 0; i < gIdList.size(); ++i) {
            final PlayerGeneralMilitary pgm = pgmMap.get(gIdList.get(i));
            final GeneralMoveDto gmd = CityService.getGeneralMoveDto(pgm.getPlayerId(), pgm.getGeneralId());
            if (gmd != null && CDUtil.isInCD(gmd.nextMoveTime, nowDate)) {
                tuple.left = null;
                tuple.right = LocalMessages.BATTLE_IN_WORLD_MOVE;
            }
            else if (pgm.getState() <= 1) {
                final int maxHp = dataGetter.getBattleDataCache().getMaxHp(pgm);
                if (pgm.getForces() * 1.0 / maxHp < 0.05) {
                    tuple.left = null;
                    tuple.right = LocalMessages.BATTLE_INT_BATTLE_BLOOD_NO_ENOUGH;
                }
                else {
                    if (!BattleService.rewardContains(pgm.getVId())) {
                        final Set<Integer> neighborSet = dataGetter.getWorldRoadCache().getNeighbors(defId);
                        if (defId != pgm.getLocationId() && !neighborSet.contains(pgm.getLocationId())) {
                            continue;
                        }
                    }
                    if (!TeamManager.getInstance().isJoinTeam(pgm.getPlayerId(), pgm.getGeneralId())) {
                        if (gmd != null) {
                            if (gmd.cityState == 22) {
                                continue;
                            }
                            if (gmd.cityState == 23) {
                                continue;
                            }
                        }
                        chooseList.add(pgm);
                    }
                }
            }
        }
        if (chooseList.size() > 0) {
            tuple.left = chooseList;
            return tuple;
        }
        if (tuple.right != null) {
            return tuple;
        }
        tuple.left = null;
        tuple.right = LocalMessages.GENEGAL_CANNOT_BATTLE;
        return tuple;
    }
    
    @Override
    public void sendTaskMessage(final int playerId, final int defId, final IDataGetter dataGetter) {
    }
    
    @Override
    public void dealUniqueStaff(final IDataGetter dataGetter, final Battle bat, final int playerId, final int defId) {
    }
    
    @Override
    public int getGeneralState() {
        return 14;
    }
    
    @Override
    public void initJoinPlayer(final List<PlayerGeneralMilitary> pgmList, final IDataGetter dataGetter, final Player player, final Battle bat, final BaseInfo baseInfo, final LinkedList<CampArmy> campMap, final LinkedList<CampArmy> campChange, final int battleSide, final TeamInfo teamInfo) {
        long moveCd = 0L;
        if (pgmList.get(0).getLocationId() == bat.defBaseInfo.getId()) {
            moveCd = -1L;
        }
        else {
            final WorldRoad road = dataGetter.getWorldRoadCache().getRoad(pgmList.get(0).getLocationId(), bat.defBaseInfo.getId());
            final General general = (General)dataGetter.getGeneralCache().get((Object)pgmList.get(0).getGeneralId());
            final Troop troop = dataGetter.getTroopCache().getTroop(general.getTroop(), player.getPlayerId());
            moveCd = WorldCityCommon.getNextMoveCd(road, troop.getSpeed());
        }
        CampArmy campArmy = null;
        int num = 0;
        for (int i = 0; i < pgmList.size(); ++i) {
            PlayerGeneralMilitary pgm = pgmList.get(i);
            campArmy = this.copyAttCampFromPlayerTable(player, pgm, dataGetter, battleSide, moveCd, bat, battleSide);
            if (campArmy != null) {
                num += campArmy.getArmyHpOrg();
                campMap.add(campArmy);
                BattleSceneLog.getInstance().info("#batId:" + bat.getBattleId() + "_" + bat.getStartTime() + "#add:join#side:" + ((battleSide == 1) ? "att" : "def") + "#playerId:" + campArmy.getPlayerId() + ":" + campArmy.isPhantom + "#general:" + campArmy.getGeneralId() + "#" + ((battleSide == 1) ? "att" : "def") + "Size:" + campMap.size());
                campChange.add(campArmy);
            }
            else {
                pgm = dataGetter.getPlayerGeneralMilitaryDao().read(pgm.getVId());
                ErrorSceneLog.getInstance().appendErrorMsg("barbarain joinBattle failed.").appendPlayerName(player.getPlayerName()).appendPlayerId(player.getPlayerId()).appendBattleId(bat.getBattleId()).append("pgm state", pgm.getState()).append("vId", pgm.getVId()).flush();
            }
        }
        baseInfo.setNum(baseInfo.getNum() + num);
        baseInfo.setAllNum(baseInfo.getAllNum() + num);
    }
    
    @Override
    public void initAttCamp(final IDataGetter dataGetter, final BattleAttacker battleAttacker, final int defId, final Battle bat) {
        int attNum = 0;
        bat.attBaseInfo.setId(battleAttacker.attForceId);
        bat.attBaseInfo.setForceId(battleAttacker.attForceId);
        if (battleAttacker.attPlayer != null && battleAttacker.attPlayerId > 0) {
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
            final List<PlayerGeneralMilitary> pgmList = battleAttacker.pgmList;
            for (final PlayerGeneralMilitary pgm : pgmList) {
                dataGetter.getPlayerGeneralMilitaryDao().updateLocationId(pgm.getPlayerId(), pgm.getGeneralId(), defId);
            }
        }
        final List<PlayerGeneralMilitary> pgmList2 = dataGetter.getPlayerGeneralMilitaryDao().getMilitaryByLocationIdOrderByPlayerIdLvDesc(defId);
        final List<PlayerGeneralMilitary> attPgmList = new LinkedList<PlayerGeneralMilitary>();
        for (final PlayerGeneralMilitary pgm2 : pgmList2) {
            if (pgm2.getForceId() == battleAttacker.attForceId) {
                attPgmList.add(pgm2);
            }
        }
        for (final PlayerGeneralMilitary pgm2 : attPgmList) {
            if (pgm2.getState() > 1) {
                final int capitalId = WorldCityCommon.nationMainCityIdMap.get(pgm2.getForceId());
                dataGetter.getCityService().sendAttMoveInfo(pgm2.getPlayerId(), pgm2.getGeneralId(), pgm2.getLocationId(), capitalId, pgm2.getForceId(), "", pgm2.getForces(), true);
                dataGetter.getPlayerGeneralMilitaryDao().updateLocationId(pgm2.getPlayerId(), pgm2.getGeneralId(), capitalId);
                dataGetter.getGeneralService().sendGmStateSet(pgm2.getPlayerId(), pgm2.getGeneralId(), 1);
            }
            else {
                final int maxHp = dataGetter.getBattleDataCache().getMaxHp(pgm2);
                if (pgm2.getForces() * 1.0 / maxHp < 0.05) {
                    final int capitalId2 = WorldCityCommon.nationMainCityIdMap.get(pgm2.getForceId());
                    dataGetter.getCityService().sendAttMoveInfo(pgm2.getPlayerId(), pgm2.getGeneralId(), pgm2.getLocationId(), capitalId2, pgm2.getForceId(), "", pgm2.getForces(), true);
                    dataGetter.getPlayerGeneralMilitaryDao().updateLocationId(pgm2.getPlayerId(), pgm2.getGeneralId(), capitalId2);
                }
                else {
                    final Player attPlayer = dataGetter.getPlayerDao().read(pgm2.getPlayerId());
                    final CampArmy campArmy = this.copyArmyFromPlayerTable(attPlayer, pgm2, dataGetter, this.getGeneralState(), bat, 1);
                    if (campArmy == null) {
                        ErrorSceneLog.getInstance().appendErrorMsg("city battle init defCamp get null campArmy").appendBattleId(bat.getBattleId()).appendPlayerName(attPlayer.getPlayerName()).appendPlayerId(attPlayer.getPlayerId()).appendGeneralId(pgm2.getPlayerId()).append("vId", pgm2.getVId()).flush();
                    }
                    else {
                        bat.worldSceneLog.Indent().appendLogMsg("set pgm state as:" + this.getGeneralState()).appendPlayerName(attPlayer.getPlayerName()).appendGeneralName(((General)dataGetter.getGeneralCache().get((Object)pgm2.getGeneralId())).getName()).appendPlayerId(attPlayer.getPlayerId()).append("pgm vId", pgm2.getVId()).newLine();
                        attNum += campArmy.getArmyHpOrg();
                        bat.attCamp.add(campArmy);
                        BattleSceneLog.getInstance().info("#batId:" + bat.getBattleId() + "_" + bat.getStartTime() + "#add:init#side:att" + "#playerId:" + campArmy.getPlayerId() + ":" + campArmy.isPhantom + "#general:" + campArmy.getGeneralId() + "#defSize:" + bat.attCamp.size());
                        final int zdzsTech2 = dataGetter.getTechEffectCache().getTechEffect(pgm2.getPlayerId(), 43);
                        int autoStrategy2 = 0;
                        if (zdzsTech2 > 0) {
                            final PlayerBattleAttribute pba2 = dataGetter.getPlayerBattleAttributeDao().read(pgm2.getPlayerId());
                            autoStrategy2 = pba2.getAutoStrategy();
                        }
                        else {
                            autoStrategy2 = -1;
                        }
                        bat.inBattlePlayers.put(pgm2.getPlayerId(), new PlayerInfo(pgm2.getPlayerId(), true, autoStrategy2));
                    }
                }
            }
        }
        final List<PlayerGeneralMilitaryPhantom> list = dataGetter.getPlayerGeneralMilitaryPhantomDao().getPhantomByLocationIdOrderByPlayerIdLvDesc(defId);
        for (final PlayerGeneralMilitaryPhantom pgmPhantom : list) {
            final CampArmy campArmy2 = this.copyArmyFromPhantom(dataGetter, bat, pgmPhantom, 1);
            if (campArmy2 == null) {
                continue;
            }
            attNum += campArmy2.getArmyHpOrg();
            bat.attCamp.add(campArmy2);
        }
        bat.attBaseInfo.setNum(bat.attBaseInfo.getNum() + attNum);
        bat.attBaseInfo.setAllNum(bat.attBaseInfo.getAllNum() + attNum);
    }
    
    public CampArmy copyAttCampFromPlayerTable(final Player player, final PlayerGeneralMilitary pgm, final IDataGetter dataGetter, final int battleType, final long moveCd, final Battle bat, final int batSide) {
        final CampArmy campArmy = new CampArmy();
        final General general = (General)dataGetter.getGeneralCache().get((Object)pgm.getGeneralId());
        campArmy.setSpecialGeneral(new SpecialGeneral(general.getGeneralSpecialInfo().generalType, general.getGeneralSpecialInfo().param, general.getGeneralSpecialInfo().param2));
        final Troop troop = dataGetter.getTroopCache().getTroop(general.getTroop(), player.getPlayerId());
        campArmy.updateDB = true;
        campArmy.setPlayerLv(player.getPlayerLv());
        campArmy.setPlayerId(player.getPlayerId());
        campArmy.setPlayerName(player.getPlayerName());
        campArmy.setForceId(player.getForceId());
        campArmy.setActivityAddExp(dataGetter.getActivityService().getAddBatValue(player.getPlayerId(), player.getPlayerLv()));
        getTerrainValue(bat.terrainVal, troop, batSide, campArmy);
        campArmy.setGeneralLv(pgm.getLv());
        campArmy.setGeneralId(pgm.getGeneralId());
        campArmy.setTroopId(troop.getId());
        campArmy.setTroopName(troop.getName());
        campArmy.setTroopSerial(troop.getSerial());
        campArmy.setTroopType(troop.getType());
        campArmy.setPgmVId(pgm.getVId());
        campArmy.setArmyName(general.getName());
        campArmy.setGeneralName(general.getName());
        campArmy.setGeneralPic(general.getPic());
        campArmy.setQuality(general.getQuality());
        campArmy.setStrength(pgm.getStrength(general.getStrength()));
        campArmy.setLeader(pgm.getLeader(general.getLeader()));
        campArmy.setTacicId(general.getTacticId());
        int forces = pgm.getForces();
        final int remainder = forces % 3;
        forces -= remainder;
        campArmy.setArmyHp(forces);
        campArmy.setArmyHpOrg(forces);
        Builder.getAttDefHp(dataGetter, campArmy);
        final int cityId = bat.getDefBaseInfo().getId();
        final GeneralMoveDto gmd = CityService.getGeneralMoveDto(pgm.getPlayerId(), pgm.getGeneralId());
        if (gmd != null) {
            gmd.moveLine = "";
        }
        try {
            final int done = dataGetter.getPlayerGeneralMilitaryDao().attack(pgm.getPlayerId(), pgm.getGeneralId(), 14, cityId);
            if (done <= 0) {
                return null;
            }
            if (campArmy.getPlayerId() > 0 && !campArmy.isPhantom) {
                NewBattleManager.getInstance().joinBattle(bat, campArmy.getPlayerId(), campArmy.getGeneralId());
            }
            dataGetter.getCityService().updateGNumAndSend(pgm.getLocationId(), cityId);
            campArmy.setId(bat.campNum.getAndIncrement());
            campArmy.setStrategies(troop.getStrategyMap().get(bat.terrainVal));
            if (general.getTacticId() > 0 && pgm.getForces() >= campArmy.getMaxForces()) {
                campArmy.setTacticVal(1);
                if (campArmy.getSpecialGeneral().generalType == 7) {
                    campArmy.setTacticVal((int)campArmy.getSpecialGeneral().param);
                }
            }
            dataGetter.getGeneralService().sendGmUpdate(pgm.getPlayerId(), pgm.getGeneralId(), false);
            TaskMessageHelper.sendWorldMoveTaskMessage(player.getPlayerId());
            return campArmy;
        }
        catch (Exception e) {
            final int capitalId = WorldCityCommon.nationMainCityIdMap.get(player.getForceId());
            dataGetter.getPlayerGeneralMilitaryDao().updateStateLocationId(player.getPlayerId(), pgm.getGeneralId(), 1, capitalId);
            ErrorSceneLog.getInstance().appendErrorMsg("BarbarainBuilder copyAttCampFromPlayerTable \u66f4\u6539\u6b66\u5c06\u72b6\u6001Exception").appendClassName("BarbarainBuilder").appendMethodName("copyAttCampFromPlayerTable").append("PlayerId", pgm.getPlayerId()).append("GeneralId", pgm.getGeneralId()).flush();
            ErrorSceneLog.getInstance().error("BarbarainBuilder copyAttCampFromPlayerTable" + e);
            return null;
        }
    }
    
    public static double getTerrainValue(final int terrainType, final Troop troop, final int batSide, final CampArmy campArmy) {
        final TroopTerrain terrain = troop.getTerrains().get(terrainType);
        int effect = 0;
        if (terrain != null) {
            if (batSide == 1) {
                effect = terrain.getAttEffect();
                campArmy.terrainAttDefAdd.left = effect;
                campArmy.setTerrainQ(terrain.getAttQuality());
            }
            else {
                effect = terrain.getDefEffect();
                campArmy.terrainAttDefAdd.right = effect;
                campArmy.setTerrainQ(terrain.getDefQuality());
            }
        }
        campArmy.setTerrain(effect / 100.0);
        campArmy.terrainAdd = effect;
        return campArmy.getTerrain();
    }
    
    @Override
    public int getBattleSide(final IDataGetter dataGetter, final Player player, final int defId) {
        return 1;
    }
    
    @Override
    public int getBattleSide(final IDataGetter dataGetter, final Player player, final Battle battle) {
        return 1;
    }
    
    @Override
    public String getAttTargetName(final IDataGetter dataGetter, final Battle bat) {
        return ((WorldCity)dataGetter.getWorldCityCache().get((Object)bat.getDefBaseInfo().getId())).getName();
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
        return bat;
    }
    
    @Override
    public void countNpcReward(final boolean attWin, final IDataGetter dataGetter, final Battle bat, final BattleResult battleResult) {
    }
    
    @Override
    public void dealNextNpc(final boolean attWin, final IDataGetter dataGetter, final Battle bat, final BattleResult battleResult) {
        final int cityId = bat.defBaseInfo.id;
        try {
            dataGetter.getCityService().changeState(cityId, 0, false);
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("barbarain battle ended, reset city state only exception.", e);
        }
        for (final CampArmy campArmy : bat.attCamp) {
            if (campArmy.isUpdateDB() && campArmy.armyHpKill > 0) {
                final int forceId = campArmy.forceId;
                final int killAdd = campArmy.armyHpKill;
                NewBattleManager.getInstance().addBarbarainKill(forceId, killAdd);
                this.dealKillrank(false, campArmy.armyHpKill, bat, dataGetter, campArmy.getPlayerId());
            }
        }
    }
    
    @Override
    public void afterBat(final boolean attWin, final IDataGetter dataGetter, final Battle bat) {
        try {
            String name = LocalMessages.MANZU_CHUZHENG_NAME;
            String copperAttribute = "\u51fa\u5f81\u86ee\u65cf\u7ed3\u675f\uff0c\u6298\u7b97\u5e7b\u5f71\u83b7\u5f97\u94f6\u5e01";
            String expAttribute = "\u51fa\u5f81\u86ee\u65cf\u7ed3\u675f\uff0c\u6298\u7b97\u5e7b\u5f71\u589e\u52a0\u7ecf\u9a8c";
            final int forceId = bat.attBaseInfo.forceId;
            final Tuple<Integer, Date> tuple = dataGetter.getNationService().getTryMap().get(forceId);
            if (tuple != null && tuple.right != null && TimeUtil.now2specMs(tuple.right.getTime()) < 1800000L) {
                name = LocalMessages.MANZU_TRY_NAME;
                copperAttribute = "\u56fd\u5bb6\u8bd5\u70bc\u7ed3\u675f\uff0c\u6298\u7b97\u5e7b\u5f71\u83b7\u5f97\u94f6\u5e01";
                expAttribute = "\u56fd\u5bb6\u8bd5\u70bc\u7ed3\u675f\uff0c\u6298\u7b97\u5e7b\u5f71\u589e\u52a0\u7ecf\u9a8c";
            }
            final int cityId = bat.getDefBaseInfo().getId();
            final List<PlayerGeneralMilitaryPhantom> list = dataGetter.getPlayerGeneralMilitaryPhantomDao().getPhantomByLocationIdOrderByPlayerIdLvDesc(cityId);
            final Map<Integer, PGMPhantomToExpObj> playergExpMap = new HashMap<Integer, PGMPhantomToExpObj>();
            for (final PlayerGeneralMilitaryPhantom temp : list) {
                final int playerId = temp.getPlayerId();
                final int gId = temp.getGeneralId();
                PGMPhantomToExpObj obj = playergExpMap.get(playerId);
                if (obj == null) {
                    obj = new PGMPhantomToExpObj();
                    playergExpMap.put(playerId, obj);
                }
                final General general = (General)dataGetter.getGeneralCache().get((Object)gId);
                final Troop troop = dataGetter.getTroopCache().getTroop(general.getTroop(), playerId);
                final int troopId = troop.getId();
                final double troopFoodConsumeCoe = ((TroopConscribe)dataGetter.getTroopConscribeCache().get((Object)troopId)).getFood();
                final int hp = temp.getHp();
                final double troopFood = troopFoodConsumeCoe * hp;
                final double delta = troopFood * 0.15;
                final PGMPhantomToExpObj pgmPhantomToExpObj = obj;
                pgmPhantomToExpObj.copperSum += delta;
                final double attTechAddGZJY = dataGetter.getTechEffectCache().getTechEffect(playerId, 40) / 100.0;
                final PGMPhantomToExpObj pgmPhantomToExpObj2 = obj;
                pgmPhantomToExpObj2.chiefExpSum += (1.0 + attTechAddGZJY) * delta;
                final double gExpAdd = dataGetter.getTechEffectCache().getTechEffect(playerId, 16) / 100.0;
                final double gExp = (1.0 + gExpAdd) * delta;
                final Double gExpSum = obj.gExpMap.get(gId);
                if (gExpSum == null) {
                    obj.gExpMap.put(gId, gExp);
                }
                else {
                    obj.gExpMap.put(gId, gExpSum + gExp);
                }
            }
            for (final Map.Entry<Integer, PGMPhantomToExpObj> entry : playergExpMap.entrySet()) {
                final int playerId = entry.getKey();
                final PGMPhantomToExpObj obj2 = entry.getValue();
                dataGetter.getPlayerResourceDao().addCopperIgnoreMax(playerId, obj2.copperSum, copperAttribute, true);
                final AddExpInfo expResult = dataGetter.getPlayerService().updateExpAndPlayerLevel(playerId, (int)obj2.chiefExpSum, expAttribute);
                dataGetter.getJobService().addJob("autoBattleService", "increaseExp", String.valueOf(playerId) + "#" + expResult.addExp, System.currentTimeMillis(), false);
                final StringBuilder content = new StringBuilder();
                content.append(MessageFormatter.format(LocalMessages.JIEBING_FANHUAN_FORMAT_PART1, new Object[] { name, (int)obj2.copperSum, expResult.addExp }));
                for (final Map.Entry<Integer, Double> entry2 : obj2.gExpMap.entrySet()) {
                    final int gId2 = entry2.getKey();
                    final int gExp2 = (int)(Object)entry2.getValue();
                    final List<UpdateExp> gExpUpList = dataGetter.getGeneralService().updateExpAndGeneralLevel(playerId, gId2, gExp2);
                    if (gExpUpList != null) {
                        int addGExp = 0;
                        for (final UpdateExp ue : gExpUpList) {
                            addGExp += (int)ue.getCurExp();
                        }
                        final String gName = ((General)dataGetter.getGeneralCache().get((Object)gId2)).getName();
                        content.append(MessageFormatter.format(LocalMessages.JIEBING_FANHUAN_FORMAT_PART2, new Object[] { gName, addGExp }));
                    }
                }
                dataGetter.getMailService().writeSystemMail(LocalMessages.T_MAIL_ROLE_SYSTEM, LocalMessages.JIEBING_FANHUAN_TITLE, content.toString(), 1, playerId, new Date());
            }
            dataGetter.getPlayerGeneralMilitaryPhantomDao().deleteByLocationId(cityId);
            final boolean hasGoldOrder = dataGetter.getBattleService().hasGoldOrderInCertainCity(cityId);
            if (hasGoldOrder) {
                dataGetter.getBattleService().deleteAllGoldOrderInBattle(cityId);
            }
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("BarbarainBuilder.afterBat Exception", e);
        }
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
    public void roundReduceTroopSingle(final IDataGetter dataGetter, final Battle bat, final CampArmy campArmyA, final CampArmy campArmyB, final boolean isBeKill, final int lostA, final int lostB) {
        if (lostA < 0) {
            BattleSceneLog.getInstance().debug("AAAAAAAAAAAAAAAAAAAAA city lostA:" + lostA + " " + bat.getBattleId());
        }
        if (campArmyA.updateDB && campArmyA.playerId > 0) {
            if (campArmyA.isPhantom) {
                this.roundReducePlayerPhantom(dataGetter, bat, campArmyA, campArmyB, isBeKill, lostA, lostB);
            }
            else {
                this.roundReduceTruePlayer(dataGetter, bat, campArmyA, campArmyB, isBeKill, lostA, lostB);
            }
        }
        else if (isBeKill) {
            this.dealNpcSlave(dataGetter, bat, campArmyA, campArmyB);
        }
        if (WorldCityCommon.barbarainForceSet.contains(campArmyA.getForceId()) && WorldCityCommon.barbarainCitySet.contains(bat.getDefBaseInfo().id)) {
            if (isBeKill) {
                dataGetter.getBarbarainPhantomDao().deleteById(campArmyA.pgmVId);
                final int forceId = bat.getAttBaseInfo().forceId;
                final int stage = dataGetter.getNationService().getStageByForceId(forceId);
                if (stage < 4) {
                    final StringBuffer sb = new StringBuffer();
                    sb.append(forceId).append("#1");
                    dataGetter.getJobService().addJob("nationService", "addKillGeneralNum", sb.toString(), System.currentTimeMillis(), false);
                }
            }
            else {
                final int hp = campArmyA.armyHpOrg - campArmyA.armyHpLoss;
                dataGetter.getBarbarainPhantomDao().updateHpTacticVal(campArmyA.pgmVId, hp, campArmyA.tacticVal);
            }
        }
    }
    
    private void dealNpcSlave(final IDataGetter dataGetter, final Battle bat, final CampArmy campArmyA, final CampArmy campArmyB) {
        try {
            if (campArmyB.playerId < 0 || campArmyB.isPhantom) {
                return;
            }
            if (campArmyA.generalName == null) {
                ErrorSceneLog.getInstance().appendErrorMsg("campArmyA.generalName == null").appendBattleId(bat.getBattleId()).append("BarbarainPhantom vId", campArmyA.pgmVId).append("generalId", campArmyA.generalId).append("campArmyA.isBarPhantom", campArmyA.isBarPhantom).append("campArmyA.isEA", campArmyA.isEA).append("campArmyA.isBarEA", campArmyA.isBarEA).append("campArmyA.nationTaskEAType", campArmyA.nationTaskEAType).append("city", ((WorldCity)dataGetter.getWorldCityCache().get((Object)bat.defBaseInfo.id)).getName()).appendClassName("BarbarainBuilder").appendMethodName("dealNpcSlave").flush();
                return;
            }
            if (campArmyA.generalLv == 0) {
                ErrorSceneLog.getInstance().appendErrorMsg("campArmyA.generalLv == 0").appendBattleId(bat.getBattleId()).append("BarbarainPhantom vId", campArmyA.pgmVId).append("generalId", campArmyA.generalId).append("campArmyA.isBarPhantom", campArmyA.isBarPhantom).append("campArmyA.isEA", campArmyA.isEA).append("campArmyA.isBarEA", campArmyA.isBarEA).append("campArmyA.nationTaskEAType", campArmyA.nationTaskEAType).append("city", ((WorldCity)dataGetter.getWorldCityCache().get((Object)bat.defBaseInfo.id)).getName()).appendClassName("BarbarainBuilder").appendMethodName("dealNpcSlave").flush();
                return;
            }
            final StringBuilder slaveParam = new StringBuilder();
            slaveParam.append(campArmyB.playerId).append("#").append(campArmyB.generalId).append("#").append(campArmyA.playerId).append("#").append(campArmyA.generalId).append("#").append(campArmyA.killGeneral).append("#").append(2).append("#").append(campArmyA.forceId).append("#").append(campArmyA.generalName).append("#").append(campArmyA.generalLv).append("#");
            Builder.timerLog.info("slaveService.dealSlave. params:" + slaveParam.toString() + "; " + bat.getBattleId());
            dataGetter.getJobService().addJob("slaveService", "dealSlave", slaveParam.toString(), System.currentTimeMillis(), true);
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("BarbarainBuilder.dealNpcSlave catch Exception", e);
        }
    }
    
    public void roundReducePlayerPhantom(final IDataGetter dataGetter, final Battle bat, final CampArmy campArmyA, final CampArmy campArmyB, final boolean isBeKill, final int lostA, final int lostB) {
        if (isBeKill) {
            final int forceId = campArmyA.forceId;
            final int killAdd = campArmyA.armyHpKill;
            NewBattleManager.getInstance().addBarbarainKill(forceId, killAdd);
            this.dealKillrank(false, campArmyA.getArmyHpKill(), bat, dataGetter, campArmyA.getPlayerId());
            final int done = dataGetter.getPlayerGeneralMilitaryPhantomDao().deleteById(campArmyA.pgmVId);
            if (done != 1) {
                ErrorSceneLog.getInstance().appendErrorMsg("phanton delete failed.").appendBattleId(bat.getBattleId()).append("phantom vId", campArmyA.pgmVId).append("city", ((WorldCity)dataGetter.getWorldCityCache().get((Object)bat.defBaseInfo.id)).getName()).append("cityId", bat.defBaseInfo.id).appendClassName("").appendMethodName("roundReducePlayerPhantom");
            }
        }
        else {
            final int hp = campArmyA.armyHpOrg - campArmyA.armyHpLoss;
            final int done2 = dataGetter.getPlayerGeneralMilitaryPhantomDao().updateHp(campArmyA.pgmVId, hp);
            if (done2 != 1) {
                ErrorSceneLog.getInstance().appendErrorMsg("phanton updateHp failed.").appendBattleId(bat.getBattleId()).append("phantom vId", campArmyA.pgmVId).append("hp", hp).append("city", ((WorldCity)dataGetter.getWorldCityCache().get((Object)bat.defBaseInfo.id)).getName()).appendClassName("BarbarainBuilder").appendMethodName("roundReducePlayerPhantom");
            }
        }
    }
    
    public void roundReduceTruePlayer(final IDataGetter dataGetter, final Battle bat, final CampArmy campArmyA, final CampArmy campArmyB, final boolean isBeKill, final int lostA, final int lostB) {
        if (isBeKill) {
            final int forceId = campArmyA.forceId;
            final int killAdd = campArmyA.armyHpKill;
            NewBattleManager.getInstance().addBarbarainKill(forceId, killAdd);
            this.dealKillrank(false, campArmyA.getArmyHpKill(), bat, dataGetter, campArmyA.getPlayerId());
            final int capitalId = WorldCityCommon.nationMainCityIdMap.get(campArmyA.getForceId());
            final int done = dataGetter.getPlayerGeneralMilitaryDao().updateLocationForceSetState1(campArmyA.getPlayerId(), campArmyA.generalId, capitalId, lostA, new Date());
            if (done != 1) {
                ErrorSceneLog.getInstance().appendErrorMsg("updateLocationForceSetState1 fail").appendBattleId(bat.getBattleId()).appendPlayerId(campArmyA.getPlayerId()).appendGeneralId(campArmyA.generalId).append("capitalId", capitalId).append("lostA", lostA).appendMethodName("roundReduceTruePlayer").flush();
            }
            try {
                dataGetter.getCityService().updateGNumAndSend(bat.getDefBaseInfo().getId(), capitalId);
            }
            catch (Exception e) {
                ErrorSceneLog.getInstance().appendErrorMsg("updateGNumAndSend exception").append("playerId", campArmyA.getPlayerId()).append("generalId", campArmyA.generalId).appendClassName("CityService").appendMethodName("updateGNumAndSend").flush();
                ErrorSceneLog.getInstance().error("roundReduceTruePlayer 1", e);
            }
            try {
                dataGetter.getCityService().sendAttMoveInfo(campArmyA.getPlayerId(), campArmyA.generalId, bat.defBaseInfo.id, capitalId, campArmyA.getForceId(), "", campArmyA.getArmyHp(), true);
            }
            catch (Exception e) {
                ErrorSceneLog.getInstance().appendErrorMsg("sendAttMoveInfo exception").append("playerId", campArmyA.getPlayerId()).append("generalId", campArmyA.generalId).appendClassName("CityService").appendMethodName("sendAttMoveInfo").flush();
                ErrorSceneLog.getInstance().error("roundReduceTruePlayer 2", e);
            }
            try {
                final String cgm = dataGetter.getCityService().getColoredGeneralName(campArmyA.getGeneralId());
                dataGetter.getCityDataCache().fireCityMoveMessage(campArmyA.getPlayerId(), bat.defBaseInfo.id, capitalId, cgm);
            }
            catch (Exception e) {
                ErrorSceneLog.getInstance().error("roundReduceTruePlayer 3", e);
            }
            try {
                dataGetter.getGeneralService().sendGmForcesReduce(campArmyA.getPlayerId(), campArmyA.generalId, lostA, lostB, bat.inSceneSet.contains(campArmyA.getPlayerId()), campArmyB.playerName, campArmyB.generalName, campArmyB.quality);
                dataGetter.getGeneralService().sendGmUpdate(campArmyA.getPlayerId(), campArmyA.generalId, bat.inSceneSet.contains(campArmyA.getPlayerId()));
            }
            catch (Exception e) {
                ErrorSceneLog.getInstance().error("roundReduceTruePlayer 4", e);
            }
            dataGetter.getAutoBattleService().nextRoundAfterGeneralDead(campArmyA.getPlayerId(), campArmyA.generalId);
        }
        else {
            try {
                final int done2 = dataGetter.getPlayerGeneralMilitaryDao().consumeForces(campArmyA.getPlayerId(), campArmyA.generalId, lostA, new Date());
                if (done2 != 1) {
                    ErrorSceneLog.getInstance().appendErrorMsg("consumeForces fail").appendBattleId(bat.getBattleId()).appendPlayerId(campArmyA.getPlayerId()).appendGeneralId(campArmyA.generalId).append("lostA", lostA).appendMethodName("roundReduceTruePlayer").flush();
                }
                bat.worldSceneLog.appendLogMsg("round reduce force").newLine().Indent().appendPlayerName(campArmyA.playerName).appendGeneralName(campArmyA.generalName).append("reduce", lostA).appendClassName("BarbarainBuilder").appendMethodName("roundReduceTroopSingle").newLine();
                dataGetter.getGeneralService().sendGmForcesReduce(campArmyA.getPlayerId(), campArmyA.generalId, lostA, lostB, bat.inSceneSet.contains(campArmyA.getPlayerId()), campArmyB.playerName, campArmyB.generalName, campArmyB.quality);
            }
            catch (Exception e2) {
                ErrorSceneLog.getInstance().error("roundReduceTruePlayer 5", e2);
            }
        }
    }
    
    @Override
    public void tacticUpdateDB(final IDataGetter dataGetter, final Battle bat, final LinkedList<CampArmy> campList, final CampArmy exeTacticCa, final CampArmy firstDefCa, final TacticInfo tacticInfoA) {
        if (tacticInfoA.reduceMap == null) {
            return;
        }
        for (final CampArmy campArmy : tacticInfoA.reduceMap.keySet()) {
            if (campArmy == firstDefCa) {
                continue;
            }
            final int reduce = tacticInfoA.reduceMap.get(campArmy);
            final boolean dead = campArmy.getArmyHpLoss() >= campArmy.getArmyHpOrg();
            if (WorldCityCommon.barbarainForceSet.contains(campArmy.getForceId()) && WorldCityCommon.barbarainCitySet.contains(bat.getDefBaseInfo().id)) {
                if (dead) {
                    dataGetter.getBarbarainPhantomDao().deleteById(campArmy.pgmVId);
                    final int forceId = bat.getAttBaseInfo().forceId;
                    final int stage = dataGetter.getNationService().getStageByForceId(forceId);
                    if (stage < 4) {
                        final StringBuffer sb = new StringBuffer();
                        sb.append(forceId).append("#1");
                        dataGetter.getJobService().addJob("nationService", "addKillGeneralNum", sb.toString(), System.currentTimeMillis(), false);
                    }
                }
                else {
                    final int hp = campArmy.armyHpOrg - campArmy.armyHpLoss;
                    dataGetter.getBarbarainPhantomDao().updateHpTacticVal(campArmy.pgmVId, hp, campArmy.tacticVal);
                }
            }
            final int exeTacticCaLost = 0;
            if (campArmy.updateDB && campArmy.playerId > 0) {
                if (campArmy.isPhantom) {
                    this.roundReducePlayerPhantom(dataGetter, bat, campArmy, exeTacticCa, dead, reduce, exeTacticCaLost);
                }
                else {
                    this.roundReduceTruePlayer(dataGetter, bat, campArmy, exeTacticCa, dead, reduce, exeTacticCaLost);
                }
            }
            else {
                if (!dead) {
                    continue;
                }
                this.dealNpcSlave(dataGetter, bat, campArmy, exeTacticCa);
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
            if (!exeTacticCa.isPhantom) {
                dataGetter.getGeneralService().sendGmForcesReduce(exeTacticCa.playerId, exeTacticCa.generalId, 0, tacticInfoA.allCReduce, bat.isInSceneSet(exeTacticCa.playerId));
            }
        }
    }
    
    @Override
    public void endCampsDeal(final IDataGetter dataGetter, final Battle bat, final BattleResult battleResult, final boolean attWin) {
        int i = 1;
        for (final CampArmy ca : bat.attCamp) {
            this.updateGeneralDB(dataGetter, bat, ca, attWin);
            BattleSceneLog.getInstance().info("#batId:" + bat.getBattleId() + "_" + bat.getStartTime() + "#quit:win#side:att" + "#playerId:" + ca.getPlayerId() + ":" + ca.isPhantom + "#general:" + ca.getGeneralId() + "#attSize:" + (bat.attCamp.size() - i));
            ++i;
        }
        final int cityId = bat.defBaseInfo.id;
        dataGetter.getBarbarainPhantomDao().resetStateByLocationAndState(cityId, 3);
    }
    
    @Override
    public void updateGeneralDB(final IDataGetter dataGetter, final Battle bat, final CampArmy ca, final boolean attWin) {
        final PlayerInfo pi = bat.inBattlePlayers.get(ca.getPlayerId());
        if (pi == null) {
            return;
        }
        if (!ca.inBattle || ca.armyHp < 0) {
            return;
        }
        if (!bat.inBattlePlayers.containsKey(ca.getPlayerId())) {
            return;
        }
        if (ca.getPlayerId() < -1) {
            dataGetter.getCityNpcDao().reduceHp(ca.armyHpLoss);
            return;
        }
        if (ca.killGeneral > pi.maxKillG) {
            pi.maxKillG = ca.killGeneral;
        }
        final int state = 1;
        final int capitalId = WorldCityCommon.nationMainCityIdMap.get(ca.getForceId());
        final int done = dataGetter.getPlayerGeneralMilitaryDao().updateLocationForceSetState1(ca.getPlayerId(), ca.getGeneralId(), capitalId, state, new Date());
        NewBattleManager.getInstance().quitBattle(bat, ca.getPlayerId(), ca.getGeneralId());
        try {
            dataGetter.getCityService().sendAttMoveInfo(ca.getPlayerId(), ca.generalId, bat.defBaseInfo.id, capitalId, ca.getForceId(), "", ca.getArmyHp(), true);
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().appendErrorMsg("sendAttMoveInfo exception").append("playerId", ca.getPlayerId()).append("generalId", ca.generalId).appendClassName("CityService").appendMethodName("sendAttMoveInfo").flush();
            ErrorSceneLog.getInstance().error("updateGeneralDB", e);
        }
        dataGetter.getGeneralService().sendGmUpdate(ca.playerId, ca.generalId, bat.inSceneSet.contains(ca.playerId));
    }
    
    @Override
    public int quitUpdateDb(final IDataGetter dataGetter, final CampArmy ca, final Battle bat, final PlayerInfo pi) {
        ca.inBattle = false;
        final int reduceNum = ca.armyHp;
        ca.armyHp = -1;
        if (pi.isAttSide) {
            bat.attCamp.remove(ca);
            BattleSceneLog.getInstance().info("#batId:" + bat.getBattleId() + "_" + bat.getStartTime() + "#quit:quit#side:att" + "#playerId:" + ca.getPlayerId() + ":" + ca.isPhantom + "#general:" + ca.getGeneralId() + "#attSize:" + bat.attCamp.size());
        }
        else {
            bat.defCamp.remove(ca);
            BattleSceneLog.getInstance().info("#batId:" + bat.getBattleId() + "_" + bat.getStartTime() + "#quit:quit#side:def" + "#playerId:" + ca.getPlayerId() + ":" + ca.isPhantom + "#general:" + ca.getGeneralId() + "#defSize:" + bat.defCamp.size());
        }
        if (ca.getPlayerId() > 0 && !ca.isPhantom) {
            NewBattleManager.getInstance().quitBattle(bat, ca.getPlayerId(), ca.getGeneralId());
        }
        dataGetter.getPlayerGeneralMilitaryDao().restartRecruit(ca.playerId, ca.generalId, ca.isInRecruit ? 1 : 0, new Date());
        dataGetter.getGroupArmyService().quit(ca.playerId, ca.generalId);
        return reduceNum;
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
    public void dealKillTotalStaff(final IDataGetter dataGetter, final Battle bat, final RoundInfo roundInfo) {
    }
    
    @Override
    public void getExReward(final boolean attWin, final IDataGetter dataGetter, final PlayerInfo pi, final Battle bat) {
        if (!attWin) {
            return;
        }
        final int stage = dataGetter.getNationService().getStageByForceId(bat.getAttBaseInfo().forceId);
        final int degree = bat.defBaseInfo.id2;
        if (stage >= 4 && degree > 0) {
            final Barbarain barbarain = dataGetter.getBarbarainCache().getByDegree(degree);
            final BattleDrop battleDrop = barbarain.getBattleDrop();
            AsynchronousDBOperationManager.getInstance().addBattleDropRetry(pi.playerId, battleDrop, "\u6218\u6597\u83b7\u5f97");
            pi.addDrop(battleDrop);
            final String content = MessageFormatter.format(LocalMessages.BARBARAIN_BATTLE_WIN, new Object[] { battleDrop.num });
            dataGetter.getMailService().writeSystemMail(LocalMessages.T_MAIL_ROLE_SYSTEM, LocalMessages.BARBARAIN_BATTLE_REWARD, content, 1, pi.playerId, new Date());
        }
    }
    
    @Override
    public LinkedList<CampArmy> addDefNpc(final IDataGetter dataGetter, final Battle bat, final RoundInfo roundInfo) {
        final int degree = bat.defBaseInfo.id2;
        if (degree < 1) {
            return null;
        }
        if (bat.defCamp.size() >= 100) {
            return null;
        }
        roundInfo.needPushReport13 = true;
        final LinkedList<CampArmy> campChange = new LinkedList<CampArmy>();
        int addNum = 0;
        final Barbarain barbarain = dataGetter.getBarbarainCache().getByDegree(degree);
        final Player player = new Player();
        player.setPlayerId(-1);
        player.setPlayerLv(barbarain.getLv());
        Integer[] ArmyIds = null;
        final int defForceId = bat.defBaseInfo.forceId;
        player.setForceId(defForceId);
        switch (defForceId) {
            case 101: {
                player.setPlayerName(barbarain.getWeiName());
                ArmyIds = barbarain.getWeiArmyIds();
                break;
            }
            case 102: {
                player.setPlayerName(barbarain.getShuName());
                ArmyIds = barbarain.getShuArmyIds();
                break;
            }
            case 103: {
                player.setPlayerName(barbarain.getWuName());
                ArmyIds = barbarain.getWuArmyIds();
                break;
            }
            default: {
                ErrorSceneLog.getInstance().appendErrorMsg("defId error").append("defForceId", defForceId).appendBattleId(bat.getBattleId()).appendMethodName("addDefNpc").appendClassName("BarbarainBuilder").flush();
                break;
            }
        }
        for (int i = bat.defCamp.size(); i < 100; ++i) {
            final int randInt = this.rand.nextInt(ArmyIds.length);
            final int npc = ArmyIds[randInt];
            final Army armyCach = (Army)dataGetter.getArmyCache().get((Object)npc);
            final int id = bat.campNum.getAndIncrement();
            final CampArmy campArmy = Builder.copyArmyFromCach(player, npc, dataGetter, id, bat.terrainVal, armyCach.getGeneralLv());
            bat.newlyJoinSet.add(campArmy);
            addNum += campArmy.getArmyHpOrg();
            bat.defCamp.add(campArmy);
        }
        final BaseInfo defBaseInfo = bat.defBaseInfo;
        defBaseInfo.allNum += addNum;
        final BaseInfo defBaseInfo2 = bat.defBaseInfo;
        defBaseInfo2.num += addNum;
        return campChange;
    }
    
    @Override
    public void systemSinglePK(final IDataGetter dataGetter, final Battle bat, final RoundInfo roundInfo) {
        try {
            if (bat.attCamp.size() <= 50) {
                return;
            }
            if (bat.defCamp.size() <= 50) {
                return;
            }
            for (int singlePKNum = (int)Math.ceil((bat.attCamp.size() + bat.defCamp.size()) / 150.0), i = 0; i < singlePKNum; ++i) {
                final CampArmy[] cas = new CampArmy[2];
                int chooseId = 10;
                if (bat.getAttCamp().size() <= chooseId) {
                    chooseId = bat.getAttCamp().size() - 1;
                }
                if (bat.attCamp.size() <= chooseId) {
                    return;
                }
                if (bat.defCamp.size() <= chooseId) {
                    return;
                }
                cas[0] = bat.attCamp.get(chooseId);
                cas[1] = bat.defCamp.get(chooseId);
                if (cas[0].onQueues || cas[1].onQueues) {
                    return;
                }
                bat.attCamp.remove(cas[0]);
                bat.defCamp.remove(cas[1]);
                bat.attBaseInfo.setAllNum(bat.attBaseInfo.getAllNum() - cas[0].armyHp);
                bat.attBaseInfo.setNum(bat.attBaseInfo.getNum() - cas[0].armyHp);
                bat.defBaseInfo.setAllNum(bat.defBaseInfo.getAllNum() - cas[1].armyHp);
                bat.defBaseInfo.setNum(bat.defBaseInfo.getNum() - cas[1].armyHp);
                dataGetter.getBattleService().createOneToOneBattle(-1, cas, bat, 3, 0);
            }
        }
        catch (Exception e) {
            BattleSceneLog.getInstance().error("BarbarainBuilder systemSinglePK ERROR", e);
        }
    }
    
    @Override
    public int isBattleEnd(final IDataGetter dataGetter, final Battle bat) {
        final int stage = dataGetter.getNationService().getStageByForceId(bat.getAttBaseInfo().forceId);
        int degree = bat.defBaseInfo.id2;
        if (stage >= 4 && degree > 0) {
            final Barbarain barbarain = dataGetter.getBarbarainCache().getByDegree(degree);
            if (barbarain == null) {
                ErrorSceneLog.getInstance().appendErrorMsg("isBattleEnd, barbarain is null").append("degree", degree).appendBattleId(bat.battleId).append("ticket", bat.ticket.get()).flush();
            }
            final long target = barbarain.getTarget();
            final int forceId = bat.attBaseInfo.forceId;
            final long killTotal = NewBattleManager.getInstance().getBarbarainKillByForceId(forceId);
            if (killTotal >= target) {
                return 3;
            }
            degree = dataGetter.getRankService().hasBarTasks(forceId);
            if (killTotal < target && degree == 0) {
                return 2;
            }
            final int aQLsize = bat.attList.size();
            final int dQLsize = bat.defList.size();
            if (aQLsize < 1 && dQLsize < 1) {
                return 4;
            }
            if (aQLsize < 1) {
                return 2;
            }
            if (dQLsize < 1) {
                return 3;
            }
            return 1;
        }
        else {
            final int aQLsize2 = bat.attList.size();
            final int dQLsize2 = bat.defList.size();
            if (aQLsize2 < 1 && dQLsize2 < 1) {
                return 4;
            }
            if (aQLsize2 < 1) {
                return 2;
            }
            if (dQLsize2 < 1) {
                final int forceId2 = bat.getAttBaseInfo().forceId;
                final ForceInfo fi = dataGetter.getForceInfoDao().read(forceId2);
                final int maxNum = dataGetter.getCdExamsCache().getGeneralNum(fi.getId(), stage);
                final int lastGeneralNum = fi.getGeneralNum();
                if (lastGeneralNum < maxNum) {
                    final StringBuffer sb = new StringBuffer();
                    sb.append(forceId2).append("#").append(maxNum - lastGeneralNum);
                    dataGetter.getJobService().addJob("nationService", "addKillGeneralNum", sb.toString(), System.currentTimeMillis(), false);
                    ErrorSceneLog.getInstance().error("#class:RankService#method:addKillGeneralNum#lastGeneralNum\uff1a" + lastGeneralNum + "#maxNum:" + maxNum);
                }
                return 3;
            }
            return 1;
        }
    }
    
    @Override
    public void initZhengZhaoLingJoinPlayer(final IDataGetter dataGetter, final Battle battle, final Player player, final int battleSide, final List<PlayerGeneralMilitary> pgmList, final BaseInfo baseInfo, final LinkedList<CampArmy> campMap, final LinkedList<CampArmy> campChange, final double teamEffect, final int attEffect, final int defEffect, final String playerName) {
        CampArmy campArmy = null;
        int num = 0;
        for (int i = 0; i < pgmList.size(); ++i) {
            final PlayerGeneralMilitary pgm = pgmList.get(i);
            campArmy = copyCampFromPlayerTable(player, pgm, dataGetter, this.getGeneralState(), battle, battleSide);
            if (campArmy != null) {
                num += campArmy.getArmyHpOrg();
                final String teamName = String.valueOf(playerName) + LocalMessages.GOLDORDER_NAME;
                campArmy.setAttEffect(campArmy.getAttEffect() + attEffect);
                campArmy.setDefEffect(campArmy.getDefEffect() + defEffect);
                campArmy.setTeamEffect(teamEffect);
                campArmy.setTeamGenreal(teamName);
                campMap.add(campArmy);
                BattleSceneLog.getInstance().info("#batId:" + battle.getBattleId() + "_" + battle.getStartTime() + "#add:join#side:" + ((battleSide == 1) ? "att" : "def") + "#playerId:" + campArmy.getPlayerId() + ":" + campArmy.isPhantom + "#general:" + campArmy.getGeneralId() + "#" + ((battleSide == 1) ? "att" : "def") + "Size:" + campMap.size());
                campChange.add(campArmy);
            }
        }
        baseInfo.setNum(baseInfo.getNum() + num);
        baseInfo.setAllNum(baseInfo.getAllNum() + num);
    }
    
    public static CampArmy copyCampFromPlayerTable(final Player player, final PlayerGeneralMilitary pgm, final IDataGetter dataGetter, final int generalState, final Battle bat, final int batSide) {
        final CampArmy campArmy = new CampArmy();
        final General general = (General)dataGetter.getGeneralCache().get((Object)pgm.getGeneralId());
        final SpecialGeneral sg = new SpecialGeneral(general.getGeneralSpecialInfo().generalType, general.getGeneralSpecialInfo().param, general.getGeneralSpecialInfo().param2);
        if (general.getGeneralSpecialInfo().generalType == 2) {
            sg.param = ((WorldCity)dataGetter.getWorldCityCache().get((Object)bat.getDefBaseInfo().getId())).getDistance(player.getForceId());
        }
        campArmy.setSpecialGeneral(sg);
        final Troop troop = dataGetter.getTroopCache().getTroop(general.getTroop(), player.getPlayerId());
        campArmy.updateDB = true;
        campArmy.setPlayerLv(player.getPlayerLv());
        campArmy.setPlayerId(player.getPlayerId());
        campArmy.setPlayerName(player.getPlayerName());
        campArmy.setForceId(player.getForceId());
        campArmy.setActivityAddExp(dataGetter.getActivityService().getAddBatValue(player.getPlayerId(), player.getPlayerLv()));
        getTerrainValue(bat.terrainVal, troop, batSide, campArmy);
        campArmy.setGeneralLv(pgm.getLv());
        campArmy.setGeneralId(pgm.getGeneralId());
        campArmy.setTroopId(troop.getId());
        campArmy.setTroopName(troop.getName());
        campArmy.setTroopSerial(troop.getSerial());
        campArmy.setTroopType(troop.getType());
        campArmy.setPgmVId(pgm.getVId());
        campArmy.setArmyName(general.getName());
        campArmy.setGeneralName(general.getName());
        campArmy.setGeneralPic(general.getPic());
        campArmy.setQuality(general.getQuality());
        campArmy.setStrength(pgm.getStrength(general.getStrength()));
        campArmy.setLeader(pgm.getLeader(general.getLeader()));
        campArmy.setTacicId(general.getTacticId());
        int forces = pgm.getForces();
        final int remainder = forces % 3;
        forces -= remainder;
        campArmy.setArmyHp(forces);
        campArmy.setArmyHpOrg(forces);
        Builder.getAttDefHp(dataGetter, campArmy);
        if (pgm.getState() == 1 || pgm.getAuto() == 1) {
            campArmy.setInRecruit(true);
        }
        final int cityId = bat.getDefBaseInfo().getId();
        final GeneralMoveDto gmd = CityService.getGeneralMoveDto(pgm.getPlayerId(), pgm.getGeneralId());
        if (gmd != null) {
            gmd.moveLine = "";
        }
        try {
            final int done = dataGetter.getPlayerGeneralMilitaryDao().attack(pgm.getPlayerId(), pgm.getGeneralId(), generalState, cityId);
            if (done <= 0) {
                return null;
            }
            if (campArmy.getPlayerId() > 0 && !campArmy.isPhantom) {
                NewBattleManager.getInstance().joinBattle(bat, campArmy.getPlayerId(), campArmy.getGeneralId());
            }
            campArmy.setId(bat.campNum.getAndIncrement());
            if (batSide == 1) {
                campArmy.setStrategies(troop.getStrategyMap().get(bat.terrainVal));
            }
            else {
                campArmy.setStrategies(troop.getStrategyDefMap().get(bat.terrainVal));
            }
            if (general.getTacticId() > 0 && pgm.getForces() >= campArmy.getMaxForces()) {
                campArmy.setTacticVal(1);
                if (campArmy.getSpecialGeneral().generalType == 7) {
                    campArmy.setTacticVal((int)campArmy.getSpecialGeneral().param);
                }
            }
            dataGetter.getGeneralService().sendGmUpdate(pgm.getPlayerId(), pgm.getGeneralId(), false);
            TaskMessageHelper.sendWorldMoveTaskMessage(player.getPlayerId());
            return campArmy;
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().appendErrorMsg("CityBuilder copyAttCampFromPlayerTable \u66f4\u6539\u6b66\u5c06\u72b6\u6001Exception").appendClassName("Builder").appendMethodName("copyAttCampFromPlayerTable").append("PlayerId", pgm.getPlayerId()).append("GeneralId", pgm.getGeneralId()).flush();
            ErrorSceneLog.getInstance().error("cityBuilder copyCampFromPlayerTable", e);
            final int capitalId = WorldCityCommon.nationMainCityIdMap.get(player.getForceId());
            dataGetter.getPlayerGeneralMilitaryDao().updateStateLocationId(player.getPlayerId(), pgm.getGeneralId(), 1, capitalId);
            return null;
        }
    }
    
    class PGMPhantomToExpObj
    {
        double copperSum;
        double chiefExpSum;
        Map<Integer, Double> gExpMap;
        
        PGMPhantomToExpObj() {
            this.copperSum = 0.0;
            this.chiefExpSum = 0.0;
            this.gExpMap = new HashMap<Integer, Double>();
        }
    }
}
