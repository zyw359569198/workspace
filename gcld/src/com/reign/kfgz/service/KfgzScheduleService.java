package com.reign.kfgz.service;

import org.springframework.stereotype.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.framework.json.*;
import com.reign.kfgz.team.*;
import com.reign.kf.match.sdata.domain.*;
import com.reign.kfgz.resource.dto.*;
import com.reign.kfgz.resource.*;
import java.util.*;
import com.reign.kfgz.dto.*;
import com.reign.kf.match.common.*;
import com.reign.kfgz.battle.*;
import com.reign.kf.match.sdata.cache.*;
import com.reign.util.*;
import com.reign.kfgz.world.*;
import com.reign.kfgz.comm.*;
import com.reign.kfgz.control.*;

@Component
public class KfgzScheduleService implements IKfgzScheduleService
{
    @Autowired
    IKfWorldService kfWorldService;
    
    @Override
    public byte[] getBattleIniInfo(final KfPlayerInfo player, final int teamId) {
        final int cId = player.getCompetitorId();
        final KfTeam team = KfgzTeamManager.getKfTeam(teamId, player.getGzId());
        final KfBattle battle = team.battle;
        if (battle == null || battle.getBattleState() != 2) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.BATTLE_IS_OVER);
        }
        final StringBuilder battleMsg = new StringBuilder();
        battle.getIniBattleMsg(battleMsg, player.getCompetitorId());
        final int terrain = team.terrain;
        final boolean isAtt = team.isAtt(player.getForceId());
        final int battleId = team.getTeamId();
        final int gzId = player.getGzId();
        int officeTokenNum = 0;
        if (player.getOfficeTokenNum() > 0) {
            final List<Integer> list = new ArrayList<Integer>();
            for (final KfGeneralInfo gInfo : player.getgMap().values()) {
                if (gInfo.getTeam().getTeamId() == team.getTeamId()) {
                    list.add(gInfo.getgId());
                }
            }
            if (list.size() > 0) {
                officeTokenNum = player.getOfficeTokenNum();
            }
        }
        final KfgzBaseInfo baseInfo = KfgzManager.getGzBaseInfoById(gzId);
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.startObject("battleInfo");
        doc.createElement("terrain", terrain);
        doc.createElement("isAtt", isAtt);
        if (officeTokenNum > 0) {
            doc.createElement("officeTokenNum", officeTokenNum);
            final Halls hall = HallsCache.getHallsById(player.getOfficerId());
            if (hall != null) {
                doc.createElement("officeId", hall.getOfficialId());
            }
        }
        if (player.getPlayerLevel() >= 70) {
            doc.createElement("canOrder", 1);
            final Chargeitem ci = ChargeitemCache.getById(76);
            doc.createElement("orderGold", ci.getCost());
            doc.createElement("goldOrderExp", ci.getParam());
        }
        doc.createElement("battleId", KfgzBattleConstants.getBattleTitle(battleId, player.getGzId()));
        doc.createElement("battleType", team.getBattleType());
        doc.createElement("cityId", team.getCityId());
        doc.createElement("expCoef", Math.round(baseInfo.getExpCoef() * 100.0f));
        doc.createElement("freeBuyPhantom", KfgzResChangeManager.getPhantomCount(cId));
        doc.createElement("remainBuyPhantom", player.getRemainBuyPhantomTimes(baseInfo));
        doc.endObject();
        doc.createElement("battleReport", battleMsg);
        final KfGroupArmyTeam gArmyTeam = KfgzGroupTeamManager.getPlayerGroupArmyInfo(gzId, cId);
        if (gArmyTeam != null && team instanceof KfCity) {
            doc.createElement("hasGroupArmy", 1);
        }
        if (player.getPlayerLevel() < 70) {
            doc.createElement("autoAtt", (-1));
        }
        else {
            doc.createElement("autoAtt", player.isAutoStg() ? 1 : 0);
        }
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    public static void main(final String[] args) {
        final List<Integer> enemyCitys = new ArrayList<Integer>();
        final List<Integer> list = new ArrayList<Integer>();
        list.add(123);
        list.add(126);
        enemyCitys.add(1);
        enemyCitys.add(3);
        JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("citys", enemyCitys);
        doc.createElement("gIds", list);
        doc.endObject();
        System.out.println(new String(JsonBuilder.getJson(State.SUCCESS, doc.toByte())));
        doc = new JsonDocument();
        doc.startObject();
        doc.startObject("battleInfo");
        doc.createElement("terrain", 1);
        doc.createElement("isAtt", true);
        doc.createElement("battleId", 121);
        doc.createElement("battleType", 1025);
        doc.endObject();
        doc.createElement("battleReport", "fasfdsa");
        doc.endObject();
        System.out.println(new String(JsonBuilder.getJson(State.SUCCESS, doc.toByte())));
    }
    
    @Override
    public byte[] chooseStrategyOrTactic(final KfPlayerInfo player, final int pos, final int tacticId, final int teamId) {
        final KfTeam team = KfgzTeamManager.getKfTeam(teamId, player.getGzId());
        final KfBattle battle = team.battle;
        if (battle == null || battle.getBattleState() != 2) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.BATTLE_IS_OVER);
        }
        final int useSTRes = battle.chooseStrategyOrTactic(player.getCompetitorId(), pos, tacticId);
        if (useSTRes == 1) {
            return JsonBuilder.getJson(State.SUCCESS, "");
        }
        if (useSTRes == 2) {
            return JsonBuilder.getJson(State.FAIL, "");
        }
        return JsonBuilder.getJson(State.FAIL, "");
    }
    
    @Override
    public byte[] doSolo(final KfPlayerInfo player, final int teamId) {
        final KfTeam team = KfgzTeamManager.getKfTeam(teamId, player.getGzId());
        final KfBattle battle = team.battle;
        if (battle == null || battle.getBattleState() != 2) {
            return JsonBuilder.getJson(State.FAIL, "");
        }
        final int forceId = player.getForceId();
        final boolean isAtt = team.isAtt(forceId);
        final KfGeneralInfo gInfo = battle.getSoloGeneral(player);
        if (gInfo == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.NOGENERAL_TO_SOLO);
        }
        final int generalId = gInfo.getgId();
        final General general = GeneralCache.getGeneralById(generalId);
        final Troop troop = TroopCache.getTroop(general.getTroop(), player.getTech28());
        final double troopFoodConsumeCoeA = TroopConscribeCache.getTroopConscribeById(troop.getId()).getFood();
        final double troopFoodA = troopFoodConsumeCoeA * gInfo.getCampArmy().armyHp;
        float due = 0.0f;
        float dueLv = 0.0f;
        if (!isAtt) {
            due = CCache.getCById("World.Due.CopperE").getValue();
            dueLv = CCache.getCById("World.Due.LvCopperE").getValue();
        }
        else {
            due = CCache.getCById("World.InitDue.CopperE").getValue();
            dueLv = CCache.getCById("World.InitDue.LvCopperE").getValue();
        }
        final int copper = ((int)((troopFoodA * due + dueLv * player.getPlayerLevel()) / 100.0) + 1) * 100;
        if (!KfgzResChangeManager.canConsumeResource(player.getCompetitorId(), copper, "copper")) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.NOENOUGH_COPPER);
        }
        final int soloRes = battle.doSoloByPlayer(player);
        if (soloRes >= 10000) {
            KfgzResChangeManager.consumeResource(player.getCompetitorId(), copper, "copper", "\u8de8\u670d\u56fd\u6218");
            final JsonDocument doc = new JsonDocument();
            doc.startObject();
            doc.createElement("copper", copper);
            doc.createElement("soloTeamId", soloRes);
            doc.endObject();
            return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
        }
        if (soloRes == 4) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.NOGENERAL_TO_SOLO);
        }
        if (soloRes == 3) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.NOGENERAL_TO_SOLO);
        }
        if (soloRes == 2) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.SOLO_FAILED);
        }
        return JsonBuilder.getJson(State.FAIL, "");
    }
    
    @Override
    public byte[] doRush(final KfPlayerInfo player, final int toteamId, final String gIds) {
        final KfTeam team = KfgzTeamManager.getKfTeam(toteamId, player.getGzId());
        if (((KfCity)team).isCaptial()) {
            JsonBuilder.getJson(State.FAIL, LocalMessages.CANNOT_RUSH_TO_CAPITAL);
        }
        final String[] gIdArray = gIds.split("#");
        final List<KfGeneralInfo> gIdList = new ArrayList<KfGeneralInfo>();
        String[] array;
        for (int length = (array = gIdArray).length, i = 0; i < length; ++i) {
            final String sgId = array[i];
            final Integer gId = Integer.parseInt(sgId);
            final KfGeneralInfo gInfo = player.getgMap().get(gId);
            if (gInfo == null) {
                return JsonBuilder.getJson(State.FAIL, "\u53c2\u6570\u9519\u8bef");
            }
            gIdList.add(gInfo);
        }
        int res = 2;
        for (final KfGeneralInfo gInfo2 : gIdList) {
            final KfBattle battle = gInfo2.getTeam().battle;
            if (battle != null) {
                res = battle.doRush(gInfo2, toteamId);
            }
        }
        if (res == 2) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.RUSH_FAILED);
        }
        if (res == 1) {
            final JsonDocument doc = new JsonDocument();
            doc.startObject();
            doc.createElement("teamId", toteamId);
            doc.endObject();
            return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
        }
        return JsonBuilder.getJson(State.FAIL, LocalMessages.RUSH_FAILED);
    }
    
    @Override
    public byte[] getCanRushInfo(final KfPlayerInfo player, final int teamId) {
        final KfTeam team = KfgzTeamManager.getKfTeam(teamId, player.getGzId());
        final KfBattle battle = team.battle;
        if (battle == null || battle.getBattleState() != 2) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.BATTLE_IS_OVER);
        }
        if (team.getTeamType() != 1) {
            return JsonBuilder.getJson(State.FAIL, "");
        }
        boolean isAtt = true;
        if (team.getForceId() == player.getForceId()) {
            isAtt = false;
        }
        final int tech39 = player.getTech39();
        if (isAtt) {
            if (team.attGList.size() < (3 - tech39) * team.defGList.size()) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.CANNOT_RUSH_NOW);
            }
        }
        else if (team.defGList.size() < (3 - tech39) * team.attGList.size()) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.CANNOT_RUSH_NOW);
        }
        final List<Integer> list = new ArrayList<Integer>();
        for (final Map.Entry<Integer, KfGeneralInfo> entry : player.getgMap().entrySet()) {
            final KfGeneralInfo gInfo = entry.getValue();
            if (gInfo.team.getTeamId() == teamId && gInfo.getState() == 2) {
                list.add(gInfo.getgId());
            }
        }
        if (list.size() == 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.CANNOT_RUSH_NOW);
        }
        final int gzId = player.getGzId();
        final List<KfCity> cList = KfgzManager.getKfWorldByGzId(gzId).getNearByCities(teamId);
        final List<Integer> enemyCitys = new ArrayList<Integer>();
        for (final KfCity city : cList) {
            if ((city.getForceId() != player.getForceId() || (city.battle != null && city.battle.getBattleState() == 2)) && !city.isCaptial()) {
                enemyCitys.add(city.getTeamId());
            }
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("citys", enemyCitys);
        doc.createElement("gIds", list);
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public byte[] fastAddTroopHp(final KfPlayerInfo player, final int gId) {
        final KfGeneralInfo gInfo = player.getgMap().get(gId);
        if (gInfo == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.COMM_1);
        }
        if (gInfo.getState() != 1 || gInfo.getGeneralState() == 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.WORLD_MOVE_1);
        }
        int needNum = gInfo.needMubingNum();
        int mNum = 0;
        if (!KfgzResChangeManager.canConsumeResource(player.getCompetitorId(), needNum, "recruitToken")) {
            mNum = KfgzResChangeManager.getMubingNum(player.getCompetitorId());
        }
        else {
            mNum = needNum;
        }
        final List<ConsumeResourceDto> crdList = new ArrayList<ConsumeResourceDto>();
        if (mNum <= 0) {
            final ConsumeResourceDto crd = new ConsumeResourceDto();
            crd.setUnit("gold");
            crd.setValue(needNum);
            crd.setReason("\u5feb\u901f\u52df\u5175");
            crdList.add(crd);
        }
        else {
            needNum = mNum;
            final ConsumeResourceDto crd = new ConsumeResourceDto();
            crd.setUnit("recruitToken");
            crd.setValue(needNum);
            crd.setReason("\u5feb\u901f\u52df\u5175");
            crdList.add(crd);
        }
        final Chargeitem ci = ChargeitemCache.getById(13);
        final int num = (int)(KfgzResourceService.getOutput(player.getCompetitorId()) * ci.getParam() * 60.0 * needNum);
        final int food = gInfo.campArmy.getRecuitConsumeForPublic(num);
        final ConsumeResourceDto crd2 = new ConsumeResourceDto();
        crd2.setUnit("food");
        crd2.setValue(food);
        crd2.setReason("\u52df\u5175");
        crdList.add(crd2);
        for (final ConsumeResourceDto c : crdList) {
            if (c.getValue() < 0) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.COMM_3);
            }
        }
        if (!KfgzResChangeManager.consumeResourceList(player.getCompetitorId(), crdList)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.COMM_3);
        }
        gInfo.campArmy.MubingIgnoreResource(num);
        gInfo.pushHpData();
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        if (mNum <= 0) {
            doc.createElement("gold", needNum);
        }
        else {
            doc.createElement("token", needNum);
        }
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public byte[] getRetreatInfo(final KfPlayerInfo player, final int teamId) {
        final KfTeam team = KfgzTeamManager.getKfTeam(teamId, player.getGzId());
        final KfBattle battle = team.battle;
        if (battle == null || battle.getBattleState() != 2) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.BATTLE_IS_OVER);
        }
        if (team.getTeamType() != 1) {
            return JsonBuilder.getJson(State.FAIL, "");
        }
        int hptotal = 0;
        final List<Integer> list = new ArrayList<Integer>();
        for (final Map.Entry<Integer, KfGeneralInfo> entry : player.getgMap().entrySet()) {
            final KfGeneralInfo gInfo = entry.getValue();
            if (gInfo.team.getTeamId() == teamId && gInfo.getState() == 2) {
                list.add(gInfo.getgId());
                hptotal += gInfo.getCampArmy().getArmyHp();
            }
        }
        if (list.size() == 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.NO_GENERAL_TO_RETREATE);
        }
        final int gzId = player.getGzId();
        final List<KfCity> cList = KfgzManager.getKfWorldByGzId(gzId).getNearByCities(teamId);
        final List<Integer> selfCitys = new ArrayList<Integer>();
        for (final KfCity city : cList) {
            if (city.getForceId() == player.getForceId()) {
                selfCitys.add(city.getTeamId());
            }
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("citys", selfCitys);
        doc.createElement("gIds", list);
        doc.createElement("hpLost", hptotal / 10);
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public byte[] doRetreat(final KfPlayerInfo player, final String gIds, final int toteamId) {
        final KfTeam team = KfgzTeamManager.getKfTeam(toteamId, player.getGzId());
        if (team.getTeamType() != 1) {
            return JsonBuilder.getJson(State.FAIL, "");
        }
        final KfBattle battle = team.battle;
        if (battle != null && (battle.getBattleState() == 2 || battle.getBattleState() == 1)) {
            return JsonBuilder.getJson(State.FAIL, "");
        }
        final String[] gIdArray = gIds.split("#");
        final List<KfGeneralInfo> gIdList = new ArrayList<KfGeneralInfo>();
        String[] array;
        for (int length = (array = gIdArray).length, i = 0; i < length; ++i) {
            final String sgId = array[i];
            final Integer gId = Integer.parseInt(sgId);
            final KfGeneralInfo gInfo = player.getgMap().get(gId);
            if (gInfo == null) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.PARAMETER_FAILED);
            }
            gIdList.add(gInfo);
        }
        int res = 2;
        final int gzId = player.getGzId();
        for (final KfGeneralInfo gInfo2 : gIdList) {
            final KfBattle nowGbattle = gInfo2.getTeam().battle;
            if (nowGbattle != null && nowGbattle.getBattleState() == 2) {
                final int cityId = nowGbattle.getKfTeam().getTeamId();
                if (!KfgzManager.getKfWorldByGzId(gzId).isCityNearBy(cityId, toteamId)) {
                    continue;
                }
                res = nowGbattle.doRetreat(gInfo2, toteamId);
            }
        }
        if (res == 2) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.RETREATE_FAILED);
        }
        if (res == 1) {
            return JsonBuilder.getJson(State.SUCCESS, "");
        }
        return JsonBuilder.getJson(State.FAIL, LocalMessages.RETREATE_FAILED);
    }
    
    @Override
    public byte[] buyPhantom(final KfPlayerInfo player, final int teamId) {
        final KfTeam team = KfgzTeamManager.getKfTeam(teamId, player.getGzId());
        final KfBattle battle = team.battle;
        if (battle == null || battle.getBattleState() != 2) {
            return JsonBuilder.getJson(State.FAIL, "");
        }
        if (!(team instanceof KfCity)) {
            return JsonBuilder.getJson(State.FAIL, "not correct cityid");
        }
        final int cId = player.getCompetitorId();
        final int gzId = player.getGzId();
        final KfgzBaseInfo baseInfo = KfgzManager.getGzBaseInfoById(gzId);
        final int canBuyRes = this.canBuyPhantom(player, baseInfo);
        if (canBuyRes != 1) {
            if (canBuyRes == 2) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.BUYPHANTOMLIMIT1);
            }
            if (canBuyRes == 3) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.BUYPHANTOMLIMIT2);
            }
            if (canBuyRes == 4) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.BUYPHANTOMLIMIT3);
            }
            return JsonBuilder.getJson(State.FAIL, "");
        }
        else {
            KfGeneralInfo chooseGInfo = null;
            for (final Map.Entry<Integer, KfGeneralInfo> entry : player.getgMap().entrySet()) {
                final KfGeneralInfo gInfo = entry.getValue();
                if (gInfo.team.getTeamId() == teamId && gInfo.getState() != 1 && (chooseGInfo == null || gInfo.campArmy.getGeneralLv() > chooseGInfo.campArmy.getGeneralLv())) {
                    chooseGInfo = gInfo;
                }
            }
            if (chooseGInfo == null) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.NO_GENERAL_IN_BATTLE);
            }
            final boolean useFreeBuyPhantom = KfgzResChangeManager.consumeResource(cId, 1, "phantomCount", "\u56fd\u6218\u5e7b\u5f71");
            if (!useFreeBuyPhantom) {
                final int cost = ChargeitemCache.getById(53).getCost();
                final boolean useGoldSuc = KfgzResChangeManager.consumeResource(cId, cost, "gold", "\u56fd\u6218\u5e7b\u5f71");
                if (!useGoldSuc) {
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.GOLD_NOT_ENOUPH);
                }
            }
            team.addPhantom(chooseGInfo);
            this.doChangePlayerInfoWhenBuyPhantom(player, baseInfo);
            final int forceId = player.getForceId();
            final int addExp = player.getTech49();
            final JsonDocument doc = new JsonDocument();
            doc.startObject();
            if (addExp > 0 && !useFreeBuyPhantom) {
                KfgzResChangeManager.addResource(cId, addExp, "exp", "\u56fd\u6218\u5e7b\u5f71\u79d1\u6280");
                doc.createElement("expAdd", addExp);
            }
            doc.createElement("gId", chooseGInfo.getgId());
            doc.createElement("freeBuyPhantom", KfgzResChangeManager.getPhantomCount(cId));
            doc.createElement("remainBuyPhantom", player.getRemainBuyPhantomTimes(baseInfo));
            doc.endObject();
            return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
        }
    }
    
    private int canBuyPhantom(final KfPlayerInfo player, final KfgzBaseInfo baseInfo) {
        final long cd = baseInfo.getEndCD();
        if (cd > 1800000L) {
            return 2;
        }
        if (cd <= 1800000L && cd > 900000L && player.getBuyPhantom15Min() >= 50) {
            return 3;
        }
        if (cd <= 900000L && player.getBuyPhantom30Min() >= 50) {
            return 4;
        }
        return 1;
    }
    
    private void doChangePlayerInfoWhenBuyPhantom(final KfPlayerInfo player, final KfgzBaseInfo baseInfo) {
        final long cd = baseInfo.getEndCD();
        if (cd <= 1800000L && cd > 900000L) {
            player.setBuyPhantom15Min(player.getBuyPhantom15Min() + 1);
        }
        if (cd <= 900000L) {
            player.setBuyPhantom30Min(player.getBuyPhantom30Min() + 1);
        }
    }
    
    @Override
    public byte[] callGeneral(final KfPlayerInfo player, final int toteamId, final String gIds) {
        final KfTeam team = KfgzTeamManager.getKfTeam(toteamId, player.getGzId());
        if (team.getTeamType() != 1) {
            return JsonBuilder.getJson(State.FAIL, "not correct cityid");
        }
        final String[] gIdArray = gIds.split("#");
        final Set<KfGeneralInfo> gIdList = new HashSet<KfGeneralInfo>();
        String[] array;
        for (int length = (array = gIdArray).length, i = 0; i < length; ++i) {
            final String sgId = array[i];
            final Integer gId = Integer.parseInt(sgId);
            final KfGeneralInfo gInfo = player.getgMap().get(gId);
            if (gInfo == null) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.PARAMETER_FAILED);
            }
            gIdList.add(gInfo);
        }
        boolean res = true;
        for (final KfGeneralInfo gInfo2 : gIdList) {
            final boolean suc = this.kfWorldService.move(player, gInfo2.getgId(), toteamId).right;
            if (!suc) {
                res = false;
            }
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        if (!res) {
            doc.createElement("reason", "\u90e8\u5206\u6b66\u5c06\u65e0\u6cd5\u5230\u8fbe");
        }
        for (final KfGeneralInfo gInfo3 : gIdList) {
            if (gInfo3.getCityList() != null) {
                try {
                    doc.startArray(String.valueOf(gInfo3.getgId()));
                    for (final int c : gInfo3.getCityList()) {
                        doc.startObject();
                        doc.createElement("id", c);
                        doc.endObject();
                    }
                    doc.endArray();
                }
                catch (Exception ex) {}
            }
        }
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public byte[] getCallGeneralInfo(final KfPlayerInfo player, final int teamId) {
        final KfTeam team = KfgzTeamManager.getKfTeam(teamId, player.getGzId());
        if (team.getTeamType() != 1) {
            return JsonBuilder.getJson(State.FAIL, "");
        }
        final List<Integer> list = new ArrayList<Integer>();
        for (final Map.Entry<Integer, KfGeneralInfo> entry : player.getgMap().entrySet()) {
            final KfGeneralInfo gInfo = entry.getValue();
            if (gInfo.team.getTeamId() != teamId && gInfo.getState() == 1) {
                list.add(gInfo.getgId());
            }
        }
        if (list.size() == 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.NO_GENERAL_TO_CALL);
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("gIds", list);
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public byte[] getGzPlayerResult(final KfPlayerInfo player) {
        final int gzId = player.getGzId();
        final int forceId = player.getForceId();
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        final byte[] pRankingInfo = KfgzManager.getBattleRankingByGzID(gzId).getPlayerRankingInfo(player);
        doc.appendJson(pRankingInfo);
        final int selfCityNum = KfgzManager.getKfWorldByGzId(gzId).getForceCityNum(forceId);
        doc.createElement("selfCityNum", selfCityNum);
        final int oppCityNum = KfgzManager.getKfWorldByGzId(gzId).getForceCityNum(3 - forceId);
        doc.createElement("oppCityNum", oppCityNum);
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public byte[] setAutoAttack(final KfPlayerInfo player) {
        if (player.getPlayerLevel() < 70) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.FUNCTION_NOT_OPEN);
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        final boolean isAutoAtt = player.isAutoStg();
        player.setAutoStg(!isAutoAtt);
        doc.createElement("autoAtt", player.isAutoStg() ? 1 : 0);
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public byte[] leaveBattleTeam(final KfPlayerInfo player, final int teamId) {
        final KfTeam team = KfgzTeamManager.getKfTeam(teamId, player.getGzId());
        final KfBattle battle = team.battle;
        if (battle == null || battle.getBattleState() != 2) {
            return JsonBuilder.getJson(State.SUCCESS, "");
        }
        final int cId = player.getCompetitorId();
        battle.leave(cId);
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Override
    public byte[] clearSoloCd(final KfPlayerInfo player) {
        final int cost = ChargeitemCache.getById(54).getCost();
        final boolean useGoldSuc = KfgzResChangeManager.consumeResource(player.getCompetitorId(), cost, "gold", "\u56fd\u6218\u79d2\u7a81\u51fbcd");
        if (!useGoldSuc) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.GOLD_NOT_ENOUPH);
        }
        player.clearCD();
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Override
    public byte[] useOfficeToken(final KfPlayerInfo player, final int teamId) {
        if (player.getOfficeTokenNum() <= 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.NO_ENOUPH_OFFICE_TOKEN);
        }
        final int gzId = player.getGzId();
        final KfTeam team = KfgzTeamManager.getKfTeam(teamId, gzId);
        final KfBattle battle = team.battle;
        if (battle == null || battle.getBattleState() != 2) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.BATTLE_IS_OVER);
        }
        final int forceId = player.getForceId();
        final KfWorld world = KfgzManager.getKfWorldByGzId(gzId);
        if (!world.getCities().containsKey(teamId) || world.getCapitals().containsKey(teamId)) {
            return JsonBuilder.getJson(State.FAIL, "error ciytid");
        }
        final KfgzOfficeToken officeToken = world.getOfficeTokenByCityIdAndForceId(teamId, forceId);
        if (officeToken != null && officeToken.isEffect()) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.CITY_USE_OFFICE_TOKEN_ALREADY);
        }
        final int officeId = player.getOfficerId();
        final KfgzOfficeToken newOfficeToken = new KfgzOfficeToken();
        final Halls hall = HallsCache.getHallsById(officeId);
        newOfficeToken.setFoodAdd(hall.getFoodAdd());
        newOfficeToken.setForceId(forceId);
        newOfficeToken.setNation(player.getNation());
        newOfficeToken.setOfficerId(officeId);
        newOfficeToken.setOfficerName(hall.getNameList());
        newOfficeToken.setPlayerName(player.getPlayerName());
        newOfficeToken.setStartTime(System.currentTimeMillis());
        newOfficeToken.setCityId(teamId);
        world.setNewOfficeTokenByCityIdAndForceId(newOfficeToken);
        player.setOfficeTokenNum(player.getOfficeTokenNum() - 1);
        KfgzMessageSender.sendMsgToForce(gzId, forceId, newOfficeToken.createJsonObject(), PushCommand.PUSH_KF_OFFICETOKEN);
        final String content = MessageFormatter.format(LocalMessages.KFGZ_USE_OFFICE_TOKEN_NOTICE, new Object[] { newOfficeToken.getPlayerName(), WorldCityCache.getById(teamId).getName() });
        KfgzMessageSender.sendChatToForce(gzId, forceId, content);
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Override
    public byte[] getOfficeTokenTeamInfo(final KfPlayerInfo player, final int teamId) {
        final int gzId = player.getGzId();
        final KfTeam team = KfgzTeamManager.getKfTeam(teamId, gzId);
        final KfBattle battle = team.battle;
        if (battle == null || battle.getBattleState() != 2) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.BATTLE_IS_OVER);
        }
        final int forceId = player.getForceId();
        final KfWorld world = KfgzManager.getKfWorldByGzId(gzId);
        if (!world.getCities().containsKey(teamId)) {
            return JsonBuilder.getJson(State.FAIL, "error city id");
        }
        final KfgzOfficeToken newOfficeToken = world.getOfficeTokenByCityIdAndForceId(teamId, forceId);
        if (newOfficeToken == null || !newOfficeToken.isEffect()) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.NO_OFFICE_TOKEN_INCITY);
        }
        if (newOfficeToken.playerhasUsed(player.getCompetitorId())) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.YOU_USE_OFFICE_TOKEN_ALREADY);
        }
        final List<Integer> list = new ArrayList<Integer>();
        for (final KfGeneralInfo gInfo : player.getgMap().values()) {
            if (gInfo.getState() == 1 && gInfo.getCampArmy().getArmyHp() > gInfo.getCampArmy().getArmyHpOrg() / 20) {
                list.add(gInfo.getgId());
            }
        }
        if (list.size() == 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.NO_FREE_GENERAL);
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.startObject("officeToken");
        doc.createElement("cityId", newOfficeToken.getCityId());
        doc.createElement("playerName", newOfficeToken.getPlayerName());
        doc.createElement("officerName", newOfficeToken.getOfficerName());
        doc.createElement("cd", newOfficeToken.getTokenCD());
        doc.createElement("foodAdd", newOfficeToken.getFoodAdd());
        doc.createElement("forceId", newOfficeToken.getForceId());
        doc.createElement("nation", newOfficeToken.getNation());
        doc.endObject();
        doc.createElement("gIds", list);
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public byte[] doRushInOfficeTokenTeam(final KfPlayerInfo player, final int toTeamId, final String gIds) {
        final int gzId = player.getGzId();
        final KfTeam toTeam = KfgzTeamManager.getKfTeam(toTeamId, gzId);
        KfBattle battle = toTeam.battle;
        if (battle == null || battle.getBattleState() != 2) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.BATTLE_IS_OVER);
        }
        final int forceId = player.getForceId();
        final KfWorld world = KfgzManager.getKfWorldByGzId(gzId);
        if (!world.getCities().containsKey(toTeamId)) {
            return JsonBuilder.getJson(State.FAIL, "error city id");
        }
        final KfgzOfficeToken newOfficeToken = world.getOfficeTokenByCityIdAndForceId(toTeamId, forceId);
        if (newOfficeToken == null || !newOfficeToken.isEffect()) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.NO_OFFICE_TOKEN_INCITY);
        }
        if (newOfficeToken.playerhasUsed(player.getCompetitorId())) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.YOU_USE_OFFICE_TOKEN_ALREADY);
        }
        final String[] gIdArray = gIds.split("#");
        final List<KfGeneralInfo> gIdList = new ArrayList<KfGeneralInfo>();
        String[] array;
        for (int length = (array = gIdArray).length, i = 0; i < length; ++i) {
            final String sgId = array[i];
            final Integer gId = Integer.parseInt(sgId);
            final KfGeneralInfo gInfo = player.getgMap().get(gId);
            if (gInfo == null) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.PARAMETER_FAILED);
            }
            gIdList.add(gInfo);
        }
        final int foodAdd = newOfficeToken.getFoodAdd();
        int allFoodAdd = 0;
        for (final KfGeneralInfo gInfo2 : gIdList) {
            final KfTeam oldTeam = gInfo2.team;
            Label_0410: {
                try {
                    KfgzConstants.doLockCities((KfCity)oldTeam, (KfCity)toTeam);
                    if (gInfo2.team != oldTeam) {
                        continue;
                    }
                    if (gInfo2.getState() != 1) {
                        continue;
                    }
                    battle = toTeam.battle;
                    if (battle == null) {
                        continue;
                    }
                    if (battle.getBattleState() != 2) {
                        continue;
                    }
                    gInfo2.team.removeGeneral(gInfo2);
                    gInfo2.pushDirectMove();
                    toTeam.addGeneral(gInfo2);
                    allFoodAdd += foodAdd;
                    break Label_0410;
                    continue;
                }
                finally {
                    KfgzConstants.doUnlockCities((KfCity)oldTeam, (KfCity)toTeam);
                }
            }
            KfgzConstants.doUnlockCities((KfCity)oldTeam, (KfCity)toTeam);
        }
        newOfficeToken.addNewUseCId(player.getCompetitorId());
        KfgzResChangeManager.addResource(player.getCompetitorId(), allFoodAdd, "food", "\u56fd\u6218\u5b98\u5458\u4ee4\u7cae\u98df");
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Override
    public byte[] chooseNpcAI(final KfPlayerInfo player, final int choosenId) {
        final int gzId = player.getGzId();
        if (player.getHallOfficeId() > 2 || player.getHallOfficeId() <= 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.KFGZ_CANNOTCHOOSEN_NPCAI);
        }
        if (choosenId < 1 || choosenId > 3) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.KFGZ_CANNOTCHOOSEN_NPCAI);
        }
        final int forceId = player.getForceId();
        final boolean canChoosenAI = KfgzNpcAIManager.canChoosenNewAI(gzId, forceId);
        if (!canChoosenAI) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.KFGZ_CHOOSEN_ALREADY);
        }
        KfgzNpcAIManager.choosenNpcSkill(gzId, forceId, choosenId);
        final String worldChooseNpcInfo = KfgzNpcAIManager.getNpcAIXml(gzId, forceId);
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.appendJson(worldChooseNpcInfo.getBytes());
        doc.endObject();
        final Halls ph = player.getHallInfo();
        final String content = MessageFormatter.format(LocalMessages.CHANGE_ALLYNPC_NOTICE, new Object[] { (ph == null) ? "" : ph.getNameList(), player.getPlayerName() });
        KfgzMessageSender.sendChatToForce(gzId, forceId, content);
        KfgzMessageSender.sendMsgToForce(gzId, forceId, doc.toByte(), PushCommand.PUSH_KF_CHOOSENNPCAI);
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Override
    public byte[] getBattleCampList(final KfPlayerInfo player, final int teamId, final int page, final int side) {
        final int cId = player.getCompetitorId();
        final KfTeam team = KfgzTeamManager.getKfTeam(teamId, player.getGzId());
        final KfBattle battle = team.battle;
        if (battle == null || battle.getBattleState() != 2) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.BATTLE_IS_OVER);
        }
        return battle.getBattleCampList(page, side);
    }
}
