package com.reign.gcld.tech.action;

import com.reign.gcld.common.web.*;
import com.reign.gcld.tech.service.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.framework.netty.servlet.*;
import com.reign.framework.netty.mvc.result.*;
import com.reign.gcld.player.dto.*;
import com.reign.framework.netty.mvc.annotation.*;

public class TechAction extends BaseAction
{
    private static final long serialVersionUID = 1L;
    @Autowired
    private ITechService techService;
    
    @Command("tech@getTechInfo")
    public ByteResult getTechInfo(@RequestParam("page") final int page, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.techService.getTechInfo(playerDto, page), request);
    }
    
    @Command("tech@capitalInject")
    public ByteResult capitalInject(@RequestParam("techId") final int techId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.techService.capitalInject(playerDto, techId), request);
    }
    
    @Command("tech@research")
    public ByteResult research(@RequestParam("techId") final int techId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.techService.research(playerDto, techId), request);
    }
    
    @Command("tech@cdRecover")
    public ByteResult cdRecover(@RequestParam("techId") final int techId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.techService.cdRecover(playerDto.playerId, techId), request);
    }
    
    @Command("tech@cdRecoverConfirm")
    public ByteResult cdRecoverConfirm(@RequestParam("techId") final int techId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.techService.cdRecoverConfirm(playerDto.playerId, techId), request);
    }
}
