package com.reign.gcld.task.message;

public class TaskMessageOfficer extends TaskMessage
{
    private int num;
    
    public TaskMessageOfficer(final int playerId, final int num) {
        super(playerId, 5);
        this.num = num;
    }
    
    public int getNum() {
        return this.num;
    }
}
