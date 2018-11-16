package com.reign.gcld.kfgz.action;

import com.reign.gcld.common.web.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.kfgz.service.*;
import com.reign.framework.netty.servlet.*;
import com.reign.framework.netty.mvc.result.*;
import com.reign.gcld.player.dto.*;
import com.reign.framework.netty.mvc.annotation.*;

public class KfgzAction extends BaseAction
{
    private static final long serialVersionUID = 1L;
    @Autowired
    private IKfgzMatchService kfgzMatchService;
    @Autowired
    private IKfgzSeasonService kfgzSeasonService;
    
    @Command("kfgz@signUp")
    public ByteResult signUp(final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.kfgzMatchService.signUp(playerDto), request);
    }
    
    @Command("kfgz@scheduleInfoList")
    public ByteResult scheduleInfoList(final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.kfgzSeasonService.scheduleInfoList(playerDto), request);
    }
    
    @Command("kfgz@getRewardBoard")
    public ByteResult getRewardBoard(final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.kfgzMatchService.getRewardBoard(playerDto), request);
    }
    
    @Command("kfgz@getReward")
    public ByteResult getReward(@RequestParam("times") final int times, final Request request) throws Exception {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.kfgzMatchService.getReward(playerDto, times), request);
    }
    
    @Command("kfgz@getKfgzAllRankRes")
    public ByteResult getKfgzAllRankRes(final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.kfgzSeasonService.getKfgzAllRankRes(playerDto), request);
    }
    
    @Command("kfgz@getEndRewardBoard")
    public ByteResult getEndRewardBoard(final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.kfgzSeasonService.getEndRewardBoard(playerDto), request);
    }
    
    @Command("kfgz@getEndReward")
    public ByteResult getEndReward(@RequestParam("id") final int id, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.kfgzSeasonService.getEndReward(playerDto, id), request);
    }
}
