package com.reign.gcld.task.request;

import com.reign.gcld.player.dto.*;
import com.reign.gcld.task.common.*;
import com.reign.gcld.common.*;
import com.reign.gcld.task.message.*;
import com.reign.gcld.general.domain.*;
import java.util.*;

public class TaskRequestBlood extends TaskRequestBase
{
    private int number;
    
    public TaskRequestBlood(final String[] s) {
        this.number = Integer.parseInt(s[1]);
    }
    
    @Override
    public boolean check(final PlayerDto playerDto, final IDataGetter taskDataGetter, final int vId) {
        final int curNum = this.getMaxForces(taskDataGetter, playerDto.playerId);
        return curNum >= this.number;
    }
    
    @Override
    public boolean doRequest(final PlayerDto playerDto, final IDataGetter taskDataGetter, final int vId) {
        return this.check(playerDto, taskDataGetter, vId);
    }
    
    @Override
    public void handleMessage(final TaskMessage message, final IDataGetter taskDataGetter, final int vId) {
        if (message instanceof TaskMessageBlood) {
            final PlayerDto playerDto = Players.getPlayer(message.getPlayerId());
            final TaskMessageBlood taskMessageBlood = (TaskMessageBlood)message;
            if (this.number <= taskMessageBlood.getNum()) {
                final TaskRequestProcessViewer viewer = new TaskRequestProcessViewer(true, this.number, this.number);
                final TaskChangeContent taskChangeContent = new TaskChangeContent(this.getTask(), viewer.getProcessStr(), viewer.isCompleted());
                Players.push(message.getPlayerId(), PushCommand.PUSH_TASK, TaskBuilderJson.sendJsonTaskChange(taskChangeContent, playerDto, taskDataGetter));
            }
            else {
                final int curNum = this.getMaxForces(taskDataGetter, taskMessageBlood.getPlayerId());
                final TaskRequestProcessViewer viewer2 = new TaskRequestProcessViewer(false, this.number, curNum);
                final TaskChangeContent taskChangeContent2 = new TaskChangeContent(this.getTask(), viewer2.getProcessStr(), viewer2.isCompleted());
                Players.push(message.getPlayerId(), PushCommand.PUSH_TASK, TaskBuilderJson.sendJsonTaskChange(taskChangeContent2, playerDto, taskDataGetter));
            }
        }
    }
    
    @Override
    public boolean isConcernedMessage(final TaskMessage message) {
        return message instanceof TaskMessageBlood;
    }
    
    @Override
    public TaskRequestProcessViewer getProcess(final PlayerDto playerDto, final IDataGetter taskDataGetter, final int vId) {
        boolean completed = false;
        final int curNum = this.getMaxForces(taskDataGetter, playerDto.playerId);
        if (curNum >= this.number) {
            completed = true;
        }
        final TaskRequestProcessViewer rtn = new TaskRequestProcessViewer(completed, this.number, completed ? this.number : curNum);
        return rtn;
    }
    
    public int getMaxForces(final IDataGetter taskDataGetter, final int playerId) {
        final List<PlayerGeneralMilitary> list = taskDataGetter.getPlayerGeneralMilitaryDao().getMilitaryList(playerId);
        int curNum = 0;
        int hp = 0;
        for (final PlayerGeneralMilitary pgm : list) {
            hp = taskDataGetter.getBattleDataCache().getMaxHp(pgm);
            if (hp > curNum) {
                curNum = hp;
            }
        }
        return curNum;
    }
}
