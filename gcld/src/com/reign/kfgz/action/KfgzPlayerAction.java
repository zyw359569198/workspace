package com.reign.kfgz.action;

import com.reign.kf.match.common.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.kfgz.service.*;
import com.reign.framework.netty.servlet.*;
import com.reign.framework.netty.mvc.result.*;
import com.reign.kf.match.common.web.session.*;
import com.reign.kfgz.constants.*;
import com.reign.framework.json.*;
import com.reign.framework.netty.mvc.annotation.*;
import com.reign.kfgz.control.*;
import com.reign.kfgz.comm.*;

public class KfgzPlayerAction extends BaseAction
{
    private static final long serialVersionUID = 1L;
    @Autowired
    IKfgzScheduleService kfgzScheduleService;
    @Autowired
    IKfgzGroupTeamService kfgzGroupTeamService;
    @Autowired
    IKfgzOrderService kfgzOrderService;
    
    @Command("gameserver@kfgzlogin")
    public ByteResult login(@RequestParam("competitorId") final int competitorId, @RequestParam("certificate") final String certificate, final Request request) {
        final PlayerDto playerDto = new PlayerDto(competitorId, 2);
        if (!certificate.equals(KfgzCommConstants.getKfgzKey(competitorId, KfgzManager.curSeasonId))) {
            return this.getResult(JsonBuilder.getJson(State.FAIL, "\u8bc1\u4e66\u9519\u8bef"), request);
        }
        this.putToSession("CONNECTOR", playerDto, request);
        return this.getResult(JsonBuilder.getJson(State.SUCCESS, ""), request);
    }
    
    @Command("gameserver@gzHeart")
    public ByteResult login(final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("CONNECTOR", request);
        if (playerDto == null) {
            this.getResult(JsonBuilder.getJson(State.FAIL, "\u8fde\u63a5\u5df2\u7ecf\u65ad\u5f00\uff0c\u6e05\u91cd\u65b0\u767b\u5f55\u56fd\u6218"), request);
        }
        return this.getResult(JsonBuilder.getJson(State.SUCCESS, ""), request);
    }
    
    @Command("gameserver@getKfgzBattleIniInfo")
    public ByteResult getBattleIniInfo(@RequestParam("teamId") final int teamId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("CONNECTOR", request);
        if (playerDto == null) {
            return null;
        }
        final KfPlayerInfo player = KfgzPlayerManager.getPlayerByCId(playerDto.getCompetitorId());
        if (player == null) {
            return null;
        }
        return this.getResult(this.kfgzScheduleService.getBattleIniInfo(player, teamId), request);
    }
    
    @Command("gameserver@kfgzUseST")
    public ByteResult chooseStrategyOrTactic(@RequestParam("teamId") final int teamId, @RequestParam("pos") final int pos, @RequestParam("tacticId") final int tacticId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("CONNECTOR", request);
        if (playerDto == null) {
            return null;
        }
        final KfPlayerInfo player = KfgzPlayerManager.getPlayerByCId(playerDto.getCompetitorId());
        if (player == null) {
            return null;
        }
        return this.getResult(this.kfgzScheduleService.chooseStrategyOrTactic(player, pos, tacticId, teamId), request);
    }
    
    @Command("gameserver@doSolo")
    public ByteResult doSolo(@RequestParam("teamId") final int teamId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("CONNECTOR", request);
        if (playerDto == null) {
            return null;
        }
        final KfPlayerInfo player = KfgzPlayerManager.getPlayerByCId(playerDto.getCompetitorId());
        if (player == null) {
            return null;
        }
        return this.getResult(this.kfgzScheduleService.doSolo(player, teamId), request);
    }
    
