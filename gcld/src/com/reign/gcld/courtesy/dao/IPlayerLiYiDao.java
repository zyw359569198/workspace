package com.reign.gcld.courtesy.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.courtesy.domain.*;
import java.util.*;

public interface IPlayerLiYiDao extends IBaseDao<PlayerLiYi>
{
    PlayerLiYi read(final int p0);
    
    PlayerLiYi readForUpdate(final int p0);
    
    List<PlayerLiYi> getModels();
    
    int getModelSize();
    
    int create(final PlayerLiYi p0);
    
    int deleteById(final int p0);
    
    int addliYiDu(final int p0, final int p1);
    
    int updateRewardInfo(final int p0, final String p1);
}
