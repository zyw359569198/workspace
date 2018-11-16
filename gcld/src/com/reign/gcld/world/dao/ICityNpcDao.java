package com.reign.gcld.world.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.world.domain.*;
import java.util.*;

public interface ICityNpcDao extends IBaseDao<CityNpc>
{
    CityNpc read(final int p0);
    
    CityNpc readForUpdate(final int p0);
    
    List<CityNpc> getModels();
    
    int getModelSize();
    
    int create(final CityNpc p0);
    
    int deleteById(final int p0);
    
    List<CityNpc> getByCityId(final int p0);
    
    int getSizeByCityId(final int p0);
    
    List<CityNpc> getByForceId(final int p0);
    
    int update(final CityNpc p0);
    
    int reduceHp(final int p0);
    
    int delByCityId(final int p0);
    
    int updateArmyHp(final int p0);
    
    int updateHpById(final Integer p0, final int p1);
}
