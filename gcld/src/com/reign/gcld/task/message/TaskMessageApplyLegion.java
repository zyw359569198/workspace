package com.reign.gcld.task.message;

public class TaskMessageApplyLegion extends TaskMessage
{
    private int num;
    
    public TaskMessageApplyLegion(final int playerId, final int num) {
        super(playerId, 9);
        this.num = num;
    }
    
    public int getNum() {
        return this.num;
    }
}
