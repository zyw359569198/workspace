package com.reign.gcld.task.request;

import com.reign.gcld.player.dto.*;
import com.reign.gcld.task.common.*;
import com.reign.gcld.common.*;
import com.reign.gcld.task.message.*;

public class TaskRequestStoreBuy extends TaskRequestBase
{
    private int count;
    
    public TaskRequestStoreBuy(final String[] s) {
        this.count = Integer.parseInt(s[1]);
    }
    
    @Override
    public boolean check(final PlayerDto playerDto, final IDataGetter taskDataGetter, final int vId) {
        final int num = taskDataGetter.getStoreHouseDao().getCountByPlayerId(playerDto.playerId);
        return num >= this.count;
    }
    
    @Override
    public boolean doRequest(final PlayerDto playerDto, final IDataGetter taskDataGetter, final int vId) {
        return this.check(playerDto, taskDataGetter, vId);
    }
    
    @Override
    public void handleMessage(final TaskMessage message, final IDataGetter taskDataGetter, final int vId) {
        if (message instanceof TaskMessageStoreBuy) {
            final int num = taskDataGetter.getStoreHouseDao().getCountByPlayerId(message.getPlayerId());
            final TaskRequestProcessViewer viewer = new TaskRequestProcessViewer(((TaskMessageStoreBuy)message).getNum() >= this.count, this.count, num);
            final TaskChangeContent taskChangeContent = new TaskChangeContent(this.getTask(), viewer.getProcessStr(), viewer.isCompleted());
            Players.push(message.getPlayerId(), PushCommand.PUSH_TASK, TaskBuilderJson.sendJsonTaskChange(taskChangeContent, Players.getPlayer(message.getPlayerId()), taskDataGetter));
        }
    }
    
    @Override
    public boolean isConcernedMessage(final TaskMessage message) {
        return message instanceof TaskMessageStoreBuy;
    }
    
    @Override
    public TaskRequestProcessViewer getProcess(final PlayerDto playerDto, final IDataGetter taskDataGetter, final int vId) {
        boolean completed = false;
        final int num = taskDataGetter.getStoreHouseDao().getCountByPlayerId(playerDto.playerId);
        if (num >= this.count) {
            completed = true;
        }
        final TaskRequestProcessViewer rtn = new TaskRequestProcessViewer(completed, this.count, completed ? this.count : num);
        return rtn;
    }
}
