package com.reign.gcld.kfzb.util;

import org.apache.commons.logging.*;
import java.util.concurrent.*;
import com.reign.gcld.battle.common.*;
import com.reign.kfzb.dto.response.*;
import com.reign.gcld.kfzb.service.*;
import com.reign.gcld.player.dto.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.gcld.common.*;
import com.reign.util.*;
import com.reign.gcld.kfgz.domain.*;
import java.util.*;
import com.reign.gcld.log.*;
import com.reign.gcld.kfzb.domain.*;
import java.io.*;

public class KfzbManager
{
    public static final int FAILED = -1;
    public static final int UN_REWARDED = 0;
    public static final int REWARDED = 1;
    private static IDataGetter dataGetter;
    private static Log kfzbLogger;
    public static Map<Integer, Integer> playerIdCIdMap;
    public static Map<Integer, Integer> cIdPlayerIdMap;
    public static Map<Integer, KfzbSignObj> playerSignMap;
    public static Map<Integer, Tuple<List<Integer>, Integer>> playerTicketsMap;
    
    static {
        KfzbManager.dataGetter = null;
        KfzbManager.kfzbLogger = new KfzbLogger();
        KfzbManager.playerIdCIdMap = new ConcurrentHashMap<Integer, Integer>();
        KfzbManager.cIdPlayerIdMap = new ConcurrentHashMap<Integer, Integer>();
        KfzbManager.playerSignMap = new ConcurrentHashMap<Integer, KfzbSignObj>();
        KfzbManager.playerTicketsMap = new ConcurrentHashMap<Integer, Tuple<List<Integer>, Integer>>();
    }
    
    public static void init(final IDataGetter dataGetter) {
        KfzbManager.dataGetter = dataGetter;
    }
    
    public static void cacheOnePlayerSupportInfo(final KfzbInfo kfzbInfo, final KfzbState kfzbState) {
    }
    
