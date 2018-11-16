package com.reign.kfzb.service;

import java.util.concurrent.locks.*;
import org.apache.commons.logging.*;
import com.reign.util.*;
import com.reign.kfzb.constants.*;
import com.reign.kfzb.domain.*;
import com.reign.kf.match.common.util.*;
import com.reign.kfzb.dto.response.*;
import java.util.*;

public class KfzbTimeControlService
{
    private static ReentrantReadWriteLock changeTimelock;
    private static Log scheduleInfoLog;
    private static volatile int seasonId;
    private static volatile int totalLayer;
    private static volatile int day1Layer;
    private static volatile int day2Layer;
    private static volatile int day3Layer;
    private static Date activeTime;
    private static Date signUpTime;
    private static Date signUpFinishTime;
    private static Date scheduleTime;
    private static Date day1showBattleTime;
    private static Date day1BattleTime;
    private static Date day2showBattleTime;
    private static Date day2BattleTime;
    private static Date day3showBattleTime;
    private static Date day3BattleTime;
    private static volatile int roundInteval;
    private static volatile int battleInteval;
    private static KfzbRewardInfo rewardInfo;
    private static volatile Map<Integer, Long> battleTimeMap;
    private static Map<String, Set<String>> playerLimitInfo;
    private static volatile int battleBufferNum;
    private static volatile Date endTime;
    public static final long MILLSECONDS = 1000L;
    public static int currentLay;
    public static int currentRound;
    public static final int MaxLayerSup = 20;
    public static int[][] day1RewardWin;
    public static final int baseTicket = 5000;
    
    static {
        KfzbTimeControlService.changeTimelock = new ReentrantReadWriteLock();
        KfzbTimeControlService.scheduleInfoLog = LogFactory.getLog("astd.kfzb.log.scheduleInfo");
        KfzbTimeControlService.battleTimeMap = new HashMap<Integer, Long>();
        KfzbTimeControlService.playerLimitInfo = new HashMap<String, Set<String>>();
        KfzbTimeControlService.battleBufferNum = 1;
        KfzbTimeControlService.currentLay = -1;
        KfzbTimeControlService.currentRound = -1;
        KfzbTimeControlService.day1RewardWin = new int[21][];
    }
    
    private static int getLayerRoundID(final int layer, final int round) {
        return layer << 6 | round;
    }
    
    public static void processTimeInfo(final KfzbSeasonInfo newInfo) {
        KfzbTimeControlService.changeTimelock.writeLock().lock();
        try {
            KfzbTimeControlService.seasonId = newInfo.getSeasonId();
            KfzbTimeControlService.activeTime = newInfo.getActiveTime();
            KfzbTimeControlService.day2Layer = 2;
            KfzbTimeControlService.day3Layer = 2;
            KfzbTimeControlService.signUpTime = newInfo.getSignUpTime();
            KfzbTimeControlService.signUpFinishTime = newInfo.getSignUpFinishTime();
            KfzbTimeControlService.scheduleTime = newInfo.getScheduleTime();
            KfzbTimeControlService.day1showBattleTime = newInfo.getDay1showBattleTime();
            KfzbTimeControlService.day1BattleTime = newInfo.getDay1BattleTime();
            KfzbTimeControlService.day2showBattleTime = newInfo.getDay2showBattleTime();
            KfzbTimeControlService.day2BattleTime = newInfo.getDay2BattleTime();
            KfzbTimeControlService.day3showBattleTime = newInfo.getDay3showBattleTime();
            KfzbTimeControlService.day3BattleTime = newInfo.getDay3BattleTime();
            KfzbTimeControlService.roundInteval = newInfo.getRoundInterval();
            KfzbTimeControlService.battleInteval = newInfo.getBattleInterval();
            KfzbTimeControlService.endTime = newInfo.getEndTime();
            KfzbTimeControlService.battleTimeMap.clear();
        }
        finally {
            KfzbTimeControlService.changeTimelock.writeLock().unlock();
        }
        KfzbTimeControlService.changeTimelock.writeLock().unlock();
    }
    
    public static void iniTimeInfo(final KfzbSeasonInfo newInfo) {
        KfzbTimeControlService.currentLay = -1;
        KfzbTimeControlService.currentRound = -1;
    }
    
