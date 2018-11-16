package com.reign.gcld.feat.domain;

import com.reign.framework.mybatis.*;

public class FeatBuilding implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer playerId;
    private Integer feat;
    
    public Integer getPlayerId() {
        return this.playerId;
    }
    
    public void setPlayerId(final Integer playerId) {
        this.playerId = playerId;
    }
    
    public Integer getFeat() {
        return this.feat;
    }
    
    public void setFeat(final Integer feat) {
        this.feat = feat;
    }
}
