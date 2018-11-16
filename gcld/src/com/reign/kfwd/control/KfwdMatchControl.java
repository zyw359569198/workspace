package com.reign.kfwd.control;

import org.springframework.stereotype.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.kfwd.service.*;
import com.reign.kf.match.controller.*;
import com.reign.kf.match.log.*;
import com.reign.kf.match.common.*;
import com.reign.kf.comm.protocol.*;
import java.util.*;
import com.reign.kfwd.notice.*;
import com.reign.kf.comm.entity.kfwd.request.*;
import com.reign.kf.comm.entity.kfwd.response.*;

@Component
public class KfwdMatchControl implements IKfwdMatchControl
{
    private static final Logger log;
    @Autowired
    private IKfwdMatchService kfwdMatchService;
    @Autowired
    private IKfwdScheduleService kfwdScheduleService;
    
    static {
        log = CommonLog.getLog(MatchController.class);
    }
    
    @Override
    public List<Response> handle(final RequestChunk chunk) {
        final List<Response> responseList = new ArrayList<Response>();
        final CommandWatch watch = new CommandWatch();
        final boolean returnMoreInfo = true;
        for (final Request request : chunk.getRequestList()) {
            if (request.getCommand() == Command.GZ_ALIVE) {
                continue;
            }
            final Response response = new Response();
            response.setResponseId(request.getRequestId());
            response.setCommand(request.getCommand());
            try {
                if (request.getCommand() == Command.KFWD_SIGN_FROM_GAMESERVER) {
                    this.handleSignUp(request, response, chunk.getMachineId());
                }
                else if (request.getCommand() == Command.KFWD_SYNDATA_FROM_GAMESERVER) {
                    this.handleSynData(request, response, chunk.getMachineId());
                }
                else if (request.getCommand() == Command.KFWD_RT_MATCH) {
                    this.handleRTMatchInfo(request, response, chunk.getMachineId());
                }
                else if (request.getCommand() == Command.KFWD_RT_MATCH_DISPLAY) {
                    this.handleRTMatchDisplayerInfo(request, response, chunk.getMachineId());
                }
                else if (request.getCommand() == Command.KFWD_RT_RANKING_LIST) {
                    this.handleRTRankingMapInfo(request, response, chunk.getMachineId());
                }
                else if (request.getCommand() == Command.KFWD_STATE) {
                    response.setMessage(this.kfwdScheduleService.getWdState());
                    final String serverKey = chunk.getMachineId();
                    if (serverKey != null) {
                        this.doAddTicketRewardInfo(responseList, serverKey);
                        this.doAddDayBattleEnd(responseList, serverKey);
                    }
                }
                else if (request.getCommand() == Command.KFWD_DOUBLEREWARD) {
                    this.handleDoubleReward(request, response, chunk.getMachineId());
                }
                else if (request.getCommand() == Command.KFWD_REWARDTICKETINFO) {
                    this.handleTicketRewardInfo(request, response, chunk.getMachineId());
                }
                responseList.add(response);
            }
            catch (Throwable t) {
                try {
                    KfwdMatchControl.log.error("handle request, param:" + Types.OBJECT_MAPPER.writeValueAsString(request), t);
                }
                catch (Exception ex) {}
                response.setMessage((Object)null);
                responseList.add(response);
            }
        }
        return responseList;
    }
    
    private void doAddDayBattleEnd(final List<Response> responseList, final String serverKey) {
        KfwdDayBattleEndNoticeInfo.addTicketRewardInfo(responseList, serverKey);
    }
    
    private void doAddTicketRewardInfo(final List<Response> responseList, final String serverKey) {
        KfwdTicketRewardNoticeInfo.addTicketRewardInfo(responseList, serverKey);
    }
    
    protected void handleTicketRewardInfo(final Request request, final Response response, final String machineId) {
        final KfwdPlayerKey key = (KfwdPlayerKey)request.getMessage();
        final KfwdTicketResultInfo res = this.kfwdScheduleService.doGetTicketRewardInfo(key);
        response.setMessage(res);
    }
    
    protected void handleDoubleReward(final Request request, final Response response, final String machineId) {
        final KfwdDoubleRewardKey key = (KfwdDoubleRewardKey)request.getMessage();
        final KfwdDoubleRewardResult res = this.kfwdScheduleService.doDoubleReward(key);
        response.setMessage(res);
    }
    
    protected void handleRTRankingMapInfo(final Request request, final Response response, final String machineId) {
        final KfwdRTRankingListKey key = (KfwdRTRankingListKey)request.getMessage();
        final KfwdRankingListInfo info = this.kfwdScheduleService.getRTRankingList(key);
        response.setMessage(info);
    }
    
    protected void handleRTMatchDisplayerInfo(final Request request, final Response response, final String machineId) {
        final KfwdPlayerKey key = (KfwdPlayerKey)request.getMessage();
        final KfwdRTDisPlayInfo rtdis = this.kfwdScheduleService.getRTDisPlayerInfo(key);
        response.setMessage(rtdis);
    }
    
    private void handleSignUp(final Request request, final Response response, final String gameServer) {
        final KfwdSignInfoParam param = (KfwdSignInfoParam)request.getMessage();
        final KfwdSignResult entity = this.kfwdScheduleService.processNationRegist(gameServer, param, true);
        response.setMessage(entity);
    }
    
    private void handleSynData(final Request request, final Response response, final String gameServer) {
        final KfwdSignInfoParam param = (KfwdSignInfoParam)request.getMessage();
        final KfwdSignResult entity = this.kfwdScheduleService.processNationRegist(gameServer, param, false);
        response.setMessage(entity);
    }
    
    private void handleRTMatchInfo(final Request request, final Response response, final String machineId) {
        final KfwdPlayerKey pKey = (KfwdPlayerKey)request.getMessage();
        final KfwdRTMatchInfo rtInfo = this.kfwdScheduleService.getRTMatchInfo(pKey);
        response.setMessage(rtInfo);
    }
}
