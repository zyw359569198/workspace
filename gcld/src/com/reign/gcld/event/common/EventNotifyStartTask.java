package com.reign.gcld.event.common;

import com.reign.util.timer.*;
import com.reign.gcld.common.log.*;
import com.reign.gcld.log.*;
import com.reign.util.*;

public class EventNotifyStartTask extends BaseSystemTimeTimerTask
{
    private static final Logger log;
    private static final Logger dayReportLogger;
    private Event event;
    
    static {
        log = CommonLog.getLog(EventNotifyStartTask.class);
        dayReportLogger = new DayReportLogger();
    }
    
    public EventNotifyStartTask(final Event event) {
        super(event.getStartTime().getTime());
        (this.event = event).setStartTaskId(this.getTaskId());
    }
    
    @Override
	public void run() {
        try {
            EventNotifyStartTask.log.info("[eventId: " + this.event.getEventId() + "] notify start start");
            this.event.notifyEventStart();
            for (final String log : ThreadLocalFactory.getTreadLocalLogs()) {
                EventNotifyStartTask.dayReportLogger.info(log);
            }
            EventNotifyStartTask.log.info("[eventId: " + this.event.getEventId() + "] notify start end");
        }
        catch (Exception e) {
            EventNotifyStartTask.log.error("EventNotifyStartTask thread error", e);
            return;
        }
        finally {
            ThreadLocalFactory.clearTreadLocalLog();
            ThreadLocalFactory.getTreadLocalLog();
        }
        ThreadLocalFactory.clearTreadLocalLog();
        ThreadLocalFactory.getTreadLocalLog();
    }
}
