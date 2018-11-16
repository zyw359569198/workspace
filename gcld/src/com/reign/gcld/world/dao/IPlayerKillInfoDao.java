package com.reign.gcld.world.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.world.domain.*;
import java.util.*;

public interface IPlayerKillInfoDao extends IBaseDao<PlayerKillInfo>
{
    PlayerKillInfo read(final int p0);
    
    PlayerKillInfo readForUpdate(final int p0);
    
    List<PlayerKillInfo> getModels();
    
    int getModelSize();
    
    int create(final PlayerKillInfo p0);
    
    int deleteById(final int p0);
    
    PlayerKillInfo getByTodayInfo(final int p0, final String p1);
    
    List<PlayerKillInfo> getListByKillNum(final int p0, final String p1);
    
    int updateKillNum(final int p0, final int p1, final String p2);
    
    int deleteByDate(final String p0);
    
    List<PlayerKillInfo> getListByPlayerId(final int p0);
    
    int updateBoxInfo(final int p0, final String p1);
    
    int resetBoxInfo(final String p0, final String p1);
    
    List<PlayerKillInfo> getTodayInfo(final String p0);
}
