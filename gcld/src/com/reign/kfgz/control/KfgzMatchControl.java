package com.reign.kfgz.control;

import org.springframework.stereotype.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.kfgz.resource.*;
import com.reign.kfgz.service.*;
import com.reign.kf.match.log.*;
import com.reign.kf.comm.protocol.*;
import java.util.*;
import com.reign.kfgz.dto.request.*;
import com.reign.kfgz.dto.response.*;

@Component
public class KfgzMatchControl implements IKfgzMatchControl
{
    private static final Logger log;
    @Autowired
    IKfgzMatchService kfgzMatchService;
    @Autowired
    private IKfgzResourceService kfgzResourceService;
    @Autowired
    private IKfgzScheduleService kfgzScheduleService;
    
    static {
        log = CommonLog.getLog(KfgzMatchControl.class);
    }
    
    @Override
    public List<Response> handle(final RequestChunk chunk) {
        final List<Response> responseList = new ArrayList<Response>();
        for (final Request request : chunk.getRequestList()) {
            if (request.getCommand() == Command.GZ_ALIVE) {
                continue;
            }
            final Response response = new Response();
            response.setResponseId(request.getRequestId());
            response.setCommand(request.getCommand());
            try {
                if (request.getCommand() == Command.KFGZ_SIGN_FROM_GAMESERVER) {
                    this.handleSignUp(request, response, chunk.getMachineId());
                }
                else if (request.getCommand() == Command.KFGZ_SYNDATA_FROM_GAMESERVER) {
                    this.handleSyncData(request, response, chunk.getMachineId());
                }
                else if (request.getCommand() == Command.KFGZ_GET_MATCH_GZINFO_FROM_GAMESERVER) {
                    this.handleGetGzInfo(request, response, chunk.getMachineId());
                }
                else if (request.getCommand() == Command.KFGZ_GET_MATCH_GZRESULT_FROM_GAMESERVER) {
                    this.handleGetGzResult(request, response, chunk.getMachineId());
                }
                responseList.add(response);
            }
            catch (Throwable t) {
                try {
                    KfgzMatchControl.log.error("handle request, param:" + Types.OBJECT_MAPPER.writeValueAsString(request), t);
                }
                catch (Exception ex) {}
                response.setMessage((Object)null);
                responseList.add(response);
            }
        }
        return responseList;
    }
    
    private void handleGetGzResult(final Request request, final Response response, final String serverKey) {
        final KfgzNationResKey nKey = (KfgzNationResKey)request.getMessage();
        final KfgzNationResInfo res = this.kfgzMatchService.getGzResultInfo(nKey, serverKey);
        response.setMessage(res);
    }
    
    private void handleGetGzInfo(final Request request, final Response response, final String serverKey) {
        final KfgzGzKey gzKey = (KfgzGzKey)request.getMessage();
        final KfgzBaseInfoRes res = this.kfgzMatchService.getGzBaseInfo(gzKey, serverKey);
        response.setMessage(res);
    }
    
    private void handleSignUp(final Request request, final Response response, final String machineId) {
        final KfgzSignResult res = this.kfgzMatchService.doSignUp((KfgzSignInfoParam)request.getMessage(), machineId);
        response.setMessage(res);
    }
    
    private void handleSyncData(final Request request, final Response response, final String machineId) {
        final KfgzSyncDataResult res = this.kfgzResourceService.syncResource((KfgzSyncDataParam)request.getMessage());
        response.setMessage(res);
    }
}
