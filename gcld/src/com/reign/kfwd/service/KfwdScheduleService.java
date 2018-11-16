package com.reign.kfwd.service;

import org.springframework.beans.factory.*;
import org.springframework.stereotype.*;
import org.springframework.context.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.kfwd.cache.*;
import com.reign.kfwd.dao.*;
import org.apache.commons.logging.*;
import com.reign.kfwd.constants.*;
import com.reign.kfwd.util.*;
import java.util.concurrent.*;
import com.reign.framework.hibernate.model.*;
import com.reign.kfwd.notice.*;
import java.util.*;
import org.springframework.transaction.annotation.*;
import org.springframework.beans.*;
import java.io.*;
import com.reign.kf.comm.param.match.*;
import com.reign.kf.match.sdata.domain.*;
import com.reign.util.*;
import com.reign.kf.match.common.*;
import com.reign.kfwd.dto.*;
import com.reign.kfwd.domain.*;
import com.reign.kf.match.sdata.cache.*;
import com.reign.kf.match.common.web.session.*;
import com.reign.kf.match.operationresult.*;
import com.reign.kf.comm.entity.kfwd.request.*;
import com.reign.kf.comm.entity.kfwd.response.*;
import com.reign.framework.json.*;
import com.reign.kfwd.battle.*;

@Component
public class KfwdScheduleService implements IKfwdScheduleService, ApplicationContextAware, InitializingBean
{
    public static volatile int curSeasonId;
    public static volatile int globalstate;
    public static volatile long lastSetGlobalStateTime;
    public static volatile boolean oneScheduleToMaxRound;
    public static volatile int scheduleNum;
    private ApplicationContext context;
    private IKfwdScheduleService self;
    private Object scheduleNumLock;
    ScheduledThreadPoolExecutor exeutors;
    @Autowired
    IKfwdBattleWarriorDao kfwdBattleWarriorDao;
    @Autowired
    IKfwdBattleWarriorGeneralDao kfwdBattleWarriorGeneralDao;
    @Autowired
    IKfwdMatchService kfwdMatchService;
    @Autowired
    IKfwdRuntimeResultDao kfwdRuntimeResultDao;
    @Autowired
    IKfwdRuntimeMatchDao kfwdRuntimeMatchDao;
    @Autowired
    IKfwdRuntimeInspireDao kfwdRuntimeInspireDao;
    @Autowired
    IKfwdCacheManager kfwdCacheManager;
    @Autowired
    WdMatchReportQueue wdMatchReportQueue;
    @Autowired
    IKfwdRewardDoubleDao kfwdRewardDoubleDao;
    @Autowired
    IKfwdTicketRewardDao kfwdTicketRewardDao;
    private Map<Integer, KfwdRTMatchInfo> kfwdMatchInfoMap;
    public static volatile Map<Integer, Integer> scheduleIdLevelLimitMap;
    private static Log rtInfoLog;
    private static Log scheduleInfoLog;
    private static Log commonLog;
    private static Log interfaceLog;
    private static Log battleReportLog;
    HashMap<Integer, HashMap<Integer, Integer>> roundMatchNumMap;
    Object roundMatchNumlock;
    Map<Integer, KfwdRankingListInfo> rankingListMap;
    public static final int NOBATTLEAGAIN_NUM = 5;
    final long SECOND30 = 30000L;
    
    static {
        KfwdScheduleService.scheduleIdLevelLimitMap = new HashMap<Integer, Integer>();
        KfwdScheduleService.rtInfoLog = LogFactory.getLog("astd.kfwd.log.rtInfo");
        KfwdScheduleService.scheduleInfoLog = LogFactory.getLog("astd.kfwd.log.scheduleInfo");
        KfwdScheduleService.commonLog = LogFactory.getLog("astd.kfwd.log.comm");
        KfwdScheduleService.interfaceLog = LogFactory.getLog("astd.kfwd.log.interface");
        KfwdScheduleService.battleReportLog = LogFactory.getLog("mj.kfwd.battleReport.log");
    }
    
    public KfwdScheduleService() {
        this.scheduleNumLock = new Object();
        this.exeutors = new ScheduledThreadPoolExecutor(6);
        this.kfwdMatchInfoMap = new ConcurrentHashMap<Integer, KfwdRTMatchInfo>();
        this.roundMatchNumMap = new HashMap<Integer, HashMap<Integer, Integer>>();
        this.roundMatchNumlock = new Object();
        this.rankingListMap = new HashMap<Integer, KfwdRankingListInfo>();
    }
    
    @Override
    public void intiSeasonInfo(final KfwdSeasonInfo newInfo) {
        KfwdScheduleService.curSeasonId = newInfo.getSeasonId();
        KfwdScheduleService.globalstate = 1;
    }
    
    @Override
    public boolean beginMatch(final int seasonId) {
        return false;
    }
    
    @Override
    public boolean hasScheduledMatch(final int seasonId) {
        return KfwdScheduleService.globalstate >= 3;
    }
    
    @Override
    public void scheduleMatch(final KfwdMatchScheduleInfo schInfo) {
        this.kfwdMatchInfoMap.clear();
        KfwdBattleManager.clear();
        KfwdDayBattleEndNoticeInfo.clearAll();
        KfwdScheduleService.scheduleNum = 0;
        KfwdScheduleService.lastSetGlobalStateTime = 0L;
        KfwdScheduleService.oneScheduleToMaxRound = false;
        for (final KfwdGwScheduleInfoDto schDto : schInfo.getsList()) {
            this.iniScheduleInfo(schDto);
        }
        if (KfwdScheduleService.globalstate < 3) {
            KfwdScheduleService.globalstate = 3;
        }
    }
    
    private void iniScheduleInfo(final KfwdGwScheduleInfoDto schDto) {
        final int round = 1;
        if (schDto == null) {
            return;
        }
        ++KfwdScheduleService.scheduleNum;
        KfwdScheduleService.scheduleInfoLog.info("scheduleNum=" + KfwdScheduleService.scheduleNum + "scheduleId=0");
        final int scheduleId = schDto.getScheduleId();
        this.roundMatchNumMap.remove(scheduleId);
        final String slevelRange = schDto.getLevelRange();
        final Integer[] levelRange = KfwdConstantsAndMethod.parseLevelRangeString(slevelRange);
        final int topLevel = (levelRange == null) ? 300 : levelRange[1];
        if (schDto.getLevelRangeType() == 2) {
            KfwdScheduleService.scheduleIdLevelLimitMap.put(schDto.getScheduleId(), Integer.MAX_VALUE);
        }
        else {
            KfwdScheduleService.scheduleIdLevelLimitMap.put(schDto.getScheduleId(), topLevel);
        }
        final int lastRound = this.createRunTimeMatch(scheduleId);
        if (lastRound == 0) {
            this.self.runNextRoundMatch(schDto.getSeasonId(), schDto.getScheduleId(), 1);
            return;
        }
        this.scheduleRunTimeMatch(scheduleId, lastRound);
    }
    
    private int createRunTimeMatch(final int scheduleId) {
        final List<KfwdRuntimeMatch> matchList = this.kfwdRuntimeMatchDao.getLastRoundMatch(KfwdScheduleService.curSeasonId, scheduleId);
        if (matchList == null || matchList.size() == 0) {
            return 0;
        }
        final int lastRound = matchList.get(0).getRound();
        final Map<Integer, KfwdRuntimeMatch> map = new HashMap<Integer, KfwdRuntimeMatch>();
        for (final KfwdRuntimeMatch match : matchList) {
            final int key = match.getMatchId() * 10 + match.getsRound();
            map.put(key, match);
        }
        for (final KfwdRuntimeMatch match : matchList) {
            if (match.getsRoundWinner() != 0 && match.getsRound() < 1) {
                final int nextKey = match.getMatchId() * 10 + match.getsRound() + 1;
                if (map.containsKey(nextKey)) {
                    continue;
                }
                this.self.buildNewMatch(match);
            }
        }
        return lastRound;
    }
    
