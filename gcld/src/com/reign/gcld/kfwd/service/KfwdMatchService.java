package com.reign.gcld.kfwd.service;

import org.springframework.beans.factory.*;
import org.springframework.stereotype.*;
import org.apache.commons.logging.*;
import com.reign.kf.comm.transfer.oio.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.kfgz.dao.*;
import com.reign.gcld.player.dao.*;
import com.reign.gcld.kfwd.dao.*;
import com.reign.gcld.tickets.dao.*;
import com.reign.gcld.activity.dao.*;
import com.reign.gcld.activity.service.*;
import org.springframework.context.*;
import com.reign.gcld.sdata.cache.*;
import com.reign.gcld.log.*;
import com.reign.gcld.player.domain.*;
import com.reign.gcld.kfgz.domain.*;
import com.reign.gcld.activity.domain.*;
import com.reign.gcld.kfzb.domain.*;
import com.reign.gcld.battle.common.*;
import com.reign.gcld.chat.common.*;
import com.reign.gcld.player.dto.*;
import com.reign.gcld.kfwd.manager.*;
import com.reign.kfwd.constants.*;
import org.springframework.transaction.annotation.*;
import java.util.concurrent.*;
import com.reign.gcld.kfwd.common.transferconfig.*;
import com.reign.kf.comm.transfer.*;
import java.text.*;
import java.util.regex.*;
import com.reign.gcld.kfwd.domain.*;
import com.reign.kf.comm.protocol.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.gcld.general.domain.*;
import java.util.*;
import com.reign.kf.comm.entity.kfwd.request.*;
import com.reign.gcld.common.*;
import com.reign.framework.json.*;
import com.reign.kf.comm.entity.kfwd.response.*;
import com.reign.gcld.kfwd.dto.*;
import org.springframework.beans.*;
import com.reign.util.*;

@Component
public class KfwdMatchService implements IKfwdMatchService, InitializingBean, ResponseHandler, ApplicationContextAware
{
    private static Log logger;
    private static KfConnection connection;
    @Autowired
    private IDataGetter dataGetter;
    @Autowired
    private IKfgzTitleDao kfgzTitleDao;
    @Autowired
    private IKfgzPlayerRewardDao kfgzPlayerRewardDao;
    @Autowired
    private IPlayerDao playerDao;
    @Autowired
    private IKfwdSignupDao kfwdSignupDao;
    @Autowired
    private IKfwdRewardDao kfwdRewardDao;
    @Autowired
    private IKfwdRewardDoubleDao kfwdRewardDoubleDao;
    @Autowired
    private IPlayerTicketsDao playerTicketsDao;
    private IKfwdMatchService self;
    @Autowired
    private IPrivilege360Dao privilege360Dao;
    @Autowired
    private IActivityService activityService;
    ApplicationContext context;
    @Autowired
    private GeneralTreasureCache generalTreasureCache;
    @Autowired
    private KfwdMatchManager kfwdMatchManager;
    private static ScheduledExecutorService executor;
    public static volatile String[] top3PName;
    public static volatile int top3SeasonId;
    public static Map<String, String> nationKfwdTitleMap;
    public static Map<String, String> kfgzTitleMap;
    public static Map<String, String> kfzbTitleMap;
    public static int kfzbTitleSeasonId;
    public static Map<String, String> privilege360TitleMap;
    long lastNoticeAssignTime;
    public boolean hasNoticeSignFinish;
    boolean noticeEnd;
    public static boolean isfirstLoadWdTitle;
    public static List<KfwdRankTreasureInfo> treasureRewardInfo;
    public static final long BATTLE_PRAPARE_TIME_BASE = 600000L;
    public static final long BATTLE_BEGIN_FIHGT_NOTICE_INTERVAL = 60000L;
    public static final long BATTLE_PRAPARE_NOTICE_INTERVAL = 900000L;
    public static final long BATTLE_PRAPARE_TIME_TOTALTIME = 3600000L;
    public static final long MIN_MICSECEND = 60000L;
    public static final long SECOND_MICSECEND = 1000L;
    public static final long SECEND30_MICSECEND = 30000L;
    
    static {
        KfwdMatchService.logger = new KfwdMatchOperationLogger();
        KfwdMatchService.executor = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
        KfwdMatchService.top3PName = new String[] { "", "", "" };
        KfwdMatchService.top3SeasonId = 0;
        KfwdMatchService.nationKfwdTitleMap = new HashMap<String, String>();
        KfwdMatchService.kfgzTitleMap = new HashMap<String, String>();
        KfwdMatchService.kfzbTitleMap = new HashMap<String, String>();
        KfwdMatchService.kfzbTitleSeasonId = -1;
        KfwdMatchService.privilege360TitleMap = new HashMap<String, String>();
        KfwdMatchService.isfirstLoadWdTitle = false;
        KfwdMatchService.treasureRewardInfo = new ArrayList<KfwdRankTreasureInfo>();
    }
    
    public KfwdMatchService() {
        this.lastNoticeAssignTime = 0L;
        this.hasNoticeSignFinish = false;
        this.noticeEnd = false;
    }
    
    public static ScheduledExecutorService getExecutor() {
        return KfwdMatchService.executor;
    }
    
    public static String getTitleByPlayerName(final String playerName) {
        final int wdPos = getWinPos(playerName);
        final String nationWdTitle = KfwdMatchService.nationKfwdTitleMap.get(playerName);
        final String gzTitle = KfwdMatchService.kfgzTitleMap.get(playerName);
        final String privi360Title = KfwdMatchService.privilege360TitleMap.get(playerName);
        final String kfzbTitle = KfwdMatchService.kfzbTitleMap.get(playerName);
        if (kfzbTitle != null) {
            return kfzbTitle;
        }
        if (wdPos > 0) {
            return getKfwdTitleByPos(wdPos);
        }
        if (gzTitle != null) {
            return gzTitle;
        }
        if (nationWdTitle != null) {
            return nationWdTitle;
        }
        if (privi360Title != null) {
            return privi360Title;
        }
        return null;
    }
    
    private static String getKfwdTitleByPos(final int pos) {
        switch (pos) {
            case 1: {
                return LocalMessages.TITLE_KFWD_1;
            }
            case 2: {
                return LocalMessages.TITLE_KFWD_2;
            }
            case 3: {
                return LocalMessages.TITLE_KFWD_3;
            }
            default: {
                return null;
            }
        }
    }
    
    private void loadKfwdTitle() {
        final Integer seasonId = this.kfwdRewardDao.getMaxSeasonId();
        if (seasonId != null) {
            final List<KfwdReward> rlist = this.kfwdRewardDao.getRewardBySeasonId(seasonId);
            final Map<Integer, List<KfwdReward>> nationList = new HashMap<Integer, List<KfwdReward>>();
            for (int n = 1; n < 4; ++n) {
                final List<KfwdReward> list = new ArrayList<KfwdReward>();
                nationList.put(n, list);
            }
            for (final KfwdReward r : rlist) {
                final int d3Rank = r.getDay3Ranking();
                final Player player = this.dataGetter.getPlayerDao().read(r.getPlayerId());
                if (player != null && d3Rank > 0) {
                    nationList.get(player.getForceId()).add(r);
                }
                if (d3Rank <= 3 && d3Rank >= 1 && player != null) {
                    KfwdMatchService.top3PName[d3Rank - 1] = player.getPlayerName();
                }
            }
            KfwdMatchService.top3SeasonId = seasonId;
            final ComparatorForTitle cft = new ComparatorForTitle((ComparatorForTitle)null);
            for (int n2 = 1; n2 < 4; ++n2) {
                Collections.sort(nationList.get(n2), cft);
                if (nationList.get(n2).size() > 0) {
                    final Player player2 = this.dataGetter.getPlayerDao().read(nationList.get(n2).get(0).getPlayerId());
                    KfwdMatchService.nationKfwdTitleMap.put(player2.getPlayerName(), LocalMessages.TITLE_KFWD_4);
                }
                if (nationList.get(n2).size() > 1) {
                    final Player player2 = this.dataGetter.getPlayerDao().read(nationList.get(n2).get(1).getPlayerId());
                    KfwdMatchService.nationKfwdTitleMap.put(player2.getPlayerName(), LocalMessages.TITLE_KFWD_5);
                }
                if (nationList.get(n2).size() > 2) {
                    final Player player2 = this.dataGetter.getPlayerDao().read(nationList.get(n2).get(2).getPlayerId());
                    KfwdMatchService.nationKfwdTitleMap.put(player2.getPlayerName(), LocalMessages.TITLE_KFWD_6);
                }
            }
        }
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        this.self = (IKfwdMatchService)this.context.getBean("kfwdMatchService");
    }
    
