package com.reign.gcld.task.reward;

import com.reign.gcld.player.dto.*;
import com.reign.framework.json.*;
import com.reign.gcld.common.*;
import java.util.*;

public class TaskRewardRefreshStore implements ITaskReward
{
    @Override
    public Map<Integer, Reward> rewardPlayer(final PlayerDto playerDto, final IDataGetter taskDataGetter, final String prefixAttribute, final Object obj) {
        Players.push(playerDto.playerId, PushCommand.PUSH_STORE, JsonBuilder.getSimpleJson("store", 1));
        return this.getReward(playerDto, taskDataGetter, obj);
    }
    
    @Override
    public Map<Integer, Reward> getReward(final PlayerDto playerDto, final IDataGetter taskDataGetter, final Object obj) {
        final Map<Integer, Reward> map = new HashMap<Integer, Reward>();
        map.put(38, new Reward(38, "refresh_store", 1));
        return map;
    }
}
