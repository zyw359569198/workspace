package com.reign.gcld.pay.domain;

import com.reign.framework.mybatis.*;

public class PlayerVipTx implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer vId;
    private Integer playerId;
    private Integer dailyStatus;
    private Integer rookieStatus;
    private String upgradeStatus;
    private Integer extraStatus;
    private Integer isYellowYearVip;
    private Integer isYellowHighVip;
    private Integer isYellowVip;
    private Integer yellowVipLv;
    
    public Integer getVId() {
        return this.vId;
    }
    
    public void setVId(final Integer vId) {
        this.vId = vId;
    }
    
    public Integer getPlayerId() {
        return this.playerId;
    }
    
    public void setPlayerId(final Integer playerId) {
        this.playerId = playerId;
    }
    
    public Integer getDailyStatus() {
        return this.dailyStatus;
    }
    
    public void setDailyStatus(final Integer dailyStatus) {
        this.dailyStatus = dailyStatus;
    }
    
    public Integer getRookieStatus() {
        return this.rookieStatus;
    }
    
    public void setRookieStatus(final Integer rookieStatus) {
        this.rookieStatus = rookieStatus;
    }
    
    public String getUpgradeStatus() {
        return this.upgradeStatus;
    }
    
    public void setUpgradeStatus(final String upgradeStatus) {
        this.upgradeStatus = upgradeStatus;
    }
    
    public Integer getExtraStatus() {
        return this.extraStatus;
    }
    
    public void setExtraStatus(final Integer extraStatus) {
        this.extraStatus = extraStatus;
    }
    
    public Integer getIsYellowYearVip() {
        return this.isYellowYearVip;
    }
    
    public void setIsYellowYearVip(final Integer isYellowYearVip) {
        this.isYellowYearVip = isYellowYearVip;
    }
    
    public Integer getIsYellowHighVip() {
        return this.isYellowHighVip;
    }
    
    public void setIsYellowHighVip(final Integer isYellowHighVip) {
        this.isYellowHighVip = isYellowHighVip;
    }
    
    public Integer getIsYellowVip() {
        return this.isYellowVip;
    }
    
    public void setIsYellowVip(final Integer isYellowVip) {
        this.isYellowVip = isYellowVip;
    }
    
    public Integer getYellowVipLv() {
        return this.yellowVipLv;
    }
    
    public void setYellowVipLv(final Integer yellowVipLv) {
        this.yellowVipLv = yellowVipLv;
    }
}
