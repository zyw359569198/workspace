package com.reign.gcld.sdata.cache;

import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.gcld.task.common.*;
import java.util.*;
import com.reign.gcld.task.request.*;
import com.reign.gcld.task.reward.*;
import com.reign.gcld.battle.common.*;

@Component("taskCache")
public class TaskCache extends AbstractCache<Integer, Task>
{
    @Autowired
    private SDataLoader dataLoader;
    public static Map<Integer, Items> itemMap;
    public static Map<Integer, Integer> taskSerialIdMap;
    
    static {
        TaskCache.itemMap = new HashMap<Integer, Items>();
        TaskCache.taskSerialIdMap = new HashMap<Integer, Integer>();
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<Task> taskList = this.dataLoader.getModels((Class)Task.class);
        final List<Items> itemList = this.dataLoader.getModels((Class)Items.class);
        for (final Items item : itemList) {
            TaskCache.itemMap.put(item.getId(), item);
        }
        for (final Task task : taskList) {
            super.put((Object)task.getId(), (Object)task);
            ITaskRequest taskRequest = null;
            try {
                taskRequest = TaskRequestFactory.getInstance().getTaskReQuest(task.getTarget());
            }
            catch (Exception e) {
                throw new RuntimeException("Task init taskRequest fail in taskId:" + task.getId() + "  " + e.getMessage());
            }
            if (taskRequest == null) {
                throw new RuntimeException("Task init taskRequest fail in taskId:" + task.getId());
            }
            final ITaskReward taskReward = TaskRewardFactory.getInstance().getTaskReward(task.getReward());
            if (taskReward == null) {
                throw new RuntimeException("Task init taskReward fail in taskId:" + task.getId());
            }
            final GameTask gameTask = new GameTask(task.getId(), task.getName(), task.getIntros(), task.getIntrol(), taskRequest, taskReward, task.getNext(), task.getArea(), task.getMarkTrace(), 1, task.getTarget(), task.getReward(), task.getPic(), task.getNewTrace(), task.getPlot(), task.getTelephone(), task.getIosMarktrace());
            TaskFactory.getInstance().addTask(gameTask);
        }
        this.caculateTaskSerialId();
        TaskFactory.getInstance().initTasks(taskList.size());
    }
    
    private void caculateTaskSerialId() {
        int taskId = 1;
        Task task = (Task)this.get((Object)taskId);
        int taskSerialId = 1;
        while (task != null) {
            taskId = task.getId();
            if (TaskCache.taskSerialIdMap.get(taskId) != null) {
                throw new RuntimeException("Task init taskSerialIdMap, loop detected. taskId:" + taskId);
            }
            TaskCache.taskSerialIdMap.put(taskId, taskSerialId);
            final int nextTaskId = task.getNext();
            task = (Task)this.get((Object)nextTaskId);
            if (++taskSerialId > 1000) {
                throw new RuntimeException("Task init taskSerialIdMap, loop detected");
            }
        }
    }
    
    public void printTaskSerialIdMap() {
        for (final Map.Entry<Integer, Integer> entry : TaskCache.taskSerialIdMap.entrySet()) {
            System.out.println(entry.getKey() + "\t" + entry.getValue());
        }
    }
    
    public Integer getTaskSerialId(final int taskId) {
        final Integer serialId = TaskCache.taskSerialIdMap.get(taskId);
        if (serialId == null) {
            ErrorSceneLog.getInstance().appendErrorMsg("serialId is null").append("taskId", taskId).appendClassName("TaskCache").appendMethodName("getTaskSerialId").flush();
        }
        return serialId;
    }
    
    @Override
	public void clear() {
        super.clear();
        TaskFactory.getInstance().clearCache();
        TaskCache.itemMap.clear();
        TaskCache.taskSerialIdMap.clear();
    }
}
