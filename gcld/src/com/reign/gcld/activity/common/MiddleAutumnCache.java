package com.reign.gcld.activity.common;

import java.util.concurrent.*;
import com.reign.gcld.event.util.*;
import com.reign.gcld.common.util.*;
import com.reign.util.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.gcld.battle.common.*;
import com.reign.gcld.common.*;
import java.util.*;

public class MiddleAutumnCache
{
    private IDataGetter dataGetter;
    private static final MiddleAutumnCache instance;
    private ConcurrentHashMap<Integer, CountryMoonCakeObj> countryMoonCakeMap;
    public static final long MOON_CAKE_ARMY_GAP_0_8 = 900000L;
    public static final long MOON_CAKE_ARMY_GAP_8_24 = 480000L;
    public static final long MOON_CAKE_ARMY_GAP_MATIOM_TASK = 180000L;
    public static final int MOON_CAKE_ARMY_ID = 102001;
    public static final int BAO_MA_ARMY_ID = 102002;
    public static final int MEI_JIU_ARMY_ID = 102003;
    public static final int SHU_HUA_ARMY_ID = 102004;
    public static final int IRON_TOKEN_ARMY_ID = 102005;
    public static final int GIFT_BOX_ARMY_ID = 102006;
    public static final int BALL_ARMY_ID = 102007;
    public static final int SNOW_ARMY_ID = 102008;
    public static final int BAO_ZHU_ARMY_ID = 102009;
    public static final List<Integer> NATIONAL_DAY_ARMY_ID_LIST;
    public static final List<Integer> CHRISTMAS_DAY_ARMY_ID_LIST;
    public static final Map<Integer, Integer> checkMap;
    public static final int MOON_CAKE_ARMY_NUM_TIMES = 3;
    public static final int MOON_CAKE_ARMY_NUM_MINIMUM = 5;
    public static final double MOON_CAKE_ARMY_DELETE_COEFFICIENT = 0.00625;
    
    static {
        instance = new MiddleAutumnCache();
        (NATIONAL_DAY_ARMY_ID_LIST = new ArrayList<Integer>()).add(102002);
        MiddleAutumnCache.NATIONAL_DAY_ARMY_ID_LIST.add(102003);
        MiddleAutumnCache.NATIONAL_DAY_ARMY_ID_LIST.add(102004);
        (CHRISTMAS_DAY_ARMY_ID_LIST = new ArrayList<Integer>()).add(102006);
        MiddleAutumnCache.CHRISTMAS_DAY_ARMY_ID_LIST.add(102007);
        MiddleAutumnCache.CHRISTMAS_DAY_ARMY_ID_LIST.add(102008);
        (checkMap = new HashMap<Integer, Integer>()).put(102001, 102001);
        MiddleAutumnCache.checkMap.put(102002, 102002);
        MiddleAutumnCache.checkMap.put(102003, 102003);
        MiddleAutumnCache.checkMap.put(102004, 102004);
        MiddleAutumnCache.checkMap.put(102005, 102005);
        MiddleAutumnCache.checkMap.put(102006, 102006);
        MiddleAutumnCache.checkMap.put(102007, 102007);
        MiddleAutumnCache.checkMap.put(102008, 102008);
        MiddleAutumnCache.checkMap.put(102009, 102009);
    }
    
    private MiddleAutumnCache() {
        this.countryMoonCakeMap = new ConcurrentHashMap<Integer, CountryMoonCakeObj>();
    }
    
    public static MiddleAutumnCache getInstance() {
        return MiddleAutumnCache.instance;
    }
    
    public static int isInActivity() {
        int activityType = 0;
        if (EventUtil.isEventTime(10)) {
            activityType = 10;
        }
        else if (EventUtil.isEventTime(11)) {
            activityType = 11;
        }
        else if (EventUtil.isEventTime(15)) {
            activityType = 15;
        }
        else if (EventUtil.isEventTime(16)) {
            activityType = 16;
        }
        else if (EventUtil.isEventTime(18)) {
            activityType = 18;
        }
        return activityType;
    }
    
    public int getActivityArmyId(final int activityType) {
        switch (activityType) {
            case 10: {
                return 102001;
            }
            case 11: {
                final int index = WebUtil.nextInt(MiddleAutumnCache.NATIONAL_DAY_ARMY_ID_LIST.size());
                return MiddleAutumnCache.NATIONAL_DAY_ARMY_ID_LIST.get(index);
            }
            case 15: {
                return 102005;
            }
            case 16: {
                final int index2 = WebUtil.nextInt(MiddleAutumnCache.CHRISTMAS_DAY_ARMY_ID_LIST.size());
                return MiddleAutumnCache.CHRISTMAS_DAY_ARMY_ID_LIST.get(index2);
            }
            case 18: {
                return 102009;
            }
            default: {
                return 0;
            }
        }
    }
    
