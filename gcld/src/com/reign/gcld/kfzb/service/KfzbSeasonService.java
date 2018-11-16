package com.reign.gcld.kfzb.service;

import org.springframework.beans.factory.*;
import org.springframework.stereotype.*;
import org.apache.commons.logging.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.context.*;
import com.reign.kf.comm.transfer.oio.*;
import com.reign.gcld.log.*;
import java.util.concurrent.*;
import com.reign.gcld.kfwd.common.*;
import com.reign.kf.comm.transfer.*;
import org.springframework.beans.*;
import com.reign.kf.comm.entity.*;
import com.reign.gcld.common.*;
import com.reign.gcld.battle.common.*;
import com.reign.kf.comm.protocol.*;
import com.reign.kfzb.dto.response.*;
import java.util.*;

@Component("kfzbSeasonService")
public class KfzbSeasonService implements IKfzbSeasonService, InitializingBean, ApplicationContextAware
{
    private static Log kfzbLogger;
    @Autowired
    private IDataGetter dataGetter;
    private ApplicationContext context;
    private IKfzbSeasonService self;
    private SyncSeasonThread singleThread;
    private static final long sleepTimeSlow = 30000L;
    public static Map<Integer, KfzbTreasureReward> treasureRewardMap;
    private static KfConnection connectionGW;
    private KfzbSeasonInfo seasonInfo;
    
    static {
        KfzbSeasonService.kfzbLogger = new KfzbLogger();
        KfzbSeasonService.treasureRewardMap = new ConcurrentHashMap<Integer, KfzbTreasureReward>();
        KfzbSeasonService.connectionGW = new KfConnection((TransferConfig)new TransferConfigGW(), KfzbSeasonService.kfzbLogger);
    }
    
    public KfzbSeasonService() {
        this.singleThread = null;
        this.seasonInfo = null;
    }
    
    @Override
	public void setApplicationContext(final ApplicationContext arg0) throws BeansException {
        this.context = arg0;
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        this.self = (IKfzbSeasonService)this.context.getBean("kfzbSeasonService");
    }
    
    @Override
    public void init() {
        if (this.singleThread == null) {
            (this.singleThread = new SyncSeasonThread()).start();
        }
    }
    
    @Override
    public KfzbSeasonInfo getKfzbSeasonInfo() {
        return this.seasonInfo;
    }
    
