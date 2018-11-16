package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import java.util.*;

@Component("ktSdRankingCache")
public class KtSdRankingCache extends AbstractCache<Integer, KtSdRanking>
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
        final List<KtSdRanking> list = this.dataLoader.getModels((Class)KtSdRanking.class);
        for (final KtSdRanking ktSdRanking : list) {
            super.put((Object)ktSdRanking.getId(), (Object)ktSdRanking);
        }
    }
}
