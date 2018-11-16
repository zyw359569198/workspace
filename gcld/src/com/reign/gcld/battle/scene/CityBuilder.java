package com.reign.gcld.battle.scene;

import com.reign.gcld.common.log.*;
import com.reign.gcld.juben.service.*;
import com.reign.gcld.juben.common.*;
import com.reign.framework.json.*;
import com.reign.gcld.team.service.*;
import com.reign.gcld.phantom.common.*;
import com.reign.gcld.common.util.*;
import com.reign.gcld.team.common.*;
import com.reign.gcld.player.domain.*;
import com.reign.gcld.player.dto.*;
import com.reign.gcld.general.domain.*;
import com.reign.util.*;
import com.reign.gcld.activity.domain.*;
import com.reign.gcld.general.dto.*;
import com.reign.gcld.battle.domain.*;
import com.reign.gcld.rank.service.*;
import java.text.*;
import com.reign.gcld.world.domain.*;
import com.reign.gcld.common.*;
import com.reign.gcld.rank.*;
import com.reign.gcld.huizhan.domain.*;
import com.reign.gcld.battle.service.*;
import java.util.*;
import com.reign.gcld.task.message.*;
import com.reign.gcld.world.common.*;
import com.reign.gcld.sdata.common.*;
import com.reign.gcld.world.service.*;
import com.reign.gcld.player.common.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.gcld.rank.domain.*;
import com.reign.gcld.battle.common.*;
import com.reign.gcld.battle.reward.*;
import java.io.*;

public class CityBuilder extends Builder
{
    private static final Logger errorLog;
    
    static {
        errorLog = CommonLog.getLog(CityBuilder.class);
    }
    
    public CityBuilder(final int battleType) {
        this.battleType = battleType;
    }
    
