package com.reign.gcld.kfzb.service;

import org.springframework.beans.factory.*;
import org.springframework.stereotype.*;
import org.apache.commons.logging.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.context.*;
import com.reign.gcld.kfgz.service.*;
import com.reign.gcld.sdata.cache.*;
import java.util.concurrent.*;
import com.reign.kf.comm.transfer.oio.*;
import java.util.concurrent.locks.*;
import com.reign.gcld.log.*;
import org.springframework.beans.*;
import com.reign.gcld.kfwd.service.*;
import com.reign.gcld.kfzb.transferconfig.*;
import com.reign.kf.comm.transfer.*;
import java.text.*;
import com.reign.gcld.chat.common.*;
import com.reign.gcld.battle.common.*;
import com.reign.kf.comm.protocol.*;
import com.reign.gcld.player.dto.*;
import com.reign.gcld.player.domain.*;
import com.reign.gcld.kfzb.util.*;
import com.reign.gcld.system.service.*;
import com.reign.gcld.kfzb.domain.*;
import com.reign.kfzb.dto.request.*;
import com.reign.gcld.common.*;
import com.reign.framework.json.*;
import com.reign.gcld.kfgz.domain.*;
import org.springframework.transaction.annotation.*;
import com.reign.gcld.general.domain.*;
import com.reign.kfzb.dto.response.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.kfzb.constants.*;
import java.util.*;
import com.reign.kf.comm.entity.kfwd.response.*;
import com.reign.util.*;

@Component("kfzbMatchService")
public class KfzbMatchService implements IKfzbMatchService, InitializingBean, ApplicationContextAware
{
    private static Log kfzbLogger;
    @Autowired
    private IDataGetter dataGetter;
    private IKfzbMatchService self;
    private ApplicationContext context;
    @Autowired
    private IKfgzSeasonService kfgzSeasonService;
    @Autowired
    private IKfzbSeasonService kfzbSeasonService;
    @Autowired
    private IKfwdMatchService kfwdMatchService;
    @Autowired
    private GeneralTreasureCache generalTreasureCache;
    @Autowired
    private IKfzbFeastService kfzbFeastService;
    private boolean cacheSuccHandled;
    private KfzbState kfzbState;
    private String address;
    private int port;
    private Map<Integer, Integer> playerSaiquIdMap;
    private Set<Integer> phase1AndPhase2KickOutCIdSet;
    private Set<Integer> phase2InbattleCIdSet;
    private Map<Integer, KfzbBattleInfo> kfzbBattleInfoMap;
    private Map<Integer, KfzbBattleReport> kfzbBattleReportMap;
    private List<KfzbRTSupport> supportList;
    private Set<String> autoSavedMatchIdSet;
    private ConcurrentHashMap<Integer, Integer> playerViewMap;
    private ConcurrentHashMap<Integer, Set<Integer>> matchViewPlayersMap;
    private static KfConnection connectionZbMatchState;
    private static MatchStateThread stateThread;
    private static MatchReportThread reportThread;
    private static final long stateSleepTime = 2000L;
    private static final long reportSleepTime = 200L;
    public static boolean alive1;
    public static boolean alive2;
    static ReentrantReadWriteLock getSupLock;
    static ReentrantReadWriteLock getTicketLock;
    public static long day1NoticeTime;
    public static long day2NoticeTime;
    public static long day3NoticeTime;
    public static boolean day1BattleBeginNotice;
    public static boolean day2BattleBeginNotice;
    public static boolean day3BattleBeginNotice;
    public static boolean battleEndNotice;
    public static long TIME_15MINMS;
    public static SimpleDateFormat sdf;
    
    static {
        KfzbMatchService.kfzbLogger = new KfzbLogger();
        KfzbMatchService.stateThread = null;
        KfzbMatchService.reportThread = null;
        KfzbMatchService.alive1 = false;
        KfzbMatchService.alive2 = false;
        KfzbMatchService.getSupLock = new ReentrantReadWriteLock();
        KfzbMatchService.getTicketLock = new ReentrantReadWriteLock();
        KfzbMatchService.day1NoticeTime = 0L;
        KfzbMatchService.day2NoticeTime = 0L;
        KfzbMatchService.day3NoticeTime = 0L;
        KfzbMatchService.day1BattleBeginNotice = false;
        KfzbMatchService.day2BattleBeginNotice = false;
        KfzbMatchService.day3BattleBeginNotice = false;
        KfzbMatchService.battleEndNotice = false;
        KfzbMatchService.TIME_15MINMS = 900000L;
        KfzbMatchService.sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    }
    
    public KfzbMatchService() {
        this.cacheSuccHandled = false;
        this.kfzbState = null;
        this.address = null;
        this.port = 0;
        this.playerSaiquIdMap = new HashMap<Integer, Integer>();
        this.phase1AndPhase2KickOutCIdSet = new HashSet<Integer>();
        this.phase2InbattleCIdSet = new HashSet<Integer>();
        this.kfzbBattleInfoMap = new HashMap<Integer, KfzbBattleInfo>();
        this.kfzbBattleReportMap = new HashMap<Integer, KfzbBattleReport>();
        this.supportList = new LinkedList<KfzbRTSupport>();
        this.autoSavedMatchIdSet = new HashSet<String>();
        this.playerViewMap = new ConcurrentHashMap<Integer, Integer>();
        this.matchViewPlayersMap = new ConcurrentHashMap<Integer, Set<Integer>>();
    }
    
    @Override
    public void setAddressPort(final String address, final int port) {
        this.address = address;
        this.port = port;
    }
    
    @Override
	public void setApplicationContext(final ApplicationContext arg0) throws BeansException {
        this.context = arg0;
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final Object object = this.context.getBean("kfzbMatchService");
        this.self = (IKfzbMatchService)object;
    }
    
    @Override
    public void startStateThread(final String address, final int port) {
        KfzbMatchService.kfzbLogger.info("KfzbMatchService stateThread start, address:" + address + ", port:" + port);
        this.setConnection(address, port);
        KfzbMatchService.alive1 = true;
        if (KfzbMatchService.stateThread == null) {
            (KfzbMatchService.stateThread = new MatchStateThread()).start();
        }
        this.startReportThread();
        KfzbMatchService.kfzbLogger.info("KfzbMatchService stateThread start succ.");
    }
    
    @Override
    public void clearAllCacheBeforeStartNewSeason(final int newSeasonId) {
        KfzbMatchService.kfzbLogger.info("clearAllCacheBeforeStartNewSeason start.");
        this.kfzbState = null;
        this.address = null;
        this.port = 0;
        this.playerSaiquIdMap.clear();
        this.phase1AndPhase2KickOutCIdSet.clear();
        this.phase2InbattleCIdSet.clear();
        this.kfzbBattleInfoMap.clear();
        this.kfzbBattleReportMap.clear();
        this.playerViewMap.clear();
        this.matchViewPlayersMap.clear();
        this.supportList.clear();
        this.autoSavedMatchIdSet.clear();
        this.clearNoticeTime();
        this.cacheSuccHandled = false;
        KfzbManager.playerSignMap.clear();
        KfzbManager.playerTicketsMap.clear();
        KfwdMatchService.clearZbTitle(newSeasonId);
        KfzbMatchService.kfzbLogger.info("clearAllCacheBeforeStartNewSeason succ.");
    }
    
    public void setConnection(final String address, final int port) {
        KfzbMatchService.connectionZbMatchState = new KfConnection((TransferConfig)new TransferConfigMatchKfZb(address, port), KfzbMatchService.kfzbLogger, "KfzbMatchService_state_connection");
        this.address = address;
        this.port = port;
    }
    
    private void startReportThread() {
        KfzbMatchService.kfzbLogger.info("KfzbMatchService reportThread start, address:" + this.address + ", port:" + this.port);
        KfzbMatchService.alive2 = true;
        if (KfzbMatchService.reportThread == null) {
            (KfzbMatchService.reportThread = new MatchReportThread()).start();
        }
        KfzbMatchService.kfzbLogger.info("KfzbMatchService reportThread start succ.");
    }
    
    @Override
    public void stopMatchService() {
    }
    
    public void doSendSupNotice(final KfzbState oldKfzbState, final KfzbState newKfzbState) {
        if (oldKfzbState == null || newKfzbState == null) {
            return;
        }
        final int oldLayer = oldKfzbState.getLayer();
        final int oldRound = oldKfzbState.getRound();
        final int newLayer = newKfzbState.getLayer();
        final int newRound = newKfzbState.getRound();
        if (oldLayer > newLayer) {
            if (newLayer == 3 || newLayer == 4) {
                final String content = MessageFormat.format(LocalMessages.KFZB_CHAT_TOSUP1, LocalMessages.KFZB_BAT_POS[newLayer - 1]);
                this.dataGetter.getChatService().sendSystemChat("GLOBAL", 0, 0, content, null);
            }
            if (newLayer == 2 || newLayer == 1) {
                final String content = MessageFormat.format(LocalMessages.KFZB_CHAT_TOSUP2, LocalMessages.KFZB_BAT_POS[newLayer - 1], newRound);
                this.dataGetter.getChatService().sendSystemChat("GLOBAL", 0, 0, content, null);
            }
        }
        else if (oldRound < newRound && (newLayer == 2 || newLayer == 1)) {
            final String content = MessageFormat.format(LocalMessages.KFZB_CHAT_TOSUP2, LocalMessages.KFZB_BAT_POS[newLayer - 1], newRound);
            this.dataGetter.getChatService().sendSystemChat("GLOBAL", 0, 0, content, null);
        }
    }
    
    public void sendAllLastLayerReward(final int seasonId, final int layerId) {
        KfzbMatchService.getSupLock.writeLock().lock();
        try {
            KfzbMatchService.kfzbLogger.info("sendAllLastLayerReward");
            final List<KfzbSupport> supList = this.dataGetter.getKfzbSupportDao().getUnTakedSupportInfoBySeasonId(seasonId);
            final Map<Integer, Integer> playerCardMap = new HashMap<Integer, Integer>();
            for (final KfzbSupport sup : supList) {
                Integer card = playerCardMap.get(sup.getPlayerId());
                if (card == null) {
                    card = 0;
                }
                ++card;
                this.dataGetter.getKfzbSupportDao().updateTaketIt(sup);
                playerCardMap.put(sup.getPlayerId(), card);
            }
            for (final Map.Entry<Integer, Integer> entry : playerCardMap.entrySet()) {
                final int playerId = entry.getKey();
                final int card2 = entry.getValue();
                KfzbMatchService.kfzbLogger.info("addSupCard=" + playerId + "-" + card2);
                this.kfzbFeastService.addFreeCard(playerId, card2);
                String lastBattleName = "";
                if (layerId <= 3 && layerId >= 0) {
                    lastBattleName = LocalMessages.KFZB_BAT_POS[layerId];
                }
                String content = null;
                content = MessageFormatter.format(LocalMessages.KF_ZB_AUTO_SUP_REWARD_SEND, new Object[] { lastBattleName, card2 });
                this.dataGetter.getMailService().writeSystemMail(LocalMessages.T_MAIL_ROLE_SYSTEM, LocalMessages.KF_ZB_TITLE, content, 1, playerId, new Date());
            }
        }
        finally {
            KfzbMatchService.getSupLock.writeLock().unlock();
        }
        KfzbMatchService.getSupLock.writeLock().unlock();
    }
    
    public void sendAllLostPersonReward(final int seasonId) {
        KfzbMatchService.kfzbLogger.info("send lost person autoReward");
        KfzbMatchService.getTicketLock.writeLock().lock();
        try {
            final List<KfzbReward> list = this.dataGetter.getKfzbRewardDao().getBySeasonId(seasonId);
            if (list == null || list.size() == 0) {
                return;
            }
            for (final KfzbReward kfzbReward : list) {
                try {
                    final int playerId = kfzbReward.getPlayerId();
                    final Integer cId = KfzbManager.playerIdCIdMap.get(playerId);
                    if (cId == null) {
                        continue;
                    }
                    if (!this.phase1AndPhase2KickOutCIdSet.contains(cId)) {
                        continue;
                    }
                    int tickets = 0;
                    int addNum = 0;
                    if (kfzbReward.getRewardinfo() == null) {
                        continue;
                    }
                    final String[] rewards = kfzbReward.getRewardinfo().split(",");
                    final List<Integer> rewardlist = new LinkedList<Integer>();
                    for (int i = 0; i < rewards.length; ++i) {
                        final int num = Integer.parseInt(rewards[i]);
                        tickets += num;
                        rewardlist.add(num);
                        if (i >= kfzbReward.getDoneNum()) {
                            addNum += num;
                        }
                    }
                    if (addNum <= 0) {
                        continue;
                    }
                    this.dataGetter.getPlayerTicketsDao().addTickets(playerId, addNum, "\u8de8\u670d\u4e89\u9738\u8d5b\u81ea\u52a8\u9886\u53d6\u83b7\u5f97\u70b9\u5238" + kfzbReward.getDoneNum() + "-" + rewards.length, false);
                    this.dataGetter.getKfzbRewardDao().updateDoneNum(playerId, seasonId, rewards.length);
                    final Tuple<List<Integer>, Integer> tuple = KfzbManager.playerTicketsMap.get(playerId);
                    tuple.left = rewardlist;
                    tuple.right = rewards.length;
                    String content = null;
                    content = MessageFormatter.format(LocalMessages.KF_ZB_AUTO_BATTLE_REWARD_SEND, new Object[] { addNum });
                    this.dataGetter.getMailService().writeSystemMail(LocalMessages.T_MAIL_ROLE_SYSTEM, LocalMessages.KF_ZB_TITLE, content, 1, playerId, new Date());
                }
                catch (Exception e) {
                    ErrorSceneLog.getInstance().error("", e);
                }
            }
        }
        finally {
            KfzbMatchService.getTicketLock.writeLock().unlock();
        }
        KfzbMatchService.getTicketLock.writeLock().unlock();
    }
    