    @Command("gameserver@doRush")
    public ByteResult doRush(@RequestParam("toTeamId") final int toTeamId, @RequestParam("gIds") final String gIds, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("CONNECTOR", request);
        if (playerDto == null) {
            return null;
        }
        final KfPlayerInfo player = KfgzPlayerManager.getPlayerByCId(playerDto.getCompetitorId());
        if (player == null) {
            return null;
        }
        return this.getResult(this.kfgzScheduleService.doRush(player, toTeamId, gIds), request);
    }
    
    @Command("gameserver@getCanRushInfo")
    public ByteResult getCanRushInfo(@RequestParam("teamId") final int teamId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("CONNECTOR", request);
        if (playerDto == null) {
            return null;
        }
        final KfPlayerInfo player = KfgzPlayerManager.getPlayerByCId(playerDto.getCompetitorId());
        if (player == null) {
            return null;
        }
        return this.getResult(this.kfgzScheduleService.getCanRushInfo(player, teamId), request);
    }
    
    @Command("gameserver@fastAddTroopHp")
    public ByteResult fastAddTroopHp(@RequestParam("gId") final int gId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("CONNECTOR", request);
        if (playerDto == null) {
            return null;
        }
        final KfPlayerInfo player = KfgzPlayerManager.getPlayerByCId(playerDto.getCompetitorId());
        if (player == null) {
            return null;
        }
        return this.getResult(this.kfgzScheduleService.fastAddTroopHp(player, gId), request);
    }
    
    @Command("gameserver@getRetreatInfo")
    public ByteResult getRetreatInfo(@RequestParam("teamId") final int teamId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("CONNECTOR", request);
        if (playerDto == null) {
            return null;
        }
        final KfPlayerInfo player = KfgzPlayerManager.getPlayerByCId(playerDto.getCompetitorId());
        if (player == null) {
            return null;
        }
        return this.getResult(this.kfgzScheduleService.getRetreatInfo(player, teamId), request);
    }
    
    @Command("gameserver@doRetreat")
    public ByteResult doRetreat(@RequestParam("gIds") final String gIds, @RequestParam("toTeamId") final int toTeamId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("CONNECTOR", request);
        if (playerDto == null) {
            return null;
        }
        final KfPlayerInfo player = KfgzPlayerManager.getPlayerByCId(playerDto.getCompetitorId());
        if (player == null) {
            return null;
        }
        return this.getResult(this.kfgzScheduleService.doRetreat(player, gIds, toTeamId), request);
    }
    
    @Command("gameserver@buyPhantom")
    public ByteResult buyPhantom(@RequestParam("teamId") final int teamId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("CONNECTOR", request);
        if (playerDto == null) {
            return null;
        }
        final KfPlayerInfo player = KfgzPlayerManager.getPlayerByCId(playerDto.getCompetitorId());
        if (player == null) {
            return null;
        }
        return this.getResult(this.kfgzScheduleService.buyPhantom(player, teamId), request);
    }
    
    @Command("gameserver@getCallGeneralInfo")
    public ByteResult getCallGeneralInfo(@RequestParam("teamId") final int teamId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("CONNECTOR", request);
        if (playerDto == null) {
            return null;
        }
        final KfPlayerInfo player = KfgzPlayerManager.getPlayerByCId(playerDto.getCompetitorId());
        if (player == null) {
            return null;
        }
        return this.getResult(this.kfgzScheduleService.getCallGeneralInfo(player, teamId), request);
    }
    
    @Command("gameserver@callGeneral")
    public ByteResult callGeneral(@RequestParam("teamId") final int teamId, @RequestParam("gIds") final String gIds, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("CONNECTOR", request);
        if (playerDto == null) {
            return null;
        }
        final KfPlayerInfo player = KfgzPlayerManager.getPlayerByCId(playerDto.getCompetitorId());
        if (player == null) {
            return null;
        }
        return this.getResult(this.kfgzScheduleService.callGeneral(player, teamId, gIds), request);
    }
    
