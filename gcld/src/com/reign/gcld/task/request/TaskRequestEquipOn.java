package com.reign.gcld.task.request;

import com.reign.gcld.player.dto.*;
import com.reign.gcld.task.common.*;
import com.reign.gcld.common.*;
import com.reign.gcld.task.message.*;

public class TaskRequestEquipOn extends TaskRequestBase
{
    private int type;
    private int quality;
    private int degree;
    private int num;
    
    public TaskRequestEquipOn(final String[] s) {
        this.type = Integer.parseInt(s[1]);
        this.quality = Integer.parseInt(s[2]);
        this.degree = Integer.parseInt(s[3]);
        this.num = Integer.parseInt(s[4]);
    }
    
    @Override
    public boolean check(final PlayerDto playerDto, final IDataGetter dataGetter, final int vId) {
        final int nowNum = dataGetter.getStoreHouseDao().getWearNumByLvOrQuality(playerDto.playerId, this.type, this.degree, this.quality, 1);
        return nowNum >= this.num;
    }
    
    @Override
    public boolean doRequest(final PlayerDto playerDto, final IDataGetter dataGetter, final int vId) {
        return this.check(playerDto, dataGetter, vId);
    }
    
    @Override
    public TaskRequestProcessViewer getProcess(final PlayerDto playerDto, final IDataGetter dataGetter, final int vId) {
        final int nowNum = dataGetter.getStoreHouseDao().getWearNumByLvOrQuality(playerDto.playerId, this.type, this.degree, this.quality, 1);
        final TaskRequestProcessViewer viewer = new TaskRequestProcessViewer(nowNum >= this.num, this.num, nowNum);
        return viewer;
    }
    
    @Override
    public void handleMessage(final TaskMessage message, final IDataGetter dataGetter, final int vId) {
        if (message instanceof TaskMessageEquipOn) {
            final int nowNum = dataGetter.getStoreHouseDao().getWearNumByLvOrQuality(message.getPlayerId(), this.type, this.degree, this.quality, 1);
            final TaskRequestProcessViewer viewer = new TaskRequestProcessViewer(nowNum >= this.num, this.num, nowNum);
            final TaskChangeContent taskChangeContent = new TaskChangeContent(this.getTask(), viewer.getProcessStr(), viewer.isCompleted());
            final PlayerDto playerDto = Players.getPlayer(message.getPlayerId());
            Players.push(message.getPlayerId(), PushCommand.PUSH_TASK, TaskBuilderJson.sendJsonTaskChange(taskChangeContent, playerDto, dataGetter));
        }
    }
    
    @Override
    public boolean isConcernedMessage(final TaskMessage message) {
        return message instanceof TaskMessageEquipOn;
    }
    
    public int getType() {
        return this.type;
    }
    
    public int getQuality() {
        return this.quality;
    }
    
    public int getDegree() {
        return this.degree;
    }
    
    public int getNum() {
        return this.num;
    }
}
