package com.reign.gcld.tech.service;

import com.reign.gcld.player.dto.*;

public interface ITechService
{
    byte[] getTechInfo(final PlayerDto p0, final int p1);
    
    byte[] capitalInject(final PlayerDto p0, final int p1);
    
    byte[] research(final PlayerDto p0, final int p1);
    
    byte[] cdRecover(final int p0, final int p1);
    
    byte[] cdRecoverConfirm(final int p0, final int p1);
    
    void finishResearch(final String p0);
    
    void dropTech(final int p0, final int p1);
    
    int getTechEffect(final int p0, final int p1);
    
    int getTechEffect2(final int p0, final int p1);
    
    double getTechEffect3(final int p0, final int p1);
    
    void openTechFunction(final int p0);
}
