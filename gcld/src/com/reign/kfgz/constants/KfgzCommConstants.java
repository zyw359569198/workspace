package com.reign.kfgz.constants;

import java.util.regex.*;
import org.apache.commons.lang.*;
import com.reign.kfgz.dto.*;
import java.util.*;
import com.reign.kf.comm.util.*;

public class KfgzCommConstants
{
    public static final long DAY_SEC = 86400000L;
    public static final long HOUR_SEC_MS = 3600000L;
    public static final long MINUTE_SEC = 60000L;
    public static final long MINUTE_SEC_30 = 1800000L;
    public static final long HOUR_SEC = 3600L;
    public static final long SEC_MS = 1000L;
    public static final String SPLIT1 = ",";
    public static final String SPLIT2 = ":";
    public static final String SPLIT3 = "#";
    public static final int GROUP_BASE_COUNT = 6;
    public static final int RANKTOPNUM = 10;
    public static final int FORCEID1 = 1;
    public static final int FORCEID2 = 2;
    public static final int CITY_TYPE_NORMAL = 0;
    public static final int CITY_TYPE_CAPITAL = 1;
    public static final int CITY_TYPE_KEYPOINT = 2;
    public static final int CITY_TYPE_RESOURCE = 3;
    public static final int CITY_TYPE_JIEBING = 4;
    public static final int RESULT_SUCC = 1;
    public static final int RESULT_FAILED = 2;
    public static final int RESULT_BATTLE_END = 5;
    public static final int RESULT_SAME_PLAYER = 3;
    public static final int CHOOSESTRATEGY_SUC = 1;
    public static final int CHOOSESTRATEGY_FAILED = 2;
    public static final int MILITARY_BUILDING_STATE_OFF = 0;
    public static final int MILITARY_BUILDING_STATE_ON = 1;
    public static final int MILITARY_BUILDING_BATTLE = 2;
    public static final int MILITARY_BUILDING_NATION_BATTLE = 3;
    public static final int MILITARY_OCCUPY_BATTLE = 4;
    public static final int GENERAL_STATE_SEARCH = 5;
    public static final int GENERAL_STATE_MOVE = 6;
    public static final int GENERAL_STATE_MINE_BATTLE = 7;
    public static final int GENERAL_STATE_NATION_RANK_BATTLE = 8;
    public static final int GENERAL_STATE_KU_WD_BATTLE = 9;
    public static final int GENERAL_STATE_CITY_BATTLE = 10;
    public static final int GENERAL_STATE_CITY_ONEVSONE_BATTLE = 13;
    public static final int GENERAL_STATE_BARBARAIN_BATTLE = 14;
    public static final int GENERAL_STATE_BARBARAIN_ONEVSONE_BATTLE = 15;
    public static final int GENERAL_STATE_IN_TEAM = 16;
    public static final int GENERAL_STATE_DUEL = 17;
    public static final int GENERAL_STATE_CITY_EVENT = 18;
    public static final int GENERAL_STATE_JUBEN = 19;
    public static final int GENERAL_STATE_JUBEN_ONEVSONE = 20;
    public static final int TEAMGZIDMASKLEN = 25;
    public static final int GENERAL_STATE_IN_KFGZ_BATTLE = 1003;
    public static final int GENERAL_STATE_KFGZCITY_ONEVSONE_BATTLE = 1013;
    public static final int GENERAL_STATE_IN_KFGZ_JTJ = 1015;
    public static final String WORLD_DUE_COPPER_E = "World.Due.CopperE";
    public static final String WORLD_INITDUE_COPPER_E = "World.InitDue.CopperE";
    public static final String WORLD_DUE_LV_COPPER_E = "World.Due.LvCopperE";
    public static final String WORLD_INITDUE_LV_COPPER_E = "World.InitDue.LvCopperE";
    public static final String CITY_CNUM = "cnum";
    public static final String CITY_WIN = "win";
    public static final String CITY_LOST = "lost";
    public static final String UPDOWNRULE_UP = "up";
    public static final String UPDOWNRULE_DOWN = "down";
    
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
    
