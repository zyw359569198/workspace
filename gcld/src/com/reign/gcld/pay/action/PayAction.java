package com.reign.gcld.pay.action;

import com.reign.gcld.common.web.*;
import com.reign.gcld.pay.service.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.framework.netty.servlet.*;
import com.reign.framework.netty.mvc.result.*;
import com.reign.gcld.player.dto.*;
import com.reign.framework.netty.mvc.annotation.*;

public class PayAction extends BaseAction
{
    private static final long serialVersionUID = 1L;
    @Autowired
    private IPayService payService;
    
    @Command("pay@getVipInfo")
    public ByteResult getVipInfo(final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.payService.getVipInfo(playerDto), request);
    }
    
    @Command("pay@handleVipPrivilege")
    public ByteResult handleVipPrivilege(@RequestParam("pic") final String pic, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.payService.handleVipPrivilege(pic, playerDto), request);
    }
    
    @Command("pay@getPayAcitivityInfo")
    public ByteResult getPayAcitivityInfo(final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.payService.getPayAcitivityInfo(playerDto), request);
    }
    
    @Command("pay@getTicketAcitivityInfo")
    public ByteResult getTicketAcitivityInfo(final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.payService.getTicketAcitivityInfo(playerDto), request);
    }
    
    @Command("pay@getYellowVipInfo")
    public ByteResult getYellowVipInfo(final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.payService.getYellowVipInfo(playerDto), request);
    }
    
    @Command("pay@recvYellowVipReward")
    public ByteResult recvYellowVipReward(@RequestParam("type") final int type, @RequestParam("seq") final int seq, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.payService.recvYellowVipReward(playerDto, type, seq), request);
    }
}
