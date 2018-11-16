package com.reign.gcld.event.util;

import com.reign.gcld.common.log.*;
import com.reign.gcld.log.*;
import com.reign.util.timer.*;
import com.reign.gcld.common.*;
import com.reign.gcld.event.common.*;
import com.reign.framework.exception.*;
import java.util.*;

public class EventFactory
{
    private static final Logger timerLog;
    public static EventFactory instance;
    private IDataGetter dataGetter;
    
    static {
        timerLog = new TimerLogger();
        EventFactory.instance = new EventFactory();
    }
    
    public static EventFactory getInstance() {
        return EventFactory.instance;
    }
    
    public void init(final IDataGetter dataGetter) {
        this.dataGetter = dataGetter;
    }
    
    public Event createEvent(final int eventId, final Date startTime, final Date endTime, final String paramInfo) {
        switch (eventId) {
            case 9: {
                final Event event = new SlaveEvent(eventId, startTime, endTime, paramInfo, this.dataGetter);
                if (startTime.after(new Date())) {
                    Constants.timer.schedule(new EventNotifyStartTask(event));
                }
                Constants.timer.schedule(new EventNotifyOverTask(event));
                return event;
            }
            case 10: {
                final Event event = new MidAutumnEvent(eventId, startTime, endTime, paramInfo, this.dataGetter);
                if (startTime.after(new Date())) {
                    Constants.timer.schedule(new EventNotifyStartTask(event));
                }
                else {
                    final int dayth = event.getDayth();
                    if (1 == dayth) {
                        this.dataGetter.getJobService().addJob("eventService", "moonCakeTimeTask", "1", TimeUtil.getNext1Day0Clock(), false);
                        this.dataGetter.getJobService().addJob("eventService", "moonCakeTimeTask", "2", TimeUtil.getNext2Day0Clock(), false);
                        EventFactory.timerLog.error("class:EventFactory#method:createEvent#1-day-job_time:" + new Date(TimeUtil.getNext1Day0Clock()));
                        EventFactory.timerLog.error("class:EventFactory#method:createEvent#2-day-job_time:" + new Date(TimeUtil.getNext2Day0Clock()));
                    }
                    else if (2 == dayth) {
                        this.dataGetter.getJobService().addJob("eventService", "moonCakeTimeTask", "2", TimeUtil.getNext1Day0Clock(), false);
                        EventFactory.timerLog.error("class:EventFactory#method:createEvent#2-day-job_time:" + new Date(TimeUtil.getNext1Day0Clock()));
                    }
                }
                Constants.timer.schedule(new EventNotifyOverTask(event));
                return event;
            }
            case 11: {
                final Event event = new NationalDayEvent(eventId, startTime, endTime, paramInfo, this.dataGetter);
                if (startTime.after(new Date())) {
                    Constants.timer.schedule(new EventNotifyStartTask(event));
                }
                else {
                    final int dayth = event.getDayth();
                    if (1 == dayth) {
                        this.dataGetter.getJobService().addJob("eventService", "nationalDayTimeTask", "1", TimeUtil.getNext1Day0Clock(), false);
                        this.dataGetter.getJobService().addJob("eventService", "nationalDayTimeTask", "2", TimeUtil.getNext2Day0Clock(), false);
                        EventFactory.timerLog.error("class:EventFactory#method:createEvent#target:nationalDayTimeTask#1-day-job_time:" + new Date(TimeUtil.getNext1Day0Clock()));
                        EventFactory.timerLog.error("class:EventFactory#method:createEvent#target:nationalDayTimeTask#2-day-job_time:" + new Date(TimeUtil.getNext2Day0Clock()));
                    }
                    else if (2 == dayth) {
                        this.dataGetter.getJobService().addJob("eventService", "nationalDayTimeTask", "2", TimeUtil.getNext1Day0Clock(), false);
                        EventFactory.timerLog.error("class:EventFactory#method:createEvent#target:nationalDayTimeTask#2-day-job_time:" + new Date(TimeUtil.getNext1Day0Clock()));
                    }
                }
                Constants.timer.schedule(new EventNotifyOverTask(event));
                return event;
            }
            case 12: {
                final Event event = new ResourceAdditionEvent(eventId, startTime, endTime, paramInfo, this.dataGetter);
                if (startTime.after(new Date())) {
                    Constants.timer.schedule(new EventNotifyStartTask(event));
                }
                Constants.timer.schedule(new EventNotifyOverTask(event));
                return event;
            }
            case 13: {
                final Event event = new IronRewardEvent(eventId, startTime, endTime, paramInfo, this.dataGetter);
                if (startTime.after(new Date())) {
                    Constants.timer.schedule(new EventNotifyStartTask(event));
                }
                Constants.timer.schedule(new EventNotifyOverTask(event));
                return event;
            }
            case 14: {
                final Event event = new XiLianEvent(eventId, startTime, endTime, paramInfo, this.dataGetter);
                if (startTime.after(new Date())) {
                    Constants.timer.schedule(new EventNotifyStartTask(event));
                }
                else {
                    final List<Date> dateList = event.get0ClockList();
                    for (int size = dateList.size(), i = 0; i < size; ++i) {
                        final Date date = dateList.get(i);
                        this.dataGetter.getJobService().addJob("eventService", "xiLianTimeTask", String.valueOf(i + 1), date.getTime(), false);
                        EventFactory.timerLog.error("class:EventFactory#method:createEvent#target:xiLianTimeTask#" + String.valueOf(i + 1) + "-day-job_time:" + date);
                    }
                }
                Constants.timer.schedule(new EventNotifyOverTask(event));
                return event;
            }
            case 15: {
                final Event event = new IronGiveEvent(eventId, startTime, endTime, paramInfo, this.dataGetter);
                if (startTime.after(new Date())) {
                    Constants.timer.schedule(new EventNotifyStartTask(event));
                }
                Constants.timer.schedule(new EventNotifyOverTask(event));
                return event;
            }
            case 16: {
                final Event event = new ChristmasDayEvent(eventId, startTime, endTime, paramInfo, this.dataGetter);
                if (startTime.after(new Date())) {
                    Constants.timer.schedule(new EventNotifyStartTask(event));
                }
                Constants.timer.schedule(new EventNotifyOverTask(event));
                return event;
            }
            case 17: {
                final Event event = new WishEvent(eventId, startTime, endTime, paramInfo, this.dataGetter);
                if (startTime.after(new Date())) {
                    Constants.timer.schedule(new EventNotifyStartTask(event));
                }
                Constants.timer.schedule(new EventNotifyOverTask(event));
                return event;
            }
            case 18: {
                final Event event = new BeastEvent(eventId, startTime, endTime, paramInfo, this.dataGetter);
                if (startTime.after(new Date())) {
                    Constants.timer.schedule(new EventNotifyStartTask(event));
                }
                Constants.timer.schedule(new EventNotifyOverTask(event));
                return event;
            }
            case 19: {
                final Event event = new BaiNianEvent(eventId, startTime, endTime, paramInfo, this.dataGetter);
                if (startTime.after(new Date())) {
                    Constants.timer.schedule(new EventNotifyStartTask(event));
                }
                Constants.timer.schedule(new EventNotifyOverTask(event));
                return event;
            }
            case 20: {
                final Event event = new RedPaperEvent(eventId, startTime, endTime, paramInfo, this.dataGetter);
                if (startTime.after(new Date())) {
                    Constants.timer.schedule(new EventNotifyStartTask(event));
                }
                Constants.timer.schedule(new EventNotifyOverTask(event));
                return event;
            }
            case 21: {
                final Event event = new LanternEvent(eventId, startTime, endTime, paramInfo, this.dataGetter);
                if (startTime.after(new Date())) {
                    Constants.timer.schedule(new EventNotifyStartTask(event));
                }
                Constants.timer.schedule(new EventNotifyOverTask(event));
                return event;
            }
            default: {
                throw new InternalException("unknow event [eventId:" + eventId + "]");
            }
        }
    }
}
