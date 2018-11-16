package com.reign.gcld.incense.service;

import com.reign.gcld.player.dto.*;

public interface IIncenseService
{
    byte[] getIncenseInfo(final PlayerDto p0);
    
    void addIncenseGod(final int p0, final int p1);
    
    byte[] doWorship(final int p0, final PlayerDto p1);
    
    void addIncense();
    
    int openIncense(final int p0);
    
    int getGold(final int p0, final int p1, final int p2);
    
    int getIndexGold(final int p0, final int p1, final int p2);
}
