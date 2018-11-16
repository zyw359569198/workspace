package com.reign.gcld.event.util;

import com.reign.gcld.event.common.*;
import com.reign.gcld.common.log.*;
import java.util.concurrent.*;
import java.util.*;
import com.reign.gcld.common.*;

public class EventUtil
{
    private static final Logger log;
    private static ConcurrentMap<Integer, Event> eventMap;
    private static ConcurrentMap<Integer, Event> copyEventMap;
    
    static {
        log = CommonLog.getLog(EventUtil.class);
        EventUtil.eventMap = new ConcurrentHashMap<Integer, Event>(5);
        EventUtil.copyEventMap = new ConcurrentHashMap<Integer, Event>(5);
    }
    
    public static void addEvent(final int eventId, final Date startTime, final Date endTime, final String paramInfo) {
        try {
            EventUtil.eventMap.put(eventId, EventFactory.getInstance().createEvent(eventId, startTime, endTime, paramInfo));
        }
        catch (Exception e) {
            EventUtil.log.warn("add event failed", e);
        }
    }
    
    public static boolean isEventTime(final int eventId) {
        final Event event = EventUtil.eventMap.get(eventId);
        return event != null && event.isEventTime();
    }
    
    public static void handleOperation(final int playerId, final int type, final int val) {
        if (EventUtil.eventMap.size() > 0) {
            final Set<Map.Entry<Integer, Event>> entrySet = EventUtil.eventMap.entrySet();
            for (final Map.Entry<Integer, Event> entry : entrySet) {
                final Event event = entry.getValue();
                if (event.isEventTime()) {
                    event.handleOperation(type, playerId, val);
                }
            }
        }
    }
    
    public static Set<Integer> getEventIdSet(final int playerId) {
        Set<Integer> set = null;
        if (EventUtil.eventMap.size() > 0) {
            set = new HashSet<Integer>(3);
            final Set<Map.Entry<Integer, Event>> entrySet = EventUtil.eventMap.entrySet();
            for (final Map.Entry<Integer, Event> entry : entrySet) {
                final Event event = entry.getValue();
                if (event.isEventTime()) {
                    set.add(event.getEventId());
                }
            }
        }
        if (set == null) {
            return Collections.emptySet();
        }
        return set;
    }
    
    public static List<Event> getEventList() {
        EventUtil.copyEventMap.putAll((Map<?, ?>)EventUtil.eventMap);
        final List<Event> eventList = new ArrayList<Event>(10);
        final Set<Map.Entry<Integer, Event>> entrySet = EventUtil.copyEventMap.entrySet();
        for (final Map.Entry<Integer, Event> entry : entrySet) {
            final Event event = entry.getValue();
            if (!EventUtil.eventMap.containsKey(entry.getKey())) {
                event.setEventOver();
            }
            eventList.add(event);
        }
        return eventList;
    }
    
    public static void removeEvent(final int eventId) {
        final Event event = EventUtil.eventMap.get(eventId);
        if (event != null && event.getEventId() >= 9) {
            if (event.isEventTime()) {
                event.notifyEventOver();
            }
            Constants.timer.cancel(event.getStartTaskId());
            Constants.timer.cancel(event.getOverTaskId());
        }
        EventUtil.eventMap.remove(eventId);
        EventUtil.copyEventMap.remove(eventId);
    }
    
    public static Event getEvent(final int eventId) {
        return EventUtil.eventMap.get(eventId);
    }
    
    public static long getEventCd(final int eventId) {
        final Event event = EventUtil.eventMap.get(eventId);
        if (event == null) {
            return 0L;
        }
        return event.getEventCD();
    }
}
