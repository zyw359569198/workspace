package com.reign.gcld.player.service;

import com.reign.framework.netty.servlet.*;
import com.reign.util.*;
import com.reign.gcld.player.domain.*;
import com.reign.gcld.player.common.*;
import com.reign.gcld.user.dto.*;
import com.reign.gcld.player.dto.*;

public interface IPlayerService
{
    boolean validatePlayerName(final String p0);
    
    boolean addPlayerName(final String p0);
    
    Tuple<byte[], Boolean> addNewPlayer(final String p0, final String p1, final String p2, final String p3, final int p4, final Request p5);
    
    Tuple<Player, byte[]> getPlayerInfo(final int p0, final String p1, final String p2);
    
    Tuple<Player, byte[]> getPlayerInfo(final int p0, final String p1);
    
    byte[] getPlayerList(final String p0, final String p1);
    
    byte[] getPlayerSize(final String p0, final String p1);
    
    AddExpInfo updateExpAndPlayerLevel(final int p0, final int p1, final Object p2);
    
    byte[] getForceInfo(final UserDto p0);
    
    byte[] setPlayerForce(final int p0, final int p1);
    
    byte[] setPlayerNameAndPic(final PlayerDto p0, final String p1, final int p2, final Request p3);
    
    byte[] deletePlayer(final int p0, final String p1, final String p2);
    
    byte[] retrievePlayer(final int p0, final String p1, final String p2);
    
    boolean checkName(final String p0);
    
    int getForcePlayerNum(final int p0);
    
    void firePlayerTrainningStart(final Player p0, final int p1, final long p2);
    
    void firePlayerTrainningOver(final String p0);
    
    Tuple<Boolean, String> rewardUser(final int p0, final int p1, final String p2);
    
    long isSecond(final String p0);
    
    void removeTopLv(final int p0);
    
    int getYxRenrenForceId(final Request p0, final String p1);
    
    void resetDailyOnlineTime();
    
    void afterOpenFunction(final int p0, final int p1);
    
    byte[] setDefaultPay(final UserDto p0, final int p1);
    
    byte[] payByReceipt(final PlayerDto p0, final String p1, final String p2, final Request p3);
    
    void loginReward();
    
    byte[] getLoginRewardInfo(final PlayerDto p0);
    
    byte[] getLoginReward(final PlayerDto p0);
}
