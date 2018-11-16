package com.reign.gcld.task.request;

import com.reign.gcld.player.dto.*;
import com.reign.gcld.common.*;
import com.reign.gcld.task.message.*;

public class TaskRequestDone extends TaskRequestBase
{
    public TaskRequestDone(final String[] s) {
    }
    
    @Override
    public boolean check(final PlayerDto playerDto, final IDataGetter taskDataGetter, final int vId) {
        return true;
    }
    
    @Override
    public boolean doRequest(final PlayerDto playerDto, final IDataGetter taskDataGetter, final int vId) {
        return this.check(playerDto, taskDataGetter, vId);
    }
    
    @Override
    public void handleMessage(final TaskMessage message, final IDataGetter taskDataGetter, final int vId) {
        final boolean b = message instanceof TaskMessageBuilding;
    }
    
    @Override
    public boolean isConcernedMessage(final TaskMessage message) {
        return false;
    }
    
    @Override
    public TaskRequestProcessViewer getProcess(final PlayerDto playerDto, final IDataGetter dataGetter, final int vId) {
        final TaskRequestProcessViewer rtn = new TaskRequestProcessViewer(true, 1, 1);
        return rtn;
    }
}
