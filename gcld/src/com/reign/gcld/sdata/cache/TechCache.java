package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import java.util.*;

@Component("techCache")
public class TechCache extends AbstractCache<Integer, Tech>
{
    @Autowired
    private SDataLoader dataLoader;
    private List<Tech> techDropList;
    
    public TechCache() {
        this.techDropList = new ArrayList<Tech>();
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<Tech> resultList = this.dataLoader.getModels((Class)Tech.class);
        for (final Tech temp : resultList) {
            super.put((Object)temp.getId(), (Object)temp);
            if (temp.getDropIndex() > 0) {
                this.techDropList.add(temp);
            }
        }
        Collections.sort(this.techDropList, new Comparator<Tech>() {
            @Override
            public int compare(final Tech o1, final Tech o2) {
                return o1.getDropIndex() - o2.getDropIndex();
            }
        });
    }
    
    public Tech getDropTechByDropIndex(final int dropIndex) {
        if (dropIndex < 0 || dropIndex >= this.techDropList.size()) {
            return null;
        }
        return this.techDropList.get(dropIndex);
    }
    
    @Override
	public void clear() {
        super.clear();
        this.techDropList.clear();
    }
}
