package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import java.util.*;

@Component("worldGuardInfoCache")
public class WorldGuardInfoCache extends AbstractCache<Integer, WorldGuardInfo>
{
    @Autowired
    private SDataLoader dataLoader;
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<WorldGuardInfo> list = this.dataLoader.getModels((Class)WorldGuardInfo.class);
        int maxDis = 0;
        for (final WorldGuardInfo wgi : list) {
            if (maxDis < wgi.getDistance()) {
                maxDis = wgi.getDistance();
            }
            super.put((Object)wgi.getDistance(), (Object)wgi);
        }
        for (int i = maxDis + 1; i <= 30; ++i) {
            super.put((Object)i, (Object)list.get(0));
        }
    }
}
