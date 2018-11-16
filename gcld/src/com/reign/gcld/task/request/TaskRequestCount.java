package com.reign.gcld.task.request;

import com.reign.gcld.player.dto.*;
import com.reign.gcld.common.*;
import com.reign.gcld.task.domain.*;

public abstract class TaskRequestCount extends TaskRequestBase
{
    protected int times;
    
    public TaskRequestCount() {
        this.times = 0;
    }
    
    public TaskRequestCount(final String[] s) {
        this.times = 0;
        if (s.length > 1) {
            this.times = Integer.parseInt(s[1]);
        }
    }
    
    public TaskRequestCount(final int times) {
        this.times = 0;
        this.times = times;
    }
    
    @Override
    public boolean check(final PlayerDto playerDto, final IDataGetter taskDataGetter, final int vId) {
        final PlayerTask playerTask = taskDataGetter.getPlayerTaskDao().read(vId);
        return playerTask != null && playerTask.getProcess() >= this.times;
    }
    
    @Override
    public boolean doRequest(final PlayerDto playerDto, final IDataGetter taskDataGetter, final int vId) {
        return this.check(playerDto, taskDataGetter, vId);
    }
    
    @Override
    public TaskRequestProcessViewer getProcess(final PlayerDto playerDto, final IDataGetter taskDataGetter, final int vId) {
        final PlayerTask playerTask = taskDataGetter.getPlayerTaskDao().read(vId);
        if (playerTask == null) {
            return new TaskRequestProcessViewer(false, this.times, 0);
        }
        return new TaskRequestProcessViewer(playerTask.getProcess() >= this.times, this.times, playerTask.getProcess());
    }
    
    protected int getTimes() {
        return this.times;
    }
    
    protected void setTimes(final int times) {
        this.times = times;
    }
}
