package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;
import com.reign.gcld.sdata.common.*;

public class QualifyingLevel implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer rank;
    private Integer group;
    private Integer chief;
    private String npcs;
    private String reward;
    private ChiefNpc chiefNpc;
    
    public Integer getRank() {
        return this.rank;
    }
    
    public void setRank(final Integer rank) {
        this.rank = rank;
    }
    
    public Integer getGroup() {
        return this.group;
    }
    
    public void setGroup(final Integer group) {
        this.group = group;
    }
    
    public Integer getChief() {
        return this.chief;
    }
    
    public void setChief(final Integer chief) {
        this.chief = chief;
    }
    
    public String getNpcs() {
        return this.npcs;
    }
    
    public void setNpcs(final String npcs) {
        this.npcs = npcs;
    }
    
    public String getReward() {
        return this.reward;
    }
    
    public void setReward(final String reward) {
        this.reward = reward;
    }
    
    public ChiefNpc getChiefNpc() {
        return this.chiefNpc;
    }
    
    public void setChiefNpc(final ChiefNpc chiefNpc) {
        this.chiefNpc = chiefNpc;
    }
}
