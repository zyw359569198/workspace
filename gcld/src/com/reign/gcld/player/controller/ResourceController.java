package com.reign.gcld.player.controller;

import org.springframework.beans.factory.*;
import org.springframework.stereotype.*;
import com.reign.gcld.player.service.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.system.dao.*;
import com.reign.gcld.common.log.*;
import com.reign.gcld.common.*;
import com.reign.gcld.player.dto.*;
import com.reign.util.*;
import com.reign.gcld.common.web.*;
import java.util.*;
import com.reign.gcld.log.*;

@Component("resourceController")
public class ResourceController implements IResourceController, InitializingBean
{
    private static final Logger log;
    private static final Logger dayReportLogger;
    private static final Logger timerLog;
    @Autowired
    private IResourceService resourceService;
    @Autowired
    private IServerTimeDao serverTimeDao;
    public static final int OUTPUT_TIME = 10000;
    
    static {
        log = CommonLog.getLog(ResourceController.class);
        dayReportLogger = new DayReportLogger();
        timerLog = new TimerLogger();
    }
    
    @Override
    public void output() {
        final Collection<PlayerDto> onlinePlayerList = Players.getAllPlayer();
        for (final PlayerDto playerDto : onlinePlayerList) {
            if (playerDto.playerId <= 0) {
                continue;
            }
            this.resourceService.output(playerDto);
        }
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final SyncResourceThread thread = new SyncResourceThread();
        thread.start();
    }
    
    private class SyncResourceThread extends Thread
    {
        public SyncResourceThread() {
            super("sync-resource-thread");
        }
        
        @Override
        public void run() {
            long time = 0L;
            while (true) {
                final long startTime = System.currentTimeMillis();
                Label_0253: {
                    try {
                        final Collection<PlayerDto> onlinePlayerList = Players.getAllPlayer();
                        for (final PlayerDto playerDto : onlinePlayerList) {
                            if (playerDto.playerId <= 0) {
                                continue;
                            }
                            try {
                                ResourceController.this.resourceService.output(playerDto);
                                for (final String log : ThreadLocalFactory.getTreadLocalLogs()) {
                                    ResourceController.dayReportLogger.info(log);
                                }
                            }
                            catch (Exception e) {
                                ResourceController.log.error("resource output thread error", e);
                                continue;
                            }
                            finally {
                                ThreadLocalFactory.clearTreadLocalLog();
                                ThreadLocalFactory.getTreadLocalLog();
                            }
                            ThreadLocalFactory.clearTreadLocalLog();
                            ThreadLocalFactory.getTreadLocalLog();
                        }
                        ResourceController.this.serverTimeDao.updateEndTime(GcldInitManager.serverTimeVid, new Date());
                        ResourceController.timerLog.info(LogUtil.formatThreadLog("ResourceController", "run", 2, System.currentTimeMillis() - startTime, "param:"));
                    }
                    catch (Exception e2) {
                        ResourceController.log.error("resource output thread error", e2);
                        break Label_0253;
                    }
                    finally {
                        ThreadLocalFactory.clearTreadLocalLog();
                        ThreadLocalFactory.getTreadLocalLog();
                    }
                    ThreadLocalFactory.clearTreadLocalLog();
                    ThreadLocalFactory.getTreadLocalLog();
                    try {
                        final long endTime = System.currentTimeMillis();
                        time = endTime - startTime;
                        time = ((time > 10000L) ? 1000L : (10000L - time));
                        Thread.sleep(time);
                    }
                    catch (InterruptedException e3) {
                        ResourceController.log.error("resource output thread error", e3);
                    }
                }
            }
        }
    }
}
