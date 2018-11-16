package com.reign.kfgz.service;

import org.springframework.beans.factory.*;
import org.springframework.stereotype.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.kf.gw.kfwd.dao.*;
import com.reign.kfgz.dao.*;
import org.springframework.context.*;
import java.util.concurrent.atomic.*;
import java.util.concurrent.locks.*;
import org.apache.commons.logging.*;
import java.io.*;
import com.reign.framework.hibernate.model.*;
import com.reign.kfgz.constants.*;
import org.apache.commons.lang.math.*;
import com.reign.kf.gw.kfwd.domain.*;
import java.util.*;
import org.springframework.transaction.annotation.*;
import org.springframework.beans.*;
import com.reign.kf.comm.entity.*;
import com.reign.kfgz.test.*;
import com.reign.kfgz.domain.*;
import com.reign.kfgz.dto.request.*;
import com.reign.kfgz.dto.response.*;

@Component
public class KfgzGatewayService implements IKfgzGatewayService, InitializingBean, ApplicationContextAware
{
    @Autowired
    IKfgzSeasonInfoDao kfgzSeasonInfoDao;
    @Autowired
    IAstdBgServerInfoDao astdBgServerInfoDao;
    @Autowired
    IGameServerInfoDao gameServerInfoDao;
    @Autowired
    IKfgzGameSeverLimitDao kfgzGameSeverLimitDao;
    @Autowired
    IKfgzBattleRewardDao kfgzBattleRewardDao;
    @Autowired
    IKfgzEndRewardDao kfgzEndRewardDao;
    @Autowired
    IKfgzGwScheduleInfoDao kfgzGwScheduleInfoDao;
    @Autowired
    IKfgzLayerInfoDao kfgzLayerInfoDao;
    @Autowired
    IKfgzRewardDao kfgzRewardDao;
    @Autowired
    IKfgzRuleDao kfgzRuleDao;
    @Autowired
    IMatchServerInfoDao matchServerInfoDao;
    @Autowired
    IKfMaxUidDao kfMaxUidDao;
    @Autowired
    IKfgzNationResultDao kfgzNationResultDao;
    @Autowired
    IKfgzPlayerRankingInfoDao kfgzPlayerRankingInfoDao;
    @Autowired
    IKfgzGameServerGroupLimitDao kfgzGameServerGroupLimitDao;
    ApplicationContext context;
    IKfgzGatewayService self;
    private static Log seasonInfoLog;
    private static Log rankInfoLog;
    public static AtomicInteger uid;
    ReentrantLock uidLock;
    public static final int LAYERMAXLEN = 10;
    public static final int GIDMAXLEN = 100;
    public static Object[][] layergIdLock;
    Map<Long, List<KfgzPlayerRankingInfoReq>> killArmyCache;
    Map<Long, List<KfgzPlayerRankingInfoReq>> soloCache;
    Map<Long, List<KfgzPlayerRankingInfoReq>> occupyCityCache;
    
    static {
        KfgzGatewayService.seasonInfoLog = LogFactory.getLog("com.xinyun.kfgzSeasonInfo");
        KfgzGatewayService.rankInfoLog = LogFactory.getLog("com.xinyun.kfgzRankInfo");
        KfgzGatewayService.uid = null;
        KfgzGatewayService.layergIdLock = new Object[10][100];
        for (int i = 0; i < 10; ++i) {
            for (int j = 0; j < 100; ++j) {
                KfgzGatewayService.layergIdLock[i][j] = new Object();
            }
        }
    }
    
    public KfgzGatewayService() {
        this.uidLock = new ReentrantLock();
        this.killArmyCache = new HashMap<Long, List<KfgzPlayerRankingInfoReq>>();
        this.soloCache = new HashMap<Long, List<KfgzPlayerRankingInfoReq>>();
        this.occupyCityCache = new HashMap<Long, List<KfgzPlayerRankingInfoReq>>();
    }
    
    @Override
    public void processKfgzSeasonInfo() {
        KfgzGatewayService.seasonInfoLog.info("processKfgzSeasonInfo");
        final KfgzSeasonInfo si = this.kfgzSeasonInfoDao.getActiveSeasonInfo();
        if (si == null || si.getSeasonId() == 0) {
            return;
        }
        if (si.getState() == 1) {
            final long doScheduleInterVal = 43200000L;
            final long nowtime = System.currentTimeMillis();
            if (si.getBeginTime() != null && si.getBeginTime().getTime() < nowtime + doScheduleInterVal) {
                this.self.scheduleNewSeason(si);
            }
        }
    }
    
    @Override
	public void setApplicationContext(final ApplicationContext arg0) throws BeansException {
        this.context = arg0;
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        this.self = (IKfgzGatewayService)this.context.getBean("kfgzGatewayService");
        final KfMaxUid kfMaxUid = (KfMaxUid)this.kfMaxUidDao.read((Serializable)1);
        if (kfMaxUid == null) {
            final KfMaxUid mId = new KfMaxUid();
            mId.setMaxId(1);
            mId.setPk(1);
            this.kfMaxUidDao.create((IModel)mId);
            KfgzGatewayService.uid = new AtomicInteger(1);
        }
        else {
            final int addNumId = 51;
            kfMaxUid.setMaxId(kfMaxUid.getMaxId() + addNumId);
            this.kfMaxUidDao.update((IModel)kfMaxUid);
            KfgzGatewayService.uid = new AtomicInteger(kfMaxUid.getMaxId());
        }
    }
    
