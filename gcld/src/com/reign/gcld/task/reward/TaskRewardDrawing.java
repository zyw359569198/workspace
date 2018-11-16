package com.reign.gcld.task.reward;

import com.reign.gcld.player.dto.*;
import java.util.*;
import com.reign.gcld.common.*;

public class TaskRewardDrawing implements ITaskReward
{
    private int id;
    
    public TaskRewardDrawing(final int id) {
        this.id = id;
    }
    
    public TaskRewardDrawing(final String[] s) {
        if (s.length > 1) {
            this.id = Integer.parseInt(s[1]);
        }
        else {
            this.id = 0;
        }
    }
    
    @Override
    public Map<Integer, Reward> rewardPlayer(final PlayerDto playerDto, final IDataGetter taskDataGetter, final String prefixAttribute, final Object obj) {
        taskDataGetter.getBuildingService().dropBluePrintById(playerDto.playerId, this.id);
        return this.getReward(playerDto, taskDataGetter, obj);
    }
    
    @Override
    public Map<Integer, Reward> getReward(final PlayerDto playerDto, final IDataGetter taskDataGetter, final Object obj) {
        final Map<Integer, Reward> map = new HashMap<Integer, Reward>();
        map.put(41, new Reward(41, LocalMessages.T_COMM_10036, this.id));
        return map;
    }
}
