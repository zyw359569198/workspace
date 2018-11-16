package com.reign.gcld.common;

import com.reign.gcld.player.dto.*;
import java.util.concurrent.atomic.*;
import com.reign.gcld.player.common.*;
import com.reign.gcld.common.log.*;
import java.util.concurrent.*;
import com.reign.gcld.chat.service.*;
import com.reign.gcld.common.util.*;
import com.reign.framework.json.*;
import com.reign.framework.netty.util.*;
import com.reign.gcld.log.*;
import java.util.*;
import com.reign.framework.netty.servlet.*;

public class Players
{
    private static final Logger log;
    public static ConcurrentMap<Integer, PlayerDto> playerMap;
    public static ConcurrentMap<Integer, Session> playerSessionMap;
    public static ConcurrentMap<String, PlayerDto> sessionPlayerMap;
    private static Map<String, AtomicInteger> countMap;
    private static Map<String, Map<PlatForm, AtomicInteger>> countPlatFormMap;
    private static Object lock;
    
    static {
        log = CommonLog.getLog(Players.class);
        Players.playerMap = new ConcurrentHashMap<Integer, PlayerDto>();
        Players.playerSessionMap = new ConcurrentHashMap<Integer, Session>();
        Players.sessionPlayerMap = new ConcurrentHashMap<String, PlayerDto>();
        Players.countMap = new ConcurrentHashMap<String, AtomicInteger>();
        Players.countPlatFormMap = new ConcurrentHashMap<String, Map<PlatForm, AtomicInteger>>();
        Players.lock = new Object();
        GroupManager.getInstance().createGroup(ChatType.GLOBAL.toString());
        for (int i = 1; i <= 3; ++i) {
            GroupManager.getInstance().createGroup(String.valueOf(ChatType.COUNTRY.toString()) + i);
            GroupManager.getInstance().createGroup(String.valueOf(ChatType.COUNTRY.toString()) + i + "_" + 1);
            GroupManager.getInstance().createGroup(String.valueOf(ChatType.COUNTRY.toString()) + i + "_" + 2);
        }
        for (int i = 0; i < 280; ++i) {
            GroupManager.getInstance().createGroup(String.valueOf(ChatType.WORLD.toString()) + i);
        }
        GroupManager.getInstance().createGroup(ChatType.LEGION.toString());
        GroupManager.getInstance().createGroup(ChatType.AREA.toString());
        GroupManager.getInstance().createGroup(ChatType.BATTLE.toString());
        GroupManager.getInstance().createGroup(ChatType.WORLD.toString());
        GroupManager.getInstance().createGroup(ChatType.WORLD_1.toString());
        GroupManager.getInstance().createGroup(ChatType.WORLD_2.toString());
        GroupManager.getInstance().createGroup(ChatType.WORLD_3.toString());
        GroupManager.getInstance().createGroup(ChatType.WORLD_OPENED_1.toString());
        GroupManager.getInstance().createGroup(ChatType.WORLD_OPENED_2.toString());
        GroupManager.getInstance().createGroup(ChatType.WORLD_OPENED_3.toString());
        for (final String yx : YxUtil.getYxMap().keySet()) {
            Players.countMap.put(yx, new AtomicInteger(0));
            final Map<PlatForm, AtomicInteger> map = new ConcurrentHashMap<PlatForm, AtomicInteger>();
            map.put(PlatForm.PC, new AtomicInteger(0));
            map.put(PlatForm.MOBILE_IPHONE, new AtomicInteger(0));
            map.put(PlatForm.MOBILE_ANDROID, new AtomicInteger(0));
            Players.countPlatFormMap.put(yx, map);
            GroupManager.getInstance().createGroup(String.valueOf(ChatType.YX.toString()) + yx);
        }
    }
    
    public static void addPlayer(final PlayerDto dto, final Session session) {
        final PlayerDto temp = Players.playerMap.put(dto.playerId, dto);
        if (temp != null) {
            record(temp, Symbol.MINUES);
        }
        record(dto, Symbol.PLUS);
        Players.playerSessionMap.put(dto.playerId, session);
        Players.sessionPlayerMap.put(session.getId(), dto);
    }
    
    public static PlayerDto getPlayer(final int playerId) {
        return Players.playerMap.get(playerId);
    }
    
    public static void removePlayer(final Integer playerId) {
        final PlayerDto dto = Players.playerMap.remove(playerId);
        Players.log.info("- " + Players.playerMap.size());
        record(dto, Symbol.MINUES);
        final Session session = Players.playerSessionMap.remove(playerId);
        if (session != null) {
            Players.sessionPlayerMap.remove(session.getId());
        }
    }
    
    public static void sessionInvalidate(final String sessionId) {
        final PlayerDto dto = Players.sessionPlayerMap.remove(sessionId);
        if (dto != null) {
            Players.playerMap.remove(dto.playerId);
            Players.playerSessionMap.remove(dto.playerId);
        }
    }
    
