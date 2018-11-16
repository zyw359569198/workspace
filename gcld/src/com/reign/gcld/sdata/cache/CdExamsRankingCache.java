package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import java.util.concurrent.*;
import java.util.*;

@Component("cdExamsRankingCache")
public class CdExamsRankingCache extends AbstractCache<Integer, CdExamsRanking>
{
    @Autowired
    private SDataLoader dataLoader;
    private Map<Integer, Double> lvRewardMap;
    
    public CdExamsRankingCache() {
        this.lvRewardMap = new ConcurrentHashMap<Integer, Double>();
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<CdExamsRanking> list = this.dataLoader.getModels((Class)CdExamsRanking.class);
        for (final CdExamsRanking temp : list) {
            super.put((Object)temp.getId(), (Object)temp);
        }
    }
    
    public Double getRewardRate(final int rank) {
        if (this.lvRewardMap.containsKey(rank)) {
            return this.lvRewardMap.get(rank);
        }
        final int size = this.getModels().size();
        double e = 0.0;
        for (int i = 1; i <= size; ++i) {
            final CdExamsRanking cer = (CdExamsRanking)this.get((Object)i);
            if (cer.getHighLv() <= rank && rank <= cer.getLowLv()) {
                e = cer.getE();
                break;
            }
        }
        this.lvRewardMap.put(rank, e);
        return e;
    }
    
    @Override
	public void clear() {
        super.clear();
        this.lvRewardMap.clear();
    }
}