    @Transactional
    @Override
    public void scheduleNewSeason(final KfgzSeasonInfo si) {
        KfgzGatewayService.seasonInfoLog.info("processKfgzSeasonInfo");
        final int seasonId = si.getSeasonId();
        final int gameServerLimit = si.getGameServerLimit();
        final Set<String> limitSet = new HashSet<String>();
        if (gameServerLimit == 1) {
            final List<KfgzGameSeverLimit> limitList = this.kfgzGameSeverLimitDao.getModels();
            for (final KfgzGameSeverLimit gsl : limitList) {
                limitSet.add(gsl.getServerKey());
            }
        }
        final List<GameServerInfo> activeGameServerList = this.gameServerInfoDao.getAllActiveAstdServer();
        final List<AstdBgServerInfo> list = this.astdBgServerInfoDao.getAll();
        final Map<String, AstdBgServerInfo> bgServerMap = new HashMap<String, AstdBgServerInfo>();
        for (final AstdBgServerInfo absi : list) {
            bgServerMap.put(absi.getGameServer(), absi);
        }
        final List<GameServerInfo> gsList = new ArrayList<GameServerInfo>();
        for (final GameServerInfo gs : activeGameServerList) {
            if (gameServerLimit == 1 && !limitSet.contains(gs.getGameServer())) {
                continue;
            }
            if (!bgServerMap.containsKey(gs.getGameServer())) {
                continue;
            }
            gsList.add(gs);
        }
        final Map<Integer, List<KfgzNationInfo>> layerGsMap = new HashMap<Integer, List<KfgzNationInfo>>();
        for (final GameServerInfo gs2 : gsList) {
            for (int nation = 1; nation <= 3; ++nation) {
                final KfgzNationInfo nationInfo = new KfgzNationInfo(gs2.getGameServer(), gs2.getServerName(), nation, gs2.getGzLayerIdByNation(nation));
                int layerId = nationInfo.getLayerId();
                if (layerId == 0) {
                    layerId = 1;
                }
                List<KfgzNationInfo> layerGList = layerGsMap.get(layerId);
                if (layerGList == null) {
                    layerGList = new ArrayList<KfgzNationInfo>();
                    layerGsMap.put(layerId, layerGList);
                }
                layerGList.add(nationInfo);
            }
        }
        final List<KfgzGameServerGroupLimit> gsglist = this.kfgzGameServerGroupLimitDao.getModels();
        final Map<String, Map<Integer, Integer>> gsgMap = new HashMap<String, Map<Integer, Integer>>();
        for (final KfgzGameServerGroupLimit gs3 : gsglist) {
            final int nation2 = gs3.getNation();
            final String gameServer = gs3.getGameServer();
            final int gId = gs3.getGroupId();
            Map<Integer, Integer> gsM1 = gsgMap.get(gameServer);
            if (gsM1 == null) {
                gsM1 = new HashMap<Integer, Integer>();
                gsgMap.put(gameServer, gsM1);
            }
            gsM1.put(nation2, gId);
        }
        final List<KfgzLayerInfo> layerInfo = this.kfgzLayerInfoDao.getModels();
        final int ruleId = si.getRuleId();
        final int rewardgId = si.getRewardgId();
        final KfgzRule rule = (KfgzRule)this.kfgzRuleDao.read((Serializable)ruleId);
        if (rule == null) {
            KfgzGatewayService.seasonInfoLog.info("kfgzRule " + ruleId + " is empty");
            return;
        }
        final long oneBattleMSecond = KfgzCommConstants.getBattleTimeByRuleBattleTime(rule.getBattleTime());
        final int battleNum = rule.getBattleNum();
        final long[] battleTimeDelayMSeconds = KfgzCommConstants.getBattleDelayInfo(rule.getBattleDelayInfo());
        final List<KfgzReward> rewardList = this.kfgzRewardDao.getRewardListByGId(rewardgId);
        final Map<Integer, KfgzReward> rewardMap = new HashMap<Integer, KfgzReward>();
        for (final KfgzReward kr : rewardList) {
            rewardMap.put(kr.getLayerId(), kr);
        }
        final List<MatchServerInfo> matchList = this.matchServerInfoDao.getActiveMatch();
        final int AllmatchSize = matchList.size();
        int m1 = 1;
        for (final Map.Entry<Integer, List<KfgzNationInfo>> entry : layerGsMap.entrySet()) {
            final int layer = entry.getKey();
            final List<KfgzNationInfo> gList = entry.getValue();
            final KfgzReward reward = rewardMap.get(layer);
            final int allGsNum = gList.size();
            final Map<Integer, List<KfgzNationInfo>> nationGroupMap = new HashMap<Integer, List<KfgzNationInfo>>();
            int maxGId = KfgzCommConstants.getGroupInfoByNum(allGsNum).length;
            int i = 0;
            while (i < gList.size()) {
                final KfgzNationInfo nInfo = gList.get(i);
                final String gs4 = nInfo.getGameServer();
                final int nation3 = nInfo.getNation();
                if (gsgMap.get(gs4) != null && gsgMap.get(gs4).get(nation3) != null) {
                    final int gId2 = gsgMap.get(gs4).get(nation3);
                    gList.remove(i);
                    List<KfgzNationInfo> list2 = nationGroupMap.get(gId2);
                    if (list2 == null) {
                        list2 = new ArrayList<KfgzNationInfo>();
                        nationGroupMap.put(gId2, list2);
                    }
                    list2.add(nInfo);
                    if (gId2 <= maxGId) {
                        continue;
                    }
                    maxGId = gId2;
                }
                else {
                    ++i;
                }
            }
            for (int gId3 = 1; gId3 <= maxGId; ++gId3) {
                List<KfgzNationInfo> choosenNList = nationGroupMap.get(gId3);
                if (choosenNList == null) {
                    choosenNList = new ArrayList<KfgzNationInfo>();
                    nationGroupMap.put(gId3, choosenNList);
                }
                for (int remainNum = 6 - choosenNList.size(), j = 0; j < remainNum; ++j) {
                    final int size = gList.size();
                    if (size <= 0) {
                        break;
                    }
                    final int rnum = RandomUtils.nextInt(size);
                    final KfgzNationInfo cN = gList.remove(rnum);
                    choosenNList.add(cN);
                }
            }
            for (int gId3 = 1; gId3 <= maxGId; ++gId3) {
                ++m1;
                final List<KfgzNationInfo> choosenNList = nationGroupMap.get(gId3);
                final int gNum = choosenNList.size();
                if (gNum > 0) {
                    final int[][] res = KfgzCommConstants.getBattleScheduleInfoByNumAndRound(gNum, battleNum);
                    for (int round = 1; round <= battleNum; ++round) {
                        final Date roundBattleTime = new Date(si.getFirstBattleTime().getTime() + battleTimeDelayMSeconds[round]);
                        int posId = 1;
                        for (int k = 1; k <= gNum; ++k) {
                            final int l = res[round - 1][k];
                            if (l == 0) {
                                final int gzId = KfgzCommConstants.getGzId(layer, gId3, posId, round);
                                final KfgzGwScheduleInfo schInfo = new KfgzGwScheduleInfo();
                                schInfo.setBattleDate(roundBattleTime);
                                final KfgzNationInfo nInfo2 = choosenNList.get(k - 1);
                                schInfo.setGameServer1(nInfo2.getGameServer());
                                schInfo.setServerName1(nInfo2.getServerName());
                                schInfo.setGameServer2("");
                                schInfo.setGzId(gzId);
                                final MatchServerInfo usedMatch = matchList.get(m1 % AllmatchSize);
                                schInfo.setMatchAddress(usedMatch.getMatchAdress());
                                schInfo.setMatchName(usedMatch.getMatchName());
                                schInfo.setNation1(nInfo2.getNation());
                                schInfo.setNation2(0);
                                schInfo.setRewardgId(si.getRewardgId());
                                schInfo.setRuleId(si.getRuleId());
                                schInfo.setSeasonId(seasonId);
                                schInfo.setLayerId(layer);
                                schInfo.setRound(round);
                                schInfo.setgId(gId3);
                                this.kfgzGwScheduleInfoDao.create((IModel)schInfo);
                                ++posId;
                            }
                            else if (k < l) {
                                final int gzId = KfgzCommConstants.getGzId(layer, gId3, posId, round);
                                final KfgzGwScheduleInfo schInfo = new KfgzGwScheduleInfo();
                                schInfo.setBattleDate(roundBattleTime);
                                final KfgzNationInfo nInfo2 = choosenNList.get(k - 1);
                                final KfgzNationInfo nInfo3 = choosenNList.get(l - 1);
                                schInfo.setGameServer1(nInfo2.getGameServer());
                                schInfo.setGameServer2(nInfo3.getGameServer());
                                schInfo.setServerName1(nInfo2.getServerName());
                                schInfo.setServerName2(nInfo3.getServerName());
                                schInfo.setGzId(gzId);
                                final MatchServerInfo usedMatch2 = matchList.get(m1 % AllmatchSize);
                                schInfo.setMatchAddress(usedMatch2.getMatchAdress());
                                schInfo.setMatchName(usedMatch2.getMatchName());
                                schInfo.setNation1(nInfo2.getNation());
                                schInfo.setNation2(nInfo3.getNation());
                                schInfo.setRewardgId(si.getRewardgId());
                                schInfo.setRuleId(si.getRuleId());
                                schInfo.setSeasonId(seasonId);
                                schInfo.setLayerId(layer);
                                schInfo.setRound(round);
                                schInfo.setgId(gId3);
                                this.kfgzGwScheduleInfoDao.create((IModel)schInfo);
                                ++posId;
                            }
                        }
                    }
                }
            }
        }
        si.setState(2);
        this.killArmyCache.clear();
        this.soloCache.clear();
        this.occupyCityCache.clear();
        this.kfgzSeasonInfoDao.update((IModel)si);
    }
    
