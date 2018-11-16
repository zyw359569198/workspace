package com.reign.gcld.task.reward;

import com.reign.gcld.player.dto.*;
import java.util.*;
import com.reign.gcld.common.*;

public class TaskRewardNewIncense implements ITaskReward
{
    private int godId;
    
    public TaskRewardNewIncense(final int godId) {
        this.godId = godId;
    }
    
    public TaskRewardNewIncense(final String[] s) {
        if (s.length > 1) {
            this.godId = Integer.parseInt(s[1]);
        }
        else {
            this.godId = 0;
        }
    }
    
    @Override
    public Map<Integer, Reward> rewardPlayer(final PlayerDto playerDto, final IDataGetter taskDataGetter, final String prefixAttribute, final Object obj) {
        taskDataGetter.getIncenseService().addIncenseGod(playerDto.playerId, this.godId);
        return this.getReward(playerDto, taskDataGetter, obj);
    }
    
    @Override
    public Map<Integer, Reward> getReward(final PlayerDto playerDto, final IDataGetter taskDataGetter, final Object obj) {
        final Map<Integer, Reward> map = new HashMap<Integer, Reward>();
        map.put(13, new Reward(13, LocalMessages.T_COMM_10019, this.godId));
        return map;
    }
}
