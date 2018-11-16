package com.reign.gcld.world.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.world.domain.*;
import java.util.*;

public interface ICityNpcLostDao extends IBaseDao<CityNpcLost>
{
    CityNpcLost read(final int p0);
    
    CityNpcLost readForUpdate(final int p0);
    
    List<CityNpcLost> getModels();
    
    int getModelSize();
    
    int create(final CityNpcLost p0);
    
    int deleteById(final int p0);
    
    int updateNpcLost(final int p0, final String p1);
}
