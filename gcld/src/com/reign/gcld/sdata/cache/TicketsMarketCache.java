package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import java.util.*;

@Component("ticketsMarketCache")
public class TicketsMarketCache extends AbstractCache<Integer, TicketsMarket>
{
    @Autowired
    private SDataLoader dataLoader;
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<TicketsMarket> resultList = this.dataLoader.getModels((Class)TicketsMarket.class);
        for (final TicketsMarket tc : resultList) {
            this.put((Object)tc.getId(), (Object)tc);
        }
    }
}
