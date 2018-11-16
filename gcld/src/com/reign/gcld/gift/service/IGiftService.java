package com.reign.gcld.gift.service;

import com.reign.gcld.player.dto.*;
import com.reign.gcld.player.domain.*;

public interface IGiftService
{
    boolean haveDayGift(final PlayerAttribute p0);
    
    byte[] getDayGift(final PlayerDto p0);
    
    byte[] getOnlineGiftNumber(final PlayerDto p0);
    
    byte[] getOnlineGift(final PlayerDto p0);
    
    void pushOnlineGift();
    
    void openOnlineGiftFunctin(final int p0);
    
    boolean hasGift(final Player p0);
    
    byte[] getGiftInfo(final int p0);
    
    byte[] getGift(final PlayerDto p0, final int p1);
    
    byte[] getGiftByCode(final PlayerDto p0, final String p1);
    
    void pushDayGift();
}
