package com.reign.gcld.battle.domain;

import com.reign.framework.mybatis.*;
import java.util.*;

public class BarbarainPhantom implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer vId;
    private Integer npcType;
    private Integer state;
    private Date createTime;
    private Integer locationId;
    private Integer forceId;
    private Integer barbarainId;
    private Integer armyId;
    private Integer hp;
    private Integer tacticval;
    private String name;
    public static final int BARBARAIN_PHANTOM_TABLE_TYPE_1 = 1;
    public static final int BARBARAIN_PHANTOM_TABLE_TYPE_2 = 2;
    public static final int BARBARAIN_PHANTOM_TABLE_TYPE_3 = 3;
    public static final int BARBARAIN_PHANTOM_TABLE_TYPE_4 = 4;
    
    public Integer getVId() {
        return this.vId;
    }
    
    public void setVId(final Integer vId) {
        this.vId = vId;
    }
    
    public Integer getNpcType() {
        return this.npcType;
    }
    
    public void setNpcType(final Integer npcType) {
        this.npcType = npcType;
    }
    
    public Integer getState() {
        return this.state;
    }
    
    public void setState(final Integer state) {
        this.state = state;
    }
    
    public Date getCreateTime() {
        return this.createTime;
    }
    
    public void setCreateTime(final Date createTime) {
        this.createTime = createTime;
    }
    
    public Integer getLocationId() {
        return this.locationId;
    }
    
    public void setLocationId(final Integer locationId) {
        this.locationId = locationId;
    }
    
    public Integer getForceId() {
        return this.forceId;
    }
    
    public void setForceId(final Integer forceId) {
        this.forceId = forceId;
    }
    
    public Integer getBarbarainId() {
        return this.barbarainId;
    }
    
    public void setBarbarainId(final Integer barbarainId) {
        this.barbarainId = barbarainId;
    }
    
    public Integer getArmyId() {
        return this.armyId;
    }
    
    public void setArmyId(final Integer armyId) {
        this.armyId = armyId;
    }
    
    public Integer getHp() {
        return this.hp;
    }
    
    public void setHp(final Integer hp) {
        this.hp = hp;
    }
    
    public Integer getTacticval() {
        return this.tacticval;
    }
    
    public void setTacticval(final Integer tacticval) {
        this.tacticval = tacticval;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
}
