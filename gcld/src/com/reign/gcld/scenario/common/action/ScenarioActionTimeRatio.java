package com.reign.gcld.scenario.common.action;

import com.reign.gcld.common.*;
import com.reign.gcld.scenario.common.*;
import com.reign.gcld.juben.service.*;
import com.reign.gcld.juben.common.*;

public class ScenarioActionTimeRatio extends ScenarioAction
{
    private float ratio;
    private String pic;
    
    public String getPic() {
        return this.pic;
    }
    
    public void setPic(final String pic) {
        this.pic = pic;
    }
    
    public float getRatio() {
        return this.ratio;
    }
    
    public void setRatio(final float ratio) {
        this.ratio = ratio;
    }
    
    public ScenarioActionTimeRatio(final String[] operationSingle) {
        this.ratio = Float.parseFloat(operationSingle[1]);
        if (operationSingle.length >= 3) {
            this.pic = operationSingle[2];
        }
    }
    
    @Override
    public void doWork(final IDataGetter getter, final int playerId, final ScenarioEvent scenarioEvent) {
        final JuBenDto juBenDto = JuBenManager.getInstance().getByPid(playerId);
        if (juBenDto == null) {
            return;
        }
        for (final ScenarioEvent event : juBenDto.eventList) {
            final ScenarioAction action = event.getOperation();
            if (action != null) {
                if (action instanceof ScenarioActionMarching) {
                    final ScenarioActionMarching cast = (ScenarioActionMarching)action;
                    cast.ratioTime(this.ratio, playerId, this.pic);
                }
                else if (action instanceof ScenarioActionRepeatAction) {
                    final ScenarioActionRepeatAction cast2 = (ScenarioActionRepeatAction)action;
                    cast2.ratioTime(this.ratio, playerId, scenarioEvent, this.pic);
                }
                else {
                    if (!(action instanceof ScenarioActionAnd)) {
                        continue;
                    }
                    final ScenarioActionAnd cast3 = (ScenarioActionAnd)action;
                    cast3.ratioTimeForMarchAndRepeat(this.ratio, playerId, scenarioEvent, this.pic);
                }
            }
        }
    }
    
    @Override
    public ScenarioActionTimeRatio clone() throws CloneNotSupportedException {
        return (ScenarioActionTimeRatio)super.clone();
    }
}