    public static Tuple3<Integer, Long, Integer> getNowStateAndCDAndSeasonId() {
        KfzbTimeControlService.changeTimelock.readLock().lock();
        try {
            final long now = System.currentTimeMillis();
            if (KfzbTimeControlService.activeTime == null) {
                return null;
            }
            if (now < KfzbTimeControlService.activeTime.getTime()) {
                return new Tuple3(0, 0L, KfzbTimeControlService.seasonId);
            }
            if (now < KfzbTimeControlService.signUpTime.getTime()) {
                return new Tuple3(10, 0L, KfzbTimeControlService.seasonId);
            }
            if (now < KfzbTimeControlService.signUpFinishTime.getTime()) {
                return new Tuple3(20, KfzbTimeControlService.signUpFinishTime.getTime() - now, KfzbTimeControlService.seasonId);
            }
            if (now < KfzbTimeControlService.scheduleTime.getTime()) {
                return new Tuple3(30, 0L, KfzbTimeControlService.seasonId);
            }
            if (now < KfzbTimeControlService.day1showBattleTime.getTime()) {
                return new Tuple3(40, 0L, KfzbTimeControlService.seasonId);
            }
            if (KfzbTimeControlService.currentLay > 4) {
                return new Tuple3(50, KfzbTimeControlService.day2showBattleTime.getTime() - now, KfzbTimeControlService.seasonId);
            }
            if (now < KfzbTimeControlService.day2BattleTime.getTime()) {
                return new Tuple3(51, KfzbTimeControlService.day2showBattleTime.getTime() - now, KfzbTimeControlService.seasonId);
            }
            if (now < KfzbTimeControlService.day3showBattleTime.getTime()) {
                return new Tuple3(60, KfzbTimeControlService.day2showBattleTime.getTime() - now, KfzbTimeControlService.seasonId);
            }
            if (now < KfzbTimeControlService.day3BattleTime.getTime()) {
                return new Tuple3(61, KfzbTimeControlService.day2showBattleTime.getTime() - now, KfzbTimeControlService.seasonId);
            }
            if (now < KfzbTimeControlService.endTime.getTime()) {
                return new Tuple3(65, KfzbTimeControlService.endTime.getTime() - now, KfzbTimeControlService.seasonId);
            }
            if (now >= KfzbTimeControlService.endTime.getTime()) {
                return new Tuple3(80, 0L, KfzbTimeControlService.seasonId);
            }
        }
        finally {
            KfzbTimeControlService.changeTimelock.readLock().unlock();
        }
        KfzbTimeControlService.changeTimelock.readLock().unlock();
        return null;
    }
    
    public static ReentrantReadWriteLock getChangeTimelock() {
        return KfzbTimeControlService.changeTimelock;
    }
    
    public static void setChangeTimelock(final ReentrantReadWriteLock changeTimelock) {
        KfzbTimeControlService.changeTimelock = changeTimelock;
    }
    
    public static Date getActiveTime() {
        return KfzbTimeControlService.activeTime;
    }
    
    public static void setActiveTime(final Date activeTime) {
        KfzbTimeControlService.activeTime = activeTime;
    }
    
    public static int getTotalLayer() {
        return KfzbTimeControlService.totalLayer;
    }
    
    public static void setTotalLayer(final int totalLayer) {
        KfzbTimeControlService.totalLayer = totalLayer;
    }
    
    public static int getDay1Layer() {
        return KfzbTimeControlService.day1Layer;
    }
    
    public static void setDay1Layer(final int day1Layer) {
        KfzbTimeControlService.day1Layer = day1Layer;
    }
    
    public static int getDay2Layer() {
        return KfzbTimeControlService.day2Layer;
    }
    
    public static void setDay2Layer(final int day2Layer) {
        KfzbTimeControlService.day2Layer = day2Layer;
    }
    
    public static int getDay3Layer() {
        return KfzbTimeControlService.day3Layer;
    }
    
    public static void setDay3Layer(final int day3Layer) {
        KfzbTimeControlService.day3Layer = day3Layer;
    }
    
    public static Date getDay1BattleTime() {
        return KfzbTimeControlService.day1BattleTime;
    }
    
    public static void setDay1BattleTime(final Date day1BattleTime) {
        KfzbTimeControlService.day1BattleTime = day1BattleTime;
    }
    
    public static Date getDay2BattleTime() {
        return KfzbTimeControlService.day2BattleTime;
    }
    
    public static void setDay2BattleTime(final Date day2BattleTime) {
        KfzbTimeControlService.day2BattleTime = day2BattleTime;
    }
    
    public static Date getDay3BattleTime() {
        return KfzbTimeControlService.day3BattleTime;
    }
    
