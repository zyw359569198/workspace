package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import java.util.*;

@Component("chargeitemCache")
public class ChargeitemCache extends AbstractCache<Integer, Chargeitem>
{
    @Autowired
    private SDataLoader dataLoader;
    private Map<Integer, List<Chargeitem>> lvMap;
    public static int size;
    
    static {
        ChargeitemCache.size = 0;
    }
    
    public ChargeitemCache() {
        this.lvMap = new HashMap<Integer, List<Chargeitem>>();
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<Chargeitem> resultList = this.dataLoader.getModels((Class)Chargeitem.class);
        for (final Chargeitem chargeitem : resultList) {
            super.put((Object)chargeitem.getId(), (Object)chargeitem);
            if (ChargeitemCache.size < chargeitem.getId()) {
                ChargeitemCache.size = chargeitem.getId();
            }
            if (chargeitem.getIfShow() == 1) {
                List<Chargeitem> list = this.lvMap.get(chargeitem.getLv());
                if (list == null) {
                    list = new ArrayList<Chargeitem>();
                }
                list.add(chargeitem);
                this.lvMap.put(chargeitem.getLv(), list);
            }
        }
    }
    
    @Override
	public void clear() {
        super.clear();
        this.lvMap.clear();
    }
    
    public List<Chargeitem> getLvList(final int lv) {
        return this.lvMap.get(lv);
    }
}
