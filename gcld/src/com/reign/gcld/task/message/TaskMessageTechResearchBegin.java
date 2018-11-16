package com.reign.gcld.task.message;

public class TaskMessageTechResearchBegin extends TaskMessage
{
    private int techId;
    
    public TaskMessageTechResearchBegin(final int playerId, final int techId) {
        super(playerId, 123);
        this.techId = techId;
    }
    
    public int getTechId() {
        return this.techId;
    }
}
