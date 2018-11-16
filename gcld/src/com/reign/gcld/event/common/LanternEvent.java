package com.reign.gcld.event.common;

import com.reign.gcld.log.*;
import com.reign.gcld.common.log.*;
import java.util.concurrent.*;
import com.reign.gcld.rank.service.*;
import org.apache.commons.lang.*;
import com.reign.gcld.event.domain.*;
import com.reign.gcld.rank.*;
import com.reign.gcld.player.domain.*;
import com.reign.gcld.world.common.*;
import com.reign.framework.json.*;
import com.reign.gcld.common.*;
import java.util.*;
import com.reign.gcld.player.dto.*;
import com.reign.util.*;
import com.reign.gcld.task.reward.*;

public class LanternEvent extends DefaultEvent
{
    private static final Logger timerLog;
    private static final Logger log;
    private static Map<Integer, Tuple<String, Map<Integer, Tuple<Integer, Integer>>>> baseTypeValueMap;
    public static int LENGTH;
    private static Map<Integer, Tuple<Integer, Integer>> rateMap;
    private static int RATE_LENGTH;
    private static Map<Integer, Integer> lanterRateMap;
    private static Map<Integer, Integer> lanterRateCacheMap;
    public static final int GOLD = 5;
    private static Map<String, String> baseRewadMap;
    private static Map<String, String> roundsRewadMap;
    private static Map<Integer, Integer> rateCacheMap;
    private static Map<Integer, Boolean> rewardRateBolleanMap;
    private static final int INIT_NUM = 3;
    public static final int MAX_NUM = 10;
    public static final long ROUND_GAP_MS = 30000L;
    public static final long ROUND_LAST_MS = 60000L;
    public static final Tuple<Integer, Integer> timeTuple;
    public static final int MAX_LANTERN = 100;
    private static Map<Integer, Integer> finalLanternTitleMap;
    private static int FINAL_LANTERN_TITLE_LENGTH;
    private static Map<Integer, Integer> finalLanternTitleCacheMap;
    private static Map<Integer, Boolean> titleRewardRateBolleanMap;
    public static String roundRewardType;
    public static int roundRewardValue;
    public static boolean roundBoolean;
    private static Map<Integer, String> titleBaseTypeMap;
    private static int TITLE_BASE_TYPE_LENGTH;
    private static Map<Integer, Tuple5<Integer, Integer, Integer, Integer, Integer>> titleBaseValueMap;
    private static Map<String, String> titelRewadMap;
    private static Map<Integer, String> titleTextMap;
    public static Map<Integer, Tuple<Integer, Integer>> randomMap;
    private IDataGetter dataGetter;
    
