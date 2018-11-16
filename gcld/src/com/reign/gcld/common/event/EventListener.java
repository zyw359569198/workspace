package com.reign.gcld.common.event;

import java.util.concurrent.*;
import java.util.*;
import com.reign.gcld.common.*;
import com.reign.framework.json.*;
import com.reign.framework.netty.util.*;
import com.reign.framework.netty.servlet.*;

public class EventListener
{
    public static ConcurrentMap<Integer, List<Event>> pipe;
    
    static {
        EventListener.pipe = new ConcurrentHashMap<Integer, List<Event>>();
    }
    
    public static void init(final int playerId) {
        List<Event> eventList = EventListener.pipe.get(playerId);
        if (eventList == null) {
            eventList = Collections.synchronizedList(new ArrayList<Event>());
            final List<Event> temp = EventListener.pipe.putIfAbsent(playerId, eventList);
            eventList = ((temp == null) ? eventList : temp);
        }
    }
    
    public static void fireEvent(final Event event) {
        fireEvent(event.getSendPlayerId(), event);
    }
    
    public static void fireEvent(final int playerId, final Event event) {
        final List<Event> eventList = EventListener.pipe.get(playerId);
        if (eventList != null) {
            eventList.add(event);
        }
    }
    
    public static Map<Integer, Event> getEvent(final int playerId) {
        final List<Event> eventList = EventListener.pipe.get(playerId);
        if (eventList != null && eventList.size() > 0) {
            synchronized (eventList) {
                final Map<Integer, Event> resultMap = new HashMap<Integer, Event>();
                int size = eventList.size();
                while (size > 0) {
                    final Event event = eventList.remove(0);
                    --size;
                    switch (event.getEventType()) {
                        case 2: {
                            if (event.getDelayNum() == 0) {
                                resultMap.put(event.getEventId(), event);
                                continue;
                            }
                            event.setDelayNum(event.getDelayNum() - 1);
                            eventList.add(event);
                            continue;
                        }
                        case 3: {
                            if (event.getTimestamp() <= System.currentTimeMillis()) {
                                resultMap.put(event.getEventId(), event);
                                continue;
                            }
                            eventList.add(event);
                            continue;
                        }
                        default: {
                            continue;
                        }
                        case 1: {
                            resultMap.put(event.getEventId(), event);
                            continue;
                        }
                    }
                }
                // monitorexit(eventList)
                return resultMap;
            }
        }
        return new HashMap<Integer, Event>();
    }
    
    public static void dealEvent(final Event event, final PushCommand command, final byte[] body) {
        final int[] recvPlayerIds = event.getRecvPlayerIds();
        int[] array;
        for (int length = (array = recvPlayerIds).length, i = 0; i < length; ++i) {
            final int recvId = array[i];
            final Session session = Players.getSession(Integer.valueOf(recvId));
            if (session != null) {
                final byte[] bytes = JsonBuilder.getJson(JsonBuilder.getJson(State.PUSH, command.getModule(), body));
                session.write(WrapperUtil.wrapper(command.getCommand(), 0, bytes));
            }
        }
    }
}
