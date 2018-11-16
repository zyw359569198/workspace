package com.reign.kfzb.domain;

import com.reign.framework.hibernate.model.*;
import java.util.*;

public class KfzbSeasonInfoD implements IModel
{
    private int pk;
    private int seasonId;
    private Integer minLv;
    private int supLv;
    private int matchId;
    private Integer globalState;
    private int roundInterval;
    private int battleInterval;
    private Date activeTime;
    private Date signUpTime;
    private Date signUpFinishTime;
    private Date scheduleTime;
    private Date day1showBattleTime;
    private Date day1BattleTime;
    private Date day2showBattleTime;
    private Date day2BattleTime;
    private Date day3showBattleTime;
    private Date day3BattleTime;
    private Date endTime;
    private int useLimit;
    
    public int getPk() {
        return this.pk;
    }
    
    public void setPk(final int pk) {
        this.pk = pk;
    }
    
    public int getSeasonId() {
        return this.seasonId;
    }
    
    public void setSeasonId(final int seasonId) {
        this.seasonId = seasonId;
    }
    
    public Integer getMinLv() {
        return this.minLv;
    }
    
    public void setMinLv(final Integer minLv) {
        this.minLv = minLv;
    }
    
    public int getMatchId() {
        return this.matchId;
    }
    
    public void setMatchId(final int matchId) {
        this.matchId = matchId;
    }
    
    public Integer getGlobalState() {
        return this.globalState;
    }
    
    public void setGlobalState(final Integer globalState) {
        this.globalState = globalState;
    }
    
    public int getRoundInterval() {
        return this.roundInterval;
    }
    
    public void setRoundInterval(final int roundInterval) {
        this.roundInterval = roundInterval;
    }
    
    public Date getActiveTime() {
        return this.activeTime;
    }
    
    public void setActiveTime(final Date activeTime) {
        this.activeTime = activeTime;
    }
    
    public Date getSignUpTime() {
        return this.signUpTime;
    }
    
    public void setSignUpTime(final Date signUpTime) {
        this.signUpTime = signUpTime;
    }
    
    public Date getSignUpFinishTime() {
        return this.signUpFinishTime;
    }
    
    public void setSignUpFinishTime(final Date signUpFinishTime) {
        this.signUpFinishTime = signUpFinishTime;
    }
    
    public Date getScheduleTime() {
        return this.scheduleTime;
    }
    
    public void setScheduleTime(final Date scheduleTime) {
        this.scheduleTime = scheduleTime;
    }
    
    public Date getDay1showBattleTime() {
        return this.day1showBattleTime;
    }
    
    public void setDay1showBattleTime(final Date day1showBattleTime) {
        this.day1showBattleTime = day1showBattleTime;
    }
    
    public Date getDay1BattleTime() {
        return this.day1BattleTime;
    }
    
    public void setDay1BattleTime(final Date day1BattleTime) {
        this.day1BattleTime = day1BattleTime;
    }
    
    public Date getDay2showBattleTime() {
        return this.day2showBattleTime;
    }
    
    public void setDay2showBattleTime(final Date day2showBattleTime) {
        this.day2showBattleTime = day2showBattleTime;
    }
    
    public Date getDay2BattleTime() {
        return this.day2BattleTime;
    }
    
    public void setDay2BattleTime(final Date day2BattleTime) {
        this.day2BattleTime = day2BattleTime;
    }
    
    public Date getDay3showBattleTime() {
        return this.day3showBattleTime;
    }
    
    public void setDay3showBattleTime(final Date day3showBattleTime) {
        this.day3showBattleTime = day3showBattleTime;
    }
    
    public Date getDay3BattleTime() {
        return this.day3BattleTime;
    }
    
    public void setDay3BattleTime(final Date day3BattleTime) {
        this.day3BattleTime = day3BattleTime;
    }
    
    public Date getEndTime() {
        return this.endTime;
    }
    
    public void setEndTime(final Date endTime) {
        this.endTime = endTime;
    }
    
    public int getBattleInterval() {
        return this.battleInterval;
    }
    
    public void setBattleInterval(final int battleInterval) {
        this.battleInterval = battleInterval;
    }
    
    public int getSupLv() {
        return this.supLv;
    }
    
    public void setSupLv(final int supLv) {
        this.supLv = supLv;
    }
    
    public int getUseLimit() {
        return this.useLimit;
    }
    
    public void setUseLimit(final int useLimit) {
        this.useLimit = useLimit;
    }
}
