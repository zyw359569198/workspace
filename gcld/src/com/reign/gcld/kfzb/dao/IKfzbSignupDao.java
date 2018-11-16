package com.reign.gcld.kfzb.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.kfzb.domain.*;
import java.util.*;

public interface IKfzbSignupDao extends IBaseDao<KfzbSignup>
{
    KfzbSignup read(final int p0);
    
    KfzbSignup readForUpdate(final int p0);
    
    List<KfzbSignup> getModels();
    
    int getModelSize();
    
    int create(final KfzbSignup p0);
    
    int deleteById(final int p0);
    
    KfzbSignup getByPlayerIdAndSeasonId(final int p0, final int p1);
    
    List<KfzbSignup> getBySeasonId(final int p0);
}
