package com.reign.gcld.nation.service;

import com.reign.gcld.player.dto.*;

public interface IProtectService
{
    byte[] getProtectInfo(final PlayerDto p0);
    
    byte[] getProtectReward(final PlayerDto p0);
    
    byte[] getProtectTaskInfo(final int p0);
    
    byte[] getManWangLingInfo();
    
    void openPRank(final int p0);
    
    void pushPTaskResult(final int p0, final boolean p1);
}
