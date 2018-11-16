package com.reign.gcld.task.message;

public class TaskMessageDinner extends TaskMessage
{
    private int triggerType;
    
    public TaskMessageDinner(final int playerId, final int triggerType) {
        super(playerId, 48);
        this.triggerType = 0;
        this.triggerType = triggerType;
    }
    
    public void setTriggerType(final int triggerType) {
        this.triggerType = triggerType;
    }
    
    public int getTriggerType() {
        return this.triggerType;
    }
}
