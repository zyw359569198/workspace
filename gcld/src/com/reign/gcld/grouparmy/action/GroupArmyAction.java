package com.reign.gcld.grouparmy.action;

import com.reign.gcld.common.web.*;
import com.reign.gcld.grouparmy.service.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.framework.netty.servlet.*;
import com.reign.framework.netty.mvc.result.*;
import com.reign.gcld.player.dto.*;
import com.reign.framework.netty.mvc.annotation.*;

public class GroupArmyAction extends BaseAction
{
    private static final long serialVersionUID = 1L;
    @Autowired
    private IGroupArmyService groupArmyService;
    
    @Command("grouparmy@getTeamInfo")
    public ByteResult getTeamInfo(final Request request, @RequestParam("cityId") final int cityId, @RequestParam("generalId") final int generalId) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.groupArmyService.getTeamInfo(playerDto, cityId, generalId), request);
    }
    
    @Command("grouparmy@followGeneral")
    public ByteResult followGeneral(final Request request, @RequestParam("generalId") final int generalId, @RequestParam("followPlayerId") final int followPlayerId, @RequestParam("followGeneralId") final int followGeneralId) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.groupArmyService.followGeneral(playerDto, generalId, followPlayerId, followGeneralId), request);
    }
    
    @Command("grouparmy@stopFollow")
    public ByteResult stopFollow(final Request request, @RequestParam("generalId") final int generalId, @RequestParam("followPlayerId") final int followPlayerId, @RequestParam("followGeneralId") final int followGeneralId) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.groupArmyService.stopFollow(playerDto, generalId, followPlayerId, followGeneralId), request);
    }
}
