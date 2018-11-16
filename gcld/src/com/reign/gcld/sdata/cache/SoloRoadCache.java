package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import java.util.*;

@Component("soloRoadCache")
public class SoloRoadCache extends AbstractCache<Integer, SoloRoad>
{
    @Autowired
    private SDataLoader dataLoader;
    private Map<Integer, Map<String, SoloRoad>> roadMap;
    private Map<Integer, Set<Integer>> nodeNeighbors;
    private Map<Integer, String> intToStringMap;
    
    public SoloRoadCache() {
        this.roadMap = new HashMap<Integer, Map<String, SoloRoad>>();
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
        final List<SoloRoad> list = this.dataLoader.getModels((Class)SoloRoad.class);
        for (final SoloRoad soloRoad : list) {
            Map<String, SoloRoad> rMap = this.roadMap.get(soloRoad.getSoloId());
            if (rMap == null) {
                rMap = new HashMap<String, SoloRoad>();
                this.roadMap.put(soloRoad.getSoloId(), rMap);
            }
            final String key = this.makeMapKey(soloRoad.getStart(), soloRoad.getEnd());
            rMap.put(key, soloRoad);
            this.intToStringMap.put(index, key);
            this.handleNeighbor(soloRoad);
            ++index;
            super.put((Object)soloRoad.getId(), (Object)soloRoad);
        }
    }
    
    private void handleNeighbor(final SoloRoad soloRoad) {
        Set<Integer> startNeighbors = this.nodeNeighbors.get(soloRoad.getStart());
        if (startNeighbors == null) {
            startNeighbors = new HashSet<Integer>();
            this.nodeNeighbors.put(soloRoad.getStart(), startNeighbors);
        }
        startNeighbors.add(soloRoad.getEnd());
        Set<Integer> endNeighbors = this.nodeNeighbors.get(soloRoad.getEnd());
        if (endNeighbors == null) {
            endNeighbors = new HashSet<Integer>();
            this.nodeNeighbors.put(soloRoad.getEnd(), endNeighbors);
        }
        endNeighbors.add(soloRoad.getStart());
    }
    
    public Map<String, SoloRoad> getRoadMap(final int soloId) {
        return this.roadMap.get(soloId);
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
    
    public SoloRoad getRoad(final int soloId, final int node1, final int node2) {
        final Map<String, SoloRoad> rMap = this.roadMap.get(soloId);
        String key = this.makeMapKey(node1, node2);
        if (rMap.containsKey(key)) {
            return rMap.get(key);
        }
        key = this.makeMapKey(node2, node1);
        return rMap.get(key);
    }
    
    @Override
	public void clear() {
        super.clear();
        this.roadMap.clear();
        this.nodeNeighbors.clear();
        this.intToStringMap.clear();
    }
}
