package com.reign.util.characterFilter.PatternWM;

public class AtomicPattern extends Pattern
{
    private UnionPattern belongUnionPattern;
    
    public AtomicPattern(final String str) {
        super(str);
    }
    
    public UnionPattern getBelongUnionPattern() {
        return this.belongUnionPattern;
    }
    
    public void setBelongUnionPattern(final UnionPattern belongUnionPattern) {
        this.belongUnionPattern = belongUnionPattern;
    }
}