    static {
        timerLog = new TimerLogger();
        log = CommonLog.getLog(LanternEvent.class);
        LanternEvent.baseTypeValueMap = new HashMap<Integer, Tuple<String, Map<Integer, Tuple<Integer, Integer>>>>();
        final Map<Integer, Tuple<Integer, Integer>> baseValueMap1_4Map = new HashMap<Integer, Tuple<Integer, Integer>>();
        baseValueMap1_4Map.put(1, new Tuple(5, 8000));
        baseValueMap1_4Map.put(2, new Tuple(10, 7000));
        baseValueMap1_4Map.put(3, new Tuple(20, 6000));
        baseValueMap1_4Map.put(4, new Tuple(50, 5000));
        baseValueMap1_4Map.put(5, new Tuple(Integer.MAX_VALUE, 4000));
        final Map<Integer, Tuple<Integer, Integer>> baseValueMap5_7Map = new HashMap<Integer, Tuple<Integer, Integer>>();
        baseValueMap5_7Map.put(1, new Tuple(5, 8000));
        baseValueMap5_7Map.put(2, new Tuple(10, 7000));
        baseValueMap5_7Map.put(3, new Tuple(20, 6000));
        baseValueMap5_7Map.put(4, new Tuple(50, 5000));
        baseValueMap5_7Map.put(5, new Tuple(Integer.MAX_VALUE, 4000));
        final Map<Integer, Tuple<Integer, Integer>> baseValueMap8_10Map = new HashMap<Integer, Tuple<Integer, Integer>>();
        baseValueMap8_10Map.put(1, new Tuple(5, 800));
        baseValueMap8_10Map.put(2, new Tuple(10, 700));
        baseValueMap8_10Map.put(3, new Tuple(20, 600));
        baseValueMap8_10Map.put(4, new Tuple(50, 500));
        baseValueMap8_10Map.put(5, new Tuple(Integer.MAX_VALUE, 400));
        LanternEvent.baseTypeValueMap.put(1, new Tuple("ChiefExp,", baseValueMap1_4Map));
        LanternEvent.baseTypeValueMap.put(2, new Tuple("ChiefExp,", baseValueMap1_4Map));
        LanternEvent.baseTypeValueMap.put(3, new Tuple("ChiefExp,", baseValueMap1_4Map));
        LanternEvent.baseTypeValueMap.put(4, new Tuple("ChiefExp,", baseValueMap1_4Map));
        LanternEvent.baseTypeValueMap.put(5, new Tuple("food,", baseValueMap5_7Map));
        LanternEvent.baseTypeValueMap.put(6, new Tuple("food,", baseValueMap5_7Map));
        LanternEvent.baseTypeValueMap.put(7, new Tuple("food,", baseValueMap5_7Map));
        LanternEvent.baseTypeValueMap.put(8, new Tuple("iron,", baseValueMap8_10Map));
        LanternEvent.baseTypeValueMap.put(9, new Tuple("iron,", baseValueMap8_10Map));
        LanternEvent.baseTypeValueMap.put(10, new Tuple("iron,", baseValueMap8_10Map));
        LanternEvent.LENGTH = LanternEvent.baseTypeValueMap.size();
        (LanternEvent.rateMap = new HashMap<Integer, Tuple<Integer, Integer>>()).put(1, new Tuple(50, 1));
        LanternEvent.rateMap.put(2, new Tuple(60, 1));
        LanternEvent.rateMap.put(3, new Tuple(70, 3));
        LanternEvent.rateMap.put(4, new Tuple(80, 8));
        LanternEvent.rateMap.put(5, new Tuple(90, 16));
        LanternEvent.rateMap.put(6, new Tuple(100, 20));
        LanternEvent.rateMap.put(7, new Tuple(Integer.MAX_VALUE, 24));
        LanternEvent.RATE_LENGTH = LanternEvent.rateMap.size();
        (LanternEvent.lanterRateMap = new HashMap<Integer, Integer>()).put(1, 2);
        LanternEvent.lanterRateMap.put(2, 2);
        LanternEvent.lanterRateMap.put(3, 2);
        LanternEvent.lanterRateMap.put(4, 3);
        LanternEvent.lanterRateMap.put(5, 4);
        LanternEvent.lanterRateCacheMap = new ConcurrentHashMap<Integer, Integer>();
        LanternEvent.baseRewadMap = new ConcurrentHashMap<String, String>();
        LanternEvent.roundsRewadMap = new ConcurrentHashMap<String, String>();
        LanternEvent.rateCacheMap = new ConcurrentHashMap<Integer, Integer>();
        (LanternEvent.rewardRateBolleanMap = new HashMap<Integer, Boolean>()).put(1, true);
        LanternEvent.rewardRateBolleanMap.put(2, true);
        LanternEvent.rewardRateBolleanMap.put(3, true);
        LanternEvent.rewardRateBolleanMap.put(4, true);
        LanternEvent.rewardRateBolleanMap.put(5, true);
        LanternEvent.rewardRateBolleanMap.put(6, true);
        LanternEvent.rewardRateBolleanMap.put(7, true);
        LanternEvent.rewardRateBolleanMap.put(8, true);
        LanternEvent.rewardRateBolleanMap.put(9, true);
        LanternEvent.rewardRateBolleanMap.put(10, true);
        timeTuple = new Tuple(21, 45);
        (LanternEvent.finalLanternTitleMap = new HashMap<Integer, Integer>()).put(1, 500);
        LanternEvent.finalLanternTitleMap.put(2, 400);
        LanternEvent.finalLanternTitleMap.put(3, 300);
        LanternEvent.finalLanternTitleMap.put(4, 200);
        LanternEvent.finalLanternTitleMap.put(5, 100);
        LanternEvent.FINAL_LANTERN_TITLE_LENGTH = LanternEvent.finalLanternTitleMap.size();
        LanternEvent.finalLanternTitleCacheMap = new ConcurrentHashMap<Integer, Integer>();
        (LanternEvent.titleRewardRateBolleanMap = new HashMap<Integer, Boolean>()).put(1, true);
        LanternEvent.titleRewardRateBolleanMap.put(2, false);
        LanternEvent.titleRewardRateBolleanMap.put(3, false);
        LanternEvent.titleRewardRateBolleanMap.put(4, false);
        LanternEvent.titleRewardRateBolleanMap.put(5, false);
        LanternEvent.roundRewardType = "ChiefExp,";
        LanternEvent.roundRewardValue = 2000;
        LanternEvent.roundBoolean = true;
        (LanternEvent.titleBaseTypeMap = new HashMap<Integer, String>()).put(1, "iron,");
        LanternEvent.titleBaseTypeMap.put(2, "freeNiubiQuenchingTimes,");
        LanternEvent.titleBaseTypeMap.put(3, "phantom,");
        LanternEvent.titleBaseTypeMap.put(4, "ticket,");
        LanternEvent.titleBaseTypeMap.put(5, "gjdstq,");
        LanternEvent.TITLE_BASE_TYPE_LENGTH = LanternEvent.titleBaseTypeMap.size();
        (LanternEvent.titleBaseValueMap = new HashMap<Integer, Tuple5<Integer, Integer, Integer, Integer, Integer>>()).put(1, new Tuple5(100, 2, 2, 800, 3));
        LanternEvent.titleBaseValueMap.put(2, new Tuple5(150, 2, 2, 1000, 3));
        LanternEvent.titleBaseValueMap.put(3, new Tuple5(250, 2, 2, 1200, 3));
        LanternEvent.titleBaseValueMap.put(4, new Tuple5(400, 4, 4, 1600, 5));
        LanternEvent.titleBaseValueMap.put(5, new Tuple5(500, 5, 5, 2000, 6));
        LanternEvent.titelRewadMap = new ConcurrentHashMap<String, String>();
        (LanternEvent.titleTextMap = new HashMap<Integer, String>()).put(1, LocalMessages.LANTERN_TITLE_1);
        LanternEvent.titleTextMap.put(2, LocalMessages.LANTERN_TITLE_2);
        LanternEvent.titleTextMap.put(3, LocalMessages.LANTERN_TITLE_3);
        LanternEvent.titleTextMap.put(4, LocalMessages.LANTERN_TITLE_4);
        LanternEvent.titleTextMap.put(5, LocalMessages.LANTERN_TITLE_5);
        LanternEvent.randomMap = new ConcurrentHashMap<Integer, Tuple<Integer, Integer>>();
    }
    
