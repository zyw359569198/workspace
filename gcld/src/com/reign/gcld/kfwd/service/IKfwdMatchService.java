package com.reign.gcld.kfwd.service;

import com.reign.gcld.player.dto.*;
import com.reign.gcld.player.domain.*;
import com.reign.kf.comm.entity.kfwd.response.*;
import com.reign.gcld.kfwd.domain.*;
import java.util.*;
import com.reign.gcld.kfzb.domain.*;

public interface IKfwdMatchService
{
    void iniNewSeason(final KfwdSeasonInfo p0, final KfwdScheduleInfoDto p1, final KfwdRewardResult p2, final KfwdTicketMarketListInfo p3, final KfwdRankTreasureList p4);
    
    byte[] getMatchInfo(final PlayerDto p0);
    
    byte[] signUp(final PlayerDto p0);
    
    KfwdSignup doSignUp(final Player p0, final String p1, final boolean p2);
    
    void doProcessReward(final Integer p0, final KfwdTicketResultInfo p1);
    
    byte[] synPlayerData(final PlayerDto p0, final String p1);
    
    KfwdDoubleRewardResult doDoubleReward(final Player p0, final int p1, final int p2, final int p3);
    
    byte[] processDoubleReward(final PlayerDto p0, final int p1, final int p2);
    
    byte[] getPlayerTicketInfo(final PlayerDto p0);
    
    byte[] useTicket(final PlayerDto p0, final int p1, final int p2);
    
    boolean isInKfwd();
    
    int getWdState(final int p0);
    
    void ini();
    
    void addLastDayRankingReward(final KfwdReward p0);
    
    List<KfzbReward> loadKfzbTitle();
    
    byte[] getTreasure(final PlayerDto p0);
}
