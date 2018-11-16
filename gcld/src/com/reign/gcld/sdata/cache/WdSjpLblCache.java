package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.util.*;
import com.reign.gcld.common.log.*;
import java.util.*;
import com.reign.gcld.common.util.*;

@Component("wdSjpLblCache")
public class WdSjpLblCache extends AbstractCache<Integer, WdSjpLbl>
{
    @Autowired
    private SDataLoader dataLoader;
    private static final Logger errorLog;
    private List<Tuple<Double, WdSjpLbl>> probList;
    
    static {
        errorLog = CommonLog.getLog(WdSjpLblCache.class);
    }
    
    public WdSjpLblCache() {
        this.probList = new ArrayList<Tuple<Double, WdSjpLbl>>();
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<WdSjpLbl> resultList = this.dataLoader.getModels((Class)WdSjpLbl.class);
        double accProb = 0.0;
        for (final WdSjpLbl wdSjpXtys : resultList) {
            accProb += wdSjpXtys.getProb();
            final Tuple<Double, WdSjpLbl> tuple = new Tuple();
            tuple.left = accProb;
            tuple.right = wdSjpXtys;
            this.probList.add(tuple);
            super.put((Object)wdSjpXtys.getId(), (Object)wdSjpXtys);
        }
        if (accProb < 0.99) {
            WdSjpLblCache.errorLog.error("class:WdSjpXtysCache#method:afterPropertiesSet#total_prob < 1");
            throw new RuntimeException("class:WdSjpXtysCache#method:afterPropertiesSet#total_prob < 1");
        }
    }
    
    public WdSjpLbl getWdSjpXtys() {
        final double prob = WebUtil.nextDouble();
        for (final Tuple<Double, WdSjpLbl> tuple : this.probList) {
            if (prob <= tuple.left) {
                return tuple.right;
            }
        }
        WdSjpLblCache.errorLog.error("class:WdSjpXtysCache#method:getWdSjpXtys#prob:" + prob);
        return this.probList.get(0).right;
    }
    
    @Override
	public void clear() {
        super.clear();
        this.probList.clear();
    }
}
