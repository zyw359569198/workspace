package com.reign.kf.match.sdata.cache;

import com.reign.kf.match.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import java.util.*;

@Component("fightRewardCoeCache")
public class FightRewardCoeCache extends AbstractCache<Integer, FightRewardCoe>
{
    @Autowired
    private SDataLoader dataLoader;
    private static FightRewardCoeCache fightRewardCoeCache;
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<FightRewardCoe> resultList = this.dataLoader.getModels((Class)FightRewardCoe.class);
        for (final FightRewardCoe temp : resultList) {
            super.put((Object)temp.getId(), (Object)temp);
        }
        FightRewardCoeCache.fightRewardCoeCache = this;
    }
    
    public static FightRewardCoe getFightRewardCoeById(final int id) {
        return (FightRewardCoe)FightRewardCoeCache.fightRewardCoeCache.get((Object)id);
    }
}
