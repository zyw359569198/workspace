package com.reign.gcld.gift.action;

import com.reign.gcld.common.web.*;
import com.reign.gcld.gift.service.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.framework.netty.servlet.*;
import com.reign.framework.netty.mvc.result.*;
import com.reign.gcld.player.dto.*;
import com.reign.framework.netty.mvc.annotation.*;

public class GiftAction extends BaseAction
{
    private static final long serialVersionUID = -5339339643214341045L;
    @Autowired
    private IGiftService giftService;
    
    @Command("gift@getDayGift")
    @ChatTransactional
    public ByteResult getDayGift(final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.giftService.getDayGift(playerDto), request);
    }
    
    @Command("gift@getOnlineGiftNumber")
    public ByteResult getOnlineGiftNumber(final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.giftService.getOnlineGiftNumber(playerDto), request);
    }
    
    @Command("gift@getOnlineGift")
    @ChatTransactional
    public ByteResult getOnlineGift(final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.giftService.getOnlineGift(playerDto), request);
    }
    
    @Command("gift@getGiftInfo")
    public ByteResult getGiftInfo(final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.giftService.getGiftInfo(playerDto.playerId), request);
    }
    
    @Command("gift@getGift")
    public ByteResult getGift(@RequestParam("id") final int id, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.giftService.getGift(playerDto, id), request);
    }
    
    @Command("gift@getGiftByCode")
    public ByteResult getGiftByCode(@RequestParam("code") final String code, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.giftService.getGiftByCode(playerDto, code), request);
    }
}
