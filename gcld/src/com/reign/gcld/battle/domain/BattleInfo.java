package com.reign.gcld.battle.domain;

import com.reign.framework.mybatis.*;

public class BattleInfo implements IModel
{
    private static final long serialVersionUID = 1L;
    private String battleId;
    private Integer attForceId;
    
    public String getBattleId() {
        return this.battleId;
    }
    
    public void setBattleId(final String battleId) {
        this.battleId = battleId;
    }
    
    public Integer getAttForceId() {
        return this.attForceId;
    }
    
    public void setAttForceId(final Integer attForceId) {
        this.attForceId = attForceId;
    }
}
