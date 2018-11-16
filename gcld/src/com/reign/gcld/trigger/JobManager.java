package com.reign.gcld.trigger;

import org.springframework.beans.factory.*;
import org.springframework.stereotype.*;
import org.springframework.context.*;
import com.reign.gcld.common.log.*;
import org.springframework.beans.*;
import java.util.concurrent.atomic.*;
import com.reign.gcld.common.util.*;
import org.quartz.impl.*;
import java.util.*;
import org.quartz.*;
import java.text.*;

@Component("jobManager")
public class JobManager implements IJobManager, ApplicationContextAware, InitializingBean
{
    private static final Logger log;
    private static SchedulerFactory schedulerFactory;
    private static Scheduler scheduler;
    public static final String APPLICATION_CONTEXT = "application_context";
    public static final String JOB_PREFIX = "QuartzJOB_";
    public static final String TRIGGER_PREFIX = "QuartzTrigger_";
    public static final String JOB_INFO = "JOB_INFO";
    private ApplicationContext ctx;
    
    static {
        log = CommonLog.getLog(JobManager.class);
    }
    
    @Override
	public void setApplicationContext(final ApplicationContext ctx) throws BeansException {
        this.ctx = ctx;
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<MyJob> jobList = this.initJobList();
        this.initJobTrigger(jobList);
    }
    
