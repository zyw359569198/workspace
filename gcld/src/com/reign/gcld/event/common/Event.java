package com.reign.gcld.event.common;

import com.reign.gcld.common.message.*;
import java.util.*;

public interface Event extends Message
{
    int getEventId();
    
    boolean isEventTime();
    
    long getEventCD();
    
    boolean setEventOver();
    
    void notifyEventStart();
    
    void notifyEventOver();
    
    Date getStartTime();
    
    Date getEndTime();
    
    void setEndTime(final Date p0);
    
    int getStartTaskId();
    
    void setStartTaskId(final int p0);
    
    int getOverTaskId();
    
    void setOverTaskId(final int p0);
    
    void handleOperation(final int p0, final int p1, final int p2);
    
    void startEvent();
    
    void overEvent();
    
    int getDayth();
    
    List<Date> get0ClockList();
}
