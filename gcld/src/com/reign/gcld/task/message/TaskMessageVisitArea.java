package com.reign.gcld.task.message;

public class TaskMessageVisitArea extends TaskMessage
{
    private int areaId;
    
    public TaskMessageVisitArea(final int playerId, final int areaId) {
        super(playerId, 3);
        this.areaId = areaId;
    }
    
    public int getAreaId() {
        return this.areaId;
    }
}