    @Override
    public void ini() {
        this.loadKfwdTitle();
        final int kfgzSeasonId = this.kfgzPlayerRewardDao.getMaxSeasonId();
        final List<KfgzTitle> kfgzTitleList = this.kfgzTitleDao.getKfgzTitleListBySeasonId(kfgzSeasonId);
        for (final KfgzTitle kt : kfgzTitleList) {
            KfwdMatchService.kfgzTitleMap.put(kt.getPlayerName(), kt.getTitle());
        }
        final List<Privilege360> titleList = this.privilege360Dao.getTitleList();
        for (final Privilege360 temp : titleList) {
            final String playerName = this.playerDao.getPlayerName(temp.getPlayerId());
            KfwdMatchService.privilege360TitleMap.put(playerName, temp.getTitle());
        }
        this.loadKfzbTitle();
    }
    
    @Override
    public List<KfzbReward> loadKfzbTitle() {
        final List<KfzbReward> titleList = new ArrayList<KfzbReward>();
        try {
            final int kfzbSeasonId = this.dataGetter.getKfzbRewardDao().getMaxSeasonId();
            final List<KfzbReward> kfzbRewardList = this.dataGetter.getKfzbRewardDao().getHaveTitleBySeasonId(kfzbSeasonId);
            if (kfzbRewardList != null && kfzbRewardList.size() > 0) {
                for (final KfzbReward kfzbReward : kfzbRewardList) {
                    final int playerId = kfzbReward.getPlayerId();
                    if (kfzbReward.getTitle() == null) {
                        ErrorSceneLog.getInstance().appendErrorMsg("title is null").appendPlayerId(playerId).append("kfzbSeasonId", kfzbSeasonId).appendClassName(this.getClass().getSimpleName()).appendMethodName("ini").flush();
                    }
                    else {
                        final Player player = this.dataGetter.getPlayerDao().read(playerId);
                        if (player == null) {
                            ErrorSceneLog.getInstance().appendErrorMsg("player == null").appendPlayerId(playerId).append("kfzbSeasonId", kfzbSeasonId).appendClassName(this.getClass().getSimpleName()).appendMethodName("ini").flush();
                        }
                        else {
                            titleList.add(kfzbReward);
                        }
                    }
                }
            }
            if (titleList.size() > KfwdMatchService.kfzbTitleMap.size()) {
                for (final KfzbReward kfzbReward : titleList) {
                    final Player player2 = this.dataGetter.getPlayerDao().read(kfzbReward.getPlayerId());
                    KfwdMatchService.kfzbTitleMap.put(player2.getPlayerName(), kfzbReward.getTitle());
                }
                KfwdMatchService.kfzbTitleSeasonId = kfzbSeasonId;
                return titleList;
            }
            return new ArrayList<KfzbReward>();
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("", e);
            return titleList;
        }
    }
    
    @Override
	public void handle(final Response response) {
        if (response.getCommand() == Command.KFWD_STATE) {
            final KfwdState wdState = (KfwdState)response.getMessage();
            if (wdState.getSeasonId() != this.kfwdMatchManager.getCurSeasonId()) {
                return;
            }
            this.kfwdMatchManager.processWdState(wdState);
            final int globalState = wdState.getGlobalState();
            if (globalState == 20) {
                final long nowTime = System.currentTimeMillis();
                if (this.lastNoticeAssignTime + 600000L < nowTime) {
                    this.noticeAllWdBegin();
                    final String content = MessageFormatter.format(LocalMessages.KFWD_START_NOTICE, new Object[0]);
                    this.dataGetter.getChatService().sendSystemChat("GLOBAL", 0, 0, content, null);
                    this.lastNoticeAssignTime = nowTime;
                }
            }
            else if (globalState == 30) {
                this.noticeAllWdSignFinish();
            }
            else if (globalState == 70) {
                this.doCheckAndSendEndNoticeAndMail();
            }
        }
        else if (response.getCommand() == Command.KFWD_RT_MATCH) {
            final KfwdRTMatchInfo rtInfo = (KfwdRTMatchInfo)response.getMessage();
        }
        else if (response.getCommand() == Command.KFWD_RT_MATCH_DISPLAY) {
            final KfwdRTDisPlayInfo rtDisPlayerInfo = (KfwdRTDisPlayInfo)response.getMessage();
        }
        else if (response.getCommand() == Command.KFWD_RT_RANKING_LIST) {
            final KfwdRankingListInfo rtDisPlayerInfo2 = (KfwdRankingListInfo)response.getMessage();
        }
        else if (response.getCommand() == Command.KFWD_GAMESERVERREWARDTICKETINFO) {
            final KfwdGameServerRewardInfo rewardInfo = (KfwdGameServerRewardInfo)response.getMessage();
            this.doProcessGameServerRewardInfo(rewardInfo);
        }
        else if (response.getCommand() == Command.KFWD_GAMESERVERDAYBATTLEENDNOTICE) {
            final KfwdDayBattleEndNotice noticeInfo = (KfwdDayBattleEndNotice)response.getMessage();
            this.doNoticeDayBattleEnd(noticeInfo);
        }
    }
    
    private void noticeAllWdSignFinish() {
        if (!this.hasNoticeSignFinish) {
            this.hasNoticeSignFinish = true;
            for (final Map.Entry<Integer, PlayerDto> entry : Players.playerMap.entrySet()) {
                final PlayerDto dto = entry.getValue();
                final int playerId = dto.playerId;
                final KfwdSignInfo signInfo = this.kfwdMatchManager.getSignInfoByPlayerId(playerId);
                if (signInfo == null) {
                    Players.push(dto.playerId, PushCommand.PUSH_UPDATE, JsonBuilder.getSimpleJson("inkfwd", 4));
                }
                else {
                    Players.push(dto.playerId, PushCommand.PUSH_UPDATE, JsonBuilder.getSimpleJson("inkfwd", 2));
                }
            }
        }
    }
    
    private void doNoticeDayBattleEnd(final KfwdDayBattleEndNotice noticeInfo) {
        if (noticeInfo != null) {
            final int day = noticeInfo.getDay();
            if (day == 1) {
                this.dataGetter.getChatService().sendSystemChat("GLOBAL", 0, 0, MessageFormat.format(LocalMessages.KFWD_DAY1END_NOTICE, this.getWdName()), null);
            }
            else if (day == 2) {
                this.dataGetter.getChatService().sendSystemChat("GLOBAL", 0, 0, MessageFormat.format(LocalMessages.KFWD_DAY2END_NOTICE, this.getWdName()), null);
            }
            else if (day == 3) {
                final List<KfwdPlayerInfo> list = noticeInfo.getList();
                String content = LocalMessages.KFWD_DAY3END_NOTICE;
                final String[] pName = { "", "", "" };
                final String[] pNation = { "", "", "" };
                final String[] pInfo = { "", "", "" };
                final String[] pServerInfo = { "", "", "" };
                int i = 0;
                final String weiColor = "<font color=\"#6EB4EE\">";
                final String suColor = "<font color=\"#EB9642\">";
                final String wuColor = "<font color=\"#88D442\">";
                final String colorEnd = "</font>";
                for (final KfwdPlayerInfo p : list) {
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
                    ++i;
                }
                content = MessageFormat.format(content, pServerInfo[0], pInfo[0], pServerInfo[1], pInfo[1], pServerInfo[2], pInfo[2]);
                this.dataGetter.getChatService().sendSystemChat("GLOBAL", 0, 0, content, null);
            }
        }
    }
    
