package com.reign.gcld.battle.scene;

import com.reign.gcld.juben.service.*;
import com.reign.gcld.common.*;
import com.reign.gcld.battle.service.*;
import com.reign.framework.json.*;
import com.reign.gcld.general.domain.*;
import com.reign.gcld.phantom.common.*;
import com.reign.gcld.common.util.*;
import com.reign.gcld.juben.common.*;
import com.reign.gcld.player.domain.*;
import com.reign.gcld.player.dto.*;
import com.reign.gcld.scenario.message.*;
import com.reign.gcld.battle.domain.*;
import com.reign.gcld.world.service.*;
import com.reign.util.*;
import java.util.*;
import com.reign.gcld.general.dto.*;
import com.reign.gcld.sdata.common.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.gcld.battle.common.*;
import com.reign.gcld.battle.reward.*;

public class ScenarioBuilder extends Builder
{
    public static final int MAX_NPC_CHECK_NUM = 200;
    
    public ScenarioBuilder(final int battleType) {
        this.battleType = battleType;
    }
    
    @Override
    public void setAttDefBaseInfo(final IDataGetter dataGetter, final Battle battle, final BattleAttacker battleAttacker, final int battleType, final int defId) {
        if (battleAttacker.attPlayerId > 0) {
            battle.attBaseInfo.setId(battleAttacker.attPlayerId);
        }
        else {
            battle.attBaseInfo.setId(battleAttacker.defPlayerId);
        }
        battle.defBaseInfo.setId(defId);
    }
    
