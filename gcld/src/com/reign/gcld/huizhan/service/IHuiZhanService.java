package com.reign.gcld.huizhan.service;

import com.reign.gcld.player.dto.*;
import com.reign.framework.json.*;
import com.reign.gcld.battle.scene.*;
import com.reign.gcld.huizhan.domain.*;
import java.util.*;

public interface IHuiZhanService
{
    void startHuiZhanForTimer(final String p0);
    
    void huiZhanInStatePreparation();
    
    void huiZhanInStateDoing(final String p0);
    
    void huiZhanInStateOver(final String p0);
    
    void updateHzStateById(final int p0, final int p1);
    
    byte[] getHuiZhanGatherInfo(final PlayerDto p0);
    
    byte[] joinHuiZhan(final PlayerDto p0, final String p1);
    
    boolean isHuiZhanInStatePreparation(final int p0);
    
    void dealPkReward(final Battle p0, final int p1, final int p2);
    
    byte[] getHuiZhanInfo(final PlayerDto p0);
    
    void recoverHuizhan();
    
    byte[] receiveHuizhanRewards(final PlayerDto p0);
    
    void getHzInfoInBattle(final JsonDocument p0, final int p1);
    
    void getHzInfoInCity(final JsonDocument p0);
    
    void getHuiZhanTaskInfoForLogin(final int p0, final JsonDocument p1);
    
    void dealHuiZhanPk(final CampArmy p0, final CampArmy p1, final Battle p2);
    
    boolean isHuiZhanInProcess(final int p0);
    
    void pushHuiZhanIcon(final boolean p0, final int p1);
    
    void pushHuiZhanTaskInfoForSinglePlayer(final int p0, final int p1);
    
    boolean hasHzRewards(final int p0);
    
    void pushHuiZhanTaskInfo(final int p0, final int p1);
    
    void clearPlayerHuizhan();
    
    HuizhanHistory getTodayHuizhanInProcess();
    
    HuizhanHistory getTodayHuizhanBySate(final int p0);
    
    HuizhanHistory getTodayHuiZhan();
    
    void resetTodayHuiZhan();
    
    byte[] startHuizhan();
    
    Calendar getNextHzTime();
    
    void addHzWinNumByForceId(final int p0);
    
    void addHzTotalNum();
    
    void updateHzAttForce1ByVid(final int p0, final int p1);
    
    void updateGatherFlagByVid(final int p0, final int p1);
    
    void updateHzAttForce2ByVid(final int p0, final int p1);
    
    void updateHzDefForceByVid(final int p0, final int p1);
}
