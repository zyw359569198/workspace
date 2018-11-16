package com.reign.gcld.world.action;

import com.reign.gcld.common.web.*;
import com.reign.framework.netty.servlet.*;
import com.reign.framework.netty.mvc.result.*;
import com.reign.gcld.player.dto.*;
import com.reign.gcld.common.*;
import com.reign.framework.json.*;
import com.reign.framework.netty.mvc.annotation.*;

public class KillRankAction extends BaseAction
{
    private static final long serialVersionUID = -7728818035299398002L;
    
    @Command("killRank@getRankList")
    public ByteResult getRankList(final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10012), request);
    }
    
    @Command("killRank@reward")
    public ByteResult reward(final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10012), request);
    }
    
    @Command("killRank@getBoxReward")
    public ByteResult getBoxReward(@RequestParam("boxId") final int boxId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10012), request);
    }
}
