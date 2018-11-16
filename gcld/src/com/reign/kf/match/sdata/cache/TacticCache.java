package com.reign.kf.match.sdata.cache;

import com.reign.kf.match.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import java.util.*;

@Component("tacticCache")
public class TacticCache extends AbstractCache<Integer, Tactic>
{
    @Autowired
    private SDataLoader dataLoader;
    public static TacticCache staticTacticCache;
    
    static {
        TacticCache.staticTacticCache = new TacticCache();
    }
    
    public static Tactic getTacticById(final int tacticId) {
        return (Tactic)TacticCache.staticTacticCache.get((Object)tacticId);
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<Tactic> resultList = this.dataLoader.getModels((Class)Tactic.class);
        for (final Tactic temp : resultList) {
            super.put((Object)temp.getId(), (Object)temp);
        }
        TacticCache.staticTacticCache = this;
    }
}
