package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;

public class KillToplistTreasure implements IModel, Comparable<KillToplistTreasure>
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private String reward;
    private Integer prob;
    private Integer minLv;
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public String getReward() {
        return this.reward;
    }
    
    public void setReward(final String reward) {
        this.reward = reward;
    }
    
    public Integer getProb() {
        return this.prob;
    }
    
    public void setProb(final Integer prob) {
        this.prob = prob;
    }
    
    public Integer getMinLv() {
        return this.minLv;
    }
    
    public void setMinLv(final Integer minLv) {
        this.minLv = minLv;
    }
    
    @Override
	public int compareTo(final KillToplistTreasure o) {
        if (this.getId() >= o.getId()) {
            return 1;
        }
        return 0;
    }
}
