package com.reign.gcld.task.request;

import com.reign.gcld.player.dto.*;
import com.reign.gcld.common.*;
import com.reign.gcld.task.message.*;
import com.reign.gcld.task.common.*;
import com.reign.gcld.task.domain.*;

public interface ITaskRequest
{
    boolean check(final PlayerDto p0, final IDataGetter p1, final int p2);
    
    boolean doRequest(final PlayerDto p0, final IDataGetter p1, final int p2);
    
    TaskRequestProcessViewer getProcess(final PlayerDto p0, final IDataGetter p1, final int p2);
    
    void handleMessage(final TaskMessage p0, final IDataGetter p1, final int p2);
    
    void setTask(final GameTask p0);
    
    void init(final PlayerTask p0, final IDataGetter p1);
    
    boolean isConcernedMessage(final TaskMessage p0);
    
    boolean isMobileFastFinish(final PlayerDto p0);
}
