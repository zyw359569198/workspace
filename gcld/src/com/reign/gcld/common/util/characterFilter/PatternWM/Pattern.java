package com.reign.gcld.common.util.characterFilter.PatternWM;

public class Pattern
{
    private String str;
    public int length;
    
    public Pattern(final String str) {
        this.str = str;
        this.length = str.length();
    }
    
    public String getStr() {
        return this.str;
    }
    
    public int getLength() {
        return this.length;
    }
    
    public char charAtEnd(final int index) {
        if (this.length > index) {
            return this.str.charAt(this.length - index - 1);
        }
        return '\0';
    }
    
    public boolean findMatchInString(final String str) {
        if (this.length > str.length()) {
            return false;
        }
        final int beginIndex = str.length() - this.length;
        final String eqaulLengthStr = str.substring(beginIndex);
        return this.str.equalsIgnoreCase(eqaulLengthStr);
    }
}
