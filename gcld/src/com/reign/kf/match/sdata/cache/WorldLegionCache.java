package com.reign.kf.match.sdata.cache;

import com.reign.kf.match.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import java.util.*;

@Component("worldLegionCache")
public class WorldLegionCache extends AbstractCache<Integer, WorldLegion>
{
    @Autowired
    private SDataLoader dataLoader;
    public static final int WLKEY = 1;
    private static WorldLegionCache worldStaticCache;
    
    public static WorldLegion getWorldLegionById(final int id) {
        return (WorldLegion)WorldLegionCache.worldStaticCache.get((Object)id);
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<WorldLegion> list = this.dataLoader.getModels((Class)WorldLegion.class);
        for (final WorldLegion wdl : list) {
            super.put((Object)wdl.getId(), (Object)wdl);
        }
        WorldLegionCache.worldStaticCache = this;
    }
}
