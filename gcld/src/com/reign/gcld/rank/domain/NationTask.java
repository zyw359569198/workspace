package com.reign.gcld.rank.domain;

import com.reign.framework.mybatis.*;
import java.util.*;

public class NationTask implements IModel, Comparable<NationTask>
{
    private static final long serialVersionUID = 1L;
    private Integer nationTaskId;
    private Integer forceid;
    private Integer target;
    private Date endtime;
    private Integer attType;
    private Integer iswin;
    private Long finishtime;
    private String taskRelateInfo;
    
    public Integer getNationTaskId() {
        return this.nationTaskId;
    }
    
    public void setNationTaskId(final Integer nationTaskId) {
        this.nationTaskId = nationTaskId;
    }
    
    public Integer getForceid() {
        return this.forceid;
    }
    
    public void setForceid(final Integer forceid) {
        this.forceid = forceid;
    }
    
    public Integer getTarget() {
        return this.target;
    }
    
    public void setTarget(final Integer target) {
        this.target = target;
    }
    
    public Date getEndtime() {
        return this.endtime;
    }
    
    public void setEndtime(final Date endtime) {
        this.endtime = endtime;
    }
    
    public Integer getAttType() {
        return this.attType;
    }
    
    public void setAttType(final Integer attType) {
        this.attType = attType;
    }
    
    public Integer getIswin() {
        return this.iswin;
    }
    
    public void setIswin(final Integer iswin) {
        this.iswin = iswin;
    }
    
    public Long getFinishtime() {
        return this.finishtime;
    }
    
    public void setFinishtime(final Long finishtime) {
        this.finishtime = finishtime;
    }
    
    public String getTaskRelateInfo() {
        return this.taskRelateInfo;
    }
    
    public void setTaskRelateInfo(final String taskRelateInfo) {
        this.taskRelateInfo = taskRelateInfo;
    }
    
    @Override
	public int compareTo(final NationTask o) {
        if (o == null) {
            return 1;
        }
        if (this.getNationTaskId() > o.getNationTaskId()) {
            return 1;
        }
        return 0;
    }
}
