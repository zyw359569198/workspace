package com.reign.gcld.common.event;

public class CommonEvent implements Event
{
    private int sendPlayerId;
    private int[] recvPlayerIds;
    private int eventId;
    private Object content;
    
    public CommonEvent(final int eventId, final int sendPlayerId) {
        this.eventId = eventId;
        this.sendPlayerId = sendPlayerId;
        this.recvPlayerIds = new int[] { sendPlayerId };
    }
    
    public CommonEvent(final int eventId, final int sendPlayerId, final int... recvPlayerIds) {
        this.eventId = eventId;
        this.sendPlayerId = sendPlayerId;
        this.recvPlayerIds = recvPlayerIds;
    }
    
    public CommonEvent(final int eventId, final int sendPlayerId, final Object content, final int... recvPlayerIds) {
        this.eventId = eventId;
        this.content = content;
        this.sendPlayerId = sendPlayerId;
        this.recvPlayerIds = recvPlayerIds;
    }
    
    @Override
    public int getSendPlayerId() {
        return this.sendPlayerId;
    }
    
    @Override
    public int[] getRecvPlayerIds() {
        return this.recvPlayerIds;
    }
    
    @Override
    public int getEventId() {
        return this.eventId;
    }
    
    @Override
    public Object getEventContent() {
        return this.content;
    }
    
    @Override
    public long getTimestamp() {
        return 0L;
    }
    
    @Override
    public int getDelayNum() {
        return 0;
    }
    
    @Override
    public void setDelayNum(final int num) {
    }
    
    @Override
    public int getEventType() {
        return 1;
    }
}
