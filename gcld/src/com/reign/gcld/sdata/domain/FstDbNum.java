package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;

public class FstDbNum implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private Integer cityOcc;
    private Integer dNum;
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public Integer getCityOcc() {
        return this.cityOcc;
    }
    
    public void setCityOcc(final Integer cityOcc) {
        this.cityOcc = cityOcc;
    }
    
    public Integer getDNum() {
        return this.dNum;
    }
    
    public void setDNum(final Integer dNum) {
        this.dNum = dNum;
    }
}
