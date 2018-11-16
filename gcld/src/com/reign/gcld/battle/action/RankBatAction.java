package com.reign.gcld.battle.action;

import com.reign.gcld.common.web.*;
import com.reign.gcld.battle.service.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.framework.netty.servlet.*;
import com.reign.framework.netty.mvc.result.*;
import com.reign.gcld.player.dto.*;
import com.reign.framework.netty.mvc.annotation.*;

public class RankBatAction extends BaseAction
{
    private static final long serialVersionUID = -7189925495563995470L;
    @Autowired
    private IRankBatService rankBatService;
    
    @Command("rankBat@getRankInfo")
    public ByteResult getRankInfo(final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.rankBatService.getRankPanel(playerDto), request);
    }
    
    @Command("rankBat@ChallengeRewardInfo")
    public ByteResult ChallengeRewardInfo(@RequestParam("targetRank") final int targetRank, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.rankBatService.ChallengeRewardInfo(playerDto, targetRank), request);
    }
    
    @Command("rankBat@doReward")
    public ByteResult doReward(final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.rankBatService.doReward(playerDto), request);
    }
    
    @Command("rankBat@getRewardInfo")
    public ByteResult getRewardInfo(final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.rankBatService.getRewardInfo(playerDto.playerId), request);
    }
    
    @Command("rankBat@buyOneTime")
    public ByteResult buyOneTime(final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.rankBatService.buyOneTime(playerDto.playerId), request);
    }
    
    @Command("rankBat@getJifenReward")
    public ByteResult getJifenReward(final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.rankBatService.getJifenReward(playerDto.playerId), request);
    }
}
