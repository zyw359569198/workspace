package com.reign.gcld.task.reward;

import com.reign.gcld.player.dto.*;
import com.reign.gcld.common.*;
import java.util.*;

public class TaskRewardStoreLockOn implements ITaskReward
{
    private int itemId;
    
    public TaskRewardStoreLockOn(final int itemId) {
        this.itemId = itemId;
    }
    
    public TaskRewardStoreLockOn(final String[] s) {
        this.itemId = Integer.valueOf(s[1]);
    }
    
    @Override
    public Map<Integer, Reward> rewardPlayer(final PlayerDto playerDto, final IDataGetter taskDataGetter, final String prefixAttribute, final Object obj) {
        taskDataGetter.getStoreService().addLockId(playerDto.playerId, this.itemId);
        return this.getReward(playerDto, taskDataGetter, obj);
    }
    
    @Override
    public Map<Integer, Reward> getReward(final PlayerDto playerDto, final IDataGetter taskDataGetter, final Object obj) {
        final Map<Integer, Reward> map = new HashMap<Integer, Reward>();
        map.put(14, new Reward(14, "storelockon", this.itemId));
        return map;
    }
}
