package com.reign.gcld.market.action;

import com.reign.gcld.common.web.*;
import com.reign.gcld.market.service.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.framework.netty.servlet.*;
import com.reign.framework.netty.mvc.result.*;
import com.reign.gcld.player.dto.*;
import com.reign.framework.netty.mvc.annotation.*;

public class MarketAction extends BaseAction
{
    private static final long serialVersionUID = 1L;
    @Autowired
    private IMarketService marketService;
    
    @Command("market@getMarketInfo")
    public ByteResult getMarketInfo(final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.marketService.getMarketInfo(playerDto), request);
    }
    
    @Command("market@buyMarketProduct")
    public ByteResult buyMarketProduct(@RequestParam("id") final int id, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.marketService.buyMarketProduct(id, playerDto), request);
    }
    
    @Command("market@getBlackMarketInfo")
    public ByteResult getBlackMarketInfo(final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.marketService.getBlackMarketInfo(playerDto), request);
    }
    
    @Command("market@blackMarketTrade")
    public ByteResult blackMarketTrade(@RequestParam("left") final int left, @RequestParam("right") final int right, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.marketService.blackMarketTrade(left, right, playerDto), request);
    }
    
    @Command("market@cdRecover")
    public ByteResult blackMarketCdRecover(final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.marketService.blackMarketCdRecover(playerDto.playerId), request);
    }
    
    @Command("market@cdRecoverConfirm")
    public ByteResult blackMarketCdRecoverConfirm(final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.marketService.blackMarketCdRecoverConfirm(playerDto.playerId), request);
    }
}
