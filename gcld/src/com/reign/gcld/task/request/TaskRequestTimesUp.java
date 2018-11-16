package com.reign.gcld.task.request;

import com.reign.gcld.player.dto.*;
import com.reign.gcld.task.domain.*;
import com.reign.gcld.task.common.*;
import com.reign.gcld.common.*;
import com.reign.gcld.task.message.*;

public class TaskRequestTimesUp extends TaskRequestBase
{
    private int time;
    
    public TaskRequestTimesUp(final String[] s) {
        this.time = Integer.parseInt(s[1]);
    }
    
    @Override
    public boolean check(final PlayerDto playerDto, final IDataGetter taskDataGetter, final int vId) {
        final PlayerTask playerTask = taskDataGetter.getPlayerTaskDao().read(vId);
        return playerTask.getStartTime() + this.time * 60000L <= System.currentTimeMillis();
    }
    
    @Override
    public boolean doRequest(final PlayerDto playerDto, final IDataGetter taskDataGetter, final int vId) {
        return this.check(playerDto, taskDataGetter, vId);
    }
    
    @Override
    public void handleMessage(final TaskMessage message, final IDataGetter taskDataGetter, final int vId) {
        if (message instanceof TaskMessageTimesUp) {
            final PlayerTask playerTask = taskDataGetter.getPlayerTaskDao().read(vId);
            boolean res = true;
            int min = 0;
            if (playerTask.getStartTime() + this.time * 60000L > System.currentTimeMillis()) {
                res = false;
                min = (int)(this.time - (System.currentTimeMillis() - playerTask.getStartTime()) / 60000L);
            }
            final PlayerDto playerDto = Players.getPlayer(message.getPlayerId());
            if (playerDto != null) {
                final TaskRequestProcessViewer viewer = new TaskRequestProcessViewer(res, this.time, min);
                final TaskChangeContent taskChangeContent = new TaskChangeContent(this.getTask(), viewer.getProcessStr(), viewer.isCompleted());
                Players.push(message.getPlayerId(), PushCommand.PUSH_TASK, TaskBuilderJson.sendJsonTaskChange(taskChangeContent, playerDto, taskDataGetter));
            }
        }
    }
    
    @Override
    public boolean isConcernedMessage(final TaskMessage message) {
        return message instanceof TaskMessageTimesUp;
    }
    
    @Override
    public TaskRequestProcessViewer getProcess(final PlayerDto playerDto, final IDataGetter taskDataGetter, final int vId) {
        final PlayerTask playerTask = taskDataGetter.getPlayerTaskDao().read(vId);
        boolean completed = true;
        int min = 0;
        if (playerTask.getStartTime() + this.time * 60000L > System.currentTimeMillis()) {
            completed = false;
            min = (int)(this.time - (System.currentTimeMillis() - playerTask.getStartTime()) / 60000L);
        }
        final TaskRequestProcessViewer rtn = new TaskRequestProcessViewer(completed, this.time, completed ? this.time : min);
        return rtn;
    }
}