    public static int getTitle(final int lantern) {
        Integer title = LanternEvent.finalLanternTitleCacheMap.get(lantern);
        if (title != null) {
            return title;
        }
        for (int i = 1; i <= LanternEvent.FINAL_LANTERN_TITLE_LENGTH; ++i) {
            if (lantern >= LanternEvent.finalLanternTitleMap.get(i)) {
                title = i;
                break;
            }
        }
        if (title == null) {
            title = 0;
        }
        LanternEvent.finalLanternTitleCacheMap.put(lantern, title);
        return title;
    }
    
    private static int getLanternRate(final int forceId, final IDataGetter dataGetter) {
        try {
            Integer rate = LanternEvent.lanterRateCacheMap.get(forceId);
            if (rate != null) {
                return rate;
            }
            final NationFestival nationFestival = dataGetter.getRankService().getNationFestival();
            final int[] curSerial = nationFestival.getCurSerial();
            final int serial = curSerial[forceId - 1];
            rate = LanternEvent.lanterRateMap.get(serial);
            if (rate == null) {
                LanternEvent.timerLog.error("class:LanternEvent#method:getLanternRate#forceId:" + forceId + "#rate_is_null");
                rate = 1;
            }
            LanternEvent.lanterRateCacheMap.put(forceId, rate);
            return rate;
        }
        catch (Exception e) {
            LanternEvent.timerLog.error("class:LanternEvent#method:getLanternRate#forceId:" + forceId + "#exception:", e);
            return 1;
        }
    }
    
