package com.reign.gcld.task.request;

import com.reign.gcld.player.dto.*;
import com.reign.gcld.task.common.*;
import com.reign.gcld.common.*;
import com.reign.gcld.task.message.*;

public class TaskRequestStoreBuyS extends TaskRequestBase
{
    private int type;
    private int quality;
    private int num;
    
    public TaskRequestStoreBuyS(final String[] s) {
        this.type = Integer.parseInt(s[1]);
        this.quality = Integer.parseInt(s[2]);
        this.num = Integer.parseInt(s[3]);
    }
    
    @Override
    public boolean check(final PlayerDto playerDto, final IDataGetter dataGetter, final int vId) {
        final int nowNum = dataGetter.getStoreHouseDao().getCountByQualityNType(playerDto.playerId, this.type, this.quality, 1);
        return nowNum >= this.num;
    }
    
    @Override
    public boolean doRequest(final PlayerDto playerDto, final IDataGetter dataGetter, final int vId) {
        return this.check(playerDto, dataGetter, vId);
    }
    
    @Override
    public TaskRequestProcessViewer getProcess(final PlayerDto playerDto, final IDataGetter dataGetter, final int vId) {
        final int nowNum = dataGetter.getStoreHouseDao().getCountByQualityNType(playerDto.playerId, this.type, this.quality, 1);
        final TaskRequestProcessViewer viewer = new TaskRequestProcessViewer(nowNum >= this.num, this.num, nowNum);
        return viewer;
    }
    
    @Override
    public void handleMessage(final TaskMessage message, final IDataGetter dataGetter, final int vId) {
        if (message instanceof TaskMessageStoreBuyS) {
            final int nowNum = dataGetter.getStoreHouseDao().getCountByQualityNType(message.getPlayerId(), this.type, this.quality, 1);
            final TaskRequestProcessViewer viewer = new TaskRequestProcessViewer(nowNum >= this.num, this.num, nowNum);
            final TaskChangeContent taskChangeContent = new TaskChangeContent(this.getTask(), viewer.getProcessStr(), viewer.isCompleted());
            Players.push(message.getPlayerId(), PushCommand.PUSH_TASK, TaskBuilderJson.sendJsonTaskChange(taskChangeContent, Players.getPlayer(message.getPlayerId()), dataGetter));
        }
    }
    
    @Override
    public boolean isConcernedMessage(final TaskMessage message) {
        return message instanceof TaskMessageStoreBuyS;
    }
    
    public int getRequestType() {
        return this.type;
    }
    
    public int getRequestQuality() {
        return this.quality;
    }
}
