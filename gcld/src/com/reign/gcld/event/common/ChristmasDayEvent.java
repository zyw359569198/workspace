package com.reign.gcld.event.common;

import com.reign.gcld.common.log.*;
import com.reign.util.*;
import com.reign.gcld.log.*;
import java.util.concurrent.*;
import org.apache.commons.lang.*;
import com.reign.gcld.world.common.*;
import com.reign.gcld.event.domain.*;
import com.reign.gcld.slave.util.*;
import com.reign.gcld.activity.common.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.framework.json.*;
import java.util.*;
import com.reign.gcld.player.dto.*;
import com.reign.gcld.common.*;
import com.reign.gcld.task.reward.*;

public class ChristmasDayEvent extends DefaultEvent
{
    private static final Logger timerLog;
    public static int LENGTH;
    public static int ZYYY_NUM;
    private static Map<Integer, Map<Integer, Tuple<Integer, Integer>>> suitMap;
    public static Map<String, Integer> baseNumMap;
    private static Map<Integer, String> baseTypeMap;
    private static Map<Integer, Tuple<Integer, Integer>> baseMap;
    private static Map<Integer, Tuple<Integer, Integer>> rateMap;
    private static int SIZE;
    public static Map<Integer, Integer> goldMap;
    private static Map<String, String> singleRewadMap;
    private static Map<String, String> baseRewadMap;
    private static Map<String, String> bigRewardMap;
    private static Map<String, Integer> rateCacheMap;
    private static Map<Integer, String> bigGiftTypeMap;
    private static int BIG_GIFT_SIZE;
    private static Map<Integer, Integer> bigGiftValueMap;
    private static Map<Integer, Boolean> bigGiftRateBolleanMap;
    private IDataGetter dataGetter;
    
