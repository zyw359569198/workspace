package com.reign.kfgz.world;

import org.springframework.stereotype.*;
import com.reign.kf.comm.util.*;
import com.reign.kfgz.comm.*;
import com.reign.kfgz.ai.constants.*;
import com.reign.kfgz.ai.event.*;
import com.reign.framework.json.*;
import com.reign.kfgz.constants.*;
import com.reign.kf.match.sdata.cache.*;
import com.reign.kf.match.sdata.domain.*;
import com.reign.kfgz.team.*;
import com.reign.kf.match.common.*;
import com.reign.kfgz.battle.*;
import com.reign.kfgz.dto.*;
import java.util.*;
import com.reign.kfgz.control.*;

@Component("kfWorldService")
public class KfWorldService implements IKfWorldService
{
    @Override
    public byte[] getWorldMap(final KfPlayerInfo player) {
        return JsonBuilder.getObjectJson(State.SUCCESS, this.getResult(player));
    }
    
    @Override
    public Tuple<byte[], Boolean> move(final KfPlayerInfo player, final int gId, final int city) {
        final KfGeneralInfo gInfo = player.getgMap().get(gId);
        if (gInfo == null) {
            return new Tuple(JsonBuilder.getJson(State.FAIL, LocalMessages.COMM_1), false);
        }
        if (gInfo.getState() != 1 || gInfo.getCampArmy().getArmyHp() <= 0) {
            return new Tuple(JsonBuilder.getJson(State.FAIL, LocalMessages.WORLD_MOVE_1), false);
        }
        if (gInfo.getTeam() instanceof KfGroupArmyTeam) {
            return new Tuple(JsonBuilder.getJson(State.FAIL, "\u96c6\u56e2\u519b\u4e2d\u4e0d\u80fd\u79fb\u52a8"), false);
        }
        if (city != 0) {
            final KfgzWorldCity wc = WorldCityCache.getById(city);
            if (wc.getType() == 1 && wc.getForce_id() != player.getForceId()) {
                return new Tuple(JsonBuilder.getJson(State.FAIL, LocalMessages.WORLD_MOVE_3), false);
            }
        }
        final KfGeneralAI gAi = new KfGeneralAI();
        gAi.setgInfo(gInfo);
        gAi.setScript(AIConstants.getMoveToCityString(city));
        gInfo.setGeneralAI(gAi);
        final AIEvent event = new AIEvent();
        event.setType(0);
        gAi.nextBehaviour(event);
        final Tuple<byte[], Boolean> res = this.getResult(gInfo);
        return new Tuple(JsonBuilder.getObjectJson(State.SUCCESS, res.left), res.right);
    }
    
    private Tuple<byte[], Boolean> getResult(final KfGeneralInfo gInfo) {
        final JsonDocument doc = new JsonDocument();
        boolean suc = false;
        if (gInfo.getCityList() != null) {
            if (gInfo.getCityList().size() > 0) {
                suc = true;
            }
            doc.startArray("cityList");
            for (final int c : gInfo.getCityList()) {
                doc.startObject();
                doc.createElement("id", c);
                doc.endObject();
            }
            doc.endArray();
        }
        return new Tuple(doc.toByte(), suc);
    }
    
