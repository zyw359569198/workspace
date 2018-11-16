package com.reign.gcld.world.common;

import java.util.concurrent.*;
import com.reign.gcld.common.*;
import com.reign.gcld.sdata.domain.*;
import java.util.*;

public class WorldCityCommon
{
    public static Map<Integer, Integer> nationMainCityIdMap;
    public static Map<Integer, Integer> mainCityNationIdMap;
    public static Map<Integer, Integer> specialNationIdMap;
    public static Map<Integer, String> mainCityPositionMap;
    public static Map<Integer, String> titleMap;
    public static Map<Integer, String> nationIdNameMap;
    public static Map<Integer, String> nationKingNameMap;
    public static Map<Integer, String> nationIdNameMapDot;
    public static Set<Integer> barbarainCitySet;
    public static Set<Integer> barbarainForceSet;
    public static Map<Integer, Integer> playerManZuForceMap;
    public static Map<Integer, Integer> manZuPlayerForceMap;
    public static Map<Integer, Integer> manZuForceIdManWangLingTargetMap;
    public static Map<Integer, Integer> forcIdManzuCityIdMap;
    public static Map<Integer, Integer> forceIdSpecialCityMap;
    public static int MIN_MANZU_SHOUMAI_COUNTRY_LV;
    public static int MANZU_SHOUMAI_COUNT_PER_DAY;
    public static int MAX_COUNTRY_LV;
    public static ConcurrentHashMap<Integer, CountryPrivilege> countryPrivilegeMap;
    
    static {
        (WorldCityCommon.nationMainCityIdMap = new HashMap<Integer, Integer>()).put(1, 123);
        WorldCityCommon.nationMainCityIdMap.put(2, 19);
        WorldCityCommon.nationMainCityIdMap.put(3, 207);
        (WorldCityCommon.mainCityNationIdMap = new HashMap<Integer, Integer>()).put(123, 1);
        WorldCityCommon.mainCityNationIdMap.put(19, 2);
        WorldCityCommon.mainCityNationIdMap.put(207, 3);
        (WorldCityCommon.specialNationIdMap = new HashMap<Integer, Integer>()).put(123, 1);
        WorldCityCommon.specialNationIdMap.put(19, 2);
        WorldCityCommon.specialNationIdMap.put(207, 3);
        WorldCityCommon.specialNationIdMap.put(250, 102);
        WorldCityCommon.specialNationIdMap.put(251, 101);
        WorldCityCommon.specialNationIdMap.put(252, 103);
        (WorldCityCommon.mainCityPositionMap = new HashMap<Integer, String>()).put(1, "2455_810");
        WorldCityCommon.mainCityPositionMap.put(2, "700_1345");
        WorldCityCommon.mainCityPositionMap.put(3, "2885_1590");
        (WorldCityCommon.titleMap = new HashMap<Integer, String>()).put(1, LocalMessages.WORLD_CITY_TITLE_1);
        WorldCityCommon.titleMap.put(2, LocalMessages.WORLD_CITY_TITLE_2);
        (WorldCityCommon.nationIdNameMap = new HashMap<Integer, String>()).put(1, LocalMessages.T_FORCE_WEI);
        WorldCityCommon.nationIdNameMap.put(2, LocalMessages.T_FORCE_SHU);
        WorldCityCommon.nationIdNameMap.put(3, LocalMessages.T_FORCE_WU);
        (WorldCityCommon.nationKingNameMap = new HashMap<Integer, String>()).put(1, LocalMessages.T_FORCE_KING_WEI_DOT);
        WorldCityCommon.nationKingNameMap.put(2, LocalMessages.T_FORCE_KING_SHU_DOT);
        WorldCityCommon.nationKingNameMap.put(3, LocalMessages.T_FORCE_KING_WU_DOT);
        (WorldCityCommon.nationIdNameMapDot = new HashMap<Integer, String>()).put(1, LocalMessages.T_FORCE_WEI_DOT);
        WorldCityCommon.nationIdNameMapDot.put(2, LocalMessages.T_FORCE_SHU_DOT);
        WorldCityCommon.nationIdNameMapDot.put(3, LocalMessages.T_FORCE_WU_DOT);
        WorldCityCommon.nationIdNameMapDot.put(101, LocalMessages.T_FORCE_BEIDI_DOT);
        WorldCityCommon.nationIdNameMapDot.put(102, LocalMessages.T_FORCE_XIRONG_DOT);
        WorldCityCommon.nationIdNameMapDot.put(103, LocalMessages.T_FORCE_DONGYI_DOT);
        WorldCityCommon.nationIdNameMapDot.put(104, LocalMessages.T_FORCE_HUANGJIN_DOT);
        (WorldCityCommon.barbarainCitySet = new HashSet<Integer>()).add(250);
        WorldCityCommon.barbarainCitySet.add(251);
        WorldCityCommon.barbarainCitySet.add(252);
        (WorldCityCommon.barbarainForceSet = new HashSet<Integer>()).add(101);
        WorldCityCommon.barbarainForceSet.add(102);
        WorldCityCommon.barbarainForceSet.add(103);
        (WorldCityCommon.playerManZuForceMap = new HashMap<Integer, Integer>()).put(1, 101);
        WorldCityCommon.playerManZuForceMap.put(2, 102);
        WorldCityCommon.playerManZuForceMap.put(3, 103);
        (WorldCityCommon.manZuPlayerForceMap = new HashMap<Integer, Integer>()).put(101, 1);
        WorldCityCommon.manZuPlayerForceMap.put(102, 2);
        WorldCityCommon.manZuPlayerForceMap.put(103, 3);
        (WorldCityCommon.manZuForceIdManWangLingTargetMap = new HashMap<Integer, Integer>()).put(101, 164);
        WorldCityCommon.manZuForceIdManWangLingTargetMap.put(102, 24);
        WorldCityCommon.manZuForceIdManWangLingTargetMap.put(103, 246);
        (WorldCityCommon.forcIdManzuCityIdMap = new HashMap<Integer, Integer>()).put(1, 251);
        WorldCityCommon.forcIdManzuCityIdMap.put(2, 250);
        WorldCityCommon.forcIdManzuCityIdMap.put(3, 252);
        (WorldCityCommon.forceIdSpecialCityMap = new HashMap<Integer, Integer>()).put(1, 132);
        WorldCityCommon.forceIdSpecialCityMap.put(2, 60);
        WorldCityCommon.forceIdSpecialCityMap.put(3, 193);
        WorldCityCommon.MIN_MANZU_SHOUMAI_COUNTRY_LV = 3;
        WorldCityCommon.MANZU_SHOUMAI_COUNT_PER_DAY = 3;
        WorldCityCommon.MAX_COUNTRY_LV = 1;
        WorldCityCommon.countryPrivilegeMap = new ConcurrentHashMap<Integer, CountryPrivilege>();
    }
    
