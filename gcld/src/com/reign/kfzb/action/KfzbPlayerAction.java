package com.reign.kfzb.action;

import com.reign.kf.match.common.*;
import com.reign.kfzb.service.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.framework.netty.servlet.*;
import com.reign.framework.netty.mvc.result.*;
import com.reign.kf.match.common.web.session.*;
import com.reign.util.*;
import com.reign.framework.json.*;
import com.reign.framework.netty.mvc.annotation.*;

public class KfzbPlayerAction extends BaseAction
{
    private static final long serialVersionUID = 1L;
    @Autowired
    IKfzbScheduleService kfzbScheduleService;
    
    @Command("gameserver@getKfzbMatchRTInfo")
    public ByteResult getMatchRTInfo(final Request request) {
        final PlayerDto player = (PlayerDto)this.getFromSession("CONNECTOR", request);
        if (player == null) {
            return null;
        }
        return this.getStateResult(this.kfzbScheduleService.getPlayerMatchInfo(player.getCompetitorId()), request);
    }
    
    private ByteResult getStateResult(final Tuple<byte[], State> playerMatchInfo, final Request request) {
        return this.getResult(JsonBuilder.getJson(playerMatchInfo.right, playerMatchInfo.left), request);
    }
    
    @Command("gameserver@getKfzbBattleIniInfo")
    public ByteResult getBattleIniInfo(final Request request) {
        final PlayerDto player = (PlayerDto)this.getFromSession("CONNECTOR", request);
        if (player == null) {
            return null;
        }
        return this.getResult(this.kfzbScheduleService.getBattleIniInfo(player.getCompetitorId()), request);
    }
    
    @Command("gameserver@useKfzbST")
    public ByteResult chooseStrategyOrTactic(@RequestParam("pos") final int pos, @RequestParam("tacticId") final int tacticId, @RequestParam("strategyId") final int strategyId, final Request request) {
        final PlayerDto player = (PlayerDto)this.getFromSession("CONNECTOR", request);
        if (player == null) {
            return null;
        }
        return this.getResult(this.kfzbScheduleService.chooseStrategyOrTactic(player.getCompetitorId(), pos, tacticId, strategyId), request);
    }
}
