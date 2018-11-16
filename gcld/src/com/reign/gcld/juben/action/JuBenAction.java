package com.reign.gcld.juben.action;

import com.reign.gcld.common.web.*;
import com.reign.gcld.juben.service.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.framework.netty.servlet.*;
import com.reign.framework.netty.mvc.result.*;
import com.reign.gcld.player.dto.*;
import com.reign.framework.netty.mvc.annotation.*;

public class JuBenAction extends BaseAction
{
    private static final long serialVersionUID = -6329098621479271082L;
    @Autowired
    private IJuBenService juBenService;
    
    @Command("juben@getJuBenList")
    public ByteResult getJuBenList(final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.juBenService.getJuBenList(playerDto, request), request);
    }
    
    @Command("juben@juBenPermit")
    public ByteResult juBenPermit(final Request request, @RequestParam("sId") final int sId, @RequestParam("grade") final int grade, @RequestParam("create") final int create) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.juBenService.juBenPermit(playerDto, sId, grade, create, request), request);
    }
    
    @Command("juben@getJuBenScene")
    public ByteResult getJuBenScene(final Request request, @RequestParam("sId") final int sId, @RequestParam("grade") final int grade) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.juBenService.getJuBenScene(playerDto, sId, grade, request), request);
    }
    
    @Command("juben@enterJuBenScene")
    public ByteResult enterJuBenScene(final Request request, @RequestParam("sId") final int sId, @RequestParam("grade") final int grade, @RequestParam("create") final int create) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.juBenService.enterJuBenScene(playerDto, sId, grade, create, request), request);
    }
    
    @Command("juben@getJuBenReward")
    public ByteResult getJuBenReward(final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.juBenService.getJuBenReward(playerDto), request);
    }
    
    @Command("juben@enterJuBenQuick")
    public ByteResult enterJuBenQuick(final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.juBenService.enterJuBenQuick(playerDto, request), request);
    }
    
    @Command("juben@autoMoveJuBen")
    public ByteResult autoMoveJuBen(@RequestParam("cityId") final int cityId, @RequestParam("generalId") final int generalId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.juBenService.autoMoveJuBen(playerDto.playerId, cityId, generalId), request);
    }
    
    @Command("juben@autoMoveJuBenStop")
    public ByteResult autoMoveJuBenStop(@RequestParam("generalId") final int generalId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.juBenService.autoMoveJuBenStop(playerDto, generalId), request);
    }
    
    @Command("juben@pleaseGiveMeAReply")
    public ByteResult pleaseGiveMeAReply(@RequestParam("generalId") final int generalId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.juBenService.pleaseGiveMeAReply(playerDto, generalId), request);
    }
    
    @Command("juben@quitJuBen")
    public ByteResult quitJuBen(final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.juBenService.quitJuBen(playerDto), request);
    }
    
    @Command("juben@getChoice")
    public ByteResult getChoiceInfo(final Request request, @RequestParam("eventId") final int eventId) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.juBenService.getChoiceInfo(playerDto, eventId), request);
    }
    
    @Command("juben@makeAChoice")
    public ByteResult makeAChoice(final Request request, @RequestParam("eventId") final int eventId, @RequestParam("choice") final int choice, @SessionParam("PLAYER") final PlayerDto playerDto) {
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.juBenService.makeAChoice(playerDto.playerId, eventId, choice), request);
    }
    
    @Command("juben@getJuBenCityInfo")
    public ByteResult getJuBenCityInfo(@RequestParam("cityId") final int cityId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.juBenService.getJuBenCityInfo(cityId, playerDto), request);
    }
}
