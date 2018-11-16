package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;

public class TaskDaily implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer group;
    private Integer index;
    private String name;
    private String introS;
    private String introL;
    private String target;
    private String reward;
    private String markTrace;
    private String pic;
    
    public Integer getGroup() {
        return this.group;
    }
    
    public void setGroup(final Integer group) {
        this.group = group;
    }
    
    public Integer getIndex() {
        return this.index;
    }
    
    public void setIndex(final Integer index) {
        this.index = index;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public String getIntroS() {
        return this.introS;
    }
    
    public void setIntroS(final String introS) {
        this.introS = introS;
    }
    
    public String getIntroL() {
        return this.introL;
    }
    
    public void setIntroL(final String introL) {
        this.introL = introL;
    }
    
    public String getTarget() {
        return this.target;
    }
    
    public void setTarget(final String target) {
        this.target = target;
    }
    
    public String getReward() {
        return this.reward;
    }
    
    public void setReward(final String reward) {
        this.reward = reward;
    }
    
    public String getMarkTrace() {
        return this.markTrace;
    }
    
    public void setMarkTrace(final String markTrace) {
        this.markTrace = markTrace;
    }
    
    public String getPic() {
        return this.pic;
    }
    
    public void setPic(final String pic) {
        this.pic = pic;
    }
}