    public static void setDay3BattleTime(final Date day3BattleTime) {
        KfzbTimeControlService.day3BattleTime = day3BattleTime;
    }
    
    public static int getRoundInteval() {
        return KfzbTimeControlService.roundInteval;
    }
    
    public static void setRoundInteval(final int roundInteval) {
        KfzbTimeControlService.roundInteval = roundInteval;
    }
    
    public static Map<Integer, Long> getBattleTimeMap() {
        return KfzbTimeControlService.battleTimeMap;
    }
    
    public static void setBattleTimeMap(final Map<Integer, Long> battleTimeMap) {
        KfzbTimeControlService.battleTimeMap = battleTimeMap;
    }
    
    public static Date getEndTime() {
        return KfzbTimeControlService.endTime;
    }
    
    public static void setEndTime(final Date endTime) {
        KfzbTimeControlService.endTime = endTime;
    }
    
    public static int getLayerBattleNum(final int layer) {
        if (layer > 4) {
            return 1;
        }
        if (layer > 0) {
            return KfzbCommonConstants.LAYERROUNDINFO[layer];
        }
        return 1;
    }
    
    public static Date getBattleTime(final int matchId, final int layer, final int round) {
        KfzbTimeControlService.changeTimelock.readLock().lock();
        try {
            if (layer <= KfzbTimeControlService.totalLayer) {
                if (layer > 4) {
                    final long time = KfzbTimeControlService.day1BattleTime.getTime() + (KfzbTimeControlService.totalLayer - layer) * KfzbTimeControlService.roundInteval * 1000L;
                    return new Date(time);
                }
                if (layer == 4) {
                    final long time = KfzbTimeControlService.day2BattleTime.getTime() + (round - 1) * KfzbTimeControlService.roundInteval * 1000L;
                    return new Date(time);
                }
                if (layer == 3) {
                    final long time = KfzbTimeControlService.day2BattleTime.getTime() + (KfzbCommonConstants.LAYERROUNDINFO[4] + (round - 1)) * KfzbTimeControlService.roundInteval * 1000L;
                    return new Date(time);
                }
                if (layer == 2) {
                    final long time = KfzbTimeControlService.day3BattleTime.getTime() + (round - 1) * KfzbTimeControlService.roundInteval * 1000L;
                    return new Date(time);
                }
                if (layer == 1) {
                    final long time = KfzbTimeControlService.day3BattleTime.getTime() + (KfzbCommonConstants.LAYERROUNDINFO[2] + (round - 1)) * KfzbTimeControlService.roundInteval * 1000L;
                    return new Date(time);
                }
            }
            return null;
        }
        finally {
            KfzbTimeControlService.changeTimelock.readLock().unlock();
        }
    }
    
    public static long getMatchDelay(final KfzbRuntimeMatch match) {
        final long delay = match.getStartTime().getTime() - System.currentTimeMillis() + WebUtil.nextInt(100);
        return (delay > 0L) ? delay : 0L;
    }
    
    public static Date getLastRoundBattleTime(final int matchId, final int layer, final int round) {
        if (round > 1) {
            return getBattleTime(1, layer, round - 1);
        }
        if (layer + 1 > KfzbTimeControlService.totalLayer) {
            return null;
        }
        return getBattleTime(1, layer + 1, getLayerBattleNum(layer + 1));
    }
    
    public static Map<Integer, KfzbBattleBuffer> getBattleBufferMap() {
        return null;
    }
    
    public static int getBattleBufferNum() {
        return KfzbTimeControlService.battleBufferNum;
    }
    
    public static KfzbBattleBuffer getKfzbBattleBuff(final int id) {
        return null;
    }
    
    public static boolean processRewardAndLimitInfo(final KfzbRewardInfo rewardInfo, final KfzbPlayerLimitInfo limitInfo) {
        KfzbTimeControlService.rewardInfo = rewardInfo;
        final Map<String, Set<String>> newPlayerLimitInfo = new HashMap<String, Set<String>>();
        for (final KfzbPlayerLimit info : limitInfo.getList()) {
            final String gameServer = info.getGameServer();
            final String playerName = info.getPlayerName();
            Set<String> nameSet = newPlayerLimitInfo.get(gameServer);
            if (nameSet == null) {
                nameSet = new HashSet<String>();
                newPlayerLimitInfo.put(gameServer, nameSet);
            }
            nameSet.add(playerName);
        }
        KfzbTimeControlService.playerLimitInfo = newPlayerLimitInfo;
        return true;
    }
    
    public static int getSeasonId() {
        return KfzbTimeControlService.seasonId;
    }
    
