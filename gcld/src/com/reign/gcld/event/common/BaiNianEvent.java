package com.reign.gcld.event.common;

import com.reign.util.*;
import com.reign.gcld.log.*;
import com.reign.gcld.common.log.*;
import java.util.concurrent.*;
import org.apache.commons.lang.*;
import com.reign.gcld.event.domain.*;
import com.reign.gcld.task.reward.*;
import com.reign.framework.json.*;
import com.reign.gcld.player.dto.*;
import com.reign.gcld.common.*;
import java.util.*;

public class BaiNianEvent extends DefaultEvent
{
    private static final Logger timerLog;
    private static final Logger log;
    private static Map<Integer, String> baseTypeMap;
    private static Map<Integer, Integer> baseMap;
    public static int LENGTH;
    private static Map<Integer, Tuple<Integer, Integer>> rateMap;
    private static int RATE_LENGTH;
    private static Map<String, String> baseRewadMap;
    private static Map<Integer, Integer> rateCacheMap;
    private static Map<Integer, Boolean> rewardRateBolleanMap;
    public static Map<Integer, Long> buffMap;
    private static final int BUFF = 100;
    public static final int CD = 24;
    private IDataGetter dataGetter;
    
    static {
        timerLog = new TimerLogger();
        log = CommonLog.getLog(BaiNianEvent.class);
        (BaiNianEvent.baseTypeMap = new HashMap<Integer, String>()).put(1, "food,");
        BaiNianEvent.baseTypeMap.put(2, "freeNiubiQuenchingTimes,");
        BaiNianEvent.baseTypeMap.put(3, "ticket,");
        (BaiNianEvent.baseMap = new HashMap<Integer, Integer>()).put(1, 50000);
        BaiNianEvent.baseMap.put(2, 20);
        BaiNianEvent.baseMap.put(3, 5000);
        BaiNianEvent.LENGTH = BaiNianEvent.baseMap.size();
        (BaiNianEvent.rateMap = new HashMap<Integer, Tuple<Integer, Integer>>()).put(1, new Tuple(50, 1));
        BaiNianEvent.rateMap.put(2, new Tuple(60, 1));
        BaiNianEvent.rateMap.put(3, new Tuple(70, 3));
        BaiNianEvent.rateMap.put(4, new Tuple(80, 8));
        BaiNianEvent.rateMap.put(5, new Tuple(90, 16));
        BaiNianEvent.rateMap.put(6, new Tuple(100, 20));
        BaiNianEvent.rateMap.put(7, new Tuple(Integer.MAX_VALUE, 24));
        BaiNianEvent.RATE_LENGTH = BaiNianEvent.rateMap.size();
        BaiNianEvent.baseRewadMap = new ConcurrentHashMap<String, String>();
        BaiNianEvent.rateCacheMap = new ConcurrentHashMap<Integer, Integer>();
        (BaiNianEvent.rewardRateBolleanMap = new HashMap<Integer, Boolean>()).put(1, true);
        BaiNianEvent.rewardRateBolleanMap.put(2, false);
        BaiNianEvent.rewardRateBolleanMap.put(3, false);
        BaiNianEvent.buffMap = new ConcurrentHashMap<Integer, Long>();
    }
    
    public static String getBaseRewardNumString(final int playerLv) {
        final String key = String.valueOf(playerLv) + "_";
        String baseReward = BaiNianEvent.baseRewadMap.get(key);
        if (StringUtils.isNotBlank(baseReward)) {
            return baseReward;
        }
        baseReward = "";
        for (int i = 1; i <= BaiNianEvent.LENGTH; ++i) {
            baseReward = String.valueOf(baseReward) + BaiNianEvent.baseTypeMap.get(i);
            int value = BaiNianEvent.baseMap.get(i);
            if (BaiNianEvent.rewardRateBolleanMap.get(i)) {
                value *= getRate(playerLv);
            }
            baseReward = String.valueOf(baseReward) + String.valueOf(value);
            baseReward = String.valueOf(baseReward) + ";";
        }
        baseReward = baseReward.substring(0, baseReward.length() - 1);
        BaiNianEvent.baseRewadMap.put(key, baseReward);
        return baseReward;
    }
    
    private static int getRate(final int playerLv) {
        Integer rate = BaiNianEvent.rateCacheMap.get(playerLv);
        if (rate != null) {
            return rate;
        }
        rate = 0;
        for (int i = 1; i <= BaiNianEvent.RATE_LENGTH; ++i) {
            final Tuple<Integer, Integer> tuple = BaiNianEvent.rateMap.get(i);
            if (playerLv <= tuple.left) {
                rate = tuple.right;
                break;
            }
        }
        BaiNianEvent.rateCacheMap.put(playerLv, rate);
        return rate;
    }
    
    public static int getBuff(final int playerId, final IDataGetter dataGetter) {
        Long cd = BaiNianEvent.buffMap.get(playerId);
        if (cd == null) {
            final PlayerEvent pe = dataGetter.getPlayerEventDao().getPlayerEvent(playerId, 19);
            if (pe == null || pe.getCd1() == null) {
                BaiNianEvent.buffMap.put(playerId, 0L);
            }
            else {
                BaiNianEvent.buffMap.put(playerId, pe.getCd1().getTime());
            }
            cd = BaiNianEvent.buffMap.get(playerId);
            if (cd > System.currentTimeMillis()) {
                dataGetter.getJobService().addJob("eventService", "baiNianTimeTask", new StringBuilder(String.valueOf(playerId)).toString(), cd);
                return 100;
            }
            return 0;
        }
        else {
            if (cd > System.currentTimeMillis()) {
                return 100;
            }
            return 0;
        }
    }
    
