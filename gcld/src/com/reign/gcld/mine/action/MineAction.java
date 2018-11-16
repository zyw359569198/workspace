package com.reign.gcld.mine.action;

import com.reign.gcld.common.web.*;
import com.reign.gcld.mine.service.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.framework.netty.servlet.*;
import com.reign.framework.netty.mvc.result.*;
import com.reign.gcld.player.dto.*;
import com.reign.framework.netty.mvc.annotation.*;

public class MineAction extends BaseAction
{
    private static final long serialVersionUID = 1L;
    @Autowired
    private IMineService mineService;
    
    @Command("mine@getMineInfo")
    public ByteResult getMineInfo(@RequestParam("page") final int page, @RequestParam("style") final int style, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.mineService.getMineInfo(page, style, playerDto), request);
    }
    
    @Command("mine@rush")
    public ByteResult rush(@RequestParam("style") final int style, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.mineService.rush(style, playerDto), request);
    }
    
    @Command("mine@abandon")
    public ByteResult abandon(@RequestParam("style") final int style, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.mineService.abandon(style, playerDto), request);
    }
    
    @Command("mine@mine")
    public ByteResult mine(@RequestParam("style") final int style, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.mineService.mine(style, playerDto), request);
    }
}
