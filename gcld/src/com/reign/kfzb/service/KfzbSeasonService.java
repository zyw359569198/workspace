package com.reign.kfzb.service;

import org.springframework.beans.factory.*;
import org.springframework.stereotype.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.kf.comm.transfer.oio.*;
import com.reign.kfzb.cache.*;
import org.apache.commons.logging.*;
import com.reign.kf.match.common.*;
import java.io.*;
import com.reign.kf.comm.util.*;
import com.reign.kfzb.dto.response.*;
import com.reign.kf.comm.entity.*;
import com.reign.kf.comm.protocol.*;
import com.reign.kfwd.conf.*;
import com.reign.kf.comm.transfer.*;

@Component
public class KfzbSeasonService implements IKfzbSeasonService, Runnable, InitializingBean
{
    private static Log logger;
    @Autowired
    IKfzbScheduleService kfzbScheduleService;
    private KfConnection connection;
    @Autowired
    IKfzbCacheManager kfzbCacheManager;
    public volatile KfzbSeasonInfo lastSeasonInfo;
    public volatile KfzbSeasonInfo curSeasonInfo;
    private static Log seasonInfoLog;
    public volatile int synZbCampainTimeId;
    
    static {
        KfzbSeasonService.logger = LogFactory.getLog("com.reign.KfzbMatchCommLogger");
        KfzbSeasonService.seasonInfoLog = LogFactory.getLog("astd.kfzb.log.seasonInfo");
    }
    
    public KfzbSeasonService() {
        this.lastSeasonInfo = null;
        this.curSeasonInfo = null;
        this.synZbCampainTimeId = 1;
    }
    
    @Override
    public void run() {
        while (true) {
            try {
                while (true) {
                    this.syncSeason();
                    Thread.sleep(10000L);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                try {
                    Thread.sleep(60000L);
                }
                catch (InterruptedException e2) {
                    e2.printStackTrace();
                }
                continue;
            }
            break;
        }
    }
    
    private synchronized void syncSeason() {
        try {
            KfzbSeasonService.seasonInfoLog.info("syncSeason");
            final KfzbSeasonInfo newInfo = this.fetchInfoFromGW();
            if (newInfo != null && newInfo.getSeasonId() > 0) {
                if (!newInfo.getMatchName().equals(Configuration.getProperty("match.name"))) {
                    return;
                }
                final int seasonId = newInfo.getSeasonId();
                final int seasonState = newInfo.getGlobalState();
                if (this.curSeasonInfo == null || this.curSeasonInfo.getSeasonId() != seasonId) {
                    KfzbSeasonService.seasonInfoLog.info("reset");
                    this.resetSeason(newInfo);
                }
                KfzbTimeControlService.processTimeInfo(newInfo);
                switch (seasonState) {
                    case 1: {
                        this.doBegionMatch(seasonId);
                        break;
                    }
                    case 2: {
                        this.doScheduleMatch(newInfo, seasonState);
                        break;
                    }
                }
                try {
                    if (this.synZbCampainTimeId % 5 == 1) {
                        this.checkAndSendZbResultToGw(newInfo);
                    }
                    else {
                        ++this.synZbCampainTimeId;
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else {
                try {
                    if (this.synZbCampainTimeId % 100 == 13) {
                        this.checkAndSendZbResultToGw(null);
                    }
                    else {
                        ++this.synZbCampainTimeId;
                    }
                }
                catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        }
        catch (IOException e3) {
            e3.printStackTrace();
        }
        catch (HttpUtil.ServerUnavailable e4) {
            e4.printStackTrace();
        }
    }
    
    private void checkAndSendZbResultToGw(final KfzbSeasonInfo newInfo) throws IOException, HttpUtil.ServerUnavailable {
    }
    
    private void resetSeason(final KfzbSeasonInfo newInfo) {
        this.lastSeasonInfo = this.curSeasonInfo;
        this.curSeasonInfo = newInfo;
        this.kfzbScheduleService.intiSeasonInfo(newInfo);
        this.kfzbCacheManager.iniSeasonCache(newInfo);
        KfzbTimeControlService.iniTimeInfo(newInfo);
    }
    
    private void doBegionMatch(final int seasonId) {
        if (this.kfzbScheduleService.beginMatch(seasonId)) {
            return;
        }
    }
    
    private synchronized void doScheduleMatch(final KfzbSeasonInfo newInfo, final int seasonState) throws IOException, HttpUtil.ServerUnavailable {
        if (this.kfzbScheduleService.hasScheduledMatch(newInfo.getSeasonId())) {
            return;
        }
        final KfzbPlayerLimitInfo limitInfo = this.getPlayerLimitInfo();
        if (limitInfo == null) {
            return;
        }
        final KfzbRewardInfo rewardInfo = this.getRewardInfo();
        if (rewardInfo == null) {
            return;
        }
        final boolean processRewardSuc = KfzbTimeControlService.processRewardAndLimitInfo(rewardInfo, limitInfo);
        if (!processRewardSuc) {
            return;
        }
        this.kfzbScheduleService.scheduleMatch(newInfo);
    }
    
    private KfzbSeasonInfo fetchInfoFromGW() throws IOException, HttpUtil.ServerUnavailable {
        final Request request = new Request();
        request.setCommand(Command.KFZB_GW_SEASONINFO_FROM_MATCH);
        final MatchServerEntity gs = this.getMatchServerEntityInfo();
        request.setMessage(gs);
        final Response res = this.connection.sendSyncAndGetResponse(request);
        return (KfzbSeasonInfo)res.getMessage();
    }
    
    private MatchServerEntity getMatchServerEntityInfo() {
        final MatchServerEntity gs = new MatchServerEntity();
        gs.setMatchSvrName(Configuration.getProperty("match.name"));
        gs.setMatchUrl(Configuration.getProperty("match.url"));
        return gs;
    }
    
    private KfzbPlayerLimitInfo getPlayerLimitInfo() {
        final Request request = new Request();
        request.setCommand(Command.KFZB_GW_PLAYERLIMIT);
        final MatchServerEntity gs = this.getMatchServerEntityInfo();
        request.setMessage(gs);
        final Response res = this.connection.sendSyncAndGetResponse(request);
        if (res != null) {
            final KfzbPlayerLimitInfo rInfo = (KfzbPlayerLimitInfo)res.getMessage();
            if (rInfo == null) {
                KfzbSeasonService.seasonInfoLog.info("rInfo is null");
            }
            else {
                KfzbSeasonService.seasonInfoLog.info("rInfo get" + rInfo.getList().size());
            }
            return rInfo;
        }
        return null;
    }
    
    private KfzbRewardInfo getRewardInfo() throws IOException {
        final Request request = new Request();
        request.setCommand(Command.KFZB_GW_REWARDINFO_FROM_MATCH);
        final MatchServerEntity gs = this.getMatchServerEntityInfo();
        request.setMessage(gs);
        final Response res = this.connection.sendSyncAndGetResponse(request);
        return (KfzbRewardInfo)res.getMessage();
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        (this.connection = new KfConnection((TransferConfig)new TransferConfigGW(), KfzbSeasonService.logger)).connect();
        final Thread t = new Thread(this, "kfzbSeasonT");
        t.start();
    }
    
    @Override
    public KfConnection getConnection() {
        return this.connection;
    }
}
