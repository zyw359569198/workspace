package com.reign.gcld.scenario.message;

public class ScenarioEventTimeTickMessage extends ScenarioEventMessage
{
    public ScenarioEventTimeTickMessage(final Integer playerId) {
        super(playerId, 7);
    }
}
