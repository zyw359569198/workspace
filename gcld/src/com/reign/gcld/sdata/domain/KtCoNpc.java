package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;

public class KtCoNpc implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private Integer day;
    private Integer defNum;
    private Integer attInt;
    private Integer attNum;
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public Integer getDay() {
        return this.day;
    }
    
    public void setDay(final Integer day) {
        this.day = day;
    }
    
    public Integer getDefNum() {
        return this.defNum;
    }
    
    public void setDefNum(final Integer defNum) {
        this.defNum = defNum;
    }
    
    public Integer getAttInt() {
        return this.attInt;
    }
    
    public void setAttInt(final Integer attInt) {
        this.attInt = attInt;
    }
    
    public Integer getAttNum() {
        return this.attNum;
    }
    
    public void setAttNum(final Integer attNum) {
        this.attNum = attNum;
    }
}
