package com.reign.gcld.task.request;

import com.reign.gcld.player.dto.*;
import com.reign.gcld.task.common.*;
import com.reign.gcld.common.*;
import com.reign.gcld.task.message.*;

public class TaskRequestTechLevel extends TaskRequestBase
{
    private int techId;
    private int level;
    
    public TaskRequestTechLevel(final String[] s) {
        this.techId = Integer.valueOf(s[1]);
        this.level = Integer.valueOf(s[2]);
    }
    
    @Override
    public boolean check(final PlayerDto playerDto, final IDataGetter dataGetter, final int vId) {
        return false;
    }
    
    @Override
    public boolean doRequest(final PlayerDto playerDto, final IDataGetter dataGetter, final int vId) {
        return this.check(playerDto, dataGetter, vId);
    }
    
    @Override
    public TaskRequestProcessViewer getProcess(final PlayerDto playerDto, final IDataGetter dataGetter, final int vId) {
        dataGetter.getPlayerTechDao().getPlayerTech(playerDto.playerId, this.techId);
        final int nowLevel = 0;
        final TaskRequestProcessViewer viewer = new TaskRequestProcessViewer(nowLevel >= this.level, this.level, nowLevel);
        return viewer;
    }
    
    @Override
    public void handleMessage(final TaskMessage message, final IDataGetter dataGetter, final int vId) {
        if (message instanceof TaskMessageTechLevel) {
            final int nowLevel = 0;
            final TaskRequestProcessViewer viewer = new TaskRequestProcessViewer(nowLevel >= this.level, this.level, nowLevel);
            final TaskChangeContent taskChangeContent = new TaskChangeContent(this.getTask(), viewer.getProcessStr(), viewer.isCompleted());
            Players.push(message.getPlayerId(), PushCommand.PUSH_TASK, TaskBuilderJson.sendJsonTaskChange(taskChangeContent, Players.getPlayer(message.getPlayerId()), dataGetter));
        }
    }
    
    @Override
    public boolean isConcernedMessage(final TaskMessage message) {
        return message instanceof TaskMessageTechLevel;
    }
}
