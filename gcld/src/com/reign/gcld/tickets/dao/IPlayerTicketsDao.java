package com.reign.gcld.tickets.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.tickets.domain.*;
import java.util.*;

public interface IPlayerTicketsDao extends IBaseDao<PlayerTickets>
{
    PlayerTickets safeGetPlayerTickets(final int p0);
    
    PlayerTickets read(final int p0);
    
    PlayerTickets readForUpdate(final int p0);
    
    List<PlayerTickets> getModels();
    
    int getModelSize();
    
    int create(final PlayerTickets p0);
    
    int deleteById(final int p0);
    
    void addTickets(final int p0, final int p1, final Object p2, final boolean p3);
    
    boolean consumeTickets(final int p0, final int p1, final Object p2);
}
