package com.reign.kf.gw.kfwd.domain;

import com.reign.framework.hibernate.model.*;

public class KfwdTicketMarket implements IModel
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
