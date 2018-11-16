package com.reign.gcld.score.service;

import org.springframework.stereotype.*;
import com.reign.gcld.activity.service.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.score.dao.*;
import com.reign.gcld.activity.dao.*;
import com.reign.gcld.rank.service.*;
import com.reign.gcld.sdata.cache.*;
import com.reign.gcld.player.service.*;
import com.reign.gcld.battle.dao.*;
import com.reign.gcld.player.dao.*;
import com.reign.gcld.common.log.*;
import com.reign.gcld.player.dto.*;
import com.reign.gcld.common.*;
import com.reign.framework.json.*;
import com.reign.gcld.world.common.*;
import com.reign.gcld.score.domain.*;
import java.util.*;
import com.reign.gcld.player.domain.*;
import com.reign.gcld.sdata.domain.*;

@Component("scoreRankService")
public class ScoreRankService implements IScoreRankService
{
    private static final Logger errorLog;
    @Autowired
    private IActivityService activityService;
    @Autowired
    private IPlayerScoreRankDao playerScoreRankDao;
    @Autowired
    private IPlayerDragonDao playerDragonDao;
    @Autowired
    private TpCoTnumCache tpCoTnumCache;
    @Autowired
    private TpCoLrankingCache tpCoLrankingCache;
    @Autowired
    private IRankService rankService;
    @Autowired
    private IPlayerDao playerDao;
    @Autowired
    private TpCoTrewardCache tpCoTrewardCache;
    @Autowired
    private IPlayerService playerService;
    @Autowired
    private IPlayerBattleAttributeDao playerBattleAttributeDao;
    @Autowired
    private IPlayerAttributeDao playerAttributeDao;
    @Autowired
    private IPlayerResourceDao playerResourceDao;
    
    static {
        errorLog = CommonLog.getLog(ScoreRankService.class);
    }
    
