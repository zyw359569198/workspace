package com.reign.gcld.task.reward;

import com.reign.gcld.player.dto.*;
import com.reign.gcld.common.*;
import java.util.*;

public class TaskRewardDanShuTieQuan implements ITaskReward
{
    private int num;
    
    public TaskRewardDanShuTieQuan(final int num) {
        this.num = num;
    }
    
    public TaskRewardDanShuTieQuan(final String[] s) {
        if (s.length > 1) {
            this.num = Integer.parseInt(s[1]);
        }
        else {
            this.num = 0;
        }
    }
    
    @Override
    public Map<Integer, Reward> rewardPlayer(final PlayerDto playerDto, final IDataGetter taskDataGetter, final String prefixAttribute, final Object obj) {
        taskDataGetter.getStoreHouseService().gainSearchItems(106, this.num, playerDto, prefixAttribute);
        return this.getReward(playerDto, taskDataGetter, obj);
    }
    
    @Override
    public Map<Integer, Reward> getReward(final PlayerDto playerDto, final IDataGetter taskDataGetter, final Object obj) {
        final Map<Integer, Reward> map = new HashMap<Integer, Reward>();
        map.put(24, new Reward(24, "danshutiequan", this.num));
        return map;
    }
}
