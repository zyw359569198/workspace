package com.reign.kf.comm.entity.kfwd.response;

import java.io.*;
import java.util.*;
import com.reign.kfwd.constants.*;
import org.codehaus.jackson.annotate.*;
import com.reign.kfwd.dto.*;

@JsonAutoDetect
public class KfwdRTMatchInfo implements Serializable
{
    private static final long serialVersionUID = 1L;
    private int scheduleId;
    private int matchId;
    private int competitorId1;
    private int competitorId2;
    private int inspire1;
    private int inspire2;
    private int res;
    private long historyRes1;
    private long historyRes2;
    private int round;
    private int showround;
    private int sRound;
    private int showSRound;
    private long nextSRoundCD;
    private long nextShowSRoundCD;
    private String lastReport;
    private Date roundBattleTime;
    private Date lastRoundBattleTime;
    private int score;
    private int lastScore;
    private int ticket;
    private int lastTicket;
    private int p1score;
    private int p1Ranking;
    private int p2score;
    private int p2Ranking;
    
    @JsonIgnore
    public int getLastWinRoundNum(final int competitorId) {
        int res = 0;
        if (competitorId == this.competitorId1) {
            final int[] his = KfwdConstantsAndMethod.getLastWinInfo(this.historyRes1);
            int t = 0;
            int[] array;
            for (int length = (array = his).length, i = 0; i < length; ++i) {
                final int r = array[i];
                if (t >= this.round - 1) {
                    break;
                }
                ++t;
                if (r == 1) {
                    ++res;
                }
            }
        }
        else if (competitorId == this.competitorId2) {
            final int[] his = KfwdConstantsAndMethod.getLastWinInfo(this.historyRes2);
            int t = 0;
            int[] array2;
            for (int length2 = (array2 = his).length, j = 0; j < length2; ++j) {
                final int r = array2[j];
                if (t >= this.round - 1) {
                    break;
                }
                ++t;
                if (r == 1) {
                    ++res;
                }
            }
        }
        return res;
    }
    
    @JsonIgnore
    public int getLastTicketNum(final int competitorId) {
        if (competitorId == this.competitorId1) {
            return this.ticket;
        }
        if (competitorId == this.competitorId2) {
            return this.ticket;
        }
        return this.ticket;
    }
    
    @JsonIgnore
    public boolean isLastRoundWin(final int competitorId) {
        if (this.round <= 1) {
            return false;
        }
        if (competitorId == this.competitorId1) {
            final WdLastRoundInfo info = KfwdConstantsAndMethod.getLastRoundInfo(this.historyRes1);
            final int[] res = KfwdConstantsAndMethod.getBattleResByRes(info.getBattleRes());
            final boolean isAtt = info.isAttack();
            if (isAtt) {
                return res[2] == 1;
            }
            return res[2] == 2;
        }
        else {
            if (competitorId != this.competitorId2) {
                return false;
            }
            final WdLastRoundInfo info = KfwdConstantsAndMethod.getLastRoundInfo(this.historyRes2);
            final int[] res = KfwdConstantsAndMethod.getBattleResByRes(info.getBattleRes());
            final boolean isAtt = info.isAttack();
            if (isAtt) {
                return res[2] == 1;
            }
            return res[2] == 2;
        }
    }
    
    @JsonIgnore
    public int[] getShownInspireResult() {
        final int[] res = new int[2];
        if (this.sRound == 3) {
            final boolean c1Att = KfwdConstantsAndMethod.isC1AttackerRound3(this.matchId, this.round, this.scheduleId);
            if (c1Att) {
                res[0] = this.inspire1;
                res[1] = this.inspire2;
            }
            else {
                res[1] = this.inspire1;
                res[0] = this.inspire2;
            }
            return res;
        }
        if (this.sRound % 2 == 1) {
            res[0] = this.inspire1;
            res[1] = this.inspire2;
        }
        else {
            res[1] = this.inspire1;
            res[0] = this.inspire2;
        }
        return res;
    }
    
    @JsonIgnore
    public int[] getBattleResult() {
        final int[] res = new int[2];
        final int[] bRes = KfwdConstantsAndMethod.getBattleResByRes(this.res);
        int[] array;
        for (int length = (array = bRes).length, i = 0; i < length; ++i) {
            final int r = array[i];
            if (r == 1) {
                final int[] array2 = res;
                final int n = 0;
                ++array2[n];
                if (res[0] > 2) {
                    res[0] = 2;
                }
            }
            else if (r == 2) {
                final int[] array3 = res;
                final int n2 = 1;
                ++array3[n2];
                if (res[1] > 2) {
                    res[1] = 2;
                }
            }
        }
        return res;
    }
    
