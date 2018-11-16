package com.reign.gcld.world.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.world.domain.*;
import java.util.*;

public interface IPlayerFarmDao extends IBaseDao<PlayerFarm>
{
    PlayerFarm read(final int p0);
    
    PlayerFarm readForUpdate(final int p0);
    
    List<PlayerFarm> getModels();
    
    int getModelSize();
    
    int create(final PlayerFarm p0);
    
    int deleteById(final int p0);
    
    PlayerFarm getByPAndGId(final int p0, final int p1);
    
    List<PlayerFarm> getListByPid(final int p0);
    
    List<PlayerFarm> getFarmsAfterNow();
    
    int deletByPAndGId(final int p0, final int p1);
    
    int deletByPlayerId(final int p0);
}
