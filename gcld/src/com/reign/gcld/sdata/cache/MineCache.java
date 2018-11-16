package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import java.util.*;

@Component("mineCache")
public class MineCache extends AbstractCache<Integer, Mine>
{
    @Autowired
    private SDataLoader dataLoader;
    private Map<Integer, List<Mine>> typeMineMap;
    public static int maxPageNum;
    public static int ironForceMineId;
    public static int gemForceMineId;
    
    static {
        MineCache.maxPageNum = 0;
        MineCache.ironForceMineId = 0;
        MineCache.gemForceMineId = 0;
    }
    
    public MineCache() {
        this.typeMineMap = new HashMap<Integer, List<Mine>>();
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<Mine> list = this.dataLoader.getModels((Class)Mine.class);
        for (final Mine mine : list) {
            super.put((Object)mine.getId(), (Object)mine);
            if (mine.getPage() > MineCache.maxPageNum) {
                MineCache.maxPageNum = mine.getPage();
            }
            if (mine.getType() == 3) {
                MineCache.gemForceMineId = mine.getId();
            }
            if (mine.getType() == 1) {
                MineCache.ironForceMineId = mine.getId();
            }
            List<Mine> typeList = this.typeMineMap.get(mine.getType());
            if (typeList == null) {
                typeList = new ArrayList<Mine>();
                this.typeMineMap.put(mine.getType(), typeList);
            }
            typeList.add(mine);
        }
    }
    
    public List<Mine> getMines(final int type, final int pageNum) {
        final List<Mine> result = new ArrayList<Mine>();
        for (final Mine mine : this.typeMineMap.get(type)) {
            if (mine.getPage() == pageNum) {
                result.add(mine);
            }
        }
        return result;
    }
    
    @Override
	public void clear() {
        super.clear();
        this.typeMineMap.clear();
        MineCache.maxPageNum = 0;
        MineCache.ironForceMineId = 0;
        MineCache.gemForceMineId = 0;
    }
}
