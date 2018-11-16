package com.reign.kf.comm.entity.kfwd.response;

public class KfwdTicketMarketInfo
{
    int pk;
    String rewardInfo;
    int ticketNum;
    
    public int getPk() {
        return this.pk;
    }
    
    public void setPk(final int pk) {
        this.pk = pk;
    }
    
    public String getRewardInfo() {
        return this.rewardInfo;
    }
    
    public void setRewardInfo(final String rewardInfo) {
        this.rewardInfo = rewardInfo;
    }
    
    public int getTicketNum() {
        return this.ticketNum;
    }
    
    public void setTicketNum(final int ticketNum) {
        this.ticketNum = ticketNum;
    }
}
