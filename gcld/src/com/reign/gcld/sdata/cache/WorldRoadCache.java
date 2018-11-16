package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import java.util.*;

@Component("worldRoadCache")
public class WorldRoadCache extends AbstractCache<Integer, WorldRoad>
{
    @Autowired
    private SDataLoader dataLoader;
    private Map<String, WorldRoad> roadMap;
    private Map<Integer, Set<Integer>> nodeNeighbors;
    private Map<Integer, String> intToStringMap;
    
    public WorldRoadCache() {
        this.roadMap = new HashMap<String, WorldRoad>();
        this.nodeNeighbors = new HashMap<Integer, Set<Integer>>();
        this.intToStringMap = new HashMap<Integer, String>();
    }
    
    public Map<Integer, String> getIntToStringMap() {
        return this.intToStringMap;
    }
    
    public void setIntToStringMap(final Map<Integer, String> intToStringMap) {
        this.intToStringMap = intToStringMap;
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        int index = 0;
        final List<WorldRoad> list = this.dataLoader.getModels((Class)WorldRoad.class);
        for (final WorldRoad worldRoad : list) {
            final String key = this.makeMapKey(worldRoad.getStart(), worldRoad.getEnd());
            this.roadMap.put(key, worldRoad);
            this.intToStringMap.put(index, key);
            this.handleNeighbor(worldRoad);
            ++index;
        }
    }
    
    private void handleNeighbor(final WorldRoad worldRoad) {
        Set<Integer> startNeighbors = this.nodeNeighbors.get(worldRoad.getStart());
        if (startNeighbors == null) {
            startNeighbors = new HashSet<Integer>();
            this.nodeNeighbors.put(worldRoad.getStart(), startNeighbors);
        }
        startNeighbors.add(worldRoad.getEnd());
        Set<Integer> endNeighbors = this.nodeNeighbors.get(worldRoad.getEnd());
        if (endNeighbors == null) {
            endNeighbors = new HashSet<Integer>();
            this.nodeNeighbors.put(worldRoad.getEnd(), endNeighbors);
        }
        endNeighbors.add(worldRoad.getStart());
    }
    
    public Map<String, WorldRoad> getRoadMap() {
        return this.roadMap;
    }
    
    public Set<Integer> getNeighbors(final int cityId) {
        return this.nodeNeighbors.get(cityId);
    }
    
    private String makeMapKey(final int node1, final int node2) {
        final StringBuilder sb = new StringBuilder();
        sb.append(node1);
        sb.append("-");
        sb.append(node2);
        return sb.toString();
    }
    
    public WorldRoad getRoad(final int node1, final int node2) {
        String key = this.makeMapKey(node1, node2);
        if (this.roadMap.containsKey(key)) {
            return this.roadMap.get(key);
        }
        key = this.makeMapKey(node2, node1);
        return this.roadMap.get(key);
    }
    
    @Override
	public void clear() {
        super.clear();
        this.roadMap.clear();
        this.nodeNeighbors.clear();
        this.intToStringMap.clear();
    }
}
