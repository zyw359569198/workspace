package com.reign.gcld.battle.domain;

import com.reign.framework.mybatis.*;
import java.util.*;

public class PlayerBattleAttribute implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer playerId;
    private Integer type;
    private Date supportTime;
    private String armiesBattleOrder;
    private Integer armiesAutoCount;
    private Long youdiTime;
    private Long chujiTime;
    private Integer phantomWorkShopLv;
    private Integer phantomToday;
    private Integer vip3PhantomCount;
    private Integer autoStrategy;
    private Integer teamTimes;
    private Long shoumaiManzuTime;
    private Integer winTimes;
    private Integer failTimes;
    private Integer changebat;
    private Integer eventGemCount;
    private Integer eventGemCountToday;
    private Integer eventJiebingCountToday;
    private Integer eventXtysCountToday;
    private Integer eventNationalTreasureCountToday;
    private Integer eventSdlrCountToday;
    private Integer activityBatExp;
    private Integer eventWorldDramaCountToday;
    private Integer eventTrainningTokenCountToday;
    private Integer eventSlaveCountToday;
    
    public Integer getPlayerId() {
        return this.playerId;
    }
    
    public void setPlayerId(final Integer playerId) {
        this.playerId = playerId;
    }
    
    public Integer getType() {
        return this.type;
    }
    
    public void setType(final Integer type) {
        this.type = type;
    }
    
    public Date getSupportTime() {
        return this.supportTime;
    }
    
    public void setSupportTime(final Date supportTime) {
        this.supportTime = supportTime;
    }
    
    public String getArmiesBattleOrder() {
        return this.armiesBattleOrder;
    }
    
    public void setArmiesBattleOrder(final String armiesBattleOrder) {
        this.armiesBattleOrder = armiesBattleOrder;
    }
    
    public Integer getArmiesAutoCount() {
        return this.armiesAutoCount;
    }
    
    public void setArmiesAutoCount(final Integer armiesAutoCount) {
        this.armiesAutoCount = armiesAutoCount;
    }
    
    public Long getYoudiTime() {
        return this.youdiTime;
    }
    
    public void setYoudiTime(final Long youdiTime) {
        this.youdiTime = youdiTime;
    }
    
    public Long getChujiTime() {
        return this.chujiTime;
    }
    
    public void setChujiTime(final Long chujiTime) {
        this.chujiTime = chujiTime;
    }
    
    public Integer getPhantomWorkShopLv() {
        return this.phantomWorkShopLv;
    }
    
    public void setPhantomWorkShopLv(final Integer phantomWorkShopLv) {
        this.phantomWorkShopLv = phantomWorkShopLv;
    }
    
    public Integer getPhantomToday() {
        return this.phantomToday;
    }
    
    public void setPhantomToday(final Integer phantomToday) {
        this.phantomToday = phantomToday;
    }
    
    public Integer getVip3PhantomCount() {
        return this.vip3PhantomCount;
    }
    
    public void setVip3PhantomCount(final Integer vip3PhantomCount) {
        this.vip3PhantomCount = vip3PhantomCount;
    }
    
    public Integer getAutoStrategy() {
        return this.autoStrategy;
    }
    
    public void setAutoStrategy(final Integer autoStrategy) {
        this.autoStrategy = autoStrategy;
    }
    
    public Integer getTeamTimes() {
        return this.teamTimes;
    }
    
    public void setTeamTimes(final Integer teamTimes) {
        this.teamTimes = teamTimes;
    }
    
    public Long getShoumaiManzuTime() {
        return this.shoumaiManzuTime;
    }
    
    public void setShoumaiManzuTime(final Long shoumaiManzuTime) {
        this.shoumaiManzuTime = shoumaiManzuTime;
    }
    
    public Integer getWinTimes() {
        return this.winTimes;
    }
    
    public void setWinTimes(final Integer winTimes) {
        this.winTimes = winTimes;
    }
    
    public Integer getFailTimes() {
        return this.failTimes;
    }
    
    public void setFailTimes(final Integer failTimes) {
        this.failTimes = failTimes;
    }
    
    public Integer getChangebat() {
        return this.changebat;
    }
    
    public void setChangebat(final Integer changebat) {
        this.changebat = changebat;
    }
    
    public Integer getEventGemCount() {
        return this.eventGemCount;
    }
    
    public void setEventGemCount(final Integer eventGemCount) {
        this.eventGemCount = eventGemCount;
    }
    
    public Integer getEventGemCountToday() {
        return this.eventGemCountToday;
    }
    
    public void setEventGemCountToday(final Integer eventGemCountToday) {
        this.eventGemCountToday = eventGemCountToday;
    }
    
    public Integer getEventJiebingCountToday() {
        return this.eventJiebingCountToday;
    }
    
    public void setEventJiebingCountToday(final Integer eventJiebingCountToday) {
        this.eventJiebingCountToday = eventJiebingCountToday;
    }
    
    public Integer getEventXtysCountToday() {
        return this.eventXtysCountToday;
    }
    
    public void setEventXtysCountToday(final Integer eventXtysCountToday) {
        this.eventXtysCountToday = eventXtysCountToday;
    }
    
    public Integer getEventNationalTreasureCountToday() {
        return this.eventNationalTreasureCountToday;
    }
    
    public void setEventNationalTreasureCountToday(final Integer eventNationalTreasureCountToday) {
        this.eventNationalTreasureCountToday = eventNationalTreasureCountToday;
    }
    
    public Integer getEventSdlrCountToday() {
        return this.eventSdlrCountToday;
    }
    
    public void setEventSdlrCountToday(final Integer eventSdlrCountToday) {
        this.eventSdlrCountToday = eventSdlrCountToday;
    }
    
    public Integer getActivityBatExp() {
        return this.activityBatExp;
    }
    
    public void setActivityBatExp(final Integer activityBatExp) {
        this.activityBatExp = activityBatExp;
    }
    
    public Integer getEventWorldDramaCountToday() {
        return this.eventWorldDramaCountToday;
    }
    
    public void setEventWorldDramaCountToday(final Integer eventWorldDramaCountToday) {
        this.eventWorldDramaCountToday = eventWorldDramaCountToday;
    }
    
    public Integer getEventTrainningTokenCountToday() {
        return this.eventTrainningTokenCountToday;
    }
    
    public void setEventTrainningTokenCountToday(final Integer eventTrainningTokenCountToday) {
        this.eventTrainningTokenCountToday = eventTrainningTokenCountToday;
    }
    
    public Integer getEventSlaveCountToday() {
        return this.eventSlaveCountToday;
    }
    
    public void setEventSlaveCountToday(final Integer eventSlaveCountToday) {
        this.eventSlaveCountToday = eventSlaveCountToday;
    }
}
