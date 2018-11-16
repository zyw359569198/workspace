package com.reign.kf.match.action;

import com.reign.kf.match.common.*;
import com.reign.kfwd.service.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.framework.netty.servlet.*;
import com.reign.framework.netty.mvc.result.*;
import com.reign.kf.match.common.web.session.*;
import com.reign.util.*;
import com.reign.framework.json.*;
import com.reign.framework.netty.mvc.annotation.*;

public class KfwdPlayerAction extends BaseAction
{
    private static final long serialVersionUID = 1L;
    @Autowired
    IKfwdScheduleService kfwdScheduleService;
    
    @Command("gameserver@getKfwdMatchRTInfo")
    public ByteResult getMatchRTInfo(final Request request) {
        final PlayerDto player = (PlayerDto)this.getFromSession("CONNECTOR", request);
        if (player == null) {
            return null;
        }
        return this.getStateResult(this.kfwdScheduleService.getPlayerMatchInfo(player.getCompetitorId()), request);
    }
    
    private ByteResult getStateResult(final Tuple<byte[], State> playerMatchInfo, final Request request) {
        return this.getResult(JsonBuilder.getJson(playerMatchInfo.right, playerMatchInfo.left), request);
    }
    
    @Command("gameserver@getKfwdBattleIniInfo")
    public ByteResult getBattleIniInfo(final Request request) {
        final PlayerDto player = (PlayerDto)this.getFromSession("CONNECTOR", request);
        if (player == null) {
            return null;
        }
        return this.getResult(this.kfwdScheduleService.getBattleIniInfo(player.getCompetitorId()), request);
    }
    
    @Command("gameserver@getKfwdRankingInfo")
    public ByteResult getKfwdRankingInfo(final Request request) {
        final PlayerDto player = (PlayerDto)this.getFromSession("CONNECTOR", request);
        if (player == null) {
            return null;
        }
        return this.getResult(this.kfwdScheduleService.getKfwdRankingInfo(player.getCompetitorId()), request);
    }
    
    @Command("gameserver@useST")
    public ByteResult chooseStrategyOrTactic(@RequestParam("pos") final int pos, @RequestParam("tacticId") final int tacticId, @RequestParam("strategyId") final int strategyId, final Request request) {
        final PlayerDto player = (PlayerDto)this.getFromSession("CONNECTOR", request);
        if (player == null) {
            return null;
        }
        return this.getResult(this.kfwdScheduleService.chooseStrategyOrTactic(player.getCompetitorId(), pos, tacticId, strategyId), request);
    }
    
    @Command("gameserver@getKfwdDayReward")
    public ByteResult getKfwdDayReward(@RequestParam("day") final int day, final Request request) {
        final PlayerDto player = (PlayerDto)this.getFromSession("CONNECTOR", request);
        if (player == null) {
            return null;
        }
        return this.getResult(this.kfwdScheduleService.getKfwdDayReward(player.getCompetitorId(), day), request);
    }
}
