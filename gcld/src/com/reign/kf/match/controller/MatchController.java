package com.reign.kf.match.controller;

import org.springframework.stereotype.*;
import com.reign.kf.match.service.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.kf.match.log.*;
import com.reign.kf.match.common.*;
import com.reign.kf.comm.protocol.*;
import com.reign.util.*;
import org.codehaus.jackson.*;
import java.io.*;
import java.util.*;
import com.reign.kf.match.model.*;
import com.reign.kf.comm.param.match.*;
import com.reign.kf.comm.entity.match.*;

@Component("matchController")
public class MatchController implements IMatchController
{
    private static final Logger log;
    @Autowired
    private IMatchService matchService;
    
    static {
        log = CommonLog.getLog(MatchController.class);
    }
    
    @Override
    public List<Response> handle(final RequestChunk chunk) {
        final List<Response> responseList = new ArrayList<Response>();
        final CommandWatch watch = new CommandWatch();
        for (final Request request : chunk.getRequestList()) {
            if (request.getCommand() == Command.GZ_ALIVE) {
                continue;
            }
            final StopWatch sw = StopWatch.run(new Runnable() {
                @Override
                public void run() {
                    final Response response = new Response();
                    response.setResponseId(request.getRequestId());
                    response.setCommand(request.getCommand());
                    try {
                        if (request.getCommand() == Command.QUERY_MATCHSTATE) {
                            MatchController.this.handleQueryMatchState(request, response);
                        }
                        else if (request.getCommand() == Command.SIGN) {
                            MatchController.this.handleSign(request, response, chunk.getMachineId());
                        }
                        else if (request.getCommand() == Command.QUERY_MATCHSCHEDULE) {
                            MatchController.this.handleQueryMatchSchedule(request, response, chunk.getMachineId());
                        }
                        else if (request.getCommand() == Command.INSPIRE) {
                            MatchController.this.handleInspire(request, response, chunk.getMachineId());
                        }
                        else if (request.getCommand() == Command.QUERY_MATCHRTINFO) {
                            MatchController.this.handleQueryMatchRTInfo(request, response);
                        }
                        else if (request.getCommand() == Command.QUERY_MATCHREPORT) {
                            MatchController.this.handleQueryMatchReport(request, response);
                        }
                        else if (request.getCommand() == Command.QUERY_TURNRANK) {
                            MatchController.this.handleQueryTurnRank(request, response);
                        }
                        else if (request.getCommand() == Command.QUERY_MATCHNUMSCHEDULE) {
                            MatchController.this.handleQueryMatchNumSchedule(request, response);
                        }
                        else if (request.getCommand() == Command.SYNC) {
                            MatchController.this.handleSync(request, response, chunk.getMachineId());
                        }
                        else if (request.getCommand() == Command.QUERY_MATCHRESULT) {
                            MatchController.this.handleQueryMatchResult(request, response, chunk.getMachineId());
                        }
                        responseList.add(response);
                    }
                    catch (Throwable t) {
                        try {
                            MatchController.log.error("handle request, param:" + Types.OBJECT_MAPPER.writeValueAsString(request), t);
                        }
                        catch (Exception ex) {}
                        response.setMessage((Object)null);
                        responseList.add(response);
                    }
                }
            });
            watch.addWatch(request.getCommand(), sw.getElapsedTime());
        }
        ThreadLocalFactory.setThreadLocalObj(watch);
        return responseList;
    }
    
    protected void handleQueryMatchResult(final Request request, final Response response, final String machineId) {
        final QueryMatchResultParam param = (QueryMatchResultParam)request.getMessage();
        final List<MatchResultEntity> resultList = this.matchService.getMatchResult(param, machineId);
        response.setMessage(resultList);
        response.setType(Types.id(Types.JAVATYPE_MATCHRESULTENTITYLIST));
    }
    
    protected void handleSync(final Request request, final Response response, final String machineId) throws JsonProcessingException, IOException {
        final SignAndSyncParam param = (SignAndSyncParam)request.getMessage();
        param.setCampList();
        final SignEntity entity = this.matchService.sync(param, machineId);
        response.setMessage(entity);
    }
    
