package com.reign.gcld.task.reward;

import com.reign.gcld.player.dto.*;
import com.reign.gcld.world.common.*;
import java.util.*;
import com.reign.gcld.common.*;

public class TaskRewardIron implements ITaskReward
{
    private int num;
    
    public TaskRewardIron(final int num) {
        this.num = num;
    }
    
    public TaskRewardIron(final String[] s) {
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
        boolean joinActivity = true;
        if (obj != null && obj instanceof Boolean) {
            joinActivity = (boolean)obj;
        }
        if (obj != null && obj instanceof CityEventRate) {
            final CityEventRate cer = (CityEventRate)obj;
            final double rate = cer.rate;
            add = (int)(this.num * rate);
        }
        taskDataGetter.getPlayerResourceDao().addIronIgnoreMax(playerDto.playerId, add, String.valueOf(prefixAttribute) + "\u9554\u94c1", joinActivity);
        final Map<Integer, Reward> map = new HashMap<Integer, Reward>();
        map.put(4, new Reward(4, LocalMessages.T_COMM_10018, add));
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
        map.put(4, new Reward(4, LocalMessages.T_COMM_10018, add));
        return map;
    }
}
