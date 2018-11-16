package com.reign.gcld.task.reward;

import com.reign.gcld.player.dto.*;
import com.reign.util.*;
import java.util.*;
import com.reign.gcld.common.*;

public class TaskRewardLeader implements ITaskReward
{
    int addLeader;
    
    public TaskRewardLeader(final int addLeader) {
        this.addLeader = addLeader;
    }
    
    public TaskRewardLeader(final String[] s) {
        if (s.length > 1) {
            this.addLeader = Integer.valueOf(s[1]);
        }
        else {
            this.addLeader = 0;
        }
    }
    
    @Override
    public Map<Integer, Reward> rewardPlayer(final PlayerDto playerDto, final IDataGetter taskDataGetter, final String prefixAttribute, final Object obj) {
        final int playerId = playerDto.playerId;
        final Tuple<Integer, Double> tuple = (Tuple<Integer, Double>)obj;
        final int generalId = tuple.left;
        taskDataGetter.getPlayerGeneralMilitaryDao().addLeader(playerId, generalId, this.addLeader);
        return this.getReward(playerDto, taskDataGetter, obj);
    }
    
    @Override
    public Map<Integer, Reward> getReward(final PlayerDto playerDto, final IDataGetter taskDataGetter, final Object obj) {
        final Map<Integer, Reward> map = new HashMap<Integer, Reward>();
        map.put(35, new Reward(35, LocalMessages.T_COMM_10032, this.addLeader));
        return map;
    }
}
