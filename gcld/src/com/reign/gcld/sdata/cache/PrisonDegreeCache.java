package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import java.util.*;

@Component("prisonDegreeCache")
public class PrisonDegreeCache extends AbstractCache<Integer, PrisonDegree>
{
    @Autowired
    private SDataLoader dataLoader;
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<PrisonDegree> resultList = this.dataLoader.getModels((Class)PrisonDegree.class);
        for (final PrisonDegree temp : resultList) {
            super.put((Object)temp.getDegree(), (Object)temp);
        }
    }
}
