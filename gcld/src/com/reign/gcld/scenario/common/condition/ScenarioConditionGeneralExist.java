package com.reign.gcld.scenario.common.condition;

import com.reign.gcld.common.*;
import com.reign.gcld.scenario.message.*;
import com.reign.gcld.scenario.common.*;

public class ScenarioConditionGeneralExist extends ScenarioCondition implements Cloneable
{
    private int generalId;
    private int cityId;
    
    public ScenarioConditionGeneralExist(final String[] triggerSingle) {
        this.generalId = 0;
        this.cityId = Integer.parseInt(triggerSingle[1]);
    }
    
    @Override
	public boolean doCheck(final IDataGetter dataGetter, final int playerId, final ScenarioEvent scenarioEvent, final ScenarioEventMessage scenarioEventMessage) {
        boolean result = false;
        if (this.isConcernedMessage(scenarioEventMessage)) {
            final ScenarioEventMessageMoveToCity moveMessage = (ScenarioEventMessageMoveToCity)scenarioEventMessage;
            final int generalIdInMessage = moveMessage.getGeneralId();
            if (this.generalId == 0 || generalIdInMessage == this.generalId) {
                result = (this.cityId == moveMessage.getCityId());
            }
            final boolean conditionBak = this.conditionFulfil;
            this.conditionFulfil = result;
            boolean change = false;
            if (conditionBak != this.conditionFulfil) {
                change = !conditionBak;
                scenarioEvent.eventChangeToSave(dataGetter, playerId);
            }
            if (change && moveMessage.getCityId() == 6) {
                ScenarioEventJsonBuilder.sendDialog(playerId, 1001, null);
            }
            return result;
        }
        final int number = dataGetter.getPlayerGeneralMilitaryDao().getGeneralNumInCity(playerId, this.cityId);
        return number > 0;
    }
    
    @Override
    protected boolean isConcernedMessage(final ScenarioEventMessage scenarioEventMessage) {
        return scenarioEventMessage instanceof ScenarioEventMessageMoveToCity;
    }
    
    @Override
    public ScenarioConditionGeneralExist clone() throws CloneNotSupportedException {
        return (ScenarioConditionGeneralExist)super.clone();
    }
    
    @Override
    protected String getConditionType() {
        return "general_exist";
    }
    
    @Override
    public boolean checkRequest(final IDataGetter getter, final int playerId, final ScenarioEvent scenarioEvent) {
        final int number = getter.getPlayerGeneralMilitaryDao().getGeneralNumInCity(playerId, this.cityId);
        return number > 0;
    }
}
