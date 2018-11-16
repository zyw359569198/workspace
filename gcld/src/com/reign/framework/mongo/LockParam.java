package com.reign.framework.mongo;

import java.util.concurrent.*;

public class LockParam
{
    public LockMode lockMode;
    public Long time;
    public TimeUnit timeUnit;
    
    public LockParam(final LockMode mode) {
        this.lockMode = mode;
        this.check();
    }
    
    public LockParam(final LockMode mode, final long time, final TimeUnit timeUnit) {
        this.lockMode = mode;
        this.time = time;
        this.timeUnit = timeUnit;
        this.check();
    }
    
    private void check() {
        switch (this.lockMode) {
            case TRY_LOCK: {
                if (this.time == null || this.timeUnit == null) {
                    throw new IllegalArgumentException("time and timeunit must not be null");
                }
                break;
            }
        }
    }
}
