package com.reign.gcld.rank.common;

import com.reign.gcld.common.*;

public class InMemmoryIndivTaskContent
{
    int id;
    String name;
    String intro;
    int goal;
    int process;
    int star;
    int maxStar;
    boolean hasUpgrade;
    String pic;
    int rewardType;
    int rewardNum;
    boolean hasRewarded;
    boolean canUpdate;
    int itemId;
    int itemNum;
    
    public InMemmoryIndivTaskContent(final InMemmoryIndivTask task) {
        this.id = task.id;
        this.name = task.name;
        this.intro = task.intro;
        final MultiResult reqProcess = task.req.getProcessInfo();
        this.goal = (int)((reqProcess == null) ? 0 : reqProcess.result2);
        this.process = (int)((reqProcess == null) ? 0 : reqProcess.result1);
        this.star = task.grade;
        this.maxStar = (task.canUpdate ? (task.grade + 1) : task.grade);
        this.canUpdate = task.canUpdate;
        this.hasUpgrade = task.hasUpdate;
        this.pic = task.pic;
        this.hasRewarded = (task.req != null && task.req.hasRewarded != 0);
        final MultiResult rewInfo = (task.reward == null) ? null : task.reward.getRewardInfo();
        this.rewardType = (int)((rewInfo == null) ? 0 : rewInfo.result1);
        this.rewardNum = (int)((rewInfo == null) ? 0 : rewInfo.result2);
        this.itemId = (int)((rewInfo == null) ? 0 : rewInfo.result3);
        this.itemNum = (int)((rewInfo == null) ? 0 : rewInfo.result4);
    }
}