    public static String getBaseRewardNumString(final int playerLv, final int forceId, final int rank, final IDataGetter dataGetter) {
        final int round = LanternTask.rounds.get();
        final String key = String.valueOf(round) + "_" + playerLv + "_" + forceId + "_" + rank;
        String baseReward = LanternEvent.baseRewadMap.get(key);
        if (StringUtils.isNotBlank(baseReward)) {
            return baseReward;
        }
        final Tuple<String, Map<Integer, Tuple<Integer, Integer>>> tuple = LanternEvent.baseTypeValueMap.get(round);
        baseReward = tuple.left;
        final Map<Integer, Tuple<Integer, Integer>> baseValueMap = tuple.right;
        final int length = baseValueMap.size();
        int value = 0;
        for (int i = 1; i <= length; ++i) {
            final Tuple<Integer, Integer> tempTuple = baseValueMap.get(i);
            if (rank > tempTuple.left) {
                break;
            }
            value = tempTuple.right;
        }
        if (LanternEvent.rewardRateBolleanMap.get(round)) {
            value *= getRate(playerLv);
        }
        value *= getLanternRate(forceId, dataGetter);
        baseReward = String.valueOf(baseReward) + String.valueOf(value);
        LanternEvent.baseRewadMap.put(key, baseReward);
        return baseReward;
    }
    
    public static String getRoundsRewardNumString(final int playerLv, final int forceId, final int roundNum, final IDataGetter dataGetter) {
        final String key = String.valueOf(playerLv) + "_" + forceId + "_" + roundNum;
        String baseReward = LanternEvent.roundsRewadMap.get(key);
        if (StringUtils.isNotBlank(baseReward)) {
            return baseReward;
        }
        baseReward = LanternEvent.roundRewardType;
        int value = LanternEvent.roundRewardValue;
        if (LanternEvent.roundBoolean) {
            value *= getRate(playerLv);
        }
        value *= getLanternRate(forceId, dataGetter);
        value *= roundNum;
        baseReward = String.valueOf(baseReward) + String.valueOf(value);
        LanternEvent.roundsRewadMap.put(key, baseReward);
        return baseReward;
    }
    
    public static String getTitleRewardNumString(final int title, final int playerLv, final int forceId, final IDataGetter dataGetter) {
        final String key = String.valueOf(title) + "_" + playerLv + "_" + forceId;
        String titleReward = LanternEvent.titelRewadMap.get(key);
        if (StringUtils.isNotBlank(titleReward)) {
            return titleReward;
        }
        final Tuple5<Integer, Integer, Integer, Integer, Integer> tuple5 = LanternEvent.titleBaseValueMap.get(title);
        final StringBuffer sb = new StringBuffer();
        for (int i = 1; i <= LanternEvent.TITLE_BASE_TYPE_LENGTH; ++i) {
            final String rewardType = LanternEvent.titleBaseTypeMap.get(i);
            sb.append(rewardType);
            int value = (int)tuple5.getValue(i);
            if (LanternEvent.titleRewardRateBolleanMap.get(i)) {
                value *= getRate(playerLv);
            }
            value *= getLanternRate(forceId, dataGetter);
            sb.append(value);
            sb.append(";");
        }
        titleReward = sb.substring(0, sb.length() - 1);
        LanternEvent.titelRewadMap.put(key, titleReward);
        return titleReward;
    }
    
