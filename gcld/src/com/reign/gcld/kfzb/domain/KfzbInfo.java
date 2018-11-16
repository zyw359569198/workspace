package com.reign.gcld.kfzb.domain;

import com.reign.framework.mybatis.*;

public class KfzbInfo implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer playerId;
    private Integer seasonId;
    private Integer flower1;
    private Integer flower1Buy;
    private String support1Info;
    private Integer flower2;
    private Integer flower2Buy;
    private String support2Info;
    
    public Integer getPlayerId() {
        return this.playerId;
    }
    
    public void setPlayerId(final Integer playerId) {
        this.playerId = playerId;
    }
    
    public Integer getSeasonId() {
        return this.seasonId;
    }
    
    public void setSeasonId(final Integer seasonId) {
        this.seasonId = seasonId;
    }
    
    public Integer getFlower1() {
        return this.flower1;
    }
    
    public void setFlower1(final Integer flower1) {
        this.flower1 = flower1;
    }
    
    public Integer getFlower1Buy() {
        return this.flower1Buy;
    }
    
    public void setFlower1Buy(final Integer flower1Buy) {
        this.flower1Buy = flower1Buy;
    }
    
    public String getSupport1Info() {
        return this.support1Info;
    }
    
    public void setSupport1Info(final String support1Info) {
        this.support1Info = support1Info;
    }
    
    public Integer getFlower2() {
        return this.flower2;
    }
    
    public void setFlower2(final Integer flower2) {
        this.flower2 = flower2;
    }
    
    public Integer getFlower2Buy() {
        return this.flower2Buy;
    }
    
    public void setFlower2Buy(final Integer flower2Buy) {
        this.flower2Buy = flower2Buy;
    }
    
    public String getSupport2Info() {
        return this.support2Info;
    }
    
    public void setSupport2Info(final String support2Info) {
        this.support2Info = support2Info;
    }
}
