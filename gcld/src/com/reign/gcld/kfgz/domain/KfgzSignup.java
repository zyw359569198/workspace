package com.reign.gcld.kfgz.domain;

import com.reign.framework.mybatis.*;

public class KfgzSignup implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer playerId;
    private Integer competitorId;
    
    public Integer getPlayerId() {
        return this.playerId;
    }
    
    public void setPlayerId(final Integer playerId) {
        this.playerId = playerId;
    }
    
    public Integer getCompetitorId() {
        return this.competitorId;
    }
    
    public void setCompetitorId(final Integer competitorId) {
        this.competitorId = competitorId;
    }
}
