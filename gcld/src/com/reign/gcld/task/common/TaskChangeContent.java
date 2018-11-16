package com.reign.gcld.task.common;

public class TaskChangeContent
{
    private int taskId;
    private String taskName;
    private String intros;
    private String introl;
    private String processStr;
    private boolean requestCompleted;
    private String markTrace;
    private String iosMarktrace;
    private String newTrace;
    private int area;
    private String pic;
    private String plot;
    private int type;
    private int group;
    private int index;
    
    public TaskChangeContent() {
        this.taskId = 0;
        this.taskName = "";
        this.introl = "";
        this.intros = "";
        this.processStr = "";
        this.requestCompleted = false;
        this.markTrace = "";
        this.iosMarktrace = "";
        this.area = 0;
        this.pic = "";
        this.newTrace = "";
        this.plot = "";
    }
    
    public TaskChangeContent(final int taskId, final String taskName, final String intros, final String introl, final String processStr, final boolean requestCompleted, final String target, final int area, final int power, final String coordinate, final int trace, final int rewardEffect, final String markTrace, final String iosMarktrace, final String pic, final String plot) {
        this.taskId = taskId;
        this.taskName = taskName;
        this.intros = intros;
        this.introl = introl;
        this.processStr = processStr;
        this.requestCompleted = requestCompleted;
        this.markTrace = markTrace;
        this.iosMarktrace = iosMarktrace;
        this.area = area;
        this.pic = pic;
        this.plot = plot;
    }
    
    public TaskChangeContent(final GameTask gameTask, final String processStr, final boolean requestCompleted, final boolean isCompleteByLv) {
        this.taskId = gameTask.getId();
        this.taskName = gameTask.getName();
        this.intros = gameTask.getIntros();
        this.introl = gameTask.getIntrol();
        this.processStr = processStr;
        this.requestCompleted = requestCompleted;
        this.area = gameTask.getAreaId();
        this.pic = gameTask.getPic();
    }
    
    public TaskChangeContent(final GameTask gameTask, final String processStr, final boolean requestCompleted) {
        this.taskId = gameTask.getId();
        this.taskName = gameTask.getName();
        this.intros = gameTask.getIntros();
        this.introl = gameTask.getIntrol();
        this.processStr = processStr;
        this.requestCompleted = requestCompleted;
        this.markTrace = gameTask.getMarkTrace();
        this.iosMarktrace = gameTask.getIosMarktrace();
        this.newTrace = gameTask.getNewTrace();
        this.type = gameTask.getType();
        this.group = gameTask.getGroup();
        this.index = gameTask.getIndex();
        this.area = gameTask.getAreaId();
        this.pic = gameTask.getPic();
        this.plot = gameTask.getPlot();
    }
    
    public int getTaskId() {
        return this.taskId;
    }
    
    public String getTaskName() {
        return this.taskName;
    }
    
    public String getProcessStr() {
        return this.processStr;
    }
    
    public boolean isRequestCompleted() {
        return this.requestCompleted;
    }
    
    public String getIntros() {
        return this.intros;
    }
    
    public String getIntrol() {
        return this.introl;
    }
    
    public String getMarkTrace() {
        return this.markTrace;
    }
    
    public String getIosMarkTrace() {
        return this.iosMarktrace;
    }
    
    public int getType() {
        return this.type;
    }
    
    public void setType(final int type) {
        this.type = type;
    }
    
    public int getGroup() {
        return this.group;
    }
    
    public void setGroup(final int group) {
        this.group = group;
    }
    
    public int getIndex() {
        return this.index;
    }
    
    public void setIndex(final int index) {
        this.index = index;
    }
    
    public void setArea(final int area) {
        this.area = area;
    }
    
    public int getArea() {
        return this.area;
    }
    
    public void setPic(final String pic) {
        this.pic = pic;
    }
    
    public String getPic() {
        return this.pic;
    }
    
    public String getNewTrace() {
        return this.newTrace;
    }
    
    public String getPlot() {
        return this.plot;
    }
    
    public void setPlot(final String plot) {
        this.plot = plot;
    }
}
