package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.common.log.*;
import java.util.*;

@Component("fstDbLveCache")
public class FstDbLveCache extends AbstractCache<Integer, FstDbLve>
{
    @Autowired
    private SDataLoader dataLoader;
    private Logger errorLog;
    private Map<Integer, Double> lvRateMap;
    
    public FstDbLveCache() {
        this.errorLog = CommonLog.getLog(FstDbLveCache.class);
        this.lvRateMap = new HashMap<Integer, Double>();
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<FstDbLve> resultList = this.dataLoader.getModels((Class)FstDbLve.class);
        for (final FstDbLve temp : resultList) {
            super.put((Object)temp.getId(), (Object)temp);
        }
    }
    
    public Double getRate(final int playerLv) {
        if (this.lvRateMap.containsKey(playerLv)) {
            return this.lvRateMap.get(playerLv);
        }
        for (final FstDbLve fsd : this.getModels()) {
            if (fsd.getLvLow() <= playerLv && playerLv <= fsd.getLvHigh()) {
                this.lvRateMap.put(playerLv, fsd.getE());
                return fsd.getE();
            }
        }
        this.errorLog.error("class:FstDbLveCache#method:getRate#playerLv:" + playerLv);
        this.lvRateMap.put(playerLv, 0.0);
        return 0.0;
    }
    
    @Override
	public void clear() {
        super.clear();
        this.lvRateMap.clear();
    }
}
