package com.reign.gcld.log.action;

import com.reign.gcld.common.web.*;
import com.reign.gcld.common.log.*;
import com.reign.framework.netty.servlet.*;
import com.reign.framework.netty.mvc.result.*;
import com.reign.gcld.player.dto.*;
import com.reign.framework.json.*;
import com.reign.framework.netty.mvc.annotation.*;

public class LogAction extends BaseAction
{
    private static final long serialVersionUID = -7239417980895607958L;
    private static final Logger errorLogger;
    
    static {
        errorLogger = CommonLog.getLog(LogAction.class);
    }
    
    @Command("log@fileNotFound")
    public ByteResult fileNotFound(@RequestParam("reason") final String reason, final Request request) {
        final PlayerDto dto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (dto == null) {
            return null;
        }
        LogAction.errorLogger.info("file_not_found#reason:#" + reason);
        return this.getResult(JsonBuilder.getJson(State.SUCCESS, ""), request);
    }
}
