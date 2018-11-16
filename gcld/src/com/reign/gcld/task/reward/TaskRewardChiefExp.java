package com.reign.gcld.task.reward;

import com.reign.gcld.player.dto.*;
import com.reign.gcld.world.common.*;
import java.util.*;
import com.reign.gcld.common.*;
import com.reign.gcld.player.common.*;

public class TaskRewardChiefExp implements ITaskReward
{
    private int num;
    
    public TaskRewardChiefExp(final int num) {
        this.num = num;
    }
    
    public TaskRewardChiefExp(final String[] s) {
        if (s.length > 1) {
            this.num = Integer.parseInt(s[1]);
        }
        else {
            this.num = 0;
        }
    }
    
    @Override
    public Map<Integer, Reward> rewardPlayer(final PlayerDto playerDto, final IDataGetter taskDataGetter, final String prefixAttribute, final Object obj) {
        int exp = this.num;
        if (obj != null && obj instanceof CityEventRate) {
            final CityEventRate cer = (CityEventRate)obj;
            final double rate = cer.rate;
            exp = (int)(this.num * rate);
        }
        final AddExpInfo aei = taskDataGetter.getPlayerService().updateExpAndPlayerLevel(playerDto.playerId, exp, String.valueOf(prefixAttribute) + "\u73a9\u5bb6\u7ecf\u9a8c\u503c");
        final Map<Integer, Reward> map = new HashMap<Integer, Reward>();
        if (aei.addExp > 0) {
            map.put(5, new Reward(5, LocalMessages.T_COMM_10006, aei.addExp));
            return map;
        }
        return map;
    }
    
    @Override
    public Map<Integer, Reward> getReward(final PlayerDto playerDto, final IDataGetter taskDataGetter, final Object obj) {
        int exp = this.num;
        if (obj != null && obj instanceof CityEventRate) {
            final CityEventRate cer = (CityEventRate)obj;
            final double rate = cer.rate;
            exp = (int)(this.num * rate);
        }
        final Map<Integer, Reward> map = new HashMap<Integer, Reward>();
        map.put(5, new Reward(5, LocalMessages.T_COMM_10006, exp));
        return map;
    }
}
