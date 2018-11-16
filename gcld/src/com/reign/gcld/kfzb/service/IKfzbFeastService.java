package com.reign.gcld.kfzb.service;

import com.reign.kfzb.dto.response.*;
import com.reign.gcld.player.dto.*;

public interface IKfzbFeastService
{
    void init();
    
    KfzbFeastInfo getKfzbFeastInfo();
    
    boolean inFeast();
    
    byte[] getFeastInfo(final PlayerDto p0);
    
    byte[] buyDrink(final PlayerDto p0);
    
    byte[] buyCard(final PlayerDto p0, final int p1);
    
    byte[] getRoomInfo(final PlayerDto p0, final int p1, final int p2);
    
    void addFreeCard(final int p0, final int p1);
}
