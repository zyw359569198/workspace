package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import java.util.*;
import com.reign.gcld.task.reward.*;

@Component("loginRewardComboCache")
public class LoginRewardComboCache extends AbstractCache<Integer, LoginRewardCombo>
{
    @Autowired
    private SDataLoader dataLoader;
    private Double[] prob;
    
    public LoginRewardComboCache() {
        this.prob = null;
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<LoginRewardCombo> list = this.dataLoader.getModels((Class)LoginRewardCombo.class);
        this.prob = new Double[list.size()];
        int i = 0;
        for (final LoginRewardCombo temp : list) {
            ITaskReward reward = TaskRewardFactory.getInstance().getTaskReward(temp.getRewardBase());
            temp.setBaseReward(reward);
            reward = TaskRewardFactory.getInstance().getTaskReward(temp.getRewardCombo());
            temp.setComboReward(reward);
            this.prob[i++] = temp.getProb();
            super.put((Object)temp.getId(), (Object)temp);
        }
    }
    
    public LoginRewardCombo getProb(final double rate) {
        if (rate <= this.prob[0]) {
            return this.getModels().get(0);
        }
        for (int i = 0; i < this.prob.length - 1; ++i) {
            if (rate > this.prob[i] && rate <= this.prob[i + 1]) {
                return this.getModels().get(i + 1);
            }
        }
        return this.getModels().get(this.prob.length - 1);
    }
    
    @Override
	public void clear() {
        super.clear();
        this.prob = null;
    }
}
