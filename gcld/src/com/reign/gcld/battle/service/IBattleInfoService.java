package com.reign.gcld.battle.service;

import com.reign.gcld.battle.scene.*;
import com.reign.gcld.world.domain.*;
import java.util.*;

public interface IBattleInfoService
{
    void saveBattle(final Battle p0);
    
    void deleteBattle(final Battle p0);
    
    Set<Integer> resetCityBattles(final Map<Integer, City> p0);
    
    void recoverCountryLevelUpBattles();
}
