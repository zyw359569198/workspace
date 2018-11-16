package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import java.util.*;

@Component("prisonLvCache")
public class PrisonLvCache extends AbstractCache<Integer, PrisonLv>
{
    @Autowired
    private SDataLoader dataLoader;
    Map<Integer, Integer> lvItemsIdMap;
    
    public PrisonLvCache() {
        this.lvItemsIdMap = new HashMap<Integer, Integer>();
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<PrisonLv> resultList = this.dataLoader.getModels((Class)PrisonLv.class);
        for (final PrisonLv temp : resultList) {
            super.put((Object)temp.getPrisonLv(), (Object)temp);
            this.lvItemsIdMap.put(temp.getPrisonLv(), temp.getDrawing());
        }
    }
    
    public Integer getItemsId(final int prisonLv) {
        return this.lvItemsIdMap.get(prisonLv);
    }
    
    @Override
	public void clear() {
        super.clear();
        this.lvItemsIdMap.clear();
    }
}
