package com.reign.gcld.world.service;

import java.util.*;
import com.reign.gcld.world.domain.*;

public interface ICityDataCache
{
    List<Integer> getMinPath(final int p0, final int p1, final int[] p2);
    
    List<Integer> getMinPathFire(final int p0, final int p1, final int[] p2, final Set<Integer> p3);
    
    int getCityNum(final int p0);
    
    int getCityGuardState(final int p0);
    
    void upCityGuardState(final int p0, final int p1);
    
    void fireCityNumChangeEvent(final int p0, final int p1);
    
    void init();
    
    void initCityArray();
    
    void initForceCityNum();
    
    int getCNPNum(final int p0);
    
    void fireCityMessageChanged(final CityMessage p0);
    
    void sendCityMessage(final CityMessage p0);
    
    void fireCityStateMessage(final int p0, final int p1, final String p2, final int p3, final int p4);
    
    void fireCityTrickMessage(final int p0, final int p1, final String p2, final int p3);
    
    void fireBattleMessage(final int p0, final int p1, final int p2);
    
    void fireBattleEnd(final int p0, final int p1);
    
    void fireCityMoveMessage(final int p0, final int p1, final int p2, final String p3);
    
    int getGeneralNum(final int p0);
    
    void ressetGeneralNum(final int p0, final int p1);
    
    boolean hasCity(final int p0, final int p1);
    
    int getStrongestForce();
    
    List<Integer> getMinPath(final int p0, final int p1);
    
    Set<Integer> getBFSCitySetByBreadth(final int p0, final int p1);
    
    List<Integer> getBFSCityOrderListByBreadth(final int p0, final int p1);
}
