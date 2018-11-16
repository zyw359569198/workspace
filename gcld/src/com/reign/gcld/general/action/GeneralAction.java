package com.reign.gcld.general.action;

import com.reign.gcld.common.web.*;
import com.reign.gcld.general.service.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.framework.netty.servlet.*;
import com.reign.framework.netty.mvc.result.*;
import com.reign.gcld.player.dto.*;
import com.reign.framework.netty.mvc.annotation.*;

public class GeneralAction extends BaseAction
{
    private static final long serialVersionUID = 4855955078366555823L;
    @Autowired
    private IGeneralService generalService;
    
    @Command("general@getGeneralInfo")
    public ByteResult getGeneralInfo(final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.generalService.getGeneralInfo(playerDto), request);
    }
    
    @Command("general@getCivilInfo")
    public ByteResult getCivilInfo(@RequestParam("generalId") final int generalId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.generalService.getCivilInfo(playerDto, generalId), request);
    }
    
    @Command("general@getCivils")
    public ByteResult getCivils(final Request request, @RequestParam("gId") final int gId) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.generalService.getCivils(playerDto, gId), request);
    }
    
    @Command("general@getGeneralSimpleInfo")
    public ByteResult getGeneralSimpleInfo(final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.generalService.getGeneralSimpleInfo(playerDto), request);
    }
    
    @Command("general@startRecruitForces")
    public ByteResult startRecruitForces(@RequestParam("generalId") final int generalId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.generalService.startRecruitForces(playerDto, generalId), request);
    }
    
    @Command("general@stopRecruitForces")
    public ByteResult stopRecruitForces(@RequestParam("generalId") final int generalId, @RequestParam("auto") final int auto, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.generalService.stopRecruitForces(playerDto, generalId, auto), request);
    }
    
    @Command("general@fireGeneral")
    public ByteResult fireGeneral(@RequestParam("generalId") final int generalId, @RequestParam("type") final int type, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.generalService.fireGeneral(playerDto.playerId, generalId, type), request);
    }
    
    @Command("general@getGeneral")
    public ByteResult getGeneral(@RequestParam("type") final int type, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.generalService.getGeneral(playerDto.playerId, type), request);
    }
    
    @Command("general@cdRecoverConfirm")
    public ByteResult cdRecoverConfirm(@RequestParam("generalId") final int generalId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.generalService.cdRecoverConfirm(playerDto, generalId, 0), request);
    }
    
    @Command("general@cdRecover")
    public ByteResult cdRecover(@RequestParam("generalId") final int generalId, @SessionParam("PLAYER") final PlayerDto dto, final Request request) {
        if (dto == null) {
            return null;
        }
        return this.getResult(this.generalService.cdRecover(dto.playerId, generalId), request);
    }
    
    @Command("general@autoRecruit")
    public ByteResult autoRecruit(@RequestParam("auto") final int auto, @RequestParam("generalId") final int generalId, @SessionParam("PLAYER") final PlayerDto dto, final Request request) {
        if (dto == null) {
            return null;
        }
        return this.getResult(this.generalService.autoRecruit(dto.playerId, generalId, auto), request);
    }
    
    @Command("general@getGeneralTreasureInfo")
    public ByteResult getGeneralTreasureInfo(@RequestParam("generalId") final int generalId, @RequestParam("location") final int location, @RequestParam("type") final int type, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.generalService.getGeneralTreasureInfo(playerDto.playerId, generalId, location, type), request);
    }
    
    @Command("general@changeGeneralTreasure")
    public ByteResult changeGeneralTreasure(@RequestParam("generalId") final int generalId, @RequestParam("vId") final int vId, @RequestParam("location") final int location, @RequestParam("type") final int type, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.generalService.changeGeneralTreasure(playerDto.playerId, generalId, vId, location, type), request);
    }
    
    @Command("equip@changAllEquip")
    public ByteResult changAllEquip(@RequestParam("orgGeneralId") final int orgGeneralId, @RequestParam("nowGeneralId") final int nowGeneralId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.generalService.changAllEquip(playerDto, orgGeneralId, nowGeneralId), request);
    }
}
