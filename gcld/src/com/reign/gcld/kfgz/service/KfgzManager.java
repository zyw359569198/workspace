package com.reign.gcld.kfgz.service;

import com.reign.kfgz.dto.response.*;
import java.util.concurrent.*;
import java.util.*;

public class KfgzManager
{
    private static Map<Integer, KfgzSignResult> playerSignMap1;
    private static Map<Integer, KfgzSignResult> playerSignMap2;
    private static Map<Integer, KfgzSignResult> playerSignMap3;
    
    static {
        KfgzManager.playerSignMap1 = new ConcurrentHashMap<Integer, KfgzSignResult>();
        KfgzManager.playerSignMap2 = new ConcurrentHashMap<Integer, KfgzSignResult>();
        KfgzManager.playerSignMap3 = new ConcurrentHashMap<Integer, KfgzSignResult>();
    }
    
    public static void clear() {
        KfgzManager.playerSignMap1.clear();
        KfgzManager.playerSignMap2.clear();
        KfgzManager.playerSignMap3.clear();
    }
    
    public static KfgzSignResult getKfgzSignResultByCid(final int cId) {
        for (final KfgzSignResult k : KfgzManager.playerSignMap1.values()) {
            if (k.getCompetitor() == cId) {
                return k;
            }
        }
        for (final KfgzSignResult k : KfgzManager.playerSignMap2.values()) {
            if (k.getCompetitor() == cId) {
                return k;
            }
        }
        for (final KfgzSignResult k : KfgzManager.playerSignMap3.values()) {
            if (k.getCompetitor() == cId) {
                return k;
            }
        }
        return null;
    }
    
    public static Map<Integer, KfgzSignResult> getSignMap(final int nation) {
        switch (nation) {
            case 1: {
                return KfgzManager.playerSignMap1;
            }
            case 2: {
                return KfgzManager.playerSignMap2;
            }
            case 3: {
                return KfgzManager.playerSignMap3;
            }
            default: {
                return null;
            }
        }
    }
    
    public static void putKfgzPLayerInfo(final KfgzSignResult playerInfo, final int nation) {
        switch (nation) {
            case 1: {
                KfgzManager.playerSignMap1.put(playerInfo.getPlayerId(), playerInfo);
                break;
            }
            case 2: {
                KfgzManager.playerSignMap2.put(playerInfo.getPlayerId(), playerInfo);
                break;
            }
            case 3: {
                KfgzManager.playerSignMap3.put(playerInfo.getPlayerId(), playerInfo);
                break;
            }
        }
    }
    
    public static KfgzSignResult getKfgzPlayerInfo(final int playerId, final int nation) {
        switch (nation) {
            case 1: {
                return KfgzManager.playerSignMap1.get(playerId);
            }
            case 2: {
                return KfgzManager.playerSignMap2.get(playerId);
            }
            case 3: {
                return KfgzManager.playerSignMap3.get(playerId);
            }
            default: {
                return null;
            }
        }
    }
}
