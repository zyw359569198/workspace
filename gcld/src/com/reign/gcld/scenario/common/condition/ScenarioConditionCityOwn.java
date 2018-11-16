package com.reign.gcld.scenario.common.condition;

import com.reign.gcld.common.*;
import com.reign.gcld.scenario.common.*;
import com.reign.gcld.scenario.message.*;
import com.reign.gcld.juben.service.*;
import com.reign.gcld.juben.common.*;

public class ScenarioConditionCityOwn extends ScenarioCondition implements Cloneable
{
    private int cityId;
    private int forceId;
    
    public ScenarioConditionCityOwn(final String[] triggerSingle) {
        this.cityId = Integer.parseInt(triggerSingle[1]);
        this.forceId = Integer.parseInt(triggerSingle[2]);
    }
    
    public int getCityId() {
        return this.cityId;
    }
    
    public void setCityId(final int cityId) {
        this.cityId = cityId;
    }
    
    public int getForceId() {
        return this.forceId;
    }
    
    public void setForceId(final int forceId) {
        this.forceId = forceId;
    }
    
    @Override
    public ScenarioConditionCityOwn clone() throws CloneNotSupportedException {
        return (ScenarioConditionCityOwn)super.clone();
    }
    
    @Override
    protected boolean doCheck(final IDataGetter dataGetter, final int playerId, final ScenarioEvent scenarioEvent, final ScenarioEventMessage scenarioEventMessage) {
        return this.checkRequest(dataGetter, playerId, scenarioEvent);
    }
    
    @Override
    protected boolean isConcernedMessage(final ScenarioEventMessage scenarioEventMessage) {
        return true;
    }
    
    @Override
    public boolean checkRequest(final IDataGetter getter, final int playerId, final ScenarioEvent scenarioEvent) {
        final JuBenDto dto = JuBenManager.getInstance().getByPid(playerId);
        if (dto == null) {
            return false;
        }
        final JuBenCityDto cityDto = dto.juBenCityDtoMap.get(this.cityId);
        if (cityDto == null) {
            return false;
        }
        final int forceId = cityDto.forceId;
        if (this.forceId == 1) {
            return forceId < 1 || forceId > 3;
        }
        if (this.forceId == 4) {
            final int changeForceId = JuBenService.changeForceId(dto.player_force_id);
            return changeForceId == forceId;
        }
        if (this.forceId == 0) {
            return dto.player_force_id == forceId;
        }
        return forceId == this.forceId;
    }
}
