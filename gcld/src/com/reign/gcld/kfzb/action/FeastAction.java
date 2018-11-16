package com.reign.gcld.kfzb.action;

import com.reign.gcld.common.web.*;
import com.reign.gcld.kfzb.service.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.framework.netty.servlet.*;
import com.reign.framework.netty.mvc.result.*;
import com.reign.gcld.player.dto.*;
import com.reign.framework.netty.mvc.annotation.*;

public class FeastAction extends BaseAction
{
    private static final long serialVersionUID = -5408455302553336451L;
    @Autowired
    private IKfzbFeastService kfzbFeastService;
    
    @Command("feast@getFeastInfo")
    public ByteResult getFeastInfo(final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.kfzbFeastService.getFeastInfo(playerDto), request);
    }
    
    @Command("feast@buyDrink")
    public ByteResult buyDrink(final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.kfzbFeastService.buyDrink(playerDto), request);
    }
    
    @Command("feast@buyCard")
    public ByteResult buyCard(@RequestParam("type") final int type, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.kfzbFeastService.buyCard(playerDto, type), request);
    }
    
    @Command("feast@getRoomInfo")
    public ByteResult getRoomInfo(@RequestParam("pos") final int pos, @RequestParam("cardType") final int cardType, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.kfzbFeastService.getRoomInfo(playerDto, pos, cardType), request);
    }
}
