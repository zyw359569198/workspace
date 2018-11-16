package com.reign.gcld.task.reward;

import com.reign.gcld.player.dto.*;
import com.reign.gcld.world.common.*;
import java.util.*;
import com.reign.gcld.common.*;

public class TaskRewardFood implements ITaskReward
{
    private int num;
    
    public TaskRewardFood(final int num) {
        this.num = num;
    }
    
    public TaskRewardFood(final String[] s) {
        if (s.length > 1) {
            this.num = Integer.parseInt(s[1]);
        }
        else {
            this.num = 0;
        }
    }
    
    @Override
    public Map<Integer, Reward> rewardPlayer(final PlayerDto playerDto, final IDataGetter taskDataGetter, final String prefixAttribute, final Object obj) {
        int add = this.num;
        if (obj != null && obj instanceof Integer) {
            add = DataCastUtil.double2int(this.num * (1.0 + taskDataGetter.getTechEffectCache().getTechEffect(playerDto.playerId, (int)obj) / 100.0));
        }
        if (obj != null && obj instanceof CityEventRate) {
            final CityEventRate cer = (CityEventRate)obj;
            final double rate = cer.rate;
            add = (int)(this.num * rate);
        }
        taskDataGetter.getPlayerResourceDao().addFoodIgnoreMax(playerDto.playerId, add, String.valueOf(prefixAttribute) + "\u7cae\u98df");
        final Map<Integer, Reward> map = new HashMap<Integer, Reward>();
        map.put(3, new Reward(3, LocalMessages.T_COMM_10017, add));
        return map;
    }
    
    @Override
    public Map<Integer, Reward> getReward(final PlayerDto playerDto, final IDataGetter taskDataGetter, final Object obj) {
        final Map<Integer, Reward> map = new HashMap<Integer, Reward>();
        int add = this.num;
        if (obj != null && obj instanceof Integer) {
            add = DataCastUtil.double2int(this.num * (1.0 + taskDataGetter.getTechEffectCache().getTechEffect(playerDto.playerId, (int)obj) / 100.0));
        }
        if (obj != null && obj instanceof CityEventRate) {
            final CityEventRate cer = (CityEventRate)obj;
            final double rate = cer.rate;
            add = (int)(this.num * rate);
        }
        map.put(3, new Reward(3, LocalMessages.T_COMM_10017, add));
        return map;
    }
}
