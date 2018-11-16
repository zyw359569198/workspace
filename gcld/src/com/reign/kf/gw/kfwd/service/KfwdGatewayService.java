package com.reign.kf.gw.kfwd.service;

import org.springframework.beans.factory.*;
import org.springframework.stereotype.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.kfgz.service.*;
import com.reign.kfzb.service.*;
import com.reign.kf.gw.kfwd.dao.*;
import org.springframework.context.*;
import org.apache.commons.logging.*;
import java.util.concurrent.*;
import com.reign.kf.comm.entity.*;
import org.springframework.beans.*;
import com.reign.framework.hibernate.model.*;
import org.springframework.transaction.annotation.*;
import com.reign.kfwd.constants.*;
import com.reign.kf.gw.common.*;
import org.apache.commons.lang.*;
import com.reign.json.*;
import java.net.*;
import java.util.*;
import java.io.*;
import com.reign.kf.gw.kfwd.domain.*;
import com.reign.kf.comm.entity.kfwd.response.*;

@Component("kfwdGatewayService")
public class KfwdGatewayService implements IKfwdGatewayService, InitializingBean, ApplicationContextAware, Runnable
{
    @Autowired
    IKfwdSeasonInfoDao kfwdSeasonInfoDao;
    @Autowired
    IKfwdGwScheduleInfoDao kfwdGwScheduleInfoDao;
    @Autowired
    IKfwdRewardRuleDao kfwdRewardRuleDao;
    @Autowired
    IAstdBgServerInfoDao astdBgServerInfoDao;
    @Autowired
    IGameServerInfoDao gameServerInfoDao;
    @Autowired
    IMatchServerInfoDao matchServerInfoDao;
    @Autowired
    IKfwdTicketMarketDao kfwdTicketMarketDao;
    @Autowired
    IKfwdRankingRewardDao kfwdRankingRewardDao;
    @Autowired
    IKfgzGatewayService kfgzGatewayService;
    @Autowired
    IKfzbGatewayService kfzbGatewayService;
    @Autowired
    IKfwdRuleDao kfwdRuleDao;
    @Autowired
    IKfwdRankTreasureDao kfwdRankTreasureDao;
    ApplicationContext context;
    private static Log seasonInfoLog;
    private static Log interfaceLog;
    IKfwdGatewayService self;
    public static final HashMap<String, AstdBgServerInfo> activeSeverMap;
    public static final String BG_GAMESERVER = "mixName";
    public static final String BG_TYPE = "state";
    public static final String BG_SERVERINFO = "child";
    public static final String BG_SERVER_TITLE = "list";
    public static final String BG_SERVER_TIMESTAMP = "server_time";
    public static final Map<String, AstdBgServerInfo> bgServerMap;
    public static Object bglock;
    
    static {
        KfwdGatewayService.seasonInfoLog = LogFactory.getLog("com.xinyun.kfwdSeasonInfo");
        KfwdGatewayService.interfaceLog = LogFactory.getLog("com.xinyun.kfwdInterface");
        activeSeverMap = new HashMap<String, AstdBgServerInfo>();
        bgServerMap = new ConcurrentHashMap<String, AstdBgServerInfo>();
        KfwdGatewayService.bglock = new Object();
    }
    
    public KfwdGatewayService() {
        this.self = null;
    }
    
    @Override
    public KfwdTicketMarketListInfo handleGetTicketMarketInfo() {
        KfwdGatewayService.interfaceLog.debug("ms#all:GetTicketMarketInfo");
        final List<KfwdTicketMarket> tList = this.kfwdTicketMarketDao.getModels();
        final KfwdTicketMarketListInfo mListInfo = new KfwdTicketMarketListInfo();
        for (final KfwdTicketMarket tm : tList) {
            final KfwdTicketMarketInfo tInfo = new KfwdTicketMarketInfo();
            BeanUtils.copyProperties(tm, tInfo);
            mListInfo.getList().add(tInfo);
        }
        return mListInfo;
    }
    
    @Override
    public KfwdSeasonInfo handleAstdKFwdSeasonInfo(final GameServerEntity message) {
        KfwdGatewayService.interfaceLog.debug("gs#" + message.getServerKey() + ":" + "seasonInfo");
        this.self.checkAndregistServer(message);
        final com.reign.kf.gw.kfwd.domain.KfwdSeasonInfo si = this.kfwdSeasonInfoDao.getActiveSeasonInfo();
        if (si == null) {
            final KfwdSeasonInfo siForMatch = new KfwdSeasonInfo();
            siForMatch.setGlobalState(4);
            siForMatch.setSeasonId(-1);
            return siForMatch;
        }
        final KfwdSeasonInfo siForMatch = new KfwdSeasonInfo();
        BeanUtils.copyProperties((Object)si, (Object)siForMatch);
        return siForMatch;
    }
    
    @Override
    public KfwdRewardResult handleMatchKFwdRewardRuleInfo(final MatchServerEntity message) {
        KfwdGatewayService.interfaceLog.debug("ms#all:KFwdRewardRuleInfo");
        final List<KfwdRewardRuleInfo> listReward = new ArrayList<KfwdRewardRuleInfo>();
        final List<KfwdRewardRule> list = this.kfwdRewardRuleDao.getModels();
        for (final KfwdRewardRule kr : list) {
            final KfwdRewardRuleInfo kfwdRewardRuleInfo = new KfwdRewardRuleInfo();
            BeanUtils.copyProperties(kr, kfwdRewardRuleInfo);
            kfwdRewardRuleInfo.setSeasonId(0);
            listReward.add(kfwdRewardRuleInfo);
        }
        final List<KfwdRankingRewardInfo> rankingRewardList = new ArrayList<KfwdRankingRewardInfo>();
        final List<KfwdRankingReward> rrlist = this.kfwdRankingRewardDao.getOrderedRanking();
        for (final KfwdRankingReward rr : rrlist) {
            final KfwdRankingRewardInfo rri = new KfwdRankingRewardInfo();
            BeanUtils.copyProperties(rr, rri);
            rankingRewardList.add(rri);
        }
        final KfwdRewardResult mes = new KfwdRewardResult();
        mes.setSeasonId(0);
        mes.setRewardList(listReward);
        mes.setRankingRewardList(rankingRewardList);
        return mes;
    }
    
    @Override
    public KfwdMatchScheduleInfo handleMatchKFwdSeasonScheduleInfo(final MatchServerEntity message) {
        if (message == null || message.getMatchSvrName() == null) {
            return null;
        }
        KfwdGatewayService.interfaceLog.debug("ms#" + message.getMatchSvrName() + ":" + "SeasonScheduleInfo");
        final int seasonId = message.getSeasonId();
        final String matchName = message.getMatchSvrName();
        final List<KfwdGwScheduleInfo> sList = this.kfwdGwScheduleInfoDao.getScheduleInfoByMatchAndSeasonId(matchName, seasonId);
        if (sList.size() == 0) {
            return null;
        }
        final KfwdMatchScheduleInfo siForMatch = new KfwdMatchScheduleInfo();
        siForMatch.setSeasonId(seasonId);
        final List<KfwdGwScheduleInfoDto> sListcop = new ArrayList<KfwdGwScheduleInfoDto>();
        siForMatch.setsList(sListcop);
        final HashSet<Integer> sIdSet = new HashSet<Integer>();
        for (final KfwdGwScheduleInfo info : sList) {
            if (sIdSet.contains(info.getScheduleId())) {
                continue;
            }
            final KfwdGwScheduleInfoDto newInfo = new KfwdGwScheduleInfoDto();
            BeanUtils.copyProperties(info, newInfo);
            sListcop.add(newInfo);
            sIdSet.add(newInfo.getScheduleId());
        }
        return siForMatch;
    }
    
    @Override
	public void setApplicationContext(final ApplicationContext arg0) throws BeansException {
        this.context = arg0;
    }
    
    @Override
    public KfwdRewardResult handleAstdKFwdSeasonRewardInfo(final GameServerEntity message) {
        KfwdGatewayService.interfaceLog.debug("gs#all:SeasonRewardInfo");
        final com.reign.kf.gw.kfwd.domain.KfwdSeasonInfo si = this.kfwdSeasonInfoDao.getActiveSeasonInfo();
        if (si == null || si.getSeasonId() == 0) {
            return null;
        }
        final List<KfwdRewardRule> list = this.kfwdRewardRuleDao.getRewardBySeasonId(si.getSeasonId(), si.getRuleId());
        final Map<Integer, KfwdRewardRule> rewardRuleMap = new HashMap<Integer, KfwdRewardRule>();
        for (final KfwdRewardRule reward : list) {
            rewardRuleMap.put(reward.getGroupType(), reward);
        }
        final List<KfwdRewardRuleInfo> listReward = new ArrayList<KfwdRewardRuleInfo>();
        final List<KfwdGwScheduleInfo> sInfoList = this.kfwdGwScheduleInfoDao.getScheduleInfoByAstdGameServer(message.getServerKey(), si.getSeasonId());
        for (final KfwdRewardRule kr : list) {
            final KfwdRewardRuleInfo kfwdRewardRuleInfo = new KfwdRewardRuleInfo();
            BeanUtils.copyProperties(kr, kfwdRewardRuleInfo);
            kfwdRewardRuleInfo.setSeasonId(si.getSeasonId());
            listReward.add(kfwdRewardRuleInfo);
        }
        final KfwdRewardResult mes = new KfwdRewardResult();
        mes.setSeasonId(si.getSeasonId());
        mes.setRewardList(listReward);
        return mes;
    }
    
