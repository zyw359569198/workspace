package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.common.log.*;
import java.util.*;
import com.reign.gcld.common.util.*;

@Component("tpFtTrewardCache")
public class TpFtTrewardCache extends AbstractCache<Integer, TpFtTreward>
{
    @Autowired
    private SDataLoader dataLoader;
    private static final Logger errorLog;
    
    static {
        errorLog = CommonLog.getLog(TpFtTrewardCache.class);
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<TpFtTreward> list = this.dataLoader.getModels((Class)TpFtTreward.class);
        double rate = 0.0;
        for (final TpFtTreward temp : list) {
            super.put((Object)temp.getId(), (Object)temp);
            rate += temp.getProb();
        }
        if (rate < 0.99) {
            throw new RuntimeException("class:TpFtTrewardCache#methond:afterPropertiesSet#please find huangxl, this rate < 0.99");
        }
    }
    
    public TpFtTreward getTpFtTreward() {
        double rate = WebUtil.nextDouble();
        for (final TpFtTreward fdr : this.getModels()) {
            if (rate <= fdr.getProb()) {
                return fdr;
            }
            rate -= fdr.getProb();
        }
        TpFtTrewardCache.errorLog.error("class:TpFtTrewardCache#methond:getTpFtTreward#shoud not go there, find huangxl");
        return (TpFtTreward)this.get((Object)this.getModels().size());
    }
}
