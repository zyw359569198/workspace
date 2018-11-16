package com.reign.gcld.store.action;

import com.reign.gcld.common.web.*;
import com.reign.gcld.store.service.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.framework.netty.servlet.*;
import com.reign.framework.netty.mvc.result.*;
import com.reign.gcld.player.dto.*;
import com.reign.framework.netty.mvc.annotation.*;

public class StoreAction extends BaseAction
{
    private static final long serialVersionUID = 1L;
    @Autowired
    private IStoreService storeService;
    
    @Command("store@getItem")
    public ByteResult getItems(@RequestParam("type") final int style, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.storeService.getItems(playerDto.playerId, style), request);
    }
    
    @ChatTransactional
    @Command("store@refreshItem")
    public ByteResult refreshItem(@RequestParam("type") final int style, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.storeService.refreshItem(playerDto.playerId, style, true), request);
    }
    
    @ChatTransactional
    @Command("store@getEquipSuitTipInfo")
    public ByteResult getEquipSuitTipInfo(@RequestParam("type") final int style, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.storeService.getEquipSuitTipInfo(playerDto.playerId, style), request);
    }
    
    @ChatTransactional
    @Command("store@buyItem")
    public ByteResult buyItem(@RequestParam("itemId") final int itemId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.storeService.buyItem(playerDto, itemId), request);
    }
    
    @Command("store@lockItem")
    public ByteResult lockItem(@RequestParam("itemId") final int itemId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.storeService.lockItem(playerDto.playerId, itemId), request);
    }
    
    @Command("store@unlockItem")
    public ByteResult unlockItem(@RequestParam("itemId") final int itemId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.storeService.unlockItem(playerDto.playerId, itemId), request);
    }
    
    @Command("store@cdRecover")
    public ByteResult cdRecover(@RequestParam("type") final int style, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.storeService.cdRecover(playerDto.playerId, style), request);
    }
    
    @Command("store@cdRecoverConfirm")
    public ByteResult cdRecoverConfirm(@RequestParam("type") final int style, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.storeService.cdRecoverConfirm(playerDto.playerId, style), request);
    }
}