    protected void handleQueryMatchNumSchedule(final Request request, final Response response) {
        final QueryMatchNumScheduleParam param = (QueryMatchNumScheduleParam)request.getMessage();
        MatchController.log.info("\u6309\u7167\u6bd4\u8d5b\u573a\u6b21\u67e5\u8be2\u6bd4\u8d5b\u5b89\u6392\u53c2\u6570\uff1a[matchTag:" + param.getMatchTag() + " matchNum" + param.getMatchNum() + " turn" + param.getTurn() + " session" + param.getSession() + " nowTime:" + new Date() + "]");
        final MatchScheduleEntity entity = this.matchService.getMatchNumSchedule(param);
        response.setMessage(entity);
    }
    
    protected void handleQueryTurnRank(final Request request, final Response response) {
        final QueryTurnRankParam param = (QueryTurnRankParam)request.getMessage();
        final List<MatchRankEntity> resultList = this.matchService.getMatchTurnRank(param);
        response.setMessage(resultList);
        response.setType(Types.id(Types.JAVATYPE_MATCHRANKENTITYLIST));
    }
    
    protected void handleQueryMatchReport(final Request request, final Response response) {
        final QueryMatchReportParam param = (QueryMatchReportParam)request.getMessage();
        MatchController.log.info("\u67e5\u8be2\u6bd4\u8d5b\u6218\u62a5\u53c2\u6570\uff1a[id:" + param.getMatchId() + " matchtag:" + param.getMatchTag() + " session:" + param.getSession() + " nowTime:" + new Date() + "]");
        final MatchReportEntity entity = this.matchService.getMatchReport(param);
        if (entity != null) {
            entity.setNextCd();
        }
        response.setMessage(entity);
    }
    
    private void handleQueryMatchRTInfo(final Request request, final Response response) {
        final QueryMatchRTInfoParam param = (QueryMatchRTInfoParam)request.getMessage();
        MatchController.log.info("\u67e5\u8be2\u6bd4\u8d5b\u8be6\u7ec6\u60c5\u51b5\u53c2\u6570\uff1a[id:" + param.getMatchId() + " matchTag:" + param.getMatchTag() + " version:" + param.getVersion() + " nowTime:" + new Date() + "]");
        final MatchRTInfoEntity entity = this.matchService.getMatchRTInfo(param);
        entity.setMatchCd();
        response.setMessage(entity);
    }
    
    private void handleQueryMatchSchedule(final Request request, final Response response, final String machineId) {
        final QueryMatchScheduleParam param = (QueryMatchScheduleParam)request.getMessage();
        MatchController.log.info("\u67e5\u8be2\u6bd4\u8d5b\u5b89\u6392\u53c2\u6570\uff1a[matchTag:" + param.getMatchTag() + " turn" + param.getTurn() + " nowTime:" + new Date() + "]");
        final List<MatchScheduleEntity> mseList = this.matchService.getMatchSchedule(param, machineId);
        response.setMessage(mseList);
        response.setType(Types.id(Types.JAVATYPE_MATCHSCHEDULEENTIRYLIST));
    }
    
    private void handleSign(final Request request, final Response response, final String machineId) throws JsonProcessingException, IOException {
        final SignAndSyncParam param = (SignAndSyncParam)request.getMessage();
        param.setCampList();
        final SignEntity entity = this.matchService.sign(param, machineId);
        response.setMessage(entity);
    }
    
    private void handleQueryMatchState(final Request request, final Response response) {
        final QueryMatchParam param = (QueryMatchParam)request.getMessage();
        final Match match = MatchManager.getInstance().getMatch(param.getMatchTag());
        if (match != null) {
            final MatchStateEntity entity = new MatchStateEntity();
            entity.setMatchTag(match.getMatchTag());
            entity.setState(match.getState());
            entity.setCd(match.getCD());
            response.setMessage(entity);
        }
    }
    
    private void handleInspire(final Request request, final Response response, final String machineId) throws JsonProcessingException, IOException {
        final InspireParam param = (InspireParam)request.getMessage();
        final InspireEntity entity = this.matchService.inspire(param, machineId);
        response.setMessage(entity);
    }
}