    @Override
    public Tuple<Boolean, String> attPermitCreate(final IDataGetter dataGetter, final int playerId, final int defId) {
        final Tuple<Boolean, String> tuple = new Tuple();
        tuple.left = false;
        final Player player = dataGetter.getPlayerDao().read(playerId);
        final City city = dataGetter.getCityDao().read(defId);
        city.getForceId();
        player.getForceId();
        final JuBenDto juBenDto = JuBenManager.getInstance().getByPid(playerId);
        if (juBenDto != null) {
            tuple.right = LocalMessages.IN_JUBEN_CANNT_BATTLE;
            return tuple;
        }
        final PlayerAttribute pa = dataGetter.getPlayerAttributeDao().read(player.getPlayerId());
        final char[] cs = pa.getFunctionId().toCharArray();
        if (cs[24] == '0') {
            tuple.right = LocalMessages.T_COMM_10020;
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
        final Battle battle = NewBattleManager.getInstance().getBattleByDefId(3, defId);
        if (battle == null) {
            dataGetter.getCityService().changeState(defId, 0, false);
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
        int terrainPic = 1;
        final boolean isNTYellowTurbans = dataGetter.getBattleService().isNTYellowTurbansXiangYangDoing(defId);
        if (isNTYellowTurbans) {
            terrainPic = 11;
        }
        else {
            terrainPic = display;
        }
        return new Terrain(display, (worldCity == null) ? 1 : worldCity.getTerrainEffectType(), terrainPic);
    }
    
    @Override
    public byte[] getOtherBatInfo(final IDataGetter dataGetter, final int defId, final int playerId, final int battleSide, PlayerBattleAttribute pba) {
        final JsonDocument doc = new JsonDocument();
        doc.createElement("targetName", ((WorldCity)dataGetter.getWorldCityCache().get((Object)defId)).getName());
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
        return doc.toByte();
    }
    
    @Override
    public int getBatInfo(final IDataGetter dataGetter, final int defId) {
        return this.battleType * 100000 + 1;
    }
    
    @Override
    public byte[] getPrepareInfo(final IDataGetter dataGetter, final PlayerDto playerDto, final int defId, final Battle battle, final int terrain) {
        final JsonDocument doc = new JsonDocument();
        doc.createElement("bat", this.battleType * 100000 + 1);
        return doc.toByte();
    }
    
    @Override
    public byte[] getBattleTopInfo(final IDataGetter dataGetter, final PlayerDto playerDto, final Battle battle) {
        final JsonDocument doc = new JsonDocument();
        final City city = dataGetter.getCityDao().read(battle.getDefBaseInfo().getId());
        doc.createElement("bat", this.battleType * 100000 + 1);
        if (city.getForceId() == playerDto.forceId) {
            doc.appendJson(this.getAttTopLeft(dataGetter, battle));
            doc.appendJson(this.getDefTopRight(dataGetter, playerDto.forceId, battle));
        }
        else {
            doc.appendJson(this.getAttTopLeft(dataGetter, playerDto.playerId, battle, playerDto.forceId));
            doc.appendJson(this.getDefTopRight(dataGetter, playerDto.playerId, battle, city));
        }
        return doc.toByte();
    }
    
    private byte[] getAttTopLeft(final IDataGetter dataGetter, final Battle battle) {
        final JsonDocument doc = new JsonDocument();
        doc.createElement("playerId", battle.getAttBaseInfo().getForceId());
        doc.createElement("playerName", (Object)(String.valueOf(WorldCityCommon.nationIdNameMap.get(battle.getAttBaseInfo().getForceId())) + LocalMessages.T_FORCE_NATION));
        doc.createElement("playerPic", battle.getAttBaseInfo().getForceId());
        doc.createElement("playerLv", 0);
        doc.createElement("playerForces", battle.getAttBaseInfo().getNum());
        doc.createElement("playerMaxForces", battle.getAttBaseInfo().getAllNum());
        return doc.toByte();
    }
    
    private byte[] getDefTopRight(final IDataGetter dataGetter, final int forceId, final Battle battle) {
        final JsonDocument doc = new JsonDocument();
        doc.createElement("npcId", forceId);
        doc.createElement("npcName", (Object)(String.valueOf(WorldCityCommon.nationIdNameMap.get(forceId)) + LocalMessages.T_FORCE_NATION));
        doc.createElement("npcPic", forceId);
        doc.createElement("npcLv", 0);
        if (battle != null) {
            doc.createElement("npcForces", battle.getDefBaseInfo().getNum());
            doc.createElement("npcMaxForces", battle.getDefBaseInfo().getAllNum());
        }
        return doc.toByte();
    }
    
    private byte[] getAttTopLeft(final IDataGetter dataGetter, final int playerId, final Battle battle, final int forceId) {
        final JsonDocument doc = new JsonDocument();
        doc.createElement("playerId", forceId);
        doc.createElement("playerName", (Object)(String.valueOf(WorldCityCommon.nationIdNameMap.get(forceId)) + LocalMessages.T_FORCE_NATION));
        doc.createElement("playerPic", forceId);
        doc.createElement("playerLv", 0);
        if (battle != null) {
            doc.createElement("playerForces", battle.getAttBaseInfo().getNum());
            doc.createElement("playerMaxForces", battle.getAttBaseInfo().getAllNum());
        }
        return doc.toByte();
    }
    
    private byte[] getDefTopRight(final IDataGetter dataGetter, final int playerId, final Battle battle, final City city) {
        final JsonDocument doc = new JsonDocument();
        if (city.getForceId() == 0) {
            final Army army = (Army)dataGetter.getArmyCache().get((Object)((WorldCity)dataGetter.getWorldCityCache().get((Object)city.getId())).getChief());
            doc.createElement("npcId", army.getGeneralId());
            doc.createElement("npcName", army.getName());
            doc.createElement("npcPic", ((General)dataGetter.getGeneralCache().get((Object)army.getGeneralId())).getPic());
            doc.createElement("npcFlag", "NPC");
            doc.createElement("npcLv", army.getGeneralLv());
        }
        else {
            doc.createElement("npcId", city.getForceId());
            doc.createElement("npcName", (Object)(String.valueOf(WorldCityCommon.nationIdNameMap.get(city.getForceId())) + LocalMessages.T_FORCE_NATION));
            doc.createElement("npcPic", city.getForceId());
            doc.createElement("npcLv", 0);
        }
        if (battle != null) {
            doc.createElement("npcForces", battle.getDefBaseInfo().getNum());
            doc.createElement("npcMaxForces", battle.getDefBaseInfo().getAllNum());
        }
        return doc.toByte();
    }
    
    @Override
    public String getBattleId(final IDataGetter dataGetter, final Player player, final int defId) {
        return NewBattleManager.getBattleId(this.battleType, player.getForceId(), defId);
    }
    
    public static String getBattleId(final IDataGetter dataGetter, final int forceId, final int defId) {
        return NewBattleManager.getBattleId(3, forceId, defId);
    }
    
    @Override
    public Tuple<Boolean, String> canCreateBattle(final Player player, final int defId, final IDataGetter dataGetter) {
        final Tuple<Boolean, String> tuple = new Tuple();
        tuple.left = false;
        if (dataGetter.getWorldCityCache().get((Object)defId) == null) {
            tuple.right = String.valueOf(LocalMessages.BATTLE_NO_SUCH_CITY) + " defId==" + defId;
            return tuple;
        }
        if (CityService.getCityFlag(defId) != 0) {
            tuple.right = String.valueOf(LocalMessages.BATTLE_IN_NATION_TASK_YELLOW_TURBANS) + " defId==" + defId;
            return tuple;
        }
        final PlayerAttribute pa = dataGetter.getPlayerAttributeDao().read(player.getPlayerId());
        final char[] cs = pa.getFunctionId().toCharArray();
        if (cs[24] == '0') {
            tuple.right = LocalMessages.T_COMM_10020;
            return tuple;
        }
        if (WorldCityCommon.mainCityNationIdMap.containsKey(defId)) {
            tuple.right = LocalMessages.BATTLE_CANNOT_BAT_MAIN_CITY;
            return tuple;
        }
        final boolean hzFlag = dataGetter.getHuiZhanService().isHuiZhanInStatePreparation(defId);
        if (!hzFlag) {
            final City city = dataGetter.getCityDao().read(defId);
            if (player.getForceId() == city.getForceId()) {
                tuple.right = LocalMessages.BATTLE_INFO_WORLD_CITY_SELF;
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
        final PlayerAttribute pa = dataGetter.getPlayerAttributeDao().read(player.getPlayerId());
        final char[] cs = pa.getFunctionId().toCharArray();
        if (cs[24] == '0') {
            tuple.right = LocalMessages.T_COMM_10020;
            return tuple;
        }
        if (CityService.getCityFlag(defId) != 0) {
            tuple.right = String.valueOf(LocalMessages.BATTLE_IN_NATION_TASK_YELLOW_TURBANS) + " defId==" + defId;
            return tuple;
        }
        if (WorldCityCommon.mainCityNationIdMap.containsKey(defId)) {
            tuple.right = LocalMessages.BATTLE_CANNOT_BAT_MAIN_CITY;
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
    public void inBattleInfo(final int playerId, final boolean inBattle) {
    }
    
    @Override
    public void addInSceneSet(final Battle bat, final int playerId) {
    }
    
    @Override
    public void sendBattleInfo(final IDataGetter dataGetter, final Battle bat, final BattleAttacker battleAttacker) {
        final int cityId = bat.defBaseInfo.id;
        try {
            bat.worldSceneLog.appendLogMsg("battle created, set state as in war").appendCityName(((WorldCity)dataGetter.getWorldCityCache().get((Object)cityId)).getName()).append("state", 1).appendCityId(cityId).appendClassName("CityBuilder").appendMethodName("sendBattleInfo").newLine();
            dataGetter.getCityService().changeState(bat.defBaseInfo.getId(), 1, false);
        }
        catch (Exception e) {
            final City city = dataGetter.getCityDao().read(cityId);
            bat.worldSceneLog.appendLogMsg("CityBuilder sendBattleInfo set city state catch Exception.").appendCityId(city.getId()).appendCityName(((WorldCity)dataGetter.getWorldCityCache().get((Object)city.getId())).getName()).append("state after create battle", city.getState()).newLine();
            ErrorSceneLog.getInstance().error("CityBuilder sendBattleInfo set city state catch Exception.", e);
        }
    }
    
    public void initDefCampOfNpcBattle(final IDataGetter dataGetter, final City city, final Battle bat, final BattleAttacker battleAttacker, final int defForceId) {
        final int cityId = city.getId();
        final WorldCity worldCity = (WorldCity)dataGetter.getWorldCityCache().get((Object)cityId);
        final int playerId = -1;
        final String playerName = "NPC";
        int defNum = 0;
        bat.worldSceneLog.appendCityName(worldCity.getName()).append("attacker", playerName).append("city forceId", city.getForceId()).appendCityId(city.getId()).appendPlayerId(playerId).newLine();
        final CityNpcLost cityNpcLost = dataGetter.getCityNpcLostDao().read(cityId);
        if (cityNpcLost == null) {
            ErrorSceneLog.getInstance().appendErrorMsg("Exception. cityNpcLost is null").appendClassName("CityBuilder").appendMethodName("initDefCamp").append("playerId", playerId).append("cityId", cityId).flush();
        }
        String[] npcLosts = null;
        final Integer[] defNpcs = worldCity.getArmiesId();
        if (cityNpcLost.getNpcLost() == null || cityNpcLost.getNpcLost().equals("")) {
            npcLosts = new String[defNpcs.length];
            for (int i = 0; i < npcLosts.length; ++i) {
                npcLosts[i] = "0";
            }
        }
        else {
            npcLosts = cityNpcLost.getNpcLost().split(";");
            if (defNpcs.length != npcLosts.length) {
                ErrorSceneLog.getInstance().appendErrorMsg("Exception. Table cityNpcLost npclost error").appendClassName("CityBuilder").appendMethodName("initDefCamp").append("CityId", cityNpcLost.getCityId()).flush();
            }
        }
        int id = 0;
        int npcLost = 0;
        int npcId = 0;
        CampArmy campArmy = null;
        final Army armyCach = (Army)dataGetter.getArmyCache().get((Object)worldCity.getChief());
        for (int j = 0; j < defNpcs.length; ++j) {
            id = bat.campNum.getAndIncrement();
            npcId = defNpcs[j];
            if (j >= npcLosts.length) {
                npcLost = 0;
            }
            else {
                npcLost = Integer.parseInt(npcLosts[j]);
            }
            campArmy = copyArmyFromCach(npcId, npcLost, dataGetter, id, bat.terrainVal, armyCach.getGeneralLv());
            defNum += campArmy.getArmyHpOrg();
            bat.defCamp.add(campArmy);
            BattleSceneLog.getInstance().info("#batId:" + bat.getBattleId() + "_" + bat.getStartTime() + "#add:init#side:def" + "#playerId:" + campArmy.getPlayerId() + ":" + campArmy.isPhantom + "#general:" + campArmy.getGeneralId() + "#defSize:" + bat.defCamp.size());
        }
        bat.worldSceneLog.newLine();
        final BaseInfo defBaseInfo = bat.defBaseInfo;
        defBaseInfo.allNum += defNum;
        final BaseInfo defBaseInfo2 = bat.defBaseInfo;
        defBaseInfo2.num += defNum;
        bat.defBaseInfo.setForceId(defForceId);
    }
    
    public void initDefCampOfTruePlayerBattle(final IDataGetter dataGetter, final City city, final Battle bat, final BattleAttacker battleAttacker, final int defForceId) {
        final int cityId = city.getId();
        final WorldCity worldCity = (WorldCity)dataGetter.getWorldCityCache().get((Object)cityId);
        final int attType = battleAttacker.attType;
        int defNum = 0;
        int hzDefNpcNum = 0;
        if (attType == 1) {
            for (final PlayerGeneralMilitary pgm : battleAttacker.pgmList) {
                dataGetter.getPlayerGeneralMilitaryDao().updateLocationId(pgm.getPlayerId(), pgm.getGeneralId(), cityId);
            }
        }
        final List<PlayerGeneralMilitary> pgmList = dataGetter.getPlayerGeneralMilitaryDao().getMilitaryByLocationIdOrderByPlayerIdLvDesc(city.getId());
        if (pgmList.size() > 0) {
            bat.worldSceneLog.appendLogMsg("def pgmList").appendMethodName("initDefCamp").newLine();
        }
        final List<PlayerGeneralMilitary> defPgmList = new LinkedList<PlayerGeneralMilitary>();
        for (final PlayerGeneralMilitary pgm2 : pgmList) {
            if (pgm2.getForceId() == defForceId) {
                defPgmList.add(pgm2);
            }
        }
        for (final PlayerGeneralMilitary pgm2 : defPgmList) {
            if (pgm2.getState() > 1) {
                final int capitalId = WorldCityCommon.nationMainCityIdMap.get(pgm2.getForceId());
                dataGetter.getCityService().sendAttMoveInfo(pgm2.getPlayerId(), pgm2.getGeneralId(), pgm2.getLocationId(), capitalId, pgm2.getForceId(), "", pgm2.getForces(), true);
                dataGetter.getPlayerGeneralMilitaryDao().updateLocationId(pgm2.getPlayerId(), pgm2.getGeneralId(), capitalId);
            }
            else {
                final int maxHp = dataGetter.getBattleDataCache().getMaxHp(pgm2);
                if (pgm2.getForces() * 1.0 / maxHp < 0.05) {
                    final int capitalId2 = WorldCityCommon.nationMainCityIdMap.get(pgm2.getForceId());
                    dataGetter.getCityService().sendAttMoveInfo(pgm2.getPlayerId(), pgm2.getGeneralId(), pgm2.getLocationId(), capitalId2, pgm2.getForceId(), "", pgm2.getForces(), true);
                    dataGetter.getPlayerGeneralMilitaryDao().updateLocationId(pgm2.getPlayerId(), pgm2.getGeneralId(), capitalId2);
                }
                else {
                    final Player Ownner = dataGetter.getPlayerDao().read(pgm2.getPlayerId());
                    final GeneralMoveDto gmd = CityService.getGeneralMoveDto(pgm2.getPlayerId(), pgm2.getGeneralId());
                    if (gmd != null && CDUtil.isInCD(gmd.nextMoveTime, new Date())) {
                        gmd.moveLine = "";
                        gmd.nextMoveTime = 0L;
                        gmd.cityState = 0;
                    }
                    final CampArmy campArmy = this.copyArmyFromPlayerTable(Ownner, pgm2, dataGetter, this.getGeneralState(), bat, 0);
                    if (campArmy == null) {
                        ErrorSceneLog.getInstance().appendErrorMsg("city battle init defCamp get null campArmy").appendBattleId(bat.getBattleId()).appendPlayerName(Ownner.getPlayerName()).appendPlayerId(Ownner.getPlayerId()).appendGeneralId(pgm2.getPlayerId()).append("vId", pgm2.getVId()).flush();
                    }
                    else {
                        bat.worldSceneLog.Indent().appendLogMsg("set pgm state as:" + this.getGeneralState()).appendPlayerName(Ownner.getPlayerName()).appendGeneralName(((General)dataGetter.getGeneralCache().get((Object)pgm2.getGeneralId())).getName()).appendPlayerId(Ownner.getPlayerId()).append("pgm vId", pgm2.getVId()).newLine();
                        defNum += campArmy.getArmyHpOrg();
                        bat.defCamp.add(campArmy);
                        BattleSceneLog.getInstance().info("#batId:" + bat.getBattleId() + "_" + bat.getStartTime() + "#add:init#side:def" + "#playerId:" + campArmy.getPlayerId() + ":" + campArmy.isPhantom + "#general:" + campArmy.getGeneralId() + "#defSize:" + bat.defCamp.size());
                        final int zdzsTech = dataGetter.getTechEffectCache().getTechEffect(pgm2.getPlayerId(), 43);
                        int autoStrategy = 0;
                        if (zdzsTech > 0) {
                            final PlayerBattleAttribute pba = dataGetter.getPlayerBattleAttributeDao().read(pgm2.getPlayerId());
                            autoStrategy = pba.getAutoStrategy();
                        }
                        else {
                            autoStrategy = -1;
                        }
                        bat.inBattlePlayers.put(pgm2.getPlayerId(), new PlayerInfo(pgm2.getPlayerId(), false, autoStrategy));
                    }
                }
            }
        }
        final List<PlayerGeneralMilitaryPhantom> phantomList = dataGetter.getPlayerGeneralMilitaryPhantomDao().getPhantomByLocationIdOrderByPlayerIdLvDesc(city.getId());
        final List<PlayerGeneralMilitaryPhantom> defPhantomList = new LinkedList<PlayerGeneralMilitaryPhantom>();
        for (final PlayerGeneralMilitaryPhantom phantom : phantomList) {
            if (phantom.getForceId() == defForceId) {
                defPhantomList.add(phantom);
            }
        }
        for (final PlayerGeneralMilitaryPhantom phantom : defPhantomList) {
            final CampArmy CaPhantom = this.copyArmyFromPhantom(dataGetter, bat, phantom, 0);
            final PlayerInfo piPhantom = bat.inBattlePlayers.get(CaPhantom.playerId);
            if (piPhantom == null) {
                final int zdzsTech = dataGetter.getTechEffectCache().getTechEffect(phantom.getPlayerId(), 43);
                int autoStrategy = 0;
                if (zdzsTech > 0) {
                    final PlayerBattleAttribute pba = dataGetter.getPlayerBattleAttributeDao().read(phantom.getPlayerId());
                    autoStrategy = pba.getAutoStrategy();
                }
                else {
                    autoStrategy = -1;
                }
                bat.inBattlePlayers.put(CaPhantom.playerId, new PlayerInfo(CaPhantom.playerId, false, autoStrategy));
            }
            defNum += CaPhantom.getArmyHpOrg();
            bat.defCamp.add(CaPhantom);
            BattleSceneLog.getInstance().info("#batId:" + bat.getBattleId() + "_" + bat.getStartTime() + "#add:init#side:def" + "#playerId:" + CaPhantom.getPlayerId() + ":" + CaPhantom.isPhantom + "#general:" + CaPhantom.getGeneralId() + "#defSize:" + bat.defCamp.size());
            bat.worldSceneLog.Indent().appendLogMsg("phantom join def camp").appendPlayerName(CaPhantom.getPlayerName()).appendGeneralName(CaPhantom.getGeneralName()).appendPlayerId(CaPhantom.getPlayerId()).append("phantom vId", phantom.getVId()).newLine();
        }
        final List<BarbarainPhantom> BarPhantomList = dataGetter.getBarbarainPhantomDao().getBarPhantomByLocationId(city.getId());
        final List<BarbarainPhantom> defBarPhantomList = new LinkedList<BarbarainPhantom>();
        for (final BarbarainPhantom barPhantom : BarPhantomList) {
            if (barPhantom.getState() > 0) {
                ErrorSceneLog.getInstance().appendErrorMsg("barPhantom is not free").appendBattleId(bat.getBattleId()).append("barPhantom vId", barPhantom.getVId()).append("barPhantom state", barPhantom.getState()).appendClassName("CityBuilder").appendMethodName("initDefCamp").flush();
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
            if (barPhantom.getNpcType() == 1) {
                final Barbarain barbarain = (Barbarain)dataGetter.getBarbarainCache().get((Object)barPhantom.getBarbarainId());
                campArmy2 = this.copyArmyformBarPhantom(dataGetter, barbarain, bat, barPhantom, battleSide);
                if (campArmy2 == null) {
                    ErrorSceneLog.getInstance().appendErrorMsg("city battle init defCamp get null campArmy from BarPhantom").appendBattleId(bat.getBattleId()).append("barbarainId", barbarain.getId()).append("barPhantom vId", barPhantom.getVId()).append("barPhantom state", barPhantom.getState()).flush();
                    continue;
                }
            }
            else if (barPhantom.getNpcType() == 2) {
                final KtSdmzS ktSdmzS = (KtSdmzS)dataGetter.getKtSdmzSCache().get((Object)barPhantom.getBarbarainId());
                campArmy2 = this.copyArmyformBarPhantom2(dataGetter, ktSdmzS, bat, barPhantom, battleSide);
                if (campArmy2 == null) {
                    ErrorSceneLog.getInstance().appendErrorMsg("city battle init defCamp get null campArmy from BarPhantom").appendBattleId(bat.getBattleId()).append("ktSdmzS", ktSdmzS.getId()).append("barPhantom vId", barPhantom.getVId()).append("barPhantom state", barPhantom.getState()).flush();
                    continue;
                }
            }
            else if (barPhantom.getNpcType() == 3) {
                final Barbarain barbarain = (Barbarain)dataGetter.getBarbarainCache().get((Object)barPhantom.getBarbarainId());
                campArmy2 = this.copyArmyformBarPhantom3(dataGetter, barbarain, bat, barPhantom, battleSide);
                if (campArmy2 == null) {
                    ErrorSceneLog.getInstance().appendErrorMsg("city battle init defCamp get null campArmy from BarPhantom").appendBattleId(bat.getBattleId()).append("barbarainId", barbarain.getId()).append("barPhantom vId", barPhantom.getVId()).append("barPhantom state", barPhantom.getState()).flush();
                    continue;
                }
            }
            else if (barPhantom.getNpcType() == 4) {
                campArmy2 = this.copyArmyformBarPhantom4(dataGetter, bat, barPhantom, battleSide);
                if (campArmy2 == null) {
                    ErrorSceneLog.getInstance().appendErrorMsg("city battle init defCamp get null campArmy from BarPhantom").appendBattleId(bat.getBattleId()).append("barPhantom vId", barPhantom.getVId()).append("barPhantom state", barPhantom.getState()).flush();
                    continue;
                }
            }
            if (campArmy2 != null) {
                dataGetter.getBarbarainPhantomDao().updateState(barPhantom.getVId(), 3);
                defNum += campArmy2.getArmyHpOrg();
                hzDefNpcNum += campArmy2.getArmyHpOrg();
                bat.defCamp.add(campArmy2);
            }
        }
        final List<ExpeditionArmy> ExpeditionArmyList = dataGetter.getExpeditionArmyDao().getEAsByLocationId(city.getId());
        final List<ExpeditionArmy> defExpeditionArmyList = new LinkedList<ExpeditionArmy>();
        for (final ExpeditionArmy expeditionArmy : ExpeditionArmyList) {
            if (expeditionArmy.getForceId() == defForceId) {
                if (expeditionArmy.getState() > 0) {
                    ErrorSceneLog.getInstance().appendErrorMsg("expeditionArmy is not free").appendBattleId(bat.getBattleId()).append("expeditionArmy vId", expeditionArmy.getVId()).append("expeditionArmy state", expeditionArmy.getState()).appendClassName("CityBuilder").appendMethodName("initDefCamp").flush();
                }
                else {
                    defExpeditionArmyList.add(expeditionArmy);
                }
            }
        }
        for (final ExpeditionArmy expeditionArmy : defExpeditionArmyList) {
            final EfLv eflv = (EfLv)dataGetter.getEfLvCache().get((Object)expeditionArmy.getEfLvId());
            final int battleSide2 = 0;
            final CampArmy EACa = this.copyArmyformExpeditionArmy(dataGetter, eflv, bat, expeditionArmy, battleSide2);
            if (EACa == null) {
                ErrorSceneLog.getInstance().appendErrorMsg("city battle init defCamp get null campArmy from expeditionArmy").appendBattleId(bat.getBattleId()).append("eflv Id", eflv.getLv()).append("expeditionArmy vId", expeditionArmy.getVId()).append("expeditionArmy state", expeditionArmy.getState()).flush();
            }
            else {
                dataGetter.getExpeditionArmyDao().updateState(expeditionArmy.getVId(), 3);
                defNum += EACa.getArmyHpOrg();
                hzDefNpcNum += EACa.getArmyHpOrg();
                bat.defCamp.add(EACa);
            }
        }
        final List<BarbarainExpeditionArmy> barExpeditionArmyList = dataGetter.getBarbarainExpeditionArmyDao().getBarEAsByLocationId(city.getId());
        final List<BarbarainExpeditionArmy> defBarExpeditionArmyList = new LinkedList<BarbarainExpeditionArmy>();
        for (final BarbarainExpeditionArmy barExpeditionArmy : barExpeditionArmyList) {
            if (barExpeditionArmy.getForceId() == defForceId) {
                if (barExpeditionArmy.getState() > 0) {
                    ErrorSceneLog.getInstance().appendErrorMsg("expeditionArmy is not free").appendBattleId(bat.getBattleId()).append("barExpeditionArmy vId", barExpeditionArmy.getVId()).append("barExpeditionArmy state", barExpeditionArmy.getState()).appendClassName("CityBuilder").appendMethodName("initDefCamp").flush();
                }
                else {
                    defBarExpeditionArmyList.add(barExpeditionArmy);
                }
            }
        }
        for (final BarbarainExpeditionArmy barExpeditionArmy : defBarExpeditionArmyList) {
            final WorldPaidB worldPaidB = (WorldPaidB)dataGetter.getWorldPaidBCache().get((Object)barExpeditionArmy.getWorldPaidBId());
            final int battleSide3 = 0;
            final CampArmy EACa2 = this.copyArmyformBarExpeditionArmy(dataGetter, worldPaidB, bat, barExpeditionArmy, battleSide3);
            if (EACa2 == null) {
                ErrorSceneLog.getInstance().appendErrorMsg("city battle init defCamp get null campArmy from expeditionArmy").appendBattleId(bat.getBattleId()).append("worldPaidB Id", worldPaidB.getId()).append("barExpeditionArmy vId", barExpeditionArmy.getVId()).append("barExpeditionArmy state", barExpeditionArmy.getState()).flush();
            }
            else {
                dataGetter.getBarbarainExpeditionArmyDao().updateState(barExpeditionArmy.getVId(), 3);
                defNum += EACa2.getArmyHpOrg();
                hzDefNpcNum += EACa2.getArmyHpOrg();
                bat.defCamp.add(EACa2);
            }
        }
        final List<NationTaskExpeditionArmy> NTExpeditionArmyList = dataGetter.getNationTaskExpeditionArmyDao().getNationTaskEAsByLocationId(city.getId());
        final List<NationTaskExpeditionArmy> defNTExpeditionArmyList = new LinkedList<NationTaskExpeditionArmy>();
        for (final NationTaskExpeditionArmy tempExpeditionArmy : NTExpeditionArmyList) {
            if (tempExpeditionArmy.getForceId() == defForceId) {
                if (tempExpeditionArmy.getState() > 0) {
                    ErrorSceneLog.getInstance().appendErrorMsg("NTexpeditionArmy is not free").appendBattleId(bat.getBattleId()).append("NTexpeditionArmy vId", tempExpeditionArmy.getVId()).append("NTexpeditionArmy state", tempExpeditionArmy.getState()).appendClassName("CityBuilder").appendMethodName("initDefCamp").flush();
                }
                else {
                    defNTExpeditionArmyList.add(tempExpeditionArmy);
                }
            }
        }
        for (final NationTaskExpeditionArmy tempExpeditionArmy : defNTExpeditionArmyList) {
            final EfLv eflv2 = (EfLv)dataGetter.getEfLvCache().get((Object)tempExpeditionArmy.getTableId());
            final int battleSide4 = 0;
            final CampArmy EACa3 = this.copyArmyFromNationTaskExpeditionArmy(dataGetter, eflv2, bat, tempExpeditionArmy, battleSide4);
            if (EACa3 == null) {
                ErrorSceneLog.getInstance().appendErrorMsg("city battle init defCamp get null campArmy from NationTaskExpeditionArmy").appendBattleId(bat.getBattleId()).append("eflv Id", eflv2.getEfLv()).append("NationTaskExpeditionArmy vId", tempExpeditionArmy.getVId()).append("NationTaskExpeditionArmy state", tempExpeditionArmy.getState()).flush();
            }
            else {
                dataGetter.getNationTaskExpeditionArmyDao().updateState(tempExpeditionArmy.getVId(), 3);
                defNum += EACa3.getArmyHpOrg();
                hzDefNpcNum += EACa3.getArmyHpOrg();
                bat.defCamp.add(EACa3);
            }
        }
        final List<ActivityNpc> activityNpcList = dataGetter.getActivityNpcDao().getActivityNpcsByLocationId(city.getId());
        final List<ActivityNpc> defActivityNpcList = new LinkedList<ActivityNpc>();
        for (final ActivityNpc activityNpc : activityNpcList) {
            if (activityNpc.getForceId() == defForceId) {
                if (activityNpc.getState() > 0) {
                    ErrorSceneLog.getInstance().appendErrorMsg("activityNpc is not free").appendBattleId(bat.getBattleId()).append("activityNpc vId", activityNpc.getVId()).append("activityNpc state", activityNpc.getState()).appendClassName("CityBuilder").appendMethodName("initDefCamp").flush();
                }
                else {
                    defActivityNpcList.add(activityNpc);
                }
            }
        }
        for (final ActivityNpc activityNpc : defActivityNpcList) {
            final int battleSide5 = 0;
            final CampArmy activityNpcCa = this.copyArmyFromActivityNpc(dataGetter, null, bat, activityNpc, battleSide5);
            if (activityNpcCa == null) {
                ErrorSceneLog.getInstance().appendErrorMsg("city battle init defCamp get null campArmy from NationTaskExpeditionArmy").appendBattleId(bat.getBattleId()).append("activityNpc vId", activityNpc.getVId()).append("activityNpc state", activityNpc.getState()).flush();
            }
            else {
                dataGetter.getActivityNpcDao().updateState(activityNpc.getVId(), 3);
                defNum += activityNpcCa.getArmyHpOrg();
                hzDefNpcNum += activityNpcCa.getArmyHpOrg();
                bat.defCamp.add(activityNpcCa);
            }
        }
        if (!dataGetter.getHuiZhanService().isHuiZhanInProcess(cityId)) {
            int defenceNpcNum = 0;
            if (city.getForceId() == 101 || city.getForceId() == 102 || city.getForceId() == 103) {
                defenceNpcNum = 5;
                for (int i = 0; i < defenceNpcNum; ++i) {
                    final CampArmy defenceNpc = copyArmyFromCityDefenceNpc(dataGetter, bat, city, 0);
                    if (defenceNpc != null) {
                        defNum += defenceNpc.getArmyHpOrg();
                        hzDefNpcNum += defenceNpc.getArmyHpOrg();
                        bat.defCamp.add(defenceNpc);
                        BattleSceneLog.getInstance().info("#batId:" + bat.getBattleId() + "_" + bat.getStartTime() + "#add:init#side:def" + "#playerId:" + defenceNpc.getPlayerId() + ":" + defenceNpc.isPhantom + "#general:" + defenceNpc.getGeneralId() + "#defSize:" + bat.defCamp.size());
                        bat.worldSceneLog.Indent().appendLogMsg("country defenceNpc join def camp").appendPlayerName(defenceNpc.getPlayerName()).appendGeneralName(defenceNpc.getGeneralName()).appendPlayerId(defenceNpc.getPlayerId()).newLine();
                    }
                }
            }
            else {
                final EfLv efLv = (EfLv)dataGetter.getEfLvCache().get((Object)((WorldCityCommon.MAX_COUNTRY_LV == 1) ? 1 : (WorldCityCommon.MAX_COUNTRY_LV - 1)));
                Integer[] armyIds = null;
                WorldCityDistanceNpcNum wcdnNum_w = null;
                switch (city.getForceId()) {
                    case 1: {
                        wcdnNum_w = (WorldCityDistanceNpcNum)dataGetter.getWorldCityDistanceNpcNumCache().get((Object)worldCity.getWeiDistance());
                        defenceNpcNum = wcdnNum_w.getNpcNum();
                        armyIds = efLv.getWeiDefArmyIds();
                        break;
                    }
                    case 2: {
                        wcdnNum_w = (WorldCityDistanceNpcNum)dataGetter.getWorldCityDistanceNpcNumCache().get((Object)worldCity.getShuDistance());
                        defenceNpcNum = wcdnNum_w.getNpcNum();
                        armyIds = efLv.getShuDefArmyIds();
                        break;
                    }
                    case 3: {
                        wcdnNum_w = (WorldCityDistanceNpcNum)dataGetter.getWorldCityDistanceNpcNumCache().get((Object)worldCity.getWuDistance());
                        defenceNpcNum = wcdnNum_w.getNpcNum();
                        armyIds = efLv.getWuDefArmyIds();
                        break;
                    }
                }
                final int armyId = armyIds[WebUtil.nextInt(armyIds.length)];
                for (int j = 0; j < defenceNpcNum; ++j) {
                    final CampArmy defenceNpc2 = copyArmyFromCityDefenceNpc(dataGetter, bat, city, armyId, 0);
                    if (defenceNpc2 != null) {
                        defNum += defenceNpc2.getArmyHpOrg();
                        hzDefNpcNum += defenceNpc2.getArmyHpOrg();
                        bat.defCamp.add(defenceNpc2);
                        BattleSceneLog.getInstance().info("#batId:" + bat.getBattleId() + "_" + bat.getStartTime() + "#add:init#side:def" + "#playerId:" + defenceNpc2.getPlayerId() + ":" + defenceNpc2.isPhantom + "#general:" + defenceNpc2.getGeneralId() + "#defSize:" + bat.defCamp.size());
                        bat.worldSceneLog.Indent().appendLogMsg("country defenceNpc join def camp").appendPlayerName(defenceNpc2.getPlayerName()).appendGeneralName(defenceNpc2.getGeneralName()).appendPlayerId(defenceNpc2.getPlayerId()).newLine();
                    }
                }
            }
        }
        bat.worldSceneLog.newLine();
        final BaseInfo defBaseInfo = bat.defBaseInfo;
        defBaseInfo.allNum += defNum;
        final BaseInfo defBaseInfo2 = bat.defBaseInfo;
        defBaseInfo2.num += defNum;
        bat.defBaseInfo.setForceId(defForceId);
        if (hzDefNpcNum > 0) {
            dataGetter.getBattleService().updateHuizhanNationForce(cityId, defForceId, hzDefNpcNum);
        }
    }
    
    public void initDefCampOfYellowTurbansNpc(final IDataGetter dataGetter, final City city, final Battle bat, final BattleAttacker battleAttacker, final int defForceId) {
        int defNum = 0;
        final List<YellowTurbans> yellowTurbansList = dataGetter.getYellowTurbansDao().getYellowTurbansByCityId(city.getId());
        final List<YellowTurbans> defYellowTurbansList = new LinkedList<YellowTurbans>();
        for (final YellowTurbans yellowTurbans : yellowTurbansList) {
            if (yellowTurbans.getForceId() == defForceId) {
                if (yellowTurbans.getState() > 0) {
                    ErrorSceneLog.getInstance().appendErrorMsg("NTYellowTurbans is not free").appendBattleId(bat.getBattleId()).append("NTYellowTurbans vId", yellowTurbans.getVId()).append("NTYellowTurbans state", yellowTurbans.getState()).appendClassName("CityBuilder").appendMethodName("initDefCamp").flush();
                }
                else {
                    defYellowTurbansList.add(yellowTurbans);
                }
            }
        }
        for (final YellowTurbans yellowTurbans : defYellowTurbansList) {
            final int battleSide = 0;
            final CampArmy yellowTurbansCamp = this.copyArmyfromNationTaskYellowTurbans(dataGetter, bat, yellowTurbans, battleSide);
            if (yellowTurbansCamp == null) {
                ErrorSceneLog.getInstance().appendErrorMsg("city battle init defCamp get null campArmy from NationTaskYellowTurbans").appendBattleId(bat.getBattleId()).append("YellowTurbans vId", yellowTurbans.getVId()).append("YellowTurbans state", yellowTurbans.getState()).flush();
            }
            else {
                dataGetter.getYellowTurbansDao().updateState(yellowTurbans.getVId(), 3);
                defNum += yellowTurbansCamp.getArmyHpOrg();
                bat.defCamp.add(yellowTurbansCamp);
                bat.worldSceneLog.newLine();
                final BaseInfo defBaseInfo = bat.defBaseInfo;
                defBaseInfo.allNum += defNum;
                final BaseInfo defBaseInfo2 = bat.defBaseInfo;
                defBaseInfo2.num += defNum;
                bat.defBaseInfo.setForceId(defForceId);
            }
        }
    }
    
    @Override
    public boolean initDefCamp(final IDataGetter dataGetter, final BattleAttacker battleAttacker, final int defId, final Battle bat) {
        final City city = dataGetter.getCityDao().read(defId);
        final boolean isNpc = city.getForceId() == 0;
        final int defForceId = city.getForceId();
        if (defForceId == 0) {
            this.initDefCampOfNpcBattle(dataGetter, city, bat, battleAttacker, defForceId);
        }
        else if (defForceId == 104) {
            this.initDefCampOfYellowTurbansNpc(dataGetter, city, bat, battleAttacker, defForceId);
        }
        else {
            BattleSceneLog.getInstance().error("initDefCamp--bat.getBattleId():" + bat.getBattleId() + " now:" + System.currentTimeMillis() + " defForceId:" + city.getForceId() + " attForceId:" + battleAttacker.attForceId + " defId:" + defId);
            this.initDefCampOfTruePlayerBattle(dataGetter, city, bat, battleAttacker, defForceId);
        }
        return isNpc;
    }
    
    public static CampArmy copyArmyFromCach(final int npcId, final int npcLost, final IDataGetter dataGetter, final int id, final int terrainType, final int npcLv) {
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
        final SpecialGeneral sg = new SpecialGeneral(general.getGeneralSpecialInfo().generalType, general.getGeneralSpecialInfo().param, general.getGeneralSpecialInfo().param2);
        campArmy.setSpecialGeneral(sg);
        campArmy.setGeneralPic(general.getPic());
        campArmy.setQuality(general.getQuality());
        campArmy.setStrength(general.getStrength());
        campArmy.setLeader(general.getLeader());
        campArmy.setTacicId(general.getTacticId());
        if (general.getTacticId() > 0) {
            campArmy.setTacticVal(1);
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
        int armyHp = armyCach.getArmyHp() - npcLost;
        final int remainder = armyHp % 3;
        armyHp -= remainder;
        campArmy.setArmyHp(armyHp);
        campArmy.setArmyHpOrg(armyHp);
        campArmy.setColumn(armyCach.getArmyHp() / armyCach.getTroopHp());
        campArmy.setStrategies(troop.getStrategyDefMap().get(terrainType));
        return campArmy;
    }
    
    public static CampArmy copyArmyFromCityDefenceNpc(final IDataGetter dataGetter, final Battle bat, final City city, final int batSide) {
        final CityDefenceNpc cityDefenceNpc = dataGetter.getCityDefenceNpcDao().read(city.getId());
        if (cityDefenceNpc == null) {
            ErrorSceneLog.getInstance().appendErrorMsg("copyArmyFromCityDefenceNpc. cityDefenceNpc is null").appendBattleId(bat.getBattleId()).append("cityId", city.getId()).append("city", ((WorldCity)dataGetter.getWorldCityCache().get((Object)city.getId())).getName()).flush();
            return null;
        }
        if (cityDefenceNpc.getGeneralId() == 0) {
            return null;
        }
        final CampArmy campArmy = new CampArmy();
        final General general = (General)dataGetter.getGeneralCache().get((Object)cityDefenceNpc.getGeneralId());
        final SpecialGeneral sg = new SpecialGeneral(general.getGeneralSpecialInfo().generalType, general.getGeneralSpecialInfo().param, general.getGeneralSpecialInfo().param2);
        if (general.getGeneralSpecialInfo().generalType == 2) {
            sg.param = ((WorldCity)dataGetter.getWorldCityCache().get((Object)bat.getDefBaseInfo().getId())).getDistance(city.getForceId());
        }
        campArmy.setSpecialGeneral(sg);
        final int troopId = cityDefenceNpc.getTroopId();
        final Troop troop = (Troop)dataGetter.getTroopCache().get((Object)troopId);
        campArmy.updateDB = false;
        campArmy.setPlayerLv(cityDefenceNpc.getPlayerLv());
        campArmy.setPlayerId(-1);
        campArmy.setForceId(city.getForceId());
        switch (city.getForceId()) {
            case 1: {
                campArmy.setPlayerName(LocalMessages.WEI_DEFENCE_PLAYER_NAME);
                break;
            }
            case 2: {
                campArmy.setPlayerName(LocalMessages.SHU_DEFENCE_PLAYER_NAME);
                break;
            }
            case 3: {
                campArmy.setPlayerName(LocalMessages.WU_DEFENCE_PLAYER_NAME);
                break;
            }
            case 101: {
                campArmy.setPlayerName(LocalMessages.WEI_DEFENCE_MANZU_NAME);
                break;
            }
            case 102: {
                campArmy.setPlayerName(LocalMessages.SHU_DEFENCE_MANZU_NAME);
                break;
            }
            case 103: {
                campArmy.setPlayerName(LocalMessages.WU_DEFENCE_MANZU_NAME);
                break;
            }
        }
        getTerrainValue(bat.terrainVal, troop, batSide, campArmy);
        campArmy.setTroopId(troop.getId());
        campArmy.setTroopSerial(troop.getSerial());
        campArmy.setTroopType(troop.getType());
        campArmy.setTroopName(troop.getName());
        campArmy.isDefenceNpc = true;
        campArmy.setGeneralId(cityDefenceNpc.getGeneralId());
        campArmy.setGeneralLv(cityDefenceNpc.getGeneralLv());
        campArmy.setPgmVId(0);
        campArmy.setStrength(cityDefenceNpc.getStrength());
        campArmy.setLeader(cityDefenceNpc.getLeader());
        campArmy.setArmyName(general.getName());
        campArmy.setGeneralName(general.getName());
        campArmy.setGeneralPic("zumao");
        campArmy.setQuality(general.getQuality());
        campArmy.setTacicId(general.getTacticId());
        campArmy.setId(bat.campNum.getAndIncrement());
        int forces = cityDefenceNpc.getHp();
        final int remainder = forces % 3;
        forces -= remainder;
        campArmy.setArmyHp(forces);
        campArmy.setArmyHpOrg(forces);
        campArmy.setAttEffect(cityDefenceNpc.getAtt());
        campArmy.setDefEffect(cityDefenceNpc.getDef());
        campArmy.setMaxForces(cityDefenceNpc.getHp());
        campArmy.setColumn(cityDefenceNpc.getColumnNum());
        campArmy.setTroopHp(cityDefenceNpc.getHp() / cityDefenceNpc.getColumnNum());
        campArmy.setAttDef_B(new AttDef_B());
        campArmy.getAttDef_B().ATT_B = cityDefenceNpc.getAttB();
        campArmy.getAttDef_B().DEF_B = cityDefenceNpc.getDefB();
        campArmy.setTACTIC_ATT(cityDefenceNpc.getTacticAtt());
        campArmy.setTACTIC_DEF(cityDefenceNpc.getTacticDef());
        if (general.getTacticId() > 0) {
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
    
    public static CampArmy copyArmyFromCityDefenceNpc(final IDataGetter dataGetter, final Battle bat, final City city, int armyId, final int batSide) {
        final CampArmy campArmy = new CampArmy();
        final WnCitynpcLv wnCitynpcLv = dataGetter.getWnCitynpcLvCache().getWnCitynpcLvByDay(dataGetter.getRankService().getCountryNpcDefDays());
        if (wnCitynpcLv == null) {
            return null;
        }
        Integer[] armyIds = null;
        campArmy.setPlayerId(-1);
        switch (city.getForceId()) {
            case 1: {
                campArmy.setPlayerName(LocalMessages.WEI_DEFENCE_PLAYER_NAME);
                armyIds = wnCitynpcLv.getWeiArmyIds();
                break;
            }
            case 2: {
                campArmy.setPlayerName(LocalMessages.SHU_DEFENCE_PLAYER_NAME);
                armyIds = wnCitynpcLv.getShuArmyIds();
                break;
            }
            case 3: {
                campArmy.setPlayerName(LocalMessages.WU_DEFENCE_PLAYER_NAME);
                armyIds = wnCitynpcLv.getWuArmyIds();
                break;
            }
        }
        armyId = armyIds[WebUtil.nextInt(armyIds.length)];
        final Army armyCach = (Army)dataGetter.getArmyCache().get((Object)armyId);
        campArmy.setForceId(city.getForceId());
        campArmy.setPlayerLv(armyCach.getGeneralLv());
        campArmy.setId(bat.campNum.getAndIncrement());
        campArmy.isDefenceNpc = true;
        campArmy.setPgmVId(0);
        campArmy.setArmyName(armyCach.getName());
        campArmy.setGeneralId(armyCach.getGeneralId());
        campArmy.setGeneralName(campArmy.getPlayerName());
        campArmy.setGeneralLv(wnCitynpcLv.getGLv());
        final General general = (General)dataGetter.getGeneralCache().get((Object)armyCach.getGeneralId());
        final SpecialGeneral sg = new SpecialGeneral(general.getGeneralSpecialInfo().generalType, general.getGeneralSpecialInfo().param, general.getGeneralSpecialInfo().param2);
        campArmy.setSpecialGeneral(sg);
        campArmy.setGeneralPic(general.getPic());
        campArmy.setQuality(general.getQuality());
        campArmy.setStrength(general.getStrength());
        campArmy.setLeader(general.getLeader());
        campArmy.setTacicId(general.getTacticId());
        if (general.getTacticId() > 0) {
            campArmy.setTacticVal(1);
            if (campArmy.getSpecialGeneral().generalType == 7) {
                campArmy.setTacticVal((int)campArmy.getSpecialGeneral().param);
            }
        }
        final Troop troop = (Troop)dataGetter.getTroopCache().get((Object)general.getTroop());
        campArmy.setTroopId(troop.getId());
        campArmy.setTroopSerial(troop.getSerial());
        campArmy.setTroopType(troop.getType());
        campArmy.setTroopName(troop.getName());
        campArmy.setAttEffect(armyCach.getAtt());
        campArmy.setDefEffect(armyCach.getDef());
        campArmy.setBdEffect(armyCach.getBd());
        campArmy.setTroopHp(armyCach.getTroopHp());
        campArmy.setMaxForces(armyCach.getTroopHp());
        int armyHp = armyCach.getArmyHp();
        final int remainder = armyHp % 3;
        armyHp -= remainder;
        campArmy.setArmyHp(armyHp);
        campArmy.setArmyHpOrg(armyHp);
        campArmy.setColumn(armyCach.getArmyHp() / armyCach.getTroopHp());
        campArmy.setStrategies(troop.getStrategyDefMap().get(bat.terrainVal));
        return campArmy;
    }
    
    public CampArmy copyArmyFromCach(final IDataGetter dataGetter, final CityNpc cn, final int id, final Battle bat) {
        final CampArmy campArmy = new CampArmy();
        final WorldCountryNpc wcn = (WorldCountryNpc)dataGetter.getWorldCountryNpcCache().get((Object)cn.getNpcId());
        bat.defBaseInfo.setNpcNum(bat.defBaseInfo.getNpcNum() + 1);
        campArmy.setPlayerId(-1 - bat.defBaseInfo.getNpcNum());
        campArmy.setPlayerName(wcn.getName());
        campArmy.setForceId(cn.getForceId());
        campArmy.setPlayerLv(cn.getArmyLv());
        campArmy.setId(id);
        campArmy.setPgmVId(0);
        campArmy.setArmyName(wcn.getName());
        campArmy.setGeneralId(wcn.getId());
        final General general = (General)dataGetter.getGeneralCache().get((Object)campArmy.getGeneralId());
        final SpecialGeneral sg = new SpecialGeneral(general.getGeneralSpecialInfo().generalType, general.getGeneralSpecialInfo().param, general.getGeneralSpecialInfo().param2);
        if (general.getGeneralSpecialInfo().generalType == 2) {
            sg.param = ((WorldCity)dataGetter.getWorldCityCache().get((Object)bat.getDefBaseInfo().getId())).getDistance(cn.getForceId());
        }
        campArmy.setSpecialGeneral(sg);
        campArmy.setGeneralName(wcn.getName());
        campArmy.setGeneralLv(cn.getArmyLv());
        campArmy.setGeneralPic(wcn.getPic());
        campArmy.setQuality(WebUtil.nextInt(6) + 1);
        campArmy.setStrength(wcn.getStrength());
        campArmy.setLeader(wcn.getLeader());
        campArmy.setTacicId(wcn.getTacticId());
        if (wcn.getTacticId() > 0) {
            campArmy.setTacticVal(1);
            if (campArmy.getSpecialGeneral().generalType == 7) {
                campArmy.setTacticVal((int)campArmy.getSpecialGeneral().param);
            }
        }
        final Troop troop = (Troop)dataGetter.getTroopCache().get((Object)wcn.getTroopId());
        campArmy.setTroopId(troop.getId());
        campArmy.setTroopSerial(troop.getSerial());
        campArmy.setTroopType(troop.getType());
        campArmy.setTroopName(troop.getName());
        campArmy.setAttEffect(cn.getAtt());
        campArmy.setDefEffect(cn.getDef());
        campArmy.setTroopHp(cn.getMaxHp());
        campArmy.setMaxForces(cn.getMaxHp());
        int armyHp = cn.getHp();
        final int remainder = armyHp % 3;
        armyHp -= remainder;
        campArmy.setArmyHp(armyHp);
        campArmy.setArmyHpOrg(armyHp);
        campArmy.setColumn(armyHp / cn.getMaxHp());
        campArmy.setStrategies(troop.getStrategyDefMap().get(bat.terrainVal));
        return campArmy;
    }
    
    @Override
    public void dealKillrank(final boolean isBarbarainInvade, int gKillTotal, final Battle bat, final IDataGetter dataGetter, final int playerId) {
        try {
            if (gKillTotal <= 0 || playerId <= 0) {
                return;
            }
            final int defId = bat.getDefBaseInfo().getId();
            final Player player = dataGetter.getPlayerDao().read(playerId);
            if (player == null) {
                return;
            }
            final HuizhanHistory hh = dataGetter.getHuiZhanService().getTodayHuizhanInProcess();
            if (hh != null && hh.getCityId() == defId) {
                final BaseRanker ranker = RankService.HuiZhanKillRanker;
                if (ranker != null) {
                    final PlayerHuizhan ph = dataGetter.getPlayerHuizhanDao().getByhzIdAndPlayerId(hh.getVId(), playerId);
                    if (ph != null) {
                        ranker.fireRankEvent(1, new RankData(playerId, gKillTotal + ph.getKillNum()));
                    }
                    ranker.fireTotalChange(player.getForceId(), gKillTotal, System.currentTimeMillis());
                }
                dataGetter.getPlayerHuizhanDao().updateKillNumByhzIdAndPlayerId(gKillTotal, hh.getVId(), playerId);
            }
            Date date = new Date();
            date = WorldCityCommon.getDateAfter23(date);
            final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            final String dateStr = sdf.format(date);
            if (gKillTotal > 0) {
                final boolean shouldDouble = bat.IsShaDiLingDoubleed(player.getForceId());
                if (shouldDouble) {
                    gKillTotal *= 2;
                }
                int killNum = gKillTotal;
                final PlayerKillInfo pki = dataGetter.getPlayerKillInfoDao().getByTodayInfo(playerId, dateStr);
                if (pki != null) {
                    killNum += pki.getKillNum();
                }
                if (isBarbarainInvade) {
                    int taskType = dataGetter.getRankService().hasNationTasks(player.getForceId());
                    taskType = ((taskType == 7 || taskType == 3) ? taskType : 1);
                    dataGetter.getRankService().updateKillNum(taskType, gKillTotal, playerId, System.currentTimeMillis());
                    dataGetter.getIndividualTaskService().sendTaskMessage(new PlayerDto(playerId, player.getForceId()), 1, "killmz");
                }
                else {
                    final PlayerAttribute pa = dataGetter.getPlayerAttributeDao().read(playerId);
                    final char[] cs = pa.getFunctionId().toCharArray();
                    final boolean functionIsOpen = cs[32] == '1';
                    dataGetter.getRankService().updateKillNum(1, gKillTotal, playerId, System.currentTimeMillis());
                    dataGetter.getRankService().updateWholeKillNum(playerId, gKillTotal);
                    if (functionIsOpen) {
                        dataGetter.getKillRankService().updateKillNum(playerId, killNum, gKillTotal);
                        final int succ = dataGetter.getPlayerKillInfoDao().updateKillNum(playerId, gKillTotal, dateStr);
                        if (succ < 1 && pki == null) {
                            final PlayerKillInfo pkr = new PlayerKillInfo();
                            pkr.setPlayerId(playerId);
                            pkr.setForceId(player.getForceId());
                            pkr.setBox_reward_info("0,0,0,0,0");
                            pkr.setKillNum(gKillTotal);
                            pkr.setKillDate(date);
                            dataGetter.getPlayerKillInfoDao().create(pkr);
                            final JsonDocument doc = new JsonDocument();
                            doc.startObject();
                            doc.createElement("playerId", playerId);
                            doc.createElement("killNum", killNum);
                            doc.endObject();
                            Players.push(playerId, PushCommand.PUSH_KILL_ADD, doc.toByte());
                            dataGetter.getKillRankService().fireRankEvent(1, new RankData(playerId, gKillTotal));
                        }
                        else {
                            final JsonDocument doc2 = new JsonDocument();
                            doc2.startObject();
                            doc2.createElement("playerId", playerId);
                            doc2.createElement("killNum", killNum);
                            doc2.endObject();
                            Players.push(playerId, PushCommand.PUSH_KILL_ADD, doc2.toByte());
                            dataGetter.getKillRankService().fireRankEvent(1, new RankData(playerId, killNum));
                        }
                        dataGetter.getRankService().updateTodayKillNum(playerId, gKillTotal);
                    }
                }
                dataGetter.getIndividualTaskService().sendTaskMessage(new PlayerDto(playerId, player.getForceId()), gKillTotal, "kill");
            }
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("CityBuilder dealKillrank ERROR playerId:" + playerId, e);
        }
    }
    
    @Override
    public void sendBattleCityEndInfo(final IDataGetter dataGetter, final Battle bat) {
        final int cityId = bat.defBaseInfo.id;
        try {
            bat.worldSceneLog.appendCityId(cityId).appendCityName(((WorldCity)dataGetter.getWorldCityCache().get((Object)cityId)).getName()).appendBattleId(bat.getBattleId()).appendLogMsg("reset city state").appendLogMsg("quit lead to battle ended").appendClassName("CityBuilder").appendMethodName("sendBattleCityEndInfo").newLine();
            dataGetter.getCityService().changeState(cityId, 0, false);
        }
        catch (Exception e) {
            final City city = dataGetter.getCityDao().read(cityId);
            bat.worldSceneLog.appendLogMsg("sendBattleCityEndInfo reset city state catch exception").append("now state", city.getState()).newLine();
            ErrorSceneLog.getInstance().appendErrorMsg("quit lead to battle ended, reset city state catch exception").append("cityId", cityId).append("cityName", ((WorldCity)dataGetter.getWorldCityCache().get((Object)cityId)).getName()).appendClassName("CityBuilder").appendMethodName("sendBattleCityEndInfo").flush();
        }
    }
    
    @Override
    public Battle existBattle(final IDataGetter dataGetter, final Player player, final int defId) {
        Battle battle = NewBattleManager.getInstance().getBattleByParm(this.battleType, player.getForceId(), defId);
        if (battle == null) {
            battle = NewBattleManager.getInstance().getBattleByDefId(this.battleType, defId);
        }
        return battle;
    }
    
    @Override
    public Tuple<List<PlayerGeneralMilitary>, String> chooseGeneral(final IDataGetter dataGetter, final Player player, final int defId, final List<Integer> gIdList) {
        final Tuple<List<PlayerGeneralMilitary>, String> tuple = new Tuple();
        final List<PlayerGeneralMilitary> chooseList = new ArrayList<PlayerGeneralMilitary>();
        final Map<Integer, PlayerGeneralMilitary> pgmMap = dataGetter.getPlayerGeneralMilitaryDao().getMilitaryMap(player.getPlayerId());
        boolean needCheckLocation = true;
        if (ManWangLingManager.getInstance().manWangLingObjMap.get(player.getForceId()) != null) {
            needCheckLocation = false;
        }
        final Date nowDate = new Date();
        for (int i = 0; i < gIdList.size(); ++i) {
            final PlayerGeneralMilitary pgm = pgmMap.get(gIdList.get(i));
            final GeneralMoveDto gmd = CityService.getGeneralMoveDto(pgm.getPlayerId(), pgm.getGeneralId());
            if (gmd != null && CDUtil.isInCD(gmd.nextMoveTime, nowDate)) {
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
                        final boolean hzFlag = dataGetter.getHuiZhanService().isHuiZhanInProcess(defId);
                        if (!hzFlag && needCheckLocation && !BattleService.rewardContains(pgm.getVId())) {
                            final Set<Integer> neighborSet = dataGetter.getWorldRoadCache().getNeighbors(defId);
                            if (defId != pgm.getLocationId() && !neighborSet.contains(pgm.getLocationId())) {
                                continue;
                            }
                        }
                        if (!TeamManager.getInstance().isJoinTeam(pgm.getPlayerId(), pgm.getGeneralId())) {
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
        TaskMessageHelper.sendWorldPvpTaskMessage(playerId);
    }
    
    @Override
    public void dealUniqueStaff(final IDataGetter dataGetter, final Battle bat, final int playerId, final int defId) {
        final int attForceId = bat.getAttBaseInfo().getForceId();
        try {
            dataGetter.getCityDataCache().fireBattleMessage(bat.defBaseInfo.id, attForceId, bat.defBaseInfo.forceId);
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().appendErrorMsg("CityDataCache.fireBattleMessage Exception").appendClassName("CityBuilder").appendMethodName("dealUniqueStaff").append("cityId", bat.defBaseInfo.id).append("cityForceId", bat.defBaseInfo.forceId).append("attForceId", attForceId).flush();
            ErrorSceneLog.getInstance().error("dealUniqueStaff ", e);
        }
        CityEventManager.getInstance().pushCityEventChangeInfoDueToBattle(defId);
        dataGetter.getCourtesyService().addPlayerEvent(playerId, 8, 0);
    }
    
    @Override
    public int getGeneralState() {
        return 3;
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
            campArmy = copyCampFromPlayerTable(player, pgm, dataGetter, this.getGeneralState(), bat, battleSide);
            if (campArmy != null) {
                num += campArmy.getArmyHpOrg();
                if (teamInfo != null) {
                    campArmy.setTeamGenreal(teamInfo.teamName);
                    campArmy.setTeamEffect(teamInfo.teamEffect);
                }
                campMap.add(campArmy);
                BattleSceneLog.getInstance().info("#batId:" + bat.getBattleId() + "_" + bat.getStartTime() + "#add:join#side:" + ((battleSide == 1) ? "att" : "def") + "#playerId:" + campArmy.getPlayerId() + ":" + campArmy.isPhantom + "#general:" + campArmy.getGeneralId() + "#" + ((battleSide == 1) ? "att" : "def") + "Size:" + campMap.size());
                campChange.add(campArmy);
            }
        }
        final boolean hzFlag = dataGetter.getHuiZhanService().isHuiZhanInStatePreparation(bat.getDefBaseInfo().getId());
        if (hzFlag) {
            bat.caculateAgainstInfo(dataGetter);
        }
        baseInfo.setNum(baseInfo.getNum() + num);
        baseInfo.setAllNum(baseInfo.getAllNum() + num);
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
    
    @Override
    public void initAttCamp(final IDataGetter dataGetter, final BattleAttacker battleAttacker, final int cityId, final Battle bat) {
        final int attType = battleAttacker.attType;
        int attForceId = battleAttacker.attForceId;
        final HuizhanHistory hh = dataGetter.getHuiZhanService().getTodayHuizhanBySate(1);
        if (hh != null && cityId == hh.getCityId() && attForceId == hh.getDefForceId()) {
            attForceId = hh.getAttForceId1();
        }
        final City city = dataGetter.getCityDao().read(cityId);
        int attNum = 0;
        final int defForceId = city.getForceId();
        final int battleSide = 1;
        int hzAttNpcNum = 0;
        if (attType == 2) {
            final WorldCity wc = (WorldCity)dataGetter.getWorldCityCache().get((Object)cityId);
            final int distance = wc.getDistance(attForceId);
            final WorldGuardInfo wgi = (WorldGuardInfo)dataGetter.getWorldGuardInfoCache().get((Object)distance);
            final WorldGuard wg = dataGetter.getWorldGuardCache().getByForceIdDegree(attForceId, wgi.getNpcDegreeBase());
            CampArmy campArmy = null;
            int id = 0;
            for (int i = 0; i < wgi.getNpcNum(); ++i) {
                id = bat.campNum.getAndIncrement();
                final Player playerTemp = new Player();
                playerTemp.setPlayerId(-2);
                playerTemp.setPlayerName(wg.getName());
                playerTemp.setForceId(attForceId);
                playerTemp.setPlayerLv(wg.getLv());
                campArmy = Builder.copyArmyFromCach(playerTemp, wg.getArmyId(), dataGetter, id, bat.terrainVal, wg.getLv());
                if (campArmy != null) {
                    attNum += campArmy.getArmyHpOrg();
                    hzAttNpcNum += campArmy.getArmyHpOrg();
                    bat.attCamp.add(campArmy);
                    bat.newlyJoinSet.add(campArmy);
                    BattleSceneLog.getInstance().info("#batId:" + bat.getBattleId() + "_" + bat.getStartTime() + "#add:init#side:att" + "#playerId:" + campArmy.getPlayerId() + ":" + campArmy.isPhantom + "#general:" + campArmy.getGeneralId() + "#attSize:" + bat.attCamp.size());
                }
            }
        }
        if (attType == 1) {
            for (final PlayerGeneralMilitary pgm : battleAttacker.pgmList) {
                dataGetter.getPlayerGeneralMilitaryDao().updateLocationId(pgm.getPlayerId(), pgm.getGeneralId(), cityId);
            }
        }
        final List<PlayerGeneralMilitary> pgmList = dataGetter.getPlayerGeneralMilitaryDao().getMilitaryByLocationIdOrderByPlayerIdLvDesc(cityId);
        final List<PlayerGeneralMilitary> attPgmList = new LinkedList<PlayerGeneralMilitary>();
        for (final PlayerGeneralMilitary pgm2 : pgmList) {
            if (pgm2.getForceId() != defForceId) {
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
                    final CampArmy campArmy2 = this.copyArmyFromPlayerTable(attPlayer, pgm2, dataGetter, this.getGeneralState(), bat, 1);
                    if (campArmy2 == null) {
                        ErrorSceneLog.getInstance().appendErrorMsg("city battle init defCamp get null campArmy").appendBattleId(bat.getBattleId()).appendPlayerName(attPlayer.getPlayerName()).appendPlayerId(attPlayer.getPlayerId()).appendGeneralId(pgm2.getPlayerId()).append("vId", pgm2.getVId()).flush();
                    }
                    else {
                        bat.worldSceneLog.Indent().appendLogMsg("set pgm state as:" + this.getGeneralState()).appendPlayerName(attPlayer.getPlayerName()).appendGeneralName(((General)dataGetter.getGeneralCache().get((Object)pgm2.getGeneralId())).getName()).appendPlayerId(attPlayer.getPlayerId()).append("pgm vId", pgm2.getVId()).newLine();
                        attNum += campArmy2.getArmyHpOrg();
                        bat.attCamp.add(campArmy2);
                        BattleSceneLog.getInstance().info("#batId:" + bat.getBattleId() + "_" + bat.getStartTime() + "#add:init#side:att" + "#playerId:" + campArmy2.getPlayerId() + ":" + campArmy2.isPhantom + "#general:" + campArmy2.getGeneralId() + "#defSize:" + bat.attCamp.size());
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
        final List<PlayerGeneralMilitaryPhantom> phantomList = dataGetter.getPlayerGeneralMilitaryPhantomDao().getPhantomByLocationIdOrderByPlayerIdLvDesc(city.getId());
        final List<PlayerGeneralMilitaryPhantom> attPhantomList = new LinkedList<PlayerGeneralMilitaryPhantom>();
        for (final PlayerGeneralMilitaryPhantom phantom : phantomList) {
            if (phantom.getForceId() != defForceId) {
                attPhantomList.add(phantom);
            }
        }
        for (final PlayerGeneralMilitaryPhantom phantom : attPhantomList) {
            final CampArmy attCaPhantom = this.copyArmyFromPhantom(dataGetter, bat, phantom, 1);
            final PlayerInfo piPhantom = bat.inBattlePlayers.get(attCaPhantom.playerId);
            if (piPhantom == null) {
                final int zdzsTech2 = dataGetter.getTechEffectCache().getTechEffect(phantom.getPlayerId(), 43);
                int autoStrategy2 = 0;
                if (zdzsTech2 > 0) {
                    final PlayerBattleAttribute pba2 = dataGetter.getPlayerBattleAttributeDao().read(phantom.getPlayerId());
                    autoStrategy2 = pba2.getAutoStrategy();
                }
                else {
                    autoStrategy2 = -1;
                }
                bat.inBattlePlayers.put(attCaPhantom.playerId, new PlayerInfo(attCaPhantom.playerId, true, autoStrategy2));
            }
            attNum += attCaPhantom.getArmyHpOrg();
            bat.attCamp.add(attCaPhantom);
            BattleSceneLog.getInstance().info("#batId:" + bat.getBattleId() + "_" + bat.getStartTime() + "#add:init#side:att" + "#playerId:" + attCaPhantom.getPlayerId() + ":" + attCaPhantom.isPhantom + "#general:" + attCaPhantom.getGeneralId() + "#attSize:" + bat.attCamp.size());
            bat.worldSceneLog.Indent().appendLogMsg("phantom join att camp").appendPlayerName(attCaPhantom.getPlayerName()).appendGeneralName(attCaPhantom.getGeneralName()).appendPlayerId(attCaPhantom.getPlayerId()).append("phantom vId", phantom.getVId()).newLine();
        }
        final List<BarbarainPhantom> BarPhantomList = dataGetter.getBarbarainPhantomDao().getBarPhantomByLocationId(city.getId());
        final List<BarbarainPhantom> attBarPhantomList = new LinkedList<BarbarainPhantom>();
        for (final BarbarainPhantom barPhantom : BarPhantomList) {
            if (barPhantom.getForceId() != defForceId) {
                if (barPhantom.getState() > 0) {
                    ErrorSceneLog.getInstance().appendErrorMsg("barPhantom is not free").appendBattleId(bat.getBattleId()).append("barPhantom vId", barPhantom.getVId()).append("barPhantom state", barPhantom.getState()).appendClassName("CityBuilder").appendMethodName("initAttCamp").flush();
                }
                else {
                    attBarPhantomList.add(barPhantom);
                }
            }
        }
        for (final BarbarainPhantom barPhantom : attBarPhantomList) {
            CampArmy campArmy3 = null;
            if (barPhantom.getNpcType() == 1) {
                final Barbarain barbarain = (Barbarain)dataGetter.getBarbarainCache().get((Object)barPhantom.getBarbarainId());
                campArmy3 = this.copyArmyformBarPhantom(dataGetter, barbarain, bat, barPhantom, battleSide);
                if (campArmy3 == null) {
                    ErrorSceneLog.getInstance().appendErrorMsg("city battle init attCamp get null campArmy from BarPhantom").appendBattleId(bat.getBattleId()).append("barbarainId", barbarain.getId()).append("barPhantom vId", barPhantom.getVId()).append("barPhantom state", barPhantom.getState()).flush();
                    continue;
                }
            }
            else if (barPhantom.getNpcType() == 2) {
                final KtSdmzS ktSdmzS = (KtSdmzS)dataGetter.getKtSdmzSCache().get((Object)barPhantom.getBarbarainId());
                campArmy3 = this.copyArmyformBarPhantom2(dataGetter, ktSdmzS, bat, barPhantom, battleSide);
                if (campArmy3 == null) {
                    ErrorSceneLog.getInstance().appendErrorMsg("city battle init attCamp get null campArmy from BarPhantom").appendBattleId(bat.getBattleId()).append("ktSdmzS", ktSdmzS.getId()).append("barPhantom vId", barPhantom.getVId()).append("barPhantom state", barPhantom.getState()).flush();
                    continue;
                }
            }
            else if (barPhantom.getNpcType() == 3) {
                final Barbarain barbarain = (Barbarain)dataGetter.getBarbarainCache().get((Object)barPhantom.getBarbarainId());
                campArmy3 = this.copyArmyformBarPhantom3(dataGetter, barbarain, bat, barPhantom, battleSide);
                if (campArmy3 == null) {
                    ErrorSceneLog.getInstance().appendErrorMsg("city battle init attCamp get null campArmy from BarPhantom").appendBattleId(bat.getBattleId()).append("barbarainId", barbarain.getId()).append("barPhantom vId", barPhantom.getVId()).append("barPhantom state", barPhantom.getState()).flush();
                    continue;
                }
            }
            else if (barPhantom.getNpcType() == 4) {
                campArmy3 = this.copyArmyfromBarPhantom4(dataGetter, bat, barPhantom, battleSide);
                if (campArmy3 == null) {
                    ErrorSceneLog.getInstance().appendErrorMsg("city battle init attCamp get null campArmy from BarPhantom").appendBattleId(bat.getBattleId()).append("npcType", barPhantom.getNpcType()).append("barPhantom vId", barPhantom.getVId()).append("barPhantom state", barPhantom.getState()).flush();
                    continue;
                }
            }
            dataGetter.getBarbarainPhantomDao().updateState(barPhantom.getVId(), 3);
            attNum += campArmy3.getArmyHpOrg();
            hzAttNpcNum += campArmy3.getArmyHpOrg();
            bat.attCamp.add(campArmy3);
        }
        final List<ExpeditionArmy> ExpeditionArmyList = dataGetter.getExpeditionArmyDao().getEAsByLocationId(city.getId());
        final List<ExpeditionArmy> attExpeditionArmyList = new LinkedList<ExpeditionArmy>();
        for (final ExpeditionArmy expeditionArmy : ExpeditionArmyList) {
            if (expeditionArmy.getForceId() != defForceId) {
                if (expeditionArmy.getState() > 0) {
                    ErrorSceneLog.getInstance().appendErrorMsg("expeditionArmy is not free").appendBattleId(bat.getBattleId()).append("expeditionArmy vId", expeditionArmy.getVId()).append("expeditionArmy state", expeditionArmy.getState()).appendClassName("CityBuilder").appendMethodName("initAttCamp").flush();
                }
                else {
                    attExpeditionArmyList.add(expeditionArmy);
                }
            }
        }
        for (final ExpeditionArmy expeditionArmy : attExpeditionArmyList) {
            final EfLv eflv = (EfLv)dataGetter.getEfLvCache().get((Object)expeditionArmy.getEfLvId());
            final CampArmy EACa = this.copyArmyformExpeditionArmy(dataGetter, eflv, bat, expeditionArmy, battleSide);
            if (EACa == null) {
                ErrorSceneLog.getInstance().appendErrorMsg("city battle init attCamp get null campArmy from expeditionArmy").appendBattleId(bat.getBattleId()).append("eflv Id", eflv.getLv()).append("expeditionArmy vId", expeditionArmy.getVId()).append("expeditionArmy state", expeditionArmy.getState()).flush();
            }
            else {
                attNum += EACa.getArmyHpOrg();
                hzAttNpcNum += EACa.getArmyHpOrg();
                bat.attCamp.add(EACa);
            }
        }
        final List<BarbarainExpeditionArmy> barExpeditionArmyList = dataGetter.getBarbarainExpeditionArmyDao().getBarEAsByLocationId(city.getId());
        final List<BarbarainExpeditionArmy> attBarExpeditionArmyList = new LinkedList<BarbarainExpeditionArmy>();
        for (final BarbarainExpeditionArmy barExpeditionArmy : barExpeditionArmyList) {
            if (barExpeditionArmy.getForceId() != defForceId) {
                if (barExpeditionArmy.getState() > 0) {
                    ErrorSceneLog.getInstance().appendErrorMsg("expeditionArmy is not free").appendBattleId(bat.getBattleId()).append("barExpeditionArmy vId", barExpeditionArmy.getVId()).append("barExpeditionArmy state", barExpeditionArmy.getState()).appendClassName("CityBuilder").appendMethodName("initAttCamp").flush();
                }
                else {
                    attBarExpeditionArmyList.add(barExpeditionArmy);
                }
            }
        }
        for (final BarbarainExpeditionArmy barExpeditionArmy : attBarExpeditionArmyList) {
            final WorldPaidB worldPaidB = (WorldPaidB)dataGetter.getWorldPaidBCache().get((Object)barExpeditionArmy.getWorldPaidBId());
            final CampArmy BarEACa = this.copyArmyformBarExpeditionArmy(dataGetter, worldPaidB, bat, barExpeditionArmy, battleSide);
            if (BarEACa == null) {
                ErrorSceneLog.getInstance().appendErrorMsg("city battle init attCamp get null campArmy from expeditionArmy").appendBattleId(bat.getBattleId()).append("worldPaidB Id", worldPaidB.getId()).append("barExpeditionArmy vId", barExpeditionArmy.getVId()).append("barExpeditionArmy state", barExpeditionArmy.getState()).flush();
            }
            else {
                dataGetter.getBarbarainExpeditionArmyDao().updateState(barExpeditionArmy.getVId(), 3);
                attNum += BarEACa.getArmyHpOrg();
                hzAttNpcNum += BarEACa.getArmyHpOrg();
                bat.attCamp.add(BarEACa);
            }
        }
        final List<NationTaskExpeditionArmy> NTExpeditionArmy = dataGetter.getNationTaskExpeditionArmyDao().getNationTaskEAsByLocationId(city.getId());
        final List<NationTaskExpeditionArmy> attNTExpeditionArmy = new LinkedList<NationTaskExpeditionArmy>();
        for (final NationTaskExpeditionArmy tempEA : NTExpeditionArmy) {
            if (tempEA.getForceId() != defForceId) {
                if (tempEA.getState() > 0) {
                    ErrorSceneLog.getInstance().appendErrorMsg("NationTaskExpeditionArmy is not free").appendBattleId(bat.getBattleId()).append("NationTaskExpeditionArmy vId", tempEA.getVId()).append("NationTaskExpeditionArmy state", tempEA.getState()).appendClassName("CityBuilder").appendMethodName("initAttCamp").flush();
                }
                else {
                    attNTExpeditionArmy.add(tempEA);
                }
            }
        }
        for (final NationTaskExpeditionArmy tempEA : attNTExpeditionArmy) {
            final EfLv eflv2 = (EfLv)dataGetter.getEfLvCache().get((Object)tempEA.getTableId());
            final CampArmy NTEACa = this.copyArmyFromNationTaskExpeditionArmy(dataGetter, eflv2, bat, tempEA, battleSide);
            if (NTEACa == null) {
                ErrorSceneLog.getInstance().appendErrorMsg("city battle init attCamp get null campArmy from expeditionArmy").appendBattleId(bat.getBattleId()).append("eflv Id", eflv2.getEfLv()).append("NationTaskExpeditionArmy vId", tempEA.getVId()).append("NationTaskExpeditionArmy state", tempEA.getState()).flush();
            }
            else {
                dataGetter.getNationTaskExpeditionArmyDao().updateState(tempEA.getVId(), 3);
                attNum += NTEACa.getArmyHpOrg();
                hzAttNpcNum += NTEACa.getArmyHpOrg();
                bat.attCamp.add(NTEACa);
            }
        }
        final List<ActivityNpc> activityNpcList = dataGetter.getActivityNpcDao().getActivityNpcsByLocationId(city.getId());
        final List<ActivityNpc> attActivityNpcList = new LinkedList<ActivityNpc>();
        for (final ActivityNpc activityNpc : activityNpcList) {
            if (activityNpc.getForceId() != defForceId) {
                if (activityNpc.getState() > 0) {
                    ErrorSceneLog.getInstance().appendErrorMsg("NationTaskExpeditionArmy is not free").appendBattleId(bat.getBattleId()).append("NationTaskExpeditionArmy vId", activityNpc.getVId()).append("NationTaskExpeditionArmy state", activityNpc.getState()).appendClassName("CityBuilder").appendMethodName("initAttCamp").flush();
                }
                else {
                    attActivityNpcList.add(activityNpc);
                }
            }
        }
        for (final ActivityNpc activityNpc : attActivityNpcList) {
            final CampArmy activityNpcCa = this.copyArmyFromActivityNpc(dataGetter, null, bat, activityNpc, battleSide);
            if (activityNpcCa == null) {
                ErrorSceneLog.getInstance().appendErrorMsg("city battle init attCamp get null campArmy from activityNpc").appendBattleId(bat.getBattleId()).append("activityNpc vId", activityNpc.getVId()).append("activityNpc state", activityNpc.getState()).flush();
            }
            else {
                dataGetter.getActivityNpcDao().updateState(activityNpc.getVId(), 3);
                attNum += activityNpcCa.getArmyHpOrg();
                hzAttNpcNum += activityNpcCa.getArmyHpOrg();
                bat.attCamp.add(activityNpcCa);
            }
        }
        final BaseInfo attBaseInfo = bat.attBaseInfo;
        attBaseInfo.num += attNum;
        final BaseInfo attBaseInfo2 = bat.attBaseInfo;
        attBaseInfo2.allNum += attNum;
        bat.attBaseInfo.setForceId(attForceId);
        if (hzAttNpcNum > 0) {
            dataGetter.getBattleService().updateHuizhanNationForce(cityId, attForceId, hzAttNpcNum);
        }
    }
    
    @Override
    public CampArmy copyArmyFromPlayerTable(final Player player, final PlayerGeneralMilitary pgm, final IDataGetter dataGetter, final int generalBattleType, final Battle bat, final int batSide) {
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
        final PlayerBattleAttribute pba = dataGetter.getPlayerBattleAttributeDao().read(player.getPlayerId());
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
            final int res = dataGetter.getPlayerGeneralMilitaryDao().updateStateCheck(player.getPlayerId(), pgm.getGeneralId(), generalBattleType);
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
            NewBattleManager.getInstance().joinBattle(bat, pgm.getPlayerId(), pgm.getGeneralId());
        }
        dataGetter.getGeneralService().sendGmStateSet(player.getPlayerId(), pgm.getGeneralId(), this.battleType);
        int forces = pgm.getForces();
        final int remainder = forces % 3;
        forces -= remainder;
        campArmy.setArmyHp(forces);
        campArmy.setArmyHpOrg(forces);
        Builder.getAttDefHp(dataGetter, campArmy);
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
            final int done = dataGetter.getPlayerGeneralMilitaryDao().updateStateCityCheck(pgm.getPlayerId(), pgm.getGeneralId(), generalState, cityId);
            if (done <= 0) {
                return null;
            }
            if (campArmy.getPlayerId() > 0 && !campArmy.isPhantom) {
                NewBattleManager.getInstance().joinBattle(bat, campArmy.getPlayerId(), campArmy.getGeneralId());
            }
            dataGetter.getCityService().updateGNumAndSend(pgm.getLocationId(), cityId);
            bat.worldSceneLog.Indent().Indent().appendPlayerName(player.getPlayerName()).appendGeneralName(((General)dataGetter.getGeneralCache().get((Object)pgm.getGeneralId())).getName()).append("now state", 3).append("pre location", ((WorldCity)dataGetter.getWorldCityCache().get((Object)pgm.getLocationId())).getName()).append("now location", ((WorldCity)dataGetter.getWorldCityCache().get((Object)cityId)).getName()).append("pre state", pgm.getState()).append("pre locationId", pgm.getLocationId()).append("now locationId", cityId).appendPlayerId(player.getPlayerId()).append("pgm vId", pgm.getVId()).appendMethodName("PlayerGeneralMilitaryDao.attack").newLine();
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
    
    public CampArmy copyCampArmyFromPgm(final Player player, final PlayerGeneralMilitary pgm, final IDataGetter dataGetter) {
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
        final City city = dataGetter.getCityDao().read(defId);
        if (city == null) {
            ErrorSceneLog.getInstance().error("city builder error. defId = " + defId);
        }
        if (city.getForceId() == player.getForceId()) {
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
    
    public void dealBroadCastMsg(final boolean attWin, final IDataGetter dataGetter, final Battle bat, final BattleResult battleResult) {
        final WorldCity worldCity = (WorldCity)dataGetter.getWorldCityCache().get((Object)bat.defBaseInfo.getId());
        int winForceId = 0;
        if (winForceId != 1 && winForceId != 2 && winForceId != 3) {
            return;
        }
        int lossForceId = 0;
        if (attWin) {
            if (bat.getAttList().size() > 0) {
                winForceId = bat.getAttList().get(0).getCampArmy().getForceId();
            }
            else {
                winForceId = bat.attBaseInfo.forceId;
            }
            lossForceId = bat.defBaseInfo.forceId;
        }
        else {
            winForceId = bat.defBaseInfo.forceId;
            lossForceId = bat.attBaseInfo.forceId;
        }
        final int thirdForceId = 6 - winForceId - lossForceId;
        if (bat.defBaseInfo.forceId == 0) {
            if (attWin) {
                dataGetter.getBroadCastUtil().sendCaptureNeutralPlaceBroadCast(winForceId, worldCity.getName());
            }
        }
        else if (attWin) {
            dataGetter.getBroadCastUtil().sendPlaceChangeHandsBroadCast(thirdForceId, winForceId, worldCity.getName());
            if (worldCity.getTerrain() == 5 || worldCity.getTerrain() == 6) {
                dataGetter.getChatUtil().sendWinHostileCountryCityChat(winForceId, lossForceId, worldCity.getName(), !bat.isQuickConquer, bat.attList.get(0).getCampArmy().getPlayerName());
                dataGetter.getChatUtil().sendLoseCountryCityChat(lossForceId, winForceId, worldCity.getName(), !bat.isQuickConquer, bat.attList.get(0).getCampArmy().getPlayerName());
            }
            else {
                dataGetter.getBroadCastUtil().sendWinHCCommonPlaceBroadCast(winForceId, lossForceId, worldCity.getName());
                dataGetter.getBroadCastUtil().sendLoseCommonPlaceBroadCast(lossForceId, winForceId, worldCity.getName());
            }
        }
    }
    
    @Override
    public void dealNextNpc(final boolean attWin, final IDataGetter dataGetter, final Battle bat, final BattleResult battleResult) {
        try {
            this.dealBroadCastMsg(attWin, dataGetter, bat, battleResult);
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().appendErrorMsg("World war dealBroadCastMsg Exception").appendClassName("CityBuilder").appendMethodName("dealBroadCastMsg").append("battleId", bat.getBattleId()).append("attWin", attWin).flush();
            ErrorSceneLog.getInstance().error("CityBuilder dealNextNpc ", e);
        }
        final int cityId = bat.defBaseInfo.id;
        CityEventManager.getInstance().pushCityEventChangeInfo(cityId);
        if (attWin) {
            battleResult.cityName = ((WorldCity)dataGetter.getWorldCityCache().get((Object)cityId)).getName();
            battleResult.cType = 2;
            if (bat.isNpc) {
                dataGetter.getCityNpcLostDao().updateNpcLost(cityId, null);
            }
            final CampArmy currentWinCa = bat.attList.get(0).getCampArmy();
            final CityDefenceNpc cityDefenceNpc = new CityDefenceNpc();
            cityDefenceNpc.setCityId(cityId);
            if (currentWinCa.playerId == -2) {
                try {
                    final PlayerGeneralMilitary maxPgm = dataGetter.getPlayerGeneralMilitaryDao().getMaxLvMilitary(bat.attBaseInfo.forceId);
                    final Player maxPgmPlayer = dataGetter.getPlayerDao().read(maxPgm.getPlayerId());
                    final CampArmy maxPgmCa = this.copyCampArmyFromPgm(maxPgmPlayer, maxPgm, dataGetter);
                    cityDefenceNpc.setPlayerLv(maxPgmCa.playerLv);
                    cityDefenceNpc.setGeneralId(maxPgmCa.generalId);
                    cityDefenceNpc.setGeneralLv(maxPgmCa.generalLv);
                    cityDefenceNpc.setTroopId(maxPgmCa.troopId);
                    cityDefenceNpc.setStrength(maxPgmCa.strength);
                    cityDefenceNpc.setLeader(maxPgmCa.leader);
                    cityDefenceNpc.setAtt(maxPgmCa.attEffect);
                    cityDefenceNpc.setDef(maxPgmCa.defEffect);
                    cityDefenceNpc.setHp(maxPgmCa.maxForces);
                    cityDefenceNpc.setColumnNum(maxPgmCa.getColumn());
                    if (maxPgmCa.getAttDef_B() != null) {
                        cityDefenceNpc.setAttB(maxPgmCa.getAttDef_B().ATT_B);
                        cityDefenceNpc.setDefB(maxPgmCa.getAttDef_B().DEF_B);
                    }
                    else {
                        cityDefenceNpc.setAttB(0);
                        cityDefenceNpc.setDefB(0);
                    }
                    cityDefenceNpc.setTacticAtt(maxPgmCa.getTACTIC_ATT());
                    cityDefenceNpc.setTacticDef(maxPgmCa.getTACTIC_DEF());
                    dataGetter.getCityDefenceNpcDao().updateDefenceNpc(cityId, cityDefenceNpc);
                }
                catch (Exception e2) {
                    ErrorSceneLog.getInstance().error("CityBuilder dealNextNpc 3 ", e2);
                }
            }
            else {
                cityDefenceNpc.setPlayerLv(currentWinCa.playerLv);
                cityDefenceNpc.setGeneralId(currentWinCa.generalId);
                cityDefenceNpc.setGeneralLv(currentWinCa.generalLv);
                cityDefenceNpc.setTroopId(currentWinCa.troopId);
                cityDefenceNpc.setStrength(currentWinCa.strength);
                cityDefenceNpc.setLeader(currentWinCa.leader);
                cityDefenceNpc.setAtt(currentWinCa.attEffect);
                cityDefenceNpc.setDef(currentWinCa.defEffect);
                cityDefenceNpc.setHp(currentWinCa.maxForces);
                cityDefenceNpc.setColumnNum(currentWinCa.getColumn());
                if (currentWinCa.getAttDef_B() != null) {
                    cityDefenceNpc.setAttB(currentWinCa.getAttDef_B().ATT_B);
                    cityDefenceNpc.setDefB(currentWinCa.getAttDef_B().DEF_B);
                }
                else {
                    cityDefenceNpc.setAttB(0);
                    cityDefenceNpc.setDefB(0);
                }
                cityDefenceNpc.setTacticAtt(currentWinCa.getTACTIC_ATT());
                cityDefenceNpc.setTacticDef(currentWinCa.getTACTIC_DEF());
                dataGetter.getCityDefenceNpcDao().updateDefenceNpc(cityId, cityDefenceNpc);
            }
            bat.worldSceneLog.appendLogMsg("battle ended, modify city force, reset state.").append("attWin", attWin).appendCityName(((WorldCity)dataGetter.getWorldCityCache().get((Object)cityId)).getName()).append("now force", bat.attBaseInfo.getForceId()).append("att force", bat.attBaseInfo.forceId).append("def force", bat.defBaseInfo.forceId).append("cityId", cityId).appendClassName("CityBuilder").appendMethodName("dealNextNpc").newLine();
            final int winForceId = bat.attList.get(0).getCampArmy().getForceId();
            final int loseForceId = bat.defBaseInfo.forceId;
            if (winForceId == 101 || winForceId == 102 || winForceId == 103) {
                final long endTime = dataGetter.getRankService().getBarbarianNationTaskEnd();
                final int playerForceId = WorldCityCommon.manZuPlayerForceMap.get(winForceId);
                final boolean needBoBao = endTime >= System.currentTimeMillis() || dataGetter.getRankService().hasNationTasks(playerForceId) == 7;
                if (needBoBao) {
                    try {
                        final String barbarainName = bat.attList.get(0).getCampArmy().getPlayerName();
                        final String cityName = ((WorldCity)dataGetter.getWorldCityCache().get((Object)cityId)).getName();
                        dataGetter.getChatUtil().sendLostCityByBarbarainChat(loseForceId, barbarainName, cityName);
                    }
                    catch (Exception e5) {
                        ErrorSceneLog.getInstance().appendErrorMsg("ChatUtil.sendLostCityByBarbarainChat exception").appendClassName("cityBuilder").appendMethodName("dealNextNpc").flush();
                    }
                }
            }
            try {
                dataGetter.getCityService().changeForceIdAndState(cityId, winForceId, 0, bat.attList.get(0).getCampArmy().getPlayerId(), bat.attList.get(0).getCampArmy().getPlayerName());
            }
            catch (Exception e3) {
                final City city = dataGetter.getCityDao().read(cityId);
                bat.worldSceneLog.Indent().appendLogMsg("battle ended, att Win, modify city force and state exception.").appendCityName(((WorldCity)dataGetter.getWorldCityCache().get((Object)cityId)).getName()).append("now force", city.getForceId()).append("now state", city.getState()).appendCityId(cityId).newLine();
                ErrorSceneLog.getInstance().error("battle ended, att Win, modify city force and state exception.", e3);
            }
            for (final CampArmy campArmy : bat.attCamp) {
                if (campArmy.isUpdateDB() && campArmy.armyHpLoss < campArmy.armyHpOrg) {
                    this.dealKillrank(false, campArmy.armyHpKill, bat, dataGetter, campArmy.getPlayerId());
                    this.dealKillrank(true, campArmy.barbarainHpKill, bat, dataGetter, campArmy.getPlayerId());
                }
            }
            try {
                if (currentWinCa.playerId > 0) {
                    dataGetter.getRankService().updatePlayerOccupyCItyInfo(currentWinCa.playerId, currentWinCa.generalId, 1);
                    if (currentWinCa.getPlayerId() > 0) {
                        final PlayerInfo playerInfo = bat.getInBattlePlayers().get(currentWinCa.getPlayerId());
                        if (playerInfo != null) {
                            playerInfo.battleMode = 3;
                        }
                        else {
                            ErrorSceneLog.getInstance().appendErrorMsg("playerInfo is null").appendBattleId(bat.getBattleId()).appendPlayerId(currentWinCa.getPlayerId()).appendPlayerName(currentWinCa.getPlayerName()).appendClassName("CityBuilder").appendMethodName("dealNextNpc").flush();
                        }
                    }
                }
            }
            catch (Exception e3) {
                final City city = dataGetter.getCityDao().read(cityId);
                bat.worldSceneLog.Indent().appendLogMsg("battle ended, att Win, modify city force and state exception.   updatePlayerOccupyCItyInfo ").appendCityName(((WorldCity)dataGetter.getWorldCityCache().get((Object)cityId)).getName()).append("now force", city.getForceId()).append("now state", city.getState()).appendCityId(cityId).append("playerId", currentWinCa.playerId).append("generalId", currentWinCa.generalId).newLine();
            }
            this.dealOccupyCityJiFenPerDay(dataGetter, bat, currentWinCa, cityId);
        }
        else {
            if (bat.isNpc) {
                final WorldCity worldCity = (WorldCity)dataGetter.getWorldCityCache().get((Object)cityId);
                final StringBuilder sb = new StringBuilder();
                int serialNo = 0;
                Integer[] armiesId;
                for (int length = (armiesId = worldCity.getArmiesId()).length, i = 0; i < length; ++i) {
                    final int id = armiesId[i];
                    Builder.getCampArmyLost(dataGetter, sb, bat, id, serialNo);
                    ++serialNo;
                }
                sb.replace(sb.length() - 1, sb.length(), "");
                dataGetter.getCityNpcLostDao().updateNpcLost(cityId, sb.toString());
                dataGetter.getCityDao().updateHp(cityId, bat.defBaseInfo.num);
            }
            bat.worldSceneLog.appendLogMsg("battle ended, reset city state only.").append("attWin", attWin).append("cityId name", ((WorldCity)dataGetter.getWorldCityCache().get((Object)cityId)).getName()).append("att force", bat.attBaseInfo.forceId).append("def force", bat.defBaseInfo.forceId).append("cityId", cityId).appendClassName("CityBuilder").appendMethodName("dealNextNpc").newLine();
            try {
                dataGetter.getCityService().changeState(cityId, 0, false);
            }
            catch (Exception e4) {
                final City city2 = dataGetter.getCityDao().read(cityId);
                bat.worldSceneLog.appendLogMsg("battle ended, reset city state only exception.").appendCityId(cityId).appendCityName(((WorldCity)dataGetter.getWorldCityCache().get((Object)cityId)).getName()).append("now state", city2.getState()).newLine();
                ErrorSceneLog.getInstance().error("battle ended, reset city state only exception.", e4);
            }
            for (final CampArmy campArmy2 : bat.defCamp) {
                if (campArmy2.isUpdateDB() && campArmy2.armyHpLoss < campArmy2.armyHpOrg) {
                    this.dealKillrank(false, campArmy2.armyHpKill, bat, dataGetter, campArmy2.getPlayerId());
                    this.dealKillrank(true, campArmy2.barbarainHpKill, bat, dataGetter, campArmy2.getPlayerId());
                }
            }
        }
    }
    
    private void dealOccupyCityJiFenPerDay(final IDataGetter dataGetter, final Battle bat, final CampArmy currentWinCa, final int cityId) {
        for (final PlayerInfo playerInfo : bat.inBattlePlayers.values()) {
            try {
                if (!playerInfo.isAttSide) {
                    continue;
                }
                final Player player = dataGetter.getPlayerDao().read(playerInfo.playerId);
                if (player == null) {
                    continue;
                }
                if (player.getForceId() != currentWinCa.forceId) {
                    continue;
                }
                if (playerInfo.battleMode == 1) {
                    continue;
                }
                Integer occupyCityScore = WorldService.playerCityOccupyMap.get(playerInfo.playerId);
                if (occupyCityScore == null) {
                    occupyCityScore = 0;
                }
                final StringBuilder params = new StringBuilder();
                params.append(playerInfo.playerId).append("#").append(cityId).append("#");
                final BattleDrop battleDrop = new BattleDrop();
                int score = 0;
                switch (playerInfo.battleMode) {
                    case 0: {
                        params.append(3);
                        score = 1;
                        battleDrop.type = 27;
                        break;
                    }
                    case 2: {
                        params.append(2);
                        score = 2;
                        battleDrop.type = 26;
                        break;
                    }
                    case 3: {
                        params.append(1);
                        score = 5;
                        battleDrop.type = 25;
                        break;
                    }
                }
                if (occupyCityScore < 500) {
                    int addExp = score * dataGetter.getTechEffectCache().getTechEffect(playerInfo.playerId, 50);
                    if (addExp > 0) {
                        final AddExpInfo addExpInfo = dataGetter.getPlayerService().updateExpAndPlayerLevel(playerInfo.playerId, addExp, "\u5360\u9886\u57ce\u5e02\u989d\u5916\u589e\u52a0\u7ecf\u9a8c");
                        addExp = addExpInfo.addExp;
                        if (addExp > 0) {
                            final BattleDrop battleDrop2 = new BattleDrop();
                            battleDrop2.type = 1005;
                            battleDrop2.num = addExp;
                            playerInfo.addDrop(battleDrop2);
                        }
                    }
                }
                WorldService.playerCityOccupyMap.put(playerInfo.playerId, occupyCityScore + score);
                final char[] cs = dataGetter.getPlayerAttributeDao().getFunctionId(playerInfo.playerId).toCharArray();
                if (cs[63] == '1') {
                    battleDrop.num = 1;
                    playerInfo.addDrop(battleDrop);
                }
                Builder.timerLog.info("rankService.updateTodayScoreRank. params:" + params.toString() + "; " + player.getPlayerName());
                dataGetter.getJobService().addJob("rankService", "updateTodayScoreRank", params.toString(), System.currentTimeMillis(), false);
            }
            catch (Exception e) {
                ErrorSceneLog.getInstance().error("dealOccupyCityJiFenPerDay catch Exception. playerId:" + playerInfo.playerId, e);
            }
        }
    }
    
    @Override
    public void afterBat(final boolean attWin, final IDataGetter dataGetter, final Battle bat) {
        if (attWin) {
            final int winForceId = bat.getAttList().get(0).getCampArmy().getForceId();
            final int cityId = bat.getDefBaseInfo().getId();
            for (final CampArmy ca : bat.attCamp) {
                final int attPlayerId = ca.getPlayerId();
                if (winForceId != ca.getForceId()) {
                    final int attGeneralId = ca.getGeneralId();
                    dataGetter.getJobService().addJob("battleService", "battleReStart", getParams(attPlayerId, attGeneralId, cityId, ca.isPhantom, ca.getForceId()), System.currentTimeMillis() + 500L, false);
                    break;
                }
            }
        }
        else {
            final int winForceId = bat.getDefBaseInfo().getForceId();
            final int cityId = bat.getDefBaseInfo().getId();
            for (final CampArmy ca : bat.defCamp) {
                final int attPlayerId = ca.getPlayerId();
                if (winForceId != ca.getForceId()) {
                    final int attGeneralId = ca.getGeneralId();
                    dataGetter.getJobService().addJob("battleService", "battleReStart", getParams(attPlayerId, attGeneralId, cityId, ca.isPhantom, ca.getForceId()), System.currentTimeMillis() + 500L, false);
                    break;
                }
            }
        }
        final int cityId2 = bat.getDefBaseInfo().getId();
        final boolean hasGoldOrder = dataGetter.getBattleService().hasGoldOrderInCertainCity(cityId2);
        if (hasGoldOrder) {
            dataGetter.getBattleService().deleteAllGoldOrderInBattle(cityId2);
        }
        this.handleHzResult(attWin, dataGetter, bat);
    }
    
    private void handleHzResult(final boolean attWin, final IDataGetter dataGetter, final Battle bat) {
        try {
            final int cityId = bat.getDefBaseInfo().getId();
            final HuizhanHistory hh = dataGetter.getHuiZhanService().getTodayHuizhanBySate(2);
            if (hh != null && cityId == hh.getCityId()) {
                dataGetter.getHuiZhanService().updateHzStateById(3, hh.getVId());
                dataGetter.getHuizhanHistoryDao().updateHzEndTimeById(new Date(), hh.getVId());
                int hzWinnerForceId = 0;
                if (attWin) {
                    hzWinnerForceId = bat.getAttCamp().get(0).getForceId();
                    dataGetter.getHuiZhanService().addHzWinNumByForceId(hh.getAttForceId1());
                    dataGetter.getHuiZhanService().addHzWinNumByForceId(hh.getAttForceId2());
                }
                else {
                    hzWinnerForceId = hh.getDefForceId();
                    dataGetter.getHuiZhanService().addHzWinNumByForceId(hh.getDefForceId());
                }
                dataGetter.getHuizhanHistoryDao().updateWinnerByVid(hzWinnerForceId, hh.getVId());
                dataGetter.getHuiZhanService().pushHuiZhanTaskInfo(3, 0);
                dataGetter.getBattleService().refreshWorld();
                dataGetter.getHuiZhanService().pushHuiZhanIcon(false, 0);
                dataGetter.getHuiZhanService().resetTodayHuiZhan();
                dataGetter.getHuiZhanService().addHzTotalNum();
            }
        }
        catch (Exception e) {
            CityBuilder.errorLog.error("#className:cityBuilder#methodName:handleHzResult#EXCEPTION:", e);
        }
    }
    
    public static String getParams(final int playerId, final int generalId, final int cityId, final boolean isPhantom, final int attForceId) {
        final StringBuilder sb = new StringBuilder();
        sb.append(playerId).append("#").append(generalId).append("#").append(cityId).append("#").append(isPhantom ? 1 : 0).append("#").append(attForceId);
        return sb.toString();
    }
    
    @Override
    public void roundCaculateReward(final IDataGetter dataGetter, final Battle bat, final RoundInfo roundInfo) {
        FightRewardCoe frc = (FightRewardCoe)dataGetter.getFightRewardCoeCache().get((Object)this.getFightRewardCoeId(dataGetter, bat));
        if (frc == null) {
            BattleSceneLog.getInstance().debug("FightRewardCoe is null. battle type:" + bat.getBattleType());
            frc = new FightRewardCoe();
        }
        this.roundCaculateAttReward(dataGetter, frc, bat, roundInfo);
        if (!bat.isNpc) {
            this.roundCaculateDefReward(dataGetter, frc, bat, roundInfo);
        }
    }
    
    public void roundReduceTruePlayer(final IDataGetter dataGetter, final Battle bat, final CampArmy campArmyA, final CampArmy campArmyB, final boolean isBeKill, final int lostA, final int lostB) {
        final String param = String.valueOf(campArmyA.getPlayerId()) + "#" + campArmyB.getForceId() + "#" + lostB + "#" + bat.defBaseInfo.id;
        dataGetter.getJobService().addJob("rankService", "updatePRank", param, System.currentTimeMillis(), false);
        if (isBeKill) {
            if (campArmyB.playerId > 0 && !campArmyB.isPhantom) {
                final StringBuilder slaveParam = new StringBuilder();
                slaveParam.append(campArmyB.playerId).append("#").append(campArmyB.generalId).append("#").append(campArmyA.playerId).append("#").append(campArmyA.generalId).append("#").append(campArmyA.killGeneral).append("#").append(1);
                Builder.timerLog.info("slaveService.dealSlave. params:" + slaveParam.toString() + "; " + bat.getBattleId());
                dataGetter.getJobService().addJob("slaveService", "dealSlave", slaveParam.toString(), System.currentTimeMillis(), true);
            }
            this.dealKillrank(false, campArmyA.getArmyHpKill(), bat, dataGetter, campArmyA.getPlayerId());
            this.dealKillrank(true, campArmyA.barbarainHpKill, bat, dataGetter, campArmyA.getPlayerId());
            final int capitalId = WorldCityCommon.nationMainCityIdMap.get(campArmyA.getForceId());
            final int done = dataGetter.getPlayerGeneralMilitaryDao().updateLocationForceSetState1(campArmyA.getPlayerId(), campArmyA.generalId, capitalId, lostA, new Date());
            if (done != 1) {
                ErrorSceneLog.getInstance().appendErrorMsg("updateLocationForceSetState1 fail").appendBattleId(bat.getBattleId()).appendPlayerId(campArmyA.getPlayerId()).appendGeneralId(campArmyA.generalId).append("capitalId", capitalId).append("lostA", lostA).appendMethodName("roundReduceTruePlayer").flush();
            }
            bat.worldSceneLog.appendLogMsg("round be killed. quit GroupArmy").newLine().Indent().appendPlayerName(campArmyA.getPlayerName()).appendGeneralName(campArmyA.getGeneralName()).append("now state", 1).append("now location", ((WorldCity)dataGetter.getWorldCityCache().get((Object)capitalId)).getName()).appendPlayerId(campArmyA.getPlayerId()).appendGeneralId(campArmyA.getGeneralId()).append("now locationId", capitalId).appendMethodName("roundReduceTroopSingle").newLine();
            try {
                dataGetter.getCityService().updateGNumAndSend(bat.getDefBaseInfo().getId(), capitalId);
            }
            catch (Exception e) {
                ErrorSceneLog.getInstance().appendErrorMsg("updateGNumAndSend exception").append("playerId", campArmyA.getPlayerId()).append("generalId", campArmyA.generalId).appendClassName("CityService").appendMethodName("updateGNumAndSend").flush();
                ErrorSceneLog.getInstance().error("CityBuilder roundReduceTruePlayer 1 ", e);
            }
            try {
                dataGetter.getCityService().sendAttMoveInfo(campArmyA.getPlayerId(), campArmyA.generalId, bat.defBaseInfo.id, capitalId, campArmyA.getForceId(), "", campArmyA.getArmyHp(), true);
            }
            catch (Exception e) {
                ErrorSceneLog.getInstance().appendErrorMsg("sendAttMoveInfo exception").append("playerId", campArmyA.getPlayerId()).append("generalId", campArmyA.generalId).appendClassName("CityService").appendMethodName("sendAttMoveInfo").flush();
                ErrorSceneLog.getInstance().error("CityBuilder roundReduceTruePlayer 2", e);
            }
            try {
                final String cgm = dataGetter.getCityService().getColoredGeneralName(campArmyA.getGeneralId());
                dataGetter.getCityDataCache().fireCityMoveMessage(campArmyA.getPlayerId(), bat.defBaseInfo.id, capitalId, cgm);
            }
            catch (Exception e) {
                ErrorSceneLog.getInstance().error("CityBuilder roundReduceTruePlayer 3 ", e);
            }
            try {
                dataGetter.getGeneralService().sendGmForcesReduce(campArmyA.getPlayerId(), campArmyA.generalId, lostA, lostB, bat.inSceneSet.contains(campArmyA.getPlayerId()), campArmyB.playerName, campArmyB.generalName, campArmyB.quality);
                dataGetter.getGeneralService().sendGmUpdate(campArmyA.getPlayerId(), campArmyA.generalId, bat.inSceneSet.contains(campArmyA.getPlayerId()));
            }
            catch (Exception e) {
                ErrorSceneLog.getInstance().error("CityBuilder roundReduceTruePlayer 5", e);
            }
            dataGetter.getAutoBattleService().nextRoundAfterGeneralDead(campArmyA.getPlayerId(), campArmyA.generalId);
        }
        else {
            try {
                final int done2 = dataGetter.getPlayerGeneralMilitaryDao().consumeForces(campArmyA.getPlayerId(), campArmyA.generalId, lostA, new Date());
                if (done2 != 1) {
                    ErrorSceneLog.getInstance().appendErrorMsg("consumeForces fail").appendBattleId(bat.getBattleId()).appendPlayerId(campArmyA.getPlayerId()).appendGeneralId(campArmyA.generalId).append("lostA", lostA).appendMethodName("roundReduceTruePlayer").flush();
                }
                bat.worldSceneLog.appendLogMsg("round reduce force").newLine().Indent().appendPlayerName(campArmyA.playerName).appendGeneralName(campArmyA.generalName).append("reduce", lostA).appendClassName("CityBuilder").appendMethodName("roundReduceTroopSingle").newLine();
                dataGetter.getGeneralService().sendGmForcesReduce(campArmyA.getPlayerId(), campArmyA.generalId, lostA, lostB, bat.inSceneSet.contains(campArmyA.getPlayerId()), campArmyB.playerName, campArmyB.generalName, campArmyB.quality);
            }
            catch (Exception e2) {
                ErrorSceneLog.getInstance().error("CityBuilder roundReduceTruePlayer 4 ", e2);
            }
        }
    }
    
    public void roundReducePlayerPhantom(final IDataGetter dataGetter, final Battle bat, final CampArmy campArmyA, final CampArmy campArmyB, final boolean isBeKill, final int lostA, final int lostB) {
        final String param = String.valueOf(campArmyA.getPlayerId()) + "#" + campArmyB.getForceId() + "#" + lostB + "#" + bat.getDefBaseInfo().id;
        dataGetter.getJobService().addJob("rankService", "updatePRank", param, System.currentTimeMillis(), false);
        if (isBeKill) {
            if (campArmyB.playerId > 0 && !campArmyB.isPhantom) {
                final StringBuilder slaveParam = new StringBuilder();
                slaveParam.append(campArmyB.playerId).append("#").append(campArmyB.generalId).append("#").append(campArmyA.playerId).append("#").append(campArmyA.generalId).append("#").append(campArmyA.killGeneral).append("#").append(0);
                Builder.timerLog.info("slaveService.dealSlave. params:" + slaveParam.toString() + "; " + bat.getBattleId());
                dataGetter.getJobService().addJob("slaveService", "dealSlave", slaveParam.toString(), System.currentTimeMillis(), true);
            }
            this.dealKillrank(false, campArmyA.getArmyHpKill(), bat, dataGetter, campArmyA.getPlayerId());
            this.dealKillrank(true, campArmyA.barbarainHpKill, bat, dataGetter, campArmyA.getPlayerId());
            final int done = dataGetter.getPlayerGeneralMilitaryPhantomDao().deleteById(campArmyA.pgmVId);
            if (done != 1) {
                ErrorSceneLog.getInstance().appendErrorMsg("phanton delete failed.").appendBattleId(bat.getBattleId()).append("phantom vId", campArmyA.pgmVId).append("city", ((WorldCity)dataGetter.getWorldCityCache().get((Object)bat.defBaseInfo.id)).getName()).append("cityId", bat.defBaseInfo.id).appendClassName("CityBuilder").appendMethodName("roundReducePlayerPhantom");
            }
        }
        else {
            final int hp = campArmyA.armyHpOrg - campArmyA.armyHpLoss;
            final int done2 = dataGetter.getPlayerGeneralMilitaryPhantomDao().updateHp(campArmyA.pgmVId, hp);
            if (done2 != 1) {
                ErrorSceneLog.getInstance().appendErrorMsg("phanton updateHp failed.").appendBattleId(bat.getBattleId()).append("phantom vId", campArmyA.pgmVId).append("hp", hp).append("city", ((WorldCity)dataGetter.getWorldCityCache().get((Object)bat.defBaseInfo.id)).getName()).appendClassName("CityBuilder").appendMethodName("roundReducePlayerPhantom");
            }
        }
    }
    
    private void updateSaoDangManZuPercent(final IDataGetter dataGetter, final BarbarainPhantom barbarainPhantom) {
        try {
            if (barbarainPhantom.getNpcType() != 2) {
                return;
            }
            final Integer forceId = WorldCityCommon.manZuPlayerForceMap.get(barbarainPhantom.getForceId());
            if (forceId == null) {
                ErrorSceneLog.getInstance().appendErrorMsg("forceId is null").append("barbarainPhantom", barbarainPhantom.getVId()).append("barbarainPhantom.getForceId()", barbarainPhantom.getForceId()).appendClassName("CityBuilder").appendMethodName("updateSaoDangManZuPercent").flush();
                return;
            }
            final List<NationTask> nationTasks = dataGetter.getNationTaskDao().getListByForce(forceId);
            if (nationTasks == null || nationTasks.size() == 0 || nationTasks.size() > 1) {
                ErrorSceneLog.getInstance().appendErrorMsg("nationTasks is invalid").append("forceId", forceId).appendClassName("CityBuilder").appendMethodName("updateSaoDangManZuPercent").flush();
                return;
            }
            final NationTask nationTask = nationTasks.get(0);
            if (RankService.getTaskTypeById(nationTask.getNationTaskId()) != 7) {
                ErrorSceneLog.getInstance().appendErrorMsg("nationTask type is invalid").append("forceId", forceId).append("nationTask.getNationTaskId()", nationTask.getNationTaskId()).appendClassName("CityBuilder").appendMethodName("updateSaoDangManZuPercent").flush();
                return;
            }
            final String info = nationTask.getTaskRelateInfo();
            if (info == null) {
                ErrorSceneLog.getInstance().appendErrorMsg("info is null").append("forceId", forceId).append("nationTask", nationTask.getNationTaskId()).appendClassName("CityBuilder").appendMethodName("updateSaoDangManZuPercent").flush();
                return;
            }
            final String[] sArray = info.split("_");
            if (sArray.length != 2) {
                ErrorSceneLog.getInstance().appendErrorMsg("info is invalid").append("info", info).append("forceId", forceId).append("nationTask", nationTask.getNationTaskId()).appendClassName("CityBuilder").appendMethodName("updateSaoDangManZuPercent").flush();
                return;
            }
            long doneNum = 0L;
            long allNum = 0L;
            try {
                doneNum = Long.parseLong(sArray[0]);
                allNum = Long.parseLong(sArray[1]);
            }
            catch (Exception e2) {
                ErrorSceneLog.getInstance().appendErrorMsg("parseLong catch exception").append("info", info).append("forceId", forceId).append("nationTask", nationTask.getNationTaskId()).appendClassName("CityBuilder").appendMethodName("updateSaoDangManZuPercent").flush();
                return;
            }
            final Army army = (Army)dataGetter.getArmyCache().get((Object)barbarainPhantom.getArmyId());
            doneNum += army.getArmyHp();
            final String info2 = String.valueOf(doneNum) + "_" + allNum;
            dataGetter.getNationTaskDao().updateManZuSaoDangTaskRelateInfo(forceId, info2);
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("CityBuilder.updateSaoDangManZuPercent catch exception.", e);
        }
    }
    
    public void roundReduceBarPhantom(final IDataGetter dataGetter, final Battle bat, final CampArmy campArmyA, final CampArmy campArmyB, final boolean isBeKill, final int lostA, final int lostB) {
        if (isBeKill) {
            final BarbarainPhantom barbarainPhantom = dataGetter.getBarbarainPhantomDao().read(campArmyA.pgmVId);
            if (barbarainPhantom == null) {
                ErrorSceneLog.getInstance().appendErrorMsg("BarbarainPhantom is null.").appendBattleId(bat.getBattleId()).append("barbarainPhantom vId", campArmyA.pgmVId).appendClassName("CityBuilder").appendMethodName("roundReduceBarPhantom").flush();
                return;
            }
            this.updateSaoDangManZuPercent(dataGetter, barbarainPhantom);
            final int done = dataGetter.getBarbarainPhantomDao().deleteById(campArmyA.pgmVId);
            if (done != 1) {
                ErrorSceneLog.getInstance().appendErrorMsg("BarbarainPhantom delete failed.").appendBattleId(bat.getBattleId()).append("BarbarainPhantom vId", campArmyA.pgmVId).append("city", ((WorldCity)dataGetter.getWorldCityCache().get((Object)bat.defBaseInfo.id)).getName()).append("cityId", bat.defBaseInfo.id).appendClassName("CityBuilder").appendMethodName("roundReduceBarPhantom").flush();
            }
        }
        else {
            final int hp = campArmyA.armyHpOrg - campArmyA.armyHpLoss;
            final int done = dataGetter.getBarbarainPhantomDao().updateHpTacticVal(campArmyA.pgmVId, hp, campArmyA.tacticVal);
            if (done != 1) {
                ErrorSceneLog.getInstance().appendErrorMsg("BarbarainPhantom updateHpTacticVal failed.").appendBattleId(bat.getBattleId()).append("BarbarainPhantom vId", campArmyA.pgmVId).append("hp", hp).append("tacticVal", campArmyA.tacticVal).append("city", ((WorldCity)dataGetter.getWorldCityCache().get((Object)bat.defBaseInfo.id)).getName()).appendClassName("CityBuilder").appendMethodName("roundReducePlayerPhantom").flush();
            }
        }
        if (campArmyB != null && campArmyB.playerId > 0) {
            dataGetter.getIndividualTaskService().sendTaskMessage(new PlayerDto(campArmyB.playerId, campArmyB.forceId), lostA, "killmz");
        }
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
        if (campArmyA.isBarPhantom) {
            this.roundReduceBarPhantom(dataGetter, bat, campArmyA, campArmyB, isBeKill, lostA, lostB);
        }
        else if (campArmyA.isEA) {
            this.roundReduceExpeditionArmy(dataGetter, bat, campArmyA, campArmyB, isBeKill, lostA, lostB);
        }
        else if (campArmyA.isBarEA) {
            this.roundReduceBarExpeditionArmy(dataGetter, bat, campArmyA, campArmyB, isBeKill, lostA, lostB);
        }
        else if (campArmyA.isYellowTrubans) {
            this.roundReduceYellowTurbansArmy(dataGetter, bat, campArmyA, campArmyB, isBeKill, lostA, lostB);
        }
        else if (campArmyA.nationTaskEAType > 0) {
            this.roundReduceNationTaskExpeditionArmy(dataGetter, bat, campArmyA, campArmyB, isBeKill, lostA, lostB);
        }
        else if (campArmyA.npcType == 1) {
            this.roundReduceActivityNpc(dataGetter, bat, campArmyA, campArmyB, isBeKill, lostA, lostB);
        }
    }
    
    private void dealNpcSlave(final IDataGetter dataGetter, final Battle bat, final CampArmy campArmyA, final CampArmy campArmyB) {
        try {
            if (campArmyB.playerId < 0 || campArmyB.isPhantom) {
                return;
            }
            if (campArmyA.generalName == null) {
                ErrorSceneLog.getInstance().appendErrorMsg("campArmyA.generalName == null").appendBattleId(bat.getBattleId()).append("BarbarainPhantom vId", campArmyA.pgmVId).append("generalId", campArmyA.generalId).append("campArmyA.isBarPhantom", campArmyA.isBarPhantom).append("campArmyA.isEA", campArmyA.isEA).append("campArmyA.isBarEA", campArmyA.isBarEA).append("campArmyA.nationTaskEAType", campArmyA.nationTaskEAType).append("city", ((WorldCity)dataGetter.getWorldCityCache().get((Object)bat.defBaseInfo.id)).getName()).appendClassName("CityBuilder").appendMethodName("dealNpcSlave").flush();
                return;
            }
            if (campArmyA.generalLv == 0) {
                ErrorSceneLog.getInstance().appendErrorMsg("campArmyA.generalLv == 0").appendBattleId(bat.getBattleId()).append("BarbarainPhantom vId", campArmyA.pgmVId).append("generalId", campArmyA.generalId).append("campArmyA.isBarPhantom", campArmyA.isBarPhantom).append("campArmyA.isEA", campArmyA.isEA).append("campArmyA.isBarEA", campArmyA.isBarEA).append("campArmyA.nationTaskEAType", campArmyA.nationTaskEAType).append("city", ((WorldCity)dataGetter.getWorldCityCache().get((Object)bat.defBaseInfo.id)).getName()).appendClassName("CityBuilder").appendMethodName("dealNpcSlave").flush();
                return;
            }
            final StringBuilder slaveParam = new StringBuilder();
            slaveParam.append(campArmyB.playerId).append("#").append(campArmyB.generalId).append("#").append(campArmyA.playerId).append("#").append(campArmyA.generalId).append("#").append(campArmyA.killGeneral).append("#").append(2).append("#").append(campArmyA.forceId).append("#").append(campArmyA.generalName).append("#").append(campArmyA.generalLv).append("#");
            Builder.timerLog.info("slaveService.dealSlave. params:" + slaveParam.toString() + "; " + bat.getBattleId());
            dataGetter.getJobService().addJob("slaveService", "dealSlave", slaveParam.toString(), System.currentTimeMillis(), true);
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("CityBuilder.dealNpcSlave catch Exception", e);
        }
    }
    
    public void roundReduceBarExpeditionArmy(final IDataGetter dataGetter, final Battle bat, final CampArmy campArmyA, final CampArmy campArmyB, final boolean isBeKill, final int lostA, final int lostB) {
        if (isBeKill) {
            final int done = dataGetter.getBarbarainExpeditionArmyDao().deleteById(campArmyA.pgmVId);
            if (done != 1) {
                ErrorSceneLog.getInstance().appendErrorMsg("barExpeditionArmy delete failed.").appendBattleId(bat.getBattleId()).append("barExpeditionArmy vId", campArmyA.pgmVId).append("city", ((WorldCity)dataGetter.getWorldCityCache().get((Object)bat.defBaseInfo.id)).getName()).append("cityId", bat.defBaseInfo.id).appendClassName("CityBuilder").appendMethodName("roundReduceBarExpeditionArmy");
            }
        }
        else {
            final int hp = campArmyA.armyHpOrg - campArmyA.armyHpLoss;
            final int done2 = dataGetter.getBarbarainExpeditionArmyDao().updateHpAndTacticVal(campArmyA.pgmVId, hp, campArmyA.tacticVal);
            if (done2 != 1) {
                ErrorSceneLog.getInstance().appendErrorMsg("barExpeditionArmy updateHpTacticVal failed.").appendBattleId(bat.getBattleId()).append("barExpeditionArmy vId", campArmyA.pgmVId).append("hp", hp).append("tacticVal", campArmyA.tacticVal).append("city", ((WorldCity)dataGetter.getWorldCityCache().get((Object)bat.defBaseInfo.id)).getName()).appendClassName("CityBuilder").appendMethodName("roundReduceBarExpeditionArmy");
            }
        }
    }
    
    public void roundReduceActivityNpc(final IDataGetter dataGetter, final Battle bat, final CampArmy campArmyA, final CampArmy campArmyB, final boolean isBeKill, final int lostA, final int lostB) {
        if (isBeKill) {
            final int done = dataGetter.getActivityNpcDao().deleteById(campArmyA.pgmVId);
            if (done != 1) {
                ErrorSceneLog.getInstance().appendErrorMsg("ActivityNpc delete failed.").appendBattleId(bat.getBattleId()).append("ActivityNpc vId", campArmyA.pgmVId).append("city", ((WorldCity)dataGetter.getWorldCityCache().get((Object)bat.defBaseInfo.id)).getName()).append("cityId", bat.defBaseInfo.id).appendClassName("CityBuilder").appendMethodName("roundReduceActivityNpc");
            }
        }
        else {
            final int hp = campArmyA.armyHpOrg - campArmyA.armyHpLoss;
            final int done2 = dataGetter.getActivityNpcDao().updateHpAndTacticVal(campArmyA.pgmVId, hp, campArmyA.tacticVal);
            if (done2 != 1) {
                ErrorSceneLog.getInstance().appendErrorMsg("ActivityNpc updateHpTacticVal failed.").appendBattleId(bat.getBattleId()).append("ActivityNpc vId", campArmyA.pgmVId).append("hp", hp).append("tacticVal", campArmyA.tacticVal).append("city", ((WorldCity)dataGetter.getWorldCityCache().get((Object)bat.defBaseInfo.id)).getName()).appendClassName("CityBuilder").appendMethodName("roundReduceActivityNpc");
            }
        }
    }
    
    public void roundReduceExpeditionArmy(final IDataGetter dataGetter, final Battle bat, final CampArmy campArmyA, final CampArmy campArmyB, final boolean isBeKill, final int lostA, final int lostB) {
        if (isBeKill) {
            final int done = dataGetter.getExpeditionArmyDao().deleteById(campArmyA.pgmVId);
            if (done != 1) {
                ErrorSceneLog.getInstance().appendErrorMsg("ExpeditionArmy delete failed.").appendBattleId(bat.getBattleId()).append("ExpeditionArmy vId", campArmyA.pgmVId).append("city", ((WorldCity)dataGetter.getWorldCityCache().get((Object)bat.defBaseInfo.id)).getName()).append("cityId", bat.defBaseInfo.id).appendClassName("CityBuilder").appendMethodName("roundReduceExpeditionArmy");
            }
        }
        else {
            final int hp = campArmyA.armyHpOrg - campArmyA.armyHpLoss;
            final int done2 = dataGetter.getExpeditionArmyDao().updateHpAndTacticVal(campArmyA.pgmVId, hp, campArmyA.tacticVal);
            if (done2 != 1) {
                ErrorSceneLog.getInstance().appendErrorMsg("ExpeditionArmy updateHpTacticVal failed.").appendBattleId(bat.getBattleId()).append("ExpeditionArmy vId", campArmyA.pgmVId).append("hp", hp).append("tacticVal", campArmyA.tacticVal).append("city", ((WorldCity)dataGetter.getWorldCityCache().get((Object)bat.defBaseInfo.id)).getName()).appendClassName("CityBuilder").appendMethodName("roundReduceExpeditionArmy");
            }
        }
    }
    
    public void roundReduceYellowTurbansArmy(final IDataGetter dataGetter, final Battle bat, final CampArmy campArmyA, final CampArmy campArmyB, final boolean isKilled, final int lostA, final int lostB) {
        try {
            if (isKilled) {
                final int done = dataGetter.getYellowTurbansDao().deleteById(campArmyA.pgmVId);
                if (done != 1) {
                    ErrorSceneLog.getInstance().appendErrorMsg("Yellow Turbans delete failed.").appendBattleId(bat.getBattleId()).append("Yellow Turbans vId: ", campArmyA.pgmVId).append("city", ((WorldCity)dataGetter.getWorldCityCache().get((Object)bat.defBaseInfo.id)).getName()).append("cityId", bat.defBaseInfo.id).appendClassName("CityBuilder").appendMethodName("roundReduceYellowTurbansArmy");
                }
            }
            else {
                final int hp = campArmyA.armyHpOrg - campArmyA.armyHpLoss;
                final int done2 = dataGetter.getYellowTurbansDao().updateHpAndTacticVal(campArmyA.pgmVId, hp, campArmyA.tacticVal);
                if (done2 != 1) {
                    ErrorSceneLog.getInstance().appendErrorMsg("YellowTurbansArmy updateHpTacticVal failed.").appendBattleId(bat.getBattleId()).append("YellowTurbansArmy vId", campArmyA.pgmVId).append("hp", hp).append("tacticVal", campArmyA.tacticVal).append("city", ((WorldCity)dataGetter.getWorldCityCache().get((Object)bat.defBaseInfo.id)).getName()).appendClassName("CityBuilder").appendMethodName("roundReduceYellowTurbansArmy");
                }
            }
            if (campArmyB != null && campArmyB.playerId > 0) {
                dataGetter.getIndividualTaskService().sendTaskMessage(new PlayerDto(campArmyB.playerId, campArmyB.forceId), lostA, "killhj");
            }
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error(this, e);
        }
    }
    
    public void roundReduceNationTaskExpeditionArmy(final IDataGetter dataGetter, final Battle bat, final CampArmy campArmyA, final CampArmy campArmyB, final boolean isBeKill, final int lostA, final int lostB) {
        if (isBeKill) {
            final int done = dataGetter.getNationTaskExpeditionArmyDao().deleteById(campArmyA.pgmVId);
            if (done != 1) {
                ErrorSceneLog.getInstance().appendErrorMsg("NationTaskExpeditionArmy delete failed.").appendBattleId(bat.getBattleId()).append("NationTaskExpeditionArmy vId", campArmyA.pgmVId).append("city", ((WorldCity)dataGetter.getWorldCityCache().get((Object)bat.defBaseInfo.id)).getName()).append("cityId", bat.defBaseInfo.id).appendClassName("CityBuilder").appendMethodName("roundReduceNationTaskExpeditionArmy");
            }
        }
        else {
            final int hp = campArmyA.armyHpOrg - campArmyA.armyHpLoss;
            final int done2 = dataGetter.getNationTaskExpeditionArmyDao().updateHpAndTacticVal(campArmyA.pgmVId, hp, campArmyA.tacticVal);
            if (done2 != 1) {
                ErrorSceneLog.getInstance().appendErrorMsg("NationTaskExpeditionArmy updateHpTacticVal failed.").appendBattleId(bat.getBattleId()).append("NationTaskExpeditionArmy vId", campArmyA.pgmVId).append("hp", hp).append("tacticVal", campArmyA.tacticVal).append("city", ((WorldCity)dataGetter.getWorldCityCache().get((Object)bat.defBaseInfo.id)).getName()).appendClassName("CityBuilder").appendMethodName("roundReduceNationTaskExpeditionArmy");
            }
        }
        if (campArmyB != null && campArmyB.playerId > 0) {
            dataGetter.getIndividualTaskService().sendTaskMessage(new PlayerDto(campArmyB.playerId, campArmyB.forceId), lostA, "killnpc");
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
            if (campArmy.updateDB && campArmy.playerId > 0) {
                if (campArmy.isPhantom) {
                    this.roundReducePlayerPhantom(dataGetter, bat, campArmy, exeTacticCa, dead, reduce, exeTacticCaLost);
                }
                else {
                    this.roundReduceTruePlayer(dataGetter, bat, campArmy, exeTacticCa, dead, reduce, exeTacticCaLost);
                }
            }
            else if (dead) {
                this.dealNpcSlave(dataGetter, bat, campArmy, exeTacticCa);
            }
            if (campArmy.isBarPhantom) {
                this.roundReduceBarPhantom(dataGetter, bat, campArmy, exeTacticCa, dead, reduce, exeTacticCaLost);
            }
            else if (campArmy.isEA) {
                this.roundReduceExpeditionArmy(dataGetter, bat, campArmy, exeTacticCa, dead, reduce, exeTacticCaLost);
            }
            else if (campArmy.isBarEA) {
                this.roundReduceBarExpeditionArmy(dataGetter, bat, campArmy, exeTacticCa, dead, reduce, exeTacticCaLost);
            }
            else if (campArmy.isYellowTrubans) {
                this.roundReduceYellowTurbansArmy(dataGetter, bat, campArmy, exeTacticCa, dead, reduce, exeTacticCaLost);
            }
            else if (campArmy.nationTaskEAType > 0) {
                this.roundReduceNationTaskExpeditionArmy(dataGetter, bat, campArmy, exeTacticCa, dead, reduce, exeTacticCaLost);
            }
            else {
                if (campArmy.npcType != 1) {
                    continue;
                }
                this.roundReduceActivityNpc(dataGetter, bat, campArmy, exeTacticCa, dead, reduce, exeTacticCaLost);
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
            bat.worldSceneLog.appendLogMsg("battle ended. att restartRecruit").newLine();
            int i = 1;
            for (final CampArmy ca : bat.attCamp) {
                if (ca.isPhantom) {
                    if (ca.armyHpLoss > 0) {
                        dataGetter.getPlayerGeneralMilitaryPhantomDao().updateHp(ca.pgmVId, ca.armyHpOrg - ca.armyHpLoss);
                    }
                }
                else {
                    if (ca.getPlayerId() > 0) {
                        NewBattleManager.getInstance().quitBattle(bat, ca.getPlayerId(), ca.getGeneralId());
                    }
                    this.updateGeneralDB(dataGetter, bat, ca, attWin);
                }
                BattleSceneLog.getInstance().info("#batId:" + bat.getBattleId() + "_" + bat.getStartTime() + "#quit:win#side:att" + "#playerId:" + ca.getPlayerId() + ":" + ca.isPhantom + "#general:" + ca.getGeneralId() + "#attSize:" + (bat.attCamp.size() - i));
                ++i;
            }
        }
        else {
            bat.worldSceneLog.appendLogMsg("battle ended. def restartRecruit").newLine();
            int i = 1;
            for (final CampArmy ca : bat.defCamp) {
                if (ca.isPhantom) {
                    if (ca.armyHpLoss > 0) {
                        dataGetter.getPlayerGeneralMilitaryPhantomDao().updateHp(ca.pgmVId, ca.armyHpOrg - ca.armyHpLoss);
                    }
                }
                else {
                    if (ca.getPlayerId() > 0) {
                        NewBattleManager.getInstance().quitBattle(bat, ca.getPlayerId(), ca.getGeneralId());
                    }
                    this.updateGeneralDB(dataGetter, bat, ca, attWin);
                }
                BattleSceneLog.getInstance().info("#batId:" + bat.getBattleId() + "_" + bat.getStartTime() + "#quit:win#side:def" + "#playerId:" + ca.getPlayerId() + ":" + ca.isPhantom + "#general:" + ca.getGeneralId() + "#defSize:" + (bat.defCamp.size() - i));
                ++i;
            }
        }
        final List<PlayerGeneralMilitary> list = dataGetter.getPlayerGeneralMilitaryDao().getByLocationAndState3(bat.getDefBaseInfo().getId());
        if (list != null && list.size() > 0) {
            for (final PlayerGeneralMilitary pgm : list) {
                BattleSceneLog.getInstance().error("BATTLE_ERROR batId:" + bat.getBattleId() + "_" + bat.getStartTime() + "#playerId:" + pgm.getPlayerId() + "#general:" + pgm.getGeneralId());
            }
            dataGetter.getPlayerGeneralMilitaryDao().updateByLocationAndState3(bat.getDefBaseInfo().getId(), 1, new Date());
        }
        final int cityId = bat.defBaseInfo.id;
        dataGetter.getBarbarainPhantomDao().resetStateByLocationAndState(cityId, 3);
        dataGetter.getExpeditionArmyDao().resetStateByLocationAndState(cityId, 3);
        dataGetter.getBarbarainExpeditionArmyDao().resetStateByLocationAndState(cityId, 3);
        dataGetter.getNationTaskExpeditionArmyDao().resetStateByLocationAndState(cityId, 3);
        dataGetter.getYellowTurbansDao().resetStateByLocationAndState(cityId, 3);
        dataGetter.getActivityNpcDao().resetStateByLocationAndState(cityId, 3);
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
        bat.worldSceneLog.appendLogMsg("quit. restartRecruit, quit GroupArmy").newLine().Indent().appendPlayerName(ca.getPlayerName()).appendGeneralName(ca.getGeneralName()).append("state", ca.isInRecruit ? 1 : 0).appendPlayerId(ca.getPlayerId()).appendGeneralId(ca.getGeneralId()).newLine();
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
    
    public static int getMaxHp(final IDataGetter dataGetter, final int cityId) {
        final City city = dataGetter.getCityDao().read(cityId);
        if (city.getForceId() == 0) {
            return city.getHpMax();
        }
        return Integer.MIN_VALUE;
    }
    
    public static Tuple<Integer, Integer> getHpMaxHp(final IDataGetter dataGetter, final int cityId) {
        final Tuple<Integer, Integer> tuple = new Tuple();
        final WorldCity worldCity = (WorldCity)dataGetter.getWorldCityCache().get((Object)cityId);
        final Integer[] defNpcs = worldCity.getArmiesId();
        if (defNpcs == null) {
            tuple.right = 0;
            tuple.left = 0;
            return tuple;
        }
        int allNum = 0;
        for (int i = 0; i < defNpcs.length; ++i) {
            final Army armyCach = (Army)dataGetter.getArmyCache().get((Object)defNpcs[i]);
            allNum += armyCach.getArmyHp();
        }
        tuple.right = allNum;
        int lostTotal = 0;
        final CityNpcLost cityNpcLost = dataGetter.getCityNpcLostDao().read(cityId);
        if (cityNpcLost != null && cityNpcLost.getNpcLost() != null) {
            final String[] losts = cityNpcLost.getNpcLost().split(";");
            String[] array;
            for (int length = (array = losts).length, j = 0; j < length; ++j) {
                final String s = array[j];
                lostTotal += Integer.parseInt(s);
            }
        }
        tuple.left = allNum - lostTotal;
        if (tuple.left < 0) {
            tuple.left = 0;
            ErrorSceneLog.getInstance().appendErrorMsg("lostTotal bigger than allNum.").appendClassName("CituBuilder").appendMethodName("getHpMaxHp").append("cityId", cityId).append("allNum", allNum).append("lostTotal", lostTotal).flush();
        }
        return tuple;
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
    public void setSurroundState(final IDataGetter dataGetter, final Battle bat) {
        final City city = dataGetter.getCityDao().read(bat.defBaseInfo.getId());
        bat.setSurround(city.getTitle());
    }
    
    @Override
    public CampArmy copyArmyformBarPhantom(final IDataGetter dataGetter, final Barbarain barbarain, final Battle bat, final BarbarainPhantom barPhantom, final int batSide) {
        if (barPhantom.getNpcType() != 1) {
            ErrorSceneLog.getInstance().appendErrorMsg("barPhantom.getNpcType() error").appendBattleId(bat.battleId).append("barPhantom.getNpcType()", barPhantom.getNpcType()).append("barPhantom", barPhantom.getVId()).appendClassName("CityBuilder").appendMethodName("copyArmyformBarPhantom").flush();
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
        campArmy.setPlayerLv(barbarain.getLv());
        campArmy.setPlayerId(-4);
        campArmy.setForceId(barPhantom.getForceId());
        String playerName = null;
        switch (barPhantom.getForceId()) {
            case 101: {
                playerName = barbarain.getWeiIName();
                break;
            }
            case 102: {
                playerName = barbarain.getShuIName();
                break;
            }
            case 103: {
                playerName = barbarain.getWuIName();
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
        campArmy.setGeneralLv(barbarain.getLv());
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
    public CampArmy copyArmyformBarPhantom2(final IDataGetter dataGetter, final KtSdmzS ktSdmzS, final Battle bat, final BarbarainPhantom barPhantom, final int batSide) {
        if (barPhantom.getNpcType() != 2) {
            ErrorSceneLog.getInstance().appendErrorMsg("barPhantom.getNpcType() error").appendBattleId(bat.battleId).append("barPhantom.getNpcType()", barPhantom.getNpcType()).append("barPhantom", barPhantom.getVId()).appendClassName("CityBuilder").appendMethodName("copyArmyformBarPhantom2").flush();
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
                playerName = ktSdmzS.getWeiName();
                break;
            }
            case 102: {
                playerName = ktSdmzS.getShuName();
                break;
            }
            case 103: {
                playerName = ktSdmzS.getWuName();
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
    public CampArmy copyArmyformBarPhantom4(final IDataGetter dataGetter, final Battle bat, final BarbarainPhantom barPhantom, final int batSide) {
        if (barPhantom.getNpcType() != 4) {
            ErrorSceneLog.getInstance().appendErrorMsg("barPhantom.getNpcType() error").appendBattleId(bat.battleId).append("barPhantom.getNpcType()", barPhantom.getNpcType()).append("barPhantom", barPhantom.getVId()).appendClassName("CityBuilder").appendMethodName("copyArmyformBarPhantom2").flush();
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
    public CampArmy copyArmyformBarPhantom3(final IDataGetter dataGetter, final Barbarain barbarain, final Battle bat, final BarbarainPhantom barPhantom, final int batSide) {
        if (barPhantom.getNpcType() != 3) {
            ErrorSceneLog.getInstance().appendErrorMsg("barPhantom.getNpcType() error").appendBattleId(bat.battleId).append("barPhantom.getNpcType()", barPhantom.getNpcType()).append("barPhantom", barPhantom.getVId()).appendClassName("CityBuilder").appendMethodName("copyArmyformBarPhantom2").flush();
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
                playerName = barbarain.getWeiName();
                break;
            }
            case 102: {
                playerName = barbarain.getShuName();
                break;
            }
            case 103: {
                playerName = barbarain.getWuName();
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
    public CampArmy copyArmyfromBarPhantom4(final IDataGetter dataGetter, final Battle battle, final BarbarainPhantom barPhantom, final int battleSide) {
        if (barPhantom.getNpcType() != 4) {
            ErrorSceneLog.getInstance().appendErrorMsg("barPhantom.getNpcType() error").appendBattleId(battle.battleId).append("barPhantom.getNpcType()", barPhantom.getNpcType()).append("barPhantom", barPhantom.getVId()).appendClassName("CityBuilder").appendMethodName("copyArmyfromBarPhantom4").flush();
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
        campArmy.setId(battle.campNum.getAndIncrement());
        campArmy.setPgmVId(barPhantom.getVId());
        campArmy.setArmyName(general.getName());
        campArmy.setPlayerLv(army.getGeneralLv());
        campArmy.setPlayerId(-4);
        campArmy.setForceId(barPhantom.getForceId());
        campArmy.setPlayerName(barPhantom.getName());
        getTerrainValue(battle.terrainVal, troop, battleSide, campArmy);
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
        if (battleSide == 1) {
            campArmy.setStrategies(troop.getStrategyMap().get(battle.terrainVal));
        }
        else {
            campArmy.setStrategies(troop.getStrategyDefMap().get(battle.terrainVal));
        }
        return campArmy;
    }
    
    @Override
    public CampArmy copyArmyformExpeditionArmy(final IDataGetter dataGetter, final EfLv eflv, final Battle bat, final ExpeditionArmy expeditionArmy, final int batSide) {
        final CampArmy campArmy = new CampArmy();
        final int armyId = expeditionArmy.getArmyId();
        final Army army = (Army)dataGetter.getArmyCache().get((Object)armyId);
        final General general = (General)dataGetter.getGeneralCache().get((Object)armyId);
        final SpecialGeneral sg = new SpecialGeneral(general.getGeneralSpecialInfo().generalType, general.getGeneralSpecialInfo().param, general.getGeneralSpecialInfo().param2);
        if (general.getGeneralSpecialInfo().generalType == 2) {
            sg.param = ((WorldCity)dataGetter.getWorldCityCache().get((Object)bat.getDefBaseInfo().getId())).getDistance(expeditionArmy.getForceId());
        }
        campArmy.setSpecialGeneral(sg);
        campArmy.isEA = true;
        campArmy.updateDB = false;
        campArmy.setId(bat.campNum.getAndIncrement());
        campArmy.setPgmVId(expeditionArmy.getVId());
        campArmy.setArmyName(general.getName());
        campArmy.setPlayerLv(eflv.getLv());
        campArmy.setPlayerId(-5);
        campArmy.setForceId(expeditionArmy.getForceId());
        String playerName = null;
        switch (expeditionArmy.getForceId()) {
            case 1: {
                playerName = eflv.getWeiName();
                break;
            }
            case 2: {
                playerName = eflv.getShuName();
                break;
            }
            case 3: {
                playerName = eflv.getWuName();
                break;
            }
        }
        campArmy.setPlayerName(playerName);
        final Troop troop = (Troop)dataGetter.getTroopCache().get((Object)general.getTroop());
        getTerrainValue(bat.terrainVal, troop, batSide, campArmy);
        campArmy.setTroopId(troop.getId());
        campArmy.setTroopSerial(troop.getSerial());
        campArmy.setTroopType(troop.getType());
        campArmy.setTroopName(troop.getName());
        campArmy.setTroopDropType(BattleDrop.getDropType(troop.getDrop()));
        campArmy.setTroopDrop(troop.getTroopDrop());
        campArmy.setGeneralId(general.getId());
        campArmy.setGeneralLv(eflv.getLv());
        campArmy.setGeneralName(general.getName());
        campArmy.setGeneralPic(general.getPic());
        campArmy.setQuality(general.getQuality());
        campArmy.setTacicId(general.getTacticId());
        campArmy.setStrength(general.getStrength());
        campArmy.setLeader(general.getLeader());
        int forces = expeditionArmy.getHp();
        final int remainder = forces % 3;
        forces -= remainder;
        campArmy.setArmyHp(forces);
        campArmy.setArmyHpOrg(forces);
        campArmy.setAttEffect(army.getAtt());
        campArmy.setDefEffect(army.getDef());
        campArmy.setBdEffect(army.getBd());
        campArmy.setMaxForces(army.getArmyHp());
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
        campArmy.setTacticVal(expeditionArmy.getTacticval());
        if (batSide == 1) {
            campArmy.setStrategies(troop.getStrategyMap().get(bat.terrainVal));
        }
        else {
            campArmy.setStrategies(troop.getStrategyDefMap().get(bat.terrainVal));
        }
        return campArmy;
    }
    
    @Override
    public CampArmy copyArmyFromNationTaskExpeditionArmy(final IDataGetter dataGetter, final EfLv eflv, final Battle bat, final NationTaskExpeditionArmy activityNpc, final int batSide) {
        final CampArmy campArmy = new CampArmy();
        final int armyId = activityNpc.getArmyId();
        final Army army = (Army)dataGetter.getArmyCache().get((Object)armyId);
        final General general = (General)dataGetter.getGeneralCache().get((Object)armyId);
        final SpecialGeneral sg = new SpecialGeneral(general.getGeneralSpecialInfo().generalType, general.getGeneralSpecialInfo().param, general.getGeneralSpecialInfo().param2);
        if (general.getGeneralSpecialInfo().generalType == 2) {
            sg.param = ((WorldCity)dataGetter.getWorldCityCache().get((Object)bat.getDefBaseInfo().getId())).getDistance(activityNpc.getForceId());
        }
        campArmy.setSpecialGeneral(sg);
        if (activityNpc.getMoveLine() != null) {
            campArmy.nationTaskEAType = 1;
        }
        else {
            campArmy.nationTaskEAType = 2;
        }
        campArmy.updateDB = false;
        campArmy.setId(bat.campNum.getAndIncrement());
        campArmy.setPgmVId(activityNpc.getVId());
        campArmy.setArmyName(general.getName());
        campArmy.setPlayerLv(eflv.getLv());
        campArmy.setPlayerId(-5);
        campArmy.setForceId(activityNpc.getForceId());
        String playerName = null;
        if (activityNpc.getNpcType() == 1) {
            switch (activityNpc.getForceId()) {
                case 1: {
                    if (activityNpc.getMoveLine() != null) {
                        playerName = eflv.getWeiAttName();
                        break;
                    }
                    playerName = eflv.getWeiDefName();
                    break;
                }
                case 2: {
                    if (activityNpc.getMoveLine() != null) {
                        playerName = eflv.getShuAttName();
                        break;
                    }
                    playerName = eflv.getShuDefName();
                    break;
                }
                case 3: {
                    if (activityNpc.getMoveLine() != null) {
                        playerName = eflv.getWuAttName();
                        break;
                    }
                    playerName = eflv.getWuDefName();
                    break;
                }
            }
        }
        else if (activityNpc.getNpcType() == 2) {
            playerName = LocalMessages.NATION_TASK_ARMY_NAME_2;
        }
        campArmy.setPlayerName(playerName);
        final Troop troop = (Troop)dataGetter.getTroopCache().get((Object)general.getTroop());
        getTerrainValue(bat.terrainVal, troop, batSide, campArmy);
        campArmy.setTroopId(troop.getId());
        campArmy.setTroopSerial(troop.getSerial());
        campArmy.setTroopType(troop.getType());
        campArmy.setTroopName(troop.getName());
        campArmy.setTroopDropType(BattleDrop.getDropType(troop.getDrop()));
        campArmy.setTroopDrop(troop.getTroopDrop());
        campArmy.setGeneralId(general.getId());
        campArmy.setGeneralLv(eflv.getLv());
        campArmy.setGeneralName(general.getName());
        campArmy.setGeneralPic(general.getPic());
        campArmy.setQuality(general.getQuality());
        campArmy.setTacicId(general.getTacticId());
        campArmy.setStrength(general.getStrength());
        campArmy.setLeader(general.getLeader());
        int forces = activityNpc.getHp();
        final int remainder = forces % 3;
        forces -= remainder;
        campArmy.setArmyHp(forces);
        campArmy.setArmyHpOrg(forces);
        campArmy.setAttEffect(army.getAtt());
        campArmy.setDefEffect(army.getDef());
        campArmy.setBdEffect(army.getBd());
        campArmy.setMaxForces(army.getArmyHp());
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
        campArmy.setTacticVal(activityNpc.getTacticval());
        if (batSide == 1) {
            campArmy.setStrategies(troop.getStrategyMap().get(bat.terrainVal));
        }
        else {
            campArmy.setStrategies(troop.getStrategyDefMap().get(bat.terrainVal));
        }
        return campArmy;
    }
    
    @Override
    public CampArmy copyArmyFromActivityNpc(final IDataGetter dataGetter, final Object table, final Battle bat, final ActivityNpc activityNpc, final int battleSide) {
        final CampArmy campArmy = new CampArmy();
        final int armyId = activityNpc.getArmyId();
        final Army army = (Army)dataGetter.getArmyCache().get((Object)armyId);
        final General general = (General)dataGetter.getGeneralCache().get((Object)armyId);
        final SpecialGeneral sg = new SpecialGeneral(general.getGeneralSpecialInfo().generalType, general.getGeneralSpecialInfo().param, general.getGeneralSpecialInfo().param2);
        if (general.getGeneralSpecialInfo().generalType == 2) {
            sg.param = ((WorldCity)dataGetter.getWorldCityCache().get((Object)bat.getDefBaseInfo().getId())).getDistance(activityNpc.getForceId());
        }
        campArmy.setSpecialGeneral(sg);
        campArmy.npcType = 1;
        campArmy.updateDB = false;
        campArmy.setId(bat.campNum.getAndIncrement());
        campArmy.setPgmVId(activityNpc.getVId());
        campArmy.setArmyName(general.getName());
        campArmy.setPlayerLv(army.getGeneralLv());
        campArmy.setPlayerId(-5);
        campArmy.setForceId(activityNpc.getForceId());
        campArmy.setPlayerName(general.getName());
        final Troop troop = (Troop)dataGetter.getTroopCache().get((Object)general.getTroop());
        getTerrainValue(bat.terrainVal, troop, battleSide, campArmy);
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
        int forces = activityNpc.getHp();
        final int remainder = forces % 3;
        forces -= remainder;
        campArmy.setArmyHp(forces);
        campArmy.setArmyHpOrg(forces);
        campArmy.setAttEffect(army.getAtt());
        campArmy.setDefEffect(army.getDef());
        campArmy.setBdEffect(army.getBd());
        campArmy.setMaxForces(army.getArmyHp());
        int column = forces / army.getTroopHp();
        if (column <= 0) {
            column = 1;
        }
        campArmy.setColumn(column);
        campArmy.setTroopHp(army.getTroopHp());
        campArmy.setAttDef_B(new AttDef_B());
        campArmy.getAttDef_B().ATT_B = 9999;
        campArmy.setTACTIC_ATT(campArmy.getAttDef_B().DEF_B = 9999);
        campArmy.setTACTIC_DEF(9999);
        campArmy.setTacticVal(activityNpc.getTacticval());
        if (battleSide == 1) {
            campArmy.setStrategies(troop.getStrategyMap().get(bat.terrainVal));
        }
        else {
            campArmy.setStrategies(troop.getStrategyDefMap().get(bat.terrainVal));
        }
        return campArmy;
    }
    
    @Override
    public CampArmy copyArmyformBarExpeditionArmy(final IDataGetter dataGetter, final WorldPaidB worldPaidB, final Battle bat, final BarbarainExpeditionArmy barbarainExpeditionArmy, final int batSide) {
        final CampArmy campArmy = new CampArmy();
        final int armyId = barbarainExpeditionArmy.getArmyId();
        final Army army = (Army)dataGetter.getArmyCache().get((Object)armyId);
        final General general = (General)dataGetter.getGeneralCache().get((Object)armyId);
        final SpecialGeneral sg = new SpecialGeneral(general.getGeneralSpecialInfo().generalType, general.getGeneralSpecialInfo().param, general.getGeneralSpecialInfo().param2);
        campArmy.setSpecialGeneral(sg);
        final Troop troop = (Troop)dataGetter.getTroopCache().get((Object)general.getTroop());
        campArmy.isBarEA = true;
        campArmy.updateDB = false;
        campArmy.setId(bat.campNum.getAndIncrement());
        campArmy.setPgmVId(barbarainExpeditionArmy.getVId());
        campArmy.setArmyName(general.getName());
        campArmy.setPlayerLv(worldPaidB.getLv());
        campArmy.setPlayerId(-6);
        campArmy.setForceId(barbarainExpeditionArmy.getForceId());
        String playerName = null;
        switch (barbarainExpeditionArmy.getForceId()) {
            case 101: {
                playerName = worldPaidB.getNameWei();
                break;
            }
            case 102: {
                playerName = worldPaidB.getNameShu();
                break;
            }
            case 103: {
                playerName = worldPaidB.getNameWu();
                break;
            }
            default: {
                ErrorSceneLog.getInstance().appendErrorMsg("playerName is null").append("ForceId", barbarainExpeditionArmy.getForceId()).append("worldPaidB", worldPaidB.getId()).appendMethodName("copyArmyformBarExpeditionArmy").appendClassName("CityBuilder").flush();
                break;
            }
        }
        campArmy.setPlayerName(playerName);
        getTerrainValue(bat.terrainVal, troop, batSide, campArmy);
        campArmy.setTroopId(troop.getId());
        campArmy.setTroopSerial(troop.getSerial());
        campArmy.setTroopType(troop.getType());
        campArmy.setTroopName(troop.getName());
        campArmy.setGeneralId(general.getId());
        campArmy.setGeneralLv(worldPaidB.getLv());
        campArmy.setGeneralName(general.getName());
        campArmy.setGeneralPic(general.getPic());
        campArmy.setQuality(general.getQuality());
        campArmy.setTacicId(general.getTacticId());
        campArmy.setStrength(general.getStrength());
        campArmy.setLeader(general.getLeader());
        int forces = barbarainExpeditionArmy.getHp();
        final int remainder = forces % 3;
        forces -= remainder;
        campArmy.setArmyHp(forces);
        campArmy.setArmyHpOrg(forces);
        campArmy.setAttEffect(army.getAtt());
        campArmy.setDefEffect(army.getDef());
        campArmy.setBdEffect(army.getBd());
        campArmy.setMaxForces(army.getArmyHp());
        int column = army.getArmyHp() / army.getTroopHp();
        if (column <= 0) {
            column = 1;
        }
        campArmy.setColumn(column);
        campArmy.setTroopHp(army.getTroopHp());
        campArmy.setAttDef_B(new AttDef_B());
        campArmy.getAttDef_B().ATT_B = 0;
        campArmy.setTACTIC_ATT(campArmy.getAttDef_B().DEF_B = 0);
        campArmy.setTACTIC_DEF(0);
        campArmy.setTacticVal(barbarainExpeditionArmy.getTacticval());
        if (batSide == 1) {
            campArmy.setStrategies(troop.getStrategyMap().get(bat.terrainVal));
        }
        else {
            campArmy.setStrategies(troop.getStrategyDefMap().get(bat.terrainVal));
        }
        return campArmy;
    }
    
    private CampArmy[] chooseEA_CampArmy(final LinkedList<CampArmy> aaCamp, final LinkedList<CampArmy> bbCamp) {
        final CampArmy[] cas = new CampArmy[2];
        final int idx = 9;
        if (aaCamp.size() >= 10 && aaCamp.get(idx).nationTaskEAType == 1) {
            cas[0] = aaCamp.get(idx);
        }
        else {
            for (int i = aaCamp.size() - 1; i >= 3; --i) {
                final CampArmy ca = aaCamp.get(i);
                if (!ca.onQueues && ca.nationTaskEAType == 1) {
                    cas[0] = ca;
                    break;
                }
            }
        }
        if (cas[0] != null) {
            if (bbCamp.size() >= 10) {
                cas[1] = bbCamp.get(idx);
            }
            else {
                for (int i = bbCamp.size() - 1; i >= 3; --i) {
                    final CampArmy ca = bbCamp.get(i);
                    if (!ca.onQueues) {
                        cas[1] = ca;
                        break;
                    }
                }
            }
        }
        return cas;
    }
    
    private void pkEA(final IDataGetter dataGetter, final CampArmy[] cas, final Battle battle, final RoundInfo roundInfo) {
        if (cas[0] != null && cas[1] != null) {
            roundInfo.needPushReport13 = true;
            battle.attCamp.remove(cas[0]);
            battle.defCamp.remove(cas[1]);
            battle.attBaseInfo.setAllNum(battle.attBaseInfo.getAllNum() - cas[0].armyHp);
            battle.attBaseInfo.setNum(battle.attBaseInfo.getNum() - cas[0].armyHp);
            battle.defBaseInfo.setAllNum(battle.defBaseInfo.getAllNum() - cas[1].armyHp);
            battle.defBaseInfo.setNum(battle.defBaseInfo.getNum() - cas[1].armyHp);
            dataGetter.getBattleService().createOneToOneBattle(-1, cas, battle, 3, 0);
        }
    }
    
    private void conquerEA_PK(final IDataGetter dataGetter, final Battle battle, final RoundInfo roundInfo) {
        final CampArmy[] cas1 = this.chooseEA_CampArmy(battle.attCamp, battle.defCamp);
        this.pkEA(dataGetter, cas1, battle, roundInfo);
        final CampArmy[] cas2 = this.chooseEA_CampArmy(battle.defCamp, battle.attCamp);
        final CampArmy ca = cas2[0];
        cas2[0] = cas2[1];
        cas2[1] = ca;
        this.pkEA(dataGetter, cas2, battle, roundInfo);
    }
    
    @Override
    public void systemSinglePK(final IDataGetter dataGetter, final Battle battle, final RoundInfo roundInfo) {
        try {
            final boolean isNTYellowTurbans = dataGetter.getBattleService().isNTYellowTurbansXiangYangDoing(battle.getDefBaseInfo().getId());
            if (isNTYellowTurbans) {
                return;
            }
            this.conquerEA_PK(dataGetter, battle, roundInfo);
            final int attSize = battle.attCamp.size();
            final int defSize = battle.defCamp.size();
            final int sizeLimit = 50;
            if (attSize <= sizeLimit || defSize <= sizeLimit) {
                return;
            }
            if (WebUtil.nextDouble() > 0.16666666666666666) {
                return;
            }
            for (int pkCount = (int)Math.ceil((battle.attCamp.size() + battle.defCamp.size()) / 150.0), i = 0; i < pkCount; ++i) {
                final CampArmy[] cas = new CampArmy[2];
                final int attChooseId = battle.attCamp.size() - 1;
                final int defChooseId = battle.defCamp.size() - 1;
                cas[0] = battle.attCamp.get(attChooseId);
                cas[1] = battle.defCamp.get(defChooseId);
                if (!cas[0].onQueues) {
                    if (!cas[1].onQueues) {
                        battle.attCamp.remove(cas[0]);
                        battle.defCamp.remove(cas[1]);
                        battle.attBaseInfo.setAllNum(battle.attBaseInfo.getAllNum() - cas[0].armyHp);
                        battle.attBaseInfo.setNum(battle.attBaseInfo.getNum() - cas[0].armyHp);
                        battle.defBaseInfo.setAllNum(battle.defBaseInfo.getAllNum() - cas[1].armyHp);
                        battle.defBaseInfo.setNum(battle.defBaseInfo.getNum() - cas[1].armyHp);
                        dataGetter.getBattleService().createOneToOneBattle(-1, cas, battle, 3, 0);
                    }
                }
            }
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("CityBuilder.systemSinglePK catch Exception", e);
        }
    }
    
    @Override
    public int getViewPlayer7ReportResult(final boolean attWin, final Battle bat, final Player player) {
        final int defFrceId = bat.defBaseInfo.forceId;
        int result = 2;
        if ((attWin && player.getForceId() != defFrceId) || (!attWin && player.getForceId() == defFrceId)) {
            result = 1;
        }
        return result;
    }
    
    @Override
    public CampArmy copyArmyfromNationTaskYellowTurbans(final IDataGetter dataGetter, final Battle battle, final YellowTurbans yellowTurbans, final int battleSide) {
        final CampArmy campArmy = new CampArmy();
        final int armyId = yellowTurbans.getArmyId();
        final Army army = (Army)dataGetter.getArmyCache().get((Object)armyId);
        final General general = (General)dataGetter.getGeneralCache().get((Object)armyId);
        campArmy.setSpecialGeneral(new SpecialGeneral(general.getGeneralSpecialInfo().generalType, general.getGeneralSpecialInfo().param, general.getGeneralSpecialInfo().param2));
        final Troop troop = (Troop)dataGetter.getTroopCache().get((Object)general.getTroop());
        campArmy.isYellowTrubans = true;
        campArmy.updateDB = false;
        campArmy.setId(battle.campNum.getAndIncrement());
        campArmy.setPgmVId(yellowTurbans.getVId());
        campArmy.setArmyName(general.getName());
        campArmy.setPlayerLv(army.getGeneralLv());
        campArmy.setPlayerId(-9);
        campArmy.setForceId(yellowTurbans.getForceId());
        campArmy.setPlayerName(army.getName());
        getTerrainValue(battle.terrainVal, troop, battleSide, campArmy);
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
        int forces = yellowTurbans.getHp();
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
        campArmy.setTacticVal(yellowTurbans.getTacticval());
        if (battleSide == 1) {
            campArmy.setStrategies(troop.getStrategyMap().get(battle.terrainVal));
        }
        else {
            campArmy.setStrategies(troop.getStrategyDefMap().get(battle.terrainVal));
        }
        return campArmy;
    }
    
    @Override
    public CampArmy copyArmyfromHuizhanPkRewardNpc(final IDataGetter dataGetter, final Battle battle, final int armyId, final int battleSide, final int forceId) {
        final CampArmy campArmy = new CampArmy();
        final Army army = (Army)dataGetter.getArmyCache().get((Object)armyId);
        final General general = (General)dataGetter.getGeneralCache().get((Object)armyId);
        campArmy.setSpecialGeneral(new SpecialGeneral(general.getGeneralSpecialInfo().generalType, general.getGeneralSpecialInfo().param, general.getGeneralSpecialInfo().param2));
        final Troop troop = (Troop)dataGetter.getTroopCache().get((Object)general.getTroop());
        campArmy.isYellowTrubans = true;
        campArmy.updateDB = false;
        campArmy.setId(battle.campNum.getAndIncrement());
        campArmy.setPgmVId(0);
        campArmy.setArmyName(general.getName());
        campArmy.setPlayerLv(army.getGeneralLv());
        campArmy.setPlayerId(-10);
        campArmy.setForceId(forceId);
        campArmy.setPlayerName(army.getName());
        getTerrainValue(battle.terrainVal, troop, battleSide, campArmy);
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
        int forces = army.getArmyHp();
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
        campArmy.setTacticVal(1);
        if (battleSide == 1) {
            campArmy.setStrategies(troop.getStrategyMap().get(battle.terrainVal));
        }
        else {
            campArmy.setStrategies(troop.getStrategyDefMap().get(battle.terrainVal));
        }
        return campArmy;
    }
}
