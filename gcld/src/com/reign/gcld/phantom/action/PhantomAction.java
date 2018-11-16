package com.reign.gcld.phantom.action;

import com.reign.gcld.common.web.*;
import com.reign.gcld.phantom.service.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.framework.netty.servlet.*;
import com.reign.framework.netty.mvc.result.*;
import com.reign.gcld.player.dto.*;
import com.reign.gcld.common.*;
import com.reign.framework.json.*;
import com.reign.framework.netty.mvc.annotation.*;

public class PhantomAction extends BaseAction
{
    private static final long serialVersionUID = -2580484070695116398L;
    @Autowired
    private IPhantomService phantomService;
    
    @Command("phantom@buildWorkShop")
    public ByteResult buildWorkShop(final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return this.getResult(JsonBuilder.getJson(State.FAIL, LocalMessages.T_USER_10004), request);
        }
        return this.getResult(this.phantomService.buildWorkShop(playerDto), request);
    }
    
    @Command("phantom@getPhantomPanel")
    public ByteResult getPhantomPanel(final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return this.getResult(JsonBuilder.getJson(State.FAIL, LocalMessages.T_USER_10004), request);
        }
        return this.getResult(this.phantomService.getPhantomPanel(playerDto), request);
    }
    
    @Command("phantom@getWiazrdDetail")
    public ByteResult getWiazrdDetail(@RequestParam("wizardId") final int wizardId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return this.getResult(JsonBuilder.getJson(State.FAIL, LocalMessages.T_USER_10004), request);
        }
        return this.getResult(this.phantomService.getWiazrdDetail(playerDto, wizardId), request);
    }
    
    @Command("phantom@gainPhantom")
    public ByteResult gainPhantom(@RequestParam("wizardId") final int wizardId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return this.getResult(JsonBuilder.getJson(State.FAIL, LocalMessages.T_USER_10004), request);
        }
        return this.getResult(this.phantomService.gainPhantom(wizardId, playerDto), request);
    }
    
    @Command("phantom@gainExtraNum")
    public ByteResult gainExtraNum(@RequestParam("wizardId") final int wizardId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return this.getResult(JsonBuilder.getJson(State.FAIL, LocalMessages.T_USER_10004), request);
        }
        return this.getResult(this.phantomService.gainExtraNum(playerDto, wizardId), request);
    }
    
    @Command("phantom@gainDoneNum")
    public ByteResult gainDoneNum(@RequestParam("wizardId") final int wizardId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return this.getResult(JsonBuilder.getJson(State.FAIL, LocalMessages.T_USER_10004), request);
        }
        return this.getResult(this.phantomService.gainDoneNum(playerDto, wizardId), request);
    }
    
    @Command("phantom@upgradeWizard")
    public ByteResult upgradeWizard(@RequestParam("wizardId") final int wizardId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return this.getResult(JsonBuilder.getJson(State.FAIL, LocalMessages.T_USER_10004), request);
        }
        return this.getResult(this.phantomService.upgradeWizard(wizardId, playerDto), request);
    }
}
