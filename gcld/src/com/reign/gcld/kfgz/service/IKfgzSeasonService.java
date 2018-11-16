package com.reign.gcld.kfgz.service;

import com.reign.gcld.kfgz.domain.*;
import com.reign.gcld.player.dto.*;
import java.util.*;

public interface IKfgzSeasonService
{
    KfgzSignup requestCidFromGW(final int p0);
    
    int getSeasonId();
    
    int getGzIdByNation(final int p0);
    
    int getMatchState();
    
    int getMatchStateForQianduan();
    
    String[] getMatchAddressAndPortByNation(final int p0);
    
    void chatKfgz();
    
    byte[] scheduleInfoList(final PlayerDto p0);
    
    void requestKfgzAllRankRes(final int p0);
    
    byte[] getKfgzAllRankRes(final PlayerDto p0);
    
    byte[] getEndRewardBoard(final PlayerDto p0);
    
    byte[] getEndReward(final PlayerDto p0, final int p1);
    
    boolean isFinalRound(final int p0);
    
    void issueFinalReward(final int p0);
    
    boolean isAfterLastBattle();
    
    boolean isInBattleDay(final Date p0);
    
    void init();
}
