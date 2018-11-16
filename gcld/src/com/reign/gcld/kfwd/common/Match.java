package com.reign.gcld.kfwd.common;

import org.apache.commons.logging.*;
import com.reign.kf.comm.transfer.oio.*;
import com.reign.kf.comm.transfer.*;
import java.util.concurrent.locks.*;
import com.reign.gcld.log.*;
import com.reign.kf.comm.entity.gw.*;
import com.reign.gcld.kfwd.common.reward.*;
import com.reign.gcld.kfwd.common.condition.*;
import com.reign.gcld.kfwd.common.transferconfig.*;
import com.reign.gcld.kfwd.common.responsehandler.*;
import com.reign.gcld.kfwd.domain.*;
import com.reign.gcld.player.domain.*;
import com.reign.framework.json.*;
import com.reign.gcld.world.common.*;
import com.reign.gcld.common.event.*;
import com.reign.util.*;
import com.reign.gcld.kfwd.service.*;
import java.util.concurrent.*;
import com.reign.gcld.kfwd.common.handler.*;
import com.reign.kf.comm.protocol.*;
import com.reign.kf.comm.entity.match.*;
import com.reign.gcld.kfwd.common.runner.*;
import com.reign.kf.comm.param.match.*;
import com.reign.gcld.player.dto.*;
import com.reign.gcld.common.*;
import java.util.*;
import com.reign.gcld.sdata.domain.*;

public class Match
{
    private static final Log logger;
    private int matchId;
    private String matchTag;
    private int season;
    private int state;
    private Date signupStartTime;
    private Date signupEndTime;
    private Date matchTime;
    private String gameServerHost;
    private int gameServerPort;
    private List<IMatchCondition> conditionList;
    private int minLv;
    private int maxLv;
    private IMatchReward matchReward;
    private IDataGetter dataGetter;
    private KfConnection connection;
    private TransferConfig transferConfig;
    private ConcurrentMap<Integer, MatchAttendee> signupMap;
    private ConcurrentMap<Integer, Integer> competitorIdPlayerIdMap;
    private ConcurrentMap<Integer, MatchFight> matchFightMap;
    public ConcurrentMap<Integer, PushReportInfo> pushReportMap;
    private int turn;
    private long nextGetScheduleTimestamp;
    private List<MatchRankEntity> rankList;
    private List<MatchResultEntity> resultList;
    private final ReentrantReadWriteLock lock;
    private final Lock readLock;
    private final Lock writeLock;
    private QueryMatchReportHandler queryMatchReportHandler;
    private QueryMatchNumScheduleHandler queryMatchNumScheduleHandler;
    private QueryTurnRankHandler queryTurnRankHandler;
    
    static {
        logger = new KfwdMatchOperationLogger();
    }
    
    public Match(final SeasonInfoEntity seasonInfoEntity, final IDataGetter dataGetter) {
        this.minLv = 1;
        this.maxLv = 180;
        this.signupMap = new ConcurrentHashMap<Integer, MatchAttendee>();
        this.competitorIdPlayerIdMap = new ConcurrentHashMap<Integer, Integer>();
        this.matchFightMap = new ConcurrentHashMap<Integer, MatchFight>();
        this.pushReportMap = new ConcurrentHashMap<Integer, PushReportInfo>();
        this.nextGetScheduleTimestamp = 0L;
        this.lock = new ReentrantReadWriteLock();
        this.readLock = this.lock.readLock();
        this.writeLock = this.lock.writeLock();
        this.dataGetter = dataGetter;
        this.matchId = seasonInfoEntity.getId();
        this.matchTag = seasonInfoEntity.getTag();
        this.season = seasonInfoEntity.getSeason();
        this.state = 0;
        this.signupStartTime = seasonInfoEntity.getSignStartTime();
        this.signupEndTime = seasonInfoEntity.getSignEndTime();
        this.matchTime = seasonInfoEntity.getMatchTime();
        this.gameServerHost = seasonInfoEntity.getHost();
        this.gameServerPort = seasonInfoEntity.getPort();
        this.conditionList = MatchConditionFactory.getMatchConditionList(seasonInfoEntity.getMatchRule());
        this.matchReward = new MatchReward();
        this.turn = 0;
        this.rankList = new ArrayList<MatchRankEntity>();
        this.resultList = new ArrayList<MatchResultEntity>();
        for (final IMatchCondition matchCondition : this.conditionList) {
            if (matchCondition instanceof MatchConditionPlayerLvScope) {
                final MatchConditionPlayerLvScope matchConditionPlayerLvScope = (MatchConditionPlayerLvScope)matchCondition;
                this.minLv = matchConditionPlayerLvScope.getMinLv();
                this.maxLv = matchConditionPlayerLvScope.getMaxLv();
            }
        }
    }
    
    public int getChangeRewardModeGold(final int rewardMode) {
        if (rewardMode == 0) {
            return 10;
        }
        if (rewardMode == 1) {
            return 20;
        }
        return Integer.MAX_VALUE;
    }
    
    public int getMinLv() {
        return this.minLv;
    }
    
    public int getMaxLv() {
        return this.maxLv;
    }
    
    public int getState() {
        return this.state;
    }
    
    public void setState(final int state) {
        this.state = state;
    }
    
    public int getSignCount() {
        return this.signupMap.size();
    }
    
    public int getMatchId() {
        return this.matchId;
    }
    
    public String getMatchTag() {
        return this.matchTag;
    }
    
    public int getSeason() {
        return this.season;
    }
    
    public Date getSignupStartTime() {
        return this.signupStartTime;
    }
    
    public Date getSignupEndTime() {
        return this.signupEndTime;
    }
    
    public Date getMatchTime() {
        return this.matchTime;
    }
    
    public ConcurrentMap<Integer, MatchFight> getMatchFightMap() {
        return this.matchFightMap;
    }
    
    public int getTurn() {
        return this.turn;
    }
    
    public List<MatchRankEntity> getRankList() {
        return this.rankList;
    }
    
    public List<MatchResultEntity> getResultList() {
        return this.resultList;
    }
    
