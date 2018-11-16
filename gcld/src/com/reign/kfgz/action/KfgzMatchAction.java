package com.reign.kfgz.action;

import com.reign.kfgz.control.*;
import org.springframework.beans.factory.annotation.*;
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
public class KfgzMatchAction
{
    private static final Logger log;
    private static final Logger interfaceLog;
    @Autowired
    private IKfgzMatchControl kfgzMatchControl;
    
    static {
        log = CommonLog.getLog(KfgzMatchAction.class);
        interfaceLog = new InterfaceLogger();
    }
    
    @Command("match@kfgzgateway")
    public MatchResult handleGameServer(final Request request) {
        final StopWatch sw = StopWatch.begin();
        RequestChunk chunk = null;
        List<Response> responseList = null;
        try {
            chunk = MatchUtil.resolveStream(request, RequestChunk.class, false);
            responseList = this.kfgzMatchControl.handle(chunk);
            if (responseList.size() <= 0) {
                return null;
            }
            return new MatchResult(responseList);
        }
        catch (Exception e) {
            KfgzMatchAction.log.error("handle request error", e);
        }
        finally {
            sw.stop();
            KfgzMatchAction.interfaceLog.isDebugEnabled();
        }
        return new MatchResult(null);
    }
}
