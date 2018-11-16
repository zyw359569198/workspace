package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import java.util.*;

@Component("worldLegionCache")
public class WorldLegionCache extends AbstractCache<Integer, WorldLegion>
{
    @Autowired
    private SDataLoader dataLoader;
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<WorldLegion> list = this.dataLoader.getModels((Class)WorldLegion.class);
        for (final WorldLegion wdl : list) {
            super.put((Object)wdl.getId(), (Object)wdl);
        }
    }
}
