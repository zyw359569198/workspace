package com.reign.gcld.scenario.common.action;

import com.reign.gcld.common.*;
import com.reign.gcld.scenario.common.*;

public class ScenarioActionTimeChange extends ScenarioAction implements Cloneable
{
    long reduceTime;
    
    public ScenarioActionTimeChange(final String[] operationSingle) {
        this.reduceTime = Long.parseLong(operationSingle[1]);
    }
    
    @Override
    public void doWork(final IDataGetter getter, final int playerId, final ScenarioEvent scenarioEvent) {
        getter.getJuBenService().addEndTime(playerId, this.reduceTime * 1000L);
    }
    
    @Override
    public ScenarioActionTimeChange clone() throws CloneNotSupportedException {
        return (ScenarioActionTimeChange)super.clone();
    }
}
