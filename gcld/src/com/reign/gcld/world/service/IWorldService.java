package com.reign.gcld.world.service;

import com.reign.gcld.player.dto.*;

public interface IWorldService
{
    byte[] getOperations(final PlayerDto p0, final int p1);
    
    void addCountryRewards();
    
    byte[] getCountryReward(final int p0, final PlayerDto p1);
    
    void createRecord(final int p0);
    
    void createWholeKill(final int p0);
    
    byte[] getRewardInfo(final PlayerDto p0, final int p1);
    
    void dealLeague();
    
    int getLeagueAddNPC(final int p0);
    
    byte[] getQuizReward(final PlayerDto p0, final int p1);
    
    void updateCitiesPerHour();
    
    void updateCountryRewardPerHour();
    
    String getCountryRewardsPerHour(final int p0);
    
    void pushCountryRewardNum();
}
