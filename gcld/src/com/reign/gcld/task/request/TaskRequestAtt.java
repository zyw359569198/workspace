package com.reign.gcld.task.request;

import com.reign.gcld.player.dto.*;
import com.reign.gcld.task.common.*;
import com.reign.gcld.common.*;
import com.reign.gcld.task.message.*;
import com.reign.gcld.general.domain.*;
import java.util.*;

public class TaskRequestAtt extends TaskRequestBase
{
    private int num;
    
    public TaskRequestAtt(final String[] s) {
        this.num = Integer.parseInt(s[1]);
    }
    
    @Override
    public boolean check(final PlayerDto playerDto, final IDataGetter taskDataGetter, final int vId) {
        final int curNum = this.getMaxAtt(taskDataGetter, playerDto.playerId);
        return curNum >= this.num;
    }
    
    @Override
    public boolean doRequest(final PlayerDto playerDto, final IDataGetter taskDataGetter, final int vId) {
        return this.check(playerDto, taskDataGetter, vId);
    }
    
    @Override
    public void handleMessage(final TaskMessage message, final IDataGetter taskDataGetter, final int vId) {
        if (message instanceof TaskMessageAtt) {
            final PlayerDto playerDto = Players.getPlayer(message.getPlayerId());
            final TaskMessageAtt taskMessageAtt = (TaskMessageAtt)message;
            if (this.num <= taskMessageAtt.getNum()) {
                final TaskRequestProcessViewer viewer = new TaskRequestProcessViewer(true, this.num, this.num);
                final TaskChangeContent taskChangeContent = new TaskChangeContent(this.getTask(), viewer.getProcessStr(), viewer.isCompleted());
                Players.push(message.getPlayerId(), PushCommand.PUSH_TASK, TaskBuilderJson.sendJsonTaskChange(taskChangeContent, playerDto, taskDataGetter));
            }
            else {
                final int curNum = this.getMaxAtt(taskDataGetter, taskMessageAtt.getPlayerId());
                final TaskRequestProcessViewer viewer2 = new TaskRequestProcessViewer(false, this.num, curNum);
                final TaskChangeContent taskChangeContent2 = new TaskChangeContent(this.getTask(), viewer2.getProcessStr(), viewer2.isCompleted());
                Players.push(message.getPlayerId(), PushCommand.PUSH_TASK, TaskBuilderJson.sendJsonTaskChange(taskChangeContent2, playerDto, taskDataGetter));
            }
        }
    }
    
    @Override
    public boolean isConcernedMessage(final TaskMessage message) {
        return message instanceof TaskMessageAtt;
    }
    
    @Override
    public TaskRequestProcessViewer getProcess(final PlayerDto playerDto, final IDataGetter taskDataGetter, final int vId) {
        boolean completed = false;
        final int curNum = this.getMaxAtt(taskDataGetter, playerDto.playerId);
        if (curNum >= this.num) {
            completed = true;
        }
        final TaskRequestProcessViewer rtn = new TaskRequestProcessViewer(completed, this.num, completed ? this.num : curNum);
        return rtn;
    }
    
    private int getMaxAtt(final IDataGetter taskDataGetter, final int playerId) {
        final List<PlayerGeneralMilitary> list = taskDataGetter.getPlayerGeneralMilitaryDao().getMilitaryList(playerId);
        int curNum = 0;
        int att = 0;
        for (final PlayerGeneralMilitary pgm : list) {
            att = taskDataGetter.getBattleDataCache().getAtt(pgm);
            if (att > curNum) {
                curNum = att;
            }
        }
        return curNum;
    }
}