    private static int getRate(final int playerLv) {
        Integer rate = LanternEvent.rateCacheMap.get(playerLv);
        if (rate != null) {
            return rate;
        }
        rate = 0;
        for (int i = 1; i <= LanternEvent.RATE_LENGTH; ++i) {
            final Tuple<Integer, Integer> tuple = LanternEvent.rateMap.get(i);
            if (playerLv <= tuple.left) {
                rate = tuple.right;
                break;
            }
        }
        LanternEvent.rateCacheMap.put(playerLv, rate);
        return rate;
    }
    
    public LanternEvent(final int eventId, final Date startTime, final Date endTime, final String paramInfo, final IDataGetter dataGetter) {
        super(eventId, startTime, endTime, paramInfo);
        this.dataGetter = dataGetter;
    }
    
    @Override
    public void handleOperation(final int type, final int playerId, final int val) {
        if (type == 21) {
            LanternEvent.timerLog.error("class:LanternEvent#method:handleOperation#type:" + type + "#playerId:" + playerId + "#val:" + val);
            PlayerEvent playerEvent = this.dataGetter.getPlayerEventDao().getPlayerEvent(playerId, 21);
            if (playerEvent == null) {
                playerEvent = this.initLanternEvent(playerId);
                this.dataGetter.getPlayerEventDao().create(playerEvent);
            }
        }
    }
    
    private PlayerEvent initLanternEvent(final int playerId) {
        final PlayerEvent pe = new PlayerEvent();
        pe.setEventId(21);
        pe.setPlayerId(playerId);
        pe.setParam1(3);
        pe.setParam2(0);
        pe.setParam3(0);
        pe.setParam4(0);
        pe.setParam5(0);
        pe.setParam6(0);
        pe.setParam7(0);
        pe.setParam8(0);
        pe.setParam9(0);
        pe.setParam10(0);
        pe.setCd1(null);
        return pe;
    }
    
