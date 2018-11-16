package com.reign.gcld.kfwd.action;

import com.reign.gcld.common.web.*;
import com.reign.gcld.kfwd.service.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.framework.netty.servlet.*;
import com.reign.framework.netty.mvc.result.*;
import com.reign.gcld.player.dto.*;
import com.reign.framework.netty.mvc.annotation.*;

public class KfWdAction extends BaseAction
{
    private static final long serialVersionUID = 1L;
    @Autowired
    private IKfwdMatchService kfwdMatchService;
    
    @Command("kfwd@getPlayerInfo")
    public ByteResult getMatchInfo(final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.kfwdMatchService.getMatchInfo(playerDto), request);
    }
    
    @Command("kfwd@getTreasure")
    public ByteResult getTreasure(final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.kfwdMatchService.getTreasure(playerDto), request);
    }
    
    @Command("kfwd@signUp")
    public ByteResult signUp(final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.kfwdMatchService.signUp(playerDto), request);
    }
    
    @Command("kfwd@synData")
    public ByteResult synData(@RequestParam("gIds") final String gIds, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.kfwdMatchService.synPlayerData(playerDto, gIds), request);
    }
    
    @Command("kfwd@doubleReward")
    public ByteResult doubleReward(@RequestParam("coef") final int coef, @RequestParam("round") final int round, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.kfwdMatchService.processDoubleReward(playerDto, round, coef), request);
    }
    
    @Command("kfwd@getPlayerTicketInfo")
    public ByteResult getPlayerTicketInfo(final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.kfwdMatchService.getPlayerTicketInfo(playerDto), request);
    }
    
    @Command("kfwd@useTicket")
    public ByteResult useTicket(@RequestParam("pk") final int pk, @RequestParam("num") final int num, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.kfwdMatchService.useTicket(playerDto, pk, num), request);
    }
}
