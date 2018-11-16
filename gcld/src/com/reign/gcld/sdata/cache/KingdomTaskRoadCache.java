package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import java.util.*;

@Component("kingdomTaskRoadCache")
public class KingdomTaskRoadCache extends AbstractCache<Integer, KindomTaskRoad>
{
    @Autowired
    private SDataLoader dataLoader;
    private Map<Integer, List<KindomTaskRoad>> typeRoadMap;
    
    public KingdomTaskRoadCache() {
        this.typeRoadMap = new HashMap<Integer, List<KindomTaskRoad>>();
    }
    
    public SDataLoader getDataLoader() {
        return this.dataLoader;
    }
    
    public void setDataLoader(final SDataLoader dataLoader) {
        this.dataLoader = dataLoader;
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<KindomTaskRoad> list = this.dataLoader.getModels((Class)KindomTaskRoad.class);
        int type = 0;
        for (final KindomTaskRoad road : list) {
            super.put((Object)road.getId(), (Object)road);
            type = road.getId() / 100;
            if (this.typeRoadMap.containsKey(type)) {
                List<KindomTaskRoad> roads = this.typeRoadMap.get(type);
                if (roads == null) {
                    roads = new ArrayList<KindomTaskRoad>();
                    roads.add(road);
                    this.typeRoadMap.put(type, roads);
                }
                else {
                    roads.add(road);
                }
            }
            else {
                final List<KindomTaskRoad> roads = new ArrayList<KindomTaskRoad>();
                roads.add(road);
                this.typeRoadMap.put(type, roads);
            }
        }
    }
    
    public List<KindomTaskRoad> getRoadsByType(final int type) {
        final List<KindomTaskRoad> roads = this.typeRoadMap.get(type);
        Collections.shuffle(roads);
        return roads;
    }
    
    @Override
	public void clear() {
        super.clear();
        this.typeRoadMap.clear();
    }
}
