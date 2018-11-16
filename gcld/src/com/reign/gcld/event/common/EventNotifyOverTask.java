package com.reign.gcld.event.common;

import com.reign.util.timer.*;
import com.reign.gcld.common.log.*;
import com.reign.gcld.log.*;
import com.reign.util.*;

public class EventNotifyOverTask extends BaseSystemTimeTimerTask
{
    private static final Logger log;
    private static final Logger dayReportLogger;
    private Event event;
    
    static {
        log = CommonLog.getLog(EventNotifyOverTask.class);
        dayReportLogger = new DayReportLogger();
    }
    
    public EventNotifyOverTask(final Event event) {
        super(event.getEndTime().getTime());
        (this.event = event).setOverTaskId(this.getTaskId());
    }
    
    @Override
	public void run() {
        try {
            EventNotifyOverTask.log.info("[eventId: " + this.event.getEventId() + "] notify over start");
            this.event.notifyEventOver();
            EventNotifyOverTask.log.info("[eventId: " + this.event.getEventId() + "] notify over end");
            for (final String log : ThreadLocalFactory.getTreadLocalLogs()) {
                EventNotifyOverTask.dayReportLogger.info(log);
            }
        }
        catch (Exception e) {
            EventNotifyOverTask.log.error("EventNotifyOverTask thread error", e);
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
