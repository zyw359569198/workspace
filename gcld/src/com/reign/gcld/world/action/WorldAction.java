package com.reign.gcld.world.action;

import com.reign.gcld.common.web.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.world.service.*;
import com.reign.gcld.rank.service.*;
import com.reign.framework.netty.servlet.*;
import com.reign.framework.netty.mvc.result.*;
import com.reign.gcld.player.dto.*;
import com.reign.framework.netty.mvc.annotation.*;

public class WorldAction extends BaseAction
{
    private static final long serialVersionUID = 1L;
    @Autowired
    private IWorldService worldService;
    @Autowired
    private ICityService cityService;
    @Autowired
    private IRankService rankService;
    
    @Command("world@leaveWorldScene")
    public ByteResult leaveWorldScene(@RequestParam("generalId") final int generalId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        this.cityService.leaveGroup(playerDto.playerId, playerDto.forceId, request);
        return this.getResult(this.cityService.leaveWorldScene(playerDto, generalId, request), request);
    }
    
    @Command("world@enterWorldScene")
    public ByteResult enterWorldScene(final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.cityService.enterWorldScene(playerDto, request), request);
    }
    
    @Command("world@getCityEventPanel")
    public ByteResult getCityEventPanel(@RequestParam("cityId") final int cityId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.cityService.getCityEventPanel(playerDto, cityId), request);
    }
    
    @Command("world@dealCityEvent")
    public ByteResult dealCityEvent(@RequestParam("cityId") final int cityId, @RequestParam("option") final int option, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.cityService.dealCityEvent(playerDto, cityId, option), request);
    }
    
    @Command("world@getPEPanel")
    public ByteResult getPEPanel(@RequestParam("cityId") final int cityId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.cityService.getPlayerEventPanel(playerDto, cityId), request);
    }
    
    @Command("world@dealPlayerEvent")
    public ByteResult dealPlayerEvent(@RequestParam("cityId") final int cityId, @RequestParam("id") final int id, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.cityService.dealPlayerEvent(playerDto, cityId, id), request);
    }
    
    @Command("world@attMoveInfo")
    public ByteResult attMoveInfo(@RequestParam("generalId") final int generalId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.cityService.attMoveInfo(playerDto, generalId), request);
    }
    
    @Command("world@getOperations")
    public ByteResult getOperations(@RequestParam("cityId") final int cityId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.worldService.getOperations(playerDto, cityId), request);
    }
    
    @Command("world@autoMoveInfo")
    public ByteResult autoMoveInfo(@RequestParam("cityId") final int cityId, @RequestParam("generalId") final int generalId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.cityService.autoMoveInfo(playerDto, cityId, generalId), request);
    }
    
    @Command("world@autoMove")
    public ByteResult autoMove(@RequestParam("cityId") final int cityId, @RequestParam("generalId") final int generalId, @RequestParam("kick") final int kick, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.cityService.autoMove(playerDto.playerId, generalId, cityId, kick), request);
    }
    
    @Command("world@autoMoveStop")
    public ByteResult autoMoveStop(@RequestParam("generalId") final int generalId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.cityService.autoMoveStop(playerDto, generalId), request);
    }
    
    @Command("world@countryReward")
    public ByteResult countryReward(@RequestParam("id") final int id, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.worldService.getCountryReward(id, playerDto), request);
    }
    
    @Command("world@getRewardInfo")
    public ByteResult getRewardInfo(@RequestParam("id") final int id, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.worldService.getRewardInfo(playerDto, id), request);
    }
    
    @Command("world@quizReward")
    public ByteResult getQuizReward(@RequestParam("quiziId") final int quizId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.worldService.getQuizReward(playerDto, quizId), request);
    }
    
    @Command("world@getMoveResponse")
    public ByteResult getMoveResponse(@RequestParam("generalId") final int generalId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.cityService.pleaseGiveMeAReply(playerDto, generalId), request);
    }
    
    @Command("world@moveStop")
    public ByteResult moveStop(@RequestParam("vId") final int vId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.cityService.moveStop(vId), request);
    }
    
    @Command("world@getCityDetailInfo")
    public ByteResult getCityDetailInfo(@RequestParam("cityId") final int cityId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.cityService.getCityDetailInfo(cityId, playerDto), request);
    }
    
    @Command("world@getManzuShoumaiInfo")
    public ByteResult getManzuShoumaiInfo(final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.cityService.getManzuShoumaiInfo(playerDto), request);
    }
    
    @Command("world@manzuShoumai")
    public ByteResult manzuShoumai(@RequestParam("cityId") final int cityId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.cityService.manzuShoumai(playerDto, cityId), request);
    }
    
    @Command("world@getCoverManzuShoumaiCdCost")
    public ByteResult getCoverManzuShoumaiCdCost(final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.cityService.getCoverManzuShoumaiCdCost(playerDto), request);
    }
    
    @Command("world@coverManzuShoumaiCd")
    public ByteResult coverManzuShoumaiCd(final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.cityService.coverManzuShoumaiCd(playerDto), request);
    }
    
    @Command("world@faDongmanzu")
    public ByteResult faDongmanzu(@RequestParam("cityId") final int cityId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.cityService.faDongmanzu(playerDto, cityId), request);
    }
    
    @Command("world@getRewardWholePointKill")
    public ByteResult getRewardWholePointKill(@RequestParam("cityId") final int cityId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.rankService.getRewardWholePointKill(playerDto), request);
    }
    
    @Command("world@getFarmCityInfo")
    public ByteResult getFarmCityInfo(final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.cityService.getFarmCityInfo(playerDto), request);
    }
}
