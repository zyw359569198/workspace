package com.reign.kf.match.common.web.session;

import java.util.concurrent.*;
import java.util.*;
import com.reign.kf.match.common.*;
import com.reign.framework.json.*;
import com.reign.framework.netty.util.*;
import com.reign.framework.netty.servlet.*;

public class Players
{
    public static ConcurrentMap<Long, PlayerDto> playerMap;
    public static ConcurrentMap<Long, Session> playerSessionMap;
    public static ConcurrentMap<String, PlayerDto> sessionPlayerMap;
    
    static {
        Players.playerMap = new ConcurrentHashMap<Long, PlayerDto>();
        Players.playerSessionMap = new ConcurrentHashMap<Long, Session>();
        Players.sessionPlayerMap = new ConcurrentHashMap<String, PlayerDto>();
    }
    
    public static void addPlayer(final PlayerDto dto, final Session session) {
        if (dto.getUuid() == 0L || (dto.getUuid() & 0x3FFL) == 0x0L) {
            return;
        }
        Players.playerMap.put(dto.getUuid(), dto);
        Players.playerSessionMap.put(dto.getUuid(), session);
        Players.sessionPlayerMap.put(session.getId(), dto);
    }
    
    public static PlayerDto getPlayer(final long uuid) {
        return Players.playerMap.get(uuid);
    }
    
    public static void removePlayer(final long uuid) {
        Players.playerMap.remove(uuid);
        final Session session = Players.playerSessionMap.remove(uuid);
        if (session != null) {
            Players.sessionPlayerMap.remove(session.getId());
        }
    }
    
    public static void sessionInvalidate(final String sessionId) {
        final PlayerDto dto = Players.sessionPlayerMap.remove(sessionId);
        if (dto != null) {
            Players.playerMap.remove(dto.getUuid());
            Players.playerSessionMap.remove(dto.getUuid());
        }
    }
    
    public static Session getSession(final long uuid) {
        return Players.playerSessionMap.get(uuid);
    }
    
    public static Collection<PlayerDto> getAllPlayer() {
        return Players.playerMap.values();
    }
    
    public static Collection<Long> getAllPlayerId() {
        return Players.playerMap.keySet();
    }
    
    public static boolean isValidate(final PlayerDto dto) {
        if (Players.playerMap.putIfAbsent(dto.getUuid(), dto) == null) {
            return true;
        }
        final PlayerDto temp = Players.playerMap.get(dto.getUuid());
        return dto.getLastLoginTime() >= temp.getLastLoginTime();
    }
    
    public static void push(final long uuid, final PushCommand command, final byte[] body) {
        final Session session = getSession(uuid);
        if (session != null) {
            final byte[] bytes = JsonBuilder.getMjcsJson(State.PUSH, command.getCommand(), body);
            session.write(WrapperUtil.wrapper(command.getCommand(), 0, bytes));
        }
    }
    
    public static void push(final Group group, final PushCommand command, final byte[] body) {
        final byte[] bytes = JsonBuilder.getMjcsJson(State.PUSH, command.getCommand(), body);
        group.notify((String)null, WrapperUtil.wrapper(command.getCommand(), 0, bytes));
    }
    
    public static void push(final int excludePlayerId, final Group group, final PushCommand command, final byte[] body) {
        final byte[] bytes = JsonBuilder.getMjcsJson(State.PUSH, command.getCommand(), body);
        final Session session = getSession(excludePlayerId);
        if (session == null) {
            group.notify((String)null, WrapperUtil.wrapper(command.getCommand(), 0, bytes));
        }
        else {
            group.notify(session.getId(), WrapperUtil.wrapper(command.getCommand(), 0, bytes));
        }
    }
}
