package com.reign.gcld.kfzb.action;

import com.reign.gcld.common.web.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.kfzb.service.*;
import com.reign.framework.netty.servlet.*;
import com.reign.framework.netty.mvc.result.*;
import com.reign.gcld.player.dto.*;
import com.reign.framework.netty.mvc.annotation.*;

public class KfzbAction extends BaseAction
{
    private static final long serialVersionUID = -6899148564968383190L;
    @Autowired
    private IKfzbSeasonService kfzbSeasonService;
    @Autowired
    private IKfzbMatchService kfzbMatchService;
    
    @Command("kfzb@getSignUpPanel")
    public ByteResult getSignUpPanel(final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.kfzbMatchService.getSignUpPanel(playerDto), request);
    }
    
    @Command("kfzb@signUp")
    public ByteResult signUp(final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.kfzbMatchService.signUp(playerDto), request);
    }
    
    @Command("kfzb@synData")
    public ByteResult synData(@RequestParam("gIds") final String gIds, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.kfzbMatchService.synData(playerDto, gIds), request);
    }
    
    @Command("kfzb@get16Table")
    public ByteResult getMatchInfo(final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.kfzbMatchService.get16Table(playerDto), request);
    }
    
    @Command("kfzb@getSupportPanel")
    public ByteResult support(@RequestParam("matchId") final Integer matchId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.kfzbMatchService.getSupportPanel(playerDto, matchId), request);
    }
    
    @Command("kfzb@support")
    public ByteResult support(@RequestParam("matchId") final Integer matchId, @RequestParam("cId") final Integer cId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.kfzbMatchService.support(playerDto, matchId, cId), request);
    }
    
    @Command("kfzb@viewBattle")
    public ByteResult viewBattle(@RequestParam("matchId") final Integer matchId, @RequestParam("round") final Integer round, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.kfzbMatchService.viewBattle(playerDto, matchId, round), request);
    }
    
    @Command("kfzb@getTickets")
    public ByteResult getTickets(final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.kfzbMatchService.getTickets(playerDto), request);
    }
    
    @Command("kfzb@getSupTickets")
    public ByteResult getSupTickets(final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.kfzbMatchService.getSupTickets(playerDto), request);
    }
    
    @Command("kfzb@buyFlower")
    public ByteResult buyFlower(final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.kfzbMatchService.buyFlower(playerDto), request);
    }
}
