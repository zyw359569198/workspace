package com.reign.gcld.civiltrick.action;

import com.reign.gcld.common.web.*;
import com.reign.gcld.civiltrick.service.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.player.dto.*;
import com.reign.framework.netty.servlet.*;
import com.reign.framework.netty.mvc.result.*;
import com.reign.gcld.common.*;
import com.reign.framework.json.*;
import com.reign.framework.netty.mvc.annotation.*;

public class CilvilTrickAction extends BaseAction
{
    private static final long serialVersionUID = 1L;
    @Autowired
    private ICilvilTrickService civilTrickService;
    
    @Command("cilviltrick@getpitchlocation")
    public ByteResult getPitchLocation(@RequestParam("generalId") final int generalId, @RequestParam("trickId") final int trickId, @SessionParam("PLAYER") final PlayerDto playerDto, final Request request) {
        if (playerDto == null) {
            return this.getResult(JsonBuilder.getJson(State.FAIL, LocalMessages.T_USER_10004), request);
        }
        return this.getResult(this.civilTrickService.getPitchLocation(playerDto.playerId, generalId, trickId), request);
    }
    
    @Command("cilviltrick@usetrick")
    public ByteResult useTrick(@RequestParam("trickId") final int trickId, @RequestParam("cityId") final int cityId, @RequestParam("generalId") final int generalId, @SessionParam("PLAYER") final PlayerDto playerDto, final Request request, @RequestParam("cilvilId") final int cilvilId, @RequestParam("type") final int type) {
        return this.getResult(this.civilTrickService.useTrick(playerDto, generalId, trickId, cityId, cilvilId, type), request);
    }
}
