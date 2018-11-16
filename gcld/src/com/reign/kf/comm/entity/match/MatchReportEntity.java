package com.reign.kf.comm.entity.match;

import java.util.*;

public class MatchReportEntity
{
    private int matchId;
    private int winner;
    private String report;
    private Date nextTime;
    private long nextCd;
    private boolean hasNext;
    private int session;
    
    public int getMatchId() {
        return this.matchId;
    }
    
    public void setMatchId(final int matchId) {
        this.matchId = matchId;
    }
    
    public int getWinner() {
        return this.winner;
    }
    
    public void setWinner(final int winner) {
        this.winner = winner;
    }
    
    public String getReport() {
        return this.report;
    }
    
    public void setReport(final String report) {
        this.report = report;
    }
    
    public long getNextCd() {
        return this.nextCd;
    }
    
    public void setNextCd(final long nextCd) {
        this.nextCd = nextCd;
    }
    
    public void setNextCd() {
        if (this.getNextTime() == null) {
            this.nextCd = 0L;
        }
        else {
            this.nextCd = this.getNextTime().getTime() - System.currentTimeMillis();
            this.nextCd = ((this.nextCd < 0L) ? 0L : this.nextCd);
        }
    }
    
    public boolean isHasNext() {
        return this.hasNext;
    }
    
    public void setHasNext(final boolean hasNext) {
        this.hasNext = hasNext;
    }
    
    public Date getNextTime() {
        return this.nextTime;
    }
    
    public void setNextTime(final Date nextTime) {
        this.nextTime = nextTime;
    }
    
    public int getSession() {
        return this.session;
    }
    
    public void setSession(final int session) {
        this.session = session;
    }
}
