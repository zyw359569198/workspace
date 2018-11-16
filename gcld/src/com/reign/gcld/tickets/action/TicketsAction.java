package com.reign.gcld.tickets.action;

import com.reign.gcld.common.web.*;
import com.reign.gcld.tickets.service.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.framework.netty.servlet.*;
import com.reign.framework.netty.mvc.result.*;
import com.reign.gcld.player.dto.*;
import com.reign.framework.netty.mvc.annotation.*;

public class TicketsAction extends BaseAction
{
    private static final long serialVersionUID = 1L;
    @Autowired
    private ITicketsService ticketsService;
    
    @Command("tickets@getMarket")
    public ByteResult getMarket(final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.ticketsService.getMarket(playerDto), request);
    }
    
    @Command("tickets@buy")
    public ByteResult buy(@RequestParam("id") final int id, @RequestParam("num") final int num, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.ticketsService.buy(playerDto, id, num), request);
    }
}
