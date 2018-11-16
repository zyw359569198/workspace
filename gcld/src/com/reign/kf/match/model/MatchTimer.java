package com.reign.kf.match.model;

import com.reign.util.timer.*;

public class MatchTimer extends BaseSystemTimeTimer
{
    public MatchTimer() {
        super(10);
    }
    
    public MatchTimer(final int num) {
        super(num);
    }
    
    public <T extends BaseSystemTimeTimerTask> void addTask(final T task) {
        super.schedule(task);
    }
}
