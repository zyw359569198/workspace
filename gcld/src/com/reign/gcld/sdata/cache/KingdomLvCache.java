package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import java.util.*;

@Component("kingdomLvCache")
public class KingdomLvCache extends AbstractCache<Integer, KindomLv>
{
    public int maxLv;
    private List<Integer> expList;
    @Autowired
    private SDataLoader dataLoader;
    
    public KingdomLvCache() {
        this.maxLv = 0;
        this.expList = new ArrayList<Integer>();
    }
    
    public SDataLoader getDataLoader() {
        return this.dataLoader;
    }
    
    public void setDataLoader(final SDataLoader dataLoader) {
        this.dataLoader = dataLoader;
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<KindomLv> kindomLvs = this.dataLoader.getModels((Class)KindomLv.class);
        int curExp = 0;
        for (final KindomLv kl : kindomLvs) {
            super.put((Object)kl.getLv(), (Object)kl);
            if (kl.getLv() > this.maxLv) {
                this.maxLv = kl.getLv();
            }
            this.expList.add(curExp);
            curExp += kl.getExpUpgrade();
        }
    }
    
    public int getBeforeLvExp(final int forceLv) {
        return this.expList.get(forceLv - 1);
    }
    
    public int getExp(final int beforeLv, final int afterLv) {
        int exp = 0;
        for (int i = beforeLv; i < afterLv; ++i) {
            exp += ((KindomLv)this.get((Object)i)).getExpUpgrade();
        }
        return exp;
    }
    
    @Override
	public void clear() {
        super.clear();
        this.expList.clear();
    }
}
