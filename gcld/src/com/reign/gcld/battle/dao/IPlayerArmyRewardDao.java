package com.reign.gcld.battle.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.battle.domain.*;
import java.util.*;

public interface IPlayerArmyRewardDao extends IBaseDao<PlayerArmyReward>
{
    PlayerArmyReward read(final int p0);
    
    PlayerArmyReward readForUpdate(final int p0);
    
    List<PlayerArmyReward> getModels();
    
    int getModelSize();
    
    int create(final PlayerArmyReward p0);
    
    int deleteById(final int p0);
    
    List<PlayerArmyReward> getPlayerArmyRewardByPowerId(final int p0, final int p1);
    
    PlayerArmyReward getPlayerArmyRewardByArmyId(final int p0, final int p1);
    
    int updateFirst(final int p0, final int p1, final int p2);
    
    int updateExpireTime(final int p0, final int p1, final Date p2);
    
    int updateNpcLost(final int p0, final int p1, final String p2);
    
    int updateNpcLostHp(final int p0, final int p1, final String p2, final int p3);
    
    List<PlayerArmyReward> getPlayerArmyRewardByPowerIdAndState(final int p0, final int p1, final int p2);
    
    int updateState(final int p0, final int p1, final int p2);
    
    int updateStateAndExpireTime(final int p0, final int p1, final int p2, final Date p3);
    
    List<PlayerArmyReward> getListByPlayerId(final int p0);
    
    int update(final int p0, final PlayerArmyReward p1);
    
    int updateFirstWin(final int p0, final int p1, final int p2);
    
    int updateWinCount(final int p0, final int p1, final int p2);
}
