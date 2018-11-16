package com.reign.gcld.kfgz.service;

import org.springframework.beans.factory.*;
import org.springframework.stereotype.*;
import org.apache.commons.logging.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.player.dao.*;
import com.reign.gcld.tickets.dao.*;
import com.reign.gcld.kfgz.dao.*;
import java.util.concurrent.locks.*;
import com.reign.gcld.common.log.*;
import com.reign.kf.comm.transfer.oio.*;
import org.springframework.context.*;
import com.reign.gcld.kfwd.common.*;
import com.reign.kf.comm.transfer.*;
import org.springframework.beans.*;
import com.reign.gcld.player.dto.*;
import org.springframework.transaction.annotation.*;
import com.reign.framework.json.*;
import com.reign.kfgz.constants.*;
import com.reign.kfgz.dto.*;
import com.reign.gcld.player.domain.*;
import com.reign.gcld.kfzb.util.*;
import com.reign.kf.comm.protocol.*;
import java.util.*;
import com.reign.kf.comm.entity.*;
import com.reign.kfgz.dto.response.*;
import com.reign.gcld.world.common.*;
import com.reign.gcld.kfwd.service.*;
import com.reign.gcld.chat.common.*;
import com.reign.gcld.common.*;
import java.util.concurrent.*;
import com.reign.kf.comm.util.*;
import com.reign.gcld.log.*;
import com.reign.kfgz.dto.request.*;
import com.reign.gcld.kfgz.domain.*;
import com.reign.util.*;

@Component
public class KfgzSeasonService implements Runnable, IKfgzSeasonService, InitializingBean, ApplicationContextAware
{
    private static Log logger;
    public static final int UNSTART = 0;
    public static final int START = 1;
    public static final int BATTLE = 2;
    public static final int BEFORE_FIRST_BATTLE = 3;
    public static final int AFTER_LAST_BATTLE = 4;
    public static final int BETWEEN_BATTLE = 5;
    @Autowired
    private IDataGetter dataGetter;
    @Autowired
    private IKfgzMatchService kfgzMatchService;
    @Autowired
    private IKfgzSignupDao kfgzSignupDao;
    @Autowired
    private IPlayerDao playerDao;
    @Autowired
    private IKfgzTitleDao kfgzTitleDao;
    @Autowired
    private IPlayerTicketsDao playerTicketsDao;
    @Autowired
    private IKfgzPlayerFinalRewardDao kfgzPlayerFinalRewardDao;
    private KfgzScheduleInfoRes[] scheduleInfoList;
    private ReentrantLock scheduleInfoLock;
    private static final Logger timerLog;
    private KfgzAllRankRes[] kfgzAllRankRes;
    private boolean[] firstBattleMarks;
    private KfgzSeasonInfoRes seasonInfo;
    private ReentrantLock seasonLock;
    private static KfConnection connection;
    private IKfgzSeasonService self;
    ApplicationContext context;
    private static Thread singleThread;
    private static final long sleepTimeFast = 10000L;
    private static final long sleepTimeSlow = 30000L;
    public static final int finalRewardNum = 4;
    public static String initFinalReward;
    
    static {
        KfgzSeasonService.logger = new KfgzLogger();
        timerLog = new TimerLogger();
        KfgzSeasonService.connection = new KfConnection((TransferConfig)new TransferConfigGW(), KfgzSeasonService.logger);
        KfgzSeasonService.singleThread = null;
        KfgzSeasonService.initFinalReward = "";
        for (int i = 0; i < 4; ++i) {
            KfgzSeasonService.initFinalReward = String.valueOf(KfgzSeasonService.initFinalReward) + "0";
        }
    }
    
    public KfgzSeasonService() {
        this.scheduleInfoList = new KfgzScheduleInfoRes[4];
        this.scheduleInfoLock = new ReentrantLock();
        this.kfgzAllRankRes = new KfgzAllRankRes[4];
        this.firstBattleMarks = new boolean[4];
        this.seasonInfo = null;
        this.seasonLock = new ReentrantLock();
    }
    
    @Override
    public void init() {
        if (KfgzSeasonService.singleThread != null) {
            return;
        }
        (KfgzSeasonService.singleThread = new Thread(this)).start();
    }
    
    @Override
	public void setApplicationContext(final ApplicationContext arg0) throws BeansException {
        this.context = arg0;
    }
    
    @Override
    public int getSeasonId() {
        if (this.seasonInfo == null) {
            return -1;
        }
        return this.seasonInfo.getSeasonId();
    }
    
    @Override
    public int getGzIdByNation(final int nation) {
        if (this.scheduleInfoList[nation] == null) {
            return -1;
        }
        return this.scheduleInfoList[nation].getGzId();
    }
    
    @Override
    public String[] getMatchAddressAndPortByNation(final int nation) {
        if (this.scheduleInfoList[nation] == null) {
            return null;
        }
        return this.scheduleInfoList[nation].getMatchAddress().split(":");
    }
    
    @Override
    public boolean isFinalRound(final int nation) {
        return this.kfgzAllRankRes[nation] != null && this.kfgzAllRankRes[nation].isLastRound();
    }
    
