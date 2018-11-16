package com.reign.gcld.building.service;

import org.springframework.stereotype.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.building.dao.*;
import com.reign.gcld.tech.service.*;
import com.reign.gcld.player.service.*;
import com.reign.gcld.sdata.cache.*;
import com.reign.gcld.player.dao.*;
import java.util.concurrent.*;
import com.reign.gcld.kfgz.service.*;
import com.reign.gcld.player.domain.*;
import java.util.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.gcld.building.domain.*;
import org.apache.commons.lang.*;
import com.reign.framework.json.*;
import com.reign.gcld.common.*;

@Component("buildingOutputCache")
public class BuildingOutputCache implements IBuildingOutputCache
{
    @Autowired
    private BuildingCache buildingCache;
    @Autowired
    private SerialCache serialCache;
    @Autowired
    private IPlayerResourceAdditionDao playerResourceAdditionDao;
    @Autowired
    private TechEffectCache techEffectCache;
    @Autowired
    private HallsCache hallsCache;
    @Autowired
    private IResourceService resourceService;
    @Autowired
    private CCache cCache;
    @Autowired
    private IBuildingService buildingService;
    @Autowired
    private TroopConscribeSpeedCache troopConscribeSpeedCache;
    @Autowired
    private ChargeitemCache chargeItemCache;
    @Autowired
    private IPlayerOfficeRelativeDao playerOfficeRelativeDao;
    private ConcurrentMap<String, Integer> cacheTotalMap;
    private ConcurrentMap<String, Integer> cacheMap;
    private ConcurrentMap<String, Integer> cacheAdditionMap;
    private ConcurrentMap<String, Integer> cacheTechMap;
    private ConcurrentMap<String, Integer> cacheOfficerMap;
    private ConcurrentMap<String, Double> additionCacheMap;
    private ConcurrentMap<String, Integer> limitCacheMap;
    private static final Set<Integer> resourceLimitBuildingSet;
    private static final Map<Integer, Integer> buildingType2TeckKey;
    private static final String PREFIX_TOTAL = "T";
    private static final String PREFIX_SINGAL = "S";
    
    static {
        (resourceLimitBuildingSet = new HashSet<Integer>()).add(16);
        BuildingOutputCache.resourceLimitBuildingSet.add(32);
        BuildingOutputCache.resourceLimitBuildingSet.add(48);
        BuildingOutputCache.resourceLimitBuildingSet.add(64);
        (buildingType2TeckKey = new HashMap<Integer, Integer>()).put(1, 0);
        BuildingOutputCache.buildingType2TeckKey.put(2, 0);
        BuildingOutputCache.buildingType2TeckKey.put(3, 6);
        BuildingOutputCache.buildingType2TeckKey.put(4, 0);
        BuildingOutputCache.buildingType2TeckKey.put(5, 8);
    }
    
    public BuildingOutputCache() {
        this.cacheTotalMap = new ConcurrentHashMap<String, Integer>();
        this.cacheMap = new ConcurrentHashMap<String, Integer>();
        this.cacheAdditionMap = new ConcurrentHashMap<String, Integer>();
        this.cacheTechMap = new ConcurrentHashMap<String, Integer>();
        this.cacheOfficerMap = new ConcurrentHashMap<String, Integer>();
        this.additionCacheMap = new ConcurrentHashMap<String, Double>();
        this.limitCacheMap = new ConcurrentHashMap<String, Integer>();
    }
    
