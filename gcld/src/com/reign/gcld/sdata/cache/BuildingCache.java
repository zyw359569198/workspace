package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import java.util.*;

@Component("buildingCache")
public class BuildingCache extends AbstractCache<Integer, Building>
{
    @Autowired
    private SDataLoader dataLoader;
    private Map<Integer, List<Building>> typeBuildMap;
    
    public BuildingCache() {
        this.typeBuildMap = new HashMap<Integer, List<Building>>();
    }
    
    public List<Building> getBuildingByType(final int type) {
        return this.typeBuildMap.get(type);
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<Building> resultList = this.dataLoader.getModels((Class)Building.class);
        for (final Building building : resultList) {
            super.put((Object)building.getId(), (Object)building);
            List<Building> bList = this.typeBuildMap.get(building.getType());
            if (bList == null) {
                bList = new ArrayList<Building>();
                this.typeBuildMap.put(building.getType(), bList);
            }
            bList.add(building);
        }
    }
    
    @Override
	public void clear() {
        super.clear();
        this.typeBuildMap.clear();
    }
}
