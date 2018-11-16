package com.reign.gcld.kfwd.service;

import org.springframework.beans.factory.*;
import org.springframework.stereotype.*;
import org.apache.commons.logging.*;
import com.reign.kf.comm.transfer.oio.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.player.dao.*;
import com.reign.gcld.log.*;
import java.util.concurrent.*;
import com.reign.gcld.kfwd.common.*;
import com.reign.kf.comm.transfer.*;
import com.reign.kf.comm.protocol.*;
import com.reign.kf.comm.entity.*;
import com.reign.gcld.common.*;
import com.reign.kf.comm.entity.kfwd.response.*;

@Component
public class GwService implements IGwService, InitializingBean, ResponseHandler
{
    private static Log logger;
    private KfConnection connection;
    @Autowired
    private IDataGetter dataGetter;
    @Autowired
    private IKfwdSeasonService kfwdSeasonService;
    @Autowired
    private IPlayerDao playerDao;
    private static ScheduledExecutorService executor;
    
    static {
        GwService.logger = new KfwdMatchOperationLogger();
        GwService.executor = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
    }
    
    public static ScheduledExecutorService getExecutor() {
        return GwService.executor;
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        (this.connection = new KfConnection((TransferConfig)new TransferConfigGW(), GwService.logger)).registerHandler(Command.QUERY_SEASONINFO, (ResponseHandler)this);
        this.connection.connect();
        final SyncGWThread thread = new SyncGWThread();
        thread.start();
    }
    
    @Override
	public void handle(final Response response) {
    }
    
    private class SyncGWThread extends Thread
    {
        public SyncGWThread() {
            super("sync-gw-thread");
        }
        
        @Override
        public void run() {
            while (!this.isInterrupted()) {
                try {
                    Thread.sleep(10000L);
                }
                catch (InterruptedException e) {
                    GwService.logger.error("gw query thread error", e);
                }
                try {
                    final Request request = new Request();
                    request.setCommand(Command.KFWD_GW_SEASONINFO);
                    final GameServerEntity gs = new GameServerEntity();
                    gs.setServerKey(Configuration.getProperty(Configuration.SERVER_KEY));
                    gs.setServerName(Configuration.getProperty("gcld.serverchinesename"));
                    request.setMessage(gs);
                    final Response seasonInfoResponse = GwService.this.connection.sendSyncAndGetResponse(request);
                    if (seasonInfoResponse.getCommand() != Command.KFWD_GW_SEASONINFO) {
                        continue;
                    }
                    final KfwdSeasonInfo seasonInfo = (KfwdSeasonInfo)seasonInfoResponse.getMessage();
                    if (seasonInfo == null) {
                        continue;
                    }
                    final int seasonId = seasonInfo.getSeasonId();
                    final int globalState = seasonInfo.getGlobalState();
                    if (globalState != 2 || GwService.this.kfwdSeasonService.hasScheduledSeason(seasonId)) {
                        continue;
                    }
                    final Request request2 = new Request();
                    request2.setCommand(Command.KFWD_GW_SCHEDULEINFO);
                    request2.setMessage(gs);
                    final KfwdScheduleInfoDto scheduleInfo = (KfwdScheduleInfoDto)GwService.this.connection.sendSyncAndGetResponse(request2).getMessage();
                    final Request request3 = new Request();
                    request3.setCommand(Command.KFWD_GW_REWARDINFO);
                    request3.setMessage(gs);
                    final KfwdRewardResult rewardInfo = (KfwdRewardResult)GwService.this.connection.sendSyncAndGetResponse(request3).getMessage();
                    final Request request4 = new Request();
                    request4.setCommand(Command.KFWD_GW_TICKETMARKETINFO);
                    request4.setMessage(gs);
                    final KfwdTicketMarketListInfo ticketInfo = (KfwdTicketMarketListInfo)GwService.this.connection.sendSyncAndGetResponse(request4).getMessage();
                    final Request request5 = new Request();
                    request5.setCommand(Command.KFWD_GW_TREASURE_REWARDINFO);
                    request5.setMessage(gs);
                    final KfwdRankTreasureList treasureInfo = (KfwdRankTreasureList)GwService.this.connection.sendSyncAndGetResponse(request5).getMessage();
                    if (treasureInfo == null) {
                        return;
                    }
                    GwService.this.kfwdSeasonService.createNewSeason(seasonInfo, scheduleInfo, rewardInfo, ticketInfo, treasureInfo);
                }
                catch (Exception e2) {
                    GwService.logger.error("gw query thread error", e2);
                }
            }
        }
    }
}