    @Override
    public int getBuildingsOutput(final int playerId, final int buildingType) {
        final String key = this.getKey2(playerId, buildingType);
        Integer output = 0;
        if (this.cacheTotalMap.containsKey(key) && (output = this.cacheTotalMap.get(key)) != null) {
            if (buildingType == 5) {
                KfgzMatchService.freshPlayerResourceCacheMubing(playerId, output);
            }
            return output;
        }
        output = this.getBuildingsOutputBase(playerId, buildingType) + this.getAdditionsOutput(playerId, buildingType) + this.getOfficersOutput(playerId, buildingType) + this.getTechsOutput(playerId, buildingType);
        if (5 == buildingType) {
            output = (int)(output / ((TroopConscribeSpeed)this.troopConscribeSpeedCache.get((Object)(this.techEffectCache.getTechEffect(playerId, 28) + 1))).getSpeedMutiE());
        }
        this.cacheTotalMap.put(key, output);
        if (buildingType == 5) {
            KfgzMatchService.freshPlayerResourceCacheMubing(playerId, output);
        }
        return output;
    }
    
    @Override
    public int getBuildingsOutputBase(final int playerId, final int buildingType) {
        final String key = this.getKey("T", playerId, buildingType);
        Integer output = 0;
        if (this.cacheMap.containsKey(key) && (output = this.cacheMap.get(key)) != null) {
            return output;
        }
        final List<PlayerBuilding> buildings = this.buildingService.getPlayerBuildingByType(playerId, buildingType);
        for (final PlayerBuilding pb : buildings) {
            if (((Building)this.buildingCache.get((Object)pb.getBuildingId())).getOutputType() != 4) {
                output += this.getBuildingOutputBase(playerId, pb.getBuildingId());
            }
        }
        if (5 == buildingType) {
            output += (int)(Object)((C)this.cCache.get((Object)"Troop.Conscribe.BaseSpeed")).getValue();
        }
        this.cacheMap.put(key, output);
        return output;
    }
    
    @Override
    public int getAdditionsOutput(final int playerId, final int buildingType) {
        final String key = this.getKey2(playerId, buildingType);
        Integer output = 0;
        if (this.cacheAdditionMap.containsKey(key) && (output = this.cacheAdditionMap.get(key)) != null) {
            return output;
        }
        final double addition = this.getAdditionCache(playerId, buildingType);
        if (addition < 1.0) {
            output = 0;
        }
        else {
            output = (int)(this.getBuildingsOutputBase(playerId, buildingType) * (addition - 1.0));
        }
        this.cacheAdditionMap.put(key, output);
        return output;
    }
    
    @Override
    public int getTechsOutput(final int playerId, final int buildingType) {
        final String key = this.getKey2(playerId, buildingType);
        Integer output = 0;
        if (this.cacheTechMap.containsKey(key) && (output = this.cacheTechMap.get(key)) != null) {
            return output;
        }
        if (3 == buildingType || 5 == buildingType) {
            output = (int)(this.getBuildingsOutputBase(playerId, buildingType) * (this.techEffectCache.getTechEffect(playerId, BuildingOutputCache.buildingType2TeckKey.get(buildingType)) / 100.0));
        }
        else {
            output = 0;
        }
        this.cacheTechMap.put(key, output);
        return output;
    }
    
    @Override
    public int getOfficersOutput(final int playerId, final int buildingType) {
        if (5 == buildingType) {
            return 0;
        }
        final String key = this.getKey2(playerId, buildingType);
        Integer output = 0;
        if (this.cacheOfficerMap.containsKey(key) && (output = this.cacheOfficerMap.get(key)) != null) {
            return output;
        }
        final PlayerOfficeRelative por = this.playerOfficeRelativeDao.read(playerId);
        long reputationTime = 0L;
        int pri = 0;
        if (por != null) {
            reputationTime = ((por.getReputationTime() == null) ? 0L : por.getReputationTime().getTime());
            pri = ((por.getLastOfficerId() == null) ? 0 : por.getLastOfficerId());
        }
        int base = 0;
        if (reputationTime > System.currentTimeMillis() && pri > 0) {
            base = this.hallsCache.getOutputByType(pri, buildingType);
        }
        else {
            final int officerId = this.playerOfficeRelativeDao.getOfficerId(playerId);
            base = this.hallsCache.getOutputByType(officerId, buildingType);
        }
        if (4 == buildingType) {
            base = (int)(base * this.techEffectCache.getTechEffect(playerId, 35) / 100.0);
        }
        final double addition = 1.0 + this.techEffectCache.getTechEffect(playerId, 22) / 100.0;
        output = (int)(base * addition);
        this.cacheOfficerMap.put(key, output);
        return output;
    }
    
