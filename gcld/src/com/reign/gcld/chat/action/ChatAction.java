package com.reign.gcld.chat.action;

import com.reign.gcld.common.web.*;
import com.reign.gcld.chat.service.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.player.dto.*;
import com.reign.framework.netty.servlet.*;
import com.reign.framework.netty.mvc.result.*;
import com.reign.gcld.common.*;
import com.reign.framework.json.*;
import com.reign.framework.netty.mvc.annotation.*;
import com.reign.gcld.user.dto.*;
import java.util.*;

public class ChatAction extends BaseAction
{
    private static final long serialVersionUID = 973256186245556095L;
    @Autowired
    private IChatService chatService;
    
    @Command("chat@send")
    public ByteResult send(@RequestParam("type") final String type, @RequestParam("to") final String to, @RequestParam("msg") final String msg, @SessionParam("PLAYER") final PlayerDto playerDto, final Request request) {
        if (playerDto == null) {
            return this.getResult(JsonBuilder.getJson(State.FAIL, LocalMessages.T_USER_10004), request);
        }
        return this.getResult(this.chatService.send(type, playerDto, to, msg), request);
    }
    
    @Command("chat@silence")
    public ByteResult silence(@SessionParam("PLAYER") final PlayerDto playerDto, @SessionParam("user") final UserDto userDto, final Request request) {
        this.chatService.keepSilence(userDto.userId, userDto.yx, playerDto.playerId, "just silence", new Date(System.currentTimeMillis() + 3600000L));
        return this.getResult(JsonBuilder.getJson(State.SUCCESS, ""), request);
    }
    
    @Command("chat@getBlackList")
    public ByteResult getBlackList(@SessionParam("PLAYER") final PlayerDto playerDto, final Request request) {
        if (playerDto == null) {
            return this.getResult(JsonBuilder.getJson(State.FAIL, LocalMessages.T_USER_10004), request);
        }
        return this.getResult(this.chatService.getBlackList(playerDto), request);
    }
    
    @Command("chat@addBlackName")
    public ByteResult addBlackName(@RequestParam("blackName") final String blackName, @SessionParam("PLAYER") final PlayerDto playerDto, final Request request) {
        if (playerDto == null) {
            return this.getResult(JsonBuilder.getJson(State.FAIL, LocalMessages.T_USER_10004), request);
        }
        return this.getResult(this.chatService.addBlackName(playerDto, blackName), request);
    }
    
    @Command("chat@removeBlackName")
    public ByteResult removeBlackName(@RequestParam("vId") final int vId, @SessionParam("PLAYER") final PlayerDto playerDto, final Request request) {
        if (playerDto == null) {
            return this.getResult(JsonBuilder.getJson(State.FAIL, LocalMessages.T_USER_10004), request);
        }
        return this.getResult(this.chatService.removeBlackName(playerDto, vId), request);
    }
    
    @Command("chat@speak")
    public ByteResult speak(@RequestParam("id") final String id, @RequestParam("time") final int time, @RequestParam("type") final String type, @RequestParam("to") final String to, @SessionParam("PLAYER") final PlayerDto playerDto, final Request request) {
        if (playerDto == null) {
            return this.getResult(JsonBuilder.getJson(State.FAIL, LocalMessages.T_USER_10004), request);
        }
        return this.getResult(this.chatService.speak(id, time, type, to, playerDto), request);
    }
}
