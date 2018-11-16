package com.reign.gcld.task.message;

public class TaskMessageBuilding extends TaskMessage
{
    private int buildingId;
    private int lv;
    
    public TaskMessageBuilding(final int playerId, final int buildingId, final int lv) {
        super(playerId, 2);
        this.buildingId = buildingId;
        this.lv = lv;
    }
    
    public int getBuildingId() {
        return this.buildingId;
    }
    
    public int getLv() {
        return this.lv;
    }
}
