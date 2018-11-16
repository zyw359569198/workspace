package com.reign.gcld.common.util;

import java.util.*;
import com.reign.util.*;
import com.reign.framework.exception.*;
import org.apache.commons.lang.*;
import com.reign.gcld.common.*;

public class TimeUtil
{
    private static volatile Calendar upCalendar;
    
    static {
        TimeUtil.upCalendar = null;
    }
    
    public static void init(final long time) {
        Constants.UP_TIME = getServerStartTime(time);
        (TimeUtil.upCalendar = Calendar.getInstance()).setTimeInMillis(Constants.UP_TIME);
        TimeUtil.upCalendar.set(11, 0);
        TimeUtil.upCalendar.set(12, 0);
        TimeUtil.upCalendar.set(13, 0);
        TimeUtil.upCalendar.set(14, 0);
    }
    
    public static long time(final int year, final int season) {
        final int day = (year - 184) * 4 + (season - 1);
        final Calendar cg = Calendar.getInstance();
        cg.setTime(TimeUtil.upCalendar.getTime());
        cg.add(6, day);
        return cg.getTimeInMillis();
    }
    
    public static String getTimeLeft(long time) {
        if (time < 0L) {
            time = 0L;
        }
        final long hour = time / 3600000L;
        time %= 3600000L;
        final long minute = time / 60000L;
        time %= 60000L;
        final long second = time / 1000L;
        final String hourStr = (hour / 10L >= 1L) ? String.valueOf(hour) : String.valueOf("0" + hour);
        final String MinuteStr = (minute / 10L >= 1L) ? String.valueOf(minute) : String.valueOf("0" + minute);
        final String SecondStr = (second / 10L >= 1L) ? String.valueOf(second) : String.valueOf("0" + second);
        return hourStr + ":" + MinuteStr + ":" + SecondStr;
    }
    
    public static long getStartServerTime() {
        return TimeUtil.upCalendar.getTimeInMillis();
    }
    
    public static Calendar getStartServerTimeCalendar() {
        return TimeUtil.upCalendar;
    }
    
    public static int getServerAge() {
        final Calendar cg1 = TimeUtil.upCalendar;
        final Calendar cg2 = Calendar.getInstance();
        final int days = (int)((cg2.getTimeInMillis() - cg1.getTimeInMillis()) / 86400000L);
        return days;
    }
    
    public static int getYear(final long time) {
        final Calendar cg1 = TimeUtil.upCalendar;
        final Calendar cg2 = Calendar.getInstance();
        cg2.setTimeInMillis(time);
        int days = (int)((cg2.getTimeInMillis() - cg1.getTimeInMillis()) / 86400000L);
        if (days < 0) {
            days = 0;
        }
        return 184 + days / 4;
    }
    
    public static int getSeason(final long time) {
        final Calendar cg1 = TimeUtil.upCalendar;
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
        final Tuple<Integer, Integer> tuple = new Tuple();
        final Calendar cg1 = TimeUtil.upCalendar;
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
                return MessageFormatter.format(LocalMessages.T_TIME_10001, new Object[] { tuple.left });
            }
            case 2: {
                return MessageFormatter.format(LocalMessages.T_TIME_10002, new Object[] { tuple.left });
            }
            case 3: {
                return MessageFormatter.format(LocalMessages.T_TIME_10003, new Object[] { tuple.left });
            }
            case 4: {
                return MessageFormatter.format(LocalMessages.T_TIME_10004, new Object[] { tuple.left });
            }
            default: {
                return null;
            }
        }
    }
    
    public static String getSeasonEffect(final int season) {
        switch (season) {
            case 1: {
                return LocalMessages.T_TIME_10007;
            }
            case 2: {
                return LocalMessages.T_TIME_10008;
            }
            case 3: {
                return LocalMessages.T_TIME_10009;
            }
            case 4: {
                return LocalMessages.T_TIME_10010;
            }
            default: {
                throw new InternalException("unknow season: " + season);
            }
        }
    }
    
    public static String getYearAndSeasonAndTimeDesc(final long time) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        return MessageFormatter.format(LocalMessages.T_TIME_10005, new Object[] { getYearAndSeasonDesc(time), StringUtils.leftPad(String.valueOf(calendar.get(11)), 2, '0'), StringUtils.leftPad(String.valueOf(calendar.get(12)), 2, '0') });
    }
    
    private static long getServerStartTime(final long time) {
        Long startTime = null;
        try {
            startTime = Long.parseLong(Configuration.getProperty("gcld.server.time"));
        }
        catch (NumberFormatException e) {
            startTime = null;
        }
        if (startTime == null) {
            Configuration.saveProperties("gcld.server.time", String.valueOf(time), "serverstate.properties");
            startTime = time;
        }
        return startTime;
    }
    
    public static void main(final String[] args) {
        System.out.println(getTimeLeft(60000L));
    }
}
