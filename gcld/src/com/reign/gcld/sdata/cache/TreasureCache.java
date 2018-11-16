package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import java.util.*;

@Component("treasureCache")
public class TreasureCache extends AbstractCache<Integer, Treasure>
{
    @Autowired
    private SDataLoader dataLoader;
    private Map<Integer, List<Treasure>> typeTreasures;
    
    public TreasureCache() {
        this.typeTreasures = new HashMap<Integer, List<Treasure>>();
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<Treasure> list = this.dataLoader.getModels((Class)Treasure.class);
        for (final Treasure temp : list) {
            super.put((Object)temp.getId(), (Object)temp);
            List<Treasure> typeList = this.typeTreasures.get(temp.getType());
            if (typeList == null) {
                typeList = new ArrayList<Treasure>();
                this.typeTreasures.put(temp.getType(), typeList);
            }
            typeList.add(temp);
        }
    }
    
    public List<Treasure> getTreasuresByType(final int type) {
        return this.typeTreasures.get(type);
    }
    
    @Override
	public void clear() {
        super.clear();
        this.typeTreasures.clear();
    }
}
