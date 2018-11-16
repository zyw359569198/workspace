package com.reign.gcld.task.reward;

import com.reign.gcld.player.dto.*;
import com.reign.gcld.event.util.*;
import java.util.*;
import com.reign.gcld.common.*;

public class TaskRewardMs implements ITaskReward
{
    private int num;
    
    public TaskRewardMs(final int num) {
        this.num = num;
    }
    
    public TaskRewardMs(final String[] s) {
        if (s.length > 1) {
            this.num = Integer.parseInt(s[1]);
        }
        else {
            this.num = 0;
        }
    }
    
    @Override
    public Map<Integer, Reward> rewardPlayer(final PlayerDto playerDto, final IDataGetter taskDataGetter, final String prefixAttribute, final Object obj) {
        EventUtil.handleOperation(playerDto.playerId, 11, this.num);
        final Map<Integer, Reward> map = new HashMap<Integer, Reward>();
        map.put(84, new Reward(84, LocalMessages.T_COMM_10046, this.num));
        return map;
    }
    
    @Override
    public Map<Integer, Reward> getReward(final PlayerDto playerDto, final IDataGetter taskDataGetter, final Object obj) {
        final Map<Integer, Reward> map = new HashMap<Integer, Reward>();
        map.put(84, new Reward(84, LocalMessages.T_COMM_10046, this.num));
        return map;
    }
}
