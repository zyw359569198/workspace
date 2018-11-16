package com.reign.gcld.chat.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.chat.domain.*;
import java.util.*;

public interface IPlayerBlackDao extends IBaseDao<PlayerBlack>
{
    PlayerBlack read(final int p0);
    
    PlayerBlack readForUpdate(final int p0);
    
    List<PlayerBlack> getModels();
    
    int getModelSize();
    
    int create(final PlayerBlack p0);
    
    int deleteById(final int p0);
    
    List<PlayerBlack> getPlayerBlackList(final int p0);
    
    List<PlayerBlack> getBlackPlayerList(final int p0);
    
    PlayerBlack getPlayerBlackByBid(final int p0, final int p1);
}
