package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import java.util.*;

@Component("fbGuideCache")
public class FbGuideCache extends AbstractCache<Integer, FbGuide>
{
    @Autowired
    private SDataLoader dataLoader;
    private Map<Integer, List<FbGuide>> powerToListMap;
    
    public FbGuideCache() {
        this.powerToListMap = new HashMap<Integer, List<FbGuide>>();
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<FbGuide> result = this.dataLoader.getModels((Class)FbGuide.class);
        for (final FbGuide temp : result) {
            final int powerId = temp.getPowerId();
            List<FbGuide> list = this.powerToListMap.get(powerId);
            if (list == null) {
                list = new ArrayList<FbGuide>();
                this.powerToListMap.put(powerId, list);
            }
            list.add(temp);
            super.put((Object)temp.getId(), (Object)temp);
        }
        final Comparator<FbGuide> fbGuideComparator = new Comparator<FbGuide>() {
            @Override
            public int compare(final FbGuide o1, final FbGuide o2) {
                return o1.getId() - o2.getId();
            }
        };
        for (final List<FbGuide> list2 : this.powerToListMap.values()) {
            Collections.sort(list2, fbGuideComparator);
        }
    }
    
    public List<FbGuide> getListByPowerId(final int powerId) {
        return this.powerToListMap.get(powerId);
    }
    
    @Override
	public void clear() {
        super.clear();
        this.powerToListMap.clear();
    }
}
