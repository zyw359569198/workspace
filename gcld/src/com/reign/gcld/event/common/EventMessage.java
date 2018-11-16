package com.reign.gcld.event.common;

import com.reign.gcld.common.message.*;

public class EventMessage implements Message
{
    public static int STATE_START;
    public static int STATE_OVER;
    private Event event;
    private EventStage eventStage;
    
    static {
        EventMessage.STATE_START = 0;
        EventMessage.STATE_OVER = 1;
    }
    
    public void setEvent(final Event event) {
        this.event = event;
    }
    
    public Event getEvent() {
        return this.event;
    }
    
    public void setEventStage(final EventStage eventStage) {
        this.eventStage = eventStage;
    }
    
    public EventStage getEventStage() {
        return this.eventStage;
    }
    
    public EventMessage(final Event event, final EventStage eventStage) {
        this.event = event;
        this.eventStage = eventStage;
    }
}
