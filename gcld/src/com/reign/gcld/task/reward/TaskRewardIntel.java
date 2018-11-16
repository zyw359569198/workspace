package com.reign.gcld.task.reward;

import com.reign.gcld.player.dto.*;
import com.reign.util.*;
import java.util.*;
import com.reign.gcld.common.*;

public class TaskRewardIntel implements ITaskReward
{
    int addIntel;
    
    public TaskRewardIntel(final int addIntel) {
        this.addIntel = addIntel;
    }
    
    public TaskRewardIntel(final String[] s) {
        if (s.length > 1) {
            this.addIntel = Integer.valueOf(s[1]);
        }
        else {
            this.addIntel = 0;
        }
    }
    
    @Override
    public Map<Integer, Reward> rewardPlayer(final PlayerDto playerDto, final IDataGetter taskDataGetter, final String prefixAttribute, final Object obj) {
        final int playerId = playerDto.playerId;
        final Tuple<Integer, Double> tuple = (Tuple<Integer, Double>)obj;
        final int generalId = tuple.left;
        taskDataGetter.getPlayerGeneralCivilDao().addIntel(playerId, generalId, this.addIntel);
        return this.getReward(playerDto, taskDataGetter, obj);
    }
    
    @Override
    public Map<Integer, Reward> getReward(final PlayerDto playerDto, final IDataGetter taskDataGetter, final Object obj) {
        final Map<Integer, Reward> map = new HashMap<Integer, Reward>();
        map.put(34, new Reward(34, LocalMessages.T_COMM_10031, this.addIntel));
        return map;
    }
}
