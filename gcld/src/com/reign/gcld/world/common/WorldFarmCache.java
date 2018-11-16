package com.reign.gcld.world.common;

import java.util.concurrent.*;
import com.reign.gcld.battle.common.*;
import java.util.*;

public class WorldFarmCache
{
    private static WorldFarmCache cache;
    public static Map<Integer, Integer> forceCityIdMap;
    private Map<Integer, Long> cdMap;
    private Map<String, Long> buffMap;
    public static final int FARM_CITY_ID_WEI = 254;
    public static final int FARM_CITY_ID_SHU = 253;
    public static final int FARM_CITY_ID_WU = 206;
    public static int FARMING_GENERAL_NUMBER;
    public static FarmingInfo[][] infos;
    public static Object[] objects;
    
    static {
        WorldFarmCache.cache = null;
        (WorldFarmCache.forceCityIdMap = new HashMap<Integer, Integer>()).put(1, 254);
        WorldFarmCache.forceCityIdMap.put(2, 253);
        WorldFarmCache.forceCityIdMap.put(3, 206);
        WorldFarmCache.FARMING_GENERAL_NUMBER = 20;
        WorldFarmCache.infos = new FarmingInfo[3][WorldFarmCache.FARMING_GENERAL_NUMBER];
        WorldFarmCache.objects = new Object[] { new Object(), new Object(), new Object() };
    }
    
    public WorldFarmCache() {
        this.cdMap = new ConcurrentHashMap<Integer, Long>();
        this.buffMap = new ConcurrentHashMap<String, Long>();
    }
    
    public static WorldFarmCache getInstatnce() {
        if (WorldFarmCache.cache == null) {
            WorldFarmCache.cache = new WorldFarmCache();
        }
        return WorldFarmCache.cache;
    }
    
    public long getCdByPlayerId(final int playerId) {
        return (this.cdMap.get(playerId) == null) ? System.currentTimeMillis() : this.cdMap.get(playerId);
    }
    
    public long getBuffCdByPlayerId(final int playerId, final int generalId) {
        final String key = String.valueOf(playerId) + "-" + generalId;
        return (this.buffMap.get(key) == null) ? System.currentTimeMillis() : this.buffMap.get(key);
    }
    
    public void updatePlayerCd(final int playerId, final long expireTime) {
        this.cdMap.put(playerId, expireTime);
    }
    
    public void updatePlayerBuffCd(final int playerId, final int generalId, final long expireTime) {
        final String key = String.valueOf(playerId) + "-" + generalId;
        this.buffMap.put(key, expireTime);
    }
    
    public void addFarmingInfoByForceId(final int forceId, final FarmingInfo toAdd) {
        try {
            if (forceId > 3 || forceId < 0) {
                return;
            }
            final Object object = WorldFarmCache.objects[forceId - 1];
            synchronized (object) {
                int index = -1;
                for (int i = 0; i < WorldFarmCache.FARMING_GENERAL_NUMBER; ++i) {
                    final FarmingInfo info = WorldFarmCache.infos[forceId - 1][i];
                    if (info == null) {
                        index = ((index < 0) ? i : index);
                    }
                    else {
                        final Date date = info.endTimeDate;
                        if (date.before(new Date())) {
                            index = ((index < 0) ? i : index);
                            WorldFarmCache.infos[forceId - 1][i] = null;
                        }
                        else if (toAdd.playerId == info.playerId && toAdd.generalId == info.generalId) {
                            index = ((index < 0) ? i : index);
                            WorldFarmCache.infos[forceId - 1][i] = null;
                        }
                    }
                }
                if (index >= 0 && index < WorldFarmCache.FARMING_GENERAL_NUMBER) {
                    WorldFarmCache.infos[forceId - 1][index] = toAdd;
                }
            }
            // monitorexit(object)
        }
        catch (Exception e) {
            final ErrorSceneLog errorSceneLog = ErrorSceneLog.getInstance();
            errorSceneLog.error(this, e);
        }
    }
    
    public void addFarmingInfoByForceId(final int forceId, final List<FarmingInfo> toAdd) {
        try {
            if (forceId > 3 || forceId < 0) {
                return;
            }
            final Object object = WorldFarmCache.objects[forceId - 1];
            synchronized (object) {
                for (final FarmingInfo f : toAdd) {
                    int index = -1;
                    for (int i = 0; i < WorldFarmCache.FARMING_GENERAL_NUMBER; ++i) {
                        final FarmingInfo info = WorldFarmCache.infos[forceId - 1][i];
                        if (info == null) {
                            index = ((index < 0) ? i : index);
                        }
                        else {
                            final Date date = info.endTimeDate;
                            if (date.before(new Date())) {
                                index = ((index < 0) ? i : index);
                                WorldFarmCache.infos[forceId - 1][i] = null;
                            }
                            else if (f.playerId == info.playerId && f.generalId == info.generalId) {
                                index = ((index < 0) ? i : index);
                                WorldFarmCache.infos[forceId - 1][i] = null;
                            }
                        }
                    }
                    if (index >= 0 && index < WorldFarmCache.FARMING_GENERAL_NUMBER) {
                        WorldFarmCache.infos[forceId - 1][index] = f;
                    }
                }
            }
            // monitorexit(object)
        }
        catch (Exception e) {
            final ErrorSceneLog errorSceneLog = ErrorSceneLog.getInstance();
            errorSceneLog.error(this, e);
        }
    }
    
    public List<FarmingInfo> getRandNumberInfos(final int forceId, final int playerId) {
        if (forceId > 3 || forceId < 1) {
            return null;
        }
        final List<FarmingInfo> list = new ArrayList<FarmingInfo>();
        for (int i = 0; i < WorldFarmCache.FARMING_GENERAL_NUMBER; ++i) {
            final FarmingInfo info = WorldFarmCache.infos[forceId - 1][i];
            if (info != null) {
                if (playerId != info.playerId) {
                    list.add(info);
                }
            }
        }
        return list;
    }
}
