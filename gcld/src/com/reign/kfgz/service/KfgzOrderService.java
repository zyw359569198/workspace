package com.reign.kfgz.service;

import org.springframework.stereotype.*;
import com.reign.kfgz.control.*;
import com.reign.kfgz.resource.*;
import com.reign.kfgz.dto.*;
import com.reign.kf.match.common.*;
import com.reign.kf.match.sdata.cache.*;
import com.reign.util.*;
import com.reign.framework.json.*;
import com.reign.kfgz.battle.*;
import com.reign.kfgz.world.*;
import com.reign.kf.match.sdata.domain.*;
import java.util.*;
import com.reign.kfgz.team.*;
import com.reign.kfgz.comm.*;

@Component
public class KfgzOrderService implements IKfgzOrderService
{
    @Override
    public byte[] useOrder(final KfPlayerInfo player, final int teamId) {
        final int cId = player.getCompetitorId();
        if (player.getPlayerLevel() < 70) {
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
        final KfgzOrderToken officeToken = world.getOrderTokenByCityIdAndForceId(teamId, forceId);
        if (officeToken != null && officeToken.isEffect()) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.KFGZ_ALREALY_ORDERARMY);
        }
        boolean hasGeneralInToTeam = false;
        for (final KfGeneralInfo gInfo : player.getgMap().values()) {
            if (gInfo.getTeam() == team) {
                hasGeneralInToTeam = true;
                break;
            }
        }
        if (!hasGeneralInToTeam) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.NOGENERAL_INCITY);
        }
        final Chargeitem ci = ChargeitemCache.getById(76);
        final boolean useGoldSuc = KfgzResChangeManager.consumeResource(cId, ci.getCost(), "gold", "\u56fd\u6218\u5f81\u53ec\u4ee4");
        if (!useGoldSuc) {
            return JsonBuilder.getJson(State.FAIL, "\u6ca1\u6709\u8db3\u591f\u91d1\u5e01");
        }
        final double addExp = ci.getParam();
        KfgzResChangeManager.addResource(cId, (int)addExp, "exp", "\u56fd\u6218\u5f81\u53ec\u4ee4");
        final int officeId = player.getOfficerId();
        final KfgzOrderToken newOfficeToken = new KfgzOrderToken();
        final Halls hall = HallsCache.getHallsById(officeId);
        final int foodCoe = (int)(Object)CCache.getCById("World.GoldOrd.Food").getValue();
        newOfficeToken.setFoodAdd(foodCoe);
        newOfficeToken.setForceId(forceId);
        newOfficeToken.setNation(player.getNation());
        newOfficeToken.setOfficerId(officeId);
        newOfficeToken.setOfficerName((hall == null) ? "" : hall.getNameList());
        newOfficeToken.setPlayerName(player.getPlayerName());
        newOfficeToken.setStartTime(System.currentTimeMillis());
        newOfficeToken.setCityId(teamId);
        world.setNewOrderTokenByCityIdAndForceId(newOfficeToken);
        KfgzMessageSender.sendMsgToForce(gzId, forceId, newOfficeToken.createJsonObject(), PushCommand.PUSH_KF_ORDERTOKEN);
        final String content = MessageFormatter.format(LocalMessages.KFGZ_USE_ORDER_TOKEN_NOTICE, new Object[] { newOfficeToken.getPlayerName(), WorldCityCache.getById(teamId).getName() });
        KfgzMessageSender.sendChatToForce(gzId, forceId, content);
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("goldOrderExp", (int)addExp);
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public byte[] getOrderTokenTeamInfo(final KfPlayerInfo player, final int teamId) {
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
        final KfgzOrderToken newOrderToken = world.getOrderTokenByCityIdAndForceId(teamId, forceId);
        if (newOrderToken == null || !newOrderToken.isEffect()) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.NO_ORDER_TOKEN_INCITY);
        }
        if (newOrderToken.playerhasUsed(player.getCompetitorId())) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.YOU_USE_ORDER_TOKEN_ALREADY);
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
        doc.createElement("cityId", newOrderToken.getCityId());
        doc.createElement("playerName", newOrderToken.getPlayerName());
        doc.createElement("officerName", newOrderToken.getOfficerName());
        doc.createElement("cd", newOrderToken.getTokenCD());
        doc.createElement("foodAdd", newOrderToken.getFoodAdd());
        doc.createElement("forceId", newOrderToken.getForceId());
        doc.createElement("nation", newOrderToken.getNation());
        final double fightCoe = CCache.getCById("World.GoldOrd.LegionE").getValue();
        doc.createElement("fightReward", fightCoe * 100.0);
        doc.endObject();
        doc.createElement("gIds", list);
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public byte[] doRushInOrderTokenTeam(final KfPlayerInfo player, final int toTeamId, final String gIds) {
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
        final KfgzOrderToken newOrderToken = world.getOrderTokenByCityIdAndForceId(toTeamId, forceId);
        if (newOrderToken == null || !newOrderToken.isEffect()) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.NO_ORDER_TOKEN_INCITY);
        }
        if (newOrderToken.playerhasUsed(player.getCompetitorId())) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.YOU_USE_ORDER_TOKEN_ALREADY);
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
        final int foodAdd = newOrderToken.getFoodAdd();
        int allFoodAdd = 0;
        for (final KfGeneralInfo gInfo2 : gIdList) {
            final KfTeam oldTeam = gInfo2.team;
            Label_0530: {
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
                    final KfCampArmy ca = gInfo2.getCampArmy();
                    ca.Mubing((ca.getArmyHpOrg() - ca.getArmyHp() > 0) ? (ca.getArmyHpOrg() - ca.getArmyHp()) : 0);
                    gInfo2.team.removeGeneral(gInfo2);
                    gInfo2.pushDirectMove();
                    KfgzResChangeManager.addResource(gInfo2.getpInfo().getCompetitorId(), foodAdd, "food", "\u56fd\u6218\u96c6\u56e2\u519b\u7cae\u98df");
                    final Chargeitem ci = ChargeitemCache.getById(63);
                    final double inspireEffect = ci.getParam();
                    if (inspireEffect > 0.0) {
                        gInfo2.getCampArmy().setTeamEffect(inspireEffect);
                        gInfo2.getCampArmy().setTeamGenreal("\u96c6\u56e2\u519b");
                    }
                    toTeam.addGeneral(gInfo2);
                    toTeam.battle.doSolo(gInfo2);
                    allFoodAdd += foodAdd;
                    break Label_0530;
                    continue;
                }
                finally {
                    KfgzConstants.doUnlockCities((KfCity)oldTeam, (KfCity)toTeam);
                }
            }
            KfgzConstants.doUnlockCities((KfCity)oldTeam, (KfCity)toTeam);
        }
        newOrderToken.addNewUseCId(player.getCompetitorId());
        KfgzResChangeManager.addResource(player.getCompetitorId(), allFoodAdd, "food", "\u56fd\u6218\u5b98\u5458\u4ee4\u7cae\u98df");
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
}