    @Override
    public KfwdScheduleInfoDto handleAstdKFwdSeasonScheduleInfo(final GameServerEntity message) {
        final com.reign.kf.gw.kfwd.domain.KfwdSeasonInfo si = this.kfwdSeasonInfoDao.getActiveSeasonInfo();
        if (si == null) {
            return null;
        }
        KfwdGatewayService.interfaceLog.debug("gs#" + message.getServerKey() + ":" + "SeasonScheduleInfo");
        if (si.getGlobalState() < 2) {
            return null;
        }
        final List<KfwdGwScheduleInfo> sInfoList = this.kfwdGwScheduleInfoDao.getScheduleInfoByAstdGameServer(message.getServerKey(), si.getSeasonId());
        if (sInfoList.size() == 0) {
            return null;
        }
        final KfwdScheduleInfoDto sInfo = new KfwdScheduleInfoDto();
        sInfo.setSeasonId(si.getSeasonId());
        final List<KfwdGwScheduleInfoDto> list = new ArrayList<KfwdGwScheduleInfoDto>();
        for (final KfwdGwScheduleInfo wdInfo : sInfoList) {
            final KfwdGwScheduleInfoDto siForMatch = new KfwdGwScheduleInfoDto();
            BeanUtils.copyProperties(wdInfo, siForMatch);
            siForMatch.setMatchAdress(wdInfo.getMatchAddress());
            list.add(siForMatch);
        }
        sInfo.setList(list);
        return sInfo;
    }
    
    @Transactional
    @Override
    public KfwdSeasonInfo handleMatchKFwdSeasonInfo(final MatchServerEntity message) {
        if (message == null || message.getMatchSvrName() == null) {
            return null;
        }
        KfwdGatewayService.interfaceLog.debug("ms#" + message.getMatchSvrName() + ":" + "KFwdSeasonInfo");
        final int state = message.getState();
        final com.reign.kf.gw.kfwd.domain.KfwdSeasonInfo si = this.kfwdSeasonInfoDao.getActiveSeasonInfo();
        if (si != null) {
            final KfwdSeasonInfo siForMatch = new KfwdSeasonInfo();
            BeanUtils.copyProperties((Object)si, (Object)siForMatch);
            return siForMatch;
        }
        final com.reign.kf.gw.kfwd.domain.KfwdSeasonInfo si2 = this.kfwdSeasonInfoDao.getActiveSeasonInfoWithOutEndTime();
        if (si2 != null && si2.getEndTime() != null && si2.getEndTime().before(new Date(System.currentTimeMillis() - 1800000L))) {
            si2.setGlobalState(4);
            this.kfwdSeasonInfoDao.update((IModel)si2);
            return null;
        }
        return null;
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        this.self = (IKfwdGatewayService)this.context.getBean("kfwdGatewayService");
        final Thread syn = new Thread(this);
        syn.start();
    }
    
