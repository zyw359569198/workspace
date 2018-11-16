package com.reign.gcld.huizhan.action;

import com.reign.gcld.common.web.*;
import com.reign.gcld.huizhan.service.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.framework.netty.servlet.*;
import com.reign.framework.netty.mvc.result.*;
import com.reign.gcld.player.dto.*;
import com.reign.framework.netty.mvc.annotation.*;

public class HuiZhanAction extends BaseAction
{
    private static final long serialVersionUID = 1L;
    @Autowired
    private IHuiZhanService huiZhanService;
    
    @Command("huizhan@getHuiZhanGatherInfo")
    public ByteResult getHuiZhanGatherInfo(final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.huiZhanService.getHuiZhanGatherInfo(playerDto), request);
    }
    
    @Command("huizhan@joinHuiZhan")
    public ByteResult joinHuiZhan(@RequestParam("gIds") final String gIds, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.huiZhanService.joinHuiZhan(playerDto, gIds), request);
    }
    
    @Command("huizhan@getHuiZhanInfo")
    public ByteResult getHuiZhanInfo(final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.huiZhanService.getHuiZhanInfo(playerDto), request);
    }
    
    @Command("huizhan@receiveHuizhanRewards")
    public ByteResult receiveHuizhanRewards(final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.huiZhanService.receiveHuizhanRewards(playerDto), request);
    }
}