    private KfzbSeasonInfo requestSeasonInfoFromGW() {
        final Request request = new Request();
        request.setCommand(Command.KFZB_GW_SEASONINFO);
        final GameServerEntity gameServerEntity = new GameServerEntity();
        gameServerEntity.setServerKey(Configuration.getProperty(Configuration.SERVER_KEY));
        request.setMessage(gameServerEntity);
        Response response = null;
        try {
            response = KfzbSeasonService.connectionGW.sendSyncAndGetResponse(request);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        if (response == null) {
            ErrorSceneLog.getInstance().appendErrorMsg("response is null").appendClassName(this.getClass().getSimpleName()).appendMethodName("requestSeasonInfoFromGW").flush();
            return null;
        }
        final KfzbSeasonInfo kfzbSeasonInfo = (KfzbSeasonInfo)response.getMessage();
        if (kfzbSeasonInfo == null) {
            return null;
        }
        return kfzbSeasonInfo;
    }
    
    private void handleNewlyKfzbSeasonInfo(final KfzbSeasonInfo newKfzbSeasonInfo) {
        try {
            if (this.seasonInfo == null) {
                if (newKfzbSeasonInfo == null) {
                    return;
                }
                this.startNewSeason(newKfzbSeasonInfo);
            }
            else if (newKfzbSeasonInfo != null) {
                if (this.seasonInfo.getSeasonId() == newKfzbSeasonInfo.getSeasonId()) {
                    this.handleCurrentSeason(newKfzbSeasonInfo);
                    final String address = newKfzbSeasonInfo.getMatchAdress().split(":")[0];
                    final int port = Integer.valueOf(newKfzbSeasonInfo.getMatchAdress().split(":")[1]);
                    this.dataGetter.getKfzbMatchService().setAddressPort(address, port);
                }
                else {
                    this.transferToNewSeason(newKfzbSeasonInfo);
                }
            }
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("", e);
        }
    }
    
    private void startMatchServiceThread() {
        if (this.seasonInfo != null && !KfzbMatchService.alive1) {
            if (this.seasonInfo.getMatchAdress() == null) {
                ErrorSceneLog.getInstance().appendErrorMsg("this.seasonInfo.getMatchAdress() == null").append("SeasonId", this.seasonInfo.getSeasonId()).flush();
                return;
            }
            final String address = this.seasonInfo.getMatchAdress().split(":")[0];
            final int port = Integer.valueOf(this.seasonInfo.getMatchAdress().split(":")[1]);
            this.dataGetter.getKfzbMatchService().startStateThread(address, port);
        }
    }
    
    private void startNewSeason(final KfzbSeasonInfo newKfzbSeasonInfo) {
        try {
            this.getTreasureRewardInfoFromGw();
            this.seasonInfo = newKfzbSeasonInfo;
            this.dataGetter.getKfzbMatchService().clearAllCacheBeforeStartNewSeason(newKfzbSeasonInfo.getSeasonId());
            this.startMatchServiceThread();
            KfzbSeasonService.kfzbLogger.info("startNewSeason: " + this.seasonInfo.getSeasonId());
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("", e);
        }
    }
    
    private void getTreasureRewardInfoFromGw() {
        final Request request = new Request();
        request.setCommand(Command.KFZB_GW_REWARDINFO);
        final GameServerEntity gameServerEntity = new GameServerEntity();
        gameServerEntity.setServerKey(Configuration.getProperty(Configuration.SERVER_KEY));
        request.setMessage(gameServerEntity);
        Response response = null;
        try {
            response = KfzbSeasonService.connectionGW.sendSyncAndGetResponse(request);
        }
        catch (Exception e) {
            e.printStackTrace();
            return;
        }
        if (response == null) {
            ErrorSceneLog.getInstance().appendErrorMsg("response is null").appendClassName(this.getClass().getSimpleName()).appendMethodName("requestRewardInfoFromGW").flush();
            return;
        }
        final KfzbRewardInfo rewardInfo = (KfzbRewardInfo)response.getMessage();
        if (rewardInfo != null) {
            final List<KfzbTreasureReward> trlist = rewardInfo.getList();
            KfzbSeasonService.treasureRewardMap.clear();
            for (final KfzbTreasureReward ktr : trlist) {
                KfzbSeasonService.treasureRewardMap.put(ktr.getPos(), ktr);
            }
        }
    }
    
    private void transferToNewSeason(final KfzbSeasonInfo newKfzbSeasonInfo) {
        this.getTreasureRewardInfoFromGw();
        KfzbSeasonService.kfzbLogger.info("transfer from Season to NewSeason:" + this.seasonInfo.getSeasonId() + "," + newKfzbSeasonInfo.getSeasonId());
        this.seasonInfo = newKfzbSeasonInfo;
        this.dataGetter.getKfzbMatchService().clearAllCacheBeforeStartNewSeason(newKfzbSeasonInfo.getSeasonId());
        this.startMatchServiceThread();
    }
    
    private void handleCurrentSeason(final KfzbSeasonInfo newKfzbSeasonInfo) {
        this.seasonInfo = newKfzbSeasonInfo;
    }
    
    private class SyncSeasonThread extends Thread
    {
        public SyncSeasonThread() {
            super("KfzbSeasonService-SyncSeasonThread");
        }
        
        @Override
        public void run() {
            while (true) {
                try {
                    final KfzbSeasonInfo kfzbSeasonInfo = KfzbSeasonService.this.requestSeasonInfoFromGW();
                    KfzbSeasonService.this.handleNewlyKfzbSeasonInfo(kfzbSeasonInfo);
                }
                catch (Exception e) {
                    ErrorSceneLog.getInstance().error(String.valueOf(this.getClass().getSimpleName()) + " run catch Exception", e);
                    try {
                        Thread.sleep(30000L);
                    }
                    catch (InterruptedException e2) {
                        ErrorSceneLog.getInstance().error(String.valueOf(this.getClass().getSimpleName()) + " run.sleep catch Exception", e2);
                    }
                    continue;
                }
                finally {
                    try {
                        Thread.sleep(30000L);
                    }
                    catch (InterruptedException e2) {
                        ErrorSceneLog.getInstance().error(String.valueOf(this.getClass().getSimpleName()) + " run.sleep catch Exception", e2);
                    }
                }
                try {
                    Thread.sleep(30000L);
                }
                catch (InterruptedException e2) {
                    ErrorSceneLog.getInstance().error(String.valueOf(this.getClass().getSimpleName()) + " run.sleep catch Exception", e2);
                }
            }
        }
    }
}