    @Override
    public double getAdditionCache(final int playerId, final int buildingType) {
        final String key = this.getKey2(playerId, buildingType);
        Double addition = 0.0;
        if (this.additionCacheMap.containsKey(key) && (addition = this.additionCacheMap.get(key)) != null) {
            return addition;
        }
        final PlayerResourceAddition pra = this.playerResourceAdditionDao.getByPlayerIdAndType(playerId, buildingType);
        if (pra != null && pra.getEndTime().after(new Date())) {
            addition = ((Chargeitem)this.chargeItemCache.get((Object)this.buildingService.getId(buildingType, pra.getAdditionMode()))).getParam();
        }
        this.additionCacheMap.put(key, addition);
        return addition;
    }
    
    @Override
    public void clearOutputAddition(final int playerId, final int buildingType) {
        final String key = this.getKey2(playerId, buildingType);
        this.additionCacheMap.remove(key);
        this.cacheAdditionMap.remove(key);
        this.cacheTotalMap.remove(key);
        this.resourceService.pushOutput(playerId);
    }
    
    @Override
    public int getBuildingOutputBase(final int playerId, final int buildingId) {
        final String key = this.getKey("S", playerId, buildingId);
        final Building building = (Building)this.buildingCache.get((Object)buildingId);
        int buildingType = building.getType();
        if (buildingType >= 6 && buildingType <= 8) {
            buildingType = 5;
        }
        Integer base = 0;
        if (this.cacheMap.containsKey(key) && (base = this.cacheMap.get(key)) != null) {
            return base;
        }
        base = this.calcOutput(playerId, buildingId);
        this.cacheMap.put(key, base);
        return base;
    }
    
    @Override
    public int getBuildingOutput(final int playerId, final int buildingId) {
        final Building building = (Building)this.buildingCache.get((Object)buildingId);
        if (4 != building.getOutputType()) {
            return this.getBuildingOutputBase(playerId, buildingId);
        }
        final String key = this.getKey2(playerId, building.getType());
        Integer output = 0;
        if (this.limitCacheMap.containsKey(key) && (output = this.limitCacheMap.get(key)) != null) {
            return output;
        }
        output = (int)(this.getBuildingOutputBase(playerId, buildingId) * (1.0 + this.techEffectCache.getTechEffect(playerId, 20) / 100.0));
        this.limitCacheMap.put(key, output);
        return output;
    }
    
    private int calcOutput(final int playerId, final int buildingId) {
        if (buildingId <= 0) {
            return 0;
        }
        final Building building = (Building)this.buildingCache.get((Object)buildingId);
        final PlayerBuilding pb = this.buildingService.getPlayerBuilding(playerId, buildingId);
        if (pb == null) {
            if (building.getOutputType() == 4) {
                return 10000;
            }
            return 0;
        }
        else {
            final int outputType = building.getOutputType();
            if (outputType == 1 || outputType == 5 || outputType == 4) {
                return (int)(building.getOutputE() * this.serialCache.get(building.getOutputS(), pb.getLv()));
            }
            if (outputType == 2 || outputType == 3) {
                final int c1 = this.getRelateBuildingOutput(playerId, building);
                return (int)(building.getOutputE() * this.serialCache.get(building.getOutputS(), pb.getLv()) + building.getOutputE1() * c1);
            }
            return 0;
        }
    }
    
