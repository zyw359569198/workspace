package com.reign.framework.netty.servlet;

import java.util.concurrent.*;

public class GroupManager
{
    private static final GroupManager instance;
    private ConcurrentMap<String, Group> groupMap;
    
    static {
        instance = new GroupManager();
    }
    
    private GroupManager() {
        this.groupMap = new ConcurrentHashMap<String, Group>();
    }
    
    public static GroupManager getInstance() {
        return GroupManager.instance;
    }
    
    public Group createGroup(final String groupId) {
        if (this.groupMap.containsKey(groupId)) {
            return this.groupMap.get(groupId);
        }
        final Group group = new GroupImpl(groupId);
        final Group temp = this.groupMap.putIfAbsent(groupId, group);
        return (temp != null) ? temp : group;
    }
    
    public void deleteGroup(final String groupId) {
        if (this.groupMap.containsKey(groupId)) {
            this.groupMap.remove(groupId);
        }
    }
    
    public Group getGroup(final String groupId) {
        return this.groupMap.get(groupId);
    }
    
    public void notifyAll(final String groupId, final Object content) {
        final Group group = this.groupMap.get(groupId);
        if (group != null) {
            group.notify(content);
        }
    }
    
    public void notifyAll(final String groupId, final String sessionId, final Object content) {
        final Group group = this.groupMap.get(groupId);
        if (group != null) {
            group.notify(sessionId, content);
        }
    }
    
    public void notify(final String groupId, final Object content, final String... sessionIds) {
        final Group group = this.groupMap.get(groupId);
        if (group != null) {
            group.notify(content, sessionIds);
        }
    }
    
    public void leave(final String sessionId) {
        for (final Group group : this.groupMap.values()) {
            group.leave(sessionId);
        }
    }
}
