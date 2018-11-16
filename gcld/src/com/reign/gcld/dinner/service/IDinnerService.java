package com.reign.gcld.dinner.service;

import com.reign.gcld.player.dto.*;

public interface IDinnerService
{
    void addDinnerNum();
    
    byte[] getDinnerInfo(final PlayerDto p0);
    
    byte[] choiceLiqueurId(final PlayerDto p0, final int p1);
    
    byte[] haveDinner(final PlayerDto p0, final int p1);
    
    void openDinnerFunction(final int p0);
    
    void addDinnerNumByTech();
}
