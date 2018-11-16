package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import java.util.*;

@Component("fightRewardCoeCache")
public class FightRewardCoeCache extends AbstractCache<Integer, FightRewardCoe>
{
    @Autowired
    private SDataLoader dataLoader;
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<FightRewardCoe> resultList = this.dataLoader.getModels((Class)FightRewardCoe.class);
        for (final FightRewardCoe temp : resultList) {
            super.put((Object)temp.getId(), (Object)temp);
        }
    }
}
