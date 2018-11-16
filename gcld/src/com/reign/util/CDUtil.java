package com.reign.util;

import java.util.*;
import org.apache.commons.lang.*;

public final class CDUtil
{
    public static long getCD(final long totalCDTime, final Date lastCDTime, final Date nowDate) {
        long cd = totalCDTime - ((lastCDTime == null) ? totalCDTime : (nowDate.getTime() - lastCDTime.getTime()));
        if (cd < 0L) {
            cd = 0L;
        }
        return cd;
    }
    
    public static long getCD(final long totalCDTime, final Long lastCDTime, final Date nowDate) {
        long cd = totalCDTime - ((lastCDTime == null) ? totalCDTime : (nowDate.getTime() - lastCDTime));
        if (cd < 0L) {
            cd = 0L;
        }
        return cd;
    }
    
    public static boolean isInCD(final long totalCDTime, final Date lastCDTime, final Date nowDate) {
        final long cd = getCD(totalCDTime, lastCDTime, nowDate);
        return cd > 0L;
    }
    
    public static boolean isInCD(final long totalCDTime, final Long lastCDTime, final Date nowDate) {
        final long cd = getCD(totalCDTime, lastCDTime, nowDate);
        return cd > 0L;
    }
    
    public static boolean isInCD(final Date endTime, final Date nowDate) {
        boolean result = true;
        result = (endTime != null && nowDate.before(endTime));
        return result;
    }
    
    public static boolean isInCD(final Date endTime, final Date nowDate, final long lastTime, final int cdFlag, final int eventFlag) {
        boolean result = true;
        result = (endTime != null && nowDate.before(endTime) && ((cdFlag & eventFlag) == eventFlag || endTime.getTime() - nowDate.getTime() - lastTime > 0L));
        return result;
    }
    
    public static boolean isInCD(final Long endTime, final Date nowDate, final long lastTime, final int cdFlag, final int eventFlag) {
        boolean result = true;
        result = (endTime != null && nowDate.getTime() <= endTime && ((cdFlag & eventFlag) == eventFlag || endTime - nowDate.getTime() - lastTime > 0L));
        return result;
    }
    
    public static boolean isInCD(final Long endTime, final Date nowDate) {
        boolean result = true;
        result = (endTime != null && nowDate.getTime() < endTime);
        return result;
    }
    
    public static long getCD(final Date endTime, final Date nowDate) {
        long cd = 0L;
        if (endTime == null) {
            cd = 0L;
        }
        else {
            cd = endTime.getTime() - nowDate.getTime();
            if (cd < 0L) {
                cd = 0L;
            }
        }
        return cd;
    }
    
    public static long getCD(final Long endTime, final Date nowDate) {
        long cd = 0L;
        if (endTime == null) {
            cd = 0L;
        }
        else {
            cd = endTime - nowDate.getTime();
            if (cd < 0L) {
                cd = 0L;
            }
        }
        return cd;
    }
    
    public static long getCD(final Long endTime, final long currentTime) {
        long cd = 0L;
        if (endTime == null) {
            cd = 0L;
        }
        else {
            cd = endTime - currentTime;
            if (cd < 0L) {
                cd = 0L;
            }
        }
        return cd;
    }
    
    public static long getCD(long oldCD, final int secend) {
        final long now = System.currentTimeMillis();
        if (oldCD == 0L || oldCD - now <= 0L) {
            oldCD = now;
        }
        return oldCD + secend * 1000;
    }
    
    public static String getCDStr(final long cd) {
        final StringBuilder builder = new StringBuilder(16);
        final int day = (int)(cd / 86400000L);
        long temp = cd % 86400000L;
        final int hour = (int)(temp / 3600000L);
        temp %= 3600000L;
        final int minute = (int)(temp / 60000L);
        temp %= 60000L;
        final int sec = (int)(temp / 1000L);
        if (day != 0) {
            builder.append(day).append(LocalMessages.L_TIME_DAY).append(LocalMessages.L_SYMBOL_BLANK);
        }
        if (hour != 0) {
            builder.append(StringUtils.leftPad(String.valueOf(hour), 2, '0')).append(LocalMessages.L_SYMBOL_COLON);
        }
        builder.append(StringUtils.leftPad(String.valueOf(minute), 2, '0')).append(LocalMessages.L_SYMBOL_COLON);
        builder.append(StringUtils.leftPad(String.valueOf(sec), 2, '0'));
        return builder.toString();
    }
    
    public static void main(final String[] args) {
        System.out.println(getCDStr(3601000L));
    }
}
