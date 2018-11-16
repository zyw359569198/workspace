package com.reign.gcld.player.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.player.domain.*;
import java.util.*;

public interface IPlayerNameDao extends IBaseDao<PlayerName>
{
    PlayerName read(final int p0);
    
    PlayerName readForUpdate(final int p0);
    
    List<PlayerName> getModels();
    
    int getModelSize();
    
    int create(final PlayerName p0);
    
    int deleteById(final int p0);
    
    List<String> getNameList(final int p0);
}
