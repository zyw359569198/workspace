package com.reign.gcld.activity.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.activity.domain.*;
import java.util.*;

public interface IBonusActivityDao extends IBaseDao<BonusActivity>
{
    BonusActivity read(final int p0);
    
    BonusActivity readForUpdate(final int p0);
    
    List<BonusActivity> getModels();
    
    int getModelSize();
    
    int create(final BonusActivity p0);
    
    int deleteById(final int p0);
    
    int clearAll();
    
    int addConsumeGold(final int p0, final int p1);
    
    int getConsumeGold(final int p0);
}
