package com.reign.gcld.duel.service;

import com.reign.gcld.player.dto.*;

public interface IDuelService
{
    byte[] getDuelInfo(final PlayerDto p0);
    
    byte[] getGeneralInfo(final PlayerDto p0, final int p1);
    
    void clear();
}
