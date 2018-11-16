package com.reign.gcld.task.request;

import com.reign.gcld.player.dto.*;
import com.reign.gcld.general.domain.*;
import com.reign.gcld.task.domain.*;
import java.util.*;
import com.reign.gcld.task.common.*;
import com.reign.gcld.common.*;
import com.reign.gcld.task.message.*;

public class TaskRequestDinner extends TaskRequestCount
{
    public TaskRequestDinner(final String[] s) {
        super(1);
    }
    
    @Override
    public boolean check(final PlayerDto playerDto, final IDataGetter taskDataGetter, final int vId) {
        final PlayerTask playerTask = taskDataGetter.getPlayerTaskDao().read(vId);
        if (playerTask == null) {
            return false;
        }
        if (playerTask.getProcess() >= this.times) {
            return true;
        }
        int count = 0;
        final List<PlayerGeneralMilitary> pgmList = taskDataGetter.getPlayerGeneralMilitaryDao().getMilitaryListOrder(playerDto.playerId);
        for (final PlayerGeneralMilitary pgm : pgmList) {
            if (pgm.getForces() < taskDataGetter.getBattleDataCache().getMaxHp(pgm)) {
                ++count;
            }
        }
        return count == 0;
    }
    
    @Override
    public void handleMessage(final TaskMessage message, final IDataGetter taskDataGetter, final int vId) {
        if (message instanceof TaskMessageDinner) {
            final PlayerTask playerTask = taskDataGetter.getPlayerTaskDao().read(vId);
            if (playerTask == null) {
                return;
            }
            final TaskMessageDinner tmd = (TaskMessageDinner)message;
            final int triggerType = tmd.getTriggerType();
            boolean completed = false;
            if (triggerType == 0) {
                int count = 0;
                final List<PlayerGeneralMilitary> pgmList = taskDataGetter.getPlayerGeneralMilitaryDao().getMilitaryListOrder(message.getPlayerId());
                for (final PlayerGeneralMilitary pgm : pgmList) {
                    if (pgm.getForces() < taskDataGetter.getBattleDataCache().getMaxHp(pgm)) {
                        ++count;
                    }
                }
                if (count == 0) {
                    completed = true;
                }
            }
            else {
                taskDataGetter.getPlayerTaskDao().addProcess(vId, 1);
                completed = true;
            }
            final TaskRequestProcessViewer viewer = new TaskRequestProcessViewer(completed, this.getTimes(), playerTask.getProcess() + 1);
            final TaskChangeContent taskChangeContent = new TaskChangeContent(this.getTask(), viewer.getProcessStr(), viewer.isCompleted());
            final PlayerDto playerDto = Players.getPlayer(message.getPlayerId());
            Players.push(message.getPlayerId(), PushCommand.PUSH_TASK, TaskBuilderJson.sendJsonTaskChange(taskChangeContent, playerDto, taskDataGetter));
        }
    }
    
    @Override
    public boolean isConcernedMessage(final TaskMessage message) {
        return message instanceof TaskMessageDinner;
    }
}
