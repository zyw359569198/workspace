package com.reign.gcld.rank.common;

import com.reign.gcld.log.*;
import com.reign.gcld.common.*;
import com.reign.framework.json.*;
import com.reign.gcld.player.dto.*;

public class InMemmoryIndivTaskDelegate
{
    private InMemmoryIndivTask task;
    private static ErrorLogger log;
    
    static {
        InMemmoryIndivTaskDelegate.log = new ErrorLogger();
    }
    
    public InMemmoryIndivTaskDelegate(final InMemmoryIndivTask inMemmoryIndivTask) {
        this.task = inMemmoryIndivTask;
    }
    
    public void handle(final InMemmoryIndivTaskMessage message, final IDataGetter getter) {
        if (this.task.req == null || !this.task.req.isConcerned(message)) {
            return;
        }
        final boolean isFinish = this.task.req.handleMessage(message, getter, this.task);
        this.pushTaskChangeInfo(message, getter);
        if (isFinish && this.task.reward != null) {
            this.pushTaskFinishInfo(message, getter);
        }
    }
    
    private void pushTaskFinishInfo(final InMemmoryIndivTaskMessage message, final IDataGetter getter) {
        try {
            final int playerId = message.playerId;
            final JsonDocument doc = IndividualJsonBuilder.getTaskFinishInfo(this.task, playerId, getter);
            Players.push(playerId, PushCommand.PUSH_NATIONINDIV_REWARD, doc.toByte());
        }
        catch (Exception e) {
            InMemmoryIndivTaskDelegate.log.error(this, e);
        }
    }
    
    private void pushTaskChangeInfo(final InMemmoryIndivTaskMessage message, final IDataGetter getter) {
        try {
            final int playerId = message.playerId;
            final JsonDocument doc = IndividualJsonBuilder.getTasksSimpleJson(this.task, playerId, getter);
            Players.push(playerId, PushCommand.PUSH_NATIONINDIV_TASK, doc.toByte());
        }
        catch (Exception e) {
            InMemmoryIndivTaskDelegate.log.error(this, e);
        }
    }
    
    public InMemmoryIndivTaskContent getContent() {
        final InMemmoryIndivTaskContent content = new InMemmoryIndivTaskContent(this.task);
        return content;
    }
    
    public String handleReward(final IDataGetter getter, final PlayerDto playerDto) {
        String result = "";
        if (this.task.reward != null && this.task.req.hasRewarded == 0) {
            result = this.task.reward.handleReward(getter, playerDto);
            this.task.req.hasRewarded = 1;
            final InMemmoryIndivTaskMessage message = new InMemmoryIndivTaskMessage();
            message.playerId = playerDto.playerId;
            message.forceId = playerDto.forceId;
            this.task.req.updateDbInfo(getter, this.task, message);
            this.pushTaskChangeInfo(message, getter);
        }
        return result;
    }
}
