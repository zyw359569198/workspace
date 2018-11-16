package com.reign.kfgz.service;

import org.springframework.stereotype.*;
import java.util.concurrent.locks.*;
import com.reign.framework.json.*;
import com.reign.kfgz.comm.*;
import java.util.*;
import com.reign.kfgz.resource.*;
import com.reign.kf.match.common.*;
import com.reign.kfgz.battle.*;
import com.reign.util.*;
import com.reign.kfgz.team.*;
import com.reign.kfgz.control.*;
import com.reign.kf.match.sdata.cache.*;
import com.reign.kf.match.sdata.domain.*;

@Component
public class KfgzGroupTeamService implements IKfgzGroupTeamService
{
    public ReentrantReadWriteLock teamlock;
    
    public KfgzGroupTeamService() {
        this.teamlock = new ReentrantReadWriteLock();
    }
    
    @Override
    public byte[] getGroupTeamInfo(final KfPlayerInfo player) {
        final int cId = player.getCompetitorId();
        final int gzId = player.getGzId();
        final int forceId = player.getForceId();
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        KfgzGroupTeamManager.focusOnGroupTeam(gzId, cId, forceId);
        WorldLegion wl = WorldLegionCache.getWorldLegionById(1);
        doc.startArray("types");
        doc.startObject();
        doc.createElement("teamType", wl.getId());
        doc.createElement("maxNum", wl.getMax());
        doc.createElement("cost", wl.getGoldInit());
        int s = SerialCache.getValue(wl.getOwnerExpS(), player.getPlayerLevel());
        doc.createElement("exp", s * wl.getMax());
        doc.endObject();
        doc.endArray();
        doc.startArray("teamList");
        final Set<KfGroupArmyTeam> teamSet = KfgzGroupTeamManager.getJoinTeam(gzId, player);
        if (teamSet != null && teamSet.size() > 0) {
            for (final KfGroupArmyTeam team : teamSet) {
                if (team.getCreateCId() == cId) {
                    if (team.getCreatePLv() < player.getPlayerLevel()) {
                        team.setCreatePLv(player.getPlayerLevel());
                        wl = WorldLegionCache.getWorldLegionById(team.getWorldLegionId());
                        s = SerialCache.getValue(wl.getOwnerExpS(), player.getPlayerLevel());
                        team.setOwnerAddExp(s);
                    }
                    this.teamJoinInfo(doc, team, cId);
                }
            }
            for (final KfGroupArmyTeam team : teamSet) {
                if (team.getCreateCId() != cId) {
                    this.teamJoinInfo(doc, team, cId);
                }
            }
        }
        for (final KfGroupArmyTeam team : KfgzGroupTeamManager.getAllTeam(gzId)) {
            if (team.getForceId() != forceId) {
                continue;
            }
            if (team.getCreateCId() == cId) {
                continue;
            }
            if (teamSet.contains(team)) {
                continue;
            }
            this.teamJoinInfo(doc, team, cId);
        }
        doc.endArray();
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    private void teamJoinInfo(final JsonDocument doc, final KfGroupArmyTeam team, final int cId) {
        doc.startObject();
        doc.createElement("teamId", team.getTeamId());
        doc.createElement("teamName", team.getTeamName());
        doc.createElement("creatorName", team.getCreatePlayerName());
        doc.createElement("generalNum", team.defGList.size());
        doc.createElement("generalMaxNum", team.getMaxNum());
        doc.createElement("pic", team.getCreatePic());
        final int[] armyNumInfo = team.getTotalForces();
        doc.createElement("totalForces", armyNumInfo[0]);
        doc.createElement("totalMaxForces", armyNumInfo[1]);
        doc.createElement("lv", team.getCreatePLv());
        doc.createElement("teamExp", team.getOwnerAddExp() * team.getCurNum());
        boolean isCreator = false;
        if (team.getCreateCId() == cId) {
            isCreator = true;
        }
        doc.createElement("isCreator", isCreator);
        doc.createElement("creator", team.getCreatePLv());
        doc.startArray("generalList");
        final List<KfGeneralInfo> gList = new ArrayList<KfGeneralInfo>();
        for (final KfGeneralInfo gInfo : team.defGList) {
            gList.add(gInfo);
        }
        Collections.sort(gList, new Comparator<KfGeneralInfo>() {
            @Override
            public int compare(final KfGeneralInfo o1, final KfGeneralInfo o2) {
                return (o1.getCampArmy().getGeneralLv() > o2.getCampArmy().getGeneralLv()) ? -1 : 0;
            }
        });
        for (final KfGeneralInfo gInfo : gList) {
            this.memberJoinInfo(doc, gInfo);
        }
        doc.endArray();
        doc.endObject();
    }
    
    private void memberJoinInfo(final JsonDocument doc, final KfGeneralInfo gInfo) {
        doc.startObject();
        doc.createElement("cId", gInfo.getpInfo().getCompetitorId());
        doc.createElement("pName", gInfo.getpInfo().getPlayerName());
        doc.createElement("generalId", gInfo.getgId());
        doc.createElement("generalLv", gInfo.getCampArmy().getGeneralLv());
        doc.createElement("generalQuality", gInfo.getCampArmy().getQuality());
        doc.createElement("generalName", gInfo.getCampArmy().getGeneralName());
        doc.endObject();
    }
    
    @Override
    public byte[] createGroupTeam(final KfPlayerInfo player) {
        final int cId = player.getCompetitorId();
        final int gzId = player.getGzId();
        final int forceId = player.getForceId();
        if (player.getPlayerLevel() < 70) {
            return JsonBuilder.getJson(State.FAIL, "\u60a8\u7684\u7b49\u7ea7\u592a\u4f4e\u65e0\u53d1\u521b\u5efa\u96c6\u56e2\u519b");
        }
        final WorldLegion wl = WorldLegionCache.getWorldLegionById(1);
        final int maxNum = wl.getMax();
        try {
            this.teamlock.writeLock().lock();
            final boolean hasCreate = KfgzGroupTeamManager.hasPlayerCreateTeam(gzId, cId);
            if (hasCreate) {
                return JsonBuilder.getJson(State.FAIL, "\u60a8\u5df2\u7ecf\u521b\u5efa\u8fc7\u961f\u4f0d");
            }
            final boolean useGoldSuc = KfgzResChangeManager.consumeResource(cId, wl.getGoldInit(), "gold", "\u56fd\u6218\u96c6\u56e2\u519b");
            if (!useGoldSuc) {
                return JsonBuilder.getJson(State.FAIL, "\u6ca1\u6709\u8db3\u591f\u91d1\u5e01");
            }
            final KfGroupArmyTeam gteam = KfgzGroupTeamManager.createNewTeam(player, wl);
            final JsonDocument doc = new JsonDocument();
            doc.startObject();
            doc.createElement("gstate", 1);
            doc.endObject();
            KfgzMessageSender.sendMsgToForce(gzId, forceId, doc.toByte(), PushCommand.PUSH_KF_NEWGROUPTIKCET);
            KfgzGroupTeamManager.sendTeamChangeInfo(gzId, forceId);
            final String content = MessageFormatter.format("\u3010\u8de8\u670d\u56fd\u6218\u3011\u73a9\u5bb6{0}\u521b\u5efa\u4e86\u96c6\u56e2\u519b", new Object[] { player.getPlayerName() });
            KfgzMessageSender.sendChatToForce(gzId, forceId, content);
            return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
        }
        finally {
            this.teamlock.writeLock().unlock();
        }
    }
    
    @Override
    public byte[] getAddGroupTeamInfo(final KfPlayerInfo player, final int teamId) {
        final int gzId = player.getGzId();
        final int forceId = player.getForceId();
        final int cId = player.getCompetitorId();
        final KfTeam team = KfgzTeamManager.getKfTeam(teamId, gzId);
        if (!(team instanceof KfGroupArmyTeam)) {
            return JsonBuilder.getJson(State.FAIL, "");
        }
        final List<Integer> list = new ArrayList<Integer>();
        for (final KfGeneralInfo gInfo : player.getgMap().values()) {
            if (gInfo.getState() == 1 && gInfo.getCampArmy().getArmyHp() > gInfo.getCampArmy().getArmyHpOrg() / 20 && gInfo.getTeam() instanceof KfCity) {
                list.add(gInfo.getgId());
            }
        }
        final KfGroupArmyTeam kfgteam = (KfGroupArmyTeam)team;
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("createName", kfgteam.getCreatePlayerName());
        doc.createElement("gIds", list);
        final WorldLegion wl = WorldLegionCache.getWorldLegionById(kfgteam.getWorldLegionId());
        final int foodAdd = SerialCache.getValue(wl.getPFoodS(), player.getPlayerLevel());
        doc.createElement("foodAdd", foodAdd);
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public byte[] addToGroupTeam(final KfPlayerInfo player, final int teamId, final String gIds) {
        final int gzId = player.getGzId();
        final int forceId = player.getForceId();
        final int cId = player.getCompetitorId();
        final KfTeam team = KfgzTeamManager.getKfTeam(teamId, gzId);
        if (!(team instanceof KfGroupArmyTeam)) {
            return JsonBuilder.getJson(State.FAIL, "");
        }
        if (team.getForceId() != player.getForceId()) {
            return JsonBuilder.getJson(State.FAIL, "");
        }
        final KfGroupArmyTeam kfgteam = (KfGroupArmyTeam)team;
        if (!kfgteam.isActive()) {
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
                return JsonBuilder.getJson(State.FAIL, "\u53c2\u6570\u9519\u8bef");
            }
            gIdList.add(gInfo);
        }
        try {
            this.teamlock.writeLock().lock();
            for (final KfGeneralInfo gInfo2 : gIdList) {
                final KfTeam oldTeam = gInfo2.getTeam();
                if (oldTeam instanceof KfCity) {
                    try {
                        kfgteam.teamLock.writeLock().lock();
                        oldTeam.teamLock.writeLock().lock();
                        if (kfgteam.getCurNum() >= kfgteam.getMaxNum()) {
                            return JsonBuilder.getJson(State.FAIL, "\u961f\u4f0d\u5df2\u6ee1");
                        }
                        if (gInfo2.team != oldTeam || gInfo2.getState() != 1) {
                            continue;
                        }
                        gInfo2.team.removeGeneral(gInfo2);
                        kfgteam.addGeneral(gInfo2);
                        gInfo2.pushDirectMove();
                    }
                    finally {
                        oldTeam.teamLock.writeLock().unlock();
                        kfgteam.teamLock.writeLock().unlock();
                    }
                    oldTeam.teamLock.writeLock().unlock();
                    kfgteam.teamLock.writeLock().unlock();
                }
            }
            KfgzGroupTeamManager.sendTeamChangeInfo(gzId, forceId);
        }
        finally {
            this.teamlock.writeLock().unlock();
        }
        this.teamlock.writeLock().unlock();
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Override
    public byte[] dismissGroupTeam(final KfPlayerInfo player, final int teamId) {
        final int gzId = player.getGzId();
        final int forceId = player.getForceId();
        final int cId = player.getCompetitorId();
        final KfTeam team = KfgzTeamManager.getKfTeam(teamId, gzId);
        if (!(team instanceof KfGroupArmyTeam)) {
            return JsonBuilder.getJson(State.FAIL, "");
        }
        final KfGroupArmyTeam kfgteam = (KfGroupArmyTeam)team;
        if (kfgteam.getCreateCId() != cId) {
            return JsonBuilder.getJson(State.FAIL, "");
        }
        try {
            this.teamlock.writeLock().lock();
            kfgteam.dismissTeam();
            KfgzGroupTeamManager.sendTeamChangeInfo(gzId, forceId);
            final Set<KfGroupArmyTeam> gSet = KfgzGroupTeamManager.getAllTeamByForceId(gzId, forceId);
            if (gSet != null && gSet.size() > 0) {
                final JsonDocument doc = new JsonDocument();
                doc.startObject();
                doc.createElement("gstate", 2);
                doc.endObject();
                KfgzMessageSender.sendMsgToForce(gzId, forceId, doc.toByte(), PushCommand.PUSH_KF_NEWGROUPTIKCET);
            }
        }
        finally {
            this.teamlock.writeLock().unlock();
        }
        this.teamlock.writeLock().unlock();
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Override
    public byte[] leaveGroupTeam(final KfPlayerInfo player, final int unusedteamId, final int gId) {
        final int gzId = player.getGzId();
        final int forceId = player.getForceId();
        final int cId = player.getCompetitorId();
        final KfGeneralInfo gInfo = player.getgMap().get(gId);
        if (gInfo == null) {
            return JsonBuilder.getJson(State.FAIL, "\u53c2\u6570\u9519\u8bef");
        }
        final KfTeam team = gInfo.getTeam();
        if (!(team instanceof KfGroupArmyTeam)) {
            return JsonBuilder.getJson(State.FAIL, "");
        }
        final KfGroupArmyTeam kfgteam = (KfGroupArmyTeam)team;
        try {
            this.teamlock.writeLock().lock();
            if (gInfo.getTeam() != kfgteam) {
                return JsonBuilder.getJson(State.FAIL, "\u6b66\u5c06\u4e0d\u5728\u8be5\u961f\u4f0d\u4e2d");
            }
            kfgteam.leaveGeneral(gInfo);
            KfgzGroupTeamManager.sendTeamChangeInfo(gzId, forceId);
        }
        finally {
            this.teamlock.writeLock().unlock();
        }
        this.teamlock.writeLock().unlock();
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Override
    public byte[] kickOutGroupTeam(final KfPlayerInfo player, final int teamId, final int toCId, final int toGId) {
        final int gzId = player.getGzId();
        final int forceId = player.getForceId();
        final int cId = player.getCompetitorId();
        final KfTeam team = KfgzTeamManager.getKfTeam(teamId, gzId);
        if (!(team instanceof KfGroupArmyTeam)) {
            return JsonBuilder.getJson(State.FAIL, "");
        }
        final KfGroupArmyTeam kfgteam = (KfGroupArmyTeam)team;
        if (kfgteam.getCreateCId() != cId) {
            return JsonBuilder.getJson(State.FAIL, "");
        }
        final KfGeneralInfo gInfo = KfgzPlayerManager.getPlayerByCId(toCId).getgMap().get(toGId);
        if (gInfo == null) {
            return JsonBuilder.getJson(State.FAIL, "\u53c2\u6570\u9519\u8bef");
        }
        try {
            this.teamlock.writeLock().lock();
            if (gInfo.getTeam() != kfgteam) {
                return JsonBuilder.getJson(State.FAIL, "\u6b66\u5c06\u4e0d\u5728\u8be5\u961f\u4f0d\u4e2d");
            }
            kfgteam.leaveGeneral(gInfo);
            KfgzGroupTeamManager.sendTeamChangeInfo(gzId, forceId);
        }
        finally {
            this.teamlock.writeLock().unlock();
        }
        this.teamlock.writeLock().unlock();
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Override
    public byte[] doBattleGroupTeam(final KfPlayerInfo player, final int unsedteamId, final int teamBatType, final int toTeamId) {
        final int gzId = player.getGzId();
        final int forceId = player.getForceId();
        final int cId = player.getCompetitorId();
        final KfTeam team = KfgzGroupTeamManager.getPlayerGroupArmyInfo(gzId, cId);
        if (!(team instanceof KfGroupArmyTeam)) {
            return JsonBuilder.getJson(State.FAIL, "");
        }
        final KfGroupArmyTeam kfgteam = (KfGroupArmyTeam)team;
        if (kfgteam.getCreateCId() != cId) {
            return JsonBuilder.getJson(State.FAIL, "");
        }
        final KfTeam toTeam = KfgzTeamManager.getKfTeam(toTeamId, gzId);
        if (toTeam == null || toTeam.battle == null || toTeam.battle.getBattleState() != 2) {
            return JsonBuilder.getJson(State.FAIL, "\u6218\u6597\u5df2\u7ed3\u675f");
        }
        final KfCity tocity = (KfCity)toTeam;
        if (tocity.isCaptial()) {
            return JsonBuilder.getJson(State.FAIL, "\u4e0d\u80fd\u5bf9\u8be5\u57ce\u5e02\u53d1\u52a8\u6218\u6597");
        }
        boolean hasGeneralInToTeam = false;
        for (final KfGeneralInfo gInfo : player.getgMap().values()) {
            if (gInfo.getTeam() == toTeam) {
                hasGeneralInToTeam = true;
                break;
            }
        }
        if (!hasGeneralInToTeam) {
            return JsonBuilder.getJson(State.FAIL, "\u6218\u573a\u6ca1\u6709\u60a8\u7684\u6b66\u5c06");
        }
        final WorldLegion wl = WorldLegionCache.getWorldLegionById(kfgteam.getWorldLegionId());
        final boolean useGoldSuc = KfgzResChangeManager.consumeResource(cId, kfgteam.getCurNum() * wl.getGoldDeploy(), "gold", "\u56fd\u6218\u96c6\u56e2\u519b\u5f00\u6218");
        if (!useGoldSuc) {
            return JsonBuilder.getJson(State.FAIL, "\u6ca1\u6709\u8db3\u591f\u7684\u91d1\u5e01");
        }
        try {
            this.teamlock.writeLock().lock();
            final int pExp = SerialCache.getValue(wl.getOwnerExpS(), player.getPlayerLevel()) * kfgteam.getCurNum();
            KfgzResChangeManager.addResource(cId, pExp, "exp", "\u56fd\u6218\u96c6\u56e2\u519b\u5f00\u6253");
            kfgteam.doBattle(toTeam, teamBatType);
            KfgzGroupTeamManager.sendTeamChangeInfo(gzId, forceId);
            final Set<KfGroupArmyTeam> gSet = KfgzGroupTeamManager.getAllTeamByForceId(gzId, forceId);
            if (gSet != null && gSet.size() > 0) {
                final JsonDocument doc = new JsonDocument();
                doc.startObject();
                doc.createElement("gstate", 2);
                doc.endObject();
                KfgzMessageSender.sendMsgToForce(gzId, forceId, doc.toByte(), PushCommand.PUSH_KF_NEWGROUPTIKCET);
            }
            final String content = MessageFormatter.format("\u3010\u8de8\u670d\u56fd\u6218\u3011\u73a9\u5bb6{0}\u53d1\u52a8\u4e86\u96c6\u56e2\u519b", new Object[] { player.getPlayerName() });
            KfgzMessageSender.sendChatToForce(gzId, forceId, content);
            final JsonDocument doc2 = new JsonDocument();
            doc2.startObject();
            doc2.createElement("exp", pExp);
            doc2.endObject();
            return JsonBuilder.getJson(State.SUCCESS, doc2.toByte());
        }
        finally {
            this.teamlock.writeLock().unlock();
        }
    }
    
    @Override
    public byte[] getGroupTeamBatCost(final KfPlayerInfo player, final int unUsedteamId) {
        final int gzId = player.getGzId();
        final int forceId = player.getForceId();
        final int cId = player.getCompetitorId();
        final KfTeam team = KfgzGroupTeamManager.getPlayerGroupArmyInfo(gzId, cId);
        if (!(team instanceof KfGroupArmyTeam)) {
            return JsonBuilder.getJson(State.FAIL, "");
        }
        final KfGroupArmyTeam kfgteam = (KfGroupArmyTeam)team;
        if (kfgteam.getCreateCId() != cId) {
            return JsonBuilder.getJson(State.FAIL, "");
        }
        final WorldLegion wl = WorldLegionCache.getWorldLegionById(kfgteam.getWorldLegionId());
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("teamId", kfgteam.getTeamId());
        doc.createElement("gold", kfgteam.getCurNum() * wl.getGoldDeploy());
        doc.createElement("curNum", kfgteam.getCurNum());
        doc.createElement("maxNum", kfgteam.getMaxNum());
        doc.createElement("teamExp", kfgteam.getOwnerAddExp() * kfgteam.getCurNum());
        final int[] totalForceInfo = kfgteam.getTotalForces();
        doc.createElement("totalForces", totalForceInfo[0]);
        doc.createElement("totalMaxForces", totalForceInfo[1]);
        doc.createElement("order", kfgteam.isOrder());
        final Chargeitem orderCi = ChargeitemCache.getById(72);
        final int cost = (int)Math.ceil((1.0 - totalForceInfo[0] * 1.0 / totalForceInfo[1]) * orderCi.getCost() * kfgteam.getCurNum());
        doc.createElement("orderGold", cost);
        final Chargeitem ci = ChargeitemCache.getById(63);
        doc.createElement("inspireCost", ci.getCost());
        doc.createElement("inspireEffect", kfgteam.getInspireEffect() * 100.0);
        final int addExp = 500 * ci.getCost();
        doc.createElement("addExp", addExp);
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public byte[] groupTeamInspire(final KfPlayerInfo player, final int unUSedteamId) {
        final int gzId = player.getGzId();
        final int forceId = player.getForceId();
        final int cId = player.getCompetitorId();
        final KfTeam team = KfgzGroupTeamManager.getPlayerGroupArmyInfo(gzId, cId);
        if (!(team instanceof KfGroupArmyTeam)) {
            return JsonBuilder.getJson(State.FAIL, "");
        }
        final KfGroupArmyTeam kfgteam = (KfGroupArmyTeam)team;
        if (kfgteam.getCreateCId() != cId) {
            return JsonBuilder.getJson(State.FAIL, "");
        }
        final WorldLegion wl = WorldLegionCache.getWorldLegionById(kfgteam.getWorldLegionId());
        if (kfgteam.getInspireEffect() > 0.0) {
            return JsonBuilder.getJson(State.FAIL, "\u5df2\u7ecf\u9f13\u821e\u8fc7");
        }
        final Chargeitem ci = ChargeitemCache.getById(63);
        final boolean useGoldSuc = KfgzResChangeManager.consumeResource(cId, ci.getCost(), "gold", "\u56fd\u6218\u96c6\u56e2\u519b\u9f13\u821e");
        if (!useGoldSuc) {
            return JsonBuilder.getJson(State.FAIL, "\u6ca1\u6709\u8db3\u591f\u7684\u91d1\u5e01");
        }
        final int addExp = 500 * ci.getCost();
        kfgteam.setInspireEffect(ci.getParam());
        KfgzResChangeManager.addResource(cId, addExp, "exp", "\u56fd\u6218\u96c6\u56e2\u519b\u9f13\u821e");
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("teamId", kfgteam.getTeamId());
        doc.createElement("gold", kfgteam.getCurNum() * wl.getGoldDeploy());
        doc.createElement("curNum", kfgteam.getCurNum());
        doc.createElement("maxNum", kfgteam.getMaxNum());
        doc.createElement("teamExp", kfgteam.getOwnerAddExp() * kfgteam.getCurNum());
        doc.createElement("inspireCost", ci.getCost());
        doc.createElement("inspireEffect", kfgteam.getInspireEffect() * 100.0);
        doc.createElement("addExp", addExp);
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public byte[] groupTeamOrder(final KfPlayerInfo player, final int unsedteamId) {
        final int gzId = player.getGzId();
        final int forceId = player.getForceId();
        final int cId = player.getCompetitorId();
        final KfTeam team = KfgzGroupTeamManager.getPlayerGroupArmyInfo(gzId, cId);
        if (!(team instanceof KfGroupArmyTeam)) {
            return JsonBuilder.getJson(State.FAIL, "");
        }
        final KfGroupArmyTeam kfgteam = (KfGroupArmyTeam)team;
        if (kfgteam.getCreateCId() != cId) {
            return JsonBuilder.getJson(State.FAIL, "");
        }
        final WorldLegion wl = WorldLegionCache.getWorldLegionById(kfgteam.getWorldLegionId());
        if (kfgteam.isOrder()) {
            return JsonBuilder.getJson(State.FAIL, "\u5df2\u7ecf\u53f7\u4ee4\u8fc7");
        }
        if (kfgteam.getCurNum() <= 0) {
            return JsonBuilder.getJson(State.FAIL, "\u4eba\u6570\u4e0d\u591f");
        }
        final int[] totalForceInfo = kfgteam.getTotalForces();
        if (totalForceInfo[0] >= totalForceInfo[1]) {
            return JsonBuilder.getJson(State.FAIL, "\u563f\u563f\uff0c\u5927\u5bb6\u90fd\u6ee1\u5175");
        }
        final Chargeitem ci = ChargeitemCache.getById(72);
        final int cost = (int)Math.ceil((1.0 - totalForceInfo[0] * 1.0 / totalForceInfo[1]) * ci.getCost() * kfgteam.getCurNum());
        final boolean useGoldSuc = KfgzResChangeManager.consumeResource(cId, cost, "gold", "\u56fd\u6218\u96c6\u56e2\u519b\u5f00\u6218");
        if (!useGoldSuc) {
            return JsonBuilder.getJson(State.FAIL, "\u6ca1\u6709\u8db3\u591f\u7684\u91d1\u5e01");
        }
        final int addExp = (int)(ci.getParam() * cost);
        KfgzResChangeManager.addResource(cId, addExp, "exp", "\u56fd\u6218\u96c6\u56e2\u519b\u9f13\u821e");
        kfgteam.setOrder(true);
        kfgteam.doOrder();
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("teamId", kfgteam.getTeamId());
        doc.createElement("order", kfgteam.isOrder());
        doc.createElement("addOrderExp", addExp);
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public byte[] closeGroupTeam(final KfPlayerInfo player) {
        final int cId = player.getCompetitorId();
        final int gzId = player.getGzId();
        final int forceId = player.getForceId();
        KfgzGroupTeamManager.unfocusOnGroupTeam(gzId, cId, forceId);
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
}
