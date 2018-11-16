package com.reign.gcld.player.domain;

import com.reign.framework.mybatis.*;
import java.util.*;
import com.reign.gcld.kfgz.service.*;

public class Player implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer playerId;
    private String playerName;
    private Integer playerLv;
    private Integer maxLv;
    private Integer sysGold;
    private Integer userGold;
    private String userId;
    private Integer consumeLv;
    private String yx;
    private String yxSource;
    private Integer forceId;
    private Integer pic;
    private Integer powerId;
    private Date loginTime;
    private Date quitTime;
    private Integer dailyOnlineTime;
    private Integer state;
    private Date deleteTime;
    private Date createTime;
    private Integer playerServerId;
    private Integer totalUserGold;
    private Integer totalTicketGold;
    private Integer gm;
    private Integer defaultPay;
    private String servernameServeridPlayerid;
    
    public Integer getPlayerId() {
        return this.playerId;
    }
    
    public void setPlayerId(final Integer playerId) {
        this.playerId = playerId;
    }
    
    public String getPlayerName() {
        return this.playerName;
    }
    
    public void setPlayerName(final String playerName) {
        this.playerName = playerName;
    }
    
    public Integer getPlayerLv() {
        return this.playerLv;
    }
    
    public void setPlayerLv(final Integer playerLv) {
        this.playerLv = playerLv;
    }
    
    public Integer getGold() {
        KfgzMatchService.freshPlayerResourceCacheGold(this.playerId, this.sysGold + this.userGold);
        return this.sysGold + this.userGold;
    }
    
    public Integer getMaxLv() {
        return this.maxLv;
    }
    
    public void setMaxLv(final Integer maxLv) {
        this.maxLv = maxLv;
    }
    
    public Integer getSysGold() {
        return this.sysGold;
    }
    
    public void setSysGold(final Integer sysGold) {
        this.sysGold = sysGold;
    }
    
    public Integer getUserGold() {
        return this.userGold;
    }
    
    public void setUserGold(final Integer userGold) {
        this.userGold = userGold;
    }
    
    public String getUserId() {
        return this.userId;
    }
    
    public void setUserId(final String userId) {
        this.userId = userId;
    }
    
    public Integer getConsumeLv() {
        return this.consumeLv;
    }
    
    public void setConsumeLv(final Integer consumeLv) {
        this.consumeLv = consumeLv;
    }
    
    public String getYx() {
        return this.yx;
    }
    
    public void setYx(final String yx) {
        this.yx = yx;
    }
    
    public String getYxSource() {
        return this.yxSource;
    }
    
    public void setYxSource(final String yxSource) {
        this.yxSource = yxSource;
    }
    
    public Integer getForceId() {
        return this.forceId;
    }
    
    public void setForceId(final Integer forceId) {
        this.forceId = forceId;
    }
    
    public Integer getPic() {
        return this.pic;
    }
    
    public void setPic(final Integer pic) {
        this.pic = pic;
    }
    
    public Integer getPowerId() {
        return this.powerId;
    }
    
    public void setPowerId(final Integer powerId) {
        this.powerId = powerId;
    }
    
    public Date getLoginTime() {
        return this.loginTime;
    }
    
    public void setLoginTime(final Date loginTime) {
        this.loginTime = loginTime;
    }
    
    public Date getQuitTime() {
        return this.quitTime;
    }
    
    public void setQuitTime(final Date quitTime) {
        this.quitTime = quitTime;
    }
    
    public void setDailyOnlineTime(final Integer dailyOnlineTime) {
        this.dailyOnlineTime = dailyOnlineTime;
    }
    
    public Integer getDailyOnlineTime() {
        return this.dailyOnlineTime;
    }
    
    public Integer getState() {
        return this.state;
    }
    
    public void setState(final Integer state) {
        this.state = state;
    }
    
    public Date getDeleteTime() {
        return this.deleteTime;
    }
    
    public void setDeleteTime(final Date deleteTime) {
        this.deleteTime = deleteTime;
    }
    
    public Date getCreateTime() {
        return this.createTime;
    }
    
    public void setCreateTime(final Date createTime) {
        this.createTime = createTime;
    }
    
    public Integer getPlayerServerId() {
        return this.playerServerId;
    }
    
    public void setPlayerServerId(final Integer playerServerId) {
        this.playerServerId = playerServerId;
    }
    
    public Integer getTotalUserGold() {
        return this.totalUserGold;
    }
    
    public void setTotalUserGold(final Integer totalUserGold) {
        this.totalUserGold = totalUserGold;
    }
    
    public Integer getTotalTicketGold() {
        return this.totalTicketGold;
    }
    
    public void setTotalTicketGold(final Integer totalTicketGold) {
        this.totalTicketGold = totalTicketGold;
    }
    
    public Integer getGm() {
        return this.gm;
    }
    
    public void setGm(final Integer gm) {
        this.gm = gm;
    }
    
    public Integer getDefaultPay() {
        return this.defaultPay;
    }
    
    public void setDefaultPay(final Integer defaultPay) {
        this.defaultPay = defaultPay;
    }
    
    public String getServernameServeridPlayerid() {
        return this.servernameServeridPlayerid;
    }
    
    public void setServernameServeridPlayerid(final String servernameServeridPlayerid) {
        this.servernameServeridPlayerid = servernameServeridPlayerid;
    }
}
