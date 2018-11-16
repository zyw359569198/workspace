package com.reign.gcld.activity.domain;

import com.reign.framework.mybatis.*;

public class BonusActivity implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer playerId;
    private Integer bonusGold;
    
    public Integer getPlayerId() {
        return this.playerId;
    }
    
    public void setPlayerId(final Integer playerId) {
        this.playerId = playerId;
    }
    
    public Integer getBonusGold() {
        return this.bonusGold;
    }
    
    public void setBonusGold(final Integer bonusGold) {
        this.bonusGold = bonusGold;
    }
}
