package com.reign.gcld.user.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.user.domain.*;
import java.util.*;

public interface IUserDao extends IBaseDao<User>
{
    User read(final int p0);
    
    User readForUpdate(final int p0);
    
    List<User> getModels();
    
    int getModelSize();
    
    int create(final User p0);
    
    int deleteById(final int p0);
    
    User getUserByUserName(final String p0);
    
    int updateRewardFroce(final int p0, final int p1);
}
