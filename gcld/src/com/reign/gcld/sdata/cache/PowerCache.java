package com.reign.gcld.sdata.cache;

import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.util.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.gcld.task.reward.*;
import java.util.*;

@Component("powerCache")
public class PowerCache extends AbstractCache<Integer, Power>
{
    @Autowired
    private SDataLoader dataLoader;
    public static final int EXTRA_POWER_COEFFICIENT = 100;
    private Map<Integer, Power> powerMap;
    private Map<Integer, Tuple<Integer, Integer>> powerLvScaleMap;
    
    public PowerCache() {
        this.powerMap = new HashMap<Integer, Power>();
        this.powerLvScaleMap = new HashMap<Integer, Tuple<Integer, Integer>>();
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<Power> list = this.dataLoader.getModels((Class)Power.class);
        for (int i = 0; i < list.size(); ++i) {
            final Power power = list.get(i);
            final ITaskReward taskReward = TaskRewardFactory.getInstance().getTaskReward(power.getReward());
            if (taskReward == null) {
                throw new RuntimeException("UpdateRewardCache reward fail in lv " + power.getId());
            }
            power.setTaskReward(taskReward);
            super.put((Object)power.getId(), (Object)power);
            if (i < list.size() - 1) {
                this.powerMap.put(power.getNextPower(), power);
            }
        }
        final List<Armies> armiesList = this.dataLoader.getModels((Class)Armies.class);
        for (final Armies temp : armiesList) {
            final int powerId = temp.getPowerId();
            Tuple<Integer, Integer> tuple = this.powerLvScaleMap.get(powerId);
            if (tuple == null) {
                tuple = new Tuple();
                tuple.left = Integer.MAX_VALUE;
                tuple.right = Integer.MIN_VALUE;
                this.powerLvScaleMap.put(powerId, tuple);
            }
            final int lv = temp.getLevel();
            if (lv < tuple.left) {
                tuple.left = lv;
            }
            if (lv > tuple.right) {
                tuple.right = lv;
            }
        }
    }
    
    public Tuple<Integer, Integer> getLvScale(final int powerId) {
        return this.powerLvScaleMap.get(powerId);
    }
    
    public Power getPrePower(final int powerId) {
        return this.powerMap.get(powerId);
    }
    
    public int fromArmyToExtra(final int powerId) {
        return 100 * powerId + 1;
    }
    
    public int fromExtraToArmy(final int extraPowerId) {
        return extraPowerId / 100;
    }
    
    @Override
	public void clear() {
        super.clear();
        this.powerMap.clear();
    }
}
