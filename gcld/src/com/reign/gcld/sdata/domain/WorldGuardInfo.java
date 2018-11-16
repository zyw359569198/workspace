package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;

public class WorldGuardInfo implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer distance;
    private Integer npcDegreeBase;
    private Integer npcNum;
    
    public Integer getDistance() {
        return this.distance;
    }
    
    public void setDistance(final Integer distance) {
        this.distance = distance;
    }
    
    public Integer getNpcDegreeBase() {
        return this.npcDegreeBase;
    }
    
    public void setNpcDegreeBase(final Integer npcDegreeBase) {
        this.npcDegreeBase = npcDegreeBase;
    }
    
    public Integer getNpcNum() {
        return this.npcNum;
    }
    
    public void setNpcNum(final Integer npcNum) {
        this.npcNum = npcNum;
    }
}
