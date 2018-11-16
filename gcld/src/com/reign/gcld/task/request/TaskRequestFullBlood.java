package com.reign.gcld.task.request;

import com.reign.gcld.player.dto.*;
import com.reign.gcld.general.domain.*;
import java.util.*;
import com.reign.gcld.task.common.*;
import com.reign.gcld.common.*;
import com.reign.gcld.task.message.*;

public class TaskRequestFullBlood extends TaskRequestBase
{
    public TaskRequestFullBlood(final String[] s) {
    }
    
    @Override
    public boolean check(final PlayerDto playerDto, final IDataGetter taskDataGetter, final int vId) {
        final List<PlayerGeneralMilitary> list = taskDataGetter.getPlayerGeneralMilitaryDao().getMilitaryList(playerDto.playerId);
        for (final PlayerGeneralMilitary pgm : list) {
            if (taskDataGetter.getBattleDataCache().getMaxHp(pgm) <= pgm.getForces()) {
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
    public void handleMessage(final TaskMessage message, final IDataGetter dataGetter, final int vId) {
        if (message instanceof TaskMessageFullBlood) {
            final PlayerDto playerDto = Players.getPlayer(message.getPlayerId());
            final TaskRequestProcessViewer viewer = new TaskRequestProcessViewer(true, 1, 1, "");
            final TaskChangeContent taskChangeContent = new TaskChangeContent(this.getTask(), viewer.getProcessStr(), viewer.isCompleted());
            Players.push(message.getPlayerId(), PushCommand.PUSH_TASK, TaskBuilderJson.sendJsonTaskChange(taskChangeContent, playerDto, dataGetter));
        }
    }
    
    @Override
    public boolean isConcernedMessage(final TaskMessage message) {
        return message instanceof TaskMessageFullBlood;
    }
    
    @Override
    public TaskRequestProcessViewer getProcess(final PlayerDto playerDto, final IDataGetter dataGetter, final int vId) {
        final boolean completed = this.check(playerDto, dataGetter, vId);
        final TaskRequestProcessViewer rtn = new TaskRequestProcessViewer(completed, 1, completed ? 1 : 0, "");
        return rtn;
    }
}
