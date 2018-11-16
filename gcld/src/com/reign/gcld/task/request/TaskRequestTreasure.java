package com.reign.gcld.task.request;

import com.reign.gcld.player.dto.*;
import com.reign.gcld.task.common.*;
import com.reign.gcld.common.*;
import com.reign.gcld.task.message.*;

public class TaskRequestTreasure extends TaskRequestBase
{
    private int num;
    
    public TaskRequestTreasure(final String[] s) {
        this.num = Integer.valueOf(s[1]);
    }
    
    @Override
    public boolean check(final PlayerDto playerDto, final IDataGetter dataGetter, final int vId) {
        return dataGetter.getPlayerTreasureDao().getTreasureCount(playerDto.playerId) >= this.num;
    }
    
    @Override
    public boolean doRequest(final PlayerDto playerDto, final IDataGetter dataGetter, final int vId) {
        return this.check(playerDto, dataGetter, vId);
    }
    
    @Override
    public TaskRequestProcessViewer getProcess(final PlayerDto playerDto, final IDataGetter dataGetter, final int vId) {
        final int nowNum = dataGetter.getPlayerTreasureDao().getTreasureCount(playerDto.playerId);
        final TaskRequestProcessViewer viewer = new TaskRequestProcessViewer(nowNum >= this.num, this.num, nowNum);
        return viewer;
    }
    
    @Override
    public void handleMessage(final TaskMessage message, final IDataGetter dataGetter, final int vId) {
        if (message instanceof TaskMessageTreasure) {
            final int nowNum = dataGetter.getPlayerTreasureDao().getTreasureCount(message.getPlayerId());
            final TaskRequestProcessViewer viewer = new TaskRequestProcessViewer(nowNum >= this.num, this.num, nowNum);
            final TaskChangeContent taskChangeContent = new TaskChangeContent(this.getTask(), viewer.getProcessStr(), viewer.isCompleted());
            Players.push(message.getPlayerId(), PushCommand.PUSH_TASK, TaskBuilderJson.sendJsonTaskChange(taskChangeContent, Players.getPlayer(message.getPlayerId()), dataGetter));
        }
    }
    
    @Override
    public boolean isConcernedMessage(final TaskMessage message) {
        return message instanceof TaskMessageTreasure;
    }
}
