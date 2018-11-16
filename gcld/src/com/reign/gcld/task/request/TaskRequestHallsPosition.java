package com.reign.gcld.task.request;

import com.reign.gcld.task.common.*;
import com.reign.gcld.common.*;
import com.reign.gcld.task.message.*;
import com.reign.gcld.player.domain.*;
import com.reign.gcld.player.dto.*;

public class TaskRequestHallsPosition extends TaskRequestBase
{
    public TaskRequestHallsPosition(final String[] s) {
    }
    
    @Override
    public void handleMessage(final TaskMessage message, final IDataGetter taskDataGetter, final int vId) {
        if (message instanceof TaskMessageHallsPosition) {
            final int playerId = message.getPlayerId();
            final PlayerOfficeRelative por = taskDataGetter.getPlayerOfficeRelativeDao().read(playerId);
            if (por == null) {
                return;
            }
            final boolean achieve = por.getOfficerId() != 37;
            final TaskRequestProcessViewer viewer = new TaskRequestProcessViewer(achieve, 1, achieve ? 1 : 0);
            final TaskChangeContent taskChangeContent = new TaskChangeContent(this.getTask(), viewer.getProcessStr(), viewer.isCompleted());
            final PlayerDto playerDto = Players.getPlayer(message.getPlayerId());
            Players.push(message.getPlayerId(), PushCommand.PUSH_TASK, TaskBuilderJson.sendJsonTaskChange(taskChangeContent, playerDto, taskDataGetter));
        }
    }
    
    @Override
    public boolean isConcernedMessage(final TaskMessage message) {
        return message instanceof TaskMessageHallsPosition;
    }
    
    @Override
    public boolean check(final PlayerDto playerDto, final IDataGetter dataGetter, final int vId) {
        final int playerId = playerDto.playerId;
        final PlayerOfficeRelative por = dataGetter.getPlayerOfficeRelativeDao().read(playerId);
        return por != null && por.getOfficerId() != 37;
    }
    
    @Override
    public boolean doRequest(final PlayerDto playerDto, final IDataGetter dataGetter, final int vId) {
        return this.check(playerDto, dataGetter, vId);
    }
    
    @Override
    public TaskRequestProcessViewer getProcess(final PlayerDto playerDto, final IDataGetter dataGetter, final int vId) {
        final int playerId = playerDto.playerId;
        final PlayerOfficeRelative por = dataGetter.getPlayerOfficeRelativeDao().read(playerId);
        if (por == null) {
            return null;
        }
        final boolean achieve = por.getOfficerId() != 37;
        final TaskRequestProcessViewer viewer = new TaskRequestProcessViewer(achieve, 1, achieve ? 1 : 0);
        return viewer;
    }
}
