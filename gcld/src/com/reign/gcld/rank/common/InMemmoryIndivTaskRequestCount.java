package com.reign.gcld.rank.common;

import com.reign.gcld.common.util.*;
import com.reign.gcld.rank.domain.*;
import com.reign.gcld.battle.common.*;
import java.util.*;
import com.reign.gcld.common.*;

public class InMemmoryIndivTaskRequestCount extends InMemmoryIndivTaskRequest implements Cloneable
{
    private int count;
    private int nowCount;
    
    public InMemmoryIndivTaskRequestCount(final String[] reqs) {
        this.count = Integer.parseInt(reqs[1]);
        this.nowCount = 0;
    }
    
    @Override
    public boolean isConcerned(final InMemmoryIndivTaskMessage message) {
        return super.isConcerned(message);
    }
    
    @Override
    public boolean handleMessage(final InMemmoryIndivTaskMessage message, final IDataGetter getter, final InMemmoryIndivTask task) {
        final int count = message.count;
        this.nowCount += count;
        this.nowCount = ((this.nowCount > this.count) ? this.count : this.nowCount);
        if (this.nowCount >= this.count) {
            this.isFinished = true;
        }
        this.updateDbInfo(getter, task, message);
        return this.isFinished;
    }
    
    @Override
    public void updateDbInfo(final IDataGetter getter, final InMemmoryIndivTask task, final InMemmoryIndivTaskMessage message) {
        try {
            final InMemmoryIndivTaskManager manager = InMemmoryIndivTaskManager.getInstance();
            final Map<Integer, InMemmoryIndivTask> map = manager.getTasks().get(message.playerId);
            if (map == null) {
                return;
            }
            final StringBuffer buffer = new StringBuffer();
            for (final InMemmoryIndivTask inTask : map.values()) {
                buffer.append(inTask.req.currentReqInfo(inTask)).append(";");
            }
            SymbolUtil.removeTheLast(buffer);
            PlayerIndivTask indivTask = getter.getPlayerIndivTaskDao().read(message.playerId);
            if (indivTask == null) {
                indivTask = new PlayerIndivTask();
                indivTask.setPlayerId(message.playerId);
                indivTask.setForceId(message.forceId);
                indivTask.setIndivTaskInfo(buffer.toString());
                getter.getPlayerIndivTaskDao().create(indivTask);
            }
            else {
                getter.getPlayerIndivTaskDao().updateTaskInfo(buffer.toString(), message.playerId);
            }
        }
        catch (Exception e) {
            final ErrorSceneLog error = ErrorSceneLog.getInstance();
            error.error(this, e);
        }
    }
    
    @Override
    public String currentReqInfo(final InMemmoryIndivTask task) {
        final int id = task.id;
        final String value = String.valueOf(id) + ":" + this.count + "," + this.nowCount + "," + this.hasRewarded + "," + task.reward.itemsId + "," + task.reward.itemNum;
        return value;
    }
    
    @Override
    public MultiResult getProcessInfo() {
        final MultiResult result = new MultiResult();
        result.result1 = this.nowCount;
        result.result2 = this.count;
        return result;
    }
    
    @Override
    protected InMemmoryIndivTaskRequestCount clone() throws CloneNotSupportedException {
        return (InMemmoryIndivTaskRequestCount)super.clone();
    }
    
    @Override
    public void restore(final int count, final int hasRewarded) {
        this.nowCount = count;
        this.hasRewarded = hasRewarded;
        this.isFinished = (this.nowCount >= this.count);
    }
}
