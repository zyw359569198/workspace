package com.reign.gcld.market.domain;

import com.reign.framework.mybatis.*;
import java.util.*;

public class PlayerMarket implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer playerId;
    private Double canbuyNum;
    private Date refreshTime;
    private String showInfo;
    private Date getBuynumTime;
    
    public Integer getPlayerId() {
        return this.playerId;
    }
    
    public void setPlayerId(final Integer playerId) {
        this.playerId = playerId;
    }
    
    public Double getCanbuyNum() {
        return this.canbuyNum;
    }
    
    public void setCanbuyNum(final Double canbuyNum) {
        this.canbuyNum = canbuyNum;
    }
    
    public Date getRefreshTime() {
        return this.refreshTime;
    }
    
    public void setRefreshTime(final Date refreshTime) {
        this.refreshTime = refreshTime;
    }
    
    public String getShowInfo() {
        return this.showInfo;
    }
    
    public void setShowInfo(final String showInfo) {
        this.showInfo = showInfo;
    }
    
    public Date getGetBuynumTime() {
        return this.getBuynumTime;
    }
    
    public void setGetBuynumTime(final Date getBuynumTime) {
        this.getBuynumTime = getBuynumTime;
    }
}