    @Override
    public KfgzSeasonInfoRes handleKfgzSeasonInfo() {
        final KfgzSeasonInfo seasonInfo = this.kfgzSeasonInfoDao.getLastSeasonInfo();
        if (seasonInfo == null || seasonInfo.getState() == 1) {
            return null;
        }
        final KfgzSeasonInfoRes seasonInfoRes = new KfgzSeasonInfoRes();
        BeanUtils.copyProperties(seasonInfo, seasonInfoRes);
        final KfgzRule rule = (KfgzRule)this.kfgzRuleDao.read((Serializable)seasonInfo.getRuleId());
        seasonInfoRes.setRuleBattleTime(rule.getBattleTime());
        seasonInfoRes.setBattleDelayInfo(rule.getBattleDelayInfo());
        if (seasonInfo.getEndTime().before(new Date())) {
            seasonInfoRes.setState(3);
            if (new Date().getTime() - seasonInfo.getEndTime().getTime() > 86400000L) {
                seasonInfo.setState(4);
                this.self.saveSeasonInfo(seasonInfo);
            }
        }
        return seasonInfoRes;
    }
    
    @Override
    public Integer handleGetGameServerPlayerUid() {
        try {
            this.uidLock.lock();
            final int newId = KfgzGatewayService.uid.getAndIncrement();
            if (newId % 50 == 1) {
                this.self.saveNewUid(newId);
            }
            return newId;
        }
        finally {
            this.uidLock.unlock();
        }
    }
    
