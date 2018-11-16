package com.reign.kf.match.common.web.session;

import com.reign.framework.netty.servlet.*;
import java.util.concurrent.*;
import java.util.*;
import com.reign.kf.match.common.*;
import com.reign.framework.json.*;
import com.reign.framework.netty.util.*;

public class GameServerIds
{
    public ConcurrentMap<Integer, GameServerDto> serverIdMap;
    public ConcurrentMap<Integer, Session> serverIdSessionMap;
    public ConcurrentMap<String, GameServerDto> sessionserverIdMap;
    
    public GameServerIds() {
        this.serverIdMap = new ConcurrentHashMap<Integer, GameServerDto>();
        this.serverIdSessionMap = new ConcurrentHashMap<Integer, Session>();
        this.sessionserverIdMap = new ConcurrentHashMap<String, GameServerDto>();
    }
    
    public void addGameServer(final GameServerDto dto, final Session session) {
        this.serverIdMap.put(dto.getServerId(), dto);
        this.serverIdSessionMap.put(dto.getServerId(), session);
        this.sessionserverIdMap.put(session.getId(), dto);
    }
    
    public GameServerDto getGameServer(final int serverId) {
        return this.serverIdMap.get(serverId);
    }
    
    public Session removeGameServer(final int serverId) {
        this.serverIdMap.remove(serverId);
        final Session session = this.serverIdSessionMap.remove(serverId);
        if (session != null) {
            this.sessionserverIdMap.remove(session.getId());
        }
        return session;
    }
    
    public void sessionInvalidate(final String sessionId) {
        final GameServerDto dto = this.sessionserverIdMap.remove(sessionId);
        if (dto != null) {
            this.serverIdMap.remove(dto.getServerId());
            this.serverIdSessionMap.remove(dto.getServerId());
        }
    }
    
    public Session getSession(final int serverId) {
        return this.serverIdSessionMap.get(serverId);
    }
    
    public Collection<GameServerDto> getAllGameServers() {
        return this.serverIdMap.values();
    }
    
    public boolean isValidate(final GameServerDto dto) {
        return this.serverIdMap.putIfAbsent(dto.getServerId(), dto) == null;
    }
    
    public void push(final int serverId, final PushCommand command, final byte[] body) {
        final Session session = this.getSession(serverId);
        if (session != null) {
            final byte[] bytes = JsonBuilder.getJson(JsonBuilder.getJson(State.PUSH, command.getModule(), body));
            session.write(WrapperUtil.wrapper(command.getCommand(), 0, bytes));
        }
    }
}
