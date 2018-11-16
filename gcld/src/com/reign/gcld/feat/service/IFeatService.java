package com.reign.gcld.feat.service;

import com.reign.gcld.player.dto.*;

public interface IFeatService
{
    void openFeatRecord(final int p0);
    
    void openFeatBuilding(final int p0);
    
    byte[] getRankInfo(final PlayerDto p0);
    
    byte[] getBoxReward(final PlayerDto p0);
    
    byte[] getRankReward(final PlayerDto p0);
}
