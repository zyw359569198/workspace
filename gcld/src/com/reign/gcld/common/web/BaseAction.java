package com.reign.gcld.common.web;

import java.io.*;
import com.reign.framework.netty.mvc.annotation.*;
import com.reign.framework.netty.mvc.view.*;
import com.reign.gcld.common.event.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.framework.netty.servlet.*;
import com.reign.framework.netty.mvc.result.*;
import com.reign.gcld.player.dto.*;
import com.reign.framework.json.*;
import com.reign.gcld.common.*;

@Action
@Views({ @View(name = "byte", type = ByteView.class) })
public class BaseAction implements Serializable
{
    private static final long serialVersionUID = -1834822056752116266L;
    @Autowired
    private EventHandler eventHandler;
    
    public ByteResult getResult(final byte[] result, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto != null) {
            this.eventHandler.handle(playerDto.playerId, playerDto, PushCommand.PUSH_UPDATE);
        }
        return new ByteResult(JsonBuilder.getJson(result));
    }
    
    public ByteResult getNoneExtraResult(final byte[] result) {
        return new ByteResult(JsonBuilder.getJson(result));
    }
    
    public Object getFromSession(final String key, final Request request) {
        return request.getSession().getAttribute(key);
    }
    
    public void putToSession(final String key, final Object obj, final Request request) {
        request.getSession().setAttribute(key, obj);
    }
    
    public void clearSession(final Request request) {
        Players.clearSession(request);
    }
    
    public void clearSession(final Request request, final String key) {
        request.getSession().removeAttribute(key);
    }
}