    private void scheduleRunTimeMatch(final int scheduleId, final int round) {
        this.doIniRankingMap(KfwdScheduleService.curSeasonId, scheduleId, round);
        final List<KfwdRuntimeResult> resultList = this.kfwdRuntimeResultDao.getResultByScheduleId(KfwdScheduleService.curSeasonId, scheduleId);
        final Map<Integer, Long> resMap = new HashMap<Integer, Long>();
        final Map<Integer, Integer> lastRoundScoreMap = new HashMap<Integer, Integer>();
        for (final KfwdRuntimeResult kfwdResult : resultList) {
            resMap.put(kfwdResult.getCompetitorId(), kfwdResult.getWinRes());
            lastRoundScoreMap.put(kfwdResult.getCompetitorId(), kfwdResult.getLastScore());
        }
        final List<KfwdRuntimeInspire> inpList = this.kfwdRuntimeInspireDao.getInspireByScheduleIdAndRound(KfwdScheduleService.curSeasonId, scheduleId, round);
        final Map<Integer, Integer> inspireMap = new HashMap<Integer, Integer>();
        for (final KfwdRuntimeInspire inspire : inpList) {
            inspireMap.put(inspire.getCompetitorId(), KfwdConstantsAndMethod.getInspireValue(inspire.getAttNum(), inspire.getDefNum()));
        }
        final RewardInfo rInfo = KfwdTimeControlService.getRewardInfoBuyScheduleId(scheduleId);
        for (int i = 1; i <= 1; ++i) {
            List<KfwdRuntimeMatch> matchList1 = this.kfwdRuntimeMatchDao.getSRoundMatch(KfwdScheduleService.curSeasonId, scheduleId, round, i);
            for (final KfwdRuntimeMatch match : matchList1) {
                final int p1 = match.getPlayer1Id();
                final int p2 = match.getPlayer2Id();
                int p1RoundRes = 0;
                int p2RoundRes = 0;
                if (match.getsRoundWinner() != 0) {
                    if (match.getsRoundWinner() == p1) {
                        p1RoundRes = 1;
                        p2RoundRes = 2;
                    }
                    else {
                        p2RoundRes = 1;
                        p1RoundRes = 2;
                    }
                }
                final Integer insp1 = inspireMap.get(p1);
                final Integer insp2 = inspireMap.get(p2);
                KfwdRTMatchInfo rtInfo1 = this.copyRtInfo(this.kfwdMatchInfoMap.get(p1));
                if (rtInfo1 == null) {
                    rtInfo1 = new KfwdRTMatchInfo();
                }
                Long res2 = resMap.get(p2);
                if (res2 == null) {
                    res2 = 0L;
                }
                rtInfo1.setCompetitorId1(p1);
                rtInfo1.setCompetitorId2(p2);
                rtInfo1.setMatchId(match.getMatchId());
                rtInfo1.setScheduleId(scheduleId);
                rtInfo1.setRound(match.getRound());
                rtInfo1.setsRound(match.getsRound());
                rtInfo1.setLastRoundBattleTime(rtInfo1.getRoundBattleTime());
                rtInfo1.setRoundBattleTime(match.getStartTime());
                rtInfo1.addsRoundBattleRes(i, p1RoundRes);
                int ticket = KfwdConstantsAndMethod.getTicketByScore(rInfo.getBasicScore(), rInfo.getWinCoef(), match.getP1Score());
                rtInfo1.setScoreAndTicket(match.getP1Score(), ticket);
                final int lastScore = (lastRoundScoreMap.get(p1) == null) ? 0 : lastRoundScoreMap.get(p1);
                rtInfo1.setLastScore(lastScore);
                rtInfo1.setLastTicket(ticket);
                rtInfo1.setP1score(match.getP1Score());
                rtInfo1.setP2score(match.getP2Score());
                rtInfo1.setP1Ranking(match.getP1pos());
                rtInfo1.setP2Ranking(match.getP2pos());
                final Long res3 = resMap.get(p1);
                if (res3 != null) {
                    rtInfo1.addBattleHisRes(p1, res3);
                    rtInfo1.addBattleHisRes(p2, res2);
                }
                if (insp1 != null) {
                    rtInfo1.setInspire1(insp1);
                }
                if (insp2 != null) {
                    rtInfo1.setInspire2(insp2);
                }
                this.kfwdMatchInfoMap.put(p1, rtInfo1);
                KfwdRTMatchInfo rtInfo2 = this.copyRtInfo(this.kfwdMatchInfoMap.get(p2));
                if (rtInfo2 == null) {
                    rtInfo2 = new KfwdRTMatchInfo();
                }
                rtInfo2.setCompetitorId1(p1);
                rtInfo2.setCompetitorId2(p2);
                rtInfo2.setMatchId(match.getMatchId());
                rtInfo2.setScheduleId(scheduleId);
                rtInfo2.setRound(match.getRound());
                rtInfo2.setsRound(match.getsRound());
                rtInfo2.setLastRoundBattleTime(rtInfo2.getRoundBattleTime());
                rtInfo2.setRoundBattleTime(match.getStartTime());
                rtInfo2.addsRoundBattleRes(i, p1RoundRes);
                ticket = KfwdConstantsAndMethod.getTicketByScore(rInfo.getBasicScore(), rInfo.getWinCoef(), match.getP2Score());
                rtInfo2.setScoreAndTicket(match.getP1Score(), ticket);
                rtInfo2.setLastScore((lastRoundScoreMap.get(p2) == null) ? 0 : ((int)lastRoundScoreMap.get(p2)));
                rtInfo2.setLastTicket(ticket);
                rtInfo2.setP1score(match.getP1Score());
                rtInfo2.setP2score(match.getP2Score());
                rtInfo2.setP1Ranking(match.getP1pos());
                rtInfo2.setP2Ranking(match.getP2pos());
                rtInfo2.addBattleHisRes(p2, res2);
                rtInfo2.addBattleHisRes(p1, res3);
                if (insp1 != null) {
                    rtInfo2.setInspire1(insp1);
                }
                if (insp2 != null) {
                    rtInfo2.setInspire2(insp2);
                }
                this.kfwdMatchInfoMap.put(p2, rtInfo2);
                KfwdScheduleService.rtInfoLog.info("iN=" + p1 + KfwdLogUtils.getRtInfoLog(this.kfwdMatchInfoMap.get(p1)));
                KfwdScheduleService.rtInfoLog.info("iN=" + p2 + KfwdLogUtils.getRtInfoLog(this.kfwdMatchInfoMap.get(p2)));
            }
            matchList1 = null;
        }
        final List<KfwdRuntimeMatch> matchList2 = this.kfwdRuntimeMatchDao.getRunMatchByRound(KfwdScheduleService.curSeasonId, scheduleId, round);
        KfwdScheduleService.scheduleInfoLog.info("iniRound#" + KfwdScheduleService.curSeasonId + "#" + scheduleId + "#" + round + "#matchNum=" + matchList2.size());
        this.putNumtoRoundMatchNumMap(scheduleId, round, matchList2.size());
        if (matchList2.size() == 0) {
            this.self.runNextRoundMatch(KfwdScheduleService.curSeasonId, scheduleId, round + 1);
            return;
        }
        for (final KfwdRuntimeMatch match2 : matchList2) {
            this.runMatch(match2);
        }
        if (round >= KfwdConstantsAndMethod.MAXROUND + 1) {
            synchronized (this.scheduleNumLock) {
                --KfwdScheduleService.scheduleNum;
                KfwdScheduleService.lastSetGlobalStateTime = System.currentTimeMillis();
                KfwdScheduleService.oneScheduleToMaxRound = true;
                if (KfwdScheduleService.scheduleNum <= 0) {
                    KfwdScheduleService.globalstate = 7;
                }
                KfwdScheduleService.scheduleInfoLog.info("scheduleNum=" + KfwdScheduleService.scheduleNum + "scheduleId=" + scheduleId);
            }
            // monitorexit(this.scheduleNumLock)
        }
    }
    
    private void doIniRankingMap(final int seasonId, final int scheduleId, final int round) {
        final List<KfwdRuntimeResult> wdResultList = this.kfwdRuntimeResultDao.getSortResultByScheduleId(seasonId, scheduleId);
        Collections.sort(wdResultList, KfwdRuntimeResult.compare2);
        final KfwdRankingListInfo rankingInfo = new KfwdRankingListInfo();
        rankingInfo.setScheduleId(scheduleId);
        final List<KfwdRuntimeResultDto> rankingList = new ArrayList<KfwdRuntimeResultDto>();
        rankingInfo.setRankingList(rankingList);
        rankingInfo.setRound(round - 1);
        rankingInfo.setTotalRound(KfwdTimeControlService.getTotalRound());
        final int zb = KfwdTimeControlService.getZb();
        final int zbWarriorNum = 1 >> KfwdTimeControlService.getZbLayer();
        if (zb == 1) {
            rankingInfo.setZb(zb);
            rankingInfo.setZbWarriorNum(zbWarriorNum);
        }
        final int size = wdResultList.size();
        int matchId = 1;
        final HashMap<Integer, KfwdRuntimeResultDto> rankingMap = new HashMap<Integer, KfwdRuntimeResultDto>();
        final int day = KfwdTimeControlService.getAndCheckDayRewardRound(round);
        final List<KfwdPlayerInfo> topList = new ArrayList<KfwdPlayerInfo>();
        if (round == KfwdTimeControlService.getTotalRound() + 1) {
            for (int i = 0; i < 3 && i < wdResultList.size(); ++i) {
                final KfwdRuntimeResult rr = wdResultList.get(i);
                final KfwdPlayerInfo pInfo = new KfwdPlayerInfo();
                pInfo.setCompetitorId(rr.getCompetitorId());
                pInfo.setNation(rr.getNation());
                pInfo.setPlayerName(rr.getPlayerName());
                pInfo.setServerId(rr.getServerId());
                pInfo.setServerName(rr.getServerName());
                pInfo.setPos(i + 1);
                topList.add(pInfo);
            }
        }
        for (int i = 0; i < size; ++i) {
            final KfwdRuntimeResult wdResult1 = wdResultList.get(i);
            final KfwdRuntimeResultDto resDto = new KfwdRuntimeResultDto();
            resDto.setCompetitorId(wdResult1.getCompetitorId());
            resDto.setPlayerName(wdResult1.getPlayerName());
            resDto.setWinNum(wdResult1.getWinNum());
            resDto.setServerName(wdResult1.getServerName());
            resDto.setServerId(wdResult1.getServerId());
            resDto.setPlv(wdResult1.getPlv());
            resDto.setScore(wdResult1.getScore());
            resDto.setPos(i);
            final String dayScore = wdResult1.getDayScore();
            resDto.setDayScore((dayScore == null) ? KfwdRuntimeResult.EMPTYDAYSCOREINFO : dayScore);
            rankingList.add(resDto);
            rankingMap.put(wdResult1.getCompetitorId(), resDto);
            KfwdRuntimeResult wdResult2 = null;
            if (i + 1 < size) {
                wdResult2 = wdResultList.get(i + 1);
                final KfwdRuntimeResultDto resDto2 = new KfwdRuntimeResultDto();
                resDto2.setCompetitorId(wdResult2.getCompetitorId());
                resDto2.setPlayerName(wdResult2.getPlayerName());
                resDto2.setWinNum(wdResult2.getWinNum());
                resDto2.setServerName(wdResult2.getServerName());
                resDto2.setServerId(wdResult2.getServerId());
                resDto2.setScore(wdResult2.getScore());
                final String dayScore2 = wdResult2.getDayScore();
                resDto2.setDayScore((dayScore2 == null) ? KfwdRuntimeResult.EMPTYDAYSCOREINFO : dayScore2);
                resDto2.setPlv(wdResult2.getPlv());
                resDto2.setPos(i + 1);
                rankingList.add(resDto2);
                rankingMap.put(wdResult2.getCompetitorId(), resDto2);
                ++i;
                ++matchId;
            }
        }
        rankingInfo.setRankingMap(rankingMap);
        this.rankingListMap.put(scheduleId, rankingInfo);
    }
    
