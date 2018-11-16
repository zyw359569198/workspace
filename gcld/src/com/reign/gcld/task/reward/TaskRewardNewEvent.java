package com.reign.gcld.task.reward;

import com.reign.gcld.player.dto.*;
import com.reign.gcld.common.*;
import java.util.*;

public class TaskRewardNewEvent implements ITaskReward
{
    private int areaId;
    
    public TaskRewardNewEvent(final int areaId) {
        this.areaId = areaId;
    }
    
    public TaskRewardNewEvent(final String[] s) {
        if (s.length > 1) {
            this.areaId = Integer.parseInt(s[1]);
        }
        else {
            this.areaId = 0;
        }
    }
    
    @Override
    public Map<Integer, Reward> rewardPlayer(final PlayerDto playerDto, final IDataGetter taskDataGetter, final String prefixAttribute, final Object obj) {
        taskDataGetter.getPoliticsService().rewardPolitcsEvent(this.areaId, playerDto.playerId);
        return this.getReward(playerDto, taskDataGetter, obj);
    }
    
    @Override
    public Map<Integer, Reward> getReward(final PlayerDto playerDto, final IDataGetter taskDataGetter, final Object obj) {
        final Map<Integer, Reward> map = new HashMap<Integer, Reward>();
        map.put(18, new Reward(18, "new_event", this.areaId));
        return map;
    }
}