    public static long getTeamIdByGzId(final long id, final long gzId) {
        return id << 25 | gzId;
    }
    
    public static long getBattleTimeByRuleBattleTime(final String battleTime) {
        return getTimeByString(battleTime);
    }
    
    private static long getTimeByString(final String battleTime) {
        final Pattern pa = Pattern.compile("(\\d+)*(h|m|s)");
        final Matcher ma = pa.matcher(battleTime);
        if (ma.matches()) {
            final int value = Integer.parseInt(ma.group(1));
            final String type = ma.group(2);
            long rs = 0L;
            if (type.equals("h")) {
                rs = 3600L * value * 1000L;
            }
            else if (type.equals("m")) {
                rs = 60000L * value;
            }
            else if (type.equals("s")) {
                rs = value * 1000L;
            }
            return rs;
        }
        return -1L;
    }
    
    public static long[] getBattleDelayInfo(final String battleDelayInfo) {
        final String[] ss = battleDelayInfo.split(",");
        final long[] res = new long[ss.length + 1];
        String[] array;
        for (int length = (array = ss).length, i = 0; i < length; ++i) {
            final String s = array[i];
            final String[] ss2 = s.split(":");
            final int layer = Integer.parseInt(ss2[0]);
            final long delay = getTimeByString(ss2[1]);
            res[layer] = delay;
        }
        return res;
    }
    
    public static int[] getGroupInfoByNum(final int allGsNum) {
        final int gNum = (allGsNum + 6 - 1) / 6;
        final int[] res = new int[gNum];
        final int remain = allGsNum % 6;
        if (gNum == 1) {
            res[0] = allGsNum;
            return res;
        }
        for (int i = 0; i < gNum - 2; ++i) {
            res[i] = 6;
        }
        final int remain2 = 6 + remain;
        int last1 = remain2 / 2;
        int last2 = remain2 - last1;
        if (last2 % 2 == 1) {
            ++last2;
            --last1;
        }
        res[gNum - 2] = last2;
        res[gNum - 1] = last1;
        return res;
    }
    
    public static final int getGzId(final int layer, final int gId, final int pos, final int round) {
        return round % 100 + pos % 10 * 100 + gId % 1000 * 1000 + layer % 100 * 1000000;
    }
    
    public static final int getRoundByGzId(final int gzId) {
        return gzId % 100;
    }
    
    public static final int getLayerByGzID(final int gzId) {
        return gzId / 1000000 % 100;
    }
    
    public static final int getGIdByGzID(final int gzId) {
        return gzId / 1000 % 1000;
    }
    
    public static int[][] getBattleScheduleInfoByNumAndRound(final int count, final int round) {
        final int count2 = (count + 1) / 2;
        final int count3 = count2 * 2;
        final int[][] res = new int[count][count + 1];
        int num = 0;
        int[] pos;
        if (count % 2 == 0) {
            pos = new int[count];
            for (int i = 0; i < count; ++i) {
                pos[i] = i + 1;
            }
        }
        else {
            pos = new int[count + 1];
            for (int i = 0; i <= count; ++i) {
                pos[i] = i;
            }
        }
        for (int i = 1; i < count3; ++i) {
            for (int j = 0; j < count2; ++j) {
                final int a = count3 - j - 1;
                if (pos[j] != 0 && pos[a] != 0) {
                    res[i - 1][pos[j]] = pos[a];
                    res[i - 1][pos[a]] = pos[j];
                    ++num;
                }
            }
            final int value = pos[count3 - 1];
            for (int k = count3 - 1; k > 1; --k) {
                pos[k] = pos[k - 1];
            }
            pos[1] = value;
        }
        final int[][] roundRes = new int[round][count + 1];
        for (int l = 0; l < round; ++l) {
            for (int k = 1; k <= count; ++k) {
                if (l < count - 1) {
                    roundRes[l][k] = res[l][k];
                }
                else {
                    roundRes[l][k] = res[l % (count - 1)][k];
                }
            }
        }
        for (int l = 0; l < round; ++l) {
            for (int k = 1; k <= count; ++k) {
                System.out.print(String.valueOf(roundRes[l][k]) + ",");
            }
            System.out.println();
        }
        return roundRes;
    }
    
