package com.reign.kf.gw.kfwd.domain;

import com.reign.framework.hibernate.model.*;

public class KfwdRankingReward implements IModel
{
    private int id;
    private int rank;
    private int tickets;
    private int day;
    
    public int getId() {
        return this.id;
    }
    
    public void setId(final int id) {
        this.id = id;
    }
    
    public int getRank() {
        return this.rank;
    }
    
    public void setRank(final int rank) {
        this.rank = rank;
    }
    
    public int getTickets() {
        return this.tickets;
    }
    
    public void setTickets(final int tickets) {
        this.tickets = tickets;
    }
    
    public int getDay() {
        return this.day;
    }
    
    public void setDay(final int day) {
        this.day = day;
    }
}
