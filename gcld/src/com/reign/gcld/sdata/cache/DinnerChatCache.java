package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import java.util.*;

@Component("dinnerChatCache")
public class DinnerChatCache extends AbstractCache<Integer, DinnerChat>
{
    @Autowired
    private SDataLoader dataLoader;
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<DinnerChat> resultList = this.dataLoader.getModels((Class)DinnerChat.class);
        for (final DinnerChat temp : resultList) {
            super.put((Object)temp.getId(), (Object)temp);
        }
    }
}
