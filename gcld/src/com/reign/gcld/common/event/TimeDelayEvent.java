package com.reign.gcld.common.event;

public class TimeDelayEvent extends CommonEvent
{
    private long timestamp;
    
    public TimeDelayEvent(final int eventId, final int sendPlayerId, final int... recvPlayerIds) {
        super(eventId, sendPlayerId, recvPlayerIds);
        this.timestamp = 0L;
    }
    
    public TimeDelayEvent(final int eventId, final int sendPlayerId, final long timestamp, final Object content, final int... recvPlayerIds) {
        super(eventId, sendPlayerId, content, recvPlayerIds);
        this.timestamp = timestamp;
    }
    
    @Override
    public long getTimestamp() {
        return this.timestamp;
    }
    
    @Override
    public int getEventType() {
        return 3;
    }
    
    public void setTimestamp(final long timestamp) {
        this.timestamp = timestamp;
    }
}
