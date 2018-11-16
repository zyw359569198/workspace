package com.reign.gcld.rank.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.rank.domain.*;
import java.util.*;

public interface IPlayerOccupyCityDao extends IBaseDao<PlayerOccupyCity>
{
    PlayerOccupyCity read(final int p0);
    
    PlayerOccupyCity readForUpdate(final int p0);
    
    List<PlayerOccupyCity> getModels();
    
    int getModelSize();
    
    int create(final PlayerOccupyCity p0);
    
    int deleteById(final int p0);
    
    int getByPlayerId(final int p0);
    
    List<PlayerOccupyCity> getListByPlayerIdOrderByNum(final int p0);
    
    int updateVtimes(final int p0, final int p1, final int p2);
    
    PlayerOccupyCity getInfoByPAndG(final int p0, final int p1);
    
    int clearTodayNum();
    
    int addTodayNum(final int p0, final int p1, final int p2);
    
    int getTodayNumByPlayerId(final int p0);
    
    int clearTodayNumByPlayerId(final int p0);
    
    int setTodayNumOneGeneral(final int p0, final int p1);
}
