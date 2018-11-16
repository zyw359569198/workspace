package com.reign.gcld.score.action;

import com.reign.gcld.common.web.*;
import com.reign.gcld.player.dto.*;
import com.reign.framework.netty.servlet.*;
import com.reign.framework.netty.mvc.result.*;
import com.reign.gcld.common.*;
import com.reign.framework.json.*;
import com.reign.framework.netty.mvc.annotation.*;

public class ScoreRankAction extends BaseAction
{
    private static final long serialVersionUID = 2114117996300379464L;
    
    @Command("scoreRank@getRankInfo")
    public ByteResult getRankInfo(@SessionParam("PLAYER") final PlayerDto playerDto, final Request request) {
        if (playerDto == null) {
            return null;
        }
        return this.getResult(JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10012), request);
    }
    
    @Command("scoreRank@getBoxReward")
    public ByteResult getBoxReward(@SessionParam("PLAYER") final PlayerDto playerDto, final Request request) {
        if (playerDto == null) {
            return null;
        }
        return this.getResult(JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10012), request);
    }
    
    @Command("scoreRank@getRankReward")
    public ByteResult getRankReward(@SessionParam("PLAYER") final PlayerDto playerDto, final Request request) {
        if (playerDto == null) {
            return null;
        }
        return this.getResult(JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10012), request);
    }
}
