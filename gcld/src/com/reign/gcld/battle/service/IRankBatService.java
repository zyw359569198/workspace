package com.reign.gcld.battle.service;

import com.reign.gcld.player.dto.*;
import com.reign.gcld.battle.scene.*;

public interface IRankBatService
{
    byte[] getRankPanel(final PlayerDto p0);
    
    int[] getSixRankInfo(final int p0, final int p1);
    
    void dealRankBat();
    
    byte[] doReward(final PlayerDto p0);
    
    byte[] getRewardInfo(final int p0);
    
    void initBatRankList();
    
    void updateRankInfo();
    
    void changeNameList(final int p0, final int p1, final int p2);
    
    int getPlayerRank(final int p0, final int p1);
    
    int getRankPlayer(final int p0, final int p1);
    
    byte[] ChallengeRewardInfo(final PlayerDto p0, final int p1);
    
    byte[] buyOneTime(final int p0);
    
    byte[] getJifenReward(final int p0);
    
    long getLeftRankTimes(final int p0);
    
    int addRankBatRewardAndJifen(final int p0, final PlayerInfo p1, final boolean p2);
    
    void resetBuyNumTimes();
    
    void addNumPerTwoHours();
}