    static {
        timerLog = new TimerLogger();
        ChristmasDayEvent.LENGTH = 3;
        ChristmasDayEvent.ZYYY_NUM = 2;
        ChristmasDayEvent.suitMap = new HashMap<Integer, Map<Integer, Tuple<Integer, Integer>>>();
        final Map<Integer, Tuple<Integer, Integer>> map1 = new HashMap<Integer, Tuple<Integer, Integer>>();
        map1.put(1, new Tuple(4, 4));
        map1.put(2, new Tuple(4, 4));
        map1.put(3, new Tuple(4, 4));
        ChristmasDayEvent.suitMap.put(1, map1);
        final Map<Integer, Tuple<Integer, Integer>> map2 = new HashMap<Integer, Tuple<Integer, Integer>>();
        map2.put(1, new Tuple(3, 7));
        map2.put(2, new Tuple(3, 7));
        map2.put(3, new Tuple(3, 7));
        ChristmasDayEvent.suitMap.put(2, map2);
        final Map<Integer, Tuple<Integer, Integer>> map3 = new HashMap<Integer, Tuple<Integer, Integer>>();
        map3.put(1, new Tuple(2, 12));
        map3.put(2, new Tuple(2, 12));
        map3.put(3, new Tuple(2, 12));
        ChristmasDayEvent.suitMap.put(3, map3);
        ChristmasDayEvent.baseNumMap = new HashMap<String, Integer>();
        int index = 0;
        int value = 0;
        for (int i = 1; i <= ChristmasDayEvent.LENGTH; ++i) {
            for (int j = 1; j <= ChristmasDayEvent.LENGTH; ++j) {
                final Tuple<Integer, Integer> tuple = ChristmasDayEvent.suitMap.get(j).get(j);
                value = tuple.right;
                for (int k = 1; k <= tuple.left; ++k) {
                    final String key = String.valueOf(i) + "_" + index++;
                    ChristmasDayEvent.baseNumMap.put(key, value);
                }
            }
            value = 0;
            final String key2 = String.valueOf(i) + "_" + index++;
            ChristmasDayEvent.baseNumMap.put(key2, value);
            index = 0;
        }
        (ChristmasDayEvent.baseTypeMap = new HashMap<Integer, String>()).put(1, "food,");
        ChristmasDayEvent.baseTypeMap.put(2, "ChiefExp,");
        ChristmasDayEvent.baseTypeMap.put(3, "iron,");
        (ChristmasDayEvent.baseMap = new HashMap<Integer, Tuple<Integer, Integer>>()).put(1, new Tuple(3000, 50000));
        ChristmasDayEvent.baseMap.put(2, new Tuple(4000, 50000));
        ChristmasDayEvent.baseMap.put(3, new Tuple(500, 2500));
        (ChristmasDayEvent.rateMap = new HashMap<Integer, Tuple<Integer, Integer>>()).put(1, new Tuple(50, 2));
        ChristmasDayEvent.rateMap.put(2, new Tuple(60, 3));
        ChristmasDayEvent.rateMap.put(3, new Tuple(70, 5));
        ChristmasDayEvent.rateMap.put(4, new Tuple(80, 7));
        ChristmasDayEvent.rateMap.put(5, new Tuple(90, 8));
        ChristmasDayEvent.rateMap.put(6, new Tuple(100, 10));
        ChristmasDayEvent.rateMap.put(7, new Tuple(Integer.MAX_VALUE, 12));
        ChristmasDayEvent.SIZE = ChristmasDayEvent.rateMap.size();
        (ChristmasDayEvent.goldMap = new HashMap<Integer, Integer>()).put(1, 20);
        ChristmasDayEvent.goldMap.put(2, 50);
        ChristmasDayEvent.singleRewadMap = new ConcurrentHashMap<String, String>();
        ChristmasDayEvent.baseRewadMap = new ConcurrentHashMap<String, String>();
        ChristmasDayEvent.bigRewardMap = new ConcurrentHashMap<String, String>();
        ChristmasDayEvent.rateCacheMap = new ConcurrentHashMap<String, Integer>();
        (ChristmasDayEvent.bigGiftTypeMap = new HashMap<Integer, String>()).put(1, "ticket,");
        ChristmasDayEvent.bigGiftTypeMap.put(2, "freeNiubiQuenchingTimes,");
        ChristmasDayEvent.bigGiftTypeMap.put(3, "gjdstq,");
        ChristmasDayEvent.bigGiftTypeMap.put(4, "phantom,");
        ChristmasDayEvent.bigGiftTypeMap.put(5, "iron,");
        ChristmasDayEvent.BIG_GIFT_SIZE = ChristmasDayEvent.bigGiftTypeMap.size();
        (ChristmasDayEvent.bigGiftValueMap = new HashMap<Integer, Integer>()).put(1, 4000);
        ChristmasDayEvent.bigGiftValueMap.put(2, 10);
        ChristmasDayEvent.bigGiftValueMap.put(3, 30);
        ChristmasDayEvent.bigGiftValueMap.put(4, 20);
        ChristmasDayEvent.bigGiftValueMap.put(5, 8000);
        (ChristmasDayEvent.bigGiftRateBolleanMap = new HashMap<Integer, Boolean>()).put(1, false);
        ChristmasDayEvent.bigGiftRateBolleanMap.put(2, false);
        ChristmasDayEvent.bigGiftRateBolleanMap.put(3, false);
        ChristmasDayEvent.bigGiftRateBolleanMap.put(4, false);
        ChristmasDayEvent.bigGiftRateBolleanMap.put(5, true);
    }
    
    public static String getBigRewardNumString(final int playerLv, final int index) {
        final String key = String.valueOf(playerLv) + "_" + index;
        String bigReward = ChristmasDayEvent.bigRewardMap.get(key);
        if (StringUtils.isNotBlank(bigReward)) {
            return bigReward;
        }
        final int base = ChristmasDayEvent.bigGiftValueMap.get(index);
        int rate = 1;
        if (ChristmasDayEvent.bigGiftRateBolleanMap.get(index)) {
            rate = getRate(playerLv, index);
        }
        final int value = base * rate;
        bigReward = String.valueOf(ChristmasDayEvent.bigGiftTypeMap.get(index)) + value;
        ChristmasDayEvent.bigRewardMap.put(key, bigReward);
        return bigReward;
    }
    
