package com.reign.gcld.task.reward;

import com.reign.gcld.player.dto.*;
import com.reign.gcld.common.*;
import com.reign.gcld.task.common.*;
import com.reign.gcld.task.domain.*;
import java.util.*;

public class TaskRewardDailyTask implements ITaskReward
{
    private int group;
    
    public TaskRewardDailyTask(final int group) {
        this.group = group;
    }
    
    public TaskRewardDailyTask(final String[] s) {
        if (s.length > 1) {
            this.group = Integer.parseInt(s[1]);
        }
        else {
            this.group = 0;
        }
    }
    
    @Override
    public Map<Integer, Reward> rewardPlayer(final PlayerDto playerDto, final IDataGetter taskDataGetter, final String prefixAttribute, final Object obj) {
        final int playerId = playerDto.playerId;
        taskDataGetter.getPlayerTaskDao().clearDailyTask(playerId);
        final List<GameTask> taskList = TaskFactory.getInstance().getDailyTaskList(this.group);
        if (taskList == null) {
            return null;
        }
        for (int index = 1; index < taskList.size(); ++index) {
            final GameTask task = taskList.get(index);
            final PlayerTask pt = new PlayerTask();
            pt.setGroupId(this.group);
            pt.setTaskId(task.getIndex());
            pt.setPlayerId(playerId);
            pt.setProcess(0);
            pt.setState(1);
            pt.setType(2);
            taskDataGetter.getPlayerTaskDao().create(pt);
        }
        return this.getReward(playerDto, taskDataGetter, obj);
    }
    
    @Override
    public Map<Integer, Reward> getReward(final PlayerDto playerDto, final IDataGetter taskDataGetter, final Object obj) {
        final Map<Integer, Reward> map = new HashMap<Integer, Reward>();
        map.put(10, new Reward(10, "dailytask", this.group));
        return map;
    }
}
