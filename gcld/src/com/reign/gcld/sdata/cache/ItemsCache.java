package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import java.util.*;

@Component("itemsCache")
public class ItemsCache extends AbstractCache<Integer, Items>
{
    @Autowired
    private SDataLoader dataLoader;
    private Map<String, Items> itemsMap;
    
    public ItemsCache() {
        this.itemsMap = new HashMap<String, Items>();
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<Items> list = this.dataLoader.getModels((Class)Items.class);
        for (final Items i : list) {
            super.put((Object)i.getId(), (Object)i);
            final String key = i.getType() + "_" + i.getIndex();
            this.itemsMap.put(key, i);
        }
    }
    
    public Items getItemsByTypeAndIndex(final int type, final int index) {
        final String key = String.valueOf(type) + "_" + index;
        return this.itemsMap.get(key);
    }
}
