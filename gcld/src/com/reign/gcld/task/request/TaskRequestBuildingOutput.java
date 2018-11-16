package com.reign.gcld.task.request;

import com.reign.gcld.player.dto.*;
import com.reign.gcld.task.common.*;
import com.reign.gcld.common.*;
import com.reign.gcld.task.message.*;

public class TaskRequestBuildingOutput extends TaskRequestBase
{
    private int outputType;
    private int num;
    
    public TaskRequestBuildingOutput(final String[] s) {
        this.outputType = Integer.parseInt(s[1]);
        this.num = Integer.parseInt(s[2]);
    }
    
    @Override
    public boolean check(final PlayerDto playerDto, final IDataGetter taskDataGetter, final int vId) {
        final int curOutput = taskDataGetter.getBuildingOutputCache().getBuildingsOutput(playerDto.playerId, this.outputType);
        return curOutput >= this.num;
    }
    
    @Override
    public boolean doRequest(final PlayerDto playerDto, final IDataGetter taskDataGetter, final int vId) {
        return this.check(playerDto, taskDataGetter, vId);
    }
    
    @Override
    public void handleMessage(final TaskMessage message, final IDataGetter dataGetter, final int vId) {
        if (message instanceof TaskMessageResource) {
            final TaskMessageResource taskMessageResource = (TaskMessageResource)message;
            if (this.outputType == taskMessageResource.getOutputType()) {
                TaskRequestProcessViewer viewer = null;
                final PlayerDto playerDto = Players.getPlayer(message.getPlayerId());
                if (this.num <= taskMessageResource.getNum()) {
                    viewer = new TaskRequestProcessViewer(true, this.num, taskMessageResource.getNum());
                    final TaskChangeContent taskChangeContent = new TaskChangeContent(this.getTask(), viewer.getProcessStr(), viewer.isCompleted());
                    if (dataGetter.getPlayerTaskDao().read(vId).getProcess() < 1) {
                        dataGetter.getPlayerTaskDao().addProcess(vId, 1);
                        Players.push(message.getPlayerId(), PushCommand.PUSH_TASK, TaskBuilderJson.sendJsonTaskChange(taskChangeContent, playerDto, dataGetter));
                    }
                }
                else {
                    viewer = new TaskRequestProcessViewer(false, this.num, taskMessageResource.getNum());
                    final TaskChangeContent taskChangeContent = new TaskChangeContent(this.getTask(), viewer.getProcessStr(), viewer.isCompleted());
                    Players.push(message.getPlayerId(), PushCommand.PUSH_TASK, TaskBuilderJson.sendJsonTaskChange(taskChangeContent, playerDto, dataGetter));
                }
            }
        }
    }
    
    @Override
    public boolean isConcernedMessage(final TaskMessage message) {
        return message instanceof TaskMessageResource;
    }
    
    @Override
    public TaskRequestProcessViewer getProcess(final PlayerDto playerDto, final IDataGetter taskDataGetter, final int vId) {
        boolean completed = false;
        final int curOutput = taskDataGetter.getBuildingOutputCache().getBuildingsOutput(playerDto.playerId, this.outputType);
        if (curOutput >= this.num) {
            completed = true;
        }
        final TaskRequestProcessViewer rtn = new TaskRequestProcessViewer(completed, this.num, completed ? this.num : curOutput);
        return rtn;
    }
}
