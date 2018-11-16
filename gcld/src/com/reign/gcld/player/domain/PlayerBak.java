package com.reign.gcld.player.domain;

import com.reign.framework.mybatis.*;
import java.util.*;

public class PlayerBak implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer playerId;
    private String playerName;
    private Integer playerLv;
    private Integer sysGold;
    private Integer userGold;
    private String userId;
    private Integer consumeLv;
    private String yx;
    private Integer forceId;
    private Integer pic;
    private Integer powerId;
    private Date loginTime;
    private Date quitTime;
    private Integer isdelete;
    private Date abandonTime;
    
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
    
    public Integer getIsdelete() {
        return this.isdelete;
    }
    
    public void setIsdelete(final Integer isdelete) {
        this.isdelete = isdelete;
    }
    
    public Date getAbandonTime() {
        return this.abandonTime;
    }
    
    public void setAbandonTime(final Date abandonTime) {
        this.abandonTime = abandonTime;
    }
}
