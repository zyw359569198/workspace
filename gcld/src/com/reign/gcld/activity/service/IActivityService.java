package com.reign.gcld.activity.service;

import com.reign.gcld.player.dto.*;
import com.reign.gcld.player.domain.*;
import com.reign.framework.json.*;

public interface IActivityService
{
    byte[] get51activity(final PlayerDto p0);
    
    byte[] reward51Activity(final PlayerDto p0);
    
    void sendBatExpActivity();
    
    void clearBatExpActivity();
    
    boolean isTodayInBatExpActivity();
    
    float getAddBatValue(final int p0, final int p1);
    
    byte[] initActivity(final int p0, final long p1, final long p2, final String p3, final boolean p4);
    
    byte[] initLvExpActivity(final int p0, final long p1, final long p2, final String p3, final boolean p4);
    
    byte[] initDragonActivity(final int p0, final long p1, final long p2);
    
    byte[] initIronActivity(final int p0, final long p1, final long p2, final String p3);
    
    byte[] initDstqActivity(final int p0, final long p1, final long p2, final String p3);
    
    void initLvExpActivityStart(final String p0);
    
    void initDragonActivityStart(final String p0);
    
    void initIronActivityBegin(final String p0);
    
    void initDstqActivityBegin(final String p0);
    
    void initDstqActivityEnd(final String p0);
    
    void initIronActivityEnd(final String p0);
    
    void initLvExpActivityEnd(final String p0);
    
    void initDragonActivityEnd(final String p0);
    
    byte[] getLvExpActivity(final PlayerDto p0);
    
    byte[] rewardLvExpActivity(final PlayerDto p0);
    
    byte[] initLvExp(final PlayerDto p0);
    
    byte[] getDragonInfo(final PlayerDto p0);
    
    byte[] useDragon(final PlayerDto p0);
    
    void reachJoinLvExpActivity(final int p0);
    
    void sendBatExpActivity(final String p0);
    
    void clearBatExpActivity(final String p0);
    
    boolean inDragonBoatFestival();
    
    boolean inIronActivity();
    
    boolean inTicketActivity();
    
    boolean inDstqActivity();
    
    void openPlayerScoreRank(final int p0);
    
    void openDragonRecord(final int p0);
    
    byte[] initQuenchingActivity(final int p0, final long p1, final long p2);
    
    void initQuenchingActivityStart(final String p0);
    
    void initQuenchingActivityEnd(final String p0);
    
    byte[] getQuenching(final PlayerDto p0);
    
    void clearIronActivity();
    
    void updateIron(final String p0);
    
    void addDstqGold(final int p0, final int p1);
    
    byte[] getIronInfo(final PlayerDto p0);
    
    byte[] useIron(final PlayerDto p0);
    
    void innerActivity5(final String p0);
    
    void initInnerActivity();
    
    byte[] getDstqInfo(final PlayerDto p0);
    
    byte[] recv360Privilege(final PlayerDto p0, final int p1);
    
    byte[] get360PrivilegeInfo(final PlayerDto p0);
    
    void handle360PrivilegeForLogin(final Player p0, final JsonDocument p1);
    
    int recv360PrivilegeForTest(final String p0, final int p1);
}
