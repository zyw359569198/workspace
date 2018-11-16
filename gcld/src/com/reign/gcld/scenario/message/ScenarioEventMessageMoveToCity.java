package com.reign.gcld.scenario.message;

public class ScenarioEventMessageMoveToCity extends ScenarioEventMessage
{
    private int generalId;
    private int cityId;
    
    public int getGeneralId() {
        return this.generalId;
    }
    
    public void setGeneralId(final int generalId) {
        this.generalId = generalId;
    }
    
    public int getCityId() {
        return this.cityId;
    }
    
    public void setCityId(final int cityId) {
        this.cityId = cityId;
    }
    
    public ScenarioEventMessageMoveToCity(final int playerId2, final int generalId2, final int cityId2) {
        super(playerId2, 1);
        this.generalId = generalId2;
        this.cityId = cityId2;
    }
}
