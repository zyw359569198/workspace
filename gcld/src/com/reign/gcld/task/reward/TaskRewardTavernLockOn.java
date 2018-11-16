package com.reign.gcld.task.reward;

import com.reign.gcld.player.dto.*;
import com.reign.gcld.common.*;
import java.util.*;

public class TaskRewardTavernLockOn implements ITaskReward
{
    private int generalId;
    
    public TaskRewardTavernLockOn(final int generalId) {
        this.generalId = generalId;
    }
    
    public TaskRewardTavernLockOn(final String[] s) {
        this.generalId = Integer.valueOf(s[1]);
    }
    
    @Override
    public Map<Integer, Reward> rewardPlayer(final PlayerDto playerDto, final IDataGetter taskDataGetter, final String prefixAttribute, final Object obj) {
        taskDataGetter.getTavernService().addLockId(playerDto.playerId, this.generalId);
        return this.getReward(playerDto, taskDataGetter, obj);
    }
    
    @Override
    public Map<Integer, Reward> getReward(final PlayerDto playerDto, final IDataGetter taskDataGetter, final Object obj) {
        final Map<Integer, Reward> map = new HashMap<Integer, Reward>();
        map.put(16, new Reward(16, "tavernlockon", this.generalId));
        return map;
    }
}
