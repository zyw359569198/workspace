package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import java.util.*;
import com.reign.gcld.common.util.*;

@Component("marketIronCache")
public class MarketIronCache extends AbstractCache<Integer, MarketIron>
{
    @Autowired
    private SDataLoader dataLoader;
    private Map<Integer, List<MarketIron>> degreeListMap;
    private static int maxId;
    
    static {
        MarketIronCache.maxId = 0;
    }
    
    public MarketIronCache() {
        this.degreeListMap = new HashMap<Integer, List<MarketIron>>();
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<MarketIron> list = this.dataLoader.getModels((Class)MarketIron.class);
        for (final MarketIron temp : list) {
            super.put((Object)temp.getId(), (Object)temp);
            List<MarketIron> degreeList = this.degreeListMap.get(temp.getDegree());
            if (degreeList == null) {
                degreeList = new ArrayList<MarketIron>();
                this.degreeListMap.put(temp.getDegree(), degreeList);
            }
            degreeList.add(temp);
            if (temp.getId() > MarketIronCache.maxId) {
                MarketIronCache.maxId = temp.getId();
            }
        }
    }
    
    public int getMaxId() {
        return MarketIronCache.maxId;
    }
    
    public MarketIron getMarketIronByDegree(final int degree) {
        final List<MarketIron> list = this.degreeListMap.get(degree);
        if (list == null || list.size() <= 0) {
            return null;
        }
        if (1 == list.size()) {
            return list.get(0);
        }
        final List<MarketIron> copyList = new ArrayList<MarketIron>();
        int total = 0;
        for (final MarketIron mi : list) {
            total += mi.getProb();
            copyList.add(mi);
        }
        int rate = WebUtil.nextInt(total) + 1;
        while (copyList.size() > 0) {
            if (rate <= copyList.get(0).getProb()) {
                return copyList.get(0);
            }
            rate -= copyList.get(0).getProb();
            copyList.remove(0);
        }
        return null;
    }
    
    @Override
	public void clear() {
        super.clear();
        for (final List<MarketIron> degreeList : this.degreeListMap.values()) {
            degreeList.clear();
        }
        this.degreeListMap.clear();
    }
}
