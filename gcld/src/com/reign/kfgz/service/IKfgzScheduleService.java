package com.reign.kfgz.service;

import com.reign.kfgz.comm.*;

public interface IKfgzScheduleService
{
    byte[] getBattleIniInfo(final KfPlayerInfo p0, final int p1);
    
    byte[] chooseStrategyOrTactic(final KfPlayerInfo p0, final int p1, final int p2, final int p3);
    
    byte[] doSolo(final KfPlayerInfo p0, final int p1);
    
    byte[] doRush(final KfPlayerInfo p0, final int p1, final String p2);
    
    byte[] getCanRushInfo(final KfPlayerInfo p0, final int p1);
    
    byte[] fastAddTroopHp(final KfPlayerInfo p0, final int p1);
    
    byte[] getRetreatInfo(final KfPlayerInfo p0, final int p1);
    
    byte[] doRetreat(final KfPlayerInfo p0, final String p1, final int p2);
    
    byte[] buyPhantom(final KfPlayerInfo p0, final int p1);
    
    byte[] callGeneral(final KfPlayerInfo p0, final int p1, final String p2);
    
    byte[] getCallGeneralInfo(final KfPlayerInfo p0, final int p1);
    
    byte[] getGzPlayerResult(final KfPlayerInfo p0);
    
    byte[] setAutoAttack(final KfPlayerInfo p0);
    
    byte[] leaveBattleTeam(final KfPlayerInfo p0, final int p1);
    
    byte[] clearSoloCd(final KfPlayerInfo p0);
    
    byte[] useOfficeToken(final KfPlayerInfo p0, final int p1);
    
    byte[] getOfficeTokenTeamInfo(final KfPlayerInfo p0, final int p1);
    
    byte[] doRushInOfficeTokenTeam(final KfPlayerInfo p0, final int p1, final String p2);
    
    byte[] chooseNpcAI(final KfPlayerInfo p0, final int p1);
    
    byte[] getBattleCampList(final KfPlayerInfo p0, final int p1, final int p2, final int p3);
}
