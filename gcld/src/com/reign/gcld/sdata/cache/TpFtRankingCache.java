package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.common.log.*;
import java.util.concurrent.*;
import java.util.*;

@Component("tpFtRankingCache")
public class TpFtRankingCache extends AbstractCache<Integer, TpFtRanking>
{
    @Autowired
    private SDataLoader dataLoader;
    private Logger errorLog;
    private Map<Integer, Integer> rankCopperMap;
    private Map<Integer, Integer> rankIronMap;
    private Map<Integer, Integer> rankExpMap;
    
    public TpFtRankingCache() {
        this.errorLog = CommonLog.getLog(TpFtRankingCache.class);
        this.rankCopperMap = new ConcurrentHashMap<Integer, Integer>();
        this.rankIronMap = new ConcurrentHashMap<Integer, Integer>();
        this.rankExpMap = new ConcurrentHashMap<Integer, Integer>();
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<TpFtRanking> resultList = this.dataLoader.getModels((Class)TpFtRanking.class);
        for (final TpFtRanking temp : resultList) {
            super.put((Object)temp.getId(), (Object)temp);
        }
    }
    
    public int getRewardCopper(final int rank) {
        if (this.rankCopperMap.containsKey(rank)) {
            return this.rankCopperMap.get(rank);
        }
        for (final TpFtRanking tfr : this.getModels()) {
            if (tfr.getRkLow() <= rank && rank <= tfr.getRkHigh()) {
                this.rankCopperMap.put(rank, tfr.getRewardCopper());
                return tfr.getRewardCopper();
            }
        }
        this.errorLog.error("class:tpFtRankingCache#method:getRewardCopper#rank:" + rank);
        this.rankCopperMap.put(rank, 0);
        return 0;
    }
    
    public int getRewardIron(final int rank) {
        if (this.rankIronMap.containsKey(rank)) {
            return this.rankIronMap.get(rank);
        }
        for (final TpFtRanking tfr : this.getModels()) {
            if (tfr.getRkLow() <= rank && rank <= tfr.getRkHigh()) {
                this.rankIronMap.put(rank, tfr.getRewardIron());
                return tfr.getRewardIron();
            }
        }
        this.errorLog.error("class:tpFtRankingCache#method:getRewardIron#rank:" + rank);
        this.rankIronMap.put(rank, 0);
        return 0;
    }
    
    public int getRewardExp(final int rank) {
        if (this.rankExpMap.containsKey(rank)) {
            return this.rankExpMap.get(rank);
        }
        for (final TpFtRanking tfr : this.getModels()) {
            if (tfr.getRkLow() <= rank && rank <= tfr.getRkHigh()) {
                this.rankExpMap.put(rank, tfr.getRewardExp());
                return tfr.getRewardExp();
            }
        }
        this.errorLog.error("class:tpFtRankingCache#method:getRewardExp#rank:" + rank);
        this.rankExpMap.put(rank, 0);
        return 0;
    }
    
    @Override
	public void clear() {
        super.clear();
        this.rankCopperMap.clear();
        this.rankIronMap.clear();
        this.rankExpMap.clear();
    }
}
