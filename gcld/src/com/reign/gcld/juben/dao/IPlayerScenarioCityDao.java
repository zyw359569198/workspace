package com.reign.gcld.juben.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.juben.domain.*;
import java.util.*;

public interface IPlayerScenarioCityDao extends IBaseDao<PlayerScenarioCity>
{
    PlayerScenarioCity read(final int p0);
    
    PlayerScenarioCity readForUpdate(final int p0);
    
    List<PlayerScenarioCity> getModels();
    
    int getModelSize();
    
    int create(final PlayerScenarioCity p0);
    
    int deleteById(final int p0);
    
    List<PlayerScenarioCity> getSCityByPidSid(final int p0, final int p1);
    
    int updateInit(final int p0, final int p1, final int p2, final int p3, final int p4, final String p5, final long p6, final int p7, final int p8, final String p9);
    
    int updateForceId(final int p0, final int p1, final int p2, final int p3, final int p4);
    
    int updateTrickInfo(final String p0, final int p1, final int p2, final int p3);
}
