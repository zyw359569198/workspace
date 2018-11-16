package com.reign.gcld.team.action;

import com.reign.gcld.common.web.*;
import com.reign.gcld.team.service.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.framework.netty.servlet.*;
import com.reign.framework.netty.mvc.result.*;
import com.reign.gcld.player.dto.*;
import com.reign.framework.netty.mvc.annotation.*;

public class TeamAction extends BaseAction
{
    private static final long serialVersionUID = 1L;
    @Autowired
    private ITeamService teamService;
    
    @Command("team@teamCreate")
    public ByteResult teamCreate(@RequestParam("teamType") final int teamType, @RequestParam("teamName") final String teamName, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.teamService.createTeam(playerDto, teamType, teamName), request);
    }
    
    @Command("team@getTeamInfo")
    public ByteResult getTeamInfo(final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.teamService.getTeamInfo(playerDto), request);
    }
    
    @Command("team@closeTeamInfo")
    public ByteResult closeTeamInfo(final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.teamService.closeTeamInfo(playerDto), request);
    }
    
    @Command("team@getGeneralInfo")
    public ByteResult getGeneralInfo(@RequestParam("teamId") final String teamId, @RequestParam("teamType") final int teamType, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.teamService.getGeneralInfo(playerDto, teamId, teamType), request);
    }
    
    @Command("team@joinTeam")
    public ByteResult joinTeam(@RequestParam("teamId") final String teamId, @RequestParam("gIds") final String gIds, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.teamService.joinTeam(playerDto, teamId, gIds), request);
    }
    
    @Command("team@kickOutTeam")
    public ByteResult kickOutTeam(@RequestParam("teamId") final String teamId, @RequestParam("kickPid") final int kickPid, @RequestParam("kickGid") final int kickGid, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.teamService.kickOutTeam(playerDto, teamId, kickPid, kickGid), request);
    }
    
    @Command("team@dismissTeam")
    public ByteResult dismissTeam(@RequestParam("teamId") final String teamId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.teamService.dismissTeam(playerDto, teamId), request);
    }
    
    @Command("team@leaveTeam")
    public ByteResult leaveTeam(@RequestParam("teamId") final String teamId, @RequestParam("generalId") final int generalId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.teamService.leaveTeam(playerDto, teamId, generalId), request);
    }
    
    @Command("team@teamBattle")
    public ByteResult teamBattle(@RequestParam("battleId") final String battleId, @RequestParam("curNum") final int curNum, @RequestParam("type") final int type, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.teamService.teamBattle(playerDto, battleId, curNum, type), request);
    }
    
    @Command("team@getBatCost")
    public ByteResult getBatCost(@RequestParam("teamType") final int teamType, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.teamService.getBatCost(playerDto, teamType), request);
    }
    
    @Command("team@teamInspire")
    public ByteResult teamInspire(@RequestParam("teamId") final String teamId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.teamService.teamInspire(playerDto, teamId), request);
    }
    
    @Command("team@teamOrder")
    public ByteResult teamOrder(@RequestParam("teamId") final String teamId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.teamService.teamOrder(playerDto, teamId), request);
    }
}
