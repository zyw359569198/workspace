package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.common.log.*;
import java.util.*;

@Component("marketProductsCache")
public class MarketProductsCache extends AbstractCache<Integer, MarketProducts>
{
    @Autowired
    private SDataLoader dataLoader;
    private Map<Integer, Map<Integer, List<MarketProducts>>> degreeListMap;
    public static int maxDegree;
    private static final Logger errorLog;
    
    static {
        MarketProductsCache.maxDegree = 0;
        errorLog = CommonLog.getLog(MarketProductsCache.class);
    }
    
    public MarketProductsCache() {
        this.degreeListMap = new HashMap<Integer, Map<Integer, List<MarketProducts>>>();
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<MarketProducts> list = this.dataLoader.getModels((Class)MarketProducts.class);
        for (final MarketProducts temp : list) {
            if (temp.getId() > 90000) {
                MarketProductsCache.errorLog.info("MarketProducts's id bigger than Constants.MARKET_IRON_ID_BASE (90000), and it is:" + temp.getId());
                throw new RuntimeException("MarketProducts's id bigger than Constants.MARKET_IRON_ID_BASE (90000), and it is:" + temp.getId());
            }
            super.put((Object)temp.getId(), (Object)temp);
            Map<Integer, List<MarketProducts>> degreeMap = this.degreeListMap.get(temp.getDegree());
            if (degreeMap == null) {
                degreeMap = new HashMap<Integer, List<MarketProducts>>();
                this.degreeListMap.put(temp.getDegree(), degreeMap);
            }
            List<MarketProducts> qualityList = degreeMap.get(temp.getQuality());
            if (qualityList == null) {
                qualityList = new ArrayList<MarketProducts>();
                degreeMap.put(temp.getQuality(), qualityList);
            }
            qualityList.add(temp);
            if (temp.getDegree() <= MarketProductsCache.maxDegree) {
                continue;
            }
            MarketProductsCache.maxDegree = temp.getDegree();
        }
        for (final Integer key : this.degreeListMap.keySet()) {
            final Map<Integer, List<MarketProducts>> degreeMap = this.degreeListMap.get(key);
            for (int i = 1; i <= 6; ++i) {
                if (degreeMap.get(i) == null || degreeMap.get(i).size() < 1) {
                    throw new RuntimeException("marketProductsCache init fail in quailtyMap, quailty" + i);
                }
            }
        }
    }
    
    public Map<Integer, List<MarketProducts>> getDegreeMap(final int degree) {
        return this.degreeListMap.get(degree);
    }
    
    @Override
	public void clear() {
        super.clear();
        this.degreeListMap.clear();
        MarketProductsCache.maxDegree = 0;
    }
}
