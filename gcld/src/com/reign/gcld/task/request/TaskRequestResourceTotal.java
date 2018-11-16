package com.reign.gcld.task.request;

import com.reign.gcld.player.dto.*;
import com.reign.gcld.task.common.*;
import com.reign.gcld.common.*;
import com.reign.gcld.task.message.*;

public class TaskRequestResourceTotal extends TaskRequestBase
{
    private int resourceType;
    private int total;
    private long curNum;
    
    public TaskRequestResourceTotal(final String[] s) {
        this.resourceType = Integer.parseInt(s[1]);
        this.total = Integer.parseInt(s[2]);
    }
    
    @Override
    public boolean check(final PlayerDto playerDto, final IDataGetter taskDataGetter, final int vId) {
        if (this.resourceType == 1) {
            this.curNum = taskDataGetter.getPlayerResourceDao().read(playerDto.playerId).getCopper();
        }
        else if (this.resourceType == 2) {
            this.curNum = taskDataGetter.getPlayerResourceDao().read(playerDto.playerId).getWood();
        }
        else if (this.resourceType == 3) {
            this.curNum = taskDataGetter.getPlayerResourceDao().read(playerDto.playerId).getFood();
        }
        else if (this.resourceType == 4) {
            this.curNum = taskDataGetter.getPlayerResourceDao().read(playerDto.playerId).getIron();
        }
        return this.curNum >= this.total;
    }
    
    @Override
    public boolean doRequest(final PlayerDto playerDto, final IDataGetter taskDataGetter, final int vId) {
        return this.check(playerDto, taskDataGetter, vId);
    }
    
    @Override
    public void handleMessage(final TaskMessage message, final IDataGetter dataGetter, final int vId) {
        if (message instanceof TaskMessageResourceTotal) {
            final TaskRequestProcessViewer viewer = this.getProcess(Players.getPlayer(message.getPlayerId()), dataGetter, vId);
            final PlayerDto playerDto = Players.getPlayer(message.getPlayerId());
            if (playerDto != null && this.gameTask.getTaskRequest().isMobileFastFinish(playerDto)) {
                viewer.setCompleted(true);
                viewer.setCurrNum(viewer.getWannaNum());
            }
            final TaskChangeContent taskChangeContent = new TaskChangeContent(this.getTask(), viewer.getProcessStr(), viewer.isCompleted());
            Players.push(message.getPlayerId(), PushCommand.PUSH_TASK, TaskBuilderJson.sendJsonTaskChange(taskChangeContent, Players.getPlayer(message.getPlayerId()), dataGetter));
        }
    }
    
    @Override
    public boolean isConcernedMessage(final TaskMessage message) {
        return message instanceof TaskMessageResourceTotal;
    }
    
    @Override
    public TaskRequestProcessViewer getProcess(final PlayerDto playerDto, final IDataGetter taskDataGetter, final int vId) {
        return new TaskRequestProcessViewer(this.doRequest(playerDto, taskDataGetter, vId), (int)this.curNum, this.total);
    }
}
