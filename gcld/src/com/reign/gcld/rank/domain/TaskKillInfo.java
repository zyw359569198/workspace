package com.reign.gcld.rank.domain;

import com.reign.framework.mybatis.*;

public class TaskKillInfo implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer vid;
    private Integer playerId;
    private Integer taskId;
    private Integer killnum;
    private Integer isrewarded;
    private Long updatetime;
    
    public Integer getVid() {
        return this.vid;
    }
    
    public void setVid(final Integer vid) {
        this.vid = vid;
    }
    
    public Integer getPlayerId() {
        return this.playerId;
    }
    
    public void setPlayerId(final Integer playerId) {
        this.playerId = playerId;
    }
    
    public Integer getTaskId() {
        return this.taskId;
    }
    
    public void setTaskId(final Integer taskId) {
        this.taskId = taskId;
    }
    
    public Integer getKillnum() {
        return this.killnum;
    }
    
    public void setKillnum(final Integer killnum) {
        this.killnum = killnum;
    }
    
    public Integer getIsrewarded() {
        return this.isrewarded;
    }
    
    public void setIsrewarded(final Integer isrewarded) {
        this.isrewarded = isrewarded;
    }
    
    public Long getUpdatetime() {
        return this.updatetime;
    }
    
    public void setUpdatetime(final Long updatetime) {
        this.updatetime = updatetime;
    }
}
