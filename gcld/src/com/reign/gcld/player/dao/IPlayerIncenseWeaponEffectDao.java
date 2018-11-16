package com.reign.gcld.player.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.player.domain.*;
import java.util.*;

public interface IPlayerIncenseWeaponEffectDao extends IBaseDao<PlayerIncenseWeaponEffect>
{
    PlayerIncenseWeaponEffect read(final int p0);
    
    PlayerIncenseWeaponEffect readForUpdate(final int p0);
    
    List<PlayerIncenseWeaponEffect> getModels();
    
    int getModelSize();
    
    int create(final PlayerIncenseWeaponEffect p0);
    
    int deleteById(final int p0);
    
    int updateIncenseEffect(final int p0, final int p1, final int p2, final int p3, final Date p4);
    
    int updateWeaponEffect(final int p0, final int p1, final int p2, final int p3, final Date p4);
    
    int reduceIncenseLimit(final int p0);
    
    int reduceWeaponLimit(final int p0);
}
