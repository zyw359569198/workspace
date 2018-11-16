package com.reign.gcld.task.request;

import com.reign.gcld.task.common.*;
import com.reign.gcld.common.*;
import com.reign.gcld.task.message.*;
import com.reign.gcld.player.dto.*;

public class TaskRequestHallsFight extends TaskRequestCount
{
    public TaskRequestHallsFight(final String[] s) {
        super(1);
    }
    
    @Override
    public void handleMessage(final TaskMessage message, final IDataGetter taskDataGetter, final int vId) {
        if (message instanceof TaskMessageHallsFight) {
            final TaskRequestProcessViewer viewer = new TaskRequestProcessViewer(true, this.getTimes(), 1, "");
            taskDataGetter.getPlayerTaskDao().addProcess(vId, 1);
            final TaskChangeContent taskChangeContent = new TaskChangeContent(this.getTask(), viewer.getProcessStr(), viewer.isCompleted());
            final PlayerDto playerDto = Players.getPlayer(message.getPlayerId());
            Players.push(message.getPlayerId(), PushCommand.PUSH_TASK, TaskBuilderJson.sendJsonTaskChange(taskChangeContent, playerDto, taskDataGetter));
        }
    }
    
    @Override
    public boolean isConcernedMessage(final TaskMessage message) {
        return message instanceof TaskMessageHallsFight;
    }
}
