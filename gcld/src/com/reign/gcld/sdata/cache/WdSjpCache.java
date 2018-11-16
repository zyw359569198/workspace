package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.util.*;
import java.util.*;

@Component("wdSjpCache")
public class WdSjpCache extends AbstractCache<Integer, WdSjp>
{
    @Autowired
    private SDataLoader dataLoader;
    public Map<Integer, Tuple<Integer, Integer>> timeWindowtMap;
    
    public WdSjpCache() {
        this.timeWindowtMap = new HashMap<Integer, Tuple<Integer, Integer>>();
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<WdSjp> resultList = this.dataLoader.getModels((Class)WdSjp.class);
        for (final WdSjp wdSjp : resultList) {
            final Tuple<Integer, Integer> tuple = new Tuple();
            tuple.left = wdSjp.getOpen();
            tuple.right = wdSjp.getClose();
            this.timeWindowtMap.put(wdSjp.getId(), tuple);
            super.put((Object)wdSjp.getId(), (Object)wdSjp);
        }
    }
    
    @Override
	public void clear() {
        super.clear();
        this.timeWindowtMap.clear();
    }
}
