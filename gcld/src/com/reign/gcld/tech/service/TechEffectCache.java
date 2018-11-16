package com.reign.gcld.tech.service;

import org.springframework.stereotype.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.tech.dao.*;
import com.reign.gcld.sdata.cache.*;
import java.util.concurrent.*;
import com.reign.gcld.tech.domain.*;
import com.reign.gcld.sdata.domain.*;
import java.util.*;

@Component("techEffectCache")
public class TechEffectCache
{
    @Autowired
    private ITechService techService;
    @Autowired
    private IPlayerTechDao playerTechDao;
    @Autowired
    private TechCache techCache;
    private static ConcurrentMap<Integer, Map<Integer, Integer>> cacheMap;
    private static ConcurrentMap<Integer, Map<Integer, Integer>> cacheMap2;
    private static ConcurrentMap<Integer, Map<Integer, Double>> cacheMap3;
    
    static {
        TechEffectCache.cacheMap = new ConcurrentHashMap<Integer, Map<Integer, Integer>>();
        TechEffectCache.cacheMap2 = new ConcurrentHashMap<Integer, Map<Integer, Integer>>();
        TechEffectCache.cacheMap3 = new ConcurrentHashMap<Integer, Map<Integer, Double>>();
    }
    
    private void initTechCache(final int playerId) {
        final Map<Integer, Integer> map = new HashMap<Integer, Integer>();
        final List<PlayerTech> ptList = this.playerTechDao.getListByPlayerIdAndStatus(playerId, 5);
        for (final PlayerTech pt : ptList) {
            if (this.techCache.get((Object)pt.getTechId()) == null) {
                continue;
            }
            if (map.containsKey(pt.getKeyId())) {
                map.put(pt.getKeyId(), map.get(pt.getKeyId()) + ((Tech)this.techCache.get((Object)pt.getTechId())).getPar1());
            }
            else {
                map.put(pt.getKeyId(), ((Tech)this.techCache.get((Object)pt.getTechId())).getPar1());
            }
        }
        TechEffectCache.cacheMap.put(playerId, map);
    }
    
    private void initTechCache2(final int playerId) {
        final Map<Integer, Integer> map = new HashMap<Integer, Integer>();
        final List<PlayerTech> ptList = this.playerTechDao.getListByPlayerIdAndStatus(playerId, 5);
        for (final PlayerTech pt : ptList) {
            if (this.techCache.get((Object)pt.getTechId()) == null) {
                continue;
            }
            if (map.containsKey(pt.getKeyId())) {
                map.put(pt.getKeyId(), map.get(pt.getKeyId()) + ((Tech)this.techCache.get((Object)pt.getTechId())).getPar2());
            }
            else {
                map.put(pt.getKeyId(), ((Tech)this.techCache.get((Object)pt.getTechId())).getPar2());
            }
        }
        TechEffectCache.cacheMap2.put(playerId, map);
    }
    
    private void initTechCache3(final int playerId) {
        final Map<Integer, Double> map = new HashMap<Integer, Double>();
        final List<PlayerTech> ptList = this.playerTechDao.getListByPlayerIdAndStatus(playerId, 5);
        for (final PlayerTech pt : ptList) {
            if (this.techCache.get((Object)pt.getTechId()) == null) {
                continue;
            }
            if (map.containsKey(pt.getKeyId())) {
                map.put(pt.getKeyId(), map.get(pt.getKeyId()) + ((Tech)this.techCache.get((Object)pt.getTechId())).getPar3());
            }
            else {
                map.put(pt.getKeyId(), ((Tech)this.techCache.get((Object)pt.getTechId())).getPar3());
            }
        }
        TechEffectCache.cacheMap3.put(playerId, map);
    }
    
    public int getTechEffect(final int playerId, final int techKey) {
        Integer temp = 0;
        Map<Integer, Integer> map = null;
        if (!TechEffectCache.cacheMap.containsKey(playerId) || (map = TechEffectCache.cacheMap.get(playerId)) == null) {
            this.initTechCache(playerId);
            return ((map = TechEffectCache.cacheMap.get(playerId)) == null) ? 0 : (((temp = map.get(techKey)) == null) ? 0 : temp);
        }
        if ((temp = map.get(techKey)) != null) {
            return temp;
        }
        map.put(techKey, 0);
        return 0;
    }
    
    public int getTechEffect2(final int playerId, final int techKey) {
        Integer temp2 = 0;
        Map<Integer, Integer> map2 = null;
        if (!TechEffectCache.cacheMap2.containsKey(playerId) || (map2 = TechEffectCache.cacheMap2.get(playerId)) == null) {
            this.initTechCache2(playerId);
            return ((map2 = TechEffectCache.cacheMap2.get(playerId)) == null) ? 0 : (((temp2 = map2.get(techKey)) == null) ? 0 : temp2);
        }
        if ((temp2 = map2.get(techKey)) != null) {
            return temp2;
        }
        map2.put(techKey, 0);
        return 0;
    }
    
    public double getTechEffect3(final int playerId, final int techKey) {
        Double temp3 = 0.0;
        Map<Integer, Double> map3 = null;
        if (!TechEffectCache.cacheMap3.containsKey(playerId) || (map3 = TechEffectCache.cacheMap3.get(playerId)) == null) {
            this.initTechCache3(playerId);
            return ((map3 = TechEffectCache.cacheMap3.get(playerId)) == null) ? 0.0 : (((temp3 = map3.get(techKey)) == null) ? 0.0 : temp3);
        }
        if ((temp3 = map3.get(techKey)) != null) {
            return temp3;
        }
        map3.put(techKey, 0.0);
        return 0.0;
    }
    
    public void refreshTechEffect(final int playerId, final int techKey) {
        if (TechEffectCache.cacheMap.containsKey(playerId)) {
            TechEffectCache.cacheMap.get(playerId).put(techKey, this.techService.getTechEffect(playerId, techKey));
        }
        if (TechEffectCache.cacheMap2.containsKey(playerId)) {
            TechEffectCache.cacheMap2.get(playerId).put(techKey, this.techService.getTechEffect2(playerId, techKey));
        }
        if (TechEffectCache.cacheMap3.containsKey(playerId)) {
            TechEffectCache.cacheMap3.get(playerId).put(techKey, this.techService.getTechEffect3(playerId, techKey));
        }
    }
    
    public void clearTechEffect(final int playerId) {
        final Map<Integer, Integer> map = TechEffectCache.cacheMap.get(playerId);
        final Map<Integer, Integer> map2 = TechEffectCache.cacheMap2.get(playerId);
        final Map<Integer, Double> map3 = TechEffectCache.cacheMap3.get(playerId);
        if (map != null) {
            map.clear();
            TechEffectCache.cacheMap.remove(playerId);
        }
        if (map2 != null) {
            map2.clear();
            TechEffectCache.cacheMap2.remove(playerId);
        }
        if (map3 != null) {
            map3.clear();
            TechEffectCache.cacheMap3.remove(playerId);
        }
    }
}
