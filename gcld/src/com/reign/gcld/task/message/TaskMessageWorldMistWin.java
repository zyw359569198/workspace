package com.reign.gcld.task.message;

public class TaskMessageWorldMistWin extends TaskMessage
{
    int area;
    
    public TaskMessageWorldMistWin(final int playerId, final int area) {
        super(playerId, 67);
        this.area = area;
    }
    
    public int getArea() {
        return this.area;
    }
}
