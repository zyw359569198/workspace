package com.reign.gcld.world.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.world.domain.*;
import java.util.*;

public interface ICountryRewardPerHourDao extends IBaseDao<CountryRewardPerHour>
{
    CountryRewardPerHour read(final int p0);
    
    CountryRewardPerHour readForUpdate(final int p0);
    
    List<CountryRewardPerHour> getModels();
    
    int getModelSize();
    
    int create(final CountryRewardPerHour p0);
    
    int deleteById(final int p0);
    
    CountryRewardPerHour getByHourAndForceId(final int p0, final int p1);
    
    int UpdateByHourAndForceId(final String p0, final int p1, final int p2);
}
