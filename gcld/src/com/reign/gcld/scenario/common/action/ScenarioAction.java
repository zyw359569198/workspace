package com.reign.gcld.scenario.common.action;

import com.reign.gcld.common.*;
import com.reign.gcld.scenario.common.*;

public class ScenarioAction implements Cloneable
{
    int executeTimesGoal;
    public long repeatCirce;
    int excutedTimes;
    public long excuteTime;
    public boolean hasMarching;
    
    public ScenarioAction() {
        this.executeTimesGoal = 1;
        this.repeatCirce = 0L;
        this.excutedTimes = 0;
        this.excuteTime = 0L;
        this.hasMarching = false;
    }
    
    public void work(final IDataGetter getter, final int playerId, final ScenarioEvent scenarioEvent) {
        if (this.executeTimesGoal <= 0) {
            this.doWork(getter, playerId, scenarioEvent);
        }
        else {
            if (this.excutedTimes >= this.executeTimesGoal) {
                return;
            }
            final long now = System.currentTimeMillis();
            final long last = now - this.excuteTime;
            if (last >= this.repeatCirce) {
                this.doWork(getter, playerId, scenarioEvent);
                this.excuteTime = System.currentTimeMillis();
                ++this.excutedTimes;
            }
        }
    }
    
    public void doWork(final IDataGetter getter, final int playerId, final ScenarioEvent scenarioEvent) {
    }
    
    @Override
	public ScenarioAction clone() throws CloneNotSupportedException {
        final ScenarioAction scenarioAction = (ScenarioAction)super.clone();
        return scenarioAction;
    }
    
    public void retoreChasing(final String chasingInfo, final long restoreTime) {
    }
}