    private KfwdRTMatchInfo copyRtInfo(final KfwdRTMatchInfo kfwdRTMatchInfo) {
        if (kfwdRTMatchInfo == null) {
            return null;
        }
        final KfwdRTMatchInfo newInfo = new KfwdRTMatchInfo();
        newInfo.setCompetitorId1(kfwdRTMatchInfo.getCompetitorId1());
        newInfo.setCompetitorId2(kfwdRTMatchInfo.getCompetitorId2());
        newInfo.setHistoryRes1(kfwdRTMatchInfo.getHistoryRes1());
        newInfo.setHistoryRes2(kfwdRTMatchInfo.getHistoryRes2());
        newInfo.setInspire1(kfwdRTMatchInfo.getInspire1());
        newInfo.setInspire2(kfwdRTMatchInfo.getInspire2());
        newInfo.setLastReport(kfwdRTMatchInfo.getLastReport());
        newInfo.setLastRoundBattleTime(kfwdRTMatchInfo.getLastRoundBattleTime());
        newInfo.setMatchId(kfwdRTMatchInfo.getMatchId());
        newInfo.setNextShowSRoundCD(kfwdRTMatchInfo.getNextShowSRoundCD());
        newInfo.setNextSRoundCD(kfwdRTMatchInfo.getNextSRoundCD());
        newInfo.setRes(kfwdRTMatchInfo.getRes());
        newInfo.setRound(kfwdRTMatchInfo.getRound());
        newInfo.setRoundBattleTime(kfwdRTMatchInfo.getRoundBattleTime());
        newInfo.setScoreAndTicket(kfwdRTMatchInfo.getScore(), kfwdRTMatchInfo.getTicket());
        newInfo.setLastScore(kfwdRTMatchInfo.getLastScore());
        newInfo.setScheduleId(kfwdRTMatchInfo.getScheduleId());
        newInfo.setShowround(kfwdRTMatchInfo.getShowround());
        newInfo.setShowSRound(kfwdRTMatchInfo.getShowSRound());
        newInfo.setsRound(kfwdRTMatchInfo.getsRound());
        newInfo.setP1score(kfwdRTMatchInfo.getP1score());
        newInfo.setP1Ranking(kfwdRTMatchInfo.getP1Ranking());
        newInfo.setP2score(kfwdRTMatchInfo.getP2score());
        newInfo.setP2Ranking(kfwdRTMatchInfo.getP2Ranking());
        return newInfo;
    }
    
    @Override
    public void runNextRoundMatch(final int seasonId, final int scheduleId, final int nextRound) {
        KfwdScheduleService.scheduleInfoLog.info("schRound#" + KfwdScheduleService.curSeasonId + "#" + scheduleId + "#" + nextRound);
        if (nextRound > KfwdConstantsAndMethod.MAXROUND + 1) {
            return;
        }
        if (nextRound == KfwdConstantsAndMethod.MAXROUND + 1) {
            synchronized (this.scheduleNumLock) {
                --KfwdScheduleService.scheduleNum;
                KfwdScheduleService.lastSetGlobalStateTime = System.currentTimeMillis();
                KfwdScheduleService.oneScheduleToMaxRound = true;
                if (KfwdScheduleService.scheduleNum <= 0) {
                    KfwdScheduleService.globalstate = 7;
                }
                KfwdScheduleService.scheduleInfoLog.info("scheduleNum=" + KfwdScheduleService.scheduleNum + "scheduleId=" + scheduleId);
            }
            // monitorexit(this.scheduleNumLock)
        }
        final long delay = KfwdTimeControlService.getRunDelayMillSecondsByRound(nextRound, scheduleId);
        KfwdScheduleService.scheduleInfoLog.info("schRound#" + KfwdScheduleService.curSeasonId + "#" + scheduleId + "#" + nextRound + "#" + new Date(System.currentTimeMillis() + delay));
        this.exeutors.schedule(new Runnable() {
            @Override
            public void run() {
                KfwdScheduleService.scheduleInfoLog.info("runRound#" + KfwdScheduleService.curSeasonId + "#" + scheduleId + "#" + nextRound);
                KfwdScheduleService.this.self.scheduleMatchRound(seasonId, scheduleId, nextRound);
                KfwdScheduleService.scheduleInfoLog.info("finRound#" + KfwdScheduleService.curSeasonId + "#" + scheduleId + "#" + nextRound);
            }
        }, delay, TimeUnit.MILLISECONDS);
    }
    
    @Override
    public KfwdSignResult processNationRegist(final String gameServer, final KfwdSignInfoParam signInfo, final boolean isSignUp) {
        final int sPId = signInfo.getPlayerInfo().getPlayerId();
        final Integer sCId = signInfo.getPlayerInfo().getCompetitorId();
        Integer state = null;
        if (KfwdTimeControlService.getNowStateAndCD() != null) {
            state = KfwdTimeControlService.getNowStateAndCD().left;
        }
        if (KfwdScheduleService.curSeasonId == 0 || state == null) {
            final KfwdSignResult res = new KfwdSignResult();
            res.setPlayerId(sPId);
            res.setCompetitor(sCId);
            res.setState(0);
            KfwdScheduleService.commonLog.info("syn state null");
            return res;
        }
        if (state != null && state >= 30 && (isSignUp || this.inBattlePrePareTimeDefLock(sCId))) {
            final KfwdSignResult res = new KfwdSignResult();
            res.setPlayerId(sPId);
            res.setCompetitor(sCId);
            res.setState(0);
            return res;
        }
        final KfwdSignResult res = this.kfwdMatchService.signUp(gameServer, signInfo, KfwdScheduleService.curSeasonId, isSignUp);
        return res;
    }
    
    private boolean inBattlePrePareTime(final Integer sCId) {
        if (sCId == null) {
            return true;
        }
        final long now = System.currentTimeMillis();
        final KfwdRTMatchInfo rtInfo = this.kfwdMatchInfoMap.get(sCId);
        if (rtInfo == null) {
            return true;
        }
        final Date battleTime = rtInfo.getRoundBattleTime();
        if (battleTime == null) {
            return true;
        }
        final long cd = battleTime.getTime() - now;
        return cd < 30000L;
    }
    
    private boolean inBattlePrePareTimeDefLock(final Integer sCId) {
        if (sCId == null) {
            return true;
        }
        final long now = System.currentTimeMillis();
        final KfwdRTMatchInfo rtInfo = this.kfwdMatchInfoMap.get(sCId);
        if (rtInfo == null) {
            return true;
        }
        final Date battleTime = rtInfo.getRoundBattleTime();
        if (battleTime == null) {
            return true;
        }
        final long cd = battleTime.getTime() - now;
        if (cd < 8000L) {
            KfwdScheduleService.commonLog.info("cId=" + sCId + "#round=" + rtInfo.getRound() + "#sround=" + rtInfo.getsRound() + "#inBattlePrePareTime#time=" + new Date() + "#battleTime=" + battleTime);
            return true;
        }
        return false;
    }
    
