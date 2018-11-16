package com.reign.gcld.task.request;

import com.reign.gcld.player.dto.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.gcld.player.domain.*;
import com.reign.gcld.task.common.*;
import com.reign.gcld.common.*;
import com.reign.gcld.task.message.*;

public class TaskRequestOfficial extends TaskRequestBase
{
    private int id;
    
    public TaskRequestOfficial(final String[] s) {
        this.id = Integer.parseInt(s[1]);
    }
    
    @Override
    public boolean check(final PlayerDto playerDto, final IDataGetter taskDataGetter, final int vId) {
        final PlayerOfficeRelative por = taskDataGetter.getPlayerOfficeRelativeDao().read(playerDto.playerId);
        final int currId = (por == null) ? 37 : por.getOfficerId();
        final int oId = ((Halls)taskDataGetter.getHallsCache().get((Object)currId)).getOfficialId();
        return oId <= this.id;
    }
    
    @Override
    public boolean doRequest(final PlayerDto playerDto, final IDataGetter taskDataGetter, final int vId) {
        return this.check(playerDto, taskDataGetter, vId);
    }
    
    @Override
    public void handleMessage(final TaskMessage message, final IDataGetter dataGetter, final int vId) {
        if (message instanceof TaskMessageOfficial) {
            final PlayerOfficeRelative por = dataGetter.getPlayerOfficeRelativeDao().read(message.getPlayerId());
            final int currId = (por == null) ? 37 : por.getOfficerId();
            final int oId = ((Halls)dataGetter.getHallsCache().get((Object)currId)).getOfficialId();
            if (oId <= this.id) {
                final TaskRequestProcessViewer viewer = new TaskRequestProcessViewer(true, 1, 1);
                final TaskChangeContent taskChangeContent = new TaskChangeContent(this.getTask(), viewer.getProcessStr(), viewer.isCompleted());
                Players.push(message.getPlayerId(), PushCommand.PUSH_TASK, TaskBuilderJson.sendJsonTaskChange(taskChangeContent, Players.getPlayer(message.getPlayerId()), dataGetter));
            }
            else {
                final TaskRequestProcessViewer viewer = new TaskRequestProcessViewer(false, 1, 0);
                final TaskChangeContent taskChangeContent = new TaskChangeContent(this.getTask(), viewer.getProcessStr(), viewer.isCompleted());
                Players.push(message.getPlayerId(), PushCommand.PUSH_TASK, TaskBuilderJson.sendJsonTaskChange(taskChangeContent, Players.getPlayer(message.getPlayerId()), dataGetter));
            }
        }
    }
    
    @Override
    public boolean isConcernedMessage(final TaskMessage message) {
        return message instanceof TaskMessageOfficial;
    }
    
    @Override
    public TaskRequestProcessViewer getProcess(final PlayerDto playerDto, final IDataGetter taskDataGetter, final int vId) {
        boolean completed = false;
        final PlayerOfficeRelative por = taskDataGetter.getPlayerOfficeRelativeDao().read(playerDto.playerId);
        final int currId = (por == null) ? 37 : por.getOfficerId();
        final int oId = ((Halls)taskDataGetter.getHallsCache().get((Object)currId)).getOfficialId();
        if (oId <= this.id) {
            completed = true;
        }
        final TaskRequestProcessViewer rtn = new TaskRequestProcessViewer(completed, 1, completed ? 1 : 0);
        return rtn;
    }
}
