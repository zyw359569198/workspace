package com.reign.gcld.event.common;

import java.util.concurrent.atomic.*;
import com.reign.gcld.common.log.*;
import com.reign.framework.exception.*;
import com.reign.gcld.common.message.*;
import com.reign.gcld.common.*;
import java.util.*;

public abstract class DefaultEvent implements Event
{
    private static final Logger log;
    public int eventId;
    public int startTaskId;
    public int overTaskId;
    public Date startTime;
    public Date endTime;
    public String paramInfo;
    public boolean isOver;
    public Handler handler;
    public AtomicBoolean notifyStart;
    public AtomicBoolean notifyOver;
    
    static {
        log = CommonLog.getLog(DefaultEvent.class);
    }
    
    public DefaultEvent(final int eventId, final Date startTime, final Date endTime, final String paramInfo) {
        this.notifyStart = new AtomicBoolean();
        this.notifyOver = new AtomicBoolean();
        if (startTime == null || endTime == null || endTime.before(startTime)) {
            throw new InternalException("invalidate event time, [eventId:" + eventId + "]");
        }
        this.eventId = eventId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.paramInfo = paramInfo;
        this.isOver = false;
        this.handler = HandlerManager.getHandler(Event.class);
    }
    
    @Override
    public int getEventId() {
        return this.eventId;
    }
    
    @Override
    public boolean isEventTime() {
        final Date nowDate = new Date();
        final boolean result = !this.isOver && !nowDate.before(this.startTime) && !nowDate.after(this.endTime);
        if (!result && !nowDate.before(this.startTime)) {
            this.notifyEventOver();
        }
        return result;
    }
    
    @Override
    public Date getStartTime() {
        return this.startTime;
    }
    
    @Override
    public Date getEndTime() {
        return this.endTime;
    }
    
    @Override
    public synchronized void setEndTime(final Date endTime) {
        this.endTime = endTime;
    }
    
    @Override
    public void notifyEventStart() {
        if (this.notifyStart.compareAndSet(false, true)) {
            int retryTimes = 0;
            while (retryTimes < 3) {
                try {
                    DefaultEvent.log.info("notifyStart event handler start[eventId:" + this.getEventId() + "]");
                    this.handler.handler(new EventMessage(this, EventStage.START));
                    DefaultEvent.log.info("notifyStart event handler end[eventId:" + this.getEventId() + "]");
                    break;
                }
                catch (Exception e) {
                    DefaultEvent.log.error("notifyStart event notifyStart fail[eventId:" + this.getEventId() + "]", e);
                    ++retryTimes;
                }
            }
        }
        else {
            DefaultEvent.log.info("notifyStart event handler notifyStart false[eventId:" + this.getEventId() + "]");
        }
    }
    
    @Override
    public void notifyEventOver() {
        if (this.notifyOver.compareAndSet(false, true)) {
            int retryTimes = 0;
            while (retryTimes < 3) {
                try {
                    DefaultEvent.log.info("notifyOver event handler start[eventId:" + this.getEventId() + "]");
                    this.handler.handler(new EventMessage(this, EventStage.OVER));
                    DefaultEvent.log.info("notifyOver event handler end[eventId:" + this.getEventId() + "]");
                    break;
                }
                catch (Exception e) {
                    DefaultEvent.log.error("notifyOver event notifyOver fail[eventId:" + this.getEventId() + "]", e);
                    ++retryTimes;
                }
            }
        }
        else {
            DefaultEvent.log.info("notifyOver event handler notifyOver false[eventId:" + this.getEventId() + "]");
        }
    }
    
    @Override
    public long getEventCD() {
        return this.isEventTime() ? (this.endTime.getTime() - System.currentTimeMillis()) : 0L;
    }
    
    @Override
    public boolean setEventOver() {
        return this.isOver = true;
    }
    
    @Override
    public int getStartTaskId() {
        return this.startTaskId;
    }
    
    @Override
    public void setStartTaskId(final int startTaskId) {
        this.startTaskId = startTaskId;
    }
    
    @Override
    public int getOverTaskId() {
        return this.overTaskId;
    }
    
    @Override
    public void setOverTaskId(final int overTaskId) {
        this.overTaskId = overTaskId;
    }
    
    @Override
    public void handleOperation(final int type, final int playerId, final int val) {
    }
    
    @Override
    public void startEvent() {
    }
    
    @Override
    public void overEvent() {
    }
    
    @Override
    public int getDayth() {
        final long interval = System.currentTimeMillis() - TimeUtil.getDay0ClackMS(this.startTime);
        return (int)Math.ceil(interval * 1.0 / Constants.ONE_DAY_MS);
    }
    
    @Override
    public List<Date> get0ClockList() {
        long today0ClockMs = TimeUtil.getDay0ClackMS(new Date());
        final List<Date> dateList = new ArrayList<Date>();
        for (int i = 1; i <= 1000; ++i) {
            today0ClockMs += Constants.ONE_DAY_MS;
            if (today0ClockMs >= this.getEndTime().getTime()) {
                break;
            }
            dateList.add(new Date(today0ClockMs));
        }
        return dateList;
    }
}
