package com.reign.gcld.kfzb.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.kfzb.domain.*;
import java.util.*;

public interface IKfzbFeastDao extends IBaseDao<KfzbFeast>
{
    KfzbFeast read(final int p0);
    
    KfzbFeast readForUpdate(final int p0);
    
    List<KfzbFeast> getModels();
    
    int getModelSize();
    
    int create(final KfzbFeast p0);
    
    int deleteById(final int p0);
    
    int addDrink(final int p0, final int p1);
    
    int consumeDrink(final int p0, final int p1);
    
    int setFreeCardInit(final int p0);
    
    int consumeFreeCard(final int p0);
    
    int addGoldCard(final int p0, final int p1);
    
    int consumeGoldCard(final int p0);
    
    int getDrink(final int p0);
    
    int setXiqoqian(final int p0);
    
    int clearData();
    
    List<KfzbFeast> getMailList();
    
    int addFreeCard(final int p0, final int p1);
}
