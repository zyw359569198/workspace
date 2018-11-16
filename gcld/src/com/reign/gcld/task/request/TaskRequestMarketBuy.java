package com.reign.gcld.task.request;

import com.reign.gcld.player.dto.*;
import com.reign.gcld.task.domain.*;
import com.reign.gcld.task.common.*;
import com.reign.gcld.common.*;
import com.reign.gcld.task.message.*;

public class TaskRequestMarketBuy extends TaskRequestCount
{
    public TaskRequestMarketBuy(final String[] s) {
        super(s);
    }
    
    @Override
    public boolean check(final PlayerDto playerDto, final IDataGetter dataGetter, final int vId) {
        final PlayerTask playerTask = dataGetter.getPlayerTaskDao().read(vId);
        if (playerTask == null) {
            return false;
        }
        final Double num = dataGetter.getPlayerMarketDao().getNum(playerDto.playerId);
        return num < 1.0 || playerTask.getProcess() >= this.getTimes();
    }
    
    @Override
    public boolean doRequest(final PlayerDto playerDto, final IDataGetter dataGetter, final int vId) {
        return this.check(playerDto, dataGetter, vId);
    }
    
    @Override
    public TaskRequestProcessViewer getProcess(final PlayerDto playerDto, final IDataGetter dataGetter, final int vId) {
        boolean completed = false;
        final PlayerTask playerTask = dataGetter.getPlayerTaskDao().read(vId);
        if (playerTask != null) {
            final Double num = dataGetter.getPlayerMarketDao().getNum(playerDto.playerId);
            if (num < 1.0) {
                completed = true;
            }
            else if (playerTask.getProcess() >= this.getTimes()) {
                completed = true;
            }
        }
        final TaskRequestProcessViewer viewer = new TaskRequestProcessViewer(completed, this.getTimes(), 0);
        return viewer;
    }
    
    @Override
    public void handleMessage(final TaskMessage message, final IDataGetter dataGetter, final int vId) {
        if (message instanceof TaskMessageMarketBuy) {
            final PlayerTask playerTask = dataGetter.getPlayerTaskDao().read(vId);
            if (playerTask == null) {
                return;
            }
            dataGetter.getPlayerTaskDao().addProcess(vId, 1);
            boolean completed = false;
            final Double num = dataGetter.getPlayerMarketDao().getNum(message.getPlayerId());
            if (num < 1.0) {
                completed = true;
            }
            else if (playerTask.getProcess() + 1 >= this.getTimes()) {
                completed = true;
            }
            final TaskRequestProcessViewer viewer = new TaskRequestProcessViewer(completed, this.getTimes(), playerTask.getProcess() + 1);
            final TaskChangeContent taskChangeContent = new TaskChangeContent(this.getTask(), viewer.getProcessStr(), viewer.isCompleted());
            Players.push(message.getPlayerId(), PushCommand.PUSH_TASK, TaskBuilderJson.sendJsonTaskChange(taskChangeContent, Players.getPlayer(message.getPlayerId()), dataGetter));
        }
    }
    
    @Override
    public boolean isConcernedMessage(final TaskMessage message) {
        return message instanceof TaskMessageMarketBuy;
    }
}
