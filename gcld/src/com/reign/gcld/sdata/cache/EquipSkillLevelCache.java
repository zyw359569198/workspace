package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import java.util.*;

@Component("equipSkillLevelCache")
public class EquipSkillLevelCache extends AbstractCache<Integer, EquipSkillLv>
{
    @Autowired
    private SDataLoader dataLoader;
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<EquipSkillLv> esls = this.dataLoader.getModels((Class)EquipSkillLv.class);
        for (final EquipSkillLv esl : esls) {
            super.put((Object)esl.getLv(), (Object)esl);
        }
    }
    
    public void setDataLoader(final SDataLoader dataLoader) {
        this.dataLoader = dataLoader;
    }
    
    public SDataLoader getDataLoader() {
        return this.dataLoader;
    }
}
