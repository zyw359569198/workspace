package com.reign.gcld.battle.domain;

import com.reign.framework.mybatis.*;

public class PlayerMineBatInfo implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer mineId;
    private String battleInfo;
    
    public Integer getMineId() {
        return this.mineId;
    }
    
    public void setMineId(final Integer mineId) {
        this.mineId = mineId;
    }
    
    public String getBattleInfo() {
        return this.battleInfo;
    }
    
    public void setBattleInfo(final String battleInfo) {
        this.battleInfo = battleInfo;
    }
}
