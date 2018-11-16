package com.reign.gcld.task.reward;

import com.reign.gcld.player.dto.*;
import java.util.*;
import com.reign.gcld.common.*;

public class TaskRewardPeopleLoyal implements ITaskReward
{
    private int peopleLoyal;
    
    public TaskRewardPeopleLoyal(final int peopleLoyal) {
        this.peopleLoyal = peopleLoyal;
    }
    
    public TaskRewardPeopleLoyal(final String[] s) {
        if (s.length > 1) {
            this.peopleLoyal = Integer.parseInt(s[1]);
        }
        else {
            this.peopleLoyal = 0;
        }
    }
    
    @Override
    public Map<Integer, Reward> rewardPlayer(final PlayerDto playerDto, final IDataGetter taskDataGetter, final String prefixAttribute, final Object obj) {
        final int add = this.peopleLoyal + taskDataGetter.getTechEffectCache().getTechEffect(playerDto.playerId, 3);
        taskDataGetter.getPlayerPoliticsEventDao().addPeopleLoyal(playerDto.playerId, add, 100);
        final Map<Integer, Reward> map = new HashMap<Integer, Reward>();
        map.put(23, new Reward(23, LocalMessages.T_COMM_10028, add));
        return map;
    }
    
    @Override
    public Map<Integer, Reward> getReward(final PlayerDto playerDto, final IDataGetter taskDataGetter, final Object obj) {
        final Map<Integer, Reward> map = new HashMap<Integer, Reward>();
        final int add = this.peopleLoyal + taskDataGetter.getTechEffectCache().getTechEffect(playerDto.playerId, 3);
        map.put(23, new Reward(23, LocalMessages.T_COMM_10028, add));
        return map;
    }
}
