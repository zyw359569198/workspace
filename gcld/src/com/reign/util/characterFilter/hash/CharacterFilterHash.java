package com.reign.util.characterFilter.hash;

import java.util.concurrent.locks.*;
import org.apache.commons.logging.*;
import com.reign.util.characterFilter.*;
import com.reign.util.characterFilter.PatternAC.*;
import java.util.*;
import java.io.*;

public class CharacterFilterHash extends CharacterFilterBase
{
    private static final Log logger;
    private HashSet<String> keywordSet;
    private byte[] fastCheck;
    private byte[] fastLength;
    private BitSet charCheck;
    private BitSet endCheck;
    private int maxWordLength;
    private int minWordLength;
    private final ReentrantReadWriteLock lock;
    private final Lock readLock;
    private final Lock writeLock;
    
    static {
        logger = LogFactory.getLog(CharacterFilterHash.class);
    }
    
    public CharacterFilterHash() {
        this.keywordSet = new HashSet<String>();
        this.fastCheck = new byte[65535];
        this.fastLength = new byte[65535];
        this.charCheck = new BitSet(65535);
        this.endCheck = new BitSet(65535);
        this.maxWordLength = 0;
        this.minWordLength = Integer.MAX_VALUE;
        this.lock = new ReentrantReadWriteLock();
        this.readLock = this.lock.readLock();
        this.writeLock = this.lock.writeLock();
    }
    
    public CharacterFilterHash(final IReplaceCharacterGetter replaceCharacterGetter) {
        super(replaceCharacterGetter);
        this.keywordSet = new HashSet<String>();
        this.fastCheck = new byte[65535];
        this.fastLength = new byte[65535];
        this.charCheck = new BitSet(65535);
        this.endCheck = new BitSet(65535);
        this.maxWordLength = 0;
        this.minWordLength = Integer.MAX_VALUE;
        this.lock = new ReentrantReadWriteLock();
        this.readLock = this.lock.readLock();
        this.writeLock = this.lock.writeLock();
    }
    
    public CharacterFilterHash(final BufferedReader buff) {
        this.keywordSet = new HashSet<String>();
        this.fastCheck = new byte[65535];
        this.fastLength = new byte[65535];
        this.charCheck = new BitSet(65535);
        this.endCheck = new BitSet(65535);
        this.maxWordLength = 0;
        this.minWordLength = Integer.MAX_VALUE;
        this.lock = new ReentrantReadWriteLock();
        this.readLock = this.lock.readLock();
        this.writeLock = this.lock.writeLock();
        this.buildFilterKeyWord(buff);
    }
    
    public CharacterFilterHash(final IReplaceCharacterGetter replaceCharacterGetter, final BufferedReader buff) {
        super(replaceCharacterGetter);
        this.keywordSet = new HashSet<String>();
        this.fastCheck = new byte[65535];
        this.fastLength = new byte[65535];
        this.charCheck = new BitSet(65535);
        this.endCheck = new BitSet(65535);
        this.maxWordLength = 0;
        this.minWordLength = Integer.MAX_VALUE;
        this.lock = new ReentrantReadWriteLock();
        this.readLock = this.lock.readLock();
        this.writeLock = this.lock.writeLock();
        this.buildFilterKeyWord(buff);
    }
    
    private void initKeyword(final String keyword) {
        this.maxWordLength = Math.max(this.maxWordLength, keyword.length());
        this.minWordLength = Math.min(this.minWordLength, keyword.length());
        for (int i = 0; i < 7 && i < keyword.length(); ++i) {
            final byte[] fastCheck = this.fastCheck;
            final char char1 = keyword.charAt(i);
            fastCheck[char1] |= (byte)(1 << i);
        }
        for (int i = 7; i < keyword.length(); ++i) {
            final byte[] fastCheck2 = this.fastCheck;
            final char char2 = keyword.charAt(i);
            fastCheck2[char2] |= (byte)128;
        }
        if (keyword.length() == 1) {
            this.charCheck.set(keyword.charAt(0), true);
        }
        else {
            final byte[] fastLength = this.fastLength;
            final char char3 = keyword.charAt(0);
            fastLength[char3] |= (byte)(1 << Math.min(7, keyword.length() - 2));
            this.endCheck.set(keyword.charAt(keyword.length() - 1), true);
            this.keywordSet.add(keyword);
        }
    }
    