    public static void renewPlayerTickets(final KfzbPhase1RewardInfo kfzbPhase1RewardInfo, final int seasonId) {
        try {
            final Integer playerId = KfzbManager.cIdPlayerIdMap.get(kfzbPhase1RewardInfo.getcId());
            if (playerId == null) {
                ErrorSceneLog.getInstance().appendErrorMsg("KfzbManager.cIdPlayerIdMap get null").append("kfzbPhase1RewardInfo.getcId()", kfzbPhase1RewardInfo.getcId()).appendClassName("KfzbManager").appendMethodName("renewPlayerTickets").flush();
                return;
            }
            final Tuple<List<Integer>, Integer> tuple = KfzbManager.playerTicketsMap.get(playerId);
            if (tuple == null) {
                ErrorSceneLog.getInstance().appendErrorMsg("KfzbManager.playerTicketsMap get null").append("kfzbPhase1RewardInfo.getcId()", kfzbPhase1RewardInfo.getcId()).appendPlayerId(playerId).appendClassName("KfzbManager").appendMethodName("renewPlayerTickets").flush();
                return;
            }
            if (((List)tuple.left).size() == kfzbPhase1RewardInfo.getRewardTicketList().size()) {
                return;
            }
            synchronized (tuple) {
                tuple.left = kfzbPhase1RewardInfo.getRewardTicketList();
                final StringBuilder sbBuilder = new StringBuilder();
                for (final Integer ticket : (List)tuple.left) {
                    sbBuilder.append(ticket).append(",");
                }
                sbBuilder.delete(sbBuilder.length() - 1, sbBuilder.length());
                KfzbManager.dataGetter.getKfzbRewardDao().updateRewardInfo(playerId, seasonId, sbBuilder.toString());
                KfzbManager.kfzbLogger.info("update reward" + playerId + "#" + seasonId + "#" + sbBuilder.toString());
            }
            if (kfzbPhase1RewardInfo.isFinish()) {
                final int layer = kfzbPhase1RewardInfo.getLostLayer();
                if (layer > 4) {
                    return;
                }
                KfzbManager.kfzbLogger.info("send treasure layer=" + layer);
                final KfzbTreasureReward kfzbTr = KfzbSeasonService.treasureRewardMap.get(layer + 1);
                if (kfzbTr != null) {
                    KfzbManager.kfzbLogger.info("send treasure layer=" + layer + " playerId=" + playerId);
                    final PlayerDto playerDto = new PlayerDto(playerId);
                    KfzbManager.dataGetter.getTreasureService().tryGetGeneralTreasure(playerDto, kfzbTr.getTreasureId(), true, kfzbTr.getLea(), kfzbTr.getStr(), false, "\u8de8\u670d\u4e89\u9738\u5956\u52b1");
                    final GeneralTreasure generalTreasure = (GeneralTreasure)KfzbManager.dataGetter.getGeneralTreasureCache().get((Object)kfzbTr.getTreasureId());
                    final String content = MessageFormatter.format(LocalMessages.KF_ZB_TREASURE_REWARD, new Object[] { LocalMessages.KFZB_POS[layer], generalTreasure.getName() });
                    KfzbManager.dataGetter.getMailService().writeSystemMail(LocalMessages.T_MAIL_ROLE_SYSTEM, LocalMessages.KF_ZB_TREASURE_TITLE, content, 1, playerId, new Date());
                }
            }
            else if (kfzbPhase1RewardInfo.isCampain() && kfzbPhase1RewardInfo.getRewardTicketList().size() > 0) {
                KfzbManager.kfzbLogger.info("send treasure layer=-1");
                KfzbManager.kfzbLogger.info("send treasure layer=1 playerId=" + playerId);
                final KfzbTreasureReward kfzbTr2 = KfzbSeasonService.treasureRewardMap.get(1);
                if (kfzbTr2 != null) {
                    final PlayerDto playerDto2 = new PlayerDto(playerId);
                    KfzbManager.dataGetter.getTreasureService().tryGetGeneralTreasure(playerDto2, kfzbTr2.getTreasureId(), true, kfzbTr2.getLea(), kfzbTr2.getStr(), false, "\u8de8\u670d\u4e89\u9738\u5956\u52b1");
                    final GeneralTreasure generalTreasure2 = (GeneralTreasure)KfzbManager.dataGetter.getGeneralTreasureCache().get((Object)kfzbTr2.getTreasureId());
                    final String content2 = MessageFormatter.format(LocalMessages.KF_ZB_TREASURE_REWARD, new Object[] { LocalMessages.KFZB_POS[0], generalTreasure2.getName() });
                    KfzbManager.dataGetter.getMailService().writeSystemMail(LocalMessages.T_MAIL_ROLE_SYSTEM, LocalMessages.KF_ZB_TREASURE_TITLE, content2, 1, playerId, new Date());
                }
            }
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error(String.valueOf(kfzbPhase1RewardInfo.getcId()) + ":" + kfzbPhase1RewardInfo.getRewardTicketList(), e);
        }
    }
    
    public static void initPlayerSignMap(final int seasonId) {
        final List<KfgzSignup> kfgzSignupList = KfzbManager.dataGetter.getKfgzSignupDao().getModels();
        for (final KfgzSignup kfgzSignup : kfgzSignupList) {
            final int playerId = kfgzSignup.getPlayerId();
            final int cId = kfgzSignup.getCompetitorId();
            KfzbManager.playerIdCIdMap.put(playerId, cId);
            KfzbManager.cIdPlayerIdMap.put(cId, playerId);
        }
        final List<KfzbSignup> kfzbSignupList = KfzbManager.dataGetter.getKfzbSignupDao().getBySeasonId(seasonId);
        for (final KfzbSignup kfzbSignup : kfzbSignupList) {
            try {
                final int playerId2 = kfzbSignup.getPlayerId();
                final Integer cId2 = KfzbManager.playerIdCIdMap.get(playerId2);
                if (cId2 == null) {
                    ErrorSceneLog.getInstance().appendErrorMsg("cId == null").appendPlayerId(playerId2).appendClassName("KfzbManager").appendMethodName("initPlayerSignMap").flush();
                }
                else {
                    final KfzbSignObj kfzbSignObj = new KfzbSignObj();
                    kfzbSignObj.competitorId = cId2;
                    kfzbSignObj.playerId = kfzbSignup.getPlayerId();
                    kfzbSignObj.seasonId = kfzbSignup.getSeasonId();
                    KfzbManager.playerSignMap.put(playerId2, kfzbSignObj);
                }
            }
            catch (Exception e) {
                ErrorSceneLog.getInstance().error("", e);
            }
        }
    }
    
