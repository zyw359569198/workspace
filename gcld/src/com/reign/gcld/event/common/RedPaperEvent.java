package com.reign.gcld.event.common;

import com.reign.util.*;
import com.reign.gcld.log.*;
import com.reign.gcld.common.log.*;
import java.util.concurrent.*;
import com.reign.gcld.common.util.*;
import org.apache.commons.lang.*;
import com.reign.gcld.event.domain.*;
import com.reign.framework.json.*;
import com.reign.gcld.player.dto.*;
import com.reign.gcld.common.*;
import java.util.*;
import com.reign.gcld.task.reward.*;

public class RedPaperEvent extends DefaultEvent
{
    private static final Logger timerLog;
    private static final Logger log;
    private static Map<Integer, String> baseTypeMap;
    public static int LENGTH;
    private static Map<Integer, Integer> baseMap;
    private static Map<Integer, Tuple<Integer, Integer>> rateMap;
    private static int RATE_LEGTH;
    private static Map<Integer, Tuple<Integer, Integer>> goldRedPaperMap;
    private static int RED_PAPER_LENGTH;
    private static Map<String, String> baseRewadMap;
    private static Map<String, Integer> redPaperMap;
    private static Map<Integer, Integer> rateCacheMap;
    private static Map<Integer, Boolean> rewardRateBolleanMap;
    private IDataGetter dataGetter;
    
    static {
        timerLog = new TimerLogger();
        log = CommonLog.getLog(RedPaperEvent.class);
        (RedPaperEvent.baseTypeMap = new HashMap<Integer, String>()).put(1, "ChiefExp,");
        RedPaperEvent.baseTypeMap.put(2, "ChiefExp,");
        RedPaperEvent.baseTypeMap.put(3, "iron,");
        RedPaperEvent.baseTypeMap.put(4, "gjdstq,");
        RedPaperEvent.baseTypeMap.put(5, "ticket,");
        RedPaperEvent.baseTypeMap.put(6, "phantom,");
        RedPaperEvent.baseTypeMap.put(7, "gold,");
        RedPaperEvent.LENGTH = RedPaperEvent.baseTypeMap.size();
        (RedPaperEvent.baseMap = new HashMap<Integer, Integer>()).put(1, 25000);
        RedPaperEvent.baseMap.put(2, 25000);
        RedPaperEvent.baseMap.put(3, 8000);
        RedPaperEvent.baseMap.put(4, 30);
        RedPaperEvent.baseMap.put(5, 3000);
        RedPaperEvent.baseMap.put(6, 10);
        RedPaperEvent.baseMap.put(7, 40);
        (RedPaperEvent.rateMap = new HashMap<Integer, Tuple<Integer, Integer>>()).put(1, new Tuple(50, 1));
        RedPaperEvent.rateMap.put(2, new Tuple(60, 1));
        RedPaperEvent.rateMap.put(3, new Tuple(70, 3));
        RedPaperEvent.rateMap.put(4, new Tuple(80, 8));
        RedPaperEvent.rateMap.put(5, new Tuple(90, 16));
        RedPaperEvent.rateMap.put(6, new Tuple(100, 20));
        RedPaperEvent.rateMap.put(7, new Tuple(Integer.MAX_VALUE, 24));
        RedPaperEvent.RATE_LEGTH = RedPaperEvent.rateMap.size();
        (RedPaperEvent.goldRedPaperMap = new HashMap<Integer, Tuple<Integer, Integer>>()).put(1, new Tuple(100, 1));
        RedPaperEvent.goldRedPaperMap.put(2, new Tuple(1000, 4));
        RedPaperEvent.goldRedPaperMap.put(3, new Tuple(2000, 5));
        RedPaperEvent.goldRedPaperMap.put(4, new Tuple(5000, 15));
        RedPaperEvent.goldRedPaperMap.put(5, new Tuple(10000, 20));
        RedPaperEvent.goldRedPaperMap.put(6, new Tuple(20000, 40));
        RedPaperEvent.goldRedPaperMap.put(7, new Tuple(50000, 120));
        RedPaperEvent.RED_PAPER_LENGTH = RedPaperEvent.goldRedPaperMap.size();
        RedPaperEvent.baseRewadMap = new ConcurrentHashMap<String, String>();
        RedPaperEvent.redPaperMap = new ConcurrentHashMap<String, Integer>();
        RedPaperEvent.rateCacheMap = new ConcurrentHashMap<Integer, Integer>();
        (RedPaperEvent.rewardRateBolleanMap = new HashMap<Integer, Boolean>()).put(1, true);
        RedPaperEvent.rewardRateBolleanMap.put(2, true);
        RedPaperEvent.rewardRateBolleanMap.put(3, true);
        RedPaperEvent.rewardRateBolleanMap.put(4, false);
        RedPaperEvent.rewardRateBolleanMap.put(5, false);
        RedPaperEvent.rewardRateBolleanMap.put(6, false);
        RedPaperEvent.rewardRateBolleanMap.put(7, false);
    }
    
