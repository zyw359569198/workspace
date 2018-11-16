package com.reign.kfwd.service;

import org.springframework.beans.factory.*;
import org.springframework.stereotype.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.kf.comm.transfer.oio.*;
import com.reign.kfwd.cache.*;
import org.apache.commons.logging.*;
import com.reign.kf.comm.entity.kfwd.response.*;
import java.io.*;
import com.reign.kf.comm.entity.*;
import com.reign.kf.comm.protocol.*;
import com.reign.kf.match.common.*;
import com.reign.kfwd.conf.*;
import com.reign.kf.comm.transfer.*;

@Component
public class KfwdSeasonService implements IKfwdSeasonService, Runnable, InitializingBean, ResponseHandler
{
    private static Log logger;
    @Autowired
    IKfwdScheduleService kfwdScheduleService;
    private KfConnection connection;
    @Autowired
    IKfwdCacheManager kfwdCacheManager;
    public volatile KfwdSeasonInfo lastSeasonInfo;
    public volatile KfwdSeasonInfo curSeasonInfo;
    private static Log seasonInfoLog;
    
    static {
        KfwdSeasonService.logger = LogFactory.getLog("com.reign.KfwdMatchCommLogger");
        KfwdSeasonService.seasonInfoLog = LogFactory.getLog("astd.kfwd.log.seasonInfo");
    }
    
    public KfwdSeasonService() {
        this.lastSeasonInfo = null;
        this.curSeasonInfo = null;
    }
    
    @Override
    public void run() {
        while (true) {
            try {
                while (true) {
                    Thread.sleep(10000L);
                    this.syncSeason();
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                continue;
            }
            break;
        }
    }
    
    private synchronized void syncSeason() {
        try {
            KfwdSeasonService.seasonInfoLog.info("syncSeason");
            final KfwdSeasonInfo newInfo = this.fetchInfoFromGW();
            if (newInfo != null && newInfo.getSeasonId() > 0) {
                final int seasonId = newInfo.getSeasonId();
                final int seasonState = newInfo.getGlobalState();
                if (this.curSeasonInfo == null || this.curSeasonInfo.getSeasonId() != seasonId) {
                    KfwdSeasonService.seasonInfoLog.info("reset");
                    this.resetSeason(newInfo);
                }
                KfwdTimeControlService.processTimeInfo(newInfo);
                switch (seasonState) {
                    case 1: {
                        this.doBegionMatch(seasonId);
                        break;
                    }
                    case 2: {
                        this.doScheduleMatch(seasonId, seasonState);
                        break;
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void resetSeason(final KfwdSeasonInfo newInfo) {
        this.lastSeasonInfo = this.curSeasonInfo;
        this.curSeasonInfo = newInfo;
        this.kfwdScheduleService.intiSeasonInfo(newInfo);
        this.kfwdCacheManager.iniSeasonCache(newInfo);
        KfwdTimeControlService.iniTimeInfo(newInfo);
    }
    
    private void doBegionMatch(final int seasonId) {
        if (this.kfwdScheduleService.beginMatch(seasonId)) {
            return;
        }
    }
    
    private synchronized void doScheduleMatch(final int seasonId, final int seasonState) throws IOException {
        if (this.kfwdScheduleService.hasScheduledMatch(seasonId)) {
            return;
        }
        final KfwdMatchScheduleInfo schInfo = this.getSeasonScheduleInfo(seasonId, seasonState);
        if (schInfo == null) {
            return;
        }
        final KfwdRewardResult rewardInfo = this.getRewardInfo();
        if (rewardInfo == null) {
            return;
        }
        final boolean processRewardSuc = KfwdTimeControlService.processNewRewardInfo(rewardInfo, schInfo);
        if (!processRewardSuc) {
            return;
        }
        this.kfwdScheduleService.scheduleMatch(schInfo);
    }
    
    private KfwdRewardResult getRewardInfo() throws IOException {
        KfwdSeasonService.seasonInfoLog.info("getschduleInfo ");
        final Request request = new Request();
        request.setCommand(Command.KFWD_GW_REWARDINFO_FROM_MATCH);
        final MatchServerEntity gs = this.getMatchServerEntityInfo();
        request.setMessage(gs);
        final Response res = this.connection.sendSyncAndGetResponse(request);
        if (res != null) {
            final KfwdRewardResult rInfo = (KfwdRewardResult)res.getMessage();
            if (rInfo == null) {
                KfwdSeasonService.seasonInfoLog.info("rInfo is null");
            }
            else {
                KfwdSeasonService.seasonInfoLog.info("rInfo get" + rInfo.getRewardList().size());
            }
            return rInfo;
        }
        return null;
    }
    
    private KfwdMatchScheduleInfo getSeasonScheduleInfo(final int seasonId, final int seasonState) throws IOException {
        KfwdSeasonService.seasonInfoLog.info("getschduleInfo ");
        final Request request = new Request();
        request.setCommand(Command.KFWD_GW_SCHEDULEINFO_FROM_MATCH);
        final MatchServerEntity gs = this.getMatchServerEntityInfo();
        if (this.curSeasonInfo != null) {
            gs.setSeasonId(this.curSeasonInfo.getSeasonId());
        }
        request.setMessage(gs);
        final Response res = this.connection.sendSyncAndGetResponse(request);
        if (res != null) {
            final KfwdMatchScheduleInfo sInfo = (KfwdMatchScheduleInfo)res.getMessage();
            if (sInfo == null) {
                KfwdSeasonService.seasonInfoLog.info("sInfo is null");
            }
            else {
                KfwdSeasonService.seasonInfoLog.info("sInfo get" + sInfo.getSeasonId());
            }
            return sInfo;
        }
        return null;
    }
    
    private KfwdSeasonInfo fetchInfoFromGW() {
        final Request request = new Request();
        request.setCommand(Command.KFWD_GW_SEASONINFO_FROM_MATCH);
        final MatchServerEntity gs = this.getMatchServerEntityInfo();
        request.setMessage(gs);
        final Response res = this.connection.sendSyncAndGetResponse(request);
        return (KfwdSeasonInfo)res.getMessage();
    }
    
    private MatchServerEntity getMatchServerEntityInfo() {
        final MatchServerEntity gs = new MatchServerEntity();
        gs.setMatchSvrName(Configuration.getProperty("match.name"));
        gs.setMatchUrl(Configuration.getProperty("match.url"));
        return gs;
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        (this.connection = new KfConnection((TransferConfig)new TransferConfigGW(), KfwdSeasonService.logger)).registerHandler(Command.QUERY_SEASONINFO, (ResponseHandler)this);
        this.connection.connect();
        final Thread t = new Thread(this, "kfwdSeasonT");
        t.start();
    }
    
    @Override
    public String getWriteURL() {
        if (this.curSeasonInfo == null) {
            return null;
        }
        return this.curSeasonInfo.getReportWriteUrl();
    }
    
    @Override
    public String getReportReadURL() {
        if (this.curSeasonInfo == null) {
            return "";
        }
        return this.curSeasonInfo.getReportReadUrl();
    }
    
    @Override
	public void handle(final Response response) {
    }
}
