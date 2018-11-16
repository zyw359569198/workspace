package com.reign.gcld.charge.action;

import com.reign.gcld.common.web.*;
import com.reign.gcld.charge.service.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.framework.netty.servlet.*;
import com.reign.framework.netty.mvc.result.*;
import com.reign.gcld.player.dto.*;
import com.reign.gcld.common.*;
import com.reign.framework.json.*;
import com.reign.framework.netty.mvc.annotation.*;

public class ChargeAction extends BaseAction
{
    private static final long serialVersionUID = 1L;
    @Autowired
    private IChargeItemService chargeItemService;
    
    @Command("charge@noDisturb")
    public ByteResult noDisturb(@RequestParam("key") final String[] key, @RequestParam("on") final int[] on, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return this.getResult(JsonBuilder.getJson(State.FAIL, LocalMessages.T_USER_10004), request);
        }
        final byte[] result = this.chargeItemService.noDisturb(playerDto.playerId, key, on);
        return this.getResult(result, request);
    }
}
