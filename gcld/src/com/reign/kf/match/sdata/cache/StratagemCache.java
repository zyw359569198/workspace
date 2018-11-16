package com.reign.kf.match.sdata.cache;

import com.reign.kf.match.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import java.util.*;

@Component("stratagemCache")
public class StratagemCache extends AbstractCache<Integer, Stratagem>
{
    private Map<String, List<Integer>> trickMap;
    static StratagemCache stratagemCache;
    @Autowired
    private SDataLoader dataLoader;
    
    static {
        StratagemCache.stratagemCache = null;
    }
    
    public StratagemCache() {
        this.trickMap = new HashMap<String, List<Integer>>();
    }
    
    public Map<String, List<Integer>> getTrickMap() {
        return this.trickMap;
    }
    
    public void setTrickMap(final Map<String, List<Integer>> trickMap) {
        this.trickMap = trickMap;
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<Stratagem> list = this.dataLoader.getModels((Class)Stratagem.class);
        for (final Stratagem c : list) {
            super.put((Object)c.getId(), (Object)c);
            if (this.trickMap.containsKey(c.getType())) {
                this.trickMap.get(c.getType()).add(c.getId());
            }
            else {
                final List<Integer> list2 = new ArrayList<Integer>();
                list2.add(c.getId());
                this.trickMap.put(c.getType(), list2);
            }
        }
        StratagemCache.stratagemCache = this;
    }
    
    public static Stratagem getStgById(final int sId) {
        return (Stratagem)StratagemCache.stratagemCache.get((Object)sId);
    }
    
    @Override
	public void clear() {
        super.clear();
        this.trickMap.clear();
    }
}