    public static String getBaseRewardNumString(final int playerLv) {
        final int index = WebUtil.nextInt(RedPaperEvent.LENGTH) + 1;
        final String key = String.valueOf(playerLv) + "_" + index;
        String baseReward = RedPaperEvent.baseRewadMap.get(key);
        if (StringUtils.isNotBlank(baseReward)) {
            return baseReward;
        }
        baseReward = RedPaperEvent.baseTypeMap.get(index);
        int value = RedPaperEvent.baseMap.get(index);
        if (RedPaperEvent.rewardRateBolleanMap.get(index)) {
            value *= getRate(playerLv);
        }
        baseReward = String.valueOf(baseReward) + String.valueOf(value);
        RedPaperEvent.baseRewadMap.put(key, baseReward);
        return baseReward;
    }
    
    private static int getRate(final int playerLv) {
        Integer rate = RedPaperEvent.rateCacheMap.get(playerLv);
        if (rate != null) {
            return rate;
        }
        rate = 0;
        for (int i = 1; i <= RedPaperEvent.RATE_LEGTH; ++i) {
            final Tuple<Integer, Integer> tuple = RedPaperEvent.rateMap.get(i);
            if (playerLv <= tuple.left) {
                rate = tuple.right;
                break;
            }
        }
        RedPaperEvent.rateCacheMap.put(playerLv, rate);
        return rate;
    }
    
    public RedPaperEvent(final int eventId, final Date startTime, final Date endTime, final String paramInfo, final IDataGetter dataGetter) {
        super(eventId, startTime, endTime, paramInfo);
        this.dataGetter = dataGetter;
    }
    
    @Override
    public void handleOperation(final int type, final int playerId, final int val) {
        if (type == 20) {
            RedPaperEvent.timerLog.error("class:RedPaperEvent#method:handleOperation#type:" + type + "#playerId:" + playerId + "#val:" + val);
            PlayerEvent playerEvent = this.dataGetter.getPlayerEventDao().getPlayerEvent(playerId, 20);
            if (playerEvent == null) {
                playerEvent = this.initRedPaperEvent(playerId);
                this.dataGetter.getPlayerEventDao().create(playerEvent);
            }
            if (val <= 0) {
                return;
            }
            final int beforeGold = playerEvent.getParam1();
            final int afterGold = beforeGold + val;
            final int num = this.getRedPaper(beforeGold, afterGold);
            if (num > 0) {
                this.dataGetter.getPlayerEventDao().addParam1andParam2(playerId, 20, val, num);
            }
            else {
                this.dataGetter.getPlayerEventDao().addParam1(playerId, 20, val);
            }
        }
    }
    
    private int getRedPaper(final int beforeGold, final int afterGold) {
        final String key = String.valueOf(beforeGold) + "_" + afterGold;
        Integer redPaper = RedPaperEvent.redPaperMap.get(key);
        if (redPaper != null) {
            return redPaper;
        }
        int beforeRedPaper = 0;
        for (int i = 1; i <= RedPaperEvent.RED_PAPER_LENGTH; ++i) {
            final Tuple<Integer, Integer> tuple = RedPaperEvent.goldRedPaperMap.get(i);
            if (beforeGold < tuple.left) {
                break;
            }
            beforeRedPaper += tuple.right;
        }
        int afterRedPaper = 0;
        for (int j = 1; j <= RedPaperEvent.RED_PAPER_LENGTH; ++j) {
            final Tuple<Integer, Integer> tuple2 = RedPaperEvent.goldRedPaperMap.get(j);
            if (afterGold < tuple2.left) {
                break;
            }
            afterRedPaper += tuple2.right;
        }
        redPaper = afterRedPaper - beforeRedPaper;
        RedPaperEvent.redPaperMap.put(key, redPaper);
        return redPaper;
    }
    
    private PlayerEvent initRedPaperEvent(final int playerId) {
        final PlayerEvent pe = new PlayerEvent();
        pe.setEventId(20);
        pe.setPlayerId(playerId);
        pe.setParam1(0);
        pe.setParam2(0);
        pe.setParam3(0);
        pe.setParam4(0);
        pe.setParam5(0);
        pe.setParam6(0);
        pe.setParam7(0);
        pe.setParam8(0);
        pe.setParam9(0);
        pe.setParam10(0);
        return pe;
    }
    
