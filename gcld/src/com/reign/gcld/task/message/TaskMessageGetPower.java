package com.reign.gcld.task.message;

public class TaskMessageGetPower extends TaskMessage
{
    private int powerId;
    
    public TaskMessageGetPower(final int playerId, final int powerId) {
        super(playerId, 8);
        this.powerId = powerId;
    }
    
    public int getPowerId() {
        return this.powerId;
    }
}
