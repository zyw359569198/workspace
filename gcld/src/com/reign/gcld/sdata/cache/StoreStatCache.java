package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import java.util.*;

@Component("storeStatCache")
public class StoreStatCache extends AbstractCache<Integer, StoreStat>
{
    @Autowired
    private SDataLoader dataLoader;
    private Map<Integer, List<StoreStat>> storeStatMap;
    
    public StoreStatCache() {
        this.storeStatMap = new HashMap<Integer, List<StoreStat>>();
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<StoreStat> resultList = this.dataLoader.getModels((Class)StoreStat.class);
        for (final StoreStat storeStat : resultList) {
            List<StoreStat> list = this.storeStatMap.get(storeStat.getPreStat());
            if (list == null) {
                list = new ArrayList<StoreStat>();
                this.storeStatMap.put(storeStat.getPreStat(), list);
            }
            list.add(storeStat);
        }
    }
    
    public List<StoreStat> getStoreStatList(final int preState) {
        return this.storeStatMap.get(preState);
    }
    
    @Override
	public void clear() {
        super.clear();
        this.storeStatMap.clear();
    }
}