    public BaiNianEvent(final int eventId, final Date startTime, final Date endTime, final String paramInfo, final IDataGetter dataGetter) {
        super(eventId, startTime, endTime, paramInfo);
        this.dataGetter = dataGetter;
    }
    
    @Override
    public void handleOperation(final int type, final int playerId, final int val) {
        if (type == 19) {
            BaiNianEvent.timerLog.error("class:BaiNianEvent#method:handleOperation#type:" + type + "#playerId:" + playerId + "#val:" + val);
            PlayerEvent playerEvent = this.dataGetter.getPlayerEventDao().getPlayerEvent(playerId, 19);
            if (playerEvent == null) {
                playerEvent = this.initBaiNianEvent(playerId);
                this.dataGetter.getPlayerEventDao().create(playerEvent);
            }
        }
    }
    
    private PlayerEvent initBaiNianEvent(final int playerId) {
        final PlayerEvent pe = new PlayerEvent();
        pe.setEventId(19);
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
        doc.startArray("bless");
        for (int i = 1; i <= BaiNianEvent.LENGTH; ++i) {
            doc.startObject();
            doc.createElement("id", i);
            doc.startArray("rewards");
            final String rewardStr = getBaseRewardNumString(playerDto.playerLv);
            final ITaskReward tr = TaskRewardFactory.getInstance().getTaskReward(rewardStr);
            final Map<Integer, Reward> rewardMap = tr.getReward(playerDto, this.dataGetter, null);
            for (final Reward reward : rewardMap.values()) {
                doc.startObject();
                doc.createElement("type", reward.getType());
                doc.createElement("value", reward.getNum());
                doc.endObject();
            }
            doc.endArray();
            doc.endObject();
        }
        doc.endArray();
        doc.createElement("id", pe.getParam1());
        doc.createElement("received", pe.getParam2());
    }
    
    @Override
    public void startEvent() {
        final byte[] send = JsonBuilder.getSimpleJson("haveBaiNianActivity", 1);
        for (final PlayerDto dto : Players.getAllPlayer()) {
            if (dto.cs[10] == '1') {
                Players.push(dto.playerId, PushCommand.PUSH_UPDATE, send);
            }
        }
        this.dataGetter.getPlayerEventDao().clearEvent(this.eventId);
    }
    
    @Override
    public void overEvent() {
        final byte[] send = JsonBuilder.getSimpleJson("haveBaiNianActivity", 0);
        for (final PlayerDto dto : Players.getAllPlayer()) {
            if (dto.cs[10] == '1') {
                Players.push(dto.playerId, PushCommand.PUSH_UPDATE, send);
            }
        }
        final List<PlayerEvent> peList = this.dataGetter.getPlayerEventDao().getBaiNianRewardList(19);
        final Date cd = TimeUtil.nowAddHours(24);
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("haveBaiNianBuff", 1);
        doc.createElement("baiNianBuffCd", 24 * Constants.ONE_HOUR_MS);
        doc.endObject();
        final byte[] send2 = doc.toByte();
        for (final PlayerEvent pe : peList) {
            try {
                final int playerId = pe.getPlayerId();
                PlayerDto dto2 = Players.getPlayer(playerId);
                if (dto2 == null) {
                    dto2 = PlayerDtoUtil.getPlayerDto(this.dataGetter.getPlayerDao().read(playerId), this.dataGetter.getPlayerAttributeDao().read(playerId));
                }
                final int playerLv = dto2.playerLv;
                final String rewardStr = getBaseRewardNumString(playerLv);
                final ITaskReward tr = TaskRewardFactory.getInstance().getTaskReward(rewardStr);
                final Map<Integer, Reward> rewardMap = tr.rewardPlayer(dto2, this.dataGetter, "\u62dc\u5e74\u6d3b\u52a8\u5956\u52b1", null);
                final StringBuffer msgSb = new StringBuffer();
                msgSb.append(LocalMessages.BAINIAN_MAIL_CONTENT);
                for (final Reward reward : rewardMap.values()) {
                    msgSb.append(reward.getName());
                    msgSb.append(reward.getNum());
                    msgSb.append("\uff0c");
                }
                final String msg = msgSb.substring(0, msgSb.length() - 1);
                this.dataGetter.getMailService().writeSystemMail(LocalMessages.T_MAIL_ROLE_SYSTEM, LocalMessages.BAINIAN_MAIL_TITLE, msg, 1, pe.getPlayerId(), 0);
                this.dataGetter.getPlayerEventDao().updateParam2updateCD1(playerId, 19, 1, cd);
                BaiNianEvent.buffMap.put(playerId, cd.getTime());
                Players.push(playerId, PushCommand.PUSH_UPDATE, send2);
                this.dataGetter.getJobService().addJob("eventService", "baiNianTimeTask", new StringBuilder(String.valueOf(playerId)).toString(), cd.getTime());
            }
            catch (Exception e) {
                BaiNianEvent.log.error("class:BaiNianEvent#method:overEvent#playerId:" + pe.getPlayerId(), e);
            }
        }
        this.dataGetter.getPlayerEventDao().clearEvent(this.eventId);
    }
}
