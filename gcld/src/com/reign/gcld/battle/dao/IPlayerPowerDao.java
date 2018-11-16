package com.reign.gcld.battle.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.battle.domain.*;
import java.util.*;

public interface IPlayerPowerDao extends IBaseDao<PlayerPower>
{
    PlayerPower read(final int p0);
    
    PlayerPower readForUpdate(final int p0);
    
    List<PlayerPower> getModels();
    
    int getModelSize();
    
    int create(final PlayerPower p0);
    
    int deleteById(final int p0);
    
    PlayerPower getPlayerPower(final int p0, final int p1);
    
    List<PlayerPower> getPlayerPowers(final int p0);
    
    int updateAttackable(final int p0, final int p1, final int p2);
    
    int updateRewardState(final int p0, final int p1, final int p2);
    
    int getNowPowerId(final int p0);
    
    int updateStateAndBuyCountAndExpireTime(final int p0, final int p1, final int p2, final int p3, final Date p4);
    
    List<PlayerPower> getFourPlayerPower(final int p0, final int p1, final int p2, final int p3, final int p4);
    
    int deleteByPowerId(final int p0);
    
    int updateState(final int p0, final int p1, final int p2);
}
