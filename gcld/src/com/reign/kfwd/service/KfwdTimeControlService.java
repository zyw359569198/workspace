package com.reign.kfwd.service;

import java.util.concurrent.locks.*;
import com.reign.kfwd.dto.*;
import org.apache.commons.logging.*;
import com.reign.kfwd.constants.*;
import com.reign.kf.match.common.util.*;
import com.reign.kfwd.domain.*;
import com.reign.util.*;
import org.apache.commons.lang.*;
import com.reign.kf.comm.entity.kfwd.response.*;
import java.util.*;
import java.util.regex.*;

public class KfwdTimeControlService
{
    private static Log scheduleInfoLog;
    private static ReentrantReadWriteLock changeTimelock;
    private static volatile int seasonId;
    private static volatile Date activeTime;
    private static volatile Date scheduleTime;
    private static volatile Date battleTime;
    private static volatile Integer globalState;
    private static volatile int valid;
    private static volatile int roundInterval;
    private static volatile int battleInterval;
    private static volatile Date signUpTime;
    private static volatile Date signUpFinishTime;
    private static volatile Date showBattleTime;
    private static volatile int oneDayRoundLimit;
    private static volatile Date nextDayBegionTime;
    private static volatile Date thirdDayBegionTime;
    private static volatile int totalRound;
    private static volatile Date endTime;
    private static volatile int zb;
    private static volatile int zbLayer;
    static final long MILLSECONDS = 1000L;
    private static volatile Map<Integer, RewardInfo> rewardInfoMap;
    private static volatile Map<Integer, List<KfwdRankingRewardInfo>> rankingRewardInfoListMap;
    
    static {
        KfwdTimeControlService.scheduleInfoLog = LogFactory.getLog("astd.kfwd.log.scheduleInfo");
        KfwdTimeControlService.changeTimelock = new ReentrantReadWriteLock();
        KfwdTimeControlService.oneDayRoundLimit = KfwdConstantsAndMethod.MAXROUND;
        KfwdTimeControlService.rewardInfoMap = new HashMap<Integer, RewardInfo>();
        KfwdTimeControlService.rankingRewardInfoListMap = new HashMap<Integer, List<KfwdRankingRewardInfo>>();
    }
    
    public static void processTimeInfo(final KfwdSeasonInfo newInfo) {
        KfwdTimeControlService.changeTimelock.writeLock().lock();
        try {
            KfwdTimeControlService.seasonId = newInfo.getSeasonId();
            KfwdTimeControlService.roundInterval = newInfo.getRoundInterval();
            final Calendar c = Calendar.getInstance();
            c.setTime(newInfo.getBattleTime());
            KfwdTimeControlService.battleTime = newInfo.getBattleTime();
            KfwdTimeControlService.activeTime = newInfo.getActiveTime();
            KfwdTimeControlService.signUpTime = newInfo.getSignUpTime();
            KfwdTimeControlService.signUpFinishTime = newInfo.getSignUpFinishTime();
            KfwdTimeControlService.showBattleTime = newInfo.getShowBattleTime();
            KfwdTimeControlService.endTime = newInfo.getEndTime();
            KfwdTimeControlService.oneDayRoundLimit = newInfo.getOneDayRoundLimit();
            KfwdTimeControlService.nextDayBegionTime = newInfo.getNextDayBegionTime();
            KfwdTimeControlService.thirdDayBegionTime = newInfo.getThirdDayBegionTime();
            KfwdTimeControlService.battleInterval = newInfo.getBattleInterval();
            KfwdTimeControlService.totalRound = newInfo.getTotalRound();
            if (KfwdTimeControlService.totalRound != KfwdConstantsAndMethod.MAXROUND && KfwdTimeControlService.totalRound > 0) {
                KfwdConstantsAndMethod.MAXROUND = KfwdTimeControlService.totalRound;
            }
            KfwdTimeControlService.zb = newInfo.getZb();
            KfwdTimeControlService.zbLayer = newInfo.getZbLayer();
        }
        finally {
            KfwdTimeControlService.changeTimelock.writeLock().unlock();
        }
        KfwdTimeControlService.changeTimelock.writeLock().unlock();
    }
    
    public static void iniTimeInfo(final KfwdSeasonInfo newInfo) {
    }
    
    public static long getRunDelayMillSecondsByRound(final int round, final int scheduleId) {
        KfwdTimeControlService.changeTimelock.readLock().lock();
        try {
            final long nowTime = System.currentTimeMillis();
            if (round == 1) {
                final long sDelay = KfwdConstantsAndMethod.getScheduleDelay(scheduleId);
                final long res = KfwdTimeControlService.signUpFinishTime.getTime() - nowTime + sDelay;
                return (res < 0L) ? 0L : res;
            }
            final Date btime = getRunMatchTime(round - 1, 1, 1, scheduleId);
            final long res2 = btime.getTime() + KfwdTimeControlService.battleInterval * 1000L - nowTime;
            return (res2 < 0L) ? 0L : res2;
        }
        finally {
            KfwdTimeControlService.changeTimelock.readLock().unlock();
        }
    }
    