    public void buildJson(final JsonDocument doc, final PlayerDto playerDto, PlayerEvent pe) {
        doc.createElement("eventId", this.eventId);
        doc.createElement("eventCd", this.getEventCD());
        if (pe == null) {
            this.handleOperation(this.eventId, playerDto.playerId, 0);
            pe = this.dataGetter.getPlayerEventDao().getPlayerEvent(playerDto.playerId, this.eventId);
        }
        final long current = System.currentTimeMillis();
        final long special = TimeUtil.getSpecialTime(LanternEvent.timeTuple.left, LanternEvent.timeTuple.right);
        int state = 1;
        long cd = current - special;
        final LanternRank lRank = this.dataGetter.getRankService().getLanternRank();
        if (cd > 0L) {
            cd = TimeUtil.getCd(LanternTask.nextCd);
            final LanternTask.State ls = LanternTask.state;
            if (ls == LanternTask.State.START) {
                state = 2;
                final Map<Integer, Tuple3<String, Integer, Integer>> map = new HashMap<Integer, Tuple3<String, Integer, Integer>>();
                Player player = this.dataGetter.getPlayerDao().read(playerDto.playerId);
                map.put(1, new Tuple3(player.getPlayerName(), player.getPic(), lRank.getValue(player.getForceId(), player.getPlayerId())));
                Tuple<Integer, Integer> tuple = LanternEvent.randomMap.get(playerDto.playerId);
                if (tuple == null) {
                    final List<Integer> list = new ArrayList<Integer>();
                    for (int i = 0; i < 3; ++i) {
                        if (list.size() >= 2) {
                            break;
                        }
                        final RankData rd = lRank.getRankNum(playerDto.forceId, i);
                        if (rd == null) {
                            break;
                        }
                        if (rd.playerId != playerDto.playerId) {
                            list.add(rd.playerId);
                        }
                    }
                    if (list.size() >= 2) {
                        tuple = new Tuple(list.get(0), list.get(1));
                        LanternEvent.randomMap.put(playerDto.playerId, tuple);
                    }
                }
                if (tuple != null) {
                    player = this.dataGetter.getPlayerDao().read(tuple.left);
                    map.put(2, new Tuple3(player.getPlayerName(), player.getPic(), lRank.getValue(player.getForceId(), player.getPlayerId())));
                    player = this.dataGetter.getPlayerDao().read(tuple.right);
                    map.put(3, new Tuple3(player.getPlayerName(), player.getPic(), lRank.getValue(player.getForceId(), player.getPlayerId())));
                }
                doc.startArray("generals");
                for (int LENGTH = map.size(), i = 1; i <= LENGTH; ++i) {
                    doc.startObject();
                    final Tuple3<String, Integer, Integer> tuple2 = map.get(i);
                    doc.createElement("id", i);
                    doc.createElement("playerName", tuple2.get_1());
                    doc.createElement("pic", tuple2.get_2());
                    doc.createElement("eatNum", tuple2.get_3());
                    doc.endObject();
                }
                doc.endArray();
                doc.createElement("round", LanternTask.rounds.get());
            }
            else if (ls == LanternTask.State.END) {
                state = 3;
                final int rank = lRank.getRank(1, playerDto.playerId, playerDto.forceId);
                doc.startArray("ranks");
                if (rank <= 5) {
                    for (int j = 0; j < 5; ++j) {
                        final RankData rd2 = lRank.getRankNum(playerDto.forceId, j);
                        if (rd2 == null) {
                            break;
                        }
                        doc.startObject();
                        doc.createElement("id", j + 1);
                        doc.createElement("rank", j + 1);
                        doc.createElement("playerName", this.dataGetter.getPlayerDao().getPlayerName(rd2.playerId));
                        doc.createElement("eatNum", rd2.value);
                        doc.endObject();
                    }
                }
                else {
                    for (int j = 0; j < 3; ++j) {
                        final RankData rd2 = lRank.getRankNum(playerDto.forceId, j);
                        if (rd2 == null) {
                            break;
                        }
                        doc.startObject();
                        doc.createElement("id", j + 1);
                        doc.createElement("rank", j);
                        doc.createElement("playerName", this.dataGetter.getPlayerDao().getPlayerName(rd2.playerId));
                        doc.createElement("eatNum", rd2.value);
                        doc.endObject();
                    }
                    RankData rd3 = lRank.getRankNum(playerDto.forceId, rank - 2);
                    if (rd3 != null) {
                        doc.startObject();
                        doc.createElement("id", 4);
                        doc.createElement("rank", rank - 1);
                        doc.createElement("playerName", this.dataGetter.getPlayerDao().getPlayerName(rd3.playerId));
                        doc.createElement("eatNum", rd3.value);
                        doc.endObject();
                    }
                    rd3 = lRank.getRankNum(playerDto.forceId, rank - 1);
                    if (rd3 != null) {
                        doc.startObject();
                        doc.createElement("id", 5);
                        doc.createElement("rank", rank);
                        doc.createElement("playerName", this.dataGetter.getPlayerDao().getPlayerName(rd3.playerId));
                        doc.createElement("eatNum", rd3.value);
                        doc.endObject();
                    }
                }
                doc.endArray();
                doc.createElement("haveRankReward", (rank > 0) ? 1 : 0);
                doc.createElement("haveRankReceived", pe.getParam5());
            }
            else if (ls == LanternTask.State.OVER) {
                state = 4;
                cd = 0L;
                doc.createElement("eatNum", pe.getParam2());
                doc.createElement("title", getTitle(pe.getParam2()));
                doc.createElement("haveTitleReceived", pe.getParam6());
            }
            else {
                state = 5;
            }
        }
        doc.createElement("state", state);
        doc.createElement("cd", (cd < 0L) ? (-cd) : cd);
        doc.createElement("gold", 5);
        doc.createElement("currentNum", pe.getParam1());
        doc.createElement("totalNum", 10 - LanternTask.rounds.get());
        doc.createElement("round", LanternTask.rounds.get());
    }
    
    @Override
    public void startEvent() {
        final byte[] send = JsonBuilder.getSimpleJson("haveLanternActivity", 1);
        for (final PlayerDto dto : Players.getAllPlayer()) {
            Players.push(dto.playerId, PushCommand.PUSH_UPDATE, send);
        }
        this.dataGetter.getPlayerEventDao().clearEvent(this.eventId);
        this.dataGetter.getPlayerEventDao().updateParam1All(this.eventId, 3);
        this.dataGetter.getJobService().addJob("eventService", "lanternTimeTask", "", TimeUtil.getSpecialTime(LanternEvent.timeTuple.left, LanternEvent.timeTuple.right));
    }
    
