package com.reign.kfgz.domain;

import com.reign.framework.hibernate.model.*;

public class KfgzReward implements IModel
{
    int pk;
    int groupId;
    int layerId;
    int battleRewardId;
    int endRewardId;
    
    public int getPk() {
        return this.pk;
    }
    
    public void setPk(final int pk) {
        this.pk = pk;
    }
    
    public int getGroupId() {
        return this.groupId;
    }
    
    public void setGroupId(final int groupId) {
        this.groupId = groupId;
    }
    
    public int getLayerId() {
        return this.layerId;
    }
    
    public void setLayerId(final int layerId) {
        this.layerId = layerId;
    }
    
    public int getBattleRewardId() {
        return this.battleRewardId;
    }
    
    public void setBattleRewardId(final int battleRewardId) {
        this.battleRewardId = battleRewardId;
    }
    
    public int getEndRewardId() {
        return this.endRewardId;
    }
    
    public void setEndRewardId(final int endRewardId) {
        this.endRewardId = endRewardId;
    }
}