    public static int getRandomBits() {
        final List<Integer> tempList = new ArrayList<Integer>();
        for (int i = 1; i <= ChristmasDayEvent.BIG_GIFT_SIZE; ++i) {
            tempList.add(i);
        }
        Collections.shuffle(tempList);
        int result = 0;
        for (int j = 0; j < 3; ++j) {
            result += (int)Math.pow(2.0, tempList.get(j) - 1);
        }
        return result;
    }
    
    public static String getBaseRewardNumString(final int playerLv, final int index) {
        final String key = String.valueOf(playerLv) + "_" + index;
        String baseReward = ChristmasDayEvent.baseRewadMap.get(key);
        if (StringUtils.isNotBlank(baseReward)) {
            return baseReward;
        }
        final int base = ChristmasDayEvent.baseMap.get(index).right;
        final int rate = getRate(playerLv, index);
        final int value = base * rate;
        baseReward = String.valueOf(ChristmasDayEvent.baseTypeMap.get(index)) + value;
        ChristmasDayEvent.baseRewadMap.put(key, baseReward);
        return baseReward;
    }
    
    public static String getSingleRewardNumString(final int playerLv, final int index) {
        final String key = String.valueOf(playerLv) + "_" + index;
        String singReward = ChristmasDayEvent.singleRewadMap.get(key);
        if (StringUtils.isNotBlank(singReward)) {
            return singReward;
        }
        final int base = ChristmasDayEvent.baseMap.get(index).left;
        final int rate = getRate(playerLv, index);
        final int value = base * rate;
        singReward = String.valueOf(ChristmasDayEvent.baseTypeMap.get(index)) + value;
        ChristmasDayEvent.singleRewadMap.put(key, singReward);
        return singReward;
    }
    
    private static int getRate(final int playerLv, final int index) {
        final String key = String.valueOf(playerLv) + "_" + index;
        Integer rate = ChristmasDayEvent.rateCacheMap.get(key);
        if (rate != null) {
            return rate;
        }
        rate = 0;
        for (int i = 1; i <= ChristmasDayEvent.SIZE; ++i) {
            final Tuple<Integer, Integer> tuple = ChristmasDayEvent.rateMap.get(i);
            if (playerLv <= tuple.left) {
                rate = tuple.right;
                break;
            }
        }
        ChristmasDayEvent.rateCacheMap.put(key, rate);
        return rate;
    }
    
    public ChristmasDayEvent(final int eventId, final Date startTime, final Date endTime, final String paramInfo, final IDataGetter dataGetter) {
        super(eventId, startTime, endTime, paramInfo);
        this.dataGetter = dataGetter;
    }
    
    @Override
    public void handleOperation(final int type, final int playerId, final int val) {
        if (type == 16) {
            final int temp = val / 1000;
            final int num = val % 1000;
            ChristmasDayEvent.timerLog.error("class:ChristmasDayEvent#method:handleOperation#type:" + type + "#playerId:" + playerId + "#val:" + val + "#temp:" + temp + "#num:" + num);
            PlayerEvent playerEvent = this.dataGetter.getPlayerEventDao().getPlayerEvent(playerId, 16);
            if (playerEvent == null) {
                playerEvent = this.initChristmasDayEvent(playerId);
                this.dataGetter.getPlayerEventDao().create(playerEvent);
                final Tuple<Integer, Integer> tuple = this.dataGetter.getWdSjpCache().timeWindowtMap.get(7);
                if (CityEventManager.getInstance().isInPlayerEventTimeWindow(tuple.left, tuple.right)) {
                    CityEventManager.getInstance().addPlayerEvent(playerId, 7);
                    CityEventManager.getInstance().bobaoOnePlayerEvent(playerId, 7);
                }
            }
            if (num > 0) {
                if (1 == temp) {
                    this.dataGetter.getPlayerEventDao().addParam1(playerId, 16, num);
                }
                else if (2 == temp) {
                    this.dataGetter.getPlayerEventDao().addParam2(playerId, 16, num);
                }
                else if (3 == temp) {
                    this.dataGetter.getPlayerEventDao().addParam3(playerId, 16, num);
                }
            }
        }
    }
    