    @Command("gameserver@getGzPlayerResult")
    public ByteResult getGzPlayerResult(final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("CONNECTOR", request);
        if (playerDto == null) {
            return null;
        }
        final KfPlayerInfo player = KfgzPlayerManager.getPlayerByCId(playerDto.getCompetitorId());
        if (player == null) {
            return null;
        }
        return this.getResult(this.kfgzScheduleService.getGzPlayerResult(player), request);
    }
    
    @Command("gameserver@setAutoAttack")
    public ByteResult setAutoAttack(final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("CONNECTOR", request);
        if (playerDto == null) {
            return null;
        }
        final KfPlayerInfo player = KfgzPlayerManager.getPlayerByCId(playerDto.getCompetitorId());
        if (player == null) {
            return null;
        }
        return this.getResult(this.kfgzScheduleService.setAutoAttack(player), request);
    }
    
    @Command("gameserver@leaveBattleTeam")
    public ByteResult leaveBattleTeam(@RequestParam("teamId") final int teamId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("CONNECTOR", request);
        if (playerDto == null) {
            return null;
        }
        final KfPlayerInfo player = KfgzPlayerManager.getPlayerByCId(playerDto.getCompetitorId());
        if (player == null) {
            return null;
        }
        return this.getResult(this.kfgzScheduleService.leaveBattleTeam(player, teamId), request);
    }
    
    @Command("gameserver@clearSoloCd")
    public ByteResult clearSoloCd(final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("CONNECTOR", request);
        if (playerDto == null) {
            return null;
        }
        final KfPlayerInfo player = KfgzPlayerManager.getPlayerByCId(playerDto.getCompetitorId());
        if (player == null) {
            return null;
        }
        return this.getResult(this.kfgzScheduleService.clearSoloCd(player), request);
    }
    
    @Command("gameserver@useOfficeToken")
    public ByteResult useOfficeToken(@RequestParam("teamId") final int teamId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("CONNECTOR", request);
        if (playerDto == null) {
            return null;
        }
        final KfPlayerInfo player = KfgzPlayerManager.getPlayerByCId(playerDto.getCompetitorId());
        if (player == null) {
            return null;
        }
        return this.getResult(this.kfgzScheduleService.useOfficeToken(player, teamId), request);
    }
    
    @Command("gameserver@getOTTeamInfo")
    public ByteResult getOfficeTokenTeamInfo(@RequestParam("teamId") final int teamId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("CONNECTOR", request);
        if (playerDto == null) {
            return null;
        }
        final KfPlayerInfo player = KfgzPlayerManager.getPlayerByCId(playerDto.getCompetitorId());
        if (player == null) {
            return null;
        }
        return this.getResult(this.kfgzScheduleService.getOfficeTokenTeamInfo(player, teamId), request);
    }
    
    @Command("gameserver@doRushInOTTeam")
    public ByteResult doRushInOfficeTokenTeam(@RequestParam("teamId") final int toTeamId, @RequestParam("gIds") final String gIds, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("CONNECTOR", request);
        if (playerDto == null) {
            return null;
        }
        final KfPlayerInfo player = KfgzPlayerManager.getPlayerByCId(playerDto.getCompetitorId());
        if (player == null) {
            return null;
        }
        return this.getResult(this.kfgzScheduleService.doRushInOfficeTokenTeam(player, toTeamId, gIds), request);
    }
    
    @Command("gameserver@getGroupTeamInfo")
    public ByteResult getGroupTeamInfo(final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("CONNECTOR", request);
        if (playerDto == null) {
            return null;
        }
        final KfPlayerInfo player = KfgzPlayerManager.getPlayerByCId(playerDto.getCompetitorId());
        if (player == null) {
            return null;
        }
        return this.getResult(this.kfgzGroupTeamService.getGroupTeamInfo(player), request);
    }
    
    @Command("gameserver@createGroupTeam")
    public ByteResult createGroupTeam(final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("CONNECTOR", request);
        if (playerDto == null) {
            return null;
        }
        final KfPlayerInfo player = KfgzPlayerManager.getPlayerByCId(playerDto.getCompetitorId());
        if (player == null) {
            return null;
        }
        return this.getResult(this.kfgzGroupTeamService.createGroupTeam(player), request);
    }
    
