package com.reign.gcld.task.reward;

import com.reign.gcld.player.dto.*;
import java.util.*;
import com.reign.gcld.common.*;

public class TaskRewardWorshipGet implements ITaskReward
{
    private int num;
    
    public TaskRewardWorshipGet(final int num) {
        this.num = num;
    }
    
    public TaskRewardWorshipGet(final String[] s) {
        if (s.length > 1) {
            this.num = Integer.parseInt(s[1]);
        }
        else {
            this.num = 0;
        }
    }
    
    @Override
    public Map<Integer, Reward> rewardPlayer(final PlayerDto playerDto, final IDataGetter taskDataGetter, final String prefixAttribute, final Object obj) {
        taskDataGetter.getPlayerIncenseDao().addIncenseNum(playerDto.playerId, this.num);
        return this.getReward(playerDto, taskDataGetter, obj);
    }
    
    @Override
    public Map<Integer, Reward> getReward(final PlayerDto playerDto, final IDataGetter taskDataGetter, final Object obj) {
        final Map<Integer, Reward> map = new HashMap<Integer, Reward>();
        map.put(22, new Reward(22, LocalMessages.T_COMM_10019, this.num));
        return map;
    }
}