    private PlayerEvent initChristmasDayEvent(final int playerId) {
        final PlayerEvent pe = new PlayerEvent();
        pe.setEventId(16);
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
        doc.startArray("goods");
        for (int i = 1; i <= ChristmasDayEvent.LENGTH; ++i) {
            doc.startObject();
            doc.createElement("id", i);
            final int suitNum = getSuitNum(i, pe);
            final String key = String.valueOf(i) + "_" + suitNum;
            final int needNum = ChristmasDayEvent.baseNumMap.get(key);
            doc.createElement("needNum", needNum);
            doc.createElement("num", getNum(i, pe));
            doc.endObject();
        }
        doc.endArray();
        doc.startArray("layers");
        for (int i = 1; i <= ChristmasDayEvent.LENGTH; ++i) {
            doc.startObject();
            doc.createElement("layerId", i);
            doc.startArray("obejects");
            for (int j = 1; j <= ChristmasDayEvent.LENGTH; ++j) {
                doc.startObject();
                doc.createElement("id", j);
                final int needNum2 = ChristmasDayEvent.suitMap.get(i).get(j).left;
                doc.createElement("needNum", needNum2);
                doc.createElement("num", this.getLayerSuitNum(i, j, pe));
                doc.endObject();
            }
            doc.endArray();
            doc.createElement("haveReward", SlaveUtil.hasReward(pe.getParam4(), i) == 1 && SlaveUtil.hasReward(pe.getParam5(), i) == 0);
            doc.endObject();
        }
        doc.endArray();
        final int yyyNum = pe.getParam9();
        doc.createElement("yyyNum", yyyNum);
        doc.createElement("display", SlaveUtil.get1Num(pe.getParam5()) >= ChristmasDayEvent.LENGTH && yyyNum <= ChristmasDayEvent.ZYYY_NUM);
        doc.createElement("giftNum", SlaveUtil.get1Num(pe.getParam10()));
        doc.createElement("gold", (yyyNum == 0 || yyyNum > ChristmasDayEvent.ZYYY_NUM) ? 0 : ChristmasDayEvent.goldMap.get(yyyNum));
        doc.startArray("cities");
        int count = 0;
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
        int layer = SlaveUtil.getLast1Index(pe.getParam4()) + 1;
        if (layer > ChristmasDayEvent.LENGTH) {
            layer = 0;
        }
        doc.createElement("currentId", layer);
    }
    
    public static int getNum(final int index, final PlayerEvent pe) {
        if (1 == index) {
            return pe.getParam1();
        }
        if (2 == index) {
            return pe.getParam2();
        }
        if (3 == index) {
            return pe.getParam3();
        }
        return 0;
    }
    
    public static int getSuitNum(final int index, final PlayerEvent pe) {
        if (1 == index) {
            return pe.getParam6();
        }
        if (2 == index) {
            return pe.getParam7();
        }
        if (3 == index) {
            return pe.getParam8();
        }
        return 0;
    }
    
    private int getLayerSuitNum(final int layerId, final int index, final PlayerEvent pe) {
        int result = 0;
        if (1 == index) {
            result = pe.getParam6();
        }
        else if (2 == index) {
            result = pe.getParam7();
        }
        else if (3 == index) {
            result = pe.getParam8();
        }
        if (1 == layerId) {
            final int needNum = ChristmasDayEvent.suitMap.get(1).get(index).left;
            return (result >= needNum) ? needNum : result;
        }
        if (2 == layerId) {
            int needNum = ChristmasDayEvent.suitMap.get(1).get(index).left;
            result -= needNum;
            if (result <= 0) {
                return 0;
            }
            needNum = ChristmasDayEvent.suitMap.get(2).get(index).left;
            return (result >= needNum) ? needNum : result;
        }
        else {
            if (3 != layerId) {
                return 0;
            }
            int needNum = ChristmasDayEvent.suitMap.get(1).get(index).left;
            result -= needNum;
            if (result <= 0) {
                return 0;
            }
            needNum = ChristmasDayEvent.suitMap.get(2).get(index).left;
            result -= needNum;
            if (result <= 0) {
                return 0;
            }
            needNum = ChristmasDayEvent.suitMap.get(3).get(index).left;
            return (result >= needNum) ? needNum : result;
        }
    }
    
