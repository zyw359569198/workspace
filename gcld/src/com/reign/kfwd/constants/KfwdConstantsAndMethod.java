package com.reign.kfwd.constants;

import com.reign.kfwd.dto.*;
import java.util.*;
import org.apache.commons.lang.*;
import com.reign.kf.comm.util.*;

public class KfwdConstantsAndMethod
{
    public static final int MATCH_GLOBALSTATE_UNBEGIN = 0;
    public static final int MATCH_GLOBALSTATE_ACTIVE = 10;
    public static final int MATCH_GLOBALSTATE_SIGNUP = 20;
    public static final int MATCH_GLOBALSTATE_SIGNUP_FINISH = 30;
    public static final int MATCH_GLOBALSTATE_PREPARE = 30;
    public static final int MATCH_GLOBALSTATE_BATTLE = 50;
    public static final int MATCH_GLOBALSTATE_BATTLE_FINISH = 60;
    public static final int MATCH_GLOBALSTATE_SEASON_FINISH = 70;
    public static volatile int MAXROUND;
    public static int MAXFIGHTDAY;
    public static final int BATTLE_WIN = 1;
    public static final int BATTLE_LOST = 2;
    public static final int BATTLE_NONE = 0;
    public static final int RESULT_SUCC = 1;
    public static final int RESULT_FAIL = 0;
    public static final int MAXSROUND = 1;
    public static final int WINROUND_BATTLENUM = 1;
    public static final double INSPIREEFFECT = 0.04;
    public static final int LEVELRANGE_TYPE_ARMY = 2;
    public static final int SROUND_TOTAL_SCORE = 32;
    public static final int SROUND_AVERAGE_SCORE = 16;
    public static final int DOUBLECOEF_ONETIMEDOUBE = 1;
    public static final int DOUBLECOEF_TWOTIMEDOUBE = 2;
    public static final int DOUBLECOEF_THREETIMEDOUBE = 3;
    public static final int SHOWRANKINGSIZE = 8;
    public static final int SHOWRANKINGBETWEENSIZE = 4;
    static final long battleMask;
    public static final int isAttKey = 317;
    public static final String NORMALSPLIT = ",";
    public static final int REWARDSTATE_GET = 1;
    public static final String ID_BINGTIE_PATTERN = "bingtie";
    public static final String ID_FOOD_PATTERN = "food";
    public static final String NORMAL_PATTERN = "(\\w+):(\\d+)";
    
    static {
        KfwdConstantsAndMethod.MAXROUND = 16;
        KfwdConstantsAndMethod.MAXFIGHTDAY = 3;
        battleMask = Math.round(Math.pow(2.0, 24.0) - 1.0);
    }
    
    public static int[] getResultByInspireValue(final int value) {
        final int[] res = { value >> 4, value & 0xF };
        return res;
    }
    
    public static int getInspireValue(final int attnum, final int defnum) {
        return attnum << 4 | defnum;
    }
    
    public static int addsRoundBattleRes(final int res, final int sRound, final int battleRes) {
        if (sRound > 3 || sRound < 1) {
            return res;
        }
        final int t = sRound - 1;
        return (res & ~(3 << t * 2)) | battleRes << t * 2;
    }
    
    public static int[] getBattleResByRes(final int value) {
        final int[] res = { value & 0x3, (value & 0xC) >> 2, (value & 0x30) >> 4 };
        return res;
    }
    
    public static WdLastRoundInfo getLastRoundInfo(final long historyRes) {
        final WdLastRoundInfo info = new WdLastRoundInfo();
        final long isatt = historyRes & 0x1L;
        info.setAttack(isatt == 1L);
        final long isRound3Att = historyRes >> 1 & 0x1L;
        info.setRound3IsAttacker(isRound3Att == 1L);
        info.setBattleRes((int)(historyRes >> 2 & 0x3FL));
        info.setInspire1((int)(historyRes >> 8 & 0xFFL));
        info.setInspire2((int)(historyRes >> 16) & 0xFF);
        return info;
    }
    
    public static long addLastRoundInfo(final long historyRes, final boolean lastIsAtt1, final boolean round3IsAtt, final int lastBattleRes, final int lastp1Inspire, final int lastp2Inspire) {
        final long isatt = lastIsAtt1 ? 1 : 0;
        final long r3IsAtt = round3IsAtt ? 1 : 0;
        return historyRes >> 24 << 24 | isatt | r3IsAtt << 1 | lastBattleRes << 2 | lastp1Inspire << 8 | lastp2Inspire << 16;
    }
    
    public static int[] getLastWinInfo(final long historyRes) {
        final int[] res = new int[KfwdConstantsAndMethod.MAXROUND];
        final long winRes = historyRes >> 24;
        for (int i = 0; i < KfwdConstantsAndMethod.MAXROUND; ++i) {
            res[i] = (int)(winRes >> i * 2 & 0x3L);
        }
        return res;
    }
    
    public static long addHisRoundBattleRes(final long hisRes, final int round, final long battleWin) {
        if (round < 1 || round > KfwdConstantsAndMethod.MAXROUND) {
            return hisRes;
        }
        final long mask = ~(3L << round * 2 + 22);
        return (hisRes & mask) | battleWin << round * 2 + 22;
    }
    
    public static long addAllBattleHisRes(final long historyRes, final long res1) {
        return (historyRes & KfwdConstantsAndMethod.battleMask) | res1;
    }
    
