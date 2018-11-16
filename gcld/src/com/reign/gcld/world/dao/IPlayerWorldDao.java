package com.reign.gcld.world.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.world.domain.*;
import java.util.*;

public interface IPlayerWorldDao extends IBaseDao<PlayerWorld>
{
    PlayerWorld read(final int p0);
    
    PlayerWorld readForUpdate(final int p0);
    
    List<PlayerWorld> getModels();
    
    int getModelSize();
    
    int create(final PlayerWorld p0);
    
    int deleteById(final int p0);
    
    int addRewardNum(final int p0);
    
    int reduceRewardNum(final int p0, final int p1);
    
    int updateAttInfo(final int p0, final String p1, final String p2);
    
    List<Integer> getByRewardNum(final int p0);
    
    int addRewards(final int p0, final String p1);
    
    int reduceReward(final int p0, final String p1, final String p2);
    
    void updateBoxInfo(final int p0, final String p1);
    
    void updateQuizInfo(final int p0, final int p1);
    
    void updateNpcLostDetail(final int p0, final String p1);
    
    int clearRewardNum(final int p0);
}
