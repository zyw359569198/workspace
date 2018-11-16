package com.reign.gcld.team.service;

import com.reign.gcld.player.dto.*;

public interface ITeamService
{
    byte[] createTeam(final PlayerDto p0, final int p1, final String p2);
    
    byte[] getTeamInfo(final PlayerDto p0);
    
    byte[] closeTeamInfo(final PlayerDto p0);
    
    byte[] getGeneralInfo(final PlayerDto p0, final String p1, final int p2);
    
    byte[] joinTeam(final PlayerDto p0, final String p1, final String p2);
    
    byte[] kickOutTeam(final PlayerDto p0, final String p1, final int p2, final int p3);
    
    byte[] dismissTeam(final PlayerDto p0, final String p1);
    
    byte[] leaveTeam(final PlayerDto p0, final String p1, final int p2);
    
    byte[] teamBattle(final PlayerDto p0, final String p1, final int p2, final int p3);
    
    byte[] getBatCost(final PlayerDto p0, final int p1);
    
    void initTeam();
    
    byte[] teamInspire(final PlayerDto p0, final String p1);
    
    void cancelTeam(final String p0);
    
    byte[] teamOrder(final PlayerDto p0, final String p1);
}
