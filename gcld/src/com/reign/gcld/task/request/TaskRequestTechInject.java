package com.reign.gcld.task.request;

import com.reign.gcld.player.dto.*;
import com.reign.gcld.tech.domain.*;
import com.reign.gcld.task.common.*;
import com.reign.gcld.common.*;
import com.reign.gcld.task.message.*;
import com.reign.gcld.task.domain.*;

public class TaskRequestTechInject extends TaskRequestCount
{
    private int techId;
    
    public TaskRequestTechInject(final String[] s) {
        this.techId = Integer.valueOf(s[1]);
        if (s.length > 2) {
            this.setTimes(Integer.parseInt(s[2]));
        }
        else {
            this.setTimes(1);
        }
    }
    
    @Override
    public boolean check(final PlayerDto playerDto, final IDataGetter taskDataGetter, final int vId) {
        final PlayerTech pt = taskDataGetter.getPlayerTechDao().getPlayerTech(playerDto.playerId, this.techId);
        return pt != null && pt.getNum() >= this.getTimes();
    }
    
    @Override
    public boolean doRequest(final PlayerDto playerDto, final IDataGetter taskDataGetter, final int vId) {
        return this.check(playerDto, taskDataGetter, vId);
    }
    
    @Override
    public void handleMessage(final TaskMessage message, final IDataGetter taskDataGetter, final int vId) {
        if (message instanceof TaskMessageTechInject) {
            final PlayerTask playerTask = taskDataGetter.getPlayerTaskDao().read(vId);
            if (playerTask == null) {
                return;
            }
            final TaskMessageTechInject tiMessage = (TaskMessageTechInject)message;
            if (this.techId != tiMessage.getTechId()) {
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
        return message instanceof TaskMessageTechInject;
    }
    
    @Override
    public TaskRequestProcessViewer getProcess(final PlayerDto playerDto, final IDataGetter taskDataGetter, final int vId) {
        final boolean completed = this.check(playerDto, taskDataGetter, vId);
        int process = 0;
        final PlayerTech pt = taskDataGetter.getPlayerTechDao().getPlayerTech(playerDto.playerId, this.techId);
        if (pt != null) {
            process = pt.getNum();
        }
        final TaskRequestProcessViewer rtn = new TaskRequestProcessViewer(completed, this.getTimes(), process);
        return rtn;
    }
}
