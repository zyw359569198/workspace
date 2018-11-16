package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import java.util.*;

@Component("KtHjNpcCache")
public class KtHjNpcCache extends AbstractCache<Integer, KtHjNpc>
{
    @Autowired
    private SDataLoader dataLoader;
    private Map<String, List<KtHjNpc>> ktHjNpcMap;
    
    public KtHjNpcCache() {
        this.ktHjNpcMap = new HashMap<String, List<KtHjNpc>>();
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<KtHjNpc> resultList = this.dataLoader.getModels((Class)KtHjNpc.class);
        for (final KtHjNpc ktHjNpc : resultList) {
            super.put((Object)ktHjNpc.getId(), (Object)ktHjNpc);
            final String key = ktHjNpc.getKindomLv() + "_" + ktHjNpc.getType();
            List<KtHjNpc> list = this.ktHjNpcMap.get(key);
            if (list == null) {
                list = new ArrayList<KtHjNpc>();
            }
            list.add(ktHjNpc);
            this.ktHjNpcMap.put(key, list);
        }
    }
    
    public List<KtHjNpc> getBylvAndType(final int lv, final int type) {
        final String key = String.valueOf(lv) + "_" + type;
        return this.ktHjNpcMap.get(key);
    }
}
