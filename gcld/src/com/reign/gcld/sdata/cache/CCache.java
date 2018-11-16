package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import java.util.*;

@Component("cCache")
public class CCache extends AbstractCache<String, C>
{
    @Autowired
    private SDataLoader dataLoader;
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<C> resultList = this.dataLoader.getModels((Class)C.class);
        for (final C c : resultList) {
            super.put((Object)c.getParam(), (Object)c);
        }
    }
}
