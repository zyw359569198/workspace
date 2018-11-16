package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import java.util.*;

@Component("hmGtMainCache")
public class HmGtMainCache extends AbstractCache<Integer, HmGtMain>
{
    @Autowired
    private SDataLoader dataLoader;
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<HmGtMain> list = this.dataLoader.getModels((Class)HmGtMain.class);
        for (final HmGtMain temp : list) {
            super.put((Object)temp.getLv(), (Object)temp);
        }
    }
}
