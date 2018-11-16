package com.reign.gcld.task.reward;

import com.reign.gcld.player.dto.*;
import com.reign.gcld.common.*;
import com.reign.gcld.task.domain.*;
import com.reign.gcld.task.common.*;
import java.util.*;

public class TaskRewardBrunchTask implements ITaskReward
{
    private int branch;
    
    public TaskRewardBrunchTask(final int branch) {
        this.branch = branch;
    }
    
    public TaskRewardBrunchTask(final String[] s) {
        if (s.length > 1) {
            this.branch = Integer.parseInt(s[1]);
        }
        else {
            this.branch = 0;
        }
    }
    
    @Override
    public Map<Integer, Reward> rewardPlayer(final PlayerDto playerDto, final IDataGetter taskDataGetter, final String prefixAttribute, final Object obj) {
        final GameTask task = TaskFactory.getInstance().getTask(this.branch, 1, 3);
        if (task != null && taskDataGetter.getPlayerTaskDao().getBranchTask(playerDto.playerId, this.branch, 1) == null) {
            final PlayerTask pt = new PlayerTask();
            pt.setGroupId(this.branch);
            pt.setTaskId(task.getIndex());
            pt.setPlayerId(playerDto.playerId);
            pt.setProcess(0);
            pt.setType(3);
            pt.setState(1);
            pt.setStartTime(System.currentTimeMillis());
            taskDataGetter.getPlayerTaskDao().create(pt);
        }
        return this.getReward(playerDto, taskDataGetter, obj);
    }
    
    @Override
    public Map<Integer, Reward> getReward(final PlayerDto playerDto, final IDataGetter taskDataGetter, final Object obj) {
        final Map<Integer, Reward> map = new HashMap<Integer, Reward>();
        map.put(11, new Reward(11, "brunchtask", this.branch));
        return map;
    }
}
