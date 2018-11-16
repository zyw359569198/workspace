package com.reign.util.characterFilter.PatternWM;

import java.util.concurrent.locks.*;
import org.apache.commons.logging.*;
import com.reign.util.characterFilter.*;
import java.io.*;
import java.util.*;

public class CharacterFilterSimplePatternWM extends CharacterFilterBase
{
    private static final Log logger;
    private static final int MAX_INDEX;
    private int[] shiftTable;
    private Vector<Pattern>[] hashTable;
    private final ReentrantReadWriteLock lock;
    private final Lock readLock;
    private final Lock writeLock;
    
    static {
        logger = LogFactory.getLog(CharacterFilterSimplePatternWM.class);
        MAX_INDEX = (int)Math.pow(2.0, 16.0);
    }
    
    public CharacterFilterSimplePatternWM() {
        this.lock = new ReentrantReadWriteLock();
        this.readLock = this.lock.readLock();
        this.writeLock = this.lock.writeLock();
        this.shiftTable = new int[CharacterFilterSimplePatternWM.MAX_INDEX];
        for (int i = 0; i < CharacterFilterSimplePatternWM.MAX_INDEX; ++i) {
            this.shiftTable[i] = 2;
        }
        this.hashTable = new Vector[CharacterFilterSimplePatternWM.MAX_INDEX];
        for (int i = 0; i < CharacterFilterSimplePatternWM.MAX_INDEX; ++i) {
            this.hashTable[i] = new Vector<Pattern>();
        }
    }
    
    public CharacterFilterSimplePatternWM(final IReplaceCharacterGetter replaceCharacterGetter) {
        super(replaceCharacterGetter);
        this.lock = new ReentrantReadWriteLock();
        this.readLock = this.lock.readLock();
        this.writeLock = this.lock.writeLock();
        this.shiftTable = new int[CharacterFilterSimplePatternWM.MAX_INDEX];
        for (int i = 0; i < CharacterFilterSimplePatternWM.MAX_INDEX; ++i) {
            this.shiftTable[i] = 2;
        }
        this.hashTable = new Vector[CharacterFilterSimplePatternWM.MAX_INDEX];
        for (int i = 0; i < CharacterFilterSimplePatternWM.MAX_INDEX; ++i) {
            this.hashTable[i] = new Vector<Pattern>();
        }
    }
    
    public CharacterFilterSimplePatternWM(final BufferedReader buff) {
        this.lock = new ReentrantReadWriteLock();
        this.readLock = this.lock.readLock();
        this.writeLock = this.lock.writeLock();
        this.buildFilterKeyWord(buff);
    }
    
    public CharacterFilterSimplePatternWM(final IReplaceCharacterGetter replaceCharacterGetter, final BufferedReader buff) {
        super(replaceCharacterGetter);
        this.lock = new ReentrantReadWriteLock();
        this.readLock = this.lock.readLock();
        this.writeLock = this.lock.writeLock();
        this.buildFilterKeyWord(buff);
    }
    
    @Override
    public String filter(final String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        final Vector<PatternResult> matchPattern = new Vector<PatternResult>();
        final int length = str.length();
        this.readLock.lock();
        try {
            int i = 0;
            while (i < length) {
                final char checkChar = str.charAt(i);
                if (this.shiftTable[checkChar] == 0) {
                    matchPattern.addAll(this.findMathAps(str.substring(0, i + 1), i, this.hashTable[checkChar]));
                    ++i;
                }
                else {
                    i += this.shiftTable[checkChar];
                }
            }
        }
        finally {
            this.readLock.unlock();
        }
        this.readLock.unlock();
        if (matchPattern.size() == 0) {
            return str;
        }
        final char[] rtn = str.toCharArray();
        for (final PatternResult result : matchPattern) {
            for (int j = result.getFrom(); j <= result.getTo(); ++j) {
                rtn[j] = this.replaceCharacterGetter.getChar();
            }
        }
        return new String(rtn);
    }
    
    @Override
    public boolean isValid(final String str) {
        final int length = str.length();
        this.readLock.lock();
        try {
            int i = 0;
            while (i < length) {
                final char checkChar = str.charAt(i);
                if (this.shiftTable[checkChar] == 0) {
                    if (this.isFindMatchPattern(str.substring(0, i + 1), this.hashTable[checkChar])) {
                        return false;
                    }
                    ++i;
                }
                else {
                    i += this.shiftTable[checkChar];
                }
            }
        }
        finally {
            this.readLock.unlock();
        }
        this.readLock.unlock();
        return true;
    }
    
    @Override
    public void buildFilterKeyWord(final BufferedReader buff) {
        final List<Pattern> list = new ArrayList<Pattern>();
        int interval = Integer.MAX_VALUE;
        String keyword = null;
        try {
            buff.read();
            while ((keyword = buff.readLine()) != null) {
                final Pattern pattern = new Pattern(keyword.trim());
                list.add(pattern);
                if (pattern.getLength() < interval) {
                    interval = pattern.getLength();
                }
            }
        }
        catch (IOException e) {
            CharacterFilterSimplePatternWM.logger.error("IO\u5f02\u5e38!", e);
        }
        final int[] currShiftTable = this.initShiftTable(interval, list);
        final Vector[] currHashTable = this.initHashTable(list);
        this.writeLock.lock();
        try {
            this.shiftTable = currShiftTable;
            this.hashTable = currHashTable;
        }
        finally {
            this.writeLock.unlock();
        }
        this.writeLock.unlock();
    }
    
    private int[] initShiftTable(int interval, final List<Pattern> patternList) {
        if (interval > 2) {
            interval = 2;
        }
        final int[] rtn = new int[CharacterFilterSimplePatternWM.MAX_INDEX];
        for (int i = 0; i < CharacterFilterSimplePatternWM.MAX_INDEX; ++i) {
            rtn[i] = interval;
        }
        for (final Pattern pattern : patternList) {
            if (rtn[pattern.charAtEnd(1)] != 0) {
                rtn[pattern.charAtEnd(1)] = 1;
            }
            if (rtn[pattern.charAtEnd(0)] != 0) {
                rtn[pattern.charAtEnd(0)] = 0;
            }
        }
        return rtn;
    }
    
    private Vector<Pattern>[] initHashTable(final List<Pattern> patternList) {
        final Vector[] rtn = new Vector[CharacterFilterSimplePatternWM.MAX_INDEX];
        for (int i = 0; i < CharacterFilterSimplePatternWM.MAX_INDEX; ++i) {
            rtn[i] = new Vector();
        }
        for (final Pattern pattern : patternList) {
            if (pattern.charAtEnd(0) != '\0') {
                rtn[pattern.charAtEnd(0)].add(pattern);
            }
        }
        return rtn;
    }
    
    private boolean isFindMatchPattern(final String str, final Vector<Pattern> dest) {
        for (final Pattern pattern : dest) {
            if (pattern.findMatchInString(str)) {
                return true;
            }
        }
        return false;
    }
    
    private Vector<PatternResult> findMathAps(final String str, final int index, final Vector<Pattern> dest) {
        final Vector<PatternResult> trn = new Vector<PatternResult>();
        for (final Pattern pattern : dest) {
            if (pattern.findMatchInString(str)) {
                trn.add(new PatternResult(pattern, index));
            }
        }
        return trn;
    }
}
