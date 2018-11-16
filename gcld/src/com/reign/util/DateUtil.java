package com.reign.util;

import org.apache.commons.logging.*;
import org.apache.commons.lang.*;
import java.text.*;
import java.util.*;

public class DateUtil
{
    private static Log log;
    private static final char DATE_HYPHEN = '-';
    private static final char DATE_SLASH = '/';
    private static final char DATE_SPLITER = ' ';
    private static final int SPLIT_YMD_COUNT = 3;
    private static final String DATETIME_SEPARATOR_FULL = "yyyy MM dd HH mm ss";
    private static final String DATETIME_SEPARATOR_YMDHM = "yyyy MM dd HH mm";
    private static final String DATE_SEPARATOR_FULL = "yyyy MM dd";
    private static final String DATE_SEPARATOR_YM = "yyyy MM";
    public static final String DATETIME_FULLSLASH = "yyyy/MM/dd HH:mm:ss";
    public static final String DATETIME_HM_SLASH = "yyyy/MM/dd HH:mm";
    public static final String DATE_FULLSLASH = "yyyy/MM/dd";
    public static final String DATE_YM_SLASH = "yyyy/MM";
    public static final String DATETIME_FULLHYPHEN = "yyyy-MM-dd HH:mm:ss";
    public static final String DATETIME_HM_HYPHEN = "yyyy-MM-dd HH:mm";
    public static final String DATE_FULLHYPHEN = "yyyy-MM-dd";
    public static final String DATE_YM_HYPHEN = "yyyy-MM";
    public static final String DATE_HM = "HH:mm";
    public static final String DATE_MS = "mm:SS";
    public static final String DATETIME_COMPACT_FULL_S = "yyyyMMddHHmmssS";
    public static final String DATETIME_COMPACT_FULL = "yyyyMMddHHmmss";
    public static final String DATETIME_COMPACT_YMDHM = "yyyyMMddHHmm";
    public static final String DATE_COMPACT_FULL = "yyyyMMdd";
    public static final String DATE_COMPACT_YM = "yyyyMM";
    public static final String DATE_YY = "yy";
    private static final String V_DATETIME_FULLSLASH = "[0-9]{4}\\/[0-9]{2}\\/[0-9]{2} [0-9]{2}\\:[0-9]{2}\\:[0-9]{2}";
    private static final String V_DATETIME_HM_SLASH = "[0-9]{4}\\/[0-9]{2}\\/[0-9]{2} [0-9]{2}\\:[0-9]{2}";
    private static final String V_DATE_FULLSLASH = "[0-9]{4}\\/[0-9]{2}\\/[0-9]{2}";
    private static final String V_DATE_YM_SLASH = "[0-9]{4}\\/[0-9]{2}";
    private static final long MINUTE_SEC = 60000L;
    private static final Map<String, String> SEPARATE_MAP;
    private static final Map<Integer, String> CONVERT_MAP;
    private static final Map<Integer, String> VALIDATE_MAP;
    
    static {
        DateUtil.log = LogFactory.getLog(DateUtil.class);
        SEPARATE_MAP = Collections.synchronizedMap(new HashMap<String, String>());
        CONVERT_MAP = Collections.synchronizedMap(new HashMap<Integer, String>());
        VALIDATE_MAP = Collections.synchronizedMap(new HashMap<Integer, String>());
        DateUtil.SEPARATE_MAP.put("yyyy/MM/dd HH:mm:ss", "yyyy MM dd HH mm ss");
        DateUtil.SEPARATE_MAP.put("yyyy/MM/dd HH:mm", "yyyy MM dd HH mm");
        DateUtil.SEPARATE_MAP.put("yyyy/MM/dd", "yyyy MM dd");
        DateUtil.SEPARATE_MAP.put("yyyy/MM", "yyyy MM");
        DateUtil.SEPARATE_MAP.put("yyyy-MM-dd HH:mm:ss", "yyyy MM dd HH mm ss");
        DateUtil.SEPARATE_MAP.put("yyyy-MM-dd HH:mm", "yyyy MM dd HH mm");
        DateUtil.SEPARATE_MAP.put("yyyy-MM-dd", "yyyy MM dd");
        DateUtil.SEPARATE_MAP.put("yyyy-MM", "yyyy MM");
        DateUtil.SEPARATE_MAP.put("yyyyMMddHHmmss", "yyyy MM dd HH mm ss");
        DateUtil.SEPARATE_MAP.put("yyyyMMddHHmm", "yyyy MM dd HH mm");
        DateUtil.SEPARATE_MAP.put("yyyyMMdd", "yyyy MM dd");
        DateUtil.SEPARATE_MAP.put("yyyyMM", "yyyy MM");
        DateUtil.CONVERT_MAP.put("yyyy/MM/dd HH:mm:ss".length(), "yyyy/MM/dd HH:mm:ss");
        DateUtil.CONVERT_MAP.put("yyyy/MM/dd HH:mm".length(), "yyyy/MM/dd HH:mm");
        DateUtil.CONVERT_MAP.put("yyyy/MM/dd".length(), "yyyy/MM/dd");
        DateUtil.CONVERT_MAP.put("yyyy/MM".length(), "yyyy/MM");
        DateUtil.CONVERT_MAP.put("yyyyMMddHHmmss".length(), "yyyyMMddHHmmss");
        DateUtil.CONVERT_MAP.put("yyyyMMddHHmm".length(), "yyyyMMddHHmm");
        DateUtil.CONVERT_MAP.put("yyyyMMdd".length(), "yyyyMMdd");
        DateUtil.CONVERT_MAP.put("yyyyMM".length(), "yyyyMM");
        DateUtil.VALIDATE_MAP.put("yyyy/MM/dd HH:mm:ss".length(), "[0-9]{4}\\/[0-9]{2}\\/[0-9]{2} [0-9]{2}\\:[0-9]{2}\\:[0-9]{2}");
        DateUtil.VALIDATE_MAP.put("yyyy/MM/dd HH:mm".length(), "[0-9]{4}\\/[0-9]{2}\\/[0-9]{2} [0-9]{2}\\:[0-9]{2}");
        DateUtil.VALIDATE_MAP.put("yyyy/MM/dd".length(), "[0-9]{4}\\/[0-9]{2}\\/[0-9]{2}");
        DateUtil.VALIDATE_MAP.put("yyyy/MM".length(), "[0-9]{4}\\/[0-9]{2}");
        DateUtil.VALIDATE_MAP.put("yyyyMMddHHmmss".length(), "[0-9]{14}");
        DateUtil.VALIDATE_MAP.put("yyyyMMddHHmm".length(), "[0-9]{12}");
        DateUtil.VALIDATE_MAP.put("yyyyMMdd".length(), "[0-9]{8}");
        DateUtil.VALIDATE_MAP.put("yyyyMM".length(), "[0-9]{6}");
    }
    