    public static void main(final String[] args) {
        getBattleScheduleInfoByNumAndRound(1, 5);
    }
    
    public static int getGzCityReward(final String cityR) {
        final String[] ss = cityR.split(",");
        String[] array;
        for (int length = (array = ss).length, i = 0; i < length; ++i) {
            final String s = array[i];
            final String[] ss2 = s.split(":");
            if (ss2[0].equals("cnum")) {
                return Integer.parseInt(ss2[1]);
            }
        }
        return 0;
    }
    
    public static int getGzWinReward(final String cityR) {
        final String[] ss = cityR.split(",");
        String[] array;
        for (int length = (array = ss).length, i = 0; i < length; ++i) {
            final String s = array[i];
            final String[] ss2 = s.split(":");
            if (ss2[0].equals("win")) {
                return Integer.parseInt(ss2[1]);
            }
        }
        return 0;
    }
    
    public static int getGzLostReward(final String cityR) {
        final String[] ss = cityR.split(",");
        String[] array;
        for (int length = (array = ss).length, i = 0; i < length; ++i) {
            final String s = array[i];
            final String[] ss2 = s.split(":");
            if (ss2[0].equals("lost")) {
                return Integer.parseInt(ss2[1]);
            }
        }
        return 0;
    }
    
    public static int[] getDownUpInfoByRule(final String upDownRule) {
        final int[] res = { 0, 999 };
        if (StringUtils.isBlank(upDownRule)) {
            return res;
        }
        final String[] ss = upDownRule.split(",");
        String[] array;
        for (int length = (array = ss).length, i = 0; i < length; ++i) {
            final String s = array[i];
            final String[] ss2 = s.split(":");
            if (ss2[1].equals("up")) {
                res[0] = Integer.parseInt(ss2[0]);
            }
            else if (ss2[1].equals("down")) {
                res[1] = Integer.parseInt(ss2[0]);
            }
        }
        return res;
    }
    
    public static StringBuilder getGeneralsInfo(final int generalLv, final String generalName, final String generalPic, final int quality) {
        final StringBuilder sb = new StringBuilder();
        sb.append(generalName).append("#").append(generalLv).append("#").append(generalPic).append("#").append(quality).append(",");
        return sb;
    }
    
    public static List<SimpleGInfo> getGInfosFromGInfoString(final String gInfos) {
        final List<SimpleGInfo> list = new ArrayList<SimpleGInfo>();
        if (StringUtils.isBlank(gInfos)) {
            return list;
        }
        final String[] ss = gInfos.split(",");
        String[] array;
        for (int length = (array = ss).length, i = 0; i < length; ++i) {
            final String s = array[i];
            final SimpleGInfo gInfo = new SimpleGInfo();
            final String[] ss2 = s.split("#");
            gInfo.setgName(ss2[0]);
            gInfo.setgLv(Integer.parseInt(ss2[1]));
            gInfo.setPic(ss2[2]);
            gInfo.setQuality(Integer.parseInt(ss2[3]));
            list.add(gInfo);
        }
        return list;
    }
    
    public static String getKfgzKey(final int cId, final int seasonId) {
        final StringBuffer sb = new StringBuffer();
        sb.append(235);
        sb.append((int)Math.cbrt(cId));
        sb.append("kfgz");
        sb.append(cId);
        sb.append(seasonId);
        sb.append(cId % 345);
        sb.append(MD5SecurityUtil.code(String.valueOf(cId)));
        return MD5SecurityUtil.code(sb.toString());
    }
}
