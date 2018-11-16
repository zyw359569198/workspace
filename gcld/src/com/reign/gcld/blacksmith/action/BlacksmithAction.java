package com.reign.gcld.blacksmith.action;

import com.reign.gcld.common.web.*;
import com.reign.gcld.blacksmith.service.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.framework.netty.servlet.*;
import com.reign.framework.netty.mvc.result.*;
import com.reign.gcld.player.dto.*;
import com.reign.framework.netty.mvc.annotation.*;

public class BlacksmithAction extends BaseAction
{
    private static final long serialVersionUID = 4191390312866302539L;
    @Autowired
    IBlacksmithService blacksmithService;
    
    @Command("blacksmith@getBlacksmithInfo")
    public ByteResult getBlacksmithInfo(final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.blacksmithService.getBlacksmithInfo(playerDto), request);
    }
    
    @Command("blacksmith@getSmithInfo")
    public ByteResult getSmithInfo(@RequestParam("smithId") final int smithId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.blacksmithService.getSmithInfo(playerDto, smithId), request);
    }
    
    @Command("blacksmith@dissolve")
    public ByteResult dissolve(@RequestParam("smithId") final int smithId, @RequestParam("vId") final int vId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.blacksmithService.dissolve(playerDto, smithId, vId), request);
    }
    
    @Command("blacksmith@updateBlackSmith")
    public ByteResult updateBlackSmith(final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.blacksmithService.updateBlackSmith(playerDto), request);
    }
    
    @Command("blacksmith@updateSmith")
    public ByteResult updateSmith(@RequestParam("smithId") final int smithId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.blacksmithService.updateSmith(playerDto, smithId), request);
    }
}
