package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.common.log.*;
import java.util.*;
import com.reign.gcld.task.reward.*;

@Component("tradeMarchantCache")
public class TradeMarchantCache extends AbstractCache<Integer, TradeMarchant>
{
    private Logger logger;
    @Autowired
    private SDataLoader dataLoader;
    
    public TradeMarchantCache() {
        this.logger = CommonLog.getLog(TradeMarchantCache.class);
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<TradeMarchant> list = this.dataLoader.getModels((Class)TradeMarchant.class);
        for (final TradeMarchant temp : list) {
            final ITaskReward taskReward = TaskRewardFactory.getInstance().getTaskReward(temp.getReward());
            if (taskReward == null) {
                this.logger.error("updateRewardCache init taskReward fail in taskId:" + temp.getId());
                super.put((Object)temp.getId(), (Object)temp);
            }
            else {
                temp.setTaskReward(taskReward);
                super.put((Object)temp.getId(), (Object)temp);
            }
        }
    }
}
