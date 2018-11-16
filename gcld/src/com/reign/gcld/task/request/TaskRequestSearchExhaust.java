package com.reign.gcld.task.request;

import com.reign.gcld.player.dto.*;
import com.reign.gcld.task.common.*;
import com.reign.gcld.common.*;
import com.reign.gcld.task.message.*;

public class TaskRequestSearchExhaust extends TaskRequestBase
{
    public TaskRequestSearchExhaust(final String[] s) {
    }
    
    @Override
    public boolean isConcernedMessage(final TaskMessage message) {
        return message instanceof TaskMessageSearchExhaust;
    }
    
    @Override
    public boolean check(final PlayerDto playerDto, final IDataGetter dataGetter, final int vId) {
        return dataGetter.getPlayerSearchDao().read(playerDto.playerId).getSearchNumLeft() == 0;
    }
    
    @Override
    public boolean doRequest(final PlayerDto playerDto, final IDataGetter dataGetter, final int vId) {
        return this.check(playerDto, dataGetter, vId);
    }
    
    @Override
    public TaskRequestProcessViewer getProcess(final PlayerDto playerDto, final IDataGetter dataGetter, final int vId) {
        final boolean completed = this.check(playerDto, dataGetter, vId);
        final TaskRequestProcessViewer rtn = new TaskRequestProcessViewer(completed, 1, completed ? 1 : 0);
        return rtn;
    }
    
    @Override
    public void handleMessage(final TaskMessage message, final IDataGetter dataGetter, final int vId) {
        if (message instanceof TaskMessageSearchExhaust) {
            final PlayerDto playerDto = Players.getPlayer(message.getPlayerId());
            final boolean finished = dataGetter.getPlayerSearchDao().read(playerDto.playerId).getSearchNumLeft() == 0;
            final TaskRequestProcessViewer viewer = new TaskRequestProcessViewer(finished, 1, finished ? 1 : 0);
            final TaskChangeContent taskChangeContent = new TaskChangeContent(this.getTask(), viewer.getProcessStr(), viewer.isCompleted());
            Players.push(message.getPlayerId(), PushCommand.PUSH_TASK, TaskBuilderJson.sendJsonTaskChange(taskChangeContent, playerDto, dataGetter));
        }
    }
}
