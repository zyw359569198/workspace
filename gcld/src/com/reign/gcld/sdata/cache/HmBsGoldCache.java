package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import java.util.*;

@Component("hmBsGoldCache")
public class HmBsGoldCache extends AbstractCache<Integer, HmBsGold>
{
    @Autowired
    private SDataLoader dataLoader;
    private Map<Integer, Integer> bsIdMaxLvMap;
    private Map<String, HmBsGold> bsIdManLvHBGMap;
    
    public HmBsGoldCache() {
        this.bsIdMaxLvMap = new HashMap<Integer, Integer>();
        this.bsIdManLvHBGMap = new HashMap<String, HmBsGold>();
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<HmBsGold> resultList = this.dataLoader.getModels((Class)HmBsGold.class);
        for (final HmBsGold hbm : resultList) {
            super.put((Object)hbm.getId(), (Object)hbm);
            final Integer maxLv = this.bsIdMaxLvMap.get(hbm.getBsId());
            if (maxLv == null || hbm.getManLv() > maxLv) {
                this.bsIdMaxLvMap.put(hbm.getBsId(), hbm.getManLv());
            }
            this.bsIdManLvHBGMap.put(this.getKey(hbm.getBsId(), hbm.getManLv()), hbm);
        }
    }
    
    public int getMaxLv(final int bsId) {
        return this.bsIdMaxLvMap.get(bsId);
    }
    
    public HmBsGold getHmBsGold(final int bsId, final int manLv) {
        final String key = this.getKey(bsId, manLv);
        return this.bsIdManLvHBGMap.get(key);
    }
    
    private String getKey(final int bsId, final int manLv) {
        return String.valueOf(bsId) + "_" + manLv;
    }
    
    @Override
	public void clear() {
        super.clear();
        this.bsIdMaxLvMap.clear();
        this.bsIdManLvHBGMap.clear();
    }
}
