package com.reign.gcld.player.dao;

import com.reign.framework.mybatis.*;
import java.util.*;
import com.reign.gcld.player.domain.*;

public interface ILoginRewardDao extends IBaseDao<LoginReward>
{
    LoginReward read(final int p0);
    
    LoginReward readForUpdate(final int p0);
    
    List<LoginReward> getModels();
    
    int getModelSize();
    
    int create(final LoginReward p0);
    
    int deleteById(final int p0);
    
    int received(final int p0, final int p1);
    
    int receivedAll(final int p0);
    
    int deleteByTotalDay(final int p0);
    
    int updateRecord(final int p0, final int p1, final int p2, final Date p3);
    
    List<PlayerTotalDay> getList(final List<Integer> p0);
    
    int battchUpdate(final List<Integer> p0);
}
