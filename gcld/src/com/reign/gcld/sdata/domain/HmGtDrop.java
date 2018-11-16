package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;

public class HmGtDrop implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private String intro;
    private Integer items;
    private Integer num1;
    private Integer num2;
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public String getIntro() {
        return this.intro;
    }
    
    public void setIntro(final String intro) {
        this.intro = intro;
    }
    
    public Integer getItems() {
        return this.items;
    }
    
    public void setItems(final Integer items) {
        this.items = items;
    }
    
    public Integer getNum1() {
        return this.num1;
    }
    
    public void setNum1(final Integer num1) {
        this.num1 = num1;
    }
    
    public Integer getNum2() {
        return this.num2;
    }
    
    public void setNum2(final Integer num2) {
        this.num2 = num2;
    }
}