    @Override
    public void overEvent() {
        final byte[] send = JsonBuilder.getSimpleJson("haveLanternActivity", 0);
        for (final PlayerDto dto : Players.getAllPlayer()) {
            Players.push(dto.playerId, PushCommand.PUSH_UPDATE, send);
        }
        final List<PlayerEvent> peList = this.dataGetter.getPlayerEventDao().getLanternTitleRewardList(21);
        for (final PlayerEvent pe : peList) {
            try {
                final int playerId = pe.getPlayerId();
                PlayerDto dto2 = Players.getPlayer(playerId);
                if (dto2 == null) {
                    dto2 = PlayerDtoUtil.getPlayerDto(this.dataGetter.getPlayerDao().read(playerId), this.dataGetter.getPlayerAttributeDao().read(playerId));
                }
                final int playerLv = dto2.playerLv;
                final int forceId = dto2.forceId;
                final int lantern = pe.getParam1();
                final int total = pe.getParam2();
                final int titleReceived = pe.getParam6();
                if (titleReceived == 0) {
                    final int title = getTitle(total);
                    if (title > 0) {
                        final String rewardStr = getTitleRewardNumString(title, playerLv, forceId, this.dataGetter);
                        final ITaskReward tr = TaskRewardFactory.getInstance().getTaskReward(rewardStr);
                        final Map<Integer, Reward> rewardMap = tr.rewardPlayer(dto2, this.dataGetter, "\u5143\u5bb5\u6d3b\u52a8\u79f0\u53f7\u5956\u52b1", null);
                        final StringBuffer msgSb = new StringBuffer();
                        msgSb.append(MessageFormatter.format(LocalMessages.LANTERN_TITLE_CONTENT, new Object[] { LanternEvent.titleTextMap.get(title) }));
                        for (final Reward reward : rewardMap.values()) {
                            msgSb.append(reward.getName());
                            msgSb.append(reward.getNum());
                            msgSb.append("\uff0c");
                        }
                        final String msg = msgSb.substring(0, msgSb.length() - 1);
                        this.dataGetter.getMailService().writeSystemMail(LocalMessages.T_MAIL_ROLE_SYSTEM, LocalMessages.LANTERN_TITLE_LANTERN_TITLE, msg, 1, playerId, 0);
                    }
                }
                if (lantern <= 0) {
                    continue;
                }
                final String rewardStr2 = getRoundsRewardNumString(playerLv, forceId, lantern, this.dataGetter);
                final ITaskReward tr2 = TaskRewardFactory.getInstance().getTaskReward(rewardStr2);
                final Map<Integer, Reward> rewardMap2 = tr2.rewardPlayer(dto2, this.dataGetter, "\u5143\u5bb5\u6d3b\u52a8\u79f0\u53f7\u5956\u52b1", null);
                final StringBuffer msgSb2 = new StringBuffer();
                msgSb2.append(MessageFormatter.format(LocalMessages.LANTERN_LANTERN_CONTENT, new Object[] { lantern }));
                for (final Reward reward2 : rewardMap2.values()) {
                    msgSb2.append(reward2.getName());
                    msgSb2.append(reward2.getNum());
                    msgSb2.append("\uff0c");
                }
                final String msg2 = msgSb2.substring(0, msgSb2.length() - 1);
                this.dataGetter.getMailService().writeSystemMail(LocalMessages.T_MAIL_ROLE_SYSTEM, LocalMessages.LANTERN_TITLE_LANTERN_TITLE, msg2, 1, playerId, 0);
            }
            catch (Exception e) {
                LanternEvent.log.error("class:BaiNianEvent#method:overEvent#playerId:" + pe.getPlayerId(), e);
            }
        }
        this.dataGetter.getPlayerEventDao().clearEvent(this.eventId);
    }
}
