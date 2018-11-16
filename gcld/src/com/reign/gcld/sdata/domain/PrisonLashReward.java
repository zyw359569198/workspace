package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;

public class PrisonLashReward implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private Integer type;
    private Integer highLv;
    private Integer lowLv;
    private Integer officialHigh;
    private Integer officialLow;
    private Integer expReward;
    private Integer prisonLowLv;
    private Integer prisonHighLv;
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public Integer getType() {
        return this.type;
    }
    
    public void setType(final Integer type) {
        this.type = type;
    }
    
    public Integer getHighLv() {
        return this.highLv;
    }
    
    public void setHighLv(final Integer highLv) {
        this.highLv = highLv;
    }
    
    public Integer getLowLv() {
        return this.lowLv;
    }
    
    public void setLowLv(final Integer lowLv) {
        this.lowLv = lowLv;
    }
    
    public Integer getOfficialHigh() {
        return this.officialHigh;
    }
    
    public void setOfficialHigh(final Integer officialHigh) {
        this.officialHigh = officialHigh;
    }
    
    public Integer getOfficialLow() {
        return this.officialLow;
    }
    
    public void setOfficialLow(final Integer officialLow) {
        this.officialLow = officialLow;
    }
    
    public Integer getExpReward() {
        return this.expReward;
    }
    
    public void setExpReward(final Integer expReward) {
        this.expReward = expReward;
    }
    
    public Integer getPrisonLowLv() {
        return this.prisonLowLv;
    }
    
    public void setPrisonLowLv(final Integer prisonLowLv) {
        this.prisonLowLv = prisonLowLv;
    }
    
    public Integer getPrisonHighLv() {
        return this.prisonHighLv;
    }
    
    public void setPrisonHighLv(final Integer prisonHighLv) {
        this.prisonHighLv = prisonHighLv;
    }
}
