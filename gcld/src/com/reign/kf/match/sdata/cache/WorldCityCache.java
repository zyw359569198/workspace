package com.reign.kf.match.sdata.cache;

import com.reign.kf.match.sdata.domain.*;
import org.springframework.beans.factory.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.kfgz.comm.*;
import java.util.*;

@Component("worldCityCache")
public class WorldCityCache extends AbstractCache<Integer, KfgzWorldCity> implements InitializingBean
{
    private SDataLoader dataLoader;
    private static WorldCityCache worldCityCache;
    
    public static KfgzWorldCity getById(final Integer cityId) {
        return (KfgzWorldCity)WorldCityCache.worldCityCache.get((Object)cityId);
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
        final List<KfgzWorldCity> cList = this.dataLoader.getModels((Class)KfgzWorldCity.class);
        for (final KfgzWorldCity c : cList) {
            super.put((Object)c.getId(), (Object)c);
        }
        WorldCityCache.worldCityCache = this;
    }
    
    @Override
	public void reload() throws Exception {
        super.clear();
        this.afterPropertiesSet();
    }
    
    public static synchronized List<KfgzWorldCity> getWorldCities(final int worldId) {
        final List<KfgzWorldCity> list = new ArrayList<KfgzWorldCity>();
        for (final KfgzWorldCity city : WorldCityCache.worldCityCache.getModels()) {
            if (city.getWorld_id() == worldId) {
                list.add(city);
            }
        }
        return list;
    }
}
