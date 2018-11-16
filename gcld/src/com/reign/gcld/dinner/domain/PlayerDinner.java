package com.reign.gcld.dinner.domain;

import com.reign.framework.mybatis.*;

public class PlayerDinner implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer playerId;
    private Integer dinnerNum;
    
    public Integer getPlayerId() {
        return this.playerId;
    }
    
    public void setPlayerId(final Integer playerId) {
        this.playerId = playerId;
    }
    
    public Integer getDinnerNum() {
        return this.dinnerNum;
    }
    
    public void setDinnerNum(final Integer dinnerNum) {
        this.dinnerNum = dinnerNum;
    }
}
