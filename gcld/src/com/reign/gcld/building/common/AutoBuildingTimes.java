package com.reign.gcld.building.common;

public class AutoBuildingTimes
{
    public int type;
    public int exeTimes;
    public int fisTimes;
    public long startTime;
    
    public AutoBuildingTimes() {
    }
    
    public AutoBuildingTimes(final int type, final int exeTimes, final int fisTimes, final long startTime) {
        this.type = type;
        this.exeTimes = exeTimes;
        this.fisTimes = fisTimes;
        this.startTime = startTime;
    }
    
    public AutoBuildingTimes(final int type, final int exeTimes, final int fisTimes) {
        this.type = type;
        this.exeTimes = exeTimes;
        this.fisTimes = fisTimes;
    }
}
