package com.reign.gcld.player.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.player.domain.*;
import java.util.*;

public interface IPlayerBakDao extends IBaseDao<PlayerBak>
{
    PlayerBak read(final int p0);
    
    PlayerBak readForUpdate(final int p0);
    
    List<PlayerBak> getModels();
    
    int getModelSize();
    
    int create(final PlayerBak p0);
    
    int deleteById(final int p0);
}
