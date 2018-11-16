package com.reign.gcld.task.message;

public class TaskMessageBlood extends TaskMessage
{
    private int num;
    
    public TaskMessageBlood(final int playerId, final int num) {
        super(playerId, 28);
        this.num = num;
    }
    
    public int getNum() {
        return this.num;
    }
}
