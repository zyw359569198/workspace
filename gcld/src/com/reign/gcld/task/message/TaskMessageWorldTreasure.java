package com.reign.gcld.task.message;

public class TaskMessageWorldTreasure extends TaskMessage
{
    private int boxId;
    
    public TaskMessageWorldTreasure(final int playerId, final int boxId) {
        super(playerId, 121);
        this.setBoxId(boxId);
    }
    
    public void setBoxId(final int boxId) {
        this.boxId = boxId;
    }
    
    public int getBoxId() {
        return this.boxId;
    }
}
