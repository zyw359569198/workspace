package com.reign.gcld.task.message;

public class TaskMessageDef extends TaskMessage
{
    private int num;
    
    public TaskMessageDef(final int playerId, final int num) {
        super(playerId, 27);
        this.num = num;
    }
    
    public int getNum() {
        return this.num;
    }
}
