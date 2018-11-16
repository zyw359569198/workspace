package com.reign.gcld.task.request;

import com.reign.gcld.player.dto.*;
import com.reign.gcld.task.common.*;
import com.reign.gcld.common.*;
import com.reign.gcld.task.message.*;

public class TaskRequestKillBandit extends TaskRequestBase
{
    public TaskRequestKillBandit(final String[] s) {
    }
    
    @Override
    public boolean check(final PlayerDto playerDto, final IDataGetter dataGetter, final int vId) {
        final int hasBandit = dataGetter.getPlayerAttributeDao().getHasBandit(playerDto.playerId);
        return hasBandit <= 0;
    }
    
    @Override
    public boolean doRequest(final PlayerDto playerDto, final IDataGetter dataGetter, final int vId) {
        return this.check(playerDto, dataGetter, vId);
    }
    
    @Override
    public TaskRequestProcessViewer getProcess(final PlayerDto playerDto, final IDataGetter dataGetter, final int vId) {
        final int hasBandit = dataGetter.getPlayerAttributeDao().getHasBandit(playerDto.playerId);
        final TaskRequestProcessViewer viewer = new TaskRequestProcessViewer(hasBandit <= 0, 0, 0);
        return viewer;
    }
    
    @Override
    public void handleMessage(final TaskMessage message, final IDataGetter dataGetter, final int vId) {
        if (message instanceof TaskMessageKillBandit) {
            final int hasBandit = dataGetter.getPlayerAttributeDao().getHasBandit(message.getPlayerId());
            final TaskRequestProcessViewer viewer = new TaskRequestProcessViewer(hasBandit <= 0, 0, 0);
            final TaskChangeContent taskChangeContent = new TaskChangeContent(this.getTask(), viewer.getProcessStr(), viewer.isCompleted());
            final PlayerDto playerDto = Players.getPlayer(message.getPlayerId());
            Players.push(message.getPlayerId(), PushCommand.PUSH_TASK, TaskBuilderJson.sendJsonTaskChange(taskChangeContent, playerDto, dataGetter));
        }
    }
    
    @Override
    public boolean isConcernedMessage(final TaskMessage message) {
        return message instanceof TaskMessageKillBandit;
    }
}
