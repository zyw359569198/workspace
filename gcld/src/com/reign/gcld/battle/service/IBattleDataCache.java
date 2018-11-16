package com.reign.gcld.battle.service;

import com.reign.gcld.general.domain.*;
import com.reign.gcld.sdata.domain.*;
import java.util.*;
import com.reign.gcld.store.domain.*;
import com.reign.gcld.battle.common.*;

public interface IBattleDataCache
{
    void removeEquipEffect(final int p0, final int p1);
    
    void removeTroopEffect(final int p0, final int p1);
    
    void addTreasureEffect(final int p0, final String p1);
    
    void refreshWeaponEffect(final int p0);
    
    void refreshWeaponEffect(final int p0, final int p1);
    
    void refreshDiamondEffect(final int p0);
    
    int getAtt(final PlayerGeneralMilitary p0);
    
    int getAtt(final int p0, final int p1, final Troop p2, final int p3);
    
    Map<Integer, Integer> getAttDefHp(final int p0, final int p1, final Troop p2, final int p3);
    
    Map<Integer, Integer> getAttDefHp(final PlayerGeneralMilitary p0);
    
    int getDef(final PlayerGeneralMilitary p0);
    
    int getDef(final int p0, final int p1, final Troop p2, final int p3);
    
    int getMaxHp(final PlayerGeneralMilitary p0);
    
    int getMaxHp(final int p0, final int p1, final Troop p2, final int p3);
    
    int getColumNum(final int p0);
    
    int changGEquipNewMaxHp(final PlayerGeneralMilitary p0, final int p1, final int p2);
    
    int changCEquipNewMaxHp(final PlayerGeneralMilitary p0, final int p1, final int p2);
    
    EquipEffectCache getEquipMax(final StoreHouse p0);
    
    Map<Integer, Double> getGemAttribute(final int p0);
}
