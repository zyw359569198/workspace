package com.reign.kf.comm.entity.kfwd.response;

public class KfwdRankingRewardInfo
{
    private int id;
    private int rank;
    private int tickets;
    private int day;
    
    public void setId(final int id) {
        this.id = id;
    }
    
    public int getId() {
        return this.id;
    }
    
    public void setTickets(final int tickets) {
        this.tickets = tickets;
    }
    
    public int getTickets() {
        return this.tickets;
    }
    
    public void setRank(final int rank) {
        this.rank = rank;
    }
    
    public int getRank() {
        return this.rank;
    }
    
    public int getDay() {
        return this.day;
    }
    
    public void setDay(final int day) {
        this.day = day;
    }
}
