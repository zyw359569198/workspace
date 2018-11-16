package com.reign.framework.netty.servlet;

import java.util.concurrent.*;
import java.util.*;

public class GroupImpl implements Group
{
    private ConcurrentMap<String, Session> userMap;
    private String groupId;
    
    @Override
    public ConcurrentMap<String, Session> getUserMap() {
        return this.userMap;
    }
    
    public GroupImpl(final String groupId) {
        this.userMap = new ConcurrentHashMap<String, Session>();
        this.groupId = groupId;
    }
    
    @Override
    public Group createGroup(final String groupId) {
        final Group group = new GroupImpl(groupId);
        return group;
    }
    
    @Override
    public boolean join(final Session session) {
        this.userMap.put(session.getId(), session);
        return true;
    }
    
    @Override
    public boolean leave(final String sessionId) {
        return this.userMap.remove(sessionId) != null;
    }
    
    @Override
    public void clear() {
        this.userMap.clear();
    }
    
    @Override
    public void notify(final String sessionId, final Object content) {
        final Set<Map.Entry<String, Session>> entrySet = this.userMap.entrySet();
        if (sessionId == null) {
            for (final Map.Entry<String, Session> entry : entrySet) {
                entry.getValue().write(content);
            }
        }
        else {
            for (final Map.Entry<String, Session> entry : entrySet) {
                if (!entry.getKey().equals(sessionId)) {
                    entry.getValue().write(content);
                }
            }
        }
    }
    
    @Override
    public void notify(final Object content) {
        final Set<Map.Entry<String, Session>> entrySet = this.userMap.entrySet();
        for (final Map.Entry<String, Session> entry : entrySet) {
            entry.getValue().write(content);
        }
    }
    
    @Override
    public int[] notify(final Object content, final String... sessionIds) {
        final int[] result = new int[sessionIds.length];
        int index = 0;
        for (final String id : sessionIds) {
            if (this.userMap.containsKey(id)) {
                final Session session = this.userMap.get(id);
                session.write(content);
                result[index++] = 1;
            }
        }
        return result;
    }
    
    @Override
    public String getGroupId() {
        return this.groupId;
    }
}
