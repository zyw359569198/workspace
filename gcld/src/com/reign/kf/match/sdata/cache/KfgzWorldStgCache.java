package com.reign.kf.match.sdata.cache;

import com.reign.kf.match.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.kfgz.comm.*;
import java.util.*;

@Component
public class KfgzWorldStgCache extends AbstractCache<Integer, KfgzWorldStg>
{
    @Autowired
    private SDataLoader dataLoader;
    private static HashMap<Integer, HashMap<Integer, KfgzWorldStg>> worldStgGzMap;
    private static KfgzWorldStgCache kfgzWorldStgCache;
    
    static {
        KfgzWorldStgCache.worldStgGzMap = new HashMap<Integer, HashMap<Integer, KfgzWorldStg>>();
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        if (KfgzConstants.GZALLCLOSE) {
            return;
        }
        final List<KfgzWorldStg> list = this.dataLoader.getModels((Class)KfgzWorldStg.class);
        for (final KfgzWorldStg wstg : list) {
            this.put((Object)wstg.getId(), (Object)wstg);
            HashMap<Integer, KfgzWorldStg> wmap = KfgzWorldStgCache.worldStgGzMap.get(wstg.getWorldStgid());
            if (wmap == null) {
                wmap = new HashMap<Integer, KfgzWorldStg>();
                KfgzWorldStgCache.worldStgGzMap.put(wstg.getWorldStgid(), wmap);
            }
            wmap.put(wstg.getId(), wstg);
        }
        KfgzWorldStgCache.kfgzWorldStgCache = this;
    }
    
    public static HashMap<Integer, KfgzWorldStg> getWordStgsByGzId(final int gzId) {
        return KfgzWorldStgCache.worldStgGzMap.get(gzId);
    }
}