    @Command("gameserver@getAddGroupTeamInfo")
    public ByteResult getAddGroupTeamInfo(@RequestParam("teamId") final int teamId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("CONNECTOR", request);
        if (playerDto == null) {
            return null;
        }
        final KfPlayerInfo player = KfgzPlayerManager.getPlayerByCId(playerDto.getCompetitorId());
        if (player == null) {
            return null;
        }
        return this.getResult(this.kfgzGroupTeamService.getAddGroupTeamInfo(player, teamId), request);
    }
    
    @Command("gameserver@addToGroupTeam")
    public ByteResult addToGroupTeam(@RequestParam("teamId") final int teamId, @RequestParam("gIds") final String gIds, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("CONNECTOR", request);
        if (playerDto == null) {
            return null;
        }
        final KfPlayerInfo player = KfgzPlayerManager.getPlayerByCId(playerDto.getCompetitorId());
        if (player == null) {
            return null;
        }
        return this.getResult(this.kfgzGroupTeamService.addToGroupTeam(player, teamId, gIds), request);
    }
    
    @Command("gameserver@dismissGroupTeam")
    public ByteResult dismissGroupTeam(@RequestParam("teamId") final int teamId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("CONNECTOR", request);
        if (playerDto == null) {
            return null;
        }
        final KfPlayerInfo player = KfgzPlayerManager.getPlayerByCId(playerDto.getCompetitorId());
        if (player == null) {
            return null;
        }
        return this.getResult(this.kfgzGroupTeamService.dismissGroupTeam(player, teamId), request);
    }
    
    @Command("gameserver@leaveGroupTeam")
    public ByteResult leaveGroupTeam(@RequestParam("teamId") final int teamId, @RequestParam("gId") final int gId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("CONNECTOR", request);
        if (playerDto == null) {
            return null;
        }
        final KfPlayerInfo player = KfgzPlayerManager.getPlayerByCId(playerDto.getCompetitorId());
        if (player == null) {
            return null;
        }
        return this.getResult(this.kfgzGroupTeamService.leaveGroupTeam(player, teamId, gId), request);
    }
    
    @Command("gameserver@kickOutGroupTeam")
    public ByteResult kickOutGroupTeam(@RequestParam("teamId") final int teamId, @RequestParam("cId") final int toCId, @RequestParam("gId") final int toGId, @RequestParam("gId") final int gId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("CONNECTOR", request);
        if (playerDto == null) {
            return null;
        }
        final KfPlayerInfo player = KfgzPlayerManager.getPlayerByCId(playerDto.getCompetitorId());
        if (player == null) {
            return null;
        }
        return this.getResult(this.kfgzGroupTeamService.kickOutGroupTeam(player, teamId, toCId, toGId), request);
    }
    
    @Command("gameserver@doBattleGroupTeam")
    public ByteResult doBattleGroupTeam(@RequestParam("teamId") final int teamId, @RequestParam("toTeamId") final int toTeamId, @RequestParam("teamBatType") final int teamBatType, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("CONNECTOR", request);
        if (playerDto == null) {
            return null;
        }
        final KfPlayerInfo player = KfgzPlayerManager.getPlayerByCId(playerDto.getCompetitorId());
        if (player == null) {
            return null;
        }
        return this.getResult(this.kfgzGroupTeamService.doBattleGroupTeam(player, teamId, teamBatType, toTeamId), request);
    }
    
    @Command("gameserver@getGroupTeamBatCost")
    public ByteResult getGroupTeamBatCost(@RequestParam("teamId") final int teamId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("CONNECTOR", request);
        if (playerDto == null) {
            return null;
        }
        final KfPlayerInfo player = KfgzPlayerManager.getPlayerByCId(playerDto.getCompetitorId());
        if (player == null) {
            return null;
        }
        return this.getResult(this.kfgzGroupTeamService.getGroupTeamBatCost(player, teamId), request);
    }
    
