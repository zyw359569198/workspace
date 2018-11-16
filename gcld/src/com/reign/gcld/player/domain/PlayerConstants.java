package com.reign.gcld.player.domain;

import com.reign.framework.mybatis.*;

public class PlayerConstants implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer playerId;
    private Integer extraNum;
    private String vipExpression;
    
    public Integer getPlayerId() {
        return this.playerId;
    }
    
    public void setPlayerId(final Integer playerId) {
        this.playerId = playerId;
    }
    
    public Integer getExtraNum() {
        return this.extraNum;
    }
    
    public void setExtraNum(final Integer extraNum) {
        this.extraNum = extraNum;
    }
    
    public String getVipExpression() {
        return this.vipExpression;
    }
    
    public void setVipExpression(final String vipExpression) {
        this.vipExpression = vipExpression;
    }
}
