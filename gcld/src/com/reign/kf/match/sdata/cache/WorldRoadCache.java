package com.reign.kf.match.sdata.cache;

import com.reign.kf.match.sdata.domain.*;
import org.springframework.beans.factory.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import java.util.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.kfgz.comm.*;

@Component("worldRoadCache")
public class WorldRoadCache extends AbstractCache<Integer, KfgzWorldRoad> implements InitializingBean
{
    private SDataLoader dataLoader;
    private static WorldRoadCache worldRoadCache;
    private static Map<Integer, Map<Integer, KfgzWorldRoad>> worldRoadMap;
    private static Map<Integer, List<KfgzWorldRoad>> worldRoadListMap;
    
    static {
        WorldRoadCache.worldRoadMap = new HashMap<Integer, Map<Integer, KfgzWorldRoad>>();
        WorldRoadCache.worldRoadListMap = new HashMap<Integer, List<KfgzWorldRoad>>();
    }
    
    public static List<KfgzWorldRoad> getChangeKfgzWorldRoad(final int worldId) {
        final List<KfgzWorldRoad> result = new ArrayList<KfgzWorldRoad>();
        for (final KfgzWorldRoad wr : getAllModels(worldId)) {
            if (1 == wr.getType()) {
                result.add(wr);
            }
        }
        return result;
    }
    
    public static KfgzWorldRoad getById(final int roadId) {
        return (KfgzWorldRoad)WorldRoadCache.worldRoadCache.get((Object)roadId);
    }
    
    public static List<KfgzWorldRoad> getAllModels(final int worldId) {
        return WorldRoadCache.worldRoadListMap.get(worldId);
    }
    
    @Autowired
    public void setDataLoader(final SDataLoader dataLoader) {
        this.dataLoader = dataLoader;
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        if (KfgzConstants.GZALLCLOSE) {
            return;
        }
        final List<KfgzWorldRoad> campaignList = this.dataLoader.getModels((Class)KfgzWorldRoad.class);
        for (final KfgzWorldRoad campaign : campaignList) {
            super.put((Object)campaign.getId(), (Object)campaign);
            final int worldId = campaign.getWorld_id();
            Map<Integer, KfgzWorldRoad> map = WorldRoadCache.worldRoadMap.get(worldId);
            if (map == null) {
                map = new HashMap<Integer, KfgzWorldRoad>();
                WorldRoadCache.worldRoadMap.put(worldId, map);
            }
            map.put(campaign.getId(), campaign);
            List<KfgzWorldRoad> rlist = WorldRoadCache.worldRoadListMap.get(worldId);
            if (rlist == null) {
                rlist = new ArrayList<KfgzWorldRoad>();
                WorldRoadCache.worldRoadListMap.put(worldId, rlist);
            }
            rlist.add(campaign);
        }
        WorldRoadCache.worldRoadCache = this;
    }
    
    @Override
	public void reload() throws Exception {
        super.clear();
        this.afterPropertiesSet();
    }
}
