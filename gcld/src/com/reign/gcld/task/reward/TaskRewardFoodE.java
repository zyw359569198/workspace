package com.reign.gcld.task.reward;

import com.reign.gcld.player.dto.*;
import com.reign.util.*;
import java.util.*;
import com.reign.gcld.common.*;

public class TaskRewardFoodE implements ITaskReward
{
    private double rate;
    
    public TaskRewardFoodE(final double rate) {
        this.rate = rate;
    }
    
    public TaskRewardFoodE(final String[] s) {
        if (s.length > 1) {
            this.rate = Double.parseDouble(s[1]);
        }
        else {
            this.rate = 0.0;
        }
    }
    
    @Override
    public Map<Integer, Reward> rewardPlayer(final PlayerDto playerDto, final IDataGetter taskDataGetter, final String prefixAttribute, final Object obj) {
        double additionRate = 1.0;
        if (obj != null) {
            final Tuple<Integer, Double> tuple = (Tuple<Integer, Double>)obj;
            additionRate = tuple.right;
        }
        final int outputPerHour = taskDataGetter.getBuildingOutputCache().getBuildingsOutputBase(playerDto.playerId, 3);
        final int result = (int)Math.ceil(outputPerHour * this.rate * additionRate);
        taskDataGetter.getPlayerResourceDao().addFoodIgnoreMax(playerDto.playerId, result, String.valueOf(prefixAttribute) + "\u7cae\u98df");
        final Map<Integer, Reward> map = new HashMap<Integer, Reward>();
        map.put(3, new Reward(3, LocalMessages.T_COMM_10017, result));
        return map;
    }
    
    @Override
    public Map<Integer, Reward> getReward(final PlayerDto playerDto, final IDataGetter taskDataGetter, final Object obj) {
        final int outputPerHour = taskDataGetter.getBuildingOutputCache().getBuildingsOutputBase(playerDto.playerId, 3);
        final int resultFood = (int)Math.ceil(outputPerHour * this.rate);
        final Map<Integer, Reward> map = new HashMap<Integer, Reward>();
        map.put(3, new Reward(3, LocalMessages.T_COMM_10017, resultFood));
        return map;
    }
    
    public double getRate() {
        return this.rate;
    }
}
