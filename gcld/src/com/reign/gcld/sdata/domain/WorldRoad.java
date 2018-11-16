package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;

public class WorldRoad implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private Integer start;
    private Integer end;
    private Integer length;
    private String weiReward;
    private String shuReward;
    private String wuReward;
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public Integer getStart() {
        return this.start;
    }
    
    public void setStart(final Integer start) {
        this.start = start;
    }
    
    public Integer getEnd() {
        return this.end;
    }
    
    public void setEnd(final Integer end) {
        this.end = end;
    }
    
    public Integer getLength() {
        return this.length;
    }
    
    public void setLength(final Integer length) {
        this.length = length;
    }
    
    public String getWeiReward() {
        return this.weiReward;
    }
    
    public void setWeiReward(final String weiReward) {
        this.weiReward = weiReward;
    }
    
    public String getShuReward() {
        return this.shuReward;
    }
    
    public void setShuReward(final String shuReward) {
        this.shuReward = shuReward;
    }
    
    public String getWuReward() {
        return this.wuReward;
    }
    
    public void setWuReward(final String wuReward) {
        this.wuReward = wuReward;
    }
}
