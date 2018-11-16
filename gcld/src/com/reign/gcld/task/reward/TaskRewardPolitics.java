package com.reign.gcld.task.reward;

import com.reign.gcld.player.dto.*;
import com.reign.util.*;
import java.util.*;
import com.reign.gcld.common.*;

public class TaskRewardPolitics implements ITaskReward
{
    int addPolitics;
    
    public TaskRewardPolitics(final int addPolitics) {
        this.addPolitics = addPolitics;
    }
    
    public TaskRewardPolitics(final String[] s) {
        if (s.length > 1) {
            this.addPolitics = Integer.valueOf(s[1]);
        }
        else {
            this.addPolitics = 0;
        }
    }
    
    @Override
    public Map<Integer, Reward> rewardPlayer(final PlayerDto playerDto, final IDataGetter taskDataGetter, final String prefixAttribute, final Object obj) {
        final int playerId = playerDto.playerId;
        final Tuple<Integer, Double> tuple = (Tuple<Integer, Double>)obj;
        final int generalId = tuple.left;
        taskDataGetter.getPlayerGeneralCivilDao().addPolitics(playerId, generalId, this.addPolitics);
        return this.getReward(playerDto, taskDataGetter, obj);
    }
    
    @Override
    public Map<Integer, Reward> getReward(final PlayerDto playerDto, final IDataGetter taskDataGetter, final Object obj) {
        final Map<Integer, Reward> map = new HashMap<Integer, Reward>();
        map.put(36, new Reward(36, LocalMessages.T_COMM_10033, this.addPolitics));
        return map;
    }
}
