package com.reign.gcld.kfwd.service;

import com.reign.gcld.player.dto.*;

public interface IMatchService
{
    byte[] getMatchInfo(final PlayerDto p0);
    
    byte[] signUp(final PlayerDto p0);
    
    byte[] getRankList(final PlayerDto p0);
    
    byte[] setFormation(final String p0, final PlayerDto p1);
    
    byte[] enter(final PlayerDto p0);
    
    byte[] changeRewardMode(final int p0, final PlayerDto p1);
    
    byte[] inspire(final PlayerDto p0);
    
    byte[] exit(final PlayerDto p0);
    
    byte[] getBoxInfo(final PlayerDto p0);
    
    byte[] receiveBox(final PlayerDto p0, final int p1);
}
