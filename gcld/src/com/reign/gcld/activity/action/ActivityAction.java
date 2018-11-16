package com.reign.gcld.activity.action;

import com.reign.gcld.common.web.*;
import com.reign.gcld.activity.service.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.framework.netty.servlet.*;
import com.reign.framework.netty.mvc.result.*;
import com.reign.gcld.player.dto.*;
import com.reign.framework.netty.mvc.annotation.*;

public class ActivityAction extends BaseAction
{
    private static final long serialVersionUID = 4623941159737491775L;
    @Autowired
    private IActivityService activityService;
    
    @Command("activity@get51activity")
    public ByteResult get51activity(final Request request) {
        final PlayerDto dto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (dto == null) {
            return null;
        }
        return this.getResult(this.activityService.get51activity(dto), request);
    }
    
    @Command("activity@reward51Activity")
    public ByteResult reward51Activity(final Request request) {
        final PlayerDto dto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (dto == null) {
            return null;
        }
        return this.getResult(this.activityService.reward51Activity(dto), request);
    }
    
    @Command("activity@getLvExpActivity")
    public ByteResult getLvExpActivity(final Request request) {
        final PlayerDto dto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (dto == null) {
            return null;
        }
        return this.getResult(this.activityService.getLvExpActivity(dto), request);
    }
    
    @Command("activity@rewardLvExpActivity")
    public ByteResult rewardLvExpActivity(final Request request) {
        final PlayerDto dto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (dto == null) {
            return null;
        }
        return this.getResult(this.activityService.rewardLvExpActivity(dto), request);
    }
    
    @Command("activity@initLvExp")
    public ByteResult initLvExp(final Request request) {
        final PlayerDto dto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (dto == null) {
            return null;
        }
        return this.getResult(this.activityService.initLvExp(dto), request);
    }
    
    @Command("activity@getDragonInfo")
    public ByteResult getDragonInfo(final Request request) {
        final PlayerDto dto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (dto == null) {
            return null;
        }
        return this.getResult(this.activityService.getDragonInfo(dto), request);
    }
    
    @Command("activity@useDragon")
    public ByteResult useDragon(final Request request) {
        final PlayerDto dto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (dto == null) {
            return null;
        }
        return this.getResult(this.activityService.useDragon(dto), request);
    }
    
    @Command("activity@quenching")
    public ByteResult getQuenching(final Request request) {
        final PlayerDto dto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (dto == null) {
            return null;
        }
        return this.getResult(this.activityService.getQuenching(dto), request);
    }
    
    @Command("activity@getIronInfo")
    public ByteResult getIronInfo(final Request request) {
        final PlayerDto dto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (dto == null) {
            return null;
        }
        return this.getResult(this.activityService.getIronInfo(dto), request);
    }
    
    @Command("activity@useIron")
    public ByteResult useIron(final Request request) {
        final PlayerDto dto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (dto == null) {
            return null;
        }
        return this.getResult(this.activityService.useIron(dto), request);
    }
    
    @Command("activity@getDstqInfo")
    public ByteResult getDstqInfo(final Request request) {
        final PlayerDto dto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (dto == null) {
            return null;
        }
        return this.getResult(this.activityService.getDstqInfo(dto), request);
    }
    
    @Command("activity@get360PrivilegeInfo")
    public ByteResult get360PrivilegeInfo(final Request request) {
        final PlayerDto dto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (dto == null) {
            return null;
        }
        return this.getResult(this.activityService.get360PrivilegeInfo(dto), request);
    }
    
    @Command("activity@recv360Privilege")
    public ByteResult recv360Privilege(final Request request, @RequestParam("level") final int level) {
        final PlayerDto dto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (dto == null) {
            return null;
        }
        return this.getResult(this.activityService.recv360Privilege(dto, level), request);
    }
}
