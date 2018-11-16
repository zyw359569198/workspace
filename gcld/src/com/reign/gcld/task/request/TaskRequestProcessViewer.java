package com.reign.gcld.task.request;

import com.reign.gcld.common.*;
import com.reign.util.*;

public class TaskRequestProcessViewer
{
    protected boolean completed;
    protected int wannaNum;
    protected int currNum;
    protected String processStr;
    
    public TaskRequestProcessViewer() {
        this.completed = false;
        this.wannaNum = 0;
        this.currNum = 0;
        this.processStr = "";
    }
    
    public TaskRequestProcessViewer(final boolean completed, final int wannaNum, final int currNum, final String processStr) {
        this.completed = completed;
        this.wannaNum = wannaNum;
        this.currNum = currNum;
        this.processStr = "";
    }
    
    public TaskRequestProcessViewer(final boolean completed, final int wannaNum, final int currNum) {
        this.completed = completed;
        this.wannaNum = wannaNum;
        this.currNum = currNum;
        this.processStr = "";
    }
    
    public TaskRequestProcessViewer(final boolean completed, final int wannaNum, final int currNum, final boolean showProcessStr) {
        this.completed = completed;
        this.wannaNum = wannaNum;
        this.currNum = currNum;
        this.buildProcessStr();
    }
    
    public boolean isCompleted() {
        return this.completed;
    }
    
    public void setCompleted(final boolean completed) {
        this.completed = completed;
    }
    
    public int getWannaNum() {
        return this.wannaNum;
    }
    
    public void setWannaNum(final int wannaNum) {
        this.wannaNum = wannaNum;
    }
    
    public int getCurrNum() {
        return this.currNum;
    }
    
    public void setCurrNum(final int currNum) {
        this.currNum = currNum;
    }
    
    public String getProcessStr() {
        return this.processStr;
    }
    
    public void buildProcessStr() {
        this.processStr = MessageFormatter.format(LocalMessages.T_TASK_TARGET_PROCESS_FORMAT, new Object[] { (this.currNum > this.wannaNum) ? this.wannaNum : this.currNum, this.wannaNum });
    }
}
