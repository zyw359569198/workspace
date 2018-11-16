package com.reign.kf.comm.entity.kfwd.response;

public class KfwdTributeRuleInfo
{
    private int id;
    private int score;
    private int tickets;
    private int gold;
    private String name;
    
    public void setId(final int id) {
        this.id = id;
    }
    
    public int getId() {
        return this.id;
    }
    
    public void setScore(final int score) {
        this.score = score;
    }
    
    public int getScore() {
        return this.score;
    }
    
    public void setTickets(final int tickets) {
        this.tickets = tickets;
    }
    
    public int getTickets() {
        return this.tickets;
    }
    
    public void setGold(final int gold) {
        this.gold = gold;
    }
    
    public int getGold() {
        return this.gold;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }
}
