package com.reign.gcld.pay.service;

import com.reign.gcld.player.dto.*;
import com.reign.framework.netty.servlet.*;
import com.reign.gcld.player.domain.*;
import java.util.*;
import com.reign.util.*;
import com.reign.framework.json.*;

public interface IPayService
{
    byte[] getVipInfo(final PlayerDto p0);
    
    byte[] pay(final String p0, final int p1, final String p2, final String p3, final int p4, final String p5, final Request p6);
    
    boolean commonPay(final String p0, final int p1, final String p2, final String p3, final int p4, final Object p5, final Player p6, final Date p7, final String p8, final Request p9);
    
    boolean addAdditionalGold(final int p0, final int p1);
    
    void addTicketGold(final int p0, final int p1);
    
    byte[] handleVipPrivilege(final String p0, final PlayerDto p1);
    
    byte[] getPayAcitivityInfo(final PlayerDto p0);
    
    byte[] getTicketAcitivityInfo(final PlayerDto p0);
    
    void pushPayActivityInfo(final String p0);
    
    void pushPayActivityInfoEnd(final String p0);
    
    Tuple<Boolean, String> updateVipTimes(final int p0, final int p1, final int p2);
    
    void checkVipForLogin(final Player p0, final JsonDocument p1);
    
    byte[] getYellowVipInfo(final PlayerDto p0);
    
    byte[] recvYellowVipReward(final PlayerDto p0, final int p1, final int p2);
    
    void resetTxVipStatus();
    
    void addTxYellowVipInfo(final int p0, final JsonDocument p1);
}
