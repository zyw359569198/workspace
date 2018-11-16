package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.battle.reward.*;
import java.util.*;

@Component("tacticCache")
public class TacticCache extends AbstractCache<Integer, Tactic>
{
    @Autowired
    private SDataLoader dataLoader;
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<Tactic> resultList = this.dataLoader.getModels((Class)Tactic.class);
        for (final Tactic temp : resultList) {
            RewardFactory.getInstance().getReward(temp);
            super.put((Object)temp.getId(), (Object)temp);
        }
    }
}
