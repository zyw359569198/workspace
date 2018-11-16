package com.reign.gcld.battle.domain;

import com.reign.framework.mybatis.*;
import java.util.*;

public class OfficerBuildingInfo implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer vId;
    private Integer forceId;
    private Integer buildingId;
    private Date occupyTime;
    private Integer state;
    private Integer playerId;
    private byte[] battleData;
    private Integer memberCount;
    private Integer autoPass;
    
    public Integer getVId() {
        return this.vId;
    }
    
    public void setVId(final Integer vId) {
        this.vId = vId;
    }
    
    public Integer getForceId() {
        return this.forceId;
    }
    
    public void setForceId(final Integer forceId) {
        this.forceId = forceId;
    }
    
    public Integer getBuildingId() {
        return this.buildingId;
    }
    
    public void setBuildingId(final Integer buildingId) {
        this.buildingId = buildingId;
    }
    
    public Date getOccupyTime() {
        return this.occupyTime;
    }
    
    public void setOccupyTime(final Date occupyTime) {
        this.occupyTime = occupyTime;
    }
    
    public Integer getState() {
        return this.state;
    }
    
    public void setState(final Integer state) {
        this.state = state;
    }
    
    public Integer getPlayerId() {
        return this.playerId;
    }
    
    public void setPlayerId(final Integer playerId) {
        this.playerId = playerId;
    }
    
    public byte[] getBattleData() {
        return this.battleData;
    }
    
    public void setBattleData(final byte[] battleData) {
        this.battleData = battleData;
    }
    
    public Integer getMemberCount() {
        return this.memberCount;
    }
    
    public void setMemberCount(final Integer memberCount) {
        this.memberCount = memberCount;
    }
    
    public Integer getAutoPass() {
        return this.autoPass;
    }
    
    public void setAutoPass(final Integer autoPass) {
        this.autoPass = autoPass;
    }
}
