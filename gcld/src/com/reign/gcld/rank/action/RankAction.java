package com.reign.gcld.rank.action;

import com.reign.gcld.common.web.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.world.service.*;
import com.reign.gcld.rank.service.*;
import com.reign.gcld.player.dto.*;
import com.reign.framework.netty.servlet.*;
import com.reign.framework.netty.mvc.result.*;
import com.reign.framework.netty.mvc.annotation.*;
import com.reign.gcld.world.util.*;
import com.reign.gcld.common.*;
import com.reign.framework.json.*;

public class RankAction extends BaseAction
{
    private static final long serialVersionUID = 5749082181397795945L;
    @Autowired
    public IRankService rankService;
    @Autowired
    public ICityService cityService;
    @Autowired
    public IIndividualTaskService individualTaskService;
    
    @Command("rank@getInfo")
    public ByteResult send(@RequestParam("rankId") final int rankId, @SessionParam("PLAYER") final PlayerDto playerDto, final Request request) {
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.rankService.getRankList(rankId), request);
    }
    
    @Command("nationRank@getCurRankInfo")
    public ByteResult getCurRankInfo(@SessionParam("PLAYER") final PlayerDto playerDto, @RequestParam("type") final int type, final Request request) {
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.rankService.getCurRankInfo(playerDto, type), request);
    }
    
    @Command("nationRank@getNationTaskReward")
    public ByteResult getNationTaskReward(@SessionParam("PLAYER") final PlayerDto playerDto, @RequestParam("taskId") final int taskId, final Request request) {
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.rankService.getNationTaskReward(playerDto, taskId), request);
    }
    
    @Command("nationRank@startNationTask")
    public ByteResult startNationTask(@SessionParam("PLAYER") final PlayerDto playerDto, @RequestParam("taskType") final int taskType, final Request request) {
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.rankService.startNationTask(playerDto, taskType), request);
    }
    
    @Command("nationRank@getInvestmentInfo")
    public ByteResult getPersonalInvestmentInfo(@SessionParam("PLAYER") final PlayerDto playerDto, final Request request) {
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.rankService.getPersonalInvestmentInfo(playerDto), request);
    }
    
    @Command("nationRank@invenstCopper")
    public ByteResult invest(@SessionParam("PLAYER") final PlayerDto playerDto, final Request request) {
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.rankService.investCopper(playerDto), request);
    }
    
    @Command("nationRank@investCdConfirm")
    public ByteResult investCdRecover(@SessionParam("PLAYER") final PlayerDto playerDto, final Request request) {
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.rankService.investCdRecoverConfirm(playerDto), request);
    }
    
    @Command("nationRank@investCdRecover")
    public ByteResult investCdRecoverConfirm(@SessionParam("PLAYER") final PlayerDto playerDto, final Request request) {
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.rankService.investCdRecover(playerDto), request);
    }
    
    @Command("rank@getTwoRankInfo")
    public ByteResult getOccupyRankInfo(@SessionParam("PLAYER") final PlayerDto playerDto, @RequestParam("page") final int page, @RequestParam("type") final int type, final Request request) {
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.rankService.getOccupyRankInfo(page, playerDto, type), request);
    }
    
    @Command("rank@getRankerReward")
    public ByteResult getRankerReward(@SessionParam("PLAYER") final PlayerDto playerDto, @RequestParam("type") final int type, final Request request) {
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.rankService.getRankerReward(playerDto, type), request);
    }
    
    @Deprecated
    @Command("rank@useInvestCoupon")
    public ByteResult useInvestCoupon(@SessionParam("PLAYER") final PlayerDto playerDto, @RequestParam("type") final int type, final Request request) {
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.rankService.useInvestCoupon(playerDto, type), request);
    }
    
    @Command("rank@isWholeKill")
    public ByteResult isWholeKill(@SessionParam("PLAYER") final PlayerDto playerDto, final Request request) {
        if (playerDto == null) {
            return null;
        }
        final boolean isWholeKill = WorldUtil.isWholePointKill();
        if (isWholeKill) {
            final JsonDocument doc = new JsonDocument();
            doc.startObject();
            this.cityService.getWholeKillTitle(playerDto.playerId, playerDto.forceId, doc, false);
            doc.endObject();
            Players.push(playerDto.playerId, PushCommand.PUSH_WHOLE_KILL, doc.toByte());
        }
        return this.getResult(JsonBuilder.getJson(State.SUCCESS, JsonBuilder.getSimpleJson("isWholeKill", isWholeKill)), request);
    }
    
    @Command("rank@getIndivInfo")
    public ByteResult getIndivInfo(@SessionParam("PLAYER") final PlayerDto playerDto, final Request request) {
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.individualTaskService.getIndiviInfo(playerDto), request);
    }
    
    @Command("rank@investyx")
    public ByteResult investYx(@SessionParam("PLAYER") final PlayerDto playerDto, final Request request, @RequestParam("type") final int type) {
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.rankService.investYx(playerDto, type), request);
    }
    
    @Command("rank@getIndivReward")
    public ByteResult getIndivReward(@SessionParam("PLAYER") final PlayerDto playerDto, final Request request, @RequestParam("id") final int id) {
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.individualTaskService.getIndivReward(playerDto, id), request);
    }
}