    public static int getBits(int num1, int num2, int num3) {
        int result = 0;
        for (int i = 1; i <= ChristmasDayEvent.LENGTH; ++i) {
            final Map<Integer, Tuple<Integer, Integer>> map = ChristmasDayEvent.suitMap.get(i);
            final int temp1 = map.get(1).left;
            final int temp2 = map.get(2).left;
            final int temp3 = map.get(3).left;
            if (num1 < temp1 || num2 < temp2 || num3 < temp3) {
                break;
            }
            result += (int)Math.pow(2.0, i - 1);
            num1 -= temp1;
            num2 -= temp2;
            num3 -= temp3;
        }
        return result;
    }
    
    public static int getMaxSuitNum(final int layer, final int id) {
        int maxNeedNum = 0;
        for (int i = 1; i <= layer; ++i) {
            maxNeedNum += ChristmasDayEvent.suitMap.get(i).get(id).left;
        }
        return maxNeedNum;
    }
    
    @Override
    public void startEvent() {
        final byte[] send = JsonBuilder.getSimpleJson("haveChristmasDayActivity", 1);
        for (final PlayerDto dto : Players.getAllPlayer()) {
            if (dto.cs[10] == '1') {
                Players.push(dto.playerId, PushCommand.PUSH_UPDATE, send);
            }
        }
        this.dataGetter.getPlayerEventDao().clearEvent(this.eventId);
        this.dataGetter.getJobService().addJob("timerBattleService", "addMoonCakeArmyForThreeCountry", "", System.currentTimeMillis(), false);
        CityEventManager.getInstance().addFirstRoundPlayerEvent(7);
    }
    
