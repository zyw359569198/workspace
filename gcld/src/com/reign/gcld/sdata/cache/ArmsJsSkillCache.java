package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import java.util.*;

@Component("armsJsSkillCache")
public class ArmsJsSkillCache extends AbstractCache<Integer, ArmsJsSkill>
{
    @Autowired
    private SDataLoader dataLoader;
    public static int SKILL_NUM;
    
    static {
        ArmsJsSkillCache.SKILL_NUM = 0;
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<ArmsJsSkill> list = this.dataLoader.getModels((Class)ArmsJsSkill.class);
        for (final ArmsJsSkill temp : list) {
            super.put((Object)temp.getId(), (Object)temp);
            ++ArmsJsSkillCache.SKILL_NUM;
        }
    }
    
    @Override
	public void clear() {
        super.clear();
        ArmsJsSkillCache.SKILL_NUM = 0;
    }
}