    @Override
    public void scheduleMatchRound(final int seasonId, final int scheduleId, final int round) {
        try {
            final List<KfwdRuntimeMatch> matchList = this.self.createNewRoundRunTimeMatch(seasonId, scheduleId, round);
            KfwdScheduleService.scheduleInfoLog.info("preRound#" + KfwdScheduleService.curSeasonId + "#" + scheduleId + "#" + round + "#matchNum=" + matchList.size());
            if (matchList.size() == 0) {
                synchronized (this.scheduleNumLock) {
                    --KfwdScheduleService.scheduleNum;
                    if (KfwdScheduleService.scheduleNum <= 0) {
                        KfwdScheduleService.globalstate = 7;
                    }
                    KfwdScheduleService.scheduleInfoLog.info("scheduleNum=" + KfwdScheduleService.scheduleNum + "scheduleId=" + scheduleId);
                }
                // monitorexit(this.scheduleNumLock)
            }
            this.putNumtoRoundMatchNumMap(scheduleId, round, matchList.size());
            for (final KfwdRuntimeMatch match : matchList) {
                this.runMatch(match);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void putNumtoRoundMatchNumMap(final int scheduleId, final int round, final int size) {
        synchronized (this.roundMatchNumlock) {
            HashMap<Integer, Integer> sMap = this.roundMatchNumMap.get(scheduleId);
            if (sMap == null) {
                sMap = new HashMap<Integer, Integer>();
                this.roundMatchNumMap.put(scheduleId, sMap);
            }
            sMap.put(round, size);
        }
        // monitorexit(this.roundMatchNumlock)
    }
    
    private int minusRoundMatchNum(final int scheduleId, final int round) {
        synchronized (this.roundMatchNumlock) {
            HashMap<Integer, Integer> sMap = this.roundMatchNumMap.get(scheduleId);
            if (sMap == null) {
                sMap = new HashMap<Integer, Integer>();
                this.roundMatchNumMap.put(scheduleId, sMap);
            }
            int remainBattle = sMap.get(round);
            --remainBattle;
            sMap.put(round, remainBattle);
            // monitorexit(this.roundMatchNumlock)
            return remainBattle;
        }
    }
    
    private void runMatch(final KfwdRuntimeMatch match) {
        KfwdScheduleService.scheduleInfoLog.info("schMatch" + KfwdLogUtils.getMachInfo(match));
        this.makeNewRtMatchInfoByNewMatch(match);
        if (match.getRound() == KfwdConstantsAndMethod.MAXROUND + 1) {
            return;
        }
        final long delay = KfwdTimeControlService.getMatchDelay(match);
        this.exeutors.schedule(new Runnable() {
            @Override
            public void run() {
                final KfwdRuntimeMatchResult resMatch = null;
                try {
                    if (match.getSeasonId() != KfwdScheduleService.curSeasonId) {
                        return;
                    }
                    KfwdScheduleService.scheduleInfoLog.info("runMatch" + KfwdLogUtils.getMachInfo(match));
                    KfwdScheduleService.this.kfwdMatchService.runMatch(match);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, delay, TimeUnit.MILLISECONDS);
    }
    
    @Override
    public void doFinishMatch(final KfwdRuntimeMatch resMatch) {
        KfwdScheduleService.scheduleInfoLog.info("aftMatch" + KfwdLogUtils.getMachInfo(resMatch));
        this.self.buildNewMatchAndRun(resMatch);
    }
    
    private void makeNewRtMatchInfoByNewMatch(final KfwdRuntimeMatch match) {
        if (match == null) {
            return;
        }
        final int p1 = match.getPlayer1Id();
        final int p2 = match.getPlayer2Id();
        KfwdRTMatchInfo mInfo1 = this.copyRtInfo(this.kfwdMatchInfoMap.get(p1));
        if (mInfo1 == null) {
            mInfo1 = new KfwdRTMatchInfo();
            mInfo1.setScheduleId(match.getScheduleId());
        }
        KfwdRTMatchInfo mInfo2 = this.copyRtInfo(this.kfwdMatchInfoMap.get(p2));
        if (mInfo2 == null) {
            mInfo2 = new KfwdRTMatchInfo();
            mInfo2.setScheduleId(match.getScheduleId());
        }
        final RewardInfo rInfo = KfwdTimeControlService.getRewardInfoBuyScheduleId(match.getScheduleId());
        int ticket = KfwdConstantsAndMethod.getTicketByScore(rInfo.getBasicScore(), rInfo.getWinCoef(), match.getP1Score());
        final KfwdRTMatchInfo mInfo2Temp = this.copyRtInfo(mInfo2);
        if (match.getsRound() == 1) {
            final int oldRound = mInfo1.getRound();
            final boolean lastIsAtt = mInfo1.getCompetitorId1() == p1;
            final boolean round3IsAtt = KfwdConstantsAndMethod.isC1AttackerRound3(match.getMatchId(), match.getRound(), match.getScheduleId());
            final int lastBattleRes = mInfo1.getRes();
            final int lastp1Inspire = mInfo1.getInspire1();
            final int lastp2Inspire = mInfo1.getInspire2();
            if (mInfo1.getScore() < match.getP1Score()) {
                mInfo1.setLastScore(mInfo1.getScore());
            }
            final long lastHisRes = lastIsAtt ? mInfo1.getHistoryRes1() : mInfo1.getHistoryRes2();
            if (match.getRound() > 1) {
                mInfo1.addLastRoundInfo(lastHisRes, lastIsAtt, round3IsAtt, lastBattleRes, lastp1Inspire, lastp2Inspire, true);
                if (p2 != 0) {
                    mInfo2.addLastRoundInfo(lastHisRes, lastIsAtt, round3IsAtt, lastBattleRes, lastp1Inspire, lastp2Inspire, true);
                }
            }
            mInfo1.setRes(0);
            if (oldRound != match.getRound()) {
                mInfo1.setInspire1(0);
                mInfo1.setInspire2(0);
            }
        }
        mInfo1.setCompetitorId1(p1);
        mInfo1.setCompetitorId2(p2);
        mInfo1.setMatchId(match.getMatchId());
        mInfo1.setP1Ranking(match.getP1pos());
        mInfo1.setP2Ranking(match.getP2pos());
        mInfo1.setRound(match.getRound());
        mInfo1.setsRound(match.getsRound());
        mInfo1.setP1score(match.getP1Score());
        mInfo1.setP2score(match.getP2Score());
        mInfo1.setScoreAndTicket(match.getP1Score(), ticket);
        if (mInfo1.getRoundBattleTime() != null) {
            long lastRoundTime1 = mInfo1.getRoundBattleTime().getTime();
            if (match.getStartTime().getTime() - lastRoundTime1 < 100000L) {
                int lastRound = match.getRound();
                int lastSround = match.getsRound();
                if (lastSround > 1) {
                    --lastSround;
                }
                else {
                    lastSround = 1;
                    --lastRound;
                }
                if (lastRound == 0) {
                    lastRoundTime1 = 0L;
                }
                else {
                    lastRoundTime1 = KfwdTimeControlService.getRunMatchTime(lastRound, lastSround, match.getMatchId(), match.getScheduleId()).getTime();
                }
            }
            if (lastRoundTime1 > 0L) {
                mInfo1.setLastRoundBattleTime(new Date(lastRoundTime1));
            }
            else {
                mInfo1.setLastRoundBattleTime((Date)null);
            }
        }
        mInfo1.setRoundBattleTime(match.getStartTime());
        if (p2 != 0) {
            if (match.getsRound() == 1) {
                final int oldRound = mInfo2.getRound();
                final boolean lastIsAtt = mInfo2Temp.getCompetitorId1() == p2;
                final boolean round3IsAtt = KfwdConstantsAndMethod.isC1AttackerRound3(match.getMatchId(), match.getRound(), match.getScheduleId());
                if (mInfo2.getScore() < match.getP1Score()) {
                    mInfo2.setLastScore(mInfo2.getScore());
                }
                final int lastBattleRes = mInfo2Temp.getRes();
                final int lastp1Inspire = mInfo2Temp.getInspire1();
                final int lastp2Inspire = mInfo2Temp.getInspire2();
                final long lastHisRes = lastIsAtt ? mInfo2Temp.getHistoryRes1() : mInfo2Temp.getHistoryRes2();
                if (match.getRound() > 1) {
                    mInfo2.addLastRoundInfo(lastHisRes, lastIsAtt, round3IsAtt, lastBattleRes, lastp1Inspire, lastp2Inspire, false);
                    mInfo1.addLastRoundInfo(lastHisRes, lastIsAtt, round3IsAtt, lastBattleRes, lastp1Inspire, lastp2Inspire, false);
                }
                mInfo2.setRes(0);
                if (oldRound != match.getRound()) {
                    mInfo2.setInspire1(0);
                    mInfo2.setInspire2(0);
                }
            }
            mInfo2.setCompetitorId1(p1);
            mInfo2.setCompetitorId2(p2);
            mInfo2.setMatchId(match.getMatchId());
            mInfo2.setP1Ranking(match.getP1pos());
            mInfo2.setP2Ranking(match.getP2pos());
            mInfo2.setP1score(match.getP1Score());
            mInfo2.setP2score(match.getP2Score());
            mInfo2.setRound(match.getRound());
            mInfo2.setsRound(match.getsRound());
            ticket = KfwdConstantsAndMethod.getTicketByScore(rInfo.getBasicScore(), rInfo.getWinCoef(), match.getP2Score());
            mInfo2.setScoreAndTicket(match.getP1Score(), ticket);
            if (mInfo2.getRoundBattleTime() != null) {
                long lastRoundTime2 = mInfo2.getRoundBattleTime().getTime();
                if (match.getStartTime().getTime() - lastRoundTime2 < 100000L) {
                    int lastRound = match.getRound();
                    int lastSround = match.getsRound();
                    if (lastSround > 1) {
                        --lastSround;
                    }
                    else {
                        lastSround = 1;
                        --lastRound;
                    }
                    if (lastRound == 0) {
                        lastRoundTime2 = 0L;
                    }
                    else {
                        lastRoundTime2 = KfwdTimeControlService.getRunMatchTime(lastRound, lastSround, match.getMatchId(), match.getScheduleId()).getTime();
                    }
                }
                if (lastRoundTime2 > 0L) {
                    mInfo2.setLastRoundBattleTime(new Date(lastRoundTime2));
                }
                else {
                    mInfo2.setLastRoundBattleTime((Date)null);
                }
            }
            mInfo2.setRoundBattleTime(match.getStartTime());
            this.kfwdMatchInfoMap.put(p2, mInfo2);
            KfwdScheduleService.rtInfoLog.info("mN=" + p2 + KfwdLogUtils.getRtInfoLog(this.kfwdMatchInfoMap.get(p2)));
            this.sendRtInfo(p2);
        }
        this.sendRtInfo(p1);
        this.kfwdMatchInfoMap.put(p1, mInfo1);
        KfwdScheduleService.rtInfoLog.info("mN=" + p1 + KfwdLogUtils.getRtInfoLog(this.kfwdMatchInfoMap.get(p1)));
    }
    
    private void sendRtInfo(final int competitorId) {
        if (competitorId > 0) {
            KfwdBuilder.sendMsgToOne(competitorId, this.getPlayerMatchInfo(competitorId).left);
        }
    }
    
    @Transactional
    @Override
    public List<KfwdRuntimeMatch> createNewRoundRunTimeMatch(final int seasonId, final int scheduleId, final int round) {
        final List<KfwdRuntimeMatch> matchList = new ArrayList<KfwdRuntimeMatch>();
        final List<KfwdRuntimeResult> wdResultList = this.kfwdRuntimeResultDao.getSortResultByScheduleId(seasonId, scheduleId);
        Collections.sort(wdResultList, KfwdRuntimeResult.compare2);
        final KfwdRankingListInfo rankingInfo = new KfwdRankingListInfo();
        rankingInfo.setScheduleId(scheduleId);
        final List<KfwdRuntimeResultDto> rankingList = new ArrayList<KfwdRuntimeResultDto>();
        rankingInfo.setRankingList(rankingList);
        rankingInfo.setRound(round - 1);
        rankingInfo.setTotalRound(KfwdTimeControlService.getTotalRound());
        final int zb = KfwdTimeControlService.getZb();
        final int zbWarriorNum = 1 >> KfwdTimeControlService.getZbLayer();
        if (zb == 1) {
            rankingInfo.setZb(zb);
            rankingInfo.setZbWarriorNum(zbWarriorNum);
        }
        final int size = wdResultList.size();
        int matchId = 1;
        final HashMap<Integer, KfwdRuntimeResultDto> rankingMap = new HashMap<Integer, KfwdRuntimeResultDto>();
        final int day = KfwdTimeControlService.getAndCheckDayRewardRound(round);
        final List<KfwdPlayerInfo> topList = new ArrayList<KfwdPlayerInfo>();
        if (round == KfwdTimeControlService.getTotalRound() + 1) {
            for (int i = 0; i < 3 && i < wdResultList.size(); ++i) {
                final KfwdRuntimeResult rr = wdResultList.get(i);
                final KfwdPlayerInfo pInfo = new KfwdPlayerInfo();
                pInfo.setCompetitorId(rr.getCompetitorId());
                pInfo.setNation(rr.getNation());
                pInfo.setPlayerName(rr.getPlayerName());
                pInfo.setServerId(rr.getServerId());
                pInfo.setServerName(rr.getServerName());
                pInfo.setPos(i + 1);
                topList.add(pInfo);
            }
        }
        for (int i = 0; i < size; ++i, ++matchId, ++i) {
            final KfwdRuntimeResult wdResult1 = wdResultList.get(i);
            final KfwdRuntimeResultDto resDto = new KfwdRuntimeResultDto();
            resDto.setCompetitorId(wdResult1.getCompetitorId());
            resDto.setPlayerName(wdResult1.getPlayerName());
            resDto.setWinNum(wdResult1.getWinNum());
            resDto.setServerName(wdResult1.getServerName());
            resDto.setServerId(wdResult1.getServerId());
            resDto.setScore(wdResult1.getScore());
            resDto.setPlv(wdResult1.getPlv());
            resDto.setPos(i);
            final String dayScore = wdResult1.getDayScore();
            resDto.setDayScore((dayScore == null) ? KfwdRuntimeResult.EMPTYDAYSCOREINFO : dayScore);
            rankingList.add(resDto);
            rankingMap.put(wdResult1.getCompetitorId(), resDto);
            if (day > 0) {
                final KfwdTicketReward ticReward1 = this.kfwdCacheManager.getTicketInfo(wdResult1.getCompetitorId());
                if (ticReward1 != null) {
                    ticReward1.addDayRanking(day, i + 1);
                    this.kfwdTicketRewardDao.update((IModel)ticReward1);
                    this.kfwdCacheManager.putIntoCache(ticReward1);
                    KfwdTicketRewardNoticeInfo.addNoticeInfo(ticReward1);
                }
            }
            KfwdRuntimeResult wdResult2 = null;
            if (i + 1 < size) {
                wdResult2 = wdResultList.get(i + 1);
                final KfwdRuntimeResultDto resDto2 = new KfwdRuntimeResultDto();
                resDto2.setCompetitorId(wdResult2.getCompetitorId());
                resDto2.setPlayerName(wdResult2.getPlayerName());
                resDto2.setWinNum(wdResult2.getWinNum());
                resDto2.setServerName(wdResult2.getServerName());
                resDto2.setServerId(wdResult2.getServerId());
                resDto2.setScore(wdResult2.getScore());
                final String dayScore2 = wdResult2.getDayScore();
                resDto2.setDayScore((dayScore2 == null) ? KfwdRuntimeResult.EMPTYDAYSCOREINFO : dayScore2);
                resDto2.setPlv(wdResult2.getPlv());
                resDto2.setPos(i + 1);
                rankingList.add(resDto2);
                rankingMap.put(wdResult2.getCompetitorId(), resDto2);
                if (day > 0) {
                    final KfwdTicketReward ticReward2 = this.kfwdCacheManager.getTicketInfo(wdResult2.getCompetitorId());
                    if (ticReward2 != null) {
                        ticReward2.addDayRanking(day, i + 2);
                        this.kfwdTicketRewardDao.update((IModel)ticReward2);
                        this.kfwdCacheManager.putIntoCache(ticReward2);
                        KfwdTicketRewardNoticeInfo.addNoticeInfo(ticReward2);
                    }
                }
            }
        }
        matchId = 1;
        final Set<Integer> cIdSet = new HashSet<Integer>();
        for (int j = 0; j < size; ++j) {
            final KfwdRuntimeResult wdResult3 = wdResultList.get(j);
            if (!cIdSet.contains(wdResult3.getCompetitorId())) {
                List<Integer> lastCIds = wdResult3.getLastBattleCIdSet();
                if (lastCIds.size() > 5) {
                    lastCIds = lastCIds.subList(lastCIds.size() - 5, lastCIds.size());
                }
                final int nextOpp = j + 1;
                KfwdRuntimeResult wdResult4 = null;
                KfwdRuntimeResult wdResult2Bak = null;
                for (int oppId = nextOpp; oppId < size; ++oppId) {
                    final KfwdRuntimeResult wdResult5 = wdResultList.get(oppId);
                    final int oppCId = wdResult5.getCompetitorId();
                    if (!cIdSet.contains(oppCId)) {
                        if (!lastCIds.contains(oppCId)) {
                            wdResult4 = wdResult5;
                            break;
                        }
                        if (wdResult2Bak == null) {
                            wdResult2Bak = wdResult5;
                        }
                    }
                }
                if (wdResult4 == null && wdResult2Bak != null) {
                    wdResult4 = wdResult2Bak;
                }
                cIdSet.add(wdResult3.getCompetitorId());
                if (wdResult4 != null) {
                    cIdSet.add(wdResult4.getCompetitorId());
                }
                final int player1Id = wdResult3.getCompetitorId();
                final int player2Id = (wdResult4 == null) ? 0 : wdResult4.getCompetitorId();
                final KfwdRuntimeMatch newMatch = new KfwdRuntimeMatch();
                newMatch.setPlayer1Id(player1Id);
                newMatch.setP1Score(wdResult3.getScore());
                newMatch.setPlayer2Id(player2Id);
                newMatch.setP2Score((wdResult4 == null) ? 0 : wdResult4.getScore());
                newMatch.setPlayer1Win(0);
                newMatch.setPlayer2Win(0);
                newMatch.setRound(round);
                newMatch.setScheduleId(scheduleId);
                newMatch.setsRound(1);
                newMatch.setsRoundWinner(0);
                newMatch.setWinnerId(0);
                newMatch.setSeasonId(seasonId);
                final KfwdRuntimeResultDto r1 = rankingMap.get(player1Id);
                if (r1 != null) {
                    newMatch.setP1pos(r1.getPos() + 1);
                }
                final KfwdRuntimeResultDto r2 = rankingMap.get(player2Id);
                if (r2 != null && player2Id > 0) {
                    newMatch.setP2pos(r2.getPos() + 1);
                }
                if (player2Id == 0) {
                    newMatch.setsRoundWinner(player1Id);
                    newMatch.setWinnerId(player1Id);
                }
                newMatch.setStartTime(KfwdTimeControlService.getRunMatchTime(round, 1, matchId, scheduleId));
                newMatch.setMatchId(matchId);
                this.kfwdRuntimeMatchDao.create((IModel)newMatch);
                matchList.add(newMatch);
                ++matchId;
            }
        }
        this.rankingListMap.put(scheduleId, rankingInfo);
        rankingInfo.setRankingMap(rankingMap);
        if (round > 1) {
            final int nextDay = KfwdTimeControlService.getDayByRound(round);
            final int lastDay = KfwdTimeControlService.getDayByRound(round - 1);
            if (nextDay > lastDay) {
                KfwdDayBattleEndNoticeInfo.addDayResult(seasonId, scheduleId, lastDay, topList);
            }
        }
        this.rankingListMap.put(scheduleId, rankingInfo);
        return matchList;
    }
    
    @Override
    public void buildNewMatchAndRun(final KfwdRuntimeMatch resMatch) {
        this.modifyRtMatchInfoByMatchRes(resMatch);
        if (resMatch.getsRound() == 1) {
            final int reMainNum = this.minusRoundMatchNum(resMatch.getScheduleId(), resMatch.getRound());
            KfwdScheduleService.scheduleInfoLog.info("finMatch" + KfwdLogUtils.getMachInfo(resMatch) + "#remain=" + reMainNum);
            if (reMainNum <= 0) {
                this.self.runNextRoundMatch(resMatch.getSeasonId(), resMatch.getScheduleId(), resMatch.getRound() + 1);
            }
            return;
        }
        if (resMatch.getsRound() < 1) {
            final KfwdRuntimeMatch newMatch = this.self.buildNewMatch(resMatch);
            this.runMatch(newMatch);
        }
    }
    
    private void modifyRtMatchInfoByMatchRes(final KfwdRuntimeMatch resMatch) {
        final int p1 = resMatch.getPlayer1Id();
        final int p2 = resMatch.getPlayer2Id();
        final KfwdRTMatchInfo rtInfo1 = this.copyRtInfo(this.kfwdMatchInfoMap.get(p1));
        final KfwdRTMatchInfo rtInfo2 = this.copyRtInfo(this.kfwdMatchInfoMap.get(p2));
        if (resMatch.getsRound() == 1) {
            if (resMatch.getWinnerId() == p1) {
                if (rtInfo1 != null) {
                    rtInfo1.addHisRoundBattleRes(resMatch.getRound(), true);
                }
                if (rtInfo2 != null) {
                    rtInfo2.addHisRoundBattleRes(resMatch.getRound(), true);
                }
            }
            else {
                if (rtInfo1 != null) {
                    rtInfo1.addHisRoundBattleRes(resMatch.getRound(), false);
                }
                if (rtInfo2 != null) {
                    rtInfo2.addHisRoundBattleRes(resMatch.getRound(), false);
                }
            }
        }
        if (resMatch.getsRoundWinner() == resMatch.getPlayer1Id()) {
            if (rtInfo1 != null) {
                rtInfo1.addsRoundBattleRes(resMatch.getsRound(), 1);
                rtInfo1.setLastReport(resMatch.getReportId());
            }
            if (rtInfo2 != null) {
                rtInfo2.addsRoundBattleRes(resMatch.getsRound(), 1);
                rtInfo2.setLastReport(resMatch.getReportId());
            }
        }
        else {
            if (rtInfo1 != null) {
                rtInfo1.addsRoundBattleRes(resMatch.getsRound(), 2);
                rtInfo1.setLastReport(resMatch.getReportId());
            }
            if (rtInfo2 != null) {
                rtInfo2.addsRoundBattleRes(resMatch.getsRound(), 2);
                rtInfo2.setLastReport(resMatch.getReportId());
            }
        }
        this.kfwdMatchInfoMap.put(p1, rtInfo1);
        if (p2 != 0) {
            this.kfwdMatchInfoMap.put(p2, rtInfo2);
        }
        KfwdScheduleService.rtInfoLog.info("aW=" + p1 + KfwdLogUtils.getRtInfoLog(this.kfwdMatchInfoMap.get(p1)));
        KfwdScheduleService.rtInfoLog.info("aW=" + p2 + KfwdLogUtils.getRtInfoLog(this.kfwdMatchInfoMap.get(p2)));
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        this.self = (IKfwdScheduleService)this.context.getBean("kfwdScheduleService");
    }
    
    @Override
	public void setApplicationContext(final ApplicationContext arg0) throws BeansException {
        this.context = arg0;
    }
    
    @Override
    public void newNextRoundMatch(final int getsRound) {
    }
    
    @Transactional
    @Override
    public KfwdRuntimeMatch buildNewMatch(final KfwdRuntimeMatch resMatch) {
        if (resMatch != null) {
            final KfwdRuntimeMatch newMatch = new KfwdRuntimeMatch();
            BeanUtils.copyProperties(resMatch, newMatch);
            newMatch.setReportId(null);
            newMatch.setsRound(resMatch.getsRound() + 1);
            newMatch.setsRoundWinner(0);
            newMatch.setStartTime(KfwdTimeControlService.getRunMatchTime(newMatch.getRound(), newMatch.getsRound(), newMatch.getMatchId(), newMatch.getScheduleId()));
            newMatch.setPk(null);
            newMatch.setP1Score(resMatch.getP1Score());
            newMatch.setP2Score(resMatch.getP2Score());
            newMatch.setP1pos(resMatch.getP1pos());
            newMatch.setP2pos(resMatch.getP2pos());
            this.kfwdRuntimeMatchDao.create((IModel)newMatch);
            KfwdScheduleService.scheduleInfoLog.info("buiMatch" + KfwdLogUtils.getMachInfo(newMatch));
            return newMatch;
        }
        return null;
    }
    
    @Override
    public void buildIniNewMatch(final KfwdRuntimeMatch match) {
    }
    
    @Transactional
    @Override
    public boolean processInspire(final KfwdRTInspire rtInspire) {
        if (!this.hasScheduledMatch()) {
            return false;
        }
        if (this.inBattlePrePareTime(rtInspire.getCompetitorId())) {
            return false;
        }
        if (rtInspire.getRound() == 0) {
            return false;
        }
        final int selfcId = rtInspire.getCompetitorId();
        KfwdRuntimeInspire inspire = this.kfwdRuntimeInspireDao.getInspire(KfwdScheduleService.curSeasonId, selfcId, rtInspire.getRound());
        final int inspireValue = KfwdConstantsAndMethod.getInspireValue(rtInspire.getAttNum(), rtInspire.getDefNum());
        if (inspire == null) {
            inspire = new KfwdRuntimeInspire();
            BeanUtils.copyProperties(rtInspire, inspire);
            inspire.setSeasonId(KfwdScheduleService.curSeasonId);
            this.kfwdRuntimeInspireDao.create((IModel)inspire);
            this.kfwdCacheManager.putIntoCache(inspire);
            final KfwdRTMatchInfo selfMatchInfo = this.kfwdMatchInfoMap.get(selfcId);
            if (selfMatchInfo != null) {
                if (selfMatchInfo.getCompetitorId1() == selfcId) {
                    selfMatchInfo.setInspire1(inspireValue);
                    final KfwdRTMatchInfo targetMatchInfo = this.kfwdMatchInfoMap.get(selfMatchInfo.getCompetitorId2());
                    if (targetMatchInfo != null) {
                        targetMatchInfo.setInspire1(inspireValue);
                    }
                }
                else {
                    selfMatchInfo.setInspire2(inspireValue);
                    final KfwdRTMatchInfo targetMatchInfo = this.kfwdMatchInfoMap.get(selfMatchInfo.getCompetitorId1());
                    if (targetMatchInfo != null) {
                        targetMatchInfo.setInspire2(inspireValue);
                    }
                }
            }
        }
        else {
            if (rtInspire.getAttNum() + rtInspire.getDefNum() <= inspire.getAttNum() + inspire.getDefNum()) {
                return true;
            }
            inspire.setAttNum(rtInspire.getAttNum());
            inspire.setDefNum(rtInspire.getDefNum());
            this.kfwdRuntimeInspireDao.update((IModel)inspire);
            this.kfwdCacheManager.putIntoCache(inspire);
            final KfwdRTMatchInfo selfMatchInfo = this.kfwdMatchInfoMap.get(selfcId);
            if (selfMatchInfo != null) {
                if (selfMatchInfo.getCompetitorId1() == selfcId) {
                    selfMatchInfo.setInspire1(inspireValue);
                    final KfwdRTMatchInfo targetMatchInfo = this.kfwdMatchInfoMap.get(selfMatchInfo.getCompetitorId2());
                    if (targetMatchInfo != null) {
                        targetMatchInfo.setInspire1(inspireValue);
                    }
                }
                else {
                    selfMatchInfo.setInspire2(inspireValue);
                    final KfwdRTMatchInfo targetMatchInfo = this.kfwdMatchInfoMap.get(selfMatchInfo.getCompetitorId1());
                    if (targetMatchInfo != null) {
                        targetMatchInfo.setInspire2(inspireValue);
                    }
                }
            }
        }
        return true;
    }
    
    @Override
    public KfwdRTMatchInfo getRTMatchInfo(final KfwdPlayerKey pKey) {
        if (!this.hasScheduledMatch()) {
            return null;
        }
        final KfwdRTMatchInfo rtInfo = this.kfwdMatchInfoMap.get(pKey.getCompetitorId());
        if (rtInfo == null) {
            return null;
        }
        final long nowTime = System.currentTimeMillis();
        final Date bTime = rtInfo.getRoundBattleTime();
        final Date lastBTime = rtInfo.getLastRoundBattleTime();
        if (bTime != null) {
            long cd = bTime.getTime() - nowTime;
            if (cd < 0L) {
                cd = 0L;
            }
            rtInfo.setNextSRoundCD(cd);
            if (lastBTime != null) {
                final long lastcd = lastBTime.getTime() - nowTime + 30000L;
                if (lastcd < 30000L && lastcd > 0L) {
                    rtInfo.setNextShowSRoundCD(lastcd);
                    if (rtInfo.getsRound() > 1) {
                        rtInfo.setShowSRound(rtInfo.getsRound() - 1);
                        rtInfo.setShowround(rtInfo.getRound());
                    }
                    else {
                        rtInfo.setShowSRound(1);
                        rtInfo.setShowround(rtInfo.getRound() - 1);
                    }
                }
                else {
                    rtInfo.setNextShowSRoundCD(cd + 30000L);
                    rtInfo.setShowSRound(rtInfo.getsRound());
                    rtInfo.setShowround(rtInfo.getRound());
                }
            }
            else {
                rtInfo.setNextShowSRoundCD(cd + 30000L);
                rtInfo.setShowSRound(rtInfo.getsRound());
                rtInfo.setShowround(rtInfo.getRound());
            }
        }
        return rtInfo;
    }
    
    private boolean hasScheduledMatch() {
        return KfwdScheduleService.curSeasonId != 0 && KfwdScheduleService.globalstate >= 3;
    }
    
    @Override
    public KfwdRTDisPlayInfo getRTDisPlayerInfo(final KfwdPlayerKey key) {
        if (!this.hasScheduledMatch()) {
            return null;
        }
        final KfwdRTDisPlayInfo disPlayerInfo = new KfwdRTDisPlayInfo();
        final KfwdRTMatchInfo rtInfo = this.kfwdMatchInfoMap.get(key.getCompetitorId());
        if (rtInfo == null) {
            return null;
        }
        final int player1Id = rtInfo.getCompetitorId1();
        final int player2Id = rtInfo.getCompetitorId2();
        disPlayerInfo.setMatchId(rtInfo.getMatchId());
        disPlayerInfo.setRound(rtInfo.getRound());
        KfwdBattleWarrior w1 = this.kfwdCacheManager.getBattleWarrior(player1Id);
        if (w1 == null) {
            System.out.println("MIss w" + player1Id);
            w1 = (KfwdBattleWarrior)this.kfwdBattleWarriorDao.read((Serializable)player1Id);
            this.kfwdCacheManager.putIntoCache(w1);
        }
        final KfwdPlayerInfo kfwdPInfo1 = new KfwdPlayerInfo();
        BeanUtils.copyProperties(w1, kfwdPInfo1);
        kfwdPInfo1.setPos(rtInfo.getP1Ranking());
        kfwdPInfo1.setScore(rtInfo.getP1score());
        kfwdPInfo1.setCompetitorId(w1.getCompetitorId());
        kfwdPInfo1.setPlayerLevel(w1.getPlayerLevel());
        disPlayerInfo.setpInfo1(kfwdPInfo1);
        KfwdBattleWarriorGeneral g1 = this.kfwdCacheManager.getBattleWarriorGeneral(player1Id);
        if (g1 == null) {
            System.out.println("MIss g" + player1Id);
            g1 = (KfwdBattleWarriorGeneral)this.kfwdBattleWarriorGeneralDao.read((Serializable)player1Id);
            this.kfwdCacheManager.putIntoCache(g1);
        }
        final CampArmyParam[] campArmys1 = g1.getCampList();
        final KfwdGInfo gInfo1 = this.getGInfoFromCampArmys(campArmys1);
        disPlayerInfo.setgInfo1(gInfo1);
        if (player2Id > 0) {
            KfwdBattleWarrior w2 = this.kfwdCacheManager.getBattleWarrior(player2Id);
            if (w2 == null) {
                System.out.println("MIss w" + player2Id);
                w2 = (KfwdBattleWarrior)this.kfwdBattleWarriorDao.read((Serializable)player2Id);
                this.kfwdCacheManager.putIntoCache(w2);
            }
            final KfwdPlayerInfo kfwdPInfo2 = new KfwdPlayerInfo();
            BeanUtils.copyProperties(w2, kfwdPInfo2);
            kfwdPInfo2.setPos(rtInfo.getP2Ranking());
            kfwdPInfo2.setScore(rtInfo.getP2score());
            disPlayerInfo.setpInfo2(kfwdPInfo2);
            KfwdBattleWarriorGeneral g2 = this.kfwdCacheManager.getBattleWarriorGeneral(player2Id);
            if (g2 == null) {
                System.out.println("MIss g" + player2Id);
                g2 = (KfwdBattleWarriorGeneral)this.kfwdBattleWarriorGeneralDao.read((Serializable)player2Id);
                this.kfwdCacheManager.putIntoCache(g2);
            }
            final CampArmyParam[] campArmys2 = g2.getCampList();
            final KfwdGInfo gInfo2 = this.getGInfoFromCampArmys(campArmys2);
            disPlayerInfo.setgInfo2(gInfo2);
        }
        return disPlayerInfo;
    }
    
    private KfwdGInfo getGInfoFromCampArmys(final CampArmyParam[] campArmys1) {
        final KfwdGInfo gInfo1 = new KfwdGInfo();
        final KfwdSimpleGInfo[] gInfoList1 = new KfwdSimpleGInfo[campArmys1.length];
        for (int i = 0; i < campArmys1.length; ++i) {
            final CampArmyParam cap = campArmys1[i];
            if (cap != null) {
                final KfwdSimpleGInfo sgInfo = new KfwdSimpleGInfo();
                sgInfo.setIndex(i + 1);
                sgInfo.setArmyHp(cap.getArmyHp());
                sgInfo.setArmyHpMax(cap.getMaxForces());
                sgInfo.setGeneralId(cap.getGeneralId());
                sgInfo.setGeneralName(cap.getGeneralName());
                sgInfo.setGeneralPic(cap.getGeneralPic());
                sgInfo.setQuality(cap.getQuality());
                final Tactic tc = TacticCache.getTacticById(cap.getTacicId());
                if (tc != null) {
                    sgInfo.setTacticName(tc.getName());
                }
                else {
                    KfwdScheduleService.commonLog.error(String.valueOf(cap.getTacicId()) + "null");
                }
                sgInfo.setTroopId(cap.getTroopType());
                sgInfo.setTroopType(cap.getTroopSerial());
                sgInfo.setGeneralLv(cap.getGeneralLv());
                gInfoList1[i] = sgInfo;
            }
        }
        gInfo1.setList(gInfoList1);
        return gInfo1;
    }
    
    @Override
    public KfwdRankingListInfo getRTRankingList(final KfwdRTRankingListKey key) {
        return this.rankingListMap.get(key.getScheduleId());
    }
    
    @Override
    public KfwdState getWdState() {
        KfwdState wdState = new KfwdState();
        wdState.setSeasonId(KfwdScheduleService.curSeasonId);
        wdState.setGlobalState(1);
        final Tuple<Integer, Long> res = KfwdTimeControlService.getNowStateAndCD();
        if (res == null) {
            wdState = new KfwdState();
            wdState.setSeasonId(0);
            wdState.setGlobalState(80);
            return wdState;
        }
        wdState.setGlobalState(res.left);
        wdState.setNextGlobalStateCD(res.right);
        if (wdState.getGlobalState() >= 70) {
            wdState.setCurrentTimestamp(System.currentTimeMillis());
            return wdState;
        }
        if (wdState.getGlobalState() > 30) {}
        if (KfwdScheduleService.globalstate < 3) {
            wdState.setGlobalState(30);
            wdState.setNextGlobalStateCD(0L);
        }
        final long now = System.currentTimeMillis();
        wdState.setCurrentTimestamp(System.currentTimeMillis());
        return wdState;
    }
    
    @Override
    public Tuple<byte[], State> getPlayerMatchInfo(final int competitorId) {
        final long nowTime = System.currentTimeMillis();
        final KfwdPlayerKey pKey = new KfwdPlayerKey(competitorId);
        KfwdRTMatchInfo rtInfo = this.getRTMatchInfo(pKey);
        if (rtInfo == null) {
            final Tuple<byte[], State> tuple = new Tuple();
            tuple.left = LocalMessages.PLAYERNOTASSIGNED.getBytes();
            tuple.right = State.FAIL;
            return tuple;
        }
        final KfwdRTDisPlayInfo displayInfo = this.getRTDisPlayerInfo(pKey);
        if (displayInfo == null) {
            final Tuple<byte[], State> tuple2 = new Tuple();
            tuple2.left = LocalMessages.PLAYERNOTASSIGNED.getBytes();
            tuple2.right = State.FAIL;
            return tuple2;
        }
        if (displayInfo.getRound() != rtInfo.getRound() || displayInfo.getsRound() != rtInfo.getsRound()) {
            rtInfo = this.getRTMatchInfo(pKey);
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        final KfwdBaseInfo baseInfo = new KfwdBaseInfo();
        final KfwdState wdState = this.getWdState();
        baseInfo.setKfwdState(wdState.getGlobalState());
        baseInfo.setNextStateCD(wdState.getNextGlobalStateCD());
        doc.createElement("kfwdbaseInfo", baseInfo);
        if (baseInfo.getKfwdState() != 20 && baseInfo.getKfwdState() == 50) {
            final KfwdBattleInfo bInfo = new KfwdBattleInfo();
            bInfo.setCompetitorId(competitorId);
            BeanUtils.copyProperties(rtInfo, bInfo);
            bInfo.setTerrain(KfwdConstantsAndMethod.getRanTerrain(rtInfo.getMatchId(), rtInfo.getRound(), KfwdScheduleService.curSeasonId, rtInfo.getScheduleId()));
            final KfwdRewardDouble rd = this.kfwdCacheManager.getRewardDouble(competitorId);
            if (rd != null) {
                bInfo.setDoubleCoef(rd.getRoundDoubleCoef(rtInfo.getRound()));
            }
            final Date bTime = rtInfo.getRoundBattleTime();
            final long nextRoundCd = KfwdTimeControlService.getRunDelayMillSecondsByRound(bInfo.getRound() + 1, rtInfo.getScheduleId());
            bInfo.setBattleCD(nextRoundCd);
            bInfo.setBattleId(KfwdConstantsAndMethod.getBattleIdByMatch(KfwdScheduleService.curSeasonId, rtInfo.getScheduleId(), rtInfo.getMatchId(), rtInfo.getRound()));
            bInfo.setTicket(KfwdConstantsAndMethod.getTicketByDoubleCoef(bInfo.getTicket(), bInfo.getDoubleCoef()));
            final int gold = this.getDoubleGold(rtInfo, bInfo.getDoubleCoef(), bInfo.getBattleId(), rtInfo.getTicket());
            bInfo.setDoubleCost(gold);
            bInfo.setCompetitorId(competitorId);
            bInfo.setP1Info(displayInfo.getpInfo1());
            bInfo.setP2Info(displayInfo.getpInfo2());
            bInfo.setP1gInfo(displayInfo.getgInfo1());
            bInfo.setP2gInfo(displayInfo.getgInfo2());
            final int round = rtInfo.getRound();
            doc.createElement("bInfo", bInfo);
        }
        doc.endObject();
        KfwdScheduleService.interfaceLog.info(String.valueOf(competitorId) + "#\t" + doc.toString());
        final Tuple<byte[], State> tuple3 = new Tuple();
        tuple3.left = doc.toByte();
        tuple3.right = State.SUCCESS;
        return tuple3;
    }
    
    private int getDoubleGold(final KfwdRTMatchInfo rtInfo, final int doubleCoef, final long battleId, final int ticket) {
        final KfwdBattle battle = KfwdBattleManager.getBattleById(battleId);
        double goldBasic = 0.0;
        if (battle != null) {
            goldBasic = ChargeitemCache.getById(46).getCost();
        }
        else {
            goldBasic = ChargeitemCache.getById(45).getCost();
        }
        final int gold = KfwdConstantsAndMethod.getNextDoubleCost(goldBasic, doubleCoef, ticket);
        return gold;
    }
    
    @Override
    public OperateResult playerLogin(final int competitorId, final String certificate) {
        if (!KfwdConstantsAndMethod.getCertifacateByCId(competitorId).equals(certificate)) {
            return new OperateResultFail("\u8bc1\u4e66\u9519\u8bef");
        }
        if (KfwdScheduleService.globalstate < 3 || new Date().after(KfwdTimeControlService.getBattleEndTime())) {
            return new OperateResultFail(LocalMessages.NOTINWDTIME);
        }
        final KfwdBattleWarrior bw = this.kfwdCacheManager.getBattleWarrior(competitorId);
        if (bw == null) {
            return new OperateResultFail(LocalMessages.PLAYERNOTASSIGNED);
        }
        final PlayerDto pDto = new PlayerDto(bw);
        return new OperateResultSuccessWithExtraData(pDto);
    }
    
    @Transactional
    @Override
    public KfwdDoubleRewardResult doDoubleReward(final KfwdDoubleRewardKey key) {
        final int competitorId = key.getCompetitorId();
        final KfwdRTMatchInfo rtInfo = this.kfwdMatchInfoMap.get(competitorId);
        if (rtInfo == null) {
            return new KfwdDoubleRewardResult(0, key, LocalMessages.KFWD_DOUBLE_PARAMETER_ERROR);
        }
        if (rtInfo.getRound() != key.getRound() || rtInfo.getRes() > 0) {
            return new KfwdDoubleRewardResult(0, key, LocalMessages.KFWD_DOUBLE_PARAMETER_ERROR);
        }
        if (key.getRound() > KfwdTimeControlService.getTotalRound()) {
            return new KfwdDoubleRewardResult(0, key, "\u5f53\u524d\u6bd4\u8d5b\u5df2\u7ecf\u7ed3\u675f");
        }
        final int newCoef = key.getDoubleCoef();
        if (newCoef > 3) {
            return new KfwdDoubleRewardResult(0, key, "\u5df2\u7ecf\u8fbe\u5230\u6700\u5927\u7ffb\u500d");
        }
        KfwdRewardDouble kfrd = this.kfwdCacheManager.getRewardDouble(competitorId);
        if (kfrd != null) {
            final int oldCoef = kfrd.getRoundDoubleCoef(key.getRound());
            if (newCoef <= oldCoef || newCoef > oldCoef + 1) {
                return new KfwdDoubleRewardResult(0, key, LocalMessages.KFWD_DOUBLE_PARAMETER_ERROR);
            }
        }
        else {
            if (newCoef > 1) {
                return new KfwdDoubleRewardResult(0, key, LocalMessages.KFWD_DOUBLE_PARAMETER_ERROR);
            }
            kfrd = new KfwdRewardDouble();
            kfrd.setScheduleId(rtInfo.getScheduleId());
            kfrd.setCompetitorId(competitorId);
            kfrd.setSeasonId(KfwdScheduleService.curSeasonId);
            kfrd.setDoubleInfo(0L);
            this.kfwdRewardDoubleDao.create((IModel)kfrd);
            this.kfwdCacheManager.putIntoCache(kfrd);
        }
        final KfwdTicketReward tr = this.kfwdCacheManager.getTicketInfo(competitorId);
        if (tr != null && tr.getRoundTicket(key.getRound()) > 0) {
            return new KfwdDoubleRewardResult(0, key, "\u5f53\u524d\u4e0d\u5728\u7ffb\u500d\u65f6\u95f4");
        }
        final long battleId = KfwdConstantsAndMethod.getBattleIdByMatch(KfwdScheduleService.curSeasonId, rtInfo.getScheduleId(), rtInfo.getMatchId(), rtInfo.getRound());
        final int gold = this.getDoubleGold(rtInfo, newCoef - 1, battleId, rtInfo.getTicket());
        if (gold > key.getGold()) {
            return new KfwdDoubleRewardResult(0, key, LocalMessages.GOLD_NOT_ENOUPH);
        }
        kfrd.setRoundDoubleInfo(key.getRound(), newCoef);
        this.kfwdRewardDoubleDao.update((IModel)kfrd);
        this.kfwdCacheManager.putIntoCache(kfrd);
        return new KfwdDoubleRewardResult(1, key, gold);
    }
    
    @Override
    public KfwdTicketResultInfo doGetTicketRewardInfo(final KfwdPlayerKey key) {
        final int competitorId = key.getCompetitorId();
        final KfwdRTMatchInfo rtInfo = this.kfwdMatchInfoMap.get(competitorId);
        if (rtInfo == null) {
            return null;
        }
        final KfwdTicketReward tr = this.kfwdCacheManager.getTicketInfo(competitorId);
        if (tr == null) {
            return null;
        }
        final KfwdTicketResultInfo tri = new KfwdTicketResultInfo();
        tri.setCompetitorId(competitorId);
        tri.setPlayerId(key.getPlayerId());
        tri.setRewardInfo(tr.getRewardInfo());
        tri.setScheduleId(tr.getScheduleId());
        tri.setSeasonId(tr.getSeasonId());
        return tri;
    }
    
    @Override
    public byte[] getBattleIniInfo(final int competitorId) {
        final KfwdRTMatchInfo rtInfo = this.kfwdMatchInfoMap.get(competitorId);
        if (rtInfo == null) {
            return JsonBuilder.getJson(State.FAIL, "");
        }
        final long battleId = KfwdConstantsAndMethod.getBattleIdByMatch(KfwdScheduleService.curSeasonId, rtInfo.getScheduleId(), rtInfo.getMatchId(), rtInfo.getRound());
        final KfwdBattle battle = KfwdBattleManager.getBattleById(battleId);
        if (battle == null) {
            return JsonBuilder.getJson(State.FAIL, "");
        }
        final int round = battle.getMatch().getRound();
        final long nextRoundCd = KfwdTimeControlService.getRunDelayMillSecondsByRound(round + 1, rtInfo.getScheduleId());
        if (battle.state != 1) {
            final StringBuilder battleMsg = new StringBuilder();
            battle.getIniBattleMsg(battleMsg);
            KfwdScheduleService.battleReportLog.info(battleMsg.toString());
            KfwdScheduleService.interfaceLog.info(String.valueOf(competitorId) + "#\t" + battleMsg.toString());
            return JsonBuilder.getJson(State.SUCCESS, "battleIni", battleMsg.toString());
        }
        final KfwdBattleRes battleRes = battle.getRes();
        if (battleRes == null) {
            return JsonBuilder.getJson(State.FAIL, "");
        }
        KfwdScheduleService.interfaceLog.info(String.valueOf(competitorId) + "#\t" + new String(battleRes.getResJson(competitorId, nextRoundCd)));
        return JsonBuilder.getJson(State.SUCCESS, "battleRes", battleRes.getResJson(competitorId, nextRoundCd));
    }
    
    @Override
    public byte[] getKfwdRankingInfo(final int competitorId) {
        final KfwdRTMatchInfo rtInfo = this.kfwdMatchInfoMap.get(competitorId);
        if (rtInfo == null) {
            return JsonBuilder.getJson(State.FAIL, "");
        }
        final int round = rtInfo.getRound();
        final int scheduleId = rtInfo.getScheduleId();
        final KfwdRankingListInfo rankingInfo = this.rankingListMap.get(scheduleId);
        if (rankingInfo == null) {
            return JsonBuilder.getJson(State.FAIL, "");
        }
        final HashMap<Integer, KfwdRuntimeResultDto> rankingMap = rankingInfo.getRankingMap();
        final List<KfwdRuntimeResultDto> rankingList = rankingInfo.getRankingList();
        final int rankListSize = rankingList.size();
        final KfwdRuntimeResultDto selfDto = rankingMap.get(competitorId);
        final int selfPos = selfDto.getPos();
        final int[] posArray = new int[8];
        for (int i = 0; i < 8; ++i) {
            posArray[i] = i;
        }
        int selfArrayPos = 7;
        if (selfPos < selfArrayPos) {
            selfArrayPos = selfPos;
        }
        for (int j = selfArrayPos - 4; j <= selfArrayPos && j >= 0; ++j) {
            posArray[j] = selfPos - selfArrayPos + j;
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        final KfwdRuntimeResultDto[] dtoArray = new KfwdRuntimeResultDto[9];
        for (int k = 0; k < 8; ++k) {
            final int pos = posArray[k];
            if (pos >= rankListSize) {
                break;
            }
            final KfwdRuntimeResultDto dto = rankingList.get(pos);
            final KfwdRuntimeResultDto copdto = new KfwdRuntimeResultDto();
            BeanUtils.copyProperties(dto, copdto);
            if (copdto.getCompetitorId() == competitorId) {
                copdto.setSelf(1);
            }
            else {
                copdto.setSelf(0);
            }
            dtoArray[k] = copdto;
        }
        final KfwdRuntimeResultDto dto2 = rankingList.get(selfPos);
        doc.createElement("selfPos", selfPos);
        doc.createElement("dtoList", dtoArray);
        doc.createElement("day", KfwdTimeControlService.getDayByRound(rankingInfo.getRound()));
        final int rewardDay = KfwdTimeControlService.getAndCheckDayRewardRound(round);
        if (rewardDay > 0) {
            final KfwdTicketReward ticketReward = this.kfwdCacheManager.getTicketInfo(competitorId);
            if (ticketReward != null) {
                final int[] res = ticketReward.getRewardInfoByDay(rewardDay);
                doc.createElement("hasGetReward", res[0]);
                doc.createElement("rewardTicket", res[1]);
            }
        }
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public byte[] chooseStrategyOrTactic(final int competitorId, final int pos, final int tacticId, final int strategyId) {
        final KfwdRTMatchInfo rtInfo = this.kfwdMatchInfoMap.get(competitorId);
        if (rtInfo == null) {
            return JsonBuilder.getJson(State.FAIL, "");
        }
        final long battleId = KfwdConstantsAndMethod.getBattleIdByMatch(KfwdScheduleService.curSeasonId, rtInfo.getScheduleId(), rtInfo.getMatchId(), rtInfo.getRound());
        final KfwdBattle battle = KfwdBattleManager.getBattleById(battleId);
        if (battle == null) {
            return JsonBuilder.getJson(State.FAIL, "");
        }
        if (battle.state == 1) {
            return JsonBuilder.getJson(State.FAIL, "");
        }
        boolean isAtt = false;
        if (battle.getMatch().getPlayer1Id() == competitorId) {
            isAtt = true;
        }
        final int res = battle.chooseStrategyOrTactic(isAtt, pos, tacticId, strategyId);
        if (res == 2) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.CHOOSESTRATEGYORTACTIC_FAILED);
        }
        return JsonBuilder.getJson(State.SUCCESS, LocalMessages.CHOOSESTRATEGYORTACTIC_FAILED);
    }
    
    @Transactional
    @Override
    public byte[] getKfwdDayReward(final int competitorId, final int day) {
        final KfwdRTMatchInfo rtInfo = this.kfwdMatchInfoMap.get(competitorId);
        if (rtInfo == null) {
            return JsonBuilder.getJson(State.FAIL, "");
        }
        final int round = rtInfo.getRound();
        final int roundDay = KfwdTimeControlService.getDayByRound(round);
        if (roundDay <= day) {
            return JsonBuilder.getJson(State.FAIL, "\u73b0\u5728\u4e0d\u662f\u9886\u53d6\u6bcf\u65e5\u5956\u52b1\u9636\u6bb5");
        }
        final KfwdTicketReward ticketReward = this.kfwdCacheManager.getTicketInfo(competitorId);
        if (ticketReward == null) {
            return JsonBuilder.getJson(State.FAIL, "");
        }
        if (day <= 0 || day > KfwdConstantsAndMethod.MAXFIGHTDAY) {
            return JsonBuilder.getJson(State.FAIL, "\u53c2\u6570\u9519\u8bef");
        }
        final int[] res = ticketReward.getRewardInfoByDay(day);
        final int ticket = res[1];
        final int state = res[0];
        if (ticket == 0) {
            return JsonBuilder.getJson(State.FAIL, "\u5f53\u524d\u6ca1\u6709\u5956\u52b1");
        }
        if (state == 1) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.KFWD_RECIEVE_REWARD_ALREADY);
        }
        ticketReward.addReward(day);
        this.kfwdTicketRewardDao.update((IModel)ticketReward);
        this.kfwdCacheManager.putIntoCache(ticketReward);
        KfwdTicketRewardNoticeInfo.addNoticeInfo(ticketReward);
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
}
