package com.reign.gcld.grouparmy.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.grouparmy.domain.*;
import java.util.*;

public interface IPlayerGroupArmyDao extends IBaseDao<PlayerGroupArmy>
{
    PlayerGroupArmy read(final int p0);
    
    PlayerGroupArmy readForUpdate(final int p0);
    
    List<PlayerGroupArmy> getModels();
    
    int getModelSize();
    
    int create(final PlayerGroupArmy p0);
    
    int deleteById(final int p0);
    
    PlayerGroupArmy getPlayerGroupArmy(final int p0, final int p1);
    
    List<PlayerGroupArmy> getList(final int p0);
    
    void updateIsLeader(final int p0, final int p1);
    
    Map<Integer, PlayerGroupArmy> getGroupArmies(final int p0);
    
    int deleteByArmyId(final int p0);
    
    int updateArmyId(final int p0, final int p1);
    
    int deleteByPlayerId(final int p0);
    
    int getCountByArmyId(final int p0);
}