    private List<MyJob> initJobList() {
        JobManager.log.info("init job list start");
        JobManager.log.info("i am fan yy");
        final List<MyJob> jobList = new ArrayList<MyJob>();
        final AtomicInteger atomicInteger = new AtomicInteger(1);
        jobList.add(new MyJob(atomicInteger.incrementAndGet(), "resetAtZeroClock", "battleService", "resetAtZeroClock", WebUtil.getCronExpression("job.system.resetAtZeroClock")));
        jobList.add(new MyJob(atomicInteger.incrementAndGet(), "producePoliticsEvent", "politicsService", "producePoliticsEvent", WebUtil.getCronExpression("job.politics.producePoliticsEvent")));
        jobList.add(new MyJob(atomicInteger.incrementAndGet(), "PrintOnlineNum", "systemService", "printOnlineNum", WebUtil.getCronExpression("job.system.printOnlineNum")));
        jobList.add(new MyJob(atomicInteger.incrementAndGet(), "statisticsGold", "systemService", "statisticsGold", WebUtil.getCronExpression("job.system.statisticsGold")));
        jobList.add(new MyJob(atomicInteger.incrementAndGet(), "addMarketBuyNum", "marketService", "addCanBuyNum", WebUtil.getCronExpression("job.market.addMarketBuyNum")));
        jobList.add(new MyJob(atomicInteger.incrementAndGet(), "refreshMarket", "marketService", "refreshMarket", WebUtil.getCronExpression("job.market.refreshMarket")));
        jobList.add(new MyJob(atomicInteger.incrementAndGet(), "dealTodayInfo", "killRankService", "dealTodayInfo", WebUtil.getCronExpression("job.killRank.dealTodayInfo")));
        jobList.add(new MyJob(atomicInteger.incrementAndGet(), "addDinnerNum", "dinnerService", "addDinnerNum", WebUtil.getCronExpression("job.dinner.addDinnerNum")));
        jobList.add(new MyJob(atomicInteger.incrementAndGet(), "clearDeleteMail", "mailService", "clearDeleteMail", WebUtil.getCronExpression("job.mailService.cleanMail")));
        jobList.add(new MyJob(atomicInteger.incrementAndGet(), "pushOnlineGift", "giftService", "pushOnlineGift", WebUtil.getCronExpression("job.gift.pushOnlineGift")));
        jobList.add(new MyJob(atomicInteger.incrementAndGet(), "addCountryRewards", "worldService", "addCountryRewards", WebUtil.getCronExpression("job.world.addCountryRewards")));
        jobList.add(new MyJob(atomicInteger.incrementAndGet(), "tech_addIncense", "incenseService", "addIncense", WebUtil.getCronExpression("tech.job.incense.addIncense")));
        jobList.add(new MyJob(atomicInteger.incrementAndGet(), "tech_addDinnerNumByTech", "dinnerService", "addDinnerNumByTech", WebUtil.getCronExpression("tech.job.dinner.addDinnerNumByTech")));
        jobList.add(new MyJob(atomicInteger.incrementAndGet(), "dealLeague", "worldService", "dealLeague", WebUtil.getCronExpression("job.nation.dealLeague")));
        jobList.add(new MyJob(atomicInteger.incrementAndGet(), "pushDayGift", "giftService", "pushDayGift", WebUtil.getCronExpression("job.gift.pushDayGift")));
        jobList.add(new MyJob(atomicInteger.incrementAndGet(), "pushNotice", "noticeService", "pushNotice", WebUtil.getCronExpression("job.notice.pushNotice")));
        jobList.add(new MyJob(atomicInteger.incrementAndGet(), "freeQuenchingTimes", "quenchingService", "addFreeQuenchingTimes", WebUtil.getCronExpression("job.quenching.getFreeTimes")));
        jobList.add(new MyJob(atomicInteger.incrementAndGet(), "addOfficerToken", "battleService", "addOfficerToken", WebUtil.getCronExpression("job.quenching.addOfficerToken")));
        jobList.add(new MyJob(atomicInteger.incrementAndGet(), "addCountryNpc", "timerBattleService", "addCountryNpc", WebUtil.getCronExpression("job.battle.addCountryNpc")));
        jobList.add(new MyJob(atomicInteger.incrementAndGet(), "pushData", "dataPushCenterUtil", "pushData", WebUtil.getCronExpression("job.dataPushCenterUtil.pushData")));
        jobList.add(new MyJob(atomicInteger.incrementAndGet(), "taskStart", "rankService", "startNationTasks", WebUtil.getCronExpression("job.nationTask.taskStart")));
        jobList.add(new MyJob(atomicInteger.incrementAndGet(), "taskEnd", "rankService", "nationTaskTimeIsOver", WebUtil.getCronExpression("job.nationTask.taskEnd")));
        jobList.add(new MyJob(atomicInteger.incrementAndGet(), "scanNationTask", "rankService", "scanNationTask", WebUtil.getCronExpression("job.nationTask.scanNationTask")));
        jobList.add(new MyJob(atomicInteger.incrementAndGet(), "checkInvestSerialChange", "rankService", "checkInvestSerialChange", WebUtil.getCronExpression("job.nationTask.checkInvestSerialChange")));
        jobList.add(new MyJob(atomicInteger.incrementAndGet(), "worldExpeditionAddEachHour", "timerBattleService", "worldExpeditionAddEachHour", WebUtil.getCronExpression("job.battle.worldExpeditionAddEachHour")));
        jobList.add(new MyJob(atomicInteger.incrementAndGet(), "worldDoExpeditionEachMinute", "timerBattleService", "worldDoExpeditionEachMinute", WebUtil.getCronExpression("job.battle.worldDoExpeditionEachMinute")));
        jobList.add(new MyJob(atomicInteger.incrementAndGet(), "worldDoManZuExpeditionEachMinute", "timerBattleService", "worldDoManZuExpeditionEachMinute", WebUtil.getCronExpression("job.battle.worldDoManZuExpeditionEachMinute")));
        jobList.add(new MyJob(atomicInteger.incrementAndGet(), "worldManZuExpeditionBobaoEach30Minutes", "timerBattleService", "worldManZuExpeditionBobaoEach30Minutes", WebUtil.getCronExpression("job.battle.worldManZuExpeditionBobaoEach30Minutes")));
        jobList.add(new MyJob(atomicInteger.incrementAndGet(), "addFreePhantom", "occupyService", "addFreePhantom", WebUtil.getCronExpression("job.officer.addFreePhantom")));
        jobList.add(new MyJob(atomicInteger.incrementAndGet(), "addCityEventEachMinute", "timerBattleService", "addCityEventEachMinute", WebUtil.getCronExpression("job.world.addCityEventEachMinute")));
        jobList.add(new MyJob(atomicInteger.incrementAndGet(), "clearWholeKill", "rankService", "clearWholeKill", WebUtil.getCronExpression("job.rank.wholeKillNum")));
        jobList.add(new MyJob(atomicInteger.incrementAndGet(), "sendBatExpActivity", "activityService", "sendBatExpActivity", WebUtil.getCronExpression("job.activity.sendBatExpActivity")));
        jobList.add(new MyJob(atomicInteger.incrementAndGet(), "clearBatExpActivity", "activityService", "clearBatExpActivity", WebUtil.getCronExpression("job.activity.clearBatExpActivity")));
        jobList.add(new MyJob(atomicInteger.incrementAndGet(), "pushWholeKill", "rankService", "pushWholeKill", WebUtil.getCronExpression("job.rank.pushWholeKill")));
        jobList.add(new MyJob(atomicInteger.incrementAndGet(), "addPlayerEventPerDay", "cityService", "addPlayerEventPerDay", WebUtil.getCronExpression("job.playerEvent.addPlayerEventPerDay")));
        jobList.add(new MyJob(atomicInteger.incrementAndGet(), "chatKfgz", "kfgzSeasonService", "chatKfgz", WebUtil.getCronExpression("job.kfgz.chatKfgz")));
        jobList.add(new MyJob(atomicInteger.incrementAndGet(), "resetDailyOnlineTime", "playerService", "resetDailyOnlineTime", WebUtil.getCronExpression("job.player.resetDailyOnlineTime")));
        jobList.add(new MyJob(atomicInteger.incrementAndGet(), "clearFeatRank", "rankService", "clearFeatRank", WebUtil.getCronExpression("job.rank.clearFeatRank")));
        jobList.add(new MyJob(atomicInteger.incrementAndGet(), "clearIronActivity", "activityService", "clearIronActivity", WebUtil.getCronExpression("job.activity.clearIronActivity")));
        jobList.add(new MyJob(atomicInteger.incrementAndGet(), "clearPlayerHuizhan", "huiZhanService", "clearPlayerHuizhan", WebUtil.getCronExpression("job.huizhan.clearPlayerHuizhan")));
        jobList.add(new MyJob(atomicInteger.incrementAndGet(), "activityScan", "timerBattleService", "checkActivityPer3Seconds", WebUtil.getCronExpression("job.activity.scan")));
        jobList.add(new MyJob(atomicInteger.incrementAndGet(), "resetTxVipStatus", "payService", "resetTxVipStatus", WebUtil.getCronExpression("job.pay.clearTxVipStatus")));
        jobList.add(new MyJob(atomicInteger.incrementAndGet(), "resetRTimes", "diamondShopService", "resetRTimes", WebUtil.getCronExpression("job.diamondshop.resetDailyTimes")));
        jobList.add(new MyJob(atomicInteger.incrementAndGet(), "loginReward", "playerService", "loginReward", WebUtil.getCronExpression("job.player.loginReward")));
        JobManager.log.info("init job list end");
        return jobList;
    }
    
    @Override
    public void initJobTrigger(final List<MyJob> jobList) throws SchedulerException, ParseException {
        JobManager.log.info("scheduler job start");
        JobManager.schedulerFactory = new StdSchedulerFactory();
        JobManager.scheduler = JobManager.schedulerFactory.getScheduler();
        JobManager.scheduler.getContext().put("application_context", this.ctx);
        for (final MyJob job : jobList) {
            final JobDetail tokenJob = new JobDetail(job.jobId, "DEFAULT", JobExecutor.class);
            final JobDataMap dataMap = new JobDataMap();
            dataMap.put("JOB_INFO", job);
            final CronTrigger conTrigger = new CronTrigger(job.triggerId, "DEFAULT");
            conTrigger.setJobDataMap(dataMap);
            conTrigger.setCronExpression(job.cronExpression);
            JobManager.scheduler.scheduleJob(tokenJob, conTrigger);
            JobManager.log.info("scheduler job: [" + job.jobName + "]");
        }
        JobManager.scheduler.start();
        JobManager.log.info("scheduler job end");
    }
}