    public static int[] getOther2ForceIds(final int forceId) {
        int[] result = null;
        if (forceId == 1) {
            result = new int[] { 2, 3 };
        }
        else if (forceId == 2) {
            result = new int[] { 1, 3 };
        }
        else if (forceId == 3) {
            result = new int[] { 1, 2 };
        }
        return result;
    }
    
    public static long getNextMoveCd(final WorldRoad road, final float speed) {
        if (road == null) {
            return 0L;
        }
        final long cd = (long)(road.getLength() / speed * 60000.0f);
        final int db = getMoveSpeedByTime(new Date());
        return cd / db / 4L;
    }
    
    public static long getNextMoveCd(final int len, final float speed) {
        final long cd = (long)(len / speed * 60000.0f);
        final int db = getMoveSpeedByTime(new Date());
        return cd / db / 4L;
    }
    
    public static int getMoveSpeedByTime(final Date date) {
        final Calendar startTime = Calendar.getInstance();
        startTime.set(11, 0);
        startTime.set(12, 0);
        startTime.set(13, 0);
        startTime.set(14, 0);
        startTime.add(11, 8);
        if (date.before(startTime.getTime())) {
            return 3;
        }
        startTime.add(11, 8);
        if (date.before(startTime.getTime())) {
            return 2;
        }
        return 2;
    }
    
    public static String makeTaskParam(final int playerId, final int generalId, final boolean isRecruiting, final boolean auto) {
        final StringBuilder sb = new StringBuilder();
        sb.append(playerId);
        sb.append(";");
        sb.append(generalId);
        sb.append(";");
        if (isRecruiting) {
            sb.append(1);
        }
        else {
            sb.append(0);
        }
        sb.append(";");
        if (auto) {
            sb.append(1);
        }
        else {
            sb.append(0);
        }
        return sb.toString();
    }
    
    public static Date getDateAfter23(final Date date) {
        final Calendar startTime = Calendar.getInstance();
        startTime.set(11, 0);
        startTime.set(12, 10);
        startTime.set(13, 0);
        startTime.set(14, 0);
        startTime.add(11, 23);
        if (date.getTime() > startTime.getTimeInMillis()) {
            final Calendar nextDate = Calendar.getInstance();
            nextDate.add(11, 24);
            return nextDate.getTime();
        }
        return date;
    }
    
    public static Date getYesterday(final Date date) {
        final Calendar calendar = Calendar.getInstance();
        Date resultDate = null;
        calendar.setTime(date);
        final int day = calendar.get(5);
        calendar.set(5, day - 1);
        resultDate = calendar.getTime();
        return resultDate;
    }
    
    public static int getStaticIndex(int quizId) {
        ++quizId;
        int index;
        for (index = 0; quizId != 1; quizId >>= 1, ++index) {}
        return index;
    }
    
    public static int getDistanceState(final int distance) {
        if (distance <= 1) {
            return 1;
        }
        if (distance <= 3) {
            return 2;
        }
        if (distance <= 5) {
            return 3;
        }
        return 0;
    }
    
    public static float getAddExp(final int distanceState) {
        if (distanceState == 1) {
            return 1.5f;
        }
        if (distanceState == 2) {
            return 1.3f;
        }
        if (distanceState == 3) {
            return 1.15f;
        }
        return 1.0f;
    }
    
    public static float getReduceAttDef(final int distanceState) {
        if (distanceState == 1) {
            return 0.5f;
        }
        if (distanceState == 2) {
            return 0.25f;
        }
        if (distanceState == 3) {
            return 0.1f;
        }
        return 0.0f;
    }
}
