package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.common.log.*;
import java.util.*;
import com.reign.gcld.task.reward.*;

@Component("tradeComboCache")
public class TradeComboCache extends AbstractCache<Integer, TradeCombo>
{
    private Logger logger;
    @Autowired
    private SDataLoader dataLoader;
    
    public TradeComboCache() {
        this.logger = CommonLog.getLog(TradeComboCache.class);
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<TradeCombo> list = this.dataLoader.getModels((Class)TradeCombo.class);
        for (final TradeCombo temp : list) {
            final ITaskReward taskReward = TaskRewardFactory.getInstance().getTaskReward(temp.getReward());
            if (taskReward == null) {
                super.put((Object)temp.getCardQuality(), (Object)temp);
                this.logger.error("updateRewardCache init taskReward fail in taskId:" + temp.getId());
            }
            else {
                temp.setTaskReward(taskReward);
                super.put((Object)temp.getCardQuality(), (Object)temp);
            }
        }
    }
}
