package com.reign.gcld.task.message;

public class TaskMessageWorldTreasureByType extends TaskMessage
{
    private int boxType;
    
    public TaskMessageWorldTreasureByType(final int playerId, final int boxType) {
        super(playerId, 122);
        this.setBoxType(boxType);
    }
    
    public void setBoxType(final int boxType) {
        this.boxType = boxType;
    }
    
    public int getBoxType() {
        return this.boxType;
    }
}
