package com.reign.gcld.task.reward;

import com.reign.gcld.player.dto.*;
import com.reign.gcld.player.domain.*;
import java.util.*;
import com.reign.gcld.common.*;

public class TaskRewardGold implements ITaskReward
{
    private int num;
    
    public TaskRewardGold(final int num) {
        this.num = num;
    }
    
    public TaskRewardGold(final String[] s) {
        if (s.length > 1) {
            this.num = Integer.parseInt(s[1]);
        }
        else {
            this.num = 0;
        }
    }
    
    @Override
    public Map<Integer, Reward> rewardPlayer(final PlayerDto playerDto, final IDataGetter dataGetter, final String prefixAttribute, final Object obj) {
        final Player player = dataGetter.getPlayerDao().read(playerDto.playerId);
        dataGetter.getPlayerDao().addSysGold(player, this.num, String.valueOf(prefixAttribute) + "\u91d1\u5e01");
        return this.getReward(playerDto, dataGetter, obj);
    }
    
    @Override
    public Map<Integer, Reward> getReward(final PlayerDto playerDto, final IDataGetter taskDataGetter, final Object obj) {
        final Map<Integer, Reward> map = new HashMap<Integer, Reward>();
        map.put(19, new Reward(19, LocalMessages.T_COMM_10009, this.num));
        return map;
    }
}