    @Transactional
    @Override
    public void saveNewUid(final int newId) {
        final KfMaxUid uid = (KfMaxUid)this.kfMaxUidDao.read((Serializable)1);
        uid.setMaxId(newId);
        this.kfMaxUidDao.update((IModel)uid);
    }
    
    @Override
    public KfgzScheduleInfoList handleGamerServerKfgzScheduleInfo(final GameServerEntity gs) {
        final String serverKey = gs.getServerKey();
        final KfgzSeasonInfo si = this.kfgzSeasonInfoDao.getActiveSeasonInfo();
        if (si == null || si.getState() == 1) {
            return null;
        }
        final int seasonId = si.getSeasonId();
        final long nowTime = new Date().getTime();
        final int ruleId = si.getRuleId();
        final int rewardgId = si.getRewardgId();
        final KfgzRule rule = (KfgzRule)this.kfgzRuleDao.read((Serializable)ruleId);
        final long oneBattleSecond = KfgzCommConstants.getBattleTimeByRuleBattleTime(rule.getBattleTime());
        final int battleNum = rule.getBattleNum();
        final long[] battleTimeDelayMSeconds = KfgzCommConstants.getBattleDelayInfo(rule.getBattleDelayInfo());
        final long firstBattleTime = si.getFirstBattleTime().getTime();
        int realRound = 0;
        for (int round = battleNum; round > 0; --round) {
            if (firstBattleTime + battleTimeDelayMSeconds[round] < nowTime) {
                realRound = round;
                break;
            }
        }
        if (realRound == 0) {
            realRound = 1;
        }
        final List<KfgzGwScheduleInfo> sInfoList = this.kfgzGwScheduleInfoDao.getInfoByGameServerSeasonIdAndRound(seasonId, realRound, serverKey);
        final KfgzScheduleInfoList resList = new KfgzScheduleInfoList();
        resList.setGameServer(serverKey);
        for (final KfgzGwScheduleInfo sInfo : sInfoList) {
            final KfgzScheduleInfoRes res = new KfgzScheduleInfoRes();
            BeanUtils.copyProperties(sInfo, res);
            resList.getList().add(res);
        }
        return resList;
    }
    
    @Override
    public KfgzRuleInfoList handleKfgzRuleInfo() {
        final KfgzRuleInfoList res = new KfgzRuleInfoList();
        final List<KfgzRule> ruleList = this.kfgzRuleDao.getModels();
        final List<KfgzLayerInfo> layerInfoList = this.kfgzLayerInfoDao.getModels();
        for (final KfgzRule rule : ruleList) {
            final KfgzRuleInfoRes newRule = new KfgzRuleInfoRes();
            BeanUtils.copyProperties(rule, newRule);
            res.getrMap().put(newRule.getRuleId(), newRule);
        }
        for (final KfgzLayerInfo layerInfo : layerInfoList) {
            final KfgzLayerInfoRes gzlayerInfo = new KfgzLayerInfoRes();
            BeanUtils.copyProperties(layerInfo, gzlayerInfo);
            res.getLayMap().put(gzlayerInfo.getLayerId(), gzlayerInfo);
        }
        return res;
    }
    
    @Override
    public KfgzRewardInfoRes handleKfgzRewardInfo() {
        final KfgzRewardInfoRes rewardInfoRes = new KfgzRewardInfoRes();
        final List<KfgzReward> rewardList = this.kfgzRewardDao.getModels();
        for (final KfgzReward r : rewardList) {
            final KfgzRewardRes r2 = new KfgzRewardRes();
            BeanUtils.copyProperties(r, r2);
            rewardInfoRes.getRewardList().add(r2);
        }
        final List<KfgzBattleReward> brList = this.kfgzBattleRewardDao.getModels();
        for (final KfgzBattleReward br : brList) {
            final KfgzBattleRewardRes bRes = new KfgzBattleRewardRes();
            BeanUtils.copyProperties(br, bRes);
            rewardInfoRes.getBattleRewardMap().put(bRes.getPk(), bRes);
        }
        final List<KfgzEndReward> erList = this.kfgzEndRewardDao.getModels();
        for (final KfgzEndReward er : erList) {
            final KfgzEndRewardRes eRes = new KfgzEndRewardRes();
            BeanUtils.copyProperties(er, eRes);
            rewardInfoRes.getEndRewardMap().put(eRes.getPk(), eRes);
        }
        return rewardInfoRes;
    }
    
    @Override
    public KfgzScheduleInfoList getSchInfoFromMatch(final MatchServerEntity ms) {
        final String matchName = ms.getMatchSvrName();
        final KfgzSeasonInfo si = this.kfgzSeasonInfoDao.getActiveSeasonInfo();
        if (si == null || si.getState() == 1) {
            return null;
        }
        final int seasonId = si.getSeasonId();
        long nowTime = new Date().getTime();
        nowTime += 60000L;
        final int ruleId = si.getRuleId();
        final int rewardgId = si.getRewardgId();
        final KfgzRule rule = (KfgzRule)this.kfgzRuleDao.read((Serializable)ruleId);
        final long oneBattleSecond = KfgzCommConstants.getBattleTimeByRuleBattleTime(rule.getBattleTime());
        final int battleNum = rule.getBattleNum();
        final long[] battleTimeDelayMSeconds = KfgzCommConstants.getBattleDelayInfo(rule.getBattleDelayInfo());
        final long firstBattleTime = si.getFirstBattleTime().getTime();
        int realRound = 0;
        for (int round = battleNum; round > 0; --round) {
            if (firstBattleTime + battleTimeDelayMSeconds[round] < nowTime) {
                realRound = round;
                break;
            }
        }
        if (realRound == 0) {
            realRound = 1;
        }
        final List<KfgzGwScheduleInfo> sInfoList = this.kfgzGwScheduleInfoDao.getInfoByMatchNameSeasonIdAndRound(seasonId, realRound, matchName);
        final KfgzScheduleInfoList resList = new KfgzScheduleInfoList();
        resList.setGameServer(matchName);
        for (final KfgzGwScheduleInfo sInfo : sInfoList) {
            final KfgzScheduleInfoRes res = new KfgzScheduleInfoRes();
            BeanUtils.copyProperties(sInfo, res);
            resList.getList().add(res);
        }
        return resList;
    }
    
