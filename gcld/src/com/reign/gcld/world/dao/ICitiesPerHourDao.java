package com.reign.gcld.world.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.world.domain.*;
import java.util.*;

public interface ICitiesPerHourDao extends IBaseDao<CitiesPerHour>
{
    CitiesPerHour read(final int p0);
    
    CitiesPerHour readForUpdate(final int p0);
    
    List<CitiesPerHour> getModels();
    
    int getModelSize();
    
    int create(final CitiesPerHour p0);
    
    int deleteById(final int p0);
}
