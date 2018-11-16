package com.reign.gcld.blacksmith.service;

import com.reign.gcld.player.dto.*;

public interface IBlacksmithService
{
    byte[] getBlacksmithInfo(final PlayerDto p0);
    
    byte[] getSmithInfo(final PlayerDto p0, final int p1);
    
    byte[] dissolve(final PlayerDto p0, final int p1, final int p2);
    
    byte[] updateBlackSmith(final PlayerDto p0);
    
    byte[] updateSmith(final PlayerDto p0, final int p1);
    
    boolean is_3_Length(final String p0);
    
    boolean is_5(final String p0);
    
    int isSameSpecialSkillLv4(final String p0);
}
