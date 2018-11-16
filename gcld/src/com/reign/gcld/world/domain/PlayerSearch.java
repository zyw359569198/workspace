package com.reign.gcld.world.domain;

import com.reign.framework.mybatis.*;
import java.util.*;

public class PlayerSearch implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer playerId;
    private String canSearchInfo;
    private String currSearchInfo;
    private Integer searchNumLeft;
    private Date lastSearchnumTime;
    private Integer buySearchNum;
    
    public Integer getPlayerId() {
        return this.playerId;
    }
    
    public void setPlayerId(final Integer playerId) {
        this.playerId = playerId;
    }
    
    public String getCanSearchInfo() {
        return this.canSearchInfo;
    }
    
    public void setCanSearchInfo(final String canSearchInfo) {
        this.canSearchInfo = canSearchInfo;
    }
    
    public String getCurrSearchInfo() {
        return this.currSearchInfo;
    }
    
    public void setCurrSearchInfo(final String currSearchInfo) {
        this.currSearchInfo = currSearchInfo;
    }
    
    public Integer getSearchNumLeft() {
        return this.searchNumLeft;
    }
    
    public void setSearchNumLeft(final Integer searchNumLeft) {
        this.searchNumLeft = searchNumLeft;
    }
    
    public Date getLastSearchnumTime() {
        return this.lastSearchnumTime;
    }
    
    public void setLastSearchnumTime(final Date lastSearchnumTime) {
        this.lastSearchnumTime = lastSearchnumTime;
    }
    
    public Integer getBuySearchNum() {
        return this.buySearchNum;
    }
    
    public void setBuySearchNum(final Integer buySearchNum) {
        this.buySearchNum = buySearchNum;
    }
}