    public static void initPlayerTicketsMap(final int seasonId) {
        final List<KfzbReward> kfzbRewardList = KfzbManager.dataGetter.getKfzbRewardDao().getBySeasonId(seasonId);
        for (final KfzbReward kfzbReward : kfzbRewardList) {
            try {
                final int playerId = kfzbReward.getPlayerId();
                final Tuple<List<Integer>, Integer> tuple = new Tuple();
                if (kfzbReward.getRewardinfo() != null) {
                    tuple.left = parseTicketsInfo(kfzbReward.getRewardinfo());
                }
                else {
                    tuple.left = new LinkedList();
                }
                tuple.right = kfzbReward.getDoneNum();
                KfzbManager.playerTicketsMap.put(playerId, tuple);
            }
            catch (Exception e) {
                ErrorSceneLog.getInstance().error("", e);
            }
        }
    }
    
    private static List<Integer> parseTicketsInfo(final String rewardInfo) {
        final List<Integer> list = new LinkedList<Integer>();
        try {
            final String[] rewardArray = rewardInfo.split(",");
            for (int i = 0; i < rewardArray.length; ++i) {
                if (!rewardArray[i].trim().isEmpty()) {
                    int num = 0;
                    try {
                        num = Integer.parseInt(rewardArray[i]);
                    }
                    catch (Exception e) {
                        ErrorSceneLog.getInstance().error("", e);
                    }
                    list.add(num);
                }
            }
            return list;
        }
        catch (Exception e2) {
            ErrorSceneLog.getInstance().error("", e2);
            return list;
        }
    }
    
    public static void autoSaveSupportTickets(final int seasonId, final int matchId, final int roundId, final int winnerCId) {
        try {
            final List<KfzbSupport> list = KfzbManager.dataGetter.getKfzbSupportDao().getUnRewardedListByWithIndex(seasonId, matchId, roundId, 0);
            if (list == null || list.size() == 0) {
                return;
            }
            final long start = System.currentTimeMillis();
            KfzbManager.kfzbLogger.debug(LogUtil.formatThreadLog("KfzbManager", "autoSaveSupportTickets", 0, 0L, String.valueOf(seasonId) + "#" + matchId + "#" + roundId + "#" + winnerCId + "#" + list.size()));
            int guessRight = 0;
            for (final KfzbSupport kfzbSupport : list) {
                try {
                    if (kfzbSupport.getRewarded() == 1) {
                        ErrorSceneLog.getInstance().appendErrorMsg("kfzbSupport rewarded").appendPlayerId(kfzbSupport.getPlayerId()).append("seasonId", seasonId).append("matchId", matchId).append("roundId", roundId).appendClassName("KfzbManager").appendMethodName("autoSaveSupportTickets").flush();
                    }
                    else {
                        if (!kfzbSupport.getCId().equals(winnerCId)) {
                            continue;
                        }
                        ++guessRight;
                    }
                }
                catch (Exception e) {
                    ErrorSceneLog.getInstance().error(e);
                }
            }
            KfzbManager.dataGetter.getKfzbSupportDao().updateAsRewarded(seasonId, matchId, roundId, winnerCId, 1);
            KfzbManager.dataGetter.getKfzbSupportDao().updateAsFailed(seasonId, matchId, roundId, winnerCId, -1);
            KfzbManager.kfzbLogger.debug(LogUtil.formatThreadLog("BattleScheduler", "addBattleToScheduler", 2, System.currentTimeMillis() - start, String.valueOf(seasonId) + "#" + matchId + "#" + roundId + "#" + winnerCId + "#" + guessRight));
        }
        catch (Exception e2) {
            ErrorSceneLog.getInstance().error(e2);
        }
    }
}
