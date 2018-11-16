package com.reign.kf.match.common.web.session;

import java.util.concurrent.*;
import com.reign.framework.netty.servlet.*;
import java.util.*;
import com.reign.kf.match.common.*;
import com.reign.framework.json.*;
import com.reign.framework.netty.util.*;

public class GameServers
{
    public static ConcurrentMap<String, GameServerIds> map;
    public static ConcurrentMap<String, GameServerIds> sessionMap;
    
    static {
        GameServers.map = new ConcurrentHashMap<String, GameServerIds>();
        GameServers.sessionMap = new ConcurrentHashMap<String, GameServerIds>();
    }
    
    public static void addGameServer(final GameServerDto dto, final Session session) {
        GameServerIds gameServerIds = GameServers.map.get(dto.getServerName());
        if (gameServerIds == null) {
            gameServerIds = new GameServerIds();
            GameServers.map.put(dto.getServerName(), gameServerIds);
        }
        gameServerIds.addGameServer(dto, session);
        GameServers.sessionMap.put(session.getId(), gameServerIds);
    }
    
    public static GameServerDto getGameServer(final String serverName, final int serverId) {
        final GameServerIds gameServerIds = GameServers.map.get(serverName);
        if (gameServerIds == null) {
            return null;
        }
        return gameServerIds.getGameServer(serverId);
    }
    
    public static void removeGameServer(final String serverName, final int serverId) {
        final GameServerIds gameServerIds = GameServers.map.get(serverName);
        if (gameServerIds != null) {
            final Session session = gameServerIds.removeGameServer(serverId);
            if (session != null) {
                GameServers.sessionMap.remove(session.getId());
            }
        }
    }
    
    public static void sessionInvalidate(final String sessionId) {
        final GameServerIds gameServerIds = GameServers.sessionMap.get(sessionId);
        if (gameServerIds != null) {
            gameServerIds.sessionInvalidate(sessionId);
            GameServers.sessionMap.remove(sessionId);
        }
    }
    
    public static Session getSession(final String serverName, final int serverId) {
        final GameServerIds gameServerIds = GameServers.map.get(serverName);
        if (gameServerIds != null) {
            return gameServerIds.getSession(serverId);
        }
        return null;
    }
    
    public static Collection<GameServerDto> getAllGameServers() {
        final List<GameServerDto> rtn = new ArrayList<GameServerDto>();
        for (final GameServerIds gameServerIds : GameServers.map.values()) {
            rtn.addAll(gameServerIds.getAllGameServers());
        }
        return rtn;
    }
    
    public static boolean isValidate(final GameServerDto dto) {
        final GameServerIds gameServerIds = GameServers.map.get(dto.getServerName());
        return gameServerIds == null || gameServerIds.isValidate(dto);
    }
    
    public static void push(final String serverName, final int serverId, final PushCommand command, final byte[] body) {
        final Session session = getSession(serverName, serverId);
        if (session != null) {
            final byte[] bytes = JsonBuilder.getJson(JsonBuilder.getJson(State.PUSH, command.getModule(), body));
            session.write(WrapperUtil.wrapper(command.getCommand(), 0, bytes));
        }
    }
}
