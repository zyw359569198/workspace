package com.reign.kfzb.service;

import org.springframework.beans.factory.*;
import org.springframework.stereotype.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.kfwd.dao.*;
import com.reign.kfzb.dao.*;
import com.reign.kfzb.cache.*;
import org.springframework.context.*;
import org.apache.commons.logging.*;
import com.reign.kfzb.constants.*;
import com.reign.kfzb.util.*;
import com.reign.kf.comm.param.match.*;
import java.util.concurrent.*;
import com.reign.framework.hibernate.model.*;
import org.springframework.transaction.annotation.*;
import java.util.*;
import com.reign.kfzb.domain.*;
import org.apache.commons.lang.*;
import com.reign.kf.match.common.util.*;
import com.reign.kfwd.domain.*;
import org.springframework.beans.*;
import com.reign.kf.comm.util.*;
import java.io.*;
import com.reign.kf.comm.protocol.*;
import com.reign.kf.comm.entity.*;
import com.reign.kfzb.notice.*;
import com.reign.kf.comm.entity.kfwd.response.*;
import com.reign.kf.match.sdata.cache.*;
import com.reign.kf.match.sdata.domain.*;
import com.reign.kfzb.battle.*;
import com.reign.kf.match.common.*;
import com.reign.util.*;
import com.reign.framework.json.*;
import com.reign.kfzb.dto.*;
import com.reign.kfzb.dto.request.*;
import com.reign.kfzb.dto.response.*;

@Component
public class KfzbScheduleService implements IKfzbScheduleService, ApplicationContextAware, InitializingBean
{
    public static volatile int curSeasonId;
    public static volatile int globalstate;
    public static volatile long lastSetGlobalStateTime;
    @Autowired
    IKfzbBattleWarriorDao kfzbBattleWarriorDao;
    @Autowired
    IKfzbBattleWarriorGeneralDao kfzbBattleWarriorGeneralDao;
    @Autowired
    IKfzbRuntimeMatchDao kfzbRuntimeMatchDao;
    @Autowired
    IKfwdBattleWarriorDao kfwdBattleWarriorDao;
    @Autowired
    IKfzbRuntimeResultDao kfzbRuntimeResultDao;
    @Autowired
    IKfzbRuntimeSupportDao kfzbRuntimeSupportDao;
    @Autowired
    IKfzbMatchService kfzbMatchService;
    @Autowired
    IKfzbCacheManager kfzbCacheManager;
    @Autowired
    IKfzbSeasonService kfzbSeasonService;
    private static Log rtInfoLog;
    private static Log scheduleInfoLog;
    private static Log commonLog;
    private static Log battleReportLog;
    private static Log interfaceLog;
    private ApplicationContext context;
    private IKfzbScheduleService self;
    ScheduledThreadPoolExecutor exeutors;
    private volatile Map<Integer, KfzbRuntimeMatch> matchMap;
    private volatile HashMap<Integer, Integer> layerMatchNumMap;
    Object LayerMatchNumlock;
    private volatile Map<Integer, KfzbRTMatchInfo> kfzbMatchInfoMap;
    private volatile Map<Integer, KfzbMatchSupportInfo> kfzbSupportInfoMap;
    private volatile Map<Integer, Long> kfzbSupportTimeMap;
    private volatile Map<Integer, Boolean> scheduledMap;
    private static volatile ConcurrentHashMap<Long, CopyOnWriteArrayList<FrameBattleReport>> battleReportMap;
    private volatile Map<Integer, Integer> posCIdMap;
    private volatile Map<Integer, Integer> posWdPosMap;
    private volatile Map<Integer, Integer> matchWinnerMap;
    private volatile int layerRunning;
    Map<Integer, KfzbBattleInfo> phase2BattleInfoMap;
    public static SendWinnerInfoThread winnerInfoSendThread;
    public static LinkedBlockingQueue<Integer> sendWinnerInfoQueue;
    final long SECOND30 = 30000L;
    public static final int PHASE2MAXMATCHID = 15;
    public static Object[] matchLockArray;
    
    static {
        KfzbScheduleService.rtInfoLog = LogFactory.getLog("astd.kfzb.log.rtInfo");
        KfzbScheduleService.scheduleInfoLog = LogFactory.getLog("astd.kfzb.log.scheduleInfo");
        KfzbScheduleService.commonLog = LogFactory.getLog("astd.kfzb.log.comm");
        KfzbScheduleService.battleReportLog = LogFactory.getLog("mj.kfzb.battleReport.log");
        KfzbScheduleService.interfaceLog = LogFactory.getLog("astd.kfzb.log.interface");
        KfzbScheduleService.battleReportMap = new ConcurrentHashMap<Long, CopyOnWriteArrayList<FrameBattleReport>>();
        KfzbScheduleService.winnerInfoSendThread = null;
        KfzbScheduleService.sendWinnerInfoQueue = new LinkedBlockingQueue<Integer>();
        KfzbScheduleService.matchLockArray = new Object[1024];
        for (int i = 0; i < KfzbScheduleService.matchLockArray.length; ++i) {
            KfzbScheduleService.matchLockArray[i] = new Object();
        }
    }
    
    public KfzbScheduleService() {
        this.exeutors = new ScheduledThreadPoolExecutor(10);
        this.matchMap = new ConcurrentHashMap<Integer, KfzbRuntimeMatch>();
        this.layerMatchNumMap = new HashMap<Integer, Integer>();
        this.LayerMatchNumlock = new Object();
        this.kfzbMatchInfoMap = new ConcurrentHashMap<Integer, KfzbRTMatchInfo>();
        this.kfzbSupportInfoMap = new ConcurrentHashMap<Integer, KfzbMatchSupportInfo>();
        this.kfzbSupportTimeMap = new ConcurrentHashMap<Integer, Long>();
        this.scheduledMap = new ConcurrentHashMap<Integer, Boolean>();
        this.posCIdMap = new ConcurrentHashMap<Integer, Integer>();
        this.posWdPosMap = new ConcurrentHashMap<Integer, Integer>();
        this.matchWinnerMap = new ConcurrentHashMap<Integer, Integer>();
        this.layerRunning = -1;
        this.phase2BattleInfoMap = new ConcurrentHashMap<Integer, KfzbBattleInfo>();
    }
    
    @Override
    public void intiSeasonInfo(final KfzbSeasonInfo newInfo) {
        KfzbScheduleService.curSeasonId = newInfo.getSeasonId();
        KfzbScheduleService.globalstate = 1;
        this.scheduledMap.clear();
    }
    
    @Override
    public boolean beginMatch(final int seasonId) {
        return false;
    }
    
    @Override
    public boolean hasScheduledMatch(final int seasonId) {
        return KfzbScheduleService.curSeasonId == seasonId && KfzbScheduleService.globalstate >= 3;
    }
    
    @Override
    public synchronized void scheduleMatch(final KfzbSeasonInfo newInfo) {
        this.kfzbSupportInfoMap.clear();
        this.kfzbMatchInfoMap.clear();
        this.layerMatchNumMap.clear();
        this.posCIdMap.clear();
        this.posWdPosMap.clear();
        this.matchWinnerMap.clear();
        this.matchMap.clear();
        this.kfzbSupportTimeMap.clear();
        this.phase2BattleInfoMap.clear();
        KfzbScheduleService.battleReportMap.clear();
        this.layerRunning = -1;
        final int curSeasonId = newInfo.getSeasonId();
        this.self.checkandGetWarriorData();
        if (this.scheduleSeason(curSeasonId)) {
            final int totalLay = KfzbTimeControlService.getTotalLayer();
            final int layer = this.createRunTimeMatch(totalLay);
            this.iniMatchWinnerMap(layer, totalLay);
            this.intRTMatchInfo(layer);
            this.iniRTSupport();
            this.iniPhase2BattleInfo(curSeasonId);
            this.scheduleMatchByLayer(layer);
        }
        else {
            this.scheduleAllMatch(newInfo.getSeasonId());
        }
        if (KfzbScheduleService.globalstate < 3) {
            KfzbScheduleService.globalstate = 3;
        }
    }
    
    private void iniPhase2BattleInfo(final int curSeasonId) {
        final List<KfzbRuntimeMatch> mlist = this.kfzbRuntimeMatchDao.getPhase2BattleInfo(curSeasonId);
        final Map<Integer, KfzbBattleInfo> p2BattleInfoMap = new ConcurrentHashMap<Integer, KfzbBattleInfo>();
        final Map<Integer, Integer> maxMatchRoundIdMap = new HashMap<Integer, Integer>();
        final Map<Integer, Integer> matchBattleResMap = new HashMap<Integer, Integer>();
        for (final KfzbRuntimeMatch match : mlist) {
            final int matchId = match.getMatchId();
            final Integer maxRound = maxMatchRoundIdMap.get(matchId);
            if (maxRound == null || match.getRound() > maxRound) {
                maxMatchRoundIdMap.put(matchId, match.getRound());
            }
            Integer oldRes = matchBattleResMap.get(matchId);
            if (oldRes == null) {
                oldRes = 0;
            }
            int roundRes = 0;
            if (match.getRoundWinner() == match.getPlayer1Id() && match.getPlayer1Id() != 0) {
                roundRes = 1;
            }
            if (match.getRoundWinner() == match.getPlayer2Id() && match.getPlayer2Id() != 0) {
                roundRes = 2;
            }
            oldRes = KfzbCommonConstants.addRoundBattleRes(oldRes, match.getRound(), roundRes);
            matchBattleResMap.put(matchId, oldRes);
        }
        for (final KfzbRuntimeMatch match : mlist) {
            final int matchId = match.getMatchId();
            final int round = match.getRound();
            if (round != maxMatchRoundIdMap.get(matchId)) {
                continue;
            }
            if (p2BattleInfoMap.get(matchId) != null) {
                continue;
            }
            final KfzbBattleInfo battleInfo = new KfzbBattleInfo();
            final int pId1 = match.getPlayer1Id();
            final int pId2 = match.getPlayer2Id();
            battleInfo.setMatchId(matchId);
            battleInfo.setRound(match.getRound());
            battleInfo.setTerrain(KfzbCommonConstants.getRanTerrain(matchId, match.getRound(), curSeasonId));
            battleInfo.setBattleTime(KfzbTimeControlService.getBattleTime(matchId, match.getLayer(), match.getRound()));
            final KfzbPlayerInfo zbPlayerInfo1 = new KfzbPlayerInfo();
            final KfzbBattleWarrior w1 = this.kfzbCacheManager.getBattleWarrior(pId1);
            BeanUtils.copyProperties(w1, zbPlayerInfo1);
            battleInfo.setP1(zbPlayerInfo1);
            final KfzbPlayerInfo zbPlayerInfo2 = new KfzbPlayerInfo();
            final KfzbBattleWarrior w2 = this.kfzbCacheManager.getBattleWarrior(pId2);
            BeanUtils.copyProperties(w2, zbPlayerInfo2);
            battleInfo.setP2(zbPlayerInfo2);
            final KfzbBattleWarriorGeneral g1 = this.kfzbCacheManager.getBattleWarriorGeneral(pId1);
            final CampArmyParam[] campArmys1 = g1.getCampList();
            final KfwdGInfo gInfo1 = this.getGInfoFromCampArmys(campArmys1);
            battleInfo.setG1(gInfo1);
            final KfzbBattleWarriorGeneral g2 = this.kfzbCacheManager.getBattleWarriorGeneral(pId2);
            final CampArmyParam[] campArmys2 = g2.getCampList();
            final KfwdGInfo gInfo2 = this.getGInfoFromCampArmys(campArmys2);
            battleInfo.setG2(gInfo2);
            battleInfo.setP1Win(match.getPlayer1Win());
            battleInfo.setP2Win(match.getPlayer2Win());
            battleInfo.setLayerRound(KfzbTimeControlService.getLayerBattleNum(match.getLayer()));
            final KfzbMatchSupportInfo supInfo = this.kfzbSupportInfoMap.get(matchId);
            if (supInfo != null) {
                final int cId1 = supInfo.getcId1();
                final int cId2 = supInfo.getcId2();
                final int sup1 = supInfo.getSupportNum1();
                final int sup2 = supInfo.getSupportNum2();
                if (pId1 == cId1) {
                    battleInfo.setSup1(sup1);
                    battleInfo.setSup2(sup2);
                }
                else {
                    battleInfo.setSup2(sup1);
                    battleInfo.setSup1(sup2);
                }
            }
            battleInfo.setBattleRes(matchBattleResMap.get(matchId));
            final boolean needChange = KfzbCommonConstants.getNeedChange(curSeasonId, matchId, round);
            battleInfo.setNeedChange(needChange);
            p2BattleInfoMap.put(matchId, battleInfo);
            KfzbScheduleService.rtInfoLog.info(KfzbLogUtils.getPhase2RtMatchInfo(battleInfo));
        }
        this.phase2BattleInfoMap = p2BattleInfoMap;
    }
    