    private List<KfzbBattleReport> requestKfzbBattleReportFromMatch() {
        final List<Request> requestList = new ArrayList<Request>();
        for (final KfzbBattleInfo kfzbBattleInfo : this.kfzbBattleInfoMap.values()) {
            if (kfzbBattleInfo.isFinish()) {
                continue;
            }
            final KfzbPhase2MatchKey kfzbPhase2MatchKey = new KfzbPhase2MatchKey();
            final int matchId = kfzbBattleInfo.getMatchId();
            final int roundId = kfzbBattleInfo.getRound();
            kfzbPhase2MatchKey.setMatchId(matchId);
            kfzbPhase2MatchKey.setRoundId(roundId);
            final KfzbBattleReport kfzbBattleReport = this.kfzbBattleReportMap.get(matchId);
            if (kfzbBattleReport != null) {
                if (kfzbBattleReport.getRound() == roundId) {
                    final int size = kfzbBattleReport.getList().size();
                    final int frame = kfzbBattleReport.getList().get(size - 1).getFrame();
                    kfzbPhase2MatchKey.setFrame(frame);
                }
                else {
                    kfzbPhase2MatchKey.setFrame(0);
                }
            }
            else {
                kfzbPhase2MatchKey.setFrame(0);
            }
            final Request request = new Request();
            request.setCommand(Command.KFZB_GET_PHASE2_MATCH_DETAIL_INFO);
            request.setMessage(kfzbPhase2MatchKey);
            requestList.add(request);
        }
        if (requestList.size() == 0) {
            return null;
        }
        List<Response> responseList = null;
        try {
            responseList = (List<Response>)KfzbMatchService.connectionZbMatchState.sendRequestAndGetResponseList((List)requestList);
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("", e);
            return null;
        }
        if (responseList == null) {
            ErrorSceneLog.getInstance().appendErrorMsg("responseList is null").appendClassName(this.getClass().getSimpleName()).appendMethodName("requestKfzbBattleReportFromMatch").flush();
            return null;
        }
        final List<KfzbBattleReport> list = new ArrayList<KfzbBattleReport>();
        for (final Response response : responseList) {
            final KfzbBattleReport kfzbBattleReport2 = (KfzbBattleReport)response.getMessage();
            list.add(kfzbBattleReport2);
        }
        return list;
    }
    
    private void submitSupportAdd() {
        try {
            if (this.supportList.size() == 0) {
                return;
            }
            final List<Request> requestList = new ArrayList<Request>();
            synchronized (this.supportList) {
                for (final KfzbRTSupport kfzbRTSupport : this.supportList) {
                    final Request request = new Request();
                    request.setCommand(Command.KFZB_RT_SUPPORT);
                    request.setMessage(kfzbRTSupport);
                    requestList.add(request);
                }
                this.supportList.clear();
            }
            // monitorexit(this.supportList)
            try {
                KfzbMatchService.connectionZbMatchState.sendRequestAndGetResponseList((List)requestList);
            }
            catch (Exception e) {
                ErrorSceneLog.getInstance().error("", e);
            }
        }
        catch (Exception e2) {
            ErrorSceneLog.getInstance().error("", e2);
        }
    }
    
    public void handleAllCache(final KfzbState kfzbState) {
        try {
            final int seasonId = kfzbState.getSeasonId();
            KfzbManager.initPlayerSignMap(seasonId);
            final int globalState = kfzbState.getGlobalState();
            KfzbManager.initPlayerTicketsMap(seasonId);
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("", e);
        }
    }
    
    private void handleNewKfzbBattleReportList(final List<KfzbBattleReport> reportist) {
        for (final KfzbBattleReport kfzbBattleReport : reportist) {
            if (kfzbBattleReport != null && kfzbBattleReport.getList() != null) {
                if (kfzbBattleReport.getList().size() == 0) {
                    continue;
                }
                final int matchId = kfzbBattleReport.getMatchId();
                for (final FrameBattleReport frameBattleReport : kfzbBattleReport.getList()) {
                    KfzbMatchService.kfzbLogger.info(String.valueOf(matchId) + "-" + kfzbBattleReport.getRound() + "-" + frameBattleReport.getFrame());
                    KfzbMatchService.kfzbLogger.info(frameBattleReport.getIniReport());
                    KfzbMatchService.kfzbLogger.info(frameBattleReport.getBattleReport());
                    KfzbMatchService.kfzbLogger.info("");
                }
                this.kfzbBattleReportMap.put(matchId, kfzbBattleReport);
                final Set<Integer> viewSet = this.matchViewPlayersMap.get(matchId);
                if (viewSet == null) {
                    continue;
                }
                if (viewSet.size() == 0) {
                    continue;
                }
                try {
                    synchronized (viewSet) {
                        for (final Integer playerId : viewSet) {
                            try {
                                for (final FrameBattleReport frameBattleReport2 : kfzbBattleReport.getList()) {
                                    KfzbMatchService.kfzbLogger.info("send to playerId:" + playerId + ",fame:" + frameBattleReport2.getFrame());
                                    final JsonDocument doc = new JsonDocument();
                                    doc.startObject();
                                    doc.createElement("report", frameBattleReport2.getBattleReport());
                                    if (frameBattleReport2.isEnd()) {
                                        doc.createElement("nextRoundCD", frameBattleReport2.getNextRoundTime().getTime() - System.currentTimeMillis());
                                    }
                                    doc.endObject();
                                    Players.push(playerId, PushCommand.PUSH_KFZB, doc.toByte());
                                }
                                KfzbMatchService.kfzbLogger.info("");
                            }
                            catch (Exception e) {
                                ErrorSceneLog.getInstance().error("", e);
                            }
                        }
                        KfzbMatchService.kfzbLogger.info("\n");
                    }
                }
                catch (Exception e2) {
                    ErrorSceneLog.getInstance().error("", e2);
                }
            }
        }
    }
    
    @Deprecated
    public void appendSupportInfo(final JsonDocument doc, final Integer playerId, final int matchId, final FrameBattleReport frameBattleReport) {
        try {
            final KfzbBattleInfo kfzbBattleInfo = this.kfzbBattleInfoMap.get(matchId);
            if (kfzbBattleInfo == null) {
                ErrorSceneLog.getInstance().appendErrorMsg("kfzbBattleInfo == null").append("matchId", matchId).appendClassName(this.getClass().getSimpleName()).appendMethodName("appendSupportInfo").flush();
                return;
            }
            doc.startObject("zhichi");
            final int seasonId = this.kfzbState.getSeasonId();
            final int round = kfzbBattleInfo.getRound();
            KfzbSupport kfzbSupport = null;
            final int currentLayer = this.kfzbState.getPhase2Info().getCurLayer();
            if (matchId > 3) {
                kfzbSupport = this.dataGetter.getKfzbSupportDao().getByUniqIndex(playerId, seasonId, matchId, 0);
            }
            else {
                kfzbSupport = this.dataGetter.getKfzbSupportDao().getByUniqIndex(playerId, seasonId, matchId, round);
            }
            if (kfzbSupport != null) {
                if (kfzbBattleInfo.getP1().getCompetitorId().equals(kfzbSupport.getCId())) {
                    doc.createElement("supported", 1);
                }
                else if (kfzbBattleInfo.getP2().getCompetitorId().equals(kfzbSupport.getCId())) {
                    doc.createElement("supported", 2);
                }
                doc.endObject();
                return;
            }
            if (matchId > 3) {
                if (frameBattleReport.getState() == 2) {
                    doc.createElement("supported", (-1));
                    doc.endObject();
                    return;
                }
            }
            else if (!frameBattleReport.isEnd() && frameBattleReport.getState() == 2) {
                doc.createElement("supported", (-1));
                doc.endObject();
                return;
            }
            doc.createElement("supported", 0);
            doc.createElement("sup1", kfzbBattleInfo.getSup1());
            doc.createElement("sup2", kfzbBattleInfo.getSup2());
            final KfzbInfo kfzbInfo = this.dataGetter.getKfzbInfoDao().getByPlayerIdSeasonId(playerId, seasonId);
            if (kfzbInfo != null) {
                if (currentLayer > 2) {
                    doc.createElement("leftNum", kfzbInfo.getFlower1());
                }
                else {
                    doc.createElement("leftNum", kfzbInfo.getFlower2());
                }
            }
            doc.endObject();
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("", e);
        }
    }
    
    private KfzbState requestKfzbStateFromMatch() {
        final List<Request> requestList = new LinkedList<Request>();
        final Request request = new Request();
        request.setCommand(Command.KFZB_STATE);
        requestList.add(request);
        List<Response> responseList = null;
        try {
            responseList = (List<Response>)KfzbMatchService.connectionZbMatchState.sendRequestAndGetResponseList((List)requestList);
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("", e);
            return null;
        }
        if (responseList == null || responseList.size() == 0) {
            ErrorSceneLog.getInstance().appendErrorMsg("responseList is null").appendClassName(this.getClass().getSimpleName()).appendMethodName("requestKfzbStateFromMatch").flush();
            return null;
        }
        final KfzbState kfzbState = (KfzbState)responseList.get(0).getMessage();
        if (kfzbState == null) {
            ErrorSceneLog.getInstance().appendErrorMsg("kfzbState is null").appendClassName(this.getClass().getSimpleName()).appendMethodName("requestKfzbStateFromMatch").flush();
            return null;
        }
        return kfzbState;
    }
    
    private void requestKfzbRewardFromMatchAndHandle() {
        final List<Integer> cIdList = new LinkedList<Integer>();
        for (final Integer playerId : KfzbManager.playerSignMap.keySet()) {
            final Integer cId = KfzbManager.playerIdCIdMap.get(playerId);
            if (cId == null) {
                ErrorSceneLog.getInstance().appendErrorMsg("cId is null").appendPlayerId(playerId).appendClassName(this.getClass().getSimpleName()).appendMethodName("requestKfzbRewardFromMatchAndHandle").flush();
            }
            else {
                if (this.phase1AndPhase2KickOutCIdSet.contains(cId)) {
                    continue;
                }
                cIdList.add(cId);
            }
        }
        if (cIdList.size() == 0) {
            return;
        }
        final List<Request> requestList = new LinkedList<Request>();
        final Request request = new Request();
        request.setCommand(Command.KFZB_GET_PHASE1_REWARD_INFO);
        final KfzbPlayerListKey kfzbPlayerListKey = new KfzbPlayerListKey();
        kfzbPlayerListKey.setcIdList(cIdList);
        request.setMessage(kfzbPlayerListKey);
        requestList.add(request);
        List<Response> responseList = null;
        try {
            responseList = (List<Response>)KfzbMatchService.connectionZbMatchState.sendRequestAndGetResponseList((List)requestList);
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("", e);
            return;
        }
        if (responseList == null) {
            ErrorSceneLog.getInstance().appendErrorMsg("response is null").appendClassName(this.getClass().getSimpleName()).appendMethodName("requestKfzbRewardFromMatchAndHandle").flush();
            return;
        }
        final KfzbPhase1RewardInfoList kfzbPhase1RewardInfoList = (KfzbPhase1RewardInfoList)responseList.get(0).getMessage();
        if (kfzbPhase1RewardInfoList == null) {
            ErrorSceneLog.getInstance().appendErrorMsg("kfzbPhase1RewardInfoList is null").appendClassName(this.getClass().getSimpleName()).appendMethodName("requestKfzbRewardFromMatchAndHandle").flush();
            return;
        }
        final boolean inPhase2 = this.kfzbState.getGlobalState() >= 60;
        for (final KfzbPhase1RewardInfo kfzbPhase1RewardInfo : kfzbPhase1RewardInfoList.getList()) {
            final int layer = kfzbPhase1RewardInfo.getLostLayer();
            if (kfzbPhase1RewardInfo.isFinish()) {
                if (inPhase2) {
                    this.createKfzbInfoRecordForKickedOutInPhase2Player(kfzbPhase1RewardInfo.getcId());
                }
                if (layer < 4) {
                    this.saveKfZbTitle(kfzbPhase1RewardInfo.getcId(), layer);
                }
                this.phase1AndPhase2KickOutCIdSet.add(kfzbPhase1RewardInfo.getcId());
            }
            if (layer == 0 && this.kfzbState.getGlobalState() >= 70) {
                this.saveKfZbTitle(kfzbPhase1RewardInfo.getcId(), layer);
                this.phase1AndPhase2KickOutCIdSet.add(kfzbPhase1RewardInfo.getcId());
                final List<KfzbReward> titlelist = this.kfwdMatchService.loadKfzbTitle();
                for (final KfzbReward r : titlelist) {
                    try {
                        final String content = MessageFormatter.format(LocalMessages.KF_ZB_REWARD_TITLE, new Object[] { LocalMessages.KFZB_POS[r.getLastPos()], r.getTitle() });
                        this.dataGetter.getMailService().writeSystemMail(LocalMessages.T_MAIL_ROLE_SYSTEM, LocalMessages.KF_ZB_TITLE, content, 1, r.getPlayerId(), new Date());
                    }
                    catch (Exception e2) {
                        KfzbMatchService.kfzbLogger.error("", e2);
                    }
                }
            }
            KfzbManager.renewPlayerTickets(kfzbPhase1RewardInfo, this.kfzbState.getSeasonId());
        }
    }
    
