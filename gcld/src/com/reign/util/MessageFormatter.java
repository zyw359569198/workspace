package com.reign.util;

import java.util.concurrent.*;
import java.util.*;
import java.text.*;
import java.io.*;

public class MessageFormatter
{
    private List<TextPattern> patternList;
    private String pattern;
    private List<Integer> offSetList;
    private int maxOffSet;
    private static Map<String, MessageFormatter> cacheMap;
    private static Object lock;
    
    static {
        MessageFormatter.cacheMap = new ConcurrentHashMap<String, MessageFormatter>();
        MessageFormatter.lock = new Object();
    }
    
    public static String format(final String pattern, final Object... params) {
        MessageFormatter formatter = MessageFormatter.cacheMap.get(pattern);
        if (formatter == null) {
            synchronized (MessageFormatter.lock) {
                formatter = MessageFormatter.cacheMap.get(pattern);
                if (formatter == null) {
                    formatter = new MessageFormatter(pattern);
                    MessageFormatter.cacheMap.put(pattern, formatter);
                }
            }
            // monitorexit(MessageFormatter.lock)
        }
        return formatter.format(params);
    }
    
    private String format(final Object... params) {
        final StringBuilder builder = new StringBuilder(this.pattern.length());
        int lastOffset = 0;
        int i = 0;
        for (final Integer offset : this.offSetList) {
            builder.append(this.pattern.substring(lastOffset, offset));
            final int index = this.patternList.get(i).index;
            if (index < params.length) {
                builder.append(params[index]);
            }
            else {
                builder.append("{").append(index).append("}");
            }
            lastOffset = offset;
            ++i;
        }
        builder.append(this.pattern.substring(lastOffset, this.pattern.length()));
        return builder.toString();
    }
    
    public MessageFormatter(final String pattern) {
        this.patternList = new ArrayList<TextPattern>(10);
        this.offSetList = new ArrayList<Integer>(10);
        this.applyPattern(pattern);
    }
    
    private void applyPattern(final String pattern) {
        int braceStack = 0;
        final StringBuilder builder = new StringBuilder();
        final StringBuilder builder2 = new StringBuilder();
        for (int i = 0; i < pattern.length(); ++i) {
            final char ch = pattern.charAt(i);
            if (ch == '{' && braceStack == 0) {
                ++braceStack;
            }
            else if (braceStack != 0) {
                switch (ch) {
                    case '{': {
                        ++braceStack;
                        builder.append(ch);
                        continue;
                    }
                    case '}': {
                        if (braceStack == 1) {
                            ++this.maxOffSet;
                            this.offSetList.add(builder.length());
                            this.patternList.add(new TextPattern(Integer.parseInt(builder2.toString()), null));
                            builder2.setLength(0);
                            --braceStack;
                            continue;
                        }
                        --braceStack;
                        builder.append(ch);
                        break;
                    }
                }
                builder2.append(ch);
            }
            else {
                builder.append(ch);
            }
        }
        this.pattern = builder.toString();
    }
    
    public static void main(final String[] args) throws UnsupportedEncodingException {
        System.out.println(format("\u83b7\u5f97{0}", "123456"));
    }
    
    private class TextPattern
    {
        public int index;
        public Format format;
        
        public TextPattern(final int index, final Format format) {
            this.index = index;
            this.format = format;
        }
    }
}
