package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import java.util.*;

@Component("stringCCache")
public class StringCCache extends AbstractCache<Integer, StringC>
{
    @Autowired
    private SDataLoader dataLoader;
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<StringC> resultList = this.dataLoader.getModels((Class)StringC.class);
        for (final StringC temp : resultList) {
            super.put((Object)temp.getId(), (Object)temp);
        }
    }
}
