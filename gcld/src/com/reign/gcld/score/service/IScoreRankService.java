package com.reign.gcld.score.service;

import com.reign.gcld.player.dto.*;

public interface IScoreRankService
{
    byte[] getRankInfo(final PlayerDto p0);
    
    byte[] getBoxReward(final PlayerDto p0);
    
    byte[] getRankReward(final PlayerDto p0);
}
