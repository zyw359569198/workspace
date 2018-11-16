package com.reign.gcld.task.message;

public class TaskMessageAtt extends TaskMessage
{
    private int num;
    
    public TaskMessageAtt(final int playerId, final int num) {
        super(playerId, 26);
        this.num = num;
    }
    
    public int getNum() {
        return this.num;
    }
}
