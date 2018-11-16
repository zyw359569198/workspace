package com.reign.gcld.task.action;

import com.reign.gcld.common.web.*;
import com.reign.gcld.task.service.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.player.dto.*;
import com.reign.framework.netty.servlet.*;
import com.reign.framework.netty.mvc.result.*;
import com.reign.gcld.common.*;
import com.reign.framework.json.*;
import com.reign.framework.netty.mvc.annotation.*;

public class TaskAction extends BaseAction
{
    private static final long serialVersionUID = -1542824424491784427L;
    @Autowired
    private IPlayerTaskService playerTaskService;
    
    @Command("task@getCurTaskInfo")
    public ByteResult createBattle(@SessionParam("PLAYER") final PlayerDto playerDto, final Request request) {
        if (playerDto == null) {
            return this.getResult(JsonBuilder.getJson(State.FAIL, LocalMessages.T_USER_10004), request);
        }
        final byte[] result = this.playerTaskService.getCurTaskInfo(playerDto);
        return this.getResult(result, request);
    }
    
    @Command("task@finishTask")
    public ByteResult finishTask(@RequestParam("type") final int type, @RequestParam("group") final int group, @RequestParam("index") final int index, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return this.getResult(JsonBuilder.getJson(State.FAIL, LocalMessages.T_USER_10004), request);
        }
        final byte[] result = this.playerTaskService.finishCurTask(playerDto, type, group, index);
        return this.getResult(result, request);
    }
    
    @Command("task@getDailyBattleTaskInfo")
    public ByteResult getDailyBattleTaskInfo(final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return this.getResult(JsonBuilder.getJson(State.FAIL, LocalMessages.T_USER_10004), request);
        }
        final byte[] result = this.playerTaskService.getDailyBattleTaskInfo(playerDto);
        return this.getResult(result, request);
    }
    
    @Command("task@receiveBattleTaskReward")
    public ByteResult receiveDailyBattleTaskReward(final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return this.getResult(JsonBuilder.getJson(State.FAIL, LocalMessages.T_USER_10004), request);
        }
        final byte[] result = this.playerTaskService.receiveDailyBattleTaskReward(playerDto);
        return this.getResult(result, request);
    }
    
    @Command("task@guideUpdate")
    public ByteResult guideUpdate(@RequestParam("guideId") final int guideId, @SessionParam("PLAYER") final PlayerDto playerDto, final Request request) {
        if (playerDto == null) {
            return this.getResult(JsonBuilder.getJson(State.FAIL, LocalMessages.T_USER_10004), request);
        }
        final byte[] result = this.playerTaskService.guideUpdate(playerDto, guideId);
        return this.getResult(result, request);
    }
    
    @Command("task@getfmTaskInfo")
    public ByteResult getFreshManTaskInfo(@SessionParam("PLAYER") final PlayerDto playerDto, final Request request) {
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.playerTaskService.getFreshManTaskInfo(playerDto), request);
    }
    
    @Command("task@getfmTaskReward")
    public ByteResult getfmTaskReward(@SessionParam("PLAYER") final PlayerDto playerDto, @RequestParam("armiesId") final int armiesId, final Request request) {
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.playerTaskService.getfmTaskReward(playerDto, armiesId), request);
    }
}
