package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;

public class SoloEvent implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private String name;
    private String intro;
    private String trigger;
    private String flag1;
    private String operation;
    private String dealcondition;
    private String flag2;
    private String title;
    private String choice1;
    private String choice2;
    private String dealoperation1;
    private String dealoperation2;
    private String failcondition;
    private String failoperation;
    private String faildailog;
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
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
    
    public String getTrigger() {
        return this.trigger;
    }
    
    public void setTrigger(final String trigger) {
        this.trigger = trigger;
    }
    
    public String getFlag1() {
        return this.flag1;
    }
    
    public void setFlag1(final String flag1) {
        this.flag1 = flag1;
    }
    
    public String getOperation() {
        return this.operation;
    }
    
    public void setOperation(final String operation) {
        this.operation = operation;
    }
    
    public String getDealcondition() {
        return this.dealcondition;
    }
    
    public void setDealcondition(final String dealcondition) {
        this.dealcondition = dealcondition;
    }
    
    public String getFlag2() {
        return this.flag2;
    }
    
    public void setFlag2(final String flag2) {
        this.flag2 = flag2;
    }
    
    public String getTitle() {
        return this.title;
    }
    
    public void setTitle(final String title) {
        this.title = title;
    }
    
    public String getChoice1() {
        return this.choice1;
    }
    
    public void setChoice1(final String choice1) {
        this.choice1 = choice1;
    }
    
    public String getChoice2() {
        return this.choice2;
    }
    
    public void setChoice2(final String choice2) {
        this.choice2 = choice2;
    }
    
    public String getDealoperation1() {
        return this.dealoperation1;
    }
    
    public void setDealoperation1(final String dealoperation1) {
        this.dealoperation1 = dealoperation1;
    }
    
    public String getDealoperation2() {
        return this.dealoperation2;
    }
    
    public void setDealoperation2(final String dealoperation2) {
        this.dealoperation2 = dealoperation2;
    }
    
    public String getFailcondition() {
        return this.failcondition;
    }
    
    public void setFailcondition(final String failcondition) {
        this.failcondition = failcondition;
    }
    
    public String getFailoperation() {
        return this.failoperation;
    }
    
    public void setFailoperation(final String failoperation) {
        this.failoperation = failoperation;
    }
    
    public String getFaildailog() {
        return this.faildailog;
    }
    
    public void setFaildailog(final String faildailog) {
        this.faildailog = faildailog;
    }
}
