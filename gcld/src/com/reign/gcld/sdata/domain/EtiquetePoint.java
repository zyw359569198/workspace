package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;
import com.reign.gcld.battle.common.*;

public class EtiquetePoint implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private Integer demand;
    private String reward;
    private BattleDrop rewardDrop;
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public Integer getDemand() {
        return this.demand;
    }
    
    public void setDemand(final Integer demand) {
        this.demand = demand;
    }
    
    public String getReward() {
        return this.reward;
    }
    
    public void setReward(final String reward) {
        this.reward = reward;
    }
    
    public BattleDrop getRewardDrop() {
        return this.rewardDrop;
    }
    
    public void setRewardDrop(final BattleDrop rewardDrop) {
        this.rewardDrop = rewardDrop;
    }
}
