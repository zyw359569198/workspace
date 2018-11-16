package com.reign.gcld.task.reward;

import com.reign.gcld.player.dto.*;
import com.reign.gcld.common.*;
import com.reign.gcld.player.domain.*;
import java.util.*;

public class TaskRewardGem implements ITaskReward
{
    private int degree;
    private int num;
    
    public TaskRewardGem(final int degree, final int num) {
        this.degree = degree;
        this.num = num;
    }
    
    public TaskRewardGem(final String[] s) {
        if (s.length > 2) {
            this.degree = Integer.parseInt(s[1]);
            this.num = Integer.parseInt(s[2]);
        }
        else {
            this.degree = 0;
            this.num = 0;
        }
    }
    
    @Override
    public Map<Integer, Reward> rewardPlayer(final PlayerDto playerDto, final IDataGetter taskDataGetter, final String prefixAttribute, final Object obj) {
        final Player player = taskDataGetter.getPlayerDao().read(playerDto.playerId);
        taskDataGetter.getStoreHouseService().gainGem(player, this.num, this.degree, LocalMessages.T_LOG_GEM_12, null);
        return this.getReward(playerDto, taskDataGetter, obj);
    }
    
    @Override
    public Map<Integer, Reward> getReward(final PlayerDto playerDto, final IDataGetter taskDataGetter, final Object obj) {
        final Map<Integer, Reward> map = new HashMap<Integer, Reward>();
        map.put(11, new Reward(11, LocalMessages.T_COMM_10023, this.num, this.degree));
        return map;
    }
}
