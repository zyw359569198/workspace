package com.reign.kfgz.team;

import java.util.concurrent.*;

public class KfgzTeamManager
{
    private static ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, KfTeam>> teamMap;
    
    static {
        KfgzTeamManager.teamMap = new ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, KfTeam>>();
    }
    
    public static void createNewTeam(final KfTeam kfTeam) {
        final int gzId = kfTeam.getGzId();
        final int teamId = kfTeam.getTeamId();
        if (KfgzTeamManager.teamMap.get(gzId) == null) {
            KfgzTeamManager.teamMap.putIfAbsent(gzId, new ConcurrentHashMap<Integer, KfTeam>());
        }
        KfgzTeamManager.teamMap.get(gzId).put(teamId, kfTeam);
    }
    
    public static KfTeam getKfTeam(final int teamId, final int gzId) {
        final ConcurrentHashMap<Integer, KfTeam> map = KfgzTeamManager.teamMap.get(gzId);
        if (map != null) {
            return map.get(teamId);
        }
        return null;
    }
    
    public static void moveAllGzTeam(final int gzId) {
        KfgzTeamManager.teamMap.remove(gzId);
    }
    
    public static void moveKTeam(final int teamId, final int gzId) {
        final ConcurrentHashMap<Integer, KfTeam> map = KfgzTeamManager.teamMap.get(gzId);
        if (map != null) {
            map.remove(teamId);
        }
    }
}
