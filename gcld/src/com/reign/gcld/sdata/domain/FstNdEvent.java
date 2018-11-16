package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;

public class FstNdEvent implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private String name;
    private String pic;
    private String greeting;
    private String plot1;
    private String reward1;
    private String cost1;
    private String bye1;
    private String plot2;
    private String reward2;
    private String cost2;
    private String bye2;
    
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
    
    public String getPic() {
        return this.pic;
    }
    
    public void setPic(final String pic) {
        this.pic = pic;
    }
    
    public String getGreeting() {
        return this.greeting;
    }
    
    public void setGreeting(final String greeting) {
        this.greeting = greeting;
    }
    
    public String getPlot1() {
        return this.plot1;
    }
    
    public void setPlot1(final String plot1) {
        this.plot1 = plot1;
    }
    
    public String getReward1() {
        return this.reward1;
    }
    
    public void setReward1(final String reward1) {
        this.reward1 = reward1;
    }
    
    public String getCost1() {
        return this.cost1;
    }
    
    public void setCost1(final String cost1) {
        this.cost1 = cost1;
    }
    
    public String getBye1() {
        return this.bye1;
    }
    
    public void setBye1(final String bye1) {
        this.bye1 = bye1;
    }
    
    public String getPlot2() {
        return this.plot2;
    }
    
    public void setPlot2(final String plot2) {
        this.plot2 = plot2;
    }
    
    public String getReward2() {
        return this.reward2;
    }
    
    public void setReward2(final String reward2) {
        this.reward2 = reward2;
    }
    
    public String getCost2() {
        return this.cost2;
    }
    
    public void setCost2(final String cost2) {
        this.cost2 = cost2;
    }
    
    public String getBye2() {
        return this.bye2;
    }
    
    public void setBye2(final String bye2) {
        this.bye2 = bye2;
    }
}
