package com.reign.gcld.task.message;

public class TaskMessageTechResearchDone extends TaskMessage
{
    private int techId;
    
    public TaskMessageTechResearchDone(final int playerId, final int techId) {
        super(playerId, 124);
        this.techId = techId;
    }
    
    public int getTechId() {
        return this.techId;
    }
}
