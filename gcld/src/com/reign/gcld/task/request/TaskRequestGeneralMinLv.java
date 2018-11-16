package com.reign.gcld.task.request;

import com.reign.gcld.player.dto.*;
import com.reign.gcld.task.common.*;
import com.reign.gcld.common.*;
import com.reign.gcld.task.message.*;
import com.reign.gcld.general.domain.*;
import java.util.*;

public class TaskRequestGeneralMinLv extends TaskRequestBase
{
    private int lv;
    
    public TaskRequestGeneralMinLv(final String[] s) {
        this.lv = Integer.parseInt(s[1]);
    }
    
    @Override
    public boolean check(final PlayerDto playerDto, final IDataGetter taskDataGetter, final int vId) {
        final int curNum = this.getMinForces(taskDataGetter, playerDto.playerId);
        return curNum >= this.lv;
    }
    
    @Override
    public boolean doRequest(final PlayerDto playerDto, final IDataGetter taskDataGetter, final int vId) {
        return this.check(playerDto, taskDataGetter, vId);
    }
    
    @Override
    public void handleMessage(final TaskMessage message, final IDataGetter taskDataGetter, final int vId) {
        if (message instanceof TaskMessageGeneralLv) {
            final PlayerDto playerDto = Players.getPlayer(message.getPlayerId());
            final TaskMessageGeneralLv taskMessageGeneralLv = (TaskMessageGeneralLv)message;
            final int curNum = this.getMinForces(taskDataGetter, taskMessageGeneralLv.getPlayerId());
            final TaskRequestProcessViewer viewer = new TaskRequestProcessViewer(curNum >= this.lv, this.lv, curNum);
            final TaskChangeContent taskChangeContent = new TaskChangeContent(this.getTask(), viewer.getProcessStr(), viewer.isCompleted());
            Players.push(message.getPlayerId(), PushCommand.PUSH_TASK, TaskBuilderJson.sendJsonTaskChange(taskChangeContent, playerDto, taskDataGetter));
        }
    }
    
    @Override
    public boolean isConcernedMessage(final TaskMessage message) {
        return message instanceof TaskMessageGeneralLv;
    }
    
    @Override
    public TaskRequestProcessViewer getProcess(final PlayerDto playerDto, final IDataGetter taskDataGetter, final int vId) {
        boolean completed = false;
        final int curNum = this.getMinForces(taskDataGetter, playerDto.playerId);
        if (curNum >= this.lv) {
            completed = true;
        }
        final TaskRequestProcessViewer rtn = new TaskRequestProcessViewer(completed, this.lv, completed ? this.lv : curNum);
        return rtn;
    }
    
    public int getMinForces(final IDataGetter taskDataGetter, final int playerId) {
        final List<PlayerGeneralMilitary> list = taskDataGetter.getPlayerGeneralMilitaryDao().getMilitaryList(playerId);
        int curNum = 99999;
        for (final PlayerGeneralMilitary pgm : list) {
            if (pgm.getLv() < curNum) {
                curNum = pgm.getLv();
            }
        }
        if (curNum == 99999) {
            return 0;
        }
        return curNum;
    }
}
