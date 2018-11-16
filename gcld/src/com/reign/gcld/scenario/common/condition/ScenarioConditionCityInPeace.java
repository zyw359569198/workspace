package com.reign.gcld.scenario.common.condition;

import com.reign.gcld.common.*;
import com.reign.gcld.scenario.common.*;
import com.reign.gcld.scenario.message.*;
import com.reign.gcld.juben.service.*;
import com.reign.gcld.juben.common.*;

public class ScenarioConditionCityInPeace extends ScenarioCondition implements Cloneable
{
    private int cityId;
    private int cityState;
    
    public ScenarioConditionCityInPeace(final String[] triggerSingle) {
        this.cityId = Integer.parseInt(triggerSingle[1]);
        this.cityState = 0;
    }
    
    @Override
	public boolean doCheck(final IDataGetter dataGetter, final int playerId, final ScenarioEvent scenarioEvent, final ScenarioEventMessage scenarioEventMessage) {
        final JuBenDto juBenDto = JuBenManager.getInstance().getByPid(playerId);
        if (juBenDto == null) {
            return false;
        }
        final JuBenCityDto cityDto = juBenDto.juBenCityDtoMap.get(this.cityId);
        if (cityDto != null) {
            final boolean result = cityDto.state == this.cityState;
            final boolean conditionBak = this.conditionFulfil;
            this.conditionFulfil = result;
            if (conditionBak != this.conditionFulfil) {
                scenarioEvent.eventChangeToSave(dataGetter, playerId);
            }
            return result;
        }
        return false;
    }
    
    @Override
    protected boolean isConcernedMessage(final ScenarioEventMessage scenarioEventMessage) {
        return true;
    }
    
    @Override
    public ScenarioConditionCityInPeace clone() throws CloneNotSupportedException {
        return (ScenarioConditionCityInPeace)super.clone();
    }
    
    @Override
    protected String getConditionType() {
        return "peace";
    }
    
    @Override
    public boolean checkRequest(final IDataGetter getter, final int playerId, final ScenarioEvent scenarioEvent) {
        final JuBenDto juBenDto = JuBenManager.getInstance().getByPid(playerId);
        if (juBenDto == null) {
            return false;
        }
        final JuBenCityDto cityDto = juBenDto.juBenCityDtoMap.get(this.cityId);
        return cityDto != null && cityDto.state == this.cityState;
    }
}
