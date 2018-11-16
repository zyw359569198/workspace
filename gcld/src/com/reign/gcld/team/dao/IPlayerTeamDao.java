package com.reign.gcld.team.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.team.domain.*;
import java.util.*;

public interface IPlayerTeamDao extends IBaseDao<PlayerTeam>
{
    PlayerTeam read(final int p0);
    
    PlayerTeam readForUpdate(final int p0);
    
    List<PlayerTeam> getModels();
    
    int getModelSize();
    
    int create(final PlayerTeam p0);
    
    int deleteById(final int p0);
    
    int updatePlayerGenrealIds(final int p0, final String p1);
}
