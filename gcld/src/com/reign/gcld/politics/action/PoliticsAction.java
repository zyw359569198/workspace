package com.reign.gcld.politics.action;

import com.reign.gcld.common.web.*;
import com.reign.gcld.politics.service.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.framework.netty.servlet.*;
import com.reign.framework.netty.mvc.result.*;
import com.reign.gcld.player.dto.*;
import com.reign.gcld.common.*;
import com.reign.framework.json.*;
import com.reign.framework.netty.mvc.annotation.*;

public class PoliticsAction extends BaseAction
{
    private static final long serialVersionUID = 1L;
    @Autowired
    private IPoliticsService politicsService;
    
    @Command("politics@getEventInfo")
    public ByteResult getEventInfo(@RequestParam("buildingId") final int buildingId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return this.getResult(JsonBuilder.getJson(State.FAIL, LocalMessages.T_USER_10004), request);
        }
        return this.getResult(this.politicsService.getEventInfo(playerDto, buildingId), request);
    }
    
    @Command("politics@chooseEventOption")
    public ByteResult chooseEventOption(@RequestParam("buildingId") final int buildingId, @RequestParam("option") final int option, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return this.getResult(JsonBuilder.getJson(State.FAIL, LocalMessages.T_USER_10004), request);
        }
        return this.getResult(this.politicsService.chooseEventOption(playerDto, buildingId, option), request);
    }
    
    @Command("politics@getReward")
    public ByteResult getReward(final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return this.getResult(JsonBuilder.getJson(State.FAIL, LocalMessages.T_USER_10004), request);
        }
        return this.getResult(this.politicsService.getReward(playerDto), request);
    }
}