    public String getActivityBoBaoMsg(final int activityType, final String cityName, final int armyId) {
        try {
            switch (activityType) {
                case 10: {
                    return MessageFormatter.format(LocalMessages.MOON_CAKE_ARMY_ADD_BOBAO_FORMAT, new Object[] { cityName });
                }
                case 11: {
                    String rewardName = null;
                    final General general = (General)this.dataGetter.getGeneralCache().get((Object)armyId);
                    final Troop troop = (Troop)this.dataGetter.getTroopCache().get((Object)general.getTroop());
                    final int dropType = BattleDrop.getDropType(troop.getDrop());
                    switch (dropType) {
                        case 31: {
                            rewardName = LocalMessages.NATIONAL_DAY_REWARD_NAME_BAOMA;
                            break;
                        }
                        case 32: {
                            rewardName = LocalMessages.NATIONAL_DAY_REWARD_NAME_MEIJIU;
                            break;
                        }
                        case 33: {
                            rewardName = LocalMessages.NATIONAL_DAY_REWARD_NAME_SHUHUA;
                            break;
                        }
                    }
                    return MessageFormatter.format(LocalMessages.NATIONAL_DAY_ARMY_ADD_BOBAO_FORMAT, new Object[] { rewardName, cityName });
                }
                case 15: {
                    return MessageFormatter.format(LocalMessages.IRON_GIVE_BOBAO, new Object[] { cityName });
                }
                case 16: {
                    String rewardName2 = null;
                    final General general2 = (General)this.dataGetter.getGeneralCache().get((Object)armyId);
                    final Troop troop2 = (Troop)this.dataGetter.getTroopCache().get((Object)general2.getTroop());
                    final int dropType2 = BattleDrop.getDropType(troop2.getDrop());
                    switch (dropType2) {
                        case 35: {
                            rewardName2 = LocalMessages.CHRISTMAS_DAY_GIFT_BOX;
                            break;
                        }
                        case 36: {
                            rewardName2 = LocalMessages.CHRISTMAS_DAY_BALL;
                            break;
                        }
                        case 37: {
                            rewardName2 = LocalMessages.CHRISTMAS_DAY_SNOW;
                            break;
                        }
                    }
                    return MessageFormatter.format(LocalMessages.CHRISTMAS_DAY_ARMY_ADD_BOBAO, new Object[] { rewardName2, cityName });
                }
                case 18: {
                    return MessageFormatter.format(LocalMessages.BEAST_BOBAO, new Object[] { cityName });
                }
                default: {
                    return null;
                }
            }
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error(String.valueOf(this.getClass().getSimpleName()) + " getActivityBoBaoMsg catch Exception", e);
            return null;
        }
    }
    
    public void init(final IDataGetter dataGetter) {
        try {
            this.dataGetter = dataGetter;
            final int activityType = isInActivity();
            if (activityType == 0) {
                return;
            }
            for (final Integer forceId : Constants.PLAYER_FORCE_SET) {
                final CountryMoonCakeObj countryMoonCakeObj = new CountryMoonCakeObj();
                final long time = System.currentTimeMillis();
                countryMoonCakeObj.nextTime = time;
                countryMoonCakeObj.activityType = activityType;
                this.countryMoonCakeMap.put(forceId, countryMoonCakeObj);
            }
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error(String.valueOf(this.getClass().getSimpleName()) + " init catch Exception", e);
        }
    }
    
    public CountryMoonCakeObj getCurrentObj(final Integer forceId) {
        try {
            return this.countryMoonCakeMap.get(forceId);
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error(String.valueOf(this.getClass().getSimpleName()) + " getCurrentObj catch Exception", e);
            return null;
        }
    }
    
    public int getCurrentCityId(final Integer forceId) {
        try {
            final CountryMoonCakeObj countryMoonCakeObj = this.countryMoonCakeMap.get(forceId);
            if (countryMoonCakeObj == null) {
                return 0;
            }
            return countryMoonCakeObj.cityId;
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error(String.valueOf(this.getClass().getSimpleName()) + " getCurrentCityId catch Exception", e);
            return 0;
        }
    }
    
    public int getCurrentDropType(final Integer forceId) {
        try {
            final CountryMoonCakeObj countryMoonCakeObj = this.countryMoonCakeMap.get(forceId);
            if (countryMoonCakeObj == null) {
                return 0;
            }
            return countryMoonCakeObj.dropType;
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error(String.valueOf(this.getClass().getSimpleName()) + " getCurrentDropType catch Exception", e);
            return 0;
        }
    }
    
    public long getNextMoonCakeTime(final Integer forceId) {
        try {
            final CountryMoonCakeObj countryMoonCakeObj = this.countryMoonCakeMap.get(forceId);
            if (countryMoonCakeObj == null) {
                return 0L;
            }
            return countryMoonCakeObj.nextTime;
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error(String.valueOf(this.getClass().getSimpleName()) + " getNextMoonCakeTime catch Exception", e);
            return 0L;
        }
    }
    
