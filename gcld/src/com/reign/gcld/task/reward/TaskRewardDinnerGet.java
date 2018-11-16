package com.reign.gcld.task.reward;

import com.reign.gcld.player.dto.*;
import java.util.*;
import com.reign.gcld.common.*;

public class TaskRewardDinnerGet implements ITaskReward
{
    private int num;
    
    public TaskRewardDinnerGet(final int num) {
        this.num = num;
    }
    
    public TaskRewardDinnerGet(final String[] s) {
        if (s.length > 1) {
            this.num = Integer.parseInt(s[1]);
        }
        else {
            this.num = 0;
        }
    }
    
    @Override
    public Map<Integer, Reward> rewardPlayer(final PlayerDto playerDto, final IDataGetter dataGetter, final String prefixAttribute, final Object obj) {
        dataGetter.getPlayerDinnerDao().rewardDinnerNum(playerDto.playerId, this.num);
        return this.getReward(playerDto, dataGetter, obj);
    }
    
    @Override
    public Map<Integer, Reward> getReward(final PlayerDto playerDto, final IDataGetter taskDataGetter, final Object obj) {
        final Map<Integer, Reward> map = new HashMap<Integer, Reward>();
        map.put(28, new Reward(28, LocalMessages.T_TASK_REWARD_DINNER_GET, this.num));
        return map;
    }
}
