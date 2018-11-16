package com.reign.gcld.world.action;

import com.reign.gcld.common.web.*;
import com.reign.gcld.world.service.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.framework.netty.servlet.*;
import com.reign.framework.netty.mvc.result.*;
import com.reign.gcld.player.dto.*;
import com.reign.framework.netty.mvc.annotation.*;

public class WorldFarmAction extends BaseAction
{
    private static final long serialVersionUID = -5507023145522812826L;
    @Autowired
    public IWorldFarmService worldFarmService;
    
    @Command("farm@investFarm")
    public ByteResult investFarm(final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.worldFarmService.investFarm(playerDto), request);
    }
    
    @Command("farm@start")
    public ByteResult start(final Request request, @RequestParam("type") final int type, @RequestParam("gId") final int generalId) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.worldFarmService.start(playerDto, type, generalId), request);
    }
    
    @Command("farm@stop")
    public ByteResult stop(final Request request, @RequestParam("gId") final int generalId) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.worldFarmService.stop(playerDto, generalId), request);
    }
    
    @Command("farm@getFarmInfo")
    public ByteResult getFarmInfo(final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.worldFarmService.getFarmInfo(playerDto), request);
    }
    
    @Command("farm@startAll")
    public ByteResult startAll(final Request request, @RequestParam("type") final int type) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.worldFarmService.startAll(playerDto, type), request);
    }
    
    @Command("farm@getRecoverCostGold")
    public ByteResult getRecoverCostGold(final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.worldFarmService.getRecoverCostGold(playerDto), request);
    }
    
    @Command("farm@recoverGold")
    public ByteResult recoverGold(final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.worldFarmService.recoverGold(playerDto), request);
    }
    
    @Command("farm@stopAll")
    public ByteResult stopAll(final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.worldFarmService.stopAll(playerDto), request);
    }
    
    @Command("farm@getReward")
    public ByteResult getReward(final Request request, @RequestParam("gId") final int generalId, @RequestParam("isVid") final boolean isVid) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.worldFarmService.getReward(playerDto, generalId, isVid), request);
    }
}
