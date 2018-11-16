package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import java.util.*;

@Component("ktInitCache")
public class KtInitCache extends AbstractCache<Integer, KtInit>
{
    @Autowired
    private SDataLoader dataLoader;
    
    public SDataLoader getDataLoader() {
        return this.dataLoader;
    }
    
    public void setDataLoader(final SDataLoader dataLoader) {
        this.dataLoader = dataLoader;
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<KtInit> list = this.dataLoader.getModels((Class)KtInit.class);
        for (final KtInit ktInit : list) {
            super.put((Object)ktInit.getId(), (Object)ktInit);
        }
    }
}
