package com.reign.gcld.store.service;

import com.reign.gcld.task.message.*;

public class TaskQuenchingMessage extends TaskMessage
{
    public TaskQuenchingMessage(final int playerId) {
        super(playerId, 126);
    }
}
