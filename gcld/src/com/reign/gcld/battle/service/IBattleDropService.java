package com.reign.gcld.battle.service;

import com.reign.gcld.battle.common.*;

public interface IBattleDropService
{
    void dropCopperIgnoreMax(final int p0, final BattleDrop p1, final String p2);
    
    void dropLumberIgnoreMax(final int p0, final BattleDrop p1, final String p2);
    
    void dropFoodIgnoreMax(final int p0, final BattleDrop p1, final String p2);
    
    void dropIronIgnoreMax(final int p0, final BattleDrop p1, final String p2);
    
    void dropChiefExpIgnoreMax(final int p0, final BattleDrop p1, final String p2);
    
    void saveBattleDrop(final int p0, final BattleDrop p1, final String p2);
    
    void saveBattleDrop(final int p0, final BattleDropAnd p1);
    
    void dropTouZiDoubleTicket(final int p0, final BattleDrop p1, final String p2);
    
    void dropGem(final int p0, final BattleDrop p1, final String p2);
}
