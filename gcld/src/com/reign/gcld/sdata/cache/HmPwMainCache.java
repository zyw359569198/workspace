package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import java.util.*;

@Component("hmPwMainCache")
public class HmPwMainCache extends AbstractCache<Integer, HmPwMain>
{
    @Autowired
    private SDataLoader dataLoader;
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<HmPwMain> resultList = this.dataLoader.getModels((Class)HmPwMain.class);
        for (final HmPwMain wdSjpHy : resultList) {
            super.put((Object)wdSjpHy.getId(), (Object)wdSjpHy);
        }
    }
}
