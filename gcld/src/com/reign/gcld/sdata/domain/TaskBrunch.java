package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;

public class TaskBrunch implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer brunch;
    private Integer index;
    private String name;
    private String introS;
    private String introL;
    private String target;
    private String reward;
    private String marktrace;
    private String pic;
    private Integer telephone;
    private String iosMarktrace;
    
    public Integer getBrunch() {
        return this.brunch;
    }
    
    public void setBrunch(final Integer brunch) {
        this.brunch = brunch;
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
    
    public String getMarktrace() {
        return this.marktrace;
    }
    
    public void setMarktrace(final String marktrace) {
        this.marktrace = marktrace;
    }
    
    public String getPic() {
        return this.pic;
    }
    
    public void setPic(final String pic) {
        this.pic = pic;
    }
    
    public Integer getTelephone() {
        return this.telephone;
    }
    
    public void setTelephone(final Integer telephone) {
        this.telephone = telephone;
    }
    
    public String getIosMarktrace() {
        return this.iosMarktrace;
    }
    
    public void setIosMarktrace(final String iosMarktrace) {
        this.iosMarktrace = iosMarktrace;
    }
}
