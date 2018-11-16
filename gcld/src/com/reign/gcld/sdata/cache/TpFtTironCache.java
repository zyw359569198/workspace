package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.common.log.*;
import java.util.*;
import com.reign.gcld.common.util.*;

@Component("tpFtTironCache")
public class TpFtTironCache extends AbstractCache<Integer, TpFtTiron>
{
    @Autowired
    private SDataLoader dataLoader;
    private static final Logger errorLog;
    
    static {
        errorLog = CommonLog.getLog(WdSjpXtysCache.class);
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        double prob = 0.0;
        final List<TpFtTiron> resultList = this.dataLoader.getModels((Class)TpFtTiron.class);
        for (final TpFtTiron tft : resultList) {
            super.put((Object)tft.getId(), (Object)tft);
            prob += tft.getProb();
        }
        if (prob < 0.99) {
            TpFtTironCache.errorLog.error("class:TpFtTironCache#method:afterPropertiesSet#total_prob < 1");
            throw new RuntimeException("class:TpFtTironCache#method:afterPropertiesSet#total_prob < 1");
        }
    }
    
    public TpFtTiron getTpFtTiron() {
        double prob = WebUtil.nextDouble();
        for (int size = this.getModels().size(), i = 1; i <= size; ++i) {
            final TpFtTiron temp = (TpFtTiron)this.get((Object)i);
            if (prob <= temp.getProb()) {
                return temp;
            }
            prob -= temp.getProb();
        }
        TpFtTironCache.errorLog.error("class:TpFtTironCache#method:getTpFtTiron#prob:" + prob);
        return (TpFtTiron)this.get((Object)1);
    }
}
