package com.reign.gcld.task.request;

import com.reign.gcld.player.dto.*;
import com.reign.gcld.task.common.*;
import com.reign.gcld.common.*;
import com.reign.gcld.task.message.*;
import com.reign.gcld.general.domain.*;
import java.util.*;

public class TaskRequestDef extends TaskRequestBase
{
    private int num;
    
    public TaskRequestDef(final String[] s) {
        this.num = Integer.parseInt(s[1]);
    }
    
    @Override
    public boolean check(final PlayerDto playerDto, final IDataGetter taskDataGetter, final int vId) {
        final int curNum = this.getDef(taskDataGetter, playerDto.playerId);
        return curNum >= this.num;
    }
    
    @Override
    public boolean doRequest(final PlayerDto playerDto, final IDataGetter taskDataGetter, final int vId) {
        return this.check(playerDto, taskDataGetter, vId);
    }
    
    @Override
    public void handleMessage(final TaskMessage message, final IDataGetter taskDataGetter, final int vId) {
        if (message instanceof TaskMessageDef) {
            final PlayerDto playerDto = Players.getPlayer(message.getPlayerId());
            final TaskMessageDef taskMessageDef = (TaskMessageDef)message;
            if (this.num <= taskMessageDef.getNum()) {
                final TaskRequestProcessViewer viewer = new TaskRequestProcessViewer(true, this.num, this.num);
                final TaskChangeContent taskChangeContent = new TaskChangeContent(this.getTask(), viewer.getProcessStr(), viewer.isCompleted());
                Players.push(message.getPlayerId(), PushCommand.PUSH_TASK, TaskBuilderJson.sendJsonTaskChange(taskChangeContent, playerDto, taskDataGetter));
            }
            else {
                final int curNum = this.getDef(taskDataGetter, taskMessageDef.getPlayerId());
                final TaskRequestProcessViewer viewer2 = new TaskRequestProcessViewer(false, this.num, curNum);
                final TaskChangeContent taskChangeContent2 = new TaskChangeContent(this.getTask(), viewer2.getProcessStr(), viewer2.isCompleted());
                Players.push(message.getPlayerId(), PushCommand.PUSH_TASK, TaskBuilderJson.sendJsonTaskChange(taskChangeContent2, playerDto, taskDataGetter));
            }
        }
    }
    
    @Override
    public boolean isConcernedMessage(final TaskMessage message) {
        return message instanceof TaskMessageDef;
    }
    
    @Override
    public TaskRequestProcessViewer getProcess(final PlayerDto playerDto, final IDataGetter taskDataGetter, final int vId) {
        boolean completed = false;
        final int curNum = this.getDef(taskDataGetter, playerDto.playerId);
        if (curNum >= this.num) {
            completed = true;
        }
        final TaskRequestProcessViewer rtn = new TaskRequestProcessViewer(completed, this.num, completed ? this.num : curNum);
        return rtn;
    }
    
    public int getDef(final IDataGetter taskDataGetter, final int playerId) {
        final List<PlayerGeneralMilitary> list = taskDataGetter.getPlayerGeneralMilitaryDao().getMilitaryList(playerId);
        int curNum = 0;
        int def = 0;
        for (final PlayerGeneralMilitary pgm : list) {
            def = taskDataGetter.getBattleDataCache().getDef(pgm);
            if (def > curNum) {
                curNum = def;
            }
        }
        return curNum;
    }
}