    public static void main(final String[] args) {
        final int[] res1 = getBattleResByRes(9);
        int[] array;
        for (int length = (array = res1).length, i = 0; i < length; ++i) {
            final int re = array[i];
            System.out.println(re);
        }
    }
    
    public static List<Integer[]> parseLevelRangeList(final String rangeList) {
        final List<Integer[]> list = new ArrayList<Integer[]>();
        if (rangeList == null) {
            return list;
        }
        final String[] ss = rangeList.split(",");
        String[] array;
        for (int length = (array = ss).length, i = 0; i < length; ++i) {
            final String s = array[i];
            if (!StringUtils.isBlank(s)) {
                final Integer[] res = parseLevelRangeString(s);
                if (res != null) {
                    list.add(res);
                }
            }
        }
        return list;
    }
    
    public static Integer[] parseLevelRangeString(final String s) {
        final Integer[] res = { 0, 0 };
        if (s == null || (!s.matches("(\\d)+\\-(\\d)+") && !s.matches("(\\d)+"))) {
            return null;
        }
        final String[] ss = s.split("-");
        int i = 0;
        String[] array;
        for (int length = (array = ss).length, j = 0; j < length; ++j) {
            final String s2 = array[j];
            final Integer r = Integer.valueOf(s2);
            res[i] = r;
            ++i;
        }
        return res;
    }
    
    public static long getScheduleDelay(final int scheduleId) {
        return 60000 * (scheduleId / 10000 % 10 - 1) + (scheduleId % 10 - 1) * 6 * 1000;
    }
    
    public static List<Integer> parseRuleList(final String rewardRuleGroupType) {
        final String[] ss = rewardRuleGroupType.split(",");
        final List<Integer> list = new ArrayList<Integer>();
        String[] array;
        for (int length = (array = ss).length, i = 0; i < length; ++i) {
            final String s = array[i];
            list.add(Integer.parseInt(s));
        }
        return list;
    }
    
    public static boolean isC1AttackerRound3(final int matchId, final int round, final int scheduleId) {
        final int res = (matchId + round + scheduleId / 100000 % 1000) * 137 / 10 % 2;
        return res != 1;
    }
    
    public static final int getTicketByScore(final int basicScore, final int winCoef, final int p1Score) {
        return (int)(Math.ceil((basicScore + winCoef * p1Score * (p1Score / 100 + 2)) / 50.0f) * 50.0);
    }
    
    public static long getBattleIdByMatch(final long seasonId, final long scheduleId, final long matchId, final long round) {
        return seasonId << 40 | scheduleId << 20 | matchId << 5 | round;
    }
    
    public static int[] getWinScore(final boolean isAttWin, final int attRemainNum, final int defRemainNum) {
        if (isAttWin) {
            return new int[] { 20 + attRemainNum, 16 - attRemainNum };
        }
        return new int[] { 16 - defRemainNum, 20 + defRemainNum };
    }
    
    public static int[] getRewardInfoByDay(final int rewardDay, final String dayTicket, final int dayReward) {
        final int[] res = new int[3];
        if (dayTicket == null) {
            return res;
        }
        int ticket = 0;
        final String[] st = dayTicket.split(",");
        ticket = Integer.valueOf(st[rewardDay - 1]);
        res[0] = (dayReward >> rewardDay - 1 & 0x1);
        res[1] = ticket;
        return res;
    }
    
    public static int addGetDayReward(final int day, final int dayReward) {
        return dayReward | 1 << day - 1;
    }
    
    public static int getDayReward(final int day, final int dayReward) {
        return dayReward >> day - 1 & 0x1;
    }
    
    public static int getRanTerrain(final int matchId, final int round, final int seasonId, final int scheduleId) {
        int ten = (matchId + round + seasonId + scheduleId) % 3 + 1;
        if (ten == 2) {
            ten = 4;
        }
        return ten;
    }
    
    public static int getTerrainValByTerrain(final int terrain) {
        if (terrain == 3) {
            return 2;
        }
        if (terrain == 4) {
            return 3;
        }
        if (terrain == 5 || terrain == 6) {
            return 4;
        }
        return 1;
    }
    
    public static int getNextDoubleCost(final double goldBasic, final int doubleCoef, final int ticket) {
        double c1 = 0.0;
        int doubleNum = 1;
        if (doubleCoef == 0) {
            c1 = 100.0;
        }
        else if (doubleCoef == 1) {
            c1 = 50.0;
            doubleNum = 2;
        }
        else if (doubleCoef == 2) {
            c1 = 25.0;
            doubleNum = 4;
        }
        if (c1 == 0.0) {
            return 0;
        }
        return (int)Math.ceil(doubleNum * ticket / (c1 * 10.0) * goldBasic);
    }
    
    public static String getCertifacateByCId(final int completedId) {
        final String s = String.valueOf(completedId % 7) + completedId + completedId % 137 + completedId % 37;
        return MD5SecurityUtil.code(s);
    }
    
    public static int getTicketByDoubleCoef(final int ticket, final int coef) {
        if (coef == 0) {
            return ticket;
        }
        if (coef == 1) {
            return ticket * 2;
        }
        if (coef == 2) {
            return ticket * 4;
        }
        if (coef == 3) {
            return ticket * 8;
        }
        return 0;
    }
}
