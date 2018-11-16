package com.reign.gcld.nation.service;

import org.springframework.stereotype.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.rank.dao.*;
import com.reign.gcld.nation.dao.*;
import com.reign.gcld.rank.service.*;
import com.reign.gcld.player.service.*;
import com.reign.gcld.player.dao.*;
import com.reign.gcld.sdata.cache.*;
import com.reign.gcld.player.dto.*;
import com.reign.framework.json.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.gcld.rank.domain.*;
import com.reign.gcld.common.*;
import com.reign.gcld.nation.domain.*;
import com.reign.gcld.player.domain.*;
import java.util.concurrent.*;
import com.reign.gcld.battle.common.*;
import java.util.*;

@Component("protectService")
public class ProtectService implements IProtectService
{
    @Autowired
    private IPlayerDao playerDao;
    @Autowired
    private IDataGetter dataGetter;
    @Autowired
    private IForceInfoDao forceInfoDao;
    @Autowired
    private IPlayerAttributeDao playerAttributeDao;
    @Autowired
    private IPlayerPRankDao playerPRankDao;
    @Autowired
    private IRankService rankService;
    @Autowired
    private CdExamsCache cdExamsCache;
    @Autowired
    private IPlayerService playerService;
    @Autowired
    private IPlayerResourceDao playerResourceDao;
    @Autowired
    private KingdomTaskRankingCache kingdomTaskRankingCache;
    
