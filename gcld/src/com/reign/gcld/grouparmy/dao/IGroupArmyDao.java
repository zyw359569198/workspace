package com.reign.gcld.grouparmy.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.grouparmy.domain.*;
import java.util.*;

public interface IGroupArmyDao extends IBaseDao<GroupArmy>
{
    GroupArmy read(final int p0);
    
    GroupArmy readForUpdate(final int p0);
    
    List<GroupArmy> getModels();
    
    int getModelSize();
    
    int create(final GroupArmy p0);
    
    int deleteById(final int p0);
    
    List<GroupArmy> getByNowCity(final int p0);
    
    void updateLeader(final int p0, final int p1);
    
    void updateSpeed(final int p0, final float p1);
    
    GroupArmy getBy2Id(final int p0, final int p1);
    
    int updateNowCity(final int p0, final int p1);
    
    List<GroupArmy> getByLeaderId(final int p0);
    
    int deleteByLeaderId(final int p0);
}
