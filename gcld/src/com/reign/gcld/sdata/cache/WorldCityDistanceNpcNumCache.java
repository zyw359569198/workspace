package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import java.util.*;

@Component("WorldCityDistanceNpcNumCache")
public class WorldCityDistanceNpcNumCache extends AbstractCache<Integer, WorldCityDistanceNpcNum>
{
    @Autowired
    private SDataLoader dataLoader;
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<WorldCityDistanceNpcNum> list = this.dataLoader.getModels((Class)WorldCityDistanceNpcNum.class);
        for (final WorldCityDistanceNpcNum wcdnn : list) {
            super.put((Object)wcdnn.getId(), (Object)wcdnn);
        }
    }
}
