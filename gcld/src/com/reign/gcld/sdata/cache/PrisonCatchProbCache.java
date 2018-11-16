package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import java.util.*;
import com.reign.gcld.common.util.*;

@Component("prisonCatchProbCache")
public class PrisonCatchProbCache extends AbstractCache<Integer, PrisonCatchProb>
{
    @Autowired
    private SDataLoader dataLoader;
    private Map<Integer, List<PrisonCatchProb>> rateListMap;
    
    public PrisonCatchProbCache() {
        this.rateListMap = new HashMap<Integer, List<PrisonCatchProb>>();
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<PrisonCatchProb> resultList = this.dataLoader.getModels((Class)PrisonCatchProb.class);
        for (final PrisonCatchProb temp : resultList) {
            super.put((Object)temp.getId(), (Object)temp);
            List<PrisonCatchProb> list = this.rateListMap.get(temp.getN());
            if (list == null) {
                list = new ArrayList<PrisonCatchProb>();
                this.rateListMap.put(temp.getN(), list);
            }
            list.add(temp);
        }
    }
    
    private double getRate(final int n, final int prisonLv) {
        final List<PrisonCatchProb> list = this.rateListMap.get(n);
        if (list == null) {
            return 0.0;
        }
        for (final PrisonCatchProb pcp : list) {
            if (prisonLv >= pcp.getPrisonLowLv() && prisonLv <= pcp.getPrisonHighLv()) {
                return pcp.getProb();
            }
        }
        return 0.0;
    }
    
    public int getQuality(final int n, final int prisonLv) {
        final List<PrisonCatchProb> list = this.rateListMap.get(n);
        if (list == null) {
            return 5;
        }
        for (final PrisonCatchProb pcp : list) {
            if (prisonLv >= pcp.getPrisonLowLv() && prisonLv <= pcp.getPrisonHighLv()) {
                return pcp.getProbLv();
            }
        }
        return 5;
    }
    
    public boolean canCatch(final int n, final int prisonLv, final int killNum) {
        return this.getRate(n, prisonLv) * this.getKillNumRate(killNum) > WebUtil.nextDouble();
    }
    
    private double getKillNumRate(final int killNum) {
        final double rate = 1.0 - (killNum - 1) * 0.1;
        return (rate < 0.3) ? 0.3 : rate;
    }
    
    @Override
	public void clear() {
        super.clear();
        this.rateListMap.clear();
    }
}
