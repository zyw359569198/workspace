package com.reign.gcld.courtesy.action;

import com.reign.gcld.common.web.*;
import com.reign.gcld.courtesy.service.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.framework.netty.servlet.*;
import com.reign.framework.netty.mvc.result.*;
import com.reign.gcld.player.dto.*;
import com.reign.gcld.common.*;
import com.reign.framework.json.*;
import com.reign.framework.netty.mvc.annotation.*;

public class CourtesyAction extends BaseAction
{
    private static final long serialVersionUID = -7555921096665439599L;
    @Autowired
    private ICourtesyService courtesyService;
    
    @Command("courtesy@getPanel")
    public ByteResult getPanel(final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return this.getResult(JsonBuilder.getJson(State.FAIL, LocalMessages.T_USER_10004), request);
        }
        return this.getResult(this.courtesyService.getPanel(playerDto), request);
    }
    
    @Command("courtesy@handleEvent")
    public ByteResult handleEvent(@RequestParam("eventId") final int eventId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return this.getResult(JsonBuilder.getJson(State.FAIL, LocalMessages.T_USER_10004), request);
        }
        return this.getResult(this.courtesyService.handleEvent(playerDto, eventId), request);
    }
    
    @Command("courtesy@getLiYiDuReward")
    public ByteResult getLiYiDuReward(@RequestParam("rewardId") final int rewardId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return this.getResult(JsonBuilder.getJson(State.FAIL, LocalMessages.T_USER_10004), request);
        }
        return this.getResult(this.courtesyService.getLiYiDuReward(playerDto, rewardId), request);
    }
}
