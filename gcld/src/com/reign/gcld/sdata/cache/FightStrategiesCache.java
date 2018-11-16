package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import java.util.*;

@Component("fightStrategiesCache")
public class FightStrategiesCache extends AbstractCache<Integer, FightStrategies>
{
    @Autowired
    private SDataLoader dataLoader;
    public static Map<Integer, String> picMap;
    
    static {
        FightStrategiesCache.picMap = new HashMap<Integer, String>();
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<FightStrategies> fsList = this.dataLoader.getModels((Class)FightStrategies.class);
        for (final FightStrategies temp : fsList) {
            super.put((Object)temp.getId(), (Object)temp);
            FightStrategiesCache.picMap.put(temp.getId(), temp.getPic());
        }
    }
    
    public static String getStrategyPic(final int id) {
        return FightStrategiesCache.picMap.get(id);
    }
    
    @Override
	public void clear() {
        super.clear();
        FightStrategiesCache.picMap.clear();
    }
}
