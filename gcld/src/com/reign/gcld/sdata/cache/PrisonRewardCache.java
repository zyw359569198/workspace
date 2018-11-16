package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.util.*;
import com.reign.gcld.common.log.*;
import java.util.*;
import com.reign.gcld.task.reward.*;

@Component("prisonRewardCache")
public class PrisonRewardCache extends AbstractCache<Integer, PrisonReward>
{
    @Autowired
    private SDataLoader dataLoader;
    Logger logger;
    private static List<Tuple<Integer, Integer>> level2stage;
    private static List<Tuple<Integer, Integer>> work2stage;
    
    static {
        PrisonRewardCache.level2stage = new ArrayList<Tuple<Integer, Integer>>();
        PrisonRewardCache.work2stage = new ArrayList<Tuple<Integer, Integer>>();
    }
    
    public PrisonRewardCache() {
        this.logger = CommonLog.getLog(TaskRewardAnd.class);
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<PrisonReward> resultList = this.dataLoader.getModels((Class)PrisonReward.class);
        if (resultList.size() > 60 || resultList.size() % 4 != 0) {
            this.logger.error("slave system : stage can't beyond 60 and  stage can divided by 4 , unfortunately reality stage max is :" + resultList.size());
            throw new RuntimeException("slave system : stage can't beyond 60 and  stage can divided by 4 , unfortunately reality stage max is :" + resultList.size());
        }
        final Map<Integer, Integer> tempMap = new HashMap<Integer, Integer>();
        for (final PrisonReward pr : resultList) {
            final ITaskReward taskReward = TaskRewardFactory.getInstance().getTaskReward(pr.getReward());
            if (taskReward == null) {
                throw new RuntimeException("PrisonRewardCache reward fail in id " + pr.getId());
            }
            pr.setTaskReward(taskReward);
            super.put((Object)pr.getId(), (Object)pr);
            final Integer openLv = pr.getOpenLv();
            final Integer id = tempMap.get(openLv);
            if (id == null) {
                tempMap.put(openLv, pr.getId());
            }
            else if (id < pr.getId()) {
                tempMap.put(openLv, pr.getId());
            }
            final Tuple<Integer, Integer> tuple = new Tuple();
            tuple.left = pr.getWork();
            tuple.right = pr.getId();
            PrisonRewardCache.work2stage.add(tuple);
        }
        for (final int openLv2 : tempMap.keySet()) {
            final Tuple<Integer, Integer> tuple2 = new Tuple();
            tuple2.left = openLv2;
            tuple2.right = tempMap.get(openLv2);
            PrisonRewardCache.level2stage.add(tuple2);
        }
        Collections.sort(PrisonRewardCache.level2stage, new Comparator<Tuple<Integer, Integer>>() {
            @Override
            public int compare(final Tuple<Integer, Integer> o1, final Tuple<Integer, Integer> o2) {
                return o1.left - o2.left;
            }
        });
        Collections.sort(PrisonRewardCache.work2stage, new Comparator<Tuple<Integer, Integer>>() {
            @Override
            public int compare(final Tuple<Integer, Integer> o1, final Tuple<Integer, Integer> o2) {
                return o1.left - o2.left;
            }
        });
    }
    
    public int getStageByLv(final int lv) {
        if (lv < PrisonRewardCache.level2stage.get(0).left) {
            return 0;
        }
        for (int i = 0; i < PrisonRewardCache.level2stage.size() - 1; ++i) {
            final Tuple<Integer, Integer> tuple1 = PrisonRewardCache.level2stage.get(i);
            final Tuple<Integer, Integer> tuple2 = PrisonRewardCache.level2stage.get(i + 1);
            if (tuple1.left <= lv && lv < tuple2.left) {
                return tuple1.right;
            }
        }
        return PrisonRewardCache.level2stage.get(PrisonRewardCache.level2stage.size() - 1).right;
    }
    
    public int getStageByWork(final int work) {
        if (work < PrisonRewardCache.work2stage.get(0).left) {
            return 0;
        }
        for (int i = 0; i < PrisonRewardCache.work2stage.size() - 1; ++i) {
            final Tuple<Integer, Integer> tuple1 = PrisonRewardCache.work2stage.get(i);
            final Tuple<Integer, Integer> tuple2 = PrisonRewardCache.work2stage.get(i + 1);
            if (tuple1.left <= work && work < tuple2.left) {
                return tuple1.right;
            }
        }
        return PrisonRewardCache.work2stage.get(PrisonRewardCache.work2stage.size() - 1).right;
    }
    
    public int getInvolvedStageByWork(final int work) {
        if (work <= PrisonRewardCache.work2stage.get(0).left) {
            return 1;
        }
        for (int i = 0; i < PrisonRewardCache.work2stage.size() - 1; ++i) {
            final Tuple<Integer, Integer> tuple1 = PrisonRewardCache.work2stage.get(i);
            final Tuple<Integer, Integer> tuple2 = PrisonRewardCache.work2stage.get(i + 1);
            if (tuple1.left < work && work <= tuple2.left) {
                return tuple2.right;
            }
        }
        return PrisonRewardCache.work2stage.get(PrisonRewardCache.work2stage.size() - 1).right;
    }
    
    @Override
	public void clear() {
        super.clear();
        PrisonRewardCache.level2stage.clear();
        PrisonRewardCache.work2stage.clear();
    }
}
