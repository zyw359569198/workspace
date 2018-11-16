package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import java.util.*;
import com.reign.gcld.task.reward.*;

@Component("worldSearchDefaultCache")
public class WorldSearchDefaultCache extends AbstractCache<Integer, WorldSearchDefault>
{
    @Autowired
    private SDataLoader dataLoader;
    private Map<Integer, List<WorldSearchDefault>> typeEventMap;
    
    public WorldSearchDefaultCache() {
        this.typeEventMap = new HashMap<Integer, List<WorldSearchDefault>>();
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<WorldSearchDefault> list = this.dataLoader.getModels((Class)WorldSearchDefault.class);
        for (final WorldSearchDefault temp : list) {
            final ITaskReward taskReward = TaskRewardFactory.getInstance().getTaskReward(temp.getReward());
            if (taskReward == null) {
                throw new RuntimeException("WorldSearchDefault init taskReward fail in taskId:" + temp.getId());
            }
            temp.setTaskReward(taskReward);
            super.put((Object)temp.getId(), (Object)temp);
            List<WorldSearchDefault> typeList = this.typeEventMap.get(temp.getType());
            if (typeList == null) {
                typeList = new ArrayList<WorldSearchDefault>();
                this.typeEventMap.put(temp.getType(), typeList);
            }
            typeList.add(temp);
        }
    }
    
    public List<WorldSearchDefault> getEventListByType(final int type) {
        return this.typeEventMap.get(type);
    }
    
    @Override
	public void clear() {
        super.clear();
        this.typeEventMap.clear();
    }
}
