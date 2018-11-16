package com.reign.gcld.timer;

public class TimerTask
{
    private int taskId;
    private long executionTime;
    private String className;
    private String methodName;
    private String params;
    private boolean sync;
    private boolean done;
    private boolean cancelled;
    
    public TimerTask() {
    }
    
    public TimerTask(final long executionTime, final String className, final String methodName, final String params) {
        this.executionTime = executionTime;
        this.className = className;
        this.methodName = methodName;
        this.params = params;
        this.taskId = -1;
        this.sync = false;
        this.done = false;
    }
    
    public TimerTask(final int taskId, final long executionTime, final String className, final String methodName, final String params, final boolean isSync) {
        this.executionTime = executionTime;
        this.className = className;
        this.methodName = methodName;
        this.params = params;
        this.taskId = taskId;
        this.sync = isSync;
        this.done = false;
    }
    
    public TimerTask(final long executionTime, final String className, final String methodName) {
        this.executionTime = executionTime;
        this.className = className;
        this.methodName = methodName;
        this.params = "";
        this.taskId = -1;
        this.sync = false;
        this.done = false;
    }
    
    public boolean isSync() {
        return this.sync;
    }
    
    public void setSync(final boolean sync) {
        this.sync = sync;
    }
    
    public boolean isDone() {
        return this.done;
    }
    
    public void setDone(final boolean done) {
        this.done = done;
    }
    
    public int getTaskId() {
        return this.taskId;
    }
    
    public void setTaskId(final int taskId) {
        this.taskId = taskId;
    }
    
    public long getExecutionTime() {
        return this.executionTime;
    }
    
    public String getExecutor() {
        return this.className;
    }
    
    public String getExecutorMethod() {
        return this.methodName;
    }
    
    public String getParams() {
        return this.params;
    }
    
    public void setParams(final String params) {
        this.params = params;
    }
    
    public boolean isCancelled() {
        return this.cancelled;
    }
    
    public void setCancelled(final boolean cancelled) {
        this.cancelled = cancelled;
    }
    
    public String getMethodName() {
        return this.methodName;
    }
    
    public void setMethodName(final String methodName) {
        this.methodName = methodName;
    }
    
    public void setExecutionTime(final long executionTime) {
        this.executionTime = executionTime;
    }
    
    public String getClassName() {
        return this.className;
    }
    
    public void setClassName(final String className) {
        this.className = className;
    }
}
