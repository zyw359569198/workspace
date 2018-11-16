package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.util.*;
import com.reign.gcld.common.log.*;
import java.util.*;
import com.reign.gcld.common.util.*;

@Component("wdSjpXtysCache")
public class WdSjpXtysCache extends AbstractCache<Integer, WdSjpXtys>
{
    @Autowired
    private SDataLoader dataLoader;
    private static final Logger errorLog;
    private List<Tuple<Double, WdSjpXtys>> probList;
    
    static {
        errorLog = CommonLog.getLog(WdSjpXtysCache.class);
    }
    
    public WdSjpXtysCache() {
        this.probList = new ArrayList<Tuple<Double, WdSjpXtys>>();
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<WdSjpXtys> resultList = this.dataLoader.getModels((Class)WdSjpXtys.class);
        double accProb = 0.0;
        for (final WdSjpXtys wdSjpXtys : resultList) {
            accProb += wdSjpXtys.getProb();
            final Tuple<Double, WdSjpXtys> tuple = new Tuple();
            tuple.left = accProb;
            tuple.right = wdSjpXtys;
            this.probList.add(tuple);
            super.put((Object)wdSjpXtys.getId(), (Object)wdSjpXtys);
        }
        if (accProb < 0.99) {
            WdSjpXtysCache.errorLog.error("class:WdSjpXtysCache#method:afterPropertiesSet#total_prob < 1");
            throw new RuntimeException("class:WdSjpXtysCache#method:afterPropertiesSet#total_prob < 1");
        }
    }
    
    public WdSjpXtys getWdSjpXtys() {
        final double prob = WebUtil.nextDouble();
        for (final Tuple<Double, WdSjpXtys> tuple : this.probList) {
            if (prob <= tuple.left) {
                return tuple.right;
            }
        }
        WdSjpXtysCache.errorLog.error("class:WdSjpXtysCache#method:getWdSjpXtys#prob:" + prob);
        return this.probList.get(0).right;
    }
    
    @Override
	public void clear() {
        super.clear();
        this.probList.clear();
    }
}
