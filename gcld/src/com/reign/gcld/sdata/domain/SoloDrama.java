package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;

public class SoloDrama implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private String name;
    private String plot;
    private Integer lv;
    private String openPower;
    private String event1;
    private String event2;
    private String event3;
    private String event4;
    private String event5;
    private int grade;
    
    public int getGrade() {
        return this.grade;
    }
    
    public void setGrade(final int grade) {
        this.grade = grade;
    }
    
    public String getEvent(final int event) {
        if (event == 1) {
            return this.event1;
        }
        if (event == 2) {
            return this.event2;
        }
        if (event == 3) {
            return this.event3;
        }
        if (event == 4) {
            return this.event4;
        }
        if (event == 5) {
            return this.event5;
        }
        return this.event1;
    }
    
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
    
    public String getPlot() {
        return this.plot;
    }
    
    public void setPlot(final String plot) {
        this.plot = plot;
    }
    
    public Integer getLv() {
        return this.lv;
    }
    
    public void setLv(final Integer lv) {
        this.lv = lv;
    }
    
    public String getOpenPower() {
        return this.openPower;
    }
    
    public void setOpenPower(final String openPower) {
        this.openPower = openPower;
    }
    
    public String getEvent1() {
        return this.event1;
    }
    
    public void setEvent1(final String event1) {
        this.event1 = event1;
    }
    
    public String getEvent2() {
        return this.event2;
    }
    
    public void setEvent2(final String event2) {
        this.event2 = event2;
    }
    
    public String getEvent3() {
        return this.event3;
    }
    
    public void setEvent3(final String event3) {
        this.event3 = event3;
    }
    
    public String getEvent4() {
        return this.event4;
    }
    
    public void setEvent4(final String event4) {
        this.event4 = event4;
    }
    
    public String getEvent5() {
        return this.event5;
    }
    
    public void setEvent5(final String event5) {
        this.event5 = event5;
    }
}
