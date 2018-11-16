package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import java.util.*;

@Component("trainerCache")
public class TrainerCache extends AbstractCache<Integer, Trainer>
{
    @Autowired
    private SDataLoader dataLoader;
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<Trainer> list = this.dataLoader.getModels((Class)Trainer.class);
        for (final Trainer trainer : list) {
            super.put((Object)trainer.getId(), (Object)trainer);
        }
    }
    
    public void setDataLoader(final SDataLoader dataLoader) {
        this.dataLoader = dataLoader;
    }
    
    public SDataLoader getDataLoader() {
        return this.dataLoader;
    }
}
