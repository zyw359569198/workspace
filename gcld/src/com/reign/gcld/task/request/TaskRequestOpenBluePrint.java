package com.reign.gcld.task.request;

import com.reign.gcld.player.dto.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.gcld.building.domain.*;
import com.reign.gcld.task.common.*;
import com.reign.gcld.common.*;
import com.reign.gcld.task.message.*;
import com.reign.gcld.task.domain.*;

public class TaskRequestOpenBluePrint extends TaskRequestCount
{
    private int id;
    
    public TaskRequestOpenBluePrint(final String[] s) {
        this.id = Integer.valueOf(s[1]);
    }
    
    @Override
    public boolean check(final PlayerDto playerDto, final IDataGetter taskDataGetter, final int vId) {
        final int playerId = playerDto.playerId;
        final PlayerBuilding pb = taskDataGetter.getPlayerBuildingDao().getPlayerBuilding(playerId, this.id);
        if (pb != null) {
            return true;
        }
        BluePrint bp = taskDataGetter.getBluePrintDao().getByPlayerIdAndIndex(playerId, this.id);
        if (bp == null) {
            bp = new BluePrint();
            bp.setPlayerId(playerId);
            bp.setIndex(this.id);
            bp.setState(1);
            bp.setJobId(0);
            taskDataGetter.getBluePrintDao().create(bp);
            return false;
        }
        final BuildingDrawing bd = (BuildingDrawing)taskDataGetter.getBuildingDrawingCache().get((Object)this.id);
        return bd == null || bd.getGet() == 0 || 1 < bp.getState();
    }
    
    @Override
    public boolean doRequest(final PlayerDto playerDto, final IDataGetter taskDataGetter, final int vId) {
        return this.check(playerDto, taskDataGetter, vId);
    }
    
    @Override
    public void handleMessage(final TaskMessage message, final IDataGetter taskDataGetter, final int vId) {
        if (message instanceof TaskMessageOpenBluePrint) {
            final PlayerTask playerTask = taskDataGetter.getPlayerTaskDao().read(vId);
            if (playerTask == null) {
                return;
            }
            final TaskMessageOpenBluePrint obpMessage = (TaskMessageOpenBluePrint)message;
            if (obpMessage.getid() != this.id) {
                return;
            }
            taskDataGetter.getPlayerTaskDao().addProcess(vId, 1);
            final PlayerDto playerDto = Players.getPlayer(message.getPlayerId());
            final boolean completed = this.check(playerDto, taskDataGetter, vId);
            final TaskRequestProcessViewer viewer = new TaskRequestProcessViewer(completed, 1, completed ? 1 : 0);
            final TaskChangeContent taskChangeContent = new TaskChangeContent(this.getTask(), viewer.getProcessStr(), viewer.isCompleted());
            Players.push(message.getPlayerId(), PushCommand.PUSH_TASK, TaskBuilderJson.sendJsonTaskChange(taskChangeContent, playerDto, taskDataGetter));
        }
    }
    
    @Override
    public boolean isConcernedMessage(final TaskMessage message) {
        return message instanceof TaskMessageOpenBluePrint;
    }
    
    @Override
    public TaskRequestProcessViewer getProcess(final PlayerDto playerDto, final IDataGetter taskDataGetter, final int vId) {
        final boolean completed = this.check(playerDto, taskDataGetter, vId);
        final TaskRequestProcessViewer rtn = new TaskRequestProcessViewer(completed, 1, completed ? 1 : 0);
        return rtn;
    }
}
