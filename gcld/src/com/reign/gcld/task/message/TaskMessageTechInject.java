package com.reign.gcld.task.message;

public class TaskMessageTechInject extends TaskMessage
{
    private int techId;
    
    public TaskMessageTechInject(final int playerId, final int techId) {
        super(playerId, 60);
        this.techId = techId;
    }
    
    public int getTechId() {
        return this.techId;
    }
}
