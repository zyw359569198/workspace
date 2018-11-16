package com.reign.gcld.task.reward;

import com.reign.gcld.player.dto.*;
import com.reign.util.*;
import java.util.*;
import com.reign.gcld.common.*;

public class TaskRewardLumberE implements ITaskReward
{
    private double rate;
    
    public TaskRewardLumberE(final double rate) {
        this.rate = rate;
    }
    
    public TaskRewardLumberE(final String[] s) {
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
        final int outputPerHour = taskDataGetter.getBuildingOutputCache().getBuildingsOutputBase(playerDto.playerId, 2);
        final int result = (int)Math.ceil(outputPerHour * this.rate * additionRate);
        taskDataGetter.getPlayerResourceDao().addWoodIgnoreMax(playerDto.playerId, result, String.valueOf(prefixAttribute) + "\u6728\u6750", true);
        final Map<Integer, Reward> map = new HashMap<Integer, Reward>();
        map.put(2, new Reward(2, LocalMessages.T_COMM_10005, result));
        return map;
    }
    
    @Override
    public Map<Integer, Reward> getReward(final PlayerDto playerDto, final IDataGetter taskDataGetter, final Object obj) {
        final int outputPerHour = taskDataGetter.getBuildingOutputCache().getBuildingsOutputBase(playerDto.playerId, 2);
        final int resultLumber = (int)Math.ceil(outputPerHour * this.rate);
        final Map<Integer, Reward> map = new HashMap<Integer, Reward>();
        map.put(2, new Reward(2, LocalMessages.T_COMM_10005, resultLumber));
        return map;
    }
    
    public double getRate() {
        return this.rate;
    }
}
