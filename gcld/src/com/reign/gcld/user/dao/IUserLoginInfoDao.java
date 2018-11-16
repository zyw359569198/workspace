package com.reign.gcld.user.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.user.domain.*;
import java.util.*;

public interface IUserLoginInfoDao extends IBaseDao<UserLoginInfo>
{
    UserLoginInfo read(final int p0);
    
    UserLoginInfo readForUpdate(final int p0);
    
    List<UserLoginInfo> getModels();
    
    int getModelSize();
    
    int create(final UserLoginInfo p0);
    
    int deleteById(final int p0);
    
    UserLoginInfo getUserLoginInfo(final String p0, final String p1);
    
    void update(final UserLoginInfo p0);
}
