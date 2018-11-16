package com.reign.gcld.task.request;

import com.reign.gcld.player.dto.*;
import com.reign.gcld.building.domain.*;
import com.reign.gcld.task.common.*;
import com.reign.gcld.common.*;
import com.reign.gcld.task.message.*;

public class TaskRequestBuilding extends TaskRequestBase
{
    private int buildingId;
    private int lv;
    
    public TaskRequestBuilding(final String[] s) {
        this.buildingId = Integer.parseInt(s[1]);
        this.lv = Integer.parseInt(s[2]);
    }
    
    @Override
    public boolean check(final PlayerDto playerDto, final IDataGetter taskDataGetter, final int vId) {
        final PlayerBuilding pb = taskDataGetter.getBuildingService().getPlayerBuilding(playerDto.playerId, this.buildingId);
        return pb != null && pb.getLv() >= this.lv;
    }
    
    @Override
    public boolean doRequest(final PlayerDto playerDto, final IDataGetter taskDataGetter, final int vId) {
        return this.check(playerDto, taskDataGetter, vId);
    }
    
    @Override
    public void handleMessage(final TaskMessage message, final IDataGetter dataGetter, final int vId) {
        if (message instanceof TaskMessageBuilding) {
            final PlayerDto playerDto = Players.getPlayer(message.getPlayerId());
            final TaskMessageBuilding taskMessageBuilding = (TaskMessageBuilding)message;
            if (this.buildingId == taskMessageBuilding.getBuildingId() && this.lv <= taskMessageBuilding.getLv()) {
                final TaskRequestProcessViewer viewer = new TaskRequestProcessViewer(true, this.lv, taskMessageBuilding.getLv());
                final TaskChangeContent taskChangeContent = new TaskChangeContent(this.getTask(), viewer.getProcessStr(), viewer.isCompleted());
                Players.push(message.getPlayerId(), PushCommand.PUSH_TASK, TaskBuilderJson.sendJsonTaskChange(taskChangeContent, playerDto, dataGetter));
            }
            else if (this.buildingId == taskMessageBuilding.getBuildingId()) {
                final TaskRequestProcessViewer viewer = new TaskRequestProcessViewer(false, this.lv, taskMessageBuilding.getLv());
                final TaskChangeContent taskChangeContent = new TaskChangeContent(this.getTask(), viewer.getProcessStr(), viewer.isCompleted());
                Players.push(message.getPlayerId(), PushCommand.PUSH_TASK, TaskBuilderJson.sendJsonTaskChange(taskChangeContent, playerDto, dataGetter));
            }
        }
    }
    
    @Override
    public boolean isConcernedMessage(final TaskMessage message) {
        return message instanceof TaskMessageBuilding;
    }
    
    @Override
    public TaskRequestProcessViewer getProcess(final PlayerDto playerDto, final IDataGetter taskDataGetter, final int vId) {
        boolean completed = false;
        final PlayerBuilding pb = taskDataGetter.getBuildingService().getPlayerBuilding(playerDto.playerId, this.buildingId);
        int curLv = 0;
        if (pb != null) {
            curLv = pb.getLv();
            if (curLv >= this.lv) {
                completed = true;
            }
        }
        final TaskRequestProcessViewer rtn = new TaskRequestProcessViewer(completed, this.lv, completed ? this.lv : curLv);
        return rtn;
    }
}
