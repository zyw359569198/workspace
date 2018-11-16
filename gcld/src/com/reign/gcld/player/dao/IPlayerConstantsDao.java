package com.reign.gcld.player.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.player.domain.*;
import java.util.*;

public interface IPlayerConstantsDao extends IBaseDao<PlayerConstants>
{
    PlayerConstants read(final int p0);
    
    PlayerConstants readForUpdate(final int p0);
    
    List<PlayerConstants> getModels();
    
    int getModelSize();
    
    int create(final PlayerConstants p0);
    
    int deleteById(final int p0);
    
    void updateExtraNum(final int p0, final int p1);
    
    void updateExpression(final int p0, final String p1);
}
