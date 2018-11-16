package com.reign.gcld.slave.domain;

import com.reign.framework.mybatis.*;
import java.util.*;

public class Slaveholder implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer playerId;
    private Integer prisonLv;
    private Integer lashLv;
    private Integer grabNum;
    private Integer lashNum;
    private Integer autoLashExp;
    private Integer point;
    private Date expireTime;
    private Integer trailGold;
    
    public Integer getPlayerId() {
        return this.playerId;
    }
    
    public void setPlayerId(final Integer playerId) {
        this.playerId = playerId;
    }
    
    public Integer getPrisonLv() {
        return this.prisonLv;
    }
    
    public void setPrisonLv(final Integer prisonLv) {
        this.prisonLv = prisonLv;
    }
    
    public Integer getLashLv() {
        return this.lashLv;
    }
    
    public void setLashLv(final Integer lashLv) {
        this.lashLv = lashLv;
    }
    
    public Integer getGrabNum() {
        return this.grabNum;
    }
    
    public void setGrabNum(final Integer grabNum) {
        this.grabNum = grabNum;
    }
    
    public Integer getLashNum() {
        return this.lashNum;
    }
    
    public void setLashNum(final Integer lashNum) {
        this.lashNum = lashNum;
    }
    
    public Integer getAutoLashExp() {
        return this.autoLashExp;
    }
    
    public void setAutoLashExp(final Integer autoLashExp) {
        this.autoLashExp = autoLashExp;
    }
    
    public Integer getPoint() {
        return this.point;
    }
    
    public void setPoint(final Integer point) {
        this.point = point;
    }
    
    public Date getExpireTime() {
        return this.expireTime;
    }
    
    public void setExpireTime(final Date expireTime) {
        this.expireTime = expireTime;
    }
    
    public Integer getTrailGold() {
        return this.trailGold;
    }
    
    public void setTrailGold(final Integer trailGold) {
        this.trailGold = trailGold;
    }
}
