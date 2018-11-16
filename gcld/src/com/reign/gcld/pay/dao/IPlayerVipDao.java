package com.reign.gcld.pay.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.pay.domain.*;
import java.util.*;

public interface IPlayerVipDao extends IBaseDao<PlayerVip>
{
    PlayerVip read(final int p0);
    
    PlayerVip readForUpdate(final int p0);
    
    List<PlayerVip> getModels();
    
    int getModelSize();
    
    int create(final PlayerVip p0);
    
    int deleteById(final int p0);
    
    String getVipStatus(final int p0);
    
    int setVipStatus(final int p0, final String p1);
    
    String getVipRemainingTimes(final int p0);
    
    int setVipRemainingTimes(final int p0, final String p1);
}
