package com.reign.gcld.common.util;

import org.apache.commons.lang.*;
import java.util.*;

public class SymbolUtil
{
    public static String toString(final int[] wei, final String string) {
        if (StringUtils.isBlank(string) || wei.length == 0) {
            return "";
        }
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < wei.length; ++i) {
            sb.append(wei[i]).append(string);
        }
        removeTheLast(sb);
        return sb.toString();
    }
    
    public static void removeTheLast(final StringBuffer sb) {
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
    }
    
    public static String[] StringtoArray(final String s, final String split) {
        if (StringUtils.isBlank(split) || StringUtils.isBlank(s)) {
            return null;
        }
        return s.split(split);
    }
    
    public static int[] StringtoIntArray(final String s, final String split) {
        if (StringUtils.isBlank(split) || StringUtils.isBlank(s)) {
            return null;
        }
        final String[] arrays = s.split(split);
        final int[] arrayInt = new int[arrays.length];
        for (int i = 0; i < arrayInt.length; ++i) {
            arrayInt[i] = Integer.parseInt(arrays[i]);
        }
        return arrayInt;
    }
    
    public static String toString(final String[] box, final String string) {
        if (StringUtils.isBlank(string) || box.length == 0) {
            return "";
        }
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < box.length; ++i) {
            sb.append(box[i]).append(string);
        }
        removeTheLast(sb);
        return sb.toString();
    }
    
    public static List<Integer> stringToList(final String rewardStr, final String reg) {
        if (StringUtils.isBlank(rewardStr)) {
            return null;
        }
        final String[] strings = rewardStr.split(reg);
        final List<Integer> list = new ArrayList<Integer>();
        for (int i = 0; i < strings.length; ++i) {
            final String string = strings[i];
            if (!StringUtils.isBlank(string)) {
                list.add(Integer.parseInt(string));
            }
        }
        return list;
    }
    
    public static Set<Integer> stringToSet(final String rewardStr, final String reg) {
        if (StringUtils.isBlank(rewardStr)) {
            return null;
        }
        final String[] strings = rewardStr.split(reg);
        final Set<Integer> list = new HashSet<Integer>();
        for (int i = 0; i < strings.length; ++i) {
            final String string = strings[i];
            if (!StringUtils.isBlank(string)) {
                list.add(Integer.parseInt(string));
            }
        }
        return list;
    }
    
    public static String listToString(final List<Integer> list, final String reg) {
        if (list == null || list.isEmpty()) {
            return "";
        }
        final StringBuffer sb = new StringBuffer();
        for (final Integer integer : list) {
            sb.append(integer).append(reg);
        }
        removeTheLast(sb);
        return sb.toString();
    }
    
    public static int getSplitNum(final String rewardStr, final String comma) {
        if (StringUtils.isBlank(rewardStr)) {
            return 0;
        }
        return rewardStr.split(comma).length;
    }
    
    public static String CollectionToString(final Collection<Integer> stateCities, final String string) {
        if (stateCities == null || stateCities.isEmpty()) {
            return "";
        }
        final StringBuffer sb = new StringBuffer();
        for (final Integer integer : stateCities) {
            sb.append(integer).append(string);
        }
        removeTheLast(sb);
        return sb.toString();
    }
}
