package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import java.util.*;
import com.reign.gcld.task.reward.*;

@Component("eventDailyCache")
public class EventDailyCache extends AbstractCache<Integer, EventDaily>
{
    @Autowired
    private SDataLoader dataLoader;
    private Map<Integer, List<EventDaily>> eventDailyMap;
    
    public EventDailyCache() {
        this.eventDailyMap = new HashMap<Integer, List<EventDaily>>();
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<EventDaily> list = this.dataLoader.getModels((Class)EventDaily.class);
        final boolean[] mark = new boolean[5];
        for (final EventDaily temp : list) {
            final ITaskReward taskReward1 = TaskRewardFactory.getInstance().getTaskReward(temp.getReward1());
            if (taskReward1 == null) {
                throw new RuntimeException("eventDaily init eventDailyCache fail in taskReward1:" + temp.getId());
            }
            temp.setTaskReward1(taskReward1);
            final ITaskReward taskReward2 = TaskRewardFactory.getInstance().getTaskReward(temp.getReward2());
            if (taskReward2 == null) {
                throw new RuntimeException("eventDaily init eventDailyCache fail in taskReward2:" + temp.getId());
            }
            temp.setTaskReward2(taskReward2);
            super.put((Object)temp.getId(), (Object)temp);
            List<EventDaily> eventList = this.eventDailyMap.get(temp.getType());
            if (eventList == null) {
                eventList = new ArrayList<EventDaily>();
                this.eventDailyMap.put(temp.getType(), eventList);
            }
            eventList.add(temp);
            mark[temp.getType() - 1] = true;
        }
        boolean[] array;
        for (int length = (array = mark).length, i = 0; i < length; ++i) {
            final boolean temp2 = array[i];
            if (!temp2) {
                throw new RuntimeException("eventDaily init eventDailyCache fail beacuse mark[] = :" + mark[0] + ":" + mark[1] + ":" + mark[2] + ":" + mark[3] + ":" + mark[4]);
            }
        }
    }
    
    public List<EventDaily> getEventDailyMap(final int type) {
        return this.eventDailyMap.get(type);
    }
    
    @Override
	public void clear() {
        super.clear();
        this.eventDailyMap.clear();
    }
}
