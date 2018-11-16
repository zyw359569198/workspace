package com.reign.gcld.kfwd.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.kfwd.domain.*;
import java.util.*;

public interface IKfwdMatchSignDao extends IBaseDao<KfwdMatchSign>
{
    KfwdMatchSign read(final int p0);
    
    KfwdMatchSign readForUpdate(final int p0);
    
    List<KfwdMatchSign> getModels();
    
    int getModelSize();
    
    int create(final KfwdMatchSign p0);
    
    int deleteById(final int p0);
    
    KfwdMatchSign getWorldMatchSign(final String p0, final int p1);
    
    void update(final KfwdMatchSign p0);
    
    List<KfwdMatchSign> getWorldMatchSignListByMatchTag(final String p0);
}