    public static Date getRunMatchTime(final int round, final int sround, final int matchId, final int scheduleId) {
        KfwdTimeControlService.changeTimelock.readLock().lock();
        try {
            if (round <= KfwdTimeControlService.oneDayRoundLimit || KfwdTimeControlService.oneDayRoundLimit == 0 || KfwdTimeControlService.nextDayBegionTime == null) {
                final long fTime = KfwdTimeControlService.battleTime.getTime() + ((round - 1) * 1 + (sround - 1)) * KfwdTimeControlService.roundInterval * 1000L + WebUtil.nextInt(1000) + KfwdConstantsAndMethod.getScheduleDelay(scheduleId);
                return new Date(fTime);
            }
            if (round < KfwdTimeControlService.oneDayRoundLimit * 2 + 1 || KfwdTimeControlService.thirdDayBegionTime == null) {
                final long fTime = KfwdTimeControlService.nextDayBegionTime.getTime() + ((round - 1 - KfwdTimeControlService.oneDayRoundLimit) * 1 + (sround - 1)) * KfwdTimeControlService.roundInterval * 1000L + WebUtil.nextInt(1000) + KfwdConstantsAndMethod.getScheduleDelay(scheduleId);
                return new Date(fTime);
            }
            final long fTime = KfwdTimeControlService.thirdDayBegionTime.getTime() + ((round - 1 - KfwdTimeControlService.oneDayRoundLimit * 2) * 1 + (sround - 1)) * KfwdTimeControlService.roundInterval * 1000L + WebUtil.nextInt(1000) + KfwdConstantsAndMethod.getScheduleDelay(scheduleId);
            return new Date(fTime);
        }
        finally {
            KfwdTimeControlService.changeTimelock.readLock().unlock();
        }
    }
    
    public static long getMatchDelay(final KfwdRuntimeMatch match) {
        final long delay = match.getStartTime().getTime() - System.currentTimeMillis();
        return (delay > 0L) ? delay : 0L;
    }
    
    public static Tuple<Integer, Long> getNowStateAndCD() {
        KfwdTimeControlService.changeTimelock.readLock().lock();
        try {
            final Tuple<Integer, Long> res = new Tuple();
            final long now = System.currentTimeMillis();
            if (KfwdTimeControlService.activeTime == null) {
                return null;
            }
            if (now < KfwdTimeControlService.activeTime.getTime()) {
                res.left = 0;
                res.right = 0L;
                return res;
            }
            if (now < KfwdTimeControlService.signUpTime.getTime()) {
                res.left = 10;
                res.right = KfwdTimeControlService.signUpTime.getTime() - now;
                return res;
            }
            if (now < KfwdTimeControlService.signUpFinishTime.getTime()) {
                res.left = 20;
                res.right = KfwdTimeControlService.signUpFinishTime.getTime() - now;
                return res;
            }
            if (now < KfwdTimeControlService.showBattleTime.getTime()) {
                res.left = 30;
                res.right = KfwdTimeControlService.showBattleTime.getTime() - now;
                return res;
            }
            if (now < KfwdTimeControlService.endTime.getTime()) {
                res.left = 50;
                res.right = KfwdTimeControlService.endTime.getTime() - now;
                return res;
            }
            if (now >= KfwdTimeControlService.endTime.getTime()) {
                res.left = 70;
                res.right = 0L;
                return res;
            }
        }
        finally {
            KfwdTimeControlService.changeTimelock.readLock().unlock();
        }
        KfwdTimeControlService.changeTimelock.readLock().unlock();
        return null;
    }
    
    public static int getTotalRound() {
        return KfwdTimeControlService.totalRound;
    }
    
    public static int getZb() {
        return KfwdTimeControlService.zb;
    }
    
    public static int getZbLayer() {
        return KfwdTimeControlService.zbLayer;
    }
    
