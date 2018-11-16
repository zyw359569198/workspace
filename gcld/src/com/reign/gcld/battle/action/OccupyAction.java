package com.reign.gcld.battle.action;

import com.reign.gcld.common.web.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.battle.service.*;
import com.reign.framework.netty.servlet.*;
import com.reign.framework.netty.mvc.result.*;
import com.reign.gcld.player.dto.*;
import com.reign.framework.netty.mvc.annotation.*;

public class OccupyAction extends BaseAction
{
    private static final long serialVersionUID = -705670047137021471L;
    @Autowired
    private IOccupyService occupyService;
    @Autowired
    private IBattleService battleService;
    
    @Command("occupy@getAllOfficerBuilding")
    public ByteResult getAllOfficerBuilding(final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.occupyService.getAllOfficerBuilding(playerDto), request);
    }
    
    @Command("officer@getRankInfo")
    public ByteResult getRankInfo(@RequestParam("page") final int page, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.occupyService.getRankInfo(playerDto, page), request);
    }
    
    @Command("officer@getSalary")
    public ByteResult getSalary(final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.occupyService.getSalary(playerDto), request);
    }
    
    @Command("occupy@attackBuilding")
    public ByteResult attackBuilding(@RequestParam("buildingId") final int buildingId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        final int create = 1;
        return this.getResult(this.battleService.attPermit(playerDto, 4, buildingId, create, 0), request);
    }
    
    @Command("occupy@applyBuilding")
    public ByteResult applyBuilding(@RequestParam("buildingId") final int buildingId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.occupyService.applyBuilding(playerDto, buildingId), request);
    }
    
    @Command("occupy@quitBuilding")
    public ByteResult quitBuilding(@RequestParam("buildingId") final int buildingId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.occupyService.quitBuilding(playerDto, buildingId), request);
    }
    
    @Command("occupy@getApplyList")
    public ByteResult getApplyList(@RequestParam("type") final int type, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.occupyService.getApplyList(type, playerDto), request);
    }
    
    @Command("occupy@passApply")
    public ByteResult passApply(@RequestParam("playerId") final int applyPlayerId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.occupyService.passApply(playerDto, applyPlayerId), request);
    }
    
    @Command("occupy@refuseApply")
    public ByteResult refuseApply(@RequestParam("playerId") final int applyPlayerId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.occupyService.refuseApply(playerDto, applyPlayerId), request);
    }
    
    @Command("occupy@kickMember")
    public ByteResult kickMember(@RequestParam("playerId") final int kickedPlayerId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.occupyService.kickMember(playerDto, kickedPlayerId), request);
    }
    
    @Command("occupy@getOperation")
    public ByteResult getOperation(@RequestParam("buildingId") final int buildingId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.occupyService.getOperation(playerDto, buildingId), request);
    }
    
    @Command("occupy@changeAutoPass")
    public ByteResult changeAutoPass(@RequestParam("state") final int state, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.occupyService.changeAutoPass(playerDto, state), request);
    }
}