    public static PlayerDto getSession(final String sessionId) {
        return Players.sessionPlayerMap.get(sessionId);
    }
    
    public static Session getSession(final Integer playerId) {
        return Players.playerSessionMap.get(playerId);
    }
    
    public static Collection<PlayerDto> getAllPlayer() {
        return Players.playerMap.values();
    }
    
    public static List<PlayerDto> getAllPlayerByForceId(final int forceId) {
        final List<PlayerDto> list = new ArrayList<PlayerDto>();
        for (final PlayerDto dto : Players.playerMap.values()) {
            if (forceId == dto.forceId) {
                list.add(dto);
            }
        }
        return list;
    }
    
    public static List<PlayerDto> getAllPlayerByForceIdAndYx(final int forceId, final String yx) {
        final List<PlayerDto> list = new ArrayList<PlayerDto>();
        for (final PlayerDto dto : Players.playerMap.values()) {
            if (forceId == dto.forceId && dto.yx.equals(yx)) {
                list.add(dto);
            }
        }
        return list;
    }
    
    public static boolean isValidate(final PlayerDto dto) {
        if (Players.playerMap.putIfAbsent(dto.playerId, dto) == null) {
            Players.log.info("+ " + Players.playerMap.size());
            record(dto, Symbol.PLUS);
            return true;
        }
        final PlayerDto temp = Players.playerMap.get(dto.playerId);
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
    
    public static void pushToALL(final PushCommand command, final byte[] body) {
        for (final Map.Entry<Integer, PlayerDto> entry : Players.playerMap.entrySet()) {
            final PlayerDto dto = entry.getValue();
            push(dto.playerId, command, body);
        }
    }
    
    public static void printLog(final Logger log) {
        final Set<Map.Entry<String, AtomicInteger>> entrySet = Players.countMap.entrySet();
        for (final Map.Entry<String, AtomicInteger> entry : entrySet) {
            final Map<PlatForm, AtomicInteger> map = Players.countPlatFormMap.get(entry.getKey());
            log.info(LogUtil.formatOnlineLog(entry.getKey(), entry.getValue().get(), map.get(PlatForm.PC).get(), map.get(PlatForm.MOBILE_IPHONE).get(), map.get(PlatForm.MOBILE_ANDROID).get()));
        }
    }
    
    private static void record(final PlayerDto player, final Symbol symbol) {
        if (player == null || player.yx == null) {
            return;
        }
        AtomicInteger count = Players.countMap.get(player.yx);
        if (count == null) {
            synchronized (Players.lock) {
                count = Players.countMap.get(player.yx);
                if (count == null) {
                    count = new AtomicInteger(0);
                    Players.countMap.put(player.yx, count);
                }
            }
            // monitorexit(Players.lock)
        }
        if (Symbol.PLUS.equals(symbol)) {
            count.incrementAndGet();
            Players.countPlatFormMap.get(player.yx).get(LogUtil.getPlatForm(null, null, player.platForm)).incrementAndGet();
            Players.log.info("count+ " + count.get());
        }
        else {
            count.decrementAndGet();
            Players.countPlatFormMap.get(player.yx).get(LogUtil.getPlatForm(null, null, player.platForm)).decrementAndGet();
            Players.log.info("count- " + count.get());
        }
    }
    
    public static void clearSession(final Request request) {
        request.getSession().markDiscard();
    }
    
    public static void pushNotToThisGroup(final Group group, final Group countryGroup, final PushCommand command, final byte[] body) {
        if (countryGroup == null) {
            return;
        }
        Map<String, Session> excludeMap = null;
        if (group != null) {
            excludeMap = ((GroupImpl)group).getUserMap();
        }
        final GroupImpl impl = (GroupImpl)countryGroup;
        final Map<String, Session> map = impl.getUserMap();
        final byte[] bytes = JsonBuilder.getJson(JsonBuilder.getJson(State.PUSH, command.getModule(), body));
        for (final String key : map.keySet()) {
            final Session session = map.get(key);
            if (session == null) {
                continue;
            }
            if (excludeMap != null && excludeMap.containsKey(key)) {
                continue;
            }
            session.write(WrapperUtil.wrapper(command.getCommand(), 0, bytes));
        }
    }
    
    public static int getOnlinePlayersNumber(final String yx) {
        final AtomicInteger atom = Players.countMap.get(yx);
        return (atom == null) ? 0 : atom.get();
    }
    
    public static List<Integer> getAllPlayerIds() {
        return (Players.playerMap == null) ? null : new ArrayList<Integer>(Players.playerMap.keySet());
    }
    
    public enum Symbol
    {
        PLUS("PLUS", 0), 
        MINUES("MINUES", 1);
        
        private Symbol(final String s, final int n) {
        }
    }
}
