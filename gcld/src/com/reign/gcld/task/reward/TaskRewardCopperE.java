package com.reign.gcld.task.reward;

import com.reign.gcld.player.dto.*;
import com.reign.util.*;
import java.util.*;
import com.reign.gcld.common.*;

public class TaskRewardCopperE implements ITaskReward
{
    private double rate;
    
    public TaskRewardCopperE(final double rate) {
        this.rate = rate;
    }
    
    public TaskRewardCopperE(final String[] s) {
        if (s.length > 1) {
            this.rate = Double.parseDouble(s[1]);
        }
        else {
            this.rate = 0.0;
        }
    }
    
    @Override
    public Map<Integer, Reward> rewardPlayer(final PlayerDto playerDto, final IDataGetter taskDataGetter, final String prefixAttribute, final Object obj) {
        final int outputPerHour = taskDataGetter.getBuildingOutputCache().getBuildingsOutputBase(playerDto.playerId, 1);
        double additionRate = 1.0;
        if (obj != null) {
            final Tuple<Integer, Double> tuple = (Tuple<Integer, Double>)obj;
            additionRate = tuple.right;
        }
        final int resultCopper = (int)Math.ceil(outputPerHour * this.rate * additionRate);
        taskDataGetter.getPlayerResourceDao().addCopperIgnoreMax(playerDto.playerId, resultCopper, String.valueOf(prefixAttribute) + "\u94f6\u5e01", true);
        final Map<Integer, Reward> map = new HashMap<Integer, Reward>();
        map.put(1, new Reward(1, LocalMessages.T_COMM_10004, resultCopper));
        return map;
    }
    
    @Override
    public Map<Integer, Reward> getReward(final PlayerDto playerDto, final IDataGetter taskDataGetter, final Object obj) {
        final int outputPerHour = taskDataGetter.getBuildingOutputCache().getBuildingsOutputBase(playerDto.playerId, 1);
        final int resultCopper = (int)Math.ceil(outputPerHour * this.rate);
        final Map<Integer, Reward> map = new HashMap<Integer, Reward>();
        map.put(1, new Reward(1, LocalMessages.T_COMM_10004, resultCopper));
        return map;
    }
    
    public double getRate() {
        return this.rate;
    }
}
