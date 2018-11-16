package com.reign.gcld.building.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.building.domain.*;
import java.util.*;

public interface IPlayerBuildingWorkDao extends IBaseDao<PlayerBuildingWork>
{
    PlayerBuildingWork read(final int p0);
    
    PlayerBuildingWork readForUpdate(final int p0);
    
    List<PlayerBuildingWork> getModels();
    
    int getModelSize();
    
    int create(final PlayerBuildingWork p0);
    
    int deleteById(final int p0);
    
    PlayerBuildingWork getFreeWork(final int p0, final Date p1);
    
    List<PlayerBuildingWork> getPlayerBuildingWork(final int p0);
    
    int assignedWork(final PlayerBuildingWork p0);
    
    PlayerBuildingWork getPlayerBuildingWork(final int p0, final int p1);
    
    int resetBuildingWork(final int p0, final int p1);
    
    int updateEndTime(final int p0, final Date p1);
    
    List<PlayerBuildingWork> getBusyWorkList(final int p0);
    
    int getBusyWorkNum(final int p0);
    
    int getFreeWorkNum(final int p0);
}