    private void clearFilterKeyWord() {
        this.keywordSet.clear();
        this.fastCheck = new byte[65535];
        this.fastLength = new byte[65535];
        this.charCheck.clear();
        this.endCheck.clear();
        this.maxWordLength = 0;
        this.minWordLength = Integer.MAX_VALUE;
    }
    
    private String replaceChar(final String str, final List<Interval> intervalList) {
        if (intervalList.size() == 0) {
            return str;
        }
        final char[] rtn = str.toCharArray();
        for (final Interval interval : intervalList) {
            for (int i = interval.getFrom(); i <= interval.getTo(); ++i) {
                rtn[i] = this.replaceCharacterGetter.getChar();
            }
        }
        return new String(rtn);
    }
    
    @Override
    public String filter(final String str) {
        int index = 0;
        final List<Interval> intervalList = new ArrayList<Interval>();
        this.readLock.lock();
        try {
            while (index < str.length()) {
                int count = 1;
                if (index > 0 || (this.fastCheck[str.charAt(index)] & 0x1) == 0x0) {
                    while (index < str.length() - 1 && (this.fastCheck[str.charAt(++index)] & 0x1) == 0x0) {}
                }
                final char begin = str.charAt(index);
                if (this.minWordLength == 1 && this.charCheck.get(begin)) {
                    intervalList.add(new Interval(index, index));
                }
                for (int j = 1; j < Math.min(this.maxWordLength, str.length() - index - 1); ++j) {
                    final char current = str.charAt(index + j);
                    if ((this.fastCheck[current] & 0x1) == 0x0 && count == j) {
                        ++count;
                    }
                    if ((this.fastCheck[current] & 1 << Math.min(j, 7)) == 0x0) {
                        break;
                    }
                    if (j + 1 >= this.minWordLength && (this.fastLength[begin] & 1 << Math.min(j - 1, 7)) > 0 && this.endCheck.get(current)) {
                        final String sub = str.substring(index, index + j + 1);
                        if (this.keywordSet.contains(sub)) {
                            intervalList.add(new Interval(index, index + j));
                        }
                    }
                }
                index += count;
            }
            return this.replaceChar(str, intervalList);
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    @Override
    public boolean isValid(final String str) {
        int index = 0;
        this.readLock.lock();
        try {
            while (index < str.length()) {
                int count = 1;
                if (index > 0 || (this.fastCheck[str.charAt(index)] & 0x1) == 0x0) {
                    while (index < str.length() - 1 && (this.fastCheck[str.charAt(++index)] & 0x1) == 0x0) {}
                }
                final char begin = str.charAt(index);
                if (this.minWordLength == 1 && this.charCheck.get(begin)) {
                    return true;
                }
                for (int j = 1; j < Math.min(this.maxWordLength, str.length() - index - 1); ++j) {
                    final char current = str.charAt(index + j);
                    if ((this.fastCheck[current] & 0x1) == 0x0 && count == j) {
                        ++count;
                    }
                    if ((this.fastCheck[current] & 1 << Math.min(j, 7)) == 0x0) {
                        break;
                    }
                    if (j + 1 >= this.minWordLength && (this.fastLength[begin] & 1 << Math.min(j - 1, 7)) > 0 && this.endCheck.get(current)) {
                        final String sub = str.substring(index, index + j + 1);
                        if (this.keywordSet.contains(sub)) {
                            return true;
                        }
                    }
                }
                index += count;
            }
            return false;
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    @Override
    public void buildFilterKeyWord(final BufferedReader buff) {
        final List<String> keywordList = new ArrayList<String>();
        String keyword = null;
        try {
            buff.read();
            while ((keyword = buff.readLine()) != null) {
                keywordList.add(keyword);
            }
        }
        catch (IOException e) {
            CharacterFilterHash.logger.error("IO\u5f02\u5e38!", e);
        }
        this.writeLock.lock();
        try {
            this.clearFilterKeyWord();
            for (final String str : keywordList) {
                this.initKeyword(str);
            }
        }
        finally {
            this.writeLock.unlock();
        }
        this.writeLock.unlock();
    }
}
