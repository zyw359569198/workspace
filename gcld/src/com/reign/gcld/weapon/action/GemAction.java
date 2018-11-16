package com.reign.gcld.weapon.action;

import com.reign.gcld.common.web.*;
import com.reign.gcld.weapon.service.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.framework.netty.servlet.*;
import com.reign.framework.netty.mvc.result.*;
import com.reign.gcld.player.dto.*;
import com.reign.framework.netty.mvc.annotation.*;

public class GemAction extends BaseAction
{
    private static final long serialVersionUID = 7035415468782399263L;
    @Autowired
    private IGemService gemService;
    
    @Command("gem@gemPolish")
    public ByteResult polish(@RequestParam("id") final int id, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.gemService.polish(playerDto, id), request);
    }
    
    @Command("gem@gemUpgrade")
    public ByteResult gemUpgrade(@RequestParam("id") final int id, @RequestParam("ids") final String ids, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.gemService.gemUpgrade(playerDto, id, ids), request);
    }
    
    @Command("gem@gemRefine")
    public ByteResult gemRefine(@RequestParam("id") final int id, @RequestParam("sn") final int sn, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.gemService.gemRefine(playerDto, id, sn), request);
    }
}