    @Override
    public byte[] getProtectInfo(final PlayerDto playerDto) {
        if (playerDto.cs[10] != '1') {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10020);
        }
        final int playerId = playerDto.playerId;
        final int forceId = playerDto.forceId;
        final ForceInfo fi = this.forceInfoDao.read(forceId);
        final int cityId = fi.getPCityId();
        if (cityId <= 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.PROTECT_TASK_NO_JOIN);
        }
        final boolean haveReward = this.havaProtectReward(this.playerDao.read(playerId));
        final WorldCity wc = (WorldCity)this.dataGetter.getWorldCityCache().get((Object)cityId);
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("cityId", cityId);
        doc.createElement("cityName", wc.getName());
        doc.createElement("defForceId", fi.getPForceId());
        doc.createElement("killNum", this.dataGetter.getRankService().getPRank().getValue(forceId, playerId));
        final int id = fi.getPId();
        doc.createElement("targetNum", ((CdExams)this.dataGetter.getCdExamsCache().get((Object)id)).getWinConP());
        final int rank = this.dataGetter.getRankService().getPRank().getRank(1, playerId, forceId);
        final int lvAfter = this.kingdomTaskRankingCache.getTaskRankingLv(rank, 0);
        final String titlePic = this.kingdomTaskRankingCache.getTitlePic(lvAfter, 0);
        doc.createElement("rank", rank);
        doc.createElement("title", titlePic);
        doc.createElement("isWin", fi.getPWin());
        doc.createElement("haveReward", haveReward ? 1 : 0);
        final ForceInfo forceInfo = this.forceInfoDao.read(forceId);
        final ForceInfo forceInfoOther = this.forceInfoDao.read(forceInfo.getPForceId());
        if (forceInfoOther.getTryEndTime() != null && forceInfoOther.getTryEndTime().getTime() > System.currentTimeMillis() && forceInfoOther.getTryWin() == 0 && forceInfo.getPWin() == 0 && forceInfoOther.getStage() == 3) {
            doc.createElement("cd", TimeUtil.now2specMs(forceInfoOther.getTryEndTime().getTime()));
        }
        else {
            doc.createElement("cd", 0);
        }
        final int rankExp = this.cdExamsCache.getPRankingExp(id, rank);
        final int rankIron = this.cdExamsCache.getPRankingIron(id, rank);
        doc.startArray("rewards");
        for (int i = 1; i <= 2; ++i) {
            doc.startObject();
            if (1 == i) {
                doc.createElement("type", 10);
                doc.createElement("value", rankExp);
            }
            else {
                doc.createElement("type", 4);
                doc.createElement("value", rankIron);
            }
            doc.endObject();
        }
        doc.endArray();
        doc.createElement("totalNum", this.dataGetter.getRankService().getPRank().getTotalNum(forceId));
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public byte[] getProtectReward(final PlayerDto playerDto) {
        if (playerDto.cs[10] != '1') {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10020);
        }
        final int forceId = playerDto.forceId;
        final ForceInfo fi = this.forceInfoDao.read(forceId);
        if (fi.getPWin() != 1) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.PROTECT_TASK_NOT_WIN);
        }
        final int playerId = playerDto.playerId;
        final PlayerPRank ppr = this.playerPRankDao.read(playerId);
        if (1 == ppr.getReceived()) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.PROTECT_TASK_RECEIVED);
        }
        if (ppr.getNum() <= 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.PROTECT_TASK_NO_JOIN);
        }
        final int rank = this.rankService.getPRank().getRank(1, playerId, forceId);
        if (rank <= 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.PROTECT_TASK_LOW_RANK);
        }
        this.playerPRankDao.received(playerId);
        final int id = fi.getPId();
        final int rankExp = this.cdExamsCache.getPRankingExp(id, rank);
        final int rankIron = this.cdExamsCache.getPRankingIron(id, rank);
        this.playerService.updateExpAndPlayerLevel(playerId, rankExp, "\u4fdd\u62a4\u86ee\u738b\u6392\u540d\u5956\u52b1\u7ecf\u9a8c");
        this.playerResourceDao.addIronIgnoreMax(playerId, rankIron, "\u4fdd\u62a4\u86ee\u738b\u6392\u540d\u83b7\u53d6\u9554\u94c1", true);
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.startArray("rewards");
        for (int i = 1; i <= 2; ++i) {
            doc.startObject();
            if (1 == i) {
                doc.createElement("type", 10);
                doc.createElement("value", rankExp);
            }
            else {
                doc.createElement("type", 4);
                doc.createElement("value", rankIron);
            }
            doc.endObject();
        }
        doc.endArray();
        doc.endObject();
        final JsonDocument doc2 = new JsonDocument();
        doc2.startObject();
        doc2.appendJson(this.dataGetter.getProtectService().getProtectTaskInfo(playerId));
        doc2.endObject();
        Players.push(playerId, PushCommand.PUSH_UPDATE, doc2.toByte());
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public byte[] getProtectTaskInfo(final int playerId) {
        final Player player = this.playerDao.read(playerId);
        final int forceId = player.getForceId();
        final JsonDocument doc = new JsonDocument();
        doc.startObject("protectTasks");
        if (this.playerAttributeDao.getFunctionId(playerId).toCharArray()[10] == '1') {
            final ForceInfo forceInfo = this.forceInfoDao.read(forceId);
            final ForceInfo forceInfoOther = this.forceInfoDao.read(forceInfo.getPForceId());
            if (forceInfoOther != null && forceInfoOther.getTryEndTime() != null && forceInfoOther.getTryEndTime().getTime() > System.currentTimeMillis() && forceInfoOther.getTryWin() == 0 && forceInfo.getPWin() == 0 && forceInfoOther.getStage() == 3) {
                doc.createElement("cd", TimeUtil.now2specMs(forceInfoOther.getTryEndTime().getTime()));
                doc.createElement("state", 0);
            }
            else if (forceInfo.getPWin() == 1) {
                doc.createElement("state", 2);
            }
            else {
                doc.createElement("state", 1);
            }
            final int cityId = forceInfo.getPCityId();
            final WorldCity wc = (WorldCity)this.dataGetter.getWorldCityCache().get((Object)cityId);
            doc.createElement("cityId", cityId);
            doc.createElement("cityName", (wc == null) ? "" : wc.getName());
            doc.createElement("hasReward", this.havaProtectReward(player));
        }
        doc.endObject();
        return doc.toByte();
    }
    
    @Override
    public byte[] getManWangLingInfo() {
        final long now = System.currentTimeMillis();
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.startArray("protectTasks");
        final ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, ManWangLingObj>> map = ManWangLingManager.getInstance().manWangLingObjMap;
        for (final ConcurrentHashMap<Integer, ManWangLingObj> map2 : map.values()) {
            final ManWangLingObj obj = map2.get(2);
            if (obj != null && obj.expireTime > now) {
                doc.startObject();
                doc.createElement("cityId", obj.targetCityId);
                doc.createElement("forceId", obj.fromForceId);
                doc.endObject();
            }
        }
        doc.endArray();
        doc.endObject();
        return doc.toByte();
    }
    
    @Override
    public void openPRank(final int playerId) {
        PlayerPRank ppr = this.playerPRankDao.read(playerId);
        if (ppr == null) {
            ppr = new PlayerPRank();
            ppr.setPlayerId(playerId);
            ppr.setNum(0);
            ppr.setReceived(1);
            this.playerPRankDao.create(ppr);
        }
    }
    
    @Override
    public void pushPTaskResult(final int forceId, final boolean result) {
        final byte[] send = JsonBuilder.getSimpleJson("pTaskSuccess", result ? 1 : 0);
        for (final PlayerDto dto : Players.getAllPlayerByForceId(forceId)) {
            if (dto.cs[10] == '1') {
                Players.push(dto.playerId, PushCommand.PUSH_UPDATE, send);
            }
        }
    }
    
    private boolean havaProtectReward(final Player player) {
        final ForceInfo fi = this.forceInfoDao.read(player.getForceId());
        if (fi.getPWin() != 1) {
            return false;
        }
        final PlayerPRank ppr = this.playerPRankDao.read(player.getPlayerId());
        if (1 == ppr.getReceived()) {
            return false;
        }
        final int rank = this.dataGetter.getRankService().getPRank().getRank(1, player.getPlayerId(), player.getForceId());
        return rank > 0;
    }
}
