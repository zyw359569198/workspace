package com.reign.gcld.task.message;

public class TaskMessageUseIncense extends TaskMessage
{
    private int id;
    
    public TaskMessageUseIncense(final int playerId, final int id) {
        super(playerId, 57);
        this.id = id;
    }
    
    public int getId() {
        return this.id;
    }
}
