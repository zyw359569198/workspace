package com.reign.gcld.nation.action;

import com.reign.gcld.common.web.*;
import com.reign.gcld.nation.service.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.framework.netty.servlet.*;
import com.reign.framework.netty.mvc.result.*;
import com.reign.gcld.player.dto.*;
import com.reign.framework.netty.mvc.annotation.*;

public class ProtectAction extends BaseAction
{
    private static final long serialVersionUID = -6506626055568724373L;
    @Autowired
    private IProtectService protectService;
    
    @Command("protect@getProtectInfo")
    public ByteResult getNationInfo(final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.protectService.getProtectInfo(playerDto), request);
    }
    
    @Command("protect@getProtectRewards")
    public ByteResult getProtectRewards(final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.protectService.getProtectReward(playerDto), request);
    }
}
