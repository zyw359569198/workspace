package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import java.util.*;

@Component("storeItemsCache")
public class StoreItemsCache extends AbstractCache<Integer, StoreItems>
{
    @Autowired
    private SDataLoader dataLoader;
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<StoreItems> resultList = this.dataLoader.getModels((Class)StoreItems.class);
        for (final StoreItems storeItem : resultList) {
            super.put((Object)storeItem.getItemId(), (Object)storeItem);
        }
    }
}