    private void saveKfZbTitle(final int cId, final int layer) {
        try {
            final Integer playerId = KfzbManager.cIdPlayerIdMap.get(cId);
            if (playerId == null) {
                ErrorSceneLog.getInstance().appendErrorMsg("KfzbManager.cIdPlayerIdMap get null").append("cId", cId).appendClassName(this.getClass().getSimpleName()).appendMethodName("saveKfZbTitle").flush();
                return;
            }
            final int seasonId = this.kfzbState.getSeasonId();
            final KfzbReward kfzbReward = this.dataGetter.getKfzbRewardDao().getByPlayerIdSeasonId(playerId, seasonId);
            if (kfzbReward == null) {
                ErrorSceneLog.getInstance().appendErrorMsg("kfzbReward == null").appendPlayerId(playerId).append("seasonId", seasonId).append("layer", layer).appendClassName(this.getClass().getSimpleName()).appendMethodName("saveKfZbTitle").flush();
                return;
            }
            final String kfZbTitle = KfZbConstants.layerToTitleMap.get(layer);
            if (kfZbTitle == null) {
                ErrorSceneLog.getInstance().appendErrorMsg("kfZbTitle == null").append("layer", layer).appendClassName(this.getClass().getSimpleName()).appendMethodName("saveKfZbTitle").flush();
                return;
            }
            this.dataGetter.getKfzbRewardDao().updateTitle(playerId, seasonId, kfZbTitle, layer);
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("", e);
        }
    }
    
    private void createKfzbInfoRecordForKickedOutInPhase2Player(final Integer cId) {
        try {
            final Integer playerId = KfzbManager.cIdPlayerIdMap.get(cId);
            if (playerId == null) {
                ErrorSceneLog.getInstance().appendErrorMsg("KfzbManager.cIdPlayerIdMap get null").append("cId", cId).appendClassName(this.getClass().getSimpleName()).appendMethodName("createKfzbInfoRecordForKickedOutInPhase2Player").flush();
                return;
            }
            final int seasonId = this.kfzbState.getSeasonId();
            KfzbInfo kfzbInfo = this.dataGetter.getKfzbInfoDao().getByPlayerIdSeasonId(playerId, seasonId);
            if (kfzbInfo != null) {
                return;
            }
            final int key;
            final int layer = key = this.kfzbState.getPhase2Info().getCurLayer();
            final Tuple<Integer, Integer> tuple = KfZbConstants.kickedOutFlowermap.get(key);
            if (tuple == null) {
                ErrorSceneLog.getInstance().appendErrorMsg("tuple == null").append("key", key).appendClassName(this.getClass().getSimpleName()).appendMethodName("createKfzbInfoRecordForKickedOutInPhase2Player").flush();
                return;
            }
            kfzbInfo = new KfzbInfo();
            kfzbInfo.setPlayerId(playerId);
            kfzbInfo.setSeasonId(seasonId);
            kfzbInfo.setFlower1(tuple.left);
            kfzbInfo.setFlower1Buy(0);
            kfzbInfo.setSupport1Info(null);
            kfzbInfo.setFlower2(tuple.right);
            kfzbInfo.setFlower2Buy(0);
            kfzbInfo.setSupport2Info(null);
            this.dataGetter.getKfzbInfoDao().create(kfzbInfo);
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("", e);
        }
    }
    
    private void handleNewlyKfzbState(final KfzbState newKfzbState) {
        try {
            if (newKfzbState.getGlobalState() > 30 && newKfzbState.getGlobalState() <= 50) {
                this.requestKfzbSaiquInfoFromMatch();
            }
            this.doChatNotice(newKfzbState);
            this.handlePhase2InbattleCIdSet(newKfzbState);
            this.autoSaveSupportTickets(newKfzbState);
            this.mapBattleInfos(newKfzbState);
            boolean roundLayerInfoChange = false;
            if (this.kfzbState == null) {
                this.kfzbState = newKfzbState;
                this.pushNewStateToOnLinePlayer(newKfzbState.getGlobalState());
            }
            else {
                if (this.kfzbState.getLayer() != newKfzbState.getLayer() || this.kfzbState.getRound() != newKfzbState.getRound()) {
                    roundLayerInfoChange = true;
                }
                if (this.kfzbState.getGlobalState() == newKfzbState.getGlobalState()) {
                    this.handleOneState(newKfzbState);
                }
                else {
                    this.handleTransferState(newKfzbState);
                    this.kfzbState = newKfzbState;
                    if (newKfzbState.getGlobalState() >= 70) {
                        final List<KfzbReward> titlelist = this.kfwdMatchService.loadKfzbTitle();
                        for (final KfzbReward r : titlelist) {
                            try {
                                final String content = MessageFormatter.format(LocalMessages.KF_ZB_REWARD_TITLE, new Object[] { LocalMessages.KFZB_POS[r.getLastPos()], r.getTitle() });
                                this.dataGetter.getMailService().writeSystemMail(LocalMessages.T_MAIL_ROLE_SYSTEM, LocalMessages.KF_ZB_TITLE, content, 1, r.getPlayerId(), new Date());
                            }
                            catch (Exception e) {
                                KfzbMatchService.kfzbLogger.error("", e);
                            }
                        }
                    }
                    if (newKfzbState.getGlobalState() >= 80 && KfzbManager.playerTicketsMap.size() > 0) {
                        KfzbManager.playerTicketsMap.clear();
                        KfzbManager.playerSignMap.clear();
                        this.playerSaiquIdMap.clear();
                        this.phase1AndPhase2KickOutCIdSet.clear();
                        this.phase2InbattleCIdSet.clear();
                        this.kfzbBattleInfoMap.clear();
                        this.kfzbBattleReportMap.clear();
                        this.playerViewMap.clear();
                        this.matchViewPlayersMap.clear();
                        this.endCurrentSeason(newKfzbState.getSeasonId());
                    }
                    else if (newKfzbState.getGlobalState() >= 80) {
                        final JsonDocument doc = new JsonDocument();
                        doc.startObject();
                        doc.createElement("kfzbState", (-1));
                        doc.endObject();
                        for (final PlayerDto playerDto : Players.getAllPlayer()) {
                            Players.push(playerDto.playerId, PushCommand.PUSH_UPDATE, doc.toByte());
                        }
                    }
                }
                if (roundLayerInfoChange) {
                    final JsonDocument doc = new JsonDocument();
                    doc.startObject();
                    doc.createElement("refresh", 1);
                    doc.endObject();
                    Players.pushToALL(PushCommand.PUSH_KFZB, doc.toByte());
                }
            }
        }
        catch (Exception e2) {
            ErrorSceneLog.getInstance().error("", e2);
        }
    }
    
    private void clearNoticeTime() {
        KfzbMatchService.day1NoticeTime = 0L;
        KfzbMatchService.day2NoticeTime = 0L;
        KfzbMatchService.day3NoticeTime = 0L;
        KfzbMatchService.day1BattleBeginNotice = false;
        KfzbMatchService.day2BattleBeginNotice = false;
        KfzbMatchService.day3BattleBeginNotice = false;
        KfzbMatchService.battleEndNotice = false;
    }
    
    private void doChatNotice(final KfzbState newKfzbState) {
        final KfzbSeasonInfo seasonInfo = this.kfzbSeasonService.getKfzbSeasonInfo();
        if (newKfzbState == null || seasonInfo == null || seasonInfo.getSeasonId() != newKfzbState.getSeasonId()) {
            return;
        }
        final int state = newKfzbState.getGlobalState();
        final long nextCd = newKfzbState.getNextGlobalStateCD();
        final long nowTime = System.currentTimeMillis();
        if (state == 50) {
            final long d1BattleTime = seasonInfo.getDay1BattleTime().getTime();
            final long d1BattleCd = d1BattleTime - nowTime;
            if (d1BattleCd > 0L && d1BattleCd < 60000L && KfzbMatchService.day1NoticeTime + KfzbMatchService.TIME_15MINMS < nowTime) {
                KfzbMatchService.day1NoticeTime = nowTime;
                final String content = MessageFormat.format(LocalMessages.KFZB_CHAT_1, KfzbMatchService.sdf.format(seasonInfo.getDay1BattleTime()));
                this.dataGetter.getChatService().sendSystemChat("GLOBAL", 0, 0, content, null);
            }
            if (d1BattleTime < 60000L && d1BattleTime > 0L && !KfzbMatchService.day1BattleBeginNotice) {
                this.dataGetter.getChatService().sendSystemChat("GLOBAL", 0, 0, LocalMessages.KFZB_CHAT_4, null);
                KfzbMatchService.day1BattleBeginNotice = true;
            }
        }
        else if (state == 60) {
            final long d2BattleTime = seasonInfo.getDay2BattleTime().getTime();
            final long d2BattleCd = d2BattleTime - nowTime;
            if (d2BattleCd > 0L && d2BattleCd < 60000L && KfzbMatchService.day2NoticeTime + KfzbMatchService.TIME_15MINMS < nowTime) {
                KfzbMatchService.day2NoticeTime = nowTime;
                final String content = MessageFormat.format(LocalMessages.KFZB_CHAT_2, KfzbMatchService.sdf.format(seasonInfo.getDay2BattleTime()));
                this.dataGetter.getChatService().sendSystemChat("GLOBAL", 0, 0, content, null);
            }
            if (d2BattleTime < 60000L && d2BattleTime > 0L && !KfzbMatchService.day2BattleBeginNotice) {
                this.dataGetter.getChatService().sendSystemChat("GLOBAL", 0, 0, LocalMessages.KFZB_CHAT_5, null);
                KfzbMatchService.day2BattleBeginNotice = true;
            }
        }
        else if (state == 65) {
            final long d3BattleTime = seasonInfo.getDay3BattleTime().getTime();
            final long d3BattleCd = d3BattleTime - nowTime;
            if (d3BattleCd > 0L && d3BattleCd < 60000L && KfzbMatchService.day3NoticeTime + KfzbMatchService.TIME_15MINMS < nowTime) {
                KfzbMatchService.day3NoticeTime = nowTime;
                final String content = MessageFormat.format(LocalMessages.KFZB_CHAT_3, KfzbMatchService.sdf.format(seasonInfo.getDay3BattleTime()));
                this.dataGetter.getChatService().sendSystemChat("GLOBAL", 0, 0, content, null);
            }
            if (d3BattleTime < 60000L && d3BattleTime > 0L && !KfzbMatchService.day3BattleBeginNotice) {
                this.dataGetter.getChatService().sendSystemChat("GLOBAL", 0, 0, LocalMessages.KFZB_CHAT_6, null);
                KfzbMatchService.day3BattleBeginNotice = true;
            }
        }
        else if (state >= 70 && !KfzbMatchService.battleEndNotice) {
            final KfzbPhase2Info p2Info = newKfzbState.getPhase2Info();
            if (p2Info != null) {
                final KfzbBattleInfo battleInfo = p2Info.getMap().get(1);
                if (battleInfo != null && battleInfo.isFinish()) {
                    final String[] pName = { "", "" };
                    final String[] pNation = { "", "" };
                    final String[] pInfo = { "", "" };
                    final String[] pServerInfo = { "", "" };
                    final String weiColor = "<font color=\"#6EB4EE\">";
                    final String suColor = "<font color=\"#EB9642\">";
                    final String wuColor = "<font color=\"#88D442\">";
                    final String colorEnd = "</font>";
                    for (int i = 0; i <= 1; ++i) {
                        KfzbPlayerInfo p = null;
                        if (battleInfo.getP1Win() > battleInfo.getP2Win()) {
                            if (i == 0) {
                                p = battleInfo.getP1();
                            }
                            else {
                                p = battleInfo.getP2();
                            }
                        }
                        else if (i == 1) {
                            p = battleInfo.getP1();
                        }
                        else {
                            p = battleInfo.getP2();
                        }
                        pName[i] = p.getPlayerName();
                        if (p.getNation() == 1) {
                            pNation[i] = LocalMessages.T_FORCE_WEI;
                            pInfo[i] = String.valueOf(weiColor) + pNation[i] + "\u2022" + pName[i] + colorEnd;
                        }
                        else if (p.getNation() == 2) {
                            pNation[i] = LocalMessages.T_FORCE_SHU;
                            pInfo[i] = String.valueOf(suColor) + pNation[i] + "\u2022" + pName[i] + colorEnd;
                        }
                        else {
                            pNation[i] = LocalMessages.T_FORCE_WU;
                            pInfo[i] = String.valueOf(wuColor) + pNation[i] + "\u2022" + pName[i] + colorEnd;
                        }
                        pServerInfo[i] = String.valueOf(p.getServerName()) + " " + p.getServerId();
                    }
                    final String content2 = MessageFormat.format(LocalMessages.KFZB_CHAT_END, pServerInfo[0], pInfo[0], pServerInfo[1], pInfo[1]);
                    this.dataGetter.getChatService().sendSystemChat("GLOBAL", 0, 0, content2, null);
                    KfzbMatchService.battleEndNotice = true;
                }
            }
        }
    }
    
