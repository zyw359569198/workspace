package com.reign.gcld.task.message;

public class TaskMessageStoreBuy extends TaskMessage
{
    private int num;
    
    public TaskMessageStoreBuy(final int playerId, final int num) {
        super(playerId, 13);
        this.num = num;
    }
    
    public int getNum() {
        return this.num;
    }
}
