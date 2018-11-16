package com.reign.gcld.system.service;

import java.util.*;
import com.reign.framework.netty.servlet.*;
import com.alibaba.fastjson.*;
import com.reign.util.*;

public interface ISystemService
{
    void printOnlineNum();
    
    byte[] banChat(final String p0, final String p1, final long p2, final String p3);
    
    byte[] unbanChat(final String p0, final String p1);
    
    byte[] getBanRecord(final String p0, final String p1);
    
    byte[] banUser(final String p0, final String p1, final long p2, final String p3);
    
    byte[] unbanUser(final String p0, final String p1);
    
    byte[] getUserBanListByYx(final String p0);
    
    void initIntercepterBlockMap();
    
    byte[] getPlayerInfo(final String p0, final String p1, final String p2);
    
    byte[] repay(final String p0, final int p1, final String p2, final int p3, final String p4, final Date p5, final int p6, final Request p7);
    
    byte[] yxPayData(final Date p0, final Date p1, final String p2);
    
    byte[] yxPayHistory(final Date p0, final Date p1, final String p2);
    
    void statisticsGold();
    
    byte[] getGiftContent();
    
    byte[] addGift(final JSONObject p0);
    
    byte[] backpay(final String p0, final int p1, final String p2, final Request p3);
    
    byte[] sendMail(final int p0, final int p1, final String p2, final String p3, final String p4, final String p5, final String p6);
    
    byte[] activity(final int p0, final String p1, final String p2, final String p3);
    
    byte[] activityList();
    
    Tuple<Boolean, String> checkPayActivity(final int p0, final long p1, final long p2, final String p3);
    
    void initAllActivity();
    
    byte[] gmAuthority(final String p0, final int p1);
    
    byte[] getGmInfo();
    
    byte[] consumeGold(final String p0);
    
    void initTicketActivityBegin(final String p0);
    
    void initTicketActivityEnd(final String p0);
    
    byte[] banChat2(final String p0, final String p1, final long p2, final String p3);
    
    byte[] unbanChat2(final String p0, final String p1);
    
    byte[] getBanRecord2(final String p0);
    
    void blockReward(final int p0, final int p1);
    
    byte[] rtblockByIds(final String p0);
    
    byte[] rtblockByNames(final String p0);
}
