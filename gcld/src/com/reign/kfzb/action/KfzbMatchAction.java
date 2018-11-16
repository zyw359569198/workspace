package com.reign.kfzb.action;

import com.reign.kfzb.control.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.kf.match.action.*;
import com.reign.kf.match.log.*;
import com.reign.framework.netty.servlet.*;
import com.reign.kf.match.common.web.*;
import com.reign.util.*;
import com.reign.kf.match.common.util.*;
import java.util.*;
import com.reign.kf.comm.protocol.*;
import com.reign.framework.netty.mvc.annotation.*;

@Action
@Views({ @View(name = "matchview", type = MatchView.class, compress = "false") })
public class KfzbMatchAction
{
    private static final Logger log;
    private static final Logger interfaceLog;
    @Autowired
    private IKfzbMatchControl kfzbMatchControl;
    
    static {
        log = CommonLog.getLog(MatchAction.class);
        interfaceLog = new InterfaceLogger();
    }
    
    @Command("match@kfzbgateway")
    public MatchResult handleGameServer(final Request request) {
        final StopWatch sw = StopWatch.begin();
        RequestChunk chunk = null;
        List<Response> responseList = null;
        try {
            chunk = MatchUtil.resolveStream(request, RequestChunk.class, false);
            responseList = this.kfzbMatchControl.handle(chunk);
            if (responseList.size() <= 0) {
                return null;
            }
            return new MatchResult(responseList);
        }
        catch (Exception e) {
            KfzbMatchAction.log.error("handle request error", e);
        }
        finally {
            sw.stop();
            KfzbMatchAction.interfaceLog.isDebugEnabled();
        }
        return new MatchResult(null);
    }
}