    public static boolean isInSignUpTime(final int curSeasonId) {
        final long now = System.currentTimeMillis();
        return now > KfzbTimeControlService.signUpTime.getTime() && now < KfzbTimeControlService.signUpFinishTime.getTime();
    }
    
    public static boolean inSynDataTime(final int curSeasonId) {
        return true;
    }
    
    public static Map<String, Set<String>> getPlayerLimitInfo() {
        return KfzbTimeControlService.playerLimitInfo;
    }
    
    public static Date getScheduleTime() {
        return KfzbTimeControlService.scheduleTime;
    }
    
    public static void setTotolLay(final int curSeasonId, final int totalLay) {
        KfzbTimeControlService.changeTimelock.writeLock().lock();
        try {
            if (KfzbTimeControlService.seasonId != curSeasonId) {
                return;
            }
            KfzbTimeControlService.totalLayer = totalLay;
            KfzbTimeControlService.day1Layer = totalLay - 4;
            KfzbTimeControlService.currentLay = totalLay;
            KfzbTimeControlService.currentRound = 1;
        }
        finally {
            KfzbTimeControlService.changeTimelock.writeLock().unlock();
        }
        KfzbTimeControlService.changeTimelock.writeLock().unlock();
    }
    
    public static long getNextBattleStartCD(int layer, int round) {
        KfzbTimeControlService.changeTimelock.readLock().lock();
        try {
            if (layer > 4) {
                final Date d = getBattleTime(1, layer - 1, 1);
                final long delay = d.getTime() - System.currentTimeMillis();
                return (delay > 0L) ? delay : 0L;
            }
            if (round >= KfzbCommonConstants.LAYERROUNDINFO[layer]) {
                --layer;
                round = 1;
            }
            else {
                ++round;
            }
            if (layer == 0) {
                return 0L;
            }
            final Date d = getBattleTime(1, layer, round);
            final long delay = d.getTime() - System.currentTimeMillis();
            return (delay > 0L) ? delay : 0L;
        }
        finally {
            KfzbTimeControlService.changeTimelock.readLock().unlock();
        }
    }
    
    public static KfzbRewardInfo getRewardInfo() {
        return KfzbTimeControlService.rewardInfo;
    }
    
    public static int getAllTicketsByLayerAndFinish(final int layer, final boolean playerFinished, final boolean allFinished) {
        final List<Integer> res = getTicketByLayerAndFinish(layer, playerFinished, allFinished);
        int tickets = 0;
        for (final Integer ticket : res) {
            tickets += ticket;
        }
        return tickets;
    }
    
    public static List<Integer> getTicketByLayerAndFinish(final int layer, final boolean playerFinished, final boolean allFinished) {
        final int totalLayer = getTotalLayer();
        final KfzbRewardInfo rewardInfo = KfzbTimeControlService.rewardInfo;
        final int day1BaseTicket = rewardInfo.getDay1BaseTicket();
        final int day1RoundTicketAdd = rewardInfo.getDay1RoundTicketAdd();
        final List<Integer> res = new ArrayList<Integer>();
        if (layer <= 4) {
            for (int i = 0; i < totalLayer - 4; ++i) {
                if (i == 0) {
                    res.add(5000 + day1BaseTicket + i * day1RoundTicketAdd);
                }
                else {
                    res.add(day1BaseTicket + i * day1RoundTicketAdd);
                }
            }
            if (layer == 4) {
                if (!playerFinished) {
                    return res;
                }
                res.add(rewardInfo.getLayer4Ticket() / 2);
            }
            if (layer == 3) {
                if (!playerFinished) {
                    res.add(rewardInfo.getLayer4Ticket());
                }
                else {
                    res.add(rewardInfo.getLayer4Ticket());
                    res.add(rewardInfo.getLayer3Ticket() / 2);
                }
            }
            if (layer == 2) {
                if (!playerFinished) {
                    res.add(rewardInfo.getLayer4Ticket());
                    res.add(rewardInfo.getLayer3Ticket());
                }
                else {
                    res.add(rewardInfo.getLayer4Ticket());
                    res.add(rewardInfo.getLayer3Ticket());
                    res.add(rewardInfo.getLayer2Ticket() / 2);
                }
            }
            if (layer == 1) {
                if (!playerFinished && !allFinished) {
                    res.add(rewardInfo.getLayer4Ticket());
                    res.add(rewardInfo.getLayer3Ticket());
                    res.add(rewardInfo.getLayer2Ticket());
                }
                else if (!playerFinished && allFinished) {
                    res.add(rewardInfo.getLayer4Ticket());
                    res.add(rewardInfo.getLayer3Ticket());
                    res.add(rewardInfo.getLayer2Ticket());
                    res.add(rewardInfo.getLayer1Ticket());
                }
                else {
                    res.add(rewardInfo.getLayer4Ticket());
                    res.add(rewardInfo.getLayer3Ticket());
                    res.add(rewardInfo.getLayer2Ticket());
                    res.add(rewardInfo.getLayer1Ticket() / 2);
                }
            }
            return res;
        }
        if (layer == totalLayer && playerFinished) {
            res.add(5000);
            return res;
        }
        for (int i = 0; i < totalLayer - layer; ++i) {
            if (i == 0) {
                res.add(5000 + day1BaseTicket + i * day1RoundTicketAdd);
            }
            else {
                res.add(day1BaseTicket + i * day1RoundTicketAdd);
            }
        }
        return res;
    }
    
