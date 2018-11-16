package com.reign.gcld.phantom.service;

import com.reign.gcld.player.dto.*;
import com.reign.util.*;

public interface IPhantomService
{
    byte[] getPhantomPanel(final PlayerDto p0);
    
    byte[] gainPhantom(final int p0, final PlayerDto p1);
    
    byte[] upgradeWizard(final int p0, final PlayerDto p1);
    
    Integer getPhantomWorkShopLv(final int p0);
    
    byte[] buildWorkShop(final PlayerDto p0);
    
    byte[] getWiazrdDetail(final PlayerDto p0, final int p1);
    
    byte[] gainExtraNum(final PlayerDto p0, final int p1);
    
    Tuple<Boolean, String> canDoPhantomOperation(final PlayerDto p0);
    
    void resetPahntomWorkShopTodayNum();
    
    byte[] gainDoneNum(final PlayerDto p0, final int p1);
    
    void changePhantomFlag(final String p0);
    
    Integer getPahntomWorkShopIconType(final int p0);
    
    void pushIconForOnLinePlayers();
    
    void recoverPhantomJob();
}
