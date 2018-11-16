package com.reign.gcld.rank.domain;

import com.reign.framework.mybatis.*;

public class PlayerOccupyCity implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer vid;
    private Integer playerId;
    private Integer generalId;
    private Integer occupyCityNum;
    
    public Integer getVid() {
        return this.vid;
    }
    
    public void setVid(final Integer vid) {
        this.vid = vid;
    }
    
    public Integer getPlayerId() {
        return this.playerId;
    }
    
    public void setPlayerId(final Integer playerId) {
        this.playerId = playerId;
    }
    
    public Integer getGeneralId() {
        return this.generalId;
    }
    
    public void setGeneralId(final Integer generalId) {
        this.generalId = generalId;
    }
    
    public Integer getOccupyCityNum() {
        return this.occupyCityNum;
    }
    
    public void setOccupyCityNum(final Integer occupyCityNum) {
        this.occupyCityNum = occupyCityNum;
    }
}
