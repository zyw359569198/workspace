package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import java.util.*;

@Component("worldOutputPerTimeCache")
public class WorldOutputPerTimeCache extends AbstractCache<Integer, WorldOutputPerTime>
{
    @Autowired
    private SDataLoader dataLoader;
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<WorldOutputPerTime> resultList = this.dataLoader.getModels((Class)WorldOutputPerTime.class);
        for (final WorldOutputPerTime temp : resultList) {
            super.put((Object)temp.getId(), (Object)temp);
        }
    }
}
