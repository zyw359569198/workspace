package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import java.util.*;

@Component("officerAffairCache")
public class OfficerAffairCache extends AbstractCache<Integer, OfficerAffair>
{
    @Autowired
    private SDataLoader dataLoader;
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<OfficerAffair> list = this.dataLoader.getModels((Class)OfficerAffair.class);
        for (final OfficerAffair temp : list) {
            super.put((Object)temp.getId(), (Object)temp);
        }
    }
}
