package com.reign.gcld.battle.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.battle.domain.*;
import java.util.*;

public interface IPlayerBattleAutoDao extends IBaseDao<PlayerBattleAuto>
{
    PlayerBattleAuto read(final int p0);
    
    PlayerBattleAuto readForUpdate(final int p0);
    
    List<PlayerBattleAuto> getModels();
    
    int getModelSize();
    
    int create(final PlayerBattleAuto p0);
    
    int deleteById(final int p0);
    
    int updateTimes(final int p0, final int p1);
    
    int updateTimesReport(final int p0, final int p1, final String p2);
}
