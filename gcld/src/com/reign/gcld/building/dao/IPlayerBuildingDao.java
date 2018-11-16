package com.reign.gcld.building.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.building.domain.*;
import java.util.*;

public interface IPlayerBuildingDao extends IBaseDao<PlayerBuilding>
{
    PlayerBuilding read(final int p0);
    
    PlayerBuilding readForUpdate(final int p0);
    
    List<PlayerBuilding> getModels();
    
    int getModelSize();
    
    int create(final PlayerBuilding p0);
    
    int deleteById(final int p0);
    
    PlayerBuilding getPlayerBuilding(final int p0, final int p1);
    
    int upgradeBuilding(final int p0, final int p1, final int p2, final int p3, final int p4);
    
    void updateBuildingNewState(final int p0, final int p1, final int p2);
    
    void upgradeBuildingState(final int p0, final int p1, final int p2, final int p3);
    
    List<PlayerBuilding> getPlayerBuildingByAreaId(final int p0, final int p1);
    
    List<PlayerBuilding> getPlayerBuildingWithoutEvent(final int p0);
    
    List<PlayerBuilding> getPlayerBuildingWithoutEvent2(final int p0, final int p1);
    
    List<PlayerBuilding> getPlayerBuildingWithEvent(final int p0);
    
    PlayerBuilding getNextBuildingWithEvent(final int p0, final int p1);
    
    void updateEventId(final PlayerBuilding p0, final int p1);
    
    List<PlayerBuilding> getPlayerBuildings(final int p0);
    
    void upgradeBuildingLv(final int p0, final int p1);
    
    List<PlayerBuilding> getPlayerBuildingByType(final int p0, final int p1);
    
    int update(final PlayerBuilding p0);
}
