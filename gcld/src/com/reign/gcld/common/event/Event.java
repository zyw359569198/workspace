package com.reign.gcld.common.event;

public interface Event
{
    public static final int ALL_PLAYER_ID = -1;
    public static final int EVENT_TYPE_COMMON = 1;
    public static final int EVENT_TYPE_DELAY = 2;
    public static final int EVENT_TYPE_DELAY_TIME = 3;
    
    int getSendPlayerId();
    
    int[] getRecvPlayerIds();
    
    int getEventId();
    
    Object getEventContent();
    
    long getTimestamp();
    
    int getDelayNum();
    
    void setDelayNum(final int p0);
    
    int getEventType();
}
