package com.reign.gcld.task.message;

import com.reign.gcld.common.message.*;

public class TaskMessage implements Message
{
    private int playerId;
    private int taskMessageType;
    
    public TaskMessage(final int playerId, final int taskMessageType) {
        this.playerId = playerId;
        this.taskMessageType = taskMessageType;
    }
    
    public int getPlayerId() {
        return this.playerId;
    }
    
    public int getTaskMessageType() {
        return this.taskMessageType;
    }
}
