package com.reign.gcld.duel.action;

import com.reign.gcld.common.web.*;
import com.reign.gcld.duel.service.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.framework.netty.servlet.*;
import com.reign.framework.netty.mvc.result.*;
import com.reign.gcld.player.dto.*;
import com.reign.framework.netty.mvc.annotation.*;

public class DuelAction extends BaseAction
{
    private static final long serialVersionUID = -2076893718935020977L;
    @Autowired
    private IDuelService duelService;
    
    @Command("duel@getDuelInfo")
    public ByteResult getDuelInfo(final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.duelService.getDuelInfo(playerDto), request);
    }
    
    @Command("duel@getGeneralInfo")
    public ByteResult getGeneralInfo(@RequestParam("playerId") final int playerId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.duelService.getGeneralInfo(playerDto, playerId), request);
    }
}