    @Override
	public void run() {
        while (true) {
            try {
                this.kfzbGatewayService.processKfgzSeasonInfo();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(10000L);
            }
            catch (InterruptedException e2) {
                e2.printStackTrace();
            }
            try {
                this.kfgzGatewayService.processKfgzSeasonInfo();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(100000L);
                this.self.fethInfoFromBg();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            try {
                this.self.processKfwdSeasonInfo();
                Thread.sleep(10000L);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    @Override
    public synchronized void processKfwdSeasonInfo() {
        KfwdGatewayService.seasonInfoLog.info("synWdSeasonInfo4.19-17");
        final com.reign.kf.gw.kfwd.domain.KfwdSeasonInfo si = this.kfwdSeasonInfoDao.getActiveSeasonInfo();
        if (si == null || si.getSeasonId() == 0) {
            return;
        }
        if (si.getGlobalState() == 1) {
            final long doScheduleInterVal = 43200000L;
            final long nowtime = System.currentTimeMillis();
            if (si.getActiveTime() != null && si.getActiveTime().getTime() < nowtime + doScheduleInterVal) {
                this.self.scheduleNewSeason(si);
            }
        }
    }
    
    @Transactional
    @Override
    public synchronized void scheduleNewSeason(final com.reign.kf.gw.kfwd.domain.KfwdSeasonInfo si) {
        KfwdGatewayService.seasonInfoLog.info("scheduleNewSeason seasonId=" + si.getSeasonId());
        final int seasonId = si.getSeasonId();
        final int ruleId = si.getRuleId();
        final List<KfwdRule> ruleList = this.kfwdRuleDao.getRulesByRuleId(ruleId);
        final List<KfwdRewardRule> rewardRuleList = this.kfwdRewardRuleDao.getModels();
        final Map<Integer, KfwdRewardRule> rewardRuleMap = new HashMap<Integer, KfwdRewardRule>();
        for (final KfwdRewardRule reward : rewardRuleList) {
            rewardRuleMap.put(reward.getGroupType(), reward);
        }
        KfwdGatewayService.seasonInfoLog.info("rewardRuleMap size=" + rewardRuleMap.size());
        final Map<Integer, KfwdRule> ruleMap = new HashMap<Integer, KfwdRule>();
        int ruleNum = 0;
        for (final KfwdRule wdRule : ruleList) {
            ++ruleNum;
            ruleMap.put(ruleNum, wdRule);
        }
        if (ruleNum == 0) {
            return;
        }
        KfwdGatewayService.seasonInfoLog.info("KfwdRule size=" + ruleMap.size());
        this.kfwdGwScheduleInfoDao.deleteAllBySeasonId(si.getSeasonId());
        final int type = si.getType();
        final List<AstdBgServerInfo> list = this.astdBgServerInfoDao.getAll();
        final Map<String, AstdBgServerInfo> bgServerMap = new HashMap<String, AstdBgServerInfo>();
        for (final AstdBgServerInfo absi : list) {
            bgServerMap.put(absi.getGameServer(), absi);
        }
        KfwdGatewayService.seasonInfoLog.info("astdBgServer size=" + bgServerMap.size());
        KfwdGatewayService.seasonInfoLog.info("type=" + type);
        KfwdGatewayService.activeSeverMap.clear();
        for (final AstdBgServerInfo absi : list) {
            KfwdGatewayService.activeSeverMap.put(absi.getGameServer(), absi);
        }
        KfwdGatewayService.seasonInfoLog.info("activeSeverMap(bg+limit) size=" + KfwdGatewayService.activeSeverMap.size());
        final List<GameServerInfo> listAstdBasic = this.gameServerInfoDao.getAllActiveAstdServer();
        KfwdGatewayService.seasonInfoLog.info("activeAstdServer size=" + listAstdBasic.size());
        final List<GameServerInfo> listAstd = new ArrayList<GameServerInfo>();
        for (final GameServerInfo asi : listAstdBasic) {
            if (asi.getGameServer() != null && KfwdGatewayService.activeSeverMap.containsKey(asi.getGameServer())) {
                listAstd.add(asi);
            }
        }
        KfwdGatewayService.seasonInfoLog.info("activeAstdServerUsed size=" + listAstdBasic.size());
        final List<MatchServerInfo> matchList = this.matchServerInfoDao.getActiveMatch();
        if (matchList.size() == 0) {
            return;
        }
        final int matchSize = matchList.size();
        final Map<Integer, List<GameServerInfo>> sMap = new HashMap<Integer, List<GameServerInfo>>();
        for (int i = 1; i <= ruleNum; ++i) {
            sMap.put(i, new ArrayList<GameServerInfo>());
        }
        KfwdGatewayService.seasonInfoLog.info("activeAstdServerUsed size=" + listAstdBasic.size());
        for (final GameServerInfo asi2 : listAstd) {
            final AstdBgServerInfo bgs = bgServerMap.get(asi2.getGameServer());
            final long serverSTime = bgs.getServerStartstamp();
            for (int j = 1; j <= ruleNum; ++j) {
                final KfwdRule wdRule2 = ruleMap.get(j);
                if (wdRule2 != null) {
                    final long ruleSTime = wdRule2.getServerStartTime().getTime();
                    final long ruleETime = wdRule2.getServerEndTime().getTime();
                    if (serverSTime < ruleSTime) {
                        break;
                    }
                    if (serverSTime <= ruleETime && serverSTime >= ruleSTime) {
                        sMap.get(j).add(asi2);
                        break;
                    }
                }
            }
        }
        final Map<Integer, Integer> ruleServerCountMap = new HashMap<Integer, Integer>();
        for (int k = 1; k <= ruleNum; ++k) {
            ruleServerCountMap.put(k, sMap.get(k).size());
        }
        final Map<Integer, Integer> ruleMatchNumMap = new HashMap<Integer, Integer>();
        int usedMatchCount = 0;
        int limitMatchSizeRuleNum = 0;
        int serverNumNeedCalculate = 0;
        for (int j = 1; j <= ruleNum; ++j) {
            final KfwdRule rule = ruleMap.get(j);
            if (rule.getMatchLimit() > 0) {
                ruleMatchNumMap.put(j, rule.getMatchLimit());
                usedMatchCount += rule.getMatchLimit();
                ++limitMatchSizeRuleNum;
            }
            else {
                serverNumNeedCalculate += ruleServerCountMap.get(j);
            }
        }
        final int remainMatchCount = matchSize - usedMatchCount;
        if (remainMatchCount < 0) {
            System.out.println("no Enouph MatchCount");
            KfwdGatewayService.seasonInfoLog.info("no Enouph MatchCount");
            return;
        }
        final int unLimitRuleNum = ruleNum - limitMatchSizeRuleNum;
        if (unLimitRuleNum > 0 && remainMatchCount < unLimitRuleNum) {
            System.out.println("no Enouph MatchCount2");
            KfwdGatewayService.seasonInfoLog.info("no Enouph MatchCount2");
            return;
        }
        final Map<Integer, Integer> ruleChangeMap = new HashMap<Integer, Integer>();
        final Map<Integer, Integer> calcuMap = new HashMap<Integer, Integer>();
        int l = 1;
        for (int m = 1; m <= ruleNum; ++m) {
            if (ruleMatchNumMap.get(m) == null) {
                ruleChangeMap.put(l, m);
                calcuMap.put(l, ruleServerCountMap.get(m));
                ++l;
            }
        }
        final Map<Integer, Integer> tmapr = this.getRuleNum(calcuMap, remainMatchCount);
        for (final Map.Entry<Integer, Integer> en : tmapr.entrySet()) {
            final Integer key = en.getKey();
            final Integer value = en.getValue();
            ruleMatchNumMap.put(ruleChangeMap.get(key), value);
        }
        for (int i2 = 1; i2 <= ruleNum; ++i2) {
            KfwdGatewayService.seasonInfoLog.info("rule:" + i2 + "--" + ruleMatchNumMap.get(i2));
            System.out.println(String.valueOf(i2) + "--" + ruleMatchNumMap.get(i2));
        }
        int usedMatch = 0;
        for (int i3 = 1; i3 <= ruleNum; ++i3) {
            final List<GameServerInfo> slist = sMap.get(i3);
            final KfwdRule rule2 = ruleMap.get(i3);
            final int matchCount = ruleMatchNumMap.get(i3);
            final int ssize = slist.size();
            int snum = 0;
            for (final GameServerInfo asi3 : slist) {
                final int usedMatchPos = (++snum * matchCount - 1) / ssize;
                final MatchServerInfo matchInfo = matchList.get(usedMatchPos + usedMatch);
                final List<Integer[]> rangeList = KfwdConstantsAndMethod.parseLevelRangeList(rule2.getLevelRangeList());
                final List<Integer> rrList = KfwdConstantsAndMethod.parseRuleList(rule2.getRewardRuleGroupType());
                final List<Integer> rgList = KfwdConstantsAndMethod.parseRuleList(rule2.getRoundGodList());
                int pos = 0;
                for (final Integer[] range : rangeList) {
                    final Integer rewardId = rrList.get(pos);
                    final Integer godId = rgList.get(pos);
                    ++pos;
                    final KfwdGwScheduleInfo kgsi = new KfwdGwScheduleInfo();
                    kgsi.setGameServer(asi3.getGameServer());
                    kgsi.setRuleId(rule2.getRuleId());
                    kgsi.setLevelRange(range[0] + "-" + range[1]);
                    kgsi.setLevelRangeType(rule2.getLevelRangeType());
                    kgsi.setScheduleId(this.getGwScheduleId(matchInfo.getMatchId(), rule2.getRuleId(), pos, seasonId));
                    kgsi.setRoundGold((godId == null) ? 10 : ((int)godId));
                    kgsi.setSeasonId(si.getSeasonId());
                    kgsi.setState(-1);
                    kgsi.setMatchName(matchInfo.getMatchName());
                    kgsi.setMatchAddress(matchInfo.getMatchAdress());
                    kgsi.setRewardRule(rewardId);
                    this.kfwdGwScheduleInfoDao.create((IModel)kgsi);
                }
            }
            usedMatch += matchCount;
        }
        si.setGlobalState(2);
        this.kfwdSeasonInfoDao.update((IModel)si);
        KfwdGatewayService.seasonInfoLog.info("finishDoWdIt");
    }
    
    private int getGwScheduleId(final int matchId, final int pk, final int pos, final int seasonId) {
        final int sId = matchId % 100 + pk % 10 * 100 + pos * 10000 + seasonId % 1000 * 100000;
        return sId;
    }
    
    public Map<Integer, Integer> getRuleNum(final Map<Integer, Integer> tmap, final int maxsize) {
        final int s1 = tmap.size();
        int maxNum = 0;
        if (maxsize < s1 || s1 == 0) {
            return new HashMap<Integer, Integer>();
        }
        for (int i = 1; i <= s1; ++i) {
            final Integer id = tmap.get(i);
            maxNum += id;
        }
        final Map<Integer, Integer> tmapr = new HashMap<Integer, Integer>();
        final List<Integer> extraNumList = new LinkedList<Integer>();
        for (int j = 1; j <= s1; ++j) {
            final Integer v = tmap.get(j);
            int t1 = (int)Math.round(v * 1.0f * maxsize / maxNum + 0.5);
            if (t1 < 1) {
                t1 = 1;
            }
            tmapr.put(j, t1);
            extraNumList.add(v * 10 + j);
        }
        Collections.sort(extraNumList);
        int maxSize2 = 0;
        for (int k = 1; k <= s1; ++k) {
            final Integer v2 = tmapr.get(k);
            maxSize2 += v2;
        }
        int needMinus = maxSize2 - maxsize;
        int t1 = 0;
        while (needMinus > 0) {
            final int n1 = t1 % s1;
            final int pos = extraNumList.get(n1) % 10;
            Integer n2 = tmapr.get(pos);
            if (n2 > 1) {
                --n2;
                tmapr.put(pos, n2);
                --needMinus;
            }
            ++t1;
        }
        return tmapr;
    }
    
    @Transactional
    @Override
    public void checkAndregistServer(final GameServerEntity message) {
        final String serverKey = message.getServerKey();
        final String serverName = message.getServerName();
        GameServerInfo gs = this.gameServerInfoDao.getInfoByServerKey(serverKey);
        final Date now = new Date();
        if (gs == null) {
            gs = new GameServerInfo();
            gs.setLastSynDate(now);
            gs.setGameServer(serverKey);
            gs.setServerName(serverName);
            this.gameServerInfoDao.create((IModel)gs);
        }
        else if ((gs.getServerName() == null && message.getServerName() != null) || gs.getLastSynDate() == null || now.getTime() - gs.getLastSynDate().getTime() > 3600000L) {
            gs.setLastSynDate(now);
            gs.setServerName(serverName);
            this.gameServerInfoDao.update((IModel)gs);
        }
    }
    
    @Override
    public void fethInfoFromBg() {
        synchronized (KfwdGatewayService.bglock) {
            KfwdGatewayService.seasonInfoLog.info("fetchBgInfo");
            try {
                final String s1 = "{\"list\":[{\"child\":\"shengda_S3\",\"mixName\":\"shengda_S3\",\"server_time\":\"1336028400000\",\"state\":\"1\"},{\"child\":\"duowan_S77\",\"mixName\":\"duowan_S77\",\"server_time\":\"1335769200000\",\"state\":\"1\"},{\"child\":\"renren_S26\",\"mixName\":\"renren_S26\",\"server_time\":\"1335361217000\",\"state\":\"1\"},{\"child\":\"37wan_S129\",\"mixName\":\"37wan_S129\",\"server_time\":\"1335682800000\",\"state\":\"1\"},{\"child\":\"webxgame_S69\",\"mixName\":\"webxgame_S69\",\"server_time\":\"1335313991000\",\"state\":\"1\"},{\"child\":\"ebo_S1\",\"mixName\":\"ebo_S1\",\"server_time\":\"1335596400000\",\"state\":\"1\"},{\"child\":\"yaowan_S426\",\"mixName\":\"yaowan_S426\",\"server_time\":\"1335855600000\",\"state\":\"1\"},{\"child\":\"yaowan_S425,peiyou_S126\",\"mixName\":\"mix_S175\",\"server_time\":\"1335682800000\",\"state\":\"1\"},{\"child\":\"yaowan_S424\",\"mixName\":\"yaowan_S424\",\"server_time\":\"1335596400000\",\"state\":\"1\"},{\"child\":\"zhulang_S53\",\"mixName\":\"zhulang_S53\",\"server_time\":\"1335488400000\",\"state\":\"1\"},{\"child\":\"yaowan_S423,peiyou_S125\",\"mixName\":\"mix_S173\",\"server_time\":\"1335337200000\",\"state\":\"1\"},{\"child\":\"duowan_S76\",\"mixName\":\"duowan_S76\",\"server_time\":\"1334991600000\",\"state\":\"1\"},{\"child\":\"webxgame_S68\",\"mixName\":\"webxgame_S68\",\"server_time\":\"1334905200000\",\"state\":\"1\"},{\"child\":\"yaowan_S421,peiyou_S124\",\"mixName\":\"mix_S172\",\"server_time\":\"1334937600000\",\"state\":\"1\"},{\"child\":\"shengda_S2\",\"mixName\":\"shengda_S2\",\"server_time\":\"1334905200000\",\"state\":\"1\"},{\"child\":\"yaowan_S422\",\"mixName\":\"yaowan_S422\",\"server_time\":\"1335164400000\",\"state\":\"1\"},{\"child\":\"360_S33\",\"mixName\":\"360_S33\",\"server_time\":\"1334905200000\",\"state\":\"1\"},{\"child\":\"yaowan_S420\",\"mixName\":\"yaowan_S420\",\"server_time\":\"1334818800000\",\"state\":\"1\"},{\"child\":\"webxgame_S67\",\"mixName\":\"webxgame_S67\",\"server_time\":\"1334386800000\",\"state\":\"1\"},{\"child\":\"7k7k_S10\",\"mixName\":\"7k7k_S10\",\"server_time\":\"1334559600000\",\"state\":\"1\"},{\"child\":\"yaowan_S419,peiyou_S123\",\"mixName\":\"mix_S170\",\"server_time\":\"1334646000000\",\"state\":\"1\"},{\"child\":\"yaowan_S418\",\"mixName\":\"yaowan_S418\",\"server_time\":\"1334473200000\",\"state\":\"1\"},{\"child\":\"duowan_S75\",\"mixName\":\"duowan_S75\",\"server_time\":\"1334214000000\",\"state\":\"1\"},{\"child\":\"yaowan_S416\",\"mixName\":\"yaowan_S416\",\"server_time\":\"1334127600000\",\"state\":\"1\"},{\"child\":\"yaowan_S417,peiyou_S122\",\"mixName\":\"mix_S169\",\"server_time\":\"1334300400000\",\"state\":\"1\"},{\"child\":\"yaowan_S415,peiyou_S121\",\"mixName\":\"mix_S168\",\"server_time\":\"1333954800000\",\"state\":\"1\"},{\"child\":\"duowan_S74\",\"mixName\":\"duowan_S74\",\"server_time\":\"1333350000000\",\"state\":\"1\"},{\"child\":\"yaowan_S414\",\"mixName\":\"yaowan_S414\",\"server_time\":\"1333782000000\",\"state\":\"1\"},{\"child\":\"yaowan_S412\",\"mixName\":\"yaowan_S412\",\"server_time\":\"1333465200000\",\"state\":\"1\"},{\"child\":\"peiyou_S120,yaowan_S413\",\"mixName\":\"mix_S167\",\"server_time\":\"1333609200000\",\"state\":\"1\"},{\"child\":\"37wan_S128\",\"mixName\":\"37wan_S128\",\"server_time\":\"1332918000000\",\"state\":\"1\"},{\"child\":\"ifeng_S12\",\"mixName\":\"ifeng_S12\",\"server_time\":\"1332831600000\",\"state\":\"1\"},{\"child\":\"yaowan_S411,peiyou_S119\",\"mixName\":\"mix_S166\",\"server_time\":\"1333263600000\",\"state\":\"1\"},{\"child\":\"game2_S47\",\"mixName\":\"game2_S47\",\"server_time\":\"1332658800000\",\"state\":\"1\"},{\"child\":\"duowan_S73\",\"mixName\":\"duowan_S73\",\"server_time\":\"1332658800000\",\"state\":\"1\"},{\"child\":\"yaowan_S409,peiyou_S118\",\"mixName\":\"mix_S165\",\"server_time\":\"1300845527000\",\"state\":\"1\"},{\"child\":\"yaowan_S407,peiyou_S117\",\"mixName\":\"mix_S164\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S410\",\"mixName\":\"yaowan_S410\",\"server_time\":\"1303638784000\",\"state\":\"1\"},{\"child\":\"yaowan_S408\",\"mixName\":\"yaowan_S408\",\"server_time\":\"1303718549000\",\"state\":\"1\"},{\"child\":\"yaowan_S406\",\"mixName\":\"yaowan_S406\",\"server_time\":\"1303552849000\",\"state\":\"1\"},{\"child\":\"360_S32\",\"mixName\":\"360_S32\",\"server_time\":\"1303098320000\",\"state\":\"1\"},{\"child\":\"96pk_S14\",\"mixName\":\"96pk_S14\",\"server_time\":\"1303638975000\",\"state\":\"1\"},{\"child\":\"37wan_S127\",\"mixName\":\"37wan_S127\",\"server_time\":\"1300503221000\",\"state\":\"1\"},{\"child\":\"178_S21,uoyoo_S37,pps_S30,ifeng_S13\",\"mixName\":\"mix_S171\",\"server_time\":\"1302320047000\",\"state\":\"1\"},{\"child\":\"webxgame_S66\",\"mixName\":\"webxgame_S66\",\"server_time\":\"1303638936000\",\"state\":\"1\"},{\"child\":\"duowan_S72\",\"mixName\":\"duowan_S72\",\"server_time\":\"1302319215000\",\"state\":\"1\"},{\"child\":\"yaowan_S404\",\"mixName\":\"yaowan_S404\",\"server_time\":\"1303551547000\",\"state\":\"1\"},{\"child\":\"peiyou_S116,yaowan_S405\",\"mixName\":\"mix_S163\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S402\",\"mixName\":\"yaowan_S402\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"37wan_S126\",\"mixName\":\"37wan_S126\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"is_S18\",\"mixName\":\"is_S18\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"duowan_S71\",\"mixName\":\"duowan_S71\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"xilu_S2\",\"mixName\":\"xilu_S2\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"6711_S109\",\"mixName\":\"6711_S109\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"37wan_S125\",\"mixName\":\"37wan_S125\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"jinju_S32\",\"mixName\":\"jinju_S32\",\"server_time\":\"1300692173000\",\"state\":\"1\"},{\"child\":\"pps_S29,uoyoo_S35,ifeng_S11,baofeng_S6,178_S19,tiexue_S5\",\"mixName\":\"mix_S160\",\"server_time\":\"1301291660000\",\"state\":\"1\"},{\"child\":\"peiyou_S114,yaowan_S401\",\"mixName\":\"mix_S159\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S403,peiyou_S115\",\"mixName\":\"mix_S162\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S400\",\"mixName\":\"yaowan_S400\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"peiyou_S113,yaowan_S399\",\"mixName\":\"mix_S158\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S398\",\"mixName\":\"yaowan_S398\",\"server_time\":\"1303804696000\",\"state\":\"1\"},{\"child\":\"peiyou_S112,yaowan_S397\",\"mixName\":\"mix_S157\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S396\",\"mixName\":\"yaowan_S396\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"jinju_S31\",\"mixName\":\"jinju_S31\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S394,peiyou_S111\",\"mixName\":\"mix_S156\",\"server_time\":\"1303804736000\",\"state\":\"1\"},{\"child\":\"yaowan_S395\",\"mixName\":\"yaowan_S395\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S393\",\"mixName\":\"yaowan_S393\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S392\",\"mixName\":\"yaowan_S392\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S391,peiyou_S110\",\"mixName\":\"mix_S155\",\"server_time\":\"1303717794000\",\"state\":\"1\"},{\"child\":\"duowan_S70\",\"mixName\":\"duowan_S70\",\"server_time\":\"1303894256000\",\"state\":\"1\"},{\"child\":\"ifeng_S10\",\"mixName\":\"ifeng_S10\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"jinju_S30\",\"mixName\":\"jinju_S30\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S390\",\"mixName\":\"yaowan_S390\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"37wan_S124\",\"mixName\":\"37wan_S124\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"91_S10,webxgame_S62,666wan_S4\",\"mixName\":\"mix_S154\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S389\",\"mixName\":\"yaowan_S389\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S388,peiyou_S109\",\"mixName\":\"mix_S153\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"zhulang_S52\",\"mixName\":\"zhulang_S52\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S387\",\"mixName\":\"yaowan_S387\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"duowan_S68\",\"mixName\":\"duowan_S68\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"webxgame_S61,pps_S28,91_S9\",\"mixName\":\"mix_S143\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S386\",\"mixName\":\"yaowan_S386\",\"server_time\":\"1303797317000\",\"state\":\"1\"},{\"child\":\"peiyou_S108,yaowan_S385\",\"mixName\":\"mix_S152\",\"server_time\":\"1303786440000\",\"state\":\"1\"},{\"child\":\"yaowan_S384\",\"mixName\":\"yaowan_S384\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"ifeng_S9\",\"mixName\":\"ifeng_S9\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"sdo_S9\",\"mixName\":\"sdo_S9\",\"server_time\":\"1303634392000\",\"state\":\"1\"},{\"child\":\"jinju_S28\",\"mixName\":\"jinju_S28\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S383\",\"mixName\":\"yaowan_S383\",\"server_time\":\"1303786714000\",\"state\":\"1\"},{\"child\":\"37wan_S123\",\"mixName\":\"37wan_S123\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"duowan_S66\",\"mixName\":\"duowan_S66\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"ifeng_S8\",\"mixName\":\"ifeng_S8\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"pipi_S5,jinju_S25\",\"mixName\":\"mix_S151\",\"server_time\":\"1300517940000\",\"state\":\"1\"},{\"child\":\"37wan_S122\",\"mixName\":\"37wan_S122\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"webxgame_S60\",\"mixName\":\"webxgame_S60\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S382,peiyou_S107\",\"mixName\":\"mix_S150\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S376,peiyou_S105\",\"mixName\":\"mix_S148\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S379,peiyou_S106\",\"mixName\":\"mix_S149\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S373,peiyou_S104\",\"mixName\":\"mix_S147\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"kedou_S2\",\"mixName\":\"kedou_S2\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"51_S13\",\"mixName\":\"51_S13\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"37wan_S121\",\"mixName\":\"37wan_S121\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"rising_S5,91_S8,ifeng_S7,pps_S27,yahoo_S5\",\"mixName\":\"mix_S146\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S370,peiyou_S103\",\"mixName\":\"mix_S145\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S367,peiyou_S102\",\"mixName\":\"mix_S144\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"56uu_S50\",\"mixName\":\"56uu_S50\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"boke_S6\",\"mixName\":\"boke_S6\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"ifeng_S6\",\"mixName\":\"ifeng_S6\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"0807g_S1\",\"mixName\":\"0807g_S1\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S364,peiyou_S101\",\"mixName\":\"mix_S142\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"37wan_S120\",\"mixName\":\"37wan_S120\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"webxgame_S56\",\"mixName\":\"webxgame_S56\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S361,peiyou_S100\",\"mixName\":\"mix_S141\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"kuwo_S17\",\"mixName\":\"kuwo_S17\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S358,peiyou_S99\",\"mixName\":\"mix_S139\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"37wan_S119\",\"mixName\":\"37wan_S119\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"renren_S25\",\"mixName\":\"renren_S25\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"37wan_S118\",\"mixName\":\"37wan_S118\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"zhulang_S50\",\"mixName\":\"zhulang_S50\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"37wan_S117\",\"mixName\":\"37wan_S117\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"jinju_S23,666wan_S3,91_S7,webxgame_S54\",\"mixName\":\"mix_S138\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"duowan_S62\",\"mixName\":\"duowan_S62\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"6711_S108\",\"mixName\":\"6711_S108\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"2133_S1\",\"mixName\":\"2133_S1\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S355,peiyou_S98\",\"mixName\":\"mix_S136\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S352,peiyou_S97\",\"mixName\":\"mix_S135\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S349,peiyou_S96\",\"mixName\":\"mix_S134\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S346,peiyou_S95\",\"mixName\":\"mix_S132\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S343,peiyou_S94\",\"mixName\":\"mix_S131\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"360_S31\",\"mixName\":\"360_S31\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"ifeng_S5\",\"mixName\":\"ifeng_S5\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"leju_S47\",\"mixName\":\"leju_S47\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"178_S17\",\"mixName\":\"178_S17\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"sxwz_S22,webxgame_S52,jinju_S21,tom_S3,pps_S26,91_S5,wanyou_S1\",\"mixName\":\"mix_S133\",\"server_time\":\"1301197460000\",\"state\":\"1\"},{\"child\":\"yaowan_S340,peiyou_S93\",\"mixName\":\"mix_S129\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"666wan_S1\",\"mixName\":\"666wan_S1\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"renren_S24\",\"mixName\":\"renren_S24\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"37wan_S116\",\"mixName\":\"37wan_S116\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"cga_S10\",\"mixName\":\"cga_S10\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"duowan_S58\",\"mixName\":\"duowan_S58\",\"server_time\":\"1303827730000\",\"state\":\"1\"},{\"child\":\"zhulang_S47\",\"mixName\":\"zhulang_S47\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S337,peiyou_S92\",\"mixName\":\"mix_S128\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"shengda_S1\",\"mixName\":\"shengda_S1\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S334,peiyou_S91\",\"mixName\":\"mix_S126\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"boke_S5\",\"mixName\":\"boke_S5\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S331,peiyou_S90\",\"mixName\":\"mix_S125\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S328,peiyou_S89\",\"mixName\":\"mix_S123\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"360_S30\",\"mixName\":\"360_S30\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"51_S11\",\"mixName\":\"51_S11\",\"server_time\":\"1303892719000\",\"state\":\"1\"},{\"child\":\"91_S4,sxwz_S19,webxgame_S46,jinju_S17,yahoo_S4,rising_S4,pipi_S4\",\"mixName\":\"mix_S122\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"renren_S23\",\"mixName\":\"renren_S23\",\"server_time\":\"1303717750000\",\"state\":\"1\"},{\"child\":\"duowan_S54\",\"mixName\":\"duowan_S54\",\"server_time\":\"1303796677000\",\"state\":\"1\"},{\"child\":\"peiyou_S88,yaowan_S325\",\"mixName\":\"mix_S120\",\"server_time\":\"1303796762000\",\"state\":\"1\"},{\"child\":\"yaowan_S322,peiyou_S87\",\"mixName\":\"mix_S119\",\"server_time\":\"1303890601000\",\"state\":\"1\"},{\"child\":\"37wan_S114\",\"mixName\":\"37wan_S114\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"cga_S9\",\"mixName\":\"cga_S9\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"kaixin_S1\",\"mixName\":\"kaixin_S1\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S319,peiyou_S86\",\"mixName\":\"mix_S117\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S316,peiyou_S85\",\"mixName\":\"mix_S116\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"vsa_S1\",\"mixName\":\"vsa_S1\",\"server_time\":\"1303717669000\",\"state\":\"1\"},{\"child\":\"xilu_S1\",\"mixName\":\"xilu_S1\",\"server_time\":\"1303786771000\",\"state\":\"1\"},{\"child\":\"yaowan_S313,peiyou_S84\",\"mixName\":\"mix_S115\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S310,peiyou_S83\",\"mixName\":\"mix_S114\",\"server_time\":\"1303718275000\",\"state\":\"1\"},{\"child\":\"renren_S22\",\"mixName\":\"renren_S22\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"duowan_S50\",\"mixName\":\"duowan_S50\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"91_S1,pps_S25\",\"mixName\":\"mix_S113\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S307,peiyou_S82\",\"mixName\":\"mix_S111\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"56uu_S44\",\"mixName\":\"56uu_S44\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"cga_S8\",\"mixName\":\"cga_S8\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"360_S29\",\"mixName\":\"360_S29\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"zhulang_S41\",\"mixName\":\"zhulang_S41\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"178_S15\",\"mixName\":\"178_S15\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"37wan_S112\",\"mixName\":\"37wan_S112\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S304,peiyou_S81\",\"mixName\":\"mix_S109\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S301,peiyou_S80\",\"mixName\":\"mix_S108\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"pps_S24,ifeng_S4,sxwz_S13,29293_S4,webxgame_S37,huanlang_S30,pipi_S3,rising_S2\",\"mixName\":\"mix_S97\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S298,peiyou_S79\",\"mixName\":\"mix_S106\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"kaikai_S1\",\"mixName\":\"kaikai_S1\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"renren_S21\",\"mixName\":\"renren_S21\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"boke_S4\",\"mixName\":\"boke_S4\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"6711_S106\",\"mixName\":\"6711_S106\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S295,peiyou_S78\",\"mixName\":\"mix_S105\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"peiyou_S76,yaowan_S289\",\"mixName\":\"mix_S102\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"360_S28\",\"mixName\":\"360_S28\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"51_S8\",\"mixName\":\"51_S8\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"boke_S3\",\"mixName\":\"boke_S3\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"178_S13\",\"mixName\":\"178_S13\",\"server_time\":\"1303799002000\",\"state\":\"1\"},{\"child\":\"jj_S4\",\"mixName\":\"jj_S4\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S283,peiyou_S74\",\"mixName\":\"mix_S99\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S280,peiyou_S73\",\"mixName\":\"mix_S98\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"smggame_S5\",\"mixName\":\"smggame_S5\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"duowan_S42\",\"mixName\":\"duowan_S42\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"zhulang_S37\",\"mixName\":\"zhulang_S37\",\"server_time\":\"1303892749000\",\"state\":\"1\"},{\"child\":\"cga_S7\",\"mixName\":\"cga_S7\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S277,peiyou_S72\",\"mixName\":\"mix_S96\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"boke_S2\",\"mixName\":\"boke_S2\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S274,peiyou_S71\",\"mixName\":\"mix_S95\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"uoyoo_S29\",\"mixName\":\"uoyoo_S29\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"37wan_S108\",\"mixName\":\"37wan_S108\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S271,peiyou_S70\",\"mixName\":\"mix_S94\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S268,peiyou_S69\",\"mixName\":\"mix_S92\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"6711_S102,pps_S23\",\"mixName\":\"mix_S91\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"kuwo_S15\",\"mixName\":\"kuwo_S15\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S265,peiyou_S68\",\"mixName\":\"mix_S90\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"360_S26\",\"mixName\":\"360_S26\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"is_S12\",\"mixName\":\"is_S12\",\"server_time\":\"1303894193000\",\"state\":\"1\"},{\"child\":\"yaowan_S262,peiyou_S67\",\"mixName\":\"mix_S88\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"kuaiwan_S3\",\"mixName\":\"kuaiwan_S3\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S259,peiyou_S66\",\"mixName\":\"mix_S86\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"leju_S44\",\"mixName\":\"leju_S44\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"37wan_S104\",\"mixName\":\"37wan_S104\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"boke_S1\",\"mixName\":\"boke_S1\",\"server_time\":\"1303709176000\",\"state\":\"1\"},{\"child\":\"cga_S6\",\"mixName\":\"cga_S6\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"lewan_S3\",\"mixName\":\"lewan_S3\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S253,peiyou_S64\",\"mixName\":\"mix_S80\",\"server_time\":\"1303799071000\",\"state\":\"1\"},{\"child\":\"cga_S5\",\"mixName\":\"cga_S5\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S250,peiyou_S63\",\"mixName\":\"mix_S78\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S247,peiyou_S62\",\"mixName\":\"mix_S77\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"37wan_S100\",\"mixName\":\"37wan_S100\",\"server_time\":\"1303719117000\",\"state\":\"1\"},{\"child\":\"178_S11\",\"mixName\":\"178_S11\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"duowan_S34\",\"mixName\":\"duowan_S34\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S241,peiyou_S60\",\"mixName\":\"mix_S73\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S244,peiyou_S61\",\"mixName\":\"mix_S74\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"youcheng_S2,uoyoo_S20,jinju_S12,sxwz_S5,webxgame_S31,fengxing_S7,29293_S3,rising_S1,tom_S2\",\"mixName\":\"mix_S72\",\"server_time\":\"1300718869000\",\"state\":\"1\"},{\"child\":\"6711_S94\",\"mixName\":\"6711_S94\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"tiexue_S3\",\"mixName\":\"tiexue_S3\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"jj_S3\",\"mixName\":\"jj_S3\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"zhulang_S30\",\"mixName\":\"zhulang_S30\",\"server_time\":\"1303797425000\",\"state\":\"1\"},{\"child\":\"360_S22\",\"mixName\":\"360_S22\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"ourgame_S1\",\"mixName\":\"ourgame_S1\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S238,peiyou_S59\",\"mixName\":\"mix_S70\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"cga_S3\",\"mixName\":\"cga_S3\",\"server_time\":\"1303886766000\",\"state\":\"1\"},{\"child\":\"yaowan_S235,peiyou_S58\",\"mixName\":\"mix_S68\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"37wan_S96\",\"mixName\":\"37wan_S96\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"kedou_S1\",\"mixName\":\"kedou_S1\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"peiyou_S57,yaowan_S232\",\"mixName\":\"mix_S66\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S223,peiyou_S55\",\"mixName\":\"mix_S64\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S226,peiyou_S56\",\"mixName\":\"mix_S65\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"6711_S90\",\"mixName\":\"6711_S90\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"360_S20\",\"mixName\":\"360_S20\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"sdo_S6\",\"mixName\":\"sdo_S6\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"178_S10\",\"mixName\":\"178_S10\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S220,peiyou_S54\",\"mixName\":\"mix_S62\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"51_S5\",\"mixName\":\"51_S5\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"uusee_S7\",\"mixName\":\"uusee_S7\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"game2_S41\",\"mixName\":\"game2_S41\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"kuaiwan_S2\",\"mixName\":\"kuaiwan_S2\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S217,peiyou_S53\",\"mixName\":\"mix_S59\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"leju_S38\",\"mixName\":\"leju_S38\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"cga_S2\",\"mixName\":\"cga_S2\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"178_S9\",\"mixName\":\"178_S9\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"baidu_S33\",\"mixName\":\"baidu_S33\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"baidu_S35\",\"mixName\":\"baidu_S35\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S214,peiyou_S52\",\"mixName\":\"mix_S56\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"renren_S19\",\"mixName\":\"renren_S19\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"neotv_S2\",\"mixName\":\"neotv_S2\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"37wan_S92\",\"mixName\":\"37wan_S92\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"xunlei_S2\",\"mixName\":\"xunlei_S2\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"pipi_S1\",\"mixName\":\"pipi_S1\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S211,peiyou_S51\",\"mixName\":\"mix_S54\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"tele_S5\",\"mixName\":\"tele_S5\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"kuaiwan_S1\",\"mixName\":\"kuaiwan_S1\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"leju_S36\",\"mixName\":\"leju_S36\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"sxwz_S1,uoyoo_S12,yahoo_S3,175pt_S1,bihan_S2,29293_S1,pps_S20,bazhaoyu_S11,jinju_S9\",\"mixName\":\"mix_S55\",\"server_time\":\"1301117664000\",\"state\":\"1\"},{\"child\":\"pc_S6\",\"mixName\":\"pc_S6\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"peiyou_S50,yaowan_S208\",\"mixName\":\"mix_S51\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"webxgame_S26\",\"mixName\":\"webxgame_S26\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"pps_S19,6711_S85\",\"mixName\":\"mix_S49\",\"server_time\":\"1303700630000\",\"state\":\"1\"},{\"child\":\"xunlei_S1\",\"mixName\":\"xunlei_S1\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"kuwo_S13\",\"mixName\":\"kuwo_S13\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S204,peiyou_S48\",\"mixName\":\"mix_S48\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"360_S17\",\"mixName\":\"360_S17\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"cga_S1\",\"mixName\":\"cga_S1\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"56uu_S34\",\"mixName\":\"56uu_S34\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"baidu_S32\",\"mixName\":\"baidu_S32\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"baidu_S31\",\"mixName\":\"baidu_S31\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"juyou_S1\",\"mixName\":\"juyou_S1\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"baidu_S30\",\"mixName\":\"baidu_S30\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"baidu_S29\",\"mixName\":\"baidu_S29\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S200,peiyou_S46\",\"mixName\":\"mix_S45\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"37wan_S87\",\"mixName\":\"37wan_S87\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"fengxing_S6\",\"mixName\":\"fengxing_S6\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S196,peiyou_S44\",\"mixName\":\"mix_S43\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"peiyou_S42,yaowan_S192\",\"mixName\":\"mix_S39\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"37wan_S83\",\"mixName\":\"37wan_S83\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"51_S3\",\"mixName\":\"51_S3\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"ifeng_S1\",\"mixName\":\"ifeng_S1\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"renren_S16\",\"mixName\":\"renren_S16\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"giant_S1\",\"mixName\":\"giant_S1\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"youcheng_S1,lewan_S2,webxgame_S25,pg88_S1,jinju_S7,bazhaoyu_S10,uoyoo_S7\",\"mixName\":\"mix_S33\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"6711_S80\",\"mixName\":\"6711_S80\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S188,peiyou_S40\",\"mixName\":\"mix_S36\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"zhulang_S23\",\"mixName\":\"zhulang_S23\",\"server_time\":\"1303810337000\",\"state\":\"1\"},{\"child\":\"fengxing_S5\",\"mixName\":\"fengxing_S5\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"kugou_S9\",\"mixName\":\"kugou_S9\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"plu_S2\",\"mixName\":\"plu_S2\",\"server_time\":\"1303719073000\",\"state\":\"1\"},{\"child\":\"6711_S79,pps_S17\",\"mixName\":\"mix_S34\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S184,peiyou_S38\",\"mixName\":\"mix_S32\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"37wan_S79\",\"mixName\":\"37wan_S79\",\"server_time\":\"1303890495000\",\"state\":\"1\"},{\"child\":\"360_S15\",\"mixName\":\"360_S15\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"178_S7\",\"mixName\":\"178_S7\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"baidu_S27\",\"mixName\":\"baidu_S27\",\"server_time\":\"1303796716000\",\"state\":\"1\"},{\"child\":\"baidu_S25\",\"mixName\":\"baidu_S25\",\"server_time\":\"1303797244000\",\"state\":\"1\"},{\"child\":\"6711_S77,pps_S15\",\"mixName\":\"mix_S30\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S182,peiyou_S37\",\"mixName\":\"mix_S29\",\"server_time\":\"1303892775000\",\"state\":\"1\"},{\"child\":\"kuwo_S11\",\"mixName\":\"kuwo_S11\",\"server_time\":\"1303786793000\",\"state\":\"1\"},{\"child\":\"yaowan_S180,peiyou_S36\",\"mixName\":\"mix_S28\",\"server_time\":\"1303718735000\",\"state\":\"1\"},{\"child\":\"leju_S30\",\"mixName\":\"leju_S30\",\"server_time\":\"1303786587000\",\"state\":\"1\"},{\"child\":\"veryCD_S4\",\"mixName\":\"veryCD_S4\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"renren_S14\",\"mixName\":\"renren_S14\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S178,peiyou_S35\",\"mixName\":\"mix_S26\",\"server_time\":\"1303886724000\",\"state\":\"1\"},{\"child\":\"pps_S14\",\"mixName\":\"pps_S14\",\"server_time\":\"1303710206000\",\"state\":\"1\"},{\"child\":\"37wan_S75\",\"mixName\":\"37wan_S75\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"jj_S1\",\"mixName\":\"jj_S1\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S175,peiyou_S34\",\"mixName\":\"mix_S23\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"6711_S73\",\"mixName\":\"6711_S73\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"renren_S12\",\"mixName\":\"renren_S12\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S173,peiyou_S33\",\"mixName\":\"mix_S20\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"uoyoo_S1,lewan_S1,jinju_S3,bihan_S1\",\"mixName\":\"mix_S18\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"4399_S18\",\"mixName\":\"4399_S18\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S170,peiyou_S31,is_S5\",\"mixName\":\"mix_S17\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S171,peiyou_S32\",\"mixName\":\"mix_S19\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"kugou_S5\",\"mixName\":\"kugou_S5\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"37wan_S71\",\"mixName\":\"37wan_S71\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"benteng_S19\",\"mixName\":\"benteng_S19\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"tianya_S2\",\"mixName\":\"tianya_S2\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S169\",\"mixName\":\"yaowan_S169\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"webxgame_S21\",\"mixName\":\"webxgame_S21\",\"server_time\":\"1303700709000\",\"state\":\"1\"},{\"child\":\"baidu_S23\",\"mixName\":\"baidu_S23\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"baidu_S21\",\"mixName\":\"baidu_S21\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"duowan_S18\",\"mixName\":\"duowan_S18\",\"server_time\":\"1301464028000\",\"state\":\"1\"},{\"child\":\"yaowan_S167,peiyou_S30\",\"mixName\":\"mix_S16\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"56uu_S27\",\"mixName\":\"56uu_S27\",\"server_time\":\"1303786557000\",\"state\":\"1\"},{\"child\":\"6711_S69\",\"mixName\":\"6711_S69\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"3896_S15\",\"mixName\":\"3896_S15\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"360_S12\",\"mixName\":\"360_S12\",\"server_time\":\"1303886571000\",\"state\":\"1\"},{\"child\":\"yaowan_S165,peiyou_S29\",\"mixName\":\"mix_S14\",\"server_time\":\"1303786743000\",\"state\":\"1\"},{\"child\":\"bazhaoyu_S4\",\"mixName\":\"bazhaoyu_S4\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"sdo_S1\",\"mixName\":\"sdo_S1\",\"server_time\":\"1303710235000\",\"state\":\"1\"},{\"child\":\"kugou_S3\",\"mixName\":\"kugou_S3\",\"server_time\":\"1303886785000\",\"state\":\"1\"},{\"child\":\"leju_S25\",\"mixName\":\"leju_S25\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S163,51wan_S17,peiyou_S28\",\"mixName\":\"mix_S12\",\"server_time\":\"1302594548000\",\"state\":\"1\"},{\"child\":\"kuwo_S9\",\"mixName\":\"kuwo_S9\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"37wan_S66\",\"mixName\":\"37wan_S66\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"6711_S65\",\"mixName\":\"6711_S65\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"baidu_S19\",\"mixName\":\"baidu_S19\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S161\",\"mixName\":\"yaowan_S161\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"tiexue_S1\",\"mixName\":\"tiexue_S1\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S159,51wan_S16\",\"mixName\":\"mix_S22\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"renren_S7\",\"mixName\":\"renren_S7\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S157\",\"mixName\":\"yaowan_S157\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"uusee_S5\",\"mixName\":\"uusee_S5\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"niua_S19\",\"mixName\":\"niua_S19\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"game2_S31\",\"mixName\":\"game2_S31\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"51wan_S15,is_S4,bazhaoyu_S9,yaowan_S155\",\"mixName\":\"mix_S9\",\"server_time\":\"1303886357000\",\"state\":\"1\"},{\"child\":\"baidu_S17\",\"mixName\":\"baidu_S17\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"benteng_S17\",\"mixName\":\"benteng_S17\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"360_S10\",\"mixName\":\"360_S10\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"6711_S60\",\"mixName\":\"6711_S60\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S153,51wan_S14\",\"mixName\":\"mix_S8\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"178_S4\",\"mixName\":\"178_S4\",\"server_time\":\"1303786525000\",\"state\":\"1\"},{\"child\":\"yaowan_S151,51wan_S13\",\"mixName\":\"mix_S7\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"4399_S17\",\"mixName\":\"4399_S17\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"tele_S1\",\"mixName\":\"tele_S1\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"56uu_S20\",\"mixName\":\"56uu_S20\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"37wan_S60\",\"mixName\":\"37wan_S60\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S149,51wan_S12\",\"mixName\":\"mix_S6\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"kugou_S1\",\"mixName\":\"kugou_S1\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"56uu_S18\",\"mixName\":\"56uu_S18\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"baofeng_S3\",\"mixName\":\"baofeng_S3\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"leju_S17\",\"mixName\":\"leju_S17\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"kuwo_S7\",\"mixName\":\"kuwo_S7\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S147,51wan_S11\",\"mixName\":\"mix_S5\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"6711_S57\",\"mixName\":\"6711_S57\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"smggame_S1\",\"mixName\":\"smggame_S1\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"lequ_S14\",\"mixName\":\"lequ_S14\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"renren_S4\",\"mixName\":\"renren_S4\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"aiyou_S1\",\"mixName\":\"aiyou_S1\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"56uu_S16\",\"mixName\":\"56uu_S16\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S145,51wan_S10\",\"mixName\":\"mix_S3\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S143\",\"mixName\":\"yaowan_S143\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"feixue_S1\",\"mixName\":\"feixue_S1\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"51_S1\",\"mixName\":\"51_S1\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"leju_S14\",\"mixName\":\"leju_S14\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"360_S8\",\"mixName\":\"360_S8\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"fengxing_S3\",\"mixName\":\"fengxing_S3\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S139,51wan_S9\",\"mixName\":\"mix_Smix2\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"baidu_S15\",\"mixName\":\"baidu_S15\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"baidu_S13\",\"mixName\":\"baidu_S13\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"niua_S15\",\"mixName\":\"niua_S15\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"4399_S16\",\"mixName\":\"4399_S16\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"37wan_S56\",\"mixName\":\"37wan_S56\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S137\",\"mixName\":\"yaowan_S137\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"mttang_S1\",\"mixName\":\"mttang_S1\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"zhulang_S17\",\"mixName\":\"zhulang_S17\",\"server_time\":\"1303803426000\",\"state\":\"1\"},{\"child\":\"benteng_S15\",\"mixName\":\"benteng_S15\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"baofeng_S2\",\"mixName\":\"baofeng_S2\",\"server_time\":\"1303804675000\",\"state\":\"1\"},{\"child\":\"56uu_S14\",\"mixName\":\"56uu_S14\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"webxgame_S17\",\"mixName\":\"webxgame_S17\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"6711_S54\",\"mixName\":\"6711_S54\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"peiyou_S24\",\"mixName\":\"peiyou_S24\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S135\",\"mixName\":\"yaowan_S135\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"tom_S1\",\"mixName\":\"TOM_S1\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"6711_S52\",\"mixName\":\"6711_S52\",\"server_time\":\"1303804717000\",\"state\":\"1\"},{\"child\":\"yaowan_S133\",\"mixName\":\"yaowan_S133\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"kuwo_S5\",\"mixName\":\"kuwo_S5\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"leju_S9\",\"mixName\":\"leju_S9\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"baidu_S11\",\"mixName\":\"baidu_S11\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S131\",\"mixName\":\"yaowan_S131\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"leju_S7\",\"mixName\":\"leju_S7\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"pptv_S3\",\"mixName\":\"pptv_S3\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S127\",\"mixName\":\"yaowan_S127\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S125\",\"mixName\":\"yaowan_S125\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S123\",\"mixName\":\"yaowan_S123\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"leju_S4\",\"mixName\":\"leju_S4\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"6711_S49\",\"mixName\":\"6711_S49\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S121\",\"mixName\":\"yaowan_S121\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"game2_S24\",\"mixName\":\"game2_S24\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"360_S6\",\"mixName\":\"360_S6\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S119\",\"mixName\":\"yaowan_S119\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"96pk_S10\",\"mixName\":\"96pk_S10\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"6711_S47\",\"mixName\":\"6711_S47\",\"server_time\":\"1303890552000\",\"state\":\"1\"},{\"child\":\"6711_S45\",\"mixName\":\"6711_S45\",\"server_time\":\"1303796656000\",\"state\":\"1\"},{\"child\":\"yaowan_S115\",\"mixName\":\"yaowan_S115\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"fengxing_S1\",\"mixName\":\"fengxing_S1\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"3896_S11\",\"mixName\":\"3896_S11\",\"server_time\":\"1303886542000\",\"state\":\"1\"},{\"child\":\"yaowan_S113\",\"mixName\":\"yaowan_S113\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S111\",\"mixName\":\"yaowan_S111\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S109\",\"mixName\":\"yaowan_S109\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"6711_S42\",\"mixName\":\"6711_S42\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"benteng_S13\",\"mixName\":\"benteng_S13\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S107\",\"mixName\":\"yaowan_S107\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"niua_S9\",\"mixName\":\"niua_S9\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"baidu_S6\",\"mixName\":\"baidu_S6\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"baidu_S9\",\"mixName\":\"baidu_S9\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"webxgame_S15\",\"mixName\":\"webxgame_S15\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S105\",\"mixName\":\"yaowan_S105\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"huanlang_S22\",\"mixName\":\"huanlang_S22\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"baidu_S5\",\"mixName\":\"baidu_S5\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"game2_S19\",\"mixName\":\"game2_S19\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"56uu_S6\",\"mixName\":\"56uu_S6\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S103\",\"mixName\":\"yaowan_S103\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S101\",\"mixName\":\"yaowan_S101\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S129\",\"mixName\":\"yaowan_S129\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"3722_S1\",\"mixName\":\"3722_S1\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"youwo_S5\",\"mixName\":\"youwo_S5\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S20\",\"mixName\":\"yaowan_S20\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"96pk_S1\",\"mixName\":\"96pk_S1\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"4399_S7\",\"mixName\":\"4399_S7\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"37wan_S14\",\"mixName\":\"37wan_S14\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S63\",\"mixName\":\"yaowan_S63\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"3737_S1\",\"mixName\":\"3737_S1\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"3896_S9\",\"mixName\":\"3896_S9\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"webxgame_S1,yaowan_S35\",\"mixName\":\"mix_S1\",\"server_time\":\"1300718408000\",\"state\":\"1\"},{\"child\":\"1p1k_S1\",\"mixName\":\"1p1k_S1\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"youwo_S8\",\"mixName\":\"youwo_S8\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S22\",\"mixName\":\"yaowan_S22\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"6711_S8\",\"mixName\":\"6711_S8\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"6711_S1\",\"mixName\":\"6711_S1\",\"server_time\":\"1300500835000\",\"state\":\"1\"},{\"child\":\"plu_S1\",\"mixName\":\"plu_S1\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"51wan_S1\",\"mixName\":\"51wan_S1\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"37wan_S2\",\"mixName\":\"37wan_S2\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"37wan_S1\",\"mixName\":\"37wan_S1\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"4399_S1\",\"mixName\":\"4399_S1\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"zhulang_S1\",\"mixName\":\"zhulang_S1\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S24\",\"mixName\":\"yaowan_S24\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"huanlang_S1\",\"mixName\":\"huanlang_S1\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"pc_S1\",\"mixName\":\"pc_S1\",\"server_time\":\"1303964497000\",\"state\":\"1\"},{\"child\":\"benteng_S3\",\"mixName\":\"benteng_S3\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S67\",\"mixName\":\"yaowan_S67\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"178_S1\",\"mixName\":\"178_S1\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S41\",\"mixName\":\"yaowan_S41\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"game2_S1\",\"mixName\":\"game2_S1\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"4399_S10\",\"mixName\":\"4399_S10\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"kuwan_S1\",\"mixName\":\"kuwan_S1\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S26\",\"mixName\":\"yaowan_S26\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"huanlang_S14\",\"mixName\":\"huanlang_S14\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"benteng_S5\",\"mixName\":\"benteng_S5\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"pps_S1\",\"mixName\":\"pps_S1\",\"server_time\":\"1303190975000\",\"state\":\"1\"},{\"child\":\"6711_S36\",\"mixName\":\"6711_S36\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"zhulang_S8\",\"mixName\":\"zhulang_S8\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"leju_S1\",\"mixName\":\"leju_S1\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S117\",\"mixName\":\"yaowan_S117\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"game2_S11\",\"mixName\":\"game2_S11\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S47\",\"mixName\":\"yaowan_S47\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"kuwan_S6\",\"mixName\":\"kuwan_S6\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S28\",\"mixName\":\"yaowan_S28\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"huanlang_S18\",\"mixName\":\"huanlang_S18\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"xuxupai_S1\",\"mixName\":\"xuxupai_S1\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"baofeng_S1\",\"mixName\":\"baofeng_S1\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"peiyou_S1\",\"mixName\":\"peiyou_S1\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"37wan_S44\",\"mixName\":\"37wan_S44\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"lequ_S1\",\"mixName\":\"lequ_S1\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S141\",\"mixName\":\"yaowan_S141\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"6711_S11\",\"mixName\":\"6711_S11\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S5\",\"mixName\":\"yaowan_S5\",\"server_time\":\"1303710222000\",\"state\":\"1\"},{\"child\":\"kuwo_S1\",\"mixName\":\"kuwo_S1\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S3\",\"mixName\":\"yaowan_S3\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S1\",\"mixName\":\"yaowan_S1\",\"server_time\":\"1303700607000\",\"state\":\"1\"},{\"child\":\"7k7k_S1\",\"mixName\":\"7k7k_S1\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"pptv_S1\",\"mixName\":\"pptv_S1\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"baidu_S1\",\"mixName\":\"baidu_S1\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"6711_S39\",\"mixName\":\"6711_S39\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"37wan_S50\",\"mixName\":\"37wan_S50\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S12\",\"mixName\":\"yaowan_S12\",\"server_time\":\"1301208658000\",\"state\":\"1\"},{\"child\":\"lequ_S11\",\"mixName\":\"lequ_S11\",\"server_time\":\"1303717728000\",\"state\":\"1\"},{\"child\":\"neotv_S1\",\"mixName\":\"neotv_S1\",\"server_time\":\"1303894233000\",\"state\":\"1\"},{\"child\":\"6711_S6\",\"mixName\":\"6711_S6\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"3896_S1\",\"mixName\":\"3896_S1\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"6711_S15\",\"mixName\":\"6711_S15\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S51\",\"mixName\":\"yaowan_S51\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S30\",\"mixName\":\"yaowan_S30\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"huanlang_S6\",\"mixName\":\"huanlang_S6\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"6711_S4\",\"mixName\":\"6711_S4\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"6711_S18\",\"mixName\":\"6711_S18\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S55\",\"mixName\":\"yaowan_S55\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S34\",\"mixName\":\"yaowan_S34\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"bazhaoyu_S1\",\"mixName\":\"bazhaoyu_S1\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"91555_S1\",\"mixName\":\"91555_S1\",\"server_time\":\"1303700663000\",\"state\":\"1\"},{\"child\":\"veryCD_S1\",\"mixName\":\"veryCD_S1\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"renren_S1\",\"mixName\":\"renren_S1\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"360_S4\",\"mixName\":\"360_S4\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"lequ_S3\",\"mixName\":\"lequ_S3\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S16\",\"mixName\":\"yaowan_S16\",\"server_time\":\"1302321360000\",\"state\":\"1\"},{\"child\":\"niua_S1\",\"mixName\":\"niua_S1\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"3896_S13\",\"mixName\":\"3896_S13\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"91wan_S1\",\"mixName\":\"91wan_S1\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"6711_S2\",\"mixName\":\"6711_S2\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"4399_S2\",\"mixName\":\"4399_S2\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"360_S2\",\"mixName\":\"360_S2\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S57\",\"mixName\":\"yaowan_S57\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"peiyou_S3\",\"mixName\":\"peiyou_S3\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"lequ_S5\",\"mixName\":\"lequ_S5\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yesjd_S1\",\"mixName\":\"yesjd_S1\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"56uu_S9\",\"mixName\":\"56uu_S9\",\"server_time\":\"1303803411000\",\"state\":\"1\"},{\"child\":\"peiyou_S5\",\"mixName\":\"peiyou_S5\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"91wan_S5\",\"mixName\":\"91wan_S5\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"4399_S4\",\"mixName\":\"4399_S4\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S59\",\"mixName\":\"yaowan_S59\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"tianya_S1\",\"mixName\":\"tianya_S1\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"6711_S43\",\"mixName\":\"6711_S43\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"56uu_S1\",\"mixName\":\"56uu_S1\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"lequ_S8\",\"mixName\":\"lequ_S8\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"youwo_S1\",\"mixName\":\"youwo_S1\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"game2_S8\",\"mixName\":\"game2_S8\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"webxgame_S9\",\"mixName\":\"webxgame_S9\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"duowan_S1\",\"mixName\":\"duowan_S1\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"6711_S7\",\"mixName\":\"6711_S7\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"5ding_S1\",\"mixName\":\"5ding_S1\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"3896_S5\",\"mixName\":\"3896_S5\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"91wan_S9\",\"mixName\":\"91wan_S9\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"4399_S6\",\"mixName\":\"4399_S6\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"zhulang_S5\",\"mixName\":\"zhulang_S5\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S61\",\"mixName\":\"yaowan_S61\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"kuwo_S3\",\"mixName\":\"kuwo_S3\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"peiyou_S17\",\"mixName\":\"peiyou_S17\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S97\",\"mixName\":\"yaowan_S97\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"6711_S40\",\"mixName\":\"6711_S40\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S99\",\"mixName\":\"yaowan_S99\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S95\",\"mixName\":\"yaowan_S95\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"6711_S38\",\"mixName\":\"6711_S38\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"uusee_S1\",\"mixName\":\"uusee_S1\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S93\",\"mixName\":\"yaowan_S93\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"baidu_S3\",\"mixName\":\"baidu_S3\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S91\",\"mixName\":\"yaowan_S91\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S89\",\"mixName\":\"yaowan_S89\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"is_S1,leju_S16\",\"mixName\":\"mix_S4\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"37wan_S34\",\"mixName\":\"37wan_S34\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S87\",\"mixName\":\"yaowan_S87\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"6711_S34\",\"mixName\":\"6711_S34\",\"server_time\":\"1303803345000\",\"state\":\"1\"},{\"child\":\"yaowan_S85\",\"mixName\":\"yaowan_S85\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S83\",\"mixName\":\"yaowan_S83\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"game5_S1\",\"mixName\":\"game5_S1\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"benteng_S9\",\"mixName\":\"benteng_S9\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"zhulang_S13\",\"mixName\":\"zhulang_S13\",\"server_time\":\"1303799131000\",\"state\":\"1\"},{\"child\":\"yaowan_S81\",\"mixName\":\"yaowan_S81\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"6711_S31\",\"mixName\":\"6711_S31\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"6711_S30\",\"mixName\":\"6711_S30\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S79\",\"mixName\":\"yaowan_S79\",\"server_time\":\"1303803372000\",\"state\":\"1\"},{\"child\":\"yaowan_S77\",\"mixName\":\"yaowan_S77\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"4399_S12\",\"mixName\":\"4399_S12\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"360_S1\",\"mixName\":\"360_S1\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"peiyou_S13\",\"mixName\":\"peiyou_S13\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S75\",\"mixName\":\"yaowan_S75\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"6711_S28\",\"mixName\":\"6711_S28\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S73\",\"mixName\":\"yaowan_S73\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S71\",\"mixName\":\"yaowan_S71\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S69\",\"mixName\":\"yaowan_S69\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"37wan_S26\",\"mixName\":\"37wan_S26\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"6711_S25\",\"mixName\":\"6711_S25\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"96pk_S4\",\"mixName\":\"96pk_S4\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"3896_S7\",\"mixName\":\"3896_S7\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"peiyou_S9\",\"mixName\":\"peiyou_S9\",\"server_time\":\"0\",\"state\":\"1\"},{\"child\":\"yaowan_S65\",\"mixName\":\"yaowan_S65\",\"server_time\":\"0\",\"state\":\"1\"}]}";
                String echo = null;
                try {
                    String bgUrl = Configuration.getProperty("background.url");
                    KfwdGatewayService.seasonInfoLog.info("bgUrl=" + bgUrl);
                    if (bgUrl == null) {
                        bgUrl = "http://bs.gc.aoshitang.com";
                    }
                    echo = sendRequest(String.valueOf(bgUrl) + "/outsideRequest!queryServer.action", new HashMap<String, Object>());
                    KfwdGatewayService.seasonInfoLog.info("getBgInfo=" + echo);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                if (StringUtils.isBlank(echo)) {
                    // monitorexit(KfwdGatewayService.bglock)
                    return;
                }
                this.astdBgServerInfoDao.deleteAll();
                final JSONObject jb = new JSONObject(echo);
                final JSONArray ja = jb.getJSONArray("list");
                KfwdGatewayService.bgServerMap.clear();
                for (int i = 0; i < ja.length(); ++i) {
                    final JSONObject jo = (JSONObject)ja.get(i);
                    final String sState = (String)jo.get("state");
                    final int type = Integer.parseInt(sState);
                    if (type == 1) {
                        final String gameServer = (String)jo.get("mixName");
                        final String serverInfo = (String)jo.get("child");
                        final String timeStamp = (String)jo.get("server_time");
                        final AstdBgServerInfo as = new AstdBgServerInfo();
                        as.setGameServer(gameServer);
                        as.setType(type);
                        as.setServerStartstamp(new Long(timeStamp));
                        as.setServerInfo(serverInfo);
                        this.astdBgServerInfoDao.create((IModel)as);
                        KfwdGatewayService.bgServerMap.put(gameServer, as);
                    }
                }
            }
            catch (JSONException e2) {
                e2.printStackTrace();
            }
        }
        // monitorexit(KfwdGatewayService.bglock)
    }
    
    public static String sendRequest(final String requestURL, final Map<String, Object> paramMap) {
        URL url = null;
        HttpURLConnection connection = null;
        BufferedInputStream bis = null;
        try {
            url = new URL(requestURL);
            connection = (HttpURLConnection)url.openConnection();
            connection.setDoOutput(true);
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            final OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
            final String param = getParam(paramMap);
            out.write(param);
            out.flush();
            out.close();
            final int code = connection.getResponseCode();
            if (code == 200) {
                bis = new BufferedInputStream(connection.getInputStream());
                int length = -1;
                final byte[] buff = new byte[1024];
                final StringBuilder builder = new StringBuilder("");
                while ((length = bis.read(buff)) != -1) {
                    builder.append(new String(buff, 0, length));
                }
                return builder.toString();
            }
            return "";
        }
        catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        catch (IOException e2) {
            throw new RuntimeException(e2);
        }
        catch (Exception e3) {
            throw new RuntimeException(e3);
        }
        finally {
            if (bis != null) {
                try {
                    bis.close();
                }
                catch (IOException ex) {}
            }
        }
    }
    
    private static String getParam(final Map<String, Object> paramMap) throws UnsupportedEncodingException {
        final StringBuilder builder = new StringBuilder();
        final Set<Map.Entry<String, Object>> entrySet = paramMap.entrySet();
        int index = 0;
        for (final Map.Entry<String, Object> entry : entrySet) {
            if (index != 0) {
                builder.append("&");
            }
            builder.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            ++index;
        }
        return builder.toString();
    }
    
    @Override
    public KfwdRankTreasureList handleGetTreasureRewardInfo(final GameServerEntity gs) {
        final KfwdRankTreasureList list = new KfwdRankTreasureList();
        final List<KfwdRankTreasure> rList = this.kfwdRankTreasureDao.getModels();
        for (final KfwdRankTreasure tr : rList) {
            final KfwdRankTreasureInfo info = new KfwdRankTreasureInfo();
            BeanUtils.copyProperties(tr, info);
            list.getList().add(info);
        }
        return list;
    }
}
