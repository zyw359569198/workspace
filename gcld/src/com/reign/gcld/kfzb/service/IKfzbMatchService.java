package com.reign.gcld.kfzb.service;

import com.reign.gcld.player.dto.*;
import com.reign.gcld.player.domain.*;
import com.reign.util.*;
import com.reign.framework.json.*;

public interface IKfzbMatchService
{
    byte[] getSignUpPanel(final PlayerDto p0);
    
    byte[] signUp(final PlayerDto p0);
    
    void stopMatchService();
    
    byte[] support(final PlayerDto p0, final int p1, final Integer p2);
    
    byte[] viewBattle(final PlayerDto p0, final Integer p1, final Integer p2);
    
    byte[] synData(final PlayerDto p0, final String p1);
    
    byte[] get16Table(final PlayerDto p0);
    
    void startStateThread(final String p0, final int p1);
    
    byte[] getSupportPanel(final PlayerDto p0, final Integer p1);
    
    Tuple<Boolean, String> syncData(final Player p0, final String p1, final boolean p2);
    
    byte[] getTickets(final PlayerDto p0);
    
    byte[] buyFlower(final PlayerDto p0);
    
    void appendKfzbSeasonInfo(final Player p0, final JsonDocument p1);
    
    void clearAllCacheBeforeStartNewSeason(final int p0);
    
    void setAddressPort(final String p0, final int p1);
    
    byte[] getSupTickets(final PlayerDto p0);
}
