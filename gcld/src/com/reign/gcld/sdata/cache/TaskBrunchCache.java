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

@Component("taskBrunchCache")
public class TaskBrunchCache extends AbstractCache<Integer, TaskBrunch>
{
    private Logger logger;
    @Autowired
    private SDataLoader dataLoader;
    
    public TaskBrunchCache() {
        this.logger = CommonLog.getLog(TaskCache.class);
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<TaskBrunch> taskList = this.dataLoader.getModels((Class)TaskBrunch.class);
        for (final TaskBrunch task : taskList) {
            final ITaskRequest taskRequest = TaskRequestFactory.getInstance().getTaskReQuest(task.getTarget());
            if (taskRequest == null) {
                this.logger.error("BrunchTask init taskRequest fail in group:" + task.getBrunch() + " index:" + task.getIndex());
                throw new RuntimeException("BrunchTask init taskRequest fail in group:" + task.getBrunch() + " index:" + task.getIndex());
            }
            final ITaskReward taskReward = TaskRewardFactory.getInstance().getTaskReward(task.getReward());
            if (taskReward == null) {
                this.logger.error("BrunchTask init taskReward fail in group:" + task.getBrunch() + " index:" + task.getIndex());
                throw new RuntimeException("BrunchTask init taskReward fail in group:" + task.getBrunch() + " index:" + task.getIndex());
            }
            final GameTask gameTask = new GameTask(task.getBrunch(), task.getIndex(), 3, task.getName(), task.getIntroS(), task.getIntroL(), taskRequest, taskReward, task.getMarktrace(), task.getPic(), task.getTelephone(), task.getIosMarktrace());
            TaskFactory.getInstance().addTask(gameTask);
        }
    }
    
    @Override
	public void clear() {
        super.clear();
        TaskFactory.getInstance().clearBranchCache();
    }
}
