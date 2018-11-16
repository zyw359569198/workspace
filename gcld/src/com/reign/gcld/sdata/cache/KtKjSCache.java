package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import java.util.*;

@Component("ktKjSCache")
public class KtKjSCache extends AbstractCache<Integer, KtKjS>
{
    @Autowired
    private SDataLoader dataLoader;
    private List<KtKjS> lists;
    
    public KtKjSCache() {
        this.lists = null;
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<KtKjS> list = this.dataLoader.getModels((Class)KtKjS.class);
        this.lists = new ArrayList<KtKjS>();
        for (final KtKjS kjS : list) {
            super.put((Object)kjS.getId(), (Object)kjS);
            this.lists.add(kjS);
        }
    }
    
    public KtKjS getCurSerial(final long lastTime) {
        final long minutes = lastTime / 60000L;
        if (minutes > 120L) {
            return this.getLastSerial();
        }
        int index = 1;
        int miMax = 0;
        for (final KtKjS kjS : this.lists) {
            final int integer = kjS.getId();
            if (minutes < miMax) {
                return (KtKjS)super.get((Object)index);
            }
            index = integer;
            miMax += kjS.getT();
        }
        return (KtKjS)super.get((Object)index);
    }
    
    public long getEndTime(final KtKjS ktKjS, long end) {
        final int id = ktKjS.getId();
        for (int i = this.lists.size() - 1; i >= 0; --i) {
            final KtKjS kjS = this.lists.get(i);
            if (id == kjS.getId()) {
                break;
            }
            end -= kjS.getT() * 60000L;
        }
        return end;
    }
    
    public int isNewSerial(final long lastTime) {
        long lastMinutes = 0L;
        for (final KtKjS ktKjS : this.lists) {
            lastMinutes += ktKjS.getT();
            if (lastTime >= lastMinutes) {
                return ktKjS.getId() + 1;
            }
        }
        return 0;
    }
    
    public KtKjS getLastSerial() {
        return this.lists.get(this.lists.size() - 1);
    }
}
