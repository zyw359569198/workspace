package com.reign.gcld.battle.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.battle.domain.*;
import java.util.*;

public interface IPlayerArmyDao extends IBaseDao<PlayerArmy>
{
    PlayerArmy read(final int p0);
    
    PlayerArmy readForUpdate(final int p0);
    
    List<PlayerArmy> getModels();
    
    int getModelSize();
    
    int create(final PlayerArmy p0);
    
    int deleteById(final int p0);
    
    List<PlayerArmy> getPlayerPowerArmies(final int p0, final int p1);
    
    PlayerArmy getPlayerArmy(final int p0, final int p1);
    
    int updateAttackable(final int p0, final int p1, final int p2);
    
    int updateAttackWinNum(final int p0, final int p1, final int p2, final int p3);
    
    int updateAttack(final int p0, final int p1, final int p2, final int p3);
    
    List<PlayerArmy> getPlayerArmyList(final int p0);
    
    int resetFirstWin(final int p0, final int p1, final int p2);
    
    int resetFirstOpen(final int p0, final int p1, final int p2);
    
    int updateAttackWin(final int p0, final int p1, final int p2, final int p3, final int p4);
    
    int updateAttNum(final int p0, final int p1, final int p2);
    
    int getMaxArmyIdByPlayerId(final int p0);
    
    int addDropCount(final int p0, final int p1, final int p2);
    
    int getLastWinArmy(final int p0);
    
    void updateGoldReward(final int p0, final int p1, final int p2);
    
    List<PlayerArmy> getPlayerArmyRewardList(final int p0, final int p1);
}
