package com.reign.gcld.juben.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.juben.domain.*;
import java.util.*;

public interface IPlayerScenarioDao extends IBaseDao<PlayerScenario>
{
    PlayerScenario read(final int p0);
    
    PlayerScenario readForUpdate(final int p0);
    
    List<PlayerScenario> getModels();
    
    int getModelSize();
    
    int create(final PlayerScenario p0);
    
    int deleteById(final int p0);
    
    List<PlayerScenario> getScenarioByPid(final int p0);
    
    PlayerScenario getScenarioByPidSid(final int p0, final int p1);
    
    int updateInit(final int p0, final int p1, final int p2, final Date p3, final Date p4, final int p5, final int p6);
    
    int updateState(final int p0, final int p1, final int p2);
    
    int updateStateOverTime(final int p0, final int p1, final int p2, final long p3);
    
    int updateStar(final int p0, final int p1, final int p2, final Date p3);
    
    List<PlayerScenario> getListByState();
    
    void updateScenarioInfo(final int p0, final int p1, final String p2);
    
    void updateRewardStarLv(final int p0, final int p1, final String p2, final int p3);
    
    int updateEndTime(final int p0, final int p1, final Date p2);
    
    int updateJieBingCount(final int p0, final int p1, final int p2);
    
    int updateEndTimeCurStar(final int p0, final int p1, final int p2, final Date p3);
    
    String getDramaTimes(final Object p0, final Object p1);
    
    int updateDramaTimes(final Object p0, final Object p1, final String p2);
}
