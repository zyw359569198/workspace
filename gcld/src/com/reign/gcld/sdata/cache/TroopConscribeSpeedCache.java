package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import java.util.*;

@Component("troopConscribeSpeedCache")
public class TroopConscribeSpeedCache extends AbstractCache<Integer, TroopConscribeSpeed>
{
    @Autowired
    private SDataLoader dataLoader;
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<TroopConscribeSpeed> list = this.dataLoader.getModels((Class)TroopConscribeSpeed.class);
        for (final TroopConscribeSpeed tcs : list) {
            super.put((Object)tcs.getLevel(), (Object)tcs);
        }
    }
}
