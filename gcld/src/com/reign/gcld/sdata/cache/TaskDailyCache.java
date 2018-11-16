package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.common.log.*;
import com.reign.gcld.task.common.*;
import java.util.*;
import com.reign.gcld.task.request.*;
import com.reign.gcld.task.reward.*;

@Component("taskDailyCache")
public class TaskDailyCache extends AbstractCache<Integer, TaskDaily>
{
    private Logger logger;
    @Autowired
    private SDataLoader dataLoader;
    
    public TaskDailyCache() {
        this.logger = CommonLog.getLog(TaskCache.class);
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<TaskDaily> taskList = this.dataLoader.getModels((Class)TaskDaily.class);
        for (final TaskDaily task : taskList) {
            final ITaskRequest taskRequest = TaskRequestFactory.getInstance().getTaskReQuest(task.getTarget());
            if (taskRequest == null) {
                this.logger.error("DailyTask init taskRequest fail in group:" + task.getGroup() + " index:" + task.getIndex());
            }
            else {
                final ITaskReward taskReward = TaskRewardFactory.getInstance().getTaskReward(task.getReward());
                if (taskReward == null) {
                    this.logger.error("DailyTask init taskReward fail in group:" + task.getGroup() + " index:" + task.getIndex());
                }
                else {
                    final GameTask gameTask = new GameTask(task.getGroup(), task.getIndex(), 2, task.getName(), task.getIntroS(), task.getIntroL(), taskRequest, taskReward, task.getMarkTrace(), task.getPic(), 1, "");
                    TaskFactory.getInstance().addTask(gameTask);
                }
            }
        }
    }
    
    @Override
	public void clear() {
        super.clear();
        TaskFactory.getInstance().clearDailyCache();
    }
}