    private void scheduleAllMatch(final int seasonId) {
        synchronized (this.scheduledMap) {
            final Tuple3<Integer, Long, Integer> res = KfzbTimeControlService.getNowStateAndCDAndSeasonId();
            final int curSeasonId = res.get_3();
            if (seasonId != curSeasonId) {
                // monitorexit(this.scheduledMap)
                return;
            }
            final int state = res.get_1();
            if (this.scheduleSeason(curSeasonId)) {
                // monitorexit(this.scheduledMap)
                return;
            }
            int totalLay = 0;
            if (state >= 40) {
                final List<KfzbBattleWarrior> warriors = this.kfzbBattleWarriorDao.getWarriorBySeasonId(curSeasonId);
                final int warriorNum = warriors.size();
                if (warriorNum <= 17) {
                    KfzbScheduleService.scheduleInfoLog.info("no enouph warriors");
                    // monitorexit(this.scheduledMap)
                    return;
                }
                totalLay = (int)Math.ceil(Math.log(warriorNum) / Math.log(2.0));
                if (warriors.get(0).getBattlePos() == 0) {
                    final Map<String, Set<String>> playerLimitInfo = KfzbTimeControlService.getPlayerLimitInfo();
                    final Set<Integer> limitPk = new HashSet<Integer>();
                    for (final KfzbBattleWarrior w : warriors) {
                        final String gameServer = w.getGameServer();
                        final String playerName = w.getPlayerName();
                        final Set<String> gameServerNames = playerLimitInfo.get(gameServer);
                        if (gameServerNames != null && gameServerNames.contains(playerName)) {
                            limitPk.add(w.getPk());
                        }
                    }
                    final int groupNum = 1 << totalLay >> 4;
                    final int limitPlayerNum = limitPk.size();
                    final int prePlayerNum = 1 << totalLay - 1;
                    final int[] posArray = new int[warriorNum];
                    for (int i = 0; i < prePlayerNum; ++i) {
                        posArray[i] = (i >> 4 << 1) + 1 + groupNum * (i & 0xF);
                    }
                    for (int i = prePlayerNum; i < warriorNum; ++i) {
                        posArray[i] = (i - prePlayerNum >> 4 << 1) + 2 + groupNum * (i & 0xF);
                    }
                    int posBase = limitPlayerNum;
                    int limitBase = 0;
                    for (final KfzbBattleWarrior w2 : warriors) {
                        final int pk = w2.getPk();
                        if (!limitPk.contains(pk)) {
                            w2.setBattlePos(posArray[posBase]);
                            ++posBase;
                        }
                        else {
                            w2.setBattlePos(posArray[limitBase]);
                            ++limitBase;
                        }
                    }
                    for (int saveNum = 100, j = 0; j < (warriorNum + saveNum - 1) / saveNum; ++j) {
                        final int fromIndex = j * saveNum;
                        int toIndex = (j + 1) * saveNum;
                        if (toIndex > warriorNum) {
                            toIndex = warriorNum;
                        }
                        final List<KfzbBattleWarrior> subList = warriors.subList(fromIndex, toIndex);
                        this.self.saveWarriorPos(subList);
                    }
                    this.self.updateRuntimeResultMaxLayerId(seasonId, totalLay);
                }
                KfzbTimeControlService.setTotolLay(seasonId, totalLay);
                this.scheduledMap.put(curSeasonId, true);
                final int layer = this.createRunTimeMatch(totalLay);
                this.iniMatchWinnerMap(layer, totalLay);
                this.intRTMatchInfo(layer);
                this.iniRTSupport();
                this.iniPhase2BattleInfo(curSeasonId);
                this.scheduleMatchByLayer(layer);
            }
            else {
                final Date time = KfzbTimeControlService.getScheduleTime();
                final long delay = time.getTime() - new Date().getTime();
                this.exeutors.schedule(new Runnable() {
                    @Override
                    public void run() {
                        KfzbScheduleService.this.scheduleAllMatch(seasonId);
                    }
                }, delay, TimeUnit.MILLISECONDS);
            }
        }
        // monitorexit(this.scheduledMap)
    }
    
    private boolean scheduleSeason(final int curSeasonId2) {
        final Boolean scheduleInfo = this.scheduledMap.get(KfzbScheduleService.curSeasonId);
        return scheduleInfo != null && scheduleInfo;
    }
    
    @Transactional
    @Override
    public void saveWarriorPos(final List<KfzbBattleWarrior> warriors) {
        for (final KfzbBattleWarrior w : warriors) {
            this.kfzbBattleWarriorDao.update((IModel)w);
            this.kfzbCacheManager.putIntoCache(w);
        }
    }
    
    @Transactional
    @Override
    public void updateRuntimeResultMaxLayerId(final int seasonId, final int totalLay) {
        this.kfzbRuntimeResultDao.updateTotalLayer(seasonId, totalLay);
    }
    
    public static void main(final String[] args) {
        final long t1 = System.currentTimeMillis();
        final List<KfzbBattleWarrior> warriors = new ArrayList<KfzbBattleWarrior>();
        for (int i = 0; i < 50; ++i) {
            final KfzbBattleWarrior w = new KfzbBattleWarrior();
            w.setPk(i + 1);
            w.setGameServer("1");
            w.setPlayerName(String.valueOf(i));
            warriors.add(w);
        }
    }
    
    private void iniRTSupport() {
        final List<KfzbRuntimeSupport> slist = this.kfzbRuntimeSupportDao.getAllSupportBySeasonId(KfzbScheduleService.curSeasonId);
        for (final KfzbRuntimeSupport rs : slist) {
            final int matchKey = rs.getMatchId();
            final KfzbMatchSupportInfo sInfo = new KfzbMatchSupportInfo();
            sInfo.setMatchId(rs.getMatchId());
            sInfo.setSupportNum1(rs.getSupportNum1());
            sInfo.setSupportNum2(rs.getSupportNum2());
            sInfo.setSeasonId(KfzbScheduleService.curSeasonId);
            sInfo.setcId1(rs.getcId1());
            sInfo.setcId2(rs.getcId2());
            this.kfzbSupportInfoMap.put(matchKey, sInfo);
        }
    }
    
    private void iniMatchWinnerMap(final int layer, final int totalLay) {
        for (int curlayer = totalLay; curlayer > 0; --curlayer) {
            final int layRound = KfzbTimeControlService.getLayerBattleNum(curlayer);
            final List<KfzbRuntimeMatch> mlist = this.kfzbRuntimeMatchDao.getMatchBylayerRoundAndSeasonId(curlayer, layRound, KfzbScheduleService.curSeasonId);
            for (final KfzbRuntimeMatch m : mlist) {
                if (m.getRoundWinner() != 0 && m.getLayerWinner() != 0) {
                    this.matchWinnerMap.put(m.getMatchId(), m.getLayerWinner());
                }
            }
        }
    }
    
