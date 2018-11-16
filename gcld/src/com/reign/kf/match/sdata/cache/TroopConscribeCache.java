package com.reign.kf.match.sdata.cache;

import com.reign.kf.match.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import java.util.*;

@Component("troopConscribeCache")
public class TroopConscribeCache extends AbstractCache<Integer, TroopConscribe>
{
    @Autowired
    private SDataLoader dataLoader;
    private static TroopConscribeCache troopConscribeCache;
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<TroopConscribe> resultList = this.dataLoader.getModels((Class)TroopConscribe.class);
        for (final TroopConscribe temp : resultList) {
            super.put((Object)temp.getTroopId(), (Object)temp);
        }
        TroopConscribeCache.troopConscribeCache = this;
    }
    
    public static TroopConscribe getTroopConscribeById(final int id) {
        return (TroopConscribe)TroopConscribeCache.troopConscribeCache.get((Object)id);
    }
}
