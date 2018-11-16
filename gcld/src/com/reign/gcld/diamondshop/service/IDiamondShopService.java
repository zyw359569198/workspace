package com.reign.gcld.diamondshop.service;

import com.reign.gcld.player.dto.*;

public interface IDiamondShopService
{
    byte[] getInfo(final PlayerDto p0);
    
    byte[] addNewShop(final PlayerDto p0);
    
    byte[] upgradeShop(final PlayerDto p0, final int p1);
    
    byte[] exchange(final PlayerDto p0, final int p1);
    
    void resetRTimes();
    
    boolean canRecvDropProps(final int p0, final int p1);
    
    void dropProps(final int p0, final int p1, final int p2);
}
