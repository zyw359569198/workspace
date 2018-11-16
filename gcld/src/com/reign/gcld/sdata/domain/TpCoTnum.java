package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;

public class TpCoTnum implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private Integer cityOcc;
    private Integer tNum;
    
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
    
    public Integer getTNum() {
        return this.tNum;
    }
    
    public void setTNum(final Integer tNum) {
        this.tNum = tNum;
    }
}
