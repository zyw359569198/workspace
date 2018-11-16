package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import java.util.*;

@Component("farmCoeCache")
public class FarmCoeCache extends AbstractCache<Integer, FarmCoe>
{
    @Autowired
    private SDataLoader dataLoader;
    private List<FarmCoe> fcList;
    
    public FarmCoeCache() {
        this.fcList = new ArrayList<FarmCoe>();
    }
    
    public SDataLoader getDataLoader() {
        return this.dataLoader;
    }
    
    public void setDataLoader(final SDataLoader dataLoader) {
        this.dataLoader = dataLoader;
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<FarmCoe> farmCoes = this.dataLoader.getModels((Class)FarmCoe.class);
        for (final FarmCoe coe : farmCoes) {
            super.put((Object)coe.getId(), (Object)coe);
            this.fcList.add(coe);
        }
    }
    
    public int getByLv(final int lv) {
        for (final FarmCoe coe : this.fcList) {
            if (lv >= coe.getLvLow() && lv <= coe.getLvHigh()) {
                return coe.getCoe();
            }
        }
        return 1;
    }
}
