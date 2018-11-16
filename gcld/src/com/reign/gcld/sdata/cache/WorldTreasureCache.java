package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import java.util.*;

@Component("worldTreasureCache")
public class WorldTreasureCache extends AbstractCache<Integer, WorldTreasure>
{
    @Autowired
    private SDataLoader dataLoader;
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<WorldTreasure> list = this.dataLoader.getModels((Class)WorldTreasure.class);
        for (final WorldTreasure wt : list) {
            super.put((Object)wt.getId(), (Object)wt);
        }
    }
    
    public void setDataLoader(final SDataLoader dataLoader) {
        this.dataLoader = dataLoader;
    }
    
    public SDataLoader getDataLoader() {
        return this.dataLoader;
    }
}
