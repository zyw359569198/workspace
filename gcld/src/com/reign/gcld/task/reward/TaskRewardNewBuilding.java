package com.reign.gcld.task.reward;

import com.reign.gcld.player.dto.*;
import java.util.*;
import com.reign.gcld.common.*;

public class TaskRewardNewBuilding implements ITaskReward
{
    private int id;
    
    public TaskRewardNewBuilding(final int id) {
        this.id = id;
    }
    
    public TaskRewardNewBuilding(final String[] s) {
        if (s.length > 1) {
            this.id = Integer.parseInt(s[1]);
        }
        else {
            this.id = 0;
        }
    }
    
    @Override
    public Map<Integer, Reward> rewardPlayer(final PlayerDto playerDto, final IDataGetter taskDataGetter, final String prefixAttribute, final Object obj) {
        taskDataGetter.getBuildingService().createBuilding(playerDto.playerId, this.id, 1);
        return this.getReward(playerDto, taskDataGetter, obj);
    }
    
    @Override
    public Map<Integer, Reward> getReward(final PlayerDto playerDto, final IDataGetter taskDataGetter, final Object obj) {
        final Map<Integer, Reward> map = new HashMap<Integer, Reward>();
        map.put(6, new Reward(6, LocalMessages.T_COMM_10007, this.id));
        return map;
    }
}