    public void buildJson(final JsonDocument doc, final PlayerDto playerDto, PlayerEvent pe) {
        doc.createElement("eventId", this.eventId);
        doc.createElement("eventCd", this.getEventCD());
        if (pe == null) {
            this.handleOperation(this.eventId, playerDto.playerId, 0);
            pe = this.dataGetter.getPlayerEventDao().getPlayerEvent(playerDto.playerId, this.eventId);
        }
        doc.startArray("redPapers");
        for (int i = 1; i <= RedPaperEvent.RED_PAPER_LENGTH; ++i) {
            doc.startObject();
            doc.createElement("id", i);
            final Tuple<Integer, Integer> tuple = RedPaperEvent.goldRedPaperMap.get(i);
            doc.createElement("gold", tuple.left);
            doc.createElement("num", tuple.right);
            doc.createElement("received", (pe.getParam1() >= tuple.left) ? 1 : 0);
            doc.endObject();
        }
        doc.endArray();
        doc.createElement("gold", pe.getParam1());
        doc.createElement("num", pe.getParam2() - pe.getParam3());
    }
    
    @Override
    public void startEvent() {
        final byte[] send = JsonBuilder.getSimpleJson("haveRedPaperActivity", 1);
        for (final PlayerDto dto : Players.getAllPlayer()) {
            Players.push(dto.playerId, PushCommand.PUSH_UPDATE, send);
        }
        this.dataGetter.getPlayerEventDao().clearEvent(this.eventId);
    }
    
    @Override
    public void overEvent() {
        final byte[] send = JsonBuilder.getSimpleJson("haveRedPaperActivity", 0);
        for (final PlayerDto dto : Players.getAllPlayer()) {
            Players.push(dto.playerId, PushCommand.PUSH_UPDATE, send);
        }
        final List<PlayerEvent> peList = this.dataGetter.getPlayerEventDao().getRedPaperRewardList(20);
        for (final PlayerEvent pe : peList) {
            try {
                final int diff = pe.getParam2() - pe.getParam3();
                final Map<Integer, Integer> map = new HashMap<Integer, Integer>();
                for (int i = 1; i <= diff; ++i) {
                    final int id = WebUtil.nextInt(RedPaperEvent.LENGTH) + 1;
                    final Integer value = map.get(id);
                    if (value == null) {
                        map.put(id, 1);
                    }
                    else {
                        map.put(id, value + 1);
                    }
                }
                final Integer value2 = map.get(1);
                if (value2 != null) {
                    final Integer value3 = map.get(2);
                    if (value3 == null) {
                        map.put(2, value2);
                    }
                    else {
                        map.put(2, value2 + value3);
                    }
                }
                final int playerId = pe.getPlayerId();
                int playerLv = 0;
                PlayerDto dto2 = Players.getPlayer(playerId);
                if (dto2 != null) {
                    playerLv = dto2.playerLv;
                }
                else {
                    playerLv = this.dataGetter.getPlayerDao().getPlayerLv(playerId);
                    dto2 = PlayerDtoUtil.getPlayerDto(this.dataGetter.getPlayerDao().read(playerId), this.dataGetter.getPlayerAttributeDao().read(playerId));
                }
                final StringBuffer sb = new StringBuffer();
                for (int j = 2; j <= RedPaperEvent.LENGTH; ++j) {
                    final Integer num = map.get(j);
                    if (num != null) {
                        int value4 = RedPaperEvent.baseMap.get(j);
                        int rate = 1;
                        if (RedPaperEvent.rewardRateBolleanMap.get(j)) {
                            rate = getRate(playerLv);
                        }
                        value4 *= rate;
                        value4 *= num;
                        sb.append(RedPaperEvent.baseTypeMap.get(j));
                        sb.append(value4);
                        sb.append(";");
                    }
                }
                final String rewardStr = sb.substring(0, sb.length() - 1);
                final ITaskReward tr = TaskRewardFactory.getInstance().getTaskReward(rewardStr);
                final Map<Integer, Reward> rewardMap = tr.rewardPlayer(dto2, this.dataGetter, "\u5145\u503c\u9001\u7ea2\u5305\u6d3b\u52a8\u5956\u52b1", null);
                final StringBuffer msgSb = new StringBuffer();
                msgSb.append(LocalMessages.RED_PAPER_MAIL_CONTENT);
                for (final Reward reward : rewardMap.values()) {
                    msgSb.append(reward.getName());
                    msgSb.append(reward.getNum());
                    msgSb.append("\uff0c");
                }
                final String msg = msgSb.substring(0, msgSb.length() - 1);
                this.dataGetter.getMailService().writeSystemMail(LocalMessages.T_MAIL_ROLE_SYSTEM, LocalMessages.RED_PAPER_MAIL_TITLE, msg, 1, pe.getPlayerId(), 0);
            }
            catch (Exception e) {
                RedPaperEvent.log.error("class:RedPaperEvent#method:overEvent#playerId:" + pe.getPlayerId(), e);
            }
        }
        this.dataGetter.getPlayerEventDao().clearEvent(this.eventId);
    }
}
