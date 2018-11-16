package com.reign.gcld.task.request;

import com.reign.gcld.player.dto.*;
import com.reign.gcld.general.domain.*;
import com.reign.gcld.task.common.*;
import com.reign.gcld.common.*;
import com.reign.gcld.task.message.*;

public class TaskRequestRecuitGeneral extends TaskRequestBase
{
    private int generalType;
    private int generalId;
    
    public TaskRequestRecuitGeneral(final String[] s) {
        this.generalType = Integer.parseInt(s[1]);
        this.generalId = Integer.parseInt(s[2]);
    }
    
    @Override
    public boolean check(final PlayerDto playerDto, final IDataGetter taskDataGetter, final int vId) {
        if (this.generalType == 2) {
            final PlayerGeneralMilitary pgm = taskDataGetter.getPlayerGeneralMilitaryDao().getMilitary(playerDto.playerId, this.generalId);
            if (pgm != null) {
                return true;
            }
        }
        else {
            final PlayerGeneralCivil pgc = taskDataGetter.getPlayerGeneralCivilDao().getCivil(playerDto.playerId, this.generalId);
            if (pgc != null) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean doRequest(final PlayerDto playerDto, final IDataGetter taskDataGetter, final int vId) {
        return this.check(playerDto, taskDataGetter, vId);
    }
    
    @Override
    public void handleMessage(final TaskMessage message, final IDataGetter taskDataGetter, final int vId) {
        if (message instanceof TaskMessageRecuitGeneral) {
            final TaskMessageRecuitGeneral tmrg = (TaskMessageRecuitGeneral)message;
            if (tmrg.getGeneralId() == this.generalId && tmrg.getGeneralType() == this.generalType) {
                final TaskRequestProcessViewer viewer = new TaskRequestProcessViewer(true, 1, 1);
                final TaskChangeContent taskChangeContent = new TaskChangeContent(this.getTask(), viewer.getProcessStr(), viewer.isCompleted());
                Players.push(message.getPlayerId(), PushCommand.PUSH_TASK, TaskBuilderJson.sendJsonTaskChange(taskChangeContent, Players.getPlayer(message.getPlayerId()), taskDataGetter));
            }
        }
    }
    
    @Override
    public boolean isConcernedMessage(final TaskMessage message) {
        return message instanceof TaskMessageRecuitGeneral;
    }
    
    @Override
    public TaskRequestProcessViewer getProcess(final PlayerDto playerDto, final IDataGetter taskDataGetter, final int vId) {
        final boolean completed = this.check(playerDto, taskDataGetter, vId);
        final TaskRequestProcessViewer rtn = new TaskRequestProcessViewer(completed, 1, completed ? 1 : 0);
        return rtn;
    }
}
