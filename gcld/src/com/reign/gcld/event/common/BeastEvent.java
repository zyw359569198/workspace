package com.reign.gcld.event.common;

import com.reign.gcld.log.*;
import com.reign.gcld.common.log.*;
import java.util.concurrent.*;
import org.apache.commons.lang.*;
import com.reign.gcld.event.domain.*;
import com.reign.gcld.player.dto.*;
import com.reign.gcld.activity.common.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.framework.json.*;
import com.reign.gcld.common.*;
import com.reign.util.*;
import java.util.*;

public class BeastEvent extends DefaultEvent
{
    private static final Logger timerLog;
    private static final Logger log;
    public static Tuple<Integer, Integer> beastGoldTuple;
    public static Map<Integer, Tuple<Integer, Integer>> beastBloodExpMap;
    public static int LENGTH;
    public static int thunderBlood;
    public static Tuple<Integer, Integer> baoZhuTuple;
    public static Tuple<Integer, Integer> cdGoldTuple;
    public static Tuple<Double, Double> rateTuple;
    public static Map<Integer, String> dropMap;
    public static int SIZE;
    private static Map<Integer, Tuple<Integer, Integer>> rateMap;
    public static int RATE_LENGTH;
    private static Map<String, String> baseRewadMap;
    private static Map<Integer, Integer> rateCacheMap;
    private static final String finishBeastRewadType = "ChiefExp,";
    private IDataGetter dataGetter;
    
    static {
        timerLog = new TimerLogger();
        log = CommonLog.getLog(BeastEvent.class);
        BeastEvent.beastGoldTuple = new Tuple(100, 10);
        (BeastEvent.beastBloodExpMap = new HashMap<Integer, Tuple<Integer, Integer>>()).put(1, new Tuple(1000000, 50000));
        BeastEvent.beastBloodExpMap.put(2, new Tuple(2000000, 75000));
        BeastEvent.beastBloodExpMap.put(3, new Tuple(4000000, 100000));
        BeastEvent.LENGTH = BeastEvent.beastBloodExpMap.size();
        BeastEvent.thunderBlood = 200000;
        BeastEvent.baoZhuTuple = new Tuple(5, 100000);
        BeastEvent.cdGoldTuple = new Tuple(15, 5);
        BeastEvent.rateTuple = new Tuple(0.15, 0.4);
        (BeastEvent.dropMap = new HashMap<Integer, String>()).put(1, "recruit_token,50");
        BeastEvent.dropMap.put(2, "gjdstq,20");
        BeastEvent.dropMap.put(3, "phantom,20");
        BeastEvent.dropMap.put(4, "free_construction,20");
        BeastEvent.dropMap.put(5, "gold,30");
        BeastEvent.dropMap.put(6, "ticket,5000");
        BeastEvent.SIZE = BeastEvent.dropMap.size();
        (BeastEvent.rateMap = new HashMap<Integer, Tuple<Integer, Integer>>()).put(1, new Tuple(50, 1));
        BeastEvent.rateMap.put(2, new Tuple(60, 1));
        BeastEvent.rateMap.put(3, new Tuple(70, 3));
        BeastEvent.rateMap.put(4, new Tuple(80, 8));
        BeastEvent.rateMap.put(5, new Tuple(90, 16));
        BeastEvent.rateMap.put(6, new Tuple(100, 20));
        BeastEvent.rateMap.put(7, new Tuple(Integer.MAX_VALUE, 24));
        BeastEvent.RATE_LENGTH = BeastEvent.rateMap.size();
        BeastEvent.baseRewadMap = new ConcurrentHashMap<String, String>();
        BeastEvent.rateCacheMap = new ConcurrentHashMap<Integer, Integer>();
    }
    
    public static String getBaseRewardNumString(final int playerLv, final int index) {
        final String key = String.valueOf(playerLv) + "_" + index;
        String baseReward = BeastEvent.baseRewadMap.get(key);
        if (StringUtils.isNotBlank(baseReward)) {
            return baseReward;
        }
        baseReward = "ChiefExp,";
        int value = BeastEvent.beastBloodExpMap.get(index).right;
        value *= getRate(playerLv);
        baseReward = String.valueOf(baseReward) + String.valueOf(value);
        BeastEvent.baseRewadMap.put(key, baseReward);
        return baseReward;
    }
    
    private static int getRate(final int playerLv) {
        Integer rate = BeastEvent.rateCacheMap.get(playerLv);
        if (rate != null) {
            return rate;
        }
        rate = 0;
        for (int i = 1; i <= BeastEvent.RATE_LENGTH; ++i) {
            final Tuple<Integer, Integer> tuple = BeastEvent.rateMap.get(i);
            if (playerLv <= tuple.left) {
                rate = tuple.right;
                break;
            }
        }
        BeastEvent.rateCacheMap.put(playerLv, rate);
        return rate;
    }
    
    public BeastEvent(final int eventId, final Date startTime, final Date endTime, final String paramInfo, final IDataGetter dataGetter) {
        super(eventId, startTime, endTime, paramInfo);
        this.dataGetter = dataGetter;
    }
    
