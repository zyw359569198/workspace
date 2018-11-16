package com.reign.gcld.task.request;

import com.reign.gcld.task.common.*;
import com.reign.gcld.common.*;
import com.reign.gcld.task.message.*;
import com.reign.gcld.task.domain.*;
import com.reign.gcld.player.dto.*;

public class TaskRequestChooseGeneral extends TaskRequestCount
{
    public TaskRequestChooseGeneral(final String[] s) {
        super(1);
    }
    
    @Override
    public void handleMessage(final TaskMessage message, final IDataGetter taskDataGetter, final int vId) {
        if (message instanceof TaskMessageChooseGeneral) {
            final PlayerTask playerTask = taskDataGetter.getPlayerTaskDao().read(vId);
            if (playerTask == null) {
                return;
            }
            taskDataGetter.getPlayerTaskDao().addProcess(vId, 1);
            final TaskRequestProcessViewer viewer = new TaskRequestProcessViewer(playerTask.getProcess() + 1 >= this.getTimes(), this.getTimes(), playerTask.getProcess() + 1);
            final TaskChangeContent taskChangeContent = new TaskChangeContent(this.getTask(), viewer.getProcessStr(), viewer.isCompleted());
            final PlayerDto playerDto = Players.getPlayer(message.getPlayerId());
            Players.push(message.getPlayerId(), PushCommand.PUSH_TASK, TaskBuilderJson.sendJsonTaskChange(taskChangeContent, playerDto, taskDataGetter));
        }
    }
    
    @Override
    public boolean isConcernedMessage(final TaskMessage message) {
        return message instanceof TaskMessageChooseGeneral;
    }
}
