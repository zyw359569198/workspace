package com.reign.gcld.affair.action;

import com.reign.gcld.common.web.*;
import com.reign.gcld.affair.service.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.framework.netty.servlet.*;
import com.reign.framework.netty.mvc.result.*;
import com.reign.gcld.player.dto.*;
import com.reign.framework.netty.mvc.annotation.*;

public class CivilAction extends BaseAction
{
    private static final long serialVersionUID = 1L;
    @Autowired
    private ICivilService civilService;
    
    @Command("civil@startAffair")
    public ByteResult startAffair(@RequestParam("generalId") final int generalId, @RequestParam("affairId") final int affairId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.civilService.startAffair(playerDto, generalId, affairId), request);
    }
    
    @Command("civil@stopAffair")
    public ByteResult stopAffair(@RequestParam("generalId") final int generalId, @RequestParam("affairId") final int affairId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.civilService.stopAffair(playerDto, generalId, affairId), request);
    }
    
    @Command("civil@finishAllAffair")
    public ByteResult finishAllAffair(@RequestParam("generalId") final int generalId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.civilService.finishAllAffair(generalId, playerDto), request);
    }
}
