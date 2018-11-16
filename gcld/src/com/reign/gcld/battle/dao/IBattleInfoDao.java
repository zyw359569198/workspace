package com.reign.gcld.battle.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.battle.domain.*;
import java.util.*;

public interface IBattleInfoDao extends IBaseDao<BattleInfo>
{
    BattleInfo read(final int p0);
    
    BattleInfo readForUpdate(final int p0);
    
    List<BattleInfo> getModels();
    
    int getModelSize();
    
    int create(final BattleInfo p0);
    
    int deleteById(final int p0);
    
    int deleteByBattleId(final String p0);
    
    int createBattle(final BattleInfo p0);
}
