package com.reign.gcld.antiaddiction;

public class AntiAddictionStateIntervalHalfEarnings extends AntiAddictionStateInterval
{
    public AntiAddictionStateIntervalHalfEarnings(final long min, final long max, final int level) {
        super(min, max, level);
    }
    
    @Override
    public long getIntDataAfterAntiAddiction(final long val) {
        return val / 2L;
    }
    
    @Override
    public String getAddictionLoseLevel() {
        return "half";
    }
}
