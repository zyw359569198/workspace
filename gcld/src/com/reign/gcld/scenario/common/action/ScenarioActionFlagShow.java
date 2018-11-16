package com.reign.gcld.scenario.common.action;

import com.reign.gcld.common.*;
import com.reign.gcld.scenario.common.*;

public class ScenarioActionFlagShow extends ScenarioAction implements Cloneable
{
    private int cityId;
    private String pic;
    
    public int getCityId() {
        return this.cityId;
    }
    
    public void setCityId(final int cityId) {
        this.cityId = cityId;
    }
    
    public String getPic() {
        return this.pic;
    }
    
    public void setPic(final String pic) {
        this.pic = pic;
    }
    
    public ScenarioActionFlagShow(final String[] operationSingle) {
        final int cityId = Integer.parseInt(operationSingle[1]);
        final String pic = operationSingle[2];
        this.cityId = cityId;
        this.pic = pic;
    }
    
    @Override
    public void doWork(final IDataGetter getter, final int playerId, final ScenarioEvent scenarioEvent) {
    }
    
    @Override
    public ScenarioActionFlagShow clone() throws CloneNotSupportedException {
        return (ScenarioActionFlagShow)super.clone();
    }
}
