package com.reign.gcld.task.message;

public class TaskMessageOpenBluePrint extends TaskMessage
{
    private int id;
    
    public TaskMessageOpenBluePrint(final int playerId, final int id) {
        super(playerId, 128);
        this.id = id;
    }
    
    public int getid() {
        return this.id;
    }
}
