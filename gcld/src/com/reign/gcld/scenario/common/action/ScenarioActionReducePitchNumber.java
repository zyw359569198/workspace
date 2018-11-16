package com.reign.gcld.scenario.common.action;

import com.reign.gcld.common.*;
import com.reign.gcld.scenario.common.*;
import com.reign.gcld.juben.service.*;
import com.reign.gcld.juben.common.*;

public class ScenarioActionReducePitchNumber extends ScenarioAction
{
    private int number;
    private int generalId;
    
    public int getGeneralId() {
        return this.generalId;
    }
    
    public void setGeneralId(final int generalId) {
        this.generalId = generalId;
    }
    
    public ScenarioActionReducePitchNumber(final String[] operationSingle) {
        if (operationSingle.length >= 3) {
            this.number = Integer.parseInt(operationSingle[2]);
            this.generalId = Integer.parseInt(operationSingle[1]);
        }
    }
    
    public int getNumber() {
        return this.number;
    }
    
    public void setNumber(final int number) {
        this.number = number;
    }
    
    @Override
    public void doWork(final IDataGetter getter, final int playerId, final ScenarioEvent scenarioEvent) {
        if (this.number <= 0) {
            return;
        }
        final JuBenDto dto = JuBenManager.getInstance().getByPid(playerId);
        if (dto == null) {
            return;
        }
        for (final ScenarioEvent event : dto.eventList) {
            final ScenarioAction action = event.getOperation();
            if (action != null) {
                if (action instanceof ScenarioActionMarching) {
                    final ScenarioActionMarching cast = (ScenarioActionMarching)action;
                    cast.reducePitchNumber(this.generalId, this.number);
                }
                else if (action instanceof ScenarioActionAnd) {
                    final ScenarioActionAnd cast2 = (ScenarioActionAnd)action;
                    cast2.reducePitchNumber(this.generalId, this.number);
                }
                else {
                    if (!(action instanceof ScenarioActionRepeatAction)) {
                        continue;
                    }
                    final ScenarioActionRepeatAction cast3 = (ScenarioActionRepeatAction)action;
                    cast3.reducePitchNumber(this.generalId, this.number);
                }
            }
        }
    }
    
    @Override
    public ScenarioActionReducePitchNumber clone() throws CloneNotSupportedException {
        return (ScenarioActionReducePitchNumber)super.clone();
    }
}
