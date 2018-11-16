package com.reign.gcld.weapon.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.weapon.domain.*;
import java.util.*;

public interface IPlayerWeaponDao extends IBaseDao<PlayerWeapon>
{
    PlayerWeapon read(final int p0);
    
    PlayerWeapon readForUpdate(final int p0);
    
    List<PlayerWeapon> getModels();
    
    int getModelSize();
    
    int create(final PlayerWeapon p0);
    
    int deleteById(final int p0);
    
    List<PlayerWeapon> getPlayerWeapons(final int p0);
    
    PlayerWeapon getPlayerWeapon(final int p0, final int p1);
    
    void upgradeWeapon(final int p0, final int p1, final int p2, final int p3);
    
    List<PlayerWeapon> getPlayerWeaponsByType(final int p0, final int p1);
    
    void upgradeLoadGem(final int p0, final int p1, final String p2);
    
    void setWeaponLv(final int p0, final int p1, final int p2);
    
    int deleteByPlayerId(final int p0);
}
