package com.reign.gcld.battle.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.battle.domain.*;
import java.util.*;

public interface ICityDefenceNpcDao extends IBaseDao<CityDefenceNpc>
{
    CityDefenceNpc read(final int p0);
    
    CityDefenceNpc readForUpdate(final int p0);
    
    List<CityDefenceNpc> getModels();
    
    int getModelSize();
    
    int create(final CityDefenceNpc p0);
    
    int deleteById(final int p0);
    
    int updateDefenceNpc(final int p0, final CityDefenceNpc p1);
}
