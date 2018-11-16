package com.reign.gcld.task.reward;

import com.reign.gcld.player.dto.*;
import com.reign.framework.json.*;
import com.reign.gcld.common.*;
import java.util.*;

public class TaskRewardFreeConstruction implements ITaskReward
{
    private int num;
    
    public TaskRewardFreeConstruction(final int num) {
        this.num = num;
    }
    
    public TaskRewardFreeConstruction(final String[] s) {
        if (s.length > 1) {
            this.num = Integer.parseInt(s[1]);
        }
        else {
            this.num = 0;
        }
    }
    
    @Override
    public Map<Integer, Reward> rewardPlayer(final PlayerDto playerDto, final IDataGetter taskDataGetter, final String prefixAttribute, final Object obj) {
        taskDataGetter.getPlayerAttributeDao().addFreeConstructionNum(playerDto.playerId, this.num, prefixAttribute);
        Players.push(playerDto.playerId, PushCommand.PUSH_UPDATE, JsonBuilder.getSimpleJson("freeConsNum", this.num));
        return this.getReward(playerDto, taskDataGetter, obj);
    }
    
    @Override
    public Map<Integer, Reward> getReward(final PlayerDto playerDto, final IDataGetter taskDataGetter, final Object obj) {
        final Map<Integer, Reward> map = new HashMap<Integer, Reward>();
        map.put(40, new Reward(40, "free_construction", this.num));
        return map;
    }
}
