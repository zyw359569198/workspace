package com.reign.gcld.battle.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.battle.domain.*;
import java.util.*;

public interface IPlayerArmyExtraDao extends IBaseDao<PlayerArmyExtra>
{
    PlayerArmyExtra read(final int p0);
    
    PlayerArmyExtra readForUpdate(final int p0);
    
    List<PlayerArmyExtra> getModels();
    
    int getModelSize();
    
    int create(final PlayerArmyExtra p0);
    
    int deleteById(final int p0);
    
    List<PlayerArmyExtra> getArmiesByPowerId(final int p0, final int p1);
    
    PlayerArmyExtra getArmyByArmyId(final int p0, final int p1);
    
    int updateAttackable(final int p0, final int p1, final int p2);
    
    int addWinAttNumReset(final int p0, final int p1, final int p2, final int p3);
    
    int resetFirstWin(final int p0, final int p1, final int p2);
    
    int resetFirstOpen(final int p0, final int p1, final int p2);
    
    int updateWinFirstWinAttNum(final int p0, final int p1, final int p2, final int p3, final int p4);
    
    int AddAttNum(final int p0, final int p1, final int p2);
    
    int deleteByPlayerIdArmyId(final int p0, final int p1);
    
    int updateNpcLostHp(final int p0, final int p1, final String p2, final int p3);
    
    List<PlayerArmyExtra> getArmiesByPowerIdAndAttackable(final int p0, final int p1, final int p2);
    
    int deleteByPlayerIdPowerId(final int p0, final int p1);
    
    List<PlayerArmyExtra> getListByPlayerId(final int p0);
}
