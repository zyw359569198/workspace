package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import java.util.*;
import com.reign.gcld.common.util.*;

@Component("hmGtDropCache")
public class HmGtDropCache extends AbstractCache<Integer, HmGtDrop>
{
    @Autowired
    private SDataLoader dataLoader;
    private List<Integer> exceptOccupys;
    
    public HmGtDropCache() {
        this.exceptOccupys = new ArrayList<Integer>();
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<HmGtDrop> list = this.dataLoader.getModels((Class)HmGtDrop.class);
        for (final HmGtDrop temp : list) {
            super.put((Object)temp.getId(), (Object)temp);
            if (!this.exceptOccupys.contains(temp.getItems())) {
                this.exceptOccupys.add(temp.getItems());
            }
        }
    }
    
    public int getRandomItems() {
        final int type = WebUtil.nextInt(this.exceptOccupys.size());
        return this.exceptOccupys.get(type);
    }
}
