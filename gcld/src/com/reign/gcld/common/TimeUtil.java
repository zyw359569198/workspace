package com.reign.gcld.common;

import java.util.*;
import java.text.*;

public class TimeUtil
{
    public static int nextHour() {
        final int nextHour = Calendar.getInstance().get(11) + 1;
        return (nextHour == 24) ? 0 : nextHour;
    }
    
    public static long nextHourMS() {
        final Calendar calendar = Calendar.getInstance();
        final long nowMs = calendar.getTimeInMillis();
        calendar.add(11, 1);
        calendar.set(12, 0);
        calendar.set(13, 0);
        calendar.set(14, 0);
        return calendar.getTimeInMillis() - nowMs;
    }
    
    public static long getSpecialTime(final int hour, final int minues) {
        final Calendar calendar = Calendar.getInstance();
        calendar.set(11, hour);
        calendar.set(12, minues);
        calendar.set(13, 0);
        calendar.set(14, 0);
        return calendar.getTimeInMillis();
    }
    
    public static int getHour() {
        return Calendar.getInstance().get(11);
    }
    
    public static int getMonth() {
        return Calendar.getInstance().get(2) + 1;
    }
    
    public static int getBeforeMonth(final int beforeDay) {
        final Calendar ca = Calendar.getInstance();
        ca.setTimeInMillis(System.currentTimeMillis() - beforeDay * 24 * 60 * 60 * 1000);
        return ca.get(2) + 1;
    }
    
    public static int getDay() {
        return Calendar.getInstance().get(5);
    }
    
    public static int getBeforeDay(final int beforeDay) {
        final Calendar ca = Calendar.getInstance();
        ca.setTimeInMillis(System.currentTimeMillis() - beforeDay * 24 * 60 * 60 * 1000);
        return ca.get(5);
    }
    
    public static long now2specMs(final long ms) {
        return ms - System.currentTimeMillis();
    }
    
    public static Date nowAddMinutes(final int minutes) {
        final Calendar calendar = Calendar.getInstance();
        calendar.add(12, minutes);
        return calendar.getTime();
    }
    
    public static Date nowAddHours(final int hours) {
        final Calendar calendar = Calendar.getInstance();
        calendar.add(11, hours);
        return calendar.getTime();
    }
    
    public static Date nowAddMs(final long ms) {
        return new Date(System.currentTimeMillis() + ms);
    }
    
    public static Date specialAddMinutes(final Date date, final int minutes) {
        return new Date(date.getTime() + minutes * 60 * 1000);
    }
    
    public static Date specialAddDays(final Date date, final int days) {
        return new Date(date.getTime() + days * 24 * 60 * 60 * 1000);
    }
    
    public static Date specialAddSeconds(final Date date, final int seconds) {
        return new Date(date.getTime() + seconds * 1000);
    }
    
    public static Date nowAddSeconds(final int seconds) {
        return new Date(System.currentTimeMillis() + seconds * 1000);
    }
    
    public static int specialToNowDays(final Date date) {
        final Calendar now = Calendar.getInstance();
        now.set(11, 0);
        now.set(12, 0);
        now.set(13, 0);
        now.set(14, 0);
        final Calendar spec = Calendar.getInstance();
        spec.setTime(date);
        spec.set(11, 0);
        spec.set(12, 0);
        spec.set(13, 0);
        spec.set(14, 0);
        return (int)((spec.getTimeInMillis() - now.getTimeInMillis()) / Constants.ONE_DAY_MS);
    }
    
    public static boolean in0To8() {
        final Calendar zero = Calendar.getInstance();
        zero.set(11, 0);
        zero.set(12, 0);
        zero.set(13, 0);
        zero.set(14, 0);
        final Calendar eight = Calendar.getInstance();
        eight.set(11, 8);
        eight.set(12, 0);
        eight.set(13, 0);
        eight.set(14, 0);
        final long now = System.currentTimeMillis();
        return zero.getTimeInMillis() <= now && now <= eight.getTimeInMillis();
    }
    
    public static long getNext1Day0Clock() {
        final Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(1);
        final int month = calendar.get(2);
        final int day = calendar.get(5);
        calendar.set(year, month, day, 23, 59, 59);
        return calendar.getTimeInMillis() + 1000L;
    }
    
    public static long getNext2Day0Clock() {
        final Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(1);
        final int month = calendar.get(2);
        final int day = calendar.get(5);
        calendar.set(year, month, day, 23, 59, 59);
        return calendar.getTimeInMillis() + 1000L + Constants.ONE_DAY_MS;
    }
    
    public static long getDay0ClackMS(final Date date) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(11, 0);
        calendar.set(12, 0);
        calendar.set(13, 0);
        calendar.set(14, 0);
        return calendar.getTimeInMillis();
    }
    
    public static long getCd(final Date date) {
        if (date == null) {
            return 0L;
        }
        final long cd = date.getTime() - System.currentTimeMillis();
        return (cd <= 0L) ? 0L : cd;
    }
    
    public static String getMysqlDateString(final Date date) {
        final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(date);
    }
    
    public static void main(final String[] args) {
        final Date hh = nowAddHours(1);
        System.out.println(specialToNowDays(hh));
    }
}
