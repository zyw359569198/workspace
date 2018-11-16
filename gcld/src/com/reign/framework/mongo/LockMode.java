package com.reign.framework.mongo;

public enum LockMode
{
    LOCK("LOCK", 0), 
    TRY_LOCK_ONCE("TRY_LOCK_ONCE", 1), 
    TRY_LOCK("TRY_LOCK", 2);
    
    private LockMode(final String s, final int n) {
    }
}
