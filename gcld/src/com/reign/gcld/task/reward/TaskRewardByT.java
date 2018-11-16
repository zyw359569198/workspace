package com.reign.gcld.task.reward;

import com.reign.gcld.player.dto.*;
import java.util.*;
import com.reign.gcld.common.*;

public class TaskRewardByT implements ITaskReward
{
    private int num;
    private static final int ITEM_ID = 1332;
    
    public TaskRewardByT(final int num) {
        this.num = num;
    }
    
    public TaskRewardByT(final String[] s) {
        if (s.length > 1) {
            this.num = Integer.parseInt(s[1]);
        }
        else {
            this.num = 0;
        }
    }
    
    @Override
    public Map<Integer, Reward> rewardPlayer(final PlayerDto playerDto, final IDataGetter taskDataGetter, final String prefixAttribute, final Object obj) {
        taskDataGetter.getStoreHouseService().gainItems(playerDto.playerId, this.num, 1332, prefixAttribute);
        final Map<Integer, Reward> map = new HashMap<Integer, Reward>();
        map.put(85, new Reward(85, LocalMessages.T_COMM_10047, this.num));
        return map;
    }
    
    @Override
    public Map<Integer, Reward> getReward(final PlayerDto playerDto, final IDataGetter taskDataGetter, final Object obj) {
        final Map<Integer, Reward> map = new HashMap<Integer, Reward>();
        map.put(85, new Reward(85, LocalMessages.T_COMM_10047, this.num));
        return map;
    }
}
