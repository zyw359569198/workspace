package com.reign.gcld.tavern.action;

import com.reign.gcld.common.web.*;
import com.reign.gcld.tavern.service.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.framework.netty.servlet.*;
import com.reign.framework.netty.mvc.result.*;
import com.reign.gcld.player.dto.*;
import com.reign.framework.netty.mvc.annotation.*;

public class TavernAction extends BaseAction
{
    private static final long serialVersionUID = 1L;
    @Autowired
    private ITavernService tavernService;
    
    @Command("tavern@getGeneral")
    public ByteResult getGeneral(@RequestParam("type") final int type, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.tavernService.getGeneral(playerDto.playerId, type), request);
    }
    
    @Command("tavern@refreshGeneral")
    public ByteResult refreshGeneral(@RequestParam("type") final int type, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.tavernService.refreshGeneral(playerDto.playerId, type, false, false), request);
    }
    
    @Command("tavern@lockGeneral")
    public ByteResult lockGeneral(@RequestParam("generalId") final int generalId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.tavernService.lockGeneral(playerDto.playerId, generalId), request);
    }
    
    @Command("tavern@unlockGeneral")
    public ByteResult unlockGeneral(@RequestParam("generalId") final int generalId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.tavernService.unlockGeneral(playerDto.playerId, generalId), request);
    }
    
    @ChatTransactional
    @Command("tavern@recruitGeneral")
    public ByteResult recruitGeneral(@RequestParam("generalId") final int generalId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.tavernService.recruitGeneral(playerDto, generalId), request);
    }
    
    @Command("tavern@cdRecover")
    public ByteResult cdRecover(@RequestParam("type") final int type, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.tavernService.cdRecover(playerDto.playerId, type), request);
    }
    
    @Command("tavern@cdRecoverConfirm")
    public ByteResult cdRecoverConfirm(@RequestParam("type") final int type, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.tavernService.cdRecoverConfirm(playerDto.playerId, type), request);
    }
    
    @Command("tavern@getCanDropGeneral")
    public ByteResult getCanDropGeneral(@RequestParam("type") final int type, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.tavernService.getCanDropGeneral(type, playerDto), request);
    }
}
