package com.reign.gcld.tickets.domain;

import com.reign.framework.mybatis.*;

public class PlayerTickets implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer playerId;
    private Integer tickets;
    private Integer noTips;
    
    public Integer getPlayerId() {
        return this.playerId;
    }
    
    public void setPlayerId(final Integer playerId) {
        this.playerId = playerId;
    }
    
    public Integer getTickets() {
        return this.tickets;
    }
    
    public void setTickets(final Integer tickets) {
        this.tickets = tickets;
    }
    
    public Integer getNoTips() {
        return this.noTips;
    }
    
    public void setNoTips(final Integer noTips) {
        this.noTips = noTips;
    }
}