    @Override
    public byte[] getRankInfo(final PlayerDto playerDto) {
        final int playerId = playerDto.playerId;
        if (playerDto.cs[63] != '1') {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10020);
        }
        final int boxNum = this.playerDragonDao.getBoxNumByPlayerId(playerId);
        PlayerScoreRank psr = this.playerScoreRankDao.read(playerId);
        if (psr == null) {
            this.activityService.openPlayerScoreRank(playerId);
            psr = this.playerScoreRankDao.read(playerId);
        }
        final int score = psr.getScore2();
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.startArray("lvs");
        int lv = 0;
        int count = 0;
        int needNum = 0;
        boolean flag_needGold = false;
        for (int size = this.tpCoTnumCache.getModels().size(), i = 1; i <= size; ++i) {
            final TpCoTnum tct = (TpCoTnum)this.tpCoTnumCache.get((Object)i);
            final int nNum = tct.getCityOcc();
            final int rNum = tct.getTNum();
            doc.startObject();
            doc.createElement("num", nNum);
            doc.createElement("rewardNum", rNum);
            doc.endObject();
            ++count;
            if (score >= nNum) {
                lv = count;
            }
            if (!flag_needGold && score < nNum) {
                needNum = nNum - score;
                flag_needGold = true;
            }
        }
        doc.endArray();
        doc.startArray("ranks");
        final List<RankData> rdList = this.rankService.getScoreRank().getRankList(playerDto.forceId);
        int index = 1;
        for (final RankData rd : rdList) {
            if (index > 100) {
                break;
            }
            doc.startObject();
            doc.createElement("rank", (index++));
            doc.createElement("playerId", rd.playerId);
            final Player player = this.playerDao.read(rd.playerId);
            doc.createElement("playerLv", player.getPlayerLv());
            doc.createElement("playerName", player.getPlayerName());
            doc.createElement("score", rd.value);
            doc.endObject();
        }
        doc.endArray();
        doc.createElement("score", score);
        doc.createElement("boxNum", boxNum);
        doc.createElement("lv", lv + 1);
        doc.createElement("needNum", needNum);
        doc.createElement("maxNum", this.tpCoTnumCache.getMaxNum());
        doc.createElement("occupy", psr.getOccupy());
        doc.createElement("assist", psr.getAssist());
        doc.createElement("cheer", psr.getCheer());
        doc.createElement("occupyScore", 5);
        doc.createElement("assistScore", 2);
        doc.createElement("cheerScore", 1);
        final int lastRank = psr.getLastRank();
        doc.createElement("lastRank", lastRank);
        doc.createElement("lastExp", this.tpCoLrankingCache.getRewardExp(lastRank));
        final int rank = this.rankService.getScoreRank().getRank(1, playerId, playerDto.forceId);
        doc.createElement("rank", rank);
        int needScore = 0;
        if (rank > 1) {
            final RankData rd2 = this.rankService.getScoreRank().getRankNum(playerDto.forceId, rank - 2);
            if (rd2 != null) {
                needScore = rd2.value - psr.getScore2();
                if (needScore <= 0) {
                    needScore = 1;
                }
            }
            else {
                ScoreRankService.errorLog.error("class:ScoreRankService#method:getRankInfo#playerId:" + playerId + "#rank:" + rank);
                needScore = 1;
            }
        }
        doc.createElement("needScore", needScore);
        doc.createElement("received", (psr.getReceived() != 0 || lastRank <= 0) ? 1 : 0);
        doc.createElement("exp", this.tpCoLrankingCache.getRewardExp(rank));
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public byte[] getBoxReward(final PlayerDto playerDto) {
        final int playerId = playerDto.playerId;
        final int boxNum = this.playerDragonDao.getBoxNumByPlayerId(playerId);
        if (boxNum <= 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.SCORE_RANK_NO_BOX);
        }
        this.playerDragonDao.useBox(playerId);
        final TpCoTreward tct = this.tpCoTrewardCache.getTpCoTreward();
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        final int num = tct.getNum();
        final int type = tct.getType();
        if (1 == type) {
            this.playerService.updateExpAndPlayerLevel(playerId, num, "\u653b\u57ce\u79ef\u5206\u699c\u5b9d\u7bb1\u5956\u52b1\u7ecf\u9a8c");
        }
        else if (2 == type) {
            this.playerBattleAttributeDao.addVip3PhantomCount(playerId, num, "\u4f7f\u7528\u653b\u57ce\u79ef\u5206\u699c\u5b9d\u7bb1\u83b7\u5f97\u514d\u8d39\u501f\u5175\u6b21\u6570");
        }
        else if (3 == type) {
            this.playerAttributeDao.addRecruitToken(playerId, num, "\u4f7f\u7528\u653b\u57ce\u79ef\u5206\u699c\u5b9d\u7bb1\u83b7\u5f97\u52df\u5175\u4ee4");
        }
        else {
            this.playerResourceDao.addFoodIgnoreMax(playerId, num, "\u4f7f\u7528\u653b\u57ce\u79ef\u5206\u699c\u5b9d\u7bb1\u83b7\u53d6\u7cae\u98df");
        }
        doc.createElement("type", this.getDisplayType(type));
        doc.createElement("num", num);
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public byte[] getRankReward(final PlayerDto playerDto) {
        final int playerId = playerDto.playerId;
        if (playerDto.cs[63] != '1') {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10020);
        }
        PlayerScoreRank psr = this.playerScoreRankDao.read(playerId);
        if (psr == null) {
            this.activityService.openPlayerScoreRank(playerId);
            psr = this.playerScoreRankDao.read(playerId);
        }
        final int lastRank = psr.getLastRank();
        if (lastRank <= 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.SCORE_RANK_NO_RANK);
        }
        if (1 == psr.getReceived()) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.SCORE_RANK_RECEIVED);
        }
        this.playerScoreRankDao.received(playerId);
        final int exp = this.tpCoLrankingCache.getRewardExp(lastRank);
        this.playerService.updateExpAndPlayerLevel(playerId, exp, "\u653b\u57ce\u79ef\u5206\u699c\u5956\u52b1\u7ecf\u9a8c");
        return JsonBuilder.getJson(State.SUCCESS, JsonBuilder.getSimpleJson("exp", exp));
    }
    
    private int getDisplayType(final int type) {
        if (1 == type) {
            return 6;
        }
        if (2 == type) {
            return 12;
        }
        if (3 == type) {
            return 9;
        }
        return 3;
    }
}
