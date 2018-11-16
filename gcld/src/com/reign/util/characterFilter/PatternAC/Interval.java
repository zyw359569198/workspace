package com.reign.util.characterFilter.PatternAC;

public class Interval
{
    private int from;
    private int to;
    
    public Interval(final int from, final int to) {
        this.from = from;
        this.to = to;
    }
    
    public int getFrom() {
        return this.from;
    }
    
    public int getTo() {
        return this.to;
    }
}