    @Override
    public void handleOperation(final int type, final int playerId, final int val) {
        if (type == 18) {
            BeastEvent.timerLog.error("class:BeastEvent#method:handleOperation#type:" + type + "#playerId:" + playerId + "#val:" + val);
            PlayerEvent playerEvent = this.dataGetter.getPlayerEventDao().getPlayerEvent(playerId, 18);
            if (playerEvent == null) {
                playerEvent = this.initBeastEvent(playerId);
                this.dataGetter.getPlayerEventDao().create(playerEvent);
            }
            if (val <= 0) {
                return;
            }
            this.dataGetter.getPlayerEventDao().addParam4(playerId, 18, val);
        }
    }
    
    private PlayerEvent initBeastEvent(final int playerId) {
        final PlayerEvent pe = new PlayerEvent();
        pe.setEventId(18);
        pe.setPlayerId(playerId);
        pe.setParam1(1);
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
        doc.createElement("cracker", pe.getParam4());
        doc.createElement("thunderGold", ((Chargeitem)this.dataGetter.getChargeitemCache().get((Object)85)).getCost());
        doc.createElement("getBlood", pe.getParam2());
        final int id = pe.getParam1();
        int totalBlood = 0;
        final Tuple<Integer, Integer> bloodExpTuple = BeastEvent.beastBloodExpMap.get(id);
        if (bloodExpTuple != null) {
            totalBlood = bloodExpTuple.left;
        }
        final Tuple<Integer, Integer> goldTuple = BeastEvent.beastGoldTuple;
        int beastGold = goldTuple.left + goldTuple.right * pe.getParam3();
        final long beastCd = TimeUtil.getCd(pe.getCd1());
        if (beastCd > 0L) {
            totalBlood = 0;
            beastGold = (int)Math.ceil(beastCd * 1.0 / Constants.ONE_HOUR_MS) * BeastEvent.cdGoldTuple.right;
        }
        doc.createElement("beastCd", beastCd);
        doc.createElement("totalBlood", totalBlood);
        doc.createElement("beastGold", beastGold);
        doc.createElement("id", pe.getParam5());
        int count = 0;
        doc.startArray("cities");
        final int cityId = MiddleAutumnCache.getInstance().getCurrentCityId(playerDto.forceId);
        if (cityId > 0) {
            final WorldCity wc = (WorldCity)this.dataGetter.getWorldCityCache().get((Object)cityId);
            if (wc != null) {
                doc.startObject();
                doc.createElement("cityId", cityId);
                doc.createElement("cityName", wc.getName());
                doc.createElement("dropType", MiddleAutumnCache.getInstance().getCurrentDropType(playerDto.forceId));
                doc.endObject();
                ++count;
            }
        }
        doc.endArray();
        long cd = 0L;
        if (count == 0) {
            cd = MiddleAutumnCache.getInstance().getNextMoonCakeTime(playerDto.forceId) - System.currentTimeMillis();
        }
        doc.createElement("cd", cd);
    }
    
    @Override
    public void startEvent() {
        this.dataGetter.getPlayerEventDao().clearEvent(this.eventId);
        this.dataGetter.getPlayerEventDao().updateCd1All(this.eventId, null);
        this.dataGetter.getPlayerEventDao().updateParam1All(this.eventId, 1);
        this.dataGetter.getJobService().addJob("timerBattleService", "addMoonCakeArmyForThreeCountry", "", System.currentTimeMillis(), false);
        final byte[] send = JsonBuilder.getSimpleJson("haveBeastActivity", 1);
        for (final PlayerDto dto : Players.getAllPlayer()) {
            if (dto.cs[10] == '1') {
                Players.push(dto.playerId, PushCommand.PUSH_UPDATE, send);
            }
        }
    }
    
    @Override
    public void overEvent() {
        final byte[] send = JsonBuilder.getSimpleJson("haveBeastActivity", 0);
        for (final PlayerDto dto : Players.getAllPlayer()) {
            if (dto.cs[10] == '1') {
                Players.push(dto.playerId, PushCommand.PUSH_UPDATE, send);
            }
        }
        final List<PlayerEvent> peList = this.dataGetter.getPlayerEventDao().getBeastRewardList(18);
        for (final PlayerEvent pe : peList) {
            try {
                final int id = pe.getParam1();
                final int blood = pe.getParam2();
                final Tuple<Integer, Integer> tuple = BeastEvent.beastBloodExpMap.get(id);
                int playerLv = 0;
                final PlayerDto dto2 = Players.getPlayer(pe.getPlayerId());
                if (dto2 != null) {
                    playerLv = dto2.playerLv;
                }
                else {
                    playerLv = this.dataGetter.getPlayerDao().getPlayerLv(pe.getPlayerId());
                }
                final int exp = (int)(blood * 1.0 / tuple.left * tuple.right * getRate(playerLv));
                this.dataGetter.getPlayerService().updateExpAndPlayerLevel(pe.getPlayerId(), exp, "\u6253\u5e74\u517d\u6d3b\u52a8\u83b7\u5f97\u7ecf\u9a8c");
                final String msg = MessageFormatter.format(LocalMessages.BEAST_MAIL_CONTENT, new Object[] { blood, exp });
                this.dataGetter.getMailService().writeSystemMail(LocalMessages.T_MAIL_ROLE_SYSTEM, LocalMessages.BEAST_MAIL_HEAD, msg, 1, pe.getPlayerId(), 0);
            }
            catch (Exception e) {
                BeastEvent.log.error("class:BeastEvent#method:overEvent#playerId:" + pe.getPlayerId(), e);
            }
        }
        this.dataGetter.getPlayerEventDao().clearEvent(this.eventId);
        this.dataGetter.getPlayerEventDao().updateCd1All(this.eventId, null);
    }
}
