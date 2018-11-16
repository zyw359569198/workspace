package com.reign.gcld.task.request;

import com.reign.gcld.player.dto.*;
import com.reign.gcld.task.common.*;
import com.reign.gcld.common.*;
import com.reign.gcld.task.message.*;

public class TaskRequestChoseSide extends TaskRequestBase
{
    public TaskRequestChoseSide(final String[] s) {
    }
    
    @Override
    public boolean check(final PlayerDto playerDto, final IDataGetter taskDataGetter, final int vId) {
        return true;
    }
    
    @Override
    public boolean doRequest(final PlayerDto playerDto, final IDataGetter taskDataGetter, final int vId) {
        return this.check(playerDto, taskDataGetter, vId);
    }
    
    @Override
    public void handleMessage(final TaskMessage message, final IDataGetter dataGetter, final int vId) {
        if (message instanceof TaskMessageChoseSide) {
            final TaskRequestProcessViewer viewer = new TaskRequestProcessViewer(true, 1, 1);
            final TaskChangeContent taskChangeContent = new TaskChangeContent(this.getTask(), viewer.getProcessStr(), viewer.isCompleted());
            final PlayerDto playerDto = Players.getPlayer(message.getPlayerId());
            Players.push(message.getPlayerId(), PushCommand.PUSH_TASK, TaskBuilderJson.sendJsonTaskChange(taskChangeContent, playerDto, dataGetter));
        }
    }
    
    @Override
    public boolean isConcernedMessage(final TaskMessage message) {
        return message instanceof TaskMessageChoseSide;
    }
    
    @Override
    public TaskRequestProcessViewer getProcess(final PlayerDto playerDto, final IDataGetter dataGetter, final int vId) {
        final boolean completed = this.check(playerDto, dataGetter, vId);
        final TaskRequestProcessViewer rtn = new TaskRequestProcessViewer(completed, 1, completed ? 1 : 0);
        return rtn;
    }
}
