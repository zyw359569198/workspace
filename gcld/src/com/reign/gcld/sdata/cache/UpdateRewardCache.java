package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import java.util.*;
import com.reign.gcld.task.reward.*;

@Component("updateRewardCache")
public class UpdateRewardCache extends AbstractCache<Integer, UpdateReward>
{
    @Autowired
    private SDataLoader dataLoader;
    private List<Integer> specialRewardList;
    
    public UpdateRewardCache() {
        this.specialRewardList = new ArrayList<Integer>();
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<UpdateReward> list = this.dataLoader.getModels((Class)UpdateReward.class);
        for (final UpdateReward temp : list) {
            final ITaskReward taskReward = TaskRewardFactory.getInstance().getTaskReward(temp.getReward());
            if (taskReward == null) {
                throw new RuntimeException("UpdateRewardCache reward fail in lv " + temp.getLv());
            }
            temp.setTaskReward(taskReward);
            super.put((Object)temp.getLv(), (Object)temp);
            if (temp.getType() != 2) {
                continue;
            }
            this.specialRewardList.add(temp.getLv());
        }
    }
    
    public int getNextSpecialLv(final int nowLevel) {
        int nextLv = 200;
        for (int i = 0; i < this.specialRewardList.size(); ++i) {
            if (this.specialRewardList.get(i) > nowLevel) {
                nextLv = this.specialRewardList.get(i);
                break;
            }
        }
        return nextLv;
    }
    
    @Override
	public void clear() {
        super.clear();
        this.specialRewardList.clear();
    }
}
