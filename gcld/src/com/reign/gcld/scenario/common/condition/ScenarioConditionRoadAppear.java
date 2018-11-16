package com.reign.gcld.scenario.common.condition;

import com.reign.gcld.common.*;
import com.reign.gcld.scenario.common.*;
import com.reign.gcld.scenario.message.*;
import com.reign.gcld.juben.service.*;
import com.reign.gcld.juben.common.*;

public class ScenarioConditionRoadAppear extends ScenarioCondition
{
    int roadId;
    int isLinked;
    
    public ScenarioConditionRoadAppear(final String[] triggerSingle) {
        this.roadId = Integer.parseInt(triggerSingle[1]);
        this.isLinked = Integer.parseInt(triggerSingle[2]);
    }
    
    @Override
    public ScenarioCondition clone() throws CloneNotSupportedException {
        return super.clone();
    }
    
    @Override
    protected boolean doCheck(final IDataGetter dataGetter, final int playerId, final ScenarioEvent scenarioEvent, final ScenarioEventMessage scenarioEventMessage) {
        return this.conditionFulfil = this.checkRequest(dataGetter, playerId, scenarioEvent);
    }
    
    @Override
    protected boolean isConcernedMessage(final ScenarioEventMessage scenarioEventMessage) {
        return true;
    }
    
    @Override
    public boolean isConditionFulfil() {
        return this.conditionFulfil;
    }
    
    @Override
    public void setConditionFulfil(final boolean conditionFulfil) {
        super.setConditionFulfil(conditionFulfil);
    }
    
    @Override
    public boolean checkRequest(final IDataGetter getter, final int playerId, final ScenarioEvent scenarioEvent) {
        final JuBenDto juBenDto = JuBenManager.getInstance().getByPid(playerId);
        if (juBenDto == null) {
            return false;
        }
        boolean roadLinked = true;
        if (juBenDto.roadLinked != null && !juBenDto.roadLinked.isEmpty()) {
            roadLinked = !juBenDto.roadLinked.contains(this.roadId);
        }
        return (this.isLinked == 0) ? roadLinked : (!roadLinked);
    }
    
    @Override
    protected String getConditionType() {
        return "appear";
    }
}
