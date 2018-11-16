package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import java.util.*;
import com.reign.gcld.task.reward.*;

@Component("worldSearchEventCache")
public class WorldSearchEventCache extends AbstractCache<Integer, WorldSearchEvent>
{
    @Autowired
    private SDataLoader dataLoader;
    private Map<Integer, List<WorldSearchEvent>> levelEventMap;
    
    public WorldSearchEventCache() {
        this.levelEventMap = new HashMap<Integer, List<WorldSearchEvent>>();
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<WorldSearchEvent> list = this.dataLoader.getModels((Class)WorldSearchEvent.class);
        for (final WorldSearchEvent temp : list) {
            final ITaskReward taskReward1 = TaskRewardFactory.getInstance().getTaskReward(temp.getReward1());
            if (taskReward1 == null) {
                throw new RuntimeException("WorldSearchEvent init taskReward1 fail in taskId:" + temp.getId());
            }
            temp.setTaskReward1(taskReward1);
            final ITaskReward taskReward2 = TaskRewardFactory.getInstance().getTaskReward(temp.getReward2());
            if (taskReward2 == null) {
                throw new RuntimeException("WorldSearchEvent init taskReward2 fail in taskId:" + temp.getId());
            }
            temp.setTaskReward2(taskReward2);
            super.put((Object)temp.getId(), (Object)temp);
            List<WorldSearchEvent> levelList = this.levelEventMap.get(temp.getLevel());
            if (levelList == null) {
                levelList = new ArrayList<WorldSearchEvent>();
                this.levelEventMap.put(temp.getLevel(), levelList);
            }
            levelList.add(temp);
        }
    }
    
    public List<WorldSearchEvent> getEventListByLv(final int level) {
        return this.levelEventMap.get(level);
    }
    
    @Override
	public void clear() {
        super.clear();
        this.levelEventMap.clear();
    }
}
