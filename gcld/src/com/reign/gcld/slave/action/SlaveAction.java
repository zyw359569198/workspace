package com.reign.gcld.slave.action;

import com.reign.gcld.common.web.*;
import com.reign.gcld.slave.service.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.player.dto.*;
import com.reign.framework.netty.servlet.*;
import com.reign.framework.netty.mvc.result.*;
import com.reign.framework.netty.mvc.annotation.*;

public class SlaveAction extends BaseAction
{
    private static final long serialVersionUID = 1842426954848200328L;
    @Autowired
    private ISlaveService slaveService;
    
    @Command("slave@getSlaveInfo")
    public ByteResult getSlaveInfo(@SessionParam("PLAYER") final PlayerDto playerDto, final Request request) {
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.slaveService.getSlaveInfo(playerDto), request);
    }
    
    @Command("slave@lash")
    public ByteResult lash(@RequestParam("vId") final int vId, @SessionParam("PLAYER") final PlayerDto playerDto, final Request request) {
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.slaveService.lash(playerDto, vId), request);
    }
    
    @Command("slave@makeCell")
    public ByteResult makeCell(@SessionParam("PLAYER") final PlayerDto playerDto, final Request request) {
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.slaveService.makeCell(playerDto), request);
    }
    
    @Command("slave@escape")
    public ByteResult escape(@RequestParam("generalId") final int generalId, @SessionParam("PLAYER") final PlayerDto playerDto, final Request request) {
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.slaveService.escape(playerDto, generalId), request);
    }
    
    @Command("slave@viewMaster")
    public ByteResult viewMaster(@RequestParam("masterId") final int masterId, @SessionParam("PLAYER") final PlayerDto playerDto, final Request request) {
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.slaveService.viewMaster(playerDto, masterId), request);
    }
    
    @Command("slave@freedom")
    public ByteResult freedom(@RequestParam("generalId") final int generalId, @SessionParam("PLAYER") final PlayerDto playerDto, final Request request) {
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.slaveService.freedom(playerDto, generalId), request);
    }
    
    @Command("slave@updateLimbo")
    public ByteResult updateLimbo(@SessionParam("PLAYER") final PlayerDto playerDto, final Request request) {
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.slaveService.updateLimbo(playerDto), request);
    }
    
    @Command("slave@updateLashLv")
    public ByteResult updateLashLv(@SessionParam("PLAYER") final PlayerDto playerDto, final Request request) {
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.slaveService.updateLashLv(playerDto.playerId), request);
    }
    
    @Command("slave@getTrailGold")
    public ByteResult getTrailGold(@SessionParam("PLAYER") final PlayerDto playerDto, final Request request) {
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.slaveService.getTrailGold(playerDto), request);
    }
    
    @Command("slave@useInTaril")
    public ByteResult useInTaril(@SessionParam("PLAYER") final PlayerDto playerDto, final Request request) {
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.slaveService.useInTaril(playerDto), request);
    }
}
