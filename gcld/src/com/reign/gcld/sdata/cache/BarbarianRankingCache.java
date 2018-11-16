package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import java.util.*;

@Component("barbarianRankingCache")
public class BarbarianRankingCache extends AbstractCache<Integer, BarbarainRanking>
{
    @Autowired
    private SDataLoader dataLoader;
    
    public SDataLoader getDataLoader() {
        return this.dataLoader;
    }
    
    public void setDataLoader(final SDataLoader dataLoader) {
        this.dataLoader = dataLoader;
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<BarbarainRanking> list = this.dataLoader.getModels((Class)BarbarainRanking.class);
        for (final BarbarainRanking ranking : list) {
            super.put((Object)ranking.getId(), (Object)ranking);
        }
    }
    
    public BarbarainRanking getTaskRanking(final int rank, final int forceLv) {
        for (final Integer id : super.getCacheMap().keySet()) {
            final BarbarainRanking ranking = (BarbarainRanking)super.get((Object)id);
            if (ranking.getBarbarainLv() != forceLv) {
                continue;
            }
            if (rank <= ranking.getLowLv() && rank >= ranking.getHighLv()) {
                return ranking;
            }
        }
        return null;
    }
}
