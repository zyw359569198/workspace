package com.reign.kf.gw.kfwd.domain;

import com.reign.framework.hibernate.model.*;
import java.util.*;

public class KfwdSeasonInfo implements IModel
{
    private int pk;
    private int seasonId;
    private Date activeTime;
    private Date scheduleTime;
    private Date battleTime;
    private Integer globalState;
    private int valid;
    private int roundInterval;
    private int battleInterval;
    private Date signUpTime;
    private Date signUpFinishTime;
    private Date showBattleTime;
    private int oneDayRoundLimit;
    private Date nextDayBegionTime;
    private Date thirdDayBegionTime;
    private int totalRound;
    private Date endTime;
    private int ruleId;
    private int type;
    private int zb;
    private int zbLayer;
    private int tgId;
    
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
    
    public Date getActiveTime() {
        return this.activeTime;
    }
    
    public void setActiveTime(final Date activeTime) {
        this.activeTime = activeTime;
    }
    
    public Date getScheduleTime() {
        return this.scheduleTime;
    }
    
    public void setScheduleTime(final Date scheduleTime) {
        this.scheduleTime = scheduleTime;
    }
    
    public Date getBattleTime() {
        return this.battleTime;
    }
    
    public void setBattleTime(final Date battleTime) {
        this.battleTime = battleTime;
    }
    
    public Integer getGlobalState() {
        return this.globalState;
    }
    
    public void setGlobalState(final Integer globalState) {
        this.globalState = globalState;
    }
    
    public int getValid() {
        return this.valid;
    }
    
    public void setValid(final int valid) {
        this.valid = valid;
    }
    
    public int getRoundInterval() {
        return this.roundInterval;
    }
    
    public void setRoundInterval(final int roundInterval) {
        this.roundInterval = roundInterval;
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
    
    public Date getShowBattleTime() {
        return this.showBattleTime;
    }
    
    public void setShowBattleTime(final Date showBattleTime) {
        this.showBattleTime = showBattleTime;
    }
    
    public int getOneDayRoundLimit() {
        return this.oneDayRoundLimit;
    }
    
    public void setOneDayRoundLimit(final int oneDayRoundLimit) {
        this.oneDayRoundLimit = oneDayRoundLimit;
    }
    
    public Date getNextDayBegionTime() {
        return this.nextDayBegionTime;
    }
    
    public void setNextDayBegionTime(final Date nextDayBegionTime) {
        this.nextDayBegionTime = nextDayBegionTime;
    }
    
    public Date getThirdDayBegionTime() {
        return this.thirdDayBegionTime;
    }
    
    public void setThirdDayBegionTime(final Date thirdDayBegionTime) {
        this.thirdDayBegionTime = thirdDayBegionTime;
    }
    
    public int getTotalRound() {
        return this.totalRound;
    }
    
    public void setTotalRound(final int totalRound) {
        this.totalRound = totalRound;
    }
    
    public Date getEndTime() {
        return this.endTime;
    }
    
    public void setEndTime(final Date endTime) {
        this.endTime = endTime;
    }
    
    public int getRuleId() {
        return this.ruleId;
    }
    
    public void setRuleId(final int ruleId) {
        this.ruleId = ruleId;
    }
    
    public int getType() {
        return this.type;
    }
    
    public void setType(final int type) {
        this.type = type;
    }
    
    public int getZb() {
        return this.zb;
    }
    
    public void setZb(final int zb) {
        this.zb = zb;
    }
    
    public int getZbLayer() {
        return this.zbLayer;
    }
    
    public void setZbLayer(final int zbLayer) {
        this.zbLayer = zbLayer;
    }
    
    public int getBattleInterval() {
        return this.battleInterval;
    }
    
    public void setBattleInterval(final int battleInterval) {
        this.battleInterval = battleInterval;
    }
    
    public int getTgId() {
        return this.tgId;
    }
    
    public void setTgId(final int tgId) {
        this.tgId = tgId;
    }
}
