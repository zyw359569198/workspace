package com.reign.gcld.task.message;

public class TaskMessageRecuitGeneral extends TaskMessage
{
    int generalType;
    int generalId;
    
    public TaskMessageRecuitGeneral(final int playerId, final int generalType, final int generalId) {
        super(playerId, 74);
        this.generalType = generalType;
        this.generalId = generalId;
    }
    
    public int getGeneralType() {
        return this.generalType;
    }
    
    public int getGeneralId() {
        return this.generalId;
    }
}