    @Override
    public void overEvent() {
        final byte[] send = JsonBuilder.getSimpleJson("haveChristmasDayActivity", 0);
        for (final PlayerDto dto : Players.getAllPlayer()) {
            if (dto.cs[10] == '1') {
                Players.push(dto.playerId, PushCommand.PUSH_UPDATE, send);
            }
        }
        CityEventManager.getInstance().removePlayerEventByEventType(7);
        final List<PlayerEvent> peList = this.dataGetter.getPlayerEventDao().getChristmasDayRewardList(16);
        for (final PlayerEvent pe : peList) {
            try {
                final int playerId = pe.getPlayerId();
                PlayerDto playerDto = Players.getPlayer(playerId);
                if (playerDto == null) {
                    playerDto = PlayerDtoUtil.getPlayerDto(this.dataGetter.getPlayerDao().read(playerId), this.dataGetter.getPlayerAttributeDao().read(playerId));
                }
                final int playerLv = playerDto.playerLv;
                final StringBuffer msg = new StringBuffer(LocalMessages.CHRISTMAS_DAY_MAIL_CONTENT);
                if (pe.getParam5() < pe.getParam4()) {
                    if (SlaveUtil.get1Num(pe.getParam4()) >= ChristmasDayEvent.LENGTH) {
                        for (int i = 1; i <= ChristmasDayEvent.LENGTH; ++i) {
                            if (SlaveUtil.hasReward(pe.getParam4(), i) == 1 && SlaveUtil.hasReward(pe.getParam5(), i) == 0) {
                                final String rewardStr = getBaseRewardNumString(playerLv, i);
                                final ITaskReward reward = TaskRewardFactory.getInstance().getTaskReward(rewardStr);
                                final Map<Integer, Reward> map = reward.rewardPlayer(playerDto, this.dataGetter, "\u5723\u8bde\u5c0f\u793c\u5305", null);
                                for (final Reward temp : map.values()) {
                                    msg.append(temp.getName());
                                    msg.append(temp.getNum());
                                    msg.append("\uff0c");
                                }
                            }
                        }
                        final int bits = getRandomBits();
                        for (int j = 1; j <= ChristmasDayEvent.BIG_GIFT_SIZE; ++j) {
                            if (SlaveUtil.hasReward(bits, j) == 1) {
                                final String rewardStr2 = getBigRewardNumString(playerDto.playerLv, j);
                                final ITaskReward reward2 = TaskRewardFactory.getInstance().getTaskReward(rewardStr2);
                                final Map<Integer, Reward> map2 = reward2.rewardPlayer(playerDto, this.dataGetter, "\u5723\u8bde\u5927\u793c\u5305", null);
                                for (final Reward temp2 : map2.values()) {
                                    msg.append(temp2.getName());
                                    msg.append(temp2.getNum());
                                    msg.append("\uff0c");
                                }
                            }
                        }
                    }
                    else {
                        for (int i = 1; i <= ChristmasDayEvent.LENGTH; ++i) {
                            if (SlaveUtil.hasReward(pe.getParam4(), i) == 1 && SlaveUtil.hasReward(pe.getParam5(), i) == 0) {
                                final String rewardStr = getBaseRewardNumString(playerLv, i);
                                final ITaskReward reward = TaskRewardFactory.getInstance().getTaskReward(rewardStr);
                                final Map<Integer, Reward> map = reward.rewardPlayer(playerDto, this.dataGetter, "\u5723\u8bde\u5c0f\u793c\u5305", null);
                                for (final Reward temp : map.values()) {
                                    msg.append(temp.getName());
                                    msg.append(temp.getNum());
                                    msg.append("\uff0c");
                                }
                            }
                        }
                    }
                }
                else if (pe.getParam9() == 0) {
                    final int bits = getRandomBits();
                    for (int j = 1; j <= ChristmasDayEvent.BIG_GIFT_SIZE; ++j) {
                        if (SlaveUtil.hasReward(bits, j) == 1) {
                            final String rewardStr2 = getBigRewardNumString(playerLv, j);
                            final ITaskReward reward2 = TaskRewardFactory.getInstance().getTaskReward(rewardStr2);
                            final Map<Integer, Reward> map2 = reward2.rewardPlayer(playerDto, this.dataGetter, "\u5723\u8bde\u5927\u793c\u5305", null);
                            for (final Reward temp2 : map2.values()) {
                                msg.append(temp2.getName());
                                msg.append(temp2.getNum());
                                msg.append("\uff0c");
                            }
                        }
                    }
                }
                else if (pe.getParam10() > 0) {
                    final int bits = pe.getParam10();
                    for (int j = 1; j <= ChristmasDayEvent.BIG_GIFT_SIZE; ++j) {
                        if (SlaveUtil.hasReward(bits, j) == 1) {
                            final String rewardStr2 = getBigRewardNumString(playerLv, j);
                            final ITaskReward reward2 = TaskRewardFactory.getInstance().getTaskReward(rewardStr2);
                            final Map<Integer, Reward> map2 = reward2.rewardPlayer(playerDto, this.dataGetter, "\u5723\u8bde\u5927\u793c\u5305", null);
                            for (final Reward temp2 : map2.values()) {
                                msg.append(temp2.getName());
                                msg.append(temp2.getNum());
                                msg.append("\uff0c");
                            }
                        }
                    }
                }
                msg.setLength(msg.length() - 1);
                this.dataGetter.getMailService().writeSystemMail(LocalMessages.T_MAIL_ROLE_SYSTEM, LocalMessages.CHRISTMAS_DAY_MAIL_HEAD, msg.toString(), 1, playerId, 0);
            }
            catch (Exception e) {
                ChristmasDayEvent.timerLog.error("class:ChristmasDayEvent#method:overEvent#playerId:" + pe.getPlayerId() + "#exception:", e);
            }
        }
        this.dataGetter.getPlayerEventDao().clearEvent(this.eventId);
    }
    
    public static void main(final String[] args) {
        System.out.println(getRandomBits());
        System.out.println(getRandomBits());
        System.out.println(getRandomBits());
    }
}
