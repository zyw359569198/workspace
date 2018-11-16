package com.reign.gcld.kfgz.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.kfgz.domain.*;
import java.util.*;

public interface IKfgzSignupDao extends IBaseDao<KfgzSignup>
{
    KfgzSignup read(final int p0);
    
    KfgzSignup readForUpdate(final int p0);
    
    List<KfgzSignup> getModels();
    
    int getModelSize();
    
    int create(final KfgzSignup p0);
    
    int deleteById(final int p0);
    
    KfgzSignup getByCid(final int p0);
}
