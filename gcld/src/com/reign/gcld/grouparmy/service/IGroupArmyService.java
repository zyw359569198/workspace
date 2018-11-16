package com.reign.gcld.grouparmy.service;

import com.reign.gcld.player.dto.*;

public interface IGroupArmyService
{
    byte[] quit(final int p0, final int p1);
    
    byte[] getTeamInfo(final PlayerDto p0, final int p1, final int p2);
    
    byte[] getCityGroupArmyInfo(final int p0, final int p1, final int p2);
    
    byte[] followGeneral(final PlayerDto p0, final int p1, final int p2, final int p3);
    
    byte[] stopFollow(final PlayerDto p0, final int p1, final int p2, final int p3);
}
