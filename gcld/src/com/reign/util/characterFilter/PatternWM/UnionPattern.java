package com.reign.util.characterFilter.PatternWM;

import java.util.*;

public class UnionPattern
{
    private List<AtomicPattern> patterns;
    
    public UnionPattern() {
        this.patterns = new ArrayList<AtomicPattern>();
    }
    
    public List<AtomicPattern> getSet() {
        return this.patterns;
    }
    
    public void addNewAtomicPattrn(final AtomicPattern ap) {
        this.patterns.add(ap);
        ap.setBelongUnionPattern(this);
    }
    
    public boolean isIncludeAllAp(final Vector<PatternResult> list) {
        if (this.patterns.size() > list.size()) {
            return false;
        }
        for (int i = 0; i < this.patterns.size(); ++i) {
            final AtomicPattern ap = this.patterns.get(i);
            if (!this.isInAps(ap, list)) {
                return false;
            }
        }
        return true;
    }
    
    private boolean isInAps(final AtomicPattern ap, final Vector<PatternResult> list) {
        for (int i = 0; i < list.size(); ++i) {
            final Pattern pattern = list.get(i).getPattern();
            if (ap.getStr().equalsIgnoreCase(pattern.getStr())) {
                return true;
            }
        }
        return false;
    }
}