    public void init() {
        this.writeLock.lock();
        try {
            Match.logger.info("\u5f00\u59cb\u521d\u59cb\u5316\u6bd4\u8d5b");
            this.transferConfig = new TransferConfigMatch(this.gameServerHost, this.gameServerPort);
            (this.connection = new KfConnection(this.transferConfig, Match.logger, "kfwd_match_send_thread")).registerHandler(Command.QUERY_MATCHSTATE, (ResponseHandler)new QueryMatchStateHandler());
            this.connection.registerHandler(Command.QUERY_MATCHSCHEDULE, (ResponseHandler)new QueryMatchScheduleHandler(this));
            this.connection.registerHandler(Command.QUERY_MATCHRTINFO, (ResponseHandler)new QueryMatchRTInfoHandler(this));
            this.connection.registerHandler(Command.QUERY_MATCHRESULT, (ResponseHandler)new QueryMatchResultHandler(this));
            this.connection.connect();
            this.queryMatchReportHandler = new QueryMatchReportHandler(this);
            this.queryMatchNumScheduleHandler = new QueryMatchNumScheduleHandler(this);
            this.queryTurnRankHandler = new QueryTurnRankHandler(this);
            final List<KfwdMatchSign> signList = this.dataGetter.getKfwdMatchSignDao().getWorldMatchSignListByMatchTag(this.matchTag);
            for (final KfwdMatchSign worldMatchSign : signList) {
                final Player player = this.dataGetter.getPlayerDao().read(worldMatchSign.getPlayerId());
                if (player == null) {
                    continue;
                }
                final MatchAttendee matchAttendee = new MatchAttendee(worldMatchSign, player);
                this.signupMap.put(matchAttendee.getPlayerId(), matchAttendee);
                this.setCompetitorIdPlayerIdMap(matchAttendee);
            }
            Match.logger.info("\u7ed3\u675f\u521d\u59cb\u5316\u6bd4\u8d5b");
            final QueryMatchParam queryMatchParam = new QueryMatchParam();
            queryMatchParam.setMatchTag(this.matchTag);
            final Request request = new Request();
            request.setCommand(Command.QUERY_MATCHSTATE);
            request.setMessage(queryMatchParam);
            final List<Request> requestList = new ArrayList<Request>();
            requestList.add(request);
            this.connection.sendSync((List)requestList);
            Match.logger.info("\u5f00\u59cb\u83b7\u5f97\u6bd4\u8d5b\u72b6\u6001");
        }
        catch (Exception e) {
            e.printStackTrace();
            return;
        }
        finally {
            this.writeLock.unlock();
        }
        this.writeLock.unlock();
    }
    
    public void cancel() {
        this.writeLock.lock();
        try {
            this.state = 7;
        }
        finally {
            this.writeLock.unlock();
        }
        this.writeLock.unlock();
    }
    
    public void startPrepare(final long cd) {
        this.writeLock.lock();
        try {
            this.state = 1;
            this.signupStartTime = new Date(System.currentTimeMillis() + cd);
            this.pushMatchState(this.state);
            Match.logger.info("\u6bd4\u8d5b\u51c6\u5907");
        }
        finally {
            this.writeLock.unlock();
        }
        this.writeLock.unlock();
    }
    
    public void startSignup(final long cd) {
        this.writeLock.lock();
        try {
            this.state = 2;
            this.signupStartTime = new Date();
            this.signupEndTime = new Date(this.signupStartTime.getTime() + cd);
            this.pushMatchState(this.state);
            Match.logger.info("\u62a5\u540d\u5f00\u59cb");
        }
        finally {
            this.writeLock.unlock();
        }
        this.writeLock.unlock();
    }
    
    public void endSignup(final long cd) {
        this.writeLock.lock();
        try {
            if (this.state == 2) {
                this.pushMatchState(this.state = 3);
                Match.logger.info("\u62a5\u540d\u7ed3\u675f");
            }
        }
        finally {
            this.writeLock.unlock();
        }
        this.writeLock.unlock();
    }
    
    public void endArrang() {
        this.writeLock.lock();
        try {
            Match.logger.info("\u7ed3\u675f\u6bd4\u8d5b\u5b89\u6392\u5f00\u59cb");
            this.state = 5;
            this.turn = 1;
            final QueryMatchScheduleParam queryMatchScheduleParam = new QueryMatchScheduleParam();
            queryMatchScheduleParam.setAll(false);
            queryMatchScheduleParam.setMatchTag(this.matchTag);
            queryMatchScheduleParam.setTurn(this.turn);
            final Request request = new Request();
            request.setCommand(Command.QUERY_MATCHSCHEDULE);
            request.setMessage(queryMatchScheduleParam);
            this.connection.send(request);
            this.pushMatchState(this.state);
        }
        finally {
            this.writeLock.unlock();
        }
        this.writeLock.unlock();
    }
    
    public void sendQueryMatchScheduleRequest() {
        this.writeLock.lock();
        try {
            ++this.turn;
            Match.logger.info("\u67e5\u8be2" + this.turn + "\u8f6e\u6bd4\u8d5b\u5b89\u6392\u5f00\u59cb");
            final QueryMatchScheduleParam queryMatchScheduleParam = new QueryMatchScheduleParam();
            queryMatchScheduleParam.setAll(false);
            queryMatchScheduleParam.setMatchTag(this.matchTag);
            queryMatchScheduleParam.setTurn(this.turn);
            final Request request = new Request();
            request.setCommand(Command.QUERY_MATCHSCHEDULE);
            request.setMessage(queryMatchScheduleParam);
            this.connection.send(request);
        }
        finally {
            this.writeLock.unlock();
        }
        this.writeLock.unlock();
    }
    
    public void resendQueryMatchScheduleRequest() {
        this.writeLock.lock();
        try {
            Match.logger.info("\u518d\u6b21\u67e5\u8be2" + this.turn + "\u8f6e\u6bd4\u8d5b\u5b89\u6392\u5f00\u59cb");
            final QueryMatchScheduleParam queryMatchScheduleParam = new QueryMatchScheduleParam();
            queryMatchScheduleParam.setAll(false);
            queryMatchScheduleParam.setMatchTag(this.matchTag);
            queryMatchScheduleParam.setTurn(this.turn);
            final Request request = new Request();
            request.setCommand(Command.QUERY_MATCHSCHEDULE);
            request.setMessage(queryMatchScheduleParam);
            this.connection.send(request);
        }
        finally {
            this.writeLock.unlock();
        }
        this.writeLock.unlock();
    }
    
    public void sendQueryMatchStateRequest() {
        this.readLock.lock();
        try {
            final QueryMatchParam queryMatchParam = new QueryMatchParam();
            queryMatchParam.setMatchTag(this.matchTag);
            final Request request = new Request();
            request.setCommand(Command.QUERY_MATCHSTATE);
            request.setMessage(queryMatchParam);
            this.connection.send(request);
        }
        finally {
            this.readLock.unlock();
        }
        this.readLock.unlock();
    }
    
    public boolean isPassServerCondition() {
        for (final IMatchCondition condition : this.conditionList) {
            if (!condition.checkServer(this.dataGetter)) {
                return false;
            }
        }
        return true;
    }
    
    public boolean isPassPlayerCondition(final int playerId) {
        for (final IMatchCondition condition : this.conditionList) {
            if (!condition.checkPlayer(playerId, this.dataGetter)) {
                return false;
            }
        }
        return true;
    }
    
