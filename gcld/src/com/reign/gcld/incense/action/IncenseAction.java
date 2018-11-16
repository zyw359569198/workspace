package com.reign.gcld.incense.action;

import com.reign.gcld.common.web.*;
import com.reign.gcld.incense.service.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.framework.netty.servlet.*;
import com.reign.framework.netty.mvc.result.*;
import com.reign.gcld.player.dto.*;
import com.reign.framework.netty.mvc.annotation.*;

public class IncenseAction extends BaseAction
{
    private static final long serialVersionUID = 1L;
    @Autowired
    private IIncenseService incenseService;
    
    @Command("incense@getIncenseInfo")
    public ByteResult getIncenseInfo(final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.incenseService.getIncenseInfo(playerDto), request);
    }
    
    @ChatTransactional
    @Command("incense@doWorship")
    public ByteResult worship(@RequestParam("godId") final int godId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.incenseService.doWorship(godId, playerDto), request);
    }
}