    @Override
    public KfgzScheduleInfoList handleGamerServerKfgzAllScheduleInfo(final GameServerEntity gs) {
        final String serverKey = gs.getServerKey();
        final KfgzSeasonInfo si = this.kfgzSeasonInfoDao.getActiveSeasonInfo();
        if (si == null || si.getState() == 1) {
            return null;
        }
        final int seasonId = si.getSeasonId();
        final long nowTime = new Date().getTime();
        final List<KfgzGwScheduleInfo> sInfoList = this.kfgzGwScheduleInfoDao.getInfoByGameServerAndSeasonId(seasonId, serverKey);
        final KfgzScheduleInfoList resList = new KfgzScheduleInfoList();
        resList.setGameServer(serverKey);
        for (final KfgzGwScheduleInfo sInfo : sInfoList) {
            final KfgzScheduleInfoRes res = new KfgzScheduleInfoRes();
            BeanUtils.copyProperties(sInfo, res);
            resList.getList().add(res);
        }
        return resList;
    }
    
    @Override
    public KfgzBattleResultRes handleBattleResInfo(final KfgzBattleResultInfo bInfo) {
        final List<KfgzNationResultReq> nationList = bInfo.getNationRes();
        TestGzDb.printTime(10);
        final long t1 = System.currentTimeMillis();
        KfgzGatewayService.rankInfoLog.info("handleBattleResInfobegin#size=" + bInfo.getPlayerRes().size());
        this.self.saveAll(bInfo.getPlayerRes());
        final long t2 = System.currentTimeMillis();
        KfgzGatewayService.rankInfoLog.info("handleBattleResInfo#size=" + bInfo.getPlayerRes().size() + "t=" + (t2 - t1));
        TestGzDb.printTime(20);
        this.self.saveNationResList(nationList);
        final KfgzBattleResultRes res = new KfgzBattleResultRes();
        res.setState(1);
        return res;
    }
    
    @Transactional
    @Override
    public void saveAll(final List<KfgzPlayerRankingInfoReq> playerRes) {
        if (playerRes.size() == 0) {
            return;
        }
        final int seasonId = playerRes.get(0).getSeasonId();
        final int gzId = playerRes.get(0).getGzId();
        final int roundId = KfgzCommConstants.getRoundByGzId(gzId);
        final int layerId = KfgzCommConstants.getLayerByGzID(gzId);
        final int gId = KfgzCommConstants.getGIdByGzID(gzId);
        final Map<Integer, KfgzPlayerRankingInfo> layerCIdMap = this.kfgzPlayerRankingInfoDao.getInfoMapBySeasonIdLayerAndGId(seasonId, layerId, gId);
        for (final KfgzPlayerRankingInfoReq pRanking : playerRes) {
            KfgzPlayerRankingInfo prInfo = layerCIdMap.get(pRanking.getcId());
            if (prInfo == null) {
                prInfo = new KfgzPlayerRankingInfo();
                BeanUtils.copyProperties(pRanking, prInfo);
                prInfo.setRound(roundId);
                prInfo.setLayerId(layerId);
                prInfo.setgId(gId);
                prInfo.setNation(pRanking.getNation());
                prInfo.setgInfos(pRanking.getgInfos());
                this.kfgzPlayerRankingInfoDao.create((IModel)prInfo);
            }
            else {
                if (prInfo.getRound() >= KfgzCommConstants.getRoundByGzId(pRanking.getGzId())) {
                    continue;
                }
                prInfo.setRound(roundId);
                prInfo.setLayerId(layerId);
                prInfo.setgId(gId);
                prInfo.setgInfos(pRanking.getgInfos());
                prInfo.setKillArmy(prInfo.getKillArmy() + pRanking.getKillArmy());
                prInfo.setSoloNum(prInfo.getSoloNum() + pRanking.getSoloNum());
                prInfo.setOccupyCity(prInfo.getOccupyCity() + pRanking.getOccupyCity());
                this.kfgzPlayerRankingInfoDao.update((IModel)prInfo);
            }
        }
    }
    
