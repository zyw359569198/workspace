package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import java.util.*;

@Component("troopConscribeCache")
public class TroopConscribeCache extends AbstractCache<Integer, TroopConscribe>
{
    @Autowired
    private SDataLoader dataLoader;
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<TroopConscribe> resultList = this.dataLoader.getModels((Class)TroopConscribe.class);
        for (final TroopConscribe temp : resultList) {
            super.put((Object)temp.getTroopId(), (Object)temp);
        }
    }
}
