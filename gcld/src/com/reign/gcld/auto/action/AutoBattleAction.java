package com.reign.gcld.auto.action;

import com.reign.gcld.common.web.*;
import com.reign.gcld.auto.service.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.framework.netty.servlet.*;
import com.reign.framework.netty.mvc.result.*;
import com.reign.gcld.player.dto.*;
import com.reign.framework.netty.mvc.annotation.*;

public class AutoBattleAction extends BaseAction
{
    private static final long serialVersionUID = -9204558043882038004L;
    @Autowired
    private IAutoBattleService autoBattleService;
    
    @Command("battle@startAutoBattle")
    public ByteResult startAutoBattle(@RequestParam("cityId") final int cityId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.autoBattleService.startAutoBattle(playerDto, cityId), request);
    }
    
    @Command("battle@stopAutoBattle")
    public ByteResult stopAutoBattle(final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.autoBattleService.stopAutoBattle(playerDto), request);
    }
    
    @Command("battle@getAutoBattleDetail")
    public ByteResult getAutoBattleDetail(final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.autoBattleService.getAutoBattleDetail(playerDto), request);
    }
}
