package com.reign.gcld.task.reward;

import com.reign.gcld.common.log.*;
import com.reign.gcld.player.dto.*;
import com.reign.gcld.common.*;
import java.util.*;

public class TaskRewardAnd implements ITaskReward
{
    Logger logger;
    private List<ITaskReward> rewardList;
    
    public TaskRewardAnd(final String[] rewards) {
        this.logger = CommonLog.getLog(TaskRewardAnd.class);
        this.rewardList = new ArrayList<ITaskReward>();
        for (int i = 0; i < rewards.length; ++i) {
            if (!rewards[i].trim().isEmpty()) {
                final ITaskReward taskReward = TaskRewardFactory.getInstance().getTaskReward(rewards[i]);
                if (taskReward == null) {
                    this.logger.error("Task init AND taskRequest fail in rewards:[" + rewards.toString() + "],reward[" + rewards[i] + "]");
                    final StringBuilder sb = new StringBuilder();
                    for (final String s : rewards) {
                        sb.append(s).append(";");
                    }
                    throw new RuntimeException("Task init AND taskRequest fail in rewards:[" + sb.toString() + "],reward[" + rewards[i] + "]");
                }
                this.rewardList.add(taskReward);
            }
        }
    }
    
    @Override
    public Map<Integer, Reward> rewardPlayer(final PlayerDto playerDto, final IDataGetter taskDataGetter, final String prefixAttribute, final Object obj) {
        final Map<Integer, Reward> rewardMap = new HashMap<Integer, Reward>();
        for (final ITaskReward taskReward : this.rewardList) {
            final Map<Integer, Reward> map = taskReward.rewardPlayer(playerDto, taskDataGetter, prefixAttribute, obj);
            for (final Object o : map.keySet()) {
                rewardMap.put((Integer)o, map.get(o));
            }
        }
        return rewardMap;
    }
    
    @Override
    public Map<Integer, Reward> getReward(final PlayerDto playerDto, final IDataGetter taskDataGetter, final Object obj) {
        final Map<Integer, Reward> rewardMap = new HashMap<Integer, Reward>();
        for (final ITaskReward taskReward : this.rewardList) {
            final Map<Integer, Reward> map = taskReward.getReward(playerDto, taskDataGetter, obj);
            for (final Object obj2 : map.keySet()) {
                rewardMap.put((Integer)obj2, map.get(obj2));
            }
        }
        return rewardMap;
    }
}
