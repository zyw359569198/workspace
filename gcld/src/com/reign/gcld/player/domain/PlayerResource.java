package com.reign.gcld.player.domain;

import com.reign.framework.mybatis.*;
import java.util.*;

public class PlayerResource implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer playerId;
    private Integer copper;
    private Integer wood;
    private Integer food;
    private Integer iron;
    private Integer exp;
    private Date updateTime;
    private Long kfgzVersion;
    
    public Integer getPlayerId() {
        return this.playerId;
    }
    
    public void setPlayerId(final Integer playerId) {
        this.playerId = playerId;
    }
    
    public Integer getCopper() {
        return this.copper;
    }
    
    public void setCopper(final Integer copper) {
        this.copper = copper;
    }
    
    public Integer getWood() {
        return this.wood;
    }
    
    public void setWood(final Integer wood) {
        this.wood = wood;
    }
    
    public Integer getFood() {
        return this.food;
    }
    
    public void setFood(final Integer food) {
        this.food = food;
    }
    
    public Integer getIron() {
        return this.iron;
    }
    
    public void setIron(final Integer iron) {
        this.iron = iron;
    }
    
    public Integer getExp() {
        return this.exp;
    }
    
    public void setExp(final Integer exp) {
        this.exp = exp;
    }
    
    public Date getUpdateTime() {
        return this.updateTime;
    }
    
    public void setUpdateTime(final Date updateTime) {
        this.updateTime = updateTime;
    }
    
    public Long getKfgzVersion() {
        return this.kfgzVersion;
    }
    
    public void setKfgzVersion(final Long kfgzVersion) {
        this.kfgzVersion = kfgzVersion;
    }
}
