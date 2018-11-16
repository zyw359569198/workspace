package com.reign.gcld.task.reward;

import com.reign.gcld.player.dto.*;
import java.util.*;
import com.reign.gcld.common.*;

public class TaskRewardCopper implements ITaskReward
{
    private int num;
    
    public TaskRewardCopper(final int num) {
        this.num = num;
    }
    
    public TaskRewardCopper(final String[] s) {
        if (s.length > 1) {
            this.num = Integer.parseInt(s[1]);
        }
        else {
            this.num = 0;
        }
    }
    
    @Override
    public Map<Integer, Reward> rewardPlayer(final PlayerDto playerDto, final IDataGetter dataGetter, final String prefixAttribute, final Object obj) {
        int add = this.num;
        if (obj != null && obj instanceof Integer) {
            add = DataCastUtil.double2int(this.num * (1.0 + dataGetter.getTechEffectCache().getTechEffect(playerDto.playerId, (int)obj) / 100.0));
        }
        dataGetter.getPlayerResourceDao().addCopperIgnoreMax(playerDto.playerId, add, String.valueOf(prefixAttribute) + "\u94f6\u5e01", true);
        final Map<Integer, Reward> map = new HashMap<Integer, Reward>();
        map.put(1, new Reward(1, LocalMessages.T_COMM_10004, add));
        return map;
    }
    
    @Override
    public Map<Integer, Reward> getReward(final PlayerDto playerDto, final IDataGetter taskDataGetter, final Object obj) {
        final Map<Integer, Reward> map = new HashMap<Integer, Reward>();
        int add = this.num;
        if (obj != null && obj instanceof Integer) {
            add = DataCastUtil.double2int(this.num * (1.0 + taskDataGetter.getTechEffectCache().getTechEffect(playerDto.playerId, (int)obj) / 100.0));
        }
        map.put(1, new Reward(1, LocalMessages.T_COMM_10004, add));
        return map;
    }
}
