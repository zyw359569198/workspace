package com.reign.gcld.scenario.common.action;

import com.reign.gcld.common.*;
import com.reign.gcld.scenario.common.*;
import com.reign.gcld.juben.service.*;
import com.reign.gcld.juben.common.*;

public class ScenarioActionScenarioOver extends ScenarioAction implements Cloneable
{
    public ScenarioActionScenarioOver(final String[] operationSingle) {
    }
    
    public ScenarioActionScenarioOver() {
    }
    
    @Override
    public void doWork(final IDataGetter getter, final int playerId, final ScenarioEvent scenarioEvent) {
        final JuBenDto juBenDto = JuBenManager.getInstance().getByPid(playerId);
        getter.getJuBenService().juBenOver(playerId, juBenDto, true);
    }
    
    @Override
    public ScenarioActionScenarioOver clone() throws CloneNotSupportedException {
        final ScenarioActionScenarioOver scenarioActionScenarioOver = (ScenarioActionScenarioOver)super.clone();
        return scenarioActionScenarioOver;
    }
}
