package com.reign.gcld.world.service;

import org.springframework.stereotype.*;
import com.reign.gcld.world.dao.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.sdata.cache.*;
import java.util.concurrent.*;
import com.reign.gcld.battle.service.*;
import com.reign.gcld.common.util.*;
import com.reign.gcld.battle.scene.*;
import com.reign.gcld.sdata.domain.*;
import java.util.*;

@Component("cityEffectCache")
public class CityEffectCache
{
    @Autowired
    private ICityDao cityDao;
    @Autowired
    private WorldCitySpecialCache worldCitySpecialCache;
    private static ConcurrentMap<Integer, Map<Integer, Double>> cacheMap;
    private static ConcurrentMap<Integer, Map<Integer, Double>> cacheMap2;
    private static ConcurrentMap<Integer, Set<Integer>> battleCityMap;
    private boolean isInit;
    
    static {
        CityEffectCache.cacheMap = new ConcurrentHashMap<Integer, Map<Integer, Double>>();
        CityEffectCache.cacheMap2 = new ConcurrentHashMap<Integer, Map<Integer, Double>>();
        CityEffectCache.battleCityMap = new ConcurrentHashMap<Integer, Set<Integer>>();
        for (int i = 1; i <= 3; ++i) {
            final Set<Integer> battleCityList = new HashSet<Integer>();
            CityEffectCache.battleCityMap.put(i, battleCityList);
        }
    }
    
    public CityEffectCache() {
        this.isInit = false;
    }
    
    private void initBattleCity() {
        Battle battle = null;
        BaseInfo baseInfo = null;
        int forceId = 0;
        for (final Integer cityId : this.worldCitySpecialCache.getCityIdSet()) {
            battle = NewBattleManager.getInstance().getBattleByDefId(3, cityId);
            if (battle == null) {
                continue;
            }
            baseInfo = battle.getAttBaseInfo();
            if (baseInfo != null) {
                forceId = baseInfo.getForceId();
                if (ForceUtil.isPlayerForce(forceId)) {
                    CityEffectCache.battleCityMap.get(forceId).add(cityId);
                }
            }
            baseInfo = battle.getDefBaseInfo();
            if (baseInfo == null) {
                continue;
            }
            forceId = baseInfo.getForceId();
            if (!ForceUtil.isPlayerForce(forceId)) {
                continue;
            }
            CityEffectCache.battleCityMap.get(forceId).add(cityId);
        }
    }
    
    private void initCityCache(final int forceId) {
        final Map<Integer, Double> map = new HashMap<Integer, Double>();
        final List<Integer> cityIdList = this.cityDao.getCityIdListByForceId(forceId);
        for (final Integer cityId : this.worldCitySpecialCache.getCityIdSet()) {
            if (!cityIdList.contains(cityId)) {
                continue;
            }
            final Integer key = this.worldCitySpecialCache.getKeyDisplayByCityId(cityId);
            if (key == null) {
                continue;
            }
            if (map.containsKey(key)) {
                map.put(key, map.get(key) + ((WorldCitySpecial)this.worldCitySpecialCache.get((Object)this.worldCitySpecialCache.getCityIdCalByKey(key))).getPar1());
            }
            else {
                map.put(key, ((WorldCitySpecial)this.worldCitySpecialCache.get((Object)this.worldCitySpecialCache.getCityIdCalByKey(key))).getPar1());
            }
        }
        CityEffectCache.cacheMap.put(forceId, map);
    }
    
    private void initCityCache2(final int forceId) {
        final Map<Integer, Double> map2 = new HashMap<Integer, Double>();
        final List<Integer> cityIdList = this.cityDao.getCityIdListByForceId(forceId);
        for (final Integer cityId : this.worldCitySpecialCache.getCityIdSet()) {
            if (!cityIdList.contains(cityId)) {
                continue;
            }
            final Integer key = this.worldCitySpecialCache.getKeyDisplayByCityId(cityId);
            if (key == null) {
                continue;
            }
            if (map2.containsKey(key)) {
                map2.put(key, map2.get(key) + ((WorldCitySpecial)this.worldCitySpecialCache.get((Object)this.worldCitySpecialCache.getCityIdCalByKey(key))).getPar2());
            }
            else {
                map2.put(key, ((WorldCitySpecial)this.worldCitySpecialCache.get((Object)this.worldCitySpecialCache.getCityIdCalByKey(key))).getPar2());
            }
        }
        CityEffectCache.cacheMap2.put(forceId, map2);
    }
    
