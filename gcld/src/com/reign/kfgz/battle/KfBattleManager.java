package com.reign.kfgz.battle;

import java.util.concurrent.*;

public class KfBattleManager
{
    static ConcurrentHashMap<Integer, Integer> playerWatchBattleTeamMap;
    
    static {
        KfBattleManager.playerWatchBattleTeamMap = new ConcurrentHashMap<Integer, Integer>();
    }
    
    public static Integer getPlayerWatchBattleId(final int cId) {
        return KfBattleManager.playerWatchBattleTeamMap.get(cId);
    }
    
    public static void setPlayerWatchBattleId(final int cId, final int tId) {
        KfBattleManager.playerWatchBattleTeamMap.put(cId, tId);
    }
    
    public static void removePlayerWatchBattleId(final int cId) {
        KfBattleManager.playerWatchBattleTeamMap.remove(cId);
    }
}
