package com.reign.gcld.kfwd.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.kfwd.domain.*;
import java.util.*;

public interface IKfwdSignupDao extends IBaseDao<KfwdSignup>
{
    KfwdSignup read(final int p0);
    
    KfwdSignup readForUpdate(final int p0);
    
    List<KfwdSignup> getModels();
    
    int getModelSize();
    
    int create(final KfwdSignup p0);
    
    int deleteById(final int p0);
    
    List<KfwdSignup> getSignUpInfoBySeasonIdAndSchduleId(final int p0, final int p1);
}
