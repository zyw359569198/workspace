package com.reign.gcld.rank.common;

import com.reign.gcld.battle.common.*;
import org.apache.commons.lang.*;
import java.util.*;

public class TimeForNationTask
{
    private static ErrorSceneLog errorSceneLog;
    private int hour;
    private int minute;
    
    static {
        TimeForNationTask.errorSceneLog = ErrorSceneLog.getInstance();
    }
    
    public TimeForNationTask(final String time) {
        if (StringUtils.isBlank(time)) {
            TimeForNationTask.errorSceneLog.error("time is empty....");
        }
        try {
            final String[] single = time.split(":");
            final int hour = Integer.parseInt(single[0]);
            final int minute = Integer.parseInt(single[1]);
            this.hour = hour;
            this.minute = minute;
        }
        catch (Exception e) {
            TimeForNationTask.errorSceneLog.error("init time wrong...");
            TimeForNationTask.errorSceneLog.error(e.getMessage());
            TimeForNationTask.errorSceneLog.error(this, e);
        }
    }
    
    public Date getDate() {
        final Calendar calendar = Calendar.getInstance();
        calendar.set(11, this.hour);
        calendar.set(12, this.minute);
        calendar.set(13, 0);
        calendar.set(14, 0);
        return calendar.getTime();
    }
    
    public int getHour() {
        return this.hour;
    }
    
    public int getMinute() {
        return this.minute;
    }
    
    public static boolean isInTime(final TimeForNationTask start, final TimeForNationTask end) {
        final Date startDate = start.getDate();
        final Date endDate = end.getDate();
        final Date nowDate = new Date();
        return nowDate.getTime() >= startDate.getTime() && nowDate.getTime() < endDate.getTime();
    }
    
    public static boolean isInTime(final TimeForNationTask start, final TimeForNationTask end, final Date date) {
        final Date startDate = start.getDate();
        final Date endDate = end.getDate();
        return date.getTime() >= startDate.getTime() && date.getTime() <= endDate.getTime();
    }
}
