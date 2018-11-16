package com.reign.gcld.event.service;

import com.reign.gcld.player.dto.*;
import java.util.*;
import com.reign.gcld.common.*;

public interface IEventService
{
    void init();
    
    byte[] getReward(final PlayerDto p0, final int p1, final int p2, final int p3, final int p4);
    
    byte[] getEventInfo(final PlayerDto p0, final int p1);
    
    void saveEventSetting(final int p0, final Date p1, final Date p2, final String p3);
    
    void removeEventSetting(final int p0);
    
    void removeEvent(final int p0);
    
    byte[] lashSlave(final int p0, final int p1);
    
    byte[] getBigGift(final int p0);
    
    byte[] getNationalDayBigGift(final PlayerDto p0);
    
    void moonCakeTimeTask(final String p0);
    
    void nationalDayTimeTask(final String p0);
    
    void addBmw(final int p0, final int p1, final String p2);
    
    void addXo(final int p0, final int p1, final String p2);
    
    void addPicasso(final int p0, final int p1, final String p2);
    
    void initPlayerIncenseWeaponEffect(final int p0);
    
    ThreeTuple<Integer, Integer, Long> getEffect(final String p0);
    
    void xiLianTimeTask(final String p0);
    
    void baiNianTimeTask(final String p0);
    
    void lanternTimeTask(final String p0);
    
    String getUpdateRefreshAttribute(final String p0);
    
    String getAllRefreshAttribute(final int p0);
    
    byte[] getXiLianReward(final int p0);
    
    int getGold(final int p0);
    
    byte[] decorateTree(final PlayerDto p0, final int p1);
    
    byte[] yaoYiYao(final int p0);
    
    byte[] getChristmasBigGift(final PlayerDto p0);
    
    byte[] getWishBigGift(final PlayerDto p0, final int p1);
    
    byte[] getBaiNianBigGift(final PlayerDto p0);
    
    byte[] getLanternBigGift(final PlayerDto p0);
    
    byte[] buyBeast(final PlayerDto p0);
    
    byte[] recoverBeastCd(final PlayerDto p0);
    
    byte[] buyLantern(final PlayerDto p0);
}
