package com.reign.gcld.diamondshop.action;

import com.reign.gcld.common.web.*;
import com.reign.gcld.diamondshop.service.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.framework.netty.servlet.*;
import com.reign.framework.netty.mvc.result.*;
import com.reign.gcld.player.dto.*;
import com.reign.framework.netty.mvc.annotation.*;

public class DiamondShopAction extends BaseAction
{
    private static final long serialVersionUID = 8168733376998047967L;
    @Autowired
    IDiamondShopService diamondShopService;
    
    @Command("diamondshop@getInfo")
    public ByteResult getInfo(final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.diamondShopService.getInfo(playerDto), request);
    }
    
    @Command("diamondshop@addNewShop")
    public ByteResult addNewShop(final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.diamondShopService.addNewShop(playerDto), request);
    }
    
    @Command("diamondshop@upgradeShop")
    public ByteResult upgradeShop(@RequestParam("shopId") final int shopId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.diamondShopService.upgradeShop(playerDto, shopId), request);
    }
    
    @Command("diamondshop@exchange")
    public ByteResult exchange(@RequestParam("shopId") final int shopId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.diamondShopService.exchange(playerDto, shopId), request);
    }
}