    private void intRTMatchInfo(final int layer) {
        final int totalLay = KfzbTimeControlService.getTotalLayer();
        final List<KfzbBattleWarrior> wlist = this.kfzbBattleWarriorDao.getWarriorBySeasonId(KfzbScheduleService.curSeasonId);
        final List<KfzbRuntimeResult> rlist = this.kfzbRuntimeResultDao.getResultBySeasonId(KfzbScheduleService.curSeasonId);
        final HashMap<Integer, KfzbRuntimeResult> rMap = new HashMap<Integer, KfzbRuntimeResult>();
        for (final KfzbRuntimeResult r : rlist) {
            rMap.put(r.getCompetitorId(), r);
        }
        final List<KfzbRuntimeMatch> mlist = this.kfzbRuntimeMatchDao.getAllMatch(KfzbScheduleService.curSeasonId);
        final HashMap<Integer, KfzbRuntimeMatch> mMap = new HashMap<Integer, KfzbRuntimeMatch>();
        for (final KfzbRuntimeMatch m : mlist) {
            mMap.put(KfzbRuntimeMatch.getMatchKey(m.getMatchId(), m.getRound()), m);
            this.putNewRunTimeMatchToMatchMap(m);
        }
        final HashMap<Integer, KfzbRuntimeSupport> sMap = new HashMap<Integer, KfzbRuntimeSupport>();
        final List<KfzbRuntimeSupport> slist = this.kfzbRuntimeSupportDao.getAllSupportBySeasonId(KfzbScheduleService.curSeasonId);
        for (final KfzbRuntimeSupport s : slist) {
            sMap.put(s.getMatchId(), s);
        }
        this.kfzbMatchInfoMap.put(0, new KfzbRTMatchInfo());
        for (final KfzbBattleWarrior w : wlist) {
            this.posCIdMap.put(w.getBattlePos(), w.getCompetitorId());
            this.posWdPosMap.put(w.getCompetitorId(), w.getBattlePos());
            final int cId = w.getCompetitorId();
            final KfzbRTMatchInfo mInfo = new KfzbRTMatchInfo();
            final KfzbRuntimeResult r2 = rMap.get(cId);
            mInfo.setcId(cId);
            final int curLayer = r2.getLayer();
            final int curRound = r2.getRound();
            mInfo.setLayer(curLayer);
            mInfo.setRound(curRound);
            mInfo.setRes(r2.getRes());
            mInfo.setLastRes(r2.getLastres());
            if (r2.getIsfinsh() == 3) {
                mInfo.setState(2);
            }
            final int wpos = w.getBattlePos();
            final int thisLaymId = this.getMatchIdByLayerPos(wpos, curLayer, totalLay);
            final int lastLaymId = this.getMatchIdByLayerPos(wpos, curLayer + 1, totalLay);
            final int thisMatchKey = KfzbRuntimeMatch.getMatchKey(thisLaymId, curRound);
            mInfo.setRoundBattleTime(KfzbTimeControlService.getBattleTime(thisLaymId, curLayer, curRound));
            mInfo.setMatchId(thisLaymId);
            int lastMatchKey = -1;
            if (curRound > 1) {
                lastMatchKey = KfzbRuntimeMatch.getMatchKey(thisLaymId, curRound - 1);
                mInfo.setLastRoundBattleTime(KfzbTimeControlService.getBattleTime(thisLaymId, curLayer, curRound - 1));
            }
            else if (lastLaymId > -1) {
                final int lastLayerRound = KfzbTimeControlService.getLayerBattleNum(curLayer + 1);
                lastMatchKey = KfzbRuntimeMatch.getMatchKey(lastLaymId, lastLayerRound);
                mInfo.setLastRoundBattleTime(KfzbTimeControlService.getBattleTime(lastLaymId, curLayer + 1, lastLayerRound));
            }
            final KfzbRuntimeMatch thisMatch = mMap.get(thisMatchKey);
            KfzbRuntimeMatch lastMatch = null;
            if (lastMatchKey > 0) {
                lastMatch = mMap.get(lastMatchKey);
                mInfo.setLastcId1(lastMatch.getPlayer1Id());
                mInfo.setLastcId2(lastMatch.getPlayer2Id());
                mInfo.setLastMatchId(KfzbRuntimeMatch.getMatchIdFromMatchKey(lastMatchKey));
            }
            if (thisMatch == null) {
                System.out.println("!!" + thisLaymId + "-" + curRound);
            }
            mInfo.setcId1(thisMatch.getPlayer1Id());
            mInfo.setcId2(thisMatch.getPlayer2Id());
            if (!StringUtils.isBlank(thisMatch.getReportId())) {
                mInfo.setLastReport(thisMatch.getReportId());
            }
            else if (lastMatch != null) {
                mInfo.setLastReport(lastMatch.getReportId());
            }
            this.kfzbMatchInfoMap.put(cId, mInfo);
            KfzbScheduleService.rtInfoLog.info("in=" + cId + KfzbLogUtils.getRtInfoLog(this.kfzbMatchInfoMap.get(cId)));
        }
    }
    
    private int getMatchIdByLayerPos(final int wpos, final int layer, final int totalLay) {
        if (layer > totalLay) {
            return -1;
        }
        return (wpos - 1) / 2 + (1 << totalLay >> 1) >> totalLay - layer;
    }
    
    private void putLayerMatchNum(final int layer, final int num) {
        synchronized (this.LayerMatchNumlock) {
            this.layerMatchNumMap.put(layer, num);
        }
        // monitorexit(this.LayerMatchNumlock)
    }
    
    private int minusLayerMatchNUm(final int layer) {
        synchronized (this.LayerMatchNumlock) {
            int num = this.layerMatchNumMap.get(layer);
            --num;
            this.layerMatchNumMap.put(layer, num);
            // monitorexit(this.LayerMatchNumlock)
            return num;
        }
    }
    
    private void scheduleMatchByLayer(final int layer) {
        KfzbScheduleService.scheduleInfoLog.info("scheduleMatchByLayer#" + layer);
        final List<KfzbRuntimeMatch> mlist = this.kfzbRuntimeMatchDao.getUnfinishMatchByLayerAndSeasonId(KfzbScheduleService.curSeasonId, layer);
        final int msize = mlist.size();
        this.putLayerMatchNum(layer, msize);
        if (msize == 0) {
            if (layer <= 1) {
                if (layer == 1) {
                    this.layerRunning = 0;
                    KfzbScheduleService.globalstate = 7;
                    KfzbScheduleService.lastSetGlobalStateTime = System.currentTimeMillis();
                }
                return;
            }
            this.runNextLayerMatch(KfzbScheduleService.curSeasonId, layer - 1);
        }
        else {
            long delay = KfzbTimeControlService.getLayerBattleTime(layer) - System.currentTimeMillis() - 3000L;
            if (delay < 0L) {
                delay = 0L;
            }
            this.exeutors.schedule(new Runnable() {
                @Override
                public void run() {
                    KfzbScheduleService.scheduleInfoLog.info("layerStart#" + layer);
                    for (final KfzbRuntimeMatch m : mlist) {
                        KfzbScheduleService.this.runMatch(m);
                    }
                    KfzbScheduleService.access$3(KfzbScheduleService.this, layer);
                }
            }, delay, TimeUnit.MILLISECONDS);
        }
    }
    
