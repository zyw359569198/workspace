package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import java.util.*;

@Component("kindomTaskCityCache")
public class KindomTaskCityCache extends AbstractCache<Integer, KindomTaskCity>
{
    @Autowired
    private SDataLoader dataLoader;
    
    public SDataLoader getDataLoader() {
        return this.dataLoader;
    }
    
    public void setDataLoader(final SDataLoader dataLoader) {
        this.dataLoader = dataLoader;
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<KindomTaskCity> list = this.dataLoader.getModels((Class)KindomTaskCity.class);
        for (final KindomTaskCity city : list) {
            super.put((Object)city.getCityId(), (Object)city);
        }
    }
}
