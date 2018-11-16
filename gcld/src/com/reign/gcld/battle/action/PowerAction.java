package com.reign.gcld.battle.action;

import com.reign.gcld.common.web.*;
import com.reign.gcld.battle.service.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.framework.netty.servlet.*;
import com.reign.framework.netty.mvc.result.*;
import com.reign.gcld.player.dto.*;
import com.reign.framework.netty.mvc.annotation.*;
import com.reign.gcld.common.*;
import com.reign.framework.json.*;

public class PowerAction extends BaseAction
{
    private static final long serialVersionUID = -7189925495563995470L;
    @Autowired
    private IPowerService powerService;
    
    @Command("battle@getPowerInfo")
    public ByteResult getPowerInfo(final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.powerService.getPowerInfo(playerDto), request);
    }
    
    @Command("battle@switchPowerInfo")
    public ByteResult switchPowerInfo(@RequestParam("powerId") final int powerId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.powerService.switchPowerInfo(playerDto, powerId), request);
    }
    
    @Command("battle@getExtraPowerInfo")
    public ByteResult getExtraPowerInfo(@RequestParam("powerId") final int powerId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.powerService.getExtraPowerInfo(playerDto, powerId), request);
    }
    
    @Command("battle@buyBonusNpc")
    public ByteResult buyBonusNpc(@RequestParam("armyId") final int armyId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.powerService.buyBonusNpc(playerDto, armyId), request);
    }
    
    @Command("battle@buyPowerExtra")
    public ByteResult buyPowerExtra(@RequestParam("extraPowerId") final int extraPowerId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.powerService.buyPowerExtra(playerDto, extraPowerId), request);
    }
    
    @Command("battle@getPowerGuide")
    public ByteResult getPowerGuide(final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return this.getResult(JsonBuilder.getJson(State.FAIL, LocalMessages.T_USER_10004), request);
        }
        return this.getResult(this.powerService.getPowerGuide(playerDto), request);
    }
    
    @Command("battle@getCurrentGuide")
    public ByteResult getCurrentGuide(@RequestParam("powerId") final int powerId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return this.getResult(JsonBuilder.getJson(State.FAIL, LocalMessages.T_USER_10004), request);
        }
        return this.getResult(this.powerService.getCurrentGuide(playerDto, powerId), request);
    }
}