    private void endCurrentSeason(final int seasonId) {
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("kfzbState", (-1));
        doc.endObject();
        for (final PlayerDto playerDto : Players.getAllPlayer()) {
            Players.push(playerDto.playerId, PushCommand.PUSH_UPDATE, doc.toByte());
        }
        final List<KfzbReward> list = this.dataGetter.getKfzbRewardDao().getBySeasonId(seasonId);
        if (list == null || list.size() == 0) {
            return;
        }
        for (final KfzbReward kfzbReward : list) {
            try {
                final int playerId = kfzbReward.getPlayerId();
                int tickets = 0;
                int addNum = 0;
                if (kfzbReward.getRewardinfo() != null) {
                    final String[] rewards = kfzbReward.getRewardinfo().split(",");
                    for (int i = 0; i < rewards.length; ++i) {
                        final int num = Integer.parseInt(rewards[i]);
                        tickets += num;
                        if (i >= kfzbReward.getDoneNum()) {
                            addNum += num;
                        }
                    }
                    if (addNum > 0) {
                        this.dataGetter.getPlayerTicketsDao().addTickets(playerId, addNum, "\u8de8\u670d\u4e89\u9738\u8d5b\u81ea\u52a8\u9886\u53d6\u83b7\u5f97\u70b9\u5238" + kfzbReward.getDoneNum() + "-" + rewards.length, false);
                        this.dataGetter.getKfzbRewardDao().updateDoneNum(playerId, seasonId, rewards.length);
                    }
                }
                String content = null;
                if (kfzbReward.getTitle() == null) {
                    content = MessageFormatter.format(LocalMessages.KF_ZB_SIGN_END_MAIL_FORMAT1, new Object[] { tickets });
                }
                else {
                    content = MessageFormatter.format(LocalMessages.KF_ZB_SIGN_END_MAIL_FORMAT1, new Object[] { tickets, kfzbReward.getTitle() });
                }
                this.dataGetter.getMailService().writeSystemMail(LocalMessages.T_MAIL_ROLE_SYSTEM, LocalMessages.KF_ZB_SIGN_END_MAIL_TITLE, content, 1, playerId, new Date());
            }
            catch (Exception e) {
                ErrorSceneLog.getInstance().error("", e);
            }
        }
        try {
            this.sendAllLostPersonReward(seasonId);
        }
        catch (Exception e2) {
            ErrorSceneLog.getInstance().error("", e2);
        }
    }
    
    private void pushNewStateToOnLinePlayer(final int state) {
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("kfzbState", state);
        doc.endObject();
        switch (state) {
            case 20: {
                for (final PlayerDto playerDto : Players.getAllPlayer()) {
                    if (playerDto.playerLv < this.kfzbSeasonService.getKfzbSeasonInfo().getMinLv()) {
                        continue;
                    }
                    Players.push(playerDto.playerId, PushCommand.PUSH_UPDATE, doc.toByte());
                }
                break;
            }
            case 30:
            case 40:
            case 50: {
                for (final PlayerDto playerDto : Players.getAllPlayer()) {
                    if (KfzbManager.playerSignMap.get(playerDto.playerId) == null) {
                        final JsonDocument removeDoc = new JsonDocument();
                        removeDoc.startObject();
                        removeDoc.createElement("kfzbState", (-1));
                        removeDoc.endObject();
                        Players.push(playerDto.playerId, PushCommand.PUSH_UPDATE, removeDoc.toByte());
                    }
                    else {
                        Players.push(playerDto.playerId, PushCommand.PUSH_UPDATE, doc.toByte());
                    }
                }
                break;
            }
            case 51:
            case 60:
            case 61:
            case 65: {
                for (final PlayerDto playerDto : Players.getAllPlayer()) {
                    if (playerDto.playerLv >= this.kfzbSeasonService.getKfzbSeasonInfo().getSupLv()) {
                        Players.push(playerDto.playerId, PushCommand.PUSH_UPDATE, doc.toByte());
                    }
                    else {
                        final JsonDocument newdoc = new JsonDocument();
                        newdoc.startObject();
                        newdoc.createElement("kfzbState", (-60));
                        newdoc.endObject();
                        Players.push(playerDto.playerId, PushCommand.PUSH_UPDATE, doc.toByte());
                    }
                }
                break;
            }
        }
    }
    
    private void requestKfzbSaiquInfoFromMatch() {
        if (KfzbManager.playerSignMap.size() == this.playerSaiquIdMap.size()) {
            return;
        }
        final List<Integer> cIdList = new LinkedList<Integer>();
        for (final Integer playerId : KfzbManager.playerSignMap.keySet()) {
            if (this.playerSaiquIdMap.get(playerId) != null) {
                continue;
            }
            final Integer cId = KfzbManager.playerIdCIdMap.get(playerId);
            if (cId == null) {
                ErrorSceneLog.getInstance().appendErrorMsg("cId is null").appendPlayerId(playerId).appendClassName(this.getClass().getSimpleName()).appendMethodName("requestKfzbSaiquInfoFromMatch").flush();
            }
            else {
                cIdList.add(cId);
            }
        }
        if (cIdList.size() == 0) {
            return;
        }
        final List<Request> requestList = new LinkedList<Request>();
        final Request request = new Request();
        request.setCommand(Command.KFZB_PLAYER_GID);
        final KfzbPlayerListKey kfzbPlayerListKey = new KfzbPlayerListKey();
        kfzbPlayerListKey.setcIdList(cIdList);
        request.setMessage(kfzbPlayerListKey);
        requestList.add(request);
        List<Response> responseList = null;
        try {
            responseList = (List<Response>)KfzbMatchService.connectionZbMatchState.sendRequestAndGetResponseList((List)requestList);
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("", e);
            return;
        }
        if (responseList == null) {
            ErrorSceneLog.getInstance().appendErrorMsg("responseList is null").appendClassName(this.getClass().getSimpleName()).appendMethodName("requestKfzbSaiquInfoFromMatch").flush();
            return;
        }
        final KfzbPlayerGroupInfo kfzbPlayerGroupInfo = (KfzbPlayerGroupInfo)responseList.get(0).getMessage();
        if (kfzbPlayerGroupInfo == null) {
            ErrorSceneLog.getInstance().appendErrorMsg("kfzbPlayerGroupInfo is null").appendClassName(this.getClass().getSimpleName()).appendMethodName("requestKfzbSaiquInfoFromMatch").flush();
            return;
        }
        for (final KfzbPlayerGroup kfzbPlayerGroup : kfzbPlayerGroupInfo.getList()) {
            final Integer cId2 = kfzbPlayerGroup.getcId();
            final Integer playerId2 = KfzbManager.cIdPlayerIdMap.get(cId2);
            if (playerId2 == null) {
                ErrorSceneLog.getInstance().appendErrorMsg("playerId is null").append("cId", cId2).appendClassName(this.getClass().getSimpleName()).appendMethodName("requestKfzbSaiquInfoFromMatch").flush();
            }
            else {
                this.playerSaiquIdMap.put(playerId2, kfzbPlayerGroup.getgId());
            }
        }
    }
    
    private void autoSaveSupportTickets(final KfzbState newKfzbState) {
        if (newKfzbState.getPhase2Info() == null || newKfzbState.getPhase2Info().getMap() == null || newKfzbState.getPhase2Info().getMap().size() < 8) {
            return;
        }
        final int seasonId = newKfzbState.getSeasonId();
        for (final KfzbBattleInfo kfzbBattleInfo : newKfzbState.getPhase2Info().getMap().values()) {
            final int matchId = kfzbBattleInfo.getMatchId();
            if (matchId < 4) {
                this.autoSaveSupportTickets2(kfzbBattleInfo, seasonId);
            }
            else {
                if (!kfzbBattleInfo.isFinish()) {
                    continue;
                }
                this.autoSaveSupportTickets1(kfzbBattleInfo, seasonId);
            }
        }
    }
    
    private void autoSaveSupportTickets1(final KfzbBattleInfo kfzbBattleInfo, final int seasonId) {
        try {
            final int matchId = kfzbBattleInfo.getMatchId();
            int winnerCId = 0;
            final String Key = matchId + "-" + seasonId;
            if (this.autoSavedMatchIdSet.contains(Key)) {
                return;
            }
            if (kfzbBattleInfo.getP1Win() > kfzbBattleInfo.getP2Win()) {
                winnerCId = kfzbBattleInfo.getP1().getCompetitorId();
            }
            else {
                winnerCId = kfzbBattleInfo.getP2().getCompetitorId();
            }
            KfzbManager.autoSaveSupportTickets(seasonId, matchId, 0, winnerCId);
            this.autoSavedMatchIdSet.add(Key);
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error(e);
        }
    }
    
    private void autoSaveSupportTickets2(final KfzbBattleInfo kfzbBattleInfo, final int seasonId) {
        try {
            final int matchId = kfzbBattleInfo.getMatchId();
            int winnerCId = 0;
            final int[] roundWinInfo = kfzbBattleInfo.getP1WinRes();
            for (int i = 0; i < roundWinInfo.length; ++i) {
                final int roundId = i + 1;
                final String Key = String.valueOf(matchId) + "-" + roundId + "-" + seasonId;
                if (!this.autoSavedMatchIdSet.contains(Key)) {
                    if (roundWinInfo[i] != 0) {
                        if (roundWinInfo[i] == 1) {
                            winnerCId = kfzbBattleInfo.getP1().getCompetitorId();
                        }
                        else {
                            winnerCId = kfzbBattleInfo.getP2().getCompetitorId();
                        }
                        KfzbManager.autoSaveSupportTickets(seasonId, matchId, roundId, winnerCId);
                        this.autoSavedMatchIdSet.add(Key);
                    }
                }
            }
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("", e);
        }
    }
    
    private void mapBattleInfos(final KfzbState newKfzbState) {
        if (newKfzbState == null) {
            return;
        }
        if (newKfzbState.getPhase2Info() == null || newKfzbState.getPhase2Info().getMap() == null || newKfzbState.getPhase2Info().getMap().size() == 0) {
            return;
        }
        for (final KfzbBattleInfo kfzbBattleInfo : newKfzbState.getPhase2Info().getMap().values()) {
            this.kfzbBattleInfoMap.put(kfzbBattleInfo.getMatchId(), kfzbBattleInfo);
        }
        final Set<Integer> cIdSet = new HashSet<Integer>();
        final Map<Integer, KfzbBattleInfo> battleInfoMap = newKfzbState.getPhase2Info().getMap();
        for (final KfzbBattleInfo kfzbBattleInfo2 : battleInfoMap.values()) {
            cIdSet.add(kfzbBattleInfo2.getP1().getCompetitorId());
            cIdSet.add(kfzbBattleInfo2.getP2().getCompetitorId());
        }
        for (final KfzbBattleInfo kfzbBattleInfo2 : battleInfoMap.values()) {
            if (kfzbBattleInfo2.getP1Win() + kfzbBattleInfo2.getP2Win() >= kfzbBattleInfo2.getLayerRound()) {
                if (kfzbBattleInfo2.getP1Win() < kfzbBattleInfo2.getP2Win()) {
                    cIdSet.remove(kfzbBattleInfo2.getP1().getCompetitorId());
                }
                else {
                    cIdSet.remove(kfzbBattleInfo2.getP2().getCompetitorId());
                }
            }
        }
        this.phase2InbattleCIdSet = cIdSet;
    }
    
    private void handlePhase2InbattleCIdSet(final KfzbState newKfzbState) {
    }
    
    private void handleOneState(final KfzbState newKfzbState) {
        this.kfzbState = newKfzbState;
        switch (this.kfzbState.getGlobalState()) {
            case 20: {}
            case 30: {}
            case 40: {}
            case 50: {}
            case 60: {}
            case 65: {}
            case 70: {}
        }
    }
    
    private void handleTransferState(final KfzbState newKfzbState) {
        if (newKfzbState.getGlobalState() < this.kfzbState.getGlobalState()) {
            ErrorSceneLog.getInstance().appendErrorMsg("newKfzbState.getGlobalState() < this.kfzbState.getGlobalState()").append("newKfzbState.getGlobalState()", newKfzbState.getGlobalState()).append("this.kfzbState.getGlobalState()", this.kfzbState.getGlobalState()).flush();
            return;
        }
        this.pushNewStateToOnLinePlayer(newKfzbState.getGlobalState());
        if (this.kfzbState.getGlobalState() == 20 && newKfzbState.getGlobalState() == 30) {
            return;
        }
        if (this.kfzbState.getGlobalState() == 30 && newKfzbState.getGlobalState() == 40) {
            return;
        }
        if (this.kfzbState.getGlobalState() == 40 && newKfzbState.getGlobalState() == 50) {
            return;
        }
        if (this.kfzbState.getGlobalState() == 51 && newKfzbState.getGlobalState() == 60) {
            return;
        }
        if (this.kfzbState.getGlobalState() == 60 && newKfzbState.getGlobalState() == 65) {
            return;
        }
        if (this.kfzbState.getGlobalState() == 65 && newKfzbState.getGlobalState() == 70) {
            return;
        }
        if (this.kfzbState.getGlobalState() == 70 && newKfzbState.getGlobalState() == 80) {
            return;
        }
    }
    
    @Override
    public void appendKfzbSeasonInfo(final Player player, final JsonDocument doc) {
        try {
            final KfzbState tempKfzbState = this.kfzbState;
            if (tempKfzbState == null) {
                return;
            }
            final int kfzbState = tempKfzbState.getGlobalState();
            switch (kfzbState) {
                case 20: {
                    if (player.getPlayerLv() < this.kfzbSeasonService.getKfzbSeasonInfo().getMinLv()) {
                        return;
                    }
                    doc.createElement("kfzbState", kfzbState);
                    if (KfzbManager.playerSignMap.get(player.getPlayerId()) != null) {
                        doc.createElement("kfzbState", 30);
                        break;
                    }
                    break;
                }
                case 30:
                case 40:
                case 50: {
                    if (KfzbManager.playerSignMap.get(player.getPlayerId()) != null) {
                        doc.createElement("kfzbState", kfzbState);
                        break;
                    }
                    break;
                }
                case 51:
                case 60:
                case 61:
                case 65:
                case 70: {
                    if (player.getPlayerLv() >= this.kfzbSeasonService.getKfzbSeasonInfo().getSupLv()) {
                        doc.createElement("kfzbState", kfzbState);
                        break;
                    }
                    doc.createElement("kfzbState", (-60));
                    break;
                }
            }
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("", e);
        }
    }
    