    private byte[] getResult(final KfPlayerInfo player) {
        final int gzId = player.getGzId();
        final int cId = player.getCompetitorId();
        final int forceId = player.getForceId();
        final KfWorld world = KfgzManager.getKfWorldByGzId(gzId);
        final JsonDocument doc = new JsonDocument();
        final KfgzBaseInfo baseInfo = KfgzManager.getGzBaseInfoById(player.getGzId());
        doc.createElement("round", KfgzCommConstants.getRoundByGzId(gzId));
        doc.createElement("serverName1", baseInfo.getServerName1());
        doc.createElement("nation1", baseInfo.getNation1());
        doc.createElement("serverName2", baseInfo.getServerName2());
        doc.createElement("nation2", baseInfo.getNation2());
        doc.createElement("expCoef", Math.round(baseInfo.getExpCoef() * 100.0f));
        final List<KfgzWorldRoad> changeRoad = WorldRoadCache.getChangeKfgzWorldRoad(baseInfo.getWorldId());
        doc.startArray("changeRoad");
        final Calendar nowCal = Calendar.getInstance();
        for (final KfgzWorldRoad kwr : changeRoad) {
            doc.startObject();
            doc.createElement("id", kwr.getId());
            doc.createElement("connect", world.getConnect(kwr.getId()));
            int seconds = 60 + nowCal.get(13) - world.getBaseSecond();
            if (seconds > 60) {
                seconds -= 60;
            }
            seconds += (world.getMinutes(kwr.getId()) - 1) * 60;
            if (world.getConnect(kwr.getId()) == 1) {
                doc.createElement("nextChangeSeconds", kwr.getConnect_minutes() * 60 - seconds);
            }
            else {
                doc.createElement("nextChangeSeconds", kwr.getDisconnect_minutes() * 60 - seconds);
            }
            doc.createElement("connectSeconds", kwr.getConnect_minutes() * 60);
            doc.createElement("disconnectSeconds", kwr.getDisconnect_minutes() * 60);
            doc.endObject();
        }
        doc.endArray();
        final int layerId = KfgzCommConstants.getLayerByGzID(gzId);
        doc.createElement("layerId", layerId);
        doc.startArray("city");
        for (final Map.Entry<Integer, KfCity> en : world.getCities().entrySet()) {
            final KfCity c = en.getValue();
            int cityS = 0;
            for (final KfCity city : world.getNearByCities(c.getTeamId())) {
                if (city.getForceId() == player.getForceId() && c.getForceId() != player.getForceId()) {
                    cityS = 1;
                }
            }
            c.createCityInfoDocSimple(doc, cityS);
        }
        doc.endArray();
        doc.startArray("general");
        final List<KfGeneralInfo> gList = new ArrayList<KfGeneralInfo>();
        for (final KfGeneralInfo gInfo : player.getgMap().values()) {
            gList.add(gInfo);
        }
        Collections.sort(gList, KfGeneralInfo.compare);
        for (final KfGeneralInfo gInfo : gList) {
            gInfo.createLeftGeneralInfo(doc, null);
        }
        doc.endArray();
        final List<KfgzOfficeToken> tokenList = world.getOfficeTokenListByForceId(player.getForceId());
        doc.startArray("officeTokenList");
        for (final KfgzOfficeToken newOfficeToken : tokenList) {
            if (newOfficeToken.playerhasUsed(cId)) {
                continue;
            }
            final int cityId = newOfficeToken.getCityId();
            final KfCity city2 = world.getCities().get(cityId);
            if (city2.battle == null) {
                continue;
            }
            if (city2.battle.getBattleState() != 2) {
                continue;
            }
            doc.appendJson(newOfficeToken.createJsonObject());
        }
        final Set<KfGroupArmyTeam> gSet = KfgzGroupTeamManager.getAllTeamByForceId(gzId, forceId);
        if (gSet != null && gSet.size() > 0) {
            final JsonDocument doc2 = new JsonDocument();
            doc2.startObject();
            doc2.createElement("gstate", 1);
            doc2.endObject();
            KfgzMessageSender.sendMsgToForce(gzId, forceId, doc2.toByte(), PushCommand.PUSH_KF_NEWGROUPTIKCET);
        }
        doc.endArray();
        doc.appendJson(KfgzManager.getBattleRankingByGzID(gzId).getPlayerKillArmyRankingInfo(player));
        final String wstgInfo = KfgzWorldStgManager.getWorldStgInfoXml(gzId, null);
        if (wstgInfo != null && !wstgInfo.isEmpty()) {
            doc.appendJson(wstgInfo.getBytes());
        }
        final int hallOfficeId = player.getHallOfficeId();
        doc.createElement("officeId", hallOfficeId);
        if (baseInfo.canChoosenNpcAI()) {
            final String worldChooseNpcInfo = KfgzNpcAIManager.getNpcAIXml(gzId, forceId);
            doc.appendJson(worldChooseNpcInfo.getBytes());
        }
        final List<KfgzOrderToken> orderTokenList = world.getOrderTokenListByForceId(player.getForceId());
        doc.startArray("orderTokenList");
        for (final KfgzOrderToken newOrderToken : orderTokenList) {
            if (newOrderToken.playerhasUsed(cId)) {
                continue;
            }
            final int cityId2 = newOrderToken.getCityId();
            final KfCity city3 = world.getCities().get(cityId2);
            if (city3.battle == null) {
                continue;
            }
            if (city3.battle.getBattleState() != 2) {
                continue;
            }
            doc.appendJson(newOrderToken.createJsonObject());
        }
        doc.endArray();
        doc.createElement("gzState", baseInfo.getState());
        doc.createElement("cd", baseInfo.getEndCD());
        return doc.toByte();
    }
    
    @Override
    public byte[] getCityInfo(final KfPlayerInfo player, final int cityId) {
        final int gzId = player.getGzId();
        final KfgzBaseInfo baseInfo = KfgzManager.getGzBaseInfoById(gzId);
        final KfWorld world = KfgzManager.getKfWorldByGzId(gzId);
        final KfCity city = world.getCities().get(cityId);
        final JsonDocument doc = new JsonDocument();
        city.createCityInfoDoc(doc);
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public byte[] getJieBingInfo(final KfPlayerInfo player) {
        final int gzId = player.getGzId();
        final int forceId = player.getForceId();
        final String doc = KfgzNpcManager.getJieBingInfo(gzId, forceId);
        return JsonBuilder.getJson(State.SUCCESS, doc.getBytes());
    }
    
    @Override
    public byte[] getAllyInfo(final KfPlayerInfo player) {
        final int gzId = player.getGzId();
        final int forceId = player.getForceId();
        final String doc = KfgzNpcManager.getAllyInfo(gzId, forceId);
        return JsonBuilder.getJson(State.SUCCESS, doc.getBytes());
    }
}
