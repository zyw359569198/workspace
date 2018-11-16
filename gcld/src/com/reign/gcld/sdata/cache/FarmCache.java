package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import java.util.*;

@Component("farmCache")
public class FarmCache extends AbstractCache<Integer, Farm>
{
    @Autowired
    private SDataLoader dataLoader;
    private List<Integer> lvSum;
    
    public FarmCache() {
        this.lvSum = new ArrayList<Integer>();
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<Farm> list = this.dataLoader.getModels((Class)Farm.class);
        int sum = 0;
        for (final Farm farm : list) {
            super.put((Object)farm.getLv(), (Object)farm);
            sum += farm.getUpCopper();
            this.lvSum.add(sum);
        }
    }
    
    @Override
	public void clear() {
        super.clear();
        this.lvSum.clear();
    }
    
    public int getLvBySum(final long sum) {
        for (int i = 0; i < this.lvSum.size() - 1; ++i) {
            final int value1 = this.lvSum.get(i);
            final int value2 = this.lvSum.get(i + 1);
            if (sum >= value1 && sum < value2) {
                return i + 1;
            }
            if (i == 0 && sum < value1) {
                return 0;
            }
            if (i == this.lvSum.size() - 2 && sum >= value2) {
                return this.lvSum.size();
            }
        }
        return 0;
    }
    
    public int getSumByLv(final int lv) {
        if (lv >= this.lvSum.size()) {
            return this.lvSum.get(this.lvSum.size() - 1);
        }
        return this.lvSum.get(lv - 1);
    }
    
    public int getMaxLv() {
        return this.lvSum.size();
    }
}
