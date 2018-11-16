package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.common.log.*;
import java.util.*;
import com.reign.gcld.common.util.*;

@Component("tpCoTrewardCache")
public class TpCoTrewardCache extends AbstractCache<Integer, TpCoTreward>
{
    @Autowired
    private SDataLoader dataLoader;
    private static final Logger errorLog;
    
    static {
        errorLog = CommonLog.getLog(TpCoTrewardCache.class);
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<TpCoTreward> list = this.dataLoader.getModels((Class)TpCoTreward.class);
        double rate = 0.0;
        for (final TpCoTreward temp : list) {
            super.put((Object)temp.getId(), (Object)temp);
            rate += temp.getProb();
        }
        if (rate < 1.0) {
            throw new RuntimeException("class:TpCoTrewardCache#methond:afterPropertiesSet#please find huangxl, this rate < 1");
        }
    }
    
    public TpCoTreward getTpCoTreward() {
        double rate = WebUtil.nextDouble();
        for (final TpCoTreward fdr : this.getModels()) {
            if (rate <= fdr.getProb()) {
                return fdr;
            }
            rate -= fdr.getProb();
        }
        TpCoTrewardCache.errorLog.error("class:TpCoTrewardCache#methond:getTpCoTreward#shoud not go there, find huangxl");
        return (TpCoTreward)this.get((Object)this.getModels().size());
    }
}
