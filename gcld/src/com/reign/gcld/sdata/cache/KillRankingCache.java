package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import java.util.*;
import com.reign.gcld.task.reward.*;

@Component("killRankingCache")
public class KillRankingCache extends AbstractCache<Integer, KillRanking>
{
    @Autowired
    private SDataLoader dataLoader;
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<KillRanking> list = this.dataLoader.getModels((Class)KillRanking.class);
        for (final KillRanking temp : list) {
            final ITaskReward bReward = TaskRewardFactory.getInstance().getTaskReward(temp.getBaseReward());
            if (bReward == null) {
                throw new RuntimeException("KillRankingCache getBaseReward fail in lv " + temp.getBaseReward());
            }
            temp.setbReward(bReward);
            final ITaskReward iReward = TaskRewardFactory.getInstance().getTaskReward(temp.getIronReward());
            if (iReward == null) {
                throw new RuntimeException("KillRankingCache getIronReward fail in lv " + temp.getIronReward());
            }
            temp.setiReward(iReward);
            super.put((Object)temp.getRanking(), (Object)temp);
        }
    }
    
    public int getNextRank(final int rank) {
        final KillRanking killRanking = (KillRanking)super.get((Object)rank);
        for (int i = rank - 1; i >= 1; --i) {
            final KillRanking nextKillRanking = (KillRanking)super.get((Object)i);
            if (nextKillRanking != null) {
                if (!killRanking.getKindomTaskIron().equals(nextKillRanking.getKindomTaskIron())) {
                    return i;
                }
            }
        }
        return 0;
    }
}
