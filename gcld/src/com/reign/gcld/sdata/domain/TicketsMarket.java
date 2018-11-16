package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;

public class TicketsMarket implements IModel
{
    private static final long serialVersionUID = 1L;
    private int id;
    private String reward;
    private int tickets;
    private String pic;
    private int see_lv;
    private int buy_lv;
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public void setReward(final String reward) {
        this.reward = reward;
    }
    
    public String getReward() {
        return this.reward;
    }
    
    public void setTickets(final int tickets) {
        this.tickets = tickets;
    }
    
    public int getTickets() {
        return this.tickets;
    }
    
    public void setPic(final String pic) {
        this.pic = pic;
    }
    
    public String getPic() {
        return this.pic;
    }
    
    public void setSee_lv(final int see_lv) {
        this.see_lv = see_lv;
    }
    
    public int getSee_lv() {
        return this.see_lv;
    }
    
    public void setBuy_lv(final int buy_lv) {
        this.buy_lv = buy_lv;
    }
    
    public int getBuy_lv() {
        return this.buy_lv;
    }
}