    public Integer getNextMoonCakedeleteNum(final Integer forceId) {
        try {
            final CountryMoonCakeObj countryMoonCakeObj = this.countryMoonCakeMap.get(forceId);
            if (countryMoonCakeObj == null) {
                return 0;
            }
            return countryMoonCakeObj.deleteNum;
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error(String.valueOf(this.getClass().getSimpleName()) + " getNextMoonCakedeleteNum catch Exception", e);
            return 0;
        }
    }
    
    public void setCurrentCityId(final Integer forceId, final Integer cityId, final Integer armyId) {
        try {
            final CountryMoonCakeObj countryMoonCakeObj = this.countryMoonCakeMap.get(forceId);
            if (countryMoonCakeObj == null) {
                ErrorSceneLog.getInstance().appendErrorMsg("countryMoonCakeObj is null").append("forceId", forceId).appendClassName(this.getClass().getSimpleName()).appendMethodName("setCurrentCityId").flush();
                return;
            }
            countryMoonCakeObj.cityId = cityId;
            final General general = (General)this.dataGetter.getGeneralCache().get((Object)armyId);
            final Troop troop = (Troop)this.dataGetter.getTroopCache().get((Object)general.getTroop());
            countryMoonCakeObj.dropType = BattleDrop.getDropType(troop.getDrop());
            countryMoonCakeObj.nextTime = 0L;
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error(String.valueOf(this.getClass().getSimpleName()) + " setCurrentCityId catch Exception", e);
        }
    }
    
    public void setNextMoonCakeTime(final Integer forceId) {
        try {
            final CountryMoonCakeObj countryMoonCakeObj = this.countryMoonCakeMap.get(forceId);
            if (countryMoonCakeObj == null) {
                ErrorSceneLog.getInstance().appendErrorMsg("countryMoonCakeObj is null").append("forceId", forceId).appendClassName(this.getClass().getSimpleName()).appendMethodName("setNextMoonCakeTime").flush();
                return;
            }
            long timeGap = 0L;
            if (this.dataGetter.getRankService().hasNationTasks(forceId) > 0) {
                timeGap = 180000L;
            }
            else {
                final Calendar calendar_8 = Calendar.getInstance();
                calendar_8.setTime(new Date());
                calendar_8.set(11, 8);
                calendar_8.set(12, 0);
                calendar_8.set(13, 0);
                if (System.currentTimeMillis() >= calendar_8.getTime().getTime()) {
                    timeGap = 480000L;
                }
                else {
                    timeGap = 900000L;
                }
            }
            countryMoonCakeObj.nextTime = System.currentTimeMillis() + timeGap;
            countryMoonCakeObj.cityId = 0;
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error(String.valueOf(this.getClass().getSimpleName()) + " setNextMoonCakeTime catch Exception", e);
        }
    }
    
    public void setNextMoonCakeDeleteNum(final Integer forceId, final Integer deleteNum) {
        try {
            final CountryMoonCakeObj countryMoonCakeObj = this.countryMoonCakeMap.get(forceId);
            if (countryMoonCakeObj == null) {
                ErrorSceneLog.getInstance().appendErrorMsg("countryMoonCakeObj is null").append("forceId", forceId).appendClassName(this.getClass().getSimpleName()).appendMethodName("setNextMoonCakeDeleteNum").flush();
                return;
            }
            countryMoonCakeObj.deleteNum = deleteNum;
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error(String.valueOf(this.getClass().getSimpleName()) + " setNextMoonCakeDeleteNum catch Exception", e);
        }
    }
    
    public void resetNextMoonCakeDeleteNum(final Integer forceId) {
        try {
            final CountryMoonCakeObj countryMoonCakeObj = this.countryMoonCakeMap.get(forceId);
            if (countryMoonCakeObj == null) {
                ErrorSceneLog.getInstance().appendErrorMsg("countryMoonCakeObj is null").append("forceId", forceId).appendClassName(this.getClass().getSimpleName()).appendMethodName("reSetNextMoonCakeDeleteNum").flush();
                return;
            }
            countryMoonCakeObj.deleteNum = 0;
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error(String.valueOf(this.getClass().getSimpleName()) + " reSetNextMoonCakeDeleteNum catch Exception", e);
        }
    }
    
    public static class CountryMoonCakeObj
    {
        public Integer activityType;
        public Integer cityId;
        public Integer dropType;
        public Integer deleteNum;
        public long nextTime;
        
        public CountryMoonCakeObj() {
            this.activityType = 0;
            this.cityId = 0;
            this.dropType = 0;
            this.deleteNum = 0;
            this.nextTime = 0L;
        }
    }
}
