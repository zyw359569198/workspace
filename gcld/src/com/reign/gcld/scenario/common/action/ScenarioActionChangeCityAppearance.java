package com.reign.gcld.scenario.common.action;

import com.reign.gcld.scenario.common.*;
import com.reign.gcld.juben.service.*;
import com.reign.framework.json.*;
import com.reign.gcld.common.*;
import com.reign.gcld.juben.common.*;

public class ScenarioActionChangeCityAppearance extends ScenarioAction
{
    private int cityId;
    private int changeToType;
    
    public int getCityId() {
        return this.cityId;
    }
    
    public void setCityId(final int cityId) {
        this.cityId = cityId;
    }
    
    public int getChangeToType() {
        return this.changeToType;
    }
    
    public void setChangeToType(final int changeToType) {
        this.changeToType = changeToType;
    }
    
    public ScenarioActionChangeCityAppearance(final String[] singleOperation) {
        this.cityId = Integer.parseInt(singleOperation[1]);
        this.changeToType = Integer.parseInt(singleOperation[2]);
    }
    
    @Override
    public void doWork(final IDataGetter getter, final int playerId, final ScenarioEvent scenarioEvent) {
        final JuBenDto juBenDto = JuBenManager.getInstance().getByPid(playerId);
        if (juBenDto == null) {
            return;
        }
        if (juBenDto.cityChangeMap == null) {
            return;
        }
        juBenDto.cityChangeMap.put(this.cityId, this.changeToType);
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("cityId", this.cityId);
        doc.createElement("changeToType", this.changeToType);
        doc.endObject();
        Players.push(playerId, PushCommand.PUSH_JUBEN_CITY_CHANGE, doc.toByte());
    }
    
    @Override
    public ScenarioActionChangeCityAppearance clone() throws CloneNotSupportedException {
        return (ScenarioActionChangeCityAppearance)super.clone();
    }
}
