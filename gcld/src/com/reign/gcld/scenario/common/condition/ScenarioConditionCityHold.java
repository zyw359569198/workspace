package com.reign.gcld.scenario.common.condition;

import com.reign.gcld.common.*;
import com.reign.gcld.scenario.common.*;
import com.reign.gcld.scenario.message.*;
import org.apache.commons.lang.*;
import com.reign.gcld.juben.service.*;
import com.reign.gcld.juben.common.*;

public class ScenarioConditionCityHold extends ScenarioCondition implements Cloneable
{
    private int cityGoal;
    private int cityNow;
    
    public int getCityGoal() {
        return this.cityGoal;
    }
    
    public void setCityGoal(final int cityGoal) {
        this.cityGoal = cityGoal;
    }
    
    public int getCityNow() {
        return this.cityNow;
    }
    
    public void setCityNow(final int cityNow) {
        this.cityNow = cityNow;
    }
    
    public ScenarioConditionCityHold(final String[] triggerSingle) {
        this.cityGoal = 0;
        this.cityNow = 0;
        this.cityGoal = Integer.parseInt(triggerSingle[1]);
        this.cityNow = 1;
    }
    
    @Override
    protected boolean doCheck(final IDataGetter dataGetter, final int playerId, final ScenarioEvent scenarioEvent, final ScenarioEventMessage scenarioEventMessage) {
        if (this.isConcernedMessage(scenarioEventMessage)) {
            if (scenarioEventMessage != null) {
                ++this.cityNow;
                scenarioEvent.eventChangeToSave(dataGetter, playerId);
            }
            return this.conditionFulfil = (this.cityNow >= this.cityGoal);
        }
        return this.conditionFulfil;
    }
    
    @Override
    protected boolean isConcernedMessage(final ScenarioEventMessage scenarioEventMessage) {
        return scenarioEventMessage instanceof ScenarioEventMessageCityHold;
    }
    
    @Override
    public ScenarioConditionCityHold clone() throws CloneNotSupportedException {
        return (ScenarioConditionCityHold)super.clone();
    }
    
    @Override
    public String selfStore() {
        if (this.conditionFulfil) {
            return String.valueOf(this.getConditionType()) + "," + this.conditionFulfil;
        }
        return String.valueOf(this.getConditionType()) + "," + this.conditionFulfil + "," + this.cityNow;
    }
    
    @Override
    protected String getConditionType() {
        return "city_hold";
    }
    
    @Override
    public void selfRestore(final String conditionInfo, final int playerId) {
        if (StringUtils.isBlank(conditionInfo)) {
            return;
        }
        final String[] single = conditionInfo.split(",");
        if (single.length <= 0) {
            return;
        }
        final boolean state = Boolean.parseBoolean(single[1]);
        if (state) {
            this.setConditionFulfil(state);
        }
        else {
            final int now = Integer.parseInt(single[2]);
            this.setCityNow(now);
        }
    }
    
    @Override
    public boolean checkRequest(final IDataGetter getter, final int playerId, final ScenarioEvent scenarioEvent) {
        final JuBenDto dto = JuBenManager.getInstance().getByPid(playerId);
        if (dto == null) {
            return false;
        }
        int count = 0;
        for (final JuBenCityDto cityDto : dto.juBenCityDtoMap.values()) {
            if (cityDto.cityId == dto.player_force_id) {
                ++count;
            }
        }
        return count >= this.cityGoal;
    }
}
