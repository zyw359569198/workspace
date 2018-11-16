package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;

public class Chargeitem implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private String name;
    private Integer alert;
    private Integer cost;
    private Double param;
    private Integer lv;
    private String intro;
    private Integer pic;
    private String explain;
    private Integer ifShow;
    
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
    
    public Integer getAlert() {
        return this.alert;
    }
    
    public void setAlert(final Integer alert) {
        this.alert = alert;
    }
    
    public Integer getCost() {
        return this.cost;
    }
    
    public void setCost(final Integer cost) {
        this.cost = cost;
    }
    
    public Double getParam() {
        return this.param;
    }
    
    public void setParam(final Double param) {
        this.param = param;
    }
    
    public Integer getLv() {
        return this.lv;
    }
    
    public void setLv(final Integer lv) {
        this.lv = lv;
    }
    
    public String getIntro() {
        return this.intro;
    }
    
    public void setIntro(final String intro) {
        this.intro = intro;
    }
    
    public Integer getPic() {
        return this.pic;
    }
    
    public void setPic(final Integer pic) {
        this.pic = pic;
    }
    
    public String getExplain() {
        return this.explain;
    }
    
    public void setExplain(final String explain) {
        this.explain = explain;
    }
    
    public Integer getIfShow() {
        return this.ifShow;
    }
    
    public void setIfShow(final Integer ifShow) {
        this.ifShow = ifShow;
    }
}
