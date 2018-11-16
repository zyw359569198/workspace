package com.reign.gcld.battle.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.battle.domain.*;
import java.util.*;

public interface IPlayerGeneralMilitaryPhantomDao extends IBaseDao<PlayerGeneralMilitaryPhantom>
{
    PlayerGeneralMilitaryPhantom read(final int p0);
    
    PlayerGeneralMilitaryPhantom readForUpdate(final int p0);
    
    List<PlayerGeneralMilitaryPhantom> getModels();
    
    int getModelSize();
    
    int create(final PlayerGeneralMilitaryPhantom p0);
    
    int deleteById(final int p0);
    
    List<PlayerGeneralMilitaryPhantom> getPhantomByLocationIdOrderByPlayerIdLvDesc(final int p0);
    
    int updateHp(final int p0, final int p1);
    
    List<PlayerGeneralMilitaryPhantom> getPhantomByLocationPidGid(final int p0, final int p1, final int p2);
    
    int deleteByLocationId(final int p0);
}
