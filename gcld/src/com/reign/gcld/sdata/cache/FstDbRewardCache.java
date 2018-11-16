package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.common.log.*;
import java.util.*;
import com.reign.gcld.common.util.*;

@Component("fstDbRewardCache")
public class FstDbRewardCache extends AbstractCache<Integer, FstDbReward>
{
    @Autowired
    private SDataLoader dataLoader;
    private static final Logger errorLog;
    
    static {
        errorLog = CommonLog.getLog(FstDbRewardCache.class);
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<FstDbReward> list = this.dataLoader.getModels((Class)FstDbReward.class);
        double rate = 0.0;
        for (final FstDbReward temp : list) {
            super.put((Object)temp.getId(), (Object)temp);
            rate += temp.getProb();
        }
        if (rate < 1.0) {
            throw new RuntimeException("class:FstDbRewardCache#methond:afterPropertiesSet#plase find huangxl, this rate < 1");
        }
    }
    
    public FstDbReward getFstDbReward() {
        double rate = WebUtil.nextDouble();
        for (final FstDbReward fdr : this.getModels()) {
            if (rate <= fdr.getProb()) {
                return fdr;
            }
            rate -= fdr.getProb();
        }
        FstDbRewardCache.errorLog.error("class:FstDbRewardCache#methond:getFstDbReward#shoud not go there, find huangxl");
        return (FstDbReward)this.get((Object)this.getModels().size());
    }
}