    private void noticeAllWdBegin() {
        for (final Map.Entry<Integer, PlayerDto> entry : Players.playerMap.entrySet()) {
            final PlayerDto dto = entry.getValue();
            final JsonDocument doc = new JsonDocument();
            doc.startObject();
            doc.createElement("inkfwd", 1);
            final KfwdSeasonInfo seasonInfo = this.kfwdMatchManager.getSeasonInfo();
            if (seasonInfo != null && seasonInfo.getZb() == 1) {
                doc.createElement("zb", 1);
            }
            doc.endObject();
            Players.push(dto.playerId, PushCommand.PUSH_UPDATE, doc.toByte());
        }
    }
    
    private void doCheckAndSendEndNoticeAndMail() {
        if (!this.noticeEnd) {
            this.doAutoSendDay3RewardRankingReward(this.kfwdMatchManager.getCurWdState().getSeasonId());
            for (final Map.Entry<Integer, PlayerDto> entry : Players.playerMap.entrySet()) {
                final PlayerDto dto = entry.getValue();
                Players.push(dto.playerId, PushCommand.PUSH_UPDATE, JsonBuilder.getSimpleJson("inkfwd", 4));
            }
            this.noticeEnd = true;
        }
    }
    
    private void doProcessGameServerRewardInfo(final KfwdGameServerRewardInfo rewardInfo) {
        final List<KfwdTicketResultInfo> rewardList = rewardInfo.getList();
        if (rewardList == null || rewardList.size() <= 0) {
            return;
        }
        for (final KfwdTicketResultInfo kfwdTicketResultInfo : rewardList) {
            final Integer playerId = this.kfwdMatchManager.getPlayerInfoByCId(kfwdTicketResultInfo.getCompetitorId());
            if (playerId != null && playerId > 0) {
                try {
                    this.self.doProcessReward(playerId, kfwdTicketResultInfo);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        final KfwdTicketResultInfo firstResult = rewardInfo.getList().get(0);
        final String dayRanking = firstResult.getDayRanking();
        final int rank = KfwdReward.getDay3RankingByDayRanking(dayRanking);
        if (rank > 0) {
            this.loadKfwdTitle();
            if (KfwdMatchService.isfirstLoadWdTitle) {
                for (final Map.Entry<String, String> en : KfwdMatchService.nationKfwdTitleMap.entrySet()) {
                    if (getWinPos(en.getKey()) == 0) {
                        final String content = MessageFormatter.format(LocalMessages.TITLE_KFWD_8, new Object[] { en.getValue() });
                        final Player player = this.playerDao.getPlayerByName(en.getKey());
                        this.dataGetter.getMailService().writeSystemMail(LocalMessages.T_MAIL_ROLE_SYSTEM, LocalMessages.TITLE_1, content, 1, player.getPlayerId(), 0);
                    }
                }
            }
            KfwdMatchService.isfirstLoadWdTitle = false;
        }
    }
    
    public void doAutoSendDay3RewardRankingReward(final int seasonId) {
        final List<KfwdReward> kfwdRewardList = this.kfwdRewardDao.getRewardBySeasonId(seasonId);
        for (final KfwdReward kfwdReward : kfwdRewardList) {
            try {
                final int[] lastDayRankingInfo = kfwdReward.getLastDayRankingRewardInfo();
                if (lastDayRankingInfo[0] == 1 || lastDayRankingInfo[1] <= 0) {
                    continue;
                }
                this.self.addLastDayRankingReward(kfwdReward);
            }
            catch (Exception e) {
                KfwdMatchService.logger.error("", e);
            }
        }
    }
    
    @Transactional
    @Override
    public void addLastDayRankingReward(final KfwdReward kfwdReward) {
        final int[] lastDayRankingInfo = kfwdReward.getLastDayRankingRewardInfo();
        if (lastDayRankingInfo[0] != 1 && lastDayRankingInfo[1] > 0) {
            kfwdReward.setDayReward(KfwdConstantsAndMethod.addGetDayReward(KfwdConstantsAndMethod.MAXFIGHTDAY, kfwdReward.getDayReward()));
            kfwdReward.setTickets(kfwdReward.getTickets() + lastDayRankingInfo[1]);
            this.kfwdRewardDao.updateNewRewardInfo(kfwdReward);
            this.playerTicketsDao.addTickets(kfwdReward.getPlayerId(), lastDayRankingInfo[1], LocalMessages.ATTRIBUTEKEY_TICKETS_2, true);
            final String wdName = this.getWdName();
            String content = MessageFormatter.format(LocalMessages.KFWD_DAY_REWARD_MAIL, new Object[] { KfwdConstantsAndMethod.MAXFIGHTDAY, lastDayRankingInfo[1], wdName });
            this.dataGetter.getMailService().writeSystemMail(LocalMessages.T_MAIL_ROLE_SYSTEM, LocalMessages.KFWD_REWARD_MAIL_TITLE, content, 1, kfwdReward.getPlayerId(), 0);
            final int day3Ranking = kfwdReward.getDay3Ranking();
            final int hasGetReward = kfwdReward.getGetTreasure();
            if (hasGetReward > 0) {
                return;
            }
            KfwdRankTreasureInfo getTreasureInfo = null;
            for (final KfwdRankTreasureInfo tInfo : KfwdMatchService.treasureRewardInfo) {
                if (day3Ranking <= tInfo.getMaxRanking() && day3Ranking >= tInfo.getMinRanking()) {
                    getTreasureInfo = tInfo;
                }
            }
            if (getTreasureInfo != null) {
                final int[] res = getTreasureInfo.getRandomLeaAndStr();
                final GeneralTreasure generalTreasure = (GeneralTreasure)this.generalTreasureCache.get((Object)getTreasureInfo.getTid());
                final PlayerDto playerDto = new PlayerDto(kfwdReward.getPlayerId());
                this.dataGetter.getTreasureService().tryGetGeneralTreasure(playerDto, getTreasureInfo.getTid(), true, res[0], res[1], false, "\u8de8\u670d\u6b66\u6597\u5956\u52b1");
                content = MessageFormatter.format(LocalMessages.KFWD_SEND_TREASURE, new Object[] { generalTreasure.getName() });
                this.dataGetter.getMailService().writeSystemMail(LocalMessages.T_MAIL_ROLE_SYSTEM, LocalMessages.KFWD_REWARD_MAIL_TITLE, content, 1, kfwdReward.getPlayerId(), new Date());
                this.kfwdRewardDao.updateGetTreasure(kfwdReward);
            }
        }
    }
    
    @Transactional
    @Override
    public void doProcessReward(final Integer playerId, final KfwdTicketResultInfo kfwdTicketResultInfo) {
        KfwdReward kfwdReward = this.kfwdRewardDao.getRewardByPlayerIdAndSeasonId(playerId, kfwdTicketResultInfo.getSeasonId());
        String oldRewardInfo = "";
        if (kfwdReward == null) {
            kfwdReward = new KfwdReward();
            kfwdReward.setPlayerId(playerId);
            kfwdReward.setCid(kfwdTicketResultInfo.getCompetitorId());
            kfwdReward.setSeasonId(kfwdTicketResultInfo.getSeasonId());
            kfwdReward.setDayRanking(kfwdTicketResultInfo.getDayRanking());
            kfwdReward.setDayRewardTicket(kfwdTicketResultInfo.getDayTicket());
            final int pk = this.kfwdRewardDao.create(kfwdReward);
            kfwdReward.setPk(pk);
        }
        else {
            oldRewardInfo = kfwdReward.getRewardinfo();
        }
        final String newRewardInfo = kfwdTicketResultInfo.getRewardInfo();
        if (oldRewardInfo == null || !oldRewardInfo.equals(newRewardInfo)) {
            final List<Integer[]> resList = kfwdReward.setNewTicket(oldRewardInfo, newRewardInfo, kfwdTicketResultInfo.getWinRes());
            for (final Integer[] res : resList) {
                final int round = res[0];
                final int winState = res[1];
                final int ticket = res[2];
                this.playerTicketsDao.addTickets(playerId, ticket, LocalMessages.ATTRIBUTEKEY_TICKETS_2, true);
                String content = null;
                final String wdName = this.getWdName();
                if (winState == 1) {
                    content = MessageFormatter.format(LocalMessages.KFWD_REWARD_MAIL_WIN, new Object[] { round, ticket, wdName });
                }
                else if (winState == 2) {
                    content = MessageFormatter.format(LocalMessages.KFWD_REWARD_MAIL_LOST, new Object[] { round, ticket, wdName });
                }
                final String mTitle = MessageFormatter.format(LocalMessages.KFWD_ROUND_REWARD_MAIL_TITLE, new Object[] { wdName, round });
                if (content != null) {
                    this.dataGetter.getMailService().writeSystemMail(LocalMessages.T_MAIL_ROLE_SYSTEM, mTitle, content, 1, playerId, 0);
                    final String sendContent = content;
                    KfwdMatchService.executor.schedule(new Runnable() {
                        @Override
                        public void run() {
                            KfwdMatchService.this.dataGetter.getChatService().sendSystemChat("SYS2ONE", playerId, 0, sendContent, null);
                        }
                    }, 10000L, TimeUnit.MILLISECONDS);
                }
            }
        }
        final List<Integer[]> dayResList = kfwdReward.checkAndSetNewTicket(kfwdTicketResultInfo.getDayReward(), kfwdTicketResultInfo.getDayRanking(), kfwdTicketResultInfo.getDayTicket());
        for (final Integer[] r : dayResList) {
            final int day = r[0];
            final int ticket2 = r[1];
            this.playerTicketsDao.addTickets(playerId, ticket2, LocalMessages.ATTRIBUTEKEY_TICKETS_2, true);
            final String wdName2 = this.getWdName();
            final String content = MessageFormatter.format(LocalMessages.KFWD_DAY_REWARD_MAIL, new Object[] { day, ticket2, wdName2 });
            this.dataGetter.getMailService().writeSystemMail(LocalMessages.T_MAIL_ROLE_SYSTEM, LocalMessages.KFWD_REWARD_MAIL_TITLE, content, 1, playerId, 0);
        }
        kfwdReward.setDayRanking(kfwdTicketResultInfo.getDayRanking());
        kfwdReward.setDayRewardTicket(kfwdTicketResultInfo.getDayTicket());
        final int day3Rank = kfwdReward.getDay3Ranking();
        if (day3Rank <= 3 && day3Rank >= 1) {
            final Player player = this.dataGetter.getPlayerDao().read(kfwdReward.getPlayerId());
            if (player != null) {
                KfwdMatchService.top3PName[day3Rank - 1] = player.getPlayerName();
                final String content2 = MessageFormatter.format(LocalMessages.TITLE_KFWD_7, new Object[] { getKfwdTitleByPos(day3Rank) });
                this.dataGetter.getMailService().writeSystemMail(LocalMessages.T_MAIL_ROLE_SYSTEM, LocalMessages.TITLE_1, content2, 1, player.getPlayerId(), 0);
            }
        }
        this.kfwdRewardDao.updateNewRewardInfo(kfwdReward);
    }
    
    private String getWdName() {
        final KfwdSeasonInfo seasonInfo = this.kfwdMatchManager.getSeasonInfo();
        if (seasonInfo != null && seasonInfo.getZb() == 1) {
            return LocalMessages.KFWD_TYPE_2;
        }
        return LocalMessages.KFWD_TYPE_1;
    }
    
    @Override
    public void iniNewSeason(final KfwdSeasonInfo seasonInfo, final KfwdScheduleInfoDto scheduleInfo, final KfwdRewardResult rewardInfo, final KfwdTicketMarketListInfo ticketInfo, final KfwdRankTreasureList treasureInfo) {
        if (scheduleInfo == null || scheduleInfo.getList() == null || scheduleInfo.getList().size() == 0 || treasureInfo == null || treasureInfo.getList().size() == 0) {
            return;
        }
        if (KfwdMatchService.connection != null) {
            KfwdMatchService.connection.setStoped(true);
            try {
                Thread.sleep(10000L);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
            KfwdMatchService.connection.disconnect();
        }
        this.kfwdMatchManager.putNewTicketMarketInfo(ticketInfo);
        for (final KfwdGwScheduleInfoDto sdto : scheduleInfo.getList()) {
            this.kfwdMatchManager.iniNewMatch(sdto);
        }
        if (KfwdMatchService.top3SeasonId != seasonInfo.getSeasonId()) {
            KfwdMatchService.top3PName = new String[] { "", "", "" };
            KfwdMatchService.nationKfwdTitleMap.clear();
        }
        this.setNewTreasureRewardInfo(seasonInfo, treasureInfo);
        this.clearSesasonInfo();
        this.doProPareNoticeInfo();
        final String matchAddress = scheduleInfo.getList().get(0).getMatchAdress();
        final String address = matchAddress.split(":")[0];
        final String port = matchAddress.split(":")[1];
        (KfwdMatchService.connection = new KfConnection((TransferConfig)new TransferConfigMatch(address, Integer.valueOf(port)), KfwdMatchService.logger, "kfwd_match_send_thread_new")).registerHandler(Command.KFWD_STATE, (ResponseHandler)this);
        KfwdMatchService.connection.registerHandler(Command.KFWD_RT_MATCH, (ResponseHandler)this);
        KfwdMatchService.connection.registerHandler(Command.KFWD_RT_MATCH_DISPLAY, (ResponseHandler)this);
        KfwdMatchService.connection.registerHandler(Command.KFWD_RT_RANKING_LIST, (ResponseHandler)this);
        KfwdMatchService.connection.registerHandler(Command.KFWD_GAMESERVERREWARDTICKETINFO, (ResponseHandler)this);
        KfwdMatchService.connection.connect();
        KfwdMatchService.connection.setStoped(false);
        final SyncMatchThread thread = new SyncMatchThread();
        thread.start();
    }
    
    private void setNewTreasureRewardInfo(final KfwdSeasonInfo seasonInfo, final KfwdRankTreasureList treasureInfo) {
        final int tgid = seasonInfo.getTgId();
        final List<KfwdRankTreasureInfo> rList = new ArrayList<KfwdRankTreasureInfo>();
        for (final KfwdRankTreasureInfo trInfo : treasureInfo.getList()) {
            if (trInfo.getGid() == tgid) {
                rList.add(trInfo);
            }
        }
        KfwdMatchService.treasureRewardInfo = rList;
    }
    
    private void doProPareNoticeInfo() {
        final KfwdSeasonInfo sInfo = this.kfwdMatchManager.getSeasonInfo();
        if (sInfo == null) {
            return;
        }
        final String wdName = this.getWdName();
        final long signUpTime = sInfo.getSignUpTime().getTime();
        final long signUpFinishTime = sInfo.getSignUpFinishTime().getTime();
        final long battleTime = sInfo.getBattleTime().getTime();
        final long day2BattleTime = sInfo.getNextDayBegionTime().getTime();
        final long day3BattleTime = sInfo.getThirdDayBegionTime().getTime();
        final int totalBattleNum = sInfo.getTotalRound();
        final int day1BattleNum = sInfo.getOneDayRoundLimit();
        int day2BattleNum = sInfo.getOneDayRoundLimit();
        int day3BattleNum = sInfo.getOneDayRoundLimit();
        final long roundInterval = sInfo.getRoundInterval() * 1000L;
        if (day1BattleNum + day2BattleNum < totalBattleNum) {
            day2BattleNum = totalBattleNum - day1BattleNum;
            day3BattleNum = 0;
        }
        else if (day1BattleNum + day2BattleNum + day3BattleNum < totalBattleNum) {
            day3BattleNum = totalBattleNum - day1BattleNum - day2BattleNum;
        }
        final KfwdNoticeInfo day1begin = new KfwdNoticeInfo();
        day1begin.setType(1);
        day1begin.setNoticeInfo(LocalMessages.KFWD_DAY1BEGIN_NOTICE);
        day1begin.setDay(1);
        day1begin.setSeasonId(sInfo.getSeasonId());
        day1begin.setBeginTime(signUpTime);
        day1begin.setEndTime(battleTime - 600000L - 60000L);
        List<Long> noticeTime = this.getNoticeTime(day1begin);
        for (final Long t : noticeTime) {
            final long delay = t - System.currentTimeMillis();
            KfwdMatchService.executor.schedule(new Runnable() {
                @Override
                public void run() {
                    KfwdMatchService.this.noticeInfo(day1begin);
                }
            }, delay, TimeUnit.MILLISECONDS);
        }
        for (int i = 0; i < day1BattleNum - 1; ++i) {
            final KfwdNoticeInfo day1start = new KfwdNoticeInfo();
            day1start.setType(4);
            day1start.setBeginTime(battleTime - 30000L + i * roundInterval);
            day1start.setSeasonId(sInfo.getSeasonId());
            final long delay = day1start.getBeginTime() - System.currentTimeMillis();
            if (delay > 0L) {
                KfwdMatchService.executor.schedule(new Runnable() {
                    @Override
                    public void run() {
                        KfwdMatchService.this.noticeInfo(day1start);
                    }
                }, delay, TimeUnit.MILLISECONDS);
            }
        }
        final KfwdNoticeInfo day2begin = new KfwdNoticeInfo();
        day2begin.setType(1);
        day2begin.setNoticeInfo(LocalMessages.KFWD_DAY2BEGIN_NOTICE);
        day2begin.setDay(2);
        day2begin.setSeasonId(sInfo.getSeasonId());
        day2begin.setBeginTime(battleTime + 60000L * sInfo.getTotalRound() / 3L);
        day2begin.setEndTime(day2BattleTime - 600000L - 60000L);
        noticeTime = this.getNoticeTime(day2begin);
        for (final Long t2 : noticeTime) {
            final long delay2 = t2 - System.currentTimeMillis();
            KfwdMatchService.executor.schedule(new Runnable() {
                @Override
                public void run() {
                    KfwdMatchService.this.noticeInfo(day2begin);
                }
            }, delay2, TimeUnit.MILLISECONDS);
        }
        for (int j = 0; j < day2BattleNum - 1; ++j) {
            final KfwdNoticeInfo day2start = new KfwdNoticeInfo();
            day2start.setType(4);
            day2start.setBeginTime(day2BattleTime - 30000L + j * roundInterval);
            day2start.setSeasonId(sInfo.getSeasonId());
            final long delay2 = day2start.getBeginTime() - System.currentTimeMillis();
            if (delay2 > 0L) {
                KfwdMatchService.executor.schedule(new Runnable() {
                    @Override
                    public void run() {
                        KfwdMatchService.this.noticeInfo(day2start);
                    }
                }, delay2, TimeUnit.MILLISECONDS);
            }
        }
        final KfwdNoticeInfo day3begin = new KfwdNoticeInfo();
        day3begin.setType(1);
        day3begin.setNoticeInfo(LocalMessages.KFWD_DAY3BEGIN_NOTICE);
        day3begin.setDay(3);
        day3begin.setSeasonId(sInfo.getSeasonId());
        day3begin.setBeginTime(day2BattleTime + 60000L * sInfo.getTotalRound() / 3L);
        day3begin.setEndTime(day3BattleTime - 600000L - 60000L);
        noticeTime = this.getNoticeTime(day3begin);
        for (final Long t3 : noticeTime) {
            final long delay3 = t3 - System.currentTimeMillis();
            KfwdMatchService.executor.schedule(new Runnable() {
                @Override
                public void run() {
                    KfwdMatchService.this.noticeInfo(day3begin);
                }
            }, delay3, TimeUnit.MILLISECONDS);
        }
        for (int k = 0; k < day3BattleNum - 1; ++k) {
            final KfwdNoticeInfo day3start = new KfwdNoticeInfo();
            day3start.setType(4);
            day3start.setBeginTime(day3BattleTime - 30000L + k * roundInterval);
            day3start.setSeasonId(sInfo.getSeasonId());
            final long delay3 = day3start.getBeginTime() - System.currentTimeMillis();
            if (delay3 > 0L) {
                KfwdMatchService.executor.schedule(new Runnable() {
                    @Override
                    public void run() {
                        KfwdMatchService.this.noticeInfo(day3start);
                    }
                }, delay3, TimeUnit.MILLISECONDS);
            }
        }
        final KfwdNoticeInfo day1fight = new KfwdNoticeInfo();
        day1fight.setType(2);
        day1fight.setNoticeInfo(MessageFormat.format(LocalMessages.KFWD_DAY1FIGHT_NOTICE, wdName));
        day1fight.setDay(1);
        day1fight.setSeasonId(sInfo.getSeasonId());
        day1fight.setBeginTime(battleTime - 600000L);
        day1fight.setEndTime(battleTime - 600000L);
        long delay2 = day1fight.getBeginTime() - System.currentTimeMillis();
        KfwdMatchService.executor.schedule(new Runnable() {
            @Override
            public void run() {
                KfwdMatchService.this.noticeInfo(day1fight);
            }
        }, delay2, TimeUnit.MILLISECONDS);
        final KfwdNoticeInfo day2fight = new KfwdNoticeInfo();
        day2fight.setType(2);
        day2fight.setNoticeInfo(MessageFormat.format(LocalMessages.KFWD_DAY2FIGHT_NOTICE, wdName));
        day2fight.setDay(2);
        day2fight.setSeasonId(sInfo.getSeasonId());
        day2fight.setBeginTime(day2BattleTime - 600000L);
        day2fight.setEndTime(day2BattleTime - 600000L);
        delay2 = day2fight.getBeginTime() - System.currentTimeMillis();
        KfwdMatchService.executor.schedule(new Runnable() {
            @Override
            public void run() {
                KfwdMatchService.this.noticeInfo(day2fight);
            }
        }, delay2, TimeUnit.MILLISECONDS);
        final KfwdNoticeInfo day3fight = new KfwdNoticeInfo();
        day3fight.setType(2);
        day3fight.setNoticeInfo(MessageFormat.format(LocalMessages.KFWD_DAY3FIGHT_NOTICE, wdName));
        day3fight.setDay(3);
        day3fight.setSeasonId(sInfo.getSeasonId());
        day3fight.setBeginTime(day3BattleTime - 600000L);
        day3fight.setEndTime(day3BattleTime - 600000L);
        delay2 = day3fight.getBeginTime() - System.currentTimeMillis();
        KfwdMatchService.executor.schedule(new Runnable() {
            @Override
            public void run() {
                KfwdMatchService.this.noticeInfo(day3fight);
            }
        }, delay2, TimeUnit.MILLISECONDS);
    }
    
    protected void noticeInfo(final KfwdNoticeInfo nInfo) {
        final int seasonId = this.kfwdMatchManager.getCurSeasonId();
        if (nInfo.getSeasonId() == seasonId) {
            if (nInfo.getType() == 1) {
                final SimpleDateFormat sf = new SimpleDateFormat("HH:mm");
                final Date date = new Date(nInfo.getEndTime() + 600000L + 60000L);
                final String content = MessageFormat.format(nInfo.getNoticeInfo(), sf.format(date), this.getWdName());
                this.dataGetter.getChatService().sendSystemChat("GLOBAL", 0, 0, content, null);
            }
            else {
                if (nInfo.getType() == 4) {
                    nInfo.setNoticeInfo(MessageFormat.format(LocalMessages.KFWD_BATTLESTRTIN30SECOND_NOTICE, this.getWdName()));
                }
                this.dataGetter.getChatService().sendSystemChat("GLOBAL", 0, 0, nInfo.getNoticeInfo(), null);
            }
        }
    }
    
    private List<Long> getNoticeTime(final KfwdNoticeInfo day1begin) {
        final long endTime = day1begin.getEndTime();
        long beginTime = (day1begin.getBeginTime() < endTime - 3600000L) ? (endTime - 3600000L) : day1begin.getBeginTime();
        if (beginTime < System.currentTimeMillis()) {
            beginTime = System.currentTimeMillis();
        }
        final List<Long> resList = new ArrayList<Long>();
        for (int i = 0; i < 10 && beginTime <= endTime; beginTime += 900000L, ++i) {
            resList.add(beginTime);
        }
        return resList;
    }
    
    private void clearSesasonInfo() {
        this.lastNoticeAssignTime = 0L;
        this.noticeEnd = false;
        this.hasNoticeSignFinish = false;
        KfwdMatchService.isfirstLoadWdTitle = true;
    }
    
    @Override
    public byte[] getPlayerTicketInfo(final PlayerDto playerDto) {
        final int playerId = playerDto.playerId;
        final KfwdState curState = this.kfwdMatchManager.getCurWdState();
        if (curState == null || curState.getGlobalState() >= 70) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.MATCH_GLOBALSTATE_SEASON_FINISH);
        }
        final int seasonId = curState.getSeasonId();
        final KfwdReward reward = this.kfwdRewardDao.getRewardByPlayerIdAndSeasonId(playerId, seasonId);
        final List<KfwdTicketMarketInfo> rlist = this.kfwdMatchManager.getTicketRewardList();
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        final Pattern pattern = Pattern.compile("(\\w+):(\\d+)");
        doc.startArray("reward");
        for (final KfwdTicketMarketInfo r : rlist) {
            doc.startObject();
            doc.createElement("pk", r.getPk());
            final Matcher mat = pattern.matcher(r.getRewardInfo());
            if (mat.find()) {
                doc.createElement("resNum", mat.group(2));
                doc.createElement("resName", mat.group(1));
            }
            doc.createElement("tnum", r.getTicketNum());
            doc.endObject();
        }
        doc.endArray();
        doc.createElement("ticket", (reward == null) ? 0 : reward.getTickets());
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Transactional
    @Override
    public byte[] useTicket(final PlayerDto playerDto, final int id, final int num) {
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Override
    public byte[] getMatchInfo(final PlayerDto playerDto) {
        final int playerId = playerDto.playerId;
        final int state = this.kfwdMatchManager.getMatchState();
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        final KfwdBaseInfo baseInfo = new KfwdBaseInfo();
        final KfwdState curState = this.kfwdMatchManager.getCurWdState();
        if (curState == null || curState.getGlobalState() >= 70) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.MATCH_GLOBALSTATE_SEASON_FINISH);
        }
        baseInfo.setKfwdState(curState.getGlobalState());
        long cd = System.currentTimeMillis() - curState.getCurrentTimestamp() + curState.getNextGlobalStateCD();
        cd = ((cd > 0L) ? cd : 0L);
        baseInfo.setNextStateCD(cd);
        doc.createElement("kfwdbaseInfo", baseInfo);
        if (baseInfo.getKfwdState() >= 20) {
            doc.startArray("tList");
            for (final KfwdRankTreasureInfo tInfo : KfwdMatchService.treasureRewardInfo) {
                doc.startObject();
                doc.createElement("minRank", tInfo.getMinRanking());
                doc.createElement("maxRank", tInfo.getMaxRanking());
                final GeneralTreasure generalTreasure = (GeneralTreasure)this.generalTreasureCache.get((Object)tInfo.getTid());
                doc.createElement("treasurePic", generalTreasure.getPic());
                doc.createElement("treasureName", generalTreasure.getName());
                doc.createElement("treasureId", generalTreasure.getId());
                doc.endObject();
            }
            doc.endArray();
        }
        final KfwdSignUpInfo signInfo = new KfwdSignUpInfo();
        final KfwdSignInfo sinfo = this.kfwdMatchManager.getSignInfoByPlayerId(playerId);
        if (sinfo == null) {
            signInfo.setCanSigned(true);
            signInfo.setSigned(false);
            signInfo.setPlayerId(playerId);
        }
        else {
            signInfo.setCanSigned(false);
            signInfo.setSigned(true);
            signInfo.setPlayerId(playerId);
            signInfo.setCompletedId(sinfo.getCompletedId());
            signInfo.setCertifacate(KfwdConstantsAndMethod.getCertifacateByCId(sinfo.getCompletedId()));
            signInfo.setMatchAdress(this.kfwdMatchManager.getMatchAddress());
            signInfo.setMatchPort(this.kfwdMatchManager.getMatchPort());
            final KfwdReward reward = this.kfwdRewardDao.getRewardByPlayerIdAndSeasonId(playerId, sinfo.getSeasonId());
            if (reward != null && reward.getDay3Ranking() > 0) {
                final int day3Ranking = reward.getDay3Ranking();
                doc.createElement("rank", day3Ranking);
                KfwdRankTreasureInfo getTreasureInfo = null;
                for (final KfwdRankTreasureInfo tInfo2 : KfwdMatchService.treasureRewardInfo) {
                    if (day3Ranking <= tInfo2.getMaxRanking() && day3Ranking >= tInfo2.getMinRanking()) {
                        getTreasureInfo = tInfo2;
                    }
                }
                if (getTreasureInfo != null) {
                    final GeneralTreasure generalTreasure2 = (GeneralTreasure)this.generalTreasureCache.get((Object)getTreasureInfo.getTid());
                    final int hasGetReward = reward.getGetTreasure();
                    doc.createElement("getTreasure", hasGetReward);
                    doc.createElement("treasurePic", generalTreasure2.getPic());
                    doc.createElement("treasureName", generalTreasure2.getName());
                    doc.createElement("treasureId", generalTreasure2.getId());
                }
            }
        }
        doc.createElement("levelLimit", this.kfwdMatchManager.getMinPlayerLevel());
        doc.createElement("signInfo", signInfo);
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public byte[] getTreasure(final PlayerDto playerDto) {
        final int playerId = playerDto.playerId;
        final int state = this.kfwdMatchManager.getMatchState();
        final KfwdState curState = this.kfwdMatchManager.getCurWdState();
        if (curState == null || curState.getGlobalState() >= 70) {
            return JsonBuilder.getJson(State.FAIL, "\u4e0d\u5728\u6bd4\u8d5b\u4e2d");
        }
        final KfwdSignUpInfo signInfo = new KfwdSignUpInfo();
        final KfwdSignInfo sinfo = this.kfwdMatchManager.getSignInfoByPlayerId(playerId);
        if (sinfo == null) {
            return JsonBuilder.getJson(State.FAIL, "\u6ca1\u6709\u5b9d\u7269");
        }
        final KfwdReward reward = this.kfwdRewardDao.getRewardByPlayerIdAndSeasonId(playerId, sinfo.getSeasonId());
        if (reward == null || reward.getDay3Ranking() <= 0) {
            return JsonBuilder.getJson(State.FAIL, "\u6ca1\u6709\u5b9d\u7269");
        }
        final int day3Ranking = reward.getDay3Ranking();
        final int hasGetReward = reward.getGetTreasure();
        if (hasGetReward > 0) {
            return JsonBuilder.getJson(State.FAIL, "hasGetTreasure");
        }
        KfwdRankTreasureInfo getTreasureInfo = null;
        for (final KfwdRankTreasureInfo tInfo : KfwdMatchService.treasureRewardInfo) {
            if (day3Ranking <= tInfo.getMaxRanking() && day3Ranking >= tInfo.getMinRanking()) {
                getTreasureInfo = tInfo;
            }
        }
        if (getTreasureInfo != null) {
            final int[] res = getTreasureInfo.getRandomLeaAndStr();
            this.dataGetter.getTreasureService().tryGetGeneralTreasure(playerDto, getTreasureInfo.getTid(), true, res[0], res[1], false, "\u8de8\u670d\u6b66\u6597\u5956\u52b1");
            this.kfwdRewardDao.updateGetTreasure(reward);
            return JsonBuilder.getJson(State.SUCCESS, "");
        }
        return JsonBuilder.getJson(State.FAIL, "\u6ca1\u6709\u5b9d\u7269");
    }
    
    @Override
    public byte[] signUp(final PlayerDto playerDto) {
        final Player player = this.playerDao.read(playerDto.playerId);
        if (player == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10012);
        }
        final int state = this.kfwdMatchManager.getMatchState();
        if (state != 20) {
            return JsonBuilder.getJson(State.FAIL, "");
        }
        final KfwdSignInfo signInfo = this.kfwdMatchManager.getSignInfoByPlayerId(playerDto.playerId);
        if (signInfo != null) {
            return JsonBuilder.getJson(State.FAIL, "");
        }
        final int schId = this.kfwdMatchManager.getScheduleIdByPlayerLevel(player.getPlayerLv());
        if (schId == 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.MATCH_GLOBALSTATE_SIGNUP_LEVEL_LOWER);
        }
        final KfwdSignup wdSignup = this.self.doSignUp(player, this.getDefaultGIds(playerDto.playerId), true);
        if (wdSignup == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.MATCH_GLOBALSTATE_SIGNUP_NOT_GENERAL);
        }
        final SimpleDateFormat sf = new SimpleDateFormat("yyyy.MM.dd HH ");
        final Date signDate = this.kfwdMatchManager.getSeasonInfo().getBattleTime();
        final String content = MessageFormatter.format(LocalMessages.KFWD_ASSIGN_MAIL, new Object[] { sf.format(signDate), this.kfwdMatchManager.getSeasonInfo().getOneDayRoundLimit() });
        final String contentChat = MessageFormatter.format(LocalMessages.KFWD_SIGN_CHAT, new Object[] { ColorUtil.getForceMsg(player.getForceId(), player.getPlayerName()) });
        this.dataGetter.getChatService().sendBigNotice("GLOBAL", null, contentChat, null);
        this.dataGetter.getMailService().writeSystemMail(LocalMessages.T_MAIL_ROLE_SYSTEM, LocalMessages.KFWD_REWARD_MAIL_TITLE, content, 1, playerDto.playerId, 0);
        for (final Map.Entry<Integer, PlayerDto> entry : Players.playerMap.entrySet()) {
            final PlayerDto dto = entry.getValue();
            final KfwdPlayerInfo kfwdPlayerInfo = new KfwdPlayerInfo();
            kfwdPlayerInfo.setPlayerName(player.getPlayerName());
            kfwdPlayerInfo.setNation(player.getForceId());
            kfwdPlayerInfo.setPlayerLevel(player.getPlayerLv());
            Players.push(dto.playerId, PushCommand.PUSH_UPDATE, JsonBuilder.getSimpleJson("kfwdsignup", kfwdPlayerInfo));
        }
        this.kfwdMatchManager.addNewSignInfo(wdSignup);
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Override
    public byte[] processDoubleReward(final PlayerDto playerDto, final int round, final int coef) {
        final Player player = this.playerDao.read(playerDto.playerId);
        if (player == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10012);
        }
        final int state = this.kfwdMatchManager.getMatchState();
        final int playerId = playerDto.playerId;
        final KfwdSignInfo kfwdSignInfo = this.kfwdMatchManager.getSignInfoByPlayerId(playerId);
        if (kfwdSignInfo == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.MATCH_GLOBALSTATE_SIGNUP_NOT_SIGN);
        }
        final KfwdDoubleRewardResult res = this.self.doDoubleReward(player, round, coef, kfwdSignInfo.getCompletedId());
        if (res == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.MATCH_GLOBALSTATE_SIGNUP_FAIL);
        }
        if (res.getState() != 1) {
            return JsonBuilder.getJson(State.FAIL, res.getReason());
        }
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Transactional
    @Override
    public KfwdDoubleRewardResult doDoubleReward(final Player player, final int round, final int coef, final int cId) {
        final int playerId = player.getPlayerId();
        KfwdRewardDouble kfwdRewardDouble = this.kfwdRewardDoubleDao.getPlayerRewardInfoByPIdAndSeasonId(KfwdSeasonService.curSeasonId, playerId);
        if (kfwdRewardDouble == null) {
            kfwdRewardDouble = new KfwdRewardDouble();
            kfwdRewardDouble.setCid(cId);
            kfwdRewardDouble.setPlayerId(playerId);
            kfwdRewardDouble.setDoubleinfo("");
            kfwdRewardDouble.setSeasonId(KfwdSeasonService.curSeasonId);
            final int pk = this.kfwdRewardDoubleDao.create(kfwdRewardDouble);
            kfwdRewardDouble.setPk(pk);
        }
        kfwdRewardDouble.setRoundDoubleInfo(round, coef);
        final Request request = new Request();
        request.setCommand(Command.KFWD_DOUBLEREWARD);
        final KfwdDoubleRewardKey key = new KfwdDoubleRewardKey();
        key.setCompetitorId(cId);
        key.setDoubleCoef(coef);
        key.setRound(round);
        key.setPlayerId(playerId);
        final int totalgold = player.getUserGold() + player.getSysGold();
        key.setGold(totalgold);
        request.setMessage(key);
        final List<Request> requestList = new ArrayList<Request>();
        requestList.add(request);
        Response response = null;
        synchronized (this) {
            response = KfwdMatchService.connection.sendSyncAndGetResponse((List)requestList);
        }
        final KfwdDoubleRewardResult result = (KfwdDoubleRewardResult)response.getMessage();
        if (result.getState() != 1) {
            return result;
        }
        final Chargeitem cg = (Chargeitem)this.dataGetter.getChargeitemCache().get((Object)45);
        final int goldUsed = result.getCost();
        this.playerDao.consumeGold(player, goldUsed, cg.getName());
        this.kfwdRewardDoubleDao.updateNewDoubleInfo(kfwdRewardDouble);
        return result;
    }
    
    @Override
    public byte[] synPlayerData(final PlayerDto playerDto, final String gIds) {
        final Player player = this.playerDao.read(playerDto.playerId);
        if (player == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10012);
        }
        final int state = this.kfwdMatchManager.getMatchState();
        if (state != 50) {
            return JsonBuilder.getJson(State.FAIL, "");
        }
        final KfwdSignInfo signInfo = this.kfwdMatchManager.getSignInfoByPlayerId(playerDto.playerId);
        if (signInfo == null) {
            return JsonBuilder.getJson(State.FAIL, "");
        }
        if (!this.IsgIdsCorrect(player, gIds)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.MATCH_GLOBALSTATE_SIGNUP_NOT_GENERAL2);
        }
        final KfwdSignup wdSignup = this.self.doSignUp(player, gIds, false);
        if (wdSignup == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.MATCH_GLOBALSTATE_SIGNUP_NOT_GENERAL);
        }
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    private boolean IsgIdsCorrect(final Player player, final String gIds) {
        try {
            final String[] gds = gIds.split("#");
            final List<PlayerGeneralMilitary> list = this.dataGetter.getPlayerGeneralMilitaryDao().getMilitaryList(player.getPlayerId());
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
    
    @Transactional
    @Override
    public KfwdSignup doSignUp(final Player player, final String gIds, final boolean isSignUp) {
        final int playerId = player.getPlayerId();
        final Request request = new Request();
        if (isSignUp) {
            request.setCommand(Command.KFWD_SIGN_FROM_GAMESERVER);
        }
        else {
            request.setCommand(Command.KFWD_SYNDATA_FROM_GAMESERVER);
        }
        final KfwdSignInfoParam sInfo = new KfwdSignInfoParam();
        final KfwdPlayerInfo playerInfo = new KfwdPlayerInfo();
        final int schId = this.kfwdMatchManager.getScheduleIdByPlayerLevel(player.getPlayerLv());
        sInfo.setScheduleId(schId);
        playerInfo.setPic(String.valueOf(player.getPic()));
        playerInfo.setPlayerId(player.getPlayerId());
        playerInfo.setPlayerName(player.getPlayerName());
        playerInfo.setNation(player.getForceId());
        playerInfo.setPlayerLevel(player.getPlayerLv());
        playerInfo.setServerId(Configuration.getProperty(player.getYx(), "gcld.serverid"));
        playerInfo.setServerName(Configuration.getProperty(player.getYx(), "gcld.showservername"));
        KfwdSignup kfwdSignup = null;
        if (!isSignUp) {
            final KfwdSignInfo signInfo = this.kfwdMatchManager.getSignInfoByPlayerId(playerId);
            playerInfo.setCompetitorId(signInfo.getCompletedId());
            kfwdSignup = new KfwdSignup();
        }
        sInfo.setPlayerInfo(playerInfo);
        try {
            sInfo.setCampInfo(Types.OBJECT_MAPPER.writeValueAsString((Object)this.dataGetter.getBattleService().getKfwdCampDatas(playerId, gIds)));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        if (sInfo.getCampInfo() == null || sInfo.getCampInfo().length() < 10) {
            return null;
        }
        request.setMessage(sInfo);
        final List<Request> requestList = new ArrayList<Request>();
        requestList.add(request);
        Response response = null;
        synchronized (this) {
            response = KfwdMatchService.connection.sendSyncAndGetResponse((List)requestList);
        }
        final KfwdSignResult signResult = (KfwdSignResult)response.getMessage();
        if (signResult.getState() != 1) {
            return null;
        }
        final int cId = signResult.getCompetitor();
        if (isSignUp) {
            kfwdSignup = new KfwdSignup();
            kfwdSignup.setCompetitorId(cId);
            kfwdSignup.setScheduleId(sInfo.getScheduleId());
            kfwdSignup.setSeasonId(KfwdSeasonService.curSeasonId);
            kfwdSignup.setTime(new Date());
            kfwdSignup.setPlayerId(playerId);
            this.kfwdSignupDao.create(kfwdSignup);
            final KfwdReward kfwdReward = new KfwdReward();
            kfwdReward.setPlayerId(playerId);
            kfwdReward.setCid(cId);
            kfwdReward.setSeasonId(KfwdSeasonService.curSeasonId);
            this.kfwdRewardDao.create(kfwdReward);
        }
        return kfwdSignup;
    }
    
    public static void main(final String[] args) {
        JsonDocument doc = new JsonDocument();
        final KfwdReward reward = null;
        doc.createElement("ticket", (reward == null) ? 0 : reward.getTickets());
        doc.startObject();
        final KfwdBaseInfo baseInfo = new KfwdBaseInfo();
        baseInfo.setKfwdState(50);
        doc.createElement("kfwdbaseInfo", baseInfo);
        if (baseInfo.getKfwdState() == 20) {
            final KfwdSignUpInfo signInfo = new KfwdSignUpInfo();
            final KfwdGInfo gInfo = new KfwdGInfo();
            final KfwdSimpleGInfo[] sgList = { new KfwdSimpleGInfo(), new KfwdSimpleGInfo() };
            gInfo.setList(sgList);
            signInfo.setPgInfo(gInfo);
            doc.createElement("signInfo", signInfo);
        }
        else if (baseInfo.getKfwdState() == 50) {
            final KfwdBattleInfo bInfo = new KfwdBattleInfo();
            final KfwdPlayerInfo p1 = new KfwdPlayerInfo();
            p1.setPlayerId(123);
            p1.setPlayerName("a");
            p1.setServerId("1");
            p1.setServerName("yaowan");
            final KfwdPlayerInfo p2 = new KfwdPlayerInfo();
            final KfwdGInfo p1gInfo = new KfwdGInfo();
            final KfwdGInfo p2gInfo = new KfwdGInfo();
            final KfwdSimpleGInfo[] sgList2 = { new KfwdSimpleGInfo(), new KfwdSimpleGInfo() };
            final KfwdSimpleGInfo[] sg2List = { new KfwdSimpleGInfo(), new KfwdSimpleGInfo() };
            p1gInfo.setList(sgList2);
            p2gInfo.setList(sg2List);
            bInfo.setP1Info(p1);
            bInfo.setP2Info(p2);
            bInfo.setP1gInfo(p1gInfo);
            bInfo.setP2gInfo(p2gInfo);
            doc.createElement("bInfo", bInfo);
        }
        doc.endObject();
        System.out.println(new String(doc.toByte()));
        doc = new JsonDocument();
        doc.startObject();
        doc.createElement("KfwdPlayerMatchKeyInfo", new KfwdPlayerMatchKeyInfo());
        doc.endObject();
        System.out.println(new String(doc.toByte()));
    }
    
    @Override
	public void setApplicationContext(final ApplicationContext arg0) throws BeansException {
        this.context = arg0;
    }
    
    @Override
    public boolean isInKfwd() {
        final int state = this.kfwdMatchManager.getMatchState();
        return state >= 20 && state < 70;
    }
    
    public static int getWinPos(final String playerName) {
        for (int i = 0; i < KfwdMatchService.top3PName.length; ++i) {
            final String s = KfwdMatchService.top3PName[i];
            if (s.equals(playerName)) {
                return i + 1;
            }
        }
        return 0;
    }
    
    @Override
    public int getWdState(final int playerId) {
        final KfwdState state = this.kfwdMatchManager.getCurWdState();
        final KfwdSignInfo sInfo = this.kfwdMatchManager.getSignInfoByPlayerId(playerId);
        if (state == null || state.getGlobalState() == 70 || state.getGlobalState() < 20) {
            return 0;
        }
        if (sInfo == null && state.getGlobalState() >= 30) {
            return 0;
        }
        return state.getGlobalState();
    }
    
    public static void clearZbTitle(final int newSeasonId) {
        if (KfwdMatchService.kfzbTitleSeasonId == newSeasonId) {
            return;
        }
        KfwdMatchService.kfzbTitleMap.clear();
    }
    
    private class ComparatorForTitle implements Comparator<KfwdReward>
    {
        @Override
        public int compare(final KfwdReward o1, final KfwdReward o2) {
            if (o1.getDay3Ranking() < o2.getDay3Ranking()) {
                return -1;
            }
            if (o1.getDay3Ranking() > o2.getDay3Ranking()) {
                return 1;
            }
            return 0;
        }
    }
    
    private class SyncMatchThread extends Thread
    {
        public SyncMatchThread() {
            super("sync-match-thread");
        }
        
        @Override
        public void run() {
            while (!this.isInterrupted()) {
                Label_0176: {
                    try {
                        if (KfwdMatchService.connection.isStoped()) {
                            return;
                        }
                        final Request request = new Request();
                        request.setCommand(Command.KFWD_STATE);
                        request.setMessage(1);
                        final List<Request> requestList = new ArrayList<Request>();
                        requestList.add(request);
                        List<Response> listResponse = new ArrayList<Response>();
                        synchronized (this) {
                            listResponse = (List<Response>)KfwdMatchService.connection.sendRequestAndGetResponseList((List)requestList);
                        }
                        for (final Response r : listResponse) {
                            KfwdMatchService.this.handle(r);
                        }
                    }
                    catch (Exception e) {
                        KfwdMatchService.logger.error("gw query thread error", e);
                        break Label_0176;
                    }
                    finally {
                        ThreadLocalFactory.clearTreadLocalLog();
                        ThreadLocalFactory.getTreadLocalLog();
                    }
                    ThreadLocalFactory.clearTreadLocalLog();
                    ThreadLocalFactory.getTreadLocalLog();
                    try {
                        Thread.sleep(1000L);
                    }
                    catch (InterruptedException e2) {
                        KfwdMatchService.logger.error("gw query thread error", e2);
                    }
                }
            }
        }
    }
}