    @Command("gameserver@groupTeamInspire")
    public ByteResult groupTeamInspire(@RequestParam("teamId") final int teamId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("CONNECTOR", request);
        if (playerDto == null) {
            return null;
        }
        final KfPlayerInfo player = KfgzPlayerManager.getPlayerByCId(playerDto.getCompetitorId());
        if (player == null) {
            return null;
        }
        return this.getResult(this.kfgzGroupTeamService.groupTeamInspire(player, teamId), request);
    }
    
    @Command("gameserver@groupTeamOrder")
    public ByteResult groupTeamOrder(@RequestParam("teamId") final int teamId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("CONNECTOR", request);
        if (playerDto == null) {
            return null;
        }
        final KfPlayerInfo player = KfgzPlayerManager.getPlayerByCId(playerDto.getCompetitorId());
        if (player == null) {
            return null;
        }
        return this.getResult(this.kfgzGroupTeamService.groupTeamOrder(player, teamId), request);
    }
    
    @Command("gameserver@closeGroupTeam")
    public ByteResult closeGroupTeam(final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("CONNECTOR", request);
        final KfPlayerInfo player = KfgzPlayerManager.getPlayerByCId(playerDto.getCompetitorId());
        if (player == null) {
            return null;
        }
        return this.getResult(this.kfgzGroupTeamService.closeGroupTeam(player), request);
    }
    
    @Command("gameserver@chooseNpcAI")
    public ByteResult chooseNpcAI(@RequestParam("choosenId") final int choosenId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("CONNECTOR", request);
        final KfPlayerInfo player = KfgzPlayerManager.getPlayerByCId(playerDto.getCompetitorId());
        if (player == null) {
            return null;
        }
        return this.getResult(this.kfgzScheduleService.chooseNpcAI(player, choosenId), request);
    }
    
    @Command("gameserver@useOrderToken")
    public ByteResult useOrderToken(@RequestParam("teamId") final int teamId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("CONNECTOR", request);
        final KfPlayerInfo player = KfgzPlayerManager.getPlayerByCId(playerDto.getCompetitorId());
        if (player == null) {
            return null;
        }
        return this.getResult(this.kfgzOrderService.useOrder(player, teamId), request);
    }
    
    @Command("gameserver@getOrderTeamInfo")
    public ByteResult getOrderTokenTeamInfo(@RequestParam("teamId") final int teamId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("CONNECTOR", request);
        final KfPlayerInfo player = KfgzPlayerManager.getPlayerByCId(playerDto.getCompetitorId());
        if (player == null) {
            return null;
        }
        return this.getResult(this.kfgzOrderService.getOrderTokenTeamInfo(player, teamId), request);
    }
    
    @Command("gameserver@doRushInOrderTeam")
    public ByteResult doRushInOrderTokenTeam(@RequestParam("teamId") final int toTeamId, @RequestParam("gIds") final String gIds, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("CONNECTOR", request);
        final KfPlayerInfo player = KfgzPlayerManager.getPlayerByCId(playerDto.getCompetitorId());
        if (player == null) {
            return null;
        }
        return this.getResult(this.kfgzOrderService.doRushInOrderTokenTeam(player, toTeamId, gIds), request);
    }
    
    @Command("gameserver@getBattleCampList")
    public ByteResult getBattleCampList(@RequestParam("teamId") final int teamId, @RequestParam("page") final int page, @RequestParam("side") final int side, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("CONNECTOR", request);
        if (playerDto == null) {
            return null;
        }
        final KfPlayerInfo player = KfgzPlayerManager.getPlayerByCId(playerDto.getCompetitorId());
        if (player == null) {
            return null;
        }
        return this.getResult(this.kfgzScheduleService.getBattleCampList(player, teamId, page, side), request);
    }
}
