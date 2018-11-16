package com.reign.util.time;

import com.reign.util.*;
import org.apache.commons.lang.*;
import java.util.*;

public class TimeUtils
{
    private static volatile Calendar upCalendar;
    public static final int DEFAULT_YEAR = 184;
    public static final int DEFAULT_SEASON = 1;
    public static final int CHANGE_SEASON_TIME = 5;
    public static final int SPRING = 1;
    public static final int SUMMER = 2;
    public static final int AUTUMN = 3;
    public static final int WINTER = 4;
    
    static {
        TimeUtils.upCalendar = null;
    }
    
    public static void init(final long time) {
        (TimeUtils.upCalendar = Calendar.getInstance()).setTimeInMillis(time);
        TimeUtils.upCalendar.set(11, 5);
        TimeUtils.upCalendar.set(12, 0);
        TimeUtils.upCalendar.set(13, 0);
        TimeUtils.upCalendar.set(14, 0);
    }
    
    public static long time(final int year, final int season) {
        final int day = (year - 184) * 4 + (season - 1);
        final Calendar cg = Calendar.getInstance();
        cg.setTime(TimeUtils.upCalendar.getTime());
        cg.add(6, day);
        return cg.getTimeInMillis();
    }
    
    public static long getStartServerTime() {
        return TimeUtils.upCalendar.getTimeInMillis();
    }
    
    public static Calendar getStartServerTimeCalendar() {
        return TimeUtils.upCalendar;
    }
    
    public static int getServerAge() {
        final Calendar cg1 = TimeUtils.upCalendar;
        final Calendar cg2 = Calendar.getInstance();
        final int days = (int)((cg2.getTimeInMillis() - cg1.getTimeInMillis()) / 86400000L);
        return days;
    }
    
    public static int getYear(final long time) {
        final Calendar cg1 = TimeUtils.upCalendar;
        final Calendar cg2 = Calendar.getInstance();
        cg2.setTimeInMillis(time);
        int days = (int)((cg2.getTimeInMillis() - cg1.getTimeInMillis()) / 86400000L);
        if (days < 0) {
            days = 0;
        }
        return 184 + days / 4;
    }
    
    public static int getSeason(final long time) {
        final Calendar cg1 = TimeUtils.upCalendar;
        final Calendar cg2 = Calendar.getInstance();
        cg2.setTimeInMillis(time);
        int days = (int)((cg2.getTimeInMillis() - cg1.getTimeInMillis()) / 86400000L);
        if (days < 0) {
            days = 0;
        }
        final int season = (1 + days) % 4;
        return (season == 0) ? 4 : season;
    }
    
    public static Tuple<Integer, Integer> getYearAndSeason(final long time) {
        final Tuple<Integer, Integer> tuple = new Tuple<Integer, Integer>();
        final Calendar cg1 = TimeUtils.upCalendar;
        final Calendar cg2 = Calendar.getInstance();
        cg2.setTimeInMillis(time);
        int days = (int)((cg2.getTimeInMillis() - cg1.getTimeInMillis()) / 86400000L);
        if (days < 0) {
            days = 0;
        }
        tuple.left = 184 + days / 4;
        final int season = (1 + days) % 4;
        tuple.right = ((season == 0) ? 4 : season);
        return tuple;
    }
    
    public static String getYearAndSeasonDesc(final long time) {
        final Tuple<Integer, Integer> tuple = getYearAndSeason(time);
        switch (tuple.right) {
            case 1: {
                return MessageFormatter.format(LocalMessages.T_TIME_10001, tuple.left);
            }
            case 2: {
                return MessageFormatter.format(LocalMessages.T_TIME_10002, tuple.left);
            }
            case 3: {
                return MessageFormatter.format(LocalMessages.T_TIME_10003, tuple.left);
            }
            case 4: {
                return MessageFormatter.format(LocalMessages.T_TIME_10004, tuple.left);
            }
            default: {
                return null;
            }
        }
    }
    
    public static String getYearAndSeasonAndTimeDesc(final long time) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        return MessageFormatter.format(LocalMessages.T_TIME_10005, getYearAndSeasonDesc(time), StringUtils.leftPad(String.valueOf(calendar.get(11)), 2, '0'), StringUtils.leftPad(String.valueOf(calendar.get(12)), 2, '0'));
    }
    
    public static boolean isInterval(final Date lastTime, final Date now, final int hour, final int min) {
        final Calendar cg = Calendar.getInstance();
        cg.set(11, hour);
        cg.set(12, min);
        cg.set(13, 0);
        cg.set(14, 0);
        final Date clearPoint = cg.getTime();
        final Calendar cg2 = Calendar.getInstance();
        final Calendar cg3 = Calendar.getInstance();
        final Calendar cg4 = Calendar.getInstance();
        cg2.setTime(lastTime);
        cg3.setTime(now);
        cg4.setTime(clearPoint);
        cg4.add(5, -1);
        return clearPoint.after(cg2.getTime()) && (now.after(clearPoint) || cg2.before(cg4));
    }
    
    public static int getIntervalHalfHourNum(final Date lastDate, final Date nowDate) {
        if (lastDate == null || nowDate == null || nowDate.before(lastDate)) {
            return 0;
        }
        final Calendar now = Calendar.getInstance();
        now.setTime(nowDate);
        final int nowMinute = now.get(12);
        now.set(12, 0);
        now.set(13, 0);
        now.set(14, 0);
        final Calendar last = Calendar.getInstance();
        last.setTime(lastDate);
        final int lastMinute = last.get(12);
        last.set(12, 0);
        last.set(13, 0);
        last.set(14, 0);
        int num = (int)((now.getTimeInMillis() - last.getTimeInMillis()) / 3600000L);
        num *= 2;
        if (nowMinute >= 30 && lastMinute < 30) {
            ++num;
        }
        else if (nowMinute < 30 && lastMinute >= 30) {
            num = ((--num > 0) ? num : 0);
        }
        return num;
    }
    
    public static int getIntervalHourNum(final Date lastDate, final Date nowDate) {
        if (lastDate == null || nowDate == null || nowDate.before(lastDate)) {
            return 0;
        }
        final Calendar now = Calendar.getInstance();
        now.setTime(nowDate);
        now.set(12, 0);
        now.set(13, 0);
        now.set(14, 0);
        final Calendar last = Calendar.getInstance();
        last.setTime(lastDate);
        last.set(12, 0);
        last.set(13, 0);
        last.set(14, 0);
        final int num = (int)((now.getTimeInMillis() - last.getTimeInMillis()) / 3600000L);
        return num;
    }
    
    public static String getChangeReasonCronExpression() {
        final String rtn = MessageFormatter.format("0 0 {0} ? * * *", 5);
        return rtn;
    }
}
