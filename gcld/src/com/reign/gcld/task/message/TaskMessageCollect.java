package com.reign.gcld.task.message;

public class TaskMessageCollect extends TaskMessage
{
    private int itemId;
    
    public TaskMessageCollect(final int playerId, final int itemId) {
        super(playerId, 66);
        this.setItemId(itemId);
    }
    
    public int getItemId() {
        return this.itemId;
    }
    
    public void setItemId(final int itemId) {
        this.itemId = itemId;
    }
}
