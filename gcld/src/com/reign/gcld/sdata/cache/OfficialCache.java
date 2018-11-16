package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import java.util.*;

@Component("officialCache")
public class OfficialCache extends AbstractCache<Integer, Official>
{
    @Autowired
    private SDataLoader dataLoader;
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<Official> list = this.dataLoader.getModels((Class)Official.class);
        for (final Official temp : list) {
            super.put((Object)temp.getId(), (Object)temp);
        }
    }
}