    private int getRelateBuildingOutput(final int playerId, final Building building) {
        final String str = building.getOutputRelatedBuilding();
        if (StringUtils.isBlank(str)) {
            return 0;
        }
        int result = 0;
        final String[] strs = str.split(",");
        String[] array;
        for (int length = (array = strs).length, i = 0; i < length; ++i) {
            final String value = array[i];
            final int id = Integer.valueOf(value);
            if (id > 0) {
                result += this.getBuildingOutputBase(playerId, id);
            }
        }
        return result;
    }
    
    private String getKey(final String prefix, final int playerId, final int param) {
        return new StringBuilder(15).append(prefix).append("-").append(playerId).append("-").append(param).toString();
    }
    
    private String getKey2(final int playerId, final int param) {
        return new StringBuilder(15).append(playerId).append("-").append(param).toString();
    }
    
    @Override
    public void clearLimit(final int playerId) {
        for (int i = 1; i < 5; ++i) {
            this.limitCacheMap.remove(this.getKey2(playerId, i));
        }
        this.resourceService.pushOutput(playerId);
    }
    
    @Override
    public void logoutClear(final int playerId) {
        String key = null;
        for (int i = 1; i <= 5; ++i) {
            key = this.getKey2(playerId, i);
            final List<Building> list = this.buildingCache.getBuildingByType(i);
            for (final Building bd : list) {
                this.cacheMap.remove(this.getKey("S", playerId, bd.getId()));
            }
            this.cacheMap.remove(this.getKey("T", playerId, i));
            this.cacheTechMap.remove(key);
            this.cacheOfficerMap.remove(key);
            this.cacheAdditionMap.remove(key);
            this.cacheTotalMap.remove(key);
            this.limitCacheMap.remove(key);
            if (i <= 4) {
                this.additionCacheMap.remove(key);
            }
        }
    }
    
    @Override
    public void clearBase(final int playerId, final int buildingId) {
        final Building building = (Building)this.buildingCache.get((Object)buildingId);
        final int buildingType = building.getType();
        final List<Building> list = this.buildingCache.getBuildingByType(buildingType);
        for (final Building bd : list) {
            this.cacheMap.remove(this.getKey("S", playerId, bd.getId()));
        }
        this.cacheMap.remove(this.getKey("T", playerId, buildingType));
        final String key = this.getKey2(playerId, buildingType);
        this.cacheTechMap.remove(key);
        this.cacheOfficerMap.remove(key);
        this.cacheAdditionMap.remove(key);
        this.cacheTotalMap.remove(key);
        if (4 == building.getOutputType()) {
            this.limitCacheMap.remove(key);
        }
        this.resourceService.pushOutput(playerId);
    }
    
    @Override
    public void clearTech(final int playerId, final int buildingType) {
        final String key = this.getKey2(playerId, buildingType);
        this.cacheTechMap.remove(key);
        this.cacheTotalMap.remove(key);
        this.resourceService.pushOutput(playerId);
    }
    
    @Override
    public void clearTechGaoGuan(final int playerId) {
        String key = null;
        for (int i = 1; i < 5; ++i) {
            key = this.getKey2(playerId, i);
            this.cacheOfficerMap.remove(key);
            this.cacheTotalMap.remove(key);
        }
        this.resourceService.pushOutput(playerId);
    }
    
    @Override
    public void clearOfficer(final int playerId) {
        String key = null;
        for (int i = 1; i < 5; ++i) {
            key = this.getKey2(playerId, i);
            this.cacheOfficerMap.remove(key);
            this.cacheTotalMap.remove(key);
        }
        this.resourceService.pushOutput(playerId);
    }
    
    @Override
    public void clearTechBinZhong(final int playerId) {
        final String key = this.getKey2(playerId, 5);
        this.cacheTotalMap.remove(key);
        this.resourceService.pushOutput(playerId);
        Players.push(playerId, PushCommand.PUSH_UPDATE, JsonBuilder.getSimpleJson("troopLv", this.techEffectCache.getTechEffect(playerId, 28) + 1));
    }
}
