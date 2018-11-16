package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import java.util.*;

@Component("tavernStatCache")
public class TavernStatCache extends AbstractCache<Integer, TavernStat>
{
    @Autowired
    private SDataLoader dataLoader;
    private Map<Integer, List<TavernStat>> tavernStatMap;
    
    public TavernStatCache() {
        this.tavernStatMap = new HashMap<Integer, List<TavernStat>>();
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<TavernStat> resultList = this.dataLoader.getModels((Class)TavernStat.class);
        for (final TavernStat tavernStat : resultList) {
            List<TavernStat> list = this.tavernStatMap.get(tavernStat.getPreStat());
            if (list == null) {
                list = new ArrayList<TavernStat>();
                this.tavernStatMap.put(tavernStat.getPreStat(), list);
            }
            list.add(tavernStat);
        }
    }
    
    public List<TavernStat> getTavernStatList(final int preState) {
        return this.tavernStatMap.get(preState);
    }
    
    @Override
	public void clear() {
        super.clear();
        this.tavernStatMap.clear();
    }
}