    public static void main(final String[] args) {
        final int[] res = new int[0];
    }
    
    public static void setNewLayerRound(final int seasonId2, final int layer, final int round) {
        KfzbTimeControlService.changeTimelock.writeLock().lock();
        Label_0074: {
            try {
                if (KfzbTimeControlService.seasonId == seasonId2) {
                    if (layer < KfzbTimeControlService.currentLay) {
                        KfzbTimeControlService.currentLay = layer;
                        KfzbTimeControlService.currentRound = round;
                    }
                    else {
                        if (layer != KfzbTimeControlService.currentLay || KfzbTimeControlService.currentRound >= round) {
                            break Label_0074;
                        }
                        KfzbTimeControlService.currentRound = round;
                    }
                    return;
                }
            }
            finally {
                KfzbTimeControlService.changeTimelock.writeLock().unlock();
            }
        }
        KfzbTimeControlService.changeTimelock.writeLock().unlock();
    }
    
    public static int getLayerTicket(final int layer) {
        final int totalLayer = getTotalLayer();
        final KfzbRewardInfo rewardInfo = KfzbTimeControlService.rewardInfo;
        final int day1BaseTicket = rewardInfo.getDay1BaseTicket();
        final int day1RoundTicketAdd = rewardInfo.getDay1RoundTicketAdd();
        if (layer == totalLayer) {
            return day1BaseTicket + 5000;
        }
        if (layer > 4) {
            return day1BaseTicket + (totalLayer - layer) * day1RoundTicketAdd;
        }
        if (layer == 4) {
            return rewardInfo.getLayer4Ticket();
        }
        if (layer == 3) {
            return rewardInfo.getLayer3Ticket();
        }
        if (layer == 2) {
            return rewardInfo.getLayer2Ticket();
        }
        if (layer == 1) {
            return rewardInfo.getLayer1Ticket();
        }
        return 0;
    }
    
    public static int getBattleInterval() {
        return KfzbTimeControlService.battleInteval;
    }
    
    public static long getLayerBattleTime(final int layer) {
        final Date date = getLastRoundBattleTime(0, layer, 1);
        if (date == null) {
            return 0L;
        }
        return date.getTime() + KfzbTimeControlService.battleInteval * 1000L;
    }
    
    public static int[] getLayerAndRound() {
        KfzbTimeControlService.changeTimelock.readLock().lock();
        try {
            return new int[] { KfzbTimeControlService.currentLay, KfzbTimeControlService.currentRound };
        }
        finally {
            KfzbTimeControlService.changeTimelock.readLock().unlock();
        }
    }
    
    public static int[] getNextLayerAndRound(int layer, int round) {
        KfzbTimeControlService.changeTimelock.readLock().lock();
        try {
            if (layer > 4) {
                return new int[] { layer - 1, 1 };
            }
            if (layer <= 0) {
                return new int[] { 0, 1 };
            }
            if (round >= KfzbCommonConstants.LAYERROUNDINFO[layer]) {
                --layer;
                round = 1;
                return new int[] { layer, round };
            }
            return new int[] { layer, round + 1 };
        }
        finally {
            KfzbTimeControlService.changeTimelock.readLock().unlock();
        }
    }
    
    public static long getLayerRoundScheduleTime(final int matchId, final int layer, final int round) {
        final Date date = getLastRoundBattleTime(0, layer, round);
        if (date == null) {
            return 0L;
        }
        return date.getTime() + KfzbTimeControlService.battleInteval * 1000L;
    }
}