    @Transactional
    @Override
    public byte[] getEndRewardBoard(final PlayerDto player) {
        if (this.seasonInfo == null || this.kfgzAllRankRes[player.forceId] == null || !this.kfgzAllRankRes[player.forceId].isLastRound()) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.KFGZ_9);
        }
        return JsonBuilder.getObjectJson(State.SUCCESS, this.getResult(player));
    }
    
    private byte[] getResult(final PlayerDto player) {
        final KfgzAllRankRes karr = this.kfgzAllRankRes[player.forceId];
        final KfgzNationResultReq knrr = karr.safeGetSelfNationRes();
        final KfgzPlayerFinalReward kpfr = this.kfgzPlayerFinalRewardDao.safeGetKfgzPlayerFinalReward(player.playerId, this.seasonInfo.getSeasonId(), knrr.getSelfCity());
        final JsonDocument doc = new JsonDocument();
        doc.createElement("reward", karr.getEndRewardString());
        final StringBuffer sb = new StringBuffer();
        int id = 1;
        String[] split;
        for (int length = (split = karr.getEndRewardString().split(",")).length, j = 0; j < length; ++j) {
            final String r = split[j];
            final int tickets = Integer.valueOf(r.split(":")[1]) * KfgzMatchService.getXsByRewardTimes(kpfr.getRewardTimesById(id));
            final int gold = KfgzMatchService.getTicketsGold(tickets, kpfr.getRewardTimesById(id));
            if (id > 1) {
                sb.append(",");
            }
            sb.append(gold);
            ++id;
        }
        doc.createElement("gold", sb.toString());
        doc.createElement("getRewardTimes", kpfr.getRewardTimes());
        final StringBuffer timesToXsSB = new StringBuffer();
        for (int i = 1; i < 4; ++i) {
            if (i > 1) {
                timesToXsSB.append(",");
            }
            timesToXsSB.append(i);
            timesToXsSB.append(":");
            timesToXsSB.append(KfgzMatchService.getXsByRewardTimes(i));
        }
        doc.createElement("timesToXs", timesToXsSB.toString());
        doc.createElement("maxTimes", 4);
        doc.createElement("serverName", knrr.getServerName());
        doc.createElement("nation", knrr.getNation());
        doc.createElement("layer", knrr.getLayerId());
        doc.createElement("group", knrr.getgId());
        doc.createElement("pos", knrr.getPos());
        doc.createElement("occupyCity", kpfr.getNationScore());
        final KfgzPlayerRankingInfoReq kprir = karr.getGroupKillArmyRes().get(0);
        doc.startObject("firstKiller");
        doc.createElement("playerName", kprir.getPlayerName());
        doc.createElement("playerLv", kprir.getPlayerLv());
        doc.createElement("killArmy", kprir.getKillArmy());
        doc.createElement("serverName", kprir.getServerName());
        doc.createElement("serverId", kprir.getServerId());
        doc.createElement("nation", kprir.getNation());
        doc.startArray("generals");
        for (final SimpleGInfo sg : KfgzCommConstants.getGInfosFromGInfoString(kprir.getgInfos())) {
            doc.startObject();
            doc.createElement("lv", sg.getgLv());
            doc.createElement("name", sg.getgName());
            doc.createElement("pic", sg.getPic());
            doc.createElement("quality", sg.getQuality());
            doc.endObject();
        }
        doc.endArray();
        doc.endObject();
        final KfgzPlayerRankingInfoReq kprir2 = karr.getGroupSoloRes().get(0);
        doc.startObject("firstSolo");
        doc.createElement("playerName", kprir2.getPlayerName());
        doc.createElement("playerLv", kprir2.getPlayerLv());
        doc.createElement("soloNum", kprir2.getSoloNum());
        doc.createElement("serverName", kprir2.getServerName());
        doc.createElement("serverId", kprir2.getServerId());
        doc.createElement("nation", kprir2.getNation());
        doc.startArray("generals");
        for (final SimpleGInfo sg2 : KfgzCommConstants.getGInfosFromGInfoString(kprir2.getgInfos())) {
            doc.startObject();
            doc.createElement("lv", sg2.getgLv());
            doc.createElement("name", sg2.getgName());
            doc.createElement("pic", sg2.getPic());
            doc.createElement("quality", sg2.getQuality());
            doc.endObject();
        }
        doc.endArray();
        doc.endObject();
        final KfgzPlayerRankingInfoReq kprir3 = karr.getGroupOccupyCityRes().get(0);
        doc.startObject("firstOccupyCity");
        doc.createElement("playerName", kprir3.getPlayerName());
        doc.createElement("playerLv", kprir3.getPlayerLv());
        doc.createElement("occupyCity", kprir3.getOccupyCity());
        doc.createElement("serverName", kprir3.getServerName());
        doc.createElement("serverId", kprir3.getServerId());
        doc.createElement("nation", kprir3.getNation());
        doc.startArray("generals");
        for (final SimpleGInfo sg3 : KfgzCommConstants.getGInfosFromGInfoString(kprir3.getgInfos())) {
            doc.startObject();
            doc.createElement("lv", sg3.getgLv());
            doc.createElement("name", sg3.getgName());
            doc.createElement("pic", sg3.getPic());
            doc.createElement("quality", sg3.getQuality());
            doc.endObject();
        }
        doc.endArray();
        doc.endObject();
        doc.startArray("upNations");
        for (final KfgzNationResultReq kk : karr.getGroupNationRes()) {
            if (kk.getPos() <= karr.getUpDownInfo()[0]) {
                doc.startObject();
                doc.createElement("pos", kk.getPos());
                doc.createElement("serverName", kk.getServerName());
                doc.createElement("nation", kk.getNation());
                doc.createElement("occupyCity", kk.getSelfCity());
                doc.endObject();
            }
        }
        doc.endArray();
        doc.startArray("downNations");
        for (final KfgzNationResultReq kk : karr.getGroupNationRes()) {
            if (kk.getPos() > karr.getUpDownInfo()[1]) {
                doc.startObject();
                doc.createElement("pos", kk.getPos());
                doc.createElement("serverName", kk.getServerName());
                doc.createElement("nation", kk.getNation());
                doc.createElement("occupyCity", kk.getSelfCity());
                doc.endObject();
            }
        }
        doc.endArray();
        return doc.toByte();
    }
    
    @Transactional
    @Override
    public byte[] getEndReward(final PlayerDto player, final int id) {
        if (id <= 0 || id > 4) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10012);
        }
        final KfgzAllRankRes karr = this.kfgzAllRankRes[player.forceId];
        if (karr == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.KFGZ_9);
        }
        final KfgzNationResultReq knrr = karr.safeGetSelfNationRes();
        if (knrr == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.KFGZ_9);
        }
        final KfgzPlayerFinalReward kpfr = this.kfgzPlayerFinalRewardDao.safeGetKfgzPlayerFinalReward(player.playerId, this.seasonInfo.getSeasonId(), knrr.getSelfCity());
        final int times = kpfr.getRewardTimesById(id);
        if (times >= 4) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.KFGZ_11);
        }
        final String endRewardString = karr.getEndRewardString();
        final int t = this.getFinalRewardTicketsByIdAndScore(endRewardString, id, kpfr.getNationScore());
        if (t <= 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.KFGZ_12);
        }
        final int tickets = t * KfgzMatchService.getXsByRewardTimes(times);
        final int gold = KfgzMatchService.getTicketsGold(tickets, times);
        final Player p = this.playerDao.read(player.playerId);
        if (!this.playerDao.consumeGold(p, gold, "\u8de8\u670d\u56fd\u6218\u70b9\u5238\u6d88\u8017\u91d1\u5e01")) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10003);
        }
        final StringBuffer sb = new StringBuffer();
        int i = 0;
        char[] charArray;
        for (int length = (charArray = kpfr.getRewardTimes().toCharArray()).length, j = 0; j < length; ++j) {
            final char c = charArray[j];
            if (++i == id) {
                sb.append(times + 1);
            }
            else {
                sb.append(c);
            }
        }
        this.kfgzPlayerFinalRewardDao.addGetFinalReward(player.playerId, knrr.getSeasonId(), sb.toString(), kpfr.getRewardTimes(), knrr.getSelfCity());
        this.playerTicketsDao.addTickets(player.playerId, tickets, LocalMessages.ATTRIBUTEKEY_TICKETS_1, true);
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Override
    public byte[] scheduleInfoList(final PlayerDto player) {
        if (this.kfgzAllRankRes[player.forceId] == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.KFGZ_9);
        }
        return JsonBuilder.getObjectJson(State.SUCCESS, this.getResult(player.forceId));
    }
    
    private byte[] getResult(final int nation) {
        final KfgzAllRankRes karr = this.kfgzAllRankRes[nation];
        final List<KfgzScheduleInfoRes> list = karr.safeGetKfgzScheduleInfoResList();
        final JsonDocument doc = new JsonDocument();
        int layer = 0;
        int group = 0;
        int round = 0;
        Date startTime = null;
        doc.startArray("scheduleInfo");
        for (final KfgzScheduleInfoRes k : list) {
            startTime = k.getBattleDate();
            doc.startObject();
            doc.createElement("serverName1", k.getServerName1());
            doc.createElement("nation1", k.getNation1());
            doc.createElement("rank1", k.getPos1());
            doc.createElement("serverName2", k.getServerName2());
            doc.createElement("nation2", k.getNation2());
            doc.createElement("rank2", k.getPos2());
            if ((k.getGameServer1().equals(karr.getGameServer()) && nation == k.getNation1()) || (k.getGameServer2().equals(karr.getGameServer()) && nation == k.getNation2())) {
                doc.createElement("self", 1);
            }
            doc.endObject();
            if (layer == 0) {
                layer = k.getLayerId();
            }
            if (group == 0) {
                group = k.getGId();
            }
            if (round == 0) {
                round = k.getRound();
            }
        }
        doc.endArray();
        doc.createElement("layer", layer);
        doc.createElement("group", group);
        doc.createElement("round", round);
        if (startTime != null) {
            doc.createElement("startTime", startTime.getTime());
            doc.createElement("remainTime", startTime.getTime() - System.currentTimeMillis());
            final long battleLast = KfgzCommConstants.getBattleTimeByRuleBattleTime(this.seasonInfo.getRuleBattleTime());
            doc.createElement("endTime", startTime.getTime() + battleLast);
        }
        return doc.toByte();
    }
    
    @Transactional
    @Override
    public KfgzSignup requestCidFromGW(final int playerId) {
        final Request request = new Request();
        request.setCommand(Command.KF_GETCID_FROM_GAME_SEVERR);
        request.setMessage(new Integer(playerId));
        Response response = null;
        try {
            response = KfgzSeasonService.connection.sendSyncAndGetResponse(request);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        if (response == null) {
            return null;
        }
        final int cId = (int)response.getMessage();
        final KfgzSignup result = new KfgzSignup();
        result.setCompetitorId(cId);
        result.setPlayerId(playerId);
        if (this.kfgzSignupDao.create(result) == 1) {
            KfzbManager.playerIdCIdMap.put(playerId, cId);
            KfzbManager.cIdPlayerIdMap.put(cId, playerId);
            return result;
        }
        return null;
    }
    
    @Override
    public boolean isInBattleDay(final Date now) {
        if (this.seasonInfo == null || this.noSchedule()) {
            return false;
        }
        final Calendar nowCal = Calendar.getInstance();
        nowCal.setTime(now);
        final Date nowDate = new Date();
        final long[] ll = KfgzCommConstants.getBattleDelayInfo(this.seasonInfo.getBattleDelayInfo());
        final long battleTime = KfgzCommConstants.getBattleTimeByRuleBattleTime(this.seasonInfo.getRuleBattleTime());
        final int battleMinute = (int)battleTime / 1000 / 60;
        for (int round = 1; round < ll.length; ++round) {
            final long ml = ll[round];
            final int minute2 = (int)(ml / 1000L / 60L);
            final Calendar calStart = Calendar.getInstance();
            calStart.setTime(this.seasonInfo.getFirstBattleTime());
            calStart.add(12, minute2);
            calStart.add(10, -2);
            final Date startDate = calStart.getTime();
            final Calendar calEnd = Calendar.getInstance();
            calEnd.setTime(this.seasonInfo.getFirstBattleTime());
            calEnd.add(12, minute2);
            calEnd.add(12, battleMinute);
            calEnd.add(10, 1);
            calEnd.add(12, 30);
            final Date endDate = calEnd.getTime();
            if (nowDate.after(startDate) && nowDate.before(endDate)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean isAfterLastBattle() {
        if (this.getMatchState() == 1) {
            final Date now = new Date();
            final Calendar cal = Calendar.getInstance();
            cal.setTime(this.seasonInfo.getFirstBattleTime());
            final long[] ll = KfgzCommConstants.getBattleDelayInfo(this.seasonInfo.getBattleDelayInfo());
            final int minute = (int)(KfgzCommConstants.getBattleTimeByRuleBattleTime(this.seasonInfo.getRuleBattleTime()) / 1000L / 60L);
            final long ml = ll[ll.length - 1];
            final int minute2 = (int)(ml / 1000L / 60L);
            final Calendar cal2 = Calendar.getInstance();
            cal2.setTime(this.seasonInfo.getFirstBattleTime());
            cal2.add(12, minute2);
            cal2.add(12, minute);
            if (now.after(cal2.getTime())) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public int getMatchStateForQianduan() {
        final int result = this.getMatchState();
        if (result == 1 && new Date().before(this.seasonInfo.getFirstBattleTime())) {
            return 3;
        }
        return result;
    }
    
    @Override
    public int getMatchState() {
        if (this.seasonInfo == null || this.noSchedule()) {
            return 0;
        }
        if (this.seasonInfo.getState() == 4) {
            return 0;
        }
        if (this.seasonInfo.getState() != 2) {
            return 0;
        }
        final int minute = (int)(KfgzCommConstants.getBattleTimeByRuleBattleTime(this.seasonInfo.getRuleBattleTime()) / 1000L / 60L);
        final Calendar now = Calendar.getInstance();
        final Calendar cal = Calendar.getInstance();
        cal.setTime(this.seasonInfo.getFirstBattleTime());
        final Calendar calEnd = Calendar.getInstance();
        calEnd.setTime(cal.getTime());
        calEnd.add(12, minute);
        if (now.after(cal) && now.before(calEnd)) {
            return 2;
        }
        final long[] ll = KfgzCommConstants.getBattleDelayInfo(this.seasonInfo.getBattleDelayInfo());
        for (int round = 1; round < ll.length; ++round) {
            final long ml = ll[round];
            final int minute2 = (int)(ml / 1000L / 60L);
            final Calendar cal2 = Calendar.getInstance();
            cal2.setTime(this.seasonInfo.getFirstBattleTime());
            cal2.add(12, minute2);
            final Calendar calEnd2 = Calendar.getInstance();
            calEnd2.setTime(cal2.getTime());
            calEnd2.add(12, minute);
            if (now.after(cal2) && now.before(calEnd2)) {
                return 2;
            }
        }
        return 1;
    }
    
    private boolean noSchedule() {
        final boolean scheduled = true;
        KfgzScheduleInfoRes[] scheduleInfoList;
        for (int length = (scheduleInfoList = this.scheduleInfoList).length, i = 0; i < length; ++i) {
            final KfgzScheduleInfoRes scheInfo = scheduleInfoList[i];
            if (scheInfo != null) {
                return false;
            }
        }
        return scheduled;
    }
    
    private void requestScheduleInfoFromGW(final boolean changeSeason) {
        final Request request = new Request();
        request.setCommand(Command.KFGZ_GW_GETSCHEDULEINFO_FROM_GAME_SERVER);
        final GameServerEntity gse = new GameServerEntity();
        gse.setServerKey(Configuration.getProperty(Configuration.SERVER_KEY));
        request.setMessage(gse);
        Response response = null;
        try {
            response = KfgzSeasonService.connection.sendSyncAndGetResponse(request);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        final KfgzScheduleInfoList kfil = (KfgzScheduleInfoList)response.getMessage();
        if (kfil == null || kfil.getList() == null) {
            return;
        }
        this.scheduleInfoLock.lock();
        try {
            final int chatSeasonId = this.seasonInfo.getSeasonId();
            for (final KfgzScheduleInfoRes k : kfil.getList()) {
                for (final int n : k.getMyNations(kfil.getGameServer())) {
                    if (!k.equals(this.scheduleInfoList[n])) {
                        this.scheduleInfoList[n] = k;
                        this.kfgzMatchService.init(k.getMatchAddress(), n);
                        if (k.getRound() == 1 && this.firstBattleMarks[n]) {
                            this.doRequestKfgzAllRankRes(n, true);
                        }
                        final Date now = new Date();
                        final Tuple<String, Integer> other = (Tuple<String, Integer>)k.getOtherServerName(kfil.getGameServer(), n);
                        String con;
                        if (other.left == null) {
                            con = LocalMessages.KFGZ_17;
                        }
                        else {
                            final String otherNation = (other.right == null) ? "" : WorldCityCommon.nationIdNameMap.get(other.right);
                            con = MessageFormatter.format(LocalMessages.KFGZ_3, new Object[] { other.left, ColorUtil.getForceMsg((int)other.right, otherNation) });
                        }
                        final String content = con;
                        if (k.getBattleDate().after(now)) {
                            KfwdMatchService.getExecutor().schedule(new Runnable() {
                                @Override
                                public void run() {
                                    if (KfgzSeasonService.this.seasonInfo == null || KfgzSeasonService.this.seasonInfo.getSeasonId() != chatSeasonId) {
                                        return;
                                    }
                                    KfgzSeasonService.this.dataGetter.getChatService().sendSystemChat("GLOBAL", 0, n, content, null);
                                    final JsonDocument doc = new JsonDocument();
                                    doc.startObject();
                                    doc.createElement("kfgzState", 2);
                                    doc.endObject();
                                    Players.pushToALL(PushCommand.PUSH_UPDATE, doc.toByte());
                                }
                            }, k.getBattleDate().getTime() - now.getTime(), TimeUnit.MILLISECONDS);
                        }
                        else {
                            this.dataGetter.getChatService().sendSystemChat("GLOBAL", 0, n, content, null);
                            final JsonDocument doc = new JsonDocument();
                            doc.startObject();
                            doc.createElement("kfgzState", 2);
                            doc.endObject();
                            Players.pushToALL(PushCommand.PUSH_UPDATE, doc.toByte());
                        }
                    }
                }
            }
            if (changeSeason && kfil.getList().size() > 0) {
                final JsonDocument doc2 = new JsonDocument();
                doc2.startObject();
                doc2.createElement("kfgzState", this.getMatchState());
                doc2.endObject();
                Players.pushToALL(PushCommand.PUSH_UPDATE, doc2.toByte());
                this.setChatMessage();
            }
        }
        finally {
            this.scheduleInfoLock.unlock();
        }
        this.scheduleInfoLock.unlock();
    }
    
    private void doChatKfgz(final int round, final Calendar cal, final Date now) {
        final String chat = MessageFormatter.format(LocalMessages.KFGZ_1, new Object[] { DateUtil.formatDate(cal.getTime(), "yyyy-MM-dd HH:mm:ss") });
        this.dataGetter.getChatService().sendSystemChat("GLOBAL", 0, 0, chat, null);
        final Date startTime = cal.getTime();
        cal.add(12, -65);
        if (now.after(cal.getTime())) {
            final String content = MessageFormatter.format(LocalMessages.KFGZ_2, new Object[] { DateUtil.formatDate(startTime, "yyyy-MM-dd HH:mm:ss"), round });
            for (int i = 15; i < 60; i += 15) {
                cal.setTime(startTime);
                cal.add(12, -i);
                KfwdMatchService.getExecutor().schedule(new Runnable() {
                    @Override
                    public void run() {
                        KfgzSeasonService.this.dataGetter.getChatService().sendSystemChat("GLOBAL", 0, 0, content, null);
                    }
                }, cal.getTime().getTime() - now.getTime(), TimeUnit.MILLISECONDS);
            }
        }
    }
    
    @Override
    public void chatKfgz() {
        final long start = System.currentTimeMillis();
        KfgzSeasonService.timerLog.info(LogUtil.formatThreadLog("KfgzSeasonService", "chatKfgz", 0, 0L, ""));
        if (this.seasonInfo == null || this.getMatchState() != 1) {
            KfgzSeasonService.timerLog.info(LogUtil.formatThreadLog("KfgzSeasonService", "chatKfgz", 2, System.currentTimeMillis() - start, ""));
            return;
        }
        final Date now = new Date();
        final Calendar cal = Calendar.getInstance();
        final long[] ll = KfgzCommConstants.getBattleDelayInfo(this.seasonInfo.getBattleDelayInfo());
        for (int round = 1; round < ll.length; ++round) {
            final long ml = ll[round];
            final int minute = (int)(ml / 1000L / 60L);
            cal.setTime(this.seasonInfo.getFirstBattleTime());
            cal.add(12, minute);
            if (now.before(cal.getTime())) {
                this.doChatKfgz(round, cal, now);
                break;
            }
        }
        KfgzSeasonService.timerLog.info(LogUtil.formatThreadLog("KfgzSeasonService", "chatKfgz", 2, System.currentTimeMillis() - start, ""));
    }
    
    private void setChatMessage() {
        if (this.seasonInfo == null) {
            return;
        }
        final Calendar cal = Calendar.getInstance();
        cal.setTime(this.seasonInfo.getFirstBattleTime());
        final int battleLastMinute = (int)(KfgzCommConstants.getBattleTimeByRuleBattleTime(this.seasonInfo.getRuleBattleTime()) / 1000L / 60L);
        final int chatSeasonId = this.seasonInfo.getSeasonId();
        final Date now = new Date();
        final long[] ll = KfgzCommConstants.getBattleDelayInfo(this.seasonInfo.getBattleDelayInfo());
        for (int round = 1; round < ll.length; ++round) {
            final long ml = ll[round];
            final int minute = (int)(ml / 1000L / 60L);
            cal.setTime(this.seasonInfo.getFirstBattleTime());
            cal.add(12, minute);
            cal.add(12, battleLastMinute);
            final String content = MessageFormatter.format(LocalMessages.KFGZ_4, new Object[] { round });
            KfwdMatchService.getExecutor().schedule(new Runnable() {
                @Override
                public void run() {
                    if (KfgzSeasonService.this.seasonInfo == null || KfgzSeasonService.this.seasonInfo.getSeasonId() != chatSeasonId) {
                        return;
                    }
                    KfgzSeasonService.this.dataGetter.getChatService().sendSystemChat("GLOBAL", 0, 0, content, null);
                    final JsonDocument doc = new JsonDocument();
                    doc.startObject();
                    doc.createElement("kfgzState", 1);
                    doc.endObject();
                    Players.pushToALL(PushCommand.PUSH_UPDATE, doc.toByte());
                }
            }, cal.getTime().getTime() - now.getTime(), TimeUnit.MILLISECONDS);
        }
        cal.add(13, 3);
        KfwdMatchService.getExecutor().schedule(new Runnable() {
            @Override
            public void run() {
                if (KfgzSeasonService.this.seasonInfo == null || KfgzSeasonService.this.seasonInfo.getSeasonId() != chatSeasonId) {
                    return;
                }
                KfgzSeasonService.this.dataGetter.getChatService().sendSystemChat("GLOBAL", 0, 0, LocalMessages.KFGZ_5, null);
            }
        }, cal.getTime().getTime() - now.getTime(), TimeUnit.MILLISECONDS);
    }
    
    @Override
    public byte[] getKfgzAllRankRes(final PlayerDto player) {
        final KfgzAllRankRes kark = this.kfgzAllRankRes[player.forceId];
        if (kark == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.KFGZ_9);
        }
        final JsonDocument doc = new JsonDocument();
        doc.createElement("layer", KfgzCommConstants.getLayerByGzID(kark.getGzId()));
        doc.createElement("group", KfgzCommConstants.getGIdByGzID(kark.getGzId()));
        doc.createElement("round", KfgzCommConstants.getRoundByGzId(kark.getGzId()));
        int i = 0;
        doc.startArray("groupNationRes");
        for (final KfgzNationResultReq knrr : kark.getGroupNationRes()) {
            ++i;
            doc.startObject();
            doc.createElement("id", i);
            doc.createElement("serverName", knrr.getServerName());
            doc.createElement("nation", knrr.getNation());
            doc.createElement("round", knrr.getRound());
            doc.createElement("selfCity", knrr.getSelfCity());
            doc.createElement("oppCity", knrr.getOppCity());
            doc.createElement("firstKillerName", knrr.getFirstKillerName());
            doc.createElement("firstkillArmy", knrr.getFirstkillArmy());
            doc.endObject();
        }
        doc.endArray();
        i = 0;
        doc.startArray("layerNationRes");
        for (final KfgzNationResultReq knrr : kark.getLayerNationRes()) {
            ++i;
            doc.startObject();
            doc.createElement("id", i);
            doc.createElement("serverName", knrr.getServerName());
            doc.createElement("nation", knrr.getNation());
            doc.createElement("round", knrr.getRound());
            doc.createElement("selfCity", knrr.getSelfCity());
            doc.createElement("oppCity", knrr.getOppCity());
            doc.createElement("firstKillerName", knrr.getFirstKillerName());
            doc.createElement("firstkillArmy", knrr.getFirstkillArmy());
            doc.createElement("gId", knrr.getgId());
            doc.endObject();
        }
        doc.endArray();
        i = 0;
        doc.startArray("groupKillArmy");
        for (final KfgzPlayerRankingInfoReq knrr2 : kark.getGroupKillArmyRes()) {
            ++i;
            doc.startObject();
            doc.createElement("id", i);
            doc.createElement("serverName", knrr2.getServerName());
            doc.createElement("serverId", knrr2.getServerId());
            doc.createElement("nation", knrr2.getNation());
            doc.createElement("playerName", knrr2.getPlayerName());
            doc.createElement("playerLv", knrr2.getPlayerLv());
            doc.createElement("killArmy", knrr2.getKillArmy());
            doc.endObject();
        }
        doc.endArray();
        i = 0;
        doc.startArray("layerKillArmy");
        for (final KfgzPlayerRankingInfoReq knrr2 : kark.getLayerKillArmyRes()) {
            ++i;
            doc.startObject();
            doc.createElement("id", i);
            doc.createElement("serverName", knrr2.getServerName());
            doc.createElement("serverId", knrr2.getServerId());
            doc.createElement("nation", knrr2.getNation());
            doc.createElement("playerName", knrr2.getPlayerName());
            doc.createElement("playerLv", knrr2.getPlayerLv());
            doc.createElement("killArmy", knrr2.getKillArmy());
            doc.createElement("gId", knrr2.getgId());
            doc.endObject();
        }
        doc.endArray();
        i = 0;
        doc.startArray("groupSolo");
        for (final KfgzPlayerRankingInfoReq knrr2 : kark.getGroupSoloRes()) {
            ++i;
            doc.startObject();
            doc.createElement("id", i);
            doc.createElement("serverName", knrr2.getServerName());
            doc.createElement("serverId", knrr2.getServerId());
            doc.createElement("nation", knrr2.getNation());
            doc.createElement("playerName", knrr2.getPlayerName());
            doc.createElement("playerLv", knrr2.getPlayerLv());
            doc.createElement("soloNum", knrr2.getSoloNum());
            doc.endObject();
        }
        doc.endArray();
        i = 0;
        doc.startArray("layerSolo");
        for (final KfgzPlayerRankingInfoReq knrr2 : kark.getLayerSoloRes()) {
            ++i;
            doc.startObject();
            doc.createElement("id", i);
            doc.createElement("serverName", knrr2.getServerName());
            doc.createElement("serverId", knrr2.getServerId());
            doc.createElement("nation", knrr2.getNation());
            doc.createElement("playerName", knrr2.getPlayerName());
            doc.createElement("playerLv", knrr2.getPlayerLv());
            doc.createElement("soloNum", knrr2.getSoloNum());
            doc.createElement("gId", knrr2.getgId());
            doc.endObject();
        }
        doc.endArray();
        i = 0;
        doc.startArray("groupOccupyCity");
        for (final KfgzPlayerRankingInfoReq knrr2 : kark.getGroupOccupyCityRes()) {
            ++i;
            doc.startObject();
            doc.createElement("id", i);
            doc.createElement("serverName", knrr2.getServerName());
            doc.createElement("serverId", knrr2.getServerId());
            doc.createElement("nation", knrr2.getNation());
            doc.createElement("playerName", knrr2.getPlayerName());
            doc.createElement("playerLv", knrr2.getPlayerLv());
            doc.createElement("occupyCity", knrr2.getOccupyCity());
            doc.endObject();
        }
        doc.endArray();
        i = 0;
        doc.startArray("layerOccupyCity");
        for (final KfgzPlayerRankingInfoReq knrr2 : kark.getLayerOccupyCityRes()) {
            ++i;
            doc.startObject();
            doc.createElement("id", i);
            doc.createElement("serverName", knrr2.getServerName());
            doc.createElement("serverId", knrr2.getServerId());
            doc.createElement("nation", knrr2.getNation());
            doc.createElement("playerName", knrr2.getPlayerName());
            doc.createElement("playerLv", knrr2.getPlayerLv());
            doc.createElement("occupyCity", knrr2.getOccupyCity());
            doc.createElement("gId", knrr2.getgId());
            doc.endObject();
        }
        doc.endArray();
        return JsonBuilder.getObjectJson(State.SUCCESS, doc.toByte());
    }
    
    private void doRequestKfgzAllRankRes(final int nation, final boolean init) {
        if (this.kfgzAllRankRes[nation] != null && this.kfgzAllRankRes[nation].getGzId() == this.getGzIdByNation(nation) && this.kfgzAllRankRes[nation].getState() == 1) {
            return;
        }
        if (init && this.kfgzAllRankRes[nation] != null && this.kfgzAllRankRes[nation].getState() == 4) {
            return;
        }
        final Request request = new Request();
        request.setCommand(Command.KFGZ_GW_GETBATTLERRANKING_FROM_GAME_SERVER);
        final kfgzNationGzKey gzKey = new kfgzNationGzKey();
        gzKey.setIni(init);
        gzKey.setGzId(this.getGzIdByNation(nation));
        gzKey.setGameServer(Configuration.getProperty(Configuration.SERVER_KEY));
        gzKey.setNation(nation);
        gzKey.setSeasonId(this.getSeasonId());
        request.setMessage(gzKey);
        Response response = null;
        try {
            response = KfgzSeasonService.connection.sendSyncAndGetResponse(request);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        if (response == null) {
            return;
        }
        final KfgzAllRankRes karr = (KfgzAllRankRes)response.getMessage();
        if (karr == null || (karr.getState() != 1 && karr.getState() != 4)) {
            return;
        }
        this.kfgzAllRankRes[nation] = karr;
        this.firstBattleMarks[nation] = false;
    }
    
    @Override
    public void requestKfgzAllRankRes(final int nation) {
        this.doRequestKfgzAllRankRes(nation, false);
    }
    
    private void requestSeasonInfoFromGW() {
        final Request request = new Request();
        request.setCommand(Command.KFGZ_GW_GETSEASONINFO_FROM_GAME_SERVER);
        final GameServerEntity gse = new GameServerEntity();
        gse.setServerKey(Configuration.getProperty(Configuration.SERVER_KEY));
        request.setMessage(gse);
        Response response = null;
        try {
            response = KfgzSeasonService.connection.sendSyncAndGetResponse(request);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        if (response == null) {
            return;
        }
        final KfgzSeasonInfoRes ksi = (KfgzSeasonInfoRes)response.getMessage();
        if (ksi == null) {
            return;
        }
        this.seasonLock.lock();
        try {
            if (!ksi.equals(this.seasonInfo)) {
                if (this.seasonInfo == null || this.seasonInfo.getSeasonId() != ksi.getSeasonId() || ksi.getState() == 2) {
                    boolean changeSeason = false;
                    if (this.seasonInfo == null || this.seasonInfo.getSeasonId() != ksi.getSeasonId()) {
                        this.seasonInfo = ksi;
                        this.kfgzAllRankRes = new KfgzAllRankRes[4];
                        KfgzManager.clear();
                        this.firstBattleMarks = new boolean[] { false, true, true, true };
                        KfwdMatchService.kfgzTitleMap.clear();
                        this.scheduleInfoList = new KfgzScheduleInfoRes[4];
                        KfgzSeasonService.logger.info("change season to " + ksi.getSeasonId());
                        changeSeason = true;
                    }
                    else {
                        this.seasonInfo = ksi;
                    }
                    this.requestScheduleInfoFromGW(changeSeason);
                }
                else if (this.seasonInfo.getState() == 2 && ksi.getState() == 3) {
                    this.self.issueFinalReward(this.seasonInfo.getSeasonId());
                    this.seasonInfo = ksi;
                    this.kfgzAllRankRes = new KfgzAllRankRes[4];
                    final JsonDocument doc = new JsonDocument();
                    doc.startObject();
                    doc.createElement("kfgzState", 0);
                    doc.endObject();
                    Players.pushToALL(PushCommand.PUSH_UPDATE, doc.toByte());
                    KfgzSeasonService.logger.info("end season " + ksi.getSeasonId());
                }
                else if (ksi.getState() == 4) {
                    this.kfgzMatchService.stopMatchService();
                }
            }
        }
        finally {
            this.seasonLock.unlock();
        }
        this.seasonLock.unlock();
    }
    
    private int getFinalRewardTicketsByIdAndScore(final String finalRewardString, final int id, final int score) {
        final String fs = finalRewardString.split(",")[id - 1];
        final int s = Integer.valueOf(fs.split(":")[0]);
        final int t = Integer.valueOf(fs.split(":")[1]);
        if (s > score) {
            return 0;
        }
        return t;
    }
    
    private String getIssueRewardTimesString(final String rewardTimes) {
        final StringBuffer sb = new StringBuffer();
        char[] charArray;
        for (int length = (charArray = rewardTimes.toCharArray()).length, i = 0; i < length; ++i) {
            final char c = charArray[i];
            if (c - '0' == '\0') {
                sb.append(1);
            }
            else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
    
    @Transactional
    @Override
    public void issueFinalReward(final int seasonId) {
        for (int nation = 1; nation < 4; ++nation) {
            this.kfgzMatchService.issueRoundReward(seasonId, nation);
            final KfgzNationResultReq knrr = this.kfgzAllRankRes[nation].safeGetSelfNationRes();
            if (knrr.getPos() <= this.kfgzAllRankRes[nation].getUpDownInfo()[0]) {
                final String nationName = WorldCityCommon.nationIdNameMap.get(nation);
                final String content = MessageFormatter.format(LocalMessages.KFGZ_6, new Object[] { nationName, this.kfgzAllRankRes[nation].getLayerNameArray()[knrr.getLayerId() + 1] });
                this.dataGetter.getChatService().sendSystemChat("GLOBAL", 0, 0, content, null);
            }
            else if (knrr.getPos() > this.kfgzAllRankRes[nation].getUpDownInfo()[1]) {
                final String content2 = MessageFormatter.format(LocalMessages.KFGZ_7, new Object[] { this.kfgzAllRankRes[nation].getLayerNameArray()[knrr.getLayerId() - 1] });
                this.dataGetter.getChatService().sendSystemChat("GLOBAL", 0, nation, content2, null);
            }
            final KfgzTitle kt = new KfgzTitle();
            kt.setKfgzSeasonId(knrr.getSeasonId());
            kt.setPlayerName(knrr.getFirstKillerName());
            kt.setTitle(LocalMessages.TITLE_KFGZ_1);
            this.kfgzTitleDao.create(kt);
            KfwdMatchService.kfgzTitleMap.put(knrr.getFirstKillerName(), LocalMessages.TITLE_KFGZ_1);
            final String content = MessageFormatter.format(LocalMessages.TITLE_KFGZ_2, new Object[] { LocalMessages.TITLE_KFGZ_1 });
            if (knrr.getFirstKillerName() != null) {
                final Player toPlayer = this.playerDao.getPlayerByName(knrr.getFirstKillerName());
                if (toPlayer != null) {
                    this.dataGetter.getMailService().writeSystemMail(LocalMessages.T_MAIL_ROLE_SYSTEM, LocalMessages.TITLE_1, content, 1, toPlayer.getPlayerId(), 0);
                }
            }
        }
        final String finalRewardString = this.kfgzAllRankRes[1].getEndRewardString();
        final List<KfgzPlayerFinalReward> list = this.kfgzPlayerFinalRewardDao.getBySeasonId(seasonId);
        for (final KfgzPlayerFinalReward kpfr : list) {
            int tickets = 0;
            for (int i = 1; i <= 4; ++i) {
                if (kpfr.getRewardTimesById(i) == 0) {
                    final int t = this.getFinalRewardTicketsByIdAndScore(finalRewardString, i, kpfr.getNationScore());
                    if (t > 0) {
                        tickets += t;
                    }
                }
            }
            if (tickets > 0) {
                this.playerTicketsDao.addTickets(kpfr.getPlayerId(), tickets, LocalMessages.ATTRIBUTEKEY_TICKETS_1, true);
                this.kfgzPlayerFinalRewardDao.addGetFinalReward(kpfr.getPlayerId(), kpfr.getSeasonId(), this.getIssueRewardTimesString(kpfr.getRewardTimes()), kpfr.getRewardTimes(), kpfr.getNationScore());
                final String content3 = MessageFormatter.format(LocalMessages.KFGZ_14, new Object[] { tickets });
                this.dataGetter.getMailService().writeSystemMail(LocalMessages.T_MAIL_ROLE_SYSTEM, LocalMessages.KFGZ_13, content3, 1, kpfr.getPlayerId(), 0);
            }
        }
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        this.self = (IKfgzSeasonService)this.context.getBean("kfgzSeasonService");
    }
    
    @Override
    public void run() {
        while (true) {
            try {
                this.requestSeasonInfoFromGW();
                if (this.seasonInfo != null && this.seasonInfo.getState() == 2) {
                    this.requestScheduleInfoFromGW(false);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                try {
                    if (this.seasonInfo != null && this.seasonInfo.getState() == 2) {
                        Thread.sleep(10000L);
                    }
                    else {
                        Thread.sleep(30000L);
                    }
                }
                catch (InterruptedException e2) {
                    e2.printStackTrace();
                }
                ThreadLocalFactory.clearTreadLocalLog();
                ThreadLocalFactory.getTreadLocalLog();
                continue;
            }
            finally {
                try {
                    if (this.seasonInfo != null && this.seasonInfo.getState() == 2) {
                        Thread.sleep(10000L);
                    }
                    else {
                        Thread.sleep(30000L);
                    }
                }
                catch (InterruptedException e2) {
                    e2.printStackTrace();
                }
                ThreadLocalFactory.clearTreadLocalLog();
                ThreadLocalFactory.getTreadLocalLog();
            }
            try {
                if (this.seasonInfo != null && this.seasonInfo.getState() == 2) {
                    Thread.sleep(10000L);
                }
                else {
                    Thread.sleep(30000L);
                }
            }
            catch (InterruptedException e2) {
                e2.printStackTrace();
            }
            ThreadLocalFactory.clearTreadLocalLog();
            ThreadLocalFactory.getTreadLocalLog();
        }
    }
}