    public static String formatDatetime(final String strSrc) {
        if (strSrc == null) {
            return null;
        }
        if (strSrc.length() != 10) {
            return null;
        }
        String sDate = strSrc.replace(String.valueOf('-'), String.valueOf('/'));
        sDate = sDate.substring(0, 10);
        return sDate;
    }
    
    public static String[] splitDateTime(final String inDateStr) {
        String[] dateSplitArray = null;
        if (inDateStr != null) {
            if (inDateStr.indexOf(45) > 0) {
                dateSplitArray = inDateStr.split(String.valueOf('-'));
            }
            else {
                dateSplitArray = inDateStr.split(String.valueOf('/'));
            }
            if (dateSplitArray.length != 3) {
                dateSplitArray = null;
            }
        }
        return dateSplitArray;
    }
    
    public static String formatDate(final Date date, final String pattern) {
        if (date == null) {
            return null;
        }
        if (StringUtils.isBlank(pattern)) {
            return null;
        }
        final SimpleDateFormat fmt = new SimpleDateFormat(pattern);
        final String convStr = fmt.format(date);
        return convStr;
    }
    
    public static String[] split(final Date date, final String pattern) {
        if (StringUtils.isBlank(pattern)) {
            return null;
        }
        final String convertPattern = DateUtil.SEPARATE_MAP.get(pattern);
        String[] outArray = null;
        if (date == null) {
            return outArray;
        }
        if (convertPattern != null) {
            final SimpleDateFormat fmt = new SimpleDateFormat(convertPattern);
            final String outStr = fmt.format(date);
            outArray = outStr.split(String.valueOf(' '));
        }
        return outArray;
    }
    
    public static Date parseDate(final String inDate) {
        if (StringUtils.isBlank(inDate)) {
            return null;
        }
        if (!validate(inDate)) {
            return null;
        }
        final int length = inDate.length();
        final String convPattern = DateUtil.CONVERT_MAP.get(length);
        if (convPattern == null) {
            return null;
        }
        final SimpleDateFormat formatter = new SimpleDateFormat(convPattern);
        formatter.setLenient(false);
        Date date = null;
        try {
            date = formatter.parse(inDate);
        }
        catch (ParseException e) {
            DateUtil.log.warn("date parse error", e);
        }
        return date;
    }
    
    public static String convertFullYMDHMS(final String inDateString) {
        final Date inDate = parseDate(inDateString);
        return formatDate(inDate, "yyyy/MM/dd HH:mm:ss");
    }
    
    public static String convertFullYMD(final String inDateString) {
        final Date inDate = parseDate(inDateString);
        return formatDate(inDate, "yyyy/MM/dd");
    }
    
    public static String convertFullYM(final String inDateString) {
        final Date inDate = parseDate(inDateString);
        return formatDate(inDate, "yyyy/MM");
    }
    
    public static String convertCompactYMDHMS(final String inDateString) {
        final Date inDate = parseDate(inDateString);
        return formatDate(inDate, "yyyyMMddHHmmss");
    }
    
