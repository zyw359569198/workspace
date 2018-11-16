package com.reign.gcld.kfwd.common;

import java.util.*;
import com.reign.kf.comm.entity.match.*;

public class MatchFight
{
    private int sessionId;
    private int version;
    private int matchId;
    private int turn;
    private int matchNum;
    private Date fightTime;
    private Date formationEndTime;
    private int state;
    private MatchFightMember member1;
    private MatchFightMember member2;
    
    public MatchFight(final MatchScheduleEntity matchScheduleEntity, final Match match) {
        this.sessionId = matchScheduleEntity.getSession();
        this.matchId = matchScheduleEntity.getMatchId();
        this.turn = matchScheduleEntity.getTurn();
        this.matchNum = matchScheduleEntity.getMatchNum();
        this.fightTime = new Date(System.currentTimeMillis() + matchScheduleEntity.getMatchCD());
        this.formationEndTime = new Date(System.currentTimeMillis() + matchScheduleEntity.getMatchCD() - 30000L);
        this.state = 1;
        this.member1 = new MatchFightMember(matchScheduleEntity.getPlayer1());
        this.member2 = new MatchFightMember(matchScheduleEntity.getPlayer2());
        match.setAttendeeMatchFight(matchScheduleEntity.getPlayer1(), this);
        match.setAttendeeMatchFight(matchScheduleEntity.getPlayer2(), this);
    }
    
    public synchronized void assign(final MatchScheduleEntity matchScheduleEntity) {
        this.matchId = matchScheduleEntity.getMatchId();
        this.matchNum = matchScheduleEntity.getMatchNum();
        this.fightTime = new Date(System.currentTimeMillis() + matchScheduleEntity.getMatchCD());
        this.formationEndTime = new Date(System.currentTimeMillis() + matchScheduleEntity.getMatchCD() - 30000L);
        this.state = 1;
        this.version = 0;
    }
    
    public synchronized int assign(final MatchRTInfoEntity matchRTInfoEntity) {
        if (this.version == matchRTInfoEntity.getVersion()) {
            return 0;
        }
        this.version = matchRTInfoEntity.getVersion();
        this.fightTime = new Date(System.currentTimeMillis() + matchRTInfoEntity.getMatchCD());
        this.formationEndTime = new Date(System.currentTimeMillis() + matchRTInfoEntity.getMatchCD() - 30000L);
        this.member1.assign(matchRTInfoEntity.getPlayer1());
        if (matchRTInfoEntity.getPlayer2() == null) {
            this.member2 = null;
        }
        else {
            this.member2.assign(matchRTInfoEntity.getPlayer2());
        }
        if (this.state == 1) {
            if (this.member2 == null) {
                this.state = 7;
                return 2;
            }
            if (this.matchNum == 1) {
                this.state = 2;
            }
            else if (this.matchNum == 2) {
                this.state = 3;
            }
            else if (this.matchNum == 3) {
                this.state = 2;
            }
            else if (this.matchNum == 4) {
                this.state = 3;
            }
            else {
                this.state = 4;
            }
        }
        return 1;
    }
    
    public synchronized void assign(final MatchReportEntity matchReportEntity) {
        if (matchReportEntity.getWinner() == this.member1.getCompetitorId()) {
            this.member1.setWinMatch(this.member1.getWinMatch() + 1);
        }
        else {
            this.member2.setWinMatch(this.member2.getWinMatch() + 1);
        }
        this.fightTime = new Date(System.currentTimeMillis() + matchReportEntity.getNextCd());
        if (!matchReportEntity.isHasNext()) {
            this.state = 8;
        }
        else if (this.member2 == null || this.member1.getWinMatch() >= 3 || this.member2.getWinMatch() >= 3) {
            this.state = 7;
        }
        else {
            this.state = 6;
        }
    }
    
    public int getState() {
        return this.state;
    }
    
    public void setState(final int state) {
        this.state = state;
    }
    
    public int getSessionId() {
        return this.sessionId;
    }
    
    public int getMatchId() {
        return this.matchId;
    }
    
    public int getTurn() {
        return this.turn;
    }
    
    public int getMatchNum() {
        return this.matchNum;
    }
    
    public Date getFightTime() {
        return this.fightTime;
    }
    
    public MatchFightMember getMember1() {
        return this.member1;
    }
    
    public MatchFightMember getMember2() {
        return this.member2;
    }
    
    public int getVersion() {
        return this.version;
    }
    
    public Date getFormationEndTime() {
        return this.formationEndTime;
    }
}
