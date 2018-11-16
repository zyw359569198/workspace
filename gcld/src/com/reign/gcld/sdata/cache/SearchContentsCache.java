package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import java.util.*;
import com.reign.gcld.task.reward.*;

@Component("searchContentsCache")
public class SearchContentsCache extends AbstractCache<Integer, SearchContents>
{
    @Autowired
    private SDataLoader dataLoader;
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<SearchContents> list = this.dataLoader.getModels((Class)SearchContents.class);
        for (final SearchContents i : list) {
            final ITaskReward taskReward = TaskRewardFactory.getInstance().getTaskReward(i.getReward());
            if (taskReward == null) {
                throw new RuntimeException("searchContentCache reward fail in id " + i.getId());
            }
            i.setTaskReward(taskReward);
            super.put((Object)i.getId(), (Object)i);
        }
    }
}
