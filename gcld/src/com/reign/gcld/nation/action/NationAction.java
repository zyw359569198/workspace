package com.reign.gcld.nation.action;

import com.reign.gcld.common.web.*;
import com.reign.gcld.nation.service.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.framework.netty.servlet.*;
import com.reign.framework.netty.mvc.result.*;
import com.reign.gcld.player.dto.*;
import com.reign.framework.netty.mvc.annotation.*;

public class NationAction extends BaseAction
{
    private static final long serialVersionUID = -5444588166868207780L;
    @Autowired
    private INationService nationService;
    
    @Command("nation@getNationInfo")
    public ByteResult getNationInfo(final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.nationService.getNationInfo(playerDto), request);
    }
    
    @Command("nation@openTry")
    public ByteResult openTry(final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.nationService.openTry(playerDto), request);
    }
    
    @Command("nation@getTryInfo")
    public ByteResult getTryInfo(final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.nationService.getTryInfo(playerDto), request);
    }
    
    @Command("nation@getReward")
    public ByteResult getReward(final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.nationService.getReward(playerDto), request);
    }
}
