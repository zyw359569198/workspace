package com.reign.kf.match.sdata.cache;

import com.reign.kf.match.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import java.util.*;

@Component("itemsCache")
public class ItemsCache extends AbstractCache<Integer, Items>
{
    @Autowired
    private SDataLoader dataLoader;
    private static ItemsCache itemsCache;
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<Items> list = this.dataLoader.getModels((Class)Items.class);
        for (final Items i : list) {
            super.put((Object)i.getId(), (Object)i);
        }
        ItemsCache.itemsCache = this;
    }
    
    public static Items getItemsById(final int id) {
        return (Items)ItemsCache.itemsCache.get((Object)id);
    }
}