    public static boolean processNewRewardInfo(final KfwdRewardResult rewardInfo, final KfwdMatchScheduleInfo schInfo) {
        final Map<Integer, RewardInfo> rInfoMap = new HashMap<Integer, RewardInfo>();
        for (final KfwdRewardRuleInfo rInfo : rewardInfo.getRewardList()) {
            final String reward = rInfo.getReward();
            final Pattern pattern = Pattern.compile("(\\d*)\\+?(\\w)\\*(\\d+)");
            final Matcher mat = pattern.matcher(reward);
            if (mat.find()) {
                final String basicS = mat.group(1);
                int basicScore = 0;
                if (!StringUtils.isBlank(basicS)) {
                    basicScore = Integer.valueOf(basicS);
                }
                final int winCoef = Integer.valueOf(mat.group(3));
                final RewardInfo reInfo = new RewardInfo(basicScore, winCoef);
                rInfoMap.put(rInfo.getGroupType(), reInfo);
            }
        }
        for (final KfwdGwScheduleInfoDto sdto : schInfo.getsList()) {
            final int rId = sdto.getRewardRule();
            if (rInfoMap.get(rId) == null) {
                return false;
            }
        }
        final Map<Integer, List<KfwdRankingRewardInfo>> newRankingMap = new HashMap<Integer, List<KfwdRankingRewardInfo>>();
        for (int i = 1; i <= 3; ++i) {
            newRankingMap.put(i, new ArrayList<KfwdRankingRewardInfo>());
        }
        for (final KfwdRankingRewardInfo rri : rewardInfo.getRankingRewardList()) {
            final int day = rri.getDay();
            newRankingMap.get(day).add(rri);
        }
        KfwdTimeControlService.changeTimelock.writeLock().lock();
        try {
            KfwdTimeControlService.rankingRewardInfoListMap = newRankingMap;
            KfwdTimeControlService.rewardInfoMap.clear();
            boolean isZb = false;
            if (getZb() == 1) {
                isZb = true;
            }
            for (final KfwdGwScheduleInfoDto sdto2 : schInfo.getsList()) {
                final int rId2 = sdto2.getRewardRule();
                final RewardInfo rInfo2 = rInfoMap.get(rId2);
                if (isZb) {
                    rInfo2.setBasicScore(rInfo2.getBasicScore() * 2);
                    rInfo2.setWinCoef(rInfo2.getWinCoef() * 2);
                }
                KfwdTimeControlService.rewardInfoMap.put(sdto2.getScheduleId(), rInfo2);
            }
        }
        finally {
            KfwdTimeControlService.changeTimelock.writeLock().unlock();
        }
        KfwdTimeControlService.changeTimelock.writeLock().unlock();
        return true;
    }
    
    public static RewardInfo getRewardInfoBuyScheduleId(final int scheduleId) {
        KfwdTimeControlService.changeTimelock.readLock().lock();
        try {
            return KfwdTimeControlService.rewardInfoMap.get(scheduleId);
        }
        finally {
            KfwdTimeControlService.changeTimelock.readLock().unlock();
        }
    }
    
    public static void main(final String[] args) {
        final String reward = "S*10";
        final Pattern pattern = Pattern.compile("(\\d*)\\+?(\\w)\\*(\\d+)");
        final Matcher mat = pattern.matcher(reward);
        if (mat.find()) {
            final String basicS = mat.group(1);
            int basicScore = 0;
            if (!StringUtils.isBlank(basicS)) {
                basicScore = Integer.valueOf(basicS);
            }
            final int winCoef = Integer.valueOf(mat.group(3));
            System.out.println(String.valueOf(basicScore) + "-" + winCoef);
        }
    }
    
    public static long getMaxBattleInterVal() {
        KfwdTimeControlService.changeTimelock.readLock().lock();
        try {
            return KfwdTimeControlService.battleInterval * 1000L;
        }
        finally {
            KfwdTimeControlService.changeTimelock.readLock().unlock();
        }
    }
    
    public static Date getBattleEndTime() {
        KfwdTimeControlService.changeTimelock.readLock().lock();
        try {
            return KfwdTimeControlService.endTime;
        }
        finally {
            KfwdTimeControlService.changeTimelock.readLock().unlock();
        }
    }
    
    public static int getDayByRound(final int round) {
        KfwdTimeControlService.changeTimelock.readLock().lock();
        try {
            return (round - 1) / KfwdTimeControlService.oneDayRoundLimit + 1;
        }
        finally {
            KfwdTimeControlService.changeTimelock.readLock().unlock();
        }
    }
    
    public static int getAndCheckDayRewardRound(final int round) {
        KfwdTimeControlService.changeTimelock.readLock().lock();
        try {
            if (round % KfwdTimeControlService.oneDayRoundLimit == 1) {
                return round / KfwdTimeControlService.oneDayRoundLimit;
            }
            if (round == KfwdTimeControlService.totalRound + 1) {
                return (KfwdTimeControlService.totalRound + KfwdTimeControlService.oneDayRoundLimit - 1) / KfwdTimeControlService.oneDayRoundLimit;
            }
            return 0;
        }
        finally {
            KfwdTimeControlService.changeTimelock.readLock().unlock();
        }
    }
    
    public static int getTicketByRanking(final int day, final int ranking) {
        KfwdTimeControlService.changeTimelock.readLock().lock();
        try {
            final List<KfwdRankingRewardInfo> rankingRewardInfoList = KfwdTimeControlService.rankingRewardInfoListMap.get(day);
            for (final KfwdRankingRewardInfo rr : rankingRewardInfoList) {
                if (ranking <= rr.getRank()) {
                    int res = rr.getTickets();
                    if (KfwdTimeControlService.zb == 1) {
                        res *= 2;
                    }
                    return res;
                }
            }
            return 0;
        }
        finally {
            KfwdTimeControlService.changeTimelock.readLock().unlock();
        }
    }
}
