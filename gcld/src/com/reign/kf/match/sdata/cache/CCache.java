package com.reign.kf.match.sdata.cache;

import com.reign.kf.match.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import java.util.*;

@Component("cCache")
public class CCache extends AbstractCache<String, C>
{
    static CCache staticCache;
    @Autowired
    private SDataLoader dataLoader;
    
    static {
        CCache.staticCache = null;
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<C> resultList = this.dataLoader.getModels((Class)C.class);
        for (final C c : resultList) {
            super.put((Object)c.getParam(), (Object)c);
        }
        CCache.staticCache = this;
    }
    
    public static C getCById(final String id) {
        return (C)CCache.staticCache.get((Object)id);
    }
}
