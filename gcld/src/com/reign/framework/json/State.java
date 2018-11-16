package com.reign.framework.json;

public enum State
{
    SUCCESS("SUCCESS", 0, 1), 
    FAIL("FAIL", 1, 0), 
    EXCEPTION("EXCEPTION", 2, 2), 
    PUSH("PUSH", 3, 3), 
    REDIRECT("REDIRECT", 4, 4), 
    BLOCK("BLOCK", 5, 5), 
    CODE("CODE", 6, 6), 
    SPECIAL_TIPS("SPECIAL_TIPS", 7, 7);
    
    private int value;
    
    private State(final String s, final int n, final int value) {
        this.value = value;
    }
    
    public int getValue() {
        return this.value;
    }
}