    @Override
    public Tuple<Boolean, String> attPermitCreate(final IDataGetter dataGetter, final int playerId, final int defId) {
        final Tuple<Boolean, String> tuple = new Tuple();
        tuple.left = false;
        final JuBenCityDto juBenCityDto = JuBenManager.getInstance().getJuBenCityDto(playerId, defId);
        if (juBenCityDto == null) {
            tuple.right = LocalMessages.JUBEN_NO_THIS_NODE;
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
        final String battleId = NewBattleManager.getBattleId(18, playerId, defId);
        final Battle battle = NewBattleManager.getInstance().getBattleByBatId(battleId);
        if (battle == null) {
            tuple.right = JsonBuilder.getJson(State.FAIL, LocalMessages.BATTLE_INFO_WORLD_BATENDED);
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
        final Tuple<Boolean, String> tuple = new Tuple();
        tuple.left = false;
        final JuBenCityDto juBenCityDto = JuBenManager.getInstance().getJuBenCityDto(player.getPlayerId(), defId);
        if (juBenCityDto == null) {
            tuple.right = LocalMessages.JUBEN_NO_THIS_NODE;
            return tuple;
        }
        if (player.getForceId() == juBenCityDto.forceId) {
            tuple.right = LocalMessages.BATTLE_INFO_WORLD_CITY_SELF;
            return tuple;
        }
        tuple.left = true;
        return tuple;
    }
    
    @Override
    public Tuple<Boolean, String> canJoinBattle(final Player player, final List<PlayerGeneralMilitary> pgmList, final IDataGetter dataGetter, final Battle battle) {
        final Tuple<Boolean, String> tuple = new Tuple();
        tuple.left = false;
        if (player.getPlayerId() != battle.attBaseInfo.id && battle.attBaseInfo.id > 0) {
            tuple.right = LocalMessages.JUBEN_CANNOT_JOIN;
            return tuple;
        }
        final Set<Integer> gJoinInSet = new HashSet<Integer>();
        for (final PlayerGeneralMilitary pgm : pgmList) {
            gJoinInSet.add(pgm.getGeneralId());
        }
        LinkedList<CampArmy> campList = null;
        if (player.getForceId() == battle.defBaseInfo.forceId) {
            campList = battle.defCamp;
        }
        else {
            campList = battle.attCamp;
        }
        for (final CampArmy campArmy : campList) {
            if (campArmy.playerId == player.getPlayerId() && campArmy.updateDB && !campArmy.isPhantom && gJoinInSet.contains(campArmy.generalId)) {
                dataGetter.getPlayerGeneralMilitaryDao().updateStateByPidAndGid(campArmy.playerId, campArmy.generalId, this.getGeneralState(), new Date());
                dataGetter.getGeneralService().sendGmStateSet(campArmy.playerId, campArmy.generalId, this.getGeneralState());
                tuple.right = String.valueOf(campArmy.generalName) + LocalMessages.JUBEN_PGM_ALREADY_IN;
                return tuple;
            }
        }
        tuple.left = true;
        return tuple;
    }
    
    @Override
    public Terrain getTerrain(final int playerId, final int defId, final IDataGetter dataGetter) {
        final SoloCity soloCity = (SoloCity)dataGetter.getSoloCityCache().get((Object)defId);
        final int display = (soloCity == null) ? 1 : soloCity.getTerrain();
        return new Terrain(display, (soloCity == null) ? 1 : soloCity.getTerrainEffectType(), display);
    }
    
    @Override
    public byte[] getOtherBatInfo(final IDataGetter dataGetter, final int defId, final int playerId, final int battleSide, PlayerBattleAttribute pba) {
        final JsonDocument doc = new JsonDocument();
        doc.createElement("targetName", ((SoloCity)dataGetter.getSoloCityCache().get((Object)defId)).getName());
        if (pba == null) {
            pba = dataGetter.getPlayerBattleAttributeDao().read(playerId);
        }
        if (1 == battleSide) {
            final long youdiCd = pba.getYoudiTime() - System.currentTimeMillis();
            doc.createElement("cd", (youdiCd < 0L) ? 0L : youdiCd);
        }
        else {
            final long chujiCd = pba.getChujiTime() - System.currentTimeMillis();
            doc.createElement("cd", (chujiCd < 0L) ? 0L : chujiCd);
        }
        doc.createElement("changeBat", pba.getChangebat());
        int maxFreePc = 30;
        final PlayerPhantomObj playerPhantomObj = PhantomManager.getInstance().playerPhantomObjMap.get(playerId);
        if (playerPhantomObj != null) {
            maxFreePc = playerPhantomObj.maxPhantomNum;
        }
        doc.createElement("maxFreePc", maxFreePc);
        if (dataGetter.getJuBenService().isInWorldDrama(playerId)) {
            doc.createElement("freePhantomCount", pba.getVip3PhantomCount());
        }
        else {
            final JuBenDto juBenDto = JuBenManager.getInstance().getByPid(playerId);
            if (juBenDto != null) {
                doc.createElement("freePhantomCount", juBenDto.maxJieBingCount - juBenDto.jieBingCount);
                doc.createElement("maxFreePc", juBenDto.maxJieBingCount);
            }
        }
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
        return doc.toByte();
    }
    
    @Override
    public int getBatInfo(final IDataGetter dataGetter, final int defId) {
        final SoloCity soloCity = (SoloCity)dataGetter.getSoloCityCache().get((Object)defId);
        return this.battleType * 100000 + soloCity.getSoloId();
    }
    
    @Override
    public byte[] getPrepareInfo(final IDataGetter dataGetter, final PlayerDto playerDto, final int defId, final Battle battle, final int terrain) {
        final JsonDocument doc = new JsonDocument();
        final SoloCity soloCity = (SoloCity)dataGetter.getSoloCityCache().get((Object)defId);
        doc.createElement("bat", this.battleType * 100000 + soloCity.getSoloId());
        return doc.toByte();
    }
    
    @Override
    public byte[] getBattleTopInfo(final IDataGetter dataGetter, final PlayerDto playerDto, final Battle battle) {
        final JsonDocument doc = new JsonDocument();
        final SoloCity soloCity = (SoloCity)dataGetter.getSoloCityCache().get((Object)battle.defBaseInfo.id);
        doc.createElement("bat", this.battleType * 100000 + soloCity.getSoloId());
        return doc.toByte();
    }
    
    @Override
    public String getBattleId(final IDataGetter dataGetter, final Player player, final int defId) {
        return NewBattleManager.getBattleId(this.battleType, player.getPlayerId(), defId);
    }
    
    @Override
    public void inBattleInfo(final int playerId, final boolean inBattle) {
    }
    
    @Override
    public void addInSceneSet(final Battle bat, final int playerId) {
    }
    
    @Override
    public void sendBattleInfo(final IDataGetter dataGetter, final Battle bat, final BattleAttacker battleAttacker) {
        final Player player = battleAttacker.attPlayer;
        final int nodeId = bat.defBaseInfo.id;
        try {
            if (player == null) {
                dataGetter.getJuBenService().changeState(bat.getDefBaseInfo().id2, bat.defBaseInfo.getId(), 1, false);
            }
            else {
                dataGetter.getJuBenService().changeState(player.getPlayerId(), bat.defBaseInfo.getId(), 1, false);
                if (battleAttacker.pgmList != null) {
                    for (final PlayerGeneralMilitary pgm : battleAttacker.pgmList) {
                        ScenarioEventMessageHelper.sendMoveToCitykMessage(pgm.getPlayerId(), pgm.getGeneralId(), nodeId);
                    }
                }
            }
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("ScenarioBuilder sendBattleInfo set node state catch Exception. nodeId:" + nodeId, e);
        }
    }
    
    @Override
    public boolean initDefCamp(final IDataGetter dataGetter, final BattleAttacker battleAttacker, final int nodeId, final Battle bat) {
        int playerId = 0;
        if (battleAttacker.attPlayer != null) {
            playerId = battleAttacker.attPlayer.getPlayerId();
        }
        else {
            playerId = battleAttacker.defPlayerId;
            bat.defBaseInfo.id2 = playerId;
        }
        final JuBenDto juBenDto = JuBenManager.getInstance().getByPid(playerId);
        final int capitalId = juBenDto.capital;
        final JuBenCityDto juBenCityDto = JuBenManager.getInstance().getJuBenCityDto(playerId, nodeId);
        final int battleSide = 1;
        int defNum = 0;
        final List<PlayerGeneralMilitary> pgmList = dataGetter.getPlayerGeneralMilitaryDao().getMilitaryList(playerId);
        for (final PlayerGeneralMilitary pgm : pgmList) {
            if (pgm.getForceId() != juBenCityDto.forceId) {
                continue;
            }
            if (pgm.getJubenLoId() != juBenCityDto.cityId) {
                continue;
            }
            if (pgm.getState() > 1) {
                dataGetter.getPlayerGeneralMilitaryDao().moveJuben(pgm.getPlayerId(), pgm.getGeneralId(), pgm.getState(), capitalId);
                dataGetter.getGeneralService().sendGmStateSet(pgm.getPlayerId(), pgm.getGeneralId(), pgm.getState());
            }
            else {
                final int maxHp = dataGetter.getBattleDataCache().getMaxHp(pgm);
                if (pgm.getForces() * 1.0 / maxHp < 0.05) {
                    dataGetter.getPlayerGeneralMilitaryDao().moveJuben(pgm.getPlayerId(), pgm.getGeneralId(), pgm.getState(), capitalId);
                    dataGetter.getGeneralService().sendGmStateSet(pgm.getPlayerId(), pgm.getGeneralId(), pgm.getState());
                }
                else {
                    final Player defPlayer = dataGetter.getPlayerDao().read(pgm.getPlayerId());
                    final CampArmy campArmy = this.copyArmyFromPlayerTable(defPlayer, pgm, dataGetter, this.getGeneralState(), bat, battleSide);
                    if (campArmy == null) {
                        ErrorSceneLog.getInstance().appendErrorMsg("node battle init defCamp get null campArmy").appendBattleId(bat.getBattleId()).appendPlayerName(defPlayer.getPlayerName()).appendPlayerId(defPlayer.getPlayerId()).appendGeneralId(pgm.getPlayerId()).append("vId", pgm.getVId()).flush();
                    }
                    else {
                        defNum += campArmy.getArmyHpOrg();
                        bat.defCamp.add(campArmy);
                        if (bat.inBattlePlayers.get(pgm.getPlayerId()) != null) {
                            continue;
                        }
                        final int zdzsTech = dataGetter.getTechEffectCache().getTechEffect(pgm.getPlayerId(), 43);
                        int autoStrategy = 0;
                        if (zdzsTech > 0) {
                            final PlayerBattleAttribute pba = dataGetter.getPlayerBattleAttributeDao().read(pgm.getPlayerId());
                            autoStrategy = pba.getAutoStrategy();
                        }
                        else {
                            autoStrategy = -1;
                        }
                        bat.inBattlePlayers.put(pgm.getPlayerId(), new PlayerInfo(pgm.getPlayerId(), false, autoStrategy));
                    }
                }
            }
        }
        final List<ScenarioNpc> scenarioNpcList = dataGetter.getScenarioNpcDao().getByPlayerIdLocationId(playerId, nodeId);
        if (scenarioNpcList.size() > 200) {
            ErrorSceneLog.getInstance().appendErrorMsg(" too many ScenarioNpc").appendPlayerId(playerId).append("nodeId", nodeId).appendClassName("ScenarioBuilder").appendMethodName("initDefCamp").flush();
            final StringBuilder detail = new StringBuilder();
            for (final ScenarioNpc scenarioNpc : scenarioNpcList) {
                detail.append(scenarioNpc.getVId()).append(" ").append(scenarioNpc.getPlayerId()).append(" ").append(scenarioNpc.getScenarioId()).append(" ").append(scenarioNpc.getLocationId()).append(" ").append(scenarioNpc.getForceId()).append(" ").append(scenarioNpc.getNpcType()).append(" ").append(scenarioNpc.getArmyId()).append(" ").append(scenarioNpc.getHp()).append(" ").append(scenarioNpc.getTacticVal()).append(" ").append(scenarioNpc.getState()).append("\t").append(scenarioNpc.getAddTime()).append("\n");
            }
            ErrorSceneLog.getInstance().error(detail.toString());
        }
        for (final ScenarioNpc scenarioNpc2 : scenarioNpcList) {
            if (scenarioNpc2.getForceId() != juBenCityDto.forceId) {
                continue;
            }
            final CampArmy defCa = this.copyArmyFromScenarioNpc(dataGetter, battleAttacker.attPlayer, bat, scenarioNpc2, battleSide);
            defNum += defCa.getArmyHpOrg();
            bat.defCamp.add(defCa);
        }
        final BaseInfo defBaseInfo = bat.defBaseInfo;
        defBaseInfo.allNum += defNum;
        final BaseInfo defBaseInfo2 = bat.defBaseInfo;
        defBaseInfo2.num += defNum;
        bat.defBaseInfo.setForceId(juBenCityDto.forceId);
        return true;
    }
    
    public static CampArmy copyArmyFromCach(final IDataGetter dataGetter, final Battle battle, final int battleSide, final ScenarioNpc scenarioNpc) {
        final int npcId = scenarioNpc.getArmyId();
        final int hp = scenarioNpc.getHp();
        final CampArmy campArmy = new CampArmy();
        final Army armyCach = (Army)dataGetter.getArmyCache().get((Object)npcId);
        campArmy.setPlayerId(-1);
        campArmy.setPlayerName("NPC");
        campArmy.setForceId(scenarioNpc.getForceId());
        campArmy.setPlayerLv(armyCach.getGeneralLv());
        campArmy.setId(battle.campNum.getAndIncrement());
        campArmy.setPgmVId(0);
        campArmy.setArmyName(armyCach.getName());
        campArmy.setGeneralId(armyCach.getGeneralId());
        campArmy.setGeneralName(armyCach.getName());
        campArmy.setGeneralLv(armyCach.getGeneralLv());
        final General general = (General)dataGetter.getGeneralCache().get((Object)armyCach.getGeneralId());
        final SpecialGeneral sg = new SpecialGeneral(general.getGeneralSpecialInfo().generalType, general.getGeneralSpecialInfo().param, general.getGeneralSpecialInfo().param2);
        campArmy.setSpecialGeneral(sg);
        campArmy.setGeneralPic(general.getPic());
        campArmy.setQuality(general.getQuality());
        campArmy.setStrength(general.getStrength());
        campArmy.setLeader(general.getLeader());
        campArmy.setTacicId(general.getTacticId());
        if (general.getTacticId() > 0) {
            if (scenarioNpc.getTacticVal() > 0 && hp >= armyCach.getArmyHp()) {
                campArmy.setTacticVal(1);
            }
            if (campArmy.getSpecialGeneral().generalType == 7) {
                campArmy.setTacticVal((int)campArmy.getSpecialGeneral().param);
            }
        }
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
        int armyHp = hp;
        final int remainder = armyHp % 3;
        armyHp -= remainder;
        campArmy.setArmyHp(armyHp);
        campArmy.setArmyHpOrg(armyHp);
        campArmy.setColumn(armyCach.getArmyHp() / armyCach.getTroopHp());
        campArmy.setStrategies(troop.getStrategyDefMap().get(battle.terrainVal));
        return campArmy;
    }
    
    @Override
    public void sendBattleCityEndInfo(final IDataGetter dataGetter, final Battle bat) {
        final int nodeId = bat.defBaseInfo.id;
        try {
            dataGetter.getJuBenService().changeState(bat.attBaseInfo.id, nodeId, 0, false);
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().appendErrorMsg("quit lead to battle ended, reset node state catch exception").append("nodeId", nodeId).appendClassName("ScenarioBuilder").appendMethodName("sendBattleCityEndInfo").flush();
        }
    }
    
    @Override
    public Battle existBattle(final IDataGetter dataGetter, final Player player, final int defId) {
        final Battle battle = NewBattleManager.getInstance().getBattleByParm(this.battleType, player.getPlayerId(), defId);
        return battle;
    }
    
    @Override
    public Tuple<List<PlayerGeneralMilitary>, String> chooseGeneral(final IDataGetter dataGetter, final Player player, final int nodeId, final List<Integer> gIdList) {
        final Tuple<List<PlayerGeneralMilitary>, String> tuple = new Tuple();
        final List<PlayerGeneralMilitary> chooseList = new ArrayList<PlayerGeneralMilitary>();
        final Map<Integer, PlayerGeneralMilitary> pgmMap = dataGetter.getPlayerGeneralMilitaryDao().getMilitaryMap(player.getPlayerId());
        final Date nowDate = new Date();
        for (int i = 0; i < gIdList.size(); ++i) {
            final PlayerGeneralMilitary pgm = pgmMap.get(gIdList.get(i));
            final GeneralMoveDto gmd = CityService.getGeneralMoveDto(pgm.getPlayerId(), pgm.getGeneralId());
            if (gmd != null && CDUtil.isInCD(gmd.nextMoveTime, nowDate) && gmd.type == 2) {
                tuple.left = null;
                tuple.right = LocalMessages.BATTLE_IN_WORLD_MOVE;
            }
            else {
                if (gmd != null) {
                    if (gmd.cityState == 22) {
                        continue;
                    }
                    if (gmd.cityState == 23) {
                        continue;
                    }
                }
                if (pgm.getState() <= 1) {
                    final int maxHp = dataGetter.getBattleDataCache().getMaxHp(pgm);
                    if (pgm.getForces() * 1.0 / maxHp < 0.05) {
                        tuple.left = null;
                        tuple.right = LocalMessages.BATTLE_INT_BATTLE_BLOOD_NO_ENOUGH;
                    }
                    else {
                        final Set<Integer> neighborSet = dataGetter.getSoloRoadCache().getNeighbors(nodeId);
                        if (nodeId == pgm.getJubenLoId() || neighborSet.contains(pgm.getJubenLoId())) {
                            chooseList.add(pgm);
                        }
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
        return 19;
    }
    
    @Override
    public void initJoinPlayer(final List<PlayerGeneralMilitary> pgmList, final IDataGetter dataGetter, final Player player, final Battle bat, final BaseInfo baseInfo, final LinkedList<CampArmy> campMap, final LinkedList<CampArmy> campChange, final int battleSide, final TeamInfo teamInfo) {
        if (battleSide == 1) {
            bat.worldSceneLog.appendLogMsg("join to attCamp").newLine().Indent();
        }
        else {
            bat.worldSceneLog.appendLogMsg("join to defCamp").newLine().Indent();
        }
        bat.worldSceneLog.appendPlayerName(player.getPlayerName()).appendGeneralName(((General)dataGetter.getGeneralCache().get((Object)pgmList.get(0).getGeneralId())).getName()).appendPlayerId(player.getPlayerId()).append("pgm vId", pgmList.get(0).getVId()).newLine();
        CampArmy campArmy = null;
        int num = 0;
        for (int i = 0; i < pgmList.size(); ++i) {
            final PlayerGeneralMilitary pgm = pgmList.get(i);
            dataGetter.getPlayerGeneralMilitaryDao().moveJuben(pgm.getPlayerId(), pgm.getGeneralId(), pgm.getState(), bat.defBaseInfo.id);
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
    public void initAttCamp(final IDataGetter dataGetter, final BattleAttacker battleAttacker, final int nodeId, final Battle bat) {
        final int attType = battleAttacker.attType;
        final int attForceId = battleAttacker.attForceId;
        int attNum = 0;
        final int battleSide = 1;
        int playerId = 0;
        if (battleAttacker.attPlayerId > 0) {
            playerId = battleAttacker.attPlayerId;
        }
        else {
            playerId = battleAttacker.defPlayerId;
        }
        final JuBenDto juBenDto = JuBenManager.getInstance().getByPid(playerId);
        final int capitalId = juBenDto.capital;
        final JuBenCityDto juBenCityDto = JuBenManager.getInstance().getJuBenCityDto(playerId, nodeId);
        if (attType == 1 && battleAttacker.pgmList != null) {
            for (final PlayerGeneralMilitary pgm : battleAttacker.pgmList) {
                dataGetter.getPlayerGeneralMilitaryDao().moveJuben(pgm.getPlayerId(), pgm.getGeneralId(), pgm.getState(), nodeId);
                dataGetter.getGeneralService().sendGeneralMilitaryRecruitInfo(pgm.getPlayerId(), pgm.getGeneralId());
            }
        }
        final List<PlayerGeneralMilitary> pgmList = dataGetter.getPlayerGeneralMilitaryDao().getMilitaryList(playerId);
        for (final PlayerGeneralMilitary pgm2 : pgmList) {
            if (pgm2.getForceId() == juBenCityDto.forceId) {
                continue;
            }
            if (pgm2.getJubenLoId() != nodeId) {
                continue;
            }
            if (pgm2.getState() > 1) {
                dataGetter.getPlayerGeneralMilitaryDao().moveJuben(pgm2.getPlayerId(), pgm2.getGeneralId(), pgm2.getState(), capitalId);
                dataGetter.getGeneralService().sendGmStateSet(pgm2.getPlayerId(), pgm2.getGeneralId(), pgm2.getState());
            }
            else {
                final int maxHp = dataGetter.getBattleDataCache().getMaxHp(pgm2);
                if (pgm2.getForces() * 1.0 / maxHp < 0.05) {
                    dataGetter.getPlayerGeneralMilitaryDao().moveJuben(pgm2.getPlayerId(), pgm2.getGeneralId(), pgm2.getState(), capitalId);
                    dataGetter.getGeneralService().sendGmStateSet(pgm2.getPlayerId(), pgm2.getGeneralId(), pgm2.getState());
                }
                else {
                    final Player attPlayer = dataGetter.getPlayerDao().read(pgm2.getPlayerId());
                    final CampArmy campArmy = this.copyArmyFromPlayerTable(attPlayer, pgm2, dataGetter, this.getGeneralState(), bat, battleSide);
                    if (campArmy == null) {
                        ErrorSceneLog.getInstance().appendErrorMsg("node battle init defCamp get null campArmy").appendBattleId(bat.getBattleId()).appendPlayerName(attPlayer.getPlayerName()).appendPlayerId(attPlayer.getPlayerId()).appendGeneralId(pgm2.getPlayerId()).append("vId", pgm2.getVId()).flush();
                    }
                    else {
                        attNum += campArmy.getArmyHpOrg();
                        bat.attCamp.add(campArmy);
                        final int zdzsTech = dataGetter.getTechEffectCache().getTechEffect(pgm2.getPlayerId(), 43);
                        int autoStrategy = 0;
                        if (zdzsTech > 0) {
                            final PlayerBattleAttribute pba = dataGetter.getPlayerBattleAttributeDao().read(pgm2.getPlayerId());
                            autoStrategy = pba.getAutoStrategy();
                        }
                        else {
                            autoStrategy = -1;
                        }
                        bat.inBattlePlayers.put(pgm2.getPlayerId(), new PlayerInfo(pgm2.getPlayerId(), true, autoStrategy));
                    }
                }
            }
        }
        final List<ScenarioNpc> scenarioNpcList = dataGetter.getScenarioNpcDao().getByPlayerIdLocationId(playerId, nodeId);
        for (final ScenarioNpc scenarioNpc : scenarioNpcList) {
            if (scenarioNpc.getForceId() == juBenCityDto.forceId) {
                continue;
            }
            final CampArmy attCa = this.copyArmyFromScenarioNpc(dataGetter, battleAttacker.attPlayer, bat, scenarioNpc, battleSide);
            attNum += attCa.getArmyHpOrg();
            bat.attCamp.add(attCa);
        }
        final BaseInfo attBaseInfo = bat.attBaseInfo;
        attBaseInfo.num += attNum;
        final BaseInfo attBaseInfo2 = bat.attBaseInfo;
        attBaseInfo2.allNum += attNum;
        bat.attBaseInfo.setForceId(attForceId);
    }
    
    @Override
    public CampArmy copyArmyFromScenarioNpc(final IDataGetter dataGetter, Player player, final Battle battle, final ScenarioNpc scenarioNpc, final int battleSide) {
        switch (scenarioNpc.getNpcType()) {
            case 1: {
                final PlayerGeneralMilitary pgm = dataGetter.getPlayerGeneralMilitaryDao().read(scenarioNpc.getArmyId());
                if (pgm == null) {
                    return null;
                }
                if (player == null) {
                    player = dataGetter.getPlayerDao().read(pgm.getPlayerId());
                }
                final CampArmy ca = this.copyArmyFromPgmIgnoreState(dataGetter, player, pgm, scenarioNpc.getHp(), this.getGeneralState(), battle, battleSide);
                ca.generalName = String.valueOf(ca.generalName) + LocalMessages.PGM_PHANTOM_SUFFIX;
                ca.scenarioArmyType = 1;
                ca.isPhantom = true;
                ca.pgmVId = scenarioNpc.getVId();
                return ca;
            }
            case 2: {
                final CampArmy ca2 = copyArmyFromCach(dataGetter, battle, battleSide, scenarioNpc);
                ca2.scenarioArmyType = 2;
                ca2.pgmVId = scenarioNpc.getVId();
                return ca2;
            }
            default: {
                return null;
            }
        }
    }
    
    @Override
    public CampArmy copyArmyFromPlayerTable(final Player player, final PlayerGeneralMilitary pgm, final IDataGetter dataGetter, final int gState, final Battle bat, final int batSide) {
        final CampArmy campArmy = new CampArmy();
        final General general = (General)dataGetter.getGeneralCache().get((Object)pgm.getGeneralId());
        final SpecialGeneral sg = new SpecialGeneral(general.getGeneralSpecialInfo().generalType, general.getGeneralSpecialInfo().param, general.getGeneralSpecialInfo().param2);
        campArmy.setSpecialGeneral(sg);
        final Troop troop = dataGetter.getTroopCache().getTroop(general.getTroop(), player.getPlayerId());
        campArmy.updateDB = true;
        campArmy.setPlayerLv(player.getPlayerLv());
        campArmy.setPlayerId(player.getPlayerId());
        campArmy.setPlayerName(player.getPlayerName());
        campArmy.setForceId(player.getForceId());
        campArmy.setActivityAddExp(dataGetter.getActivityService().getAddBatValue(player.getPlayerId(), player.getPlayerLv()));
        getTerrainValue(bat.terrainVal, troop, batSide, campArmy);
        campArmy.setTroopId(troop.getId());
        campArmy.setTroopSerial(troop.getSerial());
        campArmy.setTroopType(troop.getType());
        campArmy.setTroopName(troop.getName());
        campArmy.setGeneralId(pgm.getGeneralId());
        campArmy.setGeneralLv(pgm.getLv());
        campArmy.setPgmVId(pgm.getVId());
        campArmy.setStrength(pgm.getStrength(general.getStrength()));
        campArmy.setLeader(pgm.getLeader(general.getLeader()));
        campArmy.setArmyName(general.getName());
        campArmy.setGeneralName(general.getName());
        campArmy.setGeneralPic(general.getPic());
        campArmy.setQuality(general.getQuality());
        campArmy.setTacicId(general.getTacticId());
        if (pgm.getState() == 1 || pgm.getAuto() == 1) {
            campArmy.setInRecruit(true);
        }
        try {
            final int res = dataGetter.getPlayerGeneralMilitaryDao().updateStateCheck(player.getPlayerId(), pgm.getGeneralId(), gState);
            if (res <= 0) {
                return null;
            }
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().appendErrorMsg("Builder copyArmyFromPlayerTable \u66f4\u6539\u6b66\u5c06\u72b6\u6001Exception").appendClassName("Builder").appendMethodName("copyArmyFromPlayerTable").append("PlayerId", player.getPlayerId()).append("GeneralId", pgm.getGeneralId()).flush();
            return null;
        }
        campArmy.setUpdateDB(true);
        campArmy.setId(bat.campNum.getAndIncrement());
        if (campArmy.getPlayerId() > 0 && !campArmy.isPhantom) {
            NewBattleManager.getInstance().joinBattle(bat, campArmy.getPlayerId(), campArmy.getGeneralId());
        }
        dataGetter.getGeneralService().sendGmStateSet(player.getPlayerId(), pgm.getGeneralId(), gState);
        int forces = pgm.getForces();
        final int remainder = forces % 3;
        forces -= remainder;
        campArmy.setArmyHp(forces);
        campArmy.setArmyHpOrg(forces);
        Builder.getAttDefHp(dataGetter, campArmy);
        final PlayerBattleAttribute pba = dataGetter.getPlayerBattleAttributeDao().read(player.getPlayerId());
        if (pba != null && pba.getSupportTime() != null && pba.getSupportTime().getTime() > System.currentTimeMillis()) {
            if (pba.getType() == 1) {
                campArmy.setRewardDoubleType(1);
                campArmy.setRewardDouble(1.5);
            }
            else if (pba.getType() == 2) {
                campArmy.setRewardDoubleType(2);
                campArmy.setRewardDouble(2.0);
            }
        }
        if (general.getTacticId() > 0 && pgm.getForces() >= campArmy.getMaxForces()) {
            campArmy.setTacticVal(1);
            if (campArmy.getSpecialGeneral().generalType == 7) {
                campArmy.setTacticVal((int)campArmy.getSpecialGeneral().param);
            }
        }
        if (batSide == 1) {
            campArmy.setStrategies(troop.getStrategyMap().get(bat.terrainVal));
        }
        else {
            campArmy.setStrategies(troop.getStrategyDefMap().get(bat.terrainVal));
        }
        return campArmy;
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
        final JuBenCityDto juBenCityDto = JuBenManager.getInstance().getJuBenCityDto(player.getPlayerId(), defId);
        if (juBenCityDto.forceId == player.getForceId()) {
            return 0;
        }
        return 1;
    }
    
    @Override
    public int getBattleSide(final IDataGetter dataGetter, final Player player, final Battle battle) {
        if (battle.defBaseInfo.forceId == player.getForceId()) {
            return 0;
        }
        return 1;
    }
    
    @Override
    public String getAttTargetName(final IDataGetter dataGetter, final Battle bat) {
        final int nodeId = bat.defBaseInfo.id;
        return ((SoloCity)dataGetter.getSoloCityCache().get((Object)nodeId)).getName();
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
    
    public void deal5StarBroadCastMsg(final boolean attWin, final IDataGetter dataGetter, final Battle bat, final BattleResult battleResult) {
    }
    
    @Override
    public void dealNextNpc(final boolean attWin, final IDataGetter dataGetter, final Battle bat, final BattleResult battleResult) {
        final int nodeId = bat.defBaseInfo.id;
        if (attWin) {
            try {
                this.deal5StarBroadCastMsg(attWin, dataGetter, bat, battleResult);
            }
            catch (Exception e) {
                ErrorSceneLog.getInstance().appendErrorMsg("World war dealBroadCastMsg Exception").appendClassName("ScenarioBuilder").appendMethodName("deal5StarBroadCastMsg").append("battleId", bat.getBattleId()).append("attWin", attWin).flush();
                ErrorSceneLog.getInstance().error("ScenarioBuilder dealNextNpc", e);
            }
            battleResult.cityName = ((SoloCity)dataGetter.getSoloCityCache().get((Object)nodeId)).getName();
            battleResult.cType = 2;
            final int winForceId = bat.attList.get(0).getCampArmy().getForceId();
            dataGetter.getJuBenService().checkRoyalJadeRobbed(bat);
            try {
                dataGetter.getJuBenService().changeForceIdAndState(nodeId, winForceId, 0, bat.getAttBaseInfo().getId(), bat.attList.get(0).getCampArmy().getPlayerName());
            }
            catch (Exception e2) {
                ErrorSceneLog.getInstance().error("battle ended, att Win, modify node force and state exception.", e2);
            }
        }
        else {
            try {
                dataGetter.getJuBenService().changeState(bat.attBaseInfo.id, nodeId, 0, false);
            }
            catch (Exception e) {
                ErrorSceneLog.getInstance().error("battle ended, reset node state only exception.", e);
            }
        }
    }
    
    @Override
    public void afterBat(final boolean attWin, final IDataGetter dataGetter, final Battle bat) {
        if (attWin) {
            final int cityId = bat.getDefBaseInfo().getId();
            if (cityId == 113 || cityId == 10306) {
                return;
            }
            final CampArmy winCampArmy = bat.getAttList().get(0).getCampArmy();
            final int winForceId = winCampArmy.getForceId();
            int winPlayerId = winCampArmy.getPlayerId();
            if (winPlayerId < 1) {
                for (final CampArmy ca : bat.attCamp) {
                    if (ca.getForceId() == winForceId && ca.getPlayerId() > 0) {
                        winPlayerId = ca.getPlayerId();
                        break;
                    }
                }
            }
            for (final CampArmy ca : bat.attCamp) {
                final int attPlayerId = ca.getPlayerId();
                if (winForceId != ca.getForceId()) {
                    if (attPlayerId > 0) {
                        final String battleId = NewBattleManager.getBattleId(18, attPlayerId, cityId);
                        final Battle juBenCityBattle = NewBattleManager.getInstance().createBattle(battleId);
                        final Terrain terrain = this.getTerrain(-1, cityId, dataGetter);
                        final BattleAttacker battleAttacker = new BattleAttacker();
                        battleAttacker.attType = 1;
                        battleAttacker.attForceId = ca.getForceId();
                        battleAttacker.attPlayerId = ca.getPlayerId();
                        final Player player = dataGetter.getPlayerDao().read(attPlayerId);
                        battleAttacker.attPlayer = player;
                        juBenCityBattle.init(battleAttacker, 18, cityId, dataGetter, false, terrain.getValue());
                        break;
                    }
                    if (winPlayerId < 1) {
                        return;
                    }
                    final String battleId = NewBattleManager.getBattleId(18, winPlayerId, cityId);
                    final Battle juBenCityBattle = NewBattleManager.getInstance().createBattle(battleId);
                    final Terrain terrain = this.getTerrain(-1, cityId, dataGetter);
                    final BattleAttacker battleAttacker = new BattleAttacker();
                    battleAttacker.attType = 6;
                    battleAttacker.attForceId = ca.getForceId();
                    battleAttacker.attPlayerId = ca.getPlayerId();
                    battleAttacker.defPlayerId = winPlayerId;
                    battleAttacker.attPlayer = null;
                    juBenCityBattle.init(battleAttacker, 18, cityId, dataGetter, false, terrain.getValue());
                    break;
                }
            }
        }
    }
    
    @Override
    public void roundCaculateReward(final IDataGetter dataGetter, final Battle bat, final RoundInfo roundInfo) {
        FightRewardCoe frc = (FightRewardCoe)dataGetter.getFightRewardCoeCache().get((Object)this.getFightRewardCoeId(dataGetter, bat));
        if (frc == null) {
            frc = new FightRewardCoe();
        }
        this.roundCaculateAttReward(dataGetter, frc, bat, roundInfo);
    }
    
    public void roundReduceTruePlayer(final IDataGetter dataGetter, final Battle bat, final CampArmy campArmyA, final CampArmy campArmyB, final boolean isBeKill, final int lostA, final int lostB) {
        if (isBeKill) {
            final JuBenDto juBenDto = JuBenManager.getInstance().getByPid(bat.attBaseInfo.id);
            final int capitalId = juBenDto.capital;
            int done = dataGetter.getPlayerGeneralMilitaryDao().moveJuben(campArmyA.playerId, campArmyA.generalId, 1, juBenDto.capital);
            if (done != 1) {
                ErrorSceneLog.getInstance().appendErrorMsg("moveJuben fail").appendBattleId(bat.getBattleId()).appendPlayerId(campArmyA.getPlayerId()).appendGeneralId(campArmyA.generalId).append("juBenDto.capital", juBenDto.capital).appendMethodName("roundReduceTruePlayer").appendClassName("ScenarioBuilder").flush();
            }
            ScenarioEventMessageHelper.sendMoveToCitykMessage(campArmyA.playerId, campArmyA.generalId, capitalId);
            done = dataGetter.getPlayerGeneralMilitaryDao().upJuBenLocationForceSetState1(campArmyA.playerId, campArmyA.generalId, capitalId, lostA, new Date());
            if (done != 1) {
                ErrorSceneLog.getInstance().appendErrorMsg("updateLocationForceSetState1 fail").appendBattleId(bat.getBattleId()).appendPlayerId(campArmyA.getPlayerId()).appendGeneralId(campArmyA.generalId).append("capitalId", capitalId).append("lostA", lostA).appendMethodName("roundReduceTruePlayer").appendClassName("ScenarioBuilder").flush();
            }
            dataGetter.getJuBenService().sendAttMoveInfo(campArmyA.getPlayerId(), campArmyA.generalId, bat.defBaseInfo.id, capitalId, campArmyA.getForceId(), "", campArmyA.getArmyHp(), true);
            try {
                dataGetter.getGeneralService().sendGmForcesReduce(campArmyA.getPlayerId(), campArmyA.generalId, lostA, lostB, bat.inSceneSet.contains(campArmyA.getPlayerId()), campArmyB.playerName, campArmyB.generalName, campArmyB.quality);
                dataGetter.getGeneralService().sendGmUpdate(campArmyA.getPlayerId(), campArmyA.generalId, bat.inSceneSet.contains(campArmyA.getPlayerId()));
            }
            catch (Exception e) {
                ErrorSceneLog.getInstance().error("ScenarioBuilder roundReduceTruePlayer ", e);
            }
        }
        else {
            try {
                final int done2 = dataGetter.getPlayerGeneralMilitaryDao().consumeForces(campArmyA.getPlayerId(), campArmyA.generalId, lostA, new Date());
                if (done2 != 1) {
                    ErrorSceneLog.getInstance().appendErrorMsg("consumeForces fail").appendBattleId(bat.getBattleId()).appendPlayerId(campArmyA.getPlayerId()).appendGeneralId(campArmyA.generalId).append("lostA", lostA).appendMethodName("roundReduceTruePlayer").flush();
                }
                dataGetter.getGeneralService().sendGmForcesReduce(campArmyA.getPlayerId(), campArmyA.generalId, lostA, lostB, bat.inSceneSet.contains(campArmyA.getPlayerId()), campArmyB.playerName, campArmyB.generalName, campArmyB.quality);
            }
            catch (Exception e2) {
                ErrorSceneLog.getInstance().error("ScenarioBuilder roundReduceTruePlayer 2", e2);
            }
        }
    }
    
    private void roundReduceScenarioNpc(final IDataGetter dataGetter, final Battle bat, final CampArmy campArmyA, final CampArmy campArmyB, final boolean isBeKill, final int lostA, final int lostB) {
        if (isBeKill) {
            final int done = dataGetter.getScenarioNpcDao().deleteById(campArmyA.pgmVId);
            if (done != 1) {
                ErrorSceneLog.getInstance().appendErrorMsg("ScenarioNpc delete failed.").appendBattleId(bat.getBattleId()).append("ScenarioNpc vId", campArmyA.pgmVId).appendClassName("ScenarioBuilder").appendMethodName("roundReduceScenarioNpc");
            }
        }
    }
    
    @Override
    public void roundReduceTroopSingle(final IDataGetter dataGetter, final Battle bat, final CampArmy campArmyA, final CampArmy campArmyB, final boolean isBeKill, final int lostA, final int lostB) {
        if (lostA < 0) {
            BattleSceneLog.getInstance().debug("AAAAAAAAAAAAAAAAAAAAA node lostA:" + lostA + " " + bat.getBattleId());
        }
        if (campArmyA.scenarioArmyType > 0) {
            this.roundReduceScenarioNpc(dataGetter, bat, campArmyA, campArmyB, isBeKill, lostA, lostB);
        }
        else if (campArmyA.updateDB && campArmyA.playerId > 0) {
            this.roundReduceTruePlayer(dataGetter, bat, campArmyA, campArmyB, isBeKill, lostA, lostB);
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
            final int exeTacticCaLost = 0;
            if (campArmy.scenarioArmyType > 0) {
                this.roundReduceScenarioNpc(dataGetter, bat, campArmy, exeTacticCa, dead, reduce, exeTacticCaLost);
            }
            else {
                if (!campArmy.updateDB || campArmy.playerId <= 0) {
                    continue;
                }
                this.roundReduceTruePlayer(dataGetter, bat, campArmy, exeTacticCa, dead, reduce, exeTacticCaLost);
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
        if (attWin) {
            for (final CampArmy ca : bat.attCamp) {
                if (ca.scenarioArmyType == 0 && ca.getPlayerId() > 0) {
                    NewBattleManager.getInstance().quitBattle(bat, ca.getPlayerId(), ca.getGeneralId());
                }
                this.updateGeneralDB(dataGetter, bat, ca, attWin);
            }
            for (final CampArmy ca : bat.defCamp) {
                if (ca.armyHp == 0 && ca.scenarioArmyType == 0 && ca.getPlayerId() > 0 && ca.updateDB) {
                    final int capitalId = dataGetter.getSoloCityCache().getCapitalIdByCityId(bat.defBaseInfo.id);
                    dataGetter.getPlayerGeneralMilitaryDao().moveJuben(ca.getPlayerId(), ca.getGeneralId(), 1, capitalId);
                    ScenarioEventMessageHelper.sendMoveToCitykMessage(ca.getPlayerId(), ca.getGeneralId(), bat.defBaseInfo.id);
                }
            }
        }
        else {
            for (final CampArmy ca : bat.defCamp) {
                if (ca.scenarioArmyType == 0 && ca.getPlayerId() > 0) {
                    NewBattleManager.getInstance().quitBattle(bat, ca.getPlayerId(), ca.getGeneralId());
                }
                this.updateGeneralDB(dataGetter, bat, ca, attWin);
            }
            for (final CampArmy ca : bat.attCamp) {
                if (ca.armyHp == 0 && ca.scenarioArmyType == 0 && ca.getPlayerId() > 0 && ca.updateDB) {
                    final int capitalId = dataGetter.getSoloCityCache().getCapitalIdByCityId(bat.defBaseInfo.id);
                    dataGetter.getPlayerGeneralMilitaryDao().moveJuben(ca.getPlayerId(), ca.getGeneralId(), 1, capitalId);
                    ScenarioEventMessageHelper.sendMoveToCitykMessage(ca.getPlayerId(), ca.getGeneralId(), bat.defBaseInfo.id);
                }
            }
        }
    }
    
    @Override
    public void updateGeneralDB(final IDataGetter dataGetter, final Battle bat, final CampArmy ca, final boolean attWin) {
        if (ca.playerId <= 0) {
            return;
        }
        if (ca.isPhantom) {
            return;
        }
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
        if (ca.killGeneral > pi.maxKillG) {
            pi.maxKillG = ca.killGeneral;
        }
        final int state = 1;
        final int done = dataGetter.getPlayerGeneralMilitaryDao().updateStateByPidAndGid(ca.getPlayerId(), ca.getGeneralId(), state, new Date());
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
    public void dealTroopDrop(final IDataGetter dataGetter, final Battle bat, final RoundInfo roundInfo) {
        final int attPlayerId = roundInfo.attCampArmy.playerId;
        if (attPlayerId > 0) {
            for (final BattleArmy ba : roundInfo.defKilledList) {
                final int troopId = ba.getCampArmy().getTroopId();
                final Troop bonusTroop = (Troop)dataGetter.getTroopCache().get((Object)troopId);
                if (bonusTroop != null && bonusTroop.getTroopDrop() != null) {
                    dataGetter.getBattleDropService().saveBattleDrop(attPlayerId, bonusTroop.getTroopDrop());
                    final Map<Integer, BattleDrop> dropMap = bonusTroop.getTroopDrop().getDropAndMap();
                    dropMap.size();
                    roundInfo.attRoundReward.addDropMap(dropMap);
                }
            }
        }
    }
    
    @Override
    public void setSurroundState(final IDataGetter dataGetter, final Battle bat) {
        final int playerId = bat.attBaseInfo.id;
        final int nodeId = bat.defBaseInfo.id;
        final JuBenCityDto juBenCityDto = JuBenManager.getInstance().getJuBenCityDto(playerId, nodeId);
        if (juBenCityDto != null) {
            bat.setSurround(juBenCityDto.title);
        }
        else {
            ErrorSceneLog.getInstance().appendErrorMsg("juBenCityDto is null").appendBattleId(bat.getBattleId()).appendPlayerId(playerId).append("nodeId", nodeId).appendClassName("ScenarioBuilder").appendMethodName("setSurroundState").flush();
            bat.setSurround(0);
        }
    }
    
    @Override
    public void checkErrorAndHandle(final IDataGetter dataGetter, final Battle battle) {
        if (battle.attList.size() == 0 || battle.defList.size() == 0) {
            return;
        }
        for (final CampArmy campArmy : battle.attCamp) {
            if (campArmy.playerId > 0 && campArmy.updateDB && !campArmy.isPhantom) {
                final PlayerGeneralMilitary pgm = dataGetter.getPlayerGeneralMilitaryDao().getMilitary(campArmy.playerId, campArmy.generalId);
                if (pgm.getState() == this.getGeneralState()) {
                    continue;
                }
                dataGetter.getPlayerGeneralMilitaryDao().updateState(pgm.getVId(), this.getGeneralState());
                dataGetter.getGeneralService().sendGmStateSet(pgm.getPlayerId(), pgm.getGeneralId(), this.getGeneralState());
            }
        }
        for (final CampArmy campArmy : battle.defCamp) {
            if (campArmy.playerId > 0 && campArmy.updateDB && !campArmy.isPhantom) {
                final PlayerGeneralMilitary pgm = dataGetter.getPlayerGeneralMilitaryDao().getMilitary(campArmy.playerId, campArmy.generalId);
                if (pgm.getState() == this.getGeneralState()) {
                    continue;
                }
                dataGetter.getPlayerGeneralMilitaryDao().updateState(pgm.getVId(), this.getGeneralState());
                dataGetter.getGeneralService().sendGmStateSet(pgm.getPlayerId(), pgm.getGeneralId(), this.getGeneralState());
            }
        }
    }
}
