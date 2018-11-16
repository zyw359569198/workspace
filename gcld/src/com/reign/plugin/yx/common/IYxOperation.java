package com.reign.plugin.yx.common;

import com.reign.framework.netty.servlet.*;
import com.reign.util.*;
import java.util.*;

public interface IYxOperation
{
    Session login(final String p0, final String p1, final String p2, final String p3, final String p4, final String p5, final Request p6);
    
    Tuple<Integer, Integer> pay(final String p0, final int p1, final String p2, final String p3, final int p4, final Request p5);
    
    List<YxPlayerInfo> queryPlayer(final String p0, final String p1);
    
    YxPlayerInfo queryPlayer(final String p0, final String p1, final String p2);
    
    YxPlayerInfo queryPlayerByPlayerNameAndYx(final String p0, final String p1);
    
    List<YxPlayerPayInfo> queryPlayerPayDetails(final String p0, final String p1, final int p2);
    
    List<YxSourceInfo> queryPlayerYxSource(final String p0, final Date p1, final int p2, final int p3);
    
    int queryPlayerYxSourceSize(final String p0, final Date p1);
    
    List<YxPlayerPayInfo> queryYxPayDetails(final String p0, final Date p1, final Date p2);
    
    YxUserInfo getYxUserInfo(final Request p0);
    
    YxPlayerInfo getYxPlayerInfo(final Request p0);
    
    int getDefaultPayPlayer(final String p0, final String p1);
    
    YxPlayerInfo getDefaultPayPlayer(final Request p0);
    
    int getOnlinePlayersNumber(final String p0);
    
    YxPlayerInfo getPlayerById(final int p0);
    
    int getDailyOnlineTime(final int p0);
    
    YxPlayerPayInfo queryOrder(final String p0, final String p1);
    
    List<YxPlayerPayInfo> queryOrderByDateAndPage(final Date p0, final Date p1, final int p2, final int p3, final String p4);
    
    boolean banChat(final String p0, final int p1, final String p2);
    
    boolean unbanChat(final String p0, final String p1);
    
    boolean banUser(final String p0, final String p1);
    
    boolean unbanUser(final String p0, final String p1);
    
    List<YxNoticeInfo> queryNotice(final String p0);
    
    Tuple<Boolean, Integer> modifyUserId(final String p0, final String p1, final String p2);
    
    Session loginForTencent(final String p0, final String p1, final String p2, final YxTencentUserInfo p3, final Request p4);
    
    boolean checkEmptyParams(final Map<String, String> p0, final Request p1, final String p2);
    
    String exeTencentQuery(final String p0, final Map<String, Object> p1, final String p2, final String p3, final String p4, final boolean p5, final boolean p6);
    
    String getOpenAPISign(final String p0, final Map<String, Object> p1, final String p2, final String p3, final boolean p4);
    
    void setTencentPayMap(final Map<String, YxTencentPayInfo> p0);
    
    Map<String, YxTencentPayInfo> getTencentPayMap();
    
    void removeTencentPayMap(final String p0);
    
    boolean checkTencentPf(final String p0);
    
    Tuple<Integer, Integer> rewardWX(final int p0, final String p1, final String p2);
    
    int getAllPlayerNumber();
    
    List<PingAnPlayerInfo> queryPlayerInfo(final String p0, final String p1, final String p2);
    
    PingAnMoneyInfo queryMoneyInfo(final String p0, final String p1);
    
    int test360Privilege(final String p0, final int p1);
}