    private void pushNewStateToPlayer(final int playerId, final KfzbState tempKfzbState) {
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("kfzbState", tempKfzbState.getGlobalState());
        doc.endObject();
        Players.push(playerId, PushCommand.PUSH_UPDATE, doc.toByte());
    }
    
    @Override
    public byte[] getSignUpPanel(final PlayerDto playerDto) {
        final KfzbState tempKfzbState = this.kfzbState;
        if (tempKfzbState == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.KF_SEASON_NOT_OPEN);
        }
        if (tempKfzbState.getGlobalState() >= 50) {
            this.pushNewStateToPlayer(playerDto.playerId, tempKfzbState);
            return JsonBuilder.getJson(State.FAIL, "\u6d77\u9009\u6bd4\u8d5b\u5df2\u7ecf\u5f00\u59cb\uff0c\u524d\u7aef\u5e94\u8be5\u8c03\u7528\u5176\u4ed6\u63a5\u53e3");
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        final KfzbSignObj kfzbSignObj = KfzbManager.playerSignMap.get(playerDto.playerId);
        if (kfzbSignObj != null) {
            doc.createElement("address", this.address);
            doc.createElement("port", this.port);
            doc.createElement("cId", kfzbSignObj.competitorId);
        }
        else {
            doc.createElement("minLv", this.kfzbSeasonService.getKfzbSeasonInfo().getMinLv());
        }
        doc.createElement("countDown", tempKfzbState.getNextGlobalStateCD());
        doc.createElement("nextcd", tempKfzbState.getNextGlobalStateCD());
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public byte[] signUp(final PlayerDto playerDto) {
        final Player player = this.dataGetter.getPlayerDao().read(playerDto.playerId);
        if (player == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10012);
        }
        final KfzbState tempKfzbState = this.kfzbState;
        if (tempKfzbState == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.KF_SEASON_NOT_OPEN);
        }
        if (tempKfzbState.getGlobalState() != 20) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.KF_SEASON_CANNOT_SIGNUP);
        }
        final KfzbSeasonInfo kfzbSeasonInfo = this.dataGetter.getKfzbSeasonService().getKfzbSeasonInfo();
        if (playerDto.playerLv < kfzbSeasonInfo.getMinLv()) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.KF_ZB_LV_NOT_ENOUGH);
        }
        final KfzbSignObj kfzbSignObj = KfzbManager.playerSignMap.get(playerDto.playerId);
        if (kfzbSignObj != null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.KF_SEASON_SIGNUPED_ALREADY);
        }
        final KfzbSignup kfzbSignup = this.dataGetter.getKfzbSignupDao().getByPlayerIdAndSeasonId(playerDto.playerId, this.kfzbState.getSeasonId());
        if (kfzbSignup != null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.KF_SEASON_SIGNUPED_ALREADY);
        }
        final Tuple<Boolean, String> result = this.self.syncData(player, this.getDefaultGIds(playerDto.playerId), true);
        if (!(boolean)result.left) {
            return JsonBuilder.getJson(State.FAIL, result.right);
        }
        String content = MessageFormatter.format(LocalMessages.KF_ZB_SIGN_UP_CHAT_FORMAT, new Object[] { SystemService.getForceByForceId(player.getForceId()), player.getPlayerName() });
        this.dataGetter.getChatService().sendSystemChat("GLOBAL", 0, 0, content, null);
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        content = MessageFormatter.format(LocalMessages.KF_ZB_SIGN_UP_MAIL_FORMAT, new Object[] { sdf.format(kfzbSeasonInfo.getDay1BattleTime()) });
        this.dataGetter.getMailService().writeSystemMail(LocalMessages.T_MAIL_ROLE_SYSTEM, LocalMessages.KF_ZB_SIGN_UP_MAIL_TITLE, content, 1, playerDto.playerId, new Date());
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("kfzbState", 30);
        doc.endObject();
        Players.pushToALL(PushCommand.PUSH_UPDATE, doc.toByte());
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Transactional
    @Override
    public Tuple<Boolean, String> syncData(final Player player, final String gIds, final boolean isSignUp) {
        final Tuple<Boolean, String> resultTuple = new Tuple();
        resultTuple.left = false;
        final int playerId = player.getPlayerId();
        final KfzbSignInfo kfzbSignInfo = new KfzbSignInfo();
        final KfzbPlayerInfo kfzbPlayerInfo = new KfzbPlayerInfo();
        kfzbPlayerInfo.setPic(String.valueOf(player.getPic()));
        kfzbPlayerInfo.setPlayerId(player.getPlayerId());
        kfzbPlayerInfo.setPlayerName(player.getPlayerName());
        kfzbPlayerInfo.setNation(player.getForceId());
        kfzbPlayerInfo.setPlayerLevel(player.getPlayerLv());
        kfzbPlayerInfo.setServerId(Configuration.getProperty(player.getYx(), "gcld.serverid"));
        kfzbPlayerInfo.setServerName(Configuration.getProperty(player.getYx(), "gcld.showservername"));
        int cId = 0;
        if (!isSignUp) {
            final KfzbSignObj kfzbSignObj = KfzbManager.playerSignMap.get(playerId);
            cId = kfzbSignObj.competitorId;
        }
        else {
            KfgzSignup kfgzSignup = this.dataGetter.getKfgzSignupDao().read(playerId);
            if (kfgzSignup == null) {
                final KfgzSignup cidFromGW = this.kfgzSeasonService.requestCidFromGW(playerId);
                if (cidFromGW == null) {
                    resultTuple.right = LocalMessages.KF_ZB_CID_ERROR_FROM_GW;
                    return resultTuple;
                }
                kfgzSignup = cidFromGW;
            }
            cId = kfgzSignup.getCompetitorId();
        }
        kfzbPlayerInfo.setCompetitorId(cId);
        kfzbSignInfo.setSeasonId(this.kfzbState.getSeasonId());
        kfzbSignInfo.setPlayerInfo(kfzbPlayerInfo);
        try {
            kfzbSignInfo.setCampInfo(Types.OBJECT_MAPPER.writeValueAsString((Object)this.dataGetter.getBattleService().getKfwdCampDatas(playerId, gIds)));
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("", e);
        }
        if (kfzbSignInfo.getCampInfo() == null || kfzbSignInfo.getCampInfo().length() < 10) {
            ErrorSceneLog.getInstance().appendErrorMsg("invalid CampInfo").appendClassName(this.getClass().getSimpleName()).appendMethodName("syncData").flush();
            resultTuple.right = LocalMessages.KF_ZB_SERIALIZE_GENERAL_ERROR;
            return resultTuple;
        }
        final Request request = new Request();
        if (isSignUp) {
            request.setCommand(Command.KFZB_SIGN_FROM_GAMESERVER);
        }
        else {
            request.setCommand(Command.KFZB_SYNDATA_FROM_GAMESERVER);
        }
        request.setMessage(kfzbSignInfo);
        final List<Request> requestList = new ArrayList<Request>();
        requestList.add(request);
        Response response = null;
        synchronized (this) {
            response = KfzbMatchService.connectionZbMatchState.sendSyncAndGetResponse((List)requestList);
        }
        final KfzbSignResult kfzbSignResult = (KfzbSignResult)response.getMessage();
        if (kfzbSignResult == null || kfzbSignResult.getState() == 0) {
            ErrorSceneLog.getInstance().appendErrorMsg("\u5411match\u540c\u6b65\u6570\u636e\u5931\u8d25").appendClassName(this.getClass().getSimpleName()).appendMethodName("syncData").flush();
            resultTuple.right = LocalMessages.KF_ZB_NO_RESPONSE_FROM_MATCH;
            return resultTuple;
        }
        if (isSignUp) {
            final KfzbState tempKfzbState = this.kfzbState;
            final KfzbSignup kfzbSignup = new KfzbSignup();
            kfzbSignup.setPlayerId(player.getPlayerId());
            kfzbSignup.setSeasonId(tempKfzbState.getSeasonId());
            kfzbSignup.setSaiquId(0);
            this.dataGetter.getKfzbSignupDao().create(kfzbSignup);
            final KfzbReward kfzbReward = new KfzbReward();
            kfzbReward.setPlayerId(player.getPlayerId());
            kfzbReward.setSeasonId(tempKfzbState.getSeasonId());
            kfzbReward.setDoneNum(0);
            this.dataGetter.getKfzbRewardDao().create(kfzbReward);
            final KfzbSignObj kfzbSignObj2 = new KfzbSignObj();
            kfzbSignObj2.playerId = player.getPlayerId();
            kfzbSignObj2.seasonId = tempKfzbState.getSeasonId();
            kfzbSignObj2.competitorId = cId;
            KfzbManager.playerSignMap.put(kfzbSignObj2.playerId, kfzbSignObj2);
            final Tuple<List<Integer>, Integer> tuple = new Tuple();
            tuple.left = new LinkedList();
            tuple.right = 0;
            KfzbManager.playerTicketsMap.put(player.getPlayerId(), tuple);
        }
        resultTuple.left = true;
        resultTuple.right = "";
        return resultTuple;
    }
    
    private String getDefaultGIds(final int playerId) {
        final List<PlayerGeneralMilitary> list = this.dataGetter.getPlayerGeneralMilitaryDao().getMilitaryList(playerId);
        final StringBuilder sb = new StringBuilder();
        for (final PlayerGeneralMilitary pg : list) {
            sb.append(pg.getGeneralId());
            sb.append("#");
        }
        final String gIds = sb.toString();
        return gIds;
    }
    
    private boolean IsgIdsCorrect(final int playerId, final String gIds) {
        try {
            final String[] gds = gIds.split("#");
            final List<PlayerGeneralMilitary> list = this.dataGetter.getPlayerGeneralMilitaryDao().getMilitaryList(playerId);
            final Set<Integer> set = new HashSet<Integer>();
            for (final PlayerGeneralMilitary pg : list) {
                set.add(pg.getGeneralId());
            }
            final Set<Integer> gIdset = new HashSet<Integer>();
            String[] array;
            for (int length = (array = gds).length, i = 0; i < length; ++i) {
                final String gId = array[i];
                final Integer id = Integer.parseInt(gId);
                if (gIdset.contains(id)) {
                    return false;
                }
                gIdset.add(id);
                if (!set.contains(id)) {
                    return false;
                }
            }
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public byte[] synData(final PlayerDto playerDto, final String gIds) {
        if (gIds == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.PLUG_IS_SHAMEFUL);
        }
        if (!this.IsgIdsCorrect(playerDto.playerId, gIds)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.PLUG_IS_SHAMEFUL);
        }
        final List<PlayerGeneralMilitary> pgmList = this.dataGetter.getPlayerGeneralMilitaryDao().getMilitaryList(playerDto.playerId);
        final Set<Integer> gIdSet = new HashSet<Integer>();
        for (final PlayerGeneralMilitary pgm : pgmList) {
            gIdSet.add(pgm.getGeneralId());
        }
        final String[] gIdArray = gIds.split("#");
        try {
            String[] array;
            for (int length = (array = gIdArray).length, i = 0; i < length; ++i) {
                final String temp = array[i];
                final int id = Integer.parseInt(temp);
                if (!gIdSet.contains(id)) {
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.PLUG_IS_SHAMEFUL);
                }
            }
        }
        catch (Exception e) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.PLUG_IS_SHAMEFUL);
        }
        final KfzbSignObj kfzbSignObj = KfzbManager.playerSignMap.get(playerDto.playerId);
        if (kfzbSignObj == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.KF_ZB_NOT_SIGNUP);
        }
        if (this.phase1AndPhase2KickOutCIdSet.contains(kfzbSignObj.competitorId)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.KF_ZB_DEAD);
        }
        final Player player = this.dataGetter.getPlayerDao().read(playerDto.playerId);
        final Tuple<Boolean, String> result = this.self.syncData(player, gIds, false);
        if (!(boolean)result.left) {
            return JsonBuilder.getJson(State.FAIL, result.right);
        }
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Override
    public byte[] get16Table(final PlayerDto playerDto) {
        final KfzbState tempKfzbState = this.kfzbState;
        if (tempKfzbState == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.KF_ZB_NO_SEASON);
        }
        if (tempKfzbState.getGlobalState() < 30) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.KF_ZB_CANNOT_DO_THIS_NOW);
        }
        if (tempKfzbState.getGlobalState() >= 51) {
            return this.get16TablePhase2(playerDto, tempKfzbState);
        }
        final KfzbSignObj kfzbSignObj = KfzbManager.playerSignMap.get(playerDto.playerId);
        if (kfzbSignObj == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.KF_ZB_NOT_SIGNUP);
        }
        if (this.playerSaiquIdMap.size() == 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.KF_ZB_WAIT_FOR_PAISAI);
        }
        final Integer saiqu = this.playerSaiquIdMap.get(playerDto.playerId);
        if (saiqu == null) {
            ErrorSceneLog.getInstance().appendErrorMsg("saiqu == null").appendPlayerId(playerDto.playerId).appendPlayerName(playerDto.playerName).appendClassName(this.getClass().getSimpleName()).appendMethodName("get16Table").flush();
            return JsonBuilder.getJson(State.FAIL, LocalMessages.KF_ZB_DATA_ERROR_NO_SAIQU);
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        final KfzbTreasureReward kfzbTr = KfzbSeasonService.treasureRewardMap.get(5);
        final GeneralTreasure generalTreasure = (GeneralTreasure)this.generalTreasureCache.get((Object)kfzbTr.getTreasureId());
        doc.createElement("treasurePic", generalTreasure.getPic());
        doc.createElement("treasureLea", kfzbTr.getLea());
        doc.createElement("treasureStr", kfzbTr.getStr());
        doc.createElement("treasureId", generalTreasure.getId());
        doc.createElement("treasureName", generalTreasure.getName());
        doc.createElement("phase", 1);
        doc.createElement("address", this.address);
        doc.createElement("port", this.port);
        doc.createElement("cId", kfzbSignObj.competitorId);
        doc.createElement("key", KfzbCommonConstants.getKfzbKey(kfzbSignObj.competitorId, this.kfzbState.getSeasonId()));
        if (tempKfzbState.getGlobalState() >= 50) {
            doc.createElement("totalLayer", tempKfzbState.getTotalLayer());
        }
        doc.createElement("saiqu", saiqu);
        doc.createElement("layer", tempKfzbState.getLayer());
        doc.createElement("cd", 0);
        final Date day2ShowDate = this.kfzbSeasonService.getKfzbSeasonInfo().getDay2showBattleTime();
        doc.createElement("day2Cd", day2ShowDate.getTime() - System.currentTimeMillis());
        if (tempKfzbState.getGlobalState() < 50) {
            doc.createElement("flag", 1);
        }
        else {
            final Integer cId = KfzbManager.playerIdCIdMap.get(playerDto.playerId);
            if (this.phase1AndPhase2KickOutCIdSet.contains(cId)) {
                doc.createElement("flag", 3);
            }
            else {
                doc.createElement("flag", 2);
                doc.createElement("baowu", 1);
            }
            int tickets = 0;
            final Tuple<List<Integer>, Integer> tuple = KfzbManager.playerTicketsMap.get(playerDto.playerId);
            if (tuple == null) {
                tickets = 0;
            }
            else {
                int sum = 0;
                final int start = tuple.right;
                for (int end = ((List)tuple.left).size(), i = start; i < end; ++i) {
                    sum += ((List)tuple.left).get(i);
                }
                tickets = sum;
            }
            doc.createElement("tickets", tickets);
        }
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    private byte[] get16TablePhase2(final PlayerDto playerDto, final KfzbState tempKfzbState) {
        if (playerDto.playerLv < this.kfzbSeasonService.getKfzbSeasonInfo().getSupLv()) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.KF_ZB_LV_NOT_ENOUGH);
        }
        final int seasonId = tempKfzbState.getSeasonId();
        if (tempKfzbState.getPhase2Info() == null || tempKfzbState.getPhase2Info().getMap() == null || tempKfzbState.getPhase2Info().getMap().size() == 0) {
            ErrorSceneLog.getInstance().appendErrorMsg("\u540e\u7aef\u6570\u636e\u53d1\u751f\u9519\u8bef,\u6ca1\u6709\u8f6e\u8be2\u5230\u5bf9\u9635\u4fe1\u606f").appendMethodName("get16Table").flush();
            return JsonBuilder.getJson(State.FAIL, LocalMessages.KF_ZB_DATA_ERROR_NO_DUIZHEN);
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("phase", 2);
        doc.createElement("nextGlobalStateCD", tempKfzbState.getNextGlobalStateCD());
        final int currentLayer = tempKfzbState.getPhase2Info().getCurLayer();
        doc.createElement("curLayer", currentLayer);
        doc.createElement("curRound", tempKfzbState.getPhase2Info().getCurRound());
        final KfzbTreasureReward kfzbTr = KfzbSeasonService.treasureRewardMap.get(currentLayer);
        final GeneralTreasure generalTreasure = (GeneralTreasure)this.generalTreasureCache.get((Object)kfzbTr.getTreasureId());
        doc.createElement("treasurePic", generalTreasure.getPic());
        doc.createElement("treasureLea", kfzbTr.getLea());
        doc.createElement("treasureStr", kfzbTr.getStr());
        doc.createElement("treasureId", generalTreasure.getId());
        doc.createElement("treasureName", generalTreasure.getName());
        if (tempKfzbState.getBattleTime() != null) {
            doc.createElement("battleTime", tempKfzbState.getBattleTime().getTime() - System.currentTimeMillis());
        }
        doc.createElement("nextLayer", tempKfzbState.getNextLayer());
        doc.createElement("nextRound", tempKfzbState.getNextRound());
        if (tempKfzbState.getNextBatTime() != null) {
            doc.createElement("nextBatTime", tempKfzbState.getNextBatTime().getTime() - System.currentTimeMillis());
        }
        if (tempKfzbState.getNextRBegTime() != null) {
            doc.createElement("nextRBegTime", tempKfzbState.getNextRBegTime().getTime() - System.currentTimeMillis());
        }
        int tickets = 0;
        final Tuple<List<Integer>, Integer> tuple = KfzbManager.playerTicketsMap.get(playerDto.playerId);
        if (tuple == null) {
            tickets = 0;
        }
        else {
            for (int i = tuple.right; i < ((List)tuple.left).size(); ++i) {
                tickets += ((List)tuple.left).get(i);
            }
        }
        doc.createElement("tickets", tickets);
        final List<KfzbSupport> untaListket = this.dataGetter.getKfzbSupportDao().getUnTakedSupportInfo(playerDto.playerId, tempKfzbState.getSeasonId());
        int supticket = 0;
        for (final KfzbSupport sup : untaListket) {
            ++supticket;
        }
        doc.createElement("suptickets", supticket);
        doc.startArray("Infos");
        boolean allBattleFinished = false;
        for (final KfzbBattleInfo kfzbBattleInfo : tempKfzbState.getPhase2Info().getMap().values()) {
            doc.startObject();
            final int matchId = kfzbBattleInfo.getMatchId();
            final int p1CId = kfzbBattleInfo.getP1().getCompetitorId();
            final int p2CId = kfzbBattleInfo.getP2().getCompetitorId();
            final int round = kfzbBattleInfo.getRound();
            KfzbSupport kfzbSupport = null;
            int supSucNum = 0;
            if (matchId >= 4) {
                kfzbSupport = this.dataGetter.getKfzbSupportDao().getByUniqIndex(playerDto.playerId, tempKfzbState.getSeasonId(), matchId, 0);
                if (kfzbSupport != null && kfzbSupport.getRewarded() == 1) {
                    ++supSucNum;
                }
            }
            else {
                final List<KfzbSupport> kfzbSupportList = this.dataGetter.getKfzbSupportDao().getByMatchId(playerDto.playerId, tempKfzbState.getSeasonId(), matchId);
                for (final KfzbSupport sup2 : kfzbSupportList) {
                    if (sup2.getRoundId() == round) {
                        kfzbSupport = sup2;
                    }
                    if (sup2.getRewarded() == 1) {
                        ++supSucNum;
                    }
                }
            }
            if (kfzbSupport != null) {
                if (kfzbSupport.getCId().equals(p1CId)) {
                    doc.createElement("guanzhu1", true);
                }
                else if (kfzbSupport.getCId().equals(p2CId)) {
                    doc.createElement("guanzhu2", true);
                }
            }
            doc.createElement("supSucNum", supSucNum);
            doc.createElement("matchId", matchId);
            doc.createElement("layerRound", kfzbBattleInfo.getLayerRound());
            doc.createElement("win1", kfzbBattleInfo.getP1Win());
            doc.createElement("win2", kfzbBattleInfo.getP2Win());
            doc.createElement("name1", kfzbBattleInfo.getP1().getPlayerName());
            doc.createElement("pic1", kfzbBattleInfo.getP1().getPic());
            doc.createElement("force1", kfzbBattleInfo.getP1().getNation());
            doc.createElement("server1", kfzbBattleInfo.getP1().getServerName());
            doc.createElement("serverId1", kfzbBattleInfo.getP1().getServerId());
            doc.createElement("lv1", kfzbBattleInfo.getP1().getPlayerLevel());
            doc.createElement("sup1", kfzbBattleInfo.getSup1());
            doc.createElement("name2", kfzbBattleInfo.getP2().getPlayerName());
            doc.createElement("pic2", kfzbBattleInfo.getP2().getPic());
            doc.createElement("force2", kfzbBattleInfo.getP2().getNation());
            doc.createElement("server2", kfzbBattleInfo.getP2().getServerName());
            doc.createElement("serverId2", kfzbBattleInfo.getP2().getServerId());
            doc.createElement("lv2", kfzbBattleInfo.getP2().getPlayerLevel());
            doc.createElement("sup2", kfzbBattleInfo.getSup2());
            if (matchId == 1 && kfzbBattleInfo.isFinish()) {
                allBattleFinished = true;
            }
            doc.endObject();
        }
        doc.endArray();
        final KfzbInfo KfzbInfo = this.getPlayerKfzbInfoWithCheck(playerDto, seasonId);
        if (KfzbInfo != null) {
            if (currentLayer > 2) {
                doc.createElement("supportNum", KfzbInfo.getFlower1());
            }
            else {
                doc.createElement("supportNum", KfzbInfo.getFlower2());
            }
        }
        final KfzbSignObj kfzbSignObj = KfzbManager.playerSignMap.get(playerDto.playerId);
        if (kfzbSignObj != null && this.phase2InbattleCIdSet.contains(kfzbSignObj.competitorId)) {
            doc.createElement("selfState", 1);
            doc.createElement("address", this.address);
            doc.createElement("port", this.port);
            doc.createElement("cId", kfzbSignObj.competitorId);
            doc.createElement("key", KfzbCommonConstants.getKfzbKey(kfzbSignObj.competitorId, this.kfzbState.getSeasonId()));
        }
        else {
            doc.createElement("selfState", 2);
        }
        if (allBattleFinished) {
            final KfzbSeasonInfo currSeasonInfo = this.kfzbSeasonService.getKfzbSeasonInfo();
            if (currSeasonInfo != null && currSeasonInfo.getDay3BattleTime() != null) {
                final Date day3Battle = currSeasonInfo.getDay3BattleTime();
                final Calendar newBeginTime = Calendar.getInstance();
                newBeginTime.setTime(day3Battle);
                newBeginTime.set(11, 0);
                newBeginTime.set(12, 0);
                newBeginTime.set(13, 0);
                newBeginTime.add(6, 1);
                long feastCd = newBeginTime.getTime().getTime() - System.currentTimeMillis();
                feastCd = ((feastCd < 0L) ? 0L : feastCd);
                doc.createElement("feastCd", feastCd);
            }
        }
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public byte[] getSupportPanel(final PlayerDto playerDto, final Integer matchId) {
        final KfzbState tempKfzbState = this.kfzbState;
        if (tempKfzbState == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.KF_ZB_NO_SEASON);
        }
        if (playerDto.playerLv < this.kfzbSeasonService.getKfzbSeasonInfo().getSupLv()) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.KF_ZB_LV_NOT_ENOUGH);
        }
        final int globalState = tempKfzbState.getGlobalState();
        if (globalState < 51) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.KF_ZB_CANNOT_DO_THIS_NOW);
        }
        if (globalState > 70) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.KF_ZB_CANNOT_DO_THIS_NOW);
        }
        final Integer selfCId = KfzbManager.playerIdCIdMap.get(playerDto.playerId);
        if (selfCId != null && this.phase2InbattleCIdSet.contains(selfCId)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.KF_ZB_ALIVE_CANNOT_GUANKAN);
        }
        final KfzbBattleInfo kfzbBattleInfo = this.kfzbBattleInfoMap.get(matchId);
        if (kfzbBattleInfo == null) {
            return JsonBuilder.getJson(State.FAIL, String.valueOf(LocalMessages.KF_ZB_NO_MATCH_INFO) + ",matchId:" + matchId);
        }
        if (kfzbBattleInfo.isFinish()) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.KF_ZB_MATCH_ENDED);
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        if (tempKfzbState.getGlobalState() >= 61) {
            doc.createElement("buySupGold", 2);
        }
        else {
            doc.createElement("buySupGold", 1);
        }
        doc.createElement("matchId", matchId);
        doc.createElement("terrain", kfzbBattleInfo.getTerrain());
        doc.createElement("round", kfzbBattleInfo.getRound());
        doc.createElement("nextCd", kfzbBattleInfo.getBattleTime().getTime() - System.currentTimeMillis());
        final int round = kfzbBattleInfo.getRound();
        KfzbSupport kfzbSupport = null;
        if (matchId > 3) {
            kfzbSupport = this.dataGetter.getKfzbSupportDao().getByUniqIndex(playerDto.playerId, tempKfzbState.getSeasonId(), matchId, 0);
        }
        else {
            kfzbSupport = this.dataGetter.getKfzbSupportDao().getByUniqIndex(playerDto.playerId, tempKfzbState.getSeasonId(), matchId, round);
        }
        final boolean needChange = kfzbBattleInfo.isNeedChange();
        if (kfzbSupport == null) {
            doc.createElement("supported", 0);
            if (needChange) {
                doc.createElement("sup1", kfzbBattleInfo.getSup2());
                doc.createElement("sup2", kfzbBattleInfo.getSup1());
            }
            else {
                doc.createElement("sup1", kfzbBattleInfo.getSup1());
                doc.createElement("sup2", kfzbBattleInfo.getSup2());
            }
            final KfzbInfo KfzbInfo = this.getPlayerKfzbInfoWithCheck(playerDto, tempKfzbState.getSeasonId());
            if (KfzbInfo != null) {
                final int currentLayer = tempKfzbState.getPhase2Info().getCurLayer();
                if (currentLayer > 2) {
                    doc.createElement("leftNum", KfzbInfo.getFlower1());
                }
                else {
                    doc.createElement("leftNum", KfzbInfo.getFlower2());
                }
            }
        }
        else {
            if (kfzbBattleInfo.getP1().getCompetitorId().equals(kfzbSupport.getCId())) {
                if (needChange) {
                    doc.createElement("supported", 2);
                }
                else {
                    doc.createElement("supported", 1);
                }
            }
            else if (kfzbBattleInfo.getP2().getCompetitorId().equals(kfzbSupport.getCId())) {
                if (needChange) {
                    doc.createElement("supported", 1);
                }
                else {
                    doc.createElement("supported", 2);
                }
            }
            doc.createElement("tickets", 1);
        }
        KfzbPlayerInfo kfzbPlayerInfo = null;
        KfwdSimpleGInfo[] list = null;
        for (int i = 1; i <= 2; ++i) {
            int winNum = 0;
            if ((i == 1 && !needChange) || (i == 2 && needChange)) {
                kfzbPlayerInfo = kfzbBattleInfo.getP1();
                list = kfzbBattleInfo.getG1().getList();
                winNum = kfzbBattleInfo.getP1Win();
            }
            else {
                kfzbPlayerInfo = kfzbBattleInfo.getP2();
                list = kfzbBattleInfo.getG2().getList();
                winNum = kfzbBattleInfo.getP2Win();
            }
            doc.createElement("cId" + i, kfzbPlayerInfo.getCompetitorId());
            doc.createElement("Lv" + i, kfzbPlayerInfo.getPlayerLevel());
            doc.createElement("name" + i, kfzbPlayerInfo.getPlayerName());
            doc.createElement("pic" + i, kfzbPlayerInfo.getPic());
            doc.createElement("win" + i, winNum);
            doc.createElement("force" + i, kfzbPlayerInfo.getNation());
            doc.createElement("serverName" + i, kfzbPlayerInfo.getServerName());
            doc.createElement("serverId" + i, kfzbPlayerInfo.getServerId());
            doc.startArray("generals" + i);
            KfwdSimpleGInfo[] array;
            for (int length = (array = list).length, j = 0; j < length; ++j) {
                final KfwdSimpleGInfo kfwdSimpleGInfo = array[j];
                doc.startObject();
                doc.createElement("generalId", kfwdSimpleGInfo.getGeneralId());
                doc.createElement("generalName", kfwdSimpleGInfo.getGeneralName());
                doc.createElement("generalLv", kfwdSimpleGInfo.getGeneralLv());
                doc.createElement("generalPic", kfwdSimpleGInfo.getGeneralPic());
                doc.createElement("quality", kfwdSimpleGInfo.getQuality());
                doc.createElement("troopId", kfwdSimpleGInfo.getTroopId());
                doc.createElement("troopType", kfwdSimpleGInfo.getTroopType());
                doc.createElement("armyHp", kfwdSimpleGInfo.getArmyHp());
                doc.createElement("armyHpMax", kfwdSimpleGInfo.getArmyHpMax());
                doc.createElement("tacticName", kfwdSimpleGInfo.getTacticName());
                doc.endObject();
            }
            doc.endArray();
        }
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public byte[] support(final PlayerDto playerDto, final int matchId, final Integer cId) {
        final KfzbState tempKfzbState = this.kfzbState;
        if (tempKfzbState == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.KF_ZB_NO_SEASON);
        }
        final int globalState = tempKfzbState.getGlobalState();
        if (playerDto.playerLv < this.kfzbSeasonService.getKfzbSeasonInfo().getSupLv()) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.KF_ZB_LV_NOT_ENOUGH);
        }
        if (globalState < 51) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.KF_ZB_CANNOT_DO_THIS_NOW);
        }
        if (globalState > 70) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.KF_ZB_CANNOT_DO_THIS_NOW);
        }
        final Integer selfCId = KfzbManager.playerIdCIdMap.get(playerDto.playerId);
        if (selfCId != null && this.phase2InbattleCIdSet.contains(selfCId)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.KF_ZB_ALIVE_CANNOT_GUANKAN);
        }
        final KfzbInfo kfzbInfo = this.getPlayerKfzbInfoWithCheck(playerDto, tempKfzbState.getSeasonId());
        if (kfzbInfo == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.KF_ZB_LV_NOT_ENOUGH);
        }
        if (!this.phase2InbattleCIdSet.contains(cId)) {
            return JsonBuilder.getJson(State.FAIL, String.valueOf(LocalMessages.KF_ZB_DEAD_CANNOT_SUPPORT) + ",cId:" + cId);
        }
        final KfzbBattleInfo kfzbBattleInfo = this.kfzbBattleInfoMap.get(matchId);
        if (kfzbBattleInfo == null) {
            return JsonBuilder.getJson(State.FAIL, String.valueOf(LocalMessages.KF_ZB_NO_MATCH_INFO) + ",matchId:" + matchId);
        }
        if (kfzbBattleInfo.isFinish()) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.KF_ZB_MATCH_ENDED);
        }
        if (kfzbBattleInfo.getP1().getCompetitorId() != cId && kfzbBattleInfo.getP2().getCompetitorId() != cId) {
            return JsonBuilder.getJson(State.FAIL, "supId not correct");
        }
        final int round = kfzbBattleInfo.getRound();
        int supTickets = 0;
        final KfzbBattleReport kfzbBattleReport = this.kfzbBattleReportMap.get(matchId);
        if (kfzbBattleReport == null) {
            supTickets = 400;
        }
        else {
            final int size = kfzbBattleReport.getList().size();
            final FrameBattleReport frameBattleReport = kfzbBattleReport.getList().get(size - 1);
            if (matchId > 3) {
                if (frameBattleReport.getState() == 2) {
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.KF_ZB_CANNOT_SUPPORT_ALREADY);
                }
                supTickets = 200;
            }
            else if (frameBattleReport.isEnd()) {
                supTickets = 400;
            }
            else {
                if (frameBattleReport.getState() == 2) {
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.KF_ZB_CANNOT_SUPPORT_ALREADY);
                }
                supTickets = 200;
            }
        }
        final int playerId = playerDto.playerId;
        final int seasonId = tempKfzbState.getSeasonId();
        int flower = 0;
        KfzbSupport kfzbSupport = null;
        if (matchId > 3) {
            kfzbSupport = this.dataGetter.getKfzbSupportDao().getByUniqIndex(playerDto.playerId, tempKfzbState.getSeasonId(), matchId, 0);
            if (kfzbSupport != null) {
                return JsonBuilder.getJson(State.FAIL, String.valueOf(LocalMessages.KF_ZB_SUPPORTED_ALREADY) + ",matchId:" + matchId);
            }
            flower = kfzbInfo.getFlower1();
            if (flower <= 0) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.KF_ZB_NO_FLOWER);
            }
            this.dataGetter.getKfzbInfoDao().updateSupport1DecreaseFlower1(playerId, seasonId, "");
            kfzbSupport = new KfzbSupport();
            kfzbSupport.setPlayerId(playerId);
            kfzbSupport.setSeasonId(seasonId);
            kfzbSupport.setMatchId(matchId);
            kfzbSupport.setRoundId(0);
            kfzbSupport.setCId(cId);
            kfzbSupport.setTickets(supTickets);
            kfzbSupport.setRewarded(0);
            kfzbSupport.setTakeIt(0);
            this.dataGetter.getKfzbSupportDao().create(kfzbSupport);
        }
        else {
            kfzbSupport = this.dataGetter.getKfzbSupportDao().getByUniqIndex(playerDto.playerId, tempKfzbState.getSeasonId(), matchId, round);
            if (kfzbSupport != null) {
                return JsonBuilder.getJson(State.FAIL, String.valueOf(LocalMessages.KF_ZB_SUPPORTED_ALREADY) + ",matchId:" + matchId);
            }
            flower = kfzbInfo.getFlower2();
            if (flower <= 0) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.KF_ZB_NO_FLOWER);
            }
            this.dataGetter.getKfzbInfoDao().updateSupport2DecreaseFlower2(playerId, seasonId, "");
            kfzbSupport = new KfzbSupport();
            kfzbSupport.setPlayerId(playerId);
            kfzbSupport.setSeasonId(seasonId);
            kfzbSupport.setMatchId(matchId);
            kfzbSupport.setRoundId(round);
            kfzbSupport.setCId(cId);
            kfzbSupport.setTickets(supTickets);
            kfzbSupport.setRewarded(0);
            kfzbSupport.setTakeIt(0);
            this.dataGetter.getKfzbSupportDao().create(kfzbSupport);
        }
        synchronized (this.supportList) {
            KfzbRTSupport targetKfzbRTSupport = null;
            for (final KfzbRTSupport kfzbRTSupport : this.supportList) {
                if (kfzbRTSupport.getSeasonId() == tempKfzbState.getSeasonId() && kfzbRTSupport.getMatchId() == matchId && kfzbRTSupport.getcId() == cId) {
                    targetKfzbRTSupport = kfzbRTSupport;
                    break;
                }
            }
            if (targetKfzbRTSupport != null) {
                targetKfzbRTSupport.setSupAdd(targetKfzbRTSupport.getSupAdd() + 1);
            }
            else {
                targetKfzbRTSupport = new KfzbRTSupport();
                targetKfzbRTSupport.setSeasonId(tempKfzbState.getSeasonId());
                targetKfzbRTSupport.setMatchId(matchId);
                targetKfzbRTSupport.setcId(cId);
                targetKfzbRTSupport.setSupAdd(1);
                this.supportList.add(targetKfzbRTSupport);
            }
        }
        // monitorexit(this.supportList)
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Override
    public byte[] viewBattle(final PlayerDto playerDto, final Integer matchId, final Integer getRound) {
        if (playerDto.playerLv < this.kfzbSeasonService.getKfzbSeasonInfo().getSupLv()) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.KF_ZB_LV_NOT_ENOUGH);
        }
        final KfzbBattleReport kfzbBattleReport = this.kfzbBattleReportMap.get(matchId);
        if (kfzbBattleReport == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.KF_ZB_NO_MATCH_INFO);
        }
        if (kfzbBattleReport.getRound() != getRound) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.KF_ZB_NO_MATCH_INFO);
        }
        final Integer oldMatchId = this.playerViewMap.get(playerDto.playerId);
        if (oldMatchId != null) {
            final Set<Integer> oldPlayersSet = this.matchViewPlayersMap.get(oldMatchId);
            if (oldPlayersSet != null) {
                synchronized (oldPlayersSet) {
                    oldPlayersSet.remove(playerDto.playerId);
                }
                // monitorexit(oldPlayersSet)
            }
        }
        Set<Integer> newPlayersSet = this.matchViewPlayersMap.get(matchId);
        if (newPlayersSet == null) {
            newPlayersSet = new HashSet<Integer>();
            this.matchViewPlayersMap.put(matchId, newPlayersSet);
        }
        synchronized (newPlayersSet) {
            newPlayersSet.add(playerDto.playerId);
        }
        this.playerViewMap.put(playerDto.playerId, matchId);
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        final int index = kfzbBattleReport.getList().size() - 1;
        final FrameBattleReport frameBattleReport = kfzbBattleReport.getList().get(index);
        if (frameBattleReport.isEnd()) {
            doc.createElement("nextRoundCD", frameBattleReport.getNextRoundTime().getTime() - System.currentTimeMillis());
        }
        doc.createElement("report", frameBattleReport.getIniReport());
        doc.createElement("plus", frameBattleReport.getBattleReport());
        final KfzbBattleInfo kfzbBattleInfo = this.kfzbBattleInfoMap.get(matchId);
        if (kfzbBattleInfo != null) {
            final int seasonId = this.kfzbState.getSeasonId();
            final int round = kfzbBattleInfo.getRound();
            KfzbSupport kfzbSupport = null;
            final int currentLayer = this.kfzbState.getPhase2Info().getCurLayer();
            if (matchId > 3) {
                kfzbSupport = this.dataGetter.getKfzbSupportDao().getByUniqIndex(playerDto.playerId, seasonId, matchId, 0);
            }
            else {
                kfzbSupport = this.dataGetter.getKfzbSupportDao().getByUniqIndex(playerDto.playerId, seasonId, matchId, round);
            }
            if (kfzbSupport == null) {
                int supported = 0;
                if ((currentLayer == 4 || currentLayer == 3) && round >= 2) {
                    supported = -1;
                }
                doc.createElement("supported", supported);
                doc.createElement("sup1", kfzbBattleInfo.getSup1());
                doc.createElement("sup2", kfzbBattleInfo.getSup2());
                final KfzbInfo KfzbInfo = this.getPlayerKfzbInfoWithCheck(playerDto, seasonId);
                if (KfzbInfo != null) {
                    if (currentLayer > 2) {
                        doc.createElement("leftNum", KfzbInfo.getFlower1());
                    }
                    else {
                        doc.createElement("leftNum", KfzbInfo.getFlower2());
                    }
                }
            }
            else if (kfzbBattleInfo.getP1().getCompetitorId().equals(kfzbSupport.getCId())) {
                doc.createElement("supported", 1);
            }
            else if (kfzbBattleInfo.getP2().getCompetitorId().equals(kfzbSupport.getCId())) {
                doc.createElement("supported", 2);
            }
        }
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Transactional
    @Override
    public byte[] getTickets(final PlayerDto playerDto) {
        final KfzbState tempKfzbState = this.kfzbState;
        if (tempKfzbState == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.KF_ZB_NO_SEASON);
        }
        final int playerId = playerDto.playerId;
        final Tuple<List<Integer>, Integer> tuple = KfzbManager.playerTicketsMap.get(playerId);
        if (tuple == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.KF_ZB_NOT_SIGNUP);
        }
        if (tuple.right >= ((List)tuple.left).size()) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.KF_ZB_NO_TICKETS);
        }
        KfzbMatchService.getTicketLock.readLock().lock();
        try {
            int sum = 0;
            synchronized (tuple) {
                final int start = tuple.right;
                final int end = ((List)tuple.left).size();
                for (int i = start; i < end; ++i) {
                    sum += ((List)tuple.left).get(i);
                }
                if (sum <= 0) {
                    // monitorexit(tuple)
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.KF_ZB_NO_TICKETS);
                }
                this.dataGetter.getPlayerTicketsDao().addTickets(playerId, sum, "\u8de8\u670d\u4e89\u9738\u8d5b\u624b\u52a8\u9886\u53d6\u83b7\u5f97\u70b9\u5238" + start + "-" + end, false);
                final int seasonId = tempKfzbState.getSeasonId();
                this.dataGetter.getKfzbRewardDao().updateDoneNum(playerId, seasonId, end);
                tuple.right = end;
            }
            // monitorexit(tuple)
            final JsonDocument doc = new JsonDocument();
            doc.startObject();
            doc.createElement("tickets", sum);
            doc.endObject();
            return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
        }
        finally {
            KfzbMatchService.getTicketLock.readLock().unlock();
        }
    }
    
    @Transactional
    @Override
    public byte[] getSupTickets(final PlayerDto playerDto) {
        final KfzbState tempKfzbState = this.kfzbState;
        if (tempKfzbState == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.KF_ZB_NO_SEASON);
        }
        final int playerId = playerDto.playerId;
        int totalcard = 0;
        try {
            KfzbMatchService.getSupLock.readLock().lock();
            final List<KfzbSupport> supList = this.dataGetter.getKfzbSupportDao().getUnTakedSupportInfo(playerId, this.kfzbState.getSeasonId());
            final Map<Integer, Integer> playerCardMap = new HashMap<Integer, Integer>();
            for (final KfzbSupport sup : supList) {
                Integer card = playerCardMap.get(sup.getPlayerId());
                if (card == null) {
                    card = 0;
                }
                ++card;
                this.dataGetter.getKfzbSupportDao().updateTaketIt(sup);
                playerCardMap.put(sup.getPlayerId(), card);
            }
            for (final Map.Entry<Integer, Integer> entry : playerCardMap.entrySet()) {
                final int pId = entry.getKey();
                totalcard = entry.getValue();
                KfzbMatchService.kfzbLogger.info("addSupCard=" + pId + "-" + totalcard);
                this.kfzbFeastService.addFreeCard(playerId, totalcard);
            }
            if (totalcard <= 0) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.KF_ZB_NO_TICKETS);
            }
            final JsonDocument doc = new JsonDocument();
            doc.startObject();
            doc.createElement("tickets", totalcard);
            doc.endObject();
            return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
        }
        finally {
            KfzbMatchService.getSupLock.readLock().unlock();
        }
    }
    
    @Override
    public byte[] buyFlower(final PlayerDto playerDto) {
        final KfzbState tempKfzbState = this.kfzbState;
        if (tempKfzbState == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.KF_ZB_NO_SEASON);
        }
        final Player player = this.dataGetter.getPlayerDao().read(playerDto.playerId);
        if (player == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.PLUG_IS_SHAMEFUL);
        }
        final KfzbInfo KfzbInfo = this.getPlayerKfzbInfoWithCheck(playerDto, tempKfzbState.getSeasonId());
        if (KfzbInfo == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.KF_ZB_LV_NOT_ENOUGH);
        }
        if (tempKfzbState.getGlobalState() >= 61) {
            return this.buyFlower2(player, KfzbInfo);
        }
        return this.buyFlower1(player, KfzbInfo);
    }
    
    private byte[] buyFlower1(final Player player, final KfzbInfo KfzbInfo) {
        if (KfzbInfo.getFlower1Buy() >= 4) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.KF_ZB_REACH_BUY_LIMIT);
        }
        final int gold = 1;
        if (!this.dataGetter.getPlayerDao().consumeGold(player, gold, "\u8de8\u670d\u4e89\u9738\u8d5b\u8d2d\u4e70\u82b1\u6735\u6d88\u8017\u91d1\u5e01")) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10003);
        }
        final int oldFlower = KfzbInfo.getFlower1();
        final int currentLayer = this.kfzbState.getPhase2Info().getCurLayer();
        final int currentRound = this.kfzbState.getPhase2Info().getCurRound();
        final int num = this.dataGetter.getKfzbSupportDao().getPlayerByLayerAndRoundNum(this.kfzbState.getSeasonId(), player.getPlayerId(), currentLayer, 0);
        int totalFlowerNeed = 0;
        if (currentLayer == 3) {
            if (currentRound < 2) {
                totalFlowerNeed = 4 - num;
            }
        }
        else if (currentLayer == 4) {
            if (currentRound >= 2) {
                totalFlowerNeed = 4;
            }
            else {
                totalFlowerNeed = 12 - num;
            }
        }
        if (oldFlower >= totalFlowerNeed) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.KF_ZB_ENOUPH_FLOWER);
        }
        this.dataGetter.getKfzbInfoDao().buyFlower1(KfzbInfo.getPlayerId(), KfzbInfo.getSeasonId());
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    private byte[] buyFlower2(final Player player, final KfzbInfo KfzbInfo) {
        if (KfzbInfo.getFlower2Buy() >= 5) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.KF_ZB_REACH_BUY_LIMIT);
        }
        final int oldFlower = KfzbInfo.getFlower2();
        final int currentLayer = this.kfzbState.getPhase2Info().getCurLayer();
        final int currentRound = this.kfzbState.getPhase2Info().getCurRound();
        final int num = this.dataGetter.getKfzbSupportDao().getPlayerByLayerAndRoundNum(this.kfzbState.getSeasonId(), player.getPlayerId(), currentLayer, currentRound);
        int totalFlowerNeed = 0;
        if (currentLayer == 1) {
            totalFlowerNeed = KfzbCommonConstants.LAYERROUNDINFO[1] - currentRound + 1 - num;
        }
        else {
            totalFlowerNeed = (KfzbCommonConstants.LAYERROUNDINFO[2] - currentRound + 1) * 2 + KfzbCommonConstants.LAYERROUNDINFO[1] - num;
        }
        if (oldFlower >= totalFlowerNeed) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.KF_ZB_ENOUPH_FLOWER);
        }
        final int gold = 2;
        if (!this.dataGetter.getPlayerDao().consumeGold(player, gold, "\u8de8\u670d\u4e89\u9738\u8d5b\u8d2d\u4e70\u82b1\u6735\u6d88\u8017\u91d1\u5e01")) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10003);
        }
        this.dataGetter.getKfzbInfoDao().buyFlower2(KfzbInfo.getPlayerId(), KfzbInfo.getSeasonId());
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    private KfzbInfo getPlayerKfzbInfoWithCheck(final PlayerDto playerDto, final int seasonId) {
        KfzbInfo kfzbInfo = this.dataGetter.getKfzbInfoDao().getByPlayerIdSeasonId(playerDto.playerId, seasonId);
        if (kfzbInfo != null) {
            return kfzbInfo;
        }
        if (playerDto.playerLv < this.kfzbSeasonService.getKfzbSeasonInfo().getSupLv()) {
            return null;
        }
        final Integer selfCId = KfzbManager.playerIdCIdMap.get(playerDto.playerId);
        if (selfCId != null && this.phase2InbattleCIdSet.contains(selfCId)) {
            return null;
        }
        kfzbInfo = new KfzbInfo();
        kfzbInfo.setPlayerId(playerDto.playerId);
        kfzbInfo.setSeasonId(seasonId);
        kfzbInfo.setFlower1(8);
        kfzbInfo.setFlower1Buy(0);
        kfzbInfo.setSupport1Info(null);
        kfzbInfo.setFlower2(6);
        kfzbInfo.setFlower2Buy(0);
        kfzbInfo.setSupport2Info(null);
        this.dataGetter.getKfzbInfoDao().create(kfzbInfo);
        return kfzbInfo;
    }
    
    static /* synthetic */ void access$5(final KfzbMatchService kfzbMatchService, final boolean cacheSuccHandled) {
        kfzbMatchService.cacheSuccHandled = cacheSuccHandled;
    }
    
    private class MatchStateThread extends Thread
    {
        public MatchStateThread() {
            super("KfzbMatchService-MatchStateThread");
        }
        
        @Override
        public void run() {
            while (KfzbMatchService.alive1) {
                try {
                    final KfzbState newKfzbState = KfzbMatchService.this.requestKfzbStateFromMatch();
                    if (KfzbMatchService.this.kfzbState != null && KfzbMatchService.this.kfzbState.getGlobalState() >= 50 && KfzbMatchService.this.kfzbState.getGlobalState() <= 70) {
                        final boolean needRequestReward = true;
                        if (needRequestReward) {
                            KfzbMatchService.this.requestKfzbRewardFromMatchAndHandle();
                        }
                        if (KfzbMatchService.this.kfzbState != null) {
                            if (KfzbMatchService.this.kfzbState.getLayer() != newKfzbState.getLayer()) {
                                try {
                                    KfzbMatchService.this.sendAllLostPersonReward(KfzbMatchService.this.kfzbState.getSeasonId());
                                }
                                catch (Exception e) {
                                    KfzbMatchService.kfzbLogger.error("", e);
                                    ErrorSceneLog.getInstance().error("", e);
                                }
                                try {
                                    if (newKfzbState.getLayer() <= 4) {
                                        KfzbMatchService.this.sendAllLastLayerReward(KfzbMatchService.this.kfzbState.getSeasonId(), newKfzbState.getLayer());
                                    }
                                }
                                catch (Exception e) {
                                    KfzbMatchService.kfzbLogger.error("", e);
                                    ErrorSceneLog.getInstance().error("", e);
                                }
                            }
                            KfzbMatchService.this.doSendSupNotice(KfzbMatchService.this.kfzbState, newKfzbState);
                        }
                    }
                    if (newKfzbState != null && newKfzbState.getGlobalState() >= 20) {
                        if (!KfzbMatchService.this.cacheSuccHandled) {
                            KfzbMatchService.this.handleAllCache(newKfzbState);
                            KfzbMatchService.access$5(KfzbMatchService.this, true);
                        }
                        else {
                            KfzbMatchService.this.handleNewlyKfzbState(newKfzbState);
                        }
                    }
                }
                catch (Exception e2) {
                    ErrorSceneLog.getInstance().error(String.valueOf(this.getClass().getSimpleName()) + " run catch Exception", e2);
                    continue;
                }
                finally {
                    try {
                        Thread.sleep(2000L);
                    }
                    catch (InterruptedException e3) {
                        ErrorSceneLog.getInstance().error(String.valueOf(this.getClass().getSimpleName()) + " run.sleep catch Exception", e3);
                    }
                    ThreadLocalFactory.clearTreadLocalLog();
                    ThreadLocalFactory.getTreadLocalLog();
                }
                try {
                    Thread.sleep(2000L);
                }
                catch (InterruptedException e3) {
                    ErrorSceneLog.getInstance().error(String.valueOf(this.getClass().getSimpleName()) + " run.sleep catch Exception", e3);
                }
                ThreadLocalFactory.clearTreadLocalLog();
                ThreadLocalFactory.getTreadLocalLog();
            }
            KfzbMatchService.kfzbLogger.info("KfzbMatchService MatchStateThread end.");
        }
    }
    
    private class MatchReportThread extends Thread
    {
        public MatchReportThread() {
            super("KfzbMatchService-MatchReportThread");
        }
        
        @Override
        public void run() {
            while (KfzbMatchService.alive2) {
                try {
                    if (KfzbMatchService.this.kfzbState != null && KfzbMatchService.this.kfzbState.getGlobalState() >= 51 && KfzbMatchService.this.kfzbState.getGlobalState() <= 70) {
                        final List<KfzbBattleReport> reportist = KfzbMatchService.this.requestKfzbBattleReportFromMatch();
                        if (reportist != null) {
                            KfzbMatchService.this.handleNewKfzbBattleReportList(reportist);
                        }
                        KfzbMatchService.this.submitSupportAdd();
                    }
                }
                catch (Exception e) {
                    ErrorSceneLog.getInstance().error(String.valueOf(this.getClass().getSimpleName()) + " run catch Exception", e);
                    try {
                        Thread.sleep(200L);
                    }
                    catch (InterruptedException e2) {
                        ErrorSceneLog.getInstance().error(String.valueOf(this.getClass().getSimpleName()) + " run.sleep catch Exception", e2);
                    }
                    continue;
                }
                finally {
                    try {
                        Thread.sleep(200L);
                    }
                    catch (InterruptedException e2) {
                        ErrorSceneLog.getInstance().error(String.valueOf(this.getClass().getSimpleName()) + " run.sleep catch Exception", e2);
                    }
                }
                try {
                    Thread.sleep(200L);
                }
                catch (InterruptedException e2) {
                    ErrorSceneLog.getInstance().error(String.valueOf(this.getClass().getSimpleName()) + " run.sleep catch Exception", e2);
                }
            }
            KfzbMatchService.kfzbLogger.info("KfzbMatchService MatchReportThread end.");
        }
    }
}
