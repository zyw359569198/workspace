package com.reign.gcld.task.request;

import com.reign.gcld.task.common.*;
import com.reign.gcld.task.domain.*;
import com.reign.gcld.common.*;
import com.reign.gcld.player.dto.*;
import com.reign.gcld.player.common.*;
import com.reign.gcld.battle.common.*;

public abstract class TaskRequestBase implements ITaskRequest
{
    protected GameTask gameTask;
    
    public TaskRequestBase() {
        this.gameTask = null;
    }
    
    public String getTarget() {
        return "";
    }
    
    @Override
    public void setTask(final GameTask gameTask) {
        this.gameTask = gameTask;
    }
    
    public GameTask getTask() {
        return this.gameTask;
    }
    
    @Override
    public void init(final PlayerTask playerTask, final IDataGetter dataGetter) {
    }
    
    @Override
    public boolean isMobileFastFinish(final PlayerDto playerDto) {
        try {
            return playerDto != null && playerDto.platForm != null && playerDto.platForm != PlatForm.PC && this.gameTask.getTelephone() == 0;
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error(String.valueOf(this.getClass().getSimpleName()) + " catch Exception", e);
            return false;
        }
    }
}
