package com.reign.gcld.common.web;

import com.reign.gcld.job.service.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.system.service.*;
import com.reign.gcld.world.service.*;
import com.reign.gcld.rank.service.*;
import com.reign.gcld.team.service.*;
import com.reign.gcld.battle.service.*;
import com.reign.gcld.juben.common.*;
import com.reign.gcld.juben.service.*;
import com.reign.gcld.system.dao.*;
import com.reign.gcld.slave.dao.*;
import com.reign.gcld.building.service.*;
import com.reign.gcld.kfwd.service.*;
import com.reign.gcld.huizhan.service.*;
import com.reign.gcld.event.service.*;
import com.reign.gcld.kfzb.service.*;
import com.reign.gcld.common.log.*;
import com.reign.gcld.event.common.*;
import com.reign.gcld.activity.common.*;
import com.reign.gcld.asynchronousDB.manager.*;
import com.reign.gcld.world.common.*;
import com.reign.gcld.phantom.common.*;
import com.reign.gcld.courtesy.common.*;
import com.reign.gcld.kfzb.util.*;
import com.reign.gcld.auto.common.*;
import com.reign.gcld.rank.common.*;
import com.reign.gcld.yx.common.*;
import com.reign.plugin.yx.*;
import com.reign.gcld.common.*;
import com.reign.gcld.system.domain.*;
import java.util.*;
import com.reign.gcld.battle.common.*;

public class GcldInitManager implements IGcldInitManager
{
    private static final Logger log;
    @Autowired
    private IJobService jobService;
    @Autowired
    private ICityService cityService;
    @Autowired
    private ISystemService systemService;
    @Autowired
    private CityDataCache cityDataCache;
    @Autowired
    private IRankBatService rankBatService;
    @Autowired
    private IRankService rankService;
    @Autowired
    private ITeamService teamService;
    @Autowired
    private TimerBattleService timerBattleService;
    @Autowired
    private IDataGetter dataGetter;
    @Autowired
    private IBattleService battleService;
    @Autowired
    private IDbVersionDao dbVersionDao;
    @Autowired
    private IJuBenDataCache juBenDataCache;
    @Autowired
    private IJuBenService juBenService;
    @Autowired
    private IServerTimeDao serverTimeDao;
    @Autowired
    private IPlayerSlaveDao playerSlaveDao;
    @Autowired
    private IBuildingService buildingService;
    @Autowired
    private IKfwdMatchService kfwdMatchService;
    @Autowired
    private IHuiZhanService huiZhanService;
    @Autowired
    private IEventService eventService;
    @Autowired
    private IKfzbFeastService kfzbFeastService;
    public static int serverTimeVid;
    public static ServerTime lastServerTime;
    public static ServerTime latestServerTime;
    
    static {
        log = CommonLog.getLog(GcldInitManager.class);
        GcldInitManager.serverTimeVid = 0;
        GcldInitManager.lastServerTime = null;
        GcldInitManager.latestServerTime = null;
    }
    