    public double getCityEffect(final int forceId, final int key) {
        Double temp = 0.0;
        Map<Integer, Double> map = null;
        if (!CityEffectCache.cacheMap.containsKey(forceId) || (map = CityEffectCache.cacheMap.get(forceId)) == null) {
            this.initCityCache(forceId);
            return ((map = CityEffectCache.cacheMap.get(forceId)) == null) ? 0.0 : (((temp = map.get(key)) == null) ? 0.0 : temp);
        }
        if ((temp = map.get(key)) != null) {
            return temp;
        }
        map.put(key, 0.0);
        return 0.0;
    }
    
    public double getCityEffect2(final int forceId, final int key) {
        Double temp2 = 0.0;
        Map<Integer, Double> map2 = null;
        if (!CityEffectCache.cacheMap2.containsKey(forceId) || (map2 = CityEffectCache.cacheMap2.get(forceId)) == null) {
            this.initCityCache2(forceId);
            return ((map2 = CityEffectCache.cacheMap2.get(forceId)) == null) ? 0.0 : (((temp2 = map2.get(key)) == null) ? 0.0 : temp2);
        }
        if ((temp2 = map2.get(key)) != null) {
            return temp2;
        }
        map2.put(key, 0.0);
        return 0.0;
    }
    
    public void refreshCityEffect(final int winForceId, final int loseForceId, final int cityId) {
        final Integer key = this.worldCitySpecialCache.getKeyDisplayByCityId(cityId);
        if (key == null) {
            return;
        }
        if (ForceUtil.isPlayerForce(winForceId)) {
            if (CityEffectCache.cacheMap.containsKey(winForceId)) {
                CityEffectCache.cacheMap.get(winForceId).put(key, this.getEffect(winForceId, key));
            }
            if (CityEffectCache.cacheMap2.containsKey(winForceId)) {
                CityEffectCache.cacheMap2.get(winForceId).put(key, this.getEffect2(winForceId, key));
            }
        }
        if (ForceUtil.isPlayerForce(loseForceId)) {
            if (CityEffectCache.cacheMap.containsKey(loseForceId)) {
                CityEffectCache.cacheMap.get(loseForceId).put(key, this.getEffect2(loseForceId, key));
            }
            if (CityEffectCache.cacheMap2.containsKey(loseForceId)) {
                CityEffectCache.cacheMap2.get(loseForceId).put(key, this.getEffect2(loseForceId, key));
            }
        }
    }
    
    private double getEffect(final int forceId, final int key) {
        Integer cityId = this.worldCitySpecialCache.getCityIdDisplayByKey(key);
        if (cityId == null) {
            return 0.0;
        }
        final List<Integer> cityIdList2 = this.cityDao.getCityIdListByForceId(forceId);
        if (cityIdList2 == null || cityIdList2.size() <= 0) {
            return 0.0;
        }
        double result = 0.0;
        WorldCitySpecial wcs = null;
        if (cityIdList2.contains(cityId) && (wcs = (WorldCitySpecial)this.worldCitySpecialCache.get((Object)cityId)) != null) {
            cityId = this.worldCitySpecialCache.getCityIdCalByKey(key);
            if (cityId != null) {
                wcs = (WorldCitySpecial)this.worldCitySpecialCache.get((Object)cityId);
                if (wcs != null) {
                    result += wcs.getPar1();
                }
            }
        }
        return result;
    }
    
    private double getEffect2(final int forceId, final int key) {
        Integer cityId = this.worldCitySpecialCache.getCityIdDisplayByKey(key);
        if (cityId == null) {
            return 0.0;
        }
        final List<Integer> cityIdList2 = this.cityDao.getCityIdListByForceId(forceId);
        if (cityIdList2 == null || cityIdList2.size() <= 0) {
            return 0.0;
        }
        double result = 0.0;
        WorldCitySpecial wcs = null;
        if (cityIdList2.contains(cityId) && (wcs = (WorldCitySpecial)this.worldCitySpecialCache.get((Object)cityId)) != null) {
            cityId = this.worldCitySpecialCache.getCityIdCalByKey(key);
            if (cityId != null) {
                wcs = (WorldCitySpecial)this.worldCitySpecialCache.get((Object)cityId);
                if (wcs != null) {
                    result += wcs.getPar2();
                }
            }
        }
        return result;
    }
    
    public Set<Integer> getBattleCity(final int forceId) {
        if (!this.isInit) {
            this.initBattleCity();
            this.isInit = true;
        }
        return CityEffectCache.battleCityMap.get(forceId);
    }
}