    private void runMatch(final KfzbRuntimeMatch match) {
        KfzbScheduleService.scheduleInfoLog.info("schMatch" + KfzbLogUtils.getMachInfo(match));
        this.makeNewRtMatchInfoByNewMatch(match);
        if (match.getLayer() >= KfzbTimeControlService.getTotalLayer() + 1) {
            return;
        }
        final long delay = KfzbTimeControlService.getMatchDelay(match);
        this.exeutors.schedule(new Runnable() {
            @Override
            public void run() {
                final KfzbRuntimeMatchResult resMatch = null;
                try {
                    if (match.getSeasonId() != KfzbScheduleService.curSeasonId) {
                        return;
                    }
                    KfzbScheduleService.scheduleInfoLog.info("runMatch" + KfzbLogUtils.getMachInfo(match));
                    KfzbScheduleService.this.kfzbMatchService.runMatch(match);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, delay, TimeUnit.MILLISECONDS);
    }
    
    @Override
    public void doFinishMatch(final KfzbRuntimeMatch match) {
        KfzbScheduleService.scheduleInfoLog.info("aftMatch" + KfzbLogUtils.getMachInfo(match));
        final KfzbBattleWarrior w1 = this.kfzbCacheManager.getBattleWarrior(match.getPlayer1Id());
        final KfzbBattleWarrior w2 = this.kfzbCacheManager.getBattleWarrior(match.getPlayer2Id());
        KfzbScheduleService.scheduleInfoLog.info("aftMatch" + KfzbLogUtils.getMachInfo(match));
        this.self.buildNewMatchAndRun(match);
    }
    
    protected void sendKfzbReportDetailToGw(final KfzbMatchResDetailInfo matchResDetailInfo) {
    }
    
    private int createRunTimeMatch(final int totalLay) {
        Integer layer = this.kfzbRuntimeMatchDao.getMinLayerBySeasonId(KfzbScheduleService.curSeasonId);
        if (layer == null) {
            layer = totalLay;
        }
        this.createMatchByLayer(layer);
        return layer;
    }
    
    private void createMatchByLayer(final int layer) {
        KfzbScheduleService.scheduleInfoLog.info("createMatchByLayer" + layer);
        final long t1 = System.currentTimeMillis();
        final int totalLay = KfzbTimeControlService.getTotalLayer();
        int lastLayer = 0;
        final HashMap<Integer, Integer> lastMatchWinIdMap = new HashMap<Integer, Integer>();
        if (layer < totalLay) {
            lastLayer = layer + 1;
            final int lastLayerRound = KfzbTimeControlService.getLayerBattleNum(lastLayer);
            final List<KfzbRuntimeMatch> lastmList = this.kfzbRuntimeMatchDao.getMatchBylayerRoundAndSeasonId(lastLayer, lastLayerRound, KfzbScheduleService.curSeasonId);
            for (final KfzbRuntimeMatch m : lastmList) {
                if (m.getLayerWinner() > 0) {
                    lastMatchWinIdMap.put(m.getMatchId(), m.getLayerWinner());
                }
                else {
                    final int ran = WebUtil.nextInt(2);
                    if (ran == 0) {
                        final int cId = m.getPlayer1Id();
                        if (cId > 0) {
                            lastMatchWinIdMap.put(m.getMatchId(), cId);
                        }
                        else {
                            lastMatchWinIdMap.put(m.getMatchId(), m.getPlayer2Id());
                        }
                    }
                    else {
                        final int cId = m.getPlayer2Id();
                        if (cId > 0) {
                            lastMatchWinIdMap.put(m.getMatchId(), cId);
                        }
                        else {
                            lastMatchWinIdMap.put(m.getMatchId(), m.getPlayer1Id());
                        }
                    }
                }
            }
        }
        else {
            final List<KfzbBattleWarrior> wList = this.kfzbBattleWarriorDao.getWarriorBySeasonId(KfzbScheduleService.curSeasonId);
            final int offset = 1 << totalLay;
            for (final KfzbBattleWarrior w : wList) {
                lastMatchWinIdMap.put(w.getBattlePos() + offset - 1, w.getCompetitorId());
            }
        }
        KfzbScheduleService.scheduleInfoLog.info("afterProcessWinMap" + layer);
        final int layerBattleNum = KfzbTimeControlService.getLayerBattleNum(layer);
        final int firstMatchId = this.getFirstMatchIdByLayer(layer);
        final int matchNum = this.getMatchNumByLayer(layer);
        final List<KfzbRuntimeMatch> mList = this.kfzbRuntimeMatchDao.getMatchBylayerAndSeasonId(layer, KfzbScheduleService.curSeasonId);
        final HashMap<Integer, KfzbRuntimeMatch> matchMap = new HashMap<Integer, KfzbRuntimeMatch>();
        for (final KfzbRuntimeMatch i : mList) {
            matchMap.put(KfzbRuntimeMatch.getMatchKey(i.getMatchId(), i.getRound()), i);
            KfzbTimeControlService.setNewLayerRound(i.getSeasonId(), i.getLayer(), i.getRound());
        }
        KfzbScheduleService.scheduleInfoLog.info("getMatchBylayerAndSeasonId" + layer);
        final List<KfzbRuntimeMatch> listNeedCreateMatch = new ArrayList<KfzbRuntimeMatch>();
        for (int mId = firstMatchId; mId < firstMatchId + matchNum; ++mId) {
            for (int round = layerBattleNum; round > 0; --round) {
                final int mkey = KfzbRuntimeMatch.getMatchKey(mId, round);
                KfzbRuntimeMatch j = matchMap.get(mkey);
                final int lastmkey = KfzbRuntimeMatch.getMatchKey(mId, round - 1);
                final KfzbRuntimeMatch lastm = matchMap.get(lastmkey);
                if (j == null) {
                    if (round == 1 || (lastm != null && lastm.getRoundWinner() > 0)) {
                        j = new KfzbRuntimeMatch();
                        j.setMatchId(mId);
                        j.setLayer(layer);
                        Integer p1 = lastMatchWinIdMap.get(j.getMatchId() << 1);
                        Integer p2 = lastMatchWinIdMap.get((j.getMatchId() << 1) + 1);
                        p1 = ((p1 == null) ? 0 : p1);
                        p2 = ((p2 == null) ? 0 : p2);
                        j.setPlayer1Id(p1);
                        j.setPlayer2Id(p2);
                        j.setRound(round);
                        if (round > 1) {
                            j.setPlayer1Win(lastm.getPlayer1Win());
                            j.setPlayer2Win(lastm.getPlayer2Win());
                            j.setLayerWinner(lastm.getLayerWinner());
                        }
                        j.setSeasonId(KfzbScheduleService.curSeasonId);
                        j.setStartTime(KfzbTimeControlService.getBattleTime(j.getMatchId(), j.getLayer(), j.getRound()));
                        listNeedCreateMatch.add(j);
                        break;
                    }
                }
                else if (j.getRoundWinner() == 0) {
                    break;
                }
            }
        }
        KfzbScheduleService.scheduleInfoLog.info("endCreateMatchList#");
        this.self.createMatchList(listNeedCreateMatch, layer);
        KfzbScheduleService.scheduleInfoLog.info("aftercreateMatchByLayer" + layer);
        final long t2 = System.currentTimeMillis();
        KfzbScheduleService.scheduleInfoLog.info("createMatchByLayerTime" + (t2 - t1));
    }
    
    private int getMatchNumByLayer(final int layer) {
        return 1 << layer >> 1;
    }
    
    private int getFirstMatchIdByLayer(final int layer) {
        return 1 << layer >> 1;
    }
    
    @Transactional
    @Override
    public int checkandGetWarriorData() {
        final List<KfzbBattleWarrior> wlist = this.kfzbBattleWarriorDao.getWarriorBySeasonId(KfzbScheduleService.curSeasonId);
        final int totalLay = KfzbTimeControlService.getTotalLayer();
        final int allPlayerNeed = this.getPlayerByLayer(totalLay);
        if (wlist.size() == 0) {
            this.loadZbWarriorData(totalLay, allPlayerNeed);
            return totalLay;
        }
        return 0;
    }
    
    private int getPlayerByLayer(final int totalLay) {
        return 1 << totalLay;
    }
    
    private void loadZbWarriorData(final int totalLay, final int allPlayerNeed) {
        final List<KfwdRuntimeResult> sortrList = new ArrayList<KfwdRuntimeResult>();
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        this.self = (IKfzbScheduleService)this.context.getBean("kfzbScheduleService");
    }
    
    @Override
	public void setApplicationContext(final ApplicationContext arg0) throws BeansException {
        this.context = arg0;
    }
    
    @Transactional
    @Override
    public void createMatch(final KfzbRuntimeMatch m) {
        this.kfzbRuntimeMatchDao.create((IModel)m);
        this.putNewRunTimeMatchToMatchMap(m);
        this.modifyRuntimeResultWithNewMatch(m);
    }
    
    @Transactional
    @Override
    public void createMatchList(final List<KfzbRuntimeMatch> listNeedCreateMatch, final int layer) {
        for (final KfzbRuntimeMatch m : listNeedCreateMatch) {
            KfzbScheduleService.scheduleInfoLog.info("createMatchTime#" + m.getMatchId());
            this.kfzbRuntimeMatchDao.create((IModel)m);
            this.putNewRunTimeMatchToMatchMap(m);
            KfzbScheduleService.scheduleInfoLog.info("createMatchAfterTime#" + m.getMatchId());
        }
        if (listNeedCreateMatch.size() == 0) {
            return;
        }
        final KfzbRuntimeMatch firstMatch = listNeedCreateMatch.get(0);
        if (firstMatch != null) {
            this.modifyRuntimeResultWithNewMatchByLayer(firstMatch.getSeasonId(), layer, firstMatch.getRound());
        }
    }
    
    private void modifyRuntimeResultWithNewMatchByLayer(final int seasonId, final int layer, final int round) {
        this.kfzbRuntimeResultDao.updateResultByCreateMatch(seasonId, layer, round);
    }
    
    private void modifyRuntimeResultWithNewMatch(final KfzbRuntimeMatch m) {
        final int cId1 = m.getPlayer1Id();
        final int cId2 = m.getPlayer2Id();
        if (cId1 > 0) {
            final KfzbRuntimeResult r = this.kfzbRuntimeResultDao.getInfoByCIdAndSeasonId(cId1, m.getSeasonId());
            if (r.getLayer() > m.getLayer()) {
                r.setLastres(r.getRes());
                r.setRes(0);
                r.setLayer(m.getLayer());
                r.setRound(m.getRound());
                this.kfzbRuntimeResultDao.update((IModel)r);
            }
            else if (r.getLayer() == m.getLayer() && r.getRound() < m.getRound()) {
                r.setRound(m.getRound());
                this.kfzbRuntimeResultDao.update((IModel)r);
            }
        }
        if (cId2 > 0) {
            final KfzbRuntimeResult r = this.kfzbRuntimeResultDao.getInfoByCIdAndSeasonId(cId2, m.getSeasonId());
            if (r.getLayer() > m.getLayer()) {
                r.setLastres(r.getRes());
                r.setRes(0);
                r.setLayer(m.getLayer());
                r.setRound(m.getRound());
                this.kfzbRuntimeResultDao.update((IModel)r);
            }
            else if (r.getLayer() == m.getLayer() && r.getRound() < m.getRound()) {
                r.setRound(m.getRound());
                this.kfzbRuntimeResultDao.update((IModel)r);
            }
        }
    }
    
    @Override
    public void buildNewMatchAndRun(final KfzbRuntimeMatch resMatch) {
        final int layerRounds = KfzbTimeControlService.getLayerBattleNum(resMatch.getLayer());
        this.modifyRtMatchInfoByMatchRes(resMatch, layerRounds);
        this.putNewRunTimeMatchToMatchMap(resMatch);
        if (resMatch.getRound() == layerRounds) {
            this.matchWinnerMap.put(resMatch.getMatchId(), resMatch.getLayerWinner());
            final int reMainNum = this.minusLayerMatchNUm(resMatch.getLayer());
            KfzbScheduleService.scheduleInfoLog.info("finMatch" + KfzbLogUtils.getMachInfo(resMatch) + "#remain=" + reMainNum);
            if (reMainNum <= 0) {
                this.self.runNextLayerMatch(resMatch.getSeasonId(), resMatch.getLayer() - 1);
            }
            return;
        }
        if (resMatch.getRound() < layerRounds) {
            final KfzbRuntimeMatch newMatch = this.self.buildNewMatch(resMatch);
            long delay = KfzbTimeControlService.getLayerRoundScheduleTime(1, newMatch.getLayer(), newMatch.getRound()) - System.currentTimeMillis() - 3000L;
            if (delay < 0L) {
                delay = 0L;
            }
            this.exeutors.schedule(new Runnable() {
                @Override
                public void run() {
                    KfzbScheduleService.scheduleInfoLog.info("layerRoundStart#" + newMatch.getLayer() + "-" + newMatch.getRound());
                    KfzbScheduleService.this.runMatch(newMatch);
                }
            }, delay, TimeUnit.MILLISECONDS);
        }
    }
    
    private void putNewRunTimeMatchToMatchMap(final KfzbRuntimeMatch resMatch) {
        final KfzbRuntimeMatch cpMatch = new KfzbRuntimeMatch();
        BeanUtils.copyProperties(resMatch, cpMatch);
        this.matchMap.put(cpMatch.getMatchKey(), cpMatch);
    }
    
    @Override
    public void runNextLayerMatch(final int seasonId, final int layer) {
        if (seasonId != KfzbScheduleService.curSeasonId) {
            return;
        }
        KfzbScheduleService.scheduleInfoLog.info("runNextLayer" + layer);
        if (layer <= 0) {
            KfzbScheduleService.globalstate = 7;
            KfzbScheduleService.lastSetGlobalStateTime = System.currentTimeMillis();
            this.doSendWinnerToGw(seasonId);
            return;
        }
        this.createMatchByLayer(layer);
        this.scheduleMatchByLayer(layer);
    }
    
    private void doSendWinnerToGw(final int seasonId) {
        if (KfzbScheduleService.winnerInfoSendThread == null) {
            (KfzbScheduleService.winnerInfoSendThread = new SendWinnerInfoThread("sendwinnerInfoThread")).start();
        }
        KfzbScheduleService.sendWinnerInfoQueue.add(seasonId);
    }
    
    public boolean doSendWinnerInfo(final Integer seasonId) throws HttpUtil.ServerUnavailable, IOException {
        final List<KfzbRuntimeResult> top16Res = this.kfzbRuntimeResultDao.getTop16PlayerInfo(seasonId);
        final KfzbWinnerInfo winnerInfo = new KfzbWinnerInfo();
        for (final KfzbRuntimeResult kResult : top16Res) {
            final KfzbTopPlayerInfo toplayerInfo = new KfzbTopPlayerInfo();
            final KfzbBattleWarrior warrior = this.kfzbBattleWarriorDao.getPlayerByCId(seasonId, kResult.getCompetitorId());
            BeanUtils.copyProperties(warrior, toplayerInfo);
            int pos = kResult.getLayer();
            if (pos == 1 && kResult.getIsfinsh() == 0) {
                pos = -1;
            }
            toplayerInfo.setPos(pos);
            winnerInfo.getList().add(toplayerInfo);
        }
        winnerInfo.setSeasonId(seasonId);
        return this.sendWinnerInfoToGW(winnerInfo);
    }
    
    private boolean sendWinnerInfoToGW(final KfzbWinnerInfo winnerInfo) throws IOException, HttpUtil.ServerUnavailable {
        final Request request = new Request();
        request.setCommand(Command.KFZB_WINNER_INFO);
        request.setMessage(winnerInfo);
        final Response res = this.kfzbSeasonService.getConnection().sendSyncAndGetResponse(request);
        final Integer resInteger = (Integer)res.getMessage();
        return resInteger != null && resInteger == 1;
    }
    
    private MatchServerEntity getMatchServerEntityInfo() {
        final MatchServerEntity gs = new MatchServerEntity();
        gs.setMatchSvrName(Configuration.getProperty("match.name"));
        gs.setMatchUrl(Configuration.getProperty("match.url"));
        return gs;
    }
    
    @Transactional
    @Override
    public KfzbRuntimeMatch buildNewMatch(final KfzbRuntimeMatch resMatch) {
        final KfzbRuntimeMatch newMatch = new KfzbRuntimeMatch();
        BeanUtils.copyProperties(resMatch, newMatch);
        newMatch.setRound(resMatch.getRound() + 1);
        newMatch.setStartTime(KfzbTimeControlService.getBattleTime(newMatch.getMatchId(), newMatch.getLayer(), newMatch.getRound()));
        newMatch.setReportId(null);
        newMatch.setPk(null);
        newMatch.setRoundWinner(0);
        this.kfzbRuntimeMatchDao.create((IModel)newMatch);
        this.putNewRunTimeMatchToMatchMap(newMatch);
        this.modifyRuntimeResultWithNewMatch(newMatch);
        return newMatch;
    }
    
    @Override
    public KfzbState getWdState() {
        final KfzbState wdState = new KfzbState();
        wdState.setGlobalState(1);
        final Tuple3<Integer, Long, Integer> res = KfzbTimeControlService.getNowStateAndCDAndSeasonId();
        if (res == null) {
            return null;
        }
        wdState.setGlobalState(res.get_1());
        wdState.setNextGlobalStateCD(res.get_2());
        wdState.setSeasonId(res.get_3());
        final int[] curLayerAndRound = KfzbTimeControlService.getLayerAndRound();
        wdState.setLayer(curLayerAndRound[0]);
        wdState.setRound(curLayerAndRound[1]);
        if (wdState.getGlobalState() >= 50) {
            wdState.setTotalLayer(KfzbTimeControlService.getTotalLayer());
        }
        if (wdState.getGlobalState() >= 80) {
            wdState.setCurrentTimestamp(System.currentTimeMillis());
            return wdState;
        }
        wdState.setBattleTime(KfzbTimeControlService.getBattleTime(0, wdState.getLayer(), wdState.getRound()));
        wdState.setLayerRound(KfzbTimeControlService.getLayerBattleNum(wdState.getLayer()));
        final int[] nextLayerAndRound = KfzbTimeControlService.getNextLayerAndRound(wdState.getLayer(), wdState.getRound());
        wdState.setNextLayer(nextLayerAndRound[0]);
        wdState.setNextRound(nextLayerAndRound[1]);
        wdState.setNextBatTime(KfzbTimeControlService.getBattleTime(0, wdState.getNextLayer(), wdState.getNextRound()));
        if (wdState.getNextBatTime() != null) {
            wdState.setNextRBegTime(new Date(wdState.getNextBatTime().getTime() - (KfzbTimeControlService.getRoundInteval() - KfzbTimeControlService.getBattleInterval()) * 1000L));
        }
        if (wdState.getGlobalState() > 0 && KfzbScheduleService.globalstate < 3) {
            wdState.setGlobalState(0);
            wdState.setNextGlobalStateCD(0L);
        }
        if (wdState.getGlobalState() >= 51) {
            final Map<Integer, KfzbBattleInfo> p2Map = this.phase2BattleInfoMap;
            final KfzbPhase2Info p2Info = new KfzbPhase2Info();
            final KfzbBattleInfo bInfo = p2Map.get(10);
            p2Info.setCurLayer(KfzbTimeControlService.currentLay);
            p2Info.setCurRound(KfzbTimeControlService.currentRound);
            p2Info.setMap(p2Map);
            wdState.setPhase2Info(p2Info);
        }
        final long now = System.currentTimeMillis();
        if (KfzbScheduleService.globalstate == 7 && now > KfzbScheduleService.lastSetGlobalStateTime + 45000L) {
            wdState.setGlobalState(70);
        }
        wdState.setCurrentTimestamp(now);
        return wdState;
    }
    
    @Override
    public KfzbSignResult processSynPlayerData(final String gameServer, final KfzbSignInfo signInfo, final boolean isSignUp) {
        final int sPId = signInfo.getPlayerInfo().getPlayerId();
        final Integer sCId = signInfo.getPlayerInfo().getCompetitorId();
        Integer state = null;
        if (KfzbTimeControlService.getNowStateAndCDAndSeasonId() != null) {
            state = KfzbTimeControlService.getNowStateAndCDAndSeasonId().get_1();
        }
        if (KfzbScheduleService.curSeasonId == 0 || state == null) {
            final KfzbSignResult res = new KfzbSignResult();
            res.setPlayerId(sPId);
            res.setState(0);
            KfzbScheduleService.commonLog.info("syn state null");
            return res;
        }
        if (state != null && state >= 50 && this.inBattlePrePareTimeDefLock(sCId)) {
            final KfzbSignResult res = new KfzbSignResult();
            res.setPlayerId(sPId);
            res.setState(0);
            return res;
        }
        final KfzbSignResult res = this.kfzbMatchService.signUp(gameServer, signInfo, isSignUp);
        return res;
    }
    
    private boolean inBattlePrePareTimeDefLock(final Integer sCId) {
        if (sCId == null) {
            return true;
        }
        final long now = System.currentTimeMillis();
        final KfzbRTMatchInfo rtInfo = this.kfzbMatchInfoMap.get(sCId);
        if (rtInfo == null) {
            return true;
        }
        final int layerRound = KfzbTimeControlService.getLayerBattleNum(rtInfo.getLayer());
        final Date battleTime = rtInfo.getRoundBattleTime();
        if (battleTime == null) {
            return true;
        }
        final long cd = battleTime.getTime() - now;
        if (cd < 8000L) {
            KfzbScheduleService.commonLog.info("cId=" + sCId + "#round=" + rtInfo.getLayer() + "#sround=" + rtInfo.getRound() + "#inBattlePrePareTime#time=" + new Date() + "#battleTime=" + battleTime);
            return true;
        }
        return false;
    }
    
    private boolean inSupportTime(final int sCId) {
        final long now = System.currentTimeMillis();
        final KfzbRTMatchInfo rtInfo = this.kfzbMatchInfoMap.get(sCId);
        if (rtInfo == null) {
            return false;
        }
        final Date battleTime = rtInfo.getRoundBattleTime();
        if (battleTime == null) {
            return false;
        }
        final long cd = battleTime.getTime() - now;
        return true;
    }
    
    @Override
    public KfzbRTMatchInfo getRTMatchInfo(final KfzbPlayerKey pKey, final String gameServer) {
        if (!this.hasScheduledMatch(KfzbScheduleService.curSeasonId)) {
            return null;
        }
        final int requestCId;
        int cId = requestCId = pKey.getCompetitorId();
        cId = this.getRealCId(cId, gameServer);
        if (cId <= 0) {
            return null;
        }
        KfzbRTMatchInfo rtInfo = this.kfzbMatchInfoMap.get(cId);
        if (rtInfo == null) {
            return null;
        }
        rtInfo = this.copyRtInfo(rtInfo);
        final long nowTime = System.currentTimeMillis();
        final Date bTime = rtInfo.getRoundBattleTime();
        final Date lastBTime = rtInfo.getLastRoundBattleTime();
        rtInfo.setShowMatchId(rtInfo.getMatchId());
        boolean showlast = false;
        if (bTime != null) {
            final long realcd;
            long cd = realcd = bTime.getTime() - nowTime;
            if (cd < 0L) {
                cd = 0L;
            }
            rtInfo.setNextRoundCD(cd);
            if (lastBTime != null) {
                final long lastcd = lastBTime.getTime() - nowTime + 30000L;
                if (lastcd < 30000L && lastcd > 0L) {
                    showlast = true;
                    rtInfo.setNextShowRoundCD(lastcd);
                    if (rtInfo.getRound() > 1) {
                        rtInfo.setShowRound(rtInfo.getRound() - 1);
                        rtInfo.setShowlayer(rtInfo.getLayer());
                    }
                    else {
                        rtInfo.setShowRound(KfzbTimeControlService.getLayerBattleNum(rtInfo.getLayer() + 1));
                        rtInfo.setShowlayer(rtInfo.getLayer() + 1);
                        rtInfo.setShowMatchId(rtInfo.getLastMatchId());
                    }
                }
                else {
                    rtInfo.setNextShowRoundCD((realcd + 30000L > 0L) ? (realcd + 30000L) : 0L);
                    rtInfo.setShowRound(rtInfo.getRound());
                    rtInfo.setShowlayer(rtInfo.getLayer());
                }
            }
            else {
                rtInfo.setNextShowRoundCD((realcd + 30000L > 0L) ? (realcd + 30000L) : 0L);
                rtInfo.setShowRound(rtInfo.getRound());
                rtInfo.setShowlayer(rtInfo.getLayer());
            }
        }
        if (showlast) {
            rtInfo.setShowcId1(rtInfo.getLastcId1());
            rtInfo.setShowcId2(rtInfo.getLastcId2());
        }
        else {
            rtInfo.setShowcId1(rtInfo.getcId1());
            rtInfo.setShowcId2(rtInfo.getcId2());
        }
        if (requestCId <= 0) {
            rtInfo.setState(0);
        }
        rtInfo.setShowLayerRound(KfzbTimeControlService.getLayerBattleNum(rtInfo.getShowlayer()));
        return rtInfo;
    }
    
    private int getRealCId(int cId, final String gameServer) {
        if (cId <= 0) {
            final int totalLay = KfzbTimeControlService.getTotalLayer();
            final int allPlayerNeed = this.getPlayerByLayer(totalLay);
            int hash = gameServer.hashCode();
            if (hash < 0) {
                hash = -hash;
            }
            int wdPos = hash % allPlayerNeed;
            int tmpcId = this.posCIdMap.get(wdPos);
            if (cId < 0) {
                tmpcId = -cId;
                final Integer wdPost = this.posWdPosMap.get(tmpcId);
                if (wdPost == null) {
                    return 0;
                }
                wdPos = wdPost;
            }
            final int layerR = this.layerRunning;
            if (layerR == totalLay) {
                cId = tmpcId;
            }
            else {
                if (layerR >= totalLay) {
                    return 0;
                }
                final int tSourceMatchId = (allPlayerNeed >> 1) + (wdPos >> 1);
                final int layerDif = totalLay - layerR - 1;
                final int targetMatchId = tSourceMatchId >> layerDif;
                final Integer tcId2 = this.matchWinnerMap.get(targetMatchId);
                if (tcId2 == null) {
                    return 0;
                }
                cId = tcId2;
            }
        }
        return cId;
    }
    
    private void makeNewRtMatchInfoByNewMatch(final KfzbRuntimeMatch match) {
        if (match == null) {
            return;
        }
        final int p1 = match.getPlayer1Id();
        final int p2 = match.getPlayer2Id();
        final int matchId = match.getMatchId();
        final int layer = match.getLayer();
        final int round = match.getRound();
        final KfzbRTMatchInfo mInfo1 = this.copyRtInfo(this.kfzbMatchInfoMap.get(p1));
        final int oldLayer1 = mInfo1.getLayer();
        final int oldRound1 = mInfo1.getRound();
        if (oldLayer1 != match.getLayer() || oldRound1 != match.getRound()) {
            if (matchId != mInfo1.getMatchId()) {
                mInfo1.setLastMatchId(mInfo1.getMatchId());
            }
            mInfo1.setLastcId1(mInfo1.getcId1());
            mInfo1.setLastcId2(mInfo1.getcId2());
            mInfo1.setMatchId(matchId);
            mInfo1.setLayer(layer);
            mInfo1.setRound(round);
            mInfo1.setcId1(p1);
            mInfo1.setcId2(p2);
            if (round == 1) {
                mInfo1.setLastRes(mInfo1.getRes());
                mInfo1.setRes(0);
            }
            mInfo1.setLastRoundBattleTime(KfzbTimeControlService.getLastRoundBattleTime(matchId, layer, round));
            mInfo1.setRoundBattleTime(KfzbTimeControlService.getBattleTime(matchId, layer, round));
        }
        final KfzbRTMatchInfo mInfo2 = this.copyRtInfo(this.kfzbMatchInfoMap.get(p2));
        final int oldLayer2 = mInfo2.getLayer();
        final int oldRound2 = mInfo2.getRound();
        if (oldLayer2 != match.getLayer() || oldRound2 != match.getRound()) {
            if (matchId != mInfo2.getMatchId()) {
                mInfo2.setLastMatchId(mInfo2.getMatchId());
            }
            mInfo2.setLastcId1(mInfo2.getcId1());
            mInfo2.setLastcId2(mInfo2.getcId2());
            mInfo2.setMatchId(matchId);
            mInfo2.setLayer(layer);
            mInfo2.setRound(round);
            mInfo2.setcId1(p1);
            mInfo2.setcId2(p2);
            if (round == 1) {
                mInfo2.setLastRes(mInfo2.getRes());
                mInfo2.setRes(0);
            }
            mInfo2.setLastRoundBattleTime(KfzbTimeControlService.getLastRoundBattleTime(matchId, layer, round));
            mInfo2.setRoundBattleTime(KfzbTimeControlService.getBattleTime(matchId, layer, round));
        }
        this.kfzbMatchInfoMap.put(p1, mInfo1);
        this.kfzbMatchInfoMap.put(p2, mInfo2);
        this.setNewPhase2MatchInfo(match);
        KfzbTimeControlService.setNewLayerRound(match.getSeasonId(), match.getLayer(), match.getRound());
        KfzbScheduleService.rtInfoLog.info("mN=" + p1 + KfzbLogUtils.getRtInfoLog(this.kfzbMatchInfoMap.get(p1)));
        KfzbScheduleService.rtInfoLog.info("mN=" + p2 + KfzbLogUtils.getRtInfoLog(this.kfzbMatchInfoMap.get(p2)));
    }
    
    private void setNewPhase2MatchInfo(final KfzbRuntimeMatch match) {
        final int matchId = match.getMatchId();
        if (matchId > 15) {
            return;
        }
        KfzbBattleInfo battleInfo = this.phase2BattleInfoMap.get(matchId);
        if (battleInfo == null) {
            battleInfo = new KfzbBattleInfo();
            final int pId1 = match.getPlayer1Id();
            final int pId2 = match.getPlayer2Id();
            battleInfo.setMatchId(matchId);
            battleInfo.setRound(match.getRound());
            battleInfo.setTerrain(KfzbCommonConstants.getRanTerrain(matchId, match.getRound(), match.getSeasonId()));
            battleInfo.setBattleTime(KfzbTimeControlService.getBattleTime(matchId, match.getLayer(), match.getRound()));
            final KfzbPlayerInfo zbPlayerInfo1 = new KfzbPlayerInfo();
            final KfzbBattleWarrior w1 = this.kfzbCacheManager.getBattleWarrior(pId1);
            BeanUtils.copyProperties(w1, zbPlayerInfo1);
            battleInfo.setP1(zbPlayerInfo1);
            int roundRes = 0;
            if (match.getRoundWinner() == match.getPlayer1Id() && match.getPlayer1Id() != 0) {
                roundRes = 1;
            }
            if (match.getRoundWinner() == match.getPlayer2Id() && match.getPlayer2Id() != 0) {
                roundRes = 2;
            }
            battleInfo.setBattleRes(KfzbCommonConstants.addRoundBattleRes(battleInfo.getBattleRes(), match.getRound(), roundRes));
            final KfzbPlayerInfo zbPlayerInfo2 = new KfzbPlayerInfo();
            final KfzbBattleWarrior w2 = this.kfzbCacheManager.getBattleWarrior(pId2);
            BeanUtils.copyProperties(w2, zbPlayerInfo2);
            battleInfo.setP2(zbPlayerInfo2);
            final KfzbBattleWarriorGeneral g1 = this.kfzbCacheManager.getBattleWarriorGeneral(pId1);
            final CampArmyParam[] campArmys1 = g1.getCampList();
            final KfwdGInfo gInfo1 = this.getGInfoFromCampArmys(campArmys1);
            battleInfo.setG1(gInfo1);
            final KfzbBattleWarriorGeneral g2 = this.kfzbCacheManager.getBattleWarriorGeneral(pId2);
            final CampArmyParam[] campArmys2 = g2.getCampList();
            final KfwdGInfo gInfo2 = this.getGInfoFromCampArmys(campArmys2);
            battleInfo.setG2(gInfo2);
            battleInfo.setP1Win(match.getPlayer1Win());
            battleInfo.setP2Win(match.getPlayer2Win());
            battleInfo.setLayerRound(KfzbTimeControlService.getLayerBattleNum(match.getLayer()));
            final KfzbMatchSupportInfo supInfo = this.kfzbSupportInfoMap.get(matchId);
            if (supInfo != null) {
                final int cId1 = supInfo.getcId1();
                final int cId2 = supInfo.getcId2();
                final int sup1 = supInfo.getSupportNum1();
                final int sup2 = supInfo.getSupportNum2();
                if (pId1 == cId1) {
                    battleInfo.setSup1(sup1);
                    battleInfo.setSup2(sup2);
                }
                else {
                    battleInfo.setSup2(sup1);
                    battleInfo.setSup1(sup2);
                }
            }
            final boolean needChange = KfzbCommonConstants.getNeedChange(match.getSeasonId(), matchId, match.getRound());
            battleInfo.setNeedChange(needChange);
            this.phase2BattleInfoMap.put(matchId, battleInfo);
        }
        else {
            battleInfo.setRound(match.getRound());
            battleInfo.setTerrain(KfzbCommonConstants.getRanTerrain(matchId, match.getRound(), match.getSeasonId()));
            battleInfo.setBattleTime(KfzbTimeControlService.getBattleTime(matchId, match.getLayer(), match.getRound()));
            int roundRes2 = 0;
            if (match.getRoundWinner() == match.getPlayer1Id() && match.getPlayer1Id() != 0) {
                roundRes2 = 1;
            }
            if (match.getRoundWinner() == match.getPlayer2Id() && match.getPlayer2Id() != 0) {
                roundRes2 = 2;
            }
            battleInfo.setBattleRes(KfzbCommonConstants.addRoundBattleRes(battleInfo.getBattleRes(), match.getRound(), roundRes2));
            battleInfo.setP1Win(match.getPlayer1Win());
            battleInfo.setP2Win(match.getPlayer2Win());
            final boolean needChange2 = KfzbCommonConstants.getNeedChange(match.getSeasonId(), matchId, match.getRound());
            battleInfo.setNeedChange(needChange2);
        }
        KfzbScheduleService.rtInfoLog.info(KfzbLogUtils.getPhase2RtMatchInfo(battleInfo));
    }
    
    private void modifyRtMatchInfoByMatchRes(final KfzbRuntimeMatch resMatch, final int layerRounds) {
        final int p1 = resMatch.getPlayer1Id();
        final int p2 = resMatch.getPlayer2Id();
        final KfzbRTMatchInfo rtInfo1 = this.copyRtInfo(this.kfzbMatchInfoMap.get(p1));
        final KfzbRTMatchInfo rtInfo2 = this.copyRtInfo(this.kfzbMatchInfoMap.get(p2));
        if (resMatch.getRoundWinner() == resMatch.getPlayer1Id()) {
            if (rtInfo1 != null) {
                rtInfo1.addRoundBattleRes(resMatch.getRound(), 1);
                rtInfo1.setLastReport(resMatch.getReportId());
            }
            if (rtInfo2 != null) {
                rtInfo2.addRoundBattleRes(resMatch.getRound(), 2);
                rtInfo2.setLastReport(resMatch.getReportId());
            }
        }
        else {
            if (rtInfo1 != null) {
                rtInfo1.addRoundBattleRes(resMatch.getRound(), 2);
                rtInfo1.setLastReport(resMatch.getReportId());
            }
            if (rtInfo2 != null) {
                rtInfo2.addRoundBattleRes(resMatch.getRound(), 1);
                rtInfo2.setLastReport(resMatch.getReportId());
            }
        }
        boolean allMatchOver = false;
        if (rtInfo1.getRound() == layerRounds) {
            if (resMatch.getLayerWinner() == resMatch.getPlayer1Id()) {
                rtInfo2.setState(2);
            }
            else if (resMatch.getLayerWinner() == resMatch.getPlayer2Id()) {
                rtInfo1.setState(2);
            }
            if (resMatch.getLayer() == 1) {
                allMatchOver = true;
            }
        }
        this.kfzbMatchInfoMap.put(p1, rtInfo1);
        if (p2 != 0) {
            this.kfzbMatchInfoMap.put(p2, rtInfo2);
        }
        KfzbScheduleService.rtInfoLog.info("aW=" + p1 + KfzbLogUtils.getRtInfoLog(this.kfzbMatchInfoMap.get(p1)));
        KfzbScheduleService.rtInfoLog.info("aW=" + p2 + KfzbLogUtils.getRtInfoLog(this.kfzbMatchInfoMap.get(p2)));
        this.setNewPhase2MatchInfo(resMatch);
        if (p1 > 0) {
            final KfzbTicketReward rewardInfo = new KfzbTicketReward();
            rewardInfo.setcId(rtInfo1.getcId());
            rewardInfo.setGameServer(this.kfzbCacheManager.getGameServerByCId(rtInfo1.getcId()));
            rewardInfo.setFinish(rtInfo1.getState() == 2);
            rewardInfo.setRewardTicketList(KfzbTimeControlService.getTicketByLayerAndFinish(resMatch.getLayer(), rewardInfo.isFinish(), allMatchOver));
            if (rtInfo1.getRound() == layerRounds && !rewardInfo.isFinish() && !allMatchOver) {
                rewardInfo.setRewardTicketList(KfzbTimeControlService.getTicketByLayerAndFinish(resMatch.getLayer() - 1, rewardInfo.isFinish(), allMatchOver));
            }
            KfzbTicketRewardNoticeInfo.addNoticeInfo(rewardInfo);
        }
        if (p2 > 0) {
            final KfzbTicketReward rewardInfo = new KfzbTicketReward();
            rewardInfo.setcId(rtInfo2.getcId());
            rewardInfo.setGameServer(this.kfzbCacheManager.getGameServerByCId(rtInfo2.getcId()));
            rewardInfo.setFinish(rtInfo2.getState() == 2);
            rewardInfo.setRewardTicketList(KfzbTimeControlService.getTicketByLayerAndFinish(resMatch.getLayer(), rewardInfo.isFinish(), allMatchOver));
            if (rtInfo2.getRound() == layerRounds && !rewardInfo.isFinish() && !allMatchOver) {
                rewardInfo.setRewardTicketList(KfzbTimeControlService.getTicketByLayerAndFinish(resMatch.getLayer() - 1, rewardInfo.isFinish(), allMatchOver));
            }
            KfzbTicketRewardNoticeInfo.addNoticeInfo(rewardInfo);
        }
    }
    
    private KfzbRTMatchInfo copyRtInfo(final KfzbRTMatchInfo kfzbRTMatchInfo) {
        if (kfzbRTMatchInfo == null) {
            return null;
        }
        final KfzbRTMatchInfo rtInfo = new KfzbRTMatchInfo();
        rtInfo.setcId(kfzbRTMatchInfo.getcId());
        rtInfo.setMatchId(kfzbRTMatchInfo.getMatchId());
        rtInfo.setLastMatchId(kfzbRTMatchInfo.getLastMatchId());
        rtInfo.setcId1(kfzbRTMatchInfo.getcId1());
        rtInfo.setcId2(kfzbRTMatchInfo.getcId2());
        rtInfo.setLastcId1(kfzbRTMatchInfo.getLastcId1());
        rtInfo.setLastcId2(kfzbRTMatchInfo.getLastcId2());
        rtInfo.setRes(kfzbRTMatchInfo.getRes());
        rtInfo.setLastRes(kfzbRTMatchInfo.getLastRes());
        rtInfo.setLayer(kfzbRTMatchInfo.getLayer());
        rtInfo.setShowlayer(kfzbRTMatchInfo.getShowlayer());
        rtInfo.setRound(kfzbRTMatchInfo.getRound());
        rtInfo.setShowRound(kfzbRTMatchInfo.getShowRound());
        rtInfo.setState(kfzbRTMatchInfo.getState());
        rtInfo.setNextRoundCD(kfzbRTMatchInfo.getNextRoundCD());
        rtInfo.setNextShowRoundCD(kfzbRTMatchInfo.getNextShowRoundCD());
        rtInfo.setLastReport(kfzbRTMatchInfo.getLastReport());
        rtInfo.setRoundBattleTime(kfzbRTMatchInfo.getRoundBattleTime());
        rtInfo.setLastRoundBattleTime(kfzbRTMatchInfo.getLastRoundBattleTime());
        rtInfo.setLastSynTime(kfzbRTMatchInfo.getLastSynTime());
        return rtInfo;
    }
    
    @Override
    public void processRTSupport(final KfzbRTSupport supInfo) {
        if (!this.hasScheduledMatch(KfzbScheduleService.curSeasonId)) {
            return;
        }
        if (KfzbScheduleService.curSeasonId != supInfo.getSeasonId()) {
            return;
        }
        final int cId1 = supInfo.getcId();
        if (cId1 > 0 && !this.inSupportTime(cId1)) {
            return;
        }
        final int matchKey;
        final int matchId = matchKey = supInfo.getMatchId();
        synchronized (KfzbScheduleService.matchLockArray[matchId % 1024]) {
            KfzbMatchSupportInfo sInfo = this.kfzbSupportInfoMap.get(matchKey);
            if (sInfo == null) {
                sInfo = new KfzbMatchSupportInfo();
                sInfo.setMatchId(matchId);
                sInfo.setSupportNum1(supInfo.getSupAdd());
                sInfo.setSeasonId(KfzbScheduleService.curSeasonId);
                sInfo.setcId1(supInfo.getcId());
            }
            else {
                sInfo.setSeasonId(KfzbScheduleService.curSeasonId);
                if (sInfo.getcId1() == supInfo.getcId()) {
                    sInfo.setSupportNum1(supInfo.getSupAdd() + sInfo.getSupportNum1());
                }
                else {
                    sInfo.setSupportNum2(supInfo.getSupAdd() + sInfo.getSupportNum2());
                }
            }
            final Long lastSupportTime = this.kfzbSupportTimeMap.get(matchId);
            final long now = System.currentTimeMillis();
            if (lastSupportTime == null) {
                this.self.saveMatchSupport(sInfo);
                this.kfzbSupportTimeMap.put(matchId, now);
            }
            else if (now - lastSupportTime > 2000L) {
                this.self.saveMatchSupport(sInfo);
                this.kfzbSupportTimeMap.put(matchId, now);
            }
            this.kfzbSupportInfoMap.put(matchKey, sInfo);
            final KfzbBattleInfo battleInfo = this.phase2BattleInfoMap.get(sInfo.getMatchId());
            if (battleInfo != null) {
                if (battleInfo.getP1().getCompetitorId() == sInfo.getcId1()) {
                    battleInfo.setSup1(sInfo.getSupportNum1());
                    battleInfo.setSup2(sInfo.getSupportNum2());
                }
                else {
                    battleInfo.setSup2(sInfo.getSupportNum1());
                    battleInfo.setSup1(sInfo.getSupportNum2());
                }
            }
        }
        // monitorexit(KfzbScheduleService.matchLockArray[matchId % 1024])
    }
    
    @Transactional
    @Override
    public void saveMatchSupport(final KfzbMatchSupportInfo sInfo) {
        KfzbRuntimeSupport sup = this.kfzbRuntimeSupportDao.getSupport(sInfo.getSeasonId(), sInfo.getMatchId());
        if (sup == null) {
            sup = new KfzbRuntimeSupport();
            sup.setMatchId(sInfo.getMatchId());
            sup.setSeasonId(sInfo.getSeasonId());
            sup.setSupportNum1(sInfo.getSupportNum1());
            sup.setSupportNum2(sInfo.getSupportNum2());
            sup.setcId1(sInfo.getcId1());
            sup.setcId2(sInfo.getcId2());
            this.kfzbRuntimeSupportDao.create((IModel)sup);
        }
        else {
            sup.setSupportNum1(sInfo.getSupportNum1());
            sup.setSupportNum2(sInfo.getSupportNum2());
            this.kfzbRuntimeSupportDao.update((IModel)sup);
        }
    }
    
    @Override
    public KfzbMatchSupport getRTSupportInfo(final KfzbMatchKey matchKey) {
        if (!this.hasScheduledMatch(KfzbScheduleService.curSeasonId)) {
            return null;
        }
        final KfzbMatchSupportInfo sInfo = this.kfzbSupportInfoMap.get(KfzbRuntimeMatch.getMatchKey(matchKey.getMatchId(), matchKey.getRound()));
        final KfzbMatchSupport msInfo = new KfzbMatchSupport();
        msInfo.setSeasonId(KfzbScheduleService.curSeasonId);
        msInfo.setMatchId(matchKey.getMatchId());
        msInfo.setRound(matchKey.getRound());
        if (sInfo != null) {
            BeanUtils.copyProperties(sInfo, msInfo);
        }
        return msInfo;
    }
    
    @Override
    public KfzbMatchInfo getMatchInfo(final KfzbMatchKey matchKey) {
        if (matchKey.getSeasonId() != KfzbScheduleService.curSeasonId) {
            return null;
        }
        if (!this.hasScheduledMatch(KfzbScheduleService.curSeasonId)) {
            return null;
        }
        final KfzbMatchInfo mInfo = new KfzbMatchInfo();
        final int key = KfzbRuntimeMatch.getMatchKey(matchKey.getMatchId(), matchKey.getRound());
        final KfzbRuntimeMatch mInfo2 = this.matchMap.get(key);
        BeanUtils.copyProperties(mInfo2, mInfo);
        return mInfo;
    }
    
    @Override
    public void setPlayerLastSynTime(final int competitorId, final long sessionTimestamp) {
        final KfzbRTMatchInfo mInfo1 = this.kfzbMatchInfoMap.get(competitorId);
        if (mInfo1 == null) {
            return;
        }
        final long nowTime = System.currentTimeMillis();
        mInfo1.setLastSynTime(nowTime);
        if (mInfo1.getcId1() == mInfo1.getcId()) {
            final KfzbRTMatchInfo mInfo2 = this.kfzbMatchInfoMap.get(mInfo1.getcId2());
            mInfo2.setLastSynTime(nowTime);
        }
        else {
            final KfzbRTMatchInfo mInfo2 = this.kfzbMatchInfoMap.get(mInfo1.getcId1());
            mInfo2.setLastSynTime(nowTime);
        }
    }
    
    @Override
    public KfzbPhase1RewardInfoList getKfzbRewardListInfo(final List<Integer> cIdList) {
        final KfzbPhase1RewardInfoList res = new KfzbPhase1RewardInfoList();
        final int totalLayer = KfzbTimeControlService.getTotalLayer();
        for (final Integer cId : cIdList) {
            final KfzbRTMatchInfo rtInfo = this.kfzbMatchInfoMap.get(cId);
            if (rtInfo != null) {
                final int layer = rtInfo.getLayer();
                final int layerRounds = KfzbTimeControlService.getLayerBattleNum(layer);
                final boolean isFinish = rtInfo.getState() == 2;
                final KfzbPhase1RewardInfo kfzbRewardInfo = new KfzbPhase1RewardInfo();
                kfzbRewardInfo.setcId(cId);
                kfzbRewardInfo.setFinish(isFinish);
                kfzbRewardInfo.setTotalLayer(totalLayer);
                kfzbRewardInfo.setRewardTicketList(KfzbTimeControlService.getTicketByLayerAndFinish(layer, isFinish, KfzbScheduleService.globalstate == 7));
                final int battleRes = rtInfo.getRes();
                final int[] bres = KfzbCommonConstants.getBattleResByRes(battleRes, layerRounds);
                if (rtInfo.getRound() == layerRounds && bres[layerRounds - 1] > 0 && !kfzbRewardInfo.isFinish() && layer > 1) {
                    kfzbRewardInfo.setRewardTicketList(KfzbTimeControlService.getTicketByLayerAndFinish(layer - 1, kfzbRewardInfo.isFinish(), false));
                }
                res.getList().add(kfzbRewardInfo);
            }
        }
        return res;
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
                    KfzbScheduleService.commonLog.error(String.valueOf(cap.getTacicId()) + "null");
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
    public byte[] getBattleIniInfo(final int competitorId) {
        final KfzbRTMatchInfo rtInfo = this.kfzbMatchInfoMap.get(competitorId);
        if (rtInfo == null) {
            return JsonBuilder.getJson(State.FAIL, "");
        }
        final long battleId = KfzbCommonConstants.getBattleIdByMatch(KfzbScheduleService.curSeasonId, rtInfo.getMatchId(), rtInfo.getRound());
        final KfzbBattle battle = KfzbBattleManager.getBattleById(battleId);
        if (battle == null) {
            return JsonBuilder.getJson(State.FAIL, "");
        }
        final int round = battle.getMatch().getRound();
        final long nextRoundCd = KfzbTimeControlService.getNextBattleStartCD(battle.getMatch().getLayer(), battle.getMatch().getRound());
        long nextRoundCdShowCD = 0L;
        nextRoundCdShowCD = battle.getMatch().getStartTime().getTime() + KfzbTimeControlService.getBattleInterval() * 1000L - System.currentTimeMillis();
        if (battle.state != 1) {
            final StringBuilder battleMsg = new StringBuilder();
            battle.getIniBattleMsg(battleMsg);
            KfzbScheduleService.battleReportLog.info(battleMsg.toString());
            KfzbScheduleService.interfaceLog.info(String.valueOf(competitorId) + "#\t" + battleMsg.toString());
            return JsonBuilder.getJson(State.SUCCESS, "battleIni", battleMsg.toString());
        }
        final KfzbBattleRes battleRes = battle.getRes();
        if (battleRes == null) {
            return JsonBuilder.getJson(State.FAIL, "");
        }
        KfzbScheduleService.interfaceLog.info(String.valueOf(competitorId) + "#\t" + new String(battleRes.getResJson(competitorId, nextRoundCdShowCD)));
        return JsonBuilder.getJson(State.SUCCESS, "battleRes", battleRes.getResJson(competitorId, nextRoundCdShowCD));
    }
    
    @Override
    public byte[] chooseStrategyOrTactic(final int competitorId, final int pos, final int tacticId, final int strategyId) {
        final KfzbRTMatchInfo rtInfo = this.kfzbMatchInfoMap.get(competitorId);
        if (rtInfo == null) {
            return JsonBuilder.getJson(State.FAIL, "");
        }
        final long battleId = KfzbCommonConstants.getBattleIdByMatch(KfzbScheduleService.curSeasonId, rtInfo.getMatchId(), rtInfo.getRound());
        final KfzbBattle battle = KfzbBattleManager.getBattleById(battleId);
        if (battle == null) {
            return JsonBuilder.getJson(State.FAIL, "");
        }
        if (battle.state == 1) {
            return JsonBuilder.getJson(State.FAIL, "");
        }
        final boolean needChange = KfzbMatchService.getNeedChangeFromMatch(battle.getMatch().getMatchId(), battle.getMatch().getRound());
        boolean isAtt = false;
        if ((battle.getMatch().getPlayer1Id() == competitorId && !needChange) || (battle.getMatch().getPlayer2Id() == competitorId && needChange)) {
            isAtt = true;
        }
        final int res = battle.chooseStrategyOrTactic(isAtt, pos, tacticId, strategyId);
        if (res == 2) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.CHOOSESTRATEGYORTACTIC_FAILED);
        }
        return JsonBuilder.getJson(State.SUCCESS, "suc");
    }
    
    @Override
    public Tuple<byte[], State> getPlayerMatchInfo(final int cId) {
        final long nowTime = System.currentTimeMillis();
        final KfzbRTMatchInfo rtInfo = this.kfzbMatchInfoMap.get(cId);
        if (rtInfo == null) {
            final Tuple<byte[], State> tuple = (Tuple<byte[], State>)new Tuple();
            tuple.left = LocalMessages.PLAYERNOTASSIGNED.getBytes();
            tuple.right = State.FAIL;
            return tuple;
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        final KfzbBaseInfo baseInfo = new KfzbBaseInfo();
        final KfzbState wdState = this.getWdState();
        baseInfo.setKfzbState(wdState.getGlobalState());
        baseInfo.setNextStateCD(wdState.getNextGlobalStateCD());
        doc.createElement("kfzbbaseInfo", baseInfo);
        if (baseInfo.getKfzbState() != 20 && baseInfo.getKfzbState() >= 40) {
            final KfzbBattleInfo bInfo = new KfzbBattleInfo();
            BeanUtils.copyProperties(rtInfo, bInfo);
            bInfo.setCompetitorId(cId);
            final boolean needChange = KfzbMatchService.getNeedChangeFromMatch(rtInfo.getMatchId(), rtInfo.getRound());
            bInfo.setTerrain(KfzbCommonConstants.getRanTerrain(rtInfo.getMatchId(), rtInfo.getRound(), KfzbScheduleService.curSeasonId));
            bInfo.setWinTicket(KfzbTimeControlService.getLayerTicket(rtInfo.getLayer()));
            final Date bTime = rtInfo.getRoundBattleTime();
            final int layerRound = KfzbTimeControlService.getLayerBattleNum(rtInfo.getLayer());
            final int res = rtInfo.getRes();
            final int[] battleRes = KfzbCommonConstants.getBattleWinNum(res, layerRound);
            final long cd = rtInfo.getRoundBattleTime().getTime() - System.currentTimeMillis();
            bInfo.setBattleCd(cd);
            bInfo.setBattleId(KfzbCommonConstants.getBattleIdByMatch(KfzbScheduleService.curSeasonId, rtInfo.getMatchId(), rtInfo.getRound()));
            if (rtInfo.getcId1() > 0) {
                final KfzbBattleWarrior battleWarrior = this.kfzbCacheManager.getBattleWarrior(rtInfo.getcId1());
                final KfzbPlayerInfo pInfo1 = new KfzbPlayerInfo();
                BeanUtils.copyProperties(battleWarrior, pInfo1);
                final KfzbBattleWarriorGeneral g1 = this.kfzbCacheManager.getBattleWarriorGeneral(rtInfo.getcId1());
                final CampArmyParam[] campArmys1 = g1.getCampList();
                final KfwdGInfo gInfo1 = this.getGInfoFromCampArmys(campArmys1);
                if (needChange) {
                    bInfo.setP2(pInfo1);
                    bInfo.setG2(gInfo1);
                }
                else {
                    bInfo.setP1(pInfo1);
                    bInfo.setG1(gInfo1);
                }
            }
            if (rtInfo.getcId2() > 0) {
                final KfzbBattleWarrior battleWarrior = this.kfzbCacheManager.getBattleWarrior(rtInfo.getcId2());
                final KfzbPlayerInfo pInfo2 = new KfzbPlayerInfo();
                BeanUtils.copyProperties(battleWarrior, pInfo2);
                final KfzbBattleWarriorGeneral g2 = this.kfzbCacheManager.getBattleWarriorGeneral(rtInfo.getcId2());
                final CampArmyParam[] campArmys2 = g2.getCampList();
                final KfwdGInfo gInfo2 = this.getGInfoFromCampArmys(campArmys2);
                if (needChange) {
                    bInfo.setP1(pInfo2);
                    bInfo.setG1(gInfo2);
                }
                else {
                    bInfo.setP2(pInfo2);
                    bInfo.setG2(gInfo2);
                }
            }
            if (bInfo.getP1() != null && bInfo.getP1().getCompetitorId() == cId) {
                bInfo.setP1Win(battleRes[0]);
                bInfo.setP2Win(battleRes[1]);
            }
            else {
                bInfo.setP2Win(battleRes[0]);
                bInfo.setP1Win(battleRes[1]);
            }
            final int round = rtInfo.getRound();
            doc.createElement("bInfo", bInfo);
            final int layerNum = KfzbTimeControlService.getTotalLayer() - rtInfo.getLayer() + 1;
            doc.createElement("layerNum", layerNum);
        }
        doc.endObject();
        KfzbScheduleService.interfaceLog.info(String.valueOf(cId) + "#\t" + doc.toString());
        final Tuple<byte[], State> tuple2 = (Tuple<byte[], State>)new Tuple();
        tuple2.left = doc.toByte();
        tuple2.right = State.SUCCESS;
        return tuple2;
    }
    
    @Override
    public KfzbBattleReport getPhase2MatchDetail(final KfzbPhase2MatchKey matchKey) {
        final int matchId = matchKey.getMatchId();
        final int round = matchKey.getRoundId();
        final long key = KfzbCommonConstants.getMatchKey(0, matchId, 0, round);
        final CopyOnWriteArrayList<FrameBattleReport> list = KfzbScheduleService.battleReportMap.get(key);
        final KfzbBattleReport report = new KfzbBattleReport();
        report.setMatchId(matchId);
        report.setRound(round);
        if (list == null) {
            return null;
        }
        final int getFrame = matchKey.getFrame();
        if (list.size() > 0) {
            for (final FrameBattleReport fReport : list) {
                if (fReport.getFrame() > getFrame) {
                    report.getList().add(fReport);
                    fReport.isEnd();
                }
            }
        }
        return report;
    }
    
    public static void addNewFrameReport(final FrameBattleReport report, final KfzbRuntimeMatch match) {
        final int matchId = match.getMatchId();
        final int round = match.getRound();
        final long key = KfzbCommonConstants.getMatchKey(0, matchId, 0, round);
        KfzbScheduleService.battleReportMap.putIfAbsent(key, new CopyOnWriteArrayList<FrameBattleReport>());
        final CopyOnWriteArrayList<FrameBattleReport> list = KfzbScheduleService.battleReportMap.get(key);
        list.add(report);
    }
    
    @Override
    public KfzbPlayerGroupInfo getPlayerGroupInfo(final List<Integer> cIdList) {
        if (this.scheduleSeason(KfzbScheduleService.curSeasonId)) {
            final KfzbPlayerGroupInfo gInfo = new KfzbPlayerGroupInfo();
            final int totalLayer = KfzbTimeControlService.getTotalLayer();
            for (final Integer cId : cIdList) {
                final KfzbBattleWarrior w = this.kfzbCacheManager.getBattleWarrior(cId);
                if (w != null && w.getBattlePos() > 0) {
                    final KfzbPlayerGroup pg = new KfzbPlayerGroup();
                    pg.setcId(cId);
                    pg.setPlayerId(w.getPlayerId());
                    pg.setgId((w.getBattlePos() - 1 >> totalLayer - 4) + 1);
                    gInfo.getList().add(pg);
                }
            }
            return gInfo;
        }
        return null;
    }
    
    static /* synthetic */ void access$3(final KfzbScheduleService kfzbScheduleService, final int layerRunning) {
        kfzbScheduleService.layerRunning = layerRunning;
    }
    
    class SendWinnerInfoThread extends Thread
    {
        public SendWinnerInfoThread(final String name) {
            super(name);
        }
        
        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(30000L);
                }
                catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                final Integer seasonId = KfzbScheduleService.sendWinnerInfoQueue.poll();
                try {
                    if (seasonId == null) {
                        continue;
                    }
                    final boolean res = KfzbScheduleService.this.doSendWinnerInfo(seasonId);
                    if (res) {
                        continue;
                    }
                    KfzbScheduleService.sendWinnerInfoQueue.add(seasonId);
                }
                catch (Exception e2) {
                    KfzbScheduleService.sendWinnerInfoQueue.add(seasonId);
                }
            }
        }
    }
}
