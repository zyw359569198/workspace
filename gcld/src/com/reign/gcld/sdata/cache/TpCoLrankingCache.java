package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.common.log.*;
import java.util.concurrent.*;
import java.util.*;

@Component("tpCoLrankingCache")
public class TpCoLrankingCache extends AbstractCache<Integer, TpCoLranking>
{
    @Autowired
    private SDataLoader dataLoader;
    private Logger errorLog;
    private Map<Integer, Integer> rankExpMap;
    
    public TpCoLrankingCache() {
        this.errorLog = CommonLog.getLog(TpCoLrankingCache.class);
        this.rankExpMap = new ConcurrentHashMap<Integer, Integer>();
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<TpCoLranking> resultList = this.dataLoader.getModels((Class)TpCoLranking.class);
        for (final TpCoLranking temp : resultList) {
            super.put((Object)temp.getId(), (Object)temp);
        }
    }
    
    public int getRewardExp(final int rank) {
        if (this.rankExpMap.containsKey(rank)) {
            return this.rankExpMap.get(rank);
        }
        for (final TpCoLranking tcl : this.getModels()) {
            if (tcl.getRkLow() <= rank && rank <= tcl.getRkHigh()) {
                this.rankExpMap.put(rank, tcl.getRewardExp());
                return tcl.getRewardExp();
            }
        }
        this.errorLog.error("class:TpCoLrankingCache#method:getRewardExp#rank:" + rank);
        this.rankExpMap.put(rank, 0);
        return 0;
    }
    
    @Override
	public void clear() {
        super.clear();
        this.rankExpMap.clear();
    }
}
