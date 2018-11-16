package com.reign.gcld.task.reward;

import com.reign.gcld.player.dto.*;
import java.util.*;
import com.reign.gcld.common.*;

public class TaskRewardTicket implements ITaskReward
{
    private int num;
    
    public TaskRewardTicket(final int num) {
        this.num = num;
    }
    
    public TaskRewardTicket(final String[] s) {
        if (s.length > 1) {
            this.num = Integer.parseInt(s[1]);
        }
        else {
            this.num = 0;
        }
    }
    
    @Override
    public Map<Integer, Reward> rewardPlayer(final PlayerDto playerDto, final IDataGetter taskDataGetter, final String prefixAttribute, final Object obj) {
        taskDataGetter.getPlayerTicketsDao().addTickets(playerDto.playerId, this.num, prefixAttribute, true);
        return this.getReward(playerDto, taskDataGetter, obj);
    }
    
    @Override
    public Map<Integer, Reward> getReward(final PlayerDto playerDto, final IDataGetter taskDataGetter, final Object obj) {
        final Map<Integer, Reward> map = new HashMap<Integer, Reward>();
        map.put(100, new Reward(100, LocalMessages.T_COMM_10054, this.num));
        return map;
    }
}
