package com.reign.kfwd.comm;

import java.util.concurrent.atomic.*;
import java.util.concurrent.*;
import java.util.*;
import com.reign.kf.match.common.*;
import com.reign.framework.json.*;
import com.reign.framework.netty.util.*;
import com.reign.framework.netty.servlet.*;

public class KfwdPlayers
{
    public static ConcurrentMap<Integer, KfwdPlayerDto> playerMap;
    public static ConcurrentMap<Integer, Session> playerSessionMap;
    public static ConcurrentMap<String, KfwdPlayerDto> sessionPlayerMap;
    private static Map<String, AtomicInteger> countMap;
    private static Object lock;
    
    static {
        KfwdPlayers.playerMap = new ConcurrentHashMap<Integer, KfwdPlayerDto>();
        KfwdPlayers.playerSessionMap = new ConcurrentHashMap<Integer, Session>();
        KfwdPlayers.sessionPlayerMap = new ConcurrentHashMap<String, KfwdPlayerDto>();
        KfwdPlayers.countMap = new ConcurrentHashMap<String, AtomicInteger>();
        KfwdPlayers.lock = new Object();
    }
    
    public static void addPlayer(final KfwdPlayerDto dto, final Session session) {
        final KfwdPlayerDto temp = KfwdPlayers.playerMap.put(dto.playerId, dto);
        record((temp == null) ? dto : null, Symbol.PLUS);
        KfwdPlayers.playerSessionMap.put(dto.playerId, session);
        KfwdPlayers.sessionPlayerMap.put(session.getId(), dto);
    }
    
    public static KfwdPlayerDto getPlayer(final int playerId) {
        return KfwdPlayers.playerMap.get(playerId);
    }
    
    public static void removePlayer(final Integer playerId) {
        final KfwdPlayerDto dto = KfwdPlayers.playerMap.remove(playerId);
        record(dto, Symbol.MINUES);
        final Session session = KfwdPlayers.playerSessionMap.remove(playerId);
        if (session != null) {
            KfwdPlayers.sessionPlayerMap.remove(session.getId());
        }
    }
    
    public static void sessionInvalidate(final String sessionId) {
        final KfwdPlayerDto dto = KfwdPlayers.sessionPlayerMap.remove(sessionId);
        if (dto != null) {
            KfwdPlayers.playerMap.remove(dto.playerId);
            KfwdPlayers.playerSessionMap.remove(dto.playerId);
        }
    }
    
    public static KfwdPlayerDto getSession(final String sessionId) {
        return KfwdPlayers.sessionPlayerMap.get(sessionId);
    }
    
    public static Session getSession(final Integer playerId) {
        return KfwdPlayers.playerSessionMap.get(playerId);
    }
    
    public static Collection<KfwdPlayerDto> getAllPlayer() {
        return KfwdPlayers.playerMap.values();
    }
    
    public static boolean isValidate(final KfwdPlayerDto dto) {
        if (KfwdPlayers.playerMap.putIfAbsent(dto.playerId, dto) == null) {
            record(dto, Symbol.PLUS);
            return true;
        }
        final KfwdPlayerDto temp = KfwdPlayers.playerMap.get(dto.playerId);
        return dto.loginTime >= temp.loginTime;
    }
    
    public static void push(final int playerId, final PushCommand command, final byte[] body) {
        final Session session = getSession(Integer.valueOf(playerId));
        if (session != null) {
            final byte[] bytes = JsonBuilder.getJson(JsonBuilder.getJson(State.PUSH, command.getModule(), body));
            session.write(WrapperUtil.wrapper(command.getCommand(), 0, bytes));
        }
    }
    
    public static void push(final int playerId, final String command, final Map<String, byte[]> map) {
        final Session session = getSession(Integer.valueOf(playerId));
        if (session != null) {
            final byte[] bytes = JsonBuilder.getJson(JsonBuilder.getJson(State.PUSH, (Map)map));
            session.write(WrapperUtil.wrapper(command, 0, bytes));
        }
    }
    
    private static void record(final KfwdPlayerDto player, final Symbol symbol) {
    }
    
    public static void clearSession(final Request request) {
        request.getSession().markDiscard();
    }
    
    public enum Symbol
    {
        PLUS("PLUS", 0), 
        MINUES("MINUES", 1);
        
        private Symbol(final String s, final int n) {
        }
    }
}
