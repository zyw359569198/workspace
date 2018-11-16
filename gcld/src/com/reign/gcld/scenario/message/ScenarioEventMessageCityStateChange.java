package com.reign.gcld.scenario.message;

public class ScenarioEventMessageCityStateChange extends ScenarioEventMessage
{
    private int state;
    private int cityId;
    
    public int getCityId() {
        return this.cityId;
    }
    
    public void setCityId(final int cityId) {
        this.cityId = cityId;
    }
    
    public int getState() {
        return this.state;
    }
    
    public void setState(final int state) {
        this.state = state;
    }
    
    public ScenarioEventMessageCityStateChange(final int cityId, final int state, final int playerId) {
        super(playerId, 6);
        this.state = 0;
        this.cityId = 0;
        this.state = state;
        this.cityId = cityId;
    }
}
