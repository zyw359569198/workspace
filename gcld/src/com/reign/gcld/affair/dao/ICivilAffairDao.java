package com.reign.gcld.affair.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.affair.domain.*;
import java.util.*;

public interface ICivilAffairDao extends IBaseDao<CivilAffair>
{
    CivilAffair read(final int p0);
    
    CivilAffair readForUpdate(final int p0);
    
    List<CivilAffair> getModels();
    
    int getModelSize();
    
    int create(final CivilAffair p0);
    
    int deleteById(final int p0);
    
    List<CivilAffair> getAffairList(final int p0, final int p1);
    
    List<CivilAffair> getAffairs(final int p0);
    
    int getRunningAffairCount(final int p0, final int p1);
    
    CivilAffair getAffair(final int p0, final int p1, final int p2);
    
    void updageStartTime(final int p0, final int p1, final int p2, final Date p3);
    
    void addAffairLevel(final int p0, final int p1);
    
    void updageCivilStartTime(final int p0, final int p1, final Date p2);
    
    void changeUpgradeShow(final int p0, final int p1);
}
