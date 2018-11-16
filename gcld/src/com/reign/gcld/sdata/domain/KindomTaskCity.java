package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;

public class KindomTaskCity implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer cityId;
    private Integer weiDegree;
    private Integer shuDegree;
    private Integer wuDegree;
    
    public Integer getCityId() {
        return this.cityId;
    }
    
    public void setCityId(final Integer cityId) {
        this.cityId = cityId;
    }
    
    public Integer getWeiDegree() {
        return this.weiDegree;
    }
    
    public void setWeiDegree(final Integer weiDegree) {
        this.weiDegree = weiDegree;
    }
    
    public Integer getShuDegree() {
        return this.shuDegree;
    }
    
    public void setShuDegree(final Integer shuDegree) {
        this.shuDegree = shuDegree;
    }
    
    public Integer getWuDegree() {
        return this.wuDegree;
    }
    
    public void setWuDegree(final Integer wuDegree) {
        this.wuDegree = wuDegree;
    }
}
