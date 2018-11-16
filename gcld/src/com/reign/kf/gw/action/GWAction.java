package com.reign.kf.gw.action;

import com.reign.framework.netty.mvc.annotation.*;
import com.reign.kf.gw.controller.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.kf.gw.common.log.*;
import com.reign.framework.netty.servlet.*;
import com.reign.kf.gw.common.web.*;
import com.reign.util.*;
import com.reign.framework.netty.util.*;
import com.reign.kf.gw.common.util.*;
import com.reign.kf.comm.protocol.*;

@Action
@Views({ @View(name = "gwview", type = GWView.class) })
public class GWAction
{
    private static final Logger log;
    protected static final InterfaceLogger interfaceLog;
    @Autowired
    private IGWController gwController;
    
    static {
        log = CommonLog.getLog(GWAction.class);
        interfaceLog = new InterfaceLogger();
    }
    
    @com.reign.framework.netty.mvc.annotation.Command("gw@gameserver")
    public GWResult handleGameServer(final Request request) {
        final StopWatch sw = StopWatch.begin();
        Response response = null;
        com.reign.kf.comm.protocol.Request re = null;
        try {
            re = MatchUtil.resolveStream(request, com.reign.kf.comm.protocol.Request.class, WrapperUtil.compress);
            if (re.getCommand() == Command.GZ_ALIVE) {
                return null;
            }
            response = this.gwController.handle(request.getChannel(), re, 1);
            return new GWResult(response);
        }
        catch (Exception e) {
            e.printStackTrace();
            GWAction.log.error("handle game server request error", e);
        }
        finally {
            sw.stop();
        }
        return null;
    }
    
    @com.reign.framework.netty.mvc.annotation.Command("gw@matchserver")
    public GWResult handleMatchServer(final Request request) {
        final StopWatch sw = StopWatch.begin();
        Response response = null;
        com.reign.kf.comm.protocol.Request re = null;
        try {
            re = MatchUtil.resolveStream(request, com.reign.kf.comm.protocol.Request.class, WrapperUtil.compress);
            if (re.getCommand() == Command.GZ_ALIVE) {
                return null;
            }
            response = this.gwController.handle(request.getChannel(), re, 2);
            return new GWResult(response);
        }
        catch (Exception e) {
            GWAction.log.error("handle game server request error", e);
        }
        finally {
            sw.stop();
        }
        return null;
    }
}
