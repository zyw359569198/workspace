package com.reign.gcld.scenario.common.action;

import com.reign.gcld.common.*;
import com.reign.gcld.scenario.common.*;

public class ScenarioActionKillAll extends ScenarioAction implements Cloneable
{
    private int cityId;
    private int camp;
    
    public int getCityId() {
        return this.cityId;
    }
    
    public void setCityId(final int cityId) {
        this.cityId = cityId;
    }
    
    public int getCamp() {
        return this.camp;
    }
    
    public void setCamp(final int camp) {
        this.camp = camp;
    }
    
    public ScenarioActionKillAll(final String[] operationSingle) {
        this.cityId = 0;
        this.camp = 0;
        final int cityId = Integer.parseInt(operationSingle[1]);
        final int camp = Integer.parseInt(operationSingle[2]);
        this.cityId = cityId;
        this.camp = camp;
    }
    
    @Override
    public void doWork(final IDataGetter getter, final int playerId, final ScenarioEvent scenarioEvent) {
        if (this.camp == 0) {
            getter.getJuBenService().killAllPlayerPgmsInThisCity(playerId, this.cityId);
        }
        else {
            getter.getJuBenService().killAllNpcsInThisCity(playerId, this.cityId);
        }
    }
    
    @Override
    public ScenarioActionKillAll clone() throws CloneNotSupportedException {
        return (ScenarioActionKillAll)super.clone();
    }
}
