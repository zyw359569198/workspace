package com.reign.kfgz.service;

import com.reign.kfgz.comm.*;

public interface IKfgzOrderService
{
    byte[] useOrder(final KfPlayerInfo p0, final int p1);
    
    byte[] getOrderTokenTeamInfo(final KfPlayerInfo p0, final int p1);
    
    byte[] doRushInOrderTokenTeam(final KfPlayerInfo p0, final int p1, final String p2);
}
