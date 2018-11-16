package com.reign.framework.common.concurrent;

public enum LockType
{
    READ("READ", 0), 
    WRITE("WRITE", 1);
    
    private LockType(final String s, final int n) {
    }
}
