package com.reign.util.characterFilter.PatternWM;

import java.util.concurrent.locks.*;
import org.apache.commons.logging.*;
import com.reign.util.characterFilter.*;
import java.io.*;
import java.util.*;

public class CharacterFilterSimplePatternWMUnion extends CharacterFilterBase
{
    private static final Log logger;
    private static final int MAX_INDEX;
    private int[] shiftTable;
    private Vector<Pattern>[] hashTable;
    private final ReentrantReadWriteLock lock;
    private final Lock readLock;
    private final Lock writeLock;
    
    static {
        logger = LogFactory.getLog(CharacterFilterSimplePatternWMUnion.class);
        MAX_INDEX = (int)Math.pow(2.0, 16.0);
    }
    
    public CharacterFilterSimplePatternWMUnion() {
        this.lock = new ReentrantReadWriteLock();
        this.readLock = this.lock.readLock();
        this.writeLock = this.lock.writeLock();
        this.shiftTable = new int[CharacterFilterSimplePatternWMUnion.MAX_INDEX];
        for (int i = 0; i < CharacterFilterSimplePatternWMUnion.MAX_INDEX; ++i) {
            this.shiftTable[i] = 2;
        }
        this.hashTable = new Vector[CharacterFilterSimplePatternWMUnion.MAX_INDEX];
        for (int i = 0; i < CharacterFilterSimplePatternWMUnion.MAX_INDEX; ++i) {
            this.hashTable[i] = new Vector<Pattern>();
        }
    }
    
    public CharacterFilterSimplePatternWMUnion(final IReplaceCharacterGetter replaceCharacterGetter) {
        super(replaceCharacterGetter);
        this.lock = new ReentrantReadWriteLock();
        this.readLock = this.lock.readLock();
        this.writeLock = this.lock.writeLock();
        this.shiftTable = new int[CharacterFilterSimplePatternWMUnion.MAX_INDEX];
        for (int i = 0; i < CharacterFilterSimplePatternWMUnion.MAX_INDEX; ++i) {
            this.shiftTable[i] = 2;
        }
        this.hashTable = new Vector[CharacterFilterSimplePatternWMUnion.MAX_INDEX];
        for (int i = 0; i < CharacterFilterSimplePatternWMUnion.MAX_INDEX; ++i) {
            this.hashTable[i] = new Vector<Pattern>();
        }
    }
    
    public CharacterFilterSimplePatternWMUnion(final BufferedReader buff) {
        this.lock = new ReentrantReadWriteLock();
        this.readLock = this.lock.readLock();
        this.writeLock = this.lock.writeLock();
        this.buildFilterKeyWord(buff);
    }
    
    public CharacterFilterSimplePatternWMUnion(final IReplaceCharacterGetter replaceCharacterGetter, final BufferedReader buff) {
        super(replaceCharacterGetter);
        this.lock = new ReentrantReadWriteLock();
        this.readLock = this.lock.readLock();
        this.writeLock = this.lock.writeLock();
        this.buildFilterKeyWord(buff);
    }
    
    @Override
    public String filter(final String str) {
        Vector<PatternResult> matchPattern = new Vector<PatternResult>();
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
            matchPattern = this.parseAtomicPatternSet(matchPattern);
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
        Vector<PatternResult> matchPattern = new Vector<PatternResult>();
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
            matchPattern = this.parseAtomicPatternSet(matchPattern);
        }
        finally {
            this.readLock.unlock();
        }
        this.readLock.unlock();
        return matchPattern.size() > 0;
    }
    
    @Override
    public void buildFilterKeyWord(final BufferedReader buff) {
        final List<Pattern> list = new ArrayList<Pattern>();
        int interval = Integer.MAX_VALUE;
        String keyword = null;
        try {
            buff.read();
            while ((keyword = buff.readLine()) != null) {
                final String[] s = keyword.split("\\s");
                if (s.length == 1) {
                    final Pattern pattern = new Pattern(keyword.trim());
                    list.add(pattern);
                    if (pattern.getLength() >= interval) {
                        continue;
                    }
                    interval = pattern.getLength();
                }
                else {
                    final UnionPattern unionPattern = new UnionPattern();
                    String[] array;
                    for (int length = (array = s).length, i = 0; i < length; ++i) {
                        final String word = array[i];
                        if (word != null && !word.trim().isEmpty()) {
                            final AtomicPattern atomicPattern = new AtomicPattern(word);
                            unionPattern.addNewAtomicPattrn(atomicPattern);
                            list.add(atomicPattern);
                            if (atomicPattern.getLength() < interval) {
                                interval = atomicPattern.getLength();
                            }
                        }
                    }
                }
            }
        }
        catch (IOException e) {
            CharacterFilterSimplePatternWMUnion.logger.error("IO\u5f02\u5e38!", e);
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
        final int[] rtn = new int[CharacterFilterSimplePatternWMUnion.MAX_INDEX];
        for (int i = 0; i < CharacterFilterSimplePatternWMUnion.MAX_INDEX; ++i) {
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
        final Vector[] rtn = new Vector[CharacterFilterSimplePatternWMUnion.MAX_INDEX];
        for (int i = 0; i < CharacterFilterSimplePatternWMUnion.MAX_INDEX; ++i) {
            rtn[i] = new Vector();
        }
        for (final Pattern pattern : patternList) {
            if (pattern.charAtEnd(0) != '\0') {
                rtn[pattern.charAtEnd(0)].add(pattern);
            }
        }
        return rtn;
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
    
    private Vector<PatternResult> parseAtomicPatternSet(final Vector<PatternResult> patterns) {
        final Vector<PatternResult> list = new Vector<PatternResult>();
        for (final PatternResult patternResult : patterns) {
            final Pattern pattern = patternResult.getPattern();
            if (pattern instanceof AtomicPattern) {
                final AtomicPattern atomicPattern = (AtomicPattern)pattern;
                final UnionPattern unionPattern = atomicPattern.getBelongUnionPattern();
                if (!unionPattern.isIncludeAllAp(patterns)) {
                    continue;
                }
                list.add(patternResult);
            }
            else {
                list.add(patternResult);
            }
        }
        return list;
    }
}
