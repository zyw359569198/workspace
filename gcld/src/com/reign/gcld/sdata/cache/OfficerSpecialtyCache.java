package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import java.util.*;

@Component("officerSpecialtyCache")
public class OfficerSpecialtyCache extends AbstractCache<Integer, OfficerSpecialty>
{
    @Autowired
    private SDataLoader dataLoader;
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<OfficerSpecialty> list = this.dataLoader.getModels((Class)OfficerSpecialty.class);
        for (final OfficerSpecialty temp : list) {
            super.put((Object)temp.getId(), (Object)temp);
        }
    }
}
