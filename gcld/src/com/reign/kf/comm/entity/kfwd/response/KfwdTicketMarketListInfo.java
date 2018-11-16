package com.reign.kf.comm.entity.kfwd.response;

import java.util.*;

public class KfwdTicketMarketListInfo
{
    List<KfwdTicketMarketInfo> list;
    
    public KfwdTicketMarketListInfo() {
        this.list = new ArrayList<KfwdTicketMarketInfo>();
    }
    
    public List<KfwdTicketMarketInfo> getList() {
        return this.list;
    }
    
    public void setList(final List<KfwdTicketMarketInfo> list) {
        this.list = list;
    }
}