    public MatchAttendee getSignMatchAttendee(final int playerId) {
        return this.signupMap.get(playerId);
    }
    
    public void removeSignMatchAttendee(final int playerId) {
        this.signupMap.remove(playerId);
    }
    
    public byte[] signup(final Player player, final String gIds) {
        this.readLock.lock();
        try {
            if (this.signupMap.containsKey((int)player.getPlayerId())) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_KFWD_MATCH_10004);
            }
            if (this.state != 2) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_KFWD_MATCH_10005);
            }
            final MatchAttendee matchAttendee = new MatchAttendee(this, player);
            matchAttendee.setgIds(gIds);
            final SignAndSyncParam signAndSyncParam = matchAttendee.buildParam(this.dataGetter, gIds);
            final Request request = new Request();
            request.setCommand(Command.SIGN);
            request.setMessage(signAndSyncParam);
            try {
                final List<Request> list = new ArrayList<Request>();
                list.add(request);
                final Response r = this.connection.sendSyncAndGetResponse((List)list);
                Match.logger.info("get signUp response " + r.getMessage());
                if (r.getCommand() != Command.SIGN) {
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.T_KFWD_MATCH_10015);
                }
                final SignEntity signEntity = (SignEntity)r.getMessage();
                if (signEntity.getState() == 1) {
                    matchAttendee.setCompetitorId(signEntity.getCompetitorId());
                    this.signupMap.put(matchAttendee.getPlayerId(), matchAttendee);
                    this.setCompetitorIdPlayerIdMap(matchAttendee);
                    final RewardOperationHandlerRetry handler = new RewardOperationHandlerRetry(3, player.getPlayerId());
                    handler.addHandler(new RewardOperationHandlerWorldMatchSaveSign(matchAttendee));
                    handler.handle(this.dataGetter);
                    final JsonDocument doc = new JsonDocument();
                    doc.startObject();
                    doc.createElement("forceName", WorldCityCommon.nationIdNameMap.get(matchAttendee.getForceId()));
                    doc.createElement("serverId", matchAttendee.getServerId());
                    doc.createElement("serverName", matchAttendee.getServerName());
                    doc.createElement("playerName", matchAttendee.getPlayerName());
                    doc.endObject();
                    EventListener.fireEvent(new CommonEvent(25, player.getPlayerId(), doc.toByte(), new int[] { -1 }));
                    return JsonBuilder.getJson(State.SUCCESS, MessageFormatter.format(LocalMessages.T_WORLD_MATCH_MSG_1, new Object[] { this.minLv, this.maxLv }));
                }
                if (signEntity.getErrorCode() == -2) {
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.T_KFWD_MATCH_10012);
                }
                if (signEntity.getErrorCode() == -1) {
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.T_KFWD_MATCH_10013);
                }
                if (signEntity.getErrorCode() == -3) {
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.T_KFWD_MATCH_10014);
                }
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_KFWD_MATCH_10015);
            }
            catch (Exception e) {
                Match.logger.error("signup request error", e);
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_KFWD_MATCH_10015);
            }
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    public byte[] enter(final Player player) {
        final Date nowDate = new Date();
        if (!this.signupMap.containsKey((int)player.getPlayerId())) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_KFWD_MATCH_10006);
        }
        final MatchAttendee matchAttendee = this.signupMap.get((int)player.getPlayerId());
        if (this.state != 5) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_KFWD_MATCH_10029);
        }
        final MatchFight matchFight = matchAttendee.getMatchFight();
        if (matchFight == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_KFWD_MATCH_10028);
        }
        PushReportInfo pri = this.pushReportMap.get(player.getPlayerId());
        if (pri == null) {
            pri = new PushReportInfo(0, "");
            this.pushReportMap.put(player.getPlayerId(), pri);
        }
        pri.setInScene(true);
        if (matchFight.getState() == 6 || matchFight.getState() == 7) {
            final MatchFightMember member1 = matchFight.getMember1();
            final MatchFightMember member2 = matchFight.getMember2();
            boolean isAttSide = false;
            if (member1.getCompetitorId() == matchAttendee.getCompetitorId()) {
                isAttSide = true;
            }
            this.rePushReport(player, isAttSide, member1, member2);
            final JsonDocument doc = new JsonDocument();
            doc.startObject();
            doc.createElement("fighting", true);
            doc.endObject();
            return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
        }
        final MatchFightMember member1 = matchFight.getMember1();
        final MatchFightMember member2 = matchFight.getMember2();
        boolean isAttack = false;
        MatchFightMember myself;
        MatchFightMember opponent;
        if (member1.getCompetitorId() == matchAttendee.getCompetitorId()) {
            myself = member1;
            opponent = member2;
            isAttack = true;
        }
        else {
            myself = member2;
            opponent = member1;
        }
        final JsonDocument doc2 = new JsonDocument();
        doc2.startObject();
        doc2.createElement("fighting", false);
        doc2.createElement("isAttack", isAttack);
        doc2.createElement("turn", matchFight.getTurn());
        doc2.createElement("matchNum", matchFight.getMatchNum());
        doc2.createElement("myPlayerName", myself.getPlayerName());
        doc2.createElement("myPlayerPic", myself.getPlayerPic());
        doc2.createElement("myPlayerLv", myself.getPlayerLv());
        doc2.createElement("myRewardMode", matchAttendee.getRewardMode());
        doc2.createElement("myWinTimes", myself.getWinMatch());
        doc2.createElement("myInspireTimes", matchAttendee.getInspireTimes());
        doc2.startArray("myGenerals");
        final CampArmyParam[] myGenerals = myself.getCampDatas();
        final String[] gIds = matchAttendee.getgIds().split("#");
        final ArrayList<Integer> list = new ArrayList<Integer>();
        String[] array;
        for (int length = (array = gIds).length, i = 0; i < length; ++i) {
            final String gId = array[i];
            list.add(Integer.valueOf(gId));
        }
        CampArmyParam[] array2;
        for (int length2 = (array2 = myGenerals).length, j = 0; j < length2; ++j) {
            final CampArmyParam camp = array2[j];
            doc2.startObject();
            doc2.createElement("index", list.indexOf(camp.getGeneralId()) + 1);
            this.getStandByInfo(camp, doc2);
            doc2.endObject();
        }
        doc2.endArray();
        if (opponent != null) {
            doc2.createElement("enemyPlayerName", opponent.getPlayerName());
            doc2.createElement("enemyPlayerPic", opponent.getPlayerPic());
            doc2.createElement("enemyWinTimes", opponent.getWinMatch());
            doc2.createElement("enemyPlayerLv", opponent.getPlayerLv());
            doc2.startArray("enemyGenerals");
            final CampArmyParam[] enemyGenerals = opponent.getCampDatas();
            int index = 1;
            CampArmyParam[] array3;
            for (int length3 = (array3 = enemyGenerals).length, k = 0; k < length3; ++k) {
                final CampArmyParam camp2 = array3[k];
                doc2.startObject();
                doc2.createElement("index", (index++));
                this.getStandByInfo(camp2, doc2);
                doc2.endObject();
            }
            doc2.endArray();
        }
        doc2.createElement("formationCd", CDUtil.getCD(matchFight.getFormationEndTime(), nowDate));
        doc2.createElement("fightStartCd", CDUtil.getCD(matchFight.getFightTime(), nowDate));
        doc2.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc2.toByte());
    }
    
    private void rePushReport(final Player player, final boolean isAttSide, final MatchFightMember member1, final MatchFightMember member2) {
        final int playerId = player.getPlayerId();
        MatchService.getExecutor().schedule(new RePushReportRunner(playerId, isAttSide, this, member1, member2), 0L, TimeUnit.MICROSECONDS);
    }
    
    private void getStandByInfo(final CampArmyParam camp, final JsonDocument doc) {
        doc.createElement("generalId", camp.getGeneralId());
        doc.createElement("generalName", camp.getGeneralName());
        doc.createElement("generalPic", camp.getGeneralPic());
        doc.createElement("generalLv", camp.getGeneralLv());
        doc.createElement("quality", camp.getQuality());
        doc.createElement("leader", camp.getLeader());
        doc.createElement("strength", camp.getStrength());
        doc.createElement("troopId", camp.getTroopId());
        doc.createElement("troopName", camp.getTroopName());
        doc.createElement("troopHp", camp.getTroopHp());
    }
    
    public byte[] changeRewardMode(final Player player, final int mode) {
        this.readLock.lock();
        try {
            if (!this.signupMap.containsKey((int)player.getPlayerId())) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_KFWD_MATCH_10006);
            }
            final MatchAttendee matchAttendee = this.signupMap.get((int)player.getPlayerId());
            if (this.state != 5) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_KFWD_MATCH_10021);
            }
            if (matchAttendee.getMatchFight() == null || matchAttendee.getMatchFight().getMember2() == null) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_KFWD_MATCH_10022);
            }
            if (mode == 2 && matchAttendee.getRewardMode() != 1) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
            }
            if (mode == 1 && matchAttendee.getRewardMode() != 0) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
            }
            if (matchAttendee.getMatchFight().getMatchNum() != 1 || matchAttendee.getMatchFight().getState() >= 5) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_KFWD_MATCH_10021);
            }
            final int gold = this.getChangeRewardModeGold(matchAttendee.getRewardMode());
            if (!this.dataGetter.getPlayerDao().canConsumeMoney(player, gold)) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10002);
            }
            matchAttendee.setRewardMode(mode);
            final RewardOperationHandlerRetry handler = new RewardOperationHandlerRetry(3, player.getPlayerId());
            handler.addHandler(new RewardOperationHandlerConsumeGold(player.getPlayerId(), gold, "\u8de8\u670d\u6b66\u6597\u6a21\u5f0f\u6d88\u8017\u91d1\u5e01"));
            handler.handle(this.dataGetter);
            return JsonBuilder.getJson(State.SUCCESS, "");
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    public byte[] inspire(final Player player) {
        this.readLock.lock();
        try {
            if (!this.signupMap.containsKey((int)player.getPlayerId())) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_KFWD_MATCH_10006);
            }
            final MatchAttendee matchAttendee = this.signupMap.get((int)player.getPlayerId());
            if (this.state != 5) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_KFWD_MATCH_10029);
            }
            if (matchAttendee.getMatchFight() == null || matchAttendee.getMatchFight().getMember2() == null) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_KFWD_MATCH_10022);
            }
            if (matchAttendee.getMatchFight().getState() >= 5) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_KFWD_MATCH_10023);
            }
            if (matchAttendee.getInspireTimes() >= 3) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_KFWD_MATCH_10024);
            }
            final InspireParam inspireParam = new InspireParam();
            inspireParam.setCompetitorId(matchAttendee.getCompetitorId());
            inspireParam.setMatchId(matchAttendee.getMatchFight().getMatchId());
            inspireParam.setMatchTag(matchAttendee.getMatchTag());
            final Request request = new Request();
            request.setCommand(Command.INSPIRE);
            request.setMessage(inspireParam);
            try {
                final List<Request> list = new ArrayList<Request>();
                list.add(request);
                final Response r = this.connection.sendSyncAndGetResponse((List)list);
                Match.logger.info("get inspire response " + r.getMessage());
                if (r.getCommand() != Command.INSPIRE) {
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.T_KFWD_MATCH_10025);
                }
                final InspireEntity inspireEntity = (InspireEntity)r.getMessage();
                if (inspireEntity.getState() == 1) {
                    matchAttendee.setInspireTimes(matchAttendee.getInspireTimes() + 1);
                    return JsonBuilder.getJson(State.SUCCESS, "");
                }
                if (inspireEntity.getErrorCode() == -2) {
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.T_KFWD_MATCH_10012);
                }
                if (inspireEntity.getErrorCode() == -1) {
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.T_KFWD_MATCH_10013);
                }
                if (inspireEntity.getErrorCode() == -3) {
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.T_KFWD_MATCH_10014);
                }
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_KFWD_MATCH_10015);
            }
            catch (Exception e) {
                Match.logger.error("inspired request error", e);
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_KFWD_MATCH_10025);
            }
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    public byte[] setFormation(final Player player, final String gIds) {
        this.readLock.lock();
        try {
            if (!this.signupMap.containsKey((int)player.getPlayerId())) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_KFWD_MATCH_10006);
            }
            final MatchAttendee matchAttendee = this.signupMap.get((int)player.getPlayerId());
            final String oldGids = matchAttendee.getgIds();
            final String[] oldIds = oldGids.split("#");
            final Set<Integer> oldIdSet = new HashSet<Integer>();
            String[] array;
            for (int length = (array = oldIds).length, i = 0; i < length; ++i) {
                final String id = array[i];
                oldIdSet.add(Integer.valueOf(id));
            }
            String[] split;
            for (int length2 = (split = gIds.split("#")).length, j = 0; j < length2; ++j) {
                final String s = split[j];
                final int newId = Integer.valueOf(s);
                if (!oldIdSet.contains(newId)) {
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
                }
            }
            if (this.state == 2) {
                matchAttendee.assign(player, gIds, !this.isPassPlayerCondition(player.getPlayerId()));
                final SignAndSyncParam signAndSyncParam = matchAttendee.buildParam(this.dataGetter, gIds);
                final Request request = new Request();
                request.setCommand(Command.SYNC);
                request.setMessage(signAndSyncParam);
                try {
                    final List<Request> list = new ArrayList<Request>();
                    list.add(request);
                    final Response r = this.connection.sendSyncAndGetResponse((List)list);
                    Match.logger.info(r);
                    if (r.getCommand() != Command.SYNC) {
                        matchAttendee.setgIds(oldGids);
                        return JsonBuilder.getJson(State.FAIL, LocalMessages.T_KFWD_MATCH_10007);
                    }
                    final SignEntity signEntity = (SignEntity)r.getMessage();
                    if (signEntity.getState() == 1) {
                        final RewardOperationHandlerRetry handler = new RewardOperationHandlerRetry(3, player.getPlayerId());
                        handler.addHandler(new RewardOperationHandlerWorldMatchSaveSign(matchAttendee));
                        handler.handle(this.dataGetter);
                        return JsonBuilder.getJson(State.SUCCESS, "");
                    }
                    matchAttendee.setgIds(oldGids);
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.T_KFWD_MATCH_10007);
                }
                catch (Exception e) {
                    Match.logger.error("setFormation request error", e);
                    matchAttendee.setgIds(oldGids);
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.T_KFWD_MATCH_10007);
                }
            }
            if (this.state == 5) {
                if (matchAttendee.getMatchFight() == null) {
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.T_KFWD_MATCH_10008);
                }
                final MatchFight matchFight = matchAttendee.getMatchFight();
                if (matchFight.getState() == 2) {
                    if (matchAttendee.getCompetitorId() != matchFight.getMember1().getCompetitorId()) {
                        return JsonBuilder.getJson(State.FAIL, LocalMessages.T_KFWD_MATCH_10030);
                    }
                }
                else if (matchFight.getState() == 3) {
                    if (matchAttendee.getCompetitorId() != matchFight.getMember2().getCompetitorId()) {
                        return JsonBuilder.getJson(State.FAIL, LocalMessages.T_KFWD_MATCH_10031);
                    }
                }
                else if (matchFight.getState() != 4) {
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.T_KFWD_MATCH_10008);
                }
                if (matchFight.getFormationEndTime().getTime() - System.currentTimeMillis() <= 0L) {
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.T_KFWD_MATCH_10032);
                }
                matchAttendee.assign(player, gIds, !this.isPassPlayerCondition(player.getPlayerId()));
                final SignAndSyncParam signAndSyncParam2 = matchAttendee.buildParam(this.dataGetter, gIds);
                final Request request2 = new Request();
                request2.setCommand(Command.SYNC);
                request2.setMessage(signAndSyncParam2);
                try {
                    final List<Request> list2 = new ArrayList<Request>();
                    list2.add(request2);
                    final Response r2 = this.connection.sendSyncAndGetResponse((List)list2);
                    if (r2.getCommand() != Command.SYNC) {
                        matchAttendee.setgIds(oldGids);
                        return JsonBuilder.getJson(State.FAIL, LocalMessages.T_KFWD_MATCH_10007);
                    }
                    final SignEntity signEntity2 = (SignEntity)r2.getMessage();
                    if (signEntity2.getState() == 1) {
                        if (matchAttendee.getCompetitorId() == matchFight.getMember1().getCompetitorId()) {
                            matchFight.getMember1().assign(gIds);
                        }
                        else {
                            matchFight.getMember2().assign(gIds);
                        }
                        return JsonBuilder.getJson(State.SUCCESS, "");
                    }
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.T_KFWD_MATCH_10007);
                }
                catch (Exception e2) {
                    Match.logger.error("setFormation request error", e2);
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.T_KFWD_MATCH_10007);
                }
            }
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_KFWD_MATCH_10008);
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    public void setCompetitorIdPlayerIdMap(final MatchAttendee matchAttendee) {
        this.competitorIdPlayerIdMap.put(matchAttendee.getCompetitorId(), matchAttendee.getPlayerId());
    }
    
    public void setAttendeeMatchFight(final int competitorId, final MatchFight matchFight) {
        if (this.competitorIdPlayerIdMap.containsKey(competitorId)) {
            final int playerId = this.competitorIdPlayerIdMap.get(competitorId);
            final MatchAttendee matchAttendee = this.signupMap.get(playerId);
            if (matchAttendee != null) {
                matchAttendee.setMatchFight(matchFight);
            }
        }
    }
    
    public void setMatchSchedule(final List<MatchScheduleEntity> matchScheduleEntityList) {
        this.readLock.lock();
        try {
            Match.logger.info("\u8bbe\u7f6e" + this.turn + "\u8f6e\u6bd4\u8d5b\u5b89\u6392\u5f00\u59cbstart");
            this.matchFightMap.clear();
            this.nextGetScheduleTimestamp = 0L;
            for (final MatchScheduleEntity matchScheduleEntity : matchScheduleEntityList) {
                if (matchScheduleEntity == null) {
                    this.state = 6;
                    return;
                }
                final MatchFight matchFight = new MatchFight(matchScheduleEntity, this);
                this.matchFightMap.put(matchFight.getSessionId(), matchFight);
                final QueryMatchRTInfoParam queryMatchRTInfoParam = new QueryMatchRTInfoParam();
                queryMatchRTInfoParam.setMatchId(matchFight.getMatchId());
                queryMatchRTInfoParam.setMatchTag(this.matchTag);
                queryMatchRTInfoParam.setVersion(matchFight.getVersion());
                final Request request = new Request();
                request.setCommand(Command.QUERY_MATCHRTINFO);
                request.setMessage(queryMatchRTInfoParam);
                this.connection.send(request);
            }
            Match.logger.info("\u8bbe\u7f6e" + this.turn + "\u8f6e\u6bd4\u8d5b\u5b89\u6392\u5f00\u59cbend");
        }
        finally {
            this.readLock.unlock();
        }
        this.readLock.unlock();
    }
    
    public void updateMatchFight(final MatchRTInfoEntity matchRTInfoEntity) {
        this.readLock.lock();
        try {
            Match.logger.info("\u8bbe\u7f6e" + this.turn + "\u8f6e\u6bd4\u8d5b\u6218\u51b5\u5f00\u59cbstart");
            final MatchFight matchFight = this.matchFightMap.get(matchRTInfoEntity.getSession());
            if (matchFight != null) {
                final int result = matchFight.assign(matchRTInfoEntity);
                if (result == 1) {
                    MatchService.getExecutor().schedule(new SendQueryMatchReportRequestRunner(this, matchFight), matchFight.getFightTime().getTime() - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
                }
                else if (result == 2) {
                    this.sendQueryMatchReportRequest(matchFight);
                }
            }
            Match.logger.info("\u8bbe\u7f6e" + this.turn + "\u8f6e\u6bd4\u8d5b\u6218\u51b5\u5f00\u59cbend");
        }
        finally {
            this.readLock.unlock();
        }
        this.readLock.unlock();
    }
    
    public void sendQueryMatchReportRequest(final MatchFight matchFight) {
        this.readLock.lock();
        try {
            Match.logger.info("\u8bbe\u7f6e" + this.turn + "\u8f6e\u6bd4\u8d5b\u6218\u62a5\u5f00\u59cbstart");
            matchFight.setState(5);
            final QueryMatchReportParam queryMatchReportParam = new QueryMatchReportParam();
            queryMatchReportParam.setMatchId(matchFight.getMatchId());
            queryMatchReportParam.setMatchTag(this.matchTag);
            queryMatchReportParam.setSession(matchFight.getSessionId());
            final Request request = new Request();
            request.setCommand(Command.QUERY_MATCHREPORT);
            request.setMessage(queryMatchReportParam);
            this.connection.send(request, (RequestHandler)this.queryMatchReportHandler);
            Match.logger.info("\u8bbe\u7f6e" + this.turn + "\u8f6e\u6bd4\u8d5b\u6218\u62a5\u5f00\u59cbend");
        }
        finally {
            this.readLock.unlock();
        }
        this.readLock.unlock();
    }
    
    public void sendQueryMatchNumScheduleRequest(final MatchFight matchFight) {
        final QueryMatchNumScheduleParam queryMatchNumScheduleParam = new QueryMatchNumScheduleParam();
        queryMatchNumScheduleParam.setMatchNum(matchFight.getMatchNum() + 1);
        queryMatchNumScheduleParam.setMatchTag(this.matchTag);
        queryMatchNumScheduleParam.setSession(matchFight.getSessionId());
        queryMatchNumScheduleParam.setTurn(matchFight.getTurn());
        final Request request = new Request();
        request.setCommand(Command.QUERY_MATCHNUMSCHEDULE);
        request.setMessage(queryMatchNumScheduleParam);
        this.connection.send(request, (RequestHandler)this.queryMatchNumScheduleHandler);
    }
    
    public void setMatchNumSchedule(final MatchScheduleEntity matchScheduleEntity) {
        this.readLock.lock();
        try {
            final MatchFight matchFight = this.matchFightMap.get(matchScheduleEntity.getSession());
            if (matchFight != null) {
                Match.logger.info("\u8bbe\u7f6e" + this.turn + "\u8f6e" + matchScheduleEntity.getMatchNum() + "\u5c40\u6bd4\u8d5b\u5b89\u6392\u5f00\u59cbstart");
                matchFight.assign(matchScheduleEntity);
                final QueryMatchRTInfoParam queryMatchRTInfoParam = new QueryMatchRTInfoParam();
                queryMatchRTInfoParam.setMatchId(matchFight.getMatchId());
                queryMatchRTInfoParam.setMatchTag(this.matchTag);
                queryMatchRTInfoParam.setVersion(matchFight.getVersion());
                final Request request = new Request();
                request.setCommand(Command.QUERY_MATCHRTINFO);
                request.setMessage(queryMatchRTInfoParam);
                this.connection.send(request);
                Match.logger.info("\u8bbe\u7f6e" + this.turn + "\u8f6e" + matchScheduleEntity.getMatchNum() + "\u5c40\u6bd4\u8d5b\u5b89\u6392\u5f00\u59cbend");
            }
        }
        finally {
            this.readLock.unlock();
        }
        this.readLock.unlock();
    }
    
    public void updateMatchReport(final MatchReportEntity matchReportEntity) {
        this.readLock.lock();
        try {
            final MatchFight matchFight = this.matchFightMap.get(matchReportEntity.getSession());
            if (matchFight == null) {
                Match.logger.info("\u66f4\u65b0\u6218\u6597\u7ed3\u679c\u65f6\u83b7\u5f97\u7684matchFight\u4e0d\u5b58\u5728:" + matchReportEntity.getSession());
            }
            else {
                matchFight.assign(matchReportEntity);
                this.schedulePushMatchReport(matchFight, matchReportEntity);
                if (matchFight.getState() == 6) {
                    MatchService.getExecutor().schedule(new SendQueryMatchNumScheduleRequestRunner(this, matchFight), matchFight.getFightTime().getTime() - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
                    Match.logger.info("\u6bd4\u8d5b" + this.turn + "\u8f6e" + matchFight.getMatchNum() + "\u5c40\u6bd4\u8d5b\u7ed3\u675f");
                }
                else if (matchFight.getState() == 7) {
                    if (matchFight.getMember2() != null) {
                        synchronized (this) {
                            if (this.nextGetScheduleTimestamp < matchFight.getFightTime().getTime()) {
                                this.nextGetScheduleTimestamp = matchFight.getFightTime().getTime();
                            }
                        }
                    }
                    MatchService.getExecutor().schedule(new getTurnRewardRunner(this, matchFight), 10L, TimeUnit.MILLISECONDS);
                    Match.logger.info("\u6bd4\u8d5b" + this.turn + "\u8f6e" + "\u6bd4\u8d5b\u7ed3\u675f");
                    if (this.isAllMatchFightFinish()) {
                        MatchService.getExecutor().schedule(new SendQueryMatchScheduleRequestRunner(this), this.nextGetScheduleTimestamp - System.currentTimeMillis() + 5L, TimeUnit.MILLISECONDS);
                        Match.logger.info("\u6bd4\u8d5b" + this.turn + "\u8f6e" + "\u6bd4\u8d5b\u5168\u90e8\u7ed3\u675f");
                    }
                }
                else {
                    MatchService.getExecutor().schedule(new getTurnRewardRunner(this, matchFight), 10L, TimeUnit.MILLISECONDS);
                    if (this.isAllMatchFightOver()) {
                        MatchService.getExecutor().schedule(new SendQueryMatchResultFirstRequestRunner(this), 5L, TimeUnit.SECONDS);
                    }
                }
            }
        }
        finally {
            this.readLock.unlock();
        }
        this.readLock.unlock();
    }
    
    private void schedulePushMatchReport(final MatchFight matchFight, final MatchReportEntity matchReportEntity) {
        final String matchReport = matchReportEntity.getReport();
        final List<Integer> needPushPlayerId = new ArrayList<Integer>();
        int winPlayerId = 0;
        if (this.competitorIdPlayerIdMap.containsKey(matchFight.getMember1().getCompetitorId())) {
            final int playerId = matchFight.getMember1().getPlayerId();
            needPushPlayerId.add(playerId);
            if (matchFight.getMember1().getCompetitorId() == matchReportEntity.getWinner()) {
                winPlayerId = playerId;
            }
        }
        if (matchFight.getMember2() != null && this.competitorIdPlayerIdMap.containsKey(matchFight.getMember2().getCompetitorId())) {
            final int playerId = matchFight.getMember2().getPlayerId();
            needPushPlayerId.add(playerId);
            if (matchFight.getMember2().getCompetitorId() == matchReportEntity.getWinner()) {
                winPlayerId = playerId;
            }
        }
        final Iterator<Integer> iterator = needPushPlayerId.iterator();
        while (iterator.hasNext()) {
            final int playerId = iterator.next();
            final StringBuilder sb = new StringBuilder(matchReport);
            int reportIndex = matchReport.split(":").length;
            final int result = (playerId == winPlayerId) ? 1 : 0;
            final MatchAttendee matchAttendee = this.signupMap.get(playerId);
            final int rewardMode = matchAttendee.getRewardMode();
            ++reportIndex;
            final boolean someoneWin = matchFight.getState() == 7 || matchFight.getState() == 8;
            final String rewardInfoReport = this.getRewardReport(playerId, result, rewardMode, reportIndex, matchFight, someoneWin, this.dataGetter);
            sb.append(rewardInfoReport);
            if (someoneWin) {
                ++reportIndex;
                final String battlePointReport = this.getBattlePointReport(matchFight, matchAttendee, reportIndex);
                sb.append(":");
                sb.append(battlePointReport);
            }
            PushReportInfo pri = this.pushReportMap.get(playerId);
            if (pri == null) {
                pri = new PushReportInfo(0, sb.toString());
            }
            else {
                pri.setReport(sb.toString());
                pri.setReportIndex(0);
            }
            this.pushReportMap.put(playerId, pri);
            MatchService.getExecutor().schedule(new PushReportRunner(playerId, sb, 1, this, matchFight, this.dataGetter), 0L, TimeUnit.SECONDS);
        }
    }
    
    private String getRewardReport(final int playerId, final int result, final int rewardMode, final int reportIndex, final MatchFight matchFight, final boolean finish, final IDataGetter dataGetter) {
        final StringBuilder report = new StringBuilder();
        report.append(reportIndex);
        report.append("|");
        report.append(5);
        report.append("#");
        report.append(21);
        report.append("|");
        report.append(matchFight.getTurn());
        report.append("|");
        report.append(matchFight.getMatchNum());
        report.append("|");
        if (rewardMode == 1) {
            report.append(2);
        }
        else if (rewardMode == 2) {
            report.append(4);
        }
        else {
            report.append(1);
        }
        report.append("|");
        report.append(result);
        report.append(";");
        for (int resourceType = 1; resourceType < 5; ++resourceType) {
            int value = dataGetter.getBuildingOutputCache().getBuildingsOutput(playerId, resourceType);
            final int multiple = (result == 1) ? 2 : 1;
            value *= multiple;
            if (rewardMode == 1) {
                value *= 2;
            }
            else if (rewardMode == 2) {
                value *= 4;
            }
            report.append(resourceType);
            report.append("|");
            report.append(value);
            if (resourceType == 4) {
                report.append(";");
            }
            else {
                report.append("*");
            }
        }
        report.append(finish ? 1 : 0);
        report.append(";");
        report.append(3000L);
        return report.toString();
    }
    
    private String getBattlePointReport(final MatchFight matchFight, final MatchAttendee matchAttendee, final int reportIndex) {
        final StringBuilder report = new StringBuilder();
        report.append(reportIndex);
        report.append("|");
        report.append(2);
        report.append("#");
        report.append(22);
        report.append("|");
        report.append(matchFight.getTurn());
        report.append("|");
        report.append(matchAttendee.getPoints());
        report.append("|");
        if (matchFight.getMember1().getCompetitorId() == matchAttendee.getCompetitorId()) {
            report.append(matchFight.getMember1().getPoint());
        }
        else {
            report.append(matchFight.getMember2().getPoint());
        }
        report.append("|");
        report.append(matchFight.getState() == 8);
        report.append(";");
        report.append(matchFight.getMember1().getPlayerName());
        report.append("|");
        report.append(matchFight.getMember1().getWinMatch());
        report.append("*");
        report.append(matchFight.getMember2().getPlayerName());
        report.append("|");
        report.append(matchFight.getMember2().getWinMatch());
        report.append(";");
        report.append(3000L);
        report.append(";");
        return report.toString();
    }
    
    public void sendQueryResultListFirst() {
        final QueryMatchResultParam queryMatchResultParam = new QueryMatchResultParam();
        queryMatchResultParam.setMatchTag(this.matchTag);
        final Request request = new Request();
        request.setCommand(Command.QUERY_MATCHRESULT);
        request.setMessage(queryMatchResultParam);
        this.connection.send(request);
        Match.logger.info("\u5f00\u59cb\u5904\u7406\u6bd4\u8d5b\u7ed3\u679c");
    }
    
    public void sendQueryResultList() {
        Match.logger.info("\u518d\u6b21\u5f00\u59cb\u5904\u7406\u6bd4\u8d5b\u7ed3\u679c");
        final QueryMatchResultParam queryMatchResultParam = new QueryMatchResultParam();
        queryMatchResultParam.setMatchTag(this.matchTag);
        final Request request = new Request();
        request.setCommand(Command.QUERY_MATCHRESULT);
        request.setMessage(queryMatchResultParam);
        this.connection.send(request);
    }
    
    public void sendQueryRankList(final int turn) {
        final QueryTurnRankParam queryTurnRankParam = new QueryTurnRankParam();
        queryTurnRankParam.setTurn(turn);
        queryTurnRankParam.setMatchTag(this.matchTag);
        final Request request = new Request();
        request.setCommand(Command.QUERY_TURNRANK);
        request.setMessage(queryTurnRankParam);
        this.connection.send(request, (RequestHandler)this.queryTurnRankHandler);
    }
    
    public synchronized void handleMatchRank(final List<MatchRankEntity> matchRankEntityList) {
        if (matchRankEntityList == null) {
            return;
        }
        this.rankList = matchRankEntityList;
    }
    
    public void handleMatchResult(final List<MatchResultEntity> matchResultEntityList) {
        Match.logger.info("\u5904\u7406\u6bd4\u8d5b\u7ed3\u679c");
        for (final MatchResultEntity matchResultEntity : matchResultEntityList) {
            this.resultList.add(matchResultEntity);
            if (this.competitorIdPlayerIdMap.containsKey(matchResultEntity.getCompetitorId())) {
                final MatchAttendee matchAttendee = this.signupMap.get(this.competitorIdPlayerIdMap.get(matchResultEntity.getCompetitorId()));
                if (matchAttendee == null) {
                    continue;
                }
                matchAttendee.setResult(matchResultEntity);
            }
        }
        this.pushMatchState(this.state = 6);
        Match.logger.info("\u6bd4\u8d5b\u5168\u90e8\u7ed3\u675f");
    }
    
    public void getTurnReward(final MatchFight matchFight) {
        int member1Point = 0;
        int member2Point = 0;
        if (matchFight.getMember1().getWinMatch() == 3) {
            if (matchFight.getMember2().getWinMatch() == 2) {
                member1Point = 2;
                member2Point = 1;
            }
            else {
                member1Point = 3;
            }
        }
        if (matchFight.getMember2().getWinMatch() == 3) {
            if (matchFight.getMember1().getWinMatch() == 2) {
                member2Point = 2;
                member1Point = 1;
            }
            else {
                member2Point = 3;
            }
        }
        if (this.competitorIdPlayerIdMap.containsKey(matchFight.getMember1().getCompetitorId())) {
            final MatchAttendee matchAttendee = this.signupMap.get(this.competitorIdPlayerIdMap.get(matchFight.getMember1().getCompetitorId()));
            if (matchAttendee != null) {
                this.matchReward.rewardTurn(matchAttendee, matchFight.getMember1(), member1Point, this.dataGetter);
            }
        }
        if (matchFight.getMember2() != null && this.competitorIdPlayerIdMap.containsKey(matchFight.getMember2().getCompetitorId())) {
            final MatchAttendee matchAttendee = this.signupMap.get(this.competitorIdPlayerIdMap.get(matchFight.getMember2().getCompetitorId()));
            if (matchAttendee != null) {
                this.matchReward.rewardTurn(matchAttendee, matchFight.getMember2(), member2Point, this.dataGetter);
            }
        }
    }
    
    public void query(final int playerId, final JsonDocument doc) {
        this.readLock.lock();
        try {
            switch (this.state) {
                case 1: {
                    MatchJsonBuilder.buildPrepare(this, doc);
                    break;
                }
                case 2: {
                    MatchJsonBuilder.buildSign(this, playerId, doc, this.dataGetter);
                    break;
                }
                case 3: {
                    MatchJsonBuilder.buildArrage(this, playerId, doc, this.dataGetter);
                    break;
                }
                case 4: {
                    MatchJsonBuilder.buildArrage(this, playerId, doc, this.dataGetter);
                    break;
                }
                case 5: {
                    MatchJsonBuilder.buildMatching(this, playerId, doc, this.dataGetter);
                    break;
                }
                case 6: {
                    MatchJsonBuilder.buildFinish(this, playerId, doc, this.dataGetter);
                    break;
                }
                case 7: {
                    MatchJsonBuilder.buildMatchCancel(doc);
                    break;
                }
            }
        }
        finally {
            this.readLock.unlock();
        }
        this.readLock.unlock();
    }
    
    public void buildNotify(final int playerId, final JsonDocument doc) {
        final boolean isIn = this.signupMap.containsKey(playerId);
        switch (this.state) {
            case 1: {
                doc.createElement("isIn", isIn);
                doc.createElement("state", this.state);
                doc.createElement("leftMilsec", this.signupStartTime.getTime() - System.currentTimeMillis());
                break;
            }
            case 2: {
                doc.createElement("isIn", isIn);
                doc.createElement("state", this.state);
                doc.createElement("leftMilsec", this.signupEndTime.getTime() - System.currentTimeMillis());
                break;
            }
            case 3: {
                doc.createElement("isIn", isIn);
                doc.createElement("state", this.state);
                break;
            }
            case 4: {
                doc.createElement("isIn", isIn);
                doc.createElement("state", this.state);
                break;
            }
            case 5: {
                doc.createElement("isIn", isIn);
                doc.createElement("state", this.state);
                break;
            }
            case 6: {
                doc.createElement("isIn", isIn);
                doc.createElement("state", this.state);
            }
            case 7: {
                doc.createElement("state", this.state);
                break;
            }
        }
    }
    
    private boolean isAllMatchFightFinish() {
        for (final MatchFight matchFight : this.matchFightMap.values()) {
            if (matchFight.getState() != 7) {
                return false;
            }
        }
        return true;
    }
    
    private boolean isAllMatchFightOver() {
        for (final MatchFight matchFight : this.matchFightMap.values()) {
            if (matchFight.getState() != 8) {
                return false;
            }
        }
        return true;
    }
    
    private void pushMatchState(final int state) {
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("matchState", state);
        doc.endObject();
        final Collection<PlayerDto> onlinePlayerList = Players.getAllPlayer();
        for (final PlayerDto onlinePlayer : onlinePlayerList) {
            Players.push(onlinePlayer.playerId, PushCommand.PUSH_KFWD_MATCH, doc.toByte());
        }
    }
    
    public byte[] exit(final Player player) {
        final PushReportInfo pri = this.pushReportMap.get(player.getPlayerId());
        if (pri != null) {
            pri.setInScene(false);
        }
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    public byte[] getBoxInfo(final Player player) {
        final int playerId = player.getPlayerId();
        final MatchAttendee matchAttendee = this.signupMap.get(playerId);
        if (matchAttendee == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10012);
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("point", matchAttendee.getPoints());
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    public byte[] receiveBox(final Player player, final int point, final IDataGetter dataGetter) {
        final int playerId = player.getPlayerId();
        final MatchAttendee matchAttendee = this.signupMap.get(playerId);
        if (matchAttendee == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10012);
        }
        if (matchAttendee.getPoints() < point) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_KFWD_MATCH_10026);
        }
        matchAttendee.minusPoints(point);
        final int gemLv = point / 3 + 1;
        dataGetter.getStoreHouseService().gainGem(player, 1, gemLv, LocalMessages.T_LOG_GEM_13, null);
        final ArmsGem gem = (ArmsGem)dataGetter.getArmsGemCache().get((Object)gemLv);
        return JsonBuilder.getJson(State.SUCCESS, MessageFormatter.format(LocalMessages.T_KFWD_MATCH_10027, new Object[] { gem.getName() }));
    }
    
    public byte[] getFinalRank(final PlayerDto playerDto) {
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.startArray("rankList");
        MatchResultEntity myResult = null;
        for (final MatchResultEntity matchResultEntity : this.resultList) {
            doc.startObject();
            doc.createElement("rank", matchResultEntity.getRank());
            doc.createElement("playerName", matchResultEntity.getPlayerName());
            doc.createElement("serverName", matchResultEntity.getServerName());
            doc.createElement("serverId", matchResultEntity.getServerId());
            doc.createElement("point", matchResultEntity.getScore());
            doc.createElement("totalPoint", matchResultEntity.getTotalScore());
            doc.endObject();
            if (this.competitorIdPlayerIdMap.containsKey(matchResultEntity.getCompetitorId()) && this.competitorIdPlayerIdMap.get(matchResultEntity.getCompetitorId()) == playerDto.playerId) {
                myResult = matchResultEntity;
            }
        }
        doc.endArray();
        if (myResult != null) {
            doc.createElement("myRank", myResult.getRank());
            doc.createElement("myPoint", myResult.getScore());
            doc.createElement("myTotalPoint", myResult.getTotalScore());
        }
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
}
