package com.reign.util.characterFilter.PatternWM;

import java.util.*;

public class PatternResult
{
    private Pattern pattern;
    private int to;
    private int from;
    
    public PatternResult(final Pattern pattern, final int to) {
        this.pattern = pattern;
        this.to = to;
        this.from = to - pattern.getLength() + 1;
    }
    
    public Pattern getPattern() {
        return this.pattern;
    }
    
    public int getTo() {
        return this.to;
    }
    
    public int getFrom() {
        return this.from;
    }
    
    public boolean isIn(final int index) {
        return index >= this.from && index <= this.to;
    }
    
    public static boolean isIn(final Vector<PatternResult> list, final int index) {
        for (final PatternResult r : list) {
            if (r.isIn(index)) {
                return true;
            }
        }
        return false;
    }
}
