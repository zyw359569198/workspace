package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.rank.domain.*;
import java.util.*;
import com.reign.gcld.common.util.*;

@Component("ktSCache")
public class KtSCache extends AbstractCache<Integer, KtS>
{
    @Autowired
    private SDataLoader dataLoader;
    private List<TaskInit> inits;
    
    public KtSCache() {
        this.inits = null;
    }
    
    public SDataLoader getDataLoader() {
        return this.dataLoader;
    }
    
    public void setDataLoader(final SDataLoader dataLoader) {
        this.dataLoader = dataLoader;
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        this.inits = new ArrayList<TaskInit>();
        final List<KtS> list = this.dataLoader.getModels((Class)KtS.class);
        int day = 1;
        int i = 0;
        for (final KtS ktS : list) {
            ++i;
            super.put((Object)ktS.getId(), (Object)ktS);
            final int composedId = day * 100 + i;
            final TaskInit taskInit = new TaskInit();
            taskInit.setId(composedId);
            taskInit.setType(ktS.getKtType());
            if (!this.inits.contains(taskInit)) {
                this.inits.add(taskInit);
            }
            if (i == 3) {
                i = 0;
                ++day;
            }
        }
    }
    
    public List<TaskInit> getInits(final int day) {
        final List<Integer> list = new ArrayList<Integer>();
        list.add(4);
        list.add(5);
        list.add(6);
        list.add(8);
        list.add(7);
        list.add(9);
        list.add(10);
        Collections.shuffle(list);
        final List<TaskInit> inits = new ArrayList<TaskInit>();
        final Calendar calendar = Calendar.getInstance();
        for (int num = 7 - (calendar.get(7) - 1), i = 0; i < num; ++i) {
            final int ram = WebUtil.nextInt(3) + 1;
            for (int j = 1; j <= 3; ++j) {
                final TaskInit ti = new TaskInit();
                ti.setId((day + i) * 100 + j);
                if (ram == j) {
                    ti.setType(list.get(i));
                }
                else {
                    ti.setType(1);
                }
                inits.add(ti);
            }
        }
        return inits;
    }
}
