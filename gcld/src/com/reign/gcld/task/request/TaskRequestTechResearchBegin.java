package com.reign.gcld.task.request;

import com.reign.gcld.player.dto.*;
import com.reign.gcld.tech.domain.*;
import com.reign.gcld.task.common.*;
import com.reign.gcld.common.*;
import com.reign.gcld.task.message.*;
import com.reign.gcld.task.domain.*;

public class TaskRequestTechResearchBegin extends TaskRequestCount
{
    private int techId;
    
    public TaskRequestTechResearchBegin(final String[] s) {
        super(1);
        this.techId = Integer.valueOf(s[1]);
    }
    
    @Override
    public boolean check(final PlayerDto playerDto, final IDataGetter taskDataGetter, final int vId) {
        final PlayerTech pt = taskDataGetter.getPlayerTechDao().getPlayerTech(playerDto.playerId, this.techId);
        return pt != null && pt.getStatus() >= 4;
    }
    
    @Override
    public boolean doRequest(final PlayerDto playerDto, final IDataGetter taskDataGetter, final int vId) {
        return this.check(playerDto, taskDataGetter, vId);
    }
    
    @Override
    public void handleMessage(final TaskMessage message, final IDataGetter taskDataGetter, final int vId) {
        if (message instanceof TaskMessageTechResearchBegin) {
            final PlayerTask playerTask = taskDataGetter.getPlayerTaskDao().read(vId);
            if (playerTask == null) {
                return;
            }
            final TaskMessageTechResearchBegin trbMessage = (TaskMessageTechResearchBegin)message;
            if (trbMessage.getTechId() != this.techId) {
                return;
            }
            taskDataGetter.getPlayerTaskDao().addProcess(vId, 1);
            final TaskRequestProcessViewer viewer = new TaskRequestProcessViewer(playerTask.getProcess() + 1 >= this.getTimes(), this.getTimes(), playerTask.getProcess() + 1);
            final TaskChangeContent taskChangeContent = new TaskChangeContent(this.getTask(), viewer.getProcessStr(), viewer.isCompleted());
            Players.push(message.getPlayerId(), PushCommand.PUSH_TASK, TaskBuilderJson.sendJsonTaskChange(taskChangeContent, Players.getPlayer(message.getPlayerId()), taskDataGetter));
        }
    }
    
    @Override
    public boolean isConcernedMessage(final TaskMessage message) {
        return message instanceof TaskMessageTechResearchBegin;
    }
    
    @Override
    public TaskRequestProcessViewer getProcess(final PlayerDto playerDto, final IDataGetter taskDataGetter, final int vId) {
        final boolean completed = this.check(playerDto, taskDataGetter, vId);
        final TaskRequestProcessViewer rtn = new TaskRequestProcessViewer(completed, 1, completed ? 1 : 0);
        return rtn;
    }
}
