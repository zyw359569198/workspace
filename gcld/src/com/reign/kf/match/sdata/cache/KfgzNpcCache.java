package com.reign.kf.match.sdata.cache;

import com.reign.kf.match.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.kfgz.comm.*;
import java.util.*;

@Component
public class KfgzNpcCache extends AbstractCache<Integer, KfgzNpc>
{
    @Autowired
    private SDataLoader dataLoader;
    private static KfgzNpcCache kfgzNpcCache;
    
    @Override
	public void afterPropertiesSet() throws Exception {
        if (KfgzConstants.GZALLCLOSE) {
            return;
        }
        final List<KfgzNpc> list = this.dataLoader.getModels((Class)KfgzNpc.class);
        for (final KfgzNpc npc : list) {
            this.put((Object)npc.getId(), (Object)npc);
        }
        KfgzNpcCache.kfgzNpcCache = this;
    }
}