    @JsonIgnore
    public int getRoundWinner() {
        final int res = 0;
        final int[] br = this.getBattleResult();
        if (br[0] >= 2) {
            return this.competitorId1;
        }
        if (br[1] >= 2) {
            return this.competitorId2;
        }
        return res;
    }
    
    @JsonIgnore
    public boolean isAttacker(final int competitorId) {
        return this.isAttacker(competitorId, this.sRound);
    }
    
    @JsonIgnore
    private boolean isAttacker(final int competitorId, final int sRound) {
        if (competitorId == this.competitorId1) {
            if (sRound == 3) {
                final boolean c1Att = KfwdConstantsAndMethod.isC1AttackerRound3(this.matchId, this.round, this.scheduleId);
                return c1Att;
            }
            return sRound % 2 == 1;
        }
        else {
            if (sRound == 3) {
                final boolean c1Att = KfwdConstantsAndMethod.isC1AttackerRound3(this.matchId, this.round, this.scheduleId);
                return !c1Att;
            }
            return sRound % 2 == 0;
        }
    }
    
    @JsonIgnore
    public int[] getBothPlayerId() {
        return this.getBothPlayerIdByRound(this.sRound);
    }
    
    @JsonIgnore
    private int[] getBothPlayerIdByRound(final Integer sRound) {
        final int[] res = new int[2];
        if (sRound == 3) {
            final boolean c1Att = KfwdConstantsAndMethod.isC1AttackerRound3(this.matchId, this.round, this.scheduleId);
            if (c1Att) {
                res[0] = this.competitorId1;
                res[1] = this.competitorId2;
            }
            else {
                res[1] = this.competitorId1;
                res[0] = this.competitorId2;
            }
            return res;
        }
        if (sRound % 2 == 1) {
            res[0] = this.competitorId1;
            res[1] = this.competitorId2;
        }
        else {
            res[1] = this.competitorId1;
            res[0] = this.competitorId2;
        }
        return res;
    }
    
    public int getScheduleId() {
        return this.scheduleId;
    }
    
    public void setScheduleId(final int scheduleId) {
        this.scheduleId = scheduleId;
    }
    
    public int getMatchId() {
        return this.matchId;
    }
    
    public void setMatchId(final int matchId) {
        this.matchId = matchId;
    }
    
    public int getCompetitorId1() {
        return this.competitorId1;
    }
    
    public void setCompetitorId1(final int competitorId1) {
        this.competitorId1 = competitorId1;
    }
    
    public int getCompetitorId2() {
        return this.competitorId2;
    }
    
    public void setCompetitorId2(final int competitorId2) {
        this.competitorId2 = competitorId2;
    }
    
    public int getInspire1() {
        return this.inspire1;
    }
    
    public void setInspire1(final int inspire1) {
        this.inspire1 = inspire1;
    }
    
    public int getInspire2() {
        return this.inspire2;
    }
    
    public void setInspire2(final int inspire2) {
        this.inspire2 = inspire2;
    }
    
    public int getRes() {
        return this.res;
    }
    
    public void setRes(final int res) {
        this.res = res;
    }
    
    public long getHistoryRes1() {
        return this.historyRes1;
    }
    
    public void setHistoryRes1(final long historyRes1) {
        this.historyRes1 = historyRes1;
    }
    
    public long getHistoryRes2() {
        return this.historyRes2;
    }
    
    public void setHistoryRes2(final long historyRes2) {
        this.historyRes2 = historyRes2;
    }
    
    public int getRound() {
        return this.round;
    }
    
    public void setRound(final int round) {
        this.round = round;
    }
    
    public int getShowround() {
        return this.showround;
    }
    
    public void setShowround(final int showround) {
        this.showround = showround;
    }
    
    public int getsRound() {
        return this.sRound;
    }
    
    public void setsRound(final int sRound) {
        this.sRound = sRound;
    }
    
    public int getShowSRound() {
        return this.showSRound;
    }
    
    public void setShowSRound(final int showSRound) {
        this.showSRound = showSRound;
    }
    
    public long getNextSRoundCD() {
        return this.nextSRoundCD;
    }
    
    public void setNextSRoundCD(final long nextSRoundCD) {
        this.nextSRoundCD = nextSRoundCD;
    }
    
    public long getNextShowSRoundCD() {
        return this.nextShowSRoundCD;
    }
    
    public void setNextShowSRoundCD(final long nextShowSRoundCD) {
        this.nextShowSRoundCD = nextShowSRoundCD;
    }
    
    @JsonIgnore
    public int getScore() {
        return this.score;
    }
    
