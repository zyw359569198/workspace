package com.reign.gcld.task.common;

import com.reign.gcld.common.log.*;
import java.util.*;

public class TaskFactory
{
    private Logger logger;
    private static final TaskFactory instance;
    private Map<Integer, GameTask> taskMap;
    private Map<Integer, GameTask> taskSeqMap;
    private Map<Integer, List<GameTask>> taskDailyGroupMap;
    private Map<Integer, List<GameTask>> taskBranchGroupMap;
    private Map<Integer, Integer> functionMap;
    public int lastTaskId;
    private int taskNum;
    
    static {
        instance = new TaskFactory();
    }
    
    public Map<Integer, Integer> getFunctionMap() {
        return this.functionMap;
    }
    
    public void clearCache() {
        this.taskMap.clear();
        this.taskSeqMap.clear();
        this.functionMap.clear();
        this.lastTaskId = 1;
        this.taskNum = 0;
    }
    
    public void clearBranchCache() {
        this.taskBranchGroupMap.clear();
    }
    
    public void clearDailyCache() {
        this.taskDailyGroupMap.clear();
    }
    
    private TaskFactory() {
        this.logger = CommonLog.getLog(TaskFactory.class);
        this.taskMap = new HashMap<Integer, GameTask>();
        this.taskSeqMap = new HashMap<Integer, GameTask>();
        this.taskDailyGroupMap = new HashMap<Integer, List<GameTask>>();
        this.taskBranchGroupMap = new HashMap<Integer, List<GameTask>>();
        this.functionMap = new HashMap<Integer, Integer>();
        this.lastTaskId = 1;
        this.taskNum = 0;
    }
    
    public static TaskFactory getInstance() {
        return TaskFactory.instance;
    }
    
    public GameTask getTask(final int taskId) {
        return this.taskMap.get(taskId);
    }
    
    public GameTask getTask(final int group, final int index, final int type) {
        if (type == 2) {
            if (index >= this.taskDailyGroupMap.get(group).size()) {
                return null;
            }
            return this.taskDailyGroupMap.get(group).get(index);
        }
        else {
            if (type != 3) {
                return null;
            }
            if (this.taskBranchGroupMap.get(group) == null) {
                return null;
            }
            if (index >= this.taskBranchGroupMap.get(group).size()) {
                return null;
            }
            return this.taskBranchGroupMap.get(group).get(index);
        }
    }
    
    public List<GameTask> getDailyTaskList(final int group) {
        return this.taskDailyGroupMap.get(group);
    }
    
    public GameTask getTaskBySeq(final int seq) {
        return this.taskSeqMap.get(seq);
    }
    
    public GameTask getNextTask(final GameTask task) {
        if (task != null && task.getNextTaskId() != -1) {
            return this.taskMap.get(task.getNextTaskId());
        }
        return null;
    }
    
    public GameTask getFirstTask() {
        return this.taskMap.get(1);
    }
    
    public void addTask(final GameTask gameTask) {
        if (gameTask.getType() == 1) {
            this.taskMap.put(gameTask.getId(), gameTask);
            if (gameTask.getId() > this.lastTaskId) {
                this.lastTaskId = gameTask.getId();
            }
        }
        else if (gameTask.getType() == 2) {
            List<GameTask> list = this.taskDailyGroupMap.get(gameTask.getGroup());
            if (list == null) {
                list = new ArrayList<GameTask>();
                list.add(gameTask);
                this.taskDailyGroupMap.put(gameTask.getGroup(), list);
            }
            list.add(gameTask);
        }
        else if (gameTask.getType() == 3) {
            List<GameTask> list = this.taskBranchGroupMap.get(gameTask.getGroup());
            if (list == null) {
                list = new ArrayList<GameTask>();
                list.add(gameTask);
                this.taskBranchGroupMap.put(gameTask.getGroup(), list);
            }
            list.add(gameTask);
        }
    }
    
    public int getLastTaskId() {
        return this.lastTaskId;
    }
    
    public int getTaskNum() {
        return this.taskNum;
    }
    
    public void initTasks(final int size) {
        GameTask task = this.getFirstTask();
        while (task != null) {
            task.setSeq(++this.taskNum);
            final GameTask nextTask = this.getNextTask(task);
            task.setNextTask(nextTask);
            if (nextTask != null) {
                nextTask.setPrevTask(task);
            }
            this.taskSeqMap.put(this.taskNum, task);
            if (task.getReward() != null) {
                final String[] rewards = task.getReward().split(";");
                String[] array;
                for (int length = (array = rewards).length, i = 0; i < length; ++i) {
                    final String str = array[i];
                    final String[] s = str.split(",");
                    if ("functionId".equalsIgnoreCase(s[0])) {
                        this.functionMap.put(Integer.valueOf(s[1]), this.taskNum);
                    }
                }
            }
            task = nextTask;
            if (this.taskNum > size) {
                this.logger.info("Task init may be fail, next taskId in loop execution!");
                break;
            }
        }
    }
}