    @Override
    public void sysInit() {
        int num = 0;
        final long startTime = System.currentTimeMillis();
        this.rankService.initForceInfo();
        GcldInitManager.log.info("init_Time_" + num++ + ":" + (System.currentTimeMillis() - startTime) + "name:rankService.initForceInfo();");
        this.dataGetter.getPlayerGeneralMilitaryDao().updateAllState();
        this.dataGetter.getBarbarainPhantomDao().resetStateAll();
        this.dataGetter.getExpeditionArmyDao().resetAllState();
        this.dataGetter.getBarbarainExpeditionArmyDao().resetAllState();
        this.dataGetter.getNationTaskExpeditionArmyDao().resetAllState();
        this.dataGetter.getYellowTurbansDao().resetAllState();
        this.dataGetter.getActivityNpcDao().deleteAll();
        this.cityDataCache.initCityArray();
        this.battleService.initBarbarainPhantomMaxId();
        this.timerBattleService.Init();
        this.timerBattleService.BarbarainInvadeRecover();
        this.jobService.initTimer();
        AddJob.getInstance().init(this.jobService);
        this.playerSlaveDao.releaseAll();
        this.serverTimeInit();
        this.systemService.initAllActivity();
        this.rankBatService.initBatRankList();
        this.systemService.initIntercepterBlockMap();
        this.dbVersionInit();
        this.teamService.initTeam();
        this.juBenService.initScenarioNpcVid();
        SlaveEvent.init(this.dataGetter);
        GcldInitManager.log.info("init_Total_Time_" + num++ + ":" + (System.currentTimeMillis() - startTime) + "name:SlaveEvent.init");
        this.eventService.init();
        GcldInitManager.log.info("init_Total_Time_" + num++ + ":" + (System.currentTimeMillis() - startTime) + "eventService.init");
        GcldInitManager.log.info("init_Time_" + num++ + ":" + (System.currentTimeMillis() - startTime));
        MiddleAutumnCache.getInstance().init(this.dataGetter);
        AsynchronousDBOperationManager.getInstance().init(this.dataGetter);
        BattleScheduler.getInstance().startBattleScheduler(this.dataGetter);
        WorldDramaTimesCache.getInstatnce().initWorldDramaTimesCache(this.dataGetter.getPlayerScenarioDao());
        GcldInitManager.log.info("init_Time_" + num++ + ":" + (System.currentTimeMillis() - startTime) + "initWorldDramaTimesCache");
        CityEventManager.getInstance().initCityEventManager(this.dataGetter);
        GcldInitManager.log.info("init_Time_" + num++ + ":" + (System.currentTimeMillis() - startTime) + "initCityEventManager");
        PlayerRoundDbObjManager.getInstance().ini(this.dataGetter);
        PhantomManager.getInstance().initPhantomManager(this.dataGetter);
        ManWangLingManager.getInstance().initManWangLingManager(this.dataGetter);
        CourtesyManager.getInstance().initCourtesyManager(this.dataGetter);
        KfzbManager.init(this.dataGetter);
        PlayerAutoBattleManager.getInstance().initPlayerAutoBattleManager(this.dataGetter);
        this.cityService.initCountryPrivilege();
        this.rankService.initNationLv();
        GcldInitManager.log.info("init_Time_" + num++ + ":" + (System.currentTimeMillis() - startTime) + "rankService.initNationLv");
        this.buildingService.initResourceAdditionTimeJob();
        this.cityDataCache.initForceCityNum();
        this.cityService.initWorldCity();
        this.cityService.initWorldRoadXml();
        this.cityService.initCityBattleId();
        GcldInitManager.log.info("init_Time_" + num++ + ":" + (System.currentTimeMillis() - startTime) + "initCityBattleId");
        this.cityDataCache.init();
        GcldInitManager.log.info("init_Time_" + num++ + ":" + (System.currentTimeMillis() - startTime) + "cityDataCache.init");
        this.dataGetter.getNationService().initTryTask();
        GcldInitManager.log.info("init_Time_" + num++ + ":" + (System.currentTimeMillis() - startTime) + "initTryTask");
        this.dataGetter.getActivityService().initInnerActivity();
        GcldInitManager.log.info("init_Time_" + num++ + ":" + (System.currentTimeMillis() - startTime) + "initInnerActivity");
        this.rankService.initRankerAndRelativeInfo();
        GcldInitManager.log.info("init_Time_" + num++ + ":" + (System.currentTimeMillis() - startTime) + "initRankerAndRelativeInfo");
        this.huiZhanService.recoverHuizhan();
        GcldInitManager.log.info("init_Time_" + num++ + ":" + (System.currentTimeMillis() - startTime) + "recoverHuizhan");
        this.dataGetter.getCityService().initResetCityBattleAndCheckCityPGMState();
        this.battleService.loadGoldOrderFromDB(5);
        GcldInitManager.log.info("init_Time_" + num++ + ":" + (System.currentTimeMillis() - startTime) + "loadGoldOrderFromDB");
        this.dataGetter.getBattleInfoService().recoverCountryLevelUpBattles();
        this.juBenService.initMiniWinTime();
        GcldInitManager.log.info("init_Time_" + num++ + ":" + (System.currentTimeMillis() - startTime) + "initMiniWinTime");
        this.juBenDataCache.initPath();
        GcldInitManager.log.info("init_Time_" + num++ + ":" + (System.currentTimeMillis() - startTime) + "initPath");
        this.juBenService.initAllJuBenIngToCache();
        GcldInitManager.log.info("init_Time_" + num++ + ":" + (System.currentTimeMillis() - startTime) + "initAllJuBenIngToCache");
        this.kfwdMatchService.ini();
        GcldInitManager.log.info("init_Time_" + num++ + ":" + (System.currentTimeMillis() - startTime) + "kfwdMatchService");
        this.jobService.InitExeJob();
        GcldInitManager.log.info("init_Time_" + num++ + ":" + (System.currentTimeMillis() - startTime) + "InitExeJob");
        this.rankService.initTaskRelativeInfo();
        GcldInitManager.log.info("init_Time_" + num++ + ":" + (System.currentTimeMillis() - startTime) + "initTaskRelativeInfo");
        this.dataGetter.getPhantomService().recoverPhantomJob();
        this.dataGetter.getKfzbSeasonService().init();
        this.kfzbFeastService.init();
        GcldInitManager.log.info("init_Total_Time_" + num++ + ":" + (System.currentTimeMillis() - startTime) + "kfzbFeastService");
        GcldInitManager.log.info("init_Total_Time_" + num++ + ":" + (System.currentTimeMillis() - startTime));
        InMemmoryIndivTaskManager.getInstance().init(this.dataGetter);
        GcldInitManager.log.info("init_Total_Time_" + num++ + ":" + (System.currentTimeMillis() - startTime) + "InMemmoryIndivTaskManager");
    }
    
    private void serverTimeInit() {
        GcldInitManager.lastServerTime = this.serverTimeDao.getLastServerTime();
        final Date date = new Date();
        final ServerTime sTime = new ServerTime(date, date);
        this.serverTimeDao.create(sTime);
        GcldInitManager.serverTimeVid = sTime.getVId();
        GcldInitManager.latestServerTime = sTime;
    }
    
    private void dbVersionInit() {
        PluginContext.configuration = new YxConfigurationS();
        final String serverTime = Configuration.getProperty("gcld.server.time");
        if (serverTime != null) {
            try {
                final long st = Long.valueOf(serverTime);
                final List<DbVersion> dbList = this.dbVersionDao.getModels();
                if (dbList.size() > 0 && (dbList.get(0).getServerTime() == null || dbList.get(0).getServerTime().getTime() != st)) {
                    this.dbVersionDao.updateServerTime(new Date(st));
                }
            }
            catch (Exception e) {
                GcldInitManager.log.error("sysInit ", e);
            }
        }
    }
    
    public static long getRestoreTime(final long triggerTime) {
        try {
            if (GcldInitManager.lastServerTime == null || GcldInitManager.latestServerTime == null) {
                return 0L;
            }
            if (triggerTime > GcldInitManager.lastServerTime.getEndTime().getTime()) {
                return 0L;
            }
            return GcldInitManager.latestServerTime.getStartTime().getTime() - GcldInitManager.lastServerTime.getEndTime().getTime();
        }
        catch (Exception e) {
            final ErrorSceneLog errorSceneLog = ErrorSceneLog.getInstance();
            errorSceneLog.error("getRestoreTime exception...", e);
            return 0L;
        }
    }
}
