package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.util.*;
import com.reign.gcld.common.log.*;
import java.util.*;
import com.reign.gcld.common.util.*;

@Component("wdSjpSdlrCache")
public class WdSjpSdlrCache extends AbstractCache<Integer, WdSjpSdlr>
{
    @Autowired
    private SDataLoader dataLoader;
    private static final Logger errorLog;
    private List<Tuple<Double, WdSjpSdlr>> probList;
    
    static {
        errorLog = CommonLog.getLog(WdSjpSdlrCache.class);
    }
    
    public WdSjpSdlrCache() {
        this.probList = new ArrayList<Tuple<Double, WdSjpSdlr>>();
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<WdSjpSdlr> resultList = this.dataLoader.getModels((Class)WdSjpSdlr.class);
        double accProb = 0.0;
        for (final WdSjpSdlr wdSjpSdlr : resultList) {
            accProb += wdSjpSdlr.getProb();
            final Tuple<Double, WdSjpSdlr> tuple = new Tuple();
            tuple.left = accProb;
            tuple.right = wdSjpSdlr;
            this.probList.add(tuple);
            super.put((Object)wdSjpSdlr.getId(), (Object)wdSjpSdlr);
        }
        if (accProb < 0.99) {
            WdSjpSdlrCache.errorLog.error("class:WdSjpSdlrCache#method:afterPropertiesSet#total_prob < 1");
            throw new RuntimeException("class:WdSjpSdlrCache#method:afterPropertiesSet#total_prob < 1");
        }
    }
    
    public WdSjpSdlr getWdSjpSdlr() {
        final double prob = WebUtil.nextDouble();
        for (final Tuple<Double, WdSjpSdlr> tuple : this.probList) {
            if (prob <= tuple.left) {
                return tuple.right;
            }
        }
        WdSjpSdlrCache.errorLog.error("class:WdSjpSdlrCache#method:getWdSjpSdlr#prob:" + prob);
        return this.probList.get(0).right;
    }
    
    @Override
	public void clear() {
        super.clear();
        this.probList.clear();
    }
}
