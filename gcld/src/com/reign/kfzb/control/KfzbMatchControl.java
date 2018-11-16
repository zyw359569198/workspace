package com.reign.kfzb.control;

import org.springframework.stereotype.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.kfzb.service.*;
import com.reign.kf.match.controller.*;
import com.reign.kf.match.log.*;
import com.reign.kf.match.common.*;
import com.reign.kf.comm.protocol.*;
import java.util.*;
import com.reign.kfzb.dto.request.*;
import com.reign.kfzb.dto.response.*;

@Component
public class KfzbMatchControl implements IKfzbMatchControl
{
    private static final Logger log;
    @Autowired
    private IKfzbMatchService kfzbMatchService;
    @Autowired
    private IKfzbScheduleService kfzbScheduleService;
    
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
                if (request.getCommand() == Command.KFZB_SIGN_FROM_GAMESERVER) {
                    this.handleSignUp(request, response, chunk.getMachineId());
                }
                else if (request.getCommand() == Command.KFZB_SYNDATA_FROM_GAMESERVER) {
                    this.handleSynData(request, response, chunk.getMachineId());
                }
                else if (request.getCommand() == Command.KFZB_STATE) {
                    response.setMessage(this.kfzbScheduleService.getWdState());
                    final String serverKey = chunk.getMachineId();
                    if (serverKey != null) {
                        this.doAddTicketRewardInfo(responseList, serverKey);
                    }
                }
                else if (request.getCommand() == Command.KFZB_GET_PHASE1_REWARD_INFO) {
                    final List<Integer> cIdList = ((KfzbPlayerListKey)request.getMessage()).getcIdList();
                    final KfzbPhase1RewardInfoList rewardInfo = this.kfzbScheduleService.getKfzbRewardListInfo(cIdList);
                    response.setMessage(rewardInfo);
                }
                else if (request.getCommand() == Command.KFZB_RT_SUPPORT) {
                    final KfzbRTSupport rtSupport = (KfzbRTSupport)request.getMessage();
                    this.kfzbScheduleService.processRTSupport(rtSupport);
                    response.setMessage(1);
                }
                else if (request.getCommand() == Command.KFZB_GET_PHASE2_MATCH_DETAIL_INFO) {
                    final KfzbPhase2MatchKey matchKey = (KfzbPhase2MatchKey)request.getMessage();
                    final KfzbBattleReport battleReport = this.kfzbScheduleService.getPhase2MatchDetail(matchKey);
                    response.setMessage(battleReport);
                }
                else if (request.getCommand() == Command.KFZB_PLAYER_GID) {
                    final List<Integer> cIdList = ((KfzbPlayerListKey)request.getMessage()).getcIdList();
                    final KfzbPlayerGroupInfo info = this.kfzbScheduleService.getPlayerGroupInfo(cIdList);
                    response.setMessage(info);
                }
                responseList.add(response);
            }
            catch (Throwable t) {
                try {
                    KfzbMatchControl.log.error("handle request, param:" + Types.OBJECT_MAPPER.writeValueAsString(request), t);
                }
                catch (Exception ex) {}
                response.setMessage((Object)null);
                responseList.add(response);
            }
        }
        return responseList;
    }
    
    private void doAddTicketRewardInfo(final List<Response> responseList, final String serverKey) {
    }
    
    private void handleSignUp(final Request request, final Response response, final String gameServer) {
        final KfzbSignInfo signInfo = (KfzbSignInfo)request.getMessage();
        final KfzbSignResult res = this.kfzbScheduleService.processSynPlayerData(gameServer, signInfo, true);
        response.setMessage(res);
    }
    
    private void handleSynData(final Request request, final Response response, final String gameServer) {
        final KfzbSignInfo signInfo = (KfzbSignInfo)request.getMessage();
        final KfzbSignResult res = this.kfzbScheduleService.processSynPlayerData(gameServer, signInfo, false);
        response.setMessage(res);
    }
    
    private void handleRTMatchInfo(final Request request, final Response response, final String machineId) {
        final KfzbPlayerKey pKey = (KfzbPlayerKey)request.getMessage();
        final KfzbRTMatchInfo rtInfo = this.kfzbScheduleService.getRTMatchInfo(pKey, machineId);
        response.setMessage(rtInfo);
    }
}
