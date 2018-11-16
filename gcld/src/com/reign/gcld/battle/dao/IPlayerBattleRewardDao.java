package com.reign.gcld.battle.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.battle.domain.*;
import java.util.*;

public interface IPlayerBattleRewardDao extends IBaseDao<PlayerBattleReward>
{
    PlayerBattleReward read(final int p0);
    
    PlayerBattleReward readForUpdate(final int p0);
    
    List<PlayerBattleReward> getModels();
    
    int getModelSize();
    
    int create(final PlayerBattleReward p0);
    
    int deleteById(final int p0);
    
    List<PlayerBattleReward> getListByType(final int p0, final int p1);
    
    List<PlayerBattleReward> getListBy2Type(final int p0, final int p1, final int p2);
    
    List<PlayerBattleReward> getListByPlayerId(final int p0);
}