    @Transactional
    @Override
    public void saveNationResList(final List<KfgzNationResultReq> nationList) {
        for (final KfgzNationResultReq req : nationList) {
            if (req.getNation() == 0) {
                continue;
            }
            KfgzNationResult result = this.kfgzNationResultDao.getInfoBySeasonIdAndGameSever(req.getGameServer(), req.getSeasonId(), req.getNation());
            if (result == null) {
                result = new KfgzNationResult();
                BeanUtils.copyProperties(req, result);
                final int gzId = req.getGzId();
                result.setRound(KfgzCommConstants.getRoundByGzId(gzId));
                result.setLayerId(KfgzCommConstants.getLayerByGzID(gzId));
                result.setgId(KfgzCommConstants.getGIdByGzID(gzId));
                this.kfgzNationResultDao.create((IModel)result);
            }
            else {
                if (result.getRound() >= KfgzCommConstants.getRoundByGzId(req.getGzId())) {
                    continue;
                }
                final int gzId = req.getGzId();
                result.setRound(KfgzCommConstants.getRoundByGzId(gzId));
                result.setLayerId(KfgzCommConstants.getLayerByGzID(gzId));
                result.setgId(KfgzCommConstants.getGIdByGzID(gzId));
                result.setSelfCity(result.getSelfCity() + req.getSelfCity());
                result.setOppCity(result.getOppCity() + req.getOppCity());
                this.kfgzNationResultDao.update((IModel)result);
            }
        }
    }
    
    @Transactional
    @Override
    public void savePlayerRanking(final KfgzPlayerRankingInfoReq pRanking) {
    }
    
    public static long getLayerGIDKey(final long layerId, final long gId, final long round) {
        return (layerId & 0xFL) | round << 6 | gId << 15;
    }
    
