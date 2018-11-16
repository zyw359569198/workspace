package com.reign.kfgz.service;

import org.springframework.beans.factory.*;
import org.springframework.stereotype.*;
import com.reign.kf.comm.transfer.oio.*;
import com.reign.kfgz.control.*;
import org.springframework.beans.factory.annotation.*;
import org.apache.commons.logging.*;
import com.reign.kfwd.conf.*;
import com.reign.kf.comm.transfer.*;
import java.util.*;
import com.reign.kfgz.dto.response.*;
import com.reign.kf.comm.protocol.*;
import com.reign.kf.comm.entity.*;
import com.reign.kf.match.common.*;

@Component
public class KfgzSeasonService implements Runnable, InitializingBean, ResponseHandler
{
    private static Log logger;
    public static KfConnection connection;
    private static Log seasonInfoLog;
    @Autowired
    KfgzManager kfgzManager;
    
    static {
        KfgzSeasonService.logger = LogFactory.getLog("com.reign.KfwdMatchCommLogger");
        KfgzSeasonService.seasonInfoLog = LogFactory.getLog("astd.kfgz.log.seasonInfo");
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        (KfgzSeasonService.connection = new KfConnection((TransferConfig)new TransferConfigGW(), KfgzSeasonService.logger)).registerHandler(Command.KFGZ_GW_GETSEASONINFO_FROM_MATCH, (ResponseHandler)this);
        KfgzSeasonService.connection.connect();
        final Thread t = new Thread(this, "kfgzSeasonT");
        t.start();
    }
    
    @Override
	public void handle(final Response response) {
    }
    
    @Override
    public void run() {
        while (true) {
            try {
                while (true) {
                    Thread.sleep(30000L);
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
    
    private void syncSeason() {
        KfgzSeasonService.seasonInfoLog.info("sInfo");
        final KfgzSeasonInfoRes seasonInfo = this.fetchInfoFromGW();
        if (seasonInfo != null && seasonInfo.getState() == 2) {
            if (this.kfgzManager.getCurrentSeasonId() != seasonInfo.getSeasonId()) {
                KfgzSeasonService.seasonInfoLog.info("ini new Season =" + seasonInfo.getSeasonId());
                this.kfgzManager.iniNewSeason(seasonInfo);
            }
            final KfgzScheduleInfoList sList = this.fetchScheduleInfoFromGw();
            final KfgzRuleInfoList ruleList = this.fetchRuleInfoFromGw();
            final KfgzRewardInfoRes rewardRes = this.fetchRewardInfoFromGw();
            for (final KfgzScheduleInfoRes sInfo : sList.getList()) {
                final int gzId = sInfo.getGzId();
                final int ruleId = sInfo.getRuleId();
                final int rewardgId = sInfo.getRewardgId();
                final int layerId = sInfo.getLayerId();
                final KfgzRuleInfoRes rule = ruleList.getrMap().get(ruleId);
                final KfgzLayerInfoRes layerInfo = ruleList.getLayMap().get(layerId);
                final KfgzRewardRes reward = rewardRes.getRewardResByLayerIdAndGId(layerId, rewardgId);
                final KfgzBattleRewardRes bRes = rewardRes.getBattleRewardMap().get(reward.getBattleRewardId());
                this.kfgzManager.iniNewGz(gzId, sInfo, bRes, rule, layerInfo, layerInfo.getWorldId(), layerInfo.getWorldStgId(), layerInfo.getWorldNpcId());
            }
        }
    }
    
    private KfgzRewardInfoRes fetchRewardInfoFromGw() {
        final Request request = new Request();
        request.setCommand(Command.KFGZ_GW_GETREWARDINFO);
        final MatchServerEntity gs = this.getMatchServerEntityInfo();
        request.setMessage(gs);
        final Response res = KfgzSeasonService.connection.sendSyncAndGetResponse(request);
        return (KfgzRewardInfoRes)res.getMessage();
    }
    
    private KfgzRuleInfoList fetchRuleInfoFromGw() {
        final Request request = new Request();
        request.setCommand(Command.KFGZ_GW_GETRULEINFO);
        final MatchServerEntity gs = this.getMatchServerEntityInfo();
        request.setMessage(gs);
        final Response res = KfgzSeasonService.connection.sendSyncAndGetResponse(request);
        return (KfgzRuleInfoList)res.getMessage();
    }
    
    private KfgzScheduleInfoList fetchScheduleInfoFromGw() {
        final Request request = new Request();
        request.setCommand(Command.KFGZ_GW_GETSCHEDULEINFO_FROM_MATCH);
        final MatchServerEntity gs = this.getMatchServerEntityInfo();
        request.setMessage(gs);
        final Response res = KfgzSeasonService.connection.sendSyncAndGetResponse(request);
        return (KfgzScheduleInfoList)res.getMessage();
    }
    
    private KfgzSeasonInfoRes fetchInfoFromGW() {
        final Request request = new Request();
        request.setCommand(Command.KFGZ_GW_GETSEASONINFO_FROM_MATCH);
        final MatchServerEntity gs = this.getMatchServerEntityInfo();
        request.setMessage(gs);
        final Response res = KfgzSeasonService.connection.sendSyncAndGetResponse(request);
        return (KfgzSeasonInfoRes)res.getMessage();
    }
    
    private MatchServerEntity getMatchServerEntityInfo() {
        final MatchServerEntity gs = new MatchServerEntity();
        gs.setMatchSvrName(Configuration.getProperty("match.name"));
        gs.setMatchUrl(Configuration.getProperty("match.url"));
        return gs;
    }
}
