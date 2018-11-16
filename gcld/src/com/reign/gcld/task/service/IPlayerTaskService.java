package com.reign.gcld.task.service;

import com.reign.gcld.player.dto.*;
import com.reign.gcld.task.message.*;
import com.reign.gcld.player.domain.*;

public interface IPlayerTaskService
{
    byte[] getCurTaskInfo(final PlayerDto p0);
    
    byte[] getCurTaskSimpleInfo(final int p0);
    
    byte[] finishCurTask(final PlayerDto p0, final int p1, final int p2, final int p3);
    
    void handleMessage(final TaskMessage p0);
    
    void resetDailyTask(final int p0);
    
    byte[] getDailyBattleTaskInfo(final PlayerDto p0);
    
    byte[] receiveDailyBattleTaskReward(final PlayerDto p0);
    
    void cleanCurrBranchTask(final PlayerDto p0);
    
    byte[] guideUpdate(final PlayerDto p0, final int p1);
    
    byte[] getAllTaskInfo(final PlayerDto p0);
    
    void timesUpExe(final String p0);
    
    void startFreshManTask(final Player p0);
    
    void endFreshManTask(final String p0);
    
    byte[] getFreshManTaskInfo(final PlayerDto p0);
    
    byte[] getfmTaskReward(final PlayerDto p0, final int p1);
    
    void startPushFreshManTaskIcon(final Player p0);
}
