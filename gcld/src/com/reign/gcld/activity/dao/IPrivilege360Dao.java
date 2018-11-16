package com.reign.gcld.activity.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.activity.domain.*;
import java.util.*;

public interface IPrivilege360Dao extends IBaseDao<Privilege360>
{
    Privilege360 read(final int p0);
    
    Privilege360 readForUpdate(final int p0);
    
    List<Privilege360> getModels();
    
    int getModelSize();
    
    int create(final Privilege360 p0);
    
    int deleteById(final int p0);
    
    int setStatusByPid(final int p0, final int p1);
    
    int setTitleByPid(final int p0, final String p1);
    
    List<Privilege360> getTitleList();
}
