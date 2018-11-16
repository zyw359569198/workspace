package com.reign.gcld.scenario.message;

public class ScenarioEventMessageEventFinish extends ScenarioEventMessage
{
    private int eventId;
    private int choice;
    
    public int getChoice() {
        return this.choice;
    }
    
    public void setChoice(final int choice) {
        this.choice = choice;
    }
    
    public int getEventId() {
        return this.eventId;
    }
    
    public void setEventId(final int eventId) {
        this.eventId = eventId;
    }
    
    public ScenarioEventMessageEventFinish(final int playerId, final int eventId, final int choice) {
        super(playerId, 5);
        this.eventId = 0;
        this.choice = 0;
        this.eventId = eventId;
        this.choice = choice;
    }
}
