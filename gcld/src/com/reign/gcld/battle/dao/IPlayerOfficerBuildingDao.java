package com.reign.gcld.battle.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.battle.domain.*;
import java.util.*;

public interface IPlayerOfficerBuildingDao extends IBaseDao<PlayerOfficerBuilding>
{
    PlayerOfficerBuilding read(final int p0);
    
    PlayerOfficerBuilding readForUpdate(final int p0);
    
    List<PlayerOfficerBuilding> getModels();
    
    int getModelSize();
    
    int create(final PlayerOfficerBuilding p0);
    
    int deleteById(final int p0);
    
    List<PlayerOfficerBuilding> getBuildingMembers(final int p0, final int p1);
    
    void deleteByState(final int p0, final int p1, final int p2);
    
    void changeLeader(final int p0, final int p1);
    
    List<PlayerOfficerBuilding> getApplyingMembers(final int p0, final int p1);
    
    void updateState(final int p0, final int p1);
    
    void deleteByBuildingId(final int p0, final int p1);
    
    void updateIsNew(final int p0, final int p1);
    
    Integer getOwnerIdByBuilding(final Integer p0, final int p1);
}
