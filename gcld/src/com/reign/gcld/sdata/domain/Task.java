package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;

public class Task implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private String name;
    private String intros;
    private String introl;
    private String target;
    private String reward;
    private Integer next;
    private Integer area;
    private String markTrace;
    private String pic;
    private String newTrace;
    private String plot;
    private Integer telephone;
    private String iosMarktrace;
    
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
    
    public String getIntros() {
        return this.intros;
    }
    
    public void setIntros(final String intros) {
        this.intros = intros;
    }
    
    public String getIntrol() {
        return this.introl;
    }
    
    public void setIntrol(final String introl) {
        this.introl = introl;
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
    
    public Integer getNext() {
        return this.next;
    }
    
    public void setNext(final Integer next) {
        this.next = next;
    }
    
    public Integer getArea() {
        return this.area;
    }
    
    public void setArea(final Integer area) {
        this.area = area;
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
    
    public String getNewTrace() {
        return this.newTrace;
    }
    
    public void setNewTrace(final String newTrace) {
        this.newTrace = newTrace;
    }
    
    public String getPlot() {
        return this.plot;
    }
    
    public void setPlot(final String plot) {
        this.plot = plot;
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