    public static String convertCompactYMD(final String inDateString) {
        final Date inDate = parseDate(inDateString);
        return formatDate(inDate, "yyyyMMdd");
    }
    
    public static String convertCompactYM(final String inDateString) {
        final Date inDate = parseDate(inDateString);
        return formatDate(inDate, "yyyyMM");
    }
    
    public static Calendar convertCalendar(final Date date) {
        Calendar calendar = null;
        if (date != null) {
            calendar = Calendar.getInstance();
            calendar.setTime(date);
        }
        return calendar;
    }
    
    public static Date convertDate(final Calendar cal) {
        Date date = null;
        if (cal != null) {
            date = cal.getTime();
        }
        return date;
    }
    
    private static boolean validate(final String inData) {
        if (StringUtils.isEmpty(inData)) {
            return false;
        }
        final String checkPattern = DateUtil.VALIDATE_MAP.get(inData.length());
        if (checkPattern == null) {
            return false;
        }
        final boolean isValidate = inData.matches(checkPattern);
        return isValidate;
    }
    
    public static boolean isSameWeek(final Date date1, final Date date2) {
        final Calendar cg1 = Calendar.getInstance();
        final Calendar cg2 = Calendar.getInstance();
        if (date1.after(date2)) {
            cg1.setTime(date2);
            cg2.setTime(date1);
        }
        else {
            cg1.setTime(date1);
            cg2.setTime(date2);
        }
        int yearWeek1 = cg1.get(3);
        int yearWeek2 = cg2.get(3);
        final int weekDay1 = cg1.get(7);
        final int weekDay2 = cg2.get(7);
        if (weekDay1 != 1) {
            ++yearWeek1;
        }
        if (weekDay2 != 1) {
            ++yearWeek2;
        }
        return cg1.get(1) == cg2.get(1) && yearWeek1 == yearWeek2;
    }
    
    public static boolean isSameDay(final Date date1, final Date date2) {
        final Calendar cg1 = Calendar.getInstance();
        final Calendar cg2 = Calendar.getInstance();
        cg1.setTime(date1);
        cg2.setTime(date2);
        return cg1.get(1) == cg2.get(1) && cg1.get(6) == cg2.get(6);
    }
    
    public static boolean isSameMonth(final Date date1, final Date date2) {
        final Calendar cg1 = Calendar.getInstance();
        final Calendar cg2 = Calendar.getInstance();
        cg1.setTime(date1);
        cg2.setTime(date2);
        return cg1.get(1) == cg2.get(1) && cg1.get(2) == cg2.get(2);
    }
    
    public static boolean isSameDay(final long timestamp1, final long timestamp2) {
        final Calendar cg1 = Calendar.getInstance();
        final Calendar cg2 = Calendar.getInstance();
        cg1.setTimeInMillis(timestamp1);
        cg2.setTimeInMillis(timestamp2);
        return cg1.get(1) == cg2.get(1) && cg1.get(6) == cg2.get(6);
    }
    
    public static String formatTime(long time, final String pattern) {
        if ("mm:SS".equals(pattern)) {
            final long minute = time / 60000L;
            time %= 60000L;
            final long sec = time / 1000L;
            return minute + ":" + sec;
        }
        return "";
    }
    
    public static String getMonthStart(final Date date) {
        final Calendar cg = Calendar.getInstance();
        cg.setTime(date);
        cg.set(5, 1);
        return formatDate(cg.getTime(), "yyyy-MM-dd");
    }
    
    public static String getMonthEnd(final Date date) {
        final Calendar cg = Calendar.getInstance();
        cg.setTime(date);
        cg.set(5, 1);
        cg.set(2, cg.get(2) + 1);
        return formatDate(cg.getTime(), "yyyy-MM-dd");
    }
    
    public static String getDayStart(final Date date) {
        return formatDate(date, "yyyy-MM-dd");
    }
    
    public static String getDayEnd(final Date date) {
        final Calendar cg = Calendar.getInstance();
        cg.setTime(date);
        cg.add(5, 1);
        return formatDate(cg.getTime(), "yyyy-MM-dd");
    }
    
    public static String getWeekStart(final Date date) {
        final Calendar cg = Calendar.getInstance();
        cg.setTime(date);
        cg.add(5, -1);
        cg.set(7, 2);
        return formatDate(cg.getTime(), "yyyy-MM-dd");
    }
    
    public static String getWeekEnd(final Date date) {
        final Calendar cg = Calendar.getInstance();
        cg.setTime(date);
        cg.add(5, 6);
        cg.set(7, 2);
        return formatDate(cg.getTime(), "yyyy-MM-dd");
    }
    
    public static boolean isInDate(final Date now, final Date startDate, final Date endDate) {
        return !now.before(startDate) && !now.after(endDate);
    }
    
    public static void main(final String[] args) {
        final Calendar cg = Calendar.getInstance();
        cg.set(5, 14);
        System.out.println(getWeekStart(cg.getTime()));
        System.out.println(getWeekEnd(cg.getTime()));
    }
}
