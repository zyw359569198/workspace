package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;

public class BattleStat implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer stat;
    private Integer worldPvp;
    private Integer timePve;
    
    public Integer getStat() {
        return this.stat;
    }
    
    public void setStat(final Integer stat) {
        this.stat = stat;
    }
    
    public Integer getWorldPvp() {
        return this.worldPvp;
    }
    
    public void setWorldPvp(final Integer worldPvp) {
        this.worldPvp = worldPvp;
    }
    
    public Integer getTimePve() {
        return this.timePve;
    }
    
    public void setTimePve(final Integer timePve) {
        this.timePve = timePve;
    }
}
