package com.reign.gcld.pay.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.pay.domain.*;
import java.util.*;

public interface IPlayerPayDao extends IBaseDao<PlayerPay>
{
    PlayerPay read(final int p0);
    
    PlayerPay readForUpdate(final int p0);
    
    List<PlayerPay> getModels();
    
    int getModelSize();
    
    int create(final PlayerPay p0);
    
    int deleteById(final int p0);
    
    List<PlayerPay> getPlayerPayByPlayerId(final int p0);
    
    List<PlayerPay> getPlayerPayByDateAndYx(final Date p0, final Date p1, final String p2);
    
    List<PlayerPay> getPlayerPayByDateAndPage(final Date p0, final Date p1, final int p2, final int p3, final String p4);
    
    int queryPaySum(final int p0);
    
    boolean containsOrderId(final String p0, final String p1);
    
    PlayerPay queryOrder(final String p0, final String p1);
    
    int getTotalGold(final Date p0, final Date p1, final String p2);
    
    int getPlayerCount(final Date p0, final Date p1, final String p2);
    
    int getOrderCount(final Date p0, final Date p1, final String p2);
}
