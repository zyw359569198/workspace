package com.reign.kfgz.service;

import com.reign.kfgz.comm.*;

public interface IKfgzGroupTeamService
{
    byte[] getGroupTeamInfo(final KfPlayerInfo p0);
    
    byte[] createGroupTeam(final KfPlayerInfo p0);
    
    byte[] getAddGroupTeamInfo(final KfPlayerInfo p0, final int p1);
    
    byte[] addToGroupTeam(final KfPlayerInfo p0, final int p1, final String p2);
    
    byte[] dismissGroupTeam(final KfPlayerInfo p0, final int p1);
    
    byte[] leaveGroupTeam(final KfPlayerInfo p0, final int p1, final int p2);
    
    byte[] kickOutGroupTeam(final KfPlayerInfo p0, final int p1, final int p2, final int p3);
    
    byte[] doBattleGroupTeam(final KfPlayerInfo p0, final int p1, final int p2, final int p3);
    
    byte[] getGroupTeamBatCost(final KfPlayerInfo p0, final int p1);
    
    byte[] groupTeamInspire(final KfPlayerInfo p0, final int p1);
    
    byte[] groupTeamOrder(final KfPlayerInfo p0, final int p1);
    
    byte[] closeGroupTeam(final KfPlayerInfo p0);
}
