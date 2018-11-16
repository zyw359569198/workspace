package com.reign.gcld.rank.common;

import com.reign.gcld.common.*;
import com.reign.gcld.rank.domain.*;
import java.util.*;
import com.reign.gcld.rank.service.*;

public class NationTaskRankerNew
{
    private NationTaskKillRanker ranker1;
    private NationTaskKillRanker ranker2;
    
    public NationTaskRankerNew(final int type, final IDataGetter dataGetter) {
        this.ranker1 = null;
        this.ranker2 = null;
        final List<TaskKillInfo> list = dataGetter.getTaskKillInfoDao().getList();
        this.initNationTaskRanker(dataGetter, list, type);
    }
    
    private void initNationTaskRanker(final IDataGetter dataGetter, final List<TaskKillInfo> list, final int type) {
        if (type == 1) {
            final List<TaskKillInfo> list2 = new ArrayList<TaskKillInfo>();
            final List<TaskKillInfo> list3 = new ArrayList<TaskKillInfo>();
            for (final TaskKillInfo killInfo : list) {
                if (killInfo == null) {
                    continue;
                }
                final int taskId = killInfo.getTaskId();
                if (this.getListByTaskId(taskId) == 0) {
                    list2.add(killInfo);
                }
                else {
                    list3.add(killInfo);
                }
            }
            (this.ranker1 = new NationTaskKillRanker(dataGetter, list2)).init();
            (this.ranker2 = new NationTaskKillRanker(dataGetter, list3)).init();
        }
        else {
            (this.ranker1 = new NationTaskKillRanker(dataGetter, list)).init();
        }
    }
    
    public NationTaskKillRanker getRanker(final int taskId) {
        final int taskType = RankService.getTaskTypeById(taskId);
        if (taskType != 1) {
            return this.ranker1;
        }
        final int type = this.getListByTaskId(taskId);
        if (type == 0) {
            return this.ranker1;
        }
        return this.ranker2;
    }
    
    private int getListByTaskId(final int taskId) {
        return taskId % 2;
    }
    
    public void clear() {
        if (this.ranker1 != null) {
            this.ranker1.clear();
        }
        if (this.ranker2 != null) {
            this.ranker2.clear();
        }
    }
    
    public NationTaskKillRanker getRanker1() {
        return this.ranker1;
    }
    
    public void setRanker1(final NationTaskKillRanker ranker1) {
        this.ranker1 = ranker1;
    }
    
    public void clearByForceId(final Integer forceid) {
        if (this.ranker1 != null) {
            this.ranker1.clearByForceId(forceid);
        }
    }
}
