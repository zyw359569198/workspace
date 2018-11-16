package com.reign.gcld.world.service;

import com.reign.gcld.player.dto.*;
import com.reign.gcld.general.domain.*;
import com.reign.gcld.world.domain.*;
import com.reign.gcld.common.*;

public interface IWorldFarmService
{
    byte[] investFarm(final PlayerDto p0);
    
    byte[] start(final PlayerDto p0, final int p1, final int p2);
    
    byte[] stop(final PlayerDto p0, final int p1);
    
    byte[] getFarmInfo(final PlayerDto p0);
    
    void dealFarmWork(final String p0);
    
    void changeGeneralState(final int p0, final PlayerGeneralMilitary p1);
    
    boolean isInFarmForbiddenOperation(final PlayerGeneralMilitary p0, final boolean p1);
    
    byte[] startAll(final PlayerDto p0, final int p1);
    
    byte[] getRecoverCostGold(final PlayerDto p0);
    
    byte[] recoverGold(final PlayerDto p0);
    
    byte[] stopAll(final PlayerDto p0);
    
    int rewardPlayerGeneral(final PlayerFarm p0, final PlayerGeneralMilitary p1, final PlayerDto p2, final boolean p3);
    
    void rebootInit();
    
    MultiResult doStart(final PlayerDto p0, final boolean p1, final int p2, final PlayerGeneralMilitary p3);
    
    byte[] getReward(final PlayerDto p0, final int p1, final boolean p2);
    
    int getBuff(final int p0, final int p1);
}
