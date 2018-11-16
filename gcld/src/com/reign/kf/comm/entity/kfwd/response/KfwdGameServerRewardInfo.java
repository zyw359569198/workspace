package com.reign.kf.comm.entity.kfwd.response;

import java.util.*;

public class KfwdGameServerRewardInfo
{
    List<KfwdTicketResultInfo> list;
    
    public List<KfwdTicketResultInfo> getList() {
        return this.list;
    }
    
    public void setList(final List<KfwdTicketResultInfo> list) {
        this.list = list;
    }
}
