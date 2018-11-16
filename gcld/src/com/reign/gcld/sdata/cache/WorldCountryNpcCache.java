package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import java.util.*;

@Component("worldCountryNpcCache")
public class WorldCountryNpcCache extends AbstractCache<Integer, WorldCountryNpc>
{
    @Autowired
    private SDataLoader dataLoader;
    private static Map<Integer, List<WorldCountryNpc>> weiMap;
    private static Map<Integer, List<WorldCountryNpc>> shuMap;
    private static Map<Integer, List<WorldCountryNpc>> wuMap;
    
    static {
        WorldCountryNpcCache.weiMap = new HashMap<Integer, List<WorldCountryNpc>>();
        WorldCountryNpcCache.shuMap = new HashMap<Integer, List<WorldCountryNpc>>();
        WorldCountryNpcCache.wuMap = new HashMap<Integer, List<WorldCountryNpc>>();
    }
    
    public List<WorldCountryNpc> getNpcByDegree(final int forceId, final int degree) {
        if (forceId == 1) {
            return WorldCountryNpcCache.weiMap.get(degree);
        }
        if (forceId == 2) {
            return WorldCountryNpcCache.shuMap.get(degree);
        }
        return WorldCountryNpcCache.wuMap.get(degree);
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<WorldCountryNpc> resultList = this.dataLoader.getModels((Class)WorldCountryNpc.class);
        Map<Integer, List<WorldCountryNpc>> map = null;
        for (final WorldCountryNpc temp : resultList) {
            super.put((Object)temp.getId(), (Object)temp);
            if (temp.getCountry() == 1) {
                map = WorldCountryNpcCache.weiMap;
            }
            else if (temp.getCountry() == 2) {
                map = WorldCountryNpcCache.shuMap;
            }
            else {
                map = WorldCountryNpcCache.wuMap;
            }
            List<WorldCountryNpc> list = null;
            if (map.containsKey(temp.getDegree())) {
                list = map.get(temp.getDegree());
            }
            else {
                list = new ArrayList<WorldCountryNpc>();
                map.put(temp.getDegree(), list);
            }
            list.add(temp);
        }
    }
    
    @Override
	public void clear() {
        super.clear();
        WorldCountryNpcCache.weiMap.clear();
        WorldCountryNpcCache.shuMap.clear();
        WorldCountryNpcCache.wuMap.clear();
    }
}
