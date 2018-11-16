package com.reign.gcld.task.request;

import com.reign.gcld.player.dto.*;
import com.reign.gcld.task.common.*;
import com.reign.gcld.common.*;
import com.reign.gcld.task.message.*;

public class TaskRequestChiefLv extends TaskRequestBase
{
    private int lv;
    
    public TaskRequestChiefLv(final String[] s) {
        this.lv = Integer.parseInt(s[1]);
    }
    
    @Override
    public boolean check(final PlayerDto playerDto, final IDataGetter taskDataGetter, final int vId) {
        final int curLv = taskDataGetter.getPlayerDao().read(playerDto.playerId).getPlayerLv();
        return curLv >= this.lv;
    }
    
    @Override
    public boolean doRequest(final PlayerDto playerDto, final IDataGetter taskDataGetter, final int vId) {
        return this.check(playerDto, taskDataGetter, vId);
    }
    
    @Override
    public void handleMessage(final TaskMessage message, final IDataGetter dataGetter, final int vId) {
        if (message instanceof TaskMessageChiefLv) {
            final PlayerDto playerDto = Players.getPlayer(message.getPlayerId());
            final TaskMessageChiefLv taskMessageChiefLv = (TaskMessageChiefLv)message;
            if (this.lv <= taskMessageChiefLv.getLv()) {
                final TaskRequestProcessViewer viewer = new TaskRequestProcessViewer(true, this.lv, this.lv);
                final TaskChangeContent taskChangeContent = new TaskChangeContent(this.getTask(), viewer.getProcessStr(), viewer.isCompleted());
                Players.push(message.getPlayerId(), PushCommand.PUSH_TASK, TaskBuilderJson.sendJsonTaskChange(taskChangeContent, playerDto, dataGetter));
            }
            else {
                final TaskRequestProcessViewer viewer = new TaskRequestProcessViewer(false, this.lv, taskMessageChiefLv.getLv());
                final TaskChangeContent taskChangeContent = new TaskChangeContent(this.getTask(), viewer.getProcessStr(), viewer.isCompleted());
                Players.push(message.getPlayerId(), PushCommand.PUSH_TASK, TaskBuilderJson.sendJsonTaskChange(taskChangeContent, playerDto, dataGetter));
            }
        }
    }
    
    @Override
    public boolean isConcernedMessage(final TaskMessage message) {
        return message instanceof TaskMessageChiefLv;
    }
    
    @Override
    public TaskRequestProcessViewer getProcess(final PlayerDto playerDto, final IDataGetter taskDataGetter, final int vId) {
        boolean completed = false;
        final int curLv = taskDataGetter.getPlayerDao().read(playerDto.playerId).getPlayerLv();
        if (curLv >= this.lv) {
            completed = true;
        }
        final TaskRequestProcessViewer rtn = new TaskRequestProcessViewer(completed, this.lv, completed ? this.lv : curLv);
        return rtn;
    }
}
