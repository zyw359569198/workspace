package com.reign.util;

import org.apache.commons.lang.*;
import java.util.*;
import java.io.*;

public class KeyGenerator
{
    private static char[] ITEMS;
    private static char[] ID_ITEMS;
    private static char[] ITEM_ITEMS;
    
    static {
        KeyGenerator.ITEMS = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };
        KeyGenerator.ID_ITEMS = new char[] { 'M', 'C', 'A', '5', '4', 'R', 'G', 'X', 'Q', 'H', 'D', 'J', '0', 'E', 'O', 'F', '8', '2', 'K', 'P', 'Z', 'I', '9', '6', 'T', 'V', 'B', 'Y', '7', 'W', 'L', 'S', '1', '3', 'U', 'N' };
        KeyGenerator.ITEM_ITEMS = new char[] { 'Q', 'Y', 'C', 'T', 'P', '7', 'V', 'A', 'U', '2', '8', 'S', 'N', 'W', 'H', 'K', 'G', '3', 'B', 'D', 'I', 'E', 'J', '4', 'R', 'O', '5', 'X', '9', 'Z', 'L', '6', '0', 'M', '1', 'F' };
    }
    
    public static String[] getKey(final int num, final int length) {
        final String[] result = new String[num];
        for (int i = 0; i < num; ++i) {
            final StringBuilder buffer = new StringBuilder();
            for (int j = 0; j < length; ++j) {
                final double index = Math.random() * 5.340998470170229E19;
                buffer.append(KeyGenerator.ITEMS[(int)(index % KeyGenerator.ITEMS.length)]);
            }
            result[i] = buffer.toString();
        }
        return result;
    }
    
    public static String[] getKey(final int num, final int group, final int length) {
        final String[] result = new String[num];
        for (int i = 0; i < num; ++i) {
            final StringBuilder buffer = new StringBuilder();
            for (int j = 0; j < group; ++j) {
                if (j != 0) {
                    buffer.append("-");
                }
                for (int k = 0; k < length; ++k) {
                    final double index = Math.random() * 5.340998470170229E19;
                    buffer.append(KeyGenerator.ITEMS[(int)(index % KeyGenerator.ITEMS.length)]);
                }
            }
            result[i] = buffer.toString().toUpperCase();
        }
        return result;
    }
    
    public static String getIdKey(final int baseNum, final int bit, final int groupLength) {
        String str = String.valueOf(baseNum);
        str = StringUtils.leftPad(str, bit, '0');
        final StringBuilder buffer = new StringBuilder();
        final int group = KeyGenerator.ID_ITEMS.length / 10;
        for (int i = 0; i < str.length(); ++i) {
            if (i % groupLength == 0 && i != 0) {
                buffer.append("-");
            }
            final int index = Integer.parseInt(str.substring(i, i + 1)) + i % group * 10;
            buffer.append(KeyGenerator.ID_ITEMS[index]);
        }
        return buffer.toString();
    }
    
    public static int getId(final String key, final int bit, final int groupLength) {
        final String str = key.substring(0, (bit > groupLength) ? (bit + (bit - 1) / groupLength) : bit);
        final StringBuilder buffer = new StringBuilder();
        final List<String> idList = getList(KeyGenerator.ID_ITEMS);
        final int group = KeyGenerator.ID_ITEMS.length / 10;
        int offset = 0;
        final int groups = groupLength + 1;
        for (int i = 0; i < str.length(); ++i) {
            if ((i + 1) % groups == 0 && i != 0) {
                ++offset;
            }
            else {
                final List<String> subList = idList.subList((i - offset) % group * 10, (i - offset) % group * 10 + 10);
                final int index = subList.indexOf(String.valueOf(str.charAt(i)));
                buffer.append(index);
            }
        }
        try {
            final int id = Integer.parseInt(buffer.toString());
            return id;
        }
        catch (NumberFormatException e) {
            return -1;
        }
    }
    
    public static String getItemKey(final int baseNum, final int bit, final int groupLength) {
        String str = String.valueOf(baseNum);
        str = StringUtils.leftPad(str, bit, '0');
        final StringBuilder buffer = new StringBuilder();
        final int group = KeyGenerator.ITEM_ITEMS.length / 10;
        for (int i = 0; i < str.length(); ++i) {
            if (i % groupLength == 0 && i != 0) {
                buffer.append("-");
            }
            final int index = Integer.parseInt(str.substring(i, i + 1)) + i % group * 10;
            buffer.append(KeyGenerator.ITEM_ITEMS[index]);
        }
        return buffer.toString();
    }
    
    public static int getItemId(final String key, final int bit, final int groupLength) {
        final String str = key.substring(0, (bit > groupLength) ? (bit + (bit - 1) / groupLength) : bit);
        final StringBuilder buffer = new StringBuilder();
        final List<String> idList = getList(KeyGenerator.ITEM_ITEMS);
        final int group = KeyGenerator.ITEM_ITEMS.length / 10;
        int offset = 0;
        final int groups = groupLength + 1;
        for (int i = 0; i < str.length(); ++i) {
            if ((i + 1) % groups == 0 && i != 0) {
                ++offset;
            }
            else {
                final List<String> subList = idList.subList((i - offset) % group * 10, (i - offset) % group * 10 + 10);
                final int index = subList.indexOf(String.valueOf(str.charAt(i)));
                buffer.append(index);
            }
        }
        try {
            final int id = Integer.parseInt(buffer.toString());
            return id;
        }
        catch (NumberFormatException e) {
            return -1;
        }
    }
    
    private static List<String> getList(final char[] chars) {
        final List<String> resultList = new ArrayList<String>();
        for (int i = 0; i < chars.length; ++i) {
            resultList.add(String.valueOf(chars[i]));
        }
        return resultList;
    }
    
    public static void main(final String[] args) throws IOException {
        final String[] strs = getKey(1, 12);
        String[] array;
        for (int length = (array = strs).length, i = 0; i < length; ++i) {
            final String str = array[i];
            System.out.print(str);
        }
    }
}
