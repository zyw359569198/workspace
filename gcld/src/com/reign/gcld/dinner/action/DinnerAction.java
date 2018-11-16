package com.reign.gcld.dinner.action;

import com.reign.gcld.common.web.*;
import com.reign.gcld.dinner.service.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.framework.netty.servlet.*;
import com.reign.framework.netty.mvc.result.*;
import com.reign.gcld.player.dto.*;
import com.reign.framework.netty.mvc.annotation.*;

public class DinnerAction extends BaseAction
{
    private static final long serialVersionUID = -6658340656717201742L;
    @Autowired
    private IDinnerService dinnerService;
    
    @Command("dinner@getDinnerInfo")
    public ByteResult getDinnerInfo(final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.dinnerService.getDinnerInfo(playerDto), request);
    }
    
    @Command("dinner@choiceLiqueurId")
    public ByteResult choiceLiqueurId(@RequestParam("liqueurId") final int liqueurId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.dinnerService.choiceLiqueurId(playerDto, liqueurId), request);
    }
    
    @Command("dinner@haveDinner")
    public ByteResult haveDinner(@RequestParam("liqueurId") final int liqueurId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.dinnerService.haveDinner(playerDto, liqueurId), request);
    }
}
