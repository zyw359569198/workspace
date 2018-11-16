package com.reign.kfgz.control;

import org.springframework.stereotype.*;
import java.util.concurrent.*;
import com.reign.kfgz.comm.*;

@Component
public class KfgzPlayerManager
{
    public static ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, KfPlayerInfo>> playerGzMap;
    private static ConcurrentHashMap<Integer, KfPlayerInfo> playerMap;
    
    static {
        KfgzPlayerManager.playerGzMap = new ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, KfPlayerInfo>>();
        KfgzPlayerManager.playerMap = new ConcurrentHashMap<Integer, KfPlayerInfo>();
    }
    
    public static void addNewPlayer(final KfPlayerInfo pInfo) {
        final int gzId = pInfo.getGzId();
        KfgzPlayerManager.playerGzMap.putIfAbsent(gzId, new ConcurrentHashMap<Integer, KfPlayerInfo>());
        KfgzPlayerManager.playerGzMap.get(gzId).put(pInfo.getCompetitorId(), pInfo);
        KfgzPlayerManager.playerMap.put(pInfo.getCompetitorId(), pInfo);
    }
    
    public static KfPlayerInfo getPlayerByCId(final int competitorId) {
        return KfgzPlayerManager.playerMap.get(competitorId);
    }
    
    public static ConcurrentHashMap<Integer, KfPlayerInfo> getPlayerMapByGz(final int gzId) {
        return KfgzPlayerManager.playerGzMap.get(gzId);
    }
    
    public static void removeByCId(final int competitorId, final int gzId) {
        KfgzPlayerManager.playerMap.remove(competitorId);
        final ConcurrentHashMap<Integer, KfPlayerInfo> map = getPlayerMapByGz(gzId);
        if (map != null) {
            map.remove(competitorId);
        }
    }
    
    public static void clearAll() {
        KfgzPlayerManager.playerGzMap.clear();
        KfgzPlayerManager.playerMap.clear();
    }
}
