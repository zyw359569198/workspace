package com.reign.gcld.market.service;

import com.reign.gcld.player.dto.*;
import com.reign.gcld.market.domain.*;

public interface IMarketService
{
    byte[] getMarketInfo(final PlayerDto p0);
    
    byte[] buyMarketProduct(final int p0, final PlayerDto p1);
    
    void refreshMarket();
    
    void addCanBuyNum();
    
    void supplyCanBuyNum(final int p0);
    
    void openMarketFunction(final PlayerDto p0);
    
    byte[] getBlackMarketInfo(final PlayerDto p0);
    
    byte[] blackMarketTrade(final int p0, final int p1, final PlayerDto p2);
    
    byte[] blackMarketCdRecover(final int p0);
    
    byte[] blackMarketCdRecoverConfirm(final int p0);
    
    void refreshShowInfo(final PlayerMarket p0, final int p1);
}
