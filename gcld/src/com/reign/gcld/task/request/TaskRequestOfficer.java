package com.reign.gcld.task.request;

import com.reign.gcld.player.dto.*;
import com.reign.gcld.task.common.*;
import com.reign.gcld.common.*;
import com.reign.gcld.task.message.*;

public class TaskRequestOfficer extends TaskRequestBase
{
    private int count;
    
    public TaskRequestOfficer(final String[] s) {
        this.count = Integer.parseInt(s[1]);
    }
    
    @Override
    public boolean check(final PlayerDto playerDto, final IDataGetter taskDataGetter, final int vId) {
        final int num = taskDataGetter.getPlayerGeneralCivilDao().getCivilNum(playerDto.playerId);
        return num >= this.count;
    }
    
    @Override
    public boolean doRequest(final PlayerDto playerDto, final IDataGetter taskDataGetter, final int vId) {
        return this.check(playerDto, taskDataGetter, vId);
    }
    
    @Override
    public void handleMessage(final TaskMessage message, final IDataGetter taskDataGetter, final int vId) {
        if (message instanceof TaskMessageOfficer) {
            final TaskMessageOfficer taskMessageGeneral = (TaskMessageOfficer)message;
            final int num = taskDataGetter.getPlayerGeneralCivilDao().getCivilNum(taskMessageGeneral.getPlayerId());
            final TaskRequestProcessViewer viewer = new TaskRequestProcessViewer(taskMessageGeneral.getNum() >= this.count, this.count, num);
            final TaskChangeContent taskChangeContent = new TaskChangeContent(this.getTask(), viewer.getProcessStr(), viewer.isCompleted());
            Players.push(message.getPlayerId(), PushCommand.PUSH_TASK, TaskBuilderJson.sendJsonTaskChange(taskChangeContent, Players.getPlayer(message.getPlayerId()), taskDataGetter));
        }
    }
    
    @Override
    public boolean isConcernedMessage(final TaskMessage message) {
        return message instanceof TaskMessageOfficer;
    }
    
    @Override
    public TaskRequestProcessViewer getProcess(final PlayerDto playerDto, final IDataGetter taskDataGetter, final int vId) {
        boolean completed = false;
        final int num = taskDataGetter.getPlayerGeneralCivilDao().getCivilNum(playerDto.playerId);
        if (num >= this.count) {
            completed = true;
        }
        final TaskRequestProcessViewer rtn = new TaskRequestProcessViewer(completed, this.count, completed ? this.count : num);
        return rtn;
    }
}