    @Override
    public KfgzAllRankRes getBattleRankInfo(final kfgzNationGzKey ngKey) {
        final long t1 = System.currentTimeMillis();
        final KfgzAllRankRes allRankRes = new KfgzAllRankRes();
        final int seasonId = ngKey.getSeasonId();
        final int gzId = ngKey.getGzId();
        final int gId = KfgzCommConstants.getGIdByGzID(gzId);
        final int layerId = KfgzCommConstants.getLayerByGzID(gzId);
        final int round = KfgzCommConstants.getRoundByGzId(gzId);
        allRankRes.setGameServer(ngKey.getGameServer());
        allRankRes.setNation(ngKey.getNation());
        allRankRes.setGzId(ngKey.getGzId());
        KfgzGatewayService.rankInfoLog.info("get#" + seasonId + "-" + layerId + "-" + "-" + gId + "-" + round);
        final KfgzSeasonInfo si = this.kfgzSeasonInfoDao.getActiveSeasonInfo();
        if (si == null || si.getState() == 1 || si.getSeasonId() != seasonId) {
            allRankRes.setState(3);
            KfgzGatewayService.rankInfoLog.info("seasonError#" + seasonId + "-" + layerId + "-" + "-" + gId + "-" + round);
            return allRankRes;
        }
        final long nowTime = new Date().getTime();
        final KfgzRule rule = (KfgzRule)this.kfgzRuleDao.read((Serializable)si.getRuleId());
        final long oneBattleSecond = KfgzCommConstants.getBattleTimeByRuleBattleTime(rule.getBattleTime());
        final int battleNum = rule.getBattleNum();
        final long[] battleTimeDelayMSeconds = KfgzCommConstants.getBattleDelayInfo(rule.getBattleDelayInfo());
        final long firstBattleTime = si.getFirstBattleTime().getTime();
        int realRound = 0;
        for (int battleRround = battleNum; battleRround > 0; --battleRround) {
            if (firstBattleTime + battleTimeDelayMSeconds[battleRround] < nowTime) {
                realRound = battleRround;
                break;
            }
        }
        if (ngKey.isIni()) {
            final List<KfgzGwScheduleInfo> list = this.kfgzGwScheduleInfoDao.getInfoByGIdRound(seasonId, layerId, gId, 1);
            for (final KfgzGwScheduleInfo sh : list) {
                final KfgzScheduleInfoRes res = new KfgzScheduleInfoRes();
                BeanUtils.copyProperties(sh, res);
                allRankRes.getShList().add(res);
            }
            allRankRes.setState(4);
            KfgzGatewayService.rankInfoLog.info("resIni#" + seasonId + "-" + layerId + "-" + "-" + gId + "-" + round);
            return allRankRes;
        }
        if (realRound == 0) {
            realRound = 1;
        }
        if (realRound > round) {
            allRankRes.setState(3);
            return allRankRes;
        }
        if (round + 1 <= battleNum) {
            final List<KfgzGwScheduleInfo> list = this.kfgzGwScheduleInfoDao.getInfoByGIdRound(seasonId, layerId, gId, round + 1);
            for (final KfgzGwScheduleInfo sh : list) {
                final KfgzScheduleInfoRes res = new KfgzScheduleInfoRes();
                BeanUtils.copyProperties(sh, res);
                allRankRes.getShList().add(res);
            }
        }
        final List<KfgzNationResult> n1result = this.kfgzNationResultDao.getOrderedInfoBySeasonIdAndLayerId(seasonId, layerId, round);
        final List<KfgzNationResult> n2result = this.kfgzNationResultDao.getOrderedInfoBySeasonIdAndGId(seasonId, layerId, gId, round);
        final int AllFinishLayernum = this.kfgzNationResultDao.getLayerRoundInfoNum(seasonId, layerId, round);
        final int layernum = this.kfgzGwScheduleInfoDao.getLayerScheduledOriginInfoGameServerSize(seasonId, layerId);
        final int gNum = this.kfgzGwScheduleInfoDao.getLayerGIdScheduledOriginInfoGameServerSize(seasonId, layerId, gId);
        if (AllFinishLayernum != layernum || n2result.size() != gNum) {
            allRankRes.setState(2);
            return allRankRes;
        }
        synchronized (KfgzGatewayService.layergIdLock[layerId % 10][gId % 100]) {
            if (this.killArmyCache.get(getLayerGIDKey(layerId, 0L, round)) == null) {
                final List<KfgzPlayerRankingInfo> layerkillRankInfo = this.kfgzPlayerRankingInfoDao.getOrderedKillArmyInfoByLayerId(seasonId, layerId);
                final List<KfgzPlayerRankingInfoReq> layerKillReqList = this.getPlayerRankingReqByRankingList(layerkillRankInfo);
                this.killArmyCache.put(getLayerGIDKey(layerId, 0L, round), layerKillReqList);
                allRankRes.setLayerKillArmyRes(layerKillReqList);
            }
            else {
                final List<KfgzPlayerRankingInfoReq> layerKillReqList2 = this.killArmyCache.get(getLayerGIDKey(layerId, 0L, round));
                allRankRes.setLayerKillArmyRes(layerKillReqList2);
            }
            if (this.killArmyCache.get(getLayerGIDKey(layerId, gId, round)) == null) {
                final List<KfgzPlayerRankingInfo> groupkillRankInfo = this.kfgzPlayerRankingInfoDao.getOrderedKillerArmyInfoByLayerIdAndGId(seasonId, layerId, gId);
                final List<KfgzPlayerRankingInfoReq> groupkillReqList = this.getPlayerRankingReqByRankingList(groupkillRankInfo);
                this.killArmyCache.put(getLayerGIDKey(layerId, gId, round), groupkillReqList);
                allRankRes.setGroupKillArmyRes(groupkillReqList);
            }
            else {
                final List<KfgzPlayerRankingInfoReq> groupkillReqList2 = this.killArmyCache.get(getLayerGIDKey(layerId, gId, round));
                allRankRes.setGroupKillArmyRes(groupkillReqList2);
            }
            if (this.soloCache.get(getLayerGIDKey(layerId, 0L, round)) == null) {
                final List<KfgzPlayerRankingInfo> layerSoloInfo = this.kfgzPlayerRankingInfoDao.getOrderedSoloInfoByLayerId(seasonId, layerId);
                final List<KfgzPlayerRankingInfoReq> layerSoloReqList = this.getPlayerRankingReqByRankingList(layerSoloInfo);
                this.soloCache.put(getLayerGIDKey(layerId, 0L, round), layerSoloReqList);
                allRankRes.setLayerSoloRes(layerSoloReqList);
            }
            else {
                final List<KfgzPlayerRankingInfoReq> layerSoloReqList2 = this.soloCache.get(getLayerGIDKey(layerId, 0L, round));
                allRankRes.setLayerSoloRes(layerSoloReqList2);
            }
            if (this.soloCache.get(getLayerGIDKey(layerId, gId, round)) == null) {
                final List<KfgzPlayerRankingInfo> groupSoloInfo = this.kfgzPlayerRankingInfoDao.getOrderedSoloInfoByLayerIdAndGId(seasonId, layerId, gId);
                final List<KfgzPlayerRankingInfoReq> groupSoloReqList = this.getPlayerRankingReqByRankingList(groupSoloInfo);
                this.soloCache.put(getLayerGIDKey(layerId, gId, round), groupSoloReqList);
                allRankRes.setGroupSoloRes(groupSoloReqList);
            }
            else {
                final List<KfgzPlayerRankingInfoReq> groupSoloReqList2 = this.soloCache.get(getLayerGIDKey(layerId, gId, round));
                allRankRes.setGroupSoloRes(groupSoloReqList2);
            }
            if (this.occupyCityCache.get(getLayerGIDKey(layerId, 0L, round)) == null) {
                final List<KfgzPlayerRankingInfo> layeroccupyCityInfo = this.kfgzPlayerRankingInfoDao.getOrderedOccupyCityoInfoByLayerId(seasonId, layerId);
                final List<KfgzPlayerRankingInfoReq> layeroccupyReqList = this.getPlayerRankingReqByRankingList(layeroccupyCityInfo);
                this.occupyCityCache.put(getLayerGIDKey(layerId, 0L, round), layeroccupyReqList);
                allRankRes.setLayerOccupyCityRes(layeroccupyReqList);
            }
            else {
                final List<KfgzPlayerRankingInfoReq> layeroccupyReqList2 = this.occupyCityCache.get(getLayerGIDKey(layerId, 0L, round));
                allRankRes.setLayerOccupyCityRes(layeroccupyReqList2);
            }
            if (this.occupyCityCache.get(getLayerGIDKey(layerId, gId, round)) == null) {
                final List<KfgzPlayerRankingInfo> groupoccupyCityInfo = this.kfgzPlayerRankingInfoDao.getOrderedOccupyCityoInfoByLayerIdAndGId(seasonId, layerId, gId);
                final List<KfgzPlayerRankingInfoReq> groupoccupyCityReqList = this.getPlayerRankingReqByRankingList(groupoccupyCityInfo);
                this.occupyCityCache.put(getLayerGIDKey(layerId, gId, round), groupoccupyCityReqList);
                allRankRes.setGroupOccupyCityRes(groupoccupyCityReqList);
            }
            else {
                final List<KfgzPlayerRankingInfoReq> groupoccupyCityReqList2 = this.occupyCityCache.get(getLayerGIDKey(layerId, gId, round));
                allRankRes.setGroupOccupyCityRes(groupoccupyCityReqList2);
            }
        }
        // monitorexit(KfgzGatewayService.layergIdLock[layerId % 10][gId % 100])
        final long t2 = System.currentTimeMillis();
        int pos = 1;
        for (final KfgzNationResult nRes : n1result) {
            final KfgzNationResultReq nationReq = new KfgzNationResultReq();
            BeanUtils.copyProperties(nRes, nationReq);
            nationReq.setPos(pos);
            final KfgzPlayerRankingInfo topPlayer = this.kfgzPlayerRankingInfoDao.getTopPlayerKillerRankingInfo(seasonId, layerId, nationReq, nRes.getGameServer(), nRes.getNation());
            nationReq.setFirstkillArmy(topPlayer.getKillArmy());
            nationReq.setFirstKillerName(topPlayer.getPlayerName());
            allRankRes.getLayerNationRes().add(nationReq);
            ++pos;
        }
        int[] upd = new int[2];
        if (round == battleNum) {
            final KfgzLayerInfo layerInfo = (KfgzLayerInfo)this.kfgzLayerInfoDao.read((Serializable)layerId);
            final KfgzReward reward = this.kfgzRewardDao.getRewardByGIdAndLayer(si.getRewardgId(), layerId);
            if (reward != null) {
                final KfgzEndReward endReward = (KfgzEndReward)this.kfgzEndRewardDao.read((Serializable)reward.getEndRewardId());
                if (endReward != null) {
                    allRankRes.setEndRewardString(endReward.getRewardInfo());
                }
            }
            final String upDownRule = layerInfo.getUpDownRule();
            upd = KfgzCommConstants.getDownUpInfoByRule(upDownRule);
            allRankRes.setUpDownInfo(upd);
            allRankRes.setLastRound(true);
            final String[] layerNameArray = new String[layerId + 2];
            final KfgzLayerInfo downLayerInfo = (KfgzLayerInfo)this.kfgzLayerInfoDao.read((Serializable)(layerId - 1));
            if (downLayerInfo != null && layerId - 1 >= 0) {
                layerNameArray[layerId - 1] = downLayerInfo.getName();
            }
            final KfgzLayerInfo upLayerInfo = (KfgzLayerInfo)this.kfgzLayerInfoDao.read((Serializable)(layerId + 1));
            if (upLayerInfo != null && layerId + 1 >= 0) {
                layerNameArray[layerId + 1] = upLayerInfo.getName();
            }
            allRankRes.setLayerNameArray(layerNameArray);
        }
        pos = 1;
        for (final KfgzNationResult nRes2 : n2result) {
            final KfgzNationResultReq nationReq2 = new KfgzNationResultReq();
            BeanUtils.copyProperties(nRes2, nationReq2);
            nationReq2.setPos(pos);
            final KfgzPlayerRankingInfo topPlayer2 = this.kfgzPlayerRankingInfoDao.getTopPlayerKillerRankingInfo(seasonId, layerId, gId, nationReq2, nRes2.getGameServer(), nRes2.getNation());
            nationReq2.setFirstkillArmy(topPlayer2.getKillArmy());
            nationReq2.setFirstKillerName(topPlayer2.getPlayerName());
            allRankRes.getGroupNationRes().add(nationReq2);
            if (round == battleNum) {
                if (pos <= upd[0]) {
                    final GameServerInfo gsInfo = this.gameServerInfoDao.getInfoByServerKey(nationReq2.getGameServer());
                    if (gsInfo != null) {
                        final int nation = nationReq2.getNation();
                        if (nation == 1) {
                            gsInfo.setGzLayerId1(layerId + 1);
                        }
                        else if (nation == 2) {
                            gsInfo.setGzLayerId2(layerId + 1);
                        }
                        else if (nation == 3) {
                            gsInfo.setGzLayerId3(layerId + 1);
                        }
                        this.gameServerInfoDao.update((IModel)gsInfo);
                    }
                }
                if (pos > upd[1]) {
                    final GameServerInfo gsInfo = this.gameServerInfoDao.getInfoByServerKey(nationReq2.getGameServer());
                    if (gsInfo != null) {
                        final int nation = nationReq2.getNation();
                        if (nation == 1) {
                            gsInfo.setGzLayerId1(layerId - 1);
                        }
                        else if (nation == 2) {
                            gsInfo.setGzLayerId2(layerId - 1);
                        }
                        else if (nation == 3) {
                            gsInfo.setGzLayerId3(layerId - 1);
                        }
                        this.gameServerInfoDao.update((IModel)gsInfo);
                    }
                }
            }
            ++pos;
        }
        final long t3 = System.currentTimeMillis();
        KfgzGatewayService.rankInfoLog.info("resall#" + seasonId + "-" + layerId + "-" + "-" + gId + "-" + round + "#" + (t3 - t2) + "-" + (t3 - t1));
        allRankRes.setState(1);
        return allRankRes;
    }
    
    private List<KfgzPlayerRankingInfoReq> getPlayerRankingReqByRankingList(final List<KfgzPlayerRankingInfo> layerkillRankInfo) {
        final List<KfgzPlayerRankingInfoReq> list = new ArrayList<KfgzPlayerRankingInfoReq>();
        for (int pos = 0; pos < layerkillRankInfo.size(); ++pos) {
            final KfgzPlayerRankingInfo playerRankingInfo = layerkillRankInfo.get(pos);
            final KfgzPlayerRankingInfoReq req = new KfgzPlayerRankingInfoReq();
            BeanUtils.copyProperties(playerRankingInfo, req);
            req.setPos(pos + 1);
            list.add(req);
        }
        return list;
    }
    
    @Transactional
    @Override
    public void saveSeasonInfo(final KfgzSeasonInfo seasonInfo) {
        this.kfgzSeasonInfoDao.update((IModel)seasonInfo);
    }
    
    public static void main(final String[] args) {
        for (int i = 0; i < 100; ++i) {
            System.out.println(RandomUtils.nextInt(100));
        }
    }
}
