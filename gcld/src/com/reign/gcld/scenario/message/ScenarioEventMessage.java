package com.reign.gcld.scenario.message;

import com.reign.gcld.common.message.*;

public class ScenarioEventMessage implements Message
{
    private int playerId;
    private int scenarioMessageType;
    
    public ScenarioEventMessage(final int playerId, final int scenarioMessageType) {
        this.playerId = playerId;
        this.scenarioMessageType = scenarioMessageType;
    }
    
    public ScenarioEventMessage() {
    }
    
    public int getPlayerId() {
        return this.playerId;
    }
    
    public int getTaskMessageType() {
        return this.scenarioMessageType;
    }
}
