package com.reign.gcld.task.reward;

import com.reign.gcld.player.dto.*;
import java.util.*;
import com.reign.gcld.common.*;

public class TaskRewardAutoConstructionStop implements ITaskReward
{
    @Override
    public Map<Integer, Reward> rewardPlayer(final PlayerDto playerDto, final IDataGetter taskDataGetter, final String prefixAttribute, final Object obj) {
        final byte[] send = taskDataGetter.getBuildingService().stopAutoUpBuilding(playerDto.playerId);
        Players.push(playerDto.playerId, PushCommand.PUSH_BUILDING_UPGRADE, send);
        return this.getReward(playerDto, taskDataGetter, obj);
    }
    
    @Override
    public Map<Integer, Reward> getReward(final PlayerDto playerDto, final IDataGetter taskDataGetter, final Object obj) {
        final Map<Integer, Reward> map = new HashMap<Integer, Reward>();
        map.put(32, new Reward(32, LocalMessages.T_COMM_10007, 1));
        return map;
    }
}
