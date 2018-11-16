package com.reign.gcld.user.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.user.domain.*;
import java.util.*;

public interface IUserRewardDao extends IBaseDao<UserReward>
{
    UserReward read(final int p0);
    
    UserReward readForUpdate(final int p0);
    
    List<UserReward> getModels();
    
    int getModelSize();
    
    int create(final UserReward p0);
    
    int deleteById(final int p0);
    
    List<UserReward> getUserReward(final int p0, final String p1);
}
