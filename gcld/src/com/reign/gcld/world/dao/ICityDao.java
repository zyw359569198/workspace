package com.reign.gcld.world.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.world.domain.*;
import java.util.*;

public interface ICityDao extends IBaseDao<City>
{
    City read(final int p0);
    
    City readForUpdate(final int p0);
    
    List<City> getModels();
    
    int getModelSize();
    
    int create(final City p0);
    
    int deleteById(final int p0);
    
    int updateTitle(final int p0, final int p1);
    
    int updateState(final int p0, final int p1);
    
    int updateForceIdState(final int p0, final int p1, final int p2);
    
    List<City> getForceCities(final int p0);
    
    int addGNum(final int p0, final int p1);
    
    int reduceGNum(final int p0, final int p1);
    
    int getForceCounts(final int p0);
    
    int updateTrickInfo(final int p0, final String p1);
    
    int updateBorder(final int p0, final int p1);
    
    int initUpdateBorder(final int p0, final int p1);
    
    int resetBorder();
    
    int resetTitle();
    
    void updateJobId(final Integer p0, final int p1);
    
    void updateHp(final int p0, final int p1);
    
    void updateHpMaxHp(final int p0, final int p1, final int p2);
    
    List<Integer> getCityIdListByForceId(final int p0);
    
    int updateOtherInfo(final int p0, final String p1);
    
    int updateForceIdStateTitleBorder(final int p0, final int p1, final int p2, final int p3, final int p4);
}
