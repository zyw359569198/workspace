package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import java.util.*;

@Component("giftCache")
public class GiftCache extends AbstractCache<String, Gift>
{
    @Autowired
    private SDataLoader dataLoader;
    Map<String, String> nameMap;
    
    public GiftCache() {
        this.nameMap = new HashMap<String, String>();
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<Gift> resultList = this.dataLoader.getModels((Class)Gift.class);
        for (final Gift gift : resultList) {
            super.put((Object)gift.getId().toString(), (Object)gift);
            this.nameMap.put(gift.getChildId(), gift.getChildName());
        }
    }
    
    public String getName(final String key) {
        return this.nameMap.get(key);
    }
    
    public Set<String> getKeys() {
        return this.nameMap.keySet();
    }
    
    @Override
	public void clear() {
        super.clear();
        this.nameMap.clear();
    }
}