    public void setScoreAndTicket(final int score, final int ticket) {
        this.score = score;
        this.ticket = ticket;
    }
    
    @JsonIgnore
    public int getLastScore() {
        return this.lastScore;
    }
    
    public void setLastScore(final int lastScore) {
        this.lastScore = lastScore;
    }
    
    public int getTicket() {
        return this.ticket;
    }
    
    public void setTicket(final int ticket) {
        this.ticket = ticket;
    }
    
    public void setLastTicket(final int ticket) {
        this.ticket = ticket;
    }
    
    public void setLastScoreAndTiket() {
        this.lastScore = this.score;
        this.lastTicket = this.ticket;
    }
    
    @JsonIgnore
    public Date getRoundBattleTime() {
        return this.roundBattleTime;
    }
    
    public void setRoundBattleTime(final Date roundBattleTime) {
        this.roundBattleTime = roundBattleTime;
    }
    
    @JsonIgnore
    public Date getLastRoundBattleTime() {
        return this.lastRoundBattleTime;
    }
    
    public void setLastRoundBattleTime(final Date lastRoundBattleTime) {
        this.lastRoundBattleTime = lastRoundBattleTime;
    }
    
    public String getLastReport() {
        return this.lastReport;
    }
    
    public void setLastReport(final String lastReport) {
        this.lastReport = lastReport;
    }
    
    public void addsRoundBattleRes(final int sRound, final int battleRes) {
        this.res = KfwdConstantsAndMethod.addsRoundBattleRes(this.res, sRound, battleRes);
    }
    
    public int getP1score() {
        return this.p1score;
    }
    
    public void setP1score(final int p1score) {
        this.p1score = p1score;
    }
    
    public int getP1Ranking() {
        return this.p1Ranking;
    }
    
    public void setP1Ranking(final int p1Ranking) {
        this.p1Ranking = p1Ranking;
    }
    
    public int getP2score() {
        return this.p2score;
    }
    
    public void setP2score(final int p2score) {
        this.p2score = p2score;
    }
    
    public int getP2Ranking() {
        return this.p2Ranking;
    }
    
    public void setP2Ranking(final int p2Ranking) {
        this.p2Ranking = p2Ranking;
    }
    
    public int getLastTicket() {
        return this.lastTicket;
    }
    
    public void addLastRoundInfo(final long lastHisRes, final boolean lastIsAtt1, final boolean round3IsAtt, final int lastBattleRes, final int lastp1Inspire, final int lastp2Inspire, final boolean isP1) {
        if (isP1) {
            this.historyRes1 = KfwdConstantsAndMethod.addLastRoundInfo(lastHisRes, lastIsAtt1, round3IsAtt, lastBattleRes, lastp1Inspire, lastp2Inspire);
        }
        else {
            this.historyRes2 = KfwdConstantsAndMethod.addLastRoundInfo(lastHisRes, lastIsAtt1, round3IsAtt, lastBattleRes, lastp1Inspire, lastp2Inspire);
        }
    }
    
    public void addHisRoundBattleRes(final int round, final boolean isP1Win) {
        if (isP1Win) {
            this.historyRes1 = KfwdConstantsAndMethod.addHisRoundBattleRes(this.historyRes1, round, 1L);
            this.historyRes2 = KfwdConstantsAndMethod.addHisRoundBattleRes(this.historyRes2, round, 2L);
        }
        else {
            this.historyRes1 = KfwdConstantsAndMethod.addHisRoundBattleRes(this.historyRes1, round, 2L);
            this.historyRes2 = KfwdConstantsAndMethod.addHisRoundBattleRes(this.historyRes2, round, 1L);
        }
    }
    
    public void addBattleHisRes(final int p1, final long res1) {
        if (p1 == this.competitorId1) {
            this.historyRes1 = KfwdConstantsAndMethod.addAllBattleHisRes(this.historyRes1, res1);
        }
        else if (p1 == this.competitorId2) {
            this.historyRes2 = KfwdConstantsAndMethod.addAllBattleHisRes(this.historyRes2, res1);
        }
    }
    
    public static void main(final String[] args) {
        final KfwdRTMatchInfo rtInfo = new KfwdRTMatchInfo();
        rtInfo.scheduleId = 4330101;
        rtInfo.round = 10;
        rtInfo.sRound = 3;
        rtInfo.matchId = 3;
        rtInfo.competitorId1 = 4370;
        rtInfo.competitorId2 = 4357;
        System.out.println(rtInfo.getBothPlayerId()[0]);
        System.out.println(rtInfo.getBothPlayerId()[1]);
    }
}
