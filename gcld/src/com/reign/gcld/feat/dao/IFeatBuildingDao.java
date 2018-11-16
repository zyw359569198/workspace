package com.reign.gcld.feat.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.feat.domain.*;
import java.util.*;

public interface IFeatBuildingDao extends IBaseDao<FeatBuilding>
{
    FeatBuilding read(final int p0);
    
    FeatBuilding readForUpdate(final int p0);
    
    List<FeatBuilding> getModels();
    
    int getModelSize();
    
    int create(final FeatBuilding p0);
    
    int deleteById(final int p0);
    
    int getFeat(final int p0);
    
    int addFeat(final int p0, final int p1);
    
    int resetFeat(final int p0);
}
