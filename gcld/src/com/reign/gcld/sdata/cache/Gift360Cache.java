package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import java.util.*;

@Component("gift360Cache")
public class Gift360Cache extends AbstractCache<Integer, Gift360>
{
    @Autowired
    private SDataLoader dataLoader;
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<Gift360> list = this.dataLoader.getModels((Class)Gift360.class);
        for (final Gift360 temp : list) {
            super.put((Object)temp.getId(), (Object)temp);
        }
    }
}
