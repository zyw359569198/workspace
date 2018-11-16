package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;

public class IndivTasks implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private Integer taskType;
    private Integer indivTaskType;
    private Integer grade;
    private String name;
    private String intro;
    private String req;
    private String reward;
    private String pic;
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public Integer getTaskType() {
        return this.taskType;
    }
    
    public void setTaskType(final Integer taskType) {
        this.taskType = taskType;
    }
    
    public Integer getIndivTaskType() {
        return this.indivTaskType;
    }
    
    public void setIndivTaskType(final Integer indivTaskType) {
        this.indivTaskType = indivTaskType;
    }
    
    public Integer getGrade() {
        return this.grade;
    }
    
    public void setGrade(final Integer grade) {
        this.grade = grade;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public String getIntro() {
        return this.intro;
    }
    
    public void setIntro(final String intro) {
        this.intro = intro;
    }
    
    public String getReq() {
        return this.req;
    }
    
    public void setReq(final String req) {
        this.req = req;
    }
    
    public String getReward() {
        return this.reward;
    }
    
    public void setReward(final String reward) {
        this.reward = reward;
    }
    
    public String getPic() {
        return this.pic;
    }
    
    public void setPic(final String pic) {
        this.pic = pic;
    }
}
